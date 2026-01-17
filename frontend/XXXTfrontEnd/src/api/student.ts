import request from '@/utils/request'

// 学生相关接口类型定义
export interface LoginDTO {
  unOrEmail: string
  password: string
}

export interface RegisterDTO {
  username: string
  studentId: string
  email: string
  password: string
  code: string
}

export interface Student {
  id: number
  username: string
  email: string
  phone?: string
  avatar?: string
  nickname?: string
  studentId: string
  college?: string
  major?: string
  grade?: string
  status: number
  creditScore: number
  createTime: number
  updateTime: number
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  user: Student
}

// 学生API
export const studentApi = {
  // 登录
  login(data: LoginDTO) {
    return request.post<LoginResponse>('/student/login', data)
  },

  // 注册
  register(data: RegisterDTO) {
    return request.post<string>('/student/register', data)
  },

  // 发送验证码
  sendCode(email: string) {
    return request.post<string>('/student/getcode', null, {
      params: { email }
    })
  },

  // 获取当前用户信息
  getUserInfo() {
    return request.get<Student>('/student/userinfo')
  },

  // 更新用户信息
  updateUser(data: Partial<Student>) {
    return request.put<string>('/student/update', data)
  },

  // 修改密码
  changePassword(oldPassword: string, newPassword: string) {
    return request.put<string>('/student/password/change', {
      oldPassword,
      newPassword
    })
  },

  // 重置密码
  resetPassword(email: string, newPassword: string) {
    return request.put<string>('/student/password/reset', {
      email,
      newPassword
    })
  },

  // 检查用户名是否存在
  checkUsername(username: string) {
    return request.post<{ exists: boolean }>('/student/check/username', { username })
  },

  // 检查邮箱是否存在
  checkEmail(email: string) {
    return request.post<{ exists: boolean }>('/student/check/email', { email })
  },

  // 检查学号是否存在
  checkStudentId(studentId: string) {
    return request.post<{ exists: boolean }>('/student/check/studentId', { studentId })
  }
}

