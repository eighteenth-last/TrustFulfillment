<template>
  <div class="max-w-4xl mx-auto space-y-6">
    <!-- 顶部导航 -->
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button @click="router.back()" class="w-10 h-10 bg-slate-700 rounded-full flex items-center justify-center text-gray-400 hover:text-tf hover:bg-slate-600 transition">
          <i class="fas fa-arrow-left"></i>
        </button>
        <div>
          <h2 class="text-xl font-black text-white">编辑项目合同</h2>
          <p class="text-sm text-gray-400 mt-1">订单号: {{ order.orderNo }}</p>
        </div>
      </div>
      <div class="flex items-center gap-3">
        <span class="px-4 py-1.5 rounded-full text-sm font-bold bg-yellow-500/20 text-yellow-400">
          {{ contract.id ? '编辑中' : '新建合同' }}
        </span>
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="flex justify-center py-20">
      <n-spin size="large" />
    </div>

    <template v-else>
      <!-- 合同表单 -->
      <div class="bg-slate-800 rounded-3xl p-8 border border-slate-700">
        <h3 class="font-black text-white mb-6 flex items-center gap-2">
          <i class="fas fa-file-contract" style="color: #00AFE1;"></i>
          合同基本信息
        </h3>

        <div class="space-y-6">
          <!-- 合同标题 -->
          <n-form-item label="合同标题" required>
            <n-input 
              v-model:value="form.title" 
              placeholder="请输入合同标题"
              size="large"
            />
          </n-form-item>

          <!-- 双方信息 -->
          <div class="grid grid-cols-2 gap-6">
            <div class="p-4 bg-slate-700/50 rounded-xl">
              <p class="text-xs text-gray-400 font-bold mb-2">甲方（委托方）</p>
              <p class="text-white font-bold">{{ contract.partyAName || order.buyerName }}</p>
            </div>
            <div class="p-4 bg-slate-700/50 rounded-xl">
              <p class="text-xs text-gray-400 font-bold mb-2">乙方（服务方）</p>
              <p class="text-white font-bold">{{ contract.partyBName || '当前商家' }}</p>
            </div>
          </div>

          <!-- 项目描述 -->
          <n-form-item label="项目描述" required>
            <n-input 
              v-model:value="form.projectDesc" 
              type="textarea"
              placeholder="详细描述项目内容、功能需求等"
              :rows="4"
            />
          </n-form-item>

          <!-- 技术要求 -->
          <n-form-item label="技术要求">
            <n-input 
              v-model:value="form.techRequirements" 
              type="textarea"
              placeholder="描述技术栈、架构要求等"
              :rows="3"
            />
          </n-form-item>

          <!-- 验收标准 -->
          <n-form-item label="验收标准" required>
            <n-input 
              v-model:value="form.deliveryStandard" 
              type="textarea"
              placeholder="详细描述项目验收标准、交付物要求等"
              :rows="4"
            />
          </n-form-item>

          <!-- 交付期限 -->
          <n-form-item label="交付期限">
            <n-date-picker 
              v-model:value="form.deliveryDeadline"
              type="date"
              class="w-full"
              size="large"
            />
          </n-form-item>
        </div>
      </div>

      <!-- 付款条款 -->
      <div class="bg-slate-800 rounded-3xl p-8 border border-slate-700">
        <h3 class="font-black text-white mb-6 flex items-center gap-2">
          <i class="fas fa-coins" style="color: #00AFE1;"></i>
          付款条款
        </h3>

        <div class="mb-4 p-4 bg-slate-700/50 rounded-xl">
          <div class="flex justify-between items-center">
            <span class="text-gray-400">合同总金额</span>
            <span class="text-2xl font-black text-green-400">¥ {{ order.totalAmount?.toLocaleString() }}</span>
          </div>
        </div>

        <div class="space-y-3">
          <div 
            v-for="(stage, index) in stages" 
            :key="index"
            class="flex items-center gap-4 p-4 bg-slate-700/30 rounded-xl"
          >
            <div class="w-8 h-8 rounded-full bg-tf/20 text-tf flex items-center justify-center font-bold">
              {{ index + 1 }}
            </div>
            <div class="flex-1">
              <p class="font-bold text-white">{{ stage.stageName }}</p>
              <p class="text-xs text-gray-400">{{ stage.milestone }}</p>
            </div>
            <div class="text-right">
              <p class="font-bold text-white">¥ {{ stage.amount?.toLocaleString() }}</p>
              <p class="text-xs text-gray-400">{{ stage.percent }}%</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 法律条款 -->
      <div class="bg-slate-800 rounded-3xl p-8 border border-slate-700">
        <h3 class="font-black text-white mb-6 flex items-center gap-2">
          <i class="fas fa-gavel" style="color: #00AFE1;"></i>
          法律条款
        </h3>

        <n-collapse>
          <n-collapse-item title="违约条款" name="breach">
            <n-input 
              v-model:value="form.breachClause" 
              type="textarea"
              :rows="4"
            />
          </n-collapse-item>
          <n-collapse-item title="保密协议" name="confidential">
            <n-input 
              v-model:value="form.confidentialClause" 
              type="textarea"
              :rows="4"
            />
          </n-collapse-item>
          <n-collapse-item title="知识产权归属" name="ip">
            <n-input 
              v-model:value="form.ipClause" 
              type="textarea"
              :rows="4"
            />
          </n-collapse-item>
          <n-collapse-item title="争议解决方式" name="dispute">
            <n-input 
              v-model:value="form.disputeClause" 
              type="textarea"
              :rows="4"
            />
          </n-collapse-item>
        </n-collapse>
      </div>

      <!-- 操作按钮 -->
      <div class="flex gap-4">
        <n-button 
          size="large" 
          class="flex-1"
          :loading="saving"
          @click="saveContract"
        >
          <template #icon><i class="fas fa-save"></i></template>
          保存草稿
        </n-button>
        <n-button 
          type="primary" 
          color="#00AFE1"
          size="large" 
          class="flex-1 font-bold"
          :loading="submitting"
          @click="submitContract"
        >
          <template #icon><i class="fas fa-paper-plane"></i></template>
          提交合同并签署
        </n-button>
      </div>
    </template>
  </div>
</template>

<style scoped>
/* 深色主题表单样式 */
:deep(.n-form-item-label) {
  color: #e2e8f0 !important;
}

:deep(.n-form-item .n-form-item-label__text) {
  color: #e2e8f0 !important;
}

:deep(.n-collapse-item__header-main) {
  color: #e2e8f0 !important;
}

:deep(.n-collapse) {
  --n-title-text-color: #e2e8f0;
}
</style>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import { getOrderDetail } from '@/api/order'
import { createOrUpdateContract, submitContract as apiSubmitContract, getContractByOrder } from '@/api/contract'

const router = useRouter()
const route = useRoute()
const message = useMessage()

const loading = ref(true)
const saving = ref(false)
const submitting = ref(false)

const order = reactive({
  id: null,
  orderNo: '',
  title: '',
  description: '',
  techStack: '',
  totalAmount: 0,
  buyerName: '',
  deliveryTime: null
})

const contract = reactive({
  id: null,
  partyAName: '',
  partyBName: ''
})

const stages = ref([])

const form = reactive({
  title: '',
  projectDesc: '',
  techRequirements: '',
  deliveryStandard: '',
  deliveryDeadline: null,
  breachClause: '',
  confidentialClause: '',
  ipClause: '',
  disputeClause: ''
})

// 默认条款
const defaultClauses = {
  breach: '1. 甲方逾期付款超过3日，需支付应付款项0.1%/日的违约金\n2. 乙方逾期交付超过7日，甲方有权解除合同并要求退款\n3. 任一方违约导致合同解除，违约方承担对方实际损失',
  confidential: '1. 双方对项目相关技术资料、商业信息负有保密义务\n2. 保密期限为合同终止后2年\n3. 违反保密义务需承担违约责任',
  ip: '1. 乙方交付成果的知识产权归甲方所有\n2. 乙方保证交付物不侵犯第三方知识产权\n3. 如涉及第三方开源组件，乙方需提前说明',
  dispute: '1. 本合同争议首先通过平台调解解决\n2. 调解不成的，任一方可向合同签订地人民法院提起诉讼'
}

const loadData = async () => {
  loading.value = true
  try {
    // 加载订单信息
    const orderRes = await getOrderDetail(route.params.id)
    const orderData = orderRes.data.order || orderRes.data
    Object.assign(order, orderData)
    stages.value = orderRes.data.stages || []

    // 尝试加载已有合同
    const contractRes = await getContractByOrder(route.params.id)
    if (contractRes.data) {
      Object.assign(contract, contractRes.data)
      // 填充表单
      form.title = contractRes.data.title || ''
      form.projectDesc = contractRes.data.projectDesc || ''
      form.techRequirements = contractRes.data.techRequirements || ''
      form.deliveryStandard = contractRes.data.deliveryStandard || ''
      form.deliveryDeadline = contractRes.data.deliveryDeadline ? new Date(contractRes.data.deliveryDeadline).getTime() : null
      form.breachClause = contractRes.data.breachClause || defaultClauses.breach
      form.confidentialClause = contractRes.data.confidentialClause || defaultClauses.confidential
      form.ipClause = contractRes.data.ipClause || defaultClauses.ip
      form.disputeClause = contractRes.data.disputeClause || defaultClauses.dispute
    } else {
      // 新建合同，使用订单信息和默认条款
      form.title = order.title + ' - 项目合同'
      form.projectDesc = order.description || ''
      form.techRequirements = order.techStack || ''
      form.deliveryDeadline = order.deliveryTime ? new Date(order.deliveryTime).getTime() : null
      form.breachClause = defaultClauses.breach
      form.confidentialClause = defaultClauses.confidential
      form.ipClause = defaultClauses.ip
      form.disputeClause = defaultClauses.dispute
    }
  } catch (e) {
    message.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const saveContract = async () => {
  saving.value = true
  try {
    const data = {
      orderId: route.params.id,
      ...form,
      deliveryDeadline: form.deliveryDeadline ? new Date(form.deliveryDeadline).toISOString().split('T')[0] : null
    }
    const res = await createOrUpdateContract(data)
    if (res.data) {
      contract.id = res.data.id
    }
    message.success('保存成功')
  } catch (e) {
    message.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const submitContract = async () => {
  // 验证必填字段
  if (!form.title) {
    message.warning('请填写合同标题')
    return
  }
  if (!form.projectDesc) {
    message.warning('请填写项目描述')
    return
  }
  if (!form.deliveryStandard) {
    message.warning('请填写验收标准')
    return
  }

  submitting.value = true
  try {
    // 先保存合同
    const saveData = {
      orderId: route.params.id,
      ...form,
      deliveryDeadline: form.deliveryDeadline ? new Date(form.deliveryDeadline).toISOString().split('T')[0] : null
    }
    const saveRes = await createOrUpdateContract(saveData)
    const contractId = saveRes.data?.id || contract.id

    if (!contractId) {
      throw new Error('合同保存失败')
    }

    // 提交合同
    await apiSubmitContract(contractId)
    message.success('合同已提交，请进行签署')
    
    // 跳转到签署页面
    router.push(`/merchant/orders/${route.params.id}/contract/sign`)
  } catch (e) {
    message.error(e.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
