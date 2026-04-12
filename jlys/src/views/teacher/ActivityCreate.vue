<template>
  <div class="teacher-activity-create">
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <span>创建活动</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="activity-form"
      >
        <el-form-item label="活动名称" prop="title">
          <el-input v-model="formData.title" placeholder="请输入活动名称" />
        </el-form-item>

        <el-form-item label="活动类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择活动类型">
            <el-option label="宣讲会" value="宣讲会" />
            <el-option label="招聘会" value="招聘会" />
            <el-option label="双选会" value="双选会" />
            <el-option label="培训讲座" value="培训讲座" />
            <el-option label="企业参观" value="企业参观" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="活动形式" prop="mode">
          <el-radio-group v-model="formData.mode">
            <el-radio label="线上">线上</el-radio>
            <el-radio label="线下">线下</el-radio>
            <el-radio label="线上线下结合">线上线下结合</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="活动地点" prop="location">
          <el-input v-model="formData.location" placeholder="请输入活动地点" />
        </el-form-item>

        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
              v-model="formData.startTime"
              type="datetime"
              placeholder="选择开始时间"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
              v-model="formData.endTime"
              type="datetime"
              placeholder="选择结束时间"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="最大参与人数" prop="maxParticipants">
          <el-input-number
              v-model="formData.maxParticipants"
              :min="1"
              :max="10000"
              placeholder="请输入最大参与人数"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="活动海报" prop="posterUrl">
          <el-upload
              class="poster-uploader"
              action="/api/upload"
              :show-file-list="false"
              :on-success="handlePosterSuccess"
              :before-upload="beforePosterUpload"
          >
            <img v-if="formData.posterUrl" :src="formData.posterUrl" class="poster" />
            <el-icon v-else class="poster-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="活动状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="draft">草稿</el-radio>
            <el-radio label="published">发布</el-radio>
          </el-radio-group>
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
import { Plus } from '@element-plus/icons-vue'
import { createActivity } from '@/api/teacher'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const formData = ref({
  title: '',
  type: '',
  mode: '',
  location: '',
  startTime: null,
  endTime: null,
  maxParticipants: 100,
  posterUrl: '',
  status: 'draft'
})

const rules = {
  title: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择活动类型', trigger: 'change' }],
  mode: [{ required: true, message: '请选择活动形式', trigger: 'change' }],
  location: [{ required: true, message: '请输入活动地点', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  maxParticipants: [{ required: true, message: '请输入最大参与人数', trigger: 'blur' }]
}

const handlePosterSuccess = (response, file) => {
  formData.value.posterUrl = URL.createObjectURL(file.raw)
}

const beforePosterUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
  }
  return isImage && isLt2M
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await createActivity(formData.value)
        if (response.success) {
          ElMessage.success('创建活动成功')
          router.push('/teacher/activities')
        }
      } catch (error) {
        ElMessage.error('创建失败')
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
.teacher-activity-create {
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

    .activity-form {
      max-width: 800px;
    }

    .poster-uploader {
      :deep(.el-upload) {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: all 0.3s;

        &:hover {
          border-color: #409eff;
        }
      }

      .poster-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 178px;
        height: 178px;
        text-align: center;
        line-height: 178px;
      }

      .poster {
        width: 178px;
        height: 178px;
        display: block;
      }
    }
  }
}
</style>
