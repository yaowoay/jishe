<template>
  <div class="profile">
    <div class="card">
      <div class="header">
        <h2>企业信息</h2>
        <p>查看和管理您的企业信息</p>
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
            <el-descriptions-item label="公司名称">
              {{ profileData.companyName || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="所属行业">
              {{ profileData.industry || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="公司规模">
              {{ profileData.scale || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              {{ profileData.contactPhone || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="公司地址">
              {{ profileData.address || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="公司网站">
              {{ profileData.website || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="认证状态">
              <el-tag :type="getVerifyStatusType(profileData.verifyStatus)">
                {{ getVerifyStatusText(profileData.verifyStatus) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="信用评分">
              {{ profileData.creditScore || '未评分' }}
            </el-descriptions-item>
            <el-descriptions-item label="档案完整度" span="2">
              <el-progress 
                :percentage="profileData.profileCompletion || 0" 
                :color="getProgressColor(profileData.profileCompletion)"
              />
            </el-descriptions-item>
            <el-descriptions-item label="公司简介" span="2">
              {{ profileData.description || '未填写' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div v-else class="empty-state">
          <el-empty description="暂无企业信息">
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
      title="编辑企业信息"
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
            <el-form-item label="公司名称" prop="companyName">
              <el-input
                v-model="editFormData.companyName"
                placeholder="请输入公司名称"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="所属行业" prop="industry">
              <el-input
                v-model="editFormData.industry"
                placeholder="请输入所属行业"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="公司规模" prop="scale">
              <el-select v-model="editFormData.scale" placeholder="请选择公司规模" style="width: 100%">
                <el-option label="1-50人" value="1-50人" />
                <el-option label="51-100人" value="51-100人" />
                <el-option label="101-500人" value="101-500人" />
                <el-option label="500人以上" value="500人以上" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input
                v-model="editFormData.contactPhone"
                placeholder="请输入联系电话"
                maxlength="20"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="公司地址" prop="address">
              <el-input
                v-model="editFormData.address"
                placeholder="请输入公司地址"
                maxlength="255"
                show-word-limit
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="公司网站" prop="website">
              <el-input
                v-model="editFormData.website"
                placeholder="请输入公司网站"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="公司简介" prop="description">
          <el-input
            v-model="editFormData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入公司简介"
            maxlength="500"
            show-word-limit
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
import { getCompanyProfile, updateCompanyProfile } from '@/api/company'
import { Edit } from '@element-plus/icons-vue'

export default {
  name: 'CompanyProfile',
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
        companyName: '',
        industry: '',
        address: '',
        scale: '',
        website: '',
        contactPhone: '',
        description: ''
      },
      formRules: {
        companyName: [
          { required: true, message: '请输入公司名称', trigger: 'blur' },
          { max: 100, message: '公司名称长度不能超过100个字符', trigger: 'blur' }
        ],
        industry: [
          { required: true, message: '请输入所属行业', trigger: 'blur' },
          { max: 50, message: '行业名称长度不能超过50个字符', trigger: 'blur' }
        ],
        scale: [
          { required: true, message: '请选择公司规模', trigger: 'change' }
        ],
        contactPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' }
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
        const response = await getCompanyProfile()

        if (response && response.success) {
          this.profileData = response.data.profile
        } else {
          this.profileData = null
        }
      } catch (error) {
        console.error('加载企业档案失败:', error)
        this.$message.error('加载企业档案失败')
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
          companyName: this.profileData.companyName || '',
          industry: this.profileData.industry || '',
          address: this.profileData.address || '',
          scale: this.profileData.scale || '',
          website: this.profileData.website || '',
          contactPhone: this.profileData.contactPhone || '',
          description: this.profileData.description || ''
        }
      } else {
        this.resetForm()
      }
    },

    resetForm() {
      this.editFormData = {
        companyName: '',
        industry: '',
        address: '',
        scale: '',
        website: '',
        contactPhone: '',
        description: ''
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

        const response = await updateCompanyProfile(this.editFormData)

        if (response && response.success) {
          this.$message.success('保存成功')
          this.editDialogVisible = false
          
          // 更新store中的档案完成状态
          this.$store.dispatch('updateProfileStatus', true)
          
          // 重新加载企业档案
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

    getVerifyStatusType(status) {
      const statusMap = {
        'pending': 'warning',
        'approved': 'success',
        'rejected': 'danger'
      }
      return statusMap[status] || 'info'
    },

    getVerifyStatusText(status) {
      const statusMap = {
        'pending': '待审核',
        'approved': '已认证',
        'rejected': '审核失败'
      }
      return statusMap[status] || '未知状态'
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
