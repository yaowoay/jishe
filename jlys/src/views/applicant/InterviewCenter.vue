<template>
  <div class="interview-center">
    <div class="header">
      <h2>面试中心</h2>
      <p>管理您的面试安排</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ statistics.waiting || 0 }}</div>
          <div class="stat-label">待面试</div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ statistics.completed || 0 }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-number">{{ statistics.pending || 0 }}</div>
          <div class="stat-label">待定</div>
        </div>
      </el-card>
    </div>

    <!-- 筛选器 -->
    <el-card class="filter-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable>
            <el-option label="已投递" value="pending" />
            <el-option label="已查看" value="reviewed" />
            <el-option label="待笔试" value="待笔试" />
            <el-option label="待面试" value="待面试" />
            <el-option label="面试邀请" value="interview" />
            <el-option label="已接受面试" value="interview_accepted" />
            <el-option label="接受面试" value="接受面试" />
            <el-option label="面试中" value="面试中" />
            <el-option label="已录用" value="hired" />
            <el-option label="淘汰" value="淘汰" />
            <el-option label="面试完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="resetSearch" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 面试列表 -->
    <el-card>
      <el-table
          :data="interviewList"
          v-loading="loading"
          stripe
          style="width: 100%"
          empty-text="暂无面试安排"
      >
        <el-table-column label="职位" min-width="200">
          <template #default="{ row }">
            <div class="job-info">
              <div class="job-title">{{ row.jobTitle }}</div>
              <div class="job-type">{{ row.jobType }} · {{ row.jobLocation }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="公司" width="150">
          <template #default="{ row }">
            <div class="company-info">
              <div class="company-name">{{ row.companyName }}</div>
              <div class="company-location">{{ row.companyLocation }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ row.statusDisplayName }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="申请时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.applyTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <!-- 待笔试状态：可开始笔试 -->
              <template v-if="row.status === '待笔试'">
                <el-button
                    type="primary"
                    size="small"
                    @click="startExam(row)"
                    icon="Edit"
                >
                  开始笔试
                </el-button>
              </template>

              <!-- 待面试状态：可开始面试 -->
              <template v-if="row.status === '待面试'">
                <el-button
                    type="primary"
                    size="small"
                    @click="startInterview(row)"
                    icon="VideoPlay"
                >
                  开始面试
                </el-button>
              </template>

              <!-- 面试邀请状态：可接受或拒绝面试 -->
              <template v-if="row.status === 'interview'">
                <el-button
                    type="success"
                    size="small"
                    @click="acceptInterview(row)"
                >
                  接受面试
                </el-button>
                <el-button
                    type="danger"
                    size="small"
                    @click="rejectInterview(row)"
                >
                  拒绝面试
                </el-button>
              </template>

              <!-- 已接受面试：可去面试 -->
              <el-button
                  v-if="row.status === '接受面试'"
                  type="primary"
                  size="small"
                  @click="goToInterview(row)"
                  :icon="VideoPlay"
              >
                去面试
              </el-button>

              <!-- 面试中状态：可去面试 -->
              <!--              <el-button-->
              <!--                v-if="row.status === '接受面试'"-->
              <!--                type="primary"-->
              <!--                size="small"-->
              <!--                @click="goToOfficialExam(row)"-->
              <!--                :icon="VideoPlay"-->
              <!--              >-->
              <!--                去面试-->
              <!--              </el-button>-->

              <!-- 面试进度按钮：所有状态都有 -->
              <el-button
                  type="warning"
                  size="small"
                  @click="viewProgress(row)"
                  :icon="Document"
              >
                面试进度
              </el-button>

              <!-- 详情按钮：所有状态都有 -->
              <el-button
                  type="info"
                  size="small"
                  @click="viewDetail(row)"
                  :icon="View"
              >
                详情
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
        v-model="detailDialogVisible"
        title="投递详情"
        width="700px"
    >
      <div v-if="selectedInterview" class="interview-detail">
        <div class="detail-section">
          <h3>职位信息</h3>
          <div class="info-item">
            <strong>职位名称：</strong>{{ selectedInterview.jobTitle }}
          </div>
          <div class="info-item">
            <strong>职位类型：</strong>{{ selectedInterview.jobType }}
          </div>
          <div class="info-item">
            <strong>工作地点：</strong>{{ selectedInterview.jobLocation }}
          </div>
          <div class="info-item">
            <strong>薪资范围：</strong>{{ selectedInterview.minSalary }}-{{ selectedInterview.maxSalary }}元
          </div>
          <div class="info-item" v-if="selectedInterview.jobDescription">
            <strong>职位描述：</strong>
            <div class="description-text">{{ selectedInterview.jobDescription }}</div>
          </div>
        </div>

        <div class="detail-section">
          <h3>公司信息</h3>
          <div class="info-item">
            <strong>公司名称：</strong>{{ selectedInterview.companyName }}
          </div>
          <div class="info-item">
            <strong>公司地址：</strong>{{ selectedInterview.companyLocation }}
          </div>
          <div class="info-item" v-if="selectedInterview.companyDescription">
            <strong>公司简介：</strong>
            <div class="description-text">{{ selectedInterview.companyDescription }}</div>
          </div>
        </div>

        <div class="detail-section">
          <h3>申请信息</h3>
          <div class="info-item">
            <strong>当前状态：</strong>
            <el-tag :type="getStatusTagType(selectedInterview.status)">
              {{ selectedInterview.statusDisplayName }}
            </el-tag>
          </div>
          <div class="info-item">
            <strong>申请时间：</strong>{{ formatDateTime(selectedInterview.applyTime) }}
          </div>
          <div class="info-item" v-if="selectedInterview.aiEvaluationScore">
            <strong>AI评分：</strong>{{ selectedInterview.aiEvaluationScore }}分
          </div>
          <!--          <div class="info-item" v-if="selectedInterview.feedback">-->
          <!--            <strong>反馈：</strong>{{ selectedInterview.feedback }}-->
          <!--          </div>-->
        </div>

        <div class="detail-section" v-if="selectedInterview.resumeFilename">
          <h3>简历信息</h3>
          <div class="info-item">
            <strong>简历文件：</strong>{{ selectedInterview.resumeFilename }}
          </div>
          <div class="info-item">
            <strong>上传时间：</strong>{{ formatDateTime(selectedInterview.resumeUploadDate) }}
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
            v-if="selectedInterview && selectedInterview.status === 'interview'"
            type="success"
            @click="acceptInterview(selectedInterview); detailDialogVisible = false"
        >
          接受面试
        </el-button>
        <el-button
            v-if="selectedInterview && selectedInterview.status === 'interview'"
            type="danger"
            @click="rejectInterview(selectedInterview); detailDialogVisible = false"
        >
          拒绝面试
        </el-button>
        <el-button
            v-if="selectedInterview && selectedInterview.status === 'interview_accepted'"
            type="primary"
            @click="goToInterview(selectedInterview)"
        >
          去面试
        </el-button>
        <el-button
            v-if="selectedInterview && selectedInterview.status === '面试中'"
            type="primary"
            @click="goToOfficialExam(selectedInterview); detailDialogVisible = false"
        >
          去面试
        </el-button>
      </template>
    </el-dialog>

    <!-- 面试进度对话框 -->
    <el-dialog
        v-model="progressDialogVisible"
        title="面试进度"
        width="800px"
        :close-on-click-modal="false"
    >
      <div v-if="selectedProgress" class="progress-content">
        <!-- 整体进度概览 -->
        <div class="progress-overview">
          <h3>整体进度</h3>
          <el-steps :active="getProgressStep(selectedProgress)" finish-status="success">
            <el-step title="申请投递" description="已投递简历"></el-step>
            <el-step title="笔试阶段" description="在线笔试"></el-step>
            <el-step title="面试阶段" description="视频面试"></el-step>
            <el-step title="结果确认" description="最终结果"></el-step>
          </el-steps>
        </div>

        <!-- 笔试进度 -->
        <div class="test-progress-section">
          <h3>📝 笔试进度</h3>
          <el-card class="progress-card">
            <div class="progress-item">
              <div class="progress-header">
                <span class="progress-title">笔试状态</span>
                <el-tag :type="getTestStatusType(selectedProgress.writtenTestStatus)">
                  {{ getTestStatusText(selectedProgress.writtenTestStatus) }}
                </el-tag>
              </div>

              <div v-if="selectedProgress.writtenTestStatus !== 'not_started'" class="progress-details">
                <div class="detail-item" v-if="selectedProgress.writtenTestScore !== null">
                  <span class="label">笔试得分：</span>
                  <span class="value score">{{ selectedProgress.writtenTestScore }}分</span>
                </div>

                <div class="detail-item" v-if="selectedProgress.writtenTestPassed !== null">
                  <span class="label">是否通过：</span>
                  <el-tag :type="selectedProgress.writtenTestPassed ? 'success' : 'danger'">
                    {{ selectedProgress.writtenTestPassed ? '通过' : '未通过' }}
                  </el-tag>
                </div>

                <div class="detail-item" v-if="selectedProgress.writtenTestCompletedAt">
                  <span class="label">完成时间：</span>
                  <span class="value">{{ formatDateTime(selectedProgress.writtenTestCompletedAt) }}</span>
                </div>

                <div class="detail-actions" v-if="selectedProgress.writtenTestResultJson">
                  <el-button type="primary" size="small" @click="viewTestReport">
                    查看详细报告
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 面试进度 -->
        <div class="interview-progress-section">
          <h3>🎯 面试进度</h3>
          <el-card class="progress-card">
            <div v-if="!selectedProgress.writtenTestPassed && selectedProgress.writtenTestStatus === 'failed'" class="no-interview">
              <el-result
                  icon="warning"
                  title="抱歉，您未通过笔试"
                  sub-title="暂时无法进行面试，请继续努力！"
              >
              </el-result>
            </div>

            <div v-else class="progress-item">
              <div class="progress-header">
                <span class="progress-title">面试状态</span>
                <el-tag :type="getInterviewStatusType(selectedProgress.interviewStatus)">
                  {{ getInterviewStatusText(selectedProgress.interviewStatus) }}
                </el-tag>
              </div>

              <div v-if="selectedProgress.interviewStatus !== 'not_started'" class="progress-details">
                <div class="detail-item" v-if="selectedProgress.interviewScore !== null">
                  <span class="label">面试得分：</span>
                  <span class="value score">{{ selectedProgress.interviewScore }}分</span>
                </div>

                <div class="detail-item" v-if="selectedProgress.interviewPassed !== null">
                  <span class="label">是否通过：</span>
                  <el-tag :type="selectedProgress.interviewPassed ? 'success' : 'danger'">
                    {{ selectedProgress.interviewPassed ? '通过' : '未通过' }}
                  </el-tag>
                </div>

                <div class="detail-item" v-if="selectedProgress.interviewScheduledAt">
                  <span class="label">安排时间：</span>
                  <span class="value">{{ formatDateTime(selectedProgress.interviewScheduledAt) }}</span>
                </div>

                <div class="detail-item" v-if="selectedProgress.interviewCompletedAt">
                  <span class="label">完成时间：</span>
                  <span class="value">{{ formatDateTime(selectedProgress.interviewCompletedAt) }}</span>
                </div>

                <div class="detail-actions" v-if="selectedProgress.interviewResultJson">
                  <el-button type="primary" size="small" @click="viewInterviewReport">
                    查看详细报告
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 备注信息 -->
        <div v-if="selectedProgress.notes" class="notes-section">
          <h3>📋 备注信息</h3>
          <el-card class="progress-card">
            <p>{{ selectedProgress.notes }}</p>
          </el-card>
        </div>
      </div>

      <template #footer>
        <el-button @click="progressDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 笔试报告对话框 -->
    <el-dialog
        v-model="testReportDialogVisible"
        title="笔试详细报告"
        width="900px"
        :close-on-click-modal="false"
    >
      <div v-if="testReportData" class="exam-result-container">
        <!-- 复用原有的笔试结果展示 -->
        <div class="result-header">
          <div class="score-display">
            <div class="score-circle" :class="getScoreClass(testReportData.score)">
              {{ testReportData.score }}
            </div>
            <div class="score-info">
              <h1>笔试结果</h1>
              <p class="score-text">{{ getScoreText(testReportData.score) }}</p>
              <p class="completion-time">完成时间：{{ formatTime(testReportData.completionTime) }}</p>
            </div>
          </div>
          <div class="result-stats">
            <div class="stat-item">
              <span class="stat-label">正确题数</span>
              <span class="stat-value correct">{{ testReportData.correctAnswers }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">错误题数</span>
              <span class="stat-value wrong">{{ testReportData.wrongAnswers }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">总题数</span>
              <span class="stat-value total">{{ testReportData.totalQuestions }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">正确率</span>
              <span class="stat-value accuracy">{{ Math.round((testReportData.correctAnswers / testReportData.totalQuestions) * 100) }}%</span>
            </div>
          </div>
        </div>

        <!-- 详细解析 -->
        <div class="detailed-results">
          <h3>详细解析</h3>
          <div class="questions-list">
            <div
              v-for="(question, index) in testReportData.questionResults"
              :key="question.questionId"
              class="question-item"
              :class="{ 'correct': question.isCorrect, 'wrong': !question.isCorrect }"
            >
              <div class="question-header">
                <span class="question-number">第 {{ index + 1 }} 题</span>
                <span class="question-type">{{ question.type === 'choice' ? '选择题' : '判断题' }}</span>
                <el-tag :type="question.isCorrect ? 'success' : 'danger'" size="small">
                  {{ question.isCorrect ? '正确' : '错误' }}
                </el-tag>
              </div>

              <!-- 题目内容和解析 -->
              <div class="question-content">
                {{ question.type === 'choice' ? question.questionContent : question.statement }}
              </div>

              <!-- 答案对比 -->
              <div class="answer-comparison">
                <div class="answer-row">
                  <span class="answer-label">正确答案：</span>
                  <span class="correct-answer">{{ question.correctAnswer }}</span>
                </div>
                <div class="answer-row">
                  <span class="answer-label">您的答案：</span>
                  <span :class="question.isCorrect ? 'user-correct' : 'user-wrong'">
                    {{ question.userAnswer || '未作答' }}
                  </span>
                </div>
              </div>

              <!-- 解析 -->
              <div class="explanation">
                <h4>解析：</h4>
                <p>{{ question.explanation }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="loading-content">
        <el-empty description="暂无报告数据" />
      </div>

      <template #footer>
        <el-button @click="testReportDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  VideoPlay,
  View,
  Document
} from '@element-plus/icons-vue'
import { getSubmittedJobs } from '@/api/resume'
import { acceptInterviewInvitation, rejectInterviewInvitation } from '@/api/applicant'
import { getInterviewProgress, updateInterviewProgressStatus } from '@/api/interview'
import { getTestResult } from '@/api/writtenTestExam'

export default {
  name: 'InterviewCenter',
  components: {
    Search,
    Refresh,
    VideoPlay,
    View,
    Document
  },
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const detailDialogVisible = ref(false)
    const progressDialogVisible = ref(false)
    const testReportDialogVisible = ref(false)
    const interviewList = ref([])
    const selectedInterview = ref(null)
    const selectedProgress = ref(null)
    const testReportData = ref(null)

    const statistics = ref({
      waiting: 0,
      completed: 0,
      pending: 0
    })

    const searchForm = reactive({
      status: ''
    })

    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0
    })

    // 加载面试列表
    const loadInterviewList = async () => {
      try {
        loading.value = true

        // 调用API获取投递记录
        const response = await getSubmittedJobs()
        if (response.success) {
          // 过滤出有面试相关状态的记录
          const filteredData = response.data.filter(item => {
            if (searchForm.status) {
              return item.status === searchForm.status
            }
            return true
          })

          interviewList.value = filteredData
          pagination.total = filteredData.length

          // 更新统计数据
          updateStatistics()
        } else {
          ElMessage.error(response.message || '获取投递记录失败')
        }
      } catch (error) {
        console.error('Load interview list error:', error)
        ElMessage.error('获取投递记录失败')
      } finally {
        loading.value = false
      }
    }

    // 更新统计数据
    const updateStatistics = () => {
      const stats = {
        waiting: 0,
        completed: 0,
        pending: 0
      }

      interviewList.value.forEach(item => {
        if (item.status === 'interview' || item.status === 'interview_accepted' || item.status === '面试中') {
          stats.waiting++
        } else if (item.status === 'hired' || item.status === 'rejected') {
          stats.completed++
        } else if (item.status === 'completed') {
          stats.pending++
        }
      })

      statistics.value = stats
    }

    // 接受面试
    const acceptInterview = async (interview) => {
      try {
        await ElMessageBox.confirm(
          `确定接受 ${interview.jobTitle} 的面试邀请吗？`,
          '确认接受面试',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        // 调用API接受面试邀请
        const response = await acceptInterviewInvitation(interview.applicationId)

        if (response.success) {
          // 更新本地状态
          interview.status = 'interview_accepted'
          interview.statusDisplayName = '已接受面试'

          ElMessage.success('已接受面试邀请')
          updateStatistics()

          // 重新加载列表以确保数据同步
          loadInterviewList()
        } else {
          ElMessage.error(response.message || '接受面试邀请失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Accept interview error:', error)
          ElMessage.error('接受面试失败')
        }
      }
    }

    // 拒绝面试
    const rejectInterview = async (interview) => {
      try {
        await ElMessageBox.confirm(
          `确定拒绝 ${interview.jobTitle} 的面试邀请吗？`,
          '确认拒绝面试',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        // 调用API拒绝面试邀请
        const response = await rejectInterviewInvitation(interview.applicationId)

        if (response.success) {
          // 更新本地状态
          interview.status = 'rejected'
          interview.statusDisplayName = '已拒绝'

          ElMessage.success('已拒绝面试邀请')
          updateStatistics()

          // 重新加载列表以确保数据同步
          loadInterviewList()
        } else {
          ElMessage.error(response.message || '拒绝面试邀请失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Reject interview error:', error)
          ElMessage.error('拒绝面试失败')
        }
      }
    }

    // 去面试
    const goToInterview = (interview) => {
      // 跳转到面试页面
      router.push({
        name: 'BeforeExam',
        query: {
          applicationId: interview.applicationId,
          jobId: interview.jobId
        }
      })
    }

    // 去正式面试
    const goToOfficialExam = (interview) => {
      // 跳转到正式面试页面
      router.push({
        name: 'OfficialExam',
        query: {
          applicationId: interview.applicationId,
          jobId: interview.jobId,
          jobTitle: interview.jobTitle,
          companyName: interview.companyName
        }
      })
    }

    // 查看详情
    const viewDetail = (interview) => {
      selectedInterview.value = interview
      detailDialogVisible.value = true
    }

    // 查看面试进度
    const viewProgress = async (interview) => {
      try {
        loading.value = true

        // 调用API获取面试进度信息
        const response = await getInterviewProgress(interview.applicationId)

        if (response.success && response.data) {
          selectedProgress.value = response.data
          progressDialogVisible.value = true
        } else {
          // 如果没有进度记录，显示默认状态
          selectedProgress.value = {
            applicationId: interview.applicationId,
            writtenTestStatus: 'not_started',
            interviewStatus: 'not_started',
            overallStatus: 'pending',
            finalResult: 'pending'
          }
          progressDialogVisible.value = true
        }
      } catch (error) {
        console.error('获取面试进度失败:', error)
        ElMessage.error('获取面试进度失败')
      } finally {
        loading.value = false
      }
    }

    // 搜索
    const handleSearch = () => {
      pagination.current = 1
      loadInterviewList()
    }

    // 重置搜索
    const resetSearch = () => {
      searchForm.status = ''
      pagination.current = 1
      loadInterviewList()
    }

    // 分页处理
    const handleSizeChange = (size) => {
      pagination.size = size
      pagination.current = 1
      loadInterviewList()
    }

    const handleCurrentChange = (current) => {
      pagination.current = current
      loadInterviewList()
    }

    // 获取状态标签类型
    const getStatusTagType = (status) => {
      const typeMap = {
        pending: 'info',
        reviewed: 'primary',
        interview: 'warning',
        interview_accepted: 'success',
        '面试中': 'warning',
        '待笔试': 'primary',
        '待面试': 'success',  // 待面试改为成功色，表示通过了笔试
        '接受面试': 'success',
        hired: 'success',
        rejected: 'danger',
        '淘汰': 'danger',
        completed: 'info'
      }
      return typeMap[status] || 'info'
    }

    // 开始笔试
    const startExam = (application) => {
      console.log('开始笔试:', application)
      // 跳转到笔试页面
      router.push({
        name: 'ExamCenter',
        params: {
          applicationId: application.applicationId,
          jobId: application.jobId
        },
        query: {
          jobTitle: application.jobTitle,
          companyName: application.companyName
        }
      })
    }

    // 开始面试
    const startInterview = (application) => {
      console.log('开始面试:', application)
      // 跳转到面试页面
      router.push({
        name: 'BeforeExam',
        query: {
          applicationId: application.applicationId,
          jobId: application.jobId,
          jobTitle: application.jobTitle,
          companyName: application.companyName
        }
      })
    }

    // 格式化日期时间
    const formatDateTime = (dateTime) => {
      if (!dateTime) return ''
      return new Date(dateTime).toLocaleString('zh-CN')
    }

    // 获取进度步骤
    const getProgressStep = (progress) => {
      if (!progress) return 0

      switch (progress.overallStatus) {
      case 'pending': return 1
      case 'written_test': return 2
      case 'interview': return 3
      case 'completed': return 4
      case 'rejected': return progress.writtenTestStatus === 'failed' ? 2 : 3
      default: return 1
      }
    }

    // 获取笔试状态类型
    const getTestStatusType = (status) => {
      const typeMap = {
        'not_started': 'info',
        'in_progress': 'warning',
        'completed': 'primary',
        'passed': 'success',
        'failed': 'danger'
      }
      return typeMap[status] || 'info'
    }

    // 获取笔试状态文本
    const getTestStatusText = (status) => {
      const textMap = {
        'not_started': '未开始',
        'in_progress': '进行中',
        'completed': '已完成',
        'passed': '已通过',
        'failed': '未通过'
      }
      return textMap[status] || '未知状态'
    }

    // 获取面试状态类型
    const getInterviewStatusType = (status) => {
      const typeMap = {
        'not_started': 'info',
        'scheduled': 'warning',
        'in_progress': 'primary',
        'completed': 'primary',
        'passed': 'success',
        'failed': 'danger'
      }
      return typeMap[status] || 'info'
    }

    // 获取面试状态文本
    const getInterviewStatusText = (status) => {
      const textMap = {
        'not_started': '未开始',
        'scheduled': '已安排',
        'in_progress': '进行中',
        'completed': '已完成',
        'passed': '已通过',
        'failed': '未通过'
      }
      return textMap[status] || '未知状态'
    }

    // 查看笔试报告
    const viewTestReport = async () => {
      try {
        if (!selectedProgress.value?.applicationId) {
          ElMessage.error('缺少申请ID')
          return
        }

        loading.value = true

        // 调用API获取笔试结果详情
        const response = await getTestResult(selectedProgress.value.applicationId)

        if (response.success && response.data) {
          // 直接使用API返回的数据，这个数据格式和ExamCenter中的examResult一样
          testReportData.value = response.data
          testReportDialogVisible.value = true
        } else {
          ElMessage.error('获取笔试报告失败')
        }
      } catch (error) {
        console.error('获取笔试报告失败:', error)
        ElMessage.error('获取笔试报告失败')
      } finally {
        loading.value = false
      }
    }

    // 查看面试报告
    const viewInterviewReport = () => {
      if (!selectedProgress.value?.applicationId) {
        ElMessage.error('缺少申请ID')
        return
      }

      // 跳转到面试报告页面
      router.push({
        name: 'InterviewReport',
        params: {
          applicationId: selectedProgress.value.applicationId
        }
      })

      // 关闭进度对话框
      progressDialogVisible.value = false
    }

    // 复用ExamCenter中的方法
    // 获取分数等级样式
    const getScoreClass = (score) => {
      if (score >= 90) return 'excellent'
      if (score >= 80) return 'good'
      if (score >= 70) return 'average'
      if (score >= 60) return 'basic'
      return 'poor'
    }

    // 获取分数等级文本
    const getScoreText = (score) => {
      if (score >= 90) return '优秀'
      if (score >= 80) return '良好'
      if (score >= 70) return '一般'
      if (score >= 60) return '及格'
      return '不及格'
    }

    // 格式化时间
    const formatTime = (seconds) => {
      const hours = Math.floor(seconds / 3600)
      const minutes = Math.floor((seconds % 3600) / 60)
      const secs = seconds % 60

      if (hours > 0) {
        return `${hours}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
      } else {
        return `${minutes}:${secs.toString().padStart(2, '0')}`
      }
    }

    // 组件挂载时加载数据
    onMounted(() => {
      loadInterviewList()
    })

    return {
      loading,
      detailDialogVisible,
      progressDialogVisible,
      testReportDialogVisible,
      interviewList,
      selectedInterview,
      selectedProgress,
      testReportData,
      statistics,
      searchForm,
      pagination,
      loadInterviewList,
      updateStatistics,
      acceptInterview,
      rejectInterview,
      goToInterview,
      goToOfficialExam,
      startExam,
      startInterview,
      viewDetail,
      viewProgress,
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      getStatusTagType,
      formatDateTime,
      getProgressStep,
      getTestStatusType,
      getTestStatusText,
      getInterviewStatusType,
      getInterviewStatusText,
      viewTestReport,
      viewInterviewReport,
      getScoreClass,
      getScoreText,
      formatTime,
      Search,
      Refresh,
      VideoPlay,
      View,
      Document
    }
  }
}
</script>

<style scoped>
.interview-center {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.header h2 {
  margin: 0 0 8px 0;
  color: #333;
}

.header p {
  margin: 0;
  color: #666;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-content {
  padding: 10px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.filter-card {
  margin-bottom: 20px;
}

.job-info {
  line-height: 1.5;
}

.job-title {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.job-type {
  font-size: 12px;
  color: #999;
}

.company-info {
  line-height: 1.5;
}

.company-name {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.company-location {
  font-size: 12px;
  color: #999;
}

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-buttons .el-button {
  margin: 0;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

.interview-detail {
  max-height: 400px;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h3 {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 16px;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.info-item {
  margin-bottom: 8px;
  line-height: 1.6;
}

.info-item strong {
  color: #666;
  margin-right: 8px;
}

.description-text {
  margin-top: 4px;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 14px;
  line-height: 1.6;
  color: #666;
}

/* 面试进度对话框样式 */
.progress-content {
  max-height: 600px;
  overflow-y: auto;
}

.progress-overview {
  margin-bottom: 24px;
}

.progress-overview h3 {
  margin-bottom: 16px;
  color: #333;
  font-size: 16px;
}

.test-progress-section,
.interview-progress-section,
.notes-section {
  margin-bottom: 24px;
}

.test-progress-section h3,
.interview-progress-section h3,
.notes-section h3 {
  margin-bottom: 12px;
  color: #333;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-card {
  border-radius: 8px;
}

.progress-item {
  padding: 16px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.progress-title {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.progress-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-item .label {
  font-weight: 500;
  color: #666;
  min-width: 80px;
}

.detail-item .value {
  color: #333;
}

.detail-item .value.score {
  font-weight: 600;
  color: #409eff;
  font-size: 16px;
}

.detail-actions {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.no-interview {
  text-align: center;
  padding: 20px;
}

.no-interview .el-result {
  padding: 20px 0;
}

/* 步骤条样式优化 */
:deep(.el-steps) {
  margin: 20px 0;
}

:deep(.el-step__title) {
  font-size: 14px;
  line-height: 1.4;
}

:deep(.el-step__description) {
  font-size: 12px;
  color: #999;
}

/* 笔试报告对话框样式 - 复用ExamCenter的样式 */
.exam-result-container {
  max-height: 600px;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
}

.result-header {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.score-display {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
}

.score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  font-weight: bold;
  color: white;
  margin-right: 30px;
}

.score-circle.excellent { background: #67c23a; }
.score-circle.good { background: #409eff; }
.score-circle.average { background: #e6a23c; }
.score-circle.basic { background: #f56c6c; }
.score-circle.poor { background: #909399; }

.score-info h1 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 28px;
}

.score-text {
  font-size: 20px;
  color: #606266;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.completion-time {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.result-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-label {
  display: block;
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: bold;
}

.stat-value.correct { color: #67c23a; }
.stat-value.wrong { color: #f56c6c; }
.stat-value.total { color: #409eff; }
.stat-value.time { color: #e6a23c; }
.stat-value.accuracy { color: #9c27b0; }

.detailed-results {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.detailed-results h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  border-bottom: 2px solid #e1e8ed;
  padding-bottom: 10px;
}

.question-item {
  border: 1px solid #e1e8ed;
  border-radius: 8px;
  margin-bottom: 20px;
  padding: 20px;
}

.question-item.correct {
  border-color: #67c23a;
  background: #f0f9ff;
}

.question-item.wrong {
  border-color: #f56c6c;
  background: #fef0f0;
}

.question-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.question-number {
  font-weight: bold;
  color: #2c3e50;
}

.question-type {
  font-size: 12px;
  color: #909399;
}

.question-content {
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 15px;
  color: #2c3e50;
}

.answer-comparison {
  background: #f8f9fa;
  border-radius: 6px;
  padding: 15px;
  margin-bottom: 15px;
}

.answer-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.answer-row:last-child {
  margin-bottom: 0;
}

.answer-label {
  font-weight: bold;
  margin-right: 10px;
  min-width: 80px;
}

.correct-answer {
  color: #67c23a;
  font-weight: bold;
}

.user-correct {
  color: #67c23a;
  font-weight: bold;
}

.user-wrong {
  color: #f56c6c;
  font-weight: bold;
}

.explanation {
  background: #ecf5ff;
  border-left: 4px solid #409eff;
  padding: 15px;
  border-radius: 0 6px 6px 0;
}

.explanation h4 {
  margin: 0 0 10px 0;
  color: #409eff;
  font-size: 14px;
}

.explanation p {
  margin: 0;
  line-height: 1.6;
  color: #606266;
}

.loading-content {
  text-align: center;
  padding: 40px 20px;
}
</style>
