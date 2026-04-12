<template>
  <div class="ai-chat-container">
    <!-- 顶部标题 -->
    <div class="chat-header">
      <div class="header-title">
        <span>欢迎使用</span>
        <span class="highlight">智面Buff</span>
      </div>
      <div class="header-subtitle">与AI智能体进行音频模拟面试，提前体验真实面试场景</div>
      <!-- 新增产品特色描述 -->
      <div class="feature-description">
        多场景AI模拟 · 个性化反馈 · 音频互动 · 真实还原面试流程 · 专业岗位推荐 · 智能分析报告
      </div>
    </div>

    <!-- 面试类型选择 -->
    <div class="interview-type-selector">
      <div class="type-tabs">
        <div 
          class="type-tab sim-tab" 
          :class="{ active: activeType === 'simulation' }"
          @click="startInterview('simulation')"
          @mouseenter="showTooltip = 'simulation'"
          @mouseleave="showTooltip = null"
        >
          <el-icon><Headset /></el-icon>
          <span>模拟面试</span>
          <div class="tab-desc">AI辅助的模拟面试，提供参考答案和实时反馈，帮助您改进表现</div>
        </div>
        <div 
          class="type-tab real-tab" 
          :class="{ active: activeType === 'official' }"
          @click="startInterview('official')"
          @mouseenter="showTooltip = 'official'"
          @mouseleave="showTooltip = null"
        >
          <el-icon><Medal /></el-icon>
          <span>正式面试</span>
          <div class="tab-desc">完全自主答题的正式面试，模拟真实面试环境，测试您的真实能力水平</div>
        </div>
      </div>
    </div>

    <!-- AI问答区域，只保留最新AI回复和输入框 -->
    <div class="chat-main">
      <div class="modern-input-area new-input-area">
        <!-- 新样式：一排圆角按钮 -->
        <div class="feature-btn-list">
          <el-button v-for="(item, idx) in featureBtns" :key="idx" class="feature-btn" plain>
            <el-icon :style="{color: item.color, marginRight: '6px'}"><component :is="item.icon" /></el-icon>
            {{ item.text }}
          </el-button>
        </div>
        <!-- 新样式：输入框带上传图标和发送按钮 -->
        <div class="new-input-card" @click="showCozeDialog = true">
          <el-icon class="upload-icon"><Upload /></el-icon>
          <el-input
            v-model="inputMessage"
            placeholder="我是智面Buff AI助手，随意点击搜索框就能唤醒我哦~~"
            type="text"
            class="new-modern-input"
            @keydown.enter.prevent="sendMessage"
            clearable
          />
          <el-button 
            type="primary" 
            :disabled="!inputMessage.trim()"
            @click="sendMessage"
            class="send-btn new-send-btn"
            circle
          >
            <el-icon><Promotion /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 功能卡片区域 -->
    <div class="function-cards">
      <el-row :gutter="24">
        <el-col :xs="24" :md="8">
          <el-card class="func-card" shadow="hover" @click="goToWrittenExam">
            <div class="card-content">
              <el-icon class="func-icon"><EditPen /></el-icon>
              <div class="func-title">查看笔试记录</div>
              <div class="func-desc">查看历史笔试成绩和详细分析</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :md="8">
          <el-card class="func-card" shadow="hover">
            <div class="card-content">
              <el-icon class="func-icon"><User /></el-icon>
              <div class="func-title">查看面试记录</div>
              <div class="func-desc">回顾面试表现和改进建议</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :md="8">
          <el-card class="func-card" shadow="hover">
            <div class="card-content">
              <el-icon class="func-icon"><Cpu /></el-icon>
              <div class="func-title">职业性格测试记录</div>
              <div class="func-desc">获取专业的性格测试报告</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-dialog
      v-model="showCozeDialog"
      width="1100px"
      top="8vh"
      :close-on-click-modal="false"
      :destroy-on-close="true"
      class="coze-dialog"
      @opened="cozeDialogOpened = true"
      @close="cozeDialogOpened = false"
    >
      <template #title>
        <div style="display: flex; justify-content: space-between; align-items: center; width: 100%;">
          <span>
            智面Buff AI助手
            <span v-if="showBackupChat" style="font-size: 12px; opacity: 0.8;">(备用模式)</span>
          </span>
          <button
            v-if="!showBackupChat"
            @click="showBackupChat = true"
            style="background: #f56c6c; color: white; border: none; padding: 4px 8px; border-radius: 4px; font-size: 12px; cursor: pointer;"
            title="如果输入框无法使用，点击切换到备用模式"
          >
            输入框无法使用？
          </button>
        </div>
      </template>
      <div style="height: 600px; max-height: 70vh; overflow: auto;">
        <BackupChatInput
          v-if="showBackupChat"
          @close="closeCozeDialog"
          @try-original="tryOriginalCoze"
        />
        <CozeAssistant v-else-if="cozeDialogOpened" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>

import { ref } from 'vue'
import { useRouter } from 'vue-router'
import CozeAssistant from './CozeAssistant.vue' // 新增：引入CozeAssistant
import BackupChatInput from './BackupChatInput.vue' // 备用聊天输入组件
import { 
  EditPen, 
  User, 
  Cpu, 
  Headset,
  Medal,
  Promotion, 
  Connection, 
  Upload,
  Compass,
  Edit,
  DataAnalysis
} from '@element-plus/icons-vue'

const router = useRouter()
const activeType = ref(null)
const showTooltip = ref(null)
const inputMessage = ref('')
const showCozeDialog = ref(false) // 控制弹窗显示
const cozeDialogOpened = ref(false) // 控制CozeAssistant渲染时机
const showBackupChat = ref(false) // 控制备用聊天界面显示

// 推荐问题/功能按钮
const featureBtns = [
  { icon: Compass, color: '#1ec9a0', text: '岗位搜索' },
  { icon: DataAnalysis, color: '#3b82f6', text: '简历生成' },
  { icon: Edit, color: '#4f8cff', text: '简历分析' },
  { icon: Connection, color: '#1ec9c9', text: '联网搜索' }
]

// 只保留最新AI回复
const latestAIAnswer = ref('您好！我是智面Buff的AI助手。我可以帮助您进行模拟面试训练，提供面试技巧指导，或者回答您关于面试的任何问题。您想开始哪种类型的面试呢？')

// 开始面试
const startInterview = (type) => {
  activeType.value = type
  router.push(`/layout/beforeExam?type=${type}`)
}

// 打开 Coze 对话框
const openCozeDialog = () => {
  showCozeDialog.value = true
  showBackupChat.value = false
  // 确保在对话框打开后修复输入框交互
  setTimeout(() => {
    fixCozeInputInteraction()
    // 如果 5 秒后仍然无法正常使用，自动切换到备用模式
    setTimeout(() => {
      if (!checkCozeInputWorking()) {
        showBackupChat.value = true
      }
    }, 5000)
  }, 3500)
}

// 关闭对话框
const closeCozeDialog = () => {
  showCozeDialog.value = false
  showBackupChat.value = false
  cozeDialogOpened.value = false
}

// 尝试使用原版 Coze
const tryOriginalCoze = () => {
  showBackupChat.value = false
  cozeDialogOpened.value = true
  fixCozeInputInteraction()
}

// 检查 Coze 输入框是否正常工作
const checkCozeInputWorking = () => {
  const cozeContainer = document.getElementById('coze-chat-container')
  if (!cozeContainer) return false

  const inputs = cozeContainer.querySelectorAll('input, textarea')
  return inputs.length > 0 && Array.from(inputs).some(input => {
    const style = window.getComputedStyle(input)
    return style.pointerEvents !== 'none' && style.display !== 'none'
  })
}

// 修复 Coze 输入框交互问题
const fixCozeInputInteraction = () => {
  const cozeContainer = document.getElementById('coze-chat-container')
  if (cozeContainer) {
    const inputs = cozeContainer.querySelectorAll('input, textarea, button')
    inputs.forEach(element => {
      element.style.pointerEvents = 'auto'
      element.style.zIndex = '2200'
      element.style.position = 'relative'
    })
  }
}

// 发送消息 - 打开 Coze 对话框
const sendMessage = () => {
  openCozeDialog()
}

function goToWrittenExam() {
  router.push('/layout/writeExamPre')
}

</script>

<style scoped>
/* 全局样式，允许滚动 */
:global(html, body, #app) {
  min-height: 100vh;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
  overflow-y: auto;
}

.header-title {
  font-size: 54px; /* 原64px */
  font-weight: 800;
  margin-bottom: 8px; /* 原10px */
  letter-spacing: 2px; /* 原2.5px */
  color: #1a237e;
}

.highlight {
  color: #409eff;
  font-weight: 900;
  margin-left: 8px; /* 原10px */
  text-shadow: 0 2px 14px #409eff33; /* 原16px */
  font-size: 60px; /* 原70px */
}

.header-subtitle {
  font-size: 26px; /* 原30px */
  color: #3b4a6b;
  margin-bottom: 15px; /* 原18px */
  font-weight: 600;
}

/* 新增产品特色描述样式 */
.feature-description {
  font-size: clamp(16px, 2.8vw, 30px); /* 原clamp(18px, 3vw, 34px) */
  font-weight: 900;
  color: #409eff;
  margin-bottom: 30px; /* 原36px */
  letter-spacing: 1.8px; /* 原2px */
  text-align: center;
  line-height: 1.3;
  white-space: nowrap;
}

.ai-chat-container {
  width: 100%;
  height: 100%;
  min-height: 0;
  background: #eaf4ff;
  padding: 28px 8vw 20px 8vw; /* 原32px 10vw 24px 10vw */
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-sizing: border-box;
}

.chat-header {
  text-align: center;
  margin-bottom: 15px; /* 原18px */
  flex-shrink: 0;
  padding-bottom: 0;
}

.interview-type-selector {
  display: flex;
  justify-content: center;
  margin-bottom: 15px; /* 原18px */
  flex-shrink: 0;
}

.type-tabs {
  display: flex;
  background: #fff;
  border-radius: 28px; /* 原32px */
  padding: 18px 42px; /* 原20px 48px */
  box-shadow: 0 6px 24px rgba(64, 158, 255, 0.10); /* 原8px 28px */
  position: relative;
  border: 3px solid #b3d8ff; /* 原4px */
  font-size: 28px; /* 原32px */
}

.type-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px; /* 原8px */
  padding: 22px 52px 15px 52px; /* 原26px 60px 18px 60px */
  border-radius: 20px; /* 原24px */
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  font-weight: 700;
  background: #f6faff;
  font-size: 28px; /* 原32px */
  color: #222 !important;
}
.type-tab .el-icon {
  font-size: 36px; /* 原40px */
  color: #222 !important;
  transition: color 0.2s;
}
.type-tab.active {
  color: #409eff !important;
}
.type-tab.active .el-icon {
  color: #409eff !important;
}
.tab-desc {
  font-size: 14px; /* 原16px */
  color: #1976d2;
  margin-top: 5px; /* 原6px */
  text-align: center;
  font-weight: 500;
  line-height: 1.4;
  min-width: 160px; /* 原180px */
  max-width: 240px; /* 原260px */
  word-break: break-all;
  white-space: normal;
}

.type-tab:hover {
  background: #f0f9ff;
  transform: translateY(-2px);
  box-shadow: 0 3px 10px rgba(64, 158, 255, 0.2); /* 原4px 12px */
}

.type-tab.active {
  background: #f5f7fa;
  color: #409eff;
}

.type-tab.sim-tab.active {
  background: #f5f7fa;
  color: #409eff;
}
.type-tab.sim-tab {
  color: inherit;
  background: #f5f7fa;
}

.type-tab .el-icon {
  font-size: 36px; /* 原40px */
}

.tooltip-trigger {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.chat-main {
  flex: 1 1 auto;
  min-height: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 0;
  overflow: hidden;
}

.modern-input-area {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 10px; /* 原12px */
}

.feature-btn-list {
  width: 100%;
  max-width: 1000px; /* 原1100px */
  display: flex;
  flex-wrap: wrap;
  gap: 20px; /* 原24px */
  margin: 0 auto 24px auto; /* 原28px */
  justify-content: center;
}

.feature-btn {
  background: #fafdff;
  border-radius: 28px; /* 原32px */
  border: 2px solid #b3d8ff;
  color: #1976d2;
  font-size: 20px; /* 原22px */
  padding: 12px 32px; /* 原14px 36px */
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px #e0e3e8; /* 原10px */
  transition: box-shadow 0.2s, background 0.2s;
}

.feature-btn .el-icon {
  font-size: 24px !important; /* 原28px */
}

.feature-btn:hover {
  background: #e3f2fd;
  box-shadow: 0 2px 10px #409eff22; /* 原12px */
}

.new-input-card {
  width: 100%;
  max-width: 1000px; /* 原1100px */
  background: #fff;
  border-radius: 36px; /* 原40px */
  border: 2px solid #b3d8ff;
  display: flex;
  align-items: center;
  padding: 0 24px; /* 原28px */
  min-height: 60px; /* 原70px */
  box-shadow: 0 3px 16px 0 #b3d8ff44; /* 原4px 18px */
  position: relative;
  margin: 0 auto;
}

.upload-icon {
  font-size: 28px; /* 原32px */
  color: #90caf9;
  margin-right: 16px; /* 原18px */
}

.new-modern-input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 22px; /* 原26px */
  min-height: 50px; /* 原60px */
  box-shadow: none;
  outline: none;
  color: #222;
}

.new-modern-input .el-input__wrapper {
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
}

.send-btn.new-send-btn {
  background: #409eff;
  color: #fff;
  border: none;
  box-shadow: 0 2px 10px #409eff22; /* 原12px */
  width: 50px; /* 原56px */
  height: 50px;
  min-width: 50px;
  min-height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px; /* 原32px */
  border-radius: 50%;
  margin-left: 14px; /* 原16px */
  transition: background 0.2s;
}

.send-btn.new-send-btn:disabled {
  background: #e3f0fa;
  color: #b0b0b0;
}

.function-cards {
  margin-top: 28px; /* 原32px */
  flex-shrink: 0;
  overflow: hidden;
}

.func-card {
  border-radius: 18px; /* 原20px */
  border: none;
  transition: all 0.3s ease;
  cursor: pointer;
  min-height: 180px; /* 原200px */
  background: #fff;
  box-shadow: 0 3px 16px 0 #b3d8ff33; /* 原4px 18px */
}

.func-card:hover {
  transform: translateY(-3px); /* 原4px */
  box-shadow: 0 6px 20px #409eff22; /* 原8px 24px */
}

.card-content {
  text-align: center;
  padding: 32px; /* 原36px */
}

.func-icon {
  font-size: 58px; /* 原64px */
  color: #409eff;
  margin-bottom: 20px; /* 原24px */
}

.func-title {
  font-size: 18px; /* 原20px */
  font-weight: 400;
  color: #1a237e;
  margin-bottom: 10px; /* 原12px */
}

.func-desc {
  font-size: 18px; /* 原20px */
  color: #3b4a6b;
  line-height: 1.5;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8px); /* 原10px */
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ai-chat-container {
    padding: 8px 2vw 8px 2vw; /* 原10px */
  }
  
  .header-title {
    font-size: 32px; /* 原36px */
  }
  
  .header-subtitle {
    font-size: 16px; /* 原18px */
  }
  
  .feature-description {
    font-size: 16px; /* 原18px */
  }

  .type-tabs {
    font-size: 16px; /* 原18px */
    padding: 8px 8px; /* 原10px */
  }
  .type-tab {
    font-size: 16px; /* 原18px */
    padding: 8px 16px; /* 原10px 18px */
  }
  .type-tab .el-icon {
    font-size: 20px; /* 原22px */
  }
  .feature-btn {
    font-size: 12px; /* 原14px */
    padding: 12px 32px; /* 原14px 36px */
  }
  .feature-btn .el-icon {
    font-size: 24px !important; /* 原28px */
  }
  .new-input-card {
    min-height: 60px; /* 原40px */
    padding: 0 24px; /* 原8px */
  }
  .upload-icon {
    font-size: 28px; /* 原18px */
    margin-right: 14px; /* 原6px */
  }
  .new-modern-input {
    font-size: 22px; /* 原14px */
    min-height: 50px; /* 原28px */
  }
  .send-btn.new-send-btn {
    width: 48px; /* 原32px */
    height: 48px;
    min-width: 48px;
    min-height: 48px;
    font-size: 28px; /* 原18px */
    margin-left: 12px; /* 原6px */
  }
  .func-card {
    min-height: 100px; /* 原120px */
  }
  .card-content {
    padding: 12px; /* 原16px */
  }
  .func-icon {
    font-size: 28px; /* 原32px */
    margin-bottom: 8px; /* 原10px */
  }
  .func-title {
    font-size: 14px; /* 原16px */
    margin-bottom: 5px; /* 原6px */
  }
  .func-desc {
    font-size: 10px; /* 原12px */
  }
}

.coze-dialog .el-dialog__body {
  padding: 0;
  background: #f8f9fa;
  min-height: 600px;
  max-height: 70vh;
  overflow: auto;
}
</style>