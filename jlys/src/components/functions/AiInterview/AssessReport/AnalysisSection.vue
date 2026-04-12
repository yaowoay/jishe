<template>
  <div class="analysis-section">
    <div class="section-container">
      <div class="radar-container">
        <RadarChart :chartData="chartData" :chartId="chartId" />
      </div>

      <div class="analysis-details">
        <div class="summary-cards">
          <div
            v-for="(item, index) in detailedItems"
            :key="index"
            class="summary-card"
          >
            <div class="card-content">
              <div class="card-header">
                <div class="ability-name">{{ getShortenedName(item) }}</div>
              </div>
              <div class="skill-level-container">
                <div class="skill-level-wrapper">
                  <div class="arrow-indicator" :style="{ left: getArrowPosition(getAverageScore(item)) + '%' }">
                    <div class="arrow-down"></div>
                  </div>
                  <div class="skill-level">
                    <div
                      v-for="(level, idx) in skillLevels"
                      :key="idx"
                      class="level-item"
                      :class="getLevelClass(idx)"
                    >
                      {{ level }}
                    </div>
                  </div>
                </div>
                <div class="view-details-btn" @click="showDetail(analysisType, index)">
                  查看详情
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    </div>
</template>

<script>
import { ref } from 'vue'
import RadarChart from './RadarChart.vue'

export default {
  name: 'AnalysisSection',
  components: {
    RadarChart
  },
  props: {
    chartData: Object,
    chartId: String,
    detailedItems: Array,
    analysisType: String
  },
  emits: ['show-detail'],
  setup(props, { emit }) {
    const skillLevels = ref(['差', '一般', '良好', '优秀', '卓越'])

    const getCategoryIndex = (category) => {
      return props.chartData.indicators.findIndex(item => item === category)
    }

    const getScoreForCategory = (category) => {
      const index = getCategoryIndex(category)
      return index !== -1 ? props.chartData.values[index] : 0
    }

    const getCategoriesForItem = (item) => {
      return item.name.split('，').map(name => name.trim())
    }

    const getAverageScore = (item) => {
      const categories = getCategoriesForItem(item)
      const scores = categories.map(category => getScoreForCategory(category))
      const sum = scores.reduce((acc, curr) => acc + curr, 0)
      return Math.round(sum / categories.length)
    }

    const getShortenedName = (item) => {
      const categories = getCategoriesForItem(item)
      return categories.join('，')
    }

    const getLevelClass = (levelIndex) => {
      if (levelIndex === 0) return 'level-bad'
      if (levelIndex === 1) return 'level-normal'
      if (levelIndex === 2) return 'level-good'
      if (levelIndex === 3) return 'level-excellent'
      if (levelIndex === 4) return 'level-outstanding'
      return ''
    }

    const getArrowPosition = (score) => {
      if (score < 40) return 10
      if (score < 60) return 30
      if (score < 75) return 50
      if (score < 90) return 70
      return 90
    }

    // 修改为触发事件，而不是路由跳转
    const showDetail = (type, id) => {
      emit('show-detail', { type, id })
    }

    return {
      skillLevels,
      getCategoriesForItem,
      getScoreForCategory,
      getAverageScore,
      getShortenedName,
      getLevelClass,
      getArrowPosition,
      showDetail
    }
  }
}
</script>

<style scoped>
/* 样式部分无需改动 */
.analysis-section, .section-container, .summary-card, .card-content {
  margin-top: 0 !important;
  padding-top: 0 !important;
  margin-bottom: 0 !important;
}

.analysis-section {
  padding: 15px;
}

.section-container {
  display: flex;
  flex-direction: row;
  gap: 30px;
  align-items: center;
}

.radar-container {
  width: 320px;
  height: 320px;
  flex-shrink: 0;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  padding: 15px;
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.analysis-details {
  flex: 1;
  padding-top: 15px;
}

.summary-cards {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.summary-card {
  width: 100%;
  background-color: #f9f9f9;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  transition: all 0.2s ease;
  display: flex;
  flex-direction: column;
  min-height: 80px;
}

.summary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.card-content {
  padding: 15px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.ability-name {
  font-size: 16px;
  color: #333;
  font-weight: bold;
  word-break: break-word;
  line-height: 1.3;
}

.skill-level-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.skill-level-wrapper {
  position: relative;
  flex: 1;
  padding-top: 15px;
}

.arrow-indicator {
  position: absolute;
  top: 0;
  transform: translateX(-50%);
  transition: left 0.3s ease;
}

.arrow-down {
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-top: 8px solid #333;
}

.skill-level {
  display: flex;
  width: 100%;
}

.level-item {
  flex: 1;
  padding: 8px 5px;
  text-align: center;
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.level-bad {
  background-color: #F56C6C;
}

.level-normal {
  background-color: #E6A23C;
}

.level-good {
  background-color: #67C23A;
}

.level-excellent {
  background-color: #409EFF;
}

.level-outstanding {
  background-color: #8A2BE2;
}

.view-details-btn {
  padding: 5px 15px;
  background-color: #f0f0f0;
  color: #666;
  font-size: 13px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-left: 20px;
  white-space: nowrap;
}

.view-details-btn:hover {
  background-color: #1E90FF;
  color: white;
}

@media (max-width: 768px) {
  .section-container {
    flex-direction: column;
  }

  .radar-container {
    width: 100%;
    max-width: 300px;
    margin: 0 auto;
  }

  .skill-level-container {
    flex-direction: column;
    align-items: stretch;
  }

  .view-details-btn {
    margin-left: 0;
    margin-top: 10px;
    text-align: center;
  }
}

@media (prefers-color-scheme: dark) {
  .radar-container {
    background-color: #333;
  }

  .summary-card {
    background-color: #444;
  }

  .ability-name {
    color: #fff;
  }

  .arrow-down {
    border-top-color: #fff;
  }

  .view-details-btn {
    background-color: #555;
    color: #ddd;
  }

  .view-details-btn:hover {
    background-color: #1E90FF;
  }
}
</style>