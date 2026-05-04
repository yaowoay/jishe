<template>
  <div class="resume-others">
   

    <!-- 证书认证 -->
    <div class="info-section" v-if="othersData.certificates?.length > 0">
      <h4>证书认证</h4>
      <div class="info-list">
        <div 
          v-for="(cert, index) in othersData.certificates" 
          :key="index"
          class="info-item"
        >
          <div class="item-content">
            <strong>{{ cert.name }}</strong>
            <span class="issuer">{{ cert.issuer }}</span>
            <span class="date">{{ cert.date }}</span>
          </div>
          <el-button 
            size="small" 
            type="danger" 
            text 
            @click="removeCertificate(index)"
          >
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 获奖经历 -->
    <div class="info-section" v-if="othersData.awards?.length > 0">
      <h4>获奖经历</h4>
      <div class="info-list">
        <div 
          v-for="(award, index) in othersData.awards" 
          :key="index"
          class="info-item"
        >
          <div class="item-content">
            <strong>{{ award.name }}</strong>
            <span class="level">{{ award.level }}</span>
            <span class="date">{{ award.date }}</span>
          </div>
          <el-button 
            size="small" 
            type="danger" 
            text 
            @click="removeAward(index)"
          >
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 兴趣爱好 -->
    <div class="info-section" v-if="othersData.hobbies?.length > 0">
      <h4>兴趣爱好</h4>
      <div class="hobbies-list">
        <el-tag
          v-for="(hobby, index) in othersData.hobbies"
          :key="index"
          closable
          @close="removeHobby(index)"
          class="hobby-tag"
        >
          {{ hobby }}
        </el-tag>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="isEmpty" class="empty-state">
      <el-empty description="还没有添加其他信息">
        <el-button type="primary" @click="showAddDialog = true">添加信息</el-button>
      </el-empty>
    </div>

    <!-- 添加信息弹窗 -->
    <el-dialog
      v-model="showAddDialog"
      title="添加其他信息"
      width="500px"
    >
      <el-form :model="newItem" label-width="80px">
        <el-form-item label="类型" required>
          <el-select v-model="newItem.type" placeholder="选择类型" style="height: 48px;">
            <el-option label="证书认证" value="certificate" />
            <el-option label="获奖经历" value="award" />
            <el-option label="兴趣爱好" value="hobby" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <template #label>
            <span style="color: red;">*</span>名称
          </template>
          <el-input v-model="newItem.name" placeholder="请输入名称" style="height: 48px;" />
        </el-form-item>

        <el-form-item v-if="newItem.type === 'certificate'" label="颁发机构">
          <el-input v-model="newItem.issuer" placeholder="请输入颁发机构，没有则填无" style="height: 48px;" />
        </el-form-item>

        <el-form-item v-if="newItem.type === 'award'" label="级别">
          <el-select v-model="newItem.level" placeholder="选择级别" style="height: 48px;">
            <el-option label="国家级" value="国家级" />
            <el-option label="省级" value="省级" />
            <el-option label="公司级" value="公司级" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item v-if="newItem.type !== 'hobby'" label="时间">
          <el-date-picker
            v-model="newItem.date"
            type="month"
            placeholder="选择时间，没有则不填"
            format="YYYY-MM"
            value-format="YYYY-MM"
            style="height: 48px;"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmAdd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'

export default {
  name: 'ResumeOthers',
  components: {
    Plus,
    Delete
  },
  props: {
    modelValue: {
      type: Object,
      default: () => ({
        certificates: [],
        awards: [],
        hobbies: []
      })
    }
  },
  emits: ['update:modelValue', 'change'],
  setup(props, { emit }) {
    const othersData = reactive({ ...props.modelValue })
    const showAddDialog = ref(false)
    const newItem = reactive({
      type: '',
      name: '',
      issuer: '',
      level: '',
      date: ''
    })

    // 监听props变化
    watch(() => props.modelValue, (newVal) => {
      Object.assign(othersData, newVal)
    }, { deep: true })

    // 检查是否为空
    const isEmpty = computed(() => {
      return (!othersData.certificates || othersData.certificates.length === 0) &&
             (!othersData.awards || othersData.awards.length === 0) &&
             (!othersData.hobbies || othersData.hobbies.length === 0)
    })

    // 处理数据变化
    const handleChange = () => {
      emit('update:modelValue', { ...othersData })
      emit('change')
    }

    // 确认添加
    const confirmAdd = () => {
      if (!newItem.type || !newItem.name) {
        ElMessage.warning('请填写完整信息')
        return
      }

      if (newItem.type === 'certificate') {
        if (!othersData.certificates) othersData.certificates = []
        othersData.certificates.push({
          name: newItem.name,
          issuer: newItem.issuer,
          date: newItem.date
        })
      } else if (newItem.type === 'award') {
        if (!othersData.awards) othersData.awards = []
        othersData.awards.push({
          name: newItem.name,
          level: newItem.level,
          date: newItem.date
        })
      } else if (newItem.type === 'hobby') {
        if (!othersData.hobbies) othersData.hobbies = []
        if (!othersData.hobbies.includes(newItem.name)) {
          othersData.hobbies.push(newItem.name)
        }
      }

      // 重置表单
      Object.assign(newItem, {
        type: '',
        name: '',
        issuer: '',
        level: '',
        date: ''
      })

      handleChange()
      showAddDialog.value = false
    }

    // 删除证书
    const removeCertificate = (index) => {
      othersData.certificates.splice(index, 1)
      handleChange()
    }

    // 删除奖项
    const removeAward = (index) => {
      othersData.awards.splice(index, 1)
      handleChange()
    }

    // 删除爱好
    const removeHobby = (index) => {
      othersData.hobbies.splice(index, 1)
      handleChange()
    }

    return {
      othersData,
      showAddDialog,
      newItem,
      isEmpty,
      confirmAdd,
      removeCertificate,
      removeAward,
      removeHobby
    }
  }
}
</script>

<style scoped>
/* eslint-disable */
.resume-others {
  font-size: 15px;
}
.section-header {
  font-size: 16px;
  margin-bottom: 20px;
}
.section-header h3 {
  font-size: 18px;
}
.el-form-item__label {
  font-size: 14px !important;
}
.el-input, .el-select, .el-date-picker, .el-textarea {
  font-size: 14px !important;
  height: 32px !important;
  min-height: 32px !important;
}
.el-input__inner, .el-textarea__inner {
  font-size: 14px !important;
  height: 32px !important;
  min-height: 32px !important;
}
.el-button, .el-button--primary {
  font-size: 14px !important;
  height: 32px !important;
  padding: 0 16px !important;
}
/* 迁移自frontend，将scss语法改为css - 其他信息组件样式 */
.resume-others .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.resume-others .section-header h3 {
  margin: 0;
  color: var(--el-text-color-primary);
}

.resume-others .info-section {
  margin-bottom: 24px;
}

.resume-others .info-section h4 {
  margin: 0 0 12px 0;
  color: var(--el-text-color-primary);
  font-size: 16px;
  font-weight: 600;
}

.resume-others .info-section .info-list .info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 6px;
  margin-bottom: 8px;
  border: 1px solid #e9ecef;
  /* min-height: 0; 移除，避免高度被限制 */
  height: auto;
  box-sizing: border-box;
  overflow: visible; /* 保证内容不会被裁剪 */
}

.resume-others .info-section .info-list .info-item .item-content {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
}

.resume-others .info-section .info-list .info-item .item-content strong {
  color: var(--el-text-color-primary);
}

.resume-others .info-section .info-list .info-item .item-content .issuer,
.resume-others .info-section .info-list .info-item .item-content .level {
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.resume-others .info-section .info-list .info-item .item-content .date {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.resume-others .info-section .hobbies-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.resume-others .info-section .hobbies-list .hobby-tag {
  margin: 0;
}

.resume-others .empty-state {
  text-align: center;
  padding: 40px 0;
}
</style>
