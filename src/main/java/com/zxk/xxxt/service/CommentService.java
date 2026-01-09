package com.zxk.xxxt.service;

import com.zxk.xxxt.DTO.CommentCreateDTO;
import com.zxk.xxxt.DTO.CommentUpdateDTO;
import com.zxk.xxxt.VO.CommentVO;
import java.util.List;

public interface CommentService {
    /**
     * 创建评论
     * @param dto 创建评论参数
     * @param userId 用户ID
     * @return 创建结果
     */
    boolean createComment(CommentCreateDTO dto, Integer userId);

    /**
     * 根据ID查询评论
     * @param id 评论ID
     * @return 评论信息
     */
    CommentVO getById(Integer id);

    /**
     * 根据帖子ID查询评论列表
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<CommentVO> getByPostId(Integer postId);

    /**
     * 根据父评论ID查询子评论列表
     * @param parentId 父评论ID
     * @return 子评论列表
     */
    List<CommentVO> getByParentId(Integer parentId);

    /**
     * 更新评论
     * @param dto 更新评论参数
     * @return 更新结果
     */
    boolean updateComment(CommentUpdateDTO dto);

    /**
     * 删除评论（逻辑删除）
     * @param id 评论ID
     * @return 删除结果
     */
    boolean deleteComment(Integer id);
}

