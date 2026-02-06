import request from '@/utils/request'

// 创建订单
export function createOrder(data) {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

// 获取订单详情
export function getOrderDetail(id) {
  return request({
    url: `/order/${id}`,
    method: 'get'
  })
}

// 获取订单阶段列表
export function getOrderStages(orderId) {
  return request({
    url: `/order/${orderId}/stages`,
    method: 'get'
  })
}

// 接单（商家）
export function acceptOrder(orderId) {
  return request({
    url: `/order/${orderId}/accept`,
    method: 'post'
  })
}

// 托管资金（用户付款第一阶段）
export function depositFunds(orderId) {
  return request({
    url: `/order/${orderId}/deposit`,
    method: 'post'
  })
}

// 支付下一阶段款项
export function payNextStage(orderId) {
  return request({
    url: `/order/${orderId}/pay-next-stage`,
    method: 'post'
  })
}

// 开始阶段（商家）
export function startStage(orderId, stageId) {
  return request({
    url: `/order/${orderId}/stage/${stageId}/start`,
    method: 'post'
  })
}

// 提交交付（商家）
export function submitDelivery(orderId, stageId, data) {
  return request({
    url: `/order/${orderId}/stage/${stageId}/deliver`,
    method: 'post',
    data
  })
}

// 验收通过（用户）
export function acceptDelivery(orderId, stageId) {
  return request({
    url: `/order/${orderId}/stage/${stageId}/accept`,
    method: 'post'
  })
}

// 拒绝验收（用户）
export function rejectDelivery(orderId, stageId, reason) {
  return request({
    url: `/order/${orderId}/stage/${stageId}/reject`,
    method: 'post',
    data: { reason }
  })
}

// 释放质保款（用户）
export function releaseWarranty(orderId) {
  return request({
    url: `/order/${orderId}/release-warranty`,
    method: 'post'
  })
}

// 获取阶段详情
export function getStageDetail(stageId) {
  return request({
    url: `/order/stage/${stageId}`,
    method: 'get'
  })
}

// 取消订单
export function cancelOrder(orderId) {
  return request({
    url: `/order/${orderId}/cancel`,
    method: 'post'
  })
}

// 获取用户统计数据
export function getUserStats() {
  return request({
    url: '/order/stats',
    method: 'get'
  })
}

// 获取订单统计数据（别名）
export function getOrderStats() {
  return request({
    url: '/order/stats',
    method: 'get'
  })
}
