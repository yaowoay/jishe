### 1. 实体类 (Entity)
- `Resume.java` - 简历主实体
- `WorkExperience.java` - 工作经历实体
- `Education.java` - 教育经历实体
- `ProjectExperience.java` - 项目经验实体
- `Skill.java` - 技能专长实体
- `AdditionalInfo.java` - 其他信息实体

### 2. DTO类
- `ResumeCreateRequest.java` - 简历创建请求
- `ResumeUpdateRequest.java` - 简历更新请求
- `ResumeResponse.java` - 简历响应
- `WorkExperienceDTO.java` - 工作经历DTO
- `EducationDTO.java` - 教育经历DTO
- `ProjectExperienceDTO.java` - 项目经验DTO
- `SkillDTO.java` - 技能专长DTO
- `AdditionalInfoDTO.java` - 其他信息DTO

### 3. Mapper接口
- `ResumeMapper.java` - 简历数据访问
- `WorkExperienceMapper.java` - 工作经历数据访问
- `EducationMapper.java` - 教育经历数据访问
- `ProjectExperienceMapper.java` - 项目经验数据访问
- `SkillMapper.java` - 技能专长数据访问
- `AdditionalInfoMapper.java` - 其他信息数据访问

### 4. Service层
- `ResumeService.java` - 简历服务接口
- `ResumeServiceImpl.java` - 简历服务实现

### 5. Controller层
- `ResumeController.java` - 简历主控制器
- `ResumeDetailController.java` - 简历详情管理控制器

### 6. 数据库表结构
- `resume_tables.sql` - 完整的数据库表结构

### 7. MyBatis映射文件
- `ResumeMapper.xml` - 简历映射
- `WorkExperienceMapper.xml` - 工作经历映射
- `EducationMapper.xml` - 教育经历映射
- `ProjectExperienceMapper.xml` - 项目经验映射
- `SkillMapper.xml` - 技能专长映射
- `AdditionalInfoMapper.xml` - 其他信息映射

## 主要功能

### 简历基本信息管理
- 姓名、电话、邮箱
- 期望职位、工作年限
- 所在地、个人简介
- 简历名称管理

### 详细信息管理
- **工作经历**: 公司名称、职位、时间、职责、成果
- **教育经历**: 学校、专业、学历、时间、在校经历
- **项目经验**: 项目名称、角色、时间、描述
- **技能专长**: 技能名称、熟练度(1-5星)、描述
- **其他信息**: 自定义类型、名称、时间、描述

### 高级功能
- 简历复制
- 分享链接生成
- 默认简历设置
- 简历搜索
- 热门简历展示
- 最近更新简历

## 用户认证

系统使用session进行用户认证，支持两种登录方式：
1. 手机号验证码登录 (`/sms/login`)
2. 邮箱验证码登录 (`/email/login`)

登录成功后，用户信息存储在session中，所有简历操作都会自动验证用户权限。


1. **执行数据库脚本**
   ```sql
   -- 执行 sql/resume_tables.sql 创建表结构
   ```
## API接口

### 主要接口
- `POST /api/resumes` - 创建简历
- `PUT /api/resumes/{id}` - 更新简历
- `GET /api/resumes/{id}` - 获取简历详情
- `GET /api/resumes` - 获取用户简历列表
- `DELETE /api/resumes/{id}` - 删除简历

### 详情管理接口
- `POST /api/resumes/{id}/work-experiences` - 添加工作经历
- `POST /api/resumes/{id}/educations` - 添加教育经历
- `POST /api/resumes/{id}/project-experiences` - 添加项目经验
- `POST /api/resumes/{id}/skills` - 添加技能专长
- `POST /api/resumes/{id}/additional-infos` - 添加其他信息

POST http://localhost:8082/api/resumes
Content-Type: application/json

{
    "name": "我的第一份简历",
    "fullName": "张三",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "position": "Java开发工程师",
    "workYears": 3,
    "location": "北京",
    "profile": "有3年Java开发经验，熟悉Spring Boot、MyBatis等框架，有良好的代码习惯和团队协作能力。",
    "workExperiences": [
        {
            "company": "ABC科技有限公司",
            "position": "Java开发工程师",
            "startDate": "2021-01-01",
            "endDate": "2023-01-01",
            "responsibility": "负责公司核心业务系统的后端开发，参与需求分析、系统设计、编码实现和测试部署。",
            "achievement": "完成用户管理、订单管理、支付系统等核心模块开发，系统稳定性提升30%，响应时间减少50%。"
        },
        {
            "company": "XYZ互联网公司",
            "position": "初级Java开发工程师",
            "startDate": "2020-03-01",
            "endDate": "2020-12-31",
            "responsibility": "参与电商平台开发，负责商品管理、库存管理等模块的开发维护。",
            "achievement": "独立完成商品搜索功能优化，搜索效率提升40%。"
        }
    ],
    "educations": [
        {
            "school": "北京理工大学",
            "major": "计算机科学与技术",
            "degree": "本科",
            "startDate": "2016-09-01",
            "endDate": "2020-07-01",
            "description": "主修数据结构、算法设计、数据库原理、软件工程等课程，获得优秀毕业生称号。"
        }
    ],
    "projectExperiences": [
        {
            "projectName": "企业管理系统",
            "role": "后端开发负责人",
            "startDate": "2022-03-01",
            "endDate": "2022-12-31",
            "description": "负责企业级管理系统的后端架构设计和核心功能开发，使用Spring Boot + MyBatis + MySQL技术栈。"
        },
        {
            "projectName": "电商平台",
            "role": "开发工程师",
            "startDate": "2021-06-01",
            "endDate": "2022-02-28",
            "description": "参与电商平台开发，负责用户管理、商品管理、订单管理等模块的开发。"
        }
    ],
    "skills": [
        {
            "skillName": "Java",
            "proficiency": 5,
            "description": "熟练掌握Java语言，熟悉JVM调优、多线程编程。"
        },
        {
            "skillName": "Spring Boot",
            "proficiency": 4,
            "description": "熟练使用Spring Boot框架进行后端开发。"
        },
        {
            "skillName": "MySQL",
            "proficiency": 4,
            "description": "熟悉MySQL数据库设计和优化。"
        },
        {
            "skillName": "Redis",
            "proficiency": 3,
            "description": "了解Redis缓存使用和配置。"
        }
    ],
    "additionalInfos": [
        {
            "type": "证书",
            "name": "Oracle认证Java程序员",
            "startDate": "2021-06-01",
            "endDate": "2024-06-01",
            "description": "获得Oracle官方认证的Java程序员证书。"
        },
        {
            "type": "获奖",
            "name": "优秀员工",
            "startDate": "2022-12-01",
            "endDate": "2022-12-31",
            "description": "2022年度优秀员工奖。"
        }
    ]
}

获取用户简历列表
GET http://localhost:8082/api/resumes

 更新简历
PUT http://localhost:8082/api/resumes/1
Content-Type: application/json

{
    "name": "更新后的简历",
    "fullName": "张三",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "position": "高级Java开发工程师",
    "workYears": 4,
    "location": "北京",
    "profile": "有4年Java开发经验，熟悉微服务架构，有良好的代码习惯和团队协作能力。",
    "workExperiences": [
        {
            "company": "ABC科技有限公司",
            "position": "高级Java开发工程师",
            "startDate": "2021-01-01",
            "endDate": "2023-01-01",
            "responsibility": "负责公司核心业务系统的后端开发，参与需求分析、系统设计、编码实现和测试部署。",
            "achievement": "完成用户管理、订单管理、支付系统等核心模块开发，系统稳定性提升30%，响应时间减少50%。"
        }
    ],
    "educations": [
        {
            "school": "北京理工大学",
            "major": "计算机科学与技术",
            "degree": "本科",
            "startDate": "2016-09-01",
            "endDate": "2020-07-01",
            "description": "主修数据结构、算法设计、数据库原理、软件工程等课程，获得优秀毕业生称号。"
        }
    ],
    "skills": [
        {
            "skillName": "Java",
            "proficiency": 5,
            "description": "熟练掌握Java语言，熟悉JVM调优、多线程编程。"
        },
        {
            "skillName": "Spring Boot",
            "proficiency": 5,
            "description": "熟练使用Spring Boot框架进行后端开发。"
        }
    ]
}