import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useConfigStore = defineStore('config', () => {
  // 全局配置
  const apiBaseUrl = ref<string>(import.meta.env.VITE_API_BASE_URL || 'http://localhost:4096/zxk')
  const appName = ref<string>('校园平台')
  const theme = ref<'light' | 'dark'>('light')
  
  // 分类配置
  const categories = ref([
    { value: 0, label: '学习资料', icon: '📚' },
    { value: 1, label: '生活资讯', icon: '💡' },
    { value: 2, label: '日常分享', icon: '✨' }
  ])

  // 状态配置
  const statuses = ref([
    { value: 0, label: '待审核' },
    { value: 1, label: '已发布' },
    { value: 2, label: '已驳回' }
  ])

  // 获取分类名称
  function getCategoryName(category: number): string {
    const item = categories.value.find(c => c.value === category)
    return item?.label || '未知分类'
  }

  // 获取状态名称
  function getStatusName(status: number): string {
    const item = statuses.value.find(s => s.value === status)
    return item?.label || '未知状态'
  }

  // 切换主题
  function toggleTheme() {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    // 可以在这里保存主题到localStorage
    localStorage.setItem('theme', theme.value)
  }

  // 初始化主题
  function initTheme() {
    const savedTheme = localStorage.getItem('theme')
    if (savedTheme === 'light' || savedTheme === 'dark') {
      theme.value = savedTheme
    }
  }

  return {
    apiBaseUrl,
    appName,
    theme,
    categories,
    statuses,
    getCategoryName,
    getStatusName,
    toggleTheme,
    initTheme
  }
})

