package com.jkqj.job.client;

import com.jkqj.common.result.Result;
import com.jkqj.job.client.dto.JobDetailDTO;

import java.util.Collections;
import java.util.List;

/**
 * 职位dubbo服务接口
 *
 * @author cb
 * @date 2022-04-14
 */
public interface JobDubboService {

    Result<Boolean> hasPublishedJobs(Long userId);

    default Result<Integer> countPublishedJobs(Long userId) {
        return Result.success(0);
    }

    default Result<Integer> countMyJobs(Long bid) {
        return Result.success(0);
    }

    default Result<JobDetailDTO> jobDetail(Long jobId) {
        return Result.success(new JobDetailDTO());
    }

    default Result<List<JobDetailDTO>> getJobDetailList(List<Long> jobIds) {
        return Result.success(Collections.emptyList());
    }

    default Result<List<JobDetailDTO>> getJobDetailListByPublisher(Long publisherId) {
        return Result.success(Collections.emptyList());
    }

}
