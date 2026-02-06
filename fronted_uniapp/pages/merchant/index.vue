<template>
  <view class="page">
    <!-- 状态栏占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <!-- 头部信息 -->
    <view class="header">
      <view class="profile">
        <view class="avatar"></view>
        <view class="info">
          <text class="name">{{ userInfo.nickname || '商家' }}</text>
          <text class="badge">认证商家</text>
        </view>
      </view>
      <text class="setting-icon">⚙️</text>
    </view>

    <!-- 数据统计 -->
    <view class="stats-row">
      <view class="stat-card">
        <text class="stat-label">待结算金额</text>
        <text class="stat-value">¥ {{ stats.pendingAmount }}</text>
      </view>
      <view class="stat-card">
        <text class="stat-label">本月接单</text>
        <text class="stat-value">{{ stats.monthOrders }} 笔</text>
      </view>
    </view>

    <!-- 进行中的任务 -->
    <view class="section">
      <text class="section-title">进行中的任务</text>
      
      <view class="task-list">
        <view class="task-card" v-for="task in inProgressTasks" :key="task.id">
          <view class="task-header">
            <text class="task-title">{{ task.title }}</text>
            <text class="task-deadline">剩余{{ task.remainDays }}天</text>
          </view>
          
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: task.progress + '%' }"></view>
          </view>
          
          <view class="task-footer">
            <text class="task-files">已上传 {{ task.uploadedFiles }}/{{ task.totalFiles }} 文件</text>
            <text class="task-action" @click="goDeliver(task.id)">立即交付成果</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 快捷入口 -->
    <view class="section">
      <text class="section-title">快捷入口</text>
      <view class="quick-actions">
        <view class="quick-item" @click="goWallet">
          <text class="quick-icon">💰</text>
          <text class="quick-label">我的钱包</text>
        </view>
        <view class="quick-item" @click="goOrders">
          <text class="quick-icon">📋</text>
          <text class="quick-label">全部订单</text>
        </view>
        <view class="quick-item" @click="goTaskHall">
          <text class="quick-icon">🎯</text>
          <text class="quick-label">任务大厅</text>
        </view>
        <view class="quick-item" @click="goHelp">
          <text class="quick-icon">❓</text>
          <text class="quick-label">帮助中心</text>
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
      statusBarHeight: 20,
      userInfo: {},
      stats: {
        pendingAmount: '0.00',
        monthOrders: 0
      },
      inProgressTasks: [],
      loading: false
    }
  },
  onShow() {
    this.checkLogin()
  },
  mounted() {
    const sysInfo = uni.getSystemInfoSync()
    this.statusBarHeight = sysInfo.statusBarHeight
  },
  methods: {
    checkLogin() {
      const userStore = useUserStore()
      if (!userStore.checkLogin()) {
        uni.navigateTo({ url: '/pages/login/index' })
        return
      }
      this.userInfo = userStore.userInfo
      this.loadData()
    },
    async loadData() {
      this.loading = true
      try {
        // 加载商家统计数据
        // 注意：/order/stats 返回的是通用统计，可能需要后端专门增加商家统计字段
        // 这里暂时复用 /order/stats，假设它返回额外字段
        const statsRes = await request({
          url: '/order/stats',
          method: 'GET'
        })
        if (statsRes.data) {
          this.stats = {
            pendingAmount: this.formatCurrency(statsRes.data.pendingAmount || 0),
            monthOrders: statsRes.data.monthOrders || 0
          }
        }
        
        // 加载进行中的任务 (status=2: 进行中)
        const tasksRes = await request({
          url: '/order/list',
          method: 'GET',
          data: { status: 2, size: 5, role: 'merchant' } // 显式传 role 或后端自动识别
        })
        
        if (tasksRes.data && tasksRes.data.records) {
          this.inProgressTasks = tasksRes.data.records.map(task => ({
            id: task.id,
            title: task.title,
            remainDays: this.calculateRemainDays(task.deliveryTime),
            progress: this.calculateProgress(task), // 需要根据阶段计算
            uploadedFiles: 0, // 暂时无此数据，置0或隐藏
            totalFiles: 0
          }))
        }
      } catch (e) {
        console.error('加载商家数据失败', e)
      } finally {
        this.loading = false
      }
    },
    calculateRemainDays(deliveryTime) {
      if (!deliveryTime) return 0
      const now = new Date()
      const end = new Date(deliveryTime)
      const diff = end - now
      return Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)))
    },
    calculateProgress(task) {
        // 简单模拟：根据所处阶段和总阶段数计算比例，暂无详细阶段数据只能估算
        return 50 
    },
    formatCurrency(value) {
      if (!value) return '0.00'
      return parseFloat(value).toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 })
    },
    goDeliver(id) {
      uni.navigateTo({ url: '/pages/order/detail?id=' + id })
    },
    goWallet() {
      uni.navigateTo({ url: '/pages/wallet/index' })
    },
    goOrders() {
      uni.switchTab({ url: '/pages/order/list' })
    },
    goTaskHall() {
      uni.switchTab({ url: '/pages/task/list' })
    },
    goHelp() {
      uni.showToast({ title: '即将开放', icon: 'none' })
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #0f172a;
  padding: 0 32rpx;
  color: #fff;
}

.status-bar {
  width: 100%;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 48rpx 0;
}

.profile {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.avatar {
  width: 112rpx;
  height: 112rpx;
  background: #374151;
  border-radius: 32rpx;
  border: 4rpx solid #00AFE1;
}

.name {
  font-size: 36rpx;
  font-weight: 700;
  display: block;
}

.badge {
  font-size: 22rpx;
  background: rgba(0, 175, 225, 0.2);
  color: #00AFE1;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  margin-top: 8rpx;
}

.setting-icon {
  font-size: 40rpx;
  opacity: 0.5;
}

.stats-row {
  display: flex;
  gap: 24rpx;
  margin-bottom: 48rpx;
}

.stat-card {
  flex: 1;
  background: rgba(255, 255, 255, 0.05);
  border: 2rpx solid rgba(255, 255, 255, 0.1);
  border-radius: 48rpx;
  padding: 32rpx;
}

.stat-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.6);
  display: block;
}

.stat-value {
  font-size: 40rpx;
  font-weight: 900;
  margin-top: 12rpx;
  display: block;
}

.section {
  margin-bottom: 48rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  display: block;
  margin-bottom: 24rpx;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.task-card {
  background: rgba(255, 255, 255, 0.05);
  border-left: 8rpx solid #00AFE1;
  border-radius: 0 32rpx 32rpx 0;
  padding: 32rpx;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.task-title {
  font-size: 30rpx;
  font-weight: 600;
}

.task-deadline {
  font-size: 24rpx;
  color: #f59e0b;
}

.progress-bar {
  height: 12rpx;
  background: #374151;
  border-radius: 6rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #00AFE1;
  border-radius: 6rpx;
  transition: width 0.3s;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24rpx;
}

.task-files {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.5);
}

.task-action {
  font-size: 22rpx;
  color: #00AFE1;
  font-weight: 600;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24rpx;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  padding: 32rpx 0;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 24rpx;
}

.quick-icon {
  font-size: 48rpx;
}

.quick-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
}
</style>
