<template>
  <div class="stats-container">
    <div ref="positionsChart" class="chart-box"></div>
    <div ref="companiesChart" class="chart-box"></div>
    <div ref="skillsChart" class="chart-box"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'    
import * as echarts from 'echarts'    

// 创建DOM引用
const positionsChart = ref(null)    
const companiesChart = ref(null)    
const skillsChart = ref(null)    

// 原始数据
const statsData = {
  positions: {
    value: 1258,
    title: '分析岗位',
    maxValue: 2000 // 假设最大值为2000，用于计算百分比
  },
  companies: {
    value: 326,
    title: '覆盖企业',
    maxValue: 500 // 假设最大值为500
  },
  skills: {
    value: 48,
    title: '热门技能',
    maxValue: 100 // 假设最大值为100
  }
}

/**
 * 生成 ECharts 仪表盘配置的函数
 * @param {string} title - 图表标题
 * @param {number} value - 当前数值
 * @param {number} maxValue - 用于计算百分比的最大值
 * @param {Array<string>} colors - 渐变色
 * @returns {object} ECharts 配置项
 */
const getGaugeOption = (title, value, maxValue, colors) => {
  const percentage = ((value / maxValue) * 100).toFixed(0)

  return {
    title: {
      text: title,
      left: 'center',
      top: '5%',
      textStyle: {
        color: '#FFFFFF',
        fontSize: 16,
        fontWeight: 'normal'
      }
    },
    series: [
      {
        type: 'gauge',
        center: ['50%', '60%'], // 图表中心位置
        radius: '90%', // 图表半径
        startAngle: 225,
        endAngle: -45,
        min: 0,
        max: maxValue,
        splitNumber: 0, // 不显示刻度线
        progress: {
          show: true,
          width: 18, // 进度条宽度
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: colors[0] },
              { offset: 1, color: colors[1] }
            ])
          }
        },
        pointer: {
          show: false // 不显示指针
        },
        axisLine: {
          show: true,
          lineStyle: {
            width: 18,
            color: [[1, 'rgba(255, 255, 255, 0.1)']] // 底部背景轨道颜色
          }
        },
        axisTick: { show: false },
        splitLine: { show: false },
        axisLabel: { show: false },
        detail: {
          valueAnimation: true,
          offsetCenter: [0, '0%'],
          formatter: function () {
            // 使用富文本同时展示百分比和原始值
            return `{percentage|${percentage}%}\n{value|${value}}`
          },
          rich: {
            percentage: {
              fontSize: 30,
              fontWeight: 'bolder',
              color: '#FFFFFF'
            },
            value: {
              fontSize: 14,
              color: '#a0a0a0',
              padding: [5, 0, 0, 0]
            }
          }
        },
        data: [
          {
            value: value
          }
        ]
      }
    ]
  }    
}    

// 在组件挂载后初始化所有图表
onMounted(() => {
  // 1. 初始化“分析岗位”图表
  const positionsInstance = echarts.init(positionsChart.value)    
  positionsInstance.setOption(
    getGaugeOption(
      statsData.positions.title,
      statsData.positions.value,
      statsData.positions.maxValue,
      ['#5B94FF', '#2E61E8']
    )
  )    

  // 2. 初始化“覆盖企业”图表
  const companiesInstance = echarts.init(companiesChart.value)    
  companiesInstance.setOption(
    getGaugeOption(
      statsData.companies.title,
      statsData.companies.value,
      statsData.companies.maxValue,
      ['#46D3E8', '#20A1FF']
    )
  )    

  // 3. 初始化“热门技能”图表
  const skillsInstance = echarts.init(skillsChart.value)    
  skillsInstance.setOption(
    getGaugeOption(
      statsData.skills.title,
      statsData.skills.value,
      statsData.skills.maxValue,
      ['#A359FF', '#7429E8']
    )
  )    

  // 监听窗口大小变化，使图表自适应
  window.addEventListener('resize', () => {
    positionsInstance.resize()    
    companiesInstance.resize()    
    skillsInstance.resize()    
  })    
})    
</script>

<style scoped>
.stats-container {
  display: flex;
  justify-content: space-around;
  align-items: center;
  width: 100%;
  height: 250px; /* 您可以根据需要调整容器高度 */
  /* background-color: #0d1a3a; /* 深色背景，模仿图片风格 */
  padding: 20px;
  box-sizing: border-box;
}

.chart-box {
  width: 30%;
  height: 100%;
}
</style>