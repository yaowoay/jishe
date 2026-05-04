<template>
  <div class="multi-ring-chart-container">
    <div ref="chartRef" class="multi-ring-chart"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

const chartRef = ref(null)
let chartInstance = null

// 新的大数据相关岗位数据
const jobData = [
  { value: 1330, name: '大数据开发工程师' },
  { value: 262, name: '大数据工程师' },
  { value: 220, name: '高级大数据开发工程师' },
  { value: 126, name: '大数据研发工程师' },
  { value: 95, name: '高级大数据工程师' }
  // { value: 92, name: '大数据分析工程师' },
  // { value: 80, name: '大数据开发助理' }
]

// 计算总人数用于百分比
const total = jobData.reduce((sum, item) => sum + item.value, 0)

// 颜色方案（增加了两个颜色以匹配新数据量）
const colors = ['#00f2fe', '#77d2ff', '#4a8cff', '#5a6fff', '#3a5fff', '#7b61ff', '#a75fff']

const initChart = () => {
  if (!chartRef.value) return
  
  chartInstance = echarts.init(chartRef.value)
  
  const series = []
  const maxRadius = 80 // 最大半径
  const barWidth = 10 // 环形宽度
  const barGap = 5 // 环形间距
  
  jobData.forEach((item, i) => {
    const percentage = ((item.value / total) * 100).toFixed(1) + '%'
    
    // 主环形图（标签只显示在第一个片段，避免显示两处百分比）
    series.push({
      type: 'pie',
      clockWise: false,
      hoverAnimation: true,
      radius: [
        `${Math.max(0, maxRadius - i * (barGap + barWidth))}%`,
        `${Math.max(0, maxRadius - (i + 1) * barWidth - i * barGap)}%`
      ],
      center: ['25%', '55%'], // 中心位置，注意与背景环保持一致
      // 将标签移到第一个数据项，避免透明片段也渲染标签
      labelLine: {
        show: false
      },
      itemStyle: {
        borderWidth: 0,
        borderRadius: 0
      },
      data: [
        {
          value: item.value,
          name: item.name,
          label: {
            show: true,
            position: 'inside',
            formatter: percentage,
            color: '#fff',
            fontSize: 12,
            fontWeight: 'bold'
          },
          itemStyle: { 
            color: colors[i],
            shadowBlur: 10,
            shadowColor: colors[i]
          }
        },
        {
          value: total - item.value,
          name: '',
          label: { show: false },
          itemStyle: { 
            color: 'transparent' 
          },
          tooltip: { show: false },
          hoverAnimation: false
        }
      ]
    })
    
    // 背景环形图
    series.push({
      name: '',
      type: 'pie',
      silent: true,
      z: 0,
      clockWise: false,
      hoverAnimation: false,
      radius: [
        `${Math.max(0, maxRadius - i * (barGap + barWidth))}%`,
        `${Math.max(0, maxRadius - (i + 1) * barWidth - i * barGap)}%`
      ],
      center: ['25%', '55%'],
      label: { show: false },
      itemStyle: {
        color: 'rgba(255, 255, 255, 0.05)',
        borderWidth: 0
      },
      data: [
        {
          value: 1,
          tooltip: { show: false },
          hoverAnimation: false
        }
      ]
    })
  })
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: params => {
        return `${params.name}<br/>人数: ${params.value}人<br/>占比: ${((params.value / total) * 100).toFixed(1)}%`
      }
    },
    legend: {
      type: 'scroll',
      orient: 'vertical',
      right: '5%',
      top: 'center',
      itemGap: 10,
      itemWidth: 12,
      itemHeight: 12,
      textStyle: {
        color: '#fff',
        fontSize: 14,
        fontWeight: 'bold',
        rich: {
          value: {
            color: '#fff',
            fontSize: 14,
            fontWeight: 'bold',
            padding: [0, 0, 0, 10]
          },
          percent: {
            color: '#aaa',
            fontSize: 12,
            padding: [0, 0, 0, 5]
          }
        }
      },
      formatter: (name) => {
        const data = jobData.find(item => item.name === name)
        return `{a|${name}}{value|${data.value}人}`
      },
      data: jobData.map(item => item.name)
    },
    series: series,
    graphic: {
      type: 'text',
      left: 'center',
      top: '0%',
      style: {
        text: '大数据相关岗位招聘数量比较',
        fill: '#66ccff',
        fontSize: 14,
        fontWeight: 'bold',
        textAlign: 'center',
        lineHeight: 2
      }
    }
  }
  
  chartInstance.setOption(option)
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

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}
</script>

<style scoped>
.multi-ring-chart-container {
  width: 100%;
  height: 100%;
  
}

.multi-ring-chart {
  width: 100%;
  height: 100%;
 
}
</style>