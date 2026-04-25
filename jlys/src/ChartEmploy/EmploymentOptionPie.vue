<template>
  <div class="big-data-job-pie">
    <div ref="chartRef" class="chart-container"></div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import * as echarts from 'echarts'
import { getEmploymentStatusCount } from '@/api/doris'

const chartRef = ref(null)
let chartInstance = null
const statusData = ref([])

const totalCount = computed(() => {
  return statusData.value.reduce((sum, item) => sum + Number(item.value || 0), 0)
})

// 颜色配置（完全沿用你原有配色，和谐统一）
const colorList = ['#17f99e', '#2adfd4', '#09a7ff', '#FFFFFF', '#03ffff', '#46FFE9', '#A3F9FE', '#0084FF', '#0578B9']
const colorList1 = ['#17f99e', '', '#2adfd4', '', '#09a7ff', '', '#FFFFFF', '', '#03ffff', '', '#46FFE9', '', '#A3F9FE', '', '#0084FF', '', '#0578B9', '']
const colorList2 = ['rgba(255, 208, 118, 0.4)', '', 'rgba(69, 244, 245, 0.4)', '', 'rgba(7, 166, 255, 0.4)', '', 'rgba(255, 255, 255, 0.4)', '',
  'rgba(0, 194, 255, 0.4)', '', 'rgba(163, 249, 254, 0.4)', '', 'rgba(0, 132, 255, 0.4)', '', 'rgba(5, 120, 185, 0.4)', '']

const mergeInternToEmployed = (list) => {
  const mergedMap = new Map()

  list.forEach((item) => {
    const originalStatus = item.employmentStatus || '其他'
    const targetStatus = originalStatus === '实习中' ? '已就业' : originalStatus
    const count = Number(item.studentCount || 0)
    const prev = mergedMap.get(targetStatus) || 0
    mergedMap.set(targetStatus, prev + count)
  })

  return Array.from(mergedMap.entries()).map(([name, value]) => ({ name, value }))
}

const buildChartOption = (data) => {
  const safeData = Array.isArray(data) ? data : []
  const sum = safeData.reduce((total, item) => total + Number(item.value || 0), 0)
  const optionData = []

  safeData.forEach(item => {
    optionData.push({ value: item.value, name: item.name })
    optionData.push({ name: '', value: sum / 100 || 1, itemStyle: { color: 'transparent' } })
  })

  return {
    tooltip: {
      trigger: 'item'
    },
    title: [
      {
        text: '就业分布',
        left: '25%',
        top: 'center',
        itemGap: 0,
        textStyle: {
          color: '#fff',
          fontSize: 20,
          fontWeight: 500,
          rich: {
            name: { fontSize: 28, color: '#00FFFF' },
            unit: { fontSize: 18, padding: [0, 0, 0, 5] }
          }
        },
        textAlign: 'center'
      },
      {
        text: '全校毕业生就业分布',
        left: 'left',
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
      left: '50%',
      right: '20',
      itemWidth: 4,
      itemHeight: 4,
      formatter: (name) => {
        const obj = safeData.find(item => item.name === name)
        if (!obj || !sum) return ''
        const percent = ((obj.value / sum) * 100).toFixed(2)
        return `{iconName|}{offsetBlock|}{name|${name}}{percent|${percent}}{unit|%}`
      },
      align: 'right',
      textStyle: {
        color: '#FFF',
        fontSize: 16,
        lineHeight: 16,
        rich: {
          name: { color: 'rgba(255,255,255, 0.9)', fontSize: 16, align: 'left' },
          value: { fontSize: 20, align: 'right', width: 70 },
          percent: { fontSize: 20, width: 60 },
          unit: { color: 'rgba(255,255,255, 0.7)', fontSize: 14 }
        }
      },
      data: safeData.map((dItem, dIndex) => {
        let maxLen = 0
        safeData.forEach((item) => { if (maxLen < item.name.length) maxLen = item.name.length })
        return {
          ...dItem,
          textStyle: {
            rich: {
              iconName: { width: 8, height: 8, borderRadius: 2, backgroundColor: colorList[dIndex % colorList.length] },
              percent: { color: colorList[dIndex % colorList.length] },
              value: { color: colorList[dIndex % colorList.length] },
              offsetBlock: { width: 8, padding: [0, -((maxLen - dItem.name.length) * 1), 0, 0], height: 8 },
              name: { width: maxLen * 20, padding: [0, 0 + ((maxLen - dItem.name.length) * 1), 0, 0] }
            }
          }
        }
      })
    },
    series: [
      {
        type: 'pie',
        zlevel: 1,
        silent: true,
        radius: ['55%', '60%'],
        center: ['25%', '50%'],
        itemStyle: {
          color: function (params) { return colorList1[params.dataIndex % colorList1.length] }
        },
        label: { show: false },
        data: optionData
      },
      {
        type: 'pie',
        radius: ['55%', '55.2%'],
        center: ['25%', '50%'],
        emphasis: { scaleSize: 20 },
        clockwise: false,
        itemStyle: { shadowBlur: 1, shadowColor: 'rgba(15, 79, 150,0.61)', color: 'rgba(23,138,173,1)' },
        label: { show: false },
        data: [0]
      },
      {
        type: 'pie',
        radius: ['44%', '44.6%'],
        center: ['15%', '50%'],
        emphasis: { scaleSize: 20 },
        clockwise: false,
        color: ['#55c2e200', 'rgba(23,138,173,1)', '#ff5a6100', 'ff5a6100'],
        label: { show: false },
        data: [140, 60, 240, 130]
      }
    ]
  }
}

onMounted(() => {
  chartInstance = echarts.init(chartRef.value)

  const renderChart = async () => {
    try {
      const response = await getEmploymentStatusCount()
      const list = Array.isArray(response?.data) ? response.data : []
      statusData.value = mergeInternToEmployed(list)
      chartInstance.setOption(buildChartOption(statusData.value), true)
    } catch (error) {
      console.error('获取就业去向总体分布失败:', error)
      statusData.value = []
      chartInstance.setOption(buildChartOption([]), true)
    }
  }

  renderChart()

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