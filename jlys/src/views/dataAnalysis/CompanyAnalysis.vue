<template>
  <div class="dashboard-container">
    <!-- 页面标题 - 调整为EduRequirement风格 -->
    <div class="page-header">
      <div class="title-section">
        <h1>
          <el-icon><Building /></el-icon>
          招聘企业分析
        </h1>
        <p class="subtitle">基于市场数据的企业维度深度分析</p>
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

    <!-- 头部数据卡片区域 - 保持布局调整样式 -->
    <div class="data-cards">
      <!-- 行业技能差异卡片 -->
      <!-- <div class="card">
        <h3>行业技能差异</h3>
        <div class="card-content">
          <div class="industry-row">
            <span class="industry">金融AI</span>
            <span class="skills">风险管理, 量化分析, 时间序列</span>
          </div>
          <div class="industry-row">
            <span class="industry">教育科技</span>
            <span class="skills">个性化推荐, NLP, 自适应学习</span>
          </div>
        </div>
      </div> -->

      <!-- 企业规模概览 -->
      <div class="card">
        <h3>企业规模概览</h3>
        <div class="card-content">
          <div class="card-item">
            <span class="label">大型企业</span>
            <span class="value">42%</span>
            <span class="trend positive">招聘主力</span>
          </div>
          <div class="card-item">
            <span class="label">中型企业</span>
            <span class="value">31%</span>
            <span class="trend">稳定增长</span>
          </div>
          <div class="card-item">
            <span class="label">小型企业</span>
            <span class="value">21%</span>
            <span class="trend">潜力较大</span>
          </div>
          <div class="card-item">
            <span class="label">微型/超大型</span>
            <span class="value">6%</span>
            <span class="trend">占比相对较小</span>
          </div>
        </div>
      </div>

      <!-- 企业类型概览 -->
      <div class="card">
        <h3>企业类型概览</h3>
        <div class="card-content">
          <div class="card-item highlight">
            <span class="label">民营企业</span>
            <span class="value">68%</span>
            <span class="trend positive">主导招聘市场</span>
          </div>
          <div class="card-item">
            <span class="label">外企</span>
            <span class="value">22%</span>
            <span class="trend">高薪岗位集中</span>
          </div>
          <div class="card-item">
            <span class="label">国企</span>
            <span class="value">10%</span>
            <span class="trend">稳定性突出</span>
          </div>
        </div>
      </div>

      <!-- 热门福利 -->
      <div class="card">
        <h3>热门企业福利</h3>
        <div class="card-content">
          <div class="skill-tags">
            <span class="tag primary">五险一金 (96%)</span>
            <span class="tag primary">年终奖金 (89%)</span>
            <span class="tag">弹性工作 (78%)</span>
            <span class="tag">股票期权 (65%)</span>
            <span class="tag">补充医疗 (58%)</span>
            <span class="tag">带薪年假 (52%)</span>
          </div>
        </div>
      </div>

      <!-- 大厂高薪概况 -->
      <div class="card">
        <h3>大厂高薪概况</h3>
        <div class="card-content">
          <div class="card-item">
            <span class="label">平均最高薪资</span>
            <span class="value">¥50k/月</span>
            <span class="trend"></span>
          </div>
          <div class="card-item">
            <span class="label">最高薪资岗位</span>
            <span class="value">AI与大数据分析工程师</span>
            <span class="trend positive">¥66k/月</span>
          </div>
          <div class="card-item">
            <span class="label">高薪岗位数量</span>
            <span class="value">1,254个</span>
            <span class="trend">10000人以上企业</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增图表区域：企业特征分析 -->
    <div class="enhanced-charts-section">
      <div class="chart-row">
        <div class="chart-wrapper">
          <h3>企业规模招聘占比</h3>
          <CWComScaleTypePie class="chart" />
        </div>
        <div class="chart-wrapper">
          <h3>企业类型岗位分布</h3>
          <CWComTypePie class="chart" />
        </div>
        <div class="chart-wrapper">
          <h3>企业福利词云分析</h3>
          <ComWelfareWord class="chart" />
        </div>
      </div>

      <div class="chart-row">
        <div class="chart-wrapper full-width">
          <h3>企业大厂招聘高薪职位详情</h3>
          <CWComDetailTable class="chart" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 导入 Element Plus 图标和组件
import { Building, Refresh, Download } from '@element-plus/icons-vue'
import { ElButton, ElMessage } from 'element-plus'
import { ref } from 'vue'

// 导入图表组件（统一路径大小写，保持原有引入关系）
import ComWelfareWord from '@/chartz/ComWelfareWord.vue'
import CWComTypePie from '@/chartWhite/CWComTypePie.vue'
import CWComScaleTypePie from '@/chartWhite/CWComScalePie.vue'
import CWComDetailTable from '@/chartWhite/CWComDetailTable.vue'

// 刷新数据方法（无需return，setup语法糖中直接暴露）
const refreshData = () => {
  ElMessage.success('数据已刷新')
  // 实际项目中添加刷新逻辑
}

// 导出分析报告方法
const exportAnalysis = () => {
  ElMessage.success('分析报告导出中...')
  // 实际项目中添加导出逻辑
}
</script>

<style scoped>
/* 核心样式：对齐EduRequirement的视觉风格 */
.dashboard-container {
  padding: 20px 24px;
  background-color: #f5f8ff; /* 统一浅蓝背景色 */
  min-height: 100vh;
  font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif;
  overflow-x: auto;
}

/* 页面头部样式 - 匹配EduRequirement */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px 24px;
  background: #ffffff; /* 纯白背景 */
  border-radius: 8px; /* 统一圆角 */
  border: 1px solid #e5e6eb; /* 统一边框色 */
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05); /* 柔和阴影 */
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

/* 数据卡片区域 - 调整为EduRequirement风格 */
.data-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.card {
  background: #ffffff;
  border: 1px solid #e5e6eb; /* 统一边框 */
  border-radius: 8px; /* 统一圆角 */
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
}

.card:hover {
  transform: translateY(-2px); /* 细腻悬浮效果 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: #4096ff; /* 主色调边框 */
}

.card h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #1c2438;
  border-bottom: 2px solid #4096ff; /* 主色调下划线 */
  padding-bottom: 8px;
  font-weight: 600;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.card-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #e5e6eb; /* 浅色分隔线 */
}

.card-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 14px;
  color: #86909c; /* 标签文字色 */
  width: 40%;
}

.value {
  font-size: 16px;
  font-weight: 600;
  color: #1c2438; /* 数值文字色 */
  width: 30%;
  text-align: right;
}

.trend {
  font-size: 13px;
  color: #86909c;
  width: 30%;
  text-align: right;
}

.trend.positive {
  color: #00b42a; /* 正增长色 - 匹配EduRequirement */
}

.trend.negative {
  color: #f53f3f; /* 负增长色 - 匹配EduRequirement */
}

.highlight {
  background-color: rgba(64, 150, 255, 0.05); /* 主色调浅色背景 */
  padding: 8px;
  border-radius: 6px;
}

.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  background: #f0f2f5; /* 浅灰色标签背景 */
  color: #4e5969;
  padding: 4px 10px;
  border-radius: 16px;
  font-size: 12px;
}

.tag.primary {
  background: rgba(64, 150, 255, 0.1); /* 主色调标签背景 */
  color: #4096ff; /* 主色调文字 */
}

.industry-row {
  display: flex;
  margin-bottom: 8px;
}

.industry {
  font-weight: 600;
  color: #4096ff; /* 主色调行业名称 */
  width: 80px;
}

.skills {
  color: #4e5969;
  font-size: 14px;
}

/* 图表区域样式 - 统一为EduRequirement风格 */
.charts-container {
  margin-top: 20px;
  margin-bottom: 20px;
}

.chart-row {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  width: 100%;
}

.chart-wrapper {
  background: #ffffff;
  border: 1px solid #e5e6eb;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
  flex: 1;
  min-width: 280px;
  height: 320px;
  display: flex;
  flex-direction: column;
}

.chart-wrapper:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: #4096ff;
}

.chart-wrapper h3 {
  margin: 0 0 16px 0;
  color: #1c2438;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 2px solid #4096ff;
  padding-bottom: 8px;
  flex-shrink: 0;
}

/* 图表/表格内容容器样式 */
.chart-wrapper .chart {
  flex: 1;
  width: 100%;
  min-height: 0; /* 解决flex布局下高度计算问题 */
}

/* 新增图表区域样式 */
.enhanced-charts-section {
  margin-top: 20px;
}

.enhanced-charts-section h2 {
  color: #1c2438;
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: 600;
  padding: 16px 20px;
  background: #ffffff;
  border: 1px solid #e5e6eb;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.enhanced-charts-section .chart-row {
  margin-bottom: 20px;
}

/* 全屏宽度的图表容器样式 */
.chart-wrapper.full-width {
  flex: 0 0 100%;
  min-width: unset;
  height: 600px; /* 调整为合适的高度，让表格完整显示 */
}

/* 响应式设计 - 保持与EduRequirement一致 */
@media (max-width: 1440px) {
  .chart-row {
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .chart-wrapper {
    width: 100%;
    min-width: unset;
    margin-bottom: 16px;
    height: 300px;
  }

  /* 移动端全屏宽度图表高度调整 */
  .chart-wrapper.full-width {
    height: 500px;
  }

  .data-cards {
    grid-template-columns: 1fr;
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