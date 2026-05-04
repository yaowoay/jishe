// 测试结果相关API接口
import request from '@/utils/request'

/**
 * 保存测试结果到数据库
 * @param {Object} testData 测试数据
 * @returns {Promise} API响应
 */
export function saveTestResult(testData) {
  return request({
    url: '/test-results',
    method: 'post',
    data: testData
  })
}

/**
 * 获取用户测试历史记录
 * @param {Object} params 查询参数
 * @returns {Promise} API响应
 */
export function getTestHistory(params = {}) {
  return request({
    url: '/test-results/history',
    method: 'get',
    params: {
      page: params.page || 1,
      pageSize: params.pageSize || 10,
      category: params.category,
      startDate: params.startDate,
      endDate: params.endDate
    }
  })
}

/**
 * 获取测试结果详情
 * @param {number} resultId 测试结果ID
 * @returns {Promise} API响应
 */
export function getTestResultDetail(resultId) {
  return request({
    url: `/test-results/${resultId}`,
    method: 'get'
  })
}

/**
 * 获取用户测试统计信息
 * @param {Object} params 查询参数
 * @returns {Promise} API响应
 */
export function getTestStatistics(params = {}) {
  return request({
    url: '/test-results/statistics',
    method: 'get',
    params: {
      category: params.category,
      timeRange: params.timeRange || '30d' // 30d, 90d, 1y
    }
  })
}

/**
 * 删除测试记录
 * @param {number} resultId 测试结果ID
 * @returns {Promise} API响应
 */
export function deleteTestResult(resultId) {
  return request({
    url: `/test-results/${resultId}`,
    method: 'delete'
  })
}

/**
 * 批量删除测试记录
 * @param {Array} resultIds 测试结果ID数组
 * @returns {Promise} API响应
 */
export function batchDeleteTestResults(resultIds) {
  return request({
    url: '/test-results/batch-delete',
    method: 'post',
    data: { resultIds }
  })
}

/**
 * 导出测试结果
 * @param {Object} params 导出参数
 * @returns {Promise} API响应
 */
export function exportTestResults(params = {}) {
  return request({
    url: '/test-results/export',
    method: 'get',
    params: {
      format: params.format || 'excel', // excel, pdf, csv
      category: params.category,
      startDate: params.startDate,
      endDate: params.endDate
    },
    responseType: 'blob'
  })
}

/**
 * 获取能力分析报告
 * @param {Object} params 查询参数
 * @returns {Promise} API响应
 */
export function getAbilityAnalysis(params = {}) {
  return request({
    url: '/test-results/ability-analysis',
    method: 'get',
    params: {
      category: params.category,
      timeRange: params.timeRange || '30d',
      compareWith: params.compareWith // 'average', 'previous'
    }
  })
}

/**
 * 获取学习建议
 * @param {Object} params 查询参数
 * @returns {Promise} API响应
 */
export function getLearningRecommendations(params = {}) {
  return request({
    url: '/test-results/recommendations',
    method: 'get',
    params: {
      category: params.category,
      weakAreas: params.weakAreas
    }
  })
}
