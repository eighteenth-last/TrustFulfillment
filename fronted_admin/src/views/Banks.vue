<template>
  <div class="space-y-6">
    <!-- 操作栏 -->
    <div class="bg-white p-4 rounded-2xl border border-gray-100 flex items-center justify-between">
      <div class="flex items-center gap-4">
        <n-input v-model:value="searchKeyword" placeholder="搜索银行名称/代码" class="w-64">
          <template #prefix><i class="fas fa-search text-gray-400"></i></template>
        </n-input>
      </div>
      <n-button type="primary" color="#00AFE1" @click="showAddModal = true">
        <template #icon><i class="fas fa-plus"></i></template>
        添加银行
      </n-button>
    </div>

    <!-- 银行列表 -->
    <div class="bg-white rounded-2xl border border-gray-100 overflow-hidden">
      <table class="w-full text-left">
        <thead class="bg-gray-50 border-b border-gray-100">
          <tr class="text-xs text-gray-500 uppercase">
            <th class="px-6 py-4 font-bold">排序</th>
            <th class="px-6 py-4 font-bold">银行代码</th>
            <th class="px-6 py-4 font-bold">银行名称</th>
            <th class="px-6 py-4 font-bold text-center">状态</th>
            <th class="px-6 py-4 font-bold">创建时间</th>
            <th class="px-6 py-4 font-bold text-center">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="bank in filteredBanks" :key="bank.id" class="hover:bg-gray-50 transition">
            <td class="px-6 py-5 text-gray-500">{{ bank.sortOrder }}</td>
            <td class="px-6 py-5">
              <span class="font-mono text-sm font-bold text-gray-700">{{ bank.bankCode }}</span>
            </td>
            <td class="px-6 py-5">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center text-blue-500">
                  <i class="fas fa-university"></i>
                </div>
                <span class="font-bold text-gray-800">{{ bank.bankName }}</span>
              </div>
            </td>
            <td class="px-6 py-5 text-center">
              <n-switch 
                :value="bank.status === 1" 
                @update:value="toggleStatus(bank)"
              />
            </td>
            <td class="px-6 py-5 text-sm text-gray-500">{{ formatDate(bank.createTime) }}</td>
            <td class="px-6 py-5 text-center">
              <n-button size="small" ghost @click="editBank(bank)">编辑</n-button>
              <n-button size="small" type="error" ghost class="ml-2" @click="deleteBank(bank)">删除</n-button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 添加/编辑弹窗 -->
    <n-modal v-model:show="showAddModal" preset="card" :title="editingBank ? '编辑银行' : '添加银行'" style="width: 500px;">
      <n-form :model="formData" label-placement="left" label-width="80">
        <n-form-item label="银行代码">
          <n-input v-model:value="formData.bankCode" placeholder="如：ICBC" />
        </n-form-item>
        <n-form-item label="银行名称">
          <n-input v-model:value="formData.bankName" placeholder="如：中国工商银行" />
        </n-form-item>
        <n-form-item label="排序">
          <n-input-number v-model:value="formData.sortOrder" :min="0" />
        </n-form-item>
        <n-form-item label="状态">
          <n-switch v-model:value="formData.status" :checked-value="1" :unchecked-value="0" />
        </n-form-item>
      </n-form>
      <template #footer>
        <div class="flex gap-3 justify-end">
          <n-button @click="showAddModal = false">取消</n-button>
          <n-button type="primary" color="#00AFE1" @click="saveBank">保存</n-button>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useMessage, useDialog } from 'naive-ui'
import { getBankList, saveBank as apiSaveBank, deleteBank as apiDeleteBank, updateBankStatus } from '@/api/admin'

const message = useMessage()
const dialog = useDialog()

const searchKeyword = ref('')
const showAddModal = ref(false)
const editingBank = ref(null)
const banks = ref([])

const formData = reactive({
  bankCode: '',
  bankName: '',
  sortOrder: 0,
  status: 1
})

const filteredBanks = computed(() => {
  if (!searchKeyword.value) return banks.value
  const keyword = searchKeyword.value.toLowerCase()
  return banks.value.filter(b =>
    b.bankCode.toLowerCase().includes(keyword) ||
    b.bankName.toLowerCase().includes(keyword)
  )
})

const loadBanks = async () => {
  try {
    const res = await getBankList()
    if (res.data) {
      banks.value = res.data
    }
  } catch (e) {
    console.error('加载银行列表失败', e)
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const editBank = (bank) => {
  editingBank.value = bank
  Object.assign(formData, {
    bankCode: bank.bankCode,
    bankName: bank.bankName,
    sortOrder: bank.sortOrder,
    status: bank.status
  })
  showAddModal.value = true
}

const saveBank = async () => {
  if (!formData.bankCode || !formData.bankName) {
    message.warning('请填写完整信息')
    return
  }
  try {
    await apiSaveBank({
      id: editingBank.value?.id,
      ...formData
    })
    message.success('保存成功')
    showAddModal.value = false
    resetForm()
    loadBanks()
  } catch (e) {
    message.error('保存失败')
  }
}

const deleteBank = (bank) => {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除银行「${bank.bankName}」吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await apiDeleteBank(bank.id)
        message.success('删除成功')
        loadBanks()
      } catch (e) {
        message.error('删除失败')
      }
    }
  })
}

const toggleStatus = async (bank) => {
  try {
    await updateBankStatus(bank.id, { status: bank.status === 1 ? 0 : 1 })
    bank.status = bank.status === 1 ? 0 : 1
  } catch (e) {
    message.error('操作失败')
  }
}

const resetForm = () => {
  editingBank.value = null
  Object.assign(formData, { bankCode: '', bankName: '', sortOrder: 0, status: 1 })
}

onMounted(() => {
  loadBanks()
})
</script>
