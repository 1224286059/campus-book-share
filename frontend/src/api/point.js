import request from '../utils/request'

export function getMyPoints() {
  return request({
    url: '/points/my',
    method: 'get'
  })
}

export function getMyPointRecords() {
  return request({
    url: '/points/my-records',
    method: 'get'
  })
}
