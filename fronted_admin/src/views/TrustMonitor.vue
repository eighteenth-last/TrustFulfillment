<template>
  <div class="space-y-6">
    <!-- 顶部统计 -->
    <div class="grid grid-cols-3 gap-6">
      <div class="bg-white p-8 rounded-3xl border border-gray-100">
        <p class="text-gray-400 text-xs font-bold uppercase tracking-wider">当前资金总池</p>
        <h2 class="text-3xl font-black mt-2 text-tf">¥ {{ stats.totalPool.toLocaleString() }}</h2>
        <p class="text-xs mt-4 text-green-500"><i class="fas fa-shield-alt mr-1"></i> 受银联及存托银行实时监管</p>
      </div>
      <div class="bg-white p-8 rounded-3xl border border-gray-100">
        <p class="text-gray-400 text-xs font-bold uppercase tracking-wider">今日流入资金</p>
        <h2 class="text-3xl font-black mt-2 text-gray-800">¥ {{ stats.todayInflow.toLocaleString() }}</h2>
        <p class="text-xs mt-4 text-green-500"><i class="fas fa-caret-up mr-1"></i> +15.4% 环比</p>
      </div>
      <div class="bg-white p-8 rounded-3xl border border-gray-100">
        <p class="text-gray-400 text-xs font-bold uppercase tracking-wider">待清算金额</p>
        <h2 class="text-3xl font-black mt-2 text-gray-800">¥ {{ stats.pendingSettle.toLocaleString() }}</h2>
        <p class="text-xs mt-4 text-orange-500"><i class="fas fa-clock mr-1"></i> 处理中 {{ stats.pendingCount }} 笔</p>
      </div>
    </div>

    <!-- 实时资金流水 -->
    <div class="bg-white rounded-3xl p-6 border border-gray-100">
      <div class="flex items-center justify-between mb-6">
        <h4 class="font-black flex items-center gap-2">
          <i class="fas fa-history text-tf"></i> 实时资金流水
        </h4>
        <div class="flex items-center gap-3">
          <n-button ghost @click="handleExport">
            <template #icon><i class="fas fa-download"></i></template>
            导出
          </n-button>
        </div>
      </div>

      <div class="space-y-4">
        <div 
          v-for="trans in transactions" 
          :key="trans.id"
          class="flex items-center justify-between p-4 bg-gray-50 rounded-2xl hover:bg-gray-100 transition"
        >
          <div class="flex items-center gap-4">
            <div 
              class="w-10 h-10 rounded-full flex items-center justify-center"
              :class="trans.type === 'in' ? 'bg-green-100 text-green-600' : 'bg-blue-100 text-blue-600'"
            >
              <i :class="trans.type === 'in' ? 'fas fa-plus' : 'fas fa-file-invoice-dollar'"></i>
            </div>
            <div>
              <p class="text-sm font-bold">{{ trans.title }}</p>
              <p class="text-xs text-gray-400">{{ trans.detail }}</p>
            </div>
          </div>
          <div class="flex items-center gap-6">
            <span class="text-xs text-gray-400">{{ trans.time }}</span>
            <div class="text-right text-sm font-black" :class="trans.type === 'in' ? 'text-green-500' : 'text-gray-800'">
              {{ trans.type === 'in' ? '+' : '-' }} ¥ {{ trans.amount.toLocaleString() }}
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="mt-6 flex justify-center">
        <n-pagination 
          v-model:page="currentPage" 
          :page-count="totalPages"
          @update:page="handlePageChange"
        />
      </div>
    </div>

    <!-- 资金分布 -->
    <div class="grid grid-cols-2 gap-6">
      <div class="bg-white rounded-3xl p-6 border border-gray-100">
        <h4 class="font-black mb-6">资金状态分布</h4>
        <div class="space-y-4">
          <div>
            <div class="flex justify-between text-sm mb-2">
              <span class="text-gray-500">托管中</span>
              <span class="font-bold">¥ {{ distribution.frozen.toLocaleString() }}</span>
            </div>
            <div class="w-full bg-gray-100 h-2 rounded-full">
              <div class="bg-tf h-full rounded-full" style="width: 60%"></div>
            </div>
          </div>
          <div>
            <div class="flex justify-between text-sm mb-2">
              <span class="text-gray-500">待清算</span>
              <span class="font-bold">¥ {{ distribution.pending.toLocaleString() }}</span>
            </div>
            <div class="w-full bg-gray-100 h-2 rounded-full">
              <div class="bg-orange-400 h-full rounded-full" style="width: 25%"></div>
            </div>
          </div>
          <div>
            <div class="flex justify-between text-sm mb-2">
              <span class="text-gray-500">可提现</span>
              <span class="font-bold">¥ {{ distribution.available.toLocaleString() }}</span>
            </div>
            <div class="w-full bg-gray-100 h-2 rounded-full">
              <div class="bg-green-400 h-full rounded-full" style="width: 15%"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-3xl p-6 border border-gray-100">
        <h4 class="font-black mb-6">风控指标</h4>
        <div class="space-y-4">
          <div class="flex items-center justify-between p-4 bg-green-50 rounded-xl">
            <span class="text-sm font-medium text-gray-600">资金安全率</span>
            <span class="text-lg font-black text-green-500">100%</span>
          </div>
          <div class="flex items-center justify-between p-4 bg-green-50 rounded-xl">
            <span class="text-sm font-medium text-gray-600">存证一致性</span>
            <span class="text-lg font-black text-green-500">100%</span>
          </div>
          <div class="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
            <span class="text-sm font-medium text-gray-600">异常交易拦截</span>
            <span class="text-lg font-black text-gray-800">12,402 次</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { getTrustMonitor, getTrustTransactions } from '@/api/admin'
import * as XLSX from 'xlsx'
import { saveAs } from 'file-saver'

const message = useMessage()

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const filterType = ref(null)

const typeOptions = [
  { label: '资金流入', value: 'in' },
  { label: '资金流出', value: 'out' }
]

const stats = reactive({
  totalPool: 0,
  todayInflow: 0,
  pendingSettle: 0,
  pendingCount: 0
})

const distribution = reactive({
  frozen: 0,
  pending: 0,
  available: 0
})

const transactions = ref([])

// 加载信托监控数据（统计信息）
const loadMonitorData = async () => {
  loading.value = true
  try {
    const res = await getTrustMonitor()
    if (res.data) {
      stats.totalPool = res.data.totalPool || 0
      stats.todayInflow = res.data.todayInflow || 0
      stats.pendingSettle = res.data.pendingSettle || 0
      stats.pendingCount = res.data.pendingCount || 0

      if (res.data.distribution) {
        distribution.frozen = res.data.distribution.frozen || 0
        distribution.pending = res.data.distribution.pending || 0
        distribution.available = res.data.distribution.available || 0
      }
    }
  } catch (e) {
    console.error('加载监控数据失败', e)
  } finally {
    loading.value = false
  }
  
  // 加载完统计数据后，调用分页 API 获取交易记录
  await loadTransactions()
}

// 加载更多交易记录
const loadTransactions = async () => {
  try {
    const res = await getTrustTransactions({
      page: currentPage.value,
      size: pageSize.value,
      type: filterType.value || undefined
    })
    if (res.data && res.data.records) {
      transactions.value = res.data.records.map(t => ({
        id: t.id,
        type: t.type === 'deposit' || t.type === 'recharge' ? 'in' : 'out',
        title: t.title || '交易记录',
        detail: t.remark || '',
        amount: t.amount || 0,
        time: formatTime(t.createTime)
      }))
      totalPages.value = Math.max(1, Math.ceil(res.data.total / pageSize.value))
    }
  } catch (e) {
    console.error('加载交易记录失败', e)
  }
}

const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
}

// 分页切换
const handlePageChange = (page) => {
  currentPage.value = page
  loadTransactions()
}

// 导出资金流水为Excel
const handleExport = async () => {
  message.info('正在导出资金流水...')
  
  try {
    // 获取所有交易数据（不分页）
    const res = await getTrustTransactions({ page: 1, size: 1000 })
    if (!res.data || !res.data.records || res.data.records.length === 0) {
      message.warning('暂无数据可导出')
      return
    }
    
    const records = res.data.records
    
    // 企业流水格式的表头
    const headers = [
      '序号',
      '交易流水号',
      '交易时间',
      '交易类型',
      '交易摘要',
      '收入金额(元)',
      '支出金额(元)',
      '账户余额(元)',
      '交易备注'
    ]
    
    // 计算余额（从最早开始累计）
    let balance = 0
    const sortedRecords = [...records].reverse() // 按时间升序排列计算余额
    const balanceMap = new Map()
    sortedRecords.forEach(t => {
      const isIn = t.type === 'deposit' || t.type === 'recharge'
      if (isIn) {
        balance += Number(t.amount) || 0
      } else {
        balance -= Number(t.amount) || 0
      }
      balanceMap.set(t.id, balance)
    })
    
    // 转换数据为企业流水格式
    const data = records.map((t, index) => {
      const isIn = t.type === 'deposit' || t.type === 'recharge'
      const amount = Number(t.amount) || 0
      const txTime = t.createTime ? new Date(t.createTime) : new Date()
      
      return [
        index + 1,
        `TF${String(t.id).padStart(10, '0')}`,
        txTime.toLocaleString('zh-CN', { 
          year: 'numeric', 
          month: '2-digit', 
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit',
          second: '2-digit'
        }),
        isIn ? '资金流入' : '资金流出',
        t.title || '-',
        isIn ? amount.toFixed(2) : '',
        isIn ? '' : amount.toFixed(2),
        (balanceMap.get(t.id) || 0).toFixed(2),
        t.remark || '-'
      ]
    })
    
    // 创建工作表
    const ws = XLSX.utils.aoa_to_sheet([
      // 标题行
      ['臻托TF平台 - 资金流水明细表'],
      [`导出时间: ${new Date().toLocaleString('zh-CN')}`],
      [`数据范围: 全部交易记录 (共${records.length}条)`],
      [], // 空行
      headers,
      ...data
    ])
    
    // 设置列宽
    ws['!cols'] = [
      { wch: 6 },   // 序号
      { wch: 18 },  // 交易流水号
      { wch: 20 },  // 交易时间
      { wch: 10 },  // 交易类型
      { wch: 30 },  // 交易摘要
      { wch: 14 },  // 收入金额
      { wch: 14 },  // 支出金额
      { wch: 14 },  // 账户余额
      { wch: 40 }   // 交易备注
    ]
    
    // 合并标题单元格
    ws['!merges'] = [
      { s: { r: 0, c: 0 }, e: { r: 0, c: 8 } },
      { s: { r: 1, c: 0 }, e: { r: 1, c: 8 } },
      { s: { r: 2, c: 0 }, e: { r: 2, c: 8 } }
    ]
    
    // 创建工作簿
    const wb = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(wb, ws, '资金流水')
    
    // 生成文件名
    const now = new Date()
    const fileName = `臻托TF_资金流水_${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}.xlsx`
    
    // 导出文件
    const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
    const blob = new Blob([wbout], { type: 'application/octet-stream' })
    saveAs(blob, fileName)
    
    message.success('资金流水导出成功')
  } catch (e) {
    console.error('导出失败', e)
    message.error('导出失败，请稍后重试')
  }
}

onMounted(() => {
  loadMonitorData()
})
</script>
