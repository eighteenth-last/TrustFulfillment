<template>
  <div class="min-h-screen flex">
    <!-- 左侧装饰 - 占70% -->
    <div class="hidden lg:flex w-[70%] items-center justify-center p-12 left-panel">
      <div class="text-center">
        <div class="logo-wrapper">
          <img src="/logo.png" alt="臻托" class="w-28 h-28 rounded-3xl mx-auto shadow-2xl object-cover" />
        </div>
        <h1 class="text-4xl font-black mb-4 text-white mt-10">臻托 TrustFulfillment</h1>
        <p class="text-xl text-white/80">让每一笔交易都值得托付</p>
        <div class="mt-12 flex items-center justify-center gap-6 text-sm text-white/70">
          <span class="flex items-center gap-2 bg-white/10 px-4 py-2 rounded-full">
            <i class="fas fa-shield-alt"></i>资金托管
          </span>
          <span class="flex items-center gap-2 bg-white/10 px-4 py-2 rounded-full">
            <i class="fas fa-file-contract"></i>电子合同
          </span>
          <span class="flex items-center gap-2 bg-white/10 px-4 py-2 rounded-full">
            <i class="fas fa-balance-scale"></i>纠纷仲裁
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

    <!-- 右侧登录/注册表单 - 占30% -->
    <div class="flex-1 lg:w-[30%] lg:flex-none flex items-center justify-center p-8 bg-gray-50">
      <div class="w-full max-w-md">
        <div class="lg:hidden text-center mb-8">
          <img src="/logo.png" alt="臻托" class="w-16 h-16 rounded-2xl mx-auto mb-4 shadow-lg object-cover" />
          <h1 class="text-2xl font-black text-gray-800">臻托 TrustFulfillment</h1>
        </div>

        <div class="bg-white p-10 rounded-3xl shadow-xl">
          <n-tabs default-value="login" size="large" justify-content="space-evenly" type="segment">
            <n-tab-pane name="login" tab="登录">
              <n-form ref="loginFormRef" :model="loginModel" :rules="loginRules" class="mt-6">
                <n-form-item path="phone" label="手机号">
                  <n-input v-model:value="loginModel.phone" placeholder="请输入手机号" size="large" @keydown.enter="handleLogin">
                    <template #prefix><i class="fas fa-mobile-alt text-gray-400"></i></template>
                  </n-input>
                </n-form-item>
                <n-form-item path="password" label="密码">
                  <n-input
                    v-model:value="loginModel.password"
                    type="password"
                    show-password-on="click"
                    placeholder="请输入密码"
                    size="large"
                    @keydown.enter="handleLogin"
                  >
                    <template #prefix><i class="fas fa-lock text-gray-400"></i></template>
                  </n-input>
                </n-form-item>
                <n-button 
                  type="primary" 
                  block 
                  size="large" 
                  color="#00AFE1" 
                  class="mt-4 font-bold h-12"
                  @click="handleLogin" 
                  :loading="loading"
                >
                  立即登录
                </n-button>
              </n-form>
            </n-tab-pane>

            <n-tab-pane name="register" tab="注册">
              <n-form ref="regFormRef" :model="regModel" :rules="regRules" class="mt-6">
                <n-form-item path="phone" label="手机号">
                  <n-input v-model:value="regModel.phone" placeholder="请输入手机号" size="large">
                    <template #prefix><i class="fas fa-mobile-alt text-gray-400"></i></template>
                  </n-input>
                </n-form-item>
                <n-form-item path="nickname" label="昵称/企业名">
                  <n-input v-model:value="regModel.nickname" placeholder="显示名称" size="large">
                    <template #prefix><i class="fas fa-id-card text-gray-400"></i></template>
                  </n-input>
                </n-form-item>
                <n-form-item path="password" label="密码">
                  <n-input
                    v-model:value="regModel.password"
                    type="password"
                    show-password-on="click"
                    placeholder="设置密码"
                    size="large"
                  >
                    <template #prefix><i class="fas fa-lock text-gray-400"></i></template>
                  </n-input>
                </n-form-item>

                <n-button type="primary" block size="large" color="#00AFE1" class="mt-4 font-bold h-12" @click="handleRegister" :loading="loading">
                  注册账号
                </n-button>
              </n-form>
            </n-tab-pane>
          </n-tabs>

          <div class="mt-6 text-center">
            <p class="text-xs text-gray-400">
              登录即代表同意 <a href="#" class="link-primary hover:underline">《用户协议》</a> 和 <a href="#" class="link-primary hover:underline">《隐私政策》</a>
            </p>
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
import { login, register } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const userStore = useUserStore()
const loading = ref(false)

// 检查是否已登录
onMounted(() => {
  if (userStore.token) {
    const role = userStore.userInfo.role || userStore.userInfo.type
    if (role === 2 || role === 'merchant') {
      router.replace('/merchant/dashboard')
    } else {
      router.replace('/user/dashboard')
    }
  }
})

// 根据角色跳转
const redirectByRole = (userInfo) => {
  const redirect = route.query.redirect
  if (redirect) {
    router.replace(redirect)
    return
  }
  
  const role = userInfo.role || userInfo.type
  if (role === 2 || role === 'merchant') {
    router.replace('/merchant/dashboard')
  } else {
    router.replace('/user/dashboard')
  }
}

// 登录逻辑
const loginFormRef = ref(null)
const loginModel = reactive({ phone: '', password: '' })
const loginRules = {
  phone: { required: true, message: '请输入手机号', trigger: 'blur' },
  password: { required: true, message: '请输入密码', trigger: 'blur' }
}

const handleLogin = (e) => {
  if (e) e.preventDefault()
  loginFormRef.value?.validate((errors) => {
    if (!errors) {
      loading.value = true
      login(loginModel).then(res => {
        userStore.setToken(res.data.token)
        userStore.setUserInfo(res.data.userInfo)
        message.success('登录成功')
        redirectByRole(res.data.userInfo)
      }).catch(err => {
        message.error(err.message || '登录失败')
      }).finally(() => {
        loading.value = false
      })
    }
  })
}

// 注册逻辑
const regFormRef = ref(null)
const regModel = reactive({ phone: '', password: '', nickname: '', role: 'user' })
const regRules = {
  phone: { required: true, message: '请输入手机号', trigger: 'blur' },
  nickname: { required: true, message: '请输入昵称', trigger: 'blur' },
  password: { required: true, message: '请输入密码', trigger: 'blur' }
}

const handleRegister = (e) => {
  if (e) e.preventDefault()
  regFormRef.value?.validate((errors) => {
    if (!errors) {
      loading.value = true
      register(regModel).then(() => {
        message.success('注册成功，正在登录...')
        // 注册成功后自动登录
        login({ phone: regModel.phone, password: regModel.password }).then(res => {
          userStore.setToken(res.data.token)
          userStore.setUserInfo(res.data.userInfo)
          message.success('登录成功')
          redirectByRole(res.data.userInfo)
        }).catch(err => {
          message.error('自动登录失败，请手动登录')
          loginModel.phone = regModel.phone
          loginModel.password = regModel.password
          activeTab.value = 'login'
        }).finally(() => {
          loading.value = false
        })
      }).catch(err => {
        message.error(err.message || '注册失败')
        loading.value = false
      })
    }
  })
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

.link-primary {
  color: #00AFE1;
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
