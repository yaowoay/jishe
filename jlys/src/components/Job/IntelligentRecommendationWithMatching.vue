<template>
  <div class="intelligent-recommendation-with-matching">
    <!-- 页面头部 -->
    <div class="page-header">
      <el-card shadow="never">
        <div class="header-content">
          <div class="title-section">
            <h1>
              <el-icon><MagicStick /></el-icon>
              智能推荐与匹配度分析
            </h1>
            <p class="subtitle">基于AI算法为您精准推荐合适的职位，并提供详细的匹配度分析</p>
          </div>
          <div class="action-section">
            <el-button type="primary" @click="refreshRecommendations" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新推荐
            </el-button>
            <el-button @click="showProfileDialog = true">
              <el-icon><Setting /></el-icon>
              用户画像设置
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 统计概览 -->
    <div class="stats-overview">
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
                <el-icon color="#E6A23C"><Star /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.highMatchJobs }}</div>
                <div class="stat-label">高匹配职位</div>
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
                <div class="stat-number">{{ stats.clickRate }}%</div>
                <div class="stat-label">点击率</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 推荐算法选择 -->
    <el-card class="algorithm-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>推荐算法设置</span>
          <div class="algorithm-controls">
            <el-select v-model="selectedAlgorithm" placeholder="选择推荐算法" @change="onAlgorithmChange">
              <el-option label="混合推荐" value="hybrid"></el-option>
              <el-option label="协同过滤" value="collaborative"></el-option>
              <el-option label="内容推荐" value="content"></el-option>
              <el-option label="热门推荐" value="trending"></el-option>
            </el-select>
            <el-select v-model="sortBy" placeholder="排序方式" @change="sortRecommendations">
              <el-option label="匹配度" value="matchScore"></el-option>
              <el-option label="薪资" value="salary"></el-option>
              <el-option label="发布时间" value="publishTime"></el-option>
            </el-select>
          </div>
        </div>
      </template>
      
      <div class="algorithm-info">
        <el-alert
          :title="getAlgorithmDescription(selectedAlgorithm)"
          type="info"
          :closable="false"
          show-icon
        />
      </div>
    </el-card>

    <!-- 推荐职位列表 -->
    <el-card class="recommendations-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>智能推荐职位 ({{ recommendedJobs.length }})</span>
          <el-button type="text" @click="showAllRecommendations = !showAllRecommendations">
            {{ showAllRecommendations ? '收起' : '查看全部' }}
          </el-button>
        </div>
      </template>

      <div class="job-list">
        <div 
          v-for="(job, index) in displayedJobs" 
          :key="job.jobId" 
          class="job-match-item"
          :class="{ 'high-match': job.matchResult.overallScore >= 0.8 }"
        >
          <!-- 职位基本信息和总体匹配度 -->
          <div class="match-header">
            <div class="job-basic-info">
              <h3 class="job-title">{{ job.positionName }}</h3>
              <div class="job-meta">
                <span class="company">{{ job.companyName }}</span>
                <el-divider direction="vertical" />
                <span class="location">{{ job.cityName }}</span>
                <el-divider direction="vertical" />
                <span class="salary">{{ job.avgSalary }}K</span>
                <el-divider direction="vertical" />
                <span class="algorithm-tag">
                  <el-tag size="small" :type="getAlgorithmTagType(job.algorithm)">
                    {{ getAlgorithmName(job.algorithm) }}
                  </el-tag>
                </span>
              </div>
            </div>
            <div class="overall-match">
              <div class="match-score-circle">
                <el-progress
                  type="circle"
                  :percentage="Math.round(job.matchResult.overallScore * 100)"
                  :color="getMatchColor(job.matchResult.overallScore)"
                  :width="80"
                />
              </div>
              <div class="match-level">{{ getMatchLevel(job.matchResult.overallScore) }}</div>
            </div>
          </div>

          <!-- 详细匹配分析 -->
          <div class="match-details">
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="detail-item">
                  <div class="detail-label">技能匹配</div>
                  <el-progress
                    :percentage="Math.round(job.matchResult.skillMatch * 100)"
                    :color="getMatchColor(job.matchResult.skillMatch)"
                    :show-text="true"
                  />
                  <div class="match-tags">
                    <el-tag
                      v-for="skill in job.matchResult.matchedSkills?.slice(0, 3)"
                      :key="skill"
                      size="small"
                      type="success"
                    >
                      {{ skill }}
                    </el-tag>
                    <el-tag v-if="job.matchResult.matchedSkills?.length > 3" size="small" type="info">
                      +{{ job.matchResult.matchedSkills.length - 3 }}
                    </el-tag>
                  </div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="detail-item">
                  <div class="detail-label">薪资匹配</div>
                  <el-progress
                    :percentage="Math.round(job.matchResult.salaryMatch * 100)"
                    :color="getMatchColor(job.matchResult.salaryMatch)"
                    :show-text="true"
                  />
                  <div class="salary-info">
                    <span>期望: {{ userProfile.expectedSalaryRange[0] }}-{{ userProfile.expectedSalaryRange[1] }}K</span>
                    <br>
                    <span>提供: {{ job.avgSalary }}K</span>
                  </div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="detail-item">
                  <div class="detail-label">经验匹配</div>
                  <el-progress
                    :percentage="Math.round(job.matchResult.experienceMatch * 100)"
                    :color="getMatchColor(job.matchResult.experienceMatch)"
                    :show-text="true"
                  />
                  <div class="experience-info">
                    <span>要求: {{ job.experienceRequirement }}</span>
                    <br>
                    <span>您的: {{ userProfile.experience }}</span>
                  </div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="detail-item">
                  <div class="detail-label">地理位置</div>
                  <el-progress
                    :percentage="Math.round(job.matchResult.locationMatch * 100)"
                    :color="getMatchColor(job.matchResult.locationMatch)"
                    :show-text="true"
                  />
                  <div class="location-info">
                    <span>{{ job.cityName }}</span>
                    <el-tag
                      v-if="userProfile.preferredCities.includes(job.cityName)"
                      size="small"
                      type="success"
                    >
                      偏好城市
                    </el-tag>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 操作按钮 -->
          <div class="job-actions">
            <el-button type="primary" size="small" @click="viewJobDetail(job)">
              查看详情
            </el-button>
            <el-button size="small" @click="collectJob(job)">
              收藏
            </el-button>
            <el-button size="small" @click="showMatchExplanation(job)">
              匹配解释
            </el-button>
          </div>
        </div>
      </div>

      <!-- 加载更多 -->
      <div v-if="!showAllRecommendations && recommendedJobs.length > 5" class="load-more">
        <el-button type="text" @click="showAllRecommendations = true">
          查看更多推荐 ({{ recommendedJobs.length - 5 }}+)
        </el-button>
      </div>
    </el-card>

    <!-- 用户画像设置对话框 -->
    <el-dialog
      v-model="showProfileDialog"
      title="用户画像设置"
      width="800px"
    >
      <UserProfileForm
        :profile="userProfile"
        @profile-updated="handleProfileUpdate"
        @close="showProfileDialog = false"
      />
    </el-dialog>

    <!-- 匹配解释对话框 -->
    <el-dialog
      v-model="showExplanationDialog"
      title="匹配度解释"
      width="600px"
    >
      <MatchExplanation
        v-if="selectedJob"
        :job="selectedJob"
        :user-profile="userProfile"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'    
import { ElMessage } from 'element-plus'    
import { 
  MagicStick, Refresh, Setting, View, SuccessFilled, 
  Star, TrendCharts 
} from '@element-plus/icons-vue'    

// 导入子组件
import UserProfileForm from './UserProfileForm.vue'    
import MatchExplanation from './MatchExplanation.vue'    

// 响应式数据
const loading = ref(false)    
const showProfileDialog = ref(false)    
const showExplanationDialog = ref(false)    
const showAllRecommendations = ref(false)    
const selectedAlgorithm = ref('hybrid')    
const sortBy = ref('matchScore')    
const selectedJob = ref(null)    

// 用户画像数据
const userProfile = reactive({
  expectedSalaryRange: [15, 25],
  experience: '3-5年',
  education: '本科',
  skills: ['JavaScript', 'Vue.js', 'Node.js', 'Python'],
  preferredCities: ['北京', '上海', '深圳'],
  preferredIndustries: ['互联网', '金融科技']
})    

// 统计数据
const stats = reactive({
  totalRecommendations: 28,
  averageMatch: 82,
  highMatchJobs: 12,
  clickRate: 8.5
})    

// 推荐职位数据
const recommendedJobs = ref([])    

// 计算属性
const displayedJobs = computed(() => {
  return showAllRecommendations.value ? recommendedJobs.value : recommendedJobs.value.slice(0, 5)    
})    
</script>
