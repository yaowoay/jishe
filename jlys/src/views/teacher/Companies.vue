<template>
  <div class="teacher-companies">
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-select
              v-model="searchForm.verifyStatus"
              placeholder="审核状态"
              clearable
          >
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-input
              v-model="searchForm.keyword"
              placeholder="公司名称"
              clearable
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon>
            <span>查询</span>
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="table-card">
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
          @current-change="handleSearch"
          @size-change="handleSearch"
          style="margin-top: 20px; text-align: right"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getCompanies, auditCompany as auditCompanyApi } from '@/api/teacher'

const loading = ref(false)
const companiesList = ref([])
const searchForm = ref({
  verifyStatus: '',
  keyword: ''
})
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const getVerifyType = (status) => {
  const typeMap = {
    'approved': 'success',
    'pending': 'warning',
    'rejected': 'danger'
  }
  return typeMap[status] || 'info'
}

const handleSearch = async () => {
  loading.value = true
  try {
    const response = await getCompanies(searchForm.value.verifyStatus)
    if (response.success) {
      let data = response.data || []
      
      if (searchForm.value.keyword) {
        data = data.filter(item => 
          item.companyName.includes(searchForm.value.keyword)
        )
      }
      
      companiesList.value = data
      pagination.value.total = data.length
    }
  } catch (error) {
    ElMessage.error('查询企业列表失败')
  } finally {
    loading.value = false
  }
}

const auditCompany = (row, status) => {
  ElMessageBox.confirm(
    `确定要${status === 'approved' ? '通过' : '拒绝'}该企业吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await auditCompanyApi(
        row.companyId,
        status,
        status === 'approved' ? '审核通过' : '审核拒绝'
      )
      if (response.success) {
        ElMessage.success('审核成功')
        handleSearch()
      }
    } catch (error) {
      ElMessage.error('审核失败')
    }
  }).catch(() => {})
}

const viewCompany = (row) => {
  ElMessage.info(`查看企业: ${row.companyName}`)
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
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }

  .table-card {
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
}
</style>
