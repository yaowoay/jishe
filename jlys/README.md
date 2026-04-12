<<<<<<< HEAD
# AI面试系统

## 数字人视频流集成

### 后端接口响应格式

当调用面试接口时，后端可以返回数字人视频流配置：

```json
{
  "aiReply": "你好！我是你的AI面试官，请介绍一下你自己。",
  "aiRefAnswer": "AI参考答案：请保持自信，清晰表达。",
  "aiHint": "请注意语气平和，保持微笑。",
  "aiVideoStream": {
    "sid": "vms000ec4da@dx195f094539d6f19882",
    "server": "https://xrtc-cn-east-2.xf-yun.com",
    "auth": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "appid": "1000000001",
    "userId": "123123123",
    "roomId": "ase0001bbe2hu19632f0f6070442142",
    "timeStr": "123412341324"
  }
}
```

### 实时数字人展示

1. **自动初始化**：页面加载时自动连接数字人视频流
2. **动态更新**：每次调用后端接口时，如果返回新的视频流配置，会自动更新数字人
3. **实时交互**：数字人会根据对话内容实时展示相应的表情和动作

### 技术实现

- 使用 SRS RTCPlayer 播放数字人视频流
- 支持 XRTC 协议
- 自动重连和错误处理
- 兼容旧版本配置

## 功能特性

- ✅ 实时数字人视频流
- ✅ 视频窗口交换功能
- ✅ 语音识别和对话
- ✅ AI面试官智能回复
- ✅ 面试计时和评估

### AI 模拟面试
- 音频模拟面试功能
- 简历上传和分析
- 智能面试对话

### AI 智能笔试
- **笔试前设置**：用户可以根据职位、技能、经验等个性化设置
- **智能题目生成**：根据用户设置自动生成相应的笔试题目
- **实时判题**：提交后立即显示答题结果
- **详细解析**：提供知识点、场景、易错点等详细说明

### 其他功能
- OJ 系统
- 笔记管理
- 个人中心

## 笔试设置功能

新增的笔试设置页面 (`BeforeWritten.vue`) 包含以下字段：

- **职位名称**：目标职位，如 "Java Developer"
- **技能要求**：相关技能，用逗号分隔，如 "Java, Spring, MySQL"
- **工作经验**：下拉选择，从应届毕业生到10年以上
- **题目数量**：3-20道题目可选
- **难度等级**：初级、中级、高级、专家级
- **重点领域**：重点考察领域，如 "微服务、数据库、算法"

### 使用流程

1. 在 AI Chat 主页点击"开始笔试"
2. 进入笔试设置页面，填写相关信息
3. 点击"开始笔试"，系统根据设置生成题目
4. 跳转到笔试页面进行答题
5. 提交后查看答题结果和详细解析

## 技术栈

- Vue 3 (Composition API)
- Vue Router 4
- Element Plus
- Axios

## 开发

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run serve

# 构建生产版本
npm run build
```

## 项目结构

```
src/
├── components/
│   └── functions/
│       └── AiInterview/
│           ├── AiChat.vue          # AI面试主页
│           ├── BeforeWritten.vue   # 笔试设置页面
│           ├── WrittenExam.vue     # 笔试答题页面
│           └── ...
├── router/
│   └── index.js                    # 路由配置
└── utils/
    └── request.js                  # HTTP请求工具
```

## API 接口

### 笔试相关接口

- `POST /api/writtenExam/generate` - 根据设置生成题目
- `GET /api/writtenExam/questions/{examId}` - 获取生成的题目
- `POST /api/writtenExam/submit` - 提交答题结果
=======

# SpringBoot 项目初始模板

> 作者：[程序员鱼皮](https://github.com/liyupi)
> 仅分享于 [编程导航知识星球](https://yupi.icu)

基于 Java SpringBoot 的项目初始模板，整合了常用框架和主流业务的示例代码。

只需 1 分钟即可完成内容网站的后端！！！大家还可以在此基础上快速开发自己的项目。

[toc]

## 模板特点

### 主流框架 & 特性

- Spring Boot 2.7.x（贼新）
- Spring MVC
- MyBatis + MyBatis Plus 数据访问（开启分页）
- Spring Boot 调试工具和项目处理器
- Spring AOP 切面编程
- Spring Scheduler 定时任务
- Spring 事务注解

### 数据存储

- MySQL 数据库
- Redis 内存数据库
- Elasticsearch 搜索引擎
- 腾讯云 COS 对象存储

### 工具类

- Easy Excel 表格处理
- Hutool 工具库
- Gson 解析库
- Apache Commons Lang3 工具类
- Lombok 注解

### 业务特性

- Spring Session Redis 分布式登录
- 全局请求响应拦截器（记录日志）
- 全局异常处理器
- 自定义错误码
- 封装通用响应类
- Swagger + Knife4j 接口文档
- 自定义权限注解 + 全局校验
- 全局跨域处理
- 长整数丢失精度解决
- 多环境配置


## 业务功能

- 提供示例 SQL（用户、帖子、帖子点赞、帖子收藏表）
- 用户登录、注册、注销、更新、检索、权限管理
- 帖子创建、删除、编辑、更新、数据库检索、ES 灵活检索
- 帖子点赞、取消点赞
- 帖子收藏、取消收藏、检索已收藏帖子
- 帖子全量同步 ES、增量同步 ES 定时任务
- 支持微信开放平台登录
- 支持微信公众号订阅、收发消息、设置菜单
- 支持分业务的文件上传

### 单元测试

- JUnit5 单元测试
- 示例单元测试类

### 架构设计

- 合理分层


## 快速上手

> 所有需要修改的地方鱼皮都标记了 `todo`，便于大家找到修改的位置~

### MySQL 数据库

1）修改 `application.yml` 的数据库配置为你自己的：

```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/my_db
    username: root
    password: 123456
```

2）执行 `sql/create_table.sql` 中的数据库语句，自动创建库表

3）启动项目，访问 `http://localhost:8101/api/doc.html` 即可打开接口文档，不需要写前端就能在线调试接口了~

![](doc/swagger.png)

### Redis 分布式登录

1）修改 `application.yml` 的 Redis 配置为你自己的：

```yml
spring:
  redis:
    database: 1
    host: localhost
    port: 6379
    timeout: 5000
    password: 123456
```

2）修改 `application.yml` 中的 session 存储方式：

```yml
spring:
  session:
    store-type: redis
```

3）移除 `MainApplication` 类开头 `@SpringBootApplication` 注解内的 exclude 参数：

修改前：

```java
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
```

修改后：


```java
@SpringBootApplication
```

### Elasticsearch 搜索引擎

1）修改 `application.yml` 的 Elasticsearch 配置为你自己的：

```yml
spring:
  elasticsearch:
    uris: http://localhost:9200
    username: root
    password: 123456
```

2）复制 `sql/post_es_mapping.json` 文件中的内容，通过调用 Elasticsearch 的接口或者 Kibana Dev Tools 来创建索引（相当于数据库建表）

```
PUT post_v1
{
 参数见 sql/post_es_mapping.json 文件
}
```

这步不会操作的话需要补充下 Elasticsearch 的知识，或者自行百度一下~

3）开启同步任务，将数据库的帖子同步到 Elasticsearch

找到 job 目录下的 `FullSyncPostToEs` 和 `IncSyncPostToEs` 文件，取消掉 `@Component` 注解的注释，再次执行程序即可触发同步：

```java
// todo 取消注释开启任务
//@Component
```
# softwarecup
c1cd94e6144ff53bfb6c84a5142572a827d7571d
>>>>>>> 13a5362dd6628930742c562c0d253c663040ecaf
