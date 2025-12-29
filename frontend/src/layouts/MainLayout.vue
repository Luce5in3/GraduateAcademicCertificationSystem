<template>
  <div class="main-layout">
    <el-container>
      <!-- 头部 -->
      <el-header>
        <div class="header-content">
          <h2>研究生学术认证系统</h2>
          <div class="header-right">
            <span class="username">{{ userStore.userInfo?.username }}</span>
            <el-button @click="handleLogout" type="danger" size="small">退出登录</el-button>
          </div>
        </div>
      </el-header>

      <el-container>
        <!-- 侧边栏 -->
        <el-aside width="200px">
          <el-menu
            :default-active="activeMenu"
            router
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409eff"
          >
            <el-menu-item index="/dashboard">
              <el-icon><HomeFilled /></el-icon>
              <span>首页</span>
            </el-menu-item>

            <!-- 学生菜单 -->
            <template v-if="userStore.isStudent()">
              <el-menu-item index="/student/application">
                <el-icon><DocumentAdd /></el-icon>
                <span>证书申请</span>
              </el-menu-item>
              <el-menu-item index="/student/my-applications">
                <el-icon><List /></el-icon>
                <span>我的申请</span>
              </el-menu-item>
              <el-menu-item index="/student/profile">
                <el-icon><User /></el-icon>
                <span>个人信息</span>
              </el-menu-item>
            </template>

            <!-- 教师菜单 -->
            <template v-if="userStore.isTeacher()">
              <el-menu-item index="/teacher/approval">
                <el-icon><Check /></el-icon>
                <span>审批管理</span>
              </el-menu-item>
              <el-menu-item index="/teacher/applications">
                <el-icon><List /></el-icon>
                <span>申请列表</span>
              </el-menu-item>
              <el-menu-item index="/teacher/profile">
                <el-icon><User /></el-icon>
                <span>个人信息</span>
              </el-menu-item>
            </template>
          </el-menu>
        </el-aside>

        <!-- 主内容区 -->
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { HomeFilled, DocumentAdd, List, User, Check } from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const handleLogout = () => {
  userStore.logout()
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-header {
  background-color: #409eff;
  color: white;
  line-height: 60px;
  padding: 0 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h2 {
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  font-size: 14px;
}

.el-aside {
  background-color: #304156;
  overflow-x: hidden;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.el-menu {
  border-right: none;
}
</style>
