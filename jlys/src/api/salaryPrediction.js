import request from '@/utils/request'

/**
 * 查询所有预测结果
 */
export function getAllResults() {
  return request({
    url: '/api/salary-prediction/results',
    method: 'get'
  })
}

/**
 * 按日期查询预测结果
 */
export function getResultsByDate(date) {
  return request({
    url: `/api/salary-prediction/results/date/${date}`,
    method: 'get'
  })
}

/**
 * 按用户ID查询预测结果
 */
export function getResultsByUserId(userId) {
  return request({
    url: `/api/salary-prediction/user/${userId}`,
    method: 'get'
  })
}

/**
 * 获取用户最新的预测结果
 */
export function getLatestResultByUserId(userId) {
  return request({
    url: `/api/salary-prediction/user/${userId}/latest`,
    method: 'get'
  })
}

/**
 * 添加预测结果
 */
export function addPredictionResult(result) {
  return request({
    url: '/api/salary-prediction/add',
    method: 'post',
    data: result
  })
}

/**
 * 批量添加预测结果
 */
export function addPredictionResultBatch(results) {
  return request({
    url: '/api/salary-prediction/batch-add',
    method: 'post',
    data: results
  })
}

/**
 * 删除用户的预测结果
 */
export function deletePredictionResults(userId) {
  return request({
    url: `/api/salary-prediction/user/${userId}`,
    method: 'delete'
  })
}

/**
 * 获取预测总数
 */
export function getPredictionCount() {
  return request({
    url: '/api/salary-prediction/count',
    method: 'get'
  })
}
