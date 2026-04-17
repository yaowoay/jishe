<template>
  <div class="chart-wrapper">
    <div ref="chartContainer" class="chart-container"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'StackedBarChart',
  data() {
    return {
      // 经验维度（作为图例）
      experienceData: ['无需经验', '1年经验', '2年经验', '3-4年经验', '5-7年经验', '8-9年经验', '10年以上经验'],
      // 大数据相关职位列表（作为Y轴）
      positionData: [
        '大数据开发工程师', 
        '大数据工程师', 
        '高级大数据开发工程师', 
        '大数据研发工程师', 
        '高级大数据工程师', 
        '大数据分析工程师', 
        '大数据开发助理'
      ],
      // 原始岗位数量数据
      positionCountData: {
        '大数据开发工程师': [104, 166, 225, 465, 304, 43, 23],
        '大数据工程师': [14, 35, 37, 87, 69, 13, 7],
        '高级大数据开发工程师': [18, 18, 26, 87, 50, 14, 7],
        '大数据研发工程师': [8, 22, 19, 40, 31, 3, 3],
        '高级大数据工程师': [3, 13, 18, 32, 21, 4, 4],
        '大数据分析工程师': [7, 14, 11, 43, 15, 1, 1],
        '大数据开发助理': [3, 16, 12, 31, 16, 1, 1]
      },
      legendSelected: {}
    }
  },

  mounted() {
    // 初始化默认选中所有图例（经验维度）
    this.experienceData.forEach(experience => {
      this.legendSelected[experience] = true
    })
    this.renderChart()
    window.addEventListener('resize', this.handleResize)
  },

  beforeUnmount() {
    window.removeEventListener('resize', this.handleResize)
  },

  methods: {
    // 计算占比数据：按每个职位维度计算各经验的占比
    calculatePercentageData() {
      const percentageData = {}
      // 1. 计算每个职位下的总数量
      this.positionData.forEach(position => {
        const total = this.positionCountData[position].reduce((sum, count) => sum + count, 0)
        // 2. 计算该职位下各经验维度的占比（保留2位小数）
        percentageData[position] = this.positionCountData[position].map(count => {
          if (total === 0) return 0
          return Number(((count / total) * 100).toFixed(2))
        })
      })
      return percentageData
    },

    // 重构系列数据：经验为系列，职位为Y轴，值为占比
    formatSeriesData(percentageData) {
      const seriesData = []
      this.experienceData.forEach((experience, expIndex) => {
        const data = this.positionData.map(position => {
          return percentageData[position][expIndex]
        })
        seriesData.push({
          name: experience,
          type: 'bar', // 横向条形图仍用bar类型，通过x/y轴互换实现
          stack: '占比',
          data: data,
          barWidth: 17,
          emphasis: {
            focus: 'series',
            itemStyle: {
              shadowBlur: 10,
              shadowColor: 'rgba(102, 204, 255, 0.5)'
            }
          },
          itemStyle: {
            color: this.getExperienceColor(experience),
            borderColor: 'rgba(255, 255, 255, 0.1)',
            borderWidth: 1
          }
        })
      })
      return seriesData
    },

    renderChart() {
      const chart = echarts.init(this.$refs.chartContainer)
      chart.clear()

      // 计算占比数据
      const percentageData = this.calculatePercentageData()
      // 格式化系列数据
      const seriesData = this.formatSeriesData(percentageData)

      const option = {
        backgroundColor: 'transparent',
        title: {
          text: '大数据相关职位经验要求占比分布',
          left: 'center',
          top: '0%',
          textStyle: {
            color: '#333',
            fontSize: 13
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
            lineStyle: {
              color: 'rgba(200, 200, 200, 0.8)',
              width: 2
            }
          },
          backgroundColor: 'rgba(255,255,255,0.9)',
          borderColor: '#ddd',
          borderWidth: 5,
          borderRadius: 10,
          textStyle: {
            textStyle: { color: '#333' },
            fontSize: 14,
            fontWeight: 'bold',
            fontFamily: 'Consolas, monospace'
          },
          formatter: function(params) {
            let result = params[0].name + '<br/>'
            params.forEach(item => {
              if (item.value > 0) {
                // 恢复百分比展示
                result += `<span style="color:#66ccff;">${item.seriesName}</span>: <span style="color:#fff;">${item.value}%</span><br/>`
              }
            })
            return result
          },
          show: true,
          alwaysShowContent: false
        },
        legend: {
          data: this.experienceData,
          textStyle: {
            color: '#666',
            fontSize: 11
          },
          selected: this.legendSelected,
          top: '10%',
          padding: [3, 3],
          itemGap: 5,
          itemWidth: 12,
          itemHeight: 8,
          backgroundColor: 'transparent',
          borderRadius: 4,
          borderColor: 'rgba(102, 204, 255, 0.3)',
          borderWidth: 1
        },
        grid: {
          top: '30%',
          left: '1%', // 增加左侧间距，适配Y轴职位名称
          right: '8%',
          bottom: '0%',
          containLabel: true,
          backgroundColor: 'transparent',
          borderColor: 'rgba(102, 204, 255, 0.3)',
          borderWidth: 1
        },
        // 核心：互换X/Y轴配置，实现横向条形图
        yAxis: {
          type: 'category', // Y轴为分类（职位）
          data: this.positionData,
          axisLine: {
            color: 'rgba(200, 200, 200, 0.8)'
          },
          axisLabel: {
            textStyle: {
              color: '#666',
              fontSize: 12
            },
            interval: 0 // 显示所有标签
          },
          axisTick: {
            lineStyle: {
              color: 'rgba(200, 200, 200, 0.5)'
            }
          },
          splitArea: {
            show: true,
            areaStyle: {
              color: ['rgba(240, 240, 240, 0.5)']
            }
          }
        },
        xAxis: {
          type: 'value', // X轴为数值（占比）
          name: '占比',
          nameTextStyle: {
            color: '#66ccff',
            fontSize: 10
          },
          position: 'bottom',
          axisLabel: {
            formatter: '{value}%', // X轴显示百分比
            textStyle: {
              color: '#666'
            }
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(200, 200, 200, 0.3)'
            }
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(102, 204, 255, 0.1)',
              type: 'dashed'
            }
          },
          max: 100, // 占比最大值100%
          min: 0
        },
        series: seriesData,
        // emphasis: {
        //   disabled: false
        // },
        animationDuration: 1000,
        animationEasing: 'cubicOut',
        animationDelay: function (idx) {
          return idx * 50
        }
      }

      chart.setOption(option)

      // 图例选择事件（适配经验维度）
      chart.on('legendselectchanged', (params) => {
        this.legendSelected = params.selected
        chart.setOption({
          series: this.experienceData.map((experience) => ({
            name: experience,
            show: params.selected[experience]
          }))
        })
      })

      this.handleResize()
    },

    // 为每个经验维度分配渐变颜色
    getExperienceColor(experience) {
      const colors = {
        '无需经验': new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#0066ff' },
          { offset: 0, color: '#003399' }
        ]),
        '1年经验': new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#00ccff' },
          { offset: 1, color: '#006699' }
        ]),
        '2年经验': new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#3399ff' },
          { offset: 1, color: '#003366' }
        ]),
        '3-4年经验': new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#66ccff' },
          { offset: 1, color: '#336699' }
        ]),
        '5-7年经验': new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#99ddff' },
          { offset: 1, color: '#6699cc' }
        ]),
        '8-9年经验': new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#ccf0ff' },
          { offset: 1, color: '#99ccdd' }
        ]),
        '10年以上经验': new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#e6f7ff' },
          { offset: 1, color: '#b3d9ff' }
        ])
      }
      return colors[experience]
    },

    handleResize() {
      if (this.$refs.chartContainer) {
        const chart = echarts.getInstanceByDom(this.$refs.chartContainer)
        if (chart) {
          chart.resize()
        }
      }
    }
  }
}
</script>

<style scoped>
.chart-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
}

.chart-container {
  width: 100%;
  height: 100%;
}
</style>