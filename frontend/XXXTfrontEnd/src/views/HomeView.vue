<template>
  <div class="home">
    <div class="banner">
      <h1>欢迎来到{{ configStore.appName }}</h1>
      <p>分享学习资料，交流生活资讯，记录日常点滴</p>
      <div class="banner-actions">
        <router-link v-if="!userStore.isLoggedIn" to="/register" class="btn btn-primary">立即注册</router-link>
        <router-link v-if="userStore.isLoggedIn" to="/create-post" class="btn btn-primary">发布帖子</router-link>
      </div>
    </div>

    <div class="container">
      <div class="main-content">
        <div class="category-tabs">
          <button
            v-for="category in configStore.categories"
            :key="category.value"
            class="tab"
            :class="{ active: selectedCategory === category.value }"
            @click="selectedCategory = category.value"
          >
            {{ category.icon }} {{ category.label }}
          </button>
          <button
            class="tab"
            :class="{ active: selectedCategory === null }"
            @click="selectedCategory = null"
          >
            全部
          </button>
        </div>

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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { useConfigStore } from '@/stores/config'
import { postApi, type PostVO } from '@/api/post'
import PostCard from '@/components/PostCard.vue'

const userStore = useUserStore()
const configStore = useConfigStore()
const posts = ref<PostVO[]>([])
const loading = ref(false)
const selectedCategory = ref<number | null>(null)

async function loadPosts() {
  loading.value = true
  try {
    if (selectedCategory.value !== null) {
      posts.value = await postApi.getByCategory(selectedCategory.value)
    } else {
      posts.value = await postApi.getAllPublished()
    }
  } catch (error: any) {
    console.error('加载帖子失败:', error)
    alert(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

watch(selectedCategory, () => {
  loadPosts()
})

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.home {
  min-height: calc(100vh - 200px);
}

.banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 60px 20px;
  text-align: center;
}

.banner h1 {
  margin: 0 0 16px;
  font-size: 36px;
}

.banner p {
  margin: 0 0 24px;
  font-size: 18px;
  opacity: 0.9;
}

.banner-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.btn {
  padding: 12px 24px;
  border-radius: 6px;
  text-decoration: none;
  font-size: 16px;
  transition: all 0.3s;
}

.btn-primary {
  background: #fff;
  color: #667eea;
  font-weight: 600;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.main-content {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.category-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.tab {
  padding: 8px 16px;
  border: 1px solid #e8e8e8;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.tab:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.tab.active {
  background: #1890ff;
  color: #fff;
  border-color: #1890ff;
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
