package com.jkqj.interview.client;

import com.jkqj.common.result.Result;
import com.jkqj.interview.client.vo.InterviewProcRpcVO;

import java.util.List;

/**
 * 面试Proc Rpc服务
 *
 * @author liuyang
 */
public interface InterviewProcRpcDubboService {

    Result<List<InterviewProcRpcVO>> queryInterviewProcList(List<Long> interviewProcIdList);

}
