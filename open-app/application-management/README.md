# Application Management Module

应用管理模块 - Open App 核心业务模块

## 📦 模块概述

本模块提供应用管理的核心领域逻辑和数据持久化功能，包括：

- **领域模型（Domain Layer）**: 应用实体、状态枚举、状态转换逻辑
- **数据库迁移（Database Migration）**: Flyway 迁移脚本
- **领域服务（Domain Services）**: 领域模型验证、业务规则校验

## 📁 目录结构

```
application-management/
├── pom.xml                          # Maven 配置文件
├── README.md                        # 模块文档
└── src/
    ├── main/
    │   ├── java/com/openapp/application/
    │   │   └── domain/              # 领域层
    │   │       ├── Application.java           # 应用实体
    │   │       ├── enums/                     # 枚举类型
    │   │       │   ├── AppType.java           # 应用类型枚举
    │   │       │   └── AppStatus.java         # 应用状态枚举
    │   │       ├── StatusTransitions.java     # 状态转换逻辑
    │   │       └── DomainModelValidator.java  # 领域模型验证器
    │   └── resources/
    │       └── db/migration/        # 数据库迁移脚本
    │           ├── V1__init_applications.sql
    │           └── V2__init_application_status_logs.sql
    └── test/                        # 测试代码
        ├── java/
        └── resources/
```

## 🗂️ 核心组件

### 领域模型 (Domain Layer)

| 文件 | 描述 |
|------|------|
| `Application.java` | 应用实体，包含基本信息、状态跟踪 |
| `AppType.java` | 应用类型枚举（WEB、MOBILE、DESKTOP 等） |
| `AppStatus.java` | 应用状态枚举（DRAFT、PENDING、APPROVED 等） |
| `StatusTransitions.java` | 状态转换规则和验证逻辑 |
| `DomainModelValidator.java` | 领域模型验证器，实现业务规则校验 |

### 数据库迁移 (Database Migration)

| 文件 | 描述 |
|------|------|
| `V1__init_applications.sql` | 初始化 applications 表 |
| `V2__init_application_status_logs.sql` | 初始化 application_status_logs 表 |

## 🔧 技术栈

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Flyway** (数据库版本控制)
- **PostgreSQL** (数据库)
- **Lombok** (代码简化)
- **JUnit 5** (单元测试)
- **AssertJ** (流式断言)

## 🚀 使用方式

### 添加依赖

```xml
<dependency>
    <groupId>com.openapp</groupId>
    <artifactId>application-management</artifactId>
    <version>${project.version}</version>
</dependency>
```

### 运行数据库迁移

```bash
mvn flyway:migrate -pl application-management
```

### 运行测试

```bash
mvn test -pl application-management
```

## 📋 开发规范

1. **领域驱动设计 (DDD)**: 保持领域模型纯净，不依赖基础设施
2. **不变量保护**: 通过实体方法维护业务不变量
3. **状态机模式**: 使用 `StatusTransitions` 管理状态转换
4. **测试覆盖**: 领域逻辑必须有单元测试覆盖

## 📝 版本历史

| 版本 | 日期 | 描述 |
|------|------|------|
| 1.0.0 | 2026-03-23 | 初始版本，包含 TASK-001 和 TASK-002 |

## 🔗 相关模块

- `application-api` - API 接口层（待实现）
- `application-infrastructure` - 基础设施层（待实现）

## 📄 许可证

MIT License
