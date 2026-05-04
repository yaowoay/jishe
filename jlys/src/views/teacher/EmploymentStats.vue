<template>
  <div class="teacher-employment-stats">
    <!-- 筛选器 -->
    <el-card class="filter-card">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="8" :md="6">
          <el-select v-model="filterForm.collegeId" placeholder="选择学院" clearable style="width: 100%">
            <el-option label="全部学院" :value="null" />
            <el-option label="计算机与信息工程学院" :value="1" />
            <el-option label="电子工程学院" :value="2" />
            <el-option label="机械工程学院" :value="3" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="8" :md="6">
          <el-select v-model="filterForm.majorId" placeholder="选择专业" clearable style="width: 100%">
            <el-option label="全部专业" :value="null" />
            <el-option label="计算机科学与技术" :value="1" />
            <el-option label="软件工程" :value="2" />
            <el-option label="电子信息工程" :value="3" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="8" :md="4">
          <el-button type="primary" @click="loadStats" :loading="loading" style="width: 100%">
            查询
          </el-button>
        </el-col>
        <el-col :xs="24" :sm="24" :md="8" style="text-align: right">
          <el-button @click="exportData">
            <el-icon><Download /></el-icon>
            导出报表
          </el-button>
          <el-button @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 核心指标卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :xs="12" :sm="6" :md="6">
        <div class="stat-card" style="border-left-color: #409EFF">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalStudents || 0 }}</div>
            <div class="stat-label">学生总数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6">
        <div class="stat-card" style="border-left-color: #67C23A">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <el-icon><SuccessFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.employedCount || 0 }}</div>
            <div class="stat-label">已就业</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6">
        <div class="stat-card" style="border-left-color: #E6A23C">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.employmentRate?.toFixed(1) || 0 }}%</div>
            <div class="stat-label">就业率</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6" :md="6">
        <div class="stat-card" style="border-left-color: #F56C6C">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.unemployedCount || 0 }}</div>
            <div class="stat-label">待就业</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 就业状态分布 -->
      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <el-icon><PieChart /></el-icon>
                就业状态分布
              </span>
            </div>
          </template>
          <div ref="statusChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>

      <!-- 薪资分布 -->
      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <el-icon><Histogram /></el-icon>
                薪资分布
              </span>
            </div>
          </template>
          <div ref="salaryChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>

      <!-- 行业分布 -->
      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <el-icon><DataAnalysis /></el-icon>
                行业分布
              </span>
            </div>
          </template>
          <div ref="industryChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行图表 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 就业趋势 -->
      <el-col :xs="24" :md="16">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <el-icon><TrendCharts /></el-icon>
                月度就业趋势
              </span>
            </div>
          </template>
          <div ref="trendChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>

      <!-- 地区分布 -->
      <el-col :xs="24" :md="8">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                <el-icon><Location /></el-icon>
                就业城市 TOP10
              </span>
            </div>
          </template>
          <div ref="cityChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User, SuccessFilled, TrendCharts, Warning, Download, Refresh,
  PieChart, Histogram, DataAnalysis, Location
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getEmploymentStats } from '@/api/teacher'

const loading = ref(false)
const statusChartRef = ref(null)
const salaryChartRef = ref(null)
const industryChartRef = ref(null)
const cityChartRef = ref(null)
const trendChartRef = ref(null)

const filterForm = ref({
  collegeId: null,
  majorId: null
})

const stats = ref({
  totalStudents: 0,
  employedCount: 0,
  furtherStudyCount: 0,
  unemployedCount: 0,
  employmentRate: 0
})

// 就业状态分布饼图
const initStatusChart = () => {
  const chart = echarts.init(statusChartRef.value)
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
          fontSize: 20,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { value: stats.value.employedCount, name: '已就业', itemStyle: { color: '#67C23A' } },
        { value: stats.value.furtherStudyCount, name: '升学', itemStyle: { color: '#409EFF' } },
        { value: stats.value.unemployedCount, name: '待就业', itemStyle: { color: '#E6A23C' } }
      ]
    }]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

// 薪资分布柱状图
const initSalaryChart = () => {
  const chart = echarts.init(salaryChartRef.value)
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
      data: ['3k以下', '3-5k', '5-8k', '8-12k', '12-15k', '15k+'],
      axisLabel: {
        rotate: 30
      }
    },
    yAxis: {
      type: 'value',
      name: '人数'
    },
    series: [{
      data: [8, 35, 68, 95, 42, 18],
      type: 'bar',
      barWidth: '60%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ])
      },
      emphasis: {
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#2378f7' },
            { offset: 0.7, color: '#2378f7' },
            { offset: 1, color: '#83bff6' }
          ])
        }
      }
    }]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

// 行业分布饼图
const initIndustryChart = () => {
  const chart = echarts.init(industryChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      bottom: '5%',
      left: 'center',
      textStyle: {
        fontSize: 12
      }
    },
    series: [{
      name: '行业分布',
      type: 'pie',
      radius: '65%',
      center: ['50%', '45%'],
      data: [
        { value: 125, name: 'IT互联网', itemStyle: { color: '#5470c6' } },
        { value: 68, name: '制造业', itemStyle: { color: '#91cc75' } },
        { value: 42, name: '金融', itemStyle: { color: '#fac858' } },
        { value: 35, name: '教育', itemStyle: { color: '#ee6666' } },
        { value: 28, name: '医疗', itemStyle: { color: '#73c0de' } },
        { value: 22, name: '其他', itemStyle: { color: '#3ba272' } }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

// 城市分布横向柱状图
const initCityChart = () => {
  const chart = echarts.init(cityChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '15%',
      right: '10%',
      bottom: '3%',
      top: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      name: '人数'
    },
    yAxis: {
      type: 'category',
      data: ['其他', '成都', '广州', '杭州', '深圳', '上海', '北京', '郑州'].reverse(),
      axisLabel: {
        fontSize: 12
      }
    },
    series: [{
      data: [12, 18, 25, 32, 38, 45, 52, 98].reverse(),
      type: 'bar',
      barWidth: '50%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#1890ff' },
          { offset: 1, color: '#36cfc9' }
        ]),
        borderRadius: [0, 5, 5, 0]
      },
      label: {
        show: true,
        position: 'right',
        formatter: '{c}人'
      }
    }]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

// 就业趋势折线图
const initTrendChart = () => {
  const chart = echarts.init(trendChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['已就业', '升学', '待就业'],
      bottom: '5%'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月']
    },
    yAxis: {
      type: 'value',
      name: '人数'
    },
    series: [
      {
        name: '已就业',
        type: 'line',
        smooth: true,
        data: [15, 32, 58, 95, 142, 198, 245, 278, 295],
        itemStyle: { color: '#67C23A' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        }
      },
      {
        name: '升学',
        type: 'line',
        smooth: true,
        data: [8, 15, 22, 28, 35, 42, 48, 52, 55],
        itemStyle: { color: '#409EFF' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        }
      },
      {
        name: '待就业',
        type: 'line',
        smooth: true,
        data: [277, 253, 220, 177, 123, 60, 7, 0, 0],
        itemStyle: { color: '#E6A23C' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(230, 162, 60, 0.3)' },
            { offset: 1, color: 'rgba(230, 162, 60, 0.05)' }
          ])
        }
      }
    ]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

const loadStats = async () => {
  loading.value = true
  try {
    const response = await getEmploymentStats({
      collegeId: filterForm.value.collegeId,
      majorId: filterForm.value.majorId
    })
    if (response.success && response.data) {
      stats.value = response.data

      // 重新初始化图表
      await nextTick()
      initStatusChart()
      initSalaryChart()
      initIndustryChart()
      initCityChart()
      initTrendChart()
    }
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  loadStats()
}

const exportData = () => {
  ElMessage.info('导出功能开发中...')
}

onMounted(async () => {
  await loadStats()
})
</script>

<style scoped lang="scss">
.teacher-employment-stats {
  .filter-card {
    margin-bottom: 20px;
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }

  .stats-cards {
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
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
      }

      .card-header {
        .header-title {
          display: flex;
          align-items: center;
          gap: 8px;
          font-weight: 600;
          font-size: 16px;
          color: #303133;
        }
      }
    }
  }
}
</style>
