<template>
  <div class="single-pie-chart-container">
    <!-- 只保留企业类型环形图 -->
    <div ref="chartRef" class="chart-item"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'EnterpriseTypeRingChart',
  data() {
    return {
      // 仅保留企业类型数据
      enterpriseTypeData: [
        { name: '民营', value: 1220 },
        { name: '国企', value: 820 },
        { name: '已上市', value: 691 },
        { name: '外资（欧美）', value: 301 },
        { name: '合资', value: 153 },
        { name: '外资（非欧美）', value: 125 },
        { name: '事业单位', value: 69 }
      ],
      // 配色方案
      colorList: ['#4A90E2', '#9787ff', '#fdb36a', '#fdd56a', '#6da7ff', '#63e1f2', '#a8e6cf'],
      chart: null // 只保留一个图表实例
    }
  },
  mounted() {
    this.initChart()
  },
  beforeUnmount() {
    // 销毁图表实例，防止内存泄漏
    if (this.chart) {
      this.chart.dispose()
    }
  },
  methods: {
    /**
     * 初始化企业类型环形图
     */
    initChart() {
      // 初始化图表
      this.chart = echarts.init(this.$refs.chartRef)
      this.chart.setOption(this.getChartOption('企业类型', this.enterpriseTypeData))

      // 监听窗口大小变化，自适应图表
      window.addEventListener('resize', () => {
        this.chart?.resize()
      })
    },

    /**
     * 处理原始数据，转换成ECharts需要的格式
     * @param {Array} rawData 原始数据
     * @returns {Array} 处理后的数据
     */
    processData(rawData) {
      const processedData = []
      rawData.forEach((item, index) => {
        // 实际数据项
        processedData.push({
          value: item.value,
          name: item.name,
          itemStyle: {
            normal: {
              borderWidth: 5,
              shadowBlur: 20,
              borderColor: this.colorList[index % this.colorList.length],
              shadowColor: this.colorList[index % this.colorList.length]
            }
          }
        })
        // 空白间隔项
        processedData.push({
          value: 2,
          name: '',
          itemStyle: {
            normal: {
              label: { show: false },
              labelLine: { show: false },
              color: 'rgba(0, 0, 0, 0)',
              borderColor: 'rgba(0, 0, 0, 0)',
              borderWidth: 0
            }
          }
        })
      })
      return processedData
    },

    /**
     * 计算数据总和
     * @param {Array} rawData 原始数据
     * @returns {Number} 总和
     */
    calculateTotal(rawData) {
      return rawData.reduce((total, item) => total + item.value, 0)
    },

    /**
     * 获取图表配置项
     * @param {String} titleName 图表中心标题
     * @param {Array} rawData 原始数据
     * @returns {Object} ECharts配置项
     */
    getChartOption(titleName, rawData) {
      const total = this.calculateTotal(rawData)
      const processedData = this.processData(rawData)

      return {
        backgroundColor: 'transparent',
        color: this.colorList,
        title: [{
          text: titleName,
          top: '44%',
          textAlign: 'center',
          left: '49.50%',
          backgroundColor: '#163253',
          borderRadius: 100,
          textStyle: {
            color: '#fff',
            fontSize: 20,
            fontWeight: '400'
          }
        }, {
          text: total,
          top: '53%',
          textAlign: 'center',
          left: '48%',
          textStyle: {
            color: '#f6ea2f',
            fontSize: 25,
            fontWeight: '800',
            fontStyle: 'italic'
          }
        }, {
          text: '家',
          top: '53.5%',
          textAlign: 'center',
          left: '50.5%',
          textStyle: {
            color: '#fff',
            fontSize: 16,
            fontWeight: '400'
          }
        }],
        tooltip: {
          show: false
        },
        toolbox: {
          show: false
        },
        series: [{
          name: '',
          type: 'pie',
          clockWise: false,
          radius: [45, 50],
          hoverAnimation: false,
          itemStyle: {
            normal: {
              label: {
                show: true,
                position: 'outside',
                formatter: (params) => {
                  if (params.name === '') return ''
                  const percent = ((params.value / total) * 100).toFixed(0)
                  return `${params.name}\t${percent}%`
                }
              },
              labelLine: {
                length: 10,
                length2: 20,
                show: true,
                color: '#00ffff'
              }
            }
          },
          data: processedData
        }]
      }
    }
  }
}
</script>

<style scoped>
.single-pie-chart-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%; /* 确保容器有高度 */
}

.chart-item {
  width: 100%;
  height: 400px; /* 设置固定高度，也可以根据需要调整 */
}

/* 响应式适配 */
@media (max-width: 768px) {
  .chart-item {
    height: 300px;
  }
}
</style>