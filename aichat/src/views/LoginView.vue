<template>
  <div class="login-container">
    <div class="login-box">
      <div class="logo-box">
        <img :src="logoImage" alt="Logo" class="logo-img" />
      </div>
      <div class="login-form">
        <el-form ref="loginForm" :model="loginForm" size="large">
          <el-form-item>
            <div class="phone-input-group">
              <el-select v-model="loginForm.countryCode" class="country-select">
                <el-option label="+86" value="+86"></el-option>
              </el-select>
              <el-input
                v-model="loginForm.phoneNumber"
                placeholder="请输入手机号码"
                class="phone-input"
              ></el-input>
            </div>
          </el-form-item>
          <el-form-item>
            <div class="slider-container" @mousedown="startDrag">
              <div class="slider-track" :style="{ backgroundColor: trackColor }">
                <div
                  class="slider-thumb"
                  :style="{ left: sliderPosition + '%' }"
                  @mouseup="endDrag"
                ></div>
              </div>
              <p class="slider-tip">{{ sliderTip }}</p>
            </div>
          </el-form-item>
          <el-form-item>
            <div class="verification-code-group">
              <el-input
                v-model="loginForm.verificationCode"
                placeholder="6 位短信验证码"
                maxlength="6"
                show-word-limit
              ></el-input>
              <el-button type="text" @click="getVerificationCode">{{ verificationCodeBtnText }}</el-button>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-button" @click="login">登录 / 注册</el-button>
          </el-form-item>
          <div class="other-login">
            <el-divider content-position="center">其他登录方式</el-divider>
            <div class="login-icons">
              <el-button type="primary" plain class="login-icon password-login">密码登录</el-button>
              <el-button type="primary" plain class="login-icon">wx</el-button>
              <el-button type="primary" plain class="login-icon">zfb</el-button>
              <el-button type="primary" plain class="login-icon">邮箱</el-button>
            </div>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginView',
  data() {
    return {
      logoImage: require('@/assets/images/logo.png'), // 确保logo图片存在
      loginForm: {
        countryCode: '+86',
        phoneNumber: '', // 现在电话号码为空，由用户输入
        verificationCode: ''
      },
      sliderPosition: 0,
      sliderTip: '按住滑块，拖动到最右边',
      verificationCodeBtnText: '获取验证码',
      isVerificationCodeSent: false,
      isDragging: false,
      trackColor: '#e0e0e0',
      countdown: 60,
      countdownTimer: null
    }
  },
  methods: {
    startDrag(event) {
      this.isDragging = true;
      const sliderTrack = event.currentTarget.querySelector('.slider-track');
      const startX = event.clientX;
      const startLeft = this.sliderPosition;

      const handleMouseMove = (moveEvent) => {
        if (!this.isDragging) return;

        const trackRect = sliderTrack.getBoundingClientRect();
        const trackWidth = trackRect.width;
        const currentX = moveEvent.clientX;
        const diffX = currentX - startX;

        let newLeft = startLeft + (diffX / trackWidth) * 100;
        newLeft = Math.max(0, Math.min(100, newLeft));

        this.sliderPosition = newLeft;
        if (newLeft > 0) {
          this.trackColor = `linear-gradient(to right, #4caf50 ${newLeft}%, #e0e0e0 ${newLeft}%)`;
        }
      };

      const handleMouseUp = () => {
        if (!this.isDragging) return;

        this.isDragging = false;
        document.removeEventListener('mousemove', handleMouseMove);
        document.removeEventListener('mouseup', handleMouseUp);

        if (this.sliderPosition >= 95 && !this.isVerificationCodeSent) {
          this.sendVerificationCode();
        } else if (this.sliderPosition < 95) {
          this.sliderPosition = 0;
          this.trackColor = '#e0e0e0';
          this.sliderTip = '按住滑块，拖动到最右边';
        }
      };

      document.addEventListener('mousemove', handleMouseMove);
      document.addEventListener('mouseup', handleMouseUp);
    },
    sendVerificationCode() {
      this.isVerificationCodeSent = true;
      this.sliderTip = '验证码已发送';
      this.sliderPosition = 100;
      this.trackColor = '#4caf50';
      this.verificationCodeBtnText = '重新发送(60s)';
      
      // 显示醒目的提示
      this.showProminentNotification();

      // 开始倒计时
      this.countdown = 60;
      this.countdownTimer = setInterval(() => {
        this.countdown--;
        this.verificationCodeBtnText = `重新发送(${this.countdown}s)`;

        if (this.countdown <= 0) {
          this.resetVerificationCode();
        }
      }, 1000);
    },
    resetVerificationCode() {
      clearInterval(this.countdownTimer);
      this.isVerificationCodeSent = false;
      this.sliderPosition = 0;
      this.trackColor = '#e0e0e0';
      this.sliderTip = '按住滑块，拖动到最右边';
      this.verificationCodeBtnText = '获取验证码';
    },
    getVerificationCode() {
      if (!this.isVerificationCodeSent) {
        this.sendVerificationCode();
      }
    },
    login() {
      // 登录逻辑
      console.log('登录/注册');
    },
    showProminentNotification() {
      this.$message({
        message: '验证码已发送，请注意查收',
        type: 'success',
        duration: 3000,
        showClose: true,
        center: true
      });
    }
  },
  beforeUnmount() {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer);
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-box {
  width: 400px;
  background-color: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.logo-box {
  text-align: center;
  margin-bottom: 30px;
}

.logo-img {
  border: 1px solid #000;
  padding: 10px;
  display: inline-block;
  height: 40px;
}

.phone-input-group {
  display: flex;
  width: 100%;
}

.country-select {
  width: 80px;
}

.el-select {
  width: 80px;
}

.phone-input {
  flex: 1;
  margin-left: 10px;
}

.verification-code-group {
  display: flex;
  width: 100%;
}

.slider-container {
  position: relative;
  width: 100%;
  cursor: grab;
}

.slider-track {
  height: 30px;
  border-radius: 15px;
  position: relative;
  transition: background-color 0.3s;
}

.slider-thumb {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 30px;
  height: 30px;
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 50%;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
}

.slider-tip {
  text-align: center;
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.login-button {
  width: 100%;
  background-color: #8cd5ca;
  border-color: #8cd5ca;
}

.login-button:hover {
  background-color: #79c9be;
  border-color: #79c9be;
}

.other-login {
  margin-top: 30px;
}

.login-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.login-icon {
  width: 60px;
  height: 36px;
  border-radius: 18px;
  font-size: 14px;
}

.password-login {
  border-color: #409EFF;
  color: #409EFF;
}

.password-login:hover {
  background-color: #ecf5ff;
  color: #3a8ee6;
  border-color: #3a8ee6;
}

.el-divider__text {
  font-size: 14px;
  color: #303133;
}
</style>