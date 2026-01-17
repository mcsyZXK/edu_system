import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { studentApi, type Student, type LoginResponse } from '@/api/student'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string | null>(localStorage.getItem('accessToken'))
  const refreshToken = ref<string | null>(localStorage.getItem('refreshToken'))
  const userInfo = ref<Student | null>(null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const userId = computed(() => userInfo.value?.id)
  const username = computed(() => userInfo.value?.username)
  const nickname = computed(() => userInfo.value?.nickname || userInfo.value?.username)
  const avatar = computed(() => userInfo.value?.avatar)
  const creditScore = computed(() => userInfo.value?.creditScore || 100)

  // 登录
  async function login(loginData: { unOrEmail: string; password: string }) {
    try {
      const response = await studentApi.login(loginData)
      const data = response as unknown as LoginResponse
      
      token.value = data.accessToken
      refreshToken.value = data.refreshToken
      userInfo.value = data.user

      // 保存到localStorage
      localStorage.setItem('accessToken', data.accessToken)
      localStorage.setItem('refreshToken', data.refreshToken)
      if (data.user) {
        localStorage.setItem('userInfo', JSON.stringify(data.user))
      }

      return { success: true }
    } catch (error: any) {
      return { success: false, message: error.message || '登录失败' }
    }
  }

  // 注册
  async function register(registerData: {
    username: string
    studentId: string
    email: string
    password: string
    code: string
  }) {
    try {
      await studentApi.register(registerData)
      return { success: true }
    } catch (error: any) {
      return { success: false, message: error.message || '注册失败' }
    }
  }

  // 获取用户信息
  async function fetchUserInfo() {
    try {
      const data = await studentApi.getUserInfo()
      userInfo.value = data as Student
      localStorage.setItem('userInfo', JSON.stringify(data))
      return { success: true }
    } catch (error: any) {
      // 如果获取失败，清除token
      if (error.message?.includes('未登录') || error.message?.includes('Token')) {
        logout()
      }
      return { success: false, message: error.message || '获取用户信息失败' }
    }
  }

  // 更新用户信息
  async function updateUserInfo(userData: Partial<Student>) {
    try {
      await studentApi.updateUser(userData)
      // 重新获取用户信息
      await fetchUserInfo()
      return { success: true }
    } catch (error: any) {
      return { success: false, message: error.message || '更新失败' }
    }
  }

  // 修改密码
  async function changePassword(oldPassword: string, newPassword: string) {
    try {
      await studentApi.changePassword(oldPassword, newPassword)
      return { success: true }
    } catch (error: any) {
      return { success: false, message: error.message || '修改密码失败' }
    }
  }

  // 登出
  function logout() {
    token.value = null
    refreshToken.value = null
    userInfo.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }

  // 初始化（从localStorage恢复）
  function init() {
    const savedToken = localStorage.getItem('accessToken')
    const savedUserInfo = localStorage.getItem('userInfo')
    
    if (savedToken) {
      token.value = savedToken
    }
    
    if (savedUserInfo) {
      try {
        userInfo.value = JSON.parse(savedUserInfo)
      } catch (e) {
        console.error('解析用户信息失败', e)
      }
    }
    
    // 如果有token但没有用户信息，尝试获取
    if (token.value && !userInfo.value) {
      fetchUserInfo()
    }
  }

  return {
    // 状态
    token,
    refreshToken,
    userInfo,
    // 计算属性
    isLoggedIn,
    userId,
    username,
    nickname,
    avatar,
    creditScore,
    // 方法
    login,
    register,
    fetchUserInfo,
    updateUserInfo,
    changePassword,
    logout,
    init
  }
})

