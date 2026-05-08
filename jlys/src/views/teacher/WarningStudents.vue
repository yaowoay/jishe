<template>
  <div class="employment-cockpit">
    <!-- 决策概览 KPI -->
    <el-row :gutter="16" class="kpi-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="kpi-card kpi-red">
          <div class="kpi-value">{{ cockpitTier.red }}</div>
          <div class="kpi-label">红色预警 · 立即面谈</div>
          <div class="kpi-sub">规则：零投递 / 面试多次未通过</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="kpi-card kpi-yellow">
          <div class="kpi-value">{{ cockpitTier.yellow }}</div>
          <div class="kpi-label">黄色预警 · 辅导关注</div>
          <div class="kpi-sub">模型 / 综合分 / 模拟面 & 期望薪资</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="kpi-card kpi-blue">
          <div class="kpi-value">{{ cockpitTier.blue }}</div>
          <div class="kpi-label">蓝色建议 · 发展机会</div>
          <div class="kpi-sub">活跃低命中 / 实训积极</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="kpi-card kpi-health">
          <div class="kpi-health-inner">
            <el-progress type="dashboard" :percentage="healthIndex" :width="88" :stroke-width="10">
              <template #default="{ percentage }">
                <span class="health-num">{{ percentage }}</span>
              </template>
            </el-progress>
            <div class="kpi-label">就业健康指数</div>
            <div class="kpi-sub">待跟进 {{ cockpitAttention }} 人</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分级 Tab + 筛选 -->
    <el-card class="filter-card" shadow="never">
      <el-tabs v-model="activeTierTab" @tab-change="onTierTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="🔴 红色 · 立即面谈" name="red" />
        <el-tab-pane label="🟡 黄色 · 辅导关注" name="yellow" />
        <el-tab-pane label="🔵 蓝色 · 机会建议" name="blue" />
      </el-tabs>
      <div class="filter-toolbar">
        <!-- 单独一行四列网格，避免与按钮挤在同一 grid 时 select 宽度不一致 -->
        <div class="filter-fields-row">
          <div class="filter-cell">
            <el-input v-model="searchForm.keyword" class="filter-control" placeholder="学生姓名 / 学号" clearable />
          </div>
          <div class="filter-cell">
            <el-input v-model="searchForm.major" class="filter-control" placeholder="专业（模糊）" clearable />
          </div>
          <div class="filter-cell">
            <el-select v-model="searchForm.handleStatus" class="filter-control" placeholder="处理进度" clearable>
              <el-option label="待处理" value="pending" />
              <el-option label="处理中" value="processing" />
              <el-option label="已解决" value="resolved" />
              <el-option label="已忽略" value="ignored" />
            </el-select>
          </div>
          <div class="filter-cell">
            <el-select v-model="searchForm.warningType" class="filter-control" placeholder="预警类型（默认全部）" clearable>
              <el-option label="驾驶舱" value="cockpit" />
              <el-option label="就业模型" value="employment" />
              <el-option label="技能" value="skill" />
              <el-option label="简历" value="resume" />
              <el-option label="面试" value="interview" />
            </el-select>
          </div>
        </div>
        <div class="filter-actions">
          <el-button type="primary" :loading="loading" @click="handleSearch">查询</el-button>
          <el-button @click="triggerServerScan" :loading="scanLoading">全量扫描</el-button>
        </div>
      </div>
    </el-card>

    <!-- 工作表 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe class="warning-table" style="width: 100%">
        <el-table-column label="" width="40" align="center">
          <template #default="{ row }">
            <span class="tier-dot" :class="'dot-' + alertTierDotKey(row.alertTier)" />
          </template>
        </el-table-column>
        <el-table-column prop="studentName" label="学生" min-width="88" show-overflow-tooltip />
        <el-table-column prop="major" label="专业" min-width="108" show-overflow-tooltip />
        <el-table-column label="类型" min-width="88" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="plain">{{ warningTypeLabel(rowWarningType(row) || row.warningType || row.warning_type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="预警类型" min-width="86" align="center">
          <template #default="{ row }">
            <el-tag :type="tierTagType(row.alertTier)" effect="dark" size="small">
              {{ tierLabel(row.alertTier) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warningScore" label="综合分" min-width="82" align="center" />
        <el-table-column
          prop="triggerReason"
          label="风险摘要"
          min-width="300"
          show-overflow-tooltip
          class-name="col-risk-summary"
        />
        <el-table-column label="检测时间" min-width="158" show-overflow-tooltip>
          <template #default="{ row }">{{ formatDetectionTime(row.detectionTime) }}</template>
        </el-table-column>
        <el-table-column label="进度" min-width="102" align="center" class-name="col-handle-status">
          <template #default="{ row }">
            <el-tag size="small" :type="statusTag(row.handleStatus)">{{ statusText(row.handleStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link size="small" @click="openDrawer(row)">详情</el-button>
            <el-button
              type="warning"
              link
              size="small"
              :disabled="!hasStudentIdForRow(row)"
              :title="
                hasStudentIdForRow(row)
                  ? '按「' + warningTypeLabel(rowWarningType(row) || row.warningType || row.warning_type) + '」重新计算并保存'
                  : '缺少学生ID，无法刷新'
              "
              :loading="evalMap[row.id]"
              @click="reScanRow(row)"
            >
              刷新
            </el-button>
            <el-button type="primary" link size="small" @click="quickRemind(row)">提醒</el-button>
            <el-button type="success" link size="small" @click="openHandle(row)">标注</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="fetchWarnings"
        @size-change="onPageSizeChange"
        class="pager"
      />
    </el-card>

    <!-- 详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="学生预警详情" size="620px" destroy-on-close @closed="onDrawerClosed">
      <template v-if="drawerRow">
        <div class="drawer-head">
          <div>
            <div class="name">{{ drawerRow.studentName }} · {{ drawerRow.studentNo }}</div>
            <div class="sub">
              {{ drawerRow.major }} ｜ 类型 {{ warningTypeLabel(rowWarningType(drawerRow)) }} ｜ 分级
              {{ tierLabel(drawerRow.alertTier) }}
            </div>
          </div>
        </div>

        <template v-if="rowWarningType(drawerRow) === 'cockpit'">
          <h4 class="sec-title">四维风险雷达（分值越高风险越大）</h4>
          <div ref="radarRef" class="radar-box" />

          <h4 class="sec-title">证据链</h4>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="近30天投递">{{ analysisBrief.applications30d ?? '—' }}</el-descriptions-item>
            <el-descriptions-item label="面试后未通过次数">{{ analysisBrief.postInterviewRejections ?? '—' }}</el-descriptions-item>
            <el-descriptions-item label="AI面试均分">{{ analysisBrief.avgInterviewScore ?? '—' }}</el-descriptions-item>
            <el-descriptions-item label="模拟面试分">{{ analysisBrief.mockInterviewScore ?? '—' }}</el-descriptions-item>
            <el-descriptions-item label="模型就业概率">
              {{ analysisBrief.modelSuccessProbability != null ? (analysisBrief.modelSuccessProbability * 100).toFixed(0) + '%' : '—' }}
            </el-descriptions-item>
            <el-descriptions-item label="规则标签">{{ (analysisBrief.ruleTags || []).join(', ') || '—' }}</el-descriptions-item>
            <el-descriptions-item v-if="analysisBrief.lastEvaluatedAt" label="最近重算时间">
              {{ analysisBrief.lastEvaluatedAt }}
            </el-descriptions-item>
          </el-descriptions>
        </template>
        <template v-else>
          <el-alert type="info" :closable="false" show-icon class="drawer-type-tip">
            当前为「{{ warningTypeLabel(rowWarningType(drawerRow)) }}」类预警，由规则或就业模型生成；无驾驶舱雷达图。可点「重算」按该类型规则刷新本条。
          </el-alert>
          <h4 class="sec-title">本条预警</h4>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="等级">{{ drawerRow.warningLevel || '—' }}</el-descriptions-item>
            <el-descriptions-item label="综合分">{{ drawerRow.warningScore ?? '—' }}</el-descriptions-item>
            <el-descriptions-item label="摘要">{{ drawerRow.triggerReason || '—' }}</el-descriptions-item>
            <el-descriptions-item label="检测时间">{{ formatDetectionTime(drawerRow.detectionTime) }}</el-descriptions-item>
          </el-descriptions>
        </template>

        <h4 class="sec-title">建议话术</h4>
        <el-input type="textarea" :rows="5" :model-value="drawerRow.suggestedScript || ''" readonly />

        <div class="drawer-actions">
          <el-button type="primary" @click="quickRemind(drawerRow)">一键提醒学生</el-button>
          <el-button @click="openHandle(drawerRow)">标注处理记录</el-button>
        </div>
      </template>
    </el-drawer>

    <!-- 处理对话框 -->
    <el-dialog v-model="handleVisible" title="面谈 / 处理标注" width="520px" :close-on-click-modal="false">
      <el-form v-if="handleRow" label-width="100px">
        <el-form-item label="学生">{{ handleRow.studentName }}</el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="handleForm.handleStatus" style="width: 100%">
            <el-option label="待处理" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已解决" value="resolved" />
            <el-option label="已忽略" value="ignored" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进说明">
          <el-input v-model="handleForm.handleRemark" type="textarea" :rows="4" placeholder="例：已电话沟通，约定本周投递5家" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleVisible = false">取消</el-button>
        <el-button type="primary" :loading="handleLoading" @click="submitHandle">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import {
  getEarlyWarnings,
  getEarlyWarningStats,
  handleEarlyWarning,
  reEvaluateStudent,
  quickRemindWarning
} from '@/api/teacher'
import request from '@/api/index'

const loading = ref(false)
const scanLoading = ref(false)
const activeTierTab = ref('all')
const tableData = ref([])
const pagination = ref({ current: 1, size: 20, total: 0 })

const searchForm = ref({
  keyword: '',
  major: '',
  handleStatus: null,
  /** 空：不传参，后端返回全部 warning_type */
  warningType: null
})

const cockpitTier = reactive({ red: 0, yellow: 0, blue: 0 })
const cockpitAttention = ref(0)
const healthIndex = ref(100)

const drawerVisible = ref(false)
const drawerRow = ref(null)
const radarRef = ref(null)
let radarChart = null

const analysisBrief = computed(() => {
  const raw = drawerRow.value?.analysisJson
  if (!raw) return {}
  try {
    return JSON.parse(raw)
  } catch {
    return {}
  }
})

const handleVisible = ref(false)
const handleRow = ref(null)
const handleLoading = ref(false)
const handleForm = reactive({ handleStatus: 'processing', handleRemark: '' })

const evalMap = reactive({})

const buildParams = () => {
  const p = {
    handleStatus: searchForm.value.handleStatus || 'pending,processing,resolved,ignored'
  }
  const wt = searchForm.value.warningType
  if (wt !== undefined && wt !== null && String(wt).trim() !== '') {
    p.warningType = String(wt).trim()
  }
  if (activeTierTab.value !== 'all') {
    p.alertTier = activeTierTab.value
  }
  if (searchForm.value.major?.trim()) {
    p.major = searchForm.value.major.trim()
  }
  const kw = searchForm.value.keyword?.trim()
  if (kw) {
    p.keyword = kw
  }
  return p
}

const fetchWarnings = async () => {
  loading.value = true
  try {
    const res = await getEarlyWarnings({
      ...buildParams(),
      page: pagination.value.current,
      size: pagination.value.size
    })
    if (!res.success) {
      ElMessage.error(res.message || '加载失败')
      return
    }
    const pageData = res.data
    tableData.value = pageData?.records ?? []
    pagination.value.total = Number(pageData?.total ?? 0)
  } catch (e) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  pagination.value.current = 1
  await fetchWarnings()
}

const loadStats = async () => {
  try {
    const res = await getEarlyWarningStats()
    if (!res.success || !res.data) return
    const t = res.data.cockpitTierStats || {}
    cockpitTier.red = Number(t.red || 0)
    cockpitTier.yellow = Number(t.yellow || 0)
    cockpitTier.blue = Number(t.blue || 0)
    cockpitAttention.value = Number(res.data.cockpitAttentionTotal || 0)
    healthIndex.value = Number(res.data.employmentHealthIndex ?? 100)
  } catch {
    /* ignore */
  }
}

const onTierTabChange = () => {
  handleSearch()
}

const onPageSizeChange = () => {
  pagination.value.current = 1
  fetchWarnings()
}

const alertTierDotKey = (t) => {
  const k = t == null ? '' : String(t).trim().toLowerCase()
  return k === 'red' || k === 'yellow' || k === 'blue' ? k : 'none'
}

const tierLabel = (t) => {
  const k = t == null ? '' : String(t).trim().toLowerCase()
  return { red: '红色', yellow: '黄色', blue: '蓝色' }[k] || (t != null && String(t).trim() !== '' ? String(t).trim() : '未分级')
}
const tierTitle = (t) => tierLabel(t)
const tierTagType = (t) => {
  const k = t == null ? '' : String(t).trim().toLowerCase()
  return k === 'red' ? 'danger' : k === 'blue' ? 'info' : k === 'yellow' ? 'warning' : 'info'
}

const WARNING_TYPE_LABELS = {
  cockpit: '驾驶舱',
  employment: '就业',
  skill: '技能',
  resume: '简历',
  interview: '面试'
}

const warningTypeLabel = (t) => {
  if (t == null || String(t).trim() === '') return '—'
  const k = String(t).trim().toLowerCase()
  return WARNING_TYPE_LABELS[k] || String(t).trim() || '—'
}

/** 统一成小写，避免库里 COCKPIT / 前后空格 导致无法匹配 */
const rowWarningType = (row) => {
  if (!row) return ''
  const t = row.warningType ?? row.warning_type
  if (t == null || String(t).trim() === '') return ''
  return String(t).trim().toLowerCase()
}

const hasStudentIdForRow = (row) => {
  const sid = row?.studentId ?? row?.student_id
  return sid != null && String(sid) !== ''
}

/** 列表展示用；重算成功后该时间会刷新，便于确认已落库 */
const formatDetectionTime = (v) => {
  if (v == null || v === '') return '—'
  const s = typeof v === 'string' ? v : String(v)
  return s.length >= 16 ? s.slice(0, 16).replace('T', ' ') : s
}
const sourceLabel = (s) =>
  ({ rule: '规则引擎', model: '模型', composite: '综合评分', opportunity: '行为机会' }[s] || s || '—')

const statusText = (s) =>
  ({ pending: '待处理', processing: '处理中', resolved: '已解决', ignored: '已忽略' }[s] || s)
const statusTag = (s) =>
  ({ pending: 'warning', processing: 'primary', resolved: 'success', ignored: 'info' }[s] || 'info')

const openDrawer = (row) => {
  drawerRow.value = row
  drawerVisible.value = true
  nextTick(() => renderRadar())
}

const onDrawerClosed = () => {
  if (radarChart) {
    radarChart.dispose()
    radarChart = null
  }
}

const renderRadar = () => {
  if (!radarRef.value || !drawerRow.value?.analysisJson) return
  let d = {}
  try {
    d = JSON.parse(drawerRow.value.analysisJson)
  } catch {
    return
  }
  const indicators = [
    { name: '学业风险', max: 100 },
    { name: '能力风险', max: 100 },
    { name: '行为风险', max: 100 },
    { name: '结果风险', max: 100 }
  ]
  const data = [
    d.academicRisk ?? 0,
    d.abilityRisk ?? 0,
    d.behaviorRisk ?? 0,
    d.resultRisk ?? 0
  ]
  if (!radarChart) {
    radarChart = echarts.init(radarRef.value)
  }
  radarChart.setOption({
    radar: { indicator: indicators, radius: '65%' },
    series: [
      {
        type: 'radar',
        data: [{ value: data, name: '风险', areaStyle: { opacity: 0.15 } }],
        lineStyle: { width: 2 }
      }
    ]
  })
}

watch(drawerVisible, (v) => {
  if (v) {
    nextTick(() => renderRadar())
  }
})

const quickRemind = async (row) => {
  try {
    await quickRemindWarning(row.id)
    ElMessage.success('已发起提醒（待对接消息通道）')
  } catch {
    ElMessage.error('提醒失败')
  }
}

const reScanRow = async (row) => {
  const sid = row.studentId ?? row.student_id
  if (sid == null || sid === '') {
    ElMessage.error('该条记录缺少学生ID，无法重算')
    return
  }
  const wt = rowWarningType(row)
  // 未填类型时后端会对该学生执行全套预警重算（技能/简历/面试/就业/驾驶舱）
  const payload = { studentId: sid }
  if (wt) payload.warningType = wt
  evalMap[row.id] = true
  try {
    await reEvaluateStudent(payload)
    const cockpitAsync = !wt || wt === 'cockpit'
    if (cockpitAsync) {
      ElMessage.success(
        wt
          ? '规则与综合分已更新；模型在后台合并，稍后会自动刷新一次列表'
          : '已对该学生执行全套预警重算（含驾驶舱后台模型），稍后自动刷新列表'
      )
      await fetchWarnings()
      await loadStats()
      setTimeout(() => {
        fetchWarnings()
        loadStats()
      }, 4000)
    } else {
      ElMessage.success('已按该预警类型重新评估并保存')
      await fetchWarnings()
      await loadStats()
    }
    if (drawerVisible.value && drawerRow.value?.id === row.id) {
      const updated = tableData.value.find((r) => r.id === row.id)
      if (updated) drawerRow.value = updated
    }
  } catch (e) {
    ElMessage.error(e?.message || e?.response?.data?.message || '重算失败')
  } finally {
    evalMap[row.id] = false
  }
}

const openHandle = (row) => {
  handleRow.value = row
  handleForm.handleStatus = row.handleStatus || 'processing'
  handleForm.handleRemark = ''
  handleVisible.value = true
}

const submitHandle = async () => {
  if (!handleRow.value) return
  handleLoading.value = true
  try {
    await handleEarlyWarning(handleRow.value.id, {
      handleStatus: handleForm.handleStatus,
      handleRemark: handleForm.handleRemark
    })
    ElMessage.success('已保存')
    handleVisible.value = false
    await fetchWarnings()
    await loadStats()
  } catch {
    ElMessage.error('保存失败')
  } finally {
    handleLoading.value = false
  }
}

const triggerServerScan = async () => {
  scanLoading.value = true
  try {
    await request({ url: '/employment-warning/scan-active', method: 'post' })
    ElMessage.success('增量扫描已触发')
    await fetchWarnings()
    await loadStats()
  } catch {
    ElMessage.error('扫描失败')
  } finally {
    scanLoading.value = false
  }
}

onMounted(async () => {
  await loadStats()
  await fetchWarnings()
})
</script>

<style scoped lang="scss">
.employment-cockpit {
  padding-bottom: 24px;
}

.kpi-row {
  margin-bottom: 16px;
}

/* 四块 KPI 等高、内边距一致；健康指数仪表盘与其它三块数字卡视觉体量接近 */
$kpi-body-min-h: 142px;

.kpi-card {
  border: none;
  height: 100%;
  :deep(.el-card__body) {
    min-height: $kpi-body-min-h;
    padding: 14px 16px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  .kpi-value {
    font-size: 28px;
    font-weight: 700;
    line-height: 1.15;
    color: #303133;
  }
  .kpi-label {
    margin-top: 8px;
    font-size: 13px;
    font-weight: 600;
    color: #606266;
    line-height: 1.35;
  }
  .kpi-sub {
    margin-top: 6px;
    font-size: 12px;
    line-height: 1.4;
    color: #909399;
  }
}

.kpi-red {
  border-left: 4px solid #f56c6c;
}
.kpi-yellow {
  border-left: 4px solid #e6a23c;
}
.kpi-blue {
  border-left: 4px solid #409eff;
}

.kpi-health {
  :deep(.el-card__body) {
    padding-top: 12px;
    padding-bottom: 12px;
  }
  .kpi-health-inner {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    flex: 1;
  }
  .kpi-label {
    margin-top: 6px;
    text-align: center;
  }
  .kpi-sub {
    text-align: center;
  }
  .health-num {
    font-size: 22px;
    font-weight: 700;
    color: #303133;
  }
  :deep(.el-progress--dashboard) {
    margin: 0 auto;
  }
}

.filter-card {
  margin-bottom: 16px;
  border: none;
}

/* 外层：左侧四格等宽 + 右侧按钮；内层：仅负责 4 个控件 strict 25% 平分 */
.filter-toolbar {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
}

.filter-fields-row {
  flex: 1;
  min-width: 0;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  align-items: stretch;
}

@media (max-width: 1199px) {
  .filter-fields-row {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    flex: 1 1 100%;
  }
}

@media (max-width: 575px) {
  .filter-fields-row {
    grid-template-columns: 1fr;
  }
}

.filter-cell {
  min-width: 0;
  width: 100%;
  display: flex;
  align-items: stretch;
}

/*
 * 与下拉平分同一宽度：flex 子项用 flex:1 1 0 + width:0，
 * 避免 el-select（inline-block/内部 min-width）实际占位小于格子。
 */
.filter-cell > .filter-control {
  flex: 1 1 0%;
  width: 0 !important;
  min-width: 0;
  max-width: none;
}

.filter-control {
  max-width: 100%;
}

.filter-fields-row :deep(.el-input),
.filter-fields-row :deep(.el-select) {
  width: 100% !important;
  max-width: 100% !important;
  min-width: 0 !important;
  box-sizing: border-box;
}

.filter-fields-row :deep(.el-select) {
  display: block !important;
}

.filter-fields-row :deep(.el-select .el-input) {
  width: 100% !important;
  min-width: 0 !important;
}

/* EP 新版 Select 只有 el-select__wrapper，需与 el-input 边框盒同高同宽 */
.filter-fields-row :deep(.el-select__wrapper) {
  width: 100% !important;
  min-width: 0 !important;
  min-height: 36px;
  box-sizing: border-box !important;
}

.filter-fields-row :deep(.el-input__wrapper),
.filter-fields-row :deep(.el-select .el-input__wrapper) {
  width: 100% !important;
  min-width: 0 !important;
  min-height: 36px;
  box-sizing: border-box !important;
}

.filter-fields-row :deep(.el-input__inner),
.filter-fields-row :deep(.el-select .el-input__inner) {
  box-sizing: border-box;
}

/* 前两列为普通输入框，略压低高度；后两列下拉保持原高度 */
.filter-fields-row .filter-cell:nth-child(-n + 2) :deep(.el-input__wrapper) {
  min-height: 20px;
  height: 18px;
}

.filter-actions {
  display: inline-flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

@media (max-width: 1199px) {
  .filter-actions {
    flex: 1 1 auto;
    justify-content: flex-end;
    width: 100%;
  }
}

.table-card {
  border: none;
}

.warning-table {
  width: 100%;
}

:deep(.col-risk-summary .cell) {
  padding-right: 10px;
}
:deep(.col-handle-status .cell) {
  padding-left: 6px;
}

.tier-dot {
  display: inline-block;
  width: 14px;
  height: 14px;
  border-radius: 50%;
}
.dot-red {
  background: #f56c6c;
}
.dot-yellow {
  background: #e6a23c;
}
.dot-blue {
  background: #409eff;
}

.pager {
  margin-top: 16px;
  justify-content: flex-end;
}

.drawer-head {
  margin-bottom: 16px;
  .name {
    font-size: 18px;
    font-weight: 600;
  }
  .sub {
    color: #909399;
    font-size: 13px;
    margin-top: 4px;
  }
}

.sec-title {
  margin: 16px 0 8px;
  font-size: 14px;
  color: #303133;
}

.radar-box {
  height: 280px;
  width: 100%;
}

.drawer-actions {
  margin-top: 20px;
  display: flex;
  gap: 12px;
}

.drawer-type-tip {
  margin-bottom: 12px;
}

.student-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  .student-name {
    font-weight: 600;
    white-space: nowrap;
  }
  .student-major {
    color: #909399;
    font-size: 12px;
    white-space: nowrap;
  }
}

.tier-dot {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
}
.dot-red { background: #f56c6c; box-shadow: 0 0 6px #f56c6c; }
.dot-yellow { background: #e6a23c; box-shadow: 0 0 6px #e6a23c; }
.dot-blue { background: #409eff; box-shadow: 0 0 6px #409eff; }
.dot-none { background: #dcdfe6; }
</style>
