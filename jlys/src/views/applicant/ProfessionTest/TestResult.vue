<template>
  <div class="test-result">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-loading-directive v-loading="loading" text="加载测试结果中..."></el-loading-directive>
    </div>

    <!-- 结果内容 -->
    <div v-else>
      <!-- 结果头部 -->
      <div class="result-header">
      <div class="score-section">
        <div class="score-circle">
          <div class="score-number">{{ testResult.score || 0 }}</div>
          <div class="score-label">分</div>
        </div>
        <div class="score-info">
          <h1>{{ categoryName }} 测试完成</h1>
          <p class="score-desc">{{ getScoreDescription(testResult.score || 0) }}</p>
          <div class="test-meta">
            <span>测试时间：{{ formatDate(testResult.completedAt) }}</span>
            <span>用时：{{ formatDuration(testResult.duration) }}</span>
          </div>
        </div>
      </div>
      
      <div class="result-actions">
        <el-button @click="viewDetails">查看详情</el-button>
        <el-button type="primary" @click="retakeTest">重新测试</el-button>
        <el-button @click="backToList">返回列表</el-button>
      </div>
    </div>

    <!-- 能力分析 -->
    <div class="analysis-section">
      <div class="analysis-grid">
        <!-- 分类能力分析 -->
        <div class="analysis-card">
          <h3>知识点掌握情况</h3>
          <div class="category-analysis">
            <div
              v-for="item in (report.categoryAnalysis || [])"
              :key="item.category"
              class="category-item"
            >
              <div class="category-header">
                <span class="category-name">{{ item.category }}</span>
                <span class="category-score">{{ item.accuracy }}%</span>
              </div>
              <el-progress 
                :percentage="item.accuracy"
                :color="getProgressColor(item.accuracy)"
                :stroke-width="8"
              />
              <div class="category-detail">
                {{ item.correct }} / {{ item.total }} 题正确
              </div>
            </div>
          </div>
        </div>

        <!-- 难度分析 -->
        <div class="analysis-card">
          <h3>难度掌握分析</h3>
          <div class="difficulty-analysis">
            <div
              v-for="item in (report.difficultyAnalysis || [])"
              :key="item.difficulty"
              class="difficulty-item"
            >
              <div class="difficulty-header">
                <span class="difficulty-name">{{ item.difficulty }}题</span>
                <span class="difficulty-score">{{ item.accuracy }}%</span>
              </div>
              <el-progress 
                :percentage="item.accuracy"
                :color="getProgressColor(item.accuracy)"
                :stroke-width="8"
              />
              <div class="difficulty-detail">
                {{ item.correct }} / {{ item.total }} 题正确
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 学习建议 -->
      <div class="suggestions-card">
        <h3>学习建议</h3>
        <div class="suggestions-list">
          <div
            v-for="(suggestion, index) in (report.suggestions || [])"
            :key="index"
            class="suggestion-item"
          >
            <el-icon class="suggestion-icon"><InfoFilled /></el-icon>
            <span>{{ suggestion }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 答题详情 -->
    <div v-if="showDetails" class="details-section">
      <h3>答题详情</h3>
      <div class="question-details">
        <div
          v-for="(questionDetail, index) in questionDetails"
          :key="questionDetail.question.id"
          class="question-detail-card"
        >
          <div class="question-detail-header">
            <span class="question-num">第 {{ index + 1 }} 题</span>
            <el-tag :type="getDifficultyType(questionDetail.question.difficulty)">
              {{ questionDetail.question.difficulty }}
            </el-tag>
            <el-tag type="info">{{ questionDetail.question.category }}</el-tag>
            <el-tag :type="questionDetail.userAnswer.isCorrect ? 'success' : 'danger'">
              {{ questionDetail.userAnswer.isCorrect ? '正确' : '错误' }}
            </el-tag>
          </div>

          <div class="question-detail-content">
            <h4>{{ questionDetail.question.question }}</h4>
            <p class="english-question" v-if="questionDetail.question.englishQuestion">
              {{ questionDetail.question.englishQuestion }}
            </p>

            <!-- 选择题选项 -->
            <div class="question-options" v-if="questionDetail.question.questionType !== 'text' && questionDetail.parsedOptions">
              <div
                v-for="(option, optionIndex) in questionDetail.parsedOptions"
                :key="optionIndex"
                class="option-item"
                :class="{
                  'user-selected': isOptionSelected(questionDetail.userAnswer.parsedAnswer, optionIndex),
                  'correct-option': isCorrectOption(questionDetail.question.parsedCorrectAnswer, optionIndex),
                  'wrong-selected': isOptionSelected(questionDetail.userAnswer.parsedAnswer, optionIndex) && !isCorrectOption(questionDetail.question.parsedCorrectAnswer, optionIndex)
                }"
              >
                <span class="option-label">{{ String.fromCharCode(65 + optionIndex) }}.</span>
                <span class="option-text">{{ option }}</span>
                <span v-if="isOptionSelected(questionDetail.userAnswer.parsedAnswer, optionIndex)" class="selection-mark">
                  <i class="el-icon-check" v-if="isCorrectOption(questionDetail.question.parsedCorrectAnswer, optionIndex)"></i>
                  <i class="el-icon-close" v-else></i>
                </span>
                <span v-else-if="isCorrectOption(questionDetail.question.parsedCorrectAnswer, optionIndex)" class="correct-mark">
                  <i class="el-icon-check"></i>
                </span>
              </div>
            </div>

            <div class="answer-section">
              <div class="user-answer">
                <strong>您的答案：</strong>
                <span :class="{ 'wrong-answer': !questionDetail.userAnswer.isCorrect }">
                  {{ formatUserAnswer(questionDetail) }}
                </span>
              </div>

              <div class="correct-answer">
                <strong>正确答案：</strong>
                <span class="correct-text">
                  {{ formatCorrectAnswer(questionDetail) }}
                </span>
              </div>

              <div class="answer-explanation" v-if="questionDetail.question.chineseAnswer">
                <strong>答案解析：</strong>
                <p>{{ questionDetail.question.chineseAnswer }}</p>
                <p v-if="questionDetail.question.englishAnswer" class="english-explanation">
                  {{ questionDetail.question.englishAnswer }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    </div> <!-- 关闭 v-else -->
  </div>
</template>

<script>
import { InfoFilled } from '@element-plus/icons-vue'

export default {
  name: 'TestResult',
  components: {
    InfoFilled
  },
  data() {
    return {
      testResult: {},
      report: {},
      questions: [],
      userAnswers: [],
      questionDetails: [], // 合并后的题目和答案详情
      answers: {},
      categoryName: '',
      category: '',
      showDetails: false,
      loading: true
    }
  },
  created() {
    this.initResult()
  },
  methods: {
    async initResult() {
      try {
        // 从路由参数获取测试结果ID
        const testResultId = this.$route.params.testResultId || this.$route.query.testResultId
        const userId = this.getCurrentUserId()

        if (!testResultId) {
          this.$message.error('缺少测试结果ID')
          this.$router.push({ name: 'ProfessionalTest' })
          return
        }

        // 从后端API获取测试结果详情
        await this.fetchTestResultDetail(testResultId, userId)

      } catch (error) {
        console.error('初始化测试结果失败:', error)
        this.$message.error('获取测试结果失败')
        this.$router.push({ name: 'ProfessionalTest' })
      } finally {
        this.loading = false
      }
    },

    async fetchTestResultDetail(testResultId, userId) {
      try {
        const response = await fetch(`/api/test-results/${testResultId}?userId=${userId}`)
        const result = await response.json()

        if (result.success) {
          const data = result.data
          this.testResult = data.testResult
          this.questions = data.questions || []
          this.userAnswers = data.userAnswers || []

          // 解析分析报告
          if (this.testResult.analysisReport) {
            this.report = typeof this.testResult.analysisReport === 'string'
              ? JSON.parse(this.testResult.analysisReport)
              : this.testResult.analysisReport
          }

          // 设置基本信息
          this.categoryName = this.testResult.categoryName
          this.category = this.testResult.category

          // 构建答案映射（用于兼容现有的显示逻辑）
          this.buildAnswersMap()

          // 保存到历史记录
          this.saveToHistory()

        } else {
          throw new Error(result.message || '获取测试结果失败')
        }
      } catch (error) {
        console.error('获取测试结果详情失败:', error)
        throw error
      }
    },

    buildAnswersMap() {
      // 构建答案映射，用于兼容现有的显示逻辑
      this.answers = {}
      this.userAnswers.forEach(userAnswer => {
        try {
          const answer = typeof userAnswer.userAnswer === 'string'
            ? JSON.parse(userAnswer.userAnswer)
            : userAnswer.userAnswer
          this.answers[userAnswer.questionId] = answer
        } catch (e) {
          console.warn('解析用户答案失败:', userAnswer)
        }
      })

      // 构建题目详情数据
      this.buildQuestionDetails()
    },

    buildQuestionDetails() {
      // 合并题目和用户答案数据
      this.questionDetails = []

      this.questions.forEach(question => {
        // 找到对应的用户答案
        const userAnswer = this.userAnswers.find(ua => ua.questionId === question.questionId)

        if (userAnswer) {
          // 解析JSON字段
          const parsedOptions = this.parseJsonField(question.options)
          const parsedCorrectAnswer = this.parseJsonField(question.correctAnswer)
          const parsedUserAnswer = this.parseJsonField(userAnswer.userAnswer)

          const questionDetail = {
            question: {
              ...question,
              parsedOptions,
              parsedCorrectAnswer
            },
            userAnswer: {
              ...userAnswer,
              parsedAnswer: parsedUserAnswer
            }
          }

          this.questionDetails.push(questionDetail)
        }
      })

      // 按题目ID排序
      this.questionDetails.sort((a, b) => a.question.questionId - b.question.questionId)
    },

    parseJsonField(jsonString) {
      if (!jsonString) return null

      try {
        return typeof jsonString === 'string' ? JSON.parse(jsonString) : jsonString
      } catch (e) {
        console.warn('解析JSON字段失败:', jsonString)
        return jsonString
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
    },
    
    saveToHistory() {
      // 保存测试记录到本地存储
      const testRecord = {
        id: Date.now(),
        testName: `${this.categoryName}专业测试`,
        category: this.categoryName,
        score: this.report.score,
        duration: this.formatDuration(),
        completedAt: this.formatDate(new Date()),
        report: this.report,
        questions: this.questions,
        answers: this.answers
      }
      
      // 获取现有记录
      const existingRecords = JSON.parse(localStorage.getItem('testRecords') || '[]')
      existingRecords.unshift(testRecord)
      
      // 只保留最近20条记录
      if (existingRecords.length > 20) {
        existingRecords.splice(20)
      }
      
      localStorage.setItem('testRecords', JSON.stringify(existingRecords))
    },
    
    getScoreDescription(score) {
      if (score >= 90) return '优秀！您的专业水平非常出色'
      if (score >= 80) return '良好！您具备扎实的专业基础'
      if (score >= 70) return '中等！还有提升空间'
      if (score >= 60) return '及格！建议加强学习'
      return '需要努力！建议系统性学习'
    },
    
    getProgressColor(percentage) {
      if (percentage >= 80) return '#67C23A'
      if (percentage >= 60) return '#E6A23C'
      return '#F56C6C'
    },
    
    getDifficultyType(difficulty) {
      switch (difficulty) {
      case '简单': return 'success'
      case '中等': return 'warning'
      case '困难': return 'danger'
      default: return 'info'
      }
    },

    // 新增方法：判断选项是否被用户选择
    isOptionSelected(userAnswer, optionIndex) {
      if (Array.isArray(userAnswer)) {
        // 多选题
        return userAnswer.includes(optionIndex)
      } else {
        // 单选题
        return userAnswer === optionIndex
      }
    },

    // 新增方法：判断选项是否为正确答案
    isCorrectOption(correctAnswer, optionIndex) {
      if (Array.isArray(correctAnswer)) {
        // 多选题
        return correctAnswer.includes(optionIndex)
      } else {
        // 单选题
        return correctAnswer === optionIndex
      }
    },

    // 新增方法：格式化用户答案显示
    formatUserAnswer(questionDetail) {
      const userAnswer = questionDetail.userAnswer.parsedAnswer
      const options = questionDetail.question.parsedOptions
      const questionType = questionDetail.question.questionType

      if (!options || questionType === 'text') {
        return userAnswer || '未作答'
      }

      if (Array.isArray(userAnswer)) {
        // 多选题
        if (userAnswer.length === 0) return '未作答'
        return userAnswer.map(index => `${String.fromCharCode(65 + index)}. ${options[index]}`).join(', ')
      } else if (userAnswer !== null && userAnswer !== undefined) {
        // 单选题
        return `${String.fromCharCode(65 + userAnswer)}. ${options[userAnswer]}`
      } else {
        return '未作答'
      }
    },

    // 新增方法：格式化正确答案显示
    formatCorrectAnswer(questionDetail) {
      const correctAnswer = questionDetail.question.parsedCorrectAnswer
      const options = questionDetail.question.parsedOptions
      const questionType = questionDetail.question.questionType

      if (!options || questionType === 'text') {
        return correctAnswer || '无标准答案'
      }

      if (Array.isArray(correctAnswer)) {
        // 多选题
        return correctAnswer.map(index => `${String.fromCharCode(65 + index)}. ${options[index]}`).join(', ')
      } else {
        // 单选题
        return `${String.fromCharCode(65 + correctAnswer)}. ${options[correctAnswer]}`
      }
    },
    
    isCorrect(question, index) {
      const userAnswer = this.answers[question.id]
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
    
    formatAnswer(question, answer) {
      if (question.type === 'multiple') {
        if (Array.isArray(answer)) {
          return answer.map(index => question.options[index]).join('、')
        }
        return '未作答'
      } else if (question.type === 'single') {
        return question.options[answer] || '未作答'
      } else {
        return answer || '未作答'
      }
    },
    
    formatDate(dateString) {
      if (!dateString) return '未知时间'

      try {
        const date = new Date(dateString)
        return date.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        })
      } catch (e) {
        return dateString
      }
    },

    formatDuration(seconds) {
      if (!seconds) return '未知'

      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60

      if (minutes > 0) {
        return `${minutes}分${remainingSeconds}秒`
      } else {
        return `${remainingSeconds}秒`
      }
    },
    
    viewDetails() {
      this.showDetails = !this.showDetails
    },
    
    retakeTest() {
      this.$router.push({
        name: 'ProfessionalTestExam',
        params: {
          category: this.category,
          categoryName: this.categoryName
        }
      })
    },
    
    backToList() {
      this.$router.push({ name: 'ProfessionalTest' })
    }
  }
}
</script>

<style scoped>
.test-result {
  min-height: 100vh;
  background: #f8fafc;
  padding: 24px;
}

.result-header {
  background: white;
  padding: 32px;
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.score-section {
  display: flex;
  align-items: center;
  gap: 32px;
}

.score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
}

.score-number {
  font-size: 36px;
  font-weight: 700;
  line-height: 1;
}

.score-label {
  font-size: 14px;
  opacity: 0.9;
}

.score-info h1 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 28px;
}

.score-desc {
  color: #64748b;
  font-size: 16px;
  margin-bottom: 12px;
}

.test-meta {
  display: flex;
  gap: 24px;
  color: #94a3b8;
  font-size: 14px;
}

.result-actions {
  display: flex;
  gap: 12px;
}

.analysis-section {
  margin-bottom: 24px;
}

.analysis-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.analysis-card, .suggestions-card {
  background: white;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.analysis-card h3, .suggestions-card h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
}

.category-item, .difficulty-item {
  margin-bottom: 20px;
}

.category-item:last-child, .difficulty-item:last-child {
  margin-bottom: 0;
}

.category-header, .difficulty-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.category-name, .difficulty-name {
  font-weight: 600;
  color: #2c3e50;
}

.category-score, .difficulty-score {
  font-weight: 600;
  color: #409EFF;
}

.category-detail, .difficulty-detail {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 4px;
}

.suggestions-list {
  space-y: 12px;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  margin-bottom: 12px;
}

.suggestion-icon {
  color: #409EFF;
  margin-top: 2px;
  flex-shrink: 0;
}

.details-section {
  background: white;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.details-section h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
}

.question-detail-card {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
}

.question-detail-card:last-child {
  margin-bottom: 0;
}

.question-detail-header {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  align-items: center;
}

.question-num {
  font-weight: 600;
  color: #409EFF;
}

.question-detail-content h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 16px;
}

.english-question {
  color: #64748b;
  font-style: italic;
  margin-bottom: 16px;
  font-size: 14px;
}

.answer-section > div {
  margin-bottom: 12px;
}

.answer-section > div:last-child {
  margin-bottom: 0;
}

.wrong-answer {
  color: #f56565;
}

.correct-text {
  color: #48bb78;
}

.answer-explanation {
  background: #f8fafc;
  padding: 12px;
  border-radius: 8px;
  border-left: 4px solid #409EFF;
}

.answer-explanation p {
  margin: 4px 0 0 0;
  color: #64748b;
  line-height: 1.6;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  font-size: 16px;
  color: #666;
}

/* 题目选项样式 */
.question-options {
  margin: 16px 0;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  margin: 4px 0;
  border-radius: 6px;
  border: 1px solid #e1e5e9;
  background: #fff;
  transition: all 0.3s ease;
  position: relative;
}

.option-item.user-selected {
  border-color: #409eff;
  background: #ecf5ff;
}

.option-item.correct-option {
  border-color: #67c23a;
  background: #f0f9ff;
}

.option-item.wrong-selected {
  border-color: #f56c6c;
  background: #fef0f0;
}

.option-label {
  font-weight: 600;
  margin-right: 8px;
  min-width: 24px;
  color: #606266;
}

.option-text {
  flex: 1;
  line-height: 1.5;
}

.selection-mark,
.correct-mark {
  margin-left: 8px;
  font-size: 16px;
}

.selection-mark .el-icon-check {
  color: #67c23a;
}

.selection-mark .el-icon-close {
  color: #f56c6c;
}

.correct-mark .el-icon-check {
  color: #67c23a;
}

/* 答案解析样式增强 */
.answer-explanation {
  margin-top: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-left: 4px solid #409eff;
  border-radius: 4px;
}

.english-explanation {
  margin-top: 8px;
  font-style: italic;
  color: #909399;
}
</style>
