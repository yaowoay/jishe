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

    <!-- 求职信息卡片 -->
    <div class="card job-info-card">
      <div class="header">
        <h2>求职信息</h2>
        <p>完善求职信息可获得更精准的岗位推荐</p>
        <div class="header-actions">
          <el-button
            type="primary"
            @click="openJobInfoDialog"
            :icon="Edit"
          >
            {{ jobInfoData ? '编辑求职信息' : '填写求职信息' }}
          </el-button>
        </div>
      </div>

      <div class="content">
        <div v-if="loadingJobInfo" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>

        <div v-else-if="jobInfoData && hasJobInfo" class="job-info-display">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="期望职位">
              {{ jobInfoData.position || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="工作年限">
              {{ jobInfoData.workYears ? jobInfoData.workYears + '年' : '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="期望城市">
              {{ jobInfoData.expectedCity || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="期望薪资">
              {{ formatSalary(jobInfoData.expectedSalaryMin, jobInfoData.expectedSalaryMax) }}
            </el-descriptions-item>
            <el-descriptions-item label="期望行业">
              {{ jobInfoData.expectedIndustry || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="掌握技能" span="2">
              <el-tag
                v-for="skill in getSkillList(jobInfoData.skill)"
                :key="skill"
                type="success"
                style="margin-right: 8px; margin-bottom: 4px;"
              >
                {{ skill }}
              </el-tag>
              <span v-if="!jobInfoData.skill || jobInfoData.skill.length === 0">未填写</span>
            </el-descriptions-item>
            <el-descriptions-item label="个人简介" span="2">
              {{ jobInfoData.profile || '未填写' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div v-else class="empty-state">
          <el-alert
            title="提示"
            type="warning"
            description="完善求职信息可以帮助系统为您推荐更合适的岗位，提高求职成功率！"
            :closable="false"
            show-icon
            style="margin-bottom: 20px;"
          />
          <el-empty description="暂无求职信息">
            <el-button type="primary" @click="openJobInfoDialog">
              立即填写求职信息
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

    <!-- 编辑求职信息弹窗 -->
    <el-dialog
      v-model="jobInfoDialogVisible"
      title="编辑求职信息"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="jobInfoFormRef"
        :model="jobInfoFormData"
        :rules="jobInfoRules"
        label-width="120px"
        label-position="left"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="期望职位" prop="position">
              <el-input
                v-model="jobInfoFormData.position"
                placeholder="如: Java开发工程师"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="工作年限" prop="workYears">
              <el-input-number
                v-model="jobInfoFormData.workYears"
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
            <el-form-item label="期望城市" prop="expectedCity">
              <el-input
                v-model="jobInfoFormData.expectedCity"
                placeholder="如: 北京、上海"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="期望行业" prop="expectedIndustry">
              <el-input
                v-model="jobInfoFormData.expectedIndustry"
                placeholder="如: 互联网、金融"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="期望薪资(最低)" prop="expectedSalaryMin">
              <el-input-number
                v-model="jobInfoFormData.expectedSalaryMin"
                :min="0"
                :max="999999"
                placeholder="单位: 元/月"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="期望薪资(最高)" prop="expectedSalaryMax">
              <el-input-number
                v-model="jobInfoFormData.expectedSalaryMax"
                :min="0"
                :max="999999"
                placeholder="单位: 元/月"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="掌握技能" prop="skill">
          <el-input
            v-model="jobInfoFormData.skill"
            type="textarea"
            :rows="3"
            placeholder="请输入您掌握的技能，用逗号分隔，如: Java,Spring,MySQL,Vue"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="个人简介" prop="profile">
          <el-input
            v-model="jobInfoFormData.profile"
            type="textarea"
            :rows="4"
            placeholder="请简要介绍您的工作经历、项目经验、个人优势等"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="jobInfoDialogVisible = false">取消</el-button>
          <el-button @click="resetJobInfoForm">重置</el-button>
          <el-button type="primary" @click="handleSaveJobInfo" :loading="savingJobInfo">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getStudentProfile, updateStudentProfile } from '@/api/student'
import { getJobInfo, saveJobInfo } from '@/api/resume'
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
      // 求职信息相关
      loadingJobInfo: false,
      jobInfoData: null,
      jobInfoDialogVisible: false,
      savingJobInfo: false,
      jobInfoFormData: {
        position: '',
        workYears: 0,
        expectedCity: '',
        expectedSalaryMin: null,
        expectedSalaryMax: null,
        expectedIndustry: '',
        skill: '',
        profile: ''
      },
      jobInfoRules: {
        position: [
          { max: 100, message: '期望职位长度不能超过100个字符', trigger: 'blur' }
        ],
        workYears: [
          { type: 'number', min: 0, max: 50, message: '工作年限必须在0-50年之间', trigger: 'blur' }
        ],
        expectedCity: [
          { max: 100, message: '期望城市长度不能超过100个字符', trigger: 'blur' }
        ],
        expectedIndustry: [
          { max: 100, message: '期望行业长度不能超过100个字符', trigger: 'blur' }
        ],
        expectedSalaryMin: [
          { type: 'number', min: 0, max: 999999, message: '薪资必须在0-999999之间', trigger: 'blur' }
        ],
        expectedSalaryMax: [
          { type: 'number', min: 0, max: 999999, message: '薪资必须在0-999999之间', trigger: 'blur' }
        ],
        skill: [
          { max: 500, message: '技能描述长度不能超过500个字符', trigger: 'blur' }
        ],
        profile: [
          { max: 1000, message: '个人简介长度不能超过1000个字符', trigger: 'blur' }
        ]
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
    await this.loadJobInfo()
  },
  computed: {
    hasJobInfo() {
      if (!this.jobInfoData) return false
      return !!(
        this.jobInfoData.position ||
        this.jobInfoData.workYears ||
        this.jobInfoData.expectedCity ||
        this.jobInfoData.expectedSalaryMin ||
        this.jobInfoData.expectedSalaryMax ||
        this.jobInfoData.expectedIndustry ||
        this.jobInfoData.skill ||
        this.jobInfoData.profile
      )
    }
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
    },

    // 求职信息相关方法
    async loadJobInfo() {
      try {
        this.loadingJobInfo = true
        const response = await getJobInfo()

        if (response && response.success) {
          this.jobInfoData = response.data || {}
        } else {
          this.jobInfoData = {}
        }
      } catch (error) {
        console.error('加载求职信息失败:', error)
        this.jobInfoData = {}
      } finally {
        this.loadingJobInfo = false
      }
    },

    openJobInfoDialog() {
      this.fillJobInfoForm()
      this.jobInfoDialogVisible = true
    },

    fillJobInfoForm() {
      if (this.jobInfoData) {
        this.jobInfoFormData = {
          position: this.jobInfoData.position || '',
          workYears: this.jobInfoData.workYears || 0,
          expectedCity: this.jobInfoData.expectedCity || '',
          expectedSalaryMin: this.jobInfoData.expectedSalaryMin || null,
          expectedSalaryMax: this.jobInfoData.expectedSalaryMax || null,
          expectedIndustry: this.jobInfoData.expectedIndustry || '',
          skill: this.jobInfoData.skill || '',
          profile: this.jobInfoData.profile || ''
        }
      }
    },

    resetJobInfoForm() {
      this.$refs.jobInfoFormRef?.resetFields()
    },

    async handleSaveJobInfo() {
      try {
        await this.$refs.jobInfoFormRef.validate()
        this.savingJobInfo = true

        const response = await saveJobInfo(this.jobInfoFormData)

        if (response && response.success) {
          this.$message.success('保存成功')
          this.jobInfoDialogVisible = false

          // 重新加载求职信息
          await this.loadJobInfo()
        } else {
          this.$message.error(response?.message || '保存失败')
        }
      } catch (error) {
        console.error('保存失败:', error)
        if (error !== false) { // 表单验证失败时不显示错误消息
          this.$message.error('保存失败')
        }
      } finally {
        this.savingJobInfo = false
      }
    },

    formatSalary(min, max) {
      if (!min && !max) return '未填写'
      if (min && max) return `${min} - ${max} 元/月`
      if (min) return `${min}+ 元/月`
      if (max) return `${max} 元/月以下`
      return '未填写'
    },

    getSkillList(skillStr) {
      if (!skillStr) return []
      return skillStr.split(',').map(s => s.trim()).filter(s => s)
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
  margin-bottom: 20px;
}

.job-info-card {
  margin-top: 20px;
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
