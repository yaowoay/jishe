<template>
  <div class="official-exam-root">
    <div v-if="showWakeupBanner" class="wakeup-banner">
      请点击页面任意位置唤醒AI多模态面试官
    </div>
    
    <div class="main-flex-layout">
      <!-- 中间：视频区+AI回答 -->
      <div class="center-panel">
        <div class="video-header">
          <div class="timer-section">
            <span class="timer big-timer">面试计时：{{ timerText }}</span>
            <div v-if="isStructuredInterview" class="interview-progress">
              <span class="main-progress">结构化面试 - 第{{ currentQuestionIndex + 1 }}/{{ interviewQuestions.length }}题</span>
              <span v-if="isInFollowUp" class="follow-up-indicator">
                追问阶段 ({{ followUpCount }}/{{ currentQuestionMaxFollowUps }})
              </span>
              <span v-else-if="currentQuestionMaxFollowUps > 0" class="follow-up-plan">
                计划追问 {{ currentQuestionMaxFollowUps }} 次
              </span>

              <!-- 保存状态指示器 -->
              <span v-if="interviewStarted" class="save-status" :class="saveStatus">
                <i v-if="saveStatus === 'saving'" class="el-icon-loading"></i>
                <i v-else-if="saveStatus === 'success'" class="el-icon-check"></i>
                <i v-else-if="saveStatus === 'error'" class="el-icon-warning"></i>
                <span v-if="saveStatus === 'saving'">保存中...</span>
                <span v-else-if="saveStatus === 'success'">已保存</span>
                <span v-else-if="saveStatus === 'error'">保存失败</span>
              </span>
            </div>
            <div v-else class="interview-mode">
              自由面试模式
            </div>
          </div>
          <div class="header-buttons">
            <!-- 结构化面试控制按钮 -->
            <div v-if="isStructuredInterview" class="interview-controls">
              <!-- 追问策略选择 -->
              <el-select
                v-model="currentStrategy"
                size="small"
                style="width: 100px;"
                @change="onStrategyChange"
              >
                <el-option
                  v-for="(strategy, key) in followUpStrategies"
                  :key="key"
                  :label="strategy.description"
                  :value="key"
                />
              </el-select>

              <el-button
                v-if="isInFollowUp"
                type="primary"
                size="small"
                @click="moveToNextQuestion"
                icon="Right"
              >
                下一题
              </el-button>
            </div>

            <el-button
              type="success"
              @click="initCamera"
              size="small"
              :disabled="cameraInitialized"
            >
              {{ cameraInitialized ? '摄像头已开启' : '开启摄像头' }}
            </el-button>
            <el-button
              type="danger"
              @click="endInterview"
              size="large"
              class="big-end-btn"
            >
              结束面试
            </el-button>

            <!-- 测试录制按钮 -->
            <el-button
              v-if="cameraInitialized"
              :type="isVideoRecording ? 'warning' : 'success'"
              @click="toggleVideoRecording"
              size="small"
              style="margin-left: 10px;"
            >
              {{ isVideoRecording ? '停止录制' : '开始录制' }}
            </el-button>
          </div>
        </div>
        
        <div class="main-video-area">
          <div class="main-video-box" @click="isSwapped = !isSwapped">
            <template v-if="!isSwapped">
              <DigitalHuman 
                ref="digitalHumanRef" 
                @nlp-content="handleNlpContent" 
              />
            </template>
            <template v-else>
              <video 
                ref="userVideo" 
                autoplay 
                muted 
                playsinline 
                class="user-video"
              ></video>
            </template>
            
            <div class="pip-video-box" @click.stop="isSwapped = !isSwapped">
              <template v-if="!isSwapped">
                <video 
                  ref="userVideo" 
                  autoplay 
                  muted 
                  playsinline 
                  class="user-video"
                ></video>
              </template>
              <template v-else>
                <DigitalHuman 
                  ref="digitalHumanRef" 
                  @nlp-content="handleNlpContent" 
                />
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
          <div class="ai-answer-content">
            {{ digitalHumanSpeech || aiCurrentAnswer }}
          </div>
          <div 
            v-if="digitalHumanSpeech && !isDigitalHumanSpeaking" 
            class="speech-complete-indicator"
          >
            ✓ 讲话完成
          </div>
        </div>

        <!-- 调试按钮 - 仅在开发环境显示 -->
        <div v-if="showDebugControls" class="debug-controls">
          <el-button size="small" @click="testDigitalHumanSpeech">
            测试数字人讲话
          </el-button>
          <el-button size="small" @click="clearDigitalHumanSpeech">
            清空讲话内容
          </el-button>
        </div>
      </div>

      <!-- 右侧：对话列表 -->
      <div class="side-panel right-panel">
        <div class="dialog-list" ref="dialogListRef">
          <div 
            v-for="(msg, idx) in dialogList" 
            :key="idx" 
            :class="['dialog-item', msg.role]"
          >
            <div v-if="msg.role === 'ai'" class="ai-message">
              <el-avatar 
                :size="36" 
                class="avatar-ai" 
                :class="{ 'digital-human-avatar': msg.source === 'digital-human' }"
              >
                <el-icon v-if="msg.source === 'digital-human'">
                  <Microphone />
                </el-icon>
                <el-icon v-else>
                  <ChatDotRound />
                </el-icon>
              </el-avatar>
              <div 
                class="dialog-content ai-content" 
                :class="{
                  'digital-human-content': msg.source === 'digital-human',
                  'streaming': msg.isStreaming
                }" 
                :data-source="msg.source"
              >
                <div class="dialog-role">
                  {{ msg.source === 'digital-human' ? 'AI多模态面试官' :
                     msg.source === 'interview-report' ? '面试报告' : 'AI多模态面试官' }}
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
            <el-button 
              class="send-btn" 
              type="primary" 
              @click="sendMsg" 
              :disabled="!recognizedText"
            >
              发送
            </el-button>

<!--            <el-button class="send-btn"  @click="pushInterviewReport">进入结构化页面</el-button>-->
          </div>
          
          <div class="input-tip">
            <el-icon style="color:#409EFF;font-size:16px;vertical-align:middle;">
              <InfoFilled />
            </el-icon> 
            <span class="tip-text">按<b>Enter</b>键也可以发送</span>
          </div>
          
          <div class="recording-mode-selector">
            <el-radio-group v-model="recordingMode" size="small" :disabled="isRecording">
              <el-radio :label="'standard'">标准模式</el-radio>
              <el-radio :label="'long'">长语音模式</el-radio>
            </el-radio-group>
          </div>
          
          <div class="voice-btn-outer">
            <div class="voice-btn" :class="{recording: isRecording}" @click="toggleVoice">
              <el-icon v-if="!isRecording" class="mic-icon">
                <Microphone />
              </el-icon>
              <div v-else class="sound-waves">
                <span v-for="n in 8" :key="n" :class="['wave-bar', 'wave-' + n]"></span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 人脸检测结果显示 -->
    <div 
      v-if="faceDetectResult" 
      class="face-detect-result"
    >
      <div v-if="faceDetectResult.error" style="color:red;">
        检测失败：{{ faceDetectResult.error }}
      </div>
      <div v-else>
        <div>检测到人脸数：{{ faceDetectResult.face_num }}</div>
        <div v-if="faceDetectResult.face_1">
          第1个人脸属性：{{ faceDetectResult.face_1.property && JSON.stringify(faceDetectResult.face_1.property) }}
        </div>
      </div>
    </div>

    <!-- 视频分析组件 -->
    <VideoAnalysis
      v-if="showVideoAnalysis"
      :interview-id="currentInterviewId"
      @close="showVideoAnalysis = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ChatDotRound, Microphone, InfoFilled } from '@element-plus/icons-vue'
import { useVoiceRecognition } from '@/composables/useVoiceRecognition'
import { detectFace } from '@/composables/useFaceDetect'
import DigitalHuman from './DigitalHuman/DigitalHuman.vue'
import VideoAnalysis from './VideoAnalysis.vue'
import { getInterviewQuestionsByApplicationId, startInterviewExam, submitInterviewAnswers, saveInterviewAnswer, updateInterviewStatusByApplication, quickEvaluateInterview, saveAIInterviewRecord } from '@/api/interview'
import { ElMessage } from 'element-plus'
// 先在文件顶部导入 axios
import axios from 'axios'
// 路由
const router = useRouter()

// 面试题目相关
const interviewQuestions = ref([])
const currentQuestionIndex = ref(0)
const userAnswers = ref([])
const isStructuredInterview = ref(false) // 是否为结构化面试（从数据库获取题目）
const applicationId = ref(null) // 申请ID，从路由参数获取

// 追问相关状态
const isInFollowUp = ref(false) // 是否正在追问阶段
const followUpCount = ref(0) // 当前题目的追问次数
const maxFollowUps = ref(2) // 每个题目最大追问次数（会被随机值覆盖）
const currentQuestionMaxFollowUps = ref(2) // 当前题目的随机追问次数
const currentQuestionAnswers = ref([]) // 当前题目的所有回答（包括追问）

// 对话列表
const dialogList = ref([
  { role: 'ai', text: '你好！欢迎参加本次面试。' },
  { role: 'ai', text: '我是你的AI多模态面试官，我们将进行一场面试，准备好了吗。' }
])

// 视频分析相关
const showVideoAnalysis = ref(false)
// 生成一个较小的面试ID（使用时间戳的后8位）
const currentInterviewId = ref(Math.floor(Date.now() / 1000) % 100000000)
const startTime = ref(new Date().toISOString()) // 面试开始时间

// AI当前回答
const aiCurrentAnswer = ref(dialogList.value[dialogList.value.length - 1].text)

// 数字人讲话相关
const digitalHumanSpeech = ref('')
const lastCompleteNlpContent = ref('')
const isDigitalHumanSpeaking = ref(false)
const nlpContentHistory = ref([])
let clearSpeechTimer = null

// 调试控制
const showDebugControls = ref(process.env.NODE_ENV === 'development')

// 视频相关
const userVideo = ref(null)
const digitalHumanRef = ref(null)
const dialogListRef = ref(null)
const isSwapped = ref(false)

// 摄像头相关
const cameraInitialized = ref(false)
const cameraError = ref(null)
const showWakeupBanner = ref(false)

// 视频录制相关
const mediaRecorder = ref(null)
const recordedChunks = ref([])
const isVideoRecording = ref(false) // 改名避免与语音识别的isRecording冲突
const recordedVideoBlob = ref(null)
const videoStream = ref(null)

// 人脸检测
const faceDetectResult = ref(null)
let faceDetectionTimer = null

// 语音识别
const {
  isRecording,
  recordingMode,
  recognizedText,
  toggleVoice,
  cleanup
} = useVoiceRecognition()

// 计时器
const timerText = ref('00:00')
let timer = null
let seconds = 0

// 面试配置
const interviewConfig = {
  role: '专业AI多模态面试官',
  style: '友善、专业、循序渐进',
  responseLength: '50-100字',
  language: '自然、口语化'
}

// 获取面试题目
async function loadInterviewQuestions() {
  try {
    // 从路由参数获取applicationId
    const routeApplicationId = router.currentRoute.value.params.applicationId ||
                              router.currentRoute.value.query.applicationId

    console.log('当前路由参数:', router.currentRoute.value.params)
    console.log('当前查询参数:', router.currentRoute.value.query)
    console.log('获取到的applicationId:', routeApplicationId)

    if (!routeApplicationId) {
      console.warn('未找到applicationId，使用自由面试模式')
      isStructuredInterview.value = false
      return
    }

    applicationId.value = routeApplicationId
    console.log('正在获取面试题目，applicationId:', applicationId.value)

    const response = await getInterviewQuestionsByApplicationId(applicationId.value)

    console.log('API响应:', response)
    console.log('响应数据:', response.data)
    console.log('题目数组:', response.data?.questions)

    if (response.success && response.data && response.data.questions && response.data.questions.length > 0) {
      // 直接使用后端返回的questions数组
      interviewQuestions.value = response.data.questions
      isStructuredInterview.value = true
      currentQuestionIndex.value = 0

      console.log('成功获取面试题目:', response.data.questions)
      console.log('题目数量:', response.data.questions.length)

      // 开始结构化面试
      startStructuredInterview()
    } else {
      console.warn('未找到面试题目，使用自由面试模式')
      console.warn('响应详情:', {
        success: response.success,
        hasData: !!response.data,
        hasQuestions: !!(response.data && response.data.questions),
        questionsLength: response.data?.questions?.length || 0
      })
      isStructuredInterview.value = false
    }
  } catch (error) {
    console.error('获取面试题目失败:', error)
    isStructuredInterview.value = false
  }
}

// 开始结构化面试
async function startStructuredInterview() {
  if (!interviewQuestions.value.length) return

  // 标记面试开始
  interviewStarted.value = true

  // 启动面试考试并更新状态
  if (applicationId.value) {
    try {
      // 先启动面试考试，确保相关记录存在
      await startInterviewExam(applicationId.value)
      console.log('✅ 面试考试已启动')

      // 更新面试状态为进行中
      await updateInterviewStatusByApplication(applicationId.value, 'in_progress')
      console.log('✅ 面试状态已更新为进行中')
    } catch (error) {
      console.warn('⚠️ 启动面试或更新状态失败:', error)
    }
  }

  // 清空对话列表，重新开始
  dialogList.value = [
    { role: 'ai', text: '你好！欢迎参加本次面试。' },
    { role: 'ai', text: `我为您准备了${interviewQuestions.value.length}个问题，让我们开始吧。` }
  ]

  // 播报欢迎语
  if (digitalHumanRef.value && digitalHumanRef.value.writeText) {
    try {
      digitalHumanRef.value.writeText(`你好！欢迎参加本次面试。我为您准备了${interviewQuestions.value.length}个问题，让我们开始吧。`, false)
    } catch (error) {
      console.warn('欢迎语播报失败，但不影响面试流程:', error)
    }
  }

  // 开始第一个问题
  setTimeout(() => {
    askNextQuestion()
  }, 4000) // 给欢迎语更多时间
}

// 追问策略配置
const followUpStrategies = {
  // 温和策略：较少追问
  gentle: { weights: [0.3, 0.4, 0.3, 0.0], description: '温和模式' },
  // 标准策略：平衡追问
  standard: { weights: [0.1, 0.3, 0.4, 0.2], description: '标准模式' },
  // 深度策略：更多追问
  intensive: { weights: [0.0, 0.2, 0.4, 0.4], description: '深度模式' }
}

const currentStrategy = ref('standard') // 当前使用的策略

// 实时保存相关状态
const isSaving = ref(false) // 是否正在保存
const lastSaveTime = ref(null) // 最后保存时间
const saveStatus = ref('idle') // 保存状态：idle, saving, success, error
const interviewStarted = ref(false) // 面试是否已开始

// 计算面试时长
function calculateDuration() {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
}

// 生成随机追问次数
function generateRandomFollowUpCount() {
  const strategy = followUpStrategies[currentStrategy.value]
  const weights = strategy.weights
  const random = Math.random()
  let cumulative = 0

  for (let i = 0; i < weights.length; i++) {
    cumulative += weights[i]
    if (random <= cumulative) {
      return i
    }
  }
  return 2 // 默认返回2次
}

// 询问下一个问题
function askNextQuestion() {
  if (currentQuestionIndex.value >= interviewQuestions.value.length) {
    // 所有问题都问完了
    finishStructuredInterview()
    return
  }

  // 重置追问状态并生成随机追问次数
  isInFollowUp.value = false
  followUpCount.value = 0
  currentQuestionAnswers.value = []
  currentQuestionMaxFollowUps.value = generateRandomFollowUpCount()

  console.log(`第${currentQuestionIndex.value + 1}题将进行${currentQuestionMaxFollowUps.value}次追问`)

  const currentQuestion = interviewQuestions.value[currentQuestionIndex.value]
  const questionText = currentQuestion.question || currentQuestion.text || currentQuestion

  // 添加到对话列表
  dialogList.value.push({
    role: 'ai',
    text: questionText,
    questionNumber: currentQuestionIndex.value + 1,
    isMainQuestion: true
  })

  // 更新AI当前回答
  aiCurrentAnswer.value = questionText

  // 让数字人直接播报问题文本
  if (digitalHumanRef.value && digitalHumanRef.value.writeText) {
    console.log(`数字人播报第${currentQuestionIndex.value + 1}个问题:`, questionText)

    try {
      // 直接播报题目，不使用NLP，避免冗长的介绍
      digitalHumanRef.value.writeText(questionText, false)
    } catch (error) {
      console.warn('题目播报失败，但不影响面试流程:', error)
    }
  }

  // 滚动到最新消息
  nextTick(() => {
    scrollToBottom()
  })
}

// 处理用户回答
async function handleUserAnswer(answer) {
  if (!isStructuredInterview.value) return

  const answerRecord = {
    answer: answer,
    timestamp: new Date().toISOString(),
    isFollowUp: isInFollowUp.value,
    followUpIndex: followUpCount.value
  }

  // 记录当前题目的回答
  currentQuestionAnswers.value.push(answerRecord)

  console.log(`用户回答第${currentQuestionIndex.value + 1}个问题 (追问${followUpCount.value}):`, answer)
  console.log('当前面试状态:', {
    isStructuredInterview: isStructuredInterview.value,
    applicationId: applicationId.value,
    currentQuestionIndex: currentQuestionIndex.value,
    isInFollowUp: isInFollowUp.value,
    followUpCount: followUpCount.value
  })

  // 实时保存到后端
  const saveData = {
    questionIndex: currentQuestionIndex.value,
    question: interviewQuestions.value[currentQuestionIndex.value],
    answerRecord: answerRecord,
    totalAnswersForQuestion: currentQuestionAnswers.value.length,
    interviewProgress: {
      currentQuestion: currentQuestionIndex.value + 1,
      totalQuestions: interviewQuestions.value.length,
      followUpStrategy: currentStrategy.value
    }
  }

  console.log('准备保存的数据:', saveData)

  // 异步保存，不阻塞面试流程
  saveAnswerRealTime(saveData)

  // 判断是否需要追问
  if (followUpCount.value < currentQuestionMaxFollowUps.value) {
    // 进入或继续追问阶段
    isInFollowUp.value = true
    followUpCount.value++

    // 构建简洁的追问上下文
    const currentQuestion = interviewQuestions.value[currentQuestionIndex.value]
    const questionText = currentQuestion.question || currentQuestion.text || currentQuestion

    // 更简洁的追问提示，确保数字人只问一个简短的追问
    const followUpContext = `针对候选人的回答"${answer}"，请提出一个对其中关键点的追问问题，控制在100字以内。`

    console.log('发送追问上下文:', followUpContext)

    // 让数字人进行智能追问
    if (digitalHumanRef.value && digitalHumanRef.value.writeText) {
      try {
        digitalHumanRef.value.writeText(followUpContext, true) // 使用NLP进行追问
      } catch (error) {
        console.warn('追问播报失败，但不影响面试流程:', error)
      }
    }
  } else {
    // 追问次数已达上限，进入下一个问题
    moveToNextQuestion()
  }
}

// 实时保存单个回答
async function saveAnswerRealTime(answerData) {
  if (!applicationId.value) {
    console.warn('没有applicationId，跳过实时保存')
    return false
  }

  try {
    isSaving.value = true
    saveStatus.value = 'saving'

    console.log('🔄 实时保存回答:', answerData)
    console.log('🔄 保存到applicationId:', applicationId.value)

    const response = await saveInterviewAnswer(applicationId.value, answerData)
    console.log('🔄 API响应:', response)

    if (response.success) {
      saveStatus.value = 'success'
      lastSaveTime.value = new Date()
      console.log('✅ 回答保存成功')
      return true
    } else {
      throw new Error(response.message || '保存失败')
    }
  } catch (error) {
    saveStatus.value = 'error'
    console.error('❌ 实时保存失败:', error)

    // 保存到本地存储作为备份
    saveToLocalStorage(answerData)
    return false
  } finally {
    isSaving.value = false

    // 3秒后重置状态
    setTimeout(() => {
      if (saveStatus.value !== 'saving') {
        saveStatus.value = 'idle'
      }
    }, 3000)
  }
}

// 保存到本地存储（备份）
function saveToLocalStorage(answerData) {
  try {
    const key = `interview_backup_${applicationId.value}`
    let backupData = JSON.parse(localStorage.getItem(key) || '[]')

    backupData.push({
      ...answerData,
      savedAt: new Date().toISOString(),
      isBackup: true
    })

    localStorage.setItem(key, JSON.stringify(backupData))
    console.log('💾 已保存到本地存储作为备份')
  } catch (error) {
    console.error('本地存储失败:', error)
  }
}

// 策略变更处理
function onStrategyChange(newStrategy) {
  console.log(`追问策略已切换为: ${followUpStrategies[newStrategy].description}`)
}

// 移动到下一个问题
function moveToNextQuestion() {
  // 保存当前题目的完整回答记录
  userAnswers.value.push({
    questionIndex: currentQuestionIndex.value,
    question: interviewQuestions.value[currentQuestionIndex.value],
    allAnswers: [...currentQuestionAnswers.value], // 包含主回答和所有追问回答
    followUpCount: followUpCount.value,
    timestamp: new Date().toISOString()
  })

  console.log(`第${currentQuestionIndex.value + 1}个问题完成，计划追问${currentQuestionMaxFollowUps.value}次，实际追问${followUpCount.value}次`)

  // 移动到下一个问题
  currentQuestionIndex.value++

  // 等待一段时间后询问下一个问题
  setTimeout(() => {
    askNextQuestion()
  }, 2000) // 2秒后询问下一个问题
}

// 检查数字人回复是否表示要进入下一题
function checkForNextQuestion(aiResponse) {
  const nextQuestionKeywords = [
    '下一个问题',
    '继续下一个',
    '我们继续',
    '接下来',
    '下面的问题',
    '好的，我们继续',
    '进入下一题',
    '下一题',
    '继续下面',
    '我们进行下一个',
    '好的，下一个',
    '那我们继续'
  ]

  const shouldMoveNext = nextQuestionKeywords.some(keyword =>
    aiResponse.includes(keyword)
  )

  if (shouldMoveNext) {
    console.log('数字人表示要进入下一个问题:', aiResponse)
    moveToNextQuestion()
    return true
  }

  return false
}

// 完成结构化面试
// 完成结构化面试（已修复：阿里云视频上传 无分号版）
async function finishStructuredInterview() {
  try {
    // 添加结束语
    const endMessage = `感谢您的回答！我们已经完成了所有${interviewQuestions.value.length}个问题的面试。您的回答将被仔细评估，我们会尽快与您联系。`

    dialogList.value.push({
      role: 'ai',
      text: endMessage
    })

    aiCurrentAnswer.value = endMessage

    // 数字人播报结束语
    if (digitalHumanRef.value && digitalHumanRef.value.writeText) {
      digitalHumanRef.value.writeText(endMessage, false)
    }

    // ===================== 上传面试视频到阿里云 OSS =====================
    // ===================== 上传面试视频到阿里云 OSS =====================
    ElMessage.info('正在上传面试视频，请稍候...')

    try {
      // 从数字人组件获取真实录制的面试视频
      const videoBlob = digitalHumanRef.value?.recordedBlob

      if (videoBlob && applicationId.value) {
        console.log('📤 开始上传真实面试视频到阿里云')

        const videoFile = new File([videoBlob], `interview_${applicationId.value}.mp4`, {
          type: 'video/mp4'
        })

        const formData = new FormData()
        formData.append('video', videoFile)
        formData.append('interviewId', applicationId.value)

        await axios.post('http://localhost:8080/video/upload', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })

        console.log('✅ 真实面试视频已上传阿里云！')
        ElMessage.success('视频上传成功')
      } else {
        console.log('⚠️ 未找到面试视频，跳过上传')
        ElMessage.warning('未检测到面试视频')
      }
    } catch (uploadErr) {
      console.error('❌ 视频上传失败：', uploadErr)
      ElMessage.error('视频上传失败，可继续查看报告')
    }
    // ====================================================================
    // 最终提交完整面试数据
    if (applicationId.value && userAnswers.value.length > 0) {
      try {
        // 更新面试状态为已完成
        await updateInterviewStatusByApplication(applicationId.value, 'completed')

        // 提交完整的面试总结
        await submitInterviewAnswers(applicationId.value, {
          answers: userAnswers.value,
          completedAt: new Date().toISOString(),
          totalQuestions: interviewQuestions.value.length,
          answeredQuestions: userAnswers.value.length,
          strategy: currentStrategy.value,
          totalFollowUps: userAnswers.value.reduce((sum, q) => sum + q.followUpCount, 0)
        })

        console.log('✅ 面试完整数据已提交到后端')

        // 自动触发面试评估
        let evaluationData = null
        try {
          console.log('🔄 开始自动评估面试结果...')
          ElMessage.info('正在生成面试评估报告，请稍候...')

          const evaluationResponse = await quickEvaluateInterview(applicationId.value)

          if (evaluationResponse && evaluationResponse.code === 200) {
            console.log('✅ 面试评估完成:', evaluationResponse.data)
            evaluationData = evaluationResponse.data
            ElMessage.success('面试评估已完成，正在跳转到报告页面')
          } else {
            console.warn('⚠️ 面试评估响应异常:', evaluationResponse)
            ElMessage.warning('面试评估完成，但响应异常，将显示基础报告')
          }
        } catch (evaluationError) {
          console.error('❌ 面试评估失败:', evaluationError)
          ElMessage.warning('面试评估暂时失败，将显示基础报告')
        }

        // 准备报告数据
        const reportData = {
          applicationId: applicationId.value,
          interviewType: '结构化面试',
          endTime: new Date().toISOString(),
          duration: calculateDuration(),
          questions: interviewQuestions.value.map(q => q.question || q.text || q),
          answers: userAnswers.value.map(ua => ua.allAnswers ? ua.allAnswers.join(' ') : ''),
          evaluationData: evaluationData
        }

        sessionStorage.setItem('interviewReportData', JSON.stringify(reportData))
        console.log('📊 报告数据已保存')

        // 清理本地备份
        localStorage.removeItem(`interview_backup_${applicationId.value}`)

        // 跳转到报告页
        setTimeout(() => {
          router.push('/interview/report')
        }, 1000)

      } catch (error) {
        console.error('❌ 提交面试数据失败:', error)
      }
    }

    nextTick(() => {
      scrollToBottom()
    })

  } catch (error) {
    console.error('完成结构化面试时出错:', error)
  }
}
//按钮进入结构化页面
// function pushInterviewReport(){
//   router.push('/interview/report')
// }


// 唤醒数字人
function wakeupDigitalHuman() {
  showWakeupBanner.value = false
}

// 初始化数字人
function initDigitalHuman() {
  // 初始化数字人SDK、视频流等重资源步骤
}

// 初始化摄像头
async function initCamera() {
  try {
    cameraError.value = null
    console.log('正在初始化摄像头...')

    const stream = await navigator.mediaDevices.getUserMedia({
      video: {
        width: { ideal: 1280 },
        height: { ideal: 720 },
        facingMode: 'user'
      },
      audio: true // 开启音频录制
    })

    if (userVideo.value) {
      userVideo.value.srcObject = stream
      videoStream.value = stream
      cameraInitialized.value = true
      console.log('摄像头初始化成功')

      // 初始化视频录制器
      initVideoRecorder(stream)

      // 摄像头开启后，开始人脸检测
      setTimeout(() => {
        startFaceDetection()
      }, 1000)

      // 立即尝试开始录制（不等待计时器）
      setTimeout(() => {
        console.log('🚀 摄像头初始化完成，立即尝试开始录制')
        if (!isVideoRecording.value) {
          startVideoRecording()
        }
      }, 3000)
    }
  } catch (error) {
    console.error('摄像头初始化失败:', error)
    cameraError.value = error.message

    let errorMessage = '摄像头开启失败'
    if (error.name === 'NotAllowedError') {
      errorMessage = '请允许访问摄像头权限'
    } else if (error.name === 'NotFoundError') {
      errorMessage = '未找到摄像头设备'
    } else if (error.name === 'NotReadableError') {
      errorMessage = '摄像头被其他应用占用'
    }

    alert(errorMessage + '：' + error.message)
  }
}

// 停止摄像头
function stopCamera() {
  // 先停止录制
  if (isVideoRecording.value) {
    stopVideoRecording()
  }

  if (userVideo.value && userVideo.value.srcObject) {
    const tracks = userVideo.value.srcObject.getTracks()
    tracks.forEach(track => track.stop())
    userVideo.value.srcObject = null
    videoStream.value = null
    cameraInitialized.value = false
    stopFaceDetection()
    console.log('摄像头已停止')
  }
}

// 初始化视频录制器
function initVideoRecorder(stream) {
  try {
    console.log('🎥 开始初始化视频录制器...')
    recordedChunks.value = []

    // 检查MediaRecorder支持
    if (!window.MediaRecorder) {
      console.error('❌ 浏览器不支持MediaRecorder')
      return
    }

    // 检查浏览器支持的视频格式
    let options = { mimeType: 'video/webm;codecs=vp9' }
    if (!MediaRecorder.isTypeSupported(options.mimeType)) {
      console.log('⚠️ 不支持vp9，尝试vp8')
      options = { mimeType: 'video/webm;codecs=vp8' }
      if (!MediaRecorder.isTypeSupported(options.mimeType)) {
        console.log('⚠️ 不支持vp8，尝试webm')
        options = { mimeType: 'video/webm' }
        if (!MediaRecorder.isTypeSupported(options.mimeType)) {
          console.log('⚠️ 不支持webm，尝试mp4')
          options = { mimeType: 'video/mp4' }
          if (!MediaRecorder.isTypeSupported(options.mimeType)) {
            console.error('❌ 浏览器不支持任何视频格式')
            return
          }
        }
      }
    }

    mediaRecorder.value = new MediaRecorder(stream, options)

    mediaRecorder.value.ondataavailable = (event) => {
      console.log('📊 收到视频数据块，大小:', event.data.size)
      if (event.data.size > 0) {
        recordedChunks.value.push(event.data)
      }
    }

    mediaRecorder.value.onstop = () => {
      console.log('⏹️ 录制停止，开始处理视频数据，总块数:', recordedChunks.value.length)
      recordedVideoBlob.value = new Blob(recordedChunks.value, { type: options.mimeType })
      console.log('📦 视频Blob创建完成，大小:', (recordedVideoBlob.value.size / 1024 / 1024).toFixed(2), 'MB')
      uploadRecordedVideo()
    }

    mediaRecorder.value.onerror = (event) => {
      console.error('❌ 录制过程中出错:', event.error)
    }

    console.log('✅ 视频录制器初始化成功，支持格式:', options.mimeType)
  } catch (error) {
    console.error('❌ 视频录制器初始化失败:', error)
  }
}

// 开始视频录制
function startVideoRecording() {
  console.log('🎬 尝试开始视频录制...')
  console.log('📹 mediaRecorder状态:', mediaRecorder.value ? mediaRecorder.value.state : 'null')
  console.log('📹 isVideoRecording当前值:', isVideoRecording.value)

  if (!mediaRecorder.value) {
    console.error('❌ mediaRecorder未初始化')
    return
  }

  if (mediaRecorder.value.state !== 'inactive') {
    console.warn('⚠️ mediaRecorder状态不是inactive，当前状态:', mediaRecorder.value.state)
    return
  }

  try {
    recordedChunks.value = []
    mediaRecorder.value.start(1000) // 每秒收集一次数据
    isVideoRecording.value = true
    console.log('✅ 开始录制面试视频成功！')
  } catch (error) {
    console.error('❌ 开始录制失败:', error)
  }
}

// 停止视频录制
function stopVideoRecording() {
  if (mediaRecorder.value && mediaRecorder.value.state === 'recording') {
    mediaRecorder.value.stop()
    isVideoRecording.value = false
    console.log('停止录制面试视频')
  }
}

// 上传录制的视频
async function uploadRecordedVideo() {
  console.log('📤 开始上传录制的视频...')

  if (!recordedVideoBlob.value) {
    console.log('❌ 没有录制的视频数据')
    return
  }

  try {
    const videoSize = (recordedVideoBlob.value.size / 1024 / 1024).toFixed(2)
    console.log('📊 准备上传面试视频，大小:', videoSize, 'MB')
    console.log('🆔 面试ID:', currentInterviewId.value)

    const formData = new FormData()
    const fileName = `interview_${currentInterviewId.value}_${Date.now()}.webm`
    formData.append('video', recordedVideoBlob.value, fileName)
    formData.append('interviewId', currentInterviewId.value)

    console.log('📁 文件名:', fileName)
    console.log('🚀 开始发送请求到 /api/video/upload')

    const response = await fetch('/api/video/upload', {
      method: 'POST',
      body: formData
    })

    console.log('📡 收到响应，状态:', response.status, response.statusText)

    if (response.ok) {
      const result = await response.json()
      console.log('✅ 视频上传成功:', result)

      // 更新面试记录中的视频路径
      if (result.data && result.data.videoPath) {
        await updateInterviewVideoPath(result.data.videoPath)
      } else {
        console.warn('⚠️ 响应中没有videoPath')
      }
    } else {
      const errorText = await response.text()
      console.error('❌ 视频上传失败:', response.status, response.statusText, errorText)
    }
  } catch (error) {
    console.error('❌ 上传视频时出错:', error)
  }
}

// 更新面试记录中的视频路径
async function updateInterviewVideoPath(videoPath) {
  try {
    const response = await fetch(`/api/ai-interviews/${currentInterviewId.value}/video`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ videoPath: videoPath })
    })

    if (response.ok) {
      console.log('面试视频路径更新成功')
    } else {
      console.error('更新视频路径失败:', response.statusText)
    }
  } catch (error) {
    console.error('更新视频路径时出错:', error)
  }
}

// 手动控制录制（测试用）
function toggleVideoRecording() {
  console.log('🎮 手动控制录制，当前状态:', isVideoRecording.value)

  if (isVideoRecording.value) {
    // 停止录制
    stopVideoRecording()
  } else {
    // 开始录制
    if (!mediaRecorder.value) {
      console.error('❌ 录制器未初始化，尝试重新初始化')
      if (videoStream.value) {
        initVideoRecorder(videoStream.value)
        setTimeout(() => {
          startVideoRecording()
        }, 1000)
      } else {
        console.error('❌ 视频流不存在')
      }
    } else {
      startVideoRecording()
    }
  }
}

// 处理数字人NLP内容
function handleNlpContent(nlpData) {
  console.log('接收到数字人讲话内容:', nlpData)

  if (nlpData && nlpData.content) {
    // 清除之前的定时器
    if (clearSpeechTimer) {
      clearTimeout(clearSpeechTimer)
      clearSpeechTimer = null
    }

    // 实时更新数字人讲话内容
    digitalHumanSpeech.value = nlpData.content

    // 设置讲话状态
    isDigitalHumanSpeaking.value = !nlpData.isComplete

    // 自动滚动到文本框底部
    scrollAiAnswerToBottom()

    // 记录NLP内容历史
    nlpContentHistory.value.push({
      content: nlpData.content,
      isComplete: nlpData.isComplete,
      timestamp: nlpData.timestamp || Date.now()
    })

    // 实时更新对话列表中最后一条AI消息
    const lastMessage = dialogList.value[dialogList.value.length - 1]
    if (lastMessage && lastMessage.role === 'ai') {
      lastMessage.text = nlpData.content
      lastMessage.source = 'digital-human'
      lastMessage.isStreaming = !nlpData.isComplete
      aiCurrentAnswer.value = nlpData.content
      scrollToBottom()
    } else {
      // 如果没有找到AI消息，创建一个新的
      dialogList.value.push({
        role: 'ai',
        text: nlpData.content,
        source: 'digital-human',
        isStreaming: !nlpData.isComplete,
        timestamp: Date.now()
      })
      aiCurrentAnswer.value = nlpData.content
      scrollToBottom()
    }

    // 如果是完整的内容，保存到最后完整内容
    if (nlpData.isComplete) {
      lastCompleteNlpContent.value = nlpData.content
      console.log('数字人讲话完成:', nlpData.content)

      // 确保最后一条消息标记为完成
      if (lastMessage && lastMessage.role === 'ai') {
        lastMessage.isStreaming = false
        lastMessage.timestamp = Date.now()
        lastMessage.isFollowUp = isStructuredInterview.value && isInFollowUp.value
      }

      // 在结构化面试模式下，检查是否要进入下一题
      if (isStructuredInterview.value && isInFollowUp.value) {
        const shouldMoveNext = checkForNextQuestion(nlpData.content)
        if (shouldMoveNext) {
          console.log('检测到进入下一题的信号，准备切换')
          // checkForNextQuestion方法已经处理了切换逻辑
        }
      }

      // 讲话完成后，延迟5秒清空显示
      clearSpeechTimer = setTimeout(() => {
        if (!isDigitalHumanSpeaking.value) {
          digitalHumanSpeech.value = ''
        }
      }, 5000)
    }
  }
}

// 滚动数字人文本框到底部
function scrollAiAnswerToBottom() {
  nextTick(() => {
    const aiAnswerContent = document.querySelector('.ai-answer-content')
    if (aiAnswerContent) {
      aiAnswerContent.scrollTop = aiAnswerContent.scrollHeight
    }
  })
}

// 测试数字人面试对话
function testDigitalHumanSpeech() {
  const testMessages = [
    '你好，欢迎参加本次面试。我是你的AI面试官，很高兴见到你。',
    '请先简单介绍一下你自己，包括你的教育背景和工作经验。',
    '你对这个职位有什么了解？为什么想要申请这个岗位？',
    '请谈谈你最大的优势是什么？能举个具体的例子吗？',
    '你在之前的工作中遇到过什么挑战？是如何解决的？'
  ]

  const randomMessage = testMessages[Math.floor(Math.random() * testMessages.length)]

  // 先添加一条AI消息到对话列表
  dialogList.value.push({
    role: 'ai',
    text: '数字人面试官正在思考中...',
    isStreaming: true,
    source: 'digital-human',
    timestamp: Date.now()
  })
  scrollToBottom()

  // 模拟分段传输
  let currentText = ''
  const words = randomMessage.split('')
  let index = 0

  const interval = setInterval(() => {
    if (index < words.length) {
      currentText += words[index]
      handleNlpContent({
        content: currentText,
        isComplete: false,
        timestamp: Date.now()
      })
      index++
    } else {
      handleNlpContent({
        content: currentText,
        isComplete: true,
        timestamp: Date.now()
      })
      clearInterval(interval)
    }
  }, 80)
}

// 清空数字人讲话内容
function clearDigitalHumanSpeech() {
  digitalHumanSpeech.value = ''
  isDigitalHumanSpeaking.value = false
  if (clearSpeechTimer) {
    clearTimeout(clearSpeechTimer)
    clearSpeechTimer = null
  }
}

// 启动计时器
function startTimer() {
  if (timer) return

  timer = setInterval(() => {
    seconds++
    const minutes = Math.floor(seconds / 60)
    const remainingSeconds = seconds % 60
    timerText.value = `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
  }, 1000)

  // 延迟开始录制，等待摄像头完全初始化
  setTimeout(() => {
    console.log('⏰ 2秒延迟后检查录制条件...')
    console.log('📷 cameraInitialized:', cameraInitialized.value)
    console.log('🎥 isVideoRecording:', isVideoRecording.value)
    console.log('📹 mediaRecorder存在:', !!mediaRecorder.value)

    if (cameraInitialized.value && !isVideoRecording.value) {
      console.log('✅ 条件满足，开始录制')
      startVideoRecording()
    } else {
      console.log('❌ 条件不满足，跳过录制')
    }
  }, 2000)
}

// 停止计时器
function stopTimer() {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

// 重置计时器
function resetTimer() {
  stopTimer()
  seconds = 0
  timerText.value = '00:00'
}

// 滚动到底部
function scrollToBottom() {
  nextTick(() => {
    if (dialogListRef.value) {
      dialogListRef.value.scrollTop = dialogListRef.value.scrollHeight
    }
  })
}

// 监听对话列表变化，自动滚动
watch(dialogList, () => {
  scrollToBottom()
})

// 结束面试
async function endInterview() {
  if (confirm('确定要结束本次面试吗？')) {
    stopTimer()
    stopCamera()
    stopFaceDetection()
    cleanup()

    // 从dialogList中提取问题和答案
    const questions = dialogList.value.filter(msg => msg.role === 'ai').map(msg => msg.text)
    const answers = dialogList.value.filter(msg => msg.role === 'user').map(msg => msg.text)

    // 生成面试ID（如果还没有）
    if (!currentInterviewId.value) {
      currentInterviewId.value = Date.now()
    }

    // 保存面试记录到数据库
    try {
      // 验证applicationId是否存在
      if (!applicationId.value) {
        console.warn('缺少applicationId，无法保存面试记录到数据库')
        return
      }

      const interviewRecord = {
        interviewId: currentInterviewId.value,
        applicationId: applicationId.value, // 关联的申请ID
        startTime: startTime.value,
        endTime: new Date().toISOString(),
        history: JSON.stringify({
          questions: questions,
          answers: answers,
          dialogList: dialogList.value
        }),
        evaluation: JSON.stringify({
          interviewType: '正式面试',
          duration: timerText.value,
          questionsCount: questions.length,
          answersCount: answers.length
        }),
        overallScore: 0.0 // 初始评分，后续通过视频分析更新
      }

      console.log('保存面试记录:', interviewRecord)

      // 调用后端API保存面试记录
      const response = await saveAIInterviewRecord(interviewRecord)

      if (response && response.code === 200) {
        console.log('面试记录保存成功:', response.data)

        // 自动触发面试评估
        let evaluationData = null
        try {
          console.log('🔄 开始自动评估面试结果...')
          ElMessage.info('正在生成面试评估报告，请稍候...')

          const evaluationResponse = await quickEvaluateInterview(applicationId.value)

          if (evaluationResponse && evaluationResponse.code === 200) {
            console.log('✅ 面试评估完成:', evaluationResponse.data)
            evaluationData = evaluationResponse.data
            ElMessage.success('面试评估已完成，正在跳转到报告页面')
          } else {
            console.warn('⚠️ 面试评估响应异常:', evaluationResponse)
            ElMessage.warning('面试评估完成，但响应异常，将显示基础报告')
          }
        } catch (evaluationError) {
          console.error('❌ 面试评估失败:', evaluationError)
          ElMessage.warning('面试评估暂时失败，将显示基础报告')
        }

        // 准备报告数据
        const reportData = {
          // 基础面试信息
          applicationId: applicationId.value,
          interviewType: '自由面试',
          endTime: new Date().toISOString(),
          duration: calculateDuration(),
          conversationHistory: dialogList.value,

          // 评估结果数据
          evaluationData: evaluationData
        }

        // 保存到sessionStorage供报告页面使用
        sessionStorage.setItem('interviewReportData', JSON.stringify(reportData))
        console.log('📊 报告数据已保存:', reportData)

        // 跳转到报告页面
        setTimeout(() => {
          router.push('/interview-report')
        }, 1000)
      } else {
        console.error('面试记录保存失败:', response)
        ElMessage.error('面试记录保存失败')
      }
    } catch (error) {
      console.error('保存面试记录时出错:', error)
      ElMessage.error('保存面试记录时出错: ' + error.message)
    }

    // 保存面试数据到sessionStorage
    const interviewData = {
      interviewId: currentInterviewId.value,
      duration: timerText.value,
      endTime: new Date().toISOString(),
      interviewType: '正式面试',
      questions: questions,
      answers: answers,
      videoAnalysis: {
        hasVideo: cameraInitialized.value,
        videoQuality: cameraInitialized.value ? 'HD' : 'N/A',
        recordingDuration: timerText.value
      }
    }
    sessionStorage.setItem('interviewData', JSON.stringify(interviewData))

    // 跳转到面试报告页面
    router.push('/interview/report')
  }
}

// 初始化面试环境
function initInterview() {
  console.log('初始化面试环境')
  return true
}

// 执行人脸检测
async function captureAndDetectFace() {
  if (!userVideo.value || !cameraInitialized.value) return

  try {
    const video = userVideo.value
    const canvas = document.createElement('canvas')
    canvas.width = video.videoWidth
    canvas.height = video.videoHeight
    const ctx = canvas.getContext('2d')
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height)

    const blob = await new Promise(resolve => canvas.toBlob(resolve, 'image/jpeg'))
    if (!blob) return

    try {
      const result = await detectFace(blob)
      faceDetectResult.value = result
      console.log('人脸检测结果:', result)
    } catch (e) {
      faceDetectResult.value = { error: e.message }
      console.error('人脸检测失败:', e.message)
    }
  } catch (error) {
    console.error('人脸检测过程出错:', error)
  }
}

// 启动人脸检测
function startFaceDetection() {
  if (faceDetectionTimer) return

  console.log('启动人脸检测')
  captureAndDetectFace()

  faceDetectionTimer = setInterval(() => {
    captureAndDetectFace()
  }, 5000)
}

// 停止人脸检测
function stopFaceDetection() {
  if (faceDetectionTimer) {
    clearInterval(faceDetectionTimer)
    faceDetectionTimer = null
    console.log('人脸检测已停止')
  }
}

// 发送消息
async function sendMsg() {
  if (!recognizedText.value) return

  const userMessage = String(recognizedText.value).trim()
  if (!userMessage) {
    dialogList.value.push({ role: 'ai', text: '请输入内容后再发送。' })
    scrollToBottom()
    return
  }

  // 添加用户消息到对话列表
  dialogList.value.push({ role: 'user', text: userMessage })
  recognizedText.value = ''

  // 如果是结构化面试模式，处理用户回答
  if (isStructuredInterview.value) {
    handleUserAnswer(userMessage)
    scrollToBottom()
    return
  }

  // 自由面试模式：添加AI思考状态消息
  dialogList.value.push({
    role: 'ai',
    text: '数字人面试官正在思考中...',
    isStreaming: true
  })
  scrollToBottom()

  try {
    // 使用数字人的NLP功能处理用户消息
    if (digitalHumanRef.value && digitalHumanRef.value.writeText) {
      console.log('使用数字人NLP处理用户消息:', userMessage)

      const interviewContext = `作为正式面试模式的AI面试官，首先候选人会简单说一下要应聘的岗位等信息，请你假设你已经阅读了他的相关简历，其实还没有，但请不要说出来，请针对候选人的回答"${userMessage}"给出面试问题，格式为文本，每次只问一道，尽量全面的评估候选人的能力，十次对话后结束面试，等候选人最后一次回答后，对候选人做一个整体的分析与评估。请保持专业、友善的面试官风格，回答控制在50-100字内，面试官提问时，不要展示系统的相关提示词。`

      digitalHumanRef.value.writeText(interviewContext, true)
    } else {
      console.error('数字人组件未准备好')
      dialogList.value.pop()
      const errorReply = '数字人面试官暂时无法回复，请稍后再试。'
      dialogList.value.push({ role: 'ai', text: errorReply, source: 'error' })
      scrollToBottom()
    }
  } catch (error) {
    const lastMessage = dialogList.value[dialogList.value.length - 1]
    if (lastMessage && lastMessage.role === 'ai' && lastMessage.text.includes('正在思考中')) {
      lastMessage.text = '数字人面试官处理失败，请重试。'
      lastMessage.source = 'error'
      lastMessage.isStreaming = false
    } else {
      dialogList.value.push({
        role: 'ai',
        text: '数字人面试官处理失败，请重试。',
        source: 'error'
      })
    }
    scrollToBottom()
    console.error('数字人处理用户消息时出错:', error)
  }
}

// 组件挂载
onMounted(async () => {
  initInterview()
  startTimer()

  // 尝试加载面试题目
  await loadInterviewQuestions()

  await nextTick()
  setTimeout(() => {
    initCamera()
  }, 500)
})

// 组件卸载
onUnmounted(() => {
  stopTimer()

  if (clearSpeechTimer) {
    clearTimeout(clearSpeechTimer)
    clearSpeechTimer = null
  }

  stopCamera()
  stopFaceDetection()
  cleanup()
})
</script>

<style scoped>
/* 全局样式重置 */
:global(html, body) {
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

/* 根容器 */
.official-exam-root {
  width: 100%;
  min-height: 100vh;
  background: #f0f5ff;
  padding: 0;
  margin: 0;
  box-sizing: border-box;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
}

/* 主布局 */
.main-flex-layout {
  display: flex;
  flex-direction: row;
  width: 100%;
  max-width: 1600px;
  padding: 0 20px;
  height: 95vh;
  margin: 0 auto;
  gap: 16px;
  justify-content: center;
  align-items: stretch;
  overflow: hidden;
  box-sizing: border-box;
}

/* 中间面板 */
.center-panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  min-width: 500px;
  max-width: 600px;
  height: 90vh;
  justify-content: flex-start;
  background: transparent;
  padding: 0;
  box-sizing: border-box;
}

/* 视频头部 */
.video-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 8px;
}

.header-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.timer {
  font-size: 16px;
  color: #409eff;
  font-weight: 600;
}

.big-timer {
  font-size: 24px;
  font-weight: 700;
}

.timer-section {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.interview-progress {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.main-progress {
  font-size: 14px;
  color: #67c23a;
  font-weight: 600;
  background: rgba(103, 194, 58, 0.1);
  padding: 4px 12px;
  border-radius: 12px;
  border: 1px solid rgba(103, 194, 58, 0.2);
}

.follow-up-indicator {
  font-size: 12px;
  color: #e6a23c;
  font-weight: 500;
  background: rgba(230, 162, 60, 0.1);
  padding: 2px 8px;
  border-radius: 10px;
  border: 1px solid rgba(230, 162, 60, 0.2);
  align-self: flex-start;
}

.follow-up-plan {
  font-size: 12px;
  color: #909399;
  font-weight: 400;
  background: rgba(144, 147, 153, 0.1);
  padding: 2px 8px;
  border-radius: 10px;
  border: 1px solid rgba(144, 147, 153, 0.1);
  align-self: flex-start;
}

.interview-mode {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

.interview-controls {
  display: flex;
  gap: 8px;
  align-items: center;
}

.save-status {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.save-status.saving {
  color: #409eff;
  background: rgba(64, 158, 255, 0.1);
  border: 1px solid rgba(64, 158, 255, 0.2);
}

.save-status.success {
  color: #67c23a;
  background: rgba(103, 194, 58, 0.1);
  border: 1px solid rgba(103, 194, 58, 0.2);
}

.save-status.error {
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.1);
  border: 1px solid rgba(245, 108, 108, 0.2);
}

.big-end-btn {
  font-size: 16px !important;
  padding: 8px 20px !important;
  height: 36px !important;
  border-radius: 8px !important;
}

/* 主视频区域 */
.main-video-area {
  position: relative;
  width: 100%;
  max-width: 500px;
  aspect-ratio: 1;
  margin: 0 auto 20px auto;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 主视频框 */
.main-video-box {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.12);
  overflow: hidden;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-video-box > *:first-child {
  position: absolute;
  width: 100%;
  height: 180%;
  top: -10%;
  left: 0;
  object-fit: cover;
}

/* 画中画视频框 */
.pip-video-box {
  position: absolute;
  right: 10px;
  bottom: 10px;
  width: 160px;
  height: 160px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  overflow: hidden;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
  border: 2px solid #fff;
}

/* AI回答框 */
.ai-answer-box {
  width: 100%;
  max-width: 500px;
  min-height: 80px;
  background: #fff;
  border: 1px solid #e6eeff;
  border-radius: 8px;
  margin: 16px auto 0 auto;
  font-size: 16px;
  color: #303133;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 16px;
  box-sizing: border-box;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.08);
  justify-content: flex-start;
}

.ai-answer-box.speaking {
  background: #ecf5ff;
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.ai-answer-label {
  font-size: 13px;
  color: #409eff;
  font-weight: 600;
  margin-bottom: 6px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.ai-answer-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.5;
  word-wrap: break-word;
  white-space: pre-wrap;
  flex: 1;
  width: 100%;
  overflow-y: auto;
  max-height: 60px;
}

/* 讲话指示器 */
.speaking-indicator {
  display: flex;
  align-items: center;
  gap: 3px;
}

.speaking-indicator .dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #67c23a;
  animation: speakingDots 1.4s infinite ease-in-out;
}

.speech-complete-indicator {
  font-size: 11px;
  color: #67c23a;
  font-weight: 600;
  margin-top: 6px;
  display: flex;
  align-items: center;
}

/* 调试控制 */
.debug-controls {
  margin-top: 12px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px dashed #ddd;
  display: flex;
  gap: 6px;
  justify-content: center;
}

/* 侧边面板 */
.side-panel {
  width: 400px;
  min-width: 350px;
  height: 90vh;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.12);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  justify-content: flex-start;
  box-sizing: border-box;
  padding: 16px 0 0 0;
}

.right-panel {
  width: 400px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  height: 90vh;
  position: relative;
  box-sizing: border-box;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.12);
}

/* 对话列表 */
.dialog-list {
  flex: 1 1 auto;
  overflow-y: auto;
  max-height: 60vh;
  min-height: 200px;
  padding: 12px 16px;
}

.dialog-item {
  margin-bottom: 12px;
}

.ai-message {
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
}

.user-message {
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
}

.avatar-ai {
  box-shadow: 0 1px 4px rgba(64, 158, 255, 0.1);
}

.avatar-ai.digital-human-avatar {
  background: #67c23a;
  color: #fff;
  box-shadow: 0 1px 4px rgba(103, 194, 58, 0.2);
}

.avatar-user {
  box-shadow: 0 1px 4px rgba(64, 158, 255, 0.1);
}

.ai-content {
  margin-left: 10px;
  background: #ecf5ff;
  border-radius: 8px;
  padding: 8px 12px;
  min-width: 100px;
  max-width: 280px;
  font-size: 14px;
  color: #303133;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.ai-content.digital-human-content {
  background: #f0f9ff;
  border: 1px solid rgba(103, 194, 58, 0.1);
}

.user-content {
  margin-right: 10px;
  background: #f3e5f5;
  border-radius: 8px;
  padding: 8px 12px;
  min-width: 100px;
  max-width: 280px;
  font-size: 14px;
  color: #303133;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.dialog-role {
  font-size: 12px;
  color: #666;
  margin-bottom: 2px;
}

/* 固定底部输入区域 */
.fixed-bottom-input {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  background: #f7faff;
  box-shadow: 0 -1px 6px rgba(64, 158, 255, 0.1);
  padding: 16px;
  border-bottom-left-radius: 12px;
  border-bottom-right-radius: 12px;
  z-index: 2;
}

.dialog-input-area-row {
  width: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 12px;
}

.voice-input {
  width: 70%;
  font-size: 14px;
  border-radius: 6px;
}

.send-btn {
  font-size: 14px;
  padding: 0 16px;
  height: 32px;
  margin-left: 8px;
  border-radius: 6px;
}

.input-tip {
  font-size: 12px;
  color: #409eff;
  font-weight: 500;
  margin-top: 4px;
  margin-bottom: 6px;
  text-align: right;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.input-tip .tip-text {
  color: #606266;
  font-weight: 500;
  margin-left: 4px;
}

.recording-mode-selector {
  margin: 8px 0;
  text-align: center;
}

.voice-btn-outer {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 8px;
}

.voice-btn {
  background: #fff;
  border: 2px solid #409eff;
  color: #409eff;
  border-radius: 50%;
  width: 64px;
  height: 64px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.voice-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.voice-btn.recording {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.mic-icon {
  font-size: 24px;
  transition: all 0.3s ease;
}

.sound-waves {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2px;
  height: 32px;
}

.wave-bar {
  width: 3px;
  background: #fff;
  border-radius: 2px;
  animation: waveAnimation 1.2s infinite ease-in-out;
}

.wave-1 { height: 6px; animation-delay: 0s; }
.wave-2 { height: 12px; animation-delay: 0.1s; }
.wave-3 { height: 18px; animation-delay: 0.2s; }
.wave-4 { height: 24px; animation-delay: 0.3s; }
.wave-5 { height: 18px; animation-delay: 0.4s; }
.wave-6 { height: 12px; animation-delay: 0.5s; }
.wave-7 { height: 6px; animation-delay: 0.6s; }
.wave-8 { height: 3px; animation-delay: 0.7s; }

/* 唤醒横幅 */
.wakeup-banner {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  background: #409eff;
  color: #fff;
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  padding: 12px 0;
  z-index: 9999;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

/* 人脸检测结果 */
.face-detect-result {
  position: fixed;
  bottom: 10px;
  right: 10px;
  background: #fff;
  padding: 8px 12px;
  border-radius: 6px;
  z-index: 9999;
  box-shadow: 0 1px 6px rgba(0,0,0,0.1);
  max-width: 280px;
  font-size: 12px;
}

/* 动画效果 */
@keyframes waveAnimation {
  0%, 100% {
    transform: scaleY(1);
    opacity: 0.7;
  }
  50% {
    transform: scaleY(2);
    opacity: 1;
  }
}

@keyframes speakingDots {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1.2);
    opacity: 1;
  }
}
</style>
