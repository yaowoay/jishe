import request from './index'

// 完善学生档案
export function completeStudentProfile(data) {
  return request({
    url: '/student/profile/complete',
    method: 'post',
    data
  })
}

// 更新学生档案
export function updateStudentProfile(data) {
  return request({
    url: '/student/profile/update',
    method: 'put',
    data
  })
}

// 获取学生档案信息
export function getStudentProfile() {
  return request({
    url: '/student/profile/info',
    method: 'get'
  })
}

// 检查档案完成状态
export function checkProfileStatus() {
  return request({
    url: '/student/profile/status',
    method: 'get'
  })
}

// 获取当前年份毕业生总人数
export function getCurrentYearGraduateCount() {
  return request({
    url: '/student/profile/graduate/count',
    method: 'get'
  })
}

// 学生预警相关
export function getStudentWarnings(params) {
  return request({
    url: '/student/warnings/list',
    method: 'get',
    params
  })
}

export function viewStudentWarning(warningId) {
  return request({
    url: `/student/warnings/${warningId}/view`,
    method: 'post'
  })
}

export function respondStudentWarning(warningId, response) {
  return request({
    url: `/student/warnings/${warningId}/respond`,
    method: 'post',
    params: { response }
  })
}

export function getStudentWarningStats() {
  return request({
    url: '/student/warnings/stats',
    method: 'get'
  })
}

export function getStudentUnviewedWarningCount() {
  return request({
    url: '/student/warnings/unviewed-count',
    method: 'get'
  })
}

// 学生经历管理相关
export function updateStudentExperience(data) {
  return request({
    url: '/student/profile/experience',
    method: 'post',
    data
  })
}

export function getStudentExperience() {
  return request({
    url: '/student/profile/experience',
    method: 'get'
  })
}

export function updateResumeCompletionStatus(status) {
  return request({
    url: '/student/profile/resume-status',
    method: 'post',
    params: { status }
  })
}
