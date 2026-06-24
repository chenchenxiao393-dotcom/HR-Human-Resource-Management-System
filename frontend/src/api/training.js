import request from '@/utils/request'

export function getTrainingList(params) {
  return request({
    url: '/training',
    method: 'get',
    params
  })
}

export function getAllTrainings() {
  return request({
    url: '/training/all',
    method: 'get'
  })
}

export function getTrainingById(id) {
  return request({
    url: `/training/${id}`,
    method: 'get'
  })
}

export function createTraining(data) {
  return request({
    url: '/training',
    method: 'post',
    data
  })
}

export function updateTraining(id, data) {
  return request({
    url: `/training/${id}`,
    method: 'put',
    data
  })
}

export function deleteTraining(id) {
  return request({
    url: `/training/${id}`,
    method: 'delete'
  })
}

export function getAvailableTrainings() {
  return request({
    url: '/employee-training/available',
    method: 'get'
  })
}

export function enrollTraining(trainingId) {
  return request({
    url: '/employee-training/enroll',
    method: 'post',
    data: { trainingId }
  })
}

export function getMyTrainingRecords() {
  return request({
    url: '/employee-training/records',
    method: 'get'
  })
}
