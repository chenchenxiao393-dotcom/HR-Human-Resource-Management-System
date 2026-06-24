import request from '@/utils/request'

export function getMySalary() {
  return request({
    url: '/salary/my',
    method: 'get'
  })
}

export function getSalaryHistory() {
  return request({
    url: '/salary/history',
    method: 'get'
  })
}
