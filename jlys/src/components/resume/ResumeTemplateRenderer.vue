<template>
  <div class="resume-template-renderer">
    <!-- 根据template字段动态渲染不同的模板 -->
    <ResumeTemplate1 v-if="template === 'template1'" :resume="resume" />
    <ResumeTemplate2 v-else-if="template === 'template2'" :resume="resume" />
    <ResumeTemplate3 v-else-if="template === 'template3'" :resume="resume" />
    <ResumeTemplate4 v-else-if="template === 'template4'" :resume="resume" />
    <!-- 默认使用template1 -->
    <ResumeTemplate1 v-else :resume="resume" />
  </div>
</template>

<script>
import { defineProps } from 'vue'
import ResumeTemplate1 from './ResumeTemplate1.vue'
import ResumeTemplate2 from './ResumeTemplate2.vue'
import ResumeTemplate3 from './ResumeTemplate3.vue'
import ResumeTemplate4 from './ResumeTemplate4.vue'

export default {
  name: 'ResumeTemplateRenderer',
  components: {
    ResumeTemplate1,
    ResumeTemplate2,
    ResumeTemplate3,
    ResumeTemplate4
  },
  props: {
    resume: {
      type: Object,
      required: true
    },
    template: {
      type: String,
      default: 'template1'
    }
  }
}
</script>

<style scoped>
.resume-template-renderer {
  width: 100%;
  height: 100%;
}
</style>
