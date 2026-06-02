import request from './request'

// --- 学院管理 ---
export const getColleges = (params: any) => {
  return request({ url: '/admin/colleges', method: 'get', params })
}

export const getActiveColleges = () => {
  return request({ url: '/admin/colleges/active', method: 'get' })
}

export const createCollege = (data: any) => {
  return request({ url: '/admin/colleges', method: 'post', data })
}

export const updateCollege = (data: any) => {
  return request({ url: '/admin/colleges', method: 'put', data })
}

export const deleteCollege = (pkCollege: string) => {
  return request({ url: `/admin/colleges/${pkCollege}`, method: 'delete' })
}

// --- 部门管理 ---
export const getDepartments = (params: any) => {
  return request({ url: '/admin/departments', method: 'get', params })
}

export const getDepartmentsByCollege = (pkCollege: string) => {
  return request({ url: `/admin/departments/college/${pkCollege}`, method: 'get' })
}

export const createDepartment = (data: any) => {
  return request({ url: '/admin/departments', method: 'post', data })
}

export const updateDepartment = (data: any) => {
  return request({ url: '/admin/departments', method: 'put', data })
}

export const deleteDepartment = (pkDepartment: string) => {
  return request({ url: `/admin/departments/${pkDepartment}`, method: 'delete' })
}
