# 代码实现指南 (Implementation Guide)

> 本文档提供 thread-mg 项目的详细代码实现指南，帮助开发者快速上手并遵循项目规范。

---

## 📋 目录

1. [项目结构](#-项目结构)
2. [核心接口](#-核心接口)
3. [实现步骤](#-实现步骤)
4. [代码示例](#-代码示例)
5. [最佳实践](#-最佳实践)
6. [常见问题](#-常见问题)

---

## 🏗️ 项目结构

### 模块划分

本项目采用**二维矩阵**结构组织代码：

- **X 轴**: 线程技术类型（6 种）
- **Y 轴**: 应用场景（6 种）

```
src/main/java/com/threadmg/
│
├── benchmark/           # 基准测试框架（横向切面）
│   ├── core/            # 核心框架
│   ├── metrics/         # 指标收集
│   ├── reporters/       # 报告生成
│   └── config/          # 配置管理
│
├── scenarios/           # 测试场景（纵向切面 - Y 轴）
│   ├── cpu-bound/       # CPU 密集型
│   ├── io-bound/        # I/O 密集型
│   ├── high-concurrency/# 高并发短任务
│   ├── mixed-load/      # 混合负载
│   ├── recursive/       # 递归分治
│   └── async-pipeline/  # 异步流水线
│
└── threads/             # 线程实现（纵向切面 - X 轴）
    ├── platform/        # 平台线程
    ├── virtual/         # 虚拟线程
    ├── pool/            # 线程池
    ├── forkjoin/        # Fork/Join
    ├── completable/     # CompletableFuture
    └── reactive/        # Reactive Streams
```

### 命名规范

#### 包命名
```
com.threadmg.benchmark.core
com.threadmg.scenarios.cpu-bound
com.threadmg.threads.platform
```

#### 类命名
```
# 场景类
[场景名]Scenario.java
例：CpuBoundScenario.java

# 策略类
[技术名]ThreadStrategy.java
例：VirtualThreadStrategy.java

# 基准类
[场景名][技术名]Benchmark.java
例：CpuBoundVirtualBenchmark.java

# 任务类
[任务名]Task.java
例：MatrixMultiplicationTask.java
```

---

## 🔌 核心接口

### 1. Scenario 接口

所有测试场景必须实现此接口：

```java
package com.threadmg.benchmark.core;

import java.util.List;

/**
 * 测试场景接口
 * 定义一个基准测试场景的基本结构
 */
public interface Scenario {
    
    /**
     * 获取场景名称
     * @return 场景唯一标识
     */
    String getName();
    
    /**
     * 获取场景描述
     * @return 场景详细说明
     */
    String getDescription();
    
    /**
     * 获取测试任务列表
     * @return 任务列表
     */
    List<Task<?>> getTasks();
    
    /**
     * 获取验证标准
     * @return 验证标准
     */
    ValidationCriteria getValidationCriteria();
    
    /**
     * 场景初始化
     */
    default void setup() {
        // 可选的初始化逻辑
    }
    
    /**
     * 场景清理
     */
    default void teardown() {
        // 可选的清理逻辑
    }
}
```

### 2. ThreadStrategy 接口

所有线程技术实现必须实现此接口：

```java
package com.threadmg.benchmark.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 线程策略接口
 * 定义不同线程技术的实现方式
 */
public interface ThreadStrategy {
    
    /**
     * 获取策略名称
     * @return 策略唯一标识
     */
    String getName();
    
    /**
     * 获取策略描述
     * @return 策略详细说明
     */
    String getDescription();
    
    /**
     * 创建执行器
     * @return 配置好的执行器
     */
    ExecutorService createExecutor();
    
    /**
     * 提交任务
     * @param task 任务
     * @return Future 对象
     */
    <T> Future<T> submit(Task<T> task);
    
    /**
     * 批量提交任务
     * @param tasks 任务列表
     * @return Future 列表
     */
    <T> List<Future<T>> submitAll(List<Task<T>> tasks);
    
    /**
     * 关闭执行器
     */
    void shutdown();
    
    /**
     * 获取当前线程数
     * @return 线程数
     */
    int getThreadCount();
}
```

### 3. Task 接口

所有测试任务必须实现此接口：

```java
package com.threadmg.benchmark.core;

import java.util.concurrent.Callable;

/**
 * 任务接口
 * 定义基准测试中的基本任务单元
 */
public interface Task<T> extends Callable<T> {
    
    /**
     * 获取任务名称
     * @return 任务唯一标识
     */
    String getName();
    
    /**
     * 获取任务描述
     * @return 任务详细说明
     */
    String getDescription();
    
    /**
     * 获取预期结果（用于验证）
     * @return 预期结果
     */
    T getExpectedResult();
    
    /**
     * 验证结果
     * @param actual 实际结果
     * @return 是否匹配
     */
    boolean verify(T actual);
}
```

### 4. Benchmark 接口

基准测试主接口：

```java
package com.threadmg.benchmark.core;

/**
 * 基准测试接口
 * 组合场景和策略，执行完整测试
 */
public interface Benchmark {
    
    /**
     * 获取场景
     * @return 测试场景
     */
    Scenario getScenario();
    
    /**
     * 获取线程策略
     * @return 线程策略
     */
    ThreadStrategy getStrategy();
    
    /**
     * 执行测试
     * @return 测试结果
     */
    BenchmarkResult execute();
    
    /**
     * 验证结果
     * @return 验证结果
     */
    ValidationResult validate();
}
```

---

## 📝 实现步骤

### 步骤 1: 实现新场景

#### 1.1 创建场景目录
```bash
mkdir -p src/main/java/com/threadmg/scenarios/[scenario-name]
mkdir -p src/main/java/com/threadmg/scenarios/[scenario-name]/tasks
mkdir -p src/test/java/com/threadmg/scenarios/[scenario-name]
```

#### 1.2 实现 Scenario
```java
package com.threadmg.scenarios.newscenario;

import com.threadmg.benchmark.core.*;
import java.util.List;
import java.util.Arrays;

public class NewScenario implements Scenario {
    
    @Override
    public String getName() {
        return "new-scenario";
    }
    
    @Override
    public String getDescription() {
        return "新场景的详细说明";
    }
    
    @Override
    public List<Task<?>> getTasks() {
        return Arrays.asList(
            new Task1(),
            new Task2()
        );
    }
    
    @Override
    public ValidationCriteria getValidationCriteria() {
        return new ValidationCriteria.Builder()
            .minThroughput(1000)
            .maxLatencyP99(Duration.ofMillis(100))
            .build();
    }
}
```

#### 1.3 实现 Task
```java
package com.threadmg.scenarios.newscenario.tasks;

import com.threadmg.benchmark.core.Task;

public class Task1 implements Task<Double> {
    
    @Override
    public String getName() {
        return "task-1";
    }
    
    @Override
    public String getDescription() {
        return "任务 1 的说明";
    }
    
    @Override
    public Double getExpectedResult() {
        return 42.0;
    }
    
    @Override
    public boolean verify(Double actual) {
        return actual != null && actual > 0;
    }
    
    @Override
    public Double call() throws Exception {
        // 实现任务逻辑
        return compute();
    }
    
    private Double compute() {
        // 具体计算逻辑
        return 42.0;
    }
}
```

#### 1.4 编写测试
```java
package com.threadmg.scenarios.newscenario;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NewScenarioTest {
    
    @Test
    void testScenarioCreation() {
        Scenario scenario = new NewScenario();
        assertNotNull(scenario);
        assertEquals("new-scenario", scenario.getName());
    }
    
    @Test
    void testTasks() {
        Scenario scenario = new NewScenario();
        List<Task<?>> tasks = scenario.getTasks();
        assertTrue(tasks.size() > 0);
    }
}
```

### 步骤 2: 实现新线程策略

#### 2.1 创建策略目录
```bash
mkdir -p src/main/java/com/threadmg/threads/[strategy-name]
mkdir -p src/main/java/com/threadmg/threads/[strategy-name]/config
mkdir -p src/test/java/com/threadmg/threads/[strategy-name]
```

#### 2.2 实现 ThreadStrategy
```java
package com.threadmg.threads.newstrategy;

import com.threadmg.benchmark.core.*;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class NewThreadStrategy implements ThreadStrategy {
    
    private final ExecutorService executor;
    private final int threadCount;
    
    public NewThreadStrategy(int threadCount) {
        this.threadCount = threadCount;
        this.executor = createExecutor();
    }
    
    @Override
    public String getName() {
        return "new-strategy";
    }
    
    @Override
    public String getDescription() {
        return "新线程策略的详细说明";
    }
    
    @Override
    public ExecutorService createExecutor() {
        return new ThreadPoolExecutor(
            threadCount,
            threadCount,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>()
        );
    }
    
    @Override
    public <T> Future<T> submit(Task<T> task) {
        return executor.submit(task);
    }
    
    @Override
    public <T> List<Future<T>> submitAll(List<Task<T>> tasks) {
        return tasks.stream()
            .map(this::submit)
            .collect(Collectors.toList());
    }
    
    @Override
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    @Override
    public int getThreadCount() {
        return threadCount;
    }
}
```

#### 2.3 编写测试
```java
package com.threadmg.threads.newstrategy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NewThreadStrategyTest {
    
    @Test
    void testStrategyCreation() {
        ThreadStrategy strategy = new NewThreadStrategy(4);
        assertNotNull(strategy);
        assertEquals(4, strategy.getThreadCount());
    }
    
    @Test
    void testSubmit() throws Exception {
        ThreadStrategy strategy = new NewThreadStrategy(4);
        Task<String> task = new SimpleTask();
        Future<String> future = strategy.submit(task);
        assertNotNull(future);
        strategy.shutdown();
    }
}
```

### 步骤 3: 实现基准测试

#### 3.1 创建 Benchmark 类
```java
package com.threadmg.benchmarks;

import com.threadmg.benchmark.core.*;
import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(3)
public class NewScenarioNewStrategyBenchmark {
    
    private Scenario scenario;
    private ThreadStrategy strategy;
    private BenchmarkRunner runner;
    
    @Setup
    public void setup() {
        scenario = new NewScenario();
        strategy = new NewThreadStrategy(4);
        runner = new BenchmarkRunner(scenario, strategy);
    }
    
    @TearDown
    public void teardown() {
        strategy.shutdown();
        scenario.teardown();
    }
    
    @Benchmark
    public BenchmarkResult benchmark() {
        return runner.execute();
    }
}
```

---

## 💡 代码示例

### 完整示例：CPU 密集型 + 虚拟线程

#### 场景实现
```java
package com.threadmg.scenarios.cpu-bound;

import com.threadmg.benchmark.core.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CpuBoundScenario implements Scenario {
    
    private static final int MATRIX_SIZE = 100;
    
    @Override
    public String getName() {
        return "cpu-bound";
    }
    
    @Override
    public String getDescription() {
        return "CPU 密集型场景：矩阵乘法、素数计算等";
    }
    
    @Override
    public List<Task<?>> getTasks() {
        return List.of(
            new MatrixMultiplicationTask(MATRIX_SIZE),
            new PrimeNumberTask(10000),
            new FibonacciTask(30)
        );
    }
    
    @Override
    public ValidationCriteria getValidationCriteria() {
        return new ValidationCriteria.Builder()
            .minThroughput(100)
            .maxLatencyP99(java.time.Duration.ofSeconds(1))
            .build();
    }
}
```

#### 任务实现
```java
package com.threadmg.scenarios.cpu-bound.tasks;

import com.threadmg.benchmark.core.Task;

public class MatrixMultiplicationTask implements Task<double[][]> {
    
    private final int size;
    
    public MatrixMultiplicationTask(int size) {
        this.size = size;
    }
    
    @Override
    public String getName() {
        return "matrix-multiplication-" + size;
    }
    
    @Override
    public String getDescription() {
        return "计算两个 " + size + "x" + size + " 矩阵的乘积";
    }
    
    @Override
    public double[][] getExpectedResult() {
        // 简化验证，只检查结果维度
        return new double[size][size];
    }
    
    @Override
    public boolean verify(double[][] actual) {
        return actual != null && 
               actual.length == size && 
               actual[0].length == size;
    }
    
    @Override
    public double[][] call() throws Exception {
        double[][] a = generateMatrix(size);
        double[][] b = generateMatrix(size);
        return multiply(a, b);
    }
    
    private double[][] generateMatrix(int size) {
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = Math.random();
            }
        }
        return matrix;
    }
    
    private double[][] multiply(double[][] a, double[][] b) {
        double[][] result = new double[a.length][b[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                for (int k = 0; k < a[0].length; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }
}
```

#### 策略实现
```java
package com.threadmg.threads.virtual;

import com.threadmg.benchmark.core.*;
import java.util.concurrent.*;

public class VirtualThreadStrategy implements ThreadStrategy {
    
    private final ExecutorService executor;
    
    public VirtualThreadStrategy() {
        // Java 21+ 虚拟线程
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }
    
    @Override
    public String getName() {
        return "virtual";
    }
    
    @Override
    public String getDescription() {
        return "虚拟线程策略 (Java 21+)";
    }
    
    @Override
    public ExecutorService createExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
    
    @Override
    public <T> Future<T> submit(Task<T> task) {
        return executor.submit(task);
    }
    
    @Override
    public void shutdown() {
        executor.shutdown();
    }
    
    @Override
    public int getThreadCount() {
        // 虚拟线程数动态变化
        return -1;
    }
}
```

---

## 📚 最佳实践

### 1. 场景设计原则

- **单一职责**: 每个场景只测试一种类型的工作负载
- **可复现**: 使用固定随机种子确保结果可复现
- **独立**: 场景之间互不影响
- **可验证**: 提供明确的验证标准

### 2. 策略设计原则

- **接口隔离**: 只暴露必要的方法
- **资源管理**: 正确管理线程生命周期
- **错误处理**: 优雅处理异常情况
- **配置灵活**: 支持参数化配置

### 3. 测试编写原则

- **覆盖全面**: 覆盖正常、边界、异常情况
- **断言明确**: 使用清晰的断言
- **隔离**: 测试之间互相独立
- **快速**: 单元测试应在秒级完成

### 4. 性能测试原则

- **预热充分**: 确保 JIT 编译完成
- **多次运行**: 减少随机波动影响
- **环境稳定**: 固定 CPU 频率，减少干扰
- **统计有效**: 使用合适的统计方法

---

## ❓ 常见问题

### Q1: 如何选择场景和策略的组合？

**A**: 参考 README 中的工程结构矩阵，✓ 标记的组合是推荐的。

### Q2: 虚拟线程需要什么版本？

**A**: 需要 Java 21 或更高版本。

### Q3: 如何调试性能问题？

**A**: 使用 JFR 记录详细性能数据：
```bash
java -XX:StartFlightRecording=filename=recording.jfr -jar benchmark.jar
```

### Q4: 测试结果的方差太大怎么办？

**A**: 
1. 增加预热轮次
2. 增加测量轮次
3. 固定系统环境
4. 检查是否有资源竞争

### Q5: 如何添加新的指标？

**A**: 
1. 在 `MetricCollector` 中添加收集逻辑
2. 在 `BenchmarkResult` 中添加字段
3. 在 `Reporter` 中添加展示逻辑
4. 更新验证规则

---

## 🔗 相关资源

- [工程矩阵](../../README.md#-工程结构矩阵)
- [验证方案](../validation/README.md)
- [场景说明](../scenarios/)
- [线程技术说明](../threads/)

---

*文档版本：1.0 | 最后更新：2026-03-27*
