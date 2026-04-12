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
            <el-descriptions-item label="姓名">
              {{ profileData.fullName || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="手机号">
              {{ profileData.phone || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="性别">
              {{ getGenderText(profileData.gender) }}
            </el-descriptions-item>
            <el-descriptions-item label="出生日期">
              {{ formatDate(profileData.birthdate) }}
            </el-descriptions-item>
            <el-descriptions-item label="学历">
              {{ profileData.educationLevel || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="工作年限">
              {{ profileData.workYears ? `${profileData.workYears}年` : '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="期望职位">
              {{ profileData.expectedPosition || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="期望薪资">
              {{ profileData.expectedSalary ? `${profileData.expectedSalary}元` : '未填写' }}
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
      title="编辑个人信息"
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
            <el-form-item label="姓名" prop="fullName">
              <el-input
                v-model="editFormData.fullName"
                placeholder="请输入您的姓名"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="editFormData.phone"
                placeholder="请输入手机号"
                maxlength="11"
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
            <el-form-item label="出生日期" prop="birthdate">
              <el-date-picker
                v-model="editFormData.birthdate"
                type="date"
                placeholder="请选择出生日期"
                style="width: 100%"
                :disabled-date="disabledDate"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学历" prop="educationLevel">
              <el-select v-model="editFormData.educationLevel" placeholder="请选择学历" style="width: 100%">
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
                v-model="editFormData.workYears"
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
                v-model="editFormData.expectedPosition"
                placeholder="请输入期望职位"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="期望薪资" prop="expectedSalary">
              <el-input-number
                v-model="editFormData.expectedSalary"
                :min="0"
                :max="999999"
                placeholder="请输入期望薪资"
                style="width: 100%"
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
import { getApplicantProfile, saveApplicantProfile } from '@/api/applicant'
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
        fullName: '',
        phone: '',
        gender: '',
        birthdate: '',
        educationLevel: '',
        workYears: 0,
        expectedPosition: '',
        expectedSalary: null
      },
      formRules: {
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
  async mounted() {
    await this.loadProfile()
  },
  methods: {
    async loadProfile() {
      try {
        this.loading = true
        const response = await getApplicantProfile()

        // 由于响应拦截器直接返回response.data，所以response就是后端的ApiResponse
        if (response && response.success) {
          // 即使success为true，data也可能为null（表示暂无个人信息）
          this.profileData = response.data
        } else {
          this.profileData = null
        }
      } catch (error) {
        console.error('加载个人信息失败:', error)
        this.$message.error('加载个人信息失败')
        this.profileData = null
      } finally {
        this.loading = false
      }
    },

    openEditDialog() {
      // 打开编辑弹窗，并填充现有数据
      this.fillEditForm()
      this.editDialogVisible = true
    },

    fillEditForm() {
      // 将现有数据填充到编辑表单中
      if (this.profileData) {
        this.editFormData = {
          fullName: this.profileData.fullName || '',
          phone: this.profileData.phone || '',
          gender: this.profileData.gender || '',
          birthdate: this.profileData.birthdate ? new Date(this.profileData.birthdate) : '',
          educationLevel: this.profileData.educationLevel || '',
          workYears: this.profileData.workYears || 0,
          expectedPosition: this.profileData.expectedPosition || '',
          expectedSalary: this.profileData.expectedSalary || null
        }
      } else {
        // 如果没有现有数据，重置表单
        this.resetForm()
      }
    },

    resetForm() {
      // 重置表单到初始状态
      this.editFormData = {
        fullName: '',
        phone: '',
        gender: '',
        birthdate: '',
        educationLevel: '',
        workYears: 0,
        expectedPosition: '',
        expectedSalary: null
      }
      // 清除表单验证
      this.$nextTick(() => {
        if (this.$refs.editFormRef) {
          this.$refs.editFormRef.clearValidate()
        }
      })
    },

    async handleSave() {
      try {
        // 表单验证
        await this.$refs.editFormRef.validate()

        this.saving = true

        // 处理日期格式
        const submitData = { ...this.editFormData }
        if (submitData.birthdate) {
          submitData.birthdate = submitData.birthdate.toISOString().split('T')[0]
        }

        // 调用保存API
        const response = await saveApplicantProfile(submitData)

        if (response && response.success) {
          this.$message.success('保存成功')
          this.editDialogVisible = false
          // 重新加载个人信息
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
      // 禁用未来日期
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
