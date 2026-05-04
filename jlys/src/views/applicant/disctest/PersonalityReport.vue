<template>
  <div class="personality-report">
    <div class="report-header">
      <el-button @click="goBack" icon="ArrowLeft" circle></el-button>
      <h1>性格测试详细报告</h1>
      <div class="header-actions">
        <el-button @click="downloadReport" type="primary" icon="Download">下载报告</el-button>
        <el-button @click="shareReport" icon="Share">分享</el-button>
      </div>
    </div>

    <div class="report-content" v-loading="loading">
      <!-- 报告概览 -->
      <el-card class="overview-card" v-if="reportData">
        <template #header>
          <div class="card-header">
            <h2>测试概览</h2>
            <el-tag :type="getTypeTagType(reportData.primaryType)" size="large">
              {{ reportData.primaryType }}型主导
            </el-tag>
          </div>
        </template>
        
        <div class="overview-content">
          <div class="test-info">
            <div class="info-item">
              <span class="label">测试类型：</span>
              <span class="value">{{ reportData.testName }}</span>
            </div>
            <div class="info-item">
              <span class="label">完成时间：</span>
              <span class="value">{{ reportData.completedAt }}</span>
            </div>
            <div class="info-item">
              <span class="label">性格档案：</span>
              <span class="value">{{ reportData.personalityProfile }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <!-- DISC得分详情 -->
      <el-card class="scores-card" v-if="reportData && reportData.scores">
        <template #header>
          <h2>DISC维度得分</h2>
        </template>
        
        <div class="scores-content">
          <div class="score-charts">
            <div 
              v-for="(score, type) in reportData.scores" 
              :key="type"
              class="score-item"
            >
              <div class="score-header">
                <div class="score-icon" :class="`${type.toLowerCase()}-type`">
                  {{ type }}
                </div>
                <div class="score-info">
                  <h3>{{ getTypeName(type) }}</h3>
                  <p class="score-value">{{ score }} 分</p>
                </div>
              </div>
              <el-progress 
                :percentage="(score / 40) * 100"
                :color="getTypeColor(type)"
                :stroke-width="12"
                :show-text="false"
              />
              <div class="score-description">
                {{ getTypeDescription(type) }}
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 详细分析 -->
      <el-card class="analysis-card" v-if="reportData">
        <template #header>
          <h2>详细分析</h2>
        </template>
        
        <div class="analysis-content">
          <div class="analysis-section">
            <h3>性格特征分析</h3>
            <p>{{ reportData.detailedAnalysis || '您的主要性格类型体现了独特的行为风格和思维模式。' }}</p>
          </div>
          
          <div class="analysis-section">
            <h3>优势特点</h3>
            <p>{{ reportData.strengths || '您在团队协作和沟通方面表现出色，具有很强的适应能力。' }}</p>
          </div>
          
          <div class="analysis-section">
            <h3>发展建议</h3>
            <p>{{ reportData.weaknesses || '建议在决策过程中更多考虑细节，提升分析判断能力。' }}</p>
          </div>
          
          <div class="analysis-section">
            <h3>职业建议</h3>
            <p>{{ reportData.careerSuggestions || '适合从事需要人际沟通和团队协作的工作领域。' }}</p>
          </div>
        </div>
      </el-card>

      <!-- 行为风格指南 -->
      <el-card class="guide-card" v-if="reportData">
        <template #header>
          <h2>行为风格指南</h2>
        </template>
        
        <div class="guide-content">
          <div class="guide-grid">
            <div class="guide-item">
              <h4>工作风格</h4>
              <p>{{ getWorkStyleGuide(reportData.primaryType) }}</p>
            </div>
            <div class="guide-item">
              <h4>沟通方式</h4>
              <p>{{ getCommunicationGuide(reportData.primaryType) }}</p>
            </div>
            <div class="guide-item">
              <h4>决策模式</h4>
              <p>{{ getDecisionGuide(reportData.primaryType) }}</p>
            </div>
            <div class="guide-item">
              <h4>压力应对</h4>
              <p>{{ getStressGuide(reportData.primaryType) }}</p>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 空状态 -->
      <el-empty v-if="!reportData && !loading" description="报告数据加载失败">
        <el-button type="primary" @click="loadReportData">重新加载</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PersonalityReport',
  data() {
    return {
      loading: false,
      reportData: null
    }
  },
  
  created() {
    this.loadReportData()
  },
  
  methods: {
    async loadReportData() {
      this.loading = true
      const recordId = this.$route.params.recordId

      try {
        console.log('加载详细报告，记录ID:', recordId)

        // 从API获取真实的测试结果数据
        const { discTestApi } = await import('@/api/discTest')

        // 如果recordId是testSession格式，使用getTestResult
        // 如果是数字ID，可能需要其他方式获取
        let response
        if (recordId && recordId.startsWith('DISC_')) {
          // 使用testSession获取结果
          response = await discTestApi.getTestResult(recordId)
        } else {
          // 尝试获取用户最新结果
          response = await discTestApi.getLatestTestResult(1)
        }

        console.log('详细报告API响应:', response)

        if (response && response.success && response.data && response.data.result) {
          const result = response.data.result
          console.log('解析的测试结果:', result)

          this.reportData = {
            testName: 'DISC 性格测试',
            primaryType: result.primaryType || 'Unknown',
            personalityProfile: result.personalityProfile || `${result.primaryType}${result.secondaryType}型`,
            completedAt: result.completeTime ?
              new Date(result.completeTime).toLocaleDateString() :
              new Date().toLocaleDateString(),
            scores: result.scores || {
              'D': result.dScore || 0,
              'I': result.iScore || 0,
              'S': result.sScore || 0,
              'C': result.cScore || 0
            },
            detailedAnalysis: result.detailedAnalysis || '详细分析数据加载中...',
            strengths: result.strengths || '优势特点数据加载中...',
            weaknesses: result.weaknesses || '发展建议数据加载中...',
            careerSuggestions: result.careerSuggestions || '职业建议数据加载中...'
          }

          console.log('构建的报告数据:', this.reportData)
        } else {
          console.error('无法获取测试结果数据:', response)
          this.useDefaultData()
        }
      } catch (error) {
        console.error('加载详细报告失败:', error)
        this.useDefaultData()
      } finally {
        this.loading = false
      }
    },

    useDefaultData() {
      this.reportData = {
        testName: 'DISC 性格测试',
        primaryType: 'Unknown',
        personalityProfile: '数据加载失败',
        completedAt: new Date().toLocaleDateString(),
        scores: { 'D': 0, 'I': 0, 'S': 0, 'C': 0 },
        detailedAnalysis: '无法加载详细分析数据，请重试',
        strengths: '无法加载优势特点数据',
        weaknesses: '无法加载发展建议数据',
        careerSuggestions: '无法加载职业建议数据'
      }
    },
    
    goBack() {
      this.$router.go(-1)
    },
    
    downloadReport() {
      this.$message.success('报告下载功能开发中')
    },
    
    shareReport() {
      this.$message.success('分享链接已复制到剪贴板')
    },
    
    getTypeName(type) {
      const typeNames = {
        'D': '支配型',
        'I': '影响型',
        'S': '稳定型',
        'C': '谨慎型'
      }
      return typeNames[type] || type
    },
    
    getTypeColor(type) {
      const colors = {
        'D': '#ff4757',
        'I': '#ffa502',
        'S': '#2ed573',
        'C': '#3742fa'
      }
      return colors[type] || '#409eff'
    },
    
    getTypeTagType(type) {
      const tagTypes = {
        'D': 'danger',
        'I': 'warning',
        'S': 'success',
        'C': 'primary'
      }
      return tagTypes[type] || 'primary'
    },
    
    getTypeDescription(type) {
      const descriptions = {
        'D': '控制者，天生的领袖，喜欢挑战和竞争',
        'I': '社交者，活泼外向，善于沟通和影响他人',
        'S': '支持者，平和稳定，注重团队合作',
        'C': '完美主义者，注重细节和准确性'
      }
      return descriptions[type] || ''
    },
    
    getWorkStyleGuide(type) {
      const guides = {
        'D': '喜欢快节奏的工作环境，偏好挑战性任务，善于在压力下工作',
        'I': '在团队合作中表现出色，善于激励他人，喜欢多样化的工作内容',
        'S': '稳定可靠，善于维护团队和谐，偏好有序的工作环境',
        'C': '注重细节和质量，喜欢独立工作，追求完美的结果'
      }
      return guides[type] || '根据个人特点调整工作方式'
    },
    
    getCommunicationGuide(type) {
      const guides = {
        'D': '直接明了，重视效率，喜欢简洁的沟通方式',
        'I': '热情友好，善于表达，喜欢面对面的交流',
        'S': '耐心倾听，温和友善，避免冲突性的对话',
        'C': '准确详细，逻辑清晰，偏好书面沟通'
      }
      return guides[type] || '根据情况调整沟通方式'
    },
    
    getDecisionGuide(type) {
      const guides = {
        'D': '快速决策，依靠直觉和经验，敢于承担风险',
        'I': '考虑他人意见，重视团队共识，依靠人际关系',
        'S': '谨慎考虑，寻求稳定方案，避免急剧变化',
        'C': '深入分析，基于数据和事实，追求最优解'
      }
      return guides[type] || '结合理性和感性因素决策'
    },
    
    getStressGuide(type) {
      const guides = {
        'D': '通过运动或挑战性活动释放压力，保持目标导向',
        'I': '通过社交活动和交流缓解压力，寻求他人支持',
        'S': '通过规律作息和稳定环境管理压力，避免过度变化',
        'C': '通过计划和准备减少压力，寻求完美的解决方案'
      }
      return guides[type] || '找到适合自己的压力管理方式'
    }
  }
}
</script>

<style scoped>
.personality-report {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.report-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.report-header h1 {
  margin: 0;
  color: #303133;
  flex: 1;
  text-align: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.report-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 概览卡片 */
.overview-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overview-card .card-header h2 {
  margin: 0;
  color: #303133;
}

.overview-content {
  padding: 20px 0;
}

.test-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-item .label {
  font-size: 14px;
  color: #909399;
}

.info-item .value {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

/* 得分卡片 */
.scores-card h2 {
  margin: 0;
  color: #303133;
}

.scores-content {
  padding: 20px 0;
}

.score-charts {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.score-item {
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #fafafa;
}

.score-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.score-icon {
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

.score-info h3 {
  margin: 0 0 5px 0;
  color: #303133;
}

.score-value {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
}

.score-description {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
  line-height: 1.4;
}

/* 分析卡片 */
.analysis-card h2 {
  margin: 0;
  color: #303133;
}

.analysis-content {
  padding: 20px 0;
}

.analysis-section {
  margin-bottom: 25px;
}

.analysis-section h3 {
  color: #409eff;
  margin-bottom: 10px;
}

.analysis-section p {
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

/* 指南卡片 */
.guide-card h2 {
  margin: 0;
  color: #303133;
}

.guide-content {
  padding: 20px 0;
}

.guide-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.guide-item {
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #f9f9f9;
}

.guide-item h4 {
  color: #409eff;
  margin: 0 0 10px 0;
}

.guide-item p {
  color: #606266;
  line-height: 1.5;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .personality-report {
    padding: 10px;
  }

  .report-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }

  .report-header h1 {
    order: -1;
  }

  .test-info {
    grid-template-columns: 1fr;
  }

  .score-charts {
    grid-template-columns: 1fr;
  }

  .guide-grid {
    grid-template-columns: 1fr;
  }

  .header-actions {
    flex-direction: column;
    width: 100%;
  }

  .header-actions .el-button {
    width: 100%;
  }
}
</style>
