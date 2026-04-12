<template>
  <div class="teacher-cooperation">
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-select
              v-model="searchForm.status"
              placeholder="申请状态"
              clearable
          >
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-select
              v-model="searchForm.cooperationType"
              placeholder="合作类型"
              clearable
          >
            <el-option label="实习基地" value="实习基地" />
            <el-option label="就业基地" value="就业基地" />
            <el-option label="产学研合作" value="产学研合作" />
            <el-option label="订单培养" value="订单培养" />
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
          :data="cooperationList"
          stripe
          style="width: 100%"
          :loading="loading"
          v-loading="loading"
      >
        <el-table-column prop="enterpriseId" label="企业ID" width="100" />
        <el-table-column prop="cooperationType" label="合作类型" width="120" />
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="expectedStudents" label="预期学生数" width="120" />
        <el-table-column prop="expectedStartDate" label="预期开始日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
                v-if="row.status === 'pending'"
                type="success"
                size="small"
                @click="auditApplication(row, 'approved')"
            >
              通过
            </el-button>
            <el-button
                v-if="row.status === 'pending'"
                type="danger"
                size="small"
                @click="auditApplication(row, 'rejected')"
            >
              拒绝
            </el-button>
            <el-button type="info" size="small" @click="viewDetail(row)">
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

    <!-- 审核对话框 -->
    <el-dialog
        v-model="showAuditDialog"
        :title="auditForm.status === 'approved' ? '通过审核' : '拒绝审核'"
        width="500px"
        :close-on-click-modal="false"
    >
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="审核意见">
          <el-input
              v-model="auditForm.comment"
              type="textarea"
              :rows="4"
              :placeholder="auditForm.status === 'approved' ? '请输入通过意见（可选）' : '请输入拒绝原因'"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAuditDialog = false">取消</el-button>
        <el-button
            :type="auditForm.status === 'approved' ? 'success' : 'danger'"
            @click="handleAuditSubmit"
            :loading="auditLoading"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
        v-model="showDetailDialog"
        title="合作申请详情"
        width="700px"
    >
      <el-descriptions :column="2" border v-if="currentDetail">
        <el-descriptions-item label="企业ID">{{ currentDetail.enterpriseId }}</el-descriptions-item>
        <el-descriptions-item label="合作类型">{{ currentDetail.cooperationType }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentDetail.title }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ currentDetail.description }}</el-descriptions-item>
        <el-descriptions-item label="预期学生数">{{ currentDetail.expectedStudents }}</el-descriptions-item>
        <el-descriptions-item label="预期开始日期">{{ currentDetail.expectedStartDate }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentDetail.status)">
            {{ getStatusText(currentDetail.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核意见" :span="2" v-if="currentDetail.reviewComment">
          {{ currentDetail.reviewComment }}
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getCooperationApplications, auditCooperationApplication } from '@/api/teacher'

const loading = ref(false)
const auditLoading = ref(false)
const cooperationList = ref([])
const showAuditDialog = ref(false)
const showDetailDialog = ref(false)
const currentRow = ref(null)
const currentDetail = ref(null)

const searchForm = ref({
  status: '',
  cooperationType: ''
})

const auditForm = ref({
  applicationId: null,
  status: '',
  comment: ''
})

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const getStatusType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'pending': '待审核',
    'approved': '已通过',
    'rejected': '已拒绝'
  }
  return textMap[status] || status
}

const handleSearch = async () => {
  loading.value = true
  try {
    const response = await getCooperationApplications()
    if (response.success) {
      let data = response.data || []
      
      if (searchForm.value.status) {
        data = data.filter(item => item.status === searchForm.value.status)
      }
      if (searchForm.value.cooperationType) {
        data = data.filter(item => item.cooperationType === searchForm.value.cooperationType)
      }
      
      cooperationList.value = data
      pagination.value.total = data.length
    }
  } catch (error) {
    ElMessage.error('查询校企合作申请失败')
  } finally {
    loading.value = false
  }
}

const auditApplication = (row, status) => {
  currentRow.value = row
  auditForm.value = {
    applicationId: row.applicationId,
    status: status,
    comment: ''
  }
  showAuditDialog.value = true
}

const handleAuditSubmit = async () => {
  if (auditForm.value.status === 'rejected' && !auditForm.value.comment) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  
  auditLoading.value = true
  try {
    const response = await auditCooperationApplication(
      auditForm.value.applicationId,
      auditForm.value.status,
      auditForm.value.comment
    )
    if (response.success) {
      ElMessage.success('审核成功')
      showAuditDialog.value = false
      handleSearch()
    }
  } catch (error) {
    ElMessage.error('审核失败')
  } finally {
    auditLoading.value = false
  }
}

const viewDetail = (row) => {
  currentDetail.value = row
  showDetailDialog.value = true
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
.teacher-cooperation {
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
