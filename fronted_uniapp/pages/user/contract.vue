<template>
  <view class="page">
    <!-- 标签页 -->
    <view class="tabs">
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'all' }"
        @click="currentTab = 'all'"
      >
        全部
      </view>
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'pending' }"
        @click="currentTab = 'pending'"
      >
        待签署
      </view>
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'signed' }"
        @click="currentTab = 'signed'"
      >
        已签署
      </view>
    </view>

    <!-- 合同列表 -->
    <scroll-view scroll-y class="contract-scroll">
      <view class="contract-list">
        <view class="contract-card" v-for="contract in filteredContracts" :key="contract.id" @click="goDetail(contract.id)">
          <view class="contract-header">
            <text class="contract-title">{{ contract.title || '项目合同' }}</text>
            <view class="contract-status" :class="'status-' + contract.status">
              {{ getStatusText(contract.status) }}
            </view>
          </view>
          
          <view class="contract-info">
            <view class="info-row">
              <text class="info-label">合同编号</text>
              <text class="info-value">{{ contract.contractNo || '-' }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">签署时间</text>
              <text class="info-value">{{ formatSignTime(contract) }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">合同金额</text>
              <text class="info-value amount">¥{{ formatAmount(contract.totalAmount) }}</text>
            </view>
          </view>

          <view class="contract-footer">
            <text class="view-btn">查看详情 ›</text>
          </view>
        </view>

        <view v-if="filteredContracts.length === 0" class="empty">
          <text class="empty-icon">📄</text>
          <text class="empty-text">暂无合同</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { request } from '@/utils/request'

export default {
  data() {
    return {
      currentTab: 'all',
      contracts: [],
      loading: false
    }
  },
  computed: {
    filteredContracts() {
      if (this.currentTab === 'all') {
        return this.contracts
      } else if (this.currentTab === 'pending') {
        // 待签署：状态为 0(草稿)、1(待甲方签署)、2(待乙方签署)
        return this.contracts.filter(c => c.status === 0 || c.status === 1 || c.status === 2)
      } else {
        // 已签署：状态为 3(已生效)
        return this.contracts.filter(c => c.status === 3)
      }
    }
  },
  onLoad() {
    this.loadContracts()
  },
  methods: {
    async loadContracts() {
      this.loading = true
      try {
        const res = await request({
          url: '/contract/list',
          method: 'GET'
        })
        console.log('合同列表响应:', res)
        if (res.data) {
          // 后端直接返回数组，不是 { records: [] } 格式
          if (Array.isArray(res.data)) {
            this.contracts = res.data
          } else if (res.data.records) {
            this.contracts = res.data.records
          } else {
            this.contracts = []
          }
          console.log('合同列表:', this.contracts)
        }
      } catch (e) {
        console.error('加载合同失败', e)
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    getStatusText(status) {
      const map = {
        0: '草稿',
        1: '待甲方签署',
        2: '待乙方签署',
        3: '已生效',
        4: '已作废'
      }
      return map[status] || '未知'
    },
    formatSignTime(contract) {
      // 优先显示双方都签署的时间，否则显示任一方的签署时间
      if (contract.partyASignTime && contract.partyBSignTime) {
        // 取较晚的时间（最后签署的时间）
        const timeA = new Date(contract.partyASignTime).getTime()
        const timeB = new Date(contract.partyBSignTime).getTime()
        const laterTime = timeA > timeB ? contract.partyASignTime : contract.partyBSignTime
        return this.formatDate(laterTime)
      } else if (contract.partyASignTime) {
        return this.formatDate(contract.partyASignTime)
      } else if (contract.partyBSignTime) {
        return this.formatDate(contract.partyBSignTime)
      }
      return '未签署'
    },
    formatDate(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    formatAmount(amount) {
      if (!amount) return '0'
      return parseFloat(amount).toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 })
    },
    goDetail(id) {
      uni.navigateTo({ url: `/pages/user/contract-detail?id=${id}` })
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f7fa;
}

.tabs {
  display: flex;
  background: #fff;
  padding: 0 32rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 32rpx 0;
  font-size: 28rpx;
  color: #666;
  position: relative;
  
  &.active {
    color: #00AFE1;
    font-weight: 600;
    
    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 60rpx;
      height: 6rpx;
      background: #00AFE1;
      border-radius: 3rpx;
    }
  }
}

.contract-scroll {
  height: calc(100vh - 88rpx);
  padding: 32rpx;
}

.contract-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.contract-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.contract-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.contract-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  flex: 1;
}

.contract-status {
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  
  &.status-0 {
    background: rgba(156, 163, 175, 0.1);
    color: #9ca3af;
  }
  
  &.status-1 {
    background: rgba(245, 158, 11, 0.1);
    color: #f59e0b;
  }
  
  &.status-2 {
    background: rgba(249, 115, 22, 0.1);
    color: #f97316;
  }
  
  &.status-3 {
    background: rgba(16, 185, 129, 0.1);
    color: #10b981;
  }
  
  &.status-4 {
    background: rgba(239, 68, 68, 0.1);
    color: #ef4444;
  }
}

.contract-info {
  margin-bottom: 24rpx;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 12rpx 0;
  font-size: 26rpx;
}

.info-label {
  color: #999;
}

.info-value {
  color: #333;
  
  &.amount {
    color: #00AFE1;
    font-weight: 600;
  }
}

.contract-footer {
  border-top: 1rpx solid #f5f5f5;
  padding-top: 24rpx;
  text-align: right;
}

.view-btn {
  font-size: 26rpx;
  color: #00AFE1;
  font-weight: 600;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
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
