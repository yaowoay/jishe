<template>
  <div class="recruitment-overview">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="title-section">
        <h1>
          <el-icon><DataBoard /></el-icon>
          招聘概览大屏
        </h1>
        <p class="subtitle">实时监控招聘数据，全面掌握招聘动态</p>
      </div>
      <div class="action-section">
        <el-button type="primary" @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-button @click="exportReport">
          <el-icon><Download /></el-icon>
          导出报告
        </el-button>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-grid">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="metric-card total-jobs">
            <div class="metric-content">
              <div class="metric-icon">
                <el-icon><Briefcase /></el-icon>
              </div>
              <div class="metric-info">
                <div class="metric-number">{{ metrics.totalJobs }}</div>
                <div class="metric-label">总职位数</div>
                <div class="metric-change positive">
                  <el-icon><ArrowUp /></el-icon>
                  +{{ metrics.jobsGrowth }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="metric-card active-companies">
            <div class="metric-content">
              <div class="metric-icon">
                <el-icon><OfficeBuilding /></el-icon>
              </div>
              <div class="metric-info">
                <div class="metric-number">{{ metrics.activeCompanies }}</div>
                <div class="metric-label">活跃企业</div>
                <div class="metric-change positive">
                  <el-icon><ArrowUp /></el-icon>
                  +{{ metrics.companiesGrowth }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="metric-card avg-salary">
            <div class="metric-content">
              <div class="metric-icon">
                <el-icon><Money /></el-icon>
              </div>
              <div class="metric-info">
                <div class="metric-number">{{ metrics.avgSalary }}K</div>
                <div class="metric-label">平均薪资</div>
                <div class="metric-change positive">
                  <el-icon><ArrowUp /></el-icon>
                  +{{ metrics.salaryGrowth }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="metric-card hot-positions">
            <div class="metric-content">
              <div class="metric-icon">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="metric-info">
                <div class="metric-number">{{ metrics.hotPositions }}</div>
                <div class="metric-label">热门职位</div>
                <div class="metric-change positive">
                  <el-icon><ArrowUp /></el-icon>
                  +{{ metrics.hotGrowth }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <!-- 招聘趋势图 -->
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>招聘趋势分析</span>
                <el-select v-model="trendPeriod" size="small" style="width: 120px;">
                  <el-option label="近7天" value="7d"></el-option>
                  <el-option label="近30天" value="30d"></el-option>
                  <el-option label="近90天" value="90d"></el-option>
                </el-select>
              </div>
            </template>
            <div ref="trendChart" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 职位分布图 -->
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>职位类别分布</span>
            </template>
            <div ref="categoryChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <!-- 薪资分布图 -->
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>薪资分布情况</span>
            </template>
            <div ref="salaryChart" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 地区分布图 -->
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>地区分布热力图</span>
            </template>
            <div ref="regionChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 实时数据流 -->
    <div class="realtime-section">
      <el-card class="realtime-card">
        <template #header>
          <div class="realtime-header">
            <span>实时招聘动态</span>
            <el-tag type="success" effect="dark">
              <el-icon><CircleCheck /></el-icon>
              实时更新
            </el-tag>
          </div>
        </template>
        <div class="realtime-content">
          <div class="realtime-item" v-for="item in realtimeData" :key="item.id">
            <div class="item-time">{{ item.time }}</div>
            <div class="item-content">
              <el-tag :type="item.type" size="small">{{ item.action }}</el-tag>
              <span class="item-text">{{ item.content }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'  
import { ElMessage } from 'element-plus'  
import {
  DataBoard, Refresh, Download, Briefcase, OfficeBuilding,
  Money, TrendCharts, ArrowUp, CircleCheck
} from '@element-plus/icons-vue'  
import * as echarts from 'echarts'  

// 响应式数据
const loading = ref(false)  
const trendPeriod = ref('30d')  

// 核心指标数据
const metrics = reactive({
  totalJobs: 15420,
  jobsGrowth: 12.5,
  activeCompanies: 3280,
  companiesGrowth: 8.3,
  avgSalary: 18.5,
  salaryGrowth: 5.2,
  hotPositions: 156,
  hotGrowth: 15.8
})  

// 实时数据
const realtimeData = ref([
  { id: 1, time: '10:30:25', action: '新增', type: 'success', content: '阿里巴巴发布Java高级工程师职位' },
  { id: 2, time: '10:28:15', action: '更新', type: 'warning', content: '腾讯更新前端开发工程师薪资范围' },
  { id: 3, time: '10:25:42', action: '热门', type: 'danger', content: '字节跳动算法工程师职位浏览量突破1000' },
  { id: 4, time: '10:23:18', action: '新增', type: 'success', content: '美团发布产品经理职位' },
  { id: 5, time: '10:20:33', action: '更新', type: 'warning', content: '滴滴更新数据分析师职位要求' }
])  

// 图表引用
const trendChart = ref()  
const categoryChart = ref()  
const salaryChart = ref()  
const regionChart = ref()  

// 图表实例
let trendChartInstance = null  
let categoryChartInstance = null  
let salaryChartInstance = null  
let regionChartInstance = null  

// 定时器
let realtimeTimer = null  

// 方法定义
const refreshData = async () => {
  loading.value = true  
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))  
    
    // 更新指标数据
    metrics.totalJobs = Math.floor(Math.random() * 1000) + 15000  
    metrics.activeCompanies = Math.floor(Math.random() * 500) + 3000  
    metrics.avgSalary = (Math.random() * 5 + 16).toFixed(1)  
    metrics.hotPositions = Math.floor(Math.random() * 50) + 130  
    
    // 重新渲染图表
    initCharts()  
    
    ElMessage.success('数据刷新成功')  
  } catch (error) {
    ElMessage.error('数据刷新失败')  
  } finally {
    loading.value = false  
  }
}  

const exportReport = () => {
  ElMessage.success('报告导出功能开发中...')  
}  

// 初始化图表
const initCharts = () => {
  nextTick(() => {
    initTrendChart()  
    initCategoryChart()  
    initSalaryChart()  
    initRegionChart()  
  })  
}  

const initTrendChart = () => {
  if (trendChartInstance) {
    trendChartInstance.dispose()  
  }
  
  trendChartInstance = echarts.init(trendChart.value)  
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['职位发布', '简历投递', '面试邀请'] },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '职位发布',
        type: 'line',
        data: [1200, 1320, 1010, 1340, 1290, 1330, 1320, 1450, 1380, 1420, 1520, 1680],
        smooth: true,
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '简历投递',
        type: 'line',
        data: [2200, 1820, 1910, 2340, 2290, 2330, 2320, 2450, 2380, 2420, 2520, 2680],
        smooth: true,
        itemStyle: { color: '#67C23A' }
      },
      {
        name: '面试邀请',
        type: 'line',
        data: [150, 232, 201, 154, 190, 330, 410, 320, 380, 420, 520, 680],
        smooth: true,
        itemStyle: { color: '#E6A23C' }
      }
    ]
  }  
  trendChartInstance.setOption(option)  
}  

const initCategoryChart = () => {
  if (categoryChartInstance) {
    categoryChartInstance.dispose()  
  }
  
  categoryChartInstance = echarts.init(categoryChart.value)  
  const option = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 1048, name: '技术开发' },
        { value: 735, name: '产品运营' },
        { value: 580, name: '市场销售' },
        { value: 484, name: '设计创意' },
        { value: 300, name: '职能管理' }
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
  categoryChartInstance.setOption(option)  
}  

const initSalaryChart = () => {
  if (salaryChartInstance) {
    salaryChartInstance.dispose()  
  }
  
  salaryChartInstance = echarts.init(salaryChart.value)  
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['5K以下', '5-10K', '10-15K', '15-20K', '20-30K', '30K以上']
    },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: [120, 450, 680, 520, 380, 150],
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ])
      }
    }]
  }  
  salaryChartInstance.setOption(option)  
}  

const initRegionChart = () => {
  if (regionChartInstance) {
    regionChartInstance.dispose()  
  }
  
  regionChartInstance = echarts.init(regionChart.value)  
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['北京', '上海', '深圳', '杭州', '广州', '成都', '南京', '武汉']
    },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: [2340, 1890, 1650, 1200, 980, 760, 650, 520],
      itemStyle: { color: '#67C23A' }
    }]
  }  
  regionChartInstance.setOption(option)  
}  

// 启动实时数据更新
const startRealtimeUpdate = () => {
  realtimeTimer = setInterval(() => {
    const newItem = {
      id: Date.now(),
      time: new Date().toLocaleTimeString(),
      action: ['新增', '更新', '热门'][Math.floor(Math.random() * 3)],
      type: ['success', 'warning', 'danger'][Math.floor(Math.random() * 3)],
      content: `${['阿里巴巴', '腾讯', '字节跳动', '美团', '滴滴'][Math.floor(Math.random() * 5)]}发布新职位`
    }  
    
    realtimeData.value.unshift(newItem)  
    if (realtimeData.value.length > 10) {
      realtimeData.value.pop()  
    }
  }, 5000)  
}  

// 组件挂载
onMounted(() => {
  initCharts()  
  startRealtimeUpdate()  
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    trendChartInstance?.resize()  
    categoryChartInstance?.resize()  
    salaryChartInstance?.resize()  
    regionChartInstance?.resize()  
  })  
})  

// 组件卸载
onUnmounted(() => {
  if (realtimeTimer) {
    clearInterval(realtimeTimer)  
  }
  
  trendChartInstance?.dispose()  
  categoryChartInstance?.dispose()  
  salaryChartInstance?.dispose()  
  regionChartInstance?.dispose()  
  
  window.removeEventListener('resize', () => {})  
})  
</script>

<style scoped>
.recruitment-overview {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.title-section h1 {
  margin: 0;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 28px;
}

.subtitle {
  margin: 10px 0 0 0;
  color: #606266;
  font-size: 14px;
}

.action-section {
  display: flex;
  gap: 10px;
}

.metrics-grid {
  margin-bottom: 30px;
}

.metric-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.metric-card:hover {
  transform: translateY(-5px);
}

.metric-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.metric-icon {
  font-size: 40px;
  margin-right: 20px;
  padding: 15px;
  border-radius: 50%;
}

.total-jobs .metric-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.active-companies .metric-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.avg-salary .metric-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.hot-positions .metric-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.metric-number {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.metric-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.metric-change {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.metric-change.positive {
  color: #67C23A;
}

.charts-section {
  margin-bottom: 30px;
}

.chart-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.realtime-section {
  margin-bottom: 20px;
}

.realtime-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.realtime-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.realtime-content {
  max-height: 300px;
  overflow-y: auto;
}

.realtime-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.realtime-item:last-child {
  border-bottom: none;
}

.item-time {
  width: 80px;
  font-size: 12px;
  color: #909399;
  margin-right: 15px;
}

.item-content {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
}

.item-text {
  font-size: 14px;
  color: #606266;
}
</style>
