import request from '@/utils/request'

/**
 * 记录用户行为
 */
export function recordUserAction(action) {
  return request({
    url: '/user-behavior-analysis/record',
    method: 'post',
    data: action
  })
}

/**
 * 批量记录用户行为
 */
export function recordUserActionBatch(actions) {
  return request({
    url: '/user-behavior-analysis/batch-record',
    method: 'post',
    data: actions
  })
}

/**
 * 获取用户的行为记录
 */
export function getUserActions(userId) {
  return request({
    url: `/user-behavior-analysis/user/${userId}`,
    method: 'get'
  })
}

/**
 * 获取用户在时间范围内的行为记录
 */
export function getUserActionsByTimeRange(userId, startTime, endTime) {
  return request({
    url: `/user-behavior-analysis/user/${userId}/time-range`,
    method: 'get',
    params: { startTime, endTime }
  })
}

/**
 * 获取用户的行为统计
 */
export function getUserBehaviorStats(userId) {
  return request({
    url: `/user-behavior-analysis/user/${userId}/stats`,
    method: 'get'
  })
}

/**
 * 获取用户的行为类型统计
 */
export function getUserActionTypeStats(userId) {
  return request({
    url: `/user-behavior-analysis/user/${userId}/action-type-stats`,
    method: 'get'
  })
}

/**
 * 分析用户行为模式
 */
export function analyzeUserBehaviorPattern(userId, timeRangeDays = 30) {
  return request({
    url: `/user-behavior-analysis/user/${userId}/pattern`,
    method: 'get',
    params: { timeRangeDays }
  })
}

/**
 * 获取用户活跃度统计
 */
export function getUserActivityStats(userId) {
  return request({
    url: `/user-behavior-analysis/user/${userId}/activity-stats`,
    method: 'get'
  })
}

/**
 * 获取用户兴趣标签
 */
export function getUserInterestTags(userId) {
  return request({
    url: `/user-behavior-analysis/user/${userId}/interest-tags`,
    method: 'get'
  })
}

/**
 * 删除用户的行为记录
 */
export function deleteUserActions(userId) {
  return request({
    url: `/user-behavior-analysis/user/${userId}`,
    method: 'delete'
  })
}

/**
 * 获取用户行为总数
 */
export function getUserActionCount(userId) {
  return request({
    url: `/user-behavior-analysis/user/${userId}/count`,
    method: 'get'
  })
}
