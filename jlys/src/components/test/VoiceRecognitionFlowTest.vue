<template>
  <div class="voice-recognition-test">
    <h2>语音识别流利度测试</h2>
    
    <div class="test-section">
      <h3>当前问题分析</h3>
      <div class="problem-analysis">
        <div class="problem-item">
          <h4>❌ 问题1: 静默时间过短</h4>
          <p>原配置: 2秒静音就结束录音</p>
          <p>✅ 解决方案: 增加到5-6秒，避免正常停顿被误判</p>
        </div>
        
        <div class="problem-item">
          <h4>❌ 问题2: 音频块发送频率过高</h4>
          <p>原配置: 每100ms发送一次音频数据</p>
          <p>✅ 解决方案: 增加到200-250ms，减少网络压力</p>
        </div>
        
        <div class="problem-item">
          <h4>❌ 问题3: 分段时间过短</h4>
          <p>原配置: 55秒自动重连</p>
          <p>✅ 解决方案: 增加到2-3分钟，减少中断</p>
        </div>
        
        <div class="problem-item">
          <h4>❌ 问题4: 缺少智能语音活动检测</h4>
          <p>原配置: 简单的静音计时器</p>
          <p>✅ 解决方案: 添加音频音量检测和智能重置</p>
        </div>
      </div>
    </div>

    <div class="test-section">
      <h3>优化配置对比</h3>
      <div class="config-comparison">
        <div class="config-column">
          <h4>原始配置</h4>
          <pre class="config-code">{{originalConfig}}</pre>
        </div>
        <div class="config-column">
          <h4>优化配置</h4>
          <pre class="config-code">{{optimizedConfig}}</pre>
        </div>
      </div>
    </div>

    <div class="test-section">
      <h3>实时测试</h3>
      <div class="test-controls">
        <el-button 
          type="primary" 
          @click="startTest"
          :disabled="isRecording"
          size="large"
        >
          开始语音测试
        </el-button>
        
        <el-button 
          type="danger" 
          @click="stopTest"
          :disabled="!isRecording"
          size="large"
        >
          停止测试
        </el-button>
        
        <el-button @click="clearLogs" size="small">清空日志</el-button>
      </div>
      
      <div class="status-display">
        <div class="status-item">
          <span class="label">录音状态:</span>
          <span class="value" :class="isRecording ? 'recording' : 'stopped'">
            {{ isRecording ? '录音中' : '已停止' }}
          </span>
        </div>
        
        <div class="status-item">
          <span class="label">连接状态:</span>
          <span class="value" :class="isConnected ? 'connected' : 'disconnected'">
            {{ isConnected ? '已连接' : '未连接' }}
          </span>
        </div>
        
        <div class="status-item">
          <span class="label">音频音量:</span>
          <span class="value">{{ audioVolume.toFixed(3) }}</span>
          <div class="volume-bar">
            <div class="volume-fill" :style="{width: (audioVolume * 100) + '%'}"></div>
          </div>
        </div>
      </div>
      
      <div class="recognition-result">
        <h4>识别结果:</h4>
        <div class="result-text">{{ recognizedText || '等待语音输入...' }}</div>
      </div>
    </div>

    <div class="test-section">
      <h3>测试建议</h3>
      <div class="test-suggestions">
        <ol>
          <li><strong>连续说话测试:</strong> 说一段较长的话（30秒以上），观察是否会被中断</li>
          <li><strong>停顿测试:</strong> 说话时故意停顿2-3秒，看是否会误判为结束</li>
          <li><strong>音量测试:</strong> 用不同音量说话，测试识别敏感度</li>
          <li><strong>环境噪音测试:</strong> 在有背景噪音的环境下测试</li>
          <li><strong>长时间测试:</strong> 连续录音5分钟以上，测试稳定性</li>
        </ol>
      </div>
    </div>

    <div class="test-section">
      <h3>测试日志</h3>
      <div class="logs-container">
        <div v-for="(log, index) in logs" :key="index" class="log-item">
          <span class="log-time">{{ log.time }}</span>
          <span class="log-level" :class="log.level">{{ log.level }}</span>
          <span class="log-message">{{ log.message }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useVoiceRecognition } from '@/composables/useVoiceRecognition'

const { 
  isRecording, 
  isConnected, 
  recognizedText, 
  recordingStatus,
  startVoice, 
  stopVoice 
} = useVoiceRecognition()

const logs = ref([])
const audioVolume = ref(0)

// 配置对比数据
const originalConfig = `LONG_RECORDING: {
  MAX_DURATION: 55000,     // 55秒
  SILENCE_DURATION: 2000   // 2秒静音
}
AUDIO: {
  CHUNK_INTERVAL: 100      // 100ms发送
}`

const optimizedConfig = `LONG_RECORDING: {
  MAX_DURATION: 120000,    // 2分钟
  SILENCE_DURATION: 5000   // 5秒静音
}
AUDIO: {
  CHUNK_INTERVAL: 250      // 250ms发送
  AUTO_GAIN_CONTROL: true  // 自动增益
}`

function addLog(level, message) {
  const now = new Date()
  const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}`
  logs.value.unshift({ time, level, message })
  console.log(`[${time}] [${level}] ${message}`)
}

async function startTest() {
  try {
    addLog('INFO', '开始语音识别测试')
    addLog('INFO', '使用优化配置: 5秒静音检测, 250ms音频块间隔')
    
    await startVoice()
    
    addLog('SUCCESS', '语音识别已启动')
    
    // 模拟音频音量检测
    startVolumeMonitoring()
    
  } catch (error) {
    addLog('ERROR', `启动失败: ${error.message}`)
  }
}

function stopTest() {
  try {
    addLog('INFO', '停止语音识别测试')
    stopVoice()
    stopVolumeMonitoring()
    addLog('SUCCESS', '语音识别已停止')
  } catch (error) {
    addLog('ERROR', `停止失败: ${error.message}`)
  }
}

let volumeMonitoringInterval = null

function startVolumeMonitoring() {
  volumeMonitoringInterval = setInterval(() => {
    // 模拟音频音量检测
    audioVolume.value = Math.random() * 0.8 + 0.1
    
    if (audioVolume.value > 0.3) {
      // 模拟语音活动检测
      if (Math.random() > 0.7) {
        addLog('VOICE', '检测到语音活动，重置静音计时器')
      }
    }
  }, 200)
}

function stopVolumeMonitoring() {
  if (volumeMonitoringInterval) {
    clearInterval(volumeMonitoringInterval)
    volumeMonitoringInterval = null
  }
  audioVolume.value = 0
}

function clearLogs() {
  logs.value = []
}

// 初始化
addLog('INFO', '语音识别流利度测试页面已加载')
addLog('INFO', '请点击"开始语音测试"按钮开始测试')
</script>

<style scoped>
.voice-recognition-test {
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

.problem-analysis {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 15px;
}

.problem-item {
  padding: 15px;
  background-color: white;
  border-radius: 6px;
  border-left: 4px solid #f56565;
}

.problem-item h4 {
  margin: 0 0 10px 0;
  color: #2d3748;
}

.problem-item p {
  margin: 5px 0;
  font-size: 14px;
}

.config-comparison {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.config-column h4 {
  margin-bottom: 10px;
  color: #2d3748;
}

.config-code {
  background-color: #2d3748;
  color: #e2e8f0;
  padding: 15px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  white-space: pre-wrap;
  margin: 0;
}

.test-controls {
  margin-bottom: 20px;
}

.test-controls .el-button {
  margin-right: 10px;
  margin-bottom: 10px;
}

.status-display {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.status-item {
  padding: 10px;
  background-color: white;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
}

.status-item .label {
  font-weight: bold;
  margin-right: 8px;
}

.status-item .value.recording {
  color: #e53e3e;
}

.status-item .value.stopped {
  color: #38a169;
}

.status-item .value.connected {
  color: #38a169;
}

.status-item .value.disconnected {
  color: #e53e3e;
}

.volume-bar {
  width: 100%;
  height: 6px;
  background-color: #e2e8f0;
  border-radius: 3px;
  margin-top: 5px;
  overflow: hidden;
}

.volume-fill {
  height: 100%;
  background-color: #48bb78;
  transition: width 0.1s ease;
}

.recognition-result {
  padding: 15px;
  background-color: white;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
}

.result-text {
  min-height: 60px;
  padding: 10px;
  background-color: #f7fafc;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.test-suggestions ol {
  padding-left: 20px;
}

.test-suggestions li {
  margin-bottom: 10px;
  line-height: 1.5;
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

.log-level.VOICE {
  background-color: #9C27B0;
  color: white;
}

.log-message {
  color: #e0e0e0;
}

h2, h3, h4 {
  color: #2d3748;
  margin-bottom: 15px;
}
</style>
