# Task Breakdown: 应用管理 Web 端

## 元数据

| 字段 | 值 |
|------|------|
| **关联 Spec** | `APP-MGT-WEB-001` |
| **关联 Plan** | `application-management-web/plan.md v1.0.1` |
| **版本** | 1.0.0 |
| **创建日期** | 2026-03-23 |
| **总任务数** | 12 |
| **预估工期** | 10 天 |

---

## 任务汇总

### 按复杂度
| 复杂度 | 数量 | 执行策略 |
|--------|------|----------|
| **S** | 5 | 批量执行 |
| **M** | 5 | 逐个执行 |
| **L** | 2 | 需要监督 |

### 按波次
| 波次 | 数量 | 说明 |
|------|------|------|
| **Wave 1** | 3 | 无依赖，可并行 |
| **Wave 2** | 3 | 依赖 Wave 1 |
| **Wave 3** | 3 | 依赖 Wave 2 |
| **Wave 4** | 2 | 依赖 Wave 3 |
| **Wave 5** | 1 | 集成测试 |

---

## 任务详情

## TASK-001: 项目初始化与基础配置

**复杂度**: M
**前置依赖**: 无
**执行波次**: 1

### 描述
创建 Vite + React + TypeScript 项目骨架，配置开发环境和基础工具链。

### 涉及文件
- [NEW] `application-management-web/package.json`
- [NEW] `application-management-web/tsconfig.json`
- [NEW] `application-management-web/tsconfig.node.json`
- [NEW] `application-management-web/vite.config.ts`
- [NEW] `application-management-web/index.html`
- [NEW] `application-management-web/.gitignore`
- [NEW] `application-management-web/.eslintrc.cjs`
- [NEW] `application-management-web/.prettierrc`
- [NEW] `application-management-web/.env`
- [NEW] `application-management-web/.env.development`
- [NEW] `application-management-web/.env.production`
- [NEW] `application-management-web/README.md`

### 验收标准
- [ ] `npm install` 执行成功
- [ ] `npm run dev` 启动开发服务器无错误
- [ ] `npm run build` 构建生产版本无错误
- [ ] TypeScript 类型检查通过 (`npm run type-check`)
- [ ] ESLint 检查无错误 (`npm run lint`)

### 验证命令
```bash
cd application-management-web
npm install
npm run dev
npm run build
npm run type-check
npm run lint
```

---

## TASK-002: 类型定义层

**复杂度**: S
**前置依赖**: 无
**执行波次**: 1

### 描述
创建 TypeScript 类型定义，包括应用实体、API 请求/响应类型、枚举类型。

### 涉及文件
- [NEW] `application-management-web/src/types/application.ts`
- [NEW] `application-management-web/src/types/api.ts`
- [NEW] `application-management-web/src/types/index.ts`

### 验收标准
- [ ] `AppStatus` 枚举包含 4 个状态 (draft/active/disabled/deleted)
- [ ] `AppType` 枚举包含 3 个类型 (self_build/third_party/personal)
- [ ] `Application` 接口包含所有必填字段
- [ ] `ListParams`、`CreateAppData`、`UpdateAppData` 接口定义完整
- [ ] API 响应类型 `ApiResponse<T>` 泛型定义正确
- [ ] 类型导出正确，无循环依赖

### 验证命令
```bash
cd application-management-web
npx tsc --noEmit --project tsconfig.json
```

---

## TASK-003: HTTP 服务层与 API 封装

**复杂度**: M
**前置依赖**: TASK-001, TASK-002
**执行波次**: 2

### 描述
创建 HTTP 客户端配置和 API 服务封装，包含请求拦截器、错误处理、MSW Mock。

### 涉及文件
- [NEW] `application-management-web/src/services/http.ts`
- [NEW] `application-management-web/src/services/applicationApi.ts`
- [NEW] `application-management-web/src/services/index.ts`
- [NEW] `application-management-web/src/mocks/browser.ts`
- [NEW] `application-management-web/src/mocks/handlers.ts`
- [NEW] `application-management-web/src/mocks/data.ts`

### 验收标准
- [ ] Axios 实例配置正确（baseURL、timeout、withCredentials）
- [ ] 请求拦截器添加 Authorization header
- [ ] 响应拦截器统一错误处理
- [ ] 5 个 API 方法实现完整（getList、getDetail、create、update、delete）
- [ ] MSW Mock handlers 覆盖所有 API 端点
- [ ] Mock 数据格式与后端 DTO 一致

### 验证命令
```bash
cd application-management-web
npx tsc --noEmit
npm run dev
# 访问 http://localhost:5173/apps 验证 Mock 数据加载
```

---

## TASK-004: 状态管理 Store

**复杂度**: M
**前置依赖**: TASK-002, TASK-003
**执行波次**: 2

### 描述
创建 Zustand 状态管理 store，封装应用数据状态和操作方法。

### 涉及文件
- [NEW] `application-management-web/src/stores/applicationStore.ts`
- [NEW] `application-management-web/src/stores/index.ts`

### 验收标准
- [ ] `applications`、`total`、`page`、`pageSize` 状态定义正确
- [ ] `filters` 状态包含 keyword、status、type
- [ ] `currentApplication` 状态用于详情页
- [ ] `loading`、`error` UI 状态定义正确
- [ ] `fetchApplications`、`createApplication`、`updateApplication`、`deleteApplication` 方法实现
- [ ] `setCurrentApplication`、`setFilters` 方法实现
- [ ] 异步操作正确处理 loading 和 error 状态

### 验证命令
```bash
cd application-management-web
npx tsc --noEmit
# 在浏览器 DevTools 中验证 Zustand store 状态
```

---

## TASK-005: 公共组件 - 状态徽章与类型标签

**复杂度**: S
**前置依赖**: TASK-002
**执行波次**: 2

### 描述
创建基础展示组件：状态徽章和类型标签。

### 涉及文件
- [NEW] `application-management-web/src/components/AppStatusBadge/AppStatusBadge.tsx`
- [NEW] `application-management-web/src/components/AppStatusBadge/index.tsx`
- [NEW] `application-management-web/src/components/AppTypeTag/AppTypeTag.tsx`
- [NEW] `application-management-web/src/components/AppTypeTag/index.tsx`

### 验收标准
- [ ] `AppStatusBadge` 正确映射 4 种状态的颜色和文案
- [ ] `AppTypeTag` 正确映射 3 种类型的颜色和文案
- [ ] 组件支持 Ant Design Badge 和 Tag 组件
- [ ] TypeScript Props 类型定义完整
- [ ] 组件导出正确

### 验证命令
```bash
cd application-management-web
npx tsc --noEmit
npm run dev
# 在 Storybook 或测试页面验证组件渲染
```

---

## TASK-006: 公共组件 - 应用表格与卡片

**复杂度**: M
**前置依赖**: TASK-002, TASK-005
**执行波次**: 3

### 描述
创建应用列表展示组件：表格组件和卡片组件。

### 涉及文件
- [NEW] `application-management-web/src/components/AppTable/AppTable.tsx`
- [NEW] `application-management-web/src/components/AppTable/AppTable.less`
- [NEW] `application-management-web/src/components/AppTable/index.tsx`
- [NEW] `application-management-web/src/components/AppCard/AppCard.tsx`
- [NEW] `application-management-web/src/components/AppCard/AppCard.less`
- [NEW] `application-management-web/src/components/AppCard/index.tsx`

### 验收标准
- [ ] `AppTable` 展示正确列（名称、类型、状态、创建时间、操作）
- [ ] `AppTable` 支持分页、排序、行点击跳转
- [ ] `AppCard` 展示应用核心信息
- [ ] 组件使用 `AppStatusBadge` 和 `AppTypeTag`
- [ ] 样式文件正确引用
- [ ] 响应式布局支持

### 验证命令
```bash
cd application-management-web
npx tsc --noEmit
npm run dev
# 验证表格和卡片组件渲染
```

---

## TASK-007: 公共组件 - 删除确认弹窗

**复杂度**: S
**前置依赖**: TASK-002
**执行波次**: 3

### 描述
创建删除确认弹窗组件。

### 涉及文件
- [NEW] `application-management-web/src/components/DeleteConfirmModal/DeleteConfirmModal.tsx`
- [NEW] `application-management-web/src/components/DeleteConfirmModal/index.tsx`

### 验收标准
- [ ] 使用 Ant Design Modal 组件
- [ ] Props 包含 `visible`、`appName`、`onConfirm`、`onCancel`
- [ ] 二次确认文案正确（包含应用名称）
- [ ] 删除说明展示正确（30 天回收站）
- [ ] 确认按钮 Loading 状态支持

### 验证命令
```bash
cd application-management-web
npx tsc --noEmit
npm run dev
# 验证弹窗组件交互
```

---

## TASK-008: 布局组件

**复杂度**: S
**前置依赖**: TASK-001
**执行波次**: 3

### 描述
创建应用管理整体布局组件。

### 涉及文件
- [NEW] `application-management-web/src/layouts/AppLayout/AppLayout.tsx`
- [NEW] `application-management-web/src/layouts/AppLayout/AppLayout.less`
- [NEW] `application-management-web/src/layouts/AppLayout/index.tsx`
- [NEW] `application-management-web/src/layouts/index.tsx`

### 验收标准
- [ ] 布局包含 Header 和 Content 区域
- [ ] Header 展示「应用管理」标题
- [ ] Header 展示「新建应用」按钮
- [ ] Content 区域使用 Ant Design Layout.Content
- [ ] 样式支持响应式
- [ ] 导出正确

### 验证命令
```bash
cd application-management-web
npx tsc --noEmit
npm run dev
# 验证布局渲染
```

---

## TASK-009: 应用列表页

**复杂度**: L
**前置依赖**: TASK-004, TASK-006, TASK-008
**执行波次**: 4

### 描述
创建应用列表页面，整合搜索、筛选、分页功能。

### 涉及文件
- [NEW] `application-management-web/src/pages/ApplicationList/ApplicationList.tsx`
- [NEW] `application-management-web/src/pages/ApplicationList/ApplicationList.less`
- [NEW] `application-management-web/src/pages/ApplicationList/index.tsx`

### 验收标准
- [ ] 使用 `AppLayout` 布局
- [ ] 使用 `AppTable` 展示数据
- [ ] 搜索框支持按名称模糊搜索
- [ ] 状态筛选下拉框支持 5 个选项
- [ ] 分页组件与 store 联动
- [ ] 新建应用按钮跳转到创建页
- [ ] 行点击跳转到详情页
- [ ] 删除操作调用 `DeleteConfirmModal`
- [ ] Loading 状态展示正确
- [ ] 空状态展示引导创建

### 验证命令
```bash
cd application-management-web
npm run dev
# 访问 http://localhost:5173/apps 验证列表页
# 测试搜索、筛选、分页、删除功能
```

---

## TASK-010: 应用创建页

**复杂度**: L
**前置依赖**: TASK-004, TASK-008
**执行波次**: 4

### 描述
创建应用创建表单页面。

### 涉及文件
- [NEW] `application-management-web/src/pages/ApplicationCreate/ApplicationCreate.tsx`
- [NEW] `application-management-web/src/pages/ApplicationCreate/ApplicationCreate.less`
- [NEW] `application-management-web/src/pages/ApplicationCreate/index.tsx`

### 验收标准
- [ ] 使用 `AppLayout` 布局
- [ ] 表单包含所有必填字段（名称、描述、类型）
- [ ] 表单包含选填字段（图标 URL、回调 URL）
- [ ] 必填字段验证规则正确
- [ ] 应用名称实时校验唯一性（调用 API）
- [ ] 提交成功跳转到详情页
- [ ] 取消按钮返回列表页
- [ ] 提交按钮 Loading 状态防止重复提交
- [ ] 错误提示清晰友好

### 验证命令
```bash
cd application-management-web
npm run dev
# 访问 http://localhost:5173/apps/create 验证创建页
# 测试表单验证、提交、取消功能
```

---

## TASK-011: 应用详情与编辑页

**复杂度**: M
**前置依赖**: TASK-004, TASK-007, TASK-008
**执行波次**: 4

### 描述
创建应用详情页和编辑页。

### 涉及文件
- [NEW] `application-management-web/src/pages/ApplicationDetail/ApplicationDetail.tsx`
- [NEW] `application-management-web/src/pages/ApplicationDetail/ApplicationDetail.less`
- [NEW] `application-management-web/src/pages/ApplicationDetail/index.tsx`
- [NEW] `application-management-web/src/pages/ApplicationEdit/ApplicationEdit.tsx`
- [NEW] `application-management-web/src/pages/ApplicationEdit/ApplicationEdit.less`
- [NEW] `application-management-web/src/pages/ApplicationEdit/index.tsx`

### 验收标准
- [ ] 详情页展示所有应用字段
- [ ] 详情页「编辑」按钮跳转到编辑页
- [ ] 详情页「删除」按钮打开确认弹窗
- [ ] 详情页「返回」按钮返回列表页
- [ ] 编辑页表单预填充当前数据
- [ ] 编辑页可修改字段正确（名称、描述、图标 URL、回调 URL）
- [ ] 编辑页不可修改字段禁用（ID、类型、状态、所有者、时间）
- [ ] 编辑保存成功返回详情页
- [ ] 编辑取消返回详情页
- [ ] 非所有者隐藏编辑和删除按钮

### 验证命令
```bash
cd application-management-web
npm run dev
# 访问 http://localhost:5173/apps/:id 验证详情页
# 访问 http://localhost:5173/apps/:id/edit 验证编辑页
# 测试查看、编辑、删除、返回功能
```

---

## TASK-012: 路由集成与端到端测试

**复杂度**: M
**前置依赖**: TASK-009, TASK-010, TASK-011
**执行波次**: 5

### 描述
配置路由、集成所有页面、进行端到端测试。

### 涉及文件
- [NEW] `application-management-web/src/routes.tsx`
- [NEW] `application-management-web/src/App.tsx`
- [NEW] `application-management-web/src/main.tsx`
- [NEW] `application-management-web/src/vite-env.d.ts`
- [NEW] `application-management-web/src/index.less`
- [NEW] `application-management-web/public/favicon.ico`

### 验收标准
- [ ] React Router 配置正确（4 个路由）
- [ ] 路由嵌套结构正确（AppLayout 包裹子路由）
- [ ] 路由参数解析正确（`:appId`）
- [ ] 根组件整合 Provider 和 Router
- [ ] 入口文件配置正确
- [ ] 全局样式引入 Ant Design CSS
- [ ] 端到端流程测试通过：创建 → 列表 → 详情 → 编辑 → 删除
- [ ] 所有验收标准（Spec Section 12）通过

### 验证命令
```bash
cd application-management-web
npm run dev
# 完整测试用户故事 US-001 ~ US-007
# 验证所有功能需求 FR-001 ~ FR-005
# 运行端到端测试（如有）
```

---

## 执行策略

### Wave 1: 并行启动（Day 1）
```
TASK-001: 项目初始化与基础配置
TASK-002: 类型定义层
```
**说明**: 这两个任务无依赖，可同时进行。TASK-001 创建项目骨架，TASK-002 定义类型。

### Wave 2: 基础设施（Day 2-3）
```
TASK-003: HTTP 服务层与 API 封装（依赖 TASK-001, TASK-002）
TASK-004: 状态管理 Store（依赖 TASK-002）
TASK-005: 公共组件 - 状态徽章与类型标签（依赖 TASK-002）
```
**说明**: 这 3 个任务可并行执行，建立服务层和基础组件。

### Wave 3: 组件开发（Day 3-4）
```
TASK-006: 公共组件 - 应用表格与卡片（依赖 TASK-005）
TASK-007: 公共组件 - 删除确认弹窗（依赖 TASK-002）
TASK-008: 布局组件（依赖 TASK-001）
```
**说明**: 这 3 个任务可并行执行，完成所有公共组件。

### Wave 4: 页面开发（Day 5-8）
```
TASK-009: 应用列表页（依赖 TASK-004, TASK-006, TASK-008）
TASK-010: 应用创建页（依赖 TASK-004, TASK-008）
TASK-011: 应用详情与编辑页（依赖 TASK-004, TASK-007, TASK-008）
```
**说明**: 这 3 个页面任务可并行执行，但需要前序组件和 store 完成。

### Wave 5: 集成测试（Day 9-10）
```
TASK-012: 路由集成与端到端测试（依赖 TASK-009, TASK-010, TASK-011）
```
**说明**: 整合所有页面，配置路由，进行端到端测试。

---

## 依赖关系图

```
Wave 1                    Wave 2                    Wave 3                    Wave 4                    Wave 5
┌──────────┐              ┌──────────┐              ┌──────────┐              ┌──────────┐
│ TASK-001 │─────────────▶│ TASK-003 │─────────────▶│ TASK-006 │─────────────▶│ TASK-009 │───┐
│ 项目初始化 │              │ HTTP 服务  │              │ 表格/卡片  │              │ 列表页   │   │
└──────────┘              └──────────┘              └──────────┘              └──────────┘   │
       │                          │                          │                                 │
       │                          ▼                          │                                 ▼
       │                    ┌──────────┐              ┌──────────┐              ┌──────────┐ ┌──────────┐
       │                    │ TASK-004 │─────────────▶│ TASK-007 │─────────────▶│ TASK-010 │ │ TASK-012 │
       │                    │  Store   │              │ 删除弹窗  │              │ 创建页   │ │ 集成测试 │
       │                    └──────────┘              └──────────┘              └──────────┘ └──────────┘
       │                          ▲                          ▲                                 ▲
       │                          │                          │                                 │
┌──────────┐              ┌──────────┐              ┌──────────┐              ┌──────────┐   │
│ TASK-002 │─────────────▶│ TASK-005 │─────────────▶│ TASK-008 │─────────────▶│ TASK-011 │───┘
│ 类型定义  │              │ 徽章/标签  │              │  布局    │              │ 详情/编辑  │
└──────────┘              └──────────┘              └──────────┘              └──────────┘
```

---

## 修订历史

| 版本 | 日期 | 作者 | 变更说明 |
|------|------|------|----------|
| 1.0.0 | 2026-03-23 | SDD Task Agent | 初始版本 |

---

**状态**: `drafting` → `ready`  
**下一步**: Build Phase (`@sdd-4-build`)
