<template>
  <view class="page">
    <!-- 申请状态提示 -->
    <view v-if="merchantStatus.hasPendingApply" class="status-card pending">
      <text class="status-icon">⏳</text>
      <view class="status-content">
        <text class="status-title">申请审核中</text>
        <text class="status-desc">您的商家申请正在审核中，请耐心等待</text>
      </view>
    </view>
    
    <view v-else-if="merchantStatus.isMerchant" class="status-card success">
      <text class="status-icon">✅</text>
      <view class="status-content">
        <text class="status-title">您已是商家</text>
        <text class="status-desc">可以在任务大厅接单啦</text>
      </view>
    </view>
    
    <!-- 申请表单 -->
    <view v-else class="form-container">
      <!-- 商户类型选择 -->
      <view class="section-title">商户类型</view>
      <view class="type-cards">
        <view 
          class="type-card" 
          :class="{ active: form.merchantType === 1 }"
          @click="form.merchantType = 1"
        >
          <text class="type-icon">👤</text>
          <text class="type-name">个体工商户</text>
          <text class="type-rate">提成 8%</text>
        </view>
        <view 
          class="type-card" 
          :class="{ active: form.merchantType === 2 }"
          @click="form.merchantType = 2"
        >
          <text class="type-icon">🏢</text>
          <text class="type-name">企业/组织</text>
          <text class="type-rate">提成 5%</text>
        </view>
      </view>
      
      <!-- 基本信息 -->
      <view class="section-title">基本信息</view>
      <view class="form-card">
        <view class="form-item">
          <text class="label required">店铺名称</text>
          <input v-model="form.shopName" class="input" placeholder="请输入店铺名称" />
        </view>
        
        <view v-if="form.merchantType === 2" class="form-item">
          <text class="label required">企业名称</text>
          <input v-model="form.companyName" class="input" placeholder="请输入企业全称" />
        </view>
        
        <view class="form-item">
          <text class="label required">法人/负责人</text>
          <input v-model="form.legalPerson" class="input" placeholder="请输入姓名" />
        </view>
        
        <view class="form-item">
          <text class="label required">身份证号</text>
          <input v-model="form.legalIdCard" class="input" placeholder="请输入身份证号" />
        </view>
        
        <view class="form-item">
          <text class="label required">联系电话</text>
          <input v-model="form.contactPhone" class="input" type="number" placeholder="请输入联系电话" />
        </view>
        
        <view class="form-item">
          <text class="label">联系邮箱</text>
          <input v-model="form.contactEmail" class="input" type="email" placeholder="请输入邮箱" />
        </view>
        
        <view class="form-item">
          <text class="label">经营地址</text>
          <input v-model="form.businessAddress" class="input" placeholder="请输入经营地址" />
        </view>
      </view>
      
      <!-- 资质材料 -->
      <view class="section-title">资质材料</view>
      <view class="form-card">
        <view class="upload-item">
          <text class="label required">身份证正面</text>
          <view class="upload-box" @click="uploadImage('idCardFront')">
            <image v-if="form.idCardFront" :src="form.idCardFront" class="upload-img" mode="aspectFill" />
            <view v-else class="upload-placeholder">
              <text class="iconfont icon-add"></text>
              <text class="upload-text">上传照片</text>
            </view>
          </view>
        </view>
        
        <view class="upload-item">
          <text class="label required">身份证反面</text>
          <view class="upload-box" @click="uploadImage('idCardBack')">
            <image v-if="form.idCardBack" :src="form.idCardBack" class="upload-img" mode="aspectFill" />
            <view v-else class="upload-placeholder">
              <text class="iconfont icon-add"></text>
              <text class="upload-text">上传照片</text>
            </view>
          </view>
        </view>
        
        <view class="upload-item">
          <text class="label" :class="{ required: form.merchantType === 2 }">营业执照</text>
          <view class="upload-box" @click="uploadImage('licenseImg')">
            <image v-if="form.licenseImg" :src="form.licenseImg" class="upload-img" mode="aspectFill" />
            <view v-else class="upload-placeholder">
              <text class="iconfont icon-add"></text>
              <text class="upload-text">上传照片</text>
            </view>
          </view>
        </view>
        
        <view v-if="form.merchantType === 2" class="form-item">
          <text class="label">营业执照号</text>
          <input v-model="form.licenseNo" class="input" placeholder="请输入营业执照号" />
        </view>
      </view>
      
      <!-- 提交按钮 -->
      <view class="btn-group">
        <view class="submit-btn" @click="handleSubmit">提交申请</view>
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
      merchantStatus: {
        isMerchant: false,
        hasPendingApply: false
      },
      form: {
        merchantType: 1,
        shopName: '',
        companyName: '',
        legalPerson: '',
        legalIdCard: '',
        contactPhone: '',
        contactEmail: '',
        businessAddress: '',
        licenseNo: '',
        idCardFront: '',
        idCardBack: '',
        licenseImg: ''
      },
      uploading: false
    }
  },
  mounted() {
    this.loadMerchantStatus()
  },
  methods: {
    async loadMerchantStatus() {
      try {
        const res = await request({
          url: '/merchant/status',
          method: 'GET'
        })
        if (res.data) {
          this.merchantStatus = res.data
          
          // 如果已经是商家，提示并返回
          if (res.data.isMerchant) {
            setTimeout(() => {
              uni.navigateBack()
            }, 2000)
          }
        }
      } catch (e) {
        console.error('加载商家状态失败', e)
      }
    },
    uploadImage(field) {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0]
          this.doUpload(tempFilePath, field)
        }
      })
    },
    async doUpload(filePath, field) {
      this.uploading = true
      uni.showLoading({ title: '上传中...' })
      
      try {
        const userStore = useUserStore()
        const token = userStore.token
        
        if (!token) {
          uni.hideLoading()
          uni.showToast({ title: '请先登录', icon: 'none' })
          setTimeout(() => {
            uni.navigateTo({ url: '/pages/login/index' })
          }, 1500)
          return
        }
        
        uni.uploadFile({
          url: 'http://localhost:8080/api/file/upload',
          filePath: filePath,
          name: 'file',
          formData: {
            folder: 'images'
          },
          header: {
            'Authorization': token
          },
          success: (uploadRes) => {
            const data = JSON.parse(uploadRes.data)
            if (data.code === 200) {
              this.form[field] = data.data.url
              uni.showToast({ title: '上传成功', icon: 'success' })
            } else {
              uni.showToast({ title: data.message || '上传失败', icon: 'none' })
            }
          },
          fail: (err) => {
            console.error('上传失败', err)
            uni.showToast({ title: '上传失败', icon: 'none' })
          },
          complete: () => {
            uni.hideLoading()
            this.uploading = false
          }
        })
      } catch (e) {
        uni.hideLoading()
        this.uploading = false
        console.error('上传失败', e)
      }
    },
    async handleSubmit() {
      // 验证必填项
      if (!this.form.shopName) {
        uni.showToast({ title: '请输入店铺名称', icon: 'none' })
        return
      }
      if (this.form.merchantType === 2 && !this.form.companyName) {
        uni.showToast({ title: '请输入企业名称', icon: 'none' })
        return
      }
      if (!this.form.legalPerson) {
        uni.showToast({ title: '请输入法人/负责人姓名', icon: 'none' })
        return
      }
      if (!this.form.legalIdCard) {
        uni.showToast({ title: '请输入身份证号', icon: 'none' })
        return
      }
      if (!this.form.contactPhone) {
        uni.showToast({ title: '请输入联系电话', icon: 'none' })
        return
      }
      if (!this.form.idCardFront) {
        uni.showToast({ title: '请上传身份证正面', icon: 'none' })
        return
      }
      if (!this.form.idCardBack) {
        uni.showToast({ title: '请上传身份证反面', icon: 'none' })
        return
      }
      if (this.form.merchantType === 2 && !this.form.licenseImg) {
        uni.showToast({ title: '请上传营业执照', icon: 'none' })
        return
      }
      
      uni.showLoading({ title: '提交中...' })
      try {
        await request({
          url: '/merchant/apply',
          method: 'POST',
          data: this.form
        })
        
        uni.hideLoading()
        uni.showModal({
          title: '提交成功',
          content: '您的商家申请已提交，请等待审核',
          showCancel: false,
          success: () => {
            // 触发用户中心页面刷新
            const pages = getCurrentPages()
            if (pages.length > 1) {
              const prevPage = pages[pages.length - 2]
              // 如果上一页是用户中心页面，触发其刷新方法
              if (prevPage.route === 'pages/user/index' && prevPage.$vm.loadMerchantStatus) {
                prevPage.$vm.loadMerchantStatus()
              }
            }
            uni.navigateBack()
          }
        })
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
}

.status-card {
  background: #fff;
  border-radius: 32rpx;
  padding: 48rpx;
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 32rpx;
  
  &.pending {
    border-left: 8rpx solid #f59e0b;
  }
  
  &.success {
    border-left: 8rpx solid #22c55e;
  }
}

.status-icon {
  font-size: 64rpx;
}

.status-content {
  flex: 1;
}

.status-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 8rpx;
}

.status-desc {
  font-size: 26rpx;
  color: #999;
}

.form-container {
  padding-bottom: 32rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 24rpx;
}

.type-cards {
  display: flex;
  gap: 24rpx;
  margin-bottom: 48rpx;
}

.type-card {
  flex: 1;
  background: #fff;
  border-radius: 32rpx;
  padding: 40rpx 24rpx;
  text-align: center;
  border: 4rpx solid #f0f0f0;
  
  &.active {
    border-color: #00AFE1;
    background: rgba(0, 175, 225, 0.05);
  }
}

.type-icon {
  font-size: 64rpx;
  display: block;
  margin-bottom: 16rpx;
}

.type-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 8rpx;
}

.type-rate {
  font-size: 24rpx;
  color: #00AFE1;
}

.form-card {
  background: #fff;
  border-radius: 32rpx;
  padding: 32rpx;
  margin-bottom: 48rpx;
}

.form-item {
  display: flex;
  align-items: center;
  padding: 32rpx 0;
  border-bottom: 2rpx solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.label {
  width: 200rpx;
  font-size: 28rpx;
  color: #333;
  font-weight: 600;
  
  &.required::before {
    content: '*';
    color: #ef4444;
    margin-right: 4rpx;
  }
}

.input {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.upload-item {
  padding: 32rpx 0;
  border-bottom: 2rpx solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
}

.upload-box {
  margin-top: 16rpx;
  width: 200rpx;
  height: 200rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.upload-img {
  width: 100%;
  height: 100%;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  border: 2rpx dashed #ddd;
  border-radius: 16rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  
  .iconfont {
    font-size: 48rpx;
    color: #999;
  }
  
  .upload-text {
    font-size: 24rpx;
    color: #999;
    margin-top: 8rpx;
  }
}

.btn-group {
  padding: 0 32rpx;
}

.submit-btn {
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  color: #fff;
  font-size: 32rpx;
  font-weight: 600;
  padding: 32rpx;
  border-radius: 48rpx;
  text-align: center;
  box-shadow: 0 8rpx 32rpx rgba(0, 175, 225, 0.3);
}
</style>
