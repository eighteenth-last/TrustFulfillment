import request from '@/utils/request'

/**
 * 登录 (手机号+密码)
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: {
      phone: data.phone,
      password: data.password
    }
  })
}

/**
 * 注册
 */
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data: {
      phone: data.phone,
      password: data.password,
      nickname: data.nickname,
      role: data.role
    }
  })
}

/**
 * 退出登录
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return request({
    url: '/auth/info',
    method: 'get'
  })
}

/**
 * 发送短信验证码
 */
export function sendSmsCode(phone, type = 1) {
  return request({
    url: '/auth/sms/send',
    method: 'post',
    data: { phone, type }
  })
}

/**
 * 手机号验证码登录
 */
export function loginByPhone(phone, code) {
  return request({
    url: '/auth/login/phone',
    method: 'post',
    data: { phone, code }
  })
}

/**
 * 更新用户信息
 */
export function updateUserInfo(data) {
  return request({
    url: '/auth/update',
    method: 'put',
    data
  })
}

/**
 * 获取业务分类列表
 */
export function getBusinessCategories() {
  return request({
    url: '/auth/categories',
    method: 'get'
  })
}
