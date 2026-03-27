# Phase 2 工作流跳过问题报告

**问题 ID**: SDD-ISSUE-001  
**发现日期**: 2026-03-27  
**状态**: 已修复（补档 + 预防措施）

---

## 🔍 问题描述

Phase 2 扩展阶段在开发过程中跳过了 SDD 工作流的 Plan 和 Tasks 阶段，直接从 Spec 进入 Build 阶段。

### 症状
- `.specs/phase2/` 目录下只有 `spec.md` 文件
- `plan.md` 和 `tasks.md` 文件缺失
- `state.json` 错误地显示 plan 和 tasks 状态为 "completed"
- 代码实现确实已完成（说明 Build 阶段执行了）

---

## 📊 调查过程

### 1. 文件状态检查

| 目录/文件 | 期望 | 实际 | 状态 |
|-----------|------|------|------|
| `.specs/phase2/spec.md` | ✅ 存在 | ✅ 存在 (11,231 字节) | ✓ |
| `.specs/phase2/plan.md` | ✅ 存在 | ❌ 缺失 | ✗ |
| `.specs/phase2/tasks.md` | ✅ 存在 | ❌ 缺失 | ✗ |
| `.specs/.sdd/state.json` | 与实际一致 | 显示虚假完成状态 | ⚠️ |

### 2. 对比其他 Feature

```bash
# readme-implementation-gap（正常）
$ ls -la .specs/readme-implementation-gap/
spec.md      # ✓
plan.md      # ✓
tasks.md     # ✓

# phase2（异常）
$ ls -la .specs/phase2/
spec.md      # ✓
# plan.md    # ✗ 缺失
# tasks.md   # ✗ 缺失
```

### 3. Agent 配置分析

检查 `.opencode/agents/sdd-build.md` 发现：
- 配置中**声明**了前置条件（需要 plan.md 和 tasks.md）
- 但**没有强制执行机制**
- 只是"提示"用户，不会阻止执行

### 4. 插件代码分析

检查 `.opencode/plugins/sdd/index.js` 发现：
- `sdd_update_state` 工具**未定义**
- 只有 `sdd_init`、`sdd_specify`、`sdd_status` 三个工具
- 状态更新可能通过其他途径执行，绕过了文件检查

---

## 🎯 根本原因

### 原因 1：软性检查，无强制执行
Agent 配置中虽然声明了前置条件，但没有代码检查文件是否存在，没有阻止机制。

### 原因 2：状态与文件脱节
`state.json` 可以被独立更新，与实际文档生成脱节。

### 原因 3：缺少工具定义
`sdd_update_state` 工具在 agent 中被引用，但在插件中未定义。

---

## ✅ 修复措施

### 立即修复（已完成）

#### 1. 补档缺失文件
- ✅ 创建 `.specs/phase2/plan.md` (8,954 字节)
- ✅ 创建 `.specs/phase2/tasks.md` (7,733 字节)

#### 2. 更新 state.json
- ✅ 添加 `workflowCompliance` 字段记录问题
- ✅ 为 plan 和 tasks 添加 `retroactivelyCreated` 标记

#### 3. 增强 Agent 配置
- ✅ 修改 `sdd-build.md`：添加强制前置文件检查
- ✅ 修改 `sdd-tasks.md`：添加强制前置文件检查
- ✅ 修改 `sdd-plan.md`：添加强制前置文件检查

### 预防措施（建议）

#### 1. 在 SDD 插件中实现状态更新工具

```javascript
// .opencode/plugins/sdd/index.js 中添加
tool: {
  sdd_update_state: {
    description: "Update SDD workflow state with file existence check",
    args: {
      feature: { type: "string" },
      state: { type: "string" },
      data: { type: "object", optional: true }
    },
    async execute(args, context) {
      const { feature, state, data } = args;
      const featureDir = `.specs/${feature}`;
      
      // 根据状态检查必要的文件
      const requiredFiles = {
        'planned': ['spec.md'],
        'tasked': ['spec.md', 'plan.md'],
        'implementing': ['spec.md', 'plan.md', 'tasks.md'],
        'reviewed': ['spec.md', 'plan.md', 'tasks.md'],
        'validated': ['spec.md', 'plan.md', 'tasks.md']
      };
      
      const files = requiredFiles[state] || [];
      for (const file of files) {
        const filePath = `${featureDir}/${file}`;
        try {
          await context.$ `test -f ${filePath}`;
        } catch {
          throw new Error(`Required file missing: ${filePath}`);
        }
      }
      
      // 更新 state.json
      // ...
    }
  }
}
```

#### 2. 添加 SDD 工作流验证命令

```bash
# /sdd validate-workflow [feature]
# 检查：
# - spec.md 存在 → 可进入 plan
# - plan.md 存在 → 可进入 tasks
# - tasks.md 存在 → 可进入 build
```

#### 3. 在 CI/CD 中添加工作流检查

```yaml
# .github/workflows/sdd-check.yml
- name: Check SDD Workflow Compliance
  run: |
    for feature in .specs/*/; do
      if [ -f "$feature/spec.md" ]; then
        if [ ! -f "$feature/plan.md" ]; then
          echo "❌ $feature missing plan.md"
          exit 1
        fi
        if [ ! -f "$feature/tasks.md" ]; then
          echo "❌ $feature missing tasks.md"
          exit 1
        fi
      fi
    done
```

---

## 📋 修复后的目录结构

```
.specs/phase2/
├── spec.md          # 原有 (11,231 字节)
├── plan.md          # 补档 (8,954 字节)
└── tasks.md         # 补档 (7,733 字节)
```

---

## 🎓 经验教训

1. **配置即代码**：Agent 配置中的规则必须有代码强制执行，不能仅靠文档说明
2. **状态与事实同步**：状态文件应该自动从实际文件状态推导，而非手动更新
3. **防御性编程**：在每个阶段入口处检查前置条件，失败时立即停止
4. **审计追踪**：重要状态变更应该有审计日志

---

## 📝 后续行动

- [ ] 在 SDD 插件中实现 `sdd_update_state` 工具（带文件检查）
- [ ] 添加 `/sdd validate-workflow` 命令
- [ ] 在 CI 中添加 SDD 工作流合规检查
- [ ] 审查其他 feature 是否有类似问题
- [ ] 更新 SDD 使用文档，强调完整工作流的重要性

---

*报告生成日期：2026-03-27*  
*作者：SDD 工作流调查小组*
