<template>
  <div class="salary-prediction">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="title-section">
        <h1>
          <el-icon><TrendCharts /></el-icon>
          薪资预测系统
        </h1>
        <p class="subtitle">基于用户画像和市场数据的智能薪资预测</p>
      </div>
      <div class="action-section">
        <el-switch
          v-model="useAPIData"
          active-text="API数据"
          inactive-text="本地计算"
          @change="handleDataSourceChange"
          style="margin-right: 10px;"
        />
        <el-button type="primary" @click="predictSalary" :loading="predicting">
          <el-icon><Refresh /></el-icon>
          重新预测
        </el-button>
        <el-button @click="exportReport">
          <el-icon><Download /></el-icon>
          导出报告
        </el-button>
      </div>
    </div>

    <!-- 快速用户画像选择 -->
    <div class="quick-profiles">
      <h3>快速选择用户画像</h3>
      <div class="profile-cards">
        <div
            v-for="profile in quickProfiles"
            :key="profile.id"
            class="quick-profile-card"
            :class="{ active: selectedQuickProfile === profile.id }"
            @click="selectQuickProfile(profile)"
        >
          <div class="profile-avatar">
            <el-icon color="#409EFF"><User /></el-icon>
          </div>
          <div class="profile-info">
            <h4>{{ profile.name }}</h4>
            <p v-html="formatDescription(profile.description)"></p>
            <div class="profile-tags">
              <el-tag size="small">{{ profile.experience }}</el-tag>
              <el-tag size="small" type="success">{{ profile.education }}</el-tag>
              <el-tag size="small" type="warning">{{ profile.city }}</el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 用户画像输入区域 -->
    <el-row :gutter="20" class="input-section">
      <el-col :span="8">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
            </div>
          </template>
          <el-form :model="userProfile" label-width="80px">
            <el-form-item label="工作经验">
              <el-select v-model="userProfile.experience" placeholder="请选择" @change="predictSalary">
                <el-option label="应届毕业生" value="0-1"></el-option>
                <el-option label="1-3年" value="1-3"></el-option>
                <el-option label="3-5年" value="3-5"></el-option>
                <el-option label="5-10年" value="5-10"></el-option>
                <el-option label="10年以上" value="10+"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="学历">
              <el-select v-model="userProfile.education" placeholder="请选择" @change="predictSalary">
                <el-option label="专科" value="专科"></el-option>
                <el-option label="本科" value="本科"></el-option>
                <el-option label="硕士" value="硕士"></el-option>
                <el-option label="博士" value="博士"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="工作城市">
              <el-select v-model="userProfile.city" placeholder="请选择" @change="predictSalary">
                <el-option label="北京" value="北京"></el-option>
                <el-option label="上海" value="上海"></el-option>
                <el-option label="深圳" value="深圳"></el-option>
                <el-option label="杭州" value="杭州"></el-option>
                <el-option label="广州" value="广州"></el-option>
                <el-option label="成都" value="成都"></el-option>
                <el-option label="南京" value="南京"></el-option>
                <el-option label="武汉" value="武汉"></el-option>
                <el-option label="西安" value="西安"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="年龄">
              <el-select v-model="userProfile.age" placeholder="请选择" @change="predictSalary">
                <el-option label="22-25岁" value="22-25"></el-option>
                <el-option label="26-30岁" value="26-30"></el-option>
                <el-option label="31-35岁" value="31-35"></el-option>
                <el-option label="36-40岁" value="36-40"></el-option>
                <el-option label="40岁以上" value="40+"></el-option>
              </el-select>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <span>技能信息</span>
            </div>
          </template>
          <el-form :model="userProfile" label-width="80px">
            <el-form-item label="职位类型">
              <el-select v-model="userProfile.jobType" placeholder="请选择" @change="predictSalary">
                <el-option label="AI工程师" value="AI工程师"></el-option>
                <el-option label="算法工程师" value="算法工程师"></el-option>
                <el-option label="机器学习工程师" value="机器学习工程师"></el-option>
                <el-option label="数据科学家" value="数据科学家"></el-option>
                <el-option label="深度学习工程师" value="深度学习工程师"></el-option>
                <el-option label="Python开发工程师" value="Python开发工程师"></el-option>
                <el-option label="前端工程师" value="前端工程师"></el-option>
                <el-option label="后端工程师" value="后端工程师"></el-option>
                <el-option label="全栈工程师" value="全栈工程师"></el-option>
                <el-option label="产品经理" value="产品经理"></el-option>
                <el-option label="数据分析师" value="数据分析师"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="技能等级">
              <el-rate v-model="userProfile.skillLevel" :max="5" show-text @change="predictSalary"></el-rate>
            </el-form-item>
            <el-form-item label="行业经验">
              <el-checkbox-group v-model="userProfile.industries" @change="predictSalary">
                <el-checkbox label="互联网">互联网</el-checkbox>
                <el-checkbox label="金融">金融</el-checkbox>
                <el-checkbox label="教育">教育</el-checkbox>
                <el-checkbox label="医疗">医疗</el-checkbox>
                <el-checkbox label="电商">电商</el-checkbox>
                <el-checkbox label="游戏">游戏</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <span>期望信息</span>
            </div>
          </template>
          <el-form :model="userProfile" label-width="80px">
            <el-form-item label="公司规模">
              <el-select v-model="userProfile.companySize" placeholder="请选择" @change="predictSalary">
                <el-option label="创业公司(1-50人)" value="1-50"></el-option>
                <el-option label="中小企业(50-500人)" value="50-500"></el-option>
                <el-option label="大型企业(500-5000人)" value="500-5000"></el-option>
                <el-option label="超大企业(5000人以上)" value="5000+"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="期望薪资">
              <el-input-number v-model="userProfile.expectedSalary" :min="5" :max="100" :step="5" controls-position="right" @change="predictSalary">
                <template #append>K/月</template>
              </el-input-number>
            </el-form-item>
            <el-form-item label="工作性质">
              <el-radio-group v-model="userProfile.workType" @change="predictSalary">
                <el-radio label="全职">全职</el-radio>
                <el-radio label="兼职">兼职</el-radio>
                <el-radio label="远程">远程</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="语言能力">
              <el-checkbox-group v-model="userProfile.languages" @change="predictSalary">
                <el-checkbox label="英语">英语</el-checkbox>
                <el-checkbox label="日语">日语</el-checkbox>
                <el-checkbox label="韩语">韩语</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 三个卡片一行展示 -->
    <el-row :gutter="20" class="cards-container">
      <!-- 薪资预测结果 - 占8列（24/3=8） -->
      <el-col :span="8" :xs="24" :sm="12" :md="8">
        <el-card class="result-card">
          <template #header>
            <div class="card-header">
              <span>薪资预测结果</span>
            </div>
          </template>
          <div class="prediction-result">
            <div class="salary-range">
              <div class="range-item">
                <span class="label">预测薪资范围</span>
                <span class="value">{{ predictionResult.minSalary }}K - {{ predictionResult.maxSalary }}K</span>
              </div>
              <div class="range-item">
                <span class="label">推荐薪资</span>
                <span class="value recommended">{{ predictionResult.recommendedSalary }}K</span>
              </div>
              <div class="range-item">
                <span class="label">市场竞争力</span>
                <el-progress
                    :percentage="predictionResult.competitiveness"
                    :color="getCompetitivenessColor(predictionResult.competitiveness)"
                    :show-text="true"
                />
              </div>
            </div>
            <div class="confidence-score">
              <span class="label">预测置信度</span>
              <el-rate
                  v-model="predictionResult.confidence"
                  :max="5"
                  disabled
                  show-score
                  text-color="#ff9900"
              />
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 影响因素分析 - 占8列 -->
      <el-col :span="8" :xs="24" :sm="12" :md="8">
        <el-card class="analysis-card">
          <template #header>
            <div class="card-header">
              <span>影响因素分析</span>
            </div>
          </template>
          <div class="factors-analysis">
            <div v-for="factor in influenceFactors" :key="factor.name" class="factor-item">
              <div class="factor-header">
                <span class="factor-name">{{ factor.name }}</span>
                <span class="factor-impact" :class="factor.impact > 0 ? 'positive' : 'negative'">
              {{ factor.impact > 0 ? '+' : '' }}{{ factor.impact }}%
            </span>
              </div>
              <el-progress
                  :percentage="Math.abs(factor.impact)"
                  :color="factor.impact > 0 ? '#67C23A' : '#F56C6C'"
                  :show-text="false"
              />
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 优化建议 - 占8列 -->
      <el-col :span="8" :xs="24" :sm="12" :md="8">
        <el-card class="suggestions-card">
          <template #header>
            <div class="card-header">
              <span>优化建议</span>
            </div>
          </template>
          <div class="suggestions-content">
            <el-alert
                v-for="suggestion in suggestions"
                :key="suggestion.id"
                :title="suggestion.title"
                :description="suggestion.description"
                :type="suggestion.type"
                :closable="false"
                style="margin-bottom: 10px;"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  TrendCharts, Refresh, Download, User
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import * as salaryPredictionAPI from '@/api/salaryPrediction'

// 响应式数据
const predicting = ref(false)
const selectedQuickProfile = ref(null)
const useAPIData = ref(false) // 默认使用本地计算，可切换到API数据

// 快速用户画像模板
const quickProfiles = ref([
  {
    id: 1,
    name: '应届AI新人',
    description: '计算机专业应届毕业生，有AI项目经验',
    experience: '0-1',
    education: '本科',
    city: '北京',
    age: '22-25',
    jobType: 'AI工程师',
    skillLevel: 3,
    industries: ['互联网'],
    companySize: '500-5000',
    expectedSalary: 18,
    workType: '全职',
    languages: ['英语']
  },
  {
    id: 2,
    name: '资深算法专家',
    description: '5年以上算法经验，硕士学历',
    experience: '5-10',
    education: '硕士',
    city: '上海',
    age: '31-35',
    jobType: '算法工程师',
    skillLevel: 5,
    industries: ['互联网', '金融'],
    companySize: '5000+',
    expectedSalary: 45,
    workType: '全职',
    languages: ['英语', '日语']
  },
  {
    id: 3,
    name: '数据科学家',
    description: '3年数据分析经验，擅长机器学习',
    experience: '3-5',
    education: '硕士',
    city: '深圳',
    age: '26-30',
    jobType: '数据科学家',
    skillLevel: 4,
    industries: ['互联网', '金融', '医疗'],
    companySize: '500-5000',
    expectedSalary: 32,
    workType: '全职',
    languages: ['英语']
  },
  {
    id: 4,
    name: '全栈开发者',
    description: '前后端全栈开发，有AI项目集成经验',
    experience: '3-5',
    education: '本科',
    city: '杭州',
    age: '26-30',
    jobType: '全栈工程师',
    skillLevel: 4,
    industries: ['互联网', '电商'],
    companySize: '50-500',
    expectedSalary: 28,
    workType: '全职',
    languages: ['英语']
  },
  {
    id: 5,
    name: '产品经理',
    description: 'AI产品经验丰富，懂技术的产品经理',
    experience: '5-10',
    education: '本科',
    city: '北京',
    age: '31-35',
    jobType: '产品经理',
    skillLevel: 4,
    industries: ['互联网', '教育'],
    companySize: '500-5000',
    expectedSalary: 35,
    workType: '全职',
    languages: ['英语']
  }
])

// 用户画像数据
const userProfile = reactive({
  experience: '3-5',
  education: '本科',
  city: '北京',
  age: '26-30',
  jobType: 'AI工程师',
  skillLevel: 4,
  industries: ['互联网'],
  companySize: '500-5000',
  expectedSalary: 25,
  workType: '全职',
  languages: ['英语']
})

// 预测结果
const predictionResult = reactive({
  minSalary: 22,
  maxSalary: 35,
  recommendedSalary: 28,
  competitiveness: 75,
  confidence: 4.2
})

// 影响因素
const influenceFactors = ref([
  { name: '工作经验', impact: 25 },
  { name: '技能水平', impact: 20 },
  { name: '学历背景', impact: 15 },
  { name: '工作城市', impact: 18 },
  { name: '行业经验', impact: 12 },
  { name: '公司规模', impact: -5 }
])

// 优化建议
const suggestions = ref([
  {
    id: 1,
    title: '技能提升建议',
    description: '建议学习深度学习框架TensorFlow和PyTorch，可提升薪资竞争力15-20%',
    type: 'success'
  },
  {
    id: 2,
    title: '经验积累',
    description: '参与更多AI项目实践，积累行业经验，有助于薪资增长',
    type: 'info'
  },
  {
    id: 3,
    title: '城市选择',
    description: '北京地区AI岗位薪资水平较高，但竞争激烈，可考虑杭州、深圳等城市',
    type: 'warning'
  }
])

// 组件挂载时初始化
onMounted(() => {
  predictSalary()
})

// 方法定义
const selectQuickProfile = (profile) => {
  selectedQuickProfile.value = profile.id

  // 更新用户画像数据
  Object.assign(userProfile, {
    experience: profile.experience,
    education: profile.education,
    city: profile.city,
    age: profile.age,
    jobType: profile.jobType,
    skillLevel: profile.skillLevel,
    industries: [...profile.industries],
    companySize: profile.companySize,
    expectedSalary: profile.expectedSalary,
    workType: profile.workType,
    languages: [...profile.languages]
  })

  // 重新预测薪资
  predictSalary()

  ElMessage.success(`已选择用户画像：${profile.name}`)
}

const formatDescription = (desc) => {
  if (!desc) return ''
  // 转义 HTML，避免使用 v-html 时注入不安全内容
  const escapeHtml = (str) => String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')

  const escaped = escapeHtml(desc)
  // 在每个中文或英文逗号后插入换行（保留逗号），并去除逗号后的多余空格
  return escaped.replace(/(，|,)\s*/g, '$1<br>')
}

const predictSalary = async () => {
  predicting.value = true

  try {
    if (useAPIData.value) {
      // 使用API数据
      await predictSalaryFromAPI()
    } else {
      // 使用本地计算
      await new Promise(resolve => setTimeout(resolve, 800))
      calculateSalaryPrediction()
    }

    // 更新影响因素和建议
    updateInfluenceFactors()
    updateSuggestions()

    ElMessage.success('薪资预测完成')
  } catch (error) {
    console.error('预测失败:', error)
    ElMessage.error('预测失败，已切换到本地计算')
    // 失败时使用本地计算
    useAPIData.value = false
    calculateSalaryPrediction()
  } finally {
    predicting.value = false
  }
}

const predictSalaryFromAPI = async () => {
  try {
    // 使用本地计算替代API
    await new Promise(resolve => setTimeout(resolve, 500))
    calculateSalaryPrediction()
  } catch (error) {
    console.error('预测失败:', error)
    throw error
  }
}

const calculateAveragePrediction = (results) => {
  if (!results || results.length === 0) {
    return null
  }

  const sum = results.reduce((acc, result) => {
    return {
      predictedSalary: acc.predictedSalary + (result.predictedSalary || 0),
      rangeMin: acc.rangeMin + (result.rangeMin || 0),
      rangeMax: acc.rangeMax + (result.rangeMax || 0),
      confidenceScore: acc.confidenceScore + (result.confidenceScore || 0),
      count: acc.count + 1
    }
  }, { predictedSalary: 0, rangeMin: 0, rangeMax: 0, confidenceScore: 0, count: 0 })

  return {
    predictedSalary: sum.predictedSalary / sum.count,
    rangeMin: sum.rangeMin / sum.count,
    rangeMax: sum.rangeMax / sum.count,
    confidenceScore: sum.confidenceScore / sum.count
  }
}

const handleDataSourceChange = (value) => {
  if (value) {
    ElMessage.info('已切换到API数据源')
  } else {
    ElMessage.info('已切换到本地计算')
  }
  predictSalary()
}

const calculateSalaryPrediction = () => {
  // 职位基础薪资映射
  const jobBaseSalary = {
    'AI工程师': 10,
    '算法工程师': 13,
    '机器学习工程师': 11,
    '数据科学家': 15,
    '深度学习工程师': 17,
    'Python开发工程师': 7,
    '前端工程师': 5,
    '后端工程师': 8,
    '全栈工程师': 10,
    '产品经理': 13,
    '数据分析师':3
  }


  let baseSalary = jobBaseSalary[userProfile.jobType] || 20

  // 根据经验调整
  const expMultiplier = {
    '0-1': 0.7,
    '1-3': 1.0,
    '3-5': 1.1,
    '5-10': 1.2,
    '10+': 1.3
  }
  baseSalary *= expMultiplier[userProfile.experience] || 1.0

  // 根据学历调整
  const eduMultiplier = {
    '专科': 0.85,
    '本科': 1.0,
    '硕士': 1.1,
    '博士': 1.2
  }
  baseSalary *= eduMultiplier[userProfile.education] || 1.0

  // 根据城市调整
  const cityMultiplier = {
    '北京': 1.3,
    '上海': 1.25,
    '深圳': 1.2,
    '杭州': 1.1,
    '广州': 1.05,
    '成都': 0.9,
    '南京': 0.95,
    '武汉': 0.85,
    '西安': 0.8
  }
  baseSalary *= cityMultiplier[userProfile.city] || 1.0

  // 根据年龄调整
  const ageMultiplier = {
    '22-25': 0.9,
    '26-30': 1.0,
    '31-35': 1.1,
    '36-40': 1.05,
    '40+': 1.0
  }
  baseSalary *= ageMultiplier[userProfile.age] || 1.0

  // 根据技能等级调整
  baseSalary *= (0.7 + userProfile.skillLevel * 0.15)

  // 根据公司规模调整
  const companySizeMultiplier = {
    '1-50': 0.9,
    '50-500': 1.0,
    '500-5000': 1.1,
    '5000+': 1.2
  }
  baseSalary *= companySizeMultiplier[userProfile.companySize] || 1.0

  // 根据行业经验调整
  const industryBonus = userProfile.industries.length * 0.05
  baseSalary *= (1 + industryBonus)

  // 根据语言能力调整
  const languageBonus = userProfile.languages.length * 0.03
  baseSalary *= (1 + languageBonus)

  // 根据工作性质调整
  const workTypeMultiplier = {
    '全职': 1.0,
    '兼职': 0.6,
    '远程': 0.95
  }
  baseSalary *= workTypeMultiplier[userProfile.workType] || 1.0

  // 更新预测结果
  predictionResult.minSalary = Math.round(baseSalary * 0.75)
  predictionResult.maxSalary = Math.round(baseSalary * 1.5)
  predictionResult.recommendedSalary = Math.round(baseSalary)
  predictionResult.competitiveness = Math.min(95, Math.round(40 + baseSalary * 1.2))
  predictionResult.confidence = Math.min(5, 2.5 + userProfile.skillLevel * 0.4 + userProfile.industries.length * 0.2)
}

const updateInfluenceFactors = () => {
  const factors = []

  // 工作经验影响
  const expImpact = {
    '0-1': -20,
    '1-3': 0,
    '3-5': 25,
    '5-10': 40,
    '10+': 50
  }
  factors.push({ name: '工作经验', impact: expImpact[userProfile.experience] || 0 })

  // 学历影响
  const eduImpact = {
    '专科': -10,
    '本科': 0,
    '硕士': 20,
    '博士': 35
  }
  factors.push({ name: '学历背景', impact: eduImpact[userProfile.education] || 0 })

  // 城市影响
  const cityImpact = {
    '北京': 20,
    '上海': 15,
    '深圳': 10,
    '杭州': 8,
    '广州': 5,
    '成都': -8,
    '南京': -3,
    '武汉': -12,
    '西安': -15
  }
  factors.push({ name: '工作城市', impact: cityImpact[userProfile.city] || 0 })

  // 技能等级影响
  const skillImpact = (userProfile.skillLevel - 3) * 15
  factors.push({ name: '技能水平', impact: skillImpact })

  // 行业经验影响
  const industryImpact = userProfile.industries.length * 8
  factors.push({ name: '行业经验', impact: industryImpact })

  // 公司规模影响
  const companySizeImpact = {
    '1-50': -8,
    '50-500': 0,
    '500-5000': 8,
    '5000+': 15
  }
  factors.push({ name: '公司规模', impact: companySizeImpact[userProfile.companySize] || 0 })

  // 语言能力影响
  const languageImpact = userProfile.languages.length * 5
  factors.push({ name: '语言能力', impact: languageImpact })

  influenceFactors.value = factors
}

const updateSuggestions = () => {
  const newSuggestions = []

  // 基于技能等级的建议
  if (userProfile.skillLevel < 4) {
    newSuggestions.push({
      id: 1,
      title: '技能提升建议',
      description: `当前技能等级为${userProfile.skillLevel}星，建议通过在线课程、项目实践等方式提升技能水平，可提升薪资15-25%`,
      type: 'warning'
    })
  }

  // 基于经验的建议
  if (userProfile.experience === '0-1') {
    newSuggestions.push({
      id: 2,
      title: '经验积累',
      description: '作为应届生，建议多参与实际项目，积累工作经验。考虑从实习或初级岗位开始',
      type: 'info'
    })
  } else if (userProfile.experience === '1-3') {
    newSuggestions.push({
      id: 2,
      title: '职业发展',
      description: '1-3年经验是快速成长期，建议专注于某个技术领域深入发展，建立专业优势',
      type: 'success'
    })
  }

  // 基于学历的建议
  if (userProfile.education === '专科' || userProfile.education === '本科') {
    newSuggestions.push({
      id: 3,
      title: '学历提升',
      description: '考虑通过在职研究生、技术认证等方式提升学历背景，有助于职业发展',
      type: 'info'
    })
  }

  // 基于城市的建议
  const lowSalaryCities = ['成都', '武汉', '西安']
  if (lowSalaryCities.includes(userProfile.city)) {
    newSuggestions.push({
      id: 4,
      title: '城市选择',
      description: `${userProfile.city}地区薪资水平相对较低，可考虑北京、上海、深圳等一线城市获得更高薪资`,
      type: 'warning'
    })
  }

  // 基于行业经验的建议
  if (userProfile.industries.length < 2) {
    newSuggestions.push({
      id: 5,
      title: '行业拓展',
      description: '建议拓展更多行业经验，如金融、医疗、教育等领域，增加就业机会和薪资竞争力',
      type: 'success'
    })
  }

  // 基于语言能力的建议
  if (userProfile.languages.length === 0) {
    newSuggestions.push({
      id: 6,
      title: '语言能力',
      description: '掌握英语等外语能力可以显著提升薪资水平，建议加强语言学习',
      type: 'info'
    })
  }

  suggestions.value = newSuggestions
}

const exportReport = () => {
  ElMessage.success('报告导出功能开发中...')
}

const getCompetitivenessColor = (value) => {
  if (value >= 80) return '#67C23A'
  if (value >= 60) return '#E6A23C'
  return '#F56C6C'
}
</script>

<style scoped>
.salary-prediction {
  padding: 24px;
  background-color: #f9fbfd; /* 统一浅蓝背景 */
  min-height: 100vh;
  font-family: 'Inter', 'Helvetica Neue', Arial, sans-serif;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 18px;
  background: #f0f7ff; /* 与其他头部卡片一致的浅蓝纯色 */
  border-radius: 12px;
  border: 1px solid #e8f3ff;
  box-shadow: 0 2px 10px rgba(64, 158, 255, 0.06);
}

.title-section h1 {
  margin: 0;
  color: #1d2129; /* 更深的标题色 */
  display: flex;
  align-items: center;
  gap: 10px;
}

.subtitle {
  margin: 5px 0 0 0;
  color: #4e5969; /* 更明显的副标题色 */
  font-size: 14px;
}

.quick-profiles {
  margin-bottom: 30px;
  padding: 20px;
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e8f3ff;
  box-shadow: 0 2px 8px rgba(64,158,255,0.06);
}

.quick-profiles h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
}

.profile-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.quick-profile-card {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid #c9ddf4;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.06);

}

.quick-profile-card:hover {
  border-color: #409eff;
  background: #ecf5ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.quick-profile-card.active {
  border-color: #409eff;
  background: #ecf5ff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.profile-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #f0f7ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: white;
  font-size: 20px;
}

.profile-info h4 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.profile-info p {
  margin: 0 0 10px 0;
  color: #7f8c8d;
  font-size: 14px;
}

.profile-tags {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.input-section,
.result-section,
.analysis-section,
.suggestions-section {
  margin-bottom: 20px;
}

.profile-card,
.result-card,
.analysis-card,
.suggestions-card {
  height: 100%;
  border-radius: 12px;
  border: 1px solid #e8f3ff;
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(64,158,255,0.06);
  transition: all 0.25s ease;
  overflow: hidden;
}

.profile-card:hover,
.result-card:hover,
.analysis-card:hover,
.suggestions-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(64,158,255,0.12);
  border-color: #d1eaff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #f0f7ff;
  border-bottom: 1px solid #e8f3ff;
  font-weight: 600;
  color: #1d2129;
}

.prediction-result {
  padding: 20px 0;
}

.salary-range {
  margin-bottom: 20px;
}

.range-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
  padding: 10px 12px;
  background: #f8fbff;
  border-radius: 8px;
}

.range-item .label {
  font-weight: 500;
  color: #4e5969;
}

.confidence-score {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f0f7ff; /* 与卡片头色一致 */
  border-radius: 8px;
}

.range-item .value {
  font-weight: bold;
  color: #2c3e50;
  font-size: 16px;
}

.range-item .value.recommended {
  color: #67C23A;
  font-size: 18px;
}

.confidence-score {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f0f9ff;
  border-radius: 6px;
}

.factors-analysis {
  padding: 10px 0;
}

.factor-item {
  margin-bottom: 15px;
}

.factor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.factor-name {
  font-weight: 500;
  color: #2c3e50;
}

.factor-impact {
  font-weight: bold;
  font-size: 14px;
}

.factor-impact.positive {
  color: #67C23A;
}

.factor-impact.negative {
  color: #F56C6C;
}

.suggestions-content {
  padding: 10px 0;
}

.cards-container {
  margin-bottom: 20px;
}

/* 卡片统一样式，保证高度一致 */
.result-card, .analysis-card, .suggestions-card {
  height: 100%; /* 关键：让卡片占满列的高度 */
  display: flex;
  flex-direction: column;
}
/* 卡片内容区域自适应高度 */
.prediction-result, .factors-analysis, .suggestions-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 响应式适配：小屏幕下优化显示 */
@media (max-width: 992px) {
  .chart-container {
    min-height: 180px;
  }
}

@media (max-width: 768px) {
  .chart-container {
    min-height: 150px;
  }
}

/* 将基本信息、技能信息、期望信息等卡片标题居中显示 */
.input-section .card-header,
.result-card .card-header,
.analysis-card .card-header,
.suggestions-card .card-header {
  justify-content: center; /* 居中 */
  text-align: center;
  font-size: 18px; /* 放大标题 */
  font-weight: 700;
  padding: 8px 8px;
}

/* 原有样式保留 */
.card-header {
  font-weight: 600;
  color: #1f2937;
}

.range-item {
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.range-item .label {
  color: #666;
  font-size: 14px;
}

.range-item .value {
  font-weight: 600;
  font-size: 16px;
}

.range-item .recommended {
  color: #409eff;
}

.confidence-score {
  margin-top: 16px;
}

.factor-item {
  margin-bottom: 12px;
}

.factor-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.factor-name {
  font-size: 14px;
  color: #666;
}

.factor-impact.positive {
  color: #409eff; /* 正向影响改为主蓝 */
  font-weight: 600;
}

.factor-impact.negative {
  color: #F56C6C;
  font-weight: 600;
}

/* Element Plus 组件统一风格覆盖（仅当前组件作用域） */
:deep(.el-card) {
  border: 1px solid #e8f3ff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(64, 158, 255, 0.06);
  background-color: #ffffff;
  transition: all 0.3s ease;
  overflow: hidden;
}

:deep(.el-card:hover) {
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.12);
  border-color: #d1eaff;
  transform: translateY(-3px);
}

:deep(.el-card__header) {
  background-color: #f0f7ff;
  border-bottom: 1px solid #e8f3ff;
  padding: 12px 16px;
}

:deep(.el-button--primary) {
  background-color: #409eff;
  border-color: #409eff;
}

:deep(.el-button--primary:hover) {
  background-color: #3390e9;
  border-color: #3390e9;
}

:deep(.el-progress__stroke) {
  background-color: #409eff !important;
}

:deep(.el-tag--success) {
  background-color: #e8f3ff;
  color: #409eff;
  border-color: #d1eaff;
}
</style>