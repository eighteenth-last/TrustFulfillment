<template>
  <div class="max-w-4xl mx-auto space-y-6">
    <!-- 顶部导航 -->
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button @click="router.back()" class="w-10 h-10 bg-slate-700 rounded-full flex items-center justify-center text-gray-400 hover:text-tf hover:bg-slate-600 transition">
          <i class="fas fa-arrow-left"></i>
        </button>
        <div>
          <h2 class="text-xl font-black text-white">签署项目合同</h2>
          <p class="text-sm text-gray-400 mt-1">合同编号: {{ contract.contractNo }}</p>
        </div>
      </div>
      <div class="flex items-center gap-3">
        <span :class="getStatusClass(contract.status)" class="px-4 py-1.5 rounded-full text-sm font-bold">
          {{ getStatusText(contract.status) }}
        </span>
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="flex justify-center py-20">
      <n-spin size="large" />
    </div>

    <template v-else-if="contract.id">
      <!-- 合同内容预览 -->
      <div class="bg-white rounded-3xl p-8 shadow-lg">
        <!-- 合同头部 -->
        <div class="text-center mb-8 pb-6 border-b border-gray-200">
          <h1 class="text-2xl font-black text-gray-800">{{ contract.title }}</h1>
          <p class="text-sm text-gray-500 mt-2">合同编号: {{ contract.contractNo }}</p>
        </div>

        <!-- 双方信息 -->
        <div class="grid grid-cols-2 gap-6 mb-8">
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-500 font-bold mb-2">甲方（委托方）</p>
            <p class="text-gray-800 font-bold">{{ contract.partyAName }}</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-500 font-bold mb-2">乙方（服务方）</p>
            <p class="text-gray-800 font-bold">{{ contract.partyBName }}</p>
          </div>
        </div>

        <!-- 项目信息 -->
        <div class="space-y-6 mb-8">
          <div>
            <h3 class="font-bold text-gray-800 mb-2">一、项目描述</h3>
            <p class="text-gray-600 whitespace-pre-line">{{ contract.projectDesc }}</p>
          </div>

          <div v-if="contract.techRequirements">
            <h3 class="font-bold text-gray-800 mb-2">二、技术要求</h3>
            <p class="text-gray-600 whitespace-pre-line">{{ contract.techRequirements }}</p>
          </div>

          <div>
            <h3 class="font-bold text-gray-800 mb-2">三、验收标准</h3>
            <p class="text-gray-600 whitespace-pre-line">{{ contract.deliveryStandard }}</p>
          </div>

          <div v-if="contract.deliveryDeadline">
            <h3 class="font-bold text-gray-800 mb-2">四、交付期限</h3>
            <p class="text-gray-600">{{ contract.deliveryDeadline }}</p>
          </div>
        </div>

        <!-- 付款条款 -->
        <div class="mb-8">
          <h3 class="font-bold text-gray-800 mb-4">五、付款条款</h3>
          <div class="p-4 bg-green-50 rounded-xl mb-4">
            <div class="flex justify-between items-center">
              <span class="text-gray-600">合同总金额</span>
              <span class="text-xl font-black text-green-600">¥ {{ contract.totalAmount?.toLocaleString() }}</span>
            </div>
          </div>
          <div class="space-y-2">
            <div 
              v-for="(stage, index) in paymentStages" 
              :key="index"
              class="flex items-center gap-4 p-3 bg-gray-50 rounded-lg"
            >
              <span class="w-6 h-6 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center text-sm font-bold">
                {{ index + 1 }}
              </span>
              <span class="flex-1 text-gray-700">{{ stage.name }}</span>
              <span class="text-gray-500">{{ stage.milestone }}</span>
              <span class="font-bold text-gray-800">¥{{ stage.amount?.toLocaleString() }} ({{ stage.percent }}%)</span>
            </div>
          </div>
        </div>

        <!-- 法律条款 -->
        <div class="space-y-6 mb-8 text-sm">
          <div>
            <h3 class="font-bold text-gray-800 mb-2">六、违约条款</h3>
            <p class="text-gray-600 whitespace-pre-line">{{ contract.breachClause }}</p>
          </div>
          <div>
            <h3 class="font-bold text-gray-800 mb-2">七、保密协议</h3>
            <p class="text-gray-600 whitespace-pre-line">{{ contract.confidentialClause }}</p>
          </div>
          <div>
            <h3 class="font-bold text-gray-800 mb-2">八、知识产权归属</h3>
            <p class="text-gray-600 whitespace-pre-line">{{ contract.ipClause }}</p>
          </div>
          <div>
            <h3 class="font-bold text-gray-800 mb-2">九、争议解决</h3>
            <p class="text-gray-600 whitespace-pre-line">{{ contract.disputeClause }}</p>
          </div>
        </div>

        <!-- 签署区域 -->
        <div class="border-t border-gray-200 pt-8">
          <h3 class="font-bold text-gray-800 mb-6 text-center">签署确认</h3>
          
          <div class="grid grid-cols-2 gap-8">
            <!-- 甲方签名 -->
            <div class="p-4 bg-gray-50 rounded-xl">
              <p class="text-sm font-bold text-gray-600 mb-3">甲方签名</p>
              <div v-if="contract.partyASignature" class="bg-white p-2 rounded-lg border border-gray-200">
                <img :src="contract.partyASignature" alt="甲方签名" class="max-h-24 mx-auto" />
                <p class="text-xs text-gray-400 text-center mt-2">
                  签署时间: {{ formatDateTime(contract.partyASignTime) }}
                </p>
              </div>
              <div v-else class="h-24 bg-white rounded-lg border-2 border-dashed border-gray-300 flex items-center justify-center text-gray-400">
                等待签署
              </div>
            </div>

            <!-- 乙方签名 -->
            <div class="p-4 bg-gray-50 rounded-xl">
              <p class="text-sm font-bold text-gray-600 mb-3">乙方签名</p>
              <div v-if="contract.partyBSignature" class="bg-white p-2 rounded-lg border border-gray-200">
                <img :src="contract.partyBSignature" alt="乙方签名" class="max-h-24 mx-auto" />
                <p class="text-xs text-gray-400 text-center mt-2">
                  签署时间: {{ formatDateTime(contract.partyBSignTime) }}
                </p>
              </div>
              <div v-else class="h-24 bg-white rounded-lg border-2 border-dashed border-gray-300 flex items-center justify-center text-gray-400">
                等待签署
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 签署操作 -->
      <div v-if="canSign" class="bg-slate-800 rounded-3xl p-8 border border-slate-700">
        <h3 class="font-black text-white mb-6 flex items-center gap-2">
          <i class="fas fa-signature" style="color: #00AFE1;"></i>
          我的签名
        </h3>

        <SignaturePad
          ref="signaturePadRef"
          v-model="signature"
          label="请在下方签名"
          :width="500"
          :height="200"
        />

        <div class="mt-6">
          <n-button 
            block 
            type="primary" 
            color="#00AFE1"
            size="large"
            class="font-bold h-12"
            :loading="signing"
            :disabled="!signature"
            @click="handleSign"
          >
            <template #icon><i class="fas fa-check"></i></template>
            确认签署合同
          </n-button>
        </div>

        <p class="text-xs text-gray-400 text-center mt-4">
          点击"确认签署"表示您已阅读并同意上述合同条款
        </p>
      </div>

      <!-- 已签署提示 -->
      <div v-else-if="alreadySigned" class="bg-green-500/10 rounded-3xl p-8 border border-green-500/30 text-center">
        <i class="fas fa-check-circle text-4xl text-green-400 mb-4"></i>
        <h3 class="text-xl font-bold text-green-400 mb-2">您已完成签署</h3>
        <p class="text-gray-400 mb-4">
          {{ contract.status === 3 ? '合同已生效，项目已启动！' : '等待甲方签署中...' }}
        </p>
        <n-button 
          type="primary" 
          color="#00AFE1"
          size="large"
          @click="router.push(`/merchant/orders/${route.params.id}`)"
        >
          <template #icon><i class="fas fa-arrow-right"></i></template>
          {{ contract.status === 3 ? '查看订单详情' : '返回订单详情' }}
        </n-button>
      </div>

      <!-- 等待商家提交 -->
      <div v-else-if="contract.status === 0" class="bg-yellow-500/10 rounded-3xl p-8 border border-yellow-500/30 text-center">
        <i class="fas fa-edit text-4xl text-yellow-400 mb-4"></i>
        <h3 class="text-xl font-bold text-yellow-400 mb-2">合同编辑中</h3>
        <p class="text-gray-400 mb-4">合同尚未提交，请先完成编辑并提交</p>
        <n-button type="primary" color="#00AFE1" @click="router.push(`/merchant/orders/${route.params.id}/contract`)">
          继续编辑合同
        </n-button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import { useUserStore } from '@/stores/user'
import { getContractByOrder, signContract } from '@/api/contract'
import SignaturePad from '@/components/SignaturePad.vue'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const userStore = useUserStore()

const loading = ref(true)
const signing = ref(false)
const signature = ref('')
const signaturePadRef = ref(null)

const contract = reactive({
  id: null,
  contractNo: '',
  title: '',
  partyAId: null,
  partyAName: '',
  partyBId: null,
  partyBName: '',
  projectDesc: '',
  techRequirements: '',
  deliveryStandard: '',
  deliveryDeadline: '',
  totalAmount: 0,
  paymentTerms: '',
  breachClause: '',
  confidentialClause: '',
  ipClause: '',
  disputeClause: '',
  partyASignature: '',
  partyASignTime: null,
  partyBSignature: '',
  partyBSignTime: null,
  status: 0
})

const paymentStages = computed(() => {
  if (!contract.paymentTerms) return []
  try {
    return JSON.parse(contract.paymentTerms)
  } catch {
    return []
  }
})

// 判断当前用户是否可以签署
const canSign = computed(() => {
  const userId = userStore.userInfo?.id
  if (!userId || !contract.id) return false
  
  // 乙方(商家)在状态1或2时可以签署（如果还没签）
  if (contract.partyBId === userId) {
    return (contract.status === 1 || contract.status === 2) && !contract.partyBSignature
  }
  
  return false
})

// 判断当前用户是否已签署
const alreadySigned = computed(() => {
  const userId = userStore.userInfo?.id
  if (!userId || !contract.id) return false
  
  if (contract.partyBId === userId && contract.partyBSignature) {
    return true
  }
  
  return false
})

const loadContract = async () => {
  loading.value = true
  try {
    const res = await getContractByOrder(route.params.id)
    if (res.data) {
      Object.assign(contract, res.data)
    } else {
      message.warning('合同不存在，请先创建合同')
      router.push(`/merchant/orders/${route.params.id}/contract`)
    }
  } catch (e) {
    message.error(e.message || '加载合同失败')
  } finally {
    loading.value = false
  }
}

const handleSign = async () => {
  if (!signature.value) {
    message.warning('请先签名')
    return
  }

  signing.value = true
  try {
    const res = await signContract(contract.id, signature.value)
    message.success('签署成功')
    
    // 刷新合同信息
    if (res.data) {
      Object.assign(contract, res.data)
    }
    
    // 根据合同状态提示并跳转
    if (contract.status === 3) {
      // 合同已生效（双方都签署了），项目已自动托管启动
      message.success('合同已生效！项目已启动，可以开始执行')
      setTimeout(() => {
        router.push(`/merchant/orders/${route.params.id}`)
      }, 1500)
    } else {
      // 合同未生效，等待甲方签署
      message.info('等待甲方签署合同')
    }
  } catch (e) {
    message.error(e.message || '签署失败')
  } finally {
    signing.value = false
  }
}

const getStatusText = (status) => {
  const map = {
    0: '草稿',
    1: '待甲方签署',
    2: '待乙方签署',
    3: '已生效',
    4: '已作废'
  }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = {
    0: 'bg-gray-500/20 text-gray-400',
    1: 'bg-yellow-500/20 text-yellow-400',
    2: 'bg-orange-500/20 text-orange-400',
    3: 'bg-green-500/20 text-green-400',
    4: 'bg-red-500/20 text-red-400'
  }
  return map[status] || 'bg-gray-500/20 text-gray-400'
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace('T', ' ').substring(0, 16)
}

onMounted(() => {
  loadContract()
})
</script>
