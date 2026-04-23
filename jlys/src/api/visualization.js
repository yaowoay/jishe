import axios from 'axios'

const apiClient = axios.create({
  baseURL: 'http://localhost:8089/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// 职位相关API
export const jobAPI = {
  // 搜索职位
  searchJobs(filters = {}, page = 1, size = 12) {
    const params = {
      page,
      size,
      ...filters
    }
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })
    return apiClient.get('/jobs/search', { params })
  },

  // 获取职位详情
  getJobDetail(jobId) {
    return apiClient.get(`/jobs/${jobId}`)
  },

  // 收藏职位
  collectJob(userId, jobId) {
    return apiClient.post('/jobs/collect', { userId, jobId })
  },

  // 取消收藏
  cancelCollectJob(userId, jobId) {
    return apiClient.post('/jobs/cancel-collect', { userId, jobId })
  },

  // 获取收藏列表
  getCollectedJobs(userId) {
    return apiClient.get(`/jobs/collections/${userId}`)
  }
}

// 数据分析API
export const analysisAPI = {
  // 获取城市薪资数据
  getCitySalaryData() {
    return apiClient.get('/analysis/city-salary')
  },

  // 获取公司规模数据
  getCompanyScaleData() {
    return apiClient.get('/analysis/company-scale')
  },

  // 获取融资状态数据
  getFinancingData() {
    return apiClient.get('/analysis/financing-status')
  },

  // 获取技能词云数据
  getSkillWordCloud() {
    return apiClient.get('/analysis/skill-word-cloud')
  },

  // 获取行业薪资数据
  getIndustrySalaryData() {
    return apiClient.get('/recruitments/analysis/avg-salary-top-cities')
  },

  // 获取学历经验薪资数据
  getEduExpSalaryData() {
    return apiClient.get('/recruitments/analysis/avg-salary-edu-exp')
  }
}

// 用户相关API
export const userAPI = {
  // 获取当前用户信息
  getCurrentUser() {
    return apiClient.get('/user/current')
  },

  // 更新用户信息
  updateUserInfo(userData) {
    return apiClient.put('/user/update', userData)
  },

  // 获取用户个人中心信息
  getUserProfile(userId) {
    return apiClient.get(`/user/${userId}/profile`)
  },

  // 更新用户个人中心信息
  updateUserProfile(userId, profileData) {
    return apiClient.put(`/user/${userId}/profile`, profileData)
  }
}

export default {
  jobAPI,
  analysisAPI,
  userAPI
}
