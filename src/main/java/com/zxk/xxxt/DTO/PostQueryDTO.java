package com.zxk.xxxt.DTO;

import lombok.Data;

/**
 * 查询帖子参数
 */
@Data
public class PostQueryDTO {
    /**
     * 帖子ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 分类：0-学习资料，1-生活资讯，2-日常分享
     */
    private Integer category;

    /**
     * 状态：0-待审核，1-已发布，2-已驳回
     */
    private Integer status;
}

