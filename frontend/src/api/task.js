import request from '@/utils/request'

export function getTaskList(params) {
  return request({
    url: '/task',
    method: 'get',
    params
  })
}

export function getMyTasks() {
  return request({
    url: '/task/my-tasks',
    method: 'get'
  })
}

export function getTaskById(id) {
  return request({
    url: `/task/${id}`,
    method: 'get'
  })
}

export function createTask(data) {
  return request({
    url: '/task',
    method: 'post',
    data
  })
}

export function updateTask(id, data) {
  return request({
    url: `/task/${id}`,
    method: 'put',
    data
  })
}

export function deleteTask(id) {
  return request({
    url: `/task/${id}`,
    method: 'delete'
  })
}