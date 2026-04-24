<template>
  <div class="teacher-activities">
    <!-- 顶部操作栏 -->
    <el-card class="action-card">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="12" :md="6">
          <el-input
              v-model="searchForm.keyword"
              placeholder="搜索活动名称"
              clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="12" :md="5">
          <el-select
              v-model="searchForm.status"
              placeholder="活动状态"
              clearable
              style="width: 100%"
          >
            <el-option label="全部" :value="null" />
            <el-option label="草稿" value="draft" />
            <el-option label="已发布" value="published" />
            <el-option label="进行中" value="ongoing" />
            <el-option label="已结束" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="5">
          <el-select
              v-model="searchForm.type"
              placeholder="活动类型"
              clearable
              style="width: 100%"
          >
            <el-option label="全部" :value="null" />
            <el-option label="招聘会" value="job_fair" />
            <el-option label="宣讲会" value="lecture" />
            <el-option label="培训" value="training" />
            <el-option label="企业参观" value="visit" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-button type="primary" @click="handleSearch" :loading="loading" style="width: 100%">
            查询
          </el-button>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-button type="success" @click="goToCreate" style="width: 100%">
            <el-icon><Plus /></el-icon>
            创建活动
          </el-button>
        </el-col>
      </el-row>

      <!-- 视图切换 -->
      <el-divider style="margin: 16px 0" />
      <el-row justify="space-between" align="middle">
        <el-col :span="12">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="timeline">
              <el-icon><Clock /></el-icon>
              时间轴
            </el-radio-button>
            <el-radio-button label="calendar">
              <el-icon><Calendar /></el-icon>
              日历
            </el-radio-button>
            <el-radio-button label="card">
              <el-icon><Grid /></el-icon>
              卡片
            </el-radio-button>
            <el-radio-button label="table">
              <el-icon><List /></el-icon>
              表格
            </el-radio-button>
          </el-radio-group>
        </el-col>
        <el-col :span="12" style="text-align: right">
          <el-statistic title="活动总数" :value="activitiesList.length" />
        </el-col>
      </el-row>
    </el-card>

    <!-- 时间轴视图 -->
    <div v-if="viewMode === 'timeline'" class="timeline-view" v-loading="loading">
      <el-timeline>
        <el-timeline-item
            v-for="activity in activitiesList"
            :key="activity.activityId"
            :timestamp="formatDate(activity.startTime)"
            placement="top"
            :type="getTimelineType(activity.status)"
            :hollow="activity.status === 'draft'"
        >
          <el-card class="activity-timeline-card" shadow="hover">
            <div class="card-header">
              <div class="header-left">
                <el-tag :type="getStatusTagType(activity.status)" effect="dark" size="small">
                  {{ getStatusText(activity.status) }}
                </el-tag>
                <el-tag type="info" size="small" style="margin-left: 8px">
                  {{ getTypeText(activity.type) }}
                </el-tag>
                <el-tag :type="getModeTagType(activity.mode)" size="small" style="margin-left: 8px">
                  {{ getModeText(activity.mode) }}
                </el-tag>
              </div>
              <div class="header-right">
                <el-button-group size="small">
                  <el-button @click="viewActivity(activity)">
                    <el-icon><View /></el-icon>
                  </el-button>
                  <el-button @click="editActivity(activity)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button @click="showQRCode(activity)">
                    <el-icon><Qrcode /></el-icon>
                  </el-button>
                </el-button-group>
              </div>
            </div>

            <div class="card-body">
              <div class="activity-title">{{ activity.title }}</div>
              <div class="activity-info">
                <div class="info-item">
                  <el-icon><Clock /></el-icon>
                  <span>{{ formatDateTime(activity.startTime) }} - {{ formatDateTime(activity.endTime) }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ activity.location || '线上活动' }}</span>
                </div>
                <div class="info-item">
                  <el-icon><User /></el-icon>
                  <span>{{ activity.currentParticipants || 0 }} / {{ activity.maxParticipants || '不限' }} 人</span>
                </div>
              </div>

              <div v-if="activity.posterUrl" class="activity-poster">
                <el-image :src="activity.posterUrl" fit="cover" style="width: 100%; height: 150px; border-radius: 4px" />
              </div>

              <el-progress
                  v-if="activity.maxParticipants"
                  :percentage="getParticipationRate(activity)"
                  :status="getProgressStatus(activity)"
                  :stroke-width="8"
                  style="margin-top: 12px"
              />
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>

      <el-empty v-if="!loading && activitiesList.length === 0" description="暂无活动" />
    </div>

    <!-- 日历视图 -->
    <el-card v-else-if="viewMode === 'calendar'" class="calendar-view" v-loading="loading">
      <el-calendar v-model="calendarDate">
        <template #date-cell="{ data }">
          <div class="calendar-day">
            <div class="day-number">{{ data.day.split('-').slice(2).join('') }}</div>
            <div class="day-activities">
              <div
                  v-for="activity in getActivitiesByDate(data.day)"
                  :key="activity.activityId"
                  class="activity-dot"
                  :class="`status-${activity.status}`"
                  @click="viewActivity(activity)"
              >
                <el-tooltip :content="activity.title" placement="top">
                  <div class="dot"></div>
                </el-tooltip>
              </div>
            </div>
          </div>
        </template>
      </el-calendar>
    </el-card>

    <!-- 卡片视图 -->
    <div v-else-if="viewMode === 'card'" class="card-view" v-loading="loading">
      <el-row :gutter="20">
        <el-col
            v-for="activity in activitiesList"
            :key="activity.activityId"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
        >
          <el-card class="activity-card" shadow="hover">
            <div v-if="activity.posterUrl" class="card-poster">
              <el-image :src="activity.posterUrl" fit="cover" style="width: 100%; height: 160px" />
              <div class="poster-overlay">
                <el-tag :type="getStatusTagType(activity.status)" effect="dark">
                  {{ getStatusText(activity.status) }}
                </el-tag>
              </div>
            </div>

            <div class="card-content">
              <div class="activity-title">{{ activity.title }}</div>

              <div class="activity-meta">
                <el-tag size="small" type="info">{{ getTypeText(activity.type) }}</el-tag>
                <el-tag size="small" :type="getModeTagType(activity.mode)">{{ getModeText(activity.mode) }}</el-tag>
              </div>

              <div class="activity-details">
                <div class="detail-row">
                  <el-icon><Clock /></el-icon>
                  <span>{{ formatDate(activity.startTime) }}</span>
                </div>
                <div class="detail-row">
                  <el-icon><Location /></el-icon>
                  <span>{{ activity.location || '线上' }}</span>
                </div>
                <div class="detail-row">
                  <el-icon><User /></el-icon>
                  <span>{{ activity.currentParticipants || 0 }}/{{ activity.maxParticipants || '∞' }}</span>
                </div>
              </div>

              <div class="card-actions">
                <el-button size="small" type="primary" @click="viewActivity(activity)">
                  查看详情
                </el-button>
                <el-button size="small" @click="showQRCode(activity)">
                  <el-icon><Qrcode /></el-icon>
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="!loading && activitiesList.length === 0" description="暂无活动" />
    </div>

    <!-- 表格视图 -->
    <el-card v-else class="table-card">
      <el-table
          :data="activitiesList"
          stripe
          style="width: 100%"
          :loading="loading"
          v-loading="loading"
      >
        <el-table-column prop="title" label="活动名称" width="200" />
        <el-table-column prop="type" label="活动类型" width="100">
          <template #default="{ row }">
            {{ getTypeText(row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="mode" label="活动形式" width="100">
          <template #default="{ row }">
            {{ getModeText(row.mode) }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="地点" width="150" show-overflow-tooltip />
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="currentParticipants" label="参与人数" width="120">
          <template #default="{ row }">
            {{ row.currentParticipants || 0 }}/{{ row.maxParticipants || '不限' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewActivity(row)">
              查看
            </el-button>
            <el-button type="warning" size="small" @click="editActivity(row)">
              编辑
            </el-button>
            <el-button size="small" @click="showQRCode(row)">
              签到码
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

    <!-- 二维码对话框 -->
    <el-dialog
        v-model="showQRDialog"
        title="活动签到二维码"
        width="400px"
        center
    >
      <div class="qrcode-container">
        <div class="qrcode-title">{{ currentActivity?.title }}</div>
        <div class="qrcode-info">
          <div>时间：{{ formatDateTime(currentActivity?.startTime) }}</div>
          <div>地点：{{ currentActivity?.location }}</div>
        </div>
        <div ref="qrcodeRef" class="qrcode-canvas"></div>
        <div class="qrcode-tip">请学生扫码签到</div>
      </div>
      <template #footer>
        <el-button @click="downloadQRCode">下载二维码</el-button>
        <el-button type="primary" @click="showQRDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Search, Plus, Clock, Calendar, Grid, List, Location, User,
  View, Edit, Qrcode
} from '@element-plus/icons-vue'
import { getActivities } from '@/api/teacher'
import QRCode from 'qrcode'

const router = useRouter()
const loading = ref(false)
const viewMode = ref('timeline')
const activitiesList = ref([])
const calendarDate = ref(new Date())
const showQRDialog = ref(false)
const currentActivity = ref(null)
const qrcodeRef = ref(null)

const searchForm = ref({
  keyword: '',
  status: null,
  type: null
})

const pagination = ref({
  current: 1,
  size: 20,
  total: 0
})

const handleSearch = async () => {
  loading.value = true
  try {
    const response = await getActivities({
      ...searchForm.value,
      status: searchForm.value.status
    })
    if (response.success) {
      activitiesList.value = response.data || []
      pagination.value.total = activitiesList.value.length
    }
  } catch (error) {
    ElMessage.error('查询活动失败')
  } finally {
    loading.value = false
  }
}

const getActivitiesByDate = (date) => {
  return activitiesList.value.filter(activity => {
    const activityDate = new Date(activity.startTime).toISOString().split('T')[0]
    return activityDate === date
  })
}

const getParticipationRate = (activity) => {
  if (!activity.maxParticipants) return 0
  return Math.round((activity.currentParticipants / activity.maxParticipants) * 100)
}

const getProgressStatus = (activity) => {
  const rate = getParticipationRate(activity)
  if (rate >= 100) return 'success'
  if (rate >= 80) return 'warning'
  return ''
}

const getTimelineType = (status) => {
  const typeMap = {
    'draft': 'info',
    'published': 'primary',
    'ongoing': 'success',
    'completed': 'success',
    'cancelled': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusTagType = (status) => {
  const typeMap = {
    'draft': 'info',
    'published': 'primary',
    'ongoing': 'success',
    'completed': '',
    'cancelled': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'draft': '草稿',
    'published': '已发布',
    'ongoing': '进行中',
    'completed': '已结束',
    'cancelled': '已取消'
  }
  return textMap[status] || status
}

const getTypeText = (type) => {
  const textMap = {
    'job_fair': '招聘会',
    'lecture': '宣讲会',
    'training': '培训',
    'visit': '企业参观'
  }
  return textMap[type] || type
}

const getModeText = (mode) => {
  const textMap = {
    'online': '线上',
    'offline': '线下',
    'hybrid': '混合'
  }
  return textMap[mode] || mode
}

const getModeTagType = (mode) => {
  const typeMap = {
    'online': 'success',
    'offline': 'primary',
    'hybrid': 'warning'
  }
  return typeMap[mode] || 'info'
}

const formatDate = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

const formatDateTime = (time) => {
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

const goToCreate = () => {
  router.push('/teacher/activities/create')
}

const viewActivity = (activity) => {
  ElMessage.info(`查看活动: ${activity.title}`)
}

const editActivity = (activity) => {
  router.push(`/teacher/activities/edit/${activity.activityId}`)
}

const showQRCode = async (activity) => {
  currentActivity.value = activity
  showQRDialog.value = true

  await nextTick()

  // 生成二维码
  const qrData = JSON.stringify({
    activityId: activity.activityId,
    title: activity.title,
    type: 'activity_signin'
  })

  try {
    await QRCode.toCanvas(qrcodeRef.value, qrData, {
      width: 280,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#FFFFFF'
      }
    })
  } catch (error) {
    ElMessage.error('生成二维码失败')
  }
}

const downloadQRCode = () => {
  const canvas = qrcodeRef.value
  if (canvas) {
    const url = canvas.toDataURL('image/png')
    const link = document.createElement('a')
    link.download = `${currentActivity.value?.title}-签到码.png`
    link.href = url
    link.click()
    ElMessage.success('下载成功')
  }
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
.teacher-activities {
  .action-card {
    margin-bottom: 20px;
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }

  // 时间轴视图
  .timeline-view {
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    .activity-timeline-card {
      margin-bottom: 16px;

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        .header-left {
          display: flex;
          gap: 8px;
        }
      }

      .card-body {
        .activity-title {
          font-size: 18px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 12px;
        }

        .activity-info {
          display: flex;
          flex-direction: column;
          gap: 8px;
          margin-bottom: 12px;

          .info-item {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            color: #606266;

            .el-icon {
              color: #909399;
            }
          }
        }

        .activity-poster {
          margin: 12px 0;
        }
      }
    }
  }

  // 日历视图
  .calendar-view {
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    .calendar-day {
      height: 100%;
      padding: 4px;

      .day-number {
        font-size: 14px;
        color: #606266;
      }

      .day-activities {
        display: flex;
        flex-wrap: wrap;
        gap: 4px;
        margin-top: 4px;

        .activity-dot {
          cursor: pointer;

          .dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
          }

          &.status-draft .dot {
            background: #909399;
          }

          &.status-published .dot {
            background: #409EFF;
          }

          &.status-ongoing .dot {
            background: #67C23A;
          }

          &.status-completed .dot {
            background: #E6A23C;
          }

          &.status-cancelled .dot {
            background: #F56C6C;
          }
        }
      }
    }
  }

  // 卡片视图
  .card-view {
    .activity-card {
      margin-bottom: 20px;
      border: none;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-4px);
      }

      .card-poster {
        position: relative;
        margin: -20px -20px 16px -20px;

        .poster-overlay {
          position: absolute;
          top: 12px;
          right: 12px;
        }
      }

      .card-content {
        .activity-title {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 12px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .activity-meta {
          display: flex;
          gap: 8px;
          margin-bottom: 12px;
        }

        .activity-details {
          .detail-row {
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

        .card-actions {
          display: flex;
          justify-content: space-between;
          padding-top: 12px;
          border-top: 1px solid #f0f0f0;
          margin-top: 12px;
        }
      }
    }
  }

  // 表格视图
  .table-card {
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }

  // 二维码对话框
  .qrcode-container {
    text-align: center;
    padding: 20px;

    .qrcode-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 16px;
    }

    .qrcode-info {
      font-size: 14px;
      color: #606266;
      margin-bottom: 20px;

      div {
        margin-bottom: 8px;
      }
    }

    .qrcode-canvas {
      display: flex;
      justify-content: center;
      margin: 20px 0;
    }

    .qrcode-tip {
      font-size: 13px;
      color: #909399;
      margin-top: 16px;
    }
  }
}
</style>
