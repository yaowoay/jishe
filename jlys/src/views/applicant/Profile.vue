<template>
  <div class="profile">
    <div class="card">
      <div class="header">
        <h2>个人信息</h2>
        <p>查看和管理您的个人信息</p>
        <div class="header-actions">
          <el-button
            type="primary"
            @click="openEditDialog"
            :icon="Edit"
          >
            编辑信息
          </el-button>
        </div>
      </div>

      <div class="content">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="8" animated />
        </div>

        <div v-else-if="profileData" class="profile-display">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="学号">
              {{ profileData.studentNo || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="真实姓名">
              {{ profileData.realName || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="性别">
              {{ getGenderText(profileData.gender) }}
            </el-descriptions-item>
            <el-descriptions-item label="出生日期">
              {{ formatDate(profileData.birthDate) }}
            </el-descriptions-item>
            <el-descriptions-item label="学院">
              {{ profileData.college || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="专业">
              {{ profileData.major || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="班级">
              {{ profileData.className || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="年级">
              {{ profileData.grade || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="学历">
              {{ profileData.educationLevel || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="毕业年份">
              {{ profileData.graduationYear || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="档案完整度" span="2">
              <el-progress 
                :percentage="profileData.profileCompletion || 0" 
                :color="getProgressColor(profileData.profileCompletion)"
              />
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div v-else class="empty-state">
          <el-empty description="暂无个人信息">
            <el-button type="primary" @click="openEditDialog">
              立即完善信息
            </el-button>
          </el-empty>
        </div>
      </div>
    </div>

    <!-- 编辑信息弹窗 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑学籍信息"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editFormRef"
        :model="editFormData"
        :rules="formRules"
        label-width="120px"
        label-position="left"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input
                v-model="editFormData.studentNo"
                placeholder="请输入学号"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input
                v-model="editFormData.realName"
                placeholder="请输入真实姓名"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="editFormData.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="male" />
                <el-option label="女" value="female" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="editFormData.birthDate"
                type="date"
                placeholder="请选择出生日期"
                style="width: 100%"
                :disabled-date="disabledDate"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学院" prop="college">
              <el-input
                v-model="editFormData.college"
                placeholder="请输入学院名称"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input
                v-model="editFormData.major"
                placeholder="请输入专业名称"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班级" prop="className">
              <el-input
                v-model="editFormData.className"
                placeholder="请输入班级"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="年级" prop="grade">
              <el-input
                v-model="editFormData.grade"
                placeholder="请输入年级"
                maxlength="20"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学历" prop="educationLevel">
              <el-select v-model="editFormData.educationLevel" placeholder="请选择学历" style="width: 100%">
                <el-option label="专科" value="专科" />
                <el-option label="本科" value="本科" />
                <el-option label="硕士" value="硕士" />
                <el-option label="博士" value="博士" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="毕业年份" prop="graduationYear">
              <el-date-picker
                v-model="editFormData.graduationYear"
                type="year"
                placeholder="请选择毕业年份"
                style="width: 100%"
                format="YYYY"
                value-format="YYYY"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button type="primary" @click="handleSave" :loading="saving">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getStudentProfile, updateStudentProfile } from '@/api/student'
import { Edit } from '@element-plus/icons-vue'

export default {
  name: 'ApplicantProfile',
  components: {
    Edit
  },
  data() {
    return {
      loading: false,
      profileData: null,
      editDialogVisible: false,
      saving: false,
      editFormData: {
        studentNo: '',
        realName: '',
        gender: '',
        birthDate: '',
        college: '',
        major: '',
        className: '',
        grade: '',
        educationLevel: '',
        graduationYear: ''
      },
      formRules: {
        studentNo: [
          { required: true, message: '请输入学号', trigger: 'blur' },
          { max: 50, message: '学号长度不能超过50个字符', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' },
          { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        college: [
          { required: true, message: '请输入学院名称', trigger: 'blur' },
          { max: 100, message: '学院名称长度不能超过100个字符', trigger: 'blur' }
        ],
        major: [
          { required: true, message: '请输入专业名称', trigger: 'blur' },
          { max: 100, message: '专业名称长度不能超过100个字符', trigger: 'blur' }
        ],
        className: [
          { required: true, message: '请输入班级', trigger: 'blur' },
          { max: 50, message: '班级长度不能超过50个字符', trigger: 'blur' }
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
  async mounted() {
    await this.loadProfile()
  },
  methods: {
    async loadProfile() {
      try {
        this.loading = true
        const response = await getStudentProfile()

        if (response && response.success) {
          this.profileData = response.data.profile
        } else {
          this.profileData = null
        }
      } catch (error) {
        console.error('加载学生档案失败:', error)
        this.$message.error('加载学生档案失败')
        this.profileData = null
      } finally {
        this.loading = false
      }
    },

    openEditDialog() {
      this.fillEditForm()
      this.editDialogVisible = true
    },

    fillEditForm() {
      if (this.profileData) {
        this.editFormData = {
          studentNo: this.profileData.studentNo || '',
          realName: this.profileData.realName || '',
          gender: this.profileData.gender || '',
          birthDate: this.profileData.birthDate || '',
          college: this.profileData.college || '',
          major: this.profileData.major || '',
          className: this.profileData.className || '',
          grade: this.profileData.grade || '',
          educationLevel: this.profileData.educationLevel || '',
          graduationYear: this.profileData.graduationYear || ''
        }
      } else {
        this.resetForm()
      }
    },

    resetForm() {
      this.editFormData = {
        studentNo: '',
        realName: '',
        gender: '',
        birthDate: '',
        college: '',
        major: '',
        className: '',
        grade: '',
        educationLevel: '',
        graduationYear: ''
      }
      this.$nextTick(() => {
        if (this.$refs.editFormRef) {
          this.$refs.editFormRef.clearValidate()
        }
      })
    },

    async handleSave() {
      try {
        await this.$refs.editFormRef.validate()
        this.saving = true

        const response = await updateStudentProfile(this.editFormData)

        if (response && response.success) {
          this.$message.success('保存成功')
          this.editDialogVisible = false
          
          // 更新store中的档案完成状态
          this.$store.dispatch('updateProfileStatus', true)
          
          // 重新加载学生档案
          await this.loadProfile()
        } else {
          this.$message.error(response?.message || '保存失败')
        }
      } catch (error) {
        console.error('保存失败:', error)
        this.$message.error('保存失败')
      } finally {
        this.saving = false
      }
    },

    disabledDate(time) {
      return time.getTime() > Date.now()
    },

    getGenderText(gender) {
      const genderMap = {
        'male': '男',
        'female': '女',
        'other': '其他'
      }
      return genderMap[gender] || '未填写'
    },

    formatDate(date) {
      if (!date) return '未填写'
      return new Date(date).toLocaleDateString('zh-CN')
    },

    getProgressColor(percentage) {
      if (percentage >= 80) return '#67c23a'
      if (percentage >= 60) return '#e6a23c'
      return '#f56c6c'
    }
  }
}
</script>

<style scoped>
.profile {
  padding: 20px;
}

.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.header {
  padding: 20px 30px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-content {
  flex: 1;
}

.header h2 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.header-actions {
  margin-left: 20px;
}

.content {
  padding: 30px;
}

.loading-container {
  padding: 20px;
}

.profile-display {
  max-width: 800px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.el-descriptions {
  margin-top: 0;
}

.el-descriptions :deep(.el-descriptions__label) {
  font-weight: 600;
  color: #333;
  width: 120px;
}

.el-descriptions :deep(.el-descriptions__content) {
  color: #666;
}

/* 弹窗样式 */
.dialog-footer {
  text-align: right;
}

.dialog-footer .el-button {
  margin-left: 10px;
}

.el-dialog .el-form {
  padding: 0 20px;
}

.el-dialog .el-form-item {
  margin-bottom: 20px;
}

.el-dialog .el-row {
  margin-bottom: 0;
}

.el-dialog .el-col {
  padding-bottom: 0;
}

/* 弹窗内表单样式优化 */
.el-dialog .el-input-number {
  width: 100%;
}

.el-dialog .el-select {
  width: 100%;
}

.el-dialog .el-date-picker {
  width: 100%;
}
</style>
