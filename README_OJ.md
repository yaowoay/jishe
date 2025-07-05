# OJ判题系统后端实现

## 概述

这是一个简单的OJ（Online Judge）判题系统后端实现，支持题目管理、代码运行和判题功能。

## 功能特性

### 1. 题目管理
- 题目列表获取
- 题目详情查看
- 题目收藏/取消收藏
- 代码模板获取

### 2. 代码判题
- 代码运行（模拟）
- 代码提交判题
- 支持Python、Java、C语言

### 3. 数据库设计
- problem表：题目信息
- example表：题目示例
- language表：编程语言
- solution表：标准答案代码

## 数据库初始化

执行以下SQL脚本初始化数据库：

```sql
-- 执行 sql/oj_init.sql 文件
```

## API接口说明

### 题目相关接口

#### 1. 获取题目列表
```
GET /api/problems
```

响应示例：
```json
{
  "code": 0,
  "data": [
    {
      "id": 1,
      "title": "两数之和",
      "description": "给定一个整数数组...",
      "isFavorite": false,
      "status": "通过"
    }
  ],
  "message": "ok"
}
```

#### 2. 获取题目详情
```
GET /api/problems/{problemId}
```

响应示例：
```json
{
  "code": 0,
  "data": {
    "id": 1,
    "title": "两数之和",
    "description": "给定一个整数数组...",
    "isFavorite": false,
    "status": "通过",
    "examples": [
      {
        "id": 1,
        "input": "nums = [2,7,11,15], target = 9",
        "output": "[0,1]"
      }
    ]
  },
  "message": "ok"
}
```

#### 3. 收藏题目
```
POST /api/problems/{problemId}/favorite
```

#### 4. 取消收藏
```
POST /api/problems/{problemId}/unfavorite
```

#### 5. 获取代码模板
```
GET /api/problems/{problemId}/template?language=python
```

### 判题相关接口

#### 1. 运行代码
```
POST /api/oj/run
```

请求体：
```json
{
  "problemId": 1,
  "code": "def solution():\n    return [0,1]",
  "language": "python"
}
```

响应示例：
```json
{
  "success": true,
  "output": "Hello World",
  "error": null,
  "time": 100,
  "memory": 1024
}
```

#### 2. 提交代码判题
```
POST /api/oj/submit
```

请求体：
```json
{
  "problemId": 1,
  "code": "def two_sum(nums, target):\n    for i in range(len(nums)):\n        for j in range(i+1, len(nums)):\n            if nums[i] + nums[j] == target:\n                return [i, j]\n    return []",
  "language": "python"
}
```

响应示例：
```json
{
  "result": "通过",
  "message": "恭喜！代码通过所有测试用例",
  "passedCases": 2,
  "totalCases": 2,
  "time": 150,
  "memory": 2048
}
```

## 判题逻辑

当前实现采用简单的代码相似度比较：

1. 获取题目的标准答案代码
2. 计算用户代码与标准答案的相似度
3. 如果相似度超过70%，认为通过
4. 使用编辑距离算法计算相似度

## 扩展建议

### 1. 真实代码执行
- 集成Docker容器运行代码
- 实现代码沙箱安全机制
- 支持更多编程语言

### 2. 测试用例判题
- 运行用户代码与测试用例
- 比较输出结果
- 支持多种输入格式

### 3. 性能优化
- 代码执行超时控制
- 内存使用限制
- 并发判题队列

### 4. 用户系统
- 用户登录注册
- 个人提交记录
- 排行榜功能

## 文件结构

```
src/main/java/com/coldwind/easyoj/
├── controller/
│   ├── ProblemController.java    # 题目控制器
│   └── OjController.java         # 判题控制器
├── service/
│   ├── ProblemService.java       # 题目服务接口
│   ├── impl/
│   │   ├── ProblemServiceImpl.java  # 题目服务实现
│   │   └── OjServiceImpl.java       # 判题服务实现
│   └── OjService.java            # 判题服务接口
├── mapper/
│   ├── ProblemMapper.java        # 题目数据访问
│   ├── ExampleMapper.java        # 示例数据访问
│   └── SolutionMapper.java       # 解答数据访问
├── model/
│   ├── entity/
│   │   ├── Problem.java          # 题目实体
│   │   ├── Example.java          # 示例实体
│   │   ├── Language.java         # 语言实体
│   │   └── Solution.java         # 解答实体
│   ├── dto/
│   │   ├── request/
│   │   │   ├── ProblemRunRequest.java
│   │   │   └── ProblemSubmitRequest.java
│   │   └── response/
│   │       ├── ProblemRunResponse.java
│   │       └── ProblemSubmitResponse.java
│   └── vo/
│       ├── ProblemVO.java        # 题目视图对象
│       └── ExampleVO.java        # 示例视图对象
└── common/
    └── BaseResponse.java         # 统一响应格式
```

## 使用说明

1. 执行数据库初始化脚本
2. 启动Spring Boot应用
3. 前端调用相应API接口
4. 根据需要进行功能扩展

## 注意事项

1. 当前判题逻辑较为简单，仅用于演示
2. 生产环境需要实现真实的代码执行环境
3. 注意代码执行的安全性，防止恶意代码
4. 建议添加用户认证和权限控制 