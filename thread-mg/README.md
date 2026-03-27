# Thread Management Benchmark (thread-mg)

[![Java](https://img.shields.io/badge/Java-21+-blue.svg)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> 探究对比 Java 不同类型线程技术的性能差异及各自适用场景

---

## 项目概述

本项目旨在系统性地研究和对比 Java 平台上不同类型线程技术在相同场景下的性能表现，帮助开发者根据具体业务场景选择最优的并发方案。

### 研究目标

1. **性能对比** - 在相同测试场景下对比不同线程技术的吞吐量、延迟、资源消耗
2. **场景分析** - 识别每种线程技术最适合的业务场景
3. **实践指南** - 为开发者提供基于数据的选型建议

---

## 研究的线程技术

| 线程类型 | 说明 | Java 版本 |
|---------|------|----------|
| **平台线程 (Platform Threads)** | 传统 OS 线程，1:1 映射 | Java 8+ |
| **虚拟线程 (Virtual Threads)** | 轻量级用户态线程，M:N 映射 | Java 21+ |
| **线程池 (Thread Pool)** | 复用线程资源，减少创建开销 | Java 5+ |
| **Fork/Join 框架** | 工作窃取算法，适合递归任务 | Java 7+ |
| **CompletableFuture** | 异步编程，非阻塞 I/O | Java 8+ |
| **Reactive Streams** | 响应式流，背压支持 | Java 9+ |

---

## 测试场景

### 计划覆盖的场景

| 场景 | 描述 | 预期优势技术 |
|------|------|-------------|
| **CPU 密集型** | 大量计算任务 | 平台线程/Fork/Join |
| **I/O 密集型** | 网络/磁盘 I/O 等待 | 虚拟线程 |
| **高并发短任务** | 大量短时任务并发 | 虚拟线程/线程池 |
| **混合负载** | CPU+I/O 混合场景 | 根据比例选择 |
| **递归分治** | 递归算法、大数据处理 | Fork/Join |
| **异步流水线** | 多阶段异步处理 | CompletableFuture |

---

## 性能指标

### 核心指标

- **吞吐量 (Throughput)** - 单位时间内完成的任务数
- **延迟 (Latency)** - 任务完成所需时间 (P50/P95/P99)
- **资源消耗** - CPU 使用率、内存占用、线程数
- **可扩展性** - 随并发数增加的性能变化

### 监控工具

- JFR (Java Flight Recorder)
- JMH (Java Microbenchmark Harness)
- VisualVM / Async Profiler

---

## 项目结构

```
thread-mg/
├── .opencode/                    # SDD 工作流配置
│   ├── agents/                   # SDD Agent 定义
│   └── plugins/sdd/               # SDD 插件
├── .specs/                       # 项目规范文档
│   ├── architecture/             # 架构决策记录
│   └── examples/                 # 示例规范
├── src/                          # 源代码 (待实现)
│   ├── main/java/
│   │   └── com/threadmg/
│   │       ├── benchmark/        # 基准测试
│   │       ├── scenarios/        # 测试场景
│   │       └── threads/          # 线程实现
│   └── test/java/
├── docs/                         # 文档
├── results/                      # 测试结果 (生成)
├── opencode.json                 # OpenCode 配置
├── SDD_INSTALL_GUIDE.md          # SDD 安装指南
└── README.md                     # 本文件
```

---

## 开发工作流

本项目采用 **SDD (Specification-Driven Development)** 工作流：

```
1. spec → 2. plan → 3. tasks → 4. build → 5. review → 6. validate
```

### 快速开始

```bash
# 1. 开始新功能
@sdd 开始 [功能名称]

# 2. 查看进度
@sdd 状态

# 3. 继续工作
@sdd 继续
```

### 阶段说明

| 阶段 | 命令 | 输出 |
|------|------|------|
| 1. 规范编写 | `@sdd-spec` | `.specs/[feature]/spec.md` |
| 2. 技术规划 | `@sdd-plan` | `.specs/[feature]/plan.md` |
| 3. 任务分解 | `@sdd-tasks` | `.specs/[feature]/tasks.md` |
| 4. 任务实现 | `@sdd-build` | 代码、测试 |
| 5. 代码审查 | `@sdd-review` | 审查报告 |
| 6. 最终验证 | `@sdd-validate` | 验证报告 |

---

## 当前状态

### 已完成
- [x] 项目初始化
- [x] SDD 工作流配置
- [x] 项目结构设计

### 进行中
- [ ] 基准测试框架搭建
- [ ] 第一个测试场景实现

### 计划中
- [ ] 平台线程基准实现
- [ ] 虚拟线程基准实现
- [ ] 性能对比分析

---

## 技术栈

| 类别 | 技术 |
|------|------|
| **语言** | Java 21+ |
| **构建工具** | Maven / Gradle |
| **基准测试** | JMH |
| **监控** | JFR, Async Profiler |
| **开发工作流** | SDD (OpenCode) |

---

## 相关资源

- [Java Virtual Threads (JEP 444)](https://openjdk.org/jeps/444)
- [Java Concurrency in Practice](https://jcip.net/)
- [JMH Documentation](https://openjdk.org/projects/code-tools/jmh/)
- [Loom Project](https://wiki.openjdk.org/display/loom/Main)

---

## 贡献指南

本项目采用 SDD 工作流进行开发，贡献前请确保熟悉 SDD 流程：

1. 阅读 `SDD_INSTALL_GUIDE.md`
2. 在 OpenCode 中运行 `@sdd 帮助` 查看完整命令
3. 按照规范编写→技术规划→任务分解→实现的流程进行开发

---

## 许可证

MIT License - 详见 [LICENSE](LICENSE)

---

*最后更新：2026-03-27*