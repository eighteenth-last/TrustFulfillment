<template>
  <view class="page">
    <!-- 警告提示 -->
    <view class="warning-card">
      <view class="warning-header">
        <text class="warning-icon">⚠️</text>
        <text class="warning-title">仲裁申请中</text>
      </view>
      <text class="warning-text">订单 ID: {{ dispute.orderNo }} 产生纠纷。平台调查员已介入，请在24小时内补充完整证据。</text>
    </view>

    <!-- 上传证据 -->
    <view class="section">
      <text class="section-title">补充证据材料</text>
      <view class="evidence-grid">
        <view class="evidence-add" @click="addEvidence">
          <text class="add-icon">+</text>
        </view>
        <view class="evidence-item" v-for="(item, index) in evidences" :key="index">
          <image :src="item" mode="aspectFill" class="evidence-img"></image>
          <view class="evidence-delete" @click="deleteEvidence(index)">×</view>
        </view>
      </view>
    </view>

    <!-- 问题描述 -->
    <view class="section">
      <text class="section-title">问题描述</text>
      <view class="textarea-wrap">
        <textarea 
          v-model="description" 
          placeholder="请详细描述您遇到的问题..."
          maxlength="500"
          :show-confirm-bar="false"
        ></textarea>
        <text class="word-count">{{ description.length }}/500</text>
      </view>
    </view>

    <!-- 纠纷类型 -->
    <view class="section">
      <text class="section-title">纠纷类型</text>
      <view class="type-list">
        <view 
          class="type-item" 
          v-for="type in disputeTypes" 
          :key="type.value"
          :class="{ active: selectedType === type.value }"
          @click="selectedType = type.value"
        >
          {{ type.label }}
        </view>
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-btn" @click="handleSubmit">
      提交并请求平台裁决
    </view>
  </view>
</template>

<script>
import { request } from '@/utils/request'
import { useUserStore } from '@/stores/user'

export default {
  data() {
    return {
      dispute: {
        orderNo: 'TF8829310'
      },
      evidences: [],
      description: '',
      selectedType: '',
      disputeTypes: [
        { label: '未按时交付', value: 'delay' },
        { label: '质量不达标', value: 'quality' },
        { label: '与描述不符', value: 'mismatch' },
        { label: '其他问题', value: 'other' }
      ]
    }
  },
  methods: {
    addEvidence() {
      uni.chooseImage({
        count: 9 - this.evidences.length,
        success: (res) => {
          this.evidences.push(...res.tempFilePaths)
        }
      })
    },
    deleteEvidence(index) {
      this.evidences.splice(index, 1)
    },
    async handleSubmit() {
      if (!this.selectedType) {
        return uni.showToast({ title: '请选择纠纷类型', icon: 'none' })
      }
      if (!this.description) {
        return uni.showToast({ title: '请描述问题详情', icon: 'none' })
      }
      
      const userStore = useUserStore()
      if (!userStore.checkLogin()) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        setTimeout(() => {
          uni.navigateTo({ url: '/pages/login/index' })
        }, 1500)
        return
      }
      
      uni.showLoading({ title: '提交中...' })
      
      try {
        // 上传证据文件（如果有）
        const evidenceUrls = []
        for (const filePath of this.evidences) {
          const uploadRes = await request({
            url: '/file/upload',
            method: 'POST',
            data: { file: filePath }
          })
          if (uploadRes.data && uploadRes.data.url) {
            evidenceUrls.push(uploadRes.data.url)
          }
        }
        
        // 提交纠纷
        await request({
          url: '/dispute/create',
          method: 'POST',
          data: {
            orderNo: this.dispute.orderNo,
            type: this.selectedType,
            description: this.description,
            evidenceUrls: evidenceUrls.join(',')
          }
        })
        
        uni.hideLoading()
        uni.showToast({ title: '提交成功', icon: 'success' })
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.message || '提交失败', icon: 'none' })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f0f4f8;
  padding: 32rpx;
  padding-bottom: 180rpx;
}

.warning-card {
  background: #fef2f2;
  border: 2rpx solid #fecaca;
  border-radius: 32rpx;
  padding: 32rpx;
  margin-bottom: 32rpx;
}

.warning-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.warning-icon {
  font-size: 36rpx;
}

.warning-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #dc2626;
}

.warning-text {
  font-size: 26rpx;
  color: #b91c1c;
  line-height: 1.6;
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
  margin-bottom: 24rpx;
}

.evidence-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24rpx;
}

.evidence-add {
  aspect-ratio: 1;
  background: #f5f5f5;
  border: 4rpx dashed #ddd;
  border-radius: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-icon {
  font-size: 64rpx;
  color: #999;
}

.evidence-item {
  aspect-ratio: 1;
  border-radius: 24rpx;
  overflow: hidden;
  position: relative;
}

.evidence-img {
  width: 100%;
  height: 100%;
}

.evidence-delete {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
  width: 48rpx;
  height: 48rpx;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32rpx;
}

.textarea-wrap {
  position: relative;
  
  textarea {
    width: 100%;
    height: 240rpx;
    background: #f8f9fa;
    border-radius: 24rpx;
    padding: 24rpx;
    font-size: 28rpx;
    box-sizing: border-box;
  }
}

.word-count {
  position: absolute;
  bottom: 16rpx;
  right: 24rpx;
  font-size: 24rpx;
  color: #999;
}

.type-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.type-item {
  padding: 20rpx 32rpx;
  background: #f5f5f5;
  border-radius: 32rpx;
  font-size: 28rpx;
  color: #666;
  border: 2rpx solid transparent;
  
  &.active {
    background: rgba(0, 175, 225, 0.1);
    color: #00AFE1;
    border-color: #00AFE1;
  }
}

.submit-btn {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  margin: 32rpx;
  margin-bottom: calc(32rpx + env(safe-area-inset-bottom));
  background: #00AFE1;
  color: #fff;
  font-size: 34rpx;
  font-weight: 600;
  padding: 36rpx;
  border-radius: 32rpx;
  text-align: center;
}
</style>
