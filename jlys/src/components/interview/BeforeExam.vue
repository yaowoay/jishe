<template>
  <div class="before-exam-container">
    <div class="top-header-bar">
      <h2><span class="title-gradient">{{ interviewType === 'official' ? '正式面试' : '模拟面试' }}前设置</span></h2>
      <p>请完成以下三个步骤的设置，系统将为您生成个性化的面试体验</p>
      <el-progress :percentage="progressPercent" :stroke-width="16" status="success" class="custom-progress"/>
      <div class="divider"></div>
    </div>

    <div class="steps-flex-wrapper">
      <ResumeUpload 
        :hovered="hoveredStep === 0"
        :active="forceActiveStep === 0"
        :resume-file="resumeFile"
        @mouseenter="hoveredStep = 0"
        @mouseleave="onStepMouseLeave(0)"
        @file-change="handleResumeChange"
      />
      
      <ExamSettings 
        :hovered="hoveredStep === 1"
        :active="forceActiveStep === 1"
        :settings="examSettings"
        :is-complete="isStepComplete(1)"
        @mouseenter="hoveredStep = 1"
        @mouseleave="onStepMouseLeave(1)"
        @update="updateExamSettings"
      />
      
      <AvatarSettings 
        :hovered="hoveredStep === 2"
        :active="forceActiveStep === 2"
        :settings="avatarSettings"
        :is-complete="isStepComplete(2)"
        @mouseenter="hoveredStep = 2"
        @mouseleave="onStepMouseLeave(2)"
        @update="updateAvatarSettings"
      />
    </div>

    <div v-if="allStepsFinished" class="start-btn-bar-fixed">
      <el-button type="success" size="large" @click="startExam" :loading="loading" class="start-btn-strong">开始面试</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue' 
import { useRouter, useRoute } from 'vue-router' 
import { ElMessage } from 'element-plus' 
import request from '@/utils/request' 
import ResumeUpload from './steps/ResumeUpload.vue' 
import ExamSettings from './steps/ExamSettings.vue' 
import AvatarSettings from './steps/AvatarSettings.vue' 

const router = useRouter() 
const route = useRoute() 

const interviewType = ref('simulation') 
const hoveredStep = ref(null) 
const forceActiveStep = ref(null) 
const loading = ref(false) 
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
  avatarType: '',
  tone: '',
  speechRate: 1.0,
  audioTest: false,
  videoTest: false
}) 

const progressPercent = computed(() => {
  let finished = 0 
  if (resumeFile.value) finished++ 
  const examOk = examSettings.jobPosition && examSettings.skills && examSettings.experience && 
                 examSettings.questionCount && examSettings.difficultyLevel && examSettings.focusArea 
  if (examOk) finished++ 
  const avatarOk = avatarSettings.avatarType && avatarSettings.tone && avatarSettings.speechRate && 
                   avatarSettings.audioTest && avatarSettings.videoTest 
  if (avatarOk) finished++ 
  return Math.round((finished / 3) * 100) 
}) 

const allStepsFinished = computed(() => progressPercent.value === 100) 

function isStepComplete(idx) {
  if (idx === 0) return !!resumeFile.value 
  if (idx === 1) {
    return !!(examSettings.jobPosition && examSettings.skills && examSettings.experience && 
              examSettings.questionCount && examSettings.difficultyLevel && examSettings.focusArea) 
  }
  if (idx === 2) {
    return !!(avatarSettings.avatarType && avatarSettings.tone && avatarSettings.speechRate && 
              avatarSettings.audioTest && avatarSettings.videoTest) 
  }
  return false 
}

function handleResumeChange(file) {
  resumeFile.value = file 
}

function updateExamSettings(settings) {
  Object.assign(examSettings, settings) 
}

function updateAvatarSettings(settings) {
  Object.assign(avatarSettings, settings) 
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

async function startExam() {
  try {
    loading.value = true
    if (!resumeFile.value) {
      ElMessage.warning('请先上传简历文件')
      return
    }

    // 验证笔试设置是否完整
    if (!examSettings.jobPosition || !examSettings.skills || !examSettings.experience ||
        !examSettings.questionCount || !examSettings.difficultyLevel || !examSettings.focusArea) {
      ElMessage.warning('请完善笔试设置信息')
      return
    }

    const avatarConfig = {
      avatarId: avatarSettings.avatarType,
      tone: avatarSettings.tone,
      speechRate: avatarSettings.speechRate,
      language: 'zh-CN'
    }

    sessionStorage.setItem('avatarConfig', JSON.stringify(avatarConfig))
    sessionStorage.setItem('examSettings', JSON.stringify(examSettings))

    const examRequest = {
      jobPosition: examSettings.jobPosition,
      skills: examSettings.skills,
      experience: examSettings.experience,
      questionCount: examSettings.questionCount,
      difficultyLevel: examSettings.difficultyLevel,
      focusArea: examSettings.focusArea
    }

    ElMessage.success('正在生成笔试题目，请稍候...')

    // 只调用笔试生成接口，不再调用旧的面试接口
    const examResponse = await request.post('/exam/generate', examRequest, {
      timeout: 600000
    })

    console.log('笔试生成响应:', examResponse)

    if (!examResponse || !examResponse.task_id) {
      ElMessage.error('生成笔试题目失败：' + (examResponse?.message || '未知错误'))
      return
    }

    sessionStorage.setItem('currentExamTaskId', examResponse.task_id)
    ElMessage.success('笔试题目生成成功！')

    router.push({
      path: '/layout/writtenExam',
      query: {
        interviewType: interviewType.value,
        taskId: examResponse.task_id,
        avatarConfig: JSON.stringify(avatarConfig)
      }
    })
  } catch (error) {
    console.error('生成笔试题目错误:', error)
    if (error.response && error.response.data && error.response.data.message) {
      ElMessage.error('生成题目失败：' + error.response.data.message)
    } else if (error.message) {
      ElMessage.error('生成题目失败：' + error.message)
    } else {
      ElMessage.error('生成题目失败，请检查网络连接')
    }
  } finally {
    loading.value = false
  }
}

watch(() => route.path, () => {
  interviewType.value = route.path.includes('officialInterviewSetup') ? 'official' : 'simulation'
}, { immediate: true })
</script>

<style scoped>
.before-exam-container {
  max-width: 1600px;
  margin: 40px auto;
  padding-bottom: 60px;
}

.top-header-bar {
  text-align: center;
  margin-bottom: 0;
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
  margin-bottom: 8px;
}

.top-header-bar p {
  color: #666;
  font-size: 1.2rem;
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
}

.steps-flex-wrapper {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: stretch;
  gap: 48px;
  margin: 10px 0 32px 0;
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
}

.start-btn-strong:hover {
  background: linear-gradient(90deg, #409eff 0%, #67c23a 100%);
  box-shadow: 0 12px 40px #67c23a44;
}

@media (max-width: 1200px) {
  .steps-flex-wrapper {
    flex-direction: column;
    gap: 28px;
    align-items: center;
  }
}
</style>
