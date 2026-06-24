import request from '@/utils/request'

export function getTrainingRecordList(params) {
  return request({
    url: '/training-record',
    method: 'get',
    params
  })
}

export function getTrainingRecordById(id) {
  return request({
    url: `/training-record/${id}`,
    method: 'get'
  })
}

export function createTrainingRecord(data) {
  return request({
    url: '/training-record',
    method: 'post',
    data
  })
}

export function updateTrainingRecord(id, data) {
  return request({
    url: `/training-record/${id}`,
    method: 'put',
    data
  })
}

export function deleteTrainingRecord(id) {
  return request({
    url: `/training-record/${id}`,
    method: 'delete'
  })
}
