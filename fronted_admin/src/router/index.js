import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      meta: { requiresAuth: false },
      component: () => import('../views/Login.vue')
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/Layout.vue'),
      redirect: '/admin/dashboard',
      meta: { requiresAuth: true },
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          meta: { title: '平台总览' },
          component: () => import('../views/Dashboard.vue')
        },
        {
          path: 'orders',
          name: 'orders',
          meta: { title: '任务审批' },
          component: () => import('../views/Orders.vue')
        },
        {
          path: 'trust',
          name: 'trust',
          meta: { title: '信托资金' },
          component: () => import('../views/TrustMonitor.vue')
        },
        {
          path: 'disputes',
          name: 'disputes',
          meta: { title: '纠纷调处' },
          component: () => import('../views/Disputes.vue')
        },
        {
          path: 'merchants',
          name: 'merchants',
          meta: { title: '商家审核' },
          component: () => import('../views/Merchants.vue')
        },
        {
          path: 'users',
          name: 'users',
          meta: { title: '用户管理' },
          component: () => import('../views/Users.vue')
        },
        {
          path: 'risk',
          name: 'risk',
          meta: { title: '风险监控' },
          component: () => import('../views/Risk.vue')
        },
        {
          path: 'customer-service',
          name: 'customerService',
          meta: { title: '客户解答' },
          component: () => import('../views/CustomerService.vue')
        },
        {
          path: 'contracts',
          name: 'contracts',
          meta: { title: '合同管理' },
          component: () => import('../views/Contracts.vue')
        },
        {
          path: 'banks',
          name: 'banks',
          meta: { title: '银行信息' },
          component: () => import('../views/Banks.vue')
        },
        {
          path: 'notifications',
          name: 'notifications',
          meta: { title: '系统通知' },
          component: () => import('../views/Notifications.vue')
        },
        {
          path: 'categories',
          name: 'categories',
          meta: { title: '业务分类' },
          component: () => import('../views/Categories.vue')
        },
        {
          path: 'commissions',
          name: 'commissions',
          meta: { title: '提成管理' },
          component: () => import('../views/Commissions.vue')
        },
        {
          path: 'settings',
          name: 'settings',
          meta: { title: '系统设置' },
          component: () => import('../views/Settings.vue')
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('admin_token')
  const adminInfo = JSON.parse(localStorage.getItem('admin_info') || '{}')
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 臻托管理后台`
  } else {
    document.title = '臻托管理后台'
  }
  
  // 不需要登录的页面
  if (to.meta.requiresAuth === false) {
    // 已登录管理员访问登录页，跳转到首页
    if (token && to.name === 'login') {
      next('/admin/dashboard')
      return
    }
    next()
    return
  }
  
  // 需要登录的页面
  if (!token) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
    return
  }
  
  // 验证管理员权限
  if (adminInfo.role !== 'admin' && adminInfo.role !== 'super_admin') {
    // 非管理员角色
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_info')
    next('/login')
    return
  }
  
  next()
})

export default router
