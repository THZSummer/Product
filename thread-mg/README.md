# Thread Management Benchmark (thread-mg)

[![Java](https://img.shields.io/badge/Java-21+-blue.svg)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> 探究对比 Java 不同类型线程技术的性能差异及各自适用场景

---

## 📋 项目概述

本项目旨在系统性地研究和对比 Java 平台上不同类型线程技术在相同场景下的性能表现，帮助开发者根据具体业务场景选择最优的并发方案。

### 研究目标

1. **性能对比** - 在相同测试场景下对比不同线程技术的吞吐量、延迟、资源消耗
2. **场景分析** - 识别每种线程技术最适合的业务场景
3. **实践指南** - 为开发者提供基于数据的选型建议

---

## 🧵 研究的线程技术

| 线程类型 | 说明 | Java 版本 | 核心特点 |
|---------|------|----------|---------|
| **平台线程 (Platform)** | 传统 OS 线程，1:1 映射 | Java 8+ | 重量级，系统调度 |
| **虚拟线程 (Virtual)** | 轻量级用户态线程，M:N 映射 | Java 21+ | 轻量级，应用调度 |
| **线程池 (Pool)** | 复用线程资源，减少创建开销 | Java 5+ | 资源可控，复用高效 |
| **Fork/Join** | 工作窃取算法，适合递归任务 | Java 7+ | 分治策略，负载均衡 |
| **CompletableFuture** | 异步编程，非阻塞 I/O | Java 8+ | 链式调用，组合灵活 |
| **Reactive Streams** | 响应式流，背压支持 | Java 9+ | 流式处理，背压控制 |

---

## 🏗️ 工程结构矩阵

本项目按 **应用场景 × 线程技术类型** 组合划分工程结构：

### 结构矩阵总览

```
                        ┌─────────────── 线程技术类型 ────────────────┐
                        │ 平台  │ 虚拟  │ 线程池 │ F/J  │ CF   │ React│
    ┌───────────────────┼───────┼───────┼────────┼──────┼──────┼──────┤
    │ CPU 密集型        │  ✓   │  ✓   │   ✓   │  ✓  │  ✓  │  ✓  │
    │ I/O 密集型        │  ✓   │  ✓   │   ✓   │  -  │  ✓  │  ✓  │
    │ 高并发短任务      │  ✓   │  ✓   │   ✓   │  -  │  ✓  │  ✓  │
场 │ 混合负载          │  ✓   │  ✓   │   ✓   │  ✓  │  ✓  │  ✓  │
景 │ 递归分治          │  -   │  -   │   -   │  ✓  │  -  │  -  │
    │ 异步流水线       │  -   │  -   │   -   │  -  │  ✓  │  ✓  │
    └───────────────────┴───────┴───────┴────────┴──────┴──────┴──────┘
    
    图例: ✓ = 推荐  |  - = 不推荐/不支持
```

### 详细矩阵说明

| 场景 | 平台线程 | 虚拟线程 | 线程池 | Fork/Join | CompletableFuture | Reactive |
|------|:--------:|:--------:|:------:|:---------:|:-----------------:|:--------:|
| **CPU 密集型** | ⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐ |
| **I/O 密集型** | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | - | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **高并发短任务** | ⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | - | ⭐⭐⭐ | ⭐⭐⭐ |
| **混合负载** | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ |
| **递归分治** | - | - | - | ⭐⭐⭐⭐⭐ | - | - |
| **异步流水线** | - | - | - | - | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |

> **推荐指数说明**: ⭐⭐⭐⭐⭐ = 强烈推荐 | ⭐⭐⭐⭐ = 推荐 | ⭐⭐⭐ = 可选 | ⭐⭐ = 不推荐 | ⭐ = 避免使用

---

## 📁 目录结构

```
thread-mg/
│
├── 📄 README.md                          # 项目入口文档（本文件）
├── 📄 opencode.json                      # OpenCode 配置
├── 📄 SDD_INSTALL_GUIDE.md               # SDD 安装指南
│
├── 📂 .opencode/                         # SDD 工作流配置
│   ├── agents/                           # SDD Agent 定义
│   │   ├── sdd.md                        # SDD 主协调器
│   │   ├── sdd-1-spec.md                 # 阶段 1: 规范编写
│   │   ├── sdd-2-plan.md                 # 阶段 2: 技术规划
│   │   ├── sdd-3-tasks.md                # 阶段 3: 任务分解
│   │   ├── sdd-4-build.md                # 阶段 4: 任务实现
│   │   ├── sdd-5-review.md               # 阶段 5: 代码审查
│   │   └── sdd-6-validate.md             # 阶段 6: 验证
│   └── plugins/sdd/                      # SDD 插件
│       ├── index.js                      # 插件入口
│       ├── commands/                     # 命令实现
│       └── state/                        # 状态管理
│
├── 📂 .specs/                            # 项目规范文档
│   ├── architecture/                     # 架构决策记录 (ADR)
│   ├── examples/                         # 示例规范
│   └── [feature]/                        # 功能规范目录
│       ├── spec.md                       # 功能规范
│       ├── plan.md                       # 技术规划
│       └── tasks.md                      # 任务分解
│
├── 📂 src/                               # 源代码
│   ├── main/java/com/threadmg/
│   │   │
│   │   ├── benchmark/                    # 基准测试框架
│   │   │   ├── core/                     # 核心框架
│   │   │   │   ├── Benchmark.java        # 基准接口
│   │   │   │   ├── BenchmarkRunner.java  # 运行器
│   │   │   │   ├── BenchmarkResult.java  # 结果对象
│   │   │   │   ├── Scenario.java         # 场景接口
│   │   │   │   └── ThreadStrategy.java   # 线程策略接口
│   │   │   │
│   │   │   ├── metrics/                  # 指标收集
│   │   │   │   ├── MetricCollector.java  # 收集器
│   │   │   │   ├── ThroughputMetric.java # 吞吐量
│   │   │   │   ├── LatencyMetric.java    # 延迟
│   │   │   │   └── ResourceMetric.java   # 资源
│   │   │   │
│   │   │   ├── reporters/                # 报告生成
│   │   │   │   ├── MarkdownReporter.java # Markdown
│   │   │   │   ├── HtmlReporter.java     # HTML
│   │   │   │   └── JsonReporter.java     # JSON
│   │   │   │
│   │   │   └── config/                   # 配置管理
│   │   │       ├── BenchmarkConfig.java  # 基准配置
│   │   │       └── ValidationConfig.java # 验证配置
│   │   │
│   │   ├── scenarios/                    # 测试场景（按场景划分）
│   │   │   ├── cpu-bound/                # CPU 密集型
│   │   │   │   ├── CpuBoundScenario.java
│   │   │   │   └── tasks/                # 计算任务
│   │   │   ├── io-bound/                 # I/O 密集型
│   │   │   │   ├── IoBoundScenario.java
│   │   │   │   └── tasks/
│   │   │   ├── high-concurrency/         # 高并发短任务
│   │   │   ├── mixed-load/               # 混合负载
│   │   │   ├── recursive/                # 递归分治
│   │   │   └── async-pipeline/           # 异步流水线
│   │   │
│   │   └── threads/                      # 线程实现（按技术类型划分）
│   │       ├── platform/                 # 平台线程
│   │       │   ├── PlatformThreadStrategy.java
│   │       │   └── config/
│   │       ├── virtual/                  # 虚拟线程
│   │       │   ├── VirtualThreadStrategy.java
│   │       │   └── config/
│   │       ├── pool/                     # 线程池
│   │       │   ├── ThreadPoolStrategy.java
│   │       │   └── config/
│   │       ├── forkjoin/                 # Fork/Join
│   │       │   ├── ForkJoinStrategy.java
│   │       │   └── config/
│   │       ├── completable/              # CompletableFuture
│   │       │   ├── CompletableFutureStrategy.java
│   │       │   └── config/
│   │       └── reactive/                 # Reactive Streams
│   │           ├── ReactiveStrategy.java
│   │           └── config/
│   │
│   └── test/java/com/threadmg/           # 测试代码（镜像 src 结构）
│       ├── benchmark/
│       ├── scenarios/
│       └── threads/
│
├── 📂 docs/                              # 文档
│   ├── scenarios/                        # 场景说明
│   │   ├── cpu-bound.md
│   │   ├── io-bound.md
│   │   └── ...
│   ├── threads/                          # 线程技术说明
│   │   ├── platform.md
│   │   ├── virtual.md
│   │   └── ...
│   ├── validation/                       # 验证方案
│   │   └── README.md
│   └── implementation/                   # 实现指南
│       └── README.md
│
├── 📂 results/                           # 测试结果（生成）
│   ├── raw/                              # 原始数据
│   ├── processed/                        # 处理后的数据
│   └── reports/                          # 可视化报告
│
├── 📂 config/                            # 配置文件
│   ├── benchmark/                        # 基准测试配置
│   │   └── benchmark.yaml
│   └── validation/                       # 验证配置
│       └── validation.yaml
│
└── 📂 scripts/                           # 辅助脚本
    ├── run-benchmark.sh                  # 运行基准测试
    ├── generate-report.sh                # 生成报告
    └── validate.sh                       # 运行验证
```

---

## 📊 性能指标

### 核心指标

| 指标类别 | 具体指标 | 说明 | 工具 |
|---------|---------|------|------|
| **吞吐量** | ops/sec | 每秒操作数 | JMH |
| | tasks/sec | 每秒任务数 | JMH |
| | bytes/sec | 每秒处理字节数 | JMH |
| **延迟** | P50 | 中位延迟 | JFR |
| | P95 | 95 百分位延迟 | JFR |
| | P99 | 99 百分位延迟 | JFR |
| **资源** | CPU% | CPU 使用率 | JFR |
| | Memory | 内存占用 | JFR |
| | Thread Count | 线程数 | JMX |
| **稳定性** | Error Rate | 错误率 | 自定义 |
| | Crash Rate | 崩溃率 | 自定义 |

### 监控工具

- **JFR (Java Flight Recorder)** - 低开销性能监控
- **JMH (Java Microbenchmark Harness)** - 微基准测试
- **VisualVM / Async Profiler** - 性能分析

---

## ✅ 验证方案

### 验证维度

| 维度 | 指标 | 工具 | 频率 | 阈值 |
|------|------|------|------|------|
| **正确性** | 结果准确率 | JUnit 5 | 每次提交 | 100% |
| **性能** | 吞吐量/延迟 | JMH | 每次构建 | - |
| **性能回归** | 性能变化率 | 对比历史 | 每次合并 | ≤ 5% |
| **代码覆盖** | 测试覆盖率 | JaCoCo | 每次提交 | ≥ 80% |
| **资源消耗** | CPU/内存 | JFR | 每周 | 基线±10% |
| **稳定性** | 错误率 | 自定义 | 持续 | ≤ 0.1% |

### 验证流程

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   代码提交   │ →  │  单元测试   │ →  │ 基准测试   │ →  │  报告生成   │
│   (Commit)  │    │   (Unit)    │    │ (Benchmark) │    │   (Report)  │
└─────────────┘    └──────┬──────┘    └──────┬──────┘    └──────┬──────┘
                          │                  │                  │
                     ✓/✗ 通过/失败      ⚠️ 性能回归检测     📊 可视化展示
                          │                  │                  │
                          ↓                  ↓                  ↓
                   失败则拒绝合并       超阈值需要说明       存档到 results/
```

### 通过标准

| 检查项 | 标准 | 处理措施 |
|--------|------|----------|
| **单元测试** | 100% 通过 | 失败则拒绝合并 |
| **性能回归** | 下降 ≤ 5% | 超阈值需要说明原因 |
| **代码覆盖** | ≥ 80% | 低于阈值需要补充测试 |
| **文档完整** | README 已更新 | 缺失则拒绝合并 |
| **代码规范** | 无 CheckStyle 警告 | 警告需要修复 |

### 验证配置

```yaml
# config/validation/validation.yaml
validation:
  performance:
    regressionThreshold: 0.05    # 5% 性能回归阈值
    warmupIterations: 5          # 预热轮次
    benchmarkIterations: 10      # 基准轮次
  coverage:
    minimum: 0.80                # 最低覆盖率
  stability:
    testRuns: 3                  # 稳定性测试次数
    maxErrorRate: 0.001          # 最大错误率
  resources:
    maxCpuUsage: 0.90            # 最大 CPU 使用率
    maxMemoryUsage: 1073741824   # 最大内存 (1GB)
```

---

## 🚀 MVP 简化方案

### MVP 目标

快速构建**最小可行产品**，在 1-2 周内打通核心流程，验证技术方案的可行性。

### MVP 范围

| 维度 | MVP 方案 | 完整版 |
|------|---------|--------|
| **场景数量** | 2 个（CPU 密集型 + I/O 密集型） | 6 个 |
| **线程策略** | 2-3 种（平台线程 + 虚拟线程 + 可选线程池） | 6 种 |
| **开发周期** | 1-2 周 | 6-8 周 |
| **代码规模** | ~500 行 | ~3000 行 |
| **核心目标** | 验证框架设计，跑通基准测试流程 | 全面性能对比分析 |

### MVP 技术方案

```
┌─────────────────────────────────────────────────────────┐
│                    MVP 测试工具架构                      │
├─────────────────────────────────────────────────────────┤
│  场景层                                                  │
│  ├── CpuBoundScenario (CPU 密集型：矩阵计算/素数筛选)    │
│  └── IoBoundScenario (I/O 密集型：文件读写/网络模拟)     │
├─────────────────────────────────────────────────────────┤
│  策略层                                                  │
│  ├── PlatformThreadStrategy (平台线程)                  │
│  ├── VirtualThreadStrategy (虚拟线程)                   │
│  └── ThreadPoolStrategy (线程池 - 可选)                 │
├─────────────────────────────────────────────────────────┤
│  框架层                                                  │
│  ├── BenchmarkRunner (运行器)                           │
│  ├── MetricCollector (指标收集：吞吐量/延迟)             │
│  └── ConsoleReporter (控制台报告)                        │
└─────────────────────────────────────────────────────────┘
```

### MVP 交付物

- [ ] 可运行的基准测试框架
- [ ] 2 个测试场景实现
- [ ] 2-3 种线程策略实现
- [ ] 控制台输出测试结果
- [ ] 简单的性能对比报告

### 从 MVP 到完整版的演进路径

```
MVP (1-2 周)
    │
    ▼
┌───────────────────┐
│ 核心框架打通      │
│ 2 场景 + 3 策略     │
│ 基础指标收集      │
└─────────┬─────────┘
          │
          ▼
Phase 2 (3-4 周)
    │
    ▼
┌───────────────────┐
│ 扩展场景          │
│ +4 个测试场景      │
│ +3 种线程策略      │
│ 完善指标体系      │
└─────────┬─────────┘
          │
          ▼
Phase 3 (5-6 周)
    │
    ▼
┌───────────────────┐
│ 报告系统          │
│ Markdown/HTML 报告 │
│ 可视化图表        │
│ 历史数据对比      │
└─────────┬─────────┘
          │
          ▼
Phase 4 (7-8 周)
    │
    ▼
┌───────────────────┐
│ 完善与优化        │
│ 性能回归检测      │
│ CI/CD 集成        │
│ 文档完善          │
└───────────────────┘
```

### MVP 优先级

| 优先级 | 任务 | 说明 |
|:------:|------|------|
| P0 | 基准测试框架核心 | Benchmark/Scenario/Strategy 接口 |
| P0 | 平台线程策略 | 最基础的实现 |
| P0 | 虚拟线程策略 | Java 21 核心特性 |
| P1 | CPU 密集型场景 | 计算密集型测试 |
| P1 | I/O 密集型场景 | I/O 密集型测试 |
| P2 | 线程池策略 | 可选增强 |
| P2 | 控制台报告 | 基础输出 |
| P3 | Markdown 报告 | 格式美化 |

---

## 🔧 开发工作流

本项目采用 **SDD (Specification-Driven Development)** 工作流：

```
┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐
│  规范   │ → │  规划   │ → │  任务   │ → │  实现   │ → │  审查   │ → │  验证   │
│  Spec   │   │  Plan   │   │  Tasks  │   │  Build  │   │  Review │   │Validate │
└─────────┘   └─────────┘   └─────────┘   └─────────┘   └─────────┘   └─────────┘
   阶段 1         阶段 2         阶段 3         阶段 4         阶段 5         阶段 6
```

### 快速开始

```bash
# 1. 开始新 feature
@sdd 开始 [功能名称]

# 2. 查看当前进度
@sdd 状态

# 3. 继续当前工作
@sdd 继续

# 4. 跳转到特定阶段
@sdd spec [feature]    # 规范编写
@sdd plan [feature]    # 技术规划
@sdd tasks [feature]   # 任务分解
@sdd build [feature]   # 任务实现
@sdd review [feature]  # 代码审查
@sdd validate [feature]# 最终验证
```

### 阶段说明

| 阶段 | 命令 | 输出文件 | 说明 |
|------|------|----------|------|
| 1. 规范编写 | `@sdd-spec` | `.specs/[feature]/spec.md` | 定义功能需求和验收标准 |
| 2. 技术规划 | `@sdd-plan` | `.specs/[feature]/plan.md` | 设计技术方案和架构 |
| 3. 任务分解 | `@sdd-tasks` | `.specs/[feature]/tasks.md` | 分解为可执行任务 |
| 4. 任务实现 | `@sdd-build` | `src/` 代码、测试 | 编写代码实现 |
| 5. 代码审查 | `@sdd-review` | `review-report.md` | 审查代码质量 |
| 6. 最终验证 | `@sdd-validate` | `validation-report.md` | 验证是否符合规范 |

### 与工程矩阵的映射

```
SDD 阶段          输出                    对应工程模块
─────────────────────────────────────────────────────────────
spec             spec.md                 docs/scenarios/
plan             plan.md                 docs/implementation/
tasks            tasks.md                .specs/[feature]/
build            src/scenarios/*         src/scenarios/
build            src/threads/*           src/threads/
build            src/benchmark/*         src/benchmark/
review           review-report.md        docs/reviews/
validate         validation-report.md    results/reports/
```

---

## 📈 当前状态

### 项目阶段

> **当前阶段**: 🎯 MVP 开发中（目标：1-2 周完成）

### 已完成 ✅
- [x] 项目初始化
- [x] SDD 工作流配置
- [x] 项目结构设计
- [x] 工程结构矩阵定义
- [x] 验证方案设计
- [x] README 文档更新
- [x] MVP 方案规划
- [x] 基准测试脚本 (`scripts/run-benchmark.sh`)
- [x] 基准测试配置文件 (`config/benchmark/benchmark.yaml`)
- [x] BenchmarkConfig YAML 加载支持
- [x] Markdown 报告器 (`MarkdownReporter`)
- [x] HTML 报告器 (`HtmlReporter`)
- [x] JSON 报告器 (`JsonReporter`)
- [x] 控制台报告器 (`ConsoleReporter`)

### 进行中 🔄
- [x] MVP 核心框架搭建
  - [x] Benchmark 接口定义
  - [x] Scenario 接口定义
  - [x] ThreadStrategy 接口定义
  - [x] BenchmarkRunner 实现
- [x] 测试场景实现
  - [x] CPU 密集型场景 (`CpuBoundScenario`)
  - [x] I/O 密集型场景 (`IoBoundScenario`)
  - [x] 高并发短任务场景 (`HighConcurrencyScenario`)
  - [x] 混合负载场景 (`MixedLoadScenario`)
  - [x] 递归分治场景 (`RecursiveScenario`)

### 计划中 📋

#### MVP 阶段（P0/P1 优先级）
- [x] 平台线程基准实现 (`PlatformThreadStrategy`)
- [x] 虚拟线程基准实现 (`VirtualThreadStrategy`)
- [x] CPU 密集型场景实现
- [x] I/O 密集型场景实现
- [x] 基础指标收集（吞吐量/延迟）
- [x] 控制台报告输出
- [x] Markdown 报告输出

#### Phase 2 扩展
- [x] 线程池策略实现 (`ThreadPoolStrategy`)
- [x] 高并发短任务场景 (`HighConcurrencyScenario`)
- [x] 混合负载场景 (`MixedLoadScenario`)
- [x] 递归分治场景 (`RecursiveScenario`)

#### Phase 3 报告系统
- [x] Markdown 报告生成
- [x] HTML 报告生成
- [x] JSON 报告生成
- [ ] 可视化图表

#### Phase 4 完善
- [ ] 性能回归检测
- [ ] CI/CD 集成
- [ ] 完整文档
- [ ] Fork/Join 策略
- [ ] CompletableFuture 策略
- [ ] Reactive Streams 策略

---

## 🛠️ 技术栈

| 类别 | 技术 | 版本 | 用途 |
|------|------|------|------|
| **语言** | Java | 21+ | 核心开发 |
| **构建工具** | Maven | 3.9+ | 项目构建 |
| **基准测试** | JMH | 1.37+ | 性能测试 |
| **单元测试** | JUnit 5 | 5.10+ | 功能测试 |
| **断言库** | AssertJ | 3.24+ | 流式断言 |
| **监控** | JFR | Built-in | 性能监控 |
| **覆盖率** | JaCoCo | 0.8.11+ | 代码覆盖 |
| **工作流** | SDD (OpenCode) | Latest | 开发流程 |
| **CI/CD** | GitHub Actions | - | 持续集成 |

---

## 📚 使用指南

### 添加新场景

1. 在 `src/scenarios/` 创建新目录
2. 实现 `Scenario` 接口
3. 编写测试用例
4. 更新 README 矩阵
5. 运行验证

```java
// 示例：创建新场景
public class MyScenario implements Scenario {
    @Override
    public String getName() {
        return "my-scenario";
    }
    
    @Override
    public List<Task> getTasks() {
        // 定义任务
    }
}
```

### 添加新线程策略

1. 在 `src/threads/` 创建新目录
2. 实现 `ThreadStrategy` 接口
3. 配置参数
4. 编写测试用例
5. 运行基准测试

```java
// 示例：创建新策略
public class MyThreadStrategy implements ThreadStrategy {
    @Override
    public ExecutorService createExecutor() {
        // 创建执行器
    }
}
```

### 运行基准测试

```bash
# 运行所有测试（使用默认配置）
./scripts/run-benchmark.sh

# 运行特定场景和策略
./scripts/run-benchmark.sh --scenario cpu-bound --strategy virtual

# 使用自定义配置文件
./scripts/run-benchmark.sh --config config/benchmark/benchmark.yaml

# 指定输出格式（支持多种格式）
./scripts/run-benchmark.sh --output console,markdown,html,json

# 查看完整帮助信息
./scripts/run-benchmark.sh --help

# 也可以使用 sh 运行（POSIX 兼容）
sh scripts/run-benchmark.sh --help
```

### 配置文件

基准测试配置文件位于 `config/benchmark/benchmark.yaml`，支持以下配置项：

```yaml
benchmark:
  warmupIterations: 3        # 预热轮次
  benchmarkIterations: 5     # 正式测试轮次
  taskCount: 1000            # 每轮任务数
  timeoutMillis: 60000       # 超时时间 (毫秒)
  verbose: true              # 详细输出

scenarios:
  - cpu-bound                # CPU 密集型
  - io-bound                 # I/O 密集型

strategies:
  - platform                 # 平台线程
  - virtual                  # 虚拟线程

reporters:
  - console                  # 控制台输出
  - markdown                 # Markdown 报告
  - html                     # HTML 报告
  - json                     # JSON 报告
```

### 生成报告

测试完成后，报告会自动生成到 `results/reports/` 目录：

- **Markdown**: `benchmark-report-YYYYMMDD-HHmmss.md`
- **HTML**: `benchmark-report-YYYYMMDD-HHmmss.html`
- **JSON**: `benchmark-report-YYYYMMDD-HHmmss.json`

### 运行验证

#### 使用测试脚本（推荐）

本项目提供便捷的测试脚本，支持增量测试、完整测试和覆盖率报告：

```bash
# 增量测试（默认，只测试 Phase 2 变更的代码）
./scripts/test-phase2.sh

# 完整测试套件
./scripts/test-phase2.sh -full

# 生成覆盖率报告
./scripts/test-phase2.sh -coverage

# 组合使用
./scripts/test-phase2.sh -full -coverage

# 监听模式（文件变更自动测试）
./scripts/test-phase2.sh -watch

# 查看详细帮助
./scripts/test-phase2.sh --help

# 注意：test-phase2.sh 需要 bash 环境
bash scripts/test-phase2.sh --help
```

**参数说明**:
| 参数 | 长参数 | 说明 |
|------|--------|------|
| `-f` | `--full` | 运行完整测试套件 |
| `-i` | `--incremental` | 只测试 Phase 2 新增/修改的类 (默认) |
| `-c` | `--coverage` | 生成覆盖率报告 |
| `-r` | `--report` | 生成详细测试报告 |
| `-w` | `--watch` | 监听模式，文件变更自动测试 |
| `-v` | `--verbose` | 显示详细输出 |
| `-h` | `--help` | 显示帮助信息 |

**Phase 2 测试类**:
- `ThreadPoolStrategyTest` - 线程池策略测试（19 个测试）
- `HighConcurrencyScenarioTest` - 高并发场景测试（23 个测试）
- `MixedLoadScenarioTest` - 混合负载场景测试（22 个测试）
- `RecursiveScenarioTest` - 递归分治场景测试（22 个测试）

#### 使用 Maven 命令

```bash
# 运行所有单元测试
mvn test

# 运行特定测试类
mvn test -Dtest=BenchmarkConfigTest

# 运行 Phase 2 测试
mvn test -Dtest=ThreadPoolStrategyTest,HighConcurrencyScenarioTest,MixedLoadScenarioTest,RecursiveScenarioTest

# 查看代码覆盖率
mvn test jacoco:report

# 覆盖率报告位置
# HTML: target/site/jacoco/index.html
# XML:  target/site/jacoco/jacoco.xml
```

---

## 📖 相关资源

### Java 并发
- [Java Virtual Threads (JEP 444)](https://openjdk.org/jeps/444)
- [Java Concurrency in Practice](https://jcip.net/)
- [Loom Project](https://wiki.openjdk.org/display/loom/Main)

### 基准测试
- [JMH Documentation](https://openjdk.org/projects/code-tools/jmh/)
- [JFR Documentation](https://docs.oracle.com/en/java/javase/21/docs/specs/jfr/)

### SDD 工作流
- [OpenCode 文档](https://opencode.ai/docs/)
- [SDD 安装指南](SDD_INSTALL_GUIDE.md)

---

## 🤝 贡献指南

本项目采用 SDD 工作流进行开发，贡献前请确保熟悉 SDD 流程：

1. 阅读 `SDD_INSTALL_GUIDE.md`
2. 在 OpenCode 中运行 `@sdd 帮助` 查看完整命令
3. 按照**规范编写→技术规划→任务分解→实现**的流程进行开发
4. 确保代码通过所有验证检查

### 贡献步骤

```
1. Fork 项目
2. 创建 Feature 分支 (git checkout -b feature/amazing-feature)
3. 使用 SDD 流程开发
4. 运行验证 (./scripts/validate.sh)
5. 提交变更 (git commit -m 'Add amazing feature')
6. 推送到远程 (git push origin feature/amazing-feature)
7. 创建 Pull Request
```

---

## 📄 许可证

MIT License - 详见 [LICENSE](LICENSE)

---

<div align="center">

**📍 快速导航**: [工程矩阵](#-工程结构矩阵) | [目录结构](#-目录结构) | [验证方案](#-验证方案) | [开发工作流](#-开发工作流)

*最后更新：2026-03-27*

</div>
