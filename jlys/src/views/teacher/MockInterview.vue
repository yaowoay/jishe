<template>
  <div class="mock-interview-container">
    <el-card class="header-card">
      <div class="header-content">
        <div class="title-section">
          <h2>模拟面试安排</h2>
          <p class="subtitle">为学生提供真实的面试场景模拟和专业指导</p>
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
              <el-icon style="color: #1890ff"><Briefcase /></el-icon>
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

    <!-- 预约列表 -->
    <el-card class="table-card">
      <el-table :data="appointmentList" v-loading="loading" stripe>
        <el-table-column prop="appointmentId" label="预约编号" width="100" />
        <el-table-column prop="studentId" label="学生ID" width="100" />
        <el-table-column label="预约时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.appointmentTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column prop="targetPosition" label="目标职位" width="150" />
        <el-table-column label="面试类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.interviewType === 'technical'" type="primary">技术面试</el-tag>
            <el-tag v-else-if="row.interviewType === 'hr'" type="success">HR面试</el-tag>
            <el-tag v-else type="warning">综合面试</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="面试模式" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.interviewMode === 'online'" type="info">线上</el-tag>
            <el-tag v-else>线下</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="地点/链接" width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.interviewMode === 'online' ? row.meetingLink : row.location }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'pending'" type="warning">待确认</el-tag>
            <el-tag v-else-if="row.status === 'confirmed'" type="primary">已确认</el-tag>
            <el-tag v-else-if="row.status === 'completed'" type="success">已完成</el-tag>
            <el-tag v-else type="info">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="80">
          <template #default="{ row }">
            <span v-if="row.performanceScore">{{ row.performanceScore }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button link type="primary" @click="editAppointment(row)" v-if="row.status === 'pending'">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button link type="success" @click="completeAppointment(row)" v-if="row.status === 'confirmed'">
              <el-icon><CircleCheck /></el-icon>
              完成
            </el-button>
            <el-button link type="danger" @click="cancelAppointment(row)" v-if="row.status !== 'cancelled' && row.status !== 'completed'">
              <el-icon><Close /></el-icon>
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingAppointment ? '编辑预约' : '新增预约'"
      width="700px"
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
        <el-form-item label="目标职位" prop="targetPosition">
          <el-input v-model="formData.targetPosition" placeholder="请输入目标职位" />
        </el-form-item>
        <el-form-item label="面试类型" prop="interviewType">
          <el-select v-model="formData.interviewType" placeholder="请选择面试类型" style="width: 100%">
            <el-option label="技术面试" value="technical" />
            <el-option label="HR面试" value="hr" />
            <el-option label="综合面试" value="comprehensive" />
          </el-select>
        </el-form-item>
        <el-form-item label="面试模式" prop="interviewMode">
          <el-radio-group v-model="formData.interviewMode">
            <el-radio label="online">线上</el-radio>
            <el-radio label="offline">线下</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="地点" prop="location" v-if="formData.interviewMode === 'offline'">
          <el-input v-model="formData.location" placeholder="请输入面试地点" />
        </el-form-item>
        <el-form-item label="会议链接" prop="meetingLink" v-if="formData.interviewMode === 'online'">
          <el-input v-model="formData.meetingLink" placeholder="请输入线上会议链接" />
        </el-form-item>
        <el-form-item label="学生简历URL">
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
    <el-dialog v-model="showCompleteDialog" title="完成模拟面试" width="700px">
      <el-form :model="completeForm" label-width="120px">
        <el-form-item label="表现评分" required>
          <el-slider v-model="completeForm.score" :min="0" :max="100" show-input />
        </el-form-item>
        <el-form-item label="面试反馈" required>
          <el-input
            v-model="completeForm.feedback"
            type="textarea"
            :rows="4"
            placeholder="请输入面试反馈内容"
          />
        </el-form-item>
        <el-form-item label="优势">
          <el-input
            v-model="completeForm.strengths"
            type="textarea"
            :rows="3"
            placeholder="请输入学生的优势表现"
          />
        </el-form-item>
        <el-form-item label="不足">
          <el-input
            v-model="completeForm.weaknesses"
            type="textarea"
            :rows="3"
            placeholder="请输入需要改进的地方"
          />
        </el-form-item>
        <el-form-item label="改进建议">
          <el-input
            v-model="completeForm.suggestions"
            type="textarea"
            :rows="3"
            placeholder="请输入改进建议"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCompleteDialog = false">取消</el-button>
        <el-button type="primary" @click="handleComplete" :loading="submitLoading">提交</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="预约详情" width="700px">
      <el-descriptions :column="2" border v-if="currentAppointment">
        <el-descriptions-item label="预约编号">{{ currentAppointment.appointmentId }}</el-descriptions-item>
        <el-descriptions-item label="学生ID">{{ currentAppointment.studentId }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ formatDateTime(currentAppointment.appointmentTime) }}</el-descriptions-item>
        <el-descriptions-item label="时长">{{ currentAppointment.duration }}分钟</el-descriptions-item>
        <el-descriptions-item label="目标职位">{{ currentAppointment.targetPosition }}</el-descriptions-item>
        <el-descriptions-item label="面试类型">
          <el-tag v-if="currentAppointment.interviewType === 'technical'" type="primary">技术面试</el-tag>
          <el-tag v-else-if="currentAppointment.interviewType === 'hr'" type="success">HR面试</el-tag>
          <el-tag v-else type="warning">综合面试</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="面试模式">
          <el-tag v-if="currentAppointment.interviewMode === 'online'" type="info">线上</el-tag>
          <el-tag v-else>线下</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="地点/链接">
          {{ currentAppointment.interviewMode === 'online' ? currentAppointment.meetingLink : currentAppointment.location }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentAppointment.status === 'pending'" type="warning">待确认</el-tag>
          <el-tag v-else-if="currentAppointment.status === 'confirmed'" type="primary">已确认</el-tag>
          <el-tag v-else-if="currentAppointment.status === 'completed'" type="success">已完成</el-tag>
          <el-tag v-else type="info">已取消</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="评分" v-if="currentAppointment.performanceScore">
          {{ currentAppointment.performanceScore }}
        </el-descriptions-item>
        <el-descriptions-item label="学生简历" :span="2" v-if="currentAppointment.studentResumeUrl">
          <el-link :href="currentAppointment.studentResumeUrl" target="_blank" type="primary">查看简历</el-link>
        </el-descriptions-item>
        <el-descriptions-item label="学生备注" :span="2" v-if="currentAppointment.studentNotes">
          {{ currentAppointment.studentNotes }}
        </el-descriptions-item>
        <el-descriptions-item label="面试反馈" :span="2" v-if="currentAppointment.interviewFeedback">
          {{ currentAppointment.interviewFeedback }}
        </el-descriptions-item>
        <el-descriptions-item label="优势" :span="2" v-if="currentAppointment.strengths">
          {{ currentAppointment.strengths }}
        </el-descriptions-item>
        <el-descriptions-item label="不足" :span="2" v-if="currentAppointment.weaknesses">
          {{ currentAppointment.weaknesses }}
        </el-descriptions-item>
        <el-descriptions-item label="改进建议" :span="2" v-if="currentAppointment.improvementSuggestions">
          {{ currentAppointment.improvementSuggestions }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Search, Clock, Briefcase, Calendar, CircleCheck, View, Edit, Close
} from '@element-plus/icons-vue'
import {
  getMockInterviewAppointments,
  createMockInterviewAppointment,
  updateMockInterviewAppointment,
  cancelMockInterviewAppointment,
  completeMockInterview,
  getMockInterviewStatistics
} from '@/api/teacher'

const loading = ref(false)
const submitLoading = ref(false)
const showCreateDialog = ref(false)
const showCompleteDialog = ref(false)
const showDetailDialog = ref(false)
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
  interviewType: '',
  targetPosition: '',
  interviewMode: 'offline',
  meetingLink: '',
  studentResumeUrl: '',
  studentNotes: ''
})

const completeForm = ref({
  score: 80,
  feedback: '',
  strengths: '',
  weaknesses: '',
  suggestions: ''
})

const rules = {
  studentId: [{ required: true, message: '请输入学生ID', trigger: 'blur' }],
  appointmentTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
  interviewType: [{ required: true, message: '请选择面试类型', trigger: 'change' }],
  targetPosition: [{ required: true, message: '请输入目标职位', trigger: 'blur' }]
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

const loadAppointments = async () => {
  loading.value = true
  try {
    const response = await getMockInterviewAppointments(searchForm.value.status)
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
    const response = await getMockInterviewStatistics()
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
          ? updateMockInterviewAppointment(editingAppointment.value.appointmentId, formData.value)
          : createMockInterviewAppointment(formData.value)

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

const editAppointment = (row) => {
  editingAppointment.value = row
  Object.assign(formData.value, row)
  showCreateDialog.value = true
}

const completeAppointment = (row) => {
  currentAppointment.value = row
  completeForm.value = {
    score: 80,
    feedback: '',
    strengths: '',
    weaknesses: '',
    suggestions: ''
  }
  showCompleteDialog.value = true
}

const handleComplete = async () => {
  if (!completeForm.value.feedback) {
    ElMessage.warning('请输入面试反馈')
    return
  }

  submitLoading.value = true
  try {
    const response = await completeMockInterview(currentAppointment.value.appointmentId, {
      feedback: completeForm.value.feedback,
      score: completeForm.value.score,
      strengths: completeForm.value.strengths,
      weaknesses: completeForm.value.weaknesses,
      suggestions: completeForm.value.suggestions
    })

    if (response.success) {
      ElMessage.success('完成成功')
      showCompleteDialog.value = false
      loadAppointments()
      loadStatistics()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const cancelAppointment = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消此预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await cancelMockInterviewAppointment(row.appointmentId)
    if (response.success) {
      ElMessage.success('取消成功')
      loadAppointments()
      loadStatistics()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

const viewDetail = (row) => {
  currentAppointment.value = row
  showDetailDialog.value = true
}

onMounted(() => {
  loadAppointments()
  loadStatistics()
})
</script>

<style scoped>
.mock-interview-container {
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
}

.subtitle {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.filter-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}
</style>
