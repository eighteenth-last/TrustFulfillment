<template>
  <view class="page">
    <!-- 余额卡片 -->
    <view class="balance-card">
      <text class="balance-label">账户余额 (元)</text>
      <text class="balance-value">¥ {{ balance }}</text>
      <view class="balance-actions">
        <view class="balance-btn" @click="handleRecharge">充值</view>
        <view class="balance-btn outline" @click="handleWithdraw">提现</view>
      </view>
    </view>

    <!-- 交易记录 -->
    <view class="section">
      <text class="section-title">信托保障记录</text>
      
      <view class="record-list">
        <view class="record-item" v-for="record in records" :key="record.id">
          <view class="record-icon" :class="record.type">
            <text>{{ record.type === 'freeze' ? '🔒' : '🔓' }}</text>
          </view>
          <view class="record-info">
            <text class="record-title">{{ record.title }}</text>
            <text class="record-time">{{ record.time }}</text>
          </view>
          <text class="record-amount" :class="record.type">
            {{ record.type === 'freeze' ? '-' : '+' }}{{ record.amount }}
          </text>
        </view>
      </view>
    </view>

    <!-- 安全说明 -->
    <view class="safe-tips">
      <text class="tips-title">资金安全说明</text>
      <view class="tips-list">
        <text class="tips-item">• 所有资金托管于持牌金融机构</text>
        <text class="tips-item">• 资金流向全程可追溯</text>
        <text class="tips-item">• 支持随时提现，T+1到账</text>
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
      balance: '0.00',
      records: [],
      loading: false
    }
  },
  onShow() {
    this.checkLogin()
  },
  methods: {
    checkLogin() {
      const userStore = useUserStore()
      if (!userStore.checkLogin()) {
        uni.navigateTo({ url: '/pages/login/index' })
        return
      }
      this.loadData()
    },
    async loadData() {
      this.loading = true
      try {
        // 获取钱包信息
        const infoRes = await request({
          url: '/wallet/info',
          method: 'GET'
        })
        if (infoRes.data) {
          this.balance = this.formatCurrency(infoRes.data.balance)
        }

        // 获取交易记录
        const transRes = await request({
          url: '/wallet/transactions',
          method: 'GET',
          data: { page: 1, size: 10 }
        })
        if (transRes.data && transRes.data.records) {
          this.records = transRes.data.records.map(record => ({
            id: record.id,
            type: this.getRecordType(record.type),
            title: record.title || this.getTypeTitle(record.type),
            amount: this.formatCurrency(record.amount),
            time: this.formatDate(record.createTime)
          }))
        }
      } catch (e) {
        console.error('加载钱包数据失败', e)
        uni.showToast({ title: '加载失败', icon: 'none' })
      } finally {
        this.loading = false
      }
    },
    getRecordType(type) {
      if (['withdraw', 'deposit', 'freeze'].includes(type)) return 'freeze'
      return 'release'
    },
    getTypeTitle(type) {
      const map = {
        recharge: '账户充值',
        withdraw: '提现申请',
        deposit: '资金托管',
        release: '资金释放',
        freeze: '资金冻结',
        income: '项目收入'
      }
      return map[type] || '交易记录'
    },
    formatCurrency(value) {
      if (!value) return '0.00'
      return parseFloat(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getMonth() + 1}-${date.getDate()} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },
    handleRecharge() {
      uni.navigateTo({ url: '/pages/wallet/recharge' })
    },
    handleWithdraw() {
      uni.showModal({
        title: '提现',
        content: '请输入提现金额',
        editable: true,
        placeholderText: '1000',
        success: async (res) => {
          if (res.confirm) {
             const amount = parseFloat(res.content)
             if (!amount || amount <= 0) {
                 uni.showToast({ title: '金额无效', icon: 'none' })
                 return
             }
             try {
                await request({
                    url: '/wallet/withdraw',
                    method: 'POST',
                    data: { amount: amount }
                })
                uni.showToast({ title: '申请成功' })
                this.loadData()
             } catch(e) {
                uni.showToast({ title: e.message, icon: 'none' })
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
  padding: 32rpx;
}

.balance-card {
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  border-radius: 64rpx;
  padding: 64rpx;
  color: #fff;
  box-shadow: 0 16rpx 48rpx rgba(0, 175, 225, 0.3);
  margin-bottom: 48rpx;
}

.balance-label {
  font-size: 28rpx;
  opacity: 0.8;
  display: block;
  margin-bottom: 16rpx;
}

.balance-value {
  font-size: 72rpx;
  font-weight: 900;
  display: block;
  margin-bottom: 48rpx;
}

.balance-actions {
  display: flex;
  gap: 24rpx;
}

.balance-btn {
  flex: 1;
  text-align: center;
  padding: 28rpx;
  background: #fff;
  color: #00AFE1;
  font-size: 30rpx;
  font-weight: 600;
  border-radius: 32rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
  
  &.outline {
    background: rgba(255, 255, 255, 0.2);
    color: #fff;
    border: 2rpx solid rgba(255, 255, 255, 0.5);
    box-shadow: none;
  }
}

.section {
  background: #fff;
  border-radius: 48rpx;
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

.record-list {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.record-item {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.record-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  
  &.freeze {
    background: rgba(0, 175, 225, 0.1);
  }
  
  &.release {
    background: rgba(34, 197, 94, 0.1);
  }
}

.record-info {
  flex: 1;
}

.record-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  display: block;
}

.record-time {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}

.record-amount {
  font-size: 30rpx;
  font-weight: 700;
  
  &.freeze {
    color: #f59e0b;
  }
  
  &.release {
    color: #22c55e;
  }
}

.safe-tips {
  background: #fff;
  border-radius: 48rpx;
  padding: 40rpx;
}

.tips-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 24rpx;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.tips-item {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
}
</style>
