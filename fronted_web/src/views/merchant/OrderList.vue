<template>
  <div class="space-y-6">
    <!-- 页面标题和筛选 -->
    <div class="flex items-center justify-between">
      <h2 class="text-xl font-black text-white">我的订单</h2>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="bg-slate-800 p-5 rounded-2xl border border-slate-700">
        <p class="text-xs text-gray-400 font-bold">全部订单</p>
        <p class="text-2xl font-black text-white mt-1">{{ stats.total }}</p>
      </div>
      <div class="bg-slate-800 p-5 rounded-2xl border border-slate-700">
        <p class="text-xs text-gray-400 font-bold">进行中</p>
        <p class="text-2xl font-black text-tf mt-1">{{ stats.inProgress }}</p>
      </div>
      <div class="bg-slate-800 p-5 rounded-2xl border border-slate-700">
        <p class="text-xs text-gray-400 font-bold">待交付</p>
        <p class="text-2xl font-black text-orange-400 mt-1">{{ stats.pending }}</p>
      </div>
      <div class="bg-slate-800 p-5 rounded-2xl border border-slate-700">
        <p class="text-xs text-gray-400 font-bold">已完成</p>
        <p class="text-2xl font-black text-green-400 mt-1">{{ stats.completed }}</p>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="bg-slate-800 rounded-3xl border border-slate-700 overflow-hidden">
      <div class="divide-y divide-slate-700">
        <div 
          v-for="order in filteredOrders" 
          :key="order.id"
          class="p-6 hover:bg-slate-700/30 transition cursor-pointer group"
          @click="router.push(`/merchant/orders/${order.id}`)"
        >
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-5">
              <div class="w-14 h-14 bg-slate-700 rounded-2xl flex items-center justify-center text-2xl group-hover:bg-tf group-hover:text-white transition">
                <i :class="getOrderIcon(order.status)"></i>
              </div>
              <div>
                <h4 class="font-bold text-white group-hover:text-tf transition">{{ order.title }}</h4>
                <div class="flex gap-4 mt-1.5 text-xs text-gray-400">
                  <span>订单号: {{ order.orderNo }}</span>
                  <span>甲方: {{ order.buyerName }}</span>
                  <span>创建时间: {{ order.createTime }}</span>
                </div>
              </div>
            </div>
            <div class="flex items-center gap-8">
              <div class="text-right">
                <p class="text-xs text-gray-400">订单金额</p>
                <p class="text-lg font-black text-white">¥ {{ order.totalAmount.toLocaleString() }}</p>
              </div>
              <span :class="getStatusClass(order.status)" class="px-3 py-1 rounded-full text-xs font-bold">
                {{ getStatusText(order.status) }}
              </span>
              <n-button 
                v-if="order.status === 2" 
                type="primary" 
                color="#00AFE1" 
                size="small"
                @click.stop="router.push(`/merchant/orders/${order.id}`)"
              >
                提交交付
              </n-button>
              <i class="fas fa-chevron-right text-gray-500 group-hover:text-tf transition"></i>
            </div>
          </div>

          <!-- 进度条 -->
          <div v-if="order.status === 2" class="mt-4 pt-4 border-t border-slate-700">
            <div class="flex items-center justify-between text-xs text-gray-400 mb-2">
              <span>当前阶段: {{ order.currentStage }}</span>
              <span>{{ order.completedStages }}/{{ order.totalStages }} 阶段已完成</span>
            </div>
            <div class="w-full bg-slate-600 h-1.5 rounded-full overflow-hidden">
              <div 
                class="bg-tf h-full rounded-full transition-all"
                :style="{ width: `${(order.completedStages / order.totalStages) * 100}%` }"
              ></div>
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="p-20 text-center">
          <n-spin size="large" />
          <p class="text-gray-400 mt-4">加载中...</p>
        </div>

        <!-- 空状态 -->
        <div v-else-if="filteredOrders.length === 0" class="p-20 text-center">
          <div class="w-20 h-20 bg-slate-700 rounded-full flex items-center justify-center mx-auto mb-4">
            <i class="fas fa-folder-open text-3xl text-gray-500"></i>
          </div>
          <p class="text-gray-400">暂无订单数据</p>
          <n-button type="primary" color="#00AFE1" class="mt-4" @click="router.push('/merchant/tasks')">
            去任务市场接单
          </n-button>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="total > pageSize" class="p-4 border-t border-slate-700 flex justify-center">
        <n-pagination 
          v-model:page="currentPage" 
          :page-count="Math.ceil(total / pageSize)"
          show-quick-jumper
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, inject } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { getOrderList, getOrderStats } from '@/api/order'

const router = useRouter()
const message = useMessage()

// 注入刷新余额方法
const refreshBalance = inject('refreshBalance', () => {})
const filterStatus = ref(null)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const statusOptions = [
  { label: '进行中', value: 2 },
  { label: '待验收', value: 3 },
  { label: '已完成', value: 4 },
  { label: '纠纷中', value: 6 }
]

// 订单列表数据
const orders = ref([])

// 统计数据
const stats = reactive({
  total: 0,
  inProgress: 0,
  pending: 0,
  completed: 0
})

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    if (filterStatus.value !== null) {
      params.status = filterStatus.value
    }
    
    const res = await getOrderList(params)
    if (res.data && res.data.records) {
      orders.value = res.data.records.map(order => ({
        id: order.id,
        orderNo: order.orderNo || `TF${order.id}`,
        title: order.title,
        totalAmount: order.totalAmount || 0,
        status: order.status,
        buyerName: order.buyerName || '未知',
        createTime: formatDate(order.createTime),
        currentStage: getCurrentStage(order),
        completedStages: order.completedStages || 0,
        totalStages: order.totalStages || 1
      }))
      total.value = res.data.total || 0
    }
  } catch (e) {
    console.error('加载订单列表失败', e)
    message.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await getOrderStats()
    if (res.data) {
      stats.total = res.data.total || 0
      stats.inProgress = res.data.inProgress || 0
      stats.pending = res.data.pending || 0
      stats.completed = res.data.completed || 0
    }
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

// 获取当前阶段
const getCurrentStage = (order) => {
  if (order.status === 4) return '-'
  if (order.currentStageName) return order.currentStageName
  return `阶段 ${(order.completedStages || 0) + 1}`
}

const filteredOrders = computed(() => {
  // 由于已在API请求中过滤，这里直接返回
  return orders.value
})

// 监听筛选状态变化
watch(filterStatus, () => {
  currentPage.value = 1
  loadOrders()
})

// 监听分页变化
watch(currentPage, () => {
  loadOrders()
})

const getStatusText = (status) => {
  const map = { 0: '待接单', 1: '待托管', 2: '进行中', 3: '待验收', 4: '已完成', 5: '已取消', 6: '纠纷中', 7: '待甲方付款', 8: '待签合同', 9: '质保中' }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = {
    0: 'bg-gray-500/20 text-gray-400',
    1: 'bg-orange-500/20 text-orange-400',
    2: 'bg-tf/20 text-tf',
    3: 'bg-purple-500/20 text-purple-400',
    4: 'bg-green-500/20 text-green-400',
    5: 'bg-gray-500/20 text-gray-500',
    6: 'bg-red-500/20 text-red-400',
    7: 'bg-yellow-500/20 text-yellow-400',
    8: 'bg-amber-500/20 text-amber-400',
    9: 'bg-teal-500/20 text-teal-400'
  }
  return map[status] || 'bg-gray-500/20 text-gray-400'
}

const getOrderIcon = (status) => {
  const map = {
    0: 'fas fa-clock text-gray-400',
    1: 'fas fa-hourglass-half text-orange-400',
    2: 'fas fa-spinner fa-spin text-tf',
    3: 'fas fa-clipboard-check text-purple-400',
    4: 'fas fa-check-circle text-green-400',
    5: 'fas fa-ban text-gray-500',
    6: 'fas fa-exclamation-triangle text-red-400'
  }
  return map[status] || 'fas fa-file text-gray-400'
}

onMounted(() => {
  loadOrders()
  loadStats()
  refreshBalance() // 刷新顶部余额
})
</script>
