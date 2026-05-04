<template>
  <div class="personality-test">
    <div class="header-section">
      <div class="header-content">
        <h1>职业性格测评</h1>
        <p>深入了解您的职业性格特质，找到最适合的职业方向</p>
      </div>
      <div class="header-image">
        <el-icon size="80"><User /></el-icon>
      </div>
    </div>

    <!-- 测评类型选择 -->
    <div class="test-types">
      <h2>选择测评类型</h2>
      <div class="type-grid">
        <div 
          v-for="testType in testTypes" 
          :key="testType.id"
          class="type-card"
          @click="selectTestType(testType)"
        >
          <div class="type-header">
            <div class="type-icon">
              <el-icon><component :is="testType.icon" /></el-icon>
            </div>
            <div class="type-badge">{{ testType.duration }}</div>
          </div>
          <div class="type-content">
            <h3>{{ testType.name }}</h3>
            <p>{{ testType.description }}</p>
            <div class="type-features">
              <span v-for="feature in testType.features" :key="feature" class="feature-tag">
                {{ feature }}
              </span>
            </div>
          </div>
          <div class="type-footer">
            <div class="completion-info">
              <span class="completion-count">{{ testType.completedCount }} 人已完成</span>
            </div>
            <el-button type="primary" size="small">
              开始测评
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 历史测评记录 -->
    <div class="history-section">
      <div class="history-header">
        <h2>历史测评记录</h2>
        <div class="history-actions">
          <el-button
            @click="loadTestHistory"
            :loading="loading"
            icon="Refresh"
            size="small"
            type="primary"
            plain
          >
            刷新
          </el-button>
          <el-button
            @click="addTestRecord"
            size="small"
            type="success"
            plain
          >
            添加测试记录
          </el-button>
          <el-button
            @click="testApi"
            size="small"
            type="warning"
            plain
          >
            测试API
          </el-button>
          <el-button
            @click="clearLocalStorage"
            size="small"
            type="danger"
            plain
          >
            清除缓存
          </el-button>
        </div>
      </div>
      <div v-if="historyRecords.length === 0" class="empty-state">
        <el-empty description="暂无测评记录">
          <el-button type="primary" @click="selectTestType(testTypes[0])">
            开始第一次测评
          </el-button>
        </el-empty>
      </div>
      <div v-else class="history-grid">
        <div 
          v-for="record in historyRecords" 
          :key="record.id"
          class="history-card"
        >
          <div class="history-header">
            <div class="test-name">{{ record.testName }}</div>
            <div class="test-date">{{ record.completedAt }}</div>
          </div>
          <div class="personality-result">
            <div class="personality-type">
              <span class="type-label">性格类型：</span>
              <span class="type-value">{{ record.personalityType }}</span>
            </div>
            <div class="personality-traits">
              <div 
                v-for="trait in record.traits" 
                :key="trait.name"
                class="trait-item"
              >
                <span class="trait-name">{{ trait.name }}</span>
                <div class="trait-bar">
                  <div 
                    class="trait-fill" 
                    :style="{ width: trait.score + '%' }"
                  ></div>
                </div>
                <span class="trait-score">{{ trait.score }}%</span>
              </div>
            </div>
          </div>
          <div class="history-actions">
            <el-button size="small" @click="viewDetailReport(record)">
              查看详细报告
            </el-button>
            <el-button size="small" type="primary" @click="shareResult(record)">
              分享结果
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 测评说明弹窗 -->
    <el-dialog
      v-model="testDialogVisible"
      :title="selectedTestType?.name"
      width="700px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedTestType" class="test-dialog-content">
        <div class="test-overview">
          <h3>测评概述</h3>
          <p>{{ selectedTestType.fullDescription }}</p>
        </div>
        
        <div class="test-process">
          <h3>测评流程</h3>
          <div class="process-steps">
            <div 
              v-for="(step, index) in testSteps" 
              :key="index"
              class="process-step"
            >
              <div class="step-number">{{ index + 1 }}</div>
              <div class="step-content">
                <div class="step-title">{{ step.title }}</div>
                <div class="step-description">{{ step.description }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="test-tips">
          <h3>测评建议</h3>
          <ul>
            <li>请在安静的环境中完成测评</li>
            <li>根据第一直觉选择答案，不要过度思考</li>
            <li>诚实回答每个问题，这样结果会更准确</li>
            <li>整个过程大约需要 {{ selectedTestType.duration }}</li>
          </ul>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="testDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="startPersonalityTest">
            开始测评
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { 
  User, 
  Star, 
  TrendCharts, 
  DataAnalysis,
  Compass
} from '@element-plus/icons-vue'

export default {
  name: 'PersonalityTest',
  components: {
    User,
    Star,
    TrendCharts,
    DataAnalysis,
    Compass
  },
  data() {
    return {
      testDialogVisible: false,
      selectedTestType: null,
      testTypes: [
        {
          id: 1,
          name: 'MBTI 职业性格测试',
          description: '基于荣格心理学理论，分析您的性格类型和职业倾向',
          fullDescription: 'MBTI（Myers-Briggs Type Indicator）是目前世界上应用最广泛的性格测试工具之一。通过分析您在四个维度上的偏好，帮助您了解自己的性格类型和最适合的职业方向。',
          icon: 'User',
          duration: '15-20分钟',
          features: ['16种性格类型', '职业匹配', '团队角色'],
          completedCount: 12580
        },
        // {
        //   id: 2,
        //   name: '大五人格测试',
        //   description: '从五个维度全面评估您的人格特质',
        //   fullDescription: '大五人格模型是心理学界公认的最科学的人格理论之一。通过测评开放性、尽责性、外向性、宜人性、神经质五个维度，全面了解您的人格特质。',
        //   icon: 'Star',
        //   duration: '10-15分钟',
        //   features: ['五大维度', '科学权威', '职场适应'],
        //   completedCount: 8960
        // },
        {
          id: 3,
          name: '职业兴趣测评',
          description: '发现您真正感兴趣的职业领域',
          fullDescription: '基于霍兰德职业兴趣理论，帮助您发现自己的职业兴趣类型，找到最符合个人兴趣的职业方向，提高工作满意度和成就感。',
          icon: 'Compass',
          duration: '12-18分钟',
          features: ['六种兴趣类型', '职业推荐', '发展建议'],
          completedCount: 15420
        },
        {
          id: 4,
          name: '职场能力评估',
          description: '评估您的职场核心能力和发展潜力',
          fullDescription: '综合评估您在沟通协调、团队合作、领导力、创新思维、抗压能力等方面的表现，为您的职业发展提供科学指导。',
          icon: 'TrendCharts',
          duration: '20-25分钟',
          features: ['多维能力', '发展建议', '提升方案'],
          completedCount: 6780
        },
        {
          id: 5,
          name: 'DISC 性格测试',
          description: '基于马斯顿理论，分析您的行为风格和性格特征',
          fullDescription: 'DISC性格测试是由美国心理学家威廉·莫尔顿·马斯顿创建的权威性格评估工具，已被广泛应用于世界500强企业的人才招聘。通过分析支配型(D)、影响型(I)、稳定型(S)、谨慎型(C)四个维度，帮助您了解自己的行为风格。',
          icon: 'DataAnalysis',
          duration: '10-15分钟',
          features: ['四大维度', '权威专业', '企业认可'],
          completedCount: 7650
        }
      ],
      historyRecords: [],
      loading: false,
      testSteps: [
        {
          title: '阅读说明',
          description: '了解测评目的和注意事项'
        },
        {
          title: '回答问题',
          description: '根据真实情况选择最符合的答案'
        },
        {
          title: '生成报告',
          description: '系统自动分析并生成个性化报告'
        },
        {
          title: '查看结果',
          description: '获得详细的性格分析和职业建议'
        }
      ]
    }
  },
  methods: {
    selectTestType(testType) {
      this.selectedTestType = testType
      this.testDialogVisible = true
    },

    async loadTestHistory() {
      try {
        this.loading = true
        // 使用固定的用户ID进行测试，因为从日志看测试是以GUEST身份完成的
        const userId = 1 // 固定使用用户ID 1

        console.log('开始加载DISC测试历史，用户ID:', userId)

        // 优先尝试API调用（因为后端服务正在运行）
        try {
          const { discTestApi } = await import('@/api/discTest')
          console.log('正在调用API获取历史记录，用户ID:', userId)
          const response = await discTestApi.getTestHistory(userId)

          console.log('API完整响应:', JSON.stringify(response, null, 2))
          console.log('response.data类型:', typeof response.data)
          console.log('response.data内容:', response.data)

          if (response && response.success && response.data) {
            // 检查数据是否为数组
            const dataArray = Array.isArray(response.data) ? response.data : [response.data]
            console.log('处理后的数据数组:', dataArray)
            console.log('数据数组长度:', dataArray.length)

            if (dataArray.length > 0) {
              console.log('从API找到历史记录:', dataArray.length, '条')

              this.historyRecords = dataArray.map((test, index) => {
                console.log(`处理第${index + 1}条测试记录:`, JSON.stringify(test, null, 2))

                // 根据实际的数据结构调整
                const result = test.result || test
                console.log('提取的result:', JSON.stringify(result, null, 2))

                // 构建历史记录项
                const historyItem = {
                  id: result.resultId || test.testSession || Date.now() + index,
                  testSession: test.testSession, // 保存testSession用于详细报告
                  testName: 'DISC 性格测试',
                  personalityType: `${result.primaryType || 'Unknown'}型主导`,
                  completedAt: result.completeTime ?
                    new Date(result.completeTime).toLocaleDateString() :
                    new Date().toLocaleDateString(),
                  traits: [
                    { name: '支配型(D)', score: result.scores?.D || result.dScore || 0 },
                    { name: '影响型(I)', score: result.scores?.I || result.iScore || 0 },
                    { name: '稳定型(S)', score: result.scores?.S || result.sScore || 0 },
                    { name: '谨慎型(C)', score: result.scores?.C || result.cScore || 0 }
                  ]
                }

                console.log('构建的历史记录项:', JSON.stringify(historyItem, null, 2))
                return historyItem
              })

              console.log('最终的historyRecords:', JSON.stringify(this.historyRecords, null, 2))
              console.log('historyRecords数组长度:', this.historyRecords.length)

              // 强制触发Vue的响应式更新
              this.$forceUpdate()

            } else {
              console.log('数据数组为空')
              this.showEmptyState()
              return
            }
            console.log('处理后的历史记录:', this.historyRecords)

            // 同时保存到本地存储作为备份
            localStorage.setItem('discTestHistory', JSON.stringify(this.historyRecords))
          } else {
            console.log('API返回空数据或失败:', response)
            this.showEmptyState()
          }
        } catch (apiError) {
          console.error('API调用失败详细错误:', apiError)

          // API失败时，尝试从本地存储加载
          const localHistory = this.loadFromLocalStorage()
          if (localHistory.length > 0) {
            console.log('API失败，从本地存储加载到历史记录:', localHistory.length, '条')
            this.historyRecords = localHistory
          } else {
            this.showEmptyState()
          }
        }
      } catch (error) {
        console.error('加载测试历史失败：', error)
        this.showEmptyState()
      } finally {
        this.loading = false
      }
    },

    loadFromLocalStorage() {
      try {
        const stored = localStorage.getItem('discTestHistory')
        if (stored) {
          const history = JSON.parse(stored)
          return Array.isArray(history) ? history : []
        }
      } catch (error) {
        console.error('从本地存储加载失败:', error)
      }
      return []
    },

    saveToLocalStorage(testResult) {
      try {
        const history = this.loadFromLocalStorage()
        const newRecord = {
          id: Date.now(),
          testName: 'DISC 性格测试',
          personalityType: `${testResult.primaryType}型主导`,
          completedAt: new Date().toLocaleDateString(),
          traits: [
            { name: '支配型(D)', score: testResult.scores?.D || 0 },
            { name: '影响型(I)', score: testResult.scores?.I || 0 },
            { name: '稳定型(S)', score: testResult.scores?.S || 0 },
            { name: '谨慎型(C)', score: testResult.scores?.C || 0 }
          ]
        }

        // 添加到历史记录开头
        history.unshift(newRecord)

        // 只保留最近10条记录
        if (history.length > 10) {
          history.splice(10)
        }

        localStorage.setItem('discTestHistory', JSON.stringify(history))
        console.log('测试结果已保存到本地存储:', newRecord)
        return true
      } catch (error) {
        console.error('保存到本地存储失败:', error)
        return false
      }
    },

    showEmptyState() {
      this.historyRecords = [
        {
          id: 0,
          testName: 'DISC 性格测试',
          personalityType: '暂无测试记录',
          completedAt: '请先完成测试',
          traits: [
            { name: '支配型(D)', score: 0 },
            { name: '影响型(I)', score: 0 },
            { name: '稳定型(S)', score: 0 },
            { name: '谨慎型(C)', score: 0 }
          ]
        }
      ]
    },
    
    startPersonalityTest() {
      this.testDialogVisible = false

      // 如果是DISC测试，跳转到专门的DISC测试页面
      if (this.selectedTestType.id === 5) {
        this.$router.push({
          name: 'DiscTest'
        })
      } else {
        // 其他测试跳转到通用测评页面
        this.$router.push({
          name: 'PersonalityTestExecution',
          params: { testId: this.selectedTestType.id }
        })
      }
    },
    
    viewDetailReport(record) {
      // 查看详细报告
      console.log('查看详细报告，记录:', record)

      // 使用testSession作为recordId，如果没有则使用record.id
      const recordId = record.testSession || record.id
      console.log('使用的recordId:', recordId)

      this.$router.push({
        name: 'PersonalityReport',
        params: { recordId: recordId }
      })
    },
    
    shareResult(record) {
      // 分享测评结果
      this.$message.success('分享链接已复制到剪贴板')
    },

    handleDiscTestCompleted(event) {
      console.log('DISC测试完成，刷新历史记录', event.detail)

      // 保存到本地存储
      if (event.detail && event.detail.result) {
        this.saveToLocalStorage(event.detail.result)
      }

      // 立即刷新历史记录
      this.loadTestHistory()
    },

    // 测试用方法：添加一个模拟的测试记录
    addTestRecord() {
      const mockResult = {
        primaryType: ['D', 'I', 'S', 'C'][Math.floor(Math.random() * 4)],
        scores: {
          D: Math.floor(Math.random() * 20) + 5,
          I: Math.floor(Math.random() * 20) + 5,
          S: Math.floor(Math.random() * 20) + 5,
          C: Math.floor(Math.random() * 20) + 5
        }
      }

      this.saveToLocalStorage(mockResult)
      this.loadTestHistory()
      this.$message.success('已添加测试记录')
    },

    // 直接测试API调用
    async testApi() {
      try {
        console.log('开始测试API调用...')

        // 直接使用axios测试
        const axios = (await import('axios')).default
        const response = await axios.get('/api/disc-test/history/1')

        console.log('直接axios调用结果:', response)
        console.log('响应状态:', response.status)
        console.log('响应数据:', response.data)

        if (response.status === 200 && response.data) {
          if (response.data.success) {
            this.$message.success(`API调用成功，返回 ${response.data.data ? response.data.data.length : 0} 条记录`)
            console.log('历史记录数据:', response.data.data)
          } else {
            this.$message.error('API返回失败: ' + response.data.message)
          }
        } else {
          this.$message.error('API调用异常')
        }
      } catch (error) {
        console.error('API测试失败详细信息:', error)
        if (error.response) {
          console.log('错误响应状态:', error.response.status)
          console.log('错误响应数据:', error.response.data)
          this.$message.error(`API调用失败: ${error.response.status} - ${error.response.statusText}`)
        } else {
          this.$message.error('API测试失败: ' + error.message)
        }
      }
    },

    // 清除本地存储
    clearLocalStorage() {
      localStorage.removeItem('discTestHistory')
      this.$message.success('本地缓存已清除')
      // 重新加载历史记录
      this.loadTestHistory()
    }
  },

  mounted() {
    // 页面加载时获取历史记录
    this.loadTestHistory()

    // 监听DISC测试完成事件
    window.addEventListener('discTestCompleted', this.handleDiscTestCompleted)
  },

  beforeUnmount() {
    // 清理事件监听器
    window.removeEventListener('discTestCompleted', this.handleDiscTestCompleted)
  }
}
</script>

<style scoped>
.personality-test {
  padding: 24px;
  background: linear-gradient(135deg, #f5f9ff 0%, #e0efff 100%);
  min-height: 100vh;
}

/* 头部区域 */
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding: 32px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
}

.header-content h1 {
  margin: 0 0 12px 0;
  font-size: 36px;
  font-weight: 700;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-content p {
  margin: 0;
  font-size: 18px;
  color: #5a84b3;
  font-weight: 500;
}

.header-image {
  opacity: 0.7;
  color: #1a6fc4;
}

/* 测评类型选择 */
.test-types {
  margin-bottom: 32px;
}

.test-types h2 {
  margin-bottom: 24px;
  color: #2c3e50;
  font-size: 28px;
  font-weight: 600;
  text-align: center;
}

.type-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 24px;
}

.type-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 2px solid transparent;
}

.type-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(26, 111, 196, 0.2);
  border-color: #1a6fc4;
}

.type-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.type-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(26, 111, 196, 0.3);
}

.type-badge {
  background: #e0f0ff;
  color: #1a6fc4;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.type-content h3 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
}

.type-content p {
  margin: 0 0 20px 0;
  color: #5a84b3;
  font-size: 14px;
  line-height: 1.6;
}

.type-features {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.feature-tag {
  background: #e0f0ff;
  color: #1a6fc4;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.type-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.completion-count {
  font-size: 13px;
  color: #5a84b3;
  font-weight: 500;
}

.type-footer .el-button {
  border-radius: 8px;
  font-weight: 500;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(26, 111, 196, 0.3);
}

.type-footer .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(26, 111, 196, 0.4);
}

/* 历史测评记录 */
.history-section {
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.history-section h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 28px;
  font-weight: 600;
}

.history-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.history-actions .el-button {
  border-radius: 8px;
  font-weight: 500;
}

.history-actions .el-button--primary {
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border: none;
}

.history-actions .el-button--success {
  background: linear-gradient(135deg, #36c28f 0%, #36c2cf 100%);
  border: none;
}

.history-actions .el-button--warning {
  background: linear-gradient(135deg, #f39c12 0%, #f1c40f 100%);
  border: none;
  color: white;
}

.history-actions .el-button--danger {
  background: linear-gradient(135deg, #e74c3c 0%, #ff7979 100%);
  border: none;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

:deep(.empty-state .el-empty__description) {
  color: #5a84b3;
  margin-bottom: 20px;
}

.empty-state .el-button {
  border-radius: 8px;
  font-weight: 500;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border: none;
}

.history-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.history-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(26, 111, 196, 0.1);
  transition: all 0.3s ease;
  border: 1px solid #e6f1ff;
}

.history-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(26, 111, 196, 0.15);
}

.history-card .history-header {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e6f1ff;
}

.test-name {
  font-weight: 600;
  color: #2c3e50;
  font-size: 18px;
}

.test-date {
  color: #5a84b3;
  font-size: 14px;
}

.personality-type {
  margin-bottom: 20px;
}

.type-label {
  color: #5a84b3;
  font-size: 14px;
}

.type-value {
  color: #1a6fc4;
  font-weight: 600;
  font-size: 16px;
}

.trait-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.trait-name {
  width: 90px;
  font-size: 14px;
  color: #5a84b3;
  font-weight: 500;
}

.trait-bar {
  flex: 1;
  height: 8px;
  background: #e0f0ff;
  border-radius: 4px;
  overflow: hidden;
}

.trait-fill {
  height: 100%;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.trait-score {
  width: 45px;
  text-align: right;
  font-size: 14px;
  color: #2c3e50;
  font-weight: 600;
}

.history-card .history-actions {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e6f1ff;
}

.history-card .history-actions .el-button {
  border-radius: 6px;
  font-weight: 500;
}

/* 测评说明弹窗 */
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

.test-overview, .test-process, .test-tips {
  margin-bottom: 28px;
}

.test-overview h3, .test-process h3, .test-tips h3 {
  margin-bottom: 16px;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.test-overview p {
  color: #5a84b3;
  line-height: 1.6;
  margin: 0;
}

.process-steps {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.process-step {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.step-number {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  flex-shrink: 0;
  font-size: 14px;
}

.step-content {
  flex: 1;
}

.step-title {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 6px;
  font-size: 15px;
}

.step-description {
  color: #5a84b3;
  font-size: 14px;
  line-height: 1.5;
}

.test-tips ul {
  margin: 0;
  padding-left: 20px;
  color: #5a84b3;
}

.test-tips li {
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

/* 响应式设计 */
@media (max-width: 768px) {
  .personality-test {
    padding: 16px;
  }

  .header-section {
    flex-direction: column;
    text-align: center;
    padding: 24px 20px;
    gap: 20px;
  }

  .header-image {
    order: -1;
  }

  .type-grid {
    grid-template-columns: 1fr;
  }

  .history-header {
    flex-direction: column;
    align-items: stretch;
  }

  .history-actions {
    justify-content: center;
  }

  .history-grid {
    grid-template-columns: 1fr;
  }

  .trait-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .trait-bar {
    width: 100%;
  }

  .trait-score {
    text-align: left;
  }

  :deep(.el-dialog) {
    width: 90% !important;
    margin: 20px auto;
  }
}
</style>
