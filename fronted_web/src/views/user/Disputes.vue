<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <h2 class="text-xl font-black text-gray-800">纠纷裁决中心</h2>
      <n-button type="error" ghost @click="showCreateDispute = true">
        <template #icon><i class="fas fa-plus"></i></template>
        发起申诉
      </n-button>
    </div>

    <!-- 纠纷统计 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="bg-white p-5 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">全部纠纷</p>
        <p class="text-2xl font-black text-gray-800 mt-1">{{ stats.total }}</p>
      </div>
      <div class="bg-white p-5 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">处理中</p>
        <p class="text-2xl font-black text-orange-500 mt-1">{{ stats.processing }}</p>
      </div>
      <div class="bg-white p-5 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">已裁决</p>
        <p class="text-2xl font-black text-green-500 mt-1">{{ stats.resolved }}</p>
      </div>
      <div class="bg-white p-5 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">胜诉率</p>
        <p class="text-2xl font-black text-tf mt-1">{{ stats.winRate }}%</p>
      </div>
    </div>

    <!-- 纠纷列表 -->
    <div class="bg-white rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
      <div v-if="disputes.length === 0" class="p-20 text-center">
        <div class="w-20 h-20 bg-green-50 rounded-full flex items-center justify-center mx-auto mb-4">
          <i class="fas fa-check-circle text-3xl text-green-400"></i>
        </div>
        <p class="text-gray-500 font-bold">暂无纠纷记录</p>
        <p class="text-gray-400 text-sm mt-2">您的所有项目都顺利完成，继续保持！</p>
      </div>

      <div v-else class="divide-y divide-gray-50">
        <div 
          v-for="dispute in disputes" 
          :key="dispute.id"
          class="p-6 hover:bg-slate-50 transition cursor-pointer"
          @click="viewDispute(dispute)"
        >
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-5">
              <div 
                class="w-12 h-12 rounded-xl flex items-center justify-center"
                :class="getDisputeIconClass(dispute.status)"
              >
                <i :class="getDisputeIcon(dispute.status)"></i>
              </div>
              <div>
                <div class="flex items-center gap-3">
                  <h4 class="font-bold text-gray-800">案件 #{{ dispute.id }}</h4>
                  <span 
                    class="px-2 py-0.5 rounded text-xs font-bold"
                    :class="getDisputeStatusClass(dispute.status)"
                  >
                    {{ getDisputeStatusText(dispute.status) }}
                  </span>
                </div>
                <p class="text-sm text-gray-500 mt-1">{{ dispute.reason }}</p>
                <div class="flex gap-4 mt-1 text-xs text-gray-400">
                  <span>关联订单: {{ dispute.orderNo }}</span>
                  <span>发起时间: {{ dispute.createTime }}</span>
                </div>
              </div>
            </div>
            <div class="flex items-center gap-4">
              <div class="text-right">
                <p class="text-xs text-gray-400">涉及金额</p>
                <p class="text-lg font-black text-gray-800">¥ {{ dispute.amount.toLocaleString() }}</p>
              </div>
              <i class="fas fa-chevron-right text-gray-300"></i>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 发起申诉弹窗 -->
    <n-modal v-model:show="showCreateDispute" preset="card" title="发起纠纷申诉" class="max-w-2xl">
      <div class="space-y-6">
        <n-alert type="warning" class="mb-4">
          <template #icon><i class="fas fa-exclamation-triangle"></i></template>
          请谨慎使用申诉功能，恶意申诉将影响您的信用评分
        </n-alert>

        <n-form ref="disputeFormRef" :model="disputeForm" :rules="disputeRules" label-placement="top">
          <n-form-item label="选择关联订单" path="orderId">
            <n-select 
              v-model:value="disputeForm.orderId"
              :options="orderOptions"
              placeholder="请选择要申诉的订单"
            />
          </n-form-item>

          <n-form-item label="申诉原因" path="reason">
            <n-select 
              v-model:value="disputeForm.reason"
              :options="reasonOptions"
              placeholder="请选择申诉原因"
            />
          </n-form-item>

          <n-form-item label="详细描述" path="description">
            <n-input 
              v-model:value="disputeForm.description"
              type="textarea"
              placeholder="请详细描述您遇到的问题，包括时间、经过、诉求等..."
              :rows="4"
            />
          </n-form-item>

          <n-form-item label="上传证据">
            <n-upload
              multiple
              :max="5"
              accept=".jpg,.jpeg,.png,.pdf,.doc,.docx"
              list-type="image-card"
            >
              <n-button>点击上传</n-button>
            </n-upload>
            <p class="text-xs text-gray-400 mt-2">支持图片、PDF、Word文档，最多5个文件</p>
          </n-form-item>
        </n-form>

        <div class="flex gap-3 justify-end">
          <n-button @click="showCreateDispute = false">取消</n-button>
          <n-button type="error" @click="submitDispute">提交申诉</n-button>
        </div>
      </div>
    </n-modal>

    <!-- 纠纷详情弹窗 -->
    <n-modal v-model:show="showDisputeDetail" preset="card" :title="`案件 #${currentDispute?.id}`" class="max-w-3xl">
      <div v-if="currentDispute" class="space-y-6">
        <!-- 状态提示 -->
        <div 
          class="p-4 rounded-xl flex gap-3"
          :class="currentDispute.status === 0 ? 'bg-red-50 border border-red-100' : 'bg-blue-50 border border-blue-100'"
        >
          <i 
            class="mt-1"
            :class="currentDispute.status === 0 ? 'fas fa-exclamation-triangle text-red-500' : 'fas fa-info-circle text-tf'"
          ></i>
          <div>
            <p class="font-bold" :class="currentDispute.status === 0 ? 'text-red-600' : 'text-tf'">
              {{ currentDispute.status === 0 ? '仲裁申请中，请在24小时内补充完整证据' : '案件正在处理中' }}
            </p>
            <p class="text-xs text-gray-500 mt-1">平台调查员已介入，预计3个工作日内给出裁决结果</p>
          </div>
        </div>

        <!-- 纠纷信息 -->
        <div class="grid grid-cols-2 gap-6">
          <div>
            <p class="text-xs text-gray-400 font-bold uppercase mb-1">申诉原因</p>
            <p class="font-bold text-gray-800">{{ currentDispute.reason }}</p>
          </div>
          <div>
            <p class="text-xs text-gray-400 font-bold uppercase mb-1">涉及金额</p>
            <p class="font-bold text-gray-800">¥ {{ currentDispute.amount.toLocaleString() }}</p>
          </div>
        </div>

        <div>
          <p class="text-xs text-gray-400 font-bold uppercase mb-2">问题描述</p>
          <p class="text-gray-600 p-4 bg-gray-50 rounded-xl">{{ currentDispute.description }}</p>
        </div>

        <!-- 证据材料 -->
        <div>
          <p class="text-xs text-gray-400 font-bold uppercase mb-2">证据材料</p>
          <div class="grid grid-cols-4 gap-3">
            <div 
              v-for="i in 3" 
              :key="i"
              class="aspect-square bg-gray-100 rounded-xl flex items-center justify-center"
            >
              <i class="fas fa-image text-gray-300 text-xl"></i>
            </div>
            <div class="aspect-square border-2 border-dashed border-gray-200 rounded-xl flex items-center justify-center cursor-pointer hover:border-tf transition">
              <i class="fas fa-plus text-gray-300"></i>
            </div>
          </div>
        </div>

        <!-- 裁决结果 -->
        <div v-if="currentDispute.status === 2 && currentDispute.result" class="p-4 bg-green-50 rounded-xl border border-green-100">
          <p class="text-xs text-gray-400 font-bold uppercase mb-2">裁决结果</p>
          <p class="text-green-700 font-bold">{{ currentDispute.result }}</p>
        </div>
      </div>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useMessage } from 'naive-ui'

const message = useMessage()
const showCreateDispute = ref(false)
const showDisputeDetail = ref(false)
const currentDispute = ref(null)
const disputeFormRef = ref(null)

const disputeForm = reactive({
  orderId: null,
  reason: null,
  description: ''
})

const disputeRules = {
  orderId: { required: true, message: '请选择订单', trigger: 'change' },
  reason: { required: true, message: '请选择申诉原因', trigger: 'change' },
  description: { required: true, message: '请填写详细描述', trigger: 'blur' }
}

const orderOptions = [
  { label: 'TF202401001 - 官方管理系统UI设计', value: 1 },
  { label: 'TF202401002 - 跨境支付组件库开发', value: 2 }
]

const reasonOptions = [
  { label: '服务商未按时交付', value: '服务商未按时交付' },
  { label: '交付质量不符合要求', value: '交付质量不符合要求' },
  { label: '服务商失联', value: '服务商失联' },
  { label: '服务商拒绝修改', value: '服务商拒绝修改' },
  { label: '其他原因', value: '其他原因' }
]

// 模拟数据
const disputes = ref([
  // 示例数据，实际为空时显示空状态
])

const stats = computed(() => ({
  total: disputes.value.length,
  processing: disputes.value.filter(d => d.status === 0 || d.status === 1).length,
  resolved: disputes.value.filter(d => d.status === 2).length,
  winRate: disputes.value.length > 0 ? Math.round((disputes.value.filter(d => d.status === 2).length / disputes.value.length) * 100) : 100
}))

const getDisputeStatusText = (status) => {
  const map = { 0: '待处理', 1: '处理中', 2: '已裁决', 3: '已驳回' }
  return map[status] || '未知'
}

const getDisputeStatusClass = (status) => {
  const map = {
    0: 'bg-red-50 text-red-500',
    1: 'bg-orange-50 text-orange-500',
    2: 'bg-green-50 text-green-500',
    3: 'bg-gray-100 text-gray-500'
  }
  return map[status] || 'bg-gray-100 text-gray-500'
}

const getDisputeIcon = (status) => {
  const map = {
    0: 'fas fa-exclamation-triangle',
    1: 'fas fa-spinner fa-spin',
    2: 'fas fa-check-circle',
    3: 'fas fa-times-circle'
  }
  return map[status] || 'fas fa-question-circle'
}

const getDisputeIconClass = (status) => {
  const map = {
    0: 'bg-red-50 text-red-500',
    1: 'bg-orange-50 text-orange-500',
    2: 'bg-green-50 text-green-500',
    3: 'bg-gray-100 text-gray-500'
  }
  return map[status] || 'bg-gray-100 text-gray-500'
}

const viewDispute = (dispute) => {
  currentDispute.value = dispute
  showDisputeDetail.value = true
}

const submitDispute = () => {
  disputeFormRef.value?.validate((errors) => {
    if (!errors) {
      // TODO: 调用API提交纠纷
      message.success('申诉已提交，请等待平台处理')
      showCreateDispute.value = false
    }
  })
}
</script>
