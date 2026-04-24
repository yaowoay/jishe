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
    url: '/teacher/profile',
    method: 'post',
    data
  })
}

// 获取教师档案信息
export function getTeacherProfile() {
  return request({
    url: '/teacher/profile',
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
export function getActivities(params) {
  return request({
    url: '/teacher/activities',
    method: 'get',
    params
  })
}

export function createActivity(data) {
  return request({
    url: '/teacher/activities',
    method: 'post',
    data
  })
}

export function updateActivity(activityId, data) {
  return request({
    url: `/teacher/activities/${activityId}`,
    method: 'put',
    data
  })
}

export function deleteActivity(activityId) {
  return request({
    url: `/teacher/activities/${activityId}`,
    method: 'delete'
  })
}

export function getActivityDetail(activityId) {
  return request({
    url: `/teacher/activities/${activityId}`,
    method: 'get'
  })
}

export function getActivityRegistrations(activityId) {
  return request({
    url: `/teacher/activities/${activityId}/registrations`,
    method: 'get'
  })
}

export function getAssistanceRecords(params) {
  return request({
    url: '/teacher/assistance',
    method: 'get',
    params
  })
}

export function saveAssistanceRecord(data) {
  return request({
    url: '/teacher/assistance',
    method: 'post',
    data
  })
}

export function getCompanies(params) {
  return request({
    url: '/teacher/companies',
    method: 'get',
    params
  })
}

export function auditCompany(companyId, data) {
  return request({
    url: `/teacher/companies/${companyId}/audit`,
    method: 'post',
    data
  })
}

export function getCooperationApplications() {
  return request({
    url: '/teacher/cooperation-applications',
    method: 'get'
  })
}

export function auditCooperationApplication(applicationId, data) {
  return request({
    url: `/teacher/cooperation-applications/${applicationId}/audit`,
    method: 'post',
    data
  })
}

export function getTeacherDashboard() {
  return request({
    url: '/teacher/dashboard',
    method: 'get'
  })
}

export function getEmploymentList(params) {
  return request({
    url: '/teacher/employment',
    method: 'get',
    params
  })
}

export function auditEmployment(data) {
  return request({
    url: '/teacher/employment/audit',
    method: 'post',
    data
  })
}

export function getEmploymentStats(params) {
  return request({
    url: '/teacher/employment-stats',
    method: 'get',
    params
  })
}

export function getJobs(params) {
  return request({
    url: '/teacher/jobs',
    method: 'get',
    params
  })
}

export function auditJob(jobId, data) {
  return request({
    url: `/teacher/jobs/${jobId}/audit`,
    method: 'post',
    data
  })
}

export function queryStudents(data) {
  return request({
    url: '/teacher/students/query',
    method: 'post',
    data
  })
}

export function getEarlyWarnings(params) {
  return request({
    url: '/teacher/early-warnings',
    method: 'get',
    params
  })
}

export function handleEarlyWarning(warningId, data) {
  return request({
    url: `/teacher/early-warnings/${warningId}/handle`,
    method: 'post',
    data
  })
}

// ==================== 精准帮扶功能 ====================

// 简历指导预约
export function getResumeGuidanceAppointments(status) {
  return request({
    url: '/teacher/resume-guidance/appointments',
    method: 'get',
    params: { status }
  })
}

export function createResumeGuidanceAppointment(data) {
  return request({
    url: '/teacher/resume-guidance/appointments',
    method: 'post',
    data
  })
}

export function updateResumeGuidanceAppointment(appointmentId, data) {
  return request({
    url: `/teacher/resume-guidance/appointments/${appointmentId}`,
    method: 'put',
    data
  })
}

export function getResumeGuidanceAppointmentDetail(appointmentId) {
  return request({
    url: `/teacher/resume-guidance/appointments/${appointmentId}`,
    method: 'get'
  })
}

export function cancelResumeGuidanceAppointment(appointmentId) {
  return request({
    url: `/teacher/resume-guidance/appointments/${appointmentId}/cancel`,
    method: 'post'
  })
}

export function completeResumeGuidance(appointmentId, feedback) {
  return request({
    url: `/teacher/resume-guidance/appointments/${appointmentId}/complete`,
    method: 'post',
    params: { feedback }
  })
}

export function getResumeGuidanceStatistics() {
  return request({
    url: '/teacher/resume-guidance/statistics',
    method: 'get'
  })
}

// 模拟面试预约
export function getMockInterviewAppointments(status) {
  return request({
    url: '/teacher/mock-interview/appointments',
    method: 'get',
    params: { status }
  })
}

export function createMockInterviewAppointment(data) {
  return request({
    url: '/teacher/mock-interview/appointments',
    method: 'post',
    data
  })
}

export function updateMockInterviewAppointment(appointmentId, data) {
  return request({
    url: `/teacher/mock-interview/appointments/${appointmentId}`,
    method: 'put',
    data
  })
}

export function getMockInterviewAppointmentDetail(appointmentId) {
  return request({
    url: `/teacher/mock-interview/appointments/${appointmentId}`,
    method: 'get'
  })
}

export function cancelMockInterviewAppointment(appointmentId) {
  return request({
    url: `/teacher/mock-interview/appointments/${appointmentId}/cancel`,
    method: 'post'
  })
}

export function completeMockInterview(appointmentId, data) {
  return request({
    url: `/teacher/mock-interview/appointments/${appointmentId}/complete`,
    method: 'post',
    params: data
  })
}

export function getMockInterviewStatistics() {
  return request({
    url: '/teacher/mock-interview/statistics',
    method: 'get'
  })
}

// 帮扶记录管理
export function getAssistanceRecordList(status) {
  return request({
    url: '/teacher/assistance/records',
    method: 'get',
    params: { status }
  })
}

export function createAssistanceRecord(data) {
  return request({
    url: '/teacher/assistance/records',
    method: 'post',
    data
  })
}

export function updateAssistanceRecord(recordId, data) {
  return request({
    url: `/teacher/assistance/records/${recordId}`,
    method: 'put',
    data
  })
}

export function getAssistanceRecordDetail(recordId) {
  return request({
    url: `/teacher/assistance/records/${recordId}`,
    method: 'get'
  })
}

export function deleteAssistanceRecord(recordId) {
  return request({
    url: `/teacher/assistance/records/${recordId}`,
    method: 'delete'
  })
}

export function addAssistanceTracking(recordId, data) {
  return request({
    url: `/teacher/assistance/records/${recordId}/tracking`,
    method: 'post',
    params: data
  })
}

export function getAssistanceTrackingList(recordId) {
  return request({
    url: `/teacher/assistance/records/${recordId}/tracking`,
    method: 'get'
  })
}

export function getAssistanceStatistics() {
  return request({
    url: '/teacher/assistance/statistics',
    method: 'get'
  })
}