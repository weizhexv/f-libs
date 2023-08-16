package com.jkqj.cv.client;

import com.jkqj.common.result.Result;

/**
 * @author wenboxun
 */
public interface UserCvDubboService {

    /**
     * 查询是否上传简历
     *
     * @param userId 用户ID
     * @return 是否上传简历
     */
    Result<Boolean> existUserCv(long userId);

    /**
     * 获取用户简历媒体id
     *
     * @param userId 用户id
     * @return 简历媒体id
     */
    Result<Long> getUserCvMediaId(long userId);

}
