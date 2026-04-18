import request from '@/utils/request'

/**
 * 获取实时推荐
 */
export function getRealtimeRecommendations(userId, context = {}, topN = 10) {
  return request({
    url: '/realtime-recommendations/get',
    method: 'post',
    data: context,
    params: { userId, topN }
  })
}

/**
 * 获取即时推荐
 */
export function getInstantRecommendations(request) {
  return request({
    url: '/realtime-recommendations/instant',
    method: 'post',
    data: request
  })
}

/**
 * 获取混合推荐
 */
export function getHybridRecommendations(userId, topN = 10) {
  return request({
    url: `/realtime-recommendations/hybrid/${userId}`,
    method: 'get',
    params: { topN }
  })
}

/**
 * 记录推荐点击
 */
export function recordRecommendationClick(userId, jobId, algorithmType, position = 1) {
  return request({
    url: '/realtime-recommendations/click',
    method: 'post',
    params: { userId, jobId, algorithmType, position }
  })
}

/**
 * 记录推荐转化
 */
export function recordRecommendationConversion(userId, jobId, algorithmType, conversionType) {
  return request({
    url: '/realtime-recommendations/conversion',
    method: 'post',
    params: { userId, jobId, algorithmType, conversionType }
  })
}
