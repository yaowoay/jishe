<template>
  <div class="profile-complete-container">
    <div class="profile-complete-card">
      <div class="header">
        <h2>完善企业信息</h2>
        <p>完善企业信息可获得更多优质人才推荐</p>
      </div>

      <el-form
        ref="profileForm"
        :model="profileForm"
        :rules="profileRules"
        label-width="120px"
        class="profile-form"
      >
        <el-divider content-position="left">基本信息</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="公司名称" prop="companyName">
              <el-input
                v-model="profileForm.companyName"
                placeholder="请输入公司名称"
                size="large"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属行业" prop="industry">
              <el-input
                v-model="profileForm.industry"
                placeholder="如：互联网、金融、教育"
                size="large"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="公司规模" prop="scale">
              <el-select
                v-model="profileForm.scale"
                placeholder="请选择公司规模"
                size="large"
                style="width: 100%"
              >
                <el-option label="1-50人" value="1-50人" />
                <el-option label="51-100人" value="51-100人" />
                <el-option label="101-500人" value="101-500人" />
                <el-option label="500人以上" value="500人以上" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="融资情况" prop="companyType">
              <el-select
                v-model="profileForm.companyType"
                placeholder="请选择融资情况"
                size="large"
                style="width: 100%"
              >
                <el-option label="未融资" value="未融资" />
                <el-option label="天使轮" value="天使轮" />
                <el-option label="A轮" value="A轮" />
                <el-option label="B轮" value="B轮" />
                <el-option label="C轮" value="C轮" />
                <el-option label="D轮及以上" value="D轮及以上" />
                <el-option label="已上市" value="已上市" />
                <el-option label="不需要融资" value="不需要融资" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="公司Logo" prop="logoUrl">
              <el-input
                v-model="profileForm.logoUrl"
                placeholder="请输入公司Logo图片URL"
                size="large"
              />
              <div v-if="profileForm.logoUrl" style="margin-top: 10px;">
                <img :src="profileForm.logoUrl" alt="Logo预览" style="max-width: 200px; max-height: 100px; border: 1px solid #ddd; border-radius: 4px; padding: 5px;" />
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">联系方式</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input
                v-model="profileForm.contactPhone"
                placeholder="请输入联系电话"
                size="large"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公司网站" prop="website">
              <el-input
                v-model="profileForm.website"
                placeholder="请输入公司网站"
                size="large"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="公司地址" prop="address">
          <el-input
            v-model="profileForm.address"
            placeholder="请输入详细地址"
            size="large"
          />
        </el-form-item>

        <el-divider content-position="left">公司详情</el-divider>

        <el-form-item label="公司简介" prop="description">
          <el-input
            v-model="profileForm.description"
            type="textarea"
            :rows="4"
            placeholder="请简要介绍公司的业务范围、发展历程、企业文化等"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="福利待遇" prop="companyWelfare">
          <el-input
            v-model="profileForm.companyWelfare"
            placeholder="如：五险一金、带薪年假、弹性工作、股票期权等，多个用逗号分隔"
            size="large"
          />
        </el-form-item>

        <el-form-item label="公司标签" prop="companyTags">
          <el-input
            v-model="profileForm.companyTags"
            placeholder="如：技术驱动、扁平化管理、年轻团队等，多个用逗号分隔"
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
import { completeCompanyProfile } from '@/api/company'
import { ElMessage } from 'element-plus'

export default {
  name: 'CompanyProfileComplete',
  data() {
    return {
      loading: false,
      profileForm: {
        companyName: '',
        industry: '',
        address: '',
        scale: '',
        website: '',
        contactPhone: '',
        description: '',
        companyType: '',
        companyWelfare: '',
        companyTags: '',
        logoUrl: ''
      },
      profileRules: {
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
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/, message: '请输入正确的联系电话', trigger: 'blur' }
        ],
        address: [
          { max: 255, message: '地址长度不能超过255个字符', trigger: 'blur' }
        ],
        website: [
          { max: 100, message: '网站长度不能超过100个字符', trigger: 'blur' }
        ],
        description: [
          { max: 1000, message: '公司简介长度不能超过1000个字符', trigger: 'blur' }
        ],
        companyType: [
          { max: 50, message: '企业类型长度不能超过50个字符', trigger: 'blur' }
        ],
        companyWelfare: [
          { max: 255, message: '福利待遇长度不能超过255个字符', trigger: 'blur' }
        ],
        companyTags: [
          { max: 255, message: '公司标签长度不能超过255个字符', trigger: 'blur' }
        ],
        logoUrl: [
          { max: 500, message: 'Logo URL长度不能超过500个字符', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    async handleSubmit() {
      try {
        await this.$refs.profileForm.validate()
        this.loading = true

        const response = await completeCompanyProfile(this.profileForm)

        if (response.success) {
          ElMessage.success('企业信息完善成功！')
          
          // 更新store中的用户信息
          this.$store.dispatch('updateProfileStatus', true)
          
          // 跳转到企业dashboard
          this.$router.push('/company/dashboard')
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
      this.$router.push('/company/dashboard')
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