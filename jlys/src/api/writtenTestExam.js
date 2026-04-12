import request from '@/utils/request'

// 获取笔试考试信息
export function getExamInfo(applicationId) {
  return request({
    url: `/written-test-exam/info/${applicationId}`,
    method: 'get'
  })
}

// 开始笔试考试
export function startExam(applicationId) {
  return request({
    url: `/written-test-exam/start/${applicationId}`,
    method: 'post'
  })
}

// 提交笔试答案
export function submitAnswers(data) {
  return request({
    url: '/written-test-exam/submit',
    method: 'post',
    data
  })
}

// 获取笔试结果
export function getTestResult(applicationId) {
  return request({
    url: `/written-test-exam/result/${applicationId}`,
    method: 'get'
  })
}

// 检查笔试状态
export function getTestStatus(applicationId) {
  return request({
    url: `/written-test-exam/status/${applicationId}`,
    method: 'get'
  })
}

// 重新开始笔试
export function restartExam(applicationId) {
  return request({
    url: `/written-test-exam/restart/${applicationId}`,
    method: 'post'
  })
}
