import request from '@/utils/request'

// 获取银行选项列表
export function getBankOptions() {
  return request({
    url: '/banks/options',
    method: 'get'
  })
}
