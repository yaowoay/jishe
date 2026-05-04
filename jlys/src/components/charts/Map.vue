<template>
  <div id="mapDom" style="width: 100%; height: 100%;"></div>
</template>

<script setup>
import { nextTick, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import china from '../assets/china.json'

// 更新后的城市数据（包含经纬度坐标）
const cityData = [
  { city: '广州', value: 342, typeCount: 30, avgSalary: 19469.40, coords: [113.264999, 23.129101] },
  { city: '北京', value: 201, typeCount: 28, avgSalary: 21854.46, coords: [116.407001, 39.9046] },
  { city: '武汉', value: 183, typeCount: 28, avgSalary: 18709.46, coords: [114.31, 30.52] },
  { city: '南京', value: 182, typeCount: 25, avgSalary: 18879.33, coords: [118.796, 32.0583] },
  { city: '成都', value: 181, typeCount: 27, avgSalary: 20583.53, coords: [104.06, 30.67] },
  { city: '杭州', value: 146, typeCount: 27, avgSalary: 19571.32, coords: [120.16, 30.28] },
  { city: '重庆', value: 74, typeCount: 19, avgSalary: 20686.35, coords: [106.552003, 29.562696] },
  { city: '西安', value: 73, typeCount: 22, avgSalary: 19132.97, coords: [108.94, 34.341101] },
  { city: '长沙', value: 68, typeCount: 21, avgSalary: 19923.40, coords: [112.98, 28.11] }, // 补充长沙坐标
  { city: '合肥', value: 59, typeCount: 17, avgSalary: 17329.75, coords: [117.28, 31.86] }, // 补充合肥坐标
  { city: '昆明', value: 37, typeCount: 17, avgSalary: 17233.11, coords: [102.71, 25.04] }, // 补充昆明坐标
  { city: '福州', value: 34, typeCount: 16, avgSalary: 19378.65, coords: [119.30, 26.08] }, // 补充福州坐标
  { city: '南昌', value: 28, typeCount: 14, avgSalary: 20108.61, coords: [115.89, 28.68] }, // 补充南昌坐标
  { city: '沈阳', value: 23, typeCount: 7, avgSalary: 19206.52, coords: [123.43, 41.80] }, // 补充沈阳坐标
  { city: '南宁', value: 17, typeCount: 9, avgSalary: 15897.00, coords: [108.33, 22.82] }, // 补充南宁坐标
  { city: '郑州', value: 17, typeCount: 14, avgSalary: 16426.47, coords: [113.65, 34.76] }, // 补充郑州坐标
  { city: '哈尔滨', value: 15, typeCount: 9, avgSalary: 16572.20, coords: [126.63, 45.80] }, // 补充哈尔滨坐标
  { city: '长春', value: 14, typeCount: 10, avgSalary: 19702.36, coords: [125.35, 43.88] }, // 补充长春坐标
  { city: '天津', value: 11, typeCount: 8, avgSalary: 17977.27, coords: [117.20, 39.13] },
  { city: '济南', value: 11, typeCount: 5, avgSalary: 17242.36, coords: [117.00, 36.65] }, // 补充济南坐标
  { city: '贵阳', value: 11, typeCount: 10, avgSalary: 19916.64, coords: [106.71, 26.57] }, // 补充贵阳坐标
  { city: '石家庄', value: 10, typeCount: 6, avgSalary: 25200.00, coords: [114.51, 38.03] }, // 补充石家庄坐标
  { city: '海口', value: 5, typeCount: 2, avgSalary: 16100.00, coords: [110.35, 20.02] }, // 补充海口坐标
  { city: '呼和浩特', value: 4, typeCount: 3, avgSalary: 23666.50, coords: [111.65, 40.82] }, // 补充呼和浩特坐标
  { city: '兰州', value: 3, typeCount: 2, avgSalary: 14000.00, coords: [103.82, 36.06] }, // 补充兰州坐标
  { city: '太原', value: 2, typeCount: 1, avgSalary: 13000.00, coords: [112.53, 37.87] }, // 补充太原坐标
  { city: '西宁', value: 1, typeCount: 1, avgSalary: 7500.00, coords: [101.77, 36.62] } // 补充西宁坐标
]

let myChart = null
let timer = null

const mapEcharts = (geoJSON) => {
  myChart = echarts.init(document.querySelector('#mapDom'))
  echarts.registerMap('china', geoJSON)

  // 筛选有数据的城市
  const citiesWithData = cityData.filter(item => item.value > 0)
  const beijingPoint = citiesWithData.find(item => item.city === '北京')

  const pointOption = []
  const lineOption = []
  const colorList = ['#D88BFF', '#5abead', '#66FFFF', '#48D5FF', '#FFE73D', '#FF6C66']

  citiesWithData.forEach((item, index) => {
    const point = {
      name: item.city,
      value: item.coords,
      jobCount: item.value,
      typeCount: item.typeCount, // 新增种类数据
      avgSalary: item.avgSalary,
      itemStyle: { color: colorList[index % colorList.length] }
    }
    pointOption.push(point)

    // 保留与北京的连线
    if (item.city !== '北京' && beijingPoint) {
      lineOption.push({
        coords: [item.coords, beijingPoint.coords],
        lineStyle: { color: colorList[index % colorList.length] }
      })
    }
  })


  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'transparent',
      borderWidth: 0,
      formatter: params => {
        if (params.componentType === 'series' && params.data) {
          const data = params.data
          const name = data.name || '未知城市'
          const jobCount = data.jobCount ? `${data.jobCount} 个` : '无数据'
          const typeCount = data.typeCount ? `${data.typeCount} 种` : '无数据'
          // 格式化薪资为货币格式
          const salary = data.avgSalary ? `${Math.round(data.avgSalary).toLocaleString('zh-CN')} 元` : '无数据'

          return `
                        <div style="
                            background: rgba(10, 25, 50, 0.85);
                            border: 1px solid #48D5FF;
                            border-radius: 8px;
                            padding: 10px 15px;
                            color: #fff;
                            font-family: 'Microsoft YaHei', sans-serif;
                            box-shadow: 0 0 15px rgba(72, 213, 255, 0.5);
                        ">
                            <h3 style="
                                font-size: 18px;
                                color: #66FFFF;
                                margin: 0 0 8px 0;
                                padding-bottom: 5px;
                                border-bottom: 1px solid rgba(72, 213, 255, 0.3);
                            ">${name}</h3>
                            <div style="font-size: 14px; line-height: 1.6;">
                                <div>
                                    <span style="color: #B0C4DE; margin-right: 8px;">岗位数量:</span>
                                    <span style="color: #FFE73D; font-weight: bold;">${jobCount}</span>
                                </div>
                                <div>
                                    <span style="color: #B0C4DE; margin-right: 8px;">岗位种类:</span>
                                    <span style="color: #5abead; font-weight: bold;">${typeCount}</span>
                                </div>
                                <div>
                                    <span style="color: #B0C4DE; margin-right: 8px;">平均薪资:</span>
                                    <span style="color: #66FFFF; font-weight: bold;">${salary}</span>
                                </div>
                            </div>
                        </div>
                    `
        }
        return ''
      }
    },
    geo: [
      {
        map: 'china',
        silent: true,
        zoom: 1.5,
        roam: true,
        z: 2,
        top: '120px',
        label: { show: false },
        itemStyle: {
          areaColor: 'rgba(18, 38, 78, 0.6)',
          borderColor: 'rgba(76, 161, 255, 0.7)',
          borderWidth: 1.2,
          borderType: 'solid'
        },
        emphasis: {
          disabled: false,
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
        rippleEffect: {
          period: 12,
          scale: 4,
          brushType: 'fill'
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{b}',
          color: '#fff',
          fontSize: 14
        },
        itemStyle: {
          color: value => value.data.itemStyle.color
        },
        symbolSize: 12,
        data: pointOption
      }
      // 如需显示连线可取消注释
      // ,
      // {
      //     type: 'lines',
      //     geoIndex: 0,
      //     z: 4,
      //     effect: {
      //         show: true,
      //         period: 6,
      //         trailLength: 0.4,
      //         symbol: 'arrow',
      //         symbolSize: 8
      //     },
      //     lineStyle: {
      //         width: 1.5,
      //         opacity: 0.6,
      //         curveness: 0.2
      //     },
      //     data: lineOption
      // }
    ]
  }

  myChart.setOption(option)

  // 自动高亮提示功能
  let index = 0
  const onHighlight = () => {
    return setInterval(() => {
      myChart.dispatchAction({ type: 'downplay' })
      myChart.dispatchAction({
        type: 'highlight',
        seriesIndex: 0,
        dataIndex: index
      })
      myChart.dispatchAction({
        type: 'showTip',
        seriesIndex: 0,
        dataIndex: index
      })
      index++
      if (index >= pointOption.length) {
        index = 0
      }
    }, 3000)
  }

  timer = onHighlight()

  // 鼠标交互
  myChart.on('mouseover', () => {
    timer && clearInterval(timer)
  })

  myChart.on('mouseout', () => {
    timer = onHighlight()
  })

  // 响应窗口大小变化
  window.addEventListener('resize', () => {
    myChart.resize()
  })
}


onMounted(() => {
  nextTick(async () => {
    try {
      const response = await fetch('https://img.isqqw.com/profile/upload/2025/03/11/47504bbe-c003-4282-99d3-7f5957914e02.json')
      if (!response.ok) {
        throw new Error('Network response was not ok')
      }
      const geoJSON = await response.json()
      mapEcharts(geoJSON)
    } catch (error) {
      console.error('Failed to fetch GeoJSON:', error)
      alert('无法加载地图数据，请检查网络连接或联系管理员。')
    }
  })
})

onBeforeUnmount(() => {
  if (timer) {
    clearInterval(timer)
  }
  if (myChart) {
    myChart.dispose()
  }
})
</script>

<style scoped>
#mapDom {
  width: 100%;
  height: 100%;
  min-height: 500px;
  border-radius: 10px;
}
</style>