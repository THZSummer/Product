# specs-tree-root 管理目录结构命名优化 - 任务分解

## 元数据

| 字段 | 值 |
|------|-----|
| **Feature ID** | FR-DIR-001 |
| **Feature 名称** | specs-tree-root 管理目录结构命名优化 |
| **创建日期** | 2026-04-01 |
| **作者** | SDD 任务分解专家 |
| **优先级** | P0 - 紧急 |
| **状态** | tasked |
| **版本** | 1.0.0 |

---

## 任务汇总

| 指标 | 值 |
|------|-----|
| **总任务数** | 30 个 |
| **复杂度分布** | S 级 24 个，M 级 5 个，L 级 1 个 |
| **执行波次** | 5 个波次 |
| **预计工期** | 4 天 |

---

## 任务依赖关系图

```
Wave 1 (P0-Agent 模板)
├─ TASK-001 ─────────────────┐
├─ TASK-002 ─────────────────┤
├─ TASK-003 ─────────────────┤
├─ TASK-004 ─────────────────┤
├─ TASK-005 ─────────────────┤
├─ TASK-006 ─────────────────┤
├─ TASK-007 ─────────────────┤
├─ TASK-008 ─────────────────┤
├─ TASK-009 ─────────────────┤
├─ TASK-010 ─────────────────┤
├─ TASK-011 ─────────────────┤
└─ TASK-012 (依赖 001-011) ──┘
                                    ↓
Wave 2 (P1-核心运行时)
├─ TASK-013 ─────────────────┐
├─ TASK-014 ─────────────────┤
├─ TASK-015 ─────────────────┤
├─ TASK-016 ─────────────────┤
├─ TASK-017 ─────────────────┤
└─ TASK-018 (依赖 013-017) ──┘
                                    ↓
Wave 3 (P2-测试文件)
├─ TASK-019 ─────────────────┐
├─ TASK-020 ─────────────────┤
├─ TASK-021 ─────────────────┤
├─ TASK-022 ─────────────────┤
└─ TASK-023 (依赖 019-022) ──┘
                                    ↓
Wave 4 (目录迁移)
├─ TASK-024 ─────────────────┐
├─ TASK-025 ─────────────────┤
├─ TASK-026 ─────────────────┤
└─ TASK-027 (依赖 025-026) ──┘
                                    ↓
Wave 5 (验证发布)
├─ TASK-028 ─────────────────┐
├─ TASK-029 ─────────────────┤
└─ TASK-030 (依赖 028-029) ──┘
```

---

## 任务详细列表

### Phase 1: Agent 模板更新（P0-最高优先级）

---

## TASK-001: 更新主入口 Agent 模板 (sdd.md.hbs)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 1

### 描述
更新 SDD 主入口 Agent 模板中的目录路径引用，将 `.specs/` 替换为 `specs-tree-root/`。这是 Agent 认知层的入口文件，决定 Agent 如何理解目录结构。

### 涉及文件
- [MODIFY] `src/templates/agents/sdd.md.hbs`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 25 | 状态检查 | `.sdd/.specs/[feature]/` | `.sdd/specs-tree-root/[feature]/` |
| 53-55 | 跳转保护验证表 | `.sdd/.specs/` | `.sdd/specs-tree-root/` |
| 149 | 命令列表 | （隐含路径） | （隐含路径） |
| 154-156 | 流程说明 | （路径在上下文中） | （路径在上下文中） |

### 验收标准
- [ ] 第 25 行路径已更新
- [ ] 第 53-55 行验证表路径已更新
- [ ] 模板语法正确，无 Handlebars 编译错误
- [ ] `grep "\.specs/" src/templates/agents/sdd.md.hbs` 无匹配

### 验证命令
```bash
grep -n "\.specs/" src/templates/agents/sdd.md.hbs
# 预期：无输出
```

**估计工时**: 1 小时

---

## TASK-002: 更新规范编写 Agent 模板 (sdd-spec.md.hbs)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 1

### 描述
更新规范编写 Agent 模板中的目录路径引用，确保 spec agent 正确创建和读取新路径。

### 涉及文件
- [MODIFY] `src/templates/agents/sdd-spec.md.hbs`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 21 | 依赖关系 - 输出 | `.sdd/.specs/[feature]/spec.md` | `.sdd/specs-tree-root/[feature]/spec.md` |
| 31 | 前置验证 | `.sdd/.specs/[feature]/` | `.sdd/specs-tree-root/[feature]/` |
| 98 | 输出格式 | `.sdd/.specs/[feature]/spec.md` | `.sdd/specs-tree-root/[feature]/spec.md` |

### 验收标准
- [ ] 第 21 行输出路径已更新
- [ ] 第 31 行前置验证路径已更新
- [ ] 第 98 行输出格式路径已更新
- [ ] `grep "\.specs/" src/templates/agents/sdd-spec.md.hbs` 无匹配

### 验证命令
```bash
grep -n "\.specs/" src/templates/agents/sdd-spec.md.hbs
# 预期：无输出
```

**估计工时**: 0.5 小时

---

## TASK-003: 更新技术规划 Agent 模板 (sdd-plan.md.hbs)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 1

### 描述
更新技术规划 Agent 模板中的目录路径引用，确保 plan agent 正确读取 spec 并创建 plan。

### 涉及文件
- [MODIFY] `src/templates/agents/sdd-plan.md.hbs`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 20-23 | 依赖关系 | `.sdd/.specs/[feature]/spec.md` | `.sdd/specs-tree-root/[feature]/spec.md` |
| 32 | 前置验证 | `.sdd/.specs/[feature]/spec.md` | `.sdd/specs-tree-root/[feature]/spec.md` |
| 36 | 重要规则 | （规则不变） | （规则不变） |
| 82, 108 | 输出格式 | `.sdd/.specs/[feature]/plan.md` | `.sdd/specs-tree-root/[feature]/plan.md` |

### 验收标准
- [ ] 第 20-23 行依赖关系路径已更新
- [ ] 第 32 行前置验证路径已更新
- [ ] 第 82, 108 行输出格式路径已更新
- [ ] `grep "\.specs/" src/templates/agents/sdd-plan.md.hbs` 无匹配

### 验证命令
```bash
grep -n "\.specs/" src/templates/agents/sdd-plan.md.hbs
# 预期：无输出
```

**估计工时**: 0.5 小时

---

## TASK-004: 更新任务分解 Agent 模板 (sdd-tasks.md.hbs)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 1

### 描述
更新任务分解 Agent 模板中的目录路径引用，确保 tasks agent 正确读取 plan 并创建 tasks。

### 涉及文件
- [MODIFY] `src/templates/agents/sdd-tasks.md.hbs`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 20-23 | 依赖关系 | `.sdd/.specs/[feature]/` | `.sdd/specs-tree-root/[feature]/` |
| 32-33, 37 | 前置验证 | `.sdd/.specs/[feature]/plan.md` | `.sdd/specs-tree-root/[feature]/plan.md` |
| 97, 127-128 | 输出格式 | `.sdd/.specs/[feature]/tasks.md` | `.sdd/specs-tree-root/[feature]/tasks.md` |

### 验收标准
- [ ] 第 20-23 行依赖关系路径已更新
- [ ] 第 32-33, 37 行前置验证路径已更新
- [ ] 第 97, 127-128 行输出格式路径已更新
- [ ] `grep "\.specs/" src/templates/agents/sdd-tasks.md.hbs` 无匹配

### 验证命令
```bash
grep -n "\.specs/" src/templates/agents/sdd-tasks.md.hbs
# 预期：无输出
```

**估计工时**: 0.5 小时

---

## TASK-005: 更新任务实现 Agent 模板 (sdd-build.md.hbs)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 1

### 描述
更新任务实现 Agent 模板中的目录路径引用，确保 build agent 正确读取 tasks 并实现代码。

### 涉及文件
- [MODIFY] `src/templates/agents/sdd-build.md.hbs`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 20-23 | 依赖关系 | `.sdd/.specs/[feature]/tasks.md` | `.sdd/specs-tree-root/[feature]/tasks.md` |
| 34 | 前置验证 | `.sdd/.specs/[feature]/tasks.md` | `.sdd/specs-tree-root/[feature]/tasks.md` |
| 44, 66 | 输出格式 | （路径在上下文中） | （路径在上下文中） |

### 验收标准
- [ ] 第 20-23 行依赖关系路径已更新
- [ ] 第 34 行前置验证路径已更新
- [ ] `grep "\.specs/" src/templates/agents/sdd-build.md.hbs` 无匹配

### 验证命令
```bash
grep -n "\.specs/" src/templates/agents/sdd-build.md.hbs
# 预期：无输出
```

**估计工时**: 0.5 小时

---

## TASK-006: 更新代码审查 Agent 模板 (sdd-review.md.hbs)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 1

### 描述
更新代码审查 Agent 模板中的目录路径引用，确保 review agent 正确审查代码。

### 涉及文件
- [MODIFY] `src/templates/agents/sdd-review.md.hbs`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 20-22 | 依赖关系 | `.sdd/.specs/[feature]/` | `.sdd/specs-tree-root/[feature]/` |
| 41, 53 | 前置验证 | （路径在上下文中） | （路径在上下文中） |
| 64-66 | 输出格式 | `.sdd/.specs/[feature]/review.md` | `.sdd/specs-tree-root/[feature]/review.md` |

### 验收标准
- [ ] 第 20-22 行依赖关系路径已更新
- [ ] 第 64-66 行输出格式路径已更新
- [ ] `grep "\.specs/" src/templates/agents/sdd-review.md.hbs` 无匹配

### 验证命令
```bash
grep -n "\.specs/" src/templates/agents/sdd-review.md.hbs
# 预期：无输出
```

**估计工时**: 0.5 小时

---

## TASK-007: 更新最终验证 Agent 模板 (sdd-validate.md.hbs)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 1

### 描述
更新最终验证 Agent 模板中的目录路径引用，确保 validate agent 正确验证功能。

### 涉及文件
- [MODIFY] `src/templates/agents/sdd-validate.md.hbs`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 20, 23 | 依赖关系 | `.sdd/.specs/[feature]/review.md` | `.sdd/specs-tree-root/[feature]/review.md` |
| 34, 44 | 前置验证 | （路径在上下文中） | （路径在上下文中） |
| 82, 186 | 输出格式 | （路径在上下文中） | （路径在上下文中） |

### 验收标准
- [ ] 第 20, 23 行依赖关系路径已更新
- [ ] 第 82, 186 行输出格式路径已更新
- [ ] `grep "\.specs/" src/templates/agents/sdd-validate.md.hbs` 无匹配

### 验证命令
```bash
grep -n "\.specs/" src/templates/agents/sdd-validate.md.hbs
# 预期：无输出
```

**估计工时**: 0.5 小时

---

## TASK-008: 更新目录导航 Agent 模板 (sdd-docs.md.hbs)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 1

### 描述
更新目录导航 Agent 模板中的目录路径引用，确保 docs agent 正确生成目录导航。

### 涉及文件
- [MODIFY] `src/templates/agents/sdd-docs.md.hbs`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 31 | 扫描路径 | `.sdd/.specs/` | `.sdd/specs-tree-root/` |
| 48-50 | 生成逻辑 | `.sdd/.specs/[feature]/README.md` | `.sdd/specs-tree-root/[feature]/README.md` |
| 136-137, 140 | 输出路径 | （路径在上下文中） | （路径在上下文中） |
| 145, 159-161 | 验证逻辑 | `.sdd/.specs/` | `.sdd/specs-tree-root/` |

### 验收标准
- [ ] 第 31 行扫描路径已更新
- [ ] 第 48-50 行生成逻辑路径已更新
- [ ] 第 145, 159-161 行验证逻辑路径已更新
- [ ] `grep "\.specs/" src/templates/agents/sdd-docs.md.hbs` 无匹配

### 验证命令
```bash
grep -n "\.specs/" src/templates/agents/sdd-docs.md.hbs
# 预期：无输出
```

**估计工时**: 0.5 小时

---

## TASK-009: 更新 Roadmap 规划 Agent 模板 (sdd-roadmap.md.hbs)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 1

### 描述
更新 Roadmap 规划 Agent 模板中的目录路径引用，确保 roadmap agent 正确读取和更新 Roadmap。

### 涉及文件
- [MODIFY] `src/templates/agents/sdd-roadmap.md.hbs`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 35-36 | 读取路径 | `.sdd/.specs/ROADMAP.md` | `.sdd/specs-tree-root/ROADMAP.md` |
| 62 | 保存路径 | `.sdd/.specs/ROADMAP.md` | `.sdd/specs-tree-root/ROADMAP.md` |
| 75-78 | 依赖关系 | `.sdd/.specs/` | `.sdd/specs-tree-root/` |
| 100, 102 | 输出格式 | （路径在上下文中） | （路径在上下文中） |

### 验收标准
- [ ] 第 35-36 行读取路径已更新
- [ ] 第 62 行保存路径已更新
- [ ] 第 75-78 行依赖关系路径已更新
- [ ] `grep "\.specs/" src/templates/agents/sdd-roadmap.md.hbs` 无匹配

### 验证命令
```bash
grep -n "\.specs/" src/templates/agents/sdd-roadmap.md.hbs
# 预期：无输出
```

**估计工时**: 0.5 小时

---

## TASK-010: 更新帮助 Agent 模板 (sdd-help.md.hbs)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 1

### 描述
更新帮助 Agent 模板中的目录路径引用，确保 help agent 正确显示帮助信息。

### 涉及文件
- [MODIFY] `src/templates/agents/sdd-help.md.hbs`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 96-98 | 目录结构说明 | `.sdd/.specs/[feature]/` | `.sdd/specs-tree-root/[feature]/` |
| 112 | 文件列表 | `.sdd/.specs/[feature]/` | `.sdd/specs-tree-root/[feature]/` |

### 验收标准
- [ ] 第 96-98 行目录结构说明已更新
- [ ] 第 112 行文件列表路径已更新
- [ ] `grep "\.specs/" src/templates/agents/sdd-help.md.hbs` 无匹配

### 验证命令
```bash
grep -n "\.specs/" src/templates/agents/sdd-help.md.hbs
# 预期：无输出
```

**估计工时**: 0.5 小时

---

## TASK-011: 更新子功能模板 (subfeature-templates.ts)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 1

### 描述
更新子功能模板中的目录路径引用，确保子功能模板生成正确路径。

### 涉及文件
- [MODIFY] `src/templates/subfeature-templates.ts`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 252 | 目录结构注释 | `.specs/` | `specs-tree-root/` |

### 验收标准
- [ ] 第 252 行目录结构注释已更新
- [ ] `grep "\.specs/" src/templates/subfeature-templates.ts` 无匹配

### 验证命令
```bash
grep -n "\.specs/" src/templates/subfeature-templates.ts
# 预期：无输出
```

**估计工时**: 0.5 小时

---

## TASK-012: 验证所有 Agent 模板更新

**复杂度**: M  
**前置依赖**: TASK-001, TASK-002, TASK-003, TASK-004, TASK-005, TASK-006, TASK-007, TASK-008, TASK-009, TASK-010, TASK-011  
**执行波次**: 1

### 描述
验证所有 11 个 Agent 模板文件的路径更新是否完整，确保无遗漏。

### 涉及文件
- [VERIFY] `src/templates/agents/*.hbs` (11 个文件)
- [VERIFY] `src/templates/subfeature-templates.ts`

### 验收标准
- [ ] `grep -r "\.specs/" src/templates/agents/` 无结果
- [ ] `grep -r "\.specs/" src/templates/subfeature-templates.ts` 无结果
- [ ] 所有模板文件语法正确，无 Handlebars 编译错误
- [ ] 创建验证报告记录检查结果

### 验证命令
```bash
# 验证 Agent 模板
grep -r "\.specs/" src/templates/agents/
# 预期：无输出

# 验证子功能模板
grep -n "\.specs/" src/templates/subfeature-templates.ts
# 预期：无输出
```

**估计工时**: 1 小时

---

### Phase 2: 核心运行时更新（P1-高优先级）

---

## TASK-013: 更新状态机路径常量 (machine.ts)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 2

### 描述
更新状态机构造函数中的默认路径参数，将 `.specs` 替换为 `specs-tree-root`。

### 涉及文件
- [MODIFY] `src/state/machine.ts`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 52 | 构造函数默认参数 | `specsDir: string = '.specs'` | `specsDir: string = 'specs-tree-root'` |

### 验收标准
- [ ] 第 52 行构造函数参数已更新
- [ ] TypeScript 编译通过
- [ ] `grep "\.specs" src/state/machine.ts` 无匹配

### 验证命令
```bash
npm run build
# 预期：编译成功，无错误
```

**估计工时**: 0.5 小时

---

## TASK-014: 更新 Schema 路径生成 (schema-v1.2.5.ts)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 2

### 描述
更新 Schema 定义中的路径常量，将 `.specs/${feature}/` 替换为 `specs-tree-root/${feature}/`。

### 涉及文件
- [MODIFY] `src/state/schema-v1.2.5.ts`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 188-191 | 路径常量 | `.specs/${feature}/` | `specs-tree-root/${feature}/` |

### 验收标准
- [ ] 第 188-191 行路径常量已更新
- [ ] TypeScript 编译通过
- [ ] `grep "\.specs" src/state/schema-v1.2.5.ts` 无匹配

### 验证命令
```bash
npm run build
# 预期：编译成功，无错误
```

**估计工时**: 0.5 小时

---

## TASK-015: 更新迁移器路径 (migrator.ts)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 2

### 描述
更新迁移器中的路径拼接逻辑，将 `.specs/` 替换为 `specs-tree-root/`。

### 涉及文件
- [MODIFY] `src/state/migrator.ts`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 52 | 路径拼接 | `.specs/` | `specs-tree-root/` |

### 验收标准
- [ ] 第 52 行路径拼接已更新
- [ ] TypeScript 编译通过
- [ ] `grep "\.specs" src/state/migrator.ts` 无匹配

### 验证命令
```bash
npm run build
# 预期：编译成功，无错误
```

**估计工时**: 0.5 小时

---

## TASK-016: 更新子功能管理器路径 (subfeature-manager.ts)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 2

### 描述
更新子功能管理器中的路径常量，将 `.specs` 替换为 `specs-tree-root`。

### 涉及文件
- [MODIFY] `src/utils/subfeature-manager.ts`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 246 | 路径常量 | `.specs` | `specs-tree-root` |

### 验收标准
- [ ] 第 246 行路径常量已更新
- [ ] TypeScript 编译通过
- [ ] `grep "\.specs" src/utils/subfeature-manager.ts` 无匹配

### 验证命令
```bash
npm run build
# 预期：编译成功，无错误
```

**估计工时**: 0.5 小时

---

## TASK-017: 更新插件入口路径检查 (index.ts)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 2

### 描述
更新插件入口文件中的路径检查逻辑，将 `.specs/` 替换为 `specs-tree-root/`。

### 涉及文件
- [MODIFY] `src/index.ts`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 27 | 路径检查 | `.specs/` | `specs-tree-root/` |

### 验收标准
- [ ] 第 27 行路径检查已更新
- [ ] TypeScript 编译通过
- [ ] `grep "\.specs" src/index.ts` 无匹配

### 验证命令
```bash
npm run build
# 预期：编译成功，无错误
```

**估计工时**: 0.5 小时

---

## TASK-018: 验证核心运行时更新

**复杂度**: M  
**前置依赖**: TASK-013, TASK-014, TASK-015, TASK-016, TASK-017  
**执行波次**: 2

### 描述
验证所有 5 个核心运行时文件的路径更新是否完整，确保编译通过。

### 涉及文件
- [VERIFY] `src/state/machine.ts`
- [VERIFY] `src/state/schema-v1.2.5.ts`
- [VERIFY] `src/state/migrator.ts`
- [VERIFY] `src/utils/subfeature-manager.ts`
- [VERIFY] `src/index.ts`

### 验收标准
- [ ] `grep -r "\.specs/" src/state/` 无结果
- [ ] `grep -r "\.specs/" src/utils/` 无结果
- [ ] `grep -r "\.specs/" src/index.ts` 无结果
- [ ] TypeScript 编译无错误
- [ ] `npm run build` 成功
- [ ] 创建验证报告记录检查结果

### 验证命令
```bash
# 验证核心代码
grep -r "\.specs/" src/state/ src/utils/ src/index.ts
# 预期：无输出

# 编译验证
npm run build
# 预期：编译成功
```

**估计工时**: 1 小时

---

### Phase 3: 测试文件更新（P2-中优先级）

---

## TASK-019: 更新 Schema 测试 (schema-v1.2.5.test.ts)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 3

### 描述
更新 Schema 测试文件中的路径常量，确保测试验证新路径逻辑。

### 涉及文件
- [MODIFY] `src/state/schema-v1.2.5.test.ts`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 64-67 | 测试路径常量 | `.specs` | `specs-tree-root` |
| 79, 86 | 测试验证 | `.specs` | `specs-tree-root` |
| 254-257 | 测试状态创建 | `.specs` | `specs-tree-root` |

### 验收标准
- [ ] 第 64-67 行测试路径常量已更新
- [ ] 第 79, 86 行测试验证已更新
- [ ] 第 254-257 行测试状态创建已更新
- [ ] 测试用例运行通过

### 验证命令
```bash
npm test -- schema-v1.2.5.test.ts
# 预期：测试通过
```

**估计工时**: 0.5 小时

---

## TASK-020: 更新迁移器测试 (migrator.test.ts)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 3

### 描述
更新迁移器测试文件中的路径常量，确保测试验证新路径逻辑。

### 涉及文件
- [MODIFY] `src/state/migrator.test.ts`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 57, 73 | 测试路径常量 | `.specs` | `specs-tree-root` |

### 验收标准
- [ ] 第 57, 73 行测试路径常量已更新
- [ ] 测试用例运行通过

### 验证命令
```bash
npm test -- migrator.test.ts
# 预期：测试通过
```

**估计工时**: 0.5 小时

---

## TASK-021: 更新子功能管理器测试 (subfeature-manager.test.ts)

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 3

### 描述
更新子功能管理器测试文件中的路径拼接，确保测试验证新路径逻辑。

### 涉及文件
- [MODIFY] `src/utils/subfeature-manager.test.ts`

### 修改点
| 行号 | 修改类型 | 旧值 | 新值 |
|------|----------|------|------|
| 237, 254 | 测试路径拼接 | `.specs` | `specs-tree-root` |

### 验收标准
- [ ] 第 237, 254 行测试路径拼接已更新
- [ ] 测试用例运行通过

### 验证命令
```bash
npm test -- subfeature-manager.test.ts
# 预期：测试通过
```

**估计工时**: 0.5 小时

---

## TASK-022: 更新其他测试文件

**复杂度**: M  
**前置依赖**: 无  
**执行波次**: 3

### 描述
检查并更新剩余的 5 个测试文件，确保所有测试路径与新规范一致。

### 涉及文件
- [MODIFY] `src/state/multi-feature-manager.test.ts` (待检查)
- [MODIFY] `src/utils/dependency-notifier.test.ts` (待检查)
- [MODIFY] `src/utils/tasks-parser.test.ts` (待检查)
- [MODIFY] `src/utils/readme-generator.test.ts` (待检查)
- [MODIFY] `src/templates/subfeature-templates.test.ts` (待检查)

### 验收标准
- [ ] 检查所有 5 个测试文件是否包含 `.specs` 引用
- [ ] 如有引用，更新为 `specs-tree-root`
- [ ] 所有测试用例运行通过

### 验证命令
```bash
# 检查其他测试文件
grep -n "\.specs/" src/state/multi-feature-manager.test.ts
grep -n "\.specs/" src/utils/dependency-notifier.test.ts
grep -n "\.specs/" src/utils/tasks-parser.test.ts
grep -n "\.specs/" src/utils/readme-generator.test.ts
grep -n "\.specs/" src/templates/subfeature-templates.test.ts
# 预期：无输出 或 已更新

# 运行完整测试
npm test
# 预期：所有测试通过
```

**估计工时**: 2 小时

---

## TASK-023: 运行完整测试套件

**复杂度**: M  
**前置依赖**: TASK-019, TASK-020, TASK-021, TASK-022  
**执行波次**: 3

### 描述
运行完整的测试套件，验证所有测试文件更新后功能正确。

### 涉及文件
- [VERIFY] 所有测试文件

### 验收标准
- [ ] `npm test` 全部通过
- [ ] 测试覆盖率不降低
- [ ] 无路径相关测试失败
- [ ] 创建测试报告记录结果

### 验证命令
```bash
npm test
# 预期：所有测试通过
```

**估计工时**: 1 小时

---

### Phase 4: 目录迁移（P0-最高优先级）

---

## TASK-024: 准备迁移

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 4

### 描述
执行目录迁移前的准备工作，包括创建 Git 分支、备份目录、通知团队成员。

### 涉及文件
- [CREATE] Git 分支：`feature/specs-tree-directory-optimization`
- [BACKUP] `.sdd` 目录备份

### 子任务
- [ ] 创建 Git 分支
- [ ] 备份 `.sdd` 目录：`cp -r .sdd .sdd.backup.YYYYMMDD_HHMMMSS`
- [ ] 通知团队成员迁移计划
- [ ] 确认项目根目录 `src/` 存在且完整
- [ ] 确认项目根目录有测试文件
- [ ] 备份 `.sdd/docs/INSTALL.md` 内容（如果有价值）

### 验收标准
- [ ] Git 分支创建成功
- [ ] 备份目录创建成功
- [ ] 团队成员已通知
- [ ] 前置检查完成

### 验证命令
```bash
# 检查分支
git branch | grep specs-tree-directory-optimization
# 预期：显示当前分支

# 检查备份
ls -la .sdd.backup.*
# 预期：显示备份目录

# 检查项目根目录 src/
ls -la src/
# 预期：显示源代码目录
```

**估计工时**: 0.5 小时

---

## TASK-025: 执行目录迁移脚本

**复杂度**: L  
**前置依赖**: TASK-024  
**执行波次**: 4

### 描述
执行目录迁移脚本，将 `.sdd/.specs/` 重命名为 `.sdd/specs-tree-root/` 并更新子目录。

### 涉及文件
- [EXECUTE] `specs-directory-migration.sh`
- [RENAME] `.sdd/.specs/` → `.sdd/specs-tree-root/`
- [RENAME] 11 个子目录添加 `specs-tree-` 前缀
- [DELETE] `.templates/`, `examples/`
- [MOVE] `ROADMAP.md` 迁移
- [DELETE] `.sdd/docs/`, `.sdd/src/`, `.sdd/tests/`

### 迁移步骤
1. **阶段 1**: 根目录重命名 `.specs/` → `specs-tree-root/`
2. **阶段 2**: 子目录重命名（添加 `specs-tree-` 前缀）
3. **阶段 3**: 删除 `.templates/`, `examples/`
4. **阶段 4**: 迁移 `ROADMAP.md`
5. **阶段 5**: 删除 `.sdd/docs/`, `.sdd/src/`, `.sdd/tests/`

### 验收标准
- [ ] 迁移脚本执行成功
- [ ] 根目录重命名完成
- [ ] 11 个子目录重命名完成
- [ ] 指定目录已删除
- [ ] ROADMAP.md 已迁移
- [ ] 临时目录已删除

### 验证命令
```bash
# 执行迁移脚本
bash specs-directory-migration.sh
# 预期：脚本成功执行

# 验证目录结构
ls -la .sdd/
ls -la .sdd/specs-tree-root/
```

**估计工时**: 0.5 小时

---

## TASK-026: 验证迁移结果

**复杂度**: S  
**前置依赖**: TASK-025  
**执行波次**: 4

### 描述
验证目录迁移结果，确保所有目录和文件正确迁移。

### 涉及文件
- [VERIFY] `.sdd/specs-tree-root/`
- [VERIFY] 各 `specs-tree-*` 子目录

### 子任务
- [ ] 检查 `specs-tree-root/` 存在
- [ ] 检查 `specs-tree-*` 目录数量（预期 11 个）
- [ ] 检查临时目录已删除（`.templates/`, `examples/`, `docs/`, `src/`, `tests/`）
- [ ] 检查已规范目录保持不变（`specs-tree-agentic/`, `specs-tree-directory-naming/`, `specs-tree-state-json-fix/`）

### 验收标准
- [ ] `specs-tree-root/` 目录存在
- [ ] 发现 11 个 `specs-tree-*` 目录
- [ ] `.templates/` 已删除
- [ ] `examples/` 已删除
- [ ] `.sdd/docs/` 已删除
- [ ] `.sdd/src/` 已删除
- [ ] `.sdd/tests/` 已删除
- [ ] 已规范目录保持不变

### 验证命令
```bash
# 验证目录结构
ls -la .sdd/

# 验证 specs-tree-root 存在
test -d .sdd/specs-tree-root && echo "✅ specs-tree-root 存在"

# 验证 specs-tree-前缀目录数量
count=$(ls -1 .sdd/specs-tree-root/ | grep "^specs-tree-" | wc -l)
echo "✅ 发现 $count 个 specs-tree-* 目录"
# 预期：11 个

# 验证已删除目录
test ! -d .sdd/.templates && echo "✅ .templates/ 已删除"
test ! -d .sdd/examples && echo "✅ examples/ 已删除"
test ! -d .sdd/docs && echo "✅ docs/ 已删除"
test ! -d .sdd/src && echo "✅ src/ 已删除"
test ! -d .sdd/tests && echo "✅ tests/ 已删除"

# 验证已规范目录保持不变
test -d .sdd/specs-tree-agentic && echo "✅ specs-tree-agentic/ 保持不变"
test -d .sdd/specs-tree-directory-naming && echo "✅ specs-tree-directory-naming/ 保持不变"
test -d .sdd/specs-tree-state-json-fix && echo "✅ specs-tree-state-json-fix/ 保持不变"
```

**估计工时**: 0.5 小时

---

## TASK-027: 更新目录导航

**复杂度**: M  
**前置依赖**: TASK-025, TASK-026  
**执行波次**: 4

### 描述
运行 @sdd-docs 更新目录导航，生成新的 README.md 文件。

### 涉及文件
- [GENERATE] `.sdd/README.md`
- [GENERATE] `.sdd/specs-tree-root/README.md`
- [GENERATE] 各子目录 `README.md`

### 验收标准
- [ ] 运行 `@sdd-docs` 成功
- [ ] `.sdd/README.md` 已更新
- [ ] `.sdd/specs-tree-root/README.md` 已生成
- [ ] 各子目录 `README.md` 已生成
- [ ] 导航内容正确反映新目录结构

### 验证命令
```bash
# 运行 sdd-docs
# @sdd-docs

# 验证 README 存在
test -f .sdd/README.md && echo "✅ .sdd/README.md 存在"
test -f .sdd/specs-tree-root/README.md && echo "✅ specs-tree-root/README.md 存在"
```

**估计工时**: 0.5 小时

---

### Phase 5: 验证与发布

---

## TASK-028: 功能验证测试

**复杂度**: M  
**前置依赖**: TASK-027  
**执行波次**: 5

### 描述
执行完整的功能验证测试，确保所有 Agent 工具功能正常。

### 涉及文件
- [TEST] 所有 Agent 工具

### 测试项
- [ ] @sdd-spec 可正常创建新 specs（自动添加 `specs-tree-` 前缀）
- [ ] @sdd-plan 可正常读取 specs
- [ ] @sdd-tasks 可正常生成任务
- [ ] @sdd-build 可正常执行构建
- [ ] @sdd-review 可正常执行审查
- [ ] @sdd-validate 可正常执行验证
- [ ] @sdd-docs 可正常生成导航
- [ ] @sdd-roadmap 可正常读取 roadmap
- [ ] @sdd-help 可正常显示帮助信息
- [ ] 无任何 Agent 报告路径错误

### 验收标准
- [ ] 所有 Agent 工具功能正常
- [ ] 新创建的 specs 目录自动带 `specs-tree-` 前缀
- [ ] Agent 能正确读取现有 specs
- [ ] 工作流各阶段正常流转
- [ ] 创建功能验证报告

### 验证命令
```bash
# 测试创建新 specs
# @sdd-spec test-feature

# 验证目录命名
ls -la .sdd/specs-tree-root/ | grep specs-tree-test-feature
# 预期：显示新创建的目录
```

**估计工时**: 1 小时

---

## TASK-029: 代码审查

**复杂度**: M  
**前置依赖**: TASK-028  
**执行波次**: 5

### 描述
对所有修改的 24 个文件进行代码审查，确保代码质量。

### 涉及文件
- [REVIEW] 所有修改的 24 个文件（11 个模板 + 5 个核心 + 8 个测试）

### 验收标准
- [ ] 创建 Pull Request
- [ ] 代码审查通过
- [ ] 所有评论已解决
- [ ] PR 合并到主分支

### 验证命令
```bash
# 查看 Git 状态
git status
# 预期：显示所有修改的文件

# 创建 PR
gh pr create --title "feat: specs-tree-root 目录结构优化" --body "更新 Agent 模板和核心代码路径"
```

**估计工时**: 1 小时

---

## TASK-030: 发布新版本

**复杂度**: S  
**前置依赖**: TASK-028, TASK-029  
**执行波次**: 5

### 描述
发布新版本，更新版本号和文档。

### 涉及文件
- [UPDATE] `package.json` (版本号)
- [UPDATE] 文档

### 步骤
- [ ] 更新版本号
- [ ] 发布到 npm
- [ ] 更新文档
- [ ] 创建 Release

### 验收标准
- [ ] 新版本发布成功
- [ ] npm 包已更新
- [ ] 文档已同步
- [ ] Release Notes 已创建

### 验证命令
```bash
# 查看版本号
npm version patch
# 预期：版本号更新

# 发布到 npm
npm publish
# 预期：发布成功
```

**估计工时**: 0.5 小时

---

## 任务状态跟踪表

| 任务 ID | 任务名称 | 复杂度 | 波次 | 状态 | 负责人 | 完成日期 |
|---------|----------|--------|------|------|--------|----------|
| TASK-001 | 更新主入口 Agent 模板 | S | 1 | ⏳ 待开始 | - | - |
| TASK-002 | 更新规范编写 Agent 模板 | S | 1 | ⏳ 待开始 | - | - |
| TASK-003 | 更新技术规划 Agent 模板 | S | 1 | ⏳ 待开始 | - | - |
| TASK-004 | 更新任务分解 Agent 模板 | S | 1 | ⏳ 待开始 | - | - |
| TASK-005 | 更新任务实现 Agent 模板 | S | 1 | ⏳ 待开始 | - | - |
| TASK-006 | 更新代码审查 Agent 模板 | S | 1 | ⏳ 待开始 | - | - |
| TASK-007 | 更新最终验证 Agent 模板 | S | 1 | ⏳ 待开始 | - | - |
| TASK-008 | 更新目录导航 Agent 模板 | S | 1 | ⏳ 待开始 | - | - |
| TASK-009 | 更新 Roadmap 规划 Agent 模板 | S | 1 | ⏳ 待开始 | - | - |
| TASK-010 | 更新帮助 Agent 模板 | S | 1 | ⏳ 待开始 | - | - |
| TASK-011 | 更新子功能模板 | S | 1 | ⏳ 待开始 | - | - |
| TASK-012 | 验证所有 Agent 模板更新 | M | 1 | ⏳ 待开始 | - | - |
| TASK-013 | 更新状态机路径常量 | S | 2 | ⏳ 待开始 | - | - |
| TASK-014 | 更新 Schema 路径生成 | S | 2 | ⏳ 待开始 | - | - |
| TASK-015 | 更新迁移器路径 | S | 2 | ⏳ 待开始 | - | - |
| TASK-016 | 更新子功能管理器路径 | S | 2 | ⏳ 待开始 | - | - |
| TASK-017 | 更新插件入口路径检查 | S | 2 | ⏳ 待开始 | - | - |
| TASK-018 | 验证核心运行时更新 | M | 2 | ⏳ 待开始 | - | - |
| TASK-019 | 更新 Schema 测试 | S | 3 | ⏳ 待开始 | - | - |
| TASK-020 | 更新迁移器测试 | S | 3 | ⏳ 待开始 | - | - |
| TASK-021 | 更新子功能管理器测试 | S | 3 | ⏳ 待开始 | - | - |
| TASK-022 | 更新其他测试文件 | M | 3 | ⏳ 待开始 | - | - |
| TASK-023 | 运行完整测试套件 | M | 3 | ⏳ 待开始 | - | - |
| TASK-024 | 准备迁移 | S | 4 | ⏳ 待开始 | - | - |
| TASK-025 | 执行目录迁移脚本 | L | 4 | ⏳ 待开始 | - | - |
| TASK-026 | 验证迁移结果 | S | 4 | ⏳ 待开始 | - | - |
| TASK-027 | 更新目录导航 | M | 4 | ⏳ 待开始 | - | - |
| TASK-028 | 功能验证测试 | M | 5 | ⏳ 待开始 | - | - |
| TASK-029 | 代码审查 | M | 5 | ⏳ 待开始 | - | - |
| TASK-030 | 发布新版本 | S | 5 | ⏳ 待开始 | - | - |

**状态图例**:
- ⏳ 待开始
- 🔄 进行中
- ✅ 已完成
- ❌ 已阻塞

---

## 执行顺序建议

### Wave 1: Agent 模板更新（并行执行）
```
TASK-001 → TASK-002 → TASK-003 → TASK-004 → TASK-005 → 
TASK-006 → TASK-007 → TASK-008 → TASK-009 → TASK-010 → 
TASK-011 → TASK-012 (验证)
```

### Wave 2: 核心运行时更新（并行执行）
```
TASK-013 → TASK-014 → TASK-015 → TASK-016 → TASK-017 → 
TASK-018 (验证)
```

### Wave 3: 测试文件更新（并行执行）
```
TASK-019 → TASK-020 → TASK-021 → TASK-022 → 
TASK-023 (验证)
```

### Wave 4: 目录迁移（顺序执行）
```
TASK-024 (准备) → TASK-025 (迁移) → TASK-026 (验证) → 
TASK-027 (导航)
```

### Wave 5: 验证与发布（顺序执行）
```
TASK-028 (功能测试) → TASK-029 (审查) → TASK-030 (发布)
```

---

## 风险与缓解

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| 模板更新不完整 | 中 | 高 | TASK-012 专项验证，使用 grep 全面搜索 |
| 代码编译失败 | 中 | 中 | TASK-018 专项验证，分步编译 |
| 测试失败 | 高 | 中 | TASK-023 运行完整测试，预留修复时间 |
| 目录迁移丢失数据 | 低 | 高 | TASK-024 完整备份，提供回滚脚本 |
| Agent 行为异常 | 中 | 高 | TASK-028 充分测试，灰度发布 |

---

## ✅ 任务分解完成

**Feature**: specs-tree-directory-optimization  
**状态**: tasked  
**文件**: `.sdd/.specs/specs-tree-directory-optimization/tasks.md`

### 任务汇总
- **总任务数**: 30 个
- **复杂度分布**: S 级 24 个，M 级 5 个，L 级 1 个
- **执行波次**: 5 个波次

### 下一步
👉 运行 `@sdd-build TASK-001` 开始实现第一个任务

```bash
/tool sdd_update_state {"feature": "specs-tree-directory-optimization", "state": "tasked"}
```
