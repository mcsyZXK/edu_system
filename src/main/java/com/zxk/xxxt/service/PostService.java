package com.zxk.xxxt.service;

import com.zxk.xxxt.DTO.*;
import com.zxk.xxxt.VO.PostVO;
import java.util.List;

public interface PostService {
    /**
     * 创建帖子
     * @param dto 创建帖子参数
     * @param userId 用户ID
     * @return 创建结果
     */
    boolean createPost(PostCreateDTO dto, Integer userId);

    /**
     * 根据ID查询帖子
     * @param id 帖子ID
     * @return 帖子信息
     */
    PostVO getById(Integer id);

    /**
     * 根据用户ID查询帖子列表
     * @param userId 用户ID
     * @return 帖子列表
     */
    List<PostVO> getByUserId(Integer userId);

    /**
     * 根据分类查询帖子列表
     * @param category 分类：0-学习资料，1-生活资讯，2-日常分享
     * @return 帖子列表
     */
    List<PostVO> getByCategory(Integer category);

    /**
     * 查询所有已发布的帖子
     * @return 帖子列表
     */
    List<PostVO> getAllPublished();

    /**
     * 更新帖子
     * @param dto 更新帖子参数
     * @return 更新结果
     */
    boolean updatePost(PostUpdateDTO dto);

    /**
     * 删除帖子（逻辑删除）
     * @param id 帖子ID
     * @return 删除结果
     */
    boolean deletePost(Integer id);

    /**
     * 更新帖子状态
     * @param dto 更新状态参数
     * @return 更新结果
     */
    boolean updateStatus(PostStatusUpdateDTO dto);

    /**
     * 置顶/取消置顶
     * @param dto 置顶参数
     * @return 更新结果
     */
    boolean updateTopStatus(PostTopUpdateDTO dto);

    /**
     * 点赞/取消点赞
     * @param dto 点赞参数
     * @return 更新结果
     */
    boolean updateLikeCount(PostLikeDTO dto);
}

