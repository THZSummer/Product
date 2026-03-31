# Feature Specification: F-250 - SDD 容器化目录结构支持

| 元数据 | 值 |
|--------|-----|
| **Feature ID** | F-250 |
| **名称** | SDD 容器化目录结构支持 |
| **版本** | 2.0.0 |
| **创建日期** | 2026-03-31 |
| **状态** | specified |
| **优先级** | P0（前置基础设施） |
| **RICE 评分** | 80 |
| **关联 Roadmap** | v1.2.11 (Phase 2.5) |
| **依赖关系** | 无（独立基础设施） |
| **Blocking** | F-251（子 Feature Spec 结构） |

---

## 1. 上下文

### 1.1 问题背景

当前 SDD 插件的源代码硬编码了 `.specs/` 目录路径，导致无法支持新的 `.sdd/` 容器化工作空间结构。主要问题：

| 问题 | 当前状态 | 影响 |
|------|----------|------|
| **路径硬编码** | `src/index.ts` 中硬编码 `.specs/` | 无法识别 `.sdd/` 工作空间 |
| **状态管理器** | `StateMachine` 构造函数固定使用 `.specs` | 无法适配新路径结构 |
| **命令系统** | `/sdd` 命令中路径写死 | 无法支持多工作空间 |
| **无兼容层** | 仅支持单一结构 | 老用户无法渐进迁移 |

### 1.2 解决的问题

| 问题 | 当前状态 | 目标状态 |
|------|----------|----------|
| **工作空间识别** | 无识别逻辑 | 自动检测 `.sdd/` 或 `.specs/` |
| **路径管理** | 硬编码字符串 | 统一工作空间 API |
| **兼容性** | 仅支持旧结构 | 同时支持新旧结构 |
| **配置化** | 无配置选项 | 支持路径配置 |

### 1.3 设计原则

1. **代码改造优先**: 本 Feature 聚焦源代码修改，而非文件迁移
2. **向后兼容**: 不影响现有 `.specs/` 项目
3. **渐进迁移**: 用户可选择何时迁移到 `.sdd/` 结构
4. **路径透明**: Agent 自动识别工作空间，用户无感知

---

## 2. Goals & Non-Goals

### 2.1 Goals (本版本目标)

| ID | 目标 | 验收方式 |
|----|------|----------|
| **G-1** | 实现工作空间识别逻辑 | `src/utils/workspace.ts` 存在 |
| **G-2** | 更新状态管理器支持新路径 | `StateMachine` 使用新 API |
| **G-3** | 更新命令系统支持新结构 | `/sdd` 命令路径动态获取 |
| **G-4** | 添加兼容层支持旧结构 | 同时支持 `.sdd/` 和 `.specs/` |
| **G-5** | 配置系统支持路径配置 | 可选配置覆盖默认行为 |

### 2.2 Non-Goals (本版本不做)

| ID | 非目标 | 说明 | 后续版本 |
|----|--------|------|----------|
| **NG-1** | 迁移现有文件到 `.sdd/` | 文件迁移是用户行为 | 用户自选 |
| **NG-2** | 创建自动迁移脚本 | 本 Feature 仅代码改造 | v1.3.0 |
| **NG-3** | 删除 `.specs/` 支持 | 永久保留兼容 | - |
| **NG-4** | 强制用户使用新结构 | 用户可选择 | - |

---

## 3. 用户故事

| ID | 用户故事 | 价值 | 验收条件 |
|----|----------|------|----------|
| **US-250-1** | 作为新用户，我希望初始化时自动创建 `.sdd/` 结构，以便使用最新最佳实践 | 开箱即用 | `sdd_init` 创建 `.sdd/` |
| **US-250-2** | 作为老用户，我希望现有 `.specs/` 项目仍能正常工作，以便不被强制迁移 | 向后兼容 | 旧项目无影响 |
| **US-250-3** | 作为开发者，我希望 Agent 自动识别工作空间，以便无需手动配置 | 降低使用成本 | 路径自动检测 |
| **US-250-4** | 作为项目负责人，我希望可选配置工作空间路径，以便自定义组织方式 | 灵活性 | 配置项可用 |

---

## 4. 功能需求 (Functional Requirements)

### 4.1 FR-250-1: Agent 支持 `.sdd/` 工作空间根目录

| FR ID | 需求描述 | 优先级 | 验收标准 |
|-------|----------|--------|----------|
| **FR-250-1-1** | 实现 `getSDDWorkspace()` 函数，优先检测 `.sdd/` 目录 | P0 | 函数返回正确路径 |
| **FR-250-1-2** | 当 `.sdd/` 不存在时，回退到根目录（`.specs/` 模式） | P0 | 兼容旧结构 |
| **FR-250-1-3** | 工作空间识别逻辑封装在独立模块 | P0 | `src/utils/workspace.ts` 存在 |
| **FR-250-1-4** | 提供 `getSpecsDir()` 函数返回规范目录路径 | P0 | 路径计算正确 |

### 4.2 FR-250-2: 状态管理器支持新路径

| FR ID | 需求描述 | 优先级 | 验收标准 |
|-------|----------|--------|----------|
| **FR-250-2-1** | `StateMachine` 构造函数接受可选的 `specsDir` 参数 | P0 | API 向后兼容 |
| **FR-250-2-2** | 默认使用 `getSpecsDir()` 获取路径 | P0 | 自动适配新结构 |
| **FR-250-2-3** | `state.json` 读写路径使用新 API | P0 | 文件操作正确 |

### 4.3 FR-250-3: 命令系统支持新目录结构

| FR ID | 需求描述 | 优先级 | 验收标准 |
|-------|----------|--------|----------|
| **FR-250-3-1** | `/sdd init` 命令优先创建 `.sdd/` 结构 | P0 | 新目录结构正确 |
| **FR-250-3-2** | `/sdd specify` 命令使用动态路径创建规范 | P0 | 规范目录正确 |
| **FR-250-3-3** | 所有 `/sdd` 子命令使用统一路径 API | P0 | 无硬编码路径 |

### 4.4 FR-250-4: 兼容层支持旧结构

| FR ID | 需求描述 | 优先级 | 验收标准 |
|-------|----------|--------|----------|
| **FR-250-4-1** | 当仅存在 `.specs/` 时，系统正常工作 | P0 | 旧项目无影响 |
| **FR-250-4-2** | 路径检测逻辑不破坏现有功能 | P0 | 回归测试通过 |
| **FR-250-4-3** | 日志和错误信息清晰指示使用的结构 | P1 | 用户可识别模式 |

### 4.5 FR-250-5: 配置系统支持路径配置

| FR ID | 需求描述 | 优先级 | 验收标准 |
|-------|----------|--------|----------|
| **FR-250-5-1** | 支持通过环境变量 `SDD_WORKSPACE` 覆盖默认路径 | P1 | 环境变量生效 |
| **FR-250-5-2** | 支持通过配置文件指定工作空间路径 | P2 | 配置项可用 |
| **FR-250-5-3** | 配置优先级：环境变量 > 配置文件 > 自动检测 | P1 | 优先级正确 |

---

## 5. 非功能需求 (Non-Functional Requirements)

### 5.1 兼容性需求

| NFR ID | 需求 | 说明 |
|--------|------|------|
| **NFR-250-1** | 向后兼容：不影响现有 `.specs/` 项目 | 旧项目无需修改 |
| **NFR-250-2** | 渐进迁移：用户可选择何时迁移 | 无强制迁移 |
| **NFR-250-3** | 路径透明：Agent 自动识别工作空间 | 用户无感知 |

### 5.2 性能需求

| NFR ID | 需求 | 目标值 |
|--------|------|--------|
| **NFR-250-4** | 工作空间检测时间 | < 10ms |
| **NFR-250-5** | 路径解析开销 | 无显著性能下降 |

### 5.3 可维护性需求

| NFR ID | 需求 | 说明 |
|--------|------|------|
| **NFR-250-6** | 路径逻辑集中管理 | 单一职责原则 |
| **NFR-250-7** | 添加单元测试覆盖 | 覆盖率 > 80% |
| **NFR-250-8** | 代码注释清晰 | 关键逻辑有说明 |

### 5.4 安全性需求

| NFR ID | 需求 | 说明 |
|--------|------|------|
| **NFR-250-9** | 路径遍历防护 | 防止 `../` 攻击 |
| **NFR-250-10** | 权限检查 | 确保目录可读写 |

---

## 6. 技术设计

### 6.1 架构影响

```
src/
├── utils/
│   └── workspace.ts          # 新增：工作空间识别模块
├── state/
│   └── machine.ts            # 修改：使用 workspace API
├── commands/
│   └── sdd.ts                # 修改：使用 workspace API
└── index.ts                  # 修改：使用 workspace API
```

### 6.2 核心 API 设计

```typescript
// src/utils/workspace.ts

/**
 * 获取 SDD 工作空间根目录
 * 优先级：环境变量 > .sdd/ 目录 > .specs/ 目录 > 抛出错误
 */
export function getSDDWorkspace(): string;

/**
 * 获取规范目录路径
 * @returns 完整的 specs 目录路径
 */
export function getSpecsDir(): string;

/**
 * 获取 Feature 目录路径
 * @param featureId Feature 标识符
 * @returns 完整的 Feature 目录路径
 */
export function getFeatureDir(featureId: string): string;

/**
 * 检测当前使用的工作空间模式
 * @returns 'container' | 'legacy' | 'unknown'
 */
export function detectWorkspaceMode(): WorkspaceMode;

/**
 * 初始化工作空间（创建目录结构）
 */
export async function initializeWorkspace(): Promise<void>;
```

### 6.3 工作空间识别逻辑

```typescript
// src/utils/workspace.ts 核心实现
import * as fs from 'fs/promises';
import * as path from 'path';

export type WorkspaceMode = 'container' | 'legacy' | 'unknown';

let cachedWorkspace: string | null = null;

/**
 * 获取 SDD 工作空间根目录
 * 优先级：环境变量 > .sdd/ 目录 > .specs/ 目录 > 抛出错误
 */
export function getSDDWorkspace(): string {
  // 1. 检查环境变量
  const envWorkspace = process.env.SDD_WORKSPACE;
  if (envWorkspace) {
    return envWorkspace;
  }

  // 2. 使用缓存（避免重复检测）
  if (cachedWorkspace) {
    return cachedWorkspace;
  }

  // 3. 优先查找 .sdd/ 目录（容器模式）
  if (fs.existsSync('.sdd')) {
    cachedWorkspace = '.sdd';
    return '.sdd';
  }

  // 4. 回退到 .specs/ 目录（兼容模式）
  if (fs.existsSync('.specs')) {
    cachedWorkspace = '.';
    return '.';
  }

  // 5. 未找到工作空间
  throw new Error(
    '未找到 SDD 工作空间。请运行 /sdd init 初始化，或创建 .sdd/ 或 .specs/ 目录。'
  );
}

/**
 * 获取规范目录路径
 */
export function getSpecsDir(): string {
  const workspace = getSDDWorkspace();
  // 容器模式：.sdd/.specs/
  // 兼容模式：.specs/
  if (workspace === '.sdd') {
    return path.join(workspace, '.specs');
  }
  // 兼容模式下，specs 在根目录
  return '.specs';
}

/**
 * 获取 Feature 目录路径
 */
export function getFeatureDir(featureId: string): string {
  const specsDir = getSpecsDir();
  return path.join(specsDir, featureId);
}

/**
 * 检测当前使用的工作空间模式
 */
export function detectWorkspaceMode(): WorkspaceMode {
  try {
    const workspace = getSDDWorkspace();
    if (workspace === '.sdd') {
      return 'container';
    }
    return 'legacy';
  } catch {
    return 'unknown';
  }
}

/**
 * 初始化工作空间（创建目录结构）
 */
export async function initializeWorkspace(): Promise<void> {
  const mode = detectWorkspaceMode();
  
  if (mode === 'container') {
    // 容器模式已存在，无需初始化
    return;
  }

  if (mode === 'legacy') {
    // 兼容模式，可选升级到容器模式
    // 本版本不自动迁移，仅提示
    return;
  }

  // 未知模式，创建容器结构
  await fs.mkdir('.sdd/.specs', { recursive: true });
  cachedWorkspace = '.sdd';
}
```

### 6.4 状态管理器修改

```typescript
// src/state/machine.ts 修改

import { getSpecsDir } from '../utils/workspace.js';

export class StateMachine {
  private stateFilePath: string;

  constructor(specsDir?: string) {
    // 支持显式传入（用于测试）
    // 否则使用自动检测
    const dir = specsDir || getSpecsDir();
    this.stateFilePath = path.join(dir, '.sdd', 'state.json');
  }

  // ... 其他方法不变
}
```

### 6.5 命令系统修改

```typescript
// src/commands/sdd.ts 修改

import { getSpecsDir, initializeWorkspace } from '../utils/workspace.js';

async function handleInit(ctx: any) {
  // 初始化容器结构
  await initializeWorkspace();
  
  const specsDir = getSpecsDir();
  
  const dirs = [
    specsDir,
    path.join(specsDir, 'examples'),
    path.join(specsDir, 'architecture/adr'),
    // ... 其他目录
  ];

  for (const dir of dirs) {
    await fs.mkdir(dir, { recursive: true });
  }

  // ...
}
```

### 6.6 数据模型变更

无数据模型变更，仅路径计算逻辑修改。

### 6.7 第三方依赖

无新增第三方依赖。

---

## 7. 边界情况 (Edge Cases)

| EC ID | 边界情况 | 处理方式 |
|-------|----------|----------|
| **EC-250-1** | `.sdd/` 和 `.specs/` 同时存在 | 优先使用 `.sdd/`（容器模式） |
| **EC-250-2** | 两者都不存在 | 抛出错误，提示运行 `/sdd init` |
| **EC-250-3** | 路径包含符号链接 | 使用 `fs.realpath` 解析真实路径 |
| **EC-250-4** | 环境变量路径无效 | 回退到自动检测 |
| **EC-250-5** | 目录权限不足 | 抛出明确错误信息 |
| **EC-250-6** | 并发检测竞争 | 使用缓存避免重复检测 |

---

## 8. 开放问题

| ID | 问题 | 状态 | 负责人 |
|----|------|------|--------|
| **OQ-250-1** | 是否需要支持多级工作空间嵌套？ | 待定 | 架构组 |
| **OQ-250-2** | 是否需要提供迁移向导工具？ | 待定 | v1.3.0 |
| **OQ-250-3** | 是否需要支持远程工作空间？ | 待定 | 未来版本 |

---

## 9. 验收标准

| ID | 验收项 | 验证方式 |
|----|--------|----------|
| **AC-250-1** | `src/utils/workspace.ts` 存在且导出必要函数 | 文件检查 |
| **AC-250-2** | `getSDDWorkspace()` 正确检测 `.sdd/` | 单元测试 |
| **AC-250-3** | `getSDDWorkspace()` 正确回退到 `.specs/` | 单元测试 |
| **AC-250-4** | `StateMachine` 使用新路径 API | 代码审查 |
| **AC-250-5** | `/sdd init` 创建 `.sdd/` 结构 | 手动测试 |
| **AC-250-6** | 旧 `.specs/` 项目仍能正常工作 | 回归测试 |
| **AC-250-7** | 环境变量 `SDD_WORKSPACE` 生效 | 手动测试 |
| **AC-250-8** | 单元测试覆盖率 > 80% | 测试报告 |

---

## 10. 风险与缓解

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| **路径检测逻辑错误** | 中 | 高 | 充分单元测试 + 回归测试 |
| **性能下降** | 低 | 中 | 使用缓存，性能测试 |
| **兼容性问题** | 中 | 高 | 保留旧路径支持，渐进迁移 |
| **用户困惑** | 中 | 中 | 清晰文档 + 日志提示 |

---

## 11. 附录

### 11.1 术语表

| 术语 | 定义 |
|------|------|
| **容器模式** | 使用 `.sdd/` 作为工作空间根目录 |
| **兼容模式** | 使用 `.specs/` 作为工作空间根目录（旧结构） |
| **工作空间** | SDD 规范存储的根目录 |
| **路径透明** | 用户无需关心底层路径，系统自动处理 |

### 11.2 参考

- 现有 `src/state/machine.ts` 实现
- 现有 `src/commands/sdd.ts` 实现
- SDD 6 阶段工作流文档
