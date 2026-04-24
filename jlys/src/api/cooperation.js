import request from '@/utils/request'

const API_BASE = '/cooperation'

// ==================== 合作项目管理 ====================

/**
 * 创建合作项目
 */
export function createProject(data) {
  return request({
    url: `${API_BASE}/projects`,
    method: 'post',
    data
  })
}

/**
 * 更新合作项目
 */
export function updateProject(projectId, data) {
  return request({
    url: `${API_BASE}/projects/${projectId}`,
    method: 'put',
    data
  })
}

/**
 * 提交合作项目
 */
export function submitProject(projectId) {
  return request({
    url: `${API_BASE}/projects/${projectId}/submit`,
    method: 'post'
  })
}

/**
 * 获取项目详情
 */
export function getProjectDetail(projectId) {
  return request({
    url: `${API_BASE}/projects/${projectId}`,
    method: 'get'
  })
}

/**
 * 获取项目列表
 */
export function getProjectList(params) {
  return request({
    url: `${API_BASE}/projects`,
    method: 'get',
    params
  })
}

/**
 * 删除项目
 */
export function deleteProject(projectId) {
  return request({
    url: `${API_BASE}/projects/${projectId}`,
    method: 'delete'
  })
}

// ==================== 实训基地管理 ====================

/**
 * 创建实训基地
 */
export function createTrainingBase(projectId, data) {
  return request({
    url: `${API_BASE}/training-bases`,
    method: 'post',
    params: { projectId },
    data
  })
}

/**
 * 更新实训基地
 */
export function updateTrainingBase(baseId, data) {
  return request({
    url: `${API_BASE}/training-bases/${baseId}`,
    method: 'put',
    data
  })
}

/**
 * 获取实训基地详情
 */
export function getTrainingBaseDetail(baseId) {
  return request({
    url: `${API_BASE}/training-bases/${baseId}`,
    method: 'get'
  })
}

/**
 * 获取实训基地列表
 */
export function getTrainingBaseList(params) {
  return request({
    url: `${API_BASE}/training-bases`,
    method: 'get',
    params
  })
}

/**
 * 删除实训基地
 */
export function deleteTrainingBase(baseId) {
  return request({
    url: `${API_BASE}/training-bases/${baseId}`,
    method: 'delete'
  })
}

// ==================== 订单班管理 ====================

/**
 * 创建订单班
 */
export function createOrderClass(projectId, data) {
  return request({
    url: `${API_BASE}/order-classes`,
    method: 'post',
    params: { projectId },
    data
  })
}

/**
 * 更新订单班
 */
export function updateOrderClass(classId, data) {
  return request({
    url: `${API_BASE}/order-classes/${classId}`,
    method: 'put',
    data
  })
}

/**
 * 获取订单班详情
 */
export function getOrderClassDetail(classId) {
  return request({
    url: `${API_BASE}/order-classes/${classId}`,
    method: 'get'
  })
}

/**
 * 获取订单班列表
 */
export function getOrderClassList(params) {
  return request({
    url: `${API_BASE}/order-classes`,
    method: 'get',
    params
  })
}

/**
 * 删除订单班
 */
export function deleteOrderClass(classId) {
  return request({
    url: `${API_BASE}/order-classes/${classId}`,
    method: 'delete'
  })
}

// ==================== 合作案例库 ====================

/**
 * 获取案例详情
 */
export function getCaseDetail(caseId) {
  return request({
    url: `${API_BASE}/cases/${caseId}`,
    method: 'get'
  })
}

/**
 * 获取案例列表
 */
export function getCaseList(params) {
  return request({
    url: `${API_BASE}/cases`,
    method: 'get',
    params
  })
}

// ==================== 统计分析 ====================

/**
 * 获取合作统计
 */
export function getCooperationStats(params) {
  return request({
    url: `${API_BASE}/stats`,
    method: 'get',
    params
  })
}
