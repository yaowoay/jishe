<template>
  <div class="profile-complete-container">
    <div class="profile-complete-card">
      <div class="header">
        <h2>完善教师信息</h2>
        <p>完善教师信息可获得更好的管理体验</p>
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
            <el-form-item label="工号" prop="teacherNo">
              <el-input
                v-model="profileForm.teacherNo"
                placeholder="请输入工号"
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
            <el-form-item label="教师角色" prop="roleType">
              <el-select
                v-model="profileForm.roleType"
                placeholder="请选择教师角色"
                size="large"
                style="width: 100%"
              >
                <el-option label="辅导员" value="counselor" />
                <el-option label="学业导师" value="advisor" />
                <el-option label="管理员" value="admin" />
                <el-option label="领导" value="leader" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属学院" prop="collegeId">
              <el-select
                v-model="profileForm.collegeId"
                placeholder="请选择所属学院"
                size="large"
                style="width: 100%"
              >
                <el-option label="计算机与信息工程学院" :value="1" />
                <el-option label="电子工程学院" :value="2" />
                <el-option label="机械工程学院" :value="3" />
                <el-option label="经济管理学院" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="profileForm.phone"
                placeholder="请输入手机号"
                size="large"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="profileForm.email"
                placeholder="请输入邮箱"
                size="large"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="管理学院" prop="managedColleges">
          <el-input
            v-model="profileForm.managedColleges"
            placeholder="请输入管理的学院（可选）"
            size="large"
          />
        </el-form-item>

        <el-form-item label="管理专业" prop="managedMajors">
          <el-input
            v-model="profileForm.managedMajors"
            placeholder="请输入管理的专业（可选）"
            size="large"
          />
        </el-form-item>

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
import { completeTeacherProfile } from '@/api/teacher'
import { ElMessage } from 'element-plus'

export default {
  name: 'TeacherProfileComplete',
  data() {
    return {
      loading: false,
      profileForm: {
        teacherNo: '',
        realName: '',
        collegeId: null,
        roleType: '',
        phone: '',
        email: '',
        managedColleges: '',
        managedMajors: ''
      },
      profileRules: {
        teacherNo: [
          { required: true, message: '请输入工号', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        roleType: [
          { required: true, message: '请选择教师角色', trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    async handleSubmit() {
      try {
        await this.$refs.profileForm.validate()
        this.loading = true

        const response = await completeTeacherProfile(this.profileForm)

        if (response.success) {
          ElMessage.success('教师信息完善成功！')
          
          // 更新store中的用户信息
          this.$store.dispatch('updateProfileStatus', true)
          
          // 跳转到教师dashboard
          this.$router.push('/teacher/dashboard')
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
      this.$router.push('/teacher/dashboard')
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