import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/admin/logout',
    method: 'post'
  })
}

export function getAdminInfo() {
  return request({
    url: '/admin/info',
    method: 'get'
  })
}

// 更新管理员个人信息
export function updateAdminProfile(data) {
  return request({
    url: '/admin/profile',
    method: 'put',
    data
  })
}

// 修改管理员密码
export function changeAdminPassword(data) {
  return request({
    url: '/admin/password',
    method: 'post',
    data
  })
}
