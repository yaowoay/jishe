import request from '@/utils/request'

/**
 * 应聘者管理相关API
 */

// 获取应聘者列表
export function getApplicantsList(params) {
  return request({
    url: '/applicant-management/list',
    method: 'get',
    params
  })
}

// 获取应聘者详情
export function getApplicantDetail(applicationId) {
  return request({
    url: `/applicant-management/detail/${applicationId}`,
    method: 'get'
  })
}

// 更新申请状态
export function updateApplicationStatus(data) {
  return request({
    url: '/applicant-management/update-status',
    method: 'post',
    data
  })
}

// 发送面试邀请
export function sendInterviewInvitation(data) {
  return request({
    url: '/applicant-management/send-interview',
    method: 'post',
    data
  })
}

/**
 * 求职者个人信息相关API
 */

// 获取求职者个人信息
export function getApplicantProfile() {
  return request({
    url: '/applicant/profile/get',
    method: 'get'
  })
}

// 保存或更新求职者个人信息
export function saveApplicantProfile(data) {
  return request({
    url: '/applicant/profile',
    method: 'post',
    data
  })
}

// 检查是否已有求职者信息
export function checkApplicantProfileExists() {
  return request({
    url: '/applicant/profile/exists',
    method: 'get'
  })
}

// 拒绝申请
export function rejectApplication(data) {
  return request({
    url: '/applicant-management/reject',
    method: 'post',
    data
  })
}

// 批量操作申请
export function batchUpdateApplications(data) {
  return request({
    url: '/applicant-management/batch-update',
    method: 'post',
    data
  })
}

// 获取申请统计信息
export function getApplicationStatistics() {
  return request({
    url: '/applicant-management/statistics/applications',
    method: 'get'
  })
}

// 保存应聘者答题统计信息
export function saveCandidateAnswerStats(data) {
  return request({
    url: '/candidate-answer-stats',
    method: 'post',
    data
  })
}

// 设置笔试参数
export function setWrittenTestSettings(data) {
  return request({
    url: '/candidate-answer-stats/written-test',
    method: 'post',
    data
  })
}

// 设置面试参数（生成面试题目）
export function setInterviewSettings(data) {
  return request({
    url: `/interview/generate/${data.applicationId}`,
    method: 'post',
    data
  })
}

// 获取面试考试信息
export function getInterviewExamInfo(applicationId) {
  return request({
    url: `/interview/exam/${applicationId}`,
    method: 'get'
  })
}

// 开始面试考试
export function startInterviewExam(applicationId) {
  return request({
    url: `/interview/start/${applicationId}`,
    method: 'post'
  })
}

// 提交面试答案
export function submitInterviewAnswers(applicationId, answers) {
  return request({
    url: `/interview/submit/${applicationId}`,
    method: 'post',
    data: { answers }
  })
}

// 获取面试结果
export function getInterviewResult(applicationId) {
  return request({
    url: `/interview/result/${applicationId}`,
    method: 'get'
  })
}

// 获取面试状态
export function getInterviewStatus(applicationId) {
  return request({
    url: `/interview/status/${applicationId}`,
    method: 'get'
  })
}

// 获取应聘者答题统计信息
export function getCandidateAnswerStats(applicationId) {
  return request({
    url: `/candidate-answer-stats/${applicationId}`,
    method: 'get'
  })
}

// 录用应聘者
export function hireApplicant(applicationId, data) {
  return request({
    url: `/applicant-management/${applicationId}/hire`,
    method: 'post',
    data
  })
}

// 拒绝录用
export function rejectHire(applicationId, data) {
  return request({
    url: `/applicant-management/${applicationId}/reject-hire`,
    method: 'post',
    data
  })
}

/**
 * 应聘者端面试邀请相关API
 */

// 获取已投递的面试申请
export function getInterviewInvitations(params) {
  return request({
    url: '/application/submitted-jobs',
    method: 'get',
    params
  })
}

// 接受面试邀请
export function acceptInterviewInvitation(invitationId) {
  return request({
    url: `/application/submitted-jobs/${invitationId}/accept`,
    method: 'post'
  })
}

// 拒绝面试邀请
export function rejectInterviewInvitation(invitationId) {
  return request({
    url: `/application/interview-invitations/${invitationId}/reject`,
    method: 'post'
  })
}

// 获取面试邀请详情
export function getInterviewInvitationDetail(invitationId) {
  return request({
    url: `/applicant/interview-invitations/${invitationId}`,
    method: 'get'
  })
}

// 完成面试
export function completeInterview(applicationId) {
  return request({
    url: `/application/submitted-jobs/${applicationId}/complete`,
    method: 'post'
  })
}
