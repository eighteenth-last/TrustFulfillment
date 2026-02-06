<template>
  <div class="signature-pad-container">
    <div class="signature-header">
      <span class="signature-label">{{ label }}</span>
      <div class="signature-actions">
        <n-button size="small" quaternary @click="clearSignature">
          <template #icon><i class="fas fa-eraser"></i></template>
          清除
        </n-button>
      </div>
    </div>
    <div 
      class="signature-canvas-wrapper"
      :class="{ 'has-signature': hasSignature }"
    >
      <canvas
        ref="canvasRef"
        :width="canvasWidth"
        :height="canvasHeight"
        @mousedown="startDrawing"
        @mousemove="draw"
        @mouseup="stopDrawing"
        @mouseleave="stopDrawing"
        @touchstart.prevent="handleTouchStart"
        @touchmove.prevent="handleTouchMove"
        @touchend.prevent="stopDrawing"
      ></canvas>
      <div v-if="!hasSignature" class="signature-placeholder">
        <i class="fas fa-signature"></i>
        <span>请在此处签名</span>
      </div>
    </div>
    <p class="signature-tip">请使用鼠标或触摸屏在上方区域内手写签名</p>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, defineProps, defineEmits } from 'vue'

const props = defineProps({
  label: {
    type: String,
    default: '手写签名'
  },
  width: {
    type: Number,
    default: 400
  },
  height: {
    type: Number,
    default: 200
  },
  lineWidth: {
    type: Number,
    default: 2
  },
  lineColor: {
    type: String,
    default: '#1a1a2e'
  },
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const canvasRef = ref(null)
const canvasWidth = ref(props.width)
const canvasHeight = ref(props.height)
const isDrawing = ref(false)
const hasSignature = ref(false)
let ctx = null
let lastX = 0
let lastY = 0

onMounted(() => {
  initCanvas()
  // 如果有初始值，绘制到画布上
  if (props.modelValue) {
    loadSignature(props.modelValue)
  }
})

// 监听 modelValue 变化
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    loadSignature(newVal)
  } else {
    clearSignature()
  }
})

const initCanvas = () => {
  const canvas = canvasRef.value
  if (!canvas) return
  
  ctx = canvas.getContext('2d')
  ctx.strokeStyle = props.lineColor
  ctx.lineWidth = props.lineWidth
  ctx.lineCap = 'round'
  ctx.lineJoin = 'round'
  
  // 设置白色背景
  ctx.fillStyle = '#ffffff'
  ctx.fillRect(0, 0, canvasWidth.value, canvasHeight.value)
}

const getCanvasPosition = (e) => {
  const canvas = canvasRef.value
  const rect = canvas.getBoundingClientRect()
  const scaleX = canvas.width / rect.width
  const scaleY = canvas.height / rect.height
  
  return {
    x: (e.clientX - rect.left) * scaleX,
    y: (e.clientY - rect.top) * scaleY
  }
}

const startDrawing = (e) => {
  isDrawing.value = true
  const pos = getCanvasPosition(e)
  lastX = pos.x
  lastY = pos.y
}

const draw = (e) => {
  if (!isDrawing.value) return
  
  const pos = getCanvasPosition(e)
  
  ctx.beginPath()
  ctx.moveTo(lastX, lastY)
  ctx.lineTo(pos.x, pos.y)
  ctx.stroke()
  
  lastX = pos.x
  lastY = pos.y
  hasSignature.value = true
}

const stopDrawing = () => {
  if (isDrawing.value) {
    isDrawing.value = false
    emitSignature()
  }
}

// 触摸事件处理
const handleTouchStart = (e) => {
  const touch = e.touches[0]
  const canvas = canvasRef.value
  const rect = canvas.getBoundingClientRect()
  const scaleX = canvas.width / rect.width
  const scaleY = canvas.height / rect.height
  
  isDrawing.value = true
  lastX = (touch.clientX - rect.left) * scaleX
  lastY = (touch.clientY - rect.top) * scaleY
}

const handleTouchMove = (e) => {
  if (!isDrawing.value) return
  
  const touch = e.touches[0]
  const canvas = canvasRef.value
  const rect = canvas.getBoundingClientRect()
  const scaleX = canvas.width / rect.width
  const scaleY = canvas.height / rect.height
  
  const x = (touch.clientX - rect.left) * scaleX
  const y = (touch.clientY - rect.top) * scaleY
  
  ctx.beginPath()
  ctx.moveTo(lastX, lastY)
  ctx.lineTo(x, y)
  ctx.stroke()
  
  lastX = x
  lastY = y
  hasSignature.value = true
}

const clearSignature = () => {
  if (!ctx) return
  
  ctx.fillStyle = '#ffffff'
  ctx.fillRect(0, 0, canvasWidth.value, canvasHeight.value)
  hasSignature.value = false
  emit('update:modelValue', '')
  emit('change', '')
}

const emitSignature = () => {
  const canvas = canvasRef.value
  if (!canvas) return
  
  const dataUrl = canvas.toDataURL('image/png')
  emit('update:modelValue', dataUrl)
  emit('change', dataUrl)
}

const loadSignature = (dataUrl) => {
  if (!dataUrl || !ctx) return
  
  const img = new Image()
  img.onload = () => {
    ctx.fillStyle = '#ffffff'
    ctx.fillRect(0, 0, canvasWidth.value, canvasHeight.value)
    ctx.drawImage(img, 0, 0)
    hasSignature.value = true
  }
  img.src = dataUrl
}

// 暴露方法给父组件
defineExpose({
  clearSignature,
  getSignature: () => {
    const canvas = canvasRef.value
    return canvas ? canvas.toDataURL('image/png') : ''
  },
  hasSignature: () => hasSignature.value
})
</script>

<style scoped>
.signature-pad-container {
  width: 100%;
}

.signature-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.signature-label {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.signature-canvas-wrapper {
  position: relative;
  border: 2px dashed #d1d5db;
  border-radius: 12px;
  overflow: hidden;
  background: #ffffff;
  transition: all 0.3s;
}

.signature-canvas-wrapper:hover {
  border-color: #00AFE1;
}

.signature-canvas-wrapper.has-signature {
  border-style: solid;
  border-color: #10B981;
}

.signature-canvas-wrapper canvas {
  display: block;
  width: 100%;
  height: auto;
  cursor: crosshair;
}

.signature-placeholder {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #9ca3af;
  pointer-events: none;
}

.signature-placeholder i {
  font-size: 32px;
}

.signature-placeholder span {
  font-size: 14px;
}

.signature-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #9ca3af;
  text-align: center;
}
</style>
