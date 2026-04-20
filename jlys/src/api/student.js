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