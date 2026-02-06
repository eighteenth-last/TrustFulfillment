<template>
  <div class="flex h-screen bg-slate-900">
    <!-- Sidebar -->
    <div class="w-64 bg-slate-800 border-r border-slate-700 flex flex-col">
      <div class="p-6 flex items-center gap-3">
        <img src="/logo.png" alt="臻托" class="w-10 h-10 rounded-lg object-cover" />
        <span class="font-black text-xl text-white tracking-tight">臻托 TF</span>
      </div>

      <nav class="flex-1 px-4 space-y-2 mt-4">
        <router-link to="/merchant/dashboard" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'nav-active' : 'text-gray-400 hover:bg-slate-700'">
            <i class="fas fa-chart-pie w-6 text-center"></i>
            <span>工作台</span>
          </a>
        </router-link>

        <router-link to="/merchant/tasks" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'nav-active' : 'text-gray-400 hover:bg-slate-700'">
            <i class="fas fa-layer-group w-6 text-center"></i>
            <span>任务市场</span>
          </a>
        </router-link>

        <router-link to="/merchant/orders" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'nav-active' : 'text-gray-400 hover:bg-slate-700'">
            <i class="fas fa-clipboard-list w-6 text-center"></i>
            <span>我的订单</span>
          </a>
        </router-link>

        <router-link to="/merchant/finance" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'nav-active' : 'text-gray-400 hover:bg-slate-700'">
            <i class="fas fa-wallet w-6 text-center"></i>
            <span>财务结算</span>
          </a>
        </router-link>
      </nav>

      <div class="p-4 border-t border-slate-700">
        <n-dropdown 
          trigger="click" 
          :options="userMenuOptions" 
          @select="handleMenuSelect"
          placement="top-start"
          :style="{ width: '200px' }"
          :theme-overrides="dropdownTheme"
        >
          <div class="flex items-center gap-3 p-2 rounded-xl hover:bg-slate-700 cursor-pointer">
            <div class="w-10 h-10 bg-slate-600 rounded-full flex items-center justify-center text-gray-300">
              <i class="fas fa-user"></i>
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-bold text-white truncate">{{ userStore.userInfo.nickname || userStore.userInfo.username }}</p>
              <p class="text-xs truncate" style="color: #00AFE1;">
                <i class="fas fa-check-circle mr-1"></i>
                认证服务商
              </p>
            </div>
            <i class="fas fa-chevron-up text-gray-500 text-xs"></i>
          </div>
        </n-dropdown>
      </div>
    </div>

    <!-- 用户信息设置弹窗 -->
    <n-modal v-model:show="showProfileModal" preset="card" title="个人信息设置" class="max-w-lg">
      <div class="space-y-4">
        <n-form-item label="昵称/企业名称">
          <n-input v-model:value="profileForm.nickname" placeholder="请输入昵称或企业名称" />
        </n-form-item>
        <n-form-item label="真实姓名">
          <n-input v-model:value="profileForm.realName" placeholder="请输入真实姓名（用于提现验证）" />
        </n-form-item>
        <n-form-item label="手机号">
          <n-input v-model:value="profileForm.phone" placeholder="请输入手机号" disabled />
        </n-form-item>
        <n-form-item label="邮箱">
          <n-input v-model:value="profileForm.email" placeholder="请输入邮箱（用于接收通知）" />
        </n-form-item>
        <n-form-item label="主营业务">
          <n-cascader
            v-model:value="profileForm.businessCategories"
            :options="businessCategoryOptions"
            :cascade="true"
            multiple
            check-strategy="child"
            :show-path="true"
            placeholder="请选择您的主营业务"
            max-tag-count="responsive"
            clearable
            :filter="filterCategories"
            filterable
            class="w-full"
          />
          <p class="text-xs text-gray-500 mt-2">选择您擅长的业务类型（支持多选），任务市场将优先展示相关任务</p>
        </n-form-item>
      </div>
      <template #footer>
        <div class="flex justify-end">
          <n-button type="primary" color="#00AFE1" :loading="savingProfile" @click="saveProfile">
            保存修改
          </n-button>
        </div>
      </template>
    </n-modal>

    <!-- 钱包设置弹窗（银行卡管理） -->
    <n-modal v-model:show="showWalletModal" preset="card" title="钱包与银行卡设置" class="max-w-2xl">
      <div class="space-y-6">
        <!-- 已绑定的银行卡 -->
        <div>
          <h4 class="text-sm font-bold text-gray-400 mb-3">已绑定银行卡</h4>
          <div v-if="bankCards.length === 0" class="text-center py-8 text-gray-500">
            <i class="fas fa-credit-card text-4xl mb-3 opacity-30"></i>
            <p>暂无绑定的银行卡</p>
          </div>
          <div v-else class="space-y-3">
            <div 
              v-for="card in bankCards" 
              :key="card.id"
              class="flex items-center gap-4 p-4 bg-slate-700 rounded-xl"
            >
              <div class="w-12 h-12 bg-gradient-to-br from-blue-500 to-blue-700 rounded-lg flex items-center justify-center">
                <i class="fas fa-university text-white text-lg"></i>
              </div>
              <div class="flex-1">
                <p class="font-bold text-white">{{ card.bankName }}</p>
                <p class="text-sm text-gray-400">**** **** **** {{ card.cardNo.slice(-4) }}</p>
              </div>
              <n-tag v-if="card.isDefault" type="success" size="small">默认</n-tag>
              <n-button text type="error" size="small" @click="removeCard(card.id)">
                <i class="fas fa-trash"></i>
              </n-button>
            </div>
          </div>
        </div>

        <!-- 添加银行卡 -->
        <div class="pt-4 border-t border-slate-600">
          <h4 class="text-sm font-bold text-gray-400 mb-3">添加新银行卡</h4>
          <div class="grid grid-cols-2 gap-4">
            <n-form-item label="持卡人姓名">
              <n-input v-model:value="newCard.holderName" placeholder="请输入持卡人姓名" />
            </n-form-item>
            <n-form-item label="银行名称">
              <n-select 
                v-model:value="newCard.bankName" 
                :options="bankOptions" 
                placeholder="请选择银行"
              />
            </n-form-item>
            <n-form-item label="银行卡号" class="col-span-2">
              <n-input v-model:value="newCard.cardNo" placeholder="请输入银行卡号" />
            </n-form-item>
            <n-form-item label="开户行">
              <n-input v-model:value="newCard.branchName" placeholder="请输入开户行" />
            </n-form-item>
            <n-form-item label="预留手机号">
              <n-input v-model:value="newCard.phone" placeholder="请输入预留手机号" />
            </n-form-item>
          </div>
          <div class="flex justify-end mt-4">
            <n-button type="primary" color="#00AFE1" :loading="addingCard" @click="addBankCard">
              <template #icon><i class="fas fa-plus"></i></template>
              添加银行卡
            </n-button>
          </div>
        </div>
      </div>
    </n-modal>

    <!-- 系统设置弹窗 -->
    <n-modal v-model:show="showSettingsModal" preset="card" title="系统设置" class="max-w-md">
      <div class="space-y-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="font-bold text-white">消息通知</p>
            <p class="text-xs text-gray-400">接收订单和系统通知</p>
          </div>
          <n-switch v-model:value="settings.notification" />
        </div>
        <div class="flex items-center justify-between">
          <div>
            <p class="font-bold text-white">邮件提醒</p>
            <p class="text-xs text-gray-400">重要事项发送邮件提醒</p>
          </div>
          <n-switch v-model:value="settings.emailAlert" />
        </div>
        <div class="flex items-center justify-between">
          <div>
            <p class="font-bold text-white">声音提示</p>
            <p class="text-xs text-gray-400">新消息到达时播放提示音</p>
          </div>
          <n-switch v-model:value="settings.sound" />
        </div>
        <div class="pt-4 border-t border-slate-600">
          <n-button block type="error" ghost @click="handleLogout">
            <template #icon><i class="fas fa-sign-out-alt"></i></template>
            退出登录
          </n-button>
        </div>
      </div>
    </n-modal>

    <!-- Main Content -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- Header -->
      <header class="h-16 bg-slate-800 border-b border-slate-700 flex items-center justify-between px-8">
        <h2 class="font-bold text-white text-lg">
          {{ route.meta.title || '工作台' }}
        </h2>
        <div class="flex items-center gap-4">
          <n-popover trigger="click" placement="bottom-end" :style="{ padding: 0 }" :theme-overrides="popoverTheme">
            <template #trigger>
              <n-badge :value="unreadCount" :max="99" :show="unreadCount > 0">
                <n-button circle size="small" quaternary>
                  <template #icon><i class="far fa-bell text-gray-400"></i></template>
                </n-button>
              </n-badge>
            </template>
            <div class="w-80 max-h-96 flex flex-col bg-slate-800">
              <div class="px-4 py-3 border-b border-slate-700 flex items-center justify-between">
                <span class="font-bold text-white">通知消息</span>
                <n-button text size="tiny" @click="markAllRead" v-if="unreadCount > 0" style="color: #00AFE1;">
                  全部已读
                </n-button>
              </div>
              <div class="flex-1 overflow-y-auto">
                <div v-if="notifications.length === 0" class="py-12 text-center text-gray-500">
                  <i class="far fa-bell-slash text-3xl mb-2"></i>
                  <p class="text-sm">暂无通知</p>
                </div>
                <div v-else>
                  <div 
                    v-for="noti in notifications" 
                    :key="noti.id"
                    @click="handleNotificationClick(noti)"
                    class="px-4 py-3 hover:bg-slate-700 cursor-pointer border-b border-slate-700/50 transition-colors"
                    :class="{ 'bg-slate-700/30': !noti.isRead }"
                  >
                    <div class="flex items-start gap-3">
                      <div 
                        class="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0"
                        :class="getNotificationIconClass(noti.type)"
                      >
                        <i :class="getNotificationIcon(noti.type)"></i>
                      </div>
                      <div class="flex-1 min-w-0">
                        <p class="text-sm font-medium text-white truncate">{{ noti.title }}</p>
                        <p class="text-xs text-gray-400 mt-0.5 line-clamp-2">{{ noti.content }}</p>
                        <p class="text-xs text-gray-500 mt-1">{{ formatTime(noti.createTime) }}</p>
                      </div>
                      <div v-if="!noti.isRead" class="w-2 h-2 bg-cyan-500 rounded-full flex-shrink-0 mt-2"></div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="px-4 py-2 border-t border-slate-700 text-center">
                <n-button text size="small" @click="router.push('/merchant/notifications')" style="color: #00AFE1;">
                  查看全部
                </n-button>
              </div>
            </div>
          </n-popover>
          <div class="flex items-center gap-2 bg-slate-700 px-4 py-2 rounded-lg">
            <i class="fas fa-wallet" style="color: #00AFE1;"></i>
            <span class="text-gray-400 text-xs">可提现:</span>
            <span class="text-white font-bold">¥ {{ userBalance.toLocaleString() }}</span>
          </div>
        </div>
      </header>

      <!-- Page Content -->
      <main class="flex-1 overflow-y-auto p-8 bg-slate-900">
        <router-view></router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, provide, computed, h } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import { logout, updateUserInfo, getBusinessCategories } from '@/api/auth'
import { getWalletInfo, getBankCards, addBankCard as apiAddBankCard, deleteBankCard } from '@/api/wallet'
import { getNotifications, markAsRead, markAllAsRead } from '@/api/notification'
import { getBankOptions } from '@/api/bank'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()
const message = useMessage()

const userBalance = ref(0)

// 通知相关
const notifications = ref([])
const unreadCount = computed(() => notifications.value.filter(n => !n.isRead).length)

// 弹窗控制
const showProfileModal = ref(false)
const showWalletModal = ref(false)
const showSettingsModal = ref(false)
const savingProfile = ref(false)
const addingCard = ref(false)

// 用户菜单选项
const userMenuOptions = [
  {
    label: '个人信息设置',
    key: 'profile',
    icon: () => h('i', { class: 'fas fa-user-edit' })
  },
  {
    label: '钱包与银行卡',
    key: 'wallet',
    icon: () => h('i', { class: 'fas fa-credit-card' })
  },
  {
    label: '系统设置',
    key: 'settings',
    icon: () => h('i', { class: 'fas fa-cog' })
  },
  { type: 'divider' },
  {
    label: '返回用户端',
    key: 'userPortal',
    icon: () => h('i', { class: 'fas fa-home text-cyan-400' })
  },
  { type: 'divider' },
  {
    label: '退出登录',
    key: 'logout',
    icon: () => h('i', { class: 'fas fa-sign-out-alt text-red-400' })
  }
]

// 下拉菜单深色主题
const dropdownTheme = {
  color: '#1e293b',           // slate-800
  optionColorHover: '#334155', // slate-700
  optionTextColor: '#e2e8f0',  // slate-200
  optionTextColorHover: '#00AFE1',
  dividerColor: '#475569',     // slate-600
  borderRadius: '12px',
  boxShadow: '0 10px 40px rgba(0,0,0,0.5)'
}

// Popover深色主题
const popoverTheme = {
  color: '#1e293b',
  borderRadius: '12px',
  boxShadow: '0 10px 40px rgba(0,0,0,0.5)'
}

// 个人信息表单
const profileForm = reactive({
  nickname: '',
  realName: '',
  phone: '',
  email: '',
  businessCategories: null  // 级联选择器使用 null 或数组
})

// 业务分类选项（树形结构）
const businessCategoryOptions = ref([])

// 分类筛选函数
const filterCategories = (pattern, option) => {
  return option.label.toLowerCase().includes(pattern.toLowerCase())
}

// 转换分类数据为级联选择器格式
const transformCategories = (categories) => {
  return categories.map(cat => ({
    value: cat.id,
    label: cat.name,
    children: cat.children && cat.children.length > 0 
      ? transformCategories(cat.children) 
      : undefined
  }))
}

// 银行卡列表
const bankCards = ref([])

// 新增银行卡表单
const newCard = reactive({
  holderName: '',
  bankName: null,
  cardNo: '',
  branchName: '',
  phone: ''
})

// 银行选项（从后端加载）
const bankOptions = ref([])

// 系统设置
const settings = reactive({
  notification: true,
  emailAlert: false,
  sound: true
})

// 处理菜单选择
const handleMenuSelect = (key) => {
  switch (key) {
    case 'profile':
      openProfileModal()
      break
    case 'wallet':
      openWalletModal()
      break
    case 'settings':
      showSettingsModal.value = true
      break
    case 'userPortal':
      // 返回用户端
      window.location.href = '/user/dashboard'
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 打开个人信息弹窗
const openProfileModal = async () => {
  const user = userStore.userInfo
  profileForm.nickname = user.nickname || ''
  profileForm.realName = user.realName || ''
  profileForm.phone = user.phone || ''
  profileForm.email = user.email || ''
  // 解析主营业务（字符串转数组）
  if (user.businessCategories) {
    profileForm.businessCategories = user.businessCategories.split(',').map(Number).filter(n => !isNaN(n))
  } else {
    profileForm.businessCategories = null
  }
  showProfileModal.value = true
  
  // 加载业务分类选项（树形结构）
  try {
    const res = await getBusinessCategories()
    if (res.data) {
      businessCategoryOptions.value = transformCategories(res.data)
    }
  } catch (e) {
    console.error('加载业务分类失败', e)
  }
}

// 保存个人信息
const saveProfile = async () => {
  savingProfile.value = true
  try {
    // 将主营业务数组转为逗号分隔的字符串
    const businessCategoriesStr = profileForm.businessCategories 
      ? profileForm.businessCategories.join(',') 
      : ''
    await updateUserInfo({
      nickname: profileForm.nickname,
      realName: profileForm.realName,
      email: profileForm.email,
      businessCategories: businessCategoriesStr
    })
    userStore.userInfo.nickname = profileForm.nickname
    userStore.userInfo.realName = profileForm.realName
    userStore.userInfo.email = profileForm.email
    userStore.userInfo.businessCategories = businessCategoriesStr
    // 更新存储
    sessionStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
    if (localStorage.getItem('userInfo')) {
      localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
    }
    message.success('保存成功')
    showProfileModal.value = false
  } catch (e) {
    message.error(e.message || '保存失败')
  } finally {
    savingProfile.value = false
  }
}

// 加载银行选项
const loadBankOptions = async () => {
  if (bankOptions.value.length > 0) return
  try {
    const res = await getBankOptions()
    bankOptions.value = res.data || []
  } catch (e) {
    console.error('加载银行列表失败', e)
  }
}

// 打开钱包设置弹窗
const openWalletModal = async () => {
  showWalletModal.value = true
  await Promise.all([loadBankCards(), loadBankOptions()])
}

// 加载银行卡列表
const loadBankCards = async () => {
  try {
    const res = await getBankCards()
    if (res.data) {
      bankCards.value = res.data
    }
  } catch (e) {
    console.error('加载银行卡失败', e)
  }
}

// 添加银行卡
const addBankCard = async () => {
  if (!newCard.holderName || !newCard.bankName || !newCard.cardNo) {
    message.warning('请填写完整的银行卡信息')
    return
  }
  if (!/^\d{16,19}$/.test(newCard.cardNo)) {
    message.warning('请输入正确的银行卡号')
    return
  }
  addingCard.value = true
  try {
    await apiAddBankCard({
      holderName: newCard.holderName,
      bankName: newCard.bankName,
      cardNo: newCard.cardNo,
      branchName: newCard.branchName,
      phone: newCard.phone,
      isDefault: bankCards.value.length === 0
    })
    message.success('银行卡添加成功')
    // 清空表单
    newCard.holderName = ''
    newCard.bankName = null
    newCard.cardNo = ''
    newCard.branchName = ''
    newCard.phone = ''
    // 重新加载
    await loadBankCards()
  } catch (e) {
    message.error(e.message || '添加失败')
  } finally {
    addingCard.value = false
  }
}

// 删除银行卡
const removeCard = async (cardId) => {
  try {
    await deleteBankCard(cardId)
    message.success('删除成功')
    await loadBankCards()
  } catch (e) {
    message.error(e.message || '删除失败')
  }
}

// 加载钱包余额
const loadBalance = async () => {
  try {
    const res = await getWalletInfo()
    if (res.data) {
      userBalance.value = res.data.balance || 0
    }
  } catch (e) {
    console.error('加载余额失败', e)
  }
}

// 提供刷新余额方法给子组件
provide('refreshBalance', loadBalance)

const handleLogout = () => {
  logout().finally(() => {
    userStore.logout()
    message.success('已退出登录')
    router.push('/login')
  })
}

// 加载通知列表
const loadNotifications = async () => {
  try {
    const res = await getNotifications({ limit: 10 })
    notifications.value = res.data || []
  } catch (e) {
    // 静默处理，可能是表未创建或暂无数据
    notifications.value = []
  }
}

// 获取通知图标
const getNotificationIcon = (type) => {
  const icons = {
    message: 'fas fa-comment-dots',
    dispute: 'fas fa-gavel',
    system: 'fas fa-bell',
    order: 'fas fa-file-contract',
    service_message: 'fas fa-headset',
    order_message: 'fas fa-comments'
  }
  return icons[type] || 'fas fa-bell'
}

// 获取通知图标样式（深色主题）
const getNotificationIconClass = (type) => {
  const classes = {
    message: 'bg-blue-500/20 text-blue-400',
    dispute: 'bg-orange-500/20 text-orange-400',
    system: 'bg-gray-500/20 text-gray-400',
    order: 'bg-green-500/20 text-green-400',
    service_message: 'bg-green-500/20 text-green-400',
    order_message: 'bg-cyan-500/20 text-cyan-400'
  }
  return classes[type] || 'bg-gray-500/20 text-gray-400'
}

// 格式化时间
const formatTime = (timeVal) => {
  if (!timeVal) return ''
  let date
  if (typeof timeVal === 'string') {
    date = new Date(timeVal.replace('T', ' '))
  } else if (Array.isArray(timeVal)) {
    date = new Date(timeVal[0], timeVal[1] - 1, timeVal[2], timeVal[3] || 0, timeVal[4] || 0, timeVal[5] || 0)
  } else {
    return ''
  }
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString()
}

// 点击通知
const handleNotificationClick = async (noti) => {
  const notificationType = noti.notificationType || 'system'
  
  // 系统通知标记为已读
  if (notificationType === 'system' && !noti.isRead) {
    try {
      const realId = String(noti.id).replace('sys_', '')
      await markAsRead(realId)
      noti.isRead = true
    } catch (e) {
      console.error('标记已读失败', e)
    }
  }
  
  // 根据类型跳转
  if (notificationType === 'order_message' && noti.targetId) {
    // 订单消息 - 跳转到订单详情页并打开聊天界面
    router.push(`/merchant/orders/${noti.targetId}?openChat=true`)
  } else if (notificationType === 'service_message') {
    // 客服消息 - 目前商家端暂无客服聊天，提示用户
    message.info('请在用户端查看客服消息')
  } else if (noti.targetType === 'order' && noti.targetId) {
    router.push(`/merchant/orders/${noti.targetId}`)
  } else if (noti.targetType === 'dispute' && noti.targetId) {
    router.push(`/merchant/disputes/${noti.targetId}`)
  } else if (noti.targetType === 'order_chat' && noti.targetId) {
    router.push(`/merchant/orders/${noti.targetId}?openChat=true`)
  }
}

// 全部标记已读
const markAllRead = async () => {
  try {
    await markAllAsRead()
    notifications.value.forEach(n => n.isRead = true)
    message.success('已全部标记为已读')
  } catch (e) {
    message.error('操作失败')
  }
}

// 定时器引用
let notificationTimer = null

onMounted(() => {
  loadBalance()
  loadNotifications()
  
  // 每10秒刷新一次通知列表（更实时的消息提醒）
  notificationTimer = setInterval(() => {
    loadNotifications()
  }, 10000)
})

onUnmounted(() => {
  // 清理定时器
  if (notificationTimer) {
    clearInterval(notificationTimer)
    notificationTimer = null
  }
})
</script>

<style scoped>
.nav-active {
  background: rgba(0, 175, 225, 0.2);
  color: #00AFE1;
  font-weight: 700;
}
</style>
