<template>
  <div class="test-page">
    <div class="page-header">
      <h1>面试综合评估报告 V2.0</h1>
      <p class="page-subtitle">基于dify工作流的可视化评估报告</p>
    </div>
    <AssessReportView2 />
  </div>
</template>

<script setup>
import AssessReportView2 from '@/components/functions/AiInterview/AssessReport/AssessReportView2.vue'
</script>

<style scoped>
.test-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.page-header {
  text-align: center;
  padding: 40px 20px 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

h1 {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin: 0 0 10px 0;
  text-shadow: 1px 1px 3px rgba(0,0,0,0.1);
}

.page-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
  font-weight: 400;
}
</style>
