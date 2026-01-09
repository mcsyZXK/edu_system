package com.zxk.xxxt.DTO;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 更新帖子置顶状态参数
 */
@Data
public class PostTopUpdateDTO {
    /**
     * 帖子ID
     */
    @NotNull(message = "帖子ID不能为空")
    private Integer id;

    /**
     * 是否置顶：true-置顶，false-取消置顶
     */
    @NotNull(message = "置顶状态不能为空")
    private Boolean isTop;
}

