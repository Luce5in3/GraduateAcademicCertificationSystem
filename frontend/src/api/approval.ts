import request from './request'

// 审批相关接口

export interface ApprovalRequest {
  pkCa: string              // 申请主键
  approvalResult: number    // 审批结果：1-通过 2-拒绝 3-退回
  approvalOpinion?: string  // 审批意见
  attachmentUrl?: string    // 附件URL
}

export interface BatchApprovalRequest {
  pkCaList: string[]        // 申请ID列表
  approvalResult: number    // 审批结果：1-通过 2-拒绝 3-退回
  approvalOpinion?: string  // 审批意见
}

// 提交审批
export const submitApproval = (data: ApprovalRequest) => {
  return request({
    url: '/approval',
    method: 'post',
    data,
  })
}

// 批量审批
export const batchApproval = (data: BatchApprovalRequest) => {
  return request({
    url: '/approval/batch',
    method: 'post',
    data,
  })
}

// 获取待审批列表
export const getPendingApprovals = (params?: any) => {
  return request({
    url: '/application/pending',
    method: 'get',
    params,
  })
}

// 获取审批历史（当前教师的）
export const getApprovalHistory = (params?: { current?: number; size?: number }) => {
  return request({
    url: '/approval/history',
    method: 'get',
    params,
  })
}

// 获取某条申请的审批记录
export const getApprovalRecords = (pkCa: string) => {
  return request({
    url: `/approval/records/${pkCa}`,
    method: 'get',
  })
}
