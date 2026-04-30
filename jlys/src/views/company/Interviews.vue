<template>
  <div class="interview-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1>面试管理</h1>
        <p>全面管理面试流程、面试官配置和面试数据分析</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" icon="Plus" @click="showCreateDialog">
          创建面试
        </el-button>
        <el-button icon="Setting" @click="showSettingsDialog">
          系统设置
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="statistics-section">
      <div class="stats-grid">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics.pendingInterviews }}</div>
              <div class="stat-label">待面试</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon ongoing">
              <el-icon><VideoPlay /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics.ongoingInterviews }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon completed">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics.completedInterviews }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon average">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics.averageScore }}</div>
              <div class="stat-label">平均分</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <el-tabs v-model="activeTab" class="interview-tabs">
        <!-- 面试列表 -->
        <el-tab-pane label="面试列表" name="interviews">
          <div class="interview-list-section">
            <!-- 搜索和筛选 -->
            <div class="search-section">
              <el-form :model="searchForm" inline class="search-form">
                <el-form-item label="搜索">
                  <el-input
                    v-model="searchForm.keyword"
                    placeholder="搜索应聘者姓名或职位"
                    prefix-icon="Search"
                    clearable
                  />
                </el-form-item>
                <el-form-item label="状态">
                  <el-select v-model="searchForm.status" placeholder="选择状态" clearable>
                    <el-option label="全部" value="" />
                    <el-option label="待面试" value="pending" />
                    <el-option label="进行中" value="ongoing" />
                    <el-option label="已完成" value="completed" />
                    <el-option label="已取消" value="cancelled" />
                  </el-select>
                </el-form-item>
                <el-form-item label="面试类型">
                  <el-select v-model="searchForm.type" placeholder="选择类型" clearable>
                    <el-option label="全部" value="" />
                    <el-option label="初试" value="first" />
                    <el-option label="复试" value="second" />
                    <el-option label="终试" value="final" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleSearch">搜索</el-button>
                  <el-button @click="resetSearch">重置</el-button>
                </el-form-item>
              </el-form>
            </div>

            <!-- 面试列表表格 -->
            <el-table :data="interviewList" v-loading="loading" stripe class="interview-table" style="width: 100%" :fit="true">
              <el-table-column prop="candidateName" label="应聘者" min-width="140" />
              <el-table-column prop="position" label="应聘职位" min-width="220" />
              <el-table-column prop="interviewType" label="面试类型" min-width="120">
                <template #default="{ row }">
                  <el-tag :type="getTypeTagType(row.interviewType)">
                    {{ getTypeText(row.interviewType) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="interviewer" label="面试官" min-width="140" />
              <el-table-column prop="scheduledTime" label="面试时间" min-width="220">
                <template #default="{ row }">
                  {{ formatDateTime(row.scheduledTime) }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" min-width="120">
                <template #default="{ row }">
                  <el-tag :type="getStatusTagType(row.status)">
                    {{ getStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="score" label="评分" min-width="100">
                <template #default="{ row }">
                  <span v-if="row.score" :class="getScoreClass(row.score)">
                    {{ row.score }}
                  </span>
                  <span v-else class="text-gray">-</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" min-width="260" fixed="right">
                <template #default="{ row }">
                  <el-button
                    v-if="row.status === 'pending'"
                    type="primary"
                    size="small"
                    @click="startInterview(row)"
                  >
                    开始面试
                  </el-button>
                  <el-button
                    v-if="row.status === 'ongoing'"
                    type="warning"
                    size="small"
                    @click="continueInterview(row)"
                  >
                    继续面试
                  </el-button>
                  <el-button
                    v-if="row.status === 'completed'"
                    type="success"
                    size="small"
                    @click="viewReport(row)"
                  >
                    查看报告
                  </el-button>
                  <el-button
                    type="info"
                    size="small"
                    @click="viewDetail(row)"
                  >
                    详情
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination-section">
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
          </div>
        </el-tab-pane>

        <!-- 面试官管理 -->
        <el-tab-pane label="面试官管理" name="interviewers">
          <div class="interviewers-section">
            <div class="section-header">
              <h3>面试官列表</h3>
              <el-button type="primary" @click="showAddInterviewerDialog">
                添加面试官
              </el-button>
            </div>

            <el-table :data="interviewerList" stripe class="interviewer-table" style="width: 100%" :fit="true">
              <el-table-column prop="name" label="姓名" min-width="140" />
              <el-table-column prop="department" label="部门" min-width="180" />
              <el-table-column prop="position" label="职位" min-width="180" />
              <el-table-column prop="specialties" label="专业领域" min-width="260">
                <template #default="{ row }">
                  <el-tag
                    v-for="specialty in row.specialties"
                    :key="specialty"
                    size="small"
                    style="margin-right: 5px;"
                  >
                    {{ specialty }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="interviewCount" label="面试次数" min-width="120" />
              <el-table-column prop="averageRating" label="平均评分" min-width="180">
                <template #default="{ row }">
                  <el-rate
                    v-model="row.averageRating"
                    disabled
                    show-score
                    text-color="#ff9900"
                    score-template="{value}"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" min-width="120">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'active' ? 'success' : 'info'">
                    {{ row.status === 'active' ? '活跃' : '休假' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" min-width="180">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="editInterviewer(row)">
                    编辑
                  </el-button>
                  <el-button type="danger" size="small" @click="removeInterviewer(row)">
                    移除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 面试模板 -->
        <el-tab-pane label="面试模板" name="templates">
          <div class="templates-section">
            <div class="section-header">
              <h3>面试模板管理</h3>
              <el-button type="primary" @click="showCreateTemplateDialog">
                创建模板
              </el-button>
            </div>

            <div class="templates-grid">
              <el-card
                v-for="template in templateList"
                :key="template.id"
                class="template-card"
                shadow="hover"
              >
                <template #header>
                  <div class="template-header">
                    <span class="template-title">{{ template.name }}</span>
                    <el-dropdown @command="handleTemplateAction">
                      <el-button type="text" icon="MoreFilled" />
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item :command="`edit-${template.id}`">编辑</el-dropdown-item>
                          <el-dropdown-item :command="`copy-${template.id}`">复制</el-dropdown-item>
                          <el-dropdown-item :command="`delete-${template.id}`" divided>删除</el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>
                </template>

                <div class="template-content">
                  <p class="template-description">{{ template.description }}</p>
                  <div class="template-info">
                    <div class="info-item">
                      <span class="label">适用职位：</span>
                      <span class="value">{{ template.positions.join(', ') }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">题目数量：</span>
                      <span class="value">{{ template.questionCount }}题</span>
                    </div>
                    <div class="info-item">
                      <span class="label">预计时长：</span>
                      <span class="value">{{ template.duration }}分钟</span>
                    </div>
                    <div class="info-item">
                      <span class="label">使用次数：</span>
                      <span class="value">{{ template.usageCount }}次</span>
                    </div>
                  </div>
                  <div class="template-actions">
                    <el-button type="primary" size="small" @click="useTemplate(template)">
                      使用模板
                    </el-button>
                    <el-button size="small" @click="previewTemplate(template)">
                      预览
                    </el-button>
                  </div>
                </div>
              </el-card>
            </div>
          </div>
        </el-tab-pane>

        <!-- 数据分析 -->
        <el-tab-pane label="数据分析" name="analytics">
          <div class="analytics-section">
            <div class="analytics-grid">
              <!-- 面试趋势图 -->
              <el-card class="chart-card">
                <template #header>
                  <h3>面试趋势分析</h3>
                </template>
                <div class="chart-container" ref="trendChart"></div>
              </el-card>

              <!-- 面试官效率分析 -->
              <el-card class="chart-card">
                <template #header>
                  <h3>面试官效率分析</h3>
                </template>
                <div class="chart-container" ref="efficiencyChart"></div>
              </el-card>

              <!-- 职位面试分布 -->
              <el-card class="chart-card">
                <template #header>
                  <h3>职位面试分布</h3>
                </template>
                <div class="chart-container" ref="distributionChart"></div>
              </el-card>

              <!-- 面试通过率 -->
              <el-card class="chart-card">
                <template #header>
                  <h3>面试通过率统计</h3>
                </template>
                <div class="chart-container" ref="passRateChart"></div>
              </el-card>
            </div>

            <!-- 详细数据表格 -->
            <el-card class="data-table-card">
              <template #header>
                <h3>详细数据统计</h3>
              </template>
              <el-table :data="analyticsData" stripe>
                <el-table-column prop="date" label="日期" width="120" />
                <el-table-column prop="totalInterviews" label="总面试数" width="100" />
                <el-table-column prop="completedInterviews" label="已完成" width="100" />
                <el-table-column prop="passedInterviews" label="通过数" width="100" />
                <el-table-column prop="passRate" label="通过率" width="100">
                  <template #default="{ row }">
                    <span :class="getPassRateClass(row.passRate)">
                      {{ row.passRate }}%
                    </span>
                  </template>
                </el-table-column>
                <el-table-column prop="averageScore" label="平均分" width="100" />
                <el-table-column prop="averageDuration" label="平均时长" width="120">
                  <template #default="{ row }">
                    {{ row.averageDuration }}分钟
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 创建面试对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="创建面试"
      width="600px"
      :before-close="closeCreateDialog"
    >
      <el-form :model="createForm" :rules="createFormRules" ref="createFormRef" label-width="100px">
        <el-form-item label="应聘者" prop="candidateId">
          <el-select v-model="createForm.candidateId" placeholder="选择应聘者" style="width: 100%">
            <el-option
              v-for="candidate in candidateList"
              :key="candidate.id"
              :label="candidate.name"
              :value="candidate.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="面试类型" prop="type">
          <el-select v-model="createForm.type" placeholder="选择面试类型">
            <el-option label="初试" value="first" />
            <el-option label="复试" value="second" />
            <el-option label="终试" value="final" />
          </el-select>
        </el-form-item>
        <el-form-item label="面试官" prop="interviewerId">
          <el-select v-model="createForm.interviewerId" placeholder="选择面试官">
            <el-option
              v-for="interviewer in interviewerList"
              :key="interviewer.id"
              :label="interviewer.name"
              :value="interviewer.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="面试时间" prop="scheduledTime">
          <el-date-picker
            v-model="createForm.scheduledTime"
            type="datetime"
            placeholder="选择面试时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="面试模板" prop="templateId">
          <el-select v-model="createForm.templateId" placeholder="选择面试模板（可选）">
            <el-option
              v-for="template in templateList"
              :key="template.id"
              :label="template.name"
              :value="template.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="createForm.notes"
            type="textarea"
            :rows="3"
            placeholder="输入面试备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeCreateDialog">取消</el-button>
          <el-button type="primary" @click="confirmCreate" :loading="createLoading">
            创建面试
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 面试详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="面试详情"
      width="800px"
    >
      <div v-if="selectedInterview" class="interview-detail">
        <div class="detail-section">
          <h4>基本信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">应聘者：</span>
              <span class="value">{{ selectedInterview.candidateName }}</span>
            </div>
            <div class="detail-item">
              <span class="label">应聘职位：</span>
              <span class="value">{{ selectedInterview.position }}</span>
            </div>
            <div class="detail-item">
              <span class="label">面试类型：</span>
              <span class="value">{{ getTypeText(selectedInterview.interviewType) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">面试官：</span>
              <span class="value">{{ selectedInterview.interviewer }}</span>
            </div>
            <div class="detail-item">
              <span class="label">面试时间：</span>
              <span class="value">{{ formatDateTime(selectedInterview.scheduledTime) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">面试状态：</span>
              <el-tag :type="getStatusTagType(selectedInterview.status)">
                {{ getStatusText(selectedInterview.status) }}
              </el-tag>
            </div>
          </div>
        </div>

        <div class="detail-section" v-if="selectedInterview.status === 'completed'">
          <h4>面试结果</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">总分：</span>
              <span class="value" :class="getScoreClass(selectedInterview.score)">
                {{ selectedInterview.score }}分
              </span>
            </div>
            <div class="detail-item">
              <span class="label">面试时长：</span>
              <span class="value">{{ selectedInterview.duration }}分钟</span>
            </div>
            <div class="detail-item">
              <span class="label">评价：</span>
              <span class="value">{{ selectedInterview.evaluation || '暂无评价' }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section" v-if="selectedInterview.notes">
          <h4>备注信息</h4>
          <p>{{ selectedInterview.notes }}</p>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button
            v-if="selectedInterview && selectedInterview.status === 'completed'"
            type="primary"
            @click="downloadReport"
          >
            下载报告
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 系统设置对话框 -->
    <el-dialog
      v-model="settingsDialogVisible"
      title="高校学生就业能力智配平台面试系统设置"
      width="700px"
    >
      <el-form :model="settingsForm" label-width="150px">
        <el-form-item label="默认面试时长">
          <el-input-number
            v-model="settingsForm.defaultDuration"
            :min="15"
            :max="180"
            :step="15"
          />
          <span style="margin-left: 10px;">分钟</span>
        </el-form-item>
        <el-form-item label="自动提醒时间">
          <el-input-number
            v-model="settingsForm.reminderTime"
            :min="5"
            :max="60"
            :step="5"
          />
          <span style="margin-left: 10px;">分钟前</span>
        </el-form-item>
        <el-form-item label="评分标准">
          <el-radio-group v-model="settingsForm.scoringSystem">
            <el-radio label="100">百分制</el-radio>
            <el-radio label="10">十分制</el-radio>
            <el-radio label="5">五分制</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="自动录制">
          <el-switch v-model="settingsForm.autoRecord" />
        </el-form-item>
        <el-form-item label="AI辅助评分">
          <el-switch v-model="settingsForm.aiAssist" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="settingsDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Setting,
  Clock,
  VideoPlay,
  Check,
  TrendCharts,
  Search,
  MoreFilled
} from '@element-plus/icons-vue'

export default {
  name: 'InterviewManagement',
  components: {
    Plus,
    Setting,
    Clock,
    VideoPlay,
    Check,
    TrendCharts,
    Search,
    MoreFilled
  },
  setup() {
    // 响应式数据
    const loading = ref(false)
    const activeTab = ref('interviews')

    // 统计数据
    const statistics = reactive({
      pendingInterviews: 12,
      ongoingInterviews: 3,
      completedInterviews: 45,
      averageScore: 78.5
    })

    // 搜索表单
    const searchForm = reactive({
      keyword: '',
      status: '',
      type: ''
    })

    // 分页
    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0
    })

    // 面试列表
    const interviewList = ref([
      {
        id: 1,
        candidateName: '张三',
        position: '前端开发工程师',
        interviewType: 'first',
        interviewer: '李经理',
        scheduledTime: '2024-01-15 14:00:00',
        status: 'pending',
        score: null,
        duration: null,
        evaluation: null,
        notes: '技术面试，重点考察Vue.js和React技能'
      },
      {
        id: 2,
        candidateName: '李四',
        position: 'Java开发工程师',
        interviewType: 'second',
        interviewer: '王总监',
        scheduledTime: '2024-01-15 15:30:00',
        status: 'ongoing',
        score: null,
        duration: 25,
        evaluation: null,
        notes: '复试，综合能力评估'
      },
      {
        id: 3,
        candidateName: '王五',
        position: 'UI设计师',
        interviewType: 'final',
        interviewer: '陈主管',
        scheduledTime: '2024-01-14 10:00:00',
        status: 'completed',
        score: 85,
        duration: 45,
        evaluation: '表现优秀，设计思维清晰，建议录用',
        notes: '终试，重点考察设计理念和团队协作能力'
      }
    ])

    // 面试官列表
    const interviewerList = ref([
      {
        id: 1,
        name: '李经理',
        department: '技术部',
        position: '技术经理',
        specialties: ['前端开发', 'JavaScript', 'Vue.js'],
        interviewCount: 28,
        averageRating: 4.5,
        status: 'active'
      },
      {
        id: 2,
        name: '王总监',
        department: '技术部',
        position: '技术总监',
        specialties: ['Java', '架构设计', '团队管理'],
        interviewCount: 45,
        averageRating: 4.8,
        status: 'active'
      },
      {
        id: 3,
        name: '陈主管',
        department: '设计部',
        position: '设计主管',
        specialties: ['UI设计', '用户体验', '产品设计'],
        interviewCount: 32,
        averageRating: 4.6,
        status: 'active'
      }
    ])

    // 面试模板列表
    const templateList = ref([
      {
        id: 1,
        name: '前端开发面试模板',
        description: '适用于前端开发工程师的标准面试模板',
        positions: ['前端开发工程师', 'Web开发工程师'],
        questionCount: 15,
        duration: 60,
        usageCount: 23
      },
      {
        id: 2,
        name: 'Java开发面试模板',
        description: '适用于Java后端开发的综合面试模板',
        positions: ['Java开发工程师', '后端开发工程师'],
        questionCount: 18,
        duration: 75,
        usageCount: 31
      },
      {
        id: 3,
        name: 'UI设计师面试模板',
        description: '专为UI/UX设计师设计的面试模板',
        positions: ['UI设计师', 'UX设计师', '产品设计师'],
        questionCount: 12,
        duration: 50,
        usageCount: 15
      }
    ])

    // 分析数据
    const analyticsData = ref([
      {
        date: '2024-01-15',
        totalInterviews: 8,
        completedInterviews: 6,
        passedInterviews: 4,
        passRate: 66.7,
        averageScore: 78.5,
        averageDuration: 52
      },
      {
        date: '2024-01-14',
        totalInterviews: 12,
        completedInterviews: 10,
        passedInterviews: 7,
        passRate: 70.0,
        averageScore: 81.2,
        averageDuration: 48
      },
      {
        date: '2024-01-13',
        totalInterviews: 6,
        completedInterviews: 6,
        passedInterviews: 3,
        passRate: 50.0,
        averageScore: 75.8,
        averageDuration: 55
      }
    ])

    // 对话框状态
    const createDialogVisible = ref(false)
    const detailDialogVisible = ref(false)
    const settingsDialogVisible = ref(false)
    const createLoading = ref(false)

    // 选中的面试
    const selectedInterview = ref(null)

    // 创建面试表单
    const createForm = reactive({
      candidateId: '',
      type: '',
      interviewerId: '',
      scheduledTime: '',
      templateId: '',
      notes: ''
    })

    // 候选人列表（用于创建面试）
    const candidateList = ref([
      { id: 1, name: '张三' },
      { id: 2, name: '李四' },
      { id: 3, name: '王五' }
    ])

    // 系统设置表单
    const settingsForm = reactive({
      defaultDuration: 60,
      reminderTime: 15,
      scoringSystem: '100',
      autoRecord: true,
      aiAssist: true
    })

    // 表单验证规则
    const createFormRules = {
      candidateId: [
        { required: true, message: '请选择应聘者', trigger: 'change' }
      ],
      type: [
        { required: true, message: '请选择面试类型', trigger: 'change' }
      ],
      interviewerId: [
        { required: true, message: '请选择面试官', trigger: 'change' }
      ],
      scheduledTime: [
        { required: true, message: '请选择面试时间', trigger: 'change' }
      ]
    }

    // 方法
    const handleSearch = () => {
      console.log('搜索面试:', searchForm)
      // TODO: 实现搜索逻辑
    }

    const resetSearch = () => {
      Object.assign(searchForm, {
        keyword: '',
        status: '',
        type: ''
      })
      handleSearch()
    }

    const handleSizeChange = (size) => {
      pagination.size = size
      // TODO: 重新加载数据
    }

    const handleCurrentChange = (current) => {
      pagination.current = current
      // TODO: 重新加载数据
    }

    const showCreateDialog = () => {
      createDialogVisible.value = true
    }

    const closeCreateDialog = () => {
      createDialogVisible.value = false
      Object.assign(createForm, {
        candidateId: '',
        type: '',
        interviewerId: '',
        scheduledTime: '',
        templateId: '',
        notes: ''
      })
    }

    const confirmCreate = () => {
      createLoading.value = true
      // TODO: 实现创建面试逻辑
      setTimeout(() => {
        createLoading.value = false
        createDialogVisible.value = false
        ElMessage.success('面试创建成功')
      }, 1000)
    }

    const showSettingsDialog = () => {
      settingsDialogVisible.value = true
    }

    const saveSettings = () => {
      // TODO: 保存设置
      settingsDialogVisible.value = false
      ElMessage.success('设置保存成功')
    }

    const startInterview = (row) => {
      ElMessageBox.confirm(
        `确定开始与 ${row.candidateName} 的面试吗？`,
        '开始面试',
        {
          confirmButtonText: '开始',
          cancelButtonText: '取消',
          type: 'info'
        }
      ).then(() => {
        // TODO: 实现开始面试逻辑
        ElMessage.success('面试已开始')
      })
    }

    const continueInterview = (row) => {
      // TODO: 继续面试逻辑
      ElMessage.info('继续面试功能开发中')
    }

    const viewReport = (row) => {
      // TODO: 查看面试报告
      ElMessage.info('查看报告功能开发中')
    }

    const viewDetail = (row) => {
      selectedInterview.value = row
      detailDialogVisible.value = true
    }

    const downloadReport = () => {
      // TODO: 下载报告
      ElMessage.info('下载报告功能开发中')
    }

    const showAddInterviewerDialog = () => {
      // TODO: 添加面试官
      ElMessage.info('添加面试官功能开发中')
    }

    const editInterviewer = (row) => {
      // TODO: 编辑面试官
      ElMessage.info('编辑面试官功能开发中')
    }

    const removeInterviewer = (row) => {
      ElMessageBox.confirm(
        `确定移除面试官 ${row.name} 吗？`,
        '移除面试官',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        // TODO: 实现移除逻辑
        ElMessage.success('面试官已移除')
      })
    }

    const showCreateTemplateDialog = () => {
      // TODO: 创建模板
      ElMessage.info('创建模板功能开发中')
    }

    const handleTemplateAction = (command) => {
      const [action, id] = command.split('-')
      switch (action) {
      case 'edit':
        ElMessage.info('编辑模板功能开发中')
        break
      case 'copy':
        ElMessage.info('复制模板功能开发中')
        break
      case 'delete':
        ElMessageBox.confirm('确定删除此模板吗？', '删除模板', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          ElMessage.success('模板已删除')
        })
        break
      }
    }

    const useTemplate = (template) => {
      // TODO: 使用模板
      ElMessage.success(`已选择模板：${template.name}`)
    }

    const previewTemplate = (template) => {
      // TODO: 预览模板
      ElMessage.info('预览模板功能开发中')
    }

    // 工具方法
    const getStatusTagType = (status) => {
      const typeMap = {
        pending: 'warning',
        ongoing: 'primary',
        completed: 'success',
        cancelled: 'danger'
      }
      return typeMap[status] || 'info'
    }

    const getStatusText = (status) => {
      const textMap = {
        pending: '待面试',
        ongoing: '进行中',
        completed: '已完成',
        cancelled: '已取消'
      }
      return textMap[status] || status
    }

    const getTypeTagType = (type) => {
      const typeMap = {
        first: 'primary',
        second: 'warning',
        final: 'danger'
      }
      return typeMap[type] || 'info'
    }

    const getTypeText = (type) => {
      const textMap = {
        first: '初试',
        second: '复试',
        final: '终试'
      }
      return textMap[type] || type
    }

    const getScoreClass = (score) => {
      if (score >= 90) return 'score-excellent'
      if (score >= 80) return 'score-good'
      if (score >= 70) return 'score-average'
      if (score >= 60) return 'score-poor'
      return 'score-fail'
    }

    const getPassRateClass = (rate) => {
      if (rate >= 80) return 'pass-rate-high'
      if (rate >= 60) return 'pass-rate-medium'
      return 'pass-rate-low'
    }

    const formatDateTime = (dateTime) => {
      if (!dateTime) return '-'
      const date = new Date(dateTime)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    // 生命周期
    onMounted(() => {
      // TODO: 加载初始数据
      console.log('面试管理页面已加载')
    })

    return {
      loading,
      activeTab,
      statistics,
      searchForm,
      pagination,
      interviewList,
      interviewerList,
      templateList,
      analyticsData,
      createDialogVisible,
      detailDialogVisible,
      settingsDialogVisible,
      createLoading,
      selectedInterview,
      createForm,
      candidateList,
      settingsForm,
      createFormRules,
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      showCreateDialog,
      closeCreateDialog,
      confirmCreate,
      showSettingsDialog,
      saveSettings,
      startInterview,
      continueInterview,
      viewReport,
      viewDetail,
      downloadReport,
      showAddInterviewerDialog,
      editInterviewer,
      removeInterviewer,
      showCreateTemplateDialog,
      handleTemplateAction,
      useTemplate,
      previewTemplate,
      getStatusTagType,
      getStatusText,
      getTypeTagType,
      getTypeText,
      getScoreClass,
      getPassRateClass,
      formatDateTime
    }
  }
}
</script>


<style scoped>
.interview-management {
  padding: 24px;
  background: #f5f9ff;
  min-height: calc(100vh - 64px);
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 24px;
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e6f1ff;
  box-shadow: 0 2px 8px rgba(26, 111, 196, 0.1);
}

.header-content h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
}

.header-content p {
  margin: 0;
  font-size: 14px;
  color: #5a84b3;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 统计卡片 */
.statistics-section {
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.stat-card {
  border-radius: 8px;
  background: #ffffff;
  border: 1px solid #e6f1ff;
  box-shadow: 0 2px 8px rgba(26, 111, 196, 0.1);
  transition: all 0.3s ease;
  padding: 20px;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(26, 111, 196, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
}

.stat-icon.pending {
  background: #e6a23c;
}

.stat-icon.ongoing {
  background: #409eff;
}

.stat-icon.completed {
  background: #67c23a;
}

.stat-icon.average {
  background: #1a6fc4;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #5a84b3;
  font-weight: 500;
}

/* 主要内容 */
.main-content {
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e6f1ff;
  box-shadow: 0 2px 8px rgba(26, 111, 196, 0.1);
  overflow: hidden;
}

.interview-tabs {
  padding: 16px;
}

/* 搜索区域 */
.search-section {
  margin-bottom: 16px;
  padding: 16px;
  background: #f8fbff;
  border-radius: 8px;
  border: 1px solid #e6f1ff;
}

/* 分页 */
.pagination-section {
  margin-top: 16px;
  padding: 16px;
  display: flex;
  justify-content: center;
  background: #f8fbff;
  border-top: 1px solid #e6f1ff;
}

/* 面试官管理 */
.interviewers-section {
  padding: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

/* 模板管理 */
.templates-section {
  padding: 16px;
}

.templates-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.template-card {
  border-radius: 8px;
  border: 1px solid #e6f1ff;
  transition: all 0.3s ease;
}

.template-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(26, 111, 196, 0.15);
}

.template-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8fbff;
  border-bottom: 1px solid #e6f1ff;
}

.template-title {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.template-content {
  padding: 16px;
}

.template-description {
  color: #5a84b3;
  font-size: 13px;
  margin-bottom: 12px;
  line-height: 1.5;
}

.template-info {
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
}

.info-item .label {
  color: #5a84b3;
}

.info-item .value {
  color: #2c3e50;
  font-weight: 500;
}

.template-actions {
  display: flex;
  gap: 8px;
}

/* 数据分析 */
.analytics-section {
  padding: 16px;
}

.analytics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.chart-card {
  border-radius: 8px;
  border: 1px solid #e6f1ff;
}

.chart-card :deep(.el-card__header) {
  padding: 16px;
  background: #f8fbff;
  border-bottom: 1px solid #e6f1ff;
}

.chart-card h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.chart-container {
  height: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5a84b3;
  font-size: 14px;
  padding: 16px;
}

.data-table-card {
  border-radius: 8px;
  border: 1px solid #e6f1ff;
}

.data-table-card :deep(.el-card__header) {
  padding: 16px;
  background: #f8fbff;
  border-bottom: 1px solid #e6f1ff;
}

.data-table-card h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

/* 对话框 */
.interview-detail {
  padding: 0;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  padding-bottom: 8px;
  border-bottom: 1px solid #e6f1ff;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item .label {
  font-size: 12px;
  color: #5a84b3;
  font-weight: 500;
}

.detail-item .value {
  font-size: 13px;
  color: #2c3e50;
  font-weight: 600;
}

/* 分数样式 */
.score-excellent {
  color: #67c23a;
  font-weight: 700;
}

.score-good {
  color: #409eff;
  font-weight: 600;
}

.score-average {
  color: #e6a23c;
  font-weight: 600;
}

.score-poor {
  color: #f56c6c;
  font-weight: 600;
}

.score-fail {
  color: #f56c6c;
  font-weight: 700;
}

/* 通过率样式 */
.pass-rate-high {
  color: #67c23a;
  font-weight: 600;
}

.pass-rate-medium {
  color: #e6a23c;
  font-weight: 600;
}

.pass-rate-low {
  color: #f56c6c;
  font-weight: 600;
}

.text-gray {
  color: #c0c4cc;
}

/* 对话框样式调整 */
:deep(.el-dialog) {
  border-radius: 8px;
  border: 1px solid #e6f1ff;
}

:deep(.el-dialog__header) {
  padding: 16px 20px;
  background: #f8fbff;
  border-bottom: 1px solid #e6f1ff;
  margin: 0;
}

:deep(.el-dialog__title) {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__footer) {
  padding: 16px 20px;
  background: #f8fbff;
  border-top: 1px solid #e6f1ff;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 表单样式调整 */
:deep(.el-form-item__label) {
  color: #2c3e50;
  font-weight: 500;
}

:deep(.el-input__inner) {
  border: 1px solid #e6f1ff;
}

:deep(.el-input__inner:focus) {
  border-color: #1a6fc4;
}

:deep(.el-select .el-input__inner) {
  border: 1px solid #e6f1ff;
}

:deep(.el-select .el-input__inner:focus) {
  border-color: #1a6fc4;
}

/* 表格样式调整 */
:deep(.el-table) {
  border: 1px solid #e6f1ff;
  border-radius: 8px;
}

:deep(.el-table__header) {
  background: #f8fbff;
}

:deep(.el-table th) {
  background: #f8fbff;
  color: #2c3e50;
  font-weight: 600;
}

:deep(.el-table td) {
  border-bottom: 1px solid #e6f1ff;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: #f8fbff;
}

/* 标签页样式调整 */
:deep(.el-tabs__header) {
  margin: 0;
  background: #f8fbff;
  border-bottom: 1px solid #e6f1ff;
}

:deep(.el-tabs__nav-wrap::after) {
  background-color: #e6f1ff;
}

:deep(.el-tabs__item) {
  color: #5a84b3;
  font-weight: 500;
}

:deep(.el-tabs__item.is-active) {
  color: #1a6fc4;
  font-weight: 600;
}

:deep(.el-tabs__active-bar) {
  background-color: #1a6fc4;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .interview-management {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
    padding: 16px;
  }

  .header-actions {
    width: 100%;
    justify-content: center;
  }

  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 12px;
  }

  .search-section :deep(.el-form) {
    flex-direction: column;
  }

  .search-section :deep(.el-form-item) {
    margin-right: 0;
    margin-bottom: 12px;
    width: 100%;
  }

  .templates-grid {
    grid-template-columns: 1fr;
  }

  .analytics-grid {
    grid-template-columns: 1fr;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .template-actions {
    flex-direction: column;
  }

  .header-actions {
    flex-direction: column;
    width: 100%;
  }

  .header-actions .el-button {
    width: 100%;
    margin-bottom: 8px;
  }
}
</style>
