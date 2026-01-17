<template>
  <div class="comment-item">
    <div class="comment-header">
      <img 
        v-if="comment.avatar" 
        :src="comment.avatar" 
        :alt="comment.username"
        class="avatar"
      />
      <span v-else class="avatar-placeholder">{{ comment.username?.charAt(0) }}</span>
      <div class="user-info">
        <span class="username">{{ comment.nickname || comment.username }}</span>
        <span class="time">{{ formatTime(comment.createTime) }}</span>
      </div>
      <div v-if="isOwner" class="comment-actions">
        <button class="action-btn" @click="handleEdit">编辑</button>
        <button class="action-btn danger" @click="handleDelete">删除</button>
      </div>
    </div>

    <div class="comment-content">{{ comment.content }}</div>

    <div class="comment-footer">
      <button class="reply-btn" @click="handleReply">回复</button>
      <button 
        class="like-btn"
        :class="{ active: isLiked }"
        @click="handleLike"
      >
        👍 {{ comment.likeCount || 0 }}
      </button>
    </div>

    <!-- 子评论 -->
    <div v-if="comment.children && comment.children.length > 0" class="children-comments">
      <CommentItem
        v-for="child in comment.children"
        :key="child.id"
        :comment="child"
        :post-id="postId"
        @reply="handleChildReply"
        @delete="handleDelete"
        @edit="handleEdit"
      />
    </div>

    <!-- 回复框 -->
    <div v-if="showReplyBox" class="reply-box">
      <textarea
        v-model="replyContent"
        placeholder="输入回复内容..."
        rows="3"
        class="reply-input"
      ></textarea>
      <div class="reply-actions">
        <button class="btn btn-primary" @click="submitReply">发送</button>
        <button class="btn btn-cancel" @click="cancelReply">取消</button>
      </div>
    </div>
</div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { commentApi, type CommentVO } from '@/api/comment'

interface Props {
  comment: CommentVO
  postId: number
}

const props = defineProps<Props>()
const emit = defineEmits<{
  reply: [comment: CommentVO, content: string]
  delete: [id: number]
  edit: [comment: CommentVO]
}>()

const userStore = useUserStore()
const showReplyBox = ref(false)
const replyContent = ref('')
const isLiked = ref(false)

const isOwner = computed(() => {
  return userStore.userId === props.comment.userId
})

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

function handleReply() {
  if (!userStore.isLoggedIn) {
    alert('请先登录')
    return
  }
  showReplyBox.value = true
}

function handleChildReply(comment: CommentVO, content: string) {
  emit('reply', comment, content)
}

function submitReply() {
  if (!replyContent.value.trim()) {
    alert('请输入回复内容')
    return
  }
  emit('reply', props.comment, replyContent.value)
  replyContent.value = ''
  showReplyBox.value = false
}

function cancelReply() {
  replyContent.value = ''
  showReplyBox.value = false
}

function handleLike() {
  if (!userStore.isLoggedIn) {
    alert('请先登录')
    return
  }
  isLiked.value = !isLiked.value
  // 这里可以调用点赞API
}

function handleEdit() {
  emit('edit', props.comment)
}

async function handleDelete() {
  if (!confirm('确定要删除这条评论吗？')) {
    return
  }

  try {
    await commentApi.deleteComment(props.comment.id)
    emit('delete', props.comment.id)
  } catch (error: any) {
    alert(error.message || '删除失败')
  }
}
</script>

<style scoped>
.comment-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
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
  font-size: 14px;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.time {
  font-size: 12px;
  color: #999;
}

.comment-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 4px 8px;
  border: none;
  background: transparent;
  color: #666;
  cursor: pointer;
  font-size: 12px;
  border-radius: 4px;
  transition: all 0.3s;
}

.action-btn:hover {
  background: #f5f5f5;
}

.action-btn.danger {
  color: #ff4d4f;
}

.action-btn.danger:hover {
  background: #fff1f0;
}

.comment-content {
  color: #333;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 8px;
  padding-left: 44px;
}

.comment-footer {
  display: flex;
  gap: 16px;
  padding-left: 44px;
}

.reply-btn,
.like-btn {
  padding: 4px 8px;
  border: none;
  background: transparent;
  color: #666;
  cursor: pointer;
  font-size: 12px;
  border-radius: 4px;
  transition: all 0.3s;
}

.reply-btn:hover,
.like-btn:hover {
  background: #f5f5f5;
}

.like-btn.active {
  color: #1890ff;
}

.children-comments {
  margin-left: 44px;
  margin-top: 12px;
  padding-left: 12px;
  border-left: 2px solid #f0f0f0;
}

.reply-box {
  margin-top: 12px;
  padding-left: 44px;
}

.reply-input {
  width: 100%;
  padding: 8px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
}

.reply-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.btn {
  padding: 6px 16px;
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

.btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.btn-cancel:hover {
  background: #e8e8e8;
}
</style>

