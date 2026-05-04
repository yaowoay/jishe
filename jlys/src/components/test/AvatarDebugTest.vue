<template>
  <div class="avatar-debug-test">
    <h2>数字人音色调试测试</h2>
    
    <div class="debug-section">
      <h3>当前配置状态：</h3>
      <div class="config-display">
        <p><strong>Avatar ID:</strong> {{ currentConfig.avatarId }}</p>
        <p><strong>VCN音色:</strong> {{ getVcnByAvatarId(currentConfig.avatarId) }}</p>
        <p><strong>语音类型:</strong> {{ getVoiceTypeByAvatarId(currentConfig.avatarId) }}</p>
        <p><strong>显示名称:</strong> {{ getAvatarNameById(currentConfig.avatarId) }}</p>
      </div>
    </div>

    <div class="test-controls">
      <h3>测试不同配置：</h3>
      <el-button @click="testConfig('110017006')" type="primary">测试明哥音色</el-button>
      <el-button @click="testConfig('138801001')" type="success">测试悦小妮音色</el-button>
      <el-button @click="testConfig('110021006')" type="warning">测试灵小雨音色</el-button>
      <el-button @click="testConfig('110117005')" type="info">测试灵小颖音色</el-button>
    </div>

    <div class="digital-human-container">
      <h3>数字人组件：</h3>
      <DigitalHuman ref="digitalHumanRef" :avatar-config="currentConfig" />
    </div>

    <div class="logs-section">
      <h3>调试日志：</h3>
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
const logs = ref([])

const currentConfig = reactive({
  avatarId: '110117005',
  tone: 'professional',
  speechRate: 1.0,
  voiceType: 'female'
})

// 根据avatar_id获取对应的vcn音色
const getVcnByAvatarId = (avatarId) => {
  const avatarVcnMap = {
    '110017006': 'x4_mingge',                        // 明哥音色
    '138801001': 'x4_yuexiaoni_assist',              // 悦小妮音色
    '110021006': 'x4_lingxiaoyu_assist',             // 灵小雨音色
    '110117005': 'x4_lingxiaoying_assist'            // 灵小颖音色
  }
  return avatarVcnMap[avatarId] || 'x4_lingxiaoying_assist'
}

// 根据avatar_id获取语音类型
const getVoiceTypeByAvatarId = (avatarId) => {
  const avatarVoiceMap = {
    '110017006': 'male',   // 明哥音色
    '138801001': 'female', // 悦小妮音色
    '110021006': 'female', // 灵小雨音色
    '110117005': 'female'  // 灵小颖音色
  }
  return avatarVoiceMap[avatarId] || 'female'
}

// 根据avatar_id获取数字人名称
const getAvatarNameById = (avatarId) => {
  const avatarNameMap = {
    '110017006': '明哥音色',
    '138801001': '悦小妮音色',
    '110021006': '灵小雨音色',
    '110117005': '灵小颖音色'
  }
  return avatarNameMap[avatarId] || '未知形象'
}

function addLog(message) {
  const now = new Date()
  const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}`
  logs.value.unshift({ time, message })
  console.log(`[${time}] ${message}`)
}

function testConfig(avatarId) {
  addLog(`切换到数字人: ${getAvatarNameById(avatarId)} (${avatarId})`)
  addLog(`对应VCN音色: ${getVcnByAvatarId(avatarId)}`)
  
  // 更新配置
  currentConfig.avatarId = avatarId
  currentConfig.voiceType = getVoiceTypeByAvatarId(avatarId)
  
  addLog('配置已更新，等待数字人组件响应...')
}

function clearLogs() {
  logs.value = []
}

// 初始化日志
addLog('数字人音色调试测试页面已加载')
addLog(`默认配置: ${getAvatarNameById(currentConfig.avatarId)} (${currentConfig.avatarId})`)
</script>

<style scoped>
.avatar-debug-test {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.debug-section, .test-controls, .digital-human-container, .logs-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background-color: #fafafa;
}

.config-display p {
  margin: 8px 0;
  font-family: 'Courier New', monospace;
}

.test-controls .el-button {
  margin-right: 10px;
  margin-bottom: 10px;
}

.digital-human-container {
  min-height: 400px;
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
  display: block;
}

.log-time {
  color: #68d391;
  margin-right: 10px;
}

.log-message {
  color: #e2e8f0;
}

h2, h3 {
  color: #2d3748;
  margin-bottom: 15px;
}
</style>
