<template>
  <div class="space-y-6">
    <!-- 筛选栏 -->
    <div class="bg-white p-4 rounded-2xl border border-gray-100 flex items-center justify-between">
      <div class="flex items-center gap-4">
        <n-input v-model:value="searchKeyword" placeholder="搜索用户名/手机号" class="w-64">
          <template #prefix><i class="fas fa-search text-gray-400"></i></template>
        </n-input>
        <n-select 
          v-model:value="filterType" 
          :options="typeOptions"
          class="w-36"
          placeholder="全部类型"
          clearable
        />
        <n-select 
          v-model:value="filterStatus" 
          :options="statusOptions"
          class="w-36"
          placeholder="全部状态"
          clearable
        />
      </div>
      <n-button ghost @click="exportUsers" :loading="exporting">
        <template #icon><i class="fas fa-download"></i></template>
        导出
      </n-button>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="bg-white p-5 rounded-xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">总用户数</p>
        <p class="text-2xl font-black text-gray-800 mt-1">{{ stats.total.toLocaleString() }}</p>
      </div>
      <div class="bg-white p-5 rounded-xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">今日新增</p>
        <p class="text-2xl font-black text-green-500 mt-1">+{{ stats.todayNew }}</p>
      </div>
      <div class="bg-white p-5 rounded-xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">活跃用户</p>
        <p class="text-2xl font-black text-tf mt-1">{{ stats.active.toLocaleString() }}</p>
      </div>
      <div class="bg-white p-5 rounded-xl border border-gray-100">
        <p class="text-xs text-gray-400 font-bold">已禁用</p>
        <p class="text-2xl font-black text-red-500 mt-1">{{ stats.disabled }}</p>
      </div>
    </div>

    <!-- 用户列表 -->
    <div class="bg-white rounded-2xl border border-gray-100 overflow-hidden">
      <table class="w-full text-left">
        <thead class="bg-gray-50 border-b border-gray-100">
          <tr class="text-xs text-gray-500 uppercase">
            <th class="px-6 py-4 font-bold">用户信息</th>
            <th class="px-6 py-4 font-bold">类型</th>
            <th class="px-6 py-4 font-bold">注册时间</th>
            <th class="px-6 py-4 font-bold text-right">账户余额</th>
            <th class="px-6 py-4 font-bold text-center">订单数</th>
            <th class="px-6 py-4 font-bold text-center">状态</th>
            <th class="px-6 py-4 font-bold text-center">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="user in users" :key="user.id" class="hover:bg-gray-50 transition">
            <td class="px-6 py-5">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center text-gray-400">
                  <i class="fas fa-user"></i>
                </div>
                <div>
                  <p class="font-bold text-gray-800">{{ user.nickname || user.username }}</p>
                  <p class="text-xs text-gray-400">{{ user.phone }}</p>
                </div>
              </div>
            </td>
            <td class="px-6 py-5">
              <span class="text-xs font-bold px-2 py-1 rounded" :class="user.type === 1 ? 'bg-blue-100 text-blue-500' : 'bg-purple-100 text-purple-500'">
                {{ user.type === 1 ? '普通用户' : '服务商' }}
              </span>
            </td>
            <td class="px-6 py-5 text-sm text-gray-500">{{ user.createTime }}</td>
            <td class="px-6 py-5 text-right font-bold text-gray-800">¥{{ user.balance.toLocaleString() }}</td>
            <td class="px-6 py-5 text-center text-gray-600">{{ user.orderCount }}</td>
            <td class="px-6 py-5 text-center">
              <span class="text-xs font-bold px-3 py-1 rounded-full" :class="user.status === 1 ? 'bg-green-100 text-green-500' : 'bg-red-100 text-red-500'">
                {{ user.status === 1 ? '正常' : '禁用' }}
              </span>
            </td>
            <td class="px-6 py-5 text-center">
              <n-button size="small" ghost @click="viewUser(user)">详情</n-button>
              <n-button 
                v-if="user.status === 1" 
                size="small" 
                type="error" 
                ghost 
                class="ml-2"
                @click="disableUser(user)"
              >
                禁用
              </n-button>
              <n-button 
                v-else 
                size="small" 
                type="primary" 
                color="#00AFE1" 
                class="ml-2"
                @click="enableUser(user)"
              >
                启用
              </n-button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 分页 -->
      <div class="p-4 border-t border-gray-100 flex justify-center">
        <n-pagination 
          v-model:page="currentPage" 
          :page-count="totalPages"
          @update:page="handlePageChange"
        />
      </div>
    </div>

    <!-- 用户详情弹窗 -->
    <n-modal v-model:show="showDetailModal" preset="card" title="用户详情" style="width: 800px; max-width: 90vw;">
      <n-spin :show="detailLoading">
        <div v-if="currentUser" class="space-y-6">
          <!-- 基本信息 -->
          <div class="flex items-start gap-6 p-5 bg-gray-50 rounded-xl">
            <div class="w-20 h-20 bg-white rounded-2xl flex items-center justify-center text-3xl text-gray-400 border overflow-hidden">
              <img v-if="currentUser.avatar" :src="currentUser.avatar" class="w-full h-full object-cover" />
              <i v-else class="fas fa-user"></i>
            </div>
            <div class="flex-1">
              <div class="flex items-center gap-3">
                <h3 class="text-xl font-black text-gray-800">{{ currentUser.nickname || '未设置昵称' }}</h3>
                <span class="text-xs font-bold px-2 py-1 rounded" :class="currentUser.isMerchant ? 'bg-purple-100 text-purple-500' : 'bg-blue-100 text-blue-500'">
                  {{ currentUser.isMerchant ? '服务商' : '普通用户' }}
                </span>
                <span 
                  class="text-xs font-bold px-2 py-1 rounded"
                  :class="currentUser.status === 1 ? 'bg-green-100 text-green-500' : 'bg-red-100 text-red-500'"
                >
                  {{ currentUser.status === 1 ? '正常' : '已禁用' }}
                </span>
              </div>
              <p class="text-gray-500 mt-1">
                <i class="fas fa-phone mr-2"></i>{{ currentUser.phone || '未绑定手机' }}
              </p>
              <div class="flex items-center gap-4 mt-3">
                <span class="px-3 py-1 bg-cyan-100 text-cyan-600 rounded-full text-xs font-bold">
                  信用分: {{ currentUser.creditScore || 600 }}
                </span>
                <span v-if="currentUser.verified" class="px-3 py-1 bg-green-100 text-green-600 rounded-full text-xs font-bold">
                  <i class="fas fa-check-circle mr-1"></i>已实名认证
                </span>
                <span v-else class="px-3 py-1 bg-gray-100 text-gray-500 rounded-full text-xs font-bold">
                  <i class="fas fa-times-circle mr-1"></i>未实名认证
                </span>
              </div>
            </div>
          </div>

          <!-- 账户信息 -->
          <div>
            <p class="text-sm font-bold text-gray-700 mb-3">
              <i class="fas fa-wallet mr-2 text-cyan-500"></i>账户信息
            </p>
            <div class="grid grid-cols-3 gap-4">
              <div class="p-4 bg-gray-50 rounded-xl">
                <p class="text-xs text-gray-400 mb-1">可用余额</p>
                <p class="text-xl font-black text-gray-800">¥{{ (currentUser.balance || 0).toLocaleString() }}</p>
              </div>
              <div class="p-4 bg-gray-50 rounded-xl">
                <p class="text-xs text-gray-400 mb-1">冻结金额</p>
                <p class="text-xl font-black text-orange-500">¥{{ (currentUser.frozenAmount || 0).toLocaleString() }}</p>
              </div>
              <div class="p-4 bg-gray-50 rounded-xl">
                <p class="text-xs text-gray-400 mb-1">信用评分</p>
                <p class="text-xl font-black" :class="(currentUser.creditScore || 600) >= 700 ? 'text-green-500' : (currentUser.creditScore || 600) >= 500 ? 'text-orange-500' : 'text-red-500'">
                  {{ currentUser.creditScore || 600 }}
                </p>
              </div>
            </div>
          </div>

          <!-- 个人信息 -->
          <div>
            <p class="text-sm font-bold text-gray-700 mb-3">
              <i class="fas fa-user-circle mr-2 text-cyan-500"></i>个人信息
            </p>
            <div class="grid grid-cols-3 gap-4">
              <div class="p-4 bg-gray-50 rounded-xl">
                <p class="text-xs text-gray-400 mb-1">用户ID</p>
                <p class="font-bold">{{ currentUser.id }}</p>
              </div>
              <div class="p-4 bg-gray-50 rounded-xl">
                <p class="text-xs text-gray-400 mb-1">性别</p>
                <p class="font-bold">{{ currentUser.gender === 1 ? '男' : currentUser.gender === 2 ? '女' : '未设置' }}</p>
              </div>
              <div class="p-4 bg-gray-50 rounded-xl">
                <p class="text-xs text-gray-400 mb-1">邮箱</p>
                <p class="font-bold">{{ currentUser.email || '未绑定' }}</p>
              </div>
              <div class="p-4 bg-gray-50 rounded-xl">
                <p class="text-xs text-gray-400 mb-1">真实姓名</p>
                <p class="font-bold">{{ currentUser.realName || '未实名' }}</p>
              </div>
              <div class="p-4 bg-gray-50 rounded-xl">
                <p class="text-xs text-gray-400 mb-1">身份证号</p>
                <p class="font-bold">{{ currentUser.idCard ? maskIdCard(currentUser.idCard) : '未绑定' }}</p>
              </div>
              <div class="p-4 bg-gray-50 rounded-xl">
                <p class="text-xs text-gray-400 mb-1">注册来源</p>
                <p class="font-bold">{{ getRegisterSourceText(currentUser.registerSource) }}</p>
              </div>
            </div>
          </div>

          <!-- 登录信息 -->
          <div>
            <p class="text-sm font-bold text-gray-700 mb-3">
              <i class="fas fa-sign-in-alt mr-2 text-cyan-500"></i>登录信息
            </p>
            <div class="grid grid-cols-2 gap-4">
              <div class="p-4 bg-gray-50 rounded-xl">
                <p class="text-xs text-gray-400 mb-1">最后登录时间</p>
                <p class="font-bold">{{ currentUser.lastLoginTime || '从未登录' }}</p>
              </div>
              <div class="p-4 bg-gray-50 rounded-xl">
                <p class="text-xs text-gray-400 mb-1">最后登录IP</p>
                <p class="font-bold">{{ currentUser.lastLoginIp || '-' }}</p>
              </div>
              <div class="p-4 bg-gray-50 rounded-xl">
                <p class="text-xs text-gray-400 mb-1">注册时间</p>
                <p class="font-bold">{{ currentUser.createTime || '-' }}</p>
              </div>
              <div class="p-4 bg-gray-50 rounded-xl">
                <p class="text-xs text-gray-400 mb-1">更新时间</p>
                <p class="font-bold">{{ currentUser.updateTime || '-' }}</p>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="flex gap-3 justify-end pt-4 border-t">
            <n-button @click="showDetailModal = false">关闭</n-button>
            <n-button 
              v-if="currentUser.status === 1" 
              type="error"
              ghost
              @click="disableUserInModal"
            >
              禁用账号
            </n-button>
            <n-button 
              v-else 
              type="primary"
              color="#00AFE1"
              @click="enableUserInModal"
            >
              启用账号
            </n-button>
          </div>
        </div>
      </n-spin>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useMessage } from 'naive-ui'
import { getUserList, getUserStats, getUserDetail, updateUserStatus } from '@/api/admin'

const message = useMessage()

const loading = ref(false)
const detailLoading = ref(false)
const searchKeyword = ref('')
const filterType = ref(null)
const filterStatus = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const showDetailModal = ref(false)
const currentUser = ref(null)

const typeOptions = [
  { label: '普通用户', value: 1 },
  { label: '服务商', value: 2 }
]

const statusOptions = [
  { label: '正常', value: 1 },
  { label: '禁用', value: 0 }
]

const stats = reactive({
  total: 0,
  todayNew: 0,
  active: 0,
  disabled: 0
})

const users = ref([])
const exporting = ref(false)

// 导出用户数据
const exportUsers = async () => {
  exporting.value = true
  try {
    // 获取所有用户数据（不分页）
    const res = await getUserList({ page: 1, size: 10000 })
    if (res.data && res.data.records) {
      const data = res.data.records
      
      // 生成CSV内容
      const headers = ['用户ID', '昵称', '手机号', '类型', '账户余额', '状态', '注册时间']
      const rows = data.map(u => [
        u.id,
        u.nickname || '未设置',
        u.phone || '-',
        u.isMerchant ? '服务商' : '普通用户',
        u.balance || 0,
        u.status === 1 ? '正常' : '禁用',
        formatDate(u.createTime)
      ])
      
      // 添加BOM以支持中文
      const BOM = '\uFEFF'
      const csvContent = BOM + headers.join(',') + '\n' + rows.map(row => row.join(',')).join('\n')
      
      // 创建下载链接
      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = `用户列表_${new Date().toLocaleDateString().replace(/\//g, '-')}.csv`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
      message.success('导出成功')
    }
  } catch (e) {
    console.error('导出失败', e)
    message.error('导出失败')
  } finally {
    exporting.value = false
  }
}

// 加载用户统计
const loadStats = async () => {
  try {
    const res = await getUserStats()
    if (res.data) {
      stats.total = res.data.total || 0
      stats.todayNew = res.data.todayNew || 0
      stats.active = res.data.active || 0
      stats.disabled = res.data.disabled || 0
    }
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined
    })
    if (res.data && res.data.records) {
      users.value = res.data.records.map(u => ({
        id: u.id,
        username: u.phone || '',
        nickname: u.nickname || '未设置昵称',
        phone: u.phone ? u.phone.substring(0, 3) + '****' + u.phone.substring(7) : '-',
        type: u.isMerchant ? 2 : 1,
        balance: u.balance || 0,
        orderCount: 0,
        status: u.status,
        createTime: formatDate(u.createTime)
      }))
      totalPages.value = Math.max(1, Math.ceil(res.data.total / pageSize.value))
    }
  } catch (e) {
    console.error('加载用户列表失败', e)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
}

// 身份证号脱敏
const maskIdCard = (idCard) => {
  if (!idCard || idCard.length < 10) return idCard
  return idCard.substring(0, 4) + '**********' + idCard.substring(idCard.length - 4)
}

// 注册来源文字
const getRegisterSourceText = (source) => {
  const map = {
    'phone': '手机注册',
    'wechat': '微信登录',
    'admin': '后台创建'
  }
  return map[source] || source || '未知'
}

// 查看用户详情
const viewUser = async (user) => {
  showDetailModal.value = true
  detailLoading.value = true
  currentUser.value = null
  
  try {
    const res = await getUserDetail(user.id)
    if (res.data) {
      currentUser.value = {
        ...res.data,
        createTime: formatDateTime(res.data.createTime),
        updateTime: formatDateTime(res.data.updateTime),
        lastLoginTime: formatDateTime(res.data.lastLoginTime)
      }
    }
  } catch (e) {
    message.error('获取用户详情失败')
    console.error('获取用户详情失败', e)
  } finally {
    detailLoading.value = false
  }
}

const disableUser = async (user) => {
  try {
    await updateUserStatus(user.id, { status: 0 })
    user.status = 0
    message.warning(`用户 ${user.nickname} 已禁用`)
    await loadStats()
  } catch (e) {
    message.error('操作失败')
  }
}

const enableUser = async (user) => {
  try {
    await updateUserStatus(user.id, { status: 1 })
    user.status = 1
    message.success(`用户 ${user.nickname} 已启用`)
    await loadStats()
  } catch (e) {
    message.error('操作失败')
  }
}

// 弹窗内禁用用户
const disableUserInModal = async () => {
  if (!currentUser.value) return
  try {
    await updateUserStatus(currentUser.value.id, { status: 0 })
    currentUser.value.status = 0
    // 更新列表中的对应用户
    const userInList = users.value.find(u => u.id === currentUser.value.id)
    if (userInList) userInList.status = 0
    message.warning(`用户 ${currentUser.value.nickname || '未知'} 已禁用`)
    await loadStats()
  } catch (e) {
    message.error('操作失败')
  }
}

// 弹窗内启用用户
const enableUserInModal = async () => {
  if (!currentUser.value) return
  try {
    await updateUserStatus(currentUser.value.id, { status: 1 })
    currentUser.value.status = 1
    // 更新列表中的对应用户
    const userInList = users.value.find(u => u.id === currentUser.value.id)
    if (userInList) userInList.status = 1
    message.success(`用户 ${currentUser.value.nickname || '未知'} 已启用`)
    await loadStats()
  } catch (e) {
    message.error('操作失败')
  }
}

// 分页切换
const handlePageChange = (page) => {
  currentPage.value = page
  loadUsers()
}

// 监听筛选条件变化
watch([searchKeyword], () => {
  currentPage.value = 1
  loadUsers()
})

onMounted(() => {
  loadStats()
  loadUsers()
})
</script>
