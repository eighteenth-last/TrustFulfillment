import request from '@/utils/request'

// 获取钱包信息
export function getWalletInfo() {
  return request({
    url: '/wallet/info',
    method: 'get'
  })
}

// 获取交易记录
export function getTransactions(params) {
  return request({
    url: '/wallet/transactions',
    method: 'get',
    params
  })
}

// 充值
export function recharge(data) {
  return request({
    url: '/pay/recharge',
    method: 'post',
    data
  })
}

// 查询支付状态
export function checkPaymentStatus(orderNo) {
  return request({
    url: `/pay/status/${orderNo}`,
    method: 'get'
  })
}

// 提现申请
export function withdraw(data) {
  return request({
    url: '/wallet/withdraw',
    method: 'post',
    data
  })
}

// 获取银行卡列表
export function getBankCards() {
  return request({
    url: '/wallet/bank-cards',
    method: 'get'
  })
}

// 添加银行卡
export function addBankCard(data) {
  return request({
    url: '/wallet/bank-cards',
    method: 'post',
    data
  })
}

// 删除银行卡
export function deleteBankCard(cardId) {
  return request({
    url: `/wallet/bank-cards/${cardId}`,
    method: 'delete'
  })
}
