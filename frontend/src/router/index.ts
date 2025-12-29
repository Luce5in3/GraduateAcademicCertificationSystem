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
  const userInfo = localStorage.getItem('userInfo')
  
  // 设置页面标题
  document.title = `${to.meta.title || '研究生学术认证系统'}`
  
  // 需要登录的页面
  if (to.meta.requiresAuth) {
    if (!token) {
      ElMessage.warning('请先登录')
      next('/login')
      return
    }
    
    // 角色权限检查
    if (to.meta.roles && userInfo) {
      const user = JSON.parse(userInfo)
      if (!to.meta.roles.includes(user.userType)) {
        ElMessage.error('没有权限访问该页面')
        next(from.path)
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
