import request from '@/utils/request'

// 获取订单存证记录
export function getOrderEvidence(orderId) {
  return request({
    url: `/evidence/order/${orderId}`,
    method: 'get'
  })
}

// 获取存证详情
export function getEvidenceDetail(id) {
  return request({
    url: `/evidence/${id}`,
    method: 'get'
  })
}

// 获取存证类型列表
export function getEvidenceTypes() {
  return request({
    url: '/evidence/types',
    method: 'get'
  })
}
