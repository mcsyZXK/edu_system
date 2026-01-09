package com.zxk.xxxt.DTO;

import lombok.Data;

/**
 * 查询评论参数
 */
@Data
public class CommentQueryDTO {
    /**
     * 评论ID
     */
    private Integer id;

    /**
     * 帖子ID
     */
    private Integer postId;

    /**
     * 父评论ID
     */
    private Integer parentId;
}

