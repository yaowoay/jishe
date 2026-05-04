import request from '@/utils/request'


/**
 * 面试相关API
 */

// 获取面试信息
export function getInterviewInfo(interviewId) {
  return request({
    url: `/interview/${interviewId}`,
    method: 'get'
  })
}

// 开始面试
export function startInterview(interviewId) {
  return request({
    url: `/interview/${interviewId}/start`,
    method: 'post'
  })
}

export function quickEvaluate(applicationId) {
  return request({
    url: `/interview-evaluation/quick-evaluate/${applicationId}`,
    method: 'post'
  })
}

// 结束面试
export function endInterview(interviewId, data) {
  return request({
    url: `/interview/${interviewId}/end`,
    method: 'post',
    data
  })
}

// 发送消息
export function sendMessage(interviewId, data) {
  return request({
    url: `/interview/${interviewId}/message`,
    method: 'post',
    data
  })
}

// 获取聊天历史
export function getChatHistory(interviewId) {
  return request({
    url: `/interview/${interviewId}/messages`,
    method: 'get'
  })
}

// 上传语音
export function uploadAudio(interviewId, audioData) {
  const formData = new FormData()
  formData.append('audio', audioData)
  formData.append('interviewId', interviewId)
  
  return request({
    url: `/interview/${interviewId}/audio`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取面试状态
export function getInterviewStatus(interviewId) {
  return request({
    url: `/interview/${interviewId}/status`,
    method: 'get'
  })
}

// 更新面试状态
export function updateInterviewStatus(interviewId, status) {
  return request({
    url: `/interview/${interviewId}/status`,
    method: 'put',
    data: { status }
  })
}

// 获取面试评分
export function getInterviewScore(interviewId) {
  return request({
    url: `/interview/${interviewId}/score`,
    method: 'get'
  })
}

// 获取虚拟数字人配置
export function getAvatarConfig() {
  return request({
    url: '/avatar/config',
    method: 'get'
  })
}

// 获取虚拟数字人流信息
export function getAvatarStream(avatarId) {
  return request({
    url: `/avatar/${avatarId}/stream`,
    method: 'get'
  })
}

// 发送虚拟数字人指令
export function sendAvatarCommand(avatarId, command) {
  return request({
    url: `/avatar/${avatarId}/command`,
    method: 'post',
    data: command
  })
}

// 语音转文字
export function speechToText(audioData) {
  const formData = new FormData()
  formData.append('audio', audioData)
  
  return request({
    url: '/speech/to-text',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 文字转语音
export function textToSpeech(text, voice = 'default') {
  return request({
    url: '/speech/to-voice',
    method: 'post',
    data: { text, voice },
    responseType: 'blob'
  })
}

// 获取面试题目
export function getInterviewQuestions(interviewId) {
  return request({
    url: `/interview/${interviewId}/questions`,
    method: 'get'
  })
}

// 提交答案
export function submitAnswer(interviewId, questionId, answer) {
  return request({
    url: `/interview/${interviewId}/answer`,
    method: 'post',
    data: {
      questionId,
      answer,
      timestamp: new Date().toISOString()
    }
  })
}

// 获取面试报告
export function getInterviewReport(interviewId) {
  return request({
    url: `/interview/${interviewId}/report`,
    method: 'get'
  })
}

// 获取技术状态检测
export function getTechStatus() {
  return request({
    url: '/tech/status',
    method: 'get'
  })
}

// 网络质量检测
export function checkNetworkQuality() {
  return request({
    url: '/tech/network-check',
    method: 'get'
  })
}

// 音频质量检测
export function checkAudioQuality(audioData) {
  const formData = new FormData()
  formData.append('audio', audioData)
  
  return request({
    url: '/tech/audio-check',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 视频质量检测
export function checkVideoQuality() {
  return request({
    url: '/tech/video-check',
    method: 'get'
  })
}

// ==================== 视频分析相关API ====================

// 分析视频（同步）
export function analyzeVideo(formData) {
  return request({
    url: '/api/video-analysis/analyze',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 300000 // 5分钟超时
  })
}

// 异步分析视频
export function analyzeVideoAsync(formData) {
  return request({
    url: '/api/video-analysis/analyze-async',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取分析结果
export function getAnalysisResult(analysisId) {
  return request({
    url: `/api/video-analysis/result/${analysisId}`,
    method: 'get'
  })
}

// 获取分析状态
export function getAnalysisStatus(analysisId) {
  return request({
    url: `/api/video-analysis/status/${analysisId}`,
    method: 'get'
  })
}

// 根据面试ID获取分析结果
export function getAnalysisByInterviewId(interviewId) {
  return request({
    url: `/api/video-analysis/interview/${interviewId}`,
    method: 'get'
  })
}

// 视频分析相关API对象（便于导入使用）
export const videoAnalysisApi = {
  analyzeVideo,
  analyzeVideoAsync,
  getAnalysisResult,
  getAnalysisStatus,
  getAnalysisByInterviewId
}

// ==================== 面试进度相关API ====================

// 获取面试进度信息
export function getInterviewProgress(applicationId) {
  return request({
    url: `/interview-progress/info/${applicationId}`,
    method: 'get'
  })
}

// 保存笔试结果到面试进度
export function saveWrittenTestResult(data) {
  return request({
    url: '/interview-progress/save-written-test',
    method: 'post',
    data
  })
}

// 更新面试进度状态
export function updateInterviewProgressStatus(data) {
  return request({
    url: '/interview-progress/update-interview-status',
    method: 'post',
    data
  })
}

// 获取所有面试进度列表
export function getAllInterviewProgress() {
  return request({
    url: '/interview-progress/list',
    method: 'get'
  })
}

// ==================== 面试题目相关API ====================

// 根据申请ID获取面试题目
export function getInterviewQuestionsByApplicationId(applicationId) {
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
    data: answers
  })
}

// 实时保存单个回答
export function saveInterviewAnswer(applicationId, answerData) {
  return request({
    url: `/interview/save-answer/${applicationId}`,
    method: 'post',
    data: answerData
  })
}

// 更新面试状态（基于申请ID）
export function updateInterviewStatusByApplication(applicationId, status) {
  return request({
    url: `/interview/status/${applicationId}`,
    method: 'put',
    data: { status }
  })
}

// 获取面试结果
export function getInterviewResult(applicationId) {
  return request({
    url: `/interview/result/${applicationId}`,
    method: 'get'
  })
}

// ==================== 面试评估相关API ====================

// 快速评估面试结果
export function quickEvaluateInterview(applicationId) {
  return request({
    url: `/interview-evaluation/quick-evaluate/${applicationId}`,
    method: 'post'
  })
}

// 执行面试评估
export function evaluateInterview(data) {
  return request({
    url: '/interview-evaluation/evaluate',
    method: 'post',
    data
  })
}

// 获取评估结果
export function getEvaluationResult(applicationId) {
  return request({
    url: `/interview-evaluation/application/${applicationId}`,
    method: 'get'
  })
}

// 重新评估面试
export function reEvaluateInterview(applicationId) {
  return request({
    url: `/interview-evaluation/re-evaluate/${applicationId}`,
    method: 'post'
  })
}

// ==================== AI面试记录相关API ====================

// 保存AI面试记录
export function saveAIInterviewRecord(interviewRecord) {
  return request({
    url: '/ai-interviews',
    method: 'post',
    data: interviewRecord
  })
}

// 获取AI面试记录
export function getAIInterviewRecord(interviewId) {
  return request({
    url: `/ai-interviews/${interviewId}`,
    method: 'get'
  })
}

// 更新AI面试记录
export function updateAIInterviewRecord(interviewId, data) {
  return request({
    url: `/ai-interviews/${interviewId}`,
    method: 'put',
    data
  })
}
