<template>
  <div class="user-behavior-dashboard">
    <div class="page-header">
      <div class="title-section">
        <h1>
          <el-icon><TrendCharts /></el-icon>
          用户行为分析仪表板
        </h1>
        <p class="subtitle">用户行为与路径分析概览</p>
      </div>
      <div class="action-section">
        <div class="time-range-selector">
          <el-select v-model="timeRange" @change="loadAllData" placeholder="选择时间范围" class="time-select" clearable>
            <el-option label="最近7天" :value="7"></el-option>
            <el-option label="最近30天" :value="30"></el-option>
            <el-option label="最近90天" :value="90"></el-option>
          </el-select>
          <el-tooltip content="选择时间范围" placement="top">
            <el-icon class="time-info-icon"><InfoFilled /></el-icon>
          </el-tooltip>
        </div>
      </div>
    </div>

    <!-- 行为统计概览 -->
    <el-row :gutter="20" class="stats-overview">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon view-icon">
              <el-icon><View /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ behaviorAnalysis.totalViews || 0 }}</div>
              <div class="stat-label">总浏览量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon search-icon">
              <el-icon><Search /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ behaviorAnalysis.totalSearches || 0 }}</div>
              <div class="stat-label">搜索次数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon apply-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ behaviorAnalysis.totalApplications || 0 }}</div>
              <div class="stat-label">申请次数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon engagement-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ Math.round((behaviorAnalysis.engagementScore || 0) * 100) }}%</div>
              <div class="stat-label">参与度</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 行为趋势图表 -->
    <el-row :gutter="20" class="charts-section">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>行为趋势</span>
            </div>
          </template>
          <div ref="behaviorTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>搜索关键词云</span>
            </div>
          </template>
          <div ref="wordCloudChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 兴趣分析和用户画像 -->
    <el-row :gutter="20" class="analysis-section">
      <el-col :span="6">
        <el-card class="analysis-card">
          <template #header>
            <div class="card-header">
              <span>兴趣标签分析</span>
            </div>
          </template>
          <div class="interest-analysis">
            <div v-for="(weight, tag) in interestTags" :key="tag" class="interest-item">
              <div class="interest-label">{{ tag }}</div>
              <el-progress 
                :percentage="Math.round(weight * 100)" 
                :color="getInterestColor(weight)"
                :show-text="true"
              />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="analysis-card">
          <template #header>
            <div class="card-header">
              <span>活跃时段分析</span>
            </div>
          </template>
          <div ref="activityHeatmap" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="analysis-card">
          <template #header>
            <div class="card-header">
              <span>用户分类</span>
            </div>
          </template>
          <div class="user-classification">
            <div class="classification-item">
              <div class="classification-label">用户类型</div>
              <el-tag :type="getUserTypeColor(behaviorAnalysis.userType)" size="large">
                {{ getUserTypeLabel(behaviorAnalysis.userType) }}
              </el-tag>
            </div>
            <div class="classification-item">
              <div class="classification-label">活跃度</div>
              <el-rate 
                v-model="activityLevel" 
                disabled 
                show-score 
                text-color="#ff9900"
              />
            </div>
            <div class="classification-item">
              <div class="classification-label">求职意向强度</div>
              <el-progress 
                :percentage="Math.round((behaviorAnalysis.jobIntentionStrength || 0) * 100)"
                :color="getIntentionColor(behaviorAnalysis.jobIntentionStrength)"
              />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="path-card">
          <template #header>
            <div class="card-header">
              <span>用户行为路径分析</span>
            </div>
          </template>
          <div class="user-path">
            <div v-for="(step, index) in userPath" :key="index" class="path-step">
              <div class="step-row">
                <div class="step-icon">
                  <el-icon v-if="step.action === 'search'"><Search /></el-icon>
                  <el-icon v-else-if="step.action === 'view'"><View /></el-icon>
                  <el-icon v-else-if="step.action === 'apply'"><Document /></el-icon>
                  <el-icon v-else-if="step.action === 'collect'"><Star /></el-icon>
                  <el-icon v-else><Operation /></el-icon>
                </div>
                <div class="step-content">
                  <div class="step-action">{{ getActionLabel(step.action) }}</div>
                  <div class="step-time">{{ formatTime(step.timestamp) }}</div>
                  <div class="step-details" v-if="step.details">{{ step.details }}</div>
                </div>
              </div>
              <!-- <div v-if="index < userPath.length - 1" class="step-arrow">
                <el-icon><ArrowDown /></el-icon>
              </div> -->
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row> 


  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { View, Search, Document, TrendCharts, Star, Operation, ArrowDown, InfoFilled } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 响应式数据
const timeRange = ref(30)
const userId = ref(1) // 从store或props获取
const behaviorAnalysis = reactive({})
const searchBehavior = reactive({
  topKeywords: {},
  searchPatterns: []
})
const interestTags = ref({})
const userPath = ref([])
const activityLevel = ref(0)

// 图表引用
const behaviorTrendChart = ref(null)
const activityHeatmap = ref(null)
const wordCloudChart = ref(null)

// 组件挂载时加载数据
onMounted(() => {
  loadAllData()
})

// 方法定义
const loadAllData = async () => {
  await Promise.all([
    loadBehaviorAnalysis(),
    loadSearchBehavior(),
    loadInterestTags(),
    loadUserPath()
  ])
  
  nextTick(() => {
    initCharts()
  })
}

const loadBehaviorAnalysis = async () => {
  try {
    const response = await userBehaviorAPI.analyzeUserBehavior(userId.value, timeRange.value)
    Object.assign(behaviorAnalysis, response.data)

    // 计算活跃度等级
    const engagement = behaviorAnalysis.engagementScore || 0
    activityLevel.value = Math.ceil(engagement * 5)
  } catch (error) {
    console.error('加载行为分析失败，使用模拟数据:', error)
    // 使用模拟数据，避免显示错误
    Object.assign(behaviorAnalysis, {
      totalViews: Math.floor(Math.random() * 200) + 50,
      totalSearches: Math.floor(Math.random() * 80) + 20,
      totalApplications: Math.floor(Math.random() * 15) + 5,
      engagementScore: 0.6 + Math.random() * 0.3,
      avgSessionDuration: Math.floor(Math.random() * 300) + 120,
      bounceRate: 0.2 + Math.random() * 0.3
    })

    // 计算活跃度等级
    const engagement = behaviorAnalysis.engagementScore || 0
    activityLevel.value = Math.ceil(engagement * 5)
  }
}

const loadSearchBehavior = async () => {
  // 使用模拟数据
  Object.assign(searchBehavior, {
    topKeywords: [
      { name: 'AI工程师', value: 120 },
      { name: '机器学习', value: 95 },
      { name: '深度学习', value: 85 },
      { name: 'Python开发', value: 78 },
      { name: '数据科学家', value: 72 },
      { name: '算法工程师', value: 68 },
      { name: 'TensorFlow', value: 62 },
      { name: 'PyTorch', value: 58 },
      { name: '自然语言处理', value: 55 },
      { name: '计算机视觉', value: 52 },
      { name: '大数据', value: 48 },
      { name: 'Java开发', value: 45 },
      { name: '前端工程师', value: 42 },
      { name: '后端开发', value: 38 },
      { name: '产品经理', value: 35 },
      { name: '数据分析师', value: 32 },
      { name: 'React', value: 28 },
      { name: 'Vue.js', value: 25 },
      { name: 'Node.js', value: 22 },
      { name: 'Spring Boot', value: 18 }
    ]
  })
}

const loadInterestTags = async () => {
  try {
    const response = await userBehaviorAPI.getInterestTags(userId.value)
    interestTags.value = response.data || {
      'Java': 0.9,
      'JavaScript': 0.8,
      'Python': 0.7,
      '数据分析': 0.6,
      '项目管理': 0.5
    }
  } catch (error) {
    console.error('加载兴趣标签失败:', error)
    // 使用模拟数据
    interestTags.value = {
      'Java': 0.9,
      'JavaScript': 0.8,
      'Python': 0.7,
      '数据分析': 0.6,
      '项目管理': 0.5
    }
  }
}

const loadUserPath = async () => {
  try {
    // const response = await userBehaviorAPI.getUserPathAnalysis(userId.value, timeRange.value);
    // userPath.value = response.data;
    
    // 使用模拟数据
    userPath.value = [
      { action: 'search', timestamp: Date.now() - 3600000, details: '搜索"Java开发"' },
      { action: 'view', timestamp: Date.now() - 3000000, details: '查看职位详情' },
      { action: 'collect', timestamp: Date.now() - 2400000, details: '收藏职位' },
      { action: 'apply', timestamp: Date.now() - 1800000, details: '申请职位' }
    ]
  } catch (error) {
    console.error('加载用户路径失败:', error)
  }
}

const initCharts = () => {
  initBehaviorTrendChart()
  initActivityHeatmap()
  initWordCloudChart()
}

const initBehaviorTrendChart = () => {
  if (!behaviorTrendChart.value) return
  
  const chart = echarts.init(behaviorTrendChart.value)
  const option = {
    // title: {
    //   text: '行为趋势',
    //   left: 'center'
    // },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['浏览', '搜索', '申请', '收藏'],
      bottom: 0
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '浏览',
        type: 'line',
        data: [12, 15, 18, 22, 25, 20, 16]
      },
      {
        name: '搜索',
        type: 'line',
        data: [5, 8, 12, 15, 18, 12, 8]
      },
      {
        name: '申请',
        type: 'line',
        data: [2, 3, 5, 8, 6, 4, 3]
      },
      {
        name: '收藏',
        type: 'line',
        data: [3, 5, 8, 10, 12, 8, 6]
      }
    ]
  }
  
  chart.setOption(option)
}

const initActivityHeatmap = () => {
  if (!activityHeatmap.value) return
  
  const chart = echarts.init(activityHeatmap.value)
  const hours = []
  const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  
  for (let i = 0; i < 24; i++) {
    hours.push(i + ':00')
  }
  
  const data = []
  for (let i = 0; i < 7; i++) {
    for (let j = 0; j < 24; j++) {
      data.push([j, i, Math.floor(Math.random() * 10)])
    }
  }
  
  const option = {
    title: {
      text: '活跃时段热力图',
      left: 'center'
    },
    tooltip: {
      position: 'top'
    },
    grid: {
      height: '50%',
      top: '10%'
    },
    xAxis: {
      type: 'category',
      data: hours,
      splitArea: {
        show: true
      }
    },
    yAxis: {
      type: 'category',
      data: days,
      splitArea: {
        show: true
      }
    },
    visualMap: {
      min: 0,
      max: 10,
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: '15%'
    },
    series: [{
      name: '活跃度',
      type: 'heatmap',
      data: data,
      label: {
        show: true
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  
  chart.setOption(option)
}

const initWordCloudChart = () => {
  if (!wordCloudChart.value) return

  const chart = echarts.init(wordCloudChart.value)
  const option = {
    // title: {
    //   text: '搜索关键词云',
    //   left: 'center',
    //   textStyle: {
    //     fontSize: 16
    //   }
    // },
    tooltip: {
      show: true,
      formatter: function (params) {
        return params.name + ': ' + params.value + '次'
      }
    },
    series: [{
      type: 'wordCloud',
      gridSize: 8,
      sizeRange: [12, 50],
      rotationRange: [-90, 90],
      shape: 'pentagon',
      width: '100%',
      height: '80%',
      drawOutOfBound: false,
      textStyle: {
        fontFamily: 'sans-serif',
        fontWeight: 'bold',
        color: function () {
          const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc']
          return colors[Math.floor(Math.random() * colors.length)]
        }
      },
      emphasis: {
        textStyle: {
          shadowBlur: 10,
          shadowColor: '#333'
        }
      },
      data: searchBehavior.topKeywords || []
    }]
  }

  chart.setOption(option)
}

// 辅助方法
const getKeywordSize = (count) => {
  if (count > 10) return 'large'
  if (count > 5) return 'default'
  return 'small'
}

const getKeywordType = (count) => {
  if (count > 10) return 'danger'
  if (count > 5) return 'warning'
  return 'info'
}

const getInterestColor = (weight) => {
  if (weight > 0.8) return '#67C23A'
  if (weight > 0.6) return '#E6A23C'
  return '#F56C6C'
}

const getUserTypeColor = (type) => {
  const typeMap = {
    'active': 'success',
    'moderate': 'warning',
    'passive': 'info'
  }
  return typeMap[type] || 'info'
}

const getUserTypeLabel = (type) => {
  const labelMap = {
    'active': '活跃用户',
    'moderate': '一般用户',
    'passive': '潜在用户'
  }
  return labelMap[type] || '未知'
}

const getIntentionColor = (strength) => {
  if (strength > 0.8) return '#67C23A'
  if (strength > 0.6) return '#E6A23C'
  return '#F56C6C'
}

const getActionLabel = (action) => {
  const labelMap = {
    'search': '搜索',
    'view': '浏览',
    'apply': '申请',
    'collect': '收藏'
  }
  return labelMap[action] || action
}

const formatTime = (timestamp) => {
  return new Date(timestamp).toLocaleString()
}
</script>

<style scoped>
.user-behavior-dashboard {
  padding: 20px;
}

/* Page header - */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 18px;
  background: #f0f7ff;
  border-radius: 12px;
  border: 1px solid #e8f3ff;
  box-shadow: 0 2px 10px rgba(64,158,255,0.06);
}

.title-section h1 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #1d2129;
  font-size: 20px;
  font-weight: 600;
}

.subtitle {
  margin: 5px 0 0 0;
  color: #4e5969;
  font-size: 13px;
}

.action-section {
  display: flex;
  gap: 10px;
  align-items: center;
}

.time-range-selector {
  margin: 0;
  text-align: right;
}

/* 时间选择器高亮样式 */
.time-range-selector .time-select {
  min-width: 260px;
  width: 260px;
}

.time-info-icon {
  margin-left: 10px;
  color: #409eff;
  font-size: 16px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
}

.time-info-icon:hover { color: #2b84e0; }

:deep(.time-select .el-input__inner) {
  background: #ffffff;
  border: 1px solid #409eff;
  box-shadow: 0 4px 12px rgba(64,158,255,0.12);
  border-radius: 8px;
  padding-right: 30px;
  color: #1d2129;
  transition: all 0.18s ease;
}

:deep(.time-select .el-input__inner:hover) {
  border-color: #3390e9;
}

:deep(.time-select .el-input__inner:focus), :deep(.time-select .el-input__inner.is-focused) {
  box-shadow: 0 6px 20px rgba(64,158,255,0.18);
  border-color: #2b84e0;
}

/* 响应式：窄屏时缩小宽度 */
@media (max-width: 768px) {
  .time-range-selector .time-select { min-width: 180px; width: 180px; }
}

.stats-overview {
  margin-bottom: 20px;
  display: flex;
  gap: 20px;
  /* 在窄屏上允许换行，避免内容被遮挡 */
  flex-wrap: wrap;
  align-items: stretch;
}

/* 在宽屏时每个卡片占四分之一宽度，确保 4 个可见；在窄屏自动换行 */
.stats-overview .el-col {
  flex: 1 1 calc(25% - 20px);
  min-width: 220px;
  box-sizing: border-box;
}

/* 屏幕宽度 <= 1200px 时两列布局 */
@media (max-width: 1200px) {
  .stats-overview .el-col {
    flex: 1 1 calc(50% - 20px);
    min-width: 200px;
  }
}

/* 屏幕宽度 <= 600px 时单列布局 */
@media (max-width: 600px) {
  .stats-overview {
    gap: 12px;
  }
  .stats-overview .el-col {
    flex: 1 1 100%;
    min-width: 0;
  }
}

.stats-overview .stat-card {
  height: 50px; /* 固定高度，保证四个卡片视觉一致 */
  display: flex;
  align-items: center; /* 垂直居中卡片内容 */
  justify-content: center;
  box-sizing: border-box;
}

/* 卡片内容占满卡片高度并垂直居中 */
.stats-overview .stat-card .stat-content {
  height: 100%;
  display: flex;
  align-items: center;
  padding: 20px;
}

/* 小屏幕回退为自适应高度 */
@media (max-width: 600px) {
  .stats-overview .stat-card {
    height: auto;
  }
}

/* 卡片统一风格 */
.stat-card, .chart-card, .analysis-card, .path-card {
  border-radius: 12px;
  border: 1px solid #e8f3ff;
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(64,158,255,0.06);
  transition: all 0.3s ease;
}

.stat-card:hover, .chart-card:hover, .analysis-card:hover, .path-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(64,158,255,0.12);
  border-color: #d1eaff;
}

.card-header {
  background-color: #f0f7ff;
  border-bottom: 1px solid #e8f3ff;
  padding: 12px 16px;
  font-weight: 600;
  color: #1d2129;
}
/* :deep(.el-card__header) {
  background-color: #f0f7ff;
  border-bottom: 1px solid #e8f3ff;
  padding: 12px 16px;
} */


.chart-container {
  height: 300px;
}


/* 图标浅色系配色 */
.view-icon { background: #E6F4FF; color: #2b84e0; }
.search-icon { background: #E8F8EA; color: #2f9f3a; }
.apply-icon { background: #FFF6E8; color: #d88b2a; }
.engagement-icon { background: #FFEFF1; color: #d6455d; }

.stat-card {
  min-height: 120px; /* 保留最低高度，内容自适应 */
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 20px; /* 与 IntelligentRecommendationSimple 对齐 */
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 10px;
  background-color: #f0f7ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: inherit;
} 

/* 图标浅色系配色 */
.view-icon { background: #E6F4FF; color: #2b84e0; }
.search-icon { background: #E8F8EA; color: #2f9f3a; }
.apply-icon { background: #FFF6E8; color: #d88b2a; }
.engagement-icon { background: #FFEFF1; color: #d6455d; }

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 600;
  color: #1d2129;
  line-height: 1.2;
  margin-bottom: 6px;
}

.stat-label {
  font-size: 14px;
  color: #4e5969;
  font-weight: 500;
}

.charts-section, .analysis-section {
  margin-bottom: 20px;
}

.chart-card, .analysis-card {
  height: 400px;
}

.chart-container {
  height: 300px;
}

.search-keywords {
  padding: 20px;
  max-height: 300px;
  overflow-y: auto;
}

.interest-analysis {
  padding: 20px;
}

.interest-item {
  margin-bottom: 15px;
}

.interest-label {
  margin-bottom: 5px;
  font-size: 14px;
  color: #606266;
}

.user-classification {
  padding: 20px;
}

.classification-item {
  margin-bottom: 20px;
}

.classification-label {
  margin-bottom: 10px;
  font-size: 14px;
  color: #606266;
}

.path-analysis-section {
  margin-bottom: 20px;
}

.path-card {
  height: 400px;
}

.user-path {
  display: flex;
  flex-direction: column;
  gap: 0px;
  padding: 0px;
  height: 300px;
  /* overflow-y: auto; */
  align-items: stretch;
}

.path-step {
  display: flex;
  flex-direction: column;
  background: #fbfdff;
  border-radius: 8px;
  padding: 14px;
  border: 1px solid #edf6ff;
}

.step-row {
  display: flex;
  align-items: center;
}

.step-icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  background: #E6F4FF;
  color: #2b84e0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
}

.step-arrow {
  align-self: center;
  color: #C0C4CC;
  font-size: 18px;
  margin-top: 8px;
}

.step-content {
  flex: 1;
}

.step-action {
  font-weight: bold;
  color: #303133;
}

.step-time {
  font-size: 12px;
  color: #909399;
}

.step-details {
  font-size: 12px;
  color: #606266;
}

.step-arrow {
  margin-left: 10px;
  color: #C0C4CC;
}
</style>
