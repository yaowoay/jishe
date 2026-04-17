<template>
  <div id="salary-parallel-chart" style="width: 100%; height: 100%;"></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'SalaryParallelChart',
  mounted() {
    this.initChart()
  },
  methods: {
    initChart() {
      // 1. 原始数据
      const rawData = [
        ['AI与大数据分析工程师', '本科', '5-7年经验', '5000-10000人', '国企', 50000],
        ['AI与大数据分析工程师', '本科', '5-7年经验', '5000-10000人', '国企', 50000],
        ['AI与大数据分析工程师', '硕士', '3-4年经验', '5000-10000人', '国企', 50000],
        ['AI与大数据分析工程师', '本科', '无需经验', '5000-10000人', '国企', 50000],
        ['AI与大数据分析工程师', '本科', '10年以上经验', '5000-10000人', '国企', 50000],
        ['大数据架构师', '本科', '2年经验', '150-500人', '已上市', 50000],
        ['大数据架构师', '大专', '无需经验', '150-500人', '已上市', 50000],
        ['大数据架构师', '本科', '2年经验', '150-500人', '已上市', 50000],
        ['大数据架构师', '本科', '3-4年经验', '150-500人', '已上市', 50000],
        ['大数据架构师', '大专', '2年经验', '150-500人', '已上市', 50000],
        ['大数据解决方案架构师', '本科', '3-4年经验', '10000人以上', '外资（欧美）', 45000],
        ['大数据解决方案架构师', '本科', '5-7年经验', '10000人以上', '外资（欧美）', 45000],
        ['大数据解决方案架构师', '大专', '3-4年经验', '10000人以上', '外资（欧美）', 45000],
        ['大数据解决方案架构师', '本科', '8-9年经验', '10000人以上', '外资（欧美）', 45000],
        ['大数据解决方案架构师', '本科', '3-4年经验', '10000人以上', '外资（欧美）', 45000],
        ['资深大数据工程师', '本科', '无需经验', '1000-5000人', '国企', 37500],
        ['资深大数据工程师', '大专', '1年经验', '1000-5000人', '国企', 37500],
        ['资深大数据工程师', '本科', '5-7年经验', '1000-5000人', '国企', 37500],
        ['资深大数据工程师', '本科', '2年经验', '1000-5000人', '国企', 37500],
        ['资深大数据工程师', '本科', '5-7年经验', '1000-5000人', '国企', 37500],
        ['高级大数据工程师', '本科', '3-4年经验', '50-150人', '民营', 29500],
        ['高级大数据工程师', '硕士', '10年以上经验', '50-150人', '民营', 29500],
        ['高级大数据工程师', '本科', '5-7年经验', '50-150人', '民营', 29500],
        ['高级大数据工程师', '本科', '3-4年经验', '50-150人', '民营', 29500],
        ['高级大数据工程师', '本科', '2年经验', '50-150人', '民营', 29500]
        // ['Java大数据开发工程师', '本科', '3-4年经验', '1000-5000人', '外资（欧美）', 27500],
        // ['Java大数据开发工程师', '大专', '2年经验', '1000-5000人', '外资（欧美）', 27500],
        // ['Java大数据开发工程师', '本科', '5-7年经验', '1000-5000人', '外资（欧美）', 27500],
        // ['Java大数据开发工程师', '本科', '5-7年经验', '1000-5000人', '外资（欧美）', 27500],
        // ['Java大数据开发工程师', '本科', '3-4年经验', '1000-5000人', '外资（欧美）', 27500],
        // ['splunk大数据安全分析工程师', '本科', '2年经验', '150-500人', '已上市', 25000],
        // ['splunk大数据安全分析工程师', '本科', '3-4年经验', '150-500人', '已上市', 25000],
        // ['splunk大数据安全分析工程师', '本科', '5-7年经验', '150-500人', '已上市', 25000],
        // ['splunk大数据安全分析工程师', '硕士', '2年经验', '150-500人', '已上市', 25000],
        // ['splunk大数据安全分析工程师', '本科', '3-4年经验', '150-500人', '已上市', 25000]
      ]

      // 2. 分类数据数值映射规则
      // 映射改为 0-based 索引，确保与 parallelAxis 中的 category 顺序对齐
      const educationMap = { '大专': 0, '本科': 1, '硕士': 2 }
      const experienceMap = { '无需经验': 0, '1年经验': 1, '2年经验': 2, '3-4年经验': 3.5, '5-7年经验': 6, '8-9年经验': 8.5, '10年以上经验': 10 }
      const companyScaleMap = { '50-150人': 0, '150-500人': 1, '1000-5000人': 2, '5000-10000人': 3, '10000人以上': 4 }
      const companyTypeMap = { '民营': 0, '国企': 1, '已上市': 2, '外资（欧美）': 3 }

      // 3. 按职位名称分组数据
      const groupByPosition = {}
      rawData.forEach(item => {
        const [position, edu, exp, scale, type, salary] = item
        // 转换为数值数组，保留原始信息用于tooltip
        const processedItem = [
          educationMap[edu],
          experienceMap[exp],
          companyScaleMap[scale],
          companyTypeMap[type],
          salary / 1000,
          position,
          edu,
          exp,
          scale,
          type
        ]
        // 分组
        if (!groupByPosition[position]) {
          groupByPosition[position] = []
        }
        groupByPosition[position].push(processedItem)
      })

      // 4. 提取职位列表（作为图例数据）
      const positionList = Object.keys(groupByPosition)

      // 5. 定义维度schema（移除职位维度）
      const schema = [
        { name: 'education', index: 0, text: '学历' },
        { name: 'experience', index: 1, text: '工作经验(年)' },
        { name: 'companyScale', index: 2, text: '公司规模(人)' },
        { name: 'companyType', index: 3, text: '公司类型' },
        { name: 'salary', index: 4, text: '薪资(k/月)' },
        { name: 'positionName', index: 5, text: '职位名称' },
        { name: 'eduName', index: 6, text: '学历名称' },
        { name: 'expName', index: 7, text: '经验名称' },
        { name: 'scaleName', index: 8, text: '公司规模名称' },
        { name: 'typeName', index: 9, text: '公司类型名称' }
      ]

      // 6. 初始化图表
      const chartDom = document.getElementById('salary-parallel-chart')
      const myChart = echarts.init(chartDom)
      const lineStyle = { normal: { width: 1, opacity: 0.7 } }

      // 7. 构建series数组（每个职位一个series）
      const series = positionList.map(position => ({
        name: position,
        type: 'parallel',
        lineStyle: lineStyle,
        data: groupByPosition[position]
      }))

      // 8. 图表配置项
      const option = {
        title: {
          text: '薪资影响因素分析',
          left: 'center',
          textStyle: {
            color: '#66ccff',
            fontSize: 14
          }
        },
        backgroundColor: 'transparent',
        // 图例：按职位名称展示
        legend: {
          top: '80%',
          bottom: 5,
          left: 'center',
          data: positionList,
          textStyle: { color: '#fff', fontSize: 10 },
          itemWidth: 12,
          itemHeight: 8,
          itemGap: 15
        },
        tooltip: {
          padding: 10,
          backgroundColor: '#222',
          borderColor: '#777',
          borderWidth: 1,
          formatter: function (obj) {
            // 支持 params 为数组（trigger: 'axis'）或单个对象（trigger: 'item'）
            const params = Array.isArray(obj) ? obj[0] : obj
            if (!params || !params.value) return ''
            const value = params.value
            const position = value[5] || ''
            const edu = value[6] || ''
            const exp = value[7] || ''
            const scale = value[8] || ''
            const type = value[9] || ''
            const salary = (typeof value[4] !== 'undefined') ? value[4] : ''

            return `<div style="border-bottom: 1px solid rgba(255,255,255,.3); font-size: 16px;padding-bottom: 7px;margin-bottom: 7px">
                      ${position}
                    </div>
                    学历：${edu}<br>
                    工作经验：${exp}<br>
                    公司规模：${scale}<br>
                    公司类型：${type}<br>
                    薪资：${salary} 千/月<br>`
          }
        },
        // 平行坐标轴：5个核心维度
        parallelAxis: [
          {
            dim: 0,
            name: schema[0].text,
            type: 'category',
            data: ['大专', '本科', '硕士'],
            nameTextStyle: { color: '#fff', fontSize: 12 },
            axisLine: { lineStyle: { color: '#aaa' } },
            axisTick: { lineStyle: { color: '#777' } },
            axisLabel: { textStyle: { color: '#fff' } }
          },
          {
            dim: 1,
            name: schema[1].text,
            max: 10,
            nameTextStyle: { color: '#fff', fontSize: 12 },
            axisLine: { lineStyle: { color: '#aaa' } },
            axisTick: { lineStyle: { color: '#777' } },
            axisLabel: { textStyle: { color: '#fff' } }
          },
          {
            dim: 2,
            name: schema[2].text,
            type: 'category',
            data: ['50-150', '150-500', '1千-5千', '5千-1万', '1万+'],
            nameTextStyle: { color: '#fff', fontSize: 12 },
            axisLine: { lineStyle: { color: '#aaa' } },
            axisTick: { lineStyle: { color: '#777' } },
            axisLabel: { textStyle: { color: '#fff' } }
          },
          {
            dim: 3,
            name: schema[3].text,
            type: 'category',
            data: ['民营', '国企', '已上市', '外资(欧美)'],
            nameTextStyle: { color: '#fff', fontSize: 12 },
            axisLine: { lineStyle: { color: '#aaa' } },
            axisTick: { lineStyle: { color: '#777' } },
            axisLabel: { textStyle: { color: '#fff' } }
          },
          {
            dim: 4,
            name: schema[4].text,
            max: 50,
            nameTextStyle: { color: '#fff', fontSize: 12 },
            axisLine: { lineStyle: { color: '#aaa' } },
            axisTick: { lineStyle: { color: '#777' } },
            axisLabel: { textStyle: { color: '#fff' } }
          }
        ],
        // 视觉映射：按薪资着色
        // visualMap: {
        //   show: true,
        //   min: 25,
        //   max: 50,
        //   right: 20,
        //   bottom: 50,
        //   dimension: 4,
        //   calculable: true,
        //   inRange: {
        //     color: ['#50a3ba', '#eac736', '#d94e5d']
        //   },
        //   text: ['薪资（千/月）', ''],
        //   textGap: 20,
        //   textStyle: { color: '#fff' }
        // },
        parallel: {
          // left: '8%',
          // right: '15%',
          // bottom: 120,
          top: '28%',          // 绘图区距离容器顶部像素（增加以扩大上方空白）
          bottom: '23%',       // 绘图区距离容器底部像素
          left: '6%',       // 左侧边距，减小会增大轴间距，增大会减小轴间距
          right: '6%',      // 右侧边距
          width: '80%',
          parallelAxisDefault: {
            nameLocation: 'end',
            nameGap: 10
          }
        },
        series: series
      }

      // 渲染图表
      myChart.setOption(option)
      window.addEventListener('resize', () => myChart.resize())
    }
  }
}
</script>

<style scoped>
#salary-parallel-chart {
  margin: 0 auto;
}
</style>