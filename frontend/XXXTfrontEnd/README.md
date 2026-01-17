# 校园平台前端项目

基于 Vue 3 + TypeScript + Pinia + Vue Router 的前端项目。

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **TypeScript** - 类型安全的 JavaScript
- **Pinia** - Vue 的状态管理库
- **Vue Router** - Vue 的官方路由管理器
- **Axios** - HTTP 客户端
- **Vite** - 下一代前端构建工具

## 项目结构

```
src/
├── api/              # API 接口定义
│   ├── student.ts   # 学生相关接口
│   ├── post.ts      # 帖子相关接口
│   ├── comment.ts   # 评论相关接口
│   └── file.ts      # 文件相关接口
├── components/       # 公共组件
│   ├── Header.vue   # 头部组件
│   ├── Footer.vue   # 底部组件
│   ├── PostCard.vue # 帖子卡片组件
│   └── CommentItem.vue # 评论组件
├── stores/          # Pinia stores
│   ├── user.ts      # 用户状态管理
│   └── config.ts    # 全局配置管理
├── views/           # 页面组件
│   ├── HomeView.vue      # 首页
│   ├── LoginView.vue     # 登录页
│   ├── RegisterView.vue   # 注册页
│   ├── PostsView.vue     # 帖子列表页
│   ├── PostDetailView.vue # 帖子详情页
│   ├── CreatePostView.vue # 发布帖子页
│   ├── MyPostsView.vue    # 我的帖子页
│   └── ProfileView.vue    # 个人中心页
├── router/          # 路由配置
│   └── index.ts
├── utils/           # 工具函数
│   └── request.ts   # Axios 请求封装
└── App.vue          # 根组件
```

## 功能特性

### 用户功能
- ✅ 用户注册/登录
- ✅ 邮箱验证码
- ✅ 用户信息管理
- ✅ 密码修改
- ✅ 信誉分显示

### 帖子功能
- ✅ 帖子列表（支持分类筛选）
- ✅ 帖子详情
- ✅ 发布帖子（支持图片上传）
- ✅ 编辑/删除帖子
- ✅ 点赞功能

### 评论功能
- ✅ 评论列表
- ✅ 发表评论
- ✅ 回复评论（支持嵌套）
- ✅ 编辑/删除评论

### 文件功能
- ✅ 图片上传
- ✅ 文件访问

## 安装依赖

```bash
npm install
```

## 开发

```bash
npm run dev
```

项目将在 `http://localhost:5173` 启动。

## 构建

```bash
npm run build
```

## 环境配置

创建 `.env` 文件（如果不存在）：

```env
VITE_API_BASE_URL=http://localhost:4096/zxk
VITE_APP_TITLE=校园平台
```

## API 接口说明

### 基础 URL
- 开发环境: `http://localhost:4096/zxk`
- 生产环境: 根据实际部署情况配置

### 认证方式
所有需要认证的接口都需要在请求头中添加：
```
Authorization: Bearer {accessToken}
```

### 主要接口

#### 学生相关
- `POST /student/login` - 登录
- `POST /student/register` - 注册
- `POST /student/getcode` - 发送验证码
- `GET /student/userinfo` - 获取用户信息
- `PUT /student/update` - 更新用户信息
- `PUT /student/password/change` - 修改密码

#### 帖子相关
- `GET /post/list` - 获取所有已发布帖子
- `POST /post/getbyid` - 根据ID查询帖子
- `POST /post/getbycategory` - 根据分类查询帖子
- `POST /post/create` - 创建帖子
- `PUT /post/update` - 更新帖子
- `PUT /post/delete` - 删除帖子
- `PUT /post/like` - 点赞/取消点赞

#### 评论相关
- `POST /comment/getbypost` - 根据帖子ID查询评论
- `POST /comment/create` - 创建评论
- `PUT /comment/update` - 更新评论
- `PUT /comment/delete` - 删除评论

#### 文件相关
- `POST /file/upload` - 上传文件
- `GET /file/download/**` - 下载/访问文件
- `DELETE /file/delete` - 删除文件

## 路由说明

| 路径 | 名称 | 说明 | 需要登录 |
|------|------|------|---------|
| `/` | home | 首页 | 否 |
| `/login` | login | 登录页 | 否 |
| `/register` | register | 注册页 | 否 |
| `/posts` | posts | 帖子列表 | 否 |
| `/post/:id` | post-detail | 帖子详情 | 否 |
| `/create-post` | create-post | 发布帖子 | 是 |
| `/my-posts` | my-posts | 我的帖子 | 是 |
| `/profile` | profile | 个人中心 | 是 |

## 状态管理

### User Store (`stores/user.ts`)
管理用户登录状态和信息：
- `token` - 访问令牌
- `userInfo` - 用户信息
- `isLoggedIn` - 是否已登录
- `login()` - 登录
- `logout()` - 登出
- `fetchUserInfo()` - 获取用户信息
- `updateUserInfo()` - 更新用户信息

### Config Store (`stores/config.ts`)
管理全局配置：
- `categories` - 帖子分类
- `statuses` - 状态列表
- `getCategoryName()` - 获取分类名称
- `getStatusName()` - 获取状态名称

## 注意事项

1. **Token 管理**: Token 会自动保存在 localStorage，并在请求时自动添加到请求头
2. **路由守卫**: 需要登录的页面会自动跳转到登录页
3. **错误处理**: API 请求失败会自动显示错误提示
4. **文件上传**: 支持图片上传，最大 50MB
5. **响应式设计**: 所有页面都支持响应式布局

## 开发规范

1. 使用 TypeScript 进行类型定义
2. 组件使用 `<script setup>` 语法
3. 使用 Composition API
4. 遵循 Vue 3 最佳实践
5. 代码格式化使用 ESLint/Prettier（如已配置）

## 后续优化建议

1. 添加图片预览功能
2. 添加富文本编辑器
3. 添加消息通知系统
4. 添加搜索功能
5. 添加分页功能
6. 优化移动端体验
7. 添加骨架屏加载
8. 添加错误边界处理
