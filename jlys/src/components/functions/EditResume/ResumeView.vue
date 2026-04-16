<template>
  <div class="resume-module">
    <!-- 简历功能模块容器 - 整合了所有简历相关功能 -->
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'ResumeView'
}
</script>

<style scoped>
/* 简历模块容器样式 */
.resume-module {
  width: 100%;
  min-height: 100%;
  background: #f5f5f5;
  overflow-y: auto; /* 允许垂直滚动 */
  overflow-x: hidden; /* 禁止水平滚动 */
}
</style>