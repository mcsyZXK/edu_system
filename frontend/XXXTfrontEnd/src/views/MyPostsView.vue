<template>
  <div class="my-posts">
    <div class="container">
      <h2>我的帖子</h2>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="posts.length === 0" class="empty">暂无帖子</div>
      <div v-else class="posts-list">
        <PostCard
          v-for="post in posts"
          :key="post.id"
          :post="post"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { postApi, type PostVO } from '@/api/post'
import PostCard from '@/components/PostCard.vue'

const userStore = useUserStore()
const posts = ref<PostVO[]>([])
const loading = ref(false)

onMounted(async () => {
  if (userStore.userId) {
    await loadPosts()
  }
})

async function loadPosts() {
  loading.value = true
  try {
    posts.value = await postApi.getByUserId(userStore.userId!)
  } catch (error: any) {
    console.error('加载帖子失败:', error)
    alert(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.my-posts {
  min-height: calc(100vh - 200px);
  padding: 40px 20px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.container h2 {
  margin: 0 0 24px;
  font-size: 24px;
  color: #333;
}

.loading,
.empty {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
}

.posts-list {
  display: flex;
  flex-direction: column;
}
</style>

