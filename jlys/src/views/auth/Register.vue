<template>
  <div class="register-container">
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
        <router-link to="/login" class="btn btn-primary">登录</router-link>
      </div>
    </div>

    <div class="register-content">
      <div class="register-card">
        <div class="register-header">
          <h2>注册账号</h2>
          <p>加入AI面试官系统，开启智能招聘之旅</p>
        </div>

        <el-form
            ref="registerForm"
            :model="registerForm"
            :rules="registerRules"
            class="register-form"
            @submit.prevent="handleRegister"
        >
          <el-form-item prop="role">
            <el-radio-group v-model="registerForm.role" size="large">
              <el-radio label="applicant">学生</el-radio>
              <el-radio label="company">企业用户</el-radio>
              <el-radio label="teacher">教师</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item prop="email">
            <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱"
                size="large"
                prefix-icon="Message"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                prefix-icon="Lock"
                show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                size="large"
                prefix-icon="Lock"
                show-password
            />
          </el-form-item>

          <!-- 学生信息 - 修改为 applicant -->
          <template v-if="registerForm.role === 'student'">
            <el-form-item prop="studentNo">
              <el-input
                  v-model="registerForm.studentNo"
                  placeholder="请输入学号"
                  size="large"
                  prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="realName">
              <el-input
                  v-model="registerForm.realName"
                  placeholder="请输入姓名"
                  size="large"
                  prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="phone">
              <el-input
                  v-model="registerForm.phone"
                  placeholder="请输入手机号"
                  size="large"
                  prefix-icon="Phone"
              />
            </el-form-item>
          </template>

          <!-- 企业信息 -->
          <template v-if="registerForm.role === 'company'">
            <el-form-item prop="companyName">
              <el-input
                  v-model="registerForm.companyName"
                  placeholder="请输入公司名称"
                  size="large"
                  prefix-icon="OfficeBuilding"
              />
            </el-form-item>

            <el-form-item prop="industry">
              <el-input
                  v-model="registerForm.industry"
                  placeholder="请输入所属行业"
                  size="large"
                  prefix-icon="Briefcase"
              />
            </el-form-item>

            <el-form-item prop="scale">
              <el-select
                  v-model="registerForm.scale"
                  placeholder="请选择公司规模"
                  size="large"
                  style="width: 100%"
              >
                <el-option label="1-50人" value="1-50人" />
                <el-option label="51-100人" value="51-100人" />
                <el-option label="101-500人" value="101-500人" />
                <el-option label="500人以上" value="500人以上" />
              </el-select>
            </el-form-item>

            <el-form-item prop="contactPhone">
              <el-input
                  v-model="registerForm.contactPhone"
                  placeholder="请输入联系电话"
                  size="large"
                  prefix-icon="Phone"
              />
            </el-form-item>
          </template>

          <!-- 教师信息 -->
          <template v-if="registerForm.role === 'teacher'">
            <el-form-item prop="teacherNo">
              <el-input
                  v-model="registerForm.teacherNo"
                  placeholder="请输入工号"
                  size="large"
                  prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="realName">
              <el-input
                  v-model="registerForm.realName"
                  placeholder="请输入姓名"
                  size="large"
                  prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="phone">
              <el-input
                  v-model="registerForm.phone"
                  placeholder="请输入手机号"
                  size="large"
                  prefix-icon="Phone"
              />
            </el-form-item>

            <el-form-item prop="roleType">
              <el-select
                  v-model="registerForm.roleType"
                  placeholder="请选择教师角色"
                  size="large"
                  style="width: 100%"
              >
                <el-option label="辅导员" value="counselor" />
                <el-option label="学业导师" value="advisor" />
                <el-option label="管理员" value="admin" />
                <el-option label="领导" value="leader" />
              </el-select>
            </el-form-item>

            <el-form-item prop="collegeId">
              <el-select
                  v-model="registerForm.collegeId"
                  placeholder="请选择所属学院"
                  size="large"
                  style="width: 100%"
              >
                <el-option label="计算机与信息工程学院" :value="1" />
                <el-option label="电子工程学院" :value="2" />
              </el-select>
            </el-form-item>
          </template>

          <el-form-item>
            <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleRegister"
                class="register-button"
            >
              注册
            </el-button>
          </el-form-item>
        </el-form>

        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="login-link">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { authApi } from '@/api/auth'
import { ElMessage } from 'element-plus'

export default {
  name: 'Register',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      registerForm: {
        role: 'student',
        email: '',
        password: '',
        confirmPassword: '',
        // 学生信息
        studentNo: '',
        realName: '',
        phone: '',
        // 企业信息
        companyName: '',
        industry: '',
        scale: '',
        contactPhone: '',
        // 教师信息
        teacherNo: '',
        roleType: '',
        collegeId: null
      },
      registerRules: {
        role: [
          { required: true, message: '请选择用户类型', trigger: 'change' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        studentNo: [
          { required: true, message: '请输入学号', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        companyName: [
          { required: true, message: '请输入公司名称', trigger: 'blur' }
        ],
        industry: [
          { required: true, message: '请输入所属行业', trigger: 'blur' }
        ],
        scale: [
          { required: true, message: '请选择公司规模', trigger: 'change' }
        ],
        contactPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' }
        ],
        teacherNo: [
          { required: true, message: '请输入工号', trigger: 'blur' }
        ],
        roleType: [
          { required: true, message: '请选择教师角色', trigger: 'change' }
        ],
        collegeId: [
          { required: true, message: '请选择所属学院', trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    async handleRegister() {
      try {
        await this.$refs.registerForm.validate()
        this.loading = true

        const response = await authApi.register(this.registerForm)

        if (response.success) {
          ElMessage.success('注册成功，请登录')
          this.$router.push('/login')
        } else {
          ElMessage.error(response.message || '注册失败')
        }
      } catch (error) {
        console.error('注册失败:', error)
      /* ElMessage.error('注册失败，请检查网络连接')*/
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

.register-container {
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
.register-content {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 100px);
  padding: 40px 20px;
}

.register-card {
  width: 480px;
  max-width: 90vw;
  padding: 48px;
  background: white;
  border-radius: 24px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
}

.register-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary), var(--accent));
}

.register-header {
  text-align: center;
  margin-bottom: 36px;
}

.register-header h2 {
  font-size: 2rem;
  font-weight: 700;
  color: #1e40af;
  margin-bottom: 12px;
}

.register-header p {
  color: #64748b;
  font-size: 1.1rem;
  margin: 0;
  line-height: 1.6;
}

/* 表单样式 */
.register-form {
  margin-top: 8px;
}

.register-form .el-form-item {
  margin-bottom: 24px;
}

.register-form .el-radio-group {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  margin-bottom: 8px;
}

.register-form .el-radio {
  margin-right: 40px;
  font-weight: 500;
}

.register-form .el-radio:last-child {
  margin-right: 0;
}

/* 自定义Element Plus样式 */
.register-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.register-form :deep(.el-input__wrapper:hover) {
  border-color: #5bbcff;
  box-shadow: 0 4px 12px rgba(91, 188, 255, 0.1);
}

.register-form :deep(.el-input__wrapper.is-focus) {
  border-color: #5bbcff;
  box-shadow: 0 0 0 3px rgba(91, 188, 255, 0.1);
}

.register-form :deep(.el-select .el-input__wrapper) {
  border-radius: 12px;
}

.register-button {
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

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(91, 188, 255, 0.3);
}

.register-footer {
  text-align: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
  color: #64748b;
  font-size: 1rem;
}

.login-link {
  color: #5bbcff;
  text-decoration: none;
  margin-left: 8px;
  font-weight: 600;
  transition: color 0.3s ease;
}

.login-link:hover {
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

  .register-card {
    padding: 32px 24px;
    margin: 20px;
  }

  .register-header h2 {
    font-size: 1.6rem;
  }

  .register-form .el-radio-group {
    flex-direction: column;
    gap: 12px;
  }

  .register-form .el-radio {
    margin-right: 0;
  }
}
</style>
