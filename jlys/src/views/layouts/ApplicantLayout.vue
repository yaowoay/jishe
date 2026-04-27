<template>
  <!-- 原有模板内容不变 -->
  <div class="layout-container">
    <el-container>
      <div
          class="sidebar-wrapper"
          @mouseenter="showSidebar()"
          @mouseleave="hideSidebar()"
          :style="{ width: isSidebarVisible ? '240px' : '10px' }"
      >
        <div class="hover-trigger"></div>
        <el-aside
            v-if="isSidebarVisible"
            width="240px"
            class="aside"
        >
          <!-- 用户信息卡片 -->
          <div class="aside-user-section">
            <el-card shadow="hover" class="aside-user-card" @click="goToProfile">
              <div class="user-center-box">
                <div class="user-avatar">
                  <el-icon><User /></el-icon>
                </div>
                <div class="user-info">
                  <div class="user-name">{{ userInfo.fullName || '用户' }}</div>
                  <div class="user-role">学生</div>
                </div>
              </div>
            </el-card>
          </div>
          <el-menu
              :default-active="activeMenuIndex"
              class="aside-menu"
              background-color="#fff"
              text-color="#333"
              active-text-color="#409eff"
              router
              :collapse="false"
              :collapse-transition="false"
          >
            <!-- 首页 -->
            <el-menu-item index="/applicant/dashboard">
              <el-icon size="18"><House /></el-icon>
              <span class="menu-text">首页</span>
            </el-menu-item>

            <!-- 简历管理 -->
            <el-sub-menu index="resume">
              <template #title>
                <el-icon size="18"><Document /></el-icon>
                <span class="menu-text">简历管理</span>
              </template>
              <el-menu-item index="/applicant/resume/list">
                <el-icon size="18"><FolderOpened /></el-icon>
                <span class="submenu-text">我的简历</span>
              </el-menu-item>
              <el-menu-item index="/applicant/resume/analysis">
                <el-icon size="18"><DataAnalysis /></el-icon>
                <span class="submenu-text">简历分析</span>
              </el-menu-item>
              <el-menu-item index="/applicant/resume/match-analysis">
                <el-icon size="18"><DataAnalysis /></el-icon>
                <span class="submenu-text">简历匹配分析</span>
              </el-menu-item>
              <el-menu-item index="/applicant/resume/submit">
                <el-icon size="18"><Promotion /></el-icon>
                <span class="submenu-text">投递简历</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 求职管理 -->
            <el-sub-menu index="job-management">
              <template #title>
                <el-icon size="18"><Briefcase /></el-icon>
                <span class="menu-text">求职管理</span>
              </template>
              <el-menu-item index="/applicant/intelligent-recommend">
                <el-icon size="18"><MagicStick /></el-icon>
                <span class="submenu-text">智能推荐</span>
              </el-menu-item>
              <!-- <el-menu-item index="/applicant/job-list">
                <el-icon size="18"><Search /></el-icon>
                <span class="submenu-text">职位搜索</span>
              </el-menu-item> -->
              <el-menu-item index="/applicant/my-collections">
                <el-icon size="18"><Star /></el-icon>
                <span class="submenu-text">我的收藏</span>
              </el-menu-item>
              <el-menu-item index="/applicant/interview-center">
                <el-icon size="18"><VideoPlay /></el-icon>
                <span class="submenu-text">面试中心</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- AI训练营 -->
            <el-sub-menu index="ai-training">
              <template #title>
                <el-icon size="18"><ChatDotRound /></el-icon>
                <span class="menu-text">AI训练营</span>
              </template>
              <el-menu-item index="/applicant/interview">
                <el-icon size="18"><VideoPlay /></el-icon>
                <span class="submenu-text">模拟面试</span>
              </el-menu-item>
              <el-menu-item index="/applicant/professional-test">
                <el-icon size="18"><Edit /></el-icon>
                <span class="submenu-text">专业笔试</span>
              </el-menu-item>
              <el-menu-item index="/applicant/personality-test">
                <el-icon size="18"><User /></el-icon>
                <span class="submenu-text">性格测评</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 数据分析 -->
            <el-sub-menu index="data-analysis">
              <template #title>
                <el-icon size="18"><DataAnalysis /></el-icon>
                <span class="menu-text">数据分析</span>
              </template>
              <el-menu-item index="/applicant/salary-predict">
                <el-icon size="18"><TrendCharts /></el-icon>
                <span class="submenu-text">薪资预测</span>
              </el-menu-item>
              <el-menu-item index="/applicant/salary-analysis">
                <el-icon size="18"><TrendCharts /></el-icon>
                <span class="submenu-text">薪资分析</span>
              </el-menu-item>
              <el-menu-item index="/applicant/company-analysis">
                <el-icon size="18"><OfficeBuilding /></el-icon>
                <span class="submenu-text">企业分析</span>
              </el-menu-item>
              <el-menu-item index="/applicant/skill-rules">
                <el-icon size="18"><Link /></el-icon>
                <span class="submenu-text">技能关联</span>
              </el-menu-item>
              <el-menu-item index="/applicant/behavior-analysis">
                <el-icon size="18"><DataAnalysis /></el-icon>
                <span class="submenu-text">行为分析</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 预警中心 -->
            <el-menu-item index="/applicant/warnings">
              <el-icon size="18"><Warning /></el-icon>
              <span class="menu-text">预警中心</span>
            </el-menu-item>

            <!-- 学生活动 -->
            <el-sub-menu index="student-activities">
              <template #title>
                <el-icon size="18"><Calendar /></el-icon>
                <span class="menu-text">活动中心</span>
              </template>
              <el-menu-item index="/applicant/activities">
                <el-icon size="18"><Calendar /></el-icon>
                <span class="submenu-text">活动列表</span>
              </el-menu-item>
              <el-menu-item index="/applicant/my-activities">
                <el-icon size="18"><FolderOpened /></el-icon>
                <span class="submenu-text">我的活动</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 工具箱 -->
            <el-sub-menu index="tools">
              <template #title>
                <el-icon size="18"><Notebook /></el-icon>
                <span class="menu-text">工具箱</span>
              </template>
              <el-menu-item index="/applicant/notes">
                <el-icon size="18"><Notebook /></el-icon>
                <span class="submenu-text">AI笔记</span>
              </el-menu-item>
              <el-menu-item index="/applicant/visual-screen">
                <el-icon size="18"><Monitor /></el-icon>
                <span class="submenu-text">数据大屏</span>
              </el-menu-item>
              <el-menu-item index="/applicant/ApplicationVisual">
                <el-icon size="18"><Monitor /></el-icon>
                <span class="submenu-text">应聘可视化</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 个人设置 -->
            <el-menu-item index="/applicant/profile" class="menu-item-bottom">
              <el-icon size="18"><Setting /></el-icon>
              <span class="menu-text">个人设置</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
      </div>
      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <div class="page-title">
              <h2>{{ currentPageTitle }}</h2>
              <el-breadcrumb separator="/" class="breadcrumb">
                <el-breadcrumb-item :to="{ path: '/applicant/dashboard' }">
                  <el-icon><House /></el-icon>
                  首页
                </el-breadcrumb-item>
                <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
              </el-breadcrumb>
            </div>
          </div>
          <div class="header-right">
            <div class="header-actions">
              <el-tooltip content="通知" placement="bottom">
                <el-badge :value="3" class="notification-badge">
                  <el-button circle class="action-btn">
                    <el-icon><Bell /></el-icon>
                  </el-button>
                </el-badge>
              </el-tooltip>
              <el-tooltip content="帮助" placement="bottom">
                <el-button circle class="action-btn">
                  <el-icon><QuestionFilled /></el-icon>
                </el-button>
              </el-tooltip>
              <el-dropdown @command="handleCommand" class="user-dropdown">
                <el-button circle class="action-btn">
                  <el-icon><Setting /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu class="user-dropdown-menu">
                    <el-dropdown-item command="profile" class="dropdown-item">
                      <el-icon><Setting /></el-icon>
                      个人设置
                    </el-dropdown-item>
                    <el-dropdown-item command="logout" divided class="dropdown-item logout">
                      <el-icon><SwitchButton /></el-icon>
                      退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        <!-- 主要内容 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>

    <!-- 学生端 Coze 悬浮按钮 -->
    <div class="coze-float-btn-wrap">
      <el-tooltip content="数智通途AI助手" placement="left">
        <el-button class="coze-float-btn" circle type="primary" @click="showAIAssistant">
          <el-icon><ChatDotRound /></el-icon>
        </el-button>
      </el-tooltip>
    </div>

    <!-- Coze 弹窗 -->
    <el-dialog
      v-model="assistantDialogVisible"
      width="1100px"
      top="9vh"
      :close-on-click-modal="false"
      :destroy-on-close="true"
      class="coze-dialog"
    >
      <template #title>
        <span>数智通途AI助手</span>
      </template>
      <div style="height: 900px; max-height: 70vh; overflow: auto;">
        <CozeAssistant v-if="assistantDialogVisible" />
      </div>
    </el-dialog>
  </div>
</template>
<script>
// 原有脚本内容不变
import {
  House,
  User,
  Document,
  ChatDotRound,
  DataAnalysis,
  ArrowDown,
  Upload,
  Promotion,
  FolderOpened,
  VideoPlay,
  Edit,
  Setting,
  Bell,
  QuestionFilled,
  SwitchButton,
  Monitor,
  Notebook,
  Briefcase,
  Search,
  Star,
  TrendCharts,
  OfficeBuilding,
  Link,
  MagicStick,
  Warning,
  Calendar
} from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import CozeAssistant from '@/components/interview/CozeAssistant.vue'

export default {
  name: 'ApplicantLayout',
  components: {
    House,
    User,
    Document,
    ChatDotRound,
    DataAnalysis,
    ArrowDown,
    Upload,
    Promotion,
    FolderOpened,
    VideoPlay,
    Edit,
    Setting,
    Bell,
    QuestionFilled,
    SwitchButton,
    Monitor,
    Notebook,
    Briefcase,
    Search,
    Star,
    TrendCharts,
    OfficeBuilding,
    Link,
    MagicStick,
    Warning,
    Calendar,
    CozeAssistant
  },
  data() {
    return {
      userInfo: {},
      isSidebarVisible: false,
      assistantDialogVisible: false
    }
  },
  computed: {
    currentPageTitle() {
      const path = this.$route.path
      const routeMap = {
        '/applicant/dashboard': '控制台',
        '/applicant/profile': '个人设置',
        '/applicant/resume/list': '我的简历',
        '/applicant/resume/analysis': '简历分析',
        '/applicant/resume/match-analysis': '简历匹配分析',
        '/applicant/resume/submit': '投递简历',
        '/applicant/resume/generation': 'AI生成简历',
        '/applicant/interview': 'AI模拟面试',
        '/applicant/interview-center': '面试中心',
        '/applicant/professional-test': '专业题笔试',
        '/applicant/personality-test': '职业性格测评',
        '/applicant/ApplicationVisual': '可视化面板',
        '/applicant/notes': 'AI笔记',
        '/applicant/salary-analysis': '薪资维度分析',
        '/applicant/company-analysis': '招聘企业分析',
        '/applicant/skill-rules': '关联规则分析',
        '/applicant/intelligent-recommend': '智能推荐系统',
        '/applicant/job-list': '职位列表',
        '/applicant/salary-predict': '薪资预测',
        '/applicant/my-collections': '我的收藏',
        '/applicant/behavior-analysis': '行为分析',
        '/applicant/warnings': '预警中心',
        '/applicant/activities': '活动列表',
        '/applicant/my-activities': '我的活动',
        '/applicant/visual-screen': '可视化大屏'
      }

      // 处理动态路由
      if (path.startsWith('/applicant/resume/editor')) {
        return this.$route.params.id ? '编辑简历' : '创建简历'
      }
      if (path.startsWith('/applicant/resume/share')) {
        return '简历分享'
      }

      return routeMap[path] || '页面'
    },
    activeMenuIndex() {
      const path = this.$route.path.split('?')[0]
      return path
    }
  },
  methods: {
    showSidebar() {
      this.isSidebarVisible = true
    },
    hideSidebar() {
      this.isSidebarVisible = false
    },
    goToProfile() {
      this.$router.push('/applicant/profile')
    },
    showAIAssistant() {
      this.assistantDialogVisible = true
    },
    handleCommand(command) {
      switch (command) {
      case 'profile':
        this.$router.push('/applicant/profile')
        break
      case 'logout':
        this.handleLogout()
        break
      }
    },
    async handleLogout() {
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        this.$store.dispatch('logout')
        this.$router.push('/login')
      } catch (error) {
        // 用户取消
      }
    }
  }
}
</script>
<style scoped>
.layout-container {
  height: 100vh;
  background: #f5f9ff;
  overflow: hidden;
}

/* 侧边栏区域 */
.sidebar-wrapper {
  position: relative;
  height: 100vh;
  transition: all 0.3s ease;
  z-index: 100;
  box-shadow: 2px 0 10px rgba(26, 111, 196, 0.1);
}

.hover-trigger {
  position: absolute;
  top: 0;
  left: 0;
  width: 10px;
  height: 100%;
  z-index: 10;
  cursor: pointer;
}

.aside {
  height: 100vh;
  background: #ffffff;
  border-right: 1px solid #e6f1ff;
  /* 👇 修改这里：允许滚动 */
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
}
/* 👇 新增：美化滚动条 */
.aside::-webkit-scrollbar {
  width: 6px;
}
/* 用户信息卡片 */
.aside-user-section {
  padding: 20px 16px;
  border-bottom: 1px solid #e6f1ff;
  flex-shrink: 0;  /* 👈 不压缩，固定显示 */
}

.aside-user-card {
  border: none;
  border-radius: 12px;
  background: #f0f7ff;
  cursor: pointer;
  transition: all 0.3s ease;
}

.aside-user-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(26, 111, 196, 0.2);
}

:deep(.aside-user-card .el-card__body) {
  padding: 16px;
}

.user-center-box {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: #1a6fc4;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.user-info {
  flex: 1;
}

.user-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
  font-size: 16px;
}

.user-role {
  color: #5a84b3;
  font-size: 12px;
  font-weight: 500;
}

/* 菜单区域 - 可滚动 */
.aside-menu {
  flex: 1;  /* 👈 占据剩余空间 */
  display: flex;
  flex-direction: column;
  border: none;
  padding: 0 8px;
  /* 👇 确保菜单内部也可以滚动 */
  overflow-y: visible;
}

:deep(.aside-menu .menu-item-bottom) {
  margin-top: auto;
}

:deep(.aside-menu .el-menu-item),
:deep(.aside-menu .el-sub-menu__title) {
  height: 48px;
  margin: 4px 0;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.aside-menu .el-menu-item:hover),
:deep(.aside-menu .el-sub-menu__title:hover) {
  background: #e0f0ff;
}

:deep(.aside-menu .el-menu-item.is-active) {
  background: #1a6fc4;
  color: white !important;
}

:deep(.aside-menu .el-menu-item.is-active .el-icon),
:deep(.aside-menu .el-menu-item.is-active .menu-text) {
  color: white !important;
}

:deep(.aside-menu .el-icon) {
  color: #5a84b3;
  font-size: 18px;
}

.menu-text {
  font-weight: 500;
  color: #2c3e50;
  font-size: 14px;
}

.submenu-text {
  font-weight: 500;
  color: #5a84b3;
  font-size: 13px;
}

:deep(.aside-menu .el-sub-menu .el-menu-item) {
  height: 40px;
  margin: 2px 0;
}

/* 顶部导航 */
.header {
  height: 64px;
  background: #ffffff;
  border-bottom: 1px solid #e6f1ff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 10px rgba(26, 111, 196, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
}

.breadcrumb {
  margin-top: 4px;
}

:deep(.breadcrumb .el-breadcrumb__item) {
  font-size: 12px;
}

:deep(.breadcrumb .el-breadcrumb__inner) {
  color: #5a84b3;
  font-weight: 500;
}

:deep(.breadcrumb .el-breadcrumb__inner:hover) {
  color: #1a6fc4;
}

:deep(.breadcrumb .el-breadcrumb__inner.is-link) {
  color: #1a6fc4;
}

.header-right {
  display: flex;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.action-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: #f0f7ff;
  color: #1a6fc4;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: #1a6fc4;
  color: white;
  transform: translateY(-2px);
}

.notification-badge :deep(.el-badge__content) {
  background: #e74c3c;
  border: 2px solid white;
}

.user-dropdown-menu {
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.15);
  border: 1px solid #e6f1ff;
}

/* Coze 悬浮按钮 */
.coze-float-btn-wrap {
  position: fixed;
  right: 28px;
  bottom: 28px;
  z-index: 2000;
}

.coze-float-btn {
  width: 54px;
  height: 54px;
  box-shadow: 0 10px 24px rgba(64, 158, 255, 0.35);
  transition: all 0.25s ease;
}

.coze-float-btn:hover {
  transform: translateY(-2px) scale(1.03);
  box-shadow: 0 14px 30px rgba(64, 158, 255, 0.45);
}

.coze-float-btn :deep(.el-icon) {
  font-size: 22px;
}

/* Coze 弹窗样式 */
.coze-dialog :deep(.el-dialog) {
  border-radius: 12px;
}

.coze-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid #e5e7eb;
  padding: 16px 24px;
}

.coze-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
}

.coze-dialog :deep(.el-dialog__body) {
  padding: 24px;
}


.dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  font-weight: 500;
  color: #2c3e50;
  transition: all 0.3s ease;
}

.dropdown-item:hover {
  background: #f0f7ff;
  color: #1a6fc4;
}

.dropdown-item.logout {
  color: #e74c3c;
}

.dropdown-item.logout:hover {
  background: #fee;
  color: #e74c3c;
}

/* 主内容区域 */
.main-content {
  padding: 24px;
  background: transparent;
  overflow-y: auto;     /* 关键：内容区域滚动 */
  height: calc(100vh - 64px);  /* 减去头部高度 */
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar-wrapper {
    position: fixed;
    left: 0;
    top: 0;
    z-index: 1000;
  }

  .header {
    padding: 0 16px;
  }

  .page-title h2 {
    font-size: 18px;
  }

  .header-actions {
    gap: 4px;
  }

  .action-btn {
    width: 32px;
    height: 32px;
  }

  .main-content {
    padding: 16px;
    background: transparent;
    overflow-y: auto;
    overflow-x: hidden;/* 内容区域自己滚动 */
    height: 100vh;
  }

  .breadcrumb {
    display: none;
  }
}

/* 滚动条样式 */
.main-content::-webkit-scrollbar {
  width: 6px;
}

.main-content::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.main-content::-webkit-scrollbar-thumb {
  background: #1a6fc4;
  border-radius: 3px;
}

.main-content::-webkit-scrollbar-thumb:hover {
  background: #0d4a89;
}

/* 动画效果 */
.aside-menu :deep(.el-menu-item),
.aside-menu :deep(.el-sub-menu__title) {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.action-btn {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 焦点状态 */
:deep(.aside-menu .el-menu-item:focus),
:deep(.aside-menu .el-sub-menu__title:focus) {
  outline: 2px solid rgba(26, 111, 196, 0.2);
  outline-offset: 2px;
}
</style>
