import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', requiresAuth: false },
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', requiresAuth: true },
      },
      // 学生端路由
      {
        path: 'student/application',
        name: 'StudentApplication',
        component: () => import('@/views/student/Application.vue'),
        meta: { title: '证书申请', requiresAuth: true, roles: [1] },
      },
      {
        path: 'student/my-applications',
        name: 'MyApplications',
        component: () => import('@/views/student/MyApplications.vue'),
        meta: { title: '我的申请', requiresAuth: true, roles: [1] },
      },
      {
        path: 'student/certificate-detail/:pkCa',
        name: 'CertificateDetail',
        component: () => import('@/views/student/CertificateDetail.vue'),
        meta: { title: '证明详情', requiresAuth: true, roles: [1] },
      },
      {
        path: 'student/profile',
        name: 'StudentProfile',
        component: () => import('@/views/student/Profile.vue'),
        meta: { title: '个人信息', requiresAuth: true, roles: [1] },
      },
      // 教师端路由
      {
        path: 'teacher/approval',
        name: 'TeacherApproval',
        component: () => import('@/views/teacher/Approval.vue'),
        meta: { title: '审批管理', requiresAuth: true, roles: [2] },
      },
      {
        path: 'teacher/applications',
        name: 'TeacherApplications',
        component: () => import('@/views/teacher/Applications.vue'),
        meta: { title: '申请列表', requiresAuth: true, roles: [2] },
      },
      {
        path: 'teacher/profile',
        name: 'TeacherProfile',
        component: () => import('@/views/teacher/Profile.vue'),
        meta: { title: '个人信息', requiresAuth: true, roles: [2] },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfoStr = localStorage.getItem('userInfo')
  
  // 设置页面标题
  document.title = `${to.meta.title || '毕业学术证书管理系统'}`
  
  // 需要登录的页面
  if (to.meta.requiresAuth) {
    if (!token) {
      ElMessage.warning('请先登录')
      next('/login')
      return
    }
    
    // 角色权限检查
    if (to.meta.roles && userInfoStr) {
      try {
        // 安全解析 userInfo
        if (userInfoStr === 'undefined' || userInfoStr === 'null' || !userInfoStr) {
          console.error('用户信息无效，请重新登录')
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          ElMessage.warning('用户信息已过期，请重新登录')
          next('/login')
          return
        }
        
        const user = JSON.parse(userInfoStr)
        console.log('解析后的用户信息:', user)  // 调试日志
        
        // 检查用户信息结构
        if (!user || typeof user !== 'object') {
          console.error('用户信息格式错误: 不是对象', user)
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          ElMessage.warning('用户信息异常，请重新登录')
          next('/login')
          return
        }
        
        // 检查 userType 字段
        if (user.userType === undefined || user.userType === null) {
          console.error('用户信息缺少 userType 字段', user)
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          ElMessage.warning('用户信息不完整，请重新登录')
          next('/login')
          return
        }
        
        if (!to.meta.roles.includes(user.userType)) {
          ElMessage.error('没有权限访问该页面')
          next(from.path || '/dashboard')
          return
        }
      } catch (error) {
        console.error('JSON 解析错误:', error)
        console.error('原始 userInfoStr:', userInfoStr)
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        ElMessage.warning('用户信息损坏，请重新登录')
        next('/login')
        return
      }
    }
  }
  
  // 已登录用户访问登录页，重定向到首页
  if (token && (to.path === '/login' || to.path === '/register')) {
    next('/dashboard')
    return
  }
  
  next()
})

export default router
