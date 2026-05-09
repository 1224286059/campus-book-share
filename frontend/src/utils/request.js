import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const TOKEN_KEY = 'campus-book-share-token'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use((config) => {
  var token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.Authorization = token.indexOf('Bearer ') === 0 ? token : 'Bearer ' + token
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    var payload = response.data || {}
    if (payload.code === 200) {
      return payload.data
    }
    if (payload.code === 401) {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem('campus-book-share-user')
      if (router.currentRoute.value.path !== '/login') {
        router.push({
          path: '/login',
          query: {
            redirect: router.currentRoute.value.fullPath
          }
        })
      }
    }
    ElMessage.error(payload.message || '请求失败')
    return Promise.reject(payload)
  },
  (error) => {
    var message = '网络异常，请稍后重试'
    if (error.response && error.response.data && error.response.data.message) {
      message = error.response.data.message
    } else if (error.message) {
      message = error.message
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export { TOKEN_KEY }
export default request
