package com.zxk.xxxt.POJO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * 学生用户表
 */
@Data
public class Student {
    /**
    * 主键ID
    */
    private Integer id;

    /**
    * 登录账号，唯一
    */
    private String username;

    /**
    * 加密密码
    */
    private String password;

    /**
    * 邮箱
    */
    private String email;

    /**
    * 手机号
    */
    private String phone;

    /**
    * 头像URL
    */
    private String avatar;

    /**
    * 昵称
    */
    private String nickname;

    /**
    * 学号
    */
    private String studentId;

    /**
    * 学院
    */
    private String college;

    /**
    * 专业
    */
    private String major;

    /**
    * 年级
    */
    private String grade;

    /**
     * 状态：0-禁用，1-正常
     */
    @TableField("status")
    private Byte status;  // 数据库字段仍然是Byte

    /**
     * 信誉分：范围0-200，默认100
     */
    private Integer creditScore;

    /**
     * 创建时间（Unix时间戳，单位：秒）
     */
    private Long createTime;

    /**
    * 更新时间（Unix时间戳，单位：秒）
    */
    private Long updateTime;

    /**
    * 删除时间（Unix时间戳，单位：秒）
    * 0 表示未删除，有数值表示已删除
    */
    private Long deleteTime;

}