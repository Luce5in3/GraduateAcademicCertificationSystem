import request from './request'

// --- 申请反馈 ---
export const getMyFeedbacks = (params: any) => {
  return request({ url: '/feedback/my', method: 'get', params })
}

export const getFeedbackByApplication = (pkCa: string, params: any) => {
  return request({ url: `/feedback/application/${pkCa}`, method: 'get', params })
}

export const createFeedback = (data: any) => {
  return request({ url: '/feedback', method: 'post', data })
}

export const getPendingFeedbacks = (params: any) => {
  return request({ url: '/feedback/pending', method: 'get', params })
}

export const replyFeedback = (pkFeedback: string, replyContent: string) => {
  return request({ url: `/feedback/${pkFeedback}/reply`, method: 'put', data: replyContent, headers: { 'Content-Type': 'text/plain' } })
}

export const closeFeedback = (pkFeedback: string) => {
  return request({ url: `/feedback/${pkFeedback}/close`, method: 'put' })
}
