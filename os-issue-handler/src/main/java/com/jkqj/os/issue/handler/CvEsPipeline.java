package com.jkqj.os.issue.handler;

import com.jkqj.os.issue.handler.extract.IssueExtractor;
import com.jkqj.os.issue.handler.sink.IssueSink;
import lombok.extern.slf4j.Slf4j;

/**
 * 简历ES流水线
 *
 * @author liuyang
 */
@Slf4j
public class CvEsPipeline extends DefaultEsPipeline {

    public CvEsPipeline(IssueExtractor issueExtractor, IssueSink issueSink) {
        super(issueExtractor, issueSink);
    }

    @Override
    public String uniqueName() {
        return "CvEsPipeline";
    }

    @Override
    public boolean interested(String eventSource) {
        if("Cv".equals(eventSource)){
            return true;
        }
        return false;
    }

}
