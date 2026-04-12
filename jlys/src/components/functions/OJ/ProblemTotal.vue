<template>
  <div class="oj-system">
    <el-container>
      <el-main class="main">
        <template v-if="!showDetail">
          <!-- 统计卡片区 -->
          <div class="stats-row">
            <el-row :gutter="30">
              <el-col :span="8">
                <el-card class="stat-card" shadow="hover">
                  <div class="stat-icon stat-blue"><el-icon><i class="el-icon-sort"></i></el-icon></div>
                  <div class="stat-info">
                    <div class="stat-value">{{ stats.total }}</div>
                    <div class="stat-label">题目总数</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="stat-card" shadow="hover">
                  <div class="stat-icon stat-green"><el-icon><i class="el-icon-trophy"></i></el-icon></div>
                  <div class="stat-info">
                    <div class="stat-value">{{ stats.attempted }}</div>
                    <div class="stat-label">已尝试</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="stat-card" shadow="hover">
                  <div class="stat-icon stat-yellow"><el-icon><i class="el-icon-filter"></i></el-icon></div>
                  <div class="stat-info">
                    <div class="stat-value">{{ stats.passed }}</div>
                    <div class="stat-label">已通过</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
          <!-- 我的收藏/全部题目切换按钮 -->
          <div class="favorite-btn-row">
            <el-radio-group v-model="showFavorite" size="large">
              <el-radio-button :label="false">全部题目</el-radio-button>
              <el-radio-button :label="true">我的收藏</el-radio-button>
            </el-radio-group>
          </div>
          <!-- 题目表格或收藏表格 -->
          <el-card class="table-card" shadow="never">
            <template v-if="!showFavorite">
              <el-table
                :data="filteredProblems"
                row-key="id"
                style="width: 100%"
                border
                stripe
                highlight-current-row
                @row-click="handleRowClick"
                empty-text="暂无题目"
                v-loading="loading"
              >
                <el-table-column prop="id" label="题目ID" width="100" />
                <el-table-column prop="title" label="题目">
                  <template #default="{ row }">
                    <el-tag v-if="row.isFavorite" type="warning" effect="plain" size="small" style="margin-right:8px;">已收藏</el-tag>
                    <span>{{ row.title }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="我的状态" width="120">
                  <template #default="{ row }">
                    <span v-if="row.status === '未尝试'" style="color:#909399;">未尝试</span>
                    <span v-else-if="row.status === '通过'" style="color:#67C23A;font-size:20px;">✔</span>
                    <span v-else style="color:#f56c6c;font-size:20px;">❌</span>
                  </template>
                </el-table-column>
                <el-table-column label="收藏" width="100">
                  <template #default="{ row }">
                    <el-button
                      :icon="row.isFavorite ? 'el-icon-star-on' : 'el-icon-star-off'"
                      :type="row.isFavorite ? 'warning' : 'info'"
                      circle
                      size="small"
                      @click.stop="toggleFavorite(row)"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </template>
            <template v-else>
              <el-table
                :data="favoriteProblems"
                row-key="id"
                style="width: 100%"
                border
                stripe
                highlight-current-row
                @row-click="handleRowClick"
                empty-text="暂无收藏题目"
                v-loading="loading"
              >
                <el-table-column prop="id" label="题目ID" width="100" />
                <el-table-column prop="title" label="题目" />
                <el-table-column label="我的状态" width="120">
                  <template #default="{ row }">
                    <span v-if="row.status === '未尝试'" style="color:#909399;">未尝试</span>
                    <span v-else-if="row.status === '通过'" style="color:#67C23A;font-size:20px;">✔</span>
                    <span v-else style="color:#f56c6c;font-size:20px;">❌</span>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="favoriteProblems.length === 0" description="暂无收藏题目" />
            </template>
          </el-card>
          <el-pagination
            v-if="!showFavorite"
            background
            layout="prev, pager, next"
            :total="problems.length"
            :page-size="10"
            @current-change="handlePageChange"
          />
        </template>
        <template v-else>
          <ProblemDetail
            :problem="currentProblem"
            @back="showDetail = false"
            @prev="handlePrev"
            @next="handleNext"
            :prev-disabled="currentIndex === 0"
            :next-disabled="currentIndex === problems.length - 1"
            @refresh="handleRefresh"
          />
        </template>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'
import ProblemDetail from './ProblemDetail.vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'OjView',
  components: { ProblemDetail },
  setup() {
    const problems = ref([])
    const showFavorite = ref(false)
    const showDetail = ref(false)
    const currentProblem = ref(null)
    const currentIndex = ref(0)
    const loading = ref(false)

    // 调用后端接口获取题目列表
    const fetchProblems = async () => {
      loading.value = true
      try {
        const response = await request.get('/problems')
        if (response.data && Array.isArray(response.data)) {
          problems.value = response.data
        } else {
          // 如果后端接口还没实现，使用模拟数据`
          problems.value = [
            {
              id: 1,
              title: '两数之和',
              isFavorite: false,
              status: '通过',
              description: '给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那两个整数，并返回他们的数组下标。',
              examples: [
                { input: 'nums = [2,7,11,15], target = 9', output: '[0,1]' }
              ]
            },
            {
              id: 2,
              title: '无重复字符的最长子串',
              isFavorite: true,
              status: '未尝试',
              description: '给定一个字符串 s ，请你找出其中不含有重复字符的 最长子串 的长度。',
              examples: [
                { input: 's = "abcabcbb"', output: '3' }
              ]
            }
          ]
        }
      } catch (error) {
        console.error('获取题目列表失败:', error)
        ElMessage.error('获取题目列表失败')
        // 使用模拟数据
        problems.value = [
          {
            id: 1,
            title: '两数之和',
            isFavorite: false,
            status: '通过',
            description: '给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那两个整数，并返回他们的数组下标。',
            examples: [
              { input: 'nums = [2,7,11,15], target = 9', output: '[0,1]' }
            ]
          },
          {
            id: 2,
            title: '无重复字符的最长子串',
            isFavorite: true,
            status: '未尝试',
            description: '给定一个字符串 s ，请你找出其中不含有重复字符的 最长子串 的长度。',
            examples: [
              { input: 's = "abcabcbb"', output: '3' }
            ]
          }
        ]
      } finally {
        loading.value = false
      }
    }

    onMounted(fetchProblems)

    // 统计信息
    const stats = computed(() => {
      const total = problems.value.length
      const attempted = problems.value.filter(p => p.status !== '未尝试').length
      const passed = problems.value.filter(p => p.status === '通过').length
      return { total, attempted, passed }
    })

    // 收藏题目列表
    const favoriteProblems = computed(() => problems.value.filter(p => p.isFavorite))

    // 过滤后的题目列表（用于分页）
    const filteredProblems = computed(() => problems.value)

    const toggleFavorite = async (problem) => {
      try {
        const newFavoriteStatus = !problem.isFavorite
        if (newFavoriteStatus) {
          await request.post(`/problems/${problem.id}/favorite`)
        } else {
          await request.post(`/problems/${problem.id}/unfavorite`)
        }
        // 立即本地变色，提升体验
        problem.isFavorite = newFavoriteStatus
        ElMessage.success(newFavoriteStatus ? '收藏成功' : '取消收藏成功')
      } catch (error) {
        console.error('Error toggling favorite:', error)
        ElMessage.error('操作失败，请重试')
      }
    }

    const handlePageChange = (page) => {
      // 分页逻辑，可以根据需要实现
      console.log('当前页:', page)
    }

    // 行点击显示题目详情
    const handleRowClick = async (row) => {
      const idx = problems.value.findIndex(p => p.id === row.id)
      if (idx !== -1) {
        currentIndex.value = idx
        // 获取题目详细信息（包括示例）
        try {
          const response = await request.get(`/problems/${row.id}`)
          if (response.data) {
            currentProblem.value = response.data
          } else {
            currentProblem.value = problems.value[idx]
          }
        } catch (error) {
          console.error('获取题目详情失败:', error)
          currentProblem.value = problems.value[idx]
        }
        showDetail.value = true
      }
    }

    // 上一题
    const handlePrev = async () => {
      if (currentIndex.value > 0) {
        currentIndex.value--
        const problem = problems.value[currentIndex.value]
        try {
          const response = await request.get(`/problems/${problem.id}`)
          if (response.data) {
            currentProblem.value = response.data
          } else {
            currentProblem.value = problem
          }
        } catch (error) {
          currentProblem.value = problem
        }
      }
    }

    // 下一题
    const handleNext = async () => {
      if (currentIndex.value < problems.value.length - 1) {
        currentIndex.value++
        const problem = problems.value[currentIndex.value]
        try {
          const response = await request.get(`/problems/${problem.id}`)
          if (response.data) {
            currentProblem.value = response.data
          } else {
            currentProblem.value = problem
          }
        } catch (error) {
          currentProblem.value = problem
        }
      }
    }

    // 刷新题目状态（如通过/未通过、收藏等）
    const handleRefresh = async () => {
      await fetchProblems()
      // 重新获取当前题目详情，保证 currentProblem 也刷新
      if (currentProblem.value && currentProblem.value.id) {
        try {
          const response = await request.get(`/problems/${currentProblem.value.id}`)
          if (response.data) {
            currentProblem.value = response.data
          }
        } catch (error) {
          // 忽略
        }
      }
    }

    return {
      problems,
      stats,
      filteredProblems,
      favoriteProblems,
      toggleFavorite,
      handlePageChange,
      showFavorite,
      handleRowClick,
      showDetail,
      currentProblem,
      currentIndex,
      handlePrev,
      handleNext,
      loading,
      handleRefresh
    }
  }
}
</script>

<style scoped>
.oj-system {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #eaf4ff 100%);
}

.main {
  flex: 1;
  background: transparent;
  overflow-y: auto;
  padding: 40px 5vw 32px 5vw;
  min-height: 100vh;
}

.stats-row {
  margin-bottom: 36px;
}
.stat-card {
  display: flex;
  align-items: center;
  background: linear-gradient(90deg, #eaf4ff 60%, #f5f7fa 100%);
  border-radius: 18px;
  padding: 32px 38px;
  color: #222;
  border: none;
  box-shadow: 0 4px 24px rgba(91,188,255,0.10);
  transition: box-shadow 0.3s, transform 0.3s;
}
.stat-card:hover {
  box-shadow: 0 10px 32px rgba(91,188,255,0.18);
  transform: translateY(-4px) scale(1.03);
}
.stat-icon {
  font-size: 38px;
  margin-right: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  border-radius: 16px;
  background: #eaf4ff;
  box-shadow: 0 2px 8px rgba(91,188,255,0.08);
}
.stat-blue {
  background: linear-gradient(135deg, #5bbcff 60%, #a084e8 100%);
  color: #fff;
}
.stat-green {
  background: linear-gradient(135deg, #4fd18b 60%, #00cfff 100%);
  color: #fff;
}
.stat-yellow {
  background: linear-gradient(135deg, #ffd04b 60%, #ffb347 100%);
  color: #fff;
}
.stat-info {
  display: flex;
  flex-direction: column;
}
.stat-value {
  font-size: 2.2rem;
  font-weight: 800;
  margin-bottom: 2px;
  color: #5bbcff;
  letter-spacing: 1px;
}
.stat-label {
  font-size: 1.08rem;
  color: #a0aec0;
  font-weight: 500;
}

.favorite-btn-row {
  margin-bottom: 22px;
  text-align: right;
}
.el-radio-group {
  background: #fff;
  border-radius: 30px;
  box-shadow: 0 2px 8px rgba(91,188,255,0.07);
  padding: 4px 8px;
}
.el-radio-button__inner {
  border-radius: 30px !important;
  border: none !important;
  background: transparent !important;
  color: #5bbcff !important;
  font-weight: 600;
  font-size: 1.08rem;
  transition: background 0.2s, color 0.2s;
}
.el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: linear-gradient(90deg, #5bbcff, #a084e8) !important;
  color: #fff !important;
  box-shadow: 0 2px 8px rgba(91,188,255,0.13);
}

.table-card {
  border-radius: 18px;
  background: #fff;
  border: none;
  margin-bottom: 24px;
  box-shadow: 0 4px 18px rgba(91,188,255,0.07);
}
.el-table {
  margin-top: 10px;
  background: #fff;
  color: #222;
  border-radius: 16px;
  overflow: hidden;
  font-size: 1.08rem;
}
.el-table th {
  background: linear-gradient(90deg, #5bbcff 60%, #a084e8 100%);
  color: #fff;
  font-size: 1.13rem;
  font-weight: 700;
  border: none;
}
.el-table td {
  background: #fff;
  color: #222;
  font-size: 1.08rem;
  border: none;
}
.el-table__row:hover td {
  background: #eaf4ff !important;
  color: #5bbcff;
}
.el-tag {
  background: #eaf4ff !important;
  color: #5bbcff !important;
  border: none !important;
  font-weight: 600;
}
.el-button {
  margin: 0 2px;
  border-radius: 50%;
  background: #eaf4ff;
  color: #5bbcff;
  border: none;
  transition: background 0.2s, color 0.2s;
}
.el-button:hover {
  background: linear-gradient(90deg, #5bbcff, #a084e8);
  color: #fff;
}
.el-pagination {
  margin-top: 24px;
  text-align: right;
}

/* 状态色 */
span[style*="color:#67C23A"] {
  color: #4fd18b !important;
  font-size: 1.3rem !important;
  font-weight: 700;
}
span[style*="color:#f56c6c"] {
  color: #ffb347 !important;
  font-size: 1.3rem !important;
  font-weight: 700;
}
span[style*="color:#909399"] {
  color: #a0aec0 !important;
}

/* 响应式 */
@media (max-width: 1200px) {
  .main {
    padding: 32px 2vw;
  }
}
@media (max-width: 900px) {
  .main {
    padding: 18px 0;
  }
  .stat-card {
    flex-direction: column;
    align-items: flex-start;
    padding: 24px 18px;
    margin-bottom: 12px;
  }
  .stat-icon {
    margin-bottom: 10px;
    margin-right: 0;
  }
}
@media (max-width: 700px) {
  .main {
    padding: 8px 0;
  }
  .stat-card {
    padding: 16px 8px;
    border-radius: 12px;
  }
  .table-card {
    border-radius: 10px;
    padding: 0 2px;
  }
  .el-table th, .el-table td {
    font-size: 0.98rem;
  }
}
</style>