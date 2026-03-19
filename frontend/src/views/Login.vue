<template>
  <div class="login-container">
    <div class="login-background">
      <div class="background-shape shape-1"></div>
      <div class="background-shape shape-2"></div>
      <div class="background-shape shape-3"></div>
    </div>
    
    <el-card class="login-card" :class="{ 'card-enter': true }">
      <template #header>
        <div class="card-header">
          <div class="logo-container">
            <el-icon :size="48" color="var(--primary-color)">
              <DocumentChecked />
            </el-icon>
          </div>
          <h2>毕业证书管理系统</h2>
          <p>Graduate Academic Certificate System</p>
        </div>
      </template>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="80px"
        class="login-form"
      >
        <el-form-item label="用户名" prop="username" class="form-item">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            clearable
            size="large"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password" class="form-item">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
            clearable
            size="large"
            prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item class="form-item">
          <el-button
            type="primary"
            style="width: 100%"
            :loading="loading"
            @click="handleLogin"
            size="large"
            class="login-button"
          >
            <template #default>
              <span v-if="!loading">登录</span>
              <span v-else>登录中...</span>
            </template>
          </el-button>
        </el-form-item>

        <el-form-item class="form-item">
          <div class="footer-links">
            <router-link to="/register" class="register-link">
              还没有账号？去注册
            </router-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import type { FormInstance, FormRules } from 'element-plus'
import { DocumentChecked, User, Lock } from '@element-plus/icons-vue'

const userStore = useUserStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
})

const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' },
  ],
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(loginForm)
      } finally {
        loading.value = false
      }
    }
  })
}

// 页面加载时的动画效果
onMounted(() => {
  const formItems = document.querySelectorAll('.form-item')
  formItems.forEach((item, index) => {
    setTimeout(() => {
      (item as HTMLElement).style.opacity = '1'
      (item as HTMLElement).style.transform = 'translateY(0)'
    }, 200 * (index + 1))
  })
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-active) 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.background-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.shape-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: -50px;
  animation-delay: 2s;
}

.shape-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  right: 10%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 登录卡片 */
.login-card {
  width: 450px;
  z-index: 1;
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  transform: translateY(20px);
  opacity: 0;
  animation: cardEnter 0.8s ease-out forwards;
}

@keyframes cardEnter {
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* 卡片头部 */
.card-header {
  text-align: center;
  padding: var(--spacing-xl) 0;
}

.logo-container {
  margin-bottom: var(--spacing-md);
  animation: logoPulse 2s ease-in-out infinite;
}

@keyframes logoPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.card-header h2 {
  margin: 0 0 10px 0;
  color: var(--primary-color);
  font-size: 28px;
  font-weight: 600;
  animation: textFadeIn 1s ease-out forwards;
}

.card-header p {
  margin: 0;
  color: var(--text-color-light);
  font-size: 14px;
  animation: textFadeIn 1s ease-out 0.3s forwards;
  opacity: 0;
}

@keyframes textFadeIn {
  to {
    opacity: 1;
  }
}

/* 登录表单 */
.login-form {
  padding: 0 var(--spacing-xl) var(--spacing-xl);
}

.form-item {
  margin-bottom: var(--spacing-lg);
  opacity: 1;
  transform: translateY(0);
  transition: all 0.5s ease-out;
}

/* 输入框样式 */
:deep(.el-input__wrapper) {
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.1);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(22, 119, 255, 0.2);
}

/* 登录按钮 */
.login-button {
  border-radius: var(--border-radius-md);
  padding: 12px 0;
  font-size: 16px;
  font-weight: 600;
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(22, 119, 255, 0.3);
}

.login-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.login-button:hover::before {
  left: 100%;
}

/* 底部链接 */
.footer-links {
  width: 100%;
  text-align: center;
}

.register-link {
  color: var(--primary-color);
  text-decoration: none;
  font-size: 14px;
  transition: all var(--transition-fast);
  position: relative;
}

.register-link:hover {
  color: var(--primary-hover);
}

.register-link::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 1px;
  background: var(--primary-color);
  transition: width var(--transition-fast);
}

.register-link:hover::after {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    width: 90%;
    max-width: 400px;
  }
  
  .card-header {
    padding: var(--spacing-lg) 0;
  }
  
  .login-form {
    padding: 0 var(--spacing-lg) var(--spacing-lg);
  }
}
</style>
