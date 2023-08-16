package com.jkqj.mod.issue.sub;

import com.jkqj.mod.issue.sub.core.MessageHandlerDispatcher;
import com.jkqj.mod.issue.sub.executor.IssueSubWaitException;
import com.jkqj.mod.issue.sub.model.TargetBody;
import com.jkqj.mod.issue.sub.trans.CommonTransExecutor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public abstract class AbstractReceiver implements Receiver {

    @Resource
    private CommonTransExecutor commonTransExecutor;

    /**
     * 简历处理时间
     */
    static ThreadLocal<LocalDateTime> threadLocal = new ThreadLocal<>();

    private final MessageHandlerDispatcher messageHandlerDispatcher;
    private volatile boolean shut = false;
    private final CountDownLatch waitShut = new CountDownLatch(1);

    public AbstractReceiver(MessageHandlerDispatcher messageHandlerDispatcher) {
        this.messageHandlerDispatcher = messageHandlerDispatcher;
    }

    @PostConstruct
    @Override
    public void startReceiver() {
        CountDownLatch start = new CountDownLatch(1);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                start.countDown();
                while (!shut) {
                    try {
                        processBatch();
                    } catch (IssueSubWaitException e) {
                        log.info("", e);
                        safeSleep(2000L);
                    } catch (RuntimeException e) {
                        log.info("processBatch error", e);
                        safeSleep(2000L);
                    }
                }
                waitShut.countDown();
            }
        });
        thread.setName("AbstractReceiver-" + thread.getId());
        thread.start();
        try {
            start.await();
        } catch (InterruptedException e) {
            // ignore
            log.info("", e);
        }
    }


    /**
     * 线程休眠
     */
    private static void safeSleep(long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
        }
    }

    @PreDestroy
    @Override
    public void shutdown() {
        shut = true;
        try {
            waitShut.await();
        } catch (InterruptedException e) {
            // ignore
            log.info("", e);
        }
    }

    /**
     * 批量处理
     */
    private void processBatch() {
        // 1. 直接接受MQ并处理
        List<TargetBody> bodyList = receiveFromMq(messageHandlerDispatcher);
        messageHandlerDispatcher.targetDispatch(bodyList);
        // 2. 记录当前时间
        if(threadLocal.get() == null){
            threadLocal.set(LocalDateTime.now());
            return;
        }
        // 3. 若已处理5分钟以上，则拉取数据库数据处理
        if(threadLocal.get().plusMinutes(5L).isBefore(LocalDateTime.now())){
            // 数据库存量消息处理
            commonTransExecutor.doTrans(() -> {
                messageHandlerDispatcher.targetDispatch(receiveFromDb());
            });
            threadLocal.set(LocalDateTime.now());
        }
    }

    protected abstract List<TargetBody> receiveFromMq(MessageHandlerDispatcher dispatcher);

    protected abstract List<TargetBody> receiveFromDb();


}
