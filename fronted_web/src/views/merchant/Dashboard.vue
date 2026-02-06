<template>
  <div class="space-y-6">
    <div class="flex gap-8">
      <!-- 左侧主内容 -->
      <div class="flex-1 space-y-6">
        <!-- 进行中的任务 -->
        <div class="bg-slate-800 p-8 rounded-3xl border border-slate-700">
          <h3 class="font-black text-lg text-white mb-6">进行中的任务 ({{ inProgressOrders.length }})</h3>
          
          <div class="space-y-4">
            <div 
              v-for="order in inProgressOrders" 
              :key="order.id"
              class="p-5 border border-slate-700 rounded-2xl bg-slate-700/30 hover:border-tf/50 transition cursor-pointer group"
              @click="router.push(`/merchant/orders/${order.id}`)"
            >
              <div class="flex justify-between items-start">
                <div class="flex gap-4">
                  <div class="w-12 h-12 rounded-xl flex items-center justify-center" style="background: rgba(0,175,225,0.1); color: #00AFE1;">
                    <i :class="order.icon"></i>
                  </div>
                  <div>
                    <h4 class="font-bold text-white group-hover:text-tf transition">{{ order.title }}</h4>
                    <p class="text-xs text-gray-400 mt-1">甲方: {{ order.buyerName }} | 当前节点: {{ order.currentStage }}</p>
                  </div>
                </div>
                <div class="flex items-center gap-4">
                  <span class="text-xs text-orange-400">{{ order.remainingDays }}</span>
                  <n-button type="primary" color="#00AFE1" size="small" @click.stop="goToDelivery(order)">
                    提交交付
                  </n-button>
                </div>
              </div>
              
              <!-- 进度条 -->
              <div class="mt-4 pt-4 border-t border-slate-600">
                <div class="flex justify-between text-xs text-gray-400 mb-2">
                  <span>已完成 {{ order.completedStages }}/{{ order.totalStages }} 阶段</span>
                  <span>托管金额: ¥{{ order.depositAmount.toLocaleString() }}</span>
                </div>
                <div class="w-full bg-slate-600 h-1.5 rounded-full">
                  <div 
                    class="h-full rounded-full transition-all"
                    :style="{ width: `${(order.completedStages / order.totalStages) * 100}%`, background: '#00AFE1' }"
                  ></div>
                </div>
              </div>
            </div>

            <div v-if="inProgressOrders.length === 0" class="text-center py-12">
              <div class="w-16 h-16 bg-slate-700 rounded-full flex items-center justify-center mx-auto mb-4">
                <i class="fas fa-inbox text-2xl text-gray-500"></i>
              </div>
              <p class="text-gray-400">暂无进行中的任务</p>
              <n-button type="primary" color="#00AFE1" class="mt-4" @click="router.push('/merchant/tasks')">
                去任务市场接单
              </n-button>
            </div>
          </div>
        </div>

        <!-- 推荐任务 -->
        <div class="bg-slate-800 p-8 rounded-3xl border border-slate-700">
          <div class="flex items-center justify-between mb-6">
            <h3 class="font-black text-lg text-white">热门推荐任务</h3>
            <button class="text-sm font-bold hover:underline" style="color: #00AFE1;" @click="router.push('/merchant/tasks')">
              查看全部 <i class="fas fa-chevron-right ml-1"></i>
            </button>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div 
              v-for="task in recommendedTasks" 
              :key="task.id"
              class="bg-slate-700/50 p-6 rounded-2xl border border-slate-600 hover:border-tf/50 transition cursor-pointer"
            >
              <div class="flex justify-between items-start mb-3">
                <span class="text-xs font-bold" :style="task.tagStyle">{{ task.tag }}</span>
                <h5 class="font-black text-lg" style="color: #00AFE1;">¥ {{ task.amount.toLocaleString() }}</h5>
              </div>
              <h4 class="font-bold text-white">{{ task.title }}</h4>
              <p class="text-xs text-gray-400 mt-2 line-clamp-2">{{ task.description }}</p>
              <div class="mt-4 flex items-center justify-between">
                <span class="text-xs text-gray-500">{{ task.bidsCount }}人已竞标</span>
                <n-button type="primary" color="#00AFE1" size="small" ghost :loading="accepting" @click.stop="handleAcceptOrder(task)">
                  立即抢单
                </n-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧卡片 -->
      <div class="w-80 space-y-6">
        <!-- 可提现金额 -->
        <div 
          class="p-8 rounded-3xl text-white shadow-2xl relative overflow-hidden"
          style="background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%); box-shadow: 0 25px 50px -12px rgba(0,175,225,0.3);"
        >
          <p class="text-white text-sm font-black uppercase mb-2">可提现总额</p>
          <h2 class="text-3xl font-black mb-8">¥ {{ stats.withdrawable.toLocaleString() }}</h2>
          <n-button block class="font-bold" @click="router.push('/merchant/finance')">
            管理钱包/提现
          </n-button>
          <div class="mt-6 pt-4 border-t border-white/20 flex justify-between text-sm text-white">
            <span>托管中金额: ¥{{ stats.frozen.toLocaleString() }}</span>
            <i class="fas fa-circle-info"></i>
          </div>
          <div class="absolute -right-10 -bottom-10 opacity-10">
            <i class="fas fa-coins text-[8rem]"></i>
          </div>
        </div>

        <!-- 数据统计 -->
        <div class="bg-slate-800 p-6 rounded-3xl border border-slate-700">
          <h4 class="font-bold text-white mb-4">本月数据</h4>
          <div class="space-y-4">
            <div class="flex justify-between items-center">
              <span class="text-gray-400 text-sm">接单数</span>
              <span class="text-white font-bold">{{ stats.monthOrders }} 笔</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-gray-400 text-sm">收入金额</span>
              <span class="text-green-400 font-bold">¥ {{ stats.monthIncome.toLocaleString() }}</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-gray-400 text-sm">好评率</span>
              <span class="font-bold" style="color: #00AFE1;">{{ stats.goodRate }}%</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-gray-400 text-sm">信用评分</span>
              <span class="text-white font-bold">{{ stats.creditScore }} 分</span>
            </div>
          </div>
        </div>

        <!-- 快速入口 -->
        <div class="bg-slate-800 p-6 rounded-3xl border border-slate-700">
          <h4 class="font-bold text-white mb-4">快速入口</h4>
          <div class="grid grid-cols-2 gap-3">
            <button class="p-4 bg-slate-700 rounded-xl text-center hover:bg-slate-600 transition" @click="router.push('/merchant/contracts')">
              <i class="fas fa-file-contract text-xl mb-2" style="color: #00AFE1;"></i>
              <p class="text-xs text-gray-300">我的合同</p>
            </button>
            <button class="p-4 bg-slate-700 rounded-xl text-center hover:bg-slate-600 transition" @click="router.push('/merchant/reviews')">
              <i class="fas fa-star text-yellow-400 text-xl mb-2"></i>
              <p class="text-xs text-gray-300">我的评价</p>
            </button>
            <button class="p-4 bg-slate-700 rounded-xl text-center hover:bg-slate-600 transition" @click="openShopSettings">
              <i class="fas fa-cog text-gray-400 text-xl mb-2"></i>
              <p class="text-xs text-gray-300">店铺设置</p>
            </button>
            <button class="p-4 bg-slate-700 rounded-xl text-center hover:bg-slate-600 transition" @click="openCustomerService">
              <i class="fas fa-headset text-green-400 text-xl mb-2"></i>
              <p class="text-xs text-gray-300">在线客服</p>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 店铺设置弹窗 -->
    <n-modal v-model:show="showShopSettings" preset="card" title="店铺设置" class="max-w-2xl">
      <div class="space-y-6">
        <!-- 店铺基本信息 -->
        <div>
          <h4 class="text-sm font-bold text-gray-400 mb-4">店铺基本信息</h4>
          <div class="grid grid-cols-2 gap-4">
            <n-form-item label="店铺名称">
              <n-input v-model:value="shopForm.name" placeholder="请输入店铺名称" />
            </n-form-item>
            <n-form-item label="联系电话">
              <n-input v-model:value="shopForm.phone" placeholder="请输入联系电话" disabled />
            </n-form-item>
          </div>
          <n-form-item label="店铺简介">
            <n-input 
              v-model:value="shopForm.description" 
              type="textarea" 
              placeholder="介绍您的服务特点、优势等..."
              :rows="3"
            />
          </n-form-item>
        </div>

        <!-- 主营业务 -->
        <div>
          <h4 class="text-sm font-bold text-gray-400 mb-4">主营业务</h4>
          <n-cascader
            v-model:value="shopForm.businessCategories"
            :options="categoryOptions"
            :cascade="true"
            multiple
            check-strategy="child"
            :show-path="true"
            placeholder="请选择您的主营业务"
            max-tag-count="responsive"
            clearable
            filterable
            class="w-full"
          />
          <p class="text-xs text-gray-500 mt-2">选择您擅长的业务类型，任务市场将优先展示相关任务</p>
        </div>

        <!-- 服务能力 -->
        <div>
          <h4 class="text-sm font-bold text-gray-400 mb-4">服务能力</h4>
          <div class="grid grid-cols-2 gap-4">
            <n-form-item label="接单上限（同时进行）">
              <n-input-number v-model:value="shopForm.maxOrders" :min="1" :max="20" class="w-full" />
            </n-form-item>
            <n-form-item label="最低接单金额">
              <n-input-number v-model:value="shopForm.minAmount" :min="0" :step="100" class="w-full">
                <template #prefix>¥</template>
              </n-input-number>
            </n-form-item>
          </div>
          <n-form-item label="技术栈 / 擅长领域">
            <n-dynamic-tags v-model:value="shopForm.skills" />
          </n-form-item>
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end gap-3">
          <n-button @click="showShopSettings = false">取消</n-button>
          <n-button type="primary" color="#00AFE1" :loading="savingShop" @click="saveShopSettings">
            保存设置
          </n-button>
        </div>
      </template>
    </n-modal>

    <!-- 在线客服弹窗 -->
    <n-modal v-model:show="showCustomerService" preset="card" title="在线客服" class="max-w-lg">
      <div class="space-y-4">
        <div class="p-4 bg-slate-700 rounded-xl text-center">
          <div class="w-16 h-16 bg-green-500/20 rounded-full flex items-center justify-center mx-auto mb-3">
            <i class="fas fa-headset text-3xl text-green-400"></i>
          </div>
          <h3 class="font-bold text-white text-lg">平台客服</h3>
          <p class="text-gray-400 text-sm mt-1">工作时间: 9:00 - 22:00</p>
        </div>

        <div class="grid grid-cols-2 gap-3">
          <button 
            class="p-4 bg-slate-700 rounded-xl text-center hover:bg-slate-600 transition group"
            @click="copyContact('qq', '123456789')"
          >
            <i class="fab fa-qq text-2xl text-blue-400 mb-2"></i>
            <p class="text-xs text-gray-300">QQ客服</p>
            <p class="text-xs text-gray-500 group-hover:text-blue-400 transition">123456789</p>
            <p class="text-xs text-gray-500 mt-1">点击复制</p>
          </button>
          <button 
            class="p-4 bg-slate-700 rounded-xl text-center hover:bg-slate-600 transition group"
            @click="copyContact('wechat', 'TrustFulfillment')"
          >
            <i class="fab fa-weixin text-2xl text-green-400 mb-2"></i>
            <p class="text-xs text-gray-300">微信客服</p>
            <p class="text-xs text-gray-500 group-hover:text-green-400 transition">TrustFulfillment</p>
            <p class="text-xs text-gray-500 mt-1">点击复制</p>
          </button>
        </div>

        <div class="p-4 bg-blue-500/10 rounded-xl">
          <p class="text-xs text-blue-400">
            <i class="fas fa-info-circle mr-1"></i>
            您也可以通过订单详情页的"联系客服"按钮，发起与平台客服的在线沟通
          </p>
        </div>

        <n-button block type="primary" color="#00AFE1" @click="openOnlineChat">
          <template #icon><i class="fas fa-comments"></i></template>
          发起在线咨询
        </n-button>
      </div>
    </n-modal>

    <!-- 在线客服聊天抽屉 -->
    <ServiceChatDrawer v-model:show="showChatDrawer" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { getOrderList, getOrderStats, acceptOrder } from '@/api/order'
import { getWalletInfo } from '@/api/wallet'
import { getBusinessCategories, updateUserInfo } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import ServiceChatDrawer from '@/components/ServiceChatDrawer.vue'

const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

// 注入刷新顶栏余额的方法
const refreshBalance = inject('refreshBalance', () => {})

const loading = ref(false)
const accepting = ref(false)

// 店铺设置相关
const showShopSettings = ref(false)
const savingShop = ref(false)
const categoryOptions = ref([])
const shopForm = reactive({
  name: '',
  phone: '',
  description: '',
  businessCategories: null,
  maxOrders: 5,
  minAmount: 0,
  skills: []
})

// 在线客服相关
const showCustomerService = ref(false)
const showChatDrawer = ref(false)

const stats = reactive({
  withdrawable: 0,
  frozen: 0,
  monthOrders: 0,
  monthIncome: 0,
  goodRate: 0,
  creditScore: 0
})

const inProgressOrders = ref([])

const recommendedTasks = ref([])

const loadData = async () => {
  loading.value = true
  try {
    // 加载钱包信息
    const walletRes = await getWalletInfo()
    if (walletRes.data) {
      stats.withdrawable = walletRes.data.balance || 0
      stats.frozen = walletRes.data.frozenAmount || 0
      // 同步刷新顶栏余额显示
      refreshBalance()
    }

    // 加载统计数据
    const statsRes = await getOrderStats()
    if (statsRes.data) {
      stats.monthOrders = statsRes.data.monthOrders || 0
      stats.monthIncome = statsRes.data.monthIncome || 0
      stats.goodRate = statsRes.data.goodRate || 0
      stats.creditScore = statsRes.data.creditScore || 0
    }

    // 加载进行中的订单
    const ordersRes = await getOrderList({ status: 2, size: 5 })
    if (ordersRes.data && ordersRes.data.records) {
      inProgressOrders.value = ordersRes.data.records.map(order => ({
        id: order.id,
        title: order.title,
        icon: 'fas fa-tasks',
        buyerName: order.buyerName || '未知',
        currentStage: getStageLabel(order),
        remainingDays: getRemainingDays(order.deliveryTime),
        depositAmount: order.depositAmount || 0,
        completedStages: order.completedStages || 0,
        totalStages: order.totalStages || 1
      }))
    }

    // 加载推荐任务（待接单的订单 status=0）
    const tasksRes = await getOrderList({ status: 0, size: 4 })
    if (tasksRes.data && tasksRes.data.records) {
      recommendedTasks.value = tasksRes.data.records.map(order => ({
        id: order.id,
        title: order.title,
        description: order.description || '',
        amount: order.totalAmount || 0,
        tag: '待接单',
        tagClass: 'px-2 py-1 rounded text-xs',
        tagStyle: 'background: rgba(0,175,225,0.2); color: #00AFE1;',
        bidsCount: 0
      }))
    }
  } catch (e) {
    console.error('加载数据失败', e)
  } finally {
    loading.value = false
  }
}

// 立即抢单
const handleAcceptOrder = async (task) => {
  if (accepting.value) return
  accepting.value = true
  try {
    await acceptOrder(task.id)
    message.success('接单成功！')
    // 重新加载数据
    await loadData()
  } catch (e) {
    console.error('接单失败', e)
    message.error(e.message || '接单失败，请重试')
  } finally {
    accepting.value = false
  }
}

const getStageLabel = (order) => {
  const statusLabels = {
    0: '待审核',
    1: '待托管',
    2: '进行中',
    3: '待验收',
    4: '已完成',
    5: '已取消',
    6: '纠纷中'
  }
  return statusLabels[order.status] || '未知'
}

const getRemainingDays = (deliveryTime) => {
  if (!deliveryTime) return ''
  const now = new Date()
  const delivery = new Date(deliveryTime)
  const days = Math.ceil((delivery - now) / (1000 * 60 * 60 * 24))
  if (days > 0) return `剩余${days}天`
  if (days === 0) return '今日截止'
  return `已逾期${Math.abs(days)}天`
}

const goToDelivery = (order) => {
  router.push(`/merchant/delivery/${order.id}`)
}

// 转换分类数据为级联选择器格式
const transformCategories = (categories) => {
  return categories.map(cat => ({
    value: cat.id,
    label: cat.name,
    children: cat.children && cat.children.length > 0 
      ? transformCategories(cat.children) 
      : undefined
  }))
}

// 加载分类数据
const loadCategories = async () => {
  try {
    const res = await getBusinessCategories()
    if (res.data) {
      categoryOptions.value = transformCategories(res.data)
    }
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

// 打开店铺设置
const openShopSettings = async () => {
  const user = userStore.userInfo
  shopForm.name = user.nickname || ''
  shopForm.phone = user.phone || ''
  shopForm.description = user.shopDescription || ''
  shopForm.maxOrders = user.maxOrders || 5
  shopForm.minAmount = user.minAmount || 0
  shopForm.skills = user.skills ? user.skills.split(',') : []
  
  // 解析主营业务
  if (user.businessCategories) {
    shopForm.businessCategories = user.businessCategories.split(',').map(Number).filter(n => !isNaN(n))
  } else {
    shopForm.businessCategories = null
  }
  
  showShopSettings.value = true
  await loadCategories()
}

// 保存店铺设置
const saveShopSettings = async () => {
  savingShop.value = true
  try {
    const businessCategoriesStr = shopForm.businessCategories 
      ? shopForm.businessCategories.join(',') 
      : ''
    
    await updateUserInfo({
      nickname: shopForm.name,
      shopDescription: shopForm.description,
      businessCategories: businessCategoriesStr,
      maxOrders: shopForm.maxOrders,
      minAmount: shopForm.minAmount,
      skills: shopForm.skills.join(',')
    })
    
    // 更新本地存储
    userStore.userInfo.nickname = shopForm.name
    userStore.userInfo.shopDescription = shopForm.description
    userStore.userInfo.businessCategories = businessCategoriesStr
    userStore.userInfo.maxOrders = shopForm.maxOrders
    userStore.userInfo.minAmount = shopForm.minAmount
    userStore.userInfo.skills = shopForm.skills.join(',')
    
    sessionStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
    if (localStorage.getItem('userInfo')) {
      localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
    }
    
    message.success('店铺设置保存成功')
    showShopSettings.value = false
  } catch (e) {
    message.error(e.message || '保存失败')
  } finally {
    savingShop.value = false
  }
}

// 打开在线客服
const openCustomerService = () => {
  showCustomerService.value = true
}

// 复制联系方式
const copyContact = async (type, contact) => {
  try {
    await navigator.clipboard.writeText(contact)
    if (type === 'qq') {
      message.success(`QQ号 ${contact} 已复制到剪贴板`)
    } else if (type === 'wechat') {
      message.success(`微信号 ${contact} 已复制到剪贴板`)
    }
  } catch (e) {
    // 降级方案
    const input = document.createElement('input')
    input.value = contact
    document.body.appendChild(input)
    input.select()
    document.execCommand('copy')
    document.body.removeChild(input)
    if (type === 'qq') {
      message.success(`QQ号 ${contact} 已复制到剪贴板`)
    } else if (type === 'wechat') {
      message.success(`微信号 ${contact} 已复制到剪贴板`)
    }
  }
}

// 发起在线咨询
const openOnlineChat = () => {
  showCustomerService.value = false
  showChatDrawer.value = true
}

onMounted(() => {
  loadData()
})
</script>
