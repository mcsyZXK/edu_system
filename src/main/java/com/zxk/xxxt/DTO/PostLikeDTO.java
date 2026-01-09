package com.zxk.xxxt.DTO;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 点赞帖子参数
 */
@Data
public class PostLikeDTO {
    /**
     * 帖子ID
     */
    @NotNull(message = "帖子ID不能为空")
    private Integer id;

    /**
     * 增量：1-点赞，-1-取消点赞（默认为1）
     */
    private Integer increment = 1;
}

