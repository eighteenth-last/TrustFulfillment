// 简化的用户状态管理（Vue 2 兼容）
const state = {
  token: '',
  userInfo: {}
}

// 初始化从本地存储读取
try {
  state.token = uni.getStorageSync('token') || ''
  state.userInfo = uni.getStorageSync('userInfo') || {}
} catch (e) {
  console.error('读取存储失败', e)
}

export const useUserStore = () => {
  return {
    get token() {
      return state.token
    },
    get userInfo() {
      return state.userInfo
    },
    setToken(newToken) {
      state.token = newToken
      try {
        uni.setStorageSync('token', newToken)
      } catch (e) {
        console.error('存储token失败', e)
      }
    },
    setUserInfo(info) {
      state.userInfo = info
      try {
        uni.setStorageSync('userInfo', info)
      } catch (e) {
        console.error('存储userInfo失败', e)
      }
    },
    logout() {
      state.token = ''
      state.userInfo = {}
      try {
        uni.removeStorageSync('token')
        uni.removeStorageSync('userInfo')
      } catch (e) {
        console.error('清除存储失败', e)
      }
    },
    checkLogin() {
      return !!state.token
    }
  }
}
