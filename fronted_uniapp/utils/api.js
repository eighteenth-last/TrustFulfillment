/**
 * API 接口封装
 * 小程序端使用
 */
import request from './request'

// ============ 认证相关 ============

/**
 * 发送短信验证码
 * @param {string} phone 手机号
 * @param {number} type 类型: 1登录/注册, 2绑定手机
 */
export function sendSmsCode(phone, type = 1) {
  return request({
    url: '/auth/sms/send',
    method: 'POST',
    data: { phone, type }
  })
}

/**
 * 手机号验证码登录
 * @param {string} phone 手机号
 * @param {string} code 验证码
 */
export function loginByPhone(phone, code) {
  return request({
    url: '/auth/login/phone',
    method: 'POST',
    data: { 
      phone, 
      code,
      platform: 'mp'
    }
  })
}

/**
 * 微信登录
 * @param {string} code 微信登录code (wx.login获取)
 * @param {object} userInfo 用户信息 (可选)
 */
export function loginByWechat(code, userInfo = null) {
  return request({
    url: '/auth/login/wechat',
    method: 'POST',
    data: { 
      code, 
      userInfo,
      platform: 'mp'
    }
  })
}

/**
 * 绑定手机号 (微信用户首次登录)
 * @param {string} phone 手机号
 * @param {string} code 验证码
 * @param {string} openid 微信openid
 */
export function bindPhone(phone, code, openid) {
  return request({
    url: '/auth/bindPhone',
    method: 'POST',
    data: { phone, code, openid }
  })
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request({
    url: '/auth/info',
    method: 'GET'
  })
}

/**
 * 获取业务分类
 */
export function getBusinessCategories() {
  return request({
    url: '/public/categories',
    method: 'GET'
  })
}

/**
 * 退出登录
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'POST'
  })
}

// ============ 订单相关 ============

/**
 * 创建订单（发布任务）
 * @param {object} data 订单数据
 */
export function createOrder(data) {
  return request({
    url: '/order/create',
    method: 'POST',
    data
  })
}

/**
 * 获取订单列表
 */
export function getOrderList(params = {}) {
  return request({
    url: '/order/list',
    method: 'GET',
    data: params
  })
}

/**
 * 获取订单详情
 */
export function getOrderDetail(id) {
  return request({
    url: `/order/${id}`,
    method: 'GET'
  })
}

/**
 * 获取订单统计
 */
export function getOrderStats() {
  return request({
    url: '/order/stats',
    method: 'GET'
  })
}

// ============ 钱包相关 ============

/**
 * 获取钱包信息
 */
export function getWalletInfo() {
  return request({
    url: '/wallet/info',
    method: 'GET'
  })
}

/**
 * 获取交易记录
 */
export function getTransactions(params = {}) {
  return request({
    url: '/wallet/transactions',
    method: 'GET',
    data: params
  })
}

// ============ 任务大厅 ============

/**
 * 获取任务列表
 */
export function getTaskList(params = {}) {
  return request({
    url: '/task/list',
    method: 'GET',
    data: params
  })
}

/**
 * 获取任务详情
 */
export function getTaskDetail(id) {
  return request({
    url: `/task/${id}`,
    method: 'GET'
  })
}
