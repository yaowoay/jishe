<template>
  <div class="teacher-assistance">
    <el-card class="action-card">
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        <span>新增帮扶记录</span>
      </el-button>
    </el-card>

    <el-card class="table-card">
      <el-table
          :data="assistanceList"
          stripe
          style="width: 100%"
          :loading="loading"
          v-loading="loading"
      >
        <el-table-column prop="studentId" label="学生ID" width="100" />
        <el-table-column prop="difficultyType" label="困难类型" width="120" />
        <el-table-column prop="difficultyLevel" label="困难等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.difficultyLevel)">
              {{ row.difficultyLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="assistancePlan" label="帮扶计划" show-overflow-tooltip />
        <el-table-column prop="result" label="结果" width="100" />
        <el-table-column prop="followUpDate" label="跟进日期" width="120" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewDetail(row)">
              查看
            </el-button>
            <el-button type="success" size="small" @click="followUp(row)">
              跟进
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadData"
          @size-change="loadData"
          style="margin-top: 20px; text-align: right"
      />
    </el-card>

    <!-- 新增帮扶对话框 -->
    <el-dialog
        v-model="showCreateDialog"
        title="新增帮扶记录"
        width="600px"
        :close-on-click-modal="false"
    >
      <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="100px"
      >
        <el-form-item label="学生ID" prop="studentId">
          <el-input-number
              v-model="formData.studentId"
              :min="1"
              placeholder="请输入学生ID"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="困难类型" prop="difficultyType">
          <el-select v-model="formData.difficultyType" placeholder="请选择困难类型">
            <el-option label="就业困难" value="就业困难" />
            <el-option label="学业困难" value="学业困难" />
            <el-option label="心理困难" value="心理困难" />
            <el-option label="经济困难" value="经济困难" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="困难等级" prop="difficultyLevel">
          <el-select v-model="formData.difficultyLevel" placeholder="请选择困难等级">
            <el-option label="低" value="low" />
            <el-option label="中" value="medium" />
            <el-option label="高" value="high" />
          </el-select>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
              v-model="formData.description"
              type="textarea"
              :rows="3"
              placeholder="请描述学生遇到的困难"
          />
        </el-form-item>

        <el-form-item label="帮扶计划" prop="assistancePlan">
          <el-input
              v-model="formData.assistancePlan"
              type="textarea"
              :rows="3"
              placeholder="请输入帮扶计划"
          />
        </el-form-item>

        <el-form-item label="帮扶措施" prop="assistanceActions">
          <el-input
              v-model="formData.assistanceActions"
              type="textarea"
              :rows="3"
              placeholder="请输入已采取的帮扶措施"
          />
        </el-form-item>

        <el-form-item label="结果" prop="result">
          <el-input
              v-model="formData.result"
              placeholder="请输入帮扶结果（可选）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getAssistanceRecords, saveAssistanceRecord } from '@/api/teacher'

const loading = ref(false)
const submitLoading = ref(false)
const assistanceList = ref([])
const showCreateDialog = ref(false)
const formRef = ref(null)

const formData = ref({
  studentId: null,
  difficultyType: '',
  difficultyLevel: '',
  description: '',
  assistancePlan: '',
  assistanceActions: '',
  result: ''
})

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const rules = {
  studentId: [{ required: true, message: '请输入学生ID', trigger: 'blur' }],
  difficultyType: [{ required: true, message: '请选择困难类型', trigger: 'change' }],
  difficultyLevel: [{ required: true, message: '请选择困难等级', trigger: 'change' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
  assistancePlan: [{ required: true, message: '请输入帮扶计划', trigger: 'blur' }]
}

const getLevelType = (level) => {
  const typeMap = {
    'low': 'success',
    'medium': 'warning',
    'high': 'danger'
  }
  return typeMap[level] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const response = await getAssistanceRecords()
    if (response.success) {
      assistanceList.value = response.data || []
      pagination.value.total = assistanceList.value.length
    }
  } catch (error) {
    ElMessage.error('加载帮扶记录失败')
  } finally {
    loading.value = false
  }
}

const handleCreate = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const response = await saveAssistanceRecord(formData.value)
        if (response.success) {
          ElMessage.success('新增成功')
          showCreateDialog.value = false
          formData.value = {
            studentId: null,
            difficultyType: '',
            difficultyLevel: '',
            description: '',
            assistancePlan: '',
            assistanceActions: '',
            result: ''
          }
          loadData()
        }
      } catch (error) {
        ElMessage.error('新增失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const viewDetail = (row) => {
  ElMessage.info(`查看帮扶记录: ${row.recordId}`)
}

const followUp = (row) => {
  ElMessage.info(`跟进帮扶记录: ${row.recordId}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.teacher-assistance {
  .action-card {
    margin-bottom: 20px;
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }

  .table-card {
    border: none;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
}
</style>
