import axios from 'axios'

// 基础URL配置
const BASE_ANALYSIS_URL = 'http://localhost:8088/api/analysis'
const BASE_RECRUITMENT_URL = 'http://localhost:8088/api/recruitments'

/**
 * 通用的API GET请求函数
 * @param {string} url - 完整的API URL
 * @param {string} apiName - API名称，用于错误信息
 * @returns {Promise} - 返回请求的响应数据
 */
const fetchGetData = async (url, apiName) => {
  try {
    const response = await axios.get(url)
    console.log(`${apiName} 数据:`, response.data)
    return response
  } catch (error) {
    console.error(`${apiName} API 请求失败:`, error)
    throw error // 继续抛出错误，让调用方处理
  }
}
// 原有的分析API请求函数
export const getSalaryData = () =>
  fetchGetData(`${BASE_ANALYSIS_URL}/city-salary`, '城市薪资')

export const getCompanyData = () =>
  fetchGetData(`${BASE_ANALYSIS_URL}/company-scale`, '公司规模')

export const getFinanceData = () =>
  fetchGetData(`${BASE_ANALYSIS_URL}/financing-status`, '融资状态')

export const getSkillData = () =>
  fetchGetData(`${BASE_ANALYSIS_URL}/skill-word-cloud`, '技能词云')

// // 新增的招聘信息CRUD API请求函数
// export const getRecruitmentById = (id) =>
//     fetchGetData(`${BASE_RECRUITMENT_URL}/${id}`, `ID为${id}的招聘信息`)
//
// export const getAllRecruitments = () =>
//     fetchGetData(`${BASE_RECRUITMENT_URL}`, '所有招聘信息')
//
// export const createRecruitment = (recruitmentData) =>
//     fetchPostData(`${BASE_RECRUITMENT_URL}`, recruitmentData, '创建招聘信息')
//
// export const updateRecruitment = (id, recruitmentData) =>
//     fetchPutData(`${BASE_RECRUITMENT_URL}/${id}`, recruitmentData, `更新ID为${id}的招聘信息`)
//
// export const deleteRecruitment = (id) =>
//     fetchDeleteData(`${BASE_RECRUITMENT_URL}/${id}`, `删除ID为${id}的招聘信息`)

// 新增的招聘分析API请求函数
export const getAvgSalaryByIndustryInTopCities = () =>
  fetchGetData(`${BASE_RECRUITMENT_URL}/analysis/avg-salary-top-cities`, '顶级城市各行业平均薪资')

export const getAvgSalaryByEducationAndExperience = () =>
  fetchGetData(`${BASE_RECRUITMENT_URL}/analysis/avg-salary-edu-exp`, '按学历和经验的平均薪资')

export const getAvgSalaryByEduAndExpInFinancingStages = () =>
  fetchGetData(`${BASE_RECRUITMENT_URL}/analysis/avg-salary-financing`, '各融资阶段按学历和经验的平均薪资')

export const getTop8IndustriesByAvgSalary = () =>
  fetchGetData(`${BASE_RECRUITMENT_URL}/analysis/top-8-industries-by-salary`, '薪资最高的前8个行业')

export const getCountDistinctIndustriesByCity = () =>
  fetchGetData(`${BASE_RECRUITMENT_URL}/analysis/industry-count-by-city`, '各城市行业数量')

export const getProvinceJobStats = () =>
  fetchGetData(`${BASE_RECRUITMENT_URL}/analysis/job-stats-by-province`, '各省就业统计')


const apiClient = axios.create({
  baseURL: 'http://localhost:8088/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// 实时推荐API
export const realtimeRecommendationAPI = {
  // 获取实时推荐
  getRealtimeRecommendations(userId, context = {}, topN = 10) {
    return apiClient.post('/realtime-recommendations/get', context, {
      params: { userId, topN }
    })
  },

  // 获取即时推荐
  getInstantRecommendations(request) {
    return apiClient.post('/realtime-recommendations/instant', request)
  },

  // 获取混合推荐
  getHybridRecommendations(userId, topN = 10) {
    return apiClient.get(`/realtime-recommendations/hybrid/${userId}`, {
      params: { topN }
    })
  },

  // 记录推荐点击
  recordRecommendationClick(userId, jobId, algorithmType, position = 1) {
    return apiClient.post('/realtime-recommendations/click', null, {
      params: { userId, jobId, algorithmType, position }
    })
  },

  // 记录推荐转化
  recordRecommendationConversion(userId, jobId, algorithmType, conversionType) {
    return apiClient.post('/realtime-recommendations/conversion', null, {
      params: { userId, jobId, algorithmType, conversionType }
    })
  }
}

// 用户行为分析API
export const userBehaviorAPI = {
  // 分析用户行为模式
  analyzeUserBehavior(userId, timeRange = 30) {
    return apiClient.get(`/user-behavior-analysis/analyze/${userId}`, {
      params: { timeRange }
    })
  },

  // 获取用户兴趣标签
  getInterestTags(userId) {
    return apiClient.get(`/user-behavior-analysis/interest-tags/${userId}`)
  },

  // 获取用户活动统计
  getActivityStats(userId, timeRange = 30) {
    return apiClient.get(`/user-behavior-analysis/activity-stats/${userId}`, {
      params: { timeRange }
    })
  },

  // 分析用户搜索行为
  analyzeSearchBehavior(userId, timeRange = 30) {
    return apiClient.get(`/user-behavior-analysis/search-behavior/${userId}`, {
      params: { timeRange }
    })
  },

  // 追踪用户行为
  trackUserBehavior(userId, action, jobId = null, metadata = {}) {
    return apiClient.post('/user-behavior-analysis/track', metadata, {
      params: { userId, action, jobId }
    })
  }
}

// 职位匹配度评分API
export const jobMatchingAPI = {
  // 计算单个职位匹配度
  calculateJobMatch(jobId, userProfile) {
    return apiClient.post(`/job-matching/calculate/${jobId}`, userProfile)
  },

  // 批量计算职位匹配度
  calculateBatchJobMatch(jobIds, userProfile) {
    return apiClient.post('/job-matching/calculate-batch', {
      jobIds,
      userProfile
    })
  },

  // 计算技能匹配度
  calculateSkillMatch(jobSkills, userSkills) {
    return apiClient.post('/job-matching/skill-match', {
      jobSkills,
      userSkills
    })
  },

  // 根据用户ID获取推荐结果
  getRecommendationsByUserId(userId) {
    return apiClient.get(`/job-recommendations/user/${userId}`)
  },

  // 根据用户ID和职位ID获取推荐详情
  getRecommendationByUserAndJob(userId, jobId) {
    return apiClient.get(`/job-matching/calculate/${userId}/${jobId}`)
  }
}

// 薪资预测API
export const salaryPredictionAPI = {
  // 触发薪资预测计算
  calculatePrediction(dt) {
    return apiClient.post('/salary-prediction/calculate', null, {
      params: { dt }
    })
  },

  // 查询所有预测结果
  getAllResults() {
    return apiClient.get('/salary-prediction/results')
  },

  // 按日期查询预测结果
  getResultsByDate(dt) {
    return apiClient.get(`/salary-prediction/results/date/${dt}`)
  },

  // 按职位名称关键词查询
  getResultsByPosition(keyword) {
    return apiClient.get('/salary-prediction/results/position', {
      params: { keyword }
    })
  },

  // 根据用户画像预测薪资
  predictByUserProfile(experience, education, city, positionName) {
    return apiClient.get('/salary-prediction/predict', {
      params: {
        experience,
        education,
        city,
        positionName
      }
    })
  },

  // 查询预测薪资最高的前N个职位
  getTopNResults(topN = 10) {
    return apiClient.get('/salary-prediction/top', {
      params: { topN }
    })
  }
}

// 智能推荐系统API
export const intelligentRecommendationAPI = {
  // 获取推荐配置
  getRecommendationConfig(userId) {
    return apiClient.get(`/intelligent-recommendation/config/${userId}`)
  },

  // 更新推荐配置
  updateRecommendationConfig(userId, config) {
    return apiClient.put(`/intelligent-recommendation/config/${userId}`, config)
  },

  // 获取多算法推荐结果
  getMultiAlgorithmRecommendations(userId, algorithms, topN = 10) {
    return apiClient.post('/intelligent-recommendation/multi-algorithm', {
      userId,
      algorithms,
      topN
    })
  },

  // 获取个性化推荐
  getPersonalizedRecommendations(userId, preferences, topN = 10) {
    return apiClient.post('/intelligent-recommendation/personalized', {
      userId,
      preferences,
      topN
    })
  },

  // 获取推荐效果统计
  getRecommendationStats(userId, timeRange = '7d') {
    return apiClient.get(`/intelligent-recommendation/stats/${userId}`, {
      params: { timeRange }
    })
  },

  // 比较不同算法效果
  compareAlgorithmPerformance(userId, algorithms, timeRange = '7d') {
    return apiClient.post('/intelligent-recommendation/compare-algorithms', {
      userId,
      algorithms,
      timeRange
    })
  }
}

// A/B测试API
export const abTestAPI = {
  // 获取用户实验分组
  getUserAssignment(experimentId, userId) {
    return apiClient.get(`/ab-test/assignment/${experimentId}/${userId}`)
  },

  // 记录实验事件
  recordEvent(experimentId, userId, eventType, eventData = {}) {
    return apiClient.post('/ab-test/event', {
      experimentId,
      userId,
      eventType,
      eventData
    })
  },

  // 获取UI变体
  getUIVariant(userId, componentName) {
    return apiClient.get(`/ab-test/ui-variant/${userId}`, {
      params: { componentName }
    })
  }
}

// 技能关联规则分析API
export const skillAssociationRulesAPI = {
  // 获取所有技能关联规则
  getAllRules() {
    return apiClient.get('/skill-association-rules')
  },

  // 根据规则类型获取技能关联规则
  getRulesByType(ruleType) {
    return apiClient.get(`/skill-association-rules/type/${ruleType}`)
  },

  // 根据日期分区获取技能关联规则
  getRulesByDate(dt) {
    return apiClient.get(`/skill-association-rules/date/${dt}`)
  },

  // 获取置信度最高的前N条技能关联规则
  getTopNByConfidence(topN) {
    return apiClient.get(`/skill-association-rules/top-confidence/${topN}`)
  },

  // 获取提升度最高的前N条技能关联规则
  getTopNByLift(topN) {
    return apiClient.get(`/skill-association-rules/top-lift/${topN}`)
  },

  // 根据规则类型和日期获取技能关联规则
  getRulesByTypeAndDate(ruleType, dt) {
    return apiClient.get(`/skill-association-rules/type/${ruleType}/date/${dt}`)
  },

  // 查询包含指定技能作为前项的关联规则
  getRulesByAntecedent(skill) {
    return apiClient.get(`/skill-association-rules/antecedent/${skill}`)
  },

  // 查询包含指定技能作为后项的关联规则
  getRulesByConsequent(skill) {
    return apiClient.get(`/skill-association-rules/consequent/${skill}`)
  },

  // 根据职位名称查询该职位需要的技能列表
  getSkillsByPosition(positionName) {
    return apiClient.get(`/skill-association-rules/position/${positionName}`)
  }
}

export default {
  searchJobs(filters, page = 1, size = 10) {
    // 构建查询参数
    const params = {
      page,
      size,
      ...filters
    }

    // 清理空值参数
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })

    // 自定义参数序列化器，避免数组参数使用方括号 [] 导致 Tomcat 400 错误
    const paramsSerializer = (params) => {
      const parts = []
      Object.keys(params).forEach(key => {
        const value = params[key]
        if (Array.isArray(value)) {
          // 数组参数：cities=北京&cities=上海
          value.forEach(v => {
            parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(v)}`)
          })
        } else {
          parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
        }
      })
      return parts.join('&')
    }

    return apiClient.get('/jobs/search', { params, paramsSerializer })
  },
  collectJob(userId, jobId) {
    return apiClient.post('/jobs/collect', { userId, jobId })
  },
  cancelCollectJob(userId, jobId) {
    return apiClient.post('/jobs/cancel-collect', { userId, jobId })
  },
  getCollectedJobs(userId) {
    return apiClient.get(`/jobs/collections/${userId}`)
  },

  // 用户相关API
  getCurrentUser() {
    // 模拟获取当前用户信息
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          data: {
            id: 1,
            username: '用户',
            email: 'user@example.com',
            educationLevel: '本科',
            education_level: '本科'
          }
        })
      }, 500)
    })
  },

  updateUser(userData) {
    // 模拟更新用户信息
    return new Promise((resolve) => {
      setTimeout(() => {
        console.log('更新用户信息:', userData)
        resolve({
          data: {
            success: true,
            message: '用户信息更新成功'
          }
        })
      }, 500)
    })
  }
}