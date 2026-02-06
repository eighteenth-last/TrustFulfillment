<template>
  <view class="page">
    <!-- 顶部状态栏占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <!-- 头部 -->
    <view class="header" :style="headerStyle">
      <view class="header-left">
        <image class="header-logo" src="/static/logo.png" mode="aspectFit" />
        <view class="header-text">
          <text class="title">臻托 Trust</text>
          <text class="subtitle">让每一笔交易都值得托付</text>
        </view>
      </view>
      <!-- 通知按钮 - 避开小程序胶囊按钮 -->
      <view class="notify-btn" @click="goNotify">
        <text class="iconfont icon-bell"></text>
      </view>
    </view>

    <!-- 搜索框 -->
    <view class="search-box" @click="goSearch">
      <text class="iconfont icon-search search-icon"></text>
      <text class="search-placeholder">搜索任务或服务内容...</text>
    </view>

    <!-- 核心功能入口 -->
    <view class="action-cards">
      <view class="action-card primary" @click="goPublish">
        <text class="iconfont icon-add"></text>
        <text class="action-title">发布任务</text>
        <text class="action-desc">极速响应，安全担保</text>
      </view>
      <view class="action-card" @click="goAccept">
        <text class="iconfont icon-task text-tf"></text>
        <text class="action-title">承接任务</text>
        <text class="action-desc">信用背书，按时结算</text>
      </view>
    </view>

    <!-- 统计数据 -->
    <view class="stats-card">
      <view class="stat-item">
        <text class="stat-value">{{ stats.merchants || '-' }}</text>
        <text class="stat-label">活跃商家</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-value">¥{{ formatAmount(stats.trustAmount) }}</text>
        <text class="stat-label">保障资金</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.successRate }}%</text>
        <text class="stat-label">履约率</text>
      </view>
    </view>

    <!-- 推荐任务 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">最新推荐</text>
        <text class="section-more" @click="goTaskList">查看更多</text>
      </view>

      <view class="task-list">
        <view class="task-card" v-for="task in recommendTasks" :key="task.id" @click="goTaskDetail(task.id)">
          <view class="task-header">
            <text class="task-title">{{ task.title }}</text>
            <text class="task-price">¥{{ task.price }}</text>
          </view>
          <text class="task-desc">{{ task.description }}</text>
          <view class="task-tags">
            <text class="tag tag-category">{{ task.categoryName }}</text>
            <text class="tag tag-primary">信托保障</text>
            <text class="tag">限时{{ task.days }}天</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部安全距离 -->
    <view style="height: 180rpx;"></view>
    
    <!-- 自定义TabBar -->
    <custom-tabbar :current="0"></custom-tabbar>
  </view>
</template>

<script>
import { request } from '@/utils/request'

export default {
  data() {
    return {
      statusBarHeight: 20,
      menuButtonInfo: null,
      loading: false,
      stats: {
        merchants: 0,
        trustAmount: 0,
        successRate: '0'
      },
      recommendTasks: []
    }
  },
  mounted() {
    const sysInfo = uni.getSystemInfoSync()
    this.statusBarHeight = sysInfo.statusBarHeight
    
    // 获取胶囊按钮位置信息（仅小程序环境）
    // #ifdef MP-WEIXIN
    this.menuButtonInfo = uni.getMenuButtonBoundingClientRect()
    // #endif
    
    this.loadData()
  },
  onShow() {
    // 每次页面显示时刷新数据，确保抢单后状态更新
    this.loadData()
  },
  computed: {
    headerStyle() {
      // 如果获取到了胶囊按钮信息，调整头部右侧内边距避开胶囊
      if (this.menuButtonInfo) {
        // 计算胶囊按钮右侧到屏幕右边的距离，然后加上胶囊宽度和间距
        const screenWidth = uni.upx2px(750)
        const capsuleRight = this.menuButtonInfo.right
        const paddingRight = screenWidth - capsuleRight + uni.upx2px(24)
        return {
          paddingRight: paddingRight + 'px'
        }
      }
      return {}
    }
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        // 加载推荐任务（待接单的订单）
        const res = await request({
          url: '/public/orders',
          method: 'GET',
          data: { page: 1, size: 5 }
        })
        if (res.data && res.data.records && res.data.records.length > 0) {
          this.recommendTasks = res.data.records.map(order => ({
            id: order.id,
            title: order.title,
            price: order.totalAmount ? parseInt(order.totalAmount).toLocaleString() : '0',
            description: order.description || '',
            days: this.calculateDays(order.deliveryTime),
            categoryName: order.categoryName || '其他' // 添加分类名称
          }))
        }

        // 加载统计数据
        const statsRes = await request({
          url: '/public/stats',
          method: 'GET'
        })
        if (statsRes.data) {
          this.stats = {
             merchants: statsRes.data.activeMerchants || 0,
             trustAmount: statsRes.data.trustAmount || 0,
             successRate: statsRes.data.successRate || 0
          }
        }
      } catch (e) {
        console.error('加载数据失败', e)
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    calculateDays(deliveryTime) {
      if (!deliveryTime) return 7
      const now = new Date()
      const delivery = new Date(deliveryTime)
      return Math.max(1, Math.ceil((delivery - now) / (1000 * 60 * 60 * 24)))
    },
    formatAmount(amount) {
      if (amount >= 10000) {
        return (amount / 10000).toFixed(1) + 'w'
      }
      return amount.toLocaleString()
    },
    goNotify() {
      uni.showToast({ title: '暂无新消息', icon: 'none' })
    },
    goSearch() {
      uni.navigateTo({ url: '/pages/task/list?search=1' })
    },
    goPublish() {
      uni.navigateTo({ url: '/pages/task/create' })
    },
    goAccept() {
      uni.switchTab({ url: '/pages/task/list' })
    },
    goTaskList() {
      uni.switchTab({ url: '/pages/task/list' })
    },
    goTaskDetail(id) {
      uni.navigateTo({ url: '/pages/task/detail?id=' + id })
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f0f4f8;
  padding: 0 32rpx;
}

.status-bar {
  width: 100%;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx 0;
  position: relative;
}

.header-left {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 20rpx;
  flex: 1;
}

.header-logo {
  width: 88rpx;
  height: 88rpx;
  border-radius: 24rpx;
}

.header-text {
  display: flex;
  flex-direction: column;
}

.title {
  font-size: 40rpx;
  font-weight: 900;
  color: #333;
}

.subtitle {
  font-size: 24rpx;
  color: #999;
  margin-top: 4rpx;
}

.notify-btn {
  width: 80rpx;
  height: 80rpx;
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(0, 175, 225, 0.25);
  flex-shrink: 0;
  
  .iconfont {
    font-size: 36rpx;
    color: #fff;
  }
}

.search-box {
  background: #fff;
  border-radius: 32rpx;
  padding: 28rpx 32rpx;
  display: flex;
  align-items: center;
  gap: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
  margin-bottom: 32rpx;
}

.search-icon {
  font-size: 32rpx;
  color: #999;
}

.search-placeholder {
  font-size: 28rpx;
  color: #999;
}

.action-cards {
  display: flex;
  gap: 24rpx;
  margin-bottom: 32rpx;
}

.action-card {
  flex: 1;
  background: #fff;
  border-radius: 48rpx;
  padding: 40rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
  border: 2rpx solid #f0f0f0;
  
  .iconfont {
    font-size: 48rpx;
    display: block;
    margin-bottom: 16rpx;
  }
  
  &.primary {
    background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
    border: none;
    box-shadow: 0 8rpx 32rpx rgba(0, 175, 225, 0.3);
    
    .iconfont, .action-title, .action-desc {
      color: #fff;
    }
    .action-desc {
      opacity: 0.8;
    }
  }
}

.action-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  display: block;
}

.action-desc {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}

.stats-card {
  background: #fff;
  border-radius: 48rpx;
  padding: 48rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
  margin-bottom: 48rpx;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-value {
  font-size: 40rpx;
  font-weight: 900;
  color: #00AFE1;
  display: block;
}

.stat-label {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}

.stat-divider {
  width: 2rpx;
  height: 60rpx;
  background: #f0f0f0;
}

.section {
  margin-bottom: 32rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.section-more {
  font-size: 26rpx;
  color: #00AFE1;
  font-weight: 600;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.task-card {
  background: #fff;
  border-radius: 32rpx;
  padding: 32rpx;
  border-left: 8rpx solid #00AFE1;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.task-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  flex: 1;
  margin-right: 16rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.task-price {
  font-size: 32rpx;
  font-weight: 900;
  color: #00AFE1;
  flex-shrink: 0;
}

.task-desc {
  font-size: 26rpx;
  color: #999;
  margin-bottom: 20rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.task-tags {
  display: flex;
  gap: 16rpx;
}

.tag {
  font-size: 22rpx;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  background: #f5f5f5;
  color: #999;
  
  &.tag-category {
    background: rgba(139, 92, 246, 0.1);
    color: #8b5cf6;
  }
  
  &.tag-primary {
    background: rgba(0, 175, 225, 0.1);
    color: #00AFE1;
  }
}
</style>
