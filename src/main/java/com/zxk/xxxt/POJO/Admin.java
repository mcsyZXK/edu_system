package com.zxk.xxxt.POJO;

import lombok.Data;

import java.util.Date;

/**
 * 平台管理员表
 */
@Data
public class Admin {
    /**
    * 主键ID
    */
    private Long id;

    /**
    * 管理员账号，唯一
    */
    private String username;

    /**
    * 加密密码
    */
    private String password;

    /**
    * 最后登录时间
    */
    private Date lastLoginTime;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 更新时间
    */
    private Date updateTime;


}