import request from '@/utils/request'

/**
 * 职位搜索与筛选
 */
export function searchJobs(params, page = 1, size = 10) {
  return request({
    url: '/api/jobs/search',
    method: 'get',
    params: {
      ...params,
      page,
      size
    }
  })
}

/**
 * 根据ID获取职位详情
 */
export function getJobDetail(jobId) {
  return request({
    url: `/api/jobs/${jobId}`,
    method: 'get'
  })
}

/**
 * 获取所有职位
 */
export function getAllJobs() {
  return request({
    url: '/api/jobs/list',
    method: 'get'
  })
}

/**
 * 获取用户的所有推荐职位
 */
export function getRecommendationsForUser(userId) {
  return request({
    url: `/api/recommendations/user/${userId}`,
    method: 'get'
  })
}

/**
 * 分页获取用户推荐职位
 */
export function getRecommendationsByPage(userId, pageNum = 1, pageSize = 10) {
  return request({
    url: `/api/recommendations/user/${userId}/page`,
    method: 'get',
    params: { pageNum, pageSize }
  })
}

/**
 * 按匹配度排序查询推荐
 */
export function getRecommendationsSorted(userId) {
  return request({
    url: `/api/recommendations/user/${userId}/sorted`,
    method: 'get'
  })
}

/**
 * 添加推荐记录
 */
export function addRecommendation(recommendation) {
  return request({
    url: '/api/recommendations/add',
    method: 'post',
    data: recommendation
  })
}

/**
 * 更新推荐记录
 */
export function updateRecommendation(recommendation) {
  return request({
    url: '/api/recommendations/update',
    method: 'put',
    data: recommendation
  })
}

/**
 * 删除推荐记录
 */
export function deleteRecommendation(recId) {
  return request({
    url: `/api/recommendations/${recId}`,
    method: 'delete'
  })
}

/**
 * 获取推荐总数
 */
export function getRecommendationCount(userId) {
  return request({
    url: `/api/recommendations/user/${userId}/count`,
    method: 'get'
  })
}

/**
 * 收藏职位
 */
export function collectJob(userId, jobId) {
  return request({
    url: '/api/user-job-collection/collect',
    method: 'post',
    data: { userId, jobId }
  })
}

/**
 * 取消收藏职位
 */
export function cancelCollectJob(userId, jobId) {
  return request({
    url: '/api/user-job-collection/cancel-collect',
    method: 'post',
    data: { userId, jobId }
  })
}

/**
 * 获取用户收藏列表
 */
export function getCollectedJobs(userId) {
  return request({
    url: `/api/user-job-collection/collections/${userId}`,
    method: 'get'
  })
}
