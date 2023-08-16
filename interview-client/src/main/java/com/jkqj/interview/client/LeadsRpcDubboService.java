package com.jkqj.interview.client;

import com.jkqj.common.result.Result;
import com.jkqj.interview.client.vo.CLeadsStatusRpcVo;

/**
 * 线索Rpc服务
 *
 * @author liuyang
 */
public interface LeadsRpcDubboService {

    /**
     * 投递至天狼
     */
    Result<CLeadsStatusRpcVo> signUpFromSirius(Long jobId, Long cid);


    /**
     * 天狼查询
     */
    Result<CLeadsStatusRpcVo> queryLeadsStatus(Long jobId, Long cid);

}
