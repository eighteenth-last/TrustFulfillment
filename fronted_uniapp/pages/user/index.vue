<template>
  <view class="page">
    <!-- 头部背景 -->
    <view class="header-bg">
      <view class="profile-card" @click="goPage('profile')">
        <view class="avatar-wrap">
          <image v-if="userInfo.avatar" :src="userInfo.avatar" class="avatar" mode="aspectFill" />
          <view v-else class="avatar"></view>
        </view>
        <view class="user-info">
          <text class="nickname">{{ userInfo.nickname }}</text>
          <text class="user-id">臻托号: {{ userInfo.tfId }}</text>
        </view>
        <text class="edit-icon">›</text>
      </view>
    </view>

    <!-- 信用分 -->
    <view class="credit-card">
      <view class="credit-left">
        <text class="credit-icon">🏆</text>
        <text class="credit-label">臻托信用分</text>
      </view>
      <view class="credit-right">
        <text class="credit-score">{{ userInfo.creditScore }}</text>
        <text class="credit-level">{{ getCreditLevel(userInfo.creditScore) }}</text>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-card">
      <view class="menu-item" @click="goPage('wallet')">
        <text class="menu-icon">💰</text>
        <text class="menu-label">我的钱包</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goPage('contract')">
        <text class="menu-icon">📄</text>
        <text class="menu-label">我的合同</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goPage('merchantApply')">
        <text class="menu-icon">🏪</text>
        <text class="menu-label">{{ merchantStatus.isMerchant ? '商家中心' : '申请接单' }}</text>
        <text v-if="merchantStatus.hasPendingApply" class="badge">审核中</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="menu-card">
      <view class="menu-item" @click="goPage('security')">
        <text class="menu-icon">🔐</text>
        <text class="menu-label">安全中心</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goPage('help')">
        <text class="menu-icon">❓</text>
        <text class="menu-label">帮助与反馈</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @click="goPage('about')">
        <text class="menu-icon">ℹ️</text>
        <text class="menu-label">关于臻托</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-btn" @click="handleLogout">
      退出登录
    </view>
    
    <!-- 自定义TabBar -->
    <custom-tabbar :current="3"></custom-tabbar>
  </view>
</template>

<script>
import { useUserStore } from '@/stores/user'
import { request } from '@/utils/request'

export default {
  data() {
    return {
      userInfo: {
        nickname: '',
        tfId: '',
        creditScore: 0,
        avatar: ''
      },
      merchantStatus: {
        isMerchant: false,
        hasPendingApply: false
      },
      loading: false
    }
  },
  mounted() {
    this.loadUserInfo()
    this.loadMerchantStatus()
  },
  onShow() {
    // 每次显示时刷新用户信息和商家状态
    this.loadUserInfo()
    this.loadMerchantStatus()
  },
  methods: {
    async loadUserInfo() {
      const userStore = useUserStore()
      // 先从本地获取
      if (userStore.userInfo && userStore.userInfo.nickname) {
        this.userInfo = {
          nickname: userStore.userInfo.nickname || '用户',
          tfId: 'TF_' + (userStore.userInfo.id || '000000'),
          creditScore: userStore.userInfo.creditScore || 600,
          avatar: userStore.userInfo.avatar || ''
        }
      }
      // 从后端刷新最新数据
      try {
        this.loading = true
        const res = await request({
          url: '/auth/info',
          method: 'GET'
        })
        if (res.data) {
          this.userInfo = {
            nickname: res.data.nickname || '用户',
            tfId: 'TF_' + res.data.id,
            creditScore: res.data.creditScore || 600,
            avatar: res.data.avatar || ''
          }
          // 更新本地存储
          userStore.setUserInfo(res.data)
        }
      } catch (e) {
        console.error('加载用户信息失败', e)
      } finally {
        this.loading = false
      }
    },
    async loadMerchantStatus() {
      try {
        const res = await request({
          url: '/merchant/status',
          method: 'GET'
        })
        if (res.data) {
          this.merchantStatus = {
            isMerchant: res.data.isMerchant || false,
            hasPendingApply: res.data.hasPendingApply || false
          }
        }
      } catch (e) {
        console.error('加载商家状态失败', e)
      }
    },
    getCreditLevel(score) {
      if (score >= 750) return '极好'
      if (score >= 650) return '优秀'
      if (score >= 550) return '良好'
      if (score >= 350) return '中等'
      return '较差'
    },
    goPage(page) {
      const routes = {
        profile: '/pages/user/profile',
        wallet: '/pages/wallet/index',
        merchantApply: '/pages/merchant/apply',
        contract: '/pages/user/contract',
        security: '/pages/user/security',
        help: '/pages/user/help',
        about: '/pages/user/about'
      }
      
      if (routes[page]) {
        uni.navigateTo({ url: routes[page] })
      } else {
        uni.showToast({ title: '即将开放', icon: 'none' })
      }
    },
    handleLogout() {
      const userStore = useUserStore()
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            userStore.logout()
            uni.reLaunch({ url: '/pages/login/index' })
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f0f4f8;
  padding-bottom: 180rpx;
}

.header-bg {
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  height: 380rpx;
  position: relative;
}

.profile-card {
  position: absolute;
  bottom: -80rpx;
  left: 32rpx;
  right: 32rpx;
  display: flex;
  align-items: flex-end;
  gap: 24rpx;
}

.avatar-wrap {
  width: 192rpx;
  height: 192rpx;
  background: #fff;
  border-radius: 64rpx;
  padding: 8rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
}

.avatar {
  width: 100%;
  height: 100%;
  background: #e5e5e5;
  border-radius: 56rpx;
  display: block;
}

.edit-icon {
  font-size: 48rpx;
  color: #999;
  padding-bottom: 16rpx;
}

.user-info {
  padding-bottom: 16rpx;
}

.nickname {
  font-size: 40rpx;
  font-weight: 900;
  color: #333;
  display: block;
}

.user-id {
  font-size: 26rpx;
  color: #999;
  margin-top: 8rpx;
}

.credit-card {
  margin: 120rpx 32rpx 32rpx;
  background: #fff;
  border-radius: 48rpx;
  padding: 40rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.credit-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.credit-icon {
  font-size: 48rpx;
}

.credit-label {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
}

.credit-right {
  display: flex;
  align-items: baseline;
  gap: 12rpx;
}

.credit-score {
  font-size: 48rpx;
  font-weight: 900;
  color: #00AFE1;
}

.credit-level {
  font-size: 24rpx;
  color: #22c55e;
  font-weight: 600;
}

.menu-card {
  margin: 0 32rpx 32rpx;
  background: #fff;
  border-radius: 48rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 40rpx;
  border-bottom: 2rpx solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.menu-icon {
  font-size: 40rpx;
  margin-right: 24rpx;
}

.menu-label {
  flex: 1;
  font-size: 30rpx;
  color: #333;
}

.badge {
  font-size: 22rpx;
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
  margin-right: 16rpx;
}

.menu-arrow {
  font-size: 36rpx;
  color: #ccc;
}

.logout-btn {
  margin: 48rpx 32rpx;
  background: #fff;
  border-radius: 48rpx;
  padding: 36rpx;
  text-align: center;
  font-size: 30rpx;
  font-weight: 600;
  color: #ef4444;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}
</style>
