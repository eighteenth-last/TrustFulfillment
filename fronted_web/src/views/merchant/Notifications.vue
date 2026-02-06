<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-white">通知消息</h1>
        <p class="text-gray-400 mt-1">查看所有系统通知和消息提醒</p>
      </div>
      <n-button 
        v-if="unreadCount > 0" 
        type="primary" 
        ghost 
        @click="handleMarkAllRead"
        style="border-color: #00AFE1; color: #00AFE1;"
      >
        <template #icon><i class="fas fa-check-double"></i></template>
        全部标记已读
      </n-button>
    </div>

    <!-- 筛选标签 -->
    <div class="flex gap-2">
      <n-button 
        v-for="tab in tabs" 
        :key="tab.value"
        :type="activeTab === tab.value ? 'primary' : 'default'"
        :color="activeTab === tab.value ? '#00AFE1' : undefined"
        :ghost="activeTab !== tab.value"
        size="small"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
        <n-badge 
          v-if="tab.count > 0" 
          :value="tab.count" 
          :max="99"
          :offset="[10, -5]"
          type="info"
        />
      </n-button>
    </div>

    <!-- 通知列表 -->
    <div class="bg-slate-800 rounded-2xl overflow-hidden">
      <n-spin :show="loading">
        <div v-if="filteredNotifications.length === 0" class="py-20 text-center">
          <i class="far fa-bell-slash text-5xl text-gray-600 mb-4"></i>
          <p class="text-gray-500">暂无通知消息</p>
        </div>
        <div v-else class="divide-y divide-slate-700">
          <div 
            v-for="noti in filteredNotifications" 
            :key="noti.id"
            @click="handleNotificationClick(noti)"
            class="p-5 hover:bg-slate-700/50 cursor-pointer transition-all"
            :class="{ 'bg-slate-700/30': !noti.isRead }"
          >
            <div class="flex items-start gap-4">
              <!-- 图标 -->
              <div 
                class="w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0"
                :class="getNotificationIconClass(noti.type)"
              >
                <i :class="getNotificationIcon(noti.type)" class="text-lg"></i>
              </div>
              
              <!-- 内容 -->
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-3">
                  <h3 class="font-bold text-white">{{ noti.title }}</h3>
                  <n-tag v-if="!noti.isRead" size="tiny" type="info">未读</n-tag>
                </div>
                <p class="text-gray-400 mt-1">{{ noti.content }}</p>
                <div class="flex items-center gap-4 mt-3 text-xs text-gray-500">
                  <span><i class="far fa-clock mr-1"></i>{{ formatTime(noti.createTime) }}</span>
                  <span v-if="noti.targetType">
                    <i class="far fa-folder mr-1"></i>{{ getTargetTypeLabel(noti.targetType) }}
                  </span>
                </div>
              </div>
              
              <!-- 操作 -->
              <div class="flex items-center gap-2">
                <n-button 
                  v-if="!noti.isRead"
                  text 
                  size="small" 
                  @click.stop="handleMarkRead(noti)"
                  style="color: #00AFE1;"
                >
                  标为已读
                </n-button>
                <n-button 
                  text 
                  size="small" 
                  type="error"
                  @click.stop="handleDelete(noti)"
                >
                  <i class="fas fa-trash"></i>
                </n-button>
              </div>
            </div>
          </div>
        </div>
      </n-spin>
    </div>

    <!-- 分页 -->
    <div v-if="total > pageSize" class="flex justify-center">
      <n-pagination 
        v-model:page="currentPage" 
        :page-count="Math.ceil(total / pageSize)"
        @update:page="loadNotifications"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { getNotifications, markAsRead, markAllAsRead, deleteNotification } from '@/api/notification'

const router = useRouter()
const message = useMessage()

const loading = ref(false)
const notifications = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 20
const activeTab = ref('all')

// 筛选标签
const tabs = computed(() => [
  { label: '全部', value: 'all', count: notifications.value.length },
  { label: '未读', value: 'unread', count: notifications.value.filter(n => !n.isRead).length },
  { label: '订单', value: 'order', count: notifications.value.filter(n => n.type === 'order').length },
  { label: '消息', value: 'message', count: notifications.value.filter(n => n.type === 'message').length },
  { label: '系统', value: 'system', count: notifications.value.filter(n => n.type === 'system').length }
])

// 未读数量
const unreadCount = computed(() => notifications.value.filter(n => !n.isRead).length)

// 筛选后的通知
const filteredNotifications = computed(() => {
  if (activeTab.value === 'all') return notifications.value
  if (activeTab.value === 'unread') return notifications.value.filter(n => !n.isRead)
  return notifications.value.filter(n => n.type === activeTab.value)
})

// 加载通知列表
const loadNotifications = async () => {
  loading.value = true
  try {
    const res = await getNotifications({ 
      limit: pageSize,
      offset: (currentPage.value - 1) * pageSize 
    })
    notifications.value = res.data || []
    total.value = res.total || notifications.value.length
  } catch (e) {
    console.error('加载通知失败', e)
    notifications.value = []
  } finally {
    loading.value = false
  }
}

// 获取通知图标
const getNotificationIcon = (type) => {
  const icons = {
    message: 'fas fa-comment-dots',
    dispute: 'fas fa-gavel',
    system: 'fas fa-bell',
    order: 'fas fa-file-contract'
  }
  return icons[type] || 'fas fa-bell'
}

// 获取通知图标样式
const getNotificationIconClass = (type) => {
  const classes = {
    message: 'bg-blue-500/20 text-blue-400',
    dispute: 'bg-orange-500/20 text-orange-400',
    system: 'bg-gray-500/20 text-gray-400',
    order: 'bg-green-500/20 text-green-400'
  }
  return classes[type] || 'bg-gray-500/20 text-gray-400'
}

// 获取目标类型标签
const getTargetTypeLabel = (type) => {
  const labels = {
    order: '订单相关',
    dispute: '纠纷相关',
    system: '系统通知',
    message: '消息'
  }
  return labels[type] || type
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
  if (!noti.isRead) {
    try {
      await markAsRead(noti.id)
      noti.isRead = true
    } catch (e) {
      console.error('标记已读失败', e)
    }
  }
  if (noti.targetType === 'order' && noti.targetId) {
    router.push(`/merchant/orders/${noti.targetId}`)
  } else if (noti.targetType === 'dispute' && noti.targetId) {
    router.push(`/merchant/disputes/${noti.targetId}`)
  }
}

// 标记单条已读
const handleMarkRead = async (noti) => {
  try {
    await markAsRead(noti.id)
    noti.isRead = true
    message.success('已标记为已读')
  } catch (e) {
    message.error('操作失败')
  }
}

// 全部标记已读
const handleMarkAllRead = async () => {
  try {
    await markAllAsRead()
    notifications.value.forEach(n => n.isRead = true)
    message.success('已全部标记为已读')
  } catch (e) {
    message.error('操作失败')
  }
}

// 删除通知
const handleDelete = async (noti) => {
  try {
    await deleteNotification(noti.id)
    const index = notifications.value.findIndex(n => n.id === noti.id)
    if (index > -1) {
      notifications.value.splice(index, 1)
    }
    message.success('已删除')
  } catch (e) {
    message.error('删除失败')
  }
}

onMounted(() => {
  loadNotifications()
})
</script>
