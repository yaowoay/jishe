<template>
  <div class="written-test-container">
    <!-- 考试信息头部 -->
    <div class="exam-header" v-if="examInfo">
      <div class="exam-title">
        <h1>{{ examInfo.jobPosition }} - 笔试</h1>
        <p>总题数：{{ examInfo.totalQuestions }}题 | 总分：{{ examInfo.totalScore }}分 | 考试时长：{{ examInfo.timeLimit }}分钟</p>
      </div>
      <div class="exam-timer" v-if="examStatus === 'in_progress'">
        <el-icon><Clock /></el-icon>
        <span class="timer-text">{{ formatTime(remainingTime) }}</span>
      </div>
    </div>

    <!-- 考试状态页面 -->
    <div v-if="examStatus === 'not_started'" class="exam-start">
      <el-card class="start-card">
        <div class="start-content">
          <el-icon class="start-icon"><DocumentChecked /></el-icon>
          <h2>准备开始笔试</h2>
          <p>请仔细阅读以下注意事项：</p>
          <ul class="exam-rules">
            <li>考试时长为 {{ examInfo?.timeLimit || 60 }} 分钟，请合理安排时间</li>
            <li>考试过程中请勿刷新页面或关闭浏览器</li>
            <li>每题只能选择一个答案，提交后无法修改</li>
            <li>请确保网络连接稳定</li>
          </ul>
          <el-button type="primary" size="large" @click="startExam" :loading="loading">
            开始考试
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 考试进行中 -->
    <div v-else-if="examStatus === 'in_progress'" class="exam-content">
      <div class="progress-bar">
        <el-progress 
          :percentage="progressPercentage" 
          :stroke-width="8"
          :show-text="false"
        />
        <span class="progress-text">{{ currentQuestionIndex + 1 }} / {{ examInfo.totalQuestions }}</span>
      </div>

      <div class="question-container" v-if="currentQuestion">
        <div class="question-header">
          <span class="question-number">第 {{ currentQuestionIndex + 1 }} 题</span>
          <span class="question-type">{{ currentQuestion.type === 'choice' ? '选择题' : '判断题' }}</span>
        </div>

        <!-- 选择题 -->
        <div v-if="currentQuestion.type === 'choice'" class="choice-question">
          <div class="question-content">{{ currentQuestion.questionContent }}</div>
          <el-radio-group v-model="userAnswers[currentQuestion.questionId]" class="options-group">
            <el-radio label="A" class="option-item">
              <span class="option-label">A.</span>
              <span class="option-text">{{ currentQuestion.optionA }}</span>
            </el-radio>
            <el-radio label="B" class="option-item">
              <span class="option-label">B.</span>
              <span class="option-text">{{ currentQuestion.optionB }}</span>
            </el-radio>
            <el-radio label="C" class="option-item">
              <span class="option-label">C.</span>
              <span class="option-text">{{ currentQuestion.optionC }}</span>
            </el-radio>
            <el-radio label="D" class="option-item">
              <span class="option-label">D.</span>
              <span class="option-text">{{ currentQuestion.optionD }}</span>
            </el-radio>
          </el-radio-group>
        </div>

        <!-- 判断题 -->
        <div v-else-if="currentQuestion.type === 'true_false'" class="true-false-question">
          <div class="question-content">{{ currentQuestion.statement }}</div>
          <el-radio-group v-model="userAnswers[currentQuestion.questionId]" class="tf-options">
            <el-radio label="true" class="tf-option">
              <el-icon><Check /></el-icon>
              <span>正确</span>
            </el-radio>
            <el-radio label="false" class="tf-option">
              <el-icon><Close /></el-icon>
              <span>错误</span>
            </el-radio>
          </el-radio-group>
        </div>

        <!-- 导航按钮 -->
        <div class="question-navigation">
          <el-button 
            @click="previousQuestion" 
            :disabled="currentQuestionIndex === 0"
          >
            上一题
          </el-button>
          <el-button 
            v-if="currentQuestionIndex < examInfo.totalQuestions - 1"
            type="primary" 
            @click="nextQuestion"
          >
            下一题
          </el-button>
          <el-button 
            v-else
            type="success" 
            @click="showSubmitDialog"
          >
            提交答案
          </el-button>
        </div>
      </div>

      <!-- 题目导航 -->
      <div class="question-nav">
        <h3>答题进度</h3>
        <div class="nav-grid">
          <div 
            v-for="(question, index) in examInfo.questions" 
            :key="question.questionId"
            class="nav-item"
            :class="{
              'current': index === currentQuestionIndex,
              'answered': userAnswers[question.questionId],
              'unanswered': !userAnswers[question.questionId]
            }"
            @click="goToQuestion(index)"
          >
            {{ index + 1 }}
          </div>
        </div>
      </div>
    </div>

    <!-- 考试完成 -->
    <div v-else-if="examStatus === 'completed'" class="exam-completed">
      <el-card class="result-card">
        <div class="result-content">
          <el-icon class="result-icon success"><CircleCheck /></el-icon>
          <h2>考试已完成</h2>
          <p>您已成功提交答案，可以查看考试结果</p>
          <el-button type="primary" @click="viewResult">查看结果</el-button>
        </div>
      </el-card>
    </div>

    <!-- 提交确认对话框 -->
    <el-dialog
      v-model="submitDialogVisible"
      title="提交确认"
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="submit-confirm">
        <el-icon class="warning-icon"><WarningFilled /></el-icon>
        <p>确定要提交答案吗？</p>
        <p class="submit-stats">
          已答题：{{ answeredCount }} / {{ examInfo?.totalQuestions || 0 }}
        </p>
        <p class="submit-warning">提交后将无法修改答案！</p>
      </div>
      <template #footer>
        <el-button @click="submitDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAnswers" :loading="submitting">
          确定提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Clock,
  DocumentChecked,
  Check,
  Close,
  CircleCheck,
  WarningFilled
} from '@element-plus/icons-vue'
import {
  getExamInfo as getExamInfoApi,
  startExam as startExamApi,
  submitAnswers as submitAnswersApi,
  getTestStatus
} from '@/api/writtenTestExam'

export default {
  name: 'WrittenTest',
  components: {
    Clock,
    DocumentChecked,
    Check,
    Close,
    CircleCheck,
    WarningFilled
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    const applicationId = ref(route.params.applicationId)
    const loading = ref(false)
    const submitting = ref(false)
    const examInfo = ref(null)
    const examStatus = ref('not_started') // not_started, in_progress, completed
    const currentQuestionIndex = ref(0)
    const userAnswers = reactive({})
    const startTime = ref(null)
    const remainingTime = ref(0)
    const timer = ref(null)
    const submitDialogVisible = ref(false)

    // 计算属性
    const currentQuestion = computed(() => {
      if (!examInfo.value || !examInfo.value.questions) return null
      return examInfo.value.questions[currentQuestionIndex.value]
    })

    const progressPercentage = computed(() => {
      if (!examInfo.value) return 0
      return Math.round(((currentQuestionIndex.value + 1) / examInfo.value.totalQuestions) * 100)
    })

    const answeredCount = computed(() => {
      return Object.keys(userAnswers).filter(key => userAnswers[key]).length
    })

    // 方法
    const getExamInfo = async () => {
      try {
        loading.value = true
        console.log('获取考试信息，申请ID:', applicationId.value)

        const response = await getExamInfoApi(applicationId.value)
        console.log('考试信息响应:', response)

        if (response.success) {
          examInfo.value = response.data
          examStatus.value = response.data.status || 'not_started'

          // 如果考试已经开始，设置剩余时间
          if (examStatus.value === 'in_progress' && response.data.startTime) {
            const startTime = new Date(response.data.startTime)
            const now = new Date()
            const elapsedMinutes = Math.floor((now - startTime) / (1000 * 60))
            const timeLimit = response.data.timeLimit || 60
            remainingTime.value = Math.max(0, (timeLimit * 60) - (elapsedMinutes * 60))

            if (remainingTime.value > 0) {
              startTimer()
            }
          }

          console.log('考试状态:', examStatus.value)
          console.log('题目数量:', examInfo.value?.questions?.length)
        } else {
          ElMessage.error(response.message || '获取考试信息失败')
        }
      } catch (error) {
        console.error('获取考试信息失败:', error)
        ElMessage.error('获取考试信息失败，请重试')
      } finally {
        loading.value = false
      }
    }

    const startExam = async () => {
      try {
        loading.value = true
        console.log('开始考试，申请ID:', applicationId.value)

        const response = await startExamApi(applicationId.value)
        console.log('开始考试响应:', response)

        if (response.success) {
          examInfo.value = response.data
          examStatus.value = 'in_progress'
          startTime.value = new Date()
          remainingTime.value = (response.data.timeLimit || 60) * 60 // 转换为秒

          startTimer()
          ElMessage.success('考试已开始，请认真答题')
        } else {
          ElMessage.error(response.message || '开始考试失败')
        }
      } catch (error) {
        console.error('开始考试失败:', error)
        ElMessage.error('开始考试失败，请重试')
      } finally {
        loading.value = false
      }
    }

    const nextQuestion = () => {
      if (currentQuestionIndex.value < examInfo.value.totalQuestions - 1) {
        currentQuestionIndex.value++
      }
    }

    const previousQuestion = () => {
      if (currentQuestionIndex.value > 0) {
        currentQuestionIndex.value--
      }
    }

    const goToQuestion = (index) => {
      currentQuestionIndex.value = index
    }

    const showSubmitDialog = () => {
      submitDialogVisible.value = true
    }

    const submitAnswers = async () => {
      try {
        submitting.value = true

        // 构建提交数据
        const answers = []
        Object.keys(userAnswers).forEach(questionId => {
          if (userAnswers[questionId]) {
            answers.push({
              questionId: questionId,
              userAnswer: userAnswers[questionId],
              answerTime: Date.now() // 简单的时间戳
            })
          }
        })

        const submitData = {
          applicationId: parseInt(applicationId.value),
          testResultId: examInfo.value.testResultId,
          answers: answers,
          completionTime: startTime.value ? Math.floor((Date.now() - startTime.value.getTime()) / 1000) : 0
        }

        console.log('提交答案数据:', submitData)

        const response = await submitAnswersApi(submitData)
        console.log('提交答案响应:', response)

        if (response.success) {
          examStatus.value = 'completed'
          submitDialogVisible.value = false

          // 停止计时器
          if (timer.value) {
            clearInterval(timer.value)
            timer.value = null
          }

          ElMessage.success('答案提交成功！')

          // 3秒后自动跳转到结果页面
          setTimeout(() => {
            viewResult()
          }, 3000)
        } else {
          ElMessage.error(response.message || '提交答案失败')
        }
      } catch (error) {
        console.error('提交答案失败:', error)
        ElMessage.error('提交答案失败，请重试')
      } finally {
        submitting.value = false
      }
    }

    const viewResult = () => {
      router.push(`/exam/result/${applicationId.value}`)
    }

    const formatTime = (seconds) => {
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60
      return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
    }

    const startTimer = () => {
      if (timer.value) clearInterval(timer.value)
      
      timer.value = setInterval(() => {
        if (remainingTime.value > 0) {
          remainingTime.value--
        } else {
          // 时间到，自动提交
          ElMessage.warning('考试时间已到，系统将自动提交答案')
          submitAnswers()
        }
      }, 1000)
    }

    onMounted(() => {
      getExamInfo()
    })

    onUnmounted(() => {
      if (timer.value) {
        clearInterval(timer.value)
      }
    })

    return {
      applicationId,
      loading,
      submitting,
      examInfo,
      examStatus,
      currentQuestionIndex,
      userAnswers,
      remainingTime,
      submitDialogVisible,
      currentQuestion,
      progressPercentage,
      answeredCount,
      getExamInfo,
      startExam,
      nextQuestion,
      previousQuestion,
      goToQuestion,
      showSubmitDialog,
      submitAnswers,
      viewResult,
      formatTime
    }
  }
}
</script>
