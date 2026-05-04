<template>
  <div class="teacher-assistance-create">
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <span>新增帮扶记录</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="assistance-form"
      >
        <el-form-item label="学生ID" prop="studentId">
          <el-input-number
              v-model="formData.studentId"
              :min="1"
              placeholder="请输入学生ID"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="困难类型" prop="difficultyType">
          <el-select v-model="formData.difficultyType" placeholder="请选择困难类型">
            <el-option label="就业困难" value="就业困难" />
            <el-option label="学业困难" value="学业困难" />
            <el-option label="心理困难" value="心理困难" />
            <el-option label="经济困难" value="经济困难" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="困难等级" prop="difficultyLevel">
          <el-radio-group v-model="formData.difficultyLevel">
            <el-radio label="low">低</el-radio>
            <el-radio label="medium">中</el-radio>
            <el-radio label="high">高</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="困难描述" prop="description">
          <el-input
              v-model="formData.description"
              type="textarea"
              :rows="4"
              placeholder="请详细描述学生遇到的困难"
          />
        </el-form-item>

        <el-form-item label="帮扶计划" prop="assistancePlan">
          <el-input
              v-model="formData.assistancePlan"
              type="textarea"
              :rows="4"
              placeholder="请输入详细的帮扶计划"
          />
        </el-form-item>

        <el-form-item label="帮扶措施" prop="assistanceActions">
          <el-input
              v-model="formData.assistanceActions"
              type="textarea"
              :rows="4"
              placeholder="请输入已采取或计划采取的帮扶措施"
          />
        </el-form-item>

        <el-form-item label="预期结果" prop="result">
          <el-input
              v-model="formData.result"
              placeholder="请输入预期达到的帮扶结果"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            提交
          </el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { saveAssistanceRecord } from '@/api/teacher'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const formData = ref({
  studentId: null,
  difficultyType: '',
  difficultyLevel: '',
  description: '',
  assistancePlan: '',
  assistanceActions: '',
  result: ''
})

const rules = {
  studentId: [{ required: true, message: '请输入学生ID', trigger: 'blur' }],
  difficultyType: [{ required: true, message: '请选择困难类型', trigger: 'change' }],
  difficultyLevel: [{ required: true, message: '请选择困难等级', trigger: 'change' }],
  description: [{ required: true, message: '请输入困难描述', trigger: 'blur' }],
  assistancePlan: [{ required: true, message: '请输入帮扶计划', trigger: 'blur' }]
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await saveAssistanceRecord(formData.value)
        if (response.success) {
          ElMessage.success('新增帮扶记录成功')
          router.push('/teacher/assistance')
        }
      } catch (error) {
        ElMessage.error('新增失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleReset = () => {
  formRef.value.resetFields()
}

const goBack = () => {
  router.back()
}
</script>

<style scoped lang="scss">
.teacher-assistance-create {
  .form-card {
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 16px;
      font-weight: 600;
    }

    .assistance-form {
      max-width: 800px;
    }
  }
}
</style>
