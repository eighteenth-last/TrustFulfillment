<template>
  <view class="page">
    <view class="form-card">
      <view class="form-item">
        <text class="label">头像</text>
        <view class="avatar-upload" @click="chooseAvatar">
          <image v-if="form.avatar" :src="form.avatar" class="avatar-img" mode="aspectFill" />
          <view v-else class="avatar-placeholder">
            <text class="iconfont icon-camera"></text>
            <text class="placeholder-text">点击上传</text>
          </view>
        </view>
      </view>
      
      <view class="form-item">
        <text class="label">昵称</text>
        <input v-model="form.nickname" class="input" placeholder="请输入昵称" maxlength="20" />
      </view>
      
      <view class="form-item">
        <text class="label">手机号</text>
        <text class="value disabled">{{ form.phone }}</text>
      </view>
      
      <view class="form-item">
        <text class="label">真实姓名</text>
        <input v-model="form.realName" class="input" placeholder="请输入真实姓名" maxlength="20" />
      </view>
      
      <view class="form-item">
        <text class="label">邮箱</text>
        <input v-model="form.email" class="input" type="email" placeholder="请输入邮箱" />
      </view>
    </view>
    
    <view class="btn-group">
      <view class="save-btn" @click="handleSave">保存</view>
    </view>
  </view>
</template>

<script>
import { request } from '@/utils/request'
import { useUserStore } from '@/stores/user'

export default {
  data() {
    return {
      form: {
        avatar: '',
        nickname: '',
        phone: '',
        realName: '',
        email: ''
      },
      uploading: false
    }
  },
  mounted() {
    this.loadUserInfo()
  },
  methods: {
    async loadUserInfo() {
      try {
        const res = await request({
          url: '/auth/info',
          method: 'GET'
        })
        if (res.data) {
          this.form = {
            avatar: res.data.avatar || '',
            nickname: res.data.nickname || '',
            phone: res.data.phone || '',
            realName: res.data.realName || '',
            email: res.data.email || ''
          }
        }
      } catch (e) {
        console.error('加载用户信息失败', e)
      }
    },
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0]
          this.uploadAvatar(tempFilePath)
        }
      })
    },
    async uploadAvatar(filePath) {
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
              this.form.avatar = data.data.url
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
        console.error('上传头像失败', e)
      }
    },
    async handleSave() {
      if (!this.form.nickname) {
        uni.showToast({ title: '请输入昵称', icon: 'none' })
        return
      }
      
      uni.showLoading({ title: '保存中...' })
      try {
        const res = await request({
          url: '/auth/update',
          method: 'PUT',
          data: {
            avatar: this.form.avatar,
            nickname: this.form.nickname,
            realName: this.form.realName,
            email: this.form.email
          }
        })
        
        // 更新本地用户信息
        const userStore = useUserStore()
        if (res.data) {
          // 使用后端返回的完整用户信息
          userStore.setUserInfo(res.data)
        } else {
          // 如果后端没有返回，使用表单数据更新
          const updatedInfo = { ...userStore.userInfo, ...this.form }
          userStore.setUserInfo(updatedInfo)
        }
        
        uni.hideLoading()
        uni.showToast({ title: '保存成功', icon: 'success' })
        
        // 延迟返回，让用户看到成功提示
        setTimeout(() => {
          // 触发上一页面刷新
          const pages = getCurrentPages()
          if (pages.length > 1) {
            const prevPage = pages[pages.length - 2]
            // 如果上一页是用户中心页面，触发其刷新方法
            if (prevPage.route === 'pages/user/index' && prevPage.$vm.loadUserInfo) {
              prevPage.$vm.loadUserInfo()
            }
          }
          uni.navigateBack()
        }, 1500)
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.message || '保存失败', icon: 'none' })
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

.form-card {
  background: #fff;
  border-radius: 32rpx;
  padding: 32rpx;
  margin-bottom: 32rpx;
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
  width: 160rpx;
  font-size: 28rpx;
  color: #333;
  font-weight: 600;
}

.input {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.value {
  flex: 1;
  font-size: 28rpx;
  color: #333;
  
  &.disabled {
    color: #999;
  }
}

.avatar-upload {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}

.avatar-img {
  width: 120rpx;
  height: 120rpx;
  border-radius: 24rpx;
}

.avatar-placeholder {
  width: 120rpx;
  height: 120rpx;
  border-radius: 24rpx;
  border: 2rpx dashed #ddd;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  
  .iconfont {
    font-size: 40rpx;
    color: #999;
  }
  
  .placeholder-text {
    font-size: 20rpx;
    color: #999;
    margin-top: 8rpx;
  }
}

.btn-group {
  padding: 0 32rpx;
}

.save-btn {
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
