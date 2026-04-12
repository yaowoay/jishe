import request from './index'

// 完善企业档案
export function completeCompanyProfile(data) {
  return request({
    url: '/company/profile/complete',
    method: 'post',
    data
  })
}

// 更新企业档案
export function updateCompanyProfile(data) {
  return request({
    url: '/company/profile/update',
    method: 'put',
    data
  })
}

// 获取企业档案信息
export function getCompanyProfile() {
  return request({
    url: '/company/profile/info',
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