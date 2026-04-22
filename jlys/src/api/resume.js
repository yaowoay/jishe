import request from '@/utils/request'

/**
 * 简历相关API
 */

// 获取求职信息
export function getJobInfo() {
  return request({
    url: '/resume/job-info',
    method: 'get'
  })
}

// 保存求职信息
export function saveJobInfo(data) {
  return request({
    url: '/resume/job-info',
    method: 'post',
    data
  })
}

// 获取简历列表（上传的文件）- 用于"我的简历"模块
export function getResumeList() {
  return request({
    url: '/resume/my-list',  // 改为 /resume/my-list 用于"我的简历"模块
    method: 'get'
  })
}

// 获取简历列表（用于分析）- 用于"简历分析"模块
export function getAnalysisResumeList() {
  return request({
    url: '/resume/analysis-list',  // 新增 /resume/analysis-list 用于"简历分析"模块
    method: 'get'
  })
}

// 上传简历
export function uploadResume(formData) {
  return request({
    url: '/resume/upload',  // 改为 /resume/upload
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除简历
export function deleteResumeById(resumeId) {
  return request({
    url: `/resumes/${resumeId}`,  // 修正为 /resumes/{id}
    method: 'delete'
  })
}

// 获取简历详情
export function getResumeById(resumeId) {
  return request({
    url: `/resumes/${resumeId}`,
    method: 'get'
  })
}

// 下载简历
export function downloadResume(resumeId) {
  return request({
    url: `/resumes/${resumeId}/download`,
    method: 'get',
    responseType: 'blob'
  })
}

// 分析简历
export function analyzeResume(resumeId) {
  return request({
    url: `/resumes/${resumeId}/analyze`,
    method: 'post'
  })
}

// 投递简历到职位
export function submitJobApplication(data) {
  return request({
    url: '/application/submit',
    method: 'post',
    data
  })
}

// 简历筛选打分
export function analyzeJobApplication(data) {
  return request({
    url: '/application/analyze',
    method: 'post',
    data
  })
}

// 获取已投递的职位详情列表
export function getSubmittedJobs() {
  return request({
    url: '/application/submitted-jobs',
    method: 'get'
  })
}

// 获取已投递的职位ID列表
export function getSubmittedJobIds() {
  return request({
    url: '/application/submitted-job-ids',
    method: 'get'
  })
}

// 获取投递历史
export function getApplicationHistory() {
  return request({
    url: '/application/history',
    method: 'get'
  })
}

// 获取投递状态
export function getApplicationStatus(applicationId) {
  return request({
    url: `/application/${applicationId}/status`,
    method: 'get'
  })
}

// 撤回投递
export function withdrawApplication(applicationId) {
  return request({
    url: `/application/${applicationId}/withdraw`,
    method: 'post'
  })
}

/**
 * 简历分析相关API
 */

// 上传简历进行分析
export function uploadResumeForAnalysis(formData) {
  return request({
    url: '/api/resume-analysis/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 分析简历
export function analyzeResumeWithJob(data) {
  return request({
    url: '/api/resume-analysis/analyze',
    method: 'post',
    data
  })
}

// 获取分析报告
export function getAnalysisReport() {
  return request({
    url: '/api/resume-analysis/report',
    method: 'get'
  })
}

// 获取用户简历列表（用于分析）
export function getUserResumesForAnalysis() {
  return request({
    url: '/api/resume-analysis/resumes',
    method: 'get'
  })
}

// 获取简历解析数据
export function getResumeParsedData(resumeId) {
  return request({
    url: '/api/resume-analysis/parsed-data',
    method: 'get',
    params: { resumeId }
  })
}

// 投递简历（分析版本）
export function submitResumeForJob(data) {
  return request({
    url: '/resume-analysis/submit',
    method: 'post',
    data
  })
}

// 设置分析会话
export function setAnalysisSession(data) {
  return request({
    url: '/resume-analysis/set-analysis-session',
    method: 'post',
    data
  })
}

/**
 * 简历生成相关API
 */

// 生成简历
export function generateResume(data) {
  return request({
    url: '/make/generate1',  // 改成这个
    method: 'post',
    data
  })
}

// 基于模板生成简历
export function generateResumeWithTemplate(data) {
  return request({
    url: '/resume-generation/generate-with-template',
    method: 'post',
    data
  })
}

// 验证API配置
export function validateGenerationConfig() {
  return request({
    url: '/resume-generation/validate-config',
    method: 'get'
  })
}

// 获取简历模板列表
export function getResumeTemplates() {
  return request({
    url: '/resume/templates',
    method: 'get'
  })
}

// 获取模板详情
export function getTemplateById(templateId) {
  return request({
    url: `/resume/templates/${templateId}`,
    method: 'get'
  })
}

// 获取简历风格列表
export function getResumeStyles() {
  return request({
    url: '/resume-generation/styles',
    method: 'get'
  })
}


// 创建简历
export function createResume(data) {
  return request({
    url: '/resumes',
    method: 'post',
    data
  })
}

// 获取简历详情
export function getResume(resumeId) {
  return request({
    url: `/resumes/${resumeId}`,
    method: 'get'
  })
}

// 编辑/保存简历
export function updateResume(resumeId, data) {
  return request({
    url: `/resumes/${resumeId}`,
    method: 'put',
    data
  })
}

// 删除简历
export function deleteResume(resumeId) {
  return request({
    url: `/resumes/${resumeId}`,
    method: 'delete'
  })
}

// 复制简历
export function copyResume(resumeId) {
  return request({
    url: `/resumes/${resumeId}/copy`,
    method: 'post'
  })
}

// 生成分享链接
export function generateShareUrl(resumeId) {
  return request({
    url: `/resumes/${resumeId}/share`,
    method: 'post'
  })
}

// 通过分享链接获取简历
export function getResumeByShareUrl(shareUrl) {
  return request({
    url: `/resumes/share/${shareUrl}`,
    method: 'get'
  })
}

// 获取当前登录用户的简历列表（智能编辑专用）
export function getMyResumes() {
  return request({
    url: '/resumes/my',
    method: 'get'
  })
}

// AI生成简历
export function generateResumeWithAI(data) {
  return request({
    url: '/make/generate1',
    method: 'post',
    data
  })
}

// AI生成并保存简历
export function generateAndSaveResumeWithAI(data) {
  return request({
    url: '/make/save',
    method: 'post',
    data
  })
}

// 简历匹配分析
export function analyzeResumeMatch(formData) {
  return request({
    url: '/match/analyze',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 300000 // 5分钟超时，因为AI分析需要较长时间
  })
}

// 简历匹配分析健康检查
export function checkResumeMatchHealth() {
  return request({
    url: '/match/health',
    method: 'get'
  })
}
