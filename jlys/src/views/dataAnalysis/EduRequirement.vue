<template>
  <div class="edu-requirement">
    <!-- 页面标题 - 保留原有结构 -->
    <div class="page-header">
      <div class="title-section">
        <h1>
          <el-icon><TrendCharts /></el-icon>
          岗位维度分析
        </h1>
        <p class="subtitle">基于市场数据的岗位维度深度分析</p>
      </div>
      <div class="action-buttons">
        <el-button icon="Refresh" @click="refreshData" type="primary" plain>
          刷新数据
        </el-button>
        <el-button icon="Download" @click="exportAnalysis" type="success" plain>
          导出分析
        </el-button>
      </div>
    </div>

    <!-- 快速概览数据卡片 -->
    <div class="quick-stats">
      <div class="stats-row">
        <div class="stat-col">
          <el-card class="stat-card">
            <div class="stat-content">
              <p class="stat-label">总岗位数量</p>
              <p class="stat-value">4100</p>
              <p class="stat-trend positive">↑ 12.5% 较上月</p>
            </div>
          </el-card>
        </div>
        <div class="stat-col">
          <el-card class="stat-card">
            <div class="stat-content">
              <p class="stat-label">热门城市数量</p>
              <p class="stat-value">13个</p>
              <p class="stat-trend positive">↑ 2个 较上月</p>
            </div>
          </el-card>
        </div>
        <div class="stat-col">
          <el-card class="stat-card">
            <div class="stat-content">
              <p class="stat-label">本科要求占比</p>
              <p class="stat-value">68.3%</p>
              <p class="stat-trend negative">↓ 2.1% 较上月</p>
            </div>
          </el-card>
        </div>
        <div class="stat-col">
          <el-card class="stat-card">
            <div class="stat-content">
              <p class="stat-label">热门职位数量</p>
              <p class="stat-value">5个</p>
              <p class="stat-trend positive">↑ 1个 较上月</p>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 分析卡片区域 -->
    <div class="analysis-cards">
      <div class="analysis-row">
        <div class="salary-card">
          <h3>职位分布特征</h3>
          <p>5大数据行业热门职位的分布</p>
          <p>前5个热门职位占总职位数的<span class="highlight">63.2%</span></p>
          <p>大数据开发工程师和大数据工程师位列前二</p>
        </div>
        <div class="salary-card">
          <h3>城市岗位分布</h3>
          <p>Top3城市岗位数量占比达<span class="highlight">85%</span></p>
          <p>北京平均薪资最高，达<span class="highlight">34.2K/月</span></p>
          <p>13个热门城市覆盖92%的岗位需求</p>
        </div>
        <div class="salary-card">
          <h3>学历要求特征</h3>
          <p>本科及以上学历要求占比<span class="highlight">91.5%</span></p>
          <p>算法岗硕士及以上要求达<span class="highlight">78%</span></p>
          <p>数据运营岗学历门槛相对较低</p>
        </div>
      </div>
    </div>

        <!-- 第二行图表：三个等分卡片 -->
    <div class="chart-row second-row">
      <!-- 1. 学历经验分布 -->
      <div class="chart-col third-col">
        <div class="chart-container">
          <div class="chart-header">
            <h3>学历经验分布</h3>
            <p>不同学历和经验要求的岗位占比</p>
          </div>
          <div class="chart">
            <!-- 这里替换为你的学历经验分布图表组件 -->
            <JWJEduPieExpPie />
            <!-- <div style="display: flex; align-items: center; justify-content: center; height: 100%; color: #86909c;">
              学历经验分布图表区域
            </div> -->
          </div>
        </div>
      </div>
      <!-- 2. 学历要求特征 -->
      <div class="chart-col third-col">
        <div class="chart-container">
          <div class="chart-header">
            <h3>学历要求特征</h3>
            <p>不同岗位类型的学历要求差异</p>
          </div>
          <div class="chart">
            <JWJobEduH />
          </div>
        </div>
      </div>
      <!-- 3. 经验要求特征 -->
      <div class="chart-col third-col">
        <div class="chart-container">
          <div class="chart-header">
            <h3>经验要求特征</h3>
            <p>不同岗位的工作经验要求分布</p>
          </div>
          <div class="chart">
            <!-- 这里替换为你的经验要求特征图表组件 -->
            <JWJobExpS />
            <!-- <div style="display: flex; align-items: center; justify-content: center; height: 100%; color: #86909c;">
              经验要求特征图表区域
            </div> -->
          </div>
        </div>
      </div>
    </div>

    <!-- 重构后的图表区域 -->
    <!-- 第一行图表：左右两列布局 -->
    <div class="chart-row first-row">
      <!-- 左列：包含上下两个图表 -->
      <div class="chart-col left-col">
        <!-- 1. 热门职位分布 -->
        <div class="chart-container">
          <div class="chart-header">
            <h3>热门职位分布</h3>
            <p>职位数量最多的前7个职位分布</p>
          </div>
          <div class="chart">
            <JWMultiRingChart />
          </div>
        </div>
        <!-- 2. 热门技能 -->
        <div class="chart-container">
          <div class="chart-header">
            <h3>热门技能</h3>
            <p>岗位需求最高的前10个技能分布</p>
          </div>
          <div class="chart">
            <!-- 这里替换为你的热门技能图表组件 -->
            <JobSkillWord />
            <!-- <div style="display: flex; align-items: center; justify-content: center; height: 100%; color: #86909c;">
              热门技能图表区域
            </div> -->
          </div>
        </div>
      </div>
      <!-- 右列：不同城市岗位分布 -->
      <div class="chart-col right-col">
        <div class="chart-container full-height">
          <div class="chart-header">
            <h3>不同城市岗位分布与薪资水平</h3>
            <p>热门城市岗位数量与平均薪资对比</p>
          </div>
          <div class="chart">
            <JWMap />
          </div>
        </div>
      </div>
    </div>


  </div>
</template>

<script setup>
import { TrendCharts, Refresh, Download } from '@element-plus/icons-vue'
import { ElButton, ElCard, ElMessage } from 'element-plus'
import { ref, onMounted, onUnmounted, nextTick } from 'vue'

// 引入图表组件

import JobSkillWord from '@/chartz/JobSkillWord.vue'
import JWJEduPieExpPie from '@/chartWhite/JWJEduPieExpPie.vue'
import JWJobEduH from '@/chartWhite/JWJobEduH.vue'
import JWJobExpS from '@/chartWhite/JWJobExpS.vue'
import JWMap from '@/chartWhite/JWMap.vue'
import JWMultiRingChart from '@/chartWhite/JWMultiRingChart.vue'
// 刷新数据方法
const refreshData = () => {
  ElMessage.success('数据已刷新')
  // 实际项目中添加刷新逻辑
}

// 导出分析方法
const exportAnalysis = () => {
  ElMessage.success('分析报告导出中...')
  // 实际项目中添加导出逻辑
}
</script>

<style scoped>
/* 核心样式：完全对齐 SalaryDistribution 的视觉风格 */
.edu-requirement {
  padding: 20px 24px;
  background-color: #f5f8ff; /* 统一背景色 */
  min-height: 100vh;
  font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif;
  overflow-x: auto;
}

/* 页面头部样式 - 匹配 SalaryDistribution */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px 24px;
  background: #ffffff; /* 改为纯白背景 */
  border-radius: 8px; /* 统一圆角 */
  border: 1px solid #e5e6eb; /* 统一边框色 */
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05); /* 更柔和的阴影 */
  flex-wrap: wrap;
  gap: 16px;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.title-section h1 {
  margin: 0;
  color: #1c2438; /* 主标题深色 */
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
}

.subtitle {
  margin: 6px 0 0 0;
  color: #86909c; /* 副标题浅灰色 */
  font-size: 14px;
}

/* 快速概览数据卡片 - 统一样式 */
.quick-stats {
  margin-bottom: 20px;
}

.stats-row {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  width: 100%;
}

.stat-col {
  flex: 1;
  min-width: 220px;
}

.stat-card {
  border: 1px solid #e5e6eb; /* 统一边框 */
  border-radius: 8px; /* 统一圆角 */
  background-color: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05); /* 统一阴影 */
  transition: all 0.2s ease; /* 更轻快的过渡 */
  overflow: hidden;
  height: 100%;
}

.stat-card:hover {
  transform: translateY(-2px); /* 更细腻的悬浮效果 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); /* 悬浮阴影 */
  border-color: #4096ff; /* 悬浮边框高亮 */
}

.stat-content {
  padding: 16px 20px; /* 统一内边距 */
}

.stat-label {
  margin: 0 0 8px 0;
  color: #86909c; /* 标签文字色 */
  font-size: 14px;
}

.stat-value {
  margin: 0 0 8px 0;
  font-size: 28px; /* 更大的数值字体 */
  font-weight: 700;
  color: #1c2438; /* 数值文字色 */
}

.stat-trend {
  margin: 0;
  font-size: 13px;
  display: flex;
  align-items: center;
}

.stat-trend.positive {
  color: #00b42a; /* 正增长色 - 匹配 SalaryDistribution */
}

.stat-trend.negative {
  color: #f53f3f; /* 负增长色 - 匹配 SalaryDistribution */
}

/* 分析卡片样式 - 统一视觉 */
.analysis-cards {
  margin-bottom: 20px;
  width: 100%;
}

.analysis-row {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  width: 100%;
}

.salary-card {
  background: #ffffff;
  border: 1px solid #e5e6eb;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
  flex: 1;
  min-width: 280px;
}

.salary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: #4096ff;
}

.salary-card h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #1c2438;
  border-bottom: 2px solid #4096ff; /* 主色调下划线 */
  padding-bottom: 8px;
  font-weight: 600;
}

.salary-card p {
  font-size: 14px;
  color: #4e5969;
  margin: 8px 0;
  line-height: 1.5;
}

.highlight {
  color: #4096ff; /* 高亮色 - 主色调 */
  font-weight: 600;
}

/* 重构后的图表布局样式 */
.chart-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  width: 100%;
  flex-wrap: wrap;
}

/* 第一行：左右两列 */
.first-row .chart-col {
  flex: 1;
  min-width: 300px;
}

.first-row .left-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.first-row .right-col .chart-container {
  height: 700px;
}

/* 第二行：三等分列 */
.second-row .third-col {
  flex: 1;
  min-width: 280px;
}

/* 图表容器样式 - 统一风格 */
.chart-container {
  background: #ffffff;
  border: 1px solid #e5e6eb;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  padding: 20px;
  transition: all 0.2s ease;
  height: 320px; /* 统一图表高度 */
  display: flex;
  flex-direction: column;
}

.chart-container.full-height {
  height: 100%;
}

.chart-container:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: #4096ff;
}

.chart-header {
  margin-bottom: 16px;
  flex-shrink: 0;
}

.chart-header h3 {
  margin: 0 0 4px 0;
  color: #1c2438;
  font-size: 16px;
  font-weight: 600;
}

.chart-header p {
  margin: 0;
  color: #86909c;
  font-size: 13px;
}

.chart {
  width: 100% !important;
  flex: 1;
  min-height: 0; /* 解决flex子元素高度计算问题 */
}

/* 响应式设计 - 保持一致 */
@media (max-width: 1440px) {
  .analysis-row {
    flex-wrap: wrap;
  }
  
  .chart-row {
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .chart-container {
    height: 360px;
    width: 100%;
    min-width: unset;
  }

  .chart-row {
    flex-direction: column;
  }

  .first-row .left-col,
  .first-row .right-col,
  .second-row .third-col {
    width: 100%;
    min-width: unset;
  }

  .analysis-row {
    flex-direction: column;
  }

  .salary-card {
    min-width: unset;
    width: 100%;
  }

  .stats-row {
    flex-direction: column;
  }

  .stat-col {
    min-width: unset;
    width: 100%;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .action-buttons {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>