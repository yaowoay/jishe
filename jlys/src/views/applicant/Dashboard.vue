<template>
  <!-- 档案完善引导弹窗 -->
  <ProfileGuideDialog
    v-model="showProfileGuide"
    @complete="handleCompleteProfile"
    @later="handleLaterProfile"
  />
  
  <!-- 原有模板内容不变 -->
  <div class="ai-dashboard-container">
    <!-- 顶部标题 -->
    <div class="dashboard-header">
      <div class="header-title">
        <span>欢迎使用</span>
        <span class="highlight">高校学生就业能力智配平台</span>
      </div>
      <div class="header-subtitle">您的专属校园就业智配平台，助力求职成功</div>
      <!-- 产品特色描述 -->
      <div class="feature-description">
        大数据简历洞察 · AI多模态面试 · 个性化反馈 · 职业性格测评 · 专业笔试训练 · 就业数据分析
      </div>
    </div>
    <!-- 数据统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="24">
        <el-col :xs="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.resumeCount }}</div>
              <div class="stat-label">简历数量</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.interviewCount }}</div>
              <div class="stat-label">面试次数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><TrophyBase /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.averageScore }}</div>
              <div class="stat-label">平均分数</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><Briefcase /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.applicationCount }}</div>
              <div class="stat-label">投递职位</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 功能卡片区域 -->
    <div class="function-cards">
      <el-row :gutter="24">
        <el-col :xs="24" :md="8">
          <el-card class="func-card" shadow="hover" @click="$router.push('/applicant/resume/analysis')">
            <div class="card-content">
              <el-icon class="func-icon"><Document /></el-icon>
              <div class="func-title">大数据简历工坊</div>
              <div class="func-desc">基于大数据的简历生成和优化建议</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :md="8">
          <el-card class="func-card" shadow="hover" @click="$router.push('/applicant/interview')">
            <div class="card-content">
              <el-icon class="func-icon"><VideoPlay /></el-icon>
              <div class="func-title">AI多模态面试</div>
              <div class="func-desc">真实面试场景模拟和智能反馈</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :md="8">
          <el-card class="func-card" shadow="hover" @click="$router.push('/applicant/personality-test')">
            <div class="card-content">
              <el-icon class="func-icon"><User /></el-icon>
              <div class="func-title">职业性格测评</div>
              <div class="func-desc">专业性格测试和职业建议</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

  </div>
</template>
<script>
// 原有脚本内容不变
import {
  Document,
  ChatDotRound,
  TrophyBase,
  Briefcase,
  User,
  VideoPlay
} from '@element-plus/icons-vue'
import ProfileGuideDialog from '@/components/ProfileGuideDialog.vue'

export default {
  name: 'ApplicantDashboard',
  components: {
    Document,
    ChatDotRound,
    TrophyBase,
    Briefcase,
    User,
    VideoPlay,
    ProfileGuideDialog
  },
  data() {
    return {
      showProfileGuide: false,
      stats: {
        resumeCount: 0,
        interviewCount: 0,
        averageScore: 0,
        applicationCount: 0
      }

    }
  },
  mounted() {
    this.loadStats()
    this.checkProfileStatus()
  },
  methods: {
    checkProfileStatus() {
      // 检查用户角色和档案完成状态
      const userRole = this.$store.getters.userRole
      const profileCompleted = this.$store.getters.profileCompleted
      
      console.log('检查档案状态:', { userRole, profileCompleted })
      
      // 只有学生角色且档案未完善时才显示弹窗
      if (userRole === 'student' && !profileCompleted) {
        // 延迟显示弹窗，让页面先加载完成
        setTimeout(() => {
          this.showProfileGuide = true
        }, 1000)
      }
    },
    
    handleCompleteProfile() {
      // 跳转到档案完善页面
      this.$router.push('/student/profile-complete')
    },
    
    handleLaterProfile() {
      // 用户选择稍后再说，不做任何操作
      console.log('用户选择稍后完善档案')
    },
    async loadStats() {
      // TODO: 从API加载统计数据
      this.stats = {
        resumeCount: 3,
        interviewCount: 8,
        averageScore: 85,
        applicationCount: 12
      }
    }

  }
}
</script>
<style scoped>
/* 基础容器样式 */
.ai-dashboard-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

/* 顶部标题区域 */
.dashboard-header {
  margin-bottom: 32px;
}
.header-title {
  font-size: 28px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 8px;
}
.header-title .highlight {
  color: #3b82f6; /* 主蓝色，匹配招聘工作台风格 */
}
.header-subtitle {
  font-size: 16px;
  color: #4e5969;
  margin-bottom: 16px;
}
.feature-description {
  font-size: 14px;
  color: #6b7785;
  background-color: #ffffff;
  padding: 12px 16px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

/* 数据统计卡片区域 */
.stats-section {
  margin-bottom: 32px;
}
.stat-card {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
}
.stat-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background-color: #eff6ff; /* 浅蓝色背景 */
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}
.stat-icon el-icon {
  font-size: 24px;
  color: #3b82f6; /* 主蓝色图标 */
}
.stat-content .stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 4px;
}
.stat-content .stat-label {
  font-size: 14px;
  color: #6b7785;
}

/* AI助手输入区域 */
.ai-input-section {
  margin-bottom: 32px;
}
.modern-input-area {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}
.feature-btn-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}
.feature-btn {
  border-color: #dbeafe;
  color: #3b82f6;
}
.feature-btn:hover {
  background-color: #eff6ff;
  border-color: #3b82f6;
}
.new-input-card {
  display: flex;
  align-items: center;
  gap: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px 16px;
  background-color: #f9fafb;
}
.upload-icon {
  color: #6b7785;
  font-size: 20px;
}
.new-modern-input {
  flex: 1;
  border: none;
  background-color: transparent;
  font-size: 14px;
}
.new-modern-input:focus {
  outline: none;
}
.new-send-btn {
  background-color: #3b82f6;
  border-color: #3b82f6;
}
.new-send-btn:hover {
  background-color: #2563eb;
  border-color: #2563eb;
}

/* 功能卡片区域 */
.function-cards {
  margin-bottom: 24px;
}
.func-card {
  border-radius: 12px;
  border: none;
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  height: 100%;
}
.func-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}
.card-content {
  padding: 24px;
  display: flex;
  flex-direction: column;
}
.func-icon {
  font-size: 32px;
  color: #3b82f6;
  margin-bottom: 16px;
}
.func-title {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 8px;
}
.func-desc {
  font-size: 14px;
  color: #6b7785;
  flex: 1;
}

/* AI助手对话框样式 */
.coze-dialog {
  border-radius: 12px;
}
.coze-dialog .el-dialog__header {
  border-bottom: 1px solid #e5e7eb;
  padding: 16px 24px;
}
.coze-dialog .el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
}
.coze-dialog .el-dialog__body {
  padding: 24px;
}
</style>