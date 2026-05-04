<template>
  <div class="treemap-container" ref="chartRef"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getCollegeMajorEmploymentStatusCount } from '@/api/doris'

const chartRef = ref(null)
let chartInstance = null

// 暗色大屏专用：低饱和、低透明度、柔和科技色
const darkColors = [
  {
    colorStops: [
      { offset: 0, color: 'rgba(45, 90, 80, 0.2)' },
      { offset: 1, color: 'rgba(65, 130, 110, 0.3)' }
    ]
  },
  {
    colorStops: [
      { offset: 0, color: 'rgba(60, 75, 50, 0.2)' },
      { offset: 1, color: 'rgba(90, 110, 70, 0.3)' }
    ]
  },
  {
    colorStops: [
      { offset: 0, color: 'rgba(40, 70, 85, 0.2)' },
      { offset: 1, color: 'rgba(60, 95, 120, 0.3)' }
    ]
  },
  {
    colorStops: [
      { offset: 0, color: 'rgba(50, 80, 60, 0.2)' },
      { offset: 1, color: 'rgba(70, 110, 80, 0.3)' }
    ]
  },
  {
    colorStops: [
      { offset: 0, color: 'rgba(75, 65, 50, 0.2)' },
      { offset: 1, color: 'rgba(100, 85, 65, 0.3)' }
    ]
  }
]

const buildTreeData = (records) => {
  const collegeMap = new Map()
  const colorByCollege = new Map()

  records.forEach((record) => {
    const college = record.college || '未命名学院'
    const major = record.major || '未命名专业'
    const employmentStatus = record.employmentStatus || '未分类'
    const studentCount = Number(record.studentCount || 0)

    if (!collegeMap.has(college)) {
      const collegeNode = {
        name: college,
        value: 0,
        itemStyle: {
          color: {
            type: 'linear', x: 0, y: 1, x2: 1, y2: 0,
            colorStops: darkColors[collegeMap.size % darkColors.length].colorStops
          }
        },
        children: []
      }
      collegeMap.set(college, collegeNode)
      colorByCollege.set(college, collegeNode.itemStyle.color)
    }

    const collegeNode = collegeMap.get(college)
    let majorNode = collegeNode.children.find(item => item.name === major)
    if (!majorNode) {
      majorNode = {
        name: major,
        value: 0,
        children: []
      }
      collegeNode.children.push(majorNode)
    }

    majorNode.children.push({
      name: employmentStatus,
      value: studentCount
    })

    majorNode.value += studentCount
    collegeNode.value += studentCount
  })

  return Array.from(collegeMap.values())
}

const initChart = () => {
  if (chartInstance) chartInstance.dispose()
  chartInstance = echarts.init(chartRef.value)

  const option = {
    // 顶部标题
    title: {
      text: '不同学院专业就业分布',
      left: 'center',
      top: '1%',
      textStyle: {
        color: '#66ccff',
        fontSize: 13,
        fontWeight: 500
      }
    },
    backgroundColor: 'transparent',
    tooltip: {
      show: true,
      trigger: 'item',
      backgroundColor: 'rgba(20, 25, 35, 0.85)',
      borderColor: 'rgba(100,120,150,0.3)',
      textStyle: { color: '#e0e6f0' },
      formatter: (params) => `${params.name}<br/>人数：${params.value} 人`
    },
    series: [{
      type: 'treemap',
      left: 0, right: 0, top: '8%', bottom: 0,
      label: {
        show: true,
        formatter: (params) => params.value + '人\n' + params.name,
        fontSize: 14,
        fontWeight: 500,
        color: 'rgba(225,235,255,0.9)'
      },
      breadcrumb: {
        show: true,
        textStyle: { color: '#9cb0c6', fontSize: 12 }
      },
      data: [],
      roam: false,
      nodeClick: true,
      leafDepth: 2,
      itemStyle: {
        borderColor: 'rgba(100,120,150,0.25)',
        borderWidth: 1,
        gapWidth: 1
      }
    }]
  }

  chartInstance.setOption(option)
}

const fetchDataAndRender = async () => {
  try {
    const response = await getCollegeMajorEmploymentStatusCount()
    const records = Array.isArray(response?.data) ? response.data : []
    const treeData = buildTreeData(records)
    chartInstance?.setOption({
      series: [{ data: treeData }]
    })
  } catch (error) {
    console.error('获取学院专业就业去向分布失败:', error)
    chartInstance?.setOption({
      series: [{ data: [] }]
    })
  }
}

onMounted(() => {
  initChart()
  fetchDataAndRender()
  window.addEventListener('resize', () => chartInstance?.resize())
})

onUnmounted(() => {
  window.removeEventListener('resize', () => chartInstance?.resize())
  chartInstance?.dispose()
})
</script>

<style scoped>
.treemap-container {
  width: 100%;
  height: 100%;
  background: transparent !important;
}
</style>