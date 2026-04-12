<template>
  <div class="test-page">
    <div class="page-header">
      <h1>面试评估报告 V1.0</h1>
      <p class="page-subtitle">基于dify工作流的评估报告</p>
      <div class="version-switch">
        <router-link to="/test-assess-report-v2" class="switch-btn">
          切换到 V2.0 版本 →
        </router-link>
      </div>
    </div>
    <AssessReportView />
  </div>
</template>

<script setup>
import AssessReportView from '@/components/functions/AiInterview/AssessReport/AssessReportView.vue'
</script>

<style scoped>
.test-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-header {
  text-align: center;
  padding: 40px 20px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  position: relative;
}

h1 {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 10px 0;
  text-shadow: 1px 1px 3px rgba(0,0,0,0.3);
}

.page-subtitle {
  font-size: 16px;
  margin: 0 0 20px 0;
  opacity: 0.9;
  font-weight: 400;
}

.version-switch {
  margin-top: 20px;
}

.switch-btn {
  display: inline-block;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  text-decoration: none;
  padding: 12px 24px;
  border-radius: 25px;
  font-weight: 500;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.switch-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
  color: white;
  text-decoration: none;
}

/* 为AssessReportView组件添加容器样式 */
:deep(.assess-report-view) {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 30px 15px 15px;
  }
  
  h1 {
    font-size: 24px;
  }
  
  .page-subtitle {
    font-size: 14px;
  }
  
  .switch-btn {
    padding: 10px 20px;
    font-size: 14px;
  }
}
</style>
