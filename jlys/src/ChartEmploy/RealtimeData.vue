<template>
  <div class="job-stats-container">
    <!-- 最高薪资卡片 -->
    <div class="stat-card">
      <div class="stat-icon">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 1L3 5V11C3 16.55 6.84 21.74 12 23C17.16 21.74 21 16.55 21 11V5L12 1ZM12 11.99H19C18.47 16.11 15.72 19.78 12 20.93V12H5V6.3L12 3.19V11.99Z" fill="currentColor"/>
        </svg>
      </div>
      <div class="stat-content">
        <div class="stat-label">毕业生总人数</div>
        <div class="stat-value">{{ graduateCountText }}</div>
<!--        <div class="stat-desc">AI与大数据分析工程师</div>-->
      </div>
    </div>

    <!-- 热门岗位卡片 -->
    <!-- <div class="stat-card">
      <div class="stat-icon">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M20 6H16V4C16 2.89 15.11 2 14 2H10C8.89 2 8 2.89 8 4V6H4C2.89 6 2.01 6.89 2.01 8L2 19C2 20.11 2.89 21 4 21H20C21.11 21 22 20.11 22 19V8C22 6.89 21.11 6 20 6ZM14 6H10V4H14V6Z" fill="currentColor"/>
        </svg>
      </div>
      <div class="stat-content">
        <div class="stat-label">热门岗位</div>
        <div class="stat-value">LLM工程师</div>
        <div class="stat-desc">需求增长 120%</div>
      </div>
    </div> -->

    <!-- 岗位数量卡片 -->
    <div class="stat-card">
      <div class="stat-icon">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M19 3H14.82C14.4 1.84 13.3 1 12 1C10.7 1 9.6 1.84 9.18 3H5C3.9 3 3 3.9 3 5V19C3 20.1 3.9 21 5 21H19C20.1 21 21 20.1 21 19V5C21 3.9 20.1 3 19 3ZM12 3C12.55 3 13 3.45 13 4C13 4.55 12.55 5 12 5C11.45 5 11 4.55 11 4C11 3.45 11.45 3 12 3ZM12 7C14.76 7 17 9.24 17 12C17 14.76 14.76 17 12 17C9.24 17 7 14.76 7 12C7 9.24 9.24 7 12 7ZM12 9C10.34 9 9 10.34 9 12C9 13.66 10.34 15 12 15C13.66 15 15 13.66 15 12C15 10.34 13.66 9 12 9Z" fill="currentColor"/>
        </svg>
      </div>
      <div class="stat-content">
        <div class="stat-label">毕业去向落实率</div>
        <div class="stat-value">73%</div>
        <div class="stat-desc"></div>
      </div>
    </div>

    <!-- 热门技能卡片 -->
    <div class="stat-card">
      <div class="stat-icon">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M9.4 16.6L4.8 12L9.4 7.4L8 6L2 12L8 18L9.4 16.6ZM14.6 16.6L19.2 12L14.6 7.4L16 6L22 12L16 18L14.6 16.6Z" fill="currentColor"/>
        </svg>
      </div>
      <div class="stat-content">
        <div class="stat-label">协议签约率</div>
        <div class="stat-value">36%</div>
<!--        <div class="stat-desc">36%</div>-->
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { getCurrentYearGraduateCount } from '@/api/student'

const graduateCount = ref(null)
let refreshTimer = null

const graduateCountText = computed(() => {
  if (graduateCount.value === null) {
    return '--'
  }
  return Number(graduateCount.value).toLocaleString('zh-CN')
})

const fetchGraduateCount = async () => {
  try {
    const response = await getCurrentYearGraduateCount()
    graduateCount.value = response?.data?.graduateCount ?? 0
  } catch (error) {
    console.error('获取毕业生总人数失败:', error)
  }
}

onMounted(() => {
  fetchGraduateCount()
  refreshTimer = setInterval(fetchGraduateCount, 60000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
})
</script>

<style scoped>
.job-stats-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 10px;
  padding: 0 ;
}
.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
}

.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: #4fc3f7;
  background: rgba(79, 195, 247, 0.1);
  border-radius: 50%;
}

.stat-icon svg {
  width: 24px;
  height: 24px;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 4px;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #66ccff;
  margin-bottom: 4px;
}

.stat-desc {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}
</style>
