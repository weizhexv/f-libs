package com.jkqj.common.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * B端用户
 *
 * @author cb
 * @date 2022-03-13
 */
@Data
public class BUser implements Serializable {

    private Long id;
    private String mobile;
    private String username;
    private String name;
    private String nickname;
    private Boolean isMale;
    private String email;
    private String avatar;
    private String title;

}
