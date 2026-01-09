package com.zxk.xxxt.VO;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 评论视图对象
 */
@Data
public class CommentVO implements Serializable {
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
     * 评论者用户名（可选，需要关联查询）
     */
    private String username;

    /**
     * 评论者昵称（可选，需要关联查询）
     */
    private String nickname;

    /**
     * 评论者头像（可选，需要关联查询）
     */
    private String avatar;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论ID（0表示顶级评论）
     */
    private Integer parentId;

    /**
     * 父评论内容（可选，需要关联查询）
     */
    private String parentContent;

    /**
     * 父评论用户名（可选，需要关联查询）
     */
    private String parentUsername;

    /**
     * 子评论列表（可选）
     */
    private List<CommentVO> children;

    /**
     * 评论时间（Unix时间戳，单位：秒）
     */
    private Integer createTime;
}

