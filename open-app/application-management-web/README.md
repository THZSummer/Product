# 应用管理 Web 端 (application-management-web)

## 简介

这是 open-app 平台的应用管理 Web 前端模块，为应用开发者提供可视化的应用管理控制台。

## 技术栈

- **React** 18.x - UI 框架
- **TypeScript** 5.x - 类型系统
- **Vite** 5.x - 构建工具
- **Ant Design** 5.x - UI 组件库
- **Zustand** 4.x - 状态管理
- **React Router** 6.x - 路由
- **Axios** 1.x - HTTP 客户端
- **DayJS** 1.x - 日期处理
- **MSW** - API Mock

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

访问 http://localhost:5173

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

### 代码检查

```bash
# TypeScript 类型检查
npm run type-check

# ESLint 检查
npm run lint

# ESLint 自动修复
npm run lint:fix

# 代码格式化
npm run format
```

## 项目结构

```
application-management-web/
├── public/                  # 静态资源
├── src/
│   ├── assets/             # 项目资源
│   ├── components/         # 公共组件
│   ├── layouts/            # 布局组件
│   ├── pages/              # 页面组件
│   ├── stores/             # 状态管理
│   ├── services/           # API 服务层
│   ├── types/              # TypeScript 类型
│   ├── utils/              # 工具函数
│   ├── mocks/              # MSW Mock
│   ├── App.tsx             # 根组件
│   ├── main.tsx            # 入口文件
│   └── routes.tsx          # 路由配置
├── package.json
├── tsconfig.json
├── vite.config.ts
└── README.md
```

## 功能特性

- ✅ 应用列表展示（搜索、筛选、分页）
- ✅ 应用创建
- ✅ 应用详情查看
- ✅ 应用编辑
- ✅ 应用删除（软删除）
- ✅ 响应式布局

## 开发规范

### 命名规范

- **文件/文件夹**: PascalCase (组件), camelCase (工具)
- **组件**: 导出名称与文件名一致
- **类型**: PascalCase
- **常量**: UPPER_SNAKE_CASE
- **路径别名**: 使用 `@/` 和子别名

### 代码规范

- 使用 TypeScript 严格模式
- 禁止使用 `any` 类型（特殊情况需注释）
- 组件使用函数式写法
- 使用 ES Module 导入导出
- 遵循 ESLint 和 Prettier 规则

### Git 提交规范

```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式调整
refactor: 重构代码
test: 测试相关
chore: 构建/工具链相关
```

## 环境要求

- Node.js >= 20 LTS
- npm >= 10
- 现代浏览器 (Chrome 90+, Edge 90+, Safari 14+, Firefox 88+)

## 相关文档

- [功能规范](../../.specs/application-management-web/spec.md)
- [技术计划](../../.specs/application-management-web/plan.md)
- [任务分解](../../.specs/application-management-web/tasks.md)

## License

Proprietary (open-app project)
