<template>
  <div class="example-container">
    <h1>简历分析报告组件示例</h1>
    
    <!-- 示例按钮 -->
    <div class="example-buttons">
      <button @click="loadSampleData" class="sample-btn">
        加载示例数据
      </button>
      <button @click="clearData" class="clear-btn">
        清空数据
      </button>
    </div>
    
    <!-- 分析报告组件 -->
    <ResumeAnalysisReport 
      v-if="sampleData" 
      :analysis-data="sampleData" 
    />
    
    <!-- 数据预览 -->
    <div v-if="showDataPreview" class="data-preview">
      <h3>数据结构预览</h3>
      <pre>{{ JSON.stringify(sampleData, null, 2) }}</pre>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import ResumeAnalysisReport from './ResumeAnalysisReport.vue'

export default {
  name: 'ResumeAnalysisReportExample',
  components: {
    ResumeAnalysisReport
  },
  setup() {
    const sampleData = ref(null)
    const showDataPreview = ref(false)
    
    const loadSampleData = () => {
      sampleData.value = {
        upload: {
          extension: 'pdf',
          size: 694665,
          mime_type: 'application/pdf',
          name: '简历.pdf',
          created_at: 1754489189,
          id: '7ec6fa67-4845-4586-a575-ee6ca3b2af78',
          created_by: '999aded4-f076-4dc9-b756-8303a0150976'
        },
        analysis: {
          data: {
            outputs: {
              end_eponing: {
                skills: {
                  matchScore: 80,
                  technicalHighlights: [
                    '熟悉行政管理流程',
                    '优秀的口头和书面沟通能力',
                    '熟练使用办公自动化设备',
                    '能够灵活应对突发情况',
                    '具备一定的英语听说能力'
                  ],
                  expressionOptimization: '建议将\'熟练使用办公自动化设备\'具体化为\'熟练使用Microsoft Office套件（Word、Excel、PowerPoint）\'。',
                  skillGaps: [
                    '英语能力可能不足以应对高级接待需求',
                    '未提及具体办公软件名称（如Excel、Word等）'
                  ],
                  originalContent: '熟悉⾏政管理流程，具备基础的⼈事管理知识；\n优秀的书⾯和⼝头沟通能⼒，熟练使⽤办公⾃动化设备；\n能够灵活应对突发情况，保持良好⼼态和专业态度；\n具备⼀定的英语听说能⼒，可进⾏基本的英⽂接待。'
                },
                educationBackground: {
                  matchScore: 90,
                  enhancementDirections: '建议补充在校期间的任何相关课程或项目经验（如\'在校期间曾参与组织XX活动\'）。',
                  educationAdvantages: [
                    '专业对口（行政管理专业）',
                    '学历与目标职位要求匹配'
                  ],
                  originalContent: '20XX年 - 20XX年 可⽡学院 ⾏政管理专业'
                },
                personalInfo: {
                  matchScore: 85,
                  risks: [
                    '英语能力仅限基本英文接待',
                    '缺乏具体案例支持自我评价'
                  ],
                  strengths: [
                    '热情友善，具备良好的沟通和服务能力',
                    '细致入微，注重细节',
                    '良好的时间管理和组织能力',
                    '良好的团队合作精神',
                    '经验丰富，能够仔细审查和处理文件和信息',
                    '善于与他人合作，积极贡献',
                    '良好的自我管理能力'
                  ],
                  optimizationSuggestions: '建议提供具体案例或成就来支持自我评价，如\'在某次公司活动中成功接待了XX位客人，获得了XX好评\'。',
                  originalContent: '李⼩画 求职意向：⾏政前台\n1. 热情友善，具备良好的沟通和服务能⼒，能够给予客⼈良好的体验；\n2. 细致⼊微，注重细节，能够⾼效地处理⽇常事务和客⼾需求；\n3. 能够处理多任务，具备良好的时间管理和组织能⼒；\n4. 良好的团队合作精神，能够与同事紧密合作，共同完成⼯作任务。\n优势亮点\n⾃我评价\n1. 经验丰富：有丰富的知识体系做基础，能够仔细审查和处理⽂件和信息，确保准确性和完整性；\n2. 团队合作：善于与他⼈合作，能够在团队中积极贡献并共同实现⽬标；\n3. ⾃我管理：具备良好的时间管理和⾃我组织能⼒，能够有效管理⼯作任务和⼯作压⼒。'
                },
                targetPosition: {
                  name: '行政前台'
                },
                projectExperience: {
                  matchScore: 75,
                  restructuringSuggestions: '建议使用STAR原则重构项目经验，例如：\'在可瓦科技公司期间（情境），负责日常访客接待（任务），通过优化接待流程（行动），使得访客平均等待时间减少了XX%（结果）。\'',
                  projectHighlights: [
                    '负责公司日常访客接待、电话转接、邮件收发等工作',
                    '协助HR部门完成面试邀约、会议室预定及新员工入职资料准备',
                    '负责办公室日常用品采购与库存管理',
                    '参与公司各类活动的筹备与执行',
                    '掌握了人事行政相关工作流程',
                    '协助处理员工考勤记录与统计',
                    '文件归档、打印复印及办公设备的日常维护'
                  ],
                  originalContent: '20XX年XX⽉ - ⾄今 可⽡科技公司 ⾏政前台\n负责公司⽇常访客接待、电话转接、邮件收发等⼯作，确保前台区域整洁有序；\n协助HR部⻔完成⾯试邀约、会议室预定以及新员⼯⼊职资料准备；\n负责办公室⽇常⽤品采购与库存管理，确保办公环境顺畅运⾏；\n参与公司各类活动的筹备与执⾏。\n实习经历\n20XX年XX⽉ - 20XX年XX⽉ 可⽡信息公司 ⾏政助理\n掌握了⼈事⾏政相关⼯作流程，协助处理员⼯考勤记录与统计；\n与公司⽂件归档、打印复印以及办公设备的⽇常维护。',
                  improvementAreas: [
                    '缺乏具体量化成果（如\'接待了多少访客\'或\'提高了多少效率\'）',
                    '未提及具体参与的活动的规模和影响'
                  ]
                }
              }
            },
            workflow_id: '0fc805ba-0501-47f6-9546-8f85c0d61e38',
            finished_at: 1754489208,
            elapsed_time: 18.984512,
            total_tokens: 2775,
            total_steps: 4,
            created_at: 1754489189,
            id: 'e86c14f2-c0d3-448f-a8fe-fe8f46f89171',
            error: '',
            status: 'succeeded'
          },
          task_id: '0838330b-066f-4272-bd68-43a640cd89f0',
          workflow_run_id: 'e86c14f2-c0d3-448f-a8fe-fe8f46f89171'
        }
      }
    }
    
    const clearData = () => {
      sampleData.value = null
    }
    
    const toggleDataPreview = () => {
      showDataPreview.value = !showDataPreview.value
    }
    
    return {
      sampleData,
      showDataPreview,
      loadSampleData,
      clearData,
      toggleDataPreview
    }
  }
}
</script>

<style scoped>
.example-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.example-buttons {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  justify-content: center;
}

.sample-btn, .clear-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sample-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.sample-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.clear-btn {
  background: #f7fafc;
  color: #4a5568;
  border: 1px solid #e2e8f0;
}

.clear-btn:hover {
  background: #edf2f7;
}

.data-preview {
  margin-top: 30px;
  background: #f7fafc;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.data-preview h3 {
  margin: 0 0 15px 0;
  color: #2d3748;
}

.data-preview pre {
  background: white;
  padding: 15px;
  border-radius: 8px;
  overflow-x: auto;
  font-size: 12px;
  line-height: 1.4;
  color: #4a5568;
}
</style>
