<template>
  <view class="page">
    <!-- 状态栏占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <!-- Logo -->
    <view class="logo-section">
      <image class="logo-img" src="/static/logo.png" mode="aspectFit" />
      <text class="logo-title">臻托 Trust</text>
      <text class="logo-desc">让每一笔交易都值得托付</text>
    </view>

    <!-- 登录表单 -->
    <view class="form-section">
      <view class="input-group">
        <text class="input-icon">📱</text>
        <input 
          v-model="phone" 
          type="number" 
          placeholder="请输入手机号" 
          maxlength="11"
        />
      </view>
      
      <view class="input-group">
        <text class="input-icon">🔑</text>
        <input 
          v-model="code" 
          type="number" 
          placeholder="请输入验证码" 
          maxlength="6"
        />
        <view class="code-btn" :class="{ disabled: countdown > 0 || sendingCode }" @click="sendCode">
          {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
        </view>
      </view>

      <view class="login-btn" :class="{ loading: logging }" @click="handlePhoneLogin">
        {{ logging ? '登录中...' : '登录 / 注册' }}
      </view>

      <!-- 微信登录 -->
      <view class="divider">
        <text class="divider-line"></text>
        <text class="divider-text">或</text>
        <text class="divider-line"></text>
      </view>

      <button class="wechat-btn" open-type="getPhoneNumber" @getphonenumber="handleGetPhone" v-if="false">
        <text class="wechat-icon">微</text>
        <text>微信手机号快捷登录</text>
      </button>

      <view class="wechat-btn" @click="handleWechatLogin">
        <text class="wechat-icon">微</text>
        <text>微信一键登录</text>
      </view>
    </view>

    <!-- 协议 -->
    <view class="agreement">
      <view class="checkbox" :class="{ checked: agreed }" @click="agreed = !agreed">
        <text v-if="agreed">✓</text>
      </view>
      <text class="agreement-text">
        登录即表示同意
        <text class="link">《用户协议》</text>
        和
        <text class="link">《隐私政策》</text>
      </text>
    </view>
  </view>
</template>

<script>
import { useUserStore } from '@/stores/user'
import { sendSmsCode, loginByPhone, loginByWechat, bindPhone } from '@/utils/api'

export default {
  data() {
    return {
      statusBarHeight: 20,
      phone: '',
      code: '',
      countdown: 0,
      agreed: false,
      timer: null,
      logging: false,
      sendingCode: false
    }
  },
  mounted() {
    const sysInfo = uni.getSystemInfoSync()
    this.statusBarHeight = sysInfo.statusBarHeight
  },
  beforeDestroy() {
    if (this.timer) clearInterval(this.timer)
  },
  methods: {
    // 发送验证码
    async sendCode() {
      if (this.countdown > 0 || this.sendingCode) return
      if (!this.phone || this.phone.length !== 11) {
        return uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
      }
      
      this.sendingCode = true
      try {
        await sendSmsCode(this.phone, 1) // type=1 登录/注册
        uni.showToast({ title: '验证码已发送', icon: 'none' })
        this.startCountdown()
      } catch (e) {
        const errorMsg = e.message || '发送失败'
        if (errorMsg.includes('频繁')) {
          uni.showToast({ title: '发送过于频繁，请10秒后再试', icon: 'none', duration: 2000 })
        } else {
          uni.showToast({ title: errorMsg, icon: 'none' })
        }
      } finally {
        this.sendingCode = false
      }
    },
    
    startCountdown() {
      this.countdown = 60
      this.timer = setInterval(() => {
        this.countdown--
        if (this.countdown <= 0) {
          clearInterval(this.timer)
        }
      }, 1000)
    },
    
    // 手机号验证码登录
    async handlePhoneLogin() {
      if (this.logging) return
      
      if (!this.agreed) {
        return uni.showToast({ title: '请先同意用户协议', icon: 'none' })
      }
      if (!this.phone || this.phone.length !== 11) {
        return uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
      }
      if (!this.code || this.code.length < 4) {
        return uni.showToast({ title: '请输入验证码', icon: 'none' })
      }
      
      this.logging = true
      uni.showLoading({ title: '登录中...' })
      
      try {
        const res = await loginByPhone(this.phone, this.code)
        this.handleLoginSuccess(res.data)
      } catch (e) {
        uni.showToast({ title: e.message || '登录失败', icon: 'none' })
      } finally {
        this.logging = false
        uni.hideLoading()
      }
    },
    
    // 微信一键登录
    async handleWechatLogin() {
      if (!this.agreed) {
        return uni.showToast({ title: '请先同意用户协议', icon: 'none' })
      }
      
      uni.showLoading({ title: '登录中...' })
      
      try {
        // 获取微信登录 code
        const loginRes = await new Promise((resolve, reject) => {
          uni.login({
            provider: 'weixin',
            success: resolve,
            fail: reject
          })
        })
        
        // 调用后端微信登录接口
        const res = await loginByWechat(loginRes.code)
        
        // 直接登录，不再处理绑定手机号逻辑
        this.handleLoginSuccess(res.data)
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.message || '微信登录失败', icon: 'none' })
      }
    },
    
    // 登录成功处理
    handleLoginSuccess(data) {
      uni.hideLoading()
      const userStore = useUserStore()
      userStore.setToken(data.token)
      userStore.setUserInfo(data.userInfo)
      
      uni.showToast({ 
        title: data.isNewUser ? '注册成功' : '登录成功', 
        icon: 'success' 
      })
      
      setTimeout(() => {
        uni.switchTab({ url: '/pages/index/index' })
      }, 1500)
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #fff;
  padding: 0 48rpx;
}

.status-bar {
  width: 100%;
}

.logo-section {
  text-align: center;
  padding: 80rpx 0 64rpx;
}

.logo-img {
  width: 160rpx;
  height: 160rpx;
  border-radius: 48rpx;
  margin: 0 auto 32rpx;
  box-shadow: 0 16rpx 48rpx rgba(0, 0, 0, 0.15);
}

.logo-title {
  font-size: 48rpx;
  font-weight: 900;
  color: #333;
  display: block;
}

.logo-desc {
  font-size: 28rpx;
  color: #999;
  margin-top: 12rpx;
}

.form-section {
  margin-top: 48rpx;
}

.input-group {
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border-radius: 32rpx;
  padding: 0 32rpx;
  margin-bottom: 24rpx;
}

.input-icon {
  font-size: 36rpx;
  margin-right: 20rpx;
}

input {
  flex: 1;
  height: 100rpx;
  font-size: 30rpx;
}

.code-btn {
  font-size: 26rpx;
  color: #00AFE1;
  font-weight: 600;
  white-space: nowrap;
  
  &.disabled {
    color: #999;
  }
}

.login-btn {
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  color: #fff;
  font-size: 34rpx;
  font-weight: 600;
  padding: 36rpx;
  border-radius: 32rpx;
  text-align: center;
  margin-top: 48rpx;
  box-shadow: 0 16rpx 40rpx rgba(0, 175, 225, 0.3);
  
  &.loading {
    opacity: 0.7;
  }
}

.divider {
  display: flex;
  align-items: center;
  margin: 64rpx 0;
}

.divider-line {
  flex: 1;
  height: 2rpx;
  background: #eee;
}

.divider-text {
  padding: 0 32rpx;
  font-size: 26rpx;
  color: #999;
}

.wechat-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  background: #07c160;
  color: #fff;
  font-size: 30rpx;
  font-weight: 600;
  padding: 36rpx;
  border-radius: 32rpx;
  border: none;
  
  &::after {
    border: none;
  }
}

.wechat-icon {
  width: 48rpx;
  height: 48rpx;
  background: #fff;
  color: #07c160;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: 700;
}

.agreement {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  margin-top: 64rpx;
}

.checkbox {
  width: 40rpx;
  height: 40rpx;
  border: 4rpx solid #ddd;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  color: #fff;
  flex-shrink: 0;
  margin-top: 4rpx;
  
  &.checked {
    background: #00AFE1;
    border-color: #00AFE1;
  }
}

.agreement-text {
  font-size: 26rpx;
  color: #999;
  line-height: 1.6;
}

.link {
  color: #00AFE1;
}
</style>
