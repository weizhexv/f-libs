package com.jkqj.interview.client;

import com.jkqj.common.result.Result;
import com.jkqj.interview.client.vo.InterviewRpcVo;

import java.util.List;

/**
 * 面试Rpc服务
 *
 * @author liuyang
 */
public interface InterviewRpcDubboService {

    Result<Boolean> hasInvitationCode(String mobile);

    Result<InterviewRpcVo> queryInterview(Long interviewId);

    Result<Integer> queryCountByCid(Long cid);

    default Result<Integer> queryUnfinishedCountByCid(Long cid) {
        return Result.success(0);
    }

    Result<Integer> queryJobCountByCid(Long cid);

    Result<List<Long>> queryJobIdListByCid(Long cid);

}
