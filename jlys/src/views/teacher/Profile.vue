<template>
  <div class="profile">
    <div class="card">
      <div class="header">
        <h2>教师信息</h2>
        <p>查看和管理您的教师信息</p>
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
            <el-descriptions-item label="工号">
              {{ profileData.teacherNo || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="真实姓名">
              {{ profileData.realName || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="教师角色">
              {{ getRoleTypeText(profileData.roleType) }}
            </el-descriptions-item>
            <el-descriptions-item label="所属学院">
              {{ getCollegeText(profileData.collegeId) }}
            </el-descriptions-item>
            <el-descriptions-item label="手机号">
              {{ profileData.phone || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ profileData.email || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="管理学院">
              {{ profileData.managedColleges || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="管理专业">
              {{ profileData.managedMajors || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="档案完整度" span="2">
              <el-progress 
                :percentage="computedCompletion"
                :color="getProgressColor(computedCompletion)"
              />
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div v-else class="empty-state">
          <el-empty description="暂无教师信息">
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
      title="编辑教师信息"
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
            <el-form-item label="工号" prop="teacherNo">
              <el-input
                v-model="editFormData.teacherNo"
                placeholder="请输入工号"
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
            <el-form-item label="教师角色" prop="roleType">
              <el-select v-model="editFormData.roleType" placeholder="请选择教师角色" style="width: 100%">
                <el-option label="辅导员" value="counselor" />
                <el-option label="学业导师" value="advisor" />
                <el-option label="管理员" value="admin" />
                <el-option label="领导" value="leader" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="所属学院" prop="collegeId">
              <el-select v-model="editFormData.collegeId" placeholder="请选择所属学院" style="width: 100%">
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
                v-model="editFormData.phone"
                placeholder="请输入手机号"
                maxlength="20"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="editFormData.email"
                placeholder="请输入邮箱"
                maxlength="100"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="管理学院" prop="managedColleges">
          <el-input
            v-model="editFormData.managedColleges"
            placeholder="请输入管理的学院（可选）"
            maxlength="255"
          />
        </el-form-item>

        <el-form-item label="管理专业" prop="managedMajors">
          <el-input
            v-model="editFormData.managedMajors"
            placeholder="请输入管理的专业（可选）"
            maxlength="255"
          />
        </el-form-item>
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
import { getTeacherProfile, updateTeacherProfile } from '@/api/teacher'
import { Edit } from '@element-plus/icons-vue'

export default {
  name: 'TeacherProfile',
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
        teacherNo: '',
        realName: '',
        collegeId: null,
        roleType: '',
        phone: '',
        email: '',
        managedColleges: '',
        managedMajors: ''
      },
      formRules: {
        teacherNo: [
          { required: true, message: '请输入工号', trigger: 'blur' },
          { max: 50, message: '工号长度不能超过50个字符', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' },
          { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
        ],
        roleType: [
          { required: true, message: '请选择教师角色', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    // 前端实时计算档案完整度
    computedCompletion() {
      if (!this.profileData) return 0

      let score = 0
      // 每项15分：工号、真实姓名、教师角色、所属学院（4项，共60分）
      if (this.profileData.teacherNo && this.profileData.teacherNo.trim()) score += 15
      if (this.profileData.realName && this.profileData.realName.trim()) score += 15
      if (this.profileData.roleType && this.profileData.roleType.trim()) score += 15
      if (this.profileData.collegeId) score += 15

      // 每项10分：手机号、邮箱、管理学院、管理专业（4项，共40分）
      if (this.profileData.phone && this.profileData.phone.trim()) score += 10
      if (this.profileData.email && this.profileData.email.trim()) score += 10
      if (this.profileData.managedColleges && this.profileData.managedColleges.trim()) score += 10
      if (this.profileData.managedMajors && this.profileData.managedMajors.trim()) score += 10

      return Math.min(100, score)
    }
  },
  async mounted() {
    await this.loadProfile()
  },
  methods: {
    async loadProfile() {
      try {
        this.loading = true
        const response = await getTeacherProfile()

        if (response && response.success) {
          this.profileData = response.data
        } else {
          this.profileData = null
        }
      } catch (error) {
        console.error('加载教师档案失败:', error)
        this.$message.error('加载教师档案失败')
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
          teacherNo: this.profileData.teacherNo || '',
          realName: this.profileData.realName || '',
          collegeId: this.profileData.collegeId || null,
          roleType: this.profileData.roleType || '',
          phone: this.profileData.phone || '',
          email: this.profileData.email || '',
          managedColleges: this.profileData.managedColleges || '',
          managedMajors: this.profileData.managedMajors || ''
        }
      } else {
        this.resetForm()
      }
    },

    resetForm() {
      this.editFormData = {
        teacherNo: '',
        realName: '',
        collegeId: null,
        roleType: '',
        phone: '',
        email: '',
        managedColleges: '',
        managedMajors: ''
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

        const response = await updateTeacherProfile(this.editFormData)

        if (response && response.success) {
          this.$message.success('保存成功')
          this.editDialogVisible = false
          
          // 更新store中的档案完成状态
          this.$store.dispatch('updateProfileStatus', true)
          
          // 重新加载教师档案
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

    getRoleTypeText(roleType) {
      const roleMap = {
        'counselor': '辅导员',
        'advisor': '学业导师',
        'admin': '管理员',
        'leader': '领导'
      }
      return roleMap[roleType] || '未填写'
    },

    getCollegeText(collegeId) {
      const collegeMap = {
        1: '计算机与信息工程学院',
        2: '电子工程学院',
        3: '机械工程学院',
        4: '经济管理学院'
      }
      return collegeMap[collegeId] || '未填写'
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
.el-dialog .el-select {
  width: 100%;
}
</style>