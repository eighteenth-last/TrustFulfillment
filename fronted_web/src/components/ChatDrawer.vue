<template>
  <n-drawer v-model:show="visible" :width="420" placement="right" :mask-closable="true" @after-leave="handleClose">
    <n-drawer-content closable>
      <template #header>
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 bg-gradient-to-br from-cyan-400 to-blue-500 rounded-full flex items-center justify-center text-white font-bold">
            <i class="fas fa-comments"></i>
          </div>
          <div>
            <h3 class="font-bold text-gray-800">{{ title }}</h3>
            <p class="text-xs text-gray-500">订单沟通</p>
          </div>
        </div>
      </template>

      <!-- 消息列表区域 -->
      <div ref="messageListRef" class="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50" style="height: calc(100vh - 200px);">
        <!-- 加载中 -->
        <div v-if="loading" class="flex justify-center py-8">
          <n-spin size="medium" />
        </div>

        <!-- 空状态 -->
        <div v-else-if="messages.length === 0" class="flex flex-col items-center justify-center py-12 text-gray-400">
          <i class="fas fa-comment-dots text-4xl mb-3"></i>
          <p>暂无消息，开始聊天吧</p>
        </div>

        <!-- 消息列表 -->
        <template v-else>
          <div v-for="msg in messages" :key="msg.id" 
               :class="['flex', msg.senderId === currentUserId ? 'justify-end' : 'justify-start']">
            <div :class="['max-w-[75%] flex gap-2', msg.senderId === currentUserId ? 'flex-row-reverse' : 'flex-row']">
              <!-- 头像 -->
              <div class="flex-shrink-0">
                <div v-if="msg.senderAvatar" class="w-8 h-8 rounded-full overflow-hidden">
                  <img :src="msg.senderAvatar" alt="" class="w-full h-full object-cover">
                </div>
                <div v-else class="w-8 h-8 rounded-full bg-gradient-to-br from-cyan-400 to-blue-500 flex items-center justify-center text-white text-xs font-bold">
                  {{ msg.senderName?.charAt(0) || '?' }}
                </div>
              </div>

              <!-- 消息内容 -->
              <div>
                <div class="text-xs text-gray-400 mb-1" :class="msg.senderId === currentUserId ? 'text-right' : 'text-left'">
                  {{ msg.senderName || '未知用户' }}
                </div>
                <div :class="[
                  'px-4 py-2 rounded-2xl text-sm',
                  msg.senderId === currentUserId 
                    ? 'bg-gradient-to-r from-cyan-500 to-blue-500 text-white rounded-tr-sm' 
                    : 'bg-white text-gray-700 shadow-sm rounded-tl-sm'
                ]">
                  <!-- 文本消息 -->
                  <template v-if="msg.type === 1">
                    {{ msg.content }}
                  </template>
                  <!-- 图片消息 -->
                  <template v-else-if="msg.type === 2">
                    <img :src="msg.content" alt="图片" class="max-w-[200px] rounded cursor-pointer" @click="previewImage(msg.content)">
                  </template>
                  <!-- 文件消息 -->
                  <template v-else-if="msg.type === 3">
                    <a :href="msg.content" target="_blank" class="flex items-center gap-2 hover:underline">
                      <i class="fas fa-file-alt"></i>
                      <span>查看文件</span>
                    </a>
                  </template>
                </div>
                <div class="text-xs text-gray-400 mt-1" :class="msg.senderId === currentUserId ? 'text-right' : 'text-left'">
                  {{ formatTime(msg.createTime) }}
                  <span v-if="msg.senderId === currentUserId && msg.isRead" class="ml-1 text-green-500">已读</span>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>

      <!-- 输入区域 -->
      <div class="border-t border-gray-200 p-4 bg-white">
        <div class="flex gap-2">
          <n-input
            v-model:value="inputContent"
            type="textarea"
            :autosize="{ minRows: 1, maxRows: 3 }"
            placeholder="输入消息..."
            @keydown.enter.exact.prevent="handleSend"
            class="flex-1"
          />
          <n-button type="primary" color="#00AFE1" :disabled="!inputContent.trim()" @click="handleSend">
            <template #icon><i class="fas fa-paper-plane"></i></template>
          </n-button>
        </div>
        <div class="flex items-center gap-2 mt-2">
          <n-upload
            :show-file-list="false"
            :custom-request="handleUploadImage"
            accept="image/*"
          >
            <n-button text size="small">
              <template #icon><i class="fas fa-image text-gray-400"></i></template>
            </n-button>
          </n-upload>
          <span class="text-xs text-gray-400">{{ wsConnected ? '已连接' : '连接中...' }}</span>
        </div>
      </div>
    </n-drawer-content>
  </n-drawer>
</template>

<script setup>
import { ref, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { useMessage } from 'naive-ui'
import { useUserStore } from '@/stores/user'
import { getMessageHistory, sendMessage as sendMessageApi } from '@/api/message'
import { uploadFile } from '@/api/file'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  orderId: {
    type: [Number, String],
    default: null
  },
  title: {
    type: String,
    default: '在线沟通'
  }
})

const emit = defineEmits(['update:show'])

const message = useMessage()
const userStore = useUserStore()

const visible = ref(false)
const loading = ref(false)
const messages = ref([])
const inputContent = ref('')
const currentUserId = ref(null)
const messageListRef = ref(null)

// WebSocket 相关
const ws = ref(null)
const wsConnected = ref(false)
const reconnectTimer = ref(null)
const reconnectAttempts = ref(0)
const maxReconnectAttempts = 5

// 监听 show 属性
watch(() => props.show, (val) => {
  visible.value = val
  if (val) {
    currentUserId.value = userStore.userInfo?.id
    loadMessages()
    connectWebSocket()
  }
})

// 监听 visible 变化，同步给父组件
watch(visible, (val) => {
  emit('update:show', val)
})

// 加载消息历史
const loadMessages = async () => {
  loading.value = true
  try {
    const res = await getMessageHistory(props.orderId)
    messages.value = res.data.messages || []
    currentUserId.value = res.data.currentUserId
    await nextTick()
    scrollToBottom()
  } catch (e) {
    message.error(e.message || '加载消息失败')
  } finally {
    loading.value = false
  }
}

// 连接 WebSocket
const connectWebSocket = () => {
  if (ws.value && ws.value.readyState === WebSocket.OPEN) {
    // 已连接，发送订阅消息
    subscribeOrder()
    return
  }

  const token = userStore.token
  if (!token) {
    console.error('未登录，无法连接WebSocket')
    return
  }

  // 构建 WebSocket URL
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  const host = window.location.hostname
  const port = '8080'  // 后端端口
  const wsUrl = `${protocol}//${host}:${port}/ws/chat?token=${token}`

  ws.value = new WebSocket(wsUrl)

  ws.value.onopen = () => {
    console.log('WebSocket 连接成功')
    wsConnected.value = true
    reconnectAttempts.value = 0
    subscribeOrder()
  }

  ws.value.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      handleWebSocketMessage(data)
    } catch (e) {
      console.error('解析WebSocket消息失败', e)
    }
  }

  ws.value.onclose = () => {
    console.log('WebSocket 连接关闭')
    wsConnected.value = false
    // 尝试重连
    if (visible.value && reconnectAttempts.value < maxReconnectAttempts) {
      reconnectAttempts.value++
      reconnectTimer.value = setTimeout(() => {
        connectWebSocket()
      }, 2000 * reconnectAttempts.value)
    }
  }

  ws.value.onerror = (error) => {
    console.error('WebSocket 错误', error)
    wsConnected.value = false
  }
}

// 订阅订单消息
const subscribeOrder = () => {
  if (ws.value && ws.value.readyState === WebSocket.OPEN) {
    ws.value.send(JSON.stringify({
      action: 'subscribe',
      orderId: props.orderId
    }))
  }
}

// 处理 WebSocket 消息
const handleWebSocketMessage = (data) => {
  switch (data.type) {
    case 'message':
      // 收到新消息
      const msgData = data.data
      
      // 检查是否是自己发送的消息（已经本地添加了临时消息）
      if (msgData.senderId === currentUserId.value) {
        // 查找并替换临时消息
        const tempIdx = messages.value.findIndex(m => 
          String(m.id).startsWith('temp_') && 
          m.content === msgData.content &&
          m.senderId === msgData.senderId
        )
        if (tempIdx !== -1) {
          // 替换临时消息为服务端返回的真实消息
          messages.value.splice(tempIdx, 1, msgData)
          return
        }
      }
      
      // 检查是否已存在（通过真实ID）
      const exists = messages.value.some(m => m.id === msgData.id && !String(m.id).startsWith('temp_'))
      if (!exists) {
        messages.value.push(msgData)
        nextTick(() => scrollToBottom())
      }
      break
    case 'system':
      console.log('系统消息:', data.message)
      break
    case 'error':
      message.error(data.message)
      break
  }
}

// 发送消息
const handleSend = async () => {
  const content = inputContent.value.trim()
  if (!content) return

  // 先清空输入框
  const sendContent = content
  inputContent.value = ''

  // 创建临时消息（本地显示）
  const tempId = 'temp_' + Date.now()
  const tempMsg = {
    id: tempId,
    orderId: props.orderId,
    senderId: currentUserId.value,
    content: sendContent,
    type: 1,
    isRead: 0,
    createTime: new Date().toISOString(),
    senderName: userStore.userInfo?.nickname || userStore.userInfo?.phone || '我',
    senderAvatar: userStore.userInfo?.avatar
  }
  
  // 先添加到消息列表
  messages.value.push(tempMsg)
  await nextTick()
  scrollToBottom()

  // 优先使用 WebSocket 发送
  if (ws.value && ws.value.readyState === WebSocket.OPEN) {
    ws.value.send(JSON.stringify({
      action: 'send',
      orderId: props.orderId,
      content: sendContent,
      type: 1
    }))
  } else {
    // 降级使用 HTTP 发送
    try {
      const res = await sendMessageApi(props.orderId, sendContent, 1)
      // 替换临时消息为服务端返回的消息
      const idx = messages.value.findIndex(m => m.id === tempId)
      if (idx !== -1) {
        messages.value.splice(idx, 1, res.data)
      }
    } catch (e) {
      message.error(e.message || '发送失败')
      // 发送失败，移除临时消息
      const idx = messages.value.findIndex(m => m.id === tempId)
      if (idx !== -1) {
        messages.value.splice(idx, 1)
      }
    }
  }
}

// 上传图片
const handleUploadImage = async ({ file }) => {
  try {
    const res = await uploadFile(file.file)
    const imageUrl = res.data
    
    // 发送图片消息
    if (ws.value && ws.value.readyState === WebSocket.OPEN) {
      ws.value.send(JSON.stringify({
        action: 'send',
        orderId: props.orderId,
        content: imageUrl,
        type: 2
      }))
    } else {
      const msgRes = await sendMessageApi(props.orderId, imageUrl, 2)
      messages.value.push(msgRes.data)
      await nextTick()
      scrollToBottom()
    }
  } catch (e) {
    message.error(e.message || '上传失败')
  }
}

// 预览图片
const previewImage = (url) => {
  window.open(url, '_blank')
}

// 滚动到底部
const scrollToBottom = () => {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

// 格式化时间
const formatTime = (timeVal) => {
  if (!timeVal) return ''
  
  let date
  // 处理不同的时间格式
  if (typeof timeVal === 'string') {
    // 字符串格式 "2026-01-31T15:00:00"
    date = new Date(timeVal.replace('T', ' '))
  } else if (Array.isArray(timeVal)) {
    // 数组格式 [2026, 1, 31, 15, 0, 0] (Java LocalDateTime序列化)
    date = new Date(timeVal[0], timeVal[1] - 1, timeVal[2], timeVal[3] || 0, timeVal[4] || 0, timeVal[5] || 0)
  } else if (timeVal instanceof Date) {
    date = timeVal
  } else {
    return ''
  }
  
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  
  // 格式化为 MM-DD HH:mm
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${month}-${day} ${hours}:${minutes}`
}

// 关闭抽屉时断开 WebSocket
const handleClose = () => {
  if (reconnectTimer.value) {
    clearTimeout(reconnectTimer.value)
  }
}

onMounted(() => {
  currentUserId.value = userStore.userInfo?.id
})

onUnmounted(() => {
  if (ws.value) {
    ws.value.close()
  }
  if (reconnectTimer.value) {
    clearTimeout(reconnectTimer.value)
  }
})
</script>

<style scoped>
:deep(.n-drawer-body-content-wrapper) {
  display: flex;
  flex-direction: column;
}
</style>
