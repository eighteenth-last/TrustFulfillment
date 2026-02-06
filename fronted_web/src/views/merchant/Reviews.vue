<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-white">我的评价</h1>
        <p class="text-gray-400 mt-1">查看客户对您服务的评价</p>
      </div>
    </div>

    <!-- 评价统计 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="bg-slate-800 p-6 rounded-2xl border border-slate-700 text-center">
        <p class="text-3xl font-black" style="color: #00AFE1;">{{ stats.avgScore }}</p>
        <p class="text-gray-400 text-sm mt-1">综合评分</p>
        <div class="flex justify-center gap-1 mt-2">
          <i 
            v-for="i in 5" 
            :key="i" 
            class="fas fa-star text-sm"
            :class="i <= Math.round(stats.avgScore) ? 'text-yellow-400' : 'text-gray-600'"
          ></i>
        </div>
      </div>
      <div class="bg-slate-800 p-6 rounded-2xl border border-slate-700 text-center">
        <p class="text-3xl font-black text-green-400">{{ stats.goodRate }}%</p>
        <p class="text-gray-400 text-sm mt-1">好评率</p>
      </div>
      <div class="bg-slate-800 p-6 rounded-2xl border border-slate-700 text-center">
        <p class="text-3xl font-black text-white">{{ stats.totalReviews }}</p>
        <p class="text-gray-400 text-sm mt-1">总评价数</p>
      </div>
      <div class="bg-slate-800 p-6 rounded-2xl border border-slate-700 text-center">
        <p class="text-3xl font-black text-orange-400">{{ stats.replyRate }}%</p>
        <p class="text-gray-400 text-sm mt-1">回复率</p>
      </div>
    </div>

    <!-- 筛选 -->
    <div class="flex items-center gap-4">
      <n-button 
        v-for="tab in tabs" 
        :key="tab.value"
        :type="activeTab === tab.value ? 'primary' : 'default'"
        :color="activeTab === tab.value ? '#00AFE1' : undefined"
        :ghost="activeTab !== tab.value"
        size="small"
        @click="activeTab = tab.value"
      >
        {{ tab.label }} ({{ tab.count }})
      </n-button>
    </div>

    <!-- 评价列表 -->
    <div class="space-y-4">
      <n-spin :show="loading">
        <div v-if="filteredReviews.length === 0" class="bg-slate-800 rounded-3xl p-16 text-center border border-slate-700">
          <div class="w-20 h-20 bg-slate-700 rounded-full flex items-center justify-center mx-auto mb-4">
            <i class="fas fa-star text-3xl text-gray-500"></i>
          </div>
          <p class="text-gray-400 font-bold">暂无评价</p>
          <p class="text-gray-500 text-sm mt-2">完成订单后客户可以对您进行评价</p>
        </div>

        <div 
          v-for="review in filteredReviews" 
          :key="review.id"
          class="bg-slate-800 rounded-2xl p-6 border border-slate-700"
        >
          <div class="flex items-start justify-between mb-4">
            <div class="flex items-start gap-4">
              <div class="w-12 h-12 bg-slate-700 rounded-full flex items-center justify-center">
                <i class="fas fa-user text-gray-400"></i>
              </div>
              <div>
                <div class="flex items-center gap-3">
                  <p class="font-bold text-white">{{ review.buyerName }}</p>
                  <div class="flex gap-0.5">
                    <i 
                      v-for="i in 5" 
                      :key="i" 
                      class="fas fa-star text-sm"
                      :class="i <= review.score ? 'text-yellow-400' : 'text-gray-600'"
                    ></i>
                  </div>
                </div>
                <p class="text-gray-500 text-xs mt-1">
                  {{ formatDate(review.createTime) }} | 项目: {{ review.orderTitle }}
                </p>
              </div>
            </div>
            <n-tag :type="getScoreType(review.score)" size="small">
              {{ getScoreLabel(review.score) }}
            </n-tag>
          </div>

          <!-- 评价内容 -->
          <div class="mb-4">
            <p class="text-gray-300">{{ review.content || '该用户未填写评价内容' }}</p>
          </div>

          <!-- 评价标签 -->
          <div v-if="review.tags && review.tags.length > 0" class="flex flex-wrap gap-2 mb-4">
            <n-tag 
              v-for="tag in review.tags" 
              :key="tag"
              size="small"
              :bordered="false"
              :color="{ color: 'rgba(0,175,225,0.1)', textColor: '#00AFE1' }"
            >
              {{ tag }}
            </n-tag>
          </div>

          <!-- 商家回复 -->
          <div v-if="review.reply" class="p-4 bg-slate-700/50 rounded-xl">
            <p class="text-xs text-gray-400 mb-2">
              <i class="fas fa-reply mr-1"></i>商家回复
            </p>
            <p class="text-gray-300 text-sm">{{ review.reply }}</p>
          </div>

          <!-- 回复按钮 -->
          <div v-else class="flex justify-end mt-4">
            <n-button size="small" ghost @click="openReplyModal(review)">
              <template #icon><i class="fas fa-reply"></i></template>
              回复评价
            </n-button>
          </div>
        </div>
      </n-spin>
    </div>

    <!-- 回复评价弹窗 -->
    <n-modal v-model:show="showReplyModal" preset="card" title="回复评价" class="max-w-lg">
      <div v-if="replyingReview" class="space-y-4">
        <div class="p-4 bg-slate-700 rounded-xl">
          <div class="flex items-center gap-2 mb-2">
            <span class="text-white font-bold">{{ replyingReview.buyerName }}</span>
            <div class="flex gap-0.5">
              <i 
                v-for="i in 5" 
                :key="i" 
                class="fas fa-star text-xs"
                :class="i <= replyingReview.score ? 'text-yellow-400' : 'text-gray-600'"
              ></i>
            </div>
          </div>
          <p class="text-gray-400 text-sm">{{ replyingReview.content || '该用户未填写评价内容' }}</p>
        </div>

        <n-input 
          v-model:value="replyContent"
          type="textarea"
          placeholder="请输入您的回复..."
          :rows="4"
        />

        <div class="flex justify-end gap-3">
          <n-button @click="showReplyModal = false">取消</n-button>
          <n-button type="primary" color="#00AFE1" :loading="replying" @click="submitReply">
            提交回复
          </n-button>
        </div>
      </div>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useMessage } from 'naive-ui'

const message = useMessage()

const loading = ref(false)
const reviews = ref([])
const activeTab = ref('all')
const showReplyModal = ref(false)
const replyingReview = ref(null)
const replyContent = ref('')
const replying = ref(false)

const stats = reactive({
  avgScore: 4.8,
  goodRate: 100,
  totalReviews: 0,
  replyRate: 0
})

const tabs = computed(() => [
  { label: '全部', value: 'all', count: reviews.value.length },
  { label: '好评', value: 'good', count: reviews.value.filter(r => r.score >= 4).length },
  { label: '中评', value: 'medium', count: reviews.value.filter(r => r.score === 3).length },
  { label: '差评', value: 'bad', count: reviews.value.filter(r => r.score < 3).length },
  { label: '未回复', value: 'unreplied', count: reviews.value.filter(r => !r.reply).length }
])

const filteredReviews = computed(() => {
  if (activeTab.value === 'all') return reviews.value
  if (activeTab.value === 'good') return reviews.value.filter(r => r.score >= 4)
  if (activeTab.value === 'medium') return reviews.value.filter(r => r.score === 3)
  if (activeTab.value === 'bad') return reviews.value.filter(r => r.score < 3)
  if (activeTab.value === 'unreplied') return reviews.value.filter(r => !r.reply)
  return reviews.value
})

// 加载评价列表
const loadReviews = async () => {
  loading.value = true
  try {
    // TODO: 调用后端接口获取评价列表
    // const res = await getReviewList()
    // 模拟数据
    reviews.value = [
      {
        id: 1,
        buyerName: '张先生',
        orderTitle: '企业官网开发',
        score: 5,
        content: '非常专业的服务商，代码质量很高，沟通也很顺畅，项目按时交付，很满意！',
        tags: ['沟通顺畅', '按时交付', '代码规范'],
        createTime: new Date().toISOString(),
        reply: '感谢您的认可，期待下次合作！'
      },
      {
        id: 2,
        buyerName: '李女士',
        orderTitle: '小程序商城',
        score: 5,
        content: '服务很好，功能都实现了，后期维护也很及时',
        tags: ['服务态度好', '技术专业'],
        createTime: new Date(Date.now() - 86400000 * 3).toISOString(),
        reply: null
      },
      {
        id: 3,
        buyerName: '王总',
        orderTitle: 'APP后端接口',
        score: 4,
        content: '整体不错，有些小问题但都及时修复了',
        tags: ['响应及时'],
        createTime: new Date(Date.now() - 86400000 * 7).toISOString(),
        reply: '感谢您的反馈，我们会继续改进服务质量'
      }
    ]
    
    // 计算统计数据
    if (reviews.value.length > 0) {
      stats.totalReviews = reviews.value.length
      stats.avgScore = (reviews.value.reduce((sum, r) => sum + r.score, 0) / reviews.value.length).toFixed(1)
      stats.goodRate = Math.round(reviews.value.filter(r => r.score >= 4).length / reviews.value.length * 100)
      stats.replyRate = Math.round(reviews.value.filter(r => r.reply).length / reviews.value.length * 100)
    }
  } catch (e) {
    console.error('加载评价失败', e)
  } finally {
    loading.value = false
  }
}

const getScoreLabel = (score) => {
  if (score >= 4) return '好评'
  if (score === 3) return '中评'
  return '差评'
}

const getScoreType = (score) => {
  if (score >= 4) return 'success'
  if (score === 3) return 'warning'
  return 'error'
}

const formatDate = (dateVal) => {
  if (!dateVal) return '-'
  const date = new Date(dateVal)
  return date.toLocaleDateString('zh-CN')
}

// 打开回复弹窗
const openReplyModal = (review) => {
  replyingReview.value = review
  replyContent.value = ''
  showReplyModal.value = true
}

// 提交回复
const submitReply = async () => {
  if (!replyContent.value.trim()) {
    message.warning('请输入回复内容')
    return
  }
  
  replying.value = true
  try {
    // TODO: 调用后端接口提交回复
    // await replyReview(replyingReview.value.id, replyContent.value)
    
    // 模拟成功
    replyingReview.value.reply = replyContent.value
    message.success('回复成功')
    showReplyModal.value = false
    
    // 更新回复率
    stats.replyRate = Math.round(reviews.value.filter(r => r.reply).length / reviews.value.length * 100)
  } catch (e) {
    message.error('回复失败')
  } finally {
    replying.value = false
  }
}

onMounted(() => {
  loadReviews()
})
</script>
