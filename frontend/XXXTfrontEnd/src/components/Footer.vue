<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h3>{{ configStore.appName }}</h3>
          <p>校园信息交流平台</p>
        </div>
        <div class="footer-section">
          <h4>快速链接</h4>
          <router-link to="/">首页</router-link>
          <router-link to="/posts">帖子</router-link>
          <router-link v-if="userStore.isLoggedIn" to="/create-post">发布</router-link>
        </div>
        <div class="footer-section">
          <h4>关于我们</h4>
          <p>致力于为校园用户提供优质的信息交流服务</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2024 {{ configStore.appName }}. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { useConfigStore } from '@/stores/config'

const userStore = useUserStore()
const configStore = useConfigStore()
</script>

<style scoped>
.footer {
  background: #f5f5f5;
  padding: 40px 0 20px;
  margin-top: 60px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 40px;
  margin-bottom: 30px;
}

.footer-section h3 {
  margin: 0 0 12px;
  color: #1890ff;
  font-size: 20px;
}

.footer-section h4 {
  margin: 0 0 12px;
  color: #333;
  font-size: 16px;
}

.footer-section p {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
}

.footer-section a {
  display: block;
  color: #666;
  text-decoration: none;
  margin: 8px 0;
  transition: color 0.3s;
}

.footer-section a:hover {
  color: #1890ff;
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #e8e8e8;
}

.footer-bottom p {
  margin: 0;
  color: #999;
  font-size: 12px;
}
</style>

