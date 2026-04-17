<template>
  <div class="category-ratio-container">
    <!-- 学历分布图表（替换原销售占比） -->
    <div class="chart-section">
      <div class="chart-wrapper" ref="salesChartRef"></div>
    </div>
    
    <!-- 工作经验分布图表（替换原库存占比） -->
    <div class="chart-section">
      <div class="chart-wrapper" ref="inventoryChartRef"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const salesChartRef = ref(null)
const inventoryChartRef = ref(null)
let salesChartInstance = null
let inventoryChartInstance = null
// 锁定索引（用于点击后固定高亮）
let salesLockedIndex = null
let inventoryLockedIndex = null

// 1. 替换为学历数据
const generateSalesData = () => {
  const educationDataRaw = [
    { name: '本科', value: 2309 },
    { name: '大专', value: 824 },
    { name: '硕士', value: 213 },
    { name: '博士', value: 33 }
  ]
  return educationDataRaw
}

// 2. 替换为工作经验数据（合并1年/2年为1-2年）
const generateInventoryData = () => {
  const experienceDataRaw = [
    { name: '3-4年', value: 1195 },
    { name: '5-7年', value: 753 },
    { name: '1-2年', value: 539 + 451 }, // 合并2年+1年
    { name: '无需经验', value: 243 },
    { name: '8-9年', value: 117 },
    { name: '10年以上', value: 78 },
    { name: '在校生/应届生', value: 3 }
  ]
  return experienceDataRaw
}

// 蓝色系配色方案（扩展适配7个经验维度）
const blueColorPalette = [
  '#0D47A1', // 深蓝 - 最高对比度
  '#1565C0', // 中等深蓝
  '#1976D2', // 蓝色
  '#1E88E5', // 亮蓝色
  '#2196F3', // 标准蓝色
  '#42A5F5', // 浅蓝
  '#64B5F6'  // 更浅蓝
]

// 初始化学历分布图表（替换原销售占比）
const initSalesChart = () => {
  if (salesChartInstance) {
    salesChartInstance.dispose()
  }
  
  salesChartInstance = echarts.init(salesChartRef.value)
  const educationData = generateSalesData()
  // 计算学历占比（转为百分比）
  const eduTotal = educationData.reduce((sum, item) => sum + item.value, 0)
  const eduDataWithPercent = educationData.map(item => ({
    name: item.name,
    value: ((item.value / eduTotal) * 100).toFixed(1) // 保留1位小数
  }))
  
  const option = {
    backgroundColor: 'transparent',
    title: {
      text: '学历分布', // 替换标题
      left: '40%',
      top: '0%',
      textStyle: {
        fontSize: 18,
        fontWeight: 'bold',
        color: '#fff'
      }
    },
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(30, 30, 30, 0.9)',
      textStyle: {
        color: '#90CAF9'
      },
      // 自定义tooltip显示原始数值
      formatter: (params) => {
        const rawItem = educationData.find(item => item.name === params.name)
        return `学历：${params.name}<br/>数量：${rawItem.value}<br/>占比：${params.value}%`
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center',
      textStyle: {
        color: '#fff',
        fontSize: 10
      },
      //   formatter: (name) => {
      //     const item = educationData.find(d => d.name === name)
      //     const percent = ((item.value / eduTotal) * 100).toFixed(1)
      //     return item ? `${name}\n${item.value}（${percent}%）` : name
      //   }
      formatter:{
        value: '{value} '
      }
    },
    series: [
      {
        name: '学历分布', // 替换系列名称
        type: 'pie',
        radius: ['40%', '60%'],
        center: ['63%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        // emphasis: {
        //   label: {
        //     show: true,
        //     position: 'center',
        //     fontSize: 18,
        //     fontWeight: 'bold',
        //     color: '#1E88E5'
        //   },
        //   itemStyle: {
        //     shadowBlur: 10,
        //     shadowOffsetX: 0,
        //     shadowColor: 'rgba(30, 136, 229, 0.5)'
        //   }
        // },
        labelLine: {
          show: false
        },
        data: eduDataWithPercent.map((item, index) => ({
          ...item,
          itemStyle: {
            color: blueColorPalette[index % blueColorPalette.length]
          }
        }))
      }
    ]
  }
  
  salesChartInstance.setOption(option)

  // 在环形图中心添加一个 text graphic，用于在鼠标悬停/点击时显示名称并支持锁定
  salesChartInstance.setOption({
    graphic: [{
      id: 'eduCenterText',
      type: 'text',
      left: '55%',
      top: '45%',
      style: {
        text: '',
        textAlign: 'center',
        textVerticalAlign: 'middle',
        fill: '#1E88E5',
        fontWeight: 'bold',
        fontSize: 18
      }
    }]
  })

  // helper: 根据 index 设置中心文本并高亮该扇区
  const setSalesCenterByIndex = (index) => {
    if (typeof index !== 'number') return
    const raw = educationData[index]
    const percent = eduDataWithPercent[index]?.value
    salesChartInstance.dispatchAction({ type: 'downplay', seriesIndex: 0 })
    salesChartInstance.dispatchAction({ type: 'highlight', seriesIndex: 0, dataIndex: index })
    salesChartInstance.setOption({
      graphic: [{ id: 'eduCenterText', style: { text: `${raw.name}` } }]
    })
  }

  // hover 时显示临时中心提示（未锁定时）
  salesChartInstance.on('mouseover', (params) => {
    if (params.componentType === 'series' && params.seriesType === 'pie') {
      const rawItem = educationData.find(item => item.name === params.name)
      if (salesLockedIndex === null) {
        salesChartInstance.dispatchAction({ type: 'downplay', seriesIndex: 0 })
        salesChartInstance.dispatchAction({ type: 'highlight', seriesIndex: 0, dataIndex: params.dataIndex })
        salesChartInstance.setOption({ graphic: [{ id: 'eduCenterText', style: { text: `${params.name}` } }] })
      }
    }
  })

  // 点击扇区锁定高亮；点击图表空白处取消锁定
  salesChartInstance.on('click', (params) => {
    if (params.componentType === 'series' && params.seriesType === 'pie') {
      salesLockedIndex = params.dataIndex
      setSalesCenterByIndex(salesLockedIndex)
    } else {
      if (salesLockedIndex !== null) {
        salesLockedIndex = null
        salesChartInstance.dispatchAction({ type: 'downplay', seriesIndex: 0 })
        salesChartInstance.setOption({ graphic: [{ id: 'eduCenterText', style: { text: '' } }] })
      }
    }
  })

  // 鼠标移出仅在未锁定时清空
  salesChartInstance.on('mouseout', (params) => {
    if (params.componentType === 'series' && params.seriesType === 'pie') {
      if (salesLockedIndex === null) {
        salesChartInstance.dispatchAction({ type: 'downplay', seriesIndex: 0 })
        salesChartInstance.setOption({ graphic: [{ id: 'eduCenterText', style: { text: '' } }] })
      }
    }
  })
  salesChartInstance.on('globalout', () => {
    if (salesLockedIndex === null) {
      salesChartInstance.dispatchAction({ type: 'downplay', seriesIndex: 0 })
      salesChartInstance.setOption({ graphic: [{ id: 'eduCenterText', style: { text: '' } }] })
    }
  })

  // 如需页面初始化时固定某项（示例：固定索引 1 -> 大专），取消下面注释
  // setSalesCenterByIndex(1)
}


// 初始化工作经验分布图表（替换原库存占比）
const initInventoryChart = () => {
  if (inventoryChartInstance) {
    inventoryChartInstance.dispose()
  }
  
  inventoryChartInstance = echarts.init(inventoryChartRef.value)
  const experienceData = generateInventoryData()
  // 计算经验占比（转为百分比）
  const expTotal = experienceData.reduce((sum, item) => sum + item.value, 0)
  const expDataWithPercent = experienceData.map(item => ({
    name: item.name,
    value: ((item.value / expTotal) * 100).toFixed(1) // 保留1位小数
  }))
  
  const option = {
    backgroundColor: 'transparent',
    title: {
      text: '工作经验分布', // 替换标题
      left: '40%',
      top: '0%',
      textStyle: {
        fontSize: 18,
        fontWeight: 'bold',
        color: '#fff'
      }
    },
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(30, 30, 30, 0.9)',
      textStyle: {
        color: '#90CAF9'
      },
      // 自定义tooltip显示原始数值
      formatter: (params) => {
        const rawItem = experienceData.find(item => item.name === params.name)
        return `经验：${params.name}<br/>数量：${rawItem.value}<br/>占比：${params.value}%`
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center',
      textStyle: {
        color: '#fff',
        fontSize: 10
      },
      //   formatter: (name) => {
      //     const item = experienceData.find(d => d.name === name)
      //     const percent = ((item.value / expTotal) * 100).toFixed(1)
      //     return item ? `${name}\n${item.value}（${percent}%）` : name
      //   }
      formatter:{
        value: '{value} '
      }
    },
    series: [
      {
        name: '工作经验分布', // 替换系列名称
        type: 'pie',
        radius: ['40%', '60%'],
        center: ['68%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        // emphasis: {
        //   label: {
        //     show: true,
        //     position: 'center',
        //     fontSize: 18,
        //     fontWeight: 'bold',
        //     color: '#1E88E5'
        //   },
        //   itemStyle: {
        //     shadowBlur: 10,
        //     shadowOffsetX: 0,
        //     shadowColor: 'rgba(30, 136, 229, 0.5)'
        //   }
        // },
        labelLine: {
          show: false
        },
        data: expDataWithPercent.map((item, index) => ({
          ...item,
          itemStyle: {
            color: blueColorPalette[index % blueColorPalette.length]
          }
        }))
      }
    ]
  }
  
  inventoryChartInstance.setOption(option)

  // 在环形图中心添加一个 text graphic，用于在鼠标悬停/点击时显示名称并支持锁定
  inventoryChartInstance.setOption({
    graphic: [{
      id: 'expCenterText',
      type: 'text',
      left: '58%',
      top: '45%',
      style: {
        text: '',
        textAlign: 'center',
        textVerticalAlign: 'middle',
        fill: '#1E88E5',
        fontWeight: 'bold',
        fontSize: 18
      }
    }]
  })

  // helper: 根据 index 设置中心文本并高亮该扇区
  const setInventoryCenterByIndex = (index) => {
    if (typeof index !== 'number') return
    const raw = experienceData[index]
    const percent = expDataWithPercent[index]?.value
    inventoryChartInstance.dispatchAction({ type: 'downplay', seriesIndex: 0 })
    inventoryChartInstance.dispatchAction({ type: 'highlight', seriesIndex: 0, dataIndex: index })
    inventoryChartInstance.setOption({
      graphic: [{ id: 'expCenterText', style: { text: `${raw.name}` } }]
    })
  }

  // hover 时显示临时中心提示（未锁定时）
  inventoryChartInstance.on('mouseover', (params) => {
    if (params.componentType === 'series' && params.seriesType === 'pie') {
      const rawItem = experienceData.find(item => item.name === params.name)
      if (inventoryLockedIndex === null) {
        inventoryChartInstance.dispatchAction({ type: 'downplay', seriesIndex: 0 })
        inventoryChartInstance.dispatchAction({ type: 'highlight', seriesIndex: 0, dataIndex: params.dataIndex })
        inventoryChartInstance.setOption({ graphic: [{ id: 'expCenterText', style: { text: `${params.name}` } }] })
      }
    }
  })

  // 点击扇区锁定高亮；点击图表空白处取消锁定
  inventoryChartInstance.on('click', (params) => {
    if (params.componentType === 'series' && params.seriesType === 'pie') {
      inventoryLockedIndex = params.dataIndex
      setInventoryCenterByIndex(inventoryLockedIndex)
    } else {
      if (inventoryLockedIndex !== null) {
        inventoryLockedIndex = null
        inventoryChartInstance.dispatchAction({ type: 'downplay', seriesIndex: 0 })
        inventoryChartInstance.setOption({ graphic: [{ id: 'expCenterText', style: { text: '' } }] })
      }
    }
  })

  // 鼠标移出仅在未锁定时清空
  inventoryChartInstance.on('mouseout', (params) => {
    if (params.componentType === 'series' && params.seriesType === 'pie') {
      if (inventoryLockedIndex === null) {
        inventoryChartInstance.dispatchAction({ type: 'downplay', seriesIndex: 0 })
        inventoryChartInstance.setOption({ graphic: [{ id: 'expCenterText', style: { text: '' } }] })
      }
    }
  })
  inventoryChartInstance.on('globalout', () => {
    if (inventoryLockedIndex === null) {
      inventoryChartInstance.dispatchAction({ type: 'downplay', seriesIndex: 0 })
      inventoryChartInstance.setOption({ graphic: [{ id: 'expCenterText', style: { text: '' } }] })
    }
  })

  // 如需页面初始化时固定某项（示例：固定索引 2 -> 1-2年），取消下面注释
  // setInventoryCenterByIndex(2)
}


// 初始化所有图表
const initCharts = () => {
  initSalesChart()
  initInventoryChart()
}

onMounted(() => {
  initCharts()
  window.addEventListener('resize', () => {
    salesChartInstance?.resize()
    inventoryChartInstance?.resize()
  })
})

onUnmounted(() => {
  salesChartInstance?.dispose()
  inventoryChartInstance?.dispose()
})
</script>

<style scoped>
.category-ratio-container {
  width: 100%;
  height: 100%;
  display: flex;
  gap: 5px;
  padding: 0px;
  box-sizing: border-box;
  background-color: transparent;
}

.chart-section {
  flex: 1;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.chart-wrapper {
  flex: 1;
  width: 100%;
 height: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .category-ratio-container {
    flex-direction: column;
  }
  
}
</style>