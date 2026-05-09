import request from '../utils/request'

export function createEvaluation(data) {
  return request({
    url: '/evaluations',
    method: 'post',
    data: data
  })
}

export function getBookEvaluations(bookId) {
  return request({
    url: '/evaluations/book/' + bookId,
    method: 'get'
  })
}

export function getUserEvaluations(userId) {
  return request({
    url: '/evaluations/user/' + userId,
    method: 'get'
  })
}
