# 登录跳转问题修复验证

## 问题描述
用户登录后无法跳转到主页，停留在登录页面。

## 根本原因
前后端角色值不一致：
- 前端Login.vue默认角色值为 `'student'`
- 前端路由守卫期望角色为 `'applicant'`
- 后端UserServiceImpl处理 `'student'` 角色
- 导致角色匹配失败，无法正确跳转

## 修复内容

### 1. 前端Login.vue修复
- ✅ 将默认角色从 `'student'` 改为 `'applicant'`
- ✅ 角色选择器已正确使用 `'applicant'`

### 2. 前端Register.vue修复  
- ✅ 将角色选项从 `'student'` 改为 `'applicant'`
- ✅ 将默认角色从 `'student'` 改为 `'applicant'`
- ✅ 更新条件判断逻辑

### 3. 后端UserServiceImpl修复
- ✅ 将角色判断从 `"student"` 改为 `"applicant"`

### 4. 数据库数据
- ✅ 确认data.sql中已使用正确的 `'applicant'` 角色

## 验证步骤

1. 启动后端服务
2. 启动前端服务  
3. 访问注册页面，选择"学生"角色注册新用户
4. 使用新注册的用户登录
5. 验证是否能正确跳转到 `/applicant/dashboard`

## 预期结果
- 注册时发送 `role: 'applicant'`
- 登录时返回 `role: 'applicant'`  
- 路由守卫匹配成功，跳转到 `/applicant/dashboard`