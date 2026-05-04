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
      // 学历维度（图例）
      degreeData: ['博士', '硕士', '本科', '大专'],
      // 职位维度（Y轴）
      positionData: [
        '大数据开发工程师', 
        '大数据工程师', 
        '高级大数据开发工程师', 
        '大数据研发工程师', 
        '高级大数据工程师', 
        '大数据分析工程师', 
        '大数据开发助理'
      ],
      // 原始职位-学历数量数据
      positionDegreeData: {
        '大数据开发工程师': [13, 79, 925, 313],
        '大数据工程师': [0, 18, 191, 53],
        '高级大数据开发工程师': [1, 9, 151, 59],
        '大数据研发工程师': [0, 8, 86, 32],
        '高级大数据工程师': [5, 4, 62, 24],
        '大数据分析工程师': [3, 8, 57, 24],
        '大数据开发助理': [1, 6, 54, 19]
      },
      legendSelected: {}
    }
  },

  mounted() {
    // 初始化默认选中所有图例（学历）
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
    // 计算占比数据：按每个职位的总数量计算各学历的占比
    calculatePercentageData() {
      const percentageData = {}
      
      // 遍历每个职位，计算该职位下各学历的占比
      this.positionData.forEach(position => {
        // 1. 计算当前职位的总岗位数量
        const total = this.positionDegreeData[position].reduce((sum, count) => sum + count, 0)
        // 2. 计算每个学历的占比（保留2位小数）
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
          text: '大数据相关职位学历要求占比分布', // 新标题
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
                // 显示学历对应的占比（百分比）
                result += `<span style="color:#66ccff;">${item.seriesName}</span>: <span style="color:#fff;">${item.value}%</span><br/>`
              }
            })
            return result
          },
          show: true,
          alwaysShowContent: false
        },
        legend: {
          data: this.degreeData, // 图例为学历维度
          textStyle: {
            color: '#333',
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
          left: '0%', // 增加左侧间距，适配职位名称
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
            name: '占比', // X轴名称改为占比
            nameTextStyle: {
              color: '#333',
              fontSize: 10
            },
            axisLabel: {
              formatter: '{value}%', // 显示百分比符号
              textStyle: {
                color: '#333'
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
            max: 100, // 占比最大值100%
            min: 0    // 占比最小值0%
          }
        ],
        // Y轴：职位维度
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
              color: '#333',
              fontSize: 11 // 缩小字体适配职位名称长度
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
          // 系列数据：学历维度的占比
          ...this.degreeData.map((degree, index) => ({
            name: degree,
            type: 'bar',
            stack: '占比', // 堆叠名称改为占比
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
              color: this.getDegreeColor(degree), // 学历对应颜色
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

      // 图例选择事件（学历维度）
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

    // 为每个学历分配渐变颜色
    getDegreeColor(degree) {
      const colors = {
        '博士': new echarts.graphic.LinearGradient(1, 0, 0, 0, [ // 横向渐变适配横向柱状图
          { offset: 0, color: '#0066ff' },
          { offset: 1, color: '#003399' }
        ]),
        '硕士': new echarts.graphic.LinearGradient(1, 0, 0, 0, [
          { offset: 0, color: '#00ccff' },
          { offset: 1, color: '#006699' }
        ]),
        '本科': new echarts.graphic.LinearGradient(1, 0, 0, 0, [
          { offset: 0, color: '#3399ff' },
          { offset: 1, color: '#003366' }
        ]),
        '大专': new echarts.graphic.LinearGradient(1, 0, 0, 0, [
          { offset: 0, color: '#66ccff' },
          { offset: 1, color: '#336699' }
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