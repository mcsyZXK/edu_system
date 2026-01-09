package com.zxk.xxxt.POJO;

import lombok.Data;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 验证码表
 */
@Data
public class VerifyCode {
    /**
    * 验证码ID
    */
    private Integer id;

    /**
    * 邮箱
    */
    private String email;

    /**
    * 验证码
    */
    private String code;

    /**
    * 创建时间
    */
    private Date createdAt;
    public boolean isExpired() {
        if (createdAt == null) return true;
        Instant expiredTime = createdAt.toInstant().plus(Duration.ofMinutes(5));
        Instant now = Instant.now();
        return now.isAfter(expiredTime);
    }
}