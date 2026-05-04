// API配置文件
export const API_CONFIG = {
  // WebSocket语音识别接口
  WEBSOCKET_HOST: 'localhost:8085', // 替换为你的实际WebSocket主机地址
  WEBSOCKET_PATH: '/webSocket/content', // 替换为你的实际WebSocket路径
  
  // AI面试对话接口
  AI_INTERVIEW_URL: 'http://localhost:8085/api/interview/chat', // 替换为你的实际AI面试接口地址
  
  // 获取完整的WebSocket URL
  get WEBSOCKET_URL() {
    return `ws://${this.WEBSOCKET_HOST}${this.WEBSOCKET_PATH}`
  }
}

// 开发环境配置
export const DEV_CONFIG = {
  // 如果需要在开发环境中使用不同的地址，可以在这里配置
  WEBSOCKET_HOST: 'localhost:8085',
  AI_INTERVIEW_URL: 'http://localhost:8085/api/interview/chat'
}

// 生产环境配置
export const PROD_CONFIG = {
  // 生产环境的地址配置
  WEBSOCKET_HOST: 'your-production-host.com', // 替换为生产环境地址
  AI_INTERVIEW_URL: 'https://your-production-host.com/api/interview/chat' // 替换为生产环境地址
} 