package com.zxk.xxxt.service;

import com.zxk.xxxt.DTO.RegisterDTO;
import com.zxk.xxxt.POJO.Student;
import java.util.Map;

public interface StudentService {
    /**
     * 学生注册
     * @param registerDTO 注册信息
     * @return 注册结果，包含token信息
     */
    String register(RegisterDTO registerDTO);

    /**
     * 学生登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果，包含token信息
     */
    Map<String, Object> login(String username, String password);

    /**
     * 根据ID查询学生
     * @param id 学生ID
     * @return 学生信息
     */
    Student getById(Integer id);

    /**
     * 根据用户名查询学生
     * @param username 用户名
     * @return 学生信息
     */
    Student getByUsername(String username);

    /**
     * 根据邮箱查询学生
     * @param email 邮箱
     * @return 学生信息
     */
    Student getByEmail(String email);

    /**
     * 根据学号查询学生
     * @param studentId 学号
     * @return 学生信息
     */
    Student getByStudentId(String studentId);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return true-存在，false-不存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return true-存在，false-不存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查学号是否存在
     * @param studentId 学号
     * @return true-存在，false-不存在
     */
    boolean existsByStudentId(String studentId);

    /**
     * 更新学生信息
     * @param student 学生信息
     * @return 更新结果
     */
    boolean updateStudent(Student student);

    /**
     * 修改密码
     * @param id 学生ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    boolean changePassword(Integer id, String oldPassword, String newPassword);

    /**
     * 重置密码（忘记密码时使用）
     * @param email 邮箱
     * @param newPassword 新密码
     * @return 重置结果
     */
    boolean resetPassword(String email, String newPassword);

    /**
     * 删除学生（逻辑删除）
     * @param id 学生ID
     * @return 删除结果
     */
    boolean deleteStudent(Integer id);

    /**
     * 启用/禁用学生
     * @param id 学生ID
     * @param status 状态：0-禁用，1-启用
     * @return 更新结果
     */
    boolean updateStatus(Integer id, Byte status);
}
