<template>
  <div class="teacher-warning-students">
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8">
          <el-select
              v-model="searchForm.warningLevel"
              placeholder="预警等级"
              clearable
          >
            <el-option label="低" value="low" />
            <el-option label="中" value="medium" />
            <el-option label="高" value="high" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-select
              v-model="searchForm.handleStatus"
              placeholder="处理状态"
              clearable
          >
            <el-option label="待处理" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已完成" value="completed" />
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
          :data="warningList"
          stripe
          style="width: 100%"
          :loading="loading"
          v-loading="loading"
      >
        <el-table-column prop="studentId" label="学生ID" width="100" />
        <el-table-column prop="warningType" label="预警类型" width="120" />
        <el-table-column prop="warningLevel" label="预警等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.warningLevel)">
              {{ getLevelText(row.warningLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warningScore" label="预警分数" width="100" />
        <el-table-column prop="triggerReason" label="触发原因" show-overflow-tooltip />
        <el-table-column prop="detectionTime" label="检测时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.detectionTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="handleStatus" label="处理状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.handleStatus)">
              {{ getStatusText(row.handleStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
                v-if="row.handleStatus === 'pending'"
                type="primary"
                size="small"
                @click="assignHandler(row)"
            >
              分配
            </el-button>
            <el-button
                v-if="row.handleStatus === 'processing'"
                type="success"
                size="small"
                @click="completeHandle(row)"
            >
              完成
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

    <!-- 分配处理人对话框 -->
    <el-dialog
        v-model="showAssignDialog"
        title="分配处理人"
        width="500px"
        :close-on-click-modal="false"
    >
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="处理人">
          <el-select v-model="assignForm.assignedTo" placeholder="请选择处理人">
            <el-option label="张老师" :value="1" />
            <el-option label="李老师" :value="2" />
            <el-option label="王老师" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
              v-model="assignForm.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAssignDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAssignSubmit" :loading="assignLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 完成处理对话框 -->
    <el-dialog
        v-model="showCompleteDialog"
        title="完成处理"
        width="500px"
        :close-on-click-modal="false"
    >
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="处理结果">
          <el-input
              v-model="completeForm.handleRemark"
              type="textarea"
              :rows="4"
              placeholder="请输入处理结果"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showCompleteDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCompleteSubmit" :loading="completeLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const loading = ref(false)
const assignLoading = ref(false)
const completeLoading = ref(false)
const warningList = ref([])
const showAssignDialog = ref(false)
const showCompleteDialog = ref(false)
const currentRow = ref(null)

const searchForm = ref({
  warningLevel: '',
  handleStatus: ''
})

const assignForm = ref({
  assignedTo: null,
  remark: ''
})

const completeForm = ref({
  handleRemark: ''
})

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const getLevelType = (level) => {
  const typeMap = {
    'low': 'success',
    'medium': 'warning',
    'high': 'danger'
  }
  return typeMap[level] || 'info'
}

const getLevelText = (level) => {
  const textMap = {
    'low': '低',
    'medium': '中',
    'high': '高'
  }
  return textMap[level] || level
}

const getStatusType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'processing': 'primary',
    'completed': 'success'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'pending': '待处理',
    'processing': '处理中',
    'completed': '已完成'
  }
  return textMap[status] || status
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

const handleSearch = async () => {
  loading.value = true
  try {
    // TODO: 调用预警学生查询 API
    // 模拟数据
    warningList.value = [
      {
        id: 1,
        studentId: 101,
        warningType: '就业进度滞后',
        warningLevel: 'high',
        warningScore: 85,
        triggerReason: '连续30天无投递记录',
        detectionTime: new Date(),
        handleStatus: 'pending'
      },
      {
        id: 2,
        studentId: 102,
        warningType: '简历质量低',
        warningLevel: 'medium',
        warningScore: 65,
        triggerReason: '简历完整度不足50%',
        detectionTime: new Date(),
        handleStatus: 'processing'
      }
    ]
    pagination.value.total = warningList.value.length
  } catch (error) {
    ElMessage.error('查询预警学生失败')
  } finally {
    loading.value = false
  }
}

const assignHandler = (row) => {
  currentRow.value = row
  assignForm.value = {
    assignedTo: null,
    remark: ''
  }
  showAssignDialog.value = true
}

const handleAssignSubmit = async () => {
  if (!assignForm.value.assignedTo) {
    ElMessage.warning('请选择处理人')
    return
  }
  
  assignLoading.value = true
  try {
    // TODO: 调用分配处理人 API
    ElMessage.success('分配成功')
    showAssignDialog.value = false
    handleSearch()
  } catch (error) {
    ElMessage.error('分配失败')
  } finally {
    assignLoading.value = false
  }
}

const completeHandle = (row) => {
  currentRow.value = row
  completeForm.value = {
    handleRemark: ''
  }
  showCompleteDialog.value = true
}

const handleCompleteSubmit = async () => {
  if (!completeForm.value.handleRemark) {
    ElMessage.warning('请输入处理结果')
    return
  }
  
  completeLoading.value = true
  try {
    // TODO: 调用完成处理 API
    ElMessage.success('处理完成')
    showCompleteDialog.value = false
    handleSearch()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    completeLoading.value = false
  }
}

const viewDetail = (row) => {
  ElMessage.info(`查看预警详情: ${row.id}`)
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
.teacher-warning-students {
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
