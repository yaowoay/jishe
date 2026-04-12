<template>
  <div class="disc-test-container">
    <!-- 测试说明页面 -->
    <div v-if="currentStep === 'intro'" class="test-intro">
      <el-card class="intro-card">
        <template #header>
          <div class="card-header">
            <h2>DISC 职业性格测试</h2>
            <el-tag type="primary" size="large">专业权威</el-tag>
          </div>
        </template>
        
        <div class="intro-content">
          <h3>测试简介</h3>
          <p>
            DISC性格测试是由美国心理学家威廉·莫尔顿·马斯顿创建的权威性格评估工具，
            已被广泛应用于世界500强企业的人才招聘和职业发展指导。
          </p>
          
          <h3>测试维度</h3>
          <div class="dimensions">
            <div class="dimension-item">
              <div class="dimension-icon d-type">D</div>
              <div class="dimension-info">
                <h4>支配型 (Dominance)</h4>
                <p>控制者，天生的领袖，喜欢挑战和竞争</p>
              </div>
            </div>
            
            <div class="dimension-item">
              <div class="dimension-icon i-type">I</div>
              <div class="dimension-info">
                <h4>影响型 (Influence)</h4>
                <p>社交者，活泼外向，善于沟通和影响他人</p>
              </div>
            </div>
            
            <div class="dimension-item">
              <div class="dimension-icon s-type">S</div>
              <div class="dimension-info">
                <h4>稳定型 (Steadiness)</h4>
                <p>支持者，平和稳定，注重团队合作</p>
              </div>
            </div>
            
            <div class="dimension-item">
              <div class="dimension-icon c-type">C</div>
              <div class="dimension-info">
                <h4>谨慎型 (Compliance)</h4>
                <p>完美主义者，注重细节和准确性</p>
              </div>
            </div>
          </div>
          
          <h3>测试说明</h3>
          <ul class="test-instructions">
            <li>本测试共40道题，每题4个选项</li>
            <li>请在每组选项中选择最符合您的一项</li>
            <li>请按第一印象快速选择，不要过度思考</li>
            <li>如果不确定，可回忆童年时的情况</li>
            <li>测试时间约10-15分钟</li>
          </ul>
          
          <div class="start-button-container">
            <el-button type="primary" size="large" @click="startTest" :loading="loading">
              开始测评
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 测试进行页面 -->
    <div v-if="currentStep === 'testing'" class="test-progress">
      <div class="progress-header">
        <el-progress 
          :percentage="progressPercentage" 
          :stroke-width="8"
          :show-text="false"
        />
        <div class="progress-text">
          第 {{ currentQuestionIndex + 1 }} 题 / 共 {{ totalQuestions }} 题
        </div>
      </div>
      
      <el-card class="question-card" v-if="currentQuestion">
        <template #header>
          <h3>第 {{ currentQuestionIndex + 1 }} 题</h3>
        </template>
        
        <div class="question-content">
          <p class="question-instruction">请选择最符合您的选项：</p>
          
          <el-radio-group 
            v-model="currentAnswer" 
            class="option-group"
            @change="handleAnswerChange"
          >
            <el-radio 
              v-for="option in currentQuestion.options" 
              :key="option.optionLetter"
              :label="option.optionLetter"
              class="option-item"
            >
              <div class="option-content">
                <span class="option-letter">{{ option.optionLetter }}.</span>
                <span class="option-text">{{ option.optionText }}</span>
              </div>
            </el-radio>
          </el-radio-group>
        </div>
        
        <div class="question-actions">
          <el-button 
            @click="previousQuestion" 
            :disabled="currentQuestionIndex === 0"
          >
            上一题
          </el-button>
          
          <el-button 
            type="primary" 
            @click="nextQuestion"
            :disabled="!currentAnswer"
            :loading="loading"
          >
            {{ currentQuestionIndex === totalQuestions - 1 ? '完成测试' : '下一题' }}
          </el-button>
        </div>
      </el-card>
    </div>
    
    <!-- 测试结果页面 -->
    <div v-if="currentStep === 'result'" class="test-result">
      <el-card class="result-card">
        <template #header>
          <div class="result-header">
            <h2>DISC 性格测试结果</h2>
            <el-tag :type="getTypeTagType(testResult.primaryType)" size="large" v-if="testResult">
              {{ testResult.primaryType }} 型主导
            </el-tag>
          </div>
        </template>
        
        <div class="result-content" v-if="testResult">
          <!-- 得分概览 -->
          <div class="score-overview">
            <h3>各维度得分</h3>
            <div class="score-charts">
              <div 
                v-for="(score, type) in testResult.scores" 
                :key="type"
                class="score-item"
              >
                <div class="score-label">{{ getTypeName(type) }}</div>
                <el-progress 
                  :percentage="(score / totalQuestions) * 100"
                  :color="getTypeColor(type)"
                  :stroke-width="20"
                />
                <div class="score-value">{{ score }} 分</div>
              </div>
            </div>
          </div>
          
          <!-- 性格类型描述 -->
          <div class="type-description" v-if="testResult.typeDescription">
            <h3>{{ testResult.typeDescription.typeTitle }}</h3>
            <p class="type-desc">{{ testResult.typeDescription.description }}</p>
            
            <div class="characteristics">
              <h4>主要特征</h4>
              <p>{{ testResult.typeDescription.characteristics }}</p>
            </div>
            
            <div class="work-style">
              <h4>工作风格</h4>
              <p>{{ testResult.typeDescription.workStyle }}</p>
            </div>
            
            <div class="career-suggestions">
              <h4>适合职业</h4>
              <p>{{ testResult.typeDescription.careerFields }}</p>
            </div>
          </div>
          
          <!-- 详细分析 -->
          <div class="detailed-analysis">
            <h3>详细分析</h3>
            <p>{{ testResult.detailedAnalysis }}</p>
          </div>
          
          <!-- 优势与建议 -->
          <div class="strengths-weaknesses">
            <div class="strengths">
              <h4>优势特点</h4>
              <p>{{ testResult.strengths }}</p>
            </div>
            
            <div class="improvement">
              <h4>发展建议</h4>
              <p>{{ testResult.weaknesses }}</p>
            </div>
          </div>
        </div>
        
        <div class="result-actions">
          <el-button @click="restartTest">重新测试</el-button>
          <el-button type="primary" @click="saveResult">保存结果</el-button>
          <el-button @click="goBack">返回测试选择</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { discTestApi } from '@/api/discTest'

// 路由
const router = useRouter()

// 响应式数据
const currentStep = ref('intro') // intro, testing, result
const testSession = ref('')
const questions = ref([])
const answers = ref({})
const currentQuestionIndex = ref(0)
const currentAnswer = ref('')
const testResult = ref(null)
const loading = ref(false)

// 计算属性
const totalQuestions = computed(() => questions.value.length || 40)

const progressPercentage = computed(() => {
  return Math.round(((currentQuestionIndex.value + 1) / totalQuestions.value) * 100)
})

const currentQuestion = computed(() => {
  return questions.value[currentQuestionIndex.value]
})

// 方法
const startTest = async () => {
  try {
    loading.value = true
    const response = await discTestApi.startTest()
    
    if (response.success) {
      testSession.value = response.data.testSession
      questions.value = response.data.questions
      currentStep.value = 'testing'
      currentQuestionIndex.value = 0
      currentAnswer.value = ''
      
      // 如果有之前的答案，恢复它
      if (answers.value[1]) {
        currentAnswer.value = answers.value[1]
      }
      
      ElMessage.success('测试开始')
    } else {
      ElMessage.error('开始测试失败：' + response.message)
    }
  } catch (error) {
    console.error('开始测试失败：', error)
    ElMessage.error('开始测试失败，请重试')
  } finally {
    loading.value = false
  }
}

const handleAnswerChange = (value) => {
  const questionGroup = currentQuestionIndex.value + 1
  answers.value[questionGroup] = value
}

const nextQuestion = () => {
  if (!currentAnswer.value) {
    ElMessage.warning('请选择一个选项')
    return
  }
  
  // 保存当前答案
  const questionGroup = currentQuestionIndex.value + 1
  answers.value[questionGroup] = currentAnswer.value
  
  if (currentQuestionIndex.value === totalQuestions.value - 1) {
    // 最后一题，提交测试
    submitTest()
  } else {
    // 下一题
    currentQuestionIndex.value++
    currentAnswer.value = answers.value[currentQuestionIndex.value + 1] || ''
  }
}

const previousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
    currentAnswer.value = answers.value[currentQuestionIndex.value + 1] || ''
  }
}

const submitTest = async () => {
  try {
    loading.value = true
    
    // 构建答案数组
    const answerArray = []
    for (let i = 1; i <= totalQuestions.value; i++) {
      if (answers.value[i]) {
        answerArray.push({
          questionGroup: i,
          selectedOption: answers.value[i]
        })
      }
    }
    
    if (answerArray.length !== totalQuestions.value) {
      ElMessage.error('请完成所有题目')
      return
    }
    
    const response = await discTestApi.submitTest({
      testSession: testSession.value,
      answers: answerArray
    })
    
    if (response.success) {
      testResult.value = response.data.result
      currentStep.value = 'result'
      ElMessage.success('测试完成')

      // 通知父组件刷新历史记录
      window.dispatchEvent(new CustomEvent('discTestCompleted', {
        detail: { result: response.data.result }
      }))
    } else {
      ElMessage.error('提交测试失败：' + response.message)
    }
  } catch (error) {
    console.error('提交测试失败：', error)
    ElMessage.error('提交测试失败，请重试')
  } finally {
    loading.value = false
  }
}

const restartTest = () => {
  ElMessageBox.confirm('确定要重新开始测试吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 重置所有数据
    currentStep.value = 'intro'
    testSession.value = ''
    questions.value = []
    answers.value = {}
    currentQuestionIndex.value = 0
    currentAnswer.value = ''
    testResult.value = null
  })
}

const saveResult = () => {
  ElMessage.success('结果已保存')
  // 返回到测试选择页面，触发历史记录刷新
  setTimeout(() => {
    router.push({ name: 'PersonalityTest' })
  }, 500)
}

const goBack = () => {
  router.push({ name: 'PersonalityTest' })
}

// 辅助方法
const getTypeName = (type) => {
  const typeNames = {
    'D': '支配型',
    'I': '影响型', 
    'S': '稳定型',
    'C': '谨慎型'
  }
  return typeNames[type] || type
}

const getTypeColor = (type) => {
  const colors = {
    'D': '#ff4757',
    'I': '#ffa502',
    'S': '#2ed573', 
    'C': '#3742fa'
  }
  return colors[type] || '#409eff'
}

const getTypeTagType = (type) => {
  const tagTypes = {
    'D': 'danger',
    'I': 'warning',
    'S': 'success',
    'C': 'primary'
  }
  return tagTypes[type] || 'primary'
}
</script>

<style scoped>
.disc-test-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

/* 介绍页面样式 */
.intro-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.intro-content h3 {
  color: #409eff;
  margin-top: 30px;
  margin-bottom: 15px;
}

.dimensions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin: 20px 0;
}

.dimension-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #fafafa;
}

.dimension-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  color: white;
  margin-right: 15px;
}

.d-type { background: #ff4757; }
.i-type { background: #ffa502; }
.s-type { background: #2ed573; }
.c-type { background: #3742fa; }

.dimension-info h4 {
  margin: 0 0 5px 0;
  color: #303133;
}

.dimension-info p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.test-instructions {
  background: #f0f9ff;
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.test-instructions li {
  margin-bottom: 8px;
  color: #606266;
}

.start-button-container {
  text-align: center;
  margin-top: 30px;
}

/* 测试进行页面样式 */
.progress-header {
  margin-bottom: 30px;
  text-align: center;
}

.progress-text {
  margin-top: 10px;
  font-size: 16px;
  color: #606266;
}

.question-card {
  margin-bottom: 20px;
}

.question-instruction {
  font-size: 16px;
  color: #303133;
  margin-bottom: 20px;
}

.option-group {
  width: 100%;
}

.option-group .el-radio {
  margin-right: 0 !important;
  margin-bottom: 0 !important;
}

.option-item {
  display: flex !important;
  align-items: flex-start !important;
  width: 100%;
  margin-bottom: 15px;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s;
  background: white;
}

.option-item:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.option-item .el-radio__input {
  margin-top: 2px;
  margin-right: 12px;
  flex-shrink: 0;
}

.option-item .el-radio__label {
  padding-left: 0 !important;
  flex: 1;
}

.option-content {
  display: flex;
  align-items: flex-start;
  width: 100%;
}

.option-letter {
  font-weight: bold;
  color: #409eff;
  margin-right: 10px;
  min-width: 20px;
  flex-shrink: 0;
}

.option-text {
  flex: 1;
  line-height: 1.5;
  color: #303133;
}

.question-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
}

/* 结果页面样式 */
.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-header h2 {
  margin: 0;
  color: #303133;
}

.score-overview {
  margin-bottom: 30px;
}

.score-overview h3 {
  color: #409eff;
  margin-bottom: 20px;
}

.score-charts {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.score-item {
  text-align: center;
}

.score-label {
  font-weight: bold;
  margin-bottom: 10px;
  color: #303133;
}

.score-value {
  margin-top: 10px;
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
}

.type-description {
  margin-bottom: 30px;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
}

.type-description h3 {
  color: #409eff;
  margin-top: 0;
}

.type-desc {
  font-size: 16px;
  line-height: 1.6;
  color: #303133;
  margin-bottom: 20px;
}

.characteristics, .work-style, .career-suggestions {
  margin-bottom: 15px;
}

.characteristics h4, .work-style h4, .career-suggestions h4 {
  color: #606266;
  margin-bottom: 8px;
}

.detailed-analysis {
  margin-bottom: 30px;
  padding: 20px;
  background: #f0f9ff;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.detailed-analysis h3 {
  color: #409eff;
  margin-top: 0;
}

.strengths-weaknesses {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 30px;
}

.strengths, .improvement {
  padding: 20px;
  border-radius: 8px;
}

.strengths {
  background: #f0f9ff;
  border-left: 4px solid #67c23a;
}

.improvement {
  background: #fef0f0;
  border-left: 4px solid #f56c6c;
}

.strengths h4 {
  color: #67c23a;
  margin-top: 0;
}

.improvement h4 {
  color: #f56c6c;
  margin-top: 0;
}

.result-actions {
  text-align: center;
  margin-top: 30px;
}

.result-actions .el-button {
  margin: 0 10px;
}

/* Element Plus 样式覆盖 */
:deep(.el-radio) {
  margin-right: 0 !important;
  margin-bottom: 0 !important;
  width: 100%;
  display: flex !important;
  align-items: flex-start !important;
}

:deep(.el-radio__input) {
  margin-top: 2px !important;
  margin-right: 12px !important;
  flex-shrink: 0 !important;
}

:deep(.el-radio__label) {
  padding-left: 0 !important;
  flex: 1 !important;
  font-size: 14px !important;
  line-height: 1.5 !important;
}

:deep(.el-radio.is-checked .el-radio__input .el-radio__inner) {
  background-color: #409eff !important;
  border-color: #409eff !important;
}

:deep(.el-radio:hover .el-radio__input .el-radio__inner) {
  border-color: #409eff !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .disc-test-container {
    padding: 10px;
  }

  .dimensions {
    grid-template-columns: 1fr;
  }

  .score-charts {
    grid-template-columns: 1fr;
  }

  .strengths-weaknesses {
    grid-template-columns: 1fr;
  }

  .question-actions {
    flex-direction: column;
    gap: 10px;
  }

  .result-actions .el-button {
    margin: 5px;
    width: 100%;
  }
}
</style>
