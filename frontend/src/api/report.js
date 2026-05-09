import request from '../utils/request'

export function createReport(data) {
  return request({
    url: '/reports',
    method: 'post',
    data: data
  })
}
