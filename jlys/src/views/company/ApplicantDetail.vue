<template>
  <div class="applicant-detail">
    <div class="header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2>应聘者详情</h2>
    </div>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="8" animated />
    </div>

    <div v-else-if="applicantDetail" class="detail-content">
      <!-- 职位信息卡片 -->
      <el-card class="info-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>职位信息</span>
          </div>
        </template>
        <div class="job-info">
          <div class="info-row">
            <span class="label">职位名称：</span>
            <span class="value">{{ applicantDetail.jobTitle }}</span>
          </div>
          <div class="info-row">
            <span class="label">工作类型：</span>
            <span class="value">{{ applicantDetail.jobType }}</span>
          </div>
          <div class="info-row">
            <span class="label">工作地点：</span>
            <span class="value">{{ applicantDetail.jobLocation }}</span>
          </div>
        </div>
      </el-card>

      <!-- 申请人信息卡片 -->
      <el-card class="info-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>申请人信息</span>
          </div>
        </template>
        <div class="applicant-info">
          <div class="info-row">
            <span class="label">姓名：</span>
            <span class="value">{{ applicantDetail.fullName }}</span>
          </div>
          <div class="info-row">
            <span class="label">邮箱：</span>
            <span class="value">{{ applicantDetail.email }}</span>
          </div>
          <div class="info-row">
            <span class="label">电话：</span>
            <span class="value">{{ applicantDetail.phone }}</span>
          </div>
          <div class="info-row">
            <span class="label">性别：</span>
            <span class="value">{{ getGenderText(applicantDetail.gender) }}</span>
          </div>
          <div class="info-row">
            <span class="label">出生日期：</span>
            <span class="value">{{ applicantDetail.birthdate }}</span>
          </div>
          <div class="info-row">
            <span class="label">学历：</span>
            <span class="value">{{ applicantDetail.educationLevel }}</span>
          </div>
          <div class="info-row">
            <span class="label">工作年限：</span>
            <span class="value">{{ applicantDetail.workYears }}年</span>
          </div>
          <div class="info-row">
            <span class="label">期望职位：</span>
            <span class="value">{{ applicantDetail.expectedPosition }}</span>
          </div>
          <div class="info-row">
            <span class="label">期望薪资：</span>
            <span class="value">{{ applicantDetail.expectedSalary }}元</span>
          </div>
        </div>
      </el-card>

      <!-- 应聘状态卡片 -->
      <el-card class="info-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>应聘状态</span>
          </div>
        </template>
        <div class="status-info">
          <div class="info-row">
            <span class="label">当前状态：</span>
            <el-tag :type="getStatusType(applicantDetail.status)">
              {{ getStatusText(applicantDetail.status) }}
            </el-tag>
          </div>
          <div class="info-row">
            <span class="label">申请时间：</span>
            <span class="value">{{ formatDateTime(applicantDetail.applyTime) }}</span>
          </div>
          <div class="info-row" v-if="applicantDetail.aiEvaluationScore">
            <span class="label">AI评分：</span>
            <span class="value">{{ applicantDetail.aiEvaluationScore }}分</span>
          </div>
          <div class="info-row" v-if="applicantDetail.rejectionReason">
            <span class="label">拒绝理由：</span>
            <span class="value">{{ applicantDetail.rejectionReason }}</span>
          </div>
        </div>
      </el-card>

      <!-- 简历信息卡片 -->
      <el-card class="info-card" shadow="hover" v-if="applicantDetail.resumeFilename">
        <template #header>
          <div class="card-header">
            <span>简历信息</span>
          </div>
        </template>
        <div class="resume-info">
          <div class="info-row">
            <span class="label">简历文件：</span>
            <el-link 
              type="primary" 
              :href="applicantDetail.resumeFileUrl" 
              target="_blank"
              :icon="Document"
            >
              {{ applicantDetail.resumeFilename }}
            </el-link>
          </div>
          <div class="info-row">
            <span class="label">上传时间：</span>
            <span class="value">{{ formatDateTime(applicantDetail.resumeUploadDate) }}</span>
          </div>
        </div>
      </el-card>

      <!-- 调试信息 -->
      <el-card v-if="applicantDetail" class="debug-info" style="margin-bottom: 20px; background-color: #f5f5f5;">
        <h4>调试信息：</h4>
        <p><strong>原始状态值：</strong> "{{ applicantDetail.status }}"</p>
        <p><strong>状态类型：</strong> {{ typeof applicantDetail.status }}</p>
        <p><strong>canSendInterview：</strong> {{ canSendInterview }}</p>
        <p><strong>canReject：</strong> {{ canReject }}</p>
        <p><strong>canHire：</strong> {{ canHire }}</p>
        <p><strong>canRejectHire：</strong> {{ canRejectHire }}</p>
      </el-card>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button
          v-if="canSendInterview"
          type="success"
          size="large"
          @click="showInterviewDialog"
          :icon="ChatDotRound"
        >
          发送面试邀请
        </el-button>
        <el-button
          v-if="canReject"
          type="danger"
          size="large"
          @click="showRejectDialog"
          :icon="Close"
        >
          拒绝申请
        </el-button>
        <el-button
          v-if="canHire"
          type="success"
          size="large"
          @click="hireApplicant"
          :icon="Check"
        >
          录用
        </el-button>
        <el-button
          v-if="canRejectHire"
          type="warning"
          size="large"
          @click="showRejectHireDialog"
          :icon="Close"
        >
          拒绝录用
        </el-button>
        <el-button
          v-if="['hired', '已录用'].includes(applicantDetail.status)"
          type="info"
          size="large"
          disabled
        >
          已录用
        </el-button>
        <el-button
          v-if="['rejected', '已拒绝', '拒绝申请'].includes(applicantDetail.status)"
          type="info"
          size="large"
          disabled
        >
          已拒绝
        </el-button>
        <el-button
          v-if="['reject_hire', '拒绝录用'].includes(applicantDetail.status)"
          type="info"
          size="large"
          disabled
        >
          已拒绝录用
        </el-button>
      </div>
    </div>

    <!-- 面试邀请对话框 -->
    <el-dialog
      v-model="interviewDialogVisible"
      title="发送面试邀请"
      width="500px"
    >
      <el-form :model="interviewForm" label-width="100px">
        <el-form-item label="面试时间">
          <el-date-picker
            v-model="interviewForm.interviewTime"
            type="datetime"
            placeholder="选择面试时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="面试地点">
          <el-input
            v-model="interviewForm.location"
            placeholder="请输入面试地点"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="interviewForm.notes"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="interviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sendInterviewInvitation">发送邀请</el-button>
      </template>
    </el-dialog>

    <!-- 拒绝申请对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="拒绝申请"
      width="450px"
    >
      <div class="reject-dialog-content">
        <p class="reject-warning">
          <el-icon class="warning-icon"><WarningFilled /></el-icon>
          确定要拒绝这个申请吗？此操作不可撤销。
        </p>

        <el-form :model="rejectForm" label-width="80px">
          <el-form-item label="拒绝原因">
            <span class="optional-label">(可选)</span>
            <el-input
              v-model="rejectForm.reason"
              type="textarea"
              :rows="4"
              placeholder="可以选择填写拒绝原因，帮助应聘者了解改进方向"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="rejectApplication">确认拒绝</el-button>
      </template>
    </el-dialog>

    <!-- 拒绝录用对话框 -->
    <el-dialog
      v-model="rejectHireDialogVisible"
      title="拒绝录用"
      width="450px"
    >
      <div class="reject-dialog-content">
        <p class="reject-warning">
          <el-icon class="warning-icon"><WarningFilled /></el-icon>
          确定要拒绝录用这个应聘者吗？此操作不可撤销。
        </p>

        <el-form :model="rejectHireForm" label-width="80px">
          <el-form-item label="拒绝原因">
            <span class="optional-label">(可选)</span>
            <el-input
              v-model="rejectHireForm.reason"
              type="textarea"
              :rows="4"
              placeholder="可以选择填写拒绝录用的原因"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="rejectHireDialogVisible = false">取消</el-button>
        <el-button type="warning" @click="rejectHire">确认拒绝录用</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ChatDotRound, Close, Document, WarningFilled, Check } from '@element-plus/icons-vue'
import { getApplicantDetail, updateApplicationStatus, sendInterviewInvitation as sendInterviewAPI, rejectApplication as rejectApplicationAPI, hireApplicant as hireApplicantAPI, rejectHire as rejectHireAPI } from '@/api/applicant'

export default {
  name: 'ApplicantDetail',
  components: {
    ArrowLeft,
    ChatDotRound,
    Close,
    Document,
    WarningFilled,
    Check
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    const loading = ref(false)
    const applicantDetail = ref(null)
    const interviewDialogVisible = ref(false)
    const rejectDialogVisible = ref(false)
    const rejectHireDialogVisible = ref(false)

    const interviewForm = ref({
      interviewTime: '',
      location: '',
      notes: ''
    })

    const rejectForm = ref({
      reason: ''
    })

    const rejectHireForm = ref({
      reason: ''
    })
    
    // 计算属性
    const canSendInterview = computed(() => {
      if (!applicantDetail.value) return false
      const status = applicantDetail.value.status
      // 支持多种状态值格式：英文、中文等
      return ['pending', '已投递', '待处理'].includes(status)
    })

    const canReject = computed(() => {
      if (!applicantDetail.value) return false
      const status = applicantDetail.value.status
      // 可以拒绝的状态：已投递、待面试等
      return ['pending', '已投递', '待处理', 'interview', '待面试', '面试邀请'].includes(status)
    })

    const canHire = computed(() => {
      if (!applicantDetail.value) return false
      const status = applicantDetail.value.status
      // 可以录用的状态：待面试、接受面试、待定等
      return ['interview', '待面试', 'accepted', '接受面试', 'completed', '待定'].includes(status)
    })

    const canRejectHire = computed(() => {
      if (!applicantDetail.value) return false
      const status = applicantDetail.value.status
      // 可以拒绝录用的状态：与录用条件相同
      return ['interview', '待面试', 'accepted', '接受面试', 'completed', '待定'].includes(status)
    })
    
    // 方法
    const goBack = () => {
      router.go(-1)
    }
    
    const getGenderText = (gender) => {
      const genderMap = {
        male: '男',
        female: '女',
        other: '其他'
      }
      return genderMap[gender] || '未知'
    }
    
    const getStatusText = (status) => {
      const statusMap = {
        // 英文状态值
        pending: '已投递',
        rejected: '拒绝申请',
        hired: '已录用',
        reject_hire: '拒绝录用',
        interview: '待面试',
        completed: '待定',
        accepted: '接受面试',
        int_rejected: '拒绝面试',
        // 中文状态值（兼容旧数据）
        '已投递': '已投递',
        '待处理': '已投递',
        '已查看': '已查看',
        '面试邀请': '面试邀请',
        '待面试': '待面试',
        '接受面试': '接受面试',
        '拒绝面试': '拒绝面试',
        '已拒绝': '拒绝申请',
        '拒绝申请': '拒绝申请',
        '已录用': '已录用',
        '拒绝录用': '拒绝录用',
        '待定': '待定'
      }
      return statusMap[status] || status
    }

    const getStatusType = (status) => {
      const typeMap = {
        // 英文状态值
        pending: 'info',
        rejected: 'danger',
        hired: 'success',
        reject_hire: 'warning',
        interview: 'primary',
        completed: 'warning',
        accepted: 'success',
        int_rejected: 'danger',
        // 中文状态值（兼容旧数据）
        '已投递': 'info',
        '待处理': 'info',
        '已查看': 'warning',
        '面试邀请': 'primary',
        '待面试': 'primary',
        '接受面试': 'success',
        '拒绝面试': 'danger',
        '已拒绝': 'danger',
        '拒绝申请': 'danger',
        '已录用': 'success',
        '拒绝录用': 'warning',
        '待定': 'warning'
      }
      return typeMap[status] || 'info'
    }
    
    const formatDateTime = (dateTime) => {
      if (!dateTime) return ''
      return new Date(dateTime).toLocaleString('zh-CN')
    }
    
    const loadApplicantDetail = async () => {
      try {
        loading.value = true
        const applicationId = route.params.applicationId
        const response = await getApplicantDetail(applicationId)
        
        if (response.success) {
          applicantDetail.value = response.data
        } else {
          ElMessage.error(response.message || '获取应聘者详情失败')
        }
      } catch (error) {
        console.error('Load applicant detail error:', error)
        ElMessage.error('获取应聘者详情失败')
      } finally {
        loading.value = false
      }
    }
    
    const showInterviewDialog = () => {
      interviewDialogVisible.value = true
    }
    
    const showRejectDialog = () => {
      // 清空之前的拒绝理由
      rejectForm.value.reason = ''
      rejectDialogVisible.value = true
    }
    
    const sendInterviewInvitation = async () => {
      if (!interviewForm.value.interviewTime || !interviewForm.value.location) {
        ElMessage.warning('请填写面试时间和地点')
        return
      }

      try {
        const response = await sendInterviewAPI({
          applicationId: applicantDetail.value.applicationId,
          interviewTime: interviewForm.value.interviewTime,
          interviewLocation: interviewForm.value.location,
          notes: interviewForm.value.notes
        })

        if (response.success) {
          ElMessage.success('面试邀请发送成功')
          interviewDialogVisible.value = false
          applicantDetail.value.status = 'interview'
          // 重新加载详情以获取最新状态
          loadApplicantDetail()
        } else {
          ElMessage.error(response.message || '发送面试邀请失败')
        }
      } catch (error) {
        console.error('Send interview invitation error:', error)
        ElMessage.error('发送面试邀请失败')
      }
    }
    
    const rejectApplication = async () => {
      try {
        const response = await rejectApplicationAPI({
          applicationId: applicantDetail.value.applicationId,
          reason: rejectForm.value.reason || ''
        })

        if (response.success) {
          ElMessage.success('申请已拒绝')
          rejectDialogVisible.value = false
          applicantDetail.value.status = '淘汰'
          applicantDetail.value.rejectionReason = rejectForm.value.reason || ''
          // 重新加载详情以获取最新状态
          loadApplicantDetail()
        } else {
          ElMessage.error(response.message || '拒绝申请失败')
        }
      } catch (error) {
        console.error('Reject application error:', error)
        ElMessage.error('拒绝申请失败')
      }
    }

    // 显示拒绝录用对话框
    const showRejectHireDialog = () => {
      rejectHireForm.value.reason = ''
      rejectHireDialogVisible.value = true
    }

    // 录用应聘者
    const hireApplicant = async () => {
      try {
        const response = await hireApplicantAPI(applicantDetail.value.applicationId, {})

        if (response.success) {
          ElMessage.success('已录用该应聘者')
          applicantDetail.value.status = 'hired'
          loadApplicantDetail()
        } else {
          ElMessage.error(response.message || '录用操作失败')
        }
      } catch (error) {
        console.error('Hire applicant error:', error)
        ElMessage.error('录用操作失败')
      }
    }

    // 拒绝录用
    const rejectHire = async () => {
      try {
        const response = await rejectHireAPI(applicantDetail.value.applicationId, {
          reason: rejectHireForm.value.reason || ''
        })

        if (response.success) {
          ElMessage.success('已拒绝录用')
          rejectHireDialogVisible.value = false
          applicantDetail.value.status = 'reject_hire'
          applicantDetail.value.rejectionReason = rejectHireForm.value.reason || ''
          loadApplicantDetail()
        } else {
          ElMessage.error(response.message || '拒绝录用失败')
        }
      } catch (error) {
        console.error('Reject hire error:', error)
        ElMessage.error('拒绝录用失败')
      }
    }

    onMounted(() => {
      loadApplicantDetail()
    })
    
    return {
      loading,
      applicantDetail,
      interviewDialogVisible,
      rejectDialogVisible,
      rejectHireDialogVisible,
      interviewForm,
      rejectForm,
      rejectHireForm,
      canSendInterview,
      canReject,
      canHire,
      canRejectHire,
      goBack,
      getGenderText,
      getStatusText,
      getStatusType,
      formatDateTime,
      showInterviewDialog,
      showRejectDialog,
      showRejectHireDialog,
      sendInterviewInvitation,
      rejectApplication,
      hireApplicant,
      rejectHire,
      ArrowLeft,
      ChatDotRound,
      Close,
      Document
    }
  }
}
</script>

<style scoped>
.applicant-detail {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 15px;
}

.header h2 {
  margin: 0;
  color: #333;
}

.loading {
  padding: 20px;
}

.detail-content {
  display: grid;
  gap: 20px;
}

.info-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: 600;
  color: #333;
}

.info-row {
  display: flex;
  margin-bottom: 12px;
  align-items: center;
}

.label {
  font-weight: 500;
  color: #666;
  min-width: 100px;
}

.value {
  color: #333;
}

.action-buttons {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-top: 30px;
  padding: 20px;
  border-top: 1px solid #eee;
}

@media (max-width: 768px) {
  .applicant-detail {
    padding: 10px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .info-row {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .label {
    min-width: auto;
    margin-bottom: 5px;
  }
}

/* 拒绝对话框样式 */
.reject-dialog-content {
  padding: 10px 0;
}

.reject-warning {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 12px;
  background-color: #fef0f0;
  border: 1px solid #fde2e2;
  border-radius: 6px;
  color: #f56565;
  font-size: 14px;
  line-height: 1.5;
}

.warning-icon {
  margin-right: 8px;
  font-size: 16px;
}

.optional-label {
  color: #909399;
  font-size: 12px;
  margin-left: 5px;
}
</style>
