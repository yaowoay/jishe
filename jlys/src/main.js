import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

// Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 全局样式
import '@/styles/global.css'

// 解决 ResizeObserver 错误
const debounce = (fn, delay) => {
  let timeoutId
  return (...args) => {
    clearTimeout(timeoutId)
    timeoutId = setTimeout(() => fn.apply(null, args), delay)
  }
}

const _ResizeObserver = window.ResizeObserver
window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
  constructor(callback) {
    callback = debounce(callback, 20)
    super(callback)
  }
}

// 捕获并忽略 ResizeObserver 错误
const resizeObserverErrorHandler = (e) => {
  if (e.message === 'ResizeObserver loop completed with undelivered notifications.') {
    return
  }
  // ✅ 新增：忽略 Element Plus 的 getBoundingClientRect 错误
  if (e.message && e.message.includes('getBoundingClientRect')) {
    console.warn('已忽略Element Plus渲染错误')
    return
  }
  console.error(e)
}
window.addEventListener('error', resizeObserverErrorHandler)

// ✅ 新增：捕获未处理的 Promise 拒绝
window.addEventListener('unhandledrejection', (e) => {
  if (e.reason && e.reason.message && e.reason.message.includes('getBoundingClientRect')) {
    e.preventDefault()
    console.warn('已忽略Element Plus Promise错误')
    return
  }
})

// API
import api from '@/api'

const app = createApp(App)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 全局属性
app.config.globalProperties.$api = api

// ✅ 新增：Vue 全局错误处理
app.config.errorHandler = (err, vm, info) => {
  if (err && err.message && err.message.includes('getBoundingClientRect')) {
    console.warn('Vue捕获到Element Plus错误，已忽略')
    return
  }
  console.error('Vue错误:', err, info)
}

app.use(store)
app.use(router)
app.use(ElementPlus)

app.mount('#app')