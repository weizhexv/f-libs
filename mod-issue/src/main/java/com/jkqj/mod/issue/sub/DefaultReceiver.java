package com.jkqj.mod.issue.sub;

import com.jkqj.common.utils.JsonUtils;
import com.jkqj.mod.issue.sub.core.MessageHandlerDispatcher;
import com.jkqj.mod.issue.sub.dal.mapper.IssueSubMapper;
import com.jkqj.mod.issue.sub.dal.po.IssueSub;
import com.jkqj.mod.issue.sub.model.TargetBody;
import com.jkqj.nats.Message;
import com.jkqj.nats.Subject;
import com.jkqj.nats.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class DefaultReceiver extends AbstractReceiver implements Receiver {

    @Resource
    private IssueSubMapper issueSubMapper;

    private Subscriber issueSubscriber;

    private Subject subject;

    public DefaultReceiver(MessageHandlerDispatcher messageHandlerDispatcher, Subscriber subscriber, Subject subject) {
        super(messageHandlerDispatcher);
        this.issueSubscriber = subscriber;
        this.subject = subject;
    }

    /**
     * 拉取消息
     *
     * @author liuyang
     */
    @Override
    protected List<TargetBody> receiveFromMq(MessageHandlerDispatcher dispatcher) {
        // 1. 拉取5条消息
        List<Message> messages = issueSubscriber.fetch(5, Duration.ofSeconds(2));
        log.info("mod issue 主动拉取消息列表：{}", JsonUtils.toJson(messages));
        if (CollectionUtils.isEmpty(messages)) {
            return Collections.emptyList();
        }
        // 2. 持久化消息
        List<TargetBody> res = new ArrayList<>();
        for (Message message : messages) {
            String body = message.asString();
            // 多条相同数据库消息压缩（相同表相同id）
            res.addAll(quickLocalPersist(subject.getCategory(), body, dispatcher.collectHandlerName(body)));
            log.info("收到消息 DefaultReceiver.receiveFromMq: {}", message.asString());
            message.ack();
        }
        return res;
    }

    /**
     * 消息入库
     *
     * @author liuyang
     */
    private List<TargetBody> quickLocalPersist(String topic, String body, List<String> handlerUniqNameList) {
        List<TargetBody> resList = new ArrayList<>();
        for (String name : handlerUniqNameList) {
            IssueSub issueSub = new IssueSub();
            issueSub.setBody(body);
            issueSub.setTopic(topic);
            issueSub.setTarget(name);
            issueSub.setCreatedAt(LocalDateTime.now());
            issueSub.setModifiedAt(LocalDateTime.now());
            issueSub.setStatus(0);
            issueSubMapper.insert(issueSub);
            TargetBody targetBody = new TargetBody(issueSub.getId(), topic, issueSub.getTarget(), issueSub.getBody());
            resList.add(targetBody);
        }
        return resList;
    }

    /**
     * 扫描数据库
     *
     * @author liuyang
     */
    @Override
    protected List<TargetBody> receiveFromDb() {
        // 增加处理时间  超时 处理 更新成 不超时
        List<IssueSub> list = issueSubMapper.selectUnHandleIssueForUpdate();
        List<TargetBody> res = new ArrayList<>();
        for (IssueSub issueSub : list) {
            TargetBody targetBody = new TargetBody();
            targetBody.setBody(issueSub.getBody());
            targetBody.setTopic(issueSub.getTopic());
            targetBody.setTarget(issueSub.getTarget());
            targetBody.setId(issueSub.getId());
            res.add(targetBody);
        }
        return res;
    }
}
