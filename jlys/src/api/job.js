import request from '@/utils/request'

/**
 * 职位相关API
 */

// 发布新职位
export function createJob(data) {
  return request({
    url: '/jobs/create',
    method: 'post',
    data
  })
}

// 更新职位信息
export function updateJob(data) {
  return request({
    url: '/jobs/update',
    method: 'put',
    data
  })
}

// 获取职位详情
export function getJobById(jobId) {
  return request({
    url: `/jobs/${jobId}`,
    method: 'get'
  })
}

// 获取公司的所有职位
export function getCompanyJobs() {
  return request({
    url: '/jobs/company',
    method: 'get'
  })
}

// 分页获取公司的职位
export function getCompanyJobsWithPage(params) {
  return request({
    url: '/jobs/company/page',
    method: 'get',
    params
  })
}

// 搜索职位
export function searchJobs(params) {
  return request({
    url: '/jobs/search',
    method: 'get',
    params
  })
}

// 切换职位状态
export function toggleJobStatus(jobId) {
  return request({
    url: `/jobs/${jobId}/toggle-status`,
    method: 'post'
  })
}

// 删除职位
export function deleteJob(jobId) {
  return request({
    url: `/jobs/${jobId}`,
    method: 'delete'
  })
}

// 获取所有活跃职位
export function getActiveJobs() {
  return request({
    url: '/jobs/active',
    method: 'get'
  })
}

// 根据类型获取职位
export function getJobsByType(jobType) {
  return request({
    url: `/jobs/type/${jobType}`,
    method: 'get'
  })
}

// 获取即将过期的职位
export function getExpiringJobs(days = 7) {
  return request({
    url: '/jobs/expiring',
    method: 'get',
    params: { days }
  })
}

// 优化职位描述
export function optimizeJobDescription(data) {
  return request({
    url: '/job-description/generate-json',
    method: 'post',
    data
  })
}
