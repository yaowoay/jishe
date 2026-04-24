<template>
  <div>
    <div class="bar"><el-button type="primary" @click="open()">新建订单班</el-button></div>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="className" label="班级名称" min-width="160"/>
      <el-table-column prop="projectName" label="所属项目" min-width="160"/>
      <el-table-column prop="major" label="专业" width="140"/>
      <el-table-column prop="grade" label="年级" width="100"/>
      <el-table-column label="招生" width="120"><template #default="{row}">{{ row.enrolledCount || 0 }} / {{ row.plannedCount || 0 }}</template></el-table-column>
      <el-table-column prop="classStatus" label="状态" width="100"/>
      <el-table-column label="操作" width="120"><template #default="{row}"><el-button link @click="open(row)">编辑</el-button></template></el-table-column>
    </el-table>

    <el-dialog v-model="show" :title="form.id?'编辑订单班':'新建订单班'" width="620px">
      <el-form :model="form" label-width="96px">
        <el-form-item label="所属项目"><el-select v-model="form.projectId"><el-option v-for="p in projectOptions" :key="p.id" :label="p.projectName" :value="p.id"/></el-select></el-form-item>
        <el-form-item label="班级名称"><el-input v-model="form.className"/></el-form-item>
        <el-form-item label="专业"><el-input v-model="form.major"/></el-form-item>
        <el-form-item label="年级"><el-input v-model="form.grade"/></el-form-item>
        <el-form-item label="计划人数"><el-input-number v-model="form.plannedCount" :min="0"/></el-form-item>
        <el-form-item label="企业导师"><el-input v-model="form.companyMentor"/></el-form-item>
        <el-form-item label="指导教师"><el-input v-model="form.instructor"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="show=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createOrderClass, getOrderClassList, updateOrderClass } from '@/api/cooperation'

const props = defineProps({ projectOptions: { type: Array, default: () => [] } })
const emit = defineEmits(['changed'])
const loading = ref(false); const list = ref([]); const show = ref(false)
const init = () => ({ id: null, projectId: null, className: '', major: '', grade: '', plannedCount: 0, companyMentor: '', instructor: '' })
const form = reactive(init())

const load = async () => {
  loading.value = true
  try { const res = await getOrderClassList(); if (res.code === 0) list.value = res.data || [] } finally { loading.value = false }
}
const open = (row) => { Object.assign(form, init(), row || {}); show.value = true }
const save = async () => {
  const res = form.id ? await updateOrderClass(form.id, form) : await createOrderClass(form.projectId, form)
  if (res.code === 0) { ElMessage.success('保存成功'); show.value = false; load(); emit('changed') }
}
onMounted(load)
</script>

<style scoped>.bar{display:flex;justify-content:flex-end;margin-bottom:12px}</style>
