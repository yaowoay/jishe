<template>
  <div class="applicants-container">
    <div class="page-header">
      <h1 class="page-title">应聘者管理</h1>
      <p class="page-subtitle">管理求职者的申请，查看简历并进行面试邀请或拒绝操作</p>
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
              <div class="stat-number">{{ statistics.pending || 0 }}</div>
              <div class="stat-label">待处理</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon reviewed">
              <el-icon><ViewIcon /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics.reviewed || 0 }}</div>
              <div class="stat-label">已查看</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon interview">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics.interview || 0 }}</div>
              <div class="stat-label">面试中</div>
            </div>
          </div>
        </el-card>

        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon accepted">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics.accepted || 0 }}</div>
              <div class="stat-label">已录用</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <el-card>
        <div class="filter-row">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索应聘者姓名、邮箱或职位..."
            :prefix-icon="Search"
            class="search-input"
            @input="handleSearch"
            clearable
          />
          <el-select
            v-model="searchForm.status"
            placeholder="申请状态"
            @change="handleSearch"
            clearable
          >
            <el-option label="待处理" value="pending" />
            <el-option label="已查看" value="reviewed" />
            <el-option label="待笔试" value="待笔试" />
            <el-option label="笔试通过" value="笔试通过" />
            <el-option label="待面试" value="待面试" />
            <el-option label="面试中" value="interview" />
            <el-option label="已录用" value="accepted" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
          <el-button type="primary" @click="loadApplicantsList" :icon="Refresh">
            刷新
          </el-button>
          <el-button
            type="warning"
            @click="showBatchOperationDialog"
            :disabled="selectedApplications.length === 0"
            :icon="Operation"
          >
            批量操作
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 应聘者列表 -->
    <div class="applicants-list-section">
      <el-card>
        <el-table
          :data="applicantsList"
          v-loading="loading"
          stripe
          style="width: 100%"
          empty-text="暂无应聘者数据"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />

          <el-table-column label="应聘者信息" min-width="200">
            <template #default="{ row }">
              <div class="applicant-info">
                <div class="applicant-name">{{ row.fullName || row.username }}</div>
                <div class="applicant-email">{{ row.email }}</div>
                <div class="applicant-phone" v-if="row.phone">{{ row.phone }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="jobTitle" label="申请职位" width="150" />

          <el-table-column label="简历匹配度" width="120" align="center">
            <template #default="{ row }">
              <div class="score-display" v-if="row.aiEvaluationScore !== null && row.aiEvaluationScore !== undefined">
                <div class="score-circle" :class="getScoreClass(row.aiEvaluationScore)">
                  {{ Math.round(row.aiEvaluationScore) }}
                </div>
                <div class="score-text">{{ getScoreText(row.aiEvaluationScore) }}</div>
              </div>
              <span v-else class="no-score">未评分</span>
            </template>
          </el-table-column>

          <el-table-column label="申请状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.status)">
                {{ row.status || '待处理' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="applyTime" label="申请时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.applyTime) }}
            </template>
          </el-table-column>

          <el-table-column label="简历" width="120">
            <template #default="{ row }">
              <el-button
                v-if="row.resumeFileUrl"
                type="primary"
                size="small"
                @click="downloadResume(row)"
                :icon="Download"
              >
                下载
              </el-button>
              <span v-else>-</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="280" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button
                  v-if="row.feedback"
                  type="warning"
                  size="small"
                  @click="viewAnalysisReport(row)"
                  icon="Document"
                >
                  查看报告
                </el-button>
                <el-button
                  type="primary"
                  size="small"
                  @click="viewApplicantDetail(row)"
                  :icon="ViewIcon"
                >
                  详情
                </el-button>
                <!-- 根据状态显示不同的设置按钮 -->
                <el-button
                  v-if="row.status === '待笔试'"
                  type="success"
                  size="small"
                  @click="showWrittenTestSettings(row)"
                  icon="Setting"
                >
                  考核设置
                </el-button>

                <el-button
                  type="info"
                  size="small"
                  @click="showInterviewProgress(row)"
                  icon="DataAnalysis"
                >
                  面试进度
                </el-button>
                <el-button
                  v-if="row.status === '笔试通过'"
                  type="primary"
                  size="small"
                  @click="showInterviewSettings(row)"
                  icon="Setting"
                >
                  面试设置
                </el-button>

                <!-- 开始面试按钮 -->
                <el-button
                  v-if="row.status === '笔试通过' || row.status === '面试中'"
                  type="success"
                  size="small"
                  @click="startInterview(row)"
                  icon="ChatDotRound"
                >
                  开始面试
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
    </div>

    <!-- 应聘者详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="应聘者详情"
      width="80%"
      :before-close="closeDetailDialog"
    >
      <div v-if="selectedApplicant" class="applicant-detail">
        <div class="detail-header">
          <div class="applicant-basic">
            <h2>{{ selectedApplicant.fullName || selectedApplicant.username }}</h2>
            <div class="contact-info">
              <span><el-icon><Message /></el-icon> {{ selectedApplicant.email }}</span>
              <span v-if="selectedApplicant.phone">
                <el-icon><Phone /></el-icon> {{ selectedApplicant.phone }}
              </span>
            </div>
          </div>
          <div class="application-status">
            <el-tag :type="getStatusTagType(selectedApplicant.status)" size="large">
              {{ getStatusText(selectedApplicant.status) }}
            </el-tag>
          </div>
        </div>

        <el-divider />

        <div class="detail-content">
          <el-row :gutter="24">
            <el-col :span="12">
              <div class="detail-section">
                <h3>申请信息</h3>
                <div class="info-item">
                  <strong>申请职位：</strong>{{ selectedApplicant.jobTitle }}
                </div>
                <div class="info-item">
                  <strong>职位类型：</strong>{{ selectedApplicant.jobType }}
                </div>
                <div class="info-item">
                  <strong>工作地点：</strong>{{ selectedApplicant.jobLocation }}
                </div>
                <div class="info-item">
                  <strong>申请时间：</strong>{{ formatDate(selectedApplicant.applyTime) }}
                </div>
                <div class="info-item" v-if="selectedApplicant.coverLetter">
                  <strong>求职信：</strong>
                  <div class="cover-letter">{{ selectedApplicant.coverLetter }}</div>
                </div>
              </div>
            </el-col>

            <el-col :span="12">
              <div class="detail-section">
                <h3>个人信息</h3>
                <div class="info-item" v-if="selectedApplicant.education">
                  <strong>教育背景：</strong>{{ selectedApplicant.education }}
                </div>
                <div class="info-item" v-if="selectedApplicant.experience">
                  <strong>工作经验：</strong>{{ selectedApplicant.experience }}
                </div>
                <div class="info-item" v-if="selectedApplicant.skills">
                  <strong>技能特长：</strong>{{ selectedApplicant.skills }}
                </div>
                <div class="info-item" v-if="selectedApplicant.expectedSalary">
                  <strong>期望薪资：</strong>{{ selectedApplicant.expectedSalary }}
                </div>
                <div class="info-item" v-if="selectedApplicant.availability">
                  <strong>到岗时间：</strong>{{ selectedApplicant.availability }}
                </div>
              </div>
            </el-col>
          </el-row>

          <div class="detail-section" v-if="selectedApplicant.resumeFileUrl">
            <h3>简历文件</h3>
            <div class="resume-info">
              <span>{{ selectedApplicant.resumeFilename }}</span>
              <el-button
                type="primary"
                size="small"
                @click="downloadResume(selectedApplicant)"
                :icon="Download"
              >
                下载简历
              </el-button>
            </div>
          </div>

<!--          <div class="detail-section" v-if="selectedApplicant.feedback">-->
<!--            <h3>处理记录</h3>-->
<!--            <div class="feedback">{{ selectedApplicant.feedback }}</div>-->
<!--          </div>-->
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeDetailDialog">关闭</el-button>
          <el-button
            v-if="selectedApplicant && selectedApplicant.status === '笔试通过'"
            type="success"
            @click="showInterviewDialog(selectedApplicant)"
          >
            发送面试邀请
          </el-button>
          <el-button
            v-if="selectedApplicant && selectedApplicant.status !== 'rejected'"
            type="danger"
            @click="showRejectDialog(selectedApplicant)"
          >
            拒绝申请
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 简历分析报告弹窗 -->
    <el-dialog
      v-model="reportDialogVisible"
      title="简历分析报告"
      width="90%"
      :before-close="closeReportDialog"
      class="report-dialog"
    >
      <div v-if="currentReport" class="report-container">
        <!-- 报告头部 -->
        <div class="report-header">
          <div class="candidate-info">
            <h2>{{ currentReport.candidateName }}</h2>
            <p class="position">申请职位：{{ currentReport.jobTitle }}</p>
          </div>
          <div class="score-summary">
            <div class="overall-score">
              <div class="score-circle large" :class="getScoreClass(currentReport.overallScore)">
                {{ Math.round(currentReport.overallScore) }}
              </div>
              <div class="score-label">综合匹配度</div>
            </div>
          </div>
        </div>

        <!-- 分析详情 -->
        <div class="analysis-details">
          <el-row :gutter="20">
            <!-- 个人信息分析 -->
            <el-col :span="12">
              <div class="analysis-section">
                <h3>个人信息分析</h3>
                <div class="score-item">
                  <span>匹配度：</span>
                  <el-progress
                    :percentage="currentReport.personalInfo?.matchScore || 0"
                    :color="getProgressColor(currentReport.personalInfo?.matchScore || 0)"
                  />
                </div>
                <div class="strengths" v-if="currentReport.personalInfo?.strengths">
                  <h4>优势亮点</h4>
                  <ul>
                    <li v-for="strength in currentReport.personalInfo.strengths" :key="strength">
                      {{ strength }}
                    </li>
                  </ul>
                </div>
                <div class="risks" v-if="currentReport.personalInfo?.risks">
                  <h4>风险提示</h4>
                  <ul class="risk-list">
                    <li v-for="risk in currentReport.personalInfo.risks" :key="risk">
                      {{ risk }}
                    </li>
                  </ul>
                </div>
              </div>
            </el-col>

            <!-- 技能分析 -->
            <el-col :span="12">
              <div class="analysis-section">
                <h3>技能匹配分析</h3>
                <div class="score-item">
                  <span>匹配度：</span>
                  <el-progress
                    :percentage="currentReport.skills?.matchScore || 0"
                    :color="getProgressColor(currentReport.skills?.matchScore || 0)"
                  />
                </div>
                <div class="highlights" v-if="currentReport.skills?.technicalHighlights">
                  <h4>技术亮点</h4>
                  <div class="tag-list">
                    <el-tag
                      v-for="highlight in currentReport.skills.technicalHighlights"
                      :key="highlight"
                      type="success"
                      class="highlight-tag"
                    >
                      {{ highlight }}
                    </el-tag>
                  </div>
                </div>
                <div class="gaps" v-if="currentReport.skills?.skillGaps">
                  <h4>技能差距</h4>
                  <ul class="gap-list">
                    <li v-for="gap in currentReport.skills.skillGaps" :key="gap">
                      {{ gap }}
                    </li>
                  </ul>
                </div>
              </div>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <!-- 教育背景分析 -->
            <el-col :span="12">
              <div class="analysis-section">
                <h3>教育背景分析</h3>
                <div class="score-item">
                  <span>匹配度：</span>
                  <el-progress
                    :percentage="currentReport.educationBackground?.matchScore || 0"
                    :color="getProgressColor(currentReport.educationBackground?.matchScore || 0)"
                  />
                </div>
                <div class="advantages" v-if="currentReport.educationBackground?.educationAdvantages">
                  <h4>教育优势</h4>
                  <ul>
                    <li v-for="advantage in currentReport.educationBackground.educationAdvantages" :key="advantage">
                      {{ advantage }}
                    </li>
                  </ul>
                </div>
              </div>
            </el-col>

            <!-- 项目经验分析 -->
            <el-col :span="12">
              <div class="analysis-section">
                <h3>项目经验分析</h3>
                <div class="score-item">
                  <span>匹配度：</span>
                  <el-progress
                    :percentage="currentReport.projectExperience?.matchScore || 0"
                    :color="getProgressColor(currentReport.projectExperience?.matchScore || 0)"
                  />
                </div>
                <div class="highlights" v-if="currentReport.projectExperience?.projectHighlights">
                  <h4>经验亮点</h4>
                  <ul>
                    <li v-for="highlight in currentReport.projectExperience.projectHighlights" :key="highlight">
                      {{ highlight }}
                    </li>
                  </ul>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 企业建议 -->
        <div class="enterprise-suggestions">
          <h3>企业建议</h3>
          <div class="suggestion-cards">
            <div class="suggestion-card" :class="getSuggestionType(currentReport.overallScore)">
              <h4>{{ getSuggestionTitle(currentReport.overallScore) }}</h4>
              <p>{{ getSuggestionContent(currentReport.overallScore) }}</p>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeReportDialog">关闭</el-button>
          <el-button type="primary" @click="exportReport">导出报告</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 面试邀请对话框 -->
    <el-dialog
      v-model="interviewDialogVisible"
      title="发送面试邀请"
      width="500px"
      :before-close="closeInterviewDialog"
    >
      <el-form :model="interviewForm" label-width="100px">
        <el-form-item label="面试时间" required>
          <el-date-picker
            v-model="interviewForm.interviewTime"
            type="datetime"
            placeholder="选择面试时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
          />
        </el-form-item>

        <el-form-item label="备注信息">
          <el-input
            v-model="interviewForm.notes"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeInterviewDialog">取消</el-button>
          <el-button type="primary" @click="sendInterview">
            发送邀请
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 拒绝申请对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="拒绝申请"
      width="500px"
      :before-close="closeRejectDialog"
    >
      <div class="reject-dialog-content">
        <p class="reject-warning">
          <el-icon class="warning-icon"><WarningFilled /></el-icon>
          确定要拒绝这个申请吗？此操作不可撤销。
        </p>

        <el-form :model="rejectForm" label-width="100px">
          <el-form-item label="拒绝原因">
            <span class="optional-label">(可选)</span>
            <el-input
              v-model="rejectForm.reason"
              type="textarea"
              :rows="4"
              placeholder="可以选择填写拒绝原因，帮助应聘者了解改进方向"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeRejectDialog">取消</el-button>
          <el-button type="danger" @click="rejectApplicant">
            确认拒绝
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量操作对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      title="批量操作"
      width="500px"
      :before-close="closeBatchDialog"
    >
      <div class="batch-info">
        <p>已选择 <strong>{{ selectedApplications.length }}</strong> 个申请</p>
      </div>

      <el-form :model="batchForm" label-width="100px">
        <el-form-item label="操作类型" required>
          <el-select v-model="batchForm.status" placeholder="请选择操作" style="width: 100%">
            <el-option label="标记为已查看" value="reviewed" />
            <el-option label="批量拒绝" value="rejected" />
          </el-select>
        </el-form-item>

        <el-form-item label="备注信息">
          <el-input
            v-model="batchForm.feedback"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeBatchDialog">取消</el-button>
          <el-button type="primary" @click="batchUpdate">
            确认操作
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 笔试面试设置对话框 -->
    <el-dialog
      v-model="writtenTestDialogVisible"
      title="考核设置"
      width="800px"
      :before-close="closeWrittenTestDialog"
      :close-on-press-escape="!writtenTestLoading"
    >
      <div class="test-settings-content">
        <!-- 横向并列的设置区域 -->
        <div class="horizontal-settings-container">
          <!-- 笔试设置区域 -->
          <div class="settings-column written-test-column">
            <div class="column-header">
              <el-icon class="header-icon written-test-icon"><Edit /></el-icon>
              <h3>笔试设置</h3>
            </div>

            <!-- 加载状态提示 -->
            <div v-if="writtenTestLoading" class="loading-container">
              <el-icon class="is-loading" style="font-size: 20px; color: #409eff;">
                <Loading />
              </el-icon>
              <p style="margin-top: 8px; color: #606266; font-size: 14px;">正在生成笔试题目...</p>
            </div>

            <el-form
              v-else
              :model="writtenTestForm"
              label-width="100px"
              :rules="testFormRules"
              size="small"
            >
            <el-form-item label="AI生成题目" prop="aiGenerated">
              <el-switch
                v-model="writtenTestForm.aiGenerated"
                active-text="开启"
                inactive-text="关闭"
                style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
              />
                <div class="form-tip">开启后将自动AI生成笔试题目</div>
            </el-form-item>

            <el-form-item
              label="答题数量"
              prop="answerCount"
              :required="!writtenTestForm.aiGenerated"
              v-show="!writtenTestForm.aiGenerated"
            >
              <el-input-number
                v-model="writtenTestForm.answerCount"
                :min="1"
                :max="50"
                placeholder="请输入答题数量"
                style="width: 100%"
              />
              <div class="form-tip">建议设置10-20题</div>
            </el-form-item>

            <el-form-item
              label="答题范围"
              prop="answerRange"
              :required="!writtenTestForm.aiGenerated"
              v-show="!writtenTestForm.aiGenerated"
            >
              <el-select
                v-model="writtenTestForm.answerRange"
                placeholder="请选择答题范围"
                style="width: 100%"
                multiple
                collapse-tags
              >
                <el-option label="Java基础" value="java-basic" />
                <el-option label="Spring框架" value="spring" />
                <el-option label="数据库" value="database" />
                <el-option label="算法与数据结构" value="algorithm" />
                <el-option label="系统设计" value="system-design" />
                <el-option label="前端技术" value="frontend" />
                <el-option label="后端开发" value="backend" />
                <el-option label="数据分析" value="data-analysis" />
              </el-select>
              <div class="form-tip">可选择多个技术领域</div>
            </el-form-item>
            </el-form>
          </div>

          <!-- 分隔线 -->
          <div class="column-divider"></div>

          <!-- 面试设置区域 -->
          <div class="settings-column interview-column">
            <div class="column-header">
              <el-icon class="header-icon interview-icon"><ChatDotRound /></el-icon>
              <h3>面试设置</h3>
            </div>

            <!-- 加载状态提示 -->
            <div v-if="interviewLoading" class="loading-container">
              <el-icon class="is-loading" style="font-size: 20px; color: #409eff;">
                <Loading />
              </el-icon>
              <p style="margin-top: 8px; color: #606266; font-size: 14px;">正在生成面试题目...</p>
            </div>

            <el-form
              v-else
              :model="interviewSettingsForm"
              label-width="100px"
              :rules="testFormRules"
              size="small"
            >
            <el-form-item label="AI生成面试题" prop="aiGenerated">
              <el-switch
                v-model="interviewSettingsForm.aiGenerated"
                active-text="开启"
                inactive-text="关闭"
                style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
              />
                <div class="form-tip">开启后将自动AI生成面试题目</div>
            </el-form-item>

            <el-form-item
              label="面试题数量"
              prop="answerCount"
              :required="!interviewSettingsForm.aiGenerated"
              v-show="!interviewSettingsForm.aiGenerated"
            >
              <el-input-number
                v-model="interviewSettingsForm.answerCount"
                :min="1"
                :max="10"
                placeholder="请输入面试题数量"
                style="width: 100%"
              />
              <div class="form-tip">建议设置3-8题</div>
            </el-form-item>

            <el-form-item
              label="面试类型"
              prop="answerRange"
              :required="!interviewSettingsForm.aiGenerated"
              v-show="!interviewSettingsForm.aiGenerated"
            >
              <el-select
                v-model="interviewSettingsForm.answerRange"
                placeholder="请选择面试类型"
                style="width: 100%"
                multiple
                collapse-tags
              >
                <el-option label="技术面试" value="technical" />
                <el-option label="行为面试" value="behavioral" />
                <el-option label="情景面试" value="situational" />
                <el-option label="压力面试" value="stress" />
                <el-option label="案例分析" value="case-study" />
                <el-option label="编程面试" value="coding" />
              </el-select>
              <div class="form-tip">可选择多种面试类型</div>
            </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- 紧凑的操作按钮区域 -->
        <div class="compact-action-section">
          <div class="action-buttons-row">
            <!-- 发送笔试按钮 -->
            <div class="action-item">
              <div class="action-info">
                <el-icon class="action-icon written-test-icon"><Edit /></el-icon>
                <span class="action-label">笔试</span>
                <small class="action-desc">{{ writtenTestForm.aiGenerated ? 'AI生成' : `${writtenTestForm.answerCount}题` }}</small>
              </div>
              <el-button
                type="success"
                @click="saveWrittenTestSettings"
                :loading="writtenTestLoading"
                :disabled="writtenTestLoading || interviewLoading"
                class="compact-btn"
                size="default"
              >
                {{ writtenTestLoading ? '生成中' : '发送' }}
              </el-button>
            </div>

            <!-- 分隔线 -->
            <div class="action-divider"></div>

            <!-- 发送面试按钮 -->
            <div class="action-item">
              <div class="action-info">
                <el-icon class="action-icon interview-icon"><ChatDotRound /></el-icon>
                <span class="action-label">面试</span>
                <small class="action-desc">{{ interviewSettingsForm.aiGenerated ? 'AI生成' : `${interviewSettingsForm.answerCount}题` }}</small>
              </div>
              <el-button
                type="primary"
                @click="saveInterviewSettings"
                :loading="interviewLoading"
                :disabled="writtenTestLoading || interviewLoading"
                class="compact-btn"
                size="default"
              >
                {{ interviewLoading ? '生成中' : '发送' }}
              </el-button>
            </div>

            <!-- 分隔线 -->
            <div class="action-divider"></div>

            <!-- 批量操作按钮 -->
            <div class="action-item batch-item">
              <el-button
                type="warning"
                @click="sendBothSettings"
                :loading="writtenTestLoading || interviewLoading"
                :disabled="writtenTestLoading || interviewLoading"
                class="batch-compact-btn"
                size="default"
              >
                <el-icon><Operation /></el-icon>
                同时发送
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeWrittenTestDialog" :disabled="writtenTestLoading || interviewLoading">
            关闭
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 面试设置对话框 -->
    <el-dialog
      v-model="interviewSettingsDialogVisible"
      title="面试设置"
      width="500px"
      :before-close="closeInterviewSettingsDialog"
    >
      <div class="test-settings-content">
        <div class="applicant-info">
          <h4>应聘者信息</h4>
          <p><strong>姓名：</strong>{{ selectedApplicant?.name }}</p>
          <p><strong>职位：</strong>{{ selectedApplicant?.jobTitle }}</p>
        </div>

        <el-divider />

        <el-form :model="interviewSettingsForm" label-width="120px" :rules="testFormRules">
          <el-form-item label="AI生成题目" prop="aiGenerated">
            <el-switch
              v-model="interviewSettingsForm.aiGenerated"
              active-text="开启"
              inactive-text="关闭"
              style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
            />
            <div class="form-tip">开启后将自动AI生成面试题目，关闭后可自定义设置</div>
          </el-form-item>

          <el-form-item
            label="面试题数量"
            prop="answerCount"
            :required="!interviewSettingsForm.aiGenerated"
            v-show="!interviewSettingsForm.aiGenerated"
          >
            <el-input-number
              v-model="interviewSettingsForm.answerCount"
              :min="1"
              :max="20"
              placeholder="请输入面试题数量"
              style="width: 100%"
            />
            <div class="form-tip">建议设置5-10题</div>
          </el-form-item>

          <el-form-item
            label="面试范围"
            prop="answerRange"
            :required="!interviewSettingsForm.aiGenerated"
            v-show="!interviewSettingsForm.aiGenerated"
          >
            <el-select
              v-model="interviewSettingsForm.answerRange"
              placeholder="请选择面试范围"
              style="width: 100%"
              multiple
              collapse-tags
            >
              <el-option label="技术深度" value="technical-depth" />
              <el-option label="项目经验" value="project-experience" />
              <el-option label="问题解决能力" value="problem-solving" />
              <el-option label="团队协作" value="teamwork" />
              <el-option label="沟通表达" value="communication" />
              <el-option label="学习能力" value="learning-ability" />
              <el-option label="职业规划" value="career-planning" />
              <el-option label="压力处理" value="stress-management" />
            </el-select>
            <div class="form-tip">可选择多个面试考察点</div>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeInterviewSettingsDialog" :disabled="interviewLoading">取消</el-button>
          <el-button
            type="primary"
            @click="saveInterviewSettings"
            :loading="interviewLoading"
            :disabled="interviewLoading"
          >
            {{ interviewLoading ? '正在设置面试...' : '发送面试' }}
          </el-button>
        </div>
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
                <el-tag :type="getStatusTagType(selectedProgress.writtenTestStatus)">
                  {{ getStatusText(selectedProgress.writtenTestStatus) }}
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
                  <span class="value">{{ formatDate(selectedProgress.writtenTestCompletedAt) }}</span>
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
                title="该应聘者未通过笔试"
                sub-title="暂时无法进行面试环节"
              >
              </el-result>
            </div>

            <div v-else class="progress-item">
              <div class="progress-header">
                <span class="progress-title">面试状态</span>
                <el-tag :type="getStatusTagType(selectedProgress.interviewStatus)">
                  {{ getStatusText(selectedProgress.interviewStatus) }}
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
                  <span class="value">{{ formatDate(selectedProgress.interviewScheduledAt) }}</span>
                </div>

                <div class="detail-item" v-if="selectedProgress.interviewCompletedAt">
                  <span class="label">完成时间：</span>
                  <span class="value">{{ formatDate(selectedProgress.interviewCompletedAt) }}</span>
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
        <div class="dialog-footer">
          <el-button @click="closeProgressDialog">关闭</el-button>
        </div>
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
                  <span class="user-answer" :class="{ 'wrong': !question.isCorrect }">
                    {{ question.userAnswer || '未作答' }}
                  </span>
                </div>
              </div>

              <!-- 解析说明 -->
              <div v-if="question.explanation" class="explanation">
                <strong>解析：</strong>{{ question.explanation }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeTestReportDialog">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 面试报告对话框 -->
    <el-dialog
      v-model="interviewReportDialogVisible"
      title="面试详细报告"
      width="900px"
      :close-on-click-modal="false"
    >
      <div v-if="interviewReportData" class="interview-report-container">
        <div class="report-header">
          <h2>面试评估报告</h2>
          <div class="overall-score">
            <span class="score-label">综合得分：</span>
            <span class="score-value">{{ interviewReportData.overallScore || 0 }}分</span>
          </div>
        </div>

        <div class="report-content">
          <div v-if="interviewReportData.answers" class="answers-section">
            <h3>面试问答记录</h3>
            <div v-for="(answer, index) in interviewReportData.answers" :key="index" class="answer-item">
              <div class="question">
                <strong>问题 {{ index + 1 }}：</strong>{{ answer.question }}
              </div>
              <div class="answer">
                <strong>回答：</strong>{{ answer.answer }}
              </div>
              <div v-if="answer.score" class="answer-score">
                <strong>得分：</strong>{{ answer.score }}分
              </div>
            </div>
          </div>

          <div v-if="interviewReportData.evaluation" class="evaluation-section">
            <h3>评估结果</h3>
            <p>{{ interviewReportData.evaluation }}</p>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeInterviewReportDialog">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Clock,
  View as ViewIcon,
  ChatDotRound,
  Check,
  Search,
  Refresh,
  Operation,
  Download,
  Close,
  Message,
  Phone,
  WarningFilled,
  Loading
} from '@element-plus/icons-vue'
import {
  getApplicantsList,
  getApplicantDetail,
  sendInterviewInvitation,
  rejectApplication,
  batchUpdateApplications,
  getApplicationStatistics,
  setWrittenTestSettings,
  setInterviewSettings
} from '@/api/applicant'
import { getInterviewProgress } from '@/api/interview'

export default {
  name: 'CompanyApplicants',
  components: {
    Clock,
    ViewIcon,
    ChatDotRound,
    Check,
    Search,
    Refresh,
    Operation,
    Download,
    Close,
    Message,
    Phone
  },
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const detailDialogVisible = ref(false)
    const interviewDialogVisible = ref(false)
    const rejectDialogVisible = ref(false)
    const batchDialogVisible = ref(false)

    const applicantsList = ref([])
    const selectedApplicant = ref(null)
    const selectedApplications = ref([])
    const statistics = ref({})

    const searchForm = reactive({
      keyword: '',
      status: ''
    })

    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0
    })

    const interviewForm = reactive({
      interviewTime: '',
      notes: ''
    })

    const rejectForm = reactive({
      reason: ''
    })

    const batchForm = reactive({
      status: '',
      feedback: ''
    })

    // 笔试设置相关
    const writtenTestDialogVisible = ref(false)
    const writtenTestLoading = ref(false) // 笔试生成加载状态
    const writtenTestForm = reactive({
      answerCount: 10,
      answerRange: [],
      aiGenerated: true
    })

    // 面试设置相关
    const interviewSettingsDialogVisible = ref(false)
    const interviewLoading = ref(false) // 面试设置加载状态
    const interviewSettingsForm = reactive({
      answerCount: 5,
      answerRange: [],
      aiGenerated: true
    })

    // 表单验证规则
    const testFormRules = {
      answerCount: [
        { required: true, message: '请输入答题数量', trigger: 'blur' },
        { type: 'number', min: 1, message: '答题数量至少为1', trigger: 'blur' }
      ],
      answerRange: [
        { required: true, message: '请选择答题范围', trigger: 'change' },
        { type: 'array', min: 1, message: '至少选择一个范围', trigger: 'change' }
      ]
    }

    // 简历分析报告相关
    const reportDialogVisible = ref(false)
    const currentReport = ref(null)

    // 面试进度相关
    const progressDialogVisible = ref(false)
    const selectedProgress = ref(null)
    const testReportDialogVisible = ref(false)
    const testReportData = ref(null)
    const interviewReportDialogVisible = ref(false)
    const interviewReportData = ref(null)

    // 加载应聘者列表
    const loadApplicantsList = async () => {
      loading.value = true
      try {
        const params = {
          current: pagination.current,
          size: pagination.size,
          status: searchForm.status,
          keyword: searchForm.keyword
        }

        const response = await getApplicantsList(params)
        if (response.success) {
          applicantsList.value = response.data.records || []
          pagination.total = response.data.total || 0
        } else {
          ElMessage.error(response.message || '获取应聘者列表失败')
        }
      } catch (error) {
        console.error('Load applicants error:', error)
        ElMessage.error('获取应聘者列表失败')
      } finally {
        loading.value = false
      }
    }

    // 加载统计信息
    const loadStatistics = async () => {
      try {
        const response = await getApplicationStatistics()
        if (response.success) {
          statistics.value = response.data || {}
        }
      } catch (error) {
        console.error('Load statistics error:', error)
      }
    }

    // 查看应聘者详情
    const viewApplicantDetail = async (applicant) => {
      try {
        const response = await getApplicantDetail(applicant.applicationId)
        if (response.success) {
          selectedApplicant.value = response.data
          detailDialogVisible.value = true
        } else {
          ElMessage.error(response.message || '获取应聘者详情失败')
        }
      } catch (error) {
        console.error('Get applicant detail error:', error)
        ElMessage.error('获取应聘者详情失败')
      }
    }

    // 跳转到应聘者详情页面
    const goToApplicantDetail = (applicant) => {
      router.push({
        name: 'CompanyApplicantDetail',
        params: { applicationId: applicant.applicationId }
      })
    }

    // 显示面试邀请对话框
    const showInterviewDialog = (applicant) => {
      selectedApplicant.value = applicant
      interviewForm.interviewTime = ''
      interviewForm.notes = ''
      interviewDialogVisible.value = true
    }

    // 发送面试邀请
    const sendInterview = async () => {
      if (!interviewForm.interviewTime) {
        ElMessage.warning('请选择面试时间')
        return
      }

      try {
        const data = {
          applicationId: selectedApplicant.value.applicationId,
          interviewTime: interviewForm.interviewTime,
          notes: interviewForm.notes
        }

        const response = await sendInterviewInvitation(data)
        if (response.success) {
          ElMessage.success('面试邀请发送成功')
          interviewDialogVisible.value = false
          loadApplicantsList()
          loadStatistics()
        } else {
          ElMessage.error(response.message || '发送面试邀请失败')
        }
      } catch (error) {
        console.error('Send interview error:', error)
        ElMessage.error('发送面试邀请失败')
      }
    }

    // 显示拒绝对话框
    const showRejectDialog = (applicant) => {
      selectedApplicant.value = applicant
      rejectForm.reason = ''
      rejectDialogVisible.value = true
    }

    // 拒绝申请
    const rejectApplicant = async () => {
      try {
        const data = {
          applicationId: selectedApplicant.value.applicationId,
          reason: rejectForm.reason || ''
        }

        const response = await rejectApplication(data)
        if (response.success) {
          ElMessage.success('申请已拒绝')
          rejectDialogVisible.value = false
          loadApplicantsList()
          loadStatistics()
        } else {
          ElMessage.error(response.message || '拒绝申请失败')
        }
      } catch (error) {
        console.error('Reject application error:', error)
        ElMessage.error('拒绝申请失败')
      }
    }

    // 下载简历
    const downloadResume = (applicant) => {
      if (applicant.resumeFileUrl) {
        const link = document.createElement('a')
        link.href = applicant.resumeFileUrl
        link.download = applicant.resumeFilename || '简历.pdf'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
      }
    }

    // 处理搜索
    const handleSearch = () => {
      pagination.current = 1
      loadApplicantsList()
    }

    // 处理选择变化
    const handleSelectionChange = (selection) => {
      selectedApplications.value = selection
    }

    // 显示批量操作对话框
    const showBatchOperationDialog = () => {
      batchForm.status = ''
      batchForm.feedback = ''
      batchDialogVisible.value = true
    }

    // 批量操作
    const batchUpdate = async () => {
      if (!batchForm.status) {
        ElMessage.warning('请选择操作状态')
        return
      }

      try {
        const data = {
          applicationIds: selectedApplications.value.map(item => item.applicationId),
          status: batchForm.status,
          feedback: batchForm.feedback
        }

        const response = await batchUpdateApplications(data)
        if (response.success) {
          ElMessage.success('批量操作成功')
          batchDialogVisible.value = false
          loadApplicantsList()
          loadStatistics()
        } else {
          ElMessage.error(response.message || '批量操作失败')
        }
      } catch (error) {
        console.error('Batch update error:', error)
        ElMessage.error('批量操作失败')
      }
    }

    // 开始面试
    const startInterview = (applicant) => {
      console.log('开始面试，申请ID:', applicant.applicationId)

      // 跳转到面试页面，传递applicationId参数
      router.push({
        name: 'OfficialExam',
        params: {
          applicationId: applicant.applicationId
        }
      })
    }

    // 分页处理
    const handleSizeChange = (size) => {
      pagination.size = size
      pagination.current = 1
      loadApplicantsList()
    }

    const handleCurrentChange = (current) => {
      pagination.current = current
      loadApplicantsList()
    }

    // 关闭对话框
    const closeDetailDialog = () => {
      detailDialogVisible.value = false
      selectedApplicant.value = null
    }

    const closeInterviewDialog = () => {
      interviewDialogVisible.value = false
      selectedApplicant.value = null
    }

    const closeRejectDialog = () => {
      rejectDialogVisible.value = false
      selectedApplicant.value = null
    }

    const closeBatchDialog = () => {
      batchDialogVisible.value = false
    }

    // 笔试设置相关方法
    const showWrittenTestSettings = (applicant) => {
      selectedApplicant.value = applicant
      // 重置表单
      writtenTestForm.answerCount = 10
      writtenTestForm.answerRange = []
      writtenTestForm.aiGenerated = true
      writtenTestDialogVisible.value = true
    }

    const closeWrittenTestDialog = () => {
      writtenTestDialogVisible.value = false
      selectedApplicant.value = null
      // 重置表单
      writtenTestForm.answerCount = 10
      writtenTestForm.answerRange = []
      writtenTestForm.aiGenerated = true
      interviewSettingsForm.answerCount = 5
      interviewSettingsForm.answerRange = []
      interviewSettingsForm.aiGenerated = true
    }


    const saveWrittenTestSettings = async () => {
      // 验证笔试表单
      if (!writtenTestForm.aiGenerated && (!writtenTestForm.answerCount || writtenTestForm.answerRange.length === 0)) {
        ElMessage.warning('请完整填写笔试信息')
        return
      }

      // 开始加载状态
      writtenTestLoading.value = true

      try {
        const applicationId = selectedApplicant.value.applicationId

        // 调用笔试设置API
        const writtenTestData = {
          applicationId: applicationId,
          writtenAnswerCount: writtenTestForm.aiGenerated ? 0 : writtenTestForm.answerCount,
          writtenAnswerRange: writtenTestForm.aiGenerated ? '' : writtenTestForm.answerRange.join(','),
          writtenAiGenerated: writtenTestForm.aiGenerated,
          interviewAnswerCount: 0,
          interviewAnswerRange: '',
          interviewAiGenerated: true
        }

        const response = await setWrittenTestSettings(writtenTestData)

        if (response.success) {
          ElMessage.success('笔试题目生成成功，已发送给应聘者')
          // 刷新列表和统计数据
          loadApplicantsList()
          loadStatistics()
          // 可以选择是否关闭对话框，这里保持打开状态方便继续设置面试
        } else {
          ElMessage.error(response.message || '笔试设置失败')
        }
      } catch (error) {
        console.error('发送笔试失败:', error)
        ElMessage.error('笔试发送失败，请重试')
      } finally {
        // 结束加载状态
        writtenTestLoading.value = false
      }
    }

    // 面试设置相关方法
    const showInterviewSettings = (applicant) => {
      selectedApplicant.value = applicant
      // 重置表单
      interviewSettingsForm.answerCount = 5
      interviewSettingsForm.answerRange = []
      interviewSettingsForm.aiGenerated = true
      interviewSettingsDialogVisible.value = true
    }

    const closeInterviewSettingsDialog = () => {
      interviewSettingsDialogVisible.value = false
      selectedApplicant.value = null
    }

    const saveInterviewSettings = async () => {
      // 验证面试表单
      if (!interviewSettingsForm.aiGenerated && (!interviewSettingsForm.answerCount || interviewSettingsForm.answerRange.length === 0)) {
        ElMessage.warning('请完整填写面试信息')
        return
      }

      // 开始加载状态
      interviewLoading.value = true

      try {
        const applicationId = selectedApplicant.value.applicationId

        // 调用面试设置API
        const interviewData = {
          applicationId: applicationId,
          writtenAnswerCount: 0,
          writtenAnswerRange: '',
          writtenAiGenerated: true,
          interviewAnswerCount: interviewSettingsForm.aiGenerated ? 0 : interviewSettingsForm.answerCount,
          interviewAnswerRange: interviewSettingsForm.aiGenerated ? '' : interviewSettingsForm.answerRange.join(','),
          interviewAiGenerated: interviewSettingsForm.aiGenerated
        }

        const response = await setInterviewSettings(interviewData)

        if (response.success) {
          ElMessage.success('面试题目生成成功，已发送给应聘者')
          // 刷新列表和统计数据
          loadApplicantsList()
          loadStatistics()
          // 可以选择是否关闭对话框，这里保持打开状态方便继续设置笔试
        } else {
          ElMessage.error(response.message || '面试设置失败')
        }
      } catch (error) {
        console.error('发送面试失败:', error)
        ElMessage.error('面试发送失败，请重试')
      } finally {
        // 结束加载状态
        interviewLoading.value = false
      }
    }

    // 批量发送笔试和面试
    const sendBothSettings = async () => {
      // 验证两个表单
      if (!writtenTestForm.aiGenerated && (!writtenTestForm.answerCount || writtenTestForm.answerRange.length === 0)) {
        ElMessage.warning('请完整填写笔试信息')
        return
      }

      if (!interviewSettingsForm.aiGenerated && (!interviewSettingsForm.answerCount || interviewSettingsForm.answerRange.length === 0)) {
        ElMessage.warning('请完整填写面试信息')
        return
      }

      try {
        // 同时开始两个加载状态
        writtenTestLoading.value = true
        interviewLoading.value = true

        const applicationId = selectedApplicant.value.applicationId

        // 准备笔试数据
        const writtenTestData = {
          applicationId: applicationId,
          writtenAnswerCount: writtenTestForm.aiGenerated ? 0 : writtenTestForm.answerCount,
          writtenAnswerRange: writtenTestForm.aiGenerated ? '' : writtenTestForm.answerRange.join(','),
          writtenAiGenerated: writtenTestForm.aiGenerated,
          interviewAnswerCount: 0,
          interviewAnswerRange: '',
          interviewAiGenerated: true
        }

        // 准备面试数据
        const interviewData = {
          applicationId: applicationId,
          writtenAnswerCount: 0,
          writtenAnswerRange: '',
          writtenAiGenerated: true,
          interviewAnswerCount: interviewSettingsForm.aiGenerated ? 0 : interviewSettingsForm.answerCount,
          interviewAnswerRange: interviewSettingsForm.aiGenerated ? '' : interviewSettingsForm.answerRange.join(','),
          interviewAiGenerated: interviewSettingsForm.aiGenerated
        }

        // 并行发送两个请求
        const [writtenResponse, interviewResponse] = await Promise.all([
          setWrittenTestSettings(writtenTestData),
          setInterviewSettings(interviewData)
        ])

        // 检查结果
        const writtenSuccess = writtenResponse.success
        const interviewSuccess = interviewResponse.success

        if (writtenSuccess && interviewSuccess) {
          ElMessage.success('笔试和面试题目已同时发送给应聘者')
          loadApplicantsList()
          loadStatistics()
        } else if (writtenSuccess && !interviewSuccess) {
          ElMessage.warning('笔试发送成功，面试发送失败：' + (interviewResponse.message || '未知错误'))
          loadApplicantsList()
          loadStatistics()
        } else if (!writtenSuccess && interviewSuccess) {
          ElMessage.warning('面试发送成功，笔试发送失败：' + (writtenResponse.message || '未知错误'))
          loadApplicantsList()
          loadStatistics()
        } else {
          ElMessage.error('笔试和面试发送均失败，请重试')
        }
      } catch (error) {
        console.error('批量发送失败:', error)
        ElMessage.error('批量发送失败，请重试')
      } finally {
        // 结束加载状态
        writtenTestLoading.value = false
        interviewLoading.value = false
      }
    }

    // 工具方法
    const getStatusTagType = (status) => {
      const statusMap = {
        pending: 'warning',
        reviewed: 'info',
        interview: 'primary',
        accepted: 'success',
        rejected: 'danger',
        '待处理': 'warning',
        '已查看': 'info',
        '待笔试': 'info',
        '笔试通过': 'success',
        '待面试': 'warning',
        '面试中': 'primary',
        '已录用': 'success',
        '淘汰': 'danger',
        // 面试进度状态
        'not_started': 'info',
        'in_progress': 'warning',
        'completed': 'success',
        'passed': 'success',
        'failed': 'danger'
      }
      return statusMap[status] || 'info'
    }

    const getStatusText = (status) => {
      const statusMap = {
        pending: '待处理',
        reviewed: '已查看',
        interview: '面试中',
        accepted: '已录用',
        rejected: '淘汰',
        '待处理': '待处理',
        '已查看': '已查看',
        '待笔试': '待笔试',
        '笔试通过': '笔试通过',
        '待面试': '待面试',
        '面试中': '面试中',
        '已录用': '已录用',
        '淘汰': '淘汰',
        // 面试进度状态
        'not_started': '未开始',
        'in_progress': '进行中',
        'completed': '已完成',
        'passed': '通过',
        'failed': '未通过'
      }
      return statusMap[status] || status
    }

    const formatDate = (date) => {
      if (!date) return '-'
      return new Date(date).toLocaleString('zh-CN')
    }

    // 简历分析报告相关方法
    const viewAnalysisReport = (applicant) => {
      try {
        if (!applicant.feedback) {
          ElMessage.warning('该申请者暂无简历分析报告')
          return
        }

        // 解析feedback JSON数据
        const feedbackData = JSON.parse(applicant.feedback)
        const analysisData = feedbackData.data?.analysis?.data?.outputs?.end_eponing || {}

        currentReport.value = {
          candidateName: applicant.applicantName,
          jobTitle: applicant.jobTitle,
          overallScore: analysisData.overallScore || applicant.aiEvaluationScore || 0,
          personalInfo: analysisData.personalInfo || {},
          skills: analysisData.skills || {},
          educationBackground: analysisData.educationBackground || {},
          projectExperience: analysisData.projectExperience || {}
        }

        reportDialogVisible.value = true
      } catch (error) {
        console.error('解析简历分析报告失败:', error)
        ElMessage.error('简历分析报告数据格式错误')
      }
    }

    const closeReportDialog = () => {
      reportDialogVisible.value = false
      currentReport.value = null
    }

    const exportReport = () => {
      ElMessage.info('导出功能开发中...')
    }

    // 面试进度相关方法
    const showInterviewProgress = async (applicant) => {
      try {
        loading.value = true

        // 调用API获取面试进度信息
        const response = await getInterviewProgress(applicant.applicationId)

        if (response.success && response.data) {
          selectedProgress.value = response.data
          progressDialogVisible.value = true
        } else {
          // 如果没有进度记录，显示默认状态
          selectedProgress.value = {
            applicationId: applicant.applicationId,
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

    const closeProgressDialog = () => {
      progressDialogVisible.value = false
      selectedProgress.value = null
    }

    // 查看笔试详细报告
    const viewTestReport = () => {
      try {
        if (selectedProgress.value && selectedProgress.value.writtenTestResultJson) {
          testReportData.value = JSON.parse(selectedProgress.value.writtenTestResultJson)
          testReportDialogVisible.value = true
        } else {
          ElMessage.warning('暂无笔试报告数据')
        }
      } catch (error) {
        console.error('解析笔试报告失败:', error)
        ElMessage.error('笔试报告数据格式错误')
      }
    }

    // 查看面试详细报告
    const viewInterviewReport = () => {
      try {
        if (selectedProgress.value && selectedProgress.value.interviewResultJson) {
          interviewReportData.value = JSON.parse(selectedProgress.value.interviewResultJson)
          interviewReportDialogVisible.value = true
        } else {
          ElMessage.warning('暂无面试报告数据')
        }
      } catch (error) {
        console.error('解析面试报告失败:', error)
        ElMessage.error('面试报告数据格式错误')
      }
    }

    // 关闭报告对话框
    const closeTestReportDialog = () => {
      testReportDialogVisible.value = false
      testReportData.value = null
    }

    const closeInterviewReportDialog = () => {
      interviewReportDialogVisible.value = false
      interviewReportData.value = null
    }

    // 格式化时间（用于报告中）
    const formatTime = (seconds) => {
      if (!seconds) return '0分0秒'
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60
      return `${minutes}分${remainingSeconds}秒`
    }

    // 获取分数等级样式类
    const getScoreClass = (score) => {
      if (score >= 90) return 'excellent'
      if (score >= 80) return 'good'
      if (score >= 70) return 'average'
      if (score >= 60) return 'basic'
      return 'poor'
    }

    // 获取进度步骤
    const getProgressStep = (progress) => {
      if (!progress) return 0

      switch (progress.overallStatus) {
      case 'pending':
        return 0
      case 'written_test':
        return 1
      case 'interview':
        return 2
      case 'completed':
        return 3
      default:
        return 0
      }
    }

    // 评分相关方法

    const getScoreText = (score) => {
      if (score >= 90) return '优秀'
      if (score >= 80) return '良好'
      if (score >= 70) return '一般'
      if (score >= 60) return '基本'
      return '较差'
    }

    const getProgressColor = (score) => {
      if (score >= 90) return '#67c23a'
      if (score >= 80) return '#409eff'
      if (score >= 70) return '#e6a23c'
      if (score >= 60) return '#f56c6c'
      return '#909399'
    }

    const getSuggestionType = (score) => {
      if (score >= 80) return 'recommend'
      if (score >= 70) return 'consider'
      return 'reject'
    }

    const getSuggestionTitle = (score) => {
      if (score >= 80) return '推荐录用'
      if (score >= 70) return '考虑录用'
      return '不推荐录用'
    }

    const getSuggestionContent = (score) => {
      if (score >= 80) return '该候选人综合素质优秀，技能匹配度高，建议优先考虑录用。'
      if (score >= 70) return '该候选人基本符合要求，可以考虑进入下一轮面试。'
      return '该候选人与职位要求匹配度较低，建议谨慎考虑。'
    }

    onMounted(() => {
      loadApplicantsList()
      loadStatistics()
    })

    return {
      loading,
      detailDialogVisible,
      interviewDialogVisible,
      rejectDialogVisible,
      batchDialogVisible,
      applicantsList,
      selectedApplicant,
      selectedApplications,
      statistics,
      searchForm,
      pagination,
      interviewForm,
      rejectForm,
      batchForm,
      reportDialogVisible,
      currentReport,
      // 面试进度相关
      progressDialogVisible,
      selectedProgress,
      testReportDialogVisible,
      testReportData,
      interviewReportDialogVisible,
      interviewReportData,
      // 笔试设置相关
      writtenTestDialogVisible,
      writtenTestLoading,
      writtenTestForm,
      testFormRules,
      // 面试设置相关
      interviewSettingsDialogVisible,
      interviewLoading,
      interviewSettingsForm,
      loadApplicantsList,
      loadStatistics,
      viewApplicantDetail,
      goToApplicantDetail,
      showInterviewDialog,
      sendInterview,
      showRejectDialog,
      rejectApplicant,
      downloadResume,
      handleSearch,
      handleSelectionChange,
      showBatchOperationDialog,
      batchUpdate,
      handleSizeChange,
      handleCurrentChange,
      closeDetailDialog,
      closeInterviewDialog,
      closeRejectDialog,
      closeBatchDialog,
      // 笔试和面试设置方法
      showWrittenTestSettings,
      closeWrittenTestDialog,
      saveWrittenTestSettings,

      showInterviewSettings,
      closeInterviewSettingsDialog,
      saveInterviewSettings,
      sendBothSettings,
      startInterview,
      getStatusTagType,
      getStatusText,
      formatDate,
      viewAnalysisReport,
      closeReportDialog,
      exportReport,
      // 面试进度相关方法
      showInterviewProgress,
      closeProgressDialog,
      getProgressStep,
      viewTestReport,
      viewInterviewReport,
      closeTestReportDialog,
      closeInterviewReportDialog,
      formatTime,
      getScoreClass,
      getScoreText,
      getProgressColor,
      getSuggestionType,
      getSuggestionTitle,
      getSuggestionContent,
      // 图标
      Clock,
      ViewIcon,
      ChatDotRound,
      Check,
      Search,
      Refresh,
      Operation,
      Download,
      Close,
      Message,
      Phone,
      WarningFilled,
      Loading
    }
  }
}
</script>


<style scoped>
.applicants-container {
  padding: 24px;
  background: #f5f9ff;
  min-height: calc(100vh - 64px);
}

.page-header {
  margin-bottom: 24px;
  padding: 0;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #5a84b3;
  margin: 0;
  font-weight: 500;
}

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
  color: white;
  font-size: 20px;
}

.stat-icon.pending {
  background: #e6a23c;
}

.stat-icon.reviewed {
  background: #409eff;
}

.stat-icon.interview {
  background: #1a6fc4;
}

.stat-icon.accepted {
  background: #67c23a;
}

.stat-info {
  flex: 1;
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

.filter-section {
  margin-bottom: 24px;
}

.filter-row {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 16px;
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e6f1ff;
  box-shadow: 0 2px 8px rgba(26, 111, 196, 0.1);
}

.search-input {
  flex: 1;
  max-width: 400px;
}

.applicants-list-section {
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e6f1ff;
  box-shadow: 0 2px 8px rgba(26, 111, 196, 0.1);
  overflow: hidden;
}

.applicant-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.applicant-name {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.applicant-email {
  font-size: 12px;
  color: #5a84b3;
}

.applicant-phone {
  font-size: 12px;
  color: #5a84b3;
}

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-buttons .el-button {
  margin: 0;
  border-radius: 4px;
  font-weight: 500;
  font-size: 12px;
  padding: 6px 10px;
  height: auto;
}

.pagination-wrapper {
  margin-top: 16px;
  padding: 16px;
  text-align: center;
  background: #f8fbff;
  border-top: 1px solid #e6f1ff;
}

.applicant-detail {
  padding: 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.applicant-basic h2 {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.contact-info {
  display: flex;
  gap: 16px;
  color: #5a84b3;
  font-size: 14px;
}

.contact-info span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-content {
  margin-top: 16px;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 12px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e6f1ff;
}

.info-item {
  margin-bottom: 10px;
  font-size: 14px;
  line-height: 1.6;
  color: #5a84b3;
}

.info-item strong {
  color: #2c3e50;
  margin-right: 6px;
  font-weight: 600;
}

.cover-letter {
  margin-top: 8px;
  padding: 12px;
  background: #f8fbff;
  border-radius: 4px;
  white-space: pre-wrap;
  line-height: 1.6;
  border-left: 3px solid #1a6fc4;
  font-size: 14px;
}

.resume-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8fbff;
  border-radius: 4px;
  border: 1px solid #e6f1ff;
}

/* 简历匹配度样式 */
.score-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.score-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 12px;
  color: white;
}

.score-circle.large {
  width: 60px;
  height: 60px;
  font-size: 18px;
}

.score-circle.excellent {
  background: #67c23a;
}

.score-circle.good {
  background: #409eff;
}

.score-circle.average {
  background: #e6a23c;
}

.score-circle.basic {
  background: #f56c6c;
}

.score-circle.poor {
  background: #909399;
}

.score-text {
  font-size: 12px;
  color: #5a84b3;
  font-weight: 500;
}

.no-score {
  color: #c0c4cc;
  font-size: 12px;
}

/* 简历分析报告弹窗样式 */
.report-container {
  max-height: 60vh;
  overflow-y: auto;
  padding: 16px;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f0f7ff;
  margin-bottom: 20px;
  border-radius: 8px;
  border: 1px solid #e6f1ff;
}

.candidate-info h2 {
  margin: 0 0 4px 0;
  font-size: 18px;
  color: #2c3e50;
}

.candidate-info .position {
  margin: 0;
  color: #5a84b3;
  font-size: 14px;
}

.score-summary {
  text-align: center;
}

.overall-score {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.score-label {
  font-size: 12px;
  color: #5a84b3;
}

.analysis-details {
  padding: 0;
  margin-bottom: 20px;
}

.analysis-section {
  background: #f8fbff;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  border: 1px solid #e6f1ff;
}

.analysis-section h3 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 14px;
  font-weight: 600;
  padding-bottom: 8px;
  border-bottom: 1px solid #e6f1ff;
}

.analysis-section h4 {
  margin: 12px 0 8px 0;
  color: #2c3e50;
  font-size: 13px;
  font-weight: 600;
}

.score-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.score-item span {
  min-width: 50px;
  font-weight: 500;
  font-size: 13px;
  color: #2c3e50;
}

.strengths ul, .risks ul, .advantages ul, .highlights ul, .gaps ul {
  margin: 0;
  padding-left: 16px;
  font-size: 13px;
}

.strengths li, .advantages li, .highlights li {
  color: #67c23a;
  margin-bottom: 4px;
}

.risk-list li, .gap-list li {
  color: #f56c6c;
  margin-bottom: 4px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.highlight-tag {
  margin: 0;
  font-size: 12px;
}

.enterprise-suggestions {
  padding: 0 0 16px;
}

.enterprise-suggestions h3 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.suggestion-cards {
  display: grid;
  gap: 12px;
}

.suggestion-card {
  padding: 16px;
  border-radius: 8px;
  border-left: 4px solid;
  font-size: 14px;
}

.suggestion-card.recommend {
  background: #f0f9eb;
  border-left-color: #67c23a;
}

.suggestion-card.consider {
  background: #fdf6ec;
  border-left-color: #e6a23c;
}

.suggestion-card.reject {
  background: #fef0f0;
  border-left-color: #f56c6c;
}

.suggestion-card h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
}

.suggestion-card p {
  margin: 0;
  line-height: 1.5;
  color: #5a84b3;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 16px;
  border-top: 1px solid #e6f1ff;
}

/* 拒绝对话框样式 */
.reject-dialog-content {
  padding: 8px 0;
}

.reject-warning {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px;
  background-color: #fef0f0;
  border: 1px solid #fde2e2;
  border-radius: 4px;
  color: #f56c6c;
  font-size: 14px;
  line-height: 1.5;
}

.warning-icon {
  margin-right: 8px;
  font-size: 16px;
}

.optional-label {
  color: #909399;
  font-size: 12px;
  margin-left: 4px;
}

/* 测试设置对话框样式 */
.test-settings-content {
  padding: 8px 0;
}

/* 横向并列布局容器 */
.horizontal-settings-container {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.settings-column {
  flex: 1;
  padding: 16px;
  border: 1px solid #e6f1ff;
  border-radius: 8px;
  background: #f8fbff;
}

.written-test-column {
  border-left: 4px solid #67c23a;
}

.interview-column {
  border-left: 4px solid #409eff;
}

.column-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e6f1ff;
}

.header-icon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
}

.written-test-icon {
  background: #67c23a;
}

.interview-icon {
  background: #409eff;
}

.column-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.column-divider {
  width: 1px;
  background: #e6f1ff;
  flex-shrink: 0;
  align-self: stretch;
}

.applicant-info {
  background: #f8fbff;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  border: 1px solid #e6f1ff;
}

.applicant-info h4 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.applicant-info p {
  margin: 4px 0;
  color: #5a84b3;
  font-size: 14px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}

.el-form-item {
  margin-bottom: 16px;
}

/* 紧凑操作区域样式 */
.compact-action-section {
  margin-top: 16px;
  padding: 16px;
  background: #f8fbff;
  border: 1px solid #e6f1ff;
  border-radius: 8px;
}

.action-buttons-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.batch-item {
  flex: 0 0 auto;
}

.action-info {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.action-icon {
  font-size: 16px;
  width: 28px;
  height: 28px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.written-test-icon {
  background: #67c23a;
  color: white;
}

.interview-icon {
  background: #409eff;
  color: white;
}

.action-label {
  font-weight: 600;
  color: #2c3e50;
  font-size: 13px;
  white-space: nowrap;
}

.action-desc {
  color: #909399;
  font-size: 11px;
  white-space: nowrap;
}

.action-divider {
  width: 1px;
  height: 32px;
  background: #e6f1ff;
  flex-shrink: 0;
}

.compact-btn {
  min-width: 70px;
  height: 32px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 4px;
}

.batch-compact-btn {
  min-width: 90px;
  height: 32px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 4px;
}

/* 面试进度对话框样式 */
.progress-content {
  padding: 16px 0;
}

.progress-overview {
  margin-bottom: 20px;
}

.progress-overview h3 {
  margin-bottom: 12px;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

/* 笔试和面试进度部分 */
.test-progress-section,
.interview-progress-section,
.notes-section {
  margin-bottom: 20px;
}

.test-progress-section h3,
.interview-progress-section h3,
.notes-section h3 {
  margin-bottom: 12px;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.progress-card {
  border-radius: 8px;
  border: 1px solid #e6f1ff;
}

.progress-item {
  padding: 0;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e6f1ff;
}

.progress-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.progress-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f7ff;
}

.detail-item:last-child {
  border-bottom: none;
}

.label {
  font-weight: 500;
  color: #5a84b3;
  min-width: 80px;
  font-size: 13px;
}

.value {
  color: #2c3e50;
  font-weight: 500;
  font-size: 13px;
}

.value.score {
  color: #409eff;
  font-weight: 600;
  font-size: 14px;
}

.detail-actions {
  margin-top: 12px;
  text-align: right;
}

.no-interview {
  text-align: center;
  padding: 24px 16px;
}

.notes-section p {
  margin: 0;
  color: #5a84b3;
  line-height: 1.5;
  font-size: 14px;
}

/* 笔试报告样式 */
.exam-result-container {
  padding: 16px 0;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: #f0f7ff;
  border-radius: 8px;
  border: 1px solid #e6f1ff;
}

.score-display {
  display: flex;
  align-items: center;
  gap: 16px;
}

.score-circle {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.score-info h1 {
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.score-text {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #5a84b3;
}

.completion-time {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.result-stats {
  display: flex;
  gap: 12px;
}

.stat-item {
  text-align: center;
  padding: 12px;
  background: rgba(26, 111, 196, 0.1);
  border-radius: 6px;
  min-width: 70px;
}

.stat-label {
  display: block;
  font-size: 11px;
  color: #5a84b3;
  margin-bottom: 4px;
}

.stat-value {
  display: block;
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
}

.detailed-results {
  margin-top: 20px;
}

.detailed-results h3 {
  margin-bottom: 16px;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.questions-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.question-item {
  border: 1px solid #e6f1ff;
  border-radius: 8px;
  padding: 16px;
  background: #f8fbff;
}

.question-item.correct {
  border-color: #67c23a;
  background: #f0f9eb;
}

.question-item.wrong {
  border-color: #f56c6c;
  background: #fef0f0;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.question-number {
  font-weight: 600;
  color: #2c3e50;
  font-size: 13px;
}

.question-type {
  color: #909399;
  font-size: 12px;
}

.question-content {
  margin-bottom: 12px;
  color: #2c3e50;
  line-height: 1.5;
  font-size: 14px;
}

.answer-comparison {
  margin-bottom: 12px;
}

.answer-row {
  display: flex;
  margin-bottom: 6px;
  font-size: 13px;
}

.answer-label {
  min-width: 70px;
  color: #5a84b3;
  font-weight: 500;
}

.correct-answer {
  color: #67c23a;
  font-weight: 600;
}

.user-answer {
  color: #2c3e50;
  font-weight: 600;
}

.user-answer.wrong {
  color: #f56c6c;
}

.explanation {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 4px;
  border-left: 3px solid #409eff;
  color: #5a84b3;
  line-height: 1.5;
  font-size: 13px;
}

/* 面试报告样式 */
.interview-report-container {
  padding: 16px 0;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background: #f0f7ff;
  border-radius: 8px;
  border: 1px solid #e6f1ff;
}

.report-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.overall-score {
  display: flex;
  align-items: center;
  gap: 8px;
}

.score-label {
  font-size: 14px;
  color: #5a84b3;
}

.score-value {
  font-size: 18px;
  font-weight: bold;
  color: #2c3e50;
}

.report-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.answers-section h3,
.evaluation-section h3 {
  margin-bottom: 12px;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.answer-item {
  padding: 16px;
  border: 1px solid #e6f1ff;
  border-radius: 8px;
  background: #f8fbff;
  margin-bottom: 12px;
}

.answer-item .question {
  margin-bottom: 8px;
  color: #2c3e50;
  line-height: 1.5;
  font-size: 14px;
}

.answer-item .answer {
  margin-bottom: 8px;
  color: #5a84b3;
  line-height: 1.5;
  font-size: 14px;
}

.answer-score {
  color: #409eff;
  font-weight: 600;
  font-size: 14px;
}

.evaluation-section p {
  color: #5a84b3;
  line-height: 1.6;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .applicants-container {
    padding: 16px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .filter-row {
    flex-direction: column;
    gap: 12px;
  }

  .search-input {
    max-width: none;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }

  .detail-header {
    flex-direction: column;
    gap: 12px;
  }

  .contact-info {
    flex-direction: column;
    gap: 6px;
  }

  /* 横向布局响应式 */
  .horizontal-settings-container {
    flex-direction: column;
    gap: 12px;
  }

  .column-divider {
    display: none;
  }

  .settings-column {
    padding: 12px;
  }

  .action-buttons-row {
    flex-direction: column;
    gap: 12px;
  }

  .action-item {
    width: 100%;
    justify-content: space-between;
    padding: 12px;
    background: #f8fbff;
    border-radius: 6px;
    border: 1px solid #e6f1ff;
  }

  .action-divider {
    display: none;
  }

  .compact-btn,
  .batch-compact-btn {
    min-width: 80px;
  }

  .compact-action-section {
    padding: 12px;
    margin-top: 12px;
  }
}
</style>
