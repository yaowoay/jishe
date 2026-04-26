<template>
  <div class="interview-report">
    <div class="report-header">
      <div class="header-content">
        <h1>面试评估报告</h1>
        <div class="interview-info">
          <span class="info-item"><el-icon><Clock /></el-icon> 面试时长：{{ reportData.duration }}</span>
          <span class="info-item"><el-icon><Calendar /></el-icon> 完成时间：{{ reportData.interviewDate }}</span>
          <span class="info-item"><el-icon><User /></el-icon> 面试类型：{{ reportData.interviewType }}</span>
        </div>
      </div>
      <div class="header-actions">
        <el-button @click="goBack" icon="ArrowLeft">返回上一页</el-button>
        <el-button @click="goHome">返回主页</el-button>
        <el-button type="primary" @click="exportReport" icon="Download">导出报告</el-button>
      </div>
    </div>

    <div class="report-layout">
      <div class="report-main">
        <el-card class="score-card">
          <template #header><div class="card-header"><el-icon><TrendCharts /></el-icon><span>综合评分</span></div></template>
          <div class="overall-score">
            <div class="score-gauge" :style="scoreGaugeStyle">
              <div class="gauge-inner">
                <div class="score-number">{{ reportData.overallScore }}</div>
                <div class="score-label">总分</div>
              </div>
            </div>
            <div class="score-breakdown">
              <div class="score-item" v-for="item in reportData.scoreBreakdown" :key="item.name">
                <span class="score-name">{{ item.name }}</span>
                <div class="score-bar"><div class="score-fill" :style="{ width: item.score + '%' }"></div></div>
                <span class="score-value">{{ item.score }}</span>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="evaluation-card" v-if="mainEvaluations.length > 0">
          <template #header><div class="card-header"><el-icon><Star /></el-icon><span>综合与改进建议</span></div></template>
          <div class="evaluation-list">
            <div class="evaluation-item" v-for="evaluation in mainEvaluations" :key="evaluation.category">
              <div class="eval-header">
                <span class="eval-category">{{ evaluation.category }}</span>
                <el-rate v-model="evaluation.rating" disabled show-score text-color="#ff9900"></el-rate>
              </div>
              <div class="eval-comment">{{ evaluation.comment }}</div>
              <div class="eval-suggestions" v-if="evaluation.suggestions && evaluation.suggestions.length > 0">
                <strong>改进建议：</strong>
                <ul><li v-for="suggestion in evaluation.suggestions" :key="suggestion">{{ suggestion }}</li></ul>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="resources-card" v-if="reportData.learningResources && reportData.learningResources.length > 0">
          <template #header><div class="card-header"><el-icon><Reading /></el-icon><span>学习资源推荐</span></div></template>
          <div class="resources-list grid">
            <div class="resource-item" v-for="resource in reportData.learningResources" :key="resource.title || resource.name">
              <h4 class="resource-name">{{ resource.title || resource.name }}</h4>
              <div class="resource-meta-line">
                <el-tag :type="getResourceTypeColor(resource.type)" size="small">{{ getResourceTypeName(resource.type) }}</el-tag>
                <el-button type="primary" link @click="openResource(resource.url)" v-if="resource.url">访问</el-button>
              </div>
              <p class="resource-description">{{ resource.description }}</p>
            </div>
          </div>
        </el-card>
      </div>

      <div class="report-side">
        <el-card class="side-answer-card">
          <template #header><div class="card-header"><el-icon><Star /></el-icon><span>笔试答题情况</span></div></template>
          <div class="answer-content">
            <div class="answer-score">得分：{{ writtenEvaluation.score }}</div>
            <div class="answer-text">{{ writtenEvaluation.comment }}</div>
          </div>
        </el-card>

        <el-card class="side-answer-card">
          <template #header><div class="card-header"><el-icon><Star /></el-icon><span>面试答题情况</span></div></template>
          <div class="answer-content">
            <div class="answer-score">得分：{{ interviewEvaluation.score }}</div>
            <div class="answer-text">{{ interviewEvaluation.comment }}</div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Clock,
  Calendar,
  User,
  ArrowLeft,
  Download,
  TrendCharts,
  Star,
  Reading
} from '@element-plus/icons-vue'
import {quickEvaluate} from '@/api/interview'


const router = useRouter()

// 面试报告数据
const reportData = reactive({
  interviewDate: '',
  duration: '',
  interviewType: '',
  overallScore: 0,
  scoreBreakdown: [],
  detailedEvaluations: [],
  learningResources: [],
  evaluationData: null
})

const mainEvaluations = computed(() => {
  if (!reportData.detailedEvaluations || reportData.detailedEvaluations.length === 0) return []
  return reportData.detailedEvaluations.filter(item => !['笔试答题情况', '面试答题情况'].includes(item.category))
})

const writtenEvaluation = computed(() => {
  const found = reportData.detailedEvaluations?.find(item => item.category === '笔试答题情况')
  return {
    score: found?.score ?? found?.rating ?? '--',
    comment: found?.comment || '暂无笔试答题情况数据'
  }
})

const interviewEvaluation = computed(() => {
  const found = reportData.detailedEvaluations?.find(item => item.category === '面试答题情况')
  return {
    score: found?.score ?? found?.rating ?? '--',
    comment: found?.comment || '暂无面试答题情况数据'
  }
})

const scoreGaugeStyle = computed(() => {
  const score = Number(reportData.overallScore) || 0
  const deg = Math.max(0, Math.min(100, score)) * 3.6
  return {
    background: `conic-gradient(#2563eb 0deg ${deg}deg, #e5e7eb ${deg}deg 360deg)`
  }
})


async function getDate(){
  // 从路由参数获取 applicationId
  const applicationId = router.currentRoute.value.query.applicationId ||
      router.currentRoute.value.params.id

  // 或者从 sessionStorage 获取
  // const applicationId = sessionStorage.getItem('currentApplicationId');

  if (!applicationId) {
    console.error('缺少 applicationId')
    ElMessage.error('无法获取面试记录ID')
    return
  }

  try {
    const { data } = await quickEvaluate(applicationId)
    reportData.overallScore = data.weightedScore
  } catch (error) {
    console.error('获取评估失败:', error)
    ElMessage.error('获取面试评估失败')
  }
}
getDate()

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 返回主页
const goHome = () => {
  router.push('/')
}

// 导出报告
const exportReport = () => {
  ElMessage.info('导出功能开发中...')
}

// 测试Dify数据处理（开发调试用）
window.testDifyDataProcessing = () => {
  const testData = {
    'ms_answer_score': {
      'jobTitle': 'Java开发工程师',
      'question': '请简要介绍一下你自己。',
      'candidateResponse': '我叫张三，毕业于某某大学计算机专业，有三年的软件开发经验，熟悉Java和Python编程语言，曾在某某公司参与过多个大型项目的开发工作。',
      'score': 85
    },
    'bs_answer_score': {
      'answer': '没有提供具体的问题和回答内容，无法进行打分。请提供具体的面试问题和候选者的回答内容以便进行评分。'
    },
    'total_answer': {
      'interviewScore': 85,
      'writtenScore': 0,
      'finalScore': 59.5
    },
    'study_skills': {
      'candidateId': '张三',
      'interviewScore': 85,
      'writtenTestScore': 0,
      'weightedScore': 59.5,
      'jobPosition': 'Java开发工程师',
      'resources': [
        {
          'name': 'Java编程思想',
          'type': 'book',
          'url': 'https://book.douban.com/subject/2130190/'
        },
        {
          'name': 'Python官方文档',
          'type': 'article',
          'url': 'https://docs.python.org/zh-cn/3/'
        },
        {
          'name': 'LeetCode算法练习',
          'type': 'practice',
          'url': 'https://leetcode-cn.com/'
        }
      ],
      'recommendations': [
        '加强笔试准备，尤其是针对岗位相关的技术问题',
        '深入理解数据结构和算法，提升编程能力',
        '参与开源项目，增加实战经验'
      ],
      'improvementAreas': [
        '笔试表现',
        '算法能力',
        '技术深度'
      ]
    }
  }

  console.log('测试Dify数据处理:', testData)

  // 模拟存储测试数据
  const testReportData = {
    endTime: new Date().toISOString(),
    duration: '30分钟',
    interviewType: '技术面试',
    evaluationData: testData
  }

  localStorage.setItem('interviewReportData', JSON.stringify(testReportData))

  // 重新加载数据
  location.reload()
}

// 获取资源类型颜色
const getResourceTypeColor = (type) => {
  const colorMap = {
    'book': 'primary',
    'video': 'success',
    'course': 'warning',
    'article': 'info',
    'website': 'danger'
  }
  return colorMap[type] || 'info'
}

// 获取资源类型名称
const getResourceTypeName = (type) => {
  const nameMap = {
    'book': '书籍',
    'video': '视频',
    'course': '课程',
    'article': '文章',
    'website': '网站'
  }
  return nameMap[type] || '资源'
}

// 打开资源链接
const openResource = (url) => {
  if (url) {
    window.open(url, '_blank')
  }
}

// 从sessionStorage加载面试数据
const loadInterviewData = () => {
  try {
    // 优先加载新的报告数据格式
    const reportDataStored = sessionStorage.getItem('interviewReportData')
    if (reportDataStored) {
      const interviewReportData = JSON.parse(reportDataStored)
      console.log('加载的面试报告数据:', interviewReportData)

      // 基础信息
      reportData.interviewDate = new Date(interviewReportData.endTime).toLocaleString('zh-CN')
      reportData.duration = interviewReportData.duration || '未知'
      reportData.interviewType = interviewReportData.interviewType || '面试'

      // 如果有评估数据，使用真实的评估结果
      if (interviewReportData.evaluationData) {
        const evalData = interviewReportData.evaluationData
        console.log('使用真实评估数据:', evalData)

        reportData.evaluationData = evalData

        // 处理Dify工作流返回的数据结构
        let processedData = {}

        // 检查是否是Dify工作流的数据结构
        if (evalData.total_answer && evalData.study_skills) {
          console.log('检测到Dify工作流数据结构，进行转换')
          processedData = {
            interviewScore: evalData.total_answer.interviewScore || evalData.ms_answer_score?.score || 0,
            writtenTestScore: evalData.total_answer.writtenScore || 0,
            weightedScore: evalData.total_answer.finalScore || evalData.study_skills?.weightedScore || 0,
            jobPosition: evalData.study_skills?.jobPosition || evalData.ms_answer_score?.jobTitle || '',
            interviewEvaluation: evalData.ms_answer_score || null,
            writtenTestEvaluation: evalData.bs_answer_score || null,
            recommendations: evalData.study_skills?.recommendations || [],
            improvementAreas: evalData.study_skills?.improvementAreas || [],
            resources: evalData.study_skills?.resources || []
          }
        } else {
          // 使用原有的数据结构
          processedData = evalData
        }

        // 检查评估数据是否有效
        const hasValidData = processedData.weightedScore !== null && processedData.weightedScore !== undefined ||
                            processedData.interviewScore !== null && processedData.interviewScore !== undefined ||
                            processedData.writtenTestScore !== null && processedData.writtenTestScore !== undefined

        if (!hasValidData) {
          console.warn('评估数据无效，所有分数字段都为null，使用默认数据')
          useDefaultEvaluationData()
          return
        }

        // 综合评分
        reportData.overallScore = Math.round(processedData.weightedScore || 75)

        // 分项评分
        reportData.scoreBreakdown = [
          {
            name: '面试表现',
            score: Math.round(processedData.interviewScore || 0),
            color: '#409EFF'
          },
          {
            name: '笔试成绩',
            score: Math.round(processedData.writtenTestScore || 0),
            color: '#67C23A'
          },
          {
            name: '综合得分',
            score: Math.round(processedData.weightedScore || 0),
            color: '#E6A23C'
          }
        ]

        // 详细评价
        reportData.detailedEvaluations = []

        // 面试评价
        if (processedData.interviewEvaluation || processedData.interviewScore) {
          const interviewEval = processedData.interviewEvaluation || {}
          const interviewScore = processedData.interviewScore || 0

          let interviewAnswerComment = '在项目追问环节中，能够围绕组件拆分、状态管理与性能优化给出结构化回答。'
          let interviewAnalysisComment = '面试表现良好，回答问题较为完整。'

          if (interviewEval.candidateResponse && interviewEval.question) {
            interviewAnswerComment = `题目：${interviewEval.question}\n\n作答：${interviewEval.candidateResponse}\n\n评分：${interviewEval.score || interviewScore}分`
            interviewAnalysisComment = `围绕核心问题给出了清晰作答，整体逻辑较完整，技术表达具备说服力。`
          } else if (interviewScore > 0) {
            interviewAnalysisComment = `面试得分：${interviewScore}分。${interviewScore >= 80 ? '表现优秀，前端工程化与项目表达能力较强。' : interviewScore >= 70 ? '表现良好，建议进一步加强复杂场景应对能力。' : '基础能力具备，仍有较大提升空间。'}`
          }

          reportData.detailedEvaluations.push({
            category: '面试答题情况',
            score: interviewScore,
            rating: Math.min(5, Math.max(1, Math.round(interviewScore / 20))),
            comment: interviewAnswerComment,
            suggestions: ['回答项目问题时先给结论，再补充技术细节', '涉及性能问题可补充指标与优化结果']
          })

          reportData.detailedEvaluations.push({
            category: '面试表现分析',
            rating: Math.min(5, Math.max(1, Math.round(interviewScore / 20))),
            comment: interviewAnalysisComment,
            suggestions: processedData.recommendations?.slice(0, 3) || ['继续保持良好的表现', '加强专业知识学习']
          })
        }

        // 笔试评价
        if (processedData.writtenTestEvaluation || processedData.writtenTestScore) {
          const writtenEval = processedData.writtenTestEvaluation || {}
          const writtenScore = processedData.writtenTestScore || 0

          let writtenAnswerComment = '在前端基础与工程化题目中整体发挥稳定，能够正确覆盖大部分关键知识点。'
          let writtenAnalysisComment = '笔试表现良好，基础知识扎实。'

          if (writtenEval.answer && typeof writtenEval.answer === 'string') {
            writtenAnswerComment = `作答反馈：${writtenEval.answer}`
          } else if (writtenScore > 0) {
            writtenAnalysisComment = `笔试得分：${writtenScore}分。${writtenScore >= 80 ? '基础扎实，知识面覆盖较全。' : writtenScore >= 70 ? '基础较好，少量细节题可继续强化。' : '建议加强基础知识与场景题训练。'}`
          }

          reportData.detailedEvaluations.push({
            category: '笔试答题情况',
            score: writtenScore,
            rating: Math.min(5, Math.max(1, writtenScore > 0 ? Math.round(writtenScore / 20) : 2)),
            comment: writtenAnswerComment,
            suggestions: ['继续加强专业知识学习', '多做练习题']
          })

          reportData.detailedEvaluations.push({
            category: '笔试表现分析',
            rating: Math.min(5, Math.max(1, writtenScore > 0 ? Math.round(writtenScore / 20) : 2)),
            comment: writtenAnalysisComment,
            suggestions: processedData.improvementAreas?.slice(0, 3) || ['继续加强专业知识学习', '多做练习题']
          })
        }

        // 综合评估和改进建议
        const overallScore = processedData.weightedScore || 0
        if (overallScore > 0) {
          let overallComment = ''
          let overallRating = Math.min(5, Math.max(1, Math.round(overallScore / 20)))

          if (overallScore >= 90) {
            overallComment = '🎉 优秀！您的综合表现非常出色，具备了该岗位所需的核心能力。'
          } else if (overallScore >= 80) {
            overallComment = '👍 良好！您的综合表现不错，在某些方面还有提升空间。'
          } else if (overallScore >= 70) {
            overallComment = '📈 中等！您具备基本能力，建议加强相关技能的学习和实践。'
          } else if (overallScore >= 60) {
            overallComment = '💪 及格！您的基础能力尚可，需要在多个方面进行提升。'
          } else {
            overallComment = '📚 需要努力！建议系统性地学习相关知识和技能。'
          }

          // 添加改进领域信息
          if (processedData.improvementAreas && processedData.improvementAreas.length > 0) {
            overallComment += `\n\n主要改进领域：${processedData.improvementAreas.join('、')}`
          }

          reportData.detailedEvaluations.push({
            category: '综合评估与建议',
            rating: overallRating,
            comment: overallComment,
            suggestions: processedData.recommendations || ['持续学习和提升', '保持积极的学习态度']
          })
        }

        // 学习资源推荐
        if (processedData.resources && processedData.resources.length > 0) {
          reportData.learningResources = processedData.resources.map(resource => ({
            title: resource.name || resource.title || '推荐学习资源',
            type: resource.type || 'article',
            url: resource.url || '#',
            description: `${getResourceTypeIcon(resource.type)}${resource.name || resource.title || '学习资源'}`
          }))
        } else {
          // 提供默认学习资源
          reportData.learningResources = [
            {
              title: '专业技能提升',
              type: 'course',
              url: 'https://www.coursera.org',
              description: '🎓 推荐通过在线课程平台提升专业技能'
            },
            {
              title: '面试技巧指南',
              type: 'article',
              url: 'https://www.zhihu.com/topic/19550517',
              description: '📄 学习更多面试技巧和经验分享'
            }
          ]
        }

      } else {
        // 没有评估数据时使用模拟数据
        console.log('没有评估数据，使用模拟数据')
        useSimulatedData(interviewReportData)
      }

      return
    }

    // 都没有则使用默认数据
    useDefaultData()

  } catch (error) {
    console.error('加载面试数据失败:', error)
    useDefaultData()
  }
}

// 使用模拟数据
const useSimulatedData = (interviewData) => {
  // reportData.overallScore = Math.floor(Math.random() * 20) + 70
  reportData.scoreBreakdown = [
    { name: '专业技能', score: Math.floor(Math.random() * 20) + 70 },
    { name: '沟通表达', score: Math.floor(Math.random() * 20) + 70 }
  ]
  reportData.detailedEvaluations = [
    {
      category: '综合表现',
      rating: 4,
      comment: '面试表现良好，具有一定的专业能力和发展潜力。',
      suggestions: ['继续努力，保持学习热情']
    }
  ]
}

// 获取资源类型图标
const getResourceTypeIcon = (type) => {
  const iconMap = {
    'book': '📚 ',
    'course': '🎓 ',
    'video': '🎬 ',
    'article': '📄 ',
    'practice': '💻 ',
    'website': '🌐 '
  }
  return iconMap[type] || '📄 '
}

// 使用默认评估数据（当API返回的评估数据无效时）
const useDefaultEvaluationData = () => {
  console.log('使用默认评估数据')

  reportData.overallScore = 75
  reportData.scoreBreakdown = [
    { name: '面试表现', score: 75 },
    { name: '笔试成绩', score: 75 },
    { name: '综合评价', score: 75 }
  ]

  reportData.detailedEvaluations = [
    {
      category: '面试表现分析',
      rating: 4,
      comment: '面试表现良好，回答问题较为完整。由于评估系统暂时无法获取详细数据，此为基础评价。',
      suggestions: ['继续保持良好的表现', '加强专业知识学习', '提升沟通表达能力']
    },
    {
      category: '笔试表现分析',
      rating: 4,
      comment: '笔试基础扎实，具备一定的专业能力。建议继续深入学习相关知识。',
      suggestions: ['加强基础知识学习', '多做练习题', '关注行业最新动态']
    },
    {
      category: '综合评估',
      rating: 4,
      comment: '🎯 综合表现良好！您展现了积极的学习态度和基本的专业素养。虽然评估系统暂时无法提供详细分析，但您的参与本身就是一个很好的开始。',
      suggestions: ['保持学习热情', '持续提升专业技能', '积极参与实践项目']
    }
  ]

  reportData.learningResources = [
    {
      title: '专业技能提升指南',
      type: 'course',
      url: 'https://www.coursera.org',
      description: '🎓 推荐通过在线课程平台提升专业技能'
    },
    {
      title: '面试技巧与经验分享',
      type: 'article',
      url: 'https://www.zhihu.com/topic/19550517',
      description: '📄 学习更多面试技巧和经验分享'
    },
    {
      title: '行业知识学习资源',
      type: 'book',
      url: '#',
      description: '📚 建议阅读相关行业的专业书籍和资料'
    }
  ]
}

const useDefaultData = () => {
  reportData.interviewDate = new Date().toLocaleString('zh-CN')
  reportData.duration = '未知'
  reportData.interviewType = '面试'
  // reportData.overallScore = 75
  reportData.scoreBreakdown = [
    { name: '专业技能', score: 75 },
    { name: '沟通表达', score: 70 }
  ]
  reportData.detailedEvaluations = [
    {
      category: '综合表现',
      rating: 4,
      comment: '面试表现良好，具有一定的专业能力和发展潜力。',
      suggestions: ['继续努力，保持学习热情']
    }
  ]
  reportData.learningResources = [
    {
      title: '通用技能提升',
      type: 'course',
      url: 'https://www.coursera.org',
      description: '🎓 推荐通过在线课程平台提升专业技能'
    },
    {
      title: '面试技巧指南',
      type: 'article',
      url: 'https://www.zhihu.com/topic/19550517',
      description: '📄 学习更多面试技巧和经验分享'
    }
  ]
}

// 组件挂载时加载数据
onMounted(() => {
  loadInterviewData()
})
</script>

<style scoped>
.interview-report {
  min-height: 100vh;
  background: #f5f6f7;
  padding: 16px;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 16px 20px;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  margin-bottom: 14px;
}

.header-content h1 {
  margin: 0 0 8px 0;
  color: #111827;
  font-size: 22px;
  font-weight: 600;
}

.interview-info {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #4b5563;
  font-size: 13px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.report-layout {
  display: grid;
  grid-template-columns: 2.2fr 1fr;
  gap: 14px;
}

.report-main,
.report-side {
  min-width: 0;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #111827;
}

.score-card,
.evaluation-card,
.resources-card,
.side-answer-card {
  margin-bottom: 12px;
}

.overall-score {
  display: flex;
  align-items: center;
  gap: 24px;
}

.score-gauge {
  width: 130px;
  height: 130px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
  box-sizing: border-box;
  flex-shrink: 0;
}

.gauge-inner {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: #fff;
  border: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.score-number {
  font-size: 30px;
  font-weight: 700;
  line-height: 1;
}

.score-label {
  font-size: 12px;
  margin-top: 4px;
}

.score-breakdown {
  flex: 1;
}

.score-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.score-name {
  width: 72px;
  font-size: 13px;
  color: #4b5563;
}

.score-bar {
  flex: 1;
  height: 8px;
  background: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
}

.score-fill {
  height: 100%;
  background: #2563eb;
}

.score-value {
  width: 36px;
  text-align: right;
  font-weight: 600;
  color: #303133;
}

.evaluation-item {
  margin-bottom: 12px;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.eval-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.eval-category {
  font-weight: 600;
  color: #111827;
  font-size: 15px;
}

.eval-comment {
  color: #374151;
  line-height: 1.6;
  margin-bottom: 10px;
  white-space: pre-wrap;
}

.eval-suggestions ul {
  margin: 6px 0 0;
  padding-left: 20px;
}

.eval-suggestions li {
  margin: 3px 0;
  color: #4b5563;
  font-size: 13px;
}

.resources-list {
  max-height: none;
}

.resources-list.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.resource-item {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 10px;
  background: #fff;
}

.resource-item:last-child {
  border-bottom: 1px solid #e5e7eb;
}

.resource-name {
  margin: 0 0 6px 0;
  color: #111827;
  font-size: 14px;
  font-weight: 600;
}

.resource-meta-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.resource-description {
  color: #4b5563;
  line-height: 1.5;
  margin: 0;
  font-size: 13px;
}

.answer-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.answer-score {
  font-size: 16px;
  font-weight: 700;
  color: #1f2937;
}

.answer-text {
  color: #4b5563;
  line-height: 1.6;
  font-size: 13px;
  white-space: pre-wrap;
}

@media (max-width: 1024px) {
  .report-layout {
    grid-template-columns: 1fr;
  }

  .resources-list.grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .interview-report {
    padding: 10px;
  }

  .report-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .interview-info {
    justify-content: center;
  }

  .overall-score {
    flex-direction: column;
    gap: 16px;
  }

  .resources-list.grid {
    grid-template-columns: 1fr;
  }
}
</style>
