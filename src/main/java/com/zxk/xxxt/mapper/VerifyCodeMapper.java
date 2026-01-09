package com.zxk.xxxt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxk.xxxt.POJO.VerifyCode;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

public interface VerifyCodeMapper extends BaseMapper<VerifyCode> {
    int deleteByPrimaryKey(Integer id);

    int insert(VerifyCode record);

    int insertSelective(VerifyCode record);

    VerifyCode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(VerifyCode record);

    int updateByPrimaryKey(VerifyCode record);
    /**
     * 根据邮箱查找最新的验证码
     */
    VerifyCode selectLatestByEmail(@Param("email") String email);

    /**
     * 统计指定时间内某邮箱的验证码发送次数
     */
    int countByEmailAfterTime(@Param("email") String email,
                              @Param("afterTime") LocalDateTime afterTime);
}