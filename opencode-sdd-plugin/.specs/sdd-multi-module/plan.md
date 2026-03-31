# Technical Plan: SDD Multi Sub-Feature Parallel Development Support

| 元数据 | 值 |
|--------|-----|
| **Feature ID** | sdd-multi-module |
| **Feature 名称** | SDD 多子 Feature 并行开发支持 |
| **规范版本** | 1.2.10 |
| **创建日期** | 2026-03-31 |
| **状态** | planned |
| **优先级** | P0 (用户当前痛点) |
| **RICE 评分** | 72 |
| **预计工作量** | 1.5 人天 (MVP) |
| **关联 Roadmap** | v1.2.6 (Phase 2.5) |
| **依赖 Feature** | sdd-plugin-phase2 (v1.2.0) |

---

## 1. 上下文

### 1.1 项目背景

OpenCode SDD 插件自 v1.1.1 发布以来，已成功将 SDD 6 阶段工作流 (spec→plan→tasks→build→review→validate) 集成到 OpenCode 环境中。然而，随着用户在大型多子 Feature 项目中的使用，现有单 Feature 单文档的架构暴露出明显局限：

**当前痛点** (来自用户反馈):
- 单 Feature spec 文件达 80KB+，难以维护和阅读
- 多子 Feature 开发阻塞在同一 Feature 流程中，无法并行
- 团队协作者无法独立负责不同子 Feature
- 状态追踪粒度太粗，无法识别子 Feature 级阻塞

### 1.2 解决的问题

| 问题 | 当前状态 | 目标状态 |
|------|----------|----------|
| 大文件维护 | 单 spec.md 80KB+ | 主文档 5KB + 子 Feature 文档 10-15KB |
| 并行开发 | 阻塞等待 | 子 Feature 级并行 + 依赖管理 |
| 状态追踪 | Feature 级 | 子 Feature 级 + 依赖可视化 |
| 团队协作 | 单开发者 | 多开发者并行 |

### 1.3 目标用户

| 用户类型 | 场景 | 获益 |
|----------|------|------|
| **大型项目负责人** | 管理 10+ 子 Feature 的 Feature | 清晰的全局视图 + 子 Feature 级追踪 |
| **子 Feature 开发者** | 负责特定子 Feature 开发 | 独立工作流 + 依赖就绪通知 |
| **技术主管** | 评审整体进度 | 依赖关系图 + 阻塞风险预警 |

---

## 2. Goals & Non-Goals

### 2.1 Goals (本版本目标)

| ID | 目标 | 验收方式 |
|----|------|----------|
| G-1 | 支持子 Feature Spec 结构，主文档 + 子 Feature 目录 | 可成功拆分 80KB spec |
| G-2 | 子 Feature 级状态追踪，每个子 Feature 独立流转 6 阶段 | 自治 state.json 支持 |
| G-3 | 并行任务分组，组内并行、组间串行 | tasks.md 支持并行声明 |
| G-5 | 向后兼容，不影响现有单模块 Feature | 旧项目无需迁移 |

### 2.2 Non-Goals (本版本不做)

| ID | 非目标 | 说明 | 后续版本 |
|----|--------|------|----------|
| NG-1 | 完全替换核心 6 阶段流程 | 保持 spec→plan→tasks→build→review→validate | - |
| NG-2 | 移除现有核心 Agent | @sdd-spec/plan/tasks/build 保持不变 | - |
| NG-3 | 强制所有项目使用子 Feature 化 | 子 Feature 化为可选功能 | - |
| NG-4 | 跨 Feature 依赖管理 | 仅支持单 Feature 内子 Feature 依赖 | v1.3.0 |
| NG-5 | 远程状态同步 | 仅本地 state.json | v2.0.0 |

---

## 3. 用户故事

### 3.1 F-251: 子 Feature Spec 结构

| ID | 用户故事 | 价值 | 验收条件 |
|----|----------|------|----------|
| US-251-1 | 作为技术负责人，我希望将大型 spec 拆分为主文档 + 子 Feature 文档，以便降低维护复杂度 | 单文档从 80KB 降至 15KB 以内 | 主 spec.md < 10KB |
| US-251-2 | 作为子 Feature 开发者，我希望只关注自己负责的子 Feature 文档，而不被其他子 Feature 信息干扰 | 提高专注度，减少认知负担 | 子 Feature 文档独立可读 |
| US-251-3 | 作为新加入的开发者，我希望能通过子 Feature 索引快速了解整体结构 | 降低 onboarding 时间 | 索引表清晰完整 |

### 3.2 F-252: 增强 State 管理

| ID | 用户故事 | 价值 | 验收条件 |
|----|----------|------|----------|
| US-252-1 | 作为项目负责人，我希望看到每个子 Feature 的独立状态，以便识别阻塞点 | 及时发现并解决阻塞 | 子 Feature 状态可视化 |
| US-252-2 | 作为子 Feature 开发者，我希望明确知道依赖的子 Feature 是否完成，以便安排工作 | 避免无效等待 | 依赖状态可查询 |
| US-252-3 | 作为系统，我希望聚合子 Feature 状态计算 Feature 整体状态 | 保持全局视图一致性 | 聚合逻辑正确 |

### 3.3 F-253: 并行任务机制

| ID | 用户故事 | 价值 | 验收条件 |
|----|----------|------|----------|
| US-253-1 | 作为开发者 A，我希望与开发者 B 同时开发不同子 Feature，只要依赖就绪 | 缩短整体开发周期 | 并行 build 无冲突 |
| US-253-2 | 作为技术主管，我希望在 tasks.md 中显式声明并行分组 | 清晰的任务执行策略 | 分组语法简洁 |
| US-253-3 | 作为系统，我希望在依赖子 Feature 完成后自动通知等待者 | 减少人工检查 | 通知机制触发 |



---

## 4. 功能需求 (FR)

### 4.0 F-250: SDD 容器化目录结构

| 属性 | 值 |
|------|-----|
| **优先级** | P0 (前置基础设施) |
| **工作量** | 12 小时 |
| **依赖** | 无 |
| **RICE 评分** | 80 |

**功能描述**: 更新 `src/` 源代码，让 Agent 支持 `.sdd/` 容器化目录结构。

**核心设计**:
```typescript
// src/utils/workspace.ts
function getSDDWorkspace(): string {
  // 优先查找 .sdd/ 目录
  if (existsSync('.sdd')) return '.sdd';
  // 回退到 .specs/ 目录（兼容）
  if (existsSync('.specs')) return '.';
  throw new Error('未找到 SDD 工作空间');
}
```

**验收标准**:
- [ ] Agent 自动识别 `.sdd/` 工作空间
- [ ] 状态管理器支持新路径
- [ ] 命令系统支持新目录结构
- [ ] 向后兼容旧结构

---

### 4.1 F-251: 子 Feature Spec 结构

| FR ID | 需求描述 | 优先级 | 验收标准 |
|-------|----------|--------|----------|
| FR-251-1 | 主 spec.md 包含子 Feature 索引表，列出所有 sub-feature 及其状态 | P0 | 索引表包含子 feature 名、目录、状态、负责人 |
| FR-251-2 | 子目录存放各子 Feature 独立文档 | P0 | 每个子 feature 有完整目录结构 |
| FR-251-3 | 每个 sub-feature 目录包含 README.md 自说明文档 | P0 | README 清晰描述子 feature 范围和接口 |
| FR-251-4 | 跨子 Feature 协同信息 (接口约定、数据流) 在主文档集中管理 | P0 | 协同信息不分散 |
| FR-251-5 | 支持单模块模式 (无子 Feature 目录) 的向后兼容 | P0 | 旧项目无需迁移 |
| FR-251-6 | 子 Feature 文档遵循与主 spec 相同的结构规范 | P1 | 子 feature 文档可独立评审 |

### 4.2 F-252: 分布式 State 管理

| FR ID | 需求描述 | 优先级 | 验收标准 |
|-------|----------|--------|----------|
| FR-252-1 | 每个子 Feature 独立管理自身状态 (.state.json 在子目录内) | P0 | 子 feature 状态自治 |
| FR-252-2 | Feature 级状态通过扫描子目录聚合子 Feature 状态 | P0 | 全局视图可聚合 |
| FR-252-3 | 依赖关系记录在 Feature 级 state.json 中 | P0 | 依赖关系可查询 |
| FR-252-4 | Feature 状态 = 所有子 Feature 状态的聚合 (最慢子 feature 决定) | P0 | 聚合逻辑正确 |
| FR-252-5 | 旧格式 state.json 可读取并自动升级 | P1 | 兼容层工作正常 |
| FR-252-6 | 状态变更时仅更新子 Feature 自身状态文件 | P1 | 状态自治，减少冲突 |

### 4.3 F-253: 并行任务机制

| FR ID | 需求描述 | 优先级 | 验收标准 |
|-------|----------|--------|----------|
| FR-253-1 | tasks.md 支持并行分组声明语法 | P0 | 语法简洁易懂 |
| FR-253-2 | 组内任务可并行执行，组间任务串行执行 | P0 | 执行策略正确 |
| FR-253-3 | 任务可声明依赖其他任务 (跨子 Feature 依赖) | P0 | 依赖解析正确 |
| FR-253-4 | 支持多开发者同时 build 不同子 Feature | P0 | 无文件冲突 |
| FR-253-5 | 依赖就绪时触发通知机制 | P1 | 通知可配置 |

---

## 5. 非功能需求 (NFR)

### 5.1 性能需求

| NFR ID | 需求 | 目标值 | 测量方式 |
|--------|------|--------|----------|
| NFR-001 | 状态文件加载时间 | < 100ms | 本地测试 |
| NFR-002 | 子 Feature 依赖图生成时间 | < 500ms | 10 子 Feature 场景 |
| NFR-003 | spec 拆分处理时间 | < 5s | 80KB spec |
| NFR-004 | 并行 build 冲突检测时间 | < 200ms | 实时检测 |

### 5.2 兼容性需求

| NFR ID | 需求 | 说明 |
|--------|------|------|
| NFR-101 | 向后兼容单模块 Feature | 旧项目无需任何修改 |
| NFR-102 | 兼容旧格式 state.json | 自动升级，保留备份 |
| NFR-103 | 兼容现有 6 阶段流程 | spec→plan→tasks→build→review→validate 不变 |
| NFR-104 | 兼容现有核心 Agent | @sdd-spec/plan/tasks/build 无需修改 |

### 5.3 可维护性需求

| NFR ID | 需求 | 目标值 |
|--------|------|--------|
| NFR-201 | 代码注释覆盖率 | > 60% |
| NFR-202 | 新功能单元测试覆盖率 | > 80% |
| NFR-203 | 文档完整性 | 用户指南 + API 文档齐全 |

### 5.4 可靠性需求

| NFR ID | 需求 | 说明 |
|--------|------|------|
| NFR-301 | 状态文件损坏恢复 | 自动备份，可回滚 |
| NFR-302 | 并发写入冲突处理 | 自治设计降低冲突概率 + 文件锁 |
| NFR-303 | 错误降级策略 | 子 Feature 失败时降级为单模块 |

---

## 6. 技术设计

### 6.1 架构概览

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         OpenCode SDD Plugin (v1.2.5)                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────┐    ┌─────────────────────┐                     │
│  │   核心 6 阶段 Agent    │    │   子 Feature 扩展层   │                     │
│  │  (保持不变)         │    │                     │                     │
│  │  @sdd-spec          │    │  ┌───────────────┐  │                     │
│  │  @sdd-plan          │───▶│  │ 子 Feature     │  │                     │
│  │  @sdd-tasks         │    │  │ 结构支持      │  │                     │
│  │  @sdd-build         │    │  ├───────────────┤  │                     │
│  │  @sdd-review        │    │  │ 自治状态管理  │  │                     │
│  │  @sdd-validate      │    │  └───────────────┘  │                     │
│  │                     │    │                     │                     │
│  └─────────────────────┘    └─────────────────────┘                     │
│                                                                         │
│  ┌─────────────────────────────────────────────────┐                   │
│  │              State Machine (增强版)              │                   │
│  │                                                 │                   │
│  │  Feature 状态 ←── 聚合 ──→ 子 Feature 状态数组    │                   │
│  │  [planned]      [specified, planned, ...]       │                   │
│  │                     ↑                           │                   │
│  │                     │ 自治                      │                   │
│  │         每个子 Feature 独立 .state.json          │                   │
│  └─────────────────────────────────────────────────┘                   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 6.2 数据结构设计

#### 6.2.1 统一 State Schema

为简化设计，采用**单一 State Schema**，通过目录结构自动识别模式：
- **单 Feature 模式**：无子 Feature 目录
- **多子 Feature 模式**：有子 Feature 目录

**设计原则**：
- ✅ 目录结构自动发现子 Feature：扫描子 Feature 目录
- ✅ State 文件只关心自身状态，不存储结构信息
- ✅ 极简设计：字段越少越好
- ✅ 简化判断逻辑：检查目录而非读取字段

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "SDD Feature State (Unified)",
  "type": "object",
  "properties": {
    "feature": {
      "type": "string",
      "description": "Feature ID"
    },
    "name": {
      "type": "string",
      "description": "Feature 名称"
    },
    "version": {
      "type": "string",
      "description": "State schema 版本",
      "default": "1.2.10"
    },
    "status": {
      "type": "string",
      "enum": ["drafting", "specified", "planned", "tasked", "implementing", "reviewing", "validated", "completed"],
      "description": "当前状态"
    },
    "phase": {
      "type": "integer",
      "minimum": 1,
      "maximum": 6,
      "description": "当前 SDD 阶段 (1=spec, 2=plan, ..., 6=validate)"
    },
    "files": {
      "type": "object",
      "properties": {
        "spec": { "type": "string", "default": "spec.md" },
        "plan": { "type": "string", "default": "plan.md" },
        "tasks": { "type": "string", "default": "tasks.md" },
        "readme": { "type": "string", "default": "README.md" }
      },
      "description": "文档文件路径配置 (可选，使用默认值时可省略)"
    },
    "dependencies": {
      "type": "object",
      "description": "依赖关系",
      "properties": {
        "on": {
          "type": "array",
          "items": { "type": "string" },
          "description": "此 Feature 依赖的其他 Feature/Sub-Feature ID"
        },
        "blocking": {
          "type": "array",
          "items": { "type": "string" },
          "description": "被此 Feature 阻塞的其他 Feature/Sub-Feature ID"
        }
      }
    },
    "assignee": {
      "type": "string",
      "description": "负责人 (Sub-Feature 常用)"
    },
    "createdAt": {
      "type": "string",
      "format": "date-time",
      "description": "创建时间"
    },
    "updatedAt": {
      "type": "string",
      "format": "date-time",
      "description": "最后更新时间"
    }
  },
  "required": ["feature", "status"]
}
```

#### 6.2.2 字段说明

| 字段 | 类型 | 必填 | 单 Feature 模式 | 多子 Feature 模式 | 说明 |
|------|------|------|-----------------|-------------------|------|
| `feature` | string | ✅ | Feature ID | 子 Feature ID | 唯一标识符 |
| `name` | string | ❌ | 可选 | 可选 | 人类可读名称 |
| `status` | enum | ✅ | 使用 | 使用 | 8 状态枚举 |
| `phase` | integer | ❌ | 可选 | 推荐 | SDD 阶段 1-6 |
| `files` | object | ❌ | 可选 | 可选 | 文档路径配置 |
| `dependencies` | object | ❌ | 可选 | 推荐 | 依赖关系图 |
| `assignee` | string | ❌ | 可选 | Sub-Feature 推荐 | 负责人 |

#### 6.2.3 示例 1: 单 Feature 模式

```json
{
  "feature": "user-auth",
  "name": "用户认证功能",
  "version": "1.2.10",
  "status": "planned",
  "phase": 2,
  "files": {
    "spec": "spec.md",
    "plan": "plan.md",
    "tasks": "tasks.md"
  },
  "dependencies": {
    "on": ["user-db-schema"],
    "blocking": []
  },
  "assignee": "张三",
  "createdAt": "2026-03-31T09:00:00Z",
  "updatedAt": "2026-03-31T10:00:00Z"
}
```

**说明**: 单 Feature 模式无子 Feature 目录，结构简洁。

#### 示例 2: 多子 Feature 模式 (主 Feature)

```json
{
  "feature": "order-system-v2",
  "name": "订单系统 v2.0",
  "version": "1.2.10",
  "status": "planned",
  "phase": 2,
  "files": {
    "spec": "spec.md",
    "plan": "plan.md",
    "tasks": "tasks.md"
  },
  "dependencies": {
    "on": ["user-db-schema"],
    "blocking": []
  },
  "createdAt": "2026-03-31T09:00:00Z",
  "updatedAt": "2026-03-31T10:00:00Z"
}
```

**子 Feature 发现**:
- 扫描子 Feature 目录
- 每个子目录是一个 Sub-Feature
- 读取每个 Sub-Feature 的 `.state.json` 获取状态

**说明**: 主 Feature 有子 Feature 目录，自动识别为多子 Feature 模式。State 文件只关心自身状态，不存储结构信息，子 Feature 通过目录结构自动发现。

#### 示例 3: 多子 Feature 模式 (Sub-Feature)

```json
{
  "feature": "order-core",
  "name": "订单核心模块",
  "version": "1.2.10",
  "status": "planned",
  "phase": 2,
  "files": {
    "spec": "spec.md",
    "plan": "plan.md",
    "tasks": "tasks.md"
  },
  "dependencies": {
    "on": [],
    "blocking": ["inventory", "notification"]
  },
  "assignee": "张三",
  "createdAt": "2026-03-31T09:00:00Z",
  "updatedAt": "2026-03-31T10:00:00Z"
}
```

**说明**: Sub-Feature 通过目录层级隐含父子关系，无需显式声明。

### 12.4 示例：并行任务分组

```markdown
# tasks.md

## 并行执行组

### 组 1: 基础子 Feature（可并行）
- [ ] 订单核心 - 数据库设计 @张三
- [ ] 订单核心 - API 定义 @张三
- [ ] 库存服务 - 数据库设计 @李四 (依赖：订单核心 - API 定义)
- [ ] 通知服务 - 数据库设计 @王五 (依赖：订单核心 - API 定义)

### 组 2: 核心实现（等待组 1 完成）
- [ ] 订单核心 - 订单创建实现 @张三
- [ ] 订单核心 - 订单查询实现 @张三
- [ ] 库存服务 - 库存扣减实现 @李四
- [ ] 通知服务 - 消息发送实现 @王五

### 组 3: 集成测试（等待组 2 完成）
- [ ] 端到端测试 @张三
- [ ] 性能测试 @李四
- [ ] 回归测试 @王五
```

---

**文档状态**: planned  
**下一步**: 运行 `@sdd-tasks sdd-multi-module` 创建任务分解文档  
**状态更新命令**: 
```bash
/tool sdd_update_state {"feature": "sdd-multi-module", "state": "planned"}
```
