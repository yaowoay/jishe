<template>
  <div class="resume-analysis-report">
    <!-- 报告头部 -->
    <div class="report-header">
      <div class="header-content">
        <div class="candidate-info">
          <h1 class="candidate-name">{{ candidateName }}</h1>
          <p class="target-position">求职意向：{{ targetPosition }}</p>
        </div>
        <div class="file-info">
          <div class="file-details">
            <span class="file-name">{{ fileName }}</span>
            <span class="file-size">{{ formatFileSize(fileSize) }}</span>
            <span class="analysis-time">分析时间：{{ formatAnalysisTime(analysisTime) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 总体评分 -->
    <div class="overall-score-section">
      <div class="score-card">
        <h2>综合匹配度</h2>
        <div class="score-display">
          <div class="score-circle" :style="{ '--score': overallScore }">
            <span class="score-number">{{ overallScore }}</span>
            <span class="score-unit">分</span>
          </div>
          <div class="score-description">
            <p>{{ getScoreDescription(overallScore) }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 详细分析 -->
    <div class="analysis-sections">
      <!-- 个人信息分析 -->
      <div class="analysis-section">
        <div class="section-header">
          <h3>个人信息分析</h3>
          <div class="score-badge" :class="getScoreClass(personalInfo.matchScore)">
            {{ personalInfo.matchScore }}分
          </div>
        </div>
        <div class="section-content">
          <div class="strengths-section">
            <h4>优势亮点</h4>
            <ul class="strengths-list">
              <li v-for="strength in personalInfo.strengths" :key="strength" class="strength-item">
                <i class="icon-check">✓</i>
                {{ strength }}
              </li>
            </ul>
          </div>
          
          <div class="risks-section" v-if="personalInfo.risks && personalInfo.risks.length > 0">
            <h4>风险提示</h4>
            <ul class="risks-list">
              <li v-for="risk in personalInfo.risks" :key="risk" class="risk-item">
                <i class="icon-warning">⚠</i>
                {{ risk }}
              </li>
            </ul>
          </div>

          <div class="optimization-section" v-if="personalInfo.optimizationSuggestions">
            <h4>优化建议</h4>
            <p class="optimization-text">{{ personalInfo.optimizationSuggestions }}</p>
          </div>
        </div>
      </div>

      <!-- 技能分析 -->
      <div class="analysis-section">
        <div class="section-header">
          <h3>技能匹配分析</h3>
          <div class="score-badge" :class="getScoreClass(skills.matchScore)">
            {{ skills.matchScore }}分
          </div>
        </div>
        <div class="section-content">
          <div class="technical-highlights">
            <h4>技术亮点</h4>
            <div class="highlights-grid">
              <div v-for="highlight in skills.technicalHighlights" :key="highlight" class="highlight-tag">
                {{ highlight }}
              </div>
            </div>
          </div>

          <div class="skill-gaps" v-if="skills.skillGaps && skills.skillGaps.length > 0">
            <h4>技能差距</h4>
            <ul class="gaps-list">
              <li v-for="gap in skills.skillGaps" :key="gap" class="gap-item">
                <i class="icon-gap">📋</i>
                {{ gap }}
              </li>
            </ul>
          </div>

          <div class="expression-optimization" v-if="skills.expressionOptimization">
            <h4>表达优化</h4>
            <p class="optimization-text">{{ skills.expressionOptimization }}</p>
          </div>
        </div>
      </div>

      <!-- 教育背景分析 -->
      <div class="analysis-section">
        <div class="section-header">
          <h3>教育背景分析</h3>
          <div class="score-badge" :class="getScoreClass(educationBackground.matchScore)">
            {{ educationBackground.matchScore }}分
          </div>
        </div>
        <div class="section-content">
          <div class="education-advantages">
            <h4>教育优势</h4>
            <ul class="advantages-list">
              <li v-for="advantage in educationBackground.educationAdvantages" :key="advantage" class="advantage-item">
                <i class="icon-education">🎓</i>
                {{ advantage }}
              </li>
            </ul>
          </div>

          <div class="enhancement-directions" v-if="educationBackground.enhancementDirections">
            <h4>提升方向</h4>
            <p class="enhancement-text">{{ educationBackground.enhancementDirections }}</p>
          </div>
        </div>
      </div>

      <!-- 项目经验分析 -->
      <div class="analysis-section">
        <div class="section-header">
          <h3>项目经验分析</h3>
          <div class="score-badge" :class="getScoreClass(projectExperience.matchScore)">
            {{ projectExperience.matchScore }}分
          </div>
        </div>
        <div class="section-content">
          <div class="project-highlights">
            <h4>经验亮点</h4>
            <ul class="highlights-list">
              <li v-for="highlight in projectExperience.projectHighlights" :key="highlight" class="highlight-item">
                <i class="icon-project">💼</i>
                {{ highlight }}
              </li>
            </ul>
          </div>

          <div class="improvement-areas" v-if="projectExperience.improvementAreas && projectExperience.improvementAreas.length > 0">
            <h4>改进领域</h4>
            <ul class="improvement-list">
              <li v-for="area in projectExperience.improvementAreas" :key="area" class="improvement-item">
                <i class="icon-improve">📈</i>
                {{ area }}
              </li>
            </ul>
          </div>

          <div class="restructuring-suggestions" v-if="projectExperience.restructuringSuggestions">
            <h4>重构建议</h4>
            <p class="restructuring-text">{{ projectExperience.restructuringSuggestions }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 分析统计信息 -->
    <div class="analysis-stats">
      <div class="stats-grid">
        <div class="stat-item">
          <span class="stat-label">分析耗时</span>
          <span class="stat-value">{{ formatElapsedTime(elapsedTime) }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">处理步骤</span>
          <span class="stat-value">{{ totalSteps }}步</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">Token消耗</span>
          <span class="stat-value">{{ totalTokens }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">分析状态</span>
          <span class="stat-value status-success">{{ status }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ResumeAnalysisReport',
  props: {
    analysisData: {
      type: Object,
      required: true
    }
  },
  mounted() {
    // 调试信息：打印接收到的数据结构
    console.log('=== ResumeAnalysisReport 调试信息 ===')
    console.log('1. 原始数据:', this.analysisData)
    console.log('2. 解析后的分析结果:', this.analysisResult)
    console.log('3. 文件信息:', this.uploadInfo)
    console.log('4. 分析元数据:', this.analysisMetadata)
    console.log('5. 候选人姓名:', this.candidateName)
    console.log('6. 目标职位:', this.targetPosition)
    console.log('7. 综合评分:', this.overallScore)
    console.log('8. 各项评分:', {
      personalInfo: this.personalInfo.matchScore,
      skills: this.skills.matchScore,
      education: this.educationBackground.matchScore,
      project: this.projectExperience.matchScore
    })
    console.log('=== 调试信息结束 ===')
  },
  computed: {
    // 解析分析数据 - 统一的数据结构
    analysisResult() {
      return this.analysisData?.data?.analysis?.data?.outputs?.end_eponing || {}
    },

    // 文件信息
    uploadInfo() {
      return this.analysisData?.data?.upload || {}
    },

    // 分析元数据
    analysisMetadata() {
      return this.analysisData?.data?.analysis?.data || {}
    },
    
    // 候选人姓名
    candidateName() {
      const personalInfo = this.analysisResult.personalInfo?.originalContent || ''
      const nameMatch = personalInfo.match(/([^\s]+)\s+求职意向/)
      return nameMatch ? nameMatch[1] : '候选人'
    },
    
    // 目标职位
    targetPosition() {
      return this.analysisResult.targetPosition?.name || '未指定'
    },
    
    // 文件信息
    fileName() {
      return this.uploadInfo.name || '简历文件'
    },
    
    fileSize() {
      return this.uploadInfo.size || 0
    },
    
    analysisTime() {
      return this.analysisMetadata.created_at || Date.now()
    },
    
    // 各部分分析结果
    personalInfo() {
      return this.analysisResult.personalInfo || {}
    },
    
    skills() {
      return this.analysisResult.skills || {}
    },
    
    educationBackground() {
      return this.analysisResult.educationBackground || {}
    },
    
    projectExperience() {
      return this.analysisResult.projectExperience || {}
    },
    
    // 统计信息
    elapsedTime() {
      return this.analysisMetadata.elapsed_time || 0
    },
    
    totalSteps() {
      return this.analysisMetadata.total_steps || 0
    },
    
    totalTokens() {
      return this.analysisMetadata.total_tokens || 0
    },
    
    status() {
      return this.analysisMetadata.status === 'succeeded' ? '成功' : '失败'
    },
    
    // 综合评分 - 优先使用响应中的overallScore
    overallScore() {
      // 优先使用响应数据中的overallScore
      if (this.analysisResult.overallScore) {
        return this.analysisResult.overallScore
      }

      // 如果没有，则计算平均值
      const scores = [
        this.personalInfo.matchScore || 0,
        this.skills.matchScore || 0,
        this.educationBackground.matchScore || 0,
        this.projectExperience.matchScore || 0
      ].filter(score => score > 0) // 过滤掉0分

      if (scores.length === 0) return 0
      return Math.round(scores.reduce((sum, score) => sum + score, 0) / scores.length)
    }

  },
  methods: {
    // 格式化文件大小
    formatFileSize(bytes) {
      if (bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    },
    
    // 格式化分析时间
    formatAnalysisTime(timestamp) {
      return new Date(timestamp * 1000).toLocaleString('zh-CN')
    },
    
    // 格式化耗时
    formatElapsedTime(seconds) {
      return `${seconds.toFixed(2)}秒`
    },
    
    // 获取评分描述
    getScoreDescription(score) {
      if (score >= 90) return '优秀匹配'
      if (score >= 80) return '良好匹配'
      if (score >= 70) return '一般匹配'
      if (score >= 60) return '基本匹配'
      return '匹配度较低'
    },
    
    // 获取评分样式类
    getScoreClass(score) {
      if (score >= 90) return 'score-excellent'
      if (score >= 80) return 'score-good'
      if (score >= 70) return 'score-average'
      if (score >= 60) return 'score-basic'
      return 'score-low'
    }
  }
}
</script>

<style scoped>
.resume-analysis-report {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f8fafc;
  min-height: 100vh;
}

/* 报告头部 */
.report-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 16px;
  margin-bottom: 30px;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.candidate-name {
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 10px 0;
}

.target-position {
  font-size: 1.2rem;
  opacity: 0.9;
  margin: 0;
}

.file-details {
  text-align: right;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-name {
  font-weight: 600;
  font-size: 1.1rem;
}

.file-size, .analysis-time {
  opacity: 0.8;
  font-size: 0.9rem;
}

/* 总体评分 */
.overall-score-section {
  margin-bottom: 30px;
}

.score-card {
  background: white;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  text-align: center;
}

.score-card h2 {
  margin: 0 0 30px 0;
  color: #2d3748;
  font-size: 1.8rem;
}

.score-display {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40px;
}

.score-circle {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  background: conic-gradient(
    from 0deg,
    #667eea 0deg,
    #667eea calc(var(--score) * 3.6deg),
    #e2e8f0 calc(var(--score) * 3.6deg),
    #e2e8f0 360deg
  );
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
}

.score-circle::before {
  content: '';
  position: absolute;
  width: 120px;
  height: 120px;
  background: white;
  border-radius: 50%;
}

.score-number {
  font-size: 3rem;
  font-weight: 700;
  color: #667eea;
  z-index: 1;
}

.score-unit {
  font-size: 1rem;
  color: #718096;
  z-index: 1;
}

.score-description {
  text-align: left;
}

.score-description p {
  font-size: 1.2rem;
  color: #4a5568;
  margin: 0;
}

/* 分析部分 */
.analysis-sections {
  display: grid;
  gap: 30px;
  margin-bottom: 30px;
}

.analysis-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25px 30px;
  background: #f7fafc;
  border-bottom: 1px solid #e2e8f0;
}

.section-header h3 {
  margin: 0;
  color: #2d3748;
  font-size: 1.4rem;
  font-weight: 600;
}

.score-badge {
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 0.9rem;
}

.score-excellent { background: #c6f6d5; color: #22543d; }
.score-good { background: #bee3f8; color: #2a4365; }
.score-average { background: #fef5e7; color: #744210; }
.score-basic { background: #fed7d7; color: #742a2a; }
.score-low { background: #fed7d7; color: #742a2a; }

.section-content {
  padding: 30px;
}

.section-content h4 {
  margin: 0 0 15px 0;
  color: #2d3748;
  font-size: 1.1rem;
  font-weight: 600;
}

/* 列表样式 */
.strengths-list, .risks-list, .gaps-list, .advantages-list, .highlights-list, .improvement-list {
  list-style: none;
  padding: 0;
  margin: 0 0 25px 0;
}

.strength-item, .risk-item, .gap-item, .advantage-item, .highlight-item, .improvement-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f1f5f9;
}

.strength-item:last-child, .risk-item:last-child, .gap-item:last-child,
.advantage-item:last-child, .highlight-item:last-child, .improvement-item:last-child {
  border-bottom: none;
}

.icon-check { color: #38a169; font-weight: bold; }
.icon-warning { color: #ed8936; font-weight: bold; }
.icon-gap { color: #4299e1; }
.icon-education { color: #805ad5; }
.icon-project { color: #38b2ac; }
.icon-improve { color: #ed64a6; }

/* 技能标签 */
.highlights-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 25px;
}

.highlight-tag {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

/* 优化建议文本 */
.optimization-text, .enhancement-text, .restructuring-text {
  background: #f7fafc;
  padding: 20px;
  border-radius: 12px;
  border-left: 4px solid #667eea;
  color: #4a5568;
  line-height: 1.6;
  margin: 0;
}

/* 统计信息 */
.analysis-stats {
  background: white;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: #f7fafc;
  border-radius: 12px;
}

.stat-label {
  display: block;
  color: #718096;
  font-size: 0.9rem;
  margin-bottom: 8px;
}

.stat-value {
  display: block;
  color: #2d3748;
  font-size: 1.5rem;
  font-weight: 600;
}

.status-success {
  color: #38a169;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .resume-analysis-report {
    padding: 15px;
  }

  .header-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .file-details {
    text-align: center;
  }

  .candidate-name {
    font-size: 2rem;
  }

  .score-display {
    flex-direction: column;
    gap: 20px;
  }

  .section-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }

  .section-content {
    padding: 20px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
