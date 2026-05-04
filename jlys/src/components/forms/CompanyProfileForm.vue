<template>
  <div class="company-profile-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      label-position="left"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="公司名称" prop="companyName">
            <el-input
              v-model="formData.companyName"
              placeholder="请输入公司名称"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="所属行业" prop="industry">
            <el-input
              v-model="formData.industry"
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
            <el-select v-model="formData.scale" placeholder="请选择公司规模" style="width: 100%">
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
              v-model="formData.contactPhone"
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
              v-model="formData.address"
              placeholder="请输入公司地址"
              maxlength="255"
              show-word-limit
            />
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="公司网站" prop="website">
            <el-input
              v-model="formData.website"
              placeholder="请输入公司网站"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="公司描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="4"
          placeholder="请输入公司描述"
          maxlength="1000"
          show-word-limit
        />
      </el-form-item>

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
import { getCompanyProfile, saveCompanyProfile } from '@/api/company'

export default {
  name: 'CompanyProfileForm',
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
        companyName: '',
        industry: '',
        address: '',
        scale: '',
        website: '',
        contactPhone: '',
        description: ''
      },
      rules: {
        companyName: [
          { required: true, message: '请输入公司名称', trigger: 'blur' },
          { max: 100, message: '公司名称长度不能超过100个字符', trigger: 'blur' }
        ],
        industry: [
          { required: true, message: '请输入所属行业', trigger: 'blur' },
          { max: 50, message: '行业长度不能超过50个字符', trigger: 'blur' }
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
          { max: 100, message: '网站长度不能超过100个字符', trigger: 'blur' },
          { pattern: /^(https?:\/\/)?([\da-z.-]+)\.([a-z.]{2,6})([/\w .-]*)*\/?$/, message: '请输入正确的网站格式', trigger: 'blur' }
        ],
        description: [
          { max: 1000, message: '公司描述长度不能超过1000个字符', trigger: 'blur' }
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
        const response = await getCompanyProfile()
        if (response.data && response.data.data) {
          this.formData = { ...this.formData, ...response.data.data }
        }
      } catch (error) {
        console.error('加载公司信息失败:', error)
        this.$message.error('加载公司信息失败')
      }
    },
    
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
        this.loading = true
        
        const response = await saveCompanyProfile(this.formData)
        if (response.data && response.data.success) {
          this.$message.success('保存成功')
          this.$emit('success', response.data.data)
        } else {
          this.$message.error(response.data?.message || '保存失败')
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
.company-profile-form {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}
</style>
