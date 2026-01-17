<template>
  <div class="post-detail">
    <div class="container">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="post" class="post-content">
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

        <h1 class="post-title">{{ post.title }}</h1>
        <div class="post-body" v-html="post.content"></div>

        <div v-if="post.images && post.images.length > 0" class="post-images">
          <img
            v-for="(image, index) in post.images"
            :key="index"
            :src="getImageUrl(image)"
            :alt="`图片${index + 1}`"
            class="post-image"
            @click="previewImage(image)"
          />
        </div>

        <div class="post-actions">
          <button
            class="action-btn"
            :class="{ active: isLiked }"
            @click="handleLike"
          >
            👍 {{ post.likeCount || 0 }}
          </button>
          <button class="action-btn">
            💬 {{ post.commentCount || 0 }}
          </button>
          <button v-if="isOwner" class="action-btn" @click="handleEdit">编辑</button>
          <button v-if="isOwner" class="action-btn danger" @click="handleDelete">删除</button>
        </div>

        <div id="comment" class="comments-section">
          <h3>评论 ({{ comments.length }})</h3>
          <div v-if="userStore.isLoggedIn" class="comment-form">
            <textarea
              v-model="commentContent"
              placeholder="输入评论内容..."
              rows="4"
              class="comment-input"
            ></textarea>
            <button class="btn btn-primary" @click="submitComment">发表评论</button>
          </div>
          <div v-else class="login-prompt">
            <router-link to="/login">请先登录后评论</router-link>
          </div>
          <div class="comments-list">
            <CommentItem
              v-for="comment in comments"
              :key="comment.id"
              :comment="comment"
              :post-id="post.id"
              @reply="handleReply"
              @delete="handleDeleteComment"
              @edit="handleEditComment"
            />
          </div>
        </div>
      </div>
      <div v-else class="empty">帖子不存在</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useConfigStore } from '@/stores/config'
import { postApi, type PostVO } from '@/api/post'
import { commentApi, type CommentVO } from '@/api/comment'
import { fileApi } from '@/api/file'
import CommentItem from '@/components/CommentItem.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const configStore = useConfigStore()

const post = ref<PostVO | null>(null)
const comments = ref<CommentVO[]>([])
const loading = ref(false)
const commentContent = ref('')
const isLiked = ref(false)

const isOwner = computed(() => {
  return userStore.userId === post.value?.userId
})

onMounted(async () => {
  await loadPost()
  await loadComments()
  
  // 如果有锚点，滚动到评论区域
  if (route.hash === '#comment') {
    setTimeout(() => {
      document.getElementById('comment')?.scrollIntoView({ behavior: 'smooth' })
    }, 100)
  }
})

async function loadPost() {
  loading.value = true
  try {
    const postId = parseInt(route.params.id as string)
    post.value = await postApi.getById(postId)
  } catch (error: any) {
    alert(error.message || '加载失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

async function loadComments() {
  try {
    const postId = parseInt(route.params.id as string)
    comments.value = await commentApi.getByPostId(postId)
  } catch (error: any) {
    console.error('加载评论失败:', error)
  }
}

function formatTime(timestamp: number): string {
  const date = new Date(timestamp * 1000)
  return date.toLocaleString('zh-CN')
}

function getImageUrl(imagePath: string): string {
  if (imagePath.startsWith('http')) {
    return imagePath
  }
  return fileApi.getFileUrl(imagePath)
}

function previewImage(image: string) {
  window.open(getImageUrl(image), '_blank')
}

async function handleLike() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  try {
    const increment = isLiked.value ? -1 : 1
    await postApi.likePost(post.value!.id, increment)
    isLiked.value = !isLiked.value
    post.value!.likeCount += increment
  } catch (error: any) {
    alert(error.message || '操作失败')
  }
}

async function submitComment() {
  if (!commentContent.value.trim()) {
    alert('请输入评论内容')
    return
  }

  try {
    const postId = parseInt(route.params.id as string)
    await commentApi.createComment({
      postId,
      content: commentContent.value
    })
    commentContent.value = ''
    await loadComments()
    post.value!.commentCount++
  } catch (error: any) {
    alert(error.message || '评论失败')
  }
}

async function handleReply(comment: CommentVO, content: string) {
  try {
    await commentApi.createComment({
      postId: post.value!.id,
      parentId: comment.id,
      content
    })
    await loadComments()
  } catch (error: any) {
    alert(error.message || '回复失败')
  }
}

function handleEdit() {
  router.push(`/edit-post/${post.value!.id}`)
}

async function handleDelete() {
  if (!confirm('确定要删除这条帖子吗？')) {
    return
  }

  try {
    await postApi.deletePost(post.value!.id)
    alert('删除成功')
    router.push('/')
  } catch (error: any) {
    alert(error.message || '删除失败')
  }
}

async function handleDeleteComment(id: number) {
  await loadComments()
}

function handleEditComment(comment: CommentVO) {
  // 可以在这里实现编辑评论的功能
  const newContent = prompt('编辑评论:', comment.content)
  if (newContent && newContent !== comment.content) {
    commentApi.updateComment({
      id: comment.id,
      content: newContent
    }).then(() => {
      loadComments()
    })
  }
}
</script>

<style scoped>
.post-detail {
  min-height: calc(100vh - 200px);
  padding: 40px 20px;
}

.container {
  max-width: 900px;
  margin: 0 auto;
}

.loading,
.empty {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
}

.post-content {
  background: #fff;
  border-radius: 8px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #1890ff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-weight: 500;
  color: #333;
  font-size: 16px;
}

.category {
  font-size: 14px;
  color: #999;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.time {
  font-size: 14px;
  color: #999;
}

.top-badge {
  background: #ff4d4f;
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.post-title {
  margin: 0 0 24px;
  font-size: 28px;
  font-weight: 600;
  color: #333;
  line-height: 1.5;
}

.post-body {
  color: #333;
  font-size: 16px;
  line-height: 1.8;
  margin-bottom: 24px;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  margin-bottom: 24px;
}

.post-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  transition: transform 0.3s;
}

.post-image:hover {
  transform: scale(1.05);
}

.post-actions {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 32px;
}

.action-btn {
  padding: 8px 16px;
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

.action-btn.danger {
  color: #ff4d4f;
}

.action-btn.danger:hover {
  border-color: #ff4d4f;
  background: #fff1f0;
}

.comments-section {
  margin-top: 32px;
}

.comments-section h3 {
  margin: 0 0 20px;
  font-size: 20px;
  color: #333;
}

.comment-form {
  margin-bottom: 32px;
}

.comment-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
  margin-bottom: 12px;
}

.comment-input:focus {
  outline: none;
  border-color: #1890ff;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
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

.login-prompt {
  text-align: center;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 4px;
  margin-bottom: 32px;
}

.login-prompt a {
  color: #1890ff;
  text-decoration: none;
}

.comments-list {
  display: flex;
  flex-direction: column;
}
</style>

