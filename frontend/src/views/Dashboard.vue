<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner" :class="{ 'banner-enter': true }">
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
      <template v-if="userStore.isAdmin()">
        <el-card class="stat-card stat-card-primary" shadow="hover" v-loading="loading" :class="{ 'card-enter': true }">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">全站申请总数</div>
              <div class="stat-value">{{ statistics.totalCount || 0 }}</div>
              <div class="stat-trend" v-if="statistics.totalCount > 0">
                <el-icon :size="16" color="var(--success-color)"><ArrowUp /></el-icon>
                <span>较上月增长 12%</span>
              </div>
            </div>
            <el-icon :size="50" class="stat-icon">
              <Files />
            </el-icon>
          </div>
        </el-card>

        <el-card class="stat-card stat-card-success" shadow="hover" v-loading="loading" :class="{ 'card-enter': true }">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">已通过证明</div>
              <div class="stat-value">{{ statistics.approvedCount || 0 }}</div>
              <div class="stat-trend" v-if="statistics.approvedCount > 0">
                <el-icon :size="16" color="var(--success-color)"><ArrowUp /></el-icon>
                <span>通过率 85%</span>
              </div>
            </div>
            <el-icon :size="50" class="stat-icon">
              <CircleCheck />
            </el-icon>
          </div>
        </el-card>

        <el-card class="stat-card stat-card-warning" shadow="hover" v-loading="loading" :class="{ 'card-enter': true }">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">待审批总数</div>
              <div class="stat-value">{{ statistics.pendingCount || 0 }}</div>
              <div class="stat-trend" v-if="statistics.pendingCount > 0">
                <el-icon :size="16" color="var(--warning-color)"><Clock /></el-icon>
                <span>需及时处理</span>
              </div>
            </div>
            <el-icon :size="50" class="stat-icon">
              <Clock />
            </el-icon>
          </div>
        </el-card>
      </template>

      <template v-if="userStore.isStudent()">
        <el-card class="stat-card stat-card-primary" shadow="hover" v-loading="loading" :class="{ 'card-enter': true }">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">待审批申请</div>
              <div class="stat-value">{{ statistics.pendingCount || 0 }}</div>
              <div class="stat-trend" v-if="statistics.pendingCount > 0">
                <el-icon :size="16" color="var(--warning-color)"><Clock /></el-icon>
                <span>处理中</span>
              </div>
            </div>
            <el-icon :size="50" class="stat-icon">
              <Clock />
            </el-icon>
          </div>
        </el-card>

        <el-card class="stat-card stat-card-success" shadow="hover" v-loading="loading" :class="{ 'card-enter': true }">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">已通过</div>
              <div class="stat-value">{{ statistics.approvedCount || 0 }}</div>
              <div class="stat-trend" v-if="statistics.approvedCount > 0">
                <el-icon :size="16" color="var(--success-color)"><CircleCheck /></el-icon>
                <span>全部通过</span>
              </div>
            </div>
            <el-icon :size="50" class="stat-icon">
              <CircleCheck />
            </el-icon>
          </div>
        </el-card>

        <el-card class="stat-card stat-card-warning" shadow="hover" v-loading="loading" :class="{ 'card-enter': true }">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">申请总数</div>
              <div class="stat-value">{{ statistics.totalCount || 0 }}</div>
              <div class="stat-trend" v-if="statistics.totalCount > 0">
                <el-icon :size="16" color="var(--primary-color)"><DocumentCopy /></el-icon>
                <span>总计</span>
              </div>
            </div>
            <el-icon :size="50" class="stat-icon">
              <DocumentCopy />
            </el-icon>
          </div>
        </el-card>
      </template>

      <template v-if="userStore.isTeacher()">
        <el-card class="stat-card stat-card-danger" shadow="hover" v-loading="loading" :class="{ 'card-enter': true }">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">待审批</div>
              <div class="stat-value">{{ statistics.pendingCount || 0 }}</div>
              <div class="stat-trend" v-if="statistics.pendingCount > 0">
                <el-icon :size="16" color="var(--danger-color)"><Warning /></el-icon>
                <span>紧急处理</span>
              </div>
            </div>
            <el-icon :size="50" class="stat-icon">
              <Warning />
            </el-icon>
          </div>
        </el-card>

        <el-card class="stat-card stat-card-success" shadow="hover" v-loading="loading" :class="{ 'card-enter': true }">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">今日已审批</div>
              <div class="stat-value">{{ statistics.todayApprovedCount || 0 }}</div>
              <div class="stat-trend" v-if="statistics.todayApprovedCount > 0">
                <el-icon :size="16" color="var(--success-color)"><Check /></el-icon>
                <span>今日完成</span>
              </div>
            </div>
            <el-icon :size="50" class="stat-icon">
              <Select />
            </el-icon>
          </div>
        </el-card>

        <el-card class="stat-card stat-card-info" shadow="hover" v-loading="loading" :class="{ 'card-enter': true }">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">总审批数</div>
              <div class="stat-value">{{ statistics.totalApprovalCount || 0 }}</div>
              <div class="stat-trend" v-if="statistics.totalApprovalCount > 0">
                <el-icon :size="16" color="var(--info-color)"><DataAnalysis /></el-icon>
                <span>总计</span>
              </div>
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
        <el-card class="profile-card-container" shadow="hover" @click="handleGoProfile" :class="{ 'card-enter': true }">
          <template #header>
            <div class="card-header">
              <span>个人信息卡片</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </template>
          <div class="profile-card">
            <el-avatar :size="80" icon="UserFilled" class="profile-avatar" />
            <div class="profile-info">
              <h3>{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</h3>
              <div class="info-item">
                <el-icon><User /></el-icon>
                <span>{{ userTypeText }}</span>
              </div>
              <div class="info-item" v-if="userStore.isStudent()">
                <el-icon><Postcard /></el-icon>
                <span>学号：{{ userStore.userInfo?.username || '未设置' }}</span>
              </div>
              <div class="info-item" v-if="userStore.isTeacher()">
                <el-icon><Postcard /></el-icon>
                <span>工号：{{ userStore.userInfo?.username || '未设置' }}</span>
              </div>
              <div class="info-item" v-if="userStore.isAdmin()">
                <el-icon><Key /></el-icon>
                <span>管理员权限</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card :class="{ 'card-enter': true }">
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions-grid">
            <template v-if="userStore.isStudent()">
              <div class="action-item" @click="router.push('/student/application')" :class="{ 'action-enter': true }">
                <el-icon :size="32" color="var(--primary-color)"><DocumentAdd /></el-icon>
                <div class="action-text">
                  <h4>提交证书申请</h4>
                  <p>快速提交新的证书申请</p>
                </div>
              </div>
              <div class="action-item" @click="router.push('/student/my-applications')" :class="{ 'action-enter': true }">
                <el-icon :size="32" color="var(--success-color)"><List /></el-icon>
                <div class="action-text">
                  <h4>查看我的申请</h4>
                  <p>查看所有申请记录</p>
                </div>
              </div>
              <div class="action-item" @click="router.push('/student/profile')" :class="{ 'action-enter': true }">
                <el-icon :size="32" color="var(--warning-color)"><User /></el-icon>
                <div class="action-text">
                  <h4>完善个人信息</h4>
                  <p>维护个人资料</p>
                </div>
              </div>
            </template>
            
            <template v-if="userStore.isTeacher()">
              <div class="action-item" @click="router.push('/teacher/approval')" :class="{ 'action-enter': true }">
                <el-icon :size="32" color="var(--danger-color)"><Check /></el-icon>
                <div class="action-text">
                  <h4>待审批列表</h4>
                  <p>查看并处理待审批申请</p>
                </div>
              </div>
              <div class="action-item" @click="router.push('/teacher/applications')" :class="{ 'action-enter': true }">
                <el-icon :size="32" color="var(--success-color)"><List /></el-icon>
                <div class="action-text">
                  <h4>查看所有申请</h4>
                  <p>浏览全部申请记录</p>
                </div>
              </div>
              <div class="action-item" @click="router.push('/teacher/profile')" :class="{ 'action-enter': true }">
                <el-icon :size="32" color="var(--info-color)"><User /></el-icon>
                <div class="action-text">
                  <h4>个人信息管理</h4>
                  <p>更新个人信息</p>
                </div>
              </div>
            </template>

            <template v-if="userStore.isAdmin()">
              <div class="action-item" @click="router.push('/admin/templates')" :class="{ 'action-enter': true }">
                <el-icon :size="32" color="var(--primary-color)"><DocumentCopy /></el-icon>
                <div class="action-text">
                  <h4>证明模板管理</h4>
                  <p>维护系统证明模板</p>
                </div>
              </div>
              <div class="action-item" @click="router.push('/admin/students')" :class="{ 'action-enter': true }">
                <el-icon :size="32" color="var(--success-color)"><User /></el-icon>
                <div class="action-text">
                  <h4>学生信息管理</h4>
                  <p>查看全站学生资料</p>
                </div>
              </div>
              <div class="action-item" @click="router.push('/admin/applications')" :class="{ 'action-enter': true }">
                <el-icon :size="32" color="var(--danger-color)"><Files /></el-icon>
                <div class="action-text">
                  <h4>全站申请记录</h4>
                  <p>查看所有证明申请</p>
                </div>
              </div>
            </template>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动 -->
    <el-card class="recent-activities" :class="{ 'card-enter': true }">
      <template #header>
        <div class="card-header">
          <span>最近活动</span>
        </div>
      </template>
      <div class="activity-list">
        <div class="activity-item" v-for="(activity, index) in recentActivities" :key="index">
          <div class="activity-icon" :class="activity.type">
            <el-icon :size="20"><component :is="activity.icon" /></el-icon>
          </div>
          <div class="activity-content">
            <div class="activity-title">{{ activity.title }}</div>
            <div class="activity-time">{{ activity.time }}</div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getStudentStatistics, getTeacherStatistics, type StatisticsResponse } from '@/api/application'
import { getAdminStatistics } from '@/api/admin'
import { ElMessage } from 'element-plus'
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
  Document as DocumentIcon,
  Postcard,
  ArrowRight,
  Files,
  Operation,
  Key,
  ArrowUp
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const CheckIcon = Check

// 统计数据
const statistics = ref<StatisticsResponse>({})
const loading = ref(false)

// 最近活动
const recentActivities = ref([
  {
    title: '系统已更新至最新版本',
    time: '今天 09:30',
    type: 'info',
    icon: DataAnalysis
  },
  {
    title: '您的账户安全状态良好',
    time: '昨天 16:45',
    type: 'success',
    icon: CircleCheck
  },
  {
    title: '新的证明模板已添加',
    time: '2天前',
    type: 'primary',
    icon: DocumentAdd
  }
])

const userTypeText = computed(() => {
  const type = userStore.getUserType()
  if (type === 3) return '管理员'
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
  if (userStore.isAdmin()) return
  const profilePath = userStore.isStudent() ? '/student/profile' : '/teacher/profile'
  router.push(profilePath)
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    loading.value = true
    let response: any
    
    if (userStore.isStudent()) {
      response = await getStudentStatistics()
    } else if (userStore.isTeacher()) {
      response = await getTeacherStatistics()
    } else if (userStore.isAdmin()) {
      response = await getAdminStatistics()
    } else {
      return
    }
    
    if (response.code === 200 && response.data) {
      statistics.value = response.data
    }
  } catch (error: any) {
    console.error('加载统计数据失败:', error)
    ElMessage.error(error.message || '加载统计数据失败')
  } finally {
    loading.value = false
  }
}

// 组件加载时获取统计数据
onMounted(() => {
  loadStatistics()
  
  // 添加动画效果
  setTimeout(() => {
    const cards = document.querySelectorAll('.card-enter')
    cards.forEach((card, index) => {
      setTimeout(() => {
        (card as HTMLElement).style.opacity = '1'
        (card as HTMLElement).style.transform = 'translateY(0)'
      }, 100 * (index + 1))
    })
    
    const actions = document.querySelectorAll('.action-enter')
    actions.forEach((action, index) => {
      setTimeout(() => {
        (action as HTMLElement).style.opacity = '1'
        (action as HTMLElement).style.transform = 'translateY(0)'
      }, 200 * (index + 1))
    })
  }, 100)
})
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--spacing-lg);
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-active) 100%);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-xl);
  margin-bottom: var(--spacing-lg);
  color: white;
  box-shadow: var(--shadow-md);
  transform: translateY(20px);
  opacity: 0;
  animation: bannerEnter 0.8s ease-out forwards;
}

@keyframes bannerEnter {
  to {
    transform: translateY(0);
    opacity: 1;
  }
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
  animation: textFadeIn 1s ease-out forwards;
}

.sub-text {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
  animation: textFadeIn 1s ease-out 0.3s forwards;
  opacity: 0;
}

@keyframes textFadeIn {
  to {
    opacity: 1;
  }
}

.banner-icon {
  opacity: 0.3;
  animation: iconFloat 3s ease-in-out infinite;
}

@keyframes iconFloat {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-10px) rotate(5deg);
  }
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.stat-card {
  border-radius: var(--border-radius-md);
  transition: all var(--transition-normal);
  transform: translateY(20px);
  opacity: 0;
  animation: cardEnter 0.6s ease-out forwards;
}

@keyframes cardEnter {
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-md);
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md);
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: var(--text-color-light);
  margin-bottom: 8px;
}

.stat-value {
  font-size: 36px;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 4px;
  animation: valueCountUp 1s ease-out;
}

@keyframes valueCountUp {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--text-color-secondary);
}

.stat-icon {
  opacity: 0.2;
  transition: transform var(--transition-normal);
}

.stat-card:hover .stat-icon {
  transform: scale(1.1);
}

.stat-card-primary .stat-value {
  color: var(--primary-color);
}

.stat-card-success .stat-value {
  color: var(--success-color);
}

.stat-card-warning .stat-value {
  color: var(--warning-color);
}

.stat-card-danger .stat-value {
  color: var(--danger-color);
}

.stat-card-info .stat-value {
  color: var(--info-color);
}

/* 个人信息卡片 */
.profile-card-container {
  cursor: pointer;
  transition: all var(--transition-normal);
  transform: translateY(20px);
  opacity: 0;
  animation: cardEnter 0.6s ease-out forwards;
  animation-delay: 0.2s;
}

.profile-card-container:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md) !important;
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
  padding: var(--spacing-lg) 0;
}

.profile-avatar {
  margin-bottom: var(--spacing-md);
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-active) 100%);
  transition: transform var(--transition-normal);
}

.profile-card-container:hover .profile-avatar {
  transform: scale(1.05);
}

.profile-info {
  text-align: center;
  width: 100%;
}

.profile-info h3 {
  margin: 0 0 var(--spacing-md) 0;
  color: var(--text-color);
  font-size: 18px;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 8px;
  color: var(--text-color-secondary);
  font-size: 14px;
  transition: color var(--transition-fast);
}

.profile-card-container:hover .info-item {
  color: var(--primary-color);
}

/* 快捷操作网格 */
.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-md);
}

.action-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--bg-color);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: all var(--transition-normal);
  border: 2px solid transparent;
  transform: translateY(20px);
  opacity: 0;
  animation: actionEnter 0.6s ease-out forwards;
}

@keyframes actionEnter {
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.action-item:hover {
  background: var(--card-bg);
  border-color: var(--primary-color);
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.action-item:hover .action-text h4 {
  color: var(--primary-color);
}

.action-text h4 {
  margin: 0 0 4px 0;
  color: var(--text-color);
  font-size: 15px;
  transition: color var(--transition-fast);
}

.action-text p {
  margin: 0;
  color: var(--text-color-light);
  font-size: 13px;
}

/* 最近活动 */
.recent-activities {
  margin-top: var(--spacing-lg);
  transform: translateY(20px);
  opacity: 0;
  animation: cardEnter 0.6s ease-out forwards;
  animation-delay: 0.4s;
}

.activity-list {
  padding: var(--spacing-md) 0;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-md);
  padding: var(--spacing-md) 0;
  border-bottom: 1px solid var(--border-color-light);
  transition: all var(--transition-fast);
}

.activity-item:hover {
  background-color: rgba(22, 119, 255, 0.05);
  padding-left: var(--spacing-md);
  border-radius: var(--border-radius-sm);
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 2px;
}

.activity-icon.primary {
  background-color: rgba(22, 119, 255, 0.1);
  color: var(--primary-color);
}

.activity-icon.success {
  background-color: rgba(103, 194, 58, 0.1);
  color: var(--success-color);
}

.activity-icon.info {
  background-color: rgba(144, 147, 153, 0.1);
  color: var(--info-color);
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-size: 14px;
  color: var(--text-color);
  margin-bottom: 4px;
}

.activity-time {
  font-size: 12px;
  color: var(--text-color-light);
}

/* 内容区域 */
.content-section {
  margin-bottom: var(--spacing-lg);
}

.card-header {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .dashboard {
    padding: var(--spacing-md);
  }
  
  .welcome-banner {
    padding: var(--spacing-lg);
  }
  
  .banner-content {
    flex-direction: column;
    text-align: center;
  }
  
  .banner-icon {
    margin-top: var(--spacing-md);
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .el-col {
    width: 100% !important;
  }
  
  .quick-actions-grid {
    grid-template-columns: 1fr;
  }
}
</style>
