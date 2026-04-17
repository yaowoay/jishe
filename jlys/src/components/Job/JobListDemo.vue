<template>
  <div class="job-list-demo">
    <div class="demo-header">
      <h1>职位列表 - 高级筛选演示</h1>
      <p>体验全新的职位筛选功能，快速找到心仪的工作机会</p>
    </div>
    
    <!-- 功能特性展示 -->
    <div class="features-showcase">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="feature-card">
            <el-icon class="feature-icon" color="#409eff"><Filter /></el-icon>
            <h3>智能筛选</h3>
            <p>多维度筛选条件，精准匹配</p>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="feature-card">
            <el-icon class="feature-icon" color="#67c23a"><Money /></el-icon>
            <h3>薪资范围</h3>
            <p>滑块选择，直观便捷</p>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="feature-card">
            <el-icon class="feature-icon" color="#e6a23c"><Location /></el-icon>
            <h3>地域选择</h3>
            <p>覆盖全国主要城市</p>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="feature-card">
            <el-icon class="feature-icon" color="#f56c6c"><Star /></el-icon>
            <h3>个性化</h3>
            <p>根据偏好推荐职位</p>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 职位列表组件 -->
    <JobList />
  </div>
</template>

<script setup>
import JobList from './JobList.vue'
import { Filter, Money, Location, Star } from '@element-plus/icons-vue'
</script>

<style scoped>
.job-list-demo {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.demo-header {
  text-align: center;
  margin-bottom: 40px;
  color: white;
}

.demo-header h1 {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 16px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.demo-header p {
  font-size: 18px;
  opacity: 0.9;
  margin: 0;
}

.features-showcase {
  margin-bottom: 40px;
}

.feature-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
}

.feature-icon {
  font-size: 32px;
  margin-bottom: 16px;
}

.feature-card h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #303133;
}

.feature-card p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}
</style>
