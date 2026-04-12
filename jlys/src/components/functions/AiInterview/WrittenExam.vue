<template>
  <div class="written-exam-container">
    <!-- 头部说明 -->
    <el-card class="exam-header">
      <h2>AI智能笔试</h2>
      <p>请认真作答，系统将自动保存你的答题记录，完成后将进入{{ route.query.interviewType === 'official' ? '正式面试' : '模拟面试' }}</p>
    </el-card>
    <!-- 加载骨架屏 -->
    <el-card v-if="loading" class="loading-card">
      <el-skeleton rows="5" animated />
    </el-card>
    <!-- 题目展示区 -->
    <el-card v-else class="questions-card">
      <el-form>
        <template v-for="(q, idx) in questions" :key="q.questionId">
          <!-- 类型分组标题，只在每类题型第一题显示 -->
          <div v-if="idx === 0 || q.type !== questions[idx-1].type" class="type-title">
            <el-divider content-position="left">
              {{ q.type === 'choice' ? '单选题' : q.type === 'true_false' ? '判断题' : q.type }}
            </el-divider>
          </div>
          <div class="question-block">
            <div class="question-title">
              <span class="q-index">{{ idx + 1 }}.</span>
              <span v-html="q.questionContent || q.content"></span>
              <el-tag size="small" type="info" class="q-diff">{{ q.difficulty }}</el-tag>
              <!-- 眼睛图标提示按钮，只在模拟面试中显示 -->
              <el-button v-if="!isOfficialInterview" type="text" size="small" @click="showHint(q.questionId)" class="eye-btn">
                <el-icon :color="hintVisible[q.questionId] ? '#409eff' : '#bfbfbf'" style="font-size: 22px;">
                  <View v-if="hintVisible[q.questionId]" />
                  <Hide v-else />
                </el-icon>
              </el-button>
              <!-- 判题结果标签 -->
              <el-tag v-if="resultMap[q.questionId]" :type="resultMap[q.questionId].correct ? 'success' : 'danger'" class="result-tag">
                {{ resultMap[q.questionId].correct ? '答对' : '答错' }}
              </el-tag>
            </div>
            <div class="question-content-row">
              <div class="question-left-align">
                <!-- 知识点、场景、易错等提示信息，只在模拟面试中显示 -->
                <div class="question-meta" v-if="!isOfficialInterview && hintVisible[q.questionId]">
                  <el-tag size="small" type="success">知识点: {{ q.knowledgePoint }}</el-tag>
                  <el-tag size="small" type="warning" v-if="q.scenario">场景: {{ q.scenario }}</el-tag>
                  <el-tag size="small" type="danger" v-if="q.trapDetection">易错: {{ q.trapDetection }}</el-tag>
                  <el-tag size="small" type="info" v-if="q.explanation">解析: {{ q.explanation }}</el-tag>
                </div>
                <!-- 判断题 -->
                <el-radio-group
                    v-if="q.type === 'true_false'"
                    v-model="userAnswers[q.questionId]"
                    class="options-group options-left-align"
                    :disabled="!!resultMap[q.questionId]"
                >
                  <el-radio :label="'true'">对</el-radio>
                  <el-radio :label="'false'">错</el-radio>
                </el-radio-group>
                <!-- 单选题 -->
                <el-radio-group
                    v-else
                    v-model="userAnswers[q.questionId]"
                    class="options-group options-left-align"
                    :disabled="!!resultMap[q.questionId]"
                >
                  <el-radio :label="'A'">A. {{ q.A || q.optionA }}</el-radio>
                  <el-radio :label="'B'">B. {{ q.B || q.optionB }}</el-radio>
                  <el-radio :label="'C'">C. {{ q.C || q.optionC }}</el-radio>
                  <el-radio :label="'D'">D. {{ q.D || q.optionD }}</el-radio>
                </el-radio-group>
              </div>
            </div>
          </div>
        </template>
      </el-form>
      <div class="submit-area">
        <el-button type="primary" size="large" @click="submitExam" :disabled="submitted">提交试卷</el-button>
      </div>
    </el-card>
    <el-dialog v-model="showResultDialog" title="本次笔试结果" width="400px" :close-on-click-modal="false" :show-close="false">
      <div v-if="examResult">
        <div style="font-size:18px;margin-bottom:10px;">
          总题数：{{ examResult.totalQuestions }}<br>
          答对：{{ examResult.correctCount }}<br>
          得分：{{ examResult.score }} 分
        </div>
        <el-table :data="examResult.questionJudgments" border size="small" style="width:100%;margin-bottom:10px;">
          <el-table-column prop="questionId" label="题号" width="80" />
          <el-table-column label="结果" width="80">
            <template #default="scope">
              <el-tag :type="scope.row.correct ? 'success' : 'danger'">
                {{ scope.row.correct ? '✔' : '✘' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="userAnswer" label="我的答案" width="100" />
          <el-table-column prop="correctAnswer" label="正确答案" width="100" />
        </el-table>
        <div style="text-align:center;">
          <el-button type="primary" @click="goHome">确定</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 倒计时弹窗 -->
    <el-dialog v-model="showCountdownDialog" title="准备进入面试" width="500px" :close-on-click-modal="false" :show-close="false">
      <div class="countdown-content">
        <div class="countdown-icon">
          <el-icon size="60" color="#409eff"><Clock /></el-icon>
        </div>
        <div class="countdown-text">
          <h3>面试即将开始</h3>
          <p>请做好准备，面试将在以下时间后自动开始：</p>
          <div class="countdown-timer">
            <span class="countdown-number">{{ countdownTime }}</span>
            <span class="countdown-unit">秒</span>
          </div>
          <div class="countdown-tips">
            <p>💡 请确保您的设备已准备就绪：</p>
            <ul>
              <li>摄像头和麦克风正常工作</li>
              <li>网络连接稳定</li>
              <li>环境安静，光线充足</li>
            </ul>
          </div>
        </div>
        <div class="countdown-actions">
          <el-button @click="skipCountdown" type="primary" size="large">立即开始面试</el-button>
          <el-button @click="cancelInterview" size="large">取消面试</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request' // 注释掉未使用的导入
import { View, Hide, Clock } from '@element-plus/icons-vue'

// 获取路由实例
const route = useRoute()
const router = useRouter()

// 题目列表 - 从后端接口获取的题目数据
const questions = ref([])
// 加载状态
const loading = ref(true)
// 用户答案，{ [questionId]: 'A' | 'B' | 'C' | 'D' }
const userAnswers = reactive({})
// 每题提示信息显示状态
const hintVisible = reactive({})
// 判题结果，{ [questionId]: { correct: boolean, correctAnswer: string } }
const resultMap = reactive({})
// 是否已提交
const submitted = ref(false)
const showResultDialog = ref(false)
const examResult = ref(null)

// 倒计时相关变量
const showCountdownDialog = ref(false)
const countdownTime = ref(10) // 10秒倒计时
let countdownTimer = null

// 判断是否为正式面试模式
const isOfficialInterview = computed(() => {
  return route.query.interviewType === 'official'
})

// 页面加载时获取题目列表
onMounted(async () => {
  try {
    // 从路由参数或sessionStorage获取taskId
    const taskId = route.query.taskId || sessionStorage.getItem('currentExamTaskId') // 注释掉未使用的变量
    console.log('获取的taskid: ', taskId)
    // ===== 模拟题目数据（用于测试，不调用后端接口） =====
    /*const mockQuestions = [
      {
        questionId: 'q1',
        type: 'choice',
        questionContent: '在Java中，以下哪个关键字用于定义常量？',
        A: 'const',
        B: 'final',
        C: 'static',
        D: 'constant',
        answer: 'B',
        difficulty: '初级',
        knowledgePoint: 'Java基础语法',
        scenario: '变量定义场景',
        trapDetection: '注意区分const和final，Java中没有const关键字'
      },
      {
        questionId: 'q2',
        type: 'true_false',
        questionContent: '在Spring框架中，@Autowired注解只能用于构造函数注入。',
        A: '对',
        B: '错',
        answer: 'B',
        difficulty: '中级',
        knowledgePoint: 'Spring依赖注入',
        scenario: 'Spring框架使用',
        trapDetection: '@Autowired可以用于字段注入、构造函数注入、setter方法注入'
      }
    ];*/

    // 使用模拟题目数据
    /* questions.value = mockQuestions;

     // 初始化用户答案和提示状态
     questions.value.forEach(q => {
       userAnswers[q.questionId] = '';
       hintVisible[q.questionId] = false;
     });

     ElMessage.success('模拟题目加载成功！');*/

    // ===== 注释掉原有的后端接口调用代码 =====
    if (!taskId) {
      ElMessage.error('未找到考试任务ID，请重新开始笔试')
      router.push('/layout/beforeExam')
      return
    }

    // 调用后端接口获取题目详情
    const res = await request.get(`/exam/questions/${taskId}`)

    if (!res || !res.questions || res.questions.length === 0) {
      ElMessage.error('未获取到题目，请重新生成')
      router.push('/layout/beforeExam')
      return
    }

    // 将数据库中的题目数据转换为前端格式
    questions.value = res.questions.map(q => ({
      questionId: q.questionId,
      type: q.type,
      questionContent: q.content,
      A: q.optionA,
      B: q.optionB,
      C: q.optionC,
      D: q.optionD,
      answer: q.correctAnswer,
      difficulty: q.difficulty || '中等',
      knowledgePoint: q.knowledgePoint || '',
      scenario: q.scenario || '',
      trapDetection: q.explanation || ''
    }))

  } catch (e) {
    console.error('获取题目错误:', e)
    ElMessage.error('获取题目失败，请重试')
    router.push('/layout/beforeExam')
  } finally {
    loading.value = false
  }
})

// 切换显示/隐藏提示信息
function showHint(qid) {
  hintVisible[qid] = !hintVisible[qid]
}

/**
 * 提交试卷：模拟判题并跳转到面试页面
 *
 * 模拟判题逻辑：
 * 1. 检查用户答案是否完整
 * 2. 模拟判题结果
 * 3. 显示判题结果
 * 4. 跳转到面试页面
 */
function submitExam() {
  // 检查是否所有题目都已作答
  const unansweredQuestions = questions.value.filter(q => !userAnswers[q.questionId])
  if (unansweredQuestions.length > 0) {
    ElMessage.warning(`还有 ${unansweredQuestions.length} 道题目未作答，请完成后再提交`)
    return
  }

  submitted.value = true

  // ===== 模拟判题逻辑 =====
  /*const mockResults = questions.value.map(q => {
    const userAnswer = userAnswers[q.questionId];
    const correctAnswer = q.answer;
    const isCorrect = userAnswer === correctAnswer;

    return {
      questionId: q.questionId,
      correct: isCorrect,
      correctAnswer: correctAnswer,
      userAnswer: userAnswer
    };
  });

  // 计算得分
  const correctCount = mockResults.filter(r => r.correct).length;
  const totalQuestions = mockResults.length;
  const score = Math.round((correctCount / totalQuestions) * 100);

  // 显示判题结果
  mockResults.forEach(r => {
    resultMap[r.questionId] = {
      correct: r.correct,
      correctAnswer: r.correctAnswer
    };
  });

  // 显示总结果弹窗
  examResult.value = {
    totalQuestions: totalQuestions,
    correctCount: correctCount,
    score: score,
    questionJudgments: mockResults
  };
  showResultDialog.value = true;

  ElMessage.success('判题完成，已显示答题结果！');*/

  // ===== 注释掉原有的后端接口调用代码 =====
  // 从sessionStorage获取taskId
  const taskId = sessionStorage.getItem('currentExamTaskId')
  if (!taskId) {
    ElMessage.error('未获取到考试ID，请重新开始笔试')
    router.push('/layout/beforeExam')
    return
  }

  // 构造提交数据：将用户答案转换为后端需要的格式
  const answerList = Object.entries(userAnswers).map(([questionId, answer]) => ({
    questionId,     // 题目ID
    userAnswer: answer  // 用户选择的答案（A、B、C、D），字段名与后端保持一致
  }))

  // 后端接口调用：提交答案进行判题
  request.post('/exam/submit-test', {
    taskId: taskId,
    answers: answerList
  }).then(res => {
    // 新增：如果有判题总结果，弹窗展示
    console.log('判题结果数据:', res)
    if (typeof res.totalQuestions === 'number' && res.totalQuestions > 0) {
      submitted.value = true // 标记为已提交
      examResult.value = res
      showResultDialog.value = true

      // 同时更新 resultMap 用于显示题目的对错状态
      if (res.questionJudgments && Array.isArray(res.questionJudgments)) {
        res.questionJudgments.forEach(qj => {
          resultMap[qj.questionId] = {
            correct: qj.correct,
            correctAnswer: qj.correctAnswer
          }
        })
      }
      return
    }

    // 兼容新接口的判题结果结构
    let resultArr = res.results
    if (!resultArr && Array.isArray(res.questionJudgments)) {
      // 转换格式为前端习惯的 results
      resultArr = res.questionJudgments.map(qj => ({
        questionId: qj.questionId,
        correct: qj.correct,
        correctAnswer: qj.correctAnswer
      }))
    }

    if (resultArr) {
      // 将判题结果保存到本地状态，用于显示正确/错误标记
      resultArr.forEach(r => {
        resultMap[r.questionId] = {
          correct: r.correct,
          correctAnswer: r.correctAnswer
        }
      })
      ElMessage.success('判题完成，已显示答题结果！')

      setTimeout(() => {
        const interviewType = route.query.interviewType
        const resumeId = route.query.resumeId
        const avatarId = route.query.avatarId
        const avatarConfig = route.query.avatarConfig // 获取数字人配置

        if (interviewType === 'official') {
          router.push({
            path: '/layout/offecialExam',
            query: {
              taskId: taskId,
              resumeId,
              avatarId,
              avatarConfig, // 传递数字人配置
              writtenExamResults: JSON.stringify(resultArr)
            }
          })
        } else {
          router.push({
            path: '/layout/simulatExam',
            query: {
              taskId: taskId,
              resumeId,
              avatarId,
              avatarConfig, // 传递数字人配置
              writtenExamResults: JSON.stringify(resultArr)
            }
          })
        }

        sessionStorage.removeItem('currentExamTaskId')
        sessionStorage.removeItem('examSettings')
      }, 2000)
    } else {
      ElMessage.error('未获取到判题结果')
    }
  }).catch(() => {
    ElMessage.error('提交失败，请重试')
  })

}

function goHome() {
  showResultDialog.value = false

  // 启动倒计时弹窗
  startCountdown()
}

/**
 * 启动倒计时
 */
function startCountdown() {
  countdownTime.value = 10 // 重置倒计时时间
  showCountdownDialog.value = true

  // 开始倒计时
  countdownTimer = setInterval(() => {
    countdownTime.value--

    if (countdownTime.value <= 0) {
      // 倒计时结束，自动跳转到面试页面
      clearInterval(countdownTimer)
      navigateToInterview()
    }
  }, 1000)
}

/**
 * 跳过倒计时，立即开始面试
 */
function skipCountdown() {
  clearInterval(countdownTimer)
  navigateToInterview()
}

/**
 * 取消面试
 */
function cancelInterview() {
  clearInterval(countdownTimer)
  showCountdownDialog.value = false
  ElMessage.info('已取消面试，返回首页')
  router.push('/layout')
}

/**
 * 跳转到面试页面
 */
function navigateToInterview() {
  showCountdownDialog.value = false

  // 根据面试类型跳转到相应的面试页面
  const interviewType = route.query.interviewType
  const resumeId = route.query.resumeId
  const avatarId = route.query.avatarId
  const avatarConfig = route.query.avatarConfig // 获取数字人配置

  if (interviewType === 'official') {
    // 跳转到正式面试
    router.push({
      path: '/layout/offecialExam',
      query: {
        taskId: 'mockTaskId',
        resumeId,
        avatarId,
        avatarConfig, // 传递数字人配置
        writtenExamResults: JSON.stringify(examResult.value.questionJudgments)
      }
    })
  } else {
    // 跳转到模拟面试
    router.push({
      path: '/layout/simulatExam',
      query: {
        taskId: 'mockTaskId',
        resumeId,
        avatarId,
        avatarConfig, // 传递数字人配置
        writtenExamResults: JSON.stringify(examResult.value.questionJudgments)
      }
    })
  }

  // 清除sessionStorage中的考试数据
  sessionStorage.removeItem('currentExamTaskId')
  sessionStorage.removeItem('examSettings')
}
</script>

<style scoped>
/* 全局样式，确保页面可以滚动 */
:global(html, body) {
  overflow-x: hidden;
  overflow-y: auto;
}

.written-exam-container {
  max-width: 900px;
  margin: 48px auto 0 auto;
  padding-bottom: 80px;
  background: linear-gradient(135deg, #f8fafc 0%, #eaf4ff 100%);
  border-radius: 32px;
  box-shadow: 0 8px 40px rgba(91,188,255,0.10);
  min-height: 100vh;
  overflow-y: auto;
  overflow-x: hidden;
}
.exam-header {
  margin-bottom: 38px;
  text-align: center;
  background: linear-gradient(90deg, #eaf4ff 60%, #f5f7fa 100%);
  border-radius: 22px;
  box-shadow: 0 4px 18px rgba(91,188,255,0.07);
  padding: 38px 0 28px 0;
}
.exam-header h2 {
  font-size: 2.4rem;
  font-weight: 900;
  background: linear-gradient(90deg, #5bbcff, #a084e8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 12px;
  letter-spacing: 2px;
}
.exam-header p {
  font-size: 1.25rem;
  color: #5bbcff;
  font-weight: 600;
}
.questions-card {
  padding: 40px 32px 32px 32px;
  border-radius: 22px;
  box-shadow: 0 4px 18px rgba(91,188,255,0.07);
  background: #fff;
}
.question-block {
  margin-bottom: 48px;
  padding-bottom: 24px;
  border-bottom: 2px dashed #eaf4ff;
  background: rgba(91,188,255,0.03);
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(91,188,255,0.04);
  transition: box-shadow 0.3s;
}
.question-block:hover {
  box-shadow: 0 8px 24px rgba(91,188,255,0.10);
}
.question-title {
  font-size: 1.35rem;
  font-weight: 700;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
}
.q-index {
  color: #5bbcff;
  font-size: 2.1rem;
  font-weight: 900;
  margin-right: 6px;
  letter-spacing: 1px;
  text-shadow: 0 2px 8px rgba(91,188,255,0.10);
}
.q-diff {
  margin-left: 10px;
  font-size: 1.08rem;
  font-weight: 700;
  background: #eaf4ff !important;
  color: #5bbcff !important;
  border: none !important;
}
.question-content-row {
  display: flex;
  flex-direction: row;
}
.question-left-align {
  margin-left: calc(2.2em + 8px);
  width: 100%;
}
.options-group {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  /* 不设置gap/font-size/padding */
}
.el-radio,
.el-radio__label {
  font-size: 3.2rem !important;
  font-weight: normal !important;
  letter-spacing: 1.5px;
}
.el-radio__input.is-checked + .el-radio__label {
  color: #5bbcff !important;
  font-weight: 900;
}
.el-radio__input.is-checked .el-radio__inner {
  border-color: #5bbcff !important;
  background: linear-gradient(90deg, #5bbcff, #a084e8) !important;
}
.el-radio__inner {
  width: 30px !important;
  height: 30px !important;
  border-radius: 50% !important;
  border: 2.5px solid #eaf4ff !important;
  box-shadow: 0 2px 8px rgba(91,188,255,0.08);
}
.el-radio__label {
  font-size: 2.8rem !important;
  padding-left: 24px;
  letter-spacing: 2.5px;
  font-weight: normal !important;
}
.submit-area {
  text-align: center;
  margin-top: 40px;
}
.submit-area .el-button {
  font-size: 1.35rem;
  font-weight: 900;
  padding: 18px 60px;
  border-radius: 30px;
  background: linear-gradient(90deg, #5bbcff, #a084e8);
  color: #fff;
  box-shadow: 0 6px 24px rgba(91,188,255,0.13);
  border: none;
  transition: background 0.2s, box-shadow 0.2s, transform 0.2s;
}
.submit-area .el-button:hover {
  background: linear-gradient(90deg, #00cfff, #a084e8);
  box-shadow: 0 12px 32px rgba(91,188,255,0.18);
  transform: translateY(-2px) scale(1.03);
}
.loading-card {
  margin-top: 40px;
  border-radius: 18px;
  box-shadow: 0 4px 18px rgba(91,188,255,0.07);
}
.type-title {
  margin: 38px 0 18px 0;
  font-size: 1.18rem;
  font-weight: 800;
  color: #5bbcff;
  letter-spacing: 1px;
}
.eye-btn {
  margin-left: 14px;
  padding: 0 6px;
}
.eye-btn .el-icon {
  transition: color 0.2s, transform 0.2s;
  font-size: 1.5rem !important;
}
.eye-btn:hover .el-icon {
  color: #00cfff !important;
  transform: scale(1.18);
}
.result-tag {
  margin-left: 16px;
  font-size: 1.18rem !important;
  font-weight: 900;
  border-radius: 12px !important;
  padding: 4px 18px !important;
  box-shadow: 0 2px 8px rgba(91,188,255,0.10);
}
.option-correct {
  color: #4fd18b !important;
  font-weight: bold;
  font-size: 1.13rem;
}
.option-wrong {
  color: #ffb347 !important;
  font-weight: bold;
  font-size: 1.13rem;
}
.correct-answer {
  color: #4fd18b;
  font-size: 1.13rem;
  margin-left: 6px;
}

/* 倒计时弹窗样式 */
.countdown-content {
  text-align: center;
  padding: 32px 0 18px 0;
}
.countdown-icon {
  margin-bottom: 24px;
}
.countdown-text h3 {
  color: #5bbcff;
  margin-bottom: 14px;
  font-size: 2rem;
  font-weight: 900;
  letter-spacing: 1px;
}
.countdown-text p {
  color: #606266;
  margin-bottom: 24px;
  font-size: 1.18rem;
}
.countdown-timer {
  margin: 36px 0 18px 0;
  display: flex;
  justify-content: center;
  align-items: baseline;
  gap: 10px;
}
.countdown-number {
  font-size: 3.2rem;
  font-weight: 900;
  color: #5bbcff;
  font-family: 'Courier New', monospace;
  text-shadow: 0 2px 8px rgba(91,188,255,0.10);
}
.countdown-unit {
  font-size: 1.5rem;
  color: #606266;
}
.countdown-tips {
  background: #eaf4ff;
  border-radius: 12px;
  padding: 18px 18px 12px 18px;
  margin: 24px 0 0 0;
  text-align: left;
  border-left: 5px solid #5bbcff;
  box-shadow: 0 2px 8px rgba(91,188,255,0.07);
}
.countdown-tips p {
  color: #5bbcff;
  font-weight: bold;
  margin-bottom: 10px;
  font-size: 1.13rem;
}
.countdown-tips ul {
  margin: 0;
  padding-left: 22px;
  color: #606266;
  font-size: 1.08rem;
}
.countdown-tips li {
  margin-bottom: 6px;
  line-height: 1.7;
}
.countdown-actions {
  margin-top: 36px;
  display: flex;
  justify-content: center;
  gap: 22px;
}
.countdown-actions .el-button {
  font-size: 1.18rem;
  font-weight: 700;
  border-radius: 24px;
  padding: 12px 38px;
  background: linear-gradient(90deg, #5bbcff, #a084e8);
  color: #fff;
  border: none;
  box-shadow: 0 2px 8px rgba(91,188,255,0.10);
  transition: background 0.2s, box-shadow 0.2s, transform 0.2s;
}
.countdown-actions .el-button:hover {
  background: linear-gradient(90deg, #00cfff, #a084e8);
  box-shadow: 0 8px 24px rgba(91,188,255,0.18);
  transform: translateY(-2px) scale(1.03);
}

/* 结果弹窗表格美化 */
.el-dialog__body .el-table th {
  background: linear-gradient(90deg, #5bbcff 60%, #a084e8 100%);
  color: #fff;
  font-size: 1.13rem;
  font-weight: 700;
  border: none;
}
.el-dialog__body .el-table td {
  font-size: 1.13rem;
  color: #222;
  border: none;
}
.el-dialog__body .el-tag {
  font-size: 1.18rem;
  font-weight: 900;
  border-radius: 10px;
  padding: 2px 16px;
}

/* 响应式 */
@media (max-width: 1100px) {
  .written-exam-container {
    max-width: 98vw;
    border-radius: 18px;
    margin: 24px 1vw 0 1vw;
    min-height: 100vh;
    overflow-y: auto;
    overflow-x: hidden;
  }
  .questions-card {
    padding: 18px 4vw 18px 4vw;
  }
}
@media (max-width: 700px) {
  .written-exam-container {
    margin: 8px 0 0 0;
    padding-bottom: 30px;
    border-radius: 8px;
    min-height: 100vh;
    overflow-y: auto;
    overflow-x: hidden;
  }
  .exam-header {
    border-radius: 10px;
    padding: 18px 0 12px 0;
  }
  .questions-card {
    border-radius: 10px;
    padding: 8px 2vw 8px 2vw;
  }
  .question-block {
    border-radius: 6px;
    padding-bottom: 10px;
    margin-bottom: 18px;
  }
  .question-title {
    font-size: 1.08rem;
    gap: 8px;
  }
  .q-index {
    font-size: 1.3rem;
  }
  .options-group, .el-radio, .el-radio__label {
    font-size: 1.7rem !important;
  }
  .el-radio__inner {
    width: 22px !important;
    height: 22px !important;
  }
  .submit-area .el-button {
    font-size: 1.08rem;
    padding: 10px 18vw;
  }
}

</style>
