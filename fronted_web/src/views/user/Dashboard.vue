<template>
  <div class="space-y-6">
    <!-- 欢迎横幅 -->
    <div 
      class="rounded-[2rem] p-10 text-white relative overflow-hidden"
      style="background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);"
    >
      <div class="relative z-10 max-w-xl">
        <h1 class="text-3xl font-black mb-3">开启信托保障，让项目更安全。</h1>
        <p class="text-white text-base font-medium mb-6 opacity-90">臻托通过银行级的资金存托协议，确保每一分投入都在看到成果后才进行结算。</p>
        <n-button size="large" class="font-bold px-8" @click="router.push('/user/create-order')">
          立刻发布新项目
        </n-button>
      </div>
      <div class="absolute right-10 top-1/2 -translate-y-1/2 opacity-20">
        <i class="fas fa-shield-alt text-[12rem]"></i>
      </div>
      <div class="absolute -right-20 -top-20 w-64 h-64 bg-white/10 rounded-full blur-3xl"></div>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-4 gap-6">
      <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm hover:shadow-md transition cursor-pointer" @click="router.push('/user/orders')">
        <div class="w-12 h-12 rounded-2xl flex items-center justify-center mb-4 text-xl" style="background: rgba(0,175,225,0.1); color: #00AFE1;">
          <i class="fas fa-paper-plane"></i>
        </div>
        <p class="text-xs text-gray-400 font-bold uppercase tracking-wider mb-1">全部项目</p>
        <h3 class="text-3xl font-black text-gray-800">{{ stats.total }}</h3>
      </div>
      <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm hover:shadow-md transition cursor-pointer" @click="router.push('/user/wallet')">
        <div class="w-12 h-12 bg-orange-50 text-orange-500 rounded-2xl flex items-center justify-center mb-4 text-xl">
          <i class="fas fa-lock"></i>
        </div>
        <p class="text-xs text-gray-400 font-bold uppercase tracking-wider mb-1">托管中资金</p>
        <h3 class="text-3xl font-black text-gray-800">¥ {{ (stats.frozenAmount || 0).toLocaleString() }}</h3>
      </div>
      <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm hover:shadow-md transition">
        <div class="w-12 h-12 bg-green-50 text-green-500 rounded-2xl flex items-center justify-center mb-4 text-xl">
          <i class="fas fa-check-circle"></i>
        </div>
        <p class="text-xs text-gray-400 font-bold uppercase tracking-wider mb-1">已完结</p>
        <h3 class="text-3xl font-black text-gray-800">{{ stats.completed }}</h3>
      </div>
      <div class="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm hover:shadow-md transition cursor-pointer" @click="router.push('/user/disputes')">
        <div class="w-12 h-12 bg-purple-50 text-purple-500 rounded-2xl flex items-center justify-center mb-4 text-xl">
          <i class="fas fa-gavel"></i>
        </div>
        <p class="text-xs text-gray-400 font-bold uppercase tracking-wider mb-1">投诉维权</p>
        <h3 class="text-3xl font-black text-gray-800">{{ stats.disputes }}</h3>
      </div>
    </div>

    <!-- 近期项目 -->
    <div class="bg-white rounded-3xl border border-gray-100 shadow-sm p-8">
      <div class="flex items-center justify-between mb-6">
        <h3 class="font-black text-lg text-gray-800">我的托管项目</h3>
        <button class="text-sm font-bold hover:underline" style="color: #00AFE1;" @click="router.push('/user/orders')">
          管理全部项目 <i class="fas fa-chevron-right ml-1"></i>
        </button>
      </div>

      <div class="space-y-4">
        <div 
          v-for="order in recentOrders" 
          :key="order.id" 
          class="flex items-center justify-between p-5 bg-slate-50 rounded-2xl border border-transparent hover:bg-white hover:shadow-md transition cursor-pointer group"
          @click="router.push(`/user/orders/${order.id}`)"
        >
          <div class="flex items-center gap-5">
            <div class="w-14 h-14 bg-white rounded-2xl flex items-center justify-center text-2xl shadow-sm transition" style="color: #00AFE1;">
              <i :class="order.icon"></i>
            </div>
            <div>
              <h4 class="font-bold text-gray-800 transition">{{ order.title }}</h4>
              <div class="flex gap-4 mt-1.5 text-xs text-gray-400">
                <span>服务商: {{ order.sellerName }}</span>
                <span>状态: {{ order.currentStage }}</span>
              </div>
            </div>
          </div>
          <div class="flex items-center gap-8">
            <div class="text-right">
              <p class="text-xs text-gray-400 font-bold uppercase mb-1">托管金额</p>
              <span class="text-lg font-black text-gray-800">¥{{ (order.totalAmount || 0).toLocaleString() }}</span>
            </div>
            <n-button type="primary" color="#00AFE1" size="small" class="font-bold">
              查看详情
            </n-button>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="recentOrders.length === 0" class="text-center py-12">
          <div class="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <i class="fas fa-folder-open text-2xl text-gray-300"></i>
          </div>
          <p class="text-gray-400 mb-4">暂无项目</p>
          <n-button type="primary" color="#00AFE1" @click="router.push('/user/create-order')">
            发布新项目
          </n-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getOrderStats, getOrderList } from '@/api/order'
import { getWalletInfo } from '@/api/wallet'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const userBalance = ref(0)

const stats = reactive({
  total: 0,
  inProgress: 0,
  frozenAmount: 0,
  completed: 0,
  disputes: 0
})

const recentOrders = ref([])

const statusIcons = {
  0: 'fas fa-clock',
  1: 'fas fa-file-contract',
  2: 'fas fa-cogs',
  3: 'fas fa-check-double',
  4: 'fas fa-check-circle',
  5: 'fas fa-times-circle',
  6: 'fas fa-gavel'
}

const statusLabels = {
  0: '待接单',
  1: '待托管',
  2: '进行中',
  3: '待验收',
  4: '已完成',
  5: '已取消',
  6: '纠纷中'
}

const loadData = async () => {
  loading.value = true
  try {
    // 加载用户统计数据
    const statsRes = await getOrderStats()
    console.log('Dashboard 统计响应:', statsRes)
    if (statsRes && statsRes.data) {
      stats.total = statsRes.data.total ?? 0
      stats.inProgress = statsRes.data.inProgress ?? 0
      stats.frozenAmount = statsRes.data.frozenAmount ?? 0
      stats.completed = statsRes.data.completed ?? 0
      stats.disputes = statsRes.data.disputes ?? 0
    }

    // 加载最近的订单（不限状态）
    const ordersRes = await getOrderList({ page: 1, size: 5 })
    console.log('Dashboard 订单响应:', ordersRes)
    if (ordersRes && ordersRes.data && ordersRes.data.records) {
      recentOrders.value = ordersRes.data.records.map(order => ({
        id: order.id,
        title: order.title,
        icon: statusIcons[order.status] || 'fas fa-file',
        sellerName: order.sellerName || '待接单',
        currentStage: statusLabels[order.status] || '未知',
        totalAmount: order.totalAmount || 0,
        completedStages: order.completedStages || 0,
        totalStages: order.totalStages || 1
      }))
      
      // 如果 stats API 没有返回数据，从订单列表计算
      if (stats.total === 0 && ordersRes.data.records.length > 0) {
        stats.total = ordersRes.data.total || ordersRes.data.records.length
      }
    }

    // 从钱包API获取余额
    try {
      const walletRes = await getWalletInfo()
      if (walletRes && walletRes.data) {
        userBalance.value = walletRes.data.balance || 0
      }
    } catch (e) {
      console.error('加载钱包余额失败', e)
    }
  } catch (e) {
    console.error('加载数据失败', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
