import request from '@/utils/request'

export function getBusinessTripList(params) {
  return request({
    url: '/business-trip',
    method: 'get',
    params
  })
}

export function getMyBusinessTripList(params) {
  return request({
    url: '/business-trip/my',
    method: 'get',
    params
  })
}

export function getBusinessTripById(id) {
  return request({
    url: `/business-trip/${id}`,
    method: 'get'
  })
}

export function createBusinessTrip(data) {
  return request({
    url: '/business-trip',
    method: 'post',
    data
  })
}

export function updateBusinessTrip(id, data) {
  return request({
    url: `/business-trip/${id}`,
    method: 'put',
    data
  })
}

export function approveBusinessTrip(id, data) {
  return request({
    url: `/business-trip/${id}/approve`,
    method: 'put',
    data
  })
}

export function deleteBusinessTrip(id) {
  return request({
    url: `/business-trip/${id}`,
    method: 'delete'
  })
}
