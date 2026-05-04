<template>
  <div class="resume-analysis-view">
    <ResumeAnalysis />
  </div>
</template>

<script>
import ResumeAnalysis from '@/components/resumes/ResumeAnalysis.vue'

export default {
  name: 'ResumeAnalysisView',
  components: {
    ResumeAnalysis
  }
}
</script>

<style scoped>
.resume-analysis-view {
  width: 100%;
  height: 100%;
  overflow-y: auto;
}
</style>
