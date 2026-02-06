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
    // 用户端（甲方）
    {
      path: '/user',
      name: 'user',
      component: () => import('../views/UserLayout.vue'),
      meta: { requiresAuth: true, role: 'user' },
      children: [
        {
          path: 'dashboard',
          name: 'user-dashboard',
          meta: { title: '工作台' },
          component: () => import('../views/user/Dashboard.vue')
        },
        {
          path: 'create-order',
          name: 'create-order',
          meta: { title: '发布任务' },
          component: () => import('../views/user/CreateOrder.vue')
        },
        {
          path: 'orders',
          name: 'order-list',
          meta: { title: '我的项目' },
          component: () => import('../views/user/OrderList.vue')
        },
        {
          path: 'orders/:id',
          name: 'order-detail',
          meta: { title: '订单详情' },
          component: () => import('../views/user/OrderDetail.vue')
        },
        {
          path: 'orders/:id/contract/sign',
          name: 'user-contract-sign',
          meta: { title: '签署合同' },
          component: () => import('../views/user/ContractSign.vue')
        },
        {
          path: 'projects',
          name: 'projects',
          meta: { title: '我的项目' },
          component: () => import('../views/user/OrderList.vue')
        },
        {
          path: 'wallet',
          name: 'wallet',
          meta: { title: '信托钱包' },
          component: () => import('../views/user/Wallet.vue')
        },
        {
          path: 'evidence/:id',
          name: 'evidence',
          meta: { title: '存证查验' },
          component: () => import('../views/user/Evidence.vue')
        },
        {
          path: 'disputes',
          name: 'user-disputes',
          meta: { title: '纠纷裁决' },
          component: () => import('../views/user/Disputes.vue')
        },
        {
          path: 'notifications',
          name: 'user-notifications',
          meta: { title: '通知消息' },
          component: () => import('../views/user/Notifications.vue')
        }
      ]
    },
    // 商家端（乙方）
    {
      path: '/merchant',
      name: 'merchant',
      component: () => import('../views/MerchantLayout.vue'),
      meta: { requiresAuth: true, role: 'merchant' },
      children: [
        {
          path: 'dashboard',
          name: 'merchant-dashboard',
          meta: { title: '工作台' },
          component: () => import('../views/merchant/Dashboard.vue')
        },
        {
          path: 'tasks',
          name: 'task-hall',
          meta: { title: '任务市场' },
          component: () => import('../views/merchant/TaskHall.vue')
        },
        {
          path: 'orders',
          name: 'merchant-orders',
          meta: { title: '我的订单' },
          component: () => import('../views/merchant/OrderList.vue')
        },
        {
          path: 'orders/:id',
          name: 'merchant-order-detail',
          meta: { title: '订单详情' },
          component: () => import('../views/merchant/OrderDetail.vue')
        },
        {
          path: 'orders/:id/contract',
          name: 'merchant-contract-edit',
          meta: { title: '编辑合同' },
          component: () => import('../views/merchant/ContractEdit.vue')
        },
        {
          path: 'orders/:id/contract/sign',
          name: 'merchant-contract-sign',
          meta: { title: '签署合同' },
          component: () => import('../views/merchant/ContractSign.vue')
        },
        {
          path: 'delivery/:id',
          name: 'delivery',
          meta: { title: '交付管理' },
          component: () => import('../views/merchant/Delivery.vue')
        },
        {
          path: 'finance',
          name: 'merchant-finance',
          meta: { title: '财务结算' },
          component: () => import('../views/merchant/Finance.vue')
        },
        {
          path: 'notifications',
          name: 'merchant-notifications',
          meta: { title: '通知消息' },
          component: () => import('../views/merchant/Notifications.vue')
        },
        {
          path: 'contracts',
          name: 'merchant-contracts',
          meta: { title: '我的合同' },
          component: () => import('../views/merchant/Contracts.vue')
        },
        {
          path: 'reviews',
          name: 'merchant-reviews',
          meta: { title: '我的评价' },
          component: () => import('../views/merchant/Reviews.vue')
        }
      ]
    },
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 优先使用 sessionStorage（标签页独立），其次使用 localStorage（记住登录）
  const token = sessionStorage.getItem('token') || localStorage.getItem('token')
  const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || localStorage.getItem('userInfo') || '{}')
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 臻托 TrustFulfillment`
  }
  
  // 不需要登录的页面
  if (to.meta.requiresAuth === false) {
    // 已登录用户访问登录页，跳转到对应首页
    if (token && to.name === 'login') {
      const role = userInfo.role || userInfo.type
      if (role === 2 || role === 'merchant') {
        next('/merchant/dashboard')
      } else {
        next('/user/dashboard')
      }
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
  
  // 检查角色权限
  const role = userInfo.role || userInfo.type
  const requiredRole = to.meta.role || to.matched.find(r => r.meta.role)?.meta.role
  
  if (requiredRole) {
    // 判断是否有商家权限：role为merchant 或 isMerchant为1/true/"1"
    const isMerchantValue = userInfo.isMerchant
    const isMerchant = role === 2 || role === 'merchant' || 
                       isMerchantValue === 1 || isMerchantValue === true || 
                       isMerchantValue === '1' || isMerchantValue === 'true' ||
                       Number(isMerchantValue) === 1
    
    console.log('路由守卫检查:', { to: to.path, role, isMerchantValue, isMerchant, userInfo })
    
    if (requiredRole === 'merchant' && !isMerchant) {
      console.log('无商家权限，重定向到用户端')
      next('/user/dashboard')
      return
    }
    // 用户端页面，所有登录用户都可以访问
  }
  
  next()
})

export default router
