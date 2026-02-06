<template>
  <div class="max-w-5xl mx-auto space-y-6">
    <!-- 加载中 -->
    <div v-if="loading" class="flex justify-center py-20">
      <n-spin size="large" />
    </div>

    <template v-else>
      <!-- 顶部导航 -->
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-4">
          <button @click="router.back()" class="w-10 h-10 bg-white rounded-full flex items-center justify-center text-gray-400 hover:text-tf hover:shadow-md transition">
            <i class="fas fa-arrow-left"></i>
          </button>
          <div>
            <h2 class="text-xl font-black text-gray-800">{{ order.title }}</h2>
            <p class="text-sm text-gray-400 mt-1">订单号: {{ order.orderNo }}</p>
          </div>
        </div>
        <div class="flex items-center gap-3">
          <span :class="getStatusClass(order.status)" class="px-4 py-1.5 rounded-full text-sm font-bold">
            {{ getStatusText(order.status) }}
          </span>
        </div>
      </div>

      <div class="grid grid-cols-3 gap-6">
        <!-- 左侧：订单信息和阶段进度 -->
        <div class="col-span-2 space-y-6">
          <!-- 订单信息卡片 -->
          <div class="bg-white rounded-3xl p-8 shadow-sm border border-gray-100">
            <h3 class="font-black text-gray-800 mb-6 flex items-center gap-2">
              <i class="fas fa-file-contract" style="color: #00AFE1;"></i>
              项目信息
            </h3>
            
            <div class="grid grid-cols-2 gap-6">
              <div>
                <p class="text-xs text-gray-400 font-bold uppercase">项目总金额</p>
                <p class="text-2xl font-black text-gray-800 mt-1">¥ {{ order.totalAmount?.toLocaleString() }}</p>
              </div>
              <div>
                <p class="text-xs text-gray-400 font-bold uppercase">待结算金额</p>
                <p class="text-2xl font-black mt-1" style="color: #00AFE1;">¥ {{ (order.depositAmount - (order.releasedAmount || 0))?.toLocaleString() }}</p>
              </div>
              <div>
                <p class="text-xs text-gray-400 font-bold uppercase">甲方</p>
                <p class="text-lg font-bold text-gray-800 mt-1">{{ order.buyerName || '用户' }}</p>
              </div>
              <div>
                <p class="text-xs text-gray-400 font-bold uppercase">预计交付日期</p>
                <p class="text-lg font-bold text-gray-800 mt-1">{{ formatDate(order.deliveryTime) || '-' }}</p>
              </div>
            </div>

            <!-- 技术栈和功能描述 -->
            <div v-if="order.techStack || order.features" class="mt-6 pt-6 border-t border-gray-100 space-y-4">
              <div v-if="order.techStack">
                <p class="text-xs text-gray-400 font-bold uppercase mb-2">技术栈要求</p>
                <p class="text-gray-600 text-sm">{{ order.techStack }}</p>
              </div>
              <div v-if="order.features">
                <p class="text-xs text-gray-400 font-bold uppercase mb-2">功能描述</p>
                <p class="text-gray-600 text-sm leading-relaxed whitespace-pre-line">{{ order.features }}</p>
              </div>
            </div>

            <div class="mt-6 pt-6 border-t border-gray-100">
              <p class="text-xs text-gray-400 font-bold uppercase mb-2">需求描述</p>
              <p class="text-gray-600 text-sm leading-relaxed">{{ order.description }}</p>
            </div>

            <!-- 开发文档 -->
            <div v-if="order.docUrls" class="mt-6 pt-6 border-t border-gray-100">
              <p class="text-xs text-gray-400 font-bold uppercase mb-2">开发文档/参考资料</p>
              <div class="flex flex-wrap gap-2">
                <a 
                  v-for="(url, idx) in parseDocUrls(order.docUrls)" 
                  :key="idx"
                  :href="url"
                  target="_blank"
                  class="inline-flex items-center gap-2 px-3 py-2 bg-blue-50 text-blue-600 rounded-lg text-sm hover:bg-blue-100 transition"
                >
                  <i class="fas fa-file-alt"></i>
                  {{ getFileName(url) }}
                </a>
              </div>
            </div>
          </div>

          <!-- 阶段进度 -->
          <div class="bg-white rounded-3xl p-8 shadow-sm border border-gray-100">
            <h3 class="font-black text-gray-800 mb-6 flex items-center gap-2">
              <i class="fas fa-tasks" style="color: #00AFE1;"></i>
              交付阶段
            </h3>

            <div class="space-y-6">
              <div 
                v-for="(stage, index) in order.stages" 
                :key="stage.id" 
                class="relative p-5 rounded-2xl border-2 transition"
                :class="getStageCardClass(stage.status)"
              >
                <!-- 阶段头部 -->
                <div class="flex items-start justify-between mb-4">
                  <div class="flex items-center gap-3">
                    <div 
                      class="w-10 h-10 rounded-full flex items-center justify-center text-white font-bold"
                      :style="{ background: getStageColor(stage.status) }"
                    >
                      <i v-if="stage.status === 3" class="fas fa-check"></i>
                      <span v-else>{{ index + 1 }}</span>
                    </div>
                    <div>
                      <h4 class="font-bold text-gray-800">{{ stage.stageName || stage.name }}</h4>
                      <div class="flex items-center gap-3 mt-1">
                        <span class="text-sm text-gray-500">¥ {{ stage.amount?.toLocaleString() }}</span>
                        <span v-if="stage.percent" class="text-xs px-2 py-0.5 rounded bg-gray-100 text-gray-500">
                          {{ stage.percent }}%
                        </span>
                      </div>
                    </div>
                  </div>
                  
                  <div class="flex items-center gap-2">
                    <span 
                      class="px-3 py-1 rounded-full text-xs font-bold"
                      :class="getStageStatusClass(stage.status)"
                    >
                      {{ getStageStatusText(stage.status) }}
                    </span>
                  </div>
                </div>

                <!-- 付款节点 -->
                <div v-if="stage.milestone" class="mb-3 flex items-center gap-2 text-sm text-gray-500">
                  <i class="fas fa-flag text-xs"></i>
                  <span>付款节点: {{ stage.milestone }}</span>
                </div>

                <!-- 交付说明（已提交） -->
                <div v-if="stage.deliveryDesc" class="mb-3 p-3 bg-slate-50 rounded-lg">
                  <p class="text-xs text-gray-400 mb-1">交付说明:</p>
                  <p class="text-sm text-gray-600 whitespace-pre-line">{{ stage.deliveryDesc }}</p>
                </div>

                <!-- 交付物附件 -->
                <div v-if="stage.evidenceUrl" class="mb-3 flex items-center gap-3 p-3 bg-blue-50 rounded-xl">
                  <i class="fas fa-paperclip" style="color: #00AFE1;"></i>
                  <span class="text-sm font-medium" style="color: #00AFE1;">交付物已上传</span>
                  <a :href="stage.evidenceUrl" target="_blank" class="text-xs underline" style="color: #00AFE1;">查看</a>
                  <span v-if="stage.evidenceHash" class="text-xs text-gray-400 ml-auto">
                    存证: {{ stage.evidenceHash.substring(0, 8) }}...
                  </span>
                </div>

                <!-- 操作按钮 -->
                <div class="mt-4 pt-4 border-t border-gray-100">
                  <!-- 未开始：等待甲方支付（订单状态7） -->
                  <span v-if="stage.status === 0 && order.status === 7 && canStartStage(index)" class="text-sm text-orange-500">
                    <i class="fas fa-hourglass-half mr-1"></i>等待甲方支付本阶段款项...
                  </span>

                  <!-- 未开始：开始按钮（订单状态2，款项已托管） -->
                  <n-button 
                    v-else-if="stage.status === 0 && order.status === 2 && canStartStage(index)"
                    type="primary" 
                    color="#00AFE1"
                    @click="handleStartStage(stage)"
                    :loading="startingStage === stage.id"
                  >
                    <template #icon><i class="fas fa-play"></i></template>
                    开始此阶段
                  </n-button>

                  <!-- 未开始但前置未完成 -->
                  <span v-else-if="stage.status === 0 && !canStartStage(index)" class="text-sm text-gray-400">
                    <i class="fas fa-lock mr-1"></i>请先完成前置阶段
                  </span>

                  <!-- 已托管：等待商家交付 -->
                  <span v-if="stage.status === 1" class="text-sm text-blue-500">
                    <i class="fas fa-check-circle mr-1"></i>款项已托管
                  </span>

                  <!-- 待验收 -->
                  <span v-if="stage.status === 2" class="text-sm text-purple-600">
                    <i class="fas fa-clock mr-1"></i>等待甲方验收中...
                  </span>

                  <!-- 已结算 -->
                  <div v-if="stage.status === 3" class="flex items-center gap-2 text-sm text-green-600">
                    <i class="fas fa-check-circle"></i>
                    <span>已结算</span>
                    <span v-if="stage.releaseTime" class="text-gray-400">{{ formatDateTime(stage.releaseTime) }}</span>
                  </div>

                  <!-- 质保中 -->
                  <div v-if="stage.status === 4" class="flex items-center gap-2 text-sm text-teal-600">
                    <i class="fas fa-shield-alt"></i>
                    <span>质保中</span>
                    <span v-if="stage.warrantyEndTime" class="text-gray-400">至 {{ formatDate(stage.warrantyEndTime) }}</span>
                  </div>

                  <!-- 质保已释放 -->
                  <div v-if="stage.status === 5" class="flex items-center gap-2 text-sm text-green-600">
                    <i class="fas fa-check-circle"></i>
                    <span>已释放</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 付款流程说明 -->
            <div class="mt-6 p-4 bg-gray-50 rounded-xl">
              <p class="text-xs text-gray-500 font-bold mb-2">付款流程</p>
              <div class="flex items-center gap-2 text-xs text-gray-400 flex-wrap">
                <span class="px-2 py-1 rounded bg-blue-100 text-blue-600">已托管</span>
                <span class="text-gray-300">→ 提交交付 →</span>
                <span class="px-2 py-1 rounded bg-purple-100 text-purple-600">待验收</span>
                <span class="text-gray-300">→ 甲方验收 →</span>
                <span class="px-2 py-1 rounded bg-green-100 text-green-600">已结算</span>
                <span class="text-gray-300">→</span>
                <span class="px-2 py-1 rounded bg-teal-100 text-teal-600">质保中</span>
                <span class="text-gray-300">→</span>
                <span class="px-2 py-1 rounded bg-green-100 text-green-600">已释放</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：操作面板 -->
        <div class="space-y-6">
          <!-- 收益统计卡片 -->
          <div class="p-6 rounded-3xl text-white shadow-xl" style="background: linear-gradient(135deg, #10B981, #059669);">
            <p class="text-white/60 text-xs font-bold uppercase mb-2">预计收益</p>
            <h3 class="text-3xl font-black">¥ {{ order.totalAmount?.toLocaleString() }}</h3>
            
            <div class="mt-6 pt-4 border-t border-white/10 space-y-2 text-xs">
              <div class="flex justify-between">
                <span class="opacity-70">已结算</span>
                <span class="font-bold">¥ {{ order.releasedAmount?.toLocaleString() || 0 }}</span>
              </div>
              <div class="flex justify-between">
                <span class="opacity-70">待结算</span>
                <span class="font-bold">¥ {{ (order.totalAmount - (order.releasedAmount || 0))?.toLocaleString() }}</span>
              </div>
              <div class="flex justify-between">
                <span class="opacity-70">完成进度</span>
                <span class="font-bold">{{ completedPercent }}%</span>
              </div>
            </div>
          </div>

          <!-- 进度条 -->
          <div class="bg-white rounded-3xl p-6 shadow-sm border border-gray-100">
            <p class="text-xs text-gray-400 font-bold uppercase mb-3">完成进度</p>
            <n-progress 
              type="line" 
              :percentage="completedPercent" 
              :color="'#10B981'"
              :height="12"
              :border-radius="6"
            />
            <p class="text-sm text-gray-500 mt-2">
              {{ completedStages }} / {{ order.stages?.length || 0 }} 个阶段已完成
            </p>
          </div>

          <!-- 合同卡片 -->
          <div v-if="order.status === 8" class="bg-amber-50 rounded-3xl p-6 border border-amber-200">
            <div class="flex items-center gap-3 mb-4">
              <div class="w-10 h-10 rounded-full bg-amber-500/20 flex items-center justify-center">
                <i class="fas fa-file-signature text-amber-600"></i>
              </div>
              <div>
                <h4 class="font-bold text-gray-800">项目合同</h4>
                <p class="text-xs text-amber-600">请先拟定并签署合同</p>
              </div>
            </div>
            
            <div class="space-y-2">
              <n-button 
                block 
                type="primary" 
                color="#f59e0b"
                @click="router.push(`/merchant/orders/${order.id}/contract`)"
              >
                <template #icon><i class="fas fa-edit"></i></template>
                编辑合同
              </n-button>
            </div>
          </div>
          
          <!-- 待托管提示（用户余额不足时） -->
          <div v-else-if="order.status === 1" class="bg-yellow-50 rounded-3xl p-6 border border-yellow-200">
            <div class="flex items-center gap-3 mb-4">
              <div class="w-10 h-10 rounded-full bg-yellow-500/20 flex items-center justify-center">
                <i class="fas fa-clock text-yellow-600"></i>
              </div>
              <div>
                <h4 class="font-bold text-gray-800">等待甲方托管</h4>
                <p class="text-xs text-yellow-600">合同已签署，等待甲方托管款项</p>
              </div>
            </div>
            <n-button 
              block 
              ghost
              @click="router.push(`/merchant/orders/${order.id}/contract/sign`)"
            >
              <template #icon><i class="fas fa-eye"></i></template>
              查看合同
            </n-button>
          </div>

          <!-- 操作按钮 -->
          <div class="bg-white rounded-3xl p-6 shadow-sm border border-gray-100 space-y-3">
            <!-- 进行中：提交交付按钮 -->
            <n-button 
              v-if="order.status === 2 && hasEscrowedStage"
              block 
              size="large" 
              type="primary" 
              color="#10B981"
              class="font-bold"
              @click="handleSubmitFinalDelivery"
            >
              <template #icon><i class="fas fa-upload"></i></template>
              提交交付 - {{ firstEscrowedStage?.stageName }}
            </n-button>
            
            <!-- 待验收状态提示 -->
            <div v-if="order.status === 3" class="p-4 bg-purple-50 rounded-xl border border-purple-100">
              <p class="text-sm text-purple-700 font-bold mb-1">
                <i class="fas fa-clock mr-1"></i> 等待甲方验收
              </p>
              <p class="text-xs text-purple-600">
                交付已提交，请等待甲方验收确认。验收通过后款项将释放到您的账户。
              </p>
            </div>

            <n-button block size="large" type="primary" color="#00AFE1" class="font-bold" @click="showChat = true">
              <template #icon><i class="fas fa-comments"></i></template>
              联系甲方
            </n-button>
            
            <n-button block size="large" ghost class="font-bold" @click="showEvidence = true">
              <template #icon><i class="fas fa-shield-check"></i></template>
              查看存证记录
            </n-button>
          </div>

          <!-- 提示 -->
          <div class="bg-green-50 p-4 rounded-2xl border border-green-100">
            <div class="flex gap-3">
              <i class="fas fa-lightbulb text-green-600 mt-1"></i>
              <div>
                <p class="text-xs font-bold text-green-700">温馨提示</p>
                <p class="text-xs text-green-600 mt-1 leading-relaxed">
                  按时高质量交付可提升信用分和好评率，获得更多优质订单推荐。
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- 提交交付弹窗 -->
    <n-modal v-model:show="showDeliveryModal" preset="dialog" title="提交交付" style="width: 600px;">
      <div class="space-y-4">
        <p class="text-gray-600">阶段: <strong>{{ currentStage?.stageName }}</strong> (¥{{ currentStage?.amount?.toLocaleString() }})</p>
        
        <n-form-item label="交付说明" required>
          <n-input 
            v-model:value="deliveryForm.desc" 
            type="textarea" 
            placeholder="请描述本阶段的交付内容和完成情况..."
            :rows="4"
          />
        </n-form-item>

        <n-form-item label="交付物文件">
          <n-upload
            :custom-request="handleUploadEvidence"
            :show-file-list="false"
            accept=".jpg,.jpeg,.png,.gif,.pdf,.doc,.docx,.zip,.rar,.7z,.tar,.gz,.tgz"
          >
            <n-button dashed :loading="uploading">
              <template #icon><i class="fas fa-cloud-upload-alt"></i></template>
              {{ uploading ? '上传中...' : '上传交付物' }}
            </n-button>
          </n-upload>
          <div v-if="deliveryForm.evidenceUrl" class="mt-2 flex items-center gap-2 text-sm text-green-600">
            <i class="fas fa-check-circle"></i>
            <span>文件已上传</span>
            <a :href="deliveryForm.evidenceUrl" target="_blank" :download="true" class="underline">
              {{ isArchiveFile(deliveryForm.evidenceUrl) ? '下载' : '查看' }}
            </a>
          </div>
        </n-form-item>
      </div>
      <template #action>
        <n-space>
          <n-button @click="showDeliveryModal = false">取消</n-button>
          <n-button type="primary" color="#10B981" :loading="submitting" @click="confirmDelivery">
            提交交付
          </n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 聊天抽屉 -->
    <ChatDrawer 
      v-model:show="showChat" 
      :order-id="order.id" 
      title="联系甲方"
    />

    <!-- 存证记录抽屉 -->
    <EvidenceDrawer 
      v-model:show="showEvidence" 
      :order-id="order.id"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, inject } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import { getOrderDetail, startStage, submitDelivery } from '@/api/order'
import { uploadFile } from '@/api/file'
import ChatDrawer from '@/components/ChatDrawer.vue'
import EvidenceDrawer from '@/components/EvidenceDrawer.vue'

const router = useRouter()
const route = useRoute()
const message = useMessage()

// 注入刷新余额方法
const refreshBalance = inject('refreshBalance', () => {})

const loading = ref(true)
const showChat = ref(false)
const showEvidence = ref(false)
const showDeliveryModal = ref(false)
const currentStage = ref(null)
const startingStage = ref(null)
const submitting = ref(false)
const uploading = ref(false)

const deliveryForm = reactive({
  desc: '',
  evidenceUrl: ''
})

const order = reactive({
  id: null,
  orderNo: '',
  title: '',
  description: '',
  techStack: '',
  features: '',
  docUrls: '',
  totalAmount: 0,
  depositAmount: 0,
  releasedAmount: 0,
  status: 0,
  buyerName: '',
  deliveryTime: '',
  stages: []
})

const completedStages = computed(() => {
  return order.stages?.filter(s => s.status === 3).length || 0
})

const completedPercent = computed(() => {
  if (!order.stages?.length) return 0
  return Math.round((completedStages.value / order.stages.length) * 100)
})

// 是否有已托管的尾款阶段（可以提交交付）
// 首付款阶段(stageType=1)在合同签署后自动释放，不需要提交交付
// 只有尾款阶段(stageType=2)和质保款阶段(stageType=3)需要提交交付
const hasEscrowedStage = computed(() => {
  return order.stages?.some(s => s.status === 1 && s.stageType !== 1) || false
})

// 获取第一个已托管的尾款阶段（用于提交交付）
const firstEscrowedStage = computed(() => {
  return order.stages?.find(s => s.status === 1 && s.stageType !== 1) || null
})

const loadOrder = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    // 后端返回格式: { order: {...}, stages: [...] }
    const orderData = res.data.order || res.data
    const stagesData = res.data.stages || []
    Object.assign(order, orderData, { stages: stagesData })
    // 刷新顶部余额显示
    refreshBalance()
    
    // 如果带有 openChat 参数，自动打开聊天窗口
    if (route.query.openChat === 'true') {
      showChat.value = true
    }
  } catch (e) {
    message.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const parseDocUrls = (urls) => {
  if (!urls) return []
  return urls.split(',').filter(u => u)
}

const getFileName = (url) => {
  if (!url) return '文件'
  const parts = url.split('/')
  return decodeURIComponent(parts[parts.length - 1]) || '文件'
}

// 判断是否为压缩包文件
const isArchiveFile = (url) => {
  if (!url) return false
  const lowerUrl = url.toLowerCase()
  return lowerUrl.endsWith('.zip') || lowerUrl.endsWith('.rar') || 
         lowerUrl.endsWith('.7z') || lowerUrl.endsWith('.tar') || 
         lowerUrl.endsWith('.gz') || lowerUrl.endsWith('.tgz')
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.split('T')[0]
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace('T', ' ').substring(0, 16)
}

const getStatusText = (status) => {
  const map = { 0: '待接单', 1: '待托管', 2: '进行中', 3: '待验收', 4: '已完成', 5: '已取消', 6: '纠纷中', 7: '待甲方付款', 8: '待签合同', 9: '质保中' }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = {
    0: 'bg-gray-100 text-gray-500',
    1: 'bg-orange-50 text-orange-500',
    2: 'bg-blue-50 text-blue-500',
    3: 'bg-purple-50 text-purple-500',
    4: 'bg-green-50 text-green-500',
    5: 'bg-gray-100 text-gray-400',
    6: 'bg-red-50 text-red-500',
    7: 'bg-yellow-50 text-yellow-600',
    8: 'bg-amber-50 text-amber-600',
    9: 'bg-teal-50 text-teal-600'
  }
  return map[status] || 'bg-gray-100 text-gray-500'
}

const getStageStatusText = (status) => {
  // 0未开始, 1已托管, 2待验收, 3已验收/已结算, 4质保中, 5质保已释放
  const map = { 0: '未开始', 1: '已托管', 2: '待验收', 3: '已结算', 4: '质保中', 5: '已释放' }
  return map[status] || '未知'
}

const getStageStatusClass = (status) => {
  const map = {
    0: 'bg-gray-100 text-gray-400',
    1: 'bg-blue-50 text-blue-500',
    2: 'bg-purple-50 text-purple-500',
    3: 'bg-green-50 text-green-500',
    4: 'bg-teal-50 text-teal-500',
    5: 'bg-green-50 text-green-500'
  }
  return map[status] || 'bg-gray-100 text-gray-400'
}

const getStageCardClass = (status) => {
  const map = {
    0: 'border-gray-100 bg-gray-50',
    1: 'border-blue-200 bg-blue-50/30',
    2: 'border-purple-200 bg-purple-50/30',
    3: 'border-green-200 bg-green-50/30'
  }
  return map[status] || 'border-gray-100'
}

const getStageColor = (status) => {
  const map = {
    0: '#9CA3AF',
    1: '#00AFE1',
    2: '#8B5CF6',
    3: '#10B981'
  }
  return map[status] || '#9CA3AF'
}

const canStartStage = (index) => {
  // 第一个阶段可以直接开始
  if (index === 0) return true
  // 其他阶段需要前一个阶段完成
  return order.stages[index - 1]?.status === 3
}

const handleStartStage = async (stage) => {
  startingStage.value = stage.id
  try {
    await startStage(order.id, stage.id)
    message.success('阶段已开始')
    loadOrder()
  } catch (e) {
    message.error(e.message || '操作失败')
  } finally {
    startingStage.value = null
  }
}

const handleSubmitDelivery = (stage) => {
  currentStage.value = stage
  deliveryForm.desc = ''
  deliveryForm.evidenceUrl = ''
  showDeliveryModal.value = true
}

// 提交最终交付（使用第一个已托管的阶段）
const handleSubmitFinalDelivery = () => {
  if (firstEscrowedStage.value) {
    handleSubmitDelivery(firstEscrowedStage.value)
  }
}

const handleUploadEvidence = async ({ file }) => {
  uploading.value = true
  try {
    const res = await uploadFile(file.file, 'evidence')
    // res.data 是 { url: "...", name: "..." } 对象，需要取 url 属性
    deliveryForm.evidenceUrl = res.data.url || res.data
    message.success('上传成功')
  } catch (e) {
    message.error(e.message || '上传失败')
  } finally {
    uploading.value = false
  }
}

const confirmDelivery = async () => {
  if (!deliveryForm.desc.trim()) {
    message.warning('请填写交付说明')
    return
  }
  submitting.value = true
  try {
    await submitDelivery(order.id, currentStage.value.id, {
      deliveryDesc: deliveryForm.desc,
      evidenceUrl: deliveryForm.evidenceUrl
    })
    message.success('交付提交成功，等待甲方验收')
    showDeliveryModal.value = false
    loadOrder()
  } catch (e) {
    message.error(e.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadOrder()
})
</script>
