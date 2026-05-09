import request from '../utils/request'

export function getBookList(params) {
  return request({
    url: '/books',
    method: 'get',
    params: params
  })
}

export function getBookDetail(id) {
  return request({
    url: '/books/' + id,
    method: 'get'
  })
}

export function publishBook(data) {
  return request({
    url: '/books',
    method: 'post',
    data: data
  })
}

export function getMyPublishedBooks() {
  return request({
    url: '/books/my-published',
    method: 'get'
  })
}

export function getMyOwnedBooks() {
  return request({
    url: '/books/my-owned',
    method: 'get'
  })
}

export function offShelfBook(id) {
  return request({
    url: '/books/' + id + '/off-shelf',
    method: 'put'
  })
}

export function reshareBook(id, data) {
  return request({
    url: '/books/' + id + '/reshare',
    method: 'post',
    data: data
  })
}
