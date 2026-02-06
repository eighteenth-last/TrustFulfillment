<template>
  <view class="page">

    <!-- 订单状态卡片 -->
    <view class="status-card">
      <view class="status-header">
        <text class="status-badge">{{ order.statusText }}</text>
        <text class="order-no">ID: {{ order.orderNo }}</text>
      </view>
      
      <!-- 时间线 -->
      <view class="timeline">
        <view class="timeline-line"></view>
        
        <view class="timeline-item" v-for="(step, index) in timeline" :key="index">
          <view class="timeline-dot" :class="{ active: step.active, done: step.done }"></view>
          <view class="timeline-content">
            <text class="timeline-title" :class="{ active: step.active }">{{ step.title }}</text>
            <text class="timeline-desc" v-if="step.desc">{{ step.desc }}</text>
            <view class="timeline-actions" v-if="step.actions">
              <view class="action-btn primary" v-for="action in step.actions" :key="action" @click="handleAction(action, step)">
                {{ action }}
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 安全提醒 -->
    <view class="safe-card" v-if="order.status === 8 || order.status === 2">
      <text class="safe-title">安全提醒</text>
      <text class="safe-text">请勿通过微信/QQ等站外平台私下交易。站外交易无法享受"臻托"资金保障。</text>
    </view>
  </view>
</template>

<script>
import { request } from '@/utils/request'
import { useUserStore } from '@/stores/user'

export default {
  data() {
    return {
      order: {
        id: 1,
        orderNo: 'TF8829310',
        status: 1,
        statusText: '进行中',
        title: '臻托平台UI原型设计',
        amount: '3,500'
      },
      timeline: []
    }
  },
  mounted() {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    const id = currentPage.options.id
    if (id) {
        this.order.id = id
        this.loadOrderDetail(id)
    }
  },
  onShow() {
    // 每次页面显示时重新加载订单数据（从签署页面返回时会触发）
    if (this.order.id) {
      this.loadOrderDetail(this.order.id)
    }
  },
  methods: {
    async loadOrderDetail(id) {
        try {
            const res = await request({
                url: `/order/${id}`,
                method: 'GET'
            })
            if (res.data) {
                const data = res.data
                this.order = {
                    id: data.order.id,
                    orderNo: data.order.orderNo,
                    status: data.order.status,
                    statusText: this.getStatusText(data.order.status),
                    title: data.order.title,
                    amount: this.formatCurrency(data.order.totalAmount),
                    role: this.getUserRole(data.order)
                }
                
                this.buildTimeline(data)
            }
        } catch (e) {
            console.error(e)
            uni.showToast({ title: '加载失败', icon: 'none' })
        }
    },
    getUserRole(order) {
        const uid = useUserStore().userInfo.id
        if (order.buyerId === uid) return 'buyer'
        if (order.sellerId === uid) return 'seller'
        return 'visitor'
    },
    buildTimeline(data) {
        const list = []
        const order = data.order
        
        // 1. 资金托管/发布
        list.push({
            title: '资金已托管',
            desc: this.formatDate(order.createTime),
            done: true,
            active: false
        })
        
        // 2. 商家接单
        if (order.status >= 8 && order.sellerId) { // 8-待签约及后续
             list.push({
                title: '商家已接单',
                desc: '服务商已确认接单',
                done: true,
                active: false
            })
        }
        
        // 3. 待签署合同 (status = 8)
        if (order.status === 8) {
            // 检查是否已有合同
            const hasContract = data.contract && data.contract.id
            
            if (!hasContract && this.order.role === 'seller') {
                // 商家需要先创建合同
                list.push({
                    title: '创建项目合同',
                    desc: '请创建并提交项目合同',
                    done: false,
                    active: true,
                    actions: ['创建合同'],
                    orderId: order.id
                })
            } else if (hasContract) {
                // 合同已创建，等待签署
                const contractStatus = data.contract.status
                // 0-草稿 1-待甲方签署 2-待乙方签署 3-已生效
                
                if (contractStatus === 1 && this.order.role === 'buyer') {
                    list.push({
                        title: '待您签署合同',
                        desc: '商家已创建合同，请查看并签署',
                        done: false,
                        active: true,
                        actions: ['查看并签署'],
                        orderId: order.id,
                        contractId: data.contract.id
                    })
                } else if (contractStatus === 2 && this.order.role === 'seller') {
                    list.push({
                        title: '待您签署合同',
                        desc: '买家已签署，请您签署确认',
                        done: false,
                        active: true,
                        actions: ['查看并签署'],
                        orderId: order.id,
                        contractId: data.contract.id
                    })
                } else if (contractStatus === 1 && this.order.role === 'seller') {
                    list.push({
                        title: '等待买家签署',
                        desc: '合同已提交，等待买家签署',
                        done: false,
                        active: true
                    })
                } else if (contractStatus === 2 && this.order.role === 'buyer') {
                    list.push({
                        title: '等待商家签署',
                        desc: '您已签署，等待商家签署',
                        done: false,
                        active: true
                    })
                }
            } else if (this.order.role === 'buyer') {
                // 买家等待商家创建合同
                list.push({
                    title: '等待商家创建合同',
                    desc: '商家正在准备项目合同',
                    done: false,
                    active: true
                })
            }
        }
        
        // 4. 阶段信息 (status >= 2)
        if (order.status >= 2 && data.stages && data.stages.length > 0) {
            data.stages.forEach(stage => {
                list.push({
                    title: stage.stageName,
                    desc: stage.status === 1 ? '进行中' : (stage.status >= 2 ? '已完成' : '待开始'),
                    done: stage.status >= 2, // 2-Delivered, 3-Completed
                    active: stage.status === 1, // 1-Processing
                    actions: this.getStageActions(stage, this.order.role),
                    stageId: stage.id,
                    orderId: order.id
                })
            })
        }
        
        // 5. 验收完成
        if (order.status === 4) { // 4-Completed
             list.push({
                title: '订单完成',
                desc: this.formatDate(order.updateTime),
                done: true,
                active: true
            })
        }
        
        this.timeline = list
    },
    getStageActions(stage, role) {
        // 根据角色和阶段状态返回操作
        // stage status: 0-Pending, 1-Processing, 2-Delivery(Wait Accept), 3-Completed, 4-Escrowed(Wait Start)
        if (role === 'buyer' && stage.status === 2) { // 待验收状态
            return ['确认验收', '拒绝验收']
        }
        if (role === 'seller' && (stage.status === 1 || stage.status === 0 || stage.status === 4)) {
            // 这里简化逻辑，实际需要看具体业务允许何时提交
            if (stage.status === 1 || stage.status === 4) return ['提交交付']
        }
        return []
    },
    getStatusText(status) {
      const map = { 
        0: '待接单', 8: '待签约', 1: '待托管', 
        2: '进行中', 3: '待验收', 9: '质保中', 
        4: '已完成', 5: '已取消', 6: '纠纷中' 
      }
      return map[status] || '未知'
    },
    formatCurrency(value) {
      if (!value) return '0.00'
      return parseFloat(value).toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 })
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      return dateStr.substring(0, 16).replace('T', ' ')
    },
    goBack() {
      uni.navigateBack()
    },
    async handleAction(action, step) {
      const orderId = this.order.id
      
      if (action === '创建合同') {
          // 跳转到合同创建页面
          uni.navigateTo({ 
              url: `/pages/user/contract-edit?orderId=${orderId}` 
          })
          return
      }
      
      if (action === '查看并签署') {
          // 跳转到合同签署页面
          const contractId = step.contractId
          if (!contractId) {
              uni.showToast({ title: '合同ID缺失', icon: 'none' })
              return
          }
          uni.navigateTo({ 
              url: `/pages/user/contract-sign?orderId=${orderId}&contractId=${contractId}` 
          })
          return
      }
      
      if (action === '签署合同') {
          uni.showModal({
              title: '签署合同',
              content: '请确认您已阅读并同意合同条款。签署后将进入项目执行阶段。',
              confirmText: '确认签署',
              success: async (res) => {
                  if (res.confirm) {
                      uni.showLoading({ title: '签署中...' })
                      try {
                          // 1. 先获取订单对应的合同ID
                          const contractRes = await request({
                              url: `/contract/order/${orderId}`,
                              method: 'GET'
                          })
                          
                          if (!contractRes.data || !contractRes.data.id) {
                              throw new Error('合同不存在，请联系客服')
                          }
                          
                          const contractId = contractRes.data.id
                          
                          // 2. 签署合同
                          await request({
                              url: `/contract/${contractId}/sign`,
                              method: 'POST',
                              data: {
                                  signature: 'merchant_signature' // 商家签名
                              }
                          })
                          
                          uni.hideLoading()
                          uni.showToast({ title: '签署成功', icon: 'success' })
                          
                          // 3. 重新加载订单详情
                          setTimeout(() => {
                              this.loadOrderDetail(orderId)
                          }, 1500)
                      } catch (e) {
                          uni.hideLoading()
                          uni.showModal({
                              title: '签署失败',
                              content: e.message || '签署失败，请稍后重试',
                              showCancel: false
                          })
                      }
                  }
              }
          })
          return
      }
      
      if (!step || !step.stageId) return
      
      const stageId = step.stageId
      
      if (action === '提交交付') {
          uni.showModal({
              title: '提交交付',
              content: '确认提交当前阶段成果？',
              editable: true,
              placeholderText: '请输入成果描述或链接',
              success: async (res) => {
                  if (res.confirm) {
                      try {
                          await request({
                              url: `/order/${orderId}/stage/${stageId}/deliver`,
                              method: 'POST',
                              data: {
                                  evidenceUrl: 'https://oss.trust.com/demo.zip',
                                  deliveryDesc: res.content || '已完成阶段工作，请查收'
                              }
                          })
                          uni.showToast({ title: '提交成功' })
                          this.loadOrderDetail(orderId)
                      } catch (e) {
                          uni.showToast({ title: e.message, icon: 'none' })
                      }
                  }
              }
          })
      } else if (action === '确认验收') {
          uni.showModal({
              title: '确认验收',
              content: '确认验收通过该阶段成果？资金将释放给商家。',
              success: async (res) => {
                  if (res.confirm) {
                      try {
                          await request({
                              url: `/order/${orderId}/stage/${stageId}/accept`,
                              method: 'POST'
                          })
                          uni.showToast({ title: '验收成功' })
                          this.loadOrderDetail(orderId)
                      } catch (e) {
                          uni.showToast({ title: e.message, icon: 'none' })
                      }
                  }
              }
          })
      } else if (action === '拒绝验收') {
          uni.showModal({
              title: '拒绝验收',
              content: '请输入拒绝原因',
              editable: true,
              placeholderText: '原因',
              success: async (res) => {
                  if (res.confirm) {
                      try {
                          await request({
                              url: `/order/${orderId}/stage/${stageId}/reject`,
                              method: 'POST',
                              data: { reason: res.content || '成果不符合要求' }
                          })
                          uni.showToast({ title: '已拒绝' })
                          this.loadOrderDetail(orderId)
                      } catch (e) {
                          uni.showToast({ title: e.message, icon: 'none' })
                      }
                  }
              }
          })
      } else {
          uni.showToast({ title: action, icon: 'none' })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f0f4f8;
  padding-bottom: 32rpx;
}

.nav-bar {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  padding-top: calc(24rpx + env(safe-area-inset-top));
  background: #fff;
}

.back-btn {
  width: 80rpx;
  height: 80rpx;
  background: #f5f5f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  color: #666;
}

.nav-title {
  flex: 1;
  text-align: center;
  font-size: 36rpx;
  font-weight: 600;
  margin-right: 80rpx;
}

.status-card {
  margin: 24rpx 32rpx;
  background: #fff;
  border-radius: 48rpx;
  padding: 40rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 48rpx;
}

.status-badge {
  font-size: 24rpx;
  font-weight: 600;
  padding: 12rpx 24rpx;
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
  border-radius: 20rpx;
}

.order-no {
  font-size: 24rpx;
  color: #999;
}

.timeline {
  position: relative;
  padding-left: 48rpx;
}

.timeline-line {
  position: absolute;
  left: 12rpx;
  top: 16rpx;
  bottom: 16rpx;
  width: 4rpx;
  background: #eee;
}

.timeline-item {
  position: relative;
  padding-bottom: 48rpx;
  
  &:last-child {
    padding-bottom: 0;
  }
}

.timeline-dot {
  position: absolute;
  left: -42rpx;
  top: 8rpx;
  width: 24rpx;
  height: 24rpx;
  border-radius: 50%;
  background: #ddd;
  
  &.done {
    background: #22c55e;
    box-shadow: 0 0 0 8rpx rgba(34, 197, 94, 0.2);
  }
  
  &.active {
    background: #00AFE1;
    box-shadow: 0 0 0 8rpx rgba(0, 175, 225, 0.2);
  }
}

.timeline-content {
  padding-left: 16rpx;
}

.timeline-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #999;
  display: block;
  
  &.active {
    color: #00AFE1;
  }
}

.timeline-desc {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
  display: block;
}

.timeline-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 20rpx;
}

.action-btn {
  font-size: 22rpx;
  padding: 12rpx 24rpx;
  border-radius: 16rpx;
  background: #f5f5f5;
  color: #666;
  
  &.primary {
    background: #00AFE1;
    color: #fff;
  }
}

.safe-card {
  margin: 24rpx 32rpx;
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  border-radius: 48rpx;
  padding: 40rpx;
}

.safe-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #fff;
  display: block;
  margin-bottom: 16rpx;
}

.safe-text {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
}
</style>
