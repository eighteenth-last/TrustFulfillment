<template>
  <div class="max-w-4xl">
    <div class="space-y-6">
      <!-- 基本设置 -->
      <div class="bg-white rounded-2xl border border-gray-100 p-6">
        <h4 class="font-bold mb-6 flex items-center gap-2">
          <i class="fas fa-cog text-tf"></i>
          基本设置
        </h4>
        <div class="space-y-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">平台名称</p>
              <p class="text-xs text-gray-400 mt-1">显示在页面标题和Logo处</p>
            </div>
            <n-input v-model:value="settings.platformName" class="w-64" />
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">客服电话</p>
              <p class="text-xs text-gray-400 mt-1">用户咨询热线</p>
            </div>
            <n-input v-model:value="settings.servicePhone" class="w-64" />
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">客服邮箱</p>
              <p class="text-xs text-gray-400 mt-1">用户反馈邮箱</p>
            </div>
            <n-input v-model:value="settings.serviceEmail" class="w-64" />
          </div>
        </div>
      </div>

      <!-- 短信服务配置 -->
      <div class="bg-white rounded-2xl border border-gray-100 p-6">
        <h4 class="font-bold mb-6 flex items-center gap-2">
          <i class="fas fa-comment-sms text-tf"></i>
          短信服务配置
        </h4>
        <div class="space-y-6">
          <!-- 服务提供商选择 -->
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">短信服务提供商</p>
              <p class="text-xs text-gray-400 mt-1">选择短信发送通道</p>
            </div>
            <n-select
              v-model:value="smsSettings.provider"
              :options="providerOptions"
              class="w-64"
              @update:value="onProviderChange"
            />
          </div>

          <!-- 模拟模式说明 -->
          <div v-if="smsSettings.provider === 'console'" class="p-4 bg-blue-50 rounded-xl">
            <div class="flex items-start gap-3">
              <i class="fas fa-info-circle text-tf mt-0.5"></i>
              <div>
                <p class="font-medium text-gray-700">控制台模式</p>
                <p class="text-sm text-gray-500 mt-1">
                  验证码将打印到后端控制台日志中，适用于开发和测试环境。
                  生产环境请切换到阿里云或腾讯云。
                </p>
              </div>
            </div>
          </div>

          <!-- 阿里云配置 -->
          <template v-if="smsSettings.provider === 'aliyun'">
            <div class="p-4 bg-orange-50 rounded-xl mb-4">
              <div class="flex items-start gap-3">
                <i class="fab fa-alipay text-orange-500 mt-0.5"></i>
                <div>
                  <p class="font-medium text-gray-700">阿里云短信配置</p>
                  <p class="text-sm text-gray-500 mt-1">
                    请前往 <a href="https://dysms.console.aliyun.com" target="_blank" class="text-tf hover:underline">阿里云短信控制台</a> 获取相关配置
                  </p>
                </div>
              </div>
            </div>
            <div class="flex items-center justify-between">
              <div>
                <p class="font-medium">AccessKey ID</p>
                <p class="text-xs text-gray-400 mt-1">阿里云访问密钥ID</p>
              </div>
              <n-input v-model:value="smsSettings.aliyunAccessKeyId" class="w-80" placeholder="LTAI5t..." />
            </div>
            <div class="flex items-center justify-between">
              <div>
                <p class="font-medium">AccessKey Secret</p>
                <p class="text-xs text-gray-400 mt-1">阿里云访问密钥</p>
              </div>
              <n-input v-model:value="smsSettings.aliyunAccessKeySecret" type="password" show-password-on="click" class="w-80" placeholder="请输入密钥" />
            </div>
            <div class="flex items-center justify-between">
              <div>
                <p class="font-medium">短信签名</p>
                <p class="text-xs text-gray-400 mt-1">已审核通过的签名名称</p>
              </div>
              <n-input v-model:value="smsSettings.aliyunSignName" class="w-80" placeholder="臻托" />
            </div>
            <div class="flex items-center justify-between">
              <div>
                <p class="font-medium">模板代码</p>
                <p class="text-xs text-gray-400 mt-1">验证码模板ID</p>
              </div>
              <n-input v-model:value="smsSettings.aliyunTemplateCode" class="w-80" placeholder="SMS_123456789" />
            </div>
          </template>

          <!-- 腾讯云配置 -->
          <template v-if="smsSettings.provider === 'tencent'">
            <div class="p-4 bg-blue-50 rounded-xl mb-4">
              <div class="flex items-start gap-3">
                <i class="fab fa-qq text-blue-500 mt-0.5"></i>
                <div>
                  <p class="font-medium text-gray-700">腾讯云短信配置</p>
                  <p class="text-sm text-gray-500 mt-1">
                    请前往 <a href="https://console.cloud.tencent.com/smsv2" target="_blank" class="text-tf hover:underline">腾讯云短信控制台</a> 获取相关配置
                  </p>
                </div>
              </div>
            </div>
            <div class="flex items-center justify-between">
              <div>
                <p class="font-medium">SecretId</p>
                <p class="text-xs text-gray-400 mt-1">腾讯云API密钥ID</p>
              </div>
              <n-input v-model:value="smsSettings.tencentSecretId" class="w-80" placeholder="AKIDz8krbsJ5..." />
            </div>
            <div class="flex items-center justify-between">
              <div>
                <p class="font-medium">SecretKey</p>
                <p class="text-xs text-gray-400 mt-1">腾讯云API密钥</p>
              </div>
              <n-input v-model:value="smsSettings.tencentSecretKey" type="password" show-password-on="click" class="w-80" placeholder="请输入密钥" />
            </div>
            <div class="flex items-center justify-between">
              <div>
                <p class="font-medium">SDK AppID</p>
                <p class="text-xs text-gray-400 mt-1">短信应用ID</p>
              </div>
              <n-input v-model:value="smsSettings.tencentAppId" class="w-80" placeholder="1400..." />
            </div>
            <div class="flex items-center justify-between">
              <div>
                <p class="font-medium">短信签名</p>
                <p class="text-xs text-gray-400 mt-1">已审核通过的签名内容</p>
              </div>
              <n-input v-model:value="smsSettings.tencentSignName" class="w-80" placeholder="臻托" />
            </div>
            <div class="flex items-center justify-between">
              <div>
                <p class="font-medium">模板ID</p>
                <p class="text-xs text-gray-400 mt-1">验证码模板ID</p>
              </div>
              <n-input v-model:value="smsSettings.tencentTemplateId" class="w-80" placeholder="123456" />
            </div>
          </template>

          <!-- 通用设置 -->
          <div class="border-t border-gray-100 pt-6 mt-6">
            <p class="text-sm font-medium text-gray-500 mb-4">通用设置</p>
            <div class="space-y-6">
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-medium">验证码有效期</p>
                  <p class="text-xs text-gray-400 mt-1">验证码过期时间</p>
                </div>
                <n-input-number v-model:value="smsSettings.codeExpireSeconds" :min="60" :max="600" class="w-64">
                  <template #suffix>秒</template>
                </n-input-number>
              </div>
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-medium">每日发送上限</p>
                  <p class="text-xs text-gray-400 mt-1">单个手机号每日最多发送次数</p>
                </div>
                <n-input-number v-model:value="smsSettings.dayLimit" :min="1" :max="20" class="w-64">
                  <template #suffix>次</template>
                </n-input-number>
              </div>
            </div>
          </div>

          <!-- 测试短信 -->
          <div class="border-t border-gray-100 pt-6 mt-6">
            <p class="text-sm font-medium text-gray-500 mb-4">发送测试</p>
            <div class="flex items-end gap-4">
              <div class="flex-1">
                <p class="text-sm text-gray-600 mb-2">测试手机号</p>
                <n-input v-model:value="testPhone" placeholder="请输入手机号" />
              </div>
              <n-button 
                type="primary" 
                color="#00AFE1"
                :loading="testLoading"
                @click="sendTestSms"
              >
                <template #icon><i class="fas fa-paper-plane"></i></template>
                发送测试短信
              </n-button>
            </div>
          </div>

          <!-- 保存短信配置按钮 -->
          <div class="flex justify-end pt-4">
            <n-button type="primary" color="#00AFE1" @click="saveSmsSettings" :loading="smsLoading">
              <template #icon><i class="fas fa-save"></i></template>
              保存短信配置
            </n-button>
          </div>
        </div>
      </div>

      <!-- 信托设置 -->
      <div class="bg-white rounded-2xl border border-gray-100 p-6">
        <h4 class="font-bold mb-6 flex items-center gap-2">
          <i class="fas fa-vault text-tf"></i>
          信托设置
        </h4>
        <div class="space-y-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">平台服务费率</p>
              <p class="text-xs text-gray-400 mt-1">从每笔交易中收取的服务费比例</p>
            </div>
            <n-input-number v-model:value="settings.serviceFeeRate" :min="0" :max="100" :precision="2" class="w-64">
              <template #suffix>%</template>
            </n-input-number>
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">最低托管金额</p>
              <p class="text-xs text-gray-400 mt-1">单笔订单最低托管金额限制</p>
            </div>
            <n-input-number v-model:value="settings.minTrustAmount" :min="0" class="w-64">
              <template #prefix>¥</template>
            </n-input-number>
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">单笔支付上限</p>
              <p class="text-xs text-gray-400 mt-1">单次支付金额的最大限制</p>
            </div>
            <n-input-number v-model:value="settings.maxSinglePayment" :min="0" class="w-64">
              <template #prefix>¥</template>
            </n-input-number>
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">验收超时时间</p>
              <p class="text-xs text-gray-400 mt-1">甲方验收超时后自动确认</p>
            </div>
            <n-input-number v-model:value="settings.acceptTimeout" :min="1" class="w-64">
              <template #suffix>小时</template>
            </n-input-number>
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">最低提现金额</p>
              <p class="text-xs text-gray-400 mt-1">用户提现的最低金额限制</p>
            </div>
            <n-input-number v-model:value="settings.withdrawMinAmount" :min="0" class="w-64">
              <template #prefix>¥</template>
            </n-input-number>
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">提现手续费率</p>
              <p class="text-xs text-gray-400 mt-1">用户提现时收取的手续费</p>
            </div>
            <n-input-number v-model:value="settings.withdrawFeeRate" :min="0" :max="100" :precision="2" class="w-64">
              <template #suffix>%</template>
            </n-input-number>
          </div>
        </div>
      </div>

      <!-- 安全设置 -->
      <div class="bg-white rounded-2xl border border-gray-100 p-6">
        <h4 class="font-bold mb-6 flex items-center gap-2">
          <i class="fas fa-shield-alt text-tf"></i>
          安全设置
        </h4>
        <div class="space-y-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">强制双因素认证</p>
              <p class="text-xs text-gray-400 mt-1">要求所有用户启用双因素认证</p>
            </div>
            <n-switch v-model:value="settings.force2FA" />
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">登录失败锁定</p>
              <p class="text-xs text-gray-400 mt-1">连续登录失败后锁定账户</p>
            </div>
            <n-input-number v-model:value="settings.loginFailLock" :min="1" class="w-64">
              <template #suffix>次</template>
            </n-input-number>
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">会话超时时间</p>
              <p class="text-xs text-gray-400 mt-1">无操作后自动退出登录</p>
            </div>
            <n-input-number v-model:value="settings.sessionTimeout" :min="5" class="w-64">
              <template #suffix>分钟</template>
            </n-input-number>
          </div>
        </div>
      </div>

      <!-- 通知设置 -->
      <div class="bg-white rounded-2xl border border-gray-100 p-6">
        <h4 class="font-bold mb-6 flex items-center gap-2">
          <i class="fas fa-bell text-tf"></i>
          通知设置
        </h4>
        <div class="space-y-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">邮件通知</p>
              <p class="text-xs text-gray-400 mt-1">重要事件通过邮件通知管理员</p>
            </div>
            <n-switch v-model:value="settings.emailNotify" />
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">短信通知</p>
              <p class="text-xs text-gray-400 mt-1">紧急事件通过短信通知管理员</p>
            </div>
            <n-switch v-model:value="settings.smsNotify" />
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">纠纷告警</p>
              <p class="text-xs text-gray-400 mt-1">新纠纷产生时立即通知</p>
            </div>
            <n-switch v-model:value="settings.disputeAlert" />
          </div>
        </div>
      </div>

      <!-- 支付配置 -->
      <div class="bg-white rounded-2xl border border-gray-100 p-6">
        <h4 class="font-bold mb-6 flex items-center gap-2">
          <i class="fas fa-credit-card text-tf"></i>
          支付配置
        </h4>

        <!-- 支付方式激活状态 -->
        <div class="mb-6 p-4 bg-blue-50 rounded-xl">
          <div class="flex items-start gap-3">
            <i class="fas fa-info-circle text-tf mt-0.5"></i>
            <div class="flex-1">
              <p class="font-medium text-gray-700">支付方式激活状态</p>
              <p class="text-sm text-gray-500 mt-1">
                当所有支付方式都未激活时，系统将自动使用虚拟支付（测试模式）
              </p>
              <div class="flex gap-6 mt-3">
                <label class="flex items-center gap-2 cursor-pointer">
                  <n-switch v-model:value="paymentSettings.active.alipay" />
                  <span class="text-sm font-medium">支付宝</span>
                </label>
                <label class="flex items-center gap-2 cursor-pointer">
                  <n-switch v-model:value="paymentSettings.active.wechat" />
                  <span class="text-sm font-medium">微信支付</span>
                </label>
              </div>
            </div>
          </div>
        </div>

        <!-- 折叠配置区域 -->
        <n-collapse>
          <!-- 支付宝配置 -->
          <n-collapse-item title="支付宝配置" name="alipay">
            <template #header>
              <div class="flex items-center gap-2">
                <i class="fab fa-alipay text-blue-500 text-lg"></i>
                <span class="font-medium">支付宝配置</span>
              </div>
            </template>
            <div class="space-y-4">
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-medium text-sm">AppID</p>
                  <p class="text-xs text-gray-400 mt-1">支付宝应用ID</p>
                </div>
                <n-input 
                  v-model:value="paymentSettings.alipay.appId" 
                  class="w-96" 
                  placeholder="请输入支付宝AppID"
                />
              </div>
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-medium text-sm">应用私钥</p>
                  <p class="text-xs text-gray-400 mt-1">RSA2格式私钥</p>
                </div>
                <n-input 
                  v-model:value="paymentSettings.alipay.privateKey" 
                  type="textarea"
                  :autosize="{ minRows: 2, maxRows: 4 }"
                  class="w-96" 
                  placeholder="请输入应用私钥"
                />
              </div>
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-medium text-sm">支付宝公钥</p>
                  <p class="text-xs text-gray-400 mt-1">支付宝平台公钥</p>
                </div>
                <n-input 
                  v-model:value="paymentSettings.alipay.publicKey" 
                  type="textarea"
                  :autosize="{ minRows: 2, maxRows: 4 }"
                  class="w-96" 
                  placeholder="请输入支付宝公钥"
                />
              </div>
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-medium text-sm">回调地址</p>
                  <p class="text-xs text-gray-400 mt-1">异步通知URL</p>
                </div>
                <n-input 
                  v-model:value="paymentSettings.alipay.notifyUrl" 
                  class="w-96" 
                  placeholder="http://your-domain.com/api/pay/notify/alipay"
                />
              </div>
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-medium text-sm">返回地址</p>
                  <p class="text-xs text-gray-400 mt-1">支付完成后跳转URL</p>
                </div>
                <n-input 
                  v-model:value="paymentSettings.alipay.returnUrl" 
                  class="w-96" 
                  placeholder="http://your-domain.com/user/wallet"
                />
              </div>
            </div>
          </n-collapse-item>

          <!-- 微信支付配置 -->
          <n-collapse-item title="微信支付配置" name="wechat">
            <template #header>
              <div class="flex items-center gap-2">
                <i class="fab fa-weixin text-green-500 text-lg"></i>
                <span class="font-medium">微信支付配置</span>
              </div>
            </template>
            <div class="space-y-4">
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-medium text-sm">商户号</p>
                  <p class="text-xs text-gray-400 mt-1">微信支付商户号</p>
                </div>
                <n-input 
                  v-model:value="paymentSettings.wechat.mchId" 
                  class="w-96" 
                  placeholder="请输入商户号"
                />
              </div>
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-medium text-sm">AppID</p>
                  <p class="text-xs text-gray-400 mt-1">微信应用ID</p>
                </div>
                <n-input 
                  v-model:value="paymentSettings.wechat.appId" 
                  class="w-96" 
                  placeholder="请输入AppID"
                />
              </div>
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-medium text-sm">APIv3密钥</p>
                  <p class="text-xs text-gray-400 mt-1">32位字符串</p>
                </div>
                <n-input 
                  v-model:value="paymentSettings.wechat.apiV3Key" 
                  type="password"
                  show-password-on="click"
                  class="w-96" 
                  placeholder="请输入APIv3密钥"
                />
              </div>
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-medium text-sm">商户私钥</p>
                  <p class="text-xs text-gray-400 mt-1">商户API证书私钥</p>
                </div>
                <n-input 
                  v-model:value="paymentSettings.wechat.privateKey" 
                  type="textarea"
                  :autosize="{ minRows: 2, maxRows: 4 }"
                  class="w-96" 
                  placeholder="请输入商户私钥"
                />
              </div>
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-medium text-sm">证书序列号</p>
                  <p class="text-xs text-gray-400 mt-1">商户API证书序列号</p>
                </div>
                <n-input 
                  v-model:value="paymentSettings.wechat.merchantSerialNumber" 
                  class="w-96" 
                  placeholder="请输入证书序列号"
                />
              </div>
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-medium text-sm">回调地址</p>
                  <p class="text-xs text-gray-400 mt-1">异步通知URL</p>
                </div>
                <n-input 
                  v-model:value="paymentSettings.wechat.notifyUrl" 
                  class="w-96" 
                  placeholder="http://your-domain.com/api/pay/notify/wechat"
                />
              </div>
            </div>
          </n-collapse-item>
        </n-collapse>

        <!-- 保存支付配置按钮 -->
        <div class="flex justify-end pt-4 border-t border-gray-100 mt-6">
          <n-button type="primary" color="#00AFE1" @click="savePaymentSettings" :loading="paymentLoading">
            <template #icon><i class="fas fa-save"></i></template>
            保存支付配置
          </n-button>
        </div>
      </div>

      <!-- 保存按钮 -->
      <div class="flex justify-end gap-4">
        <n-button size="large" @click="resetSettings">重置</n-button>
        <n-button type="primary" color="#00AFE1" size="large" class="px-8" @click="saveSettings">
          保存设置
        </n-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { getSettings, saveSettings as apiSaveSettings, getSmsSettings, saveSmsSettings as apiSaveSmsSettings, sendTestSms as apiSendTestSms, getPayConfig, savePayConfig } from '@/api/admin'

const message = useMessage()

const loading = ref(false)
const smsLoading = ref(false)
const testLoading = ref(false)
const paymentLoading = ref(false)
const testPhone = ref('')

const settings = reactive({
  platformName: '',
  servicePhone: '',
  serviceEmail: '',
  serviceFeeRate: 3.5,
  minTrustAmount: 100,
  maxSinglePayment: 200000,
  acceptTimeout: 48,
  withdrawMinAmount: 100,
  withdrawFeeRate: 0.1,
  force2FA: false,
  loginFailLock: 5,
  sessionTimeout: 30,
  emailNotify: true,
  smsNotify: true,
  disputeAlert: true
})

// 短信服务配置
const smsSettings = reactive({
  provider: 'console',
  // 阿里云配置
  aliyunAccessKeyId: '',
  aliyunAccessKeySecret: '',
  aliyunSignName: '臻托',
  aliyunTemplateCode: '',
  // 腾讯云配置
  tencentSecretId: '',
  tencentSecretKey: '',
  tencentAppId: '',
  tencentSignName: '臻托',
  tencentTemplateId: '',
  // 通用配置
  codeExpireSeconds: 300,
  dayLimit: 10
})

// 支付配置
const paymentSettings = reactive({
  active: {
    alipay: false,
    wechat: false
  },
  alipay: {
    enabled: false,
    appId: '',
    privateKey: '',
    publicKey: '',
    signType: 'RSA2',
    gatewayUrl: 'https://openapi.alipay.com/gateway.do',
    notifyUrl: 'http://localhost:8080/api/pay/notify/alipay',
    returnUrl: 'http://localhost:3001/user/wallet'
  },
  wechat: {
    enabled: false,
    mchId: '',
    appId: '',
    apiV3Key: '',
    privateKey: '',
    merchantSerialNumber: '',
    notifyUrl: 'http://localhost:8080/api/pay/notify/wechat'
  }
})

// 短信服务提供商选项
const providerOptions = [
  { label: '控制台模式（开发测试）', value: 'console' },
  { label: '阿里云短信', value: 'aliyun' },
  { label: '腾讯云短信', value: 'tencent' }
]

// 切换提供商时的处理
const onProviderChange = (value) => {
  console.log('短信服务提供商切换为:', value)
}

// 加载设置
const loadSettings = async () => {
  loading.value = true
  try {
    const res = await getSettings()
    if (res.data) {
      Object.keys(settings).forEach(key => {
        if (res.data[key] !== undefined) {
          settings[key] = res.data[key]
        }
      })
    }
  } catch (e) {
    console.error('加载设置失败', e)
  } finally {
    loading.value = false
  }
}

// 加载短信配置
const loadSmsSettings = async () => {
  try {
    const res = await getSmsSettings()
    if (res.data) {
      Object.keys(smsSettings).forEach(key => {
        if (res.data[key] !== undefined) {
          smsSettings[key] = res.data[key]
        }
      })
    }
  } catch (e) {
    console.error('加载短信配置失败', e)
  }
}

// 加载支付配置
const loadPaymentSettings = async () => {
  try {
    const res = await getPayConfig()
    if (res.data) {
      if (res.data.alipay) {
        Object.assign(paymentSettings.alipay, res.data.alipay)
      }
      if (res.data.wechat) {
        Object.assign(paymentSettings.wechat, res.data.wechat)
      }
      if (res.data.active) {
        Object.assign(paymentSettings.active, res.data.active)
      }
    }
  } catch (e) {
    console.error('加载支付配置失败', e)
  }
}

const saveSettings = async () => {
  try {
    await apiSaveSettings({ ...settings })
    message.success('设置已保存')
  } catch (e) {
    message.error('保存失败')
  }
}

// 保存短信配置
const saveSmsSettings = async () => {
  smsLoading.value = true
  try {
    await apiSaveSmsSettings({ ...smsSettings })
    message.success('短信配置已保存')
  } catch (e) {
    message.error('保存失败: ' + (e.message || '未知错误'))
  } finally {
    smsLoading.value = false
  }
}

// 保存支付配置
const savePaymentSettings = async () => {
  paymentLoading.value = true
  try {
    // 同步 enabled 状态
    paymentSettings.alipay.enabled = paymentSettings.active.alipay
    paymentSettings.wechat.enabled = paymentSettings.active.wechat
    
    await savePayConfig({
      alipay: paymentSettings.alipay,
      wechat: paymentSettings.wechat,
      active: paymentSettings.active
    })
    message.success('支付配置已保存')
  } catch (e) {
    message.error('保存失败: ' + (e.message || '未知错误'))
  } finally {
    paymentLoading.value = false
  }
}

// 发送测试短信
const sendTestSms = async () => {
  if (!testPhone.value) {
    message.warning('请输入测试手机号')
    return
  }
  
  // 简单的手机号格式验证
  if (!/^1[3-9]\d{9}$/.test(testPhone.value)) {
    message.warning('请输入正确的手机号格式')
    return
  }
  
  testLoading.value = true
  try {
    const res = await apiSendTestSms(testPhone.value)
    if (smsSettings.provider === 'console') {
      message.success('测试短信已发送，请查看后端控制台日志')
    } else {
      message.success('测试短信已发送到 ' + testPhone.value)
    }
  } catch (e) {
    message.error('发送失败: ' + (e.message || '未知错误'))
  } finally {
    testLoading.value = false
  }
}

const resetSettings = () => {
  loadSettings()
  loadSmsSettings()
  loadPaymentSettings()
  message.info('已重置为服务器设置')
}

onMounted(() => {
  loadSettings()
  loadSmsSettings()
  loadPaymentSettings()
})
</script>
