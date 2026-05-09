import request from '../utils/request'

export function updateProfile(data) {
  return request({
    url: '/user/profile',
    method: 'put',
    data: data
  })
}

export function updatePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data: data
  })
}
