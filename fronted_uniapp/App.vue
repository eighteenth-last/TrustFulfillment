<script>
export default {
  onLaunch() {
    console.log('App Launch')
  },
  onShow() {
    console.log('App Show')
  },
  onHide() {
    console.log('App Hide')
  }
}
</script>

<style>
/* 引入 iconfont 图标库 */
@import './static/fonts/iconfont.css';

/* 全局样式 */
page {
  background-color: #f0f4f8;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'PingFang SC', sans-serif;
}

/* 主题色 */
.text-tf {
  color: #00AFE1;
}
.bg-tf {
  background-color: #00AFE1;
}
.bg-tf-gradient {
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
}
.border-tf {
  border-color: #00AFE1;
}

/* 常用样式 */
.flex {
  display: flex;
}
.flex-col {
  flex-direction: column;
}
.items-center {
  align-items: center;
}
.justify-between {
  justify-content: space-between;
}
.justify-center {
  justify-content: center;
}
.gap-2 {
  gap: 16rpx;
}
.gap-3 {
  gap: 24rpx;
}
.gap-4 {
  gap: 32rpx;
}

/* 圆角 */
.rounded-xl {
  border-radius: 24rpx;
}
.rounded-2xl {
  border-radius: 32rpx;
}
.rounded-3xl {
  border-radius: 48rpx;
}
.rounded-full {
  border-radius: 50%;
}

/* 阴影 */
.shadow-sm {
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}
.shadow-lg {
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
}

/* 间距 */
.p-4 { padding: 32rpx; }
.p-5 { padding: 40rpx; }
.p-6 { padding: 48rpx; }
.mb-4 { margin-bottom: 32rpx; }
.mb-6 { margin-bottom: 48rpx; }
.mt-4 { margin-top: 32rpx; }

/* 字体 */
.text-xs { font-size: 24rpx; }
.text-sm { font-size: 28rpx; }
.text-lg { font-size: 36rpx; }
.text-xl { font-size: 40rpx; }
.text-2xl { font-size: 48rpx; }
.font-bold { font-weight: 600; }
.font-black { font-weight: 900; }
</style>
