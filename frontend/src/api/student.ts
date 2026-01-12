import request from './request'

/**
 * 获取当前学生信息
 */
export const getCurrentStudent = () => {
  return request({
    url: '/student/current',
    method: 'get',
  })
}

/**
 * 更新学生信息
 */
export const updateStudent = (data: any) => {
  return request({
    url: '/student',
    method: 'put',
    data,
  })
}
