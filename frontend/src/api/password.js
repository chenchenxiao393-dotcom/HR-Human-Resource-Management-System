import request from '@/utils/request'

export const changePassword = (data) => {
  return request({
    url: '/auth/change-password',
    method: 'put',
    data
  })
}
