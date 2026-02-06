<template>
  <div class="space-y-6">
    <!-- 筛选栏 -->
    <div class="bg-white p-4 rounded-2xl border border-gray-100 flex items-center justify-between">
      <div class="flex items-center gap-4">
        <n-input v-model:value="searchKeyword" placeholder="搜索通知标题/内容" class="w-64">
          <template #prefix><i class="fas fa-search text-gray-400"></i></template>
        </n-input>
        <n-select 
          v-model:value="filterType" 
          :options="typeOptions"
          class="w-36"
          placeholder="全部类型"
          clearable
        />
      </div>
      <n-button type="primary" color="#00AFE1" @click="showAddModal = true">
        <template #icon><i class="fas fa-plus"></i></template>
        发送通知
      </n-button>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="bg-white p-5 rounded-xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">通知总数</p>
        <p class="text-2xl font-black text-gray-800 mt-1">{{ stats.total }}</p>
      </div>
      <div class="bg-white p-5 rounded-xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">今日发送</p>
        <p class="text-2xl font-black text-green-500 mt-1">{{ stats.todaySent }}</p>
      </div>
      <div class="bg-white p-5 rounded-xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">已读</p>
        <p class="text-2xl font-black text-blue-500 mt-1">{{ stats.read }}</p>
      </div>
      <div class="bg-white p-5 rounded-xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">未读</p>
        <p class="text-2xl font-black text-orange-500 mt-1">{{ stats.unread }}</p>
      </div>
    </div>

    <!-- 通知列表 -->
    <div class="bg-white rounded-2xl border border-gray-100 overflow-hidden">
      <table class="w-full text-left">
        <thead class="bg-gray-50 border-b border-gray-100">
          <tr class="text-xs text-gray-500 uppercase">
            <th class="px-6 py-4 font-bold">类型</th>
            <th class="px-6 py-4 font-bold">标题</th>
            <th class="px-6 py-4 font-bold">接收用户</th>
            <th class="px-6 py-4 font-bold text-center">已读</th>
            <th class="px-6 py-4 font-bold">发送时间</th>
            <th class="px-6 py-4 font-bold text-center">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="noti in notifications" :key="noti.id" class="hover:bg-gray-50 transition">
            <td class="px-6 py-5">
              <span class="text-xs font-bold px-2 py-1 rounded" :class="getTypeClass(noti.type)">
                {{ getTypeText(noti.type) }}
              </span>
            </td>
            <td class="px-6 py-5">
              <p class="font-bold text-gray-800">{{ noti.title }}</p>
              <p class="text-xs text-gray-400 mt-1 truncate max-w-xs">{{ noti.content }}</p>
            </td>
            <td class="px-6 py-5 text-sm text-gray-600">{{ noti.userName || `用户${noti.userId}` }}</td>
            <td class="px-6 py-5 text-center">
              <span v-if="noti.isRead" class="text-green-500"><i class="fas fa-check-circle"></i></span>
              <span v-else class="text-gray-300"><i class="fas fa-circle"></i></span>
            </td>
            <td class="px-6 py-5 text-sm text-gray-500">{{ formatDateTime(noti.createTime) }}</td>
            <td class="px-6 py-5 text-center">
              <n-button size="small" ghost @click="viewNotification(noti)">详情</n-button>
              <n-button size="small" type="error" ghost class="ml-2" @click="deleteNotification(noti)">删除</n-button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="p-4 border-t border-gray-100 flex justify-center">
        <n-pagination 
          v-model:page="currentPage" 
          :page-count="totalPages"
          @update:page="handlePageChange"
        />
      </div>
    </div>

    <!-- 发送通知弹窗 -->
    <n-modal v-model:show="showAddModal" preset="card" title="发送通知" style="width: 600px;">
      <n-form :model="formData" label-placement="top">
        <n-form-item label="通知类型">
          <n-select v-model:value="formData.type" :options="typeOptions.filter(o => o.value)" />
        </n-form-item>
        <n-form-item label="接收用户">
          <div class="w-full">
            <!-- 已选用户标签 -->
            <div v-if="selectedUsers.length > 0" class="flex flex-wrap gap-2 mb-3">
              <n-tag 
                v-for="user in selectedUsers" 
                :key="user.id" 
                closable 
                @close="removeSelectedUser(user)"
                type="info"
              >
                {{ user.displayName }}
              </n-tag>
            </div>
            <div v-else class="text-xs text-gray-400 mb-2">
              <i class="fas fa-info-circle mr-1"></i>不选择用户则发送给所有用户
            </div>
            <!-- 搜索输入框 -->
            <n-input 
              v-model:value="userSearchKeyword" 
              placeholder="输入用户名/手机号搜索..."
              @input="handleUserSearch"
              clearable
            >
              <template #prefix><i class="fas fa-search text-gray-400"></i></template>
            </n-input>
            <!-- 搜索结果下拉 -->
            <div v-if="userSearchResults.length > 0" class="mt-2 border border-gray-200 rounded-lg max-h-48 overflow-y-auto">
              <div 
                v-for="user in userSearchResults" 
                :key="user.id"
                class="px-3 py-2 hover:bg-gray-50 cursor-pointer flex items-center justify-between"
                @click="selectUser(user)"
              >
                <div>
                  <span class="font-medium">{{ user.displayName }}</span>
                  <span v-if="user.phone" class="text-xs text-gray-400 ml-2">{{ user.phone }}</span>
                </div>
                <i v-if="isUserSelected(user.id)" class="fas fa-check text-green-500"></i>
              </div>
            </div>
          </div>
        </n-form-item>
        <n-form-item label="通知标题">
          <n-input v-model:value="formData.title" placeholder="请输入通知标题" />
        </n-form-item>
        <n-form-item label="通知内容">
          <n-input v-model:value="formData.content" type="textarea" :rows="4" placeholder="请输入通知内容" />
        </n-form-item>
      </n-form>
      <template #footer>
        <div class="flex gap-3 justify-end">
          <n-button @click="closeAddModal">取消</n-button>
          <n-button type="primary" color="#00AFE1" @click="sendNotification">发送</n-button>
        </div>
      </template>
    </n-modal>

    <!-- 详情弹窗 -->
    <n-modal v-model:show="showDetailModal" preset="card" title="通知详情" style="width: 600px;">
      <div v-if="currentNotification" class="space-y-4">
        <div class="p-4 bg-gray-50 rounded-xl">
          <p class="text-xs text-gray-400 mb-1">通知标题</p>
          <p class="font-bold text-lg">{{ currentNotification.title }}</p>
        </div>
        <div class="p-4 bg-gray-50 rounded-xl">
          <p class="text-xs text-gray-400 mb-1">通知内容</p>
          <p class="text-gray-700 whitespace-pre-wrap">{{ currentNotification.content }}</p>
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-400 mb-1">发送时间</p>
            <p class="font-bold">{{ formatDateTime(currentNotification.createTime) }}</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-400 mb-1">阅读时间</p>
            <p class="font-bold">{{ currentNotification.readTime ? formatDateTime(currentNotification.readTime) : '未读' }}</p>
          </div>
        </div>
      </div>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useMessage, useDialog } from 'naive-ui'
import { getNotificationList, getNotificationStats, sendNotification as apiSendNotification, deleteNotification as apiDeleteNotification, searchUsers } from '@/api/admin'

const message = useMessage()
const dialog = useDialog()

const searchKeyword = ref('')
const filterType = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const showAddModal = ref(false)
const showDetailModal = ref(false)
const currentNotification = ref(null)

// 用户搜索相关
const userSearchKeyword = ref('')
const userSearchResults = ref([])
const selectedUsers = ref([])
let searchTimer = null

const typeOptions = [
  { label: '系统通知', value: 'system' },
  { label: '订单通知', value: 'order' },
  { label: '消息通知', value: 'message' },
  { label: '纠纷通知', value: 'dispute' }
]

const stats = reactive({ total: 0, todaySent: 0, read: 0, unread: 0 })
const notifications = ref([])

const formData = reactive({
  type: 'system',
  userIds: [],
  title: '',
  content: ''
})

const loadStats = async () => {
  try {
    const res = await getNotificationStats()
    if (res.data) Object.assign(stats, res.data)
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

const loadNotifications = async () => {
  try {
    const res = await getNotificationList({
      page: currentPage.value,
      size: pageSize.value,
      type: filterType.value,
      keyword: searchKeyword.value || undefined
    })
    if (res.data && res.data.records) {
      notifications.value = res.data.records
      totalPages.value = Math.max(1, Math.ceil(res.data.total / pageSize.value))
    }
  } catch (e) {
    console.error('加载通知列表失败', e)
  }
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getTypeText = (type) => {
  const map = { system: '系统', order: '订单', message: '消息', dispute: '纠纷' }
  return map[type] || type
}

const getTypeClass = (type) => {
  const map = {
    system: 'bg-purple-100 text-purple-500',
    order: 'bg-blue-100 text-blue-500',
    message: 'bg-green-100 text-green-500',
    dispute: 'bg-red-100 text-red-500'
  }
  return map[type] || 'bg-gray-100 text-gray-500'
}

const viewNotification = (noti) => {
  currentNotification.value = noti
  showDetailModal.value = true
}

// 搜索用户（防抖）
const handleUserSearch = () => {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(async () => {
    if (!userSearchKeyword.value || userSearchKeyword.value.length < 1) {
      userSearchResults.value = []
      return
    }
    try {
      const res = await searchUsers(userSearchKeyword.value)
      if (res.data) {
        userSearchResults.value = res.data
      }
    } catch (e) {
      console.error('搜索用户失败', e)
    }
  }, 300)
}

// 选择用户
const selectUser = (user) => {
  if (!isUserSelected(user.id)) {
    selectedUsers.value.push(user)
  }
  userSearchKeyword.value = ''
  userSearchResults.value = []
}

// 移除已选用户
const removeSelectedUser = (user) => {
  selectedUsers.value = selectedUsers.value.filter(u => u.id !== user.id)
}

// 检查用户是否已选中
const isUserSelected = (userId) => {
  return selectedUsers.value.some(u => u.id === userId)
}

// 关闭弹窗并重置
const closeAddModal = () => {
  showAddModal.value = false
  resetForm()
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, { type: 'system', userIds: [], title: '', content: '' })
  selectedUsers.value = []
  userSearchKeyword.value = ''
  userSearchResults.value = []
}

const sendNotification = async () => {
  if (!formData.title || !formData.content) {
    message.warning('请填写完整信息')
    return
  }
  try {
    // 构建发送数据
    const sendData = {
      type: formData.type,
      title: formData.title,
      content: formData.content,
      userIds: selectedUsers.value.map(u => u.id)
    }
    
    await apiSendNotification(sendData)
    message.success(selectedUsers.value.length > 0 
      ? `已发送给 ${selectedUsers.value.length} 位用户` 
      : '已发送给所有用户')
    closeAddModal()
    loadNotifications()
    loadStats()
  } catch (e) {
    message.error('发送失败')
  }
}

const deleteNotification = (noti) => {
  dialog.warning({
    title: '确认删除',
    content: '确定要删除这条通知吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await apiDeleteNotification(noti.id)
        message.success('删除成功')
        loadNotifications()
        loadStats()
      } catch (e) {
        message.error('删除失败')
      }
    }
  })
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadNotifications()
}

watch([searchKeyword, filterType], () => {
  currentPage.value = 1
  loadNotifications()
})

onMounted(() => {
  loadStats()
  loadNotifications()
})
</script>
