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
        <el-aside v-if="isSidebarVisible" width="240px" class="aside">
          <!-- 用户信息卡片 -->
          <div class="aside-user-section">
            <el-card shadow="hover" class="aside-user-card" @click="goToProfile">
              <div class="user-center-box">
                <div class="user-avatar">
                  <el-icon><User /></el-icon>
                </div>
                <div class="user-info">
                  <div class="user-name">{{ userInfo.realName || '教师' }}</div>
                  <div class="user-role">教师</div>
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
            <el-menu-item index="/teacher/dashboard">
              <el-icon size="18"><House /></el-icon>
              <span class="menu-text">工作台</span>
            </el-menu-item>

            <el-sub-menu index="student">
              <template #title>
                <el-icon size="18"><User /></el-icon>
                <span class="menu-text">学生管理</span>
              </template>
              <el-menu-item index="/teacher/students">
                <el-icon size="18"><FolderOpened /></el-icon>
                <span class="submenu-text">学生列表</span>
              </el-menu-item>
              <el-menu-item index="/teacher/students/warning">
                <el-icon size="18"><Warning /></el-icon>
                <span class="submenu-text">预警学生</span>
              </el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="employment">
              <template #title>
                <el-icon size="18"><Briefcase /></el-icon>
                <span class="menu-text">就业管理</span>
              </template>
              <el-menu-item index="/teacher/employment">
                <el-icon size="18"><DocumentCopy /></el-icon>
                <span class="submenu-text">就业台账</span>
              </el-menu-item>
              <el-menu-item index="/teacher/employment/stats">
                <el-icon size="18"><DataAnalysis /></el-icon>
                <span class="submenu-text">就业统计</span>
              </el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="company">
              <template #title>
                <el-icon size="18"><Building /></el-icon>
                <span class="menu-text">企业管理</span>
              </template>
              <el-menu-item index="/teacher/companies">
                <el-icon size="18"><FolderOpened /></el-icon>
                <span class="submenu-text">企业列表</span>
              </el-menu-item>
              <el-menu-item index="/teacher/jobs">
                <el-icon size="18"><Promotion /></el-icon>
                <span class="submenu-text">职位审核</span>
              </el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="activity">
              <template #title>
                <el-icon size="18"><Calendar /></el-icon>
                <span class="menu-text">活动管理</span>
              </template>
              <el-menu-item index="/teacher/activities">
                <el-icon size="18"><FolderOpened /></el-icon>
                <span class="submenu-text">活动列表</span>
              </el-menu-item>
              <el-menu-item index="/teacher/activities/create">
                <el-icon size="18"><Plus /></el-icon>
                <span class="submenu-text">创建活动</span>
              </el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="assistance">
              <template #title>
                <el-icon size="18"><Help /></el-icon>
                <span class="menu-text">帮扶管理</span>
              </template>
              <el-menu-item index="/teacher/assistance">
                <el-icon size="18"><FolderOpened /></el-icon>
                <span class="submenu-text">帮扶记录</span>
              </el-menu-item>
              <el-menu-item index="/teacher/assistance/create">
                <el-icon size="18"><Plus /></el-icon>
                <span class="submenu-text">新增帮扶</span>
              </el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="cooperation">
              <template #title>
                <el-icon size="18"><Share /></el-icon>
                <span class="menu-text">校企合作</span>
              </template>
              <el-menu-item index="/teacher/cooperation">
                <el-icon size="18"><FolderOpened /></el-icon>
                <span class="submenu-text">合作申请</span>
              </el-menu-item>
            </el-sub-menu>

            <el-menu-item index="/teacher/profile">
              <el-icon size="18"><Setting /></el-icon>
              <span class="menu-text">个人中心</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
      </div>

      <el-container class="main-container">
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <el-icon class="menu-toggle" @click="toggleSidebar"><Menu /></el-icon>
            <span class="system-title">教师院校端管理系统</span>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                {{ userInfo.realName || '用户' }}
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 主内容区 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import {
  User, House, Document, FolderOpened, Edit, Upload, DataAnalysis,
  Promotion, Setting, Menu, ArrowDown, Briefcase, DocumentCopy,
  Building, Calendar, Plus, Help, Share, Warning
} from '@element-plus/icons-vue'

const router = useRouter()
const store = useStore()

const isSidebarVisible = ref(false)
const userInfo = ref({})
const activeMenuIndex = ref('/teacher/dashboard')

const showSidebar = () => {
  isSidebarVisible.value = true
}

const hideSidebar = () => {
  isSidebarVisible.value = false
}

const toggleSidebar = () => {
  isSidebarVisible.value = !isSidebarVisible.value
}

const goToProfile = () => {
  router.push('/teacher/profile')
}

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/teacher/profile')
  } else if (command === 'logout') {
    store.commit('logout')
    router.push('/login')
    ElMessage.success('已退出登录')
  }
}

onMounted(() => {
  userInfo.value = store.state.user || {}
  activeMenuIndex.value = router.currentRoute.value.path
})
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;

  .el-container {
    height: 100%;
    display: flex;
  }

  .sidebar-wrapper {
    transition: width 0.3s ease;
    overflow: hidden;
    background-color: #f5f7fa;
    border-right: 1px solid #e4e7eb;

    .hover-trigger {
      position: absolute;
      width: 10px;
      height: 100%;
      cursor: pointer;
    }

    .aside {
      height: 100vh;
      overflow-y: auto;
      background-color: #fff;

      .aside-user-section {
        padding: 20px 10px;
        border-bottom: 1px solid #e4e7eb;

        .aside-user-card {
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
          }

          .user-center-box {
            display: flex;
            align-items: center;
            gap: 12px;

            .user-avatar {
              width: 40px;
              height: 40px;
              border-radius: 50%;
              background-color: #409eff;
              display: flex;
              align-items: center;
              justify-content: center;
              color: white;
              font-size: 20px;
            }

            .user-info {
              flex: 1;
              min-width: 0;

              .user-name {
                font-weight: 600;
                color: #333;
                font-size: 14px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
              }

              .user-role {
                color: #909399;
                font-size: 12px;
                margin-top: 4px;
              }
            }
          }
        }
      }

      .aside-menu {
        border: none;
        padding: 10px 0;

        .menu-text,
        .submenu-text {
          margin-left: 8px;
        }
      }
    }
  }

  .main-container {
    flex: 1;
    display: flex;
    flex-direction: column;

    .header {
      background-color: #fff;
      border-bottom: 1px solid #e4e7eb;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 20px;
      height: 60px;

      .header-left {
        display: flex;
        align-items: center;
        gap: 16px;

        .menu-toggle {
          cursor: pointer;
          font-size: 20px;
          color: #333;

          &:hover {
            color: #409eff;
          }
        }

        .system-title {
          font-size: 16px;
          font-weight: 600;
          color: #333;
        }
      }

      .header-right {
        .el-dropdown-link {
          cursor: pointer;
          color: #409eff;
          display: flex;
          align-items: center;
          gap: 4px;

          &:hover {
            color: #66b1ff;
          }
        }
      }
    }

    .main-content {
      flex: 1;
      overflow-y: auto;
      background-color: #f5f7fa;
      padding: 20px;
    }
  }
}
</style>
