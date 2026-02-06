<template>
  <view class="page">
    <view class="task-header">
      <text class="task-title">{{ task.title }}</text>
      <view class="task-meta">
        <text class="meta-item">发布于 {{ task.publishTime }}</text>
        <text class="tag tag-primary" v-if="task.trustDeposit">信托保障</text>
      </view>
    </view>

    <view class="price-card">
      <view class="price-info">
        <text class="price-label">托管金额</text>
        <text class="price-value">¥ {{ task.price }}</text>
      </view>
      <view class="price-info">
        <text class="price-label">交付周期</text>
        <text class="price-value">{{ task.deliveryDays }}天</text>
      </view>
    </view>

    <view class="section">
      <text class="section-title">任务描述</text>
      <text class="task-desc">{{ task.description }}</text>
    </view>

    <view class="section">
      <text class="section-title">任务要求</text>
      <view class="requirements">
        <view class="req-item" v-for="(req, index) in task.requirements" :key="index">
          <text class="req-icon">✓</text>
          <text class="req-text">{{ req }}</text>
        </view>
      </view>
    </view>

    <view class="section">
      <text class="section-title">发布者信息</text>
      <view class="publisher-card">
        <view class="publisher-avatar"></view>
        <view class="publisher-info">
          <text class="publisher-name">{{ task.publisherName }}</text>
          <text class="publisher-meta">已发布{{ task.publisherOrderCount }}个任务 | 好评率{{ task.publisherRate }}%</text>
        </view>
      </view>
    </view>

    <view class="safe-tips">
      <text class="tips-icon">🛡</text>
      <text class="tips-text">平台将对该笔资金进行全程监管，交付验收后才会释放资金给服务商。</text>
    </view>

    <view class="bottom-bar">
      <view class="action-btn chat" @click="handleChat">
        <text class="btn-icon">💬</text>
        <text>咨询</text>
      </view>
      <view class="action-btn primary" @click="handleBid">
        <text>立即抢单</text>
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
      task: {
        id: null,
        title: '',
        price: '0',
        publishTime: '',
        deliveryDays: 0,
        trustDeposit: false,
        description: '',
        requirements: [],
        publisherName: '',
        publisherOrderCount: 0,
        publisherRate: 0
      },
      loading: false
    }
  },
  mounted() {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    const id = currentPage.options.id
    if (id) {
      this.loadTaskDetail(id)
    }
  },
  methods: {
    async loadTaskDetail(id) {
      this.loading = true
      try {
        const res = await request({
          url: `/order/${id}`,
          method: 'GET'
        })
        if (res.data && res.data.order) {
          const order = res.data.order
          this.task = {
            id: order.id,
            title: order.title,
            price: this.formatCurrency(order.totalAmount),
            publishTime: this.formatDate(order.createTime),
            deliveryDays: this.calculateDays(order.deliveryTime),
            trustDeposit: order.status >= 1,
            description: order.description || '',
            requirements: order.features ? order.features.split('\n').filter(r => r.trim()) : [],
            publisherName: res.data.buyer ? res.data.buyer.nickname : '用户',
            publisherOrderCount: 0,
            publisherRate: 100
          }
        }
      } catch (e) {
        console.error('加载任务详情失败', e)
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    calculateDays(deliveryTime) {
      if (!deliveryTime) return 0
      const now = new Date()
      const end = new Date(deliveryTime)
      const diff = end - now
      return Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)))
    },
    formatCurrency(value) {
      if (!value) return '0.00'
      return parseFloat(value).toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 })
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      return dateStr.substring(0, 16).replace('T', ' ')
    },
    handleChat() {
      uni.showToast({ title: '即将开放', icon: 'none' })
    },
    async handleBid() {
      const userStore = useUserStore()
      if (!userStore.checkLogin()) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        setTimeout(() => {
          uni.navigateTo({ url: '/pages/login/index' })
        }, 1500)
        return
      }
      
      uni.showModal({
        title: '确认抢单',
        content: '确定要抢单该任务吗？抢单后需要签署合同并在规定时间内完成交付。',
        success: async (res) => {
          if (res.confirm) {
            uni.showLoading({ title: '抢单中...' })
            try {
              await request({
                url: `/order/${this.task.id}/accept`,
                method: 'POST'
              })
              uni.hideLoading()
              
              // 显示成功提示
              uni.showModal({
                title: '抢单成功',
                content: '您已成功接单，请尽快签署合同以确认项目详情。',
                showCancel: false,
                confirmText: '去签署合同',
                success: (modalRes) => {
                  if (modalRes.confirm) {
                    // 跳转到订单详情页
                    uni.redirectTo({ url: '/pages/order/detail?id=' + this.task.id })
                  } else {
                    // 返回上一页并刷新
                    uni.navigateBack()
                  }
                }
              })
            } catch (e) {
              uni.hideLoading()
              uni.showModal({
                title: '抢单失败',
                content: e.message || '抢单失败，请稍后重试',
                showCancel: false
              })
            }
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

.task-header {
  background: #fff;
  padding: 40rpx 32rpx;
}

.task-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #333;
  display: block;
  margin-bottom: 20rpx;
}

.task-meta {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.meta-item {
  font-size: 26rpx;
  color: #999;
}

.tag {
  font-size: 22rpx;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  
  &.tag-primary {
    background: rgba(0, 175, 225, 0.1);
    color: #00AFE1;
  }
}

.price-card {
  background: #fff;
  margin: 24rpx 32rpx;
  border-radius: 32rpx;
  padding: 40rpx;
  display: flex;
  justify-content: space-around;
}

.price-info {
  text-align: center;
}

.price-label {
  font-size: 26rpx;
  color: #999;
  display: block;
}

.price-value {
  font-size: 40rpx;
  font-weight: 900;
  color: #00AFE1;
  margin-top: 12rpx;
}

.section {
  background: #fff;
  margin: 24rpx 32rpx;
  border-radius: 32rpx;
  padding: 40rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 24rpx;
}

.task-desc {
  font-size: 28rpx;
  color: #666;
  line-height: 1.8;
}

.requirements {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.req-item {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
}

.req-icon {
  color: #00AFE1;
  font-size: 28rpx;
}

.req-text {
  font-size: 28rpx;
  color: #666;
  flex: 1;
}

.publisher-card {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.publisher-avatar {
  width: 96rpx;
  height: 96rpx;
  border-radius: 24rpx;
  background: #e0e0e0;
}

.publisher-name {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  display: block;
}

.publisher-meta {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}

.safe-tips {
  margin: 24rpx 32rpx;
  padding: 32rpx;
  background: rgba(0, 175, 225, 0.1);
  border-radius: 24rpx;
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
}

.tips-icon {
  font-size: 36rpx;
}

.tips-text {
  font-size: 26rpx;
  color: #00AFE1;
  line-height: 1.6;
  flex: 1;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  display: flex;
  gap: 24rpx;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding: 28rpx 48rpx;
  border-radius: 24rpx;
  font-size: 30rpx;
  font-weight: 600;
  
  &.chat {
    background: #f5f5f5;
    color: #333;
  }
  
  &.primary {
    flex: 1;
    background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
    color: #fff;
  }
}

.btn-icon {
  font-size: 36rpx;
}
</style>
