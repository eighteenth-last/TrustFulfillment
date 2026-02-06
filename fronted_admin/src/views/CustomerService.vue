<template>
  <div class="h-[calc(100vh-4rem)] flex bg-white overflow-hidden">
    <!-- 左侧会话列表 -->
    <div class="w-80 border-r border-gray-100 flex flex-col bg-gray-50">
      <!-- 搜索栏 -->
      <div class="p-4 border-b border-gray-100 bg-white space-y-3">
        <div class="flex items-center gap-2">
          <n-input v-model:value="searchKeyword" placeholder="搜索用户" clearable class="flex-1">
            <template #prefix><i class="fas fa-search text-gray-400"></i></template>
          </n-input>
          <n-button type="primary" color="#00AFE1" size="small" @click="showNewChat = true">
            <template #icon><i class="fas fa-plus"></i></template>
          </n-button>
        </div>
      </div>

      <!-- 会话列表 -->
      <div class="flex-1 overflow-y-auto">
        <div v-if="conversations.length === 0" class="flex flex-col items-center justify-center h-full text-gray-400">
          <i class="fas fa-comments text-4xl mb-3"></i>
          <p class="text-sm">暂无会话</p>
        </div>
        <div
          v-for="conv in filteredConversations"
          :key="conv.userId"
          @click="selectConversation(conv)"
          class="flex items-center gap-3 p-4 cursor-pointer transition-all border-b border-gray-100"
          :class="currentConv?.userId === conv.userId ? 'bg-white' : 'hover:bg-white'"
        >
          <div class="relative">
            <div class="w-12 h-12 bg-gray-200 rounded-full flex items-center justify-center text-gray-500 overflow-hidden">
              <img v-if="conv.avatar" :src="conv.avatar" class="w-full h-full object-cover" />
              <i v-else class="fas fa-user text-xl"></i>
            </div>
            <span
              v-if="conv.unreadCount > 0"
              class="absolute -top-1 -right-1 min-w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center px-1"
            >
              {{ conv.unreadCount > 99 ? '99+' : conv.unreadCount }}
            </span>
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center justify-between">
              <span class="font-bold text-gray-800 truncate">{{ conv.nickname || conv.phone }}</span>
              <span class="text-xs text-gray-400">{{ formatTime(conv.lastMessageTime) }}</span>
            </div>
            <p class="text-sm text-gray-500 truncate mt-1">{{ conv.lastMessage || '暂无消息' }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧聊天区域 -->
    <div class="flex-1 flex flex-col">
      <!-- 聊天头部 -->
      <div v-if="currentConv" class="h-16 px-6 border-b border-gray-100 flex items-center justify-between bg-white">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 bg-gray-200 rounded-full flex items-center justify-center overflow-hidden">
            <img v-if="currentConv.avatar" :src="currentConv.avatar" class="w-full h-full object-cover" />
            <i v-else class="fas fa-user text-gray-500"></i>
          </div>
          <div>
            <p class="font-bold text-gray-800">{{ currentConv.nickname || currentConv.phone }}</p>
            <p class="text-xs text-gray-400">{{ currentConv.phone }}</p>
          </div>
        </div>
        <div class="flex items-center gap-2">
          <n-button quaternary circle size="small" @click="viewUserDetail">
            <template #icon><i class="fas fa-user-circle text-gray-500"></i></template>
          </n-button>
        </div>
      </div>

      <!-- 消息列表 -->
      <div
        ref="messageListRef"
        class="flex-1 overflow-y-auto p-6 space-y-4"
        :class="currentConv ? 'bg-gray-50' : 'bg-white'"
      >
        <div v-if="!currentConv" class="h-full flex flex-col items-center justify-center text-gray-400">
          <i class="fas fa-comment-dots text-6xl mb-4"></i>
          <p class="text-lg">选择一个会话开始聊天</p>
        </div>

        <template v-else>
          <div v-if="messages.length === 0" class="h-full flex flex-col items-center justify-center text-gray-400">
            <i class="fas fa-paper-plane text-4xl mb-3"></i>
            <p>开始与用户对话</p>
          </div>

          <div
            v-for="msg in messages"
            :key="msg.id"
            class="flex"
            :class="msg.senderRole == 2 ? 'justify-end' : 'justify-start'"
          >
            <!-- 用户消息（左侧） -->
            <div v-if="msg.senderRole == 1" class="flex items-start gap-3 max-w-[70%]">
              <div class="w-10 h-10 bg-gray-200 rounded-full flex-shrink-0 flex items-center justify-center overflow-hidden">
                <img v-if="currentConv.avatar" :src="currentConv.avatar" class="w-full h-full object-cover" />
                <i v-else class="fas fa-user text-gray-500"></i>
              </div>
              <div>
                <div class="bg-white rounded-2xl rounded-tl-sm px-4 py-3 shadow-sm">
                  <p v-if="msg.type == 1 || !msg.type" class="text-gray-800 break-words whitespace-pre-wrap">{{ msg.content }}</p>
                  <img v-else-if="msg.type == 2" :src="msg.content" class="max-w-xs rounded-lg cursor-pointer" @click="previewImage(msg.content)" />
                  <a v-else-if="msg.type == 3" :href="msg.content" target="_blank" class="text-blue-500 underline">
                    <i class="fas fa-file mr-1"></i>查看文件
                  </a>
                  <p v-else class="text-gray-800 break-words whitespace-pre-wrap">{{ msg.content }}</p>
                </div>
                <p class="text-xs text-gray-400 mt-1 ml-2">{{ formatMessageTime(msg.createTime) }}</p>
              </div>
            </div>

            <!-- 管理员消息（右侧） -->
            <div v-else class="flex items-start gap-3 max-w-[70%] flex-row-reverse">
              <div class="w-10 h-10 rounded-full flex-shrink-0 flex items-center justify-center text-white" style="background-color: #00AFE1;">
                <i class="fas fa-headset"></i>
              </div>
              <div>
                <div class="text-white rounded-2xl rounded-tr-sm px-4 py-3 shadow-sm" style="background-color: #00AFE1;">
                  <p v-if="msg.type == 1 || !msg.type" class="break-words whitespace-pre-wrap">{{ msg.content }}</p>
                  <img v-else-if="msg.type == 2" :src="msg.content" class="max-w-xs rounded-lg cursor-pointer" @click="previewImage(msg.content)" />
                  <a v-else-if="msg.type == 3" :href="msg.content" target="_blank" class="text-white underline">
                    <i class="fas fa-file mr-1"></i>查看文件
                  </a>
                  <p v-else class="break-words whitespace-pre-wrap">{{ msg.content }}</p>
                </div>
                <p class="text-xs text-gray-400 mt-1 text-right">{{ formatMessageTime(msg.createTime) }}</p>
              </div>
            </div>
          </div>
        </template>
      </div>

      <!-- 输入区域 -->
      <div v-if="currentConv" class="border-t border-gray-200 bg-gray-50">
        <!-- 工具栏 -->
        <div class="px-4 py-2 border-b border-gray-200">
          <div class="flex items-center gap-1" style="width: fit-content;">
            <n-upload
              :show-file-list="false"
              @change="handleImageUpload"
              accept="image/*"
              style="width: auto;"
            >
              <div class="w-8 h-8 flex items-center justify-center rounded hover:bg-gray-200 cursor-pointer transition">
                <i class="far fa-image text-gray-500 text-lg"></i>
              </div>
            </n-upload>
            <n-upload
              :show-file-list="false"
              @change="handleFileUpload"
              style="width: auto;"
            >
              <div class="w-8 h-8 flex items-center justify-center rounded hover:bg-gray-200 cursor-pointer transition">
                <i class="far fa-folder text-gray-500 text-lg"></i>
              </div>
            </n-upload>
          </div>
        </div>

        <!-- 输入框区域 -->
        <div class="p-3">
          <textarea
            v-model="inputMessage"
            placeholder="输入消息，按 Enter 发送，Shift+Enter 换行"
            @keydown="handleKeydown"
            class="w-full h-24 resize-none border-none outline-none bg-transparent text-gray-800 text-sm leading-relaxed"
          ></textarea>
        </div>

        <!-- 发送按钮 -->
        <div class="px-4 pb-3 flex justify-end">
          <button 
            :disabled="!inputMessage.trim()" 
            @click="sendMessage"
            class="px-4 py-1.5 text-sm rounded transition"
            :class="inputMessage.trim() ? 'bg-[#00AFE1] text-white hover:bg-[#0099c8] cursor-pointer' : 'bg-gray-200 text-gray-400 cursor-not-allowed'"
          >
            发送(S)
          </button>
        </div>
      </div>
    </div>

    <!-- 图片预览 -->
    <n-modal v-model:show="showImagePreview" preset="card" style="width: auto; max-width: 90vw;">
      <img :src="previewImageUrl" class="max-w-full max-h-[80vh] object-contain" />
    </n-modal>

    <!-- 发起新会话 -->
    <n-modal v-model:show="showNewChat" preset="card" title="发起新会话" style="width: 480px;">
      <div class="space-y-4">
        <n-input 
          v-model:value="userSearchKeyword" 
          placeholder="搜索用户昵称/手机号..."
          @input="handleUserSearch"
          clearable
        >
          <template #prefix><i class="fas fa-search text-gray-400"></i></template>
        </n-input>
        
        <!-- 搜索结果 -->
        <div class="border rounded-lg max-h-64 overflow-y-auto">
          <div v-if="userSearchLoading" class="p-4 text-center text-gray-400">
            <i class="fas fa-spinner fa-spin mr-2"></i>搜索中...
          </div>
          <div v-else-if="searchUserList.length === 0" class="p-4 text-center text-gray-400">
            <p v-if="userSearchKeyword">未找到匹配的用户</p>
            <p v-else>输入关键词搜索用户</p>
          </div>
          <div 
            v-for="user in searchUserList" 
            :key="user.id"
            @click="startConversation(user)"
            class="flex items-center gap-3 p-3 hover:bg-gray-50 cursor-pointer border-b last:border-b-0 transition"
          >
            <div class="w-10 h-10 bg-gray-200 rounded-full flex items-center justify-center overflow-hidden">
              <img v-if="user.avatar" :src="user.avatar" class="w-full h-full object-cover" />
              <i v-else class="fas fa-user text-gray-500"></i>
            </div>
            <div class="flex-1">
              <p class="font-medium text-gray-800">{{ user.nickname || '未设置昵称' }}</p>
              <p class="text-xs text-gray-500">{{ user.phone || '无手机号' }}</p>
            </div>
            <n-button size="tiny" type="primary" color="#00AFE1" ghost>
              发起会话
            </n-button>
          </div>
        </div>
      </div>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue'
import { useMessage } from 'naive-ui'
import {
  getServiceConversations,
  getServiceMessages,
  sendServiceMessage,
  markMessagesRead,
  searchUsers
} from '@/api/admin'

const message = useMessage()

const searchKeyword = ref('')
const conversations = ref([])
const currentConv = ref(null)
const messages = ref([])
const inputMessage = ref('')
const messageListRef = ref(null)
const showImagePreview = ref(false)
const previewImageUrl = ref('')
let pollingTimer = null

// 发起新会话相关
const showNewChat = ref(false)
const userSearchKeyword = ref('')
const searchUserList = ref([])
const userSearchLoading = ref(false)
let searchTimer = null

// 过滤会话列表
const filteredConversations = computed(() => {
  if (!searchKeyword.value) return conversations.value
  const keyword = searchKeyword.value.toLowerCase()
  return conversations.value.filter(c =>
    (c.nickname && c.nickname.toLowerCase().includes(keyword)) ||
    (c.phone && c.phone.includes(keyword))
  )
})

// 加载会话列表
const loadConversations = async () => {
  try {
    const res = await getServiceConversations()
    if (res.data) {
      conversations.value = res.data
    }
  } catch (e) {
    console.error('加载会话列表失败', e)
  }
}

// 选择会话
const selectConversation = async (conv) => {
  currentConv.value = conv
  await loadMessages(conv.userId)

  // 标记消息已读
  if (conv.unreadCount > 0) {
    try {
      await markMessagesRead(conv.userId)
      conv.unreadCount = 0
    } catch (e) {
      console.error('标记已读失败', e)
    }
  }
}

// 加载消息
const loadMessages = async (userId) => {
  try {
    const res = await getServiceMessages(userId)
    if (res.data) {
      // 标准化字段名（兼容下划线和驼峰命名）
      messages.value = res.data.map(msg => ({
        id: msg.id,
        userId: msg.userId || msg.user_id,
        senderId: msg.senderId || msg.sender_id,
        senderRole: msg.senderRole || msg.sender_role,
        content: msg.content,
        type: msg.type,
        isRead: msg.isRead || msg.is_read,
        createTime: msg.createTime || msg.create_time,
        senderName: msg.senderName || msg.sender_name,
        senderAvatar: msg.senderAvatar || msg.sender_avatar
      }))
      scrollToBottom()
    }
  } catch (e) {
    console.error('加载消息失败', e)
  }
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || !currentConv.value) return

  const content = inputMessage.value.trim()
  inputMessage.value = ''

  try {
    const res = await sendServiceMessage({
      userId: currentConv.value.userId,
      content: content,
      type: 1
    })

    if (res.data) {
      // 标准化字段名
      const msg = res.data
      messages.value.push({
        id: msg.id,
        userId: msg.userId || msg.user_id,
        senderId: msg.senderId || msg.sender_id,
        senderRole: msg.senderRole || msg.sender_role,
        content: msg.content,
        type: msg.type,
        isRead: msg.isRead || msg.is_read,
        createTime: msg.createTime || msg.create_time,
        senderName: msg.senderName || msg.sender_name,
        senderAvatar: msg.senderAvatar || msg.sender_avatar
      })
      scrollToBottom()

      // 更新会话列表中的最后消息
      const conv = conversations.value.find(c => c.userId === currentConv.value.userId)
      if (conv) {
        conv.lastMessage = content
        conv.lastMessageTime = new Date().toISOString()
      }
    }
  } catch (e) {
    message.error('发送失败')
    inputMessage.value = content
  }
}

// 处理键盘事件
const handleKeydown = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

// 处理图片上传
const handleImageUpload = async ({ file }) => {
  if (!currentConv.value) return
  // 这里简化处理，实际需要先上传图片获取URL
  message.info('图片上传功能开发中')
}

// 处理文件上传
const handleFileUpload = async ({ file }) => {
  if (!currentConv.value) return
  message.info('文件上传功能开发中')
}

// 预览图片
const previewImage = (url) => {
  previewImageUrl.value = url
  showImagePreview.value = true
}

// 查看用户详情
const viewUserDetail = () => {
  message.info(`查看用户 ${currentConv.value.nickname} 详情`)
}

// 搜索用户（防抖）
const handleUserSearch = () => {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(async () => {
    if (!userSearchKeyword.value.trim()) {
      searchUserList.value = []
      return
    }
    userSearchLoading.value = true
    try {
      const res = await searchUsers(userSearchKeyword.value.trim())
      if (res.data) {
        searchUserList.value = res.data
      }
    } catch (e) {
      console.error('搜索用户失败', e)
    } finally {
      userSearchLoading.value = false
    }
  }, 300)
}

// 发起会话
const startConversation = async (user) => {
  // 检查是否已有该用户的会话
  let existingConv = conversations.value.find(c => c.userId === user.id)
  
  if (!existingConv) {
    // 创建新会话（添加到会话列表）
    existingConv = {
      userId: user.id,
      nickname: user.nickname || '未设置昵称',
      phone: user.phone,
      avatar: user.avatar,
      lastMessage: '',
      lastMessageTime: new Date().toISOString(),
      unreadCount: 0
    }
    conversations.value.unshift(existingConv)
  }
  
  // 选择该会话
  currentConv.value = existingConv
  await loadMessages(user.id)
  
  // 关闭模态框并清空搜索
  showNewChat.value = false
  userSearchKeyword.value = ''
  searchUserList.value = []
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

// 格式化时间（会话列表）
const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24))

  if (diffDays === 0) {
    return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
  } else if (diffDays === 1) {
    return '昨天'
  } else if (diffDays < 7) {
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    return weekdays[date.getDay()]
  } else {
    return `${date.getMonth() + 1}/${date.getDate()}`
  }
}

// 格式化消息时间
const formatMessageTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24))

  const time = `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`

  if (diffDays === 0) {
    return time
  } else if (diffDays === 1) {
    return `昨天 ${time}`
  } else {
    return `${date.getMonth() + 1}/${date.getDate()} ${time}`
  }
}

// 轮询新消息
const startPolling = () => {
  pollingTimer = setInterval(async () => {
    await loadConversations()
    if (currentConv.value) {
      await loadMessages(currentConv.value.userId)
    }
  }, 5000)
}

const stopPolling = () => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
}

onMounted(() => {
  loadConversations()
  startPolling()
})

onUnmounted(() => {
  stopPolling()
})
</script>
