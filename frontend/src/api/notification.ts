import request from './request'

// --- 消息通知 ---
export const getNotifications = (params: any) => {
  return request({ url: '/notification', method: 'get', params })
}

export const getUnreadNotificationCount = () => {
  return request({ url: '/notification/unread-count', method: 'get' })
}

export const markNotificationRead = (pkNotification: string) => {
  return request({ url: `/notification/${pkNotification}/read`, method: 'put' })
}

export const markAllNotificationsRead = () => {
  return request({ url: '/notification/read-all', method: 'put' })
}

// --- 系统公告 ---
export const getAnnouncements = (params: any) => {
  return request({ url: '/announcement', method: 'get', params })
}

export const getUnreadAnnouncementCount = () => {
  return request({ url: '/announcement/unread-count', method: 'get' })
}

// --- 管理员公告管理 ---
export const getAdminAnnouncements = (params: any) => {
  return request({ url: '/announcement/admin', method: 'get', params })
}

export const getAdminAnnouncementDetail = (pkAnnouncement: string) => {
  return request({ url: `/announcement/admin/${pkAnnouncement}`, method: 'get' })
}

export const createAnnouncement = (data: any) => {
  return request({ url: '/announcement/admin', method: 'post', data })
}

export const updateAnnouncement = (data: any) => {
  return request({ url: '/announcement/admin', method: 'put', data })
}

export const deleteAnnouncement = (pkAnnouncement: string) => {
  return request({ url: `/announcement/admin/${pkAnnouncement}`, method: 'delete' })
}

export const publishAnnouncement = (pkAnnouncement: string) => {
  return request({ url: `/announcement/admin/${pkAnnouncement}/publish`, method: 'put' })
}

export const revokeAnnouncement = (pkAnnouncement: string) => {
  return request({ url: `/announcement/admin/${pkAnnouncement}/revoke`, method: 'put' })
}
