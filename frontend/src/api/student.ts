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
