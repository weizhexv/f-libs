package com.jkqj.interview.client;

import com.jkqj.common.result.Result;
import com.jkqj.interview.client.request.DirectInterviewRpcRequest;
import com.jkqj.interview.client.vo.DirectInterviewRpcVo;

/**
 * 直接面试Rpc服务
 *
 * @author liuyang
 */
public interface DirectInterviewRpcDubboService {

    /**
     * 生成直面
     */
    Result<DirectInterviewRpcVo> generateDirectInterview(DirectInterviewRpcRequest request);

    /**
     * 是否可以生成直面
     */
    Result<Boolean> couldGenerateDirectInterview(DirectInterviewRpcRequest request);

}
