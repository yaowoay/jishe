<template>
  <div
    class="step-flex-card"
    :class="{ active: hovered || active, 'avatar-step': hovered || active }"
    @mouseenter="$emit('mouseenter')"
    @mouseleave="$emit('mouseleave')"
  >
    <div class="step-badge"><span>③</span></div>
    <template v-if="hovered || active">
      <div class="step-header"><el-icon class="step-icon"><i class="el-icon-user"></i></el-icon> 第三步：数字人设置</div>
      <el-form 
        :model="localSettings" 
        :rules="avatarRules" 
        ref="avatarFormRef" 
        label-width="120px"
        class="avatar-form"
      >
        <el-form-item label="数字人形象" prop="avatarType">
          <el-radio-group v-model="localSettings.avatarType" class="avatar-image-options" @change="emitUpdate">
            <el-radio :label="'110017006'" class="avatar-image-radio">
              <img src="@/assets/images/avatar4.png" class="avatar-image" alt="数字人1" />
            </el-radio>
            <el-radio :label="'138801001'" class="avatar-image-radio">
              <img src="@/assets/images/avatar3.png" class="avatar-image" alt="数字人2" />
            </el-radio>
            <el-radio :label="'110021006'" class="avatar-image-radio">
              <img src="@/assets/images/avatar2.png" class="avatar-image" alt="数字人3" />
            </el-radio>
            <el-radio :label="'110117005'" class="avatar-image-radio">
              <img src="@/assets/images/avatar1.png" class="avatar-image" alt="数字人4" />
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="语气风格" prop="tone">
          <el-select v-model="localSettings.tone" placeholder="请选择语气风格" style="width: 100%" @change="emitUpdate">
            <el-option label="正式严肃" value="正式严肃" />
            <el-option label="温和友好" value="温和友好" />
            <el-option label="轻松幽默" value="轻松幽默" />
            <el-option label="专业严谨" value="专业严谨" />
          </el-select>
        </el-form-item>
        <el-form-item label="语速设置" prop="speechRate">
          <el-slider v-model="localSettings.speechRate" :min="0.5" :max="2.0" :step="0.1" :format-tooltip="formatSpeechRate" show-input @change="emitUpdate" />
        </el-form-item>
        <el-form-item label="音频测试" prop="audioTest">
          <div class="test-section">
            <el-button @click="testAudio" :loading="audioTesting" type="primary">测试音频设备</el-button>
            <div v-if="audioTestResult" class="test-result">
              <el-tag :type="audioTestResult.success ? 'success' : 'danger'">{{ audioTestResult.message }}</el-tag>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="视频测试" prop="videoTest">
          <div class="test-section">
            <el-button @click="testVideo" :loading="videoTesting" type="primary">测试视频设备</el-button>
            <div v-if="videoTestResult" class="test-result">
              <el-tag :type="videoTestResult.success ? 'success' : 'danger'">{{ videoTestResult.message }}</el-tag>
            </div>
          </div>
        </el-form-item>
      </el-form>
    </template>
    <template v-else>
      <div class="step-header mini"><el-icon class="step-icon"><i class="el-icon-user"></i></el-icon> 数字人设置</div>
      <div v-if="isComplete" class="mini-brief set">
        <el-icon class="mini-done"><i class="el-icon-check"></i></el-icon>
        <span>{{ getAvatarName(localSettings.avatarType) }} / {{ localSettings.tone }}</span>
      </div>
      <div v-else class="mini-brief">选择面试官形象</div>
    </template>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  hovered: Boolean,
  active: Boolean,
  settings: Object,
  isComplete: Boolean
})

const emit = defineEmits(['mouseenter', 'mouseleave', 'update'])

const audioTesting = ref(false)
const videoTesting = ref(false)
const audioTestResult = ref(null)
const videoTestResult = ref(null)
const localSettings = ref({
  avatarType: '',
  tone: '',
  speechRate: 1.0,
  audioTest: false,
  videoTest: false
})

watch(() => props.settings, (newVal) => {
  if (newVal) {
    localSettings.value = { ...newVal }
  }
}, { immediate: true, deep: true })

const avatarRules = {
  avatarType: [
    { required: true, message: '请选择数字人形象', trigger: 'change' }
  ],
  tone: [
    { required: true, message: '请选择语气风格', trigger: 'change' }
  ]
}

function formatSpeechRate(val) {
  return val.toFixed(1) + 'x'
}

function getAvatarName(avatarId) {
  const avatarNameMap = {
    '110017006': '面试官1',
    '138801001': '面试官2',
    '110021006': '面试官3',
    '110117005': '面试官4'
  }
  return avatarNameMap[avatarId] || '未知形象'
}

async function testAudio() {
  audioTesting.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 2000))
    audioTestResult.value = {
      success: true,
      message: '音频设备测试通过'
    }
    localSettings.value.audioTest = true
    emitUpdate()
    ElMessage.success('音频设备测试成功！')
  } catch (error) {
    audioTestResult.value = {
      success: false,
      message: '音频设备测试失败'
    }
    ElMessage.error('音频设备测试失败')
  } finally {
    audioTesting.value = false
  }
}

async function testVideo() {
  videoTesting.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 2000))
    videoTestResult.value = {
      success: true,
      message: '视频设备测试通过'
    }
    localSettings.value.videoTest = true
    emitUpdate()
    ElMessage.success('视频设备测试成功！')
  } catch (error) {
    videoTestResult.value = {
      success: false,
      message: '视频设备测试失败'
    }
    ElMessage.error('视频设备测试失败')
  } finally {
    videoTesting.value = false
  }
}

function emitUpdate() {
  emit('update', localSettings.value)
}
</script>

<style scoped>
.step-flex-card {
  background: rgba(255,255,255,0.92);
  border-radius: 32px;
  box-shadow: 0 12px 48px 0 rgba(80,120,200,0.13), 0 2px 16px #e6e6e6;
  transition: all 0.35s cubic-bezier(.4,2,.6,1);
  width: 420px;
  min-height: 340px;
  padding: 56px 38px 32px 38px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  backdrop-filter: blur(8px);
  border: 2px solid #e0e7ff;
}

.step-flex-card.active {
  z-index: 2;
  box-shadow: 0 24px 64px #b3c0d1, 0 4px 24px #409eff44;
  transform: scale(1.09) translateY(-8px);
  cursor: default;
  border: 3px solid #409eff;
  background: rgba(255,255,255,0.98);
}

.step-flex-card.active.avatar-step {
  width: 540px;
  min-height: 520px;
}

.step-badge {
  position: absolute;
  top: 18px;
  left: 28px;
  background: linear-gradient(135deg, #e3f0ff 60%, #b3d8ff 100%);
  color: #409eff;
  font-size: 1.3rem;
  font-weight: 700;
  border-radius: 50%;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px #e0e7ff;
  border: 2px solid #b3d8ff;
  z-index: 2;
}

.step-header {
  font-size: 26px;
  font-weight: 700;
  color: #222;
  margin-bottom: 18px;
  letter-spacing: 1px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.step-header.mini {
  font-size: 22px;
  margin-bottom: 12px;
}

.step-icon {
  font-size: 1.5em;
  color: #409eff;
}

.avatar-form {
  padding: 20px 0;
}

.avatar-image-options {
  display: flex;
  flex-wrap: wrap;
  gap: 16px 16px;
  justify-content: center;
  margin-bottom: 18px;
  width: auto !important;
}

.avatar-image-radio {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 0;
  cursor: pointer;
  border: none;
  background: none;
  box-shadow: none;
  padding: 0;
  min-width: 120px;
  min-height: 120px;
  width: auto !important;
}

.avatar-image {
  width: 120px;
  height: 120px;
  border-radius: 16px;
  border: 2.5px solid #e0e7ff;
  box-shadow: 0 2px 10px #b3d8ff22;
  object-fit: cover;
  transition: border 0.2s, box-shadow 0.2s;
}

.el-radio.is-checked .avatar-image {
  border: 3px solid #409eff;
  box-shadow: 0 4px 18px #409eff33;
}

.test-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.test-result {
  margin-top: 8px;
}

.mini-brief {
  font-size: 18px;
  color: #8a98b3;
  margin-top: 12px;
  font-weight: 500;
  text-align: left;
  letter-spacing: 1px;
}

.mini-brief.set {
  color: #67c23a;
  font-weight: 600;
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.mini-done {
  color: #67c23a;
  font-size: 1.2em;
}
</style>
