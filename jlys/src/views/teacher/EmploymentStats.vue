<template>
  <div class="teacher-employment-stats">
    <el-card class="stats-overview">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #e6f7ff">
              <el-icon style="color: #1890ff"><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.totalStudents }}</div>
              <div class="stat-label">学生总数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #f6ffed">
              <el-icon style="color: #52c41a"><SuccessFilled /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.employedStudents }}</div>
              <div class="stat-label">已就业</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #fff7e6">
              <el-icon style="color: #faad14"><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.employmentRate }}%</div>
              <div class="stat-label">就业率</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-item">
            <div class="stat-icon" style="background-color: #fff1f0">
              <el-icon style="color: #ff4d4f"><Warning /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.pendingStudents }}</div>
              <div class="stat-label">待就业</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>薪资分布</span>
            </div>
          </template>
          <div ref="salaryChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>行业分布</span>
            </div>
          </template>
          <div ref="industryChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>地区分布</span>
            </div>
          </template>
          <div ref="cityChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>就业趋势</span>
            </div>
          </template>
          <div ref="trendChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { User, SuccessFilled, TrendCharts, Warning } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getEmploymentStats } from '@/api/teacher'

const salaryChartRef = ref(null)
const industryChartRef = ref(null)
const cityChartRef = ref(null)
const trendChartRef = ref(null)

const stats = ref({
  totalStudents: 0,
  employedStudents: 0,
  employmentRate: 0,
  pendingStudents: 0
})

const initSalaryChart = () => {
  const chart = echarts.init(salaryChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    xAxis: {
      type: 'category',
      data: ['3k以下', '3k-5k', '5k-8k', '8k-12k', '12k-15k', '15k以上']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: [5, 20, 45, 60, 30, 15],
      type: 'bar',
      itemStyle: {
        color: '#409eff'
      }
    }]
  }
  chart.setOption(option)
}

const initIndustryChart = () => {
  const chart = echarts.init(industryChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [{
      type: 'pie',
      radius: '50%',
      data: [
        { value: 50, name: 'IT互联网' },
        { value: 30, name: '制造业' },
        { value: 20, name: '金融' },
        { value: 15, name: '教育' },
        { value: 10, name: '其他' }
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
}

const initCityChart = () => {
  const chart = echarts.init(cityChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    xAxis: {
      type: 'category',
      data: ['郑州', '北京', '上海', '深圳', '杭州', '其他']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: [80, 30, 25, 20, 15, 5],
      type: 'bar',
      itemStyle: {
        color: '#67c23a'
      }
    }]
  }
  chart.setOption(option)
}

const initTrendChart = () => {
  const chart = echarts.init(trendChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: [10, 25, 45, 70, 90, 95],
      type: 'line',
      smooth: true,
      itemStyle: {
        color: '#409eff'
      },
      areaStyle: {
        color: 'rgba(64, 158, 255, 0.2)'
      }
    }]
  }
  chart.setOption(option)
}

const loadStats = async () => {
  try {
    const response = await getEmploymentStats()
    if (response.success) {
      stats.value = {
        totalStudents: response.data.totalStudents || 0,
        employedStudents: response.data.employedStudents || 0,
        employmentRate: response.data.totalStudents > 0 
          ? ((response.data.employedStudents / response.data.totalStudents) * 100).toFixed(2)
          : 0,
        pendingStudents: response.data.pendingStudents || 0
      }
    }
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

onMounted(async () => {
  await loadStats()
  await nextTick()
  initSalaryChart()
  initIndustryChart()
  initCityChart()
  initTrendChart()
})
</script>

<style scoped lang="scss">
.teacher-employment-stats {
  .stats-overview {
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

    .stat-item {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      background-color: #fff;
      border-radius: 8px;

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
      }

      .stat-content {
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

  .chart-card {
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

    .card-header {
      font-size: 16px;
      font-weight: 600;
    }
  }
}
</style>
