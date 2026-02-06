<template>
  <view class="page">
    <!-- 搜索框 -->
    <view class="search-box">
      <text class="search-icon">🔍</text>
      <input class="search-input" placeholder="搜索问题..." v-model="searchKeyword" />
    </view>

    <!-- 常见问题 -->
    <view class="section">
      <view class="section-title">常见问题</view>
      <view class="faq-list">
        <view class="faq-item" v-for="(item, index) in filteredFaqs" :key="index" @click="toggleFaq(index)">
          <view class="faq-question">
            <text class="faq-q-icon">Q</text>
            <text class="faq-q-text">{{ item.question }}</text>
            <text class="faq-arrow" :class="{ open: item.open }">›</text>
          </view>
          <view class="faq-answer" v-if="item.open">
            <text class="faq-a-icon">A</text>
            <text class="faq-a-text">{{ item.answer }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 联系客服 -->
    <view class="section">
      <view class="section-title">联系我们</view>
      <view class="contact-card">
        <view class="contact-item" @click="callService">
          <text class="contact-icon">📞</text>
          <view class="contact-info">
            <text class="contact-label">客服电话</text>
            <text class="contact-value">400-888-8888</text>
          </view>
        </view>
        <view class="contact-item" @click="openChat">
          <text class="contact-icon">💬</text>
          <view class="contact-info">
            <text class="contact-label">在线客服</text>
            <text class="contact-value">工作日 9:00-18:00</text>
          </view>
        </view>
        <view class="contact-item" @click="sendEmail">
          <text class="contact-icon">✉️</text>
          <view class="contact-info">
            <text class="contact-label">邮箱反馈</text>
            <text class="contact-value">service@trust.com</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 意见反馈 -->
    <view class="feedback-btn" @click="showFeedback = true">
      提交反馈
    </view>

    <!-- 反馈弹窗 -->
    <view class="feedback-modal" v-if="showFeedback" @click="showFeedback = false">
      <view class="feedback-content" @click.stop>
        <view class="feedback-header">
          <text class="feedback-title">意见反馈</text>
          <text class="feedback-close" @click="showFeedback = false">✕</text>
        </view>
        <textarea 
          class="feedback-textarea" 
          v-model="feedbackText"
          placeholder="请描述您遇到的问题或建议..."
          :maxlength="500"
        />
        <view class="feedback-count">{{ feedbackText.length }}/500</view>
        <button class="feedback-submit" @click="submitFeedback">提交</button>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      searchKeyword: '',
      showFeedback: false,
      feedbackText: '',
      faqs: [
        {
          question: '如何发布任务？',
          answer: '点击首页"发布任务"按钮，填写项目需求和预算，设置付款阶段，提交后等待商家接单。',
          open: false
        },
        {
          question: '资金托管安全吗？',
          answer: '平台采用第三方资金托管，资金由平台监管，只有在您验收通过后才会释放给商家，保障您的资金安全。',
          open: false
        },
        {
          question: '如何验收项目？',
          answer: '商家提交交付后，您会收到通知。登录查看交付内容，确认无误后点击"验收通过"，资金将自动释放给商家。',
          open: false
        },
        {
          question: '验收不通过怎么办？',
          answer: '如果交付不符合要求，可以点击"拒绝验收"并说明原因，商家需要重新修改后再次提交。',
          open: false
        },
        {
          question: '质保期是什么？',
          answer: '质保期是项目完成后的免费维护期，默认15天。期间如有问题，商家需免费修复。质保期结束后，质保款自动释放。',
          open: false
        },
        {
          question: '如何申请退款？',
          answer: '在待接单或待托管状态下可以取消订单。如果已托管资金，需要与商家协商或申请平台介入处理。',
          open: false
        },
        {
          question: '平台收取手续费吗？',
          answer: '平台向商家收取5%的服务费，用户发布任务不收取费用。',
          open: false
        },
        {
          question: '如何提高信用分？',
          answer: '按时完成交易、及时验收、给予好评等行为都会提高信用分。信用分越高，越容易获得优质商家的关注。',
          open: false
        }
      ]
    }
  },
  computed: {
    filteredFaqs() {
      if (!this.searchKeyword) {
        return this.faqs
      }
      return this.faqs.filter(faq => 
        faq.question.includes(this.searchKeyword) || 
        faq.answer.includes(this.searchKeyword)
      )
    }
  },
  methods: {
    toggleFaq(index) {
      this.faqs[index].open = !this.faqs[index].open
    },
    callService() {
      uni.makePhoneCall({
        phoneNumber: '4008888888'
      })
    },
    openChat() {
      uni.showToast({ title: '客服功能开发中', icon: 'none' })
    },
    sendEmail() {
      uni.setClipboardData({
        data: 'service@trust.com',
        success: () => {
          uni.showToast({ title: '邮箱已复制', icon: 'success' })
        }
      })
    },
    submitFeedback() {
      if (!this.feedbackText.trim()) {
        uni.showToast({ title: '请输入反馈内容', icon: 'none' })
        return
      }
      
      // TODO: 提交反馈到后端
      uni.showToast({ title: '提交成功，感谢您的反馈', icon: 'success' })
      this.showFeedback = false
      this.feedbackText = ''
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 120rpx;
}

.search-box {
  margin: 32rpx;
  background: #fff;
  border-radius: 48rpx;
  padding: 24rpx 32rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.search-icon {
  font-size: 32rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
}

.section {
  margin: 0 32rpx 32rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 16rpx;
}

.faq-list {
  background: #fff;
  border-radius: 24rpx;
  overflow: hidden;
}

.faq-item {
  border-bottom: 1rpx solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.faq-question {
  padding: 32rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.faq-q-icon {
  width: 48rpx;
  height: 48rpx;
  background: #00AFE1;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 600;
  flex-shrink: 0;
}

.faq-q-text {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.faq-arrow {
  font-size: 32rpx;
  color: #ccc;
  transition: transform 0.3s;
  
  &.open {
    transform: rotate(90deg);
  }
}

.faq-answer {
  padding: 0 32rpx 32rpx;
  display: flex;
  gap: 16rpx;
  background: #f9fafb;
}

.faq-a-icon {
  width: 48rpx;
  height: 48rpx;
  background: #10b981;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 600;
  flex-shrink: 0;
}

.faq-a-text {
  flex: 1;
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
}

.contact-card {
  background: #fff;
  border-radius: 24rpx;
  overflow: hidden;
}

.contact-item {
  padding: 32rpx;
  display: flex;
  align-items: center;
  gap: 24rpx;
  border-bottom: 1rpx solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.contact-icon {
  font-size: 48rpx;
}

.contact-info {
  flex: 1;
}

.contact-label {
  font-size: 28rpx;
  color: #333;
  display: block;
  margin-bottom: 8rpx;
}

.contact-value {
  font-size: 24rpx;
  color: #999;
}

.feedback-btn {
  margin: 48rpx 32rpx;
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  color: #fff;
  border-radius: 48rpx;
  padding: 32rpx;
  text-align: center;
  font-size: 30rpx;
  font-weight: 600;
  box-shadow: 0 8rpx 24rpx rgba(0, 175, 225, 0.3);
}

.feedback-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.feedback-content {
  width: 600rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.feedback-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.feedback-close {
  font-size: 40rpx;
  color: #999;
}

.feedback-textarea {
  width: 100%;
  height: 300rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  padding: 24rpx;
  font-size: 28rpx;
  margin-bottom: 16rpx;
}

.feedback-count {
  text-align: right;
  font-size: 24rpx;
  color: #999;
  margin-bottom: 24rpx;
}

.feedback-submit {
  width: 100%;
  background: #00AFE1;
  color: #fff;
  border-radius: 48rpx;
  padding: 24rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: none;
}
</style>
