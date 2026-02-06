<template>
  <n-drawer v-model:show="visible" :width="420" placement="right" :mask-closable="true" @after-leave="handleClose">
    <n-drawer-content closable>
      <template #header>
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 bg-gradient-to-br from-green-400 to-cyan-500 rounded-full flex items-center justify-center text-white font-bold">
            <i class="fas fa-headset"></i>
          </div>
          <div>
            <h3 class="font-bold text-gray-800">平台客服</h3>
            <p class="text-xs text-gray-500">工作时间 9:00-22:00</p>
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
          <p>有什么可以帮您？</p>
          <p class="text-xs mt-2">发送消息开始咨询</p>
        </div>

        <!-- 消息列表 -->
        <template v-else>
          <div v-for="msg in messages" :key="msg.id" 
               :class="['flex', msg.senderRole === 1 ? 'justify-end' : 'justify-start']">
            <div :class="['max-w-[75%] flex gap-2', msg.senderRole === 1 ? 'flex-row-reverse' : 'flex-row']">
              <!-- 头像 -->
              <div class="flex-shrink-0">
                <div v-if="msg.senderRole === 2" class="w-8 h-8 rounded-full bg-gradient-to-br from-green-400 to-cyan-500 flex items-center justify-center text-white text-xs">
                  <i class="fas fa-headset"></i>
                </div>
                <div v-else-if="msg.senderAvatar" class="w-8 h-8 rounded-full overflow-hidden">
                  <img :src="msg.senderAvatar" alt="" class="w-full h-full object-cover">
                </div>
                <div v-else class="w-8 h-8 rounded-full bg-gradient-to-br from-cyan-400 to-blue-500 flex items-center justify-center text-white text-xs font-bold">
                  {{ msg.senderName?.charAt(0) || '我' }}
                </div>
              </div>

              <!-- 消息内容 -->
              <div>
                <div class="text-xs text-gray-400 mb-1" :class="msg.senderRole === 1 ? 'text-right' : 'text-left'">
                  {{ msg.senderRole === 2 ? '平台客服' : (msg.senderName || '我') }}
                </div>
                <div :class="[
                  'px-4 py-2 rounded-2xl text-sm',
                  msg.senderRole === 1 
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
                <div class="text-xs text-gray-400 mt-1" :class="msg.senderRole === 1 ? 'text-right' : 'text-left'">
                  {{ formatTime(msg.createTime) }}
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
            placeholder="输入您的问题..."
            @keydown.enter.exact.prevent="handleSend"
            class="flex-1"
          />
          <n-button type="primary" color="#00AFE1" :disabled="!inputContent.trim() || sending" :loading="sending" @click="handleSend">
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
          <span class="text-xs text-gray-400">客服将尽快回复您</span>
        </div>
      </div>
    </n-drawer-content>
  </n-drawer>
</template>

<script setup>
import { ref, watch, nextTick, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { useUserStore } from '@/stores/user'
import { getServiceChatHistory, sendServiceMessage, markServiceMessageRead } from '@/api/serviceChat'
import { uploadFile } from '@/api/file'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:show'])

const message = useMessage()
const userStore = useUserStore()

const visible = ref(false)
const loading = ref(false)
const sending = ref(false)
const messages = ref([])
const inputContent = ref('')
const messageListRef = ref(null)

// 监听 show 属性
watch(() => props.show, (val) => {
  visible.value = val
  if (val) {
    loadMessages()
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
    const res = await getServiceChatHistory()
    messages.value = res.data.messages || []
    await nextTick()
    scrollToBottom()
    // 标记消息已读
    if (messages.value.length > 0) {
      markServiceMessageRead()
    }
  } catch (e) {
    console.error('加载消息失败', e)
  } finally {
    loading.value = false
  }
}

// 发送消息
const handleSend = async () => {
  const content = inputContent.value.trim()
  if (!content || sending.value) return

  // 先清空输入框
  const sendContent = content
  inputContent.value = ''
  sending.value = true

  // 创建临时消息（本地显示）
  const tempId = 'temp_' + Date.now()
  const tempMsg = {
    id: tempId,
    senderId: userStore.userInfo?.id,
    senderRole: 1,
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

  try {
    const res = await sendServiceMessage(sendContent, 1)
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
  } finally {
    sending.value = false
  }
}

// 上传图片
const handleUploadImage = async ({ file }) => {
  try {
    const res = await uploadFile(file.file)
    const imageUrl = res.data
    
    // 发送图片消息
    const msgRes = await sendServiceMessage(imageUrl, 2)
    messages.value.push(msgRes.data)
    await nextTick()
    scrollToBottom()
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
    date = new Date(timeVal.replace('T', ' '))
  } else if (Array.isArray(timeVal)) {
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
  
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${month}-${day} ${hours}:${minutes}`
}

// 关闭抽屉
const handleClose = () => {
  // 可以在这里做清理工作
}

onMounted(() => {
  // 初始化
})
</script>

<style scoped>
:deep(.n-drawer-body-content-wrapper) {
  display: flex;
  flex-direction: column;
}
</style>
