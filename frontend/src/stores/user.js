import { defineStore } from 'pinia'
import { getCurrentUser, login as loginApi } from '../api/auth'
import { TOKEN_KEY } from '../utils/request'

var USER_KEY = 'campus-book-share-user'

export const useUserStore = defineStore('user', {
  state: function () {
    return {
      token: localStorage.getItem(TOKEN_KEY) || '',
      profile: JSON.parse(localStorage.getItem(USER_KEY) || 'null'),
      loadingProfile: false
    }
  },
  getters: {
    isLoggedIn: function (state) {
      return !!state.token
    },
    isAdmin: function (state) {
      return state.profile && state.profile.role === 'ADMIN'
    }
  },
  actions: {
    setToken: function (token) {
      this.token = token || ''
      if (this.token) {
        localStorage.setItem(TOKEN_KEY, this.token)
      } else {
        localStorage.removeItem(TOKEN_KEY)
      }
    },
    setProfile: function (profile) {
      this.profile = profile || null
      if (profile) {
        localStorage.setItem(USER_KEY, JSON.stringify(profile))
      } else {
        localStorage.removeItem(USER_KEY)
      }
    },
    login: function (payload) {
      var _this = this
      return loginApi(payload).then(function (data) {
        _this.setToken(data.token)
        _this.setProfile({
          id: data.id,
          username: data.username,
          role: data.role
        })
        return _this.fetchProfile(true)
      })
    },
    fetchProfile: function (silent) {
      var _this = this
      if (!this.token) {
        return Promise.resolve(null)
      }
      this.loadingProfile = true
      return getCurrentUser()
        .then(function (profile) {
          _this.setProfile(profile)
          return profile
        })
        .catch(function (error) {
          if (!silent) {
            throw error
          }
          return null
        })
        .finally(function () {
          _this.loadingProfile = false
        })
    },
    logout: function () {
      this.setToken('')
      this.setProfile(null)
    }
  }
})
