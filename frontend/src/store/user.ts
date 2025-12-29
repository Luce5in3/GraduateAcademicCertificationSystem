import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, logout as logoutApi, LoginRequest } from '@/api/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<any>(
    localStorage.getItem('userInfo') 
      ? JSON.parse(localStorage.getItem('userInfo')!) 
      : null
  )

  // 登录
  const login = async (loginForm: LoginRequest) => {
    try {
      const res: any = await loginApi(loginForm)
      if (res.code === 200) {
        token.value = res.data.token
        userInfo.value = res.data.userInfo
        
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
        
        ElMessage.success('登录成功')
        router.push('/dashboard')
        return true
      }
      return false
    } catch (error) {
      console.error('Login error:', error)
      return false
    }
  }

  // 登出
  const logout = async () => {
    try {
      await logoutApi()
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    }
  }

  // 获取用户类型
  const getUserType = () => {
    return userInfo.value?.userType || 0
  }

  // 判断是否为学生
  const isStudent = () => {
    return userInfo.value?.userType === 1
  }

  // 判断是否为教师
  const isTeacher = () => {
    return userInfo.value?.userType === 2
  }

  return {
    token,
    userInfo,
    login,
    logout,
    getUserType,
    isStudent,
    isTeacher,
  }
})
