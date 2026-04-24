<template>
  <div class="resume-template-renderer">
    <!-- 根据模板ID动态渲染不同的模板 -->
    <ResumeTemplate1 v-if="templateId === 1" :resume="resume" />
    <ResumeTemplate2 v-else-if="templateId === 2" :resume="resume" />
    <ResumeTemplate3 v-else-if="templateId === 3" :resume="resume" />
    <!-- 默认使用template1 -->
<!--    <ResumeTemplate1 v-else :resume="resume" />-->
  </div>
</template>

<script>
import { computed } from 'vue'
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
      type: [String, Number],
      default: 'template1'
    }
  },
  setup(props) {
    const templateId = computed(() => {
      let id = 1
      if (typeof props.template === 'number') id = props.template
      else if (typeof props.template === 'string' && /^\d+$/.test(props.template)) {
        id = parseInt(props.template)
      } else {
        const nameMap = {
          'IT专业简历': 1,
          '中文简历模板': 2,
          '带照片简历': 3,
          'template1': 1,
          'template2': 2,
          'template3': 3
        }
        id = nameMap[props.template] || 1
      }
      console.log('模板ID:', id, '原始template:', props.template)  // 调试
      return id
    })
    return {templateId}
  }
}
</script>

<style scoped>
.resume-template-renderer {
  width: 100%;
  height: 100%;
}
</style>