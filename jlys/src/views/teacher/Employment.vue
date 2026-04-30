<template>
  <div class="teacher-employment">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6" :md="6">
        <div class="stat-card" style="border-left-color: #409EFF">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">总记录数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6">
        <div class="stat-card" style="border-left-color: #E6A23C">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pending }}</div>
            <div class="stat-label">待审核</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6">
        <div class="stat-card" style="border-left-color: #67C23A">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.approved }}</div>
            <div class="stat-label">已通过</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6">
        <div class="stat-card" style="border-left-color: #F56C6C">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
            <el-icon><CircleClose /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.rejected }}</div>
            <div class="stat-label">已拒绝</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <el-icon><PieChart /></el-icon>
                就业状态分布
              </span>
            </div>
          </template>
          <div ref="statusChartRef" style="height: 280px"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <el-icon><Histogram /></el-icon>
                审核状态统计
              </span>
            </div>
          </template>
          <div ref="auditChartRef" style="height: 280px"></div>
        </el-card>
      </el-col>
    </el-row>

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
              v-model="searchForm.employmentStatus"
              placeholder="就业状态"
              clearable
              style="width: 100%"
          >
            <el-option label="全部" :value="null" />
            <el-option label="已就业" value="已就业" />
            <el-option label="待就业" value="待就业" />
            <el-option label="求职中" value="求职中" />
            <el-option label="升学" value="升学" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-select
              v-model="searchForm.verifyStatus"
              placeholder="审核状态"
              clearable
              style="width: 100%"
          >
            <el-option label="全部" :value="null" />
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 100%"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="3">
          <el-button type="primary" @click="handleSearch" :loading="loading" style="width: 100%">
            查询
          </el-button>
        </el-col>
        <el-col :xs="24" :sm="12" :md="4">
          <el-button @click="exportData" style="width: 100%">
            <el-icon><Download /></el-icon>
            导出数据
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <el-table
          :data="employmentList"
          stripe
          style="width: 100%"
          :fit="true"
          :loading="loading"
          v-loading="loading"
      >
        <el-table-column prop="studentId" label="学生ID" min-width="120" />
        <el-table-column prop="employmentStatus" label="就业状态" min-width="140">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.employmentStatus)">
              {{ row.employmentStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="companyName" label="公司" min-width="260" />
        <el-table-column prop="position" label="职位" min-width="220" />
        <el-table-column prop="salaryRange" label="薪资" min-width="160" />
        <el-table-column prop="employmentCity" label="城市" min-width="160" />
        <el-table-column prop="verifyStatus" label="审核状态" min-width="140">
          <template #default="{ row }">
            <el-tag :type="getVerifyType(row.verifyStatus)">
              {{ row.verifyStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="180" fixed="right">
          <template #default="{ row }">
            <el-button
                v-if="row.verifyStatus === 'pending'"
                type="success"
                size="small"
                @click="auditEmployment(row, 'approved')"
            >
              通过
            </el-button>
            <el-button
                v-if="row.verifyStatus === 'pending'"
                type="danger"
                size="small"
                @click="auditEmployment(row, 'rejected')"
            >
              拒绝
            </el-button>
            <el-button v-else type="info" size="small" disabled>
              已审核
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, Document, Clock, CircleCheck, CircleClose,
  PieChart, Histogram, Download
} from '@element-plus/icons-vue'
import { getEmploymentList, auditEmployment as auditEmploymentApi } from '@/api/teacher'
import * as echarts from 'echarts'

const loading = ref(false)
const employmentList = ref([])
const statusChartRef = ref(null)
const auditChartRef = ref(null)

const searchForm = ref({
  keyword: '',
  verifyStatus: null,
  employmentStatus: null,
  dateRange: null
})

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const stats = computed(() => {
  return {
    total: employmentList.value.length,
    pending: employmentList.value.filter(e => e.verifyStatus === 'pending').length,
    approved: employmentList.value.filter(e => e.verifyStatus === 'approved').length,
    rejected: employmentList.value.filter(e => e.verifyStatus === 'rejected').length
  }
})

// 初始化就业状态图表
const initStatusChart = () => {
  const chart = echarts.init(statusChartRef.value)

  const statusCount = {
    '已就业': employmentList.value.filter(e => e.employmentStatus === '已就业').length,
    '待就业': employmentList.value.filter(e => e.employmentStatus === '待就业').length,
    '求职中': employmentList.value.filter(e => e.employmentStatus === '求职中').length,
    '升学': employmentList.value.filter(e => e.employmentStatus === '升学').length
  }

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      bottom: '5%',
      left: 'center'
    },
    series: [{
      name: '就业状态',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 18,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { value: statusCount['已就业'], name: '已就业', itemStyle: { color: '#67C23A' } },
        { value: statusCount['待就业'], name: '待就业', itemStyle: { color: '#E6A23C' } },
        { value: statusCount['求职中'], name: '求职中', itemStyle: { color: '#409EFF' } },
        { value: statusCount['升学'], name: '升学', itemStyle: { color: '#909399' } }
      ]
    }]
  }

  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

// 初始化审核状态图表
const initAuditChart = () => {
  const chart = echarts.init(auditChartRef.value)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['待审核', '已通过', '已拒绝']
    },
    yAxis: {
      type: 'value',
      name: '数量'
    },
    series: [{
      data: [
        { value: stats.value.pending, itemStyle: { color: '#E6A23C' } },
        { value: stats.value.approved, itemStyle: { color: '#67C23A' } },
        { value: stats.value.rejected, itemStyle: { color: '#F56C6C' } }
      ],
      type: 'bar',
      barWidth: '50%',
      label: {
        show: true,
        position: 'top',
        formatter: '{c}'
      }
    }]
  }

  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

const handleSearch = async () => {
  loading.value = true
  try {
    const response = await getEmploymentList()
    if (response.success) {
      let data = response.data || []

      // 前端筛选
      if (searchForm.value.keyword) {
        data = data.filter(e =>
          e.studentName?.includes(searchForm.value.keyword) ||
          e.studentNo?.includes(searchForm.value.keyword)
        )
      }
      if (searchForm.value.employmentStatus) {
        data = data.filter(e => e.employmentStatus === searchForm.value.employmentStatus)
      }
      if (searchForm.value.verifyStatus) {
        data = data.filter(e => e.verifyStatus === searchForm.value.verifyStatus)
      }

      employmentList.value = data
      pagination.value.total = data.length

      // 更新图表
      await nextTick()
      initStatusChart()
      initAuditChart()
    }
  } catch (error) {
    ElMessage.error('查询就业台账失败')
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const typeMap = {
    '已就业': 'success',
    '待就业': 'warning',
    '求职中': 'primary',
    '升学': 'info'
  }
  return typeMap[status] || 'info'
}

const getVerifyType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  }
  return typeMap[status] || 'info'
}

const auditEmployment = async (row, status) => {
  const action = status === 'approved' ? '通过' : '拒绝'

  try {
    await ElMessageBox.confirm(
      `确定要${action}该就业记录吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: status === 'approved' ? 'success' : 'warning'
      }
    )

    const response = await auditEmploymentApi({
      ledgerId: row.ledgerId,
      verifyStatus: status,
      remark: `${action}审核`
    })

    if (response.success) {
      ElMessage.success(`${action}成功`)
      handleSearch()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const exportData = () => {
  ElMessage.info('导出功能开发中...')
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
.teacher-employment {
  width: 100%;

  .stats-row {
    margin-bottom: 20px;

    .stat-card {
      background: white;
      border-radius: 8px;
      padding: 20px;
      display: flex;
      align-items: center;
      gap: 16px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      border-left: 4px solid;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
      }

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        color: white;
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: #303133;
          line-height: 1.2;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }

  .chart-row {
    margin-bottom: 20px;

    .chart-card {
      border: none;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

      .card-header .header-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-weight: 600;
        font-size: 16px;
      }
    }
  }

  .search-card {
    margin-bottom: 20px;
    border: none;
    box-shadow: 0 2px 12px rgba(38, 84, 124, 0.1);

    :deep(.el-card__body) {
      padding: 18px 20px;
    }
  }

  .table-card {
    width: 100%;
    border: none;
    box-shadow: 0 2px 12px rgba(38, 84, 124, 0.1);

    :deep(.el-card__body) {
      padding: 0 0 16px;
    }

    :deep(.el-table th.el-table__cell) {
      background: linear-gradient(135deg, #eef8ff 0%, #e6f6f3 100%);
      color: #2f5f7f;
      font-weight: 600;
    }

    :deep(.el-table__row:hover > td.el-table__cell) {
      background: #f3fbff;
    }
  }
}
</style>
