# E2E Tests

## 📂 目录简介

SDD 插件的端到端测试目录，验证完整的功能流程和用户场景。

## 📁 目录结构

```
e2e/
├── README.md                 # 本文件
├── e2e-test.fixture.ts       # 测试夹具
└── main-feature-flow.test.ts # 主流程测试
```

## 📄 文件说明

| 文件/目录 | 类型 | 说明 |
|-----------|------|------|
| README.md | 文件 | 本导航文件 |
| e2e-test.fixture.ts | 文件 | 测试夹具 - 提供测试环境和工具 |
| main-feature-flow.test.ts | 文件 | 主流程测试 - 验证 SDD 核心工作流 |

## 🧪 测试覆盖

### e2e-test.fixture.ts
- 测试环境设置
- Mock 数据提供
- 测试辅助函数

### main-feature-flow.test.ts
- Feature 创建流程
- Spec → Plan → Tasks 流程验证
- Build 阶段验证
- Review 和 Validate 阶段验证

## 🚀 运行测试

```bash
# 运行所有 e2e 测试
npm run test:e2e

# 运行特定测试文件
npm run test:e2e -- main-feature-flow.test.ts
```

## 🔗 相关目录

- [上级目录](../) - 测试目录
- [源代码](../../src/) - 源代码目录
- [状态管理](../../src/state/) - State 模块
- [工具类](../../src/utils/) - Utils 模块

---

**最后更新**: 2026-04-01
