package com.zxk.xxxt.DTO;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建帖子参数
 */
@Data
public class PostCreateDTO {
    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 分类：0-学习资料，1-生活资讯，2-日常分享
     */
    @NotNull(message = "分类不能为空")
    private Integer category;

    /**
     * 封面图URL（可选）
     */
    private String coverImage;
}

