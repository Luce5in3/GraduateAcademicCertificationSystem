<template>
  <div class="dashboard">
    <el-card>
      <h1>欢迎使用研究生学术认证系统</h1>
      <el-divider />
      
      <div class="welcome-info">
        <p>当前用户：{{ userStore.userInfo?.username }}</p>
        <p>用户类型：{{ userTypeText }}</p>
      </div>

      <el-divider />

      <div class="quick-links">
        <h3>快捷入口</h3>
        <div class="link-buttons">
          <template v-if="userStore.isStudent()">
            <el-button type="primary" @click="router.push('/student/application')">
              提交证书申请
            </el-button>
            <el-button type="success" @click="router.push('/student/my-applications')">
              查看我的申请
            </el-button>
          </template>
          
          <template v-if="userStore.isTeacher()">
            <el-button type="primary" @click="router.push('/teacher/approval')">
              待审批列表
            </el-button>
            <el-button type="success" @click="router.push('/teacher/applications')">
              查看所有申请
            </el-button>
          </template>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const userTypeText = computed(() => {
  const type = userStore.getUserType()
  return type === 1 ? '学生' : type === 2 ? '教师' : '未知'
})
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  color: #409eff;
}

.welcome-info {
  font-size: 16px;
  line-height: 2;
}

.quick-links {
  margin-top: 20px;
}

.quick-links h3 {
  margin-bottom: 15px;
}

.link-buttons {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}
</style>
