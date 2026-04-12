<template>
  <div class="problem-detail">
    <!-- 题目详情卡片 -->
    <el-card v-if="problem" class="detail-card" shadow="hover">
      <!-- 顶部操作栏：返回、题目标题、上一题、下一题 -->
      <div class="header-row">
        <el-button type="text" @click="$emit('back')">
          <el-icon><i class="el-icon-arrow-left"></i></el-icon> 返回
        </el-button>
        <span class="problem-title">{{ problem?.title }}</span>
        <!-- 上一题、下一题按钮由父组件控制可用性 -->
        <el-button type="primary" class="prev-btn" @click="$emit('prev')" :disabled="$attrs['prev-disabled']">
          <el-icon><i class="el-icon-arrow-left"></i></el-icon> 上一题
        </el-button>
        <el-button type="primary" class="next-btn" @click="$emit('next')" :disabled="$attrs['next-disabled']">
          下一题 <el-icon><i class="el-icon-arrow-right"></i></el-icon>
        </el-button>
      </div>
      <el-divider />
      <!-- 题目信息描述区 -->
      <el-descriptions :column="2" border class="mb-2">
        <el-descriptions-item label="题目ID">{{ problem?.id }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="problem?.status === '通过'" type="success">已通过</el-tag>
          <el-tag v-else-if="problem?.status === '未尝试'" type="info">未尝试</el-tag>
          <el-tag v-else type="danger">未通过</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <!-- 题目描述和示例输入输出 -->
      <div class="desc-io-area">
        <div class="problem-desc">
          <h3>题目描述</h3>
          <div>{{ problem?.description }}</div>
        </div>
        <div class="problem-examples" v-if="problem?.examples && problem.examples.length">
          <h3>示例</h3>
          <el-table :data="problem.examples" border stripe style="width: 100%;">
            <el-table-column prop="input" label="输入" />
            <el-table-column prop="output" label="输出" />
          </el-table>
        </div>
      </div>
      <el-divider />
      <!-- 代码操作区：语言选择、提交、运行 -->
      <div class="actions-row">
        <el-select v-model="language" @change="onLanguageChange" style="width:120px;margin-right:12px;">
          <el-option label="Python" value="python"/>
          <el-option label="Java" value="java"/>
          <el-option label="C" value="c"/>
        </el-select>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">提交代码</el-button>
        <el-button @click="handleRun" :loading="running">运行代码</el-button>
      </div>
      <!-- 运行结果展示区 -->
      <div v-if="runResult" class="run-result">
        <el-alert
          v-if="runResult.success"
          :title="`输出：${runResult.output}`"
          type="success"
          show-icon
          :description="`耗时：${runResult.time}ms，内存：${runResult.memory}KB`"
        />
        <el-alert
          v-else
          :title="`运行/编译错误`"
          type="error"
          show-icon
          :description="runResult.error"
        />
      </div>
      <!-- 评测结果展示区 -->
      <div v-if="submitResult" class="submit-result">
        <el-alert
          :title="`评测结果：${submitResult.result}`"
          :type="submitResult.result === '通过' ? 'success' : 'error'"
          show-icon
          :description="submitResult.message"
        />
      </div>
      <!-- 代码编辑区 -->
      <div class="code-area">
        <el-input
          type="textarea"
          :rows="15"
          placeholder="请在此输入你的代码..."
          v-model="userCode"
          :disabled="submitting || running"
        />
      </div>
    </el-card>
    <div v-else style="text-align:center;padding:40px 0;">暂无题目信息</div>
  </div>
</template>

<script setup>
import { ref, defineProps, watch, defineEmits } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['refresh'])

// 接收父组件传递的题目信息
const props = defineProps({
  problem: {
    type: Object,
    required: true
  }
})

// 用户输入的代码
const userCode = ref('')
// 选择的编程语言
const language = ref('python')
// 运行结果对象
const runResult = ref(null)
// 评测（提交）结果对象
const submitResult = ref(null)
// 提交状态
const submitting = ref(false)
// 运行状态
const running = ref(false)

// 先定义 setDefaultTemplate 和 loadDefaultCode
const setDefaultTemplate = () => {
  const templates = {
    python: 'def solution():\n    # 在这里实现你的解决方案\n    pass\n\n# 测试代码\nif __name__ == "__main__":\n    result = solution()\n    print(result)',
    java: 'public class Solution {\n    public static void main(String[] args) {\n        // 在这里实现你的解决方案\n        System.out.println("Hello World");\n    }\n}',
    c: '#include <stdio.h>\n\nint main() {\n    // 在这里实现你的解决方案\n    printf("Hello World\\n");\n    return 0;\n}'
  }
  userCode.value = templates[language.value] || templates.python
}

const loadDefaultCode = async () => {
  if (!props.problem || !props.problem.id) return
  try {
    const response = await request.get(`/problems/${props.problem.id}/template`, {
      params: { language: language.value }
    })
    if (response.data && response.data.code) {
      userCode.value = response.data.code
    } else {
      setDefaultTemplate()
    }
  } catch (error) {
    setDefaultTemplate()
  }
}

// 监听题目变化，重置相关状态
watch(() => props.problem, (newProblem) => {
  if (newProblem) {
    userCode.value = ''
    runResult.value = null
    submitResult.value = null
  }
}, { immediate: true, deep: true })

// 监听语言变化，手动加载模板
const onLanguageChange = () => {
  loadDefaultCode()
}

// 运行代码，调用后端接口
const handleRun = async () => {
  if (!userCode.value.trim()) {
    ElMessage.warning('请输入代码')
    return
  }
  running.value = true
  runResult.value = null
  try {
    const response = await request.post('/oj/run', {
      problemId: props.problem.id,
      code: userCode.value,
      language: language.value
    })
    runResult.value = response
    console.log('运行响应:', response)
    const runSuccess = response?.success === true || response?.result === '通过' || /通过|恭喜|success|pass/i.test(response?.message || '')
    if (runSuccess) {
      ElMessage.success('运行成功')
    } else {
      ElMessage.error('运行失败')
    }
  } catch (error) {
    runResult.value = { success: false, error: '网络错误或服务器异常' }
    ElMessage.error('运行失败，请检查网络连接')
  } finally {
    running.value = false
  }
}

// 提交代码，调用后端接口
const handleSubmit = async () => {
  if (!userCode.value.trim()) {
    ElMessage.warning('请输入代码')
    return
  }
  submitting.value = true
  submitResult.value = null
  try {
    const response = await request.post('/oj/submit', {
      problemId: props.problem.id,
      code: userCode.value,
      language: language.value
    })
    submitResult.value = response
    console.log('判题响应:', response)
    const result = response?.result
    const message = response?.message || ''
    if (
      result === '通过' ||
      /通过|恭喜|success|pass/i.test(message)
    ) {
      ElMessage.success('恭喜！代码通过所有测试用例')
      emit('refresh')
    } else {
      ElMessage.error('代码未通过测试')
    }
  } catch (error) {
    submitResult.value = { result: '未通过', message: '网络错误或服务器异常' }
    ElMessage.error('提交失败，请检查网络连接')
  } finally {
    submitting.value = false
  }
}

console.log('currentProblem.value', props.problem)
</script>

<style scoped>
.problem-detail {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 0;
}
.detail-card {
  border-radius: 12px;
  background: #fff;
  padding: 32px;
}
.header-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}
.problem-title {
  font-size: 26px;
  font-weight: bold;
  margin-left: 16px;
  flex: 1;
}
.prev-btn {
  margin-left: 12px;
}
.next-btn {
  margin-left: 12px;
}
.mb-2 {
  margin-bottom: 20px;
}
.desc-io-area {
  margin-bottom: 20px;
}
.problem-desc {
  margin-bottom: 12px;
  font-size: 16px;
  color: #333;
}
.problem-desc h3 {
  margin-bottom: 8px;
  color: #222;
}
.problem-examples h3 {
  margin-bottom: 8px;
  color: #222;
}
.actions-row {
  margin: 24px 0 12px 0;
  display: flex;
  gap: 16px;
}
.code-area {
  margin-top: 12px;
}
.run-result,
.submit-result {
  margin: 16px 0;
}
</style>