<template>
  <div class="teacher-dashboard">
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6f7ff">
              <el-icon style="color: #1890ff"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ dashboard.totalStudents }}</div>
              <div class="stat-label">学生总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f6ffed">
              <el-icon style="color: #52c41a"><SuccessFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ dashboard.employedStudents }}</div>
              <div class="stat-label">已就业</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #fff7e6">
              <el-icon style="color: #faad14"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ dashboard.pendingStudents }}</div>
              <div class="stat-label">待就业</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #fff1f0">
              <el-icon style="color: #ff4d4f"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ dashboard.warningStudents }}</div>
              <div class="stat-label">预警学生</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :md="12">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span>企业审核待办</span>
              <el-button type="primary" size="small" @click="goToCompanies">查看全部</el-button>
            </div>
          </template>
          <div class="todo-content">
            <div class="todo-item">
              <el-icon><Building /></el-icon>
              <span>{{ dashboard.pendingCompanyVerifications }} 家企业待审核</span>
            </div>
            <div class="todo-item">
              <el-icon><Promotion /></el-icon>
              <span>{{ dashboard.totalCompanies }} 家企业已入驻</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span>校企合作申请</span>
              <el-button type="primary" size="small" @click="goToCooperation">查看全部</el-button>
            </div>
          </template>
          <div class="todo-content">
            <div class="todo-item">
              <el-icon><Share /></el-icon>
              <span>{{ dashboard.cooperationApplications }} 个申请待审核</span>
            </div>
            <div class="todo-item">
              <el-icon><Calendar /></el-icon>
              <span>{{ dashboard.totalActivities }} 场就业活动</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :xs="24">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span>快速操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="goToStudents">
              <el-icon><User /></el-icon>
              <span>查看学生</span>
            </el-button>
            <el-button @click="goToEmployment">
              <el-icon><Briefcase /></el-icon>
              <span>就业审核</span>
            </el-button>
            <el-button @click="goToCompanies">
              <el-icon><Building /></el-icon>
              <span>企业审核</span>
            </el-button>
            <el-button @click="goToActivities">
              <el-icon><Calendar /></el-icon>
              <span>活动管理</span>
            </el-button>
            <el-button @click="goToAssistance">
              <el-icon><Help /></el-icon>
              <span>帮扶管理</span>
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User, SuccessFilled, Clock, Warning, Building, Promotion,
  Share, Calendar, Briefcase, Help
} from '@element-plus/icons-vue'
import { getTeacherDashboard } from '@/api/teacher'

const router = useRouter()
const dashboard = ref({
  totalStudents: 0,
  employedStudents: 0,
  pendingStudents: 0,
  warningStudents: 0,
  totalCompanies: 0,
  pendingCompanyVerifications: 0,
  totalActivities: 0,
  cooperationApplications: 0
})

const loadDashboard = async () => {
  try {
    const response = await getTeacherDashboard()
    if (response.success) {
      dashboard.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载工作台数据失败')
  }
}

const goToStudents = () => router.push('/teacher/students')
const goToEmployment = () => router.push('/teacher/employment')
const goToCompanies = () => router.push('/teacher/companies')
const goToActivities = () => router.push('/teacher/activities')
const goToAssistance = () => router.push('/teacher/assistance')
const goToCooperation = () => router.push('/teacher/cooperation')

onMounted(() => {
  loadDashboard()
})
</script>

<style scoped lang="scss">
.teacher-dashboard {
  .stat-row {
    margin-bottom: 20px;

    .stat-card {
      height: 100%;
      border: none;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
        transform: translateY(-2px);
      }

      .stat-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .stat-icon {
          width: 60px;
          height: 60px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 28px;
        }

        .stat-info {
          flex: 1;

          .stat-value {
            font-size: 24px;
            font-weight: 600;
            color: #333;
          }

          .stat-label {
            font-size: 12px;
            color: #909399;
            margin-top: 4px;
          }
        }
      }
    }
  }

  .content-row {
    margin-bottom: 20px;

    .content-card {
      border: none;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .todo-content {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .todo-item {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 12px;
          background-color: #f5f7fa;
          border-radius: 4px;
          font-size: 14px;
          color: #333;

          .el-icon {
            font-size: 18px;
            color: #409eff;
          }
        }
      }

      .quick-actions {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;

        .el-button {
          flex: 1;
          min-width: 120px;
        }
      }
    }
  }
}
</style>
