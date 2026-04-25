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
      chartInstance: null,
      // 学院
      collegeList: [
        '人工智能与大数据学院',
        '信息科学与工程学院',
        '外国语学院',
        '建筑学院',
        '机械工程学院',
        '法学院',
        '经济管理学院'
      ],
      // 各就业去向 按学院顺序数量
      dataList: {
        '出国': [88, 152, 149, 103, 117, 102, 111],
        '创业': [96, 142, 116, 127, 133, 119, 112],
        '已就业': [86, 151, 119, 115, 124, 107, 122],
        '待业': [98, 145, 100, 128, 107, 101, 132],
        '考公': [80, 163, 133, 120, 130, 111, 113],
        '考研': [78, 158, 127, 123, 116, 96, 111]
      }
    }
  },

  mounted() {
    this.chartInstance = echarts.init(this.$refs.chartContainer)
    window.addEventListener('resize', this.handleResize)
    this.renderChart()
  },

  beforeUnmount() {
    window.removeEventListener('resize', this.handleResize)
    this.chartInstance?.dispose()
  },

  methods: {
    // 计算百分比数据
    getPercentData() {
      const keys = Object.keys(this.dataList)
      const collegeCount = this.collegeList.length
      const percentData = {}

      // 遍历每个学院
      for (let i = 0; i < collegeCount; i++) {
        // 计算当前学院总人数
        let total = 0
        keys.forEach(key => {
          total += this.dataList[key][i]
        })

        // 计算每个去向占比
        keys.forEach(key => {
          if (!percentData[key]) percentData[key] = []
          const percent = ((this.dataList[key][i] / total) * 100).toFixed(2)
          percentData[key].push(Number(percent))
        })
      }
      return percentData
    },

    renderChart() {
      const chart = this.chartInstance
      const statusArr = Object.keys(this.dataList)
      const percentData = this.getPercentData()

      // 系列数据
      const series = statusArr.map(status => ({
        name: status,
        type: 'bar',
        stack: 'total',
        data: percentData[status],
        barWidth: 20,
        itemStyle: {
          color: this.getBlueGradientColor(status)
        }
      }))

      const option = {
        backgroundColor: 'transparent',
        title: {
          text: '不同学院就业去向占比',
          left: 'center',
          textStyle: { color: '#9FB7D8', fontSize: 14 }
        },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(33,51,77,0.9)',
          borderColor: '#7E9CC2',
          textStyle: { color: '#fff' },
          formatter: params => {
            let tip = params[0].name + '<br/>'
            params.forEach(item => {
              tip += `${item.seriesName}：${item.value}%<br/>`
            })
            return tip
          }
        },
        legend: {
          data: statusArr,
          top: '8%',
          textStyle: { color: '#9FB7D8' }
        },
        grid: {
          left: '3%',
          right: '5%',
          top: '22%',
          bottom: '0%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          name: '占比(%)',
          min: 0,
          max: 100,
          interval: 20,
          axisLine: { lineStyle: { color: 'rgba(126,156,194,0.65)' } },
          axisLabel: {
            color: '#B7C9E2',
            formatter: '{value}%'
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(126,156,194,0.2)'
            }
          }
        },
        yAxis: {
          type: 'category',
          data: this.collegeList,
          axisLine: { lineStyle: { color: 'rgba(126,156,194,0.65)' } },
          axisLabel: { color: '#B7C9E2', fontSize: 11 }
        },
        series
      }

      chart.setOption(option)
      this.handleResize()
    },

    // 统一蓝色系渐变（无红黄色，清爽渐变）
    getBlueGradientColor(status) {
      // 全套蓝色系配色，无任何红色/黄色
      const bluePalette = {
        '出国': ['#C6E2FF', '#8AB4F8'],
        '创业': ['#B3D8FA', '#79A9F5'],
        '已就业': ['#A0CEF5', '#689Ff2'],
        '待业': ['#8DC4F0', '#5794EE'],
        '考公': ['#7AB9EB', '#468AEA'],
        '考研': ['#67AFE6', '#357FE6']
      }
      const [startColor, endColor] = bluePalette[status] || ['#A0CEF5', '#689Ff2']
      return new echarts.graphic.LinearGradient(1, 0, 0, 0, [
        { offset: 0, color: startColor },
        { offset: 1, color: endColor }
      ])
    },

    handleResize() {
      this.chartInstance?.resize()
    }
  }
}
</script>

<style scoped>
.chart-wrapper {
  width: 100%;
  height: 100%;
}
.chart-container {
  width: 100%;
  height: 100%;
}
</style>