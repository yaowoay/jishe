import request from '@/utils/request'

/**
 * DISC性格测试相关API
 */
export const discTestApi = {
  /**
   * 开始DISC测试
   * @param {number} userId - 用户ID（可选）
   * @returns {Promise}
   */
  startTest(userId = null) {
    return request({
      url: '/disc-test/start',
      method: 'post',
      params: userId ? { userId } : {}
    })
  },

  /**
   * 提交DISC测试答案
   * @param {Object} data - 测试数据
   * @param {string} data.testSession - 测试会话ID
   * @param {Array} data.answers - 答案数组
   * @returns {Promise}
   */
  submitTest(data) {
    return request({
      url: '/disc-test/submit',
      method: 'post',
      data
    })
  },

  /**
   * 获取测试结果
   * @param {string} testSession - 测试会话ID
   * @returns {Promise}
   */
  getTestResult(testSession) {
    return request({
      url: `/disc-test/result/${testSession}`,
      method: 'get'
    })
  },

  /**
   * 获取用户最新的测试结果
   * @param {number} userId - 用户ID
   * @returns {Promise}
   */
  getLatestTestResult(userId) {
    return request({
      url: `/disc-test/latest/${userId}`,
      method: 'get'
    })
  },

  /**
   * 获取DISC测试说明
   * @returns {Promise}
   */
  getTestInfo() {
    return request({
      url: '/disc-test/info',
      method: 'get'
    })
  },

  /**
   * 获取用户的DISC测试历史记录
   * @param {number} userId - 用户ID
   * @returns {Promise}
   */
  getTestHistory(userId) {
    return request({
      url: `/disc-test/history/${userId}`,
      method: 'get'
    })
  }
}
