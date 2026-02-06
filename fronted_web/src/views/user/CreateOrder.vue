<template>
  <div class="max-w-4xl mx-auto">
    <!-- 页面标题 -->
    <div class="flex items-center gap-4 mb-8">
      <button @click="router.back()" class="w-10 h-10 bg-white rounded-full flex items-center justify-center text-gray-400 hover:text-tf hover:shadow-md transition">
        <i class="fas fa-arrow-left"></i>
      </button>
      <div>
        <h2 class="text-2xl font-black text-gray-800">发起信托托管任务</h2>
        <p class="text-gray-400 text-sm mt-1">设定明确的交付阶段，保障资金安全拨付</p>
      </div>
    </div>

    <div class="space-y-8">
      <!-- 项目基本信息 -->
      <div class="bg-white rounded-3xl p-8 shadow-sm border border-gray-100">
        <h3 class="text-xs font-black uppercase tracking-widest mb-6" style="color: #00AFE1;">1. 项目基本需求</h3>
        
        <n-form ref="formRef" :model="formData" :rules="rules" label-placement="top">
          <n-form-item label="项目名称" path="title">
            <n-input 
              v-model:value="formData.title" 
              placeholder="例如：跨境支付系统前端组件库开发"
              size="large"
            />
          </n-form-item>

          <div class="grid grid-cols-2 gap-6">
            <n-form-item label="项目分类" path="categoryId">
              <n-cascader 
                v-model:value="formData.categoryId" 
                :options="categoryOptions"
                placeholder="选择项目类型"
                :check-strategy="'child'"
                :show-path="true"
                filterable
                clearable
              />
            </n-form-item>
            <n-form-item label="技术要求" path="techStack">
              <n-input 
                v-model:value="formData.techStack" 
                placeholder="例如：Vue3, TypeScript, Node.js"
              />
            </n-form-item>
          </div>
          
          <n-form-item label="需求简述" path="description">
            <n-input 
              v-model:value="formData.description" 
              type="textarea"
              placeholder="简要描述项目背景和核心需求..."
              :rows="3"
            />
          </n-form-item>

          <n-form-item label="功能描述 / 详细需求" path="features">
            <n-input 
              v-model:value="formData.features" 
              type="textarea"
              placeholder="详细描述功能模块、交付标准、技术规格等...&#10;&#10;例如：&#10;1. 用户登录注册模块（支持手机号/微信登录）&#10;2. 商品列表展示（分页、筛选、搜索）&#10;3. 购物车功能&#10;4. 订单管理&#10;..."
              :rows="6"
            />
          </n-form-item>

          <n-form-item label="开发文档 / 参考资料">
            <div class="w-full">
              <!-- 已上传文件列表 -->
              <div v-if="uploadedFiles.length > 0" class="mb-4 space-y-2">
                <div 
                  v-for="(file, index) in uploadedFiles" 
                  :key="index"
                  class="flex items-center justify-between p-3 bg-slate-50 rounded-lg border border-gray-200"
                >
                  <div class="flex items-center gap-3">
                    <i :class="getFileIcon(file.name)" class="text-lg text-gray-500"></i>
                    <div>
                      <p class="text-sm text-gray-700 font-medium truncate max-w-xs">{{ file.name }}</p>
                      <a :href="file.url" target="_blank" class="text-xs text-tf hover:underline">查看文件</a>
                    </div>
                  </div>
                  <n-button size="small" type="error" ghost @click="removeFile(index)">
                    <i class="fas fa-times"></i>
                  </n-button>
                </div>
              </div>

              <!-- 上传区域 -->
              <n-upload
                :custom-request="handleUpload"
                :show-file-list="false"
                multiple
                accept=".jpg,.jpeg,.png,.gif,.pdf,.doc,.docx,.xls,.xlsx,.txt,.zip,.rar"
              >
                <n-button dashed block :loading="uploading">
                  <template #icon><i class="fas fa-cloud-upload-alt mr-2"></i></template>
                  {{ uploading ? '上传中...' : '点击上传文件' }}
                </n-button>
              </n-upload>

              <p class="text-xs text-gray-400 mt-2">
                <i class="fas fa-info-circle mr-1"></i>
                支持上传图片、PDF、Word、Excel、ZIP等文件，单文件最大50MB
              </p>
            </div>
          </n-form-item>

          <div class="grid grid-cols-2 gap-6">
            <n-form-item label="开始时间" path="startTime">
              <n-date-picker v-model:value="formData.startTime" type="date" class="w-full" />
            </n-form-item>
            <n-form-item label="交付日期" path="deliveryTime">
              <n-date-picker v-model:value="formData.deliveryTime" type="date" class="w-full" />
            </n-form-item>
          </div>
        </n-form>
      </div>

      <!-- 资金托管计划 -->
      <div class="bg-white rounded-3xl p-8 shadow-sm border border-gray-100">
        <h3 class="text-xs font-black uppercase tracking-widest mb-6" style="color: #00AFE1;">2. 资金托管计划（按阶段拨付）</h3>
        
        <!-- 预设方案选择 -->
        <div class="mb-6">
          <p class="text-sm text-gray-600 mb-3">选择付款方案：</p>
          <div class="flex gap-3 flex-wrap">
            <button 
              v-for="plan in paymentPlans" 
              :key="plan.id"
              @click="selectPlan(plan)"
              class="px-4 py-2 rounded-xl border-2 text-sm font-bold transition"
              :class="selectedPlanId === plan.id ? 'border-tf bg-tf/10 text-tf' : 'border-gray-200 text-gray-600 hover:border-tf/50'"
              :style="selectedPlanId === plan.id ? { borderColor: '#00AFE1', background: 'rgba(0,175,225,0.1)', color: '#00AFE1' } : {}"
            >
              {{ plan.name }}
            </button>
          </div>
        </div>

        <!-- 项目总预算 -->
        <div class="mb-6">
          <n-form-item label="项目总预算" required>
            <n-input-number 
              v-model:value="totalBudget" 
              :min="100"
              :precision="2"
              class="w-full"
              size="large"
              @update:value="recalculateStages"
            >
              <template #prefix>¥</template>
            </n-input-number>
          </n-form-item>
        </div>
        
        <!-- 阶段详情 -->
        <div class="space-y-4">
          <div 
            v-for="(stage, index) in formData.stages" 
            :key="index" 
            class="p-5 rounded-2xl border-2 transition"
            :class="stage.status === 'current' ? 'border-tf bg-tf/5' : 'border-gray-100 bg-slate-50'"
            :style="stage.status === 'current' ? { borderColor: '#00AFE1', background: 'rgba(0,175,225,0.05)' } : {}"
          >
            <div class="flex items-start gap-4">
              <!-- 阶段序号 -->
              <div 
                class="w-10 h-10 rounded-full flex items-center justify-center text-white font-bold shrink-0"
                :style="{ background: getStageColor(index) }"
              >
                {{ index + 1 }}
              </div>
              
              <!-- 阶段信息 -->
              <div class="flex-1 space-y-3">
                <div class="flex items-center gap-4">
                  <n-input 
                    v-model:value="stage.name" 
                    placeholder="阶段名称"
                    class="flex-1"
                  />
                  <div class="flex items-center gap-3">
                    <n-input-number 
                      v-model:value="stage.percent" 
                      :min="5"
                      :max="100"
                      :step="5"
                      :show-button="false"
                      style="width: 80px;"
                      @update:value="updateStageAmount(index)"
                    >
                      <template #suffix>%</template>
                    </n-input-number>
                    <span class="text-lg font-black text-gray-800 whitespace-nowrap" style="min-width: 100px; text-align: right;">
                      ¥ {{ stage.amount.toLocaleString() }}
                    </span>
                  </div>
                </div>
                
                <!-- 付款节点 -->
                <div class="flex items-center gap-2">
                  <span class="text-xs text-gray-400 w-16">付款节点:</span>
                  <n-input 
                    v-model:value="stage.milestone" 
                    placeholder="如：合同签订当日 / MVP验收通过 / 上线后15天"
                    size="small"
                    class="flex-1"
                  />
                </div>
                
                <!-- 风险说明 -->
                <div class="flex items-start gap-2">
                  <span class="text-xs text-gray-400 w-16 mt-1">风险止损:</span>
                  <n-input 
                    v-model:value="stage.riskNote" 
                    type="textarea"
                    placeholder="如：客户跑路可覆盖30%人力成本 / 客户拖延可暂停源码交付"
                    size="small"
                    :rows="1"
                    class="flex-1"
                  />
                </div>
              </div>
              
              <!-- 删除按钮 -->
              <button 
                v-if="formData.stages.length > 2"
                @click="removeStage(index)" 
                class="w-8 h-8 text-red-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition mt-1"
              >
                <i class="fas fa-trash-alt"></i>
              </button>
            </div>
          </div>
        </div>
        
        <!-- 添加阶段 -->
        <button 
          v-if="formData.stages.length < 5"
          @click="addStage"
          class="w-full mt-4 py-3 text-xs font-bold border rounded-xl hover:bg-tf/5 transition"
          style="color: #00AFE1; border-color: rgba(0,175,225,0.2);"
        >
          + 添加结算节点
        </button>

        <!-- 比例校验提示 -->
        <div v-if="totalPercent !== 100" class="mt-4 p-3 bg-red-50 rounded-lg border border-red-200">
          <p class="text-xs text-red-600">
            <i class="fas fa-exclamation-triangle mr-1"></i>
            当前比例合计 {{ totalPercent }}%，需等于 100%
          </p>
        </div>

        <!-- 方案说明 -->
        <div class="mt-6 p-4 bg-blue-50 rounded-xl border border-blue-100">
          <p class="text-xs text-blue-700 font-bold mb-2"><i class="fas fa-lightbulb mr-1"></i>分期付款方案说明</p>
          <ul class="text-xs text-blue-600 space-y-1">
            <li>• <strong>352方案</strong>：30%定金 + 50%中期款 + 20%尾款，适合大型项目，风险分摊合理</li>
            <li>• <strong>37方案</strong>：30%定金 + 70%尾款，适合中小型项目，流程简单</li>
            <li>• <strong>442方案</strong>：40%定金 + 40%中期款 + 20%尾款，定金比例较高，降低开发风险</li>
          </ul>
        </div>
      </div>

      <!-- 托管总金额 -->
      <div class="p-8 rounded-3xl text-white flex justify-between items-center shadow-xl" style="background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%); box-shadow: 0 25px 50px -12px rgba(0,175,225,0.3);">
        <div>
          <p class="text-white/60 text-xs font-bold uppercase mb-1">托管总金额</p>
          <h3 class="text-3xl font-black">¥ {{ totalAmount.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }}</h3>
        </div>
        <n-button 
          size="large" 
          class="font-black px-8"
          @click="handleSubmit"
          :loading="submitting"
        >
          发布任务
        </n-button>
      </div>

      <!-- 安全提示 -->
      <div class="p-4 bg-yellow-50 rounded-2xl border border-yellow-100 flex gap-3">
        <i class="fas fa-shield-alt text-yellow-600 mt-1"></i>
        <p class="text-xs text-yellow-700 leading-relaxed">
          提示：任务发布后等待商家接单。商家接单后，您需要进行资金托管，订单才正式开始执行。平台将对托管资金进行24小时全程监管，商家完成交付并经您验收后，资金才会释放。
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { createOrder } from '@/api/order'
import { uploadFile } from '@/api/file'
import { getBusinessCategories } from '@/api/auth'

const router = useRouter()
const message = useMessage()
const formRef = ref(null)
const submitting = ref(false)
const uploading = ref(false)
const uploadedFiles = ref([]) // { name, url }

// 分类选项（树形结构，用于级联选择器）
const categoryOptions = ref([])

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
    // 使用默认分类
    categoryOptions.value = [
      { label: 'UI设计', value: 201 },
      { label: '前端开发', value: 102 },
      { label: '后端开发', value: 101 },
      { label: '小程序开发', value: 104 },
      { label: '其他', value: 5 }
    ]
  }
}

// 预设付款方案（新流程：首付款+尾款+质保款）
const paymentPlans = [
  {
    id: '352',
    name: '352方案 (推荐)',
    stages: [
      { name: '首付款（启动款）', percent: 30, milestone: '合同签订后托管', riskNote: '覆盖30%人力成本，确保项目启动', stageType: 1 },
      { name: '尾款（验收款）', percent: 50, milestone: '项目验收通过后支付', riskNote: '验收通过后立即释放给商家', stageType: 2 },
      { name: '质保款', percent: 20, milestone: '质保期15天后释放', riskNote: '预留20%作为15天免费维护保障', stageType: 3 }
    ]
  },
  {
    id: '37',
    name: '37方案（无质保）',
    stages: [
      { name: '首付款（启动款）', percent: 30, milestone: '合同签订后托管', riskNote: '覆盖30%人力成本，确保项目启动', stageType: 1 },
      { name: '尾款（验收款）', percent: 70, milestone: '项目验收通过后支付', riskNote: '验收通过后一次性结清', stageType: 2 }
    ]
  },
  {
    id: '442',
    name: '442方案',
    stages: [
      { name: '首付款（启动款）', percent: 40, milestone: '合同签订后托管', riskNote: '较高首付降低双方风险', stageType: 1 },
      { name: '尾款（验收款）', percent: 40, milestone: '项目验收通过后支付', riskNote: '验收通过后支付', stageType: 2 },
      { name: '质保款', percent: 20, milestone: '质保期15天后释放', riskNote: '预留20%作为15天免费维护保障', stageType: 3 }
    ]
  },
  {
    id: 'custom',
    name: '自定义',
    stages: [
      { name: '首付款', percent: 50, milestone: '合同签订后托管', riskNote: '', stageType: 1 },
      { name: '尾款', percent: 50, milestone: '项目验收通过后支付', riskNote: '', stageType: 2 }
    ]
  }
]

const selectedPlanId = ref('352')
const totalBudget = ref(10000)

const formData = reactive({
  title: '',
  categoryId: null,
  techStack: '',
  description: '',
  features: '',
  startTime: null,
  deliveryTime: null,
  stages: JSON.parse(JSON.stringify(paymentPlans[0].stages.map(s => ({
    ...s,
    amount: Math.round(totalBudget.value * s.percent / 100)
  }))))
})

const rules = {
  title: { required: true, message: '请输入项目名称', trigger: 'blur' },
  categoryId: { type: 'number', required: true, message: '请选择项目分类', trigger: 'change' },
  description: { required: true, message: '请输入需求简述', trigger: 'blur' },
  startTime: { type: 'number', required: true, message: '请选择开始时间', trigger: 'change' },
  deliveryTime: { type: 'number', required: true, message: '请选择交付日期', trigger: 'change' }
}

const totalAmount = computed(() => {
  return formData.stages.reduce((sum, stage) => sum + (stage.amount || 0), 0)
})

const totalPercent = computed(() => {
  return formData.stages.reduce((sum, stage) => sum + (stage.percent || 0), 0)
})

// 选择预设方案
const selectPlan = (plan) => {
  selectedPlanId.value = plan.id
  formData.stages = JSON.parse(JSON.stringify(plan.stages.map(s => ({
    ...s,
    amount: Math.round(totalBudget.value * s.percent / 100)
  }))))
}

// 重新计算阶段金额
const recalculateStages = () => {
  formData.stages.forEach(stage => {
    stage.amount = Math.round(totalBudget.value * stage.percent / 100)
  })
}

// 更新单个阶段金额
const updateStageAmount = (index) => {
  formData.stages[index].amount = Math.round(totalBudget.value * formData.stages[index].percent / 100)
}

// 获取阶段颜色
const getStageColor = (index) => {
  const colors = ['#00AFE1', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6']
  return colors[index % colors.length]
}

const addStage = () => {
  const remainingPercent = 100 - totalPercent.value
  formData.stages.push({ 
    name: `阶段${formData.stages.length + 1}`, 
    percent: Math.max(5, remainingPercent),
    amount: Math.round(totalBudget.value * Math.max(5, remainingPercent) / 100),
    milestone: '',
    riskNote: ''
  })
  selectedPlanId.value = 'custom'
}

const removeStage = (index) => {
  formData.stages.splice(index, 1)
  selectedPlanId.value = 'custom'
}

// 文件上传处理
const handleUpload = async ({ file }) => {
  uploading.value = true
  try {
    const res = await uploadFile(file.file, 'docs')
    if (res.data) {
      uploadedFiles.value.push({
        name: res.data.name || file.name,
        url: res.data.url
      })
      message.success('文件上传成功')
    }
  } catch (e) {
    message.error(e.message || '文件上传失败')
  } finally {
    uploading.value = false
  }
}

// 删除已上传文件
const removeFile = (index) => {
  uploadedFiles.value.splice(index, 1)
}

// 获取文件图标
const getFileIcon = (filename) => {
  if (!filename) return 'fas fa-file'
  const ext = filename.split('.').pop().toLowerCase()
  const iconMap = {
    pdf: 'fas fa-file-pdf text-red-500',
    doc: 'fas fa-file-word text-blue-500',
    docx: 'fas fa-file-word text-blue-500',
    xls: 'fas fa-file-excel text-green-500',
    xlsx: 'fas fa-file-excel text-green-500',
    txt: 'fas fa-file-alt text-gray-500',
    zip: 'fas fa-file-archive text-yellow-500',
    rar: 'fas fa-file-archive text-yellow-500',
    jpg: 'fas fa-file-image text-purple-500',
    jpeg: 'fas fa-file-image text-purple-500',
    png: 'fas fa-file-image text-purple-500',
    gif: 'fas fa-file-image text-purple-500'
  }
  return iconMap[ext] || 'fas fa-file text-gray-500'
}

// 格式化日期为 YYYY-MM-DD
const formatDate = (timestamp) => {
  if (!timestamp) return null
  const date = new Date(timestamp)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const handleSubmit = () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      // 校验阶段信息
      if (formData.stages.some(s => !s.name || !s.amount)) {
        message.warning('请完善所有阶段信息')
        return
      }
      
      // 校验比例合计
      if (totalPercent.value !== 100) {
        message.warning(`付款比例合计需等于100%，当前为${totalPercent.value}%`)
        return
      }

      // 校验总预算
      if (!totalBudget.value || totalBudget.value < 100) {
        message.warning('请设置项目总预算（最低100元）')
        return
      }
      
      submitting.value = true
      try {
        const submitData = {
          title: formData.title,
          categoryId: formData.categoryId,
          techStack: formData.techStack,
          description: formData.description,
          features: formData.features,
          docUrls: uploadedFiles.value.map(f => f.url),
          startTime: formatDate(formData.startTime),
          deliveryTime: formatDate(formData.deliveryTime),
          stages: formData.stages.map(s => ({
            name: s.name,
            amount: s.amount,
            percent: s.percent,
            milestone: s.milestone,
            riskNote: s.riskNote,
            stageType: s.stageType || 2 // 1首付款, 2尾款, 3质保款
          }))
        }
        
        const res = await createOrder(submitData)
        // 订单创建成功，直接发布
        // 新流程：发布 → 商家接单 → 签合同 → 托管付款
        message.success('任务发布成功！等待商家接单')
        router.push(`/user/orders/${res.data.id}`)
      } catch (e) {
        message.error(e.message || '发布失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  loadCategories()
})
</script>
