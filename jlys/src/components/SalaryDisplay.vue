<template>
  <div class="salary-display" :class="salaryLevelClass">
    <span v-if="displayText" class="salary-text">
      {{ displayText }}
      <span v-if="showUnit && displayText !== '暂无'" class="salary-unit">{{ unitText }}</span>
    </span>
    <span v-else class="salary-default">暂无</span>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  job: {
    type: Object,
    required: true
  },
  displayMode: {
    type: String,
    default: 'simple', // 'full', 'simple', 'range-only'
    validator: (value) => ['full', 'simple', 'range-only'].includes(value)
  },
  showUnit: {
    type: Boolean,
    default: true
  }
})

// 计算显示文本
const displayText = computed(() => {
  const { job, displayMode } = props
  
  // 优先级：salary > minSalary+maxSalary > avgSalary > 暂无
  if (job.salary) {
    return job.salary
  }
  
  if (job.minSalary && job.maxSalary) {
    const range = `${job.minSalary}K-${job.maxSalary}K`
    if (displayMode === 'range-only') {
      return range
    }
    return range
  }
  
  if (job.minSalary && !job.maxSalary) {
    return `${job.minSalary}K以上`
  }
  
  if (!job.minSalary && job.maxSalary) {
    return `${job.maxSalary}K以下`
  }
  
  if (job.avgSalary && displayMode !== 'range-only') {
    return `${job.avgSalary}K`
  }
  
  return null
})

// 计算单位文本
const unitText = computed(() => {
  if (!props.showUnit) return ''
  
  const text = displayText.value
  if (!text || text === '暂无') return ''
  
  // 如果已经包含单位，不重复添加
  if (text.includes('K') || text.includes('k') || text.includes('万') || text.includes('薪')) {
    return ''
  }
  
  return '/月'
})

// 计算薪资水平样式类
const salaryLevelClass = computed(() => {
  const { job } = props
  
  // 获取最高薪资用于判断等级
  let maxSalary = 0
  
  if (job.maxSalary) {
    maxSalary = job.maxSalary
  } else if (job.minSalary) {
    maxSalary = job.minSalary
  } else if (job.avgSalary) {
    maxSalary = job.avgSalary
  }
  
  if (maxSalary >= 50) {
    return 'salary-high'
  } else if (maxSalary >= 25) {
    return 'salary-medium'
  } else if (maxSalary > 0) {
    return 'salary-low'
  }
  
  return 'salary-default'
})
</script>

<style scoped>
.salary-display {
  display: inline-flex;
  align-items: center;
  font-weight: 500
}

.salary-text {
  font-size: 14px
}

.salary-unit {
  font-size: 12px;
  opacity: 0.8;
  margin-left: 1px
}

.salary-default {
  color: #9ca3af;
  font-size: 14px;
}

/* 薪资水平颜色 */
.salary-high .salary-text {
  color: #dc2626; /* 红色 - 高薪 */
}

.salary-medium .salary-text {
  color: #ea580c; /* 橙色 - 中薪 */
}

.salary-low .salary-text {
  color: #16a34a; /* 绿色 - 低薪 */
}

.salary-default .salary-text {
  color: #6b7280; /* 灰色 - 默认 */
}

/* 不同显示模式的样式调整 */
.salary-display.full .salary-text {
  font-size: 16px;
  font-weight: 600;
}

.salary-display.simple .salary-text {
  font-size: 14px;
  font-weight: 500;
}

.salary-display.range-only .salary-text {
  font-size: 13px;
  font-weight: 400;
}
</style>
