<template>
  <div class="professional-test">
    <div class="header-section">
      <div class="header-content">
        <h1>专业题笔试</h1>
        <p>测试您的专业技能水平，提升面试竞争力</p>
      </div>
      <div class="header-stats">
        <div class="stat-card">
          <div class="stat-number">{{ totalTests }}</div>
          <div class="stat-label">可用测试</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ completedTests }}</div>
          <div class="stat-label">已完成</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ averageScore }}%</div>
          <div class="stat-label">平均分数</div>
        </div>
      </div>
    </div>

    <!-- 测试分类 -->
    <div class="test-categories">
      <h2>选择测试类别</h2>
      <div class="category-grid">
        <div 
          v-for="category in testCategories" 
          :key="category.id"
          class="category-card"
          @click="selectCategory(category)"
        >
          <div class="category-icon">
            <el-icon><component :is="category.icon" /></el-icon>
          </div>
          <div class="category-info">
            <h3>{{ category.name }}</h3>
            <p>{{ category.description }}</p>
            <div class="category-meta">
              <span class="test-count">{{ category.testCount }} 套题</span>
              <span class="difficulty">{{ category.difficulty }}</span>
            </div>
          </div>
          <div class="category-action">
            <el-button type="primary" size="small">
              开始测试
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 最近测试记录 -->
    <div class="recent-tests">
      <div class="section-header">
        <h2>最近测试记录</h2>
        <el-button
          type="text"
          icon="el-icon-refresh"
          @click="loadTestRecords"
          :loading="loading"
        >
          刷新
        </el-button>
      </div>

      <el-table
        :data="recentTestRecords"
        style="width: 100%"
        v-loading="loading"
        element-loading-text="加载测试记录中..."
      >
        <el-table-column prop="testName" label="测试名称" width="200" />
        <el-table-column prop="category" label="类别" width="120" />
        <el-table-column prop="score" label="得分" width="100">
          <template #default="{ row }">
            <el-tag :type="getScoreType(row.score)">{{ row.score }}分</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="用时" width="100" />
        <el-table-column prop="completedAt" label="完成时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewReport(row)">
              查看报告
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!loading && recentTestRecords.length === 0" class="no-data">
        <i class="el-icon-document"></i>
        <p>暂无测试记录</p>
        <p>完成第一次测试后，记录将显示在这里</p>
      </div>
    </div>

    <!-- 测试详情弹窗 -->
    <el-dialog
      v-model="testDialogVisible"
      :title="selectedCategory?.name + ' - 专业测试'"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedCategory" class="test-dialog-content">
        <div class="test-info">
          <h3>测试说明</h3>
          <ul>
            <li>本测试共 {{ selectedCategory.questionCount }} 道题</li>
            <li>建议用时：{{ selectedCategory.duration }} 分钟</li>
            <li>题型包括：{{ selectedCategory.questionTypes.join('、') }}</li>
            <li>测试结束后将生成详细的能力分析报告</li>
          </ul>
        </div>
        
        <div class="test-rules">
          <h3>注意事项</h3>
          <ul>
            <li>请确保网络连接稳定</li>
            <li>测试过程中请勿刷新页面</li>
            <li>每道题都有时间限制，请合理安排时间</li>
            <li>提交后无法修改答案</li>
          </ul>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="testDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="startTest">开始测试</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { 
  Monitor, 
  Code, 
  DataAnalysis, 
  Setting, 
  Briefcase,
  TrendCharts 
} from '@element-plus/icons-vue'

export default {
  name: 'ProfessionalTest',
  components: {
    Monitor,
    Code,
    DataAnalysis,
    Setting,
    Briefcase,
    TrendCharts
  },
  data() {
    return {
      totalTests: 24,
      completedTests: 8,
      averageScore: 85,
      testDialogVisible: false,
      selectedCategory: null,
      testCategories: [
        {
          id: 1,
          name: '前端开发',
          description: 'HTML、CSS、JavaScript、Vue、React等前端技术',
          icon: 'Monitor',
          testCount: 6,
          difficulty: '中级',
          questionCount: 10,
          duration: 45,
          questionTypes: ['单选题', '多选题']
        },
        {
          id: 2,
          name: '后端开发',
          description: 'Java、Python、Node.js、数据库等后端技术',
          icon: 'Code',
          testCount: 8,
          difficulty: '中级',
          questionCount: 10,
          duration: 25,
          questionTypes: ['单选题', '多选题']
        },
        {
          id: 3,
          name: '数据分析',
          description: 'SQL、Python、数据挖掘、统计分析',
          icon: 'DataAnalysis',
          testCount: 4,
          difficulty: '高级',
          questionCount: 10,
          duration: 50,
          questionTypes: ['单选题']
        },
        {
          id: 5,
          name: '运维工程师',
          description: 'Linux、Docker、Kubernetes、监控',
          icon: 'Setting',
          testCount: 3,
          difficulty: '高级',
          questionCount: 10,
          duration: 55,
          questionTypes: ['单选题', '多选题', '实操题']
        }
      ],
      recentTestRecords: [],
      loading: false
    }
  },
  mounted() {
    this.loadTestRecords()
  },
  methods: {
    async loadTestRecords() {
      try {
        this.loading = true
        const userId = this.getCurrentUserId()

        // 从数据库API获取测试记录
        const response = await fetch(`/api/test-results/history?userId=${userId}&pageSize=10`)
        const result = await response.json()

        if (result.success && result.data) {
          // 转换数据格式以适配现有的表格显示
          this.recentTestRecords = result.data.list.map(record => ({
            id: record.id,
            testName: record.categoryName + ' 专业测试',
            category: record.categoryName,
            score: record.score,
            duration: this.formatDuration(record.duration),
            completedAt: this.formatDate(record.completedAt),
            testResultId: record.id, // 用于查看报告
            analysisReport: record.analysisReport
          }))

          // 更新统计数据
          this.updateStatistics(result.data.list)
        } else {
          console.warn('获取测试记录失败:', result.message)
          // 降级到本地存储
          this.loadLocalTestRecords()
        }
      } catch (error) {
        console.error('加载测试记录失败:', error)
        this.$message.error('加载测试记录失败，请检查网络连接')
        // 降级到本地存储
        this.loadLocalTestRecords()
      } finally {
        this.loading = false
      }
    },

    loadLocalTestRecords() {
      // 降级方案：从本地存储加载测试记录
      const records = JSON.parse(localStorage.getItem('testRecords') || '[]')
      this.recentTestRecords = records.slice(0, 10) // 只显示最近10条
      this.updateStatistics(records)
    },

    updateStatistics(records) {
      // 更新统计数据
      if (records.length > 0) {
        this.completedTests = records.length
        const totalScore = records.reduce((sum, record) => sum + record.score, 0)
        this.averageScore = Math.round(totalScore / records.length)
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
    selectCategory(category) {
      this.selectedCategory = category
      this.testDialogVisible = true
    },
    
    startTest() {
      this.testDialogVisible = false
      // 跳转到具体的测试页面
      this.$router.push({
        name: 'ProfessionalTestExam',
        params: {
          category: this.getCategoryCode(this.selectedCategory.id),
          categoryName: this.selectedCategory.name
        }
      })
    },

    getCategoryCode(categoryId) {
      const codeMap = {
        1: 'frontend',
        2: 'backend',
        3: 'dataAnalysis',
        4: 'algorithm',
        5: 'system'
      }
      return codeMap[categoryId] || 'backend'
    },
    
    viewReport(record) {
      // 查看测试报告
      if (record.testResultId) {
        // 使用数据库中的测试结果ID跳转
        this.$router.push({
          name: 'TestResult',
          params: {
            testResultId: record.testResultId
          }
        })
      } else if (record.report) {
        // 兼容旧的本地存储数据
        this.$router.push({
          name: 'TestResult',
          params: {
            score: record.score,
            report: JSON.stringify(record.report),
            category: this.getCategoryCodeByName(record.category),
            categoryName: record.category,
            questions: JSON.stringify(record.questions || []),
            answers: JSON.stringify(record.answers || {})
          }
        })
      } else {
        this.$message.warning('测试报告不存在')
      }
    },

    getCategoryCodeByName(categoryName) {
      const nameMap = {
        '前端开发': 'frontend',
        '后端开发': 'backend',
        '数据分析': 'dataAnalysis',
        '算法': 'algorithm',
        '系统设计': 'system'
      }
      return nameMap[categoryName] || 'backend'
    },
    
    getScoreType(score) {
      if (score >= 90) return 'success'
      if (score >= 80) return 'warning'
      if (score >= 60) return 'info'
      return 'danger'
    }
  }
}
</script>
<style scoped>
.professional-test {
  padding: 24px;
  background: linear-gradient(135deg, #f5f9ff 0%, #e0efff 100%);
  min-height: 100vh;
}

/* 头部区域 */
.header-section {
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 32px;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 24px;
}

.header-content h1 {
  font-size: 36px;
  font-weight: 700;
  color: #1a6fc4;
  margin: 0 0 12px 0;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-content p {
  font-size: 18px;
  color: #5a84b3;
  margin: 0;
  font-weight: 500;
}

.header-stats {
  display: flex;
  gap: 32px;
}

.stat-card {
  text-align: center;
  padding: 0 20px;
  position: relative;
}

.stat-card:not(:last-child):after {
  content: '';
  position: absolute;
  right: -16px;
  top: 50%;
  transform: translateY(-50%);
  width: 1px;
  height: 40px;
  background: linear-gradient(to bottom, transparent, #c2dcff, transparent);
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #1a6fc4;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #5a84b3;
  font-weight: 500;
}

/* 测试分类区域 */
.test-categories {
  margin-bottom: 40px;
}

.test-categories h2 {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 24px;
  text-align: center;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 24px;
}

.category-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(26, 111, 196, 0.2);
  border-color: #1a6fc4;
}

.category-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: linear-gradient(135deg, #e0f0ff 0%, #d0e6ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1a6fc4;
  font-size: 28px;
  margin-bottom: 20px;
}

.category-info {
  flex: 1;
  margin-bottom: 20px;
}

.category-info h3 {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 12px 0;
}

.category-info p {
  color: #5a84b3;
  line-height: 1.6;
  margin: 0 0 16px 0;
}

.category-meta {
  display: flex;
  gap: 16px;
}

.test-count, .difficulty {
  font-size: 14px;
  padding: 6px 12px;
  border-radius: 6px;
  font-weight: 500;
}

.test-count {
  background: #e0f0ff;
  color: #1a6fc4;
}

.difficulty {
  background: #e8f7f0;
  color: #36c28f;
}

.category-action {
  text-align: right;
}

.category-action .el-button {
  border-radius: 8px;
  font-weight: 500;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(26, 111, 196, 0.3);
  transition: all 0.3s ease;
}

.category-action .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(26, 111, 196, 0.4);
}

/* 最近测试记录区域 */
.recent-tests {
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.section-header .el-button {
  color: #1a6fc4;
  font-weight: 500;
}

:deep(.recent-tests .el-table) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.recent-tests .el-table__header) {
  background: linear-gradient(135deg, #f0f7ff 0%, #e0efff 100%);
}

:deep(.recent-tests .el-table th) {
  background: transparent;
  color: #1a6fc4;
  font-weight: 600;
  height: 56px;
}

:deep(.recent-tests .el-table tr) {
  background: transparent;
  transition: all 0.3s ease;
}

:deep(.recent-tests .el-table .el-table__row:hover) {
  background: #f0f7ff;
}

:deep(.recent-tests .el-table td) {
  border-bottom: 1px solid #e6f1ff;
  padding: 16px 0;
}

:deep(.recent-tests .el-tag) {
  border-radius: 6px;
  font-weight: 500;
  padding: 0 10px;
  height: 26px;
  line-height: 26px;
}

:deep(.recent-tests .el-tag--success) {
  background: #e8f7f0;
  color: #36c28f;
  border-color: #c2eedb;
}

:deep(.recent-tests .el-tag--warning) {
  background: #fef5e7;
  color: #e67e22;
  border-color: #fad7a0;
}

:deep(.recent-tests .el-tag--info) {
  background: #e0f0ff;
  color: #1a6fc4;
  border-color: #c2dcff;
}

:deep(.recent-tests .el-tag--danger) {
  background: #fee;
  color: #e74c3c;
  border-color: #fcc;
}

:deep(.recent-tests .el-button) {
  border-radius: 6px;
  font-weight: 500;
}

.no-data {
  text-align: center;
  padding: 60px 0;
  color: #5a84b3;
}

.no-data .el-icon {
  font-size: 64px;
  color: #c2dcff;
  margin-bottom: 16px;
}

.no-data p:first-of-type {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
}

.no-data p:last-of-type {
  font-size: 14px;
  margin: 0;
}

/* 测试详情弹窗 */
:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(26, 111, 196, 0.2);
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f0f7ff 0%, #e0efff 100%);
  margin: 0;
  padding: 20px 24px;
  border-bottom: 1px solid #e6f1ff;
}

:deep(.el-dialog__title) {
  color: #1a6fc4;
  font-weight: 600;
  font-size: 20px;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

.test-dialog-content {
  padding: 0;
}

.test-info, .test-rules {
  margin-bottom: 28px;
}

.test-info h3, .test-rules h3 {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 16px;
}

.test-info ul, .test-rules ul {
  margin: 0;
  padding-left: 20px;
  color: #5a84b3;
}

.test-info li, .test-rules li {
  margin-bottom: 10px;
  line-height: 1.6;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #e6f1ff;
}

.dialog-footer .el-button {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 500;
}

.dialog-footer .el-button--primary {
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border: none;
}

/* 加载动画 */
:deep(.el-loading-mask) {
  border-radius: 12px;
  background-color: rgba(255, 255, 255, 0.8);
}

:deep(.el-loading-spinner .path) {
  stroke: #1a6fc4;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .professional-test {
    padding: 16px;
  }

  .header-section {
    flex-direction: column;
    text-align: center;
    padding: 24px 20px;
  }

  .header-stats {
    flex-direction: column;
    gap: 20px;
  }

  .stat-card:after {
    display: none;
  }

  .category-grid {
    grid-template-columns: 1fr;
  }

  .category-meta {
    flex-direction: column;
    gap: 8px;
  }

  .section-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  :deep(.recent-tests .el-table) {
    font-size: 14px;
  }

  :deep(.el-dialog) {
    width: 90% !important;
    margin: 20px auto;
  }
}
</style>

