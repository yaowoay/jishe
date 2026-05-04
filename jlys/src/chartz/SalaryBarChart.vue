<template>
  <div class="salary-comparison-chart">
    <div ref="chartRef" class="chart-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const chartRef = ref(null)
let chartInstance = null

onMounted(() => {
  // 初始化图表实例
  chartInstance = echarts.init(chartRef.value)

  // 模拟行业数据
  const chartData = {
    xAxisData: [
      '计算机软件',
      '通信/电信/网络设备',
      '电子技术/半导体/集成电路',
      '互联网/电子商务',
      '计算机服务(系统、数据服务、维修)',
      '新能源',
      '金融/投资/证券'
    ],
    seriesData: [
      {
        name: '平均薪资',
        color: '0,209,226',
        data: [19.48562, 22.50306, 16.44783, 16.63907, 23.09398, 25.16953, 24.90323]
      },
      {
        name: '最低薪资',
        color: '0,146,255',
        data: [5, 5, 5, 12, 16, 15, 18]
      },
      {
        name: '最高薪资',
        color: '252,184,1',
        data: [60, 6.6667, 50, 25, 30, 50, 35]
      }
    ]
  }

  // 处理系列数据
  let seriesData = []
  let maxDataArr = []

  // 生成主图表数据
  for (let i = 0; i < chartData.seriesData.length; i++) {
    const cur = chartData.seriesData[i]
    // 计算最大值用于背景条
    const maxVal = Math.max(...cur.data) * 1.1
    maxDataArr = new Array(cur.data.length).fill(maxVal)

    seriesData.push({
      name: cur.name,
      type: 'pictorialBar',
      barWidth: 12,
      barGap: '10%',
      symbol: 'path://M10,10 L10,300 H300 V20 L10,10 Z',
      itemStyle: {
        color: {
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: `rgba(${cur.color}, 1)` },
            { offset: 0.8, color: `rgba(${cur.color}, 0.2)` },
            { offset: 1, color: `rgba(${cur.color}, 0)` }
          ]
        }
      },
      data: cur.data
    })
  }

  // 添加背景柱体
  seriesData.push({
    type: 'bar',
    barWidth: 50,
    xAxisIndex: 1,
    showBackground: true,
    backgroundStyle: {
      color: {
        x: 0,
        y: 0,
        x2: 0,
        y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(0,180,255,0)' },
          { offset: 0.5, color: 'rgba(0,180,255,0.2)' },
          { offset: 1, color: 'rgba(0,180,255,0)' }
        ]
      },
      //borderColor: 'rgb(137,255,240)',
      borderColor: 'transparent',
      borderWidth: 1
    },
    itemStyle: {
      color: 'transparent'
    },
    data: maxDataArr
  })

  // 图表配置
  const option = {
    title: {
      text: '不同行业最高最低平均薪资对比图',
      left: 'center',
      top: '0%',  // 标题位置保持在顶部
      textStyle: {
        color: '#66ccff',
        fontSize: 13
      }
    },
    backgroundColor: 'transparent',
    grid: {
      left: 10,
      bottom: -10,
      right: 10,
      top: 20,
      containLabel: true
    },
    legend: {
      right: 'center',
      top: 16,
      left:80,
      textStyle: {
        color: '#fff',
        fontSize: 12
      },
      itemWidth: 15,
      itemHeight: 10,
      itemGap: 35,
      data: chartData.seriesData.map(item => item.name)
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params) {
        return `${params[0].axisValue}<br/>${params[0].seriesName}: ${params[0].value}K`
      }
    },
    xAxis: [
      {
        type: 'category',
        axisTick: {
          show: false
        },
        axisLine: {
          lineStyle: {
            color: 'rgba(255,255,255,0.2)'
          }
        },
        axisLabel: {
          textStyle: {
            color: '#fff'
          },
          rotate: 45, // 旋转标签防止重叠
          interval: 0
        },
        data: chartData.xAxisData
      },
      {
        // 背景柱体专用x轴，不显示
        type: 'category',
        show: false,
        data: chartData.xAxisData
      }
    ],
    yAxis: [
      {
        type: 'value',
        axisTick: {
          show: false
        },
        axisLine: {
          show: false
        },
        splitLine: {
          show: true,
          lineStyle: {
            type: 'dotted',
            color: 'rgba(255,255,255,0.1)'
          }
        },
        axisLabel: {
          textStyle: {
            color: '#fff'
          },
          formatter: '{value}K' // 显示单位
        }
      },
      {
        type: 'value',
        axisTick: {
          show: false
        },
        splitLine: {
          show: false
        },
        axisLine: {
          show: false
        },
        axisLabel: {
          textStyle: {
            color: 'rgba(255,255,255,0.7)'
          }
        }
      }
    ],
    series: seriesData
  }

  // 设置图表配置
  chartInstance.setOption(option)

  // 响应窗口大小变化
  const handleResize = () => {
    chartInstance?.resize()
  }
  window.addEventListener('resize', handleResize)
  chartRef.value.resizeHandler = handleResize
})

// 组件卸载时销毁图表实例
onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
  const handleResize = chartRef.value?.resizeHandler
  if (handleResize) {
    window.removeEventListener('resize', handleResize)
  }
})
</script>

<style scoped>
.salary-comparison-chart {
  width: 100%;
  height: 100%;
  padding: 0px;
}

.chart-container {
  width: 100%;
  height:100%; /* 可根据需要调整高度 */
  margin: 0 auto;
}
</style>