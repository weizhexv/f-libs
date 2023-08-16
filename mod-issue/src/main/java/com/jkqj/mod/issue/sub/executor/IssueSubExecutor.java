package com.jkqj.mod.issue.sub.executor;

import java.util.concurrent.*;

/**
 * 消息接受执行器
 *
 * @author liuyang
 */
public class IssueSubExecutor {

    /**
     * 线程池
     */
    private static final ExecutorService DISPATCHER = buildFixedExecutorService(16, 100, "issueSubDispatcher");

    /**
     * 执行任务
     */
    public static void executeTask(Runnable task) {
        DISPATCHER.execute(task);
    }

    /**
     * 构建有界线程池
     */
    static ExecutorService buildFixedExecutorService(int threadCount, int queueLimit, String name) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCount, threadCount,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueLimit),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName(name);
                    return thread;
                });
        return executor;
    }
}
