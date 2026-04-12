<template>
  <div class="backup-chat-container">
    <div class="backup-header">
      <h3>智面Buff AI助手 </h3>
      <div class="header-actions">
        <button @click="tryOriginalCoze" class="retry-btn">尝试原版助手</button>
        <button @click="$emit('close')" class="close-btn">×</button>
      </div>
    </div>
    
    <div class="chat-messages" ref="messagesContainer">
      <div v-for="(message, index) in messages" :key="index" 
           :class="['message', message.role]">
        <div class="message-content">
          <div class="message-text">{{ message.content }}</div>
          <div class="message-time">{{ formatTime(message.timestamp) }}</div>
        </div>
      </div>
      <div v-if="isLoading" class="message assistant">
        <div class="message-content">
          <div class="typing-indicator">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="input-area">
      <div class="input-container">
        <input
          v-model="inputText"
          @keydown.enter="sendMessage"
          @focus="onInputFocus"
          placeholder="请输入您的问题..."
          class="chat-input"
          ref="chatInput"
        />
        <button @click="sendMessage" :disabled="!inputText.trim() || isLoading" class="send-btn">
          <span v-if="!isLoading">发送</span>
          <span v-else>...</span>
        </button>
      </div>
      <div class="input-tips">
        <span>💡 提示：您可以询问面试技巧、简历优化、职业规划等问题</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BackupChatInput',
  emits: ['close', 'try-original'],
  
  data() {
    return {
      inputText: '',
      isLoading: false,
      messages: [
        {
          role: 'assistant',
          content: '您好！我是AI助手。我可以帮助您解答面试相关的问题，包括面试技巧、简历优化、职业规划等。请随时向我提问！',
          timestamp: new Date()
        }
      ]
    }
  },
  
  mounted() {
    // 确保输入框可以获得焦点
    this.$nextTick(() => {
      if (this.$refs.chatInput) {
        this.$refs.chatInput.focus()
      }
    })
  },
  
  methods: {
    sendMessage() {
      if (!this.inputText.trim() || this.isLoading) return
      
      const userMessage = {
        role: 'user',
        content: this.inputText.trim(),
        timestamp: new Date()
      }
      
      this.messages.push(userMessage)
      const userInput = this.inputText.trim()
      this.inputText = ''
      this.isLoading = true
      
      // 滚动到底部
      this.$nextTick(() => {
        this.scrollToBottom()
      })
      
      // 模拟 AI 回复
      setTimeout(() => {
        this.generateAIResponse(userInput)
      }, 1000 + Math.random() * 2000)
    },
    
    generateAIResponse(userInput) {
      // 简单的关键词匹配回复
      let response = ''
      
      if (userInput.includes('面试') || userInput.includes('面试技巧')) {
        response = '关于面试技巧，我建议您：\n1. 提前了解公司背景和职位要求\n2. 准备常见面试问题的回答\n3. 练习自我介绍，控制在2-3分钟\n4. 准备几个想要问面试官的问题\n5. 注意着装和仪态，保持自信\n\n您还有其他具体的面试问题吗？'
      } else if (userInput.includes('简历') || userInput.includes('简历优化')) {
        response = '简历优化建议：\n1. 突出与目标职位相关的经验和技能\n2. 使用量化数据展示工作成果\n3. 保持简洁，通常1-2页为宜\n4. 使用清晰的格式和专业的字体\n5. 检查语法和拼写错误\n\n您希望我帮您分析简历的哪个部分？'
      } else if (userInput.includes('职业规划') || userInput.includes('职业发展')) {
        response = '职业规划建议：\n1. 明确短期和长期职业目标\n2. 评估当前技能与目标岗位的差距\n3. 制定技能提升计划\n4. 建立行业人脉网络\n5. 定期回顾和调整规划\n\n您目前处于职业发展的哪个阶段？'
      } else if (userInput.includes('薪资') || userInput.includes('工资') || userInput.includes('待遇')) {
        response = '关于薪资谈判：\n1. 提前了解行业薪资水平\n2. 准备充分的理由支持您的期望薪资\n3. 考虑整体薪酬包，不只是基本工资\n4. 选择合适的谈判时机\n5. 保持专业和礼貌的态度\n\n您想了解哪个行业或职位的薪资情况？'
      } else {
        response = '感谢您的问题！作为智面Buff AI助手，我专注于帮助您解决面试和职业发展相关的问题。\n\n我可以帮助您：\n• 面试技巧和准备\n• 简历优化建议\n• 职业规划指导\n• 薪资谈判策略\n• 行业趋势分析\n\n请告诉我您具体想了解哪方面的内容？'
      }
      
      const aiMessage = {
        role: 'assistant',
        content: response,
        timestamp: new Date()
      }
      
      this.messages.push(aiMessage)
      this.isLoading = false
      
      this.$nextTick(() => {
        this.scrollToBottom()
      })
    },
    
    scrollToBottom() {
      const container = this.$refs.messagesContainer
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    },
    
    formatTime(timestamp) {
      return timestamp.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    },
    
    onInputFocus() {
      console.log('备用输入框获得焦点')
    },
    
    tryOriginalCoze() {
      this.$emit('try-original')
    }
  }
}
</script>

<style scoped>
.backup-chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.backup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(90deg, #409eff 0%, #337ecc 100%);
  color: white;
}

.backup-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.retry-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background 0.3s;
}

.retry-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 20px;
  cursor: pointer;
  padding: 4px;
  line-height: 1;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f8f9fa;
}

.message {
  margin-bottom: 16px;
  display: flex;
}

.message.user {
  justify-content: flex-end;
}

.message.assistant {
  justify-content: flex-start;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  position: relative;
}

.message.user .message-content {
  background: #409eff;
  color: white;
  border-bottom-right-radius: 4px;
}

.message.assistant .message-content {
  background: white;
  color: #333;
  border: 1px solid #e4e7ed;
  border-bottom-left-radius: 4px;
}

.message-text {
  white-space: pre-line;
  line-height: 1.5;
}

.message-time {
  font-size: 11px;
  opacity: 0.7;
  margin-top: 4px;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  align-items: center;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  background: #409eff;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}

.input-area {
  padding: 16px 20px;
  background: white;
  border-top: 1px solid #e4e7ed;
}

.input-container {
  display: flex;
  gap: 12px;
  align-items: center;
}

.chat-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #dcdfe6;
  border-radius: 20px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.chat-input:focus {
  border-color: #409eff;
}

.send-btn {
  background: #409eff;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s;
}

.send-btn:hover:not(:disabled) {
  background: #337ecc;
}

.send-btn:disabled {
  background: #c0c4cc;
  cursor: not-allowed;
}

.input-tips {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  text-align: center;
}
</style>
