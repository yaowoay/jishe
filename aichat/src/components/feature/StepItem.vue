<template>
    <div class="step-item">
      <div class="step-number">{{ number }}</div>
      <div class="step-content">
        <h3>{{ title }}</h3>
        <p class="step-desc">{{ description }}</p>
        <ul class="step-points">
          <li v-for="(point, index) in points" :key="index">
            <el-icon><Check /></el-icon>
            <span>{{ point }}</span>
          </li>
        </ul>
      </div>
    </div>
  </template>
  
  <script>
  import { Check } from '@element-plus/icons-vue'
  
  export default {
    name: 'StepItem',
    components: {
      Check
    },
    props: {
      number: {
        type: String,
        required: true
      },
      title: {
        type: String,
        required: true
      },
      description: {
        type: String,
        required: true
      },
      points: {
        type: Array,
        required: true
      }
    }
  }
  </script>