/**
 * 小程序网络请求封装
 * 
 * 注意：
 * - 小程序必须使用完整URL，开发时需要在微信开发者工具中勾选"不校验合法域名"
 * - 生产环境需要在微信公众平台配置服务器域名
 */

// 后端 API 地址
// 开发环境: http://localhost:8080/api
// 生产环境: https://your-domain.com/api
const BASE_URL = 'http://localhost:8080/api'

export const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token || ''
      },
      success: (res) => {
        if (res.statusCode === 200) {
          // HTTP 请求成功
          if (res.data.code === 200) {
            // 业务成功
            resolve(res.data)
          } else if (res.data.code === 401) {
            // 未登录，跳转登录页
            uni.removeStorageSync('token')
            uni.removeStorageSync('userInfo')
            uni.showToast({ title: '请重新登录', icon: 'none' })
            setTimeout(() => {
              uni.navigateTo({ url: '/pages/login/index' })
            }, 1500)
            reject(new Error('请重新登录'))
          } else {
            // 业务错误
            reject(new Error(res.data.message || '请求失败'))
          }
        } else {
          // HTTP 错误
          reject(new Error(`服务器错误 (${res.statusCode})`))
        }
      },
      fail: (err) => {
        // 网络错误
        console.error('网络请求失败:', err)
        reject(new Error('网络连接失败，请检查网络'))
      }
    })
  })
}

export default request
