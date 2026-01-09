package com.zxk.xxxt.DTO;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 更新帖子状态参数
 */
@Data
public class PostStatusUpdateDTO {
    /**
     * 帖子ID
     */
    @NotNull(message = "帖子ID不能为空")
    private Integer id;

    /**
     * 状态：0-待审核，1-已发布，2-已驳回
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}

