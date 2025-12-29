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
export const submitApplication = (data: ApplicationRequest) => {
  return request({
    url: '/certificate-application/submit',
    method: 'post',
    data,
  })
}

// 获取我的申请列表
export const getMyApplications = (params?: any) => {
  return request({
    url: '/certificate-application/my-list',
    method: 'get',
    params,
  })
}

// 获取申请详情
export const getApplicationDetail = (id: string) => {
  return request({
    url: `/certificate-application/${id}`,
    method: 'get',
  })
}

// 撤销申请
export const cancelApplication = (id: string) => {
  return request({
    url: `/certificate-application/${id}/cancel`,
    method: 'put',
  })
}

// 获取所有申请列表（管理员/教师）
export const getAllApplications = (params?: any) => {
  return request({
    url: '/certificate-application/list',
    method: 'get',
    params,
  })
}
