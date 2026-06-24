import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

export function getCurrentUser() {
  return request({
    url: '/auth/current',
    method: 'get'
  })
}

export function updateProfile(data) {
  return request({
    url: '/auth/profile',
    method: 'put',
    data
  })
}

export function getEmployeeProfile() {
  return request({
    url: '/auth/employee-profile',
    method: 'get'
  })
}

export function updateEmployeeProfile(data) {
  return request({
    url: '/auth/employee-profile',
    method: 'put',
    data
  })
}

export function changePassword(data) {
  return request({
    url: '/auth/change-password',
    method: 'put',
    data
  })
}
