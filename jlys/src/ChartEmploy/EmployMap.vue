<template>
  <div ref="chartRef" style="width: 100%; height: 800px;"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const chartRef = ref(null)
let myChart = null
let timer = null

onMounted(() => {
  initChart()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
  if (myChart) myChart.dispose()
})

const initChart = () => {
  myChart = echarts.init(chartRef.value)

  // 加载中国地图 JSON
  let geoJSON
  const xhr = new XMLHttpRequest()
  xhr.open('GET', 'https://img.isqqw.com/profile/upload/2025/03/11/47504bbe-c003-4282-99d3-7f5957914e02.json', false)
  xhr.onload = function () {
    if (xhr.status >= 200 && xhr.status < 300) {
      geoJSON = JSON.parse(xhr.responseText)
    } else {
      console.error('地图数据加载失败:', xhr.statusText)
    }
  }
  xhr.send()
  echarts.registerMap('china', geoJSON)

  // 目的地（全国主要城市）
  const pointOption = [
    { name: '北京', value: [116.407, 39.904] },
    { name: '上海', value: [121.474, 31.230] },
    { name: '广州', value: [113.265, 23.129] },
    { name: '深圳', value: [114.058, 22.543] },
    { name: '杭州', value: [120.154, 30.287] },
    { name: '成都', value: [104.066, 30.572] },
    { name: '重庆', value: [106.552, 29.563] },
    { name: '武汉', value: [114.305, 30.593] },
    { name: '西安', value: [108.940, 34.341] },
    { name: '南京', value: [118.796, 32.058] }
  ]

  // 起点：郑州（固定）
  const startPoint = {
    name: '郑州',
    value: [113.625, 34.747],
    lineStyle: { color: '#FF6C66' }
  }

  // 总毕业生人数
  const totalGraduate = 186

  const lineOption = []
  const colorList = ['#D88BFF', '#5abead', '#66FFFF', '#48D5FF', '#FFE73D', '#FF6C66']

  // 生成流向线：郑州 → 各城市
  pointOption.map((item, index) => {
    item.lineStyle = { color: colorList[index % colorList.length] }
    item.coords = [startPoint.value, item.value]
    lineOption.push(item)
  })
  pointOption.unshift(startPoint)

  // ====================== 已替换为你提供的地图样式 ======================
  const option = {
    title: {
      text: '毕业生就业流向',
      left: 'center',
      top: '5%',
      textStyle: {
        color: '#66ccff',
        fontSize: 16
      }
    },
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'transparent',
      borderWidth: 0,
      formatter: params => {
        const { componentType, data, dataIndex } = params
        if (componentType === 'series' && data) {
          const showNumber = Math.ceil(totalGraduate * (0.5 + dataIndex))
          return `
            <div style="background: rgba(10, 25, 50, 0.85);border: 1px solid #48D5FF;border-radius: 8px;padding: 10px 15px;color: #fff;box-shadow: 0 0 15px rgba(72, 213, 255, 0.5);">
                <h3 style="font-size: 18px;color: #66FFFF;margin: 0 0 8px 0;padding-bottom: 5px;border-bottom: 1px solid rgba(72, 213, 255, 0.3);">${data.name}</h3>
                <div style="font-size: 16px; line-height: 1.6;">
                    <div><span style="color: #B0C4DE; margin-right: 8px;">就业流向人数:</span><span style="color: #FFE73D; font-weight: bold;">${showNumber} 人</span></div>
                </div>
            </div>`
        }
        return ''
      }
    },
    geo: [
      {
        map: 'china',
        silent: true,
        zoom: 1.2,
        roam: true,
        z: 2,
        top: '120px',
        label: { show: false },
        // 你提供的地图配色
        itemStyle: {
          areaColor: 'rgba(18, 38, 78, 0.6)',
          borderColor: 'rgba(76, 161, 255, 0.7)',
          borderWidth: 1.2,
          borderType: 'solid'
        },
        emphasis: {
          itemStyle: {
            areaColor: 'rgba(28, 58, 108, 0.8)',
            borderColor: 'rgba(116, 191, 255, 1)',
            borderWidth: 1.5
          }
        },
        regions: [{
          name: '南海诸岛',
          itemStyle: { borderWidth: 0, areaColor: 'transparent' }
        }]
      }
    ],
    series: [
      {
        type: 'effectScatter',
        coordinateSystem: 'geo',
        geoIndex: 0,
        showEffectOn: 'render',
        z: 3,
        rippleEffect: { period: 12, scale: 4, brushType: 'fill' },
        label: {
          show: true,
          position: 'top',
          formatter: '{b}',
          color: '#fff',
          fontSize: 14
        },
        itemStyle: {
          color: (val) => val.data.lineStyle?.color || '#5abead',
          shadowBlur: 10
        },
        symbolSize: 12,
        data: pointOption
      },
      {
        type: 'lines',
        z: 4,
        geoIndex: 0,
        effect: {
          show: true,
          period: 6,
          trailLength: 0.4,
          symbol: 'arrow',
          symbolSize: 8
        },
        lineStyle: {
          width: 1.5,
          opacity: 0.6,
          curveness: 0.2
        },
        data: lineOption
      }
    ]
  }

  myChart.setOption(option)

  // 自动轮播提示
  let index = 0
  const onHighlight = () => {
    return setInterval(() => {
      myChart.dispatchAction({ type: 'downplay' })
      index = (index + 1) % pointOption.length
      myChart.dispatchAction({
        type: 'showTip',
        seriesIndex: 0,
        dataIndex: index
      })
    }, 3600)
  }
  timer = onHighlight()

  // 鼠标悬浮暂停
  myChart.on('mouseover', () => { if (timer) clearInterval(timer) })
  myChart.on('mouseout', () => {
    if (timer) clearInterval(timer)
    timer = onHighlight()
  })

  window.addEventListener('resize', () => myChart.resize())
}
</script>