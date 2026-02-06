<template>
  <div class="space-y-6">
    <!-- 筛选栏 -->
    <div class="bg-white p-4 rounded-2xl border border-gray-100 flex items-center justify-between">
      <div class="flex items-center gap-4">
        <n-input v-model:value="searchKeyword" placeholder="搜索合同编号/标题" class="w-64">
          <template #prefix><i class="fas fa-search text-gray-400"></i></template>
        </n-input>
        <n-select 
          v-model:value="filterStatus" 
          :options="statusOptions"
          class="w-36"
          placeholder="全部状态"
          clearable
        />
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="bg-white p-5 rounded-xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">合同总数</p>
        <p class="text-2xl font-black text-gray-800 mt-1">{{ stats.total }}</p>
      </div>
      <div class="bg-white p-5 rounded-xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">待签署</p>
        <p class="text-2xl font-black text-orange-500 mt-1">{{ stats.pending }}</p>
      </div>
      <div class="bg-white p-5 rounded-xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">已生效</p>
        <p class="text-2xl font-black text-green-500 mt-1">{{ stats.active }}</p>
      </div>
      <div class="bg-white p-5 rounded-xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">已作废</p>
        <p class="text-2xl font-black text-red-500 mt-1">{{ stats.cancelled }}</p>
      </div>
    </div>

    <!-- 合同列表 -->
    <div class="bg-white rounded-2xl border border-gray-100 overflow-hidden">
      <table class="w-full text-left">
        <thead class="bg-gray-50 border-b border-gray-100">
          <tr class="text-xs text-gray-500 uppercase">
            <th class="px-6 py-4 font-bold">合同编号</th>
            <th class="px-6 py-4 font-bold">合同标题</th>
            <th class="px-6 py-4 font-bold">甲方</th>
            <th class="px-6 py-4 font-bold">乙方</th>
            <th class="px-6 py-4 font-bold text-right">金额</th>
            <th class="px-6 py-4 font-bold text-center">状态</th>
            <th class="px-6 py-4 font-bold">创建时间</th>
            <th class="px-6 py-4 font-bold text-center">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="contract in contracts" :key="contract.id" class="hover:bg-gray-50 transition">
            <td class="px-6 py-5">
              <span class="font-mono text-sm text-gray-600">{{ contract.contractNo }}</span>
            </td>
            <td class="px-6 py-5">
              <p class="font-bold text-gray-800">{{ contract.title }}</p>
            </td>
            <td class="px-6 py-5 text-sm text-gray-600">{{ contract.partyAName || '-' }}</td>
            <td class="px-6 py-5 text-sm text-gray-600">{{ contract.partyBName || '-' }}</td>
            <td class="px-6 py-5 text-right font-bold text-gray-800">¥{{ (contract.totalAmount || 0).toLocaleString() }}</td>
            <td class="px-6 py-5 text-center">
              <span class="text-xs font-bold px-3 py-1 rounded-full" :class="getStatusClass(contract.status)">
                {{ getStatusText(contract.status) }}
              </span>
            </td>
            <td class="px-6 py-5 text-sm text-gray-500">{{ formatDate(contract.createTime) }}</td>
            <td class="px-6 py-5 text-center">
              <n-button size="small" ghost @click="viewContract(contract)">查看</n-button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="p-4 border-t border-gray-100 flex justify-center">
        <n-pagination 
          v-model:page="currentPage" 
          :page-count="totalPages"
          @update:page="handlePageChange"
        />
      </div>
    </div>

    <!-- 合同详情弹窗 -->
    <n-modal v-model:show="showDetailModal" preset="card" title="合同详情" style="width: 1000px; max-width: 95vw; max-height: 90vh;">
      <div v-if="currentContract" class="space-y-6 overflow-y-auto" style="max-height: calc(90vh - 120px);">
        <!-- 基本信息 -->
        <div class="text-center pb-4 border-b">
          <h2 class="text-xl font-black text-gray-800">{{ currentContract.title }} - 项目合同</h2>
          <p class="text-sm text-gray-400 mt-1">合同编号: {{ currentContract.contractNo }}</p>
        </div>

        <!-- 双方信息 -->
        <div class="grid grid-cols-2 gap-4">
          <div class="p-4 bg-blue-50 rounded-xl border border-blue-100">
            <p class="text-xs text-blue-400 mb-1">甲方（委托方）</p>
            <p class="font-bold text-blue-700">{{ currentContract.partyAName || '-' }}</p>
            <p v-if="currentContract.partyASignTime" class="text-xs text-green-500 mt-2">
              <i class="fas fa-check-circle mr-1"></i>已签署: {{ formatDateTime(currentContract.partyASignTime) }}
            </p>
            <p v-else class="text-xs text-orange-500 mt-2">
              <i class="fas fa-clock mr-1"></i>待签署
            </p>
          </div>
          <div class="p-4 bg-green-50 rounded-xl border border-green-100">
            <p class="text-xs text-green-400 mb-1">乙方（服务方）</p>
            <p class="font-bold text-green-700">{{ currentContract.partyBName || '-' }}</p>
            <p v-if="currentContract.partyBSignTime" class="text-xs text-green-500 mt-2">
              <i class="fas fa-check-circle mr-1"></i>已签署: {{ formatDateTime(currentContract.partyBSignTime) }}
            </p>
            <p v-else class="text-xs text-orange-500 mt-2">
              <i class="fas fa-clock mr-1"></i>待签署
            </p>
          </div>
        </div>

        <!-- 金额与期限 -->
        <div class="grid grid-cols-3 gap-4">
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-400 mb-1">合同金额</p>
            <p class="font-black text-xl text-red-500">¥{{ (currentContract.totalAmount || 0).toLocaleString() }}</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-400 mb-1">交付期限</p>
            <p class="font-bold">{{ currentContract.deliveryDeadline || '-' }}</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-400 mb-1">合同状态</p>
            <span class="text-xs font-bold px-3 py-1 rounded-full" :class="getStatusClass(currentContract.status)">
              {{ getStatusText(currentContract.status) }}
            </span>
          </div>
        </div>

        <!-- 合同条款 -->
        <div class="space-y-4">
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-sm font-bold text-gray-700 mb-2"><i class="fas fa-file-alt mr-2 text-blue-500"></i>一、项目描述</p>
            <p class="text-gray-600 whitespace-pre-wrap">{{ currentContract.projectDesc || '暂无' }}</p>
          </div>

          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-sm font-bold text-gray-700 mb-2"><i class="fas fa-code mr-2 text-purple-500"></i>二、技术要求</p>
            <p class="text-gray-600 whitespace-pre-wrap">{{ currentContract.techRequirements || '暂无' }}</p>
          </div>

          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-sm font-bold text-gray-700 mb-2"><i class="fas fa-check-double mr-2 text-green-500"></i>三、验收标准</p>
            <p class="text-gray-600 whitespace-pre-wrap">{{ currentContract.deliveryStandard || '暂无' }}</p>
          </div>

          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-sm font-bold text-gray-700 mb-2"><i class="fas fa-money-bill-wave mr-2 text-yellow-500"></i>四、付款条款</p>
            <div v-if="currentContract.paymentTerms" class="space-y-2">
              <div v-for="(stage, idx) in parsePaymentTerms(currentContract.paymentTerms)" :key="idx" 
                   class="flex items-center justify-between p-3 bg-white rounded-lg border">
                <span class="text-sm">{{ stage.name || `阶段${idx + 1}` }}</span>
                <span class="font-bold text-red-500">¥{{ (stage.amount || 0).toLocaleString() }}</span>
              </div>
            </div>
            <p v-else class="text-gray-600">暂无</p>
          </div>

          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-sm font-bold text-gray-700 mb-2"><i class="fas fa-exclamation-triangle mr-2 text-red-500"></i>五、违约条款</p>
            <p class="text-gray-600 whitespace-pre-wrap">{{ currentContract.breachClause || '暂无' }}</p>
          </div>

          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-sm font-bold text-gray-700 mb-2"><i class="fas fa-lock mr-2 text-gray-500"></i>六、保密协议</p>
            <p class="text-gray-600 whitespace-pre-wrap">{{ currentContract.confidentialClause || '暂无' }}</p>
          </div>

          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-sm font-bold text-gray-700 mb-2"><i class="fas fa-copyright mr-2 text-indigo-500"></i>七、知识产权归属</p>
            <p class="text-gray-600 whitespace-pre-wrap">{{ currentContract.ipClause || '暂无' }}</p>
          </div>

          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-sm font-bold text-gray-700 mb-2"><i class="fas fa-balance-scale mr-2 text-orange-500"></i>八、争议解决</p>
            <p class="text-gray-600 whitespace-pre-wrap">{{ currentContract.disputeClause || '暂无' }}</p>
          </div>
        </div>

        <!-- 签署信息 -->
        <div v-if="currentContract.partyASignature || currentContract.partyBSignature" class="grid grid-cols-2 gap-4 pt-4 border-t">
          <div v-if="currentContract.partyASignature" class="p-4 bg-blue-50 rounded-xl">
            <p class="text-xs text-blue-400 mb-2">甲方签名</p>
            <img :src="currentContract.partyASignature" class="max-h-20 mx-auto" alt="甲方签名" />
            <p class="text-xs text-center text-gray-400 mt-2">{{ formatDateTime(currentContract.partyASignTime) }}</p>
          </div>
          <div v-if="currentContract.partyBSignature" class="p-4 bg-green-50 rounded-xl">
            <p class="text-xs text-green-400 mb-2">乙方签名</p>
            <img :src="currentContract.partyBSignature" class="max-h-20 mx-auto" alt="乙方签名" />
            <p class="text-xs text-center text-gray-400 mt-2">{{ formatDateTime(currentContract.partyBSignTime) }}</p>
          </div>
        </div>

        <!-- 存证信息 -->
        <div v-if="currentContract.evidenceHash" class="p-4 bg-purple-50 rounded-xl border border-purple-100">
          <p class="text-xs text-purple-400 mb-1"><i class="fas fa-shield-alt mr-1"></i>区块链存证</p>
          <p class="font-mono text-xs text-purple-600 break-all">{{ currentContract.evidenceHash }}</p>
        </div>

        <div class="flex gap-3 justify-end pt-4 border-t">
          <n-button @click="showDetailModal = false">关闭</n-button>
        </div>
      </div>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useMessage } from 'naive-ui'
import { getContractList, getContractStats } from '@/api/admin'

const message = useMessage()

const searchKeyword = ref('')
const filterStatus = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const showDetailModal = ref(false)
const currentContract = ref(null)

const statusOptions = [
  { label: '草稿', value: 0 },
  { label: '待甲方签署', value: 1 },
  { label: '待乙方签署', value: 2 },
  { label: '已生效', value: 3 },
  { label: '已作废', value: 4 }
]

const stats = reactive({ total: 0, pending: 0, active: 0, cancelled: 0 })
const contracts = ref([])

const loadStats = async () => {
  try {
    const res = await getContractStats()
    if (res.data) {
      Object.assign(stats, res.data)
    }
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

const loadContracts = async () => {
  try {
    const res = await getContractList({
      page: currentPage.value,
      size: pageSize.value,
      status: filterStatus.value,
      keyword: searchKeyword.value || undefined
    })
    if (res.data && res.data.records) {
      contracts.value = res.data.records
      totalPages.value = Math.max(1, Math.ceil(res.data.total / pageSize.value))
    }
  } catch (e) {
    console.error('加载合同列表失败', e)
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const parsePaymentTerms = (terms) => {
  if (!terms) return []
  try {
    if (typeof terms === 'string') {
      return JSON.parse(terms)
    }
    return terms
  } catch (e) {
    return []
  }
}

const getStatusText = (status) => {
  const map = { 0: '草稿', 1: '待甲方签署', 2: '待乙方签署', 3: '已生效', 4: '已作废' }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = {
    0: 'bg-gray-100 text-gray-500',
    1: 'bg-orange-100 text-orange-500',
    2: 'bg-blue-100 text-blue-500',
    3: 'bg-green-100 text-green-500',
    4: 'bg-red-100 text-red-500'
  }
  return map[status] || 'bg-gray-100 text-gray-500'
}

const viewContract = (contract) => {
  currentContract.value = contract
  showDetailModal.value = true
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadContracts()
}

watch([searchKeyword, filterStatus], () => {
  currentPage.value = 1
  loadContracts()
})

onMounted(() => {
  loadStats()
  loadContracts()
})
</script>
