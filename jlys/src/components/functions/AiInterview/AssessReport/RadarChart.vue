<template>
  <div class="radar-chart-wrapper">
    <div :id="chartId" class="radar-chart-container"></div>
    <div class="legend-bottom" v-if="showAverage">
      <div class="average-score">{{ averageScore }}</div>
      <div class="average-label">总体得分</div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch, computed } from 'vue'
import * as echarts from 'echarts'

export default {
  name: 'RadarChart',
  props: {
    chartData: {
      type: Object,
      required: true,
      default: () => ({
        indicators: [],
        values: []
      })
    },
    chartId: {
      type: String,
      required: true
    },
    showAverage: {
      type: Boolean,
      default: true
    }
  },
  setup(props) {
    const chartInstance = ref(null)
    
    const averageScore = computed(() => {
      if (!props.chartData.values || props.chartData.values.length === 0) return 0
      const sum = props.chartData.values.reduce((acc, val) => acc + val, 0)
      return Math.round(sum / props.chartData.values.length)
    })
    
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
          trigger: 'item'
        },
        radar: {
          shape: 'circle',
          splitNumber: 4,
          radius: '65%',
          center: ['50%', '50%'],
          axisName: {
            color: '#666',
            fontSize: 12,
            padding: [5, 7]
          },
          nameGap: 15,
          splitArea: {
            areaStyle: {
              color: ['rgba(255, 255, 255, 0.9)', 'rgba(240, 240, 240, 0.9)']
            }
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(200, 200, 200, 0.5)'
            }
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(200, 200, 200, 0.5)'
            }
          },
          indicator: props.chartData.indicators.map(name => ({
            name: name,
            max: 100
          }))
        },
        series: [{
          type: 'radar',
          emphasis: {
            lineStyle: {
              width: 4
            }
          },
          data: [{
            value: props.chartData.values,
            name: '能力值',
            symbol: 'circle',
            symbolSize: 6,
            areaStyle: {
              color: new echarts.graphic.RadialGradient(0.5, 0.5, 1, [
                {
                  color: 'rgba(30, 144, 255, 0.7)',
                  offset: 0
                },
                {
                  color: 'rgba(30, 144, 255, 0.2)',
                  offset: 1
                }
              ])
            },
            lineStyle: {
              color: '#1E90FF',
              width: 2
            },
            itemStyle: {
              color: '#1E90FF'
            }
          }]
        }]
      }
      
      chartInstance.value.setOption(option)
    }
    
    onMounted(() => {
      initChart()
      
      window.addEventListener('resize', () => {
        if (chartInstance.value) {
          chartInstance.value.resize()
          setTimeout(() => {
            updateChart()
          }, 100)
        }
      })
    })
    
    watch(() => props.chartData, () => {
      updateChart()
    }, { deep: true })
    
    return {
      averageScore
    }
  }
}
</script>

<style scoped>
.radar-chart-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.radar-chart-container {
  width: 100%;
  height: 85%;
}

.legend-bottom {
  width: 100%;
  text-align: center;
  margin-top: 10px;
  padding-top: 5px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.average-score {
  font-size: 28px;
  font-weight: bold;
  color: #1E90FF;
  line-height: 1.2;
}

.average-label {
  font-size: 12px;
  color: #666;
}
</style> 