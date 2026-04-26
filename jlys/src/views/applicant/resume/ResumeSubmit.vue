<template>
  <div class="resume-submit-container">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">投递简历</h1>
          <p class="page-subtitle">浏览并一键投递你感兴趣的岗位</p>
        </div>
        <div class="stats-section">
          <div class="stat-item">
            <div class="stat-number">{{ jobs.length }}</div>
            <div class="stat-label">总职位</div>
          </div>
          <div class="stat-item">
            <div class="stat-number applied">{{ submittedJobIds.length }}</div>
            <div class="stat-label">已投递</div>
          </div>
          <div class="stat-item">
            <div class="stat-number not-applied">{{ jobs.length - submittedJobIds.length }}</div>
            <div class="stat-label">未投递</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-card>
        <div class="filter-row">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索职位名称、公司或地点..."
            :prefix-icon="Search"
            class="search-input"
            @input="handleSearch"
            clearable
          />
          <el-select
            v-model="searchForm.jobType"
            placeholder="职位类型"
            @change="handleSearch"
            clearable
          >
            <el-option label="全职" value="全职" />
            <el-option label="兼职" value="兼职" />
            <el-option label="实习" value="实习" />
          </el-select>
          <el-select
            v-model="searchForm.industry"
            placeholder="所属行业"
            @change="handleSearch"
            clearable
          >
            <el-option label="互联网" value="互联网" />
            <el-option label="金融" value="金融" />
            <el-option label="教育" value="教育" />
            <el-option label="医疗" value="医疗" />
            <el-option label="制造业" value="制造业" />
            <el-option label="其他" value="其他" />
          </el-select>
          <el-select
            v-model="searchForm.experience"
            placeholder="经验要求"
            @change="handleSearch"
            clearable
          >
            <el-option label="不限" value="不限" />
            <el-option label="应届生" value="应届生" />
            <el-option label="1-3年" value="1-3年" />
            <el-option label="3-5年" value="3-5年" />
            <el-option label="5年以上" value="5年以上" />
          </el-select>
          <el-select
            v-model="searchForm.education"
            placeholder="学历要求"
            @change="handleSearch"
            clearable
          >
            <el-option label="不限" value="不限" />
            <el-option label="大专" value="大专" />
            <el-option label="本科" value="本科" />
            <el-option label="硕士" value="硕士" />
            <el-option label="博士" value="博士" />
          </el-select>
          <el-select
            v-model="searchForm.applicationStatus"
            placeholder="投递状态"
            @change="handleSearch"
            clearable
          >
            <el-option label="全部职位" value="" />
            <el-option label="已投递" value="applied" />
            <el-option label="未投递" value="not_applied" />
          </el-select>
          <el-select
            v-model="searchForm.sortBy"
            placeholder="排序方式"
            @change="handleSort"
          >
            <el-option label="发布时间" value="postDate" />
            <el-option label="截止日期" value="expirationDate" />
            <el-option label="薪资范围" value="salary" />
          </el-select>
        </div>
      </el-card>
    </div>

    <!-- 结果统计和视图切换 -->
    <div class="result-summary" v-if="!loading">
      <span class="result-count">
        共找到 <strong>{{ filteredJobs.length }}</strong> 个职位
      </span>
      <div class="controls-section">
        <!-- 视图切换 -->
        <div class="view-toggle">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="card">
              <el-icon><Grid /></el-icon>
              卡片视图
            </el-radio-button>
            <el-radio-button label="list">
              <el-icon><List /></el-icon>
              列表视图
            </el-radio-button>
          </el-radio-group>
        </div>
      </div>
    </div>

    <!-- 职位卡片视图 -->
    <div v-if="viewMode === 'card'" class="job-cards-container">
      <el-row :gutter="20">
        <el-col :span="8" v-for="job in paginatedJobs" :key="job.jobId">
          <el-card class="job-card" shadow="hover">
            <div class="job-card-header">
              <div class="job-card-title" @click="viewJobDetail(job)">
                {{ job.title }}
              </div>
              <el-button
                :icon="isJobCollected(job.jobId) ? StarFilled : Star"
                :type="isJobCollected(job.jobId) ? 'warning' : 'default'"
                circle
                size="small"
                @click="handleToggleCollection(job)"
              />
            </div>
            <div class="job-card-company">{{ job.companyName }}</div>
            <div class="job-card-tags">
              <el-tag :type="getJobTypeTagType(job.jobType)" size="small">
                {{ job.jobType }}
              </el-tag>
              <el-tag type="info" size="small" v-if="job.experience">
                {{ job.experience }}
              </el-tag>
              <el-tag type="warning" size="small" v-if="job.education">
                {{ job.education }}
              </el-tag>
            </div>
            <div class="job-card-salary">
              ¥{{ formatSalary(job.minSalary) }} - ¥{{ formatSalary(job.maxSalary) }}
            </div>
            <div class="job-card-info">
              <span><el-icon><Location /></el-icon> {{ job.location }}</span>
              <span v-if="job.industry"><el-icon><OfficeBuilding /></el-icon> {{ job.industry }}</span>
            </div>
            <div class="job-card-footer">
              <el-button
                type="primary"
                size="small"
                @click="viewJobDetail(job)"
              >
                查看详情
              </el-button>
              <el-button
                v-if="!isJobApplied(job.jobId)"
                type="success"
                size="small"
                @click="applyJob(job)"
                :disabled="!job.isActive"
              >
                投递简历
              </el-button>
              <el-tag v-else type="success" size="small">已投递</el-tag>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 职位列表视图 -->
    <div v-if="viewMode === 'list'" class="job-list-section">
      <el-card>
        <el-table
          :data="paginatedJobs"
          v-loading="loading"
          stripe
          style="width: 100%"
          empty-text="暂无职位数据"
        >
          <el-table-column prop="title" label="职位名称" min-width="150">
            <template #default="{ row }">
              <div class="job-title">{{ row.title }}</div>
            </template>
          </el-table-column>

          <el-table-column prop="companyName" label="公司" width="120" show-overflow-tooltip />

          <el-table-column prop="jobType" label="类型" width="80">
            <template #default="{ row }">
              <el-tag :type="getJobTypeTagType(row.jobType)" size="small">
                {{ row.jobType }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="薪资范围" width="150">
            <template #default="{ row }">
              <div class="salary-range">
                ¥{{ formatSalary(row.minSalary) }} - ¥{{ formatSalary(row.maxSalary) }}
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="location" label="工作地点" width="120" />

          <el-table-column label="行业" width="120" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.industry || '暂无' }}
            </template>
          </el-table-column>

          <el-table-column label="公司规模" width="120" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.companyScale || '暂无' }}
            </template>
          </el-table-column>

          <el-table-column label="经验要求" width="100" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.experience || '不限' }}
            </template>
          </el-table-column>

          <el-table-column label="学历要求" width="100" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.education || '不限' }}
            </template>
          </el-table-column>

          <el-table-column prop="postDate" label="发布日期" width="120">
            <template #default="{ row }">
              {{ formatDate(row.postDate) }}
            </template>
          </el-table-column>

          <el-table-column prop="expirationDate" label="截止日期" width="120">
            <template #default="{ row }">
              {{ row.expirationDate ? formatDate(row.expirationDate) : '长期' }}
            </template>
          </el-table-column>

          <el-table-column label="投递状态" width="100">
            <template #default="{ row }">
              <el-tag
                :type="isJobApplied(row.jobId) ? 'success' : 'info'"
                size="small"
              >
                {{ isJobApplied(row.jobId) ? '已投递' : '未投递' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="招聘状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.isActive ? 'success' : 'danger'" size="small">
                {{ row.isActive ? '招聘中' : '已关闭' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button
                  type="text"
                  size="small"
                  @click="viewJobDetail(row)"
                >
                  <el-icon><View /></el-icon>
                  查看
                </el-button>
                <el-button
                  type="text"
                  size="small"
                  :class="{ 'collected': isJobCollected(row.jobId) }"
                  @click="handleToggleCollection(row)"
                >
                  <el-icon>
                    <StarFilled v-if="isJobCollected(row.jobId)" />
                    <Star v-else />
                  </el-icon>
                  {{ isJobCollected(row.jobId) ? '已收藏' : '收藏' }}
                </el-button>
                <el-button
                  v-if="!isJobApplied(row.jobId)"
                  type="success"
                  size="small"
                  @click="applyJob(row)"
                  :disabled="!row.isActive"
                >
                  投递
                </el-button>
                <el-tag v-else type="success" size="small">已投递</el-tag>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-container" v-if="pagination.total > 0">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[12, 24, 36, 48]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 职位详情对话框 -->
    <el-dialog
      v-model="jobDetailVisible"
      title="职位详情"
      width="800px"
      :before-close="closeJobDetail"
    >
      <div v-if="selectedJob" class="job-detail">
        <div class="job-header">
          <h2 class="job-title">{{ selectedJob.title }}</h2>
          <div class="job-meta">
            <span class="company">{{ selectedJob.companyName }}</span>
            <el-tag :type="getJobTypeTagType(selectedJob.jobType)">
              {{ selectedJob.jobType }}
            </el-tag>
          </div>
        </div>

        <div class="job-info">
          <div class="info-row">
            <div class="info-item">
              <strong>薪资范围：</strong>
              ¥{{ formatSalary(selectedJob.minSalary) }} - ¥{{ formatSalary(selectedJob.maxSalary) }}
            </div>
            <div class="info-item">
              <strong>工作地点：</strong>{{ selectedJob.location }}
            </div>
          </div>
          <div class="info-row">
            <div class="info-item">
              <strong>经验要求：</strong>
              <el-tag type="info" size="small">{{ selectedJob.experience || '不限' }}</el-tag>
            </div>
            <div class="info-item">
              <strong>学历要求：</strong>
              <el-tag type="warning" size="small">{{ selectedJob.education || '不限' }}</el-tag>
            </div>
          </div>
          <div class="info-row">
            <div class="info-item">
              <strong>所属行业：</strong>{{ selectedJob.industry || '暂无' }}
            </div>
            <div class="info-item">
              <strong>公司规模：</strong>{{ selectedJob.companyScale || '暂无' }}
            </div>
          </div>
          <div class="info-row">
            <div class="info-item">
              <strong>发布时间：</strong>{{ formatDate(selectedJob.postDate) }}
            </div>
            <div class="info-item">
              <strong>截止时间：</strong>
              {{ selectedJob.expirationDate ? formatDate(selectedJob.expirationDate) : '长期有效' }}
            </div>
          </div>
        </div>

        <div class="job-description">
          <h3>岗位职责</h3>
          <p>{{ selectedJob.description }}</p>
        </div>

        <div class="job-requirements">
          <h3>岗位要求</h3>
          <p>{{ selectedJob.requirements }}</p>
        </div>

        <div class="job-skills" v-if="selectedJob.skills">
          <h3>职位技能</h3>
          <div class="skills-tags">
            <el-tag
              v-for="skill in getSkillsList(selectedJob.skills)"
              :key="skill"
              type="primary"
              size="small"
              class="skill-tag"
            >
              {{ skill }}
            </el-tag>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeJobDetail">关闭</el-button>
          <el-button
            v-if="selectedJob && !isJobApplied(selectedJob.jobId)"
            type="primary"
            @click="applyJob(selectedJob)"
            :disabled="!selectedJob.isActive"
          >
            投递简历
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 投递简历对话框 -->
    <el-dialog
      v-model="applyDialogVisible"
      title="投递简历"
      width="500px"
      :before-close="closeApplyDialog"
    >
      <div v-if="applyingJob">
        <h3>{{ applyingJob.title }}</h3>
        <p class="company-name">{{ applyingJob.companyName }}</p>

        <el-form :model="applyForm" :rules="applyRules" ref="applyFormRef" label-width="100px">
          <el-form-item label="选择简历" prop="resumeId">
            <el-select v-model="applyForm.resumeId" placeholder="请选择要投递的简历" style="width: 100%">
              <el-option
                v-for="resume in resumeList"
                :key="resume.resumeId"
                :label="resume.filename"
                :value="resume.resumeId"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="求职信" prop="coverLetter">
            <el-input
              v-model="applyForm.coverLetter"
              type="textarea"
              :rows="4"
              placeholder="请简要介绍自己，说明为什么适合这个职位..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeApplyDialog">取消</el-button>
          <el-button type="primary" @click="submitApplication" :loading="submitting">
            确认投递
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search,
  View as ViewIcon,
  Promotion,
  Grid,
  List,
  Star,
  StarFilled,
  Location,
  OfficeBuilding
} from '@element-plus/icons-vue'
import { getActiveJobsWithPage } from '@/api/job'
import { getResumeList, submitJobApplication, analyzeJobApplication, getSubmittedJobs, getSubmittedJobIds } from '@/api/resume'
import { jobAPI } from '@/api/visualization'
import { logJobView, logJobCollect, logJobApply } from '@/api/userBehavior'

export default {
  name: 'ResumeSubmit',
  components: {
    Search,
    ViewIcon,
    Promotion
  },
  setup() {
    const loading = ref(false)
    const submitting = ref(false)
    const jobDetailVisible = ref(false)
    const applyDialogVisible = ref(false)
    const applyFormRef = ref(null)

    const jobs = ref([])
    const resumeList = ref([])
    const submittedJobIds = ref([])
    const collectedJobIds = ref(new Set())
    const selectedJob = ref(null)
    const applyingJob = ref(null)
    const viewMode = ref('card')
    const currentUserId = ref(null)

    const searchForm = reactive({
      keyword: '',
      jobType: '',
      industry: '',
      experience: '',
      education: '',
      applicationStatus: '',
      sortBy: 'postDate'
    })

    const applyForm = reactive({
      resumeId: '',
      coverLetter: ''
    })

    const applyRules = {
      resumeId: [
        { required: true, message: '请选择要投递的简历', trigger: 'change' }
      ]
    }

    // 分页相关状态
    const pagination = reactive({
      currentPage: 1,
      pageSize: 12,
      total: 0
    })

    // 计算属性
    const filteredJobs = computed(() => jobs.value)

    // 分页后的职位列表
    const paginatedJobs = computed(() => filteredJobs.value)

    const buildJobQuery = (current, size) => {
      const params = {
        current,
        size,
        keyword: searchForm.keyword || undefined,
        jobType: searchForm.jobType || undefined,
        location: undefined,
        industry: searchForm.industry || undefined,
        experience: searchForm.experience || undefined,
        education: searchForm.education || undefined,
        companyScale: undefined,
        applicationStatus: searchForm.applicationStatus || undefined,
        sortBy: searchForm.sortBy || 'postDate'
      }

      if (searchForm.applicationStatus && submittedJobIds.value.length > 0) {
        params.submittedJobIds = submittedJobIds.value.join(',')
      }

      return params
    }

    // 方法
    const loadJobs = async (current = pagination.currentPage, size = pagination.pageSize) => {
      loading.value = true
      try {
        const response = await getActiveJobsWithPage(buildJobQuery(current, size))
        console.log('简历投递页-职位列表响应:', response)
        if (response.success) {
          const result = response.data || {}
          jobs.value = result.jobs || []
          pagination.total = result.total || 0
          pagination.currentPage = result.current || current
          pagination.pageSize = result.size || size
          console.log('简历投递页-职位数据:', jobs.value)
          console.log('简历投递页-职位数量:', jobs.value.length)
          if (jobs.value.length > 0) {
            console.log('简历投递页-第一个职位示例:', jobs.value[0])
          }
        } else {
          ElMessage.error(response.message || '获取职位列表失败')
        }
      } catch (error) {
        console.error('Load jobs error:', error)
        ElMessage.error('获取职位列表失败')
      } finally {
        loading.value = false
      }
    }

    const loadResumeList = async () => {
      try {
        console.log('开始加载简历列表...')
        const response = await getResumeList()
        console.log('简历列表API响应:', response)

        // 处理返回数据
        if (response.code === 0 && response.data) {
          // 格式1: {code: 0, data: [...]}
          resumeList.value = response.data
          console.log('简历列表:', resumeList.value)
          console.log('简历数量:', resumeList.value.length)

          if (resumeList.value.length > 0) {
            console.log('第一个简历示例:', resumeList.value[0])
          } else {
            console.warn('简历列表为空，请先上传简历')
          }
        } else if (response.success && response.data) {
          // 格式2: {success: true, data: [...]}
          resumeList.value = response.data
          console.log('简历列表:', resumeList.value)
          console.log('简历数量:', resumeList.value.length)
        } else {
          console.error('获取简历列表失败:', response.message)
          ElMessage.error('获取简历列表失败')
        }
      } catch (error) {
        console.error('Load resume list error:', error)
        ElMessage.error('加载简历列表失败')
      }
    }

    const loadSubmittedJobs = async () => {
      try {
        console.log('开始加载已投递职位...')
        const response = await getSubmittedJobIds()
        console.log('API响应:', response)

        if (response.success) {
          // 后端返回的是职位ID数组
          submittedJobIds.value = response.data || []
          console.log('已投递的职位ID列表:', submittedJobIds.value)

          //  ElMessage.success(`加载了 ${submittedJobIds.value.length} 个已投递职位`)

          const toastKey = 'resume-submit-submitted-jobs-toast-shown'
          if (!sessionStorage.getItem(toastKey)) {
            ElMessage.success(`加载了 ${submittedJobIds.value.length} 个已投递职位`)
            sessionStorage.setItem(toastKey, '1')
          }
        } else {
          console.error('API返回失败:', response.message)
          ElMessage.error(response.message || '获取已投递职位失败')
        }
      } catch (error) {
        console.error('Load submitted jobs error:', error)
        ElMessage.error('加载已投递职位失败')
      }
    }

    const viewJobDetail = (job) => {
      selectedJob.value = job
      jobDetailVisible.value = true

      // 记录职位浏览行为
      console.log('记录浏览行为:', currentUserId.value, job.jobId)
      if (currentUserId.value && job.jobId) {
        logJobView(currentUserId.value, job.jobId)
          .then(res => {
            console.log('浏览行为记录成功:', res)
          })
          .catch(err => {
            console.error('记录浏览行为失败:', err)
          })
      } else {
        console.warn('无法记录浏览行为 - userId:', currentUserId.value, 'jobId:', job.jobId)
      }
    }

    const closeJobDetail = () => {
      jobDetailVisible.value = false
      selectedJob.value = null
    }

    const applyJob = async (job) => {
      if (resumeList.value.length === 0) {
        ElMessage.warning('请先上传简历')
        return
      }

      applyingJob.value = job
      applyForm.resumeId = ''
      applyForm.coverLetter = ''
      applyDialogVisible.value = true
    }

    const closeApplyDialog = () => {
      applyDialogVisible.value = false
      applyingJob.value = null
      if (applyFormRef.value) {
        applyFormRef.value.clearValidate()
      }
    }

    const submitApplication = async () => {
      if (!applyFormRef.value) return

      try {
        await applyFormRef.value.validate()

        submitting.value = true

        const applicationData = {
          jobId: applyingJob.value.jobId,
          resumeId: applyForm.resumeId,
          coverLetter: applyForm.coverLetter
        }

        // 第一步：投递简历
        console.log('开始投递简历...')
        const submitResponse = await submitJobApplication(applicationData)

        if (submitResponse.success) {
          // 投递成功，立即显示在前端
          ElMessage.success('简历投递成功!')
          submittedJobIds.value.push(applyingJob.value.jobId)
          closeApplyDialog()
          closeJobDetail()

          // 记录投递行为
          if (currentUserId.value && applyingJob.value.jobId) {
            logJobApply(currentUserId.value, applyingJob.value.jobId).catch(err => {
              console.error('记录投递行为失败:', err)
            })
          }

          // 第二步：后台异步进行筛选打分
          console.log('开始后台筛选打分...')
          analyzeApplicationInBackground(applicationData)

        } else {
          ElMessage.error(submitResponse.message || '投递失败')
        }
      } catch (error) {
        if (error.name !== 'ValidationError') {
          console.error('Submit application error:', error)
          // ElMessage.error('投递失败，请重试')
        }
      } finally {
        submitting.value = false
      }
    }

    // 后台异步进行简历筛选打分
    const analyzeApplicationInBackground = async (applicationData) => {
      try {
        console.log('调用筛选打分API...')
        const analyzeResponse = await analyzeJobApplication(applicationData)

        if (analyzeResponse.success) {
          console.log('筛选打分完成:', analyzeResponse.data)
          // 可以选择性地显示一个小提示
          ElMessage.info('简历分析完成')
        } else {
          console.error('筛选打分失败:', analyzeResponse.message)
        }
      } catch (error) {
        console.error('筛选打分异常:', error)
        // 筛选打分失败不影响投递结果，只记录日志
      }
    }

    const isJobApplied = (jobId) => {
      return submittedJobIds.value.includes(jobId)
    }

    const isJobCollected = (jobId) => {
      return collectedJobIds.value.has(jobId) || collectedJobIds.value.has(String(jobId))
    }

    const handleToggleCollection = async (job) => {
      const jobId = job.jobId || job.id
      if (!currentUserId.value) {
        ElMessage.error('用户信息获取失败，操作无法完成')
        return
      }
      if (!jobId) {
        ElMessage.error('职位ID获取失败')
        return
      }
      try {
        // 先判断当前收藏状态
        const wasCollected = isJobCollected(jobId)

        // 使用统一的收藏接口（支持切换）
        const response = await jobAPI.collectJob(currentUserId.value, jobId)
        console.log('收藏操作响应:', response)

        // 注意：axios 返回的数据在 response.data 中
        const result = response.data || response

        if (result.code === 0) {
          // 切换收藏状态
          if (wasCollected) {
            // 之前已收藏，现在取消收藏
            collectedJobIds.value.delete(jobId)
            collectedJobIds.value.delete(String(jobId))
            ElMessage.success(result.data || '取消收藏成功')
          } else {
            // 之前未收藏，现在收藏
            collectedJobIds.value.add(jobId)
            ElMessage.success(result.data || '收藏成功')

            // 记录收藏行为
            console.log('记录收藏行为:', currentUserId.value, jobId)
            logJobCollect(currentUserId.value, jobId).catch(err => {
              console.error('记录收藏行为失败:', err)
            })
          }
        } else {
          ElMessage.error(result.message || '操作失败')
        }
      } catch (error) {
        console.error('收藏操作失败:', error)
        ElMessage.error('操作失败，请稍后重试')
      }
    }

    const fetchCollectedJobs = async () => {
      if (!currentUserId.value) {
        console.error('User ID not found in local storage')
        return
      }
      try {
        console.log('开始获取收藏列表，用户ID:', currentUserId.value)
        const response = await jobAPI.getCollectedJobs(currentUserId.value)
        console.log('收藏列表API原始响应:', response)

        // 兼容两种返回格式
        const result = response.data || response
        console.log('收藏列表结果:', result)

        if (result.code === 0 && result.data) {
          // 将收藏的职位ID存入Set
          const ids = result.data.map(job => job.jobId || job.id)
          collectedJobIds.value = new Set(ids)
          console.log('收藏的职位ID列表:', Array.from(collectedJobIds.value))
          console.log('收藏数量:', collectedJobIds.value.size)
        } else {
          console.error('获取收藏列表失败:', result.message)
        }
      } catch (error) {
        console.error('获取收藏列表异常:', error)
      }
    }

    // 分页事件处理
    const handleSizeChange = (newSize) => {
      pagination.pageSize = newSize
      pagination.currentPage = 1 // 重置到第一页
      loadJobs(1, newSize)
    }

    const handleCurrentChange = (newPage) => {
      loadJobs(newPage, pagination.pageSize)
      pagination.currentPage = newPage
    }

    const handleSearch = () => {
      pagination.currentPage = 1
      loadJobs(1, pagination.pageSize)
    }

    const handleSort = () => {
      pagination.currentPage = 1
      loadJobs(1, pagination.pageSize)
    }

    // 工具方法
    const formatSalary = (salary) => {
      if (!salary) return '0'
      return salary.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    }

    const formatDate = (date) => {
      if (!date) return '-'
      return new Date(date).toLocaleDateString('zh-CN')
    }

    // 解析技能列表
    const getSkillsList = (skills) => {
      if (!skills) return []
      if (typeof skills === 'string') {
        // 如果是字符串，按逗号分割并去除空格
        return skills.split(',').map(skill => skill.trim()).filter(skill => skill)
      }
      if (Array.isArray(skills)) {
        return skills
      }
      try {
        // 如果是JSON字符串，尝试解析
        const parsed = JSON.parse(skills)
        return Array.isArray(parsed) ? parsed : []
      } catch (e) {
        // 解析失败，按逗号分割
        return skills.toString().split(',').map(skill => skill.trim()).filter(skill => skill)
      }
    }

    const getJobTypeTagType = (jobType) => {
      const typeMap = {
        全职: 'primary',
        兼职: 'success',
        实习: 'warning'
      }
      return typeMap[jobType] || 'info'
    }

    onMounted(() => {
      // 从本地存储获取 userId
      const userIdFromStorage = localStorage.getItem('userId')
      if (userIdFromStorage) {
        currentUserId.value = parseInt(userIdFromStorage)
      } else {
        console.error('User ID not found in local storage')
      }

      loadJobs()
      loadResumeList()
      loadSubmittedJobs()

      if (currentUserId.value) {
        fetchCollectedJobs()
      }
    })

    return {
      loading,
      submitting,
      jobDetailVisible,
      applyDialogVisible,
      applyFormRef,
      jobs,
      resumeList,
      submittedJobIds,
      collectedJobIds,
      selectedJob,
      applyingJob,
      searchForm,
      applyForm,
      applyRules,
      filteredJobs,
      paginatedJobs,
      viewMode,
      pagination,
      loadJobs,
      loadResumeList,
      loadSubmittedJobs,
      fetchCollectedJobs,
      viewJobDetail,
      closeJobDetail,
      applyJob,
      closeApplyDialog,
      submitApplication,
      analyzeApplicationInBackground,
      isJobApplied,
      isJobCollected,
      handleToggleCollection,
      handleSearch,
      handleSort,
      handleSizeChange,
      handleCurrentChange,
      formatSalary,
      formatDate,
      getSkillsList,
      getJobTypeTagType,
      // 图标
      Search,
      ViewIcon,
      Promotion,
      Grid,
      List,
      Star,
      StarFilled,
      Location,
      OfficeBuilding
    }
  }
}
</script>
<style scoped>
.resume-submit-container {
  padding: 16px;
  background: linear-gradient(135deg, #f7fbff 0%, #ebf5ff 100%);
  min-height: 100vh;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  background: linear-gradient(120deg, #4a90e2 0%, #5aa0ff 100%);
  border-radius: 10px;
  padding: 20px 24px;
  margin-bottom: 16px;
  color: white;
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.2);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.title-section .page-title {
  font-size: 22px;
  margin-bottom: 6px;
  font-weight: 700;
}

.title-section .page-subtitle {
  font-size: 14px;
  opacity: 0.9;
}

.stats-section {
  display: flex;
  gap: 20px;
  align-items: center;
  background: rgba(255, 255, 255, 0.15);
  padding: 12px 16px;
  border-radius: 8px;
  backdrop-filter: blur(5px);
}

.stat-item {
  text-align: center;
  min-width: 70px;
}

.stat-number {
  font-size: 20px;
  font-weight: 800;
  margin-bottom: 4px;
}

.stat-number.applied {
  color: #a0e7ff;
}

.stat-number.not-applied {
  color: #c3e6ff;
}

.stat-label {
  font-size: 13px;
  opacity: 0.85;
}

.filter-section {
  margin-bottom: 16px;
}

.filter-section :deep(.el-card) {
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: none;
  background: rgba(255, 255, 255, 0.9);
}

.filter-section :deep(.el-card__body) {
  padding: 16px;
}

.filter-row {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: nowrap;
  overflow-x: auto;
  scrollbar-width: none;
  padding: 4px;
}

.filter-row::-webkit-scrollbar {
  display: none;
}

.search-input {
  flex: 1;
  min-width: 180px;
}

.search-input :deep(.el-input__inner) {
  border-radius: 8px;
  height: 36px;
}

.filter-row .el-select {
  min-width: 120px;
}

.filter-row .el-select :deep(.el-input__inner) {
  border-radius: 8px;
  height: 36px;
}

.job-list-section {
  margin-bottom: 24px;
}

.job-list-section :deep(.el-card) {
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: none;
  overflow: hidden;
}

.job-list-section :deep(.el-table) {
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid #e1e8f0;
  font-size: 13px;
}

.job-list-section :deep(.el-table__header) th {
  background: linear-gradient(135deg, #f0f7ff 0%, #e6f2ff 100%) !important;
  color: #2c6fbb;
  font-weight: 600;
  height: 48px;
  border-bottom: 1px solid #dbeafe !important;
}

.job-list-section :deep(.el-table__row) td {
  padding: 10px 0;
  height: 56px;
}

.job-list-section :deep(.el-table__row:hover) td {
  background-color: #f5f9ff !important;
}

.job-title {
  color: #2c6fbb;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
}

.job-title:hover {
  color: #1a56a6;
  text-decoration: underline;
}

.salary-range {
  color: #e6a23c;
  font-weight: 500;
  font-size: 13px;
}

.action-buttons {
  display: flex;
  gap: 6px;
  align-items: center;
}

.action-buttons .el-button {
  font-size: 12px;
  padding: 6px 10px;
  height: auto;
}

.job-detail {
  padding: 8px 0;
}

.job-header {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8f1fb;
}

.job-header .job-title {
  font-size: 18px;
  margin-bottom: 8px;
  color: #2c6fbb;
}

.job-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.company {
  color: #409EFF;
  font-weight: 500;
  font-size: 15px;
}

.job-info {
  margin-bottom: 20px;
  background: #f9fbfe;
  padding: 12px;
  border-radius: 8px;
}

.info-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 12px;
}

.info-item {
  flex: 1;
  min-width: 180px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.info-item strong {
  color: #303133;
  white-space: nowrap;
}

.job-description, .job-requirements, .job-skills {
  margin-bottom: 20px;
}

.job-description h3, .job-requirements h3, .job-skills h3 {
  font-size: 15px;
  color: #2c6fbb;
  margin-bottom: 10px;
  padding-bottom: 6px;
  border-bottom: 1px solid #e0e7ff;
}

.job-description p, .job-requirements p {
  color: #606266;
  line-height: 1.6;
  text-align: justify;
  padding: 0 4px;
  font-size: 13px;
}

.skills-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.skill-tag {
  background: linear-gradient(135deg, #ecf5ff 0%, #d9ecff 100%);
  color: #409EFF;
  border-color: #b3d8ff;
  border-radius: 4px;
  font-size: 12px;
}

.company-name {
  color: #67c23a;
  margin-bottom: 16px;
  font-size: 13px;
  font-weight: 500;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

:deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: linear-gradient(90deg, #f0f7ff 0%, #e1efff 100%);
  padding: 14px 20px;
  margin: 0;
  border-bottom: 1px solid #e4edf9;
}

:deep(.el-dialog__title) {
  color: #2c6fbb;
  font-weight: 600;
  font-size: 16px;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-form) {
  padding: 8px 0;
}

:deep(.el-form-item) {
  margin-bottom: 16px;
}

:deep(.el-form-item__label) {
  color: #606266;
  font-weight: 500;
  font-size: 13px;
}

:deep(.el-select), :deep(.el-input) {
  border-radius: 6px;
}

:deep(.el-button--primary) {
  background: linear-gradient(90deg, #4a90e2 0%, #5aa0ff 100%);
  border: none;
  border-radius: 6px;
  height: 34px;
  padding: 0 14px;
  font-weight: 500;
  font-size: 13px;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(90deg, #3a80d2 0%, #4a90f2 100%);
}

:deep(.el-button--success) {
  background: linear-gradient(90deg, #67c23a 0%, #85ce61 100%);
  border: none;
  border-radius: 6px;
  height: 34px;
  padding: 0 14px;
  font-weight: 500;
  font-size: 13px;
}

:deep(.el-button--success:hover) {
  background: linear-gradient(90deg, #57b22a 0%, #75be51 100%);
}

:deep(.el-tag--primary) {
  background: linear-gradient(90deg, #ecf5ff 0%, #d9ecff 100%);
  color: #409EFF;
  border-color: #b3d8ff;
  border-radius: 4px;
  font-size: 12px;
}

:deep(.el-tag--success) {
  background: linear-gradient(90deg, #f0f9eb 0%, #e1f3d8 100%);
  color: #67c23a;
  border-color: #c2e7b0;
  border-radius: 4px;
  font-size: 12px;
}

:deep(.el-tag--warning) {
  background: linear-gradient(90deg, #fdf6ec 0%, #faecd8 100%);
  color: #e6a23c;
  border-color: #f5dab1;
  border-radius: 4px;
  font-size: 12px;
}

:deep(.el-tag--info) {
  background: linear-gradient(90deg, #f4f4f5 0%, #e9e9eb 100%);
  color: #909399;
  border-color: #d3d4d6;
  border-radius: 4px;
  font-size: 12px;
}

:deep(.el-tag--danger) {
  background: linear-gradient(90deg, #fef0f0 0%, #fde2e2 100%);
  color: #f56c6c;
  border-color: #fbc4c4;
  border-radius: 4px;
  font-size: 12px;
}

/* 结果统计和视图切换 */
.result-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px 20px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.result-count {
  color: #606266;
  font-size: 14px;
}

.result-count strong {
  color: #409eff;
  font-weight: 600;
  font-size: 16px;
}

.controls-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.view-toggle {
  display: flex;
  align-items: center;
}

.view-toggle .el-icon {
  margin-right: 4px;
}

/* 职位卡片视图 */
.job-cards-container {
  margin-bottom: 24px;
}

.job-card {
  margin-bottom: 20px;
  border-radius: 10px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.job-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(74, 144, 226, 0.15);
}

.job-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.job-card-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c6fbb;
  cursor: pointer;
  flex: 1;
  margin-right: 10px;
  line-height: 1.4;
}

.job-card-title:hover {
  color: #1a56a6;
  text-decoration: underline;
}

.job-card-company {
  color: #606266;
  font-size: 14px;
  margin-bottom: 12px;
  font-weight: 500;
}

.job-card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.job-card-salary {
  color: #e6a23c;
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 12px;
}

.job-card-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  color: #909399;
  font-size: 13px;
  margin-bottom: 16px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.job-card-info span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.job-card-footer {
  display: flex;
  gap: 10px;
  align-items: center;
}

.job-card-footer .el-button {
  flex: 1;
}

/* 列表视图操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.action-buttons .el-button--text {
  padding: 4px 8px;
}

.action-buttons .collected {
  color: #f56c6c;
}

.action-buttons .collected .el-icon {
  color: #f56c6c;
}

/* 分页容器 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding: 20px 0;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .resume-submit-container {
    padding: 12px;
  }

  .page-header {
    padding: 16px;
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .stats-section {
    width: 100%;
    justify-content: space-around;
    gap: 15px;
    padding: 10px;
  }

  .stat-item {
    min-width: 60px;
  }

  .stat-number {
    font-size: 18px;
  }

  .filter-section :deep(.el-card__body) {
    padding: 12px;
  }

  .filter-row {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }

  .search-input, .filter-row .el-select {
    width: 100%;
    min-width: auto;
  }

  .job-list-section :deep(.el-table__header) th {
    padding: 8px 0;
    font-size: 12px;
  }

  .job-list-section :deep(.el-table__row) td {
    padding: 8px 0;
    font-size: 12px;
  }

  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }

  .action-buttons .el-button {
    font-size: 11px;
    padding: 4px 8px;
  }

  .job-cards-container .el-col {
    margin-bottom: 16px;
  }

  .job-card {
    margin-bottom: 0;
  }

  .pagination-container {
    padding: 16px 0;
  }
}
</style>

