import request from './request'

// --- 证明模板管理 ---
export const getAdminTemplates = (params: any) => {
  return request({
    url: '/admin/templates',
    method: 'get',
    params
  })
}

export const createAdminTemplate = (data: any) => {
  return request({
    url: '/admin/templates',
    method: 'post',
    data
  })
}

export const updateAdminTemplate = (data: any) => {
  return request({
    url: '/admin/templates',
    method: 'put',
    data
  })
}

export const deleteAdminTemplate = (pkCt: string) => {
  return request({
    url: `/admin/templates/${pkCt}`,
    method: 'delete'
  })
}

// --- 学生管理 ---
export const getAdminStudents = (params: any) => {
  return request({
    url: '/admin/students',
    method: 'get',
    params
  })
}

// --- 教师管理 ---
export const getAdminTeachers = (params: any) => {
  return request({
    url: '/admin/teachers',
    method: 'get',
    params
  })
}

// --- 全站申请记录 ---
export const getAdminAllApplications = (params: any) => {
  return request({
    url: '/admin/applications',
    method: 'get',
    params
  })
}

// --- 统计数据 ---
export const getAdminStatistics = () => {
  return request({
    url: '/admin/statistics',
    method: 'get'
  })
}
