package com.zxk.xxxt.DTO;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 更新帖子参数
 */
@Data
public class PostUpdateDTO {
    /**
     * 帖子ID
     */
    @NotNull(message = "帖子ID不能为空")
    private Integer id;

    /**
     * 标题（可选）
     */
    private String title;

    /**
     * 内容（可选）
     */
    private String content;

    /**
     * 分类（可选）：0-学习资料，1-生活资讯，2-日常分享
     */
    private Integer category;

    /**
     * 封面图URL（可选）
     */
    private String coverImage;
}

