<template>
  <div class="resume-template-renderer">
    <!-- 根据模板ID动态渲染不同的模板 -->
    <ResumeTemplate1 v-if="templateId === 1" :resume="resume" />
    <ResumeTemplate2 v-else-if="templateId === 2" :resume="resume" />
    <ResumeTemplate3 v-else-if="templateId === 3" :resume="resume" />
    <!-- 默认使用template1 -->
    <ResumeTemplate1 v-else :resume="resume" />
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
    // 将template转换为模板ID
    const templateId = computed(() => {
      // 如果是数字ID
      if (typeof props.template === 'number') return props.template
      // 如果是字符串"1"、"2"、"3"
      if (typeof props.template === 'string' && /^\d+$/.test(props.template)) {
        return parseInt(props.template)
      }
      // 根据模板名称映射
      const nameMap = {
        'IT专业简历': 1,
        '中文简历模板': 2,
        '带照片简历': 3,
        'template1': 1,
        'template2': 2,
        'template3': 3
      }
      return nameMap[props.template] || 1
    })

    return { templateId }
  }
}
</script>

<style scoped>
.resume-template-renderer {
  width: 100%;
  height: 100%;
}
</style>