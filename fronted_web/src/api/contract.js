import request from '@/utils/request'

/**
 * 创建或更新合同
 */
export function createOrUpdateContract(data) {
  return request({
    url: '/contract/create',
    method: 'post',
    data
  })
}

/**
 * 提交合同（商家完成编辑）
 */
export function submitContract(contractId) {
  return request({
    url: `/contract/${contractId}/submit`,
    method: 'post'
  })
}

/**
 * 签署合同
 */
export function signContract(contractId, signature) {
  return request({
    url: `/contract/${contractId}/sign`,
    method: 'post',
    data: { signature }
  })
}

/**
 * 获取合同详情
 */
export function getContractDetail(contractId) {
  return request({
    url: `/contract/${contractId}`,
    method: 'get'
  })
}

/**
 * 根据订单获取合同
 */
export function getContractByOrder(orderId) {
  return request({
    url: `/contract/order/${orderId}`,
    method: 'get'
  })
}

/**
 * 获取合同列表
 */
export function getContractList(params) {
  return request({
    url: '/contract/list',
    method: 'get',
    params
  })
}
