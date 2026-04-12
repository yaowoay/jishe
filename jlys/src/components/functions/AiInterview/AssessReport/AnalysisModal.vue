<!--<template>-->
<!--  <div v-if="visible" class="modal-backdrop" @click.self="closeModal">-->
<!--    <div class="modal-content" @click.stop>-->
<!--      <div class="modal-header">-->
<!--        <h3>{{ title }}</h3>-->
<!--        <button class="close-btn" @click="closeModal">&times;</button>-->
<!--      </div>-->
<!--      <div class="modal-body" v-html="content"></div>-->
<!--      <div class="modal-footer">-->
<!--        <button class="btn confirm-btn" @click="confirm">确认</button>-->
<!--        <button class="btn cancel-btn" @click="closeModal">取消</button>-->
<!--      </div>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import { ref, watch } from 'vue'-->

<!--export default {-->
<!--  name: 'AnalysisModal',-->
<!--  props: {-->
<!--    modelValue: {-->
<!--      type: Boolean,-->
<!--      default: false-->
<!--    },-->
<!--    title: {-->
<!--      type: String,-->
<!--      default: '分析详情'-->
<!--    },-->
<!--    content: {-->
<!--      type: String,-->
<!--      default: ''-->
<!--    }-->
<!--  },-->
<!--  emits: ['update:modelValue', 'confirm'],-->
<!--  setup(props, { emit }) {-->
<!--    const visible = ref(props.modelValue)-->

<!--    watch(() => props.modelValue, (newVal) => {-->
<!--      visible.value = newVal-->
<!--    })-->

<!--    const closeModal = () => {-->
<!--      visible.value = false-->
<!--      emit('update:modelValue', false)-->
<!--    }-->

<!--    const confirm = () => {-->
<!--      emit('confirm')-->
<!--      closeModal()-->
<!--    }-->

<!--    return {-->
<!--      visible,-->
<!--      closeModal,-->
<!--      confirm-->
<!--    }-->
<!--  }-->
<!--}-->
<!--</script>-->

<!--<style scoped>-->
<!--.modal-backdrop {-->
<!--  position: fixed;-->
<!--  top: 0;-->
<!--  left: 0;-->
<!--  width: 100%;-->
<!--  height: 100%;-->
<!--  background-color: rgba(0, 0, 0, 0.5);-->
<!--  display: flex;-->
<!--  justify-content: center;-->
<!--  align-items: center;-->
<!--  z-index: 1000;-->
<!--}-->

<!--.modal-content {-->
<!--  background-color: white;-->
<!--  border-radius: 8px;-->
<!--  width: 90%;-->
<!--  max-width: 600px;-->
<!--  max-height: 80vh;-->
<!--  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);-->
<!--  overflow: hidden;-->
<!--  display: flex;-->
<!--  flex-direction: column;-->
<!--}-->

<!--.modal-header {-->
<!--  padding: 15px 20px;-->
<!--  background-color: #f8f8f8;-->
<!--  display: flex;-->
<!--  justify-content: space-between;-->
<!--  align-items: center;-->
<!--  border-bottom: 1px solid #eee;-->
<!--}-->

<!--.modal-header h3 {-->
<!--  margin: 0;-->
<!--  font-size: 18px;-->
<!--  color: #333;-->
<!--}-->

<!--.close-btn {-->
<!--  background: none;-->
<!--  border: none;-->
<!--  font-size: 24px;-->
<!--  cursor: pointer;-->
<!--  color: #666;-->
<!--}-->

<!--.modal-body {-->
<!--  padding: 20px;-->
<!--  overflow-y: auto;-->
<!--  max-height: calc(80vh - 120px);-->
<!--}-->

<!--.modal-footer {-->
<!--  padding: 15px 20px;-->
<!--  display: flex;-->
<!--  justify-content: flex-end;-->
<!--  gap: 10px;-->
<!--  border-top: 1px solid #eee;-->
<!--}-->

<!--.btn {-->
<!--  padding: 8px 16px;-->
<!--  border-radius: 4px;-->
<!--  cursor: pointer;-->
<!--  font-size: 14px;-->
<!--  transition: all 0.2s;-->
<!--}-->

<!--.confirm-btn {-->
<!--  background-color: #1E90FF;-->
<!--  color: white;-->
<!--  border: none;-->
<!--}-->

<!--.confirm-btn:hover {-->
<!--  background-color: #187bcd;-->
<!--}-->

<!--.cancel-btn {-->
<!--  background-color: #f0f0f0;-->
<!--  color: #666;-->
<!--  border: 1px solid #ddd;-->
<!--}-->

<!--.cancel-btn:hover {-->
<!--  background-color: #e0e0e0;-->
<!--}-->

<!--@media (prefers-color-scheme: dark) {-->
<!--  .modal-content {-->
<!--    background-color: #333;-->
<!--    color: #fff;-->
<!--  }-->

<!--  .modal-header {-->
<!--    background-color: #444;-->
<!--    border-color: #555;-->
<!--  }-->

<!--  .modal-header h3 {-->
<!--    color: #fff;-->
<!--  }-->

<!--  .close-btn {-->
<!--    color: #ccc;-->
<!--  }-->

<!--  .modal-footer {-->
<!--    border-color: #555;-->
<!--  }-->

<!--  .confirm-btn {-->
<!--    background-color: #409EFF;-->
<!--  }-->

<!--  .cancel-btn {-->
<!--    background-color: #444;-->
<!--    color: #ddd;-->
<!--    border-color: #555;-->
<!--  }-->

<!--  .cancel-btn:hover {-->
<!--    background-color: #555;-->
<!--  }-->
<!--}-->
<!--</style>-->


<template>
  <div v-if="modelValue" class="modal-backdrop" @click.self="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>{{ detailData.title || '分析详情' }}</h3>
        <button class="close-btn" @click="closeModal">&times;</button>
      </div>

      <div class="modal-body">
        <div v-if="detailData && detailData.details" v-html="detailData.details"></div>
        <div v-else>
          <p>正在加载详情内容...</p>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn confirm-btn" @click="confirm">确认</button>
        <button class="btn cancel-btn" @click="closeModal">取消</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AnalysisModal',
  props: {
    // v-model 会自动处理这个 prop
    modelValue: {
      type: Boolean,
      default: false
    },
    // 接收一个详情数据对象
    detailData: {
      type: Object,
      default: () => ({})
    }
  },
  emits: ['update:modelValue', 'confirm'],
  setup(props, { emit }) {
    const closeModal = () => {
      emit('update:modelValue', false)
    }

    const confirm = () => {
      emit('confirm')
      closeModal()
    }

    return {
      closeModal,
      confirm
    }
  }
}
</script>

<style scoped>
/* 弹窗的主体样式 */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 700px; /* 可以适当加宽以容纳更多内容 */
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
}
.modal-header {
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
}
.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}
.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
}
.modal-body {
  padding: 20px 30px; /* 增加内边距 */
  overflow-y: auto;
  max-height: 70vh; /* 限制最大高度 */
}
.modal-footer {
  padding: 15px 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  border-top: 1px solid #eee;
}
.btn {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}
.confirm-btn { background-color: #1E90FF; color: white; border: none; }
.cancel-btn { background-color: #f0f0f0; color: #666; border: 1px solid #ddd; }

/* 详情内容的样式 */
.content-section, .recommendations {
  margin-bottom: 25px;
}
.content-section:last-child, .recommendations:last-child {
  margin-bottom: 0;
}
.content-section h3, .recommendations h3 {
  color: #333;
  margin-bottom: 15px;
  font-size: 16px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 8px;
}
.detail-text, li {
  line-height: 1.7;
  color: #555;
}
ul {
  padding-left: 20px;
  list-style-type: disc;
}
li {
  margin-bottom: 8px;
}

/* 暗黑模式样式 */
@media (prefers-color-scheme: dark) {
  .modal-content {
    background-color: #2c2c2c;
  }

  .modal-header {
    background-color: #444;
    border-color: #555;
  }

  .modal-header h3 {
    color: #fff;
  }

  .close-btn {
    color: #ccc;
  }

  .modal-footer {
    border-color: #555;
  }

  .confirm-btn {
    background-color: #409EFF;
  }

  .cancel-btn {
    background-color: #444;
    color: #ddd;
    border-color: #555;
  }

  .cancel-btn:hover {
    background-color: #555;
  }
}
</style>