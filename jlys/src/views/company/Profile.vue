<template>
  <div class="profile">
    <div class="card">
      <div class="header">
        <h2>企业信息</h2>
        <p>完善您的企业信息，吸引更多优秀人才</p>
      </div>
      <div class="content">
        <CompanyProfileForm
          :is-edit="hasProfile"
          @success="handleSuccess"
        />
      </div>
    </div>
  </div>
  <router-link to="/simulatExam">首页</router-link>
  <router-link to="/officialExam">首页</router-link>
  <router-link to="/interview-report">首页</router-link>
</template>

<script>
import CompanyProfileForm from '@/components/forms/CompanyProfileForm.vue'
import { checkCompanyProfileExists } from '@/api/company'

export default {
  name: 'CompanyProfile',
  components: {
    CompanyProfileForm
  },
  data() {
    return {
      hasProfile: false
    }
  },
  async mounted() {
    await this.checkProfileExists()
  },
  methods: {
    async checkProfileExists() {
      try {
        const response = await checkCompanyProfileExists()
        if (response.data && response.data.success) {
          this.hasProfile = response.data.data
        }
      } catch (error) {
        console.error('检查企业信息失败:', error)
      }
    },

    handleSuccess(data) {
      this.hasProfile = true
      this.$message.success('企业信息保存成功')
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

.content {
  padding: 30px;
}
</style>
