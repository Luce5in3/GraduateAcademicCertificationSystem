<template>
  <div class="profile">
    <el-card>
      <template #header>
        <h2>个人信息</h2>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">
          {{ userStore.userInfo?.username }}
        </el-descriptions-item>
        <el-descriptions-item label="用户类型">
          学生
        </el-descriptions-item>
        <el-descriptions-item label="学号" :span="2">
          {{ userStore.userInfo?.studentId || '暂无' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
</script>

<style scoped>
.profile {
  max-width: 800px;
  margin: 0 auto;
}

h2 {
  margin: 0;
  color: #303133;
}
</style>
