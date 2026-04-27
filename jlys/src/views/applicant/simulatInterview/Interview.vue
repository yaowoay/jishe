<template>
  <div class="interview-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1>AI智能面试</h1>
        <p>体验前沿的AI面试技术，提升您的面试表现</p>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <div class="stat-number">{{ totalInterviews }}</div>
          <div class="stat-label">总面试次数</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ averageScore }}%</div>
          <div class="stat-label">平均得分</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ improvementRate }}%</div>
          <div class="stat-label">提升幅度</div>
        </div>
      </div>
    </div>

    <!-- 功能选择区域 -->
    <div class="interview-options">
      <div class="options-grid">
<!--        &lt;!&ndash; AI面试卡片 &ndash;&gt;-->
<!--        <div class="option-card ai-interview" @click="startAIInterview">-->
<!--          <div class="card-header">-->
<!--            <div class="card-icon">-->
<!--              <el-icon><ChatDotRound /></el-icon>-->
<!--            </div>-->
<!--            <div class="card-badge">经典模式</div>-->
<!--          </div>-->
<!--          <div class="card-content">-->
<!--            <h3>AI面试</h3>-->
<!--            <p>传统的AI面试模式，通过语音对话进行面试问答</p>-->
<!--            <div class="card-features">-->
<!--              <div class="feature-item">-->
<!--                <el-icon><Microphone /></el-icon>-->
<!--                <span>语音交互</span>-->
<!--              </div>-->
<!--              <div class="feature-item">-->
<!--                <el-icon><TrendCharts /></el-icon>-->
<!--                <span>实时评分</span>-->
<!--              </div>-->
<!--              <div class="feature-item">-->
<!--                <el-icon><Document /></el-icon>-->
<!--                <span>详细报告</span>-->
<!--              </div>-->
<!--            </div>-->
<!--          </div>-->
<!--          <div class="card-footer">-->
<!--            <div class="difficulty-level">-->
<!--              <span class="level-label">难度：</span>-->
<!--              <div class="level-dots">-->
<!--                <span class="dot active"></span>-->
<!--                <span class="dot active"></span>-->
<!--                <span class="dot"></span>-->
<!--              </div>-->
<!--            </div>-->
<!--            <div class="duration">-->
<!--              <el-icon><Clock /></el-icon>-->
<!--              <span>15-30分钟</span>-->
<!--            </div>-->
<!--          </div>-->
<!--        </div>-->

        <!-- 去面试卡片 -->
        <div class="option-card exam-interview" @click="gotoSimulatExam">
          <div class="card-header">
            <div class="card-icon">
              <el-icon><Flag /></el-icon>
            </div>
            <div class="card-badge primary">推荐</div>
          </div>
          <div class="card-content">
            <h3>去面试</h3>
            <p>直接进入标准化模拟面试环节，体验真实面试场景</p>
            <div class="card-features">
              <div class="feature-item">
                <el-icon><ClipboardCheck /></el-icon>
                <span>岗位题库</span>
              </div>
              <div class="feature-item">
                <el-icon><Timer /></el-icon>
                <span>计时模式</span>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <div class="difficulty-level">
              <span class="level-label">难度：</span>
              <div class="level-dots">
                <span class="dot active"></span>
                <span class="dot active"></span>
                <span class="dot active"></span>
              </div>
            </div>
            <div class="duration">
              <el-icon><Clock /></el-icon>
              <span>30-45分钟</span>
            </div>
          </div>
        </div>

      </div>
    </div>


    <!-- 面试准备弹窗 -->
    <el-dialog
        v-model="preparationDialogVisible"
        :title="selectedMode === 'ai' ? 'AI面试准备' : '多模态智能体准备'"
        width="600px"
        :close-on-click-modal="false"
    >
      <div class="preparation-content">
        <div class="preparation-checklist">
          <h3>面试前准备</h3>
          <div class="checklist-items">
            <div class="checklist-item">
              <el-checkbox v-model="checklist.camera">摄像头权限已开启</el-checkbox>
            </div>
            <div class="checklist-item">
              <el-checkbox v-model="checklist.microphone">麦克风权限已开启</el-checkbox>
            </div>
            <div class="checklist-item">
              <el-checkbox v-model="checklist.environment">环境安静，光线充足</el-checkbox>
            </div>
            <div class="checklist-item">
              <el-checkbox v-model="checklist.network">网络连接稳定</el-checkbox>
            </div>
            <div class="checklist-item" v-if="selectedMode === 'multimodal'">
              <el-checkbox v-model="checklist.position">坐姿端正，面向摄像头</el-checkbox>
            </div>
          </div>
        </div>

        <div class="preparation-tips">
          <h3>面试建议</h3>
          <ul>
            <li v-if="selectedMode === 'ai'">保持自然的语调和语速</li>
            <li v-if="selectedMode === 'multimodal'">注意面部表情和肢体语言</li>
            <li>回答问题时要条理清晰</li>
            <li>遇到不会的问题可以诚实说明</li>
            <li>保持自信和积极的态度</li>
          </ul>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="preparationDialogVisible = false">取消</el-button>
          <el-button
              type="primary"
              @click="confirmStart"
              :disabled="!allChecklistCompleted"
          >
            开始面试
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

import {
  ChatDotRound,
  Monitor,
  Microphone,
  TrendCharts,
  Document,
  Clock,
  VideoCamera,
  View,
  DataAnalysis,
  Check,
  Close,
  Flag,
  ClipboardCheck,
  Timer,
  Medal
} from '@element-plus/icons-vue'

export default {
  name: 'Interview',
  components: {
    ChatDotRound,
    Monitor,
    Microphone,
    TrendCharts,
    Document,
    Clock,
    VideoCamera,
    View,
    DataAnalysis,
    Check,
    Close,
    Flag,
    ClipboardCheck,
    Timer,
    Medal
  },
  setup() {
    const router = useRouter()

    // 响应式数据
    const totalInterviews = ref(12)
    const averageScore = ref(85)
    const improvementRate = ref(23)
    const preparationDialogVisible = ref(false)
    const selectedMode = ref('')

    // 准备清单
    const checklist = ref({
      camera: false,
      microphone: false,
      environment: false,
      network: false,
      position: false
    })

    // 功能对比数据
    const comparisonFeatures = ref([
      { name: '语音交互', ai: true, multimodal: true },
      { name: '视频分析', ai: false, multimodal: true },
      { name: '表情识别', ai: false, multimodal: true },
      { name: '肢体语言分析', ai: false, multimodal: true },
      { name: '实时反馈', ai: true, multimodal: true },
      { name: '详细报告', ai: true, multimodal: true },
      { name: '个性化建议', ai: true, multimodal: true },
      { name: '情绪分析', ai: false, multimodal: true }
    ])

    // 最近面试记录
    const recentRecords = ref([
      {
        id: 1,
        type: 'multimodal',
        title: '多模态智能体面试',
        date: '2024-01-15',
        duration: '25分钟',
        score: 88
      },
      {
        id: 2,
        type: 'ai',
        title: 'AI面试 - 前端开发',
        date: '2024-01-12',
        duration: '18分钟',
        score: 82
      },
      {
        id: 3,
        type: 'ai',
        title: 'AI面试 - 项目管理',
        date: '2024-01-10',
        duration: '22分钟',
        score: 79
      }
    ])

    // 计算属性
    const allChecklistCompleted = computed(() => {
      const requiredItems = selectedMode.value === 'multimodal'
        ? Object.values(checklist.value)
        : Object.values(checklist.value).slice(0, -1) // 排除position项
      return requiredItems.every(item => item === true)
    })

    // 方法
    const startAIInterview = () => {
      selectedMode.value = 'ai'
      preparationDialogVisible.value = true
      // 重置清单
      Object.keys(checklist.value).forEach(key => {
        checklist.value[key] = false
      })
    }

    const startMultimodalAgent = () => {
      selectedMode.value = 'multimodal'
      preparationDialogVisible.value = true
      // 重置清单
      Object.keys(checklist.value).forEach(key => {
        checklist.value[key] = false
      })
    }

    // 新增：跳转到模拟面试页面
    const gotoSimulatExam = () => {
      router.push('/beforeExam')
    }

    const confirmStart = () => {
      preparationDialogVisible.value = false

      if (selectedMode.value === 'ai') {
        // 跳转到AI面试页面
        router.push('/si')
      } else {
        // 跳转到多模态智能体页面
        router.push('/applicant/multimodal-interview')
      }
    }

    const viewReport = (record) => {
      // 查看面试报告
      router.push({
        name: 'InterviewReport',
        params: { recordId: record.id }
      })
    }

    return {
      totalInterviews,
      averageScore,
      improvementRate,
      preparationDialogVisible,
      selectedMode,
      checklist,
      comparisonFeatures,
      recentRecords,
      allChecklistCompleted,
      startAIInterview,
      startMultimodalAgent,
      gotoSimulatExam, // 导出新增方法
      confirmStart,
      viewReport
    }
  }
}
</script>
<style scoped>
.interview-page {
  padding: 24px;
  background: linear-gradient(135deg, #f5f9ff 0%, #e0efff 100%);
  min-height: 100vh;
}

/* 页面头部样式 */
.page-header {
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

.stat-item {
  text-align: center;
  padding: 0 20px;
  position: relative;
}

.stat-item:not(:last-child):after {
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

/* 面试选项区域 */
.interview-options {
  margin-bottom: 40px;
}

.options-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.option-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
}

.option-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(26, 111, 196, 0.2);
  border-color: #1a6fc4;
}

.option-card:before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: linear-gradient(135deg, #e0f0ff 0%, #d0e6ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1a6fc4;
  font-size: 28px;
}

.card-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  background: #e8f7f0;
  color: #36c28f;
}

.card-badge.primary {
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  color: white;
}

.card-badge.new {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff9a9a 100%);
  color: white;
}

.card-content h3 {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 12px 0;
}

.card-content p {
  color: #5a84b3;
  line-height: 1.6;
  margin-bottom: 20px;
}

.card-features {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #5a84b3;
}

.feature-item .el-icon {
  color: #1a6fc4;
  font-size: 16px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #e6f1ff;
}

.difficulty-level {
  display: flex;
  align-items: center;
  gap: 8px;
}

.level-label {
  font-size: 14px;
  color: #5a84b3;
}

.level-dots {
  display: flex;
  gap: 4px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #e0e0e0;
}

.dot.active {
  background: #1a6fc4;
}

.duration {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #5a84b3;
  font-size: 14px;
}

.duration .el-icon {
  color: #1a6fc4;
}

/* 功能对比区域 */
.comparison-section {
  margin-bottom: 40px;
}

.comparison-section h2 {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 24px;
  text-align: center;
}

.comparison-table {
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.12);
}

.comparison-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  background: linear-gradient(135deg, #f0f7ff 0%, #e0efff 100%);
  padding: 20px;
  font-weight: 600;
  color: #1a6fc4;
}

.comparison-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  padding: 16px 20px;
  border-bottom: 1px solid #e6f1ff;
  align-items: center;
}

.comparison-row:last-child {
  border-bottom: none;
}

.feature-name {
  color: #2c3e50;
  font-weight: 500;
}

.ai-support,
.multimodal-support {
  text-align: center;
}

.check-icon {
  color: #36c28f;
  font-size: 18px;
}

.close-icon {
  color: #e74c3c;
  font-size: 18px;
}

/* 最近面试记录 */
.recent-interviews {
  margin-bottom: 40px;
}

.recent-interviews h2 {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 24px;
  text-align: center;
}

.interview-history {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.history-item {
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(26, 111, 196, 0.1);
  transition: all 0.3s ease;
}

.history-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(26, 111, 196, 0.15);
}

.history-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  background: linear-gradient(135deg, #e0f0ff 0%, #d0e6ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1a6fc4;
  font-size: 20px;
}

.history-content {
  flex: 1;
}

.history-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 6px;
}

.history-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #5a84b3;
}

.history-score {
  text-align: center;
  padding: 0 20px;
}

.score-value {
  font-size: 20px;
  font-weight: 700;
  color: #1a6fc4;
  margin-bottom: 4px;
}

.score-label {
  font-size: 12px;
  color: #5a84b3;
}

.history-actions .el-button {
  border-radius: 8px;
  font-weight: 500;
}

/* 面试准备弹窗样式 */
:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(26, 111, 196, 0.2);
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f0f7ff 0%, #e0efff 100%);
  margin: 0;
  padding: 20px 24px;
}

:deep(.el-dialog__title) {
  color: #1a6fc4;
  font-weight: 600;
  font-size: 20px;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

.preparation-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.preparation-checklist h3,
.preparation-tips h3 {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 16px;
}

.checklist-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.checklist-item {
  display: flex;
  align-items: center;
}

:deep(.el-checkbox) {
  color: #2c3e50;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border-color: #1a6fc4;
}

.preparation-tips ul {
  padding-left: 20px;
  color: #5a84b3;
}

.preparation-tips li {
  margin-bottom: 8px;
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

.dialog-footer .el-button--primary:disabled {
  background: #c2dcff;
  color: white;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .interview-page {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    text-align: center;
    padding: 24px 20px;
  }

  .header-stats {
    flex-direction: column;
    gap: 20px;
  }

  .stat-item:after {
    display: none;
  }

  .options-grid {
    grid-template-columns: 1fr;
  }

  .comparison-header,
  .comparison-row {
    grid-template-columns: 1fr;
    gap: 10px;
    text-align: center;
  }

  .history-item {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .history-meta {
    justify-content: center;
  }

  :deep(.el-dialog) {
    width: 90% !important;
    margin: 20px auto;
  }
}
</style>
