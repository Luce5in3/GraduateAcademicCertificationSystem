import request from './request'

/**
 * 获取当前教师信息
 */
export const getCurrentTeacher = () => {
  return request({
    url: '/teacher/current',
    method: 'get',
  })
}

/**
 * 更新教师信息
 */
export const updateTeacher = (data: any) => {
  return request({
    url: '/teacher',
    method: 'put',
    data,
  })
}
