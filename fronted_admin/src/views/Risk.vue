<template>
  <div class="space-y-6">
    <!-- 风控总览 -->
    <div class="grid grid-cols-4 gap-6">
      <div class="p-6 rounded-2xl border" :class="getSafetyStatusClass()">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs font-bold" :class="getSafetyTextClass()">系统安全状态</p>
            <h3 class="text-2xl font-black mt-2" :class="getSafetyTextClass()">{{ getSafetyStatusText() }}</h3>
          </div>
          <div class="w-12 h-12 rounded-full flex items-center justify-center" :class="getSafetyIconClass()">
            <i :class="getSafetyIcon()" class="text-2xl"></i>
          </div>
        </div>
      </div>
      <div class="bg-white p-6 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">今日拦截</p>
        <h3 class="text-2xl font-black text-gray-800 mt-2">{{ stats.todayBlocked }}</h3>
        <p class="text-xs text-gray-400 mt-2">异常请求</p>
      </div>
      <div class="bg-white p-6 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">风险订单</p>
        <h3 class="text-2xl font-black text-orange-500 mt-2">{{ stats.riskOrders }}</h3>
        <p class="text-xs text-gray-400 mt-2">待审查</p>
      </div>
      <div class="bg-white p-6 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">可疑账户</p>
        <h3 class="text-2xl font-black text-red-500 mt-2">{{ stats.suspiciousUsers }}</h3>
        <p class="text-xs text-gray-400 mt-2">需关注</p>
      </div>
    </div>

    <div class="grid grid-cols-3 gap-6">
      <!-- 风险告警 -->
      <div class="col-span-2 bg-white rounded-2xl border border-gray-100 p-6">
        <div class="flex items-center justify-between mb-6">
          <h4 class="font-bold">实时风险告警</h4>
          <n-button text type="primary" @click="openAllEvents">查看全部</n-button>
        </div>

        <div v-if="alerts.length === 0" class="py-12 text-center text-gray-400">
          <i class="fas fa-shield-check text-4xl mb-3 text-green-300"></i>
          <p>暂无风险告警</p>
        </div>

        <div v-else class="space-y-4">
          <div 
            v-for="alert in alerts" 
            :key="alert.id"
            class="p-4 rounded-xl border flex items-center justify-between"
            :class="getLevelClass(alert.level)"
          >
            <div class="flex items-center gap-4">
              <div 
                class="w-10 h-10 rounded-full flex items-center justify-center"
                :class="getLevelIconClass(alert.level)"
              >
                <i :class="alert.icon"></i>
              </div>
              <div>
                <p class="font-bold text-gray-800">{{ alert.title }}</p>
                <p class="text-xs text-gray-400 mt-1">{{ alert.description }}</p>
              </div>
            </div>
            <div class="flex items-center gap-4">
              <span class="text-xs text-gray-400">{{ formatTime(alert.time) }}</span>
              <n-button size="small" ghost @click="handleAlert(alert)">处理</n-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 风控规则 -->
      <div class="bg-white rounded-2xl border border-gray-100 p-6">
        <div class="flex items-center justify-between mb-6">
          <h4 class="font-bold">风控规则状态</h4>
          <n-button size="small" :loading="savingRules" @click="saveRules" v-if="rulesChanged">
            保存
          </n-button>
        </div>
        <div class="space-y-4">
          <div class="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
            <div class="flex items-center gap-3">
              <i class="fas fa-robot text-tf"></i>
              <span class="text-sm">智能反欺诈</span>
            </div>
            <n-switch v-model:value="rules.antifraud" size="small" @update:value="rulesChanged = true" />
          </div>
          <div class="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
            <div class="flex items-center gap-3">
              <i class="fas fa-user-shield text-tf"></i>
              <span class="text-sm">账户异常检测</span>
            </div>
            <n-switch v-model:value="rules.accountAnomaly" size="small" @update:value="rulesChanged = true" />
          </div>
          <div class="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
            <div class="flex items-center gap-3">
              <i class="fas fa-money-bill-wave text-tf"></i>
              <span class="text-sm">大额交易审核</span>
            </div>
            <n-switch v-model:value="rules.largeTransaction" size="small" @update:value="rulesChanged = true" />
          </div>
          <div class="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
            <div class="flex items-center gap-3">
              <i class="fas fa-clock text-tf"></i>
              <span class="text-sm">频繁操作限制</span>
            </div>
            <n-switch v-model:value="rules.frequentOperation" size="small" @update:value="rulesChanged = true" />
          </div>
          <div class="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
            <div class="flex items-center gap-3">
              <i class="fas fa-globe text-tf"></i>
              <span class="text-sm">IP地址风控</span>
            </div>
            <n-switch v-model:value="rules.ipRisk" size="small" @update:value="rulesChanged = true" />
          </div>
        </div>
      </div>
    </div>

    <!-- 风险趋势 -->
    <div class="bg-white rounded-2xl border border-gray-100 p-6">
      <h4 class="font-bold mb-6">近7日风险趋势</h4>
      <div class="h-48 bg-gray-50 rounded-xl flex items-end justify-between p-4 gap-4">
        <div v-for="(day, i) in trendData" :key="i" class="flex-1 flex flex-col items-center gap-2">
          <div class="w-full flex flex-col gap-1">
            <div class="bg-red-400 rounded-t transition-all" :style="{ height: Math.max(day.high * 8, day.high > 0 ? 4 : 0) + 'px' }"></div>
            <div class="bg-orange-400 transition-all" :style="{ height: Math.max(day.medium * 8, day.medium > 0 ? 4 : 0) + 'px' }"></div>
            <div class="bg-yellow-400 rounded-b transition-all" :style="{ height: Math.max(day.low * 8, day.low > 0 ? 4 : 0) + 'px' }"></div>
          </div>
          <span class="text-xs text-gray-400">{{ day.date }}</span>
        </div>
      </div>
      <div class="flex items-center justify-center gap-6 mt-4 text-xs">
        <span class="flex items-center gap-2"><span class="w-3 h-3 bg-red-400 rounded"></span> 高风险</span>
        <span class="flex items-center gap-2"><span class="w-3 h-3 bg-orange-400 rounded"></span> 中风险</span>
        <span class="flex items-center gap-2"><span class="w-3 h-3 bg-yellow-400 rounded"></span> 低风险</span>
      </div>
    </div>
    
    <!-- 处理告警弹窗 -->
    <n-modal v-model:show="showProcessModal" preset="card" title="处理风险告警" style="width: 500px;">
      <div v-if="currentAlert" class="space-y-4">
        <div class="p-4 rounded-xl" :class="getLevelClass(currentAlert.level)">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-full flex items-center justify-center" :class="getLevelIconClass(currentAlert.level)">
              <i :class="currentAlert.icon"></i>
            </div>
            <div>
              <p class="font-bold text-gray-800">{{ currentAlert.title }}</p>
              <p class="text-sm text-gray-500 mt-1">{{ currentAlert.description }}</p>
            </div>
          </div>
        </div>
        
        <div>
          <label class="text-sm font-medium text-gray-700 mb-2 block">处理方式</label>
          <n-radio-group v-model:value="processForm.status">
            <n-space>
              <n-radio :value="1">已处理</n-radio>
              <n-radio :value="2">忽略</n-radio>
              <n-radio :value="3">标记拦截</n-radio>
            </n-space>
          </n-radio-group>
        </div>
        
        <div>
          <label class="text-sm font-medium text-gray-700 mb-2 block">处理备注</label>
          <n-input v-model:value="processForm.remark" type="textarea" placeholder="请输入处理备注..." :rows="3" />
        </div>
      </div>
      <template #footer>
        <div class="flex gap-3 justify-end">
          <n-button @click="showProcessModal = false">取消</n-button>
          <n-button type="primary" color="#00AFE1" @click="confirmProcess" :loading="processing">确认处理</n-button>
        </div>
      </template>
    </n-modal>
    
    <!-- 全部事件弹窗 -->
    <n-modal v-model:show="showAllEvents" preset="card" title="全部风险事件" style="width: 900px; max-width: 90vw;">
      <div class="space-y-4">
        <div class="flex items-center gap-4">
          <n-select v-model:value="eventFilter.riskLevel" placeholder="风险等级" clearable class="w-32" :options="levelOptions" />
          <n-select v-model:value="eventFilter.status" placeholder="状态" clearable class="w-32" :options="statusOptions" />
          <n-button @click="loadEventList">查询</n-button>
        </div>
        
        <n-data-table
          :columns="eventColumns"
          :data="eventList"
          :loading="loadingEvents"
          :pagination="eventPagination"
          :bordered="false"
          @update:page="handleEventPageChange"
        />
      </div>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, h, computed } from 'vue'
import { useMessage, NTag, NButton } from 'naive-ui'
import { getRiskAlerts, getRiskStats, getRiskTrend, getRiskRules, saveRiskRules as apiSaveRiskRules, getRiskEventList, processRiskEvent } from '@/api/admin'

const message = useMessage()

const loading = ref(false)
const savingRules = ref(false)
const rulesChanged = ref(false)
const processing = ref(false)
const loadingEvents = ref(false)

const stats = reactive({
  todayBlocked: 0,
  riskOrders: 0,
  suspiciousUsers: 0,
  safetyStatus: 'safe'
})

const rules = reactive({
  antifraud: true,
  accountAnomaly: true,
  largeTransaction: true,
  frequentOperation: true,
  ipRisk: true
})

const alerts = ref([])
const trendData = ref([])

const showProcessModal = ref(false)
const showAllEvents = ref(false)
const currentAlert = ref(null)
const processForm = reactive({
  status: 1,
  remark: ''
})

const eventFilter = reactive({
  riskLevel: null,
  status: null
})

const eventList = ref([])
const eventPagination = reactive({
  page: 1,
  pageSize: 10,
  pageCount: 1,
  itemCount: 0
})

const levelOptions = [
  { label: '高风险', value: 'high' },
  { label: '中风险', value: 'medium' },
  { label: '低风险', value: 'low' }
]

const statusOptions = [
  { label: '待处理', value: 0 },
  { label: '已处理', value: 1 },
  { label: '已忽略', value: 2 },
  { label: '已拦截', value: 3 }
]

const eventColumns = [
  {
    title: '风险类型',
    key: 'eventType',
    width: 120,
    render: (row) => {
      const typeMap = {
        large_transaction: '大额交易',
        frequent_operation: '频繁操作',
        abnormal_login: '异常登录',
        dispute: '纠纷风险',
        account_anomaly: '账户异常',
        ip_risk: 'IP风险',
        fraud_suspicion: '欺诈嫌疑'
      }
      return typeMap[row.eventType] || row.eventType
    }
  },
  {
    title: '风险等级',
    key: 'riskLevel',
    width: 100,
    render: (row) => {
      const config = {
        high: { text: '高', type: 'error' },
        medium: { text: '中', type: 'warning' },
        low: { text: '低', type: 'info' }
      }
      const c = config[row.riskLevel] || { text: '未知', type: 'default' }
      return h(NTag, { type: c.type, size: 'small' }, { default: () => c.text })
    }
  },
  {
    title: '标题',
    key: 'title',
    ellipsis: { tooltip: true }
  },
  {
    title: '涉及金额',
    key: 'amount',
    width: 120,
    render: (row) => row.amount ? '¥' + Number(row.amount).toLocaleString() : '-'
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: (row) => {
      const config = {
        0: { text: '待处理', type: 'warning' },
        1: { text: '已处理', type: 'success' },
        2: { text: '已忽略', type: 'default' },
        3: { text: '已拦截', type: 'error' }
      }
      const c = config[row.status] || { text: '未知', type: 'default' }
      return h(NTag, { type: c.type, size: 'small' }, { default: () => c.text })
    }
  },
  {
    title: '时间',
    key: 'createTime',
    width: 150,
    render: (row) => formatDateTime(row.createTime)
  },
  {
    title: '操作',
    key: 'actions',
    width: 80,
    render: (row) => {
      if (row.status === 0) {
        return h(NButton, {
          size: 'small',
          quaternary: true,
          type: 'primary',
          onClick: () => handleProcessEvent(row)
        }, { default: () => '处理' })
      }
      return '-'
    }
  }
]

// 安全状态相关
const getSafetyStatusClass = () => {
  const map = {
    safe: 'bg-green-50 border-green-100',
    warning: 'bg-orange-50 border-orange-100',
    danger: 'bg-red-50 border-red-100'
  }
  return map[stats.safetyStatus] || map.safe
}

const getSafetyTextClass = () => {
  const map = {
    safe: 'text-green-600',
    warning: 'text-orange-600',
    danger: 'text-red-600'
  }
  return map[stats.safetyStatus] || map.safe
}

const getSafetyIconClass = () => {
  const map = {
    safe: 'bg-green-100 text-green-500',
    warning: 'bg-orange-100 text-orange-500',
    danger: 'bg-red-100 text-red-500'
  }
  return map[stats.safetyStatus] || map.safe
}

const getSafetyIcon = () => {
  const map = {
    safe: 'fas fa-shield-check',
    warning: 'fas fa-exclamation-triangle',
    danger: 'fas fa-exclamation-circle'
  }
  return map[stats.safetyStatus] || map.safe
}

const getSafetyStatusText = () => {
  const map = {
    safe: '安全',
    warning: '警告',
    danger: '危险'
  }
  return map[stats.safetyStatus] || '安全'
}

// 加载风控统计
const loadStats = async () => {
  try {
    const res = await getRiskStats()
    if (res.data) {
      stats.todayBlocked = res.data.todayBlocked || 0
      stats.riskOrders = res.data.riskOrders || 0
      stats.suspiciousUsers = res.data.suspiciousUsers || 0
      stats.safetyStatus = res.data.safetyStatus || 'safe'
    }
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

// 加载风险告警
const loadAlerts = async () => {
  loading.value = true
  try {
    const res = await getRiskAlerts()
    if (res.data) {
      alerts.value = res.data.map(a => ({
        id: a.id,
        level: a.level || 'medium',
        icon: a.icon || 'fas fa-exclamation-triangle',
        title: a.title || '风险告警',
        description: a.description || '',
        time: a.time,
        type: a.type,
        orderId: a.orderId,
        userId: a.userId,
        amount: a.amount
      }))
    }
  } catch (e) {
    console.error('加载告警失败', e)
  } finally {
    loading.value = false
  }
}

// 加载趋势数据
const loadTrend = async () => {
  try {
    const res = await getRiskTrend()
    if (res.data) {
      trendData.value = res.data.map(t => ({
        date: t.date,
        high: t.high || 0,
        medium: t.medium || 0,
        low: t.low || 0
      }))
    }
  } catch (e) {
    console.error('加载趋势失败', e)
  }
}

// 加载风控规则
const loadRules = async () => {
  try {
    const res = await getRiskRules()
    if (res.data) {
      Object.assign(rules, res.data)
      rulesChanged.value = false
    }
  } catch (e) {
    console.error('加载规则失败', e)
  }
}

// 保存风控规则
const saveRules = async () => {
  savingRules.value = true
  try {
    await apiSaveRiskRules(rules)
    message.success('风控规则保存成功')
    rulesChanged.value = false
  } catch (e) {
    message.error('保存失败')
  } finally {
    savingRules.value = false
  }
}

// 打开全部事件弹窗
const openAllEvents = () => {
  showAllEvents.value = true
  // 重置筛选条件
  eventFilter.riskLevel = null
  eventFilter.status = null
  eventPagination.page = 1
  loadEventList()
}

// 加载事件列表
const loadEventList = async () => {
  loadingEvents.value = true
  try {
    const res = await getRiskEventList({
      page: eventPagination.page,
      size: eventPagination.pageSize,
      riskLevel: eventFilter.riskLevel,
      status: eventFilter.status
    })
    if (res.data) {
      eventList.value = res.data.records
      eventPagination.itemCount = res.data.total
      eventPagination.pageCount = Math.ceil(res.data.total / eventPagination.pageSize)
    }
  } catch (e) {
    console.error('加载事件列表失败', e)
  } finally {
    loadingEvents.value = false
  }
}

const handleEventPageChange = (page) => {
  eventPagination.page = page
  loadEventList()
}

const formatTime = (dateVal) => {
  if (!dateVal) return ''
  let date
  if (typeof dateVal === 'string') {
    date = new Date(dateVal.replace('T', ' '))
  } else if (Array.isArray(dateVal)) {
    date = new Date(dateVal[0], dateVal[1] - 1, dateVal[2], dateVal[3] || 0, dateVal[4] || 0, dateVal[5] || 0)
  } else {
    return ''
  }
  const now = new Date()
  const diff = Math.floor((now - date) / 1000 / 60)

  if (diff < 60) return `${diff}分钟前`
  if (diff < 1440) return `${Math.floor(diff / 60)}小时前`
  return `${Math.floor(diff / 1440)}天前`
}

const formatDateTime = (dateVal) => {
  if (!dateVal) return '-'
  let date
  if (typeof dateVal === 'string') {
    date = new Date(dateVal.replace('T', ' '))
  } else if (Array.isArray(dateVal)) {
    date = new Date(dateVal[0], dateVal[1] - 1, dateVal[2], dateVal[3] || 0, dateVal[4] || 0, dateVal[5] || 0)
  } else {
    return '-'
  }
  return date.toLocaleString('zh-CN')
}

const getLevelClass = (level) => {
  const map = {
    high: 'border-red-200 bg-red-50/50',
    medium: 'border-orange-200 bg-orange-50/50',
    low: 'border-yellow-200 bg-yellow-50/50'
  }
  return map[level] || 'border-gray-200'
}

const getLevelIconClass = (level) => {
  const map = {
    high: 'bg-red-100 text-red-500',
    medium: 'bg-orange-100 text-orange-500',
    low: 'bg-yellow-100 text-yellow-600'
  }
  return map[level] || 'bg-gray-100 text-gray-500'
}

const handleAlert = (alert) => {
  currentAlert.value = alert
  processForm.status = 1
  processForm.remark = ''
  showProcessModal.value = true
}

const handleProcessEvent = (event) => {
  currentAlert.value = {
    id: event.id,
    level: event.riskLevel,
    icon: 'fas fa-exclamation-triangle',
    title: event.title,
    description: event.description
  }
  processForm.status = 1
  processForm.remark = ''
  showProcessModal.value = true
}

const confirmProcess = async () => {
  if (!currentAlert.value) return
  
  // 如果是从风险事件表来的
  if (typeof currentAlert.value.id === 'number') {
    processing.value = true
    try {
      await processRiskEvent(currentAlert.value.id, processForm)
      message.success('处理成功')
      showProcessModal.value = false
      loadAlerts()
      loadStats()
      if (showAllEvents.value) {
        loadEventList()
      }
    } catch (e) {
      message.error('处理失败')
    } finally {
      processing.value = false
    }
  } else {
    // 临时告警（如订单纠纷）直接从列表移除
    const index = alerts.value.findIndex(a => a.id === currentAlert.value.id)
    if (index > -1) {
      alerts.value.splice(index, 1)
    }
    message.success('已处理')
    showProcessModal.value = false
  }
}

onMounted(() => {
  loadStats()
  loadAlerts()
  loadTrend()
  loadRules()
})
</script>
