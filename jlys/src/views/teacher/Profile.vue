<template>
  <div class="teacher-profile">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>

      <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="profile-form"
      >
        <el-form-item label="工号" prop="teacherNo">
          <el-input v-model="formData.teacherNo" disabled />
        </el-form-item>

        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入姓名" />
        </el-form-item>

        <el-form-item label="角色类型" prop="roleType">
          <el-select v-model="formData.roleType" placeholder="请选择角色">
            <el-option label="辅导员" value="counselor" />
            <el-option label="学业导师" value="advisor" />
            <el-option label="管理员" value="admin" />
            <el-option label="领导" value="leader" />
          </el-select>
        </el-form-item>

        <el-form-item label="所属学院" prop="collegeId">
          <el-select v-model="formData.collegeId" placeholder="请选择学院">
            <el-option label="计算机与信息工程学院" :value="1" />
            <el-option label="电子工程学院" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="管理学院" prop="managedColleges">
          <el-select
              v-model="managedCollegesArray"
              multiple
              placeholder="请选择管理的学院"
          >
            <el-option label="计算机与信息工程学院" :value="1" />
            <el-option label="电子工程学院" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="管理专业" prop="managedMajors">
          <el-select
              v-model="managedMajorsArray"
              multiple
              placeholder="请选择管理的专业"
          >
            <el-option label="计算机科学与技术" :value="1" />
            <el-option label="软件工程" :value="2" />
            <el-option label="电子信息工程" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="loading">
            保存
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="password-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>

      <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="120px"
          class="password-form"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入原密码"
              show-password
          />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">
            修改密码
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTeacherProfile, saveTeacherProfile } from '@/api/teacher'

const formRef = ref(null)
const passwordFormRef = ref(null)
const loading = ref(false)
const passwordLoading = ref(false)

const formData = ref({
  teacherId: null,
  teacherNo: '',
  realName: '',
  roleType: '',
  collegeId: null,
  phone: '',
  email: '',
  managedColleges: '',
  managedMajors: '',
  status: 'active'
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const managedCollegesArray = computed({
  get: () => {
    try {
      return formData.value.managedColleges ? JSON.parse(formData.value.managedColleges) : []
    } catch {
      return []
    }
  },
  set: (val) => {
    formData.value.managedColleges = JSON.stringify(val)
  }
})

const managedMajorsArray = computed({
  get: () => {
    try {
      return formData.value.managedMajors ? JSON.parse(formData.value.managedMajors) : []
    } catch {
      return []
    }
  },
  set: (val) => {
    formData.value.managedMajors = JSON.stringify(val)
  }
})

const rules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  roleType: [{ required: true, message: '请选择角色类型', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const loadProfile = async () => {
  try {
    const response = await getTeacherProfile()
    if (response.success && response.data) {
      formData.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载个人信息失败')
  }
}

const handleSave = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await saveTeacherProfile(formData.value)
        if (response.success) {
          ElMessage.success('保存成功')
          loadProfile()
        }
      } catch (error) {
        ElMessage.error('保存失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleReset = () => {
  loadProfile()
}

const handleChangePassword = async () => {
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordLoading.value = true
      try {
        // TODO: 调用修改密码 API
        ElMessage.success('密码修改成功，请重新登录')
        passwordForm.value = {
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        }
      } catch (error) {
        ElMessage.error('密码修改失败')
      } finally {
        passwordLoading.value = false
      }
    }
  })
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped lang="scss">
.teacher-profile {
  .profile-card,
  .password-card {
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

    .card-header {
      font-size: 16px;
      font-weight: 600;
    }

    .profile-form,
    .password-form {
      max-width: 600px;
    }
  }
}
</style>
