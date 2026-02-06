<template>
  <div class="space-y-6">
    <!-- 顶部统计 -->
    <div class="grid grid-cols-3 gap-6">
      <div class="bg-slate-800 p-8 rounded-3xl border border-slate-700">
        <p class="text-xs text-gray-400 font-bold uppercase mb-2">累计总收益</p>
        <h3 class="text-3xl font-black text-white">¥ {{ stats.totalIncome.toLocaleString() }}</h3>
      </div>
      <div class="bg-slate-800 p-8 rounded-3xl border border-slate-700">
        <p class="text-xs text-gray-400 font-bold uppercase mb-2">信托托管中</p>
        <h3 class="text-3xl font-black text-tf">¥ {{ stats.frozen.toLocaleString() }}</h3>
      </div>
      <div class="bg-slate-800 p-8 rounded-3xl border border-slate-700">
        <p class="text-xs text-gray-400 font-bold uppercase mb-2">可结算提现</p>
        <h3 class="text-3xl font-black text-green-400">¥ {{ stats.withdrawable.toLocaleString() }}</h3>
        <n-button type="primary" color="#00AFE1" size="small" class="mt-4" @click="showWithdraw = true">
          申请提现
        </n-button>
      </div>
    </div>

    <!-- 结算流水表 -->
    <div class="bg-slate-800 rounded-3xl border border-slate-700 overflow-hidden">
      <div class="p-6 border-b border-slate-700 flex items-center justify-between">
        <h3 class="font-black text-lg text-white">资金与结算流水</h3>
        <div class="flex items-center gap-3">
          <n-button ghost>
            <template #icon><i class="fas fa-download"></i></template>
            导出记录
          </n-button>
        </div>
      </div>

      <table class="w-full text-left">
        <thead class="bg-slate-700/50">
          <tr class="text-xs font-bold text-gray-400 uppercase tracking-wider border-b border-slate-700">
            <th class="px-6 py-4">时间</th>
            <th class="px-6 py-4">交易摘要</th>
            <th class="px-6 py-4">存证哈希</th>
            <th class="px-6 py-4 text-right">变动金额</th>
            <th class="px-6 py-4 text-center">状态</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-700">
          <tr v-if="loading">
            <td colspan="5" class="px-6 py-16 text-center">
              <n-spin size="large" />
              <p class="text-gray-400 mt-4">加载中...</p>
            </td>
          </tr>
          <tr 
            v-else
            v-for="trans in filteredTransactions" 
            :key="trans.id"
            class="hover:bg-slate-700/30 transition"
          >
            <td class="px-6 py-5 text-xs text-gray-400">{{ trans.createTime }}</td>
            <td class="px-6 py-5 font-bold text-white">{{ trans.title || trans.remark }}</td>
            <td class="px-6 py-5 text-xs text-gray-500 font-mono">
              <span v-if="trans.evidenceHash">HASH: {{ trans.evidenceHash.substring(0, 8) }}...</span>
              <span v-else>-</span>
            </td>
            <td class="px-6 py-5 text-right font-black" :class="trans.type === 'release' ? 'text-green-400' : 'text-white'">
              {{ trans.type === 'release' ? '+' : '-' }}¥ {{ trans.amount.toLocaleString() }}
            </td>
            <td class="px-6 py-5 text-center">
              <span 
                class="text-xs font-bold px-3 py-1 rounded-full"
                :class="trans.status === 1 ? 'bg-green-500/20 text-green-400' : 'bg-orange-500/20 text-orange-400'"
              >
                {{ trans.status === 1 ? '已入账' : '处理中' }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="!loading && filteredTransactions.length === 0" class="p-16 text-center">
        <div class="w-16 h-16 bg-slate-700 rounded-full flex items-center justify-center mx-auto mb-4">
          <i class="fas fa-receipt text-2xl text-gray-500"></i>
        </div>
        <p class="text-gray-400">暂无交易记录</p>
      </div>

      <!-- 分页 -->
      <div v-if="totalRecords > pageSize" class="p-4 border-t border-slate-700 flex justify-center">
        <n-pagination 
          v-model:page="currentPage" 
          :page-count="Math.ceil(totalRecords / pageSize)"
          show-quick-jumper
        />
      </div>
    </div>

    <!-- 提现弹窗 -->
    <n-modal v-model:show="showWithdraw" preset="card" title="申请提现" class="max-w-md">
      <div class="space-y-6">
        <div class="p-4 bg-slate-700 rounded-xl">
          <p class="text-xs text-gray-400">可提现余额</p>
          <p class="text-2xl font-black text-tf">¥ {{ stats.withdrawable.toLocaleString() }}</p>
        </div>

        <n-form-item label="提现金额">
          <n-input-number 
            v-model:value="withdrawAmount" 
            :min="100"
            :max="stats.withdrawable"
            :precision="2"
            size="large"
            class="w-full"
          >
            <template #prefix>¥</template>
          </n-input-number>
        </n-form-item>

        <div class="grid grid-cols-4 gap-3">
          <button 
            v-for="amount in quickAmounts" 
            :key="amount"
            @click="withdrawAmount = Math.min(amount, stats.withdrawable)"
            class="py-2 bg-slate-700 rounded-lg text-sm font-bold text-gray-300 hover:bg-slate-600 transition"
            :class="withdrawAmount === amount ? 'ring-2 ring-tf' : ''"
          >
            ¥{{ amount >= 10000 ? (amount / 10000) + '万' : amount }}
          </button>
        </div>

        <n-form-item label="提现账户">
          <n-select 
            v-model:value="withdrawAccount"
            :options="bankAccounts"
            placeholder="请选择提现账户"
          />
        </n-form-item>

        <div class="p-3 bg-slate-700 rounded-lg text-xs text-gray-400">
          <i class="fas fa-info-circle text-tf mr-1"></i>
          提现将在1-3个工作日内到账，手续费0.1%（最低1元）
        </div>

        <div class="pt-2">
          <n-button 
            block 
            type="primary" 
            color="#00AFE1" 
            size="large" 
            class="font-bold h-12 text-base"
            :loading="withdrawing" 
            @click="handleWithdraw"
          >
            <template #icon><i class="fas fa-wallet mr-1"></i></template>
            确认提现
          </n-button>
        </div>
      </div>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, inject } from 'vue'
import { useMessage } from 'naive-ui'
import { getWalletInfo, getTransactions, getBankCards, withdraw } from '@/api/wallet'

const message = useMessage()

// 注入刷新顶栏余额的方法
const refreshBalance = inject('refreshBalance', () => {})

const showWithdraw = ref(false)
const withdrawAmount = ref(null)
const withdrawAccount = ref(null)
const currentPage = ref(1)
const pageSize = ref(20)
const totalRecords = ref(0)
const filterType = ref(null)
const loading = ref(false)
const withdrawing = ref(false)

const stats = reactive({
  totalIncome: 0,
  frozen: 0,
  withdrawable: 0
})

const typeOptions = [
  { label: '阶段收款', value: 'release' },
  { label: '提现', value: 'withdraw' }
]

const quickAmounts = [1000, 5000, 10000, 50000]

// 银行卡列表
const bankAccounts = ref([])

// 交易记录
const transactions = ref([])

// 加载钱包信息
const loadWalletInfo = async () => {
  try {
    const res = await getWalletInfo()
    if (res.data) {
      stats.totalIncome = res.data.totalIncome || 0
      stats.frozen = res.data.frozenAmount || 0
      stats.withdrawable = res.data.balance || 0
      // 同步刷新顶栏余额显示
      refreshBalance()
    }
  } catch (e) {
    console.error('加载钱包信息失败', e)
  }
}

// 加载交易记录
const loadTransactions = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    if (filterType.value !== null) {
      params.type = filterType.value
    }
    
    const res = await getTransactions(params)
    if (res.data && res.data.records) {
      transactions.value = res.data.records.map(trans => ({
        id: trans.id,
        type: trans.type,
        amount: trans.amount || 0,
        title: trans.title || '',
        remark: trans.remark || '',
        evidenceHash: trans.evidenceHash || null,
        createTime: formatDateTime(trans.createTime),
        status: trans.status
      }))
      totalRecords.value = res.data.total || 0
    }
  } catch (e) {
    console.error('加载交易记录失败', e)
    message.error('加载交易记录失败')
  } finally {
    loading.value = false
  }
}

// 加载银行卡列表
const loadBankCards = async () => {
  try {
    const res = await getBankCards()
    if (res.data && res.data.length > 0) {
      bankAccounts.value = res.data.map(card => ({
        label: `${card.bankName} **** ${card.cardNo.slice(-4)}`,
        value: card.id
      }))
    }
  } catch (e) {
    console.error('加载银行卡列表失败', e)
  }
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 提现
const handleWithdraw = async () => {
  if (!withdrawAmount.value || withdrawAmount.value < 100) {
    message.warning('提现金额不能少于100元')
    return
  }
  if (!withdrawAccount.value) {
    message.warning('请选择提现账户')
    return
  }
  if (withdrawAmount.value > stats.withdrawable) {
    message.warning('提现金额不能超过可提现余额')
    return
  }

  withdrawing.value = true
  try {
    await withdraw({
      amount: withdrawAmount.value,
      bankCardId: withdrawAccount.value
    })
    message.success('提现申请已提交，预计1-3个工作日到账')
    showWithdraw.value = false
    withdrawAmount.value = null
    withdrawAccount.value = null
    // 刷新数据
    loadWalletInfo()
    loadTransactions()
  } catch (e) {
    console.error('提现失败', e)
    message.error(e.response?.data?.message || '提现失败，请稍后重试')
  } finally {
    withdrawing.value = false
  }
}

const filteredTransactions = computed(() => {
  // 由于已在API请求中过滤，这里直接返回
  return transactions.value
})

// 监听筛选条件变化
watch(filterType, () => {
  currentPage.value = 1
  loadTransactions()
})

// 监听分页变化
watch(currentPage, () => {
  loadTransactions()
})

onMounted(() => {
  loadWalletInfo()
  loadTransactions()
  loadBankCards()
})
</script>
