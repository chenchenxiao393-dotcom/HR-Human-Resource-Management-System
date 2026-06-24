import request from '@/utils/request'

export function getPersonnelChangeList(params) {
  return request({
    url: '/personnel-change',
    method: 'get',
    params
  })
}

export function getMyPersonnelChangeList(params) {
  return request({
    url: '/personnel-change/my',
    method: 'get',
    params
  })
}

export function getPersonnelChangeById(id) {
  return request({
    url: `/personnel-change/${id}`,
    method: 'get'
  })
}

export function createPersonnelChange(data) {
  return request({
    url: '/personnel-change',
    method: 'post',
    data
  })
}

export function updatePersonnelChange(id, data) {
  return request({
    url: `/personnel-change/${id}`,
    method: 'put',
    data
  })
}

export function approvePersonnelChange(id, data) {
  return request({
    url: `/personnel-change/${id}/approve`,
    method: 'put',
    data
  })
}

export function deletePersonnelChange(id) {
  return request({
    url: `/personnel-change/${id}`,
    method: 'delete'
  })
}
