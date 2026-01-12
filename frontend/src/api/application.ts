import request from './request'

// 证书申请相关接口

export interface ApplicationRequest {
  certificateType: string
  reason?: string
}

export interface ApplicationResponse {
  id: string
  studentId: string
  studentName: string
  certificateType: string
  status: number
  reason?: string
  createTime: string
}

// 提交证书申请
export const submitApplication = (data: any) => {
  return request({
    url: '/application',
    method: 'post',
    data,
  })
}

// 获取我的申请列表
export const getMyApplications = (params?: any) => {
  return request({
    url: '/application/my',
    method: 'get',
    params,
  })
}

// 获取申请详情
export const getApplicationDetail = (id: string) => {
  return request({
    url: `/application/${id}`,
    method: 'get',
  })
}

// 获取证明详情（包含学生信息和模板信息）
export const getCertificateDetail = (id: string) => {
  return request({
    url: `/application/${id}/detail`,
    method: 'get',
  })
}

// 撤销申请
export const cancelApplication = (id: string) => {
  return request({
    url: `/application/${id}/cancel`,
    method: 'put',
  })
}

// 获取待审批列表（教师用）
export const getPendingApplications = (params?: any) => {
  return request({
    url: '/application/pending',
    method: 'get',
    params,
  })
}

// 获取所有申请列表（教师用）
export const getAllApplications = (params?: any) => {
  return request({
    url: '/application/my',  // 暂时使用同一个接口
    method: 'get',
    params,
  })
}
