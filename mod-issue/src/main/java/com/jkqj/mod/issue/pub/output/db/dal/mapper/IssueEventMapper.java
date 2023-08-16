package com.jkqj.mod.issue.pub.output.db.dal.mapper;

import com.jkqj.mod.issue.pub.output.MsgDbOutput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.jkqj.mod.issue.pub.output.db.dal.po.IssueEvent;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface IssueEventMapper extends MsgDbOutput {
    @Override
    int output(@Param("message") String message);

    List<IssueEvent> scan(@Param("limit") int limit, @Param("timeoutAt")LocalDateTime timeoutAt);

    int changeStatus(@Param("idList") List<Long> idList, @Param("status") int status);
}