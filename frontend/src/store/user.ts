import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, logout as logoutApi, LoginRequest } from '@/api/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  
  // 安全地解析 userInfo
  const getUserInfoFromStorage = () => {
    try {
      const userInfoStr = localStorage.getItem('userInfo')
      if (userInfoStr && userInfoStr !== 'undefined' && userInfoStr !== 'null') {
        return JSON.parse(userInfoStr)
      }
    } catch (e) {
      console.error('Failed to parse userInfo from localStorage:', e)
      localStorage.removeItem('userInfo')
    }
    return null
  }
  
  const userInfo = ref<any>(getUserInfoFromStorage())

  // 登录
  const login = async (loginForm: LoginRequest) => {
    try {
      const res: any = await loginApi(loginForm)
      console.log('登录响应:', res)
      console.log('登录响应 data:', res.data)  // 查看完整的 data 对象
      
      if (res.code === 200 && res.data) {
        // 验证必需字段
        if (!res.data.token) {
          console.error('缺少 token:', res.data)
          ElMessage.error('登录响应缺少 token')
          return false
        }
        
        if (!res.data.userType) {
          console.error('缺少 userType:', res.data)
          ElMessage.error('登录响应缺少用户类型')
          return false
        }
        
        // 构建 userInfo 对象（后端直接返回在 data 中）
        const userInfoData = {
          userId: res.data.userId,
          username: res.data.username,
          realName: res.data.realName,
          userType: res.data.userType,
          studentId: res.data.studentId,
          teacherId: res.data.teacherId,
        }
        
        console.log('用户信息:', userInfoData)
        
        token.value = res.data.token
        userInfo.value = userInfoData
        
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('userInfo', JSON.stringify(userInfoData))
        
        console.log('已存储到 localStorage')
        
        ElMessage.success('登录成功')
        router.push('/dashboard')
        return true
      }
      
      ElMessage.error('登录失败')
      return false
    } catch (error) {
      console.error('Login error:', error)
      ElMessage.error('登录失败，请稍后重试')
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
