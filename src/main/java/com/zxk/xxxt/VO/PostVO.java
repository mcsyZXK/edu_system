package com.zxk.xxxt.VO;

import lombok.Data;
import java.io.Serializable;

/**
 * 帖子视图对象
 */
@Data
public class PostVO implements Serializable {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 发布者ID
     */
    private Integer userId;

    /**
     * 发布者用户名（可选，需要关联查询）
     */
    private String username;

    /**
     * 发布者昵称（可选，需要关联查询）
     */
    private String nickname;

    /**
     * 发布者头像（可选，需要关联查询）
     */
    private String avatar;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 分类：0-学习资料，1-生活资讯，2-日常分享
     */
    private Integer category;

    /**
     * 分类名称（可选）
     */
    private String categoryName;

    /**
     * 状态：0-待审核，1-已发布，2-已驳回
     */
    private Integer status;

    /**
     * 状态名称（可选）
     */
    private String statusName;

    /**
     * 是否置顶：0-否，1-是
     */
    private Boolean isTop;

    /**
     * 封面图URL
     */
    private String coverImage;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数（可选，需要统计）
     */
    private Integer commentCount;

    /**
     * 创建时间（Unix时间戳，单位：秒）
     */
    private Integer createTime;

    /**
     * 更新时间（Unix时间戳，单位：秒）
     */
    private Integer updateTime;
}

