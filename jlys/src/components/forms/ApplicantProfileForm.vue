<template>
  <div class="applicant-profile-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      label-position="left"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="姓名" prop="fullName">
            <el-input
              v-model="formData.fullName"
              placeholder="请输入您的姓名"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="手机号" prop="phone">
            <el-input
              v-model="formData.phone"
              placeholder="请输入手机号"
              maxlength="11"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-select v-model="formData.gender" placeholder="请选择性别" style="width: 100%">
              <el-option label="男" value="male" />
              <el-option label="女" value="female" />
              <el-option label="其他" value="other" />
            </el-select>
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="出生日期" prop="birthdate">
            <el-date-picker
              v-model="formData.birthdate"
              type="date"
              placeholder="请选择出生日期"
              style="width: 100%"
              :picker-options="{
                disabledDate(time) {
                  return time.getTime() > Date.now()
                }
              }"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="学历" prop="educationLevel">
            <el-select v-model="formData.educationLevel" placeholder="请选择学历" style="width: 100%">
              <el-option label="高中" value="高中" />
              <el-option label="大专" value="大专" />
              <el-option label="本科" value="本科" />
              <el-option label="硕士" value="硕士" />
              <el-option label="博士" value="博士" />
            </el-select>
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="工作年限" prop="workYears">
            <el-input-number
              v-model="formData.workYears"
              :min="0"
              :max="50"
              placeholder="请输入工作年限"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="期望职位" prop="expectedPosition">
            <el-input
              v-model="formData.expectedPosition"
              placeholder="请输入期望职位"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="期望薪资" prop="expectedSalary">
            <el-input-number
              v-model="formData.expectedSalary"
              :min="0"
              :max="999999"
              placeholder="请输入期望薪资"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          {{ isEdit ? '更新信息' : '保存信息' }}
        </el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getApplicantProfile, saveApplicantProfile } from '@/api/applicant'

export default {
  name: 'ApplicantProfileForm',
  props: {
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      formData: {
        fullName: '',
        phone: '',
        gender: '',
        birthdate: '',
        educationLevel: '',
        workYears: 0,
        expectedPosition: '',
        expectedSalary: null
      },
      rules: {
        fullName: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        gender: [
          { required: false, message: '请选择性别', trigger: 'change' }
        ],
        educationLevel: [
          { required: false, message: '请选择学历', trigger: 'change' }
        ],
        workYears: [
          { type: 'number', min: 0, max: 50, message: '工作年限必须在0-50年之间', trigger: 'blur' }
        ],
        expectedPosition: [
          { max: 50, message: '期望职位长度不能超过50个字符', trigger: 'blur' }
        ],
        expectedSalary: [
          { type: 'number', min: 0, max: 999999, message: '期望薪资必须在0-999999之间', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    if (this.isEdit) {
      this.loadProfile()
    }
  },
  methods: {
    async loadProfile() {
      try {
        const response = await getApplicantProfile()
        // 由于响应拦截器直接返回response.data，所以response就是后端的ApiResponse
        if (response && response.success && response.data) {
          this.formData = { ...this.formData, ...response.data }
          // 处理日期格式
          if (response.data.birthdate) {
            this.formData.birthdate = new Date(response.data.birthdate)
          }
        }
      } catch (error) {
        console.error('加载个人信息失败:', error)
        this.$message.error('加载个人信息失败')
      }
    },
    
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
        this.loading = true

        // 处理日期格式
        const submitData = { ...this.formData }
        if (submitData.birthdate) {
          submitData.birthdate = submitData.birthdate.toISOString().split('T')[0]
        }

        const response = await saveApplicantProfile(submitData)
        // 由于响应拦截器直接返回response.data，所以response就是后端的ApiResponse
        if (response && response.success) {
          this.$message.success('保存成功')
          this.$emit('success', response.data)
        } else {
          this.$message.error(response?.message || '保存失败')
        }
      } catch (error) {
        console.error('保存失败:', error)
        this.$message.error('保存失败')
      } finally {
        this.loading = false
      }
    },
    
    handleReset() {
      this.$refs.formRef.resetFields()
    }
  }
}
</script>

<style scoped>
.applicant-profile-form {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>
