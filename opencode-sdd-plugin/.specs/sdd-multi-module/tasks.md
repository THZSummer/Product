# 任务分解：SDD 多子 Feature 并行开发支持

| 元数据 | 值 |
|--------|-----|
| **Feature ID** | sdd-multi-module |
| **Feature 名称** | SDD 多子 Feature 并行开发支持 |
| **规范版本** | 1.2.5 |
| **创建日期** | 2026-03-31 |
| **状态** | tasked |
| **总任务数** | 10 个 |
| **预计工时** | 39 小时 (5 人天) |
| **关联 Plan** | `.specs/sdd-multi-module/plan.md` |

---

## 并行执行组

### 组 0: 基础设施（优先执行）
- [ ] TASK-250-001: 实现工作空间识别 (2h)
- [ ] TASK-250-002: 更新状态管理器 (3h)
- [ ] TASK-250-003: 更新命令系统 (2h)
- [ ] TASK-250-004: 添加兼容层 (2h)
- [ ] TASK-250-005: 单元测试 (2h)
- [ ] TASK-250-006: 文档更新 (1h)

### 组 1: 核心状态管理基础 (可并行)
- [ ] TASK-000: 创建 Feature README 模板生成器 (新增，FR-254-5)
- [ ] TASK-001: 定义 State Schema v1.2.5 (FR-252-1, FR-252-6)
- [ ] TASK-002: 实现状态迁移工具 (FR-252-5)
- [ ] TASK-003: 创建子 Feature 目录结构模板 (FR-251-1, FR-251-2)

### 组 2: 状态管理核心 (等待组 1)
- [ ] TASK-004: 实现多子 Feature 状态管理器 (FR-252-2, FR-252-3, FR-252-4)
- [ ] TASK-005: 实现子 Feature 目录管理 (FR-251-3, FR-251-4)

### 组 3: 并行任务机制 (等待组 2)
- [ ] TASK-006: 实现 tasks.md 解析器 (FR-253-1, FR-253-2, FR-253-3)
- [ ] TASK-007: 实现依赖就绪通知 (FR-253-4, FR-253-5)

### 组 4: 集成测试 (等待组 3)
- [ ] TASK-008: 端到端测试 (原 TASK-013, FR-254-5)
- [ ] TASK-009: 向后兼容测试 (原 TASK-014, NFR-101~104)

---

## 任务详情

## TASK-000: 创建 Feature README 模板生成器

| 属性 | 值 |
|------|-----|
| **功能需求** | FR-254-5 |
| **优先级** | P0 |
| **预估工时** | 4 小时 |
| **依赖任务** | 无 |
| **并行组** | 组 1 |
| **负责人** | TBD |

### 工作内容
- [ ] 实现 Feature README 模板生成逻辑
- [ ] 实现子 Feature README 模板生成逻辑
- [ ] 设计模板结构（导航、子 Feature 列表、快速开始）
- [ ] 编写生成器单元测试

### 交付物
- `src/utils/readme-generator.ts` - README 模板生成器 (约 150 行)
- `src/utils/readme-generator.test.ts` - 单元测试 (约 100 行)

### 验收标准
- [ ] 可生成 Feature 级 README.md（包含导航和子 Feature 列表）
- [ ] 可生成子 Feature 级 README.md（包含范围、依赖、接口）
- [ ] 模板支持自定义参数（Feature 名、子 Feature 列表等）
- [ ] 输出 Markdown 格式规范
- [ ] 单元测试覆盖率 > 80%

### 技术说明
```typescript
// README 生成器接口
interface ReadmeTemplate {
  featureName: string;
  subFeatures: SubFeatureInfo[];
}

interface SubFeatureInfo {
  id: string;
  name: string;
  dir: string;
  status: string;
  assignee?: string;
}

// 生成 Feature README
function generateFeatureReadme(template: ReadmeTemplate): string {
  return `# Feature: ${template.featureName}

快速导航：
- 📋 [Spec](spec.md) - 需求规格
- 🏗️ [Plan](plan.md) - 技术规划
- 📝 [Tasks](tasks.md) - 任务分解

## 子 Feature 列表

| 子 Feature | 状态 | 负责人 |
|------------|------|--------|
${template.subFeatures.map(sf => `| [${sf.name}](sub-features/${sf.dir}/) | ${sf.status} | ${sf.assignee || '-'} |`).join('\n')}

## 快速开始
1. 阅读 [spec.md](spec.md) 了解整体需求
2. 选择负责的子 Feature 进入对应目录
3. 查看子 Feature 的 README.md 了解详细范围
`;
}
```

---

## TASK-001: 定义 State Schema v1.2.5

| 属性 | 值 |
|------|-----|
| **功能需求** | FR-252-1, FR-252-6 |
| **优先级** | P0 |
| **预估工时** | 2 小时 |
| **依赖任务** | 无 |
| **并行组** | 组 1 |
| **负责人** | TBD |

### 工作内容
- [ ] 定义 v1.2.5 State JSON Schema，包含 subFeatures 数组和 dependencies 对象
- [ ] 添加 mode 字段区分 single/multi 模式
- [ ] 实现 Schema 验证逻辑（使用 ajv）
- [ ] 编写 Schema 单元测试

### 交付物
- `src/state/schema-v1.2.5.ts` - State Schema 定义 (约 150 行)
- `src/state/schema-v1.2.5.test.ts` - Schema 验证测试 (约 80 行)

### 验收标准
- [ ] JSON Schema 包含所有必需字段 (feature, status, version, mode, subFeatures, dependencies)
- [ ] subFeatures 数组支持子 Feature 级状态 (id, dir, status, stateFile)
- [ ] dependencies 对象支持子 Feature 依赖关系图
- [ ] 使用 ajv 验证 Schema 有效性
- [ ] 单元测试覆盖率 > 80%

### 技术说明
```typescript
// Schema 核心结构
interface MultiFeatureState {
  feature: string;
  version: "1.2.5";
  status: FeatureStatus;
  mode: "single" | "multi";
  subFeatures?: SubFeatureRef[];  // 仅 multi 模式
  dependencies?: Record<string, string[]>;  // 子 Feature 依赖图
  createdAt: string;
  updatedAt: string;
}

interface SubFeatureRef {
  id: string;
  dir: string;
  status: FeatureStatus;
  stateFile: string;
}
```

---

## TASK-002: 实现状态迁移工具

| 属性 | 值 |
|------|-----|
| **功能需求** | FR-252-5 |
| **优先级** | P1 |
| **预估工时** | 3 小时 |
| **依赖任务** | TASK-001 |
| **并行组** | 组 1 |
| **负责人** | TBD |

### 工作内容
- [ ] 实现状态版本检测逻辑
- [ ] 实现 v1.1.1 → v1.2.5 迁移逻辑
- [ ] 创建备份机制（迁移前自动备份到 .state.backup/）
- [ ] 实现回滚功能
- [ ] 编写迁移测试

### 交付物
- `src/state/migrator.ts` - 状态迁移工具 (约 120 行)
- `src/state/migrator.test.ts` - 迁移测试 (约 100 行)

### 验收标准
- [ ] 自动检测旧格式 state.json（无 version 字段或 version < 1.2.5）
- [ ] 迁移前创建备份到 `.state.backup/state-YYYYMMDD-HHMMSS.json`
- [ ] 单模块格式升级为 multi 模式，subFeatures 数组包含单个子 Feature
- [ ] 迁移失败时可回滚到备份
- [ ] 迁移日志输出清晰

### 技术说明
```typescript
// 迁移流程
async function migrateState(oldState: any): Promise<MultiFeatureState> {
  // 1. 备份旧状态
  await backupState(oldState);
  
  // 2. 检测版本并执行相应迁移
  if (!oldState.version || oldState.version === '1.1.1') {
    return migrateFrom111(oldState);
  }
  
  // 3. 验证新状态符合 v1.2.5 Schema
  validateState(newState);
  
  return newState;
}

// 单模块 → 多子 Feature 转换
function migrateFrom111(oldState: any): MultiFeatureState {
  return {
    ...oldState,
    version: '1.2.5',
    mode: 'multi',
    subFeatures: [{
      id: inferSubFeatureId(oldState.feature),
      dir: 'sub-features/' + inferSubFeatureId(oldState.feature),
      status: oldState.status,
      stateFile: `.specs/${oldState.feature}/.state.json`
    }],
    dependencies: {}
  };
}
```

---

## TASK-003: 创建子 Feature 目录结构模板

| 属性 | 值 |
|------|-----|
| **功能需求** | FR-251-1, FR-251-2 |
| **优先级** | P0 |
| **预估工时** | 4 小时 |
| **依赖任务** | 无 |
| **并行组** | 组 1 |
| **负责人** | TBD |

### 工作内容
- [ ] 设计主 spec.md 模板（包含子 Feature 索引表）
- [ ] 设计子 Feature 文档模板（sub-features/[id]/*.md）
- [ ] 设计 README.md 模板（Feature 级和子 Feature 级）
- [ ] 创建 sub-features 目录模板
- [ ] 设计跨子 Feature 协同信息结构
- [ ] 创建示例模板文件

### 交付物
- `templates/spec-multi-feature.md` - 主 spec 模板 (约 80 行)
- `templates/sub-feature-doc.md` - 子 Feature 文档模板 (约 100 行)
- `templates/readme-feature.md` - Feature README 模板 (约 50 行)
- `templates/readme-sub-feature.md` - 子 Feature README 模板 (约 60 行)
- `templates/EXAMPLE.md` - 完整示例 (约 200 行)

### 验收标准
- [ ] 主 spec.md 包含子 Feature 索引表（子 Feature ID、名称、目录路径、状态、负责人、阻塞依赖）
- [ ] 子 Feature 索引表为 Markdown 表格格式
- [ ] README.md 模板包含导航和子 Feature 列表
- [ ] 子 Feature 目录结构完整（README.md + spec.md + .state.json）
- [ ] 跨子 Feature 协同信息包含接口约定和数据流
- [ ] 模板可直接用于新项目

### 技术说明
```markdown
# 主 spec.md 模板结构

## 概述
[全局概述]

## 目标
[Feature 级目标]

## 非目标
[Feature 级非目标]

---

## 子 Feature 索引

| 子 Feature ID | 子 Feature 名称 | 目录路径 | 状态 | 负责人 | 阻塞依赖 |
|---------------|-----------------|----------|------|--------|----------|
| sub-feature-1 | 子 Feature 1 | sub-features/sub-feature-1 | specified | - | - |

---

## 跨子 Feature 协同

### 接口约定
[接口表格]

### 数据流
[数据流图]
```

---

## TASK-004: 实现多子 Feature 状态管理器

| 属性 | 值 |
|------|-----|
| **功能需求** | FR-252-2, FR-252-3, FR-252-4 |
| **优先级** | P0 |
| **预估工时** | 6 小时 |
| **依赖任务** | TASK-001, TASK-002 |
| **并行组** | 组 2 |
| **负责人** | TBD |

### 工作内容
- [ ] 实现状态聚合逻辑（Feature 状态 = 最慢子 Feature 状态）
- [ ] 实现子 Feature 依赖关系图构建
- [ ] 实现阻塞关系计算（blockedBy 字段）
- [ ] 实现循环依赖检测（DFS 算法）
- [ ] 集成到现有状态机

### 交付物
- `src/state/multi-feature.ts` - 多子 Feature 状态管理核心 (约 200 行)
- `src/utils/dependency-graph.ts` - 依赖图计算工具 (约 150 行)
- `src/state/multi-feature.test.ts` - 单元测试 (约 150 行)

### 验收标准
- [ ] 状态聚合正确：Feature 状态等于所有子 Feature 中最慢的状态
- [ ] 依赖关系图可查询任意子 Feature 的依赖和被依赖关系
- [ ] blockedBy 字段自动计算并更新
- [ ] 循环依赖检测准确，发现循环时报错并阻止
- [ ] 与现有状态机无缝集成

### 技术说明
```typescript
/**
 * 聚合子 Feature 状态计算 Feature 整体状态
 * 规则：Feature 状态 = 最慢子 Feature 的状态
 */
function aggregateFeatureState(subFeatures: SubFeatureRef[]): FeatureStatus {
  if (subFeatures.length === 0) return 'specified';
  
  const statusOrder: FeatureStatus[] = [
    'specified', 'planned', 'tasked', 'implementing', 'reviewing', 'validated', 'completed'
  ];
  
  let slowestStatus = subFeatures[0].status;
  for (const sf of subFeatures) {
    if (statusOrder.indexOf(sf.status) < statusOrder.indexOf(slowestStatus)) {
      slowestStatus = sf.status;
    }
  }
  return slowestStatus;
}

/**
 * 循环依赖检测 (DFS)
 */
function detectCircularDependency(
  subFeatureId: string,
  dependencies: Record<string, string[]>,
  visited = new Set<string>(),
  path: string[] = []
): string[] | null {
  if (path.includes(subFeatureId)) {
    return [...path, subFeatureId]; // 返回循环路径
  }
  if (visited.has(subFeatureId)) return null;
  
  visited.add(subFeatureId);
  path.push(subFeatureId);
  
  for (const dep of (dependencies[subFeatureId] || [])) {
    const cycle = detectCircularDependency(dep, dependencies, visited, [...path]);
    if (cycle) return cycle;
  }
  
  return null;
}
```

---

## TASK-005: 实现子 Feature 目录管理

| 属性 | 值 |
|------|-----|
| **功能需求** | FR-251-3, FR-251-4 |
| **优先级** | P0 |
| **预估工时** | 4 小时 |
| **依赖任务** | TASK-003 |
| **并行组** | 组 2 |
| **负责人** | TBD |

### 工作内容
- [ ] 实现 sub-features/目录创建和初始化
- [ ] 实现子 Feature 文档读取和写入
- [ ] 实现子 Feature 索引表自动生成
- [ ] 实现单模块模式检测（向后兼容）
- [ ] 实现子 Feature 文档完整性验证

### 交付物
- `src/utils/subfeature-manager.ts` - 子 Feature 目录管理工具 (约 180 行)
- `src/utils/subfeature-manager.test.ts` - 单元测试 (约 100 行)

### 验收标准
- [ ] 自动检测 Feature 是单模块还是多子 Feature 模式
- [ ] 多子 Feature 模式下自动创建 sub-features/ 目录
- [ ] 子 Feature 索引表自动从 sub-features/ 目录生成
- [ ] 单模块模式下正常工作（向后兼容）
- [ ] 子 Feature 文档缺失时告警

### 技术说明
```typescript
/**
 * 检测 Feature 模式
 */
async function detectFeatureMode(featurePath: string): Promise<'single' | 'multi'> {
  const subFeaturesPath = path.join(featurePath, 'sub-features');
  const specPath = path.join(featurePath, 'spec.md');
  
  if (await fs.exists(subFeaturesPath)) {
    const subFeatureDirs = await fs.readdir(subFeaturesPath);
    if (subFeatureDirs.filter(d => !d.startsWith('.')).length > 0) {
      return 'multi';
    }
  }
  return 'single';
}

/**
 * 生成子 Feature 索引表
 */
async function generateSubFeatureIndex(featurePath: string): Promise<string> {
  const subFeaturesPath = path.join(featurePath, 'sub-features');
  const subFeatureDirs = await fs.readdir(subFeaturesPath);
  
  const rows = await Promise.all(
    subFeatureDirs
      .filter(d => !d.startsWith('.'))
      .map(async (dir) => {
        const content = await fs.readFile(path.join(subFeaturesPath, dir, 'spec.md'), 'utf-8');
        const meta = parseSubFeatureMeta(content);
        return `| ${meta.id} | ${meta.name} | sub-features/${dir} | ${meta.status} | ${meta.assignee || '-'} | - |`;
      })
  );
  
  return [
    '| 子 Feature ID | 子 Feature 名称 | 目录路径 | 状态 | 负责人 | 阻塞依赖 |',
    '|---------------|-----------------|----------|------|--------|----------|',
    ...rows
  ].join('\n');
}
```

---

## TASK-006: 实现 tasks.md 解析器

| 属性 | 值 |
|------|-----|
| **功能需求** | FR-253-1, FR-253-2, FR-253-3 |
| **优先级** | P0 |
| **预估工时** | 4 小时 |
| **依赖任务** | TASK-004 |
| **并行组** | 组 3 |
| **负责人** | TBD |

### 工作内容
- [ ] 实现并行分组声明语法解析
- [ ] 实现组内并行、组间串行语义
- [ ] 实现跨组依赖声明解析
- [ ] 实现依赖解析和排序
- [ ] 编写解析器测试

### 交付物
- `src/utils/tasks-parser.ts` - tasks.md 解析器 (约 200 行)
- `src/utils/tasks-parser.test.ts` - 单元测试 (约 150 行)

### 验收标准
- [ ] 正确解析并行分组声明语法（### 组 X: 名称）
- [ ] 组内任务标记为可并行
- [ ] 组间依赖正确解析（等待组 X 完成）
- [ ] 跨组依赖声明正确解析（依赖：TASK-XXX）
- [ ] 输出可执行的任务顺序

### 技术说明
```typescript
interface ParallelGroup {
  id: number;
  name: string;
  tasks: ParsedTask[];
  dependencies: number[]; // 依赖的组 ID
}

interface ParsedTask {
  id: string; // TASK-XXX
  description: string;
  assignee?: string;
  dependencies: string[]; // 依赖的任务 ID
}

/**
 * 解析 tasks.md 中的并行分组
 */
function parseParallelGroups(markdown: string): ParallelGroup[] {
  const groups: ParallelGroup[] = [];
  const groupRegex = /###\s*组\s*(\d+):\s*([^\n]+)\n([\s\S]*?)(?=### 组|$)/g;
  
  let match;
  while ((match = groupRegex.exec(markdown)) !== null) {
    groups.push({
      id: parseInt(match[1]),
      name: match[2].trim(),
      tasks: parseTasks(match[3]),
      dependencies: parseGroupDependencies(match[2])
    });
  }
  
  return groups;
}

/**
 * 计算任务执行顺序（考虑依赖）
 */
function computeExecutionOrder(groups: ParallelGroup[]): ParallelGroup[][] {
  // 拓扑排序，返回波次
  const waves: ParallelGroup[][] = [];
  const completed = new Set<number>();
  const remaining = new Set(groups.map(g => g.id));
  
  while (remaining.size > 0) {
    const readyWave: ParallelGroup[] = [];
    for (const groupId of remaining) {
      const group = groups.find(g => g.id === groupId)!;
      const depsMet = group.dependencies.every(dep => completed.has(dep));
      if (depsMet) {
        readyWave.push(group);
      }
    }
    
    if (readyWave.length === 0) {
      throw new Error('检测到循环依赖');
    }
    
    waves.push(readyWave);
    readyWave.forEach(g => {
      completed.add(g.id);
      remaining.delete(g.id);
    });
  }
  
  return waves;
}
```

---

## TASK-007: 实现依赖就绪通知

| 属性 | 值 |
|------|-----|
| **功能需求** | FR-253-4, FR-253-5 |
| **优先级** | P1 |
| **预估工时** | 2 小时 |
| **依赖任务** | TASK-004, TASK-006 |
| **并行组** | 组 3 |
| **负责人** | TBD |

### 工作内容
- [ ] 实现依赖就绪检测算法
- [ ] 实现通知触发机制
- [ ] 实现通知配置（可选开启/关闭）
- [ ] 集成到状态变更流程
- [ ] 编写通知测试

### 交付物
- `src/utils/dependency-notifier.ts` - 依赖通知工具 (约 150 行)
- `src/utils/dependency-notifier.test.ts` - 单元测试 (约 100 行)

### 验收标准
- [ ] 子 Feature 状态变更时检查依赖该子 Feature 的其他子 Feature
- [ ] 依赖就绪时触发通知（控制台输出或日志）
- [ ] 通知包含：就绪子 Feature 名称、可开始的任务
- [ ] 通知机制可配置（开启/关闭）
- [ ] 与状态变更流程无缝集成

### 技术说明
```typescript
/**
 * 检查子 Feature 是否所有依赖已完成
 */
function isDependencyReady(
  subFeatureId: string,
  dependencies: Record<string, string[]>,
  subFeatureStates: Map<string, SubFeatureState>
): boolean {
  const deps = dependencies[subFeatureId] || [];
  if (deps.length === 0) return true;
  
  // 检查所有依赖子 Feature 是否至少完成 plan 阶段
  return deps.every(depId => {
    const depState = subFeatureStates.get(depId);
    return depState && depState.phase >= 2; // 至少完成 plan
  });
}

/**
 * 通知依赖就绪的子 Feature
 */
async function notifyDependencyReady(
  completedSubFeatureId: string,
  dependencies: Record<string, string[]>,
  subFeatureStates: Map<string, SubFeatureState>
): Promise<void> {
  // 找到所有依赖此子 Feature 的其他子 Feature
  const dependentSubFeatures = Object.entries(dependencies)
    .filter(([_, deps]) => deps.includes(completedSubFeatureId))
    .map(([subFeatureId, _]) => subFeatureId);
  
  for (const subFeatureId of dependentSubFeatures) {
    if (isDependencyReady(subFeatureId, dependencies, subFeatureStates)) {
      console.log(`📢 子 Feature "${subFeatureId}" 的依赖已就绪，可以开始开发`);
      // 可选：发送消息、写入日志等
    }
  }
}
```

---

## TASK-008: 端到端测试

| 属性 | 值 |
|------|-----|
| **功能需求** | FR-254-5 |
| **优先级** | P0 |
| **预估工时** | 4 小时 |
| **依赖任务** | TASK-006, TASK-007 |
| **并行组** | 组 4 |
| **负责人** | TBD |

### 工作内容
- [ ] 设计端到端测试场景
- [ ] 创建测试 fixture（多子 Feature Feature 示例）
- [ ] 实现完整流程测试（spec→plan→tasks→build）
- [ ] 验证并行执行无冲突
- [ ] 输出测试报告

### 交付物
- `tests/e2e/multi-feature.test.ts` - 端到端测试 (约 300 行)
- `tests/fixtures/multi-feature/` - 测试 fixture 目录

### 验收标准
- [ ] 可成功创建多子 Feature Feature
- [ ] 子 Feature 状态追踪正常
- [ ] 并行任务执行无文件冲突
- [ ] 依赖就绪通知触发
- [ ] 测试报告包含详细结果

### 技术说明
```typescript
describe('Multi-Feature E2E Tests', () => {
  let featurePath: string;
  
  beforeEach(async () => {
    featurePath = await createTestFeature('e2e-test-multi-feature');
  });
  
  test('完整多子 Feature 流程', async () => {
    // 1. 创建多子 Feature spec
    await runAgent('@sdd-spec', { feature: 'e2e-test', mode: 'multi' });
    
    // 2. 验证 sub-features/目录创建
    expect(await fs.exists(`${featurePath}/sub-features`)).toBe(true);
    
    // 3. 执行并行 build
    await runCommand('/sdd build --parallel');
    
    // 4. 验证无冲突
    const state = await loadState('e2e-test');
    expect(state.subFeatures.every(sf => sf.status !== 'error')).toBe(true);
    
    // 5. 验证状态聚合正确
    expect(state.status).toBeDefined();
  });
});
```

---

## TASK-009: 向后兼容测试

| 属性 | 值 |
|------|-----|
| **功能需求** | FR-254-5 |
| **优先级** | P0 |
| **预估工时** | 4 小时 |
| **依赖任务** | TASK-006 ~ TASK-012 |
| **并行组** | 组 5 |
| **负责人** | TBD |

### 工作内容
- [ ] 设计端到端测试场景
- [ ] 创建测试 fixture（多子 Feature Feature 示例）
- [ ] 实现完整流程测试（spec→plan→tasks→build）
- [ ] 验证并行执行无冲突
- [ ] 输出测试报告
- [ ] 测试 @sdd-readme-gen 功能
- [ ] 测试 @sdd-subfeature-init 功能

### 交付物
- `tests/e2e/multi-feature.test.ts` - 端到端测试 (约 300 行)
- `tests/fixtures/multi-feature/` - 测试 fixture 目录

### 验收标准
- [ ] 可成功创建多子 Feature Feature
- [ ] 子 Feature 状态追踪正常
- [ ] 并行任务执行无文件冲突
- [ ] 依赖就绪通知触发
- [ ] 所有辅助工具可正常调用
- [ ] 测试报告包含详细结果

### 技术说明
```typescript
describe('Multi-Feature E2E Tests', () => {
  let featurePath: string;
  
  beforeEach(async () => {
    featurePath = await createTestFeature('e2e-test-multi-feature');
  });
  
  test('完整多子 Feature 流程', async () => {
    // 1. 创建多子 Feature spec
    await runAgent('@sdd-spec', { feature: 'e2e-test', mode: 'multi' });
    
    // 2. 验证 sub-features/目录创建
    expect(await fs.exists(`${featurePath}/sub-features`)).toBe(true);
    
    // 3. 执行并行 build
    await runCommand('/sdd build --parallel');
    
    // 4. 验证无冲突
    const state = await loadState('e2e-test');
    expect(state.subFeatures.every(sf => sf.status !== 'error')).toBe(true);
    
    // 5. 验证 README 生成
    expect(await fs.exists(`${featurePath}/README.md`)).toBe(true);
  });
});
```

---

## TASK-009: 向后兼容测试

| 属性 | 值 |
|------|-----|
| **功能需求** | NFR-101~104 |
| **优先级** | P0 |
| **预估工时** | 3 小时 |
| **依赖任务** | TASK-002, TASK-004, TASK-005 |
| **并行组** | 组 4 |
| **负责人** | TBD |

### 工作内容
- [ ] 创建旧格式 state.json fixture（v1.1.1）
- [ ] 测试单模块 Feature 加载
- [ ] 测试状态迁移流程
- [ ] 测试核心 Agent 兼容性
- [ ] 输出兼容性测试报告

### 交付物
- `tests/compatibility/legacy.test.ts` - 兼容性测试 (约 200 行)
- `tests/fixtures/legacy-v1.1.1/` - 旧版本 fixture 目录

### 验收标准
- [ ] v1.1.1 state.json 可自动升级为 v1.2.5
- [ ] 单模块项目无需迁移可正常工作
- [ ] 核心 Agent（@sdd-spec/plan/tasks/build）正常调用
- [ ] 迁移后备份文件存在且可回滚
- [ ] 兼容性测试报告完整

### 技术说明
```typescript
describe('Backward Compatibility Tests', () => {
  test('v1.1.1 state.json 迁移', async () => {
    const legacyState = {
      feature: 'legacy-feature',
      status: 'planned',
      createdAt: '2026-03-01T00:00:00Z',
      updatedAt: '2026-03-01T00:00:00Z'
      // 无 version, mode, subFeatures 字段
    };
    
    await saveState('legacy-feature', legacyState);
    
    // 加载时应自动迁移
    const newState = await loadState('legacy-feature');
    
    expect(newState.version).toBe('1.2.5');
    expect(newState.mode).toBe('multi');
    expect(newState.subFeatures).toHaveLength(1);
    expect(newState.subFeatures[0].id).toBe('legacy-feature');
  });
  
  test('核心 Agent 兼容性', async () => {
    // 测试所有核心 Agent 在 multi-feature 模式下正常工作
    const agents = ['@sdd-spec', '@sdd-plan', '@sdd-tasks', '@sdd-build'];
    for (const agent of agents) {
      const result = await runAgent(agent, { feature: 'test-multi' });
      expect(result.error).toBeUndefined();
    }
  });
});
```

---

## 实施里程碑

| 里程碑 | 目标日期 | 完成的任务 | 状态 |
|--------|----------|------------|------|
| M1: 子 Feature 结构 | 2026-05-03 | TASK-000, TASK-001, TASK-002, TASK-003 | 📋 |
| M2: 状态管理 | 2026-05-07 | TASK-004, TASK-005 | 📋 |
| M3: 并行机制 | 2026-05-10 | TASK-006, TASK-007 | 📋 |
| M4: 测试发布 | 2026-05-13 | TASK-008, TASK-009 | 📋 |

---

## 工时统计

| 功能模块 | 任务数 | 工时 | 占比 |
|----------|--------|------|------|
| F-251 子 Feature 化 Spec | TASK-003, TASK-005 | 8 小时 | 21% |
| F-252 分布式 State | TASK-001, TASK-002, TASK-004 | 11 小时 | 28% |
| F-253 并行任务 | TASK-006, TASK-007 | 6 小时 | 15% |
| F-254 辅助工具 | TASK-000 | 4 小时 | 10% |
| 测试与验证 | TASK-008, TASK-009 | 7 小时 | 18% |
| 集成与文档 | (隐含在各任务中) | 3 小时 | 8% |
| **总计** | **10** | **39 小时** | **100%** |

**变更说明**: 简化后 10 任务 39 小时（删除 5 个辅助工具任务 + 性能测试，-20 小时）

---

## 风险与缓解

| 风险 | 影响任务 | 缓解措施 |
|------|----------|----------|
| 循环依赖检测遗漏 | TASK-004 | 使用 DFS 算法 + 启动时全量验证 |
| 状态并发写入冲突 | TASK-004 | 实现文件锁 + 乐观锁重试机制 |
| 状态迁移失败 | TASK-002 | 迁移前自动备份 + 回滚机制 |
| 并行执行冲突 | TASK-006, TASK-008 | 文件锁机制 + 冲突检测 |

---

## 下一步

👉 **运行 `@sdd-build TASK-000` 开始实现第一个任务**

建议执行顺序：
1. **Wave 1**: TASK-000, TASK-001, TASK-002, TASK-003（并行开始）
2. **Wave 2**: TASK-004, TASK-005（等待 Wave 1 完成）
3. **Wave 3**: TASK-006, TASK-007（等待 Wave 2 完成）
4. **Wave 4**: TASK-008, TASK-009（等待 Wave 3 完成）

---

**文档状态**: tasked  
**状态更新命令**: 
```bash
/tool sdd_update_state {"feature": "sdd-multi-module", "state": "tasks-simplified"}
```
