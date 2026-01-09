package com.zxk.xxxt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxk.xxxt.POJO.Comment;

public interface CommentMapper extends BaseMapper<Comment> {
    int deleteByPrimaryKey(Integer id);

    int insert(Comment record);

    int insertSelective(Comment record);

    Comment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Comment record);

    int updateByPrimaryKey(Comment record);
}