<template>
  <div class="detail-container">
    <div class="detail-header">
      <h3 class="detail-title">{{ title }}</h3>
      <div class="score-display">总分: <span class="score-value">{{ totalScore }}分</span></div>
    </div>
    
    <div class="detail-content">
      <div class="chart-section">
        <BarChart :chartData="barChartData" :chartId="`bar-chart-${uniqueId}`" />
      </div>
      
      <div class="detail-text">
        <p>{{ detailContent }}</p>
      </div>
      
      <div class="action-section" v-if="hasActions">
        <router-link :to="actionRoute" class="action-btn">查看完整分析</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import BarChart from './BarChart.vue'

export default {
  name: 'DetailContainer',
  components: {
    BarChart
  },
  props: {
    title: {
      type: String,
      required: true
    },
    categories: {
      type: Array,
      required: true
    },
    values: {
      type: Array,
      required: true
    },
    detailContent: {
      type: String,
      required: true
    },
    uniqueId: {
      type: [String, Number],
      required: true
    },
    actionRoute: {
      type: String,
      default: ''
    }
  },
  setup(props) {
    const barChartData = computed(() => ({
      categories: props.categories,
      values: props.values
    }))
    
    const totalScore = computed(() => {
      if (!props.values.length) return 0
      const sum = props.values.reduce((acc, curr) => acc + curr, 0)
      return Math.round(sum / props.values.length)
    })
    
    const hasActions = computed(() => !!props.actionRoute)
    
    return {
      barChartData,
      totalScore,
      hasActions
    }
  }
}
</script>

<style scoped>
.detail-container {
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 0;
  margin-bottom: 20px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.detail-container:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: #f9f9f9;
  border-bottom: 1px solid #f0f0f0;
}

.detail-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #444;
  position: relative;
  padding-left: 12px;
}

.detail-title:before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background-color: #1E90FF;
  border-radius: 2px;
}

.score-display {
  font-size: 14px;
  color: #666;
}

.score-value {
  font-weight: bold;
  color: #1E90FF;
  font-size: 16px;
}

.detail-content {
  padding: 15px;
  display: flex;
  flex-direction: column;
}

.chart-section {
  height: 250px;
  margin-bottom: 15px;
}

.detail-text {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 15px;
  color: #555;
  line-height: 1.5;
}

.action-section {
  text-align: center;
  margin-top: 10px;
}

.action-btn {
  display: inline-block;
  background-color: #1E90FF;
  color: white;
  padding: 8px 20px;
  border-radius: 20px;
  text-decoration: none;
  font-size: 14px;
  transition: background-color 0.2s ease;
}

.action-btn:hover {
  background-color: #1a7fd1;
}
</style> 