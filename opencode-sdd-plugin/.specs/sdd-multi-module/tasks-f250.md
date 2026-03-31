# 任务分解：F-250 - SDD 容器化目录结构支持

| 元数据 | 值 |
|--------|-----|
| **Feature ID** | F-250 |
| **名称** | SDD 容器化目录结构支持 |
| **版本** | 2.0.0 |
| **创建日期** | 2026-03-31 |
| **状态** | tasked |
| **总任务数** | 6 个 |
| **预计工时** | 12 小时 |
| **关联 Plan** | `plan-f250.md` |
| **依赖关系** | 无 |
| **Blocking** | F-251（子 Feature Spec 结构） |

---

## 任务列表

| 任务 ID | 任务名称 | 工时 | 优先级 | 依赖 |
|---------|----------|------|--------|------|
| TASK-250-001 | 实现工作空间识别 | 2h | P0 | 无 |
| TASK-250-002 | 更新状态管理器 | 3h | P0 | TASK-250-001 |
| TASK-250-003 | 更新命令系统 | 2h | P0 | TASK-250-001 |
| TASK-250-004 | 添加兼容层 | 2h | P0 | TASK-250-001 |
| TASK-250-005 | 单元测试 | 2h | P0 | TASK-250-001,002,003 |
| TASK-250-006 | 文档更新 | 1h | P1 | TASK-250-001 |

---

## 并行执行组

### 组 1: 核心模块 (必须首先完成)
- [ ] TASK-250-001: 实现工作空间识别

### 组 2: 系统集成 (等待组 1)
- [ ] TASK-250-002: 更新状态管理器
- [ ] TASK-250-003: 更新命令系统
- [ ] TASK-250-004: 添加兼容层

### 组 3: 质量保障 (等待组 2)
- [ ] TASK-250-005: 单元测试
- [ ] TASK-250-006: 文档更新

---

## 任务详情

---

## TASK-250-001: 实现工作空间识别

| 属性 | 值 |
|------|-----|
| **功能需求** | FR-250-1-1, FR-250-1-2, FR-250-1-3, FR-250-1-4 |
| **优先级** | P0 |
| **预估工时** | 2 小时 |
| **依赖任务** | 无 |
| **并行组** | 组 1 |
| **负责人** | TBD |

### 工作内容
- [ ] 创建 `src/utils/` 目录
- [ ] 创建 `src/utils/workspace.ts` 文件
- [ ] 实现 `getSDDWorkspace()` 函数
- [ ] 实现 `getSpecsDir()` 函数
- [ ] 实现 `getFeatureDir(featureId)` 函数
- [ ] 添加 JSDoc 注释

### 交付物
- `src/utils/workspace.ts` - 工作空间识别模块

### 验收标准
- [ ] 模块导出所有必要函数
- [ ] `getSDDWorkspace()` 正确检测 `.sdd/`
- [ ] `getSDDWorkspace()` 正确回退到 `.specs/`
- [ ] 环境变量 `SDD_WORKSPACE` 生效
- [ ] 抛出错误时信息清晰
- [ ] TypeScript 编译通过

### 核心设计
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

### 执行命令
```bash
# 创建目录
mkdir -p src/utils

# 创建文件
cat > src/utils/workspace.ts << 'EOF'
// 实现代码
EOF

# 编译检查
npx tsc --noEmit src/utils/workspace.ts
```

---

## TASK-250-002: 更新状态管理器

| 属性 | 值 |
|------|-----|
| **功能需求** | FR-250-2-1, FR-250-2-2, FR-250-2-3 |
| **优先级** | P0 |
| **预估工时** | 3 小时 |
| **依赖任务** | TASK-250-001 |
| **并行组** | 组 2 |
| **负责人** | TBD |

### 工作内容
- [ ] 在 `src/state/machine.ts` 中导入 workspace 模块
- [ ] 修改 `StateMachine` 构造函数使用 `getSpecsDir()`
- [ ] 保持向后兼容（支持显式传入 specsDir 参数）
- [ ] 更新所有路径相关代码
- [ ] 验证 state.json 读写路径正确

### 交付物
- 更新后的 `src/state/machine.ts`

### 验收标准
- [ ] 导入 workspace 模块
- [ ] 构造函数默认使用 `getSpecsDir()`
- [ ] 显式传入 specsDir 时优先使用
- [ ] state.json 路径正确
- [ ] TypeScript 编译通过
- [ ] 现有测试通过（回归测试）

### 技术说明
```typescript
// 修改前
constructor(private specsDir: string = '.specs') {
  this.stateFilePath = path.join(specsDir, '.sdd', 'state.json');
}

// 修改后
import { getSpecsDir } from '../utils/workspace.js';

constructor(specsDir?: string) {
  const dir = specsDir || getSpecsDir();
  this.stateFilePath = path.join(dir, '.sdd', 'state.json');
}
```

### 执行命令
```bash
# 编译检查
npx tsc --noEmit src/state/machine.ts

# 运行状态相关测试
npm test -- state
```

---

## TASK-250-003: 更新命令系统

| 属性 | 值 |
|------|-----|
| **功能需求** | FR-250-3-1, FR-250-3-2, FR-250-3-3 |
| **优先级** | P0 |
| **预估工时** | 2 小时 |
| **依赖任务** | TASK-250-001 |
| **并行组** | 组 2 |
| **负责人** | TBD |

### 工作内容
- [ ] 在 `src/commands/sdd.ts` 中导入 workspace 模块
- [ ] 修改 `handleInit` 函数使用 `getSpecsDir()`
- [ ] 修改 `handleSpecify` 函数使用 `getFeatureDir()`
- [ ] 修改其他命令处理器中的路径逻辑
- [ ] 更新帮助文档中的路径说明

### 交付物
- 更新后的 `src/commands/sdd.ts`

### 验收标准
- [ ] 所有命令使用动态路径
- [ ] `/sdd init` 创建正确结构
- [ ] `/sdd specify` 创建规范到正确目录
- [ ] 无硬编码路径字符串
- [ ] TypeScript 编译通过

### 技术说明
```typescript
// 需要修改的函数
- handleInit()      // 使用 getSpecsDir()
- handleSpecify()   // 使用 getFeatureDir()
- handleStatus()    // 使用 getSpecsDir()
- 其他路径相关逻辑
```

### 执行命令
```bash
# 编译检查
npx tsc --noEmit src/commands/sdd.ts

# 测试命令
/sdd init
/sdd specify test-feature
```

---

## TASK-250-004: 添加兼容层

| 属性 | 值 |
|------|-----|
| **功能需求** | FR-250-4-1, FR-250-4-2, FR-250-4-3 |
| **优先级** | P0 |
| **预估工时** | 2 小时 |
| **依赖任务** | TASK-250-001 |
| **并行组** | 组 2 |
| **负责人** | TBD |

### 工作内容
- [ ] 实现路径回退逻辑（在 workspace.ts 中完成）
- [ ] 验证旧 `.specs/` 项目仍能工作
- [ ] 添加错误处理和友好提示
- [ ] 实现环境变量支持

### 交付物
- 兼容层实现（在 workspace.ts 中）
- 错误处理逻辑

### 验收标准
- [ ] 仅存在 `.specs/` 时系统正常工作
- [ ] 环境变量 `SDD_WORKSPACE` 生效
- [ ] 错误信息清晰指示问题
- [ ] 回归测试通过

### 技术说明
```typescript
// 兼容模式：当仅存在 .specs/ 时
// getSDDWorkspace() 返回 '.'
// getSpecsDir() 返回 '.specs'
```

### 执行命令
```bash
# 测试兼容模式
rm -rf .sdd
mkdir -p .specs
node -e "require('./dist/utils/workspace').getSDDWorkspace()"

# 测试环境变量
SDD_WORKSPACE=/custom/path node -e "console.log(require('./dist/utils/workspace').getSDDWorkspace())"
```

---

## TASK-250-005: 单元测试

| 属性 | 值 |
|------|-----|
| **功能需求** | NFR-250-7 |
| **优先级** | P0 |
| **预估工时** | 2 小时 |
| **依赖任务** | TASK-250-001, TASK-250-002, TASK-250-003 |
| **并行组** | 组 3 |
| **负责人** | TBD |

### 工作内容
- [ ] 创建测试文件 `tests/workspace.test.ts`
- [ ] 编写 `getSDDWorkspace()` 测试用例
- [ ] 编写 `getSpecsDir()` 测试用例
- [ ] 编写 `getFeatureDir()` 测试用例
- [ ] 编写 `detectWorkspaceMode()` 测试用例
- [ ] 编写边界情况测试
- [ ] 运行测试并修复问题
- [ ] 生成覆盖率报告

### 交付物
- `tests/workspace.test.ts` - 测试文件
- 测试覆盖率报告

### 验收标准
- [ ] 所有测试用例通过
- [ ] 测试覆盖率 > 80%
- [ ] 边界情况有测试覆盖
- [ ] 测试可重复运行

### 测试用例
```typescript
describe('getSDDWorkspace', () => {
  it('should return .sdd when .sdd/ exists', () => {});
  it('should return . when .specs/ exists', () => {});
  it('should throw when neither exists', () => {});
  it('should respect SDD_WORKSPACE env var', () => {});
  it('should use cache on subsequent calls', () => {});
});

describe('getSpecsDir', () => {
  it('should return .sdd/.specs in container mode', () => {});
  it('should return .specs in legacy mode', () => {});
});

describe('getFeatureDir', () => {
  it('should return correct feature path', () => {});
  it('should sanitize feature name', () => {});
});
```

### 执行命令
```bash
# 运行测试
npm test -- workspace

# 生成覆盖率报告
npm test -- --coverage

# 查看覆盖率
cat coverage/lcov-report/index.html
```

---

## TASK-250-006: 文档更新

| 属性 | 值 |
|------|-----|
| **功能需求** | FR-250-5-2 |
| **优先级** | P1 |
| **预估工时** | 1 小时 |
| **依赖任务** | TASK-250-001 |
| **并行组** | 组 3 |
| **负责人** | TBD |

### 工作内容
- [ ] 更新 `src/utils/workspace.ts` JSDoc 注释
- [ ] 更新 README.md 中的目录结构说明
- [ ] 添加工作空间模式说明文档
- [ ] 更新迁移指南（可选）

### 交付物
- 更新的代码注释
- 更新的 README.md
- 工作空间说明文档

### 验收标准
- [ ] 所有导出函数有 JSDoc 注释
- [ ] README.md 包含新目录结构说明
- [ ] 文档清晰说明容器模式和兼容模式
- [ ] 示例代码正确

### 文档模板
```markdown
## 工作空间结构

SDD 插件支持两种工作空间模式：

### 容器模式（推荐）
```
.sdd/
├── .specs/
│   └── [feature]/
```

### 兼容模式
```
.specs/
└── [feature]/
```

系统会自动检测并选择合适模式。
```

### 执行命令
```bash
# 生成 API 文档
npx typedoc src/utils/workspace.ts

# 预览文档
open docs/index.html
```

---

## 执行顺序

```
TASK-250-001: 实现工作空间识别 (2h)
        ↓
┌───────┴───────┬───────────────┬───────────────┐
│               │               │               │
TASK-250-002   TASK-250-003   TASK-250-004   
更新状态管理器  更新命令系统    添加兼容层      
(3h)           (2h)           (2h)           
│               │               │               
└───────┬───────┴───────────────┘               
        ↓                                       
TASK-250-005: 单元测试 (2h)                     
        ↓                                       
TASK-250-006: 文档更新 (1h)                     

总工时：12 小时
```

---

## 验收清单

| ID | 验收项 | 验证方式 | 状态 |
|----|--------|----------|------|
| **AC-250-1** | `src/utils/workspace.ts` 存在 | 文件检查 | ⏳ |
| **AC-250-2** | `getSDDWorkspace()` 正确检测 | 单元测试 | ⏳ |
| **AC-250-3** | `getSpecsDir()` 返回正确路径 | 单元测试 | ⏳ |
| **AC-250-4** | `StateMachine` 适配新 API | 集成测试 | ⏳ |
| **AC-250-5** | `/sdd` 命令正常工作 | 手动测试 | ⏳ |
| **AC-250-6** | 容器模式正确识别 | 手动测试 | ⏳ |
| **AC-250-7** | 兼容模式正确识别 | 手动测试 | ⏳ |
| **AC-250-8** | 环境变量生效 | 手动测试 | ⏳ |
| **AC-250-9** | 测试覆盖率 > 80% | 测试报告 | ⏳ |
| **AC-250-10** | 文档完整 | 文档审查 | ⏳ |

---

## 回滚方案

如实现失败，执行以下回滚：

```bash
# 1. 恢复源代码
git checkout src/

# 2. 删除新文件
rm -rf src/utils/

# 3. 验证回滚
npm test
```

---

## 附录

### A. 文件变更清单

| 文件 | 操作 | 任务 | 工时 |
|------|------|------|------|
| `src/utils/workspace.ts` | 新建 | TASK-250-001 | 2h |
| `src/state/machine.ts` | 修改 | TASK-250-002 | 3h |
| `src/commands/sdd.ts` | 修改 | TASK-250-003 | 2h |
| 兼容层 | 实现 | TASK-250-004 | 2h |
| `tests/workspace.test.ts` | 新建 | TASK-250-005 | 2h |
| 文档 | 更新 | TASK-250-006 | 1h |
| **总计** | - | - | **12h** |

### B. 时间估算明细

| 任务 | 工时 | 复杂度 | 风险 |
|------|------|--------|------|
| TASK-250-001 | 2h | 中 | 低 |
| TASK-250-002 | 3h | 中 | 中 |
| TASK-250-003 | 2h | 中 | 中 |
| TASK-250-004 | 2h | 低 | 低 |
| TASK-250-005 | 2h | 中 | 低 |
| TASK-250-006 | 1h | 低 | 低 |
| **总计** | **12h** | - | - |
