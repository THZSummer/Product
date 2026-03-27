# README 更新 - 技术规划文档

## 规划元数据

| 字段 | 值 |
|------|-----|
| **Feature ID** | FR-README-001 |
| **规划版本** | 1.0 |
| **创建日期** | 2026-03-27 |
| **基于规范** | spec.md v1.0 |
| **状态** | planned |

---

## 1. 架构设计

### 1.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          thread-mg 项目架构                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                        文档层 (Documentation)                      │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐   │   │
│  │  │   README    │  │  SDD 指南   │  │     结果报告模板        │   │   │
│  │  └─────────────┘  └─────────────┘  └─────────────────────────┘   │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                    ↓                                      │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                      基准测试框架层 (Benchmark Core)               │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐   │   │
│  │  │  Benchmark  │  │  Metrics    │  │      Reporters          │   │   │
│  │  │   Engine    │  │  Collector  │  │   (Markdown/HTML)       │   │   │
│  │  └─────────────┘  └─────────────┘  └─────────────────────────┘   │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                    ↓                                      │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                     场景实现层 (Scenarios)                         │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────────┐ │   │
│  │  │  CPU    │ │   I/O   │ │  High   │ │  Mixed  │ │  Recursive  │ │   │
│  │  │  Bound  │ │  Bound  │ │  Conc   │ │   Load  │ │             │ │   │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────────┘ │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                    ↓                                      │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                    线程技术层 (Thread Implementations)             │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────────┐ │   │
│  │  │Platform │ │ Virtual │ │  Pool   │ │Fork/Join│ │ CompletableFuture│ │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────────┘ │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### 1.2 模块依赖关系

```
                    ┌─────────────────┐
                    │    README.md    │
                    │   (文档入口)    │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
        ↓                    ↓                    ↓
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│   scenarios/  │   │    threads/   │   │  benchmark/   │
│  (测试场景)   │   │ (线程实现)    │   │  (基准框架)   │
└───────┬───────┘   └───────┬───────┘   └───────┬───────┘
        │                   │                   │
        └───────────────────┼───────────────────┘
                            │
                            ↓
                  ┌─────────────────┐
                  │   validation/   │
                  │   (验证方案)    │
                  └─────────────────┘
```

---

## 2. 技术方案详解

### 2.1 工程结构矩阵实现

#### 2.1.1 矩阵定义
采用**二维矩阵**方式组织代码：
- **X 轴**: 线程技术类型（6 种）
- **Y 轴**: 应用场景（6 种）
- **单元格**: 具体的基准测试实现

#### 2.1.2 命名规范
```
[场景]-[线程技术]-Benchmark.java
例如：
- CpuBoundPlatformBenchmark.java
- CpuBoundVirtualBenchmark.java
- IoBoundVirtualBenchmark.java
```

### 2.2 目录结构实现

#### 2.2.1 源码目录
```
src/main/java/com/threadmg/
├── benchmark/
│   ├── core/
│   │   ├── BenchmarkRunner.java       # 测试运行器
│   │   ├── Scenario.java              # 场景接口
│   │   ├── ThreadStrategy.java        # 线程策略接口
│   │   └── BenchmarkResult.java       # 结果对象
│   ├── metrics/
│   │   ├── ThroughputMetric.java      # 吞吐量指标
│   │   ├── LatencyMetric.java         # 延迟指标
│   │   ├── ResourceMetric.java        # 资源指标
│   │   └── MetricCollector.java       # 指标收集器
│   └── reporters/
│       ├── MarkdownReporter.java      # Markdown 报告
│       ├── HtmlReporter.java          # HTML 报告
│       └── JsonReporter.java          # JSON 报告
│
├── scenarios/
│   ├── cpu-bound/
│   │   ├── CpuBoundScenario.java      # 场景定义
│   │   └── tasks/                     # 计算任务
│   ├── io-bound/
│   │   ├── IoBoundScenario.java
│   │   └── tasks/
│   ├── high-concurrency/
│   ├── mixed-load/
│   ├── recursive/
│   └── async-pipeline/
│
└── threads/
    ├── platform/
    │   ├── PlatformThreadStrategy.java
    │   └── config/
    ├── virtual/
    │   ├── VirtualThreadStrategy.java
    │   └── config/
    ├── pool/
    │   ├── ThreadPoolStrategy.java
    │   └── config/
    ├── forkjoin/
    │   ├── ForkJoinStrategy.java
    │   └── config/
    ├── completable/
    │   ├── CompletableFutureStrategy.java
    │   └── config/
    └── reactive/
        ├── ReactiveStrategy.java
        └── config/
```

#### 2.2.2 测试目录
```
src/test/java/com/threadmg/
├── benchmark/
│   ├── core/
│   │   └── BenchmarkRunnerTest.java
│   └── metrics/
│       └── MetricCollectorTest.java
├── scenarios/
│   └── [镜像 src 结构]
└── threads/
    └── [镜像 src 结构]
```

### 2.3 验证方案实现

#### 2.3.1 验证框架
```
validation/
├── ValidationEngine.java          # 验证引擎
├── ValidationRule.java            # 验证规则接口
├── rules/
│   ├── PerformanceRule.java       # 性能规则
│   ├── CorrectnessRule.java       # 正确性规则
│   └── ResourceRule.java          # 资源规则
└── reports/
    └── ValidationReport.java      # 验证报告
```

#### 2.3.2 验证流程
```java
// 伪代码示例
public class ValidationFlow {
    public void validate(BenchmarkResult result) {
        // 1. 正确性验证
        CorrectnessRule correctness = new CorrectnessRule();
        if (!correctness.validate(result)) {
            throw new ValidationException("结果不正确");
        }
        
        // 2. 性能验证
        PerformanceRule performance = new PerformanceRule();
        performance.checkRegression(result);
        
        // 3. 资源验证
        ResourceRule resource = new ResourceRule();
        resource.checkLimits(result);
        
        // 4. 生成报告
        ValidationReport report = generateReport(result);
    }
}
```

---

## 3. 接口设计

### 3.1 核心接口

#### Scenario 接口
```java
public interface Scenario {
    String getName();
    String getDescription();
    List<Task> getTasks();
    ValidationCriteria getValidationCriteria();
}
```

#### ThreadStrategy 接口
```java
public interface ThreadStrategy {
    String getName();
    ExecutorService createExecutor();
    <T> Future<T> submit(Task<T> task);
    void shutdown();
}
```

#### Benchmark 接口
```java
public interface Benchmark {
    Scenario getScenario();
    ThreadStrategy getStrategy();
    BenchmarkResult execute();
    void validate();
}
```

### 3.2 配置接口

```java
public interface BenchmarkConfig {
    int getWarmupIterations();
    int getBenchmarkIterations();
    Duration getIterationTime();
    int getThreadCount();
}
```

---

## 4. 数据结构设计

### 4.1 核心数据类

#### BenchmarkResult
```java
public class BenchmarkResult {
    private String scenarioName;
    private String threadStrategy;
    private Instant executionTime;
    private ThroughputMetrics throughput;
    private LatencyMetrics latency;
    private ResourceMetrics resource;
    private ValidationResult validation;
}
```

#### ThroughputMetrics
```java
public class ThroughputMetrics {
    private double opsPerSecond;
    private double tasksPerSecond;
    private double bytesPerSecond;
}
```

#### LatencyMetrics
```java
public class LatencyMetrics {
    private Duration p50;
    private Duration p95;
    private Duration p99;
    private Duration max;
    private Duration min;
}
```

### 4.2 配置数据结构

```java
public class ValidationConfig {
    private double performanceRegressionThreshold;  // 默认 5%
    private double codeCoverageThreshold;         // 默认 80%
    private int stabilityTestRuns;                // 默认 3
    private Map<String, ResourceLimit> limits;
}
```

---

## 5. 技术栈选型

| 组件 | 技术 | 版本 | 理由 |
|------|------|------|------|
| **构建工具** | Maven | 3.9+ | 广泛支持，依赖管理成熟 |
| **基准测试** | JMH | 1.37+ | Java 官方基准框架 |
| **单元测试** | JUnit 5 | 5.10+ | 现代测试框架 |
| **断言库** | AssertJ | 3.24+ | 流式断言，可读性好 |
| **监控** | JFR | Built-in | JDK 内置，低开销 |
| **报告** | Markdown | - | 易读，版本友好 |
| **CI/CD** | GitHub Actions | - | 免费，集成好 |

---

## 6. 实施策略

### 6.1 分阶段实施

#### 阶段 1: 文档更新（当前）
- [ ] 更新 README 结构矩阵
- [ ] 更新目录结构说明
- [ ] 添加验证方案文档

#### 阶段 2: 框架搭建
- [ ] 创建基准测试核心框架
- [ ] 创建场景接口和实现
- [ ] 创建线程策略接口和实现

#### 阶段 3: 验证实现
- [ ] 实现验证引擎
- [ ] 实现验证规则
- [ ] 实现报告生成

#### 阶段 4: 示例实现
- [ ] 实现一个完整场景示例
- [ ] 实现两种线程策略示例
- [ ] 运行基准测试并生成报告

### 6.2 优先级矩阵

| 任务 | 重要性 | 紧急性 | 优先级 |
|------|--------|--------|--------|
| README 文档更新 | 高 | 高 | P0 |
| 基准框架核心 | 高 | 中 | P0 |
| 验证方案实现 | 高 | 中 | P1 |
| 场景实现 (CPU) | 中 | 中 | P1 |
| 线程策略 (Platform) | 中 | 中 | P1 |
| 线程策略 (Virtual) | 中 | 中 | P1 |
| 其他场景实现 | 低 | 低 | P2 |

---

## 7. 风险评估

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| JMH 学习曲线 | 中 | 低 | 提供示例代码和文档链接 |
| 性能测试不稳定 | 中 | 中 | 多次运行取平均值，设置容差 |
| 虚拟线程环境要求 | 高 | 低 | 明确标注 Java 21+ 要求 |
| 文档维护成本 | 中 | 中 | 自动化检查文档一致性 |

---

## 8. 成功标准

### 8.1 文档成功标准
- [ ] README 清晰展示 6×6 工程矩阵
- [ ] 目录结构完整且准确
- [ ] 验证方案可理解和执行

### 8.2 技术成功标准
- [ ] 框架支持所有 6 种场景
- [ ] 框架支持所有 6 种线程技术
- [ ] 验证流程自动化运行
- [ ] 测试结果可复现

### 8.3 质量成功标准
- [ ] 单元测试覆盖率 ≥ 80%
- [ ] 代码符合项目规范
- [ ] 文档与代码一致

---

*规划版本：1.0 | 最后更新：2026-03-27*
