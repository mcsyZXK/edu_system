<template>
  <div class="posts">
    <div class="container">
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
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useConfigStore } from '@/stores/config'
import { postApi, type PostVO } from '@/api/post'
import PostCard from '@/components/PostCard.vue'

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
.posts {
  min-height: calc(100vh - 200px);
  padding: 40px 20px;
  background: #f5f5f5;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
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

