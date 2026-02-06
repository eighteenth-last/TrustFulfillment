import request from '@/utils/request'

/**
 * 获取客服消息历史
 */
export function getServiceChatHistory() {
  return request({
    url: '/service-chat/history',
    method: 'get'
  })
}

/**
 * 发送消息给客服
 */
export function sendServiceMessage(content, type = 1) {
  return request({
    url: '/service-chat/send',
    method: 'post',
    data: { content, type }
  })
}

/**
 * 标记消息已读
 */
export function markServiceMessageRead() {
  return request({
    url: '/service-chat/read',
    method: 'post'
  })
}
