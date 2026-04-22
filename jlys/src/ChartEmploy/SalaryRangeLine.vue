<template>
  <div class="salary-distribution-chart">
    <div ref="chartRef" class="chart-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const chartRef = ref(null)
let chartInstance = null

// 生成模拟数据：毕业生合理薪资区间 和 对应人数
const generateMockData = () => {
  // 毕业生合理薪资区间（k）
  const salaryRanges = [
    '3k-5k', '5k-7k', '7k-9k', '9k-11k',
    '11k-13k', '13k-15k', '15k-17k', '17k-20k'
  ]

  // 生成模拟人数（正态分布，毕业生主流薪资集中在7k-13k）
  const baseCount = 150
  const peakIndex = 3 // 峰值在9k-11k，最符合毕业生真实情况
  const counts = salaryRanges.map((_, index) => {
    const distance = Math.abs(index - peakIndex)
    return Math.round(baseCount * Math.exp(-distance * distance / 2.5) + Math.random() * 15)
  })

  return { salaryRanges, counts }
}

// 生成密度曲线数据
const generateDensityData = (counts) => {
  const density = []
  const extendedCounts = [0, ...counts, 0]

  for (let i = 1; i < extendedCounts.length - 1; i++) {
    density.push((extendedCounts[i - 1] + extendedCounts[i] * 2 + extendedCounts[i + 1]) / 4)
  }

  return density
}

onMounted(() => {
  chartInstance = echarts.init(chartRef.value)

  const { salaryRanges, counts } = generateMockData()
  const densityData = generateDensityData(counts)

  // 图表配置
  const option = {
    title: {
      text: '毕业生薪资区间分布',
      left: 'center',
      textStyle: {
        color: '#66ccff',
        fontSize: 13
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params) {
        const range = params[0].name
        const count = params[0].value
        return `${range}区间<br/>毕业生人数: ${count}人`
      }
    },
    legend: {
      data: ['人数', '分布趋势'],
      top: 30,
      textStyle: {
        color: '#fff'
      }
    },
    grid: {
      left: '3%',
      right: -20,
      bottom: '0%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: salaryRanges,
      axisLabel: {
        interval: 0,
        rotate: 30,
        color: '#fff'
      },
      axisLine: {
        lineStyle: {
          color: '#fff'
        }
      },
      axisTick: {
        lineStyle: {
          color: '#fff'
        }
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '毕业生人数',
        nameTextStyle: {
          color: '#fff'
        },
        splitLine: {
          lineStyle: {
            type: 'dashed',
            color: 'rgba(255, 255, 255, 0.3)'
          }
        },
        axisLabel: {
          color: '#fff'
        },
        axisLine: {
          lineStyle: {
            color: '#fff'
          }
        },
        axisTick: {
          lineStyle: {
            color: '#fff'
          }
        }
      },
      {
        type: 'value',
        show: false,
        max: Math.max(...counts) * 1.2
      }
    ],
    series: [
      {
        name: '人数',
        type: 'bar',
        data: counts,
        barWidth: '60%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#4facfe' },
            { offset: 1, color: '#00f2fe' }
          ])
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#00f2fe' },
              { offset: 1, color: '#4facfe' }
            ])
          }
        }
      },
      {
        name: '分布趋势',
        type: 'line',
        data: densityData,
        yAxisIndex: 1,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        showSymbol: false,
        emphasis: {
          showSymbol: true
        },
        lineStyle: {
          width: 3,
          color: 'rgba(4,241,241,0.36)'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255, 125, 0, 0.3)' },
            { offset: 1, color: 'rgba(255, 125, 0, 0)' }
          ])
        }
      }
    ]
  }

  chartInstance.setOption(option)

  const handleResize = () => {
    chartInstance?.resize()
  }
  window.addEventListener('resize', handleResize)
  chartRef.value.resizeHandler = handleResize
})

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
.salary-distribution-chart {
  width: 100%;
  height: 100%;
  padding: 0px;
  box-sizing: border-box;
}

.chart-container {
  width: 100%;
  height: 100%;
}
</style>