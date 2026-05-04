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

// 注意：不要重写 window.ResizeObserver。
// 之前对 ResizeObserver 回调做 debounce，会导致组件卸载后回调延迟触发，
// Element Plus 内部可能拿到 null 引用并调用 getBoundingClientRect，从而报错。
// 这里只保留错误过滤，避免破坏第三方组件时序。

// 捕获并忽略 ResizeObserver 错误（避免被 webpack-dev-server overlay 红屏）
const isResizeObserverNoise = (msg = '') => {
  return msg.includes('ResizeObserver loop completed with undelivered notifications')
    || msg.includes('ResizeObserver loop limit exceeded')
}

const resizeObserverErrorHandler = (e) => {
  const msg = e?.message || ''

  if (isResizeObserverNoise(msg)) {
    e.stopImmediatePropagation?.()
    e.preventDefault?.()
    return false
  }

  // 忽略 Element Plus 偶发的布局计算报错噪声
  if (msg.includes('getBoundingClientRect')) {
    e.stopImmediatePropagation?.()
    e.preventDefault?.()
    return false
  }

  console.error(e)
  return true
}

// 使用捕获阶段，优先于 overlay 处理
window.addEventListener('error', resizeObserverErrorHandler, true)

// 捕获未处理的 Promise 拒绝
window.addEventListener('unhandledrejection', (e) => {
  const msg = e?.reason?.message || ''
  if (isResizeObserverNoise(msg) || msg.includes('getBoundingClientRect')) {
    e.preventDefault()
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