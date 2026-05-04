<template>
  <div class="cooperation-page">
    <section class="hero">
      <div>
        <h1>校企合作引擎</h1>
        <p>一站式完成：合作项目申请、实训基地共建、订单班管理、历史案例参考</p>
      </div>
      <el-button type="primary" size="large" @click="refreshAll">刷新总览</el-button>
    </section>

    <el-row :gutter="16" class="stat-row">
      <el-col v-for="item in statCards" :key="item.key" :xs="24" :sm="12" :md="6">
        <div class="stat-card">
          <div class="label">{{ item.label }}</div>
          <div class="value">{{ stats[item.key] || 0 }}</div>
        </div>
      </el-col>
    </el-row>

    <el-card shadow="never" class="tabs-card">
      <el-tabs v-model="active">
        <el-tab-pane label="合作项目申请" name="project">
          <project-apply-panel @changed="refreshAll" @projects-loaded="handleProjectsLoaded" />
        </el-tab-pane>
        <el-tab-pane label="实训基地共建流程" name="base">
          <training-base-panel :project-options="approvedProjects" @changed="refreshAll" />
        </el-tab-pane>
        <el-tab-pane label="订单班管理" name="order">
          <order-class-panel :project-options="approvedProjects" @changed="refreshAll" />
        </el-tab-pane>
        <el-tab-pane label="历史案例参考库" name="case">
          <case-library-panel />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCooperationStats } from '@/api/cooperation'
import ProjectApplyPanel from '@/components/cooperation/ProjectApplyPanel.vue'
import TrainingBasePanel from '@/components/cooperation/TrainingBasePanel.vue'
import OrderClassPanel from '@/components/cooperation/OrderClassPanel.vue'
import CaseLibraryPanel from '@/components/cooperation/CaseLibraryPanel.vue'

const active = ref('project')
const stats = ref({})
const allProjects = ref([])

const statCards = [
  { key: 'totalProjects', label: '合作项目总数' },
  { key: 'submittedProjects', label: '审核中项目' },
  { key: 'ongoingProjects', label: '进行中项目' },
  { key: 'totalClasses', label: '订单班总数' }
]

const approvedProjects = computed(() =>
  allProjects.value.filter(item => ['approved', 'ongoing', 'completed'].includes(item.status))
)

const loadStats = async () => {
  const res = await getCooperationStats()
  stats.value = res.data || {}
}

const refreshAll = async () => {
  try {
    await loadStats()
    ElMessage.success('数据已更新')
  } catch (e) {
    ElMessage.error('刷新失败')
  }
}

const handleProjectsLoaded = (list) => {
  allProjects.value = list || []
}

onMounted(loadStats)
</script>

<style scoped>
.cooperation-page{padding:20px;min-height:100%;background:radial-gradient(circle at 10% 20%,#dbeafe 0,#f8fafc 45%,#eef2ff 100%)}
.hero{display:flex;justify-content:space-between;align-items:center;padding:24px;border-radius:18px;background:linear-gradient(120deg,#0ea5e9,#6366f1);color:#fff;box-shadow:0 12px 30px rgba(79,70,229,.25)}
.hero h1{margin:0 0 8px;font-size:30px}
.hero p{margin:0;opacity:.95}
.stat-row{margin:16px 0}
.stat-card{padding:16px 18px;border-radius:14px;background:rgba(255,255,255,.75);backdrop-filter:blur(6px);box-shadow:0 8px 24px rgba(30,41,59,.08)}
.stat-card .label{color:#64748b;font-size:13px}
.stat-card .value{font-weight:700;font-size:30px;color:#0f172a;line-height:1.3}
.tabs-card{border-radius:16px;background:rgba(255,255,255,.85)}
</style>
