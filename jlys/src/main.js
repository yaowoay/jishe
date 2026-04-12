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
  console.error(e)
}
window.addEventListener('error', resizeObserverErrorHandler)

// API
import api from '@/api'

const app = createApp(App)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 全局属性
app.config.globalProperties.$api = api

app.use(store)
app.use(router)
app.use(ElementPlus)

app.mount('#app')
