<template>
  <div class="simulat-exam-root">
    <div v-if="showWakeupBanner" class="wakeup-banner">请点击页面任意位置唤醒AI面试官</div>
    <div class="main-flex-layout">
      <!-- 左侧：历史对话区 -->
      <div class="side-panel history-panel">
        <div class="history-header">
          <el-icon class="history-icon"><ChatDotRound /></el-icon>
          <span class="history-title">对话历史</span>
        </div>
        <div class="history-list" ref="historyListRef">
          <div v-for="(msg, idx) in dialogList" :key="idx" :class="['history-item', msg.role]">
            <div class="history-role">{{ msg.role === 'ai' ? '面试官' : msg.role === 'user' ? '你' : 'AI' }}</div>
            <div class="history-text">{{ msg.text.length > 50 ? msg.text.slice(0, 50) + '...' : msg.text }}</div>
            <div class="history-time">{{ getTimeFromIndex(idx) }}</div>
          </div>
        </div>
      </div>

      <!-- 中间：视频区+AI回答/参考答案 -->
      <div class="center-panel">
        <div class="video-header">
          <span class="timer big-timer">面试计时：{{ timerText }}</span>
          <div class="header-buttons">
            <el-button type="success" @click="initCamera" size="small">开启摄像头</el-button>
            <el-button type="primary" @click="testDigitalHuman" size="small">测试数字人</el-button>
            <el-button type="danger" @click="endInterview" size="large" class="big-end-btn">结束面试</el-button>
          </div>
        </div>
        <div class="main-video-area">
          <div class="main-video-box" @click="isSwapped = !isSwapped">
            <template v-if="!isSwapped">
              <DigitalHuman ref="digitalHumanRef" @nlp-content="handleNlpContent" :avatar-config="avatarConfig" />
            </template>
            <template v-else>
              <video ref="userVideo" autoplay muted playsinline class="user-video"></video>
            </template>
            <div class="pip-video-box" @click.stop="isSwapped = !isSwapped">
              <template v-if="!isSwapped">
                <video ref="userVideo" autoplay muted playsinline class="user-video"></video>
              </template>
              <template v-else>
                <!-- 小窗口中的数字人不需要监听nlp-content，避免重复处理 -->
                <DigitalHuman :avatar-config="avatarConfig" />
              </template>
            </div>
          </div>
        </div>
        <div class="ai-answer-box">
          {{ currentDigitalHumanText || aiCurrentAnswer }}
        </div>
        <div class="ai-ref-answer-box">
          <el-icon style="color:#67C23A;margin-right:8px;"><i class="el-icon-lightning"></i></el-icon>
          <span class="ref-title">语音分析：</span>
          <span class="ref-content">{{ aiRefAnswer }}</span>
        </div>
      </div>

      <!-- 右侧：AI提示卡片 -->
      <div class="side-panel right-panel">
        <!-- 表情分析提示区域 -->
        <div class="emotion-analysis-bar">
          <el-icon style="color:#F56C6C;margin-right:4px;"><i class="el-icon-view"></i></el-icon>
          <span>表情分析：</span>
          <span class="emotion-content" :class="emotionAnalysis.level">{{ emotionAnalysis.text }}</span>
          <div class="emotion-details">
            <span class="emotion-score">置信度: {{ emotionAnalysis.confidence }}%</span>
          </div>
        </div>

        <!-- 人脸属性检测提示区域 -->
        <div class="face-attribute-bar">
          <el-icon style="color:#67C23A;margin-right:4px;"><i class="el-icon-user"></i></el-icon>
          <span>状态检测：</span>
          <div class="attribute-list">
            <div class="attribute-item">
              <span class="attr-label">注意力：</span>
              <span class="attr-value" :class="faceAttributes.attention.level">{{ faceAttributes.attention.text }}</span>
            </div>
            <div class="attribute-item">
              <span class="attr-label">姿态：</span>
              <span class="attr-value" :class="faceAttributes.posture.level">{{ faceAttributes.posture.text }}</span>
            </div>
            <div class="attribute-item">
              <span class="attr-label">眼神：</span>
              <span class="attr-value" :class="faceAttributes.eyeContact.level">{{ faceAttributes.eyeContact.text }}</span>
            </div>
          </div>
        </div>

        <!-- 表情提示区域 -->
        <div class="ai-hint-bar">
          <el-icon style="color:#E6A23C;margin-right:4px;"><i class="el-icon-smile"></i></el-icon>
          <span>面试建议：</span>
          <span class="ai-hint-content">{{ aiHint }}</span>
        </div>

        <!-- AI答案区域 -->
        <div class="ai-answer-hint-bar">
          <div class="ai-answer-header">
            <el-icon style="color:#409EFF;margin-right:6px;"><i class="el-icon-chat-dot-round"></i></el-icon>
            <span>AI答案：</span>
            <span v-if="isDigitalHumanSpeaking" class="speaking-indicator">正在讲话...</span>
          </div>
          <div class="ai-answer-content">{{ currentDigitalHumanText || aiCurrentAnswer }}</div>
        </div>
        <!-- 固定底部输入区 -->
        <div class="fixed-bottom-input" :class="{ 'collapsed': !isInputExpanded }">
          <!-- 主要输入区域 -->
          <div class="input-main-area">
            <div class="dialog-input-area-row">
              <!-- 伸缩控制按钮 -->
              <div class="expand-toggle-btn-left" @click="toggleInputExpansion">
                <el-icon :class="{ 'rotated': isInputExpanded }">
                  <ArrowUp />
                </el-icon>
              </div>
              <el-input
                  v-model="recognizedText"
                  placeholder="请点击下方语音按钮或手动输入..."
                  class="voice-input"
                  @keydown.enter="sendMsg"
              />
              <!-- 收缩状态下的语音按钮 -->
              <div v-if="!isInputExpanded" class="compact-voice-btn" @click="toggleVoice">
                <el-icon v-if="!isRecording" class="mic-icon"><Microphone /></el-icon>
                <div v-else class="compact-sound-waves">
                  <span v-for="n in 3" :key="n" :class="['compact-wave-bar', 'compact-wave-' + n]"></span>
                </div>
              </div>
              <el-button class="send-btn" type="primary" @click="sendMsg" :disabled="!recognizedText">发送</el-button>
            </div>

            <!-- 展开状态下的额外内容 -->
            <div v-if="isInputExpanded" class="expanded-content">
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
              <div class="voice-control-container">
                <!-- 语音按钮 -->
                <div class="voice-btn-outer">
                  <div class="voice-btn" :class="{recording: isRecording}" @click="toggleVoice">
                    <el-icon v-if="!isRecording" class="mic-icon"><Microphone /></el-icon>
                    <div v-else class="sound-waves">
                      <span v-for="n in 8" :key="n" :class="['wave-bar', 'wave-' + n]"></span>
                    </div>
                  </div>
                </div>

                <!-- 语音分析结果面板 - 绝对定位到右侧 -->
                <div class="voice-analysis-panel-right">
                  <div class="analysis-card-compact">


                    <!-- 音量 -->
                    <div class="analysis-item-compact">
                      <span class="label-compact">音量</span>
                      <span class="value-compact" :class="getVolumeClass(currentVoiceAnalysis.volume)">
                        {{ currentVoiceAnalysis.volume }}
                      </span>
                    </div>

                    <!-- 语速 -->
                    <div class="analysis-item-compact">
                      <span class="label-compact">语速</span>
                      <span class="value-compact" :class="getSpeechRateClass(currentVoiceAnalysis.speech_rate)">
                        {{ currentVoiceAnalysis.speech_rate }}
                      </span>
                    </div>

                    <!-- 情感 -->
                    <div class="analysis-item-compact">
                      <span class="label-compact">情感</span>
                      <span class="value-compact" :class="getEmotionClass(currentVoiceAnalysis.emotion)">
                        {{ extractEmotionContent(currentVoiceAnalysis.emotion) }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 人脸检测结果 -->
    <div v-if="faceDetectResult" style="position:fixed;top:10px;right:10px;background:#fff;padding:8px 16px;border-radius:8px;z-index:9999;box-shadow:0 2px 8px #0002;max-width:320px;">
      <div v-if="faceDetectResult.error" style="color:red;">检测失败：{{ faceDetectResult.error }}</div>
      <div v-else>
        <div>检测到人脸数：{{ faceDetectResult.face_num }}</div>
      </div>
    </div>


  </div>
</template>

<script setup>
/**
 * 模拟面试组件 - 使用数字人内置NLP功能
 *
 * 工作流程：
 * 1. 用户输入消息 -> sendMsg()
 * 2. 直接将用户消息传递给数字人的writeText()方法，启用NLP功能
 * 3. 数字人内置的大模型处理用户输入并生成智能回复
 * 4. 通过handleNlpContent()接收数字人的NLP输出
 * 5. 实时显示数字人的回复内容并更新对话历史
 *
 * 注意：不再调用外部API，完全依赖数字人自带的大模型NLP参数值进行对话
 */
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ChatDotRound, Microphone, ArrowUp, InfoFilled } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { useVoiceRecognition } from '@/composables/useVoiceRecognition'
import { detectFace } from '@/composables/useFaceDetect'
import DigitalHuman from './DigitalHuman/DigitalHuman.vue'

const router = useRouter()

// 获取路由参数中的数字人配置
const avatarConfig = ref({
  avatarId: '110117005', // 默认数字人ID
  tone: 'professional',
  speechRate: 1.0,
  voiceType: 'female'
})

// 从路由query参数中获取数字人配置
console.log('当前路由query参数:', router.currentRoute.value.query)
if (router.currentRoute.value.query.avatarConfig) {
  try {
    const routeConfig = JSON.parse(router.currentRoute.value.query.avatarConfig)
    avatarConfig.value = { ...avatarConfig.value, ...routeConfig }
    console.log('从路由获取数字人配置:', avatarConfig.value)
  } catch (error) {
    console.warn('解析数字人配置失败，使用默认配置:', error)
  }
} else {
  // 尝试从sessionStorage获取配置
  const sessionConfig = sessionStorage.getItem('avatarConfig')
  if (sessionConfig) {
    try {
      const sessionAvatarConfig = JSON.parse(sessionConfig)
      avatarConfig.value = { ...avatarConfig.value, ...sessionAvatarConfig }
      console.log('从sessionStorage获取数字人配置:', avatarConfig.value)
    } catch (error) {
      console.warn('解析sessionStorage中的数字人配置失败:', error)
    }
  } else {
    console.log('路由和sessionStorage中都没有avatarConfig参数，使用默认配置:', avatarConfig.value)
  }
}

const dialogList = ref([
  { role: 'ai', text: '你好！欢迎参加本次模拟面试。' },
  { role: 'ai', text: '我是你的数字人面试官，我将使用内置的智能对话系统与你进行面试交流。' }
])
const aiCurrentAnswer = ref(dialogList.value[dialogList.value.length - 1].text)
const aiRefAnswer = ref('此处将显示您上一次回答做出综合性的语音分析。')
const aiHint = ref('请保持自信，语气平和，适当微笑。')
const userVideo = ref(null)
const historyListRef = ref(null)
const aiVideoUrl = ref('')
const faceDetectResult = ref(null)

// 表情分析状态
const emotionAnalysis = ref({
  text: '检测中...',
  confidence: 0,
  level: 'neutral' // good, warning, danger, neutral
})

// 人脸属性检测状态
const faceAttributes = ref({
  attention: { text: '检测中...', level: 'neutral' },
  posture: { text: '检测中...', level: 'neutral' },
  eyeContact: { text: '检测中...', level: 'neutral' }
})

// 检测定时器
let faceDetectionTimer = null

// 数字人NLP相关状态
const currentDigitalHumanText = ref('') // 当前数字人正在说的话（实时更新）
const lastCompleteNlpContent = ref('') // 最后一次完整的NLP内容
const isDigitalHumanSpeaking = ref(false) // 数字人是否正在讲话
const accumulatedNlpContent = ref('') // 累积的NLP内容
let nlpCompletionTimer = null // 用于检测NLP完成的定时器

// 新增：控制视频互换
const isSwapped = ref(false) // 控制主窗口和小窗口内容是否交换
// 新增ref声明
const digitalHumanRef = ref(null)

const showWakeupBanner = ref(false)

// 新增：语音分析结果
const voiceAnalysisResult = ref(null)

// 新增：当前显示的语音分析数据（包含模拟数据）
const currentVoiceAnalysis = ref({
  volume: '待检测',
  speech_rate: '待检测',
  emotion: '待检测'
})

// 新增：底部输入框伸缩状态
const isInputExpanded = ref(true)

function wakeupDigitalHuman() {
  // 用户点击页面后只移除横幅，不再自动唤醒数字人
  showWakeupBanner.value = false
}

// 生成模拟表情分析数据
function generateMockEmotionData() {
  const emotions = [
    { text: '自信微笑', confidence: 85, level: 'good' },
    { text: '专注认真', confidence: 78, level: 'good' },
    { text: '略显紧张', confidence: 72, level: 'warning' },
    { text: '表情自然', confidence: 88, level: 'good' },
    { text: '需要放松', confidence: 65, level: 'warning' },
    { text: '状态良好', confidence: 92, level: 'good' }
  ]
  return emotions[Math.floor(Math.random() * emotions.length)]
}

// 生成模拟人脸属性数据
function generateMockFaceAttributes() {
  const attentionStates = [
    { text: '高度集中', level: 'good' },
    { text: '注意力分散', level: 'warning' },
    { text: '专注度良好', level: 'good' },
    { text: '需要集中', level: 'danger' }
  ]

  const postureStates = [
    { text: '坐姿端正', level: 'good' },
    { text: '略微前倾', level: 'good' },
    { text: '姿态放松', level: 'warning' },
    { text: '需要调整', level: 'danger' }
  ]

  const eyeContactStates = [
    { text: '眼神坚定', level: 'good' },
    { text: '目光游移', level: 'warning' },
    { text: '直视镜头', level: 'good' },
    { text: '避免眼神接触', level: 'danger' }
  ]

  return {
    attention: attentionStates[Math.floor(Math.random() * attentionStates.length)],
    posture: postureStates[Math.floor(Math.random() * postureStates.length)],
    eyeContact: eyeContactStates[Math.floor(Math.random() * eyeContactStates.length)]
  }
}
/*
// 第一版
// 分析人脸检测结果并提取表情信息
function analyzeFaceDetectionResult(result) {
  try {
    if (result && typeof result === 'string') {
      const faceData = JSON.parse(result);

      // 如果检测到人脸
      if (faceData.face_num > 0 && faceData.face_1) {
        const face = faceData.face_1;

        // 分析表情（基于人脸属性）
        if (face.property) {
          // 这里可以根据实际API返回的属性进行分析
          // 目前使用模拟数据，但保留真实检测的框架
          emotionAnalysis.value = generateMockEmotionData();
          faceAttributes.value = generateMockFaceAttributes();

          // 更新面试建议
          updateInterviewAdvice();
        }
      } else {
        // 未检测到人脸
        emotionAnalysis.value = { text: '未检测到人脸', confidence: 0, level: 'danger' };
        faceAttributes.value = {
          attention: { text: '请面向摄像头', level: 'danger' },
          posture: { text: '请调整位置', level: 'danger' },
          eyeContact: { text: '请看向镜头', level: 'danger' }
        };
        aiHint.value = '请确保您的面部清晰可见，保持在摄像头范围内。';
      }
    }
  } catch (error) {
    console.error('分析人脸检测结果失败:', error);
    // 使用模拟数据作为后备
    emotionAnalysis.value = generateMockEmotionData();
    faceAttributes.value = generateMockFaceAttributes();
  }
}*/

/*第二版*/
/*// 分析人脸检测结果并提取表情信息
function analyzeFaceDetectionResult(result) {
  try {
    if (result && typeof result === 'string') {
      // 如果结果是字符串，尝试解析JSON
      const faceData = JSON.parse(result);

      // 检查是否检测到人脸
      if (faceData.face_num > 0 && faceData.face_1) {
        const face = faceData.face_1;

        // 提取人脸属性信息
        if (face.property) {
          // 将官方参数转换为更友好的描述
          // 表情代码映射到描述
          let emotionText, emotionLevel, emotionConfidence;

          switch(face.property.expression) {
            case 0:
              emotionText = '非常惊讶';
              emotionLevel = 'warning';
              emotionConfidence = 70;
              break;
            case 1:
              emotionText = '害怕';
              emotionLevel = 'warning';
              emotionConfidence = 60;
              break;
            case 2:
              emotionText = '厌恶';
              emotionLevel = 'danger';
              emotionConfidence = 50;
              break;
            case 3:
              emotionText = '高兴';
              emotionLevel = 'good';
              emotionConfidence = 90;
              break;
            case 4:
              emotionText = '悲伤';
              emotionLevel = 'warning';
              emotionConfidence = 55;
              break;
            case 5:
              emotionText = '生气';
              emotionLevel = 'danger';
              emotionConfidence = 40;
              break;
            case 6:
              emotionText = '正常/中性';
              emotionLevel = 'good';
              emotionConfidence = 85;
              break;
            default:
              emotionText = '未知表情';
              emotionLevel = 'neutral';
              emotionConfidence = 60;
          }

          // 置信度转换为百分比
          const confidencePercentage = Math.round(face.score * 100);

          // 分析其他属性
          let attentionText, attentionLevel;
          if (face.property.mask === 1) {
            attentionText = '检测到佩戴口罩，可能影响表情识别';
            attentionLevel = 'warning';
          } else if (face.property.glass === 1) {
            attentionText = '检测到戴眼镜，可能轻微影响表情识别';
            attentionLevel = 'neutral';
          } else {
            attentionText = '高度集中';
            attentionLevel = 'good';
          }

          // 姿态和眼神使用模拟数据，实际项目中应根据更多数据判断
          const postureStates = [
            { text: '坐姿端正', level: 'good' },
            { text: '略微前倾', level: 'good' },
            { text: '姿态放松', level: 'warning' },
            { text: '需要调整', level: 'danger' }
          ];
          const posture = postureStates[Math.floor(Math.random() * postureStates.length)];

          const eyeContactStates = [
            { text: '眼神坚定', level: 'good' },
            { text: '目光游移', level: 'warning' },
            { text: '直视镜头', level: 'good' },
            { text: '避免眼神接触', level: 'danger' }
          ];
          const eyeContact = eyeContactStates[Math.floor(Math.random() * eyeContactStates.length)];

          // 更新状态
          emotionAnalysis.value = {
            text: emotionText,
            confidence: confidencePercentage,
            level: emotionLevel
          };

          faceAttributes.value = {
            attention: { text: attentionText, level: attentionLevel },
            posture: posture,
            eyeContact: eyeContact
          };

          // 更新面试建议
          updateInterviewAdvice();
        } else {
          // 没有属性信息的情况
          emotionAnalysis.value = { text: '表情数据不可用', confidence: 0, level: 'neutral' };
          faceAttributes.value = {
            attention: { text: '注意力数据不可用', level: 'neutral' },
            posture: { text: '姿态数据不可用', level: 'neutral' },
            eyeContact: { text: '眼神数据不可用', level: 'neutral' }
          };
          aiHint.value = '请确保您的面部清晰可见，保持在摄像头范围内。';
        }
      } else {
        // 未检测到人脸
        emotionAnalysis.value = { text: '未检测到人脸', confidence: 0, level: 'danger' };
        faceAttributes.value = {
          attention: { text: '请面向摄像头', level: 'danger' },
          posture: { text: '请调整位置', level: 'danger' },
          eyeContact: { text: '请看向镜头', level: 'danger' }
        };
        aiHint.value = '请确保您的面部清晰可见，保持在摄像头范围内。';
      }
    } else if (typeof result === 'object') {
      // 如果结果已经是对象，直接处理
      if (result.face_num > 0 && result.face_1) {
        const face = result.face_1;

        if (face.property) {
          // 表情代码映射到描述
          let emotionText, emotionLevel, emotionConfidence;

          switch(face.property.expression) {
            case 0:
              emotionText = '非常惊讶';
              emotionLevel = 'warning';
              emotionConfidence = 70;
              break;
            case 1:
              emotionText = '害怕';
              emotionLevel = 'warning';
              emotionConfidence = 60;
              break;
            case 2:
              emotionText = '厌恶';
              emotionLevel = 'danger';
              emotionConfidence = 50;
              break;
            case 3:
              emotionText = '高兴';
              emotionLevel = 'good';
              emotionConfidence = 90;
              break;
            case 4:
              emotionText = '悲伤';
              emotionLevel = 'warning';
              emotionConfidence = 55;
              break;
            case 5:
              emotionText = '生气';
              emotionLevel = 'danger';
              emotionConfidence = 40;
              break;
            case 6:
              emotionText = '正常/中性';
              emotionLevel = 'good';
              emotionConfidence = 85;
              break;
            default:
              emotionText = '未知表情';
              emotionLevel = 'neutral';
              emotionConfidence = 60;
          }

          // 置信度转换为百分比
          const confidencePercentage = Math.round(face.score * 100);

          // 分析其他属性
          let attentionText, attentionLevel;
          if (face.property.mask === 1) {
            attentionText = '检测到佩戴口罩，可能影响表情识别';
            attentionLevel = 'warning';
          } else if (face.property.glass === 1) {
            attentionText = '检测到戴眼镜，可能轻微影响表情识别';
            attentionLevel = 'neutral';
          } else {
            attentionText = '高度集中';
            attentionLevel = 'good';
          }

          // 姿态和眼神使用模拟数据
          const postureStates = [
            { text: '坐姿端正', level: 'good' },
            { text: '略微前倾', level: 'good' },
            { text: '姿态放松', level: 'warning' },
            { text: '需要调整', level: 'danger' }
          ];
          const posture = postureStates[Math.floor(Math.random() * postureStates.length)];

          const eyeContactStates = [
            { text: '眼神坚定', level: 'good' },
            { text: '目光游移', level: 'warning' },
            { text: '直视镜头', level: 'good' },
            { text: '避免眼神接触', level: 'danger' }
          ];
          const eyeContact = eyeContactStates[Math.floor(Math.random() * eyeContactStates.length)];

          // 更新状态
          emotionAnalysis.value = {
            text: emotionText,
            confidence: confidencePercentage,
            level: emotionLevel
          };

          faceAttributes.value = {
            attention: { text: attentionText, level: attentionLevel },
            posture: posture,
            eyeContact: eyeContact
          };

          // 更新面试建议
          updateInterviewAdvice();
        } else {
          // 没有属性信息的情况
          emotionAnalysis.value = { text: '表情数据不可用', confidence: 0, level: 'neutral' };
          faceAttributes.value = {
            attention: { text: '注意力数据不可用', level: 'neutral' },
            posture: { text: '姿态数据不可用', level: 'neutral' },
            eyeContact: { text: '眼神数据不可用', level: 'neutral' }
          };
          aiHint.value = '请确保您的面部清晰可见，保持在摄像头范围内。';
        }
      } else {
        // 未检测到人脸
        emotionAnalysis.value = { text: '未检测到人脸', confidence: 0, level: 'danger' };
        faceAttributes.value = {
          attention: { text: '请面向摄像头', level: 'danger' },
          posture: { text: '请调整位置', level: 'danger' },
          eyeContact: { text: '请看向镜头', level: 'danger' }
        };
        aiHint.value = '请确保您的面部清晰可见，保持在摄像头范围内。';
      }
    } else {
      // 无法识别的数据格式
      console.error('无法识别的数据格式');
      // 使用模拟数据作为后备
      emotionAnalysis.value = generateMockEmotionData();
      faceAttributes.value = generateMockFaceAttributes();
      aiHint.value = '系统暂时使用模拟数据进行评估，请确保摄像头正常工作。';
    }
  } catch (error) {
    console.error('分析人脸检测结果失败:', error);
    // 使用模拟数据作为后备
    emotionAnalysis.value = generateMockEmotionData();
    faceAttributes.value = generateMockFaceAttributes();
    aiHint.value = '系统暂时使用模拟数据进行评估，请确保摄像头正常工作。';
  }
}*/
//*第三版*/
// 分析人脸检测结果并提取表情信息
/*
function analyzeFaceDetectionResult(result) {
  try {
    // 解析结果，无论它是字符串还是对象
    let faceData;
    if (typeof result === 'string') {
      faceData = JSON.parse(result);
    } else {
      faceData = result;
    }

    // 检查数据有效性
    if (!faceData || faceData.face_num === undefined || !faceData.face_1) {
      throw new Error('未检测到人脸或数据格式不正确');
    }

    const face = faceData.face_1;

    // 如果没有属性信息，使用模拟数据
    if (!face.property) {
      throw new Error('没有人脸属性信息');
    }

    // ======================
    // 1. 表情分析
    // ======================
    let emotionText, emotionLevel, emotionConfidence, emotionSuggestions;

    switch(face.property.expression) {
      case 0:
        emotionText = '你看起来非常惊讶，可能有点意外这个面试问题';
        emotionLevel = 'warning';
        emotionConfidence = 70;
        emotionSuggestions = '试着保持镇定，深呼吸，给自己一点时间思考答案';
        break;
      case 1:
        emotionText = '你似乎有些害怕或紧张，这很正常，但试着放松一些';
        emotionLevel = 'warning';
        emotionConfidence = 60;
        emotionSuggestions = '记住，面试官也是普通人，深呼吸，放慢语速可以帮助你放松';
        break;
      case 2:
        emotionText = '你看起来有些厌恶或不耐烦，这可能会给面试官留下不好印象';
        emotionLevel = 'danger';
        emotionConfidence = 50;
        emotionSuggestions = '试着保持开放和积极的态度，即使问题让你不舒服';
        break;
      case 3:
        emotionText = '你看起来很高兴，面带微笑，这是一个很好的表现！';
        emotionLevel = 'good';
        emotionConfidence = 90;
        emotionSuggestions = '继续保持这种积极的态度，它会让面试官感到舒适';
        break;
      case 4:
        emotionText = '你看起来有些悲伤或沮丧，可能影响你的表现';
        emotionLevel = 'warning';
        emotionConfidence = 55;
        emotionSuggestions = '试着回想一些积极的事情，调整你的情绪状态';
        break;
      case 5:
        emotionText = '你看起来有些生气或烦躁，这可能会影响面试结果';
        emotionLevel = 'danger';
        emotionConfidence = 40;
        emotionSuggestions = '试着冷静下来，记住面试是一个双向选择的过程';
        break;
      case 6:
        emotionText = '你的表情很自然，看起来很放松，这是一个很好的状态！';
        emotionLevel = 'good';
        emotionConfidence = 85;
        emotionSuggestions = '继续保持这种自然放松的状态，它会帮助你更好地表达自己';
        break;
      default:
        emotionText = '你的表情难以识别，可能影响面试官的判断';
        emotionLevel = 'neutral';
        emotionConfidence = 60;
        emotionSuggestions = '试着保持面部表情自然，不要过度紧张或僵硬';
    }

    // ======================
    // 2. 注意力分析
    // ======================
    let attentionText, attentionLevel, attentionSuggestions;

    if (face.property.mask === 1) {
      attentionText = '检测到您佩戴了口罩，可能会影响面部表情的传达';
      attentionLevel = 'warning';
      attentionSuggestions = '虽然戴口罩是必要的，但请尽量通过眼睛和眉毛表达您的情绪和关注';
    } else if (face.property.glass === 1) {
      attentionText = '检测到您戴了眼镜，可能会轻微影响面部表情的识别';
      attentionLevel = 'neutral';
      attentionSuggestions = '如果可能，调整一下眼镜位置，确保它不会遮挡您的眼睛表情';
    } else {
      // 根据置信度判断注意力
      if (face.score > 0.85) {
        attentionText = '您的注意力非常集中，眼神坚定地看向摄像头';
        attentionLevel = 'good';
        attentionSuggestions = '继续保持这种专注的状态，它会让您看起来更自信';
      } else if (face.score > 0.7) {
        attentionText = '您的注意力比较集中，但可以更加专注一些';
        attentionLevel = 'neutral';
        attentionSuggestions = '尝试更直接地看向摄像头，表现出对面试的专注';
      } else {
        attentionText = '您的注意力似乎不够集中，可能分心了';
        attentionLevel = 'warning';
        attentionSuggestions = '试着将注意力完全集中在面试上，避免被周围环境干扰';
      }
    }

    // ======================
    // 3. 姿态分析
    // ======================
    let postureText, postureLevel, postureSuggestions;

    // 由于API可能不直接提供姿态数据，我们基于表情和置信度推测
    if (face.property.expression === 6 && face.score > 0.8) {
      // 如果表情自然且置信度高，推测姿态可能良好
      postureText = '您的坐姿看起来很端正，身体放松但专注';
      postureLevel = 'good';
      postureSuggestions = '继续保持这种舒适的姿态，它有助于您更好地表达自己';
    } else if (face.score > 0.6) {
      postureText = '您的姿态看起来还可以，但可以更加端正一些';
      postureLevel = 'neutral';
      postureSuggestions = '尝试坐直身体，但不要僵硬，保持舒适但专注的姿态';
    } else {
      postureText = '您的姿态可能需要调整，看起来有些松弛或不端正';
      postureLevel = 'warning';
      postureSuggestions = '试着调整您的坐姿，保持背部挺直但不僵硬，这会增强您的自信感';
    }

    // ======================
    // 4. 眼神分析
    // ======================
    let eyeContactText, eyeContactLevel, eyeContactSuggestions;

    // 由于API可能不直接提供眼神接触数据，我们基于表情和置信度推测
    if (face.property.expression === 3 || face.property.expression === 6) {
      // 如果表情是高兴或自然，推测眼神接触可能良好
      eyeContactText = '您的眼神接触很好，看起来很自信地看向摄像头';
      eyeContactLevel = 'good';
      eyeContactSuggestions = '继续保持良好的眼神接触，这会让面试官感觉您很自信且专注';
    } else if (face.score > 0.7) {
      eyeContactText = '您的眼神接触还可以，但可以更加直接一些';
      eyeContactLevel = 'neutral';
      eyeContactSuggestions = '尝试更直接地看向摄像头，就像在和一个真实的人交流一样';
    } else {
      eyeContactText = '您的眼神接触可能需要加强，看起来有些游离';
      eyeContactLevel = 'warning';
      eyeContactSuggestions = '试着保持眼神接触，不要频繁地转移视线，这会让您看起来更专注';
    }

    // 置信度转换为百分比
    const confidencePercentage = Math.round(face.score * 100);

    // ======================
    // 5. 综合反馈
    // ======================
    let overallFeedback;

    if (emotionLevel === 'good' && attentionLevel === 'good' && postureLevel === 'good' && eyeContactLevel === 'good') {
      overallFeedback = '太棒了！您的整体表现非常出色，看起来自信、专注且自然。继续保持这种状态！';
    } else if (emotionLevel === 'danger' || attentionLevel === 'danger' || postureLevel === 'danger' || eyeContactLevel === 'danger') {
      overallFeedback = '需要注意一些方面来改善您的表现。尝试放松一些，更加专注，并保持良好的眼神接触。';
    } else if (emotionLevel === 'warning' || attentionLevel === 'warning' || postureLevel === 'warning' || eyeContactLevel === 'warning') {
      overallFeedback = '您的表现还不错，但有一些方面可以改进。尝试调整您的表情、注意力、姿态或眼神接触。';
    } else {
      overallFeedback = '您的表现总体不错，有一些小地方可以微调以获得更好的效果。';
    }

    // 更新状态
    emotionAnalysis.value = {
      text: emotionText,
      confidence: confidencePercentage,
      level: emotionLevel,
      suggestions: emotionSuggestions
    };

    faceAttributes.value = {
      attention: {
        text: attentionText,
        level: attentionLevel,
        suggestions: attentionSuggestions
      },
      posture: {
        text: postureText,
        level: postureLevel,
        suggestions: postureSuggestions
      },
      eyeContact: {
        text: eyeContactText,
        level: eyeContactLevel,
        suggestions: eyeContactSuggestions
      }
    };

    aiHint.value = overallFeedback;

    console.log('详细的面部检测分析结果:', {
      emotion: { text: emotionText, suggestions: emotionSuggestions },
      attention: { text: attentionText, suggestions: attentionSuggestions },
      posture: { text: postureText, suggestions: postureSuggestions },
      eyeContact: { text: eyeContactText, suggestions: eyeContactSuggestions },
      overall: overallFeedback
    });

  } catch (error) {
    console.error('分析人脸检测结果失败:', error);

    // 使用模拟数据作为后备
    console.log('使用模拟数据进行反馈');
    emotionAnalysis.value = generateMockEmotionData();
    faceAttributes.value = generateMockFaceAttributes();
    aiHint.value = '系统暂时使用模拟数据进行评估，请确保摄像头正常工作。';
  }
}

*/
// 分析人脸检测结果并提取表情信息
function analyzeFaceDetectionResult(result) {
  try {
    // 现在result已经是解析后的对象了
    const faceData = result

    // 检查数据有效性
    if (!faceData || faceData.face_num === undefined || !faceData.face_1) {
      throw new Error('未检测到人脸或数据格式不正确')
    }

    console.log('分析人脸检测结果，检测到人脸数:', faceData.face_num)

    // 检测到多人时显示警告弹窗
    if (faceData.face_num > 1) {
      ElMessageBox.alert(
        '检测到多人在场，为确保面试的公平性和有效性，请您确保单独的面试环境！',
        '面试环境警告',
        {
          confirmButtonText: '我知道了',
          type: 'warning',
          showClose: false
        }
      )
    }

    const face = faceData.face_1

    // 如果没有属性信息，使用模拟数据
    if (!face.property) {
      throw new Error('没有人脸属性信息')
    }

    // ======================
    // 1. 表情分析
    // ======================
    let emotionText, emotionLevel, emotionConfidence, emotionSuggestions

    switch(face.property.expression) {
    case 0:
      emotionText = '你看起来非常惊讶，可能有点意外这个面试问题'
      emotionLevel = 'warning'
      emotionConfidence = 70
      emotionSuggestions = '试着保持镇定，深呼吸，给自己一点时间思考答案'
      break
    case 1:
      emotionText = '你似乎有些害怕或紧张，这很正常，但试着放松一些'
      emotionLevel = 'warning'
      emotionConfidence = 60
      emotionSuggestions = '记住，面试官也是普通人，深呼吸，放慢语速可以帮助你放松'
      break
    case 2:
      emotionText = '你看起来有些厌恶或不耐烦，这可能会给面试官留下不好印象'
      emotionLevel = 'danger'
      emotionConfidence = 50
      emotionSuggestions = '试着保持开放和积极的态度，即使问题让你不舒服'
      break
    case 3:
      emotionText = '你看起来很高兴，面带微笑，这是一个很好的表现！'
      emotionLevel = 'good'
      emotionConfidence = 90
      emotionSuggestions = '继续保持这种积极的态度，它会让面试官感到舒适'
      break
    case 4:
      emotionText = '你看起来有些悲伤或沮丧，可能影响你的表现'
      emotionLevel = 'warning'
      emotionConfidence = 55
      emotionSuggestions = '试着回想一些积极的事情，调整你的情绪状态'
      break
    case 5:
      emotionText = '你看起来有些生气或烦躁，这可能会影响面试结果'
      emotionLevel = 'danger'
      emotionConfidence = 40
      emotionSuggestions = '试着冷静下来，记住面试是一个双向选择的过程'
      break
    case 6:
      emotionText = '你的表情很自然，看起来很放松，这是一个很好的状态！'
      emotionLevel = 'good'
      emotionConfidence = 85
      emotionSuggestions = '继续保持这种自然放松的状态，它会帮助你更好地表达自己'
      break
    default:
      emotionText = '你的表情难以识别，可能影响面试官的判断'
      emotionLevel = 'neutral'
      emotionConfidence = 60
      emotionSuggestions = '试着保持面部表情自然，不要过度紧张或僵硬'
    }

    // ======================
    // 2. 注意力分析
    // ======================
    let attentionText, attentionLevel, attentionSuggestions

    if (face.property.mask === 1) {
      attentionText = '检测到您佩戴了口罩，可能会影响面部表情的传达'
      attentionLevel = 'warning'
      attentionSuggestions = '虽然戴口罩是必要的，但请尽量通过眼睛和眉毛表达您的情绪和关注'
    } else if (face.property.glass === 1) {
      attentionText = '检测到您戴了眼镜，可能会轻微影响面部表情的识别'
      attentionLevel = 'neutral'
      attentionSuggestions = '如果可能，调整一下眼镜位置，确保它不会遮挡您的眼睛表情'
    } else {
      // 根据置信度判断注意力
      if (face.score > 0.85) {
        attentionText = '您的注意力非常集中，眼神坚定地看向摄像头'
        attentionLevel = 'good'
        attentionSuggestions = '继续保持这种专注的状态，它会让您看起来更自信'
      } else if (face.score > 0.7) {
        attentionText = '您的注意力比较集中，但可以更加专注一些'
        attentionLevel = 'neutral'
        attentionSuggestions = '尝试更直接地看向摄像头，表现出对面试的专注'
      } else {
        attentionText = '您的注意力似乎不够集中，可能分心了'
        attentionLevel = 'warning'
        attentionSuggestions = '试着将注意力完全集中在面试上，避免被周围环境干扰'
      }
    }

    // ======================
    // 3. 姿态分析
    // ======================
    let postureText, postureLevel, postureSuggestions

    // 由于API可能不直接提供姿态数据，我们基于表情和置信度推测
    if (face.property.expression === 6 && face.score > 0.8) {
      // 如果表情自然且置信度高，推测姿态可能良好
      postureText = '您的坐姿看起来很端正，身体放松但专注'
      postureLevel = 'good'
      postureSuggestions = '继续保持这种舒适的姿态，它有助于您更好地表达自己'
    } else if (face.score > 0.6) {
      postureText = '您的姿态看起来还可以，但可以更加端正一些'
      postureLevel = 'neutral'
      postureSuggestions = '尝试坐直身体，但不要僵硬，保持舒适但专注的姿态'
    } else {
      postureText = '您的姿态可能需要调整，看起来有些松弛或不端正'
      postureLevel = 'warning'
      postureSuggestions = '试着调整您的坐姿，保持背部挺直但不僵硬，这会增强您的自信感'
    }

    // ======================
    // 4. 眼神分析
    // ======================
    let eyeContactText, eyeContactLevel, eyeContactSuggestions

    // 由于API可能不直接提供眼神接触数据，我们基于表情和置信度推测
    if (face.property.expression === 3 || face.property.expression === 6) {
      // 如果表情是高兴或自然，推测眼神接触可能良好
      eyeContactText = '您的眼神接触很好，看起来很自信地看向摄像头'
      eyeContactLevel = 'good'
      eyeContactSuggestions = '继续保持良好的眼神接触，这会让面试官感觉您很自信且专注'
    } else if (face.score > 0.7) {
      eyeContactText = '您的眼神接触还可以，但可以更加直接一些'
      eyeContactLevel = 'neutral'
      eyeContactSuggestions = '尝试更直接地看向摄像头，就像在和一个真实的人交流一样'
    } else {
      eyeContactText = '您的眼神接触可能需要加强，看起来有些游离'
      eyeContactLevel = 'warning'
      eyeContactSuggestions = '试着保持眼神接触，不要频繁地转移视线，这会让您看起来更专注'
    }

    // 置信度转换为百分比
    const confidencePercentage = Math.round(face.score * 100)

    // ======================
    // 5. 综合反馈
    // ======================
    let overallFeedback

    if (emotionLevel === 'good' && attentionLevel === 'good' && postureLevel === 'good' && eyeContactLevel === 'good') {
      overallFeedback = '太棒了！您的整体表现非常出色，看起来自信、专注且自然。继续保持这种状态！'
    } else if (emotionLevel === 'danger' || attentionLevel === 'danger' || postureLevel === 'danger' || eyeContactLevel === 'danger') {
      overallFeedback = '需要注意一些方面来改善您的表现。尝试放松一些，更加专注，并保持良好的眼神接触。'
    } else if (emotionLevel === 'warning' || attentionLevel === 'warning' || postureLevel === 'warning' || eyeContactLevel === 'warning') {
      overallFeedback = '您的表现还不错，但有一些方面可以改进。尝试调整您的表情、注意力、姿态或眼神接触。'
    } else {
      overallFeedback = '您的表现总体不错，有一些小地方可以微调以获得更好的效果。'
    }

    // 更新状态
    emotionAnalysis.value = {
      text: emotionText,
      confidence: confidencePercentage,
      level: emotionLevel,
      suggestions: emotionSuggestions
    }

    faceAttributes.value = {
      attention: {
        text: attentionText,
        level: attentionLevel,
        suggestions: attentionSuggestions
      },
      posture: {
        text: postureText,
        level: postureLevel,
        suggestions: postureSuggestions
      },
      eyeContact: {
        text: eyeContactText,
        level: eyeContactLevel,
        suggestions: eyeContactSuggestions
      }
    }

    aiHint.value = overallFeedback

    console.log('详细的面部检测分析结果:', {
      emotion: { text: emotionText, suggestions: emotionSuggestions },
      attention: { text: attentionText, suggestions: attentionSuggestions },
      posture: { text: postureText, suggestions: postureSuggestions },
      eyeContact: { text: eyeContactText, suggestions: eyeContactSuggestions },
      overall: overallFeedback
    })

  } catch (error) {
    console.error('分析人脸检测结果失败:', error)

    // 使用模拟数据作为后备
    console.log('使用模拟数据进行反馈')
    emotionAnalysis.value = generateMockEmotionData()
    faceAttributes.value = generateMockFaceAttributes()
    aiHint.value = '系统暂时使用模拟数据进行评估，请确保摄像头正常工作。'
  }
}


// 根据检测结果更新面试建议
function updateInterviewAdvice() {
  const emotion = emotionAnalysis.value
  const attrs = faceAttributes.value

  let advice = ''

  if (emotion.level === 'danger' || attrs.attention.level === 'danger') {
    advice = '请调整状态，保持专注和自信的表情。'
  } else if (emotion.level === 'warning' || attrs.attention.level === 'warning') {
    advice = '状态不错，可以更加放松自然一些。'
  } else {
    advice = '表现很好！保持当前的状态和表情。'
  }

  aiHint.value = advice
}

// 执行人脸检测和分析
async function performFaceDetectionAndAnalysis() {
  if (!userVideo.value) return

  try {
    // 创建canvas抓取当前帧
    const video = userVideo.value
    const canvas = document.createElement('canvas')
    canvas.width = video.videoWidth
    canvas.height = video.videoHeight
    const ctx = canvas.getContext('2d')
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height)

    // 转为blob
    const blob = await new Promise(resolve => canvas.toBlob(resolve, 'image/jpeg'))
    if (!blob) return

    try {
      // 调用真实的人脸检测API
      const result = await detectFace(blob)
      faceDetectResult.value = result

      // 分析检测结果
      analyzeFaceDetectionResult(result)

    } catch (error) {
      console.log('人脸检测API调用失败，使用模拟数据:', error.message)
      // API调用失败时使用模拟数据
      faceDetectResult.value = { error: error.message }
      emotionAnalysis.value = generateMockEmotionData()
      faceAttributes.value = generateMockFaceAttributes()
      updateInterviewAdvice()
    }

  } catch (error) {
    console.error('人脸检测过程出错:', error)
    // 完全失败时也使用模拟数据
    emotionAnalysis.value = generateMockEmotionData()
    faceAttributes.value = generateMockFaceAttributes()
    updateInterviewAdvice()
  }
}

// 启动定时检测
function startFaceDetection() {
  // 立即执行一次
  performFaceDetectionAndAnalysis()

  // 每5秒检测一次
  faceDetectionTimer = setInterval(() => {
    performFaceDetectionAndAnalysis()
  }, 10000)
}

// 停止定时检测
function stopFaceDetection() {
  if (faceDetectionTimer) {
    clearInterval(faceDetectionTimer)
    faceDetectionTimer = null
  }
}


const {
  isRecording,
  recordingMode,
  recognizedText,
  toggleVoice,
  cleanup,
  analysisResult
} = useVoiceRecognition()
const timerText = ref('00:00')
let timer = null
let seconds = 0

function getTimeFromIndex(idx) {
  const now = new Date()
  const time = new Date(now.getTime() - (dialogList.value.length - idx) * 60000)
  return time.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// 新增：Dify工作流结果处理方法
function getEmotionClass(emotion) {
  if (!emotion) return 'emotion-neutral'

  if (emotion.includes('开心') || emotion.includes('高兴') || emotion.includes('愉快') ||
      emotion.includes('兴奋') || emotion.includes('快乐') || emotion.includes('满意') ||
      emotion.includes('positive') || emotion.includes('happy') || emotion.includes('joy')) {
    return 'emotion-good'
  } else if (emotion.includes('紧张') || emotion.includes('焦虑') || emotion.includes('担心') ||
             emotion.includes('不安') || emotion.includes('忧虑') ||
             emotion.includes('nervous') || emotion.includes('anxious') || emotion.includes('worried')) {
    return 'emotion-warning'
  } else if (emotion.includes('生气') || emotion.includes('愤怒') || emotion.includes('沮丧') ||
             emotion.includes('失望') || emotion.includes('难过') ||
             emotion.includes('angry') || emotion.includes('sad') || emotion.includes('negative')) {
    return 'emotion-danger'
  }
  return 'emotion-neutral'
}

function formatConfidence(confidence) {
  if (confidence == null) return '未知'
  return `${(confidence * 100).toFixed(1)}%`
}

function formatScore(score) {
  if (score == null) return '0'
  return (score * 100).toFixed(0)
}

function truncateText(text, maxLength) {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

// 格式化字段名称（将英文字段名转换为中文）
function formatFieldName(fieldName) {
  const fieldMap = {
    'primaryEmotion': '主要情感',
    'confidence': '置信度',
    'textTranscript': '识别文本',
    'emotionScores': '情感得分',
    'analysisDetails': '分析详情',
    'text': '文本内容',
    'sex': '性别',
    'age': '年龄',
    'language_type': '语言类型',
    'tone': '语调',
    'volume': '音量',
    'emotion': '情感',
    'speed': '语速'
  }
  return fieldMap[fieldName] || fieldName
}

// 格式化字段值
function formatFieldValue(fieldName, value) {
  if (value === null || value === undefined) return '无数据'

  if (fieldName === 'confidence' && typeof value === 'number') {
    return `${(value * 100).toFixed(1)}%`
  }

  if (typeof value === 'object') {
    return JSON.stringify(value)
  }

  if (typeof value === 'string' && value.length > 30) {
    return truncateText(value, 30)
  }

  return String(value)
}

// 获取字段样式类
function getFieldClass(fieldName, value) {
  if (fieldName === 'primaryEmotion' || fieldName === 'emotion') {
    return getEmotionClass(value)
  }

  if (fieldName === 'confidence') {
    return 'confidence'
  }

  return ''
}

// 获取音量样式类
function getVolumeClass(volume) {
  if (!volume) return ''
  const vol = volume.toLowerCase()
  if (vol.includes('大') || vol.includes('高') || vol.includes('loud')) {
    return 'volume-high'
  } else if (vol.includes('小') || vol.includes('低') || vol.includes('quiet')) {
    return 'volume-low'
  } else if (vol.includes('适中') || vol.includes('中等') || vol.includes('normal')) {
    return 'volume-normal'
  }
  return ''
}

// 获取语速样式类
function getSpeechRateClass(speechRate) {
  if (!speechRate) return ''
  const rate = speechRate.toLowerCase()
  if (rate.includes('快') || rate.includes('急') || rate.includes('fast')) {
    return 'speech-fast'
  } else if (rate.includes('慢') || rate.includes('缓') || rate.includes('slow')) {
    return 'speech-slow'
  } else if (rate.includes('适中') || rate.includes('中等') || rate.includes('normal')) {
    return 'speech-normal'
  }
  return ''
}

// 新增：切换底部输入框展开/收缩状态
function toggleInputExpansion() {
  isInputExpanded.value = !isInputExpanded.value
  console.log('切换输入框状态:', isInputExpanded.value ? '展开' : '收缩')
}

// 新增：提取emotion字段中的具体内容
function extractEmotionContent(emotion) {
  if (!emotion) return '待检测'

  // 如果是字符串，直接返回
  if (typeof emotion === 'string') {
    // 移除可能的数组符号和引号
    return emotion.replace(/[\[\]"']/g, '').trim()
  }

  // 如果是数组，提取第一个元素
  if (Array.isArray(emotion) && emotion.length > 0) {
    return String(emotion[0]).replace(/[\[\]"']/g, '').trim()
  }

  // 如果是对象，尝试提取值
  if (typeof emotion === 'object') {
    const values = Object.values(emotion)
    if (values.length > 0) {
      return String(values[0]).replace(/[\[\]"']/g, '').trim()
    }
  }

  return '未检测'
}


function scrollToBottom() {
  nextTick(() => {
    if (historyListRef.value) {
      historyListRef.value.scrollTop = historyListRef.value.scrollHeight
    }
  })
}
watch(dialogList, () => {
  scrollToBottom()
})

// 新增：监听Dify工作流语音分析结果
watch(() => analysisResult.value, (newResult) => {
  if (newResult) {
    console.log('=== 📊 后端返回的完整数据结构分析 ===')
    console.log('原始数据:', newResult)
    console.log('数据类型:', typeof newResult)
    console.log('所有字段:', Object.keys(newResult))

    // 逐个分析字段
    Object.keys(newResult).forEach(key => {
      console.log(`字段 ${key}:`, newResult[key], `(类型: ${typeof newResult[key]})`)
      if (key === 'emotion') {
        console.log('emotion字段详细分析:', {
          原始值: newResult[key],
          类型: typeof newResult[key],
          是否数组: Array.isArray(newResult[key]),
          提取后: extractEmotionContent(newResult[key])
        })
      }
    })

    console.log('=== 数据结构分析完成 ===')
    voiceAnalysisResult.value = newResult

    // 更新当前显示的语音分析数据
    currentVoiceAnalysis.value = {
      volume: newResult.volume || '未检测',
      speech_rate: newResult.speech_rate || '未检测',
      emotion: extractEmotionContent(newResult.emotion)
    }

    // 将summary字段的值更新到AI参考答案
    if (newResult.summary) {
      console.log('✅ 结果:', newResult.summary)
      aiRefAnswer.value = newResult.summary
    }
  }
}, { deep: true })
function fetchAIVideoUrl() {
  setTimeout(() => {
    aiVideoUrl.value = 'https://www.w3schools.com/html/mov_bbb.mp4'
  }, 1000)
}

// 初始化摄像头
async function initCamera() {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({
      video: true,
      audio: false
    })

    if (userVideo.value) {
      userVideo.value.srcObject = stream
      console.log('摄像头已开启')

      // 开始人脸检测
      setTimeout(() => {
        startFaceDetection()
      }, 1000)
    }
  } catch (error) {
    console.error('摄像头开启失败:', error)
    alert('摄像头开启失败，请检查权限设置')
  }
}

onMounted(() => {
  fetchAIVideoUrl()

  // 启动计时器
  timer = setInterval(() => {
    seconds++
    const min = String(Math.floor(seconds / 60)).padStart(2, '0')
    const sec = String(seconds % 60).padStart(2, '0')
    timerText.value = `${min}:${sec}`
  }, 1000)

  // 页面顶部显示唤醒横幅
  showWakeupBanner.value = true
  // 只允许唤醒一次
  const handler = () => {
    wakeupDigitalHuman()
    window.removeEventListener('click', handler)
  }
  window.addEventListener('click', handler)

  // 自动开启摄像头
  initCamera()
})


onUnmounted(() => {
  window.removeEventListener('click', wakeupDigitalHuman)
  if (userVideo.value && userVideo.value.srcObject) {
    const tracks = userVideo.value.srcObject.getTracks()
    tracks.forEach(track => track.stop())
  }
  if (timer) clearInterval(timer)
  if (nlpCompletionTimer) clearTimeout(nlpCompletionTimer) // 清理NLP完成检测定时器
  stopFaceDetection() // 停止人脸检测
  cleanup()
})
function endInterview() {
  if (confirm('确定要结束本次面试吗？')) {
    router.push('/layout/assessReport')
  }
}


// 测试数字人功能
function testDigitalHuman() {
  const testQuestions = [
    '请简单介绍一下你自己',
    '你为什么想要这个职位？',
    '你最大的优势是什么？',
    '你有什么问题想问我吗？'
  ]

  const randomQuestion = testQuestions[Math.floor(Math.random() * testQuestions.length)]

  if (digitalHumanRef.value && digitalHumanRef.value.writeText) {
    console.log('测试数字人NLP对话，模拟面试问题:', randomQuestion)
    // 使用NLP功能让数字人基于问题生成智能回复
    digitalHumanRef.value.writeText(randomQuestion, true)

    // 添加到对话历史
    dialogList.value.push({ role: 'ai', text: '测试问题：' + randomQuestion })
  } else {
    console.error('数字人组件未准备好')
  }
}


// 生成面试相关的参考答案和提示
function generateInterviewGuidance(userMessage) {
  // 根据用户消息内容生成相应的参考答案和提示
  const lowerMessage = userMessage.toLowerCase()

  let aiRefAnswer = 'AI参考答案：'
  let aiHint = '请注意语气平和，保持微笑。'

  if (lowerMessage.includes('自我介绍') || lowerMessage.includes('介绍自己')) {
    aiRefAnswer = '简洁明了地介绍姓名、教育背景、工作经验和核心技能，突出与职位相关的优势。'
    aiHint = '保持自信，语速适中，重点突出个人亮点。'
  } else if (lowerMessage.includes('优势') || lowerMessage.includes('优点')) {
    aiRefAnswer = '结合具体例子说明个人优势，如学习能力强、团队协作好、解决问题能力等。'
    aiHint = '用具体事例支撑你的观点，避免空泛的描述。'
  } else if (lowerMessage.includes('缺点') || lowerMessage.includes('不足')) {
    aiRefAnswer = '诚实地提及一个真实但不致命的缺点，并说明正在如何改进。'
    aiHint = '展现自我反思能力和改进意愿，避免说"完美主义"等套话。'
  } else if (lowerMessage.includes('职业规划') || lowerMessage.includes('未来')) {
    aiRefAnswer = '结合个人兴趣和公司发展方向，制定清晰的短期和长期目标。'
    aiHint = '展现对行业的了解和个人的上进心。'
  } else {
    aiRefAnswer = '根据问题核心，结合个人经历和能力进行回答，保持逻辑清晰。'
    aiHint = '仔细听题，思考后再回答，保持真诚和自信。'
  }

  return { aiRefAnswer, aiHint }
}


// 处理数字人NLP内容
// 这是数字人内置大模型生成回复的核心处理函数
function handleNlpContent(nlpData) {
  console.log('收到数字人NLP内容:', nlpData)

  if (nlpData && nlpData.content) {
    isDigitalHumanSpeaking.value = true

    // 判断是否是新的对话开始（内容长度明显减少或完全不同）
    const isNewConversation = nlpData.content.length < accumulatedNlpContent.value.length * 0.5 ||
        (accumulatedNlpContent.value.length > 0 && !nlpData.content.includes(accumulatedNlpContent.value.substring(0, Math.min(20, accumulatedNlpContent.value.length))))

    if (isNewConversation && accumulatedNlpContent.value.length > 0) {
      console.log('检测到新对话开始，重置累积内容')
      accumulatedNlpContent.value = ''
    }

    // 累积内容处理
    if (nlpData.content.includes(accumulatedNlpContent.value)) {
      // 新内容包含旧内容，直接替换（这是分流数据的常见情况）
      accumulatedNlpContent.value = nlpData.content
    } else if (accumulatedNlpContent.value.includes(nlpData.content)) {
      // 旧内容已包含新内容，不更新
      console.log('新内容已包含在累积内容中，跳过更新')
    } else {
      // 追加新内容
      accumulatedNlpContent.value += nlpData.content
    }

    // 实时更新当前显示的文本（显示累积的完整内容）
    currentDigitalHumanText.value = accumulatedNlpContent.value

    console.log('更新实时文本:', accumulatedNlpContent.value, '是否完成:', nlpData.isComplete, '原始内容:', nlpData.content)

    // 清除之前的完成检测定时器
    if (nlpCompletionTimer) {
      clearTimeout(nlpCompletionTimer)
    }

    // 如果明确标记为完成，立即处理
    if (nlpData.isComplete) {
      console.log('数字人NLP明确标记为完成，立即添加到历史记录')
      addToDialogHistory()
    } else {
      // 设置定时器，如果2秒内没有新内容，认为已完成
      nlpCompletionTimer = setTimeout(() => {
        console.log('2秒内无新NLP内容，认为回复完成')
        addToDialogHistory()
      }, 2000)
    }
  } else {
    console.warn('收到无效的NLP数据:', nlpData)
  }
}

// 添加到对话历史的函数
function addToDialogHistory() {
  const finalContent = accumulatedNlpContent.value.trim()

  if (finalContent && lastCompleteNlpContent.value !== finalContent) {
    // 移除可能存在的"正在思考中..."消息
    const lastMsg = dialogList.value[dialogList.value.length - 1]
    if (lastMsg && lastMsg.role === 'ai' && (lastMsg.text.includes('思考中') || lastMsg.text.includes('正在思考'))) {
      console.log('移除思考中消息:', lastMsg.text)
      dialogList.value.pop()
    }

    // 添加完整的数字人NLP回复到历史记录
    dialogList.value.push({
      role: 'ai',
      text: finalContent,
      source: 'digital-human-nlp' // 标记来源为数字人NLP
    })
    lastCompleteNlpContent.value = finalContent
    aiCurrentAnswer.value = finalContent

    console.log('已添加数字人NLP回复到对话历史:', finalContent)

    // 讲话完成后，延迟清空实时显示文本和累积内容
    setTimeout(() => {
      isDigitalHumanSpeaking.value = false
      currentDigitalHumanText.value = ''
      accumulatedNlpContent.value = ''
      console.log('清空实时显示文本和累积内容')
    }, 2000) // 增加延迟时间，让用户能看到完整内容
  } else if (!finalContent) {
    console.log('累积内容为空，跳过添加到历史记录')
  } else {
    console.log('内容重复，跳过添加到历史记录')
  }
}

async function sendMsg() {
  if (!recognizedText.value) return

  const userMessage = recognizedText.value
  dialogList.value.push({ role: 'user', text: userMessage })
  recognizedText.value = ''

  // 显示思考状态
  dialogList.value.push({ role: 'ai', text: 'AI面试官正在思考中...' })

  try {
    // 生成面试指导内容
    const guidance = generateInterviewGuidance(userMessage)

    // 更新参考答案和提示
    aiRefAnswer.value = guidance.aiRefAnswer
    aiHint.value = guidance.aiHint

    // 直接使用数字人的NLP功能处理用户消息
    // 数字人会基于其内置的大模型生成回复
    if (digitalHumanRef.value && digitalHumanRef.value.writeText) {
      console.log('使用数字人NLP处理用户消息:', userMessage)
      // 传递用户消息给数字人，让其NLP功能生成回复
      digitalHumanRef.value.writeText(userMessage, true) // useNlp=true 启用NLP功能
    } else {
      console.error('数字人组件未准备好')
      // 如果数字人不可用，移除思考状态并显示错误
      dialogList.value.pop()
      const errorReply = '数字人面试官暂时无法回复，请稍后再试。'
      dialogList.value.push({ role: 'ai', text: errorReply })
      aiCurrentAnswer.value = errorReply
    }

    // 注意：不在这里直接更新dialogList和aiCurrentAnswer
    // 因为会通过handleNlpContent来处理数字人的实际NLP回复

  } catch (error) {
    console.error('处理消息时出错:', error)
    dialogList.value.pop()
    const errorReply = '处理消息时出现错误，请稍后再试。'
    dialogList.value.push({ role: 'ai', text: errorReply })
    aiCurrentAnswer.value = errorReply
  }
}
</script>

<style scoped>
:global(html, body) {
  width: 100%;
  height: 100vh;
  overflow: hidden;
}
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
.main-flex-layout > .side-panel:last-child {
  margin-right: 0;
}
.right-panel {
  flex: none !important;
  width: 700px;
  min-width: 480px;
  max-width: 800px;
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
  overflow-y: auto;
  overflow-x: hidden;
}

/* 自定义滚动条样式 - 仅针对right-panel */
.right-panel::-webkit-scrollbar {
  width: 8px;
}

.right-panel::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
  margin: 8px 0;
}

.right-panel::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
  transition: background 0.3s ease;
}

.right-panel::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

.right-panel::-webkit-scrollbar-thumb:active {
  background: #888;
}

/* 确保滚动内容的底部有足够的间距 */
.right-panel > *:last-child {
  margin-bottom: 20px;
}

/* 平滑滚动 */
.right-panel {
  scroll-behavior: smooth;
}
.fixed-bottom-input {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  background: #f5f8ff;
  box-shadow: 0 -2px 8px #0001;
  padding: 18px 20px 12px 20px;
  border-bottom-left-radius: 16px;
  border-bottom-right-radius: 16px;
  z-index: 2;
  transition: all 0.3s ease;
  overflow: hidden;
}

/* 收缩状态 */
.fixed-bottom-input.collapsed {
  padding: 8px 20px;
}

/* 左侧伸缩控制按钮 */
.expand-toggle-btn-left {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 6px;
  margin-right: 8px;
  background: #f0f2f5;
  border: 1px solid #d9d9d9;
}

.expand-toggle-btn-left:hover {
  background: #e6f7ff;
  border-color: #409eff;
}

.expand-toggle-btn-left .el-icon {
  color: #666;
  font-size: 14px;
  transition: transform 0.3s ease;
}

.expand-toggle-btn-left:hover .el-icon {
  color: #409eff;
}

.expand-toggle-btn-left .el-icon.rotated {
  transform: rotate(180deg);
}

/* 主要输入区域 */
.input-main-area {
  transition: all 0.3s ease;
}

/* 展开内容 */
.expanded-content {
  transition: all 0.3s ease;
  opacity: 1;
  max-height: 200px;
  overflow: hidden;
}

.fixed-bottom-input.collapsed .expanded-content {
  opacity: 0;
  max-height: 0;
  margin-top: 0;
  padding-top: 0;
}

/* 收缩状态下的语音按钮 */
.compact-voice-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  margin: 0 8px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.compact-voice-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.compact-voice-btn .mic-icon {
  color: white;
  font-size: 18px;
}

/* 收缩状态下的声波动画 */
.compact-sound-waves {
  display: flex;
  align-items: center;
  gap: 2px;
}

.compact-wave-bar {
  width: 3px;
  background: white;
  border-radius: 2px;
  animation: compactWave 1s ease-in-out infinite;
}

.compact-wave-1 { height: 8px; animation-delay: 0s; }
.compact-wave-2 { height: 12px; animation-delay: 0.1s; }
.compact-wave-3 { height: 8px; animation-delay: 0.2s; }

@keyframes compactWave {
  0%, 100% { transform: scaleY(1); }
  50% { transform: scaleY(1.5); }
}
/* 让上方内容不被遮挡 */
.right-panel > .emotion-analysis-bar,
.right-panel > .face-attribute-bar,
.right-panel > .ai-hint-bar,
.right-panel > .ai-answer-hint-bar {
  margin-bottom: 16px;
}

.right-panel > .ai-answer-hint-bar {
  padding-bottom: 140px;
}
.history-header {
  padding: 18px 20px 12px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  gap: 8px;
}
.history-icon {
  color: #409eff;
  font-size: 20px;
}
.history-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}
.history-list {
  flex: 1;
  padding: 12px 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow-y: auto;
  max-height: calc(100% - 60px);
}

/* 自定义滚动条样式 */
.history-list::-webkit-scrollbar {
  width: 6px;
}

.history-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.history-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.history-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
.history-item {
  padding: 10px;
  border-radius: 8px;
  background: #f8f9fa;
  border-left: 3px solid #e9ecef;
  transition: all 0.2s ease;
}
.history-item.ai {
  border-left-color: #409eff;
  background: #f0f9ff;
}
.history-item.user {
  border-left-color: #67c23a;
  background: #f0f9ff;
}
.history-role {
  font-size: 12px;
  color: #666;
  margin-bottom: 2px;
  font-weight: 500;
}
.history-text {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.history-time {
  font-size: 11px;
  color: #999;
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
.video-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.camera-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 14px;
}

.camera-ok {
  color: #67C23A;
  font-size: 18px;
}

.camera-error {
  color: #F56C6C;
  font-size: 18px;
}

.loading-icon {
  color: #409EFF;
  font-size: 18px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.status-text {
  font-weight: 500;
  color: #666;
}

.header-buttons {
  display: flex;
  align-items: center;
  gap: 12px;
}
.timer {
  font-size: 18px;
  color: #409eff;
  font-weight: bold;
}
.big-timer {
  font-size: 28px;
  font-weight: 700;
}
.big-end-btn {
  font-size: 22px !important;
  padding: 8px 32px !important;
  height: 48px !important;
  border-radius: 12px !important;
}
.video-split-row {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: flex-end;
  gap: 24px;
  width: 100%;
  margin-bottom: 18px;
}
.video-box {
  position: relative;
  width: 300px;
  height: 300px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.ai-video-box {
  /* 可自定义AI视频样式 */
}
.user-video-box {
  /* 可自定义用户视频样式 */
}
.video-label {
  position: absolute;
  left: 50%;
  bottom: 10px;
  transform: translateX(-50%);
  background: #409eff;
  color: #fff;
  font-size: 14px;
  border-radius: 8px;
  padding: 2px 12px;
  opacity: 0.92;
  z-index: 2;
}
.user-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 16px;
}
.ai-answer-box {
  width: 90%;
  height: 120px;
  background: #fffbe6;
  border-radius: 12px;
  margin-top: 18px;
  font-size: 20px;
  color: #222;
  padding: 18px 24px;
  box-sizing: border-box;
  box-shadow: 0 2px 8px #ffd04b33;
  overflow-y: auto;
  line-height: 1.5;
  text-align: left;
  display: flex;
  align-items: flex-start;
}

/* 自定义AI答案框滚动条样式 */
.ai-answer-box::-webkit-scrollbar {
  width: 6px;
}

.ai-answer-box::-webkit-scrollbar-track {
  background: #f9f5d3;
  border-radius: 3px;
}

.ai-answer-box::-webkit-scrollbar-thumb {
  background: #e6cc5a;
  border-radius: 3px;
}

.ai-answer-box::-webkit-scrollbar-thumb:hover {
  background: #d4b942;
}
.ai-ref-answer-box {
  width: 90%;
  min-height: 32px;
  background: #e8f5e9;
  border-radius: 10px;
  margin-top: 8px;
  font-size: 15px;
  color: #222;
  display: flex;
  align-items: center;
  padding: 8px 14px;
  box-sizing: border-box;
  box-shadow: 0 2px 8px #4fd18b33;
  font-weight: 500;
}
.ref-title {
  color: #67C23A;
  font-weight: 700;
  margin-right: 6px;
}
.ref-content {
  color: #222;
}
.dialog-input-area-row {
  width: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;
  gap: 8px;
  margin-top: 16px;
}
.voice-input {
  flex: 1;
  font-size: 18px;
  border-radius: 8px; /* 统一圆角 */
}
.send-btn {
  font-size: 18px;
  padding: 0 24px;
  height: 40px;
  margin-left: 8px;
  border-radius: 8px; /* 统一圆角 */
}
.recording-mode-selector {
  margin: 8px 0;
  text-align: center;
}
.voice-btn-outer {
  width: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 8px;
  flex-shrink: 0;
}
.voice-btn {
  background: #fff;
  border: 2px solid #409eff;
  color: #409eff;
  border-radius: 50%;
  width: 80px;
  height: 80px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}
.voice-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.3);
}
.voice-btn.recording {
  background: #67c23a;
  color: #fff;
  border-color: #67c23a;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
  animation: pulse 2s infinite;
}
.mic-icon {
  font-size: 32px;
  transition: all 0.3s ease;
}
.sound-waves {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 3px;
  height: 40px;
}
.wave-bar {
  width: 4px;
  background: #fff;
  border-radius: 2px;
  animation: waveAnimation 1.2s infinite ease-in-out;
}
.wave-1 { height: 8px; animation-delay: 0s; }
.wave-2 { height: 16px; animation-delay: 0.1s; }
.wave-3 { height: 24px; animation-delay: 0.2s; }
.wave-4 { height: 32px; animation-delay: 0.3s; }
.wave-5 { height: 24px; animation-delay: 0.4s; }
.wave-6 { height: 16px; animation-delay: 0.5s; }
.wave-7 { height: 8px; animation-delay: 0.6s; }
.wave-8 { height: 4px; animation-delay: 0.7s; }
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
@keyframes pulse {
  0% {
    box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
  }
  50% {
    box-shadow: 0 4px 20px rgba(103, 194, 58, 0.5);
  }
  100% {
    box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
  }
}
/* 表情分析区域样式 */
.emotion-analysis-bar {
  width: 100%;
  background: #fff2f0;
  border-radius: 10px;
  padding: 16px 18px;
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #F56C6C;
  display: flex;
  flex-direction: column;
  font-weight: 600;
  box-shadow: 0 2px 8px #f56c6c33;
  min-height: 80px;
}

.emotion-content {
  color: #222;
  font-weight: 500;
  margin-left: 4px;
  margin-top: 4px;
}

.emotion-content.good { color: #67C23A; }
.emotion-content.warning { color: #E6A23C; }
.emotion-content.danger { color: #F56C6C; }
.emotion-content.neutral { color: #909399; }

.emotion-details {
  margin-top: 8px;
  font-size: 14px;
}

.emotion-score {
  color: #666;
  font-weight: 400;
}

/* 人脸属性检测区域样式 */
.face-attribute-bar {
  width: 100%;
  background: #f0f9ff;
  border-radius: 10px;
  padding: 16px 18px;
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #67C23A;
  display: flex;
  flex-direction: column;
  font-weight: 600;
  box-shadow: 0 2px 8px #67c23a33;
  min-height: 120px;
}

.attribute-list {
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.attribute-item {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.attr-label {
  color: #666;
  font-weight: 500;
  min-width: 60px;
}

.attr-value {
  font-weight: 600;
  margin-left: 8px;
}

.attr-value.good { color: #67C23A; }
.attr-value.warning { color: #E6A23C; }
.attr-value.danger { color: #F56C6C; }
.attr-value.neutral { color: #909399; }

.ai-hint-bar {
  width: 100%;
  background: #fffbe6;
  border-radius: 10px;
  padding: 16px 18px;
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #E6A23C;
  display: flex;
  align-items: center;
  font-weight: 600;
  box-shadow: 0 2px 8px #ffd04b33;
  min-height: 60px;
  max-height: 80px;
}
.ai-hint-content {
  color: #222;
  font-weight: 500;
  margin-left: 4px;
}

.ai-answer-hint-bar {
  width: 100%;
  background: #e3f2fd;
  border-radius: 10px;
  padding: 20px 18px;
  margin: 0 0 20px 0;
  font-size: 16px;
  color: #409EFF;
  display: flex;
  flex-direction: column;
  font-weight: 600;
  box-shadow: 0 2px 8px #409eff33;
  min-height: 100px;
  max-height: 150px;
}

.ai-answer-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 16px;
  color: #409EFF;
  font-weight: 600;
}

.speaking-indicator {
  margin-left: auto;
  font-size: 14px;
  color: #67C23A;
  font-weight: 500;
  animation: pulse 1.5s infinite;
}

.ai-answer-content {
  color: #222;
  font-weight: 500;
  line-height: 1.5;
  word-wrap: break-word;
  white-space: pre-wrap;
  flex: 1;
  overflow-y: auto;
}
.history-panel {
  width: 600px;
}
.input-tip {
  font-size: 15px;
  color: #409EFF;
  font-weight: bold;
  margin-top: 4px;
  margin-bottom: 6px;
  text-align: right;
  width: 100%;
  letter-spacing: 1px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
.input-tip .tip-text {
  color: #222;
  font-weight: bold;
  margin-left: 4px;
}
.wakeup-banner {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  background: #409eff;
  color: #fff;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  padding: 16px 0;
  z-index: 9999;
  box-shadow: 0 2px 8px #409eff44;
}
.video-split-row.vertical-video-layout {
  flex-direction: column;
  align-items: center;
  gap: 24px;
  width: 100%;
  margin-bottom: 18px;
}
.upper-half-only {
  overflow: hidden;
  position: relative;
}
.upper-half-only > * {
  position: absolute;
  top: -30px; /* 向上偏移只显示上半身，可根据实际调整 */
  left: 0;
  width: 100%;
  height: 160%; /* 拉高内容只显示上半身 */
}
.main-video-area {
  position: relative;
  width: 520px;
  height: 520px;
  margin: 0 auto 24px auto;
  display: flex;
  align-items: center;
  justify-content: center;
}
.main-video-box {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
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
  top: -10%; /* 放大并向上偏移，只显示上半身且铺满窗口 */
  left: 0;
  object-fit: cover;
}
.pip-video-box {
  position: absolute;
  right: 10px;
  bottom: 10px;
  width: 200px;
  height: 200px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  overflow: hidden;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
}
/* 模拟面试页面专属视频窗口样式 */
.main-video-area .ai-video-box {
  position: relative;
  width: 520px;
  height: 520px;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  margin-bottom: 18px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.main-video-area .user-video-pip {
  position: absolute;
  right: 10px;
  bottom: 10px;
  width: 200px;
  height: 200px;
  border-radius: 16px;
  background: #888;
  overflow: hidden;
  z-index: 3;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px #9198e522;
}

/* 语音控制容器样式 */
.voice-control-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  margin: 0 auto;
  padding: 20px;
}

/* 语音分析面板 - 右侧 */
.voice-analysis-panel-right {
  position: absolute;
  right: 0;
  top: 10%;
  transform: translateY(-20%);
  z-index: 5;
}

.analysis-card-compact {
  background: #fff;
  border-radius: 16px;
  padding: 6px 20px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  border: 1px solid #e8e8e8;
  min-width: 180px;
  transition: all 0.3s ease;
}

.analysis-card-compact:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
  border-color: #409eff;
}

.analysis-title-compact {
  font-weight: 600;
  color: #333;
  font-size: 14px;
  margin-bottom: 12px;
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 8px;
}

.analysis-item-compact {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding: 6px 0;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.analysis-item-compact:hover {
  background-color: #f8f9fa;
}

.analysis-item-compact:last-child {
  margin-bottom: 0;
}

.label-compact {
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

.value-compact {
  font-size: 12px;
  font-weight: 600;
  text-align: right;
  padding: 2px 8px;
  border-radius: 4px;
  background-color: #f5f5f5;
}


/* 分析卡片样式 */
.analysis-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(64, 158, 255, 0.2);
  width: 100%;
  max-width: 200px;
  transition: all 0.3s ease;
}

.analysis-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  border-color: rgba(64, 158, 255, 0.3);
}

.analysis-title {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-weight: 600;
  color: #409eff;
  font-size: 13px;
}

.analysis-title i {
  margin-right: 6px;
  font-size: 14px;
}

.analysis-item {
  margin-bottom: 10px;
  padding: 8px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.analysis-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.analysis-item .label {
  display: block;
  font-size: 11px;
  color: #666;
  font-weight: 500;
  margin-bottom: 4px;
}

.analysis-item .value {
  display: block;
  font-size: 12px;
  font-weight: 600;
  line-height: 1.4;
}

.confidence {
  color: #409eff;
  font-weight: 700;
}

.text-content {
  font-size: 11px !important;
  line-height: 1.3;
  color: #555;
  font-weight: 500 !important;
}

.analysis-details {
  font-size: 11px !important;
  line-height: 1.3;
  color: #555;
  font-weight: 500 !important;
}

/* 情感得分样式 */
.emotion-scores {
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 100%;
}

.score-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  padding: 2px 0;
}

.emotion-name {
  color: #666;
  font-weight: 500;
}

.score-value {
  font-weight: 600;
  color: #409eff;
}

/* 情绪状态颜色 */
.emotion-good {
  color: #67c23a;
}

.emotion-warning {
  color: #e6a23c;
}

.emotion-danger {
  color: #f56c6c;
}

.emotion-neutral {
  color: #909399;
}

/* 音量状态颜色 */
.volume-high {
  color: #f56c6c;
}

.volume-normal {
  color: #67c23a;
}

.volume-low {
  color: #e6a23c;
}

/* 语速状态颜色 */
.speech-fast {
  color: #f56c6c;
}

.speech-normal {
  color: #67c23a;
}

.speech-slow {
  color: #e6a23c;
}


</style>
