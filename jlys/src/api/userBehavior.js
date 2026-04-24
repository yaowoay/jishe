import request from '@/utils/request'

/**
 * 记录用户行为（新增：用于日志采集）
 * @param {Object} behaviorData 行为数据
 * @param {String} behaviorData.actionType 行为类型：VIEW/APPLY/COLLECT
 * @param {Number} behaviorData.jobId 职位ID
 * @param {Number} behaviorData.userId 用户ID
 * @param {Number} behaviorData.score 行为分数（可选）
 */
export function logUserBehavior(behaviorData) {
  const scoreMap = {
    'VIEW': 1,
    'APPLY': 2,
    'COLLECT': 3
  }

  const data = {
    ...behaviorData,
    score: behaviorData.score || scoreMap[behaviorData.actionType] || 1,
    eventTime: new Date().toISOString().replace('T', ' ').substring(0, 19),
    dt: new Date().toISOString().substring(0, 10).replace(/-/g, '')
  }

  return request({
    url: '/behavior/log',
    method: 'post',
    data
  })
}

/**
 * 记录职位浏览行为
 */
export function logJobView(userId, jobId) {
  return logUserBehavior({
    userId,
    jobId,
    actionType: 'VIEW'
  })
}

/**
 * 记录职位收藏行为
 */
export function logJobCollect(userId, jobId) {
  return logUserBehavior({
    userId,
    jobId,
    actionType: 'COLLECT'
  })
}

/**
 * 记录简历投递行为
 */
export function logJobApply(userId, jobId) {
  return logUserBehavior({
    userId,
    jobId,
    actionType: 'APPLY'
  })
}

/**
 * 记录用户行为（旧接口，保留兼容性）
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
