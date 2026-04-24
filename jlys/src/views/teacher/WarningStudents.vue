<template>
  <div class="teacher-warning-students">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="12" :md="5">
          <el-input
              v-model="searchForm.keyword"
              placeholder="学生姓名/学号"
              clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-select
              v-model="searchForm.warningLevel"
              placeholder="预警等级"
              clearable
              style="width: 100%"
          >
            <el-option label="紧急" value="urgent" />
            <el-option label="高" value="high" />
            <el-option label="中" value="medium" />
            <el-option label="低" value="low" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-select
              v-model="searchForm.warningType"
              placeholder="预警类型"
              clearable
              style="width: 100%"
          >
            <el-option label="就业预警" value="employment" />
            <el-option label="技能预警" value="skill" />
            <el-option label="面试预警" value="interview" />
            <el-option label="简历预警" value="resume" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-button type="primary" @click="handleSearch" :loading="loading" style="width: 100%">
            查询
          </el-button>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-radio-group v-model="viewMode" size="small" style="width: 100%">
            <el-radio-button label="kanban" style="width: 50%">
              <el-icon><Grid /></el-icon>
            </el-radio-button>
            <el-radio-button label="table" style="width: 50%">
              <el-icon><List /></el-icon>
            </el-radio-button>
          </el-radio-group>
        </el-col>
        <el-col :xs="24" :sm="12" :md="3">
          <el-statistic title="预警总数" :value="totalWarnings" />
        </el-col>
      </el-row>
    </el-card>

    <!-- 看板视图 -->
    <div v-if="viewMode === 'kanban'" class="kanban-container" v-loading="loading">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6" v-for="column in kanbanColumns" :key="column.status">
          <div class="kanban-column">
            <div class="column-header" :style="{ borderColor: column.color }">
              <div class="header-title">
                <el-icon :style="{ color: column.color }">
                  <component :is="column.icon" />
                </el-icon>
                <span>{{ column.title }}</span>
              </div>
              <el-badge :value="getColumnCount(column.status)" :type="column.badgeType" />
            </div>

            <div class="column-body">
              <draggable
                  v-model="column.items"
                  :group="{ name: 'warnings', pull: true, put: true }"
                  item-key="id"
                  @change="handleDragChange($event, column.status)"
                  class="draggable-list"
              >
                <template #item="{ element }">
                  <div class="warning-card" :class="`level-${element.warningLevel}`">
                    <div class="card-header">
                      <el-tag
                        :type="getLevelType(element.warningLevel)"
                        size="small"
                        effect="dark"
                      >
                        {{ getLevelText(element.warningLevel) }}
                      </el-tag>
                      <el-tag size="small" type="info">
                        {{ getTypeText(element.warningType) }}
                      </el-tag>
                    </div>

                    <div class="card-body">
                      <div class="student-info">
                        <el-avatar :size="40" :src="element.avatar">
                          {{ element.studentName?.charAt(0) }}
                        </el-avatar>
                        <div class="info-text">
                          <div class="name">{{ element.studentName }}</div>
                          <div class="no">{{ element.studentNo }}</div>
                        </div>
                      </div>

                      <div class="warning-reason">
                        <el-icon><Warning /></el-icon>
                        <span>{{ element.triggerReason }}</span>
                      </div>

                      <div class="warning-time">
                        <el-icon><Clock /></el-icon>
                        <span>{{ formatTime(element.detectionTime) }}</span>
                      </div>
                    </div>

                    <div class="card-footer">
                      <el-button size="small" type="primary" link @click="handleWarning(element)">
                        <el-icon><Edit /></el-icon>
                        处理
                      </el-button>
                      <el-button size="small" type="info" link @click="viewDetail(element)">
                        <el-icon><View /></el-icon>
                        详情
                      </el-button>
                    </div>
                  </div>
                </template>
              </draggable>

              <el-empty
                v-if="column.items.length === 0"
                :image-size="60"
                description="暂无数据"
              />
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 表格视图 -->
    <el-card v-else class="table-card">
      <el-table
          :data="warningList"
          stripe
          style="width: 100%"
          :loading="loading"
          v-loading="loading"
      >
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="warningType" label="预警类型" width="100">
          <template #default="{ row }">
            {{ getTypeText(row.warningType) }}
          </template>
        </el-table-column>
        <el-table-column prop="warningLevel" label="预警等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.warningLevel)" size="small">
              {{ getLevelText(row.warningLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warningScore" label="预警分数" width="100" />
        <el-table-column prop="triggerReason" label="触发原因" show-overflow-tooltip />
        <el-table-column prop="detectionTime" label="检测时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.detectionTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="handleStatus" label="处理状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.handleStatus)" size="small">
              {{ getStatusText(row.handleStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleWarning(row)">
              处理
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

    <!-- 处理预警对话框 -->
    <el-dialog
        v-model="showHandleDialog"
        title="处理预警"
        width="600px"
        :close-on-click-modal="false"
    >
      <el-form :model="handleForm" label-width="100px">
        <el-form-item label="学生信息">
          <div>{{ currentWarning?.studentName }} ({{ currentWarning?.studentNo }})</div>
        </el-form-item>
        <el-form-item label="预警类型">
          <el-tag>{{ getTypeText(currentWarning?.warningType) }}</el-tag>
        </el-form-item>
        <el-form-item label="预警等级">
          <el-tag :type="getLevelType(currentWarning?.warningLevel)">
            {{ getLevelText(currentWarning?.warningLevel) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="触发原因">
          <div>{{ currentWarning?.triggerReason }}</div>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="handleForm.handleStatus" placeholder="请选择处理状态">
            <el-option label="处理中" value="processing" />
            <el-option label="已解决" value="resolved" />
            <el-option label="已忽略" value="ignored" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input
              v-model="handleForm.handleRemark"
              type="textarea"
              :rows="4"
              placeholder="请输入处理备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showHandleDialog = false">取消</el-button>
        <el-button type="primary" @click="submitHandle" :loading="handleLoading">
          提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search, Grid, List, Warning, Clock, Edit, View,
  Clock as ClockIcon, Document, CircleCheck, CircleClose
} from '@element-plus/icons-vue'
import { getEarlyWarnings, handleEarlyWarning } from '@/api/teacher'
import { VueDraggableNext as draggable } from 'vue-draggable-next'

const loading = ref(false)
const viewMode = ref('kanban')
const warningList = ref([])
const totalWarnings = ref(0)
const searchForm = ref({
  keyword: '',
  warningLevel: null,
  warningType: null
})
const pagination = ref({
  current: 1,
  size: 20,
  total: 0
})

// 看板列配置
const kanbanColumns = reactive([
  {
    status: 'pending',
    title: '待处理',
    color: '#E6A23C',
    badgeType: 'warning',
    icon: ClockIcon,
    items: []
  },
  {
    status: 'processing',
    title: '处理中',
    color: '#409EFF',
    badgeType: 'primary',
    icon: Document,
    items: []
  },
  {
    status: 'resolved',
    title: '已解决',
    color: '#67C23A',
    badgeType: 'success',
    icon: CircleCheck,
    items: []
  },
  {
    status: 'ignored',
    title: '已忽略',
    color: '#909399',
    badgeType: 'info',
    icon: CircleClose,
    items: []
  }
])

// 处理对话框
const showHandleDialog = ref(false)
const handleLoading = ref(false)
const currentWarning = ref(null)
const handleForm = reactive({
  handleStatus: '',
  handleRemark: ''
})

const getColumnCount = (status) => {
  const column = kanbanColumns.find(col => col.status === status)
  return column ? column.items.length : 0
}

const handleSearch = async () => {
  loading.value = true
  try {
    const response = await getEarlyWarnings({
      warningLevel: searchForm.value.warningLevel,
      warningType: searchForm.value.warningType
    })

    if (response.success) {
      warningList.value = response.data || []
      totalWarnings.value = warningList.value.length

      // 分配到看板列
      kanbanColumns.forEach(col => col.items = [])
      warningList.value.forEach(item => {
        const column = kanbanColumns.find(col => col.status === item.handleStatus)
        if (column) {
          column.items.push(item)
        }
      })
    }
  } catch (error) {
    ElMessage.error('查询预警失败')
  } finally {
    loading.value = false
  }
}

const handleDragChange = async (evt, newStatus) => {
  if (evt.added) {
    const item = evt.added.element
    try {
      await handleEarlyWarning(item.id, {
        handleStatus: newStatus,
        handleRemark: `拖拽更新状态为${getStatusText(newStatus)}`
      })
      ElMessage.success('状态更新成功')
      handleSearch()
    } catch (error) {
      ElMessage.error('状态更新失败')
      handleSearch() // 刷新恢复原状态
    }
  }
}

const handleWarning = (warning) => {
  currentWarning.value = warning
  handleForm.handleStatus = warning.handleStatus
  handleForm.handleRemark = ''
  showHandleDialog.value = true
}

const submitHandle = async () => {
  if (!handleForm.handleStatus) {
    ElMessage.warning('请选择处理状态')
    return
  }

  handleLoading.value = true
  try {
    await handleEarlyWarning(currentWarning.value.id, {
      handleStatus: handleForm.handleStatus,
      handleRemark: handleForm.handleRemark
    })
    ElMessage.success('处理成功')
    showHandleDialog.value = false
    handleSearch()
  } catch (error) {
    ElMessage.error('处理失败')
  } finally {
    handleLoading.value = false
  }
}

const viewDetail = (warning) => {
  ElMessage.info(`查看预警详情: ${warning.studentName}`)
}

const getLevelType = (level) => {
  const typeMap = {
    'urgent': 'danger',
    'high': 'danger',
    'medium': 'warning',
    'low': 'success'
  }
  return typeMap[level] || 'info'
}

const getLevelText = (level) => {
  const textMap = {
    'urgent': '紧急',
    'high': '高',
    'medium': '中',
    'low': '低'
  }
  return textMap[level] || level
}

const getTypeText = (type) => {
  const textMap = {
    'employment': '就业预警',
    'skill': '技能预警',
    'interview': '面试预警',
    'resume': '简历预警'
  }
  return textMap[type] || type
}

const getStatusType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'processing': 'primary',
    'resolved': 'success',
    'ignored': 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'pending': '待处理',
    'processing': '处理中',
    'resolved': '已解决',
    'ignored': '已忽略'
  }
  return textMap[status] || status
}

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
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

  .kanban-container {
    min-height: 600px;

    .kanban-column {
      background: #f5f7fa;
      border-radius: 8px;
      padding: 12px;
      height: calc(100vh - 250px);
      display: flex;
      flex-direction: column;

      .column-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px;
        background: white;
        border-radius: 6px;
        border-left: 4px solid;
        margin-bottom: 12px;

        .header-title {
          display: flex;
          align-items: center;
          gap: 8px;
          font-weight: 600;
          font-size: 15px;
        }
      }

      .column-body {
        flex: 1;
        overflow-y: auto;

        .draggable-list {
          min-height: 100px;
        }

        .warning-card {
          background: white;
          border-radius: 8px;
          padding: 12px;
          margin-bottom: 12px;
          cursor: move;
          transition: all 0.3s;
          border-left: 4px solid transparent;

          &:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            transform: translateY(-2px);
          }

          &.level-urgent {
            border-left-color: #f56c6c;
          }

          &.level-high {
            border-left-color: #e6a23c;
          }

          &.level-medium {
            border-left-color: #409eff;
          }

          &.level-low {
            border-left-color: #67c23a;
          }

          .card-header {
            display: flex;
            gap: 8px;
            margin-bottom: 12px;
          }

          .card-body {
            .student-info {
              display: flex;
              align-items: center;
              gap: 12px;
              margin-bottom: 12px;

              .info-text {
                flex: 1;

                .name {
                  font-weight: 600;
                  font-size: 14px;
                  color: #303133;
                }

                .no {
                  font-size: 12px;
                  color: #909399;
                  margin-top: 2px;
                }
              }
            }

            .warning-reason,
            .warning-time {
              display: flex;
              align-items: center;
              gap: 6px;
              font-size: 13px;
              color: #606266;
              margin-bottom: 8px;

              .el-icon {
                color: #909399;
              }
            }
          }

          .card-footer {
            display: flex;
            justify-content: space-around;
            padding-top: 12px;
            border-top: 1px solid #f0f0f0;
            margin-top: 12px;
          }
        }
      }
    }
  }

  .table-card {
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }


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
