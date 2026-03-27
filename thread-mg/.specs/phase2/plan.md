# 技术规划：Phase 2 扩展阶段

**Feature ID**: phase2-extension  
**关联规范**: [.specs/phase2/spec.md](spec.md)  
**创建日期**: 2026-03-27  
**状态**: completed（补档）

---

## 🏗️ 架构设计

### 整体架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                    Phase 2 新增组件                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────┐    ┌─────────────────────────────┐       │
│  │ ThreadPoolStrategy│    │      新增场景类             │       │
│  │ (线程池策略)      │    │                             │       │
│  │                  │    │  ┌─────────────────────┐   │       │
│  │ - corePoolSize   │    │  │ HighConcurrencyScn  │   │       │
│  │ - maxPoolSize    │    │  │ (高并发短任务)      │   │       │
│  │ - queueCapacity  │    │  └─────────────────────┘   │       │
│  │ - rejectPolicy   │    │                             │       │
│  └────────┬─────────┘    │  ┌─────────────────────┐   │       │
│           │              │  │ MixedLoadScenario   │   │       │
│           │ 实现          │  │ (混合负载)          │   │       │
│           │              │  └─────────────────────┘   │       │
│           ▼              │                             │       │
│  ┌──────────────────┐    │  ┌─────────────────────┐   │       │
│  │ ThreadStrategy   │    │  │ RecursiveScenario   │   │       │
│  │ (策略接口)        │    │  │ (递归分治)          │   │       │
│  └──────────────────┘    │  └─────────────────────┘   │       │
│                          │                             │       │
└──────────────────────────┴─────────────────────────────┘       │
                                                                 │
┌─────────────────────────────────────────────────────────────────┘
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

## 📦 模块设计

### 1. ThreadPoolStrategy 模块

#### 类设计
```java
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
    
    // ExecutorService 实例
    private ExecutorService executorService;
}
```

#### 配置参数
| 参数 | 默认值 | 说明 |
|------|--------|------|
| corePoolSize | CPU 核心数 | 核心线程数 |
| maxPoolSize | CPU 核心数 × 2 | 最大线程数 |
| keepAliveTime | 60 秒 | 空闲线程存活时间 |
| queueCapacity | 1000 | 任务队列容量 |
| rejectPolicy | CallerRuns | 拒绝策略 |

#### 拒绝策略支持
- `AbortPolicy`: 抛出 RejectedExecutionException
- `CallerRunsPolicy`: 由调用线程执行
- `DiscardPolicy`: 直接丢弃
- `DiscardOldestPolicy`: 丢弃最老任务

---

### 2. HighConcurrencyScenario 模块

#### 类设计
```java
public class HighConcurrencyScenario implements Scenario {
    private final int taskCount;
    private final int minExecutionTimeMs;
    private final int maxExecutionTimeMs;
    private final boolean simulateIoWait;
}
```

#### 配置参数
| 参数 | 默认值 | 说明 |
|------|--------|------|
| taskCount | 10000 | 任务总数 |
| minExecutionTimeMs | 1 | 最小执行时间 |
| maxExecutionTimeMs | 10 | 最大执行时间 |
| simulateIoWait | true | 是否模拟 I/O |

---

### 3. MixedLoadScenario 模块

#### 类设计
```java
public class MixedLoadScenario implements Scenario {
    private final double cpuRatio;
    private final double ioRatio;
    private final int totalTasks;
    private final int matrixSize;
    private final int ioDelayMs;
}
```

#### 配置参数
| 参数 | 默认值 | 说明 |
|------|--------|------|
| cpuRatio | 0.7 | CPU 任务占比 |
| ioRatio | 0.3 | I/O 任务占比 |
| totalTasks | 1000 | 任务总数 |
| matrixSize | 50 | 矩阵大小 |
| ioDelayMs | 5 | I/O 延迟 |

---

### 4. RecursiveScenario 模块

#### 类设计
```java
public class RecursiveScenario implements Scenario {
    private final AlgorithmType algorithmType;
    private final int dataSize;
    private final int threshold;
}

public enum AlgorithmType {
    QUICK_SORT,      // 快速排序
    MERGE_SORT,      // 归并排序
    FIBONACCI,       // 斐波那契
    MATRIX_MULTIPLY  // 矩阵乘法 (Strassen)
}
```

#### 配置参数
| 参数 | 默认值 | 说明 |
|------|--------|------|
| algorithmType | QUICK_SORT | 算法类型 |
| dataSize | 10000 | 数据规模 |
| threshold | 1000 | 并行阈值 |

---

## 📁 文件影响分析

### 需要创建的文件
```
[NEW] src/main/java/com/threadmg/threads/pool/ThreadPoolStrategy.java
[NEW] src/test/java/com/threadmg/threads/pool/ThreadPoolStrategyTest.java

[NEW] src/main/java/com/threadmg/scenarios/concurrency/HighConcurrencyScenario.java
[NEW] src/test/java/com/threadmg/scenarios/concurrency/HighConcurrencyScenarioTest.java

[NEW] src/main/java/com/threadmg/scenarios/mixed/MixedLoadScenario.java
[NEW] src/test/java/com/threadmg/scenarios/mixed/MixedLoadScenarioTest.java

[NEW] src/main/java/com/threadmg/scenarios/recursive/RecursiveScenario.java
[NEW] src/test/java/com/threadmg/scenarios/recursive/RecursiveScenarioTest.java

[NEW] docs/validation/PHASE2_REPORT.md
```

### 需要修改的文件
```
[MODIFY] config/benchmark/benchmark.yaml    # 添加新场景和策略配置
[MODIFY] README.md                          # 更新功能清单
```

---

## ⚠️ 风险评估

### 技术风险
| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| ThreadPoolStrategy 资源泄露 | 中 | 高 | 实现 try-finally 确保 shutdown |
| 递归场景栈溢出 | 低 | 中 | 设置合理阈值，使用 Fork/Join |
| 并发测试不稳定 | 中 | 中 | 增加重试机制，放宽断言 |

### 依赖风险
| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| Fork/Join 框架兼容性问题 | 低 | 低 | Java 21 原生支持 |

---

## 📊 工作量估算

| 模块 | 开发时间 | 测试时间 | 总计 |
|------|----------|----------|------|
| ThreadPoolStrategy | 2h | 1h | 3h |
| HighConcurrencyScenario | 1.5h | 1h | 2.5h |
| MixedLoadScenario | 1.5h | 1h | 2.5h |
| RecursiveScenario | 2h | 1h | 3h |
| 配置更新 | 0.5h | 0.5h | 1h |
| 文档更新 | 1h | - | 1h |
| **总计** | **8.5h** | **4.5h** | **13h** |

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

---

*文档版本：1.0（补档） | 创建日期：2026-03-27*
