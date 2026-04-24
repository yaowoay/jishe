<template>
  <div class="cooperation-container">
    <!-- 头部 -->
    <div class="page-header">
      <h1>校企合作管理</h1>
      <p class="subtitle">合作项目申请与管理平台</p>
    </div>

    <!-- 标签页 -->
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="合作项目" name="projects">
        <!-- 统计卡片 -->
        <el-row :gutter="20" class="stats-row">
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-number">{{ stats.totalProjects || 0 }}</div>
                <div class="stat-label">总项目数</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                <el-icon><Edit /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-number">{{ stats.draftProjects || 0 }}</div>
                <div class="stat-label">草稿项目</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-number">{{ stats.submittedProjects || 0 }}</div>
                <div class="stat-label">审核中</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
                <el-icon><Check /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-number">{{ stats.ongoingProjects || 0 }}</div>
                <div class="stat-label">进行中</div>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- 操作栏 -->
        <div class="action-bar">
          <el-button type="primary" @click="openProjectDialog()">
            <el-icon><Plus /></el-icon>
            新建项目
          </el-button>
          <div class="filters">
            <el-select v-model="projectFilters.status" placeholder="全部状态" clearable @change="loadProjects">
              <el-option label="草稿" value="draft" />
              <el-option label="已提交" value="submitted" />
              <el-option label="已通过" value="approved" />
              <el-option label="已拒绝" value="rejected" />
              <el-option label="进行中" value="ongoing" />
            </el-select>
            <el-select v-model="projectFilters.projectType" placeholder="全部类型" clearable @change="loadProjects">
              <el-option label="实训基地" value="training_base" />
              <el-option label="订单班" value="order_class" />
              <el-option label="实习基地" value="internship" />
              <el-option label="产学研合作" value="research" />
            </el-select>
          </div>
        </div>

        <!-- 项目列表 -->
        <el-table :data="projects" v-loading="loading" stripe>
          <el-table-column prop="projectName" label="项目名称" min-width="180" />
          <el-table-column prop="cooperationMode" label="合作模式" width="140" />
          <el-table-column prop="targetMajor" label="目标专业" width="140" />
          <el-table-column prop="studentCount" label="学生人数" width="100" align="center" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="submitTime" label="提交时间" width="160">
            <template #default="{ row }">
              {{ row.submitTime ? formatDate(row.submitTime) : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="viewProject(row)">查看</el-button>
              <el-button v-if="row.status === 'draft'" link type="primary" @click="editProject(row)">编辑</el-button>
              <el-button v-if="row.status === 'draft'" link type="success" @click="submitProject(row)">提交</el-button>
              <el-button v-if="row.status === 'draft'" link type="danger" @click="deleteProject(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadProjects"
          @current-change="loadProjects"
          class="pagination"
        />
      </el-tab-pane>

      <el-tab-pane label="实训基地" name="training-bases">
        <div class="action-bar">
          <el-button type="primary" @click="openBaseDialog()">
            <el-icon><Plus /></el-icon>
            新建实训基地
          </el-button>
        </div>

        <el-table :data="trainingBases" v-loading="loading" stripe>
          <el-table-column prop="baseName" label="基地名称" min-width="180" />
          <el-table-column prop="projectName" label="所属项目" width="160" />
          <el-table-column prop="baseAddress" label="基地地址" min-width="200" />
          <el-table-column prop="capacity" label="容纳人数" width="100" align="center" />
          <el-table-column prop="constructionStatus" label="建设状态" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="getConstructionStatusType(row.constructionStatus)">
                {{ getConstructionStatusText(row.constructionStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="viewBase(row)">查看</el-button>
              <el-button link type="primary" @click="editBase(row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="订单班管理" name="order-classes">
        <div class="action-bar">
          <el-button type="primary" @click="openClassDialog()">
            <el-icon><Plus /></el-icon>
            新建订单班
          </el-button>
        </div>

        <el-table :data="orderClasses" v-loading="loading" stripe>
          <el-table-column prop="className" label="班级名称" min-width="160" />
          <el-table-column prop="projectName" label="所属项目" width="160" />
          <el-table-column prop="major" label="专业" width="140" />
          <el-table-column prop="grade" label="年级" width="100" align="center" />
          <el-table-column label="招生进度" width="140" align="center">
            <template #default="{ row }">
              {{ row.enrolledCount || 0 }} / {{ row.plannedCount || 0 }}
            </template>
          </el-table-column>
          <el-table-column prop="classStatus" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getClassStatusType(row.classStatus)">
                {{ getClassStatusText(row.classStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="viewClass(row)">查看</el-button>
              <el-button link type="primary" @click="editClass(row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="案例参考库" name="cases">
        <div class="filters">
          <el-select v-model="caseFilters.cooperationType" placeholder="全部类型" clearable @change="loadCases">
            <el-option label="实训基地" value="training_base" />
            <el-option label="订单班" value="order_class" />
            <el-option label="实习基地" value="internship" />
            <el-option label="产学研合作" value="research" />
          </el-select>
          <el-checkbox v-model="caseFilters.isFeatured" @change="loadCases">仅显示精选案例</el-checkbox>
        </div>

        <div class="cases-grid">
          <div v-for="item in cases" :key="item.id" class="case-card" @click="viewCase(item)">
            <div class="case-header">
              <h3>{{ item.caseTitle }}</h3>
              <el-tag v-if="item.isFeatured" type="warning" size="small">精选</el-tag>
            </div>
            <div class="case-info">
              <p><strong>企业：</strong>{{ item.companyName }}</p>
              <p><strong>合作时间：</strong>{{ item.cooperationDuration }}</p>
              <p><strong>学生人数：</strong>{{ item.studentCount }}人</p>
            </div>
            <div class="case-summary">{{ item.caseSummary }}</div>
            <div class="case-footer">
              <el-tag size="small">{{ getCooperationTypeText(item.cooperationType) }}</el-tag>
              <span class="view-count">
                <el-icon><View /></el-icon>
                {{ item.viewCount }}
              </span>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <el-pagination
          v-model:current-page="casePagination.page"
          v-model:page-size="casePagination.size"
          :total="casePagination.total"
          layout="prev, pager, next"
          @current-change="loadCases"
          class="pagination"
        />
      </el-tab-pane>
    </el-tabs>

    <!-- 项目表单对话框 -->
    <el-dialog
      v-model="projectDialogVisible"
      :title="projectForm.id ? '编辑项目' : '新建项目'"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form :model="projectForm" :rules="projectRules" ref="projectFormRef" label-width="120px">
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="projectForm.projectName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目类型" prop="projectType">
          <el-select v-model="projectForm.projectType" placeholder="请选择项目类型">
            <el-option label="实训基地" value="training_base" />
            <el-option label="订单班" value="order_class" />
            <el-option label="实习基地" value="internship" />
            <el-option label="产学研合作" value="research" />
          </el-select>
        </el-form-item>
        <el-form-item label="合作模式" prop="cooperationMode">
          <el-input v-model="projectForm.cooperationMode" placeholder="如：共建实训基地" />
        </el-form-item>
        <el-form-item label="项目描述" prop="projectDesc">
          <el-input v-model="projectForm.projectDesc" type="textarea" :rows="4" placeholder="请输入项目描述" />
        </el-form-item>
        <el-form-item label="目标专业">
          <el-input v-model="projectForm.targetMajor" placeholder="如：计算机科学与技术" />
        </el-form-item>
        <el-form-item label="学生人数">
          <el-input-number v-model="projectForm.studentCount" :min="0" />
        </el-form-item>
        <el-form-item label="投资金额">
          <el-input-number v-model="projectForm.investmentAmount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="projectForm.startDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="projectForm.endDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="projectForm.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="projectForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="projectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProject">保存</el-button>
      </template>
    </el-dialog>

    <!-- 实训基地表单对话框 -->
    <el-dialog
      v-model="baseDialogVisible"
      :title="baseForm.id ? '编辑实训基地' : '新建实训基地'"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form :model="baseForm" :rules="baseRules" ref="baseFormRef" label-width="120px">
        <el-form-item label="所属项目" prop="projectId">
          <el-select v-model="baseForm.projectId" placeholder="请选择项目">
            <el-option
              v-for="project in approvedProjects"
              :key="project.id"
              :label="project.projectName"
              :value="project.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="基地名称" prop="baseName">
          <el-input v-model="baseForm.baseName" placeholder="请输入基地名称" />
        </el-form-item>
        <el-form-item label="基地地址" prop="baseAddress">
          <el-input v-model="baseForm.baseAddress" placeholder="请输入基地地址" />
        </el-form-item>
        <el-form-item label="基地面积">
          <el-input-number v-model="baseForm.baseArea" :min="0" :precision="2" />
          <span style="margin-left: 10px;">平方米</span>
        </el-form-item>
        <el-form-item label="设备价值">
          <el-input-number v-model="baseForm.equipmentValue" :min="0" :precision="2" />
          <span style="margin-left: 10px;">元</span>
        </el-form-item>
        <el-form-item label="容纳人数">
          <el-input-number v-model="baseForm.capacity" :min="0" />
        </el-form-item>
        <el-form-item label="培训领域">
          <el-input v-model="baseForm.trainingFields" type="textarea" :rows="3" placeholder="如：软件开发、网络工程" />
        </el-form-item>
        <el-form-item label="设施设备">
          <el-input v-model="baseForm.facilities" type="textarea" :rows="3" placeholder="如：计算机100台、服务器10台" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="baseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveBase">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Edit, Clock, Check, Plus, View } from '@element-plus/icons-vue'
import axios from 'axios'

const API_BASE = '/api/cooperation'

// 当前标签页
const activeTab = ref('projects')

// 加载状态
const loading = ref(false)

// 统计数据
const stats = reactive({
  totalProjects: 0,
  draftProjects: 0,
  submittedProjects: 0,
  ongoingProjects: 0
})

// 项目列表
const projects = ref([])
const projectFilters = reactive({
  status: '',
  projectType: ''
})
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 实训基地列表
const trainingBases = ref([])

// 订单班列表
const orderClasses = ref([])

// 案例列表
const cases = ref([])
const caseFilters = reactive({
  cooperationType: '',
  isFeatured: false
})
const casePagination = reactive({
  page: 1,
  size: 12,
  total: 0
})

// 项目表单
const projectDialogVisible = ref(false)
const projectFormRef = ref(null)
const projectForm = reactive({
  id: null,
  projectName: '',
  projectType: '',
  cooperationMode: '',
  projectDesc: '',
  targetMajor: '',
  studentCount: null,
  investmentAmount: null,
  startDate: '',
  endDate: '',
  contactPerson: '',
  contactPhone: ''
})

const projectRules = {
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  projectType: [{ required: true, message: '请选择项目类型', trigger: 'change' }],
  cooperationMode: [{ required: true, message: '请输入合作模式', trigger: 'blur' }],
  projectDesc: [{ required: true, message: '请输入项目描述', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

// 实训基地表单
const baseDialogVisible = ref(false)
const baseFormRef = ref(null)
const baseForm = reactive({
  id: null,
  projectId: null,
  baseName: '',
  baseAddress: '',
  baseArea: null,
  equipmentValue: null,
  capacity: null,
  trainingFields: '',
  facilities: ''
})

const baseRules = {
  projectId: [{ required: true, message: '请选择所属项目', trigger: 'change' }],
  baseName: [{ required: true, message: '请输入基地名称', trigger: 'blur' }],
  baseAddress: [{ required: true, message: '请输入基地地址', trigger: 'blur' }]
}

// 已通过的项目列表（用于实训基地和订单班选择）
const approvedProjects = ref([])

// 页面加载
onMounted(() => {
  loadStats()
  loadProjects()
})

// 标签页切换
const handleTabClick = (tab) => {
  if (tab.props.name === 'projects') {
    loadProjects()
  } else if (tab.props.name === 'training-bases') {
    loadTrainingBases()
  } else if (tab.props.name === 'order-classes') {
    loadOrderClasses()
  } else if (tab.props.name === 'cases') {
    loadCases()
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const response = await axios.get(`${API_BASE}/stats`)
    if (response.data.code === 0) {
      Object.assign(stats, response.data.data)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载项目列表
const loadProjects = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      status: projectFilters.status || undefined,
      projectType: projectFilters.projectType || undefined
    }
    const response = await axios.get(`${API_BASE}/projects`, { params })
    if (response.data.code === 0) {
      projects.value = response.data.data.records
      pagination.total = response.data.data.total
    }
  } catch (error) {
    console.error('加载项目列表失败:', error)
    ElMessage.error('加载项目列表失败')
  } finally {
    loading.value = false
  }
}

// 加载实训基地列表
const loadTrainingBases = async () => {
  loading.value = true
  try {
    const response = await axios.get(`${API_BASE}/training-bases`)
    if (response.data.code === 0) {
      trainingBases.value = response.data.data
    }
  } catch (error) {
    console.error('加载实训基地列表失败:', error)
    ElMessage.error('加载实训基地列表失败')
  } finally {
    loading.value = false
  }
}

// 加载订单班列表
const loadOrderClasses = async () => {
  loading.value = true
  try {
    const response = await axios.get(`${API_BASE}/order-classes`)
    if (response.data.code === 0) {
      orderClasses.value = response.data.data
    }
  } catch (error) {
    console.error('加载订单班列表失败:', error)
    ElMessage.error('加载订单班列表失败')
  } finally {
    loading.value = false
  }
}

// 加载案例列表
const loadCases = async () => {
  loading.value = true
  try {
    const params = {
      page: casePagination.page,
      size: casePagination.size,
      cooperationType: caseFilters.cooperationType || undefined,
      isFeatured: caseFilters.isFeatured || undefined
    }
    const response = await axios.get(`${API_BASE}/cases`, { params })
    if (response.data.code === 0) {
      cases.value = response.data.data.records
      casePagination.total = response.data.data.total
    }
  } catch (error) {
    console.error('加载案例列表失败:', error)
    ElMessage.error('加载案例列表失败')
  } finally {
    loading.value = false
  }
}

// 加载已通过的项目
const loadApprovedProjects = async () => {
  try {
    const response = await axios.get(`${API_BASE}/projects`, {
      params: { status: 'approved', page: 1, size: 100 }
    })
    if (response.data.code === 0) {
      approvedProjects.value = response.data.data.records
    }
  } catch (error) {
    console.error('加载项目列表失败:', error)
  }
}

// 打开项目对话框
const openProjectDialog = (project = null) => {
  if (project) {
    Object.assign(projectForm, project)
  } else {
    Object.keys(projectForm).forEach(key => {
      projectForm[key] = key === 'id' ? null : ''
    })
  }
  projectDialogVisible.value = true
}

// 保存项目
const saveProject = async () => {
  try {
    await projectFormRef.value.validate()

    const data = { ...projectForm }
    let response

    if (data.id) {
      response = await axios.put(`${API_BASE}/projects/${data.id}`, data)
    } else {
      response = await axios.post(`${API_BASE}/projects`, data)
    }

    if (response.data.code === 0) {
      ElMessage.success('保存成功')
      projectDialogVisible.value = false
      loadProjects()
      loadStats()
    } else {
      ElMessage.error(response.data.message || '保存失败')
    }
  } catch (error) {
    console.error('保存项目失败:', error)
  }
}

// 查看项目
const viewProject = (project) => {
  openProjectDialog(project)
}

// 编辑项目
const editProject = (project) => {
  openProjectDialog(project)
}

// 提交项目
const submitProject = async (project) => {
  try {
    await ElMessageBox.confirm('确定要提交此项目吗？提交后将无法修改。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await axios.post(`${API_BASE}/projects/${project.id}/submit`)
    if (response.data.code === 0) {
      ElMessage.success('提交成功')
      loadProjects()
      loadStats()
    } else {
      ElMessage.error(response.data.message || '提交失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交项目失败:', error)
    }
  }
}

// 删除项目
const deleteProject = async (project) => {
  try {
    await ElMessageBox.confirm('确定要删除此项目吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await axios.delete(`${API_BASE}/projects/${project.id}`)
    if (response.data.code === 0) {
      ElMessage.success('删除成功')
      loadProjects()
      loadStats()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除项目失败:', error)
    }
  }
}

// 打开实训基地对话框
const openBaseDialog = (base = null) => {
  loadApprovedProjects()
  if (base) {
    Object.assign(baseForm, base)
  } else {
    Object.keys(baseForm).forEach(key => {
      baseForm[key] = key === 'id' ? null : ''
    })
  }
  baseDialogVisible.value = true
}

// 保存实训基地
const saveBase = async () => {
  try {
    await baseFormRef.value.validate()

    const data = { ...baseForm }
    let response

    if (data.id) {
      response = await axios.put(`${API_BASE}/training-bases/${data.id}`, data)
    } else {
      response = await axios.post(`${API_BASE}/training-bases?projectId=${data.projectId}`, data)
    }

    if (response.data.code === 0) {
      ElMessage.success('保存成功')
      baseDialogVisible.value = false
      loadTrainingBases()
    } else {
      ElMessage.error(response.data.message || '保存失败')
    }
  } catch (error) {
    console.error('保存实训基地失败:', error)
  }
}

// 查看实训基地
const viewBase = (base) => {
  openBaseDialog(base)
}

// 编辑实训基地
const editBase = (base) => {
  openBaseDialog(base)
}

// 查看订单班
const viewClass = (cls) => {
  ElMessage.info('查看订单班详情')
}

// 编辑订单班
const editClass = (cls) => {
  ElMessage.info('编辑订单班')
}

// 打开订单班对话框
const openClassDialog = () => {
  ElMessage.info('新建订单班功能开发中')
}

// 查看案例
const viewCase = async (caseItem) => {
  try {
    await axios.get(`${API_BASE}/cases/${caseItem.id}`)
    ElMessage.success('查看案例详情')
  } catch (error) {
    console.error('查看案例失败:', error)
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

// 状态类型
const getStatusType = (status) => {
  const typeMap = {
    draft: 'info',
    submitted: 'warning',
    approved: 'success',
    rejected: 'danger',
    ongoing: 'primary'
  }
  return typeMap[status] || 'info'
}

// 状态文本
const getStatusText = (status) => {
  const textMap = {
    draft: '草稿',
    submitted: '已提交',
    reviewing: '审核中',
    approved: '已通过',
    rejected: '已拒绝',
    ongoing: '进行中',
    completed: '已完成'
  }
  return textMap[status] || status
}

// 建设状态类型
const getConstructionStatusType = (status) => {
  const typeMap = {
    planning: 'info',
    constructing: 'warning',
    completed: 'success',
    operating: 'primary'
  }
  return typeMap[status] || 'info'
}

// 建设状态文本
const getConstructionStatusText = (status) => {
  const textMap = {
    planning: '规划中',
    constructing: '建设中',
    completed: '已完成',
    operating: '运营中'
  }
  return textMap[status] || status
}

// 班级状态类型
const getClassStatusType = (status) => {
  const typeMap = {
    recruiting: 'warning',
    training: 'primary',
    graduated: 'success'
  }
  return typeMap[status] || 'info'
}

// 班级状态文本
const getClassStatusText = (status) => {
  const textMap = {
    recruiting: '招生中',
    training: '培训中',
    graduated: '已毕业'
  }
  return textMap[status] || status
}

// 合作类型文本
const getCooperationTypeText = (type) => {
  const textMap = {
    training_base: '实训基地',
    order_class: '订单班',
    internship: '实习基地',
    research: '产学研合作'
  }
  return textMap[type] || type
}
</script>

<style scoped>
.cooperation-container {
  padding: 20px;
  background: #f5f5f5;
  min-height: calc(100vh - 60px);
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.page-header h1 {
  font-size: 28px;
  margin: 0 0 10px 0;
}

.subtitle {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filters {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.cases-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.case-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.3s;
}

.case-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
}

.case-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.case-header h3 {
  font-size: 18px;
  margin: 0;
  color: #111827;
  flex: 1;
}

.case-info {
  margin-bottom: 15px;
}

.case-info p {
  margin: 8px 0;
  font-size: 14px;
  color: #6b7280;
}

.case-summary {
  color: #374151;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.case-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #e5e7eb;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #9ca3af;
  font-size: 12px;
}
</style>

// 项目表单
const projectDialogVisible = ref(false)
const projectFormRef = ref(null)
const projectForm = reactive({
  id: null,
  projectName: '',
  projectType: '',
  cooperationMode: '',
  projectDesc: '',
  targetMajor: '',
  studentCount: null,
  investmentAmount: null,
  startDate: '',
  endDate: '',
  contactPerson: '',
  contactPhone: ''
})
const projectRules = {
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  projectType: [{ required: true, message: '请选择项目类型', trigger: 'change' }],
  cooperationMode: [{ required: true, message: '请输入合作模式', trigger: 'blur' }],
  projectDesc: [{ required: true, message: '请输入项目描述', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

// 实训基地表单
const baseDialogVisible = ref(false)
const baseFormRef = ref(null)
const baseForm = reactive({
  id: null,
  projectId: null,
  baseName: '',
  baseAddress: '',
  baseArea: null,
  equipmentValue: null,
  capacity: null,
  trainingFields: '',
  facilities: ''
})
const baseRules = {
  projectId: [{ required: true, message: '请选择所属项目', trigger: 'change' }],
  baseName: [{ required: true, message: '请输入基地名称', trigger: 'blur' }],
  baseAddress: [{ required: true, message: '请输入基地地址', trigger: 'blur' }]
}
const approvedProjects = ref([])

// 标签页切换
const handleTabClick = (tab) => {
  if (tab.props.name === 'projects') {
    loadProjects()
    loadStats()
  } else if (tab.props.name === 'training-bases') {
    loadTrainingBases()
  } else if (tab.props.name === 'order-classes') {
    loadOrderClasses()
  } else if (tab.props.name === 'cases') {
    loadCases()
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const response = await axios.get(`${API_BASE}/stats`)
    if (response.data.code === 0) {
      Object.assign(stats, response.data.data)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载项目列表
const loadProjects = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      status: projectFilters.status || undefined,
      projectType: projectFilters.projectType || undefined
    }
    const response = await axios.get(`${API_BASE}/projects`, { params })
    if (response.data.code === 0) {
      projects.value = response.data.data.records
      pagination.total = response.data.data.total
    }
  } catch (error) {
    console.error('加载项目列表失败:', error)
    ElMessage.error('加载项目列表失败')
  } finally {
    loading.value = false
  }
}

// 打开项目对话框
const openProjectDialog = (project = null) => {
  if (project) {
    Object.assign(projectForm, project)
  } else {
    Object.keys(projectForm).forEach(key => {
      projectForm[key] = key === 'id' ? null : ''
    })
  }
  projectDialogVisible.value = true
}

// 保存项目
const saveProject = async () => {
  try {
    await projectFormRef.value.validate()

    const data = { ...projectForm }
    let response

    if (data.id) {
      response = await axios.put(`${API_BASE}/projects/${data.id}`, data)
    } else {
      response = await axios.post(`${API_BASE}/projects`, data)
    }

    if (response.data.code === 0) {
      ElMessage.success('保存成功')
      projectDialogVisible.value = false
      loadProjects()
      loadStats()
    } else {
      ElMessage.error(response.data.message || '保存失败')
    }
  } catch (error) {
    console.error('保存项目失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    }
  }
}

// 编辑项目
const editProject = (project) => {
  openProjectDialog(project)
}

// 查看项目
const viewProject = (project) => {
  openProjectDialog(project)
  // 禁用表单
  setTimeout(() => {
    const formEl = projectFormRef.value?.$el
    if (formEl) {
      formEl.querySelectorAll('input, textarea, select').forEach(el => {
        el.disabled = true
      })
    }
  }, 100)
}

// 提交项目
const submitProject = async (project) => {
  try {
    await ElMessageBox.confirm('确定要提交此项目吗？提交后将无法修改。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await axios.post(`${API_BASE}/projects/${project.id}/submit`)
    if (response.data.code === 0) {
      ElMessage.success('提交成功')
      loadProjects()
      loadStats()
    } else {
      ElMessage.error(response.data.message || '提交失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交项目失败:', error)
      ElMessage.error('提交失败')
    }
  }
}

// 删除项目
const deleteProject = async (project) => {
  try {
    await ElMessageBox.confirm('确定要删除此项目吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await axios.delete(`${API_BASE}/projects/${project.id}`)
    if (response.data.code === 0) {
      ElMessage.success('删除成功')
      loadProjects()
      loadStats()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除项目失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 加载实训基地列表
const loadTrainingBases = async () => {
  loading.value = true
  try {
    const response = await axios.get(`${API_BASE}/training-bases`)
    if (response.data.code === 0) {
      trainingBases.value = response.data.data
    }
  } catch (error) {
    console.error('加载实训基地列表失败:', error)
    ElMessage.error('加载实训基地列表失败')
  } finally {
    loading.value = false
  }
}

// 加载已通过的项目列表
const loadApprovedProjects = async () => {
  try {
    const response = await axios.get(`${API_BASE}/projects`, {
      params: { status: 'approved', page: 1, size: 100 }
    })
    if (response.data.code === 0) {
      approvedProjects.value = response.data.data.records
    }
  } catch (error) {
    console.error('加载项目列表失败:', error)
  }
}

// 打开实训基地对话框
const openBaseDialog = (base = null) => {
  loadApprovedProjects()
  if (base) {
    Object.assign(baseForm, base)
  } else {
    Object.keys(baseForm).forEach(key => {
      baseForm[key] = key === 'id' ? null : ''
    })
  }
  baseDialogVisible.value = true
}

// 保存实训基地
const saveBase = async () => {
  try {
    await baseFormRef.value.validate()

    const data = { ...baseForm }
    let response

    if (data.id) {
      response = await axios.put(`${API_BASE}/training-bases/${data.id}`, data)
    } else {
      response = await axios.post(`${API_BASE}/training-bases?projectId=${data.projectId}`, data)
    }

    if (response.data.code === 0) {
      ElMessage.success('保存成功')
      baseDialogVisible.value = false
      loadTrainingBases()
    } else {
      ElMessage.error(response.data.message || '保存失败')
    }
  } catch (error) {
    console.error('保存实训基地失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    }
  }
}

// 编辑实训基地
const editBase = (base) => {
  openBaseDialog(base)
}

// 查看实训基地
const viewBase = (base) => {
  openBaseDialog(base)
}

// 加载订单班列表
const loadOrderClasses = async () => {
  loading.value = true
  try {
    const response = await axios.get(`${API_BASE}/order-classes`)
    if (response.data.code === 0) {
      orderClasses.value = response.data.data
    }
  } catch (error) {
    console.error('加载订单班列表失败:', error)
    ElMessage.error('加载订单班列表失败')
  } finally {
    loading.value = false
  }
}

// 打开订单班对话框
const openClassDialog = () => {
  ElMessage.info('订单班功能开发中')
}

// 编辑订单班
const editClass = (cls) => {
  ElMessage.info('订单班编辑功能开发中')
}

// 查看订单班
const viewClass = (cls) => {
  ElMessage.info('订单班详情功能开发中')
}

// 加载案例列表
const loadCases = async () => {
  loading.value = true
  try {
    const params = {
      page: casePagination.page,
      size: casePagination.size,
      cooperationType: caseFilters.cooperationType || undefined,
      isFeatured: caseFilters.isFeatured || undefined
    }
    const response = await axios.get(`${API_BASE}/cases`, { params })
    if (response.data.code === 0) {
      cases.value = response.data.data.records
      casePagination.total = response.data.data.total
    }
  } catch (error) {
    console.error('加载案例列表失败:', error)
    ElMessage.error('加载案例列表失败')
  } finally {
    loading.value = false
  }
}

// 查看案例详情
const viewCase = (caseItem) => {
  ElMessageBox.alert(
    `<div style="line-height: 1.8;">
      <p><strong>企业：</strong>${caseItem.companyName}</p>
      <p><strong>合作时间：</strong>${caseItem.cooperationDuration}</p>
      <p><strong>学生人数：</strong>${caseItem.studentCount}人</p>
      <p><strong>案例摘要：</strong></p>
      <p style="text-indent: 2em;">${caseItem.caseSummary}</p>
      ${caseItem.successFactors ? `<p><strong>成功要素：</strong></p><p style="text-indent: 2em;">${caseItem.successFactors}</p>` : ''}
    </div>`,
    caseItem.caseTitle,
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  )
}

// 工具函数
const getStatusType = (status) => {
  const typeMap = {
    draft: 'info',
    submitted: 'warning',
    approved: 'success',
    rejected: 'danger',
    ongoing: 'primary'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    draft: '草稿',
    submitted: '已提交',
    reviewing: '审核中',
    approved: '已通过',
    rejected: '已拒绝',
    ongoing: '进行中',
    completed: '已完成'
  }
  return textMap[status] || status
}

const getConstructionStatusType = (status) => {
  const typeMap = {
    planning: 'info',
    constructing: 'warning',
    completed: 'success',
    operating: 'primary'
  }
  return typeMap[status] || 'info'
}

const getConstructionStatusText = (status) => {
  const textMap = {
    planning: '规划中',
    constructing: '建设中',
    completed: '已完成',
    operating: '运营中'
  }
  return textMap[status] || status
}

const getClassStatusType = (status) => {
  const typeMap = {
    recruiting: 'warning',
    training: 'primary',
    graduated: 'success'
  }
  return typeMap[status] || 'info'
}

const getClassStatusText = (status) => {
  const textMap = {
    recruiting: '招生中',
    training: '培训中',
    graduated: '已毕业'
  }
  return textMap[status] || status
}

const getCooperationTypeText = (type) => {
  const textMap = {
    training_base: '实训基地',
    order_class: '订单班',
    internship: '实习基地',
    research: '产学研合作'
  }
  return textMap[type] || type
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

// 页面加载
onMounted(() => {
  loadProjects()
  loadStats()
})

<style scoped>
.cooperation-container {
  padding: 20px;
  background: #f5f5f5;
  min-height: calc(100vh - 60px);
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.page-header h1 {
  font-size: 28px;
  margin: 0 0 10px 0;
}

.page-header .subtitle {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  margin-right: 15px;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filters {
  display: flex;
  gap: 10px;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.cases-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.case-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  cursor: pointer;
  transition: all 0.3s;
}

.case-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.case-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.case-header h3 {
  font-size: 18px;
  margin: 0;
  color: #111827;
  flex: 1;
}

.case-info {
  margin-bottom: 15px;
}

.case-info p {
  margin: 8px 0;
  color: #6b7280;
  font-size: 14px;
}

.case-summary {
  color: #374151;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.case-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #e5e7eb;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #9ca3af;
  font-size: 12px;
}

:deep(.el-tabs__header) {
  background: white;
  padding: 15px 20px 0;
  border-radius: 8px;
  margin-bottom: 20px;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}
</style>
