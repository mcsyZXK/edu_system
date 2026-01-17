<template>
  <div class="profile">
    <div class="container">
      <h2>个人中心</h2>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="userInfo" class="profile-content">
        <div class="profile-header">
          <div class="avatar-section">
            <img
              v-if="userInfo.avatar"
              :src="userInfo.avatar"
              :alt="userInfo.username"
              class="avatar-large"
            />
            <span v-else class="avatar-placeholder-large">
              {{ userInfo.username?.charAt(0) }}
            </span>
            <div class="user-basic">
              <h3>{{ userInfo.nickname || userInfo.username }}</h3>
              <p>信誉分: {{ userInfo.creditScore }}</p>
            </div>
          </div>
        </div>

        <div class="profile-form">
          <div class="form-group">
            <label>用户名</label>
            <input v-model="form.username" type="text" disabled />
          </div>
          <div class="form-group">
            <label>学号</label>
            <input v-model="form.studentId" type="text" disabled />
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input v-model="form.email" type="email" />
          </div>
          <div class="form-group">
            <label>手机号</label>
            <input v-model="form.phone" type="tel" />
          </div>
          <div class="form-group">
            <label>昵称</label>
            <input v-model="form.nickname" type="text" />
          </div>
          <div class="form-group">
            <label>学院</label>
            <input v-model="form.college" type="text" />
          </div>
          <div class="form-group">
            <label>专业</label>
            <input v-model="form.major" type="text" />
          </div>
          <div class="form-group">
            <label>年级</label>
            <input v-model="form.grade" type="text" />
          </div>
          <div class="form-actions">
            <button class="btn btn-primary" @click="handleSave" :disabled="saving">
              {{ saving ? '保存中...' : '保存' }}
            </button>
            <button class="btn btn-secondary" @click="handleChangePassword">
              修改密码
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import type { Student } from '@/api/student'

const userStore = useUserStore()
const userInfo = ref<Student | null>(null)
const loading = ref(false)
const saving = ref(false)

const form = ref({
  username: '',
  studentId: '',
  email: '',
  phone: '',
  nickname: '',
  college: '',
  major: '',
  grade: ''
})

onMounted(async () => {
  await loadUserInfo()
})

async function loadUserInfo() {
  loading.value = true
  try {
    const result = await userStore.fetchUserInfo()
    if (result.success && userStore.userInfo) {
      userInfo.value = userStore.userInfo
      Object.assign(form.value, {
        username: userInfo.value.username || '',
        studentId: userInfo.value.studentId || '',
        email: userInfo.value.email || '',
        phone: userInfo.value.phone || '',
        nickname: userInfo.value.nickname || '',
        college: userInfo.value.college || '',
        major: userInfo.value.major || '',
        grade: userInfo.value.grade || ''
      })
    }
  } catch (error: any) {
    alert(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  saving.value = true
  try {
    const result = await userStore.updateUserInfo({
      email: form.value.email,
      phone: form.value.phone,
      nickname: form.value.nickname,
      college: form.value.college,
      major: form.value.major,
      grade: form.value.grade
    })
    if (result.success) {
      alert('保存成功')
      await loadUserInfo()
    } else {
      alert(result.message || '保存失败')
    }
  } catch (error: any) {
    alert(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

function handleChangePassword() {
  const oldPassword = prompt('请输入旧密码:')
  if (!oldPassword) return

  const newPassword = prompt('请输入新密码:')
  if (!newPassword) return

  const confirmPassword = prompt('请确认新密码:')
  if (newPassword !== confirmPassword) {
    alert('两次输入的密码不一致')
    return
  }

  userStore.changePassword(oldPassword, newPassword).then(result => {
    if (result.success) {
      alert('密码修改成功')
    } else {
      alert(result.message || '密码修改失败')
    }
  })
}
</script>

<style scoped>
.profile {
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

.loading {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
}

.profile-header {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
}

.avatar-large {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder-large {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #1890ff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 32px;
}

.user-basic h3 {
  margin: 0 0 8px;
  font-size: 24px;
  color: #333;
}

.user-basic p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #1890ff;
}

.form-group input:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
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

.btn-secondary {
  background: #f5f5f5;
  color: #666;
}

.btn-secondary:hover {
  background: #e8e8e8;
}
</style>

