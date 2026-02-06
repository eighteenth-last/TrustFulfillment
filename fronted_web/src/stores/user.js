import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 用户状态管理
 * 使用 sessionStorage 存储，保证不同标签页可以登录不同账户
 * 如果需要"记住登录"功能，可以额外使用 localStorage 存储 token
 */
export const useUserStore = defineStore('user', () => {
  // 优先从 sessionStorage 读取，保证标签页独立
  // 其次从 localStorage 读取（用于"记住我"功能）
  const token = ref(sessionStorage.getItem('token') || localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(sessionStorage.getItem('userInfo') || localStorage.getItem('userInfo') || '{}'))

  function setToken(newToken, remember = false) {
    token.value = newToken
    // 始终存到 sessionStorage（标签页独立）
    sessionStorage.setItem('token', newToken)
    // 如果勾选"记住我"，也存到 localStorage
    if (remember) {
      localStorage.setItem('token', newToken)
    }
  }

  function setUserInfo(info, remember = false) {
    userInfo.value = info
    // 始终存到 sessionStorage（标签页独立）
    sessionStorage.setItem('userInfo', JSON.stringify(info))
    // 如果勾选"记住我"，也存到 localStorage
    if (remember) {
      localStorage.setItem('userInfo', JSON.stringify(info))
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    // 清除两个存储
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('userInfo')
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { token, userInfo, setToken, setUserInfo, logout }
})
