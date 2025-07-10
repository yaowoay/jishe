# 前端集成指南 - 简历上传和试题生成

## 完整流程

### 1. 前端界面设计
```
┌─────────────────────────────────────┐
│ 简历上传和试题生成                  │
├─────────────────────────────────────┤
│ 1. 上传简历文件                    │
│    [选择文件] [上传]               │
│                                     │
│ 2. 设置试题参数                    │
│    职位: [Java Developer]          │
│    技能: [Java, Spring]            │
│    经验: [1-3年]                   │
│    题目数量: [5]                   │
│    难度: [中级]                    │
│    重点领域: [微服务]              │
│                                     │
│ 3. 生成试题                        │
│    [生成试题]                      │
└─────────────────────────────────────┘
```

## 前端代码实现

### 1. HTML结构
```html
<!DOCTYPE html>
<html>
<head>
    <title>简历上传和试题生成</title>
</head>
<body>
    <div class="container">
        <h2>简历上传和试题生成</h2>
        
        <!-- 第一步：上传简历 -->
        <div class="upload-section">
            <h3>1. 上传简历文件</h3>
            <input type="file" id="resumeFile" accept=".pdf,.doc,.docx,.txt,.rtf">
            <button onclick="uploadResume()">上传简历</button>
            <div id="uploadResult"></div>
        </div>
        
        <!-- 第二步：设置参数 -->
        <div class="params-section">
            <h3>2. 设置试题参数</h3>
            <div class="form-group">
                <label>职位:</label>
                <input type="text" id="jobPosition" value="Java Developer">
            </div>
            <div class="form-group">
                <label>技能:</label>
                <input type="text" id="skills" value="Java, Spring">
            </div>
            <div class="form-group">
                <label>经验:</label>
                <select id="experience">
                    <option value="FRESH_GRADUATE">应届毕业生</option>
                    <option value="ONE_TO_THREE_YEARS" selected>1-3年</option>
                    <option value="THREE_TO_FIVE_YEARS">3-5年</option>
                    <option value="FIVE_TO_TEN_YEARS">5-10年</option>
                    <option value="MORE_THAN_TEN_YEARS">10年以上</option>
                </select>
            </div>
            <div class="form-group">
                <label>题目数量:</label>
                <input type="number" id="questionCount" value="5" min="1" max="20">
            </div>
            <div class="form-group">
                <label>难度:</label>
                <select id="difficultyLevel">
                    <option value="BEGINNER">初级</option>
                    <option value="INTERMEDIATE" selected>中级</option>
                    <option value="ADVANCED">高级</option>
                    <option value="EXPERT">专家级</option>
                </select>
            </div>
            <div class="form-group">
                <label>重点领域:</label>
                <input type="text" id="focusArea" value="微服务">
            </div>
        </div>
        
        <!-- 第三步：生成试题 -->
        <div class="generate-section">
            <h3>3. 生成试题</h3>
            <button onclick="generateExam()" id="generateBtn" disabled>生成试题</button>
            <div id="examResult"></div>
        </div>
    </div>
</body>
</html>
```

### 2. JavaScript实现
```javascript
// 全局变量存储上传的文件URL
let uploadedFileUrl = null;

// 第一步：上传简历文件
async function uploadResume() {
    const fileInput = document.getElementById('resumeFile');
    const uploadResult = document.getElementById('uploadResult');
    const generateBtn = document.getElementById('generateBtn');
    
    if (!fileInput.files[0]) {
        alert('请选择简历文件');
        return;
    }
    
    try {
        // 显示上传中状态
        uploadResult.innerHTML = '<p style="color: blue;">正在上传文件...</p>';
        
        const formData = new FormData();
        formData.append('file', fileInput.files[0]);
        
        const response = await fetch('http://localhost:8082/api/file/upload-resume', {
            method: 'POST',
            body: formData
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            uploadedFileUrl = result.data;
            uploadResult.innerHTML = `<p style="color: green;">文件上传成功: ${result.data}</p>`;
            generateBtn.disabled = false; // 启用生成按钮
        } else {
            uploadResult.innerHTML = `<p style="color: red;">上传失败: ${result.message}</p>`;
        }
    } catch (error) {
        uploadResult.innerHTML = `<p style="color: red;">上传失败: ${error.message}</p>`;
        console.error('上传失败:', error);
    }
}

// 第二步：生成试题
async function generateExam() {
    const examResult = document.getElementById('examResult');
    const generateBtn = document.getElementById('generateBtn');
    
    if (!uploadedFileUrl) {
        alert('请先上传简历文件');
        return;
    }
    
    try {
        // 显示生成中状态
        examResult.innerHTML = '<p style="color: blue;">正在生成试题...</p>';
        generateBtn.disabled = true;
        
        // 获取表单参数
        const params = {
            resumeFilePath: uploadedFileUrl,
            jobPosition: document.getElementById('jobPosition').value,
            skills: document.getElementById('skills').value,
            experience: document.getElementById('experience').value,
            questionCount: document.getElementById('questionCount').value,
            difficultyLevel: document.getElementById('difficultyLevel').value,
            focusArea: document.getElementById('focusArea').value
        };
        
        // 构造请求数据
        const formData = new URLSearchParams();
        Object.keys(params).forEach(key => {
            formData.append(key, params[key]);
        });
        
        const response = await fetch('http://localhost:8082/api/exam/generate2', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: formData
        });
        
        const result = await response.json();
        
        if (result.task_id) {
            // 生成成功，显示试题
            displayExamResult(result);
        } else {
            examResult.innerHTML = `<p style="color: red;">生成失败: ${JSON.stringify(result)}</p>`;
        }
    } catch (error) {
        examResult.innerHTML = `<p style="color: red;">生成失败: ${error.message}</p>`;
        console.error('生成失败:', error);
    } finally {
        generateBtn.disabled = false;
    }
}

// 显示试题结果
function displayExamResult(result) {
    const examResult = document.getElementById('examResult');
    
    let html = '<div style="border: 1px solid #ccc; padding: 10px; margin: 10px 0;">';
    html += `<h4>试题生成成功 (Task ID: ${result.task_id})</h4>`;
    
    if (result.data && result.data.outputs && result.data.outputs.questions) {
        const questions = result.data.outputs.questions.questions || [];
        
        questions.forEach((question, index) => {
            html += `<div style="margin: 20px 0; padding: 10px; background: #f9f9f9;">`;
            html += `<h5>题目 ${index + 1}</h5>`;
            html += `<p><strong>类型:</strong> ${question.type}</p>`;
            html += `<p><strong>题目:</strong> ${question.question}</p>`;
            
            if (question.options) {
                html += `<p><strong>选项:</strong></p><ul>`;
                question.options.forEach(option => {
                    html += `<li>${option}</li>`;
                });
                html += `</ul>`;
            }
            
            if (question.correct_answer) {
                html += `<p><strong>正确答案:</strong> ${question.correct_answer}</p>`;
            }
            
            if (question.explanation) {
                html += `<p><strong>解释:</strong> ${question.explanation}</p>`;
            }
            
            if (question.requirements) {
                html += `<p><strong>要求:</strong></p><ul>`;
                question.requirements.forEach(req => {
                    html += `<li>${req}</li>`;
                });
                html += `</ul>`;
            }
            
            html += `</div>`;
        });
    } else {
        html += '<p>暂无试题内容</p>';
    }
    
    html += '</div>';
    examResult.innerHTML = html;
}

// 页面加载时的初始化
window.onload = function() {
    // 可以在这里添加一些初始化逻辑
    console.log('页面加载完成，可以开始上传简历和生成试题');
};
```

### 3. CSS样式
```css
.container {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
    font-family: Arial, sans-serif;
}

.upload-section, .params-section, .generate-section {
    margin: 20px 0;
    padding: 15px;
    border: 1px solid #ddd;
    border-radius: 5px;
}

.form-group {
    margin: 10px 0;
}

.form-group label {
    display: inline-block;
    width: 100px;
    font-weight: bold;
}

.form-group input, .form-group select {
    width: 200px;
    padding: 5px;
    border: 1px solid #ccc;
    border-radius: 3px;
}

button {
    background: #007bff;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 3px;
    cursor: pointer;
    margin: 5px;
}

button:hover {
    background: #0056b3;
}

button:disabled {
    background: #ccc;
    cursor: not-allowed;
}

#uploadResult, #examResult {
    margin: 10px 0;
    padding: 10px;
    border-radius: 3px;
}
```

## 测试步骤

### 1. 启动后端服务
确保后端服务在8082端口运行：
```bash
mvn spring-boot:run
```

### 2. 创建前端页面
将上述HTML、JavaScript、CSS代码保存为`resume-exam.html`

### 3. 测试流程
1. 打开`resume-exam.html`
2. 选择简历文件并点击"上传简历"
3. 设置试题参数
4. 点击"生成试题"
5. 查看生成的试题结果

## 预期结果

### 成功流程
1. **文件上传成功**：显示绿色的成功消息和文件URL
2. **参数设置**：所有参数正确填写
3. **试题生成**：显示生成的试题列表，包含题目、选项、答案等

### 可能的错误
1. **文件上传失败**：检查文件格式和大小
2. **参数错误**：检查参数名称和值
3. **网络错误**：检查后端服务是否正常运行
4. **API错误**：检查Dify API连接

## 调试建议

### 1. 浏览器开发者工具
- 打开F12查看Network标签
- 检查请求和响应
- 查看Console错误信息

### 2. 后端日志
- 查看Spring Boot控制台输出
- 检查文件上传和试题生成的日志

### 3. 分步测试
1. 先测试文件上传功能
2. 再测试参数传递
3. 最后测试完整流程

## 注意事项

1. **跨域问题**：如果前端和后端不在同一域名，需要配置CORS
2. **文件大小**：确保文件不超过10MB
3. **文件格式**：只支持PDF、DOC、DOCX、TXT、RTF
4. **网络连接**：确保能访问Dify API
5. **用户登录**：确保用户已登录（如果需要）

这样前端就能正确实现简历上传和试题生成的完整流程了！ 