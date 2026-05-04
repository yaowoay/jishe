import request from '@/utils/request'

/**
 * 获取用户实验分组
 */
export function getUserAssignment(experimentId, userId) {
  return request({
    url: `/ab-test/assignment/${experimentId}/${userId}`,
    method: 'get'
  })
}

/**
 * 记录实验事件
 */
export function recordEvent(experimentId, userId, eventType, eventData = {}) {
  return request({
    url: '/ab-test/event',
    method: 'post',
    data: {
      experimentId,
      userId,
      eventType,
      eventData
    }
  })
}

/**
 * 获取UI变体
 */
export function getUIVariant(userId, componentName) {
  return request({
    url: `/ab-test/ui-variant/${userId}`,
    method: 'get',
    params: { componentName }
  })
}
