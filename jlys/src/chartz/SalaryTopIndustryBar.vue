<template>
  <div class="radar-chart-wrapper">
    <!-- 图表容器：通过类名控制尺寸 -->
    <div ref="chartRef" class="radar-chart-container"></div>
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

  // 行业数据（对应表格中的industry）
  const industries = [
    '计算机软件',
    '通信/电信/网络设备',
    '电子技术/半导体/集成电路',
    '互联网/电子商务',
    '计算机服务',
    '新能源',
    '金融/投资/证券'
  ]

  // 薪资数据（单位K，对应表格中的max_salary，保留两位小数）
  const salaries = [
    60.00, 66.67, 50.00, 25.00, 30.00, 50.00, 35.00
  ]

  // 对数据进行排序（从高到低）
  const sortedData = industries
    .map((industry, index) => ({ industry, salary: salaries[index] }))
    .sort((a, b) => b.salary - a.salary)

  // 提取排序后的行业和薪资
  const yData = sortedData.map(item => item.industry)
  const data = sortedData.map(item => item.salary)

  // 计算最大值用于背景条
  const max = Math.max(...data)
  const getMax = Array(yData.length).fill(max)

  // 图表配置（主要调整了grid的left属性）
  const option = {
    title: {
      text: '不同行业薪资对比图',
      left: 'center',
      top: '0%', // 标题更靠近顶部
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
        return `${params[0].name} : ${params[0].value}K`
      }
    },
    xAxis: {
      show: false,
      type: 'value'
    },
    grid: {
      right: '18%',
      // 关键修改：增加左侧边距，从20%调整为30%，为行业名称留出更多空间
      left: '20%',
      top: '20%',
      bottom: '1%',
      show: false
    },
    yAxis: [
      {
        type: 'category',
        inverse: true,
        // 调整offset值，让文字更靠左显示
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
        splitLine: {
          show: false
        },
        axisTick: {
          show: false
        },
        axisLine: {
          show: false
        },
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
            return `${value}K`
          }
        },
        data: data
      }
    ],
    series: [
      {
        name: '薪资',
        type: 'bar',
        zlevel: 1,
        itemStyle: {
          normal: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 1,
              y2: 0,
              colorStops: [
                {
                  offset: 0,
                  color: 'transparent'
                },
                {
                  offset: 1,
                  color: '#00d0ff'
                }
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
        itemStyle: {
          color: 'transparent'
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
.radar-chart-wrapper {
  padding: 0px;
}

.radar-chart-container {
  width: 100%;
  height: 100%;
  margin: 0 auto;
}
</style>
