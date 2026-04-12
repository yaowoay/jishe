<template>
  <div class="login-container">
    <!-- 头部导航 -->
    <div class="auth-header">
      <div class="header-left">
        <div class="logo">
          <i class="fas fa-robot"></i>
          <span>AI面试官</span>
        </div>
      </div>
      <div class="header-right">
        <router-link to="/" class="btn btn-outline">返回首页</router-link>
        <router-link to="/register" class="btn btn-primary">注册</router-link>
      </div>
    </div>

    <div class="login-content">
      <div class="login-card">
        <div class="login-header">
          <h2>登录系统</h2>
          <p>欢迎回到AI面试官系统</p>
        </div>

        <!-- 角色选择 -->
<!--        <div class="role-selector">
          <el-radio-group v-model="loginForm.role" size="large">
            <el-radio-button label="student">学生</el-radio-button> &lt;!&ndash;改&ndash;&gt;
            <el-radio-button label="company">企业</el-radio-button>
            <el-radio-button label="teacher">教师</el-radio-button>
          </el-radio-group>
        </div>-->

      <el-form
        ref="loginForm"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="email">
          <el-input
            v-model="loginForm.email"
            placeholder="请输入邮箱"
            size="large"
            prefix-icon="Message"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-button"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>

        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="register-link">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { authApi } from '@/api/auth'
import { ElMessage } from 'element-plus'

export default {
  name: 'Login',
  data() {
    return {
      loading: false,
      loginForm: {
        email: '',
        password: '',
        role: 'student'
      },
      loginRules: {
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    async handleLogin() {
      try {
        await this.$refs.loginForm.validate()
        this.loading = true

        const response = await authApi.login(this.loginForm)

        if (response.success) {
          const { token, userId, email, role, profileCompleted } = response.data

          console.log('登录成功，用户信息:', { token, userId, email, role, profileCompleted })

          // 保存登录信息到store
          this.$store.dispatch('login', {
            token,
            user: { userId, email, role, profileCompleted }
          })

          ElMessage.success('登录成功')

          // 根据角色和档案完成状态跳转
          if (role === 'student') {
            console.log('学生登录，档案完成状态:', profileCompleted)
            // 学生统一跳转到dashboard，由dashboard处理档案完善提醒
            this.$router.push('/applicant/dashboard')
          } else if (role === 'company') {
            console.log('企业登录，档案完成状态:', profileCompleted)
            // 企业统一跳转到dashboard，由dashboard处理档案完善提醒
            this.$router.push('/company/dashboard')
          } else if (role === 'teacher') {
            console.log('教师登录，档案完成状态:', profileCompleted)
            // 教师统一跳转到dashboard，由dashboard处理档案完善提醒
            this.$router.push('/teacher/dashboard')
          }
        } else {
          ElMessage.error(response.message || '登录失败')
        }
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error('登录失败，请检查网络连接')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
:root {
  --primary: #5bbcff;
  --secondary: #a084e8;
  --accent: #00cfff;
  --dark: #0f172a;
  --light: #f8fafc;
  --gray: #94a3b8;
}

.login-container {
  min-height: 100vh;
  background: #eaf4ff;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 头部样式 */
.auth-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 60px;
  background: transparent;
}

.header-left .logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.8rem;
  font-weight: 700;
  color: #2563eb;
}

.header-left .logo i {
  font-size: 2rem;
  color: #5bbcff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.btn {
  padding: 10px 24px;
  border-radius: 25px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
  font-size: 0.95rem;
}

.btn-outline {
  background: transparent;
  border: 2px solid #5bbcff;
  color: #2563eb;
}

.btn-outline:hover {
  background: #5bbcff;
  color: white;
}

.btn-primary {
  background: linear-gradient(90deg, #5bbcff, #a084e8);
  color: white;
  border: none;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(106, 90, 249, 0.13);
}

/* 内容区域 */
.login-content {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 100px);
  padding: 40px 20px;
}

.login-card {
  width: 420px;
  max-width: 90vw;
  padding: 48px;
  background: white;
  border-radius: 24px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
}

.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary), var(--accent));
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.login-header h2 {
  font-size: 2rem;
  font-weight: 700;
  color: #1e40af;
  margin-bottom: 12px;
}

.login-header p {
  color: #64748b;
  font-size: 1.1rem;
  margin: 0;
  line-height: 1.6;
}

/* 表单样式 */
.login-form {
  margin-top: 8px;
}

.login-form .el-form-item {
  margin-bottom: 24px;
}

/* 自定义Element Plus样式 */
.login-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: #5bbcff;
  box-shadow: 0 4px 12px rgba(91, 188, 255, 0.1);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #5bbcff;
  box-shadow: 0 0 0 3px rgba(91, 188, 255, 0.1);
}

.login-button {
  width: 100%;
  height: 52px;
  font-size: 1.1rem;
  font-weight: 600;
  border-radius: 26px;
  background: linear-gradient(90deg, #5bbcff, #a084e8);
  border: none;
  margin-top: 8px;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(91, 188, 255, 0.3);
}

.login-footer {
  text-align: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
  color: #64748b;
  font-size: 1rem;
}

.register-link {
  color: #5bbcff;
  text-decoration: none;
  margin-left: 8px;
  font-weight: 600;
  transition: color 0.3s ease;
}

.register-link:hover {
  color: #4aa3ff;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .auth-header {
    flex-direction: column;
    padding: 16px 20px;
    gap: 16px;
  }

  .header-right {
    gap: 12px;
  }

  .btn {
    padding: 8px 16px;
    font-size: 0.9rem;
  }

  .login-card {
    padding: 32px 24px;
    margin: 20px;
  }

  .login-header h2 {
    font-size: 1.6rem;
  }
}
</style>
