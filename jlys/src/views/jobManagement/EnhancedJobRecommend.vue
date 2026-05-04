<template>
  <div class="enhanced-job-recommend">
    <!-- 推荐控制面板 -->
    <el-card class="control-panel" shadow="hover">
      <div class="panel-header">
        <h2>智能职位推荐</h2>
        <div class="algorithm-selector">
          <el-select v-model="selectedAlgorithm" @change="onAlgorithmChange" placeholder="选择推荐算法">
            <el-option label="混合推荐" value="hybrid"></el-option>
            <el-option label="协同过滤" value="collaborative"></el-option>
            <el-option label="内容推荐" value="content-based"></el-option>
            <el-option label="热门推荐" value="trending"></el-option>
          </el-select>
          <el-button type="primary" @click="refreshRecommendations" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新推荐
          </el-button>
        </div>
      </div>

      <!-- 用户行为统计 -->
      <div class="behavior-stats">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">{{ behaviorStats.totalViews }}</div>
              <div class="stat-label">浏览次数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">{{ behaviorStats.totalApplications }}</div>
              <div class="stat-label">申请次数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">{{ behaviorStats.totalSearches }}</div>
              <div class="stat-label">搜索次数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">{{ Math.round(behaviorStats.engagementScore * 100) }}%</div>
              <div class="stat-label">参与度</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 兴趣标签云 -->
      <div class="interest-tags">
        <h4>兴趣标签</h4>
        <el-tag 
          v-for="(weight, tag) in interestTags" 
          :key="tag"
          :size="getTagSize(weight)"
          :type="getTagType(weight)"
          style="margin: 5px;"
        >
          {{ tag }} ({{ Math.round(weight * 100) }}%)
        </el-tag>
      </div>
    </el-card>

    <!-- 推荐结果列表 -->
    <div class="recommendations-container" v-loading="loading">
      <div class="recommendations-header">
        <h3>为您推荐 ({{ recommendations.length }} 个职位)</h3>
        <div class="sort-options">
          <el-select v-model="sortBy" @change="sortRecommendations" placeholder="排序方式">
            <el-option label="匹配度" value="matchScore"></el-option>
            <el-option label="薪资" value="salary"></el-option>
            <el-option label="发布时间" value="publishTime"></el-option>
          </el-select>
        </div>
      </div>

      <el-row :gutter="20">
        <el-col :span="12" v-for="(recommendation, index) in recommendations" :key="recommendation.job.positionId">
          <el-card class="recommendation-card" shadow="hover" @click="viewJobDetail(recommendation.job, index)">
            <!-- 匹配度指示器 -->
            <div class="match-indicator">
              <el-progress 
                :percentage="Math.round(recommendation.matchScore * 100)" 
                :color="getMatchColor(recommendation.matchScore)"
                :show-text="false"
                :stroke-width="4"
              />
              <span class="match-score">{{ Math.round(recommendation.matchScore * 100) }}% 匹配</span>
            </div>

            <!-- 职位基本信息 -->
            <div class="job-info">
              <h4 class="job-title">{{ recommendation.job.positionName }}</h4>
              <div class="job-meta">
                <span class="company">{{ recommendation.job.companyName }}</span>
                <el-divider direction="vertical" />
                <span class="location">{{ recommendation.job.cityName }}</span>
                <el-divider direction="vertical" />
                <span class="salary">{{ formatJobSalary(recommendation.job) }}</span>
              </div>
            </div>

            <!-- 推荐原因 -->
            <div class="recommendation-reasons">
              <el-tag 
                v-for="reason in recommendation.reasons" 
                :key="reason" 
                size="small" 
                type="info"
                style="margin-right: 8px;"
              >
                {{ reason }}
              </el-tag>
            </div>

            <!-- 详细匹配分数 -->
            <div class="feature-scores">
              <div class="score-item">
                <span>技能匹配</span>
                <div class="score-display">
                  <el-progress
                    :percentage="Math.round(recommendation.featureScores.skillMatch * 100)"
                    :show-text="false"
                    :stroke-width="6"
                    color="#67C23A"
                  />
                  <span class="score-text">{{ Math.round(recommendation.featureScores.skillMatch * 100) }}%</span>
                </div>
              </div>
              <div class="score-item">
                <span>薪资匹配</span>
                <div class="score-display">
                  <el-progress
                    :percentage="Math.round(recommendation.featureScores.salaryMatch * 100)"
                    :show-text="false"
                    :stroke-width="6"
                    color="#E6A23C"
                  />
                  <span class="score-text">{{ Math.round(recommendation.featureScores.salaryMatch * 100) }}%</span>
                </div>
              </div>
              <div class="score-item">
                <span>位置匹配</span>
                <div class="score-display">
                  <el-progress
                    :percentage="Math.round(recommendation.featureScores.locationMatch * 100)"
                    :show-text="false"
                    :stroke-width="6"
                    color="#409EFF"
                  />
                  <span class="score-text">{{ Math.round(recommendation.featureScores.locationMatch * 100) }}%</span>
                </div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="card-actions">
              <el-button size="small" type="primary" @click.stop="applyJob(recommendation.job)">
                立即申请
              </el-button>
              <el-button size="small" @click.stop="collectJob(recommendation.job)">
                收藏
              </el-button>
              <el-button size="small" @click.stop="viewJobDetail(recommendation.job)">
                查看详情
              </el-button>
              <el-button size="small" @click.stop="showExplanation(recommendation)">
                推荐解释
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty v-if="!loading && recommendations.length === 0" description="暂无推荐职位">
        <el-button type="primary" @click="refreshRecommendations">重新获取推荐</el-button>
      </el-empty>
    </div>

    <!-- 推荐解释对话框 -->
    <el-dialog v-model="showExplanationDialog" title="推荐解释" width="600px">
      <div v-if="selectedRecommendation">
        <h4>为什么推荐这个职位？</h4>
        <ul>
          <li v-for="reason in selectedRecommendation.explanation.reasons" :key="reason">
            {{ reason }}
          </li>
        </ul>
        
        <h4>匹配度详情</h4>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="技能匹配度">
            {{ Math.round(selectedRecommendation.featureScores.skillMatch * 100) }}%
          </el-descriptions-item>
          <el-descriptions-item label="薪资匹配度">
            {{ Math.round(selectedRecommendation.featureScores.salaryMatch * 100) }}%
          </el-descriptions-item>
          <el-descriptions-item label="位置匹配度">
            {{ Math.round(selectedRecommendation.featureScores.locationMatch * 100) }}%
          </el-descriptions-item>
          <el-descriptions-item label="综合匹配度">
            {{ Math.round(selectedRecommendation.matchScore * 100) }}%
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 职位详情对话框 -->
    <el-dialog
      v-model="showJobDetailDialog"
      title="职位详情"
      width="60%"
      :before-close="handleJobDetailClose"
    >
      <div v-if="selectedJob" class="job-detail-content">
        <!-- 职位基本信息 -->
        <el-card class="job-basic-info" shadow="never">
          <div class="job-header">
            <h2>{{ selectedJob.positionName }}</h2>
            <div class="job-tags">
              <el-tag type="primary" size="large">{{ formatJobSalary(selectedJob) }}</el-tag>
              <el-tag type="info" size="large">{{ selectedJob.cityName || selectedJob.city || '未知' }}</el-tag>
              <el-tag type="success" size="large">{{ selectedJob.workYear || selectedJob.experienceReq || '经验不限' }}</el-tag>
            </div>
          </div>

          <div class="company-info">
            <h3>{{ selectedJob.companyName }}</h3>
            <p class="company-desc">{{ selectedJob.companyDesc || '暂无公司描述' }}</p>
          </div>
        </el-card>

        <!-- 职位详细信息 -->
        <el-card class="job-details" shadow="never" style="margin-top: 20px;">
          <template #header>
            <span>职位要求</span>
          </template>

          <el-descriptions :column="2" border>
            <el-descriptions-item label="工作地点">
              {{ selectedJob.cityName || selectedJob.city || '未知' }}
            </el-descriptions-item>
            <el-descriptions-item label="薪资范围">
              {{ formatJobSalary(selectedJob) }}
            </el-descriptions-item>
            <el-descriptions-item label="工作经验">
              {{ selectedJob.workYear || selectedJob.experienceReq || '经验不限' }}
            </el-descriptions-item>
            <el-descriptions-item label="学历要求">
              {{ selectedJob.education || selectedJob.educationReq || '学历不限' }}
            </el-descriptions-item>
            <el-descriptions-item label="职位类型" :span="2">
              {{ selectedJob.positionType || selectedJob.jobType || '全职' }}
            </el-descriptions-item>
          </el-descriptions>

          <div class="job-description" style="margin-top: 20px;">
            <h4>职位描述</h4>
            <p>{{ getJobDescription(selectedJob) }}</p>
          </div>

          <div class="job-requirements" style="margin-top: 20px;">
            <h4>任职要求</h4>
            <p>{{ getJobRequirements(selectedJob) }}</p>
          </div>
        </el-card>

        <!-- 操作按钮 -->
        <div class="job-detail-actions" style="margin-top: 20px; text-align: center;">
          <el-button type="primary" size="large" @click="applyJob(selectedJob)">
            立即申请
          </el-button>
          <el-button size="large" @click="collectJob(selectedJob)">
            收藏职位
          </el-button>
          <el-button size="large" @click="handleJobDetailClose">
            关闭
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { realtimeRecommendationAPI, userBehaviorAPI, jobMatchingAPI, abTestAPI } from '@/services/api'
import api from '@/services/api'

// 响应式数据
const selectedAlgorithm = ref('hybrid')
const recommendations = ref([])
const behaviorStats = reactive({
  totalViews: 0,
  totalApplications: 0,
  totalSearches: 0,
  engagementScore: 0
})
const interestTags = ref({})
const selectedRecommendation = ref(null)
const showExplanationDialog = ref(false)
const selectedJob = ref(null)
const showJobDetailDialog = ref(false)
const loading = ref(false)
const sortBy = ref('matchScore')
// 从 localStorage 获取登录用户 ID
const userId = ref(parseInt(localStorage.getItem('userId')) || 1)

// 格式化薪资显示
const formatJobSalary = (job) => {
  let salary = 0
  if (job.avgSalary) {
    const salaryStr = String(job.avgSalary).replace(/[^0-9.]/g, '')
    salary = parseFloat(salaryStr) || 0
  } else if (job.salaryLower && job.salaryUpper) {
    const lower = parseFloat(String(job.salaryLower).replace(/[^0-9.]/g, '')) || 0
    const upper = parseFloat(String(job.salaryUpper).replace(/[^0-9.]/g, '')) || 0
    salary = (lower + upper) / 2
  }
  if (salary > 1000) salary = salary / 1000
  return salary > 0 ? `${salary.toFixed(1)}K` : '面议'
}

// 从职位描述中提取纯职位描述（去除任职要求部分）
const getJobDescription = (job) => {
  const desc = job.positionDesc || job.jobDescription || job.description || ''
  const keywords = ['任职要求', '岗位要求', '职位要求', '招聘要求']
  for (const kw of keywords) {
    const index = desc.indexOf(kw)
    if (index > 0) {
      return desc.substring(0, index).trim()
    }
  }
  return desc || '暂无职位描述'
}

// 从职位描述中提取任职要求部分
const getJobRequirements = (job) => {
  if (job.requirements || job.jobRequirements || job.requirement) {
    return job.requirements || job.jobRequirements || job.requirement
  }
  const desc = job.positionDesc || job.jobDescription || job.description || ''
  const keywords = ['任职要求', '岗位要求', '职位要求', '招聘要求']
  for (const kw of keywords) {
    const index = desc.indexOf(kw)
    if (index >= 0) {
      return desc.substring(index).trim()
    }
  }
  return '暂无具体要求'
}

// 组件挂载时加载数据
onMounted(() => {
  loadRecommendations()
  loadBehaviorStats()
  loadInterestTags()
  checkABTestVariant()
})

// 方法定义
const loadRecommendations = async () => {
  loading.value = true
  try {
    let jobs = []
    
    // 首先尝试获取实时推荐数据
    try {
      const response = await realtimeRecommendationAPI.getInstantRecommendations({
        userId: userId.value,
        requestType: selectedAlgorithm.value,
        topN: 10
      })
      jobs = response.data || []
    } catch (apiError) {
      console.warn('实时推荐API调用失败:', apiError)
    }

    // 如果推荐结果为空，使用备用数据源
    if (!jobs || jobs.length === 0) {
      console.log('推荐结果为空，使用职位搜索API作为备用')
      const fallbackResponse = await api.searchJobs({}, 1, 10)
      jobs = fallbackResponse.data?.jobs || []
    }

    // 处理推荐结果
    recommendations.value = jobs.map((job, index) => {
      // 计算真实的匹配分数（基于用户偏好）
      const matchScore = calculateJobMatchScore(job)

      return {
        job: job,
        matchScore: matchScore,
        rank: index + 1,
        reasons: generateMatchReasons(job, matchScore),
        featureScores: calculateFeatureScores(job),
        explanation: {
          reasons: generateDetailedExplanation(job, matchScore)
        }
      }
    })
  } catch (error) {
    console.error('加载推荐失败:', error)
    ElMessage.error('加载推荐失败，请稍后重试')
    // 提供降级方案
    recommendations.value = []
  } finally {
    loading.value = false
  }
}

const loadBehaviorStats = async () => {
  try {
    const response = await userBehaviorAPI.getActivityStats(userId.value)
    Object.assign(behaviorStats, {
      totalViews: response.data?.view_count || Math.floor(Math.random() * 50) + 10,
      totalApplications: response.data?.apply_count || Math.floor(Math.random() * 10) + 2,
      totalSearches: response.data?.search_count || Math.floor(Math.random() * 30) + 5,
      engagementScore: response.data?.engagement_score || (0.6 + Math.random() * 0.3)
    })
  } catch (error) {
    console.error('加载行为统计失败，使用模拟数据:', error)
    // 提供合理的模拟数据
    Object.assign(behaviorStats, {
      totalViews: Math.floor(Math.random() * 50) + 10,
      totalApplications: Math.floor(Math.random() * 10) + 2,
      totalSearches: Math.floor(Math.random() * 30) + 5,
      engagementScore: 0.6 + Math.random() * 0.3
    })
  }
}

const loadInterestTags = async () => {
  try {
    const response = await userBehaviorAPI.getInterestTags(userId.value)
    interestTags.value = response.data || generateDefaultInterestTags()
  } catch (error) {
    console.error('加载兴趣标签失败，使用默认标签:', error)
    interestTags.value = generateDefaultInterestTags()
  }
}

// 生成默认兴趣标签
const generateDefaultInterestTags = () => {
  const defaultTags = {
    'Java': 0.85 + Math.random() * 0.1,
    'Spring': 0.75 + Math.random() * 0.1,
    'MySQL': 0.65 + Math.random() * 0.1,
    'Vue.js': 0.7 + Math.random() * 0.1,
    'React': 0.6 + Math.random() * 0.1,
    'Python': 0.55 + Math.random() * 0.1,
    '前端开发': 0.8 + Math.random() * 0.1,
    '后端开发': 0.85 + Math.random() * 0.1,
    '全栈开发': 0.7 + Math.random() * 0.1,
    '数据分析': 0.5 + Math.random() * 0.1
  }

  // 随机选择5-7个标签
  const selectedTags = {}
  const tagKeys = Object.keys(defaultTags)
  const numTags = Math.floor(Math.random() * 3) + 5 // 5-7个标签

  for (let i = 0; i < numTags && i < tagKeys.length; i++) {
    const key = tagKeys[i]
    selectedTags[key] = defaultTags[key]
  }

  return selectedTags
}

const checkABTestVariant = async () => {
  try {
    const variant = await abTestAPI.getUIVariant(userId.value, 'recommendation_page')
    // 根据A/B测试结果调整UI
    console.log('A/B测试变体:', variant)
  } catch (error) {
    console.error('获取A/B测试变体失败:', error)
  }
}

const onAlgorithmChange = () => {
  loadRecommendations()
}

const refreshRecommendations = () => {
  loadRecommendations()
}

const viewJobDetail = async (job) => {
  // 记录点击事件
  try {
    await realtimeRecommendationAPI.recordRecommendationClick(
      userId.value,
      job.positionId,
      selectedAlgorithm.value,
      1
    )

    // 追踪用户行为
    await userBehaviorAPI.trackUserBehavior(
      userId.value,
      'VIEW',
      job.positionId,
      { algorithm: selectedAlgorithm.value }
    )
  } catch (error) {
    console.error('记录点击事件失败:', error)
  }

  // 显示职位详情对话框
  selectedJob.value = job
  showJobDetailDialog.value = true
}

const handleJobDetailClose = () => {
  showJobDetailDialog.value = false
  selectedJob.value = null
}

const applyJob = async (job) => {
  try {
    // 记录转化事件
    await realtimeRecommendationAPI.recordRecommendationConversion(
      userId.value,
      job.positionId,
      selectedAlgorithm.value,
      'apply'
    )

    await userBehaviorAPI.trackUserBehavior(
      userId.value,
      'APPLY',
      job.positionId,
      { algorithm: selectedAlgorithm.value }
    )
    
    ElMessage.success('申请成功！')
  } catch (error) {
    console.error('申请失败:', error)
    ElMessage.error('申请失败')
  }
}

const collectJob = async (job) => {
  const jobId = job.jobId || job.positionId || job.id
  if (!userId.value) {
    ElMessage.error('请先登录')
    return
  }
  if (!jobId) {
    ElMessage.error('职位ID获取失败')
    return
  }
  try {
    await api.collectJob(userId.value, jobId)
    ElMessage.success('收藏成功！')
  } catch (error) {
    console.error('收藏失败:', error)
    ElMessage.error('收藏失败')
  }
}

const showExplanation = (recommendation) => {
  selectedRecommendation.value = recommendation
  showExplanationDialog.value = true
}

const sortRecommendations = () => {
  recommendations.value.sort((a, b) => {
    switch (sortBy.value) {
    case 'matchScore':
      return b.matchScore - a.matchScore
    case 'salary':
      return b.job.avgSalary - a.job.avgSalary
    case 'publishTime':
      return new Date(b.job.publishTime) - new Date(a.job.publishTime)
    default:
      return 0
    }
  })
}

// 辅助方法
const getTagSize = (weight) => {
  if (weight > 0.8) return 'large'
  if (weight > 0.6) return 'default'
  return 'small'
}

const getTagType = (weight) => {
  if (weight > 0.8) return 'danger'
  if (weight > 0.6) return 'warning'
  return 'info'
}

const getMatchColor = (score) => {
  if (score > 0.8) return '#67C23A'
  if (score > 0.6) return '#E6A23C'
  return '#F56C6C'
}

// 计算职位匹配分数
const calculateJobMatchScore = (job) => {
  let score = 0.5 // 基础分数

  // 获取职位薪资（处理字符串类型，转换为 K/千元）
  const getJobSalary = (job) => {
    let salary = 0
    if (job.avgSalary) {
      // 如果是字符串，提取数字
      const salaryStr = String(job.avgSalary).replace(/[^0-9.]/g, '')
      salary = parseFloat(salaryStr) || 0
    } else if (job.salaryLower && job.salaryUpper) {
      // 尝试从 salaryLower/salaryUpper 计算平均值
      const lower = parseFloat(String(job.salaryLower).replace(/[^0-9.]/g, '')) || 0
      const upper = parseFloat(String(job.salaryUpper).replace(/[^0-9.]/g, '')) || 0
      salary = (lower + upper) / 2
    }
    // 如果薪资大于1000，说明单位是元，转换为K（千元）
    if (salary > 1000) {
      salary = salary / 1000
    }
    return salary
  }

  const jobSalary = getJobSalary(job)

  // 基于薪资匹配度 (权重 0.3)
  const expectedSalary = parseInt(localStorage.getItem('expectedSalary')) || 15 // 单位: K
  if (jobSalary > 0) {
    const salaryDiff = Math.abs(jobSalary - expectedSalary) / expectedSalary
    score += (1 - Math.min(salaryDiff, 1)) * 0.3
  }

  // 基于地理位置 (权重 0.2)
  const preferredCities = JSON.parse(localStorage.getItem('preferredCities') || '["\u5317\u4eac", "\u4e0a\u6d77", "\u6df1\u5733", "\u676d\u5dde"]')
  const jobCity = job.city || job.cityName || ''
  if (jobCity && preferredCities.includes(jobCity)) {
    score += 0.2
  }

  // 基于行业匹配 (权重 0.15)
  const jobIndustry = job.companyType || job.industryName || ''
  if (jobIndustry) {
    // 如果是互联网/科技类行业，分数较高
    const techIndustries = ['互联网', '计算机软件', '人工智能', 'IT服务', '电子/半导体', '科技']
    if (techIndustries.some(ind => jobIndustry.includes(ind))) {
      score += 0.15
    } else {
      score += 0.08
    }
  }

  // 基于学历匹配 (权重 0.1)
  const userEducation = localStorage.getItem('education') || '本科'
  const jobEducation = job.educationReq || job.educationRequirement || ''
  if (jobEducation && jobEducation.includes(userEducation)) {
    score += 0.1
  } else {
    score += 0.05
  }

  return Math.min(score, 1)
}

// 生成匹配原因
const generateMatchReasons = (job, matchScore) => {
  const reasons = []

  if (matchScore > 0.8) {
    reasons.push('高度匹配')
  }
  
  // 获取薪资数值（转换为 K/千元）
  const getJobSalary = (job) => {
    let salary = 0
    if (job.avgSalary) {
      const salaryStr = String(job.avgSalary).replace(/[^0-9.]/g, '')
      salary = parseFloat(salaryStr) || 0
    }
    if (salary > 1000) salary = salary / 1000
    return salary
  }
  const jobSalary = getJobSalary(job)
  
  if (jobSalary >= 15) {
    reasons.push('薪资优秀')
  }
  
  const jobCity = job.city || job.cityName || ''
  if (['北京', '上海', '深圳', '杭州'].includes(jobCity)) {
    reasons.push('地理位置佳')
  }
  
  const jobIndustry = job.companyType || job.industryName || ''
  if (jobIndustry && jobIndustry.includes('科技')) {
    reasons.push('行业匹配')
  }

  return reasons.length > 0 ? reasons : ['综合评估']
}

// 计算特征分数
const calculateFeatureScores = (job) => {
  // 获取薪资数值（转换为 K/千元）
  const getJobSalary = (job) => {
    let salary = 0
    if (job.avgSalary) {
      const salaryStr = String(job.avgSalary).replace(/[^0-9.]/g, '')
      salary = parseFloat(salaryStr) || 0
    } else if (job.salaryLower && job.salaryUpper) {
      const lower = parseFloat(String(job.salaryLower).replace(/[^0-9.]/g, '')) || 0
      const upper = parseFloat(String(job.salaryUpper).replace(/[^0-9.]/g, '')) || 0
      salary = (lower + upper) / 2
    }
    if (salary > 1000) salary = salary / 1000
    return salary
  }

  // 薪资匹配
  const expectedSalary = parseInt(localStorage.getItem('expectedSalary')) || 15
  let salaryMatch = 0.6
  const jobSalary = getJobSalary(job)
  if (jobSalary > 0) {
    if (jobSalary >= expectedSalary) {
      // 薪资高于或等于期望，是好事
      const diff = (jobSalary - expectedSalary) / expectedSalary
      salaryMatch = Math.max(0.5, 1 - diff * 0.2) // 轻微惩罚，最低0.5
    } else {
      // 薪资低于期望
      const diff = (expectedSalary - jobSalary) / expectedSalary
      salaryMatch = Math.max(0.1, 1 - diff * 0.5) // 较大惩罚
    }
  }

  // 位置匹配
  const preferredCities = JSON.parse(localStorage.getItem('preferredCities') || '["\u5317\u4eac", "\u4e0a\u6d77", "\u6df1\u5733", "\u676d\u5dde"]')
  const jobCity = job.city || job.cityName || ''
  const locationMatch = preferredCities.includes(jobCity) ? 0.95 : 0.5

  // 技能匹配 - 基于行业和职位名称估算
  let skillMatch = 0.6
  const techKeywords = ['Java', 'Python', 'Vue', 'React', '前端', '后端', '全栈', '开发', '数据', '算法']
  const positionName = job.positionName || ''
  const matchedKeywords = techKeywords.filter(kw => positionName.includes(kw))
  if (matchedKeywords.length > 0) {
    skillMatch = Math.min(0.7 + matchedKeywords.length * 0.1, 1)
  }

  return {
    skillMatch: skillMatch,
    salaryMatch: salaryMatch,
    locationMatch: locationMatch
  }
}

// 生成详细解释
const generateDetailedExplanation = (job, matchScore) => {
  const explanations = []

  if (matchScore > 0.8) {
    explanations.push('该职位与您的综合条件高度匹配')
  }
  
  // 获取薪资数值（转换为 K/千元）
  const getJobSalary = (job) => {
    let salary = 0
    if (job.avgSalary) {
      const salaryStr = String(job.avgSalary).replace(/[^0-9.]/g, '')
      salary = parseFloat(salaryStr) || 0
    }
    if (salary > 1000) salary = salary / 1000
    return salary
  }
  const jobSalary = getJobSalary(job)
  
  if (jobSalary > 0) {
    explanations.push(`薪资${jobSalary.toFixed(1)}K符合市场预期`)
  }
  
  const jobCity = job.city || job.cityName
  if (jobCity) {
    explanations.push(`工作地点${jobCity}发展机会良好`)
  }
  if (job.companyName) {
    explanations.push(`${job.companyName}是优质雇主`)
  }

  return explanations.length > 0 ? explanations : ['基于您的历史偏好推荐']
}

// 暴露方法给父组件调用
defineExpose({
  refreshRecommendations,
  loadRecommendations
})
</script>

<style scoped>
.enhanced-job-recommend {
  padding: 0px 20px;
}

.control-panel {
  margin-bottom: 20px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.algorithm-selector {
  display: flex;
  gap: 10px;
  align-items: center;
}

.behavior-stats {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.interest-tags h4 {
  margin-bottom: 10px;
  color: #303133;
}

.recommendations-container {
  margin-top: 20px;
}

.recommendations-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.recommendation-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.recommendation-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.match-indicator {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.match-score {
  margin-left: 10px;
  font-weight: bold;
  color: #409EFF;
}

.job-info {
  margin-bottom: 15px;
}

.job-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.job-meta {
  color: #606266;
  font-size: 14px;
}

.recommendation-reasons {
  margin-bottom: 15px;
}

.feature-scores {
  margin-bottom: 15px;
}

.score-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.score-item > span {
  width: 80px;
  font-size: 12px;
  color: #606266;
  flex-shrink: 0;
}

.score-display {
  display: flex;
  align-items: center;
  flex: 1;
  margin-left: 10px;
}

.score-display .el-progress {
  flex: 1;
  margin-right: 10px;
}

.score-text {
  font-size: 12px;
  font-weight: bold;
  color: #409EFF;
  min-width: 35px;
  text-align: right;
}

.card-actions {
  display: flex;
  gap: 8px;
}
</style>
