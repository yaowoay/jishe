<template>
  <div class="candidate-dashboard">
    <!-- 个人信息头 -->
    <el-card class="profile-card">
      <div class="profile-header">
        <div class="avatar-section">
          <el-avatar :size="64" :src="userInfo.avatar" />
          <div class="user-info">
            <h3>{{ userInfo.name }}</h3>
            <p>{{ userInfo.title }} · {{ userInfo.experience }}年经验</p>
            <div class="tags">
              <el-tag v-for="tag in userInfo.skills" :key="tag" size="small" type="info">
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </div>
        <div class="stats">
          <div class="stat-item">
            <div class="stat-number">{{ userStats.interviewCount }}</div>
            <div class="stat-label">总面试</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ userStats.avgScore }}</div>
            <div class="stat-label">平均分</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ userStats.successRate }}%</div>
            <div class="stat-label">成功率</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 技能评估 -->
    <div class="skills-section">
      <el-card>
        <template #header>
          <span>技能评估分析</span>
        </template>
        <div ref="skillsChart" style="width: 100%; height: 300px;"></div>
      </el-card>
    </div>

    <!-- 面试进度 -->
    <div class="progress-grid">
      <el-card>
        <template #header>
          <span>面试进度</span>
        </template>
        <div class="progress-list">
          <div v-for="item in progressData" :key="item.company" class="progress-item">
            <div class="company-info">
              <div class="company-name">{{ item.company }}</div>
              <div class="position">{{ item.position }}</div>
            </div>
            <div class="progress-status">
              <el-progress :percentage="item.progress" :status="item.status" />
              <span class="status-text">{{ item.statusText }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card>
        <template #header>
          <span>面试表现趋势</span>
        </template>
        <div ref="performanceChart" style="width: 100%; height: 300px;"></div>
      </el-card>
    </div>

    <!-- 最近面试 -->
    <el-card>
      <template #header>
        <div class="table-header">
          <span>最近面试记录</span>
          <el-button type="primary" size="small">查看历史</el-button>
        </div>
      </template>
      <el-table :data="recentInterviews" style="width: 100%">
        <el-table-column prop="company" label="公司" min-width="120" />
        <el-table-column prop="position" label="岗位" min-width="100" />
        <el-table-column prop="date" label="日期" width="100" />
        <el-table-column prop="duration" label="时长" width="80" />
        <el-table-column prop="score" label="评分" width="100">
          <template #default="{ row }">
            <el-rate v-model="row.score" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="结果" width="100">
          <template #default="{ row }">
            <el-tag :type="resultType[row.status]">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="viewReport(row)">
              查看报告
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

// 模拟数据
const userInfo = ref({
  name: '张三',
  title: '前端开发工程师',
  experience: 3,
  avatar: 'https://via.placeholder.com/64',
  skills: ['Vue.js', 'React', 'TypeScript', 'Node.js', 'Webpack']
})

const userStats = ref({
  interviewCount: 12,
  avgScore: 4.3,
  successRate: 75
})

const progressData = ref([
  { company: '阿里巴巴', position: '高级前端开发', progress: 80, status: 'success', statusText: '终面' },
  { company: '腾讯', position: '前端开发工程师', progress: 60, status: 'warning', statusText: '二面' },
  { company: '字节跳动', position: '全栈开发', progress: 40, status: 'primary', statusText: '初面' },
  { company: '美团', position: '前端开发', progress: 100, status: 'success', statusText: '已录用' }
])

const recentInterviews = ref([
  { company: '百度', position: '前端开发', date: '01-15', duration: '45min', score: 4.5, status: '通过' },
  { company: '京东', position: 'React开发', date: '01-12', duration: '38min', score: 4.2, status: '通过' },
  { company: '滴滴', position: 'Vue开发', date: '01-10', duration: '50min', score: 3.8, status: '未通过' },
  { company: '拼多多', position: '前端开发', date: '01-08', duration: '42min', score: 4.1, status: '待定' }
])

const resultType = {
  '通过': 'success',
  '未通过': 'danger',
  '待定': 'warning'
}

const skillsChart = ref(null)
const performanceChart = ref(null)

onMounted(() => {
  initCharts()
})

function initCharts() {
  // 技能雷达图
  const skillsChartInstance = echarts.init(skillsChart.value)
  skillsChartInstance.setOption({
    radar: {
      indicator: [
        { name: '技术能力', max: 100 },
        { name: '沟通表达', max: 100 },
        { name: '解决问题', max: 100 },
        { name: '团队协作', max: 100 },
        { name: '学习能力', max: 100 }
      ]
    },
    series: [{
      type: 'radar',
      data: [
        {
          value: [85, 78, 90, 82, 88],
          name: '当前能力',
          areaStyle: {
            color: 'rgba(64, 158, 255, 0.3)'
          },
          lineStyle: {
            color: '#409EFF'
          }
        }
      ]
    }]
  })

  // 表现趋势图
  const performanceChartInstance = echarts.init(performanceChart.value)
  performanceChartInstance.setOption({
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 5
    },
    series: [{
      data: [3.8, 4.0, 4.2, 4.1, 4.3, 4.5, 4.2, 4.4, 4.6, 4.5, 4.7, 4.8],
      type: 'line',
      smooth: true,
      lineStyle: {
        color: '#67C23A'
      },
      symbol: 'circle',
      symbolSize: 8,
      itemStyle: {
        color: '#67C23A'
      }
    }]
  })

  window.addEventListener('resize', () => {
    skillsChartInstance.resize()
    performanceChartInstance.resize()
  })
}

function viewReport(row) {
  console.log('查看报告:', row)
}
</script>

<style scoped>
.candidate-dashboard {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.profile-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info h3 {
  margin: 0 0 4px 0;
  font-size: 18px;
  color: #303133;
}

.user-info p {
  margin: 0 0 8px 0;
  color: #606266;
  font-size: 14px;
}

.tags {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.skills-section {
  margin-bottom: 20px;
}

.progress-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 20px;
}

.progress-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.progress-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.company-info {
  flex: 1;
}

.company-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
}

.position {
  font-size: 12px;
  color: #909399;
}

.progress-status {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.status-text {
  font-size: 12px;
  color: #409EFF;
  text-align: right;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>