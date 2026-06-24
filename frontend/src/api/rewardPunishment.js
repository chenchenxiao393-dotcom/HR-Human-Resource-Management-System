import request from '@/utils/request'

export function getRewardPunishmentList(params) {
  return request({
    url: '/reward-punishment',
    method: 'get',
    params
  })
}

export function getMyRewardPunishmentList(params) {
  return request({
    url: '/reward-punishment/my',
    method: 'get',
    params
  })
}

export function getRewardPunishmentById(id) {
  return request({
    url: `/reward-punishment/${id}`,
    method: 'get'
  })
}

export function createRewardPunishment(data) {
  return request({
    url: '/reward-punishment',
    method: 'post',
    data
  })
}

export function updateRewardPunishment(id, data) {
  return request({
    url: `/reward-punishment/${id}`,
    method: 'put',
    data
  })
}

export function approveRewardPunishment(id, data) {
  return request({
    url: `/reward-punishment/${id}/approve`,
    method: 'put',
    data
  })
}

export function deleteRewardPunishment(id) {
  return request({
    url: `/reward-punishment/${id}`,
    method: 'delete'
  })
}
