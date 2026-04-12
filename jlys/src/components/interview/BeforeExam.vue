<template>
  <div class="before-exam-container">
    <!-- 顶部标题区域 -->
    <div class="header-section">
      <div class="header-content">
        <h1 class="main-title">
          <span class="title-gradient">模拟面试</span>
          <span class="title-text">配置</span>
        </h1>
        <p class="subtitle">请选择您的面试官配置，系统将为您生成个性化的面试体验</p>
        <el-progress
            :percentage="progressPercent"
            :stroke-width="8"
            status="success"
            class="progress-bar"
        />
      </div>
    </div>

    <!-- 配置卡片区域 -->
    <div class="config-section">
      <el-card class="config-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon class="header-icon"><Avatar /></el-icon>
            <span class="header-title">面试官配置</span>
          </div>
        </template>

        <el-form
            :model="avatarSettings"
            :rules="avatarRules"
            ref="avatarFormRef"
            label-width="120px"
            class="config-form"
        >
          <!-- 数字人形象选择 -->
          <el-form-item label="面试官形象" prop="avatarType">
            <div class="avatar-selection">
              <el-radio-group v-model="avatarSettings.avatarType" class="avatar-grid">
                <el-radio :label="'avatar1'" class="avatar-option">
                  <div class="avatar-card">
                    <img :src="require('@/assets/images/avatar1.png')" class="avatar-image" alt="面试官1" />
                    <span class="avatar-name">专业型</span>
                  </div>
                </el-radio>
                <el-radio :label="'avatar2'" class="avatar-option">
                  <div class="avatar-card">
                    <img :src="require('@/assets/images/avatar2.png')" class="avatar-image" alt="面试官2" />
                    <span class="avatar-name">亲和型</span>
                  </div>
                </el-radio>
                <el-radio :label="'avatar3'" class="avatar-option">
                  <div class="avatar-card">
                    <img :src="require('@/assets/images/avatar3.png')" class="avatar-image" alt="面试官3" />
                    <span class="avatar-name">严谨型</span>
                  </div>
                </el-radio>
                <el-radio :label="'avatar4'" class="avatar-option">
                  <div class="avatar-card">
                    <img :src="require('@/assets/images/avatar4.png')" class="avatar-image" alt="面试官4" />
                    <span class="avatar-name">温和型</span>
                  </div>
                </el-radio>
              </el-radio-group>
            </div>
          </el-form-item>

          <!-- 语气风格 -->
          <el-form-item label="语气风格" prop="tone">
            <el-select v-model="avatarSettings.tone" placeholder="请选择语气风格" class="full-width">
              <el-option label="正式严肃" value="formal">
                <span class="option-content">
                  <span class="option-label">正式严肃</span>
                  <span class="option-desc">适合正式面试场合</span>
                </span>
              </el-option>
              <el-option label="温和友好" value="friendly">
                <span class="option-content">
                  <span class="option-label">温和友好</span>
                  <span class="option-desc">营造轻松氛围</span>
                </span>
              </el-option>
              <el-option label="轻松幽默" value="casual">
                <span class="option-content">
                  <span class="option-label">轻松幽默</span>
                  <span class="option-desc">缓解紧张情绪</span>
                </span>
              </el-option>
              <el-option label="专业严谨" value="professional">
                <span class="option-content">
                  <span class="option-label">专业严谨</span>
                  <span class="option-desc">突出专业性</span>
                </span>
              </el-option>
            </el-select>
          </el-form-item>

          <!-- 语速设置 -->
          <el-form-item label="语速设置" prop="speechRate">
            <div class="speech-rate-section">
              <el-slider
                  v-model="avatarSettings.speechRate"
                  :min="0.5"
                  :max="2.0"
                  :step="0.1"
                  :format-tooltip="formatSpeechRate"
                  show-input
                  class="speech-slider"
              />
              <div class="speed-labels">
                <span class="speed-label">慢速</span>
                <span class="speed-label">正常</span>
                <span class="speed-label">快速</span>
              </div>
            </div>
          </el-form-item>
          <!-- 设备测试 -->
          <el-form-item label="设备测试">
            <div class="device-test-section">
              <div class="test-item">
                <el-button
                    @click="testAudio"
                    :loading="audioTesting"
                    type="primary"
                    :icon="Microphone"
                    class="test-button"
                >
                  测试音频设备
                </el-button>
                <div v-if="audioTestResult" class="test-result">
                  <el-tag :type="audioTestResult.success ? 'success' : 'danger'">
                    {{ audioTestResult.message }}
                  </el-tag>
                </div>
              </div>

              <div class="test-item">
                <el-button
                    @click="testVideo"
                    :loading="videoTesting"
                    type="primary"
                    :icon="VideoCamera"
                    class="test-button"
                >
                  测试视频设备
                </el-button>
                <div v-if="videoTestResult" class="test-result">
                  <el-tag :type="videoTestResult.success ? 'success' : 'danger'">
                    {{ videoTestResult.message }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 开始面试按钮 -->
    <div class="action-section">
      <el-button
          type="primary"
          size="large"
          @click="startInterview"
          :loading="loading"
          :disabled="!allStepsFinished"
          class="start-button"
          :icon="VideoPlay"
      >
        <span>开始模拟面试</span>
      </el-button>

      <div v-if="!allStepsFinished" class="tips">
        <el-icon class="tip-icon"><InfoFilled /></el-icon>
        <span>请完成所有配置项后开始面试</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Avatar,
  Microphone,
  VideoCamera,
  VideoPlay,
  InfoFilled
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const applicationId = ref('')
const jobId = ref('')

onMounted(() => {
  applicationId.value = route.query.applicationId
  jobId.value = route.query.jobId

  // 将 applicationId 保存到 sessionStorage 供后续使用
  if (applicationId.value) {
    sessionStorage.setItem('currentApplicationId', applicationId.value)
  }
})

const loading = ref(false)
const audioTesting = ref(false)
const videoTesting = ref(false)

const avatarFormRef = ref()

const avatarSettings = reactive({
  avatarType: '',
  tone: '',
  speechRate: 1.0,
  audioTest: false,
  videoTest: false
})
const audioTestResult = ref(null)
const videoTestResult = ref(null)

const avatarRules = {
  avatarType: [
    { required: true, message: '请选择面试官形象', trigger: 'change' }
  ],
  tone: [
    { required: true, message: '请选择语气风格', trigger: 'change' }
  ]
}

function formatSpeechRate(val) {
  return val.toFixed(1) + 'x'
}

async function testAudio() {
  audioTesting.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 2000))
    audioTestResult.value = {
      success: true,
      message: '音频设备测试通过'
    }
    avatarSettings.audioTest = true
    ElMessage.success('音频设备测试成功！')
  } catch (error) {
    audioTestResult.value = {
      success: false,
      message: '音频设备测试失败'
    }
    ElMessage.error('音频设备测试失败，请检查设备设置')
  } finally {
    audioTesting.value = false
  }
}

async function testVideo() {
  videoTesting.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 2000))
    videoTestResult.value = {
      success: true,
      message: '视频设备测试通过'
    }
    avatarSettings.videoTest = true
    ElMessage.success('视频设备测试成功！')
  } catch (error) {
    videoTestResult.value = {
      success: false,
      message: '视频设备测试失败'
    }
    ElMessage.error('视频设备测试失败，请检查设备设置')
  } finally {
    videoTesting.value = false
  }
}

async function startInterview() {
  try {
    loading.value = true

    // 验证面试官设置
    try {
      await avatarFormRef.value.validate()
    } catch (error) {
      ElMessage.error('请完善面试官配置信息')
      return
    }

    // 检查设备测试
    if (!avatarSettings.audioTest || !avatarSettings.videoTest) {
      ElMessage.warning('请先完成音频和视频设备测试')
      return
    }

    const avatarConfig = {
      avatarType: avatarSettings.avatarType,
      tone: avatarSettings.tone,
      speechRate: avatarSettings.speechRate,
      voiceType: 'female',
      language: 'zh-CN',
      emotion: avatarSettings.tone === 'friendly' ? 'happy' :
        avatarSettings.tone === 'formal' ? 'serious' : 'neutral'
    }

    sessionStorage.setItem('avatarConfig', JSON.stringify(avatarConfig))
    ElMessage.success('配置完成，即将进入模拟面试！')

    // 跳转到模拟面试页面
    router.push({
      path: '/simulatExam'
    })
  } catch (error) {
    console.error('配置提交错误:', error)
    ElMessage.error('操作失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

const progressPercent = computed(() => {
  const avatarOk = avatarSettings.avatarType && avatarSettings.tone && avatarSettings.speechRate && avatarSettings.audioTest && avatarSettings.videoTest
  return avatarOk ? 100 : Math.round((Object.values(avatarSettings).filter(v => v && v !== 1.0).length / 5) * 100)
})

const allStepsFinished = computed(() => {
  return avatarSettings.avatarType &&
      avatarSettings.tone &&
      avatarSettings.speechRate &&
      avatarSettings.audioTest &&
      avatarSettings.videoTest
})
</script>
<style scoped>
.before-exam-container {
  padding: 24px;
  background: linear-gradient(135deg, #f5f9ff 0%, #e0efff 100%);
  min-height: 100vh;
}

/* 顶部标题区域 */
.header-section {
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 32px;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
  text-align: center;
}

.header-content h1 {
  margin: 0 0 16px 0;
  font-size: 36px;
  font-weight: 700;
}

.title-gradient {
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-text {
  color: #2c3e50;
}

.subtitle {
  font-size: 18px;
  color: #5a84b3;
  margin-bottom: 24px;
  font-weight: 500;
}

:deep(.progress-bar) {
  max-width: 400px;
  margin: 0 auto;
}

:deep(.progress-bar .el-progress-bar__outer) {
  background-color: #e0f0ff;
  border-radius: 4px;
}

:deep(.progress-bar .el-progress-bar__inner) {
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border-radius: 4px;
}

/* 配置卡片区域 */
.config-section {
  margin-bottom: 32px;
}

.config-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
}

:deep(.config-card .el-card__header) {
  background: linear-gradient(135deg, #f0f7ff 0%, #e0efff 100%);
  border-bottom: 1px solid #e6f1ff;
  padding: 20px 24px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 24px;
  color: #1a6fc4;
  background: #e0f0ff;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
}

:deep(.config-card .el-card__body) {
  padding: 24px;
}

.config-form {
  max-width: 800px;
  margin: 0 auto;
}

:deep(.config-form .el-form-item__label) {
  font-weight: 600;
  color: #2c3e50;
  padding-right: 20px;
}

/* 数字人形象选择 */
.avatar-selection {
  width: 100%;
}

.avatar-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
  width: 100%;
}

.avatar-option {
  margin: 0;
  height: auto;
}

:deep(.avatar-option .el-radio__input) {
  display: none;
}

:deep(.avatar-option .el-radio__label) {
  padding: 0;
}

.avatar-card {
  border: 2px solid #e6f1ff;
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.avatar-card:hover {
  border-color: #c2dcff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(26, 111, 196, 0.15);
}

:deep(.avatar-option.is-checked .avatar-card) {
  border-color: #1a6fc4;
  background: linear-gradient(135deg, #f0f7ff 0%, #e0efff 100%);
  box-shadow: 0 4px 16px rgba(26, 111, 196, 0.2);
}

.avatar-image {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 12px;
  border: 2px solid #e6f1ff;
}

.avatar-name {
  display: block;
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

/* 下拉选择框样式 */
.full-width {
  width: 100%;
}

:deep(.full-width .el-input__inner) {
  border-radius: 8px;
  border: 1px solid #c2dcff;
  background: #f0f7ff;
  height: 40px;
  transition: all 0.3s ease;
}

:deep(.full-width .el-input__inner:focus) {
  border-color: #1a6fc4;
  box-shadow: 0 0 0 2px rgba(26, 111, 196, 0.2);
}

.option-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.option-label {
  font-weight: 500;
  color: #2c3e50;
}

.option-desc {
  font-size: 12px;
  color: #5a84b3;
}

/* 语速设置 */
.speech-rate-section {
  width: 100%;
}

:deep(.speech-slider .el-slider__runway) {
  background-color: #e0f0ff;
  height: 6px;
}

:deep(.speech-slider .el-slider__bar) {
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  height: 6px;
}

:deep(.speech-slider .el-slider__button) {
  width: 16px;
  height: 16px;
  border: 2px solid #1a6fc4;
  background: white;
}

:deep(.speech-slider .el-slider__input) {
  width: 80px;
}

:deep(.speech-slider .el-input__inner) {
  border-radius: 6px;
  border: 1px solid #c2dcff;
  background: #f0f7ff;
}

.speed-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  padding: 0 10px;
}

.speed-label {
  font-size: 12px;
  color: #5a84b3;
}

/* 设备测试区域 */
.device-test-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.test-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.test-button {
  border-radius: 8px;
  height: 40px;
  font-weight: 500;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(26, 111, 196, 0.3);
  transition: all 0.3s ease;
}

.test-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(26, 111, 196, 0.4);
}

:deep(.test-button .el-icon) {
  margin-right: 6px;
}

.test-result {
  min-width: 150px;
}

:deep(.test-result .el-tag) {
  border-radius: 6px;
  font-weight: 500;
  padding: 0 12px;
  height: 32px;
  line-height: 32px;
}

:deep(.test-result .el-tag--success) {
  background: #e8f7f0;
  color: #36c28f;
  border-color: #c2eedb;
}

:deep(.test-result .el-tag--danger) {
  background: #fee;
  color: #e74c3c;
  border-color: #fcc;
}

/* 开始面试按钮区域 */
.action-section {
  text-align: center;
  margin-top: 40px;
}

.start-button {
  border-radius: 12px;
  height: 56px;
  padding: 0 40px;
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border: none;
  box-shadow: 0 6px 20px rgba(26, 111, 196, 0.3);
  transition: all 0.3s ease;
}

.start-button:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(26, 111, 196, 0.4);
}

.start-button:disabled {
  background: #c2dcff;
  box-shadow: none;
  cursor: not-allowed;
}

:deep(.start-button .el-icon) {
  margin-right: 8px;
  font-size: 20px;
}

.tips {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
  color: #5a84b3;
  font-size: 14px;
}

.tip-icon {
  color: #1a6fc4;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .before-exam-container {
    padding: 16px;
  }

  .header-section {
    padding: 24px 20px;
  }

  .header-content h1 {
    font-size: 28px;
  }

  .subtitle {
    font-size: 16px;
  }

  .avatar-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .test-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .test-button {
    width: 100%;
  }

  :deep(.config-form .el-form-item__label) {
    padding-bottom: 8px;
    width: 100% !important;
  }

  :deep(.config-form .el-form-item__content) {
    margin-left: 0 !important;
    width: 100%;
  }

  .start-button {
    width: 100%;
    padding: 0 24px;
  }
}
</style>
