import request from './request'

// 审批相关接口

export interface ApprovalRequest {
  pkCa: string              // 申请主键
  approvalResult: number    // 审批结果：1-通过 2-拒绝 3-退回
  approvalOpinion?: string  // 审批意见
  attachmentUrl?: string    // 附件URL
}

// 提交审批
export const submitApproval = (data: ApprovalRequest) => {
  return request({
    url: '/approval',
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

// 获取审批记录
export const getApprovalRecords = (applicationId: string) => {
  return request({
    url: `/approval/records/${applicationId}`,
    method: 'get',
  })
}
