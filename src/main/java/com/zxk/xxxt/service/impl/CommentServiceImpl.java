package com.zxk.xxxt.service.impl;

import com.zxk.xxxt.DTO.CommentCreateDTO;
import com.zxk.xxxt.DTO.CommentUpdateDTO;
import com.zxk.xxxt.POJO.Comment;
import com.zxk.xxxt.POJO.Student;
import com.zxk.xxxt.VO.CommentVO;
import com.zxk.xxxt.mapper.CommentMapper;
import com.zxk.xxxt.mapper.StudentMapper;
import com.zxk.xxxt.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createComment(CommentCreateDTO dto, Integer userId) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(dto, comment);
        comment.setUserId(userId);
        
        // 设置创建时间（Unix时间戳，单位：秒）
        int currentTime = (int) (System.currentTimeMillis() / 1000);
        comment.setCreateTime(currentTime);
        comment.setDeleteTime(0); // 默认未删除
        
        // 如果未指定父评论ID，默认为0（顶级评论）
        if (comment.getParentId() == null) {
            comment.setParentId(0);
        }

        int result = commentMapper.insertSelective(comment);
        return result > 0;
    }

    @Override
    public CommentVO getById(Integer id) {
        Comment comment = commentMapper.selectByPrimaryKey(id);
        if (comment == null || (comment.getDeleteTime() != null && comment.getDeleteTime() != 0)) {
            return null;
        }
        return convertToVO(comment);
    }

    @Override
    public List<CommentVO> getByPostId(Integer postId) {
        List<Comment> comments = commentMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Comment>()
                .eq("post_id", postId)
                .eq("delete_time", 0)
                .orderByAsc("create_time")
        );
        return convertToVOList(comments);
    }

    @Override
    public List<CommentVO> getByParentId(Integer parentId) {
        List<Comment> comments = commentMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Comment>()
                .eq("parent_id", parentId)
                .eq("delete_time", 0)
                .orderByAsc("create_time")
        );
        return convertToVOList(comments);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateComment(CommentUpdateDTO dto) {
        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setContent(dto.getContent());
        
        int result = commentMapper.updateByPrimaryKeySelective(comment);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Integer id) {
        // 逻辑删除，设置删除时间
        Comment comment = new Comment();
        comment.setId(id);
        int deleteTime = (int) (System.currentTimeMillis() / 1000);
        comment.setDeleteTime(deleteTime);
        
        int result = commentMapper.updateByPrimaryKeySelective(comment);
        return result > 0;
    }

    /**
     * 将 Comment POJO 转换为 CommentVO
     */
    private CommentVO convertToVO(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);
        
        // 填充用户信息
        if (comment.getUserId() != null) {
            Student student = studentMapper.selectByPrimaryKey(comment.getUserId());
            if (student != null) {
                vo.setUsername(student.getUsername());
                vo.setNickname(student.getNickname());
                vo.setAvatar(student.getAvatar());
            }
        }
        
        // 填充父评论信息
        if (comment.getParentId() != null && comment.getParentId() != 0) {
            Comment parentComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (parentComment != null) {
                vo.setParentContent(parentComment.getContent());
                if (parentComment.getUserId() != null) {
                    Student parentStudent = studentMapper.selectByPrimaryKey(parentComment.getUserId());
                    if (parentStudent != null) {
                        vo.setParentUsername(parentStudent.getUsername());
                    }
                }
            }
        }
        
        // TODO: 填充子评论列表（可选）
        vo.setChildren(new ArrayList<>());
        
        return vo;
    }

    /**
     * 将 Comment 列表转换为 CommentVO 列表
     */
    private List<CommentVO> convertToVOList(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return new ArrayList<>();
        }
        return comments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
}

