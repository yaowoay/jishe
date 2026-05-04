<template>
  <div class="profile-complete-container">
    <div class="profile-complete-card">
      <div class="header">
        <h2>完善学籍信息</h2>
        <p>完善学籍信息可获得更精准的岗位推荐</p>
      </div>

      <el-form
        ref="profileForm"
        :model="profileForm"
        :rules="profileRules"
        label-width="100px"
        class="profile-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input
                v-model="profileForm.studentNo"
                placeholder="请输入学号"
                size="large"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input
                v-model="profileForm.realName"
                placeholder="请输入真实姓名"
                size="large"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select
                v-model="profileForm.gender"
                placeholder="请选择性别"
                size="large"
                style="width: 100%"
              >
                <el-option label="男" value="male" />
                <el-option label="女" value="female" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学历" prop="educationLevel">
              <el-select
                v-model="profileForm.educationLevel"
                placeholder="请选择学历"
                size="large"
                style="width: 100%"
              >
                <el-option label="专科" value="专科" />
                <el-option label="本科" value="本科" />
                <el-option label="硕士" value="硕士" />
                <el-option label="博士" value="博士" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学院" prop="college">
              <el-input
                v-model="profileForm.college"
                placeholder="请输入学院名称"
                size="large"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input
                v-model="profileForm.major"
                placeholder="请输入专业名称"
                size="large"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班级" prop="className">
              <el-input
                v-model="profileForm.className"
                placeholder="请输入班级"
                size="large"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="毕业年份" prop="graduationYear">
              <el-date-picker
                v-model="profileForm.graduationYear"
                type="year"
                placeholder="请选择毕业年份"
                size="large"
                style="width: 100%"
                format="YYYY"
                value-format="YYYY"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="form-actions">
          <el-button
            size="large"
            @click="handleCancel"
          >
            稍后再说
          </el-button>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleSubmit"
          >
            提交信息
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { completeStudentProfile } from '@/api/student'
import { ElMessage } from 'element-plus'

export default {
  name: 'ProfileComplete',
  data() {
    return {
      loading: false,
      profileForm: {
        studentNo: '',
        realName: '',
        gender: '',
        college: '',
        major: '',
        className: '',
        educationLevel: '',
        graduationYear: ''
      },
      profileRules: {
        studentNo: [
          { required: true, message: '请输入学号', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        college: [
          { required: true, message: '请输入学院名称', trigger: 'blur' }
        ],
        major: [
          { required: true, message: '请输入专业名称', trigger: 'blur' }
        ],
        className: [
          { required: true, message: '请输入班级', trigger: 'blur' }
        ],
        educationLevel: [
          { required: true, message: '请选择学历', trigger: 'change' }
        ],
        graduationYear: [
          { required: true, message: '请选择毕业年份', trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    async handleSubmit() {
      try {
        await this.$refs.profileForm.validate()
        this.loading = true

        const response = await completeStudentProfile(this.profileForm)

        if (response.success) {
          ElMessage.success('学籍信息完善成功！')
          
          // 更新store中的用户信息
          this.$store.dispatch('updateProfileStatus', true)
          
          // 跳转到学生dashboard
          this.$router.push('/applicant/dashboard')
        } else {
          ElMessage.error(response.message || '提交失败')
        }
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('提交失败，请检查网络连接')
      } finally {
        this.loading = false
      }
    },
    
    handleCancel() {
      // 稍后再说，直接跳转到dashboard
      this.$router.push('/applicant/dashboard')
    }
  }
}
</script>

<style scoped>
.profile-complete-container {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.profile-complete-card {
  width: 800px;
  max-width: 90vw;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.header {
  text-align: center;
  margin-bottom: 40px;
}

.header h2 {
  font-size: 2rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 12px;
}

.header p {
  color: #7f8c8d;
  font-size: 1.1rem;
}

.profile-form {
  margin-top: 20px;
}

.profile-form .el-form-item {
  margin-bottom: 24px;
}

.profile-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s ease;
}

.profile-form :deep(.el-input__wrapper:hover) {
  border-color: #409eff;
}

.profile-form :deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.form-actions .el-button {
  padding: 12px 32px;
  font-size: 1rem;
  border-radius: 8px;
  min-width: 120px;
}

@media (max-width: 768px) {
  .profile-complete-card {
    padding: 24px;
  }
  
  .header h2 {
    font-size: 1.5rem;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .form-actions .el-button {
    width: 100%;
  }
}
</style>