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