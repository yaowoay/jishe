<template>
  <el-dialog
    v-model="localVisible"
    title="AI 智能对话"
    width="70vw"
    top="10vh"
    :close-on-click-modal="false"
    :show-close="true"
    class="big-ai-dialog"
  >
    <div class="dialog-content-area big-content-area">
      <div class="dialog-history big-dialog-history" ref="dialogHistoryRef">
        <div
          v-for="(msg, idx) in dialogList"
          :key="idx"
          :class="['dialog-msg', msg.role]"
        >
          <div class="msg-bubble">
            <span class="msg-role">{{ msg.role === 'user' ? '我：' : 'AI：' }}</span>
            <span class="msg-text">{{ msg.text }}</span>
          </div>
        </div>
      </div>

      <div class="dialog-input-area big-input-area">
        <div class="new-input-card big-input-card">
          <el-input
            v-model="inputMessage"
            placeholder="请输入问题"
            @keydown.enter.prevent="sendMessage"
            clearable
          />
          <el-button
            type="text"
            :disabled="!inputMessage.trim()"
            @click="sendMessage"
            circle
          >
            <el-icon><Promotion /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, nextTick, watch, computed } from 'vue'
import { Promotion } from '@element-plus/icons-vue'
import { getAIResponse } from '@/api/cozeApi'

const props = defineProps({ visible: Boolean, initialUserMsg: String })
const emit = defineEmits(['update:visible'])

const localVisible = computed({
  get: () => props.visible,
  set: v => emit('update:visible', v)
})

const inputMessage = ref('')
const dialogList = ref([
  { role: 'ai', text: '您好！我是 AI 助手，请问有什么可以帮您？' }
])
const dialogHistoryRef = ref(null)

watch(() => props.visible, async v => {
  if (v && props.initialUserMsg) {
    dialogList.value = [
      { role: 'ai', text: '您好！我是 AI 助手，请问有什么可以帮您？' },
      { role: 'user', text: props.initialUserMsg }
    ]
    nextTick(scrollToBottom)
    const ai = await getAIResponse(props.initialUserMsg)
    dialogList.value.push({ role: 'ai', text: ai })
    nextTick(scrollToBottom)
  }
})

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return
  dialogList.value.push({ role: 'user', text: inputMessage.value })
  const msg = inputMessage.value
  inputMessage.value = ''
  nextTick(scrollToBottom)

  const ai = await getAIResponse(msg)
  dialogList.value.push({ role: 'ai', text: ai })
  nextTick(scrollToBottom)
}

function scrollToBottom() {
  nextTick(() => {
    if (dialogHistoryRef.value) {
      dialogHistoryRef.value.scrollTop = dialogHistoryRef.value.scrollHeight
    }
  })
}
</script>


<style scoped>

.big-ai-dialog >>> .el-dialog{width:60vw!important;max-width:180vw!important;min-width:unset!important;min-height:unset!important;display:flex;flex-direction:column;justify-content:center;}
.big-ai-dialog >>> .el-dialog__body{padding:0 0 24px 0!important;}
.big-content-area{display:flex;flex-direction:column;min-height:0;max-height:none;height:75vh;}
.big-dialog-history{flex:1 1 auto;overflow-y:auto;padding:16px 0 24px 0;min-height:0;max-height:75vh;height:100%;}
.big-input-area{margin-top:8px;}
.big-input-card{width:90%;background:#fff;border-radius:40px;border:2px solid #b3d8ff;display:flex;align-items:center;padding:0 24px;min-height:56px;box-shadow:0 4px 18px 0 #b3d8ff44;position:relative;margin:0 auto;}
.big-modern-input{flex:1;border:none;background:transparent;font-size:20px;min-height:40px;box-shadow:none;outline:none;color:#222;}
.big-send-btn{background:#409eff;color:#fff;border:none;box-shadow:0 2px 12px #409eff22;width:44px;height:44px;min-width:44px;min-height:44px;display:flex;align-items:center;justify-content:center;font-size:24px;border-radius:50%;margin-left:12px;transition:background .2s;}
.big-send-btn:disabled{background:#e3f0fa;color:#b0b0b0;}
.dialog-msg{display:flex;align-items:flex-start;margin-bottom:10px;}
.dialog-msg.user{justify-content:flex-end;}
.dialog-msg.ai{justify-content:flex-start;}
.msg-bubble{background:#f5f7fa;border-radius:16px;padding:10px 18px;font-size:16px;color:#222;max-width:80%;word-break:break-all;box-shadow:0 2px 8px #e0e3e8;}
.dialog-msg.user .msg-bubble{background:#e3f2fd;color:#1976d2;align-self:flex-end;}
.dialog-msg.ai .msg-bubble{background:#fff;color:#222;align-self:flex-start;}
.msg-role{font-weight:bold;margin-right:6px;}
</style>