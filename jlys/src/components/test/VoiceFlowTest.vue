<template>
  <div class="voice-flow-test">
    <h2>数字人音色流程测试</h2>
    
    <div class="test-section">
      <h3>步骤1: 选择数字人形象</h3>
      <el-radio-group v-model="selectedAvatarId" @change="onAvatarChange">
        <el-radio label="110017006">明哥音色 (x4_mingge)</el-radio>
        <el-radio label="138801001">悦小妮音色 (x4_yuexiaoni_assist)</el-radio>
        <el-radio label="110021006">灵小雨音色 (x4_lingxiaoyu_assist)</el-radio>
        <el-radio label="110117005">灵小颖音色 (x4_lingxiaoying_assist)</el-radio>
      </el-radio-group>
    </div>

    <div class="test-section">
      <h3>步骤2: 当前配置</h3>
      <div class="config-display">
        <p><strong>Avatar ID:</strong> {{ avatarConfig.avatarId }}</p>
        <p><strong>预期VCN:</strong> {{ expectedVcn }}</p>
        <p><strong>语音类型:</strong> {{ avatarConfig.voiceType }}</p>
      </div>
    </div>

    <div class="test-section">
      <h3>步骤3: 数字人组件</h3>
      <div class="digital-human-wrapper">
        <DigitalHuman 
          ref="digitalHumanRef" 
          :avatar-config="avatarConfig"
          @nlp-content="handleNlpContent"
        />
      </div>
    </div>

    <div class="test-section">
      <h3>步骤4: 测试语音</h3>
      <el-input 
        v-model="testText" 
        placeholder="输入要测试的文本"
        style="margin-bottom: 10px;"
      />
      <el-button type="primary" @click="testVoice">测试语音</el-button>
      <el-button type="success" @click="testVoiceWithCurrentConfig">使用当前配置测试</el-button>
    </div>

    <div class="test-section">
      <h3>调试日志</h3>
      <div class="logs-container">
        <div v-for="(log, index) in logs" :key="index" class="log-item">
          <span class="log-time">{{ log.time }}</span>
          <span class="log-level" :class="log.level">{{ log.level }}</span>
          <span class="log-message">{{ log.message }}</span>
        </div>
      </div>
      <el-button @click="clearLogs" size="small">清空日志</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import DigitalHuman from '../functions/AiInterview/DigitalHuman/DigitalHuman.vue'

const digitalHumanRef = ref(null)
const selectedAvatarId = ref('110117005')
const testText = ref('你好，我是AI多模态面试官，很高兴见到你。')
const logs = ref([])

const avatarConfig = reactive({
  avatarId: '110117005',
  tone: 'professional',
  speechRate: 1.0,
  voiceType: 'female'
})

// 音色映射
const vcnMap = {
  '110017006': 'x4_mingge',
  '138801001': 'x4_yuexiaoni_assist',
  '110021006': 'x4_lingxiaoyu_assist',
  '110117005': 'x4_lingxiaoying_assist'
}

const voiceTypeMap = {
  '110017006': 'male',
  '138801001': 'female',
  '110021006': 'female',
  '110117005': 'female'
}

const expectedVcn = computed(() => vcnMap[selectedAvatarId.value] || 'x4_lingxiaoying_assist')

function addLog(level, message) {
  const now = new Date()
  const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}.${now.getMilliseconds().toString().padStart(3, '0')}`
  logs.value.unshift({ time, level, message })
  console.log(`[${time}] [${level}] ${message}`)
}

function onAvatarChange(avatarId) {
  addLog('INFO', `用户选择数字人: ${avatarId}`)
  addLog('INFO', `预期VCN音色: ${vcnMap[avatarId]}`)
  
  // 更新配置
  avatarConfig.avatarId = avatarId
  avatarConfig.voiceType = voiceTypeMap[avatarId]
  
  addLog('INFO', `avatarConfig已更新: ${JSON.stringify(avatarConfig)}`)
}

function testVoice() {
  if (!digitalHumanRef.value) {
    addLog('ERROR', '数字人组件未初始化')
    return
  }
  
  addLog('INFO', `开始测试语音: "${testText.value}"`)
  addLog('INFO', `当前配置: avatarId=${avatarConfig.avatarId}, 预期vcn=${expectedVcn.value}`)
  
  try {
    digitalHumanRef.value.writeText(testText.value)
    addLog('SUCCESS', '语音测试命令已发送')
  } catch (error) {
    addLog('ERROR', `语音测试失败: ${error.message}`)
  }
}

function testVoiceWithCurrentConfig() {
  addLog('INFO', '=== 开始完整配置测试 ===')
  addLog('INFO', `当前avatarConfig: ${JSON.stringify(avatarConfig)}`)
  addLog('INFO', `预期VCN音色: ${expectedVcn.value}`)
  
  testVoice()
}

function handleNlpContent(data) {
  addLog('NLP', `收到NLP内容: ${data.content}`)
  if (data.isComplete) {
    addLog('NLP', 'NLP内容传输完成')
  }
}

function clearLogs() {
  logs.value = []
}

// 监听avatarConfig变化
watch(avatarConfig, (newConfig) => {
  addLog('WATCH', `avatarConfig发生变化: ${JSON.stringify(newConfig)}`)
}, { deep: true })

// 初始化
addLog('INFO', '数字人音色流程测试页面已加载')
addLog('INFO', `默认配置: ${JSON.stringify(avatarConfig)}`)
</script>

<style scoped>
.voice-flow-test {
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

.config-display p {
  margin: 8px 0;
  font-family: 'Courier New', monospace;
  font-size: 14px;
}

.digital-human-wrapper {
  min-height: 300px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 20px;
  background-color: #f9f9f9;
}

.logs-container {
  max-height: 400px;
  overflow-y: auto;
  background-color: #1a1a1a;
  color: #e0e0e0;
  padding: 15px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  margin-bottom: 10px;
}

.log-item {
  margin-bottom: 3px;
  display: block;
  line-height: 1.4;
}

.log-time {
  color: #888;
  margin-right: 8px;
}

.log-level {
  margin-right: 8px;
  padding: 2px 6px;
  border-radius: 3px;
  font-weight: bold;
  font-size: 10px;
}

.log-level.INFO {
  background-color: #2196F3;
  color: white;
}

.log-level.SUCCESS {
  background-color: #4CAF50;
  color: white;
}

.log-level.ERROR {
  background-color: #F44336;
  color: white;
}

.log-level.WATCH {
  background-color: #FF9800;
  color: white;
}

.log-level.NLP {
  background-color: #9C27B0;
  color: white;
}

.log-message {
  color: #e0e0e0;
}

h2, h3 {
  color: #2d3748;
  margin-bottom: 15px;
}

.el-button {
  margin-right: 10px;
  margin-bottom: 10px;
}
</style>
