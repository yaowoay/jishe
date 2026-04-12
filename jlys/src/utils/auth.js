import Cookies from 'js-cookie'
import { STORAGE_KEYS, USER_ROLES } from './constants'

/**
 * 获取Token
 */
export function getToken() {
  return Cookies.get(STORAGE_KEYS.TOKEN) || localStorage.getItem(STORAGE_KEYS.TOKEN)
}

/**
 * 设置Token
 */
export function setToken(token) {
  Cookies.set(STORAGE_KEYS.TOKEN, token, { expires: 7 }) // 7天过期
  localStorage.setItem(STORAGE_KEYS.TOKEN, token)
}

/**
 * 移除Token
 */
export function removeToken() {
  Cookies.remove(STORAGE_KEYS.TOKEN)
  localStorage.removeItem(STORAGE_KEYS.TOKEN)
}

/**
 * 获取用户角色
 */
export function getUserRole() {
  return localStorage.getItem(STORAGE_KEYS.USER_ROLE)
}

/**
 * 设置用户角色
 */
export function setUserRole(role) {
  localStorage.setItem(STORAGE_KEYS.USER_ROLE, role)
}

/**
 * 移除用户角色
 */
export function removeUserRole() {
  localStorage.removeItem(STORAGE_KEYS.USER_ROLE)
}

/**
 * 获取用户ID
 */
export function getUserId() {
  return localStorage.getItem(STORAGE_KEYS.USER_ID)
}

/**
 * 设置用户ID
 */
export function setUserId(userId) {
  localStorage.setItem(STORAGE_KEYS.USER_ID, userId)
}

/**
 * 移除用户ID
 */
export function removeUserId() {
  localStorage.removeItem(STORAGE_KEYS.USER_ID)
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  const userInfo = localStorage.getItem(STORAGE_KEYS.USER_INFO)
  return userInfo ? JSON.parse(userInfo) : null
}

/**
 * 设置用户信息
 */
export function setUserInfo(userInfo) {
  localStorage.setItem(STORAGE_KEYS.USER_INFO, JSON.stringify(userInfo))
}

/**
 * 移除用户信息
 */
export function removeUserInfo() {
  localStorage.removeItem(STORAGE_KEYS.USER_INFO)
}

/**
 * 清除所有认证信息
 */
export function clearAuth() {
  removeToken()
  removeUserRole()
  removeUserId()
  removeUserInfo()
}

/**
 * 检查是否已登录
 */
export function isAuthenticated() {
  return !!getToken()
}

/**
 * 检查是否为求职者
 */
export function isApplicant() {
  return getUserRole() === USER_ROLES.APPLICANT
}

/**
 * 检查是否为企业用户
 */
export function isCompany() {
  return getUserRole() === USER_ROLES.COMPANY
}

/**
 * 检查用户权限
 */
export function hasPermission(requiredRole) {
  if (!requiredRole) return true

  const userRole = getUserRole()
  if (!userRole) return false

  if (Array.isArray(requiredRole)) {
    return requiredRole.includes(userRole)
  }

  return userRole === requiredRole
}

/**
 * 获取用户默认首页路径
 */
export function getDefaultHomePath() {
  const role = getUserRole()
  switch (role) {
  case USER_ROLES.APPLICANT:
    return '/applicant/dashboard'
  case USER_ROLES.COMPANY:
    return '/company/dashboard'
  default:
    return '/'
  }
}

/**
 * 检查Token是否即将过期
 */
export function isTokenExpiringSoon() {
  const token = getToken()
  if (!token) return true

  try {
    // 解析JWT token获取过期时间
    const payload = JSON.parse(atob(token.split('.')[1]))
    const exp = payload.exp * 1000 // 转换为毫秒
    const now = Date.now()
    const timeUntilExpiry = exp - now

    // 如果剩余时间少于30分钟，认为即将过期
    return timeUntilExpiry < 30 * 60 * 1000
  } catch (error) {
    console.error('解析token失败:', error)
    return true
  }
}

/**
 * 刷新Token
 */
export async function refreshToken() {
  // TODO: 实现token刷新逻辑
  // 这里应该调用后端的刷新token接口
  console.log('刷新token功能待实现')
}
