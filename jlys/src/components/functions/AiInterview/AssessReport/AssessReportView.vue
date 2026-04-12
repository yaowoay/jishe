<script>
import { ref, onMounted } from 'vue'
import AnalysisModal from './AnalysisModal.vue'
import AnalysisSection from './AnalysisSection.vue'
import PositionRecommendation from './PositionRecommendation.vue'
import { useRouter } from 'vue-router'

export default {
  name: 'AssessReportView',
  components: {
    AnalysisModal,
    AnalysisSection,
    PositionRecommendation
  },
  setup() {
    const router = useRouter()

    const handleCourseClick = (courseName) => {
      const encodedCourseName = encodeURIComponent(courseName)
      router.push(`/courses/${encodedCourseName}`)
    }

    const interviewData = ref({
      position: '运维工程师',
      interviewer: '',
      mode: '',
      duration: '',
      overallScore: 0,
      overallComment: '候选人在前端技术方面表现良好，具备基础的架构能力和较强的学习能力。',
      professionalAbilities: {
        indicators: ['专业技术水平', '系统架构能力', '应用能力', '沟通协作能力', '动手能力', '学习能力', '执行力'],
        values: [80, 75, 85, 70, 90, 80, 85]
      },
      professionalAbilitiesDetailed: [
        {
          name: '专业技术水平，系统架构能力',
          details: `
            <div class="analysis-details">
              <h3>专业技术水平，系统架构能力分析</h3>
              <table class="analysis-table">
                <thead>
                  <tr>
                    <th>类</th>
                    <th>一般</th>
                    <th>良好</th>
                    <th>优秀</th>
                    <th>卓越</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>技术水平</td>
                    <td>60-70分</td>
                    <td>70-80分</td>
                    <td>80-90分</td>
                    <td>90+分</td>
                  </tr>
                  <tr>
                    <td>架构能力</td>
                    <td>基础理解</td>
                    <td>简单设计</td>
                    <td>复杂系统</td>
                    <td>架构创新</td>
                  </tr>
                </tbody>
              </table>
              <div class="analysis-summary">
                <p><strong>您的得分：80分（良好）</strong></p>
                <p>建议：加强大型系统设计经验，学习微服务架构</p>
              </div>
            </div>
          `
        },
        {
          name: '应用能力，沟通协作能力',
          details: `
            <div class="analysis-details">
              <h3>应用能力，沟通协作能力分析</h3>
              <table class="analysis-table">
                <thead>
                  <tr>
                    <th>类</th>
                    <th>一般</th>
                    <th>良好</th>
                    <th>优秀</th>
                    <th>卓越</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>应用能力</td>
                    <td>基础应用</td>
                    <td>熟练应用</td>
                    <td>创新应用</td>
                    <td>领导应用</td>
                  </tr>
                  <tr>
                    <td>沟通协作</td>
                    <td>基本沟通</td>
                    <td>团队协作</td>
                    <td>高效协作</td>
                    <td>领导协作</td>
                  </tr>
                </tbody>
              </table>
              <div class="analysis-summary">
                <p><strong>您的得分：85分（优秀）</strong></p>
                <p>建议：进一步提升跨团队协作能力</p>
              </div>
            </div>
          `
        },
        {
          name: '动手能力，学习能力',
          details: `
            <div class="analysis-details">
              <h3>动手能力，学习能力分析</h3>
              <table class="analysis-table">
                <thead>
                  <tr>
                    <th>类</th>
                    <th>一般</th>
                    <th>良好</th>
                    <th>优秀</th>
                    <th>卓越</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>动手能力</td>
                    <td>基础实践</td>
                    <td>熟练操作</td>
                    <td>高效实现</td>
                    <td>创新实践</td>
                  </tr>
                  <tr>
                    <td>学习能力</td>
                    <td>被动学习</td>
                    <td>主动学习</td>
                    <td>快速掌握</td>
                    <td>创新学习</td>
                  </tr>
                </tbody>
              </table>
              <div class="analysis-summary">
                <p><strong>您的得分：90分（卓越）</strong></p>
                <p>建议：保持学习热情，分享学习经验</p>
              </div>
            </div>
          `
        },
        {
          name: '执行力',
          details: `
            <div class="analysis-details">
              <h3>执行力分析</h3>
              <table class="analysis-table">
                <thead>
                  <tr>
                    <th>类</th>
                    <th>一般</th>
                    <th>良好</th>
                    <th>优秀</th>
                    <th>卓越</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>任务完成</td>
                    <td>基本完成</td>
                    <td>按时完成</td>
                    <td>超额完成</td>
                    <td>创新完成</td>
                  </tr>
                  <tr>
                    <td>执行效率</td>
                    <td>一般效率</td>
                    <td>良好效率</td>
                    <td>高效执行</td>
                    <td>卓越效率</td>
                  </tr>
                </tbody>
              </table>
              <div class="analysis-summary">
                <p><strong>您的得分：85分（优秀）</strong></p>
                <p>建议：进一步提升复杂任务执行能力</p>
              </div>
            </div>
          `
        }
      ],
      stateAnalysis: {
        indicators: ['应答能力', '谈吐措辞', '表达性', '表达能力', '稳重性向'],
        values: [85, 75, 80, 80, 70]
      },
      innovationScore: 85,
      stateAnalysisDetailed: [
        {
          name: '应答能力',
          details: `
            <div class="analysis-details">
              <h3>应答能力分析</h3>
              <table class="analysis-table">
                <thead>
                  <tr>
                    <th>类</th>
                    <th>一般</th>
                    <th>良好</th>
                    <th>优秀</th>
                    <th>卓越</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>反应速度</td>
                    <td>较慢</td>
                    <td>一般</td>
                    <td>较快</td>
                    <td>极快</td>
                  </tr>
                  <tr>
                    <td>回答质量</td>
                    <td>基本回答</td>
                    <td>完整回答</td>
                    <td>深入回答</td>
                    <td>创新回答</td>
                  </tr>
                </tbody>
              </table>
              <div class="analysis-summary">
                <p><strong>您的得分：85分（优秀）</strong></p>
                <p>建议：进一步提升复杂问题应答能力</p>
              </div>
            </div>
          `
        },
        {
          name: '谈吐措辞，表达性',
          details: `
            <div class="analysis-details">
              <h3>谈吐措辞，表达性分析</h3>
              <table class="analysis-table">
                <thead>
                  <tr>
                    <th>类</th>
                    <th>一般</th>
                    <th>良好</th>
                    <th>优秀</th>
                    <th>卓越</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>语言表达</td>
                    <td>基本表达</td>
                    <td>清晰表达</td>
                    <td>流畅表达</td>
                    <td>优雅表达</td>
                  </tr>
                  <tr>
                    <td>专业术语</td>
                    <td>少量使用</td>
                    <td>适当使用</td>
                    <td>熟练使用</td>
                    <td>精准使用</td>
                  </tr>
                </tbody>
              </table>
              <div class="analysis-summary">
                <p><strong>您的得分：75分（良好）</strong></p>
                <p>建议：加强专业术语的使用和表达流畅度</p>
              </div>
            </div>
          `
        },
        {
          name: '表达能力',
          details: `
            <div class="analysis-details">
              <h3>表达能力分析</h3>
              <table class="analysis-table">
                <thead>
                  <tr>
                    <th>类</th>
                    <th>一般</th>
                    <th>良好</th>
                    <th>优秀</th>
                    <th>卓越</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>逻辑性</td>
                    <td>基本逻辑</td>
                    <td>清晰逻辑</td>
                    <td>严密逻辑</td>
                    <td>创新逻辑</td>
                  </tr>
                  <tr>
                    <td>感染力</td>
                    <td>一般</td>
                    <td>较好</td>
                    <td>强烈</td>
                    <td>极强</td>
                  </tr>
                </tbody>
              </table>
              <div class="analysis-summary">
                <p><strong>您的得分：80分（良好）</strong></p>
                <p>建议：加强表达的逻辑性和感染力</p>
              </div>
            </div>
          `
        },
        {
          name: '稳重性向',
          details: `
            <div class="analysis-details">
              <h3>稳重性向分析</h3>
              <table class="analysis-table">
                <thead>
                  <tr>
                    <th>类</th>
                    <th>一般</th>
                    <th>良好</th>
                    <th>优秀</th>
                    <th>卓越</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>情绪控制</td>
                    <td>基本控制</td>
                    <td>较好控制</td>
                    <td>优秀控制</td>
                    <td>完美控制</td>
                  </tr>
                  <tr>
                    <td>应变能力</td>
                    <td>基本应变</td>
                    <td>较好应变</td>
                    <td>优秀应变</td>
                    <td>完美应变</td>
                  </tr>
                </tbody>
              </table>
              <div class="analysis-summary">
                <p><strong>您的得分：70分（一般）</strong></p>
                <p>建议：加强情绪管理和应变能力训练</p>
              </div>
            </div>
          `
        }
      ],
      recommendations: [
        {
          position: '运维工程师',
          match: '高度匹配',
          score: 93,
          tags: ['Linux', '自动化运维', '脚本编程', '监控告警', '云平台', '故障排查'],
          reason: '候选人在Linux系统管理、自动化脚本、监控与告警、云平台运维等方面表现突出，具备良好的问题定位和应急响应能力，能够胜任运维工程师岗位。'
        },
        {
          position: '云平台运维工程师',
          match: '较高匹配',
          score: 87,
          tags: ['云计算', 'Kubernetes', '自动化部署', '安全运维'],
          reason: '具备云平台（如AWS、阿里云、K8s）运维经验，适合进一步发展为云平台运维工程师。'
        },
        {
          position: '自动化运维开发工程师',
          match: '可胜任',
          score: 80,
          tags: ['Python', 'CI/CD', '自动化工具', 'DevOps'],
          reason: '有一定自动化开发和CI/CD实践基础，建议加强DevOps理念和自动化工具链学习。'
        }
      ],
      professionalWeight: 40,
      stateWeight: 30,
      innovationWeight: 30,
      expertAdvice: {
        language: `提升专业术语表达能力，增强技术沟通的准确性和流畅度。建议在面试和实际工作中多用“自动化运维”“故障排查”“监控告警”“云平台管理”等运维核心术语，表达时注意条理清晰，善用STAR法则阐述项目经历。

此外，建议多用实际案例和数据支撑观点，遇到追问时保持自信，合理运用图表、流程图等辅助表达，提升技术影响力。面试中可适当展示对新技术（如Kubernetes、DevOps、云原生等）的关注，体现技术前瞻性。`,
        courses: [
          { name: 'Linux系统管理', source: '慕课网《Linux运维实战》' },
          { name: '自动化运维与脚本编程', source: 'B站UP主“Python自动化运维”/极客时间《自动化运维》' },
          { name: '云平台与Kubernetes', source: '极客时间《Kubernetes进阶实战》/阿里云官方文档' },
          { name: '监控与告警体系', source: 'B站UP主“Prometheus监控”/Grafana官网' },
          { name: 'CI/CD与DevOps实践', source: '极客时间《DevOps实战》/Jenkins官方文档' },
          { name: '安全运维基础', source: '腾讯课堂《安全运维》/安恒信息公开课' },
          { name: '技术沟通与表达', source: '得到App《高效沟通》/TED演讲' },
          { name: '团队协作与应急响应', source: 'Coursera《IT支持与应急响应》' }
        ],
        plan: `建议制定系统化的技能提升计划，结合岗位推荐和多模态能力要求，分阶段、分主题持续成长：

1. 每周：
  - 聚焦一个主题（如自动化脚本、监控体系、云平台管理、故障应急等），通过查阅资料、动手实践、总结笔记，形成知识闭环。
  - 参与团队例会或技术分享，主动表达观点，锻炼沟通与表达能力。
  - 关注行业动态，记录新技术、新工具的学习心得。
2. 每月：
  - 至少完成一次技术分享、复盘或小型项目实践，梳理学习成果，发现不足并及时调整学习方向。
  - 参与开源项目或技术社区，提升自动化工具开发、团队协作和技术影响力。
  - 针对岗位推荐补齐短板（如云平台、DevOps、安全运维等）。
3. 每季度：
  - 制定并回顾个人成长目标，评估阶段性成果，调整学习重点。
  - 参与大型项目或跨部门协作，提升系统架构能力和创新能力。
  - 关注技术前瞻性，主动学习Kubernetes、云原生、自动化运维等新兴技术。
4. 年度：
  - 总结全年学习与成长，形成个人技术成长档案。
  - 规划下一年度的学习路径，设定更高目标。

补充建议：
- 多用实际案例和数据支撑观点，遇到追问时保持自信，合理运用图表、流程图等辅助表达，提升表达感染力。
- 结合岗位推荐，主动承担团队沟通、技术分享、创新实践等任务，提升综合竞争力。`
      }
    })

    const avatarUrl = ref('/images/1.jpg')
    const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgd2lkdGg9IjI0IiBoZWlnaHQ9IjI0Ij48cGF0aCBmaWxsPSJub25lIiBkPSJNMCAwaDI0djI0SDB6Ii8+PHBhdGggZD0iTTQgMjJDNCAxNy41MjMgNy41MjMgMTQgMTIgMTRDMTYuNDc3IDE0IDIwIDE3LjUyMyAyMCAyMkgyNFYxMkMyNCAxMS41IDIzLjUgMTEgMjMgMTFIMUMwLjUgMTEgMCAxMS41IDAgMTJWMjJINFpNMTIgMTNDMTQuNzYgMTMgMTcgMTAuNzYgMTcgOEMxNyA1LjI0IDE0Ljc2IDMgMTIgM0M5LjI0IDMgNyA1LjI0IDcgOEM3IDEwLjc2IDkuMjQgMTMgMTIgMTNaIiBmaWxsPSIjNjU3Nzg2Ii8+PC9zdmc+'

    const modalVisible = ref(false)
    const currentDetailData = ref({})

    const showAnalysis = (data) => {
      const items = data.type === 'professional'
        ? interviewData.value.professionalAbilitiesDetailed
        : interviewData.value.stateAnalysisDetailed

      const item = items[data.id]
      if (item) {
        currentDetailData.value = {
          title: item.name,
          details: item.details
        }
        modalVisible.value = true
      }
    }

    const handleModalConfirm = () => {
      console.log('模态框确认按钮点击')
    }

    const navigateToPositionDetail = () => {
      router.push({ name: 'PositionDetail' })
    }


    onMounted(async () => {
      try {
        // 计算专业能力平均分
        if (interviewData.value.professionalAbilities && interviewData.value.professionalAbilities.values) {
          const sum = interviewData.value.professionalAbilities.values.reduce((acc, val) => acc + val, 0)
          interviewData.value.professionalAbilitiesAvg = Math.round(sum / interviewData.value.professionalAbilities.values.length)
        }

        // 计算状态分析平均分
        if (interviewData.value.stateAnalysis && interviewData.value.stateAnalysis.values) {
          const sum = interviewData.value.stateAnalysis.values.reduce((acc, val) => acc + val, 0)
          interviewData.value.stateAnalysisAvg = Math.round(sum / interviewData.value.stateAnalysis.values.length)
        }

        // 设置权重
        interviewData.value.professionalWeight = 40
        interviewData.value.stateWeight = 30
        interviewData.value.innovationWeight = 30
        interviewData.value.recommendedPosition = '前端开发工程师'

        // 计算总分
        const p = interviewData.value.professionalAbilitiesAvg || 0
        const s = interviewData.value.stateAnalysisAvg || 0
        const i = interviewData.value.innovationScore || 0
        const total = Math.round(p * 0.4 + s * 0.3 + i * 0.3)
        interviewData.value.overallScore = total
      } catch (error) {
        console.error('初始化数据失败', error)
      }
    })

    return {
      interviewData,
      showAnalysis,
      modalVisible,
      currentDetailData,
      handleCourseClick,
      handleModalConfirm,
      avatarUrl,
      defaultAvatar,
      navigateToPositionDetail
    }
  }
}
</script>

<template>
  <div class="app-background">
    <div class="decoration-circle circle-1"></div>
    <div class="decoration-circle circle-2"></div>
    <div class="decoration-circle circle-3"></div>
    <div class="decoration-dots"></div>

    <div class="container">
      <h1 class="main-title">面试结束后，生成评估报告</h1>

      <div class="report-card">
        <div class="card-header">
          <span class="icon-blue"></span>
          <span class="title">AI面试报告</span>

        </div>

        <div class="info-section">
          <div class="info-row">
            <div class="info-item">
              <span class="label">面试岗位：运维工程师</span>
            </div>
            <div class="info-item">
              <span class="label">面试人：章刚</span>
              <span class="value">{{ interviewData.interviewer }}</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-item">
              <span class="label">面试模式：模拟面试</span>
              <span class="value">{{ interviewData.mode }}</span>
            </div>
            <div class="info-item">
              <span class="label">面试时长：21分钟</span>
              <span class="value">{{ interviewData.duration }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="report-card">
        <div class="card-header">
          <span class="icon-blue"></span>
          <span class="title">总体结果</span>
        </div>

        <div class="overall-result-section">
          <div class="big-score-container">
            <div class="big-score">{{ interviewData.overallScore }}分</div>
          </div>
          <div class="overall-details">
            <div class="detail-item">
              <div class="detail-box">
                <span class="detail-title">专业能力：{{ interviewData.professionalAbilitiesAvg }}分</span>
              </div>
              <span class="detail-percent">{{ interviewData.professionalWeight }}%</span>
            </div>
            <div class="detail-item">
              <div class="detail-box">
                <span class="detail-title">软技能：{{ interviewData.stateAnalysisAvg }}分</span>
              </div>
              <span class="detail-percent">{{ interviewData.stateWeight }}%</span>
            </div>
            <div class="detail-item">
              <div class="detail-box">
                <span class="detail-title">创新能力：{{ interviewData.innovationScore }}分</span>
              </div>
              <span class="detail-percent">{{ interviewData.innovationWeight }}%</span>
            </div>
          </div>
        </div>
      </div>

      <div class="report-card">
        <div class="card-header">
          <span class="icon-blue"></span>
          <span class="title">专业能力解析</span>
          <span class="weight-tag" v-if="interviewData.professionalWeight">占比 {{ interviewData.professionalWeight }}%</span>
        </div>

        <AnalysisSection
            :chartData="interviewData.professionalAbilities"
            chartId="professional-radar"
            :detailedItems="interviewData.professionalAbilitiesDetailed"
            analysisType="professional"
            @show-detail="showAnalysis"
        />
      </div>

      <div class="report-card">
        <div class="card-header">
          <span class="icon-blue"></span>
          <span class="title">状态解析</span>
          <span class="weight-tag" v-if="interviewData.stateWeight">占比 {{ interviewData.stateWeight }}%</span>
        </div>

        <AnalysisSection
            :chartData="interviewData.stateAnalysis"
            chartId="state-radar"
            :detailedItems="interviewData.stateAnalysisDetailed"
            analysisType="state"
            @show-detail="showAnalysis"
        />
      </div>

      <div class="report-card">
        <div class="card-header">
          <span class="icon-blue"></span>
          <span class="title" style="cursor: pointer;" @click="navigateToPositionDetail">岗位匹配度推荐：</span>
        </div>

        <PositionRecommendation :recommendations="interviewData.recommendations" />
      </div>

      <div class="report-card">
        <div class="card-header">
          <span class="icon-blue"></span>
          <span class="title">AI专家建议：</span>
        </div>

        <div class="expert-advice-section">
          <div class="advice-item">
            <div class="advice-label" style="font-size:18px;font-weight:bold;margin-bottom:8px;color:#1E90FF;">语言表达建议：</div>
            <div class="advice-content" style="font-size:16px;line-height:2.1;margin-bottom:18px;">{{ interviewData.expertAdvice.language }}</div>
          </div>

          <div class="advice-item">
            <div class="advice-label" style="font-size:18px;font-weight:bold;margin-bottom:8px;color:#1E90FF;">推荐的课程：</div>
            <div class="course-list" style="gap:18px 18px;flex-wrap:wrap;margin-bottom:18px;">
              <div
                  v-for="(course, index) in interviewData.expertAdvice.courses"
                  :key="index"
                  class="course-item"
                  style="font-size:15px;padding:12px 22px;margin-bottom:10px;display:flex;flex-direction:column;align-items:center;"
                  @click="handleCourseClick(course.name)"
              >
                <span>{{ course.name }}</span>
                <span style="font-size:12px;color:#888;margin-top:4px;">{{ course.source }}</span>
              </div>
            </div>
          </div>

          <div class="advice-item">
            <div class="advice-label" style="font-size:18px;font-weight:bold;margin-bottom:8px;color:#1E90FF;">学习计划：</div>
            <div class="advice-content" style="font-size:16px;line-height:2.1;">
              <ul style="padding-left:22px;margin:0;">
                <li v-for="(line, idx) in interviewData.expertAdvice.plan.split(/\n[0-9]\\. /).filter(Boolean)" :key="idx" style="margin-bottom:10px;">
                  <span v-if="idx > 0" style="font-weight:bold;color:#409EFF;">{{ idx }}.</span>
                  <span v-html="line.replace(/^\d+\.\s*/, '')"></span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <AnalysisModal
          v-model="modalVisible"
          :detailData="currentDetailData"
          @confirm="handleModalConfirm"
      />
    </div>
  </div>
</template>

<style>
.app-background {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4ecf7 100%);
  background-image:
      radial-gradient(circle at 25px 25px, rgba(30, 144, 255, 0.05) 2%, transparent 0%),
      radial-gradient(circle at 75px 75px, rgba(30, 144, 255, 0.05) 2%, transparent 0%);
  background-size: 100px 100px;
  padding: 20px 0;
  position: relative;
  overflow-x: hidden;
  overflow-y: auto;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  background: radial-gradient(circle, rgba(30, 144, 255, 0.8) 0%, rgba(30, 144, 255, 0.1) 70%);
  z-index: 0;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  right: -50px;
}

.circle-2 {
  width: 250px;
  height: 250px;
  bottom: -80px;
  left: -80px;
}

.circle-3 {
  width: 180px;
  height: 180px;
  top: 40%;
  right: 10%;
}

.decoration-dots {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  background-image: radial-gradient(rgba(30, 144, 255, 0.2) 1px, transparent 1px);
  background-size: 20px 20px;
  opacity: 0.1;
  z-index: 0;
}

.app-background::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 200px;
  background: linear-gradient(180deg, rgba(30, 144, 255, 0.15) 0%, rgba(30, 144, 255, 0) 100%);
  z-index: 0;
}

.container {
  position: relative;
  z-index: 1;
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.main-title {
  text-align: center;
  margin-bottom: 25px;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
  position: relative;
  z-index: 1;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.1);
}

.report-card {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05), 0 3px 6px rgba(0, 0, 0, 0.03);
  margin-bottom: 20px;
  padding: 15px;
  transition: all 0.3s ease;
  border: 1px solid rgba(30, 144, 255, 0.1);
}

.report-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08), 0 4px 8px rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  align-items: center;
  position: relative;
  margin-bottom: 15px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.icon-blue {
  width: 6px;
  height: 18px;
  background-color: #1E90FF;
  border-radius: 3px;
  margin-right: 10px;
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: #444;
}

.weight-tag {
  margin-left: 8px;
  background-color: #f0f8ff;
  color: #1E90FF;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: normal;
}

.avatar-container {
  position: absolute;
  right: 10px;
  top: 5px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-image {
  width: 55px;
  height: 55px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f5f5f5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.avatar-label {
  margin-top: 5px;
  font-size: 12px;
  color: #666;
}

.info-section {
  padding: 5px 0;
}

.info-row {
  display: flex;
  margin-bottom: 10px;
}

.info-item {
  flex: 1;
  display: flex;
}

.label {
  color: #666;
  margin-right: 5px;
}

.overall-result-section {
  display: flex;
  padding: 20px 10px;
  align-items: flex-start;
}

.big-score-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-right: 40px;
  min-width: 150px;
}

.big-score {
  font-size: 80px;
  font-weight: bold;
  color: #333;
  line-height: 1;
}

.overall-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.detail-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.detail-box {
  border: 2px solid #333;
  border-radius: 0;
  padding: 8px 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  flex: 1;
}

.detail-title {
  font-weight: bold;
  color: #333;
  font-size: 16px;
}

.detail-percent {
  color: #333;
  font-weight: bold;
  font-size: 16px;
  margin-left: 15px;
  min-width: 50px;
  text-align: right;
}

.radar-section {
  display: flex;
  padding: 10px;
}

.radar-chart {
  width: 350px;
  height: 250px;
}

.ability-list {
  flex: 1;
  margin-left: 20px;
}

.ability-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.ability-name {
  color: #333;
}

.analysis-btn {
  background-color: #f0f0f0;
  color: #333;
  padding: 5px 15px;
  border-radius: 15px;
  cursor: pointer;
  font-size: 14px;
}

.recommendation-section {
  padding: 20px;
  min-height: 100px;
}

.recommendation-item {
  margin-bottom: 12px;
}

.recommendation-type {
  font-weight: bold;
  color: #333;
}

.recommendation-content {
  color: #1E90FF;
}

.expert-advice-section {
  padding: 15px;
}

.advice-item {
  margin-bottom: 20px;
}

.advice-label {
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.advice-content {
  color: #555;
  line-height: 1.5;
}

.course-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 5px;
}

.course-item {
  background-color: #f0f0f0;
  padding: 8px 15px;
  border-radius: 5px;
  color: #333;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-right: 10px;
  margin-bottom: 10px;
  display: inline-block;
}

.course-item:hover {
  background-color: #1E90FF;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.analysis-table {
  width: 100%;
  border-collapse: collapse;
  margin: 15px 0;
}

.analysis-table th,
.analysis-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: center;
}

.analysis-table th {
  background-color: #f2f2f2;
}

.analysis-summary {
  margin-top: 15px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.analysis-summary p {
  margin: 5px 0;
}

@media (prefers-color-scheme: dark) {
  .app-background {
    background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
  }

  .main-title {
    color: #f0f0f0;
  }

  .report-card {
    background-color: #333;
    border-color: #444;
  }

  .title {
    color: #f0f0f0;
  }

  .label {
    color: #bbb;
  }

  .value {
    color: #fff;
  }

  .big-score {
    color: #fff;
  }

  .detail-box {
    background-color: #444;
    border-color: #666;
  }

  .detail-title {
    color: #fff;
  }

  .detail-percent {
    color: #fff;
  }

  .weight-tag {
    background-color: #444;
    color: #1E90FF;
  }

  .advice-label {
    color: #fff;
  }

  .advice-content {
    color: #ccc;
  }

  .course-item {
    background-color: #444;
    color: #ddd;
  }

  .avatar-image {
    border-color: #444;
  }

  .analysis-table th {
    background-color: #444;
  }

  .analysis-summary {
    background-color: #333;
  }
}
</style>