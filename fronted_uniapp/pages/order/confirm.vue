<template>
  <view class="page">
    <!-- 顶部背景 -->
    <view class="header-bg">
      <view class="back-btn" @click="goBack">
        <text class="iconfont">←</text>
      </view>
      <text class="header-title">确认订单内容</text>
      <text class="header-desc">您的资金将被托管至信托账户，直到您点击验收</text>
    </view>

    <!-- 表单内容 -->
    <view class="form-container">
      <!-- 订单信息 -->
      <view class="form-card">
        <text class="form-label">订单标题</text>
        <text class="form-value">{{ orderInfo.title }}</text>
        
        <view class="divider"></view>
        
        <view class="info-row">
          <view class="info-item">
            <text class="info-label">托管金额</text>
            <text class="info-value price">¥ {{ orderInfo.amount }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">交付周期</text>
            <text class="info-value">{{ orderInfo.deliveryDays }}个工作日</text>
          </view>
        </view>
      </view>

      <!-- 付款方式 -->
      <view class="section">
        <text class="section-title">付款方式</text>
        
        <view 
          class="pay-method" 
          :class="{ active: payMethod === 'wechat' }"
          @click="payMethod = 'wechat'"
        >
          <view class="pay-icon wechat">
            <text>微</text>
          </view>
          <text class="pay-name">微信支付 (信托托管模式)</text>
          <view class="pay-check" v-if="payMethod === 'wechat'">✓</view>
          <view class="pay-radio" v-else></view>
        </view>

        <view 
          class="pay-method disabled"
          @click="payMethod = 'alipay'"
        >
          <view class="pay-icon alipay">
            <text>支</text>
          </view>
          <text class="pay-name">支付宝支付</text>
          <view class="pay-radio"></view>
        </view>
      </view>

      <!-- 安全提示 -->
      <view class="safe-tips">
        <text class="tips-icon">🛡</text>
        <text class="tips-text">提示：平台将对该笔资金进行24小时全程监管。商家完成交付并经您验收后，资金才会释放。若产生纠纷，可随时申请平台仲裁。</text>
      </view>

      <!-- 提交按钮 -->
      <view class="submit-btn" @click="handleSubmit">
        立即托管资金并发布
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
      payMethod: 'wechat',
      orderInfo: {
        title: '臻托平台UI原型设计项目',
        amount: '3,500.00',
        deliveryDays: 7
      }
    }
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    async handleSubmit() {
      const userStore = useUserStore()
      if (!userStore.checkLogin()) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        setTimeout(() => {
          uni.navigateTo({ url: '/pages/login/index' })
        }, 1500)
        return
      }
      
      uni.showLoading({ title: '提交中...' })
      
      try {
        // 创建订单
        const createRes = await request({
          url: '/order/create',
          method: 'POST',
          data: {
            title: this.orderInfo.title,
            description: '订单描述',
            categoryId: 5,
            deliveryTime: new Date(Date.now() + this.orderInfo.deliveryDays * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
            stages: [{
              name: '全款',
              amount: parseFloat(this.orderInfo.amount.replace(/,/g, '')),
              percent: 100,
              milestone: '完成交付'
            }]
          }
        })
        
        if (createRes.data && createRes.data.id) {
          // 托管资金
          await request({
            url: `/order/${createRes.data.id}/deposit`,
            method: 'POST'
          })
          
          uni.hideLoading()
          uni.showToast({ title: '订单创建成功', icon: 'success' })
          setTimeout(() => {
            uni.reLaunch({ url: '/pages/index/index' })
          }, 1500)
        }
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.message || '创建失败', icon: 'none' })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #fff;
}

.header-bg {
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  padding: 120rpx 40rpx 160rpx;
  position: relative;
}

.back-btn {
  position: absolute;
  top: 100rpx;
  left: 32rpx;
  color: #fff;
  font-size: 40rpx;
}

.header-title {
  font-size: 48rpx;
  font-weight: 700;
  color: #fff;
  display: block;
  margin-bottom: 16rpx;
}

.header-desc {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
}

.form-container {
  margin-top: -80rpx;
  padding: 0 32rpx 60rpx 32rpx;
  position: relative;
  z-index: 10;
}

.form-card {
  background: #f8f9fa;
  border-radius: 48rpx;
  padding: 48rpx;
}

.form-label {
  font-size: 24rpx;
  color: #999;
  text-transform: uppercase;
  font-weight: 600;
  display: block;
  margin-bottom: 16rpx;
}

.form-value {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.divider {
  height: 2rpx;
  background: #eee;
  margin: 32rpx 0;
}

.info-row {
  display: flex;
  gap: 48rpx;
}

.info-item {
  flex: 1;
}

.info-label {
  font-size: 26rpx;
  color: #999;
  display: block;
}

.info-value {
  font-size: 32rpx;
  font-weight: 700;
  color: #333;
  margin-top: 12rpx;
  display: block;
  
  &.price {
    color: #00AFE1;
    font-size: 36rpx;
  }
}

.section {
  margin-top: 40rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 24rpx;
  padding: 0 8rpx;
}

.pay-method {
  display: flex;
  align-items: center;
  padding: 32rpx;
  background: #fff;
  border: 4rpx solid #eee;
  border-radius: 32rpx;
  margin-bottom: 20rpx;
  
  &.active {
    border-color: #00AFE1;
    background: rgba(0, 175, 225, 0.05);
  }
  
  &.disabled {
    opacity: 0.5;
  }
}

.pay-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
  
  text {
    color: #fff;
    font-size: 32rpx;
    font-weight: 700;
  }
  
  &.wechat {
    background: #07c160;
  }
  
  &.alipay {
    background: #1677ff;
  }
}

.pay-name {
  flex: 1;
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}

.pay-check {
  width: 48rpx;
  height: 48rpx;
  background: #00AFE1;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
}

.pay-radio {
  width: 40rpx;
  height: 40rpx;
  border: 4rpx solid #ddd;
  border-radius: 50%;
}

.safe-tips {
  margin-top: 40rpx;
  padding: 32rpx;
  background: #fffbeb;
  border: 2rpx solid #fef3c7;
  border-radius: 32rpx;
  display: flex;
  gap: 16rpx;
}

.tips-icon {
  font-size: 36rpx;
}

.tips-text {
  font-size: 24rpx;
  color: #b45309;
  line-height: 1.6;
  flex: 1;
}

.submit-btn {
  margin-top: 48rpx;
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  color: #fff;
  font-size: 34rpx;
  font-weight: 700;
  padding: 40rpx;
  border-radius: 48rpx;
  text-align: center;
  box-shadow: 0 16rpx 40rpx rgba(0, 175, 225, 0.3);
}
</style>
