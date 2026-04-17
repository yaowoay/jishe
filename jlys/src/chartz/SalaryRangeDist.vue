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

// 生成模拟数据：薪资区间和对应的职位数量
const generateMockData = () => {
  // 薪资区间（k）
  const salaryRanges = [
    '5k-10k', '10k-15k', '15k-20k', '20k-25k',
    '25k-30k', '30k-35k', '35k-40k', '40k-45k', '45k-50k'
  ]

  // 生成模拟的职位数量（基于正态分布模拟集中趋势）
  const baseCount = 120
  const peakIndex = 3 // 峰值位置（20k-25k）
  const counts = salaryRanges.map((_, index) => {
    // 使用正态分布公式生成数据，使数据呈现集中趋势
    const distance = Math.abs(index - peakIndex)
    return Math.round(baseCount * Math.exp(-distance * distance / 3) + Math.random() * 20)
  })

  return { salaryRanges, counts }
}

// 生成密度曲线数据
const generateDensityData = (counts) => {
  // 对直方图数据进行平滑处理生成密度曲线
  const density = []
  // 前延和后延数据点使曲线更平滑
  const extendedCounts = [0, ...counts, 0]

  for (let i = 1; i < extendedCounts.length - 1; i++) {
    // 三点平滑处理
    density.push((extendedCounts[i - 1] + extendedCounts[i] * 2 + extendedCounts[i + 1]) / 4)
  }

  return density
}

onMounted(() => {
  // 初始化图表实例
  chartInstance = echarts.init(chartRef.value)

  // 获取模拟数据
  const { salaryRanges, counts } = generateMockData()
  const densityData = generateDensityData(counts)

  // 图表配置
  const option = {
    title: {
      text: '薪资分布与职位数量趋势',
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
        return `${range}薪资区间<br/>职位数量: ${count}个`
      }
    },
    legend: {
      data: ['职位数量', '分布趋势'],
      top: 30,
      textStyle: {
        color: '#fff' // 图例文字设置为白色
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
        color: '#fff' // X轴刻度文字设为白色
      },
      //name: '薪资区间',
      nameTextStyle: {
        //padding: [10, 0, 0, 0],
        color: '#fff' // X轴名称设为白色
      },
      axisLine: {
        lineStyle: {
          color: '#fff' // X轴线设为白色
        }
      },
      axisTick: {
        lineStyle: {
          color: '#fff' // X轴刻度线设为白色
        }
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '职位数量',
        nameTextStyle: {
          //padding: [0, 0, 0, 10],
          color: '#fff' // Y轴名称设为白色
        },
        splitLine: {
          lineStyle: {
            type: 'dashed',
            color: 'rgba(255, 255, 255, 0.3)' // 分割线设为半透明白色
          }
        },
        axisLabel: {
          color: '#fff' // Y轴刻度文字设为白色
        },
        axisLine: {
          lineStyle: {
            color: '#fff' // Y轴线设为白色
          }
        },
        axisTick: {
          lineStyle: {
            color: '#fff' // Y轴刻度线设为白色
          }
        }
      },
      {
        type: 'value',
        show: false, // 隐藏第二个Y轴
        max: Math.max(...counts) * 1.2
      }
    ],
    series: [
      {
        name: '职位数量',
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
        smooth: true, // 平滑曲线
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

  // 设置图表配置
  chartInstance.setOption(option)

  // 响应窗口大小变化
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