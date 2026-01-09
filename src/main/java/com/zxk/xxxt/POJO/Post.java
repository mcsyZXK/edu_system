package com.zxk.xxxt.POJO;

import lombok.Data;

/**
 * 信息发布表
 */
@Data
public class Post {
    /**
    * 主键ID
    */
    private Integer id;

    /**
    * 发布者ID
    */
    private Integer userId;

    /**
    * 标题
    */
    private String title;

    /**
    * 内容
    */
    private String content;

    /**
    * 分类：0-学习资料，1-生活资讯，2-日常分享
    */
    private Integer category;

    /**
    * 状态：0-待审核，1-已发布，2-已驳回
    */
    private Integer status;

    /**
    * 是否置顶：0-否，1-是
    */
    private Boolean isTop;

    /**
    * 封面图URL
    */
    private String coverImage;

    /**
    * 点赞数
    */
    private Integer likeCount;

    /**
    * 创建时间
    */
    private Integer createTime;

    /**
    * 更新时间
    */
    private Integer updateTime;

    /**
    * 删除时间（0表示未删除）
    */
    private Integer deleteTime;

}