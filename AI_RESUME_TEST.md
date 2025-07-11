

## 接口说明

### 1. 生成简历（不保存）

POST http://localhost:8082/api/make/generate1
Content-Type: application/json

{
    "userInfo": "我叫张三，有3年Java开发经验，熟悉Spring Boot、MyBatis等框架，在ABC公司担任Java开发工程师，负责后端系统开发，有良好的代码习惯和团队协作能力。毕业于北京理工大学计算机专业，本科。",
    "jobTitle": "Java开发工程师"
}
```

### 2. 生成并保存简历
```
POST http://localhost:8082/api/make/save
Content-Type: application/json

{
    "userInfo": "我叫李四，有2年前端开发经验，熟悉Vue.js、React等框架，在XYZ公司担任前端开发工程师，负责用户界面开发，有良好的用户体验设计能力。毕业于清华大学软件工程专业，本科。",
    "jobTitle": "前端开发工程师"
}
## 请求参数说明

### AiResumeRequest
```json
{
    "userInfo": "用户基本信息（必填）",
    "jobTitle": "目标职位（可选）"
}


