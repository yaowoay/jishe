import request from '@/utils/request'

/**
 * 笔记相关API
 */

// 获取笔记列表（分页）
export function getNoteList(params) {
  return request({
    url: '  /note/list',
    method: 'post',
    data: params
  })
}

// 创建笔记
export function addNote(data) {
  return request({
    url: '  /note/add',
    method: 'post',
    data
  })
}

// 编辑笔记
export function editNote(data) {
  return request({
    url: '  /note/edit',
    method: 'post',
    data
  })
}

// 删除笔记
export function deleteNote(id) {
  return request({
    url: '  /note/delete',
    method: 'post',
    params: { id }
  })
}

// 获取笔记详情
export function getNoteDetail(id) {
  return request({
    url: '  /note/get',
    method: 'get',
    params: { id }
  })
}

// 获取分类统计
export function getCategoryStats() {
  return request({
    url: '  /note/category/stats',
    method: 'get'
  })
}

// 获取所有标签
export function getAllTags() {
  return request({
    url: '  /note/tags',
    method: 'get'
  })
}

// AI优化笔记
export function optimizeNote(data) {
  return request({
    url: '  /note/ai/optimize',
    method: 'post',
    data
  })
}

// AI聊天
export function chatWithAI(data) {
  return request({
    url: '  /note/ai/chat',
    method: 'post',
    data
  })
}
