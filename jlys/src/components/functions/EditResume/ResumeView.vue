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
  height: 100%;
  background: #f5f5f5;
  overflow: hidden; /* 禁止滚动条 */
}
</style>