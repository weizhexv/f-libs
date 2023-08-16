package com.jkqj.common.request;

import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * 媒体id请求参数
 *
 * @author cb
 * @date 2022-08-30
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MediaIdRequest {

    @NotNull(message = "媒体id不能为空")
    private Long mediaId;

}
