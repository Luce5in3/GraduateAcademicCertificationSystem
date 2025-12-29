import request from './request'

// 登录请求参数
export interface LoginRequest {
  username: string
  password: string
}

// 登录响应
export interface LoginResponse {
  token: string
  userInfo: {
    id: string
    username: string
    userType: number
  }
}

// 注册请求参数
export interface RegisterRequest {
  username: string
  password: string
  userType: number
  studentId?: string
  teacherId?: string
}

// 登录
export const login = (data: LoginRequest) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data,
  })
}

// 注册
export const register = (data: RegisterRequest) => {
  return request({
    url: '/auth/register',
    method: 'post',
    data,
  })
}

// 登出
export const logout = () => {
  return request({
    url: '/auth/logout',
    method: 'post',
  })
}
