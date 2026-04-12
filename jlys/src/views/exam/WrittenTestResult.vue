<template>
  <div class="test-result-container">
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
      <p>正在加载考试结果...</p>
    </div>

    <div v-else-if="testResult" class="result-content">
      <!-- 结果头部 -->
      <div class="result-header">
        <div class="score-display">
          <div class="score-circle" :class="getScoreClass(testResult.score)">
            {{ testResult.score }}
          </div>
          <div class="score-info">
            <h2>考试完成</h2>
            <p class="score-text">{{ getScoreText(testResult.score) }}</p>
          </div>
        </div>
        <div class="result-stats">
          <div class="stat-item">
            <span class="stat-label">正确题数</span>
            <span class="stat-value correct">{{ testResult.correctAnswers }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">错误题数</span>
            <span class="stat-value wrong">{{ testResult.wrongAnswers }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">总题数</span>
            <span class="stat-value total">{{ testResult.totalQuestions }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">用时</span>
            <span class="stat-value time">{{ formatTime(testResult.completionTime) }}</span>
          </div>
        </div>
      </div>

      <!-- 详细结果 -->
      <div class="detailed-results">
        <h3>详细解析</h3>
        <div class="questions-list">
          <div 
            v-for="(question, index) in testResult.questionResults" 
            :key="question.questionId"
            class="question-item"
            :class="{ 'correct': question.isCorrect, 'wrong': !question.isCorrect }"
          >
            <div class="question-header">
              <span class="question-number">第 {{ index + 1 }} 题</span>
              <span class="question-type">{{ question.type === 'choice' ? '选择题' : '判断题' }}</span>
              <el-tag :type="question.isCorrect ? 'success' : 'danger'" size="small">
                {{ question.isCorrect ? '正确' : '错误' }}
              </el-tag>
            </div>

            <!-- 选择题 -->
            <div v-if="question.type === 'choice'" class="choice-question">
              <div class="question-content">{{ question.questionContent }}</div>
              <div class="options-review">
                <div 
                  v-for="option in ['A', 'B', 'C', 'D']" 
                  :key="option"
                  class="option-item"
                  :class="{
                    'correct-answer': option === question.correctAnswer,
                    'user-answer': option === question.userAnswer,
                    'wrong-answer': option === question.userAnswer && option !== question.correctAnswer
                  }"
                >
                  <span class="option-label">{{ option }}.</span>
                  <span class="option-text">{{ question[`option${option}`] }}</span>
                  <span v-if="option === question.correctAnswer" class="answer-mark correct">
                    <el-icon><Check /></el-icon>
                  </span>
                  <span v-else-if="option === question.userAnswer" class="answer-mark wrong">
                    <el-icon><Close /></el-icon>
                  </span>
                </div>
              </div>
            </div>

            <!-- 判断题 -->
            <div v-else-if="question.type === 'true_false'" class="true-false-question">
              <div class="question-content">{{ question.statement }}</div>
              <div class="tf-review">
                <div class="tf-answers">
                  <span class="answer-item" :class="{ 'correct': question.correctAnswer === 'true' }">
                    正确
                    <el-icon v-if="question.correctAnswer === 'true'"><Check /></el-icon>
                  </span>
                  <span class="answer-item" :class="{ 'correct': question.correctAnswer === 'false' }">
                    错误
                    <el-icon v-if="question.correctAnswer === 'false'"><Check /></el-icon>
                  </span>
                </div>
                <div class="user-choice">
                  您的答案：
                  <span :class="{ 'correct': question.isCorrect, 'wrong': !question.isCorrect }">
                    {{ question.userAnswer === 'true' ? '正确' : '错误' }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 解析 -->
            <div class="explanation">
              <h4>解析：</h4>
              <p>{{ question.explanation }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="result-actions">
        <el-button @click="goBack">返回</el-button>
        <el-button type="primary" @click="printResult">打印结果</el-button>
      </div>
    </div>

    <div v-else class="error-container">
      <el-result
        icon="warning"
        title="未找到考试结果"
        sub-title="请确认您已完成笔试"
      >
        <template #extra>
          <el-button type="primary" @click="goBack">返回</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Loading,
  Check,
  Close
} from '@element-plus/icons-vue'
import { getTestResult } from '@/api/writtenTestExam'

export default {
  name: 'WrittenTestResult',
  components: {
    Loading,
    Check,
    Close
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    const applicationId = ref(route.params.applicationId)
    const loading = ref(true)
    const testResult = ref(null)

    // 方法
    const getTestResultData = async () => {
      try {
        loading.value = true
        console.log('获取考试结果，申请ID:', applicationId.value)

        const response = await getTestResult(applicationId.value)
        console.log('考试结果响应:', response)

        if (response.success) {
          testResult.value = response.data
          console.log('考试结果数据:', testResult.value)
        } else {
          ElMessage.error(response.message || '获取考试结果失败')
        }

      } catch (error) {
        console.error('获取考试结果失败:', error)
        ElMessage.error('获取考试结果失败，请重试')
      } finally {
        loading.value = false
      }
    }

    const getScoreClass = (score) => {
      if (score >= 90) return 'excellent'
      if (score >= 80) return 'good'
      if (score >= 70) return 'average'
      if (score >= 60) return 'basic'
      return 'poor'
    }

    const getScoreText = (score) => {
      if (score >= 90) return '优秀'
      if (score >= 80) return '良好'
      if (score >= 70) return '一般'
      if (score >= 60) return '及格'
      return '不及格'
    }

    const formatTime = (seconds) => {
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60
      return `${minutes}分${remainingSeconds}秒`
    }

    const goBack = () => {
      router.go(-1)
    }

    const printResult = () => {
      window.print()
    }

    onMounted(() => {
      getTestResultData()
    })

    return {
      applicationId,
      loading,
      testResult,
      getTestResultData,
      getScoreClass,
      getScoreText,
      formatTime,
      goBack,
      printResult
    }
  }
}
</script>
