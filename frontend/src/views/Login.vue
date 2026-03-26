<template>
  <div class="login-page">
    <!-- 简约背景装饰 -->
    <div class="bg-decor">
      <div class="circle c1"></div>
      <div class="circle c2"></div>
      <div class="circle c3"></div>
    </div>

    <div class="login-card">
      <!-- Logo区域 -->
      <div class="logo-section">
        <el-icon :size="42" color="#1677FF">
          <DocumentChecked />
        </el-icon>
        <h1>毕业证书管理系统</h1>
        <p>Graduate Academic Certificate System</p>
      </div>

      <!-- 登录表单 -->
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 底部链接 -->
      <div class="footer-link">
        <router-link to="/register">还没有账号？立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '@/store/user'
import type { FormInstance, FormRules } from 'element-plus'
import { DocumentChecked } from '@element-plus/icons-vue'

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
    { min: 6, message: '密码长度至少6位', trigger: 'blur' },
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
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(160deg, #f5f7fa 0%, #e4ecf7 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 背景装饰圆 */
.bg-decor {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(22, 119, 255, 0.08), rgba(22, 119, 255, 0.03));
}

.c1 {
  width: 400px;
  height: 400px;
  top: -120px;
  right: -100px;
}

.c2 {
  width: 300px;
  height: 300px;
  bottom: -80px;
  left: -80px;
}

.c3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 10%;
  background: linear-gradient(135deg, rgba(103, 126, 234, 0.06), rgba(103, 126, 234, 0.02));
}

.login-card {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 380px;
  background: #fff;
  border-radius: 16px;
  padding: 48px 36px 40px;
  box-shadow: 0 12px 40px rgba(22, 119, 255, 0.08), 0 4px 12px rgba(0, 0, 0, 0.04);
}

.logo-section {
  text-align: center;
  margin-bottom: 36px;
}

.logo-section h1 {
  margin: 16px 0 8px;
  font-size: 22px;
  font-weight: 600;
  color: #1a1a2e;
}

.logo-section p {
  margin: 0;
  font-size: 12px;
  color: #909399;
  letter-spacing: 0.5px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #e4e7ed inset;
  transition: all 0.2s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #1677FF inset;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 500;
  border-radius: 8px;
  background: #1677FF;
  border: none;
}

.login-btn:hover {
  background: #4096ff;
}

.footer-link {
  text-align: center;
  margin-top: 24px;
}

.footer-link a {
  color: #1677FF;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.2s;
}

.footer-link a:hover {
  color: #0050b3;
}
</style>
