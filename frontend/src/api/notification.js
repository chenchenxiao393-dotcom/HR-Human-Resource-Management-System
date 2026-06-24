import request from '@/utils/request'

export const getNotifications = (employeeId) => {
  return request({
    url: '/notification',
    method: 'get',
    params: { employeeId }
  })
}

export const getAllNotifications = () => {
  return request({
    url: '/notification/all',
    method: 'get'
  })
}

export const getUnreadNotifications = (employeeId) => {
  return request({
    url: '/notification/unread',
    method: 'get',
    params: { employeeId }
  })
}

export const getUnreadCount = (employeeId) => {
  return request({
    url: '/notification/unread/count',
    method: 'get',
    params: { employeeId }
  })
}

export const markNotificationAsRead = (id) => {
  return request({
    url: `/notification/${id}/read`,
    method: 'put'
  })
}

export const sendNotification = (data) => {
  return request({
    url: '/notification/send',
    method: 'post',
    data
  })
}