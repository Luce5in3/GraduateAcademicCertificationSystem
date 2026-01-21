<template>
  <div class="main-layout">
    <el-container>
      <!-- 头部 -->
      <el-header>
        <div class="header-content">
          <div class="logo-area">
            <h2>🎓 毕业学术证书管理系统</h2>
          </div>
          <div class="header-right">
            <div class="user-info">
              <el-avatar :size="36" icon="UserFilled" />
              <div class="user-details">
                <span class="username">{{ userStore.userInfo?.username }}</span>
                <span class="user-role">{{ userRoleText }}</span>
              </div>
            </div>
            <el-dropdown trigger="click" @command="handleCommand">
              <el-button circle>
                <el-icon><Setting /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人信息
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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

            <!-- 管理员菜单 -->
            <template v-if="userStore.isAdmin()">
              <el-menu-item index="/admin/templates">
                <el-icon><DocumentCopy /></el-icon>
                <span>证明模板管理</span>
              </el-menu-item>
              <el-menu-item index="/admin/students">
                <el-icon><User /></el-icon>
                <span>学生信息管理</span>
              </el-menu-item>
              <el-menu-item index="/admin/teachers">
                <el-icon><Avatar /></el-icon>
                <span>教师信息管理</span>
              </el-menu-item>
              <el-menu-item index="/admin/applications">
                <el-icon><Files /></el-icon>
                <span>全站申请记录</span>
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
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { HomeFilled, DocumentAdd, List, User, Check, Setting, SwitchButton, DocumentCopy, Avatar, Files, Operation } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const userRoleText = computed(() => {
  if (userStore.isAdmin()) return '管理员'
  if (userStore.isTeacher()) return '教师'
  if (userStore.isStudent()) return '学生'
  return '用户'
})

const handleCommand = (command: string) => {
  if (command === 'logout') {
    handleLogout()
  } else if (command === 'profile') {
    let profilePath = '/dashboard'
    if (userStore.isStudent()) profilePath = '/student/profile'
    else if (userStore.isTeacher()) profilePath = '/teacher/profile'
    router.push(profilePath)
  }
}

const handleLogout = () => {
  userStore.logout()
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
  min-width: 1200px; /* 确保PC端最小宽度 */
}

.el-container {
  height: 100%;
}

.el-header {
  background: linear-gradient(135deg, #1677FF 0%, #0050B3 100%);
  color: white;
  line-height: 60px;
  padding: 0 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 1000;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-area {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  backdrop-filter: blur(10px);
  cursor: pointer;
  transition: all 0.3s;
}

.user-info:hover {
  background-color: rgba(255, 255, 255, 0.25);
}

.user-details {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.username {
  font-size: 14px;
  font-weight: 500;
}

.user-role {
  font-size: 12px;
  opacity: 0.85;
}

.el-aside {
  background-color: #304156;
  overflow-x: hidden;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.el-main {
  background-color: #F5F7FA;
  padding: 24px;
  overflow-y: auto;
  min-height: calc(100vh - 60px);
}

.el-menu {
  border-right: none;
  height: 100%;
}

:deep(.el-menu-item) {
  height: 56px;
  line-height: 56px;
  margin: 4px 8px;
  border-radius: 8px;
}

:deep(.el-menu-item:hover) {
  background-color: rgba(64, 158, 255, 0.1) !important;
}

:deep(.el-menu-item.is-active) {
  background-color: #1677FF !important;
  color: #fff !important;
}</style>
