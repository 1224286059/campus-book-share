import request from '../utils/request'

export function getMyBorrows() {
  return request({
    url: '/borrows/my',
    method: 'get'
  })
}

export function returnBorrow(id) {
  return request({
    url: '/borrows/' + id + '/return',
    method: 'put'
  })
}
