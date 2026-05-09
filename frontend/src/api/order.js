import request from '../utils/request'

export function createOrder(data) {
  return request({
    url: '/orders',
    method: 'post',
    data: data
  })
}

export function getMyCreatedOrders() {
  return request({
    url: '/orders/my-created',
    method: 'get'
  })
}

export function getMyReceivedOrders() {
  return request({
    url: '/orders/my-received',
    method: 'get'
  })
}

export function acceptOrder(id) {
  return request({
    url: '/orders/' + id + '/accept',
    method: 'put'
  })
}

export function rejectOrder(id) {
  return request({
    url: '/orders/' + id + '/reject',
    method: 'put'
  })
}

export function cancelOrder(id) {
  return request({
    url: '/orders/' + id + '/cancel',
    method: 'put'
  })
}

export function completeOrder(id) {
  return request({
    url: '/orders/' + id + '/complete',
    method: 'put'
  })
}
