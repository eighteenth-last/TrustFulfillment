import axios from 'axios'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器
request.interceptors.request.use(config => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers['Authorization'] = userStore.token
  }
  return config
}, error => {
  return Promise.reject(error)
})

// 响应拦截器
request.interceptors.response.use(response => {
  const res = response.data
  if (res.code === 200) {
    return res
  } else if (res.code === 401) {
    const userStore = useUserStore()
    userStore.logout()
    router.push('/login')
    return Promise.reject(new Error('请重新登录'))
  } else {
    console.error(res.message)
    return Promise.reject(new Error(res.message || 'Error'))
  }
}, error => {
  if (error.response && error.response.status === 401) {
    const userStore = useUserStore()
    userStore.logout()
    router.push('/login')
  }
  return Promise.reject(error)
})

export default request
