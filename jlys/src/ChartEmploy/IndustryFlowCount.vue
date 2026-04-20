<template>
  <div class="radar-chart-wrapper">
    <div ref="chartRef" class="radar-chart-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const chartRef = ref(null)
let chartInstance = null

onMounted(() => {
  chartInstance = echarts.init(chartRef.value)

  // 全校毕业生主流行业流向
  const industries = [
    '互联网/软件开发',
    '电子/半导体/集成电路',
    '教育/培训',
    '金融/银行/证券',
    '建筑/工程/地产',
    '制造业/生产加工',
    '文化传媒/新媒体'
  ]

  // 全校毕业生就业人数（模拟真实规模）
  const salaries = [
    682, 415, 356, 310, 278, 245, 189
  ]

  // 从高到低排序
  const sortedData = industries.map((industry, index) => ({ industry, salary: salaries[index] })).sort((a, b) => b.salary - a.salary)

  const yData = sortedData.map(item => item.industry)
  const data = sortedData.map(item => item.salary)
  const max = Math.max(...data)
  const getMax = Array(yData.length).fill(max)

  const option = {
    title: {
      text: '行业流向统计',
      left: 'center',
      top: '0%',
      textStyle: {
        color: '#66ccff',
        fontSize: 14,
        fontWeight: 600
      }
    },
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'none'
      },
      formatter: function (params) {
        return `${params[0].name}：${params[0].value} 人`
      }
    },
    xAxis: {
      show: false,
      type: 'value'
    },
    grid: {
      right: '18%',
      left: '20%',
      top: '20%',
      bottom: '1%',
      show: false
    },
    yAxis: [
      {
        type: 'category',
        inverse: true,
        offset: 80,
        axisLabel: {
          show: true,
          align: 'left',
          textStyle: {
            color: '#fff',
            fontSize: '14'
          },
          formatter: function (value, index) {
            const num = index + 1
            let str = ''

            if (index === 0) {
              str = '{no1|} {num1|' + num + '} {title1| ' + value + '}'
            } else if (index === 1) {
              str = '{no2|} {num2|' + num + '} {title2| ' + value + '}'
            } else if (index === 2) {
              str = '{no3|} {num3|' + num + '} {title3| ' + value + '}'
            } else {
              str = ' {num|' + num + '} {title| ' + value + '}'
            }
            return str
          },
          rich: {
            num: {
              color: '#387ec1',
              backgroundColor: 'transparent',
              width: 10,
              height: 10,
              fontSize: 14,
              padding: [6, 5, 3, 5],
              align: 'center',
              shadowColor: 'transparent',
              borderColor: 'transparent',
              borderWidth: 0
            },
            num1: {
              color: '#51aff8',
              backgroundColor: 'transparent',
              width: 10,
              height: 10,
              fontSize: 14,
              padding: [7, 5, 3, 5],
              align: 'center',
              shadowColor: 'transparent',
              borderColor: 'transparent',
              borderWidth: 0
            },
            num2: {
              color: '#51aff8',
              backgroundColor: 'transparent',
              width: 10,
              height: 10,
              fontSize: 10,
              padding: [7, 5, 3, 5],
              align: 'center',
              shadowColor: 'transparent',
              borderColor: 'transparent',
              borderWidth: 0
            },
            num3: {
              color: '#51aff8',
              backgroundColor: 'transparent',
              width: 10,
              height: 10,
              fontSize: 10,
              padding: [7, 5, 3, 5],
              align: 'center',
              shadowColor: 'transparent',
              borderColor: 'transparent',
              borderWidth: 0
            }
          }
        },
        splitLine: { show: false },
        axisTick: { show: false },
        axisLine: { show: false },
        data: yData
      },
      {
        type: 'category',
        inverse: true,
        offset: 0,
        axisTick: 'none',
        axisLine: 'none',
        show: true,
        axisLabel: {
          textStyle: {
            color: '#fff',
            fontSize: '14'
          },
          formatter: function(value) {
            return `${value}人`
          }
        },
        data: data
      }
    ],
    series: [
      {
        name: '就业人数',
        type: 'bar',
        zlevel: 1,
        itemStyle: {
          normal: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 1, y2: 0,
              colorStops: [
                { offset: 0, color: 'transparent' },
                { offset: 1, color: '#00d0ff' }
              ],
              global: false
            }
          }
        },
        barWidth: 20,
        data: data
      },
      {
        name: '背景',
        type: 'bar',
        barWidth: 20,
        barGap: '-100%',
        data: getMax,
        itemStyle: { color: 'transparent' }
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
.radar-chart-wrapper {
  padding: 0px;
}

.radar-chart-container {
  width: 100%;
  height: 100%;
  margin: 0 auto;
}
</style>