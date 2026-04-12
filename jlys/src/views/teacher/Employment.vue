<template>
  <div class="teacher-employment">
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
          <el-select
              v-model="searchForm.employmentStatus"
              placeholder="就业状态"
              clearable
          >
            <el-option label="已就业" value="已就业" />
            <el-option label="待就业" value="待就业" />
            <el-option label="求职中" value="求职中" />
            <el-option label="升学" value="升学" />
          </el-select>
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
          :data="employmentList"
          stripe
          style="width: 100%"
          :loading="loading"
          v-loading="loading"
      >
        <el-table-column prop="studentId" label="学生ID" width="100" />
        <el-table-column prop="employmentStatus" label="就业状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.employmentStatus)">
              {{ row.employmentStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="companyName" label="公司" width="150" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="salaryRange" label="薪资" width="100" />
        <el-table-column prop="employmentCity" label="城市" width="100" />
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
                @click="auditEmployment(row, 'approved')"
            >
              通过
            </el-button>
            <el-button
                v-if="row.verifyStatus === 'pending'"
                type="danger"
                size="small"
                @click="auditEmployment(row, 'rejected')"
            >
              拒绝
            </el-button>
            <el-button v-else type="info" size="small" disabled>
              已审核
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
import { getEmploymentList, auditEmployment as auditEmploymentApi } from '@/api/teacher'

const loading = ref(false)
const employmentList = ref([])
const searchForm = ref({
  verifyStatus: '',
  employmentStatus: ''
})
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const getStatusType = (status) => {
  const typeMap = {
    '已就业': 'success',
    '待就业': 'warning',
    '求职中': 'info',
    '升学': 'primary'
  }
  return typeMap[status] || 'info'
}

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
    const response = await getEmploymentList()
    if (response.success) {
      let data = response.data || []
      
      // 前端过滤
      if (searchForm.value.verifyStatus) {
        data = data.filter(item => item.verifyStatus === searchForm.value.verifyStatus)
      }
      if (searchForm.value.employmentStatus) {
        data = data.filter(item => item.employmentStatus === searchForm.value.employmentStatus)
      }
      
      employmentList.value = data
      pagination.value.total = data.length
    }
  } catch (error) {
    ElMessage.error('查询就业台账失败')
  } finally {
    loading.value = false
  }
}

const auditEmployment = (row, status) => {
  ElMessageBox.confirm(
    `确定要${status === 'approved' ? '通过' : '拒绝'}该就业记录吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await auditEmploymentApi({
        ledgerId: row.id,
        verifyStatus: status,
        employmentStatus: row.employmentStatus,
        companyName: row.companyName,
        position: row.position,
        salaryRange: row.salaryRange,
        employmentCity: row.employmentCity
      })
      if (response.success) {
        ElMessage.success('审核成功')
        handleSearch()
      }
    } catch (error) {
      ElMessage.error('审核失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
.teacher-employment {
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
