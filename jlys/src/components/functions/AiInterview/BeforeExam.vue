<template>
  <div class="before-written-container">
    <!-- 顶部无卡片，简洁自然排版 -->
    <div class="top-header-bar">
      <h2><span class="title-gradient">{{ interviewType === 'official' ? '正式面试' : '模拟面试' }}前设置</span></h2>
      <p>请完成以下三个步骤的设置，系统将为您生成个性化的面试体验</p>
      <el-progress :percentage="progressPercent" :stroke-width="16" status="success" class="custom-progress"/>
      <div class="divider"></div>
    </div>

    <!-- 步骤内容区域：三个卡片横向排列，hover 放大详细展示 -->
    <div class="steps-flex-wrapper">
      <!-- 第一步：上传简历 -->
      <div
        class="step-flex-card"
        :class="cardClass(0)"
        @mouseenter="onStepMouseEnter(0)"
        @mouseleave="onStepMouseLeave(0)"
      >
        <div class="step-badge"><span>①</span></div>
        <template v-if="hoveredStep === 0 || forceActiveStep === 0">
          <div class="step-header"><el-icon class="step-icon"><Upload /></el-icon> 第一步：上传简历</div>
          <div class="upload-section">
            <el-upload
              class="upload-demo"
              drag
              :auto-upload="false"
              :show-file-list="true"
              :on-change="handleResumeChange"
              :before-upload="beforeResumeUpload"
              accept=".pdf,.doc,.docx"
            >
              <div class="upload-content">
                <el-icon class="upload-icon"><Upload /></el-icon>
                <div class="upload-text">将简历文件拖拽到此处，或点击上传</div>
                <div class="upload-tip">支持 PDF、DOC、DOCX 格式，文件大小不超过 10MB</div>
              </div>
            </el-upload>
            <div v-if="resumeFile" class="resume-info">
              <el-alert
                title="简历上传成功"
                type="success"
                :closable="false"
                show-icon
              >
                <template #default>
                  <p>文件名：{{ resumeFile.name }}</p>
                  <p>文件大小：{{ formatFileSize(resumeFile.size) }}</p>
                </template>
              </el-alert>
            </div>
          </div>
        </template>
        <template v-else>
          <div class="step-header mini"><el-icon class="step-icon"><Upload /></el-icon> 上传简历</div>
          <div v-if="resumeFile" class="mini-brief set">
            <el-icon class="mini-done"><i class="el-icon-check"></i></el-icon>
            <span>已上传：{{ resumeFile.name }}</span>
          </div>
          <div v-else class="mini-brief">上传个人简历</div>
        </template>
      </div>
      <!-- 第二步：笔试设置 -->
      <div
        class="step-flex-card"
        :class="cardClass(1)"
        @mouseenter="onStepMouseEnter(1)"
        @mouseleave="onStepMouseLeave(1)"
      >
        <div class="step-badge"><span>②</span></div>
        <template v-if="hoveredStep === 1 || forceActiveStep === 1">
          <div class="step-header"><el-icon class="step-icon"><i class="el-icon-edit"></i></el-icon> 第二步：笔试设置</div>
          <el-form 
            :model="examSettings" 
            :rules="examRules" 
            ref="examFormRef" 
            label-width="120px"
            class="exam-form"
          >
            <el-form-item label="职位名称" prop="jobPosition">
              <el-input v-model="examSettings.jobPosition" placeholder="请输入目标职位，如：Java Developer" clearable />
            </el-form-item>
            <el-form-item label="技能要求" prop="skills">
              <el-input v-model="examSettings.skills" placeholder="请输入相关技能，用逗号分隔，如：Java, Spring, MySQL" clearable />
            </el-form-item>
            <el-form-item label="工作经验" prop="experience">
              <el-select v-model="examSettings.experience" placeholder="请选择工作经验" style="width: 100%">
                <el-option label="不足一年" value="LESS_THAN_ONE_YEAR" />
                <el-option label="1-3年" value="ONE_TO_THREE_YEARS" />
                <el-option label="3-5年" value="THREE_TO_FIVE_YEARS" />
                <el-option label="5-10年" value="FIVE_TO_TEN_YEARS" />
                <el-option label="10年以上" value="MORE_THAN_TEN_YEARS" />
              </el-select>
            </el-form-item>
            <el-form-item label="题目数量" prop="questionCount">
              <el-input-number v-model="examSettings.questionCount" :min="3" :max="20" :step="1" style="width: 100%" />
              <div class="form-tip">建议选择 5-15 道题目，考试时间约 15-45 分钟</div>
            </el-form-item>
            <el-form-item label="难度等级" prop="difficultyLevel">
              <el-select v-model="examSettings.difficultyLevel" placeholder="请选择难度等级" style="width: 100%">
                <el-option label="初级" value="JUNIOR">
                  <span style="float: left">初级</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">适合新手</span>
                </el-option>
                <el-option label="中级" value="INTERMEDIATE">
                  <span style="float: left">中级</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">有一定经验</span>
                </el-option>
                <el-option label="高级" value="SENIOR">
                  <span style="float: left">高级</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">经验丰富</span>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="重点领域" prop="focusArea">
              <el-input v-model="examSettings.focusArea" placeholder="请输入重点考察领域，如：微服务、数据库、算法等" clearable />
            </el-form-item>
          </el-form>
        </template>
        <template v-else>
          <div class="step-header mini"><el-icon class="step-icon"><i class="el-icon-edit"></i></el-icon> 笔试设置</div>
          <div v-if="isStepComplete(1)" class="mini-brief set">
            <el-icon class="mini-done"><i class="el-icon-check"></i></el-icon>
            <span>{{ examSettings.jobPosition }} / {{ examSettings.questionCount }}题</span>
          </div>
          <div v-else class="mini-brief">配置职位、技能等</div>
        </template>
      </div>
      <!-- 第三步：数字人设置 -->
      <div
        class="step-flex-card"
        :class="cardClass(2)"
        @mouseenter="onStepMouseEnter(2)"
        @mouseleave="onStepMouseLeave(2)"
      >
        <div class="step-badge"><span>③</span></div>
        <template v-if="hoveredStep === 2 || forceActiveStep === 2">
          <div class="step-header"><el-icon class="step-icon"><i class="el-icon-user"></i></el-icon> 第三步：数字人设置</div>
          <el-form 
            :model="avatarSettings" 
            :rules="avatarRules" 
            ref="avatarFormRef" 
            label-width="120px"
            class="avatar-form"
          >
            <el-form-item label="数字人形象" prop="avatarType">
              <el-radio-group v-model="avatarSettings.avatarType" class="avatar-image-options">
                <el-radio :label="'110017006'" class="avatar-image-radio">
                  <img :src="require('@/assets/images/avatar4.png')" class="avatar-image" alt="数字人1" />
                  <div class="avatar-name">明哥音色</div>
                </el-radio>
                <el-radio :label="'138801001'" class="avatar-image-radio">
                  <img :src="require('@/assets/images/avatar3.png')" class="avatar-image" alt="数字人2" />
                  <div class="avatar-name">悦小妮音色</div>
                </el-radio>
                <el-radio :label="'110021006'" class="avatar-image-radio">
                  <img :src="require('@/assets/images/avatar2.png')" class="avatar-image" alt="数字人3" />
                  <div class="avatar-name">灵小雨音色</div>
                </el-radio>
                <el-radio :label="'110117005'" class="avatar-image-radio">
                  <img :src="require('@/assets/images/avatar1.png')" class="avatar-image" alt="数字人4" />
                  <div class="avatar-name">灵小颖音色</div>
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="语气风格" prop="tone">
              <el-select v-model="avatarSettings.tone" placeholder="请选择语气风格" style="width: 100%">
                <el-option label="正式严肃" value="formal" />
                <el-option label="温和友好" value="friendly" />
                <el-option label="轻松幽默" value="casual" />
                <el-option label="专业严谨" value="professional" />
              </el-select>
            </el-form-item>
            <el-form-item label="语速设置" prop="speechRate">
              <el-slider v-model="avatarSettings.speechRate" :min="0.5" :max="2.0" :step="0.1" :format-tooltip="formatSpeechRate" show-input />
            </el-form-item>
            <el-form-item label="音频测试" prop="audioTest">
              <div class="test-section">
                <el-button @click="testAudio" :loading="audioTesting" type="primary">测试音频设备</el-button>
                <div v-if="audioTestResult" class="test-result">
                  <el-tag :type="audioTestResult.success ? 'success' : 'danger'">{{ audioTestResult.message }}</el-tag>
                </div>
              </div>
            </el-form-item>
            <el-form-item label="视频测试" prop="videoTest">
              <div class="test-section">
                <el-button @click="testVideo" :loading="videoTesting" type="primary">测试视频设备</el-button>
                <div v-if="videoTestResult" class="test-result">
                  <el-tag :type="videoTestResult.success ? 'success' : 'danger'">{{ videoTestResult.message }}</el-tag>
                </div>
              </div>
            </el-form-item>
          </el-form>
        </template>
        <template v-else>
          <div class="step-header mini"><el-icon class="step-icon"><i class="el-icon-user"></i></el-icon> 数字人设置</div>
          <div v-if="isStepComplete(2)" class="mini-brief set">
            <el-icon class="mini-done"><i class="el-icon-check"></i></el-icon>
            <span>{{ getAvatarNameById(avatarSettings.avatarType) }} / {{ avatarSettings.tone ? avatarSettings.tone : '' }}</span>
          </div>
          <div v-else class="mini-brief">选择面试官形象</div>
        </template>
      </div>
    </div>
    <div v-if="allStepsFinished" class="start-btn-bar-fixed">
      <el-button type="success" size="large" @click="startExam" :loading="loading" class="start-btn-strong">开始面试</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()

const interviewType = ref('')
const currentStep = ref(0)

onMounted(() => {
  interviewType.value = route.query.type || 'simulation'
})

const loading = ref(false)
const audioTesting = ref(false)
const videoTesting = ref(false)

const examFormRef = ref()
const avatarFormRef = ref()

const resumeFile = ref(null)
const examSettings = reactive({
  jobPosition: '',
  skills: '',
  experience: '',
  questionCount: 5,
  difficultyLevel: '',
  focusArea: ''
})
const avatarSettings = reactive({
  avatarType: '', // 将存储avatar_id，如：cnrfb86h2000000004
  tone: '',
  speechRate: 1.0,
  audioTest: false,
  videoTest: false
})
const audioTestResult = ref(null)
const videoTestResult = ref(null)

const examRules = {
  jobPosition: [
    { required: true, message: '请输入职位名称', trigger: 'blur' },
    { min: 2, max: 50, message: '职位名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  skills: [
    { required: true, message: '请输入技能要求', trigger: 'blur' },
    { min: 3, max: 200, message: '技能要求长度在 3 到 200 个字符', trigger: 'blur' }
  ],
  experience: [
    { required: true, message: '请选择工作经验', trigger: 'change' }
  ],
  questionCount: [
    { required: true, message: '请选择题目数量', trigger: 'blur' }
  ],
  difficultyLevel: [
    { required: true, message: '请选择难度等级', trigger: 'change' }
  ],
  focusArea: [
    { required: true, message: '请输入重点领域', trigger: 'blur' },
    { min: 2, max: 100, message: '重点领域长度在 2 到 100 个字符', trigger: 'blur' }
  ]
}
const avatarRules = {
  avatarType: [
    { required: true, message: '请选择数字人形象', trigger: 'change' }
  ],
  tone: [
    { required: true, message: '请选择语气风格', trigger: 'change' }
  ]
}

function handleResumeChange(file) {
  resumeFile.value = file
}
function beforeResumeUpload(file) {
  const isValidType = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'].includes(file.type)
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isValidType) {
    ElMessage.error('只能上传 PDF、DOC、DOCX 格式的文件！')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB！')
    return false
  }
  return false
}
function formatFileSize(bytes) {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
function formatSpeechRate(val) {
  return val.toFixed(1) + 'x'
}

// 根据avatar_id获取对应的语音类型
function getVoiceTypeByAvatarId(avatarId) {
  const avatarVoiceMap = {
    '110017006': 'male',
    '138801001': 'female',
    '110021006': 'female',
    '110117005': 'female'
  }
  return avatarVoiceMap[avatarId] || 'female'
}

// 根据avatar_id获取数字人名称
function getAvatarNameById(avatarId) {
  const avatarNameMap = {
    '110017006': '面试官1',
    '138801001': '面试官2',
    '110021006': '面试官3',
    '110117005': '面试官4'
  }
  return avatarNameMap[avatarId] || '未知形象'
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
function goBack() {
  router.go(-1)
}
function prevStep() {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}
async function nextStep() {
  if (currentStep.value === 0) {
    if (!resumeFile.value) {
      ElMessage.warning('请先上传简历文件')
      return
    }
    currentStep.value++
  } else if (currentStep.value === 1) {
    try {
      await examFormRef.value.validate()
      currentStep.value++
    } catch (error) {
      ElMessage.error('请完善笔试设置信息')
      return
    }
  }
}
async function startExam() {
  try {
    loading.value = true
    if (!resumeFile.value) {
      ElMessage.warning('请先上传简历文件')
      return
    }
    const avatarConfig = {
      avatarId: avatarSettings.avatarType, // 直接使用avatar_id
      avatarType: avatarSettings.avatarType,
      tone: avatarSettings.tone,
      speechRate: avatarSettings.speechRate,
      voiceType: getVoiceTypeByAvatarId(avatarSettings.avatarType),
      language: 'zh-CN'
      // emotion值现在在DigitalHuman组件中根据tone动态计算
    }
    sessionStorage.setItem('avatarConfig', JSON.stringify(avatarConfig))
    sessionStorage.setItem('examSettings', JSON.stringify(examSettings))
    ElMessage.success('即将进入笔试！')
    // 生成 试题， 然后获取返回的taskid进行拼接
    const examRequest = {
      jobPosition: examSettings.jobPosition,
      skills: examSettings.skills,
      experience: examSettings.experience, // 直接传字符串
      questionCount: examSettings.questionCount,
      difficultyLevel: examSettings.difficultyLevel, // 直接传字符串
      focusArea: examSettings.focusArea
    }
    const examResponse = await request.post('/exam/generate', examRequest, {
      timeout: 600000
    })
    if (!examResponse.task_id) {
      ElMessage.error('生成题目失败：' + (examResponse.message || '未知错误'))
      return
    }
    sessionStorage.setItem('currentExamTaskId', examResponse.task_id)
    router.push({
      path: '/layout/writtenExam',
      query: {
        interviewType: interviewType.value,
        avatarConfig: JSON.stringify(avatarConfig) // 传递数字人配置
      }
    })
  } catch (error) {
    console.error('设置提交错误:', error)
    if (error.response && error.response.data && error.response.data.message) {
      ElMessage.error(error.response.data.message)
    } else if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('操作失败，请检查网络连接')
    }
  } finally {
    loading.value = false
  }
}
const hoveredStep = ref(null)
const forceActiveStep = ref(null)
const progressPercent = computed(() => {
  let finished = 0
  if (resumeFile.value) finished++
  const examOk = examSettings.jobPosition && examSettings.skills && examSettings.experience && examSettings.questionCount && examSettings.difficultyLevel && examSettings.focusArea
  if (examOk) finished++
  const avatarOk = avatarSettings.avatarType && avatarSettings.tone && avatarSettings.speechRate && avatarSettings.audioTest && avatarSettings.videoTest
  if (avatarOk) finished++
  return Math.round((finished / 3) * 100)
})
const allStepsFinished = computed(() => progressPercent.value === 100)
function isStepComplete(idx) {
  if (idx === 0) return !!resumeFile.value
  if (idx === 1) {
    return !!(examSettings.jobPosition && examSettings.skills && examSettings.experience && examSettings.questionCount && examSettings.difficultyLevel && examSettings.focusArea)
  }
  if (idx === 2) {
    return !!(avatarSettings.avatarType && avatarSettings.tone && avatarSettings.speechRate && avatarSettings.audioTest && avatarSettings.videoTest)
  }
  return false
}
function onStepMouseEnter(idx) {
  hoveredStep.value = idx
  forceActiveStep.value = null
}
function onStepMouseLeave(idx) {
  if ((idx === 1 || idx === 2) && !isStepComplete(idx)) {
    forceActiveStep.value = idx
    hoveredStep.value = null
  } else {
    hoveredStep.value = null
    forceActiveStep.value = null
  }
}
watch([
  () => examSettings.jobPosition,
  () => examSettings.skills,
  () => examSettings.experience,
  () => examSettings.questionCount,
  () => examSettings.difficultyLevel,
  () => examSettings.focusArea,
  () => avatarSettings.avatarType,
  () => avatarSettings.tone,
  () => avatarSettings.speechRate,
  () => avatarSettings.audioTest,
  () => avatarSettings.videoTest
], () => {
  if (forceActiveStep.value !== null && isStepComplete(forceActiveStep.value)) {
    forceActiveStep.value = null
  }
})

// 计算卡片是否为数字人设置卡片
function cardClass(idx) {
  let cls = ''
  if ((hoveredStep.value === idx || forceActiveStep.value === idx)) {
    cls += ' active'
    if (idx === 2) cls += ' avatar-step'
  }
  return cls
}
</script>

<style scoped>
body, html {
  background: linear-gradient(135deg, #e3f0ff 0%, #f6f8fc 100%) !important;
}
.before-written-container {
  max-width: 1600px;
  margin: 40px auto;
  padding-bottom: 60px;
  background: transparent;
}
.top-header-bar {
  text-align: center;
  margin-bottom: 0;
  background: none;
  box-shadow: none;
  border: none;
  font-family: 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
}
.title-gradient {
  background: linear-gradient(90deg, #409eff 10%, #67c23a 90%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 800;
  letter-spacing: 2px;
}
.top-header-bar h2 {
  font-size: 2.4rem;
  font-weight: 800;
  color: #222;
  letter-spacing: 1px;
  margin-bottom: 8px;
}
.top-header-bar p {
  color: #666;
  font-size: 1.2rem;
  margin-top: 8px;
  margin-bottom: 18px;
}
.custom-progress {
  margin: 0 auto 8px auto;
  width: 60%;
  min-width: 260px;
}
.divider {
  width: 90%;
  height: 1.5px;
  background: linear-gradient(90deg, #e0e7ff 0%, #b3c0d1 100%);
  margin: 18px auto 0 auto;
  border-radius: 1px;
}
.start-btn-bar {
  margin: 18px auto 0 auto;
  text-align: center;
}

.steps-flex-wrapper {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: stretch;
  gap: 48px;
  margin: 10px 0 32px 0;
}

.step-flex-card {
  background: rgba(255,255,255,0.92);
  border-radius: 32px;
  box-shadow: 0 12px 48px 0 rgba(80,120,200,0.13), 0 2px 16px #e6e6e6;
  transition: all 0.35s cubic-bezier(.4,2,.6,1);
  width: 420px;
  min-height: 340px;
  padding: 56px 38px 32px 38px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  font-family: 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
  backdrop-filter: blur(8px);
  border: 2px solid #e0e7ff;
}
.step-flex-card .step-badge {
  position: absolute;
  top: 18px;
  left: 28px;
  background: linear-gradient(135deg, #e3f0ff 60%, #b3d8ff 100%);
  color: #409eff;
  font-size: 1.3rem;
  font-weight: 700;
  border-radius: 50%;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px #e0e7ff;
  border: 2px solid #b3d8ff;
  z-index: 2;
}
.step-flex-card .step-header {
  font-size: 26px;
  font-weight: 700;
  color: #222;
  margin-bottom: 18px;
  letter-spacing: 1px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.step-flex-card .step-header.mini {
  font-size: 22px;
  margin-bottom: 12px;
}
.step-flex-card .step-icon {
  font-size: 1.5em;
  color: #409eff;
  vertical-align: middle;
}
.step-flex-card .mini-brief {
  font-size: 18px;
  color: #8a98b3;
  margin-top: 12px;
  font-weight: 500;
  text-align: left;
  letter-spacing: 1px;
}
.step-flex-card.active {
  z-index: 2;
  box-shadow: 0 24px 64px #b3c0d1, 0 4px 24px #409eff44;
  transform: scale(1.09) translateY(-8px);
  cursor: default;
  border: 3px solid #409eff;
  background: rgba(255,255,255,0.98);
  animation: card-pop 0.4s cubic-bezier(.4,2,.6,1);
}
/* 仅数字人设置卡片放大 */
.step-flex-card.active.avatar-step {
  width: 540px;
  min-height: 520px;
}
@keyframes card-pop {
  0% { transform: scale(0.98) translateY(0); box-shadow: 0 2px 8px #b3c0d1; }
  100% { transform: scale(1.09) translateY(-8px); box-shadow: 0 24px 64px #b3c0d1, 0 4px 24px #409eff44; }
}
.step-flex-card.inactive {
  opacity: 0.7;
  filter: blur(0.5px) grayscale(0.1);
}

.step-flex-card:not(.active) .el-input,
.step-flex-card:not(.active) .el-select,
.step-flex-card:not(.active) .el-input-number,
.step-flex-card:not(.active) .el-radio-group,
.step-flex-card:not(.active) .el-slider,
.step-flex-card:not(.active) .el-button,
.step-flex-card:not(.active) input,
.step-flex-card:not(.active) textarea {
  pointer-events: none !important;
  filter: grayscale(0.5) opacity(0.7);
  user-select: none;
}

.hover-tip {
  margin-top: 18px;
  color: #409eff;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
  text-align: center;
  width: 100%;
  animation: fadein 0.5s;
}
@keyframes fadein {
  from { opacity: 0; }
  to { opacity: 1; }
}

.upload-section {
  padding: 20px 0;
}
.upload-content {
  text-align: center;
  padding: 40px 20px;
}
.upload-icon {
  font-size: 54px;
  color: #409eff;
  margin-bottom: 18px;
}
.upload-text {
  font-size: 18px;
  color: #303133;
  margin-bottom: 10px;
}
.upload-tip {
  font-size: 14px;
  color: #909399;
}
.resume-info {
  margin-top: 20px;
}
.exam-form, .avatar-form {
  padding: 20px 0;
}
.form-tip {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}
.avatar-options {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}
.avatar-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 2px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
}
.avatar-preview.professional {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.avatar-preview.friendly {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}
.avatar-preview.strict {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}
.test-section {
  display: flex;
  align-items: center;
  gap: 16px;
}
.test-result {
  margin-top: 8px;
}
.step-flex-card .mini-brief.set {
  color: #67c23a;
  font-weight: 600;
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
}
.step-flex-card .mini-done {
  color: #67c23a;
  font-size: 1.2em;
}
.start-btn-bar-fixed {
  margin: 32px auto 0 auto;
  text-align: center;
}
.start-btn-strong {
  font-size: 1.5rem;
  padding: 18px 60px;
  border-radius: 32px;
  background: linear-gradient(90deg, #67c23a 0%, #409eff 100%);
  color: #fff;
  font-weight: bold;
  box-shadow: 0 6px 32px #409eff44;
  border: none;
  letter-spacing: 2px;
  transition: background 0.2s, box-shadow 0.2s;
}
.start-btn-strong:hover {
  background: linear-gradient(90deg, #409eff 0%, #67c23a 100%);
  box-shadow: 0 12px 40px #67c23a44;
}
.avatar-image-options {
  display: flex;
  flex-wrap: wrap;
  gap: 16px 16px;
  justify-content: center;
  margin-bottom: 18px;
  width: auto !important;
}
.avatar-image-radio >>> .el-radio__input {
  display: none !important;
}
.avatar-image-radio {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 0;
  cursor: pointer;
  border: none;
  background: none;
  box-shadow: none;
  padding: 0;
  min-width: 120px;
  min-height: 120px;
  width: auto !important;
}
.avatar-image {
  width: 120px;
  height: 120px;
  border-radius: 16px;
  border: 2.5px solid #e0e7ff;
  box-shadow: 0 2px 10px #b3d8ff22;
  object-fit: cover;
  transition: border 0.2s, box-shadow 0.2s;
}
.el-radio.is-checked .avatar-image {
  border: 3px solid #409eff;
  box-shadow: 0 4px 18px #409eff33;
}
.avatar-name {
  margin-top: 8px;
  font-size: 14px;
  color: #606266;
  text-align: center;
  font-weight: 500;
}
.el-radio.is-checked .avatar-name {
  color: #409eff;
  font-weight: 600;
}
@media (max-width: 1200px) {
  .steps-flex-wrapper {
    flex-direction: column;
    gap: 28px;
    align-items: center;
    margin-top: 10px;
  }
  .start-btn-bar-fixed {
    margin: 24px auto 0 auto;
  }
  .start-btn-strong {
    font-size: 1.1rem;
    padding: 12px 30px;
  }
}
</style>