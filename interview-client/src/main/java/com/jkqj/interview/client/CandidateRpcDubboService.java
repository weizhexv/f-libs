package com.jkqj.interview.client;

import com.jkqj.common.result.Result;
import com.jkqj.interview.client.vo.CandidateRpcVo;
import com.jkqj.interview.client.vo.InterviewCountRpcVo;

import java.util.List;

/**
 * 候选人Rpc服务
 *
 * @author liuyang
 */
public interface CandidateRpcDubboService {

    Result<CandidateRpcVo> queryCandidateCountByBid(Long bid);


    Result<List<InterviewCountRpcVo>> queryCountByJobIdsAndBid(Long bid, List<Long> jobIds);

}
