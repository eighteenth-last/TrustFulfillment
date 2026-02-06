<template>
  <div class="space-y-6">
    <!-- 统计 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="bg-white p-5 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">全部纠纷</p>
        <p class="text-2xl font-black text-gray-800 mt-1">{{ stats.total }}</p>
      </div>
      <div class="bg-white p-5 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">待处理</p>
        <p class="text-2xl font-black text-red-500 mt-1">{{ stats.pending }}</p>
      </div>
      <div class="bg-white p-5 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">处理中</p>
        <p class="text-2xl font-black text-orange-500 mt-1">{{ stats.processing }}</p>
      </div>
      <div class="bg-white p-5 rounded-2xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">已裁决</p>
        <p class="text-2xl font-black text-green-500 mt-1">{{ stats.resolved }}</p>
      </div>
    </div>

    <!-- 纠纷列表 -->
    <div class="space-y-4">
      <div 
        v-for="dispute in disputes" 
        :key="dispute.id"
        class="bg-white rounded-2xl border overflow-hidden"
        :class="dispute.status === 0 ? 'border-red-200' : 'border-gray-100'"
      >
        <!-- 头部 -->
        <div class="p-6 flex justify-between items-center" :class="dispute.status === 0 ? 'bg-red-50/50' : 'bg-gray-50'">
          <div class="flex items-center gap-3">
            <i class="fas fa-exclamation-triangle" :class="dispute.status === 0 ? 'text-red-500' : 'text-gray-400'"></i>
            <span class="font-bold">案件：#{{ dispute.disputeNo }}</span>
          </div>
          <span 
            class="text-xs font-bold px-3 py-1 rounded-full"
            :class="getStatusClass(dispute.status)"
          >
            {{ getStatusText(dispute.status) }}
          </span>
        </div>

        <!-- 内容 -->
        <div class="p-8 grid grid-cols-2 gap-12">
          <!-- 甲方诉求 -->
          <div>
            <h5 class="text-xs font-bold text-gray-400 uppercase mb-4 tracking-widest">
              甲方诉求 ({{ dispute.buyerClaim }})
            </h5>
            <div class="p-4 bg-gray-50 rounded-2xl text-sm leading-relaxed">
              "{{ dispute.buyerReason }}"
            </div>
            <div class="mt-4 grid grid-cols-4 gap-2">
              <div v-for="i in dispute.buyerEvidenceCount" :key="i" class="aspect-square bg-gray-200 rounded-lg"></div>
            </div>
            <p class="text-xs text-gray-400 mt-2">甲方: {{ dispute.buyerName }}</p>
          </div>

          <!-- 乙方抗辩 -->
          <div>
            <h5 class="text-xs font-bold text-gray-400 uppercase mb-4 tracking-widest">
              乙方抗辩 ({{ dispute.sellerClaim }})
            </h5>
            <div class="p-4 bg-gray-50 rounded-2xl text-sm leading-relaxed">
              "{{ dispute.sellerReason }}"
            </div>
            <div class="mt-4 grid grid-cols-4 gap-2">
              <div v-for="i in dispute.sellerEvidenceCount" :key="i" class="aspect-square bg-gray-200 rounded-lg"></div>
            </div>
            <p class="text-xs text-gray-400 mt-2">乙方: {{ dispute.sellerName }}</p>
          </div>
        </div>

        <!-- 订单信息 -->
        <div class="px-8 pb-4">
          <div class="flex items-center gap-6 text-xs text-gray-400">
            <span>关联订单: {{ dispute.orderNo }}</span>
            <span>涉及金额: ¥{{ dispute.amount.toLocaleString() }}</span>
            <span>发起时间: {{ dispute.createTime }}</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="p-6 border-t bg-gray-50 flex justify-end gap-3">
          <n-button ghost @click="viewDetail(dispute)">
            查看详情
          </n-button>
          <n-button ghost @click="handleMediate(dispute)">人工介入调解</n-button>
          <n-button type="error" ghost @click="handleRefund(dispute)">裁定退款(信托划扣)</n-button>
          <n-button type="primary" color="#00AFE1" @click="handleSettle(dispute)">裁定结算</n-button>
        </div>
      </div>
    </div>

    <!-- 裁决弹窗 -->
    <n-modal v-model:show="showRulingModal" preset="card" title="纠纷裁决" class="max-w-2xl">
      <div v-if="currentDispute" class="space-y-6">
        <div class="p-4 bg-gray-50 rounded-xl">
          <h4 class="font-bold mb-2">案件 #{{ currentDispute.disputeNo }}</h4>
          <p class="text-sm text-gray-500">{{ currentDispute.orderNo }} | 涉及金额: ¥{{ currentDispute.amount.toLocaleString() }}</p>
        </div>

        <n-form-item label="裁决类型">
          <n-radio-group v-model:value="rulingType">
            <n-space>
              <n-radio value="refund">支持甲方（退款）</n-radio>
              <n-radio value="settle">支持乙方（结算）</n-radio>
              <n-radio value="partial">部分退款</n-radio>
            </n-space>
          </n-radio-group>
        </n-form-item>

        <n-form-item v-if="rulingType === 'partial'" label="退款金额">
          <n-input-number v-model:value="refundAmount" :min="0" :max="currentDispute?.amount" class="w-full">
            <template #prefix>¥</template>
          </n-input-number>
        </n-form-item>

        <n-form-item label="裁决说明">
          <n-input 
            v-model:value="rulingReason" 
            type="textarea" 
            placeholder="请输入裁决依据和说明..."
            :rows="4"
          />
        </n-form-item>

        <div class="p-3 bg-yellow-50 rounded-lg text-xs text-yellow-700">
          <i class="fas fa-exclamation-triangle mr-1"></i>
          裁决结果将直接执行资金划转，请谨慎操作
        </div>

        <div class="flex justify-end">
          <n-button type="primary" color="#00AFE1" size="large" @click="confirmRuling">
            确认裁决
          </n-button>
        </div>
      </div>
    </n-modal>

    <!-- 详情弹窗 -->
    <n-modal v-model:show="showDetailModal" preset="card" title="纠纷详情" style="width: 900px; max-width: 90vw;">
      <div v-if="currentDispute" class="space-y-6">
        <!-- 基本信息 -->
        <div class="p-4 bg-gray-50 rounded-xl">
          <div class="flex items-center justify-between mb-3">
            <h4 class="font-bold text-lg">案件 #{{ currentDispute.disputeNo }}</h4>
            <span 
              class="text-xs font-bold px-3 py-1 rounded-full"
              :class="getStatusClass(currentDispute.status)"
            >
              {{ getStatusText(currentDispute.status) }}
            </span>
          </div>
          <div class="grid grid-cols-3 gap-4 text-sm">
            <div>
              <span class="text-gray-400">关联订单：</span>
              <span class="font-medium">{{ currentDispute.orderNo }}</span>
            </div>
            <div>
              <span class="text-gray-400">涉及金额：</span>
              <span class="font-medium text-red-500">¥{{ currentDispute.amount?.toLocaleString() }}</span>
            </div>
            <div>
              <span class="text-gray-400">发起时间：</span>
              <span class="font-medium">{{ currentDispute.createTime }}</span>
            </div>
          </div>
        </div>

        <!-- 双方信息 -->
        <div class="grid grid-cols-2 gap-6">
          <!-- 甲方（买家） -->
          <div class="p-4 bg-blue-50 rounded-xl">
            <h5 class="font-bold text-blue-700 mb-3">
              <i class="fas fa-user mr-2"></i>甲方（买家）
            </h5>
            <div class="space-y-2 text-sm">
              <div>
                <span class="text-gray-500">用户名：</span>
                <span class="font-medium">{{ currentDispute.buyerName }}</span>
              </div>
              <div>
                <span class="text-gray-500">诉求：</span>
                <span class="font-medium text-blue-600">{{ currentDispute.buyerClaim }}</span>
              </div>
              <div class="mt-3">
                <span class="text-gray-500">诉求理由：</span>
                <div class="mt-1 p-3 bg-white rounded-lg">{{ currentDispute.buyerReason }}</div>
              </div>
            </div>
          </div>

          <!-- 乙方（卖家） -->
          <div class="p-4 bg-orange-50 rounded-xl">
            <h5 class="font-bold text-orange-700 mb-3">
              <i class="fas fa-store mr-2"></i>乙方（商家）
            </h5>
            <div class="space-y-2 text-sm">
              <div>
                <span class="text-gray-500">商家名：</span>
                <span class="font-medium">{{ currentDispute.sellerName }}</span>
              </div>
              <div>
                <span class="text-gray-500">抗辩：</span>
                <span class="font-medium text-orange-600">{{ currentDispute.sellerClaim }}</span>
              </div>
              <div class="mt-3">
                <span class="text-gray-500">抗辩理由：</span>
                <div class="mt-1 p-3 bg-white rounded-lg">{{ currentDispute.sellerReason }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 证据材料 -->
        <div class="p-4 border rounded-xl">
          <h5 class="font-bold mb-3">
            <i class="fas fa-file-alt mr-2 text-gray-400"></i>证据材料
          </h5>
          <div v-if="detailData.evidenceUrls && detailData.evidenceUrls.length > 0" class="grid grid-cols-4 gap-3">
            <div 
              v-for="(url, idx) in detailData.evidenceUrls" 
              :key="idx" 
              class="aspect-square bg-gray-100 rounded-lg overflow-hidden cursor-pointer hover:opacity-80"
              @click="previewImage(url)"
            >
              <img :src="url" class="w-full h-full object-cover" />
            </div>
          </div>
          <div v-else class="text-center py-6 text-gray-400">
            <i class="fas fa-image text-2xl mb-2"></i>
            <p class="text-sm">暂无证据材料</p>
          </div>
        </div>

        <!-- 调解/留言记录 -->
        <div class="p-4 border rounded-xl">
          <h5 class="font-bold mb-3">
            <i class="fas fa-comments mr-2 text-gray-400"></i>沟通记录
          </h5>
          <div v-if="detailData.messages && detailData.messages.length > 0" class="space-y-3 max-h-60 overflow-y-auto">
            <div 
              v-for="msg in detailData.messages" 
              :key="msg.id"
              class="p-3 rounded-lg"
              :class="{
                'bg-blue-50': msg.role === 'user',
                'bg-orange-50': msg.role === 'merchant',
                'bg-green-50': msg.role === 'admin'
              }"
            >
              <div class="flex items-center justify-between mb-1">
                <span class="font-medium text-sm">
                  {{ msg.role === 'admin' ? '平台客服' : (msg.role === 'user' ? '甲方' : '乙方') }}
                  <span class="text-xs text-gray-400 ml-2">{{ msg.userName }}</span>
                </span>
                <span class="text-xs text-gray-400">{{ formatDateTime(msg.createTime) }}</span>
              </div>
              <p class="text-sm">{{ msg.content }}</p>
            </div>
          </div>
          <div v-else class="text-center py-6 text-gray-400">
            <i class="fas fa-comment-slash text-2xl mb-2"></i>
            <p class="text-sm">暂无沟通记录</p>
          </div>
        </div>

        <!-- 裁决结果（如果已裁决） -->
        <div v-if="currentDispute.status === 2 && detailData.resolution" class="p-4 bg-green-50 border border-green-200 rounded-xl">
          <h5 class="font-bold text-green-700 mb-3">
            <i class="fas fa-gavel mr-2"></i>裁决结果
          </h5>
          <div class="space-y-2 text-sm">
            <div>
              <span class="text-gray-500">裁决类型：</span>
              <span class="font-medium">{{ getResolutionText(detailData.resolution) }}</span>
            </div>
            <div v-if="detailData.resolutionRemark">
              <span class="text-gray-500">裁决说明：</span>
              <span>{{ detailData.resolutionRemark }}</span>
            </div>
            <div v-if="detailData.resolveTime">
              <span class="text-gray-500">裁决时间：</span>
              <span>{{ formatDateTime(detailData.resolveTime) }}</span>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div v-if="currentDispute.status < 2" class="flex justify-end gap-3 pt-4 border-t">
          <n-button @click="showDetailModal = false">关闭</n-button>
          <n-button ghost @click="handleMediateFromDetail">人工介入调解</n-button>
          <n-button type="error" ghost @click="handleRefundFromDetail">裁定退款</n-button>
          <n-button type="primary" color="#00AFE1" @click="handleSettleFromDetail">裁定结算</n-button>
        </div>
        <div v-else class="flex justify-end pt-4 border-t">
          <n-button @click="showDetailModal = false">关闭</n-button>
        </div>
      </div>
    </n-modal>

    <!-- 人工调解弹窗 -->
    <n-modal v-model:show="showMediateModal" preset="card" title="人工介入调解" class="max-w-2xl">
      <div v-if="currentDispute" class="space-y-6">
        <div class="p-4 bg-blue-50 rounded-xl">
          <h4 class="font-bold mb-2">案件 #{{ currentDispute.disputeNo }}</h4>
          <div class="grid grid-cols-2 gap-4 mt-3 text-sm">
            <div>
              <span class="text-gray-500">甲方：</span>
              <span class="font-bold">{{ currentDispute.buyerName }}</span>
            </div>
            <div>
              <span class="text-gray-500">乙方：</span>
              <span class="font-bold">{{ currentDispute.sellerName }}</span>
            </div>
          </div>
        </div>

        <div class="p-4 bg-gray-50 rounded-xl">
          <h5 class="font-bold mb-2">纠纷原因</h5>
          <p class="text-sm text-gray-600">{{ currentDispute.buyerReason }}</p>
        </div>

        <n-form-item label="调解方案">
          <n-input 
            v-model:value="mediateMessage" 
            type="textarea" 
            placeholder="请输入调解方案或建议..."
            :rows="4"
          />
        </n-form-item>

        <div class="p-3 bg-blue-50 rounded-lg text-xs text-blue-700">
          <i class="fas fa-info-circle mr-1"></i>
          人工介入后，纠纷状态将变为"处理中"，系统会通知双方用户
        </div>

        <div class="flex justify-end">
          <n-button type="primary" color="#00AFE1" size="large" @click="confirmMediate">
            确认介入
          </n-button>
        </div>
      </div>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { getDisputeList, getDisputeStats, getDisputeDetail, resolveDispute } from '@/api/admin'

const message = useMessage()

const loading = ref(false)
const showRulingModal = ref(false)
const showMediateModal = ref(false)
const showDetailModal = ref(false)
const mediateMessage = ref('')
const currentDispute = ref(null)
const detailData = reactive({
  evidenceUrls: [],
  messages: [],
  resolution: null,
  resolutionRemark: '',
  resolveTime: null
})
const rulingType = ref('refund')
const rulingReason = ref('')
const refundAmount = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const totalRecords = ref(0)

const stats = reactive({
  total: 0,
  pending: 0,
  processing: 0,
  resolved: 0
})

const disputes = ref([])

// 加载纠纷统计
const loadStats = async () => {
  try {
    const res = await getDisputeStats()
    if (res.data) {
      stats.total = res.data.total || 0
      stats.pending = res.data.pending || 0
      stats.processing = res.data.processing || 0
      stats.resolved = res.data.resolved || 0
    }
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

// 加载纠纷列表
const loadDisputes = async () => {
  loading.value = true
  try {
    const res = await getDisputeList({
      page: currentPage.value,
      size: pageSize.value
    })
    if (res.data && res.data.records) {
      disputes.value = res.data.records.map(d => {
        let evidenceCount = 0
        try {
          if (d.evidenceUrls) {
            evidenceCount = JSON.parse(d.evidenceUrls).length
          }
        } catch (e) {
          evidenceCount = 0
        }
        return {
          id: d.id,
          disputeNo: `DISP-${d.id}`,
          orderNo: d.orderNo || `TF${d.orderId}`,
          status: d.status,
          amount: d.amount || 0,
          buyerName: d.buyerName || '未知用户',
          sellerName: d.sellerName || '未知用户',
          buyerClaim: '申请退款',
          sellerClaim: '拒绝退款',
          buyerReason: d.reason || '无',
          sellerReason: '待乙方答辩',
          buyerEvidenceCount: evidenceCount,
          sellerEvidenceCount: 0,
          createTime: formatDateTime(d.createTime)
        }
      })
      totalRecords.value = res.data.total
    }
  } catch (e) {
    console.error('加载纠纷列表失败', e)
  } finally {
    loading.value = false
  }
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getStatusText = (status) => {
  const map = { 0: '紧急处理中', 1: '调查中', 2: '已裁决', 3: '已驳回' }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = {
    0: 'bg-red-100 text-red-500',
    1: 'bg-orange-100 text-orange-500',
    2: 'bg-green-100 text-green-500',
    3: 'bg-gray-100 text-gray-500'
  }
  return map[status] || 'bg-gray-100 text-gray-500'
}

const viewDetail = async (dispute) => {
  try {
    const res = await getDisputeDetail(dispute.id)
    if (res.data) {
      currentDispute.value = {
        ...dispute,
        ...res.data
      }
      // 解析证据URL
      detailData.evidenceUrls = []
      if (res.data.evidenceUrls) {
        try {
          detailData.evidenceUrls = typeof res.data.evidenceUrls === 'string' 
            ? JSON.parse(res.data.evidenceUrls) 
            : res.data.evidenceUrls
        } catch (e) {
          if (res.data.evidenceUrls.includes(',')) {
            detailData.evidenceUrls = res.data.evidenceUrls.split(',')
          }
        }
      }
      // 获取留言记录
      detailData.messages = res.data.messages || []
      detailData.resolution = res.data.resolution
      detailData.resolutionRemark = res.data.resolutionRemark || ''
      detailData.resolveTime = res.data.resolveTime
      
      showDetailModal.value = true
    }
  } catch (e) {
    message.error('获取详情失败')
  }
}

const getResolutionText = (resolution) => {
  const map = {
    'buyer_win': '支持甲方（全额退款）',
    'seller_win': '支持乙方（结算给商家）',
    'both': '部分退款',
    'mediate': '调解中'
  }
  return map[resolution] || resolution
}

const previewImage = (url) => {
  window.open(url, '_blank')
}

// 从详情弹窗跳转到操作弹窗
const handleMediateFromDetail = () => {
  showDetailModal.value = false
  mediateMessage.value = ''
  showMediateModal.value = true
}

const handleRefundFromDetail = () => {
  showDetailModal.value = false
  rulingType.value = 'refund'
  refundAmount.value = currentDispute.value.amount
  rulingReason.value = ''
  showRulingModal.value = true
}

const handleSettleFromDetail = () => {
  showDetailModal.value = false
  rulingType.value = 'settle'
  rulingReason.value = ''
  showRulingModal.value = true
}

const handleMediate = (dispute) => {
  currentDispute.value = dispute
  mediateMessage.value = ''
  showMediateModal.value = true
}

const confirmMediate = async () => {
  if (!mediateMessage.value) {
    message.warning('请输入调解方案')
    return
  }

  try {
    // 调用API将纠纷状态改为处理中
    await resolveDispute(currentDispute.value.id, {
      result: 'mediate',
      reason: mediateMessage.value
    })

    message.success('已介入调解，纠纷状态已更新')
    showMediateModal.value = false
    mediateMessage.value = ''
    
    // 更新本地状态
    const index = disputes.value.findIndex(d => d.id === currentDispute.value.id)
    if (index > -1) {
      disputes.value[index].status = 1 // 处理中
    }
    
    await loadStats()
  } catch (e) {
    message.error('操作失败')
  }
}

const handleRefund = (dispute) => {
  currentDispute.value = dispute
  rulingType.value = 'refund'
  refundAmount.value = dispute.amount
  showRulingModal.value = true
}

const handleSettle = (dispute) => {
  currentDispute.value = dispute
  rulingType.value = 'settle'
  showRulingModal.value = true
}

const confirmRuling = async () => {
  if (!rulingReason.value) {
    message.warning('请填写裁决说明')
    return
  }
  
  try {
    let result = 'buyer_win'
    if (rulingType.value === 'settle') {
      result = 'seller_win'
    } else if (rulingType.value === 'partial') {
      result = 'both'
    }

    await resolveDispute(currentDispute.value.id, {
      result: result,
      reason: rulingReason.value,
      refundAmount: rulingType.value === 'partial' ? refundAmount.value : null
    })

    message.success('裁决已执行')
    showRulingModal.value = false
    rulingReason.value = ''
    
    // 重新加载数据
    await loadDisputes()
    await loadStats()
  } catch (e) {
    message.error('操作失败')
  }
}

onMounted(() => {
  loadStats()
  loadDisputes()
})
</script>
