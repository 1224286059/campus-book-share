import request from '../utils/request'

export function getAdminUsers(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params: params
  })
}

export function disableAdminUser(id) {
  return request({
    url: '/admin/users/' + id + '/disable',
    method: 'put'
  })
}

export function enableAdminUser(id) {
  return request({
    url: '/admin/users/' + id + '/enable',
    method: 'put'
  })
}

export function getAdminBooks(params) {
  return request({
    url: '/admin/books',
    method: 'get',
    params: params
  })
}

export function getPendingBooks() {
  return request({
    url: '/admin/books/pending',
    method: 'get'
  })
}

export function approveBook(id) {
  return request({
    url: '/admin/books/' + id + '/approve',
    method: 'put'
  })
}

export function rejectBook(id) {
  return request({
    url: '/admin/books/' + id + '/reject',
    method: 'put'
  })
}

export function adminOffShelfBook(id) {
  return request({
    url: '/admin/books/' + id + '/off-shelf',
    method: 'put'
  })
}

export function createCategory(data) {
  return request({
    url: '/admin/categories',
    method: 'post',
    data: data
  })
}

export function updateCategory(id, data) {
  return request({
    url: '/admin/categories/' + id,
    method: 'put',
    data: data
  })
}

export function deleteCategory(id) {
  return request({
    url: '/admin/categories/' + id,
    method: 'delete'
  })
}

export function getAdminOrders(params) {
  return request({
    url: '/admin/orders',
    method: 'get',
    params: params
  })
}

export function getAdminEvaluations() {
  return request({
    url: '/admin/evaluations',
    method: 'get'
  })
}

export function deleteEvaluation(id) {
  return request({
    url: '/admin/evaluations/' + id,
    method: 'delete'
  })
}

export function getAdminReports() {
  return request({
    url: '/admin/reports',
    method: 'get'
  })
}

export function processReport(id, data) {
  return request({
    url: '/admin/reports/' + id + '/process',
    method: 'put',
    data: data || {}
  })
}
