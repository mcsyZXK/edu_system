<template>
  <div class="post-card" @click="handleClick">
    <div class="post-header">
      <div class="user-info">
        <img 
          v-if="post.avatar" 
          :src="post.avatar" 
          :alt="post.username"
          class="avatar"
        />
        <span v-else class="avatar-placeholder">{{ post.username?.charAt(0) }}</span>
        <div class="user-details">
          <span class="username">{{ post.nickname || post.username }}</span>
          <span class="category">{{ configStore.getCategoryName(post.category) }}</span>
        </div>
      </div>
      <div class="post-meta">
        <span class="time">{{ formatTime(post.createTime) }}</span>
        <span v-if="post.isTop" class="top-badge">置顶</span>
      </div>
    </div>

    <h3 class="post-title">{{ post.title }}</h3>
    
    <div class="post-content" v-html="truncateContent(post.content)"></div>

    <div v-if="post.images && post.images.length > 0" class="post-images">
      <img 
        v-for="(image, index) in post.images.slice(0, 3)" 
        :key="index"
        :src="getImageUrl(image)"
        :alt="`图片${index + 1}`"
        class="post-image"
        @click.stop="previewImage(image)"
      />
      <div v-if="post.images.length > 3" class="more-images">+{{ post.images.length - 3 }}</div>
    </div>

    <div class="post-footer">
      <div class="post-stats">
        <span class="stat-item">
          <span class="icon">👍</span>
          {{ post.likeCount || 0 }}
        </span>
        <span class="stat-item">
          <span class="icon">💬</span>
          {{ post.commentCount || 0 }}
        </span>
      </div>
      <div class="post-actions">
        <button 
          class="action-btn"
          :class="{ active: isLiked }"
          @click.stop="handleLike"
        >
          👍 点赞
        </button>
        <button 
          class="action-btn"
          @click.stop="handleComment"
        >
          💬 评论
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useConfigStore } from '@/stores/config'
import { useUserStore } from '@/stores/user'
import { postApi, type PostVO } from '@/api/post'
import { fileApi } from '@/api/file'

interface Props {
  post: PostVO
}

const props = defineProps<Props>()
const router = useRouter()
const configStore = useConfigStore()
const userStore = useUserStore()
const isLiked = ref(false) // 这里可以根据实际情况判断是否已点赞

function handleClick() {
  router.push(`/post/${props.post.id}`)
}

function truncateContent(content: string): string {
  if (!content) return ''
  // 移除HTML标签
  const text = content.replace(/<[^>]*>/g, '')
  if (text.length > 150) {
    return text.substring(0, 150) + '...'
  }
  return text
}

function formatTime(timestamp: number): string {
  const date = new Date(timestamp * 1000)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

function getImageUrl(imagePath: string): string {
  if (imagePath.startsWith('http')) {
    return imagePath
  }
  return fileApi.getFileUrl(imagePath)
}

function previewImage(image: string) {
  // 可以在这里实现图片预览功能
  window.open(getImageUrl(image), '_blank')
}

async function handleLike() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  try {
    const increment = isLiked.value ? -1 : 1
    await postApi.likePost(props.post.id, increment)
    isLiked.value = !isLiked.value
    // 更新本地点赞数
    props.post.likeCount += increment
  } catch (error: any) {
    console.error('点赞失败:', error)
    alert(error.message || '操作失败')
  }
}

function handleComment() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  router.push(`/post/${props.post.id}#comment`)
}
</script>

<style scoped>
.post-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s;
}

.post-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #1890ff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.category {
  font-size: 12px;
  color: #999;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.time {
  font-size: 12px;
  color: #999;
}

.top-badge {
  background: #ff4d4f;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.post-title {
  margin: 0 0 12px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  line-height: 1.5;
}

.post-content {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 12px;
}

.post-images {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.post-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
}

.more-images {
  width: 120px;
  height: 120px;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  font-size: 18px;
  font-weight: bold;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.post-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #666;
}

.icon {
  font-size: 16px;
}

.post-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 12px;
  border: 1px solid #e8e8e8;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.action-btn:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.action-btn.active {
  background: #1890ff;
  color: #fff;
  border-color: #1890ff;
}
</style>

