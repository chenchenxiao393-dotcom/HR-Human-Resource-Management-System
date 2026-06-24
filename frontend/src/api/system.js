import request from '@/utils/request'

export function backup() {
  return request({
    url: '/system/backup',
    method: 'post'
  })
}

export function restore(backupFile) {
  return request({
    url: '/system/restore',
    method: 'post',
    params: { backupFile }
  })
}

export function health() {
  return request({
    url: '/system/health',
    method: 'get'
  })
}
