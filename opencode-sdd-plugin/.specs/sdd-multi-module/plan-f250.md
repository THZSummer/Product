# Technical Plan: F-250 - SDD 容器化目录结构支持

| 元数据 | 值 |
|--------|-----|
| **Feature ID** | F-250 |
| **名称** | SDD 容器化目录结构支持 |
| **版本** | 2.0.0 |
| **创建日期** | 2026-03-31 |
| **状态** | planned |
| **优先级** | P0（前置基础设施） |
| **RICE 评分** | 80 |
| **预计工时** | 12 小时 |
| **关联 Roadmap** | v1.2.11 (Phase 2.5) |
| **依赖 Feature** | 无 |
| **Blocking** | F-251（子 Feature Spec 结构） |

---

## 1. 技术架构

### 1.1 模块结构

```
src/
├── utils/
│   └── workspace.ts          # 新增：工作空间识别核心模块
│       └── getSDDWorkspace()
│
├── state/
│   └── machine.ts            # 修改：适配 workspace API
│       └── StateMachine 类
│
├── commands/
│   └── sdd.ts                # 修改：适配 workspace API
│       └── /sdd 命令处理器
│
└── index.ts                  # 修改：适配 workspace API
    └── SDDPlugin

tests/
└── workspace.test.ts         # 新增：单元测试
```

### 1.2 路径识别流程

```
┌─────────────────────────────────────────┐
│         getSDDWorkspace()               │
└─────────────────────────────────────────┘
                    │
                    ▼
    ┌───────────────────────────────┐
    │  1. 检查环境变量 SDD_WORKSPACE │
    │     存在 → 返回环境变量值      │
    └───────────────────────────────┘
                    │ 不存在
                    ▼
    ┌───────────────────────────────┐
    │  2. 检查 .sdd/ 目录是否存在    │
    │     存在 → 返回 '.sdd'         │
    └───────────────────────────────┘
                    │ 不存在
                    ▼
    ┌───────────────────────────────┐
    │  3. 检查 .specs/ 目录是否存在  │
    │     存在 → 返回 '.'            │
    └───────────────────────────────┘
                    │ 不存在
                    ▼
    ┌───────────────────────────────┐
    │  4. 抛出错误                  │
    └───────────────────────────────┘
```

---

## 2. 实施方案

### 2.1 阶段 1: 实现工作空间识别模块 (2 小时)

**目标**: 创建 `src/utils/workspace.ts`

**任务**:
1. 创建 `src/utils/` 目录
2. 实现 `getSDDWorkspace()` 函数
3. 实现 `getSpecsDir()` 函数
4. 实现 `getFeatureDir()` 函数
5. 实现 `detectWorkspaceMode()` 函数
6. 实现 `initializeWorkspace()` 函数
7. 添加 JSDoc 注释

**技术细节**:
```typescript
// src/utils/workspace.ts
function getSDDWorkspace(): string {
  // 1. 检查环境变量
  if (process.env.SDD_WORKSPACE) return env;
  
  // 2. 优先查找 .sdd/ 目录
  if (existsSync('.sdd')) return '.sdd';
  
  // 3. 回退到 .specs/ 目录（兼容）
  if (existsSync('.specs')) return '.';
  
  throw new Error('未找到 SDD 工作空间');
}
```

### 2.2 阶段 2: 更新状态管理器 (3 小时)

**目标**: 修改 `src/state/machine.ts` 使用 workspace API

**任务**:
1. 导入 workspace 模块
2. 修改 `StateMachine` 构造函数
3. 更新所有路径相关代码
4. 保持向后兼容（支持显式传入 specsDir）

**技术细节**:
```typescript
// src/state/machine.ts 修改
import { getSpecsDir } from '../utils/workspace.js';

export class StateMachine {
  private stateFilePath: string;

  constructor(specsDir?: string) {
    // 支持显式传入（用于测试）
    const dir = specsDir || getSpecsDir();
    this.stateFilePath = path.join(dir, '.sdd', 'state.json');
  }

  // 其他方法保持不变
}
```

### 2.3 阶段 3: 更新命令系统 (2 小时)

**目标**: 修改 `src/commands/sdd.ts` 使用 workspace API

**任务**:
1. 导入 workspace 模块
2. 修改 `handleInit` 函数
3. 修改 `handleSpecify` 函数
4. 修改其他命令处理器中的路径逻辑

**技术细节**:
```typescript
// src/commands/sdd.ts 修改
import { getSpecsDir, initializeWorkspace } from '../utils/workspace.js';

async function handleInit(ctx: any) {
  // 初始化工作空间
  await initializeWorkspace();
  
  const specsDir = getSpecsDir();
  
  const dirs = [
    specsDir,
    path.join(specsDir, 'examples'),
    path.join(specsDir, 'architecture/adr'),
    path.join(specsDir, 'development'),
    path.join(specsDir, 'planning'),
    path.join(specsDir, 'project'),
    path.join(specsDir, 'quality'),
    '.opencode/sdd/api-docs'
  ];

  for (const dir of dirs) {
    await ctx.$`mkdir -p ${dir}`;
  }

  return `✅ SDD workflow initialized!`;
}

async function handleSpecify(ctx: any, feature: string) {
  const specsDir = getSpecsDir();
  const featureDir = path.join(specsDir, feature.replace(/\s+/g, '-').toLowerCase());
  
  await ctx.$`mkdir -p ${featureDir}`;
  
  return `📝 Creating specification for: ${feature}`;
}
```

### 2.4 阶段 4: 添加兼容层 (2 小时)

**目标**: 确保旧结构项目仍能正常工作

**任务**:
1. 实现路径回退逻辑
2. 添加错误处理
3. 验证旧 `.specs/` 项目仍能工作

**技术细节**:
```typescript
// 兼容模式：当仅存在 .specs/ 时
// getSDDWorkspace() 返回 '.'
// getSpecsDir() 返回 '.specs'
```

### 2.5 阶段 5: 编写单元测试 (2 小时)

**目标**: 确保工作空间识别逻辑正确

**测试用例**:
```typescript
// tests/workspace.test.ts
import { getSDDWorkspace, getSpecsDir } from '../src/utils/workspace';

describe('workspace', () => {
  describe('getSDDWorkspace', () => {
    it('should return .sdd when .sdd/ exists', () => {
      // Mock fs.existsSync to return true for .sdd
      expect(getSDDWorkspace()).toBe('.sdd');
    });

    it('should return . when .specs/ exists', () => {
      // Mock fs.existsSync to return true for .specs
      expect(getSDDWorkspace()).toBe('.');
    });

    it('should throw when neither exists', () => {
      expect(() => getSDDWorkspace()).toThrow();
    });

    it('should respect SDD_WORKSPACE env var', () => {
      process.env.SDD_WORKSPACE = '/custom/path';
      expect(getSDDWorkspace()).toBe('/custom/path');
    });
  });

  describe('getSpecsDir', () => {
    it('should return .sdd/.specs in container mode', () => {
      // Mock
      expect(getSpecsDir()).toBe('.sdd/.specs');
    });

    it('should return .specs in legacy mode', () => {
      // Mock
      expect(getSpecsDir()).toBe('.specs');
    });
  });
});
```

### 2.6 阶段 6: 文档更新 (1 小时)

**目标**: 更新代码注释和文档

**任务**:
1. 更新 `src/utils/workspace.ts` JSDoc 注释
2. 更新 README.md 中的目录结构说明
3. 添加工作空间模式说明文档

---

---

## 3. 技术决策

### 3.1 路径检测策略

**决策**: 优先检测 `.sdd/`，回退到 `.specs/`

**理由**:
- 支持新结构优先
- 保持向后兼容
- 用户无感知切换

**替代方案**:
- 方案 A: 仅支持 `.sdd/`（❌ 破坏兼容性）
- 方案 B: 仅支持 `.specs/`（❌ 无法支持新结构）
- 方案 C: 自动检测（✅ 推荐）

### 3.2 缓存策略

**决策**: 使用模块级变量缓存检测结果

**理由**:
- 避免重复文件系统操作
- 性能优化
- 单次检测足够（运行时目录不会变化）

**实现**:
```typescript
let cachedWorkspace: string | null = null;
```

### 3.3 配置优先级

**决策**: 环境变量 > 自动检测 > 配置文件

**理由**:
- 环境变量便于 CI/CD 配置
- 自动检测便于本地开发
- 配置文件作为备选

---

## 4. 实施步骤

### 4.1 前置条件

- [ ] 确认 TypeScript 配置支持 ES 模块
- [ ] 备份现有源代码
- [ ] 确认测试框架可用

### 4.2 执行步骤

```bash
# Step 1: 创建 utils 目录
mkdir -p src/utils

# Step 2: 创建 workspace.ts
cat > src/utils/workspace.ts << 'EOF'
// ... (完整实现代码)
EOF

# Step 3: 更新 state/machine.ts
# 修改导入和构造函数

# Step 4: 更新 commands/sdd.ts
# 修改所有路径相关代码

# Step 5: 更新 index.ts
# 修改插件入口

# Step 6: 运行测试
npm test

# Step 7: 验证功能
/sdd init
/sdd specify test-feature
```

### 4.3 验证步骤

```bash
# 验证容器模式
mkdir -p .sdd
node -e "console.log(require('./dist/utils/workspace').getSDDWorkspace())"
# 应输出：.sdd

# 验证兼容模式
rm -rf .sdd
mkdir -p .specs
node -e "console.log(require('./dist/utils/workspace').getSDDWorkspace())"
# 应输出：.

# 验证错误处理
rm -rf .sdd .specs
node -e "require('./dist/utils/workspace').getSDDWorkspace()"
# 应抛出错误
```

---

## 5. 风险评估

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| **路径检测逻辑错误** | 中 | 高 | 充分单元测试 |
| **性能下降** | 低 | 中 | 使用缓存 |
| **兼容性问题** | 中 | 高 | 回归测试 |
| **TypeScript 编译错误** | 低 | 中 | 增量编译检查 |

---

## 6. 验收标准

| ID | 验收项 | 验证方式 |
|----|--------|----------|
| **AC-250-1** | `src/utils/workspace.ts` 存在 | 文件检查 |
| **AC-250-2** | 所有导出函数正常工作 | 单元测试 |
| **AC-250-3** | `StateMachine` 适配新 API | 集成测试 |
| **AC-250-4** | `/sdd` 命令正常工作 | 手动测试 |
| **AC-250-5** | 容器模式正确识别 | 手动测试 |
| **AC-250-6** | 兼容模式正确识别 | 手动测试 |
| **AC-250-7** | 环境变量生效 | 手动测试 |
| **AC-250-8** | 测试覆盖率 > 80% | 测试报告 |

---

## 7. 后续工作

### 7.1 立即执行

- [ ] 实现 workspace.ts
- [ ] 更新所有路径引用
- [ ] 编写单元测试

### 7.2 后续优化（v1.3.0）

- [ ] 添加自动迁移工具
- [ ] 支持配置文件 `.sdd/config.json`
- [ ] 支持远程工作空间

---

## 8. 附录

### 8.1 文件变更清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `src/utils/workspace.ts` | 新建 | 工作空间识别模块 |
| `src/state/machine.ts` | 修改 | 适配 workspace API |
| `src/commands/sdd.ts` | 修改 | 适配 workspace API |
| `tests/workspace.test.ts` | 新建 | 单元测试 |

### 8.2 代码行数预估

| 文件 | 新增行数 | 修改行数 |
|------|----------|----------|
| `workspace.ts` | ~30 | - |
| `machine.ts` | ~5 | ~10 |
| `sdd.ts` | ~5 | ~30 |
| `workspace.test.ts` | ~50 | - |
| **总计** | **~90** | **~40** |
