<template>
  <div class="salary-chart-container" ref="chartRef"></div>
</template>

<script setup>
import { ref, onMounted, watchEffect } from 'vue'
import * as echarts from 'echarts'

// 定义图表容器的引用
const chartRef = ref(null)
let chartInstance = null

// 生成模拟数据
const generateMockData = () => {
  // 薪资范围类别
  const salaryRanges = ['0-10k', '10k-20k', '20k-30k', '30k-50k', '50k以上']

  // 一线城市真实薪资分布数据
  const cities = {
    上海: [67, 238, 218, 50, 13],    
    北京: [30, 58, 81, 20, 12],      
    广州: [37, 161, 109, 27, 8],    
    深圳: [51, 119, 108, 26, 10]     
  }
  
  return { salaryRanges, cities }
}

// 初始化图表
const initChart = () => {
  // 如果图表实例已存在，先销毁
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  // 创建新的图表实例
  chartInstance = echarts.init(chartRef.value)
  
  // 获取模拟数据
  const { salaryRanges, cities } = generateMockData()
  
  // 配置图表选项
  const option = {
    title: {
      text: '一线城市薪资对比图',
      left: 'center',
      top: '0%',  // 标题位置保持在顶部
      textStyle: {
        color: '#66ccff',
        fontSize: 13
      }
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(10, 25, 47, 0.8)',
      borderColor: 'rgba(15, 222, 255, 0.3)',
      borderWidth: 1,
      textStyle: {
        color: '#C5D6E6'
      },
      formatter: function(params) {
        // 显示为职位数量（个），不再使用百分比
        let result = `${params[0].name}<br/>`
        params.forEach(item => {
          result += `${item.seriesName}: ${item.value} 个<br/>`
        })
        return result
      }
    },
    legend: {
      top: '10%',
      right: '2%',
      itemGap: 20,
      icon: 'circle',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: {
        color: '#C5D6E6'
      }
    },
    grid: {
      left: '5%',
      right: '5%',
      bottom: '0%',
      top: '15%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: salaryRanges,
        axisLabel: {
          textStyle: {
            color: '#C5D6E6'
          },
          rotate: 30
        },
        axisLine: {
          lineStyle: {
            color: 'rgba(217, 231, 255, 0.3)'
          }
        },
        axisTick: {
          lineStyle: {
            color: 'rgba(217, 231, 255, 0.2)'
          }
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '职位数量',
        splitNumber: 4,
        splitLine: {
          lineStyle: {
            color: 'rgba(217, 231, 255, 0.1)'
          }
        },
        axisLabel: {
          textStyle: {
            color: '#C5D6E6'
          },
          // 显示为数量，带单位“个”
          formatter: '{value} 个'
        },
        axisLine: {
          show: false
        },
        nameTextStyle: {
          color: '#999'
        }
      }
    ],
    series: [
      {
        name: '北京',
        type: 'line',
        data: cities.北京,
        lineStyle: {
          width: 2,
          color: 'rgba(15, 222, 255, 1)',
          shadowColor: 'rgba(15, 222, 255, 0.5)',
          shadowBlur: 10,
          shadowOffsetY: 8
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(
            0, 0, 0, 1,
            [
              { offset: 0, color: 'rgba(15, 222, 255, 0.3)' },
              { offset: 1, color: 'rgba(15, 222, 255, 0)' }
            ]
          )
        },
        itemStyle: {
          color: 'rgba(15, 222, 255, 1)'
        },
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        emphasis: {
          symbolSize: 8
        }
      },
      {
        name: '上海',
        type: 'line',
        data: cities.上海,
        lineStyle: {
          width: 2,
          color: 'rgba(0, 144, 255, 1)',
          shadowColor: 'rgba(0, 144, 255, 0.5)',
          shadowBlur: 10,
          shadowOffsetY: 8
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(
            0, 0, 0, 1,
            [
              { offset: 0, color: 'rgba(0, 144, 255, 0.3)' },
              { offset: 1, color: 'rgba(0, 144, 255, 0)' }
            ]
          )
        },
        itemStyle: {
          color: 'rgba(0, 144, 255, 1)'
        },
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        emphasis: {
          symbolSize: 8
        }
      },
      {
        name: '广州',
        type: 'line',
        data: cities.广州,
        lineStyle: {
          width: 2,
          color: 'rgba(255, 208, 59, 1)',
          shadowColor: 'rgba(255, 208, 59, 0.5)',
          shadowBlur: 10,
          shadowOffsetY: 8
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(
            0, 0, 0, 1,
            [
              { offset: 0, color: 'rgba(255, 208, 59, 0.3)' },
              { offset: 1, color: 'rgba(255, 208, 59, 0)' }
            ]
          )
        },
        itemStyle: {
          color: 'rgba(255, 208, 59, 1)'
        },
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        emphasis: {
          symbolSize: 8
        }
      },
      {
        name: '深圳',
        type: 'line',
        data: cities.深圳,
        lineStyle: {
          width: 2,
          color: 'rgba(130, 61, 255, 1)',
          shadowColor: 'rgba(130, 61, 255, 0.5)',
          shadowBlur: 10,
          shadowOffsetY: 8
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(
            0, 0, 0, 1,
            [
              { offset: 0, color: 'rgba(130, 61, 255, 0.3)' },
              { offset: 1, color: 'rgba(130, 61, 255, 0)' }
            ]
          )
        },
        itemStyle: {
          color: 'rgba(130, 61, 255, 1)'
        },
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        emphasis: {
          symbolSize: 8
        }
      }
    ]
  }
  
  // 设置图表选项
  chartInstance.setOption(option)
}

// 监听窗口大小变化，调整图表尺寸
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 组件挂载时初始化图表
onMounted(() => {
  if (chartRef.value) {
    initChart()
    window.addEventListener('resize', handleResize)
  }
})

// 组件卸载时清理
watchEffect((onCleanup) => {
  onCleanup(() => {
    window.removeEventListener('resize', handleResize)
    if (chartInstance) {
      chartInstance.dispose()
      chartInstance = null
    }
  })
})
</script>

<style scoped>
.salary-chart-container {
  width: 100%;
  height: 100%;
 
}
</style>
