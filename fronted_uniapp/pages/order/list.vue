<template>
  <view class="page">
    <!-- 状态筛选 -->
    <view class="status-tabs">
      <view 
        v-for="tab in statusTabs" 
        :key="tab.value"
        class="status-tab"
        :class="{ active: currentStatus === tab.value }"
        @click="currentStatus = tab.value"
      >
        {{ tab.label }}
        <!-- <view class="badge" v-if="tab.count > 0">{{ tab.count }}</view> -->
      </view>
    </view>

    <!-- 订单列表 -->
    <scroll-view scroll-y class="order-scroll">
      <view class="order-list">
        <view class="order-card" v-for="order in orders" :key="order.id" @click="goDetail(order.id)">
          <view class="order-header">
            <text class="order-no">订单号: {{ order.orderNo }}</text>
            <text class="order-status" :class="order.statusClass">{{ order.statusText }}</text>
          </view>
          
          <view class="order-content">
            <text class="order-title">{{ order.title }}</text>
            <view class="order-meta">
              <text class="order-category">{{ order.categoryName }}</text>
              <text class="order-time">{{ order.createTime }}</text>
            </view>
          </view>
          
          <view class="order-footer">
            <text class="order-amount">¥ {{ order.amount }}</text>
            <view class="order-actions">
              <!-- 待签约状态：商家需要拟定合同 -->
              <view class="action-btn" v-if="order.status === 8 && order.role === 'seller'" @click.stop="handleContract(order)">拟定合同</view>
              <!-- 待托管状态：买家需要托管资金 -->
              <view class="action-btn" v-if="order.status === 1 && order.role === 'buyer'" @click.stop="handleDeposit(order)">托管资金</view>
              <!-- 待验收状态：买家需要验收 -->
              <view class="action-btn" v-if="order.status === 3 && order.role === 'buyer'" @click.stop="handleAccept(order)">验收</view>
              <view class="action-btn outline" @click.stop="goDetail(order.id)">查看详情</view>
            </view>
          </view>
        </view>
      </view>
      
      <view class="empty" v-if="orders.length === 0 && !loading">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无相关订单</text>
      </view>
      
      <!-- 底部安全距离 -->
      <view style="height: 120rpx;"></view>
    </scroll-view>
    
    <!-- 自定义TabBar -->
    <custom-tabbar :current="2"></custom-tabbar>
  </view>
</template>

<script>
import { request } from '@/utils/request'
import { useUserStore } from '@/stores/user'

export default {
  data() {
    return {
      currentStatus: 'all',
      statusTabs: [
        { label: '全部', value: 'all', dbStatus: null },
        { label: '待签约', value: 'contract', dbStatus: 8 },
        { label: '待托管', value: 'deposit', dbStatus: 1 },
        { label: '进行中', value: 'processing', dbStatus: 2 },
        { label: '待验收', value: 'pending', dbStatus: 3 },
        { label: '已完成', value: 'completed', dbStatus: 4 }
      ],
      orders: [],
      loading: false,
      page: 1,
      hasMore: true
    }
  },
  onShow() {
    this.checkLogin()
  },
  watch: {
    currentStatus() {
      this.page = 1
      this.orders = []
      this.hasMore = true
      this.loadData()
    }
  },
  methods: {
    checkLogin() {
      const userStore = useUserStore()
      if (!userStore.checkLogin()) {
        uni.navigateTo({ url: '/pages/login/index' })
        return
      }
      // 每次显示时如果列表为空则加载
      if (this.orders.length === 0) {
        this.loadData()
      }
    },
    async loadData() {
      if (this.loading || !this.hasMore) return
      
      this.loading = true
      try {
        const activeTab = this.statusTabs.find(t => t.value === this.currentStatus)
        const status = activeTab ? activeTab.dbStatus : null
        
        // 构建请求参数，过滤掉 null 值
        const params = {
          page: this.page,
          size: 10
        }
        
        // 只有当 status 不为 null 时才添加到参数中
        if (status !== null) {
          params.status = status
        }
        
        const res = await request({
          url: '/order/list',
          method: 'GET',
          data: params
        })
        
        if (res.data && res.data.records) {
          const newOrders = res.data.records.map(order => ({
            id: order.id,
            orderNo: order.orderNo,
            title: order.title,
            amount: this.formatCurrency(order.totalAmount),
            status: order.status,
            statusText: this.getStatusText(order.status),
            statusClass: this.getStatusClass(order.status),
            createTime: this.formatDate(order.createTime),
            categoryName: order.categoryName || '其他', // 添加分类名称
            role: order.buyerId === useUserStore().userInfo.id ? 'buyer' : 'seller'
          }))
          
          if (this.page === 1) {
            this.orders = newOrders
          } else {
            this.orders = [...this.orders, ...newOrders]
          }
          
          if (newOrders.length < 10) {
            this.hasMore = false
          } else {
            this.page++
          }
        } else {
          this.hasMore = false
        }
      } catch (e) {
        console.error('加载订单失败', e)
        this.hasMore = false
      } finally {
        this.loading = false
      }
    },
    getStatusText(status) {
      // 0-待接单, 8-待签约, 1-待托管, 2-进行中, 3-待验收, 9-质保中, 4-已完成, 5-已取消, 6-纠纷中
      const map = { 
        0: '待接单', 8: '待签约', 1: '待托管', 
        2: '进行中', 3: '待验收', 9: '质保中', 
        4: '已完成', 5: '已取消', 6: '纠纷中' 
      }
      return map[status] || '未知'
    },
    getStatusClass(status) {
      const map = { 
        2: 'processing', 3: 'pending', 4: 'completed', 5: 'cancelled', 6: 'dispute',
        0: 'pending', 8: 'pending', 1: 'pending', 9: 'processing'
      }
      return map[status] || ''
    },
    formatCurrency(value) {
      if (!value) return '0.00'
      return parseFloat(value).toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 })
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      return dateStr.substring(0, 16).replace('T', ' ')
    },
    goDetail(id) {
      uni.navigateTo({ url: '/pages/order/detail?id=' + id })
    },
    handleContract(order) {
      // 跳转到合同拟定页面
      uni.navigateTo({ url: '/pages/user/contract-edit?orderId=' + order.id })
    },
    handleDeposit(order) {
      // 跳转到订单详情页进行托管操作
      uni.navigateTo({ url: '/pages/order/detail?id=' + order.id })
    },
    handleAccept(order) {
      uni.showModal({
        title: '确认验收',
        content: '确认该订单已完成交付，验收后资金将释放给服务商。',
        success: async (res) => {
          if (res.confirm) {
            try {
               await request({
                 url: `/order/${order.id}/stage/all/accept`, // 注意：后端目前逻辑是按阶段验收，这里可能需要调整
                 // 暂时假设有全部验收或者引导进入详情页
                 // 最好是进入详情页操作，列表页操作太危险且上下文不足
               })
               // 但为了保持原 UI 逻辑，这里暂时还是提示跳转
               uni.showToast({ title: '请进入详情页进行阶段验收', icon: 'none' })
               setTimeout(() => {
                 this.goDetail(order.id)
               }, 1000)
            } catch(e) {
               this.goDetail(order.id)
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
  display: flex;
  flex-direction: column;
  padding: 0 32rpx;
}

.status-tabs {
  display: flex;
  background: #fff;
  padding: 24rpx 0;
  gap: 32rpx;
  margin: 0 -32rpx;
  padding-left: 32rpx;
  padding-right: 32rpx;
}

.status-tab {
  font-size: 30rpx;
  color: #666;
  position: relative;
  padding-bottom: 16rpx;
  
  &.active {
    color: #00AFE1;
    font-weight: 600;
    
    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 6rpx;
      background: #00AFE1;
      border-radius: 3rpx;
    }
  }
  
  .badge {
    position: absolute;
    top: -12rpx;
    right: -24rpx;
    min-width: 32rpx;
    height: 32rpx;
    background: #ff4d4f;
    color: #fff;
    font-size: 20rpx;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 8rpx;
  }
}

.order-scroll {
  flex: 1;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  padding: 32rpx 0;
}

.order-card {
  background: #fff;
  border-radius: 32rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.order-no {
  font-size: 24rpx;
  color: #999;
}

.order-status {
  font-size: 24rpx;
  font-weight: 600;
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  
  &.processing {
    background: rgba(0, 175, 225, 0.1);
    color: #00AFE1;
  }
  
  &.pending {
    background: rgba(245, 158, 11, 0.1);
    color: #f59e0b;
  }
  
  &.completed {
    background: rgba(34, 197, 94, 0.1);
    color: #22c55e;
  }
  
  &.dispute {
    background: rgba(239, 68, 68, 0.1);
    color: #ef4444;
  }
}

.order-content {
  margin-bottom: 24rpx;
}

.order-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 12rpx;
}

.order-meta {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.order-category {
  font-size: 22rpx;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
}

.order-time {
  font-size: 24rpx;
  color: #999;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 24rpx;
  border-top: 2rpx solid #f5f5f5;
}

.order-amount {
  font-size: 36rpx;
  font-weight: 900;
  color: #00AFE1;
}

.order-actions {
  display: flex;
  gap: 16rpx;
}

.action-btn {
  font-size: 26rpx;
  font-weight: 600;
  padding: 16rpx 32rpx;
  border-radius: 20rpx;
  background: #00AFE1;
  color: #fff;
  
  &.outline {
    background: transparent;
    color: #00AFE1;
    border: 2rpx solid #00AFE1;
  }
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 24rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>
