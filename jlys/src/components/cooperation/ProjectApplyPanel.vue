<template>
  <div>
    <div class="bar">
      <div>
        <el-select v-model="q.status" clearable placeholder="状态" @change="load"><el-option label="草稿" value="draft"/><el-option label="已提交" value="submitted"/><el-option label="已通过" value="approved"/></el-select>
        <el-select v-model="q.projectType" clearable placeholder="类型" @change="load"><el-option label="实训基地" value="training_base"/><el-option label="订单班" value="order_class"/><el-option label="实习基地" value="internship"/><el-option label="产学研" value="research"/></el-select>
      </div>
      <el-button type="primary" @click="open()">新建申请</el-button>
    </div>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="projectName" label="项目名称" min-width="170"/>
      <el-table-column prop="projectType" label="类型" width="120"/>
      <el-table-column prop="targetMajor" label="目标专业" width="140"/>
      <el-table-column prop="studentCount" label="人数" width="80"/>
      <el-table-column prop="status" label="状态" width="100"/>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{row}">
          <el-button link @click="open(row)">编辑</el-button>
          <el-button v-if="row.status==='draft'" link type="success" @click="submit(row.id)">提交</el-button>
          <el-button v-if="row.status==='draft'" link type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="p.page" v-model:page-size="p.size" :total="p.total" layout="total, prev, pager, next" @current-change="load" class="pager"/>

    <el-dialog v-model="show" :title="form.id?'编辑项目':'新建项目'" width="620px">
      <el-form :model="form" label-width="98px">
        <el-form-item label="项目名称"><el-input v-model="form.projectName"/></el-form-item>
        <el-form-item label="项目类型"><el-select v-model="form.projectType"><el-option label="实训基地" value="training_base"/><el-option label="订单班" value="order_class"/><el-option label="实习基地" value="internship"/><el-option label="产学研" value="research"/></el-select></el-form-item>
        <el-form-item label="合作模式"><el-input v-model="form.cooperationMode"/></el-form-item>
        <el-form-item label="项目描述"><el-input v-model="form.projectDesc" type="textarea" :rows="3"/></el-form-item>
        <el-form-item label="目标专业"><el-input v-model="form.targetMajor"/></el-form-item>
        <el-form-item label="学生人数"><el-input-number v-model="form.studentCount" :min="0"/></el-form-item>
        <el-form-item label="联系人"><el-input v-model="form.contactPerson"/></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="form.contactPhone"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="show=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createProject, deleteProject, getProjectList, submitProject, updateProject } from '@/api/cooperation'

const emit = defineEmits(['changed', 'projects-loaded'])
const loading = ref(false); const show = ref(false)
const list = ref([]); const q = reactive({ status: '', projectType: '' }); const p = reactive({ page: 1, size: 10, total: 0 })
const init = () => ({ id: null, projectName: '', projectType: '', cooperationMode: '', projectDesc: '', targetMajor: '', studentCount: 0, contactPerson: '', contactPhone: '' })
const form = reactive(init())

const load = async () => {
  loading.value = true
  try {
    const res = await getProjectList({ page: p.page, size: p.size, status: q.status || undefined, projectType: q.projectType || undefined })
    if (res.code === 0) { list.value = res.data.records || []; p.total = res.data.total || 0; emit('projects-loaded', list.value) }
  } finally { loading.value = false }
}
const open = (row) => { Object.assign(form, init(), row || {}); show.value = true }
const save = async () => {
  const api = form.id ? updateProject(form.id, form) : createProject(form)
  const res = await api
  if (res.code === 0) { ElMessage.success('保存成功'); show.value = false; load(); emit('changed') }
}
const submit = async (id) => {
  await ElMessageBox.confirm('确认提交？提交后不可编辑')
  const res = await submitProject(id); if (res.code === 0) { ElMessage.success('提交成功'); load(); emit('changed') }
}
const remove = async (id) => {
  await ElMessageBox.confirm('确认删除该草稿？')
  const res = await deleteProject(id); if (res.code === 0) { ElMessage.success('删除成功'); load(); emit('changed') }
}
onMounted(load)
</script>

<style scoped>
.bar{display:flex;justify-content:space-between;align-items:center;margin-bottom:12px}.bar>div{display:flex;gap:8px}.pager{margin-top:12px;justify-content:flex-end;display:flex}
</style>
