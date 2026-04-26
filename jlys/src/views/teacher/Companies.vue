<template>
  <div class="teacher-companies">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="12" :md="5">
          <el-input
              v-model="searchForm.keyword"
              placeholder="搜索公司名称"
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
              v-model="searchForm.industry"
              placeholder="行业"
              clearable
              style="width: 100%"
          >
            <el-option label="全部" :value="null" />
            <el-option label="IT互联网" value="IT" />
            <el-option label="制造业" value="制造" />
            <el-option label="金融" value="金融" />
            <el-option label="教育" value="教育" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
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
        <el-col :xs="24" :sm="12" :md="3">
          <el-statistic title="待审核" :value="pendingCount" />
        </el-col>
      </el-row>
    </el-card>

    <!-- 卡片视图 -->
    <div v-if="viewMode === 'card'" class="card-view" v-loading="loading">
      <el-row :gutter="20">
        <el-col
          v-for="company in companiesList"
            :key="company.companyId"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
        >
          <el-card class="company-card" shadow="hover">
            <div class="card-header">
              <div class="company-logo">
                <el-avatar :size="60" :src="company.logo">
                  {{ company.companyName?.charAt(0) }}
                </el-avatar>
              </div>
              <div class="verify-badge">
                <el-tag
                  :type="getVerifyTagType(company.verifyStatus)"
                  effect="dark"
                  size="small"
                >
                  {{ getVerifyText(company.verifyStatus) }}
                </el-tag>
              </div>
            </div>

            <div class="card-body">
              <div class="company-name">{{ company.companyName }}</div>

              <div class="company-info">
                <div class="info-row">
                  <el-icon><OfficeBuilding /></el-icon>
                  <span>{{ company.industry || '未知行业' }}</span>
                </div>
                <div class="info-row">
                  <el-icon><User /></el-icon>
                  <span>{{ company.scale || '未知规模' }}</span>
                </div>
                <div class="info-row">
                  <el-icon><Location /></el-icon>
                  <span>{{ company.location || '未知地址' }}</span>
                </div>
              </div>

              <div class="company-credit">
                <span class="credit-label">信用评分：</span>
                <el-rate
                  v-model="company.creditScore"
                  disabled
                  show-score
                  :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                />
              </div>

              <div class="company-stats">
                <div class="stat-item">
                  <div class="stat-value">{{ company.jobCount || 0 }}</div>
                  <div class="stat-label">在招职位</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">{{ company.applicationCount || 0 }}</div>
                  <div class="stat-label">收到简历</div>
                </div>
              </div>
            </div>

            <div class="card-footer">
              <el-button
                v-if="company.verifyStatus === 'pending'"
                type="success"
                size="small"
                @click="quickAudit(company, 'approved')"
              >
                <el-icon><Select /></el-icon>
                通过
              </el-button>
              <el-button
                v-if="company.verifyStatus === 'pending'"
                type="danger"
                size="small"
                @click="quickAudit(company, 'rejected')"
              >
                <el-icon><CloseBold /></el-icon>
                拒绝
              </el-button>
              <el-button
                type="primary"
                size="small"
                @click="viewCompany(company)"
              >
                <el-icon><View /></el-icon>
                详情
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="!loading && companiesList.length === 0" description="暂无企业数据" />

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
          :data="companiesList"
          stripe
          style="width: 100%"
          :loading="loading"
          v-loading="loading"
      >
        <el-table-column prop="companyName" label="公司名称" width="150" />
        <el-table-column prop="industry" label="行业" width="100" />
        <el-table-column prop="scale" label="规模" width="100" />
        <el-table-column prop="creditScore" label="信用分" width="80">
          <template #default="{ row }">
            <el-rate v-model="row.creditScore" disabled allow-half />
          </template>
        </el-table-column>
        <el-table-column prop="verifyStatus" label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getVerifyType(row.verifyStatus)">
              {{ row.verifyStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
                v-if="row.verifyStatus === 'pending'"
                type="success"
                size="small"
                @click="auditCompany(row, 'approved')"
            >
              通过
            </el-button>
            <el-button
                v-if="row.verifyStatus === 'pending'"
                type="danger"
                size="small"
                @click="auditCompany(row, 'rejected')"
            >
              拒绝
            </el-button>
            <el-button type="info" size="small" @click="viewCompany(row)">
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, Grid, List, OfficeBuilding, User, Location,
  View, Select, CloseBold
} from '@element-plus/icons-vue'
import { getCompanies, auditCompany as auditCompanyApi } from '@/api/teacher'

const loading = ref(false)
const viewMode = ref('card')
const companiesList = ref([])
const searchForm = ref({
  verifyStatus: null,
  keyword: '',
  industry: null
})
const pagination = ref({
  current: 1,
  size: 12,
  total: 0
})

const pendingCount = computed(() => {
  return companiesList.value.filter(c => c.verifyStatus === 'pending').length
})

const handleSearch = async (resetPage = true) => {
  if (resetPage) {
    pagination.value.current = 1
  }

  loading.value = true
  try {
    const response = await getCompanies({
      verifyStatus: searchForm.value.verifyStatus,
      keyword: searchForm.value.keyword,
      industry: searchForm.value.industry,
      current: pagination.value.current,
      size: pagination.value.size
    })
    if (response.success) {
      const pageData = response.data || {}
      companiesList.value = pageData.records || []
      pagination.value.total = pageData.total || 0
    }
  } catch (error) {
    ElMessage.error('查询企业失败')
  } finally {
    loading.value = false
  }
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

const getVerifyType = (status) => {
  return getVerifyTagType(status)
}

const getScaleText = (scale) => {
  if (!scale) return '未知规模'
  return scale
}

const quickAudit = async (company, status) => {
  const action = status === 'approved' ? '通过' : '拒绝'

  try {
    await ElMessageBox.confirm(
      `确定要${action}企业 "${company.companyName}" 的认证申请吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: status === 'approved' ? 'success' : 'warning'
      }
    )

    const response = await auditCompanyApi(
      company.companyId, 
      status, 
      `快速${action}`
    )

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

const auditCompany = async (company, status) => {
  await quickAudit(company, status)
}

const viewCompany = (company) => {
  ElMessage.info(`查看企业详情: ${company.companyName}`)
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
.teacher-companies {
  .search-card {
    margin-bottom: 20px;
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }

  // 卡片视图
  .card-view {
    .company-card {
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
        align-items: flex-start;
        margin-bottom: 16px;

        .company-logo {
          .el-avatar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            font-size: 24px;
            font-weight: 600;
          }
        }
      }

      .card-body {
        .company-name {
          font-size: 18px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 12px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .company-info {
          margin-bottom: 12px;

          .info-row {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 8px;
            font-size: 13px;
            color: #606266;

            .el-icon {
              color: #909399;
            }
          }
        }

        .company-credit {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 12px;
          padding: 8px;
          background: #f5f7fa;
          border-radius: 4px;

          .credit-label {
            font-size: 13px;
            color: #606266;
          }
        }

        .company-stats {
          display: flex;
          justify-content: space-around;
          padding: 12px 0;
          border-top: 1px solid #f0f0f0;
          border-bottom: 1px solid #f0f0f0;
          margin-bottom: 12px;

          .stat-item {
            text-align: center;

            .stat-value {
              font-size: 20px;
              font-weight: 600;
              color: #409EFF;
              margin-bottom: 4px;
            }

            .stat-label {
              font-size: 12px;
              color: #909399;
            }
          }
        }
      }

      .card-footer {
        display: flex;
        gap: 8px;
        justify-content: space-between;
      }
    }
  }

  // 表格视图
  .table-card {
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }
}
</style>

