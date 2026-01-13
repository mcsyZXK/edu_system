package com.zxk.xxxt.service.impl;

import com.zxk.xxxt.DTO.RegisterDTO;
import com.zxk.xxxt.POJO.Student;
import com.zxk.xxxt.Utils.TokenUtils;
import com.zxk.xxxt.mapper.StudentMapper;
import com.zxk.xxxt.service.StudentService;
import com.zxk.xxxt.service.VerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private VerifyCodeService verifyCodeService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String register(RegisterDTO registerDTO) {
        // 1. 检查用户名是否已存在
        if (registerDTO.getUsername() == null || registerDTO.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (existsByUsername(registerDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 检查邮箱是否已存在
        if (registerDTO.getEmail() == null || registerDTO.getEmail().trim().isEmpty()) {
            throw new RuntimeException("邮箱不能为空");
        }
        if (existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 3. 检查学号是否已存在
        if (registerDTO.getStudentId() == null || registerDTO.getStudentId().trim().isEmpty()) {
            throw new RuntimeException("学号不能为空");
        }
        if (existsByStudentId(registerDTO.getStudentId())) {
            throw new RuntimeException("学号已被注册");
        }

        // 4. 验证验证码
        String verifyCode = registerDTO.getCode();
        if (verifyCode == null || verifyCode.trim().isEmpty()) {
            throw new RuntimeException("验证码不能为空");
        }
        boolean verified = verifyCodeService.verifyCode(registerDTO.getEmail(), verifyCode);
        if (!verified) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 5. 创建学生对象并设置信息
        Student student = new Student();
        student.setUsername(registerDTO.getUsername());
        student.setStudentId(registerDTO.getStudentId());
        student.setEmail(registerDTO.getEmail());
        
        // 6. 加密密码
        if (registerDTO.getPassword() == null || registerDTO.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        student.setPassword(passwordEncoder.encode(registerDTO.getPassword()));

        // 7. 设置默认状态为正常
        student.setStatus((byte) 1);

        // 8. 设置默认信誉分为100
        student.setCreditScore(100);

        // 9. 设置创建时间和更新时间（Unix时间戳，单位：秒）
        long currentTime = System.currentTimeMillis() / 1000;  // 转换为秒
        student.setCreateTime(currentTime);
        student.setUpdateTime(currentTime);
        student.setDeleteTime(0L);  // 默认未删除

        // 10. 插入数据库
        int result = studentMapper.insertSelective(student);
        if (result <= 0) {
            throw new RuntimeException("注册失败");
        }

        return "注册成功";
    }

    @Override
    public Map<String, Object> login(String unOrEmail, String password) {
        // 查询学生
        Student student = studentMapper.selectByUsername(unOrEmail);
        if (student == null) {
            student = studentMapper.selectByEmail(unOrEmail);
            if(student == null){
                throw new RuntimeException("用户名或密码错误");
            }
        }
        // 验证密码
        if (!passwordEncoder.matches(password, student.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查账号状态
        if (student.getStatus() == null || student.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 更新最后登录时间（Unix时间戳，单位：秒）
        student.setUpdateTime(System.currentTimeMillis() / 1000);
        studentMapper.updateByPrimaryKeySelective(student);
        // 生成Token
        Map<String, String> tokenPair = TokenUtils.generateTokenPair(
                String.valueOf(student.getId()),
                student.getUsername()
        );

        // 返回结果（不返回密码）
        student.setPassword(null);
        Map<String, Object> response = new HashMap<>();
        response.put("student", student);
        response.put("accessToken", tokenPair.get("accessToken"));
        response.put("refreshToken", tokenPair.get("refreshToken"));
        return response;
    }

    @Override
    public Student getById(Integer id) {
        Student student = studentMapper.selectByPrimaryKey(id);
        if (student != null) {
            student.setPassword(null); // 不返回密码
        }
        return student;
    }

    @Override
    public Student getByUsername(String username) {
        Student student = studentMapper.selectByUsername(username);
        if (student != null) {
            student.setPassword(null); // 不返回密码
        }
        return student;
    }

    @Override
    public Student getByEmail(String email) {
        Student student = studentMapper.selectByEmail(email);
        if (student != null) {
            student.setPassword(null); // 不返回密码
        }
        return student;
    }

    @Override
    public Student getByStudentId(String studentId) {
        Student student = studentMapper.selectByStudentId(studentId);
        if (student != null) {
            student.setPassword(null); // 不返回密码
        }
        return student;
    }

    @Override
    public boolean existsByUsername(String username) {
        return studentMapper.selectByUsername(username) != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return studentMapper.selectByEmail(email) != null;
    }

    @Override
    public boolean existsByStudentId(String studentId) {
        return studentMapper.selectByStudentId(studentId) != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStudent(Student student) {
        // 如果更新了用户名，检查是否重复
        if (student.getUsername() != null) {
            Student existing = studentMapper.selectByUsername(student.getUsername());
            if (existing != null && !existing.getId().equals(student.getId())) {
                throw new RuntimeException("用户名已被使用");
            }
        }

        // 如果更新了邮箱，检查是否重复
        if (student.getEmail() != null) {
            Student existing = studentMapper.selectByEmail(student.getEmail());
            if (existing != null && !existing.getId().equals(student.getId())) {
                throw new RuntimeException("邮箱已被使用");
            }
        }

        // 如果更新了学号，检查是否重复
        if (student.getStudentId() != null) {
            Student existing = studentMapper.selectByStudentId(student.getStudentId());
            if (existing != null && !existing.getId().equals(student.getId())) {
                throw new RuntimeException("学号已被使用");
            }
        }

        // 如果更新了信誉分，验证范围（0-200）
        if (student.getCreditScore() != null) {
            Integer creditScore = student.getCreditScore();
            if (creditScore < 0 || creditScore > 200) {
                throw new RuntimeException("信誉分必须在0-200之间");
            }
        }

        // 更新更新时间（Unix时间戳，单位：秒）
        student.setUpdateTime(System.currentTimeMillis() / 1000);

        // 不允许通过此方法更新密码
        student.setPassword(null);

        int result = studentMapper.updateByPrimaryKeySelective(student);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Integer id, String oldPassword, String newPassword) {
        Student student = studentMapper.selectByPrimaryKey(id);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, student.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 加密新密码
        String encodedPassword = passwordEncoder.encode(newPassword);

        // 更新密码
        Student updateStudent = new Student();
        updateStudent.setId(id);
        updateStudent.setPassword(encodedPassword);
        updateStudent.setUpdateTime(System.currentTimeMillis() / 1000);  // Unix时间戳，单位：秒

        int result = studentMapper.updateByPrimaryKeySelective(updateStudent);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(String email, String newPassword) {
        Student student = studentMapper.selectByEmail(email);
        if (student == null) {
            throw new RuntimeException("该邮箱未注册");
        }

        // 加密新密码
        String encodedPassword = passwordEncoder.encode(newPassword);

        // 更新密码
        Student updateStudent = new Student();
        updateStudent.setId(student.getId());
        updateStudent.setPassword(encodedPassword);
        updateStudent.setUpdateTime(System.currentTimeMillis() / 1000);  // Unix时间戳，单位：秒

        int result = studentMapper.updateByPrimaryKeySelective(updateStudent);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStudent(Integer id) {
        // 使用逻辑删除，设置删除时间
        Student student = new Student();
        student.setId(id);
        long deleteTime = System.currentTimeMillis() / 1000;  // Unix时间戳，单位：秒
        student.setDeleteTime(deleteTime);
        student.setUpdateTime(deleteTime);  // 同时更新更新时间
        int result = studentMapper.updateByPrimaryKeySelective(student);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Integer id, Byte status) {
        Student student = new Student();
        student.setId(id);
        student.setStatus(status);
        student.setUpdateTime(System.currentTimeMillis() / 1000);  // Unix时间戳，单位：秒
        int result = studentMapper.updateByPrimaryKeySelective(student);
        return result > 0;
    }
}
