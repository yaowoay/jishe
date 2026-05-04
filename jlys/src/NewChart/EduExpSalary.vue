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
      experienceData: ['1年以下', '1-5年', '5-7年', '7-10年', '10年以上', '经验不限'],
      degreeData: ['博士', '学历不限', '硕士', '统招本科', '本科', '大专'],
      salaryData: {
        博士: [59.17, 53.2, 77.3, 71.5, 114.27, 57.23],
        学历不限: [50.5, 62.4, 71.2, 115.34, 60.74, 0],
        硕士: [31.41, 52.8, 78.5, 72.5, 91.23, 51.15],
        统招本科: [1.5, 48.5, 72.1, 82.3, 97.92, 53.02],
        本科: [38.2, 54.8, 51.3, 54.8, 98.52, 43.84],
        大专: [45.0, 29.85, 29.85, 29.85, 0, 0]
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
    renderChart() {
      const chart = echarts.init(this.$refs.chartContainer)

      // 清除之前的图表
      chart.clear()

      const option = {
        backgroundColor: 'transparent', // 透明背景
        title: {
          text: '不同学历经验薪资对比图',
          left: 'center',
          top: '0%',  // 标题位置保持在顶部
          textStyle: {
            color: '#66ccff',
            fontSize: 13
          }
        },
        // ...existing code...
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
                result += `<span style="color:#66ccff;">${item.seriesName}</span>: <span style="color:#fff;">${item.value}元</span><br/>`
              }
            })
            return result
          },
          show: true,
          alwaysShowContent: false
        },
        // ...existing code...
        legend: {
          data: this.degreeData,
          textStyle: {
            color: '#66ccff',
            fontSize: 12
          },
          selected: this.legendSelected,
          top: '10%',  // 调整图例位置，为标题留出空间
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
          top: '20%',  // 增加top值使图表整体下移
          left: '4%',
          right: '6%',
          bottom: '2%',
          containLabel: true,
          backgroundColor: 'transparent',
          borderColor: 'rgba(102, 204, 255, 0.3)',
          borderWidth: 1
        },
        xAxis: {
          type: 'category',
          data: this.experienceData,
          axisLine: {
            lineStyle: {
              color: 'rgba(102, 204, 255, 0.5)'
            }
          },
          axisLabel: {
            textStyle: {
              color: '#cce5ff',
              fontSize: 12
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
        yAxis: [
          {
            type: 'value',
            name: '平均薪资',
            nameTextStyle: {
              color: '#66ccff',
              fontSize: 10
            },
            position: 'left',
            axisLabel: {
              formatter: '{value}元',
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
            }
          }
        ],
        series: [
          ...this.degreeData.map((degree) => ({
            name: degree,
            type: 'bar',
            stack: '薪资',
            data: this.salaryData[degree],
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
              // borderRadius: [4, 4, 0, 0],
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

      chart.on('legendselectchanged', (params) => {
        this.legendSelected = params.selected
        chart.setOption({
          series: this.degreeData.map((degree) => ({
            name: degree,
            show: params.selected[degree]
          }))
        })
      })

      // 移除可能冲突的点击事件，让默认的悬停事件正常工作
      // 如果需要点击也显示，保留这个事件但确保它不会影响悬停

      this.handleResize()
    },

    getDegreeColor(degree) {
      const colors = {
        博士: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#0066ff' },
          { offset: 1, color: '#003399' }
        ]),
        学历不限: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#00ccff' },
          { offset: 1, color: '#006699' }
        ]),
        硕士: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#3399ff' },
          { offset: 1, color: '#003366' }
        ]),
        统招本科: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#66ccff' },
          { offset: 1, color: '#336699' }
        ]),
        本科: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#99ddff' },
          { offset: 1, color: '#6699cc' }
        ]),
        大专: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#ccf0ff' },
          { offset: 1, color: '#99ccdd' }
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
  height: 100%; /* 增加高度以适应下移的图表 */
  position: relative;

}

.chart-container {
  width: 100%;
  height: 100%;
}
</style>
