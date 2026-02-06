<template>
  <div class="max-w-5xl mx-auto space-y-6">
    <!-- 加载中 -->
    <div v-if="loading" class="flex justify-center py-20">
      <n-spin size="large" />
    </div>

    <template v-else>
      <!-- 顶部导航 -->
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-4">
          <button @click="router.back()" class="w-10 h-10 bg-white rounded-full flex items-center justify-center text-gray-400 hover:text-tf hover:shadow-md transition">
            <i class="fas fa-arrow-left"></i>
          </button>
          <div>
            <h2 class="text-xl font-black text-gray-800">{{ order.title }}</h2>
            <p class="text-sm text-gray-400 mt-1">订单号: {{ order.orderNo }}</p>
          </div>
        </div>
        <div class="flex items-center gap-3">
          <span :class="getStatusClass(order.status)" class="px-4 py-1.5 rounded-full text-sm font-bold">
            {{ getStatusText(order.status) }}
          </span>
        </div>
      </div>

      <div class="grid grid-cols-3 gap-6">
        <!-- 左侧：订单信息和阶段进度 -->
        <div class="col-span-2 space-y-6">
          <!-- 订单信息卡片 -->
          <div class="bg-white rounded-3xl p-8 shadow-sm border border-gray-100">
            <h3 class="font-black text-gray-800 mb-6 flex items-center gap-2">
              <i class="fas fa-file-contract" style="color: #00AFE1;"></i>
              项目信息
            </h3>
            
            <div class="grid grid-cols-2 gap-6">
              <div>
                <p class="text-xs text-gray-400 font-bold uppercase">项目总金额</p>
                <p class="text-2xl font-black text-gray-800 mt-1">¥ {{ order.totalAmount?.toLocaleString() }}</p>
              </div>
              <div>
                <p class="text-xs text-gray-400 font-bold uppercase">已托管金额</p>
                <p class="text-2xl font-black mt-1" style="color: #00AFE1;">¥ {{ order.depositAmount?.toLocaleString() }}</p>
              </div>
              <div>
                <p class="text-xs text-gray-400 font-bold uppercase">服务商</p>
                <p class="text-lg font-bold text-gray-800 mt-1">{{ order.sellerName || '待接单' }}</p>
              </div>
              <div>
                <p class="text-xs text-gray-400 font-bold uppercase">预计交付日期</p>
                <p class="text-lg font-bold text-gray-800 mt-1">{{ formatDate(order.deliveryTime) || '-' }}</p>
              </div>
            </div>

            <!-- 技术栈和功能描述 -->
            <div v-if="order.techStack || order.features" class="mt-6 pt-6 border-t border-gray-100 space-y-4">
              <div v-if="order.techStack">
                <p class="text-xs text-gray-400 font-bold uppercase mb-2">技术栈要求</p>
                <p class="text-gray-600 text-sm">{{ order.techStack }}</p>
              </div>
              <div v-if="order.features">
                <p class="text-xs text-gray-400 font-bold uppercase mb-2">功能描述</p>
                <p class="text-gray-600 text-sm leading-relaxed whitespace-pre-line">{{ order.features }}</p>
              </div>
            </div>

            <div class="mt-6 pt-6 border-t border-gray-100">
              <p class="text-xs text-gray-400 font-bold uppercase mb-2">需求描述</p>
              <p class="text-gray-600 text-sm leading-relaxed">{{ order.description }}</p>
            </div>
          </div>

          <!-- 阶段进度 -->
          <div class="bg-white rounded-3xl p-8 shadow-sm border border-gray-100">
            <h3 class="font-black text-gray-800 mb-6 flex items-center gap-2">
              <i class="fas fa-tasks" style="color: #00AFE1;"></i>
              交付阶段
            </h3>

            <div class="space-y-6">
              <div 
                v-for="(stage, index) in order.stages" 
                :key="stage.id" 
                class="relative p-5 rounded-2xl border-2 transition"
                :class="getStageCardClass(stage.status)"
              >
                <!-- 阶段头部 -->
                <div class="flex items-start justify-between mb-4">
                  <div class="flex items-center gap-3">
                    <div 
                      class="w-10 h-10 rounded-full flex items-center justify-center text-white font-bold"
                      :style="{ background: getStageColor(stage.status) }"
                    >
                      <i v-if="stage.status === 3" class="fas fa-check"></i>
                      <span v-else>{{ index + 1 }}</span>
                    </div>
                    <div>
                      <h4 class="font-bold text-gray-800">{{ stage.stageName || stage.name }}</h4>
                      <div class="flex items-center gap-3 mt-1">
                        <span class="text-sm text-gray-500">¥ {{ stage.amount?.toLocaleString() }}</span>
                        <span v-if="stage.percent" class="text-xs px-2 py-0.5 rounded bg-gray-100 text-gray-500">
                          {{ stage.percent }}%
                        </span>
                      </div>
                    </div>
                  </div>
                  
                  <div class="flex items-center gap-2">
                    <span 
                      class="px-3 py-1 rounded-full text-xs font-bold"
                      :class="getStageStatusClass(stage.status)"
                    >
                      {{ getStageStatusText(stage.status) }}
                    </span>
                  </div>
                </div>

                <!-- 付款节点 -->
                <div v-if="stage.milestone" class="mb-3 flex items-center gap-2 text-sm text-gray-500">
                  <i class="fas fa-flag text-xs"></i>
                  <span>付款节点: {{ stage.milestone }}</span>
                </div>

                <!-- 风险说明 -->
                <div v-if="stage.riskNote" class="mb-3 text-xs text-orange-600 bg-orange-50 px-3 py-2 rounded-lg">
                  <i class="fas fa-shield-alt mr-1"></i>
                  {{ stage.riskNote }}
                </div>

                <!-- 交付说明 -->
                <div v-if="stage.deliveryDesc" class="mb-3 p-3 bg-slate-50 rounded-lg">
                  <p class="text-xs text-gray-400 mb-1">交付说明:</p>
                  <p class="text-sm text-gray-600 whitespace-pre-line">{{ stage.deliveryDesc }}</p>
                </div>

                <!-- 交付物附件 -->
                <div v-if="stage.evidenceUrl" class="mb-3 flex items-center gap-3 p-3 bg-blue-50 rounded-xl">
                  <i class="fas fa-paperclip" style="color: #00AFE1;"></i>
                  <span class="text-sm font-medium" style="color: #00AFE1;">交付物已上传</span>
                  <a 
                    :href="stage.evidenceUrl" 
                    target="_blank" 
                    :download="isArchiveFile(stage.evidenceUrl)"
                    class="text-xs underline" 
                    style="color: #00AFE1;"
                  >
                    <i v-if="isArchiveFile(stage.evidenceUrl)" class="fas fa-download mr-1"></i>
                    {{ isArchiveFile(stage.evidenceUrl) ? '下载文件' : '查看详情' }}
                  </a>
                  <span v-if="stage.evidenceHash" class="text-xs text-gray-400 ml-auto">
                    存证: {{ stage.evidenceHash.substring(0, 8) }}...
                  </span>
                </div>

                <!-- 操作按钮 -->
                <div v-if="stage.status === 2" class="flex gap-3 mt-4 pt-4 border-t border-gray-100">
                  <n-button 
                    type="primary" 
                    color="#00AFE1"
                    @click="handleAccept(stage)"
                  >
                    <template #icon><i class="fas fa-check"></i></template>
                    确认验收
                  </n-button>
                  <n-button 
                    type="error" 
                    ghost
                    @click="handleReject(stage)"
                  >
                    <template #icon><i class="fas fa-times"></i></template>
                    拒绝验收
                  </n-button>
                </div>

                <!-- 已结算 -->
                <div v-if="stage.status === 3 && stage.releaseTime" class="mt-3 text-xs text-green-600">
                  <i class="fas fa-check-circle mr-1"></i>
                  已结算于: {{ formatDateTime(stage.releaseTime) }}
                </div>

                <!-- 质保中 -->
                <div v-if="stage.status === 4" class="mt-3 text-xs text-teal-600">
                  <i class="fas fa-shield-alt mr-1"></i>
                  质保中，至: {{ stage.warrantyEndTime ? formatDate(stage.warrantyEndTime) : '待定' }}
                </div>

                <!-- 质保已释放 -->
                <div v-if="stage.status === 5 && stage.releaseTime" class="mt-3 text-xs text-green-600">
                  <i class="fas fa-check-circle mr-1"></i>
                  已释放于: {{ formatDateTime(stage.releaseTime) }}
                </div>
              </div>
            </div>

            <!-- 付款流程说明 -->
            <div class="mt-6 p-4 bg-gray-50 rounded-xl">
              <p class="text-xs text-gray-500 font-bold mb-2">付款流程说明</p>
              <div class="flex items-center gap-2 text-xs text-gray-400 flex-wrap">
                <span class="px-2 py-1 rounded bg-blue-100 text-blue-600">已托管</span>
                <span class="text-gray-300">→ 商家交付 →</span>
                <span class="px-2 py-1 rounded bg-purple-100 text-purple-600">待验收</span>
                <span class="text-gray-300">→ 验收通过 →</span>
                <span class="px-2 py-1 rounded bg-green-100 text-green-600">已结算</span>
                <span class="text-gray-300">/</span>
                <span class="px-2 py-1 rounded bg-teal-100 text-teal-600">质保中</span>
                <span class="text-gray-300">→</span>
                <span class="px-2 py-1 rounded bg-green-100 text-green-600">已释放</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：操作面板 -->
        <div class="space-y-6">
          <!-- 资金状态卡片 -->
          <div class="p-6 rounded-3xl text-white shadow-xl" style="background: linear-gradient(135deg, #00AFE1, #0088B3);">
            <p class="text-white/60 text-xs font-bold uppercase mb-2">当前托管资金</p>
            <h3 class="text-3xl font-black">¥ {{ order.depositAmount?.toLocaleString() }}</h3>
            
            <div class="mt-6 pt-4 border-t border-white/10 space-y-2 text-xs">
              <div class="flex justify-between">
                <span class="opacity-70">项目总金额</span>
                <span class="font-bold">¥ {{ order.totalAmount?.toLocaleString() }}</span>
              </div>
              <div class="flex justify-between">
                <span class="opacity-70">已释放金额</span>
                <span class="font-bold">¥ {{ order.releasedAmount?.toLocaleString() || 0 }}</span>
              </div>
              <div class="flex justify-between">
                <span class="opacity-70">待释放金额</span>
                <span class="font-bold">¥ {{ (order.depositAmount - (order.releasedAmount || 0))?.toLocaleString() }}</span>
              </div>
            </div>
          </div>

          <!-- 合同卡片 - 待签合同状态 -->
          <div v-if="order.status === 8" class="bg-amber-50 rounded-3xl p-6 border border-amber-200">
            <div class="flex items-center gap-3 mb-4">
              <div class="w-10 h-10 rounded-full bg-amber-500/20 flex items-center justify-center">
                <i class="fas fa-file-signature text-amber-600"></i>
              </div>
              <div>
                <h4 class="font-bold text-gray-800">项目合同</h4>
                <p class="text-xs text-amber-600">请查看并签署合同</p>
              </div>
            </div>
            
            <div class="space-y-2">
              <n-button 
                block 
                type="primary"
                color="#f59e0b"
                @click="router.push(`/user/orders/${order.id}/contract/sign`)"
              >
                <template #icon><i class="fas fa-signature"></i></template>
                查看并签署合同
              </n-button>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="bg-white rounded-3xl p-6 shadow-sm border border-gray-100 space-y-3">
            <!-- 待托管状态：托管付款按钮（余额不足时需手动托管） -->
            <template v-if="order.status === 1">
              <div class="p-4 bg-yellow-50 rounded-xl border border-yellow-100 mb-3">
                <p class="text-sm text-yellow-700 font-bold mb-1">
                  <i class="fas fa-exclamation-circle mr-1"></i> 余额不足，请充值后托管
                </p>
                <p class="text-xs text-yellow-600">
                  合同已签署，但您的余额不足以托管款项。请先充值后再进行托管付款，项目才能正式开始。
                </p>
              </div>
              <n-button 
                block 
                size="large" 
                type="primary" 
                color="#00AFE1" 
                class="font-bold"
                @click="startPayCountdown(); showPayModal = true"
              >
                <template #icon><i class="fas fa-shield-alt"></i></template>
                托管付款 ¥{{ firstStageAmount.toLocaleString() }}
              </n-button>
            </template>

            <!-- 待接单状态：取消订单按钮 -->
            <n-button 
              v-if="order.status === 0" 
              block 
              size="large" 
              type="error" 
              ghost
              class="font-bold"
              @click="handleCancel"
            >
              <template #icon><i class="fas fa-times"></i></template>
              取消订单（全额退款）
            </n-button>

            <!-- 待支付下一阶段：支付按钮 -->
            <template v-if="order.status === 7">
              <div class="p-4 bg-yellow-50 rounded-xl border border-yellow-100 mb-3">
                <p class="text-sm text-yellow-700 font-bold mb-1">
                  <i class="fas fa-exclamation-circle mr-1"></i> 当前阶段已完成
                </p>
                <p class="text-xs text-yellow-600">
                  请支付下一阶段款项以继续项目进度。商家将在收到托管后开始下一阶段工作。
                </p>
              </div>
              <n-button 
                block 
                size="large" 
                type="primary" 
                color="#00AFE1" 
                class="font-bold"
                :loading="payingNextStage"
                @click="handlePayNextStage"
              >
                <template #icon><i class="fas fa-credit-card"></i></template>
                支付下一阶段 ¥{{ nextStageAmount.toLocaleString() }}
              </n-button>
            </template>

            <!-- 质保中：释放质保款按钮 -->
            <template v-if="order.status === 9">
              <div class="p-4 bg-teal-50 rounded-xl border border-teal-100 mb-3">
                <p class="text-sm text-teal-700 font-bold mb-1">
                  <i class="fas fa-shield-alt mr-1"></i> 项目进入质保期
                </p>
                <p class="text-xs text-teal-600">
                  项目已验收通过，首付款和尾款已释放给商家。质保款将在质保期（{{ warrantyStage?.warrantyEndTime ? formatDate(warrantyStage.warrantyEndTime) : '15天后' }}）结束后释放。
                </p>
              </div>
              <n-button 
                block 
                size="large" 
                type="primary" 
                color="#14b8a6"
                class="font-bold"
                :loading="releasingWarranty"
                @click="handleReleaseWarranty"
              >
                <template #icon><i class="fas fa-unlock-alt"></i></template>
                释放质保款 ¥{{ warrantyAmount.toLocaleString() }}
              </n-button>
              <p class="text-xs text-gray-400 text-center mt-2">
                提前释放即表示您对商家服务满意，放弃剩余质保期
              </p>
            </template>

            <n-button v-if="order.status >= 2 && order.status !== 7 && order.status !== 9" block size="large" type="primary" color="#00AFE1" class="font-bold" @click="showChat = true">
              <template #icon><i class="fas fa-comments"></i></template>
              联系服务商
            </n-button>
            
            <n-button block size="large" ghost class="font-bold" @click="showEvidence = true">
              <template #icon><i class="fas fa-shield-check"></i></template>
              查看存证记录
            </n-button>

            <n-button 
              v-if="order.status >= 2 && order.status < 4" 
              block 
              size="large" 
              type="error" 
              ghost 
              class="font-bold" 
              @click="showDispute = true"
            >
              <template #icon><i class="fas fa-exclamation-triangle"></i></template>
              申请纠纷仲裁
            </n-button>
          </div>

          <!-- 安全提示 -->
          <div class="bg-yellow-50 p-4 rounded-2xl border border-yellow-100">
            <div class="flex gap-3">
              <i class="fas fa-shield-alt text-yellow-600 mt-1"></i>
              <div>
                <p class="text-xs font-bold text-yellow-700">安全提示</p>
                <p class="text-xs text-yellow-600 mt-1 leading-relaxed">
                  请勿通过微信/QQ等站外平台私下交易。站外交易无法享受"臻托"资金保障。
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- 验收确认弹窗 -->
    <n-modal v-model:show="showAcceptModal" preset="dialog" title="确认验收">
      <p class="text-gray-600 mb-4">确认验收项目: <strong class="text-tf">{{ order.title }}</strong></p>
      <div class="text-sm bg-blue-50 p-3 rounded-lg border border-blue-100 mb-3">
        <i class="fas fa-info-circle text-blue-600 mr-1"></i>
        <span class="text-blue-700">验收通过后：</span>
        <ul class="text-blue-600 mt-2 ml-5 list-disc">
          <li>首付款和尾款将立即释放给商家</li>
          <li>如有质保款，将进入质保期（15天），质保期结束后释放</li>
        </ul>
      </div>
      <p class="text-sm text-orange-600 bg-orange-50 p-3 rounded-lg">
        <i class="fas fa-exclamation-triangle mr-1"></i>
        请确保交付物符合要求后再确认验收。验收通过后无法撤销。
      </p>
      <template #action>
        <n-space>
          <n-button @click="showAcceptModal = false">取消</n-button>
          <n-button type="primary" color="#00AFE1" :loading="accepting" @click="confirmAccept">确认验收</n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 拒绝验收弹窗 -->
    <n-modal v-model:show="showRejectModal" preset="dialog" title="拒绝验收">
      <p class="text-gray-600 mb-4">请说明拒绝验收的原因，服务商将根据反馈进行修改。</p>
      <n-input 
        v-model:value="rejectReason" 
        type="textarea" 
        placeholder="请详细描述不满意的地方，以便服务商改进..."
        :rows="4"
      />
      <template #action>
        <n-space>
          <n-button @click="showRejectModal = false">取消</n-button>
          <n-button type="error" :loading="rejecting" @click="confirmReject">确认拒绝</n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 支付弹窗 -->
    <n-modal v-model:show="showPayModal" :mask-closable="false" style="width: 480px;">
      <div class="bg-white rounded-3xl overflow-hidden">
        <!-- 头部 -->
        <div class="bg-gradient-to-r from-cyan-500 to-blue-500 p-6 text-white text-center">
          <i class="fas fa-shield-alt text-4xl mb-3"></i>
          <h3 class="text-xl font-black">信托托管支付</h3>
          <p class="text-sm opacity-80 mt-1">资金由平台托管，安全有保障</p>
        </div>
        
        <!-- 内容 -->
        <div class="p-6">
          <!-- 倒计时 -->
          <div class="flex items-center justify-center gap-2 mb-6">
            <i class="fas fa-clock text-orange-500"></i>
            <span class="text-sm text-gray-500">支付有效期：</span>
            <span class="text-2xl font-black" :class="payCountdown < 60 ? 'text-red-500' : 'text-orange-500'">
              {{ formatCountdown }}
            </span>
          </div>

          <!-- 项目信息 -->
          <div class="bg-slate-50 rounded-xl p-4 mb-4">
            <p class="text-xs text-gray-400 uppercase font-bold mb-2">项目名称</p>
            <p class="font-bold text-gray-800">{{ order.title }}</p>
          </div>

          <!-- 支付金额 -->
          <div class="bg-cyan-50 rounded-xl p-4 mb-4 border border-cyan-100">
            <div class="flex justify-between items-center mb-3">
              <span class="text-gray-600">全部托管款项</span>
              <span class="text-2xl font-black" style="color: #00AFE1;">¥ {{ firstStageAmount.toLocaleString() }}</span>
            </div>
            <div class="text-xs text-gray-400 space-y-1">
              <div v-for="stage in order.stages" :key="stage.id" class="flex justify-between">
                <span>{{ stage.stageName }}</span>
                <span>¥{{ stage.amount?.toLocaleString() }}</span>
              </div>
              <div class="flex justify-between">
                <span>占比</span>
                <span>{{ order.stages?.[0]?.percent || 0 }}%</span>
              </div>
            </div>
          </div>

          <!-- 项目总金额 -->
          <div class="flex justify-between items-center text-sm text-gray-500 mb-6">
            <span>项目总金额</span>
            <span class="font-bold text-gray-700">¥ {{ order.totalAmount?.toLocaleString() }}</span>
          </div>

          <!-- 提示 -->
          <div class="bg-yellow-50 rounded-xl p-3 mb-6 border border-yellow-100">
            <p class="text-xs text-yellow-700">
              <i class="fas fa-info-circle mr-1"></i>
              全部款项将托管至平台。验收通过后释放首付款和尾款，质保款将在质保期结束后释放。
            </p>
          </div>

          <!-- 按钮 -->
          <div class="space-y-3">
            <n-button 
              block 
              size="large" 
              type="primary" 
              color="#00AFE1" 
              class="font-bold"
              :loading="paying"
              :disabled="payCountdown === 0"
              @click="handlePay"
            >
              <template #icon><i class="fas fa-lock"></i></template>
              {{ paying ? '支付中...' : '确认支付 ¥' + firstStageAmount.toLocaleString() }}
            </n-button>
            <n-button 
              block 
              size="large" 
              ghost 
              @click="showPayModal = false"
            >
              稍后支付
            </n-button>
          </div>
        </div>
      </div>
    </n-modal>

    <!-- 申诉弹窗 -->
    <n-modal v-model:show="showDispute" preset="dialog" title="申请纠纷仲裁" style="width: 600px;">
      <div class="space-y-4">
        <div class="p-4 bg-red-50 rounded-lg border border-red-100">
          <p class="text-sm text-red-700">
            <i class="fas fa-exclamation-triangle mr-2"></i>
            申诉一旦提交，订单将进入纠纷状态，平台将介入调解。请确保您已与服务商充分沟通后再提交申诉。
          </p>
        </div>

        <n-form-item label="申诉原因" required>
          <n-input 
            v-model:value="disputeForm.reason" 
            type="textarea" 
            placeholder="请详细描述您遇到的问题，包括：&#10;1. 具体问题描述&#10;2. 与服务商的沟通情况&#10;3. 您期望的解决方案"
            :rows="5"
          />
        </n-form-item>

        <n-form-item label="证据材料">
          <n-upload
            :custom-request="handleUploadEvidence"
            :show-file-list="false"
            multiple
            accept=".jpg,.jpeg,.png,.gif,.pdf,.doc,.docx"
          >
            <n-button dashed :loading="uploadingEvidence">
              <template #icon><i class="fas fa-cloud-upload-alt"></i></template>
              {{ uploadingEvidence ? '上传中...' : '上传证据文件' }}
            </n-button>
          </n-upload>
          <div v-if="disputeForm.evidenceUrls.length > 0" class="mt-2 space-y-1">
            <div v-for="(url, idx) in disputeForm.evidenceUrls" :key="idx" class="flex items-center gap-2 text-sm text-green-600">
              <i class="fas fa-check-circle"></i>
              <span>文件 {{ idx + 1 }}</span>
              <a :href="url" target="_blank" class="underline">查看</a>
              <button @click="removeEvidence(idx)" class="text-red-500 hover:text-red-700">
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
        </n-form-item>
      </div>
      <template #action>
        <n-space>
          <n-button @click="showDispute = false">取消</n-button>
          <n-button type="error" :loading="submittingDispute" @click="submitDispute">
            提交申诉
          </n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 聊天抽屉 -->
    <ChatDrawer 
      v-model:show="showChat" 
      :order-id="order.id" 
      title="联系服务商"
    />

    <!-- 存证记录抽屉 -->
    <EvidenceDrawer 
      v-model:show="showEvidence" 
      :order-id="order.id"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, inject } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import { getOrderDetail, acceptDelivery, rejectDelivery, cancelOrder, depositFunds, payNextStage, releaseWarranty } from '@/api/order'
import { createDispute } from '@/api/dispute'
import { uploadFile } from '@/api/file'
import ChatDrawer from '@/components/ChatDrawer.vue'
import EvidenceDrawer from '@/components/EvidenceDrawer.vue'

const router = useRouter()
const route = useRoute()
const message = useMessage()

// 注入刷新钱包方法
const refreshWallet = inject('refreshWallet', () => {})

const loading = ref(true)
const showChat = ref(false)
const showEvidence = ref(false)
const showDispute = ref(false)
const showAcceptModal = ref(false)
const showRejectModal = ref(false)
const showPayModal = ref(false)
const currentStage = ref(null)
const rejectReason = ref('')
const accepting = ref(false)
const rejecting = ref(false)
const submittingDispute = ref(false)
const uploadingEvidence = ref(false)
const paying = ref(false)
const payingNextStage = ref(false)
const releasingWarranty = ref(false)

// 支付倒计时
const payCountdown = ref(0)
let countdownTimer = null

const disputeForm = reactive({
  reason: '',
  evidenceUrls: []
})

// 计算全部托管金额（新流程：一次性托管全部款项）
const firstStageAmount = computed(() => {
  // 返回订单总金额
  return order.totalAmount || 0
})

// 计算下一阶段金额（状态7时使用）
const nextStageAmount = computed(() => {
  if (order.stages && order.stages.length > 0) {
    // 找到第一个状态为0（未开始）的阶段
    const nextStage = order.stages.find(s => s.status === 0)
    return nextStage?.amount || 0
  }
  return 0
})

// 计算质保款金额（状态9时使用）
const warrantyStage = computed(() => {
  if (order.stages && order.stages.length > 0) {
    // 找到类型为3（质保款）且状态为4（质保中）的阶段
    return order.stages.find(s => s.stageType === 3 && s.status === 4)
  }
  return null
})

const warrantyAmount = computed(() => {
  return warrantyStage.value?.amount || 0
})

// 格式化倒计时
const formatCountdown = computed(() => {
  const minutes = Math.floor(payCountdown.value / 60)
  const seconds = payCountdown.value % 60
  return `${minutes}:${seconds.toString().padStart(2, '0')}`
})

const order = reactive({
  id: null,
  orderNo: '',
  title: '',
  description: '',
  techStack: '',
  features: '',
  totalAmount: 0,
  depositAmount: 0,
  releasedAmount: 0,
  status: 0,
  sellerName: '',
  deliveryTime: '',
  stages: []
})

const loadOrder = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    // 后端返回格式: { order: {...}, stages: [...] }
    const orderData = res.data.order || res.data
    const stagesData = res.data.stages || []
    Object.assign(order, orderData, { stages: stagesData })
    
    // 如果是待托管状态且带有 needPay 参数，自动弹出支付弹窗
    if (order.status === 1 && route.query.needPay === 'true') {
      startPayCountdown()
      showPayModal.value = true
    }
    
    // 如果带有 openChat 参数，自动打开聊天窗口
    if (route.query.openChat === 'true') {
      showChat.value = true
    }
  } catch (e) {
    message.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

// 开始支付倒计时
const startPayCountdown = () => {
  // 新流程：合同签署后托管付款，给用户10分钟支付窗口（从打开弹窗开始）
  payCountdown.value = 10 * 60 // 10分钟
  
  if (countdownTimer) clearInterval(countdownTimer)
  
  countdownTimer = setInterval(() => {
    if (payCountdown.value > 0) {
      payCountdown.value--
    } else {
      clearInterval(countdownTimer)
      showPayModal.value = false
      message.warning('支付窗口已关闭，您可以重新点击托管付款')
    }
  }, 1000)
}

// 处理支付
const handlePay = async () => {
  paying.value = true
  try {
    await depositFunds(order.id)
    message.success('托管成功！项目已开始执行')
    showPayModal.value = false
    if (countdownTimer) clearInterval(countdownTimer)
    loadOrder()
    refreshWallet()
  } catch (e) {
    message.error(e.message || '托管失败')
  } finally {
    paying.value = false
  }
}

// 支付下一阶段款项
const handlePayNextStage = async () => {
  payingNextStage.value = true
  try {
    await payNextStage(order.id)
    message.success('支付成功，下一阶段已开始')
    loadOrder()
    refreshWallet()
  } catch (e) {
    message.error(e.message || '支付失败')
  } finally {
    payingNextStage.value = false
  }
}

// 释放质保款
const handleReleaseWarranty = async () => {
  releasingWarranty.value = true
  try {
    await releaseWarranty(order.id)
    message.success('质保款已释放，订单完成！')
    loadOrder()
    refreshWallet()
  } catch (e) {
    message.error(e.message || '释放失败')
  } finally {
    releasingWarranty.value = false
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.split('T')[0]
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace('T', ' ').substring(0, 16)
}

// 判断是否为压缩包文件
const isArchiveFile = (url) => {
  if (!url) return false
  const lowerUrl = url.toLowerCase()
  return lowerUrl.endsWith('.zip') || lowerUrl.endsWith('.rar') || 
         lowerUrl.endsWith('.7z') || lowerUrl.endsWith('.tar') || 
         lowerUrl.endsWith('.gz') || lowerUrl.endsWith('.tgz')
}

const getStatusText = (status) => {
  const map = { 0: '待接单', 1: '待托管', 2: '进行中', 3: '待验收', 4: '已完成', 5: '已取消', 6: '纠纷中', 7: '待支付下阶段', 8: '待签合同', 9: '质保中' }
  return map[status] || '未知'
}

const getStatusClass = (status) => {
  const map = {
    0: 'bg-gray-100 text-gray-500',
    1: 'bg-orange-50 text-orange-500',
    2: 'bg-blue-50 text-blue-500',
    3: 'bg-purple-50 text-purple-500',
    4: 'bg-green-50 text-green-500',
    5: 'bg-gray-100 text-gray-400',
    6: 'bg-red-50 text-red-500',
    7: 'bg-yellow-50 text-yellow-600',
    8: 'bg-amber-50 text-amber-600',
    9: 'bg-teal-50 text-teal-600'
  }
  return map[status] || 'bg-gray-100 text-gray-500'
}

const getStageStatusText = (status) => {
  // 0未开始, 1已托管, 2待验收, 3已验收/已结算, 4质保中, 5质保已释放
  const map = { 0: '未开始', 1: '已托管', 2: '待验收', 3: '已结算', 4: '质保中', 5: '已释放' }
  return map[status] || '未知'
}

const getStageStatusClass = (status) => {
  const map = {
    0: 'bg-gray-100 text-gray-400',
    1: 'bg-blue-50 text-blue-500',
    2: 'bg-purple-50 text-purple-500',
    3: 'bg-green-50 text-green-500',
    4: 'bg-teal-50 text-teal-500',
    5: 'bg-green-50 text-green-500'
  }
  return map[status] || 'bg-gray-100 text-gray-400'
}

const getStageCardClass = (status) => {
  const map = {
    0: 'border-gray-100 bg-gray-50',
    1: 'border-blue-200 bg-blue-50/30',
    2: 'border-purple-200 bg-purple-50/30',
    3: 'border-green-200 bg-green-50/30'
  }
  return map[status] || 'border-gray-100'
}

const getStageColor = (status) => {
  const map = {
    0: '#9CA3AF',
    1: '#00AFE1',
    2: '#8B5CF6',
    3: '#10B981'
  }
  return map[status] || '#9CA3AF'
}

const handleCancel = async () => {
  if (!confirm('确定要取消订单吗？取消后托管资金将全额退回。')) {
    return
  }
  try {
    await cancelOrder(order.id)
    message.success('订单已取消，资金已退回')
    loadOrder()
    refreshWallet() // 刷新钱包信息
  } catch (e) {
    message.error(e.message || '取消失败')
  }
}

const handleAccept = (stage) => {
  currentStage.value = stage
  showAcceptModal.value = true
}

const handleReject = (stage) => {
  currentStage.value = stage
  rejectReason.value = ''
  showRejectModal.value = true
}

const confirmAccept = async () => {
  accepting.value = true
  try {
    await acceptDelivery(order.id, currentStage.value.id)
    message.success('验收成功，资金已释放给服务商')
    showAcceptModal.value = false
    loadOrder()
    refreshWallet() // 刷新钱包信息（冻结金额减少）
  } catch (e) {
    message.error(e.message || '验收失败')
  } finally {
    accepting.value = false
  }
}

const confirmReject = async () => {
  if (!rejectReason.value.trim()) {
    message.warning('请填写拒绝原因')
    return
  }
  rejecting.value = true
  try {
    await rejectDelivery(order.id, currentStage.value.id, rejectReason.value)
    message.success('已拒绝验收')
    showRejectModal.value = false
    loadOrder()
  } catch (e) {
    message.error(e.message || '操作失败')
  } finally {
    rejecting.value = false
  }
}

// 上传申诉证据
const handleUploadEvidence = async ({ file }) => {
  uploadingEvidence.value = true
  try {
    const res = await uploadFile(file.file, 'evidence')
    // res.data 是 { url: "...", name: "..." } 对象，需要取 url 属性
    disputeForm.evidenceUrls.push(res.data.url || res.data)
    message.success('上传成功')
  } catch (e) {
    message.error(e.message || '上传失败')
  } finally {
    uploadingEvidence.value = false
  }
}

// 移除证据
const removeEvidence = (idx) => {
  disputeForm.evidenceUrls.splice(idx, 1)
}

// 提交申诉
const submitDispute = async () => {
  if (!disputeForm.reason.trim()) {
    message.warning('请填写申诉原因')
    return
  }
  submittingDispute.value = true
  try {
    await createDispute({
      orderId: order.id,
      reason: disputeForm.reason,
      evidenceUrls: disputeForm.evidenceUrls
    })
    message.success('申诉已提交，平台将尽快介入处理')
    showDispute.value = false
    disputeForm.reason = ''
    disputeForm.evidenceUrls = []
    loadOrder()
  } catch (e) {
    message.error(e.message || '提交失败')
  } finally {
    submittingDispute.value = false
  }
}

onMounted(() => {
  loadOrder()
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>
