<template>
  <div class="student-warning-center">
    <el-card class="mb16">
      <el-row :gutter="16">
        <el-col :span="6"><el-statistic title="总预警" :value="stats.totalCount || 0" /></el-col>
        <el-col :span="6"><el-statistic title="未查看" :value="unviewedCount" /></el-col>
        <el-col :span="6"><el-statistic title="待处理" :value="stats.pendingCount || 0" /></el-col>
        <el-col :span="6"><el-statistic title="处理中" :value="stats.processingCount || 0" /></el-col>
      </el-row>
    </el-card>

    <el-card class="mb16">
      <el-form :inline="true">
        <el-form-item label="等级">
          <el-select v-model="query.warningLevel" clearable placeholder="全部等级" style="width: 120px">
            <el-option label="紧急" value="urgent" />
            <el-option label="高" value="high" />
            <el-option label="中" value="medium" />
            <el-option label="低" value="low" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.handleStatus" clearable placeholder="全部状态" style="width: 130px">
            <el-option label="待处理" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已解决" value="resolved" />
            <el-option label="已忽略" value="ignored" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="query.keyword" clearable placeholder="原因关键词" style="width: 220px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="pagedWarnings" v-loading="loading" stripe>
        <el-table-column prop="warningType" label="类型" width="120">
          <template #default="{ row }">{{ typeText(row.warningType) }}</template>
        </el-table-column>
        <el-table-column prop="warningLevel" label="等级" width="100">
          <template #default="{ row }"><el-tag :type="levelType(row.warningLevel)">{{ levelText(row.warningLevel) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="triggerReason" label="触发原因" min-width="220" show-overflow-tooltip />
        <el-table-column prop="handleStatus" label="处理状态" width="110">
          <template #default="{ row }">{{ statusText(row.handleStatus) }}</template>
        </el-table-column>
        <el-table-column prop="detectionTime" label="时间" width="180">
          <template #default="{ row }">{{ fmt(row.detectionTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewWarning(row)">查看</el-button>
            <el-button link type="success" @click="openRespond(row)">回应</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pager-wrap">
        <el-pagination
          v-model:current-page="pager.current"
          v-model:page-size="pager.size"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :total="filteredWarnings.length"
        />
      </div>
    </el-card>

    <el-dialog v-model="showRespond" title="回应预警" width="520px">
      <el-input v-model="responseText" type="textarea" :rows="5" placeholder="请输入你的说明、计划与需要的帮助" />
      <template #footer>
        <el-button @click="showRespond = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitRespond">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentWarnings, getStudentWarningStats, getStudentUnviewedWarningCount, viewStudentWarning, respondStudentWarning } from '@/api/student'

const loading = ref(false)
const submitLoading = ref(false)
const warnings = ref([])
const stats = ref({})
const unviewedCount = ref(0)
const showRespond = ref(false)
const responseText = ref('')
const currentWarningId = ref(null)
const query = ref({ warningLevel: null, handleStatus: null, keyword: '' })
const pager = ref({ current: 1, size: 10 })

const filteredWarnings = computed(() => {
  if (!query.value.keyword?.trim()) return warnings.value
  const k = query.value.keyword.trim().toLowerCase()
  return warnings.value.filter(w => (w.triggerReason || '').toLowerCase().includes(k))
})

const pagedWarnings = computed(() => {
  const start = (pager.value.current - 1) * pager.value.size
  return filteredWarnings.value.slice(start, start + pager.value.size)
})

const loadData = async () => {
  loading.value = true
  try {
    const params = { warningLevel: query.value.warningLevel, handleStatus: query.value.handleStatus }
    const [listRes, statsRes, countRes] = await Promise.all([
      getStudentWarnings(params), getStudentWarningStats(), getStudentUnviewedWarningCount()
    ])
    if (listRes.success) warnings.value = listRes.data || []
    if (statsRes.success) stats.value = statsRes.data || {}
    if (countRes.success) unviewedCount.value = Number(countRes.data || 0)
  } catch (e) {
    ElMessage.error('加载预警信息失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => { pager.value.current = 1; loadData() }
const handleReset = () => {
  query.value = { warningLevel: null, handleStatus: null, keyword: '' }
  pager.value.current = 1
  loadData()
}

const viewWarning = async (row) => {
  try {
    const res = await viewStudentWarning(row.id)
    if (res?.success !== false) {
      row.studentViewed = true
      unviewedCount.value = Math.max(0, Number(unviewedCount.value || 0) - 1)
      ElMessage.success('已标记为查看')
    }
  } catch (e) { ElMessage.error('查看失败') }
}

const openRespond = (row) => {
  currentWarningId.value = row.id
  responseText.value = row.studentResponse || ''
  showRespond.value = true
}

const submitRespond = async () => {
  if (!responseText.value.trim()) return ElMessage.warning('请填写回应内容')
  submitLoading.value = true
  try {
    await respondStudentWarning(currentWarningId.value, responseText.value)
    ElMessage.success('回应成功')
    showRespond.value = false
    loadData()
  } catch (e) { ElMessage.error('回应失败') } finally { submitLoading.value = false }
}

const levelType = (v) => ({ urgent: 'danger', high: 'danger', medium: 'warning', low: 'success' }[v] || 'info')
const levelText = (v) => ({ urgent: '紧急', high: '高', medium: '中', low: '低' }[v] || v)
const typeText = (v) => ({ employment: '就业预警', skill: '技能预警', interview: '面试预警', resume: '简历预警' }[v] || v)
const statusText = (v) => ({ pending: '待处理', processing: '处理中', resolved: '已解决', ignored: '已忽略' }[v] || v)

watch(() => [pager.value.current, pager.value.size], () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
})
const fmt = (v) => (v ? new Date(v).toLocaleString('zh-CN') : '-')

onMounted(loadData)
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.pager-wrap { margin-top: 12px; display: flex; justify-content: flex-end; }
</style>