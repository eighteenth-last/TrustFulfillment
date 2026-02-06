import request from '@/utils/request'

// 平台统计
export function getDashboardStats() {
  return request({
    url: '/admin/dashboard/stats',
    method: 'get'
  })
}

// 获取待处理任务
export function getPendingTasks() {
  return request({
    url: '/admin/dashboard/pending-tasks',
    method: 'get'
  })
}

// ================ 订单管理 ================

export function getOrderList(params) {
  return request({
    url: '/admin/orders',
    method: 'get',
    params
  })
}

export function getOrderStats() {
  return request({
    url: '/admin/orders/stats',
    method: 'get'
  })
}

export function auditOrder(id, data) {
  return request({
    url: `/admin/orders/${id}/audit`,
    method: 'post',
    data
  })
}

// ================ 信托资金 ================

export function getTrustStats() {
  return request({
    url: '/admin/trust/stats',
    method: 'get'
  })
}

export function getTrustMonitor() {
  return request({
    url: '/admin/trust/monitor',
    method: 'get'
  })
}

export function getTrustTransactions(params) {
  return request({
    url: '/admin/trust/transactions',
    method: 'get',
    params
  })
}

// ================ 纠纷管理 ================

export function getDisputeList(params) {
  return request({
    url: '/admin/disputes',
    method: 'get',
    params
  })
}

export function getDisputeStats() {
  return request({
    url: '/admin/disputes/stats',
    method: 'get'
  })
}

export function getDisputeDetail(id) {
  return request({
    url: `/admin/disputes/${id}`,
    method: 'get'
  })
}

export function resolveDispute(id, data) {
  return request({
    url: `/admin/disputes/${id}/resolve`,
    method: 'post',
    data
  })
}

// ================ 商家管理 ================

export function getMerchantList(params) {
  return request({
    url: '/admin/merchants',
    method: 'get',
    params
  })
}

export function getMerchantApplyList(params) {
  return request({
    url: '/admin/merchant-applies',
    method: 'get',
    params
  })
}

export function getMerchantStats() {
  return request({
    url: '/admin/merchants/stats',
    method: 'get'
  })
}

export function auditMerchant(id, data) {
  return request({
    url: `/admin/merchants/${id}/audit`,
    method: 'post',
    data
  })
}

export function updateMerchantStatus(id, data) {
  return request({
    url: `/admin/merchants/${id}/status`,
    method: 'post',
    data
  })
}

// ================ 用户管理 ================

export function getUserList(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

export function getUserStats() {
  return request({
    url: '/admin/users/stats',
    method: 'get'
  })
}

export function getUserDetail(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'get'
  })
}

export function updateUserStatus(id, data) {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'post',
    data
  })
}

// ================ 风控管理 ================

export function getRiskAlerts() {
  return request({
    url: '/admin/risk/alerts',
    method: 'get'
  })
}

export function getRiskStats() {
  return request({
    url: '/admin/risk/stats',
    method: 'get'
  })
}

export function getRiskTrend() {
  return request({
    url: '/admin/risk/trend',
    method: 'get'
  })
}

// 获取风控规则配置
export function getRiskRules() {
  return request({
    url: '/admin/risk/rules',
    method: 'get'
  })
}

// 保存风控规则配置
export function saveRiskRules(data) {
  return request({
    url: '/admin/risk/rules',
    method: 'post',
    data
  })
}

// 获取风险事件列表
export function getRiskEventList(params) {
  return request({
    url: '/admin/risk/events',
    method: 'get',
    params
  })
}

// 处理风险事件
export function processRiskEvent(id, data) {
  return request({
    url: `/admin/risk/events/${id}/process`,
    method: 'post',
    data
  })
}

// ================ 客服消息 ================

export function getServiceConversations() {
  return request({
    url: '/admin/service/conversations',
    method: 'get'
  })
}

export function getServiceMessages(userId) {
  return request({
    url: `/admin/service/messages/${userId}`,
    method: 'get'
  })
}

export function sendServiceMessage(data) {
  return request({
    url: '/admin/service/messages',
    method: 'post',
    data
  })
}

export function markMessagesRead(userId) {
  return request({
    url: `/admin/service/messages/${userId}/read`,
    method: 'post'
  })
}

export function getUnreadMessageCount() {
  return request({
    url: '/admin/service/unread-count',
    method: 'get'
  })
}

// ================ 合同管理 ================

export function getContractList(params) {
  return request({
    url: '/admin/contracts',
    method: 'get',
    params
  })
}

export function getContractStats() {
  return request({
    url: '/admin/contracts/stats',
    method: 'get'
  })
}

// ================ 银行信息管理 ================

export function getBankList() {
  return request({
    url: '/admin/banks',
    method: 'get'
  })
}

export function saveBank(data) {
  return request({
    url: '/admin/banks',
    method: 'post',
    data
  })
}

export function deleteBank(id) {
  return request({
    url: `/admin/banks/${id}`,
    method: 'delete'
  })
}

export function updateBankStatus(id, data) {
  return request({
    url: `/admin/banks/${id}/status`,
    method: 'post',
    data
  })
}

// ================ 用户搜索 ================

export function searchUsers(keyword) {
  return request({
    url: '/admin/users/search',
    method: 'get',
    params: { keyword }
  })
}

// ================ 系统通知管理 ================

export function getNotificationList(params) {
  return request({
    url: '/admin/notifications',
    method: 'get',
    params
  })
}

export function getNotificationStats() {
  return request({
    url: '/admin/notifications/stats',
    method: 'get'
  })
}

export function sendNotification(data) {
  return request({
    url: '/admin/notifications',
    method: 'post',
    data
  })
}

export function deleteNotification(id) {
  return request({
    url: `/admin/notifications/${id}`,
    method: 'delete'
  })
}

// ================ 业务分类管理 ================

export function getCategoryTree() {
  return request({
    url: '/admin/categories/tree',
    method: 'get'
  })
}

export function saveCategory(data) {
  return request({
    url: '/admin/categories',
    method: 'post',
    data
  })
}

export function deleteCategory(id) {
  return request({
    url: `/admin/categories/${id}`,
    method: 'delete'
  })
}

export function updateCategoryStatus(id, data) {
  return request({
    url: `/admin/categories/${id}/status`,
    method: 'post',
    data
  })
}

// ================ 系统设置 ================

export function getSettings() {
  return request({
    url: '/admin/settings',
    method: 'get'
  })
}

export function saveSettings(data) {
  return request({
    url: '/admin/settings',
    method: 'post',
    data
  })
}

// ================ 短信服务配置 ================

// 获取短信服务配置
export function getSmsSettings() {
  return request({
    url: '/admin/sms/settings',
    method: 'get'
  })
}

// 保存短信服务配置
export function saveSmsSettings(data) {
  return request({
    url: '/admin/sms/settings',
    method: 'post',
    data
  })
}

// 发送测试短信
export function sendTestSms(phone) {
  return request({
    url: '/admin/sms/test',
    method: 'post',
    data: { phone }
  })
}

// ============ 提成管理 API ============

// 获取提成列表
export function getCommissionList(params) {
  return request({
    url: '/admin/commissions',
    method: 'get',
    params
  })
}

// 获取提成统计
export function getCommissionStats() {
  return request({
    url: '/admin/commissions/stats',
    method: 'get'
  })
}

// 获取提成详情
export function getCommissionDetail(id) {
  return request({
    url: `/admin/commissions/${id}`,
    method: 'get'
  })
}

// 更新商户提成比例
export function updateMerchantCommissionRate(merchantId, commissionRate) {
  return request({
    url: `/admin/merchants/${merchantId}/commission-rate`,
    method: 'post',
    data: { commissionRate }
  })
}

// ================ 支付配置管理 ================

// 获取支付配置
export function getPayConfig() {
  return request({
    url: '/admin/pay/config',
    method: 'get'
  })
}

// 保存支付配置
export function savePayConfig(data) {
  return request({
    url: '/admin/pay/config',
    method: 'post',
    data
  })
}
