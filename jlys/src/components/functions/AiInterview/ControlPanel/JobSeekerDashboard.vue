<template>
  <div class="dashboard-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <!-- 个人中心按钮 - 绝对定位到右上角 -->
      <div class="personal-center-wrapper">
        <el-card shadow="hover" class="personal-center-card" @click="goPersonalInfo">
          <div class="personal-center-box">
            <el-button class="personal-center-btn" type="primary" size="large" circle>
              <el-icon><Setting /></el-icon>
            </el-button>
            <span class="personal-center-text">个人中心</span>
          </div>
        </el-card>
      </div>

      <!-- 标题区域 - 居中显示 -->
      <div class="header-center">
        <h2 class="page-title">求职者<span class="highlight">数据控制台</span></h2>
        <p class="page-subtitle">全方位掌握你的求职进展和能力提升</p>
      </div>
    </div>

    <!-- 核心数据概览 -->
    <div class="overview-section">
      <el-row :gutter="16" :xs="1" :sm="2" :md="4" :lg="4">
        <el-col :span="24" :xs="24" :sm="12" :md="6" :lg="6">
          <div class="overview-card resume">
            <div class="card-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-number">{{ overview.resumeCount }}</div>
              <div class="card-label">测试已生成简历</div>
              <div class="card-change positive">本周 +{{ overview.resumeChange }}</div>
            </div>
          </div>
        </el-col>
        <el-col :span="24" :xs="24" :sm="12" :md="6" :lg="6">
          <div class="overview-card interview">
            <div class="card-icon">
              <el-icon><Headset /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-number">{{ overview.simulateCount }}</div>
              <div class="card-label">模拟面试次数</div>
              <div class="card-change positive">本月 +{{ overview.simulateChange }}</div>
            </div>
          </div>
        </el-col>
        <el-col :span="24" :xs="24" :sm="12" :md="6" :lg="6">
          <div class="overview-card test">
            <div class="card-icon">
              <el-icon><Monitor /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-number">{{ overview.ojCount }}</div>
              <div class="card-label">OJ测试题数</div>
              <div class="card-change neutral">通过率 {{ overview.ojPassRate }}%</div>
            </div>
          </div>
        </el-col>
        <el-col :span="24" :xs="24" :sm="12" :md="6" :lg="6">
          <div class="overview-card score">
            <div class="card-icon">
              <el-icon><Trophy /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-number">{{ overview.avgScore }}</div>
              <div class="card-label">平均面试评分</div>
              <div class="card-change positive">较上次 +{{ overview.scoreImprovement }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 主要内容区域 -->
    <el-row :gutter="20" class="main-content" :xs="{span:24}" :sm="{span:24}" :md="{span:24}" :lg="{span:24}">
      <!-- 左侧：最新报告和测试结果 -->
      <el-col :xs="24" :sm="24" :md="16" :lg="16">
        <!-- 最新面试报告 -->
        <div class="data-card report-card">
          <div class="card-header">
            <span class="card-title">最新模拟面试报告</span>
            <el-button type="primary" size="small" @click="viewFullReport">查看完整报告</el-button>
          </div>
          <div class="report-summary">
            <div class="report-info">
              <div class="report-meta">
                <span class="report-date">{{ latestReport.date }}</span>
                <span class="report-position">{{ latestReport.position }}</span>
                <span class="report-duration">面试时长: {{ latestReport.duration }}</span>
              </div>
              <div class="score-display">
                <div class="score-circle">
                  <span class="score-value">{{ latestReport.totalScore }}</span>
                  <span class="score-label">总分</span>
                </div>
              </div>
            </div>
            <div class="score-breakdown">
              <div class="score-item">
                <span class="score-name">技术能力</span>
                <div class="score-bar">
                  <div class="score-fill" :style="{ width: latestReport.technical + '%' }"></div>
                </div>
                <span class="score-num">{{ latestReport.technical }}</span>
              </div>
              <div class="score-item">
                <span class="score-name">表达能力</span>
                <div class="score-bar">
                  <div class="score-fill" :style="{ width: latestReport.communication + '%' }"></div>
                </div>
                <span class="score-num">{{ latestReport.communication }}</span>
              </div>
              <div class="score-item">
                <span class="score-name">逻辑思维</span>
                <div class="score-bar">
                  <div class="score-fill" :style="{ width: latestReport.logic + '%' }"></div>
                </div>
                <span class="score-num">{{ latestReport.logic }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- OJ测试结果 -->
        <div class="data-card oj-card">
          <div class="card-header">
            <span class="card-title">OJ测试结果概览</span>
            <el-button type="text" @click="goToOJ">进入OJ系统</el-button>
          </div>
          <div class="oj-stats">
            <div class="oj-stat-item">
              <div class="oj-icon">🎯</div>
              <div class="oj-content">
                <div class="oj-value">{{ ojStats.totalSolved }}</div>
                <div class="oj-label">已解决题目</div>
              </div>
            </div>
            <div class="oj-stat-item">
              <div class="oj-icon">⚡</div>
              <div class="oj-content">
                <div class="oj-value">{{ ojStats.easyCount }}</div>
                <div class="oj-label">简单题</div>
              </div>
            </div>
            <div class="oj-stat-item">
              <div class="oj-icon">🔥</div>
              <div class="oj-content">
                <div class="oj-value">{{ ojStats.mediumCount }}</div>
                <div class="oj-label">中等题</div>
              </div>
            </div>
            <div class="oj-stat-item">
              <div class="oj-icon">💎</div>
              <div class="oj-content">
                <div class="oj-value">{{ ojStats.hardCount }}</div>
                <div class="oj-label">困难题</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 图表区域 -->
        <el-row :gutter="20" :xs="1" :sm="1" :md="2" :lg="2">
          <!-- 个人能力雷达图 -->
          <el-col :xs="24" :sm="24" :md="12" :lg="12">
            <div class="data-card chart-card">
              <div class="card-header">
                <span class="card-title">个人能力雷达图</span>
              </div>
              <div ref="radarChart" class="chart-container small"></div>
            </div>
          </el-col>
          <!-- 意向岗位分布 -->
          <el-col :xs="24" :sm="24" :md="12" :lg="12">
            <div class="data-card chart-card">
              <div class="card-header">
                <span class="card-title">意向岗位分布</span>
              </div>
              <div ref="positionChart" class="chart-container small"></div>
            </div>
          </el-col>
        </el-row>
      </el-col>

      <!-- 右侧：推荐和学习进度 -->
      <el-col :xs="24" :sm="24" :md="8" :lg="8">
        <!-- 智能推荐 -->
        <div class="data-card recommendation-card">
          <div class="card-header">
            <span class="card-title">智能推荐</span>
            <el-button type="text" size="small" @click="refreshRecommendations">
              <el-icon><Refresh /></el-icon>
            </el-button>
          </div>
          <div class="recommendations">
            <div v-for="(rec, index) in recommendations" :key="index" class="recommendation-item">
              <div class="rec-icon" :class="rec.type">
                <i :class="rec.icon"></i>
              </div>
              <div class="rec-content">
                <div class="rec-title">{{ rec.title }}</div>
                <div class="rec-desc">{{ rec.description }}</div>
              </div>
              <el-button type="text" size="small" @click="handleRecommendation(rec)">
                {{ rec.action }}
              </el-button>
            </div>
          </div>
        </div>

        <!-- 资讯推荐 -->
        <div class="data-card news-card">
          <div class="card-header">
            <span class="card-title">行业资讯</span>
            <el-button type="text" size="small" @click="refreshNews">
              <el-icon><Refresh /></el-icon>
            </el-button>
          </div>
          <div class="news-items">
            <div v-for="(news, index) in newsRecommendations" :key="index" class="news-item" @click="openNews(news)">
              <div class="news-tag" :class="news.category">{{ news.categoryName }}</div>
              <div class="news-title">{{ news.title }}</div>
              <div class="news-meta">
                <span class="news-source">{{ news.source }}</span>
                <span class="news-time">{{ news.time }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
// 脚本部分保持不变
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Document, Headset, Monitor, Trophy, Refresh, Setting } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const router = useRouter()

// 跳转到个人中心
function goPersonalInfo() {
  router.push('/layout/personalInfo')
}

// 概览数据
const overview = ref({
  resumeCount: 8,
  resumeChange: 2,
  simulateCount: 15,
  simulateChange: 6,
  ojCount: 127,
  ojPassRate: 78,
  avgScore: 8.6,
  scoreImprovement: 0.8
})

// 最新面试报告
const latestReport = ref({
  date: '2024-01-15',
  position: '前端开发工程师',
  duration: '28分钟',
  totalScore: 86,
  technical: 88,
  communication: 82,
  logic: 89
})

// OJ统计
const ojStats = ref({
  totalSolved: 127,
  easyCount: 68,
  mediumCount: 45,
  hardCount: 14
})

// 推荐内容
const recommendations = ref([
  {
    type: 'interview',
    icon: 'fas fa-user-tie',
    title: '模拟面试练习',
    description: '建议进行React相关技术面试',
    action: '开始练习'
  },
  {
    type: 'study',
    icon: 'fas fa-book',
    title: '算法强化',
    description: '动态规划题目正确率偏低',
    action: '去学习'
  },
  {
    type: 'resume',
    icon: 'fas fa-file-alt',
    title: '简历优化',
    description: '项目经验描述可以更具体',
    action: '去优化'
  }
])

// 所有资讯数据池
const allNewsData = [
  {
    category: 'tech',
    categoryName: '技术',
    title: 'Vue 3.4 正式发布，带来重大性能提升',
    source: '掘金',
    time: '2小时前',
    url: '#'
  },
  {
    category: 'career',
    categoryName: '职场',
    title: '2024年前端开发薪资报告出炉',
    source: 'BOSS直聘',
    time: '5小时前',
    url: '#'
  },
  {
    category: 'industry',
    categoryName: '行业',
    title: 'AI技术在前端开发中的应用趋势',
    source: '36氪',
    time: '1天前',
    url: '#'
  },
  {
    category: 'interview',
    categoryName: '面试',
    title: '字节跳动最新前端面试题解析',
    source: '牛客网',
    time: '2天前',
    url: '#'
  },
  {
    category: 'tech',
    categoryName: '技术',
    title: 'React 18 新特性深度解析',
    source: '思否',
    time: '3小时前',
    url: '#'
  },
  {
    category: 'career',
    categoryName: '职场',
    title: '互联网大厂春招面试攻略',
    source: '拉勾网',
    time: '6小时前',
    url: '#'
  },
  {
    category: 'industry',
    categoryName: '行业',
    title: '低代码平台发展现状与趋势',
    source: 'InfoQ',
    time: '8小时前',
    url: '#'
  },
  {
    category: 'tech',
    categoryName: '技术',
    title: 'TypeScript 5.0 重大更新详解',
    source: '开发者头条',
    time: '12小时前',
    url: '#'
  },
  {
    category: 'interview',
    categoryName: '面试',
    title: '腾讯前端面试真题及解答',
    source: '力扣',
    time: '1天前',
    url: '#'
  },
  {
    category: 'career',
    categoryName: '职场',
    title: '程序员如何规划职业发展路径',
    source: 'CSDN',
    time: '1天前',
    url: '#'
  },
  {
    category: 'industry',
    categoryName: '行业',
    title: 'Web3.0时代前端开发新机遇',
    source: '极客时间',
    time: '2天前',
    url: '#'
  },
  {
    category: 'tech',
    categoryName: '技术',
    title: 'Vite 4.0 构建工具性能优化',
    source: '掘金',
    time: '2天前',
    url: '#'
  }
]

// 当前显示的资讯推荐
const newsRecommendations = ref([])

const radarChart = ref()
const positionChart = ref()

// 初始化雷达图
const initRadarChart = () => {
  const chart = echarts.init(radarChart.value)
  const option = {
    radar: {
      indicator: [
        { name: '技术能力', max: 100 },
        { name: '表达能力', max: 100 },
        { name: '逻辑思维', max: 100 },
        { name: '学习能力', max: 100 },
        { name: '团队协作', max: 100 },
        { name: '抗压能力', max: 100 }
      ],
      radius: '65%',
      axisName: { color: '#64748b', fontSize: 12 }
    },
    series: [{
      type: 'radar',
      data: [{
        value: [88, 82, 89, 85, 78, 83],
        name: '个人能力',
        itemStyle: { color: '#5bbcff' },
        areaStyle: { color: 'rgba(91, 188, 255, 0.2)' }
      }]
    }]
  }
  chart.setOption(option)
}

// 初始化岗位分布图
const initPositionChart = () => {
  const chart = echarts.init(positionChart.value)
  const option = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 45, name: '前端开发', itemStyle: { color: '#5bbcff' } },
        { value: 25, name: '全栈开发', itemStyle: { color: '#a084e8' } },
        { value: 20, name: 'React开发', itemStyle: { color: '#00cfff' } },
        { value: 10, name: 'Vue开发', itemStyle: { color: '#4fd18b' } }
      ],
      label: { fontSize: 10, color: '#334155' }
    }]
  }
  chart.setOption(option)
}

// 方法
const viewFullReport = () => {
  router.push('/layout/assessReport')
}

const goToOJ = () => {
  router.push('/layout/ojSystem')
}

const refreshRecommendations = () => {
  console.log('刷新推荐')
}

const handleRecommendation = (rec) => {
  console.log('处理推荐:', rec)

  // 根据推荐类型进行路由跳转
  switch (rec.type) {
  case 'interview':
    // 开始练习 - 跳转到模拟面试页面
    router.push('/layout/simulatExam')
    break
  case 'study':
    // 算法强化去学习 - 跳转到OJ系统
    router.push('/layout/ojSystem')
    break
  case 'resume':
    // 简历优化去优化 - 跳转到简历编辑页面
    router.push('/layout/editResume')
    break
  default:
    console.log('未知推荐类型:', rec.type)
  }
}

// 随机获取资讯数据
const getRandomNews = (count = 3) => {
  const shuffled = [...allNewsData].sort(() => 0.5 - Math.random())
  return shuffled.slice(0, count)
}

// 初始化资讯数据
const initNewsData = () => {
  newsRecommendations.value = getRandomNews(3)
}

const refreshNews = () => {
  console.log('刷新资讯')
  // 重新随机获取资讯数据
  newsRecommendations.value = getRandomNews(3)
}

const openNews = (news) => {
  console.log('打开资讯:', news)
  // 可以在这里实现跳转到资讯详情页
}

onMounted(() => {
  // 初始化资讯数据
  initNewsData()

  nextTick(() => {
    initRadarChart()
    initPositionChart()
  })
})
</script>

<style scoped>
.dashboard-container {
  padding: 16px;
  background: linear-gradient(to bottom, #eaf4ff 0%, #f3f6fb 100%);
  min-height: 100vh;
  box-sizing: border-box;
}

.page-header {
  position: relative;
  text-align: center;
  margin-bottom: 16px;
}

.header-center {
  display: inline-block;
}

.personal-center-wrapper {
  position: absolute;
  top: 0;
  right: 0;
  z-index: 10;
}

/* 个人中心按钮样式 */
.personal-center-card {
  border-radius: 16px;
  background: linear-gradient(135deg, #eaf4ff 0%, #fff 100%);
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.2);
  cursor: pointer;
  transition: all 0.3s ease;
}

.personal-center-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.3);
}

.personal-center-card .el-card__body {
  padding: 12px 16px;
}

.personal-center-box {
  display: flex;
  align-items: center;
  gap: 12px;
}

.personal-center-btn {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.personal-center-btn:hover {
  background: linear-gradient(135deg, #337ecc 0%, #2c5aa0 100%);
  transform: scale(1.05);
}

.personal-center-text {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
  white-space: nowrap;
}

.page-title {
  font-size: clamp(1.5rem, 3vw, 2rem);
  font-weight: 800;
  color: #1e40af;
  margin: 0 0 8px 0;
}

.highlight {
  color: #5bbcff;
}

.page-subtitle {
  font-size: 0.9rem;
  color: #64748b;
  margin: 0;
}

.overview-section {
  margin-bottom: 8px;
}

.overview-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 16px;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 16px rgba(91, 188, 255, 0.1);
  transition: all 0.3s ease;
  height: 100%;
  min-height: 90px;
  box-sizing: border-box;
}

.overview-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(91, 188, 255, 0.15);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-right: 12px;
  color: #fff;
  flex-shrink: 0;
}

.resume .card-icon { background: linear-gradient(135deg, #5bbcff, #a084e8); }
.interview .card-icon { background: linear-gradient(135deg, #4fd18b, #00cfff); }
.test .card-icon { background: linear-gradient(135deg, #a084e8, #5bbcff); }
.score .card-icon { background: linear-gradient(135deg, #ffd04b, #ffb347); }

.card-content {
  flex: 1;
  min-width: 0; /* 解决文字溢出问题 */
}

.card-number {
  font-size: clamp(1.5rem, 3vw, 2rem);
  font-weight: 800;
  color: #1e293b;
  line-height: 1;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-label {
  font-size: 0.85rem;
  color: #64748b;
  margin-bottom: 2px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-change {
  font-size: 0.75rem;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-change.positive { color: #4fd18b; }
.card-change.neutral { color: #64748b; }

.data-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 16px rgba(91, 188, 255, 0.08);
  margin-bottom: 16px;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.data-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(91, 188, 255, 0.12);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f1f5f9;
}

.card-title {
  font-size: 1rem;
  font-weight: 700;
  color: #1e293b;
}

.report-summary {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.report-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.report-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  font-size: 0.85rem;
  color: #64748b;
}

.score-circle {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background: linear-gradient(135deg, #5bbcff, #a084e8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.score-value {
  font-size: 1.5rem;
  font-weight: 800;
}

.score-label {
  font-size: 0.7rem;
}

.score-breakdown {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.score-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.score-name {
  width: 70px;
  font-size: 0.85rem;
  color: #64748b;
  flex-shrink: 0;
}

.score-bar {
  flex: 1;
  height: 8px;
  background: #f1f5f9;
  border-radius: 4px;
  overflow: hidden;
}

.score-fill {
  height: 100%;
  background: linear-gradient(90deg, #5bbcff, #a084e8);
  transition: width 0.3s ease;
}

.score-num {
  width: 30px;
  text-align: right;
  font-weight: 600;
  color: #1e293b;
  flex-shrink: 0;
}

.oj-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
  gap: 12px;
}

.oj-stat-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.oj-stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(91, 188, 255, 0.1);
}

.oj-icon {
  font-size: 1.2rem;
  margin-right: 10px;
  flex-shrink: 0;
}

.oj-value {
  font-size: 1.2rem;
  font-weight: 700;
  color: #1e293b;
}

.oj-label {
  font-size: 0.75rem;
  color: #64748b;
}

.chart-container {
  height: 200px;
  width: 100%;
}

.chart-container.small {
  height: 180px;
}

.recommendations {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.recommendation-item {
  display: flex;
  align-items: center;
  padding: 10px;
  background: #f8fafc;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.recommendation-item:hover {
  background: #eaf4ff;
}

.rec-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  color: white;
  flex-shrink: 0;
}

.rec-icon.interview { background: #5bbcff; }
.rec-icon.study { background: #a084e8; }
.rec-icon.resume { background: #4fd18b; }

.rec-content {
  flex: 1;
  min-width: 0;
}

.rec-title {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 3px;
  font-size: 0.9rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.rec-desc {
  font-size: 0.75rem;
  color: #64748b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.news-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.news-item {
  padding: 10px;
  background: #f8fafc;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.news-item:hover {
  background: #eaf4ff;
  transform: translateY(-1px);
}

.news-tag {
  display: inline-block;
  padding: 2px 6px;
  border-radius: 12px;
  font-size: 0.7rem;
  font-weight: 600;
  margin-bottom: 4px;
}

.news-tag.tech { background: #e0f2fe; color: #0277bd; }
.news-tag.career { background: #f3e5f5; color: #7b1fa2; }
.news-tag.industry { background: #e8f5e8; color: #2e7d32; }
.news-tag.interview { background: #fff3e0; color: #ef6c00; }

.news-title {
  font-size: 0.85rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.7rem;
  color: #64748b;
}

.news-source {
  font-weight: 500;
}

.news-time {
  color: #94a3b8;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .overview-card {
    padding: 12px;
  }

  .card-icon {
    width: 40px;
    height: 40px;
    font-size: 18px;
    margin-right: 10px;
  }

  .chart-container {
    height: 160px;
  }

  .chart-container.small {
    height: 140px;
  }

}
</style>