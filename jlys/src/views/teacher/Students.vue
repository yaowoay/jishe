<template>
  <div class="teacher-students">
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <el-input
              v-model="searchForm.keyword"
              placeholder="学生姓名/学号"
              clearable
              @keyup.enter="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-select
              v-model="searchForm.collegeId"
              placeholder="选择学院"
              clearable
          >
            <el-option label="计算机与信息工程学院" :value="1" />
            <el-option label="电子工程学院" :value="2" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-select
              v-model="searchForm.majorId"
              placeholder="选择专业"
              clearable
          >
            <el-option label="计算机科学与技术" :value="1" />
            <el-option label="软件工程" :value="2" />
            <el-option label="电子信息工程" :value="3" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon>
            <span>查询</span>
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="table-card">
      <el-table
          :data="studentList"
          stripe
          style="width: 100%"
          :loading="loading"
          v-loading="loading"
      >
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="graduationYear" label="毕业年份" width="100" />
        <el-table-column prop="gpa" label="GPA" width="80" />
        <el-table-column prop="expectedCity" label="期望城市" width="100" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewStudent(row)">
              查看
            </el-button>
            <el-button type="warning" size="small" @click="assistStudent(row)">
              帮扶
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
          @current-change="handleSearch"
          @size-change="handleSearch"
          style="margin-top: 20px; text-align: right"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { queryStudents } from '@/api/teacher'

const loading = ref(false)
const studentList = ref([])
const searchForm = ref({
  keyword: '',
  collegeId: null,
  majorId: null
})
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const handleSearch = async () => {
  loading.value = true
  try {
    const response = await queryStudents({
      ...searchForm.value,
      current: pagination.value.current,
      size: pagination.value.size
    })
    if (response.success) {
      studentList.value = response.data || []
      pagination.value.total = response.total || 0
    }
  } catch (error) {
    ElMessage.error('查询学生失败')
  } finally {
    loading.value = false
  }
}

const viewStudent = (row) => {
  ElMessage.info(`查看学生: ${row.realName}`)
}

const assistStudent = (row) => {
  ElMessage.info(`帮扶学生: ${row.realName}`)
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped lang="scss">
.teacher-students {
  .search-card {
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
