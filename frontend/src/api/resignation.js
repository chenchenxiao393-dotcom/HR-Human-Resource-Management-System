import request from '@/utils/request'

export function getResignationList(params) {
  return request({
    url: '/resignation',
    method: 'get',
    params
  })
}

export function getResignationById(id) {
  return request({
    url: `/resignation/${id}`,
    method: 'get'
  })
}

export function createResignation(data) {
  return request({
    url: '/resignation',
    method: 'post',
    data
  })
}

export function updateResignation(id, data) {
  return request({
    url: `/resignation/${id}`,
    method: 'put',
    data
  })
}

export function approveResignation(id, data) {
  return request({
    url: `/resignation/${id}/approve`,
    method: 'put',
    data
  })
}

export function deleteResignation(id) {
  return request({
    url: `/resignation/${id}`,
    method: 'delete'
  })
}
