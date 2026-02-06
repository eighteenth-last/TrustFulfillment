<template>
  <view class="page">
    <!-- 充值金额 -->
    <view class="amount-section">
      <text class="section-title">充值金额</text>
      <view class="amount-input">
        <text class="currency">¥</text>
        <input 
          type="digit" 
          v-model="amount" 
          placeholder="请输入充值金额"
          placeholder-class="placeholder"
          @input="onAmountInput"
        />
      </view>
      
      <!-- 快捷金额 -->
      <view class="quick-amounts">
        <view 
          class="quick-item" 
          v-for="item in quickAmounts" 
          :key="item"
          :class="{ active: amount == item }"
          @click="amount = item"
        >
          ¥{{ item }}
        </view>
      </view>
    </view>

    <!-- 支付方式 -->
    <view class="pay-section">
      <text class="section-title">支付方式</text>
      
      <view 
        class="pay-method" 
        v-for="method in payMethods" 
        :key="method.type"
        :class="{ active: selectedPayType === method.type, disabled: !method.enabled }"
        @click="selectPayMethod(method)"
      >
        <view class="pay-icon" :class="method.type">
          <text>{{ method.icon }}</text>
        </view>
        <view class="pay-info">
          <text class="pay-name">{{ method.name }}</text>
          <text class="pay-desc" v-if="method.desc">{{ method.desc }}</text>
        </view>
        <view class="pay-check" v-if="selectedPayType === method.type">
          <text class="iconfont icon-check">✓</text>
        </view>
        <view class="pay-radio" v-else></view>
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-btn" @click="handleSubmit">
      确认充值
    </view>

    <!-- 支付二维码弹窗 -->
    <view class="qrcode-modal" v-if="showQrcode" @click="closeQrcode">
      <view class="qrcode-content" @click.stop>
        <view class="qrcode-header">
          <text class="qrcode-title">扫码支付</text>
          <text class="qrcode-close" @click="closeQrcode">✕</text>
        </view>
        
        <view class="qrcode-amount">¥{{ amount }}</view>
        
        <view class="qrcode-box">
          <image v-if="qrcodeUrl" :src="qrcodeUrl" class="qrcode-img" mode="aspectFit" />
          <view v-else class="qrcode-loading">
            <text>生成中...</text>
          </view>
        </view>
        
        <view class="qrcode-tip">
          <text v-if="selectedPayType === 'alipay'">请使用支付宝扫码支付</text>
          <text v-else-if="selectedPayType === 'wechat'">请使用微信扫码支付</text>
          <text v-else>支付完成后将自动到账</text>
        </view>
        
        <view class="qrcode-status" v-if="paymentStatus">
          <text class="status-icon">{{ paymentStatus === 'success' ? '✓' : '⏳' }}</text>
          <text class="status-text">{{ paymentStatusText }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { request } from '@/utils/request'

export default {
  data() {
    return {
      amount: '',
      quickAmounts: [100, 500, 1000, 5000],
      selectedPayType: 'mock',
      payMethods: [
        {
          type: 'mock',
          name: '虚拟支付（测试）',
          desc: '3秒后自动完成支付',
          icon: '💳',
          enabled: true
        },
        {
          type: 'alipay',
          name: '支付宝',
          desc: '推荐使用',
          icon: '支',
          enabled: false
        },
        {
          type: 'wechat',
          name: '微信支付',
          desc: '',
          icon: '微',
          enabled: false
        }
      ],
      showQrcode: false,
      qrcodeUrl: '',
      orderNo: '',
      paymentStatus: '',
      pollingTimer: null
    }
  },
  computed: {
    paymentStatusText() {
      const map = {
        'pending': '等待支付中...',
        'success': '支付成功！',
        'failed': '支付失败'
      }
      return map[this.paymentStatus] || ''
    }
  },
  onUnload() {
    this.stopPolling()
  },
  methods: {
    onAmountInput(e) {
      // 限制只能输入数字和小数点
      let value = e.detail.value
      value = value.replace(/[^\d.]/g, '')
      // 只保留一个小数点
      const parts = value.split('.')
      if (parts.length > 2) {
        value = parts[0] + '.' + parts.slice(1).join('')
      }
      // 小数点后最多两位
      if (parts.length === 2 && parts[1].length > 2) {
        value = parts[0] + '.' + parts[1].substring(0, 2)
      }
      this.amount = value
    },
    selectPayMethod(method) {
      if (!method.enabled) {
        uni.showToast({ 
          title: '该支付方式暂未开通', 
          icon: 'none' 
        })
        return
      }
      this.selectedPayType = method.type
    },
    async handleSubmit() {
      if (!this.amount || parseFloat(this.amount) <= 0) {
        uni.showToast({ title: '请输入充值金额', icon: 'none' })
        return
      }
      
      if (parseFloat(this.amount) < 1) {
        uni.showToast({ title: '充值金额不能小于1元', icon: 'none' })
        return
      }
      
      uni.showLoading({ title: '创建订单中...' })
      
      try {
        const res = await request({
          url: '/pay/recharge',
          method: 'POST',
          data: {
            amount: parseFloat(this.amount),
            payType: this.selectedPayType
          }
        })
        
        uni.hideLoading()
        
        if (res.data) {
          this.orderNo = res.data.orderNo
          
          if (this.selectedPayType === 'mock') {
            // 虚拟支付：显示提示并开始轮询
            this.showQrcode = true
            this.qrcodeUrl = '' // 虚拟支付不需要二维码
            this.paymentStatus = 'pending'
            this.startPolling()
          } else if (res.data.qrcode) {
            // 真实支付：显示二维码
            this.qrcodeUrl = res.data.qrcode
            this.showQrcode = true
            this.paymentStatus = 'pending'
            this.startPolling()
          } else {
            uni.showToast({ title: '创建支付订单失败', icon: 'none' })
          }
        }
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.message || '创建订单失败', icon: 'none' })
      }
    },
    startPolling() {
      this.stopPolling()
      
      let count = 0
      const maxCount = 60 // 最多轮询60次（60秒）
      
      this.pollingTimer = setInterval(async () => {
        count++
        
        if (count > maxCount) {
          this.stopPolling()
          this.paymentStatus = 'failed'
          uni.showToast({ title: '支付超时', icon: 'none' })
          return
        }
        
        try {
          const res = await request({
            url: `/pay/status/${this.orderNo}`,
            method: 'GET'
          })
          
          if (res.data && res.data.status === 'success') {
            this.stopPolling()
            this.paymentStatus = 'success'
            
            setTimeout(() => {
              this.closeQrcode()
              uni.showToast({ title: '充值成功', icon: 'success' })
              setTimeout(() => {
                uni.navigateBack()
              }, 1500)
            }, 1000)
          }
        } catch (e) {
          console.error('轮询支付状态失败', e)
        }
      }, 1000)
    },
    stopPolling() {
      if (this.pollingTimer) {
        clearInterval(this.pollingTimer)
        this.pollingTimer = null
      }
    },
    closeQrcode() {
      this.stopPolling()
      this.showQrcode = false
      this.qrcodeUrl = ''
      this.orderNo = ''
      this.paymentStatus = ''
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f0f4f8;
  padding: 32rpx;
}

.amount-section, .pay-section {
  background: #fff;
  border-radius: 32rpx;
  padding: 40rpx;
  margin-bottom: 32rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 32rpx;
}

.amount-input {
  display: flex;
  align-items: center;
  background: #f5f7fa;
  border-radius: 24rpx;
  padding: 0 32rpx;
  height: 120rpx;
  margin-bottom: 32rpx;
}

.currency {
  font-size: 48rpx;
  font-weight: 600;
  color: #00AFE1;
  margin-right: 16rpx;
}

.amount-input input {
  flex: 1;
  font-size: 48rpx;
  font-weight: 600;
  color: #333;
}

.placeholder {
  color: #9ca3af;
  font-weight: 400;
}

.quick-amounts {
  display: flex;
  gap: 16rpx;
}

.quick-item {
  flex: 1;
  text-align: center;
  padding: 24rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: #666;
  border: 2rpx solid transparent;
  
  &.active {
    background: rgba(0, 175, 225, 0.1);
    color: #00AFE1;
    border-color: #00AFE1;
    font-weight: 600;
  }
}

.pay-method {
  display: flex;
  align-items: center;
  padding: 32rpx;
  background: #f5f7fa;
  border-radius: 24rpx;
  margin-bottom: 16rpx;
  border: 3rpx solid transparent;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  &.active {
    background: rgba(0, 175, 225, 0.05);
    border-color: #00AFE1;
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
  font-size: 32rpx;
  font-weight: 700;
  color: #fff;
  
  &.mock {
    background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  }
  
  &.alipay {
    background: #1677ff;
  }
  
  &.wechat {
    background: #07c160;
  }
}

.pay-info {
  flex: 1;
}

.pay-name {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  display: block;
}

.pay-desc {
  font-size: 24rpx;
  color: #999;
  margin-top: 4rpx;
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

.submit-btn {
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  color: #fff;
  font-size: 34rpx;
  font-weight: 700;
  padding: 40rpx;
  border-radius: 48rpx;
  text-align: center;
  box-shadow: 0 16rpx 40rpx rgba(0, 175, 225, 0.3);
  margin-top: 48rpx;
}

/* 二维码弹窗 */
.qrcode-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.qrcode-content {
  width: 600rpx;
  background: #fff;
  border-radius: 32rpx;
  padding: 48rpx;
}

.qrcode-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32rpx;
}

.qrcode-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
}

.qrcode-close {
  font-size: 48rpx;
  color: #999;
  line-height: 1;
}

.qrcode-amount {
  text-align: center;
  font-size: 56rpx;
  font-weight: 900;
  color: #00AFE1;
  margin-bottom: 32rpx;
}

.qrcode-box {
  width: 400rpx;
  height: 400rpx;
  margin: 0 auto 32rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qrcode-img {
  width: 100%;
  height: 100%;
}

.qrcode-loading {
  font-size: 28rpx;
  color: #999;
}

.qrcode-tip {
  text-align: center;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 24rpx;
}

.qrcode-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  padding: 24rpx;
  background: rgba(0, 175, 225, 0.1);
  border-radius: 16rpx;
}

.status-icon {
  font-size: 32rpx;
  color: #00AFE1;
}

.status-text {
  font-size: 28rpx;
  color: #00AFE1;
  font-weight: 600;
}
</style>
