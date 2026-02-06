<template>
  <div class="space-y-6">
    <!-- 搜索和筛选 -->
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <n-input 
          v-model:value="searchKeyword"
          placeholder="搜索任务关键词"
          class="w-64"
        >
          <template #prefix><i class="fas fa-search text-gray-400"></i></template>
        </n-input>
        <n-select 
          v-model:value="selectedCategory" 
          :options="categorySelectOptions"
          placeholder="选择分类"
          clearable
          filterable
          class="w-48"
        >
          <template #empty>
            <div class="text-gray-400 text-sm p-2">暂无分类</div>
          </template>
        </n-select>
        <n-tag v-if="selectedCategory !== 'all' && selectedCategoryLabel" closable @close="selectedCategory = 'all'" type="info">
          {{ selectedCategoryLabel }}
        </n-tag>
      </div>
      <div class="flex items-center gap-3">
        <span class="text-gray-400 text-sm">排序:</span>
        <n-select 
          v-model:value="sortBy" 
          :options="sortOptions"
          class="w-32"
        />
      </div>
    </div>

    <!-- 任务列表 -->
    <div class="space-y-4">
      <div 
        v-for="task in filteredTasks" 
        :key="task.id"
        class="bg-slate-800 rounded-3xl p-6 border border-slate-700 hover:border-tf/50 transition cursor-pointer group"
      >
        <div class="flex items-start justify-between mb-4">
          <div class="flex items-start gap-4">
            <div class="w-14 h-14 bg-slate-700 rounded-2xl flex items-center justify-center">
              <i :class="task.icon" class="text-2xl text-tf"></i>
            </div>
            <div>
              <div class="flex items-center gap-3 mb-2">
                <h3 class="font-bold text-white text-lg group-hover:text-tf transition">{{ task.title }}</h3>
                <span 
                  class="text-xs font-bold px-2 py-1 rounded"
                  :class="task.tagClass"
                >
                  {{ task.category }}
                </span>
                <span v-if="task.isUrgent" class="text-xs font-bold px-2 py-1 rounded bg-red-500/20 text-red-400">
                  <i class="fas fa-fire mr-1"></i>急需
                </span>
              </div>
              <p class="text-gray-400 text-sm">发布于 {{ task.publishTime }} | 甲方: {{ task.buyerName }}</p>
            </div>
          </div>
          <div class="text-right">
            <p class="text-2xl font-black text-tf">¥ {{ task.budget.toLocaleString() }}</p>
            <p class="text-xs text-gray-400 mt-1">预算金额</p>
          </div>
        </div>

        <p class="text-gray-300 text-sm leading-relaxed mb-4 line-clamp-2">{{ task.description }}</p>

        <div class="flex items-center justify-between pt-4 border-t border-slate-700">
          <div class="flex items-center gap-6 text-xs text-gray-400">
            <span><i class="far fa-clock mr-1"></i>交付周期: {{ task.deliveryDays }}天</span>
            <span><i class="fas fa-shield-alt mr-1 text-tf"></i>信托保障</span>
            <span v-if="task.techStack" class="text-tf"><i class="fas fa-code mr-1"></i>{{ task.techStack }}</span>
          </div>
          <div class="flex items-center gap-3">
            <n-button size="small" ghost @click.stop="showDetail(task)">查看详情</n-button>
            <n-button type="primary" color="#00AFE1" size="small" class="font-bold" @click.stop="handleBid(task)">
              立即接单
            </n-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredTasks.length === 0" class="bg-slate-800 rounded-3xl p-16 text-center border border-slate-700">
        <div class="w-20 h-20 bg-slate-700 rounded-full flex items-center justify-center mx-auto mb-4">
          <i class="fas fa-search text-3xl text-gray-500"></i>
        </div>
        <p class="text-gray-400 font-bold">暂无匹配的任务</p>
        <p class="text-gray-500 text-sm mt-2">试试修改筛选条件</p>
      </div>
    </div>

    <!-- 任务详情弹窗 -->
    <n-modal v-model:show="showDetailModal" preset="card" title="任务详情" class="max-w-2xl">
      <div v-if="detailTask" class="space-y-6">
        <!-- 基本信息 -->
        <div class="p-4 bg-slate-700 rounded-xl">
          <div class="flex items-start justify-between mb-3">
            <div>
              <h3 class="font-bold text-white text-xl mb-2">{{ detailTask.title }}</h3>
              <div class="flex items-center gap-3">
                <span class="text-xs font-bold px-2 py-1 rounded" :class="detailTask.tagClass">{{ detailTask.category }}</span>
                <span class="text-gray-400 text-sm">发布于 {{ detailTask.publishTime }}</span>
              </div>
            </div>
            <div class="text-right">
              <p class="text-2xl font-black text-tf">¥ {{ detailTask.budget.toLocaleString() }}</p>
              <p class="text-xs text-gray-400">预算金额</p>
            </div>
          </div>
        </div>

        <!-- 技术栈 -->
        <div v-if="detailTask.techStack" class="p-4 bg-slate-700/50 rounded-xl">
          <h4 class="text-sm font-bold text-white mb-2"><i class="fas fa-code mr-2 text-tf"></i>技术栈要求</h4>
          <p class="text-gray-300">{{ detailTask.techStack }}</p>
        </div>

        <!-- 需求简述 -->
        <div class="p-4 bg-slate-700/50 rounded-xl">
          <h4 class="text-sm font-bold text-white mb-2"><i class="fas fa-file-alt mr-2 text-tf"></i>需求简述</h4>
          <p class="text-gray-300 whitespace-pre-wrap">{{ detailTask.description }}</p>
        </div>

        <!-- 功能描述 -->
        <div v-if="detailTask.features" class="p-4 bg-slate-700/50 rounded-xl">
          <h4 class="text-sm font-bold text-white mb-2"><i class="fas fa-list-check mr-2 text-tf"></i>功能描述 / 详细需求</h4>
          <p class="text-gray-300 whitespace-pre-wrap">{{ detailTask.features }}</p>
        </div>

        <!-- 开发文档 -->
        <div v-if="detailTask.docUrls && detailTask.docUrls.length > 0" class="p-4 bg-slate-700/50 rounded-xl">
          <h4 class="text-sm font-bold text-white mb-3"><i class="fas fa-paperclip mr-2 text-tf"></i>开发文档 / 参考资料</h4>
          <div class="space-y-2">
            <a 
              v-for="(url, index) in detailTask.docUrls" 
              :key="index"
              :href="url"
              target="_blank"
              class="flex items-center gap-3 p-3 bg-slate-600/50 rounded-lg hover:bg-slate-600 transition"
            >
              <i :class="getFileIconByUrl(url)" class="text-lg"></i>
              <span class="text-gray-300 text-sm truncate flex-1">{{ getFileNameFromUrl(url) }}</span>
              <i class="fas fa-external-link-alt text-gray-400"></i>
            </a>
          </div>
        </div>

        <!-- 项目信息 -->
        <div class="grid grid-cols-2 gap-4">
          <div class="p-4 bg-slate-700/50 rounded-xl">
            <p class="text-xs text-gray-400 mb-1">甲方</p>
            <p class="text-white font-bold">{{ detailTask.buyerName }}</p>
          </div>
          <div class="p-4 bg-slate-700/50 rounded-xl">
            <p class="text-xs text-gray-400 mb-1">交付周期</p>
            <p class="text-white font-bold">{{ detailTask.deliveryDays }} 天</p>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="pt-4">
          <n-button 
            block 
            type="primary" 
            color="#00AFE1" 
            size="large" 
            class="font-bold h-12 text-base"
            @click="handleBidFromDetail"
          >
            <template #icon><i class="fas fa-hand-pointer mr-1"></i></template>
            立即接单
          </n-button>
        </div>
      </div>
    </n-modal>

    <!-- 接单确认弹窗 -->
    <n-modal v-model:show="showBidModal" preset="card" title="确认接单" class="max-w-lg">
      <div v-if="selectedTask" class="space-y-6">
        <div class="p-4 bg-slate-700 rounded-xl">
          <h4 class="font-bold text-white mb-2">{{ selectedTask.title }}</h4>
          <div class="flex items-center justify-between text-sm">
            <span class="text-gray-400">预算金额</span>
            <span class="text-tf font-bold">¥ {{ selectedTask.budget.toLocaleString() }}</span>
          </div>
        </div>

        <div class="space-y-3">
          <div class="flex items-start gap-3 text-sm text-gray-300">
            <i class="fas fa-check-circle text-green-400 mt-1"></i>
            <span>接单后，甲方将把资金托管至平台信托账户</span>
          </div>
          <div class="flex items-start gap-3 text-sm text-gray-300">
            <i class="fas fa-check-circle text-green-400 mt-1"></i>
            <span>按阶段交付验收，验收通过后资金自动释放</span>
          </div>
          <div class="flex items-start gap-3 text-sm text-gray-300">
            <i class="fas fa-check-circle text-green-400 mt-1"></i>
            <span>如有纠纷，平台提供公正仲裁服务</span>
          </div>
        </div>

        <div class="p-3 bg-yellow-500/10 rounded-lg text-xs text-yellow-400">
          <i class="fas fa-exclamation-triangle mr-1"></i>
          接单后请在约定时间内完成交付，逾期将影响信用评分
        </div>

        <div class="flex gap-3">
          <n-button size="large" class="px-8" @click="showBidModal = false">取消</n-button>
          <n-button 
            type="primary" 
            color="#00AFE1" 
            size="large" 
            class="font-bold flex-1 h-11"
            :loading="accepting" 
            @click="confirmBid"
          >
            <template #icon><i class="fas fa-check mr-1"></i></template>
            确认接单
          </n-button>
        </div>
      </div>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { useRouter } from 'vue-router'
import { getOrderList, acceptOrder, getOrderDetail } from '@/api/order'
import { useUserStore } from '@/stores/user'
import { getBusinessCategories } from '@/api/auth'

const message = useMessage()
const router = useRouter()
const userStore = useUserStore()

const searchKeyword = ref('')
const selectedCategory = ref('all')
const sortBy = ref('newest')
const showBidModal = ref(false)
const showDetailModal = ref(false)
const selectedTask = ref(null)
const detailTask = ref(null)
const loading = ref(false)
const accepting = ref(false)

// 所有分类数据（从后端加载）
const allCategoriesData = ref([])

// 扁平化分类映射（id -> 分类信息）
const categoryMap = ref({})

// 根据用户主营业务动态生成分类选项
const categories = computed(() => {
  const baseCategories = [{ label: '全部任务', value: 'all' }]
  
  // 获取用户的主营业务
  const user = userStore.userInfo
  const businessCategories = user?.businessCategories
  
  if (businessCategories && Object.keys(categoryMap.value).length > 0) {
    // 用户设置了主营业务，只显示用户选择的分类
    const categoryIds = businessCategories.split(',').map(Number).filter(n => !isNaN(n))
    if (categoryIds.length > 0) {
      categoryIds.forEach(id => {
        const cat = categoryMap.value[id]
        if (cat) {
          baseCategories.push({
            label: cat.name,
            value: id
          })
        }
      })
      return baseCategories
    }
  }
  
  // 用户未设置主营业务或分类未加载，显示二级分类作为筛选
  if (allCategoriesData.value.length > 0) {
    allCategoriesData.value.forEach(level1 => {
      if (level1.children) {
        level1.children.forEach(level2 => {
          baseCategories.push({
            label: level2.name,
            value: level2.id
          })
        })
      }
    })
    return baseCategories
  }
  
  // 默认分类（兼容旧数据）
  return [
    { label: '全部任务', value: 'all' },
    { label: 'UI设计', value: 201 },
    { label: '前端开发', value: 102 },
    { label: '后端开发', value: 101 },
    { label: '小程序开发', value: 104 },
    { label: '其他', value: 5 }
  ]
})

// 下拉选择器的分组选项格式
const categorySelectOptions = computed(() => {
  const user = userStore.userInfo
  const businessCategories = user?.businessCategories
  
  // 基础选项
  const options = [{ label: '全部任务', value: 'all' }]
  
  // 用户设置了主营业务
  if (businessCategories && Object.keys(categoryMap.value).length > 0) {
    const categoryIds = businessCategories.split(',').map(Number).filter(n => !isNaN(n))
    if (categoryIds.length > 0) {
      categoryIds.forEach(id => {
        const cat = categoryMap.value[id]
        if (cat) {
          options.push({
            label: cat.fullName || cat.name,
            value: id
          })
        }
      })
      return options
    }
  }
  
  // 用户未设置主营业务，按一级分类分组显示
  if (allCategoriesData.value.length > 0) {
    allCategoriesData.value.forEach(level1 => {
      if (level1.children && level1.children.length > 0) {
        options.push({
          type: 'group',
          label: level1.name,
          key: level1.id,
          children: level1.children.map(level2 => ({
            label: level2.name,
            value: level2.id
          }))
        })
      }
    })
    return options
  }
  
  return options
})

// 获取当前选中分类的标签名
const selectedCategoryLabel = computed(() => {
  if (selectedCategory.value === 'all') return ''
  const cat = categoryMap.value[selectedCategory.value]
  return cat ? cat.name : ''
})

// 加载分类数据
const loadCategories = async () => {
  try {
    const res = await getBusinessCategories()
    if (res.data) {
      allCategoriesData.value = res.data
      // 构建扁平化映射
      const flatMap = {}
      const flatten = (cats, parentName = '') => {
        cats.forEach(cat => {
          flatMap[cat.id] = {
            ...cat,
            fullName: parentName ? `${parentName} / ${cat.name}` : cat.name
          }
          if (cat.children) {
            flatten(cat.children, cat.name)
          }
        })
      }
      flatten(res.data)
      categoryMap.value = flatMap
    }
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

const sortOptions = [
  { label: '最新发布', value: 'newest' },
  { label: '金额最高', value: 'amount_desc' },
  { label: '金额最低', value: 'amount_asc' },
  { label: '即将截止', value: 'deadline' }
]

// 任务列表数据
const tasks = ref([])

// 加载待接单任务
const loadTasks = async () => {
  loading.value = true
  try {
    // status: 0 表示待接单状态
    const res = await getOrderList({ status: 0, size: 50 })
    if (res.data && res.data.records) {
      tasks.value = res.data.records.map(order => ({
        id: order.id,
        title: order.title,
        description: order.description || '',
        techStack: order.techStack || '',
        features: order.features || '',
        docUrls: order.docUrls ? order.docUrls.split(',').filter(u => u) : [],
        category: getCategoryLabel(order.categoryId),
        categoryValue: getCategoryValue(order.categoryId),
        categoryId: order.categoryId,
        icon: getCategoryIcon(order.categoryId),
        tagClass: getCategoryTagClass(order.categoryId),
        budget: order.totalAmount || 0,
        deliveryDays: calcDeliveryDays(order.startTime, order.deliveryTime),
        publishTime: formatPublishTime(order.createTime),
        buyerName: order.buyerName || '未知',
        bidsCount: order.bidsCount || 0,
        isUrgent: order.isUrgent || false
      }))
    }
  } catch (e) {
    console.error('加载任务列表失败', e)
    message.error('加载任务列表失败')
  } finally {
    loading.value = false
  }
}

// 计算交付天数
const calcDeliveryDays = (startTime, deliveryTime) => {
  if (!startTime || !deliveryTime) return 7
  const start = new Date(startTime)
  const end = new Date(deliveryTime)
  const diffMs = end - start
  return Math.max(1, Math.ceil(diffMs / (1000 * 60 * 60 * 24)))
}

// 根据分类ID获取分类标签
const getCategoryLabel = (categoryId) => {
  const cat = categoryMap.value[categoryId]
  if (cat) return cat.name
  // 兼容旧数据
  const oldMap = { 1: 'UI设计', 2: '前端开发', 3: '后端开发', 4: '小程序', 5: '其他' }
  return oldMap[categoryId] || '其他'
}

const getCategoryValue = (categoryId) => {
  // 直接返回ID
  return categoryId
}

const getCategoryIcon = (categoryId) => {
  const map = {
    1: 'fas fa-palette',
    2: 'fab fa-react',
    3: 'fab fa-java',
    4: 'fab fa-weixin',
    5: 'fas fa-cog'
  }
  return map[categoryId] || 'fas fa-tasks'
}

const getCategoryTagClass = (categoryId) => {
  const map = {
    1: 'bg-purple-500/20 text-purple-400',
    2: 'bg-cyan-500/20 text-cyan-400',
    3: 'bg-orange-500/20 text-orange-400',
    4: 'bg-green-500/20 text-green-400',
    5: 'bg-gray-500/20 text-gray-400'
  }
  return map[categoryId] || 'bg-gray-500/20 text-gray-400'
}

// 格式化发布时间
const formatPublishTime = (createTime) => {
  if (!createTime) return ''
  const now = new Date()
  const created = new Date(createTime)
  const diffMs = now - created
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))
  
  if (diffHours < 1) return '刚刚'
  if (diffHours < 24) return `${diffHours}小时前`
  if (diffDays < 30) return `${diffDays}天前`
  return created.toLocaleDateString()
}

const filteredTasks = computed(() => {
  let result = tasks.value
  
  if (selectedCategory.value !== 'all') {
    const selectedId = selectedCategory.value
    result = result.filter(t => {
      // 直接匹配分类ID
      if (t.categoryId === selectedId) return true
      // 检查是否属于选中分类的子分类
      const selectedCat = categoryMap.value[selectedId]
      if (selectedCat && selectedCat.level === 2) {
        // 如果选中的是二级分类，匹配该二级分类下的所有三级分类
        const taskCat = categoryMap.value[t.categoryId]
        if (taskCat && taskCat.parentId === selectedId) return true
      }
      return false
    })
  }
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(t => 
      t.title.toLowerCase().includes(keyword) || 
      t.description.toLowerCase().includes(keyword) ||
      (t.techStack && t.techStack.toLowerCase().includes(keyword))
    )
  }
  
  // 排序
  if (sortBy.value === 'amount_desc') {
    result = [...result].sort((a, b) => b.budget - a.budget)
  } else if (sortBy.value === 'amount_asc') {
    result = [...result].sort((a, b) => a.budget - b.budget)
  }
  
  return result
})

// 查看详情
const showDetail = (task) => {
  detailTask.value = task
  showDetailModal.value = true
}

// 从URL获取文件名
const getFileNameFromUrl = (url) => {
  if (!url) return '未知文件'
  const parts = url.split('/')
  const filename = parts[parts.length - 1]
  // 如果文件名是UUID格式，简化显示
  if (filename.length > 40) {
    const ext = filename.split('.').pop()
    return `文件.${ext}`
  }
  return filename
}

// 根据URL获取文件图标
const getFileIconByUrl = (url) => {
  if (!url) return 'fas fa-file text-gray-400'
  const ext = url.split('.').pop().toLowerCase()
  const iconMap = {
    pdf: 'fas fa-file-pdf text-red-400',
    doc: 'fas fa-file-word text-blue-400',
    docx: 'fas fa-file-word text-blue-400',
    xls: 'fas fa-file-excel text-green-400',
    xlsx: 'fas fa-file-excel text-green-400',
    txt: 'fas fa-file-alt text-gray-400',
    zip: 'fas fa-file-archive text-yellow-400',
    rar: 'fas fa-file-archive text-yellow-400',
    jpg: 'fas fa-file-image text-purple-400',
    jpeg: 'fas fa-file-image text-purple-400',
    png: 'fas fa-file-image text-purple-400',
    gif: 'fas fa-file-image text-purple-400',
    webp: 'fas fa-file-image text-purple-400'
  }
  return iconMap[ext] || 'fas fa-file text-gray-400'
}

// 从详情弹窗接单
const handleBidFromDetail = () => {
  showDetailModal.value = false
  selectedTask.value = detailTask.value
  showBidModal.value = true
}

const handleBid = (task) => {
  selectedTask.value = task
  showBidModal.value = true
}

const confirmBid = async () => {
  if (!selectedTask.value) return
  
  accepting.value = true
  try {
    await acceptOrder(selectedTask.value.id)
    message.success('接单成功！请前往拟定并签署合同')
    showBidModal.value = false
    // 刷新任务列表
    loadTasks()
    // 跳转到合同编辑页面
    router.push(`/merchant/orders/${selectedTask.value.id}/contract`)
  } catch (e) {
    console.error('接单失败', e)
    message.error(e.message || '接单失败，请稍后重试')
  } finally {
    accepting.value = false
  }
}

onMounted(() => {
  loadCategories()
  loadTasks()
})
</script>
