<template>
  <view class="page">
    <!-- 分类标签 -->
    <scroll-view scroll-x class="category-scroll">
      <view class="category-list">
        <view 
          v-for="cat in categories" 
          :key="cat.value"
          class="category-item"
          :class="{ active: currentCategory === cat.value }"
          @click="currentCategory = cat.value"
        >
          {{ cat.label }}
        </view>
      </view>
    </scroll-view>

    <!-- 任务列表 -->
    <scroll-view scroll-y class="task-scroll" @scrolltolower="loadMore">
      <view class="task-list">
        <view class="task-card" v-for="task in taskList" :key="task.id" @click="goDetail(task.id)">
          <view class="task-header">
            <text class="task-title">{{ task.title }}</text>
            <text class="task-price">¥{{ task.price }}</text>
          </view>
          
          <view class="task-meta">
            <text class="task-category">{{ task.categoryName }}</text>
            <text class="task-time">发布于 {{ task.publishTime }}</text>
          </view>
          
          <view class="task-tags">
            <text class="tag tag-primary" v-if="task.trustDeposit">信托账户已到账</text>
            <text class="tag">担保中</text>
          </view>
          
          <view class="task-footer">
            <view class="bid-btn" @click.stop="handleBid(task)">立即抢单</view>
          </view>
        </view>
      </view>
      
      <view class="loading-more" v-if="loading">
        <text>加载中...</text>
      </view>
      <view class="no-more" v-if="!hasMore && taskList.length > 0">
        <text>没有更多了</text>
      </view>
      
      <!-- 底部安全距离 -->
      <view style="height: 120rpx;"></view>
    </scroll-view>
    
    <!-- 自定义TabBar -->
    <custom-tabbar :current="1"></custom-tabbar>
  </view>
</template>

<script>
import { request } from '@/utils/request'
import { getBusinessCategories } from '@/utils/api'
import { useUserStore } from '@/stores/user'

export default {
  data() {
    return {
      currentCategory: null,  // null 表示全部
      loading: false,
      hasMore: true,
      page: 1,
      categories: [],  // 扁平化的分类列表（包含二级和三级）
      taskList: []
    }
  },
  onShow() {
    this.loadCategories()
    this.loadData()
  },
  watch: {
    currentCategory() {
      this.page = 1
      this.taskList = []
      this.hasMore = true
      this.loadData()
    }
  },
  methods: {
    async loadCategories() {
      try {
        const res = await getBusinessCategories()
        if (res.data && res.data.length > 0) {
          // 构建扁平化的分类列表（包含二级和三级分类）
          const cats = [{ label: '全部任务', value: null }]
          
          res.data.forEach(level1 => {
            if (level1.children && level1.children.length > 0) {
              level1.children.forEach(level2 => {
                // 添加二级分类
                cats.push({
                  label: level2.name,
                  value: level2.id,
                  level: 2
                })
                
                // 如果有三级分类，也添加进来
                if (level2.children && level2.children.length > 0) {
                  level2.children.forEach(level3 => {
                    cats.push({
                      label: '  ' + level3.name, // 添加缩进表示层级
                      value: level3.id,
                      level: 3
                    })
                  })
                }
              })
            }
          })
          
          this.categories = cats
          console.log('分类加载成功，共', cats.length, '个分类')
        }
      } catch (e) {
        console.error('加载分类失败', e)
        // 使用默认分类（包含三级）
        this.categories = [
          { label: '全部任务', value: null },
          { label: '后端开发', value: 101 },
          { label: '  Java后端开发', value: 1011 },
          { label: '  Python后端开发', value: 1012 },
          { label: '前端开发', value: 102 },
          { label: '  Vue开发', value: 1021 },
          { label: '  React开发', value: 1022 },
          { label: '小程序开发', value: 104 },
          { label: '  微信小程序', value: 1041 },
          { label: 'UI设计', value: 201 }
        ]
      }
    },
    async loadData() {
      if (this.loading || !this.hasMore) return
      
      this.loading = true
      try {
        const params = {
          page: this.page,
          size: 10
        }
        
        // 如果选择了分类，添加分类筛选参数
        if (this.currentCategory !== null) {
          params.categoryId = this.currentCategory
        }
        
        const res = await request({
          url: '/public/orders',
          method: 'GET',
          data: params
        })
        
        if (res.data && res.data.records) {
          const newTasks = res.data.records.map(order => ({
            id: order.id,
            title: order.title,
            price: this.formatCurrency(order.totalAmount),
            publishTime: this.formatTime(order.createTime),
            trustDeposit: order.status >= 1,
            categoryName: order.categoryName || '其他' // 添加分类名称
          }))
          
          if (this.page === 1) {
            this.taskList = newTasks
          } else {
            this.taskList = [...this.taskList, ...newTasks]
          }
          
          if (newTasks.length < 10) {
            this.hasMore = false
          } else {
            this.page++
          }
        } else {
          this.hasMore = false
        }
      } catch (e) {
        console.error('加载任务失败', e)
        this.hasMore = false
      } finally {
        this.loading = false
      }
    },
    formatCurrency(value) {
      if (!value) return '0'
      return parseFloat(value).toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 })
    },
    formatTime(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const now = new Date()
      const diff = Math.floor((now - date) / 1000)
      
      if (diff < 3600) return Math.floor(diff / 60) + '分钟前'
      if (diff < 86400) return Math.floor(diff / 3600) + '小时前'
      if (diff < 2592000) return Math.floor(diff / 86400) + '天前'
      return date.toLocaleDateString()
    },
    goDetail(id) {
      uni.navigateTo({ url: '/pages/task/detail?id=' + id })
    },
    async handleBid(task) {
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
        content: '确定要抢单【' + task.title + '】吗？抢单后需要签署合同。',
        success: async (res) => {
          if (res.confirm) {
            uni.showLoading({ title: '抢单中...' })
            try {
              await request({
                url: `/order/${task.id}/accept`,
                method: 'POST'
              })
              uni.hideLoading()
              
              // 刷新任务列表，移除已抢的订单
              this.page = 1
              this.taskList = []
              this.hasMore = true
              await this.loadData()
              
              // 显示成功提示
              uni.showModal({
                title: '抢单成功',
                content: '您已成功接单，请尽快签署合同以确认项目详情。',
                showCancel: false,
                confirmText: '去签署合同',
                success: (modalRes) => {
                  if (modalRes.confirm) {
                    // 跳转到订单详情页，在那里可以签署合同
                    uni.navigateTo({ url: '/pages/order/detail?id=' + task.id })
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
    },
    loadMore() {
      this.loadData()
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
}

.category-scroll {
  white-space: nowrap;
  background: #fff;
  padding: 24rpx 32rpx;
}

.category-list {
  display: inline-flex;
  gap: 24rpx;
}

.category-item {
  padding: 16rpx 32rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  color: #666;
  background: #f5f5f5;
  flex-shrink: 0;
  
  &.active {
    background: #00AFE1;
    color: #fff;
  }
}

.task-scroll {
  flex: 1;
  padding: 0;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  padding: 32rpx 32rpx;
}

.task-card {
  background: #fff;
  border-radius: 48rpx;
  padding: 32rpx 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.task-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  flex: 1;
  margin-right: 16rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.task-price {
  font-size: 36rpx;
  font-weight: 900;
  color: #00AFE1;
  flex-shrink: 0;
}

.task-meta {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.task-category {
  font-size: 22rpx;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
  flex-shrink: 0;
}

.task-time {
  font-size: 24rpx;
  color: #999;
}

.task-tags {
  display: flex;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.tag {
  font-size: 22rpx;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  border: 2rpx solid #eee;
  color: #999;
  
  &.tag-primary {
    border-color: rgba(0, 175, 225, 0.3);
    color: #00AFE1;
  }
}

.task-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.bid-btn {
  background: #00AFE1;
  color: #fff;
  font-size: 26rpx;
  font-weight: 600;
  padding: 16rpx 32rpx;
  border-radius: 24rpx;
}

.loading-more, .no-more {
  text-align: center;
  padding: 32rpx 0;
  color: #999;
  font-size: 26rpx;
}
</style>
