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

/**
 * 上传头像
 */
export const uploadAvatar = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/file/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}
