<template>
  <div class="personal-center-root">
    <!-- 选项卡导航 -->
    <div class="tabs-navigation">
      <div 
        class="tab-item" 
        :class="{ active: activeTab === 'profile' }"
        @click="activeTab = 'profile'"
      >
        <el-icon><User /></el-icon>
        <span>个人资料</span>
      </div>
      <div 
        class="tab-item" 
        :class="{ active: activeTab === 'dashboard' }"
        @click="activeTab = 'dashboard'"
      >
        <el-icon><DataAnalysis /></el-icon>
        <span>数据控制台</span>
      </div>
    </div>

    <!-- 个人资料编辑页面 -->
    <div v-show="activeTab === 'profile'" class="personal-card">
      <div class="section-title">
        <h3>个人资料管理</h3>
        <p>完善您的个人信息，提升求职成功率</p>
      </div>
      
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <div class="avatar-section">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            action="#"
          >
            <el-avatar :size="120" :src="form.avatar" class="avatar-img" />
            <div class="avatar-upload-text">点击更换头像</div>
          </el-upload>
        </div>
        <el-form-item label="用户昵称" prop="nickname" required>
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone" required>
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email" required>
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <div class="form-actions">
          <el-button type="primary" @click="onSubmit">保存</el-button>
        </div>
      </el-form>
    </div>

    <!-- 数据控制台页面 -->
    <div v-show="activeTab === 'dashboard'" class="dashboard-wrapper">
      <JobSeekerDashboard />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { User, DataAnalysis } from '@element-plus/icons-vue'
import JobSeekerDashboard from '../AiInterview/ControlPanel/JobSeekerDashboard.vue'

const formRef = ref()
const activeTab = ref('profile') // 默认显示个人资料页面
const form = reactive({
  avatar: require('@/assets/images/personal_info_icon.png'),
  nickname: 'AI小助手',
  phone: '138****8888',
  email: 'aiuser@example.com',
  gender: '男'
})

const rules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

function handleAvatarSuccess(res, file) {
  // 假设返回图片url
  // form.avatar = res.url;
  // 这里用本地预览
  const reader = new FileReader()
  reader.onload = e => {
    form.avatar = e.target.result
  }
  reader.readAsDataURL(file.raw)
}
function beforeAvatarUpload(file) {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJPG) {
    ElMessage.error('只能上传 JPG/PNG 格式图片!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}
function onSubmit() {
  formRef.value.validate((valid) => {
    if (valid) {
      // 提交API
      ElMessage.success('保存成功')
    }
  })
}
function onCancel() {
  // 关闭或重置
  // 可根据实际需求跳转或重置表单
  ElMessage.info('已关闭')
}
</script>

<style scoped>
.personal-center-root {
  width: 100%;
  min-height: 100vh;
  background: #f5f8ff;
  position: relative;
  padding: 20px;
  box-sizing: border-box;
}

/* 选项卡导航样式 */
.tabs-navigation {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  padding: 8px;
  box-shadow: 0 4px 16px rgba(91, 188, 255, 0.1);
  backdrop-filter: blur(10px);
  max-width: 400px;
  margin: 0 auto 30px;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #64748b;
  font-weight: 500;
  background: transparent;
}

.tab-item:hover {
  background: rgba(91, 188, 255, 0.1);
  color: #5bbcff;
  transform: translateY(-1px);
}

.tab-item.active {
  background: linear-gradient(135deg, #5bbcff, #a084e8);
  color: white;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(91, 188, 255, 0.3);
}

.tab-item .el-icon {
  font-size: 18px;
}

.tab-item span {
  font-size: 14px;
  white-space: nowrap;
}
.personal-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 18px;
  box-shadow: 0 4px 18px rgba(91, 188, 255, 0.1);
  padding: 40px;
  max-width: 580px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

/* 页面标题样式 */
.section-title {
  text-align: center;
  margin-bottom: 30px;
  width: 100%;
}

.section-title h3 {
  font-size: 24px;
  font-weight: 700;
  color: #1e40af;
  margin: 0 0 8px 0;
}

.section-title p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* 数据控制台容器 */
.dashboard-wrapper {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
}
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}
.avatar-img {
  border: 3px solid #409eff;
  box-shadow: 0 2px 8px #409eff22;
}
.avatar-uploader {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}
.avatar-upload-text {
  margin-top: 8px;
  color: #409eff;
  font-size: 14px;
}
.user-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.user-name {
  font-size: 22px;
  font-weight: bold;
  color: #222;
}
.user-id {
  font-size: 14px;
  color: #888;
}
.info-list {
  width: 100%;
  margin: 18px 0 0 0;
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.info-item {
  display: flex;
  align-items: center;
  font-size: 16px;
  color: #333;
}
.info-label {
  width: 90px;
  color: #888;
  font-weight: 500;
}
.info-value {
  color: #222;
  font-weight: 600;
}
.form-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 24px;
}
.logout-row {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-top: 18px;
}
.logout-btn {
  font-size: 18px;
  padding: 0 36px;
  border-radius: 8px;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .personal-center-root {
    padding: 10px;
  }
  
  .tabs-navigation {
    margin-bottom: 20px;
    padding: 6px;
  }
  
  .tab-item {
    padding: 10px 16px;
    font-size: 13px;
  }
  
  .tab-item .el-icon {
    font-size: 16px;
  }
  
  .personal-card {
    padding: 24px 20px;
    border-radius: 12px;
  }
  
  .section-title h3 {
    font-size: 20px;
  }
  
  .avatar-section {
    margin-bottom: 20px;
  }
  
  .dashboard-wrapper {
    padding: 0;
  }
}
</style>