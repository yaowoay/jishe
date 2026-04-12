import request from '@/utils/request'

/**
 * 笔记相关API
 */

// 获取笔记列表（分页）
export function getNoteList(params) {
  return request({
    url: '/api/note/list',
    method: 'post',
    data: params
  })
}

// 创建笔记
export function addNote(data) {
  return request({
    url: '/api/note/add',
    method: 'post',
    data
  })
}

// 编辑笔记
export function editNote(data) {
  return request({
    url: '/api/note/edit',
    method: 'post',
    data
  })
}

// 删除笔记
export function deleteNote(id) {
  return request({
    url: '/api/note/delete',
    method: 'post',
    params: { id }
  })
}

// 获取笔记详情
export function getNoteDetail(id) {
  return request({
    url: '/api/note/get',
    method: 'get',
    params: { id }
  })
}

// 获取分类统计
export function getCategoryStats() {
  return request({
    url: '/api/note/category/stats',
    method: 'get'
  })
}

// 获取所有标签
export function getAllTags() {
  return request({
    url: '/api/note/tags',
    method: 'get'
  })
}

// AI优化笔记
export function optimizeNote(data) {
  return request({
    url: '/api/note/ai/optimize',
    method: 'post',
    data
  })
}

// AI聊天
export function chatWithAI(data) {
  return request({
    url: '/api/note/ai/chat',
    method: 'post',
    data
  })
}
