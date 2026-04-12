import dayjs from 'dayjs'

/**
 * 格式化日期
 * @param {Date|string|number} date 日期
 * @param {string} format 格式
 * @returns {string}
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  return dayjs(date).format(format)
}

/**
 * 格式化文件大小
 * @param {number} bytes 字节数
 * @returns {string}
 */
export function formatFileSize(bytes) {
  if (bytes === 0) return '0 B'

  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))

  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

/**
 * 防抖函数
 * @param {Function} func 要防抖的函数
 * @param {number} wait 等待时间
 * @returns {Function}
 */
export function debounce(func, wait) {
  let timeout
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout)
      func(...args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}

/**
 * 节流函数
 * @param {Function} func 要节流的函数
 * @param {number} limit 时间间隔
 * @returns {Function}
 */
export function throttle(func, limit) {
  let inThrottle
  return function executedFunction(...args) {
    if (!inThrottle) {
      func.apply(this, args)
      inThrottle = true
      setTimeout(() => { inThrottle = false }, limit)
    }
  }
}

/**
 * 深拷贝
 * @param {any} obj 要拷贝的对象
 * @returns {any}
 */
export function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime())
  if (obj instanceof Array) return obj.map(item => deepClone(item))
  if (typeof obj === 'object') {
    const clonedObj = {}
    for (const key in obj) {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }
}

/**
 * 生成随机字符串
 * @param {number} length 长度
 * @returns {string}
 */
export function randomString(length = 8) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  let result = ''
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return result
}

/**
 * 验证邮箱格式
 * @param {string} email 邮箱
 * @returns {boolean}
 */
export function validateEmail(email) {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return re.test(email)
}

/**
 * 验证手机号格式
 * @param {string} phone 手机号
 * @returns {boolean}
 */
export function validatePhone(phone) {
  const re = /^1[3-9]\d{9}$/
  return re.test(phone)
}

/**
 * 获取文件扩展名
 * @param {string} filename 文件名
 * @returns {string}
 */
export function getFileExtension(filename) {
  return filename.slice((filename.lastIndexOf('.') - 1 >>> 0) + 2)
}

/**
 * 检查是否为图片文件
 * @param {string} filename 文件名
 * @returns {boolean}
 */
export function isImageFile(filename) {
  const imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp']
  const extension = getFileExtension(filename).toLowerCase()
  return imageExtensions.includes(extension)
}

/**
 * 检查是否为文档文件
 * @param {string} filename 文件名
 * @returns {boolean}
 */
export function isDocumentFile(filename) {
  const docExtensions = ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'txt']
  const extension = getFileExtension(filename).toLowerCase()
  return docExtensions.includes(extension)
}

/**
 * 获取用户角色显示名称
 * @param {string} role 角色
 * @returns {string}
 */
export function getRoleDisplayName(role) {
  const roleMap = {
    applicant: '求职者',
    company: '企业用户'
  }
  return roleMap[role] || role
}

/**
 * 获取应用状态显示名称
 * @param {string} status 状态
 * @returns {string}
 */
export function getApplicationStatusName(status) {
  const statusMap = {
    pending: '已投递',
    rejected: '拒绝申请',
    hired: '已录用',
    reject_hire: '拒绝录用',
    interview: '待面试',
    completed: '待定',
    accepted: '接受面试',
    int_rejected: '拒绝面试'
  }
  return statusMap[status] || status
}

/**
 * 获取应用状态对应的标签类型
 * @param {string} status 状态
 * @returns {string}
 */
export function getApplicationStatusType(status) {
  const typeMap = {
    pending: 'info',
    rejected: 'danger',
    hired: 'success',
    reject_hire: 'warning',
    interview: 'primary',
    completed: 'warning',
    accepted: 'success',
    int_rejected: 'danger'
  }
  return typeMap[status] || 'info'
}
