<template>
  <div class="avatar-vcn-test">
    <h2>数字人音色配置测试</h2>
    
    <div class="test-section">
      <h3>选择数字人形象：</h3>
      <el-radio-group v-model="selectedAvatarId" @change="onAvatarChange">
        <el-radio label="110017006">明哥音色 (x4_mingge)</el-radio>
        <el-radio label="138801001">悦小妮音色 (x4_yuexiaoni_assist)</el-radio>
        <el-radio label="110021006">灵小雨音色 (x4_lingxiaoyu_assist)</el-radio>
        <el-radio label="110117005">灵小颖音色 (x4_lingxiaoying_assist)</el-radio>
      </el-radio-group>
    </div>

    <div class="test-result">
      <h3>配置结果：</h3>
      <p><strong>Avatar ID:</strong> {{ selectedAvatarId }}</p>
      <p><strong>VCN音色:</strong> {{ currentVcn }}</p>
      <p><strong>语音类型:</strong> {{ currentVoiceType }}</p>
      <p><strong>显示名称:</strong> {{ currentName }}</p>
    </div>

    <div class="test-actions">
      <el-button type="primary" @click="testConfiguration">测试配置</el-button>
      <el-button type="success" @click="copyConfig">复制配置</el-button>
    </div>

    <div class="config-output" v-if="configOutput">
      <h3>生成的配置：</h3>
      <pre>{{ configOutput }}</pre>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const selectedAvatarId = ref('110117005')
const configOutput = ref('')

// 根据avatar_id获取对应的vcn音色
const getVcnByAvatarId = (avatarId) => {
  const avatarVcnMap = {
    '110017006': 'x4_mingge',                        // 明哥音色
    '138801001': 'x4_yuexiaoni_assist',              // 悦小妮音色
    '110021006': 'x4_lingxiaoyu_assist',             // 灵小雨音色
    '110117005': 'x4_lingxiaoying_assist'            // 灵小颖音色
  }
  return avatarVcnMap[avatarId] || 'x4_lingxiaoying_assist'
}

// 根据avatar_id获取语音类型
const getVoiceTypeByAvatarId = (avatarId) => {
  const avatarVoiceMap = {
    '110017006': 'male',   // 明哥音色
    '138801001': 'female', // 悦小妮音色
    '110021006': 'female', // 灵小雨音色
    '110117005': 'female'  // 灵小颖音色
  }
  return avatarVoiceMap[avatarId] || 'female'
}

// 根据avatar_id获取数字人名称
const getAvatarNameById = (avatarId) => {
  const avatarNameMap = {
    '110017006': '明哥音色',
    '138801001': '悦小妮音色',
    '110021006': '灵小雨音色',
    '110117005': '灵小颖音色'
  }
  return avatarNameMap[avatarId] || '未知形象'
}

const currentVcn = computed(() => getVcnByAvatarId(selectedAvatarId.value))
const currentVoiceType = computed(() => getVoiceTypeByAvatarId(selectedAvatarId.value))
const currentName = computed(() => getAvatarNameById(selectedAvatarId.value))

function onAvatarChange(avatarId) {
  console.log('选择的数字人:', avatarId)
  console.log('对应的VCN:', getVcnByAvatarId(avatarId))
}

function testConfiguration() {
  const config = {
    avatarId: selectedAvatarId.value,
    vcn: currentVcn.value,
    voiceType: currentVoiceType.value,
    name: currentName.value,
    ttsConfig: {
      vcn: currentVcn.value,
      speed: 60,
      pitch: 50,
      volume: 100,
      emotion: 13
    }
  }
  
  configOutput.value = JSON.stringify(config, null, 2)
  ElMessage.success('配置测试完成！')
}

function copyConfig() {
  if (configOutput.value) {
    navigator.clipboard.writeText(configOutput.value).then(() => {
      ElMessage.success('配置已复制到剪贴板！')
    }).catch(() => {
      ElMessage.error('复制失败，请手动复制')
    })
  } else {
    ElMessage.warning('请先测试配置')
  }
}
</script>

<style scoped>
.avatar-vcn-test {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-section {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.test-result {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.test-result p {
  margin: 5px 0;
}

.test-actions {
  margin-bottom: 20px;
}

.config-output {
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.config-output pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

h2, h3 {
  color: #303133;
}
</style>
