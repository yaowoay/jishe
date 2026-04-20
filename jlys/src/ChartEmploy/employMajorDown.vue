<template>
  <div class="treemap-container" ref="chartRef"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

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

// 数据：删除理学院，保留主流学院
const originData = [
  {
    name: '计算机学院',
    value: 860,
    itemStyle: {
      color: {
        type: 'linear', x: 0, y: 1, x2: 1, y2: 0,
        colorStops: darkColors[0].colorStops
      }
    },
    children: [
      { name: '计算机科学与技术', value: 420, children: [
        { name: '已就业', value: 345 }, { name: '考研', value: 58 }, { name: '考公', value: 12 }, { name: '待就业', value: 5 }
      ]},
      { name: '软件工程', value: 280, children: [
        { name: '已就业', value: 226 }, { name: '考研', value: 42 }, { name: '考公', value: 8 }, { name: '待就业', value: 4 }
      ]},
      { name: '人工智能', value: 160, children: [
        { name: '已就业', value: 132 }, { name: '考研', value: 22 }, { name: '考公', value: 4 }, { name: '待就业', value: 2 }
      ]}
    ]
  },
  {
    name: '电子信息学院',
    value: 680,
    itemStyle: {
      color: {
        type: 'linear', x: 0, y: 1, x2: 1, y2: 0,
        colorStops: darkColors[1].colorStops
      }
    },
    children: [
      { name: '电子信息工程', value: 320, children: [
        { name: '已就业', value: 256 }, { name: '考研', value: 48 }, { name: '考公', value: 10 }, { name: '待就业', value: 6 }
      ]},
      { name: '通信工程', value: 260, children: [
        { name: '已就业', value: 208 }, { name: '考研', value: 38 }, { name: '考公', value: 9 }, { name: '待就业', value: 5 }
      ]},
      { name: '集成电路', value: 100, children: [
        { name: '已就业', value: 78 }, { name: '考研', value: 18 }, { name: '考公', value: 2 }, { name: '待就业', value: 2 }
      ]}
    ]
  },
  {
    name: '机械工程学院',
    value: 590,
    itemStyle: {
      color: {
        type: 'linear', x: 0, y: 1, x2: 1, y2: 0,
        colorStops: darkColors[2].colorStops
      }
    },
    children: [
      { name: '机械设计', value: 260, children: [
        { name: '已就业', value: 201 }, { name: '考研', value: 42 }, { name: '考公', value: 12 }, { name: '待就业', value: 5 }
      ]},
      { name: '智能制造', value: 210, children: [
        { name: '已就业', value: 165 }, { name: '考研', value: 34 }, { name: '考公', value: 8 }, { name: '待就业', value: 3 }
      ]},
      { name: '车辆工程', value: 120, children: [
        { name: '已就业', value: 92 }, { name: '考研', value: 20 }, { name: '考公', value: 5 }, { name: '待就业', value: 3 }
      ]}
    ]
  },
  {
    name: '商学院',
    value: 520,
    itemStyle: {
      color: {
        type: 'linear', x: 0, y: 1, x2: 1, y2: 0,
        colorStops: darkColors[3].colorStops
      }
    },
    children: [
      { name: '会计学', value: 220, children: [
        { name: '已就业', value: 152 }, { name: '考研', value: 45 }, { name: '考公', value: 18 }, { name: '待就业', value: 5 }
      ]},
      { name: '市场营销', value: 180, children: [
        { name: '已就业', value: 128 }, { name: '考研', value: 32 }, { name: '考公', value: 14 }, { name: '待就业', value: 6 }
      ]},
      { name: '国际经济', value: 120, children: [
        { name: '已就业', value: 82 }, { name: '考研', value: 24 }, { name: '考公', value: 10 }, { name: '待就业', value: 4 }
      ]}
    ]
  },
  {
    name: '文学院',
    value: 380,
    itemStyle: {
      color: {
        type: 'linear', x: 0, y: 1, x2: 1, y2: 0,
        colorStops: darkColors[4].colorStops
      }
    },
    children: [
      { name: '汉语言文学', value: 180, children: [
        { name: '已就业', value: 112 }, { name: '考研', value: 42 }, { name: '考公', value: 22 }, { name: '待就业', value: 4 }
      ]},
      { name: '新闻传播', value: 120, children: [
        { name: '已就业', value: 76 }, { name: '考研', value: 26 }, { name: '考公', value: 14 }, { name: '待就业', value: 4 }
      ]},
      { name: '广告学', value: 80, children: [
        { name: '已就业', value: 52 }, { name: '考研', value: 16 }, { name: '考公', value: 8 }, { name: '待就业', value: 4 }
      ]}
    ]
  }
]

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
      data: originData,
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

onMounted(() => {
  initChart()
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