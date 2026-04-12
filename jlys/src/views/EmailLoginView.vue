<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
    </div>
    <div class="login-box">
      <div class="logo-section">
        <div class="logo-container">
          <img :src="logoImage" alt="Logo" class="logo-img" />
        </div>
        <h2 class="welcome-text">邮箱登录</h2>
        <p class="subtitle">AI驱动的全流程面试提升平台</p>
      </div>
      <div class="login-form">
        <el-form ref="loginForm" :model="loginForm" size="large">
          <el-form-item>
            <el-input
              v-model="loginForm.email"
              placeholder="请输入邮箱"
              class="email-input"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <div class="slider-container" @mousedown="startDrag">
              <div class="slider-track" :style="{ backgroundColor: trackColor }">
                <div
                  class="slider-thumb"
                  :style="{ left: sliderPosition + '%' }"
                  @mouseup="endDrag"
                >
                  <div class="thumb-icon">→</div>
                </div>
              </div>
              <p class="slider-tip">{{ sliderTip }}</p>
            </div>
          </el-form-item>
          <el-form-item>
            <div class="verification-code-group">
              <el-input
                v-model="loginForm.code"
                placeholder="6 位邮箱验证码"
                maxlength="6"
                show-word-limit
                class="code-input"
              ></el-input>
              <el-button 
                type="text" 
                @click="getVerificationCode"
                class="code-btn"
                :disabled="!isVerificationCodeSent && sliderPosition < 95"
              >
                {{ verificationCodeBtnText }}
              </el-button>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button 
              type="primary" 
              class="login-button" 
              @click="login"
              :loading="isLoading"
            >
              登录 / 注册
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

export default {
  name: 'EmailLoginView',
  data() {
    return {
      logoImage: require('@/assets/images/logo.png'),
      loginForm: {
        email: '',
        code: ''
      },
      sliderPosition: 0,
      sliderTip: '按住滑块，拖动到最右边',
      verificationCodeBtnText: '获取验证码',
      isVerificationCodeSent: false,
      isDragging: false,
      trackColor: '#f0f2f5',
      countdown: 60,
      countdownTimer: null,
      isLoading: false
    }
  },
  methods: {
    startDrag(event) {
      this.isDragging = true
      const sliderTrack = event.currentTarget.querySelector('.slider-track')
      const startX = event.clientX
      const startLeft = this.sliderPosition
      const handleMouseMove = (moveEvent) => {
        if (!this.isDragging) return
        const trackRect = sliderTrack.getBoundingClientRect()
        const trackWidth = trackRect.width
        const currentX = moveEvent.clientX
        const diffX = currentX - startX
        let newLeft = startLeft + (diffX / trackWidth) * 100
        newLeft = Math.max(0, Math.min(100, newLeft))
        this.sliderPosition = newLeft
        if (newLeft > 0) {
          this.trackColor = `linear-gradient(to right, #667eea ${newLeft}%, #f0f2f5 ${newLeft}%)`
        }
      }
      const handleMouseUp = () => {
        if (!this.isDragging) return
        this.isDragging = false
        document.removeEventListener('mousemove', handleMouseMove)
        document.removeEventListener('mouseup', handleMouseUp)
        if (this.sliderPosition >= 95 && !this.isVerificationCodeSent) {
          this.sendVerificationCode()
        } else if (this.sliderPosition < 95) {
          this.sliderPosition = 0
          this.trackColor = '#f0f2f5'
          this.sliderTip = '按住滑块，拖动到最右边'
        }
      }
      document.addEventListener('mousemove', handleMouseMove)
      document.addEventListener('mouseup', handleMouseUp)
    },
    async sendVerificationCode() {
      // 邮箱格式验证
      const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
      if (!emailRegex.test(this.loginForm.email)) {
        ElMessage.error('请输入有效的邮箱')
        return
      }
      try {
        this.isLoading = true
        const response = await request.post('/email/sendCode', {
          email: this.loginForm.email
        })
        if (response.code === 200) {
          this.isVerificationCodeSent = true
          this.sliderTip = '验证码已发送'
          this.sliderPosition = 100
          this.trackColor = '#667eea'
          this.verificationCodeBtnText = '重新发送(60s)'
          ElMessage({
            message: `验证码已发送至 ${this.loginForm.email}`,
            type: 'success',
            duration: 3000,
            showClose: true
          })
          this.startCountdown()
        } else {
          ElMessage.error(response.message || '验证码发送失败')
        }
      } catch (error) {
        ElMessage.error('网络错误，请稍后重试')
      } finally {
        this.isLoading = false
      }
    },
    startCountdown() {
      this.countdown = 60
      this.countdownTimer = setInterval(() => {
        this.countdown--
        this.verificationCodeBtnText = `重新发送(${this.countdown}s)`
        if (this.countdown <= 0) {
          this.resetVerificationCode()
        }
      }, 1000)
    },
    resetVerificationCode() {
      clearInterval(this.countdownTimer)
      this.isVerificationCodeSent = false
      this.sliderPosition = 0
      this.trackColor = '#f0f2f5'
      this.sliderTip = '按住滑块，拖动到最右边'
      this.verificationCodeBtnText = '获取验证码'
    },
    getVerificationCode() {
      if (!this.isVerificationCodeSent) {
        this.sendVerificationCode()
      }
    },
    async login() {
      if (!this.loginForm.email || !this.loginForm.code) {
        ElMessage.error('请输入邮箱和验证码')
        return
      }
      this.isLoading = true
      try {
        const res = await request.post('/email/login', {
          email: this.loginForm.email,
          code: this.loginForm.code
        })
        if (res.code === 200) {
          ElMessage.success('登录成功')
          this.$router.push('/layout/aiChat')
        } else {
          ElMessage.error(res.message || '登录失败')
        }
      } catch (e) {
        ElMessage.error('网络错误')
      } finally {
        this.isLoading = false
      }
    }
  },
  beforeUnmount() {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer)
    }
  }
}
</script>

<style scoped>
.login-container {
  width: 100%;
  min-height: 100vh;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  position: relative;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.bg-circle-1 {
  width: 200px;
  height: 200px;
  top: -100px;
  right: -100px;
  animation: float 6s ease-in-out infinite;
}

.bg-circle-2 {
  width: 150px;
  height: 150px;
  bottom: -75px;
  left: -75px;
  animation: float 8s ease-in-out infinite reverse;
}

.bg-circle-3 {
  width: 100px;
  height: 100px;
  top: 50%;
  right: 10%;
  animation: float 7s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

.login-box {
  width: 420px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 40px;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 10;
  margin: 40px 0;
  box-sizing: border-box;
}

.logo-section {
  text-align: center;
  margin-bottom: 40px;
}

.logo-container {
  margin-bottom: 20px;
}

.logo-img {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.2);
  background: linear-gradient(135deg, #667eea, #764ba2);
  padding: 8px;
}

.welcome-text {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.login-form {
  margin-top: 30px;
}

.phone-input-group {
  display: flex;
  width: 100%;
  margin-bottom: 20px;
}

.country-select {
  width: 80px;
  border-radius: 12px 0 0 12px;
  overflow: hidden;
}

.phone-input {
  flex: 1;
  margin-left: 0;
  border-radius: 0 12px 12px 0;
}

.verification-code-group {
  display: flex;
  width: 100%;
  margin-bottom: 24px;
  gap: 12px;
}

.code-input {
  flex: 1;
}

.code-btn {
  padding: 0 16px;
  border-radius: 12px;
  font-size: 14px;
  color: #667eea;
}

/* 滑块样式 */
.slider-container {
  position: relative;
  width: 100%;
  margin-bottom: 24px;
  user-select: none;
}

.slider-track {
  height: 8px;
  border-radius: 4px;
  background: #f0f2f5;
  position: relative;
  transition: background 0.3s;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1);
}

.slider-thumb {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s;
  z-index: 2;
  cursor: grab;
}

.slider-thumb:active {
  cursor: grabbing;
  transform: translateY(-50%) scale(1.1);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.thumb-icon {
  font-size: 14px;
  font-weight: bold;
}

.slider-tip {
  text-align: center;
  font-size: 13px;
  color: #667eea;
  margin-top: 12px;
  font-weight: 500;
  transition: color 0.3s;
}

.login-button {
  width: 100%;
  height: 48px;
  line-height: 48px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.2);
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.login-button:active {
  transform: translateY(0);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.2);
}

/* 输入框样式 */
.el-input, .el-select {
  border-radius: 12px;
}

.el-input__inner, .el-select .el-input__inner {
  border-radius: 12px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  font-size: 15px;
  padding: 12px 16px;
  transition: all 0.3s;
}

.el-input__inner:focus, .el-select .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.el-form-item {
  margin-bottom: 20px;
}

/* 其他登录方式 */
.other-login {
  margin-top: 32px;
}

.divider-text {
  font-size: 14px;
  color: #666;
  background: rgba(255, 255, 255, 0.95);
  padding: 0 16px;
}

.login-icons {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
}

.login-icon {
  height: 44px;
  border-radius: 22px;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  border: 1px solid #e9ecef;
  background: #f8f9fa;
  color: #666;
}

.login-icon:hover {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border-color: transparent;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.password-login {
  padding: 0 20px;
}

.icon-text {
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 600px) {
  .login-box {
    width: 100%;
    padding: 16px;
    border-radius: 12px;
  }
}

/* 错误状态 */
.el-form-item.is-error .el-input__inner {
  border-color: #f56c6c;
  box-shadow: 0 0 0 3px rgba(245, 108, 108, 0.1);
}

.phone-error-tip, .code-error-tip {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  display: none;
}

.phone-error .phone-error-tip, .code-error .code-error-tip {
  display: block;
}
</style> 