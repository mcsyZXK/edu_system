package com.zxk.xxxt.DTO;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * ID参数（通用）
 */
@Data
public class IdDTO {
    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Integer id;
}

