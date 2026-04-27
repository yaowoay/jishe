<template>
  <div class="simulat-exam-root">
    <div v-if="showWakeupBanner" class="wakeup-banner">请点击页面任意位置唤醒AI多模态面试官</div>
    <div class="main-flex-layout">
      <div class="left-column">
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
                  <DigitalHuman />
                </template>
              </div>
            </div>
          </div>
          <div class="ai-answer-box">{{ currentDigitalHumanText || aiCurrentAnswer }}</div>
        </div>

        <div class="insight-panel">
          <div class="insight-left">
            <div class="emotion-analysis-bar">
              <el-icon style="color:#F56C6C;margin-right:6px;"><i class="el-icon-view"></i></el-icon>
              <span>表情分析：</span>
              <span class="emotion-content" :class="emotionAnalysis.level">{{ emotionAnalysis.text }}</span>
              <div class="emotion-details">
                <span class="emotion-score">置信度: {{ emotionAnalysis.confidence }}%</span>
              </div>
            </div>
          </div>

          <div class="insight-right">
            <div class="face-attribute-bar">
              <el-icon style="color:#67C23A;margin-right:6px;"><i class="el-icon-user"></i></el-icon>
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
          </div>
        </div>
      </div>

      <div class="right-panel chat-panel">
        <div class="chat-header">
          <div class="chat-header-left">
            <el-icon class="history-icon"><ChatDotRound /></el-icon>
            <span class="history-title">面试对话</span>
          </div>
          <div class="chat-header-hint" :title="aiHint">
            <el-icon style="color:#E6A23C;margin-right:4px;"><i class="el-icon-smile"></i></el-icon>
            <span class="chat-hint-label">建议：</span>
            <span class="chat-hint-text">{{ aiHint }}</span>
          </div>
        </div>
        <div class="chat-list" ref="historyListRef">
          <div v-for="(msg, idx) in dialogList" :key="idx" :class="['chat-message', msg.role]">
            <div class="chat-role">{{ msg.role === 'ai' ? '面试官' : '你' }}</div>
            <div class="chat-text">{{ msg.text }}</div>
            <div class="chat-time">{{ getTimeFromIndex(idx) }}</div>
          </div>
        </div>

        <div class="fixed-bottom-input">
          <div class="dialog-input-area-row">
            <el-input
              v-model="recognizedText"
              placeholder="请输入你的回答，或点击语音按钮..."
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
            <div class="voice-btn" :class="{ recording: isRecording }" @click="toggleVoice">
              <el-icon v-if="!isRecording" class="mic-icon"><Microphone /></el-icon>
              <div v-else class="sound-waves">
                <span v-for="n in 8" :key="n" :class="['wave-bar', 'wave-' + n]"></span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="faceDetectResult" style="position:fixed;bottom:10px;right:10px;background:#fff;padding:6px 12px;border-radius:6px;z-index:9999;box-shadow:0 1px 4px rgba(0,0,0,0.1);font-size:12px;color:#606266;">
      <div v-if="faceDetectResult.error" style="color:#f56c6c;">检测失败</div>
      <div v-else>检测到人脸数：{{ faceDetectResult.face_num }}</div>
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
import { ChatDotRound, Microphone } from '@element-plus/icons-vue'
import { useVoiceRecognition } from '@/composables/useVoiceRecognition'
import { detectFace } from '@/composables/useFaceDetect'
import DigitalHuman from './DigitalHuman/DigitalHuman.vue'
import VideoAnalysis from './VideoAnalysis.vue'

const router = useRouter()
const dialogList = ref([
  { role: 'ai', text: '你好！欢迎参加本次模拟面试。' },
  { role: 'ai', text: '我是你的AI多模态面试官，我将使用内置的智能对话系统与你进行面试交流。' }
])

// 视频分析相关
const showVideoAnalysis = ref(false)
// 生成一个较小的面试ID（使用时间戳的后8位）
const currentInterviewId = ref(Math.floor(Date.now() / 1000) % 100000000)
const startTime = ref(new Date().toISOString()) // 面试开始时间

// 视频录制相关
const mediaRecorder = ref(null)
const recordedChunks = ref([])
const isVideoRecording = ref(false) // 改名避免与语音识别的isRecording冲突
const recordedVideoBlob = ref(null)
const videoStream = ref(null)
const aiCurrentAnswer = ref(dialogList.value[dialogList.value.length - 1].text)
const aiRefAnswer = ref('本题AI参考答案将在此展示。')
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
} */

/* 第二版 */
/* // 分析人脸检测结果并提取表情信息
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
} */
//* 第三版*/
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
    // 解析结果，无论它是字符串还是对象
    let faceData
    if (typeof result === 'string') {
      faceData = JSON.parse(result)
    } else {
      faceData = result
    }

    // 检查数据有效性
    if (!faceData || faceData.face_num === undefined || !faceData.face_1) {
      throw new Error('未检测到人脸或数据格式不正确')
    }

    const face = faceData.face_1

    // 如果没有属性信息，使用模拟数据
    if (!face.property) {
      throw new Error('没有人脸属性信息')
    }

    // ======================
    // 1. 表情分析
    // ======================
    // eslint-disable-next-line no-unused-vars
    let emotionText, emotionLevel, emotionConfidence, emotionSuggestions

    switch (face.property.expression) {
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
      // eslint-disable-next-line no-unused-vars
      emotionConfidence = 85
      emotionSuggestions = '继续保持这种自然放松的状态，它会帮助你更好地表达自己'
      break
    default:
      emotionText = '你的表情难以识别，可能影响面试官的判断'
      emotionLevel = 'neutral'
      // emotionConfidence = 60 // 未使用，先注释
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
  cleanup
} = useVoiceRecognition()
const timerText = ref('00:00')
let timer = null
let seconds = 0

function getTimeFromIndex(idx) {
  const now = new Date()
  const time = new Date(now.getTime() - (dialogList.value.length - idx) * 60000)
  return time.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}
function scrollToBottom() {
  nextTick(() => {
    if (historyListRef.value) {
      historyListRef.value.scrollTo({
        top: historyListRef.value.scrollHeight,
        behavior: 'smooth'
      })
    }
  })
}
watch(dialogList, () => {
  scrollToBottom()
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
      audio: true // 开启音频录制
    })

    if (userVideo.value) {
      userVideo.value.srcObject = stream
      videoStream.value = stream
      console.log('摄像头已开启')

      // 初始化视频录制器
      initVideoRecorder(stream)

      // 开始人脸检测
      setTimeout(() => {
        startFaceDetection()
      }, 1000)

      // 延迟开始录制
      setTimeout(() => {
        startVideoRecording()
      }, 2000)
    }
  } catch (error) {
    console.error('摄像头开启失败:', error)
    alert('摄像头开启失败，请检查权限设置')
  }
}

// 初始化视频录制器
function initVideoRecorder(stream) {
  try {
    recordedChunks.value = []

    // 检查浏览器支持的视频格式
    let options = { mimeType: 'video/webm;codecs=vp9' }
    if (!MediaRecorder.isTypeSupported(options.mimeType)) {
      options = { mimeType: 'video/webm;codecs=vp8' }
      if (!MediaRecorder.isTypeSupported(options.mimeType)) {
        options = { mimeType: 'video/webm' }
        if (!MediaRecorder.isTypeSupported(options.mimeType)) {
          options = { mimeType: 'video/mp4' }
        }
      }
    }

    mediaRecorder.value = new MediaRecorder(stream, options)

    mediaRecorder.value.ondataavailable = (event) => {
      if (event.data.size > 0) {
        recordedChunks.value.push(event.data)
      }
    }

    mediaRecorder.value.onstop = () => {
      console.log('录制停止，开始处理视频数据')
      recordedVideoBlob.value = new Blob(recordedChunks.value, { type: 'video/webm' })
      uploadRecordedVideo()
    }

    console.log('视频录制器初始化成功，支持格式:', options.mimeType)
  } catch (error) {
    console.error('视频录制器初始化失败:', error)
  }
}

// 开始视频录制
function startVideoRecording() {
  console.log(' [SimulatExam] 尝试开始视频录制...')
  console.log(' mediaRecorder状态:', mediaRecorder.value ? mediaRecorder.value.state : 'null')
  console.log(' isVideoRecording当前值:', isVideoRecording.value)

  if (!mediaRecorder.value) {
    console.error(' mediaRecorder未初始化')
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
    console.log(' [SimulatExam] 开始录制面试视频成功！')
  } catch (error) {
    console.error(' 开始录制失败:', error)
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
  if (!recordedVideoBlob.value) {
    console.log('没有录制的视频数据')
    return
  }

  try {
    console.log('准备上传面试视频，大小:', (recordedVideoBlob.value.size / 1024 / 1024).toFixed(2), 'MB')

    const formData = new FormData()
    const fileName = `interview_${currentInterviewId.value}_${Date.now()}.webm`
    formData.append('video', recordedVideoBlob.value, fileName)
    formData.append('interviewId', currentInterviewId.value)

    const response = await fetch('/api/video/upload', {
      method: 'POST',
      body: formData
    })

    if (response.ok) {
      const result = await response.json()
      console.log('视频上传成功:', result)

      // 更新面试记录中的视频路径
      await updateInterviewVideoPath(result.videoPath)
    } else {
      console.error('视频上传失败:', response.statusText)
    }
  } catch (error) {
    console.error('上传视频时出错:', error)
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
async function endInterview() {
  if (confirm('确定要结束本次面试吗？')) {
    // 停止计时器和清理资源
    if (timer) clearInterval(timer)
    if (nlpCompletionTimer) clearTimeout(nlpCompletionTimer)
    stopFaceDetection()

    // 停止视频录制
    if (isVideoRecording.value) {
      stopVideoRecording()
    }

    // 停止摄像头
    if (videoStream.value) {
      const tracks = videoStream.value.getTracks()
      tracks.forEach(track => track.stop())
      videoStream.value = null
    }

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
      const interviewRecord = {
        interviewId: currentInterviewId.value,
        startTime: startTime.value,
        endTime: new Date().toISOString(),
        history: JSON.stringify({
          questions: questions,
          answers: answers,
          dialogList: dialogList.value
        }),
        evaluation: JSON.stringify({
          interviewType: '模拟面试',
          duration: timerText.value,
          questionsCount: questions.length,
          answersCount: answers.length
        }),
        overallScore: 0.0 // 初始评分，后续通过视频分析更新
      }

      console.log('保存面试记录:', interviewRecord)

      // 调用后端API保存面试记录
      const response = await fetch('/api/ai-interviews', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(interviewRecord)
      })

      if (response.ok) {
        console.log('面试记录保存成功')
      } else {
        console.error('面试记录保存失败:', response.statusText)
      }
    } catch (error) {
      console.error('保存面试记录时出错:', error)
    }

    // 保存面试数据到sessionStorage
    const interviewData = {
      interviewId: currentInterviewId.value,
      duration: timerText.value,
      endTime: new Date().toISOString(),
      interviewType: '模拟面试',
      questions: questions,
      answers: answers,
      videoAnalysis: {
        hasVideo: true, // 模拟面试默认有视频
        videoQuality: 'HD',
        recordingDuration: timerText.value
      }
    }
    sessionStorage.setItem('interviewData', JSON.stringify(interviewData))

    // 跳转到面试报告页面
    router.push('/interview/report')
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
    aiRefAnswer = 'AI参考答案：简洁明了地介绍姓名、教育背景、工作经验和核心技能，突出与职位相关的优势。'
    aiHint = '保持自信，语速适中，重点突出个人亮点。'
  } else if (lowerMessage.includes('优势') || lowerMessage.includes('优点')) {
    aiRefAnswer = 'AI参考答案：结合具体例子说明个人优势，如学习能力强、团队协作好、解决问题能力等。'
    aiHint = '用具体事例支撑你的观点，避免空泛的描述。'
  } else if (lowerMessage.includes('缺点') || lowerMessage.includes('不足')) {
    aiRefAnswer = 'AI参考答案：诚实地提及一个真实但不致命的缺点，并说明正在如何改进。'
    aiHint = '展现自我反思能力和改进意愿，避免说"完美主义"等套话。'
  } else if (lowerMessage.includes('职业规划') || lowerMessage.includes('未来')) {
    aiRefAnswer = 'AI参考答案：结合个人兴趣和公司发展方向，制定清晰的短期和长期目标。'
    aiHint = '展现对行业的了解和个人的上进心。'
  } else {
    aiRefAnswer = 'AI参考答案：根据问题核心，结合个人经历和能力进行回答，保持逻辑清晰。'
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
  dialogList.value.push({ role: 'ai', text: 'AI多模态面试官正在思考中...' })

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
      const errorReply = 'AI多模态面试官暂时无法回复，请稍后再试。'
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
  background: #f4f6f8;
  padding: 0;
  margin: 0;
  box-sizing: border-box;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.main-flex-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  width: 100%;
  padding: 10px;
  height: 100vh;
  gap: 10px;
  align-items: stretch;
  overflow: hidden;
  box-sizing: border-box;
}
.left-column,
.right-panel {
  height: calc(100vh - 20px);
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  box-sizing: border-box;
}
.left-column {
  display: grid;
  grid-template-rows: 75% 25%;
  gap: 8px;
  padding: 8px;
  overflow: hidden;
}
.center-panel {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  width: 100%;
  height: 100%;
  min-width: 0;
  max-width: none;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px;
  box-sizing: border-box;
  overflow: hidden;
}
.insight-panel {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 6px;
  overflow: hidden;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px;
}
.insight-left,
.insight-right {
  min-width: 0;
}
.insight-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.insight-right {
  display: flex;
}
.insight-right .face-attribute-bar {
  margin: 0;
  width: 100%;
}
.right-panel {
  position: relative;
  padding: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.fixed-bottom-input {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  background: #ffffff;
  border-top: 1px solid #edf0f3;
  box-shadow: none;
  padding: 8px 10px;
  border-bottom-left-radius: 10px;
  border-bottom-right-radius: 10px;
  z-index: 2;
  box-sizing: border-box;
}
.history-icon {
  color: #409eff;
  font-size: 20px;
}
.history-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2d3d;
}
.chat-header {
  padding: 8px 10px;
  border-bottom: 1px solid #edf0f3;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  background: #fafafa;
}
.chat-header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}
.chat-header-hint {
  display: flex;
  align-items: center;
  min-width: 0;
  max-width: 52%;
  padding: 2px 6px;
  border: 1px solid #fde6bf;
  border-radius: 12px;
  background: #fffbf2;
  font-size: 12px;
  color: #8a6d3b;
}
.chat-hint-label {
  flex-shrink: 0;
}
.chat-hint-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.chat-list {
  flex: 1;
  min-height: 0;
  padding: 8px 10px 126px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow-y: auto;
}
.chat-message {
  max-width: 92%;
  padding: 8px 10px;
  border-radius: 10px;
  background: #f8fafc;
  border: 1px solid #e5e7eb;
}
.chat-message.ai {
  align-self: flex-start;
}
.chat-message.user {
  align-self: flex-end;
  background: #eff8ff;
  border-color: #d7e8ff;
}
.chat-role {
  font-size: 11px;
  color: #6b7280;
  margin-bottom: 2px;
}
.chat-text {
  font-size: 13px;
  color: #1f2937;
  line-height: 1.45;
  word-break: break-word;
  white-space: pre-wrap;
}
.chat-time {
  font-size: 10px;
  color: #9ca3af;
  margin-top: 3px;
  text-align: right;
}
.video-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  padding: 0 2px;
}
.big-timer {
  font-size: 20px;
  font-weight: 700;
}
.big-end-btn {
  font-size: 14px !important;
  padding: 6px 14px !important;
  height: 34px !important;
  border-radius: 6px !important;
}
.main-video-area {
  position: relative;
  width: 100%;
  flex: 1;
  min-height: 0;
  margin: 0 auto 6px auto;
  display: flex;
  align-items: center;
  justify-content: center;
}
.main-video-box {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 8px;
  background: #000;
  overflow: hidden;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.main-video-box > *:first-child {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  object-fit: cover;
}
.pip-video-box {
  position: absolute;
  right: 12px;
  bottom: 12px;
  width: 140px;
  height: 105px;
  border-radius: 6px;
  background: #000;
  box-shadow: 0 2px 8px rgba(0,0,0,0.3);
  overflow: hidden;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
  border: 2px solid #fff;
}
.pip-video-box > * {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.ai-answer-box {
  width: 100%;
  height: 52px;
  background: #fff;
  border: 1px solid #e6eeff;
  border-radius: 6px;
  margin-top: 0;
  font-size: 13px;
  color: #303133;
  padding: 6px 8px;
  box-sizing: border-box;
  box-shadow: none;
  overflow-y: auto;
  line-height: 1.4;
  text-align: left;
  display: flex;
  align-items: flex-start;
}
.ai-answer-box::-webkit-scrollbar {
  width: 4px;
}
.ai-answer-box::-webkit-scrollbar-track {
  background: #f1f6ff;
  border-radius: 2px;
}
.ai-answer-box::-webkit-scrollbar-thumb {
  background: #c0d4f7;
  border-radius: 2px;
}
.ai-answer-box::-webkit-scrollbar-thumb:hover {
  background: #a0bce8;
}
@media (max-width: 1200px) {
  .left-column {
    grid-template-rows: 68% 32%;
  }
  .insight-panel {
    grid-template-columns: 1fr;
    overflow-y: auto;
  }
}
.ai-ref-answer-box {
  width: 90%;
  min-height: 32px;
  background: #ecf5ff;
  border-radius: 8px;
  margin-top: 12px;
  font-size: 14px;
  color: #303133;
  display: flex;
  align-items: center;
  padding: 8px 12px;
  box-sizing: border-box;
  box-shadow: 0 1px 6px rgba(64, 158, 255, 0.1);
  font-weight: 500;
}
.ref-title {
  color: #409eff;
  font-weight: 600;
  margin-right: 6px;
}
.ref-content {
  color: #303133;
}
.dialog-input-area-row {
  width: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 0;
}
.voice-input {
  width: 100%;
  font-size: 13px;
  border-radius: 6px;
}
.send-btn {
  font-size: 13px;
  padding: 0 12px;
  height: 30px;
  margin-left: 0;
  border-radius: 6px;
}
.input-tip {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}
.recording-mode-selector {
  margin: 6px 0 4px;
  text-align: left;
  transform: scale(0.95);
  transform-origin: left center;
}
.voice-btn-outer {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 2px;
}
.voice-btn {
  background: #fff;
  border: 1.5px solid #409eff;
  color: #409eff;
  border-radius: 50%;
  width: 42px;
  height: 42px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: none;
}
.voice-btn:hover {
  transform: scale(1.03);
  box-shadow: none;
}
.voice-btn.recording {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
  box-shadow: none;
  animation: pulse 2s infinite;
}
.mic-icon {
  font-size: 18px;
  transition: all 0.2s ease;
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
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
  }
  50% {
    box-shadow: 0 2px 12px rgba(64, 158, 255, 0.4);
  }
  100% {
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
  }
}
.emotion-analysis-bar {
  width: 100%;
  background: #fef0f0;
  border-radius: 8px;
  padding: 6px 8px;
  margin: 0;
  font-size: 13px;
  color: #f56c6c;
  display: flex;
  flex-direction: column;
  font-weight: 600;
  box-shadow: 0 1px 4px rgba(245, 108, 108, 0.08);
  min-height: 92px;
  height: 100%;
  box-sizing: border-box;
}
.emotion-content {
  color: #303133;
  font-weight: 500;
  margin-left: 2px;
  margin-top: 2px;
}
.emotion-content.good { color: #67c23a; }
.emotion-content.warning { color: #e6a23c; }
.emotion-content.danger { color: #f56c6c; }
.emotion-content.neutral { color: #909399; }
.emotion-details {
  margin-top: 4px;
  font-size: 11px;
}
.emotion-score {
  color: #606266;
  font-weight: 400;
}
.face-attribute-bar {
  width: 100%;
  background: #ecf5ff;
  border-radius: 8px;
  padding: 6px 8px;
  margin: 0;
  font-size: 13px;
  color: #409eff;
  display: flex;
  flex-direction: column;
  font-weight: 600;
  box-shadow: 0 1px 4px rgba(64, 158, 255, 0.08);
  min-height: 92px;
  height: 100%;
  box-sizing: border-box;
}
.attribute-list {
  margin-top: 4px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.attribute-item {
  display: flex;
  align-items: center;
  font-size: 12px;
}
.attr-label {
  color: #606266;
  font-weight: 500;
  min-width: 46px;
}
.attr-value {
  font-weight: 600;
  margin-left: 4px;
}
.attr-value.good { color: #67c23a; }
.attr-value.warning { color: #e6a23c; }
.attr-value.danger { color: #f56c6c; }
.attr-value.neutral { color: #909399; }
.ai-hint-bar {
  width: 100%;
  background: #fdf6ec;
  border-radius: 8px;
  padding: 12px;
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #e6a23c;
  display: flex;
  align-items: center;
  font-weight: 600;
  box-shadow: 0 1px 6px rgba(230, 162, 60, 0.1);
  min-height: 56px;
  max-height: 72px;
}
.ai-hint-content {
  color: #303133;
  font-weight: 500;
  margin-left: 4px;
}
.ai-answer-hint-bar {
  width: 100%;
  background: #ecf5ff;
  border-radius: 8px;
  padding: 16px;
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #409eff;
  display: flex;
  flex-direction: column;
  font-weight: 600;
  box-shadow: 0 1px 6px rgba(64, 158, 255, 0.1);
  min-height: 92px;
  max-height: 136px;
}
.ai-answer-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #409eff;
  font-weight: 600;
}
.speaking-indicator {
  margin-left: auto;
  font-size: 12px;
  color: #67c23a;
  font-weight: 500;
  animation: pulse 1.5s infinite;
}
.ai-answer-content {
  color: #303133;
  font-weight: 500;
  line-height: 1.5;
  word-wrap: break-word;
  white-space: pre-wrap;
  flex: 1;
  overflow-y: auto;
  font-size: 13px;
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
</style>
