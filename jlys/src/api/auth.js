import api from './index'

export const authApi = {
  // 用户登录
  login(data) {
    return api.post('/auth/login', data)
  },

  // 用户注册
  register(data) {
    return api.post('/auth/register', data)
  },

  // 检查用户角色
  checkRole() {
    return api.get('/auth/check-role')
  },

  // 退出登录
  logout() {
    return api.post('/auth/logout')
  }
}
