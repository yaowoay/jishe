<template>
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
                  <el-icon><OfficeBuilding /></el-icon>
                </div>
                <div class="user-info">
                  <div class="user-name">{{ userInfo.companyName || '企业' }}</div>
                  <div class="user-role">企业用户</div>
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
            <el-menu-item index="/company/CompanyVisual">
              <el-icon size="18"><House /></el-icon>
              <span class="menu-text">可视化面板</span>
            </el-menu-item>
            <el-menu-item index="/company/dashboard">
              <el-icon size="18"><House /></el-icon>
              <span class="menu-text">企业控制台</span>
            </el-menu-item>


            <el-menu-item index="/company/jobs">
              <el-icon size="18"><Briefcase /></el-icon>
              <span class="menu-text">职位管理</span>
            </el-menu-item>

            <el-menu-item index="/company/applicants">
              <el-icon size="18"><User /></el-icon>
              <span class="menu-text">应聘者管理</span>
            </el-menu-item>

            <el-menu-item index="/company/interviews">
              <el-icon size="18"><ChatDotRound /></el-icon>
              <span class="menu-text">面试管理</span>
            </el-menu-item>
            <el-menu-item index="/company/profile">
              <el-icon size="18"><OfficeBuilding /></el-icon>
              <span class="menu-text">企业中心</span>
            </el-menu-item>
          </el-menu>
          <div class="aside-spacer"></div>
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
                <el-breadcrumb-item :to="{ path: '/company/dashboard' }">
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
                <el-badge :value="2" class="notification-badge">
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
                      企业设置
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
  </div>
</template>

<script>
import {
  House,
  OfficeBuilding,
  Briefcase,
  User,
  ChatDotRound,
  ArrowDown,
  Setting,
  Bell,
  QuestionFilled,
  SwitchButton
} from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { getCompanyProfile } from '@/api/company'  // ✅ 导入API
export default {
  name: 'CompanyLayout',
  components: {
    House,
    OfficeBuilding,
    Briefcase,
    User,
    ChatDotRound,
    ArrowDown,
    Setting,
    Bell,
    QuestionFilled,
    SwitchButton
  },
  data() {
    return {
      userInfo: {},
      isSidebarVisible: false,
      profileCompletion: 0,
      showProfileTip: false      // 是否显示完善信息提示

    }
  },
  computed: {
    currentPageTitle() {
      const routeMap = {
        '/company/dashboard': '企业控制台',
        '/company/profile': '企业中心',
        '/company/jobs': '职位管理',
        '/company/applicants': '应聘者管理',
        '/company/interviews': '面试管理',
        '/company/CompanyVisual': '可视化面板'
      }
      return routeMap[this.$route.path] || '页面'
    },
    activeMenuIndex() {
      const path = this.$route.path.split('?')[0]
      return path
    }
  },
  mounted() {
    this.loadCompanyProfile()  // ✅ 页面加载时获取企业信息
  },
  methods: {
    // ✅ 加载企业信息，计算完整度
    async loadCompanyProfile() {
      try {
        const response = await getCompanyProfile()
        if (response && response.success && response.data && response.data.profile) {
          const profile = response.data.profile

          // 计算档案完整度
          let score = 0
          if (profile.companyName && profile.companyName.trim()) score += 20
          if (profile.industry && profile.industry.trim()) score += 20
          if (profile.scale && profile.scale.trim()) score += 20
          if (profile.contactPhone && profile.contactPhone.trim()) score += 20
          if (profile.address && profile.address.trim()) score += 7
          if (profile.website && profile.website.trim()) score += 7
          if (profile.description && profile.description.trim()) score += 6

          this.profileCompletion = Math.min(100, Math.round(score))
          // 完整度低于60显示提示
          this.showProfileTip = this.profileCompletion < 60
        } else {
          this.profileCompletion = 0
          this.showProfileTip = true
        }
      } catch (error) {
        console.error('加载企业信息失败:', error)
        this.profileCompletion = 0
        this.showProfileTip = true
      }
    },

    showSidebar() {
      this.isSidebarVisible = true
    },
    hideSidebar() {
      this.isSidebarVisible = false
    },
    goToProfile() {
      this.$router.push('/company/profile')
    },
    handleCommand(command) {
      switch (command) {
      case 'profile':
        this.$router.push('/company/profile')
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
  background: linear-gradient(135deg, #f5f9ff 0%, #e0efff 100%);
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
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border-right: 1px solid #e6f1ff;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 企业用户信息卡片 */
.aside-user-section {
  padding: 20px 16px;
  border-bottom: 1px solid #e6f1ff;
}

.aside-user-card {
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #f0f7ff 0%, #e0efff 100%);
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
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
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

/* 企业菜单样式 */
.aside-menu {
  flex: 1;
  border: none;
  padding: 0 8px;
  overflow-y: auto !important;
  overflow-x: hidden !important;
}

:deep(.aside-menu .el-menu-item) {
  height: 48px;
  margin: 4px 0;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.aside-menu .el-menu-item:hover) {
  background: linear-gradient(135deg, #e0f0ff 0%, #d0e6ff 100%);
}

:deep(.aside-menu .el-menu-item.is-active) {
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
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

.aside-spacer {
  flex: 1;
}

/* 顶部导航 */
.header {
  height: 64px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
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
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  color: white;
  transform: translateY(-2px);
}

.notification-badge :deep(.el-badge__content) {
  background: linear-gradient(135deg, #e74c3c 0%, #ff7979 100%);
  border: 2px solid white;
}

.user-dropdown-menu {
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(26, 111, 196, 0.15);
  border: 1px solid #e6f1ff;
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
  overflow-y: auto !important;  /* 强制滚动 */
  overflow-x: hidden !important;
  height: calc(100vh - 64px) !important;
  max-height: calc(100vh - 64px) !important;
  flex: 1;
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
  background: linear-gradient(135deg, #1a6fc4 0%, #36c2cf 100%);
  border-radius: 3px;
}

.main-content::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #0d4a89 0%, #2aa3b8 100%);
}

/* 动画效果 */
.aside-menu :deep(.el-menu-item) {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.action-btn {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 焦点状态 */
:deep(.aside-menu .el-menu-item:focus) {
  outline: 2px solid rgba(26, 111, 196, 0.2);
  outline-offset: 2px;
}

/* 企业特色样式 */
.user-avatar {
  background: linear-gradient(135deg, #1a6fc4 0%, #2c5282 100%);
}

:deep(.aside-menu .el-menu-item.is-active) {
  background: linear-gradient(135deg, #1a6fc4 0%, #2c5282 100%);
}

.action-btn:hover {
  background: linear-gradient(135deg, #1a6fc4 0%, #2c5282 100%);
}

/* 企业主题色调整 */
:deep(.breadcrumb .el-breadcrumb__inner.is-link) {
  color: #2c5282;
}

:deep(.breadcrumb .el-breadcrumb__inner:hover) {
  color: #2c5282;
}

.dropdown-item:hover {
  color: #2c5282;
}
</style>

