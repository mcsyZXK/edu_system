package com.zxk.xxxt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxk.xxxt.POJO.Admin;

public interface AdminMapper extends BaseMapper<Admin> {
    int deleteByPrimaryKey(Long id);

    int insert(Admin record);

    int insertSelective(Admin record);

    Admin selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Admin record);

    int updateByPrimaryKey(Admin record);
}