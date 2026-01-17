import axios from 'axios'
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { useUserStore } from '@/stores/user'
import router from '@/router'

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:4096/zxk',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const userStore = useUserStore()
    // 如果有token，添加到请求头
    if (userStore.token && config.headers) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data as any
    
    // 如果返回的数据格式是 { code, data, message }
    if (res.code !== undefined) {
      if (res.code === 200 || res.code === 0) {
        return res.data !== undefined ? res.data : res
      } else {
        // 业务错误
        return Promise.reject(new Error(res.message || '请求失败'))
      }
    }
    
    // 直接返回数据
    return res
  },
  (error) => {
    // HTTP错误处理
    if (error.response) {
      const status = error.response.status
      switch (status) {
        case 401:
          // 未授权，清除token
          const userStore = useUserStore()
          // 只有在需要登录的页面才跳转到登录页
          const currentRoute = router.currentRoute.value
          if (currentRoute.meta.requiresAuth) {
            userStore.logout()
          } else {
            // 对于公开页面，只清除 token，不跳转
            userStore.token = null
            userStore.refreshToken = null
            userStore.userInfo = null
            localStorage.removeItem('accessToken')
            localStorage.removeItem('refreshToken')
            localStorage.removeItem('userInfo')
          }
          break
        case 403:
          return Promise.reject(new Error('没有权限访问'))
        case 404:
          return Promise.reject(new Error('请求的资源不存在'))
        case 500:
          return Promise.reject(new Error('服务器错误'))
        default:
          return Promise.reject(new Error(error.response.data?.message || '请求失败'))
      }
    } else if (error.request) {
      return Promise.reject(new Error('网络错误，请检查网络连接'))
    } else {
      return Promise.reject(new Error(error.message || '请求失败'))
    }
  }
)

export default service

