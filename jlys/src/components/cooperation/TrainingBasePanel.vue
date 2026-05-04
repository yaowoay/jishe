<template>
  <div>
    <div class="bar"><el-button type="primary" @click="open()">新建基地</el-button></div>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="baseName" label="基地名称" min-width="160"/>
      <el-table-column prop="projectName" label="所属项目" min-width="160"/>
      <el-table-column prop="baseAddress" label="地址" min-width="220"/>
      <el-table-column prop="capacity" label="容纳人数" width="100"/>
      <el-table-column prop="constructionStatus" label="建设状态" width="120"/>
      <el-table-column label="操作" width="120"><template #default="{row}"><el-button link @click="open(row)">编辑</el-button></template></el-table-column>
    </el-table>

    <el-dialog v-model="show" :title="form.id?'编辑实训基地':'新建实训基地'" width="620px">
      <el-form :model="form" label-width="98px">
        <el-form-item label="所属项目">
          <el-select v-model="form.projectId"><el-option v-for="p in projectOptions" :key="p.id" :label="p.projectName" :value="p.id"/></el-select>
        </el-form-item>
        <el-form-item label="基地名称"><el-input v-model="form.baseName"/></el-form-item>
        <el-form-item label="基地地址"><el-input v-model="form.baseAddress"/></el-form-item>
        <el-form-item label="基地面积"><el-input-number v-model="form.baseArea" :min="0"/></el-form-item>
        <el-form-item label="设备价值"><el-input-number v-model="form.equipmentValue" :min="0"/></el-form-item>
        <el-form-item label="容纳人数"><el-input-number v-model="form.capacity" :min="0"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="show=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createTrainingBase, getTrainingBaseList, updateTrainingBase } from '@/api/cooperation'

const props = defineProps({ projectOptions: { type: Array, default: () => [] } })
const emit = defineEmits(['changed'])
const loading = ref(false); const list = ref([]); const show = ref(false)
const init = () => ({ id: null, projectId: null, baseName: '', baseAddress: '', baseArea: 0, equipmentValue: 0, capacity: 0 })
const form = reactive(init())

const load = async () => {
  loading.value = true
  try { const res = await getTrainingBaseList(); if (res.code === 0) list.value = res.data || [] } finally { loading.value = false }
}
const open = (row) => { Object.assign(form, init(), row || {}); show.value = true }
const save = async () => {
  const res = form.id ? await updateTrainingBase(form.id, form) : await createTrainingBase(form.projectId, form)
  if (res.code === 0) { ElMessage.success('保存成功'); show.value = false; load(); emit('changed') }
}
onMounted(load)
</script>

<style scoped>.bar{display:flex;justify-content:flex-end;margin-bottom:12px}</style>
