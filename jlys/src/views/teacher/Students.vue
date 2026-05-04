<template>
  <div class="teacher-students">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="12" :md="5">
          <el-input
              v-model="searchForm.keyword"
              placeholder="学生姓名/学号"
              clearable
              @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-select
              v-model="searchForm.collegeId"
              placeholder="选择学院"
              clearable
              style="width: 100%"
          >
            <el-option label="计算机与信息工程学院" :value="1" />
            <el-option label="电子工程学院" :value="2" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-select
              v-model="searchForm.majorId"
              placeholder="选择专业"
              clearable
              style="width: 100%"
          >
            <el-option label="计算机科学与技术" :value="1" />
            <el-option label="软件工程" :value="2" />
            <el-option label="电子信息工程" :value="3" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-select
              v-model="searchForm.employmentStatus"
              placeholder="就业状态"
              clearable
              style="width: 100%"
          >
            <el-option label="已就业" value="employed" />
            <el-option label="求职中" value="seeking" />
            <el-option label="待就业" value="pending" />
            <el-option label="升学" value="further_study" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-button type="primary" @click="handleSearch" :loading="loading" style="width: 100%">
            查询
          </el-button>
        </el-col>
        <el-col :xs="24" :sm="12" :md="3">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="card">
              <el-icon><Grid /></el-icon>
            </el-radio-button>
            <el-radio-button label="table">
              <el-icon><List /></el-icon>
            </el-radio-button>
          </el-radio-group>
        </el-col>
      </el-row>
    </el-card>

    <!-- 卡片视图 -->
    <div v-if="viewMode === 'card'" v-loading="loading" class="card-container">
      <el-row :gutter="20">
        <el-col
          v-for="student in studentList"
          :key="student.studentId"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <el-card class="student-card" shadow="hover">
            <div class="student-header">
              <el-avatar :size="60" :src="student.avatar || '/default-avatar.png'">
                {{ student.realName?.charAt(0) }}
              </el-avatar>
              <div class="student-info">
                <div class="student-name">{{ student.realName }}</div>
                <div class="student-no">{{ student.studentNo }}</div>
              </div>
            </div>

            <el-divider style="margin: 12px 0" />

            <div class="student-details">
              <div class="detail-item">
                <el-icon><School /></el-icon>
                <span>{{ student.major || '未填写' }}</span>
              </div>
              <div class="detail-item">
                <el-icon><Phone /></el-icon>
                <span>{{ student.phone || '未填写' }}</span>
              </div>
              <div class="detail-item">
                <el-icon><Message /></el-icon>
                <span>{{ student.email || '未填写' }}</span>
              </div>
            </div>

            <div class="student-status">
              <el-tag
                :type="getEmploymentStatusType(student.employmentStatus)"
                effect="dark"
                size="small"
              >
                {{ getEmploymentStatusText(student.employmentStatus) }}
              </el-tag>
              <el-tag
                v-if="student.hasWarning"
                type="danger"
                effect="dark"
                size="small"
              >
                预警
              </el-tag>
            </div>

            <div class="student-actions">
              <el-button size="small" type="primary" link @click="viewStudent(student)">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
              <el-button size="small" type="success" link @click="assistStudent(student)">
                <el-icon><ChatDotRound /></el-icon>
                帮扶
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty v-if="!loading && studentList.length === 0" description="暂无学生数据" />
    </div>

    <!-- 表格视图 -->
    <el-card v-else class="table-card">
      <el-table
          :data="studentList"
          stripe
          style="width: 100%"
          :loading="loading"
          v-loading="loading"
      >
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="graduationYear" label="毕业年份" width="100" />
        <el-table-column prop="gpa" label="GPA" width="80" />
        <el-table-column prop="expectedCity" label="期望城市" width="100" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewStudent(row)">
              查看
            </el-button>
            <el-button type="warning" size="small" @click="assistStudent(row)">
              帮扶
            </el-button>
          </template>
        </el-table-column>
      </el-table>

    </el-card>

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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Grid, List, School, Phone, Message, View, ChatDotRound } from '@element-plus/icons-vue'
import { queryStudents } from '@/api/teacher'

const loading = ref(false)
const viewMode = ref('card') // 'card' 或 'table'
const studentList = ref([])
const searchForm = ref({
  keyword: '',
  collegeId: null,
  majorId: null,
  employmentStatus: null
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
    const response = await queryStudents({
      ...searchForm.value,
      current: pagination.value.current,
      size: pagination.value.size
    })
    if (response.success) {
      const pageData = response.data || {}
      studentList.value = pageData.records || []
      pagination.value.total = pageData.total || 0
    }
  } catch (error) {
    ElMessage.error('查询学生失败')
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

const getEmploymentStatusType = (status) => {
  const typeMap = {
    'employed': 'success',
    'seeking': 'warning',
    'pending': 'info',
    'further_study': 'primary'
  }
  return typeMap[status] || 'info'
}

const getEmploymentStatusText = (status) => {
  const textMap = {
    'employed': '已就业',
    'seeking': '求职中',
    'pending': '待就业',
    'further_study': '升学'
  }
  return textMap[status] || '未知'
}

const viewStudent = (row) => {
  ElMessage.info(`查看学生: ${row.realName}`)
}

const assistStudent = (row) => {
  ElMessage.info(`帮扶学生: ${row.realName}`)
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
.teacher-students {
  .search-card {
    margin-bottom: 20px;
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }

  .card-container {
    min-height: 400px;

    .student-card {
      margin-bottom: 20px;
      border-radius: 8px;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-4px);
      }

      .student-header {
        display: flex;
        align-items: center;
        gap: 12px;

        .student-info {
          flex: 1;

          .student-name {
            font-size: 16px;
            font-weight: 600;
            color: #303133;
            margin-bottom: 4px;
          }

          .student-no {
            font-size: 13px;
            color: #909399;
          }
        }
      }

      .student-details {
        margin: 12px 0;

        .detail-item {
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

      .student-status {
        display: flex;
        gap: 8px;
        margin: 12px 0;
      }

      .student-actions {
        display: flex;
        justify-content: space-around;
        padding-top: 12px;
        border-top: 1px solid #f0f0f0;
      }
    }
  }

  .table-card {
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
}
</style>
