<template>
  <div class="max-w-5xl mx-auto space-y-6">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button @click="router.back()" class="w-10 h-10 bg-white rounded-full flex items-center justify-center text-gray-400 hover:text-tf hover:shadow-md transition">
          <i class="fas fa-arrow-left"></i>
        </button>
        <div>
          <h2 class="text-xl font-black text-gray-800">数字存证查验中心</h2>
          <p class="text-sm text-gray-400 mt-1">数据已通过臻托分布式存证节点同步至互联网法院司法区块链</p>
        </div>
      </div>
      <div class="flex gap-3">
        <span class="text-xs text-green-500 font-bold bg-green-50 px-3 py-1.5 rounded-full border border-green-100">
          <i class="fas fa-shield-check mr-1"></i> 司法同步正常
        </span>
      </div>
    </div>

    <!-- 合同存证 -->
    <div class="bg-white p-10 rounded-[2.5rem] shadow-sm border border-gray-100">
      <div class="flex justify-between items-start mb-10">
        <div>
          <h3 class="text-2xl font-black text-gray-800">合同与交付存证凭证</h3>
          <p class="text-gray-400 text-sm mt-1">此数据已通过臻托分布式存证节点同步至互联网法院司法区块链</p>
        </div>
        <n-button type="primary" ghost color="#00AFE1" class="font-bold">
          <template #icon><i class="fas fa-download"></i></template>
          导出取证报告
        </n-button>
      </div>

      <!-- 合同文件卡片 -->
      <div class="space-y-6">
        <div 
          v-for="(doc, index) in documents" 
          :key="index"
          class="p-6 bg-slate-50 rounded-3xl border border-dashed border-tf/20 flex justify-between items-center hover:bg-slate-100 transition cursor-pointer"
        >
          <div class="flex items-center gap-6">
            <div class="w-14 h-14 bg-white rounded-2xl flex items-center justify-center shadow-sm">
              <i :class="getFileIcon(doc.type)" class="text-2xl"></i>
            </div>
            <div>
              <p class="font-black text-gray-800">{{ doc.name }}</p>
              <p class="text-xs text-gray-400 font-mono mt-1">HASH: {{ doc.hash }}</p>
            </div>
          </div>
          <div class="flex items-center gap-4">
            <span class="text-xs text-gray-400">{{ doc.uploadTime }}</span>
            <span 
              class="text-xs font-black px-3 py-1 rounded-full shadow-lg"
              :class="doc.verified ? 'bg-green-500 text-white shadow-green-500/20' : 'bg-yellow-500 text-white shadow-yellow-500/20'"
            >
              {{ doc.verified ? '已存证' : '存证中' }}
            </span>
          </div>
        </div>
      </div>

      <!-- 存证信息详情 -->
      <div class="mt-10 grid grid-cols-3 gap-6">
        <div class="bg-gray-50 p-5 rounded-2xl">
          <p class="text-xs text-gray-400 font-bold uppercase mb-2">存证机构</p>
          <p class="text-sm font-bold text-gray-700">杭州互联网法院</p>
        </div>
        <div class="bg-gray-50 p-5 rounded-2xl">
          <p class="text-xs text-gray-400 font-bold uppercase mb-2">区块高度</p>
          <p class="text-sm font-bold text-gray-700">#{{ blockHeight.toLocaleString() }}</p>
        </div>
        <div class="bg-gray-50 p-5 rounded-2xl">
          <p class="text-xs text-gray-400 font-bold uppercase mb-2">上链时间</p>
          <p class="text-sm font-bold text-gray-700">{{ chainTime }}</p>
        </div>
      </div>
    </div>

    <!-- 交付存证时间线 -->
    <div class="bg-white p-10 rounded-[2.5rem] shadow-sm border border-gray-100">
      <h3 class="text-xl font-black text-gray-800 mb-8 flex items-center gap-2">
        <i class="fas fa-stream text-tf"></i>
        交付存证时间线
      </h3>

      <div class="relative pl-8">
        <div class="absolute left-3 top-2 bottom-2 w-0.5 bg-gray-100"></div>

        <div class="space-y-8">
          <div v-for="(record, index) in evidenceRecords" :key="index" class="relative">
            <div 
              class="absolute -left-[25px] w-4 h-4 rounded-full border-4"
              :class="record.verified ? 'bg-green-500 border-green-100' : 'bg-tf border-blue-100'"
            ></div>

            <div class="flex items-start justify-between">
              <div>
                <h4 class="font-bold text-gray-800">{{ record.title }}</h4>
                <p class="text-xs text-gray-400 mt-1">{{ record.time }}</p>
                <p v-if="record.description" class="text-sm text-gray-500 mt-2">{{ record.description }}</p>
              </div>

              <div class="text-right">
                <p class="text-xs font-mono text-gray-400">{{ record.hash }}</p>
                <a v-if="record.verified" href="#" class="text-xs text-tf hover:underline mt-1 inline-block">
                  <i class="fas fa-external-link-alt mr-1"></i>
                  查看链上记录
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 存证说明 -->
    <div class="bg-blue-50 p-6 rounded-2xl border border-blue-100">
      <h4 class="font-bold text-tf mb-3 flex items-center gap-2">
        <i class="fas fa-info-circle"></i>
        关于司法存证
      </h4>
      <div class="text-sm text-gray-600 space-y-2">
        <p>1. 所有合同和交付物均通过SHA-256算法生成唯一哈希值，并同步至杭州互联网法院司法区块链。</p>
        <p>2. 存证数据具有法律效力，可作为电子证据用于纠纷仲裁和诉讼。</p>
        <p>3. 如需获取完整存证报告，请点击"导出取证报告"按钮。</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const blockHeight = ref(19234011)
const chainTime = ref('2024-01-15 10:22:45')

const documents = ref([
  {
    name: '官方管理系统_委托合同_V2.pdf',
    type: 'pdf',
    hash: '77a1b2c3d4e5f6...e92f2',
    uploadTime: '2024-01-15 10:00',
    verified: true
  },
  {
    name: '需求规格说明书.docx',
    type: 'doc',
    hash: 'a1b2c3d4e5f6g7...h8i9j',
    uploadTime: '2024-01-15 10:05',
    verified: true
  },
  {
    name: 'UI设计稿_阶段一.zip',
    type: 'zip',
    hash: 'k1l2m3n4o5p6q7...r8s9t',
    uploadTime: '2024-01-20 14:30',
    verified: true
  }
])

const evidenceRecords = ref([
  {
    title: '合同签署完成',
    time: '2024-01-15 10:00:00',
    description: '甲乙双方完成电子合同签署',
    hash: 'HASH: 77a1b2...e92f2',
    verified: true
  },
  {
    title: '资金托管入账',
    time: '2024-01-15 10:30:00',
    description: '甲方托管资金 ¥15,000.00 已入账',
    hash: 'HASH: f8a2c1...d4e5f',
    verified: true
  },
  {
    title: '阶段一交付提交',
    time: '2024-01-20 14:30:00',
    description: '乙方提交UI设计稿',
    hash: 'HASH: k1l2m3...r8s9t',
    verified: true
  },
  {
    title: '阶段一验收通过',
    time: '2024-01-20 18:00:00',
    description: '甲方确认验收，释放资金 ¥5,000.00',
    hash: 'HASH: u1v2w3...x4y5z',
    verified: true
  }
])

const getFileIcon = (type) => {
  const map = {
    pdf: 'fas fa-file-pdf text-red-500',
    doc: 'fas fa-file-word text-blue-500',
    zip: 'fas fa-file-archive text-yellow-500',
    image: 'fas fa-file-image text-purple-500'
  }
  return map[type] || 'fas fa-file text-gray-400'
}
</script>
