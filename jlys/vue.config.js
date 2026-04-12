const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,

  // 开发服务器配置
  devServer: {
    port: 3001,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8089',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      },
      '/external-api': {
        target: 'http://81.70.20.30',
        changeOrigin: true,
        pathRewrite: {
          '^/external-api': '/v1'
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
      args[0].title = 'AI面试系统'
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
