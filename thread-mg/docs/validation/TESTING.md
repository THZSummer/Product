# 测试指南

**文档版本**: 1.0  
**最后更新**: 2026-03-27  

---

## 📋 目录

1. [测试脚本概述](#测试脚本概述)
2. [快速开始](#快速开始)
3. [使用指南](#使用指南)
4. [增量测试](#增量测试)
5. [覆盖率报告](#覆盖率报告)
6. [监听模式](#监听模式)
7. [常见问题](#常见问题)
8. [故障排除](#故障排除)

---

## 测试脚本概述

本项目提供三套测试脚本，用于简化 Phase 2 代码的测试工作：

| 脚本 | 用途 | 说明 |
|------|------|------|
| `test-phase2.sh` | 主测试脚本 | 支持所有测试功能 |
| `test-full.sh` | 完整测试快捷方式 | 快速运行完整测试 |
| `test-watch.sh` | 监听模式快捷方式 | 文件变更自动测试 |

### 功能特性

- ✅ **增量测试** - 只测试新增/修改的代码
- ✅ **完整测试** - 运行所有测试用例
- ✅ **覆盖率报告** - 生成 JaCoCo 覆盖率报告
- ✅ **彩色输出** - 友好的终端输出
- ✅ **监听模式** - 文件变更自动触发测试
- ✅ **详细统计** - 测试结果统计和耗时

---

## 快速开始

### 基本用法

```bash
# 运行增量测试（默认）
./scripts/test-phase2.sh

# 运行完整测试
./scripts/test-phase2.sh -full

# 生成覆盖率报告
./scripts/test-phase2.sh -coverage

# 查看帮助
./scripts/test-phase2.sh -help
```

### 输出示例

```
========================================
 Phase 2 测试
========================================

模式：incremental

--- 检测到变更文件 ---
  - src/main/java/.../ThreadPoolStrategy.java
  - src/test/java/.../ThreadPoolStrategyTest.java

--- 运行 Phase 2 增量测试 ---

测试类：ThreadPoolStrategyTest

[INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0


========================================
 测试结果
========================================
✅ 所有测试通过!

📊 统计信息:
  总计：19
  通过：19
  失败：0
  错误：0
  跳过：0
  通过率：100%
  耗时：2.3s

ℹ️  详细报告：target/surefire-reports/

✅ 测试完成
```

---

## 使用指南

### 命令行选项

| 选项 | 说明 | 示例 |
|------|------|------|
| `-full` | 运行完整测试套件 | `./scripts/test-phase2.sh -full` |
| `-incremental` | 只测试 Phase 2 变更（默认） | `./scripts/test-phase2.sh -incremental` |
| `-coverage` | 生成覆盖率报告 | `./scripts/test-phase2.sh -coverage` |
| `-report` | 生成详细测试报告 | `./scripts/test-phase2.sh -report` |
| `-watch` | 监听模式，文件变更自动测试 | `./scripts/test-phase2.sh -watch` |
| `-verbose` | 显示详细输出 | `./scripts/test-phase2.sh -verbose` |
| `-help` | 显示帮助信息 | `./scripts/test-phase2.sh -help` |

### 组合使用

```bash
# 完整测试 + 覆盖率报告
./scripts/test-phase2.sh -full -coverage

# 增量测试 + 详细报告
./scripts/test-phase2.sh -incremental -report -verbose

# 监听模式 + 覆盖率报告
./scripts/test-phase2.sh -watch -coverage
```

---

## 增量测试

### 工作原理

增量测试脚本通过以下步骤工作：

1. **检测 Git 变更** - 使用 `git diff` 检测最近提交或工作区变更
2. **匹配 Phase 2 类** - 过滤与 Phase 2 相关的文件
3. **推导测试类** - 根据主类名推导对应的测试类
4. **执行测试** - 只运行相关的测试类

### Phase 2 测试类映射

| 主类 | 测试类 | 测试数量 |
|------|--------|----------|
| `ThreadPoolStrategy` | `ThreadPoolStrategyTest` | 19 |
| `HighConcurrencyScenario` | `HighConcurrencyScenarioTest` | 23 |
| `MixedLoadScenario` | `MixedLoadScenarioTest` | 22 |
| `RecursiveScenario` | `RecursiveScenarioTest` | 22 |

### 使用场景

```bash
# 场景 1: 修改了 ThreadPoolStrategy.java
# 脚本会自动检测并只运行 ThreadPoolStrategyTest
./scripts/test-phase2.sh

# 场景 2: 没有检测到变更，运行完整测试
./scripts/test-phase2.sh
# 输出：未检测到 Phase 2 相关变更，运行完整测试套件...

# 场景 3: 强制运行完整测试
./scripts/test-phase2.sh -full
```

### 检测逻辑

```bash
# 检测顺序
1. git diff HEAD~1        # 最近一次提交的变更
2. git diff               # 工作区变更
3. git diff --cached      # 暂存区变更
```

---

## 覆盖率报告

### 生成报告

```bash
# 生成覆盖率报告
./scripts/test-phase2.sh -coverage

# 或单独生成
mvn test jacoco:report
```

### 报告位置

| 类型 | 路径 | 说明 |
|------|------|------|
| HTML | `target/site/jacoco/index.html` | 可视化报告 |
| XML | `target/site/jacoco/jacoco.xml` | 机器可读格式 |
| CSV | `target/site/jacoco/jacoco.csv` | 表格数据 |

### 查看报告

```bash
# 在浏览器中打开 HTML 报告
open target/site/jacoco/index.html      # macOS
xdg-open target/site/jacoco/index.html  # Linux
start target/site/jacoco/index.html     # Windows
```

### 覆盖率要求

| 指标 | 最低要求 | 目标 |
|------|----------|------|
| 指令覆盖率 | 80% | 90% |
| 分支覆盖率 | 75% | 85% |

---

## 监听模式

### 启动监听

```bash
# 启动监听模式
./scripts/test-phase2.sh -watch

# 或使用快捷命令
./scripts/test-watch.sh
```

### 工作原理

监听模式使用以下工具检测文件变更：

1. **inotifywait** (Linux) - 高性能文件监控
2. **fswatch** (macOS) - 跨平台文件监控
3. **轮询模式** (降级方案) - 每 5 秒检查一次

### 使用场景

```bash
# 开发时保持监听，文件保存后自动测试
./scripts/test-phase2.sh -watch

# 监听 + 完整测试 + 覆盖率
./scripts/test-phase2.sh -watch -full -coverage
```

### 退出监听

按 `Ctrl+C` 退出监听模式。

---

## 常见问题

### Q1: 脚本无法执行

**问题**: `Permission denied`

**解决**:
```bash
chmod +x scripts/test-phase2.sh
chmod +x scripts/test-full.sh
chmod +x scripts/test-watch.sh
```

### Q2: 未检测到变更

**问题**: 脚本提示"未检测到 Phase 2 相关变更"

**原因**: 
- 没有 Git 提交历史
- 变更文件不在 Phase 2 范围内

**解决**:
```bash
# 查看 Git 状态
git status

# 手动运行完整测试
./scripts/test-phase2.sh -full

# 或直接运行特定测试
mvn test -Dtest=ThreadPoolStrategyTest
```

### Q3: Maven 测试超时

**问题**: 测试运行时间过长

**解决**:
```bash
# 使用详细模式查看进度
./scripts/test-phase2.sh -verbose

# 运行单个测试类
mvn test -Dtest=ThreadPoolStrategyTest
```

### Q4: 颜色输出不正常

**问题**: 终端显示 ANSI 转义码

**原因**: 终端不支持彩色输出

**解决**:
- 使用支持彩色的终端（如 iTerm2、GNOME Terminal）
- 或脚本会自动降级为黑白输出

### Q5: 监听模式不工作

**问题**: 文件变更后没有自动测试

**解决**:
```bash
# 检查是否安装了监听工具
which inotifywait    # Linux
which fswatch        # macOS

# 安装工具
sudo apt install inotify-tools    # Linux
brew install fswatch              # macOS

# 或使用轮询模式（自动降级）
./scripts/test-phase2.sh -watch
```

---

## 故障排除

### 错误：Maven 未找到

```bash
# 检查 Maven 安装
mvn -version

# 安装 Maven
sudo apt install maven              # Linux
brew install maven                  # macOS
```

### 错误：Git 未找到

```bash
# 检查 Git 安装
git --version

# 安装 Git
sudo apt install git                # Linux
brew install git                    # macOS
```

### 错误：Java 版本不兼容

```bash
# 检查 Java 版本
java -version

# 本项目需要 Java 21+
# 安装 Java 21
sudo apt install openjdk-21-jdk     # Linux
brew install openjdk@21             # macOS
```

### 测试失败排查

```bash
# 1. 使用详细模式运行
./scripts/test-phase2.sh -verbose

# 2. 查看测试报告
ls target/surefire-reports/

# 3. 查看失败详情
cat target/surefire-reports/*.txt

# 4. 重新编译后测试
mvn clean test
```

### 覆盖率报告为空

```bash
# 1. 清理并重新生成
mvn clean test jacoco:report

# 2. 检查 JaCoCo 配置
cat pom.xml | grep -A 20 jacoco

# 3. 查看报告
open target/site/jacoco/index.html
```

---

## 附录

### 测试类列表

#### Phase 2 核心测试

| 测试类 | 位置 | 测试数量 |
|--------|------|----------|
| `ThreadPoolStrategyTest` | `src/test/java/com/threadmg/threads/pool/` | 19 |
| `HighConcurrencyScenarioTest` | `src/test/java/com/threadmg/scenarios/concurrency/` | 23 |
| `MixedLoadScenarioTest` | `src/test/java/com/threadmg/scenarios/mixed/` | 22 |
| `RecursiveScenarioTest` | `src/test/java/com/threadmg/scenarios/recursive/` | 22 |

#### 其他测试

| 测试类 | 位置 |
|--------|------|
| `BenchmarkRunnerTest` | `src/test/java/com/threadmg/benchmark/core/` |
| `ScenarioTest` | `src/test/java/com/threadmg/benchmark/core/` |
| `ConsoleReporterTest` | `src/test/java/com/threadmg/reporters/` |
| `HtmlReporterTest` | `src/test/java/com/threadmg/reporters/` |
| `JsonReporterTest` | `src/test/java/com/threadmg/reporters/` |

### 脚本源码位置

| 脚本 | 源码 |
|------|------|
| `test-phase2.sh` | `scripts/test-phase2.sh` |
| `test-full.sh` | `scripts/test-full.sh` |
| `test-watch.sh` | `scripts/test-watch.sh` |

### 相关文档

- [验证方案](README.md#-验证方案)
- [SDD 工作流](README.md#-开发工作流)
- [Phase 2 任务](.specs/phase2/tasks.md)

---

<div align="center">

**📍 快速导航**: [快速开始](#快速开始) | [使用指南](#使用指南) | [常见问题](#常见问题) | [故障排除](#故障排除)

*最后更新：2026-03-27*

</div>
