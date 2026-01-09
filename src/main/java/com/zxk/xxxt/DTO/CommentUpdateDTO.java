package com.zxk.xxxt.DTO;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 更新评论参数
 */
@Data
public class CommentUpdateDTO {
    /**
     * 评论ID
     */
    @NotNull(message = "评论ID不能为空")
    private Integer id;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;
}

