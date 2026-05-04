<template>
  <div class="intelligent-recommendation">
    <!-- 页面头部 -->
    <div class="page-header">
      <el-card shadow="never">
        <div class="header-content">
          <div class="title-section">
            <h1>
              <el-icon><MagicStick /></el-icon>
              智能推荐系统
            </h1>
            <p class="subtitle">基于AI算法为您精准推荐合适的职位</p>
          </div>
          <div class="action-section">
            <el-button type="primary" @click="refreshAllRecommendations" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新推荐
            </el-button>
            <el-button @click="showPreferencesDialog = true">
              <el-icon><Setting /></el-icon>
              偏好设置
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 功能导航标签 -->
    <el-tabs v-model="activeTab" class="recommendation-tabs" @tab-change="handleTabChange">
      <!-- 智能推荐主页 -->
      <el-tab-pane label="智能推荐" name="recommendations">
        <div class="tab-content">
          <EnhancedJobRecommend ref="enhancedRecommendRef" />
        </div>
      </el-tab-pane>

      <!-- 实时推荐 -->
      <el-tab-pane label="实时推荐" name="realtime">
        <div class="tab-content">
          <RealtimeRecommendation />
        </div>
      </el-tab-pane>

      <!-- 偏好设置 -->
      <el-tab-pane label="偏好设置" name="preferences">
        <div class="tab-content">
          <UserPreferences @preferences-updated="handlePreferencesUpdate" />
        </div>
      </el-tab-pane>

      <!-- 效果分析 -->
      <el-tab-pane label="效果分析" name="analytics">
        <div class="tab-content">
          <RecommendationAnalytics />
        </div>
      </el-tab-pane>

      <!-- 匹配度评分 -->
      <el-tab-pane label="匹配度评分" name="matching">
        <div class="tab-content">
          <JobMatchingScore />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 推荐统计概览 -->
    <div class="stats-overview" v-if="activeTab === 'recommendations'">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon color="#409EFF"><View /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.totalRecommendations }}</div>
                <div class="stat-label">今日推荐</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon color="#67C23A"><SuccessFilled /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.clickRate }}%</div>
                <div class="stat-label">点击率</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon color="#E6A23C"><Star /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.averageMatch }}%</div>
                <div class="stat-label">平均匹配度</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon color="#F56C6C"><TrendCharts /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.conversionRate }}%</div>
                <div class="stat-label">转化率</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 偏好设置对话框 -->
    <el-dialog
      v-model="showPreferencesDialog"
      title="推荐偏好设置"
      width="600px"
    >
      <UserPreferences
        @preferences-updated="handlePreferencesUpdate"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'   
import { ElMessage } from 'element-plus'   
import {
  MagicStick, Refresh, Setting, View, SuccessFilled,
  Star, TrendCharts
} from '@element-plus/icons-vue'   

// 导入现有组件
import EnhancedJobRecommend from '../EnhancedJobRecommend.vue'   
import RealtimeRecommendation from '../RealtimeRecommendation.vue'   
import UserPreferences from './UserPreferences.vue'   
import RecommendationAnalytics from './RecommendationAnalytics.vue'   
import JobMatchingScore from '../JobMatchingScore.vue'   

// 响应式数据
const userId = ref(parseInt(localStorage.getItem('userId')) || 1)   
const loading = ref(false)   
const activeTab = ref('recommendations')   
const showPreferencesDialog = ref(false)   
const enhancedRecommendRef = ref()   

// 统计数据
const stats = reactive({
  totalRecommendations: 28,
  clickRate: 8.5,
  averageMatch: 82,
  conversionRate: 3.2
})   

// 方法定义
const handleTabChange = (tabName) => {
  console.log('切换到标签:', tabName)   
}   

const refreshAllRecommendations = () => {
  loading.value = true   
  if (enhancedRecommendRef.value) {
    enhancedRecommendRef.value.refreshRecommendations()   
  }
  updateStats()   
  setTimeout(() => {
    loading.value = false   
    ElMessage.success('推荐已刷新')   
  }, 1000)   
}   

const handlePreferencesUpdate = (preferences) => {
  ElMessage.success('偏好设置已更新，正在重新生成推荐...')   
  showPreferencesDialog.value = false   
  setTimeout(() => {
    refreshAllRecommendations()   
  }, 500)   
}   

const updateStats = () => {
  stats.totalRecommendations = Math.floor(Math.random() * 20) + 20   
  stats.clickRate = (Math.random() * 5 + 6).toFixed(1)   
  stats.averageMatch = Math.floor(Math.random() * 20) + 75   
  stats.conversionRate = (Math.random() * 2 + 2).toFixed(1)   
}   

// 组件挂载时初始化
onMounted(() => {
  updateStats()   
})   
</script>

<style scoped>
.intelligent-recommendation {
  padding: 20px;
  min-height: 100vh;
  background: #f5f7fa;
}

.page-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section h1 {
  margin: 0;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}

.subtitle {
  margin: 10px 0 0 0;
  color: #606266;
  font-size: 14px;
}

.action-section {
  display: flex;
  gap: 10px;
}

.recommendation-tabs {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.tab-content {
  padding: 20px;
}

.stats-overview {
  margin: 20px 0;
}

.stat-card {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  font-size: 24px;
  margin-right: 15px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}
</style>

// 算法相关
const selectedAlgorithms = ref(['collaborative', 'content', 'trending']);
const algorithmWeights = ref({
  collaborative: 0.4,
  content: 0.4,
  trending: 0.2
});

// 推荐结果
const recommendationsByAlgorithm = reactive({});
const hybridRecommendations = ref([]);
const loadingStates = reactive({});

// 统计数据
const stats = reactive({
  totalRecommendations: 0,
  clickRate: 0,
  averageMatch: 0,
  conversionRate: 0
});

// 对话框状态
const showPreferencesDialog = ref(false);
const showAlgorithmComparison = ref(false);
const showExplanationDialog = ref(false);
const selectedRecommendation = ref(null);

// 用户偏好
const userPreferences = ref({
  preferredCities: [],
  preferredIndustries: [],
  salaryRange: [0, 50],
  experienceLevel: '',
  workType: '',
  companySize: ''
});

// 计算属性
const getAlgorithmLabel = computed(() => {
  return (algorithm) => {
    const labels = {
      collaborative: '协同过滤',
      content: '内容推荐',
      trending: '热门推荐',
      hybrid: '混合推荐'
    };
    return labels[algorithm] || algorithm;
  };
});

// 生命周期
onMounted(() => {
  loadUserPreferences();
  loadRecommendationStats();
  loadAllRecommendations();
});

// 方法定义
const loadUserPreferences = async () => {
  try {
    const response = await intelligentRecommendationAPI.getRecommendationConfig(userId.value);
    if (response.data) {
      userPreferences.value = { ...userPreferences.value, ...response.data };
      selectedAlgorithms.value = response.data.algorithms || selectedAlgorithms.value;
      algorithmWeights.value = response.data.weights || algorithmWeights.value;
    }
  } catch (error) {
    console.error('加载用户偏好失败:', error);
  }
};

const loadRecommendationStats = async () => {
  try {
    const response = await intelligentRecommendationAPI.getRecommendationStats(userId.value);
    Object.assign(stats, response.data);
  } catch (error) {
    console.error('加载推荐统计失败:', error);
    // 使用模拟数据
    Object.assign(stats, {
      totalRecommendations: 25,
      clickRate: 12.5,
      averageMatch: 78,
      conversionRate: 8.2
    });
  }
};
</script>

<style scoped>
.intelligent-recommendation {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section h1 {
  margin: 0 0 8px 0;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}

.subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.action-section {
  display: flex;
  gap: 10px;
}

.stats-overview {
  margin-bottom: 20px;
}

.stat-card {
  height: 100px;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  font-size: 32px;
  margin-right: 15px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.algorithm-section {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.recommendations-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.explanation-content h4 {
  margin-top: 20px;
  margin-bottom: 10px;
  color: #303133;
}

.explanation-content ul {
  padding-left: 20px;
  margin-bottom: 20px;
}

.match-details {
  space-y: 10px;
}

.match-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.match-item span {
  width: 80px;
  font-size: 14px;
  color: #606266;
  margin-right: 15px;
}
</style>
