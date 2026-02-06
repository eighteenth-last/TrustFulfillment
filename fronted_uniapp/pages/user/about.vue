<template>
  <view class="page">
    <scroll-view scroll-y class="content">
      <!-- Logo -->
      <view class="logo-section">
        <image class="logo" src="/static/logo.png" mode="aspectFit" />
        <text class="app-name">臻托 TrustFulfillment</text>
        <text class="slogan">让每一笔交易都值得托付</text>
        <text class="version">版本 v1.0.0</text>
      </view>

      <!-- 简介 -->
      <view class="intro-section">
        <text class="intro-text">
          臻托是一个专业的信托托管交易平台，致力于为用户和商家提供安全、透明、高效的交易服务。通过第三方资金托管、智能合约、区块链存证等技术，保障每一笔交易的安全。
        </text>
      </view>

      <!-- 核心功能 -->
      <view class="feature-section">
        <view class="section-title">核心功能</view>
        <view class="feature-list">
          <view class="feature-item">
            <text class="feature-icon">🛡️</text>
            <view class="feature-info">
              <text class="feature-name">资金托管</text>
              <text class="feature-desc">第三方托管，保障资金安全</text>
            </view>
          </view>
          <view class="feature-item">
            <text class="feature-icon">📝</text>
            <view class="feature-info">
              <text class="feature-name">电子合同</text>
              <text class="feature-desc">在线签署，法律效力</text>
            </view>
          </view>
          <view class="feature-item">
            <text class="feature-icon">🔗</text>
            <view class="feature-info">
              <text class="feature-name">区块链存证</text>
              <text class="feature-desc">交易记录不可篡改</text>
            </view>
          </view>
          <view class="feature-item">
            <text class="feature-icon">⚖️</text>
            <view class="feature-info">
              <text class="feature-name">纠纷仲裁</text>
              <text class="feature-desc">专业团队快速处理</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 统计数据 -->
      <view class="stats-section">
        <view class="section-title">平台数据</view>
        <view class="stats-grid">
          <view class="stat-item">
            <text class="stat-value">10万+</text>
            <text class="stat-label">注册用户</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">5万+</text>
            <text class="stat-label">成交订单</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">99.8%</text>
            <text class="stat-label">履约率</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">2.5亿</text>
            <text class="stat-label">托管金额</text>
          </view>
        </view>
      </view>

      <!-- 联系方式 -->
      <view class="contact-section">
        <view class="section-title">联系我们</view>
        <view class="contact-list">
          <view class="contact-item">
            <text class="contact-label">客服电话</text>
            <text class="contact-value" @click="callPhone">400-888-8888</text>
          </view>
          <view class="contact-item">
            <text class="contact-label">客服邮箱</text>
            <text class="contact-value" @click="copyEmail">service@trust.com</text>
          </view>
          <view class="contact-item">
            <text class="contact-label">官方网站</text>
            <text class="contact-value" @click="openWebsite">www.trust.com</text>
          </view>
          <view class="contact-item">
            <text class="contact-label">公司地址</text>
            <text class="contact-value">北京市朝阳区xxx大厦</text>
          </view>
        </view>
      </view>

      <!-- 法律信息 -->
      <view class="legal-section">
        <view class="legal-item" @click="openDoc('privacy')">
          <text class="legal-text">隐私政策</text>
          <text class="legal-arrow">›</text>
        </view>
        <view class="legal-item" @click="openDoc('terms')">
          <text class="legal-text">用户协议</text>
          <text class="legal-arrow">›</text>
        </view>
        <view class="legal-item" @click="openDoc('license')">
          <text class="legal-text">软件许可</text>
          <text class="legal-arrow">›</text>
        </view>
      </view>

      <!-- 版权信息 -->
      <view class="copyright">
        <text class="copyright-text">© 2024 臻托科技有限公司</text>
        <text class="copyright-text">京ICP备xxxxxxxx号</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  methods: {
    callPhone() {
      uni.makePhoneCall({
        phoneNumber: '4008888888'
      })
    },
    copyEmail() {
      uni.setClipboardData({
        data: 'service@trust.com',
        success: () => {
          uni.showToast({ title: '邮箱已复制', icon: 'success' })
        }
      })
    },
    openWebsite() {
      uni.showToast({ title: '功能开发中', icon: 'none' })
    },
    openDoc(type) {
      uni.showToast({ title: '文档加载中', icon: 'none' })
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f7fa;
}

.content {
  height: 100vh;
}

.logo-section {
  padding: 80rpx 32rpx;
  text-align: center;
  background: #fff;
}

.logo {
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 24rpx;
}

.app-name {
  font-size: 40rpx;
  font-weight: 900;
  color: #333;
  display: block;
  margin-bottom: 16rpx;
}

.slogan {
  font-size: 26rpx;
  color: #999;
  display: block;
  margin-bottom: 24rpx;
}

.version {
  font-size: 24rpx;
  color: #ccc;
}

.intro-section {
  margin: 32rpx;
  padding: 32rpx;
  background: #fff;
  border-radius: 24rpx;
}

.intro-text {
  font-size: 28rpx;
  color: #666;
  line-height: 1.8;
}

.feature-section, .stats-section, .contact-section {
  margin: 0 32rpx 32rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 16rpx;
}

.feature-list {
  background: #fff;
  border-radius: 24rpx;
  padding: 16rpx;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 24rpx;
}

.feature-icon {
  font-size: 48rpx;
}

.feature-info {
  flex: 1;
}

.feature-name {
  font-size: 28rpx;
  color: #333;
  font-weight: 600;
  display: block;
  margin-bottom: 8rpx;
}

.feature-desc {
  font-size: 24rpx;
  color: #999;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
}

.stat-item {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  text-align: center;
}

.stat-value {
  font-size: 48rpx;
  font-weight: 900;
  color: #00AFE1;
  display: block;
  margin-bottom: 12rpx;
}

.stat-label {
  font-size: 24rpx;
  color: #999;
}

.contact-list {
  background: #fff;
  border-radius: 24rpx;
  overflow: hidden;
}

.contact-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.contact-label {
  font-size: 28rpx;
  color: #666;
}

.contact-value {
  font-size: 26rpx;
  color: #00AFE1;
}

.legal-section {
  margin: 0 32rpx 32rpx;
  background: #fff;
  border-radius: 24rpx;
  overflow: hidden;
}

.legal-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.legal-text {
  font-size: 28rpx;
  color: #333;
}

.legal-arrow {
  font-size: 32rpx;
  color: #ccc;
}

.copyright {
  padding: 48rpx 32rpx 80rpx;
  text-align: center;
}

.copyright-text {
  font-size: 24rpx;
  color: #999;
  display: block;
  line-height: 1.8;
}
</style>
