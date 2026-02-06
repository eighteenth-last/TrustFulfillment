<template>
  <view class="page">
    <!-- 步骤指示器 -->
    <view class="steps">
      <view class="step-item" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
        <view class="step-circle">1</view>
        <text class="step-text">基本信息</text>
      </view>
      <view class="step-line" :class="{ active: currentStep > 1 }"></view>
      <view class="step-item" :class="{ active: currentStep >= 2, completed: currentStep > 2 }">
        <view class="step-circle">2</view>
        <text class="step-text">付款条款</text>
      </view>
      <view class="step-line" :class="{ active: currentStep > 2 }"></view>
      <view class="step-item" :class="{ active: currentStep >= 3 }">
        <view class="step-circle">3</view>
        <text class="step-text">法律条款</text>
      </view>
    </view>

    <!-- 内容区域 -->
    <scroll-view scroll-y class="content-scroll">
      <!-- 步骤1: 基本信息 -->
      <view v-if="currentStep === 1" class="step-content">
        <view class="form-card">
          <view class="card-title">合同基本信息</view>
          
          <view class="form-item">
            <text class="form-label">合同标题 <text class="required">*</text></text>
            <input 
              class="form-input" 
              v-model="form.title" 
              placeholder="请输入合同标题"
            />
          </view>

          <view class="form-item">
            <text class="form-label">甲方（委托方）</text>
            <view class="form-readonly">{{ order.buyerName || '加载中...' }}</view>
          </view>

          <view class="form-item">
            <text class="form-label">乙方（服务方）</text>
            <view class="form-readonly">当前用户</view>
          </view>

          <view class="form-item">
            <text class="form-label">项目描述 <text class="required">*</text></text>
            <textarea 
              class="form-textarea" 
              v-model="form.projectDesc" 
              placeholder="详细描述项目内容、功能需求等"
              :maxlength="500"
            />
            <text class="char-count">{{ form.projectDesc.length }}/500</text>
          </view>

          <view class="form-item">
            <text class="form-label">技术要求</text>
            <textarea 
              class="form-textarea" 
              v-model="form.techRequirements" 
              placeholder="描述技术栈、架构要求等"
              :maxlength="300"
            />
            <text class="char-count">{{ form.techRequirements.length }}/300</text>
          </view>

          <view class="form-item">
            <text class="form-label">验收标准 <text class="required">*</text></text>
            <textarea 
              class="form-textarea" 
              v-model="form.deliveryStandard" 
              placeholder="详细描述项目验收标准、交付物要求等"
              :maxlength="500"
            />
            <text class="char-count">{{ form.deliveryStandard.length }}/500</text>
          </view>

          <view class="form-item">
            <text class="form-label">交付期限</text>
            <picker mode="date" :value="form.deliveryDeadline" @change="onDateChange">
              <view class="form-picker">
                {{ form.deliveryDeadline || '请选择交付日期' }}
              </view>
            </picker>
          </view>
        </view>
      </view>

      <!-- 步骤2: 付款条款 -->
      <view v-if="currentStep === 2" class="step-content">
        <view class="form-card">
          <view class="card-title">付款条款</view>
          
          <view class="amount-card">
            <text class="amount-label">合同总金额</text>
            <text class="amount-value">¥{{ order.totalAmount ? order.totalAmount.toLocaleString() : '0' }}</text>
          </view>

          <view class="stages-list">
            <view class="stage-item" v-for="(stage, index) in stages" :key="index">
              <view class="stage-number">{{ index + 1 }}</view>
              <view class="stage-info">
                <text class="stage-name">{{ stage.stageName }}</text>
                <text class="stage-milestone">{{ stage.milestone }}</text>
              </view>
              <view class="stage-amount">
                <text class="amount">¥{{ stage.amount ? stage.amount.toLocaleString() : '0' }}</text>
                <text class="percent">{{ stage.percent }}%</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 步骤3: 法律条款 -->
      <view v-if="currentStep === 3" class="step-content">
        <view class="form-card">
          <view class="card-title">法律条款</view>
          
          <view class="clause-item">
            <view class="clause-header" @click="toggleClause('breach')">
              <text class="clause-title">违约条款</text>
              <text class="clause-icon">{{ expandedClauses.breach ? '▼' : '▶' }}</text>
            </view>
            <textarea 
              v-if="expandedClauses.breach"
              class="clause-textarea" 
              v-model="form.breachClause"
              :maxlength="500"
            />
          </view>

          <view class="clause-item">
            <view class="clause-header" @click="toggleClause('confidential')">
              <text class="clause-title">保密协议</text>
              <text class="clause-icon">{{ expandedClauses.confidential ? '▼' : '▶' }}</text>
            </view>
            <textarea 
              v-if="expandedClauses.confidential"
              class="clause-textarea" 
              v-model="form.confidentialClause"
              :maxlength="500"
            />
          </view>

          <view class="clause-item">
            <view class="clause-header" @click="toggleClause('ip')">
              <text class="clause-title">知识产权归属</text>
              <text class="clause-icon">{{ expandedClauses.ip ? '▼' : '▶' }}</text>
            </view>
            <textarea 
              v-if="expandedClauses.ip"
              class="clause-textarea" 
              v-model="form.ipClause"
              :maxlength="500"
            />
          </view>

          <view class="clause-item">
            <view class="clause-header" @click="toggleClause('dispute')">
              <text class="clause-title">争议解决方式</text>
              <text class="clause-icon">{{ expandedClauses.dispute ? '▼' : '▶' }}</text>
            </view>
            <textarea 
              v-if="expandedClauses.dispute"
              class="clause-textarea" 
              v-model="form.disputeClause"
              :maxlength="500"
            />
          </view>
        </view>
      </view>

      <!-- 底部安全距离 -->
      <view style="height: 200rpx;"></view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="btn-group">
        <button 
          v-if="currentStep > 1" 
          class="btn btn-secondary" 
          @click="prevStep"
        >
          上一步
        </button>
        <button 
          v-if="currentStep < 3" 
          class="btn btn-primary" 
          @click="nextStep"
        >
          下一步
        </button>
        <button 
          v-if="currentStep === 3" 
          class="btn btn-secondary" 
          @click="saveDraft"
        >
          保存草稿
        </button>
        <button 
          v-if="currentStep === 3" 
          class="btn btn-primary" 
          @click="submitContract"
        >
          提交并签署
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { request } from '@/utils/request'

export default {
  data() {
    return {
      orderId: null,
      currentStep: 1,
      order: {},
      stages: [],
      form: {
        title: '',
        projectDesc: '',
        techRequirements: '',
        deliveryStandard: '',
        deliveryDeadline: '',
        breachClause: '',
        confidentialClause: '',
        ipClause: '',
        disputeClause: ''
      },
      expandedClauses: {
        breach: true,
        confidential: false,
        ip: false,
        dispute: false
      },
      loading: false
    }
  },
  onLoad(options) {
    if (options.orderId) {
      this.orderId = options.orderId
      this.loadData()
    } else {
      uni.showToast({ title: '订单ID缺失', icon: 'none' })
      setTimeout(() => uni.navigateBack(), 1500)
    }
  },
  methods: {
    async loadData() {
      uni.showLoading({ title: '加载中...' })
      try {
        // 加载订单信息
        const orderRes = await request({
          url: `/order/${this.orderId}`,
          method: 'GET'
        })
        if (orderRes.data) {
          this.order = orderRes.data.order || orderRes.data
          this.stages = orderRes.data.stages || []
          
          // 初始化表单
          this.form.title = this.order.title + ' - 项目合同'
          this.form.projectDesc = this.order.description || ''
          this.form.techRequirements = this.order.techStack || ''
          this.form.deliveryDeadline = this.order.deliveryTime ? this.formatDate(this.order.deliveryTime) : ''
        }

        // 尝试加载已有合同
        try {
          const contractRes = await request({
            url: `/contract/order/${this.orderId}`,
            method: 'GET'
          })
          if (contractRes.data) {
            // 填充已有合同数据
            this.form = {
              title: contractRes.data.title || this.form.title,
              projectDesc: contractRes.data.projectDesc || this.form.projectDesc,
              techRequirements: contractRes.data.techRequirements || this.form.techRequirements,
              deliveryStandard: contractRes.data.deliveryStandard || '',
              deliveryDeadline: contractRes.data.deliveryDeadline ? this.formatDate(contractRes.data.deliveryDeadline) : this.form.deliveryDeadline,
              breachClause: contractRes.data.breachClause || this.getDefaultClause('breach'),
              confidentialClause: contractRes.data.confidentialClause || this.getDefaultClause('confidential'),
              ipClause: contractRes.data.ipClause || this.getDefaultClause('ip'),
              disputeClause: contractRes.data.disputeClause || this.getDefaultClause('dispute')
            }
          } else {
            // 使用默认条款
            this.initDefaultClauses()
          }
        } catch (e) {
          // 没有合同，使用默认条款
          this.initDefaultClauses()
        }
      } catch (e) {
        console.error('加载数据失败', e)
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },
    initDefaultClauses() {
      this.form.breachClause = this.getDefaultClause('breach')
      this.form.confidentialClause = this.getDefaultClause('confidential')
      this.form.ipClause = this.getDefaultClause('ip')
      this.form.disputeClause = this.getDefaultClause('dispute')
    },
    getDefaultClause(type) {
      const clauses = {
        breach: '1. 甲方逾期付款超过3日，需支付应付款项0.1%/日的违约金\n2. 乙方逾期交付超过7日，甲方有权解除合同并要求退款\n3. 任一方违约导致合同解除，违约方承担对方实际损失',
        confidential: '1. 双方对项目相关技术资料、商业信息负有保密义务\n2. 保密期限为合同终止后2年\n3. 违反保密义务需承担违约责任',
        ip: '1. 乙方交付成果的知识产权归甲方所有\n2. 乙方保证交付物不侵犯第三方知识产权\n3. 如涉及第三方开源组件，乙方需提前说明',
        dispute: '1. 本合同争议首先通过平台调解解决\n2. 调解不成的，任一方可向合同签订地人民法院提起诉讼'
      }
      return clauses[type] || ''
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    onDateChange(e) {
      this.form.deliveryDeadline = e.detail.value
    },
    toggleClause(type) {
      this.expandedClauses[type] = !this.expandedClauses[type]
    },
    nextStep() {
      // 验证当前步骤
      if (this.currentStep === 1) {
        if (!this.form.title) {
          uni.showToast({ title: '请填写合同标题', icon: 'none' })
          return
        }
        if (!this.form.projectDesc) {
          uni.showToast({ title: '请填写项目描述', icon: 'none' })
          return
        }
        if (!this.form.deliveryStandard) {
          uni.showToast({ title: '请填写验收标准', icon: 'none' })
          return
        }
      }
      
      if (this.currentStep < 3) {
        this.currentStep++
      }
    },
    prevStep() {
      if (this.currentStep > 1) {
        this.currentStep--
      }
    },
    async saveDraft() {
      uni.showLoading({ title: '保存中...' })
      try {
        await request({
          url: '/contract/create',
          method: 'POST',
          data: {
            orderId: this.orderId,
            ...this.form
          }
        })
        uni.hideLoading()
        uni.showToast({ title: '保存成功', icon: 'success' })
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.message || '保存失败', icon: 'none' })
      }
    },
    async submitContract() {
      // 最终验证
      if (!this.form.title || !this.form.projectDesc || !this.form.deliveryStandard) {
        uni.showToast({ title: '请完善必填信息', icon: 'none' })
        return
      }

      uni.showLoading({ title: '提交中...' })
      try {
        // 先保存合同
        const saveRes = await request({
          url: '/contract/create',
          method: 'POST',
          data: {
            orderId: this.orderId,
            ...this.form
          }
        })

        const contractId = saveRes.data?.id
        if (!contractId) {
          throw new Error('合同保存失败')
        }

        // 检查合同状态，如果已经提交过（状态不是0），直接跳转签署页面
        const contractRes = await request({
          url: `/contract/${contractId}`,
          method: 'GET'
        })
        
        if (contractRes.data && contractRes.data.status !== 0) {
          // 合同已提交，直接跳转签署页面
          uni.hideLoading()
          uni.showModal({
            title: '提示',
            content: '合同已提交，请进行签署',
            showCancel: false,
            success: () => {
              uni.redirectTo({ 
                url: `/pages/user/contract-sign?orderId=${this.orderId}&contractId=${contractId}` 
              })
            }
          })
          return
        }

        // 提交合同
        await request({
          url: `/contract/${contractId}/submit`,
          method: 'POST'
        })

        uni.hideLoading()
        
        // 提示并跳转到签署页面
        uni.showModal({
          title: '提交成功',
          content: '合同已提交，请进行签署',
          showCancel: false,
          success: () => {
            uni.redirectTo({ 
              url: `/pages/user/contract-sign?orderId=${this.orderId}&contractId=${contractId}` 
            })
          }
        })
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.message || '提交失败', icon: 'none' })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 160rpx;
}

.steps {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 40rpx 32rpx;
  background: #fff;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}

.step-circle {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: #e5e7eb;
  color: #9ca3af;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 600;
  transition: all 0.3s;
}

.step-item.active .step-circle {
  background: #00AFE1;
  color: #fff;
}

.step-item.completed .step-circle {
  background: #10b981;
  color: #fff;
}

.step-text {
  font-size: 22rpx;
  color: #9ca3af;
  transition: all 0.3s;
}

.step-item.active .step-text {
  color: #00AFE1;
  font-weight: 600;
}

.step-line {
  flex: 1;
  height: 4rpx;
  background: #e5e7eb;
  margin: 0 16rpx;
  transition: all 0.3s;
}

.step-line.active {
  background: #00AFE1;
}

.content-scroll {
  height: calc(100vh - 136rpx - 160rpx);
  padding: 32rpx;
}

.step-content {
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.form-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
}

.card-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 32rpx;
  padding-bottom: 24rpx;
  border-bottom: 2rpx solid #f5f5f5;
}

.form-item {
  margin-bottom: 32rpx;
}

.form-label {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 16rpx;
}

.required {
  color: #ef4444;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 24rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: #333;
  border: 2rpx solid transparent;
}

.form-input:focus,
.form-textarea:focus {
  border-color: #00AFE1;
  background: #fff;
}

.form-textarea {
  min-height: 200rpx;
}

.form-readonly {
  padding: 24rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: #999;
}

.form-picker {
  padding: 24rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: #333;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 22rpx;
  color: #999;
  margin-top: 8rpx;
}

.amount-card {
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 32rpx;
  text-align: center;
}

.amount-label {
  display: block;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 16rpx;
}

.amount-value {
  display: block;
  font-size: 56rpx;
  font-weight: 900;
  color: #fff;
}

.stages-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.stage-item {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 32rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
}

.stage-number {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: #00AFE1;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 600;
  flex-shrink: 0;
}

.stage-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.stage-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}

.stage-milestone {
  font-size: 22rpx;
  color: #999;
}

.stage-amount {
  text-align: right;
}

.stage-amount .amount {
  display: block;
  font-size: 32rpx;
  font-weight: 600;
  color: #00AFE1;
}

.stage-amount .percent {
  display: block;
  font-size: 22rpx;
  color: #999;
  margin-top: 4rpx;
}

.clause-item {
  margin-bottom: 24rpx;
}

.clause-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  cursor: pointer;
}

.clause-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}

.clause-icon {
  font-size: 20rpx;
  color: #999;
}

.clause-textarea {
  width: 100%;
  min-height: 240rpx;
  padding: 24rpx;
  background: #fff;
  border: 2rpx solid #e5e7eb;
  border-radius: 16rpx;
  font-size: 26rpx;
  color: #333;
  line-height: 1.6;
  margin-top: 16rpx;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.btn-group {
  display: flex;
  gap: 24rpx;
}

.btn {
  flex: 1;
  height: 88rpx;
  border-radius: 24rpx;
  font-size: 30rpx;
  font-weight: 600;
  border: none;
}

.btn-secondary {
  background: #f5f7fa;
  color: #666;
}

.btn-primary {
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  color: #fff;
}
</style>
