import request from '@/utils/request'

export function getAppraisalList(params) {
  return request({
    url: '/appraisal',
    method: 'get',
    params
  })
}

export function getAppraisalById(id) {
  return request({
    url: `/appraisal/${id}`,
    method: 'get'
  })
}

export function createAppraisal(data) {
  return request({
    url: '/appraisal',
    method: 'post',
    data
  })
}

export function updateAppraisal(id, data) {
  return request({
    url: `/appraisal/${id}`,
    method: 'put',
    data
  })
}

export function deleteAppraisal(id) {
  return request({
    url: `/appraisal/${id}`,
    method: 'delete'
  })
}
