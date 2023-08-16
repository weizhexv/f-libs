package com.jkqj.os.issue.handler;

import com.google.common.base.CaseFormat;
import com.jkqj.common.utils.JsonUtils;
import com.jkqj.mod.issue.msg.IssueMessage;
import com.jkqj.mod.issue.sub.core.AbstractMessageHandler;
import com.jkqj.os.issue.handler.extract.IssueExtractor;
import com.jkqj.os.issue.handler.sink.IssueSink;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 增量消息数据分发
 *
 * @author liuyang
 */
@Slf4j
public class DefaultEsPipeline extends AbstractMessageHandler implements Pipeline {

    private final IssueExtractor issueExtractor;

    private final IssueSink issueSink;

    public DefaultEsPipeline(IssueExtractor issueExtractor, IssueSink issueSink) {
        this.issueExtractor = issueExtractor;
        this.issueSink = issueSink;
    }

    @Override
    public boolean execute(String topic, String tableName, Long idValue) {
        Map<String, Object> res = issueExtractor.extractIssueData(topic, tableName, idValue);
        if (res == null || res.isEmpty()) {
            log.info("默认抓取器数据为空，开始休眠：topic: {}, tableName：{}, idValue: {}", topic, tableName, idValue);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.info("线程休眠异常：topic: {}, tableName：{}, idValue: {}", topic, tableName, idValue);
                e.printStackTrace();
            }
            res = issueExtractor.extractIssueData(topic, tableName, idValue);
            if (res == null || res.isEmpty()) {
                log.info("默认抓取器数据再次为空！！！ ：topic: {}, tableName：{}, idValue: {}", topic, tableName, idValue);
                return false;
            }
        }
        log.info("ES 入库数据: topic: {}, tableName：{}, idValue: {} res: {}", topic, tableName, idValue, JsonUtils.toJson(res));
        return issueSink.sink(topic, tableName, idValue, res);
    }

    @Override
    public String uniqueName() {
        return "DefaultEsPipeline";
    }

    @Override
    public boolean interested(String eventSource) {
        if("Cv".equals(eventSource)){
            return false;
        }
        return true;
    }

    @Override
    protected void processMsg(String topic, IssueMessage message) {
        String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, message.getSource());
        execute(topic, tableName, Long.parseLong(message.getValue().toString()));
        log.info("DefaultEsPipeline 处理消息");
    }
}
