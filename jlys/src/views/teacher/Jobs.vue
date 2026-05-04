<template>
  <div class="teacher-jobs">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="12" :md="5">
          <el-input
              v-model="searchForm.keyword"
              placeholder="搜索职位名称"
              clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-select
              v-model="searchForm.verifyStatus"
              placeholder="审核状态"
              clearable
              style="width: 100%"
          >
            <el-option label="全部" :value="null" />
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-select
              v-model="searchForm.jobType"
              placeholder="职位类型"
              clearable
              style="width: 100%"
          >
            <el-option label="全部" :value="null" />
            <el-option label="全职" value="full_time" />
            <el-option label="兼职" value="part_time" />
            <el-option label="实习" value="internship" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="3">
          <el-button type="primary" @click="handleSearch" :loading="loading" style="width: 100%">
            查询
          </el-button>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-radio-group v-model="viewMode" size="small" style="width: 100%">
            <el-radio-button label="card" style="width: 50%">
              <el-icon><Grid /></el-icon>
            </el-radio-button>
            <el-radio-button label="table" style="width: 50%">
              <el-icon><List /></el-icon>
            </el-radio-button>
          </el-radio-group>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-checkbox v-model="batchMode" @change="handleBatchModeChange">
            批量操作
          </el-checkbox>
          <el-button
            v-if="batchMode && selectedJobs.length > 0"
            type="success"
            size="small"
            @click="batchAudit('approved')"
          >
            批量通过({{ selectedJobs.length }})
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 卡片视图 -->
    <div v-if="viewMode === 'card'" class="card-view" v-loading="loading">
      <el-row :gutter="20">
        <el-col
          v-for="job in jobsList"
            :key="job.jobId"
            :xs="24"
            :sm="12"
            :md="8"
        >
          <el-card class="job-card" shadow="hover">
            <div class="card-header">
              <el-checkbox
                v-if="batchMode && job.verifyStatus === 'pending'"
                v-model="job.selected"
                @change="handleJobSelect"
              />
              <div class="job-title">{{ job.title }}</div>
              <el-tag
                :type="getVerifyTagType(job.verifyStatus)"
                effect="dark"
                size="small"
              >
                {{ getVerifyText(job.verifyStatus) }}
              </el-tag>
            </div>

            <div class="card-body">
              <div class="company-info">
                <el-avatar :size="40" :src="job.companyLogo">
                  {{ job.companyName?.charAt(0) }}
                </el-avatar>
                <div class="company-name">{{ job.companyName }}</div>
              </div>

              <div class="job-info">
                <div class="info-row">
                  <el-icon><Money /></el-icon>
                  <span class="salary">{{ job.minSalary }}-{{ job.maxSalary }}K</span>
                </div>
                <div class="info-row">
                  <el-icon><Location /></el-icon>
                  <span>{{ job.location }}</span>
                </div>
                <div class="info-row">
                  <el-icon><User /></el-icon>
                  <span>{{ getJobTypeText(job.jobType) }}</span>
                </div>
                <div class="info-row">
                  <el-icon><Calendar /></el-icon>
                  <span>{{ formatDate(job.createdAt) }}</span>
                </div>
              </div>

              <div class="job-tags">
                <el-tag
                  v-for="(tag, index) in getJobTags(job)"
                  :key="index"
                  size="small"
                  type="info"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>

            <div class="card-footer">
              <el-button
                v-if="job.verifyStatus === 'pending'"
                type="success"
                size="small"
                @click="quickAudit(job, 'approved')"
              >
                <el-icon><Select /></el-icon>
                通过
              </el-button>
              <el-button
                v-if="job.verifyStatus === 'pending'"
                type="danger"
                size="small"
                @click="quickAudit(job, 'rejected')"
              >
                <el-icon><CloseBold /></el-icon>
                拒绝
              </el-button>
              <el-button
                type="primary"
                size="small"
                @click="viewJob(job)"
                :style="job.verifyStatus !== 'pending' ? 'width: 100%' : ''"
              >
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="!loading && jobsList.length === 0" description="暂无职位数据" />

      <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
          style="margin-top: 20px; text-align: right"
      />
    </div>

    <!-- 表格视图 -->
    <el-card v-else class="table-card">
      <el-table
          :data="jobsList"
          stripe
          style="width: 100%"
          :fit="true"
          :loading="loading"
          v-loading="loading"
          @selection-change="handleSelectionChange"
      >
        <el-table-column
          v-if="batchMode"
          type="selection"
          min-width="70"
          :selectable="row => row.verifyStatus === 'pending'"
        />
        <el-table-column prop="title" label="职位名称" min-width="260" show-overflow-tooltip />
        <el-table-column prop="companyName" label="公司名称" min-width="220" show-overflow-tooltip />
        <el-table-column prop="jobType" label="职位类型" min-width="140">
          <template #default="{ row }">
            {{ getJobTypeText(row.jobType) }}
          </template>
        </el-table-column>
        <el-table-column label="薪资范围" min-width="160">
          <template #default="{ row }">
            {{ row.minSalary }}-{{ row.maxSalary }}K
          </template>
        </el-table-column>
        <el-table-column prop="location" label="工作地点" min-width="180" />
        <el-table-column prop="createdAt" label="发布时间" min-width="220">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="verifyStatus" label="审核状态" min-width="140">
          <template #default="{ row }">
            <el-tag :type="getVerifyTagType(row.verifyStatus)" size="small">
              {{ getVerifyText(row.verifyStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-button
                v-if="row.verifyStatus === 'pending'"
                type="success"
                size="small"
                @click="quickAudit(row, 'approved')"
            >
              通过
            </el-button>
            <el-button
                v-if="row.verifyStatus === 'pending'"
                type="danger"
                size="small"
                @click="quickAudit(row, 'rejected')"
            >
              拒绝
            </el-button>
            <el-button type="info" size="small" @click="viewJob(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
          style="margin-top: 20px; text-align: right"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, Grid, List, Money, Location, User, Calendar,
  View, Select, CloseBold
} from '@element-plus/icons-vue'
import { getJobs, auditJob as auditJobApi } from '@/api/teacher'

const loading = ref(false)
const viewMode = ref('card')
const batchMode = ref(false)
const jobsList = ref([])
const selectedJobs = ref([])

const searchForm = ref({
  verifyStatus: null,
  keyword: '',
  jobType: null
})

const pagination = ref({
  current: 1,
  size: 12,
  total: 0
})

const handleSearch = async (resetPage = true) => {
  if (resetPage) {
    pagination.value.current = 1
  }

  loading.value = true
  try {
    const response = await getJobs({
      verifyStatus: searchForm.value.verifyStatus,
      keyword: searchForm.value.keyword,
      jobType: searchForm.value.jobType,
      current: pagination.value.current,
      size: pagination.value.size
    })
    if (response.success) {
      const pageData = response.data || {}
      jobsList.value = (pageData.records || []).map(j => ({ ...j, selected: false }))
      pagination.value.total = pageData.total || 0
    }
  } catch (error) {
    ElMessage.error('查询职位失败')
  } finally {
    loading.value = false
  }
}

const getVerifyTagType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  }
  return typeMap[status] || 'info'
}

const getVerifyText = (status) => {
  const textMap = {
    'pending': '待审核',
    'approved': '已通过',
    'rejected': '已拒绝'
  }
  return textMap[status] || status
}

const getJobTypeText = (type) => {
  const textMap = {
    'full_time': '全职',
    'part_time': '兼职',
    'internship': '实习'
  }
  return textMap[type] || type
}

const getJobTags = (job) => {
  const tags = []
  if (job.education) tags.push(job.education)
  if (job.experience) tags.push(job.experience)
  if (job.skills) tags.push(...job.skills.split(',').slice(0, 3))
  return tags
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

const formatDateTime = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const handleBatchModeChange = () => {
  if (!batchMode.value) {
    selectedJobs.value = []
    jobsList.value.forEach(j => j.selected = false)
  }
}

const handleJobSelect = () => {
  selectedJobs.value = jobsList.value.filter(j => j.selected)
}

const handleSelectionChange = (selection) => {
  selectedJobs.value = selection
}

const handlePageChange = (page) => {
  pagination.value.current = page
  handleSearch(false)
}

const handleSizeChange = (size) => {
  pagination.value.size = size
  pagination.value.current = 1
  handleSearch(false)
}

const quickAudit = async (job, status) => {
  const action = status === 'approved' ? '通过' : '拒绝'

  try {
    await ElMessageBox.confirm(
      `确定要${action}职位 "${job.title}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: status === 'approved' ? 'success' : 'warning'
      }
    )

    const response = await auditJobApi(job.jobId, {
      verifyStatus: status,
      remark: `快速${action}`
    })

    if (response.success) {
      ElMessage.success(`${action}成功`)
      handleSearch()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const batchAudit = async (status) => {
  const action = status === 'approved' ? '通过' : '拒绝'

  try {
    await ElMessageBox.confirm(
      `确定要批量${action} ${selectedJobs.value.length} 个职位吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const promises = selectedJobs.value.map(job =>
      auditJobApi(job.jobId, {
        verifyStatus: status,
        remark: `批量${action}`
      })
    )

    await Promise.all(promises)
    ElMessage.success(`批量${action}成功`)
    selectedJobs.value = []
    handleSearch()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`批量${action}失败`)
    }
  }
}

const auditJob = async (job, status) => {
  await quickAudit(job, status)
}

const viewJob = (job) => {
  ElMessage.info(`查看职位详情: ${job.title}`)
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
.teacher-jobs {
  width: 100%;

  .search-card {
    margin-bottom: 20px;
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    :deep(.el-card__body) {
      padding: 18px 20px;
    }
  }

  // 卡片视图
  .card-view {
    width: 100%;

    .job-card {
      margin-bottom: 20px;
      border: none;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      }

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        gap: 12px;

        .job-title {
          flex: 1;
          font-size: 18px;
          font-weight: 600;
          color: #303133;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .card-body {
        .company-info {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 16px;
          padding-bottom: 12px;
          border-bottom: 1px solid #f0f0f0;

          .company-name {
            font-size: 15px;
            font-weight: 500;
            color: #606266;
          }
        }

        .job-info {
          margin-bottom: 12px;

          .info-row {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 8px;
            font-size: 14px;
            color: #606266;

            .el-icon { color: #909399; }

            .salary {
              color: #F56C6C;
              font-weight: 600;
              font-size: 16px;
            }
          }
        }

        .job-tags {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
          margin-bottom: 12px;
        }
      }

      .card-footer {
        display: flex;
        gap: 8px;
        justify-content: space-between;
        padding-top: 12px;
        border-top: 1px solid #f0f0f0;
      }
    }
  }

  // 表格视图
  .table-card {
    width: 100%;
    border: none;
    box-shadow: 0 2px 12px rgba(46, 78, 108, 0.1);

    :deep(.el-card__body) { padding: 0 0 16px; }

    :deep(.el-table th.el-table__cell) {
      background: linear-gradient(135deg, #f4f6ff 0%, #edf7ff 100%);
      color: #3c4f80;
      font-weight: 600;
    }

    :deep(.el-table__row:hover > td.el-table__cell) {
      background: #f5f8ff;
    }
  }
}
</style>
