<template>
  <!-- 企业档案完善引导弹窗 -->
  <ProfileGuideDialog
    v-model="showProfileGuide"
    title="完善企业信息"
    description="完善企业信息可获得更多优质人才推荐，提升招聘效果。"
    @complete="handleCompleteProfile"
    @later="handleLaterProfile"
  />
  
  <div class="ai-dashboard-container">
    <!-- 顶部标题 -->
    <div class="dashboard-header">
      <div class="header-title">
        <span>欢迎使用</span>
        <span class="highlight">高校学生就业能力智配平台</span>
      </div>
      <div class="header-subtitle">企业智能招聘管理平台，数据驱动精准招聘</div>
      <!-- 产品特色描述 -->
      <div class="feature-description">
        大数据职位发布 · AI简历筛选 · 在线面试管理 · 人才评估分析 · 招聘数据统计 · 企业品牌展示
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="24">
        <el-col :xs="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><Briefcase /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.jobCount }}</div>
              <div class="stat-label">发布职位</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.applicantCount }}</div>
              <div class="stat-label">应聘者</div>
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
              <div class="stat-label">面试进行中</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :md="6">
          <div class="stat-card">
            <div class="stat-icon">
              <el-icon><Select /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.hiredCount }}</div>
              <div class="stat-label">已录用</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 快捷操作区域 -->
    <div class="ai-input-section">
      <div class="modern-input-area">
        <!-- 功能按钮列表 -->
        <div class="feature-btn-list">
          <el-button v-for="(item, idx) in featureBtns" :key="idx" class="feature-btn" plain @click="handleFeatureClick(item)">
            <el-icon :style="{color: item.color, marginRight: '6px'}"><component :is="item.icon" /></el-icon>
            {{ item.text }}
          </el-button>
        </div>

        <!-- 企业招聘概览 -->
        <div class="company-overview-card">
          <div class="overview-header">
            <el-icon class="overview-icon"><TrendCharts /></el-icon>
            <h3>招聘数据概览</h3>
          </div>
          <div class="overview-content">
            <div class="overview-item">
              <div class="overview-label">本月新增应聘者</div>
              <div class="overview-value">{{ monthlyStats.newApplicants }}</div>
            </div>
            <div class="overview-item">
              <div class="overview-label">面试通过率</div>
              <div class="overview-value">{{ monthlyStats.passRate }}%</div>
            </div>
            <div class="overview-item">
              <div class="overview-label">平均招聘周期</div>
              <div class="overview-value">{{ monthlyStats.avgCycle }}天</div>
            </div>
            <div class="overview-item">
              <div class="overview-label">活跃职位</div>
              <div class="overview-value">{{ monthlyStats.activeJobs }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 功能卡片区域 -->
    <div class="function-cards">
      <el-row :gutter="24">
        <el-col :xs="24" :md="8">
          <el-card class="func-card" shadow="hover" @click="$router.push('/company/jobs')">
            <div class="card-content">
              <el-icon class="func-icon"><Briefcase /></el-icon>
              <div class="func-title">职位管理</div>
              <div class="func-desc">发布和管理招聘职位信息</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :md="8">
          <el-card class="func-card" shadow="hover" @click="$router.push('/company/applicants')">
            <div class="card-content">
              <el-icon class="func-icon"><UserFilled /></el-icon>
              <div class="func-title">应聘者管理</div>
              <div class="func-desc">查看和筛选应聘者简历</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :md="8">
          <el-card class="func-card" shadow="hover" @click="$router.push('/company/interviews')">
            <div class="card-content">
              <el-icon class="func-icon"><VideoCamera /></el-icon>
              <div class="func-title">面试管理</div>
              <div class="func-desc">安排和管理面试流程</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 快速操作提示 -->
    <div class="quick-tips-section">
      <el-card class="tips-card" shadow="hover">
        <template #header>
          <div class="tips-header">
            <el-icon><InfoFilled /></el-icon>
            <span>快速操作指南</span>
          </div>
        </template>
        <div class="tips-content">
          <div class="tip-item">
            <el-icon class="tip-icon"><Plus /></el-icon>
            <span>点击"发布职位"快速创建新的招聘岗位</span>
          </div>
          <div class="tip-item">
            <el-icon class="tip-icon"><DataAnalysis /></el-icon>
            <span>查看"数据分析"了解招聘效果和趋势</span>
          </div>
          <div class="tip-item">
            <el-icon class="tip-icon"><Setting /></el-icon>
            <span>在"企业设置"中完善公司信息和招聘偏好</span>
          </div>
          <div class="tip-item">
            <el-icon class="tip-icon"><TrendCharts /></el-icon>
            <span>定期查看"招聘报告"优化招聘策略</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import {
  Briefcase,
  User,
  ChatDotRound,
  Select,
  UserFilled,
  VideoCamera,
  Plus,
  DataAnalysis,
  Setting,
  TrendCharts,
  InfoFilled
} from '@element-plus/icons-vue'
import ProfileGuideDialog from '@/components/ProfileGuideDialog.vue'
import { getCompanyProfile } from '@/api/company'  // ✅ 导入API
export default {
  name: 'CompanyDashboard',
  components: {
    Briefcase,
    User,
    ChatDotRound,
    Select,
    UserFilled,
    VideoCamera,
    Plus,
    DataAnalysis,
    Setting,
    TrendCharts,
    InfoFilled,
    ProfileGuideDialog
  },
  data() {
    return {
      showProfileGuide: false,
      profileCompletion: 0,  // ✅ 添加完整度
      stats: {
        jobCount: 0,
        applicantCount: 0,
        interviewCount: 0,
        hiredCount: 0
      },
      monthlyStats: {
        newApplicants: 0,
        passRate: 0,
        avgCycle: 0,
        activeJobs: 0
      },
      featureBtns: [
        { icon: 'Plus', color: '#1a6fc4', text: '发布职位', route: '/company/jobs' },
        { icon: 'DataAnalysis', color: '#1a6fc4', text: '数据分析', route: '/company/analytics' },
        { icon: 'Setting', color: '#1a6fc4', text: '企业设置', route: '/company/profile' },
        { icon: 'TrendCharts', color: '#1a6fc4', text: '招聘报告', route: '/company/reports' }
      ]
    }
  },
  async mounted() {
    this.loadStats()
    await this.checkProfileStatus()  // ✅ 改为异步
  },
  methods: {
    // ✅ 修改检查档案状态的方法
    async checkProfileStatus() {
      try {
        const response = await getCompanyProfile()

        console.log('获取到的企业信息:', response)

        if (response && response.success && response.data) {
          // ✅ 直接使用 response.data，不需要 .profile
          const profile = response.data

          // ✅ 前端自己计算完整度，不依赖后端
          let score = 0

          // 基础信息（每个20分，共4个=80分）
          if (profile.companyName && profile.companyName.trim() && profile.companyName !== '待完善') {
            score += 20
            console.log('公司名称已填写，+20')
          }
          if (profile.industry && profile.industry.trim() && profile.industry !== '未填写') {
            score += 20
            console.log('行业已填写，+20')
          }
          if (profile.scale && profile.scale.trim() && profile.scale !== '未填写') {
            score += 20
            console.log('规模已填写，+20')
          }
          if (profile.contactPhone && profile.contactPhone.trim()) {
            score += 20
            console.log('联系电话已填写，+20')
          }

          // 详细信息（加分项）
          if (profile.address && profile.address.trim() && profile.address !== '未填写') {
            score += 7
            console.log('地址已填写，+7')
          }
          if (profile.website && profile.website.trim()) {
            score += 7
            console.log('网站已填写，+7')
          }
          if (profile.description && profile.description.trim() && profile.description !== '未填写') {
            score += 6
            console.log('简介已填写，+6')
          }

          // 额外加分项
          if (profile.companyWelfare && profile.companyWelfare.trim()) {
            score += 3
            console.log('福利已填写，+3')
          }
          if (profile.companyTags && profile.companyTags.trim()) {
            score += 2
            console.log('标签已填写，+2')
          }
          if (profile.logoUrl && profile.logoUrl.trim()) {
            score += 2
            console.log('Logo已上传，+2')
          }

          this.profileCompletion = Math.min(100, Math.round(score))

          console.log('计算得到的完整度:', this.profileCompletion)
          console.log('当前得分明细:', {
            公司名称: profile.companyName,
            行业: profile.industry,
            规模: profile.scale,
            电话: profile.contactPhone,
            网站: profile.website,
            得分: score,
            最终完整度: this.profileCompletion
          })

          // 完整度低于60显示弹窗
          if (this.profileCompletion < 60) {
            // 检查用户是否在24小时内点击过"稍后再说"
            const dismissed = localStorage.getItem('profileGuideDismissed')
            const dismissedTime = parseInt(dismissed)
            const hoursPassed = dismissed ? (Date.now() - dismissedTime) / (1000 * 60 * 60) : 0

            if (!dismissed || hoursPassed >= 24) {
              console.log('完整度不足60%，显示弹窗')
              setTimeout(() => {
                this.showProfileGuide = true
              }, 1000)
            } else {
              console.log('用户在24小时内选择了稍后再说，不显示弹窗')
              this.showProfileGuide = false
            }
          } else {
            console.log('完整度充足，不显示弹窗')
            this.showProfileGuide = false
          }
        } else {
          console.log('没有获取到企业信息')
          setTimeout(() => {
            this.showProfileGuide = true
          }, 1000)
        }
      } catch (error) {
        console.error('获取企业信息失败:', error)
        setTimeout(() => {
          this.showProfileGuide = true
        }, 1000)
      }
    },
    handleCompleteProfile() {
      // 跳转到企业档案完善页面
      this.$router.push('/company/profile-complete')
    },
    
    handleLaterProfile() {
      // 用户选择稍后再说，不做任何操作
      console.log('用户选择稍后完善企业档案')
    },
    async loadStats() {
      // TODO: 从API加载统计数据
      this.stats = {
        jobCount: 5,
        applicantCount: 28,
        interviewCount: 12,
        hiredCount: 8
      }

      // 加载月度统计数据
      this.monthlyStats = {
        newApplicants: 45,
        passRate: 68,
        avgCycle: 15,
        activeJobs: 5
      }
    },

    handleFeatureClick(item) {
      if (item.route) {
        this.$router.push(item.route)
      }
    }
  }
}
</script>

<style scoped>
.ai-dashboard-container {
  padding: 0;
  background: #f5f9ff;
  min-height:auto
}

/* 顶部标题区域 */
.dashboard-header {
  background: #ffffff;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(26, 111, 196, 0.1);
  border: 1px solid #e6f1ff;
}

.header-title {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.highlight {
  color: #1a6fc4;
}

.header-subtitle {
  font-size: 16px;
  color: #5a84b3;
  margin-bottom: 16px;
}

.feature-description {
  font-size: 14px;
  color: #7a9bc8;
  padding-top: 12px;
  border-top: 1px solid #e6f1ff;
}

/* 数据统计卡片 */
.stats-section {
  margin-bottom: 24px;
}

.stat-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  height: 100%;
  box-shadow: 0 2px 8px rgba(26, 111, 196, 0.1);
  border: 1px solid #e6f1ff;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(26, 111, 196, 0.15);
}

.stat-icon {
  width: 54px;
  height: 54px;
  border-radius: 12px;
  background: #f0f7ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: #1a6fc4;
  font-size: 24px;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #5a84b3;
  margin-top: 4px;
}

/* 快捷操作区域 */
.ai-input-section {
  margin-bottom: 24px;
}

.modern-input-area {
  background: #ffffff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(26, 111, 196, 0.1);
  border: 1px solid #e6f1ff;
}

.feature-btn-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
}

.feature-btn {
  border-radius: 6px;
  padding: 10px 16px;
  font-weight: 500;
  border: 1px solid #e6f1ff;
  background: #f8fbff;
  color: #1a6fc4;
  transition: all 0.3s ease;
}

.feature-btn:hover {
  background: #e0f0ff;
  border-color: #c5e1ff;
  transform: translateY(-2px);
}

/* 企业招聘概览卡片 */
.company-overview-card {
  background: #f8fbff;
  border-radius: 8px;
  padding: 20px;
  border: 1px solid #e6f1ff;
}

.overview-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  color: #2c3e50;
}

.overview-icon {
  margin-right: 10px;
  color: #1a6fc4;
  font-size: 20px;
}

.overview-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.overview-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.overview-item {
  background: #ffffff;
  padding: 12px 16px;
  border-radius: 6px;
  border: 1px solid #e6f1ff;
}

.overview-label {
  font-size: 13px;
  color: #5a84b3;
  margin-bottom: 6px;
}

.overview-value {
  font-size: 20px;
  font-weight: 700;
  color: #2c3e50;
}

/* 功能卡片区域 */
.function-cards {
  margin-bottom: 24px;
}

.func-card {
  border-radius: 8px;
  border: 1px solid #e6f1ff;
  transition: all 0.3s ease;
  cursor: pointer;
}

.func-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(26, 111, 196, 0.15) !important;
}

:deep(.func-card .el-card__body) {
  padding: 24px;
}

.card-content {
  text-align: center;
}

.func-icon {
  width: 54px;
  height: 54px;
  border-radius: 12px;
  background: #f0f7ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  color: #1a6fc4;
  font-size: 24px;
}

.func-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.func-desc {
  font-size: 14px;
  color: #5a84b3;
}

/* 快速操作提示 */
.tips-card {
  border-radius: 8px;
  border: 1px solid #e6f1ff;
}

:deep(.tips-card .el-card__header) {
  background: #f8fbff;
  border-bottom: 1px solid #e6f1ff;
  padding: 16px 20px;
}

.tips-header {
  display: flex;
  align-items: center;
  color: #2c3e50;
  font-weight: 600;
}

.tips-header .el-icon {
  margin-right: 8px;
  color: #1a6fc4;
}

.tips-content {
  padding: 8px 0;
}

.tip-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f7ff;
}

.tip-item:last-child {
  border-bottom: none;
}

.tip-icon {
  margin-right: 12px;
  color: #1a6fc4;
  font-size: 16px;
}

.tip-item span {
  color: #5a84b3;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-header {
    padding: 16px;
  }

  .header-title {
    font-size: 24px;
  }

  .header-subtitle {
    font-size: 14px;
  }

  .feature-description {
    font-size: 12px;
  }

  .stat-icon {
    width: 44px;
    height: 44px;
    font-size: 20px;
    margin-right: 12px;
  }

  .stat-number {
    font-size: 24px;
  }

  .feature-btn-list {
    gap: 8px;
  }

  .feature-btn {
    padding: 8px 12px;
    font-size: 13px;
  }

  .overview-content {
    grid-template-columns: 1fr;
  }

  .func-icon {
    width: 44px;
    height: 44px;
    font-size: 20px;
  }

  .func-title {
    font-size: 16px;
  }

  .func-desc {
    font-size: 13px;
  }
}
</style>