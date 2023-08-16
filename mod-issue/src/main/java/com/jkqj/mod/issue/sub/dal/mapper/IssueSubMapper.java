package com.jkqj.mod.issue.sub.dal.mapper;

import com.jkqj.mod.issue.sub.dal.po.IssueSub;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IssueSubMapper {
    int insert(IssueSub issueSub);

    List<IssueSub> selectUnHandleIssueForUpdate();

    int changeStatus(@Param("id") Long id);
}