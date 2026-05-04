import request from './index'

// 完善企业档案
export function completeCompanyProfile(data) {
  return request({
    url: '/company/profile',
    method: 'post',
    data
  })
}

// 更新企业档案
export function updateCompanyProfile(data) {
  return request({
    url: '/company/profile',
    method: 'post',
    data
  })
}

// 获取企业档案信息
export function getCompanyProfile() {
  return request({
    url: '/company/profile',
    method: 'get'
  })
}

// 检查档案完成状态
export function checkCompanyProfileStatus() {
  return request({
    url: '/company/profile/status',
    method: 'get'
  })
}