package com.zxk.xxxt.POJO;

import lombok.Data;

/**
 * 评论表
 */
@Data
public class Comment {
    /**
    * 主键ID
    */
    private Integer id;

    /**
    * 关联信息ID
    */
    private Integer postId;

    /**
    * 评论者ID
    */
    private Integer userId;

    /**
    * 评论内容
    */
    private String content;

    /**
    * 父评论ID（0表示顶级评论）
    */
    private Integer parentId;

    /**
    * 评论时间
    */
    private Integer createTime;

    /**
    * 删除时间（0表示未删除）
    */
    private Integer deleteTime;

}