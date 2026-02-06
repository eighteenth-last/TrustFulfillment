<template>
  <div class="flex h-screen bg-gray-50">
    <!-- Sidebar -->
    <div class="w-64 bg-white border-r border-gray-100 flex flex-col h-screen">
      <div class="p-6 flex items-center gap-3 flex-shrink-0">
        <img src="/logo.png" alt="臻托" class="w-10 h-10 rounded-xl object-cover shadow-lg" />
        <span class="font-black text-xl text-gray-800 tracking-tight">臻托 TF</span>
      </div>

      <nav class="flex-1 px-4 space-y-1 overflow-y-auto pb-4">
        <router-link to="/admin/dashboard" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'bg-tf/10 text-tf font-bold' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-chart-pie w-5 text-center"></i>
            <span>平台总览</span>
          </a>
        </router-link>

        <router-link to="/admin/orders" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'bg-tf/10 text-tf font-bold' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-tasks w-5 text-center"></i>
            <span>任务审批</span>
          </a>
        </router-link>

        <router-link to="/admin/trust" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'bg-tf/10 text-tf font-bold' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-vault w-5 text-center"></i>
            <span>信托资金</span>
          </a>
        </router-link>

        <router-link to="/admin/disputes" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'bg-tf/10 text-tf font-bold' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-gavel w-5 text-center"></i>
            <span>纠纷调处</span>
            <span v-if="disputeCount > 0" class="ml-auto bg-red-500 text-white text-xs px-2 py-0.5 rounded-full">{{ disputeCount }}</span>
          </a>
        </router-link>

        <router-link to="/admin/merchants" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'bg-tf/10 text-tf font-bold' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-store w-5 text-center"></i>
            <span>商家审核</span>
          </a>
        </router-link>

        <router-link to="/admin/users" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'bg-tf/10 text-tf font-bold' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-users w-5 text-center"></i>
            <span>用户管理</span>
          </a>
        </router-link>

        <router-link to="/admin/risk" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'bg-tf/10 text-tf font-bold' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-shield-virus w-5 text-center"></i>
            <span>风险监控</span>
          </a>
        </router-link>

        <router-link to="/admin/customer-service" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'bg-tf/10 text-tf font-bold' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-headset w-5 text-center"></i>
            <span>客户解答</span>
            <span v-if="unreadMessageCount > 0" class="ml-auto bg-red-500 text-white text-xs px-2 py-0.5 rounded-full">{{ unreadMessageCount }}</span>
          </a>
        </router-link>
        
        <router-link to="/admin/contracts" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'bg-tf/10 text-tf font-bold' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-file-contract w-5 text-center"></i>
            <span>合同管理</span>
          </a>
        </router-link>

        <router-link to="/admin/banks" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'bg-tf/10 text-tf font-bold' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-university w-5 text-center"></i>
            <span>银行信息</span>
          </a>
        </router-link>

        <router-link to="/admin/notifications" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'bg-tf/10 text-tf font-bold' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-bell w-5 text-center"></i>
            <span>系统通知</span>
          </a>
        </router-link>

        <router-link to="/admin/categories" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'bg-tf/10 text-tf font-bold' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-tags w-5 text-center"></i>
            <span>业务分类</span>
          </a>
        </router-link>
        
        <router-link to="/admin/commissions" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'bg-tf/10 text-tf font-bold' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-coins w-5 text-center"></i>
            <span>提成管理</span>
          </a>
        </router-link>
      </nav>

      <div class="p-4 border-t border-gray-100 flex-shrink-0 bg-white">
        <n-dropdown trigger="click" :options="profileOptions" @select="handleProfileSelect" placement="top-start">
          <div class="flex items-center gap-3 p-2 rounded-xl hover:bg-gray-50 cursor-pointer">
            <div class="w-10 h-10 bg-tf rounded-full flex items-center justify-center text-white">
              <i class="fas fa-user-shield"></i>
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-bold truncate">{{ userStore.userInfo.username || '管理员' }}</p>
              <p class="text-xs text-gray-400 truncate">超级管理员</p>
            </div>
            <i class="fas fa-chevron-up text-gray-400"></i>
          </div>
        </n-dropdown>
      </div>
    </div>

    <!-- Main Content -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- Header -->
      <header class="h-16 bg-white border-b border-gray-100 flex items-center justify-between px-8">
        <h2 class="text-lg font-bold text-gray-800">
          {{ route.meta.title || '平台总览' }}
        </h2>
        <div class="flex items-center gap-4">
          <n-popover trigger="click" placement="bottom">
            <template #trigger>
              <n-badge :value="notificationCount" :max="99">
                <n-button circle size="small" quaternary>
                  <template #icon><i class="far fa-bell"></i></template>
                </n-button>
              </n-badge>
            </template>
            <div class="w-80">
              <div class="font-bold mb-3">通知消息</div>
              <div class="space-y-3 max-h-64 overflow-y-auto">
                <div v-for="(noti, i) in notifications" :key="i" 
                     class="p-3 bg-gray-50 rounded-lg text-sm hover:bg-gray-100 cursor-pointer flex items-start gap-3"
                     @click="handleNotificationClick(noti)">
                  <div class="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0" 
                       :class="getNotificationIconClass(noti.type)">
                    <i :class="getNotificationIcon(noti.type)"></i>
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="font-medium">{{ noti.title }}</p>
                    <p class="text-xs text-gray-400 mt-1">{{ noti.time }}</p>
                  </div>
                </div>
                <div v-if="notifications.length === 0" class="text-center text-gray-400 py-4">
                  暂无通知
                </div>
              </div>
              <div class="mt-3 pt-3 border-t text-center">
                <n-button text type="primary" size="small" @click="router.push('/admin/notifications')">查看全部</n-button>
              </div>
            </div>
          </n-popover>
          <n-button circle size="small" quaternary @click="router.push('/admin/settings')">
            <template #icon><i class="fas fa-cog"></i></template>
          </n-button>
        </div>
      </header>

      <!-- Page Content -->
      <main class="flex-1 overflow-y-auto bg-gray-50" :class="route.name === 'customerService' ? 'p-0' : 'p-8'">
        <router-view></router-view>
      </main>
    </div>
  </div>

  <!-- 个人信息弹窗 -->
  <n-modal v-model:show="showProfileModal" preset="card" title="修改个人信息" style="width: 450px;">
    <n-form label-placement="left" label-width="80">
      <n-form-item label="用户名">
        <n-input v-model:value="profileForm.username" placeholder="请输入用户名" />
      </n-form-item>
      <n-form-item label="手机号">
        <n-input v-model:value="profileForm.phone" placeholder="请输入手机号" />
      </n-form-item>
      <n-form-item label="邮箱">
        <n-input v-model:value="profileForm.email" placeholder="请输入邮箱" />
      </n-form-item>
    </n-form>
    <template #footer>
      <div class="flex justify-end gap-3">
        <n-button @click="showProfileModal = false">取消</n-button>
        <n-button type="primary" color="#00AFE1" @click="saveProfile">保存</n-button>
      </div>
    </template>
  </n-modal>

  <!-- 修改密码弹窗 -->
  <n-modal v-model:show="showPasswordModal" preset="card" title="修改密码" style="width: 450px;">
    <n-form label-placement="left" label-width="100">
      <n-form-item label="原密码">
        <n-input v-model:value="passwordForm.oldPassword" type="password" show-password-on="click" placeholder="请输入原密码" />
      </n-form-item>
      <n-form-item label="新密码">
        <n-input v-model:value="passwordForm.newPassword" type="password" show-password-on="click" placeholder="请输入新密码" />
      </n-form-item>
      <n-form-item label="确认新密码">
        <n-input v-model:value="passwordForm.confirmPassword" type="password" show-password-on="click" placeholder="请再次输入新密码" />
      </n-form-item>
    </n-form>
    <template #footer>
      <div class="flex justify-end gap-3">
        <n-button @click="showPasswordModal = false">取消</n-button>
        <n-button type="primary" color="#00AFE1" @click="changePassword">确认修改</n-button>
      </div>
    </template>
  </n-modal>
</template>

<script setup>
import { ref, computed, onMounted, h } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter, useRoute } from 'vue-router'
import { useMessage, useDialog } from 'naive-ui'
import { logout, updateAdminProfile, changeAdminPassword } from '@/api/auth'
import { getDisputeStats, getUnreadMessageCount, getMerchantStats, getRiskAlerts } from '@/api/admin'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()
const message = useMessage()
const dialog = useDialog()

const disputeCount = ref(0)
const unreadMessageCount = ref(0)

// 动态通知列表
const notifications = ref([])

const notificationCount = computed(() => notifications.value.length)

// 通知点击跳转
const handleNotificationClick = (noti) => {
  if (noti.route) {
    router.push(noti.route)
  }
}

// 获取通知图标
const getNotificationIcon = (type) => {
  const iconMap = {
    risk: 'fas fa-shield-virus text-white',
    dispute: 'fas fa-gavel text-white',
    merchant: 'fas fa-store text-white',
    service: 'fas fa-headset text-white'
  }
  return iconMap[type] || 'fas fa-bell text-white'
}

// 获取通知图标背景色
const getNotificationIconClass = (type) => {
  const classMap = {
    risk: 'bg-red-500',
    dispute: 'bg-orange-500',
    merchant: 'bg-blue-500',
    service: 'bg-green-500'
  }
  return classMap[type] || 'bg-gray-500'
}

// 加载待处理通知
const loadPendingNotifications = async () => {
  const pendingList = []
  
  try {
    // 1. 检查待处理纠纷
    const disputeRes = await getDisputeStats()
    if (disputeRes.data) {
      const pending = (disputeRes.data.pending || 0) + (disputeRes.data.processing || 0)
      disputeCount.value = pending
      if (pending > 0) {
        pendingList.push({
          title: `${pending} 条纠纷待处理`,
          time: '待处理',
          type: 'dispute',
          route: '/admin/disputes'
        })
      }
    }

    // 2. 检查未读客服消息
    const msgRes = await getUnreadMessageCount()
    if (msgRes.data !== undefined) {
      unreadMessageCount.value = msgRes.data
      if (msgRes.data > 0) {
        pendingList.push({
          title: `${msgRes.data} 条客户咨询待回复`,
          time: '待处理',
          type: 'service',
          route: '/admin/customer-service'
        })
      }
    }

    // 3. 检查待审核商家
    const merchantRes = await getMerchantStats()
    if (merchantRes.data && merchantRes.data.pending > 0) {
      pendingList.push({
        title: `${merchantRes.data.pending} 个商家待审核`,
        time: '待处理',
        type: 'merchant',
        route: '/admin/merchants'
      })
    }

    // 4. 检查风险预警
    const riskRes = await getRiskAlerts()
    if (riskRes.data && riskRes.data.length > 0) {
      const unprocessed = riskRes.data.filter(r => r.status === 'pending' || r.status === 0).length
      if (unprocessed > 0) {
        pendingList.push({
          title: `${unprocessed} 条风险预警待处理`,
          time: '待处理',
          type: 'risk',
          route: '/admin/risk'
        })
      }
    }
  } catch (e) {
    console.error('加载通知数据失败', e)
  }

  notifications.value = pendingList
}

onMounted(() => {
  loadPendingNotifications()
  // 每60秒刷新一次
  setInterval(loadPendingNotifications, 60000)
})

// 下拉菜单选项
const profileOptions = [
  { label: '修改个人信息', key: 'profile', icon: () => h('i', { class: 'fas fa-user-edit mr-2' }) },
  { label: '修改密码', key: 'password', icon: () => h('i', { class: 'fas fa-key mr-2' }) },
  { type: 'divider', key: 'd1' },
  { label: '退出登录', key: 'logout', icon: () => h('i', { class: 'fas fa-sign-out-alt mr-2 text-red-500' }) }
]

// 个人信息弹窗
const showProfileModal = ref(false)
const showPasswordModal = ref(false)
const profileForm = ref({
  username: '',
  phone: '',
  email: ''
})
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 处理下拉菜单选择
const handleProfileSelect = (key) => {
  if (key === 'profile') {
    profileForm.value = {
      username: userStore.userInfo.username || '',
      phone: userStore.userInfo.phone || '',
      email: userStore.userInfo.email || ''
    }
    showProfileModal.value = true
  } else if (key === 'password') {
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    showPasswordModal.value = true
  } else if (key === 'logout') {
    handleLogout()
  }
}

// 保存个人信息
const saveProfile = async () => {
  try {
    await updateAdminProfile({
      nickname: profileForm.value.username,
      phone: profileForm.value.phone,
      email: profileForm.value.email
    })
    message.success('个人信息更新成功')
    userStore.setUserInfo({ ...userStore.userInfo, ...profileForm.value })
    showProfileModal.value = false
  } catch (e) {
    message.error(e.message || '更新失败')
  }
}

// 修改密码
const changePassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    message.error('两次输入的密码不一致')
    return
  }
  try {
    await changeAdminPassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword,
      confirmPassword: passwordForm.value.confirmPassword
    })
    message.success('密码修改成功，请重新登录')
    showPasswordModal.value = false
    userStore.logout()
    router.push('/login')
  } catch (e) {
    message.error(e.message || '密码修改失败')
  }
}

const handleLogout = () => {
  dialog.warning({
    title: '确认退出',
    content: '确定要退出登录吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: () => {
      logout().catch(() => {}).finally(() => {
        userStore.logout()
        message.success('已退出登录')
        router.push('/login')
      })
    }
  })
}
</script>
