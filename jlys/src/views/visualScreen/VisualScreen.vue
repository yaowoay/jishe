<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <div class="header-center-title">
        <div class="platform-logo">
          <i class="el-icon-data-analysis"></i>
        </div>
        <div class="platform-info">
          <h1>高校学生就业能力智配平台·校园就业洞察大屏</h1>
          <p class="platform-desc">大数据洞察与AI多模态面试融合的校园就业智配平台</p>
        </div>
      </div>

      <div class="header-right">
        <div class="dashboard-time">
          <i class="el-icon-time"></i>
          {{ currentTime }}
        </div>
        <div class="header-actions">
          <router-link to="/index" class="visual-link">
            <i class="el-icon-s-home"></i>
            首页
          </router-link>
        </div>
      </div>
    </div>

    <div class="content-wrapper">
      <div class="main-content">
        <div class="left-column">
          <div class="chart-container">
            <ComWelfareWord />
          </div>

          <div class="chart-container">
            <SalaryRangeDist />
          </div>

          <div class="chart-container">
            <AreaChart />
          </div>
        </div>
        <!-- 中间列的图表容器 -->
        <div class="middle-column">
          <div class="chart-container">
            <BasicData />
          </div>
         
          <div class="chart-container">
            <Map />
          </div>
          <div class="chart-container">
            <Map2 />
          </div>

        </div>

        <div class="right-column">
          <!-- 右侧列的图表容器 -->
          <div class="chart-container">
            <MultiRingChart />
          </div>
          <div class="chart-container">
            <SalaryTopIndustryBar />
          </div>
        
          <div class="chart-container">
            <ComScaleTypePie />
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
// 导入所有组件


import Map from '@/NewChart/Map.vue'
import AreaChart from '@/NewChart/AreaChart.vue'
import MultiRingChart from '@/NewChart/MultiRingChart.vue'
import Map2 from '@/NewChart/Map2.vue'
import BasicData from '@/NewChart/BasicData.vue'


import ComScaleTypePie from '@/chartz/ComScaleTypePie.vue'
import ComWelfareWord from '@/chartz/ComWelfareWord.vue'

import SalaryRangeDist from '@/chartz/SalaryRangeDist.vue'
import SalaryTopIndustryBar from '@/chartz/SalaryTopIndustryBar.vue'

// 响应式数据
const currentTime = ref('')

// 更新时间方法
const updateTime = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const weekMap = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const week = weekMap[now.getDay()]
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  currentTime.value = `${year}-${month}-${day} ${week} ${hours}:${minutes}:${seconds}`
}

// 生命周期钩子
onMounted(() => {
  updateTime()
  setInterval(() => {
    updateTime()
  }, 1000)
})
</script>

<style scoped>
/* 基础布局样式 - 深色主题 */
.dashboard-container {
  width: 100%;
  height: 100vh;
  max-height: 100vh;
  padding: 10px;
  box-sizing: border-box;
  background: url('@/assets/back1.png') no-repeat center center;
  background-size: cover;
  background-position: center 20%;
  font-family: 'Microsoft YaHei', Arial, sans-serif;
  display: flex;
  flex-direction: column;
  color: #e2e8f0;
  overflow: hidden;
}

/* 头部样式 - 科技蓝渐变 */
.dashboard-header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 80px;
  flex-shrink: 0;
  box-sizing: border-box;
  background-image: url("@/assets/head1.png");
  background-repeat: repeat-x;
  background-position: center center;
  background-size: cover;
  padding: 10px 0;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 居中标题样式 */
.header-center-title {
  display: flex;
  align-items: center;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.platform-logo {
  font-size: 36px;
  margin-right: 15px;
  color: #60a5fa;
}

.platform-info h1 {
  color: white;
  font-size: 24px;
  margin: 0 0 5px 0;
  font-weight: 600;
}

.platform-desc {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  margin: 0;
}

/* 时间和链接样式 */
.dashboard-time, .visual-link {
  display: flex;
  align-items: center;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.05);
  padding: 8px 15px;
  border-radius: 20px;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  margin-right: 10px;
}
.visual-link {
  color: white;
  text-decoration: none;
  transition: all 0.3s;
}
.visual-link:hover {
  background: rgba(255, 255, 255, 0.1);
}
.dashboard-time {
  margin-right: 20px;
}


/* ==================== 布局核心修改区域 START ==================== */

/* 内容区域包装器 */
.content-wrapper {
  flex: 1; 
  display: flex;
  margin-top: 5px;
  min-height: 0;
}

/* 主内容区，三列的父容器 */
.main-content {
  display: flex;
  /* gap: 15px; */
  width: 100%;
  /* 改动2: 精确计算主内容区的高度 */
  /* 计算公式: 100vh - 容器上下padding(20px) - 头部高度(80px) - wrapper上边距(15px) = 100vh - 115px */
  height: calc(100vh - 105px);
}

/* 列布局 (左、中、右) */
.left-column {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
/* 列布局 (左、中、右) */
 .middle-column {
  display: flex;
  flex-direction: column;
  gap: 0px;
}
/* .middle-column {
  flex: 1.5;

  background-image: url('../assets/5.png');
  background-size: cover; 
  background-repeat: no-repeat;
  background-position: center;
  border-radius: 12px;
}  */

/* 列布局 (左、中、右) */
.right-column {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* 要调节中间组件（middle-column）的边框显示与除去，
请修改 .middle-column .chart-container 的 border 属性。
例如： */

/* 显示边框 */
/* .middle-column .chart-container {
  border: 1px solid rgba(100, 200, 255, 0.2);
} */

/* 去除边框 */
.middle-column .chart-container {
  border: none;
  box-shadow: none
}
/* 列宽比例分配 */
.left-column {
  flex: 1; /* 左列保持 1 份不变 */
}

.right-column {
  flex: 1; /* 右列缩小为 0.5 份 */
}

.middle-column {
  flex: 1.5; /* 中间列扩大为 1.8 份，以占据右列缩小的空间 */
}

/* 图表容器统一样式 */
.chart-container {
  display: flex;
  /* background: rgba(15, 25, 60, 0.03); */
  border-radius: 8px;
  padding: 12px 12px 12px 12px;
  border: 1px solid rgba(100, 200, 255, 0.2);
  box-shadow: 
    0 0 10px rgba(0, 150, 255, 0.2),
    inset 0 0 5px rgba(100, 200, 255, 0.1);
}
.chart-container {
  border: none;
  box-shadow: 
   none;
   /* 去除阴影 */
}

/* 左列和右列的图表容器统一背景图 */
.left-column .chart-container,
.right-column .chart-container {
  background-image: url('@/assets/part5.png');
  background-size: 100% 100%;
  background-repeat: no-repeat;
  background-position: center;
}

/* ✨✨✨ 【关键修改】为每一列的图表分配计算高度 ✨✨✨ */

/* 左列: 重新分配高度，实现上下小，中间大 (例如 28.5%, 43%, 28.5%)
  这样上下两个组件的高度就被“缩小”了
*/
.left-column .chart-container:nth-child(1) {
  height: calc((100% - 30px) * 0.25);
}
.left-column .chart-container:nth-child(2) {
  height: calc((100% - 30px) * 0.33);
}
.left-column .chart-container:nth-child(3) {
  height: calc((100% - 30px) * 0.38);
}


/* 中间列: 重新分配高度，实现上大下小 (65%, 35%)
*/
.middle-column .chart-container:nth-child(1) {
  height: calc((100% - 15px) * 0.1); /* 调大 */
}
.middle-column .chart-container:nth-child(2) {
  height: calc((100% - 15px) * 0.4); /* 调小 */
}

.middle-column .chart-container:nth-child(3) {
  height: calc((100% - 15px) * 0.5); /* 调小 */
}

/* 右列: 保持不变，平均分配 */
.right-column .chart-container {
  height: calc((100% - 30px) / 3);
}


/* 让图表组件本身(如 <RadioComp/>) 自动充满其父容器 */
.chart-container > * {
  width: 100%;
  height: 100%;
}

/* ==================== 布局核心修改区域 END ==================== */

/* 响应式设计调整 (保持不变，这种固定高度布局在小屏幕上体验不佳，但按要求保留) */
@media (max-width: 1400px) {
  .dashboard-container {
    height: auto; /* 在移动端恢复自动高度 */
    min-height: 100vh;
  }
  .main-content {
    flex-direction: column;
    height: auto;
  }
  .left-column, .middle-column, .right-column {
    width: 100%;
    height: auto;
  }
  .left-column, .right-column {
    flex-direction: row;
    flex-wrap: wrap;
  }
  .left-column > .chart-container, .right-column > .chart-container, .middle-column > .chart-container {
    flex: 1 1 45%;
    min-width: 300px;
    height: 300px;
  }
}

@media (max-width: 768px) {
  .left-column, .right-column {
    flex-direction: column;
  }
  .left-column > .chart-container, .right-column > .chart-container, .middle-column > .chart-container {
    flex: 1;
    height: 250px;
    min-width: auto;
  }
}
</style>