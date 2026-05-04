<template>
  <div class="resume-guidance-container">
    <el-card class="header-card">
      <div class="header-content">
        <div class="title-section">
          <h2>简历指导预约</h2>
          <p class="subtitle">为学生提供专业的简历优化指导服务</p>
        </div>
        <el-button type="primary" @click="showCreateDialog = true">
          <el-icon><Plus /></el-icon>
          新增预约
        </el-button>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6f7ff">
              <el-icon style="color: #1890ff"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.total || 0 }}</div>
              <div class="stat-label">总预约数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #fff7e6">
              <el-icon style="color: #faad14"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.pending || 0 }}</div>
              <div class="stat-label">待确认</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f0f5ff">
              <el-icon style="color: #597ef7"><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.confirmed || 0 }}</div>
              <div class="stat-label">已确认</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f6ffed">
              <el-icon style="color: #52c41a"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.completed || 0 }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 150px">
            <el-option label="待确认" value="pending" />
            <el-option label="已确认" value="confirmed" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadAppointments">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingAppointment ? '编辑预约' : '新增预约'"
      width="600px"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="学生ID" prop="studentId">
          <el-input-number v-model="formData.studentId" :min="1" placeholder="请输入学生ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预约时间" prop="appointmentTime">
          <el-date-picker
            v-model="formData.appointmentTime"
            type="datetime"
            placeholder="选择预约时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="时长(分钟)" prop="duration">
          <el-input-number v-model="formData.duration" :min="30" :max="180" :step="30" style="width: 100%" />
        </el-form-item>
        <el-form-item label="地点" prop="location">
          <el-input v-model="formData.location" placeholder="请输入指导地点" />
        </el-form-item>
        <el-form-item label="指导类型" prop="guidanceType">
          <el-select v-model="formData.guidanceType" placeholder="请选择指导类型" style="width: 100%">
            <el-option label="格式优化" value="format" />
            <el-option label="内容优化" value="content" />
            <el-option label="综合指导" value="comprehensive" />
          </el-select>
        </el-form-item>
        <el-form-item label="学生简历URL" prop="studentResumeUrl">
          <el-input v-model="formData.studentResumeUrl" placeholder="请输入学生简历URL" />
        </el-form-item>
        <el-form-item label="学生备注">
          <el-input v-model="formData.studentNotes" type="textarea" :rows="3" placeholder="学生备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 完成对话框 -->
    <el-dialog v-model="showCompleteDialog" title="完成简历指导" width="600px">
      <el-form :model="completeForm" label-width="120px">
        <el-form-item label="指导反馈" required>
          <el-input
            v-model="completeForm.feedback"
            type="textarea"
            :rows="6"
            placeholder="请输入指导反馈内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCompleteDialog = false">取消</el-button>
        <el-button type="primary" @click="handleComplete" :loading="submitLoading">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Search, Clock, Document, Calendar, CircleCheck, View, Edit, Close
} from '@element-plus/icons-vue'
import {
  getResumeGuidanceAppointments,
  createResumeGuidanceAppointment,
  updateResumeGuidanceAppointment,
  cancelResumeGuidanceAppointment,
  completeResumeGuidance,
  getResumeGuidanceStatistics
} from '@/api/teacher'

const loading = ref(false)
const submitLoading = ref(false)
const showCreateDialog = ref(false)
const showCompleteDialog = ref(false)
const appointmentList = ref([])
const editingAppointment = ref(null)
const currentAppointment = ref(null)
const formRef = ref(null)

const statistics = ref({
  total: 0,
  pending: 0,
  confirmed: 0,
  completed: 0
})

const searchForm = ref({
  status: null
})

const formData = ref({
  studentId: null,
  appointmentTime: null,
  duration: 60,
  location: '',
  guidanceType: '',
  studentResumeUrl: '',
  studentNotes: ''
})

const completeForm = ref({
  feedback: ''
})

const rules = {
  studentId: [{ required: true, message: '请输入学生ID', trigger: 'blur' }],
  appointmentTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
  guidanceType: [{ required: true, message: '请选择指导类型', trigger: 'change' }]
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

const loadAppointments = async () => {
  loading.value = true
  try {
    const response = await getResumeGuidanceAppointments(searchForm.value.status)
    if (response.success) {
      appointmentList.value = response.data || []
    }
  } catch (error) {
    ElMessage.error('加载预约列表失败')
  } finally {
    loading.value = false
  }
}

const loadStatistics = async () => {
  try {
    const response = await getResumeGuidanceStatistics()
    if (response.success) {
      statistics.value = response.data
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const apiCall = editingAppointment.value
          ? updateResumeGuidanceAppointment(editingAppointment.value.appointmentId, formData.value)
          : createResumeGuidanceAppointment(formData.value)

        const response = await apiCall
        if (response.success) {
          ElMessage.success(editingAppointment.value ? '更新成功' : '创建成功')
          showCreateDialog.value = false
          loadAppointments()
          loadStatistics()
        }
      } catch (error) {
        ElMessage.error('操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  loadAppointments()
  loadStatistics()
})
</script>

<style scoped>
.resume-guidance-container {
  padding: 20px;
}
</style>

