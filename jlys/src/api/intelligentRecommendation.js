import request from '@/utils/request'

/**
 * 获取推荐配置
 */
export function getRecommendationConfig(userId) {
  return request({
    url: `/intelligent-recommendation/config/${userId}`,
    method: 'get'
  })
}

/**
 * 更新推荐配置
 */
export function updateRecommendationConfig(userId, config) {
  return request({
    url: `/intelligent-recommendation/config/${userId}`,
    method: 'put',
    data: config
  })
}

/**
 * 获取多算法推荐结果
 */
export function getMultiAlgorithmRecommendations(userId, algorithms, topN = 10) {
  return request({
    url: '/intelligent-recommendation/multi-algorithm',
    method: 'post',
    data: {
      userId,
      algorithms,
      topN
    }
  })
}

/**
 * 获取个性化推荐
 */
export function getPersonalizedRecommendations(userId, preferences, topN = 10) {
  return request({
    url: '/intelligent-recommendation/personalized',
    method: 'post',
    data: {
      userId,
      preferences,
      topN
    }
  })
}

/**
 * 获取推荐效果统计
 */
export function getRecommendationStats(userId, timeRange = '7d') {
  return request({
    url: `/intelligent-recommendation/stats/${userId}`,
    method: 'get',
    params: { timeRange }
  })
}

/**
 * 比较不同算法效果
 */
export function compareAlgorithmPerformance(userId, algorithms, timeRange = '7d') {
  return request({
    url: '/intelligent-recommendation/compare-algorithms',
    method: 'post',
    data: {
      userId,
      algorithms,
      timeRange
    }
  })
}
