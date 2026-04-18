<template>
  <div class="notes-container">
    <!-- 头部工具栏 -->
    <div class="header-section">
      <div class="header-left">
        <h1 class="page-title">AI 笔记</h1>
        <div class="header-actions">
          <el-button type="primary" @click="showAddDialog" class="new-note-btn">
            <el-icon><Plus /></el-icon>
            新建笔记
          </el-button>
          <el-button @click="showAIChat" class="ai-chat-btn" type="info">
            <el-icon><ChatDotRound /></el-icon>
            AI助手
          </el-button>
        </div>
      </div>
      <div class="header-right">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索笔记..."
            class="search-input"
            @keyup.enter="handleSearch"
            clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 筛选标签栏 -->
    <div class="filter-bar">
      <div class="filter-tabs">
        <span
            class="filter-tab"
            :class="{ active: selectedCategory === '' }"
            @click="handleCategoryChange('')"
        >
          全部笔记
        </span>
        <span
            class="filter-tab"
            :class="{ active: selectedCategory === '技术笔记' }"
            @click="handleCategoryChange('技术笔记')"
        >
          技术笔记
        </span>
        <span
            class="filter-tab"
            :class="{ active: selectedCategory === '面试准备' }"
            @click="handleCategoryChange('面试准备')"
        >
          面试准备
        </span>
        <span
            class="filter-tab"
            :class="{ active: selectedCategory === '学习心得' }"
            @click="handleCategoryChange('学习心得')"
        >
          学习心得
        </span>
        <span
            class="filter-tab"
            :class="{ active: selectedCategory === '工作总结' }"
            @click="handleCategoryChange('工作总结')"
        >
          工作总结
        </span>
      </div>
      <div class="view-options">
        <!-- 排序选择 -->
        <el-select
            v-model="sortOption"
            placeholder="排序方式"
            @change="handleSortChange"
            style="width: 140px; margin-right: 12px;"
            size="small"
        >
          <el-option label="最新更新" value="updateTime-desc" />
          <el-option label="最早更新" value="updateTime-asc" />
          <el-option label="最新创建" value="createTime-desc" />
          <el-option label="最早创建" value="createTime-asc" />
          <el-option label="标题A-Z" value="title-asc" />
          <el-option label="标题Z-A" value="title-desc" />
        </el-select>

        <!-- 视图切换 -->
        <el-button-group>
          <el-button :type="viewMode === 'grid' ? 'primary' : ''" @click="viewMode = 'grid'">
            <el-icon><Grid /></el-icon>
          </el-button>
          <el-button :type="viewMode === 'list' ? 'primary' : ''" @click="viewMode = 'list'">
            <el-icon><List /></el-icon>
          </el-button>
        </el-button-group>
      </div>
    </div>

    <!-- 笔记内容区域 -->
    <div class="notes-content">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <div class="notes-grid">
          <el-skeleton-item
              v-for="i in 6"
              :key="i"
              variant="rect"
              class="skeleton-card"
          />
        </div>
      </div>

      <!-- 笔记网格布局 -->
      <div v-else-if="notesList.length > 0" :class="['notes-grid', viewMode]">
        <div
            v-for="note in notesList"
            :key="note.id"
            class="note-card"
            @click="viewNote(note)"
        >
          <!-- 笔记卡片头部 -->
          <div class="note-card-header">
            <div class="note-card-actions">
              <el-dropdown trigger="click" @click.stop>
                <el-button text class="more-btn">
                  <el-icon><MoreFilled /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="editNote(note)">
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-dropdown-item>
                    <el-dropdown-item @click="deleteNote(note)" class="danger-item">
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>

          <!-- 笔记标题 -->
          <h3 class="note-card-title">{{ note.title || '无标题' }}</h3>

          <!-- 笔记内容预览 -->
          <div class="note-card-content">
            {{ getContentPreview(note.content) }}
          </div>

          <!-- 笔记底部信息 -->
          <div class="note-card-footer">
            <div class="note-tags">
              <el-tag
                  v-if="note.category"
                  size="small"
                  type="info"
                  class="category-tag"
              >
                {{ note.category }}
              </el-tag>
              <el-tag
                  v-for="tag in note.tagList?.slice(0, 2)"
                  :key="tag"
                  size="small"
                  class="content-tag"
              >
                {{ tag }}
              </el-tag>
              <span v-if="note.tagList?.length > 2" class="more-tags">
                +{{ note.tagList.length - 2 }}
              </span>
            </div>
            <div class="note-time">
              {{ formatTime(note.updateTime) }}
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <div class="empty-content">
          <el-icon class="empty-icon"><DocumentAdd /></el-icon>
          <h3>还没有笔记</h3>
          <p>点击"新建笔记"开始记录你的想法</p>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            新建笔记
          </el-button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-container">
      <el-pagination
          v-model="currentPage"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>

    <!-- 新建/编辑笔记对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑笔记' : '新建笔记'"
        width="80%"
        :before-close="handleDialogClose"
    >
      <el-form
          ref="noteFormRef"
          :model="noteForm"
          :rules="noteFormRules"
          label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input
              v-model="noteForm.title"
              placeholder="请输入笔记标题"
              maxlength="200"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="分类" prop="category">
          <el-select v-model="noteForm.category" placeholder="请选择分类" clearable>
            <el-option label="技术笔记" value="技术笔记" />
            <el-option label="面试准备" value="面试准备" />
            <el-option label="学习心得" value="学习心得" />
            <el-option label="工作总结" value="工作总结" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="标签" prop="tags">
          <el-input
              v-model="noteForm.tags"
              placeholder="多个标签用逗号分隔，如：Java,Spring,数据库"
              maxlength="500"
          />
        </el-form-item>

        <el-form-item label="公开性">
          <el-radio-group v-model="noteForm.isPublic">
            <el-radio :label="0">私有</el-radio>
            <el-radio :label="1">公开</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
              v-model="noteForm.content"
              type="textarea"
              :rows="15"
              placeholder="支持Markdown格式，开始记录你的想法吧..."
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
              type="warning"
              @click="optimizeNoteWithAI"
              :loading="aiOptimizeLoading"
              :disabled="!noteForm.content.trim()"
          >
            <el-icon><Star /></el-icon>
            AI优化
          </el-button>
          <el-button
              type="success"
              @click="saveOptimizedContent"
              :loading="savingOptimized"
              :disabled="!noteForm.content.trim() || !isContentOptimized"
          >
            <el-icon><Check /></el-icon>
            保存优化内容
          </el-button>
          <el-button type="primary" @click="saveNote" :loading="saving">
            {{ saving ? '保存中...' : '保存' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- AI对话框 -->
    <el-dialog
        v-model="aiChatVisible"
        title="AI 笔记助手"
        width="700px"
        top="8vh"
        class="ai-chat-dialog"
        :before-close="() => { aiChatVisible = false }"
    >
      <div class="ai-chat-container">
        <!-- 聊天记录 -->
        <div class="chat-messages" ref="chatMessagesRef">
          <div
              v-for="(message, index) in chatMessages"
              :key="index"
              :class="['message', message.type]"
          >
            <div class="message-avatar">
              <el-icon v-if="message.type === 'user'"><User /></el-icon>
              <el-icon v-else><ChatDotRound /></el-icon>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessageContent(message.content, message.type)"></div>
              <div class="message-time">{{ message.time }}</div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chat-input-area">
          <div class="input-wrapper">
            <el-input
                v-model="chatInput"
                type="textarea"
                :rows="3"
                placeholder="请输入您的问题，我可以帮您优化笔记内容、推荐标签分类等..."
                @keydown.ctrl.enter="sendMessage"
                class="chat-input"
            />
            <div class="input-actions">
              <el-button
                  @click="clearChat"
                  size="small"
                  class="clear-btn"
              >
                <el-icon><Delete /></el-icon>
                清空对话
              </el-button>
              <el-button
                  type="primary"
                  @click="sendMessage"
                  :loading="aiLoading"
                  size="small"
                  class="send-btn"
              >
                <el-icon><Position /></el-icon>
                发送 (Ctrl+Enter)
              </el-button>
            </div>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="quick-actions">
          <el-button
              size="small"
              @click="quickOptimize"
              :loading="aiLoading"
              class="quick-btn optimize-btn"
          >
            <el-icon><Star /></el-icon>
            优化当前笔记
          </el-button>
          <el-button
              size="small"
              @click="quickSuggestTags"
              :loading="aiLoading"
              class="quick-btn tags-btn"
          >
            <el-icon><Collection /></el-icon>
            推荐标签
          </el-button>
          <el-button
              size="small"
              @click="quickImproveStructure"
              :loading="aiLoading"
              class="quick-btn structure-btn"
          >
            <el-icon><Document /></el-icon>
            改进结构
          </el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 查看笔记对话框 -->
    <el-dialog
        v-model="viewDialogVisible"
        :title="currentNote?.title"
        width="85%"
        top="3vh"
        class="note-view-dialog"
    >
      <div v-if="currentNote" class="note-view">
        <!-- 笔记头部信息 -->
        <div class="note-view-header">
          <div class="note-view-meta">
            <el-tag v-if="currentNote.category" type="info" class="category-tag">{{ currentNote.category }}</el-tag>
            <el-tag
                v-for="tag in currentNote.tagList"
                :key="tag"
                class="content-tag"
            >
              {{ tag }}
            </el-tag>
            <span class="view-time">{{ formatTime(currentNote.updateTime) }}</span>
          </div>
        </div>

        <!-- 主要内容区域 -->
        <div class="note-view-main">
          <!-- 笔记内容 -->
          <div class="note-view-content">
            <div class="content-header">
              <h3>笔记内容</h3>
            </div>
            <div class="content-body">
              <pre>{{ currentNote.content }}</pre>
            </div>
          </div>

          <!-- AI助手区域 -->
          <div class="note-ai-assistant">
            <div class="ai-assistant-header">
              <div class="ai-header-left">
                <div class="ai-icon-wrapper ai-icon-wrapper-home">
                  <el-icon class="ai-icon ai-icon-home"><ChatDotRound /></el-icon>
                </div>
                <div class="ai-header-text">
                  <h4>AI 笔记助手</h4>
                  <p>智能分析、优化建议、内容扩展</p>
                </div>
              </div>
              <!-- 展开/收起AI助手按钮已移除，AI助手默认展开 -->
            </div>

            <div v-if="showNoteAI" class="ai-assistant-content">
              <!-- AI输入框 -->
              <div class="ai-input-section">
                <el-input
                    v-model="noteAIInput"
                    placeholder="询问AI关于这篇笔记的问题，或请求优化建议..."
                    class="ai-input"
                    @keydown.enter="() => { sendNoteAIRequest(noteAIInput); noteAIInput = ''; }"
                >
                  <template #append>
                    <el-button
                        @click="() => { sendNoteAIRequest(noteAIInput); noteAIInput = ''; }"
                        :loading="noteAILoading"
                        type="primary"
                        :disabled="!noteAIInput.trim()"
                        class="send-ai-btn"
                    >
                      <el-icon><Position /></el-icon>
                      发送
                    </el-button>
                  </template>
                </el-input>
              </div>

              <!-- 快捷操作 -->
              <div class="ai-quick-actions">
                <el-button
                    size="small"
                    @click="sendNoteAIRequest('分析笔记'); activeQuickAction = '分析笔记'"
                    :loading="noteAILoading && activeQuickAction === '分析笔记'"
                    class="quick-action-btn analyze-btn"
                >
                  <el-icon><Lightning /></el-icon>
                  分析笔记
                </el-button>
                <el-button
                    size="small"
                    @click="sendNoteAIRequest('优化建议'); activeQuickAction = '优化建议'"
                    :loading="noteAILoading && activeQuickAction === '优化建议'"
                    class="quick-action-btn optimize-btn"
                >
                  <el-icon><Star /></el-icon>
                  优化建议
                </el-button>
                <el-button
                    size="small"
                    @click="sendNoteAIRequest('总结要点'); activeQuickAction = '总结要点'"
                    :loading="noteAILoading && activeQuickAction === '总结要点'"
                    class="quick-action-btn summarize-btn"
                >
                  <el-icon><Document /></el-icon>
                  总结要点
                </el-button>
                <el-button
                    size="small"
                    @click="sendNoteAIRequest('扩展内容'); activeQuickAction = '扩展内容'"
                    :loading="noteAILoading && activeQuickAction === '扩展内容'"
                    class="quick-action-btn extend-btn"
                >
                  <el-icon><Picture /></el-icon>
                  扩展内容
                </el-button>
              </div>

              <!-- AI对话记录 -->
              <div class="ai-chat-history">
                <div class="chat-history-header">
                  <h5>对话记录</h5>
                </div>
                <div class="chat-messages-container">
                  <div
                      v-for="(message, index) in noteAIMessages"
                      :key="index"
                      :class="['ai-message', message.type]"
                  >
                    <div class="message-avatar">
                      <el-icon v-if="message.type === 'user'"><User /></el-icon>
                      <el-icon v-else><ChatDotRound /></el-icon>
                    </div>
                    <div class="message-content">
                      <div class="message-text" v-html="formatMessageContent(message.content, message.type)"></div>
                      <div class="message-time">{{ message.time }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false" class="close-btn">关闭</el-button>
          <el-button
              type="warning"
              @click="optimizeCurrentNote"
              :loading="aiOptimizeLoading"
              :disabled="!currentNote?.content"
              class="footer-optimize-btn"
          >
            <el-icon><Star /></el-icon>
            AI优化
          </el-button>
          <el-button
              type="success"
              @click="saveCurrentOptimizedContent"
              :loading="savingOptimized"
              :disabled="!currentNote?.content || !isCurrentNoteOptimized"
              class="save-optimized-btn"
          >
            <el-icon><Check /></el-icon>
            保存优化内容
          </el-button>
          <el-button type="primary" @click="editCurrentNote" class="edit-btn">编辑</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Search,
  Edit,
  Delete,
  MoreFilled,
  Grid,
  List,
  DocumentAdd,
  ChatDotRound,
  Position,
  Lightning,
  Picture,
  Document,
  Star,
  User,
  Check,
  Collection
} from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'NoteBooks',
  components: {
    Plus,
    Search,
    Edit,
    Delete,
    MoreFilled,
    Grid,
    List,
    DocumentAdd,
    ChatDotRound,
    Position,
    Lightning,
    Picture,
    Document,
    Star,
    User,
    Check,
    Collection
  },
  setup() {
    // 当前激活的快捷操作
    const activeQuickAction = ref('')
    // 响应式数据
    const loading = ref(false)
    const saving = ref(false)
    const notesList = ref([])
    const allTags = ref([])
    const categoryStats = ref([])

    // 搜索和筛选
    const searchKeyword = ref('')
    const selectedCategory = ref('')
    const selectedTags = ref([])

    // 视图模式
    const viewMode = ref('grid') // 'grid' 或 'list'

    // 排序
    const sortOption = ref('updateTime-desc')

    // AI对话相关
    const aiChatVisible = ref(false)
    const aiLoading = ref(false)
    const chatInput = ref('')
    const chatMessages = ref([])
    const chatMessagesRef = ref(null)

    // AI优化相关
    const aiOptimizeLoading = ref(false)
    const savingOptimized = ref(false)
    const isContentOptimized = ref(false)
    const isCurrentNoteOptimized = ref(false)
    const originalContent = ref('')
    const originalCurrentContent = ref('')

    // 新建笔记AI助手相关（已移除，AI助手现在在笔记详情中）

    // 笔记内AI助手相关
    const showNoteAI = ref(true)
    const noteAILoading = ref(false)
    const noteAIInput = ref('')
    const noteAIMessages = ref([])

    // 分页
    const currentPage = ref(1)
    const pageSize = ref(12) // 网格布局适合12个
    const total = ref(0)

    // 对话框控制
    const dialogVisible = ref(false)
    const viewDialogVisible = ref(false)
    const isEdit = ref(false)
    const currentNote = ref(null)

    // 表单数据
    const noteForm = reactive({
      id: null,
      title: '',
      content: '',
      category: '',
      tags: '',
      isPublic: 0
    })

    // 表单验证规则
    const noteFormRules = {
      title: [
        { required: true, message: '请输入笔记标题', trigger: 'blur' },
        { max: 200, message: '标题长度不能超过200字符', trigger: 'blur' }
      ],
      content: [
        { required: true, message: '请输入笔记内容', trigger: 'blur' }
      ]
    }

    const noteFormRef = ref(null)

    // API基础URL
    const API_BASE = 'http://localhost:8085/api'

    // 模拟数据
    const mockNotes = [
      {
        id: 1,
        userId: 1,
        title: 'Vue 3 Composition API 学习笔记',
        content: `# Vue 3 Composition API 学习笔记

## 什么是 Composition API
Composition API 是 Vue 3 中新增的一套 API，它提供了一种更灵活的方式来组织组件逻辑。

## 主要特点
1. **更好的逻辑复用** - 通过组合函数实现逻辑复用
2. **更好的类型推导** - 对 TypeScript 更友好
3. **更灵活的代码组织** - 相关逻辑可以组织在一起

## 核心 API
- ref() - 创建响应式引用
- reactive() - 创建响应式对象
- computed() - 创建计算属性
- watch() - 创建侦听器

## 示例代码
\`\`\`javascript
import { ref, computed } from 'vue'

export default {
  setup() {
    const count = ref(0)
    const doubleCount = computed(() => count.value * 2)

    return {
      count,
      doubleCount
    }
  }
}
\`\`\``,
        category: '技术笔记',
        tags: 'Vue3,前端,JavaScript,学习',
        tagList: ['Vue3', '前端', 'JavaScript', '学习'],
        isPublic: 0,
        createTime: '2024-01-15 10:30:00',
        updateTime: '2024-01-15 14:20:00'
      },
      {
        id: 2,
        userId: 1,
        title: 'Spring Boot 面试常见问题',
        content: `# Spring Boot 面试常见问题

## 1. 什么是 Spring Boot？
Spring Boot 是基于 Spring 框架的快速开发脚手架，它简化了 Spring 应用的配置和部署。

## 2. Spring Boot 的核心特性
- 自动配置 (Auto Configuration)
- 起步依赖 (Starter Dependencies)
- 内嵌服务器 (Embedded Server)
- 生产就绪特性 (Production-ready features)

## 3. 自动配置原理
Spring Boot 通过 @EnableAutoConfiguration 注解启用自动配置，它会根据类路径中的依赖自动配置 Bean。

## 4. 常见注解
- @SpringBootApplication
- @RestController
- @Service
- @Repository
- @Component

## 5. 配置文件
支持 application.properties 和 application.yml 两种格式。`,
        category: '面试准备',
        tags: 'SpringBoot,Java,面试,后端',
        tagList: ['SpringBoot', 'Java', '面试', '后端'],
        isPublic: 1,
        createTime: '2024-01-14 09:15:00',
        updateTime: '2024-01-16 11:45:00'
      },
      {
        id: 3,
        userId: 1,
        title: 'MySQL 索引优化心得',
        content: `# MySQL 索引优化心得

## 索引的作用
索引是数据库中用于快速查找数据的数据结构，类似于书籍的目录。

## 索引类型
1. **主键索引** - 唯一且不能为空
2. **唯一索引** - 唯一但可以为空
3. **普通索引** - 最基本的索引
4. **复合索引** - 多个字段组成的索引

## 优化原则
- 在经常查询的字段上建立索引
- 避免在频繁更新的字段上建立索引
- 复合索引要注意字段顺序
- 定期分析和优化索引

## 实际案例
在用户表的 email 字段上建立唯一索引：
\`\`\`sql
CREATE UNIQUE INDEX idx_user_email ON users(email);
\`\`\``,
        category: '技术笔记',
        tags: 'MySQL,数据库,索引,优化',
        tagList: ['MySQL', '数据库', '索引', '优化'],
        isPublic: 0,
        createTime: '2024-01-13 16:20:00',
        updateTime: '2024-01-13 18:30:00'
      },
      {
        id: 4,
        userId: 1,
        title: '算法刷题总结 - 动态规划',
        content: `# 动态规划算法总结

## 什么是动态规划
动态规划是一种通过把原问题分解为相对简单的子问题的方式求解复杂问题的方法。

## 解题步骤
1. 确定状态定义
2. 找出状态转移方程
3. 确定初始状态
4. 确定返回值

## 经典题目
### 1. 斐波那契数列
- 状态：f(n) 表示第 n 个斐波那契数
- 转移方程：f(n) = f(n-1) + f(n-2)

### 2. 爬楼梯问题
- 状态：dp[i] 表示爬到第 i 阶的方法数
- 转移方程：dp[i] = dp[i-1] + dp[i-2]

### 3. 最长递增子序列
- 状态：dp[i] 表示以第 i 个元素结尾的最长递增子序列长度
- 转移方程：dp[i] = max(dp[j] + 1) where j < i and arr[j] < arr[i]

## 刷题心得
- 多练习，培养状态定义的直觉
- 画图帮助理解状态转移
- 注意边界条件的处理`,
        category: '学习心得',
        tags: '算法,动态规划,刷题,LeetCode',
        tagList: ['算法', '动态规划', '刷题', 'LeetCode'],
        isPublic: 1,
        createTime: '2024-01-12 20:10:00',
        updateTime: '2024-01-14 22:15:00'
      },
      {
        id: 5,
        userId: 1,
        title: 'Redis 缓存策略与实践',
        content: `# Redis 缓存策略与实践

## 缓存策略
### 1. Cache-Aside (旁路缓存)
- 应用程序直接与缓存和数据库交互
- 读：先查缓存，缓存没有再查数据库
- 写：先更新数据库，再删除缓存

### 2. Write-Through (写穿透)
- 应用程序只与缓存交互
- 缓存负责与数据库同步

### 3. Write-Behind (写回)
- 只更新缓存，异步更新数据库
- 性能最好，但可能丢失数据

## 常见问题
### 缓存穿透
- 问题：查询不存在的数据
- 解决：布隆过滤器、缓存空值

### 缓存击穿
- 问题：热点数据过期
- 解决：互斥锁、永不过期

### 缓存雪崩
- 问题：大量缓存同时过期
- 解决：随机过期时间、多级缓存

## 实际应用
在电商系统中使用 Redis 缓存商品信息，提升查询性能。`,
        category: '技术笔记',
        tags: 'Redis,缓存,性能优化,架构',
        tagList: ['Redis', '缓存', '性能优化', '架构'],
        isPublic: 0,
        createTime: '2024-01-11 14:30:00',
        updateTime: '2024-01-12 09:20:00'
      },
      {
        id: 6,
        userId: 1,
        title: '项目管理经验分享',
        content: `# 项目管理经验分享

## 项目背景
负责开发一个在线教育平台，团队规模 8 人，开发周期 3 个月。

## 管理方法
### 1. 敏捷开发
- 采用 Scrum 框架
- 2 周一个迭代
- 每日站会跟进进度

### 2. 任务分解
- 使用 WBS 工作分解结构
- 将大任务拆分为小任务
- 每个任务不超过 2 天

### 3. 风险管控
- 定期风险评估
- 制定应对预案
- 及时沟通协调

## 遇到的问题
1. **需求变更频繁** - 建立变更控制流程
2. **技术难点** - 组织技术攻关小组
3. **进度延期** - 调整资源分配

## 经验总结
- 沟通是项目成功的关键
- 及早发现和解决问题
- 团队协作比个人能力更重要
- 持续改进和学习`,
        category: '工作总结',
        tags: '项目管理,团队协作,敏捷开发,经验分享',
        tagList: ['项目管理', '团队协作', '敏捷开发', '经验分享'],
        isPublic: 1,
        createTime: '2024-01-10 11:00:00',
        updateTime: '2024-01-11 16:45:00'
      },
      {
        id: 7,
        userId: 1,
        title: 'JavaScript 异步编程深入理解',
        content: `# JavaScript 异步编程深入理解

## 异步编程的发展历程
1. **回调函数** - 最早的异步解决方案
2. **Promise** - ES6 引入，解决回调地狱
3. **async/await** - ES2017 引入，同步写法处理异步

## Promise 详解
### 三种状态
- pending（进行中）
- fulfilled（已成功）
- rejected（已失败）

### 常用方法
- Promise.resolve()
- Promise.reject()
- Promise.all()
- Promise.race()

## async/await 最佳实践
\`\`\`javascript
// 错误处理
async function fetchData() {
  try {
    const response = await fetch('/api/data')
    const data = await response.json()
    return data
  } catch (error) {
    console.error('获取数据失败:', error)
    throw error
  }
}

// 并发处理
async function fetchMultipleData() {
  const [user, posts, comments] = await Promise.all([
    fetchUser(),
    fetchPosts(),
    fetchComments()
  ])
  return { user, posts, comments }
}
\`\`\`

## 注意事项
- 避免在循环中使用 await
- 合理使用 Promise.all() 提升性能
- 正确处理错误和异常`,
        category: '技术笔记',
        tags: 'JavaScript,异步编程,Promise,async',
        tagList: ['JavaScript', '异步编程', 'Promise', 'async'],
        isPublic: 0,
        createTime: '2024-01-09 15:20:00',
        updateTime: '2024-01-10 10:30:00'
      },
      {
        id: 8,
        userId: 1,
        title: '系统设计面试准备',
        content: `# 系统设计面试准备

## 面试流程
1. **需求澄清** (5-10分钟)
2. **高层设计** (10-15分钟)
3. **详细设计** (15-20分钟)
4. **扩展讨论** (5-10分钟)

## 设计原则
### 可扩展性 (Scalability)
- 水平扩展 vs 垂直扩展
- 负载均衡
- 数据分片

### 可靠性 (Reliability)
- 冗余设计
- 故障转移
- 数据备份

### 可用性 (Availability)
- 99.9% vs 99.99% 可用性
- 降级策略
- 监控告警

## 常见系统设计题目
1. **设计短链接服务** (如 bit.ly)
2. **设计聊天系统** (如 WhatsApp)
3. **设计新闻推送系统** (如 Twitter)
4. **设计视频分享平台** (如 YouTube)

## 技术栈选择
- **数据库**: MySQL, PostgreSQL, MongoDB
- **缓存**: Redis, Memcached
- **消息队列**: RabbitMQ, Kafka
- **搜索**: Elasticsearch
- **CDN**: CloudFlare, AWS CloudFront

## 准备建议
- 多练习画架构图
- 了解各种技术的优缺点
- 关注数据量级和性能指标
- 考虑成本和维护性`,
        category: '面试准备',
        tags: '系统设计,面试,架构,分布式',
        tagList: ['系统设计', '面试', '架构', '分布式'],
        isPublic: 1,
        createTime: '2024-01-08 13:45:00',
        updateTime: '2024-01-09 17:20:00'
      }
    ]

    // 获取笔记列表
    const fetchNotesList = async () => {
      loading.value = true
      try {
        const [sortField, sortOrder] = sortOption.value.split('-')
        const response = await axios.post(`${API_BASE}/note/list/page`, {
          current: currentPage.value,
          pageSize: pageSize.value,
          keyword: searchKeyword.value,
          category: selectedCategory.value,
          tags: selectedTags.value.join(','),
          sortField: sortField,
          sortOrder: sortOrder
        })

        if (response.data.code === 0) {
          const records = response.data.data.records || []
          const totalNum = Number(response.data.data.total || 0)

          // 如果后端返回空数据，使用模拟数据
          if (records.length === 0 && totalNum === 0) {
            console.log('后端返回空数据，使用模拟数据')
            let filteredNotes = [...mockNotes]

            // 应用搜索过滤
            if (searchKeyword.value) {
              filteredNotes = filteredNotes.filter(note =>note.title.includes(searchKeyword.value) ||
                  note.content.includes(searchKeyword.value) ||
                  note.tags.includes(searchKeyword.value)
              )
            }

            // 应用分类过滤
            if (selectedCategory.value) {
              filteredNotes = filteredNotes.filter(note => note.category === selectedCategory.value)
            }

            // 应用标签过滤
            if (selectedTags.value.length > 0) {
              filteredNotes = filteredNotes.filter(note =>
                selectedTags.value.some(tag => note.tagList.includes(tag))
              )
            }

            // 应用排序
            const [sortField, sortOrder] = sortOption.value.split('-')
            filteredNotes.sort((a, b) => {
              let aValue = a[sortField]
              let bValue = b[sortField]

              if (sortField === 'title') {
                aValue = aValue.toLowerCase()
                bValue = bValue.toLowerCase()
              } else if (sortField === 'createTime' || sortField === 'updateTime') {
                aValue = new Date(aValue)
                bValue = new Date(bValue)
              }

              if (sortOrder === 'asc') {
                return aValue > bValue ? 1 : -1
              } else {
                return aValue < bValue ? 1 : -1
              }
            })

            // 分页处理
            const startIndex = (currentPage.value - 1) * pageSize.value
            const endIndex = startIndex + pageSize.value
            notesList.value = filteredNotes.slice(startIndex, endIndex)
            total.value = filteredNotes.length

            ElMessage.info('当前显示模拟数据')
          } else {
            notesList.value = records
            total.value = totalNum
          }
        } else {
          ElMessage.error(response.data.message || '获取笔记列表失败')
        }
      } catch (error) {
        console.error('获取笔记列表失败，使用模拟数据:', error)
        // 使用模拟数据
        let filteredNotes = [...mockNotes]

        // 应用搜索过滤
        if (searchKeyword.value) {
          filteredNotes = filteredNotes.filter(note =>
            note.title.includes(searchKeyword.value) ||
              note.content.includes(searchKeyword.value) ||
              note.tags.includes(searchKeyword.value)
          )
        }

        // 应用分类过滤
        if (selectedCategory.value) {
          filteredNotes = filteredNotes.filter(note => note.category === selectedCategory.value)
        }

        // 应用标签过滤
        if (selectedTags.value.length > 0) {
          filteredNotes = filteredNotes.filter(note =>
            selectedTags.value.some(tag => note.tagList.includes(tag))
          )
        }

        // 应用排序
        const [sortField, sortOrder] = sortOption.value.split('-')
        filteredNotes.sort((a, b) => {
          let aValue = a[sortField]
          let bValue = b[sortField]

          if (sortField === 'title') {
            aValue = aValue.toLowerCase()
            bValue = bValue.toLowerCase()
          } else if (sortField === 'createTime' || sortField === 'updateTime') {
            aValue = new Date(aValue)
            bValue = new Date(bValue)
          }

          if (sortOrder === 'asc') {
            return aValue > bValue ? 1 : -1
          } else {
            return aValue < bValue ? 1 : -1
          }
        })

        // 分页处理
        const startIndex = (currentPage.value - 1) * pageSize.value
        const endIndex = startIndex + pageSize.value
        notesList.value = filteredNotes.slice(startIndex, endIndex)
        total.value = filteredNotes.length

        ElMessage.info('当前显示模拟数据')
      } finally {
        loading.value = false
      }
    }

    // 获取所有标签
    const fetchAllTags = async () => {
      try {
        const response = await axios.get(`${API_BASE}/note/tags`)
        if (response.data.code === 0) {
          const tags = response.data.data || []

          // 如果后端返回空标签，使用模拟数据
          if (tags.length === 0) {
            console.log('后端返回空标签，使用模拟数据')
            const tagsSet = new Set()
            mockNotes.forEach(note => {
              note.tagList.forEach(tag => tagsSet.add(tag))
            })
            allTags.value = Array.from(tagsSet)
          } else {
            allTags.value = tags
          }
        }
      } catch (error) {
        console.error('获取标签失败，使用模拟数据:', error)
        // 从模拟数据中提取所有标签
        const tagsSet = new Set()
        mockNotes.forEach(note => {
          note.tagList.forEach(tag => tagsSet.add(tag))
        })
        allTags.value = Array.from(tagsSet)
      }
    }

    // 获取分类统计
    const fetchCategoryStats = async () => {
      try {
        const response = await axios.get('http://localhost:8089/api/note/category/stats')
        if (response.data.code === 0) {
          const stats = response.data.data || []

          // 如果后端返回空统计，使用模拟数据
          if (stats.length === 0) {
            console.log('后端返回空分类统计，使用模拟数据')
            const categoryCount = {}
            mockNotes.forEach(note => {
              const category = note.category || '未分类'
              categoryCount[category] = (categoryCount[category] || 0) + 1
            })
            categoryStats.value = Object.entries(categoryCount).map(([category, count]) => ({
              category,
              count
            }))
          } else {
            categoryStats.value = stats
          }
        }
      } catch (error) {
        console.error('获取分类统计失败，使用模拟数据:', error)
        // 从模拟数据中统计分类
        const categoryCount = {}
        mockNotes.forEach(note => {
          const category = note.category || '未分类'
          categoryCount[category] = (categoryCount[category] || 0) + 1
        })
        categoryStats.value = Object.entries(categoryCount).map(([category, count]) => ({
          category,
          count
        }))
      }
    }

    // 搜索处理
    const handleSearch = () => {
      currentPage.value = 1
      fetchNotesList()
    }

    // 分类变化处理
    const handleCategoryChange = (category) => {
      selectedCategory.value = category
      currentPage.value = 1
      fetchNotesList()
    }

    // 标签切换
    const toggleTag = (tag) => {
      const index = selectedTags.value.indexOf(tag)
      if (index > -1) {
        selectedTags.value.splice(index, 1)
      } else {
        selectedTags.value.push(tag)
      }
      currentPage.value = 1
      fetchNotesList()
    }

    // 重置筛选条件
    const resetFilters = () => {
      searchKeyword.value = ''
      selectedCategory.value = ''
      selectedTags.value = []
      currentPage.value = 1
      fetchNotesList()
    }

    // 分页处理
    const handleSizeChange = (newSize) => {
      pageSize.value = newSize
      currentPage.value = 1
      fetchNotesList()
    }

    const handleCurrentChange = (newPage) => {
      currentPage.value = newPage
      fetchNotesList()
    }

    // 显示新建对话框
    const showAddDialog = () => {
      isEdit.value = false
      resetNoteForm()
      dialogVisible.value = true
    }

    // 排序处理
    const handleSortChange = () => {
      // 更新请求参数中的排序字段
      currentPage.value = 1
      fetchNotesList()
    }

    // 显示AI对话框
    const showAIChat = () => {
      console.log('显示AI对话框')
      aiChatVisible.value = true
      if (chatMessages.value.length === 0) {
        // 添加欢迎消息和一些示例对话
        chatMessages.value.push({
          type: 'ai',
          content: '您好！我是您的AI笔记助手。我可以帮您优化笔记内容、推荐标签分类、改进文档结构等。请告诉我您需要什么帮助？',
          time: new Date().toLocaleTimeString()
        })

        // 添加一些示例对话
        setTimeout(() => {
          chatMessages.value.push({
            type: 'user',
            content: '请帮我优化这个Vue学习笔记',
            time: new Date().toLocaleTimeString()
          })

          setTimeout(() => {
            chatMessages.value.push({
              type: 'ai',
              content: '我已经分析了您的Vue学习笔记，建议进行以下优化：\n\n1. **添加清晰的章节结构**\n   - 使用 ## 二级标题分隔不同概念\n   - 添加目录索引\n\n2. **完善代码示例**\n   - 为每个概念添加实际代码演示\n   - 使用代码块格式化\n\n3. **增加实践要点**\n   - 添加常见问题和解决方案\n   - 标注重要概念和注意事项\n\n4. **推荐标签**：Vue3, 前端, JavaScript, 组件化\n\n您希望我重点优化哪个方面呢？',
              time: new Date().toLocaleTimeString()
            })
          }, 1000)
        }, 500)
      }
    }

    // 发送消息
    const sendMessage = async () => {
      if (!chatInput.value.trim()) return

      // 添加用户消息
      chatMessages.value.push({
        type: 'user',
        content: chatInput.value,
        time: new Date().toLocaleTimeString()
      })

      const userMessage = chatInput.value
      chatInput.value = ''
      aiLoading.value = true

      try {
        const response = await axios.post(`${API_BASE}/note/ai/chat`, {
          message: userMessage,
          context: currentNote.value?.content || ''
        })

        if (response.data.code === 0) {
          chatMessages.value.push({
            type: 'ai',
            content: response.data.data.response,
            time: new Date().toLocaleTimeString()
          })
        } else {
          throw new Error(response.data.message)
        }
      } catch (error) {
        console.error('AI聊天失败:', error)
        // 使用模拟回复
        chatMessages.value.push({
          type: 'ai',
          content: generateMockAIResponse(userMessage),
          time: new Date().toLocaleTimeString()
        })
      } finally {
        aiLoading.value = false
        // 滚动到底部
        setTimeout(() => {
          if (chatMessagesRef.value) {
            chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
          }
        }, 100)
      }
    }

    // 清空对话
    const clearChat = () => {
      chatMessages.value = []
      showAIChat() // 重新显示欢迎消息
    }

    // 快捷优化
    const quickOptimize = async () => {
      // 添加用户消息
      chatMessages.value.push({
        type: 'user',
        content: '请优化我当前选中的笔记',
        time: new Date().toLocaleTimeString()
      })

      aiLoading.value = true

      // 模拟AI分析过程
      setTimeout(() => {
        if (!currentNote.value) {
          chatMessages.value.push({
            type: 'ai',
            content: '请先选择一个笔记，我才能为您提供针对性的优化建议。\n\n您可以：\n1. 点击任意笔记卡片查看详情\n2. 然后再使用优化功能\n3. 或者直接告诉我您想优化的笔记类型',
            time: new Date().toLocaleTimeString()
          })
        } else {
          // 根据笔记内容生成优化建议
          const note = currentNote.value
          let optimizationSuggestions = ''

          if (note.title.includes('Vue') || note.content.includes('Vue')) {
            optimizationSuggestions = `我已经分析了您的Vue笔记"${note.title}"，提供以下优化建议：\n\n📝 **内容优化**\n- 添加Vue 3新特性对比\n- 补充Composition API实践案例\n- 增加性能优化技巧\n\n🏷️ **标签建议**：Vue3, 前端, JavaScript, 组件化, 响应式\n\n📂 **分类建议**：技术笔记 > 前端开发\n\n✨ **结构改进**\n- 添加"前置知识"部分\n- 增加"实战项目"章节\n- 补充"常见问题"总结`
          } else if (note.title.includes('Spring') || note.content.includes('Spring')) {
            optimizationSuggestions = `我已经分析了您的Spring Boot笔记"${note.title}"，提供以下优化建议：\n\n📝 **内容优化**\n- 添加自动配置原理解析\n- 补充微服务架构实践\n- 增加部署和监控内容\n\n🏷️ **标签建议**：SpringBoot, Java, 后端, 微服务, API\n\n📂 **分类建议**：技术笔记 > 后端开发\n\n✨ **结构改进**\n- 添加"环境搭建"部分\n- 增加"最佳实践"章节\n- 补充"性能调优"总结`
          } else if (note.title.includes('算法') || note.content.includes('算法')) {
            optimizationSuggestions = `我已经分析了您的算法笔记"${note.title}"，提供以下优化建议：\n\n📝 **内容优化**\n- 添加时间复杂度分析\n- 补充多种解法对比\n- 增加相关题目链接\n\n🏷️ **标签建议**：算法, 数据结构, LeetCode, 动态规划\n\n📂 **分类建议**：学习心得 > 算法刷题\n\n✨ **结构改进**\n- 添加"解题思路"部分\n- 增加"代码实现"章节\n- 补充"举一反三"总结`
          } else {
            optimizationSuggestions = `我已经分析了您的笔记"${note.title}"，提供以下通用优化建议：\n\n📝 **内容优化**\n- 添加清晰的章节标题\n- 补充实际案例和示例\n- 增加重点内容标注\n\n🏷️ **标签建议**：根据内容添加3-5个相关标签\n\n📂 **分类建议**：选择最匹配的分类\n\n✨ **结构改进**\n- 添加"概述"部分\n- 增加"实践要点"章节\n- 补充"总结回顾"部分`
          }

          chatMessages.value.push({
            type: 'ai',
            content: optimizationSuggestions + '\n\n💡 **下一步操作**\n您可以点击"编辑"按钮应用这些建议，或者告诉我您希望重点优化哪个方面。',
            time: new Date().toLocaleTimeString()
          })
        }

        aiLoading.value = false
        scrollToBottom()
      }, 1500)
    }

    // 快捷推荐标签
    const quickSuggestTags = () => {
      // 直接添加AI回复，不需要发送请求
      chatMessages.value.push({
        type: 'user',
        content: '请为我的笔记推荐合适的标签和分类',
        time: new Date().toLocaleTimeString()
      })

      setTimeout(() => {
        chatMessages.value.push({
          type: 'ai',
          content: '基于您当前的笔记内容，我为您推荐以下标签：\n\n🏷️ **技术类标签**\n- Vue3, React, JavaScript (前端技术)\n- SpringBoot, Java, MySQL (后端技术)\n- 算法, 数据结构, LeetCode (算法相关)\n\n📂 **分类建议**\n- 技术笔记：按技术栈分类\n- 学习心得：按知识领域分类\n- 面试准备：按公司或岗位分类\n\n💡 **使用技巧**\n- 每个笔记使用3-5个标签\n- 结合通用标签和专业标签\n- 定期整理重复或相似标签\n\n您希望我为特定笔记推荐更精准的标签吗？',
          time: new Date().toLocaleTimeString()
        })
        scrollToBottom()
      }, 800)
    }

    // 快捷改进结构
    const quickImproveStructure = () => {
      chatMessages.value.push({
        type: 'user',
        content: '请帮我改进笔记的结构和格式',
        time: new Date().toLocaleTimeString()
      })

      setTimeout(() => {
        chatMessages.value.push({
          type: 'ai',
          content: '我为您提供一个优化的笔记结构模板：\n\n📝 **标准笔记结构**\n\n```markdown\n# 主标题\n\n## 概述\n- 背景介绍\n- 核心目标\n\n## 重点内容\n### 关键概念\n- 概念A：解释\n- 概念B：解释\n\n### 实践示例\n```代码示例```\n\n## 注意事项\n⚠️ 重要提醒\n💡 最佳实践\n\n## 总结\n- 关键要点1\n- 关键要点2\n\n## 参考资料\n- [链接1](url)\n- [链接2](url)\n```\n\n🎯 **格式化建议**\n- 使用emoji增强可读性\n- 代码块添加语言标识\n- 重要内容用粗体标记\n- 添加分割线区分章节\n\n需要我帮您重构具体的笔记吗？',
          time: new Date().toLocaleTimeString()
        })
        scrollToBottom()
      }, 1000)
    }

    // 滚动到底部
    const scrollToBottom = () => {
      setTimeout(() => {
        if (chatMessagesRef.value) {
          chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
        }
      }, 100)
    }

    // 生成模拟AI回复
    const generateMockAIResponse = (message) => {
      const lowerMessage = message.toLowerCase()

      // 模拟AI回复的随机延迟
      const responses = []

      if (lowerMessage.includes('优化') || lowerMessage.includes('改进')) {
        responses.push(
          '我可以帮您优化笔记内容的结构和表达。建议您：\n\n📝 **内容结构优化**\n1. 使用清晰的标题层次结构 (H1, H2, H3)\n2. 添加代码示例和实际案例\n3. 在关键点添加总结和要点\n4. 使用合适的标签便于后续查找\n\n🎯 **具体建议**\n- 为技术笔记添加"前置知识"和"实践要点"部分\n- 使用表格整理对比信息\n- 添加思维导图或流程图\n\n您希望我重点优化哪个方面呢？',

          '根据您的笔记内容，我为您提供以下优化建议：\n\n🔧 **技术笔记优化**\n- 添加环境配置说明\n- 补充常见错误和解决方案\n- 增加性能优化要点\n\n📚 **学习笔记优化**\n- 建立知识点关联图\n- 添加复习检查清单\n- 记录学习心得和疑问\n\n💡 **面试笔记优化**\n- 按难度等级分类问题\n- 添加答题思路和技巧\n- 补充相关延伸知识\n\n需要我针对特定类型的笔记提供详细建议吗？'
        )
      } else if (lowerMessage.includes('标签') || lowerMessage.includes('分类')) {
        responses.push(
          '根据您的笔记内容，我建议使用以下标签和分类策略：\n\n🏷️ **技术标签建议**\n- 编程语言：JavaScript, Python, Java\n- 框架技术：Vue, React, Spring Boot\n- 工具平台：Git, Docker, MySQL\n\n📂 **分类建议**\n- 技术笔记：按技术栈分类\n- 学习笔记：按知识领域分类\n- 面试准备：按公司或职位分类\n- 项目总结：按项目类型分类\n\n💡 **标签使用技巧**\n- 使用3-5个精准标签\n- 结合通用标签和专业标签\n- 定期整理和规范标签命名\n\n需要我为您的具体笔记推荐标签吗？',

          '我来为您推荐一套完整的笔记分类和标签体系：\n\n📋 **主分类体系**\n1. 技术笔记 - 编程相关知识\n2. 学习心得 - 学习过程总结\n3. 面试准备 - 求职面试资料\n4. 工作总结 - 项目经验分享\n5. 生活随笔 - 个人思考记录\n\n🔖 **标签命名规范**\n- 技术类：前端/后端/数据库/算法\n- 难度类：入门/进阶/高级\n- 状态类：学习中/已掌握/需复习\n- 来源类：官方文档/教程/实践\n\n这样的分类体系可以让您的笔记更有条理，查找更方便。'
        )
      } else if (lowerMessage.includes('结构') || lowerMessage.includes('格式')) {
        responses.push(
          '好的笔记结构应该包括以下要素：\n\n📖 **标准笔记结构**\n1. **清晰的标题** - 概括主要内容\n2. **核心概念** - 重点知识点标注\n3. **实例说明** - 具体代码或案例\n4. **注意事项** - 易错点和最佳实践\n5. **总结回顾** - 关键要点梳理\n6. **相关链接** - 参考资料和延伸阅读\n\n✨ **格式化建议**\n- 使用Markdown语法提升可读性\n- 代码块使用语法高亮\n- 重要内容用粗体或高亮标记\n- 添加目录索引便于导航\n\n您希望我帮您重新组织某个笔记的结构吗？',

          '我为您提供一个实用的笔记模板结构：\n\n📝 **技术笔记模板**\n```\n# 标题\n## 概述\n- 技术背景\n- 应用场景\n\n## 核心概念\n- 关键术语解释\n- 工作原理\n\n## 实践示例\n```代码示例```\n\n## 最佳实践\n- 使用建议\n- 性能优化\n\n## 常见问题\n- 问题描述\n- 解决方案\n\n## 总结\n- 关键要点\n- 学习心得\n```\n\n这个模板可以确保您的笔记结构完整、逻辑清晰。'
        )
      } else if (lowerMessage.includes('vue') || lowerMessage.includes('Vue')) {
        responses.push(
          '关于Vue学习笔记，我建议您这样组织：\n\n🎯 **Vue笔记结构建议**\n\n## 基础概念\n- 响应式原理\n- 组件化思想\n- 生命周期钩子\n\n## 核心API\n- Composition API vs Options API\n- ref 和 reactive\n- computed 和 watch\n\n## 实战技巧\n- 组件通信方式\n- 状态管理 (Pinia/Vuex)\n- 路由配置\n\n## 性能优化\n- 懒加载\n- 虚拟滚动\n- 打包优化\n\n🏷️ **推荐标签**：Vue3, 前端, JavaScript, 组件化, SPA\n\n需要我为您的Vue笔记提供具体的优化建议吗？'
        )
      } else if (lowerMessage.includes('spring') || lowerMessage.includes('Spring')) {
        responses.push(
          'Spring Boot笔记可以这样优化：\n\n☕ **Spring Boot笔记结构**\n\n## 核心特性\n- 自动配置原理\n- 起步依赖管理\n- 内嵌服务器\n\n## 常用注解\n- @SpringBootApplication\n- @RestController\n- @Service, @Repository\n\n## 数据访问\n- JPA/MyBatis配置\n- 数据库连接池\n- 事务管理\n\n## 实践案例\n- RESTful API设计\n- 异常处理\n- 配置管理\n\n🏷️ **推荐标签**：SpringBoot, Java, 后端, 微服务, API\n\n您的Spring Boot笔记主要涉及哪些方面？'
        )
      } else if (lowerMessage.includes('算法') || lowerMessage.includes('algorithm')) {
        responses.push(
          '算法学习笔记的最佳实践：\n\n🧮 **算法笔记结构**\n\n## 算法分类\n- 排序算法\n- 搜索算法\n- 动态规划\n- 图论算法\n\n## 题目记录\n- 题目描述\n- 解题思路\n- 代码实现\n- 时间复杂度分析\n\n## 刷题心得\n- 解题模板\n- 常见套路\n- 易错点总结\n\n## 复习计划\n- 按难度分级\n- 定期回顾\n- 模拟练习\n\n🏷️ **推荐标签**：算法, 数据结构, LeetCode, 面试, 编程\n\n您在算法学习中遇到什么具体问题吗？'
        )
      } else if (lowerMessage.includes('面试') || lowerMessage.includes('interview')) {
        responses.push(
          '面试准备笔记的组织建议：\n\n💼 **面试笔记结构**\n\n## 技术面试\n- 基础知识点\n- 项目经验总结\n- 算法题解\n- 系统设计\n\n## 行为面试\n- STAR法则应用\n- 项目亮点提炼\n- 职业规划\n\n## 公司研究\n- 业务模式\n- 技术栈\n- 企业文化\n\n## 复习计划\n- 知识点清单\n- 模拟面试\n- 问题预演\n\n🏷️ **推荐标签**：面试, 求职, 技术面试, HR面试\n\n您准备的是哪个方向的面试呢？'
        )
      } else {
        responses.push(
          '我是您的AI笔记助手，可以帮您：\n\n✨ **核心功能**\n🔧 优化笔记内容和结构\n🏷️ 推荐合适的标签和分类\n📝 改进文档格式和排版\n🔍 提升内容的可搜索性\n📊 分析笔记质量和完整性\n\n💡 **使用建议**\n- 告诉我您的笔记类型，我会提供针对性建议\n- 分享具体问题，我会给出解决方案\n- 使用快捷按钮快速优化笔记\n\n🎯 **热门话题**\n- "帮我优化Vue学习笔记"\n- "推荐算法笔记的标签"\n- "改进面试准备的结构"\n\n请告诉我您需要什么帮助？',

          '欢迎使用AI笔记助手！我可以为您提供：\n\n📚 **笔记优化服务**\n- 内容结构调整\n- 格式规范化\n- 重点内容标注\n- 逻辑关系梳理\n\n🎯 **智能推荐**\n- 标签自动生成\n- 分类建议\n- 相关笔记关联\n- 学习路径规划\n\n🔧 **实用工具**\n- Markdown格式化\n- 代码高亮优化\n- 图表制作建议\n- 模板推荐\n\n您可以：\n1. 直接描述需求\n2. 使用快捷操作按钮\n3. 上传笔记内容让我分析\n\n现在就开始优化您的笔记吧！'
        )
      }

      // 随机选择一个回复
      return responses[Math.floor(Math.random() * responses.length)]
    }

    // 重置表单
    const resetNoteForm = () => {
      noteForm.id = null
      noteForm.title = ''
      noteForm.content = ''
      noteForm.category = ''
      noteForm.tags = ''
      noteForm.isPublic = 0
    }


    // 编辑笔记
    const editNote = (note) => {
      isEdit.value = true
      noteForm.id = note.id
      noteForm.title = note.title
      noteForm.content = note.content
      noteForm.category = note.category
      noteForm.tags = note.tags
      noteForm.isPublic = note.isPublic
      dialogVisible.value = true
    }

    // 查看笔记
    const viewNote = (note) => {
      // 深拷贝，防止currentNote被AI助手或优化操作影响原始数据
      currentNote.value = JSON.parse(JSON.stringify(note))
      viewDialogVisible.value = true
    }

    // 编辑当前查看的笔记
    const editCurrentNote = () => {
      viewDialogVisible.value = false
      editNote(currentNote.value)
    }

    // 保存笔记
    const saveNote = async () => {
      if (!noteFormRef.value) return

      try {
        await noteFormRef.value.validate()
        saving.value = true

        const url = isEdit.value ? 'http://localhost:8085/api/note/edit' : 'http://localhost:8085/api/note/add'

        try {
          const response = await axios.post(url, noteForm)
          if (response.data.code === 0) {
            ElMessage.success(isEdit.value ? '编辑成功' : '创建成功')
            dialogVisible.value = false
            fetchNotesList()
            fetchAllTags()
            fetchCategoryStats()
          } else {
            ElMessage.error(response.data.message || '保存失败')
          }
        } catch (apiError) {
          console.error('API调用失败，使用模拟模式:', apiError)

          // 模拟保存操作
          if (isEdit.value) {
            // 编辑现有笔记
            const index = mockNotes.findIndex(note => note.id === noteForm.id)
            if (index !== -1) {
              mockNotes[index] = {
                ...mockNotes[index],
                title: noteForm.title,
                content: noteForm.content,
                category: noteForm.category,
                tags: noteForm.tags,
                tagList: noteForm.tags ? noteForm.tags.split(',').map(tag => tag.trim()) : [],
                isPublic: noteForm.isPublic,
                updateTime: new Date().toLocaleString('zh-CN')
              }
            }
          } else {
            // 添加新笔记
            const newNote = {
              id: Math.max(...mockNotes.map(n => n.id)) + 1,
              userId: 1,
              title: noteForm.title,
              content: noteForm.content,
              category: noteForm.category,
              tags: noteForm.tags,
              tagList: noteForm.tags ? noteForm.tags.split(',').map(tag => tag.trim()) : [],
              isPublic: noteForm.isPublic,
              createTime: new Date().toLocaleString('zh-CN'),
              updateTime: new Date().toLocaleString('zh-CN')
            }
            mockNotes.unshift(newNote)
          }

          ElMessage.success(isEdit.value ? '编辑成功（模拟模式）' : '创建成功（模拟模式）')
          dialogVisible.value = false
          fetchNotesList()
          fetchAllTags()
          fetchCategoryStats()
        }
      } catch (error) {
        if (error.name !== 'ValidationError') {
          console.error('保存笔记失败:', error)
          ElMessage.error('保存失败')
        }
      } finally {
        saving.value = false
      }
    }

    // AI优化笔记
    const optimizeNoteWithAI = async () => {
      if (!noteForm.content.trim()) {
        ElMessage.warning('请先输入笔记内容')
        return
      }

      // 保存原始内容
      originalContent.value = noteForm.content
      aiOptimizeLoading.value = true
      try {
        // 调用笔记AI优化接口
        const response = await axios.post(`${API_BASE}/note/ai/optimize`, {
          content: noteForm.content,
          title: noteForm.title,
          category: noteForm.category
        })

        if (response.data.code === 0 && response.data.data.success) {
          const optimizedContent = response.data.data.optimizedContent

          // 更新笔记内容
          noteForm.content = optimizedContent
          isContentOptimized.value = true

          ElMessage.success('AI优化完成！内容已更新，请检查并点击"保存优化内容"按钮。')
        } else {
          // 如果优化失败，使用模拟优化
          const mockOptimizedContent = generateMockOptimizedContent(noteForm.content, noteForm.title, noteForm.category)
          noteForm.content = mockOptimizedContent
          isContentOptimized.value = true

          ElMessage.success('AI优化完成（模拟模式）！内容已更新，请检查并点击"保存优化内容"按钮。')
        }
      } catch (error) {
        console.error('AI优化失败:', error)

        // 如果AI接口失败，使用模拟优化
        const mockOptimizedContent = generateMockOptimizedContent(noteForm.content, noteForm.title, noteForm.category)
        noteForm.content = mockOptimizedContent
        isContentOptimized.value = true

        ElMessage.success('AI优化完成（模拟模式）！内容已更新，请检查并点击"保存优化内容"按钮。')
      } finally {
        aiOptimizeLoading.value = false
      }
    }

    // 保存优化后的内容
    const saveOptimizedContent = async () => {
      if (!noteForm.content.trim()) {
        ElMessage.warning('没有内容可保存')
        return
      }

      savingOptimized.value = true
      try {
        // 调用更新笔记内容接口
        const response = await axios.post(`${API_BASE}/note/update-content`, {
          noteId: noteForm.id,
          content: noteForm.content
        })

        if (response.data.code === 0) {
          ElMessage.success('优化内容保存成功！')
          isContentOptimized.value = false
          originalContent.value = ''
          // 刷新笔记列表
          fetchNotesList()
        } else {
          ElMessage.error(response.data.message || '保存失败')
        }
      } catch (error) {
        console.error('保存优化内容失败:', error)
        ElMessage.error('保存失败，请重试')
      } finally {
        savingOptimized.value = false
      }
    }

    // 生成模拟优化内容
    const generateMockOptimizedContent = (content, title, category) => {
      if (!content) return content

      let optimized = content

      // 添加标题结构
      if (!content.includes('#')) {
        optimized = `# ${title || '笔记标题'}\n\n${content}`
      }

      // 改进格式
      optimized = optimized
        .replace(/\n{3,}/g, '\n\n') // 减少多余空行
        .replace(/^(.+)$/gm, (match, line) => {
          // 为没有标题的长段落添加二级标题
          if (line.length > 50 && !line.startsWith('#') && !line.startsWith('-') && !line.startsWith('*')) {
            return `## ${line.substring(0, 20)}...\n\n${line}`
          }
          return line
        })

      // 添加总结部分
      if (!optimized.includes('总结') && !optimized.includes('## 总结')) {
        optimized += '\n\n## 总结\n\n- 本文介绍了相关概念和要点\n- 建议结合实际项目进行练习\n- 定期回顾和更新内容'
      }

      return optimized
    }

    // 删除笔记
    const deleteNote = async (note) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除笔记"${note.title}"吗？`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        try {
          const response = await axios.post('http://localhost:8089/api/note/delete', null, {
            params: { noteId: note.id }
          })

          if (response.data.code === 0) {
            ElMessage.success('删除成功')
            fetchNotesList()
            fetchCategoryStats()
          } else {
            ElMessage.error(response.data.message || '删除失败')
          }
        } catch (apiError) {
          console.error('API调用失败，使用模拟模式:', apiError)

          // 模拟删除操作
          const index = mockNotes.findIndex(n => n.id === note.id)
          if (index !== -1) {
            mockNotes.splice(index, 1)
            ElMessage.success('删除成功（模拟模式）')
            fetchNotesList()
            fetchCategoryStats()
          } else {
            ElMessage.error('笔记不存在')
          }
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除笔记失败:', error)
          ElMessage.error('删除失败')
        }
      }
    }

    // 对话框关闭处理
    const handleDialogClose = (done) => {
      if (noteForm.title || noteForm.content) {
        ElMessageBox.confirm('确定要关闭吗？未保存的内容将丢失。')
          .then(() => {
            done()
          })
          .catch(() => {})
      } else {
        done()
      }
    }

    // 工具函数
    const getContentPreview = (content) => {
      if (!content) return '暂无内容'
      return content.length > 100 ? content.substring(0, 100) + '...' : content
    }

    const formatTime = (time) => {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString('zh-CN')
    }

    // 格式化消息内容，将用户输入的文字标记为蓝色
    const formatMessageContent = (content, messageType = 'ai') => {
      if (!content) return ''

      // 只对用户消息应用蓝色高亮
      if (messageType === 'user') {
        return content.replace(/(.+)/g, '<span class="user-message-highlight">$1</span>')
      }

      return content
    }

    // 笔记AI助手方法
    const toggleNoteAI = () => {
      showNoteAI.value = !showNoteAI.value
      if (showNoteAI.value && noteAIMessages.value.length === 0) {
        // 添加欢迎消息
        noteAIMessages.value.push({
          type: 'ai',
          content: `您好！我是您的笔记AI助手。我已经分析了您的笔记《${currentNote.value?.title || '当前笔记'}》，可以为您提供以下帮助：\n\n• 分析笔记内容和结构\n• 提供优化建议\n• 总结关键要点\n• 扩展相关内容\n\n请告诉我您需要什么帮助？`,
          time: new Date().toLocaleTimeString()
        })
      }
    }

    // 统一AI助手请求方法
    const sendNoteAIRequest = async (actionType) => {
      if (!currentNote.value?.content) {
        ElMessage.warning('请先选择笔记') 
        return 
      }
      if (!actionType || !actionType.trim()) {
        ElMessage.warning('请输入要发送给AI助手的内容') 
        return 
      }
      noteAILoading.value = true 
      // 拼接内容
      const message = currentNote.value.content + '\n' + actionType 
      // 显示用户请求
      noteAIMessages.value.push({
        type: 'user',
        content: actionType,
        time: new Date().toLocaleTimeString()
      }) 
      try {
        const response = await axios.post('http://localhost:8085/api/ai/stream-chat', {
          message,
          botId: '', // 可选，留空用默认
          userId: 'user_' + (currentNote.value.userId || 1)
        }) 
        // 取AI返回内容（根据后端返回结构调整）
        let aiContent = '' 
        if (response.data && response.data.data && response.data.data.fullResponse) {
          aiContent = response.data.data.fullResponse 
        } else if (response.data && response.data.data) {
          aiContent = response.data.data 
        } else {
          aiContent = JSON.stringify(response.data) 
        }
        noteAIMessages.value.push({
          type: 'ai',
          content: aiContent,
          time: new Date().toLocaleTimeString()
        }) 
      } catch (e) {
        noteAIMessages.value.push({
          type: 'ai',
          content: 'AI服务异常，请稍后重试。',
          time: new Date().toLocaleTimeString()
        }) 
      } finally {
        noteAILoading.value = false 
      }
    }

    // 删除原有快捷AI方法（quickAnalyzeNote、quickOptimizeNote、quickSummarizeNote、quickExtendNote）

    // 初始化模拟数据
    const initializeMockData = () => {
      // 确保模拟数据中的 tagList 存在
      mockNotes.forEach(note => {
        if (!note.tagList && note.tags) {
          note.tagList = note.tags.split(',').map(tag => tag.trim())
        }
      })
    }

    // 生命周期
    onMounted(() => {
      initializeMockData()
      fetchNotesList()
      fetchAllTags()
      fetchCategoryStats()
    })

    // AI优化当前查看的笔记
    const optimizeCurrentNote = async () => {
      if (!currentNote.value?.content) {
        ElMessage.warning('当前笔记没有内容可优化')
        return
      }

      // 保存原始内容
      originalCurrentContent.value = currentNote.value.content
      aiOptimizeLoading.value = true
      try {
        // 调用笔记AI优化接口
        const response = await axios.post(`${API_BASE}/note/ai/optimize`, {
          content: currentNote.value.content,
          title: currentNote.value.title,
          category: currentNote.value.category
        })

        if (response.data.code === 0 && response.data.data.success) {
          const optimizedContent = response.data.data.optimizedContent

          // 更新当前笔记内容
          currentNote.value.content = optimizedContent
          isCurrentNoteOptimized.value = true

          ElMessage.success('AI优化完成！内容已更新，请检查并点击"保存优化内容"按钮。')
        } else {
          // 如果优化失败，使用模拟优化
          const mockOptimizedContent = generateMockOptimizedContent(
            currentNote.value.content,
            currentNote.value.title,
            currentNote.value.category
          )
          currentNote.value.content = mockOptimizedContent
          isCurrentNoteOptimized.value = true

          ElMessage.success('AI优化完成（模拟模式）！内容已更新，请检查并点击"保存优化内容"按钮。')
        }
      } catch (error) {
        console.error('AI优化失败:', error)

        // 如果AI接口失败，使用模拟优化
        const mockOptimizedContent = generateMockOptimizedContent(
          currentNote.value.content,
          currentNote.value.title,
          currentNote.value.category
        )
        currentNote.value.content = mockOptimizedContent
        isCurrentNoteOptimized.value = true

        ElMessage.success('AI优化完成（模拟模式）！内容已更新，请检查并点击"保存优化内容"按钮。')
      } finally {
        aiOptimizeLoading.value = false
      }
    }

    // 保存当前笔记的优化内容
    const saveCurrentOptimizedContent = async () => {
      if (!currentNote.value?.content) {
        ElMessage.warning('没有内容可保存')
        return
      }

      savingOptimized.value = true
      try {
        // 调用更新笔记内容接口
        const response = await axios.post(`${API_BASE}/note/update-content`, {
          noteId: currentNote.value.id,
          content: currentNote.value.content
        })

        if (response.data.code === 0) {
          ElMessage.success('优化内容保存成功！')
          isCurrentNoteOptimized.value = false
          originalCurrentContent.value = ''
          // 刷新笔记列表
          fetchNotesList()
        } else {
          ElMessage.error(response.data.message || '保存失败')
        }
      } catch (error) {
        console.error('保存优化内容失败:', error)
        ElMessage.error('保存失败，请重试')
      } finally {
        savingOptimized.value = false
      }
    }

    return {
      // 数据
      loading,
      saving,
      notesList,
      allTags,
      categoryStats,
      searchKeyword,
      selectedCategory,
      selectedTags,
      viewMode,
      sortOption,
      currentPage,
      pageSize,
      total,
      dialogVisible,
      viewDialogVisible,
      isEdit,
      currentNote,
      noteForm,
      noteFormRules,
      noteFormRef,
      // AI相关数据
      aiChatVisible,
      aiLoading,
      chatInput,
      chatMessages,
      chatMessagesRef,
      // 新建笔记AI助手数据（已移除）
      // 笔记内AI助手数据
      showNoteAI,
      noteAILoading,
      noteAIInput,
      noteAIMessages,
      // AI优化相关
      aiOptimizeLoading,
      savingOptimized,
      isContentOptimized,
      isCurrentNoteOptimized,

      // 方法
      fetchNotesList,
      handleSearch,
      handleCategoryChange,
      handleSortChange,
      toggleTag,
      resetFilters,
      handleSizeChange,
      handleCurrentChange,
      showAddDialog,
      showAIChat,
      editNote,
      viewNote,
      editCurrentNote,
      saveNote,
      deleteNote,
      handleDialogClose,
      getContentPreview,
      formatTime,
      formatMessageContent,
      // AI相关方法
      sendMessage,
      clearChat,
      quickOptimize,
      quickSuggestTags,
      quickImproveStructure,
      // 新建笔记AI助手方法（已移除，AI助手现在在笔记详情中）
      // 笔记内AI助手方法
      toggleNoteAI,
      sendNoteAIRequest,
      optimizeNoteWithAI,
      optimizeCurrentNote,
      saveOptimizedContent,
      saveCurrentOptimizedContent,
      activeQuickAction
    }
  }
}
</script>

<style scoped>
.notes-container {
  padding: 24px;
  background-color: #fafafa;
  min-height: 100vh;
}

/* 头部样式 */
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.new-note-btn {
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
  color: white !important;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
  transition: all 0.3s ease;
}

.new-note-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.4);
  color: white !important;
}

.header-right {
  display: flex;
  align-items: center;
}

.search-input {
  width: 320px;
}

/* 筛选栏样式 */
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 16px 24px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
}

.filter-tabs {
  display: flex;
  gap: 8px;
}

.filter-tab {
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
  transition: all 0.2s ease;
}

.filter-tab:hover {
  background-color: #f3f4f6;
  color: #374151;
}

.filter-tab.active {
  background-color: #3b82f6;
  color: white;
}

.view-options {
  display: flex;
  align-items: center;
}

/* 笔记内容区域 */
.notes-content {
  margin-bottom: 32px;
  position: relative;
  z-index: 2;
}

.loading-container {
  padding: 32px 0;
}

/* 网格布局 */
.notes-grid {
  display: grid;
  gap: 24px;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
}

.notes-grid.list {
  grid-template-columns: 1fr;
}

.skeleton-card {
  height: 240px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
}

/* 笔记卡片样式 */
.note-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e5e7eb;
  position: relative;
  min-height: 200px;
  display: flex;
  flex-direction: column;
}

.note-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  border-color: #3b82f6;
}

.note-card-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
  height: 24px;
}

.note-card-actions {
  opacity: 0;
  transition: opacity 0.2s ease;
}

.note-card:hover .note-card-actions {
  opacity: 1;
}

.more-btn {
  padding: 4px;
  color: #6b7280;
}

.more-btn:hover {
  color: #374151;
  background-color: #f3f4f6;
}

.note-card-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 12px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.note-card-content {
  color: #6b7280;
  font-size: 14px;
  line-height: 1.6;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 16px;
}

.note-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-top: auto;
}

.note-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
  flex: 1;
}

.category-tag {
  background-color: #dbeafe;
  color: #1e40af;
  border: none;
}

.content-tag {
  background-color: #f3f4f6;
  color: #374151;
  border: none;
}

.more-tags {
  font-size: 12px;
  color: #9ca3af;
  margin-left: 4px;
}

.note-time {
  font-size: 12px;
  color: #9ca3af;
  white-space: nowrap;
  margin-left: 12px;
}

/* 列表视图样式 */
.notes-grid.list .note-card {
  min-height: auto;
  padding: 24px;
}

.notes-grid.list .note-card-title {
  font-size: 20px;
  margin-bottom: 8px;
}

.notes-grid.list .note-card-content {
  -webkit-line-clamp: 2;
  margin-bottom: 12px;
}

/* 空状态样式 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.empty-content {
  text-align: center;
  max-width: 400px;
}

.empty-icon {
  font-size: 64px;
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-content h3 {
  font-size: 20px;
  color: #374151;
  margin: 0 0 8px 0;
}

.empty-content p {
  color: #6b7280;
  margin: 0 0 24px 0;
  font-size: 14px;
}

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 24px 0;
}

/* 下拉菜单样式 */
.danger-item {
  color: #ef4444;
}

.danger-item:hover {
  background-color: #fef2f2;
  color: #dc2626;
}

/* 笔记查看对话框样式 */
.note-view-dialog :deep(.el-dialog) {
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  max-width: 1200px;
}

.note-view-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 24px;
  margin: 0;
}

.note-view-dialog :deep(.el-dialog__title) {
  color: white;
  font-weight: 600;
  font-size: 18px;
}

.note-view-dialog :deep(.el-dialog__headerbtn) {
  color: white;
}

.note-view-dialog :deep(.el-dialog__headerbtn:hover) {
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.note-view-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.note-view {
  max-height: 75vh;
  overflow-y: auto;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

/* 笔记头部信息 */
.note-view-header {
  background: white;
  padding: 24px;
  border-bottom: 1px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.note-view-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.category-tag {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #1e40af;
  border: none;
  font-weight: 500;
}

.content-tag {
  background: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
  font-weight: 500;
}

.view-time {
  margin-left: auto;
  color: #64748b;
  font-size: 12px;
  font-weight: 500;
}


/* 主要内容区域 */
.note-view-main {
  display: grid;
  grid-template-columns: 1fr 520px;
  gap: 24px;
  padding: 24px;
  min-height: 500px;
}

/* 笔记内容区域 */
.note-view-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.content-header {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 16px 20px;
  border-bottom: 1px solid #e2e8f0;
}

.content-header h3 {
  margin: 0;
  color: #374151;
  font-size: 16px;
  font-weight: 600;
}

.content-body {
  padding: 20px;
  max-height: 400px;
  overflow-y: auto;
}

.content-body pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'SF Mono', Monaco, 'Cascadia Code', 'Roboto Mono', Consolas, 'Courier New', monospace;
  margin: 0;
  color: #374151;
  line-height: 1.6;
  font-size: 14px;
}

/* AI助手区域 */
.note-ai-assistant {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  min-width: 480px;
  max-width: 520px;
}

/* 首页主色风格：蓝紫渐变、清新明亮 */
.ai-assistant-header {
  background: #eaf6ff;
  color: #3a466e;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px 0 rgba(106, 141, 255, 0.06);
}

.ai-header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 首页风格icon圆背景 */
.ai-icon-wrapper-home {
  width: 48px;
  height: 48px;
  background: #eaf6ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px 0 rgba(160, 132, 238, 0.13);
}

.ai-icon-home {
  font-size: 28px;
  color: #6a8dff;
}

.ai-header-text h4 {
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 600;
}

.ai-header-text p {
  margin: 0;
  font-size: 12px;
  opacity: 0.9;
}

.toggle-ai-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.toggle-ai-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

.ai-assistant-content {
  padding: 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

/* AI输入区域 */
.ai-input-section {
  margin-bottom: 20px;
}

.ai-input :deep(.el-input-group__append) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  color: white;
  border-radius: 0 8px 8px 0;
}

.send-ai-btn {
  background: #7ecbff;
  border: none;
  color: #fff;
  font-weight: 500;
  transition: all 0.3s ease;
}

.send-ai-btn:hover {
  background: #4bb3fa;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.13);
}

/* 快捷操作按钮 */
.ai-quick-actions {
  display: flex;
  flex-direction: row;
  gap: 12px;
  margin-bottom: 20px;
  justify-content: flex-start;
  flex-wrap: nowrap;
}

/* 首页风格快捷按钮 */
.quick-action-btn {
  background: #eaf6ff;
  border: 1.5px solid #a084ee;
  color: #6a8dff;
  font-weight: 500;
  border-radius: 8px;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 48px;
  transition: all 0.2s;
}
.quick-action-btn.analyze-btn {
  border-color: #6a8dff;
  color: #6a8dff;
}
.quick-action-btn.optimize-btn {
  border-color: #a084ee;
  color: #a084ee;
}
.quick-action-btn.summarize-btn {
  border-color: #7ed6df;
  color: #22a6b3;
}
.quick-action-btn.extend-btn {
  border-color: #b2d7ff;
  color: #6a8dff;
}
.quick-action-btn:active,
.quick-action-btn:focus,
.quick-action-btn:hover {
  background: #d2eaff;
  border-color: #6a8dff;
  color: #6a8dff;
}

.quick-action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.analyze-btn:hover {
  border-color: #3b82f6;
  color: #2563eb;
  background: #eff6ff;
}

.optimize-btn:hover {
  border-color: #f59e0b;
  color: #d97706;
  background: #fffbeb;
}

.summarize-btn:hover {
  border-color: #10b981;
  color: #059669;
  background: #ecfdf5;
}

.extend-btn:hover {
  border-color: #8b5cf6;
  color: #7c3aed;
  background: #faf5ff;
}

/* AI对话记录 */
.ai-chat-history {
  border-top: 1px solid #e2e8f0;
  padding-top: 16px;
}

.chat-history-header {
  margin-bottom: 16px;
}

.chat-history-header h5 {
  margin: 0;
  color: #374151;
  font-size: 14px;
  font-weight: 600;
}

.chat-messages-container {
  max-height: 300px;
  overflow-y: auto;
}

.ai-message {
  display: flex;
  margin-bottom: 16px;
  gap: 12px;
  animation: messageSlideIn 0.3s ease-out;
}

.ai-message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.ai-message.user .message-avatar {
  background: linear-gradient(135deg, #667eea 0%, #b191d1 100%);
  color: white;
}

.ai-message.ai .message-avatar {
  background: linear-gradient(135deg, #daa9df 0%, #e78e9a 100%);
  color: white;
}

.message-content {
  flex: 1;
  max-width: calc(100% - 44px);
}

.ai-message.user .message-content {
  text-align: right;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  white-space: pre-wrap;
  line-height: 1.6;
  font-size: 13px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.ai-message.user .message-text {
  background: #ede7f6;
  color: #a4eba0;
  border-bottom-right-radius: 6px;
}

.ai-message.ai .message-text {
  background: white;
  border: 1px solid #e2e8f0;
  border-bottom-left-radius: 6px;
  color: #374151;
}

.message-time {
  font-size: 11px;
  color: #9ca3af;
  margin-top: 4px;
}

/* 对话框底部按钮 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  background: white;
  border-top: 1px solid #e2e8f0;
}

.close-btn {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #64748b;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #475569;
}

.footer-optimize-btn {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border: none;
  color: white;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.footer-optimize-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.4);
}

.save-optimized-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
  color: white;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.save-optimized-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
}

.edit-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  color: white;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.edit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
}

/* 动画效果 */
@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .notes-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .notes-container {
    padding: 16px;
  }

  .header-section {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .header-left {
    justify-content: space-between;
  }

  .search-input {
    width: 100%;
  }

  .filter-bar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .filter-tabs {
    flex-wrap: wrap;
    justify-content: center;
  }

  .notes-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .note-card {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 24px;
  }

  .filter-tabs {
    gap: 4px;
  }

  .filter-tab {
    padding: 6px 12px;
    font-size: 13px;
  }
}

/* AI对话框样式 */
.ai-chat-dialog :deep(.el-dialog) {
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.ai-chat-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 24px;
  margin: 0;
}

.ai-chat-dialog :deep(.el-dialog__title) {
  color: white;
  font-weight: 600;
  font-size: 18px;
}

.ai-chat-dialog :deep(.el-dialog__headerbtn) {
  color: white;
}

.ai-chat-dialog :deep(.el-dialog__headerbtn:hover) {
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.ai-chat-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.ai-chat-container {
  height: 600px;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: transparent;
  margin-bottom: 0;
}

.message {
  margin-bottom: 20px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  animation: messageSlideIn 0.3s ease-out;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.message.user .message-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message.ai .message-avatar {
  background: linear-gradient(135deg, #93ebfb 0%, #57aef5 100%);
  color: white;
}

.message-content {
  max-width: calc(100% - 60px);
  position: relative;
}

.message.user .message-content {
  text-align: right;
}

.message-text {
  padding: 16px 20px;
  border-radius: 18px;
  white-space: pre-wrap;
  line-height: 1.6;
  font-size: 14px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.message.user .message-text {
  background: #eaf6ff; /* 淡蓝色 */
  color: #2563eb;
  border-bottom-right-radius: 6px;
}

.message.ai .message-text {
  background: white;
  border: 1px solid #e2e8f0;
  border-bottom-left-radius: 6px;
  color: #374151;
}

/* 用户消息文字高亮 */
.user-message-highlight {
  color: #ffffff !important;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.message-time {
  font-size: 11px;
  color: #9ca3af;
  margin-top: 6px;
  opacity: 0.8;
}

.chat-input-area {
  background: white;
  border-top: 1px solid #e2e8f0;
  padding: 20px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.input-wrapper {
  position: relative;
}

.chat-input :deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  padding: 16px;
  font-size: 14px;
  transition: all 0.3s ease;
  resize: none;
}

.chat-input :deep(.el-textarea__inner):focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 12px;
  gap: 12px;
}

.clear-btn {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #64748b;
  transition: all 0.3s ease;
}

.clear-btn:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #475569;
  transform: translateY(-1px);
}

.send-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.send-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.quick-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  padding: 16px 20px;
  background: white;
  border-top: 1px solid #e2e8f0;
  justify-content: center;
}

.quick-btn {
  background: white;
  border: 1px solid #e2e8f0;
  color: #475569;
  font-weight: 500;
  transition: all 0.3s ease;
  border-radius: 8px;
  padding: 8px 16px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.quick-btn:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
  color: #374151;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.optimize-btn:hover {
  border-color: #f59e0b;
  color: #d97706;
}

.tags-btn:hover {
  border-color: #10b981;
  color: #059669;
}

.structure-btn:hover {
  border-color: #8b5cf6;
  color: #7c3aed;
}

.ai-chat-btn {
  margin-left: 12px;
  background: linear-gradient(135deg, #93adfb 0%, #5771f5 100%);
  border: none;
  color: white;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(240, 147, 251, 0.3);
}

.ai-chat-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(240, 147, 251, 0.4);
}

/* 消息动画 */
@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 笔记内AI助手样式 */
.note-ai-assistant {
  margin-top: 24px;
  border-top: 1px solid #e5e7eb;
  padding-top: 20px;
}

.ai-assistant-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.ai-assistant-header h4 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #374151;
  font-size: 16px;
}

.ai-assistant-content {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px;
}

.ai-input-section {
  margin-bottom: 16px;
}

.ai-input-section .ai-input :deep(.el-input-group__append) {
  background: linear-gradient(135deg, #a9daf6 0%, #5771f5 100%);
  border-color: #667eea;
  color: white;
}

.ai-input-section .ai-input :deep(.el-input-group__append):hover {
  background: linear-gradient(135deg, #a9daf6 0%, #5771f5 100%);
}

.ai-quick-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.ai-quick-actions .el-button {
  background: white;
  border: 1px solid #e2e8f0;
  color: #475569;
  transition: all 0.3s ease;
}

.ai-quick-actions .el-button:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.ai-chat-history {
  height: 300px;
  max-height: 300px;
  min-height: 200px;
  overflow-y: auto;
  border-top: 1px solid #e2e8f0;
  padding-top: 16px;
}

.ai-message {
  display: flex;
  margin-bottom: 16px;
  gap: 12px;
}

.ai-message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.ai-message.user .message-avatar {
  background: linear-gradient(135deg, #93adfb 0%, #57b3f5 100%);
  color: white;
}

.ai-message.ai .message-avatar {
  background: linear-gradient(135deg, #93adfb 0%, #5771f5 100%);
  color: white;
}

.message-content {
  flex: 1;
  max-width: calc(100% - 44px);
}

.ai-message.user .message-content {
  text-align: right;
}

.message-text {
  background: white;
  padding: 12px 16px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  white-space: pre-wrap;
  line-height: 1.6;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.ai-message.user .message-text {
  background: linear-gradient(135deg, #a9daf6 0%, #57aef5 100%);
  color: white;
  border-color: #93adfb;
}

.message-time {
  font-size: 11px;
  color: #9ca3af;
  margin-top: 4px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .note-view-main {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .note-ai-assistant {
    order: -1;
  }
}

@media (max-width: 768px) {
  .note-view-dialog :deep(.el-dialog) {
    width: 95% !important;
    margin: 5vh auto !important;
  }

  .note-view {
    max-height: 80vh;
  }

  .note-view-header {
    padding: 16px;
  }


  .note-view-main {
    padding: 16px;
    gap: 16px;
  }

  .ai-quick-actions {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .quick-action-btn {
    height: 40px;
    padding: 8px 12px;
  }

  .dialog-footer {
    flex-direction: column;
    gap: 8px;
  }

  .dialog-footer .el-button {
    width: 100%;
    justify-content: center;
  }

  /* AI聊天对话框响应式 */
  .ai-chat-dialog :deep(.el-dialog) {
    width: 95% !important;
    margin: 5vh auto !important;
  }

  .ai-chat-container {
    height: 70vh;
  }

  .chat-messages {
    padding: 16px;
  }

  .message {
    gap: 8px;
  }

  .message-avatar {
    width: 32px;
    height: 32px;
  }

  .message-text {
    padding: 12px 16px;
    font-size: 13px;
  }

  .quick-actions {
    flex-direction: column;
    gap: 8px;
    padding: 12px 16px;
  }

  .quick-btn {
    justify-content: center;
    padding: 10px 12px;
  }

  .input-actions {
    flex-direction: column;
    gap: 8px;
  }

  .clear-btn,
  .send-btn {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .note-view-dialog :deep(.el-dialog) {
    width: 98% !important;
    margin: 2vh auto !important;
  }

  .note-view {
    max-height: 85vh;
  }

  .note-view-header {
    padding: 12px;
  }

  .note-view-main {
    padding: 12px;
  }

  .content-body {
    padding: 16px;
  }

  .ai-assistant-header {
    padding: 16px;
  }

  .ai-header-left {
    gap: 12px;
  }

  .ai-icon-wrapper {
    width: 40px;
    height: 40px;
  }

  .ai-icon {
    font-size: 20px;
  }

  .ai-header-text h4 {
    font-size: 16px;
  }

  .ai-assistant-content {
    padding: 16px;
  }

  .message-avatar {
    width: 28px;
    height: 28px;
  }

  .message-text {
    padding: 10px 14px;
    font-size: 12px;
  }

  .chat-input-area {
    padding: 16px;
  }

  .quick-actions {
    padding: 12px;
  }

  /* AI聊天对话框响应式 */
  .ai-chat-dialog :deep(.el-dialog) {
    width: 98% !important;
    margin: 2vh auto !important;
  }

  .ai-chat-container {
    height: 80vh;
  }

  .chat-messages {
    padding: 12px;
  }
}

/* 新建笔记AI助手样式 */
.content-editor-container {
  position: relative;
}

.ai-assistant-bar {
  position: absolute;
  top: 8px;
  left: 8px;
  right: 8px;
  z-index: 10;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  border-radius: 6px;
  padding: 8px 12px;
  border: 1px solid #e1e5e9;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.ai-prompt {
  display: flex;
  align-items: center;
  gap: 6px;
}

.ai-icon {
  color: #6366f1;
  font-size: 14px;
  flex-shrink: 0;
}

.ai-text {
  flex: 1;
  color: #6b7280;
  font-size: 12px;
  line-height: 1.3;
}

.ai-send-btn {
  color: #6366f1;
  padding: 2px;
  border-radius: 3px;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.ai-send-btn:hover {
  background-color: #f3f4f6;
  transform: scale(1.05);
}

.ai-send-btn .el-icon {
  font-size: 12px;
}

.content-textarea {
  position: relative;
}

.content-textarea :deep(.el-textarea__inner) {
  padding-top: 45px;
  transition: padding-top 0.3s ease;
}

.content-textarea:focus-within :deep(.el-textarea__inner) {
  padding-top: 8px;
}

.ai-toolbar {
  margin-top: 8px;
  padding: 8px 12px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.ai-tools {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-start;
}

.ai-tools .el-button {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  border-radius: 4px;
  background: white;
  border: 1px solid #e2e8f0;
  color: #475569;
  font-size: 12px;
  height: 28px;
  min-width: auto;
  transition: all 0.2s ease;
}

.ai-tools .el-button:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #334155;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.ai-tools .el-button .el-icon {
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ai-assistant-bar {
    position: static;
    margin-bottom: 8px;
  }

  .content-textarea :deep(.el-textarea__inner) {
    padding-top: 8px;
  }

  .ai-toolbar {
    padding: 6px 8px;
  }

  .ai-tools {
    justify-content: flex-start;
    gap: 6px;
  }

  .ai-tools .el-button {
    padding: 4px 8px;
    font-size: 11px;
    height: 24px;
  }

  .ai-tools .el-button .el-icon {
    font-size: 11px;
  }
}
</style>