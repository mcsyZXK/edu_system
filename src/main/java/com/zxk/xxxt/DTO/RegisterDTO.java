package com.zxk.xxxt.DTO;

import lombok.Data;

/**
 * 学生注册参数
 */
@Data
public class RegisterDTO {
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 学号
     */
    private String studentId;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 验证码
     */
    private String code;
}
