package com.zxk.xxxt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxk.xxxt.POJO.Student;
import org.apache.ibatis.annotations.Param;

public interface StudentMapper extends BaseMapper<Student> {
    int deleteByPrimaryKey(Integer id);

    int insert(Student record);

    int insertSelective(Student record);

    Student selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Student record);

    int updateByPrimaryKey(Student record);

    /**
     * 根据用户名查询学生
     */
    Student selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询学生
     */
    Student selectByEmail(@Param("email") String email);

    /**
     * 根据学号查询学生
     */
    Student selectByStudentId(@Param("studentId") String studentId);
}