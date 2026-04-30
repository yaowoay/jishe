const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,

  // 开发服务器配置
  devServer: {
    port: 3001,
    open: true,
    client: {
      overlay: {
        warnings: false,
        errors: true,
        runtimeErrors: (error) => {
          const msg = (error && error.message) ? error.message : ''
          if (msg.includes('ResizeObserver loop completed with undelivered notifications')) return false
          if (msg.includes('ResizeObserver loop limit exceeded')) return false
          return true
        }
      }
    },
    proxy: {
      // 代理所有后端请求（不需要 /api 前缀）
      '/resumes': {
        target: 'http://localhost:8089',
        changeOrigin: true
      },
      '/application': {
        target: 'http://localhost:8089',
        changeOrigin: true
      },
      '/jobs': {
        target: 'http://localhost:8089',
        changeOrigin: true
      },
      '/users': {
        target: 'http://localhost:8089',
        changeOrigin: true
      },
      // 代理所有后端请求
      '/api': {
        target: 'http://localhost:8089',
        changeOrigin: true,
        ws: true,  // 支持websocket
        secure: false,
        // 关键：确保请求头被正确转发
        onProxyReq: (proxyReq, req, res) => {
          // 转发Authorization头
          if (req.headers.authorization) {
            proxyReq.setHeader('Authorization', req.headers.authorization)
          }
          // 转发其他自定义头
          if (req.headers['x-token']) {
            proxyReq.setHeader('X-Token', req.headers['x-token'])
          }
        },
        // 配置日志输出（调试用）
        logLevel: 'debug'
      },
      '/external-api': {
        target: 'http://81.70.20.30',
        changeOrigin: true,
        pathRewrite: {
          '^/external-api': '/v1'
        },
        // 添加这个 ↓↓↓
        '/application': {
          target: 'http://localhost:8089',
          changeOrigin: true
        }
      }
    }
  },

  // 生产环境配置
  publicPath: process.env.NODE_ENV === 'production' ? './' : '/',
  outputDir: 'dist',
  assetsDir: 'static',

  // CSS配置
  css: {
    extract: process.env.NODE_ENV === 'production',
    sourceMap: false
  },

  // 配置别名
  configureWebpack: {
    resolve: {
      alias: {
        '@': require('path').resolve(__dirname, 'src')
      }
    }
  },

  // 链式配置
  chainWebpack: config => {
    // 设置页面标题
    config.plugin('html').tap(args => {
      args[0].title = '高校学生就业能力智配平台'
      return args
    })

    // 优化打包
    if (process.env.NODE_ENV === 'production') {
      config.optimization.splitChunks({
        chunks: 'all',
        cacheGroups: {
          libs: {
            name: 'chunk-libs',
            test: /[\\/]node_modules[\\/]/,
            priority: 10,
            chunks: 'initial'
          },
          elementUI: {
            name: 'chunk-elementPlus',
            priority: 20,
            test: /[\\/]node_modules[\\/]_?element-plus(.*)/
          },
          commons: {
            name: 'chunk-commons',
            test: resolve('src/components'),
            minChunks: 3,
            priority: 5,
            reuseExistingChunk: true
          }
        }
      })
    }
  }
})

function resolve(dir) {
  return require('path').join(__dirname, dir)
}
