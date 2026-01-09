package com.zxk.xxxt.controller;

import com.zxk.xxxt.DTO.LoginDTO;
import com.zxk.xxxt.DTO.RegisterDTO;
import com.alibaba.fastjson.JSONObject;
import com.zxk.xxxt.POJO.Student;
import com.zxk.xxxt.Utils.JsonUtils;
import com.zxk.xxxt.Utils.TokenUtils;
import com.zxk.xxxt.anno.NotLogin;
import com.zxk.xxxt.service.StudentService;
import com.zxk.xxxt.service.impl.VerifyCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private VerifyCodeServiceImpl verifyCodeService;
    /**
     * 学生注册
     */
    @PostMapping("/register")
    @NotLogin
    public JSONObject register(@RequestBody RegisterDTO registerDTO) {
        try {
            String result = studentService.register(registerDTO);
            return JsonUtils.success(result);
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }
    /**
     * 发送验证码
     * @param email
     * @return
     */
    @PostMapping("/getcode")
    @NotLogin
    public JSONObject sendCode(@Email String email){
        boolean b = verifyCodeService.sendCode(email);
        if(b){
            return JsonUtils.success("发送成功");
        }
        return JsonUtils.fail("发送失败");
    }

    /**
     * 学生登录
     */
    @PostMapping("/login")
    @NotLogin
    public JSONObject login(@RequestBody LoginDTO loginDTO) {
        try {
            String username = loginDTO.getUnOrEmail();
            String password = loginDTO.getPassword();

            if (username == null || password == null) {
                return JsonUtils.fail("用户名和密码不能为空");
            }

            Map<String, Object> result = studentService.login(username, password);
            return JsonUtils.success(result);
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 获取当前登录学生信息
     */
    @GetMapping("/userinfo")
    public JSONObject getCurrentStudentInfo(@RequestHeader(name = "Authorization") String authHeader) {
        try {
            // 3. 获取Token（兼容大小写）
            if (authHeader == null || authHeader.trim().isEmpty()) {
                return JsonUtils.fail("未登录");
            }

            // 处理Bearer前缀（兼容大小写和空格）
            String token = null;
            String trimmedHeader = authHeader.trim();
            if (trimmedHeader.startsWith("Bearer ") || trimmedHeader.startsWith("bearer ")) {
                token = trimmedHeader.substring(7).trim();  // 去掉"Bearer "前缀并去除空格
            } else {
                // 如果没有Bearer前缀，直接使用整个值作为token（兼容直接传token的情况）
                token = trimmedHeader;
            }

            if (token == null || token.isEmpty()) {
                return JsonUtils.fail("未登录");
            }

            // 3. 验证 token
            if (TokenUtils.isTokenInvalid(token)) {
                return JsonUtils.fail("Token无效或已过期");
            }

            // 4. 解析 token 获取用户信息
            Map<String, String> userInfo = TokenUtils.parseToken(token);
            String userId = userInfo.get("userId");
            
            if (userId == null || userId.isEmpty()) {
                return JsonUtils.fail("Token中缺少用户ID信息");
            }

            // 5. 根据用户ID查询学生信息
            Student student = studentService.getById(Integer.parseInt(userId));
            if (student == null) {
                return JsonUtils.fail("学生不存在");
            }

            return JsonUtils.success(student);
        } catch (NumberFormatException e) {
            return JsonUtils.fail("用户ID格式错误");
        } catch (Exception e) {
            return JsonUtils.fail("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询学生
     */
    @PostMapping("/getbyid")
    public JSONObject getById(@RequestBody Map<String, Integer> request) {
        try {
            Integer id = request.get("id");
            if (id == null) {
                return JsonUtils.fail("ID不能为空");
            }
            Student student = studentService.getById(id);
            if (student == null) {
                return JsonUtils.fail("学生不存在");
            }
            return JsonUtils.success(student);
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 根据用户名查询学生
     */
    @PostMapping("/getbyun")
    public JSONObject getByUsername(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            if (username == null || username.isEmpty()) {
                return JsonUtils.fail("用户名不能为空");
            }
            Student student = studentService.getByUsername(username);
            if (student == null) {
                return JsonUtils.fail("学生不存在");
            }
            return JsonUtils.success(student);
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 检查用户名是否存在
     */
    @PostMapping("/check/username")
    @NotLogin
    public JSONObject checkUsername(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            if (username == null || username.isEmpty()) {
                return JsonUtils.fail("用户名不能为空");
            }
            boolean exists = studentService.existsByUsername(username);
            Map<String, Boolean> result = new HashMap<>();
            result.put("exists", exists);
            return JsonUtils.success(result);
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 检查邮箱是否存在
     */
    @PostMapping("/check/email")
    @NotLogin
    public JSONObject checkEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                return JsonUtils.fail("邮箱不能为空");
            }
            boolean exists = studentService.existsByEmail(email);
            Map<String, Boolean> result = new HashMap<>();
            result.put("exists", exists);
            return JsonUtils.success(result);
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 检查学号是否存在
     */
    @PostMapping("/check/studentId")
    @NotLogin
    public JSONObject checkStudentId(@RequestBody Map<String, String> request) {
        try {
            String studentId = request.get("studentId");
            if (studentId == null || studentId.isEmpty()) {
                return JsonUtils.fail("学号不能为空");
            }
            boolean exists = studentService.existsByStudentId(studentId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("exists", exists);
            return JsonUtils.success(result);
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 更新学生信息
     */
    @PutMapping("/update")
    @SuppressWarnings("unchecked")
    public JSONObject updateStudent(@RequestBody Student student, HttpServletRequest request) {
        try {
            // 获取当前登录用户ID
            Map<String, String> currentUser = (Map<String, String>) request.getAttribute("currentUser");
            if (currentUser == null) {
                return JsonUtils.fail("未登录");
            }

            String userId = currentUser.get("userId");
            // 只能更新自己的信息
            student.setId(Integer.parseInt(userId));

            boolean success = studentService.updateStudent(student);
            if (success) {
                return JsonUtils.success("更新成功");
            } else {
                return JsonUtils.fail("更新失败");
            }
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password/change")
    @SuppressWarnings("unchecked")
    public JSONObject changePassword(@RequestBody Map<String, String> passwordRequest, HttpServletRequest request) {
        try {
            String oldPassword = passwordRequest.get("oldPassword");
            String newPassword = passwordRequest.get("newPassword");

            if (oldPassword == null || newPassword == null) {
                return JsonUtils.fail("旧密码和新密码不能为空");
            }

            // 获取当前登录用户ID
            Map<String, String> currentUser = (Map<String, String>) request.getAttribute("currentUser");
            if (currentUser == null) {
                return JsonUtils.fail("未登录");
            }

            Integer userId = Integer.parseInt(currentUser.get("userId"));
            boolean success = studentService.changePassword(userId, oldPassword, newPassword);
            if (success) {
                return JsonUtils.success("密码修改成功");
            } else {
                return JsonUtils.fail("密码修改失败");
            }
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 重置密码（忘记密码）
     */
    @PutMapping("/password/reset")
    @NotLogin
    public JSONObject resetPassword(@RequestBody Map<String, String> resetRequest) {
        try {
            String email = resetRequest.get("email");
            String newPassword = resetRequest.get("newPassword");

            if (email == null || newPassword == null) {
                return JsonUtils.fail("邮箱和新密码不能为空");
            }

            boolean success = studentService.resetPassword(email, newPassword);
            if (success) {
                return JsonUtils.success("密码重置成功");
            } else {
                return JsonUtils.fail("密码重置失败");
            }
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }


    /**
     * 启用/禁用学生（需要管理员权限，这里先保留接口）
     */
    @PutMapping("/status")
    public JSONObject updateStatus(@RequestBody Map<String, Object> request) {
        try {
            Integer id = null;
            Byte status = null;
            
            // 处理 id 参数（可能是 Integer 或 String）
            Object idObj = request.get("id");
            if (idObj instanceof Integer) {
                id = (Integer) idObj;
            } else if (idObj instanceof String) {
                id = Integer.parseInt((String) idObj);
            } else if (idObj instanceof Number) {
                id = ((Number) idObj).intValue();
            }
            
            // 处理 status 参数（可能是 Byte、Integer 或 String）
            Object statusObj = request.get("status");
            if (statusObj instanceof Byte) {
                status = (Byte) statusObj;
            } else if (statusObj instanceof Integer) {
                status = ((Integer) statusObj).byteValue();
            } else if (statusObj instanceof String) {
                status = Byte.parseByte((String) statusObj);
            } else if (statusObj instanceof Number) {
                status = ((Number) statusObj).byteValue();
            }
            
            if (id == null || status == null) {
                return JsonUtils.fail("ID和状态不能为空");
            }
            
            boolean success = studentService.updateStatus(id, status);
            if (success) {
                return JsonUtils.success("状态更新成功");
            } else {
                return JsonUtils.fail("状态更新失败");
            }
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }

    /**
     * 删除学生（逻辑删除，通过将状态设置为0实现）
     */
    @PutMapping("/delete")
    public JSONObject deleteStudent(@RequestBody Map<String, Integer> request) {
        try {
            Integer id = request.get("id");
            if (id == null) {
                return JsonUtils.fail("ID不能为空");
            }
            
            // 将状态设置为0（禁用）实现逻辑删除
            boolean success = studentService.updateStatus(id, (byte) 0);
            if (success) {
                return JsonUtils.success("删除成功");
            } else {
                return JsonUtils.fail("删除失败");
            }
        } catch (Exception e) {
            return JsonUtils.fail(e.getMessage());
        }
    }
}

