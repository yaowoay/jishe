import request from './index'

// 完善教师档案
export function completeTeacherProfile(data) {
  return request({
    url: '/teacher/profile/complete',
    method: 'post',
    data
  })
}

// 更新教师档案
export function updateTeacherProfile(data) {
  return request({
    url: '/teacher/profile/update',
    method: 'put',
    data
  })
}

// 获取教师档案信息
export function getTeacherProfile() {
  return request({
    url: '/teacher/profile/info',
    method: 'get'
  })
}

// 检查档案完成状态
export function checkTeacherProfileStatus() {
  return request({
    url: '/teacher/profile/status',
    method: 'get'
  })
}

// src/api/teacher.js
export function getActivities() {}
export function createActivity() {}
export function getAssistanceRecords() {}
export function saveAssistanceRecord() {}
export function getCompanies() {}
export function auditCompany() {}
export function getCooperationApplications() {}
export function auditCooperationApplication() {}
export function getTeacherDashboard() {}
export function getEmploymentList() {}
export function auditEmployment() {}
export function getEmploymentStats() {}
export function getJobs() {}
export function auditJob() {}
export function queryStudents() {}