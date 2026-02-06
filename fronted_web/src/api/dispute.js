import request from '@/utils/request'

// 发起申诉
export function createDispute(data) {
  return request({
    url: '/dispute/create',
    method: 'post',
    data
  })
}

// 获取订单的申诉详情
export function getDisputeByOrder(orderId) {
  return request({
    url: `/dispute/order/${orderId}`,
    method: 'get'
  })
}

// 发送留言
export function sendDisputeMessage(disputeId, data) {
  return request({
    url: `/dispute/${disputeId}/message`,
    method: 'post',
    data
  })
}

// 获取申诉列表
export function getDisputeList(params) {
  return request({
    url: '/dispute/list',
    method: 'get',
    params
  })
}

// 撤销申诉
export function cancelDispute(disputeId) {
  return request({
    url: `/dispute/${disputeId}/cancel`,
    method: 'post'
  })
}
