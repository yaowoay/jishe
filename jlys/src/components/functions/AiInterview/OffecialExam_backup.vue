<template>
  <div class="simulat-exam-root">
    <div v-if="showWakeupBanner" class="wakeup-banner">请点击页面任意位置唤醒AI面试官</div>
    <div class="main-flex-layout">
      <!-- 中间：视频区+AI回答/参考答案 -->
      <div class="center-panel">
        <div class="video-header">
          <span class="timer big-timer">面试计时：{{ timerText }}</span>
          <el-button type="danger" @click="endInterview" size="large" class="big-end-btn">结束面试</el-button>
        </div>
        <div class="main-video-area">
          <div class="main-video-box" @click="isSwapped = !isSwapped">
            <template v-if="!isSwapped">
              <DigitalHuman ref="digitalHumanRef" @nlp-content="handleNlpContent" />
            </template>
            <template v-else>
              <video ref="userVideo" autoplay muted playsinline class="user-video"></video>
            </template>
            <div class="pip-video-box" @click.stop="isSwapped = !isSwapped">
              <template v-if="!isSwapped">
                <video ref="userVideo" autoplay muted playsinline class="user-video"></video>
              </template>
              <template v-else>
                <DigitalHuman ref="digitalHumanRef" @nlp-content="handleNlpContent" />
              </template>
            </div>
          </div>
        </div>
        <div class="ai-answer-box" :class="{ 'speaking': isDigitalHumanSpeaking }">
          <div class="ai-answer-label">
            <span v-if="isDigitalHumanSpeaking">数字人正在说：</span>
            <span v-else-if="digitalHumanSpeech">数字人说：</span>
            <span v-else>AI回答：</span>
            <div v-if="isDigitalHumanSpeaking" class="speaking-indicator">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </div>
          </div>
          <div class="ai-answer-content">{{ digitalHumanSpeech || aiCurrentAnswer }}</div>
          <div v-if="digitalHumanSpeech && !isDigitalHumanSpeaking" class="speech-complete-indicator">
            ✓ 讲话完成
          </div>
        </div>
      </div>

      <!-- 右侧：AI提示卡片 -->
      <div class="side-panel right-panel">
        <div class="dialog-list" ref="dialogListRef">
          <div v-for="(msg, idx) in dialogList" :key="idx" :class="['dialog-item', msg.role]">
            <div v-if="msg.role === 'ai'" class="ai-message">
              <el-avatar :size="36" class="avatar-ai" :class="{ 'digital-human-avatar': msg.source === 'digital-human' }">
                <el-icon v-if="msg.source === 'digital-human'"><Microphone /></el-icon>
                <el-icon v-else><ChatDotRound /></el-icon>
              </el-avatar>
              <div class="dialog-content ai-content" :class="{
                'digital-human-content': msg.source === 'digital-human',
                'streaming': msg.isStreaming
              }">
                <div class="dialog-role">
                  {{ msg.source === 'digital-human' ? '数字人面试官' : 'AI面试官' }}
                  <span v-if="msg.isStreaming" class="streaming-indicator-small">
                    <span class="dot-small"></span>
                    <span class="dot-small"></span>
                    <span class="dot-small"></span>
                  </span>
                </div>
                <div class="dialog-text">{{ msg.text }}</div>
              </div>
            </div>
            <div v-else class="user-message">
              <div class="dialog-content user-content">
                <div class="dialog-role">你</div>
                <div class="dialog-text">{{ msg.text }}</div>
              </div>
              <el-avatar :size="36" class="avatar-user">
                <el-icon><Microphone /></el-icon>
              </el-avatar>
            </div>
          </div>
        </div>
        <div class="fixed-bottom-input">
          <div class="dialog-input-area-row">
            <el-input
              v-model="recognizedText"
              placeholder="请点击下方语音按钮或手动输入..."
              class="voice-input"
              @keydown.enter="sendMsg"
            />
            <el-button class="send-btn" type="primary" @click="sendMsg" :disabled="!recognizedText">发送</el-button>
          </div>
          <div class="input-tip"><el-icon style="color:#409EFF;font-size:16px;vertical-align:middle;"><i class="el-icon-info-filled"></i></el-icon> <span class="tip-text">按<b>Enter</b>键也可以发送</span></div>
          <div class="recording-mode-selector">
            <el-radio-group v-model="recordingMode" size="small" :disabled="isRecording">
              <el-radio :label="'standard'">标准模式</el-radio>
              <el-radio :label="'long'">长语音模式</el-radio>
            </el-radio-group>
          </div>
          <div class="voice-btn-outer">
            <div class="voice-btn" :class="{recording: isRecording}" @click="toggleVoice">
              <el-icon v-if="!isRecording" class="mic-icon"><Microphone /></el-icon>
              <div v-else class="sound-waves">
                <span v-for="n in 8" :key="n" :class="['wave-bar', 'wave-' + n]"></span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ChatDotRound, Microphone } from '@element-plus/icons-vue'
import { useVoiceRecognition } from '@/composables/useVoiceRecognition'
import axios from 'axios'
import DigitalHuman from './DigitalHuman/DigitalHuman.vue'

const router = useRouter()
const dialogList = ref([
  { role: 'ai', text: '你好！欢迎参加本次模拟面试。' },
  { role: 'ai', text: '我是你的AI面试官，我们将进行一场模拟面试。' }
])
const aiCurrentAnswer = ref(dialogList.value[dialogList.value.length - 1].text)

// 数字人讲话内容
const digitalHumanSpeech = ref('')
const isDigitalHumanSpeaking = ref(false)

// 新增：控制视频互换
const isSwapped = ref(false)
const digitalHumanRef = ref(null)
const dialogListRef = ref(null)

const showWakeupBanner = ref(false)

// 处理数字人NLP内容
function handleNlpContent(nlpData) {
  console.log('接收到数字人讲话内容:', nlpData)
  
  if (nlpData && nlpData.content) {
    digitalHumanSpeech.value = nlpData.content
    isDigitalHumanSpeaking.value = !nlpData.isComplete
    
    // 实时更新对话列表中最后一条AI消息
    const lastMessage = dialogList.value[dialogList.value.length - 1]
    if (lastMessage && lastMessage.role === 'ai') {
      lastMessage.text = nlpData.content
      lastMessage.source = 'digital-human'
      lastMessage.isStreaming = !nlpData.isComplete
      scrollToBottom()
    }
    
    if (nlpData.isComplete) {
      console.log('数字人讲话完成:', nlpData.content)
    }
  }
}

const {
  isRecording,
  recordingMode,
  recognizedText,
  toggleVoice,
  cleanup
} = useVoiceRecognition()

const timerText = ref('00:00')
let timer = null
let seconds = 0

function scrollToBottom() {
  nextTick(() => {
    if (dialogListRef.value) {
      dialogListRef.value.scrollTop = dialogListRef.value.scrollHeight
    }
  })
}

watch(dialogList, () => {
  scrollToBottom()
})

const COZE_CONV_API = 'https://api.coze.cn/v1/conversation/create'
const COZE_API = 'https://api.coze.cn/v3/chat'
const BOT_ID = '7528952158264573995'
const WORKFLOW_ID = '7529347688825143311'
const TOKEN = 'pat_6qaPofRM0V83GchiA9AIlZuZmGl34XmuxpI7jMJAqsRu2JvpDOLH1RAkAesQuoUB'

const conversationId = ref('')
const userId = ref('user_' + Date.now())

async function createConversation() {
  try {
    const res = await axios.post(
      COZE_CONV_API,
      {
        bot_id: BOT_ID,
        workflow_id: WORKFLOW_ID
      },
      {
        headers: {
          Authorization: `Bearer ${TOKEN}`,
          'Content-Type': 'application/json'
        }
      }
    )
    const cid = res.data.data?.id || ''
    console.log('创建会话，conversation_id:', cid)
    return cid
  } catch (error) {
    console.error('创建会话失败:', error)
    return ''
  }
}

onMounted(async () => {
  conversationId.value = await createConversation()
  
  timer = setInterval(() => {
    seconds++
    const min = String(Math.floor(seconds / 60)).padStart(2, '0')
    const sec = String(seconds % 60).padStart(2, '0')
    timerText.value = `${min}:${sec}`
  }, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
  cleanup()
})

function endInterview() {
  router.push('/ai-interview')
}

async function sendMsg() {
  if (!recognizedText.value) return
  if (!conversationId.value) {
    dialogList.value.push({ role: 'ai', text: '会话未创建成功，请刷新页面重试。' })
    scrollToBottom()
    return
  }
  
  const userMessage = String(recognizedText.value).trim()
  if (!userMessage) {
    dialogList.value.push({ role: 'ai', text: '请输入内容后再发送。' })
    scrollToBottom()
    return
  }
  
  dialogList.value.push({ role: 'user', text: userMessage })
  recognizedText.value = ''
  dialogList.value.push({ role: 'ai', text: 'AI正在思考中...' })
  scrollToBottom()

  try {
    const body = {
      bot_id: BOT_ID,
      user_id: userId.value,
      stream: true,
      auto_save_history: true,
      additional_messages: [
        {
          role: 'user',
          content: userMessage,
          content_type: 'text'
        }
      ]
    }

    const response = await fetch(`${COZE_API}?conversation_id=${conversationId.value}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${TOKEN}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(body)
    })

    if (!response.body) throw new Error('流式响应不支持')
    const reader = response.body.getReader()
    let aiReply = ''
    let isAnswer = false

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      const textChunk = new TextDecoder().decode(value).trim()
      if (!textChunk) continue

      textChunk.split('\n').forEach(line => {
        if (line.startsWith('data:')) {
          const dataStr = line.replace('data:', '').trim()
          try {
            const dataObj = JSON.parse(dataStr)
            if (dataObj.type === 'answer' && dataObj.content) {
              aiReply = dataObj.content
              isAnswer = true
              aiCurrentAnswer.value = aiReply
              
              // 驱动数字人
              if (digitalHumanRef.value && digitalHumanRef.value.writeText) {
                digitalHumanRef.value.writeText(aiReply)
              }
            }
          } catch (e) { /* ignore */ }
        }
      })
    }

    if (!isAnswer) {
      dialogList.value[dialogList.value.length - 1].text = 'AI未返回文本回复'
      scrollToBottom()
    }
  } catch (error) {
    const lastMessage = dialogList.value[dialogList.value.length - 1]
    if (lastMessage && lastMessage.role === 'ai' && lastMessage.text === 'AI正在思考中...') {
      lastMessage.text = 'AI接口调用失败，请检查token或网络。'
    } else {
      dialogList.value.push({ role: 'ai', text: 'AI接口调用失败，请检查token或网络。' })
    }
    scrollToBottom()
    console.error(error)
  }
}
</script>

<style scoped>
/* 基础样式保持原有设计 */
.simulat-exam-root {
  width: 100%;
  height: 100vh;
  background: #f5f8ff;
  padding: 0;
  margin: 0;
  box-sizing: border-box;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.main-flex-layout {
  display: flex;
  flex-direction: row;
  width: 100%;
  padding-left: 40px;
  padding-right: 40px;
  height: 95vh;
  margin-left: auto;
  margin-right: auto;
  gap: 8px;
  justify-content: space-between;
  align-items: stretch;
  overflow: hidden;
  box-sizing: border-box;
}

.center-panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 600px;
  min-width: 520px;
  max-width: 700px;
  height: 90vh;
  justify-content: flex-start;
  background: transparent;
  border-radius: 0;
  box-shadow: none;
  padding: 0;
  box-sizing: border-box;
}

.side-panel {
  width: 340px;
  min-width: 280px;
  max-width: 400px;
  height: 90vh;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  justify-content: flex-start;
  box-sizing: border-box;
  padding: 18px 0 0 0;
}

.right-panel {
  flex: none !important;
  width: 1000px;
  min-width: 700px;
  max-width: 1100px;
  padding: 18px 18px 0 18px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  height: 90vh;
  position: relative;
  box-sizing: border-box;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  margin-top: 14px;
}

/* 其他样式保持简化版本 */
</style>
