<template>
  <div :id="chartId" class="bar-chart-container"></div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import * as echarts from 'echarts'

export default {
  name: 'BarChart',
  props: {
    chartData: {
      type: Object,
      required: true,
      default: () => ({
        categories: [],
        values: []
      })
    },
    chartId: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const chartInstance = ref(null)
    
    const initChart = () => {
      const chartDom = document.getElementById(props.chartId)
      if (!chartDom) return
      
      chartInstance.value = echarts.init(chartDom)
      updateChart()
    }
    
    const updateChart = () => {
      if (!chartInstance.value) return
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            data: props.chartData.categories,
            axisTick: {
              alignWithLabel: true
            },
            axisLabel: {
              interval: 0,
              rotate: 30
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
            max: 100
          }
        ],
        series: [
          {
            name: '得分',
            type: 'bar',
            barWidth: '60%',
            data: props.chartData.values.map((value, index) => ({
              value,
              itemStyle: {
                color: value >= 80 ? '#67C23A' : value >= 60 ? '#E6A23C' : '#F56C6C'
              }
            }))
          }
        ]
      }
      
      chartInstance.value.setOption(option)
    }
    
    onMounted(() => {
      initChart()
      
      window.addEventListener('resize', () => {
        chartInstance.value?.resize()
      })
    })
    
    watch(() => props.chartData, () => {
      updateChart()
    }, { deep: true })
    
    return {}
  }
}
</script>

<style scoped>
.bar-chart-container {
  width: 100%;
  height: 100%;
  min-height: 250px;
}
</style> 