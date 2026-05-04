<template>
  <div class="salary-chart-container" ref="chartRef"></div>
</template>

<script setup>
import { ref, onMounted, watchEffect } from 'vue'
import * as echarts from 'echarts'

const chartRef = ref(null)
let chartInstance = null

// 近4年毕业去向落实率（真实模拟，逐年上升）
const generateData = () => {
  const years = ['2023', '2024', '2025', '2026']
  const rates = {
    '全校落实率': [86.2, 88.5, 90.1, 91.8],
    '工科落实率': [89.5, 91.2, 92.8, 94.3],
    '文科落实率': [82.1, 84.6, 86.5, 88.2],
    '理科落实率': [85.3, 87.7, 89.4, 91.0]
  }
  return { years, rates }
}

const initChart = () => {
  if (chartInstance) chartInstance.dispose()
  chartInstance = echarts.init(chartRef.value)

  const { years, rates } = generateData()

  const option = {
    title: {
      text: '近四年毕业去向落实率',
      left: 'center',
      top: '0%',
      textStyle: {
        color: '#66ccff',
        fontSize: 13
      }
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(10, 25, 47, 0.8)',
      borderColor: 'rgba(15, 222, 255, 0.3)',
      borderWidth: 1,
      textStyle: { color: '#C5D6E6' },
      formatter: function(params) {
        let result = `${params[0].name}<br/>`
        params.forEach(item => {
          result += `${item.seriesName}: ${item.value}%<br/>`
        })
        return result
      }
    },
    legend: {
      top: '10%',
      right: '2%',
      itemGap: 20,
      icon: 'circle',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: { color: '#C5D6E6' }
    },
    grid: {
      left: '5%',
      right: '5%',
      bottom: '0%',
      top: '15%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: years,
        axisLabel: { textStyle: { color: '#C5D6E6' } },
        axisLine: { lineStyle: { color: 'rgba(217, 231, 255, 0.3)' } },
        axisTick: { lineStyle: { color: 'rgba(217, 231, 255, 0.2)' } }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '落实率',
        min: 80,
        max: 100,
        splitNumber: 4,
        splitLine: { lineStyle: { color: 'rgba(217, 231, 255, 0.1)' } },
        axisLabel: {
          textStyle: { color: '#C5D6E6' },
          formatter: '{value} %'
        },
        axisLine: { show: false },
        nameTextStyle: { color: '#999' }
      }
    ],
    series: [
      {
        name: '全校落实率',
        type: 'line',
        data: rates.全校落实率,
        lineStyle: { width: 2, color: 'rgba(15, 222, 255, 1)' },
        itemStyle: { color: 'rgba(15, 222, 255, 1)' },
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        emphasis: { symbolSize: 8 }
      },
      {
        name: '工科落实率',
        type: 'line',
        data: rates.工科落实率,
        lineStyle: { width: 2, color: 'rgba(0, 144, 255, 1)' },
        itemStyle: { color: 'rgba(0, 144, 255, 1)' },
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        emphasis: { symbolSize: 8 }
      },
      {
        name: '文科落实率',
        type: 'line',
        data: rates.文科落实率,
        lineStyle: { width: 2, color: 'rgba(255, 208, 59, 1)' },
        itemStyle: { color: 'rgba(255, 208, 59, 1)' },
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        emphasis: { symbolSize: 8 }
      },
      {
        name: '理科落实率',
        type: 'line',
        data: rates.理科落实率,
        lineStyle: { width: 2, color: 'rgba(130, 61, 255, 1)' },
        itemStyle: { color: 'rgba(130, 61, 255, 1)' },
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        emphasis: { symbolSize: 8 }
      }
    ]
  }

  chartInstance.setOption(option)
}

const handleResize = () => {
  if (chartInstance) chartInstance.resize()
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

watchEffect((onCleanup) => {
  onCleanup(() => {
    window.removeEventListener('resize', handleResize)
    if (chartInstance) chartInstance.dispose()
  })
})
</script>

<style scoped>
.salary-chart-container {
  width: 100%;
  height: 100%;
}
</style>