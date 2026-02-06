import request from '@/utils/request'

// 获取订单消息历史
export function getMessageHistory(orderId) {
  return request({
    url: `/message/history/${orderId}`,
    method: 'get'
  })
}

// 标记消息已读
export function markAsRead(orderId) {
  return request({
    url: `/message/read/${orderId}`,
    method: 'post'
  })
}

// 获取未读消息数量
export function getUnreadCount(orderId) {
  return request({
    url: `/message/unread/${orderId}`,
    method: 'get'
  })
}

// 发送消息 (HTTP方式，WebSocket断开时备用)
export function sendMessage(orderId, content, type = 1) {
  return request({
    url: '/message/send',
    method: 'post',
    data: { orderId, content, type }
  })
}
