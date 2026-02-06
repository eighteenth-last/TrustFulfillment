<template>
  <div class="min-h-screen flex">
    <!-- 左侧装饰 - 占70% -->
    <div class="hidden lg:flex w-[70%] items-center justify-center p-12 left-panel">
      <div class="text-center">
        <div class="logo-wrapper">
          <img src="/logo.png" alt="臻托" class="w-28 h-28 rounded-3xl mx-auto shadow-2xl object-cover" />
        </div>
        <h1 class="text-4xl font-black mb-4 text-white mt-10">臻托 TrustFulfillment</h1>
        <p class="text-xl text-white/80">管理后台控制中心</p>
        <div class="mt-12 flex items-center justify-center gap-6 text-sm text-white/70">
          <span class="flex items-center gap-2 bg-white/10 px-4 py-2 rounded-full">
            <i class="fas fa-shield-alt"></i>信托资金监管
          </span>
          <span class="flex items-center gap-2 bg-white/10 px-4 py-2 rounded-full">
            <i class="fas fa-balance-scale"></i>纠纷仲裁
          </span>
          <span class="flex items-center gap-2 bg-white/10 px-4 py-2 rounded-full">
            <i class="fas fa-chart-line"></i>风险监控
          </span>
        </div>
        <!-- 装饰元素 -->
        <div class="mt-16 flex justify-center gap-4">
          <div class="w-2 h-2 rounded-full bg-white/40"></div>
          <div class="w-2 h-2 rounded-full bg-white/60"></div>
          <div class="w-2 h-2 rounded-full bg-white/40"></div>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单 - 占30% -->
    <div class="flex-1 lg:w-[30%] lg:flex-none flex items-center justify-center p-8 bg-gray-50">
      <div class="w-full max-w-md">
        <div class="lg:hidden text-center mb-8">
          <img src="/logo.png" alt="臻托" class="w-16 h-16 rounded-2xl mx-auto mb-4 shadow-lg object-cover" />
          <h1 class="text-2xl font-black text-gray-800">臻托管理后台</h1>
        </div>

        <div class="bg-white p-10 rounded-3xl shadow-xl">
          <h2 class="text-2xl font-black text-gray-800 mb-2">管理员登录</h2>
          <p class="text-gray-400 mb-8">请使用管理员账号登录系统</p>

          <n-form ref="formRef" :model="formData" :rules="rules" @keydown="handleKeydown">
            <n-form-item path="username" label="用户名">
              <n-input 
                v-model:value="formData.username" 
                placeholder="请输入管理员账号"
                size="large"
                @keydown.enter="handleLogin"
              >
                <template #prefix>
                  <i class="fas fa-user text-gray-400"></i>
                </template>
              </n-input>
            </n-form-item>

            <n-form-item path="password" label="密码">
              <n-input 
                v-model:value="formData.password" 
                type="password"
                show-password-on="click"
                placeholder="请输入密码"
                size="large"
                @keydown.enter="handleLogin"
              >
                <template #prefix>
                  <i class="fas fa-lock text-gray-400"></i>
                </template>
              </n-input>
            </n-form-item>

            <n-button 
              type="primary" 
              color="#00AFE1"
              block 
              size="large"
              class="mt-4 font-bold h-12"
              :loading="loading"
              @click="handleLogin"
            >
              登录系统
            </n-button>
            <div class="mt-4 text-center text-sm text-gray-400">
              测试账号: 13272796154 / 123456
            </div>
          </n-form>

          <div class="mt-6 p-4 bg-blue-50 rounded-xl text-xs text-gray-500">
            <i class="fas fa-info-circle mr-1" style="color: #00AFE1;"></i>
            提示：仅授权管理员可登录此系统
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const formData = reactive({
  username: '',
  password: ''
})

const rules = {
  username: { required: true, message: '请输入用户名', trigger: 'blur' },
  password: { required: true, message: '请输入密码', trigger: 'blur' }
}

// 检查是否已登录
onMounted(() => {
  if (userStore.token) {
    router.replace('/admin/dashboard')
  }
})

const handleLogin = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true
    
    // 调用后端登录API
    const res = await login({
      phone: formData.username,
      password: formData.password
    })
    
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.userInfo)
    message.success('登录成功')
    
    // 跳转到之前的页面或首页
    const redirect = route.query.redirect || '/admin/dashboard'
    router.replace(redirect)
  } catch (e) {
    console.error(e)
    message.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}

// 支持回车登录
const handleKeydown = (e) => {
  if (e.key === 'Enter') {
    handleLogin()
  }
}
</script>

<style scoped>
.left-panel {
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 50%, #006080 100%);
  position: relative;
  overflow: hidden;
}

.left-panel::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  animation: rotate 20s linear infinite;
}

.left-panel::after {
  content: '';
  position: absolute;
  bottom: -30%;
  right: -30%;
  width: 60%;
  height: 60%;
  background: radial-gradient(circle, rgba(255,255,255,0.08) 0%, transparent 70%);
  border-radius: 50%;
}

.logo-wrapper {
  background: rgba(255, 255, 255, 0.15);
  padding: 16px;
  border-radius: 32px;
  display: inline-block;
  backdrop-filter: blur(10px);
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
