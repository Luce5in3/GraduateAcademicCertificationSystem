<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="welcome-text">
          <h1>欢迎回来,{{ userStore.userInfo?.username || '用户' }}!</h1>
          <p class="sub-text">{{ currentDate }} · {{ userTypeText }}</p>
        </div>
        <div class="banner-icon">
          <el-icon :size="80" color="#ffffff">
            <component :is="userStore.isStudent() ? DocumentIcon : CheckIcon" />
          </el-icon>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <template v-if="userStore.isStudent()">
        <el-card class="stat-card stat-card-primary" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">待审批申请</div>
              <div class="stat-value">3</div>
            </div>
            <el-icon :size="50" class="stat-icon">
              <Clock />
            </el-icon>
          </div>
        </el-card>

        <el-card class="stat-card stat-card-success" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">已通过</div>
              <div class="stat-value">5</div>
            </div>
            <el-icon :size="50" class="stat-icon">
              <CircleCheck />
            </el-icon>
          </div>
        </el-card>

        <el-card class="stat-card stat-card-warning" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">申请总数</div>
              <div class="stat-value">8</div>
            </div>
            <el-icon :size="50" class="stat-icon">
              <DocumentCopy />
            </el-icon>
          </div>
        </el-card>
      </template>

      <template v-if="userStore.isTeacher()">
        <el-card class="stat-card stat-card-danger" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">待审批</div>
              <div class="stat-value">12</div>
            </div>
            <el-icon :size="50" class="stat-icon">
              <Warning />
            </el-icon>
          </div>
        </el-card>

        <el-card class="stat-card stat-card-success" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">今日已审批</div>
              <div class="stat-value">7</div>
            </div>
            <el-icon :size="50" class="stat-icon">
              <Select />
            </el-icon>
          </div>
        </el-card>

        <el-card class="stat-card stat-card-info" shadow="hover">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">总审批数</div>
              <div class="stat-value">156</div>
            </div>
            <el-icon :size="50" class="stat-icon">
              <DataAnalysis />
            </el-icon>
          </div>
        </el-card>
      </template>
    </div>

    <!-- 个人信息卡片与快捷操作 -->
    <el-row :gutter="20" class="content-section">
      <el-col :span="8">
        <el-card class="profile-card-container" shadow="hover" @click="handleGoProfile">
          <template #header>
            <div class="card-header">
              <span>个人信息卡片</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </template>
          <div class="profile-card">
            <el-avatar :size="80" icon="UserFilled" class="profile-avatar" />
            <div class="profile-info">
              <h3>{{ userStore.userInfo?.username }}</h3>
              <div class="info-item">
                <el-icon><User /></el-icon>
                <span>{{ userTypeText }}</span>
              </div>
              <div class="info-item" v-if="userStore.isStudent()">
                <el-icon><Postcard /></el-icon>
                <span>学号：{{ userStore.userInfo?.studentId || '未设置' }}</span>
              </div>
              <div class="info-item" v-if="userStore.isTeacher()">
                <el-icon><Postcard /></el-icon>
                <span>工号：{{ userStore.userInfo?.teacherId || '未设置' }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions-grid">
            <template v-if="userStore.isStudent()">
              <div class="action-item" @click="router.push('/student/application')">
                <el-icon :size="32" color="#1677FF"><DocumentAdd /></el-icon>
                <div class="action-text">
                  <h4>提交证书申请</h4>
                  <p>快速提交新的证书申请</p>
                </div>
              </div>
              <div class="action-item" @click="router.push('/student/my-applications')">
                <el-icon :size="32" color="#67c23a"><List /></el-icon>
                <div class="action-text">
                  <h4>查看我的申请</h4>
                  <p>查看所有申请记录</p>
                </div>
              </div>
              <div class="action-item" @click="router.push('/student/profile')">
                <el-icon :size="32" color="#e6a23c"><User /></el-icon>
                <div class="action-text">
                  <h4>完善个人信息</h4>
                  <p>维护个人资料</p>
                </div>
              </div>
            </template>
            
            <template v-if="userStore.isTeacher()">
              <div class="action-item" @click="router.push('/teacher/approval')">
                <el-icon :size="32" color="#f56c6c"><Check /></el-icon>
                <div class="action-text">
                  <h4>待审批列表</h4>
                  <p>查看并处理待审批申请</p>
                </div>
              </div>
              <div class="action-item" @click="router.push('/teacher/applications')">
                <el-icon :size="32" color="#67c23a"><List /></el-icon>
                <div class="action-text">
                  <h4>查看所有申请</h4>
                  <p>浏览全部申请记录</p>
                </div>
              </div>
              <div class="action-item" @click="router.push('/teacher/profile')">
                <el-icon :size="32" color="#909399"><User /></el-icon>
                <div class="action-text">
                  <h4>个人信息管理</h4>
                  <p>更新个人信息</p>
                </div>
              </div>
            </template>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统公告与待办事项 -->
    <el-row :gutter="20" class="content-section">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统公告 / 提示</span>
            </div>
          </template>
          <div class="notice-list">
            <div class="notice-item">
              <el-icon color="#1677FF"><Bell /></el-icon>
              <span>系统将于本周六凌晨2点进行维护升级</span>
            </div>
            <div class="notice-item">
              <el-icon color="#67c23a"><Promotion /></el-icon>
              <span>新增在线证书打印功能，欢迎体验</span>
            </div>
            <div class="notice-item">
              <el-icon color="#e6a23c"><InfoFilled /></el-icon>
              <span>请及时完善个人信息以便审核</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>待办事项</span>
            </div>
          </template>
          <div class="todo-list">
            <template v-if="userStore.isStudent()">
              <div class="todo-item">
                <el-badge :value="3" class="todo-badge">
                  <el-icon :size="24" color="#e6a23c"><Clock /></el-icon>
                </el-badge>
                <div class="todo-text">
                  <h4>待审批申请</h4>
                  <p>您有3个申请正在等待审批</p>
                </div>
              </div>
              <div class="todo-item">
                <el-badge :value="5" class="todo-badge">
                  <el-icon :size="24" color="#67c23a"><CircleCheck /></el-icon>
                </el-badge>
                <div class="todo-text">
                  <h4>已通过申请</h4>
                  <p>您有5个申请已经通过审批</p>
                </div>
              </div>
            </template>
            <template v-if="userStore.isTeacher()">
              <div class="todo-item">
                <el-badge :value="12" class="todo-badge">
                  <el-icon :size="24" color="#f56c6c"><Warning /></el-icon>
                </el-badge>
                <div class="todo-text">
                  <h4>待审批申请</h4>
                  <p>12个申请等待您的审批</p>
                </div>
              </div>
              <div class="todo-item">
                <el-badge :value="7" class="todo-badge">
                  <el-icon :size="24" color="#67c23a"><Select /></el-icon>
                </el-badge>
                <div class="todo-text">
                  <h4>今日已审批</h4>
                  <p>今天已处理7个申请</p>
                </div>
              </div>
            </template>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近动态 -->
    <el-card class="content-section">
      <template #header>
        <div class="card-header">
          <span>最近动态</span>
        </div>
      </template>
      <el-timeline>
        <el-timeline-item timestamp="2025-12-30 10:30" placement="top">
          <el-card>
            <p>您的学术成果证明申请已通过审核</p>
          </el-card>
        </el-timeline-item>
        <el-timeline-item timestamp="2025-12-29 14:20" placement="top">
          <el-card>
            <p>您提交了新的证书申请</p>
          </el-card>
        </el-timeline-item>
        <el-timeline-item timestamp="2025-12-28 09:15" placement="top">
          <el-card>
            <p>系统更新：新增批量导出功能</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { 
  DocumentAdd, 
  List, 
  User, 
  Check,
  Clock,
  CircleCheck,
  DocumentCopy,
  Warning,
  Select,
  DataAnalysis,
  Bell,
  Promotion,
  InfoFilled,
  Document as DocumentIcon,
  Postcard,
  ArrowRight
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const CheckIcon = Check

const userTypeText = computed(() => {
  const type = userStore.getUserType()
  return type === 1 ? '学生' : type === 2 ? '教师' : '未知'
})

const currentDate = computed(() => {
  const now = new Date()
  const options: Intl.DateTimeFormatOptions = { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    weekday: 'long'
  }
  return now.toLocaleDateString('zh-CN', options)
})

const handleGoProfile = () => {
  const profilePath = userStore.isStudent() ? '/student/profile' : '/teacher/profile'
  router.push(profilePath)
}
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
  margin: 0 auto;
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #1677FF 0%, #0050B3 100%);
  border-radius: 12px;
  padding: 40px;
  margin-bottom: 24px;
  color: white;
  box-shadow: 0 4px 12px rgba(22, 119, 255, 0.2);
}

.banner-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text h1 {
  margin: 0 0 10px 0;
  font-size: 32px;
  font-weight: 600;
}

.sub-text {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.banner-icon {
  opacity: 0.3;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 12px;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 36px;
  font-weight: 600;
  color: #303133;
}

.stat-icon {
  opacity: 0.2;
}

.stat-card-primary .stat-value {
  color: #1677FF;
}

.stat-card-success .stat-value {
  color: #67c23a;
}

.stat-card-warning .stat-value {
  color: #e6a23c;
}

.stat-card-danger .stat-value {
  color: #f56c6c;
}

.stat-card-info .stat-value {
  color: #909399;
}

/* 个人信息卡片 */
.profile-card-container {
  cursor: pointer;
  transition: all 0.3s;
}

.profile-card-container:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(22, 119, 255, 0.15) !important;
}

.profile-card-container .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.profile-avatar {
  margin-bottom: 16px;
  background: linear-gradient(135deg, #1677FF 0%, #0050B3 100%);
}

.profile-info {
  text-align: center;
  width: 100%;
}

.profile-info h3 {
  margin: 0 0 16px 0;
  color: #303133;
  font-size: 18px;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 8px;
  color: #606266;
  font-size: 14px;
}

/* 快捷操作网格 */
.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #F5F7FA;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.action-item:hover {
  background: #fff;
  border-color: #1677FF;
  box-shadow: 0 4px 12px rgba(22, 119, 255, 0.15);
  transform: translateY(-2px);
}

.action-text h4 {
  margin: 0 0 4px 0;
  color: #303133;
  font-size: 15px;
}

.action-text p {
  margin: 0;
  color: #909399;
  font-size: 13px;
}

/* 待办事项 */
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #F5F7FA;
  border-radius: 8px;
  transition: all 0.3s;
}

.todo-item:hover {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.todo-badge {
  margin-right: 8px;
}

.todo-text h4 {
  margin: 0 0 4px 0;
  color: #303133;
  font-size: 14px;
}

.todo-text p {
  margin: 0;
  color: #909399;
  font-size: 13px;
}

/* 内容区域 */
.content-section {
  margin-bottom: 24px;
}

.card-header {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

/* 公告列表 */
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.notice-item:hover {
  background-color: #e9ecf1;
}

.notice-item span {
  color: #606266;
  font-size: 14px;
}

/* 时间轴 */
:deep(.el-timeline-item__timestamp) {
  color: #909399;
  font-size: 13px;
}

:deep(.el-timeline-item__content) {
  color: #606266;
}

/* 响应式 */
@media (max-width: 768px) {
  .welcome-banner {
    padding: 24px;
  }
  
  .banner-content {
    flex-direction: column;
    text-align: center;
  }
  
  .banner-icon {
    margin-top: 20px;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
}</style>
