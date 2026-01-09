package com.zxk.xxxt.DTO;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建评论参数
 */
@Data
public class CommentCreateDTO {
    /**
     * 帖子ID
     */
    @NotNull(message = "帖子ID不能为空")
    private Integer postId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;

    /**
     * 父评论ID（0表示顶级评论，可选，默认为0）
     */
    private Integer parentId = 0;
}

