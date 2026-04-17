<template>
  <div class="salary-chart-container">
    <div ref="chartRef" class="chart-wrapper"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import * as echarts from 'echarts'

// 新的城市数据列表（省会/直辖市，按原始数据顺序排列）
// 薪资排名前13的省会/直辖市列表（按薪资降序排列）
// 薪资排名前13的省会/直辖市列表（按薪资降序排列）
// 薪资排名前13的省会/直辖市列表（按薪资降序排列）
const cityData = [
  '石家庄市', '呼和浩特市', '北京市', '重庆市', '成都市', 
  '长沙市', '贵阳市', '长春市', '广州市', '杭州市', 
  '南京市', '武汉市', '西安市'
]

// 对应上面城市的薪资数据(单位K，保留两位小数)
const salaryData = [
  25.20, 23.67, 21.85, 20.69, 20.58,
  19.92, 19.92, 19.70, 19.47, 19.57,
  18.88, 18.71, 19.13
]
const chartRef = ref(null)
let chartInstance = null

// 处理标记点数据
const markPointData = salaryData.map((item, index) => {
  return { 
    coord: [index, item], 
    value: item.toFixed(2) + 'K' 
  }
})

const offsetX = 10
const offsetY = 5

// 绘制左侧面
const CubeLeft = echarts.graphic.extendShape({
  shape: {
    x: 0,
    y: 0
  },
  buildPath: function (ctx, shape) {
    const xAxisPoint = shape.xAxisPoint
    const c0 = [shape.x, shape.y]
    const c1 = [shape.x - offsetX, shape.y - offsetY]
    const c2 = [xAxisPoint[0] - offsetX, xAxisPoint[1] - offsetY]
    const c3 = [xAxisPoint[0], xAxisPoint[1]]
    ctx
      .moveTo(c0[0], c0[1])
      .lineTo(c1[0], c1[1])
      .lineTo(c2[0], c2[1])
      .lineTo(c3[0], c3[1])
      .closePath()
  }
})

// 绘制右侧面
const CubeRight = echarts.graphic.extendShape({
  shape: {
    x: 0,
    y: 0
  },
  buildPath: function (ctx, shape) {
    const xAxisPoint = shape.xAxisPoint
    const c1 = [shape.x, shape.y]
    const c2 = [xAxisPoint[0], xAxisPoint[1]]
    const c3 = [xAxisPoint[0] + offsetX, xAxisPoint[1] - offsetY]
    const c4 = [shape.x + offsetX, shape.y - offsetY]
    ctx
      .moveTo(c1[0], c1[1])
      .lineTo(c2[0], c2[1])
      .lineTo(c3[0], c3[1])
      .lineTo(c4[0], c4[1])
      .closePath()
  }
})

// 绘制顶面
const CubeTop = echarts.graphic.extendShape({
  shape: {
    x: 0,
    y: 0
  },
  buildPath: function (ctx, shape) {
    const c1 = [shape.x, shape.y]
    const c2 = [shape.x + offsetX, shape.y - offsetY] //右点
    const c3 = [shape.x, shape.y - offsetX]
    const c4 = [shape.x - offsetX, shape.y - offsetY]
    ctx
      .moveTo(c1[0], c1[1])
      .lineTo(c2[0], c2[1])
      .lineTo(c3[0], c3[1])
      .lineTo(c4[0], c4[1])
      .closePath()
  }
})

// 注册三个面图形
echarts.graphic.registerShape('CubeLeft', CubeLeft)
echarts.graphic.registerShape('CubeRight', CubeRight)
echarts.graphic.registerShape('CubeTop', CubeTop)

// 初始化图表
const initChart = () => {
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  chartInstance = echarts.init(chartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      backgroundColor: 'rgba(255,255,255,0.75)',
      extraCssText: 'box-shadow: 2px 2px 4px 0px rgba(0,0,0,0.3);',
      textStyle: {
        fontSize: 14,
        color: '#000'
      },
      formatter: (params) => {
        const item = params[1]
        return `${item.name} 平均薪资: ${item.value.toFixed(2)} K`
      }
    },
    grid: {
      left: '3%',
      right: '3%',
      top: '45%',
      bottom: '0%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: cityData,
      axisLine: {
        show: true,
        lineStyle: {
          width: 1,
          color: '#545454'
        }
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        fontSize: 9,
        color: '#FFFFFF',
        rotate: 30  // 城市名称较多，旋转显示避免重叠
      }
    },
    yAxis: {
      type: 'value',
      name: '薪资(K)',
      nameTextStyle: {
        color: '#fff',
        fontWeight: 400,
        fontSize: 12
      },
      axisLine: {
        show: false,
        lineStyle: {
          width: 1,
          color: '#545454'
        }
      },
      splitLine: {
        show: true,
        lineStyle: {
          color: 'rgba(84, 84, 84, 0.3)'
        }
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        fontSize: 12,
        color: '#FFFFFF',
        formatter: '{value}'
      },
      // 根据数据范围设置Y轴最大值，留出适当空间
      max: value => Math.ceil(value.max * 1.1)
    },
    series: [
      {
        type: 'custom',
        renderItem: (params, api) => {
          const location = api.coord([api.value(0), api.value(1)])
          return {
            type: 'group',
            children: [
              {
                type: 'CubeLeft',
                shape: {
                  api,
                  xValue: api.value(0),
                  yValue: api.value(1),
                  x: location[0],
                  y: location[1],
                  xAxisPoint: api.coord([api.value(0), 0])
                },
                style: {
                  fill: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#6AFFEB' },
                    { offset: 0.35, color: '#6AFFEB' },
                    { offset: 1, color: 'transparent' }
                  ]),
                  opacity: 0.6 // 增加透明度
                }
              },
              {
                type: 'CubeRight',
                shape: {
                  api,
                  xValue: api.value(0),
                  yValue: api.value(1),
                  x: location[0],
                  y: location[1],
                  xAxisPoint: api.coord([api.value(0), 0])
                },
                style: {
                  fill: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#00BEAA' },
                    { offset: 0.35, color: '#00BEAA' },
                    { offset: 1, color: 'transparent' }
                  ]),
                  opacity: 0.6 // 增加透明度
                }
              },
              {
                type: 'CubeTop',
                shape: {
                  api,
                  xValue: api.value(0),
                  yValue: api.value(1),
                  x: location[0],
                  y: location[1],
                  xAxisPoint: api.coord([api.value(0), 0])
                },
                style: {
                  fill: '#03FFB1',
                  opacity: 0.6 // 增加透明度
                }
              }
            ]
          }
        },
        symbol: 'circle',
        label: {
          show: true,
          position: 'top'
        },
        showAllSymbol: true,
        symbolSize: 8,
        itemStyle: {
          normal: {
            color: '#81A0D0'
          }
        },
        lineStyle: {
          color: '#81A0D0'
        },
        markPoint: {
          symbol:
            'image://' +
            'data:image/png;base64,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',
          symbolSize: [48, 29],
          symbolOffset: [0, -22],
          label: {
            color: '#fff',
            fontSize: 10
          },
          data: markPointData
        },
        data: salaryData.map((val, idx) => [idx, val])
      },
      {
        type: 'bar',
        itemStyle: {
          color: 'transparent'
        },
        data: salaryData
      }
    ]
  }
  
  chartInstance.setOption(option)
}

// 监听窗口大小变化，重绘图表
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

// 组件卸载时清理
watch(chartRef, (newVal, oldVal) => {
  if (!newVal) {
    window.removeEventListener('resize', handleResize)
    if (chartInstance) {
      chartInstance.dispose()
      chartInstance = null
    }
  }
})
</script>

<style scoped>
.salary-chart-container {
  width: 100%;
  height: 100%;
}

.chart-wrapper {
  width: 100%;
  height: 100%;
}
</style>