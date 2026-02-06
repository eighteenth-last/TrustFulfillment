<template>
  <div class="space-y-6">
    <!-- 标签页切换 -->
    <div class="bg-white p-4 rounded-2xl border border-gray-100">
      <n-tabs v-model:value="activeTab" type="line" @update:value="handleTabChange">
        <n-tab name="merchants">已认证商户</n-tab>
        <n-tab name="applies">
          待审核申请
          <n-badge :value="stats.pending" :max="99" :offset="[10, 0]" v-if="stats.pending > 0" />
        </n-tab>
      </n-tabs>
    </div>

    <!-- 已认证商户列表 -->
    <div v-if="activeTab === 'merchants'" class="space-y-4">
      <div v-if="merchants.length === 0" class="bg-white rounded-2xl border border-gray-100 p-12 text-center">
        <i class="fas fa-store text-4xl text-gray-300 mb-3"></i>
        <p class="text-gray-400">暂无商户数据</p>
      </div>
      <div 
        v-for="merchant in merchants" 
        :key="merchant.id"
        class="bg-white rounded-2xl border border-gray-100 p-6 hover:shadow-md transition"
      >
        <div class="flex items-start justify-between">
          <div class="flex items-start gap-4">
            <div class="w-16 h-16 bg-gray-100 rounded-2xl flex items-center justify-center text-2xl text-gray-400">
              <i class="fas fa-store"></i>
            </div>
            <div>
              <div class="flex items-center gap-3">
                <h4 class="font-bold text-gray-800 text-lg">{{ merchant.name }}</h4>
                <span class="text-xs font-bold px-2 py-1 rounded bg-blue-100 text-blue-600">
                  {{ merchant.merchantType === 1 ? '个体' : '企业' }}
                </span>
                <span 
                  class="text-xs font-bold px-2 py-1 rounded"
                  :class="getStatusClass(merchant.status)"
                >
                  {{ getStatusText(merchant.status) }}
                </span>
              </div>
              <p class="text-sm text-gray-500 mt-1">{{ merchant.companyName }}</p>
              <div class="flex items-center gap-6 mt-3 text-xs text-gray-400">
                <span><i class="fas fa-id-card mr-1"></i> {{ merchant.licenseNo }}</span>
                <span><i class="fas fa-phone mr-1"></i> {{ merchant.phone }}</span>
                <span><i class="fas fa-calendar mr-1"></i> 认证时间: {{ merchant.verifyTime || merchant.applyTime }}</span>
              </div>
            </div>
          </div>

          <div class="flex items-center gap-6">
            <div class="text-center">
              <p class="text-xs text-gray-400">信用评分</p>
              <p class="text-xl font-black" :class="merchant.creditScore >= 80 ? 'text-green-500' : 'text-orange-500'">
                {{ merchant.creditScore }}
              </p>
            </div>
            <div class="text-center">
              <p class="text-xs text-gray-400">完成订单</p>
              <p class="text-xl font-black text-gray-800">{{ merchant.orderCount }}</p>
            </div>
            <div class="text-center">
              <p class="text-xs text-gray-400">提成比例</p>
              <p class="text-xl font-black text-cyan-500">{{ merchant.commissionRate }}%</p>
            </div>
            <div class="flex flex-col gap-2">
              <n-button size="small" ghost @click="viewDetail(merchant)">查看资料</n-button>
              <n-button 
                v-if="merchant.status === 1" 
                size="small" 
                type="error"
                ghost
                @click="disableMerchant(merchant)"
              >
                禁用账号
              </n-button>
              <n-button 
                v-if="merchant.status === 2" 
                size="small" 
                type="success"
                ghost
                @click="enableMerchant(merchant)"
              >
                恢复账号
              </n-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 商户申请列表 -->
    <div v-if="activeTab === 'applies'" class="space-y-4">
      <div v-if="applies.length === 0" class="bg-white rounded-2xl border border-gray-100 p-12 text-center">
        <i class="fas fa-clipboard-list text-4xl text-gray-300 mb-3"></i>
        <p class="text-gray-400">暂无申请数据</p>
      </div>
      <div 
        v-for="apply in applies" 
        :key="apply.id"
        class="bg-white rounded-2xl border border-gray-100 p-6 hover:shadow-md transition"
      >
        <div class="flex items-start justify-between">
          <div class="flex items-start gap-4">
            <div class="w-16 h-16 rounded-2xl flex items-center justify-center text-2xl"
                 :class="apply.status === 0 ? 'bg-orange-100 text-orange-500' : apply.status === 1 ? 'bg-green-100 text-green-500' : 'bg-red-100 text-red-500'">
              <i :class="apply.status === 0 ? 'fas fa-clock' : apply.status === 1 ? 'fas fa-check' : 'fas fa-times'"></i>
            </div>
            <div>
              <div class="flex items-center gap-3">
                <h4 class="font-bold text-gray-800 text-lg">{{ apply.name }}</h4>
                <span class="text-xs font-bold px-2 py-1 rounded bg-blue-100 text-blue-600">
                  {{ apply.merchantType === 1 ? '个体工商户' : '企业/组织' }}
                </span>
                <span 
                  class="text-xs font-bold px-2 py-1 rounded"
                  :class="getApplyStatusClass(apply.status)"
                >
                  {{ getApplyStatusText(apply.status) }}
                </span>
              </div>
              <p class="text-sm text-gray-500 mt-1">申请人: {{ apply.userName }} ({{ apply.userPhone }})</p>
              <div class="flex items-center gap-6 mt-3 text-xs text-gray-400">
                <span><i class="fas fa-file-alt mr-1"></i> {{ apply.applyNo }}</span>
                <span><i class="fas fa-user mr-1"></i> {{ apply.legalPerson }}</span>
                <span><i class="fas fa-phone mr-1"></i> {{ apply.phone }}</span>
                <span><i class="fas fa-calendar mr-1"></i> 申请时间: {{ apply.applyTime }}</span>
              </div>
            </div>
          </div>

          <div class="flex items-center gap-4">
            <div class="flex flex-col gap-2">
              <n-button size="small" ghost @click="viewApplyDetail(apply)">查看详情</n-button>
              <n-button 
                v-if="apply.status === 0" 
                size="small" 
                type="primary" 
                color="#00AFE1"
                @click="auditApply(apply)"
              >
                通过审核
              </n-button>
              <n-button 
                v-if="apply.status === 0" 
                size="small" 
                type="error"
                ghost
                @click="rejectApply(apply)"
              >
                拒绝申请
              </n-button>
            </div>
          </div>
        </div>

        <!-- 资质材料预览 -->
        <div v-if="apply.status === 0" class="mt-6 pt-6 border-t border-gray-100">
          <h5 class="text-xs font-bold text-gray-400 uppercase mb-4">资质材料</h5>
          <div class="grid grid-cols-3 gap-4">
            <div class="aspect-video bg-gray-100 rounded-xl overflow-hidden">
              <img v-if="apply.idCardFront" :src="apply.idCardFront" class="w-full h-full object-cover" />
              <div v-else class="w-full h-full flex flex-col items-center justify-center text-gray-400">
                <i class="fas fa-id-card text-2xl mb-2"></i>
                <span class="text-xs">身份证正面</span>
              </div>
            </div>
            <div class="aspect-video bg-gray-100 rounded-xl overflow-hidden">
              <img v-if="apply.idCardBack" :src="apply.idCardBack" class="w-full h-full object-cover" />
              <div v-else class="w-full h-full flex flex-col items-center justify-center text-gray-400">
                <i class="fas fa-id-card text-2xl mb-2"></i>
                <span class="text-xs">身份证反面</span>
              </div>
            </div>
            <div class="aspect-video bg-gray-100 rounded-xl overflow-hidden">
              <img v-if="apply.licenseImg" :src="apply.licenseImg" class="w-full h-full object-cover" />
              <div v-else class="w-full h-full flex flex-col items-center justify-center text-gray-400">
                <i class="fas fa-file-contract text-2xl mb-2"></i>
                <span class="text-xs">营业执照</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="flex justify-center">
      <n-pagination 
        v-model:page="currentPage" 
        :page-count="totalPages"
        @update:page="handlePageChange"
      />
    </div>

    <!-- 商家详情弹窗 -->
    <n-modal v-model:show="showDetailModal" preset="card" title="商家详情" style="width: 800px; max-width: 90vw;">
      <div v-if="currentMerchant" class="space-y-6">
        <!-- 基本信息 -->
        <div class="flex items-start gap-6 p-5 bg-gray-50 rounded-xl">
          <div class="w-20 h-20 bg-white rounded-2xl flex items-center justify-center text-3xl text-gray-400 border">
            <i class="fas fa-store"></i>
          </div>
          <div class="flex-1">
            <div class="flex items-center gap-3">
              <h3 class="text-xl font-black text-gray-800">{{ currentMerchant.name }}</h3>
              <span class="text-xs font-bold px-2 py-1 rounded bg-blue-100 text-blue-600">
                {{ currentMerchant.merchantType === 1 ? '个体工商户' : '企业/组织' }}
              </span>
              <span 
                class="text-xs font-bold px-2 py-1 rounded"
                :class="getStatusClass(currentMerchant.status)"
              >
                {{ getStatusText(currentMerchant.status) }}
              </span>
            </div>
            <p class="text-gray-500 mt-1">{{ currentMerchant.companyName || '暂无企业名称' }}</p>
            <div class="flex items-center gap-4 mt-3">
              <span class="px-3 py-1 bg-green-100 text-green-600 rounded-full text-xs font-bold">
                信用评分: {{ currentMerchant.creditScore }}
              </span>
              <span class="px-3 py-1 bg-blue-100 text-blue-600 rounded-full text-xs font-bold">
                完成订单: {{ currentMerchant.orderCount }}
              </span>
              <span class="px-3 py-1 bg-cyan-100 text-cyan-600 rounded-full text-xs font-bold cursor-pointer hover:bg-cyan-200 transition" @click="openEditCommission(currentMerchant)">
                <i class="fas fa-edit mr-1"></i>提成: {{ currentMerchant.commissionRate }}%
              </span>
            </div>
          </div>
        </div>

        <!-- 详细信息 -->
        <div class="grid grid-cols-3 gap-4">
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-400 mb-1">营业执照号</p>
            <p class="font-bold">{{ currentMerchant.licenseNo || '-' }}</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-400 mb-1">法人/负责人</p>
            <p class="font-bold">{{ currentMerchant.legalPerson || '-' }}</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-400 mb-1">联系电话</p>
            <p class="font-bold">{{ currentMerchant.contactPhone || currentMerchant.phone || '-' }}</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-400 mb-1">联系邮箱</p>
            <p class="font-bold">{{ currentMerchant.contactEmail || '-' }}</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-400 mb-1">申请时间</p>
            <p class="font-bold">{{ currentMerchant.applyTime || '-' }}</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-400 mb-1">认证时间</p>
            <p class="font-bold">{{ currentMerchant.verifyTime || '-' }}</p>
          </div>
        </div>

        <!-- 经营信息 -->
        <div class="grid grid-cols-1 gap-4">
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-400 mb-1">经营地址</p>
            <p class="font-bold">{{ currentMerchant.businessAddress || '未填写' }}</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-400 mb-1">经营范围</p>
            <p class="font-bold">{{ currentMerchant.businessScope || '暂无经营范围信息' }}</p>
          </div>
        </div>

        <!-- 资质材料 -->
        <div>
          <p class="text-sm font-bold mb-3">资质材料</p>
          <div class="grid grid-cols-3 gap-4">
            <div class="bg-white rounded-xl border border-gray-100 overflow-hidden">
              <p class="text-xs text-gray-500 p-3 bg-gray-50 border-b">身份证正面</p>
              <div class="aspect-[4/3]">
                <img v-if="currentMerchant.idCardFront" :src="currentMerchant.idCardFront" class="w-full h-full object-cover cursor-pointer hover:opacity-90" @click="previewImage(currentMerchant.idCardFront)" />
                <div v-else class="w-full h-full flex items-center justify-center text-gray-300">
                  <i class="fas fa-image text-4xl"></i>
                </div>
              </div>
            </div>
            <div class="bg-white rounded-xl border border-gray-100 overflow-hidden">
              <p class="text-xs text-gray-500 p-3 bg-gray-50 border-b">身份证反面</p>
              <div class="aspect-[4/3]">
                <img v-if="currentMerchant.idCardBack" :src="currentMerchant.idCardBack" class="w-full h-full object-cover cursor-pointer hover:opacity-90" @click="previewImage(currentMerchant.idCardBack)" />
                <div v-else class="w-full h-full flex items-center justify-center text-gray-300">
                  <i class="fas fa-image text-4xl"></i>
                </div>
              </div>
            </div>
            <div class="bg-white rounded-xl border border-gray-100 overflow-hidden">
              <p class="text-xs text-gray-500 p-3 bg-gray-50 border-b">营业执照</p>
              <div class="aspect-[4/3]">
                <img v-if="currentMerchant.licenseImg" :src="currentMerchant.licenseImg" class="w-full h-full object-cover cursor-pointer hover:opacity-90" @click="previewImage(currentMerchant.licenseImg)" />
                <div v-else class="w-full h-full flex items-center justify-center text-gray-300">
                  <i class="fas fa-image text-4xl"></i>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="flex gap-3 justify-end pt-4 border-t">
          <n-button @click="showDetailModal = false">关闭</n-button>
          <n-button 
            v-if="currentMerchant.status === 0" 
            type="primary" 
            color="#00AFE1"
            @click="auditMerchant(currentMerchant); showDetailModal = false"
          >
            审核通过
          </n-button>
          <n-button 
            v-if="currentMerchant.status === 1" 
            type="error"
            ghost
            @click="disableMerchant(currentMerchant); showDetailModal = false"
          >
            禁用账号
          </n-button>
          <n-button 
            v-if="currentMerchant.status === 2" 
            type="success"
            ghost
            @click="enableMerchant(currentMerchant); showDetailModal = false"
          >
            恢复账号
          </n-button>
        </div>
      </div>
    </n-modal>

    <!-- 商户申请详情弹窗 -->
    <n-modal v-model:show="showApplyDetailModal" preset="card" title="申请详情" style="width: 800px; max-width: 90vw;">
      <div v-if="currentApply" class="space-y-6">
        <!-- 申请状态 -->
        <div class="flex items-center justify-between p-5 rounded-xl"
             :class="currentApply.status === 0 ? 'bg-orange-50' : currentApply.status === 1 ? 'bg-green-50' : 'bg-red-50'">
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 rounded-full flex items-center justify-center"
                 :class="currentApply.status === 0 ? 'bg-orange-100 text-orange-500' : currentApply.status === 1 ? 'bg-green-100 text-green-500' : 'bg-red-100 text-red-500'">
              <i :class="currentApply.status === 0 ? 'fas fa-clock text-2xl' : currentApply.status === 1 ? 'fas fa-check text-2xl' : 'fas fa-times text-2xl'"></i>
            </div>
            <div>
              <h3 class="text-xl font-black text-gray-800">{{ currentApply.name }}</h3>
              <p class="text-sm text-gray-500 mt-1">
                <span class="px-2 py-0.5 rounded bg-blue-100 text-blue-600 text-xs font-bold mr-2">
                  {{ currentApply.merchantType === 1 ? '个体工商户' : '企业/组织' }}
                </span>
                <span :class="currentApply.status === 0 ? 'text-orange-500' : currentApply.status === 1 ? 'text-green-500' : 'text-red-500'">
                  {{ getApplyStatusText(currentApply.status) }}
                </span>
              </p>
            </div>
          </div>
          <div class="text-right">
            <p class="text-xs text-gray-400">申请编号</p>
            <p class="font-mono text-sm text-gray-600">{{ currentApply.applyNo }}</p>
          </div>
        </div>

        <!-- 申请人信息 -->
        <div>
          <p class="text-sm font-bold text-gray-700 mb-3">
            <i class="fas fa-user mr-2 text-cyan-500"></i>申请人信息
          </p>
          <div class="grid grid-cols-3 gap-4">
            <div class="p-4 bg-gray-50 rounded-xl">
              <p class="text-xs text-gray-400 mb-1">申请用户</p>
              <p class="font-bold">{{ currentApply.userName || '-' }}</p>
            </div>
            <div class="p-4 bg-gray-50 rounded-xl">
              <p class="text-xs text-gray-400 mb-1">用户手机</p>
              <p class="font-bold">{{ currentApply.userPhone || '-' }}</p>
            </div>
            <div class="p-4 bg-gray-50 rounded-xl">
              <p class="text-xs text-gray-400 mb-1">申请时间</p>
              <p class="font-bold">{{ currentApply.applyTime || '-' }}</p>
            </div>
          </div>
        </div>

        <!-- 商户信息 -->
        <div>
          <p class="text-sm font-bold text-gray-700 mb-3">
            <i class="fas fa-store mr-2 text-cyan-500"></i>商户信息
          </p>
          <div class="grid grid-cols-3 gap-4">
            <div class="p-4 bg-gray-50 rounded-xl">
              <p class="text-xs text-gray-400 mb-1">店铺名称</p>
              <p class="font-bold">{{ currentApply.name || '-' }}</p>
            </div>
            <div class="p-4 bg-gray-50 rounded-xl">
              <p class="text-xs text-gray-400 mb-1">企业名称</p>
              <p class="font-bold">{{ currentApply.companyName || '-' }}</p>
            </div>
            <div class="p-4 bg-gray-50 rounded-xl">
              <p class="text-xs text-gray-400 mb-1">法人/负责人</p>
              <p class="font-bold">{{ currentApply.legalPerson || '-' }}</p>
            </div>
            <div class="p-4 bg-gray-50 rounded-xl">
              <p class="text-xs text-gray-400 mb-1">营业执照号</p>
              <p class="font-bold">{{ currentApply.licenseNo || '-' }}</p>
            </div>
            <div class="p-4 bg-gray-50 rounded-xl">
              <p class="text-xs text-gray-400 mb-1">联系电话</p>
              <p class="font-bold">{{ currentApply.phone || '-' }}</p>
            </div>
            <div class="p-4 bg-gray-50 rounded-xl">
              <p class="text-xs text-gray-400 mb-1">联系邮箱</p>
              <p class="font-bold">{{ currentApply.email || '-' }}</p>
            </div>
          </div>
        </div>

        <!-- 经营信息 -->
        <div>
          <p class="text-sm font-bold text-gray-700 mb-3">
            <i class="fas fa-briefcase mr-2 text-cyan-500"></i>经营信息
          </p>
          <div class="grid grid-cols-1 gap-4">
            <div class="p-4 bg-gray-50 rounded-xl">
              <p class="text-xs text-gray-400 mb-1">经营地址</p>
              <p class="font-bold">{{ currentApply.businessAddress || '未填写' }}</p>
            </div>
            <div class="p-4 bg-gray-50 rounded-xl">
              <p class="text-xs text-gray-400 mb-1">经营范围</p>
              <p class="font-bold">{{ currentApply.businessScope || '未填写' }}</p>
            </div>
          </div>
        </div>

        <!-- 资质材料 -->
        <div>
          <p class="text-sm font-bold text-gray-700 mb-3">
            <i class="fas fa-folder-open mr-2 text-cyan-500"></i>资质材料
          </p>
          <div class="grid grid-cols-3 gap-4">
            <div class="bg-white rounded-xl border border-gray-100 overflow-hidden">
              <p class="text-xs text-gray-500 p-3 bg-gray-50 border-b">身份证正面</p>
              <div class="aspect-[4/3]">
                <img v-if="currentApply.idCardFront" :src="currentApply.idCardFront" class="w-full h-full object-cover cursor-pointer hover:opacity-90" @click="previewImage(currentApply.idCardFront)" />
                <div v-else class="w-full h-full flex items-center justify-center text-gray-300">
                  <i class="fas fa-image text-4xl"></i>
                </div>
              </div>
            </div>
            <div class="bg-white rounded-xl border border-gray-100 overflow-hidden">
              <p class="text-xs text-gray-500 p-3 bg-gray-50 border-b">身份证反面</p>
              <div class="aspect-[4/3]">
                <img v-if="currentApply.idCardBack" :src="currentApply.idCardBack" class="w-full h-full object-cover cursor-pointer hover:opacity-90" @click="previewImage(currentApply.idCardBack)" />
                <div v-else class="w-full h-full flex items-center justify-center text-gray-300">
                  <i class="fas fa-image text-4xl"></i>
                </div>
              </div>
            </div>
            <div class="bg-white rounded-xl border border-gray-100 overflow-hidden">
              <p class="text-xs text-gray-500 p-3 bg-gray-50 border-b">营业执照</p>
              <div class="aspect-[4/3]">
                <img v-if="currentApply.licenseImg" :src="currentApply.licenseImg" class="w-full h-full object-cover cursor-pointer hover:opacity-90" @click="previewImage(currentApply.licenseImg)" />
                <div v-else class="w-full h-full flex items-center justify-center text-gray-300">
                  <i class="fas fa-image text-4xl"></i>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 审核信息（如果已审核） -->
        <div v-if="currentApply.status !== 0" class="p-4 rounded-xl" :class="currentApply.status === 1 ? 'bg-green-50' : 'bg-red-50'">
          <p class="text-sm font-bold mb-2" :class="currentApply.status === 1 ? 'text-green-700' : 'text-red-700'">
            <i :class="currentApply.status === 1 ? 'fas fa-check-circle' : 'fas fa-times-circle'" class="mr-2"></i>
            {{ currentApply.status === 1 ? '审核已通过' : '审核已拒绝' }}
          </p>
          <p class="text-sm text-gray-600">审核时间: {{ currentApply.auditTime || '-' }}</p>
          <p v-if="currentApply.auditRemark" class="text-sm text-gray-600 mt-1">审核备注: {{ currentApply.auditRemark }}</p>
        </div>

        <!-- 操作按钮 -->
        <div class="flex gap-3 justify-end pt-4 border-t">
          <n-button @click="showApplyDetailModal = false">关闭</n-button>
          <n-button 
            v-if="currentApply.status === 0" 
            type="error"
            ghost
            @click="handleRejectApply"
          >
            拒绝申请
          </n-button>
          <n-button 
            v-if="currentApply.status === 0" 
            type="primary" 
            color="#00AFE1"
            @click="auditApply(currentApply); showApplyDetailModal = false"
          >
            通过审核
          </n-button>
        </div>
      </div>
    </n-modal>

    <!-- 图片预览弹窗 -->
    <n-modal v-model:show="showImagePreview" preset="card" title="图片预览" style="width: auto; max-width: 90vw;">
      <img :src="previewImageUrl" class="max-w-full max-h-[80vh] object-contain" />
    </n-modal>

    <!-- 拒绝原因弹窗 -->
    <n-modal v-model:show="showRejectModal" preset="card" title="拒绝申请" style="width: 500px;">
      <div class="space-y-4">
        <p class="text-gray-600">请填写拒绝原因，以便申请人了解并改进：</p>
        <n-input 
          v-model:value="rejectReason" 
          type="textarea" 
          placeholder="请输入拒绝原因..."
          :rows="4"
        />
      </div>
      <template #footer>
        <div class="flex gap-3 justify-end">
          <n-button @click="showRejectModal = false">取消</n-button>
          <n-button type="error" @click="confirmRejectApply">确认拒绝</n-button>
        </div>
      </template>
    </n-modal>
    
    <!-- 编辑提成比例弹窗 -->
    <n-modal v-model:show="showCommissionModal" preset="card" title="编辑提成比例" style="width: 450px;">
      <div class="space-y-6">
        <div class="p-4 bg-cyan-50 rounded-xl">
          <div class="flex items-center gap-3">
            <i class="fas fa-store text-cyan-500 text-xl"></i>
            <div>
              <p class="font-bold text-gray-800">{{ editCommissionMerchant?.name }}</p>
              <p class="text-sm text-gray-500">{{ editCommissionMerchant?.companyName }}</p>
            </div>
          </div>
        </div>
        
        <div>
          <label class="text-sm font-medium text-gray-700 mb-2 block">提成比例 (%)</label>
          <n-input-number 
            v-model:value="editCommissionRate" 
            :min="0" 
            :max="100" 
            :precision="2"
            :step="0.5"
            placeholder="输入提成比例"
            class="w-full"
          >
            <template #suffix>%</template>
          </n-input-number>
          <p class="text-xs text-gray-400 mt-2">
            <i class="fas fa-info-circle mr-1"></i>
            提成将从商家每笔收入中自动扣除。默认：个体工商户 8%，企业 5%
          </p>
        </div>
        
        <div class="p-4 bg-gray-50 rounded-xl">
          <p class="text-sm text-gray-600">
            <span class="text-gray-400">当前提成比例:</span>
            <span class="ml-2 font-bold text-cyan-500">{{ editCommissionMerchant?.commissionRate || 0 }}%</span>
          </p>
          <p class="text-sm text-gray-600 mt-1">
            <span class="text-gray-400">修改后提成比例:</span>
            <span class="ml-2 font-bold text-green-500">{{ editCommissionRate || 0 }}%</span>
          </p>
        </div>
      </div>
      <template #footer>
        <div class="flex gap-3 justify-end">
          <n-button @click="showCommissionModal = false">取消</n-button>
          <n-button type="primary" color="#00AFE1" @click="saveCommissionRate" :loading="savingCommission">
            保存修改
          </n-button>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useMessage, NInputNumber } from 'naive-ui'
import { getMerchantList, getMerchantApplyList, getMerchantStats, auditMerchant as apiAuditMerchant, updateMerchantStatus, updateMerchantCommissionRate } from '@/api/admin'

const message = useMessage()

const loading = ref(false)
const activeTab = ref('merchants')
const searchKeyword = ref('')
const filterStatus = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const showDetailModal = ref(false)
const showApplyDetailModal = ref(false)
const showImagePreview = ref(false)
const showRejectModal = ref(false)
const showCommissionModal = ref(false)
const previewImageUrl = ref('')
const rejectReason = ref('')
const currentMerchant = ref(null)
const currentApply = ref(null)
const editCommissionMerchant = ref(null)
const editCommissionRate = ref(0)
const savingCommission = ref(false)

const statusOptions = [
  { label: '已认证', value: 1 },
  { label: '已禁用', value: 2 }
]

const applyStatusOptions = [
  { label: '待审核', value: 0 },
  { label: '已通过', value: 1 },
  { label: '已拒绝', value: 2 }
]

const merchants = ref([])
const applies = ref([])

const stats = reactive({
  total: 0,
  pending: 0,
  verified: 0,
  disabled: 0
})

// 标签页切换
const handleTabChange = (tab) => {
  currentPage.value = 1
  filterStatus.value = null
  searchKeyword.value = ''
  if (tab === 'merchants') {
    loadMerchants()
  } else {
    loadApplies()
  }
}

// 加载商家统计
const loadStats = async () => {
  try {
    const res = await getMerchantStats()
    if (res.data) {
      stats.total = res.data.total || 0
      stats.pending = res.data.pending || 0
      stats.verified = res.data.verified || 0
      stats.disabled = res.data.disabled || 0
    }
  } catch (e) {
    console.error('加载统计失败', e)
  }
}

// 加载商家列表
const loadMerchants = async () => {
  loading.value = true
  try {
    const res = await getMerchantList({
      page: currentPage.value,
      size: pageSize.value,
      status: filterStatus.value,
      keyword: searchKeyword.value || undefined
    })
    if (res.data && res.data.records) {
      merchants.value = res.data.records.map(m => ({
        id: m.id,
        name: m.shopName || '未设置店铺名',
        companyName: m.companyName || (m.merchantType === 1 ? '个体工商户' : '企业/组织'),
        merchantType: m.merchantType,
        licenseNo: m.licenseNo || '-',
        phone: m.contactPhone ? m.contactPhone.substring(0, 3) + '****' + m.contactPhone.substring(7) : '-',
        contactPhone: m.contactPhone,
        contactEmail: m.contactEmail,
        legalPerson: m.legalPerson,
        businessAddress: m.businessAddress,
        status: m.status,
        creditScore: m.creditScore || 0,
        orderCount: m.orderCount || 0,
        applyTime: formatDate(m.createTime),
        verifyTime: formatDate(m.verifyTime),
        businessScope: m.businessScope,
        licenseImg: m.licenseImg,
        idCardFront: m.idCardFront,
        idCardBack: m.idCardBack,
        commissionRate: m.commissionRate
      }))
      totalPages.value = Math.max(1, Math.ceil(res.data.total / pageSize.value))
    }
  } catch (e) {
    console.error('加载商家列表失败', e)
  } finally {
    loading.value = false
  }
}

// 加载商户申请列表
const loadApplies = async () => {
  loading.value = true
  try {
    const res = await getMerchantApplyList({
      page: currentPage.value,
      size: pageSize.value,
      status: filterStatus.value
    })
    if (res.data && res.data.records) {
      applies.value = res.data.records.map(a => ({
        id: a.id,
        applyNo: a.applyNo,
        name: a.shopName || '未设置店铺名',
        companyName: a.companyName || (a.merchantType === 1 ? '个体工商户' : '企业/组织'),
        merchantType: a.merchantType,
        legalPerson: a.legalPerson,
        licenseNo: a.licenseNo || '-',
        phone: a.contactPhone || '-',
        email: a.contactEmail || '-',
        status: a.status,
        userName: a.userName || '-',
        userPhone: a.userPhone || '-',
        applyTime: formatDate(a.createTime),
        auditTime: formatDate(a.auditTime),
        auditRemark: a.auditRemark,
        businessScope: a.businessScope,
        businessAddress: a.businessAddress,
        licenseImg: a.licenseImg,
        idCardFront: a.idCardFront,
        idCardBack: a.idCardBack
      }))
      totalPages.value = Math.max(1, Math.ceil(res.data.total / pageSize.value))
    }
  } catch (e) {
    console.error('加载商户申请列表失败', e)
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
  const map = { 1: '已认证', 2: '已禁用' }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = {
    1: 'bg-green-100 text-green-500',
    2: 'bg-red-100 text-red-500'
  }
  return map[status] || 'bg-gray-100 text-gray-500'
}

const getApplyStatusText = (status) => {
  const map = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
  return map[status] || '未知'
}

const getApplyStatusClass = (status) => {
  const map = {
    0: 'bg-orange-100 text-orange-500',
    1: 'bg-green-100 text-green-500',
    2: 'bg-red-100 text-red-500'
  }
  return map[status] || 'bg-gray-100 text-gray-500'
}

const viewDetail = (merchant) => {
  currentMerchant.value = merchant
  showDetailModal.value = true
}

const viewApplyDetail = (apply) => {
  currentApply.value = apply
  showApplyDetailModal.value = true
}

// 图片预览
const previewImage = (url) => {
  previewImageUrl.value = url
  showImagePreview.value = true
}

// 打开拒绝弹窗
const handleRejectApply = () => {
  rejectReason.value = ''
  showRejectModal.value = true
}

// 确认拒绝申请
const confirmRejectApply = async () => {
  if (!rejectReason.value.trim()) {
    message.warning('请填写拒绝原因')
    return
  }
  await rejectApply(currentApply.value, rejectReason.value)
  showRejectModal.value = false
  showApplyDetailModal.value = false
}

// 审核商户申请（通过）
const auditApply = async (apply) => {
  try {
    await apiAuditMerchant(apply.id, { approved: true })
    message.success(`商户申请 ${apply.name} 审核通过`)
    apply.status = 1
    await loadStats()
    await loadApplies()
  } catch (e) {
    message.error(e.message || '操作失败')
  }
}

// 拒绝商户申请
const rejectApply = async (apply, reason = '审核未通过') => {
  try {
    await apiAuditMerchant(apply.id, { approved: false, reason })
    message.warning(`商户申请 ${apply.name} 已拒绝`)
    apply.status = 2
    await loadStats()
    await loadApplies()
  } catch (e) {
    message.error(e.message || '操作失败')
  }
}

const auditMerchant = async (merchant) => {
  try {
    await apiAuditMerchant(merchant.id, { approved: true })
    message.success(`商家 ${merchant.name} 审核通过`)
    merchant.status = 1
    await loadStats()
  } catch (e) {
    message.error('操作失败')
  }
}

const disableMerchant = async (merchant) => {
  try {
    await updateMerchantStatus(merchant.id, { status: 0 })
    message.warning(`商家 ${merchant.name} 已禁用`)
    merchant.status = 2
    await loadStats()
  } catch (e) {
    message.error('操作失败')
  }
}

const enableMerchant = async (merchant) => {
  try {
    await updateMerchantStatus(merchant.id, { status: 1 })
    message.success(`商家 ${merchant.name} 已恢复`)
    merchant.status = 1
    await loadStats()
  } catch (e) {
    message.error('操作失败')
  }
}

// 打开编辑提成比例弹窗
const openEditCommission = (merchant) => {
  editCommissionMerchant.value = merchant
  editCommissionRate.value = Number(merchant.commissionRate) || 0
  showCommissionModal.value = true
}

// 保存提成比例
const saveCommissionRate = async () => {
  if (editCommissionRate.value < 0 || editCommissionRate.value > 100) {
    message.error('提成比例必须在 0-100% 之间')
    return
  }
  
  savingCommission.value = true
  try {
    await updateMerchantCommissionRate(editCommissionMerchant.value.id, editCommissionRate.value)
    message.success('提成比例更新成功')
    
    // 更新本地数据
    editCommissionMerchant.value.commissionRate = editCommissionRate.value
    if (currentMerchant.value && currentMerchant.value.id === editCommissionMerchant.value.id) {
      currentMerchant.value.commissionRate = editCommissionRate.value
    }
    
    showCommissionModal.value = false
    await loadMerchants()
  } catch (e) {
    message.error(e.message || '更新失败')
  } finally {
    savingCommission.value = false
  }
}

// 分页切换
const handlePageChange = (page) => {
  currentPage.value = page
  loadMerchants()
}

// 监听筛选条件变化
watch([filterStatus, searchKeyword], () => {
  currentPage.value = 1
  if (activeTab.value === 'merchants') {
    loadMerchants()
  } else {
    loadApplies()
  }
})

onMounted(() => {
  loadStats()
  loadMerchants()
})
</script>
