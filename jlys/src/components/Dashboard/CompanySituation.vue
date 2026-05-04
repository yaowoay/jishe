<template>
  <div class="company-situation">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="title-section">
        <h1>
          <el-icon><OfficeBuilding /></el-icon>
          公司情况大屏
        </h1>
        <p class="subtitle">深度分析企业招聘状况，洞察行业发展趋势</p>
      </div>
      <div class="action-section">
        <el-button type="primary" @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-select v-model="selectedIndustry" placeholder="选择行业" style="width: 150px; margin-left: 10px;">
          <el-option label="全部行业" value="all"></el-option>
          <el-option label="互联网" value="internet"></el-option>
          <el-option label="金融" value="finance"></el-option>
          <el-option label="教育" value="education"></el-option>
          <el-option label="医疗" value="medical"></el-option>
        </el-select>
      </div>
    </div>

    <!-- 企业概况指标 -->
    <div class="company-metrics">
      <el-row :gutter="20">
        <el-col :span="4">
          <el-card class="metric-card total-companies">
            <div class="metric-content">
              <div class="metric-icon">
                <el-icon><OfficeBuilding /></el-icon>
              </div>
              <div class="metric-info">
                <div class="metric-number">{{ companyMetrics.totalCompanies }}</div>
                <div class="metric-label">注册企业</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="metric-card active-recruiting">
            <div class="metric-content">
              <div class="metric-icon">
                <el-icon><Promotion /></el-icon>
              </div>
              <div class="metric-info">
                <div class="metric-number">{{ companyMetrics.activeRecruiting }}</div>
                <div class="metric-label">活跃招聘</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="metric-card unicorn-companies">
            <div class="metric-content">
              <div class="metric-icon">
                <el-icon><Trophy /></el-icon>
              </div>
              <div class="metric-info">
                <div class="metric-number">{{ companyMetrics.unicornCompanies }}</div>
                <div class="metric-label">独角兽企业</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="metric-card startup-companies">
            <div class="metric-content">
              <div class="metric-icon">
                <el-icon><Lightning /></el-icon>
              </div>
              <div class="metric-info">
                <div class="metric-number">{{ companyMetrics.startupCompanies }}</div>
                <div class="metric-label">创业公司</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="metric-card listed-companies">
            <div class="metric-content">
              <div class="metric-icon">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="metric-info">
                <div class="metric-number">{{ companyMetrics.listedCompanies }}</div>
                <div class="metric-label">上市公司</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="metric-card foreign-companies">
            <div class="metric-content">
              <div class="metric-icon">
                <el-icon><Connection /></el-icon>
              </div>
              <div class="metric-info">
                <div class="metric-number">{{ companyMetrics.foreignCompanies }}</div>
                <div class="metric-label">外资企业</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表分析区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <!-- 企业规模分布 -->
        <el-col :span="8">
          <el-card class="chart-card">
            <template #header>
              <span>企业规模分布</span>
            </template>
            <div ref="scaleChart" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 融资状态分布 -->
        <el-col :span="8">
          <el-card class="chart-card">
            <template #header>
              <span>融资状态分布</span>
            </template>
            <div ref="fundingChart" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 行业分布 -->
        <el-col :span="8">
          <el-card class="chart-card">
            <template #header>
              <span>行业分布情况</span>
            </template>
            <div ref="industryChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <!-- 企业招聘活跃度 -->
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>企业招聘活跃度趋势</span>
                <el-radio-group v-model="activityPeriod" size="small">
                  <el-radio-button label="week">周</el-radio-button>
                  <el-radio-button label="month">月</el-radio-button>
                  <el-radio-button label="quarter">季</el-radio-button>
                </el-radio-group>
              </div>
            </template>
            <div ref="activityChart" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 地区企业分布 -->
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <span>地区企业分布</span>
            </template>
            <div ref="regionChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 热门企业排行榜 -->
    <div class="ranking-section">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="ranking-card">
            <template #header>
              <div class="ranking-header">
                <span>招聘活跃企业TOP10</span>
                <el-tag type="success" size="small">实时更新</el-tag>
              </div>
            </template>
            <div class="ranking-list">
              <div 
                v-for="(company, index) in topActiveCompanies" 
                :key="company.id"
                class="ranking-item"
                :class="{ 'top-three': index < 3 }"
              >
                <div class="rank-number" :class="`rank-${index + 1}`">{{ index + 1 }}</div>
                <div class="company-logo">
                  <img :src="company.logo" :alt="company.name" />
                </div>
                <div class="company-info">
                  <div class="company-name">{{ company.name }}</div>
                  <div class="company-industry">{{ company.industry }}</div>
                </div>
                <div class="company-stats">
                  <div class="stat-item">
                    <span class="stat-number">{{ company.activeJobs }}</span>
                    <span class="stat-label">在招职位</span>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card class="ranking-card">
            <template #header>
              <div class="ranking-header">
                <span>薪资水平企业TOP10</span>
                <el-tag type="warning" size="small">按平均薪资</el-tag>
              </div>
            </template>
            <div class="ranking-list">
              <div 
                v-for="(company, index) in topSalaryCompanies" 
                :key="company.id"
                class="ranking-item"
                :class="{ 'top-three': index < 3 }"
              >
                <div class="rank-number" :class="`rank-${index + 1}`">{{ index + 1 }}</div>
                <div class="company-logo">
                  <img :src="company.logo" :alt="company.name" />
                </div>
                <div class="company-info">
                  <div class="company-name">{{ company.name }}</div>
                  <div class="company-industry">{{ company.industry }}</div>
                </div>
                <div class="company-stats">
                  <div class="stat-item">
                    <span class="stat-number">{{ company.avgSalary }}K</span>
                    <span class="stat-label">平均薪资</span>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 企业详情弹窗 -->
    <el-dialog v-model="showCompanyDetail" title="企业详情" width="60%">
      <div v-if="selectedCompany" class="company-detail">
        <div class="detail-header">
          <img :src="selectedCompany.logo" :alt="selectedCompany.name" class="detail-logo" />
          <div class="detail-info">
            <h2>{{ selectedCompany.name }}</h2>
            <p>{{ selectedCompany.description }}</p>
            <div class="detail-tags">
              <el-tag>{{ selectedCompany.industry }}</el-tag>
              <el-tag type="success">{{ selectedCompany.scale }}</el-tag>
              <el-tag type="warning">{{ selectedCompany.funding }}</el-tag>
            </div>
          </div>
        </div>
        
        <el-descriptions :column="2" border style="margin-top: 20px;">
          <el-descriptions-item label="成立时间">{{ selectedCompany.foundedYear }}</el-descriptions-item>
          <el-descriptions-item label="员工规模">{{ selectedCompany.employeeCount }}</el-descriptions-item>
          <el-descriptions-item label="总部地址">{{ selectedCompany.headquarters }}</el-descriptions-item>
          <el-descriptions-item label="融资轮次">{{ selectedCompany.fundingRound }}</el-descriptions-item>
          <el-descriptions-item label="在招职位">{{ selectedCompany.activeJobs }}</el-descriptions-item>
          <el-descriptions-item label="平均薪资">{{ selectedCompany.avgSalary }}K</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  OfficeBuilding, Refresh, Promotion, Trophy, Lightning,
  TrendCharts, Connection
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 响应式数据
const loading = ref(false)
const selectedIndustry = ref('all')
const activityPeriod = ref('month')
const showCompanyDetail = ref(false)
const selectedCompany = ref(null)

// 企业指标数据
const companyMetrics = reactive({
  totalCompanies: 8520,
  activeRecruiting: 3280,
  unicornCompanies: 45,
  startupCompanies: 1250,
  listedCompanies: 180,
  foreignCompanies: 320
})

// 热门企业数据
const topActiveCompanies = ref([
  { id: 1, name: '阿里巴巴', industry: '互联网', activeJobs: 1250, logo: '/api/placeholder/40/40' },
  { id: 2, name: '腾讯', industry: '互联网', activeJobs: 980, logo: '/api/placeholder/40/40' },
  { id: 3, name: '字节跳动', industry: '互联网', activeJobs: 850, logo: '/api/placeholder/40/40' },
  { id: 4, name: '美团', industry: '生活服务', activeJobs: 720, logo: '/api/placeholder/40/40' },
  { id: 5, name: '滴滴', industry: '出行服务', activeJobs: 650, logo: '/api/placeholder/40/40' },
  { id: 6, name: '百度', industry: '互联网', activeJobs: 580, logo: '/api/placeholder/40/40' },
  { id: 7, name: '京东', industry: '电商', activeJobs: 520, logo: '/api/placeholder/40/40' },
  { id: 8, name: '网易', industry: '互联网', activeJobs: 480, logo: '/api/placeholder/40/40' },
  { id: 9, name: '小米', industry: '智能硬件', activeJobs: 420, logo: '/api/placeholder/40/40' },
  { id: 10, name: '华为', industry: '通信设备', activeJobs: 380, logo: '/api/placeholder/40/40' }
])

const topSalaryCompanies = ref([
  { id: 1, name: '字节跳动', industry: '互联网', avgSalary: 35.2, logo: '/api/placeholder/40/40' },
  { id: 2, name: '阿里巴巴', industry: '互联网', avgSalary: 32.8, logo: '/api/placeholder/40/40' },
  { id: 3, name: '腾讯', industry: '互联网', avgSalary: 31.5, logo: '/api/placeholder/40/40' },
  { id: 4, name: '华为', industry: '通信设备', avgSalary: 28.9, logo: '/api/placeholder/40/40' },
  { id: 5, name: '美团', industry: '生活服务', avgSalary: 26.7, logo: '/api/placeholder/40/40' },
  { id: 6, name: '滴滴', industry: '出行服务', avgSalary: 25.3, logo: '/api/placeholder/40/40' },
  { id: 7, name: '百度', industry: '互联网', avgSalary: 24.8, logo: '/api/placeholder/40/40' },
  { id: 8, name: '京东', industry: '电商', avgSalary: 23.5, logo: '/api/placeholder/40/40' },
  { id: 9, name: '小米', industry: '智能硬件', avgSalary: 22.1, logo: '/api/placeholder/40/40' },
  { id: 10, name: '网易', industry: '互联网', avgSalary: 21.8, logo: '/api/placeholder/40/40' }
])

// 图表引用
const scaleChart = ref()
const fundingChart = ref()
const industryChart = ref()
const activityChart = ref()
const regionChart = ref()

// 图表实例
let scaleChartInstance = null
let fundingChartInstance = null
let industryChartInstance = null
let activityChartInstance = null
let regionChartInstance = null

// 方法定义
const refreshData = async () => {
  loading.value = true 
  try {
    await new Promise(resolve => setTimeout(resolve, 1000)) 
    
    // 更新指标数据
    companyMetrics.totalCompanies = Math.floor(Math.random() * 1000) + 8000 
    companyMetrics.activeRecruiting = Math.floor(Math.random() * 500) + 3000 
    companyMetrics.unicornCompanies = Math.floor(Math.random() * 10) + 40 
    
    // 重新渲染图表
    initCharts() 
    
    ElMessage.success('数据刷新成功') 
  } catch (error) {
    ElMessage.error('数据刷新失败') 
  } finally {
    loading.value = false 
  }
}

const viewCompanyDetail = (company) => {
  selectedCompany.value = {
    ...company,
    description: '领先的科技公司，致力于通过技术创新推动社会进步',
    foundedYear: '1999年',
    employeeCount: '10万+',
    headquarters: '杭州',
    fundingRound: 'IPO',
    activeJobs: company.activeJobs || 500,
    avgSalary: company.avgSalary || 25.0
  } 
  showCompanyDetail.value = true 
} 

// 初始化图表
const initCharts = () => {
  nextTick(() => {
    initScaleChart() 
    initFundingChart() 
    initIndustryChart() 
    initActivityChart() 
    initRegionChart() 
  }) 
} 

const initScaleChart = () => {
  if (scaleChartInstance) {
    scaleChartInstance.dispose() 
  }
  
  scaleChartInstance = echarts.init(scaleChart.value) 
  const option = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 2340, name: '大型企业(1000+人)' },
        { value: 1548, name: '中型企业(100-1000人)' },
        { value: 1048, name: '小型企业(20-100人)' },
        { value: 735, name: '微型企业(20人以下)' }
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
  scaleChartInstance.setOption(option) 
} 

const initFundingChart = () => {
  if (fundingChartInstance) {
    fundingChartInstance.dispose() 
  }
  
  fundingChartInstance = echarts.init(fundingChart.value) 
  const option = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '60%',
      data: [
        { value: 1200, name: '已上市' },
        { value: 800, name: 'D轮及以上' },
        { value: 600, name: 'C轮' },
        { value: 400, name: 'B轮' },
        { value: 300, name: 'A轮' },
        { value: 200, name: '天使轮' },
        { value: 500, name: '未融资' }
      ]
    }]
  } 
  fundingChartInstance.setOption(option) 
} 

const initIndustryChart = () => {
  if (industryChartInstance) {
    industryChartInstance.dispose() 
  }
  
  industryChartInstance = echarts.init(industryChart.value) 
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['互联网', '金融', '教育', '医疗', '制造业', '房地产'],
      axisLabel: { rotate: 45 }
    },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: [3200, 1800, 1200, 800, 600, 400],
      itemStyle: { color: '#5470c6' }
    }]
  } 
  industryChartInstance.setOption(option) 
} 

const initActivityChart = () => {
  if (activityChartInstance) {
    activityChartInstance.dispose() 
  }
  
  activityChartInstance = echarts.init(activityChart.value) 
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['新增企业', '活跃招聘', '职位发布'] },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '新增企业',
        type: 'line',
        data: [120, 132, 101, 134, 90, 230, 210, 145, 180, 142, 152, 168],
        smooth: true
      },
      {
        name: '活跃招聘',
        type: 'line',
        data: [220, 182, 191, 234, 290, 330, 310, 245, 280, 242, 252, 268],
        smooth: true
      },
      {
        name: '职位发布',
        type: 'line',
        data: [1200, 1320, 1010, 1340, 1290, 1330, 1320, 1450, 1380, 1420, 1520, 1680],
        smooth: true
      }
    ]
  } 
  activityChartInstance.setOption(option) 
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
      data: ['北京', '上海', '深圳', '杭州', '广州', '成都', '南京', '武汉', '西安', '苏州']
    },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: [2340, 1890, 1650, 1200, 980, 760, 650, 520, 480, 420],
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ])
      }
    }]
  } 
  regionChartInstance.setOption(option) 
} 

// 监听行业选择变化
watch(selectedIndustry, (newValue) => {
  console.log('选择行业:', newValue) 
  // 这里可以根据行业筛选数据
}) 

// 监听活跃度周期变化
watch(activityPeriod, (newValue) => {
  console.log('选择周期:', newValue) 
  // 重新渲染活跃度图表
  initActivityChart() 
}) 

// 组件挂载
onMounted(() => {
  initCharts() 
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    scaleChartInstance?.resize() 
    fundingChartInstance?.resize() 
    industryChartInstance?.resize() 
    activityChartInstance?.resize() 
    regionChartInstance?.resize() 
  }) 
}) 

// 组件卸载
onUnmounted(() => {
  scaleChartInstance?.dispose() 
  fundingChartInstance?.dispose() 
  industryChartInstance?.dispose() 
  activityChartInstance?.dispose() 
  regionChartInstance?.dispose() 
  
  window.removeEventListener('resize', () => {}) 
}) 
</script>

<style scoped>
.company-situation {
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
  align-items: center;
}

.company-metrics {
  margin-bottom: 30px;
}

.metric-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.metric-card:hover {
  transform: translateY(-3px);
}

.metric-content {
  display: flex;
  align-items: center;
  padding: 15px;
}

.metric-icon {
  font-size: 32px;
  margin-right: 15px;
  padding: 12px;
  border-radius: 50%;
  color: white;
}

.total-companies .metric-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.active-recruiting .metric-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.unicorn-companies .metric-icon {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

.startup-companies .metric-icon {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.listed-companies .metric-icon {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
}

.foreign-companies .metric-icon {
  background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
}

.metric-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.metric-label {
  font-size: 14px;
  color: #606266;
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
  height: 280px;
  width: 100%;
}

.ranking-section {
  margin-bottom: 20px;
}

.ranking-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.ranking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ranking-list {
  max-height: 500px;
  overflow-y: auto;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.3s ease;
}

.ranking-item:hover {
  background-color: #f8f9fa;
  cursor: pointer;
}

.ranking-item:last-child {
  border-bottom: none;
}

.ranking-item.top-three {
  background: linear-gradient(90deg, #fff7e6 0%, #ffffff 100%);
}

.rank-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 15px;
  color: white;
}

.rank-1 {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
}

.rank-2 {
  background: linear-gradient(135deg, #c0c0c0 0%, #e8e8e8 100%);
}

.rank-3 {
  background: linear-gradient(135deg, #cd7f32 0%, #daa520 100%);
}

.rank-number:not(.rank-1):not(.rank-2):not(.rank-3) {
  background: #909399;
}

.company-logo {
  width: 40px;
  height: 40px;
  margin-right: 15px;
}

.company-logo img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.company-info {
  flex: 1;
}

.company-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.company-industry {
  font-size: 12px;
  color: #909399;
}

.company-stats {
  text-align: right;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.stat-number {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.company-detail {
  padding: 20px 0;
}

.detail-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
}

.detail-logo {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  margin-right: 20px;
  object-fit: cover;
}

.detail-info h2 {
  margin: 0 0 10px 0;
  color: #303133;
}

.detail-info p {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
}

.detail-tags {
  display: flex;
  gap: 10px;
}
</style>
