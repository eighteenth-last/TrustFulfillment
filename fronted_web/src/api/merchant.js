import request from '@/utils/request'

/**
 * 获取商户状态
 */
export function getMerchantStatus() {
  return request.get('/merchant/status')
}

/**
 * 提交商户申请
 */
export function submitMerchantApply(data) {
  return request.post('/merchant/apply', data)
}

/**
 * 获取申请历史
 */
export function getApplyHistory() {
  return request.get('/merchant/apply/history')
}

/**
 * 获取商户类型配置
 */
export function getMerchantTypeConfig() {
  return request.get('/merchant/type-config')
}
