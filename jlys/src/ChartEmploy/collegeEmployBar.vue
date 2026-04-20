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
      // 就业去向维度（图例）
      degreeData: ['已就业', '待就业', '考研', '考公', '其他'],
      // 工科学院（Y轴）
      positionData: [
        '计算机学院',
        '电子信息学院',
        '机械工程学院',
        '电气工程学院',
        '土木工程学院',
        '人工智能学院',
        '自动化学院'
      ],
      // 真实工科模拟数据：[已就业, 待就业, 考研, 考公, 其他]
      positionDegreeData: {
        '计算机学院':     [820, 45, 210, 68, 32],
        '电子信息学院':   [780, 52, 195, 75, 28],
        '机械工程学院':   [745, 68, 160, 92, 35],
        '电气工程学院':   [760, 55, 140, 110, 30],
        '土木工程学院':   [710, 85, 120, 80, 45],
        '人工智能学院':   [835, 38, 230, 55, 22],
        '自动化学院':     [770, 60, 175, 88, 27]
      },
      legendSelected: {}
    }
  },

  mounted() {
    // 初始化默认选中所有图例
    this.degreeData.forEach(degree => {
      this.legendSelected[degree] = true
    })
    this.renderChart()
    window.addEventListener('resize', this.handleResize)
  },

  beforeUnmount() {
    window.removeEventListener('resize', this.handleResize)
  },

  methods: {
    // 计算占比数据：按每个学院的总数量计算各就业状态占比
    calculatePercentageData() {
      const percentageData = {}

      // 遍历每个学院，计算该学院下各就业状态的占比
      this.positionData.forEach(position => {
        // 1. 计算当前学院总人数
        const total = this.positionDegreeData[position].reduce((sum, count) => sum + count, 0)
        // 2. 计算占比（保留2位小数）
        percentageData[position] = this.positionDegreeData[position].map(count => {
          if (total === 0) return 0
          return Number(((count / total) * 100).toFixed(2))
        })
      })

      return percentageData
    },

    renderChart() {
      const chart = echarts.init(this.$refs.chartContainer)
      // 清除之前的图表
      chart.clear()

      // 获取占比数据
      const percentageData = this.calculatePercentageData()

      const option = {
        backgroundColor: 'transparent', // 透明背景
        title: {
          text: '工科各学院毕业生就业去向分布',
          left: 'center',
          top: '0%',
          textStyle: {
            color: '#66ccff',
            fontSize: 13
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
            lineStyle: {
              color: 'rgba(102, 204, 255, 0.5)',
              width: 2
            }
          },
          backgroundColor: 'rgba(20,40,80,0.85)', // 深蓝半透明高科技风
          borderColor: '#66ccff', // 蓝色边框
          borderWidth: 5,
          borderRadius: 10,
          textStyle: {
            color: '#00eaff', // 高科技亮蓝色字体
            fontSize: 14,
            fontWeight: 'bold',
            fontFamily: 'Consolas, monospace'
          },
          formatter: function(params) {
            let result = params[0].name + '<br/>'
            params.forEach(item => {
              if (item.value > 0) {
                result += `<span style="color:#66ccff;">${item.seriesName}</span>: <span style="color:#fff;">${item.value}%</span><br/>`
              }
            })
            return result
          },
          show: true,
          alwaysShowContent: false
        },
        legend: {
          data: this.degreeData,
          textStyle: {
            color: '#66ccff',
            fontSize: 12
          },
          selected: this.legendSelected,
          top: '10%',
          padding: [10, 10],
          itemGap: 12,
          itemWidth: 12,
          itemHeight: 8,
          backgroundColor: 'transparent',
          borderRadius: 4,
          borderColor: 'rgba(102, 204, 255, 0.3)',
          borderWidth: 1
        },
        grid: {
          top: '25%',
          left: '0%',
          right: '8%',
          bottom: '0%',
          containLabel: true,
          backgroundColor: 'transparent',
          borderColor: 'rgba(102, 204, 255, 0.3)',
          borderWidth: 1
        },
        // X轴：占比（百分比）
        xAxis: [
          {
            type: 'value',
            name: '占比',
            nameTextStyle: {
              color: '#66ccff',
              fontSize: 10
            },
            axisLabel: {
              formatter: '{value}%',
              textStyle: {
                color: '#cce5ff'
              }
            },
            axisLine: {
              lineStyle: {
                color: 'rgba(102, 204, 255, 0.5)'
              }
            },
            splitLine: {
              lineStyle: {
                color: 'rgba(102, 204, 255, 0.1)',
                type: 'dashed'
              }
            },
            max: 100,
            min: 0
          }
        ],
        // Y轴：学院维度
        yAxis: {
          type: 'category',
          data: this.positionData,
          axisLine: {
            lineStyle: {
              color: 'rgba(102, 204, 255, 0.5)'
            }
          },
          axisLabel: {
            textStyle: {
              color: '#cce5ff',
              fontSize: 11
            },
            interval: 0,
            rotate: 0
          },
          axisTick: {
            lineStyle: {
              color: 'rgba(102, 204, 255, 0.3)'
            }
          },
          splitArea: {
            show: true,
            areaStyle: {
              color: ['rgba(102, 204, 255, 0.05)']
            }
          }
        },
        series: [
          ...this.degreeData.map((degree, index) => ({
            name: degree,
            type: 'bar',
            stack: '占比',
            data: this.positionData.map(position => percentageData[position][index]),
            barWidth: 20,
            emphasis: {
              focus: 'series',
              itemStyle: {
                shadowBlur: 10,
                shadowColor: 'rgba(102, 204, 255, 0.5)'
              }
            },
            itemStyle: {
              color: this.getDegreeColor(degree),
              borderColor: 'rgba(255, 255, 255, 0.1)',
              borderWidth: 1
            }
          }))
        ],
        emphasis: {
          disabled: false
        },
        animationDuration: 1000,
        animationEasing: 'cubicOut',
        animationDelay: function (idx) {
          return idx * 50
        }
      }

      chart.setOption(option)

      // 图例选择事件
      chart.on('legendselectchanged', (params) => {
        this.legendSelected = params.selected
        chart.setOption({
          series: this.degreeData.map((degree) => ({
            name: degree,
            show: params.selected[degree]
          }))
        })
      })

      this.handleResize()
    },

    // 完全沿用你原有颜色 + 新增同色系蓝色
    getDegreeColor(degree) {
      const colors = {
        '已就业': new echarts.graphic.LinearGradient(1, 0, 0, 0, [
          { offset: 0, color: '#0066ff' },
          { offset: 1, color: '#003399' }
        ]),
        '待就业': new echarts.graphic.LinearGradient(1, 0, 0, 0, [
          { offset: 0, color: '#00ccff' },
          { offset: 1, color: '#006699' }
        ]),
        '考研': new echarts.graphic.LinearGradient(1, 0, 0, 0, [
          { offset: 0, color: '#3399ff' },
          { offset: 1, color: '#003366' }
        ]),
        '考公': new echarts.graphic.LinearGradient(1, 0, 0, 0, [
          { offset: 0, color: '#66ccff' },
          { offset: 1, color: '#336699' }
        ]),
        // 新增：同色系蓝色
        '其他': new echarts.graphic.LinearGradient(1, 0, 0, 0, [
          { offset: 0, color: '#4D9DFF' },
          { offset: 1, color: '#1A59B8' }
        ])
      }
      return colors[degree]
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