<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-white">我的合同</h1>
        <p class="text-gray-400 mt-1">查看所有已签署的项目合同</p>
      </div>
    </div>

    <!-- 筛选 -->
    <div class="bg-slate-800 rounded-2xl p-4 border border-slate-700 flex items-center gap-4">
      <div class="flex-1 relative">
        <i class="fas fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-500"></i>
        <input 
          v-model="searchKeyword"
          type="text"
          placeholder="搜索合同编号/项目名称..."
          class="w-full bg-slate-700 border border-slate-600 rounded-xl py-2.5 pl-10 pr-4 text-white placeholder-gray-500 focus:outline-none focus:border-tf transition"
        />
      </div>
      <select 
        v-model="statusFilter" 
        class="bg-slate-700 border border-slate-600 rounded-xl py-2.5 px-4 text-white focus:outline-none focus:border-tf transition min-w-[140px]"
      >
        <option :value="null">全部状态</option>
        <option :value="0">待签署</option>
        <option :value="1">已签署</option>
        <option :value="2">已完成</option>
        <option :value="3">已终止</option>
      </select>
    </div>

    <!-- 合同列表 -->
    <div class="space-y-4">
      <n-spin :show="loading">
        <div v-if="filteredContracts.length === 0" class="bg-slate-800 rounded-3xl p-16 text-center border border-slate-700">
          <div class="w-20 h-20 bg-slate-700 rounded-full flex items-center justify-center mx-auto mb-4">
            <i class="fas fa-file-contract text-3xl text-gray-500"></i>
          </div>
          <p class="text-gray-400 font-bold">暂无合同</p>
          <p class="text-gray-500 text-sm mt-2">接单并签署合同后将在这里显示</p>
        </div>

        <div 
          v-for="contract in filteredContracts" 
          :key="contract.id"
          class="bg-slate-800 rounded-2xl p-6 border border-slate-700 hover:border-tf/50 transition cursor-pointer"
          @click="viewContract(contract)"
        >
          <div class="flex items-start justify-between">
            <div class="flex items-start gap-4">
              <div class="w-14 h-14 bg-slate-700 rounded-xl flex items-center justify-center">
                <i class="fas fa-file-contract text-2xl" style="color: #00AFE1;"></i>
              </div>
              <div>
                <div class="flex items-center gap-3 mb-1">
                  <h3 class="font-bold text-white text-lg">{{ contract.title }}</h3>
                  <n-tag :type="getStatusType(contract.status)" size="small">
                    {{ getStatusLabel(contract.status) }}
                  </n-tag>
                </div>
                <p class="text-gray-400 text-sm">合同编号: {{ contract.contractNo }}</p>
                <p class="text-gray-500 text-xs mt-1">甲方: {{ contract.buyerName }} | 签署时间: {{ formatDate(contract.signTime) }}</p>
              </div>
            </div>
            <div class="text-right">
              <p class="text-2xl font-black" style="color: #00AFE1;">¥ {{ contract.amount.toLocaleString() }}</p>
              <p class="text-xs text-gray-500 mt-1">合同金额</p>
            </div>
          </div>

          <!-- 合同关键信息 -->
          <div class="mt-4 pt-4 border-t border-slate-700 grid grid-cols-4 gap-4 text-sm">
            <div>
              <p class="text-gray-500">交付日期</p>
              <p class="text-gray-300">{{ formatDate(contract.deliveryDate) }}</p>
            </div>
            <div>
              <p class="text-gray-500">付款方式</p>
              <p class="text-gray-300">{{ contract.paymentMethod }}</p>
            </div>
            <div>
              <p class="text-gray-500">已结算</p>
              <p class="text-green-400">¥ {{ contract.settledAmount.toLocaleString() }}</p>
            </div>
            <div class="text-right">
              <n-button type="primary" color="#00AFE1" size="small" ghost @click.stop="viewContract(contract)">
                查看详情
              </n-button>
            </div>
          </div>
        </div>
      </n-spin>
    </div>

    <!-- 合同详情弹窗 -->
    <n-modal v-model:show="showDetail" preset="card" title="合同详情" class="max-w-3xl">
      <div v-if="currentContract" class="space-y-6">
        <!-- 合同头部信息 -->
        <div class="p-4 bg-slate-700 rounded-xl">
          <div class="flex items-start justify-between">
            <div>
              <h3 class="font-bold text-white text-xl">{{ currentContract.title }}</h3>
              <p class="text-gray-400 text-sm mt-1">合同编号: {{ currentContract.contractNo }}</p>
            </div>
            <n-tag :type="getStatusType(currentContract.status)" size="large">
              {{ getStatusLabel(currentContract.status) }}
            </n-tag>
          </div>
        </div>

        <!-- 甲乙双方 -->
        <div class="grid grid-cols-2 gap-4">
          <div class="p-4 bg-slate-700/50 rounded-xl">
            <p class="text-xs text-gray-500 mb-2">甲方（委托方）</p>
            <p class="text-white font-bold">{{ currentContract.buyerName }}</p>
          </div>
          <div class="p-4 bg-slate-700/50 rounded-xl">
            <p class="text-xs text-gray-500 mb-2">乙方（服务商）</p>
            <p class="text-white font-bold">{{ currentContract.sellerName }}</p>
          </div>
        </div>

        <!-- 合同金额与日期 -->
        <div class="grid grid-cols-3 gap-4">
          <div class="p-4 bg-slate-700/50 rounded-xl">
            <p class="text-xs text-gray-500 mb-2">合同金额</p>
            <p class="text-xl font-black" style="color: #00AFE1;">¥ {{ currentContract.amount.toLocaleString() }}</p>
          </div>
          <div class="p-4 bg-slate-700/50 rounded-xl">
            <p class="text-xs text-gray-500 mb-2">签署时间</p>
            <p class="text-white font-bold">{{ formatDate(currentContract.signTime) }}</p>
          </div>
          <div class="p-4 bg-slate-700/50 rounded-xl">
            <p class="text-xs text-gray-500 mb-2">交付日期</p>
            <p class="text-white font-bold">{{ formatDate(currentContract.deliveryDate) }}</p>
          </div>
        </div>

        <!-- 付款阶段 -->
        <div class="p-4 bg-slate-700/50 rounded-xl">
          <h4 class="text-sm font-bold text-gray-400 mb-3">付款阶段</h4>
          <div class="space-y-3">
            <div 
              v-for="(stage, index) in currentContract.stages" 
              :key="index"
              class="flex items-center justify-between p-3 bg-slate-600/50 rounded-lg"
            >
              <div class="flex items-center gap-3">
                <div 
                  class="w-8 h-8 rounded-full flex items-center justify-center text-white text-sm font-bold"
                  :style="{ background: stage.status === 'completed' ? '#10B981' : '#00AFE1' }"
                >
                  {{ index + 1 }}
                </div>
                <div>
                  <p class="text-white font-medium">{{ stage.name }}</p>
                  <p class="text-xs text-gray-400">{{ stage.milestone }}</p>
                </div>
              </div>
              <div class="text-right">
                <p class="text-white font-bold">¥ {{ stage.amount.toLocaleString() }}</p>
                <p class="text-xs" :class="stage.status === 'completed' ? 'text-green-400' : 'text-gray-400'">
                  {{ stage.status === 'completed' ? '已结算' : '待结算' }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- 合同条款 -->
        <div class="p-4 bg-slate-700/50 rounded-xl">
          <h4 class="text-sm font-bold text-gray-400 mb-3">合同条款</h4>
          <div class="text-gray-300 text-sm whitespace-pre-wrap max-h-60 overflow-y-auto">
            {{ currentContract.terms || '暂无条款内容' }}
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="flex justify-end gap-3">
          <n-button @click="downloadContract">
            <template #icon><i class="fas fa-download"></i></template>
            下载合同
          </n-button>
          <n-button type="primary" color="#00AFE1" @click="goToOrder">
            查看订单
          </n-button>
        </div>
      </div>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { getContractList, getContractDetail } from '@/api/contract'

const router = useRouter()
const message = useMessage()

const loading = ref(false)
const contracts = ref([])
const searchKeyword = ref('')
const statusFilter = ref(null)
const showDetail = ref(false)
const currentContract = ref(null)

const statusOptions = [
  { label: '全部状态', value: null },
  { label: '待签署', value: 0 },
  { label: '已签署', value: 1 },
  { label: '已完成', value: 2 },
  { label: '已终止', value: 3 }
]

// 筛选后的合同
const filteredContracts = computed(() => {
  let result = contracts.value
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(c => 
      c.title.toLowerCase().includes(keyword) ||
      c.contractNo.toLowerCase().includes(keyword)
    )
  }
  
  if (statusFilter.value !== null) {
    result = result.filter(c => c.status === statusFilter.value)
  }
  
  return result
})

// 加载合同列表
const loadContracts = async () => {
  loading.value = true
  try {
    const res = await getContractList()
    if (res.data) {
      contracts.value = res.data.map(c => ({
        id: c.id,
        orderId: c.orderId,
        title: c.title || '项目合同',
        contractNo: c.contractNo || `CT${c.id}`,
        buyerName: c.buyerName || c.partyAName || '未知',
        sellerName: c.sellerName || c.partyBName || '未知',
        amount: c.totalAmount || 0,
        settledAmount: c.settledAmount || 0,
        status: getContractStatus(c.status),
        signTime: c.partyASignTime || c.partyBSignTime || c.createTime,
        deliveryDate: c.deliveryDeadline,
        paymentMethod: getPaymentMethod(c.stages),
        stages: (c.stages || []).map(s => ({
          name: s.name,
          amount: s.amount || 0,
          milestone: s.milestone || '',
          status: s.status >= 3 ? 'completed' : 'pending'
        })),
        terms: formatTerms(c)
      }))
    }
  } catch (e) {
    console.error('加载合同失败', e)
  } finally {
    loading.value = false
  }
}

// 转换合同状态（后端0-4 -> 前端显示状态）
const getContractStatus = (status) => {
  // 后端: 0草稿 1待甲方签署 2待乙方签署 3已生效 4已作废
  // 前端: 0待签署 1已签署 2已完成 3已终止
  if (status === 0 || status === 1 || status === 2) return 0 // 待签署
  if (status === 3) return 1 // 已签署/已生效
  if (status === 4) return 3 // 已终止
  return status
}

// 格式化合同条款
const formatTerms = (c) => {
  let terms = ''
  if (c.projectDesc) terms += `【项目描述】\n${c.projectDesc}\n\n`
  if (c.techRequirements) terms += `【技术要求】\n${c.techRequirements}\n\n`
  if (c.deliveryStandard) terms += `【验收标准】\n${c.deliveryStandard}\n\n`
  if (c.breachClause) terms += `【违约条款】\n${c.breachClause}\n\n`
  if (c.confidentialClause) terms += `【保密协议】\n${c.confidentialClause}\n\n`
  if (c.ipClause) terms += `【知识产权】\n${c.ipClause}\n\n`
  if (c.disputeClause) terms += `【争议解决】\n${c.disputeClause}`
  return terms || '暂无条款内容'
}

const getPaymentMethod = (stages) => {
  if (!stages || stages.length === 0) return '未设置'
  if (stages.length === 2) return '两阶段付款'
  if (stages.length === 3) return '三阶段付款'
  return `${stages.length}阶段付款`
}

const getStatusLabel = (status) => {
  const labels = {
    0: '待签署',
    1: '已签署',
    2: '已完成',
    3: '已终止'
  }
  return labels[status] || '未知'
}

const getStatusType = (status) => {
  const types = {
    0: 'warning',
    1: 'info',
    2: 'success',
    3: 'error'
  }
  return types[status] || 'default'
}

const formatDate = (dateVal) => {
  if (!dateVal) return '-'
  const date = new Date(dateVal)
  return date.toLocaleDateString('zh-CN')
}

// 查看合同详情
const viewContract = async (contract) => {
  try {
    const res = await getContractDetail(contract.orderId)
    if (res.data) {
      currentContract.value = {
        ...contract,
        ...res.data,
        stages: res.data.stages || contract.stages
      }
      showDetail.value = true
    }
  } catch (e) {
    // 如果获取详情失败，使用列表数据
    currentContract.value = contract
    showDetail.value = true
  }
}

// 下载合同
const downloadContract = () => {
  message.info('合同下载功能开发中...')
}

// 跳转到订单
const goToOrder = () => {
  if (currentContract.value?.orderId) {
    router.push(`/merchant/orders/${currentContract.value.orderId}`)
    showDetail.value = false
  }
}

onMounted(() => {
  loadContracts()
})
</script>
