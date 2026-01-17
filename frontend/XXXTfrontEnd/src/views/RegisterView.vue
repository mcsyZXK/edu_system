<template>
  <div class="register-page">
    <div class="register-container">
      <h2>注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>用户名</label>
          <input
            v-model="form.username"
            type="text"
            placeholder="请输入用户名"
            required
          />
        </div>
        <div class="form-group">
          <label>学号</label>
          <input
            v-model="form.studentId"
            type="text"
            placeholder="请输入学号"
            required
          />
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <div class="input-with-button">
            <input
              v-model="form.email"
              type="email"
              placeholder="请输入邮箱"
              required
            />
            <button
              type="button"
              class="btn btn-small"
              :disabled="codeLoading || countdown > 0"
              @click="sendCode"
            >
              {{ countdown > 0 ? `${countdown}秒后重试` : '发送验证码' }}
            </button>
          </div>
        </div>
        <div class="form-group">
          <label>验证码</label>
          <input
            v-model="form.code"
            type="text"
            placeholder="请输入验证码"
            required
          />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            required
          />
        </div>
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
        <div class="form-footer">
          <router-link to="/login">已有账号？立即登录</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { studentApi } from '@/api/student'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  username: '',
  studentId: '',
  email: '',
  code: '',
  password: ''
})
const loading = ref(false)
const codeLoading = ref(false)
const countdown = ref(0)

async function sendCode() {
  if (!form.value.email) {
    alert('请先输入邮箱')
    return
  }

  codeLoading.value = true
  try {
    await studentApi.sendCode(form.value.email)
    alert('验证码已发送，请查收邮箱')
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error: any) {
    alert(error.message || '发送验证码失败')
  } finally {
    codeLoading.value = false
  }
}

async function handleRegister() {
  loading.value = true
  try {
    const result = await userStore.register(form.value)
    if (result.success) {
      alert('注册成功，请登录')
      router.push('/login')
    } else {
      alert(result.message || '注册失败')
    }
  } catch (error: any) {
    alert(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: calc(100vh - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: #f5f5f5;
}

.register-container {
  background: #fff;
  border-radius: 8px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.register-container h2 {
  margin: 0 0 30px;
  text-align: center;
  font-size: 24px;
  color: #333;
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

.input-with-button {
  display: flex;
  gap: 8px;
}

.input-with-button input {
  flex: 1;
}

.btn-small {
  padding: 12px 16px;
  white-space: nowrap;
}

.btn {
  width: 100%;
  padding: 12px;
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

.btn-small {
  background: #f5f5f5;
  color: #333;
  border: 1px solid #e8e8e8;
}

.btn-small:hover:not(:disabled) {
  background: #e8e8e8;
}

.btn-small:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
  opacity: 0.6;
}

.form-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
}

.form-footer a {
  color: #1890ff;
  text-decoration: none;
}

.form-footer a:hover {
  text-decoration: underline;
}
</style>

