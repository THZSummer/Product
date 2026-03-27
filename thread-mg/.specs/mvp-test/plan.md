# MVP 基准测试框架技术规划

## 元数据

| 字段 | 值 |
|------|-----|
| **Feature 名称** | MVP 基准测试框架 |
| **标识符** | PLAN-MVP-001 |
| **创建日期** | 2026-03-27 |
| **作者** | SDD Plan Agent |
| **关联规范** | `.specs/mvp-test/spec.md` |
| **状态** | proposed |
| **优先级** | P0 |

---

## 1. 架构分析

### 1.1 现有架构影响

根据 README.md 定义的项目结构，MVP 阶段需要建立以下核心模块：

```
src/main/java/com/threadmg/
├── benchmark/                    # 基准测试框架核心
│   ├── core/                     # 核心接口和运行器
│   ├── metrics/                  # 指标收集
│   ├── reporters/                # 报告生成
│   └── config/                   # 配置管理
├── scenarios/                    # 测试场景实现
│   ├── cpu-bound/                # CPU 密集型场景
│   └── io-bound/                 # I/O 密集型场景
└── threads/                      # 线程策略实现
    ├── platform/                 # 平台线程策略
    └── virtual/                  # 虚拟线程策略
```

**架构约束**：
- 必须遵循已定义的目录结构
- 核心接口必须支持后续扩展（Phase 2-4）
- 不引入 JMH，使用简单计时器实现

### 1.2 新组件识别

MVP 阶段需要创建的新组件：

| 组件类型 | 组件名称 | 说明 |
|---------|---------|------|
| **核心接口** | `Scenario` | 测试场景接口 |
| **核心接口** | `ThreadStrategy` | 线程策略接口 |
| **核心类** | `BenchmarkRunner` | 基准测试运行器 |
| **核心类** | `BenchmarkResult` | 测试结果对象 |
| **核心类** | `ScenarioResult` | 场景执行结果 |
| **指标收集** | `MetricCollector` | 性能指标收集器 |
| **配置管理** | `BenchmarkConfig` | 测试配置类 |
| **场景实现** | `CpuBoundScenario` | CPU 密集型场景 |
| **场景实现** | `IoBoundScenario` | I/O 密集型场景 |
| **策略实现** | `PlatformThreadStrategy` | 平台线程策略 |
| **策略实现** | `VirtualThreadStrategy` | 虚拟线程策略 |
| **报告生成** | `ConsoleReporter` | 控制台报告 |
| **报告生成** | `MarkdownReporter` | Markdown 报告 |
| **入口类** | `BenchmarkApp` | 主程序入口 |

### 1.3 数据流分析

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐
│  Benchmark  │────▶│ Benchmark    │────▶│  Scenario   │
│     App     │     │    Runner    │     │  (CPU/IO)   │
└─────────────┘     └──────┬───────┘     └──────┬──────┘
                           │                    │
                           ▼                    ▼
                    ┌──────────────┐     ┌─────────────┐
                    │  Thread      │────▶│  Executor   │
                    │  Strategy    │     │   Service   │
                    └──────┬───────┘     └──────┬──────┘
                           │                    │
                           ▼                    ▼
                    ┌──────────────┐     ┌─────────────┐
                    │   Metric     │◀────│  Task       │
                    │  Collector   │     │  Execution  │
                    └──────┬───────┘     └─────────────┘
                           │
                           ▼
                    ┌──────────────┐
                    │  Benchmark   │
                    │    Result    │
                    └──────┬───────┘
                           │
                           ▼
                    ┌──────────────┐
                    │   Reporter   │
                    │ (Console/MD) │
                    └──────────────┘
```

### 1.4 依赖关系图

```
                    ┌─────────────────┐
                    │   BenchmarkApp  │
                    └────────┬────────┘
                             │ uses
              ┌──────────────┼──────────────┐
              ▼              ▼              ▼
     ┌────────────┐ ┌──────────────┐ ┌─────────────┐
     │Benchmark   │ │Benchmark     │ │Benchmark    │
     │Config      │ │Runner        │ │Result       │
     └────────────┘ └──────┬───────┘ └─────────────┘
                           │ orchestrates
              ┌────────────┼────────────┐
              ▼            ▼            ▼
     ┌────────────┐ ┌────────────┐ ┌─────────────┐
     │ Scenario   │ │Thread      │ │Metric       │
     │ (interface)│ │Strategy    │ │Collector    │
     └─────┬──────┘ │(interface) │ └─────────────┘
           │        └─────┬──────┘
     ┌─────┴──────┐ ┌─────┴──────────┐
     ▼            ▼ ▼                ▼
┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│CpuBound  │ │IoBound   │ │Platform  │ │Virtual   │
│Scenario  │ │Scenario  │ │Thread    │ │Thread    │
└──────────┘ └──────────┘ │Strategy  │ │Strategy  │
                          └──────────┘ └──────────┘
```

---

## 2. 技术方案对比

### 方案 A：简单计时器 + 手动指标收集（推荐）

**描述**：使用 `System.nanoTime()` 进行计时，手动计算吞吐量和延迟百分位，不依赖外部基准测试框架。

**优点**：
- ✅ 零外部依赖，实现简单
- ✅ 完全控制指标收集逻辑
- ✅ 符合 MVP 快速交付目标
- ✅ 代码透明，易于理解和调试
- ✅ 框架开销可控（< 5%）

**缺点**：
- ❌ 缺少 JMH 的优化（如 dead code elimination 防护）
- ❌ 需要手动处理预热逻辑
- ❌ 统计计算需要自己实现

**风险评估**：
- 技术风险：低（Java 基础 API）
- 精度风险：中（需注意计时精度和 GC 影响）
- 时间风险：低（估计 3-4 天完成）

**预估工作量**：3-4 天

---

### 方案 B：集成 JMH 微基准测试框架

**描述**：使用 OpenJDK 官方的 JMH 框架进行基准测试，利用其成熟的优化和统计功能。

**优点**：
- ✅ 业界标准，结果可信度高
- ✅ 自动处理预热、优化防护
- ✅ 丰富的统计指标和输出格式
- ✅ 便于后续与专业基准对比

**缺点**：
- ❌ 学习曲线陡峭
- ❌ 配置复杂，不够灵活
- ❌ 不符合 MVP 简化目标
- ❌ 需要额外依赖和构建配置

**风险评估**：
- 技术风险：中（JMH 使用复杂）
- 时间风险：高（估计 7-10 天完成）
- 范围蔓延风险：高（容易过度工程化）

**预估工作量**：7-10 天

---

### 方案 C：混合方案（简单计时器 + JFR 监控）

**描述**：使用简单计时器进行基准测试，同时启用 JFR（Java Flight Recorder）进行系统级监控。

**优点**：
- ✅ 平衡简单性和专业性
- ✅ JFR 提供系统级洞察（CPU、内存、线程）
- ✅ JFR 是 JDK 内置工具，无额外依赖
- ✅ 便于后续性能分析

**缺点**：
- ❌ JFR 配置和学习需要时间
- ❌ JFR 数据解析复杂
- ❌ MVP 阶段可能过度设计

**风险评估**：
- 技术风险：中（JFR API 复杂）
- 时间风险：中（估计 5-6 天完成）

**预估工作量**：5-6 天

---

## 3. 推荐方案

**推荐方案 A：简单计时器 + 手动指标收集**

**理由**：
1. **符合 MVP 目标**：1-2 周交付，方案 A 工作量最小
2. **技术简单**：仅使用 Java 标准库，降低学习成本
3. **可控性强**：完全掌握代码逻辑，便于调试和优化
4. **可扩展**：后续 Phase 2 可无缝升级到 JMH 或混合方案
5. **规范对齐**：spec.md 明确说明"不使用 JMH 微基准测试框架"

**技术栈决策**：

| 决策点 | 选择 | 理由 |
|-------|------|------|
| **计时方式** | `System.nanoTime()` | 纳秒精度，适合短时任务 |
| **线程管理** | `ExecutorService` | 标准并发 API，支持虚拟线程 |
| **统计计算** | 手动实现百分位 | 简单场景无需复杂库 |
| **数据结构** | `ArrayList<Long>` + 排序 | 延迟样本收集 |
| **并发安全** | 每个测试单线程执行 | 简化设计，避免锁竞争 |
| **配置管理** | 代码内默认值 + Builder | MVP 无需外部配置文件 |

---

## 4. 核心接口设计

### 4.1 Scenario 接口

```java
package com.threadmg.benchmark.core;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 测试场景接口
 * 定义基准测试的执行场景
 */
public interface Scenario {
    
    /**
     * 获取场景名称
     * @return 场景标识符（如 "cpu-bound", "io-bound"）
     */
    String getName();
    
    /**
     * 获取场景描述
     * @return 人类可读的场景描述
     */
    String getDescription();
    
    /**
     * 创建测试任务列表
     * @param count 任务数量
     * @return 可执行的任务列表
     */
    List<Runnable> createTasks(int count);
    
    /**
     * 执行场景测试
     * @param executor 执行器
     * @param taskCount 任务数量
     * @return 场景执行结果
     */
    ScenarioResult execute(ExecutorService executor, int taskCount);
    
    /**
     * 验证执行结果正确性
     * @param result 执行结果
     * @return 是否通过验证
     */
    boolean verify(ScenarioResult result);
}
```

### 4.2 ThreadStrategy 接口

```java
package com.threadmg.benchmark.core;

import java.util.concurrent.ExecutorService;

/**
 * 线程策略接口
 * 定义不同线程技术的执行策略
 */
public interface ThreadStrategy {
    
    /**
     * 获取策略名称
     * @return 策略标识符（如 "platform", "virtual"）
     */
    String getName();
    
    /**
     * 获取策略描述
     * @return 人类可读的策略描述
     */
    String getDescription();
    
    /**
     * 创建执行器
     * @return 配置好的执行器服务
     */
    ExecutorService createExecutor();
    
    /**
     * 关闭执行器，释放资源
     */
    void shutdown();
    
    /**
     * 获取默认线程数/并发度
     * @return 默认并发配置
     */
    int getDefaultThreadCount();
}
```

### 4.3 数据模型

#### ScenarioResult

```java
package com.threadmg.benchmark.core;

import java.time.Instant;
import java.util.List;

/**
 * 场景执行结果
 */
public class ScenarioResult {
    private final String scenarioName;
    private final int totalTasks;
    private final int completedTasks;
    private final long durationNanos;
    private final List<Long> taskLatenciesNanos; // 每个任务的延迟
    private final boolean success;
    private final String errorMessage;
    private final Instant timestamp;
    
    // 构造函数、getter、计算方法...
    
    public double getThroughput() {
        return (double) completedTasks / (durationNanos / 1_000_000_000.0);
    }
    
    public double getAverageLatencyMs() {
        return taskLatenciesNanos.stream()
            .mapToDouble(l -> l / 1_000_000.0)
            .average()
            .orElse(0.0);
    }
    
    public double getPercentile(double p) {
        // 计算百分位延迟
    }
}
```

#### BenchmarkResult

```java
package com.threadmg.benchmark.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 基准测试完整结果
 * 包含多轮迭代的聚合统计
 */
public class BenchmarkResult {
    private final String scenarioName;
    private final String strategyName;
    private final int warmupIterations;
    private final int benchmarkIterations;
    private final List<ScenarioResult> iterationResults;
    private final Instant timestamp;
    
    // 聚合统计方法
    public double getAverageThroughput() { }
    public double getP50Latency() { }
    public double getP95Latency() { }
    public double getP99Latency() { }
    public double getStandardDeviation() { }
}
```

#### BenchmarkConfig

```java
package com.threadmg.benchmark.config;

/**
 * 基准测试配置
 * 使用 Builder 模式构建
 */
public class BenchmarkConfig {
    private final int warmupIterations;
    private final int benchmarkIterations;
    private final int taskCount;
    private final long timeoutMillis;
    private final boolean verbose;
    
    // 默认配置
    public static BenchmarkConfig defaultConfig() {
        return new Builder()
            .warmupIterations(3)
            .benchmarkIterations(5)
            .taskCount(1000)
            .timeoutMillis(60000)
            .verbose(true)
            .build();
    }
    
    // Builder 内部类...
}
```

---

## 5. 实现类设计

### 5.1 CpuBoundScenario

**任务设计**：矩阵乘法计算
- 创建两个 100x100 的随机矩阵
- 执行矩阵乘法
- 验证结果正确性

```java
public class CpuBoundScenario implements Scenario {
    private final int matrixSize;
    private final int taskCount;
    
    @Override
    public List<Runnable> createTasks(int count) {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tasks.add(new MatrixMultiplicationTask(matrixSize));
        }
        return tasks;
    }
    
    private static class MatrixMultiplicationTask implements Runnable {
        private final int size;
        
        @Override
        public void run() {
            // 执行矩阵乘法
            double[][] a = createRandomMatrix(size);
            double[][] b = createRandomMatrix(size);
            double[][] result = multiply(a, b);
        }
    }
}
```

### 5.2 IoBoundScenario

**任务设计**：内存模拟 I/O 操作
- 使用 `ByteArrayOutputStream` 模拟写入
- 使用 `ByteArrayInputStream` 模拟读取
- 避免真实磁盘 I/O 的不确定性

```java
public class IoBoundScenario implements Scenario {
    private final int dataSize;
    
    @Override
    public List<Runnable> createTasks(int count) {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tasks.add(new SimulatedIoTask(dataSize));
        }
        return tasks;
    }
    
    private static class SimulatedIoTask implements Runnable {
        private final int size;
        
        @Override
        public void run() {
            // 模拟 I/O 操作
            byte[] data = new byte[size];
            new Random().nextBytes(data);
            
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
                baos.write(data);
                bais.read(new byte[size]);
            }
        }
    }
}
```

### 5.3 PlatformThreadStrategy

```java
public class PlatformThreadStrategy implements ThreadStrategy {
    private final int threadCount;
    private ExecutorService executor;
    
    @Override
    public ExecutorService createExecutor() {
        executor = Executors.newFixedThreadPool(threadCount);
        return executor;
    }
    
    @Override
    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
            // 等待终止...
        }
    }
}
```

### 5.4 VirtualThreadStrategy

```java
public class VirtualThreadStrategy implements ThreadStrategy {
    private ExecutorService executor;
    
    @Override
    public ExecutorService createExecutor() {
        // Java 21+ 虚拟线程
        executor = Executors.newVirtualThreadPerTaskExecutor();
        return executor;
    }
    
    @Override
    public int getDefaultThreadCount() {
        // 虚拟线程无限制，返回 Integer.MAX_VALUE 表示按需创建
        return Integer.MAX_VALUE;
    }
}
```

### 5.5 BenchmarkRunner

```java
public class BenchmarkRunner {
    private final BenchmarkConfig config;
    private final MetricCollector metricCollector;
    
    public BenchmarkResult run(Scenario scenario, ThreadStrategy strategy) {
        // 1. 预热阶段
        List<ScenarioResult> warmupResults = runIterations(
            scenario, strategy, config.getWarmupIterations(), true);
        
        // 2. 基准测试阶段
        List<ScenarioResult> benchmarkResults = runIterations(
            scenario, strategy, config.getBenchmarkIterations(), false);
        
        // 3. 收集指标
        BenchmarkResult result = aggregateResults(scenario, strategy, benchmarkResults);
        
        // 4. 清理资源
        strategy.shutdown();
        
        return result;
    }
    
    private List<ScenarioResult> runIterations(
            Scenario scenario, 
            ThreadStrategy strategy, 
            int iterations,
            boolean isWarmup) {
        List<ScenarioResult> results = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            ExecutorService executor = strategy.createExecutor();
            ScenarioResult result = scenario.execute(executor, config.getTaskCount());
            results.add(result);
            strategy.shutdown(); // 每轮后清理
        }
        return results;
    }
}
```

### 5.6 MetricCollector

```java
public class MetricCollector {
    
    public BenchmarkResult aggregate(
            String scenarioName,
            String strategyName,
            List<ScenarioResult> iterations) {
        
        // 计算聚合统计
        double avgThroughput = iterations.stream()
            .mapToDouble(ScenarioResult::getThroughput)
            .average()
            .orElse(0.0);
        
        // 收集所有延迟样本计算百分位
        List<Long> allLatencies = iterations.stream()
            .flatMap(r -> r.getTaskLatenciesNanos().stream())
            .collect(Collectors.toList());
        
        double p50 = calculatePercentile(allLatencies, 50);
        double p95 = calculatePercentile(allLatencies, 95);
        double p99 = calculatePercentile(allLatencies, 99);
        
        return new BenchmarkResult.Builder()
            .scenarioName(scenarioName)
            .strategyName(strategyName)
            .throughput(avgThroughput)
            .p50Latency(p50)
            .p95Latency(p95)
            .p99Latency(p99)
            .build();
    }
    
    private double calculatePercentile(List<Long> sortedData, double percentile) {
        // 实现百分位计算
    }
}
```

### 5.7 Reporters

#### ConsoleReporter

```java
public class ConsoleReporter {
    
    public void report(BenchmarkResult result) {
        System.out.println("========================================");
        System.out.println("Benchmark Result: " + result.getScenarioName());
        System.out.println("Strategy: " + result.getStrategyName());
        System.out.println("----------------------------------------");
        System.out.printf("Throughput: %.2f tasks/sec%n", result.getThroughput());
        System.out.printf("P50 Latency: %.2f ms%n", result.getP50Latency());
        System.out.printf("P95 Latency: %.2f ms%n", result.getP95Latency());
        System.out.printf("P99 Latency: %.2f ms%n", result.getP99Latency());
        System.out.println("========================================");
    }
}
```

#### MarkdownReporter

```java
public class MarkdownReporter {
    private final String outputDirectory;
    
    public void report(List<BenchmarkResult> results) {
        StringBuilder md = new StringBuilder();
        md.append("# 基准测试报告\n\n");
        md.append("生成时间：").append(LocalDateTime.now()).append("\n\n");
        
        // 对比表格
        md.append("## 性能对比\n\n");
        md.append("| 场景 | 策略 | 吞吐量 (tasks/s) | P50 (ms) | P95 (ms) | P99 (ms) |\n");
        md.append("|------|------|-----------------|----------|----------|----------|\n");
        
        for (BenchmarkResult r : results) {
            md.append(String.format("| %s | %s | %.2f | %.2f | %.2f | %.2f |\n",
                r.getScenarioName(), r.getStrategyName(),
                r.getThroughput(), r.getP50Latency(),
                r.getP95Latency(), r.getP99Latency()));
        }
        
        // 写入文件
        Path outputPath = Paths.get(outputDirectory, "benchmark-report.md");
        Files.writeString(outputPath, md.toString());
    }
}
```

---

## 6. 文件影响分析

### 6.1 需要创建的文件

```
[NEW] src/main/java/com/threadmg/benchmark/core/Scenario.java
[NEW] src/main/java/com/threadmg/benchmark/core/ThreadStrategy.java
[NEW] src/main/java/com/threadmg/benchmark/core/ScenarioResult.java
[NEW] src/main/java/com/threadmg/benchmark/core/BenchmarkResult.java
[NEW] src/main/java/com/threadmg/benchmark/core/BenchmarkRunner.java
[NEW] src/main/java/com/threadmg/benchmark/metrics/MetricCollector.java
[NEW] src/main/java/com/threadmg/benchmark/config/BenchmarkConfig.java
[NEW] src/main/java/com/threadmg/scenarios/cpu/CpuBoundScenario.java
[NEW] src/main/java/com/threadmg/scenarios/io/IoBoundScenario.java
[NEW] src/main/java/com/threadmg/threads/platform/PlatformThreadStrategy.java
[NEW] src/main/java/com/threadmg/threads/virtual/VirtualThreadStrategy.java
[NEW] src/main/java/com/threadmg/reporters/ConsoleReporter.java
[NEW] src/main/java/com/threadmg/reporters/MarkdownReporter.java
[NEW] src/main/java/com/threadmg/BenchmarkApp.java
[NEW] src/test/java/com/threadmg/benchmark/core/ScenarioTest.java
[NEW] src/test/java/com/threadmg/benchmark/core/ThreadStrategyTest.java
[NEW] src/test/java/com/threadmg/benchmark/core/BenchmarkRunnerTest.java
[NEW] src/test/java/com/threadmg/scenarios/CpuBoundScenarioTest.java
[NEW] src/test/java/com/threadmg/scenarios/IoBoundScenarioTest.java
[NEW] src/test/java/com/threadmg/threads/PlatformThreadStrategyTest.java
[NEW] src/test/java/com/threadmg/threads/VirtualThreadStrategyTest.java
[NEW] pom.xml (Maven 配置文件)
[NEW] results/reports/.gitkeep
```

### 6.2 需要修改的文件

```
[MODIFY] README.md - 更新当前状态和 MVP 完成情况
```

### 6.3 目录结构创建

```
[NEW] src/main/java/com/threadmg/benchmark/core/
[NEW] src/main/java/com/threadmg/benchmark/metrics/
[NEW] src/main/java/com/threadmg/benchmark/config/
[NEW] src/main/java/com/threadmg/scenarios/cpu/
[NEW] src/main/java/com/threadmg/scenarios/io/
[NEW] src/main/java/com/threadmg/threads/platform/
[NEW] src/main/java/com/threadmg/threads/virtual/
[NEW] src/main/java/com/threadmg/reporters/
[NEW] src/test/java/com/threadmg/benchmark/core/
[NEW] src/test/java/com/threadmg/scenarios/
[NEW] src/test/java/com/threadmg/threads/
[NEW] results/reports/
```

---

## 7. 架构决策记录 (ADR)

### ADR-001: 简单计时器基准测试方案

**状态**: PROPOSED

**背景**: 
- MVP 需要在 1-2 周内交付可运行的基准测试框架
- 需要在实现复杂度和结果可信度之间取得平衡
- spec.md 明确说明 MVP 阶段不使用 JMH

**决策**: 
采用简单计时器（`System.nanoTime()`）+ 手动指标收集方案，不引入 JMH 或其他基准测试框架。

**后果**:
- ✅ 实现简单，快速交付
- ✅ 代码透明，易于调试
- ⚠️ 需要手动处理预热和统计
- ⚠️ 结果精度略低于 JMH（但在可接受范围内）
- 📝 后续 Phase 2 可评估升级到 JMH

---

### ADR-002: 虚拟线程优先策略

**状态**: PROPOSED

**背景**:
- Java 21 虚拟线程是本项目核心研究对象
- 虚拟线程仅在 Java 21+ 可用
- 需要处理低版本 Java 的兼容性问题

**决策**:
1. 项目最低要求 Java 21+
2. 虚拟线程策略作为 MVP 核心功能
3. 不向下兼容 Java 20 及以下版本

**后果**:
- ✅ 简化实现，无需版本检查
- ✅ 充分利用 Java 21 新特性
- ⚠️ 用户必须升级到 Java 21
- 📝 符合项目研究目标（对比平台线程 vs 虚拟线程）

---

### ADR-003: 单线程顺序执行测试

**状态**: PROPOSED

**背景**:
- 多线程并发执行测试可能引入额外的同步开销
- MVP 阶段优先保证结果可重复性和实现简单性

**决策**:
每个场景×策略组合按顺序执行，不并行运行多个测试组合。

**后果**:
- ✅ 简化设计，避免锁竞争和并发问题
- ✅ 结果更可重复和可分析
- ⚠️ 总测试时间稍长
- 📝 Phase 2 可扩展并行执行能力

---

### ADR-004: 内存模拟 I/O 操作

**状态**: PROPOSED

**背景**:
- 真实磁盘 I/O 受系统负载、磁盘类型影响大
- 网络 I/O 受网络状况影响大
- MVP 需要稳定可重复的测试结果

**决策**:
I/O 密集型场景使用内存流（`ByteArrayInputStream`/`ByteArrayOutputStream`）模拟 I/O 操作，不使用真实磁盘或网络。

**后果**:
- ✅ 测试结果稳定可重复
- ✅ 不受外部环境影响
- ⚠️ 不能完全代表真实 I/O 场景
- 📝 Phase 2 可添加真实 I/O 场景作为补充

---

## 8. 风险评估

### 8.1 技术风险

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| **虚拟线程兼容性问题** | 低 | 高 | 要求 Java 21+，在启动时检查版本 |
| **计时精度不足** | 低 | 中 | 使用 `System.nanoTime()`，任务设计保证足够执行时间 |
| **GC 影响测试结果** | 中 | 中 | 增加预热轮次，多次迭代取平均 |
| **矩阵计算结果溢出** | 低 | 低 | 使用 `double` 类型，限制矩阵大小 |

### 8.2 依赖风险

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| **JMH 过度依赖** | 已避免 | - | 已决策不使用 JMH |
| **Maven 依赖冲突** | 低 | 低 | 仅依赖 JUnit 5 和 AssertJ，无传递依赖 |

### 8.3 时间风险

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| **范围蔓延** | 中 | 高 | 严格遵循 MVP 范围，Phase 2 再扩展 |
| **测试调试超时** | 中 | 中 | 每日构建，尽早集成测试 |
| **文档滞后** | 低 | 低 | 代码注释与实现同步，Javadoc 必须 |

### 8.4 质量风险

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| **测试覆盖率不足** | 中 | 中 | 配置 JaCoCo，要求≥80% |
| **代码规范问题** | 低 | 低 | 配置 CheckStyle，CI 检查 |
| **结果准确性** | 中 | 高 | 手工验证计算结果，添加断言测试 |

---

## 9. 技术栈决策

### 9.1 核心依赖

| 依赖 | 版本 | 用途 | 决策理由 |
|------|------|------|----------|
| **Java** | 21+ | 运行时 | 虚拟线程必需 |
| **Maven** | 3.9+ | 构建工具 | 标准 Java 构建工具 |
| **JUnit 5** | 5.10+ | 单元测试 | 现代测试框架 |
| **AssertJ** | 3.24+ | 断言库 | 流式断言，提高可读性 |

### 9.2 可选依赖（MVP 不包含）

| 依赖 | 版本 | 用途 | 排除理由 |
|------|------|------|----------|
| **JMH** | 1.37+ | 微基准测试 | MVP 简化方案，Phase 2 评估 |
| **JFR** | Built-in | 性能监控 | MVP 简化，Phase 2 添加 |
| **Jackson** | 2.15+ | JSON 处理 | MVP 无需 JSON 导出 |

### 9.3 Maven 配置要点

```xml
<properties>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <junit.version>5.10.2</junit.version>
    <assertj.version>3.24.2</assertj.version>
    <jacoco.version>0.8.11</jacoco.version>
</properties>

<dependencies>
    <!-- 测试依赖 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>${junit.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>${assertj.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 10. 实施路线图

### 10.1 开发阶段

```
Day 1-2: 框架核心
├── 创建项目结构和 pom.xml
├── 实现 Scenario 接口
├── 实现 ThreadStrategy 接口
├── 实现 BenchmarkResult 和 ScenarioResult
└── 编写核心接口单元测试

Day 3-4: 场景实现
├── 实现 CpuBoundScenario（矩阵乘法）
├── 实现 IoBoundScenario（内存 I/O 模拟）
├── 编写场景测试用例
└── 验证场景执行正确性

Day 5-6: 策略实现
├── 实现 PlatformThreadStrategy
├── 实现 VirtualThreadStrategy
├── 编写策略测试用例
└── 验证线程创建和关闭

Day 7-8: 运行器和指标
├── 实现 BenchmarkRunner
├── 实现 MetricCollector
├── 实现 BenchmarkConfig
└── 集成测试（场景×策略）

Day 9: 报告系统
├── 实现 ConsoleReporter
├── 实现 MarkdownReporter
├── 生成示例报告
└── 验证报告格式

Day 10: 完善和验收
├── 编写 BenchmarkApp 入口
├── 完整测试运行
├── 代码审查和重构
├── 文档更新
└── MVP 验收
```

### 10.2 里程碑

| 里程碑 | 预计时间 | 交付内容 | 验收标准 |
|--------|---------|----------|----------|
| **M1: 框架核心** | Day 2 | 核心接口 + 数据模型 | 单元测试通过 |
| **M2: 场景实现** | Day 4 | CPU + I/O 场景 | 场景可执行 |
| **M3: 策略实现** | Day 6 | 平台 + 虚拟线程策略 | 策略可创建执行器 |
| **M4: 运行器集成** | Day 8 | BenchmarkRunner 完整功能 | 场景×策略可运行 |
| **M5: 报告系统** | Day 9 | 控制台 + Markdown 报告 | 报告可生成 |
| **M6: MVP 完成** | Day 10 | 完整可运行系统 | 所有验收标准通过 |

---

## 11. 测试策略

### 11.1 单元测试

| 类 | 测试重点 | 覆盖率目标 |
|----|---------|-----------|
| `CpuBoundScenario` | 任务创建、执行、结果验证 | ≥85% |
| `IoBoundScenario` | 任务创建、执行、结果验证 | ≥85% |
| `PlatformThreadStrategy` | 执行器创建、关闭 | ≥80% |
| `VirtualThreadStrategy` | 执行器创建、关闭 | ≥80% |
| `BenchmarkRunner` | 预热、迭代执行、结果聚合 | ≥85% |
| `MetricCollector` | 吞吐量计算、百分位计算 | ≥90% |
| `BenchmarkConfig` | Builder 模式、默认值 | ≥80% |

### 11.2 集成测试

- 场景×策略组合执行测试
- 完整基准测试流程测试
- 报告生成测试

### 11.3 性能验证测试

- 框架开销测试（≤ 5%）
- 单次测试时长测试（≤ 60 秒）
- 内存占用测试（≤ 256MB）

---

## 12. 总结

### 12.1 规划统计

| 类别 | 数量 |
|------|------|
| **核心接口** | 2 (Scenario, ThreadStrategy) |
| **核心类** | 5 (BenchmarkRunner, Result 类等) |
| **场景实现** | 2 (CPU, I/O) |
| **策略实现** | 2 (Platform, Virtual) |
| **报告生成** | 2 (Console, Markdown) |
| **测试类** | 7+ |
| **ADR** | 4 |

### 12.2 关键决策

1. **简单计时器方案** - 快速交付，避免 JMH 复杂度
2. **Java 21+ 要求** - 虚拟线程必需，不向下兼容
3. **顺序执行测试** - 简化设计，保证结果可重复
4. **内存模拟 I/O** - 稳定可重复，避免外部因素影响

### 12.3 下一步

👉 运行 `@sdd-tasks mvp-test` 开始任务分解

---

## ✅ 技术规划完成

**Feature**: MVP 基准测试框架  
**状态**: planned  
**文件**: `.specs/mvp-test/plan.md`

### 生成的 ADR

| 编号 | 标题 | 状态 |
|------|------|------|
| ADR-001 | 简单计时器基准测试方案 | PROPOSED |
| ADR-002 | 虚拟线程优先策略 | PROPOSED |
| ADR-003 | 单线程顺序执行测试 | PROPOSED |
| ADR-004 | 内存模拟 I/O 操作 | PROPOSED |

### 文件位置

- **技术规划**: `.specs/mvp-test/plan.md`
- **架构决策**: `.specs/architecture/adr/ADR-001.md` 等

### 下一步

👉 运行 `@sdd-tasks mvp-test` 开始任务分解

### 状态更新

请运行以下命令更新状态：
```bash
/tool sdd_update_state {"feature": "mvp-test", "state": "planned"}
```
