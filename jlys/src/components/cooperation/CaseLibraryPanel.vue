<template>
  <div>
    <div class="bar">
      <el-select v-model="q.cooperationType" clearable placeholder="合作类型" @change="load"><el-option label="实训基地" value="training_base"/><el-option label="订单班" value="order_class"/><el-option label="实习基地" value="internship"/><el-option label="产学研" value="research"/></el-select>
      <el-checkbox v-model="q.isFeatured" @change="load">仅精选</el-checkbox>
    </div>
    <div class="grid">
      <el-card v-for="item in list" :key="item.id" class="case" shadow="hover" @click="detail(item.id)">
        <template #header><div class="head"><b>{{ item.caseTitle }}</b><el-tag v-if="item.isFeatured" type="warning" size="small">精选</el-tag></div></template>
        <p>{{ item.companyName }} · {{ item.cooperationDuration }}</p>
        <p class="sum">{{ item.caseSummary }}</p>
        <div class="foot"><el-tag size="small">{{ item.cooperationType }}</el-tag><span>👁 {{ item.viewCount || 0 }}</span></div>
      </el-card>
    </div>
    <el-pagination v-model:current-page="p.page" v-model:page-size="p.size" :total="p.total" layout="prev, pager, next" @current-change="load" class="pager"/>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import { getCaseDetail, getCaseList } from '@/api/cooperation'

const q = reactive({ cooperationType: '', isFeatured: false })
const p = reactive({ page: 1, size: 8, total: 0 })
const list = ref([])

const load = async () => {
  const res = await getCaseList({ page: p.page, size: p.size, cooperationType: q.cooperationType || undefined, isFeatured: q.isFeatured || undefined })
  if (res.code === 0) { list.value = res.data.records || []; p.total = res.data.total || 0 }
}
const detail = async (id) => {
  const res = await getCaseDetail(id)
  if (res.code === 0) {
    const x = res.data
    ElMessageBox.alert(`<p><b>企业：</b>${x.companyName}</p><p><b>摘要：</b>${x.caseSummary || '-'}</p><p><b>成功要素：</b>${x.successFactors || '-'}</p>`, x.caseTitle, { dangerouslyUseHTMLString: true })
  }
}
onMounted(load)
</script>

<style scoped>
.bar{display:flex;gap:10px;align-items:center;margin-bottom:12px}.grid{display:grid;gap:14px;grid-template-columns:repeat(auto-fill,minmax(280px,1fr))}
.case{cursor:pointer}.head{display:flex;justify-content:space-between;align-items:center}.sum{color:#64748b;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden}
.foot{display:flex;justify-content:space-between;align-items:center}.pager{margin-top:12px;display:flex;justify-content:center}
</style>
