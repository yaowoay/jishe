import axios from 'axios'
import { ElMessage } from 'element-plus'
import store from '@/store'
import router from '@/router'

// 创建axios实例
const api = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8089/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = store.getters.token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const res = response.data

    // 如果返回的状态码不是200，说明接口有问题
    if (res.code && res.code !== 200) {
      ElMessage.error(res.message || '请求失败')

      // 401表示token过期或无效
      if (res.code === 401) {
        store.dispatch('logout')
        router.push('/login')
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    }

    return res
  },
  error => {
    console.error('API Error:', error)

    if (error.response) {
      const { status, data } = error.response

      switch (status) {
      case 401:
        ElMessage.error('登录已过期，请重新登录')
        store.dispatch('logout')
        router.push('/login')
        break
      case 403:
        ElMessage.error('没有权限访问')
        break
      case 404:
        ElMessage.error('请求的资源不存在')
        break
      case 500:
        ElMessage.error('服务器内部错误')
        break
      default:
        ElMessage.error(data?.message || '请求失败')
      }
    } else if (error.request) {
      ElMessage.error('网络连接失败')
    } else {
      ElMessage.error('请求配置错误')
    }

    return Promise.reject(error)
  }
)

export default api
