import request from '@/utils/request'

export function getOvertimeList(params) {
  return request({
    url: '/overtime',
    method: 'get',
    params
  })
}

export function getMyOvertimeList(params) {
  return request({
    url: '/overtime/my',
    method: 'get',
    params
  })
}

export function getOvertimeById(id) {
  return request({
    url: `/overtime/${id}`,
    method: 'get'
  })
}

export function createOvertime(data) {
  return request({
    url: '/overtime',
    method: 'post',
    data
  })
}

export function updateOvertime(id, data) {
  return request({
    url: `/overtime/${id}`,
    method: 'put',
    data
  })
}

export function approveOvertime(id, data) {
  return request({
    url: `/overtime/${id}/approve`,
    method: 'put',
    data
  })
}

export function deleteOvertime(id) {
  return request({
    url: `/overtime/${id}`,
    method: 'delete'
  })
}
