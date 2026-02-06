<template>
  <view class="page">
    <scroll-view scroll-y class="content-scroll">
      <!-- 合同头部 -->
      <view class="contract-header">
        <text class="contract-title">{{ contract.title }}</text>
        <text class="contract-no">合同编号: {{ contract.contractNo }}</text>
      </view>

      <!-- 双方信息 -->
      <view class="parties-card">
        <view class="party-item">
          <text class="party-label">甲方（委托方）</text>
          <text class="party-name">{{ contract.partyAName }}</text>
          <view class="sign-status" :class="{ signed: contract.partyASignTime }">
            {{ contract.partyASignTime ? '✓ 已签署' : '待签署' }}
          </view>
        </view>
        <view class="party-divider"></view>
        <view class="party-item">
          <text class="party-label">乙方（服务方）</text>
          <text class="party-name">{{ contract.partyBName }}</text>
          <view class="sign-status" :class="{ signed: contract.partyBSignTime }">
            {{ contract.partyBSignTime ? '✓ 已签署' : '待签署' }}
          </view>
        </view>
      </view>

      <!-- 合同金额 -->
      <view class="amount-card">
        <text class="amount-label">合同总金额</text>
        <text class="amount-value">¥{{ formatCurrency(contract.totalAmount) }}</text>
      </view>

      <!-- 合同内容 -->
      <view class="content-card">
        <view class="content-section">
          <text class="section-title">项目描述</text>
          <text class="section-text">{{ contract.projectDesc }}</text>
        </view>

        <view class="content-section" v-if="contract.techRequirements">
          <text class="section-title">技术要求</text>
          <text class="section-text">{{ contract.techRequirements }}</text>
        </view>

        <view class="content-section">
          <text class="section-title">验收标准</text>
          <text class="section-text">{{ contract.deliveryStandard }}</text>
        </view>

        <view class="content-section" v-if="contract.deliveryDeadline">
          <text class="section-title">交付期限</text>
          <text class="section-text">{{ formatDate(contract.deliveryDeadline) }}</text>
        </view>
      </view>

      <!-- 付款阶段 -->
      <view class="stages-card" v-if="stages.length > 0">
        <text class="card-title">付款阶段</text>
        <view class="stage-item" v-for="(stage, index) in stages" :key="index">
          <view class="stage-number">{{ index + 1 }}</view>
          <view class="stage-info">
            <text class="stage-name">{{ stage.stageName }}</text>
            <text class="stage-milestone">{{ stage.milestone }}</text>
          </view>
          <view class="stage-amount">
            <text class="amount">¥{{ formatCurrency(stage.amount) }}</text>
            <text class="percent">{{ stage.percent }}%</text>
          </view>
        </view>
      </view>

      <!-- 法律条款 -->
      <view class="clauses-card">
        <text class="card-title">法律条款</text>
        
        <view class="clause-section" v-if="contract.breachClause">
          <text class="clause-title">违约条款</text>
          <text class="clause-text">{{ contract.breachClause }}</text>
        </view>

        <view class="clause-section" v-if="contract.confidentialClause">
          <text class="clause-title">保密协议</text>
          <text class="clause-text">{{ contract.confidentialClause }}</text>
        </view>

        <view class="clause-section" v-if="contract.ipClause">
          <text class="clause-title">知识产权归属</text>
          <text class="clause-text">{{ contract.ipClause }}</text>
        </view>

        <view class="clause-section" v-if="contract.disputeClause">
          <text class="clause-title">争议解决方式</text>
          <text class="clause-text">{{ contract.disputeClause }}</text>
        </view>
      </view>

      <!-- 同意条款 -->
      <view class="agree-card" v-if="canSign">
        <checkbox-group @change="onAgreeChange">
          <label class="agree-label">
            <checkbox value="agree" :checked="agreed" />
            <text class="agree-text">我已阅读并同意以上合同条款</text>
          </label>
        </checkbox-group>
      </view>

      <!-- 签名区域 -->
      <view class="signature-card" v-if="canSign && agreed">
        <view class="signature-header">
          <text class="signature-title">请在下方签名</text>
          <button class="clear-btn" @click="clearSignature">清除</button>
        </view>
        <view class="signature-canvas-wrapper" :class="{ 'has-signature': hasSignature }">
          <canvas 
            canvas-id="signatureCanvas" 
            class="signature-canvas"
            @touchstart="touchStart"
            @touchmove="touchMove"
            @touchend="touchEnd"
            disable-scroll="true"
          ></canvas>
          <view class="signature-placeholder" v-if="!hasSignature">
            <text class="placeholder-icon">✍️</text>
            <text class="placeholder-text">请用手指在此处签名</text>
          </view>
        </view>
        <text class="signature-tip">签名将作为您同意合同的法律凭证</text>
      </view>

      <!-- 底部安全距离 -->
      <view style="height: 200rpx;"></view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar" v-if="canSign">
      <button class="sign-btn" :disabled="!agreed || !hasSignature" @click="handleSign">
        确认签署
      </button>
    </view>

    <!-- 已签署提示 -->
    <view class="bottom-bar" v-else-if="alreadySigned">
      <view class="signed-tip">
        <text class="signed-icon">✓</text>
        <text class="signed-text">您已签署此合同</text>
      </view>
    </view>
  </view>
</template>

<script>
import { request } from '@/utils/request'
import { useUserStore } from '@/stores/user'

export default {
  data() {
    return {
      orderId: null,
      contractId: null,
      contract: {},
      stages: [],
      agreed: false,
      userRole: '', // 'buyer' or 'seller'
      loading: false,
      // 签名相关
      hasSignature: false,
      signatureData: '',
      ctx: null,
      isDrawing: false,
      lastX: 0,
      lastY: 0,
      canvasWidth: 0,
      canvasHeight: 0
    }
  },
  computed: {
    canSign() {
      // 判断当前用户是否可以签署
      const userStore = useUserStore()
      const userId = userStore.userInfo?.id
      
      if (!userId || !this.contract.id) {
        console.log('canSign: false - 缺少userId或contractId')
        return false
      }
      
      console.log('canSign 检查:', {
        userRole: this.userRole,
        contractStatus: this.contract.status,
        partyASignTime: this.contract.partyASignTime,
        partyBSignTime: this.contract.partyBSignTime
      })
      
      // 如果是买家且合同状态为1（待甲方签署），且甲方未签署
      if (this.userRole === 'buyer' && this.contract.status === 1 && !this.contract.partyASignTime) {
        console.log('canSign: true - 买家可以签署')
        return true
      }
      
      // 如果是商家，在状态1或2时都可以签署（只要乙方还没签）
      if (this.userRole === 'seller' && (this.contract.status === 1 || this.contract.status === 2) && !this.contract.partyBSignTime) {
        console.log('canSign: true - 商家可以签署')
        return true
      }
      
      console.log('canSign: false - 不满足签署条件')
      return false
    },
    alreadySigned() {
      // 判断当前用户是否已签署
      if (this.userRole === 'buyer' && this.contract.partyASignTime) {
        return true
      }
      if (this.userRole === 'seller' && this.contract.partyBSignTime) {
        return true
      }
      return false
    }
  },
  onLoad(options) {
    if (options.orderId && options.contractId) {
      this.orderId = options.orderId
      this.contractId = options.contractId
      this.loadData()
      this.initCanvas()
    } else {
      uni.showToast({ title: '参数缺失', icon: 'none' })
      setTimeout(() => uni.navigateBack(), 1500)
    }
  },
  methods: {
    async loadData() {
      uni.showLoading({ title: '加载中...' })
      try {
        // 加载合同详情
        const res = await request({
          url: `/contract/${this.contractId}`,
          method: 'GET'
        })
        
        console.log('合同详情:', res.data)
        
        if (res.data) {
          this.contract = res.data
          
          // 解析付款阶段（从 paymentTerms JSON 字符串）
          if (res.data.paymentTerms) {
            try {
              this.stages = JSON.parse(res.data.paymentTerms)
              console.log('付款阶段:', this.stages)
            } catch (e) {
              console.error('解析付款阶段失败', e)
              this.stages = []
            }
          } else {
            this.stages = []
          }
          
          // 判断用户角色
          const userStore = useUserStore()
          const userId = userStore.userInfo?.id
          
          console.log('当前用户ID:', userId)
          console.log('甲方ID:', res.data.partyAId)
          console.log('乙方ID:', res.data.partyBId)
          
          // 通过订单信息判断角色
          const orderRes = await request({
            url: `/order/${this.orderId}`,
            method: 'GET'
          })
          
          if (orderRes.data) {
            const order = orderRes.data.order || orderRes.data
            if (order.buyerId === userId) {
              this.userRole = 'buyer'
              console.log('用户角色: 买家')
            } else if (order.sellerId === userId) {
              this.userRole = 'seller'
              console.log('用户角色: 商家')
            }
          }
        }
      } catch (e) {
        console.error('加载合同失败', e)
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },
    onAgreeChange(e) {
      this.agreed = e.detail.value.includes('agree')
    },
    // 初始化画布
    initCanvas() {
      // 获取系统信息
      const sysInfo = uni.getSystemInfoSync()
      this.canvasWidth = sysInfo.windowWidth - 64 // 减去左右padding
      this.canvasHeight = 400 // rpx转px大约是200rpx
      
      // 创建canvas上下文
      this.ctx = uni.createCanvasContext('signatureCanvas', this)
      
      // 设置画布样式
      this.ctx.setStrokeStyle('#1a1a2e')
      this.ctx.setLineWidth(3)
      this.ctx.setLineCap('round')
      this.ctx.setLineJoin('round')
      
      // 设置白色背景
      this.ctx.setFillStyle('#ffffff')
      this.ctx.fillRect(0, 0, this.canvasWidth, this.canvasHeight)
      this.ctx.draw()
    },
    // 开始绘制
    touchStart(e) {
      this.isDrawing = true
      const touch = e.touches[0]
      this.lastX = touch.x
      this.lastY = touch.y
    },
    // 绘制中
    touchMove(e) {
      if (!this.isDrawing) return
      
      const touch = e.touches[0]
      const x = touch.x
      const y = touch.y
      
      this.ctx.beginPath()
      this.ctx.moveTo(this.lastX, this.lastY)
      this.ctx.lineTo(x, y)
      this.ctx.stroke()
      this.ctx.draw(true) // 保留之前的内容
      
      this.lastX = x
      this.lastY = y
      this.hasSignature = true
    },
    // 结束绘制
    touchEnd() {
      if (this.isDrawing) {
        this.isDrawing = false
        // 保存签名为图片
        this.saveSignature()
      }
    },
    // 保存签名
    saveSignature() {
      uni.canvasToTempFilePath({
        canvasId: 'signatureCanvas',
        success: (res) => {
          this.signatureData = res.tempFilePath
        },
        fail: (err) => {
          console.error('保存签名失败', err)
        }
      }, this)
    },
    // 清除签名
    clearSignature() {
      this.ctx.setFillStyle('#ffffff')
      this.ctx.fillRect(0, 0, this.canvasWidth, this.canvasHeight)
      this.ctx.draw()
      this.hasSignature = false
      this.signatureData = ''
    },
    async handleSign() {
      if (!this.agreed) {
        uni.showToast({ title: '请先阅读并同意合同条款', icon: 'none' })
        return
      }

      if (!this.hasSignature) {
        uni.showToast({ title: '请先签名', icon: 'none' })
        return
      }

      uni.showModal({
        title: '确认签署',
        content: '签署后合同将生效，请确认您已仔细阅读所有条款',
        confirmText: '确认签署',
        success: async (res) => {
          if (res.confirm) {
            uni.showLoading({ title: '签署中...' })
            try {
              // 将签名图片转为base64
              const base64 = await this.fileToBase64(this.signatureData)
              
              await request({
                url: `/contract/${this.contractId}/sign`,
                method: 'POST',
                data: {
                  signature: base64
                }
              })
              
              uni.hideLoading()
              uni.showModal({
                title: '签署成功',
                content: '合同已签署，项目即将开始',
                showCancel: false,
                success: () => {
                  // 返回订单详情页
                  uni.navigateBack()
                }
              })
            } catch (e) {
              uni.hideLoading()
              uni.showToast({ title: e.message || '签署失败', icon: 'none' })
            }
          }
        }
      })
    },
    // 文件转base64
    fileToBase64(filePath) {
      return new Promise((resolve, reject) => {
        uni.getFileSystemManager().readFile({
          filePath: filePath,
          encoding: 'base64',
          success: (res) => {
            resolve('data:image/png;base64,' + res.data)
          },
          fail: (err) => {
            reject(err)
          }
        })
      })
    },
    formatCurrency(value) {
      if (!value) return '0'
      return parseFloat(value).toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 })
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN')
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

.content-scroll {
  height: calc(100vh - 160rpx);
  padding: 32rpx;
}

.contract-header {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 24rpx;
  text-align: center;
}

.contract-title {
  display: block;
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 16rpx;
}

.contract-no {
  display: block;
  font-size: 24rpx;
  color: #999;
}

.parties-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 24rpx;
  display: flex;
  gap: 32rpx;
}

.party-item {
  flex: 1;
  text-align: center;
}

.party-label {
  display: block;
  font-size: 22rpx;
  color: #999;
  margin-bottom: 12rpx;
}

.party-name {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 16rpx;
}

.sign-status {
  display: inline-block;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  background: #f5f5f5;
  color: #999;
  
  &.signed {
    background: rgba(16, 185, 129, 0.1);
    color: #10b981;
  }
}

.party-divider {
  width: 2rpx;
  background: #f5f5f5;
}

.amount-card {
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 24rpx;
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

.content-card,
.stages-card,
.clauses-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  margin-bottom: 24rpx;
}

.card-title {
  display: block;
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 32rpx;
  padding-bottom: 24rpx;
  border-bottom: 2rpx solid #f5f5f5;
}

.content-section,
.clause-section {
  margin-bottom: 32rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.section-title,
.clause-title {
  display: block;
  font-size: 26rpx;
  font-weight: 600;
  color: #666;
  margin-bottom: 12rpx;
}

.section-text,
.clause-text {
  display: block;
  font-size: 28rpx;
  color: #333;
  line-height: 1.6;
  white-space: pre-wrap;
}

.stage-item {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 24rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  margin-bottom: 16rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
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

.agree-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.agree-label {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.agree-text {
  font-size: 26rpx;
  color: #666;
}

.signature-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.signature-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.signature-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}

.clear-btn {
  padding: 12rpx 24rpx;
  background: #f5f5f5;
  color: #666;
  font-size: 24rpx;
  border-radius: 12rpx;
  border: none;
  line-height: 1;
}

.signature-canvas-wrapper {
  position: relative;
  width: 100%;
  height: 400rpx;
  border: 4rpx dashed #e5e7eb;
  border-radius: 16rpx;
  overflow: hidden;
  background: #fff;
  margin-bottom: 16rpx;
}

.signature-canvas-wrapper.has-signature {
  border-style: solid;
  border-color: #10b981;
}

.signature-canvas {
  width: 100%;
  height: 100%;
}

.signature-placeholder {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  pointer-events: none;
}

.placeholder-icon {
  font-size: 80rpx;
}

.placeholder-text {
  font-size: 26rpx;
  color: #999;
}

.signature-tip {
  display: block;
  font-size: 22rpx;
  color: #999;
  text-align: center;
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

.sign-btn {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  color: #fff;
  font-size: 30rpx;
  font-weight: 600;
  border-radius: 24rpx;
  border: none;
  
  &:disabled {
    opacity: 0.5;
  }
}

.signed-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  padding: 24rpx;
  background: rgba(16, 185, 129, 0.1);
  border-radius: 24rpx;
}

.signed-icon {
  font-size: 40rpx;
  color: #10b981;
}

.signed-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #10b981;
}
</style>
