<template>
  <div class="teacher-assistance">
    <!-- 顶部操作栏 -->
    <el-card class="action-card">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="12" :md="6">
          <el-input
              v-model="searchForm.keyword"
              placeholder="搜索学生姓名/学号"
              clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="12" :md="5">
          <el-select
              v-model="searchForm.difficultyType"
              placeholder="困难类型"
              clearable
              style="width: 100%"
          >
            <el-option label="全部" :value="null" />
            <el-option label="就业困难" value="就业困难" />
            <el-option label="学业困难" value="学业困难" />
            <el-option label="心理困难" value="心理困难" />
            <el-option label="经济困难" value="经济困难" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-button type="primary" @click="loadData" :loading="loading" style="width: 100%">
            查询
          </el-button>
        </el-col>
        <el-col :xs="24" :sm="12" :md="5">
          <el-radio-group v-model="viewMode" size="small" style="width: 100%">
            <el-radio-button label="timeline" style="width: 50%">
              <el-icon><Clock /></el-icon>
            </el-radio-button>
            <el-radio-button label="student" style="width: 50%">
              <el-icon><User /></el-icon>
            </el-radio-button>
          </el-radio-group>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-button type="success" @click="showCreateDialog = true" style="width: 100%">
            <el-icon><Plus /></el-icon>
            新增记录
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 时间轴视图 -->
    <div v-if="viewMode === 'timeline'" class="timeline-view" v-loading="loading">
      <el-timeline>
        <el-timeline-item
            v-for="record in assistanceList"
            :key="record.recordId"
            :timestamp="formatDate(record.createdAt)"
            placement="top"
            :type="getTimelineType(record.difficultyLevel)"
        >
          <el-card class="record-card" shadow="hover">
            <div class="card-header">
              <div class="header-left">
                <el-avatar :size="40" :src="record.studentAvatar">
                  {{ record.studentName?.charAt(0) }}
                </el-avatar>
                <div class="student-info">
                  <div class="student-name">{{ record.studentName }}</div>
                  <div class="student-no">{{ record.studentNo }}</div>
                </div>
              </div>
              <div class="header-right">
                <el-tag :type="getLevelTagType(record.difficultyLevel)" effect="dark" size="small">
                  {{ getLevelText(record.difficultyLevel) }}
                </el-tag>
                <el-tag type="info" size="small" style="margin-left: 8px">
                  {{ record.difficultyType }}
                </el-tag>
              </div>
            </div>

            <div class="card-body">
              <div class="section">
                <div class="section-title">
                  <el-icon><Document /></el-icon>
                  困难描述
                </div>
                <div class="section-content">{{ record.description }}</div>
              </div>

              <div class="section">
                <div class="section-title">
                  <el-icon><Notebook /></el-icon>
                  帮扶计划
                </div>
                <div class="section-content">{{ record.assistancePlan }}</div>
              </div>

              <div v-if="record.result" class="section">
                <div class="section-title">
                  <el-icon><CircleCheck /></el-icon>
                  帮扶结果
                </div>
                <div class="section-content result">{{ record.result }}</div>
              </div>

              <div class="record-footer">
                <div class="footer-info">
                  <el-icon><Calendar /></el-icon>
                  <span>跟进日期：{{ formatDate(record.followUpDate) }}</span>
                </div>
                <div class="footer-actions">
                  <el-button size="small" type="primary" @click="viewDetail(record)">
                    <el-icon><View /></el-icon>
                    详情
                  </el-button>
                  <el-button size="small" type="success" @click="followUp(record)">
                    <el-icon><Edit /></el-icon>
                    跟进
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>

      <el-empty v-if="!loading && assistanceList.length === 0" description="暂无帮扶记录" />
    </div>

    <!-- 学生维度视图 -->
    <div v-else class="student-view" v-loading="loading">
      <el-collapse v-model="activeStudents" accordion>
        <el-collapse-item
            v-for="(records, studentId) in groupedByStudent"
            :key="studentId"
            :name="studentId"
        >
          <template #title>
            <div class="collapse-title">
              <el-avatar :size="40" :src="records[0].studentAvatar">
                {{ records[0].studentName?.charAt(0) }}
              </el-avatar>
              <div class="title-info">
                <div class="name">{{ records[0].studentName }}</div>
                <div class="meta">
                  <span>{{ records[0].studentNo }}</span>
                  <el-tag size="small" type="info" style="margin-left: 8px">
                    {{ records.length }} 条记录
                  </el-tag>
                </div>
              </div>
            </div>
          </template>

          <el-timeline>
            <el-timeline-item
                v-for="record in records"
                :key="record.recordId"
                :timestamp="formatDateTime(record.createdAt)"
                :type="getTimelineType(record.difficultyLevel)"
            >
              <div class="timeline-content">
                <div class="content-header">
                  <el-tag :type="getLevelTagType(record.difficultyLevel)" size="small">
                    {{ getLevelText(record.difficultyLevel) }}
                  </el-tag>
                  <el-tag type="info" size="small">{{ record.difficultyType }}</el-tag>
                </div>
                <div class="content-body">
                  <p><strong>困难：</strong>{{ record.description }}</p>
                  <p><strong>计划：</strong>{{ record.assistancePlan }}</p>
                  <p v-if="record.result"><strong>结果：</strong>{{ record.result }}</p>
                </div>
                <div class="content-footer">
                  <el-button size="small" type="primary" link @click="viewDetail(record)">
                    查看详情
                  </el-button>
                  <el-button size="small" type="success" link @click="followUp(record)">
                    继续跟进
                  </el-button>
                </div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-collapse-item>
      </el-collapse>

      <el-empty v-if="!loading && Object.keys(groupedByStudent).length === 0" description="暂无帮扶记录" />
    </div>

    <!-- 新增帮扶对话框 -->
    <el-dialog
        v-model="showCreateDialog"
        title="新增帮扶记录"
        width="600px"
        :close-on-click-modal="false"
    >
      <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="100px"
      >
        <el-form-item label="学生ID" prop="studentId">
          <el-input-number
              v-model="formData.studentId"
              :min="1"
              placeholder="请输入学生ID"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="困难类型" prop="difficultyType">
          <el-select v-model="formData.difficultyType" placeholder="请选择困难类型">
            <el-option label="就业困难" value="就业困难" />
            <el-option label="学业困难" value="学业困难" />
            <el-option label="心理困难" value="心理困难" />
            <el-option label="经济困难" value="经济困难" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="困难等级" prop="difficultyLevel">
          <el-select v-model="formData.difficultyLevel" placeholder="请选择困难等级">
            <el-option label="低" value="low" />
            <el-option label="中" value="medium" />
            <el-option label="高" value="high" />
          </el-select>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
              v-model="formData.description"
              type="textarea"
              :rows="3"
              placeholder="请描述学生遇到的困难"
          />
        </el-form-item>

        <el-form-item label="帮扶计划" prop="assistancePlan">
          <el-input
              v-model="formData.assistancePlan"
              type="textarea"
              :rows="3"
              placeholder="请输入帮扶计划"
          />
        </el-form-item>

        <el-form-item label="帮扶措施" prop="assistanceActions">
          <el-input
              v-model="formData.assistanceActions"
              type="textarea"
              :rows="3"
              placeholder="请输入已采取的帮扶措施"
          />
        </el-form-item>

        <el-form-item label="结果" prop="result">
          <el-input
              v-model="formData.result"
              placeholder="请输入帮扶结果（可选）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showTrackingDialog" :title="`帮扶跟踪 - ${currentRecord?.studentName || ''}`" width="760px">
      <el-form label-width="100px">
        <el-form-item label="跟踪内容">
          <el-input v-model="trackingForm.content" type="textarea" :rows="3" placeholder="请输入本次跟踪记录" />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="8">
            <el-form-item label="进展状态">
              <el-select v-model="trackingForm.progressStatus" style="width: 100%">
                <el-option label="进行中" value="in_progress" />
                <el-option label="已完成" value="completed" />
                <el-option label="暂停" value="paused" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="干预前评分">
              <el-input-number v-model="trackingForm.beforeScore" :min="0" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="干预后评分">
              <el-input-number v-model="trackingForm.afterScore" :min="0" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="下一步计划">
          <el-input v-model="trackingForm.nextAction" placeholder="请输入下一步帮扶计划" />
        </el-form-item>
      </el-form>

      <el-divider>跟踪历史</el-divider>
      <el-timeline v-if="trackingList.length">
        <el-timeline-item v-for="item in trackingList" :key="item.trackingId" :timestamp="formatDateTime(item.createdAt || item.trackingDate)">
          <div style="line-height: 1.8">
            <div><b>内容：</b>{{ item.trackingContent }}</div>
            <div><b>状态：</b>{{ progressStatusText(item.progressStatus) }}</div>
            <div><b>评分：</b>{{ item.beforeScore ?? '-' }} → {{ item.afterScore ?? '-' }}</div>
            <div>
              <b>提升率：</b>
              <span :style="{ color: improvementRateColor(item.improvementRate), fontWeight: 500 }">
                {{ improvementRateText(item.improvementRate) }}
              </span>
            </div>
            <div>
              <b>效果：</b>
              <el-tag size="small" :type="effectivenessTagType(item.effectiveness)">{{ effectivenessText(item.effectiveness) }}</el-tag>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无跟踪记录" />

      <template #footer>
        <el-button @click="showTrackingDialog = false">关闭</el-button>
        <el-button type="primary" :loading="trackingLoading" @click="submitTracking">提交跟踪</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Plus, Search, Clock, User, Document, Notebook, CircleCheck,
  Calendar, View, Edit, Histogram
} from '@element-plus/icons-vue'
import {
  getAssistanceRecordList,
  createAssistanceRecord,
  addAssistanceTracking,
  getAssistanceTrackingList
} from '@/api/teacher'

const loading = ref(false)
const submitLoading = ref(false)
const viewMode = ref('timeline')
const assistanceList = ref([])
const activeStudents = ref([])
const showCreateDialog = ref(false)
const formRef = ref(null)
const showTrackingDialog = ref(false)
const trackingLoading = ref(false)
const currentRecord = ref(null)
const trackingList = ref([])
const trackingForm = ref({
  content: '',
  progressStatus: 'in_progress',
  nextAction: '',
  beforeScore: null,
  afterScore: null
})

const searchForm = ref({
  keyword: '',
  difficultyType: null
})

const formData = ref({
  studentId: null,
  difficultyType: '',
  difficultyLevel: '',
  description: '',
  assistancePlan: '',
  assistanceActions: '',
  result: ''
})

const pagination = ref({
  current: 1,
  size: 20,
  total: 0
})

const rules = {
  studentId: [{ required: true, message: '请输入学生ID', trigger: 'blur' }],
  difficultyType: [{ required: true, message: '请选择困难类型', trigger: 'change' }],
  difficultyLevel: [{ required: true, message: '请选择困难等级', trigger: 'change' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
  assistancePlan: [{ required: true, message: '请输入帮扶计划', trigger: 'blur' }]
}

// 按学生分组
const groupedByStudent = computed(() => {
  const grouped = {}
  assistanceList.value.forEach(record => {
    const studentId = record.studentId
    if (!grouped[studentId]) {
      grouped[studentId] = []
    }
    grouped[studentId].push(record)
  })
  // 按时间倒序排序
  Object.keys(grouped).forEach(key => {
    grouped[key].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  })
  return grouped
})

const getTimelineType = (level) => {
  const typeMap = {
    'low': 'success',
    'medium': 'warning',
    'high': 'danger'
  }
  return typeMap[level] || 'primary'
}

const getLevelTagType = (level) => {
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

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

const formatDateTime = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const loadData = async () => {
  loading.value = true
  try {
    const response = await getAssistanceRecordList()
    if (response.success) {
      let data = response.data || []

      if (searchForm.value.keyword) {
        data = data.filter(r =>
          r.studentName?.includes(searchForm.value.keyword) ||
          r.studentNo?.includes(searchForm.value.keyword)
        )
      }
      if (searchForm.value.difficultyType) {
        data = data.filter(r => r.difficultyType === searchForm.value.difficultyType)
      }

      assistanceList.value = data
      pagination.value.total = data.length
    }
  } catch (error) {
    ElMessage.error('加载帮扶记录失败')
  } finally {
    loading.value = false
  }
}

const handleCreate = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const response = await createAssistanceRecord(formData.value)
        if (response.success) {
          ElMessage.success('新增成功')
          showCreateDialog.value = false
          formRef.value.resetFields()
          loadData()
        }
      } catch (error) {
        ElMessage.error('新增失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const viewDetail = async (record) => {
  currentRecord.value = record
  try {
    const res = await getAssistanceTrackingList(record.recordId)
    if (res.success) {
      trackingList.value = res.data || []
      showTrackingDialog.value = true
    }
  } catch (e) {
    ElMessage.error('获取跟踪记录失败')
  }
}

const followUp = (record) => {
  currentRecord.value = record
  trackingForm.value = {
    content: '',
    progressStatus: 'in_progress',
    nextAction: '',
    beforeScore: null,
    afterScore: null
  }
  trackingList.value = []
  showTrackingDialog.value = true
  viewDetail(record)
}

const submitTracking = async () => {
  if (!currentRecord.value) return
  if (!trackingForm.value.content?.trim()) {
    return ElMessage.warning('请输入跟踪内容')
  }
  trackingLoading.value = true
  try {
    await addAssistanceTracking(currentRecord.value.recordId, trackingForm.value)
    ElMessage.success('跟踪记录添加成功')
    trackingForm.value.content = ''
    trackingForm.value.nextAction = ''
    await viewDetail(currentRecord.value)
  } catch (e) {
    ElMessage.error('跟踪失败')
  } finally {
    trackingLoading.value = false
  }
}

const progressStatusText = (v) => ({ in_progress: '进行中', completed: '已完成', paused: '暂停' }[v] || v)

const effectivenessText = (v) => ({ excellent: '显著', good: '良好', fair: '一般', poor: '较差' }[v] || '-')

const effectivenessTagType = (v) => ({ excellent: 'success', good: 'primary', fair: 'warning', poor: 'danger' }[v] || 'info')

const improvementRateText = (v) => {
  if (v === null || v === undefined || v === '') return '-'
  return `${v}%`
}

const improvementRateColor = (v) => {
  if (v === null || v === undefined || v === '') return '#909399'
  return Number(v) < 0 ? '#F56C6C' : '#67C23A'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.teacher-assistance {
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

    .record-card {
      margin-bottom: 16px;

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        .header-left {
          display: flex;
          align-items: center;
          gap: 12px;

          .student-info {
            .student-name {
              font-size: 16px;
              font-weight: 600;
              color: #303133;
            }

            .student-no {
              font-size: 13px;
              color: #909399;
              margin-top: 2px;
            }
          }
        }

        .header-right {
          display: flex;
          gap: 8px;
        }
      }

      .card-body {
        .section {
          margin-bottom: 16px;

          .section-title {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 14px;
            font-weight: 600;
            color: #606266;
            margin-bottom: 8px;
          }

          .section-content {
            font-size: 14px;
            color: #606266;
            line-height: 1.6;
            padding-left: 22px;

            &.result {
              color: #67C23A;
              font-weight: 500;
            }
          }
        }

        .record-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding-top: 12px;
          border-top: 1px solid #f0f0f0;
          margin-top: 12px;

          .footer-info {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 13px;
            color: #909399;
          }

          .footer-actions {
            display: flex;
            gap: 8px;
          }
        }
      }
    }
  }

  // 学生维度视图
  .student-view {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    padding: 20px;

    .collapse-title {
      display: flex;
      align-items: center;
      gap: 12px;
      width: 100%;

      .title-info {
        flex: 1;

        .name {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }

        .meta {
          font-size: 13px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }

    .timeline-content {
      background: #f5f7fa;
      padding: 12px;
      border-radius: 6px;

      .content-header {
        display: flex;
        gap: 8px;
        margin-bottom: 12px;
      }

      .content-body {
        font-size: 14px;
        color: #606266;
        line-height: 1.6;

        p {
          margin: 8px 0;

          strong {
            color: #303133;
          }
        }
      }

      .content-footer {
        display: flex;
        gap: 12px;
        margin-top: 12px;
        padding-top: 12px;
        border-top: 1px solid #e4e7ed;
      }
    }
  }
}
</style>
<style scoped lang="scss">
.teacher-assistance {
  .action-card {
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
