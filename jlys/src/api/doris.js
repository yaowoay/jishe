import request from './index'

// 获取就业大屏核心数据（Doris）
export function getEmploymentScreenBigData() {
  return request({
    url: '/doris/employment-screen-bigdata',
    method: 'get'
  })
}

// 获取就业去向总体分布（Doris）
export function getEmploymentStatusCount(graduationYear) {
  return request({
    url: '/doris/employment-stat/employment-status-count',
    method: 'get',
    params: graduationYear ? { graduationYear } : undefined
  })
}

// 获取学院专业就业去向明细（Doris）
export function getCollegeMajorEmploymentStatusCount(graduationYear) {
  return request({
    url: '/doris/employment-stat/college-major-employment-status-count',
    method: 'get',
    params: graduationYear ? { graduationYear } : undefined
  })
}