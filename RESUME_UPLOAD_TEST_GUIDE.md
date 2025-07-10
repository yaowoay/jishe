# 简历上传和试题生成测试指南

## 完整流程

### 第一步：上传简历文件

#### 1.1 使用专门的简历上传接口
```bash
POST http://localhost:8082/api/file/upload-resume
Content-Type: multipart/form-data

Form Data:
- file: [选择简历文件]
```

#### 1.2 预期响应
```json
{
  "code": 200,
  "message": "ok",
  "data": "/api/file/download/resume/123/abc123.pdf"
}
```

### 第二步：基于文件URL生成试题

```bash
POST http://localhost:8082/api/exam/generate2
Content-Type: application/x-www-form-urlencoded

Form Data:
- resumeFilePath: "/api/file/download/resume/123/abc123.pdf"
- jobPosition: "Java Developer"
- skills: "Java, Spring"
- experience: "ONE_TO_THREE_YEARS"
- questionCount: "5"
- difficultyLevel: "INTERMEDIATE"
- focusArea: "微服务"
```

## Postman测试步骤

### 1. 创建第一个请求：上传简历

**请求配置：**
- Method: `POST`
- URL: `http://localhost:8082/api/file/upload-resume`
- Headers: `Content-Type: multipart/form-data`

**Body配置：**
- Type: `form-data`
- Key: `file` (Type: File)
- Value: 选择你的简历文件

**发送请求并记录返回的文件URL**

### 2. 创建第二个请求：生成试题

**请求配置：**
- Method: `POST`
- URL: `http://localhost:8082/api/exam/generate2`
- Headers: `Content-Type: application/x-www-form-urlencoded`

**Body配置：**
- Type: `x-www-form-urlencoded`
- 添加以下键值对：
  - `resumeFilePath`: [第一步返回的文件URL]
  - `jobPosition`: `Java Developer`
  - `skills`: `Java, Spring`
  - `experience`: `ONE_TO_THREE_YEARS`
  - `questionCount`: `5`
  - `difficultyLevel`: `INTERMEDIATE`
  - `focusArea`: `微服务`

## cURL测试命令

### 1. 上传简历文件
```bash
curl -X POST http://localhost:8082/api/file/upload-resume \
  -F "file=@E:pdf/简历.pdf"
```

### 2. 生成试题（使用第一步返回的URL）
```bash
curl -X POST http://localhost:8082/api/exam/generate2 \
  -d "resumeFilePath=/api/file/download/resume/123/abc123.pdf" \
  -d "jobPosition=Java Developer" \
  -d "skills=Java, Spring" \
  -d "experience=ONE_TO_THREE_YEARS" \
  -d "questionCount=5" \
  -d "difficultyLevel=INTERMEDIATE" \
  -d "focusArea=微服务" \
  -H "Content-Type: application/x-www-form-urlencoded"
```

## JavaScript测试代码

```javascript
// 第一步：上传简历文件
async function uploadResume(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch('http://localhost:8082/api/file/upload-resume', {
    method: 'POST',
    body: formData
  });
  
  const result = await response.json();
  console.log('文件上传结果:', result);
  return result.data; // 返回文件URL
}

// 第二步：生成试题
async function generateExam(resumeFilePath, params) {
  const formData = new URLSearchParams();
  formData.append('resumeFilePath', resumeFilePath);
  formData.append('jobPosition', params.jobPosition);
  formData.append('skills', params.skills);
  formData.append('experience', params.experience);
  formData.append('questionCount', params.questionCount);
  formData.append('difficultyLevel', params.difficultyLevel);
  formData.append('focusArea', params.focusArea);
  
  const response = await fetch('http://localhost:8082/api/exam/generate2', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: formData
  });
  
  const result = await response.json();
  console.log('试题生成结果:', result);
  return result;
}

// 使用示例
async function testResumeExam() {
  try {
    // 1. 上传文件
    const fileInput = document.getElementById('resumeFile');
    const fileUrl = await uploadResume(fileInput.files[0]);
    
    // 2. 生成试题
    const examParams = {
      jobPosition: 'Java Developer',
      skills: 'Java, Spring',
      experience: 'ONE_TO_THREE_YEARS',
      questionCount: 5,
      difficultyLevel: 'INTERMEDIATE',
      focusArea: '微服务'
    };
    
    const examResult = await generateExam(fileUrl, examParams);
    console.log('完整流程成功:', examResult);
  } catch (error) {
    console.error('测试失败:', error);
  }
}
```

## 文件路径转换说明

### 上传文件返回的URL格式
```
/api/file/download/resume/123/abc123.pdf
```

### 系统自动转换为实际文件路径
```
E:/java_code/rjb_oj/easyojandlogin/src/main/resources/txt/resume/123/abc123.pdf
```

### 转换逻辑
1. 移除URL前缀 `/api/file/download/`
2. 添加本地存储根路径
3. 读取文件内容并转换为base64
4. 发送给Dify API

## 常见问题

### 1. 文件上传失败
**可能原因：**
- 文件格式不支持
- 文件大小超限
- 用户未登录

**解决方案：**
- 检查文件格式（PDF、DOC、DOCX、TXT、RTF）
- 确保文件小于10MB
- 确保用户已登录

### 2. 试题生成失败
**可能原因：**
- 文件路径解析错误
- Dify API连接失败
- 文件不存在

**解决方案：**
- 检查文件URL是否正确
- 确保Dify API可访问
- 检查文件是否真实存在

### 3. 参数错误
**可能原因：**
- 参数名称错误
- 参数值格式错误

**解决方案：**
- 确保参数名称正确
- 确保枚举值正确（如`ONE_TO_THREE_YEARS`）

## 调试建议

### 1. 检查日志
启动应用后查看控制台日志，确认：
- 文件上传是否成功
- 文件路径转换是否正确
- Dify API调用是否成功

### 2. 分步测试
1. 先测试文件上传接口
2. 再测试试题生成接口
3. 最后测试完整流程

### 3. 检查文件
确保上传的文件：
- 格式正确
- 内容可读
- 大小合适 