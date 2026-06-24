import request from '@/utils/request'

export function getAttendanceList(params) {
  return request({
    url: '/attendance',
    method: 'get',
    params
  })
}

export function getMyAttendance(params) {
  return request({
    url: '/attendance/my',
    method: 'get',
    params
  })
}

export function exportAttendance(params) {
  return request({
    url: '/attendance/export',
    method: 'get',
    params
  })
}

export function getAttendanceById(id) {
  return request({
    url: `/attendance/${id}`,
    method: 'get'
  })
}

export function createAttendance(data) {
  return request({
    url: '/attendance',
    method: 'post',
    data
  })
}

export function updateAttendance(id, data) {
  return request({
    url: `/attendance/${id}`,
    method: 'put',
    data
  })
}

export function deleteAttendance(id) {
  return request({
    url: `/attendance/${id}`,
    method: 'delete'
  })
}

export function checkIn(data) {
  return request({
    url: '/attendance/check-in',
    method: 'post',
    data
  })
}

export function checkOut(data) {
  return request({
    url: '/attendance/check-out',
    method: 'post',
    data
  })
}

export function getAttendanceStatistics(params) {
  return request({
    url: '/attendance/statistics',
    method: 'get',
    params
  })
}
