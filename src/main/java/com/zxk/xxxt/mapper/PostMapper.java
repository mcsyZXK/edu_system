package com.zxk.xxxt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxk.xxxt.POJO.Post;

public interface PostMapper extends BaseMapper<Post> {
    int deleteByPrimaryKey(Integer id);

    int insert(Post record);

    int insertSelective(Post record);

    Post selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Post record);

    int updateByPrimaryKey(Post record);
}