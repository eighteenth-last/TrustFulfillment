<template>
  <div class="space-y-6">
    <!-- 钱包卡片 -->
    <div 
      class="p-10 rounded-[2.5rem] text-white shadow-2xl relative overflow-hidden"
      style="background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%); box-shadow: 0 25px 50px -12px rgba(0,175,225,0.25);"
    >
      <div class="relative z-10">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-white text-sm font-bold uppercase tracking-wider mb-2">账户余额 (元)</p>
            <h2 class="text-5xl font-black">¥{{ formatMoney(walletInfo.balance) }}</h2>
          </div>
          <div class="text-right">
            <div class="w-20 h-20 bg-white/20 rounded-2xl flex items-center justify-center backdrop-blur-sm">
              <i class="fas fa-wallet text-4xl"></i>
            </div>
          </div>
        </div>

        <div class="mt-8 flex gap-4">
          <n-button size="large" class="font-bold px-10" @click="showRecharge = true">
            充值
          </n-button>
          <n-button size="large" ghost class="font-bold px-10 text-white border-white/50 hover:bg-white/20" @click="showWithdraw = true">
            提现
          </n-button>
        </div>

        <div class="mt-8 pt-6 border-t border-white/20 grid grid-cols-3 gap-6">
          <div>
            <p class="text-white text-xs font-bold uppercase">托管中资金</p>
            <p class="text-xl font-black mt-1">¥{{ formatMoney(walletInfo.frozenAmount) }}</p>
          </div>
          <div>
            <p class="text-white text-xs font-bold uppercase">累计收入</p>
            <p class="text-xl font-black mt-1">¥{{ formatMoney(walletInfo.totalIncome) }}</p>
          </div>
          <div>
            <p class="text-white text-xs font-bold uppercase">累计支出</p>
            <p class="text-xl font-black mt-1">¥{{ formatMoney(walletInfo.totalExpense) }}</p>
          </div>
        </div>
      </div>

      <!-- 装饰背景 -->
      <div class="absolute -right-20 -top-20 w-64 h-64 bg-white/10 rounded-full blur-3xl"></div>
      <div class="absolute -left-10 -bottom-10 w-40 h-40 bg-white/5 rounded-full blur-2xl"></div>
    </div>

    <!-- 交易记录 -->
    <div class="bg-white rounded-3xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="p-6 border-b border-gray-100 flex items-center justify-between">
        <h3 class="font-black text-lg text-gray-800">
          <i class="fas fa-history mr-2" style="color: #00AFE1;"></i>
          交易记录
        </h3>
        <div class="flex items-center gap-3">
          <n-select 
            v-model:value="transType" 
            :options="typeOptions"
            class="w-32"
            placeholder="全部类型"
            clearable
            @update:value="loadTransactions"
          />
          <n-date-picker 
            v-model:value="dateRange" 
            type="daterange"
            clearable
            class="w-64"
            @update:value="loadTransactions"
          />
        </div>
      </div>

      <n-spin :show="loading">
        <div class="divide-y divide-gray-50">
          <div 
            v-for="trans in transactions" 
            :key="trans.id"
            class="p-6 hover:bg-slate-50 transition"
          >
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-4">
                <div 
                  class="w-12 h-12 rounded-xl flex items-center justify-center text-xl"
                  :class="getTransIconClass(trans.type)"
                >
                  <i :class="getTransIcon(trans.type)"></i>
                </div>
                <div>
                  <h4 class="font-bold text-gray-800">{{ trans.title || getTransTypeName(trans.type) }}</h4>
                  <div class="flex items-center gap-4 mt-1 text-xs text-gray-400">
                    <span>{{ trans.createTime }}</span>
                    <span v-if="trans.evidenceHash" class="font-mono">
                      <i class="fas fa-link mr-1"></i>
                      HASH: {{ trans.evidenceHash.substring(0, 8) }}...
                    </span>
                  </div>
                </div>
              </div>
              <div class="text-right">
                <p 
                  class="text-lg font-black"
                  :class="trans.type === 'release' || trans.type === 'recharge' ? 'text-green-500' : 'text-gray-800'"
                >
                  {{ trans.type === 'release' || trans.type === 'recharge' ? '+' : '-' }}¥{{ formatMoney(Math.abs(trans.amount)) }}
                </p>
                <p class="text-xs text-gray-400 mt-1">{{ getTransTypeName(trans.type) }}</p>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="!loading && transactions.length === 0" class="p-16 text-center">
            <div class="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <i class="fas fa-receipt text-2xl text-gray-300"></i>
            </div>
            <p class="text-gray-400">暂无交易记录</p>
          </div>
        </div>
      </n-spin>

      <!-- 分页 -->
      <div v-if="totalPages > 1" class="p-4 border-t border-gray-100 flex justify-center">
        <n-pagination 
          v-model:page="currentPage" 
          :page-count="totalPages"
          show-quick-jumper
          @update:page="loadTransactions"
        />
      </div>
    </div>

    <!-- 充值弹窗 -->
    <n-modal v-model:show="showRecharge" preset="card" title="账户充值" class="max-w-md">
      <div class="space-y-6">
        <n-form-item label="充值金额">
          <n-input-number 
            v-model:value="rechargeAmount" 
            :min="1"
            :precision="2"
            size="large"
            class="w-full"
          >
            <template #prefix>¥</template>
          </n-input-number>
        </n-form-item>

        <div class="grid grid-cols-4 gap-3">
          <button 
            v-for="amount in [100, 500, 1000, 5000]" 
            :key="amount"
            @click="rechargeAmount = amount"
            class="py-3 border rounded-xl text-sm font-bold transition amount-btn"
            :class="rechargeAmount === amount ? 'amount-btn-active' : 'border-gray-200 text-gray-600'"
          >
            ¥{{ amount }}
          </button>
        </div>

        <n-form-item label="支付方式">
          <n-radio-group v-model:value="paymentMethod" class="w-full">
            <div class="grid grid-cols-2 gap-3">
              <n-radio value="wechat" class="p-4 border rounded-xl hover:border-tf transition">
                <div class="flex items-center gap-2">
                  <i class="fab fa-weixin text-green-500 text-xl"></i>
                  <span class="font-bold">微信支付</span>
                </div>
              </n-radio>
              <n-radio value="alipay" class="p-4 border rounded-xl hover:border-tf transition">
                <div class="flex items-center gap-2">
                  <i class="fab fa-alipay text-blue-500 text-xl"></i>
                  <span class="font-bold">支付宝</span>
                </div>
              </n-radio>
            </div>
          </n-radio-group>
        </n-form-item>

        <div v-if="isMockPayment" class="p-3 bg-blue-50 rounded-lg text-xs text-blue-700">
          <i class="fas fa-info-circle mr-1"></i>
          当前使用虚拟支付（测试模式），支付将在3秒后自动完成
        </div>

        <n-button block type="primary" color="#00AFE1" size="large" class="font-bold" :loading="rechargeLoading" @click="handleRecharge">
          确认充值 ¥{{ rechargeAmount || 0 }}
        </n-button>
      </div>
    </n-modal>

    <!-- 支付二维码弹窗 -->
    <n-modal v-model:show="showPayQrcode" preset="card" title="扫码支付" class="max-w-md">
      <div class="text-center space-y-4">
        <div class="p-4 bg-gray-50 rounded-xl inline-block">
          <img v-if="qrcodeUrl" :src="qrcodeUrl" alt="支付二维码" class="w-64 h-64" />
          <n-spin v-else size="large" class="w-64 h-64 flex items-center justify-center" />
        </div>
        <p class="text-2xl font-black" style="color: #00AFE1;">¥{{ rechargeAmount }}</p>
        <p class="text-sm text-gray-500">
          <i class="fas fa-mobile-alt mr-1"></i>
          请使用{{ paymentMethod === 'alipay' ? '支付宝' : '微信' }}扫码支付
        </p>
        <div class="flex items-center justify-center gap-2 text-xs text-gray-400">
          <n-spin size="small" />
          <span>等待支付中...</span>
        </div>
      </div>
    </n-modal>

    <!-- 提现弹窗 -->
    <n-modal v-model:show="showWithdraw" preset="card" title="账户提现" class="max-w-md">
      <div class="space-y-6">
        <div class="p-4 rounded-xl" style="background: rgba(0,175,225,0.05);">
          <p class="text-xs text-gray-500">可提现余额</p>
          <p class="text-2xl font-black" style="color: #00AFE1;">¥{{ formatMoney(walletInfo.balance) }}</p>
        </div>

        <n-form-item label="提现金额">
          <n-input-number 
            v-model:value="withdrawAmount" 
            :min="100"
            :max="walletInfo.balance"
            :precision="2"
            size="large"
            class="w-full"
          >
            <template #prefix>¥</template>
          </n-input-number>
        </n-form-item>

        <n-form-item label="提现账户">
          <n-select 
            v-model:value="withdrawAccount"
            :options="bankAccounts"
            placeholder="请选择提现账户"
          />
        </n-form-item>

        <div class="p-3 bg-yellow-50 rounded-lg text-xs text-yellow-700">
          <i class="fas fa-info-circle mr-1"></i>
          提现将在1-3个工作日内到账，手续费0.1%（最低1元）
        </div>

        <n-button block type="primary" color="#00AFE1" size="large" class="font-bold" :loading="withdrawLoading" @click="handleWithdraw">
          确认提现
        </n-button>
      </div>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useMessage } from 'naive-ui'
import { useUserStore } from '@/stores/user'
import { getWalletInfo, getTransactions, recharge, withdraw, getBankCards, checkPaymentStatus } from '@/api/wallet'

const message = useMessage()
const userStore = useUserStore()

const loading = ref(false)
const rechargeLoading = ref(false)
const withdrawLoading = ref(false)

// 钱包信息
const walletInfo = reactive({
  balance: 0,
  frozenAmount: 0,
  totalIncome: 0,
  totalExpense: 0
})

// 交易记录
const transactions = ref([])
const currentPage = ref(1)
const totalPages = ref(1)
const transType = ref(null)
const dateRange = ref(null)

// 弹窗
const showRecharge = ref(false)
const showWithdraw = ref(false)
const showPayQrcode = ref(false)
const rechargeAmount = ref(100)
const withdrawAmount = ref(null)
const paymentMethod = ref('alipay')
const withdrawAccount = ref(null)
const qrcodeUrl = ref('')
const paymentOrderNo = ref('')
const isMockPayment = ref(false)
let paymentCheckTimer = null

// 银行卡列表
const bankAccounts = ref([])

const typeOptions = [
  { label: '充值', value: 'recharge' },
  { label: '提现', value: 'withdraw' },
  { label: '托管支付', value: 'deposit' },
  { label: '阶段收款', value: 'release' },
  { label: '退款', value: 'refund' }
]

// 格式化金额
const formatMoney = (value) => {
  if (value === null || value === undefined) return '0.00'
  return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 加载钱包信息
const loadWalletInfo = async () => {
  try {
    // 优先从 userStore 获取基础余额
    if (userStore.userInfo) {
      walletInfo.balance = userStore.userInfo.balance || 0
      walletInfo.frozenAmount = userStore.userInfo.frozenAmount || 0
    }

    // 从 API 获取完整钱包信息
    const res = await getWalletInfo()
    if (res.code === 200 && res.data) {
      walletInfo.balance = res.data.balance || 0
      walletInfo.frozenAmount = res.data.frozenAmount || 0
      walletInfo.totalIncome = res.data.totalIncome || 0
      walletInfo.totalExpense = res.data.totalExpense || 0
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
      size: 10
    }
    if (transType.value !== null) {
      params.type = transType.value
    }
    if (dateRange.value) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }

    const res = await getTransactions(params)
    if (res.code === 200 && res.data) {
      transactions.value = res.data.records || []
      totalPages.value = res.data.pages || 1
    }
  } catch (e) {
    console.error('加载交易记录失败', e)
  } finally {
    loading.value = false
  }
}

// 加载银行卡
const loadBankCards = async () => {
  try {
    const res = await getBankCards()
    if (res.code === 200 && res.data) {
      bankAccounts.value = res.data.map(card => ({
        label: `${card.bankName} **** ${card.cardNo.slice(-4)}`,
        value: card.id
      }))
    }
  } catch (e) {
    console.error('加载银行卡失败', e)
  }
}

// 充值
const handleRecharge = async () => {
  if (!rechargeAmount.value || rechargeAmount.value < 1) {
    message.warning('请输入充值金额')
    return
  }

  rechargeLoading.value = true
  try {
    const res = await recharge({
      amount: rechargeAmount.value,
      payType: paymentMethod.value
    })
    
    if (res.code === 200 && res.data) {
      // 判断是否为虚拟支付
      isMockPayment.value = res.data.payType === 'mock'
      
      if (isMockPayment.value) {
        // 虚拟支付：显示提示并等待3秒
        message.info('使用虚拟支付（测试模式），3秒后自动完成')
        showRecharge.value = false
        
        // 3秒后刷新钱包信息
        setTimeout(() => {
          loadWalletInfo()
          loadTransactions()
          message.success('充值成功')
        }, 3000)
      } else {
        // 真实支付：显示二维码
        paymentOrderNo.value = res.data.orderNo
        qrcodeUrl.value = res.data.qrCode || ''
        showRecharge.value = false
        showPayQrcode.value = true
        
        // 开始轮询支付状态
        startPaymentCheck()
      }
    } else {
      message.error(res.message || '充值失败')
    }
  } catch (e) {
    message.error('充值失败: ' + (e.message || '网络错误'))
  } finally {
    rechargeLoading.value = false
  }
}

// 开始轮询支付状态
const startPaymentCheck = () => {
  // 清除之前的定时器
  if (paymentCheckTimer) {
    clearInterval(paymentCheckTimer)
  }
  
  // 每2秒检查一次支付状态
  paymentCheckTimer = setInterval(async () => {
    try {
      const res = await checkPaymentStatus(paymentOrderNo.value)
      if (res.code === 200 && res.data) {
        if (res.data.status === 'success') {
          // 支付成功
          clearInterval(paymentCheckTimer)
          showPayQrcode.value = false
          message.success('支付成功')
          loadWalletInfo()
          loadTransactions()
        } else if (res.data.status === 'failed') {
          // 支付失败
          clearInterval(paymentCheckTimer)
          showPayQrcode.value = false
          message.error('支付失败')
        }
      }
    } catch (e) {
      console.error('检查支付状态失败', e)
    }
  }, 2000)
}

// 停止轮询
const stopPaymentCheck = () => {
  if (paymentCheckTimer) {
    clearInterval(paymentCheckTimer)
    paymentCheckTimer = null
  }
}

// 监听二维码弹窗关闭
const handleQrcodeClose = () => {
  stopPaymentCheck()
  showPayQrcode.value = false
}

// 提现
const handleWithdraw = async () => {
  if (!withdrawAmount.value || withdrawAmount.value < 100) {
    message.warning('最低提现金额为100元')
    return
  }
  if (!withdrawAccount.value) {
    message.warning('请选择提现账户')
    return
  }
  if (withdrawAmount.value > walletInfo.balance) {
    message.warning('提现金额不能超过可用余额')
    return
  }

  withdrawLoading.value = true
  try {
    const res = await withdraw({
      amount: withdrawAmount.value,
      bankCardId: withdrawAccount.value
    })
    if (res.code === 200) {
      message.success('提现申请已提交')
      showWithdraw.value = false
      loadWalletInfo()
      loadTransactions()
    } else {
      message.error(res.message || '提现失败')
    }
  } catch (e) {
    message.error('提现失败: ' + (e.message || '网络错误'))
  } finally {
    withdrawLoading.value = false
  }
}

const getTransTypeName = (type) => {
  const map = { 
    'recharge': '充值', 
    'withdraw': '提现', 
    'deposit': '托管支付', 
    'release': '阶段收款', 
    'refund': '退款' 
  }
  return map[type] || '其他'
}

const getTransIcon = (type) => {
  const map = {
    'recharge': 'fas fa-plus',
    'withdraw': 'fas fa-arrow-up',
    'deposit': 'fas fa-lock',
    'release': 'fas fa-unlock',
    'refund': 'fas fa-undo'
  }
  return map[type] || 'fas fa-exchange-alt'
}

const getTransIconClass = (type) => {
  const map = {
    'recharge': 'bg-green-50 text-green-500',
    'withdraw': 'bg-orange-50 text-orange-500',
    'deposit': 'trans-icon-blue',
    'release': 'bg-green-50 text-green-500',
    'refund': 'bg-purple-50 text-purple-500'
  }
  return map[type] || 'bg-gray-50 text-gray-500'
}

onMounted(() => {
  loadWalletInfo()
  loadTransactions()
  loadBankCards()
})

onUnmounted(() => {
  // 组件卸载时清除定时器
  stopPaymentCheck()
})
</script>

<style scoped>
.amount-btn:hover {
  border-color: #00AFE1;
  color: #00AFE1;
}
.amount-btn-active {
  border-color: #00AFE1;
  color: #00AFE1;
  background: rgba(0, 175, 225, 0.05);
}
.trans-icon-blue {
  background: rgb(239 246 255);
  color: #00AFE1;
}
</style>
