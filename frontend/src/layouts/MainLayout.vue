<template>
  <div class="main-layout">
    <el-container>
      <!-- 头部 -->
      <el-header>
        <div class="header-content">
          <div class="header-left">
            <el-button
              v-if="isMobile"
              type="text"
              @click="toggleSidebar"
              class="sidebar-toggle"
            >
              <el-icon :size="24"><Menu /></el-icon>
            </el-button>
            <div class="logo-area">
              <h2 v-if="!isMobile">🎓 毕业学术证书管理系统</h2>
              <h2 v-else>🎓 证书系统</h2>
            </div>
          </div>
          <div class="header-right">
            <div class="user-info">
              <el-avatar :size="isMobile ? 32 : 36" icon="UserFilled" />
              <div class="user-details" v-if="!isMobile">
                <span class="username">{{ userStore.userInfo?.username }}</span>
                <span class="user-role">{{ userRoleText }}</span>
              </div>
            </div>
            <el-dropdown trigger="click" @command="handleCommand">
              <el-button circle :size="isMobile ? 'default' : 'large'">
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
        <el-aside :width="isSidebarCollapsed ? '64px' : '200px'" class="sidebar">
          <el-menu
            :default-active="activeMenu"
            router
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409eff"
            :collapse="isSidebarCollapsed"
          >
            <el-menu-item index="/dashboard">
              <el-icon><HomeFilled /></el-icon>
              <template #title>
                <span>首页</span>
              </template>
            </el-menu-item>

            <!-- 学生菜单 -->
            <template v-if="userStore.isStudent()">
              <el-menu-item index="/student/application">
                <el-icon><DocumentAdd /></el-icon>
                <template #title>
                  <span>证书申请</span>
                </template>
              </el-menu-item>
              <el-menu-item index="/student/my-applications">
                <el-icon><List /></el-icon>
                <template #title>
                  <span>我的申请</span>
                </template>
              </el-menu-item>
              <el-menu-item index="/student/profile">
                <el-icon><User /></el-icon>
                <template #title>
                  <span>个人信息</span>
                </template>
              </el-menu-item>
            </template>

            <!-- 教师菜单 -->
            <template v-if="userStore.isTeacher()">
              <el-menu-item index="/teacher/approval">
                <el-icon><Check /></el-icon>
                <template #title>
                  <span>审批管理</span>
                </template>
              </el-menu-item>
              <el-menu-item index="/teacher/applications">
                <el-icon><List /></el-icon>
                <template #title>
                  <span>申请列表</span>
                </template>
              </el-menu-item>
              <el-menu-item index="/teacher/profile">
                <el-icon><User /></el-icon>
                <template #title>
                  <span>个人信息</span>
                </template>
              </el-menu-item>
            </template>

            <!-- 管理员菜单 -->
            <template v-if="userStore.isAdmin()">
              <el-menu-item index="/admin/templates">
                <el-icon><DocumentCopy /></el-icon>
                <template #title>
                  <span>证明模板管理</span>
                </template>
              </el-menu-item>
              <el-menu-item index="/admin/students">
                <el-icon><User /></el-icon>
                <template #title>
                  <span>学生信息管理</span>
                </template>
              </el-menu-item>
              <el-menu-item index="/admin/teachers">
                <el-icon><Avatar /></el-icon>
                <template #title>
                  <span>教师信息管理</span>
                </template>
              </el-menu-item>
              <el-menu-item index="/admin/applications">
                <el-icon><Files /></el-icon>
                <template #title>
                  <span>全站申请记录</span>
                </template>
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
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { HomeFilled, DocumentAdd, List, User, Check, Setting, SwitchButton, DocumentCopy, Avatar, Files, Menu } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isSidebarCollapsed = ref(false)
const isMobile = ref(false)

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

const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}

const checkScreenSize = () => {
  isMobile.value = window.innerWidth < 768
  if (isMobile.value) {
    isSidebarCollapsed.value = true
  } else {
    isSidebarCollapsed.value = false
  }
}

onMounted(() => {
  checkScreenSize()
  window.addEventListener('resize', checkScreenSize)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkScreenSize)
})
</script>

<style scoped>
.main-layout {
  height: 100vh;
  width: 100%;
  overflow: hidden;
}

.el-container {
  height: 100%;
}

.el-header {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-active) 100%);
  color: white;
  line-height: 60px;
  padding: 0 var(--spacing-md);
  box-shadow: var(--shadow-sm);
  z-index: 1000;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex: 1;
}

.sidebar-toggle {
  color: white;
  font-size: 20px;
}

.logo-area {
  flex: 1;
}

.header-content h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all var(--transition-normal);
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: 8px 16px;
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: var(--border-radius-md);
  backdrop-filter: blur(10px);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.user-info:hover {
  background-color: rgba(255, 255, 255, 0.25);
  transform: translateY(-1px);
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

.sidebar {
  background-color: #304156;
  overflow-x: hidden;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  transition: all var(--transition-normal);
}

.el-main {
  background-color: var(--bg-color);
  padding: var(--spacing-lg);
  overflow-y: auto;
  min-height: calc(100vh - 60px);
  transition: all var(--transition-normal);
}

.el-menu {
  border-right: none;
  height: 100%;
  transition: all var(--transition-normal);
}

:deep(.el-menu-item) {
  height: 56px;
  line-height: 56px;
  margin: 4px 8px;
  border-radius: var(--border-radius-md);
  transition: all var(--transition-normal);
}

:deep(.el-menu-item:hover) {
  background-color: rgba(64, 158, 255, 0.1) !important;
}

:deep(.el-menu-item.is-active) {
  background-color: var(--primary-color) !important;
  color: #fff !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .el-header {
    padding: 0 var(--spacing-sm);
  }
  
  .header-content h2 {
    font-size: 16px;
  }
  
  .user-info {
    padding: 6px 12px;
  }
  
  .el-main {
    padding: var(--spacing-md);
  }
  
  .sidebar {
    position: fixed;
    height: 100%;
    z-index: 999;
    transition: all var(--transition-normal);
  }
  
  .sidebar.collapsed {
    transform: translateX(-100%);
  }
}

@media (max-width: 480px) {
  .header-content h2 {
    font-size: 14px;
  }
  
  .user-info {
    padding: 4px 8px;
  }
  
  .el-main {
    padding: var(--spacing-sm);
  }
}
</style>
