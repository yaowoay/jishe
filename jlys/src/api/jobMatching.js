import request from '@/utils/request'

/**
 * 计算单个职位匹配度
 */
export function calculateJobMatch(jobId, userProfile) {
  return request({
    url: `/job-matching/calculate/${jobId}`,
    method: 'post',
    data: userProfile
  })
}

/**
 * 批量计算职位匹配度
 */
export function calculateBatchJobMatch(jobIds, userProfile) {
  return request({
    url: '/job-matching/calculate-batch',
    method: 'post',
    data: {
      jobIds,
      userProfile
    }
  })
}

/**
 * 计算技能匹配度
 */
export function calculateSkillMatch(jobSkills, userSkills) {
  return request({
    url: '/job-matching/skill-match',
    method: 'post',
    data: {
      jobSkills,
      userSkills
    }
  })
}

/**
 * 根据用户ID获取推荐结果
 */
export function getRecommendationsByUserId(userId) {
  return request({
    url: `/job-recommendations/user/${userId}`,
    method: 'get'
  })
}

/**
 * 根据用户ID和职位ID获取推荐详情
 */
export function getRecommendationByUserAndJob(userId, jobId) {
  return request({
    url: `/job-matching/calculate/${userId}/${jobId}`,
    method: 'get'
  })
}
