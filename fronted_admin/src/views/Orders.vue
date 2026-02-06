<template>
  <div class="space-y-6">
    <!-- 筛选栏 -->
    <div class="bg-white p-4 rounded-2xl border border-gray-100 flex items-center justify-between">
      <div class="flex items-center gap-4">
        <n-input v-model:value="searchKeyword" placeholder="搜索订单号/项目名称" class="w-64">
          <template #prefix><i class="fas fa-search text-gray-400"></i></template>
        </n-input>
      </div>
      <n-button ghost @click="handleExport">
        <template #icon><i class="fas fa-download"></i></template>
        导出
      </n-button>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-5 gap-4">
      <div 
        class="bg-white p-4 rounded-xl border border-gray-100 text-center hover:shadow-md transition cursor-pointer"
        :class="{ 'ring-2 ring-tf': filterStatus === null }"
        @click="filterStatus = null"
      >
        <p class="text-2xl font-black text-gray-800">{{ stats.total }}</p>
        <p class="text-xs text-gray-400 mt-1">全部订单</p>
      </div>
      <div 
        class="bg-white p-4 rounded-xl border border-gray-100 text-center hover:shadow-md transition cursor-pointer"
        :class="{ 'ring-2 ring-orange-400': filterStatus === 0 }"
        @click="quickFilter(0)"
      >
        <p class="text-2xl font-black text-orange-500">{{ stats.pending }}</p>
        <p class="text-xs text-gray-400 mt-1">待审核</p>
      </div>
      <div 
        class="bg-white p-4 rounded-xl border border-gray-100 text-center hover:shadow-md transition cursor-pointer"
        :class="{ 'ring-2 ring-tf': filterStatus === 2 }"
        @click="quickFilter(2)"
      >
        <p class="text-2xl font-black text-tf">{{ stats.inProgress }}</p>
        <p class="text-xs text-gray-400 mt-1">进行中</p>
      </div>
      <div 
        class="bg-white p-4 rounded-xl border border-gray-100 text-center hover:shadow-md transition cursor-pointer"
        :class="{ 'ring-2 ring-green-400': filterStatus === 4 }"
        @click="quickFilter(4)"
      >
        <p class="text-2xl font-black text-green-500">{{ stats.completed }}</p>
        <p class="text-xs text-gray-400 mt-1">已完成</p>
      </div>
      <div 
        class="bg-white p-4 rounded-xl border border-gray-100 text-center hover:shadow-md transition cursor-pointer"
        :class="{ 'ring-2 ring-red-400': filterStatus === 6 }"
        @click="quickFilter(6)"
      >
        <p class="text-2xl font-black text-red-500">{{ stats.disputed }}</p>
        <p class="text-xs text-gray-400 mt-1">纠纷中</p>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="bg-white rounded-2xl border border-gray-100 overflow-hidden">
      <table class="w-full text-left">
        <thead class="bg-gray-50 border-b border-gray-100">
          <tr class="text-xs text-gray-500 uppercase">
            <th class="px-6 py-4 font-bold">订单信息</th>
            <th class="px-6 py-4 font-bold">甲方</th>
            <th class="px-6 py-4 font-bold">乙方</th>
            <th class="px-6 py-4 font-bold text-right">金额</th>
            <th class="px-6 py-4 font-bold text-center">状态</th>
            <th class="px-6 py-4 font-bold text-center">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="order in orders" :key="order.id" class="hover:bg-gray-50 transition">
            <td class="px-6 py-5">
              <p class="font-bold text-gray-800">{{ order.title }}</p>
              <p class="text-xs text-gray-400 mt-1">{{ order.orderNo }} | {{ order.createTime }}</p>
            </td>
            <td class="px-6 py-5 text-sm text-gray-600">{{ order.buyerName }}</td>
            <td class="px-6 py-5 text-sm text-gray-600">{{ order.sellerName || '-' }}</td>
            <td class="px-6 py-5 text-right font-bold text-gray-800">¥{{ order.amount.toLocaleString() }}</td>
            <td class="px-6 py-5 text-center">
              <span class="text-xs font-bold px-3 py-1 rounded-full" :class="getStatusClass(order.status)">
                {{ getStatusText(order.status) }}
              </span>
            </td>
            <td class="px-6 py-5 text-center">
              <n-button size="small" ghost @click="viewOrder(order)">详情</n-button>
              <n-button v-if="order.status === 0" size="small" type="primary" color="#00AFE1" class="ml-2" @click="auditOrder(order)">
                审核
              </n-button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-if="orders.length === 0" class="p-16 text-center">
        <div class="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <i class="fas fa-inbox text-2xl text-gray-400"></i>
        </div>
        <p class="text-gray-400">暂无订单数据</p>
      </div>

      <!-- 分页 -->
      <div class="p-4 border-t border-gray-100 flex justify-center" v-if="orders.length > 0">
        <n-pagination 
          v-model:page="currentPage" 
          :page-count="totalPages"
          @update:page="handlePageChange"
        />
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <n-modal v-model:show="showDetailModal" preset="card" :title="currentOrder?.title" style="width: 700px; max-width: 90vw;">
      <div v-if="currentOrder" class="space-y-5">
        <div class="flex items-center justify-between">
          <span class="text-sm text-gray-500">订单号: {{ currentOrder.orderNo }}</span>
          <span class="text-xs font-bold px-3 py-1 rounded-full" :class="getStatusClass(currentOrder.status)">
            {{ getStatusText(currentOrder.status) }}
          </span>
        </div>

        <!-- 基本信息 -->
        <div class="grid grid-cols-2 gap-5 p-5 bg-gray-50 rounded-xl">
          <div>
            <p class="text-xs text-gray-400 mb-1">甲方（雇主）</p>
            <p class="font-bold">{{ currentOrder.buyerName }}</p>
          </div>
          <div>
            <p class="text-xs text-gray-400 mb-1">乙方（服务商）</p>
            <p class="font-bold">{{ currentOrder.sellerName || '待接单' }}</p>
          </div>
          <div>
            <p class="text-xs text-gray-400 mb-1">订单金额</p>
            <p class="font-bold text-tf text-xl">¥{{ currentOrder.amount?.toLocaleString() }}</p>
          </div>
          <div>
            <p class="text-xs text-gray-400 mb-1">项目分类</p>
            <p class="font-bold">{{ getCategoryName(currentOrder.categoryId) }}</p>
          </div>
        </div>

        <!-- 时间信息 -->
        <div class="grid grid-cols-4 gap-4 p-5 bg-gray-50 rounded-xl">
          <div>
            <p class="text-xs text-gray-400 mb-1">创建时间</p>
            <p class="font-bold text-sm">{{ currentOrder.createTime || '-' }}</p>
          </div>
          <div>
            <p class="text-xs text-gray-400 mb-1">开始时间</p>
            <p class="font-bold text-sm">{{ currentOrder.startTime || '-' }}</p>
          </div>
          <div>
            <p class="text-xs text-gray-400 mb-1">预计交付</p>
            <p class="font-bold text-sm">{{ currentOrder.deliveryTime || '-' }}</p>
          </div>
          <div>
            <p class="text-xs text-gray-400 mb-1">完成时间</p>
            <p class="font-bold text-sm">{{ currentOrder.completedTime || '-' }}</p>
          </div>
        </div>

        <!-- 订单描述 -->
        <div>
          <p class="text-sm font-bold mb-2">订单描述</p>
          <p class="text-gray-600 text-sm p-4 bg-gray-50 rounded-xl leading-relaxed">{{ currentOrder.description || '暂无描述' }}</p>
        </div>

        <!-- 开发文档 / 参考资料 -->
        <div>
          <p class="text-sm font-bold mb-2">开发文档 / 参考资料</p>
          <div class="p-4 bg-gray-50 rounded-xl">
            <div v-if="parseDocUrls(currentOrder.docUrls).length > 0" class="space-y-2">
              <a 
                v-for="(url, index) in parseDocUrls(currentOrder.docUrls)" 
                :key="index"
                :href="url"
                target="_blank"
                class="flex items-center gap-2 text-tf hover:underline text-sm break-all"
              >
                <i class="fas fa-link flex-shrink-0"></i>
                {{ url }}
              </a>
            </div>
            <p v-else class="text-gray-400 text-sm">暂无文档</p>
          </div>
        </div>

        <div class="flex gap-3 justify-end pt-4 border-t">
          <n-button @click="showDetailModal = false">关闭</n-button>
          <n-button v-if="currentOrder.status === 0" type="error" ghost @click="rejectOrder(currentOrder)">
            驳回
          </n-button>
          <n-button v-if="currentOrder.status === 0" type="primary" color="#00AFE1" @click="auditOrder(currentOrder); showDetailModal = false">
            审核通过
          </n-button>
          <n-button v-if="currentOrder.status === 6" type="warning" @click="$router.push('/admin/disputes')">
            处理纠纷
          </n-button>
        </div>
      </div>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useMessage, useDialog } from 'naive-ui'
import { useRouter } from 'vue-router'
import { getOrderList, getOrderStats, auditOrder as apiAuditOrder } from '@/api/admin'

const message = useMessage()
const dialog = useDialog()
const router = useRouter()

const loading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref(null)
const filterCategory = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const showDetailModal = ref(false)
const currentOrder = ref(null)

const statusOptions = [
  { label: '待审核', value: 0 },
  { label: '进行中', value: 2 },
  { label: '已完成', value: 4 },
  { label: '纠纷中', value: 6 }
]

const categoryOptions = [
  { label: 'UI设计', value: 1 },
  { label: '前端开发', value: 2 },
  { label: '后端开发', value: 3 },
  { label: '小程序', value: 4 }
]

const orders = ref([])

const stats = reactive({
  total: 0,
  pending: 0,
  inProgress: 0,
  completed: 0,
  disputed: 0
})

// 加载订单统计
const loadStats = async () => {
  try {
    const res = await getOrderStats()
    if (res.data) {
      stats.total = res.data.total || 0
      stats.pending = res.data.pending || 0
      stats.inProgress = res.data.inProgress || 0
      stats.completed = res.data.completed || 0
      stats.disputed = res.data.disputed || 0
    }
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const res = await getOrderList({
      page: currentPage.value,
      size: pageSize.value,
      status: filterStatus.value,
      keyword: searchKeyword.value || undefined
    })
    if (res.data && res.data.records) {
      orders.value = res.data.records.map(o => ({
        id: o.id,
        orderNo: o.orderNo,
        title: o.title,
        buyerName: o.buyerName || '未知',
        sellerName: o.sellerName || null,
        amount: o.totalAmount || 0,
        status: o.status,
        createTime: formatDate(o.createTime),
        startTime: o.startTime ? formatDate(o.startTime) : null,
        deliveryTime: o.deliveryTime ? formatDate(o.deliveryTime) : null,
        completedTime: o.completedTime ? formatDate(o.completedTime) : null,
        categoryId: o.categoryId,
        description: o.description || '',
        docUrls: o.docUrls || ''
      }))
      totalPages.value = Math.max(1, Math.ceil(res.data.total / pageSize.value))
    }
  } catch (e) {
    console.error('加载订单列表失败', e)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const getStatusText = (status) => {
  const map = { 0: '待审核', 1: '待托管', 2: '进行中', 3: '待验收', 4: '已完成', 5: '已取消', 6: '纠纷中' }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = {
    0: 'bg-orange-100 text-orange-500',
    1: 'bg-yellow-100 text-yellow-600',
    2: 'bg-blue-100 text-tf',
    3: 'bg-purple-100 text-purple-500',
    4: 'bg-green-100 text-green-500',
    5: 'bg-gray-100 text-gray-500',
    6: 'bg-red-100 text-red-500'
  }
  return map[status] || 'bg-gray-100 text-gray-500'
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  const map = { 1: 'UI设计', 2: '前端开发', 3: '后端开发', 4: '小程序', 5: '其他' }
  return map[categoryId] || '未分类'
}

// 解析文档链接
const parseDocUrls = (docUrls) => {
  if (!docUrls) return []
  try {
    const parsed = JSON.parse(docUrls)
    return Array.isArray(parsed) ? parsed : [parsed]
  } catch {
    // 如果不是JSON格式，尝试按逗号分隔或直接返回
    if (docUrls.includes(',')) {
      return docUrls.split(',').map(url => url.trim()).filter(url => url)
    }
    return docUrls ? [docUrls] : []
  }
}

const viewOrder = (order) => {
  currentOrder.value = order
  showDetailModal.value = true
}

const auditOrder = (order) => {
  dialog.warning({
    title: '审核确认',
    content: `确定要通过订单 "${order.title}" 的审核吗？`,
    positiveText: '通过',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await apiAuditOrder(order.id, { approved: true })
        message.success(`订单 ${order.orderNo} 审核通过`)
        await loadOrders()
        await loadStats()
      } catch (e) {
        message.error('操作失败')
      }
    }
  })
}

const rejectOrder = (order) => {
  dialog.error({
    title: '驳回确认',
    content: `确定要驳回订单 "${order.title}" 吗？`,
    positiveText: '确定驳回',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await apiAuditOrder(order.id, { approved: false, reason: '审核未通过' })
        showDetailModal.value = false
        message.warning(`订单 ${order.orderNo} 已驳回`)
        await loadOrders()
        await loadStats()
      } catch (e) {
        message.error('操作失败')
      }
    }
  })
}

const handleExport = () => {
  message.success('订单数据导出中...')
}

// 点击统计卡片快速筛选
const quickFilter = (status) => {
  filterStatus.value = status
}

// 分页切换
const handlePageChange = (page) => {
  currentPage.value = page
  loadOrders()
}

// 监听筛选条件变化
watch([filterStatus, searchKeyword, filterCategory], () => {
  currentPage.value = 1
  loadOrders()
})

onMounted(() => {
  loadStats()
  loadOrders()
})
</script>
