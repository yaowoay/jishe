<template>
  <div class="job-list-container">
    <!-- 高级筛选组件 -->
    <JobFilters
        :loading="loading"
        @search="handleAdvancedSearch"
        @filter-change="handleFilterChange"
    />

    <!-- 职位列表主体 -->
    <div class="job-list-main" v-loading="loading">
      <!-- 结果统计 -->
      <div class="result-summary" v-if="!loading">
        <span class="result-count">
          共找到 <strong>{{ pagination.total }}</strong> 个职位
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
          <!-- 排序选项 -->
          <div class="sort-options">
            <el-select v-model="sortBy" @change="handleSortChange" placeholder="排序方式" size="small">
              <el-option label="最新发布" value="latest"></el-option>
              <el-option label="薪资从高到低" value="salary_desc"></el-option>
              <el-option label="薪资从低到高" value="salary_asc"></el-option>
            </el-select>
          </div>
        </div>
      </div>

      <!-- 职位卡片列表 -->
      <el-row :gutter="20" class="job-cards-container" v-if="viewMode === 'card'">
        <el-col :span="8" v-for="job in jobs" :key="job.jobId || job.id">
          <JobCard
              :job="job"
              :is-collected="isJobCollected(job.jobId || job.id)"
              @toggle-collection="handleToggleCollection"
              @view-detail="handleViewDetail"
          />
        </el-col>
      </el-row>

      <!-- 职位列表视图 -->
      <div class="job-list-view" v-if="viewMode === 'list'">
        <el-table
            :data="jobs"
            style="width: 100%"
            stripe
            :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        >
          <el-table-column prop="jobId" label="ID" width="80" />
          <el-table-column label="职位名称" min-width="150" show-overflow-tooltip>
            <template #default="scope">
              {{ scope.row.positionName || '暂无' }}
            </template>
          </el-table-column>
          <el-table-column label="公司名称" min-width="120" show-overflow-tooltip>
            <template #default="scope">
              {{ scope.row.companyName || '暂无' }}
            </template>
          </el-table-column>
          <el-table-column label="城市" width="100">
            <template #default="scope">
              {{ scope.row.city || scope.row.cityName || '暂无' }}
            </template>
          </el-table-column>
          <el-table-column label="薪资" width="140">
            <template #default="scope">
              <SalaryDisplay :job="scope.row" display-mode="simple" />
            </template>
          </el-table-column>
          <el-table-column label="公司类型" width="120" show-overflow-tooltip>
            <template #default="scope">
              {{ scope.row.companyType || scope.row.industryName || '暂无' }}
            </template>
          </el-table-column>
          <el-table-column label="所属行业" width="120">
            <template #default="scope">
              {{ scope.row.companyTags || '暂无' }}
            </template>
          </el-table-column>
          <el-table-column label="公司规模" width="120" show-overflow-tooltip>
            <template #default="scope">
              {{ scope.row.companyScale || scope.row.statusName || '暂无' }}
            </template>
          </el-table-column>
          <el-table-column label="经验要求" width="120" show-overflow-tooltip>
            <template #default="scope">
              {{ scope.row.experienceReq || scope.row.experienceRequirement || '暂无' }}
            </template>
          </el-table-column>
          <el-table-column label="学历要求" width="120" show-overflow-tooltip>
            <template #default="scope">
              {{ scope.row.educationReq || scope.row.educationRequirement || '暂无' }}
            </template>
          </el-table-column>
          <el-table-column label="职位描述" min-width="200" show-overflow-tooltip>
            <template #default="scope">
              {{ scope.row.jobDescription || scope.row.description || '暂无' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="scope">
              <div class="action-buttons-row">
                <el-button
                    type="text"
                    size="small"
                    @click="handleViewDetail(scope.row)"
                >
                  <el-icon><View /></el-icon>
                  详情
                </el-button>
                <el-button
                    type="text"
                    size="small"
                    :class="{ 'collected': isJobCollected(scope.row.jobId || scope.row.id) }"
                    @click="handleToggleCollection(scope.row)"
                >
                  <el-icon>
                    <StarFilled v-if="isJobCollected(scope.row.jobId || scope.row.id)" />
                    <Star v-else />
                  </el-icon>
                  {{ isJobCollected(scope.row.jobId || scope.row.id) ? '已收藏' : '收藏' }}
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && jobs.length === 0" description="暂无相关职位">
        <el-button type="primary" @click="resetSearch">重新搜索</el-button>
      </el-empty>

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
    </div>

    <!-- 职位详情弹窗 -->
    <el-dialog
        v-model="detailDialogVisible"
        title="职位详情"
        width="800px"
        :before-close="handleCloseDetail"
        class="job-detail-dialog"
    >
      <div class="job-detail-content" v-if="selectedJob">
        <!-- 职位基本信息 -->
        <div class="detail-section">
          <h3 class="section-title">基本信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <label>职位名称：</label>
              <span>{{ selectedJob.positionName || '暂无' }}</span>
            </div>
            <div class="info-item">
              <label>薪资信息：</label>
              <SalaryDisplay :job="selectedJob" display-mode="full" />
            </div>
            <div class="info-item">
              <label>工作城市：</label>
              <span>{{ selectedJob.city || selectedJob.cityName || '暂无' }}</span>
            </div>
            <div class="info-item">
              <label>经验要求：</label>
              <span>{{ selectedJob.experienceReq || selectedJob.experienceRequirement || '暂无' }}</span>
            </div>
            <div class="info-item">
              <label>学历要求：</label>
              <span>{{ selectedJob.educationReq || selectedJob.educationRequirement || '暂无' }}</span>
            </div>
          </div>
        </div>

        <!-- 公司信息 -->
        <div class="detail-section">
          <h3 class="section-title">公司信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <label>公司名称：</label>
              <span>{{ selectedJob.companyName || '暂无' }}</span>
            </div>
            <div class="info-item">
              <label>公司类型：</label>
              <span>{{ selectedJob.companyType || selectedJob.industryName || '暂无' }}</span>
            </div>
            <div class="info-item">
              <label>公司规模：</label>
              <span>{{ selectedJob.companyScale || '暂无' }}</span>
            </div>
            <div class="info-item">
              <label>所属行业：</label>
              <span>{{ selectedJob.companyTags|| '暂无' }}</span>
            </div>
          </div>
        </div>

        <!-- 职位描述 -->
        <div class="detail-section">
          <h3 class="section-title">职位描述</h3>
          <div class="description-content">
            {{ selectedJob.jobDescription || selectedJob.description || '暂无职位描述' }}
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCloseDetail">关闭</el-button>
          <el-button
              type="primary"
              :class="{ 'collected': selectedJob && isJobCollected(selectedJob.jobId || selectedJob.id) }"
              @click="handleToggleCollectionInDialog"
          >
            <el-icon>
              <StarFilled v-if="selectedJob && isJobCollected(selectedJob.jobId || selectedJob.id)" />
              <Star v-else />
            </el-icon>
            {{ selectedJob && isJobCollected(selectedJob.jobId || selectedJob.id) ? '已收藏' : '收藏职位' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import JobCard from './JobCard.vue'
import JobFilters from './JobFilters.vue'
import SalaryDisplay from '@/components/SalaryDisplay.vue'
import { ElMessage } from 'element-plus'
import { Grid, List, Star, StarFilled, View } from '@element-plus/icons-vue'

// 从本地存储获取 userId
const currentUserId = ref(null)

const jobs = ref([])
const loading = ref(false)
const collectedJobIds = ref(new Set())

// 当前筛选条件
const currentFilters = ref({
  keyword: '',
  city: ''
})

// 排序方式
const sortBy = ref('latest')

// 视图模式
const viewMode = ref('card')

// 详情弹窗相关状态
const detailDialogVisible = ref(false)
const selectedJob = ref(null)

// 分页相关状态
const pagination = reactive({
  currentPage: 1,
  pageSize: 12,
  total: 0,
  totalPages: 0
})

const isJobCollected = (jobId) => {
  return collectedJobIds.value.has(jobId) || collectedJobIds.value.has(String(jobId))
}

const fetchJobs = async (resetPage = false) => {
  if (resetPage) {
    pagination.currentPage = 1
  }

  loading.value = true
  try {
    // 使用模拟数据
    const mockJobs = [
      {
        jobId: 1,
        positionName: '前端开发工程师',
        companyName: '某科技公司',
        city: '北京',
        minSalary: 15,
        maxSalary: 25,
        experienceReq: '3-5年',
        educationReq: '本科',
        industryName: '互联网'
      },
      {
        jobId: 2,
        positionName: 'Vue开发工程师',
        companyName: '另一家公司',
        city: '上海',
        minSalary: 18,
        maxSalary: 28,
        experienceReq: '2-4年',
        educationReq: '本科',
        industryName: '互联网'
      },
      {
        jobId: 3,
        positionName: 'React开发工程师',
        companyName: '第三家公司',
        city: '深圳',
        minSalary: 20,
        maxSalary: 30,
        experienceReq: '3-5年',
        educationReq: '本科',
        industryName: '互联网'
      }
    ]

    jobs.value = mockJobs
    pagination.total = mockJobs.length
    pagination.totalPages = Math.ceil(mockJobs.length / pagination.pageSize)
  } catch (error) {
    ElMessage.error('获取职位列表失败')
    jobs.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const fetchCollectedJobs = async () => {
  if (!currentUserId.value) {
    console.error('User ID not found in local storage')
    return
  }
  try {
    // 使用模拟数据
    collectedJobIds.value = new Set()
  } catch (error) {
    console.error('获取收藏列表失败', error)
  }
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
    if (isJobCollected(jobId)) {
      collectedJobIds.value.delete(jobId)
      ElMessage.success('已取消收藏')
    } else {
      collectedJobIds.value.add(jobId)
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 分页事件处理
const handleSizeChange = (newSize) => {
  pagination.pageSize = newSize
  pagination.currentPage = 1 // 重置到第一页
  fetchJobs()
}

const handleCurrentChange = (newPage) => {
  pagination.currentPage = newPage
  fetchJobs()
}

// 高级搜索处理
const handleAdvancedSearch = (filters) => {
  currentFilters.value = { ...filters }
  fetchJobs(true) // 重置到第一页
}

// 筛选条件变化处理（实时筛选）
const handleFilterChange = (filters) => {
  currentFilters.value = { ...filters }
  // 可以选择是否实时搜索，这里暂时不实时搜索，只在点击搜索按钮时搜索
}

// 排序变化处理
const handleSortChange = () => {
  // 这里可以添加排序逻辑
  fetchJobs(true)
}

// 重置搜索
const resetSearch = () => {
  currentFilters.value = {}
  fetchJobs(true)
}

// 查看职位详情
const handleViewDetail = (job) => {
  selectedJob.value = job
  detailDialogVisible.value = true
}

// 关闭详情弹窗
const handleCloseDetail = () => {
  detailDialogVisible.value = false
  selectedJob.value = null
}

// 在详情弹窗中切换收藏状态
const handleToggleCollectionInDialog = () => {
  if (selectedJob.value) {
    handleToggleCollection(selectedJob.value)
  }
}

// 薪资范围显示现在由 SalaryDisplay 组件处理

onMounted(() => {
  // 从本地存储获取 userId
  const userIdFromStorage = localStorage.getItem('userId')
  if (userIdFromStorage) {
    currentUserId.value = parseInt(userIdFromStorage)
  } else {
    console.error('User ID not found in local storage')
    // 这里可以根据实际需求处理，比如跳转到登录页等
  }

  // 初始化加载职位列表
  fetchJobs()

  if (currentUserId.value) {
    fetchCollectedJobs()
  }
})

// 暴露方法给父组件使用
defineExpose({
  handleAdvancedSearch,
  resetSearch,
  fetchJobs
})
</script>

<style scoped>
.job-list-container {
  padding: 20px;
}

.job-list-main {
  background: #e8f3ff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.result-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.result-count {
  color: #606266;
  font-size: 14px;
}

.result-count strong {
  color: #409eff;
  font-weight: 600;
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

.view-toggle .el-radio-button {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

.view-toggle .el-icon {
  margin-right: 4px;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 8px;
}

.job-cards-container {
  margin-bottom: 24px;
}

.job-list-view {
  margin-bottom: 24px;
}

.job-list-view .el-table {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

.job-list-view .collected {
  color: #f56c6c;
}

.job-list-view .collected .el-icon {
  color: #f56c6c;
}

.action-buttons-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: nowrap;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding: 20px 0;
}

/* 职位详情弹窗样式 */
.job-detail-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 24px;
  margin: 0;
}

.job-detail-dialog :deep(.el-dialog__title) {
  color: white;
  font-weight: 600;
  font-size: 18px;
}

.job-detail-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white;
  font-size: 20px;
}

.job-detail-dialog :deep(.el-dialog__body) {
  padding: 24px;
  max-height: 70vh;
  overflow-y: auto;
}

.job-detail-content {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

.detail-section {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 16px;
  padding-left: 12px;
  border-left: 4px solid #409eff;
  text-align: left;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.info-item label {
  min-width: 80px;
  font-weight: 500;
  color: #606266;
  margin-right: 12px;
}

.info-item span {
  color: #303133;
  flex: 1;
}

.salary-text {
  color: #f56c6c !important;
  font-weight: 600;
}

.description-content {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  line-height: 1.6;
  color: #303133;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-footer .collected {
  background-color: #f56c6c;
  border-color: #f56c6c;
  color: white;
}

.dialog-footer .collected:hover {
  background-color: #f78989;
  border-color: #f78989;
}
</style>