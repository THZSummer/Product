# Technical Plan: 应用管理 Web 端

## 元数据

| 字段 | 值 |
|------|------|
| **关联 Spec** | `APP-MGT-WEB-001` |
| **版本** | 1.0.0 |
| **创建日期** | 2026-03-23 |
| **作者** | SDD Planning Agent |
| **状态** | `drafting` |

---

## 1. 外部 API 检查

### 1.1 API 依赖扫描

根据 Spec Section 10，本模块依赖以下外部服务：

| 依赖 | 类型 | 缓存状态 |
|------|------|----------|
| **application-management API** | 后端 REST API | ✅ Spec 已提供详细 API 设计（Section 7） |
| **permission-app** | 认证授权服务 | ⚠️ 需要后续补充 API 文档 |

### 1.2 API 文档可用性

- ✅ **后端 API**: `.specs/application-management/spec.md` Section 7 提供了完整的 API 设计
- ⚠️ **认证服务**: permission-app 模块的 API 文档尚未缓存，建议在实现前补充

### 1.3 API 接口清单

基于后端 Spec，本模块需要调用以下 API：

| 方法 | 路径 | 用途 | 前端页面 |
|------|------|------|----------|
| `GET` | `/api/v1/applications` | 获取应用列表 | 列表页 |
| `GET` | `/api/v1/applications/{id}` | 获取应用详情 | 详情页 |
| `POST` | `/api/v1/applications` | 创建应用 | 创建页 |
| `PUT` | `/api/v1/applications/{id}` | 更新应用 | 编辑页 |
| `DELETE` | `/api/v1/applications/{id}` | 删除应用 | 列表页/详情页 |

---

## 2. 架构分析

### 2.1 现有架构影响

```
┌─────────────────────────────────────────────────────────────────┐
│                        open-app 项目                              │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              application-management (后端)                 │   │
│  │  - Spring Boot 3.4.15 + Java 21                           │   │
│  │  - MyBatis-Plus + MySQL + Redis                           │   │
│  │  - REST API: /api/v1/applications/*                       │   │
│  └──────────────────────────────────────────────────────────┘   │
│                              ▲                                   │
│                              │ HTTP/REST                         │
│  ┌───────────────────────────┴──────────────────────────────┐   │
│  │           application-management-web (前端) [NEW]          │   │
│  │  - React 18 + TypeScript 5 + Vite 5                        │   │
│  │  - Ant Design 5 + Zustand 4                                │   │
│  │  - 4 Pages + 10 Components                                 │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 新组件需求

根据 Spec Section 6.2 和 6.3，需要创建以下组件：

#### 页面组件 (4 个)
| 组件 | 路由 | 职责 |
|------|------|------|
| `ApplicationList` | `/apps` | 应用列表展示、搜索、筛选、分页 |
| `ApplicationCreate` | `/apps/create` | 创建应用表单 |
| `ApplicationDetail` | `/apps/:appId` | 应用详情展示 |
| `ApplicationEdit` | `/apps/:appId/edit` | 编辑应用表单 |

#### 布局组件 (1 个)
| 组件 | 职责 |
|------|------|
| `AppLayout` | 应用管理整体布局（Header + Content） |

#### 业务组件 (5 个)
| 组件 | 职责 |
|------|------|
| `AppTable` | 应用列表表格 |
| `AppCard` | 应用卡片展示 |
| `AppStatusBadge` | 状态徽章（draft/active/disabled/deleted） |
| `AppTypeTag` | 类型标签（self_build/third_party/personal） |
| `DeleteConfirmModal` | 删除确认弹窗 |

#### 状态管理 (1 个 Store)
| Store | 职责 |
|-------|------|
| `applicationStore` | 应用数据状态管理、API 调用封装 |

#### 服务层 (1 个 Service)
| Service | 职责 |
|---------|------|
| `applicationApi` | API 请求封装、类型定义 |

### 2.3 数据流设计

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   Page      │      │   Store     │      │   Service   │
│  Component  │─────▶│  (Zustand)  │─────▶│   (Axios)   │
│  (UI 层)     │      │  (状态层)    │      │  (API 层)    │
└─────────────┘      └─────────────┘      └─────────────┘
       ▲                     │                     │
       │                     │                     │
       │      ┌──────────────┘                     │
       │      │                                    │
       │      ▼                                    │
       │ ┌─────────────┐                           │
       └─│  Response   │◀──────────────────────────┘
         │   (Data)    │
         └─────────────┘
```

### 2.4 依赖关系

```
application-management-web
│
├── 开发环境
│   ├── Node.js 20 LTS (与后端一致)
│   └── NPM 10.x
│
├── 核心依赖
│   ├── React 18.x (框架)
│   ├── TypeScript 5.x (类型系统)
│   ├── Vite 5.x (构建工具)
│   └── React Router 6.x (路由)
│
├── UI/UX
│   ├── Ant Design 5.x (UI 组件)
│   └── Less 4.x (样式)
│
├── 状态/数据
│   ├── Zustand 4.x (状态管理)
│   └── Axios 1.x (HTTP 客户端)
│
└── 工具库
    └── DayJS 1.x (日期处理)
        │
        └──▶ application-management API (后端服务)
```

---

## 3. 方案对比

### 3.1 状态管理方案

#### 方案 A: Zustand (推荐)

**描述**: 使用轻量级状态管理库 Zustand

**优点**:
- 极简 API，学习成本低
- 无需 Provider 包裹，直接使用
- TypeScript 支持优秀
- Bundle 大小仅 ~1KB
- 支持中间件（persist、devtools）

**缺点**:
- 生态不如 Redux 丰富
- 大型应用可能缺少最佳实践
- 社区资源相对较少

**风险评估**: 低风险。Zustand 已成熟稳定，团队已有使用经验。

**预估工作量**: 2 天

---

#### 方案 B: React Context + useReducer

**描述**: 使用 React 原生 Context API 配合 useReducer

**优点**:
- 无需额外依赖
- React 官方方案
- 易于理解和调试

**缺点**:
- 性能优化复杂（需要 useMemo/useCallback）
- 代码冗余较多
- 跨组件共享状态不便
- 不支持 DevTools 开箱即用

**风险评估**: 低风险。但长期维护成本较高。

**预估工作量**: 3 天

---

#### 方案 C: Redux Toolkit

**描述**: 使用标准 Redux + Toolkit

**优点**:
- 生态最丰富
- DevTools 支持最好
- 大型应用最佳实践多
- 中间件生态完善

**缺点**:
- 代码冗余（尽管有 Toolkit）
- 学习曲线较陡
- Bundle 大小较大（~20KB）
- 对于本模块规模过重

**风险评估**: 低风险。但投入产出比低。

**预估工作量**: 4 天

---

### 3.2 UI 组件方案

#### 方案 A: Ant Design 5 (推荐)

**描述**: 使用 Ant Design 5 组件库

**优点**:
- 企业级组件，质量可靠
- 与 Spec 要求一致
- 文档完善，社区活跃
- TypeScript 支持完善
- 支持按需加载

**缺点**:
- Bundle 体积较大（需配合按需加载）
- 定制化需要覆盖样式

**风险评估**: 低风险。Ant Design 是成熟的企业级选择。

**预估工作量**: 3 天

---

#### 方案 B: 原生 HTML + Tailwind CSS

**描述**: 使用原生 HTML 元素配合 Tailwind CSS

**优点**:
- 完全自定义
- Bundle 体积小
- 样式灵活

**缺点**:
- 需要自己实现所有组件
- 开发效率低
- 一致性难以保证
- 与 Spec 要求不符

**风险评估**: 中风险。开发周期会显著延长。

**预估工作量**: 7 天

---

### 3.3 表单验证方案

#### 方案 A: 手动验证 (推荐)

**描述**: 使用 Ant Design Form 内置验证规则

**优点**:
- 无需额外依赖
- 与 Ant Design 深度集成
- 足够满足当前需求
- 简单直接

**缺点**:
- 复杂验证逻辑需要自定义
- 跨表单复用性差

**风险评估**: 低风险。

**预估工作量**: 1 天

---

#### 方案 B: React Hook Form + Zod

**描述**: 使用专业表单库配合 Schema 验证

**优点**:
- 性能优秀（减少重渲染）
- Schema 验证强大
- 类型推导优秀

**缺点**:
- 增加额外依赖
- 学习成本
- 与 Ant Design 集成需要额外配置

**风险评估**: 低风险。但对于本模块规模可能过度设计。

**预估工作量**: 2 天

---

## 4. 推荐方案

基于上述分析，推荐以下技术组合：

| 领域 | 推荐方案 | 理由 |
|------|----------|------|
| **状态管理** | Zustand | 轻量、简洁、TS 友好，符合模块规模 |
| **UI 组件** | Ant Design 5 | 企业级、符合 Spec、开发效率高 |
| **表单验证** | Ant Design Form | 原生集成、足够使用、零额外依赖 |
| **HTTP 客户端** | Axios | 成熟稳定、拦截器支持、TS 友好 |
| **路由** | React Router 6 | 标准方案、文档完善 |

### 4.1 推荐架构图

```
┌─────────────────────────────────────────────────────────────┐
│                     Application Page                         │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                   AppLayout                          │    │
│  │  ┌─────────────────┐  ┌─────────────────────────┐   │    │
│  │  │  Sidebar/Nav    │  │      Page Content       │   │    │
│  │  │                 │  │  ┌───────────────────┐  │   │    │
│  │  │  • 应用管理      │  │  │  ApplicationList  │  │   │    │
│  │  │                 │  │  │  - AppTable       │  │   │    │
│  │  │                 │  │  │  - AppCard        │  │   │    │
│  │  │                 │  │  │  - StatusBadge    │  │   │    │
│  │  │                 │  │  │  - TypeTag        │  │   │    │
│  │  │                 │  │  └─────────┬─────────┘  │   │    │
│  │  │                 │  │            │            │   │    │
│  │  │                 │  │  ┌─────────▼─────────┐  │   │    │
│  │  │                 │  │  │ applicationStore  │  │   │    │
│  │  │                 │  │  │   (Zustand)       │  │   │    │
│  │  │                 │  │  └─────────┬─────────┘  │   │    │
│  │  │                 │  │            │            │   │    │
│  │  │                 │  │  ┌─────────▼─────────┐  │   │    │
│  │  │                 │  │  │ applicationApi    │  │   │    │
│  │  │                 │  │  │   (Axios)         │  │   │    │
│  │  │                 │  │  └───────────────────┘  │   │    │
│  │  └─────────────────┘  └─────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

---

## 5. 文件影响分析

### 5.1 新创建文件

```
application-management-web/
├── public/
│   └── favicon.ico                          [NEW] 网站图标
├── src/
│   ├── assets/                              [NEW] 静态资源目录
│   │   └── images/
│   │       └── default-app-icon.svg         [NEW] 默认应用图标
│   ├── components/                          [NEW] 公共组件目录
│   │   ├── AppTable/
│   │   │   ├── index.tsx                    [NEW] 表格组件
│   │   │   ├── AppTable.tsx                 [NEW] 表格组件实现
│   │   │   └── AppTable.less                [NEW] 表格样式
│   │   ├── AppCard/
│   │   │   ├── index.tsx                    [NEW] 卡片组件
│   │   │   ├── AppCard.tsx                  [NEW] 卡片组件实现
│   │   │   └── AppCard.less                 [NEW] 卡片样式
│   │   ├── AppStatusBadge/
│   │   │   ├── index.tsx                    [NEW] 状态徽章组件
│   │   │   └── AppStatusBadge.tsx           [NEW] 状态徽章实现
│   │   ├── AppTypeTag/
│   │   │   ├── index.tsx                    [NEW] 类型标签组件
│   │   │   └── AppTypeTag.tsx               [NEW] 类型标签实现
│   │   └── DeleteConfirmModal/
│   │       ├── index.tsx                    [NEW] 删除确认弹窗
│   │       └── DeleteConfirmModal.tsx       [NEW] 删除确认实现
│   ├── pages/                               [NEW] 页面组件目录
│   │   ├── ApplicationList/
│   │   │   ├── index.tsx                    [NEW] 列表页入口
│   │   │   ├── ApplicationList.tsx          [NEW] 列表页实现
│   │   │   └── ApplicationList.less         [NEW] 列表页样式
│   │   ├── ApplicationCreate/
│   │   │   ├── index.tsx                    [NEW] 创建页入口
│   │   │   ├── ApplicationCreate.tsx        [NEW] 创建页实现
│   │   │   └── ApplicationCreate.less       [NEW] 创建页样式
│   │   ├── ApplicationDetail/
│   │   │   ├── index.tsx                    [NEW] 详情页入口
│   │   │   ├── ApplicationDetail.tsx        [NEW] 详情页实现
│   │   │   └── ApplicationDetail.less       [NEW] 详情页样式
│   │   └── ApplicationEdit/
│   │       ├── index.tsx                    [NEW] 编辑页入口
│   │       ├── ApplicationEdit.tsx          [NEW] 编辑页实现
│   │       └── ApplicationEdit.less         [NEW] 编辑页样式
│   ├── layouts/                             [NEW] 布局组件目录
│   │   ├── AppLayout/
│   │   │   ├── index.tsx                    [NEW] 布局入口
│   │   │   ├── AppLayout.tsx                [NEW] 布局实现
│   │   │   └── AppLayout.less               [NEW] 布局样式
│   │   └── index.tsx                        [NEW] 布局导出
│   ├── stores/                              [NEW] 状态管理目录
│   │   ├── applicationStore.ts              [NEW] 应用状态管理
│   │   └── index.ts                         [NEW] Store 导出
│   ├── services/                            [NEW] API 服务目录
│   │   ├── applicationApi.ts                [NEW] 应用 API 封装
│   │   ├── http.ts                          [NEW] HTTP 客户端配置
│   │   └── index.ts                         [NEW] Service 导出
│   ├── types/                               [NEW] 类型定义目录
│   │   ├── application.ts                   [NEW] 应用类型定义
│   │   ├── api.ts                           [NEW] API 响应类型
│   │   └── index.ts                         [NEW] 类型导出
│   ├── utils/                               [NEW] 工具函数目录
│   │   ├── format.ts                        [NEW] 格式化工具
│   │   └── index.ts                         [NEW] 工具导出
│   ├── routes.tsx                           [NEW] 路由配置
│   ├── App.tsx                              [NEW] 根组件
│   ├── main.tsx                             [NEW] 入口文件
│   ├── vite-env.d.ts                        [NEW] Vite 类型声明
│   └── index.less                           [NEW] 全局样式
├── .env                                     [NEW] 环境变量
├── .env.development                         [NEW] 开发环境变量
├── .env.production                          [NEW] 生产环境变量
├── .gitignore                               [NEW] Git 忽略配置
├── .eslintrc.cjs                            [NEW] ESLint 配置
├── .prettierrc                              [NEW] Prettier 配置
├── index.html                               [NEW] HTML 入口
├── package.json                             [NEW] NPM 依赖配置
├── tsconfig.json                            [NEW] TypeScript 配置
├── tsconfig.node.json                       [NEW] Node TypeScript 配置
├── vite.config.ts                           [NEW] Vite 配置
└── README.md                                [NEW] 模块说明文档
```

### 5.2 需要修改的文件

本模块为独立前端模块，不需要修改现有文件。

### 5.3 需要删除的文件

无。

---

## 6. 风险评估

### 6.1 技术风险

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| **Zustand 团队经验不足** | 低 | 低 | 提供使用示例，组织内部培训 |
| **Ant Design 5 样式覆盖复杂** | 中 | 中 | 使用 ConfigProvider 统一配置，建立主题变量 |
| **TypeScript 类型定义不完整** | 中 | 中 | 严格模式，逐步完善类型，使用 any 时需注释 |
| **Vite 构建配置问题** | 低 | 中 | 参考官方模板，建立标准配置 |

### 6.2 依赖风险

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| **后端 API 未就绪** | 高 | 高 | 使用 MSW 进行 Mock，并行开发 |
| **permission-app API 文档缺失** | 中 | 高 | 推动后端团队补充文档，先实现基础功能 |
| **NPM 依赖安全问题** | 低 | 中 | 定期运行 `npm audit`，使用锁文件 |
| **浏览器兼容性问题** | 低 | 中 | 使用 Browserslist 配置，CI 中增加兼容性测试 |

### 6.3 时间风险

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| **需求变更** | 中 | 高 | 与产品经理保持沟通，变更需更新 Spec |
| **开放问题未解决** | 中 | 中 | 在 Task 阶段前确认 OQ-001~OQ-005 |
| **UI/UX 设计延迟** | 中 | 中 | 使用 Ant Design 默认样式，设计可后续迭代 |
| **测试时间不足** | 中 | 高 | 开发与测试并行，建立自动化测试 |

### 6.4 风险汇总矩阵

```
        影响
        低 │  ●                  │
          │                     │
        中 │     ●  ●           │  ●
          │                     │
        高 │           ●        │  ●
          └─────────────────────┘
            低    中    高    可能性
```

---

## 7. 架构决策记录 (ADR)

### 7.1 需要创建的 ADR

| ADR 编号 | 主题 | 状态 | 优先级 |
|----------|------|------|--------|
| **ADR-003** | 前端状态管理方案选择 | PROPOSED | P0 |

### 7.2 ADR-003 概要

**决策**: 选择 Zustand 作为前端状态管理方案

**理由**:
1. 轻量级（~1KB），符合性能指标（Bundle < 500KB）
2. API 简洁，学习成本低
3. TypeScript 支持优秀
4. 无需 Provider 包裹，代码简洁
5. 支持持久化和 DevTools

**后果**:
- 正面：开发效率高，Bundle 小
- 负面：生态不如 Redux 丰富
- 风险：团队需学习新工具（可通过文档缓解）

---

## 8. 工作量预估

### 8.1 阶段划分

| 阶段 | 任务 | 工作量 |
|------|------|--------|
| **Phase 1** | 项目初始化 + 基础配置 | 0.5 天 |
| **Phase 2** | 公共组件开发（5 个） | 1.5 天 |
| **Phase 3** | 页面开发（4 个） | 3 天 |
| **Phase 4** | 状态管理 + API 服务 | 1.5 天 |
| **Phase 5** | 联调 + 测试 | 2 天 |
| **Phase 6** | 优化 + 修复 | 1.5 天 |
| **合计** | | **10 天** |

### 8.2 里程碑（与后端对齐）

| 里程碑 | 前端日期 | 后端日期 | 交付物 |
|--------|----------|----------|--------|
| M1: 项目初始化 | Day 1 | Day 1 | 可运行的空项目 |
| M2: 组件完成 | Day 3 | Day 5 | 5 个公共组件可用 |
| M3: 页面完成 | Day 6 | Day 10 | 4 个页面可用 |
| M4: 联调窗口 | **Day 7-9** | **Day 11-14** | 与后端 API 联调通过 |
| M5: 验收完成 | Day 10 | Day 15 | 通过所有验收标准 |

**注意**: 
- 后端开发周期较长（15 天 vs 10 天）
- 前端可使用 MSW Mock 先行开发，不阻塞进度
- 联调窗口：建议后端 Day 11 起提供可用 API 进行联调

---

## 9. 下一步

### 9.1 待确认事项

在进入 Task 阶段前，需要确认以下开放问题（来自 Spec Section 11）：

| 编号 | 问题 | 建议 | 决策状态 |
|------|------|------|----------|
| **OQ-001** | 是否需要支持应用卡片视图？ | 暂不支持，后续迭代 | 待确认 |
| **OQ-002** | 是否需要支持批量操作？ | 暂不支持，后续迭代 | 待确认 |
| **OQ-004** | 侧边栏导航还是顶部导航？ | 侧边栏导航 | 待确认 |
| **OQ-005** | 是否需要面包屑导航？ | 需要 | 待确认 |

### 9.2 前置条件

- [ ] 确认开放问题 OQ-001~OQ-005
- [ ] 确认 permission-app 认证 API 接口
- [ ] 确认后端 API 开发进度

### 9.3 下游任务

完成本计划后，下一步执行：
- **@sdd-3-tasks**: 基于本计划生成详细的开发任务清单

---

## 11. 与后端 Plan 对齐

### 11.1 技术栈对齐

| 技术 | 后端 Plan | 前端 Plan | 对齐状态 |
|------|-----------|-----------|----------|
| **Node.js** | 20 LTS (开发环境) | 20 LTS (开发环境) | ✅ 已对齐 |
| **API 版本** | v1 | v1 | ✅ 已对齐 |
| **认证方式** | JWT (permission-app) | JWT (permission-app) | ✅ 已对齐 |

### 11.2 功能边界对齐

| 功能 | 后端 Plan | 前端 Plan | 对齐状态 |
|------|-----------|-----------|----------|
| **应用 CRUD** | ✅ 支持 | ✅ 支持 | ✅ 一致 |
| **应用列表/搜索** | ✅ 支持 | ✅ 支持 | ✅ 一致 |
| **应用状态管理** | ✅ 支持（管理员） | ❌ 本期不做 | ⚠️ **后端预留，前端不做** |
| **权限控制** | ✅ JWT + 角色 | ❌ 留白（NG8） | ⚠️ **后端完整实现，前端留白** |

**说明**: 
- 后端 Plan 包含管理员功能（为未来管理端预留）
- 前端 Plan 本期仅实现开发者功能
- 权限控制 UI 留白，未来与用户管理模块集成

### 11.3 里程碑对齐

| 里程碑 | 前端时间 | 后端时间 | 依赖关系 |
|--------|----------|----------|----------|
| **M1 初始化** | Day 1 | Day 1-3 | 并行开始 |
| **M2 组件/Service** | Day 3 | Day 6 | 前端使用 Mock 不阻塞 |
| **M3 页面/Controller** | Day 6 | Day 10 | 前端使用 Mock 不阻塞 |
| **M4 联调窗口** | Day 7-9 | Day 11-14 | **关键路径** |
| **M5 验收** | Day 10 | Day 15 | 前端可先行验收 |

### 11.4 API 接口对齐

| API | 后端定义 | 前端调用 | 对齐状态 |
|-----|----------|----------|----------|
| `GET /api/v1/applications` | ✅ 已定义 | ✅ 已调用 | ✅ 一致 |
| `GET /api/v1/applications/{id}` | ✅ 已定义 | ✅ 已调用 | ✅ 一致 |
| `POST /api/v1/applications` | ✅ 已定义 | ✅ 已调用 | ✅ 一致 |
| `PUT /api/v1/applications/{id}` | ✅ 已定义 | ✅ 已调用 | ✅ 一致 |
| `DELETE /api/v1/applications/{id}` | ✅ 已定义 | ✅ 已调用 | ✅ 一致 |
| `PATCH /api/v1/applications/{id}/status` | ✅ 已定义 | ❌ 本期不调用 | ⚠️ **后端预留** |

### 11.5 数据模型对齐

| 实体 | 后端定义 | 前端类型 | 对齐状态 |
|------|----------|----------|----------|
| **Application** | ✅ Entity | ✅ Interface | ✅ 字段一致 |
| **AppStatus** | ✅ Enum (4 状态) | ✅ Enum (4 状态) | ✅ 一致 |
| **AppType** | ✅ Enum (3 类型) | ✅ Enum (3 类型) | ✅ 一致 |
| **CreateApplicationRequest** | ✅ DTO | ✅ Interface | ✅ 字段一致 |
| **UpdateApplicationRequest** | ✅ DTO | ✅ Interface | ✅ 字段一致 |
| **ApplicationListResponse** | ✅ DTO | ✅ Interface | ✅ 字段一致 |
| **ApplicationDetailResponse** | ✅ DTO | ✅ Interface | ✅ 字段一致 |

### 11.6 待确认事项

以下事项需要在 Task 阶段前确认：

1. **Mock 数据规范**: 
   - 使用 MSW 进行 Mock
   - Mock 数据格式需与后端 DTO 一致
   - 建议后端提供 Mock 数据示例

2. **联调计划**:
   - 后端 Day 11 起提供可用 API
   - 前端 Day 7-9 进行联调测试
   - 建议建立联调问题清单

3. **权限集成**:
   - permission-app 认证流程确认
   - JWT Token 获取和刷新机制
   - 用户信息获取接口

---

## 12. 修订历史

| 版本 | 日期 | 作者 | 变更说明 |
|------|------|------|----------|
| 1.0.0 | 2026-03-23 | SDD Planning Agent | 初始版本 |
| 1.0.1 | 2026-03-23 | Summer | 修正：对齐后端 Plan、补充 Node.js 版本、添加里程碑对齐章节 |

---

**状态**: `drafting` → `review`  
**下一步**: Review & Validate (`@sdd-3-tasks`)
