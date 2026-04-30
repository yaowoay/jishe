<template>
  <div class="salary-distribution">
    <!-- 页面标题 - 增加操作按钮 -->
    <div class="page-header">
      <div class="title-section">
        <h1>
          <el-icon><TrendCharts /></el-icon>
          薪资分布分析
        </h1>
        <p class="subtitle">基于市场数据的薪资维度深度分析</p>
      </div>
      <div class="action-buttons">
        <el-button icon="Refresh" @click="refreshData" type="primary" plain>
          刷新数据
        </el-button>
        <el-button icon="Download" @click="exportAnalysis" type="success" plain>
          导出分析
        </el-button>
      </div>
    </div>

    <!-- 快速概览数据卡片 - 改为Flex布局 -->
    <div class="quick-stats">
      <div class="stats-row">
        <div class="stat-col">
          <el-card class="stat-card">
            <div class="stat-content">
              <p class="stat-label">市场平均薪资</p>
              <p class="stat-value">26.8K/月</p>
              <p class="stat-trend positive">↑ 5.2% 较上季度</p>
            </div>
          </el-card>
        </div>
        <div class="stat-col">
          <el-card class="stat-card">
            <div class="stat-content">
              <p class="stat-label">高薪岗位占比</p>
              <p class="stat-value">18.3%</p>
              <p class="stat-trend positive">↑ 2.1% 较上季度</p>
            </div>
          </el-card>
        </div>
        <div class="stat-col">
          <el-card class="stat-card">
            <div class="stat-content">
              <p class="stat-label">薪资中位数</p>
              <p class="stat-value">24.5K/月</p>
              <p class="stat-trend positive">↑ 3.7% 较上季度</p>
            </div>
          </el-card>
        </div>
        <div class="stat-col">
          <el-card class="stat-card">
            <div class="stat-content">
              <p class="stat-label">最大薪资差距</p>
              <p class="stat-value">60K/月</p>
              <p class="stat-trend negative">↓ 1.2% 较上季度</p>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 分析卡片区域 - 四个卡片一行显示 -->
    <div class="analysis-cards">
      <div class="analysis-row">
        <div class="salary-card">
          <h3>城市-行业交叉分析</h3>
          <p><span class="highlight">北京金融AI</span>: 34.2K vs <span class="highlight">上海金融AI</span>: 32.8K</p>
          <p><span class="highlight">深圳互联网</span>: 28.5K vs <span class="highlight">杭州互联网</span>: 26.9K</p>
          <p><span class="highlight">广州教育科技</span>: 23.1K vs <span class="highlight">成都教育科技</span>: 20.4K</p>
        </div>
        <div class="salary-card">
          <h3>薪资与学历关系</h3>
          <p><span class="highlight">硕士</span>比<span class="highlight">本科</span>平均高22%</p>
          <p><span class="highlight">博士</span>比<span class="highlight">硕士</span>平均高18%</p>
          <p>名校背景带来约15%薪资溢价</p>
        </div>
        <div class="salary-card">
          <h3>经验与薪资增长</h3>
          <p><span class="highlight">5年以上经验</span>是3年经验的1.8倍</p>
          <p><span class="highlight">10年以上资深专家</span>薪资可达应届生5倍</p>
          <p>技术岗经验溢价高于非技术岗</p>
        </div>
      </div>
    </div>

    <!-- 图表容器 - 四个图表一行显示 -->
    <div class="all-charts-container">
      <!-- 1. 整体薪资区间分布 -->
      <div class="chart-container">
        <div class="chart-header">
          <h3>整体薪资区间分布</h3>
          <p>不同薪资区间的岗位数量分布</p>
        </div>
        <div ref="salaryRangeChart" class="chart"></div>
      </div>

      <!-- 2. 一线城市薪资对比 -->
      <div class="chart-container">
        <div class="chart-header">
          <h3>一线城市薪资对比</h3>
          <p>北上广深薪资分布差异</p>
        </div>
        <div ref="cityComparisonChart" class="chart"></div>
      </div>

      <!-- 3. 热门行业薪资分布 -->
      <div class="chart-container">
        <div class="chart-header">
          <h3>热门行业薪资特征</h3>
          <p>Top7行业薪资范围对比</p>
        </div>
        <div ref="industrySalaryChart" class="chart"></div>
      </div>

      <!-- 4. 复合因素薪资分析 -->
      <div class="chart-container">
        <div class="chart-header">
          <h3>高薪职位复合因素分析</h3>
          <p>学历、经验与企业规模对薪资的综合影响</p>
        </div>
        <div ref="multiFactorChart" class="chart"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'  
import { TrendCharts, Refresh, Download } from '@element-plus/icons-vue'  
import { ElMessage, ElCard, ElButton } from 'element-plus'  
import * as echarts from 'echarts'  

// 图表实例引用
const salaryRangeChart = ref(null)  
const cityComparisonChart = ref(null)  
const industrySalaryChart = ref(null)  
const multiFactorChart = ref(null)  

// 保存ECharts实例，用于后续销毁和重绘
const chartInstances = {
  salaryRange: null,
  cityComparison: null,
  industrySalary: null,
  multiFactor: null
}  

// 刷新数据
const refreshData = () => {
  ElMessage.success('数据已刷新')  
  // 先销毁旧实例再重新初始化
  disposeAllCharts()  
  // 等待DOM更新后再初始化
  nextTick(() => {
    initCharts()  
  })}  

// 导出分析
const exportAnalysis = () => {
  ElMessage.success('分析报告导出中...')  
  // 这里可以添加实际的导出逻辑
}  

// 初始化所有图表
const initCharts = () => {
  try {
    initSalaryRangeChart()
    initCityComparisonChart()
    initIndustrySalaryChart()
    initMultiFactorChart()
  } catch (error) {
    console.error('图表初始化失败:', error)
    ElMessage.error('部分图表初始化失败，请刷新页面重试')
  }
}

// 检查DOM元素是否有效
const isValidDom = (dom) => {
  if (!dom) return false  
  // 检查元素是否在文档中
  if (!document.body.contains(dom)) return false  
  // 检查元素尺寸是否有效
  const rect = dom.getBoundingClientRect()  
  return rect.width > 0 && rect.height > 0  
}  
// 生成模拟数据：薪资区间和对应的职位数量
const generateMockData = () => {
  // 薪资区间（k）
  const salaryRanges = [
    '5k-10k', '10k-15k', '15k-20k', '20k-25k',
    '25k-30k', '30k-35k', '35k-40k', '40k-45k', '45k-50k'
  ]  

  // 生成模拟的职位数量（基于正态分布模拟集中趋势）
  const baseCount = 120  
  const peakIndex = 3   // 峰值位置（20k-25k）
  const counts = salaryRanges.map((_, index) => {
    // 使用正态分布公式生成数据，使数据呈现集中趋势
    const distance = Math.abs(index - peakIndex)  
    return Math.round(baseCount * Math.exp(-distance * distance / 3) + Math.random() * 20)  
  })  

  return { salaryRanges, counts }  
}  

// 生成密度曲线数据
const generateDensityData = (counts) => {
  // 对直方图数据进行平滑处理生成密度曲线
  const density = []  
  // 前延和后延数据点使曲线更平滑
  const extendedCounts = [0, ...counts, 0]  

  for (let i = 1  ; i < extendedCounts.length - 1  ; i++) {
    // 三点平滑处理
    density.push((extendedCounts[i - 1] + extendedCounts[i] * 2 + extendedCounts[i + 1]) / 4)  
  }

  return density  
}  

// 1. 整体薪资区间分布图表
const initSalaryRangeChart = () => {
  if (!isValidDom(salaryRangeChart.value)) return  
  
  // 如果已有实例，先销毁
  if (chartInstances.salaryRange) {
    chartInstances.salaryRange.dispose()  
  }
  // 获取数据
  const { salaryRanges, counts } = generateMockData()  
  const densityData = generateDensityData(counts)  

  chartInstances.salaryRange = echarts.init(salaryRangeChart.value)  
  // 图表配置
  const option = {
    // title: {
    //   text: '薪资分布与职位数量趋势',
    //   left: 'center',
    //   textStyle: {
    //     color: '#66ccff',
    //     fontSize: 13
    //   }
    // },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params) {
        const range = params[0].name  
        const count = params[0].value  
        return `${range}薪资区间<br/>职位数量: ${count}个`  
      }
    },
    legend: {
      data: ['职位数量', '分布趋势'],
      top: 30,
      textStyle: {
        color: '#333333' // 图例文字设置为白色
      }
    },
    grid: {
      left: '3%',
      right: -20,
      bottom: '0%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: salaryRanges,
      axisLabel: {
        interval: 0,
        rotate: 30,
        color: '#333333'  // X轴刻度文字设为白色
      },
      //name: '薪资区间',
      nameTextStyle: {
        //padding: [10, 0, 0, 0],
        color: '#333333'  // X轴名称设为白色
      },
      axisLine: {
        lineStyle: {
          color: '#333333'  // X轴线设为白色
        }
      },
      axisTick: {
        lineStyle: {
          color:'#333333' // X轴刻度线设为白色
        }
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '职位数量',
        nameTextStyle: {
          //padding: [0, 0, 0, 10],
          color: '#333333' // Y轴名称设为白色
        },
        splitLine: {
          lineStyle: {
            type: 'dashed',
            color: 'rgba(255, 255, 255, 0.3)' // 分割线设为半透明白色
          }
        },
        axisLabel: {
          color: 'black' // Y轴刻度文字设为白色
        },
        axisLine: {
          lineStyle: {
            color: 'black' // Y轴线设为白色
          }
        },
        axisTick: {
          lineStyle: {
            color: 'black' // Y轴刻度线设为白色
          }
        }
      },
      {
        type: 'value',
        show: false, // 隐藏第二个Y轴
        max: Math.max(...counts) * 1.2
      }
    ],
    series: [
      {
        name: '职位数量',
        type: 'bar',
        data: counts,
        barWidth: '60%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#4facfe' },
            { offset: 1, color: '#00f2fe' }
          ])
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#00f2fe' },
              { offset: 1, color: '#4facfe' }
            ])
          }
        }
      },
      {
        name: '分布趋势',
        type: 'line',
        data: densityData,
        yAxisIndex: 1,
        smooth: true, // 平滑曲线
        symbol: 'circle',
        symbolSize: 6,
        showSymbol: false,
        emphasis: {
          showSymbol: true
        },
        lineStyle: {
          width: 3,
          color: 'rgba(4,241,241,0.36)'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255, 125, 0, 0.3)' },
            { offset: 1, color: 'rgba(255, 125, 0, 0)' }
          ])
        }
      }
    ]
  }  
  chartInstances.salaryRange.setOption(option)  
}  

// 城市薪资区域数据
const generateCityData = () => {
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
// 获取数据
const { salaryRanges, cities } = generateCityData()  
  

// 2. 一线城市薪资对比图表
const initCityComparisonChart = () => {
  if (!isValidDom(cityComparisonChart.value)) return  
  
  if (chartInstances.cityComparison) {
    chartInstances.cityComparison.dispose()  
  }
  
  chartInstances.cityComparison = echarts.init(cityComparisonChart.value)  
  // 配置图表选项
  const option = {
  // title: {
  //   text: '一线城市薪资对比图',
  //   left: 'center',
  //   top: '0%',  // 标题位置保持在顶部
  //   textStyle: {
  //     color: '#333333', // 深色文字适配浅色背景
  //     fontSize: 13
  //   },
  // },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)', // 浅色背景
      borderColor: 'rgba(200, 200, 200, 0.8)',      // 浅灰色边框
      borderWidth: 1,
      textStyle: {
        color: '#333333' // 深色文字
      },
      formatter: function(params) {
      // 显示为职位数量（个），不再使用百分比
        let result = `${params[0].name}<br/>`
        params.forEach(item => {
          result += `${item.seriesName}: ${item.value} 个<br/>`
        })
        return result
      }, 
      // 增加阴影提升提示框层次感
      shadowBlur: 5,
      shadowColor: 'rgba(0, 0, 0, 0.1)'
    },
    legend: {
      top: '5%',
      right: '2%',
      itemGap: 20,
      icon: 'circle',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: {
        color: '#333333' // 深灰色文字，比纯黑更柔和
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
            color: '#555555' // 中灰色文字
          },
          rotate: 30
        },
        axisLine: {
          lineStyle: {
            color: 'rgba(200, 200, 200, 0.8)' // 浅灰色轴线
          }
        },
        axisTick: {
          lineStyle: {
            color: 'rgba(200, 200, 200, 0.6)' // 浅灰色刻度
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
            color: 'rgba(220, 220, 220, 0.8)' // 浅灰色分割线
          }
        },
        axisLabel: {
          textStyle: {
            color: '#555555' // 中灰色文字
          },
          // 显示为数量，带单位“个”
          formatter: '{value} 个'
        },
        axisLine: {
          show: false
        },
        nameTextStyle: {
          color: '#666666' // 深灰色名称文字
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
          shadowColor: 'rgba(15, 222, 255, 0.3)', // 降低阴影透明度适配浅色
          shadowBlur: 8,
          shadowOffsetY: 4
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(
            0, 0, 0, 1,
            [
              { offset: 0, color: 'rgba(15, 222, 255, 0.2)' }, // 降低透明度
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
          shadowColor: 'rgba(0, 144, 255, 0.3)',
          shadowBlur: 8,
          shadowOffsetY: 4
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(
            0, 0, 0, 1,
            [
              { offset: 0, color: 'rgba(0, 144, 255, 0.2)' },
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
          shadowColor: 'rgba(255, 208, 59, 0.3)',
          shadowBlur: 8,
          shadowOffsetY: 4
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(
            0, 0, 0, 1,
            [
              { offset: 0, color: 'rgba(255, 208, 59, 0.2)' },
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
          shadowColor: 'rgba(130, 61, 255, 0.3)',
          shadowBlur: 8,
          shadowOffsetY: 4
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(
            0, 0, 0, 1,
            [
              { offset: 0, color: 'rgba(130, 61, 255, 0.2)' },
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
  chartInstances.cityComparison.setOption(option)
}

// 3. 热门行业薪资分布图表
const initIndustrySalaryChart = () => {
  if (!isValidDom(industrySalaryChart.value)) return
  
  if (chartInstances.industrySalary) {
    chartInstances.industrySalary.dispose()
  }
  
  chartInstances.industrySalary = echarts.init(industrySalaryChart.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(255, 255, 255, 0.95)', // 浅色背景适配
      borderColor: 'rgba(200, 200, 200, 0.8)',
      borderWidth: 1,
      textStyle: { color: '#4e5969' }
    },
    legend: {
      data: ['平均薪资', '最低薪资', '最高薪资'],
      textStyle: { color: '#4e5969' },
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      // 替换为新的行业数据
      data: ['计算机软件', '通信', '电子技术', '互联网', '计算机服务', '新能源', '金融/投资/证券'],
      axisLine: { lineStyle: { color: '#e8f3ff' } },
      axisLabel: { color: '#4e5969', rotate: 30 }
    },
    yAxis: {
      type: 'value',
      name: '薪资(K/月)',
      axisLine: { lineStyle: { color: '#4e5969' } },
      axisLabel: { color: '#4e5969' },
      splitLine: { lineStyle: { color: '#f0f7ff' } }
    },
    series: [
      { 
        name: '平均薪资', 
        type: 'bar', 
        data: [19.48562, 22.50306, 16.44783, 16.63907, 23.09398, 25.16953, 24.90323], 
        itemStyle: { color: '#409eff' } // 蓝色（保留）
      },
      { 
        name: '最低薪资', 
        type: 'bar', 
        data: [5, 5, 5, 12, 16, 15, 18], 
        itemStyle: { color: '#67C23A' } // 绿色（保留）
      },
      { 
        name: '最高薪资', 
        type: 'bar', 
        data: [60, 6.6667, 50, 25, 30, 50, 35], 
        itemStyle: { color: '#722ED1' } // 紫罗兰色（替换红色，专业且协调）
      }
    ]
  }
  chartInstances.industrySalary.setOption(option)
}

// 4. 复合因素薪资分析图表
const initMultiFactorChart = () => {
  if (!isValidDom(multiFactorChart.value)) return
  
  if (chartInstances.multiFactor) {
    chartInstances.multiFactor.dispose()
  }
  
  chartInstances.multiFactor = echarts.init(multiFactorChart.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      // 包含图片中的全部5个职位
      data: ['AI与大数据分析工程师', '大数据架构师', '大数据解决方案架构师', '资深大数据工程师', '高级大数据工程师'],
      textStyle: { color: '#4e5969' },
      bottom: 0,
      itemWidth: 12,
      itemHeight: 8,
      itemGap: 5
    },
    parallelAxis: [
      { 
        dim: 0, 
        name: '学历要求', 
        type: 'category', 
        data: ['大专', '本科', '硕士'], // 图片中的学历枚举（无博士）
        axisLine: { lineStyle: { color: '#409eff' } } 
      },
      { 
        dim: 1, 
        name: '经验要求', 
        type: 'category', 
        data: ['无需经验', '1年经验', '2年经验', '3-4年经验', '5-7年经验', '8-9年经验', '10年以上经验'],
        axisLine: { lineStyle: { color: '#409eff' } } 
      },
      { 
        dim: 2, 
        name: '企业规模', 
        type: 'category', 
        data: ['50-150人', '150-500人', '1000-5000人', '5000-10000人', '10000人以上'],
        axisLine: { lineStyle: { color: '#409eff' } } 
      },
      { 
        dim: 3, 
        name: '平均薪资(K)', 
        type: 'value', 
        min: 25, // 适配图片中29.5K的最低薪资
        max: 55, // 适配图片中50K的最高薪资
        axisLine: { lineStyle: { color: '#409eff' } } 
      }
    ],
    parallel: {
      // 控制绘图区在容器内的边距：增加 top/bottom 可扩大上下间距，调整 left/right 或 width 可改变轴之间的水平间距
      top: '15%',          // 绘图区距离容器顶部像素（增加以扩大上方空白）
      bottom:'20%',       // 绘图区距离容器底部像素
      left: '6%',       // 左侧边距，减小会增大轴间距，增大会减小轴间距
      right: '6%',      // 右侧边距
      width: '80%',
      parallelAxisDefault: {
        nameLocation: 'end',
        nameGap: 10
      }
    },
    series: [
      // 职位1：AI与大数据分析工程师
      {
        name: 'AI与大数据分析工程师',
        type: 'parallel',
        lineStyle: { width: 4 },
        data: [
          [1, 0, 3, 50],  // 本科、无需经验、5000-10000人、50K
          [1, 4, 3, 50],  // 本科、5-7年经验、5000-10000人、50K
          [2, 3, 3, 50]   // 硕士、3-4年经验、5000-10000人、50K
        ],
        itemStyle: { color: '#F56C6C' }
      },
      // 职位2：大数据架构师
      {
        name: '大数据架构师',
        type: 'parallel',
        lineStyle: { width: 4 },
        data: [
          [0, 0, 1, 50],  // 大专、无需经验、150-500人、50K
          [1, 2, 1, 50],  // 本科、2年经验、150-500人、50K
          [1, 3, 1, 50]   // 本科、3-4年经验、150-500人、50K
        ],
        itemStyle: { color: '#409eff' }
      },
      // 职位3：大数据解决方案架构师
      {
        name: '大数据解决方案架构师',
        type: 'parallel',
        lineStyle: { width: 4 },
        data: [
          [0, 3, 4, 45],  // 大专、3-4年经验、10000人以上、45K
          [1, 3, 4, 45],  // 本科、3-4年经验、10000人以上、45K
          [1, 4, 4, 45]   // 本科、5-7年经验、10000人以上、45K
        ],
        itemStyle: { color: '#67C23A' }
      },
      // 职位4：资深大数据工程师
      {
        name: '资深大数据工程师',
        type: 'parallel',
        lineStyle: { width: 4 },
        data: [
          [0, 2, 2, 37.5], // 大专、2年经验、1000-5000人、37.5K
          [1, 2, 2, 37.5], // 本科、2年经验、1000-5000人、37.5K
          [1, 3, 2, 37.5]  // 本科、3-4年经验、1000-5000人、37.5K
        ],
        itemStyle: { color: '#E6A23C' } // 新增橙色区分
      },
      // 职位5：高级大数据工程师
      {
        name: '高级大数据工程师',
        type: 'parallel',
        lineStyle: { width: 4 },
        data: [
          [0, 1, 0, 29.5], // 大专、1年经验、50-150人、29.5K
          [1, 1, 0, 29.5], // 本科、1年经验、50-150人、29.5K
          [1, 2, 0, 29.5]  // 本科、2年经验、50-150人、29.5K
        ],
        itemStyle: { color: '#909399' } // 新增灰色区分
      }
    ]
  }
  chartInstances.multiFactor.setOption(option)
}

// 销毁所有图表实例
const disposeAllCharts = () => {
  Object.values(chartInstances).forEach(instance => {
    if (instance && typeof instance.dispose === 'function') {
      instance.dispose()
    }
  })
  
  // 重置实例对象
  Object.keys(chartInstances).forEach(key => {
    chartInstances[key] = null  
  })  
}  

// 处理窗口大小变化
const handleResize = () => {
  Object.values(chartInstances).forEach(instance => {
    if (instance && typeof instance.resize === 'function') {
      instance.resize()
    }
  })  
}  

// 页面加载时初始化图表
onMounted(() => {
  // 等待DOM完全渲染后初始化
  nextTick(() => {
    initCharts()  
  })  
  
  // 监听窗口大小变化，重绘图表
  window.addEventListener('resize', handleResize)  
})  

// 组件卸载时清理资源
onUnmounted(() => {
  // 移除事件监听
  window.removeEventListener('resize', handleResize)  
  
  // 销毁所有图表实例
  disposeAllCharts()  
})  
</script>

<style scoped>
.salary-distribution {
  padding: 10px 24px;
  background-color: #f9fbfd;
  min-height: 100vh;
  font-family: 'Inter', 'Helvetica Neue', Arial, sans-serif;
  /* 防止内容溢出 */
  overflow-x: auto;
}

/* 页面头部样式 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 18px;
  background: #f0f7ff;
  border-radius: 12px;
  border: 1px solid #e8f3ff;
  box-shadow: 0 2px 10px rgba(64, 158, 255, 0.06);
  flex-wrap: wrap;
  gap: 16px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.title-section h1 {
  margin: 0;
  color: #1d2129;
  display: flex;
  align-items: center;
  gap: 10px;
}

.subtitle {
  margin: 5px 0 0 0;
  color: #4e5969;
  font-size: 14px;
}

/* 快速概览数据卡片 */
.quick-stats {
  margin-bottom: 10px;
}

.stats-row {
  display: flex;
  gap: 30px;
  flex-wrap: wrap;
  width: 100%;
}

.stat-col {
  flex: 1;
  min-width: 200px;
}

.stat-card {
  border: 1px solid #e8f3ff;
  border-radius: 12px;
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.06);
  transition: all 0.3s ease;
  overflow: hidden;
  height: 100%;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.12);
  border-color: #d1eaff;
}

.stat-content {
  padding: 0px;
}

.stat-label {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 14px;
}

.stat-value {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 700;
  color: #1d2129;
}

.stat-trend {
  margin: 0;
  font-size: 13px;
  display: flex;
  align-items: center;
}

.stat-trend.positive {
  color: #67C23A;
}

.stat-trend.negative {
  color: #F56C6C;
}

/* 分析卡片样式 - 四个卡片强制一行显示 */
.analysis-cards {
  margin-bottom: 10px;
  width: 100%;
}

.analysis-row {
  display: flex;
  gap: 20px;
  /* 强制不换行，保证四个卡片在一行 */
  flex-wrap: nowrap;
  width: 100%;
}

.salary-card {
  background: #ffffff;
  border: 1px solid #e8f3ff;
  padding: 13px 15px 10px 15px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.06);
  transition: all 0.3s ease;
  /* 平均分配宽度 */
  flex: 1;
  min-width: 200px;
}

.salary-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.12);
  border-color: #d1eaff;
}

.salary-card h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #1d2129;
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
  font-weight: 600;
}

.salary-card p {
  font-size: 14px;
  color: #4e5969;
  margin: 10px 0;
  line-height: 1.6;
}

.highlight {
  color: #409eff;
  font-weight: 600;
}

/* 图表容器样式 - 四个图表强制一行显示 */
.all-charts-container {
  display: flex;
  gap: 20px;
  /* 强制不换行，保证四个图表在一行 */
  flex-wrap: wrap;
  margin-bottom: 20px;
  width: 100%;
}

.chart-container {
  background: #ffffff;
  border: 1px solid #e8f3ff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.06);
  padding: 20px  20px 2px 20px;
  transition: all 0.3s ease;
  height: 320px; /* 增加图表高度，提升显示效果 */
  /* 平均分配宽度 */
  flex: 1;
  min-width: 280px;
}

.chart-container:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.12);
  border-color: #d1eaff;
}

.chart-header {
  margin-bottom: 0;
}

.chart-header h3 {
  margin: 0 0 5px 0;
  color: #1d2129;
  font-size: 16px;
  font-weight: 600;
}

.chart-header p {
  margin: 0;
  color: #666;
  font-size: 13px;
}

.chart {
  width: 100% !important;
  height: calc(100% - 40px) !important;
}

/* 响应式设计 - 小屏幕下恢复换行 */
@media (max-width: 1440px) {
  .analysis-row {
    flex-wrap: wrap;
  }
  
  .all-charts-container {
    flex-wrap: wrap;
  }
  
  .chart-container {
    min-width: 300px;
  }
}

@media (max-width: 768px) {
  .chart-container {
    height: 350px;
    width: 100%;
    min-width: unset;
    margin-bottom: 20px;
  }

  .analysis-row {
    flex-direction: column;
  }

  .salary-card {
    min-width: unset;
    width: 100%;
  }

  .stats-row {
    flex-direction: column;
  }

  .stat-col {
    min-width: unset;
    width: 100%;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .action-buttons {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>