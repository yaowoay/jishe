<template>
  <div class="enterprise-dashboard">
    <!-- 顶部统计信息 -->
    <div class="stats-grid">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon" style="background: #ecf5ff;">
            <el-icon color="#409EFF" :size="24"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">1,248</div>
            <div class="stat-label">总候选人</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon" style="background: #f0f9eb;">
            <el-icon color="#67C23A" :size="24"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">356</div>
            <div class="stat-label">进行中面试</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon" style="background: #fdf6ec;">
            <el-icon color="#E6A23C" :size="24"><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">89%</div>
            <div class="stat-label">面试完成率</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon" style="background: #fef0f0;">
            <el-icon color="#F56C6C" :size="24"><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">42</div>
            <div class="stat-label">待处理面试</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-row">
      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>面试趋势分析</span>
            <el-tag type="primary">月统计</el-tag>
          </div>
        </template>
        <div class="chart-container">
          <div ref="trendChart" style="width: 100%; height: 300px;"></div>
        </div>
      </el-card>

      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>岗位分布</span>
            <el-tag type="success">热门岗位</el-tag>
          </div>
        </template>
        <div class="chart-container">
          <div ref="positionChart" style="width: 100%; height: 300px;"></div>
        </div>
      </el-card>
    </div>

    <!-- 底部数据表格 -->
    <div class="data-section">
      <el-card>
        <template #header>
          <div class="table-header">
            <span>最新面试记录</span>
            <el-button type="primary" size="small">查看全部</el-button>
          </div>
        </template>
        <el-table :data="interviewData" style="width: 100%">
          <el-table-column prop="candidate" label="候选人" min-width="120" />
          <el-table-column prop="position" label="岗位" min-width="100" />
          <el-table-column prop="time" label="面试时间" min-width="120" />
          <el-table-column prop="duration" label="时长" width="80" />
          <el-table-column prop="score" label="评分" width="100">
            <template #default="{ row }">
              <el-rate v-model="row.score" disabled show-score />
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="statusType[row.status]">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default>
              <el-button size="small" type="primary" link>查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { User, Document, TrendCharts, Clock } from '@element-plus/icons-vue'

// 模拟数据
const interviewData = ref([
  { candidate: '张三', position: '前端开发', time: '2024-01-15 14:30', duration: '45分钟', score: 4.5, status: '已完成' },
  { candidate: '李四', position: '后端开发', time: '2024-01-15 16:00', duration: '38分钟', score: 4.2, status: '已完成' },
  { candidate: '王五', position: '产品经理', time: '2024-01-16 10:00', duration: '52分钟', score: 4.8, status: '已完成' },
  { candidate: '赵六', position: 'UI设计师', time: '2024-01-16 14:00', duration: '40分钟', score: 3.9, status: '进行中' },
  { candidate: '钱七', position: '测试工程师', time: '2024-01-17 09:30', duration: '35分钟', score: 4.1, status: '待安排' }
])

const statusType = {
  '已完成': 'success',
  '进行中': 'warning',
  '待安排': 'info'
}

const trendChart = ref(null)
const positionChart = ref(null)

onMounted(() => {
  initCharts()
})

function initCharts() {
  // 趋势图
  const trendChartInstance = echarts.init(trendChart.value)
  trendChartInstance.setOption({
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '面试数量',
        type: 'line',
        data: [120, 132, 101, 134, 90, 230, 210, 145, 189, 156, 178, 195],
        smooth: true,
        lineStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [{
              offset: 0, color: 'rgba(64, 158, 255, 0.3)'
            }, {
              offset: 1, color: 'rgba(64, 158, 255, 0.1)'
            }]
          }
        }
      }
    ]
  })

  // 岗位分布图
  const positionChartInstance = echarts.init(positionChart.value)
  positionChartInstance.setOption({
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        name: '岗位分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 12,
            fontWeight: 'bold'
          }
        },
        data: [
          { value: 1048, name: '前端开发' },
          { value: 735, name: '后端开发' },
          { value: 580, name: '产品经理' },
          { value: 484, name: 'UI设计师' },
          { value: 300, name: '测试工程师' }
        ]
      }
    ]
  })

  // 响应式调整
  window.addEventListener('resize', () => {
    trendChartInstance.resize()
    positionChartInstance.resize()
  })
}
</script>

<style scoped>
.enterprise-dashboard {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 8px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  padding: 10px 0;
}

.data-section {
  margin-bottom: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>