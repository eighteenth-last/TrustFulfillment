<template>
  <n-drawer v-model:show="visible" :width="480" placement="right" :mask-closable="true">
    <n-drawer-content closable>
      <template #header>
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 bg-gradient-to-br from-green-400 to-emerald-500 rounded-full flex items-center justify-center text-white font-bold">
            <i class="fas fa-shield-alt"></i>
          </div>
          <div>
            <h3 class="font-bold text-gray-800">存证记录</h3>
            <p class="text-xs text-gray-500">区块链存证保障交易安全</p>
          </div>
        </div>
      </template>

      <!-- 加载中 -->
      <div v-if="loading" class="flex justify-center py-12">
        <n-spin size="large" />
      </div>

      <!-- 空状态 -->
      <div v-else-if="evidenceList.length === 0" class="flex flex-col items-center justify-center py-16 text-gray-400">
        <i class="fas fa-file-contract text-5xl mb-4"></i>
        <p class="text-lg">暂无存证记录</p>
        <p class="text-sm mt-2">交易过程中的关键操作将自动存证</p>
      </div>

      <!-- 存证列表 -->
      <div v-else class="p-4 space-y-4">
        <!-- 统计卡片 -->
        <div class="grid grid-cols-3 gap-3 mb-6">
          <div class="bg-blue-50 rounded-xl p-3 text-center">
            <div class="text-2xl font-black text-blue-600">{{ evidenceList.length }}</div>
            <div class="text-xs text-gray-500">总存证数</div>
          </div>
          <div class="bg-green-50 rounded-xl p-3 text-center">
            <div class="text-2xl font-black text-green-600">{{ typeCount[4] || 0 }}</div>
            <div class="text-xs text-gray-500">验收确认</div>
          </div>
          <div class="bg-purple-50 rounded-xl p-3 text-center">
            <div class="text-2xl font-black text-purple-600">{{ typeCount[2] || 0 }}</div>
            <div class="text-xs text-gray-500">资金托管</div>
          </div>
        </div>

        <!-- 时间线 -->
        <n-timeline>
          <n-timeline-item
            v-for="evidence in evidenceList"
            :key="evidence.id"
            :type="getTimelineType(evidence.type)"
          >
            <template #icon>
              <div :class="['w-8 h-8 rounded-full flex items-center justify-center text-white text-sm', getIconBgClass(evidence.type)]">
                <i :class="getTypeIcon(evidence.type)"></i>
              </div>
            </template>
            
            <div class="bg-white rounded-xl p-4 shadow-sm border border-gray-100 hover:shadow-md transition cursor-pointer" @click="showDetail(evidence)">
              <div class="flex items-start justify-between mb-2">
                <div>
                  <span :class="['text-xs px-2 py-0.5 rounded-full font-bold', getTypeBadgeClass(evidence.type)]">
                    {{ getTypeName(evidence.type) }}
                  </span>
                </div>
                <span class="text-xs text-gray-400">{{ formatDateTime(evidence.createTime) }}</span>
              </div>
              
              <h4 class="font-bold text-gray-800 mb-1">{{ evidence.title }}</h4>
              <p class="text-sm text-gray-500 line-clamp-2">{{ evidence.content }}</p>
              
              <!-- 区块链信息 -->
              <div class="mt-3 pt-3 border-t border-gray-100">
                <div class="flex items-center gap-4 text-xs text-gray-400">
                  <div class="flex items-center gap-1">
                    <i class="fas fa-cube"></i>
                    <span>区块: {{ evidence.blockHeight }}</span>
                  </div>
                  <div class="flex items-center gap-1 flex-1 truncate">
                    <i class="fas fa-fingerprint"></i>
                    <span class="truncate">{{ evidence.fileHash?.substring(0, 16) }}...</span>
                  </div>
                </div>
              </div>
            </div>
          </n-timeline-item>
        </n-timeline>
      </div>

      <!-- 存证详情弹窗 -->
      <n-modal v-model:show="showDetailModal" preset="card" :title="selectedEvidence?.title" style="width: 500px;" :bordered="false">
        <div v-if="selectedEvidence" class="space-y-4">
          <!-- 类型标签 -->
          <div>
            <span :class="['px-3 py-1 rounded-full text-sm font-bold', getTypeBadgeClass(selectedEvidence.type)]">
              {{ getTypeName(selectedEvidence.type) }}
            </span>
          </div>
          
          <!-- 存证内容 -->
          <div>
            <h5 class="text-sm font-bold text-gray-500 mb-2">存证内容</h5>
            <p class="text-gray-700 whitespace-pre-wrap">{{ selectedEvidence.content }}</p>
          </div>
          
          <!-- 区块链信息 -->
          <div class="bg-gray-50 rounded-xl p-4 space-y-3">
            <h5 class="text-sm font-bold text-gray-500 flex items-center gap-2">
              <i class="fas fa-link text-blue-500"></i>
              区块链存证信息
            </h5>
            <div class="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p class="text-gray-400 text-xs">区块高度</p>
                <p class="font-mono text-gray-700">{{ selectedEvidence.blockHeight }}</p>
              </div>
              <div>
                <p class="text-gray-400 text-xs">上链时间</p>
                <p class="text-gray-700">{{ formatDateTime(selectedEvidence.chainTime) }}</p>
              </div>
            </div>
            <div>
              <p class="text-gray-400 text-xs mb-1">存证哈希</p>
              <div class="flex items-center gap-2">
                <code class="text-xs bg-gray-200 px-2 py-1 rounded flex-1 truncate font-mono">{{ selectedEvidence.fileHash }}</code>
                <n-button text size="small" @click="copyHash(selectedEvidence.fileHash)">
                  <i class="fas fa-copy"></i>
                </n-button>
              </div>
            </div>
          </div>
          
          <!-- 文件链接 -->
          <div v-if="selectedEvidence.fileUrl">
            <n-button block type="primary" color="#00AFE1" @click="openFile(selectedEvidence.fileUrl)">
              <template #icon><i class="fas fa-external-link-alt"></i></template>
              查看存证文件
            </n-button>
          </div>
          
          <!-- 创建时间 -->
          <div class="text-center text-xs text-gray-400 pt-2 border-t">
            存证创建时间: {{ formatDateTime(selectedEvidence.createTime) }}
          </div>
        </div>
      </n-modal>
    </n-drawer-content>
  </n-drawer>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useMessage } from 'naive-ui'
import { getOrderEvidence } from '@/api/evidence'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  orderId: {
    type: [Number, String],
    default: null
  }
})

const emit = defineEmits(['update:show'])

const message = useMessage()

const visible = ref(false)
const loading = ref(false)
const evidenceList = ref([])
const typeCount = ref({})
const showDetailModal = ref(false)
const selectedEvidence = ref(null)

// 存证类型配置
const typeConfig = {
  1: { name: '合同签署', icon: 'fas fa-file-signature', color: 'blue' },
  2: { name: '资金托管', icon: 'fas fa-hand-holding-usd', color: 'green' },
  3: { name: '交付提交', icon: 'fas fa-upload', color: 'orange' },
  4: { name: '验收确认', icon: 'fas fa-check-circle', color: 'emerald' },
  5: { name: '纠纷证据', icon: 'fas fa-exclamation-triangle', color: 'red' }
}

// 监听 show 属性
watch(() => props.show, (val) => {
  visible.value = val
  if (val) {
    loadEvidence()
  }
})

// 监听 visible 变化，同步给父组件
watch(visible, (val) => {
  emit('update:show', val)
})

// 加载存证记录
const loadEvidence = async () => {
  loading.value = true
  try {
    const res = await getOrderEvidence(props.orderId)
    evidenceList.value = res.data.list || []
    typeCount.value = res.data.typeCount || {}
  } catch (e) {
    message.error(e.message || '加载存证记录失败')
  } finally {
    loading.value = false
  }
}

// 获取类型名称
const getTypeName = (type) => {
  return typeConfig[type]?.name || '未知类型'
}

// 获取类型图标
const getTypeIcon = (type) => {
  return typeConfig[type]?.icon || 'fas fa-file'
}

// 获取图标背景色
const getIconBgClass = (type) => {
  const colorMap = {
    1: 'bg-blue-500',
    2: 'bg-green-500',
    3: 'bg-orange-500',
    4: 'bg-emerald-500',
    5: 'bg-red-500'
  }
  return colorMap[type] || 'bg-gray-500'
}

// 获取时间线类型
const getTimelineType = (type) => {
  const typeMap = {
    1: 'info',
    2: 'success',
    3: 'warning',
    4: 'success',
    5: 'error'
  }
  return typeMap[type] || 'default'
}

// 获取类型标签样式
const getTypeBadgeClass = (type) => {
  const classMap = {
    1: 'bg-blue-100 text-blue-600',
    2: 'bg-green-100 text-green-600',
    3: 'bg-orange-100 text-orange-600',
    4: 'bg-emerald-100 text-emerald-600',
    5: 'bg-red-100 text-red-600'
  }
  return classMap[type] || 'bg-gray-100 text-gray-600'
}

// 显示详情
const showDetail = (evidence) => {
  selectedEvidence.value = evidence
  showDetailModal.value = true
}

// 复制哈希
const copyHash = async (hash) => {
  try {
    await navigator.clipboard.writeText(hash)
    message.success('已复制到剪贴板')
  } catch (e) {
    message.error('复制失败')
  }
}

// 打开文件
const openFile = (url) => {
  window.open(url, '_blank')
}

// 格式化时间
const formatDateTime = (timeVal) => {
  if (!timeVal) return '-'
  
  let date
  // 处理不同的时间格式
  if (typeof timeVal === 'string') {
    // 字符串格式 "2026-01-31T15:00:00"
    return timeVal.replace('T', ' ').substring(0, 19)
  } else if (Array.isArray(timeVal)) {
    // 数组格式 [2026, 1, 31, 15, 0, 0] (Java LocalDateTime序列化)
    date = new Date(timeVal[0], timeVal[1] - 1, timeVal[2], timeVal[3] || 0, timeVal[4] || 0, timeVal[5] || 0)
  } else if (timeVal instanceof Date) {
    date = timeVal
  } else {
    return '-'
  }
  
  // 格式化为 YYYY-MM-DD HH:mm:ss
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}
</script>
