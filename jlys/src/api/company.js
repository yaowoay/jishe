import request from '@/utils/request'

/**
 * 公司信息相关API
 */

// 获取公司信息
export function getCompanyProfile() {
  return request({
    url: '/company/profile',
    method: 'get'
  })
}

// 保存或更新公司信息
export function saveCompanyProfile(data) {
  return request({
    url: '/company/profile',
    method: 'post',
    data
  })
}

// 检查是否已有公司信息
export function checkCompanyProfileExists() {
  return request({
    url: '/company/profile/exists',
    method: 'get'
  })
}
