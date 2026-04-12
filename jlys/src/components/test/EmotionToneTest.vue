<template>
  <div class="emotion-tone-test">
    <h2>语气风格与Emotion值测试</h2>
    
    <div class="test-section">
      <h3>语气风格映射表</h3>
      <div class="mapping-table">
        <div class="mapping-row header">
          <div class="col">语气风格</div>
          <div class="col">Tone值</div>
          <div class="col">Emotion值</div>
          <div class="col">情感描述</div>
        </div>
        <div class="mapping-row" v-for="item in emotionMapping" :key="item.tone">
          <div class="col">{{ item.label }}</div>
          <div class="col">{{ item.tone }}</div>
          <div class="col">{{ item.emotion }}</div>
          <div class="col">{{ item.description }}</div>
        </div>
      </div>
    </div>

    <div class="test-section">
      <h3>实时测试</h3>
      <div class="test-controls">
        <el-select v-model="selectedTone" placeholder="选择语气风格" @change="onToneChange">
          <el-option 
            v-for="item in emotionMapping" 
            :key="item.tone"
            :label="item.label" 
            :value="item.tone"
          />
        </el-select>
        <el-button type="primary" @click="testCurrentConfig">测试当前配置</el-button>
      </div>
      
      <div class="current-config">
        <h4>当前配置：</h4>
        <p><strong>语气风格：</strong>{{ getCurrentToneLabel() }}</p>
        <p><strong>Tone值：</strong>{{ selectedTone }}</p>
        <p><strong>Emotion值：</strong>{{ getCurrentEmotion() }}</p>
        <p><strong>情感描述：</strong>{{ getCurrentDescription() }}</p>
      </div>
    </div>

    <div class="test-section">
      <h3>数字人组件测试</h3>
      <div class="avatar-config-display">
        <p><strong>Avatar ID：</strong>{{ avatarConfig.avatarId }}</p>
        <p><strong>Tone：</strong>{{ avatarConfig.tone }}</p>
        <p><strong>预期Emotion：</strong>{{ getCurrentEmotion() }}</p>
      </div>
      
      <div class="digital-human-wrapper">
        <DigitalHuman 
          ref="digitalHumanRef" 
          :avatar-config="avatarConfig"
        />
      </div>
      
      <div class="test-actions">
        <el-input 
          v-model="testText" 
          placeholder="输入测试文本"
          style="margin-bottom: 10px;"
        />
        <el-button type="success" @click="testVoice">测试语音</el-button>
      </div>
    </div>

    <div class="test-section">
      <h3>测试日志</h3>
      <div class="logs-container">
        <div v-for="(log, index) in logs" :key="index" class="log-item">
          <span class="log-time">{{ log.time }}</span>
          <span class="log-message">{{ log.message }}</span>
        </div>
      </div>
      <el-button @click="clearLogs" size="small">清空日志</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import DigitalHuman from '../functions/AiInterview/DigitalHuman/DigitalHuman.vue'

const digitalHumanRef = ref(null)
const selectedTone = ref('professional')
const testText = ref('你好，欢迎参加本次面试，我是你的AI面试官。')
const logs = ref([])

const avatarConfig = reactive({
  avatarId: '110117005',
  tone: 'professional',
  speechRate: 1.0,
  voiceType: 'female'
})

// 语气风格与emotion值的映射
const emotionMapping = [
  {
    label: '正式严肃',
    tone: 'formal',
    emotion: 8,
    description: '严肃/正式的语调，适合正式场合'
  },
  {
    label: '温和友好',
    tone: 'friendly',
    emotion: 13,
    description: '友好/亲和的语调，温暖亲切'
  },
  {
    label: '轻松幽默',
    tone: 'casual',
    emotion: 1,
    description: '高兴/愉悦的语调，轻松活泼'
  },
  {
    label: '专业严谨',
    tone: 'professional',
    emotion: 12,
    description: '专业/商务的语调，严谨可靠'
  }
]

function getCurrentToneLabel() {
  const item = emotionMapping.find(item => item.tone === selectedTone.value)
  return item ? item.label : '未知'
}

function getCurrentEmotion() {
  const item = emotionMapping.find(item => item.tone === selectedTone.value)
  return item ? item.emotion : 13
}

function getCurrentDescription() {
  const item = emotionMapping.find(item => item.tone === selectedTone.value)
  return item ? item.description : '未知描述'
}

function addLog(message) {
  const now = new Date()
  const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}`
  logs.value.unshift({ time, message })
  console.log(`[${time}] ${message}`)
}

function onToneChange(tone) {
  addLog(`语气风格切换为: ${getCurrentToneLabel()} (${tone})`)
  addLog(`对应Emotion值: ${getCurrentEmotion()}`)
  
  // 更新avatarConfig
  avatarConfig.tone = tone
  addLog(`avatarConfig已更新: ${JSON.stringify(avatarConfig)}`)
}

function testCurrentConfig() {
  addLog('=== 开始配置测试 ===')
  addLog(`当前语气风格: ${getCurrentToneLabel()}`)
  addLog(`Tone值: ${selectedTone.value}`)
  addLog(`预期Emotion值: ${getCurrentEmotion()}`)
  addLog(`情感描述: ${getCurrentDescription()}`)
  addLog('配置测试完成')
}

function testVoice() {
  if (!digitalHumanRef.value) {
    addLog('错误: 数字人组件未初始化')
    return
  }
  
  addLog(`开始语音测试: "${testText.value}"`)
  addLog(`使用配置: tone=${avatarConfig.tone}, 预期emotion=${getCurrentEmotion()}`)
  
  try {
    digitalHumanRef.value.writeText(testText.value)
    addLog('语音测试命令已发送')
  } catch (error) {
    addLog(`语音测试失败: ${error.message}`)
  }
}

function clearLogs() {
  logs.value = []
}

// 初始化
onToneChange(selectedTone.value)
addLog('语气风格与Emotion值测试页面已加载')
</script>

<style scoped>
.emotion-tone-test {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.test-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background-color: #fafafa;
}

.mapping-table {
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.mapping-row {
  display: flex;
  border-bottom: 1px solid #ddd;
}

.mapping-row:last-child {
  border-bottom: none;
}

.mapping-row.header {
  background-color: #f5f7fa;
  font-weight: bold;
}

.col {
  flex: 1;
  padding: 12px;
  border-right: 1px solid #ddd;
  text-align: center;
}

.col:last-child {
  border-right: none;
}

.test-controls {
  margin-bottom: 20px;
}

.test-controls .el-select {
  width: 200px;
  margin-right: 10px;
}

.current-config {
  padding: 15px;
  background-color: #f0f9ff;
  border-radius: 4px;
  border-left: 4px solid #3b82f6;
}

.current-config h4 {
  margin: 0 0 10px 0;
  color: #1e40af;
}

.current-config p {
  margin: 5px 0;
  font-family: 'Courier New', monospace;
}

.avatar-config-display {
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.avatar-config-display p {
  margin: 5px 0;
  font-family: 'Courier New', monospace;
}

.digital-human-wrapper {
  min-height: 200px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 15px;
  background-color: #f9f9f9;
  margin-bottom: 15px;
}

.test-actions .el-button {
  margin-right: 10px;
}

.logs-container {
  max-height: 300px;
  overflow-y: auto;
  background-color: #2d3748;
  color: #e2e8f0;
  padding: 15px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
  margin-bottom: 10px;
}

.log-item {
  margin-bottom: 5px;
}

.log-time {
  color: #68d391;
  margin-right: 10px;
}

.log-message {
  color: #e2e8f0;
}

h2, h3, h4 {
  color: #2d3748;
  margin-bottom: 15px;
}
</style>
