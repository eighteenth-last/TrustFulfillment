<template>
  <div class="max-w-5xl mx-auto">
    <!-- 页面标题 -->
    <div class="flex items-center gap-4 mb-8">
      <button @click="router.back()" class="w-10 h-10 bg-slate-700 rounded-full flex items-center justify-center text-gray-400 hover:text-tf hover:bg-slate-600 transition">
        <i class="fas fa-arrow-left"></i>
      </button>
      <div>
        <h2 class="text-xl font-black text-white">节点交付申请</h2>
        <p class="text-gray-400 text-sm mt-1">订单: {{ order.orderNo }}</p>
      </div>
    </div>

    <div class="flex gap-8">
      <!-- 左侧表单 -->
      <div class="flex-1 space-y-6">
        <!-- 订单信息 -->
        <div class="bg-slate-800 p-6 rounded-3xl border border-slate-700">
          <h3 class="font-bold text-white mb-4 flex items-center gap-2">
            <i class="fas fa-file-contract text-tf"></i>
            订单信息
          </h3>
          <div class="grid grid-cols-2 gap-4 text-sm">
            <div>
              <p class="text-gray-400">项目名称</p>
              <p class="text-white font-bold mt-1">{{ order.title }}</p>
            </div>
            <div>
              <p class="text-gray-400">甲方</p>
              <p class="text-white font-bold mt-1">{{ order.buyerName }}</p>
            </div>
            <div>
              <p class="text-gray-400">当前阶段</p>
              <p class="text-tf font-bold mt-1">{{ currentStage.stageName }}</p>
            </div>
            <div>
              <p class="text-gray-400">预计交付日期</p>
              <p class="text-white font-bold mt-1">{{ order.deliveryTime }}</p>
            </div>
          </div>
        </div>

        <!-- 上传交付物 -->
        <div class="bg-slate-800 p-6 rounded-3xl border border-slate-700">
          <h3 class="font-bold text-white mb-6 flex items-center gap-2">
            <i class="fas fa-cloud-upload-alt text-tf"></i>
            上传交付产物
            <span class="text-xs text-gray-400 font-normal ml-2">文件将自动进行司法哈希存证</span>
          </h3>

          <n-upload
            multiple
            directory-dnd
            :max="10"
            @change="handleFileChange"
            class="mb-6"
          >
            <div class="border-4 border-dashed border-slate-600 rounded-3xl p-16 text-center hover:border-tf/50 transition cursor-pointer bg-slate-700/20">
              <i class="fas fa-cloud-arrow-up text-5xl text-tf/30 mb-4"></i>
              <p class="font-bold text-gray-300 text-lg">点击选择或拖拽交付物到此处</p>
              <p class="text-xs text-gray-500 mt-2">支持全格式，最大 1GB，文件将进行上链处理</p>
            </div>
          </n-upload>

          <!-- 已上传文件列表 -->
          <div v-if="uploadedFiles.length > 0" class="space-y-3">
            <div 
              v-for="(file, index) in uploadedFiles" 
              :key="index"
              class="flex items-center justify-between p-4 bg-slate-700/50 rounded-xl"
            >
              <div class="flex items-center gap-3">
                <i :class="getFileIcon(file.name)" class="text-xl"></i>
                <div>
                  <p class="text-white text-sm font-medium">{{ file.name }}</p>
                  <p class="text-xs text-gray-400">{{ formatFileSize(file.size) }}</p>
                </div>
              </div>
              <button @click="removeFile(index)" class="text-gray-400 hover:text-red-400 transition">
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
        </div>

        <!-- 交付说明 -->
        <div class="bg-slate-800 p-6 rounded-3xl border border-slate-700">
          <h3 class="font-bold text-white mb-4">交付详情说明</h3>
          <n-input
            v-model:value="deliveryDesc"
            type="textarea"
            placeholder="描述本次交付的核心内容及待确认项..."
            :rows="5"
          />
        </div>
      </div>

      <!-- 右侧信息 -->
      <div class="w-80 space-y-6">
        <!-- 本次预拨付资金 -->
        <div class="bg-slate-800 p-6 rounded-3xl border border-slate-700">
          <h4 class="font-bold text-white mb-4">本次预拨付资金</h4>
          <div class="flex justify-between items-end mb-4">
            <span class="text-sm text-gray-400">{{ currentStage.stageName }}</span>
            <span class="text-2xl font-black text-tf">¥ {{ currentStage.amount?.toLocaleString() }}</span>
          </div>
          <div class="pt-4 border-t border-slate-600 space-y-3">
            <div class="flex gap-3 text-xs text-gray-400">
              <i class="fas fa-shield text-tf/50"></i>
              <span>提交后，资金将进入甲方验收期，最长 48 小时。</span>
            </div>
            <div class="flex gap-3 text-xs text-gray-400">
              <i class="fas fa-clock text-tf/50"></i>
              <span>甲方超时未验收，系统自动确认并释放资金。</span>
            </div>
          </div>
        </div>

        <!-- 提交按钮 -->
        <n-button 
          block 
          type="primary" 
          color="#00AFE1" 
          size="large" 
          class="font-bold h-14 text-lg"
          :loading="submitting"
          @click="handleSubmit"
        >
          正式提交交付
        </n-button>

        <!-- 阶段进度 -->
        <div class="bg-slate-800 p-6 rounded-3xl border border-slate-700">
          <h4 class="font-bold text-white mb-4">交付阶段</h4>
          <div class="relative pl-6">
            <div class="absolute left-2 top-2 bottom-2 w-0.5 bg-slate-600"></div>
            <div v-for="(stage, index) in order.stages" :key="index" class="relative mb-4 last:mb-0">
              <div 
                class="absolute -left-[17px] w-3 h-3 rounded-full border-2"
                :class="getStagePointClass(stage.status)"
              ></div>
              <div class="ml-2">
                <p class="text-sm font-bold" :class="stage.status === 3 ? 'text-gray-500' : 'text-white'">
                  {{ stage.stageName }}
                </p>
                <p class="text-xs text-gray-400">¥{{ stage.amount?.toLocaleString() }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'

const router = useRouter()
const route = useRoute()
const message = useMessage()

const submitting = ref(false)
const deliveryDesc = ref('')
const uploadedFiles = ref([])

// 模拟订单数据
const order = reactive({
  id: 1,
  orderNo: 'TF202401001',
  title: '官方管理系统 UI 设计',
  buyerName: '臻托项目组',
  deliveryTime: '2024-02-15',
  stages: [
    { id: 1, stageName: '项目启动金', amount: 2000, status: 3 },
    { id: 2, stageName: 'UI原型设计', amount: 5000, status: 1 },
    { id: 3, stageName: '全部交付', amount: 8000, status: 0 }
  ]
})

const currentStage = computed(() => {
  return order.stages.find(s => s.status === 1) || {}
})

const handleFileChange = ({ fileList }) => {
  uploadedFiles.value = fileList.map(f => f.file)
}

const removeFile = (index) => {
  uploadedFiles.value.splice(index, 1)
}

const getFileIcon = (filename) => {
  const ext = filename.split('.').pop().toLowerCase()
  const icons = {
    pdf: 'fas fa-file-pdf text-red-500',
    doc: 'fas fa-file-word text-blue-500',
    docx: 'fas fa-file-word text-blue-500',
    zip: 'fas fa-file-archive text-yellow-500',
    rar: 'fas fa-file-archive text-yellow-500',
    png: 'fas fa-file-image text-purple-500',
    jpg: 'fas fa-file-image text-purple-500',
    jpeg: 'fas fa-file-image text-purple-500',
    psd: 'fas fa-file-image text-blue-400',
    sketch: 'fas fa-file text-orange-500',
    fig: 'fas fa-file text-purple-400'
  }
  return icons[ext] || 'fas fa-file text-gray-400'
}

const formatFileSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

const getStagePointClass = (status) => {
  const map = {
    0: 'bg-slate-700 border-slate-500',
    1: 'bg-tf border-tf',
    2: 'bg-purple-500 border-purple-300',
    3: 'bg-green-500 border-green-300'
  }
  return map[status] || 'bg-slate-700 border-slate-500'
}

const handleSubmit = () => {
  if (uploadedFiles.value.length === 0) {
    message.warning('请上传交付物')
    return
  }
  if (!deliveryDesc.value) {
    message.warning('请填写交付说明')
    return
  }

  submitting.value = true
  // TODO: 调用API提交交付
  setTimeout(() => {
    message.success('交付已提交，等待甲方验收')
    router.push('/merchant/orders')
    submitting.value = false
  }, 1500)
}

onMounted(() => {
  console.log('Order ID:', route.params.id)
})
</script>
