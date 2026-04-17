<template>
  <div class="radar-chart-wrapper">
    <div ref="radarChart" class="radar-chart-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

// 更新的模拟数据
const industryData = ref({
  categories: ['北京', '上海', '深圳', '杭州', '广州'],
  series: [
    {
      name: '金融AI',
      data: [185.25, 56.76, 36.69, 57.33, 62.40]
    },
    {
      name: '在线社交/媒体',
      data: [97.57, 66.88, 63.71, 107.25, 46.88]
    },
    {
      name: '互联网/电商',
      data: [86.46, 59.58, 82.18, 78.26, 65.42]
    },
    {
      name: '人力资源服务',
      data: [66.98, 85.18, 105.76, 51.99, 51.75]
    },
    {
      name: '新能源',
      data: [150.28, 57.75, 61.05, 40.00, 26.13]
    }
  ]
})

const radarChart = ref(null)
let chartInstance = null

const radarOption = ref({
  title: {
    text: '城市-行业薪资对比',
    left: 'left',
    top: 1,
    textStyle: {
      color: '#fff',
      fontSize: 20,
      fontWeight: 'bold'
    }
  },
  tooltip: {
    trigger: 'item',
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    borderColor: '#333',
    textStyle: {
      color: '#fff',
      fontSize: 18
    },
    formatter: params => {
      return `${params.seriesName}<br/>${params.name}: ${params.value}K`
    }
  },
  legend: {
    data: industryData.value.series.map(item => item.name),
    right: 10,
    itemGap: 12,
    orient: 'vertical',
    textStyle: {
      color: '#fff',
      fontSize: 16
    },
    itemWidth: 18,
    itemHeight: 15
  },
  radar: {
    indicator: industryData.value.categories.map(name => ({
      name,
      max: 180, // Adjusted max value to fit data range
      color: '#fff'
    })),
    radius: '75%',
    center: ['50%', '50%'],
    splitNumber: 4,
    splitArea: {
      show: false
    },
    axisName: {
      color: '#fff',
      fontSize: 12,
      padding: [5, 0]
    },
    splitLine: {
      lineStyle: {
        color: 'rgba(255, 255, 255, 0.3)',
        width: 1
      }
    },
    axisLine: {
      lineStyle: {
        color: 'rgba(255, 255, 255, 0.3)',
        width: 1
      }
    }
  },
  series: industryData.value.series.map((item, index) => ({
    name: item.name,
    type: 'radar',
    data: [{
      value: item.data,
      symbol: 'circle',
      symbolSize: 6,
      itemStyle: {
        color: ['#FF6E76', '#FDDD60', '#58D9F9', '#A1D1F9', '#B1A0D9'][index] // 点颜色与行业对应
      },
      lineStyle: {
        width: 2,
        color: ['#FF6E76', '#FDDD60', '#58D9F9', '#A1D1F9', '#B1A0D9'][index] // Line color matches industry
      }
    }],
    color: ['#FF6E76', '#FDDD60', '#58D9F9', '#A1D1F9', '#B1A0D9'][index] // Assigning color based on index
  })),
  backgroundColor: 'transparent'
})

const initChart = () => {
  if (!radarChart.value) return
  
  chartInstance = echarts.init(radarChart.value)
  chartInstance.setOption(radarOption.value)
}

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.radar-chart-container {
  width: 100%;
  height: 100%;
  min-height: 300px; /* 最小高度保证 */
}
</style>