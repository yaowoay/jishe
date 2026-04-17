<template>
  <div class="big-data-job-pie">
    <div ref="chartRef" class="chart-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const chartRef = ref(null)
let chartInstance = null

// 保留原组件数据不变
const s1_data = [
  { value: 350, name: '互联网/IT' },
  { value: 220, name: '金融/银行' },
  { value: 180, name: '制造业' },
  { value: 150, name: '医疗健康' },
  { value: 100, name: '教育培训' }
]

// 颜色配置
const colorList = ['#17f99e', '#2adfd4', '#09a7ff', '#FFFFFF', '#03ffff', '#46FFE9', '#A3F9FE', '#0084FF', '#0578B9']
const colorList1 = ['#17f99e', '', '#2adfd4', '', '#09a7ff', '', '#FFFFFF', '', '#03ffff', '', '#46FFE9', '', '#A3F9FE', '', '#0084FF', '', '#0578B9', '']
const colorList2 = ['rgba(255, 208, 118, 0.4)', '', 'rgba(69, 244, 245, 0.4)', '', 'rgba(7, 166, 255, 0.4)', '', 'rgba(255, 255, 255, 0.4)', '',
  'rgba(0, 194, 255, 0.4)', '', 'rgba(163, 249, 254, 0.4)', '', 'rgba(0, 132, 255, 0.4)', '', 'rgba(5, 120, 185, 0.4)', '']

// 处理数据（使用原组件的s1_data）
let sum = 0
let optionData = []
s1_data.forEach(item => {
  sum += Number(item.value)
})
s1_data.forEach(item => {
  optionData.push({ value: item.value, name: item.name })
  // 添加空值用于显示间距
  optionData.push({ name: '', value: sum / 100, itemStyle: { color: 'transparent' } })
})

// 生成图例数据
const l_data = s1_data.map(item => item.name)

onMounted(() => {
  // 初始化图表实例
  chartInstance = echarts.init(chartRef.value)

  // 图表配置
  const option = {
    tooltip: {
      trigger: 'item'
    },
    // 整合后的多标题配置
    title: [
      // 第一个标题（原有配置）
      {
        text: `{name|${sum}}{unit|个}`,
        left: '14%',
        top: 'center',
        itemGap: 0,
        textStyle: {
          color: '#fff',
          fontSize: 38,
          fontWeight: 500,
          rich: {
            name: {
              fontSize: 28,
              color: '#00FFFF'
            },
            unit: {
              fontSize: 18,
              padding: [0, 0, 0, 5]
            }
          }
        },
        textAlign: 'center'
      },
      // 第二个标题（新增配置）
      {
        text: '热门职位分布',
        left: 'center',
        top: '0%',
        textStyle: {
          color: '#66ccff',
          fontSize: 13
        }
      }
    ],
    legend: {
      icon: 'none',
      orient: 'vertical',
      top: 'middle',
      left: '30%',
      right: '40',
      itemWidth: 4,
      itemHeight: 4,
      formatter: (name) => {
        let obj = s1_data.find(item => item.name === name)
        if (!obj) return ''
        // 计算百分比（保留两位小数）
        const percent = ((obj.value / sum) * 100).toFixed(2)
        const arr = [
          `{iconName|}{offsetBlock|}{name|${name}}{value|${obj.value}}{unit|个}{percent|${percent}}{unit|%}`
        ]
        return arr.join('')
      },
      align: 'right',
      textStyle: {
        color: '#FFF',
        fontSize: 16,
        lineHeight: 16,
        rich: {
          name: {
            color: 'rgba(255,255,255, 0.9)',
            fontSize: 16,
            align: 'left',
            textAlign: 'left',
            padding: [0, 0, 0, 0]
          },
          value: {
            fontSize: 20,
            align: 'right',
            width: 70,
            padding: [0, 0, 0, 0]
          },
          percent: {
            fontSize: 20,
            width: 60
          },
          unit: {
            color: 'rgba(255,255,255, 0.7)',
            fontSize: 14,
            padding: [0, 0, 0, 0]
          }
        }
      },
      data: s1_data.map((dItem, dIndex) => {
        let maxLen = 0
        s1_data.forEach((item) => {
          if (maxLen < item.name.length) {
            maxLen = item.name.length
          }
        })
        return {
          ...dItem,
          textStyle: {
            rich: {
              iconName: {
                width: 8,
                height: 8,
                borderRadius: 2,
                backgroundColor: colorList[dIndex]
              },
              percent: {
                color: colorList[dIndex]
              },
              value: {
                color: colorList[dIndex]
              },
              offsetBlock: {
                width: 8,
                padding: [0, -((maxLen - dItem.name.length) * 1), 0, 0],
                height: 8
              },
              name: {
                width: maxLen * 20,
                padding: [0, 0 + ((maxLen - dItem.name.length) * 1), 0, 0]
              }
            }
          }
        }
      })
    },
    series: [
      {
        // 最外圆圈
        type: 'pie',
        zlevel: 1,
        silent: true, // 取消高亮
        radius: ['50%', '55%'],
        center: ['15%', '50%'],
        itemStyle: {
          color: function (params) {
            return colorList1[params.dataIndex]
          }
        },
        label: {
          show: false
        },
        data: optionData
      },
      {
        type: 'pie',
        radius: ['50%', '50.2%'],
        center: ['15%', '50%'],
        emphasis: {
          scaleSize: 20
        },
        clockwise: false,
        itemStyle: {
          shadowBlur: 1,
          shadowColor: 'rgba(15, 79, 150,0.61)',
          color: 'rgba(23,138,173,1)'
        },
        label: {
          show: false
        },
        data: [0]
      },
      {
        type: 'pie',
        radius: ['44%', '44.6%'],
        center: ['15%', '50%'],
        emphasis: {
          scaleSize: 20
        },
        clockwise: false,
        color: [
          '#55c2e200',
          'rgba(23,138,173,1)',
          '#ff5a6100',
          'ff5a6100'
        ],
        label: {
          show: false
        },
        data: [140, 60, 240, 130]
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
.big-data-job-pie {
  width: 100%;
  height: 100%;
  padding: 0;
  box-sizing: border-box;
}

.chart-container {
  width: 100%;
  height: 100%;
}
</style>