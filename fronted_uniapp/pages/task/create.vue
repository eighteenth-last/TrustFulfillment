<template>
  <view class="page">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="navbar-content">
        <view class="navbar-left" @click="goBack">
          <text class="back-icon">←</text>
        </view>
        <view class="navbar-title">发布任务</view>
        <view class="navbar-right"></view>
      </view>
    </view>

    <scroll-view scroll-y class="content-scroll">
      <!-- 步骤指示器 -->
      <view class="steps">
        <view class="step" :class="{ active: currentStep >= 1 }">
          <view class="step-number">1</view>
          <text class="step-text">项目需求</text>
        </view>
        <view class="step-line" :class="{ active: currentStep >= 2 }"></view>
        <view class="step" :class="{ active: currentStep >= 2 }">
          <view class="step-number">2</view>
          <text class="step-text">资金托管</text>
        </view>
        <view class="step-line" :class="{ active: currentStep >= 3 }"></view>
        <view class="step" :class="{ active: currentStep >= 3 }">
          <view class="step-number">3</view>
          <text class="step-text">确认发布</text>
        </view>
      </view>

      <!-- 第一步：项目需求 -->
      <view v-if="currentStep === 1" class="step-content">
        <view class="section">
          <view class="section-title">项目基本信息</view>
          
          <view class="form-item">
            <view class="form-label required">项目名称</view>
            <input 
              class="form-input" 
              v-model="formData.title" 
              placeholder="例如：跨境支付系统前端开发"
              placeholder-class="placeholder"
            />
          </view>

          <view class="form-item">
            <view class="form-label required">项目分类</view>
            <picker 
              mode="multiSelector" 
              :range="categoryColumns" 
              range-key="name"
              :value="categoryIndexes"
              @change="onCategoryChange"
              @columnchange="onCategoryColumnChange"
            >
              <view class="form-picker" :class="{ placeholder: !formData.categoryName }">
                {{ formData.categoryName || '请选择项目分类' }}
              </view>
            </picker>
            <view class="form-tip">三列分别选择：一级分类、二级分类、三级分类</view>
          </view>

          <view class="form-item">
            <view class="form-label">技术要求</view>
            <input 
              class="form-input" 
              v-model="formData.techStack" 
              placeholder="例如：Vue3, TypeScript, Node.js"
              placeholder-class="placeholder"
            />
          </view>

          <view class="form-item">
            <view class="form-label required">需求简述</view>
            <textarea 
              class="form-textarea" 
              v-model="formData.description" 
              placeholder="简要描述项目背景和核心需求..."
              placeholder-class="placeholder"
              :maxlength="500"
            />
            <view class="char-count">{{ formData.description.length }}/500</view>
          </view>

          <view class="form-item">
            <view class="form-label">详细需求</view>
            <textarea 
              class="form-textarea large" 
              v-model="formData.features" 
              placeholder="详细描述功能模块、交付标准等...&#10;&#10;例如：&#10;1. 用户登录注册模块&#10;2. 商品列表展示&#10;3. 购物车功能"
              placeholder-class="placeholder"
              :maxlength="2000"
            />
            <view class="char-count">{{ formData.features.length }}/2000</view>
          </view>

          <view class="form-item">
            <view class="form-label">开发文档</view>
            <view class="upload-list">
              <view class="upload-item" v-for="(file, index) in uploadedFiles" :key="index">
                <image v-if="isImage(file.name)" class="upload-preview" :src="file.url" mode="aspectFill" />
                <view v-else class="upload-file">
                  <text class="iconfont icon-file"></text>
                  <text class="file-name">{{ file.name }}</text>
                </view>
                <view class="upload-delete" @click="removeFile(index)">
                  <text class="iconfont icon-close"></text>
                </view>
              </view>
              <view class="upload-btn" @click="chooseFile" v-if="uploadedFiles.length < 9">
                <text class="iconfont icon-plus"></text>
                <text class="upload-text">上传文件</text>
              </view>
            </view>
            <view class="form-tip">支持图片、PDF、Word等，最多9个文件</view>
          </view>

          <view class="form-row">
            <view class="form-item half">
              <view class="form-label required">开始时间</view>
              <picker mode="date" :value="formData.startTime" @change="onStartTimeChange">
                <view class="form-picker" :class="{ placeholder: !formData.startTime }">
                  {{ formData.startTime || '选择日期' }}
                </view>
              </picker>
            </view>
            <view class="form-item half">
              <view class="form-label required">交付日期</view>
              <picker mode="date" :value="formData.deliveryTime" @change="onDeliveryTimeChange">
                <view class="form-picker" :class="{ placeholder: !formData.deliveryTime }">
                  {{ formData.deliveryTime || '选择日期' }}
                </view>
              </picker>
            </view>
          </view>
        </view>

        <view class="btn-group">
          <button class="btn btn-primary" @click="nextStep">下一步</button>
        </view>
      </view>

      <!-- 第二步：资金托管 -->
      <view v-if="currentStep === 2" class="step-content">
        <view class="section">
          <view class="section-title">选择付款方案</view>
          <view class="plan-list">
            <view 
              class="plan-card" 
              v-for="plan in paymentPlans" 
              :key="plan.id"
              :class="{ active: selectedPlanId === plan.id }"
              @click="selectPlan(plan)"
            >
              <view class="plan-header">
                <text class="plan-name">{{ plan.name }}</text>
                <view class="plan-check" v-if="selectedPlanId === plan.id">
                  <text class="iconfont icon-check"></text>
                </view>
              </view>
              <view class="plan-desc">{{ plan.desc }}</view>
            </view>
          </view>
        </view>

        <view class="section">
          <view class="section-title">项目总预算</view>
          <view class="budget-input">
            <text class="budget-symbol">¥</text>
            <input 
              class="budget-value" 
              type="digit" 
              v-model="totalBudget" 
              placeholder="请输入金额"
              @input="onBudgetChange"
            />
          </view>
          <view class="form-tip">最低100元，平台将收取5%服务费</view>
        </view>

        <view class="section">
          <view class="section-title">付款阶段</view>
          <view class="stage-list">
            <view class="stage-card" v-for="(stage, index) in formData.stages" :key="index">
              <view class="stage-header">
                <view class="stage-number" :style="{ background: getStageColor(index) }">
                  {{ index + 1 }}
                </view>
                <input 
                  class="stage-name" 
                  v-model="stage.name" 
                  placeholder="阶段名称"
                />
                <view class="stage-delete" v-if="formData.stages.length > 2" @click="removeStage(index)">
                  <text class="iconfont icon-delete"></text>
                </view>
              </view>
              
              <view class="stage-amount">
                <view class="amount-input">
                  <input 
                    type="digit" 
                    v-model="stage.percent" 
                    @input="updateStageAmount(index)"
                  />
                  <text>%</text>
                </view>
                <text class="amount-value">¥{{ stage.amount.toLocaleString() }}</text>
              </view>

              <view class="stage-field">
                <text class="field-label">付款节点</text>
                <input 
                  class="field-input" 
                  v-model="stage.milestone" 
                  placeholder="如：合同签订当日"
                />
              </view>

              <view class="stage-field">
                <text class="field-label">风险说明</text>
                <textarea 
                  class="field-textarea" 
                  v-model="stage.riskNote" 
                  placeholder="如：客户跑路可覆盖30%人力成本"
                  :maxlength="200"
                />
              </view>
            </view>
          </view>

          <view class="add-stage-btn" v-if="formData.stages.length < 5" @click="addStage">
            <text class="iconfont icon-plus"></text>
            <text>添加结算节点</text>
          </view>

          <view class="percent-warning" v-if="totalPercent !== 100">
            <text class="iconfont icon-warning"></text>
            <text>当前比例合计 {{ totalPercent }}%，需等于 100%</text>
          </view>
        </view>

        <view class="info-box">
          <view class="info-title">
            <text class="iconfont icon-info"></text>
            <text>分期付款方案说明</text>
          </view>
          <view class="info-item">• 352方案：30%定金 + 50%中期款 + 20%尾款</view>
          <view class="info-item">• 37方案：30%定金 + 70%尾款（无质保）</view>
          <view class="info-item">• 442方案：40%定金 + 40%中期款 + 20%尾款</view>
        </view>

        <view class="btn-group">
          <button class="btn btn-default" @click="prevStep">上一步</button>
          <button class="btn btn-primary" @click="nextStep">下一步</button>
        </view>
      </view>

      <!-- 第三步：确认发布 -->
      <view v-if="currentStep === 3" class="step-content">
        <view class="section">
          <view class="section-title">项目信息</view>
          <view class="confirm-item">
            <text class="confirm-label">项目名称</text>
            <text class="confirm-value">{{ formData.title }}</text>
          </view>
          <view class="confirm-item">
            <text class="confirm-label">项目分类</text>
            <text class="confirm-value">{{ formData.categoryName }}</text>
          </view>
          <view class="confirm-item">
            <text class="confirm-label">技术要求</text>
            <text class="confirm-value">{{ formData.techStack || '无' }}</text>
          </view>
          <view class="confirm-item">
            <text class="confirm-label">项目周期</text>
            <text class="confirm-value">{{ formData.startTime }} 至 {{ formData.deliveryTime }}</text>
          </view>
        </view>

        <view class="section">
          <view class="section-title">资金托管</view>
          <view class="confirm-budget">
            <text class="budget-label">托管总金额</text>
            <text class="budget-amount">¥{{ totalAmount.toLocaleString() }}</text>
          </view>
          <view class="stage-summary">
            <view class="summary-item" v-for="(stage, index) in formData.stages" :key="index">
              <view class="summary-dot" :style="{ background: getStageColor(index) }"></view>
              <text class="summary-name">{{ stage.name }}</text>
              <text class="summary-percent">{{ stage.percent }}%</text>
              <text class="summary-amount">¥{{ stage.amount.toLocaleString() }}</text>
            </view>
          </view>
        </view>

        <view class="warning-box">
          <text class="iconfont icon-shield"></text>
          <text class="warning-text">任务发布后等待商家接单。商家接单后，您需要进行资金托管，订单才正式开始执行。</text>
        </view>

        <view class="btn-group">
          <button class="btn btn-default" @click="prevStep">上一步</button>
          <button class="btn btn-primary" :loading="submitting" @click="handleSubmit">
            {{ submitting ? '发布中...' : '确认发布' }}
          </button>
        </view>
      </view>

      <!-- 底部安全距离 -->
      <view style="height: 40rpx;"></view>
    </scroll-view>
  </view>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { request } from '@/utils/request'
import { getBusinessCategories } from '@/utils/api'

export default {
  data() {
    return {
      statusBarHeight: 0,
      currentStep: 1,
      submitting: false,
      uploadedFiles: [],
      totalBudget: 10000,
      selectedPlanId: '352',
      
      // 分类相关 - 三列选择器
      categoryTree: [],                    // 原始树形数据
      categoryColumns: [[], [], []],       // 三列数据 [一级, 二级, 三级]
      categoryIndexes: [0, 0, 0],          // 当前选中的索引 [一级索引, 二级索引, 三级索引]
      
      paymentPlans: [
        {
          id: '352',
          name: '352方案（推荐）',
          desc: '30%定金 + 50%中期款 + 20%质保款',
          stages: [
            { name: '首付款', percent: 30, milestone: '合同签订后托管', riskNote: '覆盖30%人力成本', stageType: 1 },
            { name: '尾款', percent: 50, milestone: '项目验收通过后支付', riskNote: '验收通过后释放', stageType: 2 },
            { name: '质保款', percent: 20, milestone: '质保期15天后释放', riskNote: '15天免费维护保障', stageType: 3 }
          ]
        },
        {
          id: '37',
          name: '37方案',
          desc: '30%定金 + 70%尾款（无质保）',
          stages: [
            { name: '首付款', percent: 30, milestone: '合同签订后托管', riskNote: '覆盖30%人力成本', stageType: 1 },
            { name: '尾款', percent: 70, milestone: '项目验收通过后支付', riskNote: '验收通过后一次性结清', stageType: 2 }
          ]
        },
        {
          id: '442',
          name: '442方案',
          desc: '40%定金 + 40%中期款 + 20%质保款',
          stages: [
            { name: '首付款', percent: 40, milestone: '合同签订后托管', riskNote: '较高首付降低风险', stageType: 1 },
            { name: '尾款', percent: 40, milestone: '项目验收通过后支付', riskNote: '验收通过后支付', stageType: 2 },
            { name: '质保款', percent: 20, milestone: '质保期15天后释放', riskNote: '15天免费维护保障', stageType: 3 }
          ]
        },
        {
          id: 'custom',
          name: '自定义方案',
          desc: '自由设置付款比例和节点',
          stages: [
            { name: '首付款', percent: 50, milestone: '合同签订后托管', riskNote: '', stageType: 1 },
            { name: '尾款', percent: 50, milestone: '项目验收通过后支付', riskNote: '', stageType: 2 }
          ]
        }
      ],
      
      formData: {
        title: '',
        categoryId: null,
        categoryName: '',
        techStack: '',
        description: '',
        features: '',
        startTime: '',
        deliveryTime: '',
        stages: []
      }
    }
  },
  
  computed: {
    totalAmount() {
      return this.formData.stages.reduce((sum, stage) => sum + (stage.amount || 0), 0)
    },
    totalPercent() {
      return this.formData.stages.reduce((sum, stage) => sum + (stage.percent || 0), 0)
    }
  },
  
  onLoad() {
    const systemInfo = uni.getSystemInfoSync()
    this.statusBarHeight = systemInfo.statusBarHeight || 0
    
    // 初始化默认方案
    this.selectPlan(this.paymentPlans[0])
    
    // 加载分类数据
    this.loadCategories()
  },
  
  methods: {
    async loadCategories() {
      try {
        const res = await getBusinessCategories()
        console.log('分类数据:', res.data)
        
        if (res.data && res.data.length > 0) {
          this.categoryTree = res.data
          
          // 第一列：一级分类
          const col1 = res.data.map(item => ({
            id: item.id,
            name: item.name,
            children: item.children || []
          }))
          
          // 第二列：第一个一级分类的二级分类
          let col2 = []
          if (col1.length > 0 && col1[0].children.length > 0) {
            col2 = col1[0].children.map(item => ({
              id: item.id,
              name: item.name,
              children: item.children || []
            }))
          }
          
          // 第三列：第一个二级分类的三级分类
          let col3 = []
          if (col2.length > 0 && col2[0].children && col2[0].children.length > 0) {
            col3 = col2[0].children.map(item => ({
              id: item.id,
              name: item.name
            }))
          }
          
          // 设置三列数据
          this.categoryColumns = [col1, col2, col3]
          // 不设置默认选中，让用户自己选择
          this.categoryIndexes = [0, 0, 0]
          
          console.log('分类列数据:', this.categoryColumns)
        }
      } catch (e) {
        console.error('加载分类失败', e)
        // 使用默认分类
        this.categoryColumns = [
          [
            { id: 1, name: 'IT/互联网', children: [] },
            { id: 2, name: '设计创意', children: [] }
          ],
          [
            { id: 101, name: '后端开发', children: [] },
            { id: 102, name: '前端开发', children: [] }
          ],
          [
            { id: 1011, name: 'Java后端开发' },
            { id: 1012, name: 'Python后端开发' }
          ]
        ]
        this.categoryIndexes = [0, 0, 0]
      }
    },
    goBack() {
      if (this.currentStep > 1) {
        this.currentStep--
      } else {
        uni.navigateBack()
      }
    },
    
    // 更新分类名称和ID
    updateCategoryName() {
      const [idx1, idx2, idx3] = this.categoryIndexes
      const col1 = this.categoryColumns[0]
      const col2 = this.categoryColumns[1]
      const col3 = this.categoryColumns[2]
      
      // 检查是否有有效的分类数据
      if (!col1 || col1.length === 0) {
        this.formData.categoryName = ''
        this.formData.categoryId = null
        return
      }
      
      if (col1[idx1] && col2[idx2] && col3[idx3]) {
        // 三级分类都存在
        this.formData.categoryName = `${col1[idx1].name} > ${col2[idx2].name} > ${col3[idx3].name}`
        this.formData.categoryId = col3[idx3].id
      } else if (col1[idx1] && col2[idx2]) {
        // 只有二级分类
        this.formData.categoryName = `${col1[idx1].name} > ${col2[idx2].name}`
        this.formData.categoryId = col2[idx2].id
      } else if (col1[idx1]) {
        // 只有一级分类
        this.formData.categoryName = col1[idx1].name
        this.formData.categoryId = col1[idx1].id
      } else {
        // 没有选择任何分类
        this.formData.categoryName = ''
        this.formData.categoryId = null
      }
      
      console.log('当前选中:', this.formData.categoryName, this.formData.categoryId)
    },
    
    // 分类列变化（用户滑动某一列）
    onCategoryColumnChange(e) {
      const column = e.detail.column  // 第几列改变了 (0, 1, 2)
      const index = e.detail.value    // 改变列的新索引
      
      console.log('列变化:', column, '新索引:', index)
      
      if (column === 0) {
        // 第一列（一级分类）改变，更新第二列和第三列
        const level1 = this.categoryColumns[0][index]
        
        if (level1 && level1.children && level1.children.length > 0) {
          // 更新第二列
          const newCol2 = level1.children.map(item => ({
            id: item.id,
            name: item.name,
            children: item.children || []
          }))
          
          // 更新第三列（使用新第二列的第一项的子分类）
          let newCol3 = []
          if (newCol2[0] && newCol2[0].children && newCol2[0].children.length > 0) {
            newCol3 = newCol2[0].children.map(item => ({
              id: item.id,
              name: item.name
            }))
          }
          
          // 更新数据（必须创建新数组引用）
          this.categoryColumns = [
            this.categoryColumns[0],
            newCol2,
            newCol3
          ]
          this.categoryIndexes = [index, 0, 0]
        }
      } else if (column === 1) {
        // 第二列（二级分类）改变，更新第三列
        const level2 = this.categoryColumns[1][index]
        
        if (level2 && level2.children && level2.children.length > 0) {
          const newCol3 = level2.children.map(item => ({
            id: item.id,
            name: item.name
          }))
          
          this.categoryColumns = [
            this.categoryColumns[0],
            this.categoryColumns[1],
            newCol3
          ]
          this.categoryIndexes = [this.categoryIndexes[0], index, 0]
        } else {
          // 没有三级分类
          this.categoryColumns = [
            this.categoryColumns[0],
            this.categoryColumns[1],
            []
          ]
          this.categoryIndexes = [this.categoryIndexes[0], index, 0]
        }
      } else if (column === 2) {
        // 第三列改变
        this.categoryIndexes = [this.categoryIndexes[0], this.categoryIndexes[1], index]
      }
    },
    
    // 分类选择确认
    onCategoryChange(e) {
      this.categoryIndexes = e.detail.value
      this.updateCategoryName()
    },
    
    onStartTimeChange(e) {
      this.formData.startTime = e.detail.value
    },
    
    onDeliveryTimeChange(e) {
      this.formData.deliveryTime = e.detail.value
    },
    
    chooseFile() {
      uni.chooseMessageFile({
        count: 9 - this.uploadedFiles.length,
        type: 'all',
        success: async (res) => {
          const files = res.tempFiles
          for (let file of files) {
            await this.uploadFile(file)
          }
        }
      })
    },
    
    async uploadFile(file) {
      uni.showLoading({ title: '上传中...' })
      try {
        const token = uni.getStorageSync('token')
        const uploadTask = uni.uploadFile({
          url: 'http://localhost:8080/api/file/upload',
          filePath: file.path,
          name: 'file',
          header: {
            'Authorization': token
          },
          formData: {
            type: 'docs'
          },
          success: (res) => {
            const data = JSON.parse(res.data)
            if (data.code === 200) {
              this.uploadedFiles.push({
                name: file.name,
                url: data.data.url
              })
              uni.showToast({ title: '上传成功', icon: 'success' })
            }
          }
        })
      } catch (e) {
        uni.showToast({ title: '上传失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    },
    
    removeFile(index) {
      this.uploadedFiles.splice(index, 1)
    },
    
    isImage(filename) {
      const ext = filename.split('.').pop().toLowerCase()
      return ['jpg', 'jpeg', 'png', 'gif'].includes(ext)
    },
    
    selectPlan(plan) {
      this.selectedPlanId = plan.id
      this.formData.stages = JSON.parse(JSON.stringify(plan.stages.map(s => ({
        ...s,
        amount: Math.round(this.totalBudget * s.percent / 100)
      }))))
    },
    
    onBudgetChange() {
      this.recalculateStages()
    },
    
    recalculateStages() {
      this.formData.stages.forEach(stage => {
        stage.amount = Math.round(this.totalBudget * stage.percent / 100)
      })
    },
    
    updateStageAmount(index) {
      this.formData.stages[index].amount = Math.round(this.totalBudget * this.formData.stages[index].percent / 100)
    },
    
    addStage() {
      const remainingPercent = 100 - this.totalPercent
      this.formData.stages.push({
        name: `阶段${this.formData.stages.length + 1}`,
        percent: Math.max(5, remainingPercent),
        amount: Math.round(this.totalBudget * Math.max(5, remainingPercent) / 100),
        milestone: '',
        riskNote: '',
        stageType: 2
      })
      this.selectedPlanId = 'custom'
    },
    
    removeStage(index) {
      this.formData.stages.splice(index, 1)
      this.selectedPlanId = 'custom'
    },
    
    getStageColor(index) {
      const colors = ['#00AFE1', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6']
      return colors[index % colors.length]
    },
    
    nextStep() {
      if (this.currentStep === 1) {
        if (!this.validateStep1()) return
      } else if (this.currentStep === 2) {
        if (!this.validateStep2()) return
      }
      this.currentStep++
    },
    
    prevStep() {
      this.currentStep--
    },
    
    validateStep1() {
      if (!this.formData.title) {
        uni.showToast({ title: '请输入项目名称', icon: 'none' })
        return false
      }
      if (!this.formData.categoryId) {
        uni.showToast({ title: '请选择项目分类', icon: 'none' })
        return false
      }
      if (!this.formData.description) {
        uni.showToast({ title: '请输入需求简述', icon: 'none' })
        return false
      }
      if (!this.formData.startTime) {
        uni.showToast({ title: '请选择开始时间', icon: 'none' })
        return false
      }
      if (!this.formData.deliveryTime) {
        uni.showToast({ title: '请选择交付日期', icon: 'none' })
        return false
      }
      return true
    },
    
    validateStep2() {
      if (!this.totalBudget || this.totalBudget < 100) {
        uni.showToast({ title: '项目预算最低100元', icon: 'none' })
        return false
      }
      if (this.formData.stages.some(s => !s.name || !s.amount)) {
        uni.showToast({ title: '请完善所有阶段信息', icon: 'none' })
        return false
      }
      if (this.totalPercent !== 100) {
        uni.showToast({ title: `付款比例需等于100%，当前${this.totalPercent}%`, icon: 'none' })
        return false
      }
      return true
    },
    
    async handleSubmit() {
      this.submitting = true
      try {
        const submitData = {
          title: this.formData.title,
          categoryId: this.formData.categoryId,
          techStack: this.formData.techStack,
          description: this.formData.description,
          features: this.formData.features,
          docUrls: this.uploadedFiles.map(f => f.url),
          startTime: this.formData.startTime,
          deliveryTime: this.formData.deliveryTime,
          stages: this.formData.stages.map(s => ({
            name: s.name,
            amount: s.amount,
            percent: s.percent,
            milestone: s.milestone,
            riskNote: s.riskNote,
            stageType: s.stageType || 2
          }))
        }
        
        const res = await request({
          url: '/order/create',
          method: 'POST',
          data: submitData
        })
        
        uni.showToast({ title: '发布成功', icon: 'success' })
        setTimeout(() => {
          uni.reLaunch({ url: '/pages/index/index' })
        }, 1500)
      } catch (e) {
        uni.showToast({ title: e.message || '发布失败', icon: 'none' })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f7fa;
}

.custom-navbar {
  background: #fff;
  border-bottom: 1rpx solid #eee;
}

.navbar-content {
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32rpx;
}

.navbar-left, .navbar-right {
  width: 80rpx;
}

.navbar-left {
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 48rpx;
  color: #333;
  font-weight: 300;
  line-height: 1;
}

.navbar-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.content-scroll {
  height: calc(100vh - 88rpx);
}

/* 步骤指示器 */
.steps {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  margin: 32rpx 32rpx 32rpx 32rpx;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}

.step-number {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: #e5e7eb;
  color: #9ca3af;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: 600;
  transition: all 0.3s;
}

.step.active .step-number {
  background: #00AFE1;
  color: #fff;
}

.step-text {
  font-size: 24rpx;
  color: #9ca3af;
  transition: all 0.3s;
}

.step.active .step-text {
  color: #00AFE1;
  font-weight: 600;
}

.step-line {
  flex: 1;
  height: 4rpx;
  background: #e5e7eb;
  margin: 0 16rpx;
  transition: all 0.3s;
}

.step-line.active {
  background: #00AFE1;
}

/* 表单区域 */
.step-content {
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20rpx); }
  to { opacity: 1; transform: translateY(0); }
}

.section {
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
  margin: 0 32rpx 24rpx 32rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 32rpx;
  padding-left: 16rpx;
  border-left: 6rpx solid #00AFE1;
}

.form-item {
  margin-bottom: 32rpx;
}

.form-item.half {
  width: 48%;
}

.form-row {
  display: flex;
  justify-content: space-between;
}

.form-label {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 16rpx;
  display: block;
}

.form-label.required::before {
  content: '*';
  color: #ef4444;
  margin-right: 8rpx;
}

.form-input, .form-picker {
  height: 88rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333;
  display: flex;
  align-items: center;
}

.form-textarea {
  background: #f5f7fa;
  border-radius: 16rpx;
  padding: 24rpx;
  font-size: 28rpx;
  color: #333;
  min-height: 160rpx;
}

.form-textarea.large {
  min-height: 320rpx;
}

.placeholder {
  color: #9ca3af;
}

.char-count {
  text-align: right;
  font-size: 24rpx;
  color: #9ca3af;
  margin-top: 8rpx;
}

.form-tip {
  font-size: 24rpx;
  color: #9ca3af;
  margin-top: 12rpx;
}

/* 文件上传 */
.upload-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.upload-item {
  width: 160rpx;
  height: 160rpx;
  position: relative;
  border-radius: 16rpx;
  overflow: hidden;
}

.upload-preview {
  width: 100%;
  height: 100%;
}

.upload-file {
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.upload-file .iconfont {
  font-size: 48rpx;
  color: #9ca3af;
}

.file-name {
  font-size: 20rpx;
  color: #666;
  padding: 0 8rpx;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 140rpx;
}

.upload-delete {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
  width: 40rpx;
  height: 40rpx;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24rpx;
}

.upload-btn {
  width: 160rpx;
  height: 160rpx;
  background: #f5f7fa;
  border: 2rpx dashed #d1d5db;
  border-radius: 16rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.upload-btn .iconfont {
  font-size: 48rpx;
  color: #9ca3af;
}

.upload-text {
  font-size: 24rpx;
  color: #9ca3af;
}

/* 付款方案 */
.plan-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.plan-card {
  background: #f5f7fa;
  border: 3rpx solid transparent;
  border-radius: 16rpx;
  padding: 24rpx;
  transition: all 0.3s;
}

.plan-card.active {
  background: rgba(0, 175, 225, 0.05);
  border-color: #00AFE1;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.plan-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}

.plan-check {
  width: 40rpx;
  height: 40rpx;
  background: #00AFE1;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24rpx;
}

.plan-desc {
  font-size: 24rpx;
  color: #666;
}

/* 预算输入 */
.budget-input {
  display: flex;
  align-items: center;
  background: #f5f7fa;
  border-radius: 16rpx;
  padding: 0 24rpx;
  height: 120rpx;
}

.budget-symbol {
  font-size: 48rpx;
  font-weight: 600;
  color: #00AFE1;
  margin-right: 16rpx;
}

.budget-value {
  flex: 1;
  font-size: 48rpx;
  font-weight: 600;
  color: #333;
}

/* 阶段卡片 */
.stage-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.stage-card {
  background: #f5f7fa;
  border-radius: 16rpx;
  padding: 24rpx;
}

.stage-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.stage-number {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 28rpx;
  font-weight: 600;
  flex-shrink: 0;
}

.stage-name {
  flex: 1;
  height: 56rpx;
  background: #fff;
  border-radius: 12rpx;
  padding: 0 16rpx;
  font-size: 28rpx;
}

.stage-delete {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ef4444;
  font-size: 32rpx;
}

.stage-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.amount-input {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 12rpx;
  padding: 0 16rpx;
  height: 64rpx;
  width: 160rpx;
}

.amount-input input {
  flex: 1;
  font-size: 28rpx;
  text-align: right;
}

.amount-input text {
  font-size: 24rpx;
  color: #666;
  margin-left: 8rpx;
}

.amount-value {
  font-size: 32rpx;
  font-weight: 600;
  color: #00AFE1;
}

.stage-field {
  margin-bottom: 16rpx;
}

.stage-field:last-child {
  margin-bottom: 0;
}

.field-label {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 8rpx;
  display: block;
}

.field-input {
  height: 64rpx;
  background: #fff;
  border-radius: 12rpx;
  padding: 0 16rpx;
  font-size: 26rpx;
}

.field-textarea {
  background: #fff;
  border-radius: 12rpx;
  padding: 16rpx;
  font-size: 26rpx;
  min-height: 120rpx;
}

.add-stage-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  height: 88rpx;
  background: transparent;
  border: 2rpx dashed #00AFE1;
  border-radius: 16rpx;
  color: #00AFE1;
  font-size: 28rpx;
  font-weight: 600;
  margin-top: 24rpx;
}

.percent-warning {
  display: flex;
  align-items: center;
  gap: 12rpx;
  background: #fef2f2;
  border: 2rpx solid #fecaca;
  border-radius: 12rpx;
  padding: 16rpx;
  margin-top: 24rpx;
  color: #ef4444;
  font-size: 24rpx;
}

/* 信息提示框 */
.info-box {
  background: #eff6ff;
  border: 2rpx solid #bfdbfe;
  border-radius: 16rpx;
  padding: 24rpx;
  margin: 0 32rpx 24rpx 32rpx;
}

.info-title {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 26rpx;
  font-weight: 600;
  color: #1e40af;
  margin-bottom: 16rpx;
}

.info-item {
  font-size: 24rpx;
  color: #3b82f6;
  line-height: 1.8;
}

/* 确认信息 */
.confirm-item {
  display: flex;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f7fa;
}

.confirm-item:last-child {
  border-bottom: none;
}

.confirm-label {
  font-size: 28rpx;
  color: #666;
}

.confirm-value {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  text-align: right;
  max-width: 60%;
}

.confirm-budget {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  border-radius: 16rpx;
  margin-bottom: 24rpx;
}

.budget-label {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
}

.budget-amount {
  font-size: 48rpx;
  font-weight: 900;
  color: #fff;
}

.stage-summary {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx;
  background: #f5f7fa;
  border-radius: 12rpx;
}

.summary-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
}

.summary-name {
  flex: 1;
  font-size: 26rpx;
  color: #333;
}

.summary-percent {
  font-size: 24rpx;
  color: #666;
}

.summary-amount {
  font-size: 28rpx;
  font-weight: 600;
  color: #00AFE1;
}

/* 警告框 */
.warning-box {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  background: #fffbeb;
  border: 2rpx solid #fde68a;
  border-radius: 16rpx;
  padding: 24rpx;
  margin: 0 32rpx 24rpx 32rpx;
}

.warning-box .iconfont {
  font-size: 32rpx;
  color: #f59e0b;
  flex-shrink: 0;
}

.warning-text {
  flex: 1;
  font-size: 24rpx;
  color: #92400e;
  line-height: 1.6;
}

/* 按钮组 */
.btn-group {
  display: flex;
  gap: 24rpx;
  margin: 32rpx 32rpx 0 32rpx;
}

.btn {
  flex: 1;
  height: 96rpx;
  border-radius: 48rpx;
  font-size: 32rpx;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.btn-default {
  background: #f5f7fa;
  color: #666;
}

.btn-primary {
  background: linear-gradient(135deg, #00AFE1 0%, #0084A8 100%);
  color: #fff;
}
</style>
