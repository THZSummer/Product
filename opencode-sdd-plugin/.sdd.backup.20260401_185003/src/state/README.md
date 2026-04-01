# State Management

## 📂 目录简介

SDD 插件的状态管理模块，负责分布式 State 管理、版本迁移和 Schema 定义。

## 📁 目录结构

```
state/
├── README.md                 # 本文件
├── manager.ts                # 状态管理器
├── manager.test.ts           # 状态管理器测试
├── migrator.ts               # 状态迁移器
├── migrator.test.ts          # 迁移器测试
├── schema-v1.2.11.ts         # State Schema v1.2.11
└── schema-v1.2.11.test.ts    # Schema 测试
```

## 📄 文件说明

| 文件/目录 | 类型 | 说明 |
|-----------|------|------|
| README.md | 文件 | 本导航文件 |
| manager.ts | 文件 | 状态管理器 - 分布式 State 管理核心 |
| manager.test.ts | 文件 | 状态管理器单元测试 |
| migrator.ts | 文件 | 状态迁移器 - 版本间 State 迁移 |
| migrator.test.ts | 文件 | 迁移器单元测试 |
| schema-v1.2.11.ts | 文件 | State Schema v1.2.11 定义 |
| schema-v1.2.11.test.ts | 文件 | Schema 验证测试 |

## 📖 核心功能

### Manager (manager.ts)
- 分布式状态管理
- 状态读写操作
- 状态变更通知

### Migrator (migrator.ts)
- State 版本迁移
- 数据格式转换
- 迁移日志记录

### Schema (schema-v1.2.11.ts)
- State 数据结构定义
- 字段验证规则
- 版本兼容性声明

## 🔗 相关目录

- [上级目录](../) - SDD 源代码目录
- [工具类](../utils/) - 工具模块
- [测试目录](../../tests/) - 测试代码

---

**最后更新**: 2026-04-01
