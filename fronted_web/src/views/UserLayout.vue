<template>
  <div class="flex h-screen bg-slate-50">
    <!-- Sidebar -->
    <div class="w-64 bg-white border-r border-gray-100 flex flex-col">
      <div class="p-6 flex items-center gap-3">
        <img src="/logo.png" alt="臻托" class="w-10 h-10 rounded-lg object-cover" />
        <span class="font-black text-xl text-gray-800 tracking-tight">臻托 TF</span>
      </div>

      <nav class="flex-1 px-4 space-y-2 mt-4">
        <router-link to="/user/dashboard" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'nav-active' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-chart-pie w-6 text-center"></i>
            <span>工作台</span>
          </a>
        </router-link>

        <router-link to="/user/orders" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'nav-active' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-folder-open w-6 text-center"></i>
            <span>我的项目</span>
          </a>
        </router-link>

        <router-link to="/user/wallet" custom v-slot="{ href, navigate, isActive }">
          <a :href="href" @click="navigate"
             class="flex items-center gap-3 p-3 rounded-xl transition-all"
             :class="isActive ? 'nav-active' : 'text-gray-500 hover:bg-gray-50'">
            <i class="fas fa-wallet w-6 text-center"></i>
            <span>信托钱包</span>
          </a>
        </router-link>

        <!-- 在线客服入口 -->
        <div class="pt-4 mt-4 border-t border-gray-100">
          <button 
            @click="openCustomerService"
            class="flex items-center gap-3 p-3 rounded-xl transition-all w-full text-gray-500 hover:bg-green-50 hover:text-green-600"
          >
            <i class="fas fa-headset w-6 text-center"></i>
            <span>在线客服</span>
          </button>
        </div>
      </nav>

      <div class="p-4 border-t border-gray-100">
        <n-dropdown 
          trigger="click" 
          :options="userMenuOptions" 
          @select="handleMenuSelect"
          placement="top-start"
          :style="{ width: '200px' }"
          :theme-overrides="dropdownTheme"
        >
          <div class="flex items-center gap-3 p-2 rounded-xl hover:bg-gray-50 cursor-pointer">
            <div class="w-10 h-10 bg-gray-200 rounded-full flex items-center justify-center text-gray-500">
              <i class="fas fa-user"></i>
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-bold text-gray-800 truncate">{{ userStore.userInfo.nickname || userStore.userInfo.username }}</p>
              <p class="text-xs text-gray-400 truncate">{{ userStore.userInfo.role === 'merchant' ? '认证服务商' : '普通用户' }}</p>
            </div>
            <i class="fas fa-chevron-up text-gray-400 text-xs"></i>
          </div>
        </n-dropdown>
      </div>
    </div>

    <!-- 用户信息设置弹窗 -->
    <n-modal v-model:show="showProfileModal" preset="card" title="个人信息设置" class="max-w-lg">
      <div class="space-y-4">
        <n-form-item label="昵称">
          <n-input v-model:value="profileForm.nickname" placeholder="请输入昵称" />
        </n-form-item>
        <n-form-item label="真实姓名">
          <n-input v-model:value="profileForm.realName" placeholder="请输入真实姓名（用于提现验证）" />
        </n-form-item>
        <n-form-item label="手机号">
          <n-input v-model:value="profileForm.phone" placeholder="请输入手机号" disabled />
        </n-form-item>
        <n-form-item label="邮箱">
          <n-input v-model:value="profileForm.email" placeholder="请输入邮箱（用于接收通知）" />
        </n-form-item>
      </div>
      <template #footer>
        <div class="flex justify-end">
          <n-button type="primary" color="#00AFE1" :loading="savingProfile" @click="saveProfile">
            保存修改
          </n-button>
        </div>
      </template>
    </n-modal>

    <!-- 提现设置弹窗（银行卡管理） -->
    <n-modal v-model:show="showWithdrawModal" preset="card" title="提现设置" class="max-w-2xl">
      <div class="space-y-6">
        <!-- 已绑定的银行卡 -->
        <div>
          <h4 class="text-sm font-bold text-gray-500 mb-3">已绑定银行卡</h4>
          <div v-if="bankCards.length === 0" class="text-center py-8 text-gray-400 bg-gray-50 rounded-xl">
            <i class="fas fa-credit-card text-4xl mb-3 opacity-30"></i>
            <p>暂无绑定的银行卡</p>
            <p class="text-xs mt-1">请添加银行卡以便提现</p>
          </div>
          <div v-else class="space-y-3">
            <div 
              v-for="card in bankCards" 
              :key="card.id"
              class="flex items-center gap-4 p-4 bg-gray-50 rounded-xl border border-gray-100"
            >
              <div class="w-12 h-12 bg-gradient-to-br from-cyan-500 to-blue-600 rounded-lg flex items-center justify-center">
                <i class="fas fa-university text-white text-lg"></i>
              </div>
              <div class="flex-1">
                <p class="font-bold text-gray-800">{{ card.bankName }}</p>
                <p class="text-sm text-gray-400">**** **** **** {{ card.cardNo.slice(-4) }}</p>
              </div>
              <n-tag v-if="card.isDefault" type="success" size="small">默认</n-tag>
              <n-button text type="error" size="small" @click="removeCard(card.id)">
                <i class="fas fa-trash"></i>
              </n-button>
            </div>
          </div>
        </div>

        <!-- 添加银行卡 -->
        <div class="pt-4 border-t border-gray-200">
          <h4 class="text-sm font-bold text-gray-500 mb-3">添加新银行卡</h4>
          <div class="grid grid-cols-2 gap-4">
            <n-form-item label="持卡人姓名">
              <n-input v-model:value="newCard.holderName" placeholder="请输入持卡人姓名" />
            </n-form-item>
            <n-form-item label="银行名称">
              <n-select 
                v-model:value="newCard.bankName" 
                :options="bankOptions" 
                placeholder="请选择银行"
              />
            </n-form-item>
            <n-form-item label="银行卡号" class="col-span-2">
              <n-input v-model:value="newCard.cardNo" placeholder="请输入银行卡号" />
            </n-form-item>
            <n-form-item label="开户行">
              <n-input v-model:value="newCard.branchName" placeholder="请输入开户行" />
            </n-form-item>
            <n-form-item label="预留手机号">
              <n-input v-model:value="newCard.phone" placeholder="请输入银行预留手机号" />
            </n-form-item>
          </div>
          <div class="flex justify-end mt-4">
            <n-button type="primary" color="#00AFE1" :loading="addingCard" @click="handleAddBankCard">
              <template #icon><i class="fas fa-plus"></i></template>
              添加银行卡
            </n-button>
          </div>
        </div>
      </div>
    </n-modal>

    <!-- 在线客服弹窗 -->
    <n-modal v-model:show="showCustomerService" preset="card" title="在线客服" class="max-w-lg">
      <div class="space-y-4">
        <div class="p-4 bg-green-50 rounded-xl text-center">
          <div class="w-16 h-16 bg-green-500/20 rounded-full flex items-center justify-center mx-auto mb-3">
            <i class="fas fa-headset text-3xl text-green-500"></i>
          </div>
          <h3 class="font-bold text-gray-800 text-lg">平台客服</h3>
          <p class="text-gray-500 text-sm mt-1">工作时间: 9:00 - 22:00</p>
        </div>

        <div class="grid grid-cols-2 gap-3">
          <button 
            class="p-4 bg-gray-50 rounded-xl text-center hover:bg-blue-50 transition group"
            @click="copyContact('qq', '123456789')"
          >
            <i class="fab fa-qq text-2xl text-blue-500 mb-2"></i>
            <p class="text-xs text-gray-600">QQ客服</p>
            <p class="text-xs text-gray-400 group-hover:text-blue-500 transition">123456789</p>
            <p class="text-xs text-gray-400 mt-1">点击复制</p>
          </button>
          <button 
            class="p-4 bg-gray-50 rounded-xl text-center hover:bg-green-50 transition group"
            @click="copyContact('wechat', 'TrustFulfillment')"
          >
            <i class="fab fa-weixin text-2xl text-green-500 mb-2"></i>
            <p class="text-xs text-gray-600">微信客服</p>
            <p class="text-xs text-gray-400 group-hover:text-green-500 transition">TrustFulfillment</p>
            <p class="text-xs text-gray-400 mt-1">点击复制</p>
          </button>
        </div>

        <div class="p-4 bg-blue-50 rounded-xl">
          <p class="text-xs text-blue-600">
            <i class="fas fa-info-circle mr-1"></i>
            您也可以通过订单详情页的"联系商家"按钮，发起与服务商的在线沟通
          </p>
        </div>

        <n-button block type="primary" color="#00AFE1" @click="openOnlineChat">
          <template #icon><i class="fas fa-comments"></i></template>
          发起在线咨询
        </n-button>
      </div>
    </n-modal>

    <!-- 在线客服聊天抽屉 -->
    <ServiceChatDrawer v-model:show="showChatDrawer" />

    <!-- 系统设置弹窗 -->
    <n-modal v-model:show="showSettingsModal" preset="card" title="系统设置" class="max-w-md">
      <div class="space-y-4">
        <div class="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
          <div>
            <p class="font-bold text-gray-700">消息通知</p>
            <p class="text-xs text-gray-400">接收站内消息推送</p>
          </div>
          <n-switch v-model:value="settings.notification" />
        </div>
        <div class="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
          <div>
            <p class="font-bold text-gray-700">邮件提醒</p>
            <p class="text-xs text-gray-400">重要通知发送到邮箱</p>
          </div>
          <n-switch v-model:value="settings.emailAlert" />
        </div>
        <div class="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
          <div>
            <p class="font-bold text-gray-700">消息提示音</p>
            <p class="text-xs text-gray-400">收到消息时播放提示音</p>
          </div>
          <n-switch v-model:value="settings.sound" />
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end">
          <n-button type="primary" color="#00AFE1">保存设置</n-button>
        </div>
      </template>
    </n-modal>

    <!-- 商户申请弹窗 -->
    <n-modal v-model:show="showMerchantApplyModal" preset="card" title="申请成为商户" style="width: 700px; max-width: 90vw;">
      <div class="space-y-6">
        <!-- 商户类型选择 -->
        <div>
          <p class="text-sm font-bold text-gray-700 mb-3">选择商户类型</p>
          <div class="grid grid-cols-2 gap-4">
            <div 
              class="p-4 rounded-xl border-2 cursor-pointer transition-all"
              :class="merchantApplyForm.merchantType === 1 ? 'border-cyan-500 bg-cyan-50' : 'border-gray-200 hover:border-gray-300'"
              @click="merchantApplyForm.merchantType = 1"
            >
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-full flex items-center justify-center"
                     :class="merchantApplyForm.merchantType === 1 ? 'bg-cyan-500 text-white' : 'bg-gray-100 text-gray-400'">
                  <i class="fas fa-user"></i>
                </div>
                <div>
                  <p class="font-bold" :class="merchantApplyForm.merchantType === 1 ? 'text-cyan-600' : 'text-gray-700'">个体工商户</p>
                  <p class="text-xs text-gray-400">适合个人或小规模经营</p>
                </div>
              </div>
              <p class="text-xs text-orange-500 mt-2" v-if="merchantTypeConfig.individual">
                <i class="fas fa-info-circle mr-1"></i>平台提成 {{ merchantTypeConfig.individual.commissionRate }}%
              </p>
            </div>
            <div 
              class="p-4 rounded-xl border-2 cursor-pointer transition-all"
              :class="merchantApplyForm.merchantType === 2 ? 'border-cyan-500 bg-cyan-50' : 'border-gray-200 hover:border-gray-300'"
              @click="merchantApplyForm.merchantType = 2"
            >
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-full flex items-center justify-center"
                     :class="merchantApplyForm.merchantType === 2 ? 'bg-cyan-500 text-white' : 'bg-gray-100 text-gray-400'">
                  <i class="fas fa-building"></i>
                </div>
                <div>
                  <p class="font-bold" :class="merchantApplyForm.merchantType === 2 ? 'text-cyan-600' : 'text-gray-700'">企业/组织</p>
                  <p class="text-xs text-gray-400">适合公司、团队或组织</p>
                </div>
              </div>
              <p class="text-xs text-green-500 mt-2" v-if="merchantTypeConfig.enterprise">
                <i class="fas fa-info-circle mr-1"></i>平台提成 {{ merchantTypeConfig.enterprise.commissionRate }}%
              </p>
            </div>
          </div>
        </div>

        <!-- 基本信息 -->
        <div>
          <p class="text-sm font-bold text-gray-700 mb-3">基本信息</p>
          <div class="grid grid-cols-2 gap-4">
            <n-form-item label="店铺名称" required>
              <n-input v-model:value="merchantApplyForm.shopName" placeholder="请输入店铺名称" />
            </n-form-item>
            <n-form-item :label="merchantApplyForm.merchantType === 2 ? '企业名称' : '企业名称（可选）'" :required="merchantApplyForm.merchantType === 2">
              <n-input v-model:value="merchantApplyForm.companyName" placeholder="请输入企业/组织全称" />
            </n-form-item>
            <n-form-item label="法人/负责人" required>
              <n-input v-model:value="merchantApplyForm.legalPerson" placeholder="请输入真实姓名" />
            </n-form-item>
            <n-form-item label="身份证号" required>
              <n-input v-model:value="merchantApplyForm.legalIdCard" placeholder="请输入身份证号" />
            </n-form-item>
            <n-form-item label="联系电话" required>
              <n-input v-model:value="merchantApplyForm.contactPhone" placeholder="请输入联系电话" />
            </n-form-item>
            <n-form-item label="联系邮箱">
              <n-input v-model:value="merchantApplyForm.contactEmail" placeholder="请输入邮箱（可选）" />
            </n-form-item>
            <n-form-item label="营业执照号" :required="merchantApplyForm.merchantType === 2">
              <n-input v-model:value="merchantApplyForm.licenseNo" placeholder="统一社会信用代码" />
            </n-form-item>
            <n-form-item label="经营地址">
              <n-input v-model:value="merchantApplyForm.businessAddress" placeholder="请输入经营地址" />
            </n-form-item>
          </div>
          <n-form-item label="经营范围" class="mt-2">
            <n-input 
              type="textarea" 
              v-model:value="merchantApplyForm.businessScope" 
              placeholder="请描述您的主营业务和服务范围"
              :rows="2"
            />
          </n-form-item>
        </div>

        <!-- 资质上传 -->
        <div class="p-5 bg-gray-50 rounded-xl">
          <p class="text-sm font-bold text-gray-700 mb-4">
            <i class="fas fa-folder-open mr-2 text-cyan-500"></i>资质材料
          </p>
          <div class="grid grid-cols-3 gap-5">
            <!-- 身份证正面 -->
            <div class="bg-white rounded-xl p-4 border border-gray-100">
              <div class="flex items-center justify-between mb-3">
                <span class="text-sm font-medium text-gray-700">身份证正面</span>
                <span class="text-red-500 text-xs">必填</span>
              </div>
              <n-upload
                :custom-request="({ file }) => handleUpload(file.file, 'idCardFront')"
                :show-file-list="false"
                accept="image/*"
              >
                <div 
                  class="aspect-[4/3] border-2 border-dashed rounded-lg flex flex-col items-center justify-center cursor-pointer transition-all group"
                  :class="pendingFiles.idCardFront ? 'border-cyan-400 bg-cyan-50' : 'border-gray-200 hover:border-cyan-400 hover:bg-cyan-50/50'"
                >
                  <template v-if="uploadingIdCardFront">
                    <n-spin size="medium" />
                    <p class="text-xs text-gray-400 mt-3">上传中...</p>
                  </template>
                  <template v-else-if="pendingFiles.idCardFront">
                    <div class="w-12 h-12 rounded-full bg-cyan-100 flex items-center justify-center mb-2">
                      <i class="fas fa-check text-xl text-cyan-500"></i>
                    </div>
                    <p class="text-sm font-medium text-cyan-600">已选择文件</p>
                    <p class="text-xs text-gray-400 mt-1 px-2 truncate max-w-full">{{ pendingFiles.idCardFront.name }}</p>
                    <p class="text-xs text-cyan-500 mt-2">点击重新选择</p>
                  </template>
                  <template v-else>
                    <div class="w-12 h-12 rounded-full bg-gray-100 group-hover:bg-cyan-100 flex items-center justify-center mb-2 transition-colors">
                      <i class="fas fa-id-card text-xl text-gray-400 group-hover:text-cyan-500 transition-colors"></i>
                    </div>
                    <p class="text-sm text-gray-500">点击上传</p>
                    <p class="text-xs text-gray-400 mt-1">支持 JPG、PNG 格式</p>
                  </template>
                </div>
              </n-upload>
            </div>

            <!-- 身份证反面 -->
            <div class="bg-white rounded-xl p-4 border border-gray-100">
              <div class="flex items-center justify-between mb-3">
                <span class="text-sm font-medium text-gray-700">身份证反面</span>
                <span class="text-red-500 text-xs">必填</span>
              </div>
              <n-upload
                :custom-request="({ file }) => handleUpload(file.file, 'idCardBack')"
                :show-file-list="false"
                accept="image/*"
              >
                <div 
                  class="aspect-[4/3] border-2 border-dashed rounded-lg flex flex-col items-center justify-center cursor-pointer transition-all group"
                  :class="pendingFiles.idCardBack ? 'border-cyan-400 bg-cyan-50' : 'border-gray-200 hover:border-cyan-400 hover:bg-cyan-50/50'"
                >
                  <template v-if="uploadingIdCardBack">
                    <n-spin size="medium" />
                    <p class="text-xs text-gray-400 mt-3">上传中...</p>
                  </template>
                  <template v-else-if="pendingFiles.idCardBack">
                    <div class="w-12 h-12 rounded-full bg-cyan-100 flex items-center justify-center mb-2">
                      <i class="fas fa-check text-xl text-cyan-500"></i>
                    </div>
                    <p class="text-sm font-medium text-cyan-600">已选择文件</p>
                    <p class="text-xs text-gray-400 mt-1 px-2 truncate max-w-full">{{ pendingFiles.idCardBack.name }}</p>
                    <p class="text-xs text-cyan-500 mt-2">点击重新选择</p>
                  </template>
                  <template v-else>
                    <div class="w-12 h-12 rounded-full bg-gray-100 group-hover:bg-cyan-100 flex items-center justify-center mb-2 transition-colors">
                      <i class="fas fa-id-card text-xl text-gray-400 group-hover:text-cyan-500 transition-colors"></i>
                    </div>
                    <p class="text-sm text-gray-500">点击上传</p>
                    <p class="text-xs text-gray-400 mt-1">支持 JPG、PNG 格式</p>
                  </template>
                </div>
              </n-upload>
            </div>

            <!-- 营业执照 -->
            <div class="bg-white rounded-xl p-4 border border-gray-100">
              <div class="flex items-center justify-between mb-3">
                <span class="text-sm font-medium text-gray-700">营业执照</span>
                <span v-if="merchantApplyForm.merchantType === 2" class="text-red-500 text-xs">必填</span>
                <span v-else class="text-gray-400 text-xs">选填</span>
              </div>
              <n-upload
                :custom-request="({ file }) => handleUpload(file.file, 'license')"
                :show-file-list="false"
                accept="image/*"
              >
                <div 
                  class="aspect-[4/3] border-2 border-dashed rounded-lg flex flex-col items-center justify-center cursor-pointer transition-all group"
                  :class="pendingFiles.license ? 'border-cyan-400 bg-cyan-50' : 'border-gray-200 hover:border-cyan-400 hover:bg-cyan-50/50'"
                >
                  <template v-if="uploadingLicense">
                    <n-spin size="medium" />
                    <p class="text-xs text-gray-400 mt-3">上传中...</p>
                  </template>
                  <template v-else-if="pendingFiles.license">
                    <div class="w-12 h-12 rounded-full bg-cyan-100 flex items-center justify-center mb-2">
                      <i class="fas fa-check text-xl text-cyan-500"></i>
                    </div>
                    <p class="text-sm font-medium text-cyan-600">已选择文件</p>
                    <p class="text-xs text-gray-400 mt-1 px-2 truncate max-w-full">{{ pendingFiles.license.name }}</p>
                    <p class="text-xs text-cyan-500 mt-2">点击重新选择</p>
                  </template>
                  <template v-else>
                    <div class="w-12 h-12 rounded-full bg-gray-100 group-hover:bg-cyan-100 flex items-center justify-center mb-2 transition-colors">
                      <i class="fas fa-file-contract text-xl text-gray-400 group-hover:text-cyan-500 transition-colors"></i>
                    </div>
                    <p class="text-sm text-gray-500">点击上传</p>
                    <p class="text-xs text-gray-400 mt-1">支持 JPG、PNG 格式</p>
                  </template>
                </div>
              </n-upload>
            </div>
          </div>
          <p class="text-xs text-gray-400 mt-4">
            <i class="fas fa-info-circle mr-1"></i>
            文件将在提交申请时自动上传，请确保图片清晰可辨
          </p>
        </div>

        <!-- 提示 -->
        <div class="p-4 bg-blue-50 rounded-xl">
          <p class="text-xs text-blue-600">
            <i class="fas fa-info-circle mr-1"></i>
            提交申请后，平台将在 1-3 个工作日内完成审核。审核通过后，您将获得商家权限，可以接单服务。
          </p>
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end gap-3">
          <n-button @click="showMerchantApplyModal = false">取消</n-button>
          <n-button type="primary" color="#00AFE1" :loading="submittingApply" @click="submitMerchantApplyForm">
            提交申请
          </n-button>
        </div>
      </template>
    </n-modal>

    <!-- Main Content -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- Header -->
      <header class="h-16 bg-white border-b border-gray-100 flex items-center justify-between px-8">
        <h2 class="font-bold text-gray-800 text-lg">
          {{ route.meta.title || '概览' }}
        </h2>
        <div class="flex items-center gap-4">
          <n-popover trigger="click" placement="bottom-end" :style="{ padding: 0 }">
            <template #trigger>
              <n-badge :value="unreadCount" :max="99" :show="unreadCount > 0">
                <n-button circle size="small">
                  <template #icon><i class="far fa-bell"></i></template>
                </n-button>
              </n-badge>
            </template>
            <div class="w-80 max-h-96 flex flex-col">
              <div class="px-4 py-3 border-b border-gray-100 flex items-center justify-between">
                <span class="font-bold text-gray-800">通知消息</span>
                <n-button text type="primary" size="tiny" @click="markAllRead" v-if="unreadCount > 0">
                  全部已读
                </n-button>
              </div>
              <div class="flex-1 overflow-y-auto">
                <div v-if="notifications.length === 0" class="py-12 text-center text-gray-400">
                  <i class="far fa-bell-slash text-3xl mb-2"></i>
                  <p class="text-sm">暂无通知</p>
                </div>
                <div v-else>
                  <div 
                    v-for="noti in notifications" 
                    :key="noti.id"
                    @click="handleNotificationClick(noti)"
                    class="px-4 py-3 hover:bg-gray-50 cursor-pointer border-b border-gray-50 transition-colors"
                    :class="{ 'bg-cyan-50/50': !noti.isRead }"
                  >
                    <div class="flex items-start gap-3">
                      <div 
                        class="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0"
                        :class="getNotificationIconClass(noti.type)"
                      >
                        <i :class="getNotificationIcon(noti.type)"></i>
                      </div>
                      <div class="flex-1 min-w-0">
                        <p class="text-sm font-medium text-gray-800 truncate">{{ noti.title }}</p>
                        <p class="text-xs text-gray-500 mt-0.5 line-clamp-2">{{ noti.content }}</p>
                        <p class="text-xs text-gray-400 mt-1">{{ formatTime(noti.createTime) }}</p>
                      </div>
                      <div v-if="!noti.isRead" class="w-2 h-2 bg-cyan-500 rounded-full flex-shrink-0 mt-2"></div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="px-4 py-2 border-t border-gray-100 text-center">
                <n-button text type="primary" size="small" @click="router.push('/user/notifications')">
                  查看全部
                </n-button>
              </div>
            </div>
          </n-popover>
          <n-button 
            type="primary" 
            color="#00AFE1" 
            class="font-bold shadow-lg" 
            style="box-shadow: 0 10px 25px -5px rgba(0,175,225,0.2);"
            @click="router.push('/user/create-order')"
          >
            <template #icon><i class="fas fa-plus"></i></template>
            发布需求
          </n-button>
        </div>
      </header>

      <!-- Page Content -->
      <main class="flex-1 overflow-y-auto p-8 bg-slate-50/50">
        <router-view></router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, provide, onMounted, onUnmounted, computed, h } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import { logout, updateUserInfo } from '@/api/auth'
import { getWalletInfo, getBankCards, addBankCard, deleteBankCard } from '@/api/wallet'
import { getNotifications, markAsRead, markAllAsRead } from '@/api/notification'
import { getBankOptions } from '@/api/bank'
import { getMerchantStatus, getMerchantTypeConfig, submitMerchantApply } from '@/api/merchant'
import { uploadFile } from '@/api/file'
import ServiceChatDrawer from '@/components/ServiceChatDrawer.vue'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()
const message = useMessage()

const walletInfo = ref({
  balance: 0,
  frozenAmount: 0
})

// 通知相关
const notifications = ref([])
const unreadCount = computed(() => notifications.value.filter(n => !n.isRead).length)

// 弹窗控制
const showProfileModal = ref(false)
const showWithdrawModal = ref(false)
const showSettingsModal = ref(false)
const showCustomerService = ref(false)
const showChatDrawer = ref(false)
const showMerchantApplyModal = ref(false)
const savingProfile = ref(false)
const addingCard = ref(false)
const submittingApply = ref(false)

// 商户状态
const merchantStatus = ref({
  isMerchant: false,
  hasPendingApply: false,
  pendingApply: null,
  merchant: null
})

// 商户类型配置
const merchantTypeConfig = ref({})

// 商户申请表单
const merchantApplyForm = reactive({
  merchantType: 1,
  shopName: '',
  companyName: '',
  legalPerson: '',
  legalIdCard: '',
  licenseNo: '',
  licenseImg: '',
  idCardFront: '',
  idCardBack: '',
  contactPhone: '',
  contactEmail: '',
  businessAddress: '',
  businessScope: '',
  businessCategories: ''
})

// 上传状态
const uploadingIdCardFront = ref(false)
const uploadingIdCardBack = ref(false)
const uploadingLicense = ref(false)

// 待上传的文件（File 对象）
const pendingFiles = reactive({
  idCardFront: null,
  idCardBack: null,
  license: null
})

// 银行卡列表
const bankCards = ref([])

// 新增银行卡表单
const newCard = reactive({
  holderName: '',
  bankName: null,
  cardNo: '',
  branchName: '',
  phone: ''
})

// 银行选项（从后端加载）
const bankOptions = ref([])

// 用户菜单选项（根据商户状态动态生成）
const userMenuOptions = computed(() => {
  const options = [
    {
      label: '个人信息设置',
      key: 'profile',
      icon: () => h('i', { class: 'fas fa-user-edit' })
    },
    {
      label: '提现设置',
      key: 'withdraw',
      icon: () => h('i', { class: 'fas fa-credit-card' })
    },
    {
      label: '系统设置',
      key: 'settings',
      icon: () => h('i', { class: 'fas fa-cog' })
    }
  ]

  // 如果是商户，添加商家端入口
  if (merchantStatus.value.isMerchant) {
    options.push({ type: 'divider' })
    options.push({
      label: '进入商家端',
      key: 'merchantPortal',
      icon: () => h('i', { class: 'fas fa-store text-cyan-500' })
    })
  } else {
    // 如果不是商户，添加申请入口
    options.push({ type: 'divider' })
    if (merchantStatus.value.hasPendingApply) {
      options.push({
        label: '商户申请中...',
        key: 'merchantApplyPending',
        icon: () => h('i', { class: 'fas fa-clock text-yellow-500' }),
        disabled: true
      })
    } else {
      options.push({
        label: '申请成为商户',
        key: 'merchantApply',
        icon: () => h('i', { class: 'fas fa-store text-green-500' })
      })
    }
  }

  options.push({ type: 'divider' })
  options.push({
    label: '退出登录',
    key: 'logout',
    icon: () => h('i', { class: 'fas fa-sign-out-alt text-red-400' })
  })

  return options
})

// 下拉菜单浅色主题（与用户端主题一致）
const dropdownTheme = {
  color: '#ffffff',
  optionColorHover: '#f1f5f9',      // slate-100
  optionTextColor: '#334155',        // slate-700
  optionTextColorHover: '#00AFE1',
  dividerColor: '#e2e8f0',           // slate-200
  borderRadius: '12px',
  boxShadow: '0 10px 40px rgba(0,0,0,0.1)'
}

// 个人信息表单
const profileForm = reactive({
  nickname: '',
  realName: '',
  phone: '',
  email: ''
})

// 系统设置
const settings = reactive({
  notification: true,
  emailAlert: false,
  sound: true
})

// 处理菜单选择
const handleMenuSelect = (key) => {
  switch (key) {
    case 'profile':
      openProfileModal()
      break
    case 'withdraw':
      openWithdrawModal()
      break
    case 'settings':
      showSettingsModal.value = true
      break
    case 'merchantPortal':
      // 进入商家端
      window.location.href = '/merchant/dashboard'
      break
    case 'merchantApply':
      openMerchantApplyModal()
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 打开个人信息弹窗
const openProfileModal = () => {
  const user = userStore.userInfo
  profileForm.nickname = user.nickname || ''
  profileForm.realName = user.realName || ''
  profileForm.phone = user.phone || ''
  profileForm.email = user.email || ''
  showProfileModal.value = true
}

// 保存个人信息
const saveProfile = async () => {
  savingProfile.value = true
  try {
    await updateUserInfo({
      nickname: profileForm.nickname,
      realName: profileForm.realName,
      email: profileForm.email
    })
    // 更新本地用户信息
    userStore.userInfo.nickname = profileForm.nickname
    userStore.userInfo.realName = profileForm.realName
    userStore.userInfo.email = profileForm.email
    message.success('保存成功')
    showProfileModal.value = false
  } catch (e) {
    message.error(e.message || '保存失败')
  } finally {
    savingProfile.value = false
  }
}

// 加载银行选项
const loadBankOptions = async () => {
  if (bankOptions.value.length > 0) return
  try {
    const res = await getBankOptions()
    bankOptions.value = res.data || []
  } catch (e) {
    console.error('加载银行列表失败', e)
  }
}

// 打开提现设置弹窗
const openWithdrawModal = async () => {
  showWithdrawModal.value = true
  await Promise.all([loadBankCards(), loadBankOptions()])
}

// 加载银行卡列表
const loadBankCards = async () => {
  try {
    const res = await getBankCards()
    bankCards.value = res.data || []
  } catch (e) {
    console.error('加载银行卡失败', e)
  }
}

// 添加银行卡
const handleAddBankCard = async () => {
  if (!newCard.holderName || !newCard.bankName || !newCard.cardNo) {
    message.warning('请填写完整的银行卡信息')
    return
  }
  addingCard.value = true
  try {
    await addBankCard(newCard)
    message.success('添加成功')
    // 重置表单
    newCard.holderName = ''
    newCard.bankName = null
    newCard.cardNo = ''
    newCard.branchName = ''
    newCard.phone = ''
    // 刷新列表
    await loadBankCards()
  } catch (e) {
    message.error(e.message || '添加失败')
  } finally {
    addingCard.value = false
  }
}

// 删除银行卡
const removeCard = async (cardId) => {
  try {
    await deleteBankCard(cardId)
    message.success('删除成功')
    await loadBankCards()
  } catch (e) {
    message.error(e.message || '删除失败')
  }
}

// 加载钱包信息
const loadWalletInfo = async () => {
  try {
    const res = await getWalletInfo()
    if (res.data) {
      walletInfo.value = res.data
    }
  } catch (e) {
    console.error('加载钱包信息失败', e)
  }
}

// 提供刷新方法给子组件
provide('refreshWallet', loadWalletInfo)
provide('walletInfo', walletInfo)

const handleLogout = () => {
  logout().finally(() => {
    userStore.logout()
    message.success('已退出登录')
    router.push('/login')
  })
}

// ========== 在线客服相关 ==========

// 打开客服弹窗
const openCustomerService = () => {
  showCustomerService.value = true
}

// 复制联系方式
const copyContact = async (type, contact) => {
  try {
    await navigator.clipboard.writeText(contact)
    if (type === 'qq') {
      message.success(`QQ号 ${contact} 已复制到剪贴板`)
    } else if (type === 'wechat') {
      message.success(`微信号 ${contact} 已复制到剪贴板`)
    }
  } catch (e) {
    // 降级方案
    const input = document.createElement('input')
    input.value = contact
    document.body.appendChild(input)
    input.select()
    document.execCommand('copy')
    document.body.removeChild(input)
    if (type === 'qq') {
      message.success(`QQ号 ${contact} 已复制到剪贴板`)
    } else if (type === 'wechat') {
      message.success(`微信号 ${contact} 已复制到剪贴板`)
    }
  }
}

// 发起在线咨询
const openOnlineChat = () => {
  showCustomerService.value = false
  showChatDrawer.value = true
}

// 加载通知列表
const loadNotifications = async () => {
  try {
    const res = await getNotifications({ limit: 10 })
    notifications.value = res.data || []
  } catch (e) {
    // 静默处理，可能是表未创建或暂无数据
    notifications.value = []
  }
}

// 获取通知图标
const getNotificationIcon = (type) => {
  const icons = {
    message: 'fas fa-comment-dots',
    dispute: 'fas fa-gavel',
    system: 'fas fa-bell',
    order: 'fas fa-file-contract',
    service_message: 'fas fa-headset',
    order_message: 'fas fa-comments'
  }
  return icons[type] || 'fas fa-bell'
}

// 获取通知图标样式
const getNotificationIconClass = (type) => {
  const classes = {
    message: 'bg-blue-100 text-blue-500',
    dispute: 'bg-orange-100 text-orange-500',
    system: 'bg-gray-100 text-gray-500',
    order: 'bg-green-100 text-green-500',
    service_message: 'bg-green-100 text-green-500',
    order_message: 'bg-cyan-100 text-cyan-500'
  }
  return classes[type] || 'bg-gray-100 text-gray-500'
}

// 格式化时间
const formatTime = (timeVal) => {
  if (!timeVal) return ''
  let date
  if (typeof timeVal === 'string') {
    date = new Date(timeVal.replace('T', ' '))
  } else if (Array.isArray(timeVal)) {
    date = new Date(timeVal[0], timeVal[1] - 1, timeVal[2], timeVal[3] || 0, timeVal[4] || 0, timeVal[5] || 0)
  } else {
    return ''
  }
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString()
}

// 点击通知
const handleNotificationClick = async (noti) => {
  // 根据类型处理
  const notificationType = noti.notificationType || 'system'
  
  // 系统通知标记为已读
  if (notificationType === 'system' && !noti.isRead) {
    try {
      // 提取真实的通知ID（去掉前缀）
      const realId = String(noti.id).replace('sys_', '')
      await markAsRead(realId)
      noti.isRead = true
    } catch (e) {
      console.error('标记已读失败', e)
    }
  }
  
  // 根据类型跳转
  if (notificationType === 'service_message') {
    // 客服消息 - 打开在线客服聊天
    showChatDrawer.value = true
  } else if (notificationType === 'order_message' && noti.targetId) {
    // 订单消息 - 跳转到订单详情页并打开聊天界面
    router.push(`/user/orders/${noti.targetId}?openChat=true`)
  } else if (noti.targetType === 'order' && noti.targetId) {
    router.push(`/user/orders/${noti.targetId}`)
  } else if (noti.targetType === 'dispute' && noti.targetId) {
    router.push(`/user/disputes/${noti.targetId}`)
  }
}

// 全部标记已读
const markAllRead = async () => {
  try {
    await markAllAsRead()
    notifications.value.forEach(n => n.isRead = true)
    message.success('已全部标记为已读')
  } catch (e) {
    message.error('操作失败')
  }
}

// ========== 商户申请相关 ==========

// 加载商户状态
const loadMerchantStatus = async () => {
  try {
    const res = await getMerchantStatus()
    if (res.data) {
      merchantStatus.value = res.data
      
      // 同步更新本地存储的 userInfo 中的 isMerchant 字段
      const currentUserInfo = { ...userStore.userInfo }
      if (res.data.isMerchant !== undefined && currentUserInfo.isMerchant !== res.data.isMerchant) {
        currentUserInfo.isMerchant = res.data.isMerchant ? 1 : 0
        if (res.data.merchant) {
          currentUserInfo.merchantId = res.data.merchant.id
        }
        userStore.setUserInfo(currentUserInfo)
        // 同时更新 sessionStorage 和 localStorage
        sessionStorage.setItem('userInfo', JSON.stringify(currentUserInfo))
        if (localStorage.getItem('userInfo')) {
          localStorage.setItem('userInfo', JSON.stringify(currentUserInfo))
        }
      }
    }
  } catch (e) {
    console.error('加载商户状态失败', e)
  }
}

// 加载商户类型配置
const loadMerchantTypeConfig = async () => {
  try {
    const res = await getMerchantTypeConfig()
    if (res.data) {
      merchantTypeConfig.value = res.data
    }
  } catch (e) {
    console.error('加载商户类型配置失败', e)
  }
}

// 打开商户申请弹窗
const openMerchantApplyModal = async () => {
  // 先加载配置
  await loadMerchantTypeConfig()
  // 初始化表单
  merchantApplyForm.merchantType = 1
  merchantApplyForm.shopName = ''
  merchantApplyForm.companyName = ''
  merchantApplyForm.legalPerson = ''
  merchantApplyForm.legalIdCard = ''
  merchantApplyForm.licenseNo = ''
  merchantApplyForm.licenseImg = ''
  merchantApplyForm.idCardFront = ''
  merchantApplyForm.idCardBack = ''
  merchantApplyForm.contactPhone = userStore.userInfo.phone || ''
  merchantApplyForm.contactEmail = ''
  merchantApplyForm.businessAddress = ''
  merchantApplyForm.businessScope = ''
  // 清空待上传文件
  pendingFiles.idCardFront = null
  pendingFiles.idCardBack = null
  pendingFiles.license = null
  showMerchantApplyModal.value = true
}

// 选择文件（只存储，不立即上传）
const handleUpload = (file, type) => {
  // 存储文件对象
  if (type === 'idCardFront') {
    pendingFiles.idCardFront = file
    merchantApplyForm.idCardFront = 'pending'  // 标记已选择
  }
  if (type === 'idCardBack') {
    pendingFiles.idCardBack = file
    merchantApplyForm.idCardBack = 'pending'
  }
  if (type === 'license') {
    pendingFiles.license = file
    merchantApplyForm.licenseImg = 'pending'
  }
  message.success('文件已选择，提交时将自动上传')
  return false  // 阻止默认上传行为
}

// 提交商户申请
const submitMerchantApplyForm = async () => {
  // 验证表单
  if (!merchantApplyForm.shopName) {
    message.warning('请输入店铺名称')
    return
  }
  if (merchantApplyForm.merchantType === 2 && !merchantApplyForm.companyName) {
    message.warning('企业类型请填写企业名称')
    return
  }
  if (!merchantApplyForm.legalPerson) {
    message.warning('请输入法人/负责人姓名')
    return
  }
  if (!merchantApplyForm.legalIdCard) {
    message.warning('请输入身份证号')
    return
  }
  if (!pendingFiles.idCardFront) {
    message.warning('请选择身份证正面图片')
    return
  }
  if (!pendingFiles.idCardBack) {
    message.warning('请选择身份证反面图片')
    return
  }
  if (!merchantApplyForm.contactPhone) {
    message.warning('请输入联系电话')
    return
  }

  submittingApply.value = true
  try {
    // 先上传文件
    message.info('正在上传文件...')
    
    // 上传身份证正面
    if (pendingFiles.idCardFront) {
      uploadingIdCardFront.value = true
      const res1 = await uploadFile(pendingFiles.idCardFront, 'merchant')
      merchantApplyForm.idCardFront = res1.data?.url || res1.data
      uploadingIdCardFront.value = false
    }
    
    // 上传身份证反面
    if (pendingFiles.idCardBack) {
      uploadingIdCardBack.value = true
      const res2 = await uploadFile(pendingFiles.idCardBack, 'merchant')
      merchantApplyForm.idCardBack = res2.data?.url || res2.data
      uploadingIdCardBack.value = false
    }
    
    // 上传营业执照（如有）
    if (pendingFiles.license) {
      uploadingLicense.value = true
      const res3 = await uploadFile(pendingFiles.license, 'merchant')
      merchantApplyForm.licenseImg = res3.data?.url || res3.data
      uploadingLicense.value = false
    }

    // 提交申请
    await submitMerchantApply(merchantApplyForm)
    message.success('申请提交成功，请等待审核')
    showMerchantApplyModal.value = false
    
    // 清空待上传文件
    pendingFiles.idCardFront = null
    pendingFiles.idCardBack = null
    pendingFiles.license = null
    
    // 刷新商户状态
    await loadMerchantStatus()
  } catch (e) {
    message.error(e.message || '提交失败')
  } finally {
    submittingApply.value = false
    uploadingIdCardFront.value = false
    uploadingIdCardBack.value = false
    uploadingLicense.value = false
  }
}

// 获取商户类型的提成说明
const getCommissionDesc = computed(() => {
  const type = merchantApplyForm.merchantType
  if (type === 1 && merchantTypeConfig.value.individual) {
    return `平台提成 ${merchantTypeConfig.value.individual.commissionRate}%`
  }
  if (type === 2 && merchantTypeConfig.value.enterprise) {
    return `平台提成 ${merchantTypeConfig.value.enterprise.commissionRate}%`
  }
  return ''
})

// 定时器引用
let notificationTimer = null

onMounted(() => {
  loadWalletInfo()
  loadNotifications()
  loadMerchantStatus()
  
  // 每10秒刷新一次通知列表（更实时的消息提醒）
  notificationTimer = setInterval(() => {
    loadNotifications()
  }, 10000)
})

onUnmounted(() => {
  // 清理定时器
  if (notificationTimer) {
    clearInterval(notificationTimer)
    notificationTimer = null
  }
})
</script>

<style scoped>
.nav-active {
  background: rgba(0, 175, 225, 0.1);
  color: #00AFE1;
  font-weight: 700;
}
</style>
