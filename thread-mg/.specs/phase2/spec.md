# Phase 2: 扩展阶段规范文档

> 💡 **提示**: 本文档定义 Phase 2 的功能需求和技术规范

---

## 📋 概述

Phase 2 是 thread-mg 项目的扩展阶段，目标是实现更多线程策略和测试场景，完善基准测试框架。

### 阶段目标
- ✅ 实现 `ThreadPoolStrategy` 线程池策略
- ✅ 实现高并发短任务场景
- ✅ 实现混合负载场景
- ✅ 实现递归分治场景
- ✅ 完善单元测试覆盖率

### 完成度追踪
- 起始完成度：40%
- 目标完成度：100%

---

## 🏗️ 架构设计

### 组件关系图

```
┌─────────────────────────────────────────────────────────────┐
│                    Phase 2 新增组件                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────────┐    ┌─────────────────────────────┐   │
│  │ ThreadPoolStrategy│    │      新增场景类             │   │
│  │ (线程池策略)      │    │                             │   │
│  │                  │    │  ┌─────────────────────┐   │   │
│  │ - corePoolSize   │    │  │ HighConcurrencyScn  │   │   │
│  │ - maxPoolSize    │    │  │ (高并发短任务)      │   │   │
│  │ - queueCapacity  │    │  └─────────────────────┘   │   │
│  │ - rejectPolicy   │    │                             │   │
│  └────────┬─────────┘    │  ┌─────────────────────┐   │   │
│           │              │  │ MixedLoadScenario   │   │   │
│           │ 实现          │  │ (混合负载)          │   │   │
│           │              │  └─────────────────────┘   │   │
│           ▼              │                             │   │
│  ┌──────────────────┐    │  ┌─────────────────────┐   │   │
│  │ ThreadStrategy   │    │  │ RecursiveScenario   │   │   │
│  │ (策略接口)        │    │  │ (递归分治)          │   │   │
│  └──────────────────┘    │  └─────────────────────┘   │   │
│                          │                             │   │
└──────────────────────────┴─────────────────────────────┘   │
                                                             │
┌─────────────────────────────────────────────────────────────┘
                        │
                        ▼
        ┌───────────────────────────────┐
        │       核心接口/类              │
        │                               │
        │  ThreadStrategy (接口)        │
        │  Scenario (接口)              │
        │  ScenarioResult (结果类)      │
        │  BenchmarkRunner (运行器)     │
        └───────────────────────────────┘
```

---

## 1️⃣ ThreadPoolStrategy 实现规范

### 1.1 类设计

```java
/**
 * 线程池策略
 * 使用 ExecutorService 和 ThreadPoolExecutor 实现可配置的线程池
 */
public class ThreadPoolStrategy implements ThreadStrategy {
    // 核心线程数
    private final int corePoolSize;
    // 最大线程数
    private final int maxPoolSize;
    // 空闲线程存活时间 (秒)
    private final long keepAliveTime;
    // 任务队列容量
    private final int queueCapacity;
    // 拒绝策略
    private final RejectedExecutionHandler rejectPolicy;
    // 线程工厂
    private final ThreadFactory threadFactory;
}
```

### 1.2 配置参数

| 参数 | 默认值 | 说明 |
|------|--------|------|
| corePoolSize | CPU 核心数 | 核心线程数 |
| maxPoolSize | CPU 核心数 × 2 | 最大线程数 |
| keepAliveTime | 60 秒 | 空闲线程存活时间 |
| queueCapacity | 1000 | 任务队列容量 |
| rejectPolicy | CallerRuns | 拒绝策略 |

### 1.3 拒绝策略支持

- `AbortPolicy`: 抛出 RejectedExecutionException
- `CallerRunsPolicy`: 由调用线程执行
- `DiscardPolicy`: 直接丢弃
- `DiscardOldestPolicy`: 丢弃最老任务

### 1.4 方法要求

```java
public interface ThreadStrategy {
    String getName();
    String getDescription();
    ExecutorService createExecutor();
    void shutdown();
    int getDefaultThreadCount();
}
```

### 1.5 实现要求

- ✅ 支持无参构造函数（使用默认配置）
- ✅ 支持带参构造函数（自定义配置）
- ✅ 使用 builder 模式支持链式配置
- ✅ 完整的 JavaDoc 注释
- ✅ 资源正确释放

---

## 2️⃣ HighConcurrencyScenario 实现规范

### 2.1 类设计

```java
/**
 * 高并发短任务场景
 * 模拟大量短生命周期的任务 (如 HTTP 请求、数据库查询)
 */
public class HighConcurrencyScenario implements Scenario {
    // 任务数量
    private final int taskCount;
    // 任务执行时间范围 (毫秒)
    private final int minExecutionTimeMs;
    private final int maxExecutionTimeMs;
    // 是否模拟 I/O 等待
    private final boolean simulateIoWait;
}
```

### 2.2 配置参数

| 参数 | 默认值 | 说明 |
|------|--------|------|
| taskCount | 10000 | 任务总数 |
| minExecutionTimeMs | 1 | 最小执行时间 |
| maxExecutionTimeMs | 10 | 最大执行时间 |
| simulateIoWait | true | 是否模拟 I/O |

### 2.3 任务特点

- ✅ 执行时间短 (<10ms)
- ✅ 并发量高 (10000+ 任务)
- ✅ 无状态任务
- ✅ 可并行执行

### 2.4 实现要求

- ✅ 使用随机执行时间模拟真实场景
- ✅ 支持配置任务参数
- ✅ 记录每个任务的延迟
- ✅ 完整的单元测试

---

## 3️⃣ MixedLoadScenario 实现规范

### 3.1 类设计

```java
/**
 * 混合负载场景
 * 混合 CPU 密集型和 I/O 密集型任务
 */
public class MixedLoadScenario implements Scenario {
    // CPU 密集型任务比例 (0.0 - 1.0)
    private final double cpuRatio;
    // I/O 密集型任务比例 (0.0 - 1.0)
    private final double ioRatio;
    // 任务总数
    private final int totalTasks;
    // 矩阵大小 (CPU 任务用)
    private final int matrixSize;
    // I/O 延迟 (毫秒)
    private final int ioDelayMs;
}
```

### 3.2 配置参数

| 参数 | 默认值 | 说明 |
|------|--------|------|
| cpuRatio | 0.7 | CPU 任务占比 |
| ioRatio | 0.3 | I/O 任务占比 |
| totalTasks | 1000 | 任务总数 |
| matrixSize | 50 | 矩阵大小 |
| ioDelayMs | 5 | I/O 延迟 |

### 3.3 实现要求

- ✅ 可配置混合比例
- ✅ CPU 任务使用矩阵计算
- ✅ I/O 任务使用 Thread.sleep 模拟
- ✅ 任务随机分配
- ✅ 完整的单元测试

---

## 4️⃣ RecursiveScenario 实现规范

### 4.1 类设计

```java
/**
 * 递归分治场景
 * 实现递归分治算法测试
 */
public class RecursiveScenario implements Scenario {
    // 递归算法类型
    private final AlgorithmType algorithmType;
    // 输入数据规模
    private final int dataSize;
    // 并行阈值
    private final int threshold;
}

public enum AlgorithmType {
    QUICK_SORT,      // 快速排序
    MERGE_SORT,      // 归并排序
    FIBONACCI,       // 斐波那契
    MATRIX_MULTIPLY  // 矩阵乘法 (Strassen)
}
```

### 4.2 配置参数

| 参数 | 默认值 | 说明 |
|------|--------|------|
| algorithmType | QUICK_SORT | 算法类型 |
| dataSize | 10000 | 数据规模 |
| threshold | 1000 | 并行阈值 |

### 4.3 实现要求

- ✅ 使用 Fork/Join 框架
- ✅ 支持多种递归算法
- ✅ 验证结果正确性
- ✅ 完整的单元测试

---

## 📁 文件结构

### 新增文件列表

```
src/main/java/com/threadmg/
├── threads/
│   └── pool/
│       ├── ThreadPoolStrategy.java          # 新增
│       └── ThreadPoolStrategyBuilder.java   # 新增 (可选)
└── scenarios/
    ├── concurrency/
    │   └── HighConcurrencyScenario.java     # 新增
    ├── mixed/
    │   └── MixedLoadScenario.java           # 新增
    └── recursive/
        └── RecursiveScenario.java           # 新增

src/test/java/com/threadmg/
├── threads/
│   └── pool/
│       └── ThreadPoolStrategyTest.java      # 新增
└── scenarios/
    ├── concurrency/
    │   └── HighConcurrencyScenarioTest.java # 新增
    ├── mixed/
    │   └── MixedLoadScenarioTest.java       # 新增
    └── recursive/
        └── RecursiveScenarioTest.java       # 新增

config/benchmark/
└── benchmark.yaml                           # 更新

docs/
├── scenarios/
│   └── README.md                            # 更新
├── threads/
│   └── README.md                            # 更新
└── validation/
    └── PHASE2_REPORT.md                     # 新增
```

---

## 🧪 测试要求

### 单元测试覆盖

| 类 | 最低覆盖率 | 必测方法 |
|-----|-----------|----------|
| ThreadPoolStrategy | 90% | 构造方法、createExecutor、shutdown |
| HighConcurrencyScenario | 85% | execute、verify |
| MixedLoadScenario | 85% | execute、verify |
| RecursiveScenario | 85% | execute、verify |

### 集成测试

- ✅ 所有策略与所有场景的组合测试
- ✅ 边界条件测试
- ✅ 并发安全性测试
- ✅ 资源释放测试

---

## 📝 配置更新

### benchmark.yaml 更新

```yaml
scenarios:
  - cpu-bound
  - io-bound
  - high-concurrency    # 新增
  - mixed-load          # 新增
  - recursive           # 新增

strategies:
  - platform
  - virtual
  - pool                # 新增
```

---

## ✅ 验收标准

### 代码质量
- [ ] 所有新类有完整的 JavaDoc
- [ ] 遵循现有代码风格
- [ ] 使用 Java 21+ 特性（如适用）
- [ ] 无编译警告

### 测试覆盖
- [ ] 所有单元测试通过
- [ ] 代码覆盖率 > 80%
- [ ] 无测试失败
- [ ] 无回归问题

### 文档完整性
- [ ] 场景文档已更新
- [ ] 线程策略文档已更新
- [ ] README.md 功能清单已更新

### 功能验证
- [ ] ThreadPoolStrategy 正常工作
- [ ] 所有场景可执行
- [ ] 基准测试生成报告
- [ ] 性能指标合理

---

*文档版本：1.0 | 创建日期：2026-03-27*
