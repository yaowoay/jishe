<template>
  <div class="test-exam">
    <!-- 测试头部 -->
    <div class="exam-header">
      <div class="exam-info">
        <h1>{{ categoryName }} - 专业测试</h1>
        <div class="exam-meta">
          <span>题目：{{ currentQuestionIndex + 1 }} / {{ questions.length }}</span>
          <span class="timer-display" :class="{ 'time-warning': elapsedTime > maxTime - 600 }">
            已用时间：{{ formatTime(elapsedTime) }}
          </span>
        </div>
      </div>
      <div class="exam-progress">
        <el-progress
            :percentage="Math.round(((currentQuestionIndex + 1) / questions.length) * 100)"
            :stroke-width="8"
            color="#409EFF"
        />
      </div>
    </div>

    <!-- 测试内容 -->
    <div class="exam-content" v-if="!isFinished">
      <div class="question-card">
        <div class="question-header">
          <span class="question-number">第 {{ currentQuestionIndex + 1 }} 题</span>
          <el-tag :type="getDifficultyType(currentQuestion.difficulty)">
            {{ currentQuestion.difficulty }}
          </el-tag>
          <el-tag type="info">{{ currentQuestion.category }}</el-tag>
        </div>

        <div class="question-content">
          <h3>{{ currentQuestion.question }}</h3>
          <p class="english-question">{{ currentQuestion.englishQuestion }}</p>
        </div>

        <div class="question-options">
          <!-- 单选题 -->
          <el-radio-group
              v-if="currentQuestion.type === 'single'"
              v-model="currentAnswer"
              @change="saveAnswer"
          >
            <el-radio
                v-for="(option, index) in currentQuestion.options"
                :key="index"
                :label="index"
                class="option-item"
            >
              <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
              <span class="option-text">{{ option }}</span>
            </el-radio>
          </el-radio-group>

          <!-- 多选题 -->
          <el-checkbox-group
              v-else-if="currentQuestion.type === 'multiple'"
              v-model="currentAnswer"
              @change="saveAnswer"
          >
            <el-checkbox
                v-for="(option, index) in currentQuestion.options"
                :key="index"
                :label="index"
                class="option-item"
            >
              <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
              <span class="option-text">{{ option }}</span>
            </el-checkbox>
          </el-checkbox-group>

          <!-- 简答题 -->
          <el-input
              v-else-if="currentQuestion.type === 'text'"
              v-model="currentAnswer"
              type="textarea"
              :rows="6"
              placeholder="请输入您的答案..."
              @input="saveAnswer"
          />
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="exam-actions">
        <el-button
            @click="previousQuestion"
            :disabled="currentQuestionIndex === 0"
        >
          上一题
        </el-button>

        <el-button
            v-if="currentQuestionIndex < questions.length - 1"
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

      <!-- 题目导航 -->
      <div class="question-nav">
        <h4>答题进度</h4>
        <div class="nav-grid">
          <div
              v-for="(question, index) in questions"
              :key="index"
              class="nav-item"
              :class="{
              'current': index === currentQuestionIndex,
              'answered': answers[question.id] !== undefined && answers[question.id] !== null && answers[question.id] !== ''
            }"
              @click="goToQuestion(index)"
          >
            {{ index + 1 }}
          </div>
        </div>
      </div>
    </div>

    <!-- 提交确认对话框 -->
    <el-dialog
        v-model="submitDialogVisible"
        title="提交确认"
        width="500px"
        :close-on-click-modal="false"
    >
      <div class="submit-info">
        <p>您确定要提交答案吗？</p>
        <div class="submit-stats">
          <div class="stat-item">
            <span class="label">总题数：</span>
            <span class="value">{{ questions.length }}</span>
          </div>
          <div class="stat-item">
            <span class="label">已答题：</span>
            <span class="value">{{ answeredCount }}</span>
          </div>
          <div class="stat-item">
            <span class="label">未答题：</span>
            <span class="value">{{ questions.length - answeredCount }}</span>
          </div>
        </div>
        <p class="warning-text">提交后将无法修改答案！</p>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="submitDialogVisible = false">继续答题</el-button>
          <el-button type="primary" @click="submitExam">确认提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getQuestionsByCategory, calculateScore, generateAnalysisReport } from '@/data/questionBank'
import { saveTestResult } from '@/api/testResults'

export default {
  name: 'ProfessionalTestExam',
  data() {
    return {
      questions: [],
      answers: {},
      currentQuestionIndex: 0,
      currentAnswer: null,
      elapsedTime: 0, // 已用时间，单位秒
      maxTime: 3600, // 最大时间60分钟，单位秒
      timer: null,
      submitDialogVisible: false,
      isFinished: false,
      categoryName: '',
      category: ''
    }
  },
  computed: {
    currentQuestion() {
      return this.questions[this.currentQuestionIndex] || {}
    },
    answeredCount() {
      return Object.keys(this.answers).filter(key => {
        const answer = this.answers[key]
        return answer !== undefined && answer !== null && answer !== ''
      }).length
    }
  },
  created() {
    this.initExam()
  },
  beforeUnmount() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    initExam() {
      // 从路由参数获取测试类别
      this.category = this.$route.params.category || 'backend'
      this.categoryName = this.$route.params.categoryName || '后端开发'

      // 加载题目
      this.questions = getQuestionsByCategory(this.category, 10)

      // 初始化答案对象
      this.answers = {}

      // 设置当前答案
      this.loadCurrentAnswer()

      // 开始计时
      this.startTimer()
    },

    startTimer() {
      this.timer = setInterval(() => {
        this.elapsedTime++
        // 检查时间警告
        this.checkTimeWarning()
        // 检查是否超过最大时间（可选的时间限制）
        if (this.elapsedTime >= this.maxTime) {
          this.timeUp()
        }
      }, 1000)
    },

    timeUp() {
      this.$message.warning('考试时间已达到最大限制，系统将自动提交答案')
      this.submitExam()
    },

    formatTime(seconds) {
      const hours = Math.floor(seconds / 3600)
      const minutes = Math.floor((seconds % 3600) / 60)
      const secs = seconds % 60
      return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    },

    // 检查是否接近时间限制（可选功能）
    checkTimeWarning() {
      const warningTime = this.maxTime - 300 // 剩余5分钟时警告
      if (this.elapsedTime === warningTime) {
        this.$message.warning('提醒：您已用时较长，建议尽快完成剩余题目')
      }
    },

    loadCurrentAnswer() {
      const questionId = this.currentQuestion.id
      this.currentAnswer = this.answers[questionId] || (this.currentQuestion.type === 'multiple' ? [] : null)
    },

    saveAnswer() {
      const questionId = this.currentQuestion.id
      this.answers[questionId] = this.currentAnswer
    },

    nextQuestion() {
      if (this.currentQuestionIndex < this.questions.length - 1) {
        this.currentQuestionIndex++
        this.loadCurrentAnswer()
      }
    },

    previousQuestion() {
      if (this.currentQuestionIndex > 0) {
        this.currentQuestionIndex--
        this.loadCurrentAnswer()
      }
    },

    goToQuestion(index) {
      this.currentQuestionIndex = index
      this.loadCurrentAnswer()
    },

    showSubmitDialog() {
      this.submitDialogVisible = true
    },

    async submitExam() {
      this.submitDialogVisible = false

      if (this.timer) {
        clearInterval(this.timer)
      }

      // 计算分数和生成报告
      const answerArray = this.questions.map(q => this.answers[q.id])
      const score = calculateScore(answerArray, this.questions)
      const report = generateAnalysisReport(answerArray, this.questions, score)

      // 计算正确答案数
      const correctAnswers = answerArray.filter((answer, index) => {
        const question = this.questions[index]
        if (question.type === 'multiple') {
          if (Array.isArray(answer) && Array.isArray(question.correctAnswer)) {
            const sorted1 = [...answer].sort()
            const sorted2 = [...question.correctAnswer].sort()
            return JSON.stringify(sorted1) === JSON.stringify(sorted2)
          }
          return false
        } else {
          return answer === question.correctAnswer
        }
      }).length

      // 准备保存到数据库的数据
      const testData = {
        userId: this.getCurrentUserId(), // 从store获取用户ID
        category: this.category,
        categoryName: this.categoryName,
        score: score,
        totalQuestions: this.questions.length,
        correctAnswers: correctAnswers,
        duration: this.elapsedTime, // 实际用时（秒）
        completedAt: new Date().toISOString().slice(0, 19).replace('T', ' '), // 格式化为 yyyy-MM-dd HH:mm:ss
        questions: this.questions.map(q => ({
          id: q.id,
          question: q.question,
          englishQuestion: q.englishQuestion,
          type: q.type,
          options: q.options,
          correctAnswer: q.correctAnswer,
          chineseAnswer: q.chineseAnswer,
          englishAnswer: q.englishAnswer,
          difficulty: q.difficulty,
          category: q.category
        })),
        userAnswers: this.questions.map(q => ({
          questionId: q.id,
          userAnswer: this.answers[q.id],
          isCorrect: this.isAnswerCorrect(q, this.answers[q.id])
        })),
        analysisReport: report
      }

      try {
        // 保存测试结果到数据库
        const saveResult = await this.saveTestResultToDatabase(testData)

        // 跳转到结果页面，传递测试结果ID
        if (saveResult && saveResult.data && saveResult.data.testResultId) {
          this.$router.push({
            name: 'TestResult',
            params: {
              testResultId: saveResult.data.testResultId
            }
          })
        } else {
          // 如果没有获取到ID，使用原来的方式（兼容性处理）
          this.$router.push({
            name: 'TestResult',
            params: {
              score,
              report: JSON.stringify(report),
              category: this.category,
              categoryName: this.categoryName,
              questions: JSON.stringify(this.questions),
              answers: JSON.stringify(this.answers)
            }
          })
        }
      } catch (error) {
        console.error('保存测试结果失败:', error)
        this.$message.error('保存测试结果失败，但您仍可以查看结果')

        // 即使保存失败也跳转到结果页面
        this.$router.push({
          name: 'TestResult',
          params: {
            score,
            report: JSON.stringify(report),
            category: this.category,
            categoryName: this.categoryName,
            questions: JSON.stringify(this.questions),
            answers: JSON.stringify(this.answers)
          }
        })
      }
    },

    // 判断答案是否正确
    isAnswerCorrect(question, userAnswer) {
      if (question.type === 'multiple') {
        if (Array.isArray(userAnswer) && Array.isArray(question.correctAnswer)) {
          const sorted1 = [...userAnswer].sort()
          const sorted2 = [...question.correctAnswer].sort()
          return JSON.stringify(sorted1) === JSON.stringify(sorted2)
        }
        return false
      } else {
        return userAnswer === question.correctAnswer
      }
    },

    // 保存测试结果到数据库
    async saveTestResultToDatabase(testData) {
      try {
        // 调用Spring Boot后端API
        const response = await fetch('/api/test-results', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
            // 如果需要认证，添加Authorization头
            // 'Authorization': `Bearer ${this.$store.state.token}`
          },
          body: JSON.stringify(testData)
        })

        const result = await response.json()

        if (result.success) {
          console.log('测试结果保存成功:', result)
          this.$message.success('测试结果已保存')
          return result
        } else {
          throw new Error(result.message || '保存失败')
        }

      } catch (error) {
        console.error('保存测试结果到数据库失败:', error)

        // 根据错误类型给出不同的提示
        if (error.name === 'TypeError' && error.message.includes('fetch')) {
          this.$message.error('网络连接失败，请检查网络设置')
        } else if (error.message) {
          this.$message.error(`保存失败：${error.message}`)
        } else {
          this.$message.error('保存测试结果失败')
        }

        throw error
      }
    },

    getDifficultyType(difficulty) {
      switch (difficulty) {
      case '简单': return 'success'
      case '中等': return 'warning'
      case '困难': return 'danger'
      default: return 'info'
      }
    },

    getCurrentUserId() {
      // 尝试从多个可能的位置获取用户ID
      const userId = this.$store.state.user?.userId ||
          this.$store.state.user?.id ||
          this.$store.state.userId ||
          localStorage.getItem('userId')

      if (!userId) {
        this.$message.error('无法获取用户信息，请重新登录')
        this.$router.push('/login')
        throw new Error('用户未登录')
      }

      return parseInt(userId)
    }
  }
}
</script>

<style scoped>
.test-exam {
  min-height: 100vh;
  background: #f8fafc;
  padding: 20px;
}

.exam-header {
  background: white;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.exam-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.exam-info h1 {
  margin: 0;
  color: #2c3e50;
  font-size: 24px;
}

.exam-meta {
  display: flex;
  gap: 24px;
  color: #64748b;
  font-size: 14px;
}

.exam-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 24px;
}

.question-card {
  background: white;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.question-header {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  align-items: center;
}

.question-number {
  font-weight: 600;
  color: #409EFF;
  font-size: 16px;
}

.question-content h3 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 18px;
  line-height: 1.6;
}

.english-question {
  color: #64748b;
  font-style: italic;
  margin-bottom: 24px;
}

.question-options {
  margin-bottom: 32px;
}

.option-item {
  display: block;
  margin-bottom: 16px;
  padding: 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.option-item:hover {
  border-color: #409EFF;
  background: #f0f9ff;
}

.option-label {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  background: #409EFF;
  color: white;
  border-radius: 50%;
  font-weight: 600;
  font-size: 14px;
  margin-right: 12px;
  vertical-align: top;
}

.option-text {
  display: inline-block;
  vertical-align: top;
  line-height: 24px;
  flex: 1;
}

/* 选中状态的样式 */
.el-radio.is-checked .option-label,
.el-checkbox.is-checked .option-label {
  background: #67C23A;
}

.el-radio.is-checked .option-item,
.el-checkbox.is-checked .option-item {
  border-color: #67C23A;
  background: #f0f9ff;
}

.exam-actions {
  display: flex;
  justify-content: space-between;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
}

.question-nav {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  height: fit-content;
}

.question-nav h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
}

.nav-item {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.nav-item:hover {
  border-color: #409EFF;
}

.nav-item.current {
  background: #409EFF;
  color: white;
  border-color: #409EFF;
}

.nav-item.answered {
  background: #67C23A;
  color: white;
  border-color: #67C23A;
}

.nav-item.answered.current {
  background: #409EFF;
  border-color: #409EFF;
}

.submit-info {
  text-align: center;
}

.submit-stats {
  margin: 20px 0;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.stat-item:last-child {
  margin-bottom: 0;
}

.label {
  color: #64748b;
}

.value {
  font-weight: 600;
  color: #2c3e50;
}

.warning-text {
  color: #f56565;
  font-size: 14px;
  margin-top: 16px;
}

.dialog-footer {
  text-align: right;
}

/* 计时器样式 */
.timer-display {
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 4px;
  background: #e8f4fd;
  color: #409eff;
  transition: all 0.3s ease;
}

.timer-display.time-warning {
  background: #fef0f0;
  color: #f56c6c;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.7; }
  100% { opacity: 1; }
}
</style>
