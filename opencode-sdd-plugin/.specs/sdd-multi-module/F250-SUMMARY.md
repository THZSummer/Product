# F-250 规划摘要：SDD 容器化目录结构

> **创建日期**: 2026-03-31  
> **Feature ID**: F-250  
> **版本**: v1.2.11  
> **状态**: 规划完成，待实施  

---

## 📋 执行摘要

### 核心决策

用户已确认将**创建 `.sdd/` 容器目录结构**作为新工作项添加到 `sdd-multi-module` Feature 中，作为 **F-250（基础设施）**。

| 决策项 | 选择 |
|--------|------|
| **目录结构方案** | ✅ 方案 A: 创建新的 `.sdd/` 容器 |
| **ROADMAP 位置** | ✅ `.sdd/ROADMAP.md`（容器级） |
| **Spec 定义位置** | ✅ `.sdd/.specs/` 存放规范 |
| **实施策略** | ✅ 不存在迁移，是新设计 |
| **Feature 组织** | ✅ 作为 F-250（基础设施） |

---

## 🎯 Feature 信息

| 属性 | 值 |
|------|-----|
| **Feature ID** | F-250 |
| **名称** | SDD 容器化目录结构 |
| **版本** | 1.2.11 |
| **优先级** | P0（前置基础设施） |
| **RICE 评分** | 80 |
| **预计工时** | 4 小时 |
| **依赖关系** | 无（独立基础设施） |
| **Blocking** | F-251（子 Feature Spec 结构） |

---

## 📁 目录结构设计

### 目标结构

```
.sdd/                          # SDD 工作空间容器（新建）
├── README.md                  # SDD 工作空间说明
├── ROADMAP.md                 # 版本路线图（从 .specs/ 移入）
├── config.json                # SDD 配置（可选）
│
└── .specs/                    # 规范存储目录（新建）
    ├── README.md              # Spec 目录导航
    ├── state.json             # 顶层状态（可选）
    │
    ├── sdd-multi-module/      # 子 Feature 1
    │   ├── README.md
    │   ├── spec.md
    │   ├── spec-f250.md       # F-250 需求
    │   ├── plan-f250.md       # F-250 规划
    │   ├── tasks-f250.md      # F-250 任务
    │   └── state.json
    │
    ├── sdd-plugin-baseline/   # 子 Feature 2
    ├── sdd-plugin-phase2/     # 子 Feature 3
    └── sdd-plugin-roadmap/    # 子 Feature 4
```

### 兼容结构

```
.specs/                        # 保留为兼容
├── ROADMAP.md                 # 重定向说明（指向 .sdd/ROADMAP.md）
├── state.json                 # 保留或移至 .sdd/.specs/
└── sdd-multi-module/          # 保留或移至 .sdd/.specs/
```

---

## 📄 已创建文档

| 文档 | 位置 | 说明 | 状态 |
|------|------|------|------|
| **spec-f250.md** | `.specs/sdd-multi-module/spec-f250.md` | F-250 需求规格 | ✅ 已完成 |
| **plan-f250.md** | `.specs/sdd-multi-module/plan-f250.md` | F-250 技术规划 | ✅ 已完成 |
| **tasks-f250.md** | `.specs/sdd-multi-module/tasks-f250.md` | F-250 任务分解 | ✅ 已完成 |
| **README.md** | `.specs/sdd-multi-module/README.md` | 更新子 Feature 索引 | ✅ 已更新 |
| **ROADMAP.md** | `.specs/ROADMAP.md` | 更新版本规划 | ✅ 已更新 |

---

## 🔧 任务分解

### 并行执行组

#### 组 1: 基础结构 (可并行)
- [ ] **TASK-250-001**: 创建 `.sdd/` 目录结构 (0.5h)
- [ ] **TASK-250-002**: 创建 README 文档 (1h)

#### 组 2: 文件迁移 (等待组 1)
- [ ] **TASK-250-003**: 迁移 ROADMAP.md (0.5h)
- [ ] **TASK-250-004**: 创建兼容层 (1h)

#### 组 3: 配置更新 (等待组 2)
- [ ] **TASK-250-005**: 更新 Agent 配置 (1h)

**总工时**: 4 小时

---

## 🚀 实施步骤

### 前置条件

- [ ] 备份现有 `.specs/` 目录
- [ ] 确认无进行中的 build 操作
- [ ] 通知团队成员

### 执行命令

```bash
# Step 1: 创建容器结构
mkdir -p .sdd/.specs

# Step 2: 创建 README 文档
# （使用 spec-f250.md 中的模板）

# Step 3: 移动 ROADMAP
mv .specs/ROADMAP.md .sdd/ROADMAP.md

# Step 4: 创建重定向
cat > .specs/ROADMAP.md << 'EOF'
# ROADMAP 已迁移

**新位置**: [`.sdd/ROADMAP.md`](../.sdd/ROADMAP.md)

请更新您的引用。
EOF

# Step 5: 移动 Feature 目录
mv .specs/sdd-multi-module .sdd/.specs/
mv .specs/sdd-plugin-baseline .sdd/.specs/
mv .specs/sdd-plugin-phase2 .sdd/.specs/
mv .specs/sdd-plugin-roadmap .sdd/.specs/
mv .specs/state.json .sdd/.specs/state.json

# Step 6: 验证
tree .sdd/ -L 2
```

### 验证步骤

```bash
# 验证目录结构
ls -la .sdd/
ls -la .sdd/.specs/

# 验证文件可访问性
cat .sdd/ROADMAP.md | head -5
cat .sdd/.specs/README.md | head -5

# 验证兼容性
cat .specs/ROADMAP.md  # 应显示重定向说明

# 验证 Feature 文档
cat .sdd/.specs/sdd-multi-module/spec-f250.md | head -5
```

---

## ⚠️ 风险与缓解

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| **引用路径失效** | 中 | 高 | 保留 `.specs/` 兼容层 |
| **Agent 配置错误** | 高 | 高 | 更新所有 Agent 提示词 |
| **团队成员困惑** | 中 | 中 | 提供清晰文档和通知 |
| **迁移中断工作** | 低 | 中 | 在非工作时间执行 |

---

## ✅ 验收标准

| ID | 验收项 | 验证方式 |
|----|--------|----------|
| **AC-250-1** | `.sdd/` 目录存在 | `ls -la .sdd/` |
| **AC-250-2** | `.sdd/.specs/` 目录存在 | `ls -la .sdd/.specs/` |
| **AC-250-3** | `.sdd/README.md` 内容完整 | 阅读审查 |
| **AC-250-4** | `.sdd/ROADMAP.md` 存在 | 文件检查 |
| **AC-250-5** | `.specs/ROADMAP.md` 重定向说明 | 内容检查 |
| **AC-250-6** | 所有 Feature 可访问 | 路径测试 |
| **AC-250-7** | Agent 配置已更新 | 配置审查 |
| **AC-250-8** | 工作流测试通过 | 执行测试 |

---

## 📊 与 sdd-multi-module 的关系

### 依赖关系图

```
F-250 (容器结构)
    ↓ blocks
F-251 (子 Feature Spec 结构)
    ↓ blocks
F-252 (分布式 State 管理)
F-253 (并行任务机制)
    ↓ blocks
F-254 (辅助工具支持)
```

### 在 sdd-multi-module 中的位置

```
sdd-multi-module (v1.2.11)
├── F-250: 🏗️ 容器化目录结构 (基础设施) ← 本规划
├── F-251: 📁 子 Feature Spec 结构
├── F-252: 🔄 分布式 State 管理
├── F-253: ⚡ 并行任务机制
└── F-254: 🛠️ 辅助工具支持
```

---

## 🔄 下一步行动

### 立即执行（今天）

1. [ ] **审查规划文档**
   - [ ] 阅读 `spec-f250.md`
   - [ ] 阅读 `plan-f250.md`
   - [ ] 阅读 `tasks-f250.md`

2. [ ] **确认实施计划**
   - [ ] 确认目录结构方案
   - [ ] 确认迁移策略
   - [ ] 确认时间安排

3. [ ] **执行实施**
   - [ ] 运行实施命令
   - [ ] 验证目录结构
   - [ ] 测试工作流

### 后续工作（本周）

1. [ ] **启动 F-251 规划**
   - [ ] 创建 F-251 spec.md
   - [ ] 创建 F-251 plan.md
   - [ ] 创建 F-251 tasks.md

2. [ ] **更新 Agent 配置**
   - [ ] 更新 `.opencode/` 中的路径引用
   - [ ] 测试 Agent 工作流

---

## 📝 设计原则

1. **容器化**: `.sdd/` 作为 SDD 工作空间的明确边界
2. **向后兼容**: 保留现有 `.specs/` 目录，支持渐进迁移
3. **逻辑清晰**: ROADMAP 在容器级，Spec 在 `.specs/` 子目录
4. **简单优先**: 不引入复杂配置，保持目录结构直观

---

## 🔗 相关链接

| 链接 | 说明 |
|------|------|
| [spec-f250.md](./.specs/sdd-multi-module/spec-f250.md) | F-250 需求规格 |
| [plan-f250.md](./.specs/sdd-multi-module/plan-f250.md) | F-250 技术规划 |
| [tasks-f250.md](./.specs/sdd-multi-module/tasks-f250.md) | F-250 任务分解 |
| [ROADMAP.md](./.specs/ROADMAP.md) | 版本路线图 |
| [README.md](./.specs/sdd-multi-module/README.md) | sdd-multi-module 导航 |

---

**文档版本**: 1.0  
**最后更新**: 2026-03-31  
**状态**: 规划完成，待实施
