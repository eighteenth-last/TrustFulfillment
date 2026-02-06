<template>
  <view class="page">
    <!-- 安全等级 -->
    <view class="security-level">
      <view class="level-icon">🛡️</view>
      <view class="level-info">
        <text class="level-title">账户安全等级</text>
        <view class="level-bar">
          <view class="level-progress" :style="{ width: securityLevel + '%' }"></view>
        </view>
        <text class="level-text">{{ getLevelText(securityLevel) }}</text>
      </view>
    </view>

    <!-- 安全设置 -->
    <view class="menu-card">
      <view class="menu-item" @click="goPage('phone')">
        <view class="menu-left">
          <text class="menu-icon">📱</text>
          <text class="menu-label">手机号</text>
        </view>
        <view class="menu-right">
          <text class="menu-value">{{ phoneDisplay }}</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>

      <view class="menu-item" @click="goPage('password')">
        <view class="menu-left">
          <text class="menu-icon">🔑</text>
          <text class="menu-label">登录密码</text>
        </view>
        <view class="menu-right">
          <text class="menu-value">{{ hasPassword ? '已设置' : '未设置' }}</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>

      <view class="menu-item" @click="goPage('payPassword')">
        <view class="menu-left">
          <text class="menu-icon">💳</text>
          <text class="menu-label">支付密码</text>
        </view>
        <view class="menu-right">
          <text class="menu-value">{{ hasPayPassword ? '已设置' : '未设置' }}</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 账户管理 -->
    <view class="menu-card">
      <view class="menu-item" @click="goPage('realname')">
        <view class="menu-left">
          <text class="menu-icon">👤</text>
          <text class="menu-label">实名认证</text>
        </view>
        <view class="menu-right">
          <text class="menu-value" :class="{ verified: isRealname }">
            {{ isRealname ? '已认证' : '未认证' }}
          </text>
          <text class="menu-arrow">›</text>
        </view>
      </view>

      <view class="menu-item" @click="goPage('bankcard')">
        <view class="menu-left">
          <text class="menu-icon">💰</text>
          <text class="menu-label">银行卡管理</text>
        </view>
        <view class="menu-right">
          <text class="menu-value">{{ bankCardCount }}张</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 隐私设置 -->
    <view class="menu-card">
      <view class="menu-item" @click="goPage('privacy')">
        <view class="menu-left">
          <text class="menu-icon">🔒</text>
          <text class="menu-label">隐私设置</text>
        </view>
        <view class="menu-right">
          <text class="menu-arrow">›</text>
        </view>
      </view>

      <view class="menu-item" @click="goPage('devices')">
        <view class="menu-left">
          <text class="menu-icon">📲</text>
          <text class="menu-label">设备管理</text>
        </view>
        <view class="menu-right">
          <text class="menu-arrow">›</text>
        </view>
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
      securityLevel: 60,
      phoneDisplay: '',
      hasPassword: false,
      hasPayPassword: false,
      isRealname: false,
      bankCardCount: 0
    }
  },
  onLoad() {
    this.loadSecurityInfo()
  },
  methods: {
    async loadSecurityInfo() {
      try {
        const userStore = useUserStore()
        const userInfo = userStore.userInfo
        
        if (userInfo.phone) {
          this.phoneDisplay = userInfo.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
        }
        
        // 计算安全等级
        let level = 0
        if (userInfo.phone) level += 20
        if (userInfo.password) {
          level += 20
          this.hasPassword = true
        }
        if (userInfo.payPassword) {
          level += 20
          this.hasPayPassword = true
        }
        if (userInfo.isRealname) {
          level += 20
          this.isRealname = true
        }
        if (userInfo.bankCardCount > 0) {
          level += 20
          this.bankCardCount = userInfo.bankCardCount
        }
        
        this.securityLevel = level
      } catch (e) {
        console.error('加载安全信息失败', e)
      }
    },
    getLevelText(level) {
      if (level >= 80) return '安全等级：高'
      if (level >= 60) return '安全等级：中'
      return '安全等级：低'
    },
    goPage(page) {
      uni.showToast({ title: '功能开发中', icon: 'none' })
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 32rpx;
}

.security-level {
  margin: 32rpx;
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  border-radius: 24rpx;
  padding: 40rpx;
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.level-icon {
  font-size: 80rpx;
}

.level-info {
  flex: 1;
}

.level-title {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
  display: block;
  margin-bottom: 16rpx;
}

.level-bar {
  height: 12rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 6rpx;
  overflow: hidden;
  margin-bottom: 12rpx;
}

.level-progress {
  height: 100%;
  background: #fff;
  border-radius: 6rpx;
  transition: width 0.3s;
}

.level-text {
  font-size: 24rpx;
  color: #fff;
  font-weight: 600;
}

.menu-card {
  margin: 0 32rpx 32rpx;
  background: #fff;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.menu-icon {
  font-size: 40rpx;
}

.menu-label {
  font-size: 28rpx;
  color: #333;
}

.menu-right {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.menu-value {
  font-size: 26rpx;
  color: #999;
  
  &.verified {
    color: #10b981;
  }
}

.menu-arrow {
  font-size: 32rpx;
  color: #ccc;
}
</style>
