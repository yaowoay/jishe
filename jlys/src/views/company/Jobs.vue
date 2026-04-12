<template>
  <div class="jobs-container">
    <!-- 页面标题和操作区 -->
    <div class="header-section">
      <div class="title-area">
        <h1 class="page-title">职位管理</h1>
        <p class="page-subtitle">管理您的招聘职位</p>
      </div>
      <el-button type="primary" @click="openAddJobDialog" :icon="Plus">
        发布新职位
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <div class="filter-row">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索职位名称、类型或地点..."
          :prefix-icon="Search"
          class="search-input"
          @input="handleSearch"
          clearable
        />
        <el-select
          v-model="searchForm.jobType"
          placeholder="所有类型"
          @change="handleSearch"
          clearable
        >
          <el-option label="全职" value="全职" />
          <el-option label="兼职" value="兼职" />
          <el-option label="实习" value="实习" />
        </el-select>
        <el-select
          v-model="searchForm.status"
          placeholder="所有状态"
          @change="handleSearch"
          clearable
        >
          <el-option label="活跃" value="active" />
          <el-option label="已结束" value="inactive" />
        </el-select>
      </div>
    </div>

    <!-- 职位列表 -->
    <div class="table-section">
      <el-table
        :data="filteredJobs"
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

        <el-table-column prop="jobType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getJobTypeTagType(row.jobType)">
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

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'danger'">
              {{ row.isActive ? '活跃' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="postDate" label="发布日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.postDate) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                type="primary"
                size="small"
                @click="viewJobDetail(row)"
                :icon="ViewIcon"
              >
                查看
              </el-button>
              <el-button
                type="warning"
                size="small"
                @click="editJob(row)"
                :icon="Edit"
              >
                编辑
              </el-button>
              <el-button
                :type="row.isActive ? 'danger' : 'success'"
                size="small"
                @click="toggleJobStatus(row)"
              >
                {{ row.isActive ? '关闭' : '重新发布' }}
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 发布/编辑职位对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="800px"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="jobFormRef"
        :model="jobForm"
        :rules="jobFormRules"
        label-width="100px"
        class="job-form"
      >
        <div class="form-row">
          <el-form-item label="职位名称" prop="title" class="form-item-half">
            <el-input
              v-model="jobForm.title"
              placeholder="请输入职位名称"
              :prefix-icon="Briefcase"
            />
          </el-form-item>

          <el-form-item label="职位类型" prop="jobType" class="form-item-half">
            <el-select v-model="jobForm.jobType" placeholder="请选择类型">
              <el-option label="全职" value="全职" />
              <el-option label="兼职" value="兼职" />
              <el-option label="实习" value="实习" />
            </el-select>
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="经验要求" prop="experience" class="form-item-half">
            <el-select v-model="jobForm.experience" placeholder="请选择经验要求">
              <el-option label="不限" value="不限" />
              <el-option label="应届毕业生" value="应届毕业生" />
              <el-option label="1年以下" value="1年以下" />
              <el-option label="1-3年" value="1-3年" />
              <el-option label="3-5年" value="3-5年" />
              <el-option label="5-10年" value="5-10年" />
              <el-option label="10年以上" value="10年以上" />
            </el-select>
          </el-form-item>

          <el-form-item label="学历要求" prop="education" class="form-item-half">
            <el-select v-model="jobForm.education" placeholder="请选择学历要求">
              <el-option label="不限" value="不限" />
              <el-option label="高中及以上" value="高中及以上" />
              <el-option label="大专及以上" value="大专及以上" />
              <el-option label="本科及以上" value="本科及以上" />
              <el-option label="硕士及以上" value="硕士及以上" />
              <el-option label="博士及以上" value="博士及以上" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="岗位职责" prop="description">
          <div class="textarea-with-button">
            <el-input
              v-model="jobForm.description"
              type="textarea"
              :rows="4"
              placeholder="请详细描述该职位的工作内容和职责..."
            />
          </div>
        </el-form-item>

        <el-form-item label="岗位要求" prop="requirements">
          <el-input
            v-model="jobForm.requirements"
            type="textarea"
            :rows="4"
            placeholder="请列出该职位的具体要求，如技能、经验等..."
          />
        </el-form-item>

        <el-form-item label="职位技能" prop="skills">
          <el-input
            v-model="jobForm.skills"
            type="textarea"
            :rows="2"
            placeholder="请列出该职位需要的技能，用逗号分隔，如：Java,SpringBoot,微服务"
          />
        </el-form-item>

        <div class="form-row">
          <el-form-item label="最低薪资" prop="minSalary" class="form-item-half">
            <el-input-number
              v-model="jobForm.minSalary"
              :min="0"
              :step="500"
              placeholder="如: 8000"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="最高薪资" prop="maxSalary" class="form-item-half">
            <el-input-number
              v-model="jobForm.maxSalary"
              :min="0"
              :step="500"
              placeholder="如: 15000"
              style="width: 100%"
            />
          </el-form-item>
        </div>

        <el-form-item label="工作地点" prop="location">
          <el-input
            v-model="jobForm.location"
            placeholder="请输入工作地点"
            :prefix-icon="Location"
          />
        </el-form-item>

        <el-form-item label="截止日期" prop="expirationDate">
          <el-date-picker
            v-model="jobForm.expirationDate"
            type="date"
            placeholder="选择截止日期"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <div class="footer-left">
            <el-button
              type="warning"
              @click="optimizeJD"
              :loading="optimizing"
              :disabled="!canOptimize"
            >
              优化JD
            </el-button>
          </div>
          <div class="footer-right">
            <el-button @click="handleDialogClose">取消</el-button>
            <el-button type="primary" @click="submitJob" :loading="submitting">
              {{ isEditing ? '更新职位' : '发布职位' }}
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Search,
  View as ViewIcon,
  Edit,
  Briefcase,
  Location
} from '@element-plus/icons-vue'
import {
  getCompanyJobs,
  createJob,
  updateJob,
  toggleJobStatus as toggleJobStatusAPI,
  optimizeJobDescription
} from '@/api/job'

export default {
  name: 'CompanyJobs',
  components: {
    Plus,
    Search,
    ViewIcon,
    Edit,
    Briefcase,
    Location
  },
  setup() {
    // 响应式数据
    const loading = ref(false)
    const submitting = ref(false)
    const optimizing = ref(false)
    const dialogVisible = ref(false)
    const isEditing = ref(false)
    const jobFormRef = ref(null)

    // 职位列表
    const jobs = ref([])

    // 搜索表单
    const searchForm = reactive({
      keyword: '',
      jobType: '',
      status: ''
    })

    // 职位表单
    const jobForm = reactive({
      jobId: null,
      title: '',
      jobType: '',
      experience: '',
      education: '',
      description: '',
      requirements: '',
      skills: '',
      minSalary: null,
      maxSalary: null,
      location: '',
      expirationDate: null
    })

    // 表单验证规则
    const jobFormRules = {
      title: [
        { required: true, message: '请输入职位名称', trigger: 'blur' },
        { min: 2, max: 50, message: '职位名称长度在 2 到 50 个字符', trigger: 'blur' }
      ],
      jobType: [
        { required: true, message: '请选择职位类型', trigger: 'change' }
      ],
      experience: [
        { required: true, message: '请选择经验要求', trigger: 'change' }
      ],
      education: [
        { required: true, message: '请选择学历要求', trigger: 'change' }
      ],
      description: [
        { required: true, message: '请输入岗位职责', trigger: 'blur' },
        { min: 10, message: '岗位职责至少10个字符', trigger: 'blur' }
      ],
      requirements: [
        { required: true, message: '请输入岗位要求', trigger: 'blur' },
        { min: 10, message: '岗位要求至少10个字符', trigger: 'blur' }
      ],
      minSalary: [
        { required: true, message: '请输入最低薪资', trigger: 'blur' },
        { type: 'number', min: 0, message: '薪资不能为负数', trigger: 'blur' }
      ],
      maxSalary: [
        { required: true, message: '请输入最高薪资', trigger: 'blur' },
        { type: 'number', min: 0, message: '薪资不能为负数', trigger: 'blur' }
      ],
      location: [
        { required: true, message: '请输入工作地点', trigger: 'blur' }
      ]
    }

    // 计算属性
    const dialogTitle = computed(() => {
      return isEditing.value ? '编辑职位' : '发布新职位'
    })

    const filteredJobs = computed(() => {
      let result = jobs.value

      // 关键词搜索
      if (searchForm.keyword) {
        const keyword = searchForm.keyword.toLowerCase()
        result = result.filter(job =>
          job.title.toLowerCase().includes(keyword) ||
          job.jobType.toLowerCase().includes(keyword) ||
          job.location.toLowerCase().includes(keyword)
        )
      }

      // 职位类型筛选
      if (searchForm.jobType) {
        result = result.filter(job => job.jobType === searchForm.jobType)
      }

      // 状态筛选
      if (searchForm.status) {
        const isActive = searchForm.status === 'active'
        result = result.filter(job => job.isActive === isActive)
      }

      return result
    })

    // 判断是否可以优化JD
    const canOptimize = computed(() => {
      return jobForm.title &&
             jobForm.jobType &&
             jobForm.experience &&
             jobForm.education &&
             jobForm.location &&
             jobForm.minSalary &&
             jobForm.maxSalary
    })

    // 方法
    const loadJobs = async () => {
      loading.value = true
      try {
        const response = await getCompanyJobs()
        if (response.success) {
          jobs.value = response.data || []
        } else {
          ElMessage.error(response.message || '获取职位列表失败')
        }
      } catch (error) {
        console.error('获取职位列表失败:', error)
        ElMessage.error('获取职位列表失败')
      } finally {
        loading.value = false
      }
    }

    const openAddJobDialog = () => {
      isEditing.value = false
      resetJobForm()
      dialogVisible.value = true
    }

    const editJob = (job) => {
      isEditing.value = true
      Object.assign(jobForm, {
        jobId: job.jobId,
        title: job.title,
        jobType: job.jobType,
        experience: job.experience || '',
        education: job.education || '',
        description: job.description,
        requirements: job.requirements,
        skills: job.skills || '',
        minSalary: job.minSalary,
        maxSalary: job.maxSalary,
        location: job.location,
        expirationDate: job.expirationDate ? new Date(job.expirationDate) : null
      })
      dialogVisible.value = true
    }

    const resetJobForm = () => {
      Object.assign(jobForm, {
        jobId: null,
        title: '',
        jobType: '',
        experience: '',
        education: '',
        description: '',
        requirements: '',
        skills: '',
        minSalary: null,
        maxSalary: null,
        location: '',
        expirationDate: null
      })
      if (jobFormRef.value) {
        jobFormRef.value.clearValidate()
      }
    }

    const handleDialogClose = () => {
      dialogVisible.value = false
      resetJobForm()
    }

    // 优化JD功能
    const optimizeJD = async () => {
      if (!canOptimize.value) {
        ElMessage.warning('请先填写必要的职位信息（职位名称、类型、经验要求、学历要求、地点、薪资）')
        return
      }

      optimizing.value = true
      try {
        // 构建薪资字符串
        const salaryRange = `${jobForm.minSalary}-${jobForm.maxSalary}K/月`

        // 构建请求数据
        const requestData = {
          job: jobForm.title,
          jobType: jobForm.jobType,
          salary: salaryRange,
          education: jobForm.education,
          experience: jobForm.experience,
          place: jobForm.location,
          description: jobForm.description || '负责相关工作内容...',
          requirement: jobForm.requirements || '具备相关技能要求...',
          user: 'company-user' // 可以根据实际情况修改
        }

        // 调用工作流API
        const result = await optimizeJobDescription(requestData)

        if (result.success && result.data?.data?.outputs) {
          const outputs = result.data.data.outputs

          // 更新表单数据
          if (outputs.job_duty && Array.isArray(outputs.job_duty)) {
            jobForm.description = outputs.job_duty.join('\n')
          }

          if (outputs.job_requirement && Array.isArray(outputs.job_requirement)) {
            jobForm.requirements = outputs.job_requirement.join('\n')
          }

          if (outputs.job_skill && Array.isArray(outputs.job_skill)) {
            jobForm.skills = outputs.job_skill.join(', ')
          }

          ElMessage.success('JD优化成功！')
        } else {
          ElMessage.error(result.message || 'JD优化失败')
        }
      } catch (error) {
        console.error('优化JD失败:', error)
        ElMessage.error('优化JD失败，请重试')
      } finally {
        optimizing.value = false
      }
    }

    const submitJob = async () => {
      if (!jobFormRef.value) return

      try {
        await jobFormRef.value.validate()

        // 验证薪资范围
        if (jobForm.maxSalary <= jobForm.minSalary) {
          ElMessage.error('最高薪资必须大于最低薪资')
          return
        }

        submitting.value = true

        const jobData = {
          ...jobForm,
          expirationDate: jobForm.expirationDate
            ? jobForm.expirationDate.toISOString().split('T')[0]
            : null
        }

        const response = isEditing.value
          ? await updateJob(jobData)
          : await createJob(jobData)

        if (response.success) {
          ElMessage.success(isEditing.value ? '职位更新成功' : '职位发布成功')
          dialogVisible.value = false
          resetJobForm()
          await loadJobs()
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        if (error.name !== 'ValidationError') {
          console.error('提交职位失败:', error)
          ElMessage.error('操作失败，请重试')
        }
      } finally {
        submitting.value = false
      }
    }

    const toggleJobStatus = async (job) => {
      try {
        const action = job.isActive ? '关闭' : '重新发布'
        await ElMessageBox.confirm(
          `确定要${action}职位"${job.title}"吗？`,
          '确认操作',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        const response = await toggleJobStatusAPI(job.jobId)

        if (response.success) {
          ElMessage.success('操作成功')
          await loadJobs()
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('切换职位状态失败:', error)
          ElMessage.error('操作失败，请重试')
        }
      }
    }

    const viewJobDetail = (job) => {
      // 这里可以跳转到职位详情页面或显示详情对话框
      ElMessage.info('查看职位详情功能待实现')
    }

    const handleSearch = () => {
      // 搜索逻辑已在计算属性中实现
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

    const getJobTypeTagType = (jobType) => {
      const typeMap = {
        全职: 'primary',
        兼职: 'success',
        实习: 'warning'
      }
      return typeMap[jobType] || 'info'
    }

    const disabledDate = (time) => {
      return time.getTime() < Date.now() - 8.64e7 // 不能选择今天之前的日期
    }

    // 生命周期
    onMounted(() => {
      loadJobs()
    })

    return {
      // 响应式数据
      loading,
      submitting,
      optimizing,
      dialogVisible,
      isEditing,
      jobFormRef,
      jobs,
      searchForm,
      jobForm,
      jobFormRules,

      // 计算属性
      dialogTitle,
      filteredJobs,
      canOptimize,

      // 方法
      loadJobs,
      openAddJobDialog,
      editJob,
      resetJobForm,
      handleDialogClose,
      optimizeJD,
      submitJob,
      toggleJobStatus,
      viewJobDetail,
      handleSearch,
      formatSalary,
      formatDate,
      getJobTypeTagType,
      disabledDate,

      // 图标
      Plus,
      Search,
      ViewIcon,
      Edit,
      Briefcase,
      Location
    }
  }
}
</script>

<style scoped>
.jobs-container {
  padding: 24px;
  background: #f5f9ff;
  min-height: calc(100vh - 64px);
}

.header-section {
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

.title-area {
  flex: 1;
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
}

.filter-section {
  margin-bottom: 24px;
  padding: 20px;
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e6f1ff;
  box-shadow: 0 2px 8px rgba(26, 111, 196, 0.1);
}

.filter-row {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  flex: 1;
  max-width: 400px;
}

.table-section {
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e6f1ff;
  box-shadow: 0 2px 8px rgba(26, 111, 196, 0.1);
  overflow: hidden;
}

.job-title {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.salary-range {
  font-weight: 600;
  color: #67c23a;
  font-size: 13px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-buttons .el-button {
  margin: 0;
  font-size: 12px;
  padding: 6px 10px;
  height: auto;
}

.job-form {
  padding: 0 8px;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-item-half {
  flex: 1;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0 0;
  border-top: 1px solid #e6f1ff;
  margin-top: 16px;
}

.footer-left {
  flex: 1;
}

.footer-right {
  display: flex;
  gap: 8px;
}

.textarea-with-button {
  position: relative;
}

/* Element Plus 样式覆盖 */
:deep(.el-table) {
  border: 1px solid #e6f1ff;
  border-radius: 8px;
}

:deep(.el-table__header-wrapper) {
  background: #f8fbff;
}

:deep(.el-table th) {
  background: #f8fbff;
  color: #2c3e50;
  font-weight: 600;
  border-bottom: 1px solid #e6f1ff;
}

:deep(.el-table td) {
  border-bottom: 1px solid #e6f1ff;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: #f8fbff;
}

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

:deep(.el-textarea__inner) {
  border: 1px solid #e6f1ff;
}

:deep(.el-textarea__inner:focus) {
  border-color: #1a6fc4;
}

:deep(.el-date-editor .el-input__inner) {
  border: 1px solid #e6f1ff;
}

:deep(.el-date-editor .el-input__inner:focus) {
  border-color: #1a6fc4;
}

:deep(.el-tag) {
  font-weight: 500;
  border: none;
}

:deep(.el-tag--primary) {
  background: #ecf5ff;
  color: #1a6fc4;
}

:deep(.el-tag--success) {
  background: #f0f9eb;
  color: #67c23a;
}

:deep(.el-tag--warning) {
  background: #fdf6ec;
  color: #e6a23c;
}

:deep(.el-tag--danger) {
  background: #fef0f0;
  color: #f56c6c;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .jobs-container {
    padding: 16px;
  }

  .header-section {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
    padding: 16px;
  }

  .filter-section {
    padding: 16px;
  }

  .filter-row {
    flex-direction: column;
    gap: 12px;
  }

  .search-input {
    max-width: none;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
    margin-bottom: 4px;
  }

  :deep(.el-dialog) {
    width: 90% !important;
    margin: 20px auto;
  }

  :deep(.el-dialog__body) {
    padding: 16px;
  }
}
</style>
