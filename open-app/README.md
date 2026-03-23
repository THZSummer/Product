# open-app

权限管理系统 - Permission Management System

## 📖 项目简介

open-app 是一个专注于权限管理的系统，提供细粒度的权限控制、角色管理和访问控制功能。

### 核心功能

- 🔐 **权限管理** - 菜单级、功能级、数据级权限控制
- 👥 **角色管理** - 角色创建、权限分配、角色层级
- 🔒 **访问控制** - 服务端权限验证、最小权限原则
- 📝 **审计日志** - 敏感操作记录、权限变更追踪

---

## 🚀 快速开始

### 前置条件

- Node.js >= 18.0
- npm >= 9.0
- OpenCode >= 1.2.27

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

---

## 📁 项目结构

```
open-app/
├── src/                    # 源代码
│   ├── auth/               # 认证授权
│   ├── rbac/               # 角色权限控制
│   ├── api/                # API 接口
│   └── utils/              # 工具函数
│
├── .opencode/              # OpenCode 配置
│   ├── agents/             # AI Agent 定义
│   ├── sdd/                # SDD 工作流配置
│   └── constitution.md     # 项目宪法
│
├── .specs/                 # 规范文档
│   ├── project/            # 项目配置
│   ├── architecture/       # 架构设计
│   ├── planning/           # 规划文档
│   ├── development/        # 开发指南
│   ├── quality/            # 质量标准
│   └── examples/           # 示例规范
│
├── docs/                   # 项目文档
├── DEV_QUICKSTART.md       # 开发快速指南
├── DEV_WORKFLOW.md         # 开发工作流说明
├── package.json
└── README.md               # 本文件
```

---

## 🛠️ 开发工作流

本项目采用 **SDD (Specification-Driven Development)** 开发方法论，强调在编写代码之前先创建完整的规范文档。

### 使用 OpenCode SDD

```bash
# 开始新 feature
opencode @sdd "开始 用户登录"

# 继续工作
opencode @sdd "继续"

# 查看状态
opencode @sdd "状态"

# 查看帮助
opencode @sdd "帮助"
```

### 开发流程

```
1. 规范编写 → 2. 技术规划 → 3. 任务分解 → 4. 任务实现 → 5. 代码审查 → 6. 最终验证
```

详细开发指南请查看：
- **快速指南**: `DEV_QUICKSTART.md`
- **完整工作流**: `DEV_WORKFLOW.md`

---

## 🔐 权限管理最佳实践

### 宪法约束

项目宪法 (`.opencode/constitution.md`) 定义了不可协商的规则：

1. **服务端验证** - 所有权限检查必须在服务端执行
2. **审计日志** - 敏感操作必须有审计日志
3. **最小权限** - 遵循最小权限原则
4. **默认拒绝** - 默认拒绝，显式允许

### 权限粒度

- **菜单级权限** - 控制菜单可见性
- **功能级权限** - 控制功能按钮/操作
- **数据级权限** - 控制数据访问范围

### 测试要求

- 权限矩阵测试（角色 × 权限）
- 越权访问测试
- 并发权限变更测试

---

## 📚 文档导航

| 文档 | 路径 | 说明 |
|------|------|------|
| **快速指南** | `DEV_QUICKSTART.md` | 30 秒上手开发 |
| **开发工作流** | `DEV_WORKFLOW.md` | 完整开发流程说明 |
| **项目宪法** | `.opencode/constitution.md` | 不可协商原则 |
| **示例规范** | `.specs/examples/` | 规范编写示例 |
| **架构决策** | `.specs/architecture/adr/` | 架构决策记录 |

---

## 🧪 测试

```bash
# 运行单元测试
npm test

# 运行测试并生成覆盖率报告
npm run test:coverage

# 运行特定测试
npm test -- --testNamePattern="权限验证"
```

---

## 📦 部署

### 开发环境

```bash
npm run dev
```

### 生产环境

```bash
# 构建
npm run build

# 启动服务
npm start
```

---

## 🤝 贡献

### 提交代码前

1. 阅读项目宪法 (`.opencode/constitution.md`)
2. 遵循 SDD 工作流
3. 确保测试通过
4. 更新相关文档

### 代码审查清单

- [ ] 代码符合项目宪法
- [ ] 权限检查在服务端执行
- [ ] 敏感操作有审计日志
- [ ] 遵循最小权限原则
- [ ] 单元测试覆盖

---

## 📄 许可证

MIT License

---

## 📞 联系方式

- **项目仓库**: github.com/THZSummer/Product
- **问题反馈**: 提交 Issue

---

**版本**: 1.0.0  
**创建日期**: 2026-03-20
