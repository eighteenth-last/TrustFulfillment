<template>
  <view class="custom-tabbar">
    <view 
      v-for="(item, index) in tabList" 
      :key="index" 
      class="tabbar-item"
      :class="{ active: currentIndex === index }"
      @click="switchTab(index)"
    >
      <image 
        class="tabbar-icon" 
        :src="currentIndex === index ? item.iconActive : item.icon"
        mode="aspectFit"
      />
      <text class="tabbar-text">{{ item.text }}</text>
    </view>
  </view>
</template>

<script>
// Base64 编码的 SVG 图标
const icons = {
  // 首页图标
  home: "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjOTk5OTk5IiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCI+PHBhdGggZD0iTTMgOWw5LTcgOSA3djExYTIgMiAwIDAgMS0yIDJINWEyIDIgMCAwIDEtMi0yeiIvPjxwb2x5bGluZSBwb2ludHM9IjkgMjIgOSAxMiAxNSAxMiAxNSAyMiIvPjwvc3ZnPg==",
  homeActive: "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iIzAwQUZFMSIgc3Ryb2tlPSIjMDBBRkUxIiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCI+PHBhdGggZD0iTTMgOWw5LTcgOSA3djExYTIgMiAwIDAgMS0yIDJINWEyIDIgMCAwIDEtMi0yeiIvPjxwb2x5bGluZSBwb2ludHM9IjkgMjIgOSAxMiAxNSAxMiAxNSAyMiIgZmlsbD0iI2ZmZiIvPjwvc3ZnPg==",
  
  // 搜索/任务大厅图标
  task: "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjOTk5OTk5IiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCI+PGNpcmNsZSBjeD0iMTEiIGN5PSIxMSIgcj0iOCIvPjxwYXRoIGQ9Ik0yMSAyMWwtNC4zNS00LjM1Ii8+PC9zdmc+",
  taskActive: "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjMDBBRkUxIiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCI+PGNpcmNsZSBjeD0iMTEiIGN5PSIxMSIgcj0iOCIvPjxwYXRoIGQ9Ik0yMSAyMWwtNC4zNS00LjM1Ii8+PC9zdmc+",
  
  // 订单图标
  order: "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjOTk5OTk5IiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCI+PHBhdGggZD0iTTE0IDJINWE0IDQgMCAwIDAtMiAydjE2YTIgMiAwIDAgMCAyIDJoMTJhMiAyIDAgMCAwIDItMlY4eiIvPjxwb2x5bGluZSBwb2ludHM9IjE0IDIgMTQgOCAyMCA4Ii8+PGxpbmUgeDE9IjE2IiB5MT0iMTMiIHgyPSI4IiB5Mj0iMTMiLz48bGluZSB4MT0iMTYiIHkxPSIxNyIgeDI9IjgiIHkyPSIxNyIvPjwvc3ZnPg==",
  orderActive: "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iIzAwQUZFMSIgc3Ryb2tlPSIjMDBBRkUxIiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCI+PHBhdGggZD0iTTE0IDJINWE0IDQgMCAwIDAtMiAydjE2YTIgMiAwIDAgMCAyIDJoMTJhMiAyIDAgMCAwIDItMlY4eiIvPjxwb2x5bGluZSBwb2ludHM9IjE0IDIgMTQgOCAyMCA4IiBmaWxsPSIjZmZmIi8+PGxpbmUgeDE9IjE2IiB5MT0iMTMiIHgyPSI4IiB5Mj0iMTMiIHN0cm9rZT0iI2ZmZiIvPjxsaW5lIHgxPSIxNiIgeTE9IjE3IiB4Mj0iOCIgeTI9IjE3IiBzdHJva2U9IiNmZmYiLz48L3N2Zz4=",
  
  // 用户图标
  user: "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjOTk5OTk5IiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCI+PHBhdGggZD0iTTIwIDIxdi0yYTQgNCAwIDAgMC00LTRIOGE0IDQgMCAwIDAtNCA0djIiLz48Y2lyY2xlIGN4PSIxMiIgY3k9IjciIHI9IjQiLz48L3N2Zz4=",
  userActive: "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iIzAwQUZFMSIgc3Ryb2tlPSIjMDBBRkUxIiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCI+PHBhdGggZD0iTTIwIDIxdi0yYTQgNCAwIDAgMC00LTRIOGE0IDQgMCAwIDAtNCA0djIiLz48Y2lyY2xlIGN4PSIxMiIgY3k9IjciIHI9IjQiLz48L3N2Zz4="
}

export default {
  name: 'CustomTabbar',
  props: {
    current: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      tabList: [
        { 
          icon: icons.home, 
          iconActive: icons.homeActive, 
          text: '首页', 
          path: '/pages/index/index' 
        },
        { 
          icon: icons.task, 
          iconActive: icons.taskActive, 
          text: '任务大厅', 
          path: '/pages/task/list' 
        },
        { 
          icon: icons.order, 
          iconActive: icons.orderActive, 
          text: '订单', 
          path: '/pages/order/list' 
        },
        { 
          icon: icons.user, 
          iconActive: icons.userActive, 
          text: '我的', 
          path: '/pages/user/index' 
        }
      ]
    }
  },
  computed: {
    currentIndex() {
      return this.current
    }
  },
  methods: {
    switchTab(index) {
      if (this.currentIndex === index) return
      uni.switchTab({
        url: this.tabList[index].path
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.custom-tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background: #fff;
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-shadow: 0 -2rpx 20rpx rgba(0, 0, 0, 0.05);
  padding-bottom: env(safe-area-inset-bottom);
  z-index: 999;
}

.tabbar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10rpx 0;
  
  &.active {
    .tabbar-icon {
      transform: scale(1.1);
    }
    .tabbar-text {
      color: #00AFE1;
      font-weight: 600;
    }
  }
}

.tabbar-icon {
  width: 48rpx;
  height: 48rpx;
  margin-bottom: 4rpx;
  transition: transform 0.2s;
}

.tabbar-text {
  font-size: 22rpx;
  color: #999;
  transition: color 0.2s;
}

.tabbar-item.active .tabbar-text {
  color: #00AFE1;
}
</style>
