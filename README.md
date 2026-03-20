# Product

产品仓库 - Product Repository

## 📖 仓库简介

Product 是一个产品项目集合，包含多个相关子项目和工具。

---

## 📁 项目结构

```
Product/
├── permission-app/              # 权限管理系统
│   ├── src/                     # 源代码
│   ├── .opencode/               # OpenCode 配置
│   ├── .specs/                  # 规范文档
│   ├── README.md                # 项目说明
│   └── DEV_WORKFLOW.md          # 开发工作流
│
├── opencode-sdd-plugin/         # OpenCode SDD 插件
│   ├── src/                     # 插件源码
│   ├── templates/               # 模板文件
│   ├── README.md                # 插件说明
│   └── INSTALL.md               # 安装指南
│
└── README.md                    # 本文件
```

---

## 📦 子项目

### 1. permission-app

**权限管理系统** - Permission Management System

专注于细粒度权限控制的业务系统。

- 🔐 **核心功能**: 角色管理、权限分配、访问控制
- 🛠️ **技术栈**: Node.js, TypeScript
- 📖 **文档**: [permission-app/README.md](./permission-app/README.md)

**快速开始**:
```bash
cd permission-app
npm install
npm run dev
```

---

### 2. opencode-sdd-plugin

**OpenCode SDD 插件** - Specification-Driven Development Plugin

基于 OpenCode 官方规范的 SDD 工作流插件。

- 🎯 **功能**: 规范驱动开发、智能工作流、代码审查
- 🛠️ **技术栈**: TypeScript, OpenCode Plugin API
- 📖 **文档**: [opencode-sdd-plugin/README.md](./opencode-sdd-plugin/README.md)

**安装使用**:
```bash
cd opencode-sdd-plugin
npm install
npm run build
# 复制到目标项目
```

---

## 🛠️ 开发工具

### OpenCode

本项目使用 OpenCode 作为 AI 辅助开发工具。

- **版本**: >= 1.2.27
- **插件**: opencode-sdd-plugin
- **工作流**: SDD (Specification-Driven Development)

### SDD 工作流

```
1.spec → 2.plan → 3.tasks → 4.build → 5.review → 6.validate
```

详细开发指南请查看各子项目的开发文档。

---

## 🚀 快速开始

### 克隆仓库

```bash
git clone <repository-url> Product
cd Product
```

### 安装依赖

```bash
# 安装 permission-app 依赖
cd permission-app
npm install

# 安装插件依赖
cd ../opencode-sdd-plugin
npm install
npm run build
```

### 开始开发

```bash
# 开发 permission-app
cd permission-app
npm run dev

# 或使用 OpenCode SDD 工作流
opencode @sdd "开始 新功能"
```

---

## 📚 文档导航

| 项目 | 文档 | 说明 |
|------|------|------|
| **Product** | [README.md](./README.md) | 仓库总览 |
| **permission-app** | [README.md](./permission-app/README.md) | 项目说明 |
| **permission-app** | [DEV_QUICKSTART.md](./permission-app/DEV_QUICKSTART.md) | 开发快速指南 |
| **permission-app** | [DEV_WORKFLOW.md](./permission-app/DEV_WORKFLOW.md) | 开发工作流 |
| **opencode-sdd-plugin** | [README.md](./opencode-sdd-plugin/README.md) | 插件说明 |
| **opencode-sdd-plugin** | [INSTALL.md](./opencode-sdd-plugin/INSTALL.md) | 安装指南 |

---

## 🧪 测试

### permission-app 测试

```bash
cd permission-app
npm test
npm run test:coverage
```

### opencode-sdd-plugin 测试

```bash
cd opencode-sdd-plugin
npm run build
# 在目标项目中测试
```

---

## 📦 构建和部署

### permission-app

```bash
cd permission-app
npm run build
npm start
```

### opencode-sdd-plugin

```bash
cd opencode-sdd-plugin
npm run build
# 复制到目标项目
```

---

## 🤝 贡献

### 提交代码前

1. 阅读相关项目的 README.md
2. 遵循项目的开发工作流
3. 确保测试通过
4. 更新相关文档

### 代码审查清单

- [ ] 代码符合项目规范
- [ ] 测试覆盖完整
- [ ] 文档已更新
- [ ] 提交信息清晰

---

## 📄 许可证

MIT License

---

## 📞 联系方式

- **仓库地址**: github.com/THZSummer/Product
- **问题反馈**: 提交 Issue

---

**版本**: 1.0.0  
**创建日期**: 2026-03-20  
**最后更新**: 2026-03-20
