import request from '@/utils/request'

export function getContractList(params) {
  return request({
    url: '/contracts',
    method: 'get',
    params
  })
}

export function getContractById(id) {
  return request({
    url: `/contracts/${id}`,
    method: 'get'
  })
}

export function getContractsByEmployeeId(employeeId) {
  return request({
    url: `/contracts/employee/${employeeId}`,
    method: 'get'
  })
}

export function createContract(data) {
  return request({
    url: '/contracts',
    method: 'post',
    data
  })
}

export function updateContract(id, data) {
  return request({
    url: `/contracts/${id}`,
    method: 'put',
    data
  })
}

export function deleteContract(id) {
  return request({
    url: `/contracts/${id}`,
    method: 'delete'
  })
}

export function getExpiringContracts() {
  return request({
    url: '/contracts/expiring',
    method: 'get'
  })
}

export function getMyContracts() {
  return request({
    url: '/contracts/my',
    method: 'get'
  })
}
