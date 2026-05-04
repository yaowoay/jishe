<template>
  <div class="recommendation-analytics">
    <el-card class="analytics-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>推荐效果分析</span>
          <div class="time-selector">
            <el-select v-model="selectedTimeRange" @change="loadAnalytics" placeholder="选择时间范围">
              <el-option label="最近7天" value="7d"></el-option>
              <el-option label="最近30天" value="30d"></el-option>
              <el-option label="最近90天" value="90d"></el-option>
            </el-select>
            <el-button type="primary" @click="refreshAnalytics" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <!-- 核心指标 -->
      <div class="metrics-overview">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="metric-card">
              <div class="metric-value">{{ analytics.totalRecommendations }}</div>
              <div class="metric-label">总推荐数</div>
              <div class="metric-trend" :class="getTrendClass(analytics.recommendationsTrend)">
                {{ formatTrend(analytics.recommendationsTrend) }}
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="metric-card">
              <div class="metric-value">{{ analytics.clickRate }}%</div>
              <div class="metric-label">点击率</div>
              <div class="metric-trend" :class="getTrendClass(analytics.clickRateTrend)">
                {{ formatTrend(analytics.clickRateTrend) }}
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="metric-card">
              <div class="metric-value">{{ analytics.conversionRate }}%</div>
              <div class="metric-label">转化率</div>
              <div class="metric-trend" :class="getTrendClass(analytics.conversionRateTrend)">
                {{ formatTrend(analytics.conversionRateTrend) }}
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="metric-card">
              <div class="metric-value">{{ analytics.avgMatchScore }}%</div>
              <div class="metric-label">平均匹配度</div>
              <div class="metric-trend" :class="getTrendClass(analytics.matchScoreTrend)">
                {{ formatTrend(analytics.matchScoreTrend) }}
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 算法性能对比 -->
      <div class="algorithm-comparison">
        <h3>算法性能对比</h3>
        <div class="chart-container">
          <div ref="algorithmChart" style="width: 100%; height: 300px;"></div>
        </div>
      </div>

      <!-- 推荐趋势图 -->
      <div class="trend-analysis">
        <h3>推荐趋势分析</h3>
        <div class="chart-container">
          <div ref="trendChart" style="width: 100%; height: 300px;"></div>
        </div>
      </div>

      <!-- 用户行为热力图 -->
      <div class="behavior-heatmap">
        <h3>用户行为热力图</h3>
        <div class="heatmap-container">
          <div class="heatmap-legend">
            <span>活跃度：</span>
            <div class="legend-item low">低</div>
            <div class="legend-item medium">中</div>
            <div class="legend-item high">高</div>
          </div>
          <div class="heatmap-grid">
            <div class="heatmap-row" v-for="(row, rowIndex) in heatmapData" :key="rowIndex">
              <div class="heatmap-label">{{ getHourLabel(rowIndex) }}</div>
              <div 
                class="heatmap-cell" 
                v-for="(value, colIndex) in row" 
                :key="colIndex"
                :class="getHeatmapCellClass(value)"
                :title="`${getDayLabel(colIndex)} ${getHourLabel(rowIndex)}: ${value}次活动`"
              ></div>
            </div>
            <div class="heatmap-days">
              <div class="day-label" v-for="(day, index) in dayLabels" :key="index">
                {{ day }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 推荐质量分析 -->
      <div class="quality-analysis">
        <h3>推荐质量分析</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="quality-metrics">
              <h4>质量指标</h4>
              <el-progress 
                :percentage="analytics.relevanceScore" 
                :color="getProgressColor(analytics.relevanceScore)"
                :format="() => `相关性: ${analytics.relevanceScore}%`"
              />
              <el-progress 
                :percentage="analytics.diversityScore" 
                :color="getProgressColor(analytics.diversityScore)"
                :format="() => `多样性: ${analytics.diversityScore}%`"
              />
              <el-progress 
                :percentage="analytics.noveltyScore" 
                :color="getProgressColor(analytics.noveltyScore)"
                :format="() => `新颖性: ${analytics.noveltyScore}%`"
              />
              <el-progress 
                :percentage="analytics.satisfactionScore" 
                :color="getProgressColor(analytics.satisfactionScore)"
                :format="() => `满意度: ${analytics.satisfactionScore}%`"
              />
            </div>
          </el-col>
          <el-col :span="12">
            <div class="feedback-summary">
              <h4>用户反馈</h4>
              <div class="feedback-item">
                <span>👍 正面反馈</span>
                <el-tag type="success">{{ analytics.positiveFeedback }}</el-tag>
              </div>
              <div class="feedback-item">
                <span>👎 负面反馈</span>
                <el-tag type="danger">{{ analytics.negativeFeedback }}</el-tag>
              </div>
              <div class="feedback-item">
                <span>💬 总反馈数</span>
                <el-tag type="info">{{ analytics.totalFeedback }}</el-tag>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- A/B测试结果 -->
      <div class="ab-test-results" v-if="analytics.abTestResults">
        <h3>A/B测试结果</h3>
        <el-table :data="analytics.abTestResults" style="width: 100%">
          <el-table-column prop="variant" label="测试变体" width="120"></el-table-column>
          <el-table-column prop="users" label="用户数" width="100"></el-table-column>
          <el-table-column prop="clickRate" label="点击率" width="100">
            <template #default="scope">
              {{ scope.row.clickRate }}%
            </template>
          </el-table-column>
          <el-table-column prop="conversionRate" label="转化率" width="100">
            <template #default="scope">
              {{ scope.row.conversionRate }}%
            </template>
          </el-table-column>
          <el-table-column prop="confidence" label="置信度" width="100">
            <template #default="scope">
              {{ scope.row.confidence }}%
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'          
import { ElMessage } from 'element-plus'          
import { Refresh } from '@element-plus/icons-vue'          
import * as echarts from 'echarts'          

// 响应式数据
const loading = ref(false)          
const selectedTimeRange = ref('7d')          
const algorithmChart = ref()          
const trendChart = ref()          

// 分析数据
const analytics = reactive({
  totalRecommendations: 1250,
  recommendationsTrend: 12.5,
  clickRate: 8.3,
  clickRateTrend: 2.1,
  conversionRate: 3.2,
  conversionRateTrend: -0.5,
  avgMatchScore: 78,
  matchScoreTrend: 5.2,
  relevanceScore: 85,
  diversityScore: 72,
  noveltyScore: 68,
  satisfactionScore: 81,
  positiveFeedback: 156,
  negativeFeedback: 23,
  totalFeedback: 179,
  abTestResults: [
    { variant: 'A (原版)', users: 500, clickRate: 7.8, conversionRate: 2.9, confidence: 95, status: '对照组' },
    { variant: 'B (优化版)', users: 500, clickRate: 8.8, conversionRate: 3.5, confidence: 98, status: '获胜' }
  ]
})          

// 热力图数据
const heatmapData = ref([])          
const dayLabels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']          

// 方法定义
const loadAnalytics = async () => {
  loading.value = true          
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))          
    
    // 生成热力图数据
    generateHeatmapData()          
    
    // 渲染图表
    await nextTick()          
    renderCharts()          
    
    ElMessage.success('分析数据加载完成')          
  } catch (error) {
    ElMessage.error('加载分析数据失败')          
  } finally {
    loading.value = false          
  }
}          

const refreshAnalytics = () => {
  loadAnalytics()          
}          

const generateHeatmapData = () => {
  heatmapData.value = []          
  for (let hour = 0 ;hour < 24; hour++) {
    const row = []          
    for (let day = 0;day < 7 ; day++) {
      // 模拟活跃度数据，工作时间更活跃
      let activity = Math.random() * 10          
      if (hour >= 9 && hour <= 18 && day < 5) {
        activity += Math.random() * 20          
      }
      row.push(Math.floor(activity))          
    }
    heatmapData.value.push(row)          
  }
}          

const renderCharts = () => {
  renderAlgorithmChart()          
  renderTrendChart()          
}          

const renderAlgorithmChart = () => {
  const chart = echarts.init(algorithmChart.value)          
  const option = {
    title: {
      text: '算法性能对比',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['点击率', '转化率', '匹配度'],
      top: 30
    },
    xAxis: {
      type: 'category',
      data: ['协同过滤', '内容推荐', '混合推荐', '热门推荐']
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: '点击率',
        type: 'bar',
        data: [8.2, 7.5, 9.1, 6.8]
      },
      {
        name: '转化率',
        type: 'bar',
        data: [3.1, 2.8, 3.6, 2.5]
      },
      {
        name: '匹配度',
        type: 'bar',
        data: [76, 82, 85, 71]
      }
    ]
  }          
  chart.setOption(option)          
}          

const renderTrendChart = () => {
  const chart = echarts.init(trendChart.value)          
  const dates = []          
  const recommendations = []          
  const clicks = []          
  
  // 生成最近7天的数据
  for (let i = 6   ;        i >= 0       ;    i--) {
    const date = new Date()          
    date.setDate(date.getDate() - i)          
    dates.push(date.toLocaleDateString())          
    recommendations.push(Math.floor(Math.random() * 50) + 150)          
    clicks.push(Math.floor(Math.random() * 20) + 10)          
  }
  
  const option = {
    title: {
      text: '推荐趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['推荐数', '点击数'],
      top: 30
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '推荐数',
        type: 'line',
        data: recommendations,
        smooth: true
      },
      {
        name: '点击数',
        type: 'line',
        data: clicks,
        smooth: true
      }
    ]
  }          
  chart.setOption(option)          
}          

// 辅助方法
const getTrendClass = (trend) => {
  return trend > 0 ? 'trend-up' : 'trend-down'          
}          

const formatTrend = (trend) => {
  const sign = trend > 0 ? '+' : ''          
  return `${sign}${trend}%`          
}          

const getProgressColor = (value) => {
  if (value >= 80) return '#67C23A'          
  if (value >= 60) return '#E6A23C'          
  return '#F56C6C'          
}          

const getHourLabel = (hour) => {
  return `${hour.toString().padStart(2, '0')}:00`          
}          

const getDayLabel = (dayIndex) => {
  return dayLabels[dayIndex]          
}          

const getHeatmapCellClass = (value) => {
  if (value >= 20) return 'heat-high'          
  if (value >= 10) return 'heat-medium'          
  return 'heat-low'          
}          

const getStatusType = (status) => {
  if (status === '获胜') return 'success'          
  if (status === '失败') return 'danger'          
  return 'info'          
}          

// 组件挂载时加载数据
onMounted(() => {
  loadAnalytics()          
})          
</script>

<style scoped>
.recommendation-analytics {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.time-selector {
  display: flex;
  gap: 10px;
  align-items: center;
}

.metrics-overview {
  margin-bottom: 30px;
}

.metric-card {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.metric-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.metric-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}

.metric-trend {
  font-size: 12px;
  font-weight: bold;
}

.trend-up {
  color: #67C23A;
}

.trend-down {
  color: #F56C6C;
}

.algorithm-comparison,
.trend-analysis,
.behavior-heatmap,
.quality-analysis,
.ab-test-results {
  margin-top: 30px;
}

.chart-container {
  margin-top: 15px;
}

.heatmap-container {
  margin-top: 15px;
}

.heatmap-legend {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.legend-item {
  width: 20px;
  height: 20px;
  margin: 0 5px;
  border-radius: 3px;
}

.legend-item.low {
  background-color: #e8f4fd;
}

.legend-item.medium {
  background-color: #409EFF;
}

.legend-item.high {
  background-color: #1f5582;
}

.heatmap-grid {
  position: relative;
}

.heatmap-row {
  display: flex;
  align-items: center;
  margin-bottom: 2px;
}

.heatmap-label {
  width: 50px;
  font-size: 12px;
  text-align: right;
  margin-right: 10px;
}

.heatmap-cell {
  width: 20px;
  height: 20px;
  margin-right: 2px;
  border-radius: 2px;
  cursor: pointer;
}

.heat-low {
  background-color: #e8f4fd;
}

.heat-medium {
  background-color: #409EFF;
}

.heat-high {
  background-color: #1f5582;
}

.heatmap-days {
  display: flex;
  margin-left: 60px;
  margin-top: 5px;
}

.day-label {
  width: 22px;
  font-size: 12px;
  text-align: center;
}

.quality-metrics .el-progress {
  margin-bottom: 15px;
}

.feedback-summary {
  padding-left: 20px;
}

.feedback-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
</style>
