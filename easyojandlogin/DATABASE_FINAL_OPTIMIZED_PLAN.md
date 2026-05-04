## 最终数据库表复用方案（优化版）

### 👥 用户相关表（使用组合方案）

**使用现有表组合：**
- `users` 表 - 基本用户信息（user_id, username, email, phone, password, avatar）
- `resume` 表 - 简历信息（工作经验、教育背景等）
- `student_profile` 表 - 学生画像（技能、偏好等）

**优势：**
- ✅ 充分利用现有表结构
- ✅ 避免数据冗余
- ✅ 用户信息完整性高

---

### 🏢 企业相关表（companies 表补充字段）

**现有字段：**
- company_id ✅
- company_name ✅
- industry ✅
- scale ✅
- location ✅
- created_at ✅
- updated_at ✅

**补充字段：**
```sql
ALTER TABLE companies
    ADD COLUMN company_type VARCHAR(50) COMMENT '企业类型',
ADD COLUMN company_welfare VARCHAR(255) COMMENT '福利标签',
ADD COLUMN company_tags VARCHAR(255) COMMENT '公司标签';
```

---

### 💼 职位相关表（双表结合方案）

#### 方案：`recruitmenttable` + `jobs` 结合使用

**recruitmenttable 表用于：**
- 存储完整的职位详情信息
- 包含所有招聘相关的详细字段

**jobs 表用于：**
- 存储基础职位信息
- 与应聘系统关联

**字段映射关系：**
```
recruitmenttable.id ←→ jobs.job_id
recruitmenttable.position_name ←→ jobs.title
recruitmenttable.salary_lower/upper ←→ jobs.min_salary/max_salary
recruitmenttable.publish_time ←→ jobs.post_date
recruitmenttable.status ←→ jobs.verify_status
recruitmenttable.location ←→ jobs.location
```

**location 字段处理方案：**
```
location 字段可以存储完整的地址信息，如：
- "北京市朝阳区"
- "上海市浦东新区"

需要时可以通过字符串解析提取：
- city（城市）：从 location 中提取前2-3个字符
- district（区县）：从 location 中提取后面的部分
- province（省份）：通过城市编码表关联

或者在 jobs 表中添加冗余字段用于查询优化：
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS city VARCHAR(50);
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS district VARCHAR(50);
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS province VARCHAR(50);
```

**jobs 表补充字段（可选）：**
```sql
-- 如果需要快速查询，可以添加这些冗余字段
ALTER TABLE jobs
    ADD COLUMN avg_salary INT COMMENT '平均薪资',
ADD COLUMN city VARCHAR(50) COMMENT '城市',
ADD COLUMN district VARCHAR(50) COMMENT '区县',
ADD COLUMN province VARCHAR(50) COMMENT '省份';
```

---

### 📋 表使用总结

| 模块 | 表名 | 用途 | 状态 |
|------|------|------|------|
| **用户** | users + resume + student_profile | 用户基本信息 + 简历 + 画像 | ✅ 组合使用 |
| **企业** | companies | 企业信息 | ✅ 补充字段 |
| **职位** | recruitmenttable + jobs | 职位详情 + 基础信息 | ✅ 双表结合 |
| **应聘** | applications | 应聘记录 | ✅ 直接使用 |
| **收藏** | user_job_collection | 用户收藏 | ✅ 直接使用 |
| **浏览** | user_job_views | 用户浏览 | ✅ 直接使用 |
| **偏好** | user_preferences | 用户偏好 | ✅ 直接使用 |
| **推荐** | job_recommendation | 职位推荐 | ✅ 补充字段 |
| **薪资预测** | salary_prediction_result | 薪资预测 | 🆕 新增 |
| **技能规则** | skill_association_rules | 技能关联 | 🆕 新增 |
| **用户行为** | user_action | 行为记录 | 🆕 新增 |

---

### 🔧 具体实施方案

#### 1. 企业表补充字段
```sql
ALTER TABLE companies ADD COLUMN IF NOT EXISTS company_type VARCHAR(50) COMMENT '企业类型';
ALTER TABLE companies ADD COLUMN IF NOT EXISTS company_welfare VARCHAR(255) COMMENT '福利标签';
ALTER TABLE companies ADD COLUMN IF NOT EXISTS company_tags VARCHAR(255) COMMENT '公司标签';
```

#### 2. 职位表补充字段（可选，用于查询优化）
```sql
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS avg_salary INT COMMENT '平均薪资';
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS city VARCHAR(50) COMMENT '城市';
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS district VARCHAR(50) COMMENT '区县';
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS province VARCHAR(50) COMMENT '省份';
```

#### 3. 新增必要的表
```sql
-- 薪资预测结果表
CREATE TABLE salary_prediction_result (
                                          pred_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                          user_id INT NOT NULL,
                                          predicted_salary DECIMAL(10,2),
                                          salary_range_min DECIMAL(10,2),
                                          salary_range_max DECIMAL(10,2),
                                          confidence_score DECIMAL(5,2),
                                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          CONSTRAINT fk_salary_user
                                              FOREIGN KEY (user_id)
                                                  REFERENCES users(user_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4;

-- 技能关联规则表
CREATE TABLE skill_association_rules (
                                         rule_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                                         skill_a VARCHAR(100),
                                         skill_b VARCHAR(100),
                                         support DECIMAL(5,4),
                                         confidence DECIMAL(5,4),
                                         lift DECIMAL(5,4),
                                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         UNIQUE KEY uk_skills (skill_a, skill_b)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4;

-- 用户行为表
CREATE TABLE user_action (
                             action_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, -- 改为 INT UNSIGNED
                             user_id INT NOT NULL, -- 改为 INT，与 users 表一致
                             action_type VARCHAR(50),
                             action_target VARCHAR(100),
                             action_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_action_user
                                 FOREIGN KEY (user_id)
                                     REFERENCES users(user_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4;
```

---

### ✨ 优势总结

- ✅ 最大化复用现有表（减少新增表数量）
- ✅ 用户信息完整（users + resume + student_profile）
- ✅ 职位信息完整（recruitmenttable + jobs）
- ✅ 减少数据冗余
- ✅ 保持系统一致性
- ✅ 只需新增3个表
