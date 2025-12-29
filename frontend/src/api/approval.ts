import request from './request'

// 审批相关接口

export interface ApprovalRequest {
  applicationId: string
  result: number
  comment?: string
}

// 提交审批
export const submitApproval = (data: ApprovalRequest) => {
  return request({
    url: '/approval/submit',
    method: 'post',
    data,
  })
}

// 获取待审批列表
export const getPendingApprovals = (params?: any) => {
  return request({
    url: '/approval/pending',
    method: 'get',
    params,
  })
}

// 获取审批记录
export const getApprovalRecords = (applicationId: string) => {
  return request({
    url: `/approval/records/${applicationId}`,
    method: 'get',
  })
}
