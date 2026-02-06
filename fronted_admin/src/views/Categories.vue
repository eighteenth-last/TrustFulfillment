<template>
  <div class="space-y-6">
    <!-- 操作栏 -->
    <div class="bg-white p-4 rounded-2xl border border-gray-100 flex items-center justify-between">
      <div class="flex items-center gap-4">
        <n-input v-model:value="searchKeyword" placeholder="搜索分类名称" class="w-64">
          <template #prefix><i class="fas fa-search text-gray-400"></i></template>
        </n-input>
      </div>
      <n-button type="primary" color="#00AFE1" @click="openAddModal(null)">
        <template #icon><i class="fas fa-plus"></i></template>
        添加一级分类
      </n-button>
    </div>

    <!-- 分类树 -->
    <div class="bg-white rounded-2xl border border-gray-100 p-6">
      <div v-if="filteredCategories.length === 0" class="text-center py-12 text-gray-400">
        <i class="fas fa-folder-open text-4xl mb-3"></i>
        <p>暂无分类数据</p>
      </div>

      <div v-for="cat1 in filteredCategories" :key="cat1.id" class="mb-6 last:mb-0">
        <!-- 一级分类 -->
        <div class="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center text-blue-500">
              <i :class="cat1.icon || 'fas fa-folder'"></i>
            </div>
            <div>
              <span class="font-bold text-gray-800">{{ cat1.name }}</span>
              <span class="ml-2 text-xs text-gray-400">ID: {{ cat1.id }}</span>
            </div>
          </div>
          <div class="flex items-center gap-2">
            <n-switch :value="cat1.status === 1" @update:value="toggleStatus(cat1)" size="small" />
            <n-button size="small" ghost @click="openAddModal(cat1)">添加子分类</n-button>
            <n-button size="small" ghost @click="editCategory(cat1)">编辑</n-button>
            <n-button size="small" type="error" ghost @click="deleteCategory(cat1)">删除</n-button>
          </div>
        </div>

        <!-- 二级分类 -->
        <div v-if="cat1.children && cat1.children.length > 0" class="ml-8 mt-2 space-y-2">
          <div v-for="cat2 in cat1.children" :key="cat2.id">
            <div class="flex items-center justify-between p-3 bg-white border border-gray-100 rounded-lg hover:border-gray-200 transition">
              <div class="flex items-center gap-3">
                <i class="fas fa-chevron-right text-gray-300"></i>
                <span class="font-medium text-gray-700">{{ cat2.name }}</span>
                <span class="text-xs text-gray-400">ID: {{ cat2.id }}</span>
              </div>
              <div class="flex items-center gap-2">
                <n-switch :value="cat2.status === 1" @update:value="toggleStatus(cat2)" size="small" />
                <n-button size="tiny" ghost @click="openAddModal(cat2)">添加子分类</n-button>
                <n-button size="tiny" ghost @click="editCategory(cat2)">编辑</n-button>
                <n-button size="tiny" type="error" ghost @click="deleteCategory(cat2)">删除</n-button>
              </div>
            </div>

            <!-- 三级分类 -->
            <div v-if="cat2.children && cat2.children.length > 0" class="ml-8 mt-1 space-y-1">
              <div v-for="cat3 in cat2.children" :key="cat3.id" 
                   class="flex items-center justify-between p-2 pl-4 bg-gray-50 rounded-lg">
                <div class="flex items-center gap-2">
                  <i class="fas fa-minus text-gray-300 text-xs"></i>
                  <span class="text-sm text-gray-600">{{ cat3.name }}</span>
                  <span class="text-xs text-gray-400">ID: {{ cat3.id }}</span>
                </div>
                <div class="flex items-center gap-2">
                  <n-switch :value="cat3.status === 1" @update:value="toggleStatus(cat3)" size="small" />
                  <n-button size="tiny" ghost @click="editCategory(cat3)">编辑</n-button>
                  <n-button size="tiny" type="error" ghost @click="deleteCategory(cat3)">删除</n-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑弹窗 -->
    <n-modal v-model:show="showModal" preset="card" :title="modalTitle" style="width: 500px;">
      <n-form :model="formData" label-placement="left" label-width="80">
        <n-form-item label="分类名称">
          <n-input v-model:value="formData.name" placeholder="请输入分类名称" />
        </n-form-item>
        <n-form-item label="图标" v-if="formData.level === 1">
          <n-input v-model:value="formData.icon" placeholder="如：fas fa-laptop-code" />
        </n-form-item>
        <n-form-item label="排序">
          <n-input-number v-model:value="formData.sortOrder" :min="0" style="width: 100%;" />
        </n-form-item>
        <n-form-item label="状态">
          <n-switch v-model:value="formData.status" :checked-value="1" :unchecked-value="0" />
        </n-form-item>
      </n-form>
      <template #footer>
        <div class="flex gap-3 justify-end">
          <n-button @click="showModal = false">取消</n-button>
          <n-button type="primary" color="#00AFE1" @click="saveCategory">保存</n-button>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useMessage, useDialog } from 'naive-ui'
import { getCategoryTree, saveCategory as apiSaveCategory, deleteCategory as apiDeleteCategory, updateCategoryStatus } from '@/api/admin'

const message = useMessage()
const dialog = useDialog()

const searchKeyword = ref('')
const showModal = ref(false)
const modalTitle = ref('添加分类')
const editingCategory = ref(null)
const parentCategory = ref(null)
const categories = ref([])

const formData = reactive({
  name: '',
  icon: '',
  sortOrder: 0,
  status: 1,
  level: 1,
  parentId: 0
})

const filteredCategories = computed(() => {
  if (!searchKeyword.value) return categories.value
  const keyword = searchKeyword.value.toLowerCase()
  return categories.value.filter(cat => {
    if (cat.name.toLowerCase().includes(keyword)) return true
    if (cat.children) {
      return cat.children.some(c2 => {
        if (c2.name.toLowerCase().includes(keyword)) return true
        if (c2.children) {
          return c2.children.some(c3 => c3.name.toLowerCase().includes(keyword))
        }
        return false
      })
    }
    return false
  })
})

const loadCategories = async () => {
  try {
    const res = await getCategoryTree()
    if (res.data) {
      categories.value = res.data
    }
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

const openAddModal = (parent) => {
  editingCategory.value = null
  parentCategory.value = parent
  const level = parent ? parent.level + 1 : 1
  Object.assign(formData, {
    name: '',
    icon: '',
    sortOrder: 0,
    status: 1,
    level: level,
    parentId: parent?.id || 0
  })
  modalTitle.value = parent ? `添加「${parent.name}」的子分类` : '添加一级分类'
  showModal.value = true
}

const editCategory = (category) => {
  editingCategory.value = category
  parentCategory.value = null
  Object.assign(formData, {
    name: category.name,
    icon: category.icon || '',
    sortOrder: category.sortOrder,
    status: category.status,
    level: category.level,
    parentId: category.parentId
  })
  modalTitle.value = '编辑分类'
  showModal.value = true
}

const saveCategory = async () => {
  if (!formData.name) {
    message.warning('请输入分类名称')
    return
  }
  try {
    await apiSaveCategory({
      id: editingCategory.value?.id,
      ...formData
    })
    message.success('保存成功')
    showModal.value = false
    loadCategories()
  } catch (e) {
    message.error('保存失败')
  }
}

const deleteCategory = (category) => {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除分类「${category.name}」吗？这将同时删除所有子分类。`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await apiDeleteCategory(category.id)
        message.success('删除成功')
        loadCategories()
      } catch (e) {
        message.error('删除失败')
      }
    }
  })
}

const toggleStatus = async (category) => {
  try {
    await updateCategoryStatus(category.id, { status: category.status === 1 ? 0 : 1 })
    category.status = category.status === 1 ? 0 : 1
  } catch (e) {
    message.error('操作失败')
  }
}

onMounted(() => {
  loadCategories()
})
</script>
