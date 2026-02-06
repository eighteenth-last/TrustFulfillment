<template>
  <div class="space-y-6">
    <!-- 页面标题和筛选 -->
    <div class="flex items-center justify-between">
      <h2 class="text-xl font-black text-gray-800">我的项目</h2>
      <div class="flex items-center gap-3">
        <n-select 
          v-model:value="filterStatus" 
          :options="statusOptions"
          class="w-36"
          placeholder="全部状态"
          clearable
          @update:value="loadOrders"
        />
        <n-input 
          v-model:value="searchKeyword"
          placeholder="搜索项目名称"
          class="w-48"
        >
          <template #prefix><i class="fas fa-search text-gray-300"></i></template>
        </n-input>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-5 gap-4">
      <div class="bg-white p-5 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">全部项目</p>
        <p class="text-2xl font-black text-gray-800 mt-1">{{ stats.total }}</p>
      </div>
      <div class="bg-white p-5 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">待支付</p>
        <p class="text-2xl font-black text-red-500 mt-1">{{ stats.needPay }}</p>
      </div>
      <div class="bg-white p-5 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">进行中</p>
        <p class="text-2xl font-black mt-1" style="color: #00AFE1;">{{ stats.inProgress }}</p>
      </div>
      <div class="bg-white p-5 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">待验收</p>
        <p class="text-2xl font-black text-orange-500 mt-1">{{ stats.pending }}</p>
      </div>
      <div class="bg-white p-5 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">已完成</p>
        <p class="text-2xl font-black text-green-500 mt-1">{{ stats.completed }}</p>
      </div>
    </div>

    <!-- 订单列表 -->
    <n-spin :show="loading">
      <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="divide-y divide-gray-50">
          <div 
            v-for="order in filteredOrders" 
            :key="order.id"
            class="p-6 hover:bg-slate-50 transition cursor-pointer group"
            @click="router.push(`/user/orders/${order.id}`)"
          >
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-5">
                <div class="w-14 h-14 bg-slate-100 rounded-2xl flex items-center justify-center text-2xl transition order-icon">
                  <i :class="getOrderIcon(order.status)"></i>
                </div>
                <div>
                  <h4 class="font-bold text-gray-800 transition order-title">{{ order.title }}</h4>
                  <div class="flex gap-4 mt-1.5 text-xs text-gray-400">
                    <span>订单号: {{ order.orderNo }}</span>
                    <span>服务商: {{ order.sellerName || '待接单' }}</span>
                    <span>创建时间: {{ formatDate(order.createTime) }}</span>
                  </div>
                </div>
              </div>
              <div class="flex items-center gap-4">
                <div class="text-right">
                  <p class="text-xs text-gray-400">托管金额</p>
                  <p class="text-lg font-black text-gray-800">¥ {{ (order.totalAmount || 0).toLocaleString() }}</p>
                </div>
                <span :class="getStatusClass(order.status)" class="px-3 py-1 rounded-full text-xs font-bold">
                  {{ getStatusText(order.status) }}
                </span>
                <i class="fas fa-chevron-right text-gray-300 transition order-arrow"></i>
              </div>
            </div>

            <!-- 进度条 -->
            <div v-if="order.status === 2" class="mt-4 pt-4 border-t border-gray-100">
              <div class="flex items-center justify-between text-xs text-gray-400 mb-2">
                <span>当前阶段: {{ order.currentStage || '进行中' }}</span>
                <span>{{ order.completedStages || 0 }}/{{ order.totalStages || 1 }} 阶段已完成</span>
              </div>
              <div class="w-full bg-gray-100 h-1.5 rounded-full overflow-hidden">
                <div 
                  class="h-full rounded-full transition-all"
                  style="background: #00AFE1;"
                  :style="{ width: `${((order.completedStages || 0) / (order.totalStages || 1)) * 100}%` }"
                ></div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="!loading && filteredOrders.length === 0" class="p-20 text-center">
            <div class="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <i class="fas fa-folder-open text-3xl text-gray-300"></i>
            </div>
            <p class="text-gray-400">暂无项目数据</p>
            <n-button type="primary" color="#00AFE1" class="mt-4" @click="router.push('/user/create-order')">
              发布新项目
            </n-button>
          </div>
        </div>
      </div>
    </n-spin>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { getOrderList, getOrderStats } from '@/api/order'

const router = useRouter()
const message = useMessage()
const loading = ref(false)
const filterStatus = ref(null)
const searchKeyword = ref('')

const statusOptions = [
  { label: '待支付', value: 1 },
  { label: '待接单', value: 0 },
  { label: '进行中', value: 2 },
  { label: '待验收', value: 3 },
  { label: '已完成', value: 4 },
  { label: '纠纷中', value: 6 }
]

// 订单数据
const orders = ref([])

// 统计数据
const stats = reactive({
  total: 0,
  needPay: 0,
  inProgress: 0,
  pending: 0,
  completed: 0
})

// 过滤后的订单
const filteredOrders = computed(() => {
  let result = orders.value
  if (searchKeyword.value) {
    result = result.filter(o => o.title && o.title.includes(searchKeyword.value))
  }
  return result
})

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const params = { page: 1, size: 50 }
    if (filterStatus.value !== null) {
      params.status = filterStatus.value
    }
    
    const res = await getOrderList(params)
    if (res.data && res.data.records) {
      orders.value = res.data.records
    } else if (res.data) {
      orders.value = Array.isArray(res.data) ? res.data : []
    }
  } catch (e) {
    console.error('加载订单失败', e)
    message.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await getOrderStats()
    console.log('统计数据响应:', res)
    if (res && res.data) {
      stats.total = res.data.total ?? 0
      stats.needPay = res.data.needPay ?? 0
      stats.inProgress = res.data.inProgress ?? 0
      stats.pending = res.data.pending ?? 0
      stats.completed = res.data.completed ?? 0
      console.log('更新后的统计:', stats)
    }
  } catch (e) {
    console.error('加载统计失败', e)
    // 如果API失败，从订单列表计算统计
    if (orders.value.length > 0) {
      stats.total = orders.value.length
      stats.needPay = orders.value.filter(o => o.status === 1).length
      stats.inProgress = orders.value.filter(o => o.status === 2).length
      stats.pending = orders.value.filter(o => o.status === 3).length
      stats.completed = orders.value.filter(o => o.status === 4).length
    }
  }
}

const getStatusText = (status) => {
  const map = { 0: '待接单', 1: '待托管', 2: '进行中', 3: '待验收', 4: '已完成', 5: '已取消', 6: '纠纷中', 7: '待支付下阶段', 8: '待签合同', 9: '质保中' }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = {
    0: 'bg-gray-100 text-gray-500',
    1: 'bg-orange-50 text-orange-500',
    2: 'status-inprogress',
    3: 'bg-purple-50 text-purple-500',
    4: 'bg-green-50 text-green-500',
    5: 'bg-gray-100 text-gray-400',
    6: 'bg-red-50 text-red-500',
    7: 'bg-yellow-50 text-yellow-600',
    8: 'bg-amber-50 text-amber-600',
    9: 'bg-teal-50 text-teal-600'
  }
  return map[status] || 'bg-gray-100 text-gray-500'
}

const getOrderIcon = (status) => {
  const map = {
    0: 'fas fa-clock',
    1: 'fas fa-hourglass-half',
    2: 'fas fa-spinner fa-spin',
    3: 'fas fa-clipboard-check',
    4: 'fas fa-check-circle',
    5: 'fas fa-ban',
    6: 'fas fa-exclamation-triangle'
  }
  return map[status] || 'fas fa-file'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  // 处理 ISO 格式或普通格式
  if (dateStr.includes('T')) {
    return dateStr.split('T')[0]
  }
  return dateStr.split(' ')[0]
}

onMounted(async () => {
  await loadOrders()
  await loadStats()
  // 如果 stats API 没有返回数据，从订单列表计算
  if (stats.total === 0 && orders.value.length > 0) {
    stats.total = orders.value.length
    stats.inProgress = orders.value.filter(o => o.status === 2).length
    stats.pending = orders.value.filter(o => o.status === 3).length
    stats.completed = orders.value.filter(o => o.status === 4).length
  }
})
</script>

<style scoped>
.status-inprogress {
  background: rgba(0, 175, 225, 0.1);
  color: #00AFE1;
}
.group:hover .order-icon {
  background: #00AFE1;
  color: white;
}
.group:hover .order-title {
  color: #00AFE1;
}
.group:hover .order-arrow {
  color: #00AFE1;
}
</style>
