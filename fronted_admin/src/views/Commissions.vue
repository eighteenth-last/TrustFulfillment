<template>
  <div class="p-6">
    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
      <div class="bg-white rounded-2xl shadow-sm p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">累计提成</p>
            <p class="text-2xl font-bold text-tf mt-1">¥{{ formatMoney(stats.totalCommission) }}</p>
          </div>
          <div class="w-12 h-12 bg-tf/10 rounded-xl flex items-center justify-center">
            <i class="fas fa-coins text-tf text-xl"></i>
          </div>
        </div>
      </div>
      
      <div class="bg-white rounded-2xl shadow-sm p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">本月提成</p>
            <p class="text-2xl font-bold text-green-500 mt-1">¥{{ formatMoney(stats.monthCommission) }}</p>
          </div>
          <div class="w-12 h-12 bg-green-50 rounded-xl flex items-center justify-center">
            <i class="fas fa-calendar-alt text-green-500 text-xl"></i>
          </div>
        </div>
      </div>
      
      <div class="bg-white rounded-2xl shadow-sm p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">今日提成</p>
            <p class="text-2xl font-bold text-orange-500 mt-1">¥{{ formatMoney(stats.todayCommission) }}</p>
          </div>
          <div class="w-12 h-12 bg-orange-50 rounded-xl flex items-center justify-center">
            <i class="fas fa-chart-line text-orange-500 text-xl"></i>
          </div>
        </div>
      </div>
      
      <div class="bg-white rounded-2xl shadow-sm p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">提成记录数</p>
            <p class="text-2xl font-bold text-purple-500 mt-1">{{ stats.totalCount }}</p>
          </div>
          <div class="w-12 h-12 bg-purple-50 rounded-xl flex items-center justify-center">
            <i class="fas fa-receipt text-purple-500 text-xl"></i>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 提成趋势图 -->
    <div class="bg-white rounded-2xl shadow-sm p-6 mb-6">
      <h3 class="text-lg font-semibold mb-4">提成趋势</h3>
      <div ref="trendChartRef" class="h-64"></div>
    </div>
    
    <!-- 提成记录列表 -->
    <div class="bg-white rounded-2xl shadow-sm">
      <div class="p-6 border-b border-gray-100">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold">提成记录</h3>
          <div class="flex items-center gap-4">
            <n-input v-model:value="searchKeyword" placeholder="搜索订单号/商户" clearable class="w-48">
              <template #prefix>
                <i class="fas fa-search text-gray-400"></i>
              </template>
            </n-input>
            <n-button @click="loadData">
              <template #icon><i class="fas fa-sync-alt"></i></template>
              刷新
            </n-button>
          </div>
        </div>
      </div>
      
      <n-data-table
        :columns="columns"
        :data="commissionList"
        :loading="loading"
        :pagination="pagination"
        :bordered="false"
        :single-line="false"
        @update:page="handlePageChange"
      />
    </div>
    
    <!-- 详情弹窗 -->
    <n-modal v-model:show="showDetailModal" preset="card" title="提成详情" style="width: 600px;">
      <div v-if="currentCommission" class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="text-sm text-gray-500">订单编号</label>
            <p class="font-medium">{{ currentCommission.orderNo }}</p>
          </div>
          <div>
            <label class="text-sm text-gray-500">订单标题</label>
            <p class="font-medium">{{ currentCommission.orderTitle }}</p>
          </div>
          <div>
            <label class="text-sm text-gray-500">商户名称</label>
            <p class="font-medium">{{ currentCommission.merchantName || currentCommission.shopName || '-' }}</p>
          </div>
          <div>
            <label class="text-sm text-gray-500">阶段类型</label>
            <p class="font-medium">{{ getStageTypeName(currentCommission.stageType) }}</p>
          </div>
        </div>
        
        <n-divider />
        
        <div class="bg-gray-50 rounded-xl p-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="text-sm text-gray-500">释放金额</label>
              <p class="text-xl font-bold text-gray-800">¥{{ formatMoney(currentCommission.originalAmount) }}</p>
            </div>
            <div>
              <label class="text-sm text-gray-500">提成比例</label>
              <p class="text-xl font-bold text-orange-500">{{ currentCommission.commissionRate }}%</p>
            </div>
            <div>
              <label class="text-sm text-gray-500">提成金额</label>
              <p class="text-xl font-bold text-tf">¥{{ formatMoney(currentCommission.commissionAmount) }}</p>
            </div>
            <div>
              <label class="text-sm text-gray-500">商户实际到账</label>
              <p class="text-xl font-bold text-green-500">¥{{ formatMoney(currentCommission.actualAmount) }}</p>
            </div>
          </div>
        </div>
        
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="text-sm text-gray-500">创建时间</label>
            <p class="font-medium">{{ formatDateTime(currentCommission.createTime) }}</p>
          </div>
          <div>
            <label class="text-sm text-gray-500">备注</label>
            <p class="font-medium">{{ currentCommission.remark || '-' }}</p>
          </div>
        </div>
      </div>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, h, computed } from 'vue'
import { NButton, NTag, NDataTable, NInput, NModal, NDivider } from 'naive-ui'
import { getCommissionList, getCommissionStats, getCommissionDetail } from '@/api/admin'
import * as echarts from 'echarts'

const loading = ref(false)
const commissionList = ref([])
const searchKeyword = ref('')
const showDetailModal = ref(false)
const currentCommission = ref(null)

const stats = ref({
  totalCommission: 0,
  monthCommission: 0,
  todayCommission: 0,
  totalCount: 0,
  trendData: []
})

const pagination = ref({
  page: 1,
  pageSize: 10,
  pageCount: 1,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50],
  onChange: (page) => handlePageChange(page),
  onUpdatePageSize: (pageSize) => {
    pagination.value.pageSize = pageSize
    pagination.value.page = 1
    loadCommissions()
  }
})

// 图表
const trendChartRef = ref(null)
let trendChartInstance = null

const columns = [
  {
    title: '订单编号',
    key: 'orderNo',
    width: 180,
    ellipsis: { tooltip: true }
  },
  {
    title: '商户',
    key: 'merchantName',
    width: 120,
    render: (row) => row.merchantName || row.shopName || '-'
  },
  {
    title: '阶段类型',
    key: 'stageType',
    width: 100,
    render: (row) => {
      const type = row.stageType
      const typeMap = {
        1: { text: '首付款', color: 'info' },
        2: { text: '尾款', color: 'success' },
        3: { text: '质保款', color: 'warning' }
      }
      const config = typeMap[type] || { text: '未知', color: 'default' }
      return h(NTag, { type: config.color, size: 'small' }, { default: () => config.text })
    }
  },
  {
    title: '释放金额',
    key: 'originalAmount',
    width: 120,
    render: (row) => h('span', { class: 'text-gray-600' }, '¥' + formatMoney(row.originalAmount))
  },
  {
    title: '提成比例',
    key: 'commissionRate',
    width: 100,
    render: (row) => h('span', { class: 'text-orange-500 font-medium' }, row.commissionRate + '%')
  },
  {
    title: '提成金额',
    key: 'commissionAmount',
    width: 120,
    render: (row) => h('span', { class: 'text-tf font-bold' }, '¥' + formatMoney(row.commissionAmount))
  },
  {
    title: '实际到账',
    key: 'actualAmount',
    width: 120,
    render: (row) => h('span', { class: 'text-green-500 font-medium' }, '¥' + formatMoney(row.actualAmount))
  },
  {
    title: '时间',
    key: 'createTime',
    width: 160,
    render: (row) => formatDateTime(row.createTime)
  },
  {
    title: '操作',
    key: 'actions',
    width: 80,
    fixed: 'right',
    render: (row) => {
      return h(NButton, {
        size: 'small',
        quaternary: true,
        type: 'info',
        onClick: () => viewDetail(row)
      }, { default: () => '详情' })
    }
  }
]

const formatMoney = (value) => {
  if (value === null || value === undefined) return '0.00'
  return Number(value).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getStageTypeName = (type) => {
  const typeMap = { 1: '首付款', 2: '尾款', 3: '质保款' }
  return typeMap[type] || '未知'
}

const loadStats = async () => {
  try {
    const res = await getCommissionStats()
    if (res.code === 200) {
      stats.value = res.data
      initTrendChart()
    }
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

const loadCommissions = async () => {
  loading.value = true
  try {
    const res = await getCommissionList({
      page: pagination.value.page,
      size: pagination.value.pageSize
    })
    if (res.code === 200) {
      commissionList.value = res.data.records
      pagination.value.itemCount = res.data.total
      pagination.value.pageCount = Math.ceil(res.data.total / pagination.value.pageSize)
    }
  } catch (e) {
    console.error('加载提成列表失败', e)
  } finally {
    loading.value = false
  }
}

const loadData = () => {
  loadStats()
  loadCommissions()
}

const handlePageChange = (page) => {
  pagination.value.page = page
  loadCommissions()
}

const viewDetail = async (row) => {
  try {
    const res = await getCommissionDetail(row.id)
    if (res.code === 200) {
      currentCommission.value = res.data
      showDetailModal.value = true
    }
  } catch (e) {
    console.error('加载详情失败', e)
  }
}

const initTrendChart = () => {
  if (!trendChartRef.value || !stats.value.trendData) return
  
  if (trendChartInstance) {
    trendChartInstance.dispose()
  }
  
  trendChartInstance = echarts.init(trendChartRef.value)
  
  const months = stats.value.trendData.map(d => d.month)
  const amounts = stats.value.trendData.map(d => Number(d.amount) || 0)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => `${params[0].name}: ¥${formatMoney(params[0].value)}`
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: months,
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisLabel: { color: '#9ca3af' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } },
      axisLabel: {
        color: '#9ca3af',
        formatter: (value) => value >= 1000 ? (value / 1000).toFixed(0) + 'k' : value
      }
    },
    series: [{
      type: 'bar',
      barWidth: '50%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#00AFE1' },
          { offset: 1, color: '#67E8F9' }
        ]),
        borderRadius: [4, 4, 0, 0]
      },
      data: amounts
    }]
  }
  
  trendChartInstance.setOption(option)
}

const handleResize = () => {
  if (trendChartInstance) {
    trendChartInstance.resize()
  }
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (trendChartInstance) {
    trendChartInstance.dispose()
  }
})
</script>
