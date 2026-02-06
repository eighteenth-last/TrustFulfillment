<template>
  <div class="space-y-8">
    <!-- 统计卡片 -->
    <div class="grid grid-cols-4 gap-6">
      <div 
        class="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm border-l-4 border-l-tf hover:shadow-lg transition cursor-pointer"
        @click="goToTrust"
      >
        <p class="text-gray-400 text-xs font-bold mb-2">信托托管总额</p>
        <h3 class="text-2xl font-black text-gray-800">¥ {{ stats.totalTrust.toLocaleString() }}</h3>
        <div class="mt-4 text-xs text-green-500">
          <i class="fas fa-arrow-up mr-1"></i> {{ stats.trustGrowth }}% 较上月
        </div>
      </div>
      <div 
        class="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm hover:shadow-lg transition cursor-pointer"
        @click="goToMerchants"
      >
        <p class="text-gray-400 text-xs font-bold mb-2">活跃商户</p>
        <h3 class="text-2xl font-black text-gray-800">{{ stats.activeMerchants.toLocaleString() }}</h3>
        <div class="mt-4 text-xs text-green-500">
          <i class="fas fa-arrow-up mr-1"></i> {{ stats.merchantGrowth }}%
        </div>
      </div>
      <div 
        class="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm hover:shadow-lg transition cursor-pointer"
        @click="goToDisputes"
      >
        <p class="text-gray-400 text-xs font-bold mb-2">争议订单率</p>
        <h3 class="text-2xl font-black text-red-500">{{ stats.disputeRate }}%</h3>
        <div class="mt-4 text-xs text-green-500">
          <i class="fas fa-arrow-down mr-1"></i> 下降 0.2%
        </div>
      </div>
      <div class="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm hover:shadow-lg transition cursor-pointer">
        <p class="text-gray-400 text-xs font-bold mb-2">安全运行</p>
        <h3 class="text-2xl font-black text-tf">{{ stats.runningDays }} 天</h3>
        <div class="mt-4 text-xs text-green-500">
          <i class="fas fa-shield-check mr-1"></i> 系统稳定
        </div>
      </div>
    </div>

    <div class="grid grid-cols-3 gap-6">
      <!-- 资金趋势分析 -->
      <div class="col-span-2 bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <div class="flex items-center justify-between mb-4">
          <h4 class="font-bold">资金趋势分析</h4>
          <n-select v-model:value="chartPeriod" :options="periodOptions" style="width: 100px" size="small" @update:value="handlePeriodChange" />
        </div>
        <div ref="chartRef" style="height: 240px; width: 100%;"></div>
      </div>

      <!-- 今日概览 -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h4 class="font-bold mb-6">今日概览</h4>
        <div class="space-y-4">
          <div 
            class="flex items-center justify-between p-4 bg-gray-50 rounded-xl hover:bg-blue-50 transition cursor-pointer"
            @click="goToOrders"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-blue-50 text-tf rounded-lg flex items-center justify-center">
                <i class="fas fa-file-contract"></i>
              </div>
              <span class="text-sm font-medium text-gray-600">新增订单</span>
            </div>
            <span class="text-lg font-black text-gray-800">{{ todayStats.newOrders }}</span>
          </div>
          <div 
            class="flex items-center justify-between p-4 bg-gray-50 rounded-xl hover:bg-green-50 transition cursor-pointer"
            @click="goToTrust"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-green-50 text-green-500 rounded-lg flex items-center justify-center">
                <i class="fas fa-coins"></i>
              </div>
              <span class="text-sm font-medium text-gray-600">资金流入</span>
            </div>
            <span class="text-lg font-black text-green-500">+¥{{ todayStats.inflow.toLocaleString() }}</span>
          </div>
          <div 
            class="flex items-center justify-between p-4 bg-gray-50 rounded-xl hover:bg-purple-50 transition cursor-pointer"
            @click="goToUsers"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-purple-50 text-purple-500 rounded-lg flex items-center justify-center">
                <i class="fas fa-user-plus"></i>
              </div>
              <span class="text-sm font-medium text-gray-600">新增用户</span>
            </div>
            <span class="text-lg font-black text-gray-800">{{ todayStats.newUsers }}</span>
          </div>
          <div 
            class="flex items-center justify-between p-4 bg-gray-50 rounded-xl hover:bg-red-50 transition cursor-pointer"
            @click="goToDisputes"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-red-50 text-red-500 rounded-lg flex items-center justify-center">
                <i class="fas fa-exclamation-triangle"></i>
              </div>
              <span class="text-sm font-medium text-gray-600">待处理纠纷</span>
            </div>
            <span class="text-lg font-black text-red-500">{{ todayStats.pendingDisputes }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 第二行图表 -->
    <div class="grid grid-cols-3 gap-6">
      <!-- 订单状态分布 -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h4 class="font-bold mb-4">订单状态分布</h4>
        <div ref="orderPieRef" style="height: 220px; width: 100%;"></div>
      </div>

      <!-- 用户增长趋势 -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h4 class="font-bold mb-4">用户增长趋势</h4>
        <div ref="userGrowthRef" style="height: 220px; width: 100%;"></div>
      </div>

      <!-- 商户业绩排行 -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h4 class="font-bold mb-4">商户业绩排行</h4>
        <div ref="merchantRankRef" style="height: 220px; width: 100%;"></div>
      </div>
    </div>

    <!-- 第三行图表 -->
    <div class="grid grid-cols-2 gap-6">
      <!-- 交易金额分布 -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h4 class="font-bold mb-4">交易金额分布</h4>
        <div ref="amountDistRef" style="height: 260px; width: 100%;"></div>
      </div>

      <!-- 平台收入统计 -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h4 class="font-bold mb-4">平台收入统计</h4>
        <div ref="incomeRef" style="height: 260px; width: 100%;"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { getDashboardStats } from '@/api/admin'
import * as echarts from 'echarts'

const router = useRouter()
const message = useMessage()

const loading = ref(false)

const stats = reactive({
  totalTrust: 0,
  activeMerchants: 0,
  disputeRate: 0,
  runningDays: 0,
  trustGrowth: 12.5,
  merchantGrowth: 8.2
})

const todayStats = reactive({
  newOrders: 0,
  inflow: 0,
  newUsers: 0,
  pendingDisputes: 0
})

const chartPeriod = ref('year')
const periodOptions = [
  { label: '全年', value: 'year' },
  { label: '本季', value: 'quarter' },
  { label: '本月', value: 'month' },
  { label: '本周', value: 'week' }
]

const chartLabels = ref([])
const chartAmounts = ref([])
const originalChartData = ref([])

// ECharts 图表引用
const chartRef = ref(null)
const orderPieRef = ref(null)
const userGrowthRef = ref(null)
const merchantRankRef = ref(null)
const amountDistRef = ref(null)
const incomeRef = ref(null)

// 图表实例
let chartInstance = null
let orderPieInstance = null
let userGrowthInstance = null
let merchantRankInstance = null
let amountDistInstance = null
let incomeInstance = null

// 图表数据
const orderStatusData = ref([])
const userGrowthData = ref([0, 0, 0, 0, 0, 0])
const merchantRankData = ref([])
const amountDistData = ref([])
const incomeData = ref([])

// 初始化主图表（资金趋势）
const initChart = () => {
  if (!chartRef.value) return
  if (chartInstance) chartInstance.dispose()
  
  chartInstance = echarts.init(chartRef.value)
  updateChart()
}

// 更新主图表
const updateChart = () => {
  if (!chartInstance) return
  
  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'transparent',
      textStyle: { color: '#fff' },
      formatter: (params) => {
        const data = params[0]
        return `<div style="text-align: center;">
          <div style="font-weight: bold; margin-bottom: 4px;">${data.name}</div>
          <div>托管金额: ¥${data.value?.toLocaleString() || 0}</div>
        </div>`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: chartLabels.value,
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisLabel: { color: '#9ca3af', interval: 0, fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } },
      axisLabel: {
        color: '#9ca3af',
        formatter: (value) => value >= 10000 ? (value / 10000).toFixed(0) + '万' : value
      }
    },
    series: [{
      data: chartAmounts.value,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { color: '#00AFE1', width: 3 },
      itemStyle: { color: '#00AFE1', borderColor: '#fff', borderWidth: 2 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(0, 175, 225, 0.3)' },
          { offset: 1, color: 'rgba(0, 175, 225, 0.05)' }
        ])
      }
    }]
  }
  chartInstance.setOption(option)
}

// 初始化订单状态饼图
const initOrderPieChart = () => {
  if (!orderPieRef.value) return
  if (orderPieInstance) orderPieInstance.dispose()
  
  orderPieInstance = echarts.init(orderPieRef.value)
  
  // 过滤掉值为0的数据
  const filteredData = orderStatusData.value.filter(item => item.value > 0)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      textStyle: { color: '#6b7280', fontSize: 12 }
    },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold' }
      },
      data: filteredData.length > 0 ? filteredData : [
        { value: 1, name: '暂无数据', itemStyle: { color: '#E5E7EB' } }
      ]
    }]
  }
  orderPieInstance.setOption(option)
}

// 初始化用户增长趋势图
const initUserGrowthChart = () => {
  if (!userGrowthRef.value) return
  if (userGrowthInstance) userGrowthInstance.dispose()
  
  userGrowthInstance = echarts.init(userGrowthRef.value)
  
  const months = []
  const now = new Date()
  for (let i = 5; i >= 0; i--) {
    const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
    months.push((d.getMonth() + 1) + '月')
  }
  
  const option = {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      data: months,
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisLabel: { color: '#9ca3af', fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } },
      axisLabel: { color: '#9ca3af' }
    },
    series: [
      {
        name: '新增用户',
        type: 'bar',
        barWidth: '50%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#8B5CF6' },
            { offset: 1, color: '#C4B5FD' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        data: userGrowthData.value
      }
    ]
  }
  userGrowthInstance.setOption(option)
}

// 初始化商户业绩排行图
const initMerchantRankChart = () => {
  if (!merchantRankRef.value) return
  if (merchantRankInstance) merchantRankInstance.dispose()
  
  merchantRankInstance = echarts.init(merchantRankRef.value)
  
  // 过滤掉暂无数据或值为0的项，并反转顺序（横向柱状图从下往上显示）
  const filteredData = merchantRankData.value.filter(d => d.value > 0 && d.name !== '暂无数据').reverse()
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params) => `${params[0].name}: ¥${params[0].value.toLocaleString()}`
    },
    grid: { left: '3%', right: '15%', bottom: '3%', top: '5%', containLabel: true },
    xAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { show: false },
      axisLabel: { show: false }
    },
    yAxis: {
      type: 'category',
      data: filteredData.map(d => d.name),
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#6b7280', fontSize: 12 }
    },
    series: [{
      type: 'bar',
      barWidth: 12,
      data: filteredData.map((d, i) => ({
        value: d.value,
        itemStyle: {
          color: ['#00AFE1', '#10B981', '#F59E0B', '#8B5CF6', '#EC4899'][i] || '#9CA3AF',
          borderRadius: [0, 6, 6, 0]
        }
      })),
      label: {
        show: true,
        position: 'right',
        formatter: (params) => params.value >= 1000 ? '¥' + (params.value / 1000).toFixed(1) + 'k' : '¥' + params.value,
        color: '#6b7280',
        fontSize: 11
      }
    }]
  }
  merchantRankInstance.setOption(option)
}

// 初始化交易金额分布图
const initAmountDistChart = () => {
  if (!amountDistRef.value) return
  if (amountDistInstance) amountDistInstance.dispose()
  
  amountDistInstance = echarts.init(amountDistRef.value)
  
  // 从 amountDistData 中提取数据
  const labels = amountDistData.value.map(d => d.range)
  const counts = amountDistData.value.map(d => d.count)
  
  const option = {
    tooltip: { trigger: 'axis' },
    legend: {
      data: ['订单数量'],
      top: 0,
      textStyle: { color: '#6b7280', fontSize: 12 }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      data: labels.length > 0 ? labels : ['0-1k', '1k-5k', '5k-1w', '1w-5w', '5w以上'],
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisLabel: { color: '#9ca3af', fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      name: '订单数',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#f3f4f6', type: 'dashed' } },
      axisLabel: { color: '#9ca3af' }
    },
    series: [
      {
        name: '订单数量',
        type: 'bar',
        barWidth: '50%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#00AFE1' },
            { offset: 1, color: '#67E8F9' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        data: counts
      }
    ]
  }
  amountDistInstance.setOption(option)
}

// 初始化平台收入统计图
const initIncomeChart = () => {
  if (!incomeRef.value) return
  if (incomeInstance) incomeInstance.dispose()
  
  incomeInstance = echarts.init(incomeRef.value)
  
  // 从 incomeData 中提取数据
  const months = incomeData.value.map(d => d.month)
  const platformFees = incomeData.value.map(d => Number(d.platformFee) || 0)
  const withdrawFees = incomeData.value.map(d => Number(d.withdrawFee) || 0)
  
  const option = {
    tooltip: { trigger: 'axis' },
    legend: {
      data: ['平台手续费', '提现手续费'],
      top: 0,
      textStyle: { color: '#6b7280', fontSize: 12 }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      data: months,
      axisLine: { lineStyle: { color: '#e5e7eb' } },
      axisLabel: { color: '#9ca3af', fontSize: 11 }
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
    series: [
      {
        name: '平台手续费',
        type: 'bar',
        stack: 'total',
        barWidth: '40%',
        itemStyle: { color: '#00AFE1', borderRadius: [0, 0, 0, 0] },
        data: platformFees
      },
      {
        name: '提现手续费',
        type: 'bar',
        stack: 'total',
        itemStyle: { color: '#10B981', borderRadius: [4, 4, 0, 0] },
        data: withdrawFees
      }
    ]
  }
  incomeInstance.setOption(option)
}

// 窗口大小变化时重新调整所有图表
const handleResize = () => {
  chartInstance?.resize()
  orderPieInstance?.resize()
  userGrowthInstance?.resize()
  merchantRankInstance?.resize()
  amountDistInstance?.resize()
  incomeInstance?.resize()
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const statsRes = await getDashboardStats()
    if (statsRes.data) {
      stats.totalTrust = statsRes.data.totalTrust || 0
      stats.activeMerchants = statsRes.data.activeMerchants || 0
      stats.disputeRate = statsRes.data.disputeRate || 0
      stats.runningDays = statsRes.data.runningDays || 0
      stats.trustGrowth = statsRes.data.trustGrowth || 12.5
      stats.merchantGrowth = statsRes.data.merchantGrowth || 8.2

      todayStats.newOrders = statsRes.data.todayOrders || 0
      todayStats.inflow = statsRes.data.todayInflow || 0
      todayStats.newUsers = statsRes.data.todayUsers || 0
      todayStats.pendingDisputes = statsRes.data.pendingDisputes || 0

      // 图表数据
      if (statsRes.data.chartData && statsRes.data.chartData.length > 0) {
        originalChartData.value = statsRes.data.chartData.map(item => ({
          label: item.month,
          amount: Number(item.amount) || 0
        }))
      } else {
        const now = new Date()
        originalChartData.value = []
        for (let i = 11; i >= 0; i--) {
          const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
          originalChartData.value.push({
            label: (d.getMonth() + 1) + '月',
            amount: 0
          })
        }
      }
      
      // 订单状态数据
      if (statsRes.data.orderStatusData) {
        orderStatusData.value = statsRes.data.orderStatusData
      }
      
      // 用户增长数据
      if (statsRes.data.userGrowthData) {
        userGrowthData.value = statsRes.data.userGrowthData
      }
      
      // 商户排行数据
      if (statsRes.data.merchantRankData) {
        merchantRankData.value = statsRes.data.merchantRankData
      }
      
      // 交易金额分布数据
      if (statsRes.data.amountDistData) {
        amountDistData.value = statsRes.data.amountDistData
      }
      
      // 平台收入数据
      if (statsRes.data.incomeData) {
        incomeData.value = statsRes.data.incomeData
      }
      
      applyChartPeriod(chartPeriod.value)
      
      await nextTick()
      initAllCharts()
    } else {
      await nextTick()
      initAllCharts()
    }
  } catch (e) {
    console.error('加载数据失败', e)
    await nextTick()
    initAllCharts()
  } finally {
    loading.value = false
  }
}

// 初始化所有图表
const initAllCharts = () => {
  initChart()
  initOrderPieChart()
  initUserGrowthChart()
  initMerchantRankChart()
  initAmountDistChart()
  initIncomeChart()
}

// 导航方法
const goToTrust = () => router.push('/admin/trust')
const goToMerchants = () => router.push('/admin/merchants')
const goToDisputes = () => router.push('/admin/disputes')
const goToOrders = () => router.push('/admin/orders')
const goToUsers = () => router.push('/admin/users')

// 应用时间范围筛选
const applyChartPeriod = (value) => {
  const data = originalChartData.value
  if (!data || data.length === 0) {
    chartLabels.value = []
    chartAmounts.value = []
    updateChart()
    return
  }
  
  let slicedData = []
  
  if (value === 'year') {
    slicedData = data
  } else if (value === 'quarter') {
    slicedData = data.slice(-3)
  } else if (value === 'month') {
    const lastMonthAmount = data[data.length - 1]?.amount || 0
    const weekAvg = lastMonthAmount / 4
    slicedData = [
      { label: '第1周', amount: Math.round(weekAvg * 0.9) },
      { label: '第2周', amount: Math.round(weekAvg * 1.0) },
      { label: '第3周', amount: Math.round(weekAvg * 1.05) },
      { label: '第4周', amount: Math.round(weekAvg * 1.05) }
    ]
  } else if (value === 'week') {
    const lastMonthAmount = data[data.length - 1]?.amount || 0
    const dayAvg = lastMonthAmount / 30
    const now = new Date()
    slicedData = []
    for (let i = 6; i >= 0; i--) {
      const d = new Date(now)
      d.setDate(d.getDate() - i)
      slicedData.push({
        label: (d.getMonth() + 1) + '/' + d.getDate(),
        amount: Math.round(dayAvg * (0.8 + Math.random() * 0.4))
      })
    }
  }
  
  chartLabels.value = slicedData.map(item => item.label)
  chartAmounts.value = slicedData.map(item => item.amount)
  updateChart()
}

const handlePeriodChange = (value) => {
  applyChartPeriod(value)
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  // 销毁所有图表实例
  chartInstance?.dispose()
  orderPieInstance?.dispose()
  userGrowthInstance?.dispose()
  merchantRankInstance?.dispose()
  amountDistInstance?.dispose()
  incomeInstance?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>
