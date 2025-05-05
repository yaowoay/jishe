<template>
    <div class="footer">
      <div class="footer-container">
        <div class="footer-section">
          <h4>关于我们</h4>
          <p>AI 面试官致力于为求职者提供智能化的面试训练解决方案</p>
        </div>
        <div class="footer-section">
          <h4>联系方式</h4>
          <p>邮箱：contact@aiinterviewer.com</p>
          <p>电话：400-888-8888</p>
        </div>
        <div class="footer-section">
          <h4>快速链接</h4>
          <ul>
            <li>帮助中心</li>
            <li>隐私政策</li>
          </ul>
        </div>
        <div class="footer-section">
          <h4>关注我们</h4>
          <div class="social-icons">
            <el-icon><Wechat /></el-icon>
            <el-icon><Weibo /></el-icon>
          </div>
        </div>
      </div>
      <div class="copyright">
        <p>© 2023 AI 面试官. 保留所有权利.</p>
      </div>
    </div>
  </template>
  
  <script>
  import { Wechat, Weibo } from '@element-plus/icons-vue'
  
  export default {
    name: 'AppFooter',
    components: {
      Wechat,
      Weibo
    }
  }
  </script>