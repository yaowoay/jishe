<template>
  <div class="assess-report-v2">
    <!-- 报告头部 -->
    <div class="report-header">
      <div class="header-content">
        <div class="candidate-info">
          <div class="avatar-section">
            <img :src="reportData.candidateInfo.avatar" alt="候选人头像" class="candidate-avatar">
            <div class="candidate-basic">
              <h2 class="candidate-name">{{ reportData.candidateInfo.name }}</h2>
              <p class="candidate-position">应聘：{{ reportData.candidateInfo.targetPosition }}</p>
            </div>
          </div>
          <div class="interview-meta">
            <div class="meta-item">
              <span class="meta-label">面试时间</span>
              <span class="meta-value">{{ reportData.interviewInfo.date }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">面试时长</span>
              <span class="meta-value">{{ reportData.interviewInfo.duration }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">面试轮次</span>
              <span class="meta-value">{{ reportData.interviewInfo.interviewer }}</span>
            </div>
          </div>
        </div>
        <div class="overall-score-section">
          <div class="score-circle">
            <div class="score-value">{{ reportData.overallScore }}</div>
            <div class="score-label">综合得分</div>
          </div>
          <div class="score-level" :class="getScoreLevel(reportData.overallScore)">
            {{ getScoreLevelText(reportData.overallScore) }}
          </div>
        </div>
      </div>
    </div>

    <!-- 技能评估雷达图 -->
    <div class="report-section">
      <div class="section-header">
        <h3 class="section-title">
          <i class="icon-radar"></i>
          技能评估分析
        </h3>
        <p class="section-desc">基于面试表现的多维度技能评估</p>
      </div>
      <div class="skills-radar-container">
        <div class="radar-chart-wrapper">
          <div ref="skillsRadarChart" class="radar-chart"></div>
        </div>
        <div class="skills-breakdown">
          <div 
            v-for="(skill, index) in reportData.skillsAssessment" 
            :key="index"
            class="skill-item"
          >
            <div class="skill-header">
              <span class="skill-name">{{ skill.name }}</span>
              <span class="skill-score">{{ skill.score }}分</span>
            </div>
            <div class="skill-progress">
              <div 
                class="progress-bar" 
                :style="{ width: skill.score + '%', backgroundColor: getSkillColor(skill.score) }"
              ></div>
            </div>
            <p class="skill-comment">{{ skill.comment }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 项目匹配度分析 -->
    <div class="report-section">
      <div class="section-header">
        <h3 class="section-title">
          <i class="icon-project"></i>
          项目匹配度分析
        </h3>
        <p class="section-desc">基于项目经验与目标岗位的匹配程度</p>
      </div>
      <div class="project-analysis">
        <div class="match-overview">
          <div class="match-chart-container">
            <div ref="projectMatchChart" class="match-chart"></div>
          </div>
          <div class="match-summary">
            <div class="match-score-big">{{ reportData.projectMatch.overallMatch }}%</div>
            <div class="match-label">整体匹配度</div>
            <div class="match-level-badge" :class="getMatchLevel(reportData.projectMatch.overallMatch)">
              {{ getMatchLevelText(reportData.projectMatch.overallMatch) }}
            </div>
          </div>
        </div>
        <div class="project-details">
          <div 
            v-for="(project, index) in reportData.projectMatch.projects" 
            :key="index"
            class="project-card"
          >
            <div class="project-header">
              <h4 class="project-name">{{ project.name }}</h4>
              <div class="project-match-score">{{ project.matchScore }}%</div>
            </div>
            <div class="project-tags">
              <span 
                v-for="(tech, i) in project.technologies" 
                :key="i"
                class="tech-tag"
              >
                {{ tech }}
              </span>
            </div>
            <p class="project-description">{{ project.description }}</p>
            <div class="project-highlights">
              <div 
                v-for="(highlight, i) in project.highlights" 
                :key="i"
                class="highlight-item"
              >
                <i class="highlight-icon">✓</i>
                <span>{{ highlight }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 改进建议 -->
    <div class="report-section">
      <div class="section-header">
        <h3 class="section-title">
          <i class="icon-improve"></i>
          个性化改进建议
        </h3>
        <p class="section-desc">基于评估结果的针对性提升方案</p>
      </div>
      <div class="improvement-suggestions">
        <div class="suggestions-overview">
          <div class="priority-chart-container">
            <div ref="priorityChart" class="priority-chart"></div>
          </div>
          <div class="priority-legend">
            <div class="legend-item high">
              <span class="legend-color"></span>
              <span>高优先级 ({{ reportData.improvements.high.length }}项)</span>
            </div>
            <div class="legend-item medium">
              <span class="legend-color"></span>
              <span>中优先级 ({{ reportData.improvements.medium.length }}项)</span>
            </div>
            <div class="legend-item low">
              <span class="legend-color"></span>
              <span>低优先级 ({{ reportData.improvements.low.length }}项)</span>
            </div>
          </div>
        </div>
        <div class="suggestions-details">
          <div class="priority-section" v-for="(priority, key) in reportData.improvements" :key="key">
            <h4 class="priority-title" :class="key">
              {{ getPriorityTitle(key) }}
            </h4>
            <div class="suggestions-list">
              <div 
                v-for="(suggestion, index) in priority" 
                :key="index"
                class="suggestion-card"
                :class="key"
              >
                <div class="suggestion-header">
                  <h5 class="suggestion-title">{{ suggestion.title }}</h5>
                  <span class="suggestion-impact">{{ suggestion.impact }}</span>
                </div>
                <p class="suggestion-description">{{ suggestion.description }}</p>
                <div class="suggestion-actions">
                  <div class="action-timeline">
                    <i class="timeline-icon">⏱</i>
                    <span>{{ suggestion.timeline }}</span>
                  </div>
                  <div class="action-difficulty">
                    <i class="difficulty-icon">📊</i>
                    <span>{{ suggestion.difficulty }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 资源推荐分析 -->
    <div class="report-section">
      <div class="section-header">
        <h3 class="section-title">
          <i class="icon-resources"></i>
          智能资源推荐
        </h3>
        <p class="section-desc">基于技能缺口的精准学习资源推荐</p>
      </div>
      <div class="resources-recommendation">
        <div class="resources-categories">
          <div 
            v-for="(category, index) in reportData.resources" 
            :key="index"
            class="resource-category"
          >
            <div class="category-header">
              <div class="category-icon" :style="{ backgroundColor: category.color }">
                <i :class="category.icon"></i>
              </div>
              <div class="category-info">
                <h4 class="category-name">{{ category.name }}</h4>
                <p class="category-desc">{{ category.description }}</p>
              </div>
              <div class="category-count">{{ category.items.length }}个资源</div>
            </div>
            <div class="category-items">
              <div 
                v-for="(item, i) in category.items" 
                :key="i"
                class="resource-item"
                @click="openResource(item)"
              >
                <div class="resource-thumbnail">
                  <img :src="item.thumbnail" :alt="item.title" class="resource-image">
                  <div class="resource-type">{{ item.type }}</div>
                </div>
                <div class="resource-content">
                  <h5 class="resource-title">{{ item.title }}</h5>
                  <p class="resource-provider">{{ item.provider }}</p>
                  <div class="resource-meta">
                    <span class="resource-duration">{{ item.duration }}</span>
                    <span class="resource-level">{{ item.level }}</span>
                    <span class="resource-rating">⭐ {{ item.rating }}</span>
                  </div>
                  <div class="resource-tags">
                    <span 
                      v-for="(tag, t) in item.tags" 
                      :key="t"
                      class="resource-tag"
                    >
                      {{ tag }}
                    </span>
                  </div>
                </div>
                <div class="resource-action">
                  <button class="btn-start-learning">开始学习</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 学习路径规划 -->
    <div class="report-section">
      <div class="section-header">
        <h3 class="section-title">
          <i class="icon-roadmap"></i>
          个性化学习路径
        </h3>
        <p class="section-desc">基于当前技能水平的阶段性学习规划</p>
      </div>
      <div class="learning-roadmap">
        <div class="roadmap-timeline">
          <div 
            v-for="(phase, index) in reportData.learningPath" 
            :key="index"
            class="timeline-phase"
            :class="{ active: index === 0, completed: false }"
          >
            <div class="phase-marker">
              <span class="phase-number">{{ index + 1 }}</span>
            </div>
            <div class="phase-content">
              <h4 class="phase-title">{{ phase.title }}</h4>
              <p class="phase-duration">{{ phase.duration }}</p>
              <div class="phase-goals">
                <div 
                  v-for="(goal, g) in phase.goals" 
                  :key="g"
                  class="goal-item"
                >
                  <i class="goal-icon">🎯</i>
                  <span>{{ goal }}</span>
                </div>
              </div>
              <div class="phase-resources">
                <span 
                  v-for="(resource, r) in phase.resources" 
                  :key="r"
                  class="phase-resource-tag"
                  @click="openResource(resource)"
                >
                  {{ resource.name }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'

export default {
  name: 'AssessReportView2',
  setup() {
    const router = useRouter()
    const skillsRadarChart = ref(null)
    const projectMatchChart = ref(null)
    const priorityChart = ref(null)

    // 模拟数据
    const reportData = ref({
      candidateInfo: {
        name: '李伟',
        /* avatar: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgd2lkdGg9IjgwIiBoZWlnaHQ9IjgwIj48Y2lyY2xlIGN4PSIxMiIgY3k9IjEyIiByPSIxMiIgZmlsbD0iIzY2N2VlYSIvPjx0ZXh0IHg9IjEyIiB5PSIxNiIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZmlsbD0id2hpdGUiIGZvbnQtc2l6ZT0iMTAiIGZvbnQtZmFtaWx5PSJBcmlhbCI+5byg5LiJPC90ZXh0Pjwvc3ZnPg==',*/
        targetPosition: '前端开发工程师'
      },
      interviewInfo: {
        date: '2025-08-05 14:30',
        duration: '25分钟',
        interviewer: '6轮'
      },
      overallScore: 85,
      skillsAssessment: [
        { name: 'JavaScript基础', score: 88, comment: '掌握扎实，能够熟练运用ES6+特性' },
        { name: 'Vue.js框架', score: 85, comment: 'Vue3组合式API使用熟练，响应式原理理解深入' },
        { name: 'CSS/HTML', score: 82, comment: '布局能力强，对响应式设计有较好理解' },
        { name: '工程化工具', score: 78, comment: 'Webpack、Vite等工具使用熟练' },
        { name: '性能优化', score: 75, comment: '有基础的性能优化意识，需要深入学习' },
        { name: '团队协作', score: 90, comment: 'Git使用熟练，代码规范意识强' }
      ],
      projectMatch: {
        overallMatch: 82,
        projects: [
          {
            name: '电商管理系统',
            matchScore: 88,
            technologies: ['Vue3', 'TypeScript', 'Element Plus', 'Axios'],
            description: '基于Vue3的电商后台管理系统，包含商品管理、订单处理、用户管理等功能',
            highlights: ['使用组合式API重构', '实现了复杂的表格组件', '优化了页面加载性能']
          },
          {
            name: '移动端H5应用',
            matchScore: 78,
            technologies: ['Vue2', 'Vant', 'Sass', 'PWA'],
            description: '响应式移动端应用，支持离线访问和推送通知',
            highlights: ['实现了PWA功能', '适配多种屏幕尺寸', '集成了第三方支付']
          },
          {
            name: '数据可视化平台',
            matchScore: 85,
            technologies: ['Vue3', 'ECharts', 'D3.js', 'WebSocket'],
            description: '实时数据展示平台，支持多种图表类型和实时数据更新',
            highlights: ['自定义图表组件', '实时数据推送', '支持数据导出']
          }
        ]
      },
      improvements: {
        high: [
          {
            title: '深入学习TypeScript',
            description: '提升类型系统的使用能力，增强代码的可维护性和健壮性',
            impact: '高影响',
            timeline: '2-3个月',
            difficulty: '中等'
          },
          {
            title: '掌握微前端架构',
            description: '学习qiankun、single-spa等微前端框架，提升大型项目架构能力',
            impact: '高影响',
            timeline: '3-4个月',
            difficulty: '困难'
          }
        ],
        medium: [
          {
            title: '性能优化进阶',
            description: '深入学习浏览器渲染原理，掌握更多性能优化技巧',
            impact: '中影响',
            timeline: '1-2个月',
            difficulty: '中等'
          },
          {
            title: 'Node.js后端开发',
            description: '学习Node.js，成为全栈开发者，提升技术广度',
            impact: '中影响',
            timeline: '2-3个月',
            difficulty: '中等'
          }
        ],
        low: [
          {
            title: '设计系统理解',
            description: '学习设计系统的构建和维护，提升UI/UX理解能力',
            impact: '低影响',
            timeline: '1个月',
            difficulty: '简单'
          }
        ]
      },
      resources: [
        {
          name: '前端技术进阶',
          description: '提升前端核心技能的专业课程',
          color: '#667eea',
          icon: 'icon-code',
          items: [
            {
              title: 'TypeScript从入门到实战',
              provider: '极客时间',
              type: '视频课程',
              duration: '30小时',
              level: '中级',
              rating: 4.8,
              thumbnail: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA4MCA2MCIgd2lkdGg9IjgwIiBoZWlnaHQ9IjYwIj48cmVjdCB3aWR0aD0iODAiIGhlaWdodD0iNjAiIGZpbGw9IiMzMTc4YzYiLz48dGV4dCB4PSI0MCIgeT0iMzUiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGZpbGw9IndoaXRlIiBmb250LXNpemU9IjEyIiBmb250LWZhbWlseT0iQXJpYWwiPlRTPC90ZXh0Pjwvc3ZnPg==',
              tags: ['TypeScript', '类型系统', '工程化'],
              url: '/courses/typescript-advanced'
            },
            {
              title: 'Vue3源码解析',
              provider: '慕课网',
              type: '视频课程',
              duration: '25小时',
              level: '高级',
              rating: 4.9,
              thumbnail: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA4MCA2MCIgd2lkdGg9IjgwIiBoZWlnaHQ9IjYwIj48cmVjdCB3aWR0aD0iODAiIGhlaWdodD0iNjAiIGZpbGw9IiM0ZmM0OGQiLz48dGV4dCB4PSI0MCIgeT0iMzUiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGZpbGw9IndoaXRlIiBmb250LXNpemU9IjEwIiBmb250LWZhbWlseT0iQXJpYWwiPlZ1ZTM8L3RleHQ+PC9zdmc+',
              tags: ['Vue3', '源码', '响应式'],
              url: '/courses/vue3-source-code'
            }
          ]
        },
        {
          name: '工程化与架构',
          description: '大型项目开发必备技能',
          color: '#f093fb',
          icon: 'icon-architecture',
          items: [
            {
              title: '微前端架构实战',
              provider: 'B站课程',
              type: '实战项目',
              duration: '40小时',
              level: '高级',
              rating: 4.7,
              thumbnail: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA4MCA2MCIgd2lkdGg9IjgwIiBoZWlnaHQ9IjYwIj48cmVjdCB3aWR0aD0iODAiIGhlaWdodD0iNjAiIGZpbGw9IiNmMDkzZmIiLz48dGV4dCB4PSI0MCIgeT0iMzUiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGZpbGw9IndoaXRlIiBmb250LXNpemU9IjEwIiBmb250LWZhbWlseT0iQXJpYWwiPuW+ruWJjeeerTwvdGV4dD48L3N2Zz4=',
              tags: ['微前端', 'qiankun', '架构设计'],
              url: 'https://www.bilibili.com/video/BV13f4y1p7Ea/?vd_source=b3de36170238469382ce0e9bd9298e51'
            },
            {
              title: 'Webpack深度优化',
              provider: '拉勾教育',
              type: '视频课程',
              duration: '20小时',
              level: '中级',
              rating: 4.6,
              thumbnail: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA4MCA2MCIgd2lkdGg9IjgwIiBoZWlnaHQ9IjYwIj48cmVjdCB3aWR0aD0iODAiIGhlaWdodD0iNjAiIGZpbGw9IiM4ZGQ2ZjkiLz48dGV4dCB4PSI0MCIgeT0iMzUiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGZpbGw9IndoaXRlIiBmb250LXNpemU9IjEwIiBmb250LWZhbWlseT0iQXJpYWwiPldlYnBhY2s8L3RleHQ+PC9zdmc+',
              tags: ['Webpack', '性能优化', '构建工具'],
              url: '/courses/webpack-optimization'
            }
          ]
        },
        {
          name: '性能与优化',
          description: '前端性能优化专项提升',
          color: '#4facfe',
          icon: 'icon-performance',
          items: [
            {
              title: '前端性能优化实战',
              provider: '腾讯课堂',
              type: '实战课程',
              duration: '15小时',
              level: '中级',
              rating: 4.5,
              thumbnail: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA4MCA2MCIgd2lkdGg9IjgwIiBoZWlnaHQ9IjYwIj48cmVjdCB3aWR0aD0iODAiIGhlaWdodD0iNjAiIGZpbGw9IiM0ZmFjZmUiLz48dGV4dCB4PSI0MCIgeT0iMzUiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGZpbGw9IndoaXRlIiBmb250LXNpemU9IjEwIiBmb250LWZhbWlseT0iQXJpYWwiPuaAp+iDveS8mOWMljwvdGV4dD48L3N2Zz4=',
              tags: ['性能优化', '监控', '用户体验'],
              url: '/courses/frontend-performance'
            }
          ]
        }
      ],
      learningPath: [
        {
          title: '基础巩固阶段',
          duration: '1-2个月',
          goals: ['深化JavaScript核心概念', '掌握TypeScript基础', '熟练使用Vue3新特性'],
          resources: [
            { name: 'TypeScript从入门到实战', url: '/courses/typescript-advanced' },
            { name: 'Vue3源码解析', url: '/courses/vue3-source-code' }
          ]
        },
        {
          title: '技能提升阶段',
          duration: '2-3个月',
          goals: ['学习微前端架构', '掌握性能优化技巧', '提升工程化能力'],
          resources: [
            { name: '微前端架构实战', url: 'https://www.bilibili.com/video/BV13f4y1p7Ea/?vd_source=b3de36170238469382ce0e9bd9298e51' },
            { name: '前端性能优化实战', url: '/courses/frontend-performance' }
          ]
        },
        {
          title: '项目实战阶段',
          duration: '3-4个月',
          goals: ['完成大型项目实战', '积累架构设计经验', '提升团队协作能力'],
          resources: [
            { name: '企业级项目实战', url: '/projects/enterprise-project' },
            { name: '开源项目贡献', url: '/projects/open-source' }
          ]
        }
      ]
    })

    // 工具方法
    const getScoreLevel = (score) => {
      if (score >= 90) return 'excellent'
      if (score >= 80) return 'good'
      if (score >= 70) return 'average'
      return 'poor'
    }

    const getScoreLevelText = (score) => {
      if (score >= 90) return '优秀'
      if (score >= 80) return '良好'
      if (score >= 70) return '一般'
      return '待提升'
    }

    const getSkillColor = (score) => {
      if (score >= 85) return '#52c41a'
      if (score >= 75) return '#1890ff'
      if (score >= 65) return '#faad14'
      return '#f5222d'
    }

    const getMatchLevel = (match) => {
      if (match >= 85) return 'high'
      if (match >= 70) return 'medium'
      return 'low'
    }

    const getMatchLevelText = (match) => {
      if (match >= 85) return '高度匹配'
      if (match >= 70) return '较好匹配'
      return '一般匹配'
    }

    const getPriorityTitle = (key) => {
      const titles = {
        high: '🔥 高优先级建议',
        medium: '⚡ 中优先级建议',
        low: '💡 低优先级建议'
      }
      return titles[key] || key
    }

    const openResource = (resource) => {
      if (resource.url) {
        // 检查是否是外部链接（以http或https开头）
        if (resource.url.startsWith('http://') || resource.url.startsWith('https://')) {
          // 外部链接在新标签页打开
          window.open(resource.url, '_blank')
        } else {
          // 内部路由跳转
          router.push(resource.url)
        }
      } else {
        // 模拟外部链接
        window.open(`/resources/${encodeURIComponent(resource.title || resource.name)}`, '_blank')
      }
    }

    // 初始化技能雷达图
    const initSkillsRadarChart = () => {
      if (!skillsRadarChart.value) return

      const chart = echarts.init(skillsRadarChart.value)
      const skills = reportData.value.skillsAssessment

      const option = {
        tooltip: {
          trigger: 'item'
        },
        radar: {
          indicator: skills.map(skill => ({
            name: skill.name,
            max: 100
          })),
          radius: '70%',
          splitNumber: 4,
          axisName: {
            color: '#666',
            fontSize: 12
          },
          splitLine: {
            lineStyle: {
              color: '#e8e8e8'
            }
          },
          splitArea: {
            areaStyle: {
              color: ['rgba(102, 126, 234, 0.05)', 'rgba(102, 126, 234, 0.1)']
            }
          }
        },
        series: [{
          type: 'radar',
          data: [{
            value: skills.map(skill => skill.score),
            name: '技能评估',
            areaStyle: {
              color: 'rgba(102, 126, 234, 0.3)'
            },
            lineStyle: {
              color: '#667eea',
              width: 2
            },
            itemStyle: {
              color: '#667eea'
            }
          }]
        }]
      }

      chart.setOption(option)
      window.addEventListener('resize', () => chart.resize())
    }

    // 初始化项目匹配度图表
    const initProjectMatchChart = () => {
      if (!projectMatchChart.value) return

      const chart = echarts.init(projectMatchChart.value)
      const projects = reportData.value.projectMatch.projects

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}%'
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '50%'],
          data: projects.map(project => ({
            value: project.matchScore,
            name: project.name,
            itemStyle: {
              color: project.matchScore >= 85 ? '#52c41a' :
                project.matchScore >= 75 ? '#1890ff' : '#faad14'
            }
          })),
          label: {
            show: true,
            formatter: '{b}\n{c}%'
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      }

      chart.setOption(option)
      window.addEventListener('resize', () => chart.resize())
    }

    // 初始化优先级图表
    const initPriorityChart = () => {
      if (!priorityChart.value) return

      const chart = echarts.init(priorityChart.value)
      const improvements = reportData.value.improvements

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}项'
        },
        series: [{
          type: 'pie',
          radius: '60%',
          data: [
            { value: improvements.high.length, name: '高优先级', itemStyle: { color: '#f5222d' } },
            { value: improvements.medium.length, name: '中优先级', itemStyle: { color: '#faad14' } },
            { value: improvements.low.length, name: '低优先级', itemStyle: { color: '#52c41a' } }
          ],
          label: {
            show: false
          },
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      }

      chart.setOption(option)
      window.addEventListener('resize', () => chart.resize())
    }

    // 组件挂载后初始化图表
    onMounted(() => {
      nextTick(() => {
        initSkillsRadarChart()
        initProjectMatchChart()
        initPriorityChart()
      })
    })

    return {
      reportData,
      skillsRadarChart,
      projectMatchChart,
      priorityChart,
      getScoreLevel,
      getScoreLevelText,
      getSkillColor,
      getMatchLevel,
      getMatchLevelText,
      getPriorityTitle,
      openResource
    }
  }
}
</script>

<style scoped>
.assess-report-v2 {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f8f9fa;
  min-height: 100vh;
}

.report-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 30px;
  color: white;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.candidate-info {
  display: flex;
  gap: 30px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.candidate-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 3px solid rgba(255,255,255,0.3);
}

.candidate-name {
  font-size: 24px;
  margin: 0 0 5px 0;
  font-weight: 600;
}

.candidate-position {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

.interview-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.meta-label {
  font-size: 14px;
  opacity: 0.8;
  min-width: 60px;
}

.meta-value {
  font-weight: 500;
}

.overall-score-section {
  text-align: center;
}

.score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: rgba(255,255,255,0.2);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-bottom: 15px;
  backdrop-filter: blur(10px);
}

.score-value {
  font-size: 36px;
  font-weight: bold;
}

.score-label {
  font-size: 14px;
  opacity: 0.9;
}

.score-level {
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 500;
  background: rgba(255,255,255,0.2);
}

.report-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.section-header {
  margin-bottom: 25px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-desc {
  color: #666;
  margin: 0;
  font-size: 14px;
}

.skills-radar-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  align-items: start;
}

.radar-chart {
  width: 100%;
  height: 400px;
}

.skills-breakdown {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.skill-item {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.skill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.skill-name {
  font-weight: 500;
  color: #333;
}

.skill-score {
  font-weight: bold;
  color: #667eea;
}

.skill-progress {
  height: 6px;
  background: #e9ecef;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-bar {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.skill-comment {
  font-size: 12px;
  color: #666;
  margin: 0;
}

/* 项目匹配度分析样式 */
.project-analysis {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.match-overview {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 40px;
  align-items: center;
}

.match-chart {
  width: 300px;
  height: 300px;
}

.match-summary {
  text-align: center;
}

.match-score-big {
  font-size: 48px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 10px;
}

.match-label {
  font-size: 16px;
  color: #666;
  margin-bottom: 15px;
}

.match-level-badge {
  display: inline-block;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 500;
  color: white;
}

.match-level-badge.high {
  background: #52c41a;
}

.match-level-badge.medium {
  background: #1890ff;
}

.match-level-badge.low {
  background: #faad14;
}

.project-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
}

.project-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.project-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.project-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.project-match-score {
  background: #667eea;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-weight: bold;
  font-size: 14px;
}

.project-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.tech-tag {
  background: #e6f7ff;
  color: #1890ff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.project-description {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 15px;
}

.project-highlights {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.highlight-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #333;
}

.highlight-icon {
  color: #52c41a;
  font-weight: bold;
}

/* 改进建议样式 */
.improvement-suggestions {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.suggestions-overview {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 30px;
  align-items: center;
}

.priority-chart {
  width: 200px;
  height: 200px;
}

.priority-legend {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 50%;
}

.legend-item.high .legend-color {
  background: #f5222d;
}

.legend-item.medium .legend-color {
  background: #faad14;
}

.legend-item.low .legend-color {
  background: #52c41a;
}

.suggestions-details {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.priority-section {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
}

.priority-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 15px 0;
}

.priority-title.high {
  color: #f5222d;
}

.priority-title.medium {
  color: #faad14;
}

.priority-title.low {
  color: #52c41a;
}

.suggestions-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 15px;
}

.suggestion-card {
  background: white;
  border-radius: 8px;
  padding: 16px;
  border-left: 4px solid;
  transition: all 0.3s ease;
}

.suggestion-card.high {
  border-left-color: #f5222d;
}

.suggestion-card.medium {
  border-left-color: #faad14;
}

.suggestion-card.low {
  border-left-color: #52c41a;
}

.suggestion-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.suggestion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.suggestion-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.suggestion-impact {
  background: #e6f7ff;
  color: #1890ff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.suggestion-description {
  color: #666;
  font-size: 13px;
  line-height: 1.5;
  margin-bottom: 12px;
}

.suggestion-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-timeline, .action-difficulty {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.timeline-icon, .difficulty-icon {
  font-size: 14px;
}

/* 资源推荐样式 */
.resources-recommendation {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.resources-categories {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.resource-category {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
}

.category-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.category-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.category-info {
  flex: 1;
}

.category-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 5px 0;
}

.category-desc {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.category-count {
  background: #e6f7ff;
  color: #1890ff;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.category-items {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
}

.resource-item {
  background: white;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #e9ecef;
  cursor: pointer;
  transition: all 0.3s ease;
  display: grid;
  grid-template-columns: 80px 1fr auto;
  gap: 15px;
  align-items: start;
}

.resource-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  border-color: #667eea;
}

.resource-thumbnail {
  position: relative;
}

.resource-image {
  width: 80px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
  background: #f0f0f0;
}

.resource-type {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #667eea;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 500;
}

.resource-content {
  flex: 1;
}

.resource-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 5px 0;
}

.resource-provider {
  color: #666;
  font-size: 13px;
  margin: 0 0 8px 0;
}

.resource-meta {
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
}

.resource-duration, .resource-level, .resource-rating {
  font-size: 12px;
  color: #666;
}

.resource-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.resource-tag {
  background: #f0f0f0;
  color: #666;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 11px;
}

.resource-action {
  display: flex;
  align-items: center;
}

.btn-start-learning {
  background: #667eea;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-start-learning:hover {
  background: #5a67d8;
  transform: translateY(-1px);
}

/* 学习路径样式 */
.learning-roadmap {
  padding: 20px 0;
}

.roadmap-timeline {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.roadmap-timeline::before {
  content: '';
  position: absolute;
  left: 30px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(to bottom, #667eea, #764ba2);
}

.timeline-phase {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.phase-marker {
  position: relative;
  z-index: 2;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: #f8f9fa;
  border: 3px solid #e9ecef;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.timeline-phase.active .phase-marker {
  background: #667eea;
  border-color: #667eea;
  color: white;
}

.timeline-phase.completed .phase-marker {
  background: #52c41a;
  border-color: #52c41a;
  color: white;
}

.phase-number {
  font-size: 18px;
  font-weight: bold;
}

.phase-content {
  flex: 1;
  background: white;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e9ecef;
  margin-top: 10px;
}

.timeline-phase.active .phase-content {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
}

.phase-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.phase-duration {
  color: #666;
  font-size: 14px;
  margin: 0 0 15px 0;
}

.phase-goals {
  margin-bottom: 15px;
}

.goal-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
}

.goal-icon {
  font-size: 16px;
}

.phase-resources {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.phase-resource-tag {
  background: #e6f7ff;
  color: #1890ff;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.phase-resource-tag:hover {
  background: #1890ff;
  color: white;
  transform: translateY(-1px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .assess-report-v2 {
    padding: 15px;
  }

  .header-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .candidate-info {
    flex-direction: column;
    gap: 15px;
  }

  .skills-radar-container {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .match-overview {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .suggestions-overview {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .project-details {
    grid-template-columns: 1fr;
  }

  .category-items {
    grid-template-columns: 1fr;
  }

  .resource-item {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .roadmap-timeline::before {
    left: 15px;
  }

  .phase-marker {
    width: 30px;
    height: 30px;
  }

  .phase-number {
    font-size: 14px;
  }
}

/* 图标样式 */
.icon-radar::before { content: '📊'; }
.icon-project::before { content: '🚀'; }
.icon-improve::before { content: '💡'; }
.icon-resources::before { content: '📚'; }
.icon-roadmap::before { content: '🗺️'; }
.icon-code::before { content: '💻'; }
.icon-architecture::before { content: '🏗️'; }
.icon-performance::before { content: '⚡'; }
</style>
