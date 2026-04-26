<template>
  <div class="profile-container">
    <!-- 左侧主要内容区 -->
    <div class="main-content">
      <!-- 个人信息卡片 -->
      <div class="info-card">
        <div class="card-header">
          <h2>个人信息</h2>
          <el-button
            type="primary"
            @click="openEditDialog"
            :icon="Edit"
            size="default"
          >
            编辑信息
          </el-button>
        </div>

        <div class="card-content">
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="8" animated />
          </div>

          <div v-else-if="profileData" class="profile-display">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="学号">
                {{ profileData.studentNo || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="真实姓名">
                {{ profileData.realName || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="性别">
                {{ getGenderText(profileData.gender) }}
              </el-descriptions-item>
              <el-descriptions-item label="出生日期">
                {{ formatDate(profileData.birthDate) }}
              </el-descriptions-item>
              <el-descriptions-item label="学院">
                {{ profileData.college || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="专业">
                {{ profileData.major || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="班级">
                {{ profileData.className || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="年级">
                {{ profileData.grade || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="学历">
                {{ profileData.educationLevel || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="毕业年份">
                {{ profileData.graduationYear || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="档案完整度" span="2">
                <el-progress 
                  :percentage="profileData.profileCompletion || 0" 
                  :color="progressColor"
                />
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div v-else class="empty-state">
            <el-empty description="暂无个人信息">
              <el-button type="primary" @click="openEditDialog">
                立即完善信息
              </el-button>
            </el-empty>
          </div>
        </div>
      </div>

      <!-- 求职信息卡片 -->
      <div class="info-card">
        <div class="card-header">
          <h2>就业意向调查</h2>
          <el-button
            type="primary"
            @click="openJobInfoDialog"
            :icon="Edit"
            size="default"
          >
            {{ jobInfoData ? '编辑意向' : '填写意向' }}
          </el-button>
        </div>

        <div class="card-content">
          <div v-if="loadingJobInfo" class="loading-container">
            <el-skeleton :rows="6" animated />
          </div>

          <div v-else-if="jobInfoData && hasJobInfo" class="job-info-display">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="期望职位">
                {{ jobInfoData.position || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="工作年限">
                {{ jobInfoData.workYears ? jobInfoData.workYears + '年' : '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="期望城市">
                {{ jobInfoData.expectedCity || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="期望薪资">
                {{ formatSalary(jobInfoData.expectedSalaryMin, jobInfoData.expectedSalaryMax) }}
              </el-descriptions-item>
              <el-descriptions-item label="期望行业">
                {{ jobInfoData.expectedIndustry || '未填写' }}
              </el-descriptions-item>
              <el-descriptions-item label="掌握技能" span="2">
                <el-tag
                  v-for="skill in getSkillList(jobInfoData.skill)"
                  :key="skill"
                  type="info"
                  size="small"
                  style="margin-right: 8px; margin-bottom: 4px;"
                >
                  {{ skill }}
                </el-tag>
                <span v-if="!jobInfoData.skill || jobInfoData.skill.length === 0">未填写</span>
              </el-descriptions-item>
              <el-descriptions-item label="个人简介" span="2">
                {{ jobInfoData.profile || '未填写' }}
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div v-else class="empty-state">
            <el-alert
              title="提示"
              type="info"
              description="完善就业意向可以帮助系统为您推荐更合适的岗位，提高求职成功率！"
              :closable="false"
              show-icon
              style="margin-bottom: 20px;"
            />
            <el-empty description="暂无就业意向信息">
              <el-button type="primary" @click="openJobInfoDialog">
                立即填写就业意向
              </el-button>
            </el-empty>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧功能区域 -->
    <div class="sidebar">
      <!-- 简历管理卡片 -->
      <div class="sidebar-card">
        <div class="sidebar-header">
          <h3>简历管理</h3>
        </div>
        <div class="sidebar-content">
          <div v-if="resumeList.length > 0" class="resume-info">
            <div class="info-item">
              <span class="label">简历数量</span>
              <span class="value">{{ resumeList.length }}份</span>
            </div>
            <div class="status-item">
              <el-tag 
                v-if="resumeCompletionStatus === 1" 
                type="success" 
                size="small"
              >
                已完善
              </el-tag>
              <el-tag 
                v-else 
                type="warning" 
                size="small"
              >
                待完善
              </el-tag>
            </div>
          </div>
          <div v-else class="empty-info">
            <el-icon class="empty-icon"><Document /></el-icon>
            <p>暂无简历</p>
          </div>
          <el-button
            type="primary"
            size="small"
            @click="openResumeDialog"
            style="width: 100%; margin-top: 12px;"
          >
            {{ resumeList.length > 0 ? '管理简历' : '创建简历' }}
          </el-button>
        </div>
      </div>

      <!-- 经历管理卡片 -->
      <div class="sidebar-card">
        <div class="sidebar-header">
          <h3>经历管理</h3>
        </div>
        <div class="sidebar-content">
          <div class="experience-stats">
            <div class="stat-row">
              <span class="stat-label">科研经历</span>
              <span class="stat-value">{{ experienceStats.research }}项</span>
            </div>
            <div class="stat-row">
              <span class="stat-label">荣誉获奖</span>
              <span class="stat-value">{{ experienceStats.honors }}项</span>
            </div>
            <div class="stat-row">
              <span class="stat-label">实习经历</span>
              <span class="stat-value">{{ experienceStats.internship }}项</span>
            </div>
          </div>
          <el-button
            type="primary"
            size="small"
            @click="openExperienceDialog"
            style="width: 100%; margin-top: 12px;"
          >
            管理经历
          </el-button>
        </div>
      </div>
    </div>

    <!-- 求职信息卡片 -->
    <div class="card job-info-card">
      <div class="header">
        <h2>求职信息</h2>
        <p>完善求职信息可获得更精准的岗位推荐</p>
        <div class="header-actions">
          <el-button
            type="primary"
            @click="openJobInfoDialog"
            :icon="Edit"
          >
            {{ jobInfoData ? '编辑求职信息' : '填写求职信息' }}
          </el-button>
        </div>
      </div>

      <div class="content">
        <div v-if="loadingJobInfo" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>

        <div v-else-if="jobInfoData && hasJobInfo" class="job-info-display">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="期望职位">
              {{ jobInfoData.position || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="工作年限">
              {{ jobInfoData.workYears ? jobInfoData.workYears + '年' : '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="期望城市">
              {{ jobInfoData.expectedCity || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="期望薪资">
              {{ formatSalary(jobInfoData.expectedSalaryMin, jobInfoData.expectedSalaryMax) }}
            </el-descriptions-item>
            <el-descriptions-item label="期望行业">
              {{ jobInfoData.expectedIndustry || '未填写' }}
            </el-descriptions-item>
            <el-descriptions-item label="掌握技能" span="2">
              <el-tag
                v-for="skill in getSkillList(jobInfoData.skill)"
                :key="skill"
                type="success"
                style="margin-right: 8px; margin-bottom: 4px;"
              >
                {{ skill }}
              </el-tag>
              <span v-if="!jobInfoData.skill || jobInfoData.skill.length === 0">未填写</span>
            </el-descriptions-item>
            <el-descriptions-item label="个人简介" span="2">
              {{ jobInfoData.profile || '未填写' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div v-else class="empty-state">
          <el-alert
            title="提示"
            type="warning"
            description="完善求职信息可以帮助系统为您推荐更合适的岗位，提高求职成功率！"
            :closable="false"
            show-icon
            style="margin-bottom: 20px;"
          />
          <el-empty description="暂无求职信息">
            <el-button type="primary" @click="openJobInfoDialog">
              立即填写求职信息
            </el-button>
          </el-empty>
        </div>
      </div>
    </div>

    <!-- 编辑信息弹窗 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑学籍信息"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editFormRef"
        :model="editFormData"
        :rules="formRules"
        label-width="120px"
        label-position="left"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input
                v-model="editFormData.studentNo"
                placeholder="请输入学号"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input
                v-model="editFormData.realName"
                placeholder="请输入真实姓名"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="editFormData.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="male" />
                <el-option label="女" value="female" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="editFormData.birthDate"
                type="date"
                placeholder="请选择出生日期"
                style="width: 100%"
                :disabled-date="disabledDate"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学院" prop="college">
              <el-input
                v-model="editFormData.college"
                placeholder="请输入学院名称"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input
                v-model="editFormData.major"
                placeholder="请输入专业名称"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班级" prop="className">
              <el-input
                v-model="editFormData.className"
                placeholder="请输入班级"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="年级" prop="grade">
              <el-input
                v-model="editFormData.grade"
                placeholder="请输入年级"
                maxlength="20"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学历" prop="educationLevel">
              <el-select v-model="editFormData.educationLevel" placeholder="请选择学历" style="width: 100%">
                <el-option label="专科" value="专科" />
                <el-option label="本科" value="本科" />
                <el-option label="硕士" value="硕士" />
                <el-option label="博士" value="博士" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="毕业年份" prop="graduationYear">
              <el-date-picker
                v-model="editFormData.graduationYear"
                type="year"
                placeholder="请选择毕业年份"
                style="width: 100%"
                format="YYYY"
                value-format="YYYY"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button type="primary" @click="handleSave" :loading="saving">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑求职信息弹窗 -->
    <el-dialog
      v-model="jobInfoDialogVisible"
      title="编辑求职信息"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="jobInfoFormRef"
        :model="jobInfoFormData"
        :rules="jobInfoRules"
        label-width="120px"
        label-position="left"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="期望职位" prop="position">
              <el-input
                v-model="jobInfoFormData.position"
                placeholder="如: Java开发工程师"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="工作年限" prop="workYears">
              <el-input-number
                v-model="jobInfoFormData.workYears"
                :min="0"
                :max="50"
                placeholder="请输入工作年限"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="期望城市" prop="expectedCity">
              <el-input
                v-model="jobInfoFormData.expectedCity"
                placeholder="如: 北京、上海"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="期望行业" prop="expectedIndustry">
              <el-input
                v-model="jobInfoFormData.expectedIndustry"
                placeholder="如: 互联网、金融"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="期望薪资(最低)" prop="expectedSalaryMin">
              <el-input-number
                v-model="jobInfoFormData.expectedSalaryMin"
                :min="0"
                :max="999999"
                placeholder="单位: 元/月"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="期望薪资(最高)" prop="expectedSalaryMax">
              <el-input-number
                v-model="jobInfoFormData.expectedSalaryMax"
                :min="0"
                :max="999999"
                placeholder="单位: 元/月"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="掌握技能" prop="skill">
          <el-input
            v-model="jobInfoFormData.skill"
            type="textarea"
            :rows="3"
            placeholder="请输入您掌握的技能，用逗号分隔，如: Java,Spring,MySQL,Vue"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="个人简介" prop="profile">
          <el-input
            v-model="jobInfoFormData.profile"
            type="textarea"
            :rows="4"
            placeholder="请简要介绍您的工作经历、项目经验、个人优势等"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="jobInfoDialogVisible = false">取消</el-button>
          <el-button @click="resetJobInfoForm">重置</el-button>
          <el-button type="primary" @click="handleSaveJobInfo" :loading="savingJobInfo">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 简历管理弹窗 -->
    <el-dialog
      v-model="resumeDialogVisible"
      title="简历管理"
      width="900px"
      :close-on-click-modal="false"
    >
      <div class="resume-management">
        <div class="resume-actions">
          <el-button type="primary" @click="$router.push('/resume/create')">
            创建新简历
          </el-button>
          <el-button @click="loadResumeList">
            刷新列表
          </el-button>
        </div>

        <div v-if="loadingResume" class="loading-container">
          <el-skeleton :rows="4" animated />
        </div>

        <div v-else-if="resumeList.length > 0" class="resume-list">
          <el-table :data="resumeList" style="width: 100%">
            <el-table-column prop="title" label="简历标题" />
            <el-table-column prop="updatedAt" label="更新时间" width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.updatedAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button size="small" @click="viewResume(scope.row.id)">
                  查看
                </el-button>
                <el-button size="small" type="primary" @click="editResume(scope.row.id)">
                  编辑
                </el-button>
                <el-button size="small" type="danger" @click="deleteResume(scope.row.id)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div v-else class="empty-resume-list">
          <el-empty description="暂无简历">
            <el-button type="primary" @click="$router.push('/resume/create')">
              创建第一份简历
            </el-button>
          </el-empty>
        </div>
      </div>
    </el-dialog>

    <!-- 经历管理弹窗 -->
    <el-dialog
      v-model="experienceDialogVisible"
      title="经历管理"
      width="1000px"
      :close-on-click-modal="false"
    >
      <el-tabs v-model="activeExperienceTab" type="card">
        <!-- 科研经历 -->
        <el-tab-pane label="科研经历" name="research">
          <div class="experience-section">
            <div class="section-header">
              <el-button type="primary" @click="addResearchExperience">
                添加科研经历
              </el-button>
            </div>
            
            <div v-if="experienceData.researchExperiences && experienceData.researchExperiences.length > 0">
              <div 
                v-for="(item, index) in experienceData.researchExperiences" 
                :key="index"
                class="experience-item"
              >
                <el-card>
                  <template #header>
                    <div class="card-header">
                      <span>科研经历 {{ index + 1 }}</span>
                      <el-button size="small" type="danger" @click="removeResearchExperience(index)">删除</el-button>
                    </div>
                  </template>
                  <el-form :model="item" label-width="100px">
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <el-form-item label="项目名称">
                          <el-input v-model="item.projectName" placeholder="请输入项目名称" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="角色">
                          <el-input v-model="item.role" placeholder="如：主要研究者" />
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <el-form-item label="指导老师">
                          <el-input v-model="item.supervisor" placeholder="请输入指导老师姓名" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="6">
                        <el-form-item label="开始时间">
                          <el-date-picker v-model="item.startDate" type="month" placeholder="选择开始时间" style="width: 100%" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="6">
                        <el-form-item label="结束时间">
                          <el-date-picker v-model="item.endDate" type="month" placeholder="选择结束时间" style="width: 100%" />
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-form-item label="项目描述">
                      <el-input v-model="item.description" type="textarea" :rows="3" placeholder="请描述项目内容和您的工作" />
                    </el-form-item>
                    <el-form-item label="成果">
                      <el-input v-model="item.achievements" type="textarea" :rows="2" placeholder="请描述项目成果，如发表论文、获奖等" />
                    </el-form-item>
                  </el-form>
                </el-card>
              </div>
            </div>
            
            <el-empty v-else description="暂无科研经历">
              <el-button type="primary" @click="addResearchExperience">
                添加第一个科研经历
              </el-button>
            </el-empty>
          </div>
        </el-tab-pane>

        <!-- 荣誉获奖 -->
        <el-tab-pane label="荣誉获奖" name="honors">
          <div class="experience-section">
            <div class="section-header">
              <el-button type="primary" @click="addHonorAward">
                添加荣誉获奖
              </el-button>
            </div>
            
            <div v-if="experienceData.honorsAwards && experienceData.honorsAwards.length > 0">
              <div 
                v-for="(item, index) in experienceData.honorsAwards" 
                :key="index"
                class="experience-item"
              >
                <el-card>
                  <template #header>
                    <div class="card-header">
                      <span>荣誉获奖 {{ index + 1 }}</span>
                      <el-button size="small" type="danger" @click="removeHonorAward(index)">删除</el-button>
                    </div>
                  </template>
                  <el-form :model="item" label-width="100px">
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <el-form-item label="奖项名称">
                          <el-input v-model="item.awardName" placeholder="请输入奖项名称" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="奖项级别">
                          <el-select v-model="item.level" placeholder="请选择级别" style="width: 100%">
                            <el-option label="国家级" value="国家级" />
                            <el-option label="省级" value="省级" />
                            <el-option label="市级" value="市级" />
                            <el-option label="校级" value="校级" />
                            <el-option label="院级" value="院级" />
                          </el-select>
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <el-form-item label="获奖时间">
                          <el-date-picker v-model="item.awardDate" type="date" placeholder="选择获奖时间" style="width: 100%" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="颁发机构">
                          <el-input v-model="item.organization" placeholder="请输入颁发机构" />
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-form-item label="获奖描述">
                      <el-input v-model="item.description" type="textarea" :rows="3" placeholder="请描述获奖原因或背景" />
                    </el-form-item>
                  </el-form>
                </el-card>
              </div>
            </div>
            
            <el-empty v-else description="暂无荣誉获奖">
              <el-button type="primary" @click="addHonorAward">
                添加第一个荣誉获奖
              </el-button>
            </el-empty>
          </div>
        </el-tab-pane>

        <!-- 实习经历 -->
        <el-tab-pane label="实习经历" name="internship">
          <div class="experience-section">
            <div class="section-header">
              <el-button type="primary" @click="addInternshipExperience">
                添加实习经历
              </el-button>
            </div>
            
            <div v-if="experienceData.internshipExperiences && experienceData.internshipExperiences.length > 0">
              <div 
                v-for="(item, index) in experienceData.internshipExperiences" 
                :key="index"
                class="experience-item"
              >
                <el-card>
                  <template #header>
                    <div class="card-header">
                      <span>实习经历 {{ index + 1 }}</span>
                      <el-button size="small" type="danger" @click="removeInternshipExperience(index)">删除</el-button>
                    </div>
                  </template>
                  <el-form :model="item" label-width="100px">
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <el-form-item label="公司名称">
                          <el-input v-model="item.companyName" placeholder="请输入公司名称" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="职位">
                          <el-input v-model="item.position" placeholder="请输入实习职位" />
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-row :gutter="20">
                      <el-col :span="12">
                        <el-form-item label="部门">
                          <el-input v-model="item.department" placeholder="请输入所在部门" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="6">
                        <el-form-item label="开始时间">
                          <el-date-picker v-model="item.startDate" type="date" placeholder="选择开始时间" style="width: 100%" />
                        </el-form-item>
                      </el-col>
                      <el-col :span="6">
                        <el-form-item label="结束时间">
                          <el-date-picker v-model="item.endDate" type="date" placeholder="选择结束时间" style="width: 100%" />
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-form-item label="工作内容">
                      <el-input v-model="item.workContent" type="textarea" :rows="3" placeholder="请描述主要工作内容" />
                    </el-form-item>
                    <el-form-item label="工作成果">
                      <el-input v-model="item.achievements" type="textarea" :rows="2" placeholder="请描述工作成果和收获" />
                    </el-form-item>
                    <el-form-item label="技能">
                      <el-input v-model="item.skillsStr" placeholder="请输入掌握的技能，用逗号分隔" />
                    </el-form-item>
                  </el-form>
                </el-card>
              </div>
            </div>
            
            <el-empty v-else description="暂无实习经历">
              <el-button type="primary" @click="addInternshipExperience">
                添加第一个实习经历
              </el-button>
            </el-empty>
          </div>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="experienceDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveExperience" :loading="savingExperience">
            保存所有经历
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getStudentProfile, updateStudentProfile, getStudentExperience, updateStudentExperience, updateResumeCompletionStatus } from '@/api/student'
import { getJobInfo, saveJobInfo } from '@/api/resume'
import { Edit, Document } from '@element-plus/icons-vue'

export default {
  name: 'ApplicantProfile',
  components: {
    Edit,
    Document
  },
  data() {
    return {
      loading: false,
      profileData: null,
      editDialogVisible: false,
      saving: false,
      editFormData: {
        studentNo: '',
        realName: '',
        gender: '',
        birthDate: '',
        college: '',
        major: '',
        className: '',
        grade: '',
        educationLevel: '',
        graduationYear: ''
      },
      // 求职信息相关
      loadingJobInfo: false,
      jobInfoData: null,
      jobInfoDialogVisible: false,
      savingJobInfo: false,
      jobInfoFormData: {
        position: '',
        workYears: 0,
        expectedCity: '',
        expectedSalaryMin: null,
        expectedSalaryMax: null,
        expectedIndustry: '',
        skill: '',
        profile: ''
      },
      jobInfoRules: {
        position: [
          { max: 100, message: '期望职位长度不能超过100个字符', trigger: 'blur' }
        ],
        workYears: [
          { type: 'number', min: 0, max: 50, message: '工作年限必须在0-50年之间', trigger: 'blur' }
        ],
        expectedCity: [
          { max: 100, message: '期望城市长度不能超过100个字符', trigger: 'blur' }
        ],
        expectedIndustry: [
          { max: 100, message: '期望行业长度不能超过100个字符', trigger: 'blur' }
        ],
        expectedSalaryMin: [
          { type: 'number', min: 0, max: 999999, message: '薪资必须在0-999999之间', trigger: 'blur' }
        ],
        expectedSalaryMax: [
          { type: 'number', min: 0, max: 999999, message: '薪资必须在0-999999之间', trigger: 'blur' }
        ],
        skill: [
          { max: 500, message: '技能描述长度不能超过500个字符', trigger: 'blur' }
        ],
        profile: [
          { max: 1000, message: '个人简介长度不能超过1000个字符', trigger: 'blur' }
        ]
      },
      formRules: {
        studentNo: [
          { required: true, message: '请输入学号', trigger: 'blur' },
          { max: 50, message: '学号长度不能超过50个字符', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' },
          { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        college: [
          { required: true, message: '请输入学院名称', trigger: 'blur' },
          { max: 100, message: '学院名称长度不能超过100个字符', trigger: 'blur' }
        ],
        major: [
          { required: true, message: '请输入专业名称', trigger: 'blur' },
          { max: 100, message: '专业名称长度不能超过100个字符', trigger: 'blur' }
        ],
        className: [
          { required: true, message: '请输入班级', trigger: 'blur' },
          { max: 50, message: '班级长度不能超过50个字符', trigger: 'blur' }
        ],
        educationLevel: [
          { required: true, message: '请选择学历', trigger: 'change' }
        ],
        graduationYear: [
          { required: true, message: '请选择毕业年份', trigger: 'change' }
        ]
      },
      // 简历管理相关
      resumeDialogVisible: false,
      loadingResume: false,
      resumeList: [],
      resumeCompletionStatus: 0,
      // 经历管理相关
      experienceDialogVisible: false,
      activeExperienceTab: 'research',
      savingExperience: false,
      experienceData: {
        researchExperiences: [],
        honorsAwards: [],
        internshipExperiences: []
      }
    }
  },
  async mounted() {
    await this.loadProfile()
    await this.loadJobInfo()
    await this.loadResumeList()
    await this.loadExperience()
  },
  computed: {
    hasJobInfo() {
      if (!this.jobInfoData) return false
      return !!(
        this.jobInfoData.position ||
        this.jobInfoData.workYears ||
        this.jobInfoData.expectedCity ||
        this.jobInfoData.expectedSalaryMin ||
        this.jobInfoData.expectedSalaryMax ||
        this.jobInfoData.expectedIndustry ||
        this.jobInfoData.skill ||
        this.jobInfoData.profile
      )
    },
    experienceStats() {
      return {
        research: this.experienceData.researchExperiences?.length || 0,
        honors: this.experienceData.honorsAwards?.length || 0,
        internship: this.experienceData.internshipExperiences?.length || 0
      }
    },
    progressColor() {
      return '#87CEEB' // 马卡龙天蓝色
    }
  },
  methods: {
    async loadProfile() {
      try {
        this.loading = true
        const response = await getStudentProfile()

        if (response && response.success) {
          this.profileData = response.data.profile
        } else {
          this.profileData = null
        }
      } catch (error) {
        console.error('加载学生档案失败:', error)
        this.$message.error('加载学生档案失败')
        this.profileData = null
      } finally {
        this.loading = false
      }
    },

    openEditDialog() {
      this.fillEditForm()
      this.editDialogVisible = true
    },

    fillEditForm() {
      if (this.profileData) {
        this.editFormData = {
          studentNo: this.profileData.studentNo || '',
          realName: this.profileData.realName || '',
          gender: this.profileData.gender || '',
          birthDate: this.profileData.birthDate || '',
          college: this.profileData.college || '',
          major: this.profileData.major || '',
          className: this.profileData.className || '',
          grade: this.profileData.grade || '',
          educationLevel: this.profileData.educationLevel || '',
          graduationYear: this.profileData.graduationYear || ''
        }
      } else {
        this.resetForm()
      }
    },

    resetForm() {
      this.editFormData = {
        studentNo: '',
        realName: '',
        gender: '',
        birthDate: '',
        college: '',
        major: '',
        className: '',
        grade: '',
        educationLevel: '',
        graduationYear: ''
      }
      this.$nextTick(() => {
        if (this.$refs.editFormRef) {
          this.$refs.editFormRef.clearValidate()
        }
      })
    },

    async handleSave() {
      try {
        await this.$refs.editFormRef.validate()
        this.saving = true

        const response = await updateStudentProfile(this.editFormData)

        if (response && response.success) {
          this.$message.success('保存成功')
          this.editDialogVisible = false
          
          // 更新store中的档案完成状态
          this.$store.dispatch('updateProfileStatus', true)
          
          // 重新加载学生档案
          await this.loadProfile()
        } else {
          this.$message.error(response?.message || '保存失败')
        }
      } catch (error) {
        console.error('保存失败:', error)
        this.$message.error('保存失败')
      } finally {
        this.saving = false
      }
    },

    disabledDate(time) {
      return time.getTime() > Date.now()
    },

    getGenderText(gender) {
      const genderMap = {
        'male': '男',
        'female': '女',
        'other': '其他'
      }
      return genderMap[gender] || '未填写'
    },

    formatDate(date) {
      if (!date) return '未填写'
      return new Date(date).toLocaleDateString('zh-CN')
    },

    // 求职信息相关方法
    async loadJobInfo() {
      try {
        this.loadingJobInfo = true
        const response = await getJobInfo()

        if (response && response.success) {
          this.jobInfoData = response.data || {}
        } else {
          this.jobInfoData = {}
        }
      } catch (error) {
        console.error('加载求职信息失败:', error)
        this.jobInfoData = {}
      } finally {
        this.loadingJobInfo = false
      }
    },

    openJobInfoDialog() {
      this.fillJobInfoForm()
      this.jobInfoDialogVisible = true
    },

    fillJobInfoForm() {
      if (this.jobInfoData) {
        this.jobInfoFormData = {
          position: this.jobInfoData.position || '',
          workYears: this.jobInfoData.workYears || 0,
          expectedCity: this.jobInfoData.expectedCity || '',
          expectedSalaryMin: this.jobInfoData.expectedSalaryMin || null,
          expectedSalaryMax: this.jobInfoData.expectedSalaryMax || null,
          expectedIndustry: this.jobInfoData.expectedIndustry || '',
          skill: this.jobInfoData.skill || '',
          profile: this.jobInfoData.profile || ''
        }
      }
    },

    resetJobInfoForm() {
      this.$refs.jobInfoFormRef?.resetFields()
    },

    async handleSaveJobInfo() {
      try {
        await this.$refs.jobInfoFormRef.validate()
        this.savingJobInfo = true

        const response = await saveJobInfo(this.jobInfoFormData)

        if (response && response.success) {
          this.$message.success('保存成功')
          this.jobInfoDialogVisible = false

          // 重新加载求职信息
          await this.loadJobInfo()
        } else {
          this.$message.error(response?.message || '保存失败')
        }
      } catch (error) {
        console.error('保存失败:', error)
        if (error !== false) { // 表单验证失败时不显示错误消息
          this.$message.error('保存失败')
        }
      } finally {
        this.savingJobInfo = false
      }
    },

    formatSalary(min, max) {
      if (!min && !max) return '未填写'
      if (min && max) return `${min} - ${max} 元/月`
      if (min) return `${min}+ 元/月`
      if (max) return `${max} 元/月以下`
      return '未填写'
    },

    getSkillList(skillStr) {
      if (!skillStr) return []
      return skillStr.split(',').map(s => s.trim()).filter(s => s)
    },

    // 简历管理相关方法
    openResumeDialog() {
      this.resumeDialogVisible = true
      this.loadResumeList()
    },

    async loadResumeList() {
      try {
        this.loadingResume = true
        // 使用正确的简历列表API
        const response = await this.$http.get('/resumes/list')
        if (response && response.success) {
          this.resumeList = response.data || []
        }
      } catch (error) {
        console.error('加载简历列表失败:', error)
        this.resumeList = []
      } finally {
        this.loadingResume = false
      }
    },

    viewResume(resumeId) {
      this.$router.push(`/resume/view/${resumeId}`)
    },

    editResume(resumeId) {
      this.$router.push(`/resume/edit/${resumeId}`)
    },

    async deleteResume(resumeId) {
      try {
        await this.$confirm('确定要删除这份简历吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await this.$http.delete(`/resumes/${resumeId}`)
        if (response && response.success) {
          this.$message.success('删除成功')
          await this.loadResumeList()
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除简历失败:', error)
          this.$message.error('删除失败')
        }
      }
    },

    formatDateTime(dateTime) {
      if (!dateTime) return '未知'
      return new Date(dateTime).toLocaleString('zh-CN')
    },

    // 经历管理相关方法
    openExperienceDialog() {
      this.experienceDialogVisible = true
      this.loadExperience()
    },

    async loadExperience() {
      try {
        const response = await getStudentExperience()
        if (response && response.success) {
          this.experienceData = {
            researchExperiences: response.data.researchExperiences || [],
            honorsAwards: response.data.honorsAwards || [],
            internshipExperiences: response.data.internshipExperiences || []
          }
          
          // 处理实习经历的技能字段，转换为字符串用于表单显示
          this.experienceData.internshipExperiences.forEach(item => {
            if (item.skills && Array.isArray(item.skills)) {
              item.skillsStr = item.skills.join(', ')
            }
          })
          
          this.resumeCompletionStatus = response.data.resumeCompletionStatus || 0
        }
      } catch (error) {
        console.error('加载经历信息失败:', error)
      }
    },

    async saveExperience() {
      try {
        this.savingExperience = true
        
        // 处理数据格式
        this.processExperienceData()
        
        const response = await updateStudentExperience(this.experienceData)
        
        if (response && response.success) {
          this.$message.success('保存成功')
          this.experienceDialogVisible = false
          // 更新简历完善状态
          await updateResumeCompletionStatus(1)
          this.resumeCompletionStatus = 1
        } else {
          this.$message.error(response?.message || '保存失败')
        }
      } catch (error) {
        console.error('保存经历失败:', error)
        this.$message.error('保存失败')
      } finally {
        this.savingExperience = false
      }
    },

    // 科研经历管理
    addResearchExperience() {
      this.experienceData.researchExperiences.push({
        projectName: '',
        role: '',
        supervisor: '',
        startDate: '',
        endDate: '',
        description: '',
        achievements: ''
      })
    },

    editResearchExperience(index) {
      // 这里可以打开编辑弹窗，简化处理直接编辑
      this.$message.info('请直接在表单中编辑内容')
    },

    removeResearchExperience(index) {
      this.experienceData.researchExperiences.splice(index, 1)
    },

    // 荣誉获奖管理
    addHonorAward() {
      this.experienceData.honorsAwards.push({
        awardName: '',
        level: '',
        awardDate: '',
        organization: '',
        description: ''
      })
    },

    editHonorAward(index) {
      this.$message.info('请直接在表单中编辑内容')
    },

    removeHonorAward(index) {
      this.experienceData.honorsAwards.splice(index, 1)
    },

    // 实习经历管理
    addInternshipExperience() {
      this.experienceData.internshipExperiences.push({
        companyName: '',
        position: '',
        department: '',
        startDate: '',
        endDate: '',
        workContent: '',
        achievements: '',
        skillsStr: '', // 用于表单输入
        skills: []
      })
    },

    editInternshipExperience(index) {
      this.$message.info('请直接在表单中编辑内容')
    },

    removeInternshipExperience(index) {
      this.experienceData.internshipExperiences.splice(index, 1)
    },

    // 保存前处理数据
    processExperienceData() {
      // 处理实习经历的技能字段
      this.experienceData.internshipExperiences.forEach(item => {
        if (item.skillsStr) {
          item.skills = item.skillsStr.split(',').map(s => s.trim()).filter(s => s)
        }
      })
    }
  }
}
</script>

<style scoped>
/* 马卡龙天蓝色主题变量 */
:root {
  --primary-color: #87CEEB;
  --primary-light: #B0E0E6;
  --primary-dark: #4682B4;
  --bg-color: #F8FBFF;
  --border-color: #E6F3FF;
  --text-primary: #2C3E50;
  --text-secondary: #7F8C8D;
}

.profile-container {
  display: flex;
  gap: 24px;
  padding: 24px;
  background-color: var(--bg-color);
  min-height: calc(100vh - 60px);
}

/* 左侧主要内容区 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 右侧边栏 */
.sidebar {
  width: 280px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 信息卡片样式 */
.info-card {
  background: white;
  border-radius: 12px;
  border: 1px solid var(--border-color);
  box-shadow: 0 2px 8px rgba(135, 206, 235, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
}

.info-card:hover {
  box-shadow: 0 4px 16px rgba(135, 206, 235, 0.15);
  transform: translateY(-2px);
}

.card-header {
  padding: 20px 24px;
  background-color: var(--primary-color);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.card-header .el-button {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
}

.card-header .el-button:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.card-content {
  padding: 24px;
}

/* 侧边栏卡片样式 */
.sidebar-card {
  background: white;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  box-shadow: 0 2px 6px rgba(135, 206, 235, 0.08);
  overflow: hidden;
}

.sidebar-header {
  padding: 16px 20px;
  background-color: var(--primary-light);
  border-bottom: 1px solid var(--border-color);
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.sidebar-content {
  padding: 20px;
}

/* 简历信息样式 */
.resume-info {
  margin-bottom: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.info-item .label {
  color: var(--text-secondary);
  font-size: 14px;
}

.info-item .value {
  color: var(--primary-dark);
  font-weight: 600;
  font-size: 14px;
}

.status-item {
  text-align: center;
  margin-top: 8px;
}

.empty-info {
  text-align: center;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

.empty-icon {
  font-size: 24px;
  color: var(--primary-color);
  margin-bottom: 8px;
}

.empty-info p {
  margin: 0;
  font-size: 14px;
}

/* 经历统计样式 */
.experience-stats {
  margin-bottom: 12px;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #F5F5F5;
}

.stat-row:last-child {
  border-bottom: none;
}

.stat-label {
  color: var(--text-secondary);
  font-size: 14px;
}

.stat-value {
  color: var(--primary-dark);
  font-weight: 600;
  font-size: 14px;
}

/* 加载和空状态 */
.loading-container {
  padding: 20px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
}

.empty-state .el-alert {
  text-align: left;
}

/* 描述列表样式覆盖 */
.el-descriptions :deep(.el-descriptions__label) {
  font-weight: 600;
  color: var(--text-primary);
  background-color: #FAFBFC;
}

.el-descriptions :deep(.el-descriptions__content) {
  color: var(--text-secondary);
}

.el-descriptions :deep(.el-descriptions__table) {
  border-color: var(--border-color);
}

.el-descriptions :deep(.el-descriptions__cell) {
  border-color: var(--border-color);
}

/* 进度条样式 */
.el-progress :deep(.el-progress-bar__outer) {
  background-color: #F0F8FF;
}

/* 标签样式 */
.el-tag.el-tag--info {
  background-color: var(--primary-light);
  border-color: var(--primary-color);
  color: var(--primary-dark);
}

/* 按钮样式覆盖 */
.el-button--primary {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

.el-button--primary:hover {
  background-color: var(--primary-dark);
  border-color: var(--primary-dark);
}

/* 弹窗样式 */
.dialog-footer {
  text-align: right;
  padding-top: 20px;
}

.dialog-footer .el-button {
  margin-left: 10px;
}

.resume-management {
  min-height: 400px;
}

.resume-actions {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.resume-list {
  margin-top: 20px;
}

.empty-resume-list {
  text-align: center;
  padding: 60px 20px;
}

.experience-section {
  min-height: 300px;
}

.section-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.experience-item {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-weight: 600;
  color: var(--text-primary);
}

/* 表单样式 */
.el-dialog .el-form {
  padding: 0 20px;
}

.el-dialog .el-form-item {
  margin-bottom: 20px;
}

.el-dialog .el-row {
  margin-bottom: 0;
}

.el-dialog .el-col {
  padding-bottom: 0;
}

.el-dialog .el-input-number {
  width: 100%;
}

.el-dialog .el-select {
  width: 100%;
}

.el-dialog .el-date-picker {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .sidebar {
    width: 240px;
  }
}

@media (max-width: 768px) {
  .profile-container {
    flex-direction: column;
    padding: 16px;
    gap: 16px;
  }
  
  .sidebar {
    width: 100%;
    flex-direction: row;
    gap: 16px;
  }
  
  .sidebar-card {
    flex: 1;
  }
  
  .card-header {
    padding: 16px 20px;
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .card-content {
    padding: 20px;
  }
}

@media (max-width: 480px) {
  .sidebar {
    flex-direction: column;
  }
  
  .card-header {
    flex-direction: row;
    align-items: center;
  }
}
</style>
  margin-bottom: 20px;
}

.job-info-card {
  margin-top: 20px;
}

.header {
  padding: 20px 30px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-content {
  flex: 1;
}

.header h2 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.header-actions {
  margin-left: 20px;
}

.content {
  padding: 30px;
}

.loading-container {
  padding: 20px;
}

.profile-display {
  max-width: 800px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.el-descriptions {
  margin-top: 0;
}

.el-descriptions :deep(.el-descriptions__label) {
  font-weight: 600;
  color: #333;
  width: 120px;
}

.el-descriptions :deep(.el-descriptions__content) {
  color: #666;
}

/* 弹窗样式 */
.dialog-footer {
  text-align: right;
}

.dialog-footer .el-button {
  margin-left: 10px;
}

.el-dialog .el-form {
  padding: 0 20px;
}

.el-dialog .el-form-item {
  margin-bottom: 20px;
}

.el-dialog .el-row {
  margin-bottom: 0;
}

.el-dialog .el-col {
  padding-bottom: 0;
}

/* 弹窗内表单样式优化 */
.el-dialog .el-input-number {
  width: 100%;
}

.el-dialog .el-select {
  width: 100%;
}

.el-dialog .el-date-picker {
  width: 100%;
}

