<template>
  <div class="create-post">
    <div class="container">
      <h2>发布帖子</h2>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label>标题</label>
          <input
            v-model="form.title"
            type="text"
            placeholder="请输入帖子标题"
            required
            maxlength="100"
          />
        </div>

        <div class="form-group">
          <label>分类</label>
          <select v-model="form.category" required>
            <option value="">请选择分类</option>
            <option
              v-for="category in configStore.categories"
              :key="category.value"
              :value="category.value"
            >
              {{ category.icon }} {{ category.label }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label>内容</label>
          <textarea
            v-model="form.content"
            placeholder="请输入帖子内容..."
            rows="10"
            required
            class="content-input"
          ></textarea>
        </div>

        <div class="form-group">
          <label>图片（可选，最多9张）</label>
          <div class="upload-area">
            <input
              ref="fileInput"
              type="file"
              multiple
              accept="image/*"
              @change="handleFileChange"
              style="display: none"
            />
            <div class="image-preview-list">
              <div
                v-for="(image, index) in form.images"
                :key="index"
                class="image-preview-item"
              >
                <img :src="image" :alt="`图片${index + 1}`" />
                <button
                  type="button"
                  class="remove-btn"
                  @click="removeImage(index)"
                >
                  ×
                </button>
              </div>
              <div
                v-if="form.images.length < 9"
                class="upload-placeholder"
                @click="$refs.fileInput.click()"
              >
                <span>+</span>
                <span>添加图片</span>
              </div>
            </div>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? '发布中...' : '发布' }}
          </button>
          <button type="button" class="btn btn-cancel" @click="$router.back()">
            取消
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useConfigStore } from '@/stores/config'
import { postApi } from '@/api/post'
import { fileApi } from '@/api/file'

const router = useRouter()
const configStore = useConfigStore()

const form = ref({
  title: '',
  category: 0,
  content: '',
  images: [] as string[]
})
const loading = ref(false)
const fileInput = ref<HTMLInputElement>()

async function handleFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  const files = target.files
  if (!files) return

  for (let i = 0; i < files.length && form.value.images.length < 9; i++) {
    const file = files[i]
    if (file.type.startsWith('image/')) {
      try {
        const result = await fileApi.uploadFile(file)
        form.value.images.push(result.fileUrl)
      } catch (error: any) {
        alert(`上传图片失败: ${error.message || '未知错误'}`)
      }
    }
  }
  
  // 清空input，允许重复选择同一文件
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

function removeImage(index: number) {
  form.value.images.splice(index, 1)
}

async function handleSubmit() {
  loading.value = true
  try {
    await postApi.createPost({
      title: form.value.title,
      category: form.value.category,
      content: form.value.content,
      images: form.value.images.length > 0 ? form.value.images : undefined
    })
    alert('发布成功')
    router.push('/')
  } catch (error: any) {
    alert(error.message || '发布失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.create-post {
  min-height: calc(100vh - 200px);
  padding: 40px 20px;
  background: #f5f5f5;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  background: #fff;
  border-radius: 8px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.container h2 {
  margin: 0 0 24px;
  font-size: 24px;
  color: #333;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #1890ff;
}

.content-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
}

.content-input:focus {
  outline: none;
  border-color: #1890ff;
}

.upload-area {
  margin-top: 8px;
}

.image-preview-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
}

.image-preview-item {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 4px;
  overflow: hidden;
}

.image-preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  border: none;
  cursor: pointer;
  font-size: 18px;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-placeholder {
  width: 120px;
  height: 120px;
  border: 2px dashed #e8e8e8;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-placeholder:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.upload-placeholder span:first-child {
  font-size: 32px;
  margin-bottom: 8px;
}

.upload-placeholder span:last-child {
  font-size: 12px;
  color: #999;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 32px;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background: #1890ff;
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  background: #40a9ff;
}

.btn-primary:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.btn-cancel:hover {
  background: #e8e8e8;
}
</style>

