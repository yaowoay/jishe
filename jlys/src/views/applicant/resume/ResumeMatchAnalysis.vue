<template>
  <div class="resume-match-analysis-wrapper">
    <ResumeAnalysis />
  </div>
</template>

<script>
import ResumeAnalysis from '@/components/resumes/ResumeAnalysis.vue'

export default {
  name: 'ResumeMatchAnalysisView',
  components: {
    ResumeAnalysis
  }
}
</script>

<style>
/* 确保样式不被覆盖 */
.resume-match-analysis-wrapper {
  width: 100%;
  min-height: 100vh;
  background: #f5f7fa;
  overflow-x: hidden;
}

/* 重置 Element Plus 可能的干扰 */
.resume-match-analysis-wrapper .el-main {
  padding: 0 !important;
}

.resume-match-analysis-wrapper * {
  box-sizing: border-box;
}
</style>
