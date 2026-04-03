# SDD Specs Tree Root

这是 SDD (Specifications-Driven Development) 系统的规范根目录。根据最新目录结构优化，原 `.sdd/.specs/` 目录已迁移至此。

## 目录结构
```
.sdd/specs-tree-root/
├── README.md                    # 本文件 - 规范根目录说明  
├── [feature]/                   # Feature 目录
│   ├── spec.md                  # 功能需求规格说明
│   ├── plan.md                  # 技术实施计划
│   ├── tasks.md                 # 任务分解文档
│   ├── state.json               # 工作流状态跟踪
│   ├── review.md                # 代码审查记录 (可选)
│   └── validate.md              # 最终验证报告 (可选)
├── specs-tree-agentic/         # 智能化特性相关规范
├── specs-tree-directory-naming/ # 目录命名规范特性
├── specs-tree-directory-optimization/ # 目录结构优化特性（当前迁移来源）
├── specs-tree-state-json-fix/   # 状态管理JSON修复相关规范
└── ...                         # 其他特性目录

## 功能概览
当前包含以下主要特性模块：

1. **login-module** - 登录认证模块（P0 优先级，已规划）
2. **specs-tree-agentic** - SDD 智能化功能
3. **specs-tree-directory-naming** - 目录结构命名约定
4. **specs-tree-directory-optimization** - 目录结构优化（当前执行的迁移）
5. **specs-tree-state-json-fix** - 状态管理 JSON 修复
6. 以及其他正在开发的 specs-tree 系列功能

## 特性目录列表

| 目录 | 说明 | 状态 | 优先级 |
|------|------|------|--------|
| [login-module/](./login-module/) | 登录认证模块 - 支持用户名密码、手机验证码、第三方 OAuth 登录 | planned | P0 |
| [specs-tree-agentic/](./specs-tree-agentic/) | SDD 智能化功能 | - | - |
| [specs-tree-directory-naming/](./specs-tree-directory-naming/) | 目录结构命名约定 | - | - |
| [specs-tree-directory-optimization/](./specs-tree-directory-optimization/) | 目录结构优化 | - | - |
| [specs-tree-state-json-fix/](./specs-tree-state-json-fix/) | 状态管理 JSON 修复 | - | - |

## 特性目录约定
所有特性目录均遵循 `specs-tree-[功能命名]` 的命名模式。
