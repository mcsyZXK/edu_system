<template>
  <header class="header">
    <div class="container">
      <div class="logo" @click="$router.push('/')">
        <h1>{{ configStore.appName }}</h1>
      </div>
      
      <nav class="nav">
        <router-link to="/" class="nav-item">首页</router-link>
        <router-link to="/posts" class="nav-item">帖子</router-link>
        <router-link v-if="userStore.isLoggedIn" to="/create-post" class="nav-item">发布</router-link>
      </nav>

      <div class="user-section">
        <template v-if="userStore.isLoggedIn">
          <div class="user-info">
            <span class="credit-score">信誉分: {{ userStore.creditScore }}</span>
            <div class="user-menu" @click="showMenu = !showMenu">
              <img 
                v-if="userStore.avatar" 
                :src="userStore.avatar" 
                :alt="userStore.nickname"
                class="avatar"
              />
              <span v-else class="avatar-placeholder">{{ userStore.nickname?.charAt(0) }}</span>
              <span class="username">{{ userStore.nickname }}</span>
              <span class="arrow">▼</span>
            </div>
            <div v-if="showMenu" class="dropdown-menu">
              <router-link to="/profile" class="menu-item" @click="showMenu = false">个人中心</router-link>
              <router-link to="/my-posts" class="menu-item" @click="showMenu = false">我的帖子</router-link>
              <div class="menu-item" @click="handleLogout">退出登录</div>
            </div>
          </div>
        </template>
        <template v-else>
          <router-link to="/login" class="btn btn-primary">登录</router-link>
          <router-link to="/register" class="btn btn-outline">注册</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useConfigStore } from '@/stores/config'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const configStore = useConfigStore()
const router = useRouter()
const showMenu = ref(false)

function handleLogout() {
  userStore.logout()
  showMenu.value = false
}

// 点击外部关闭菜单
function handleClickOutside(event: MouseEvent) {
  const target = event.target as HTMLElement
  if (!target.closest('.user-menu') && !target.closest('.dropdown-menu')) {
    showMenu.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

.logo {
  cursor: pointer;
}

.logo h1 {
  margin: 0;
  font-size: 24px;
  color: #1890ff;
}

.nav {
  display: flex;
  gap: 24px;
}

.nav-item {
  text-decoration: none;
  color: #333;
  font-weight: 500;
  transition: color 0.3s;
}

.nav-item:hover,
.nav-item.router-link-active {
  color: #1890ff;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.credit-score {
  color: #666;
  font-size: 14px;
}

.user-info {
  position: relative;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background 0.3s;
}

.user-menu:hover {
  background: #f5f5f5;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #1890ff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.username {
  font-size: 14px;
  color: #333;
}

.arrow {
  font-size: 12px;
  color: #999;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  min-width: 120px;
  overflow: hidden;
}

.menu-item {
  display: block;
  padding: 12px 16px;
  color: #333;
  text-decoration: none;
  cursor: pointer;
  transition: background 0.3s;
}

.menu-item:hover {
  background: #f5f5f5;
}

.btn {
  padding: 8px 16px;
  border-radius: 4px;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-primary {
  background: #1890ff;
  color: #fff;
}

.btn-primary:hover {
  background: #40a9ff;
}

.btn-outline {
  border: 1px solid #1890ff;
  color: #1890ff;
}

.btn-outline:hover {
  background: #e6f7ff;
}
</style>

