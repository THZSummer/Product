# MVP 基准测试框架 - 测试计划与任务分解

## 文档元数据

| 字段 | 值 |
|------|-----|
| **文档类型** | 测试计划与任务分解 |
| **关联 Feature** | MVP 基准测试框架 (mvp-test) |
| **创建日期** | 2026-03-27 |
| **作者** | Test Planning Agent |
| **状态** | draft |
| **优先级** | P0 |

---

## 1. MVP 功能概述

### 1.1 已识别的 MVP 功能

基于对 `spec.md` 和 `plan.md` 的分析，MVP 包含以下核心功能：

| 功能模块 | 组件 | 说明 | 优先级 |
|---------|------|------|--------|
| **核心框架** | Scenario 接口 | 测试场景定义接口 | P0 |
| **核心框架** | ThreadStrategy 接口 | 线程策略定义接口 | P0 |
| **核心框架** | BenchmarkRunner | 基准测试运行器 | P0 |
| **核心框架** | BenchmarkResult | 测试结果数据模型 | P0 |
| **核心框架** | ScenarioResult | 场景执行结果数据模型 | P0 |
| **配置管理** | BenchmarkConfig | 测试配置类 | P1 |
| **指标收集** | MetricCollector | 性能指标收集器 | P1 |
| **场景实现** | CpuBoundScenario | CPU 密集型场景（矩阵乘法） | P0 |
| **场景实现** | IoBoundScenario | I/O 密集型场景（内存模拟） | P0 |
| **策略实现** | PlatformThreadStrategy | 平台线程策略 | P0 |
| **策略实现** | VirtualThreadStrategy | 虚拟线程策略 | P0 |
| **报告生成** | ConsoleReporter | 控制台报告输出 | P1 |
| **报告生成** | MarkdownReporter | Markdown 报告生成 | P2 |
| **入口程序** | BenchmarkApp | 主程序入口 | P1 |

### 1.2 测试范围矩阵

| 测试类型 | 单元测试 | 集成测试 | 端到端测试 | 性能测试 |
|---------|:-------:|:-------:|:---------:|:-------:|
| **核心接口** | ✅ | ✅ | - | - |
| **场景实现** | ✅ | ✅ | ✅ | ✅ |
| **策略实现** | ✅ | ✅ | ✅ | ✅ |
| **运行器** | ✅ | ✅ | ✅ | - |
| **指标收集** | ✅ | - | - | - |
| **报告生成** | ✅ | ✅ | ✅ | - |
| **配置管理** | ✅ | - | - | - |

---

## 2. 测试策略

### 2.1 测试层次

```
┌─────────────────────────────────────────────────────────┐
│                    测试金字塔                            │
├─────────────────────────────────────────────────────────┤
│                      ┌─────┐                            │
│                     /       \                           │
│                    /  E2E   \    端到端测试 (10%)        │
│                   /─────────\                           │
│                  /           \                          │
│                 /  Integration\  集成测试 (20%)          │
│                /───────────────\                        │
│               /                 \                       │
│              /      Unit         \  单元测试 (70%)       │
│             /─────────────────────\                     │
└─────────────────────────────────────────────────────────┘
```

### 2.2 测试类型定义

#### 单元测试 (Unit Tests)
- **目标**: 验证单个类/方法的功能正确性
- **框架**: JUnit 5 + AssertJ
- **覆盖率目标**: ≥ 80%
- **执行时机**: 每次代码提交

#### 集成测试 (Integration Tests)
- **目标**: 验证组件间协作正确性
- **框架**: JUnit 5
- **覆盖范围**: 场景×策略组合执行
- **执行时机**: 每次构建

#### 端到端测试 (E2E Tests)
- **目标**: 验证完整测试流程
- **框架**: JUnit 5 + 命令行执行
- **覆盖范围**: 完整基准测试运行
- **执行时机**: 每次合并前

#### 性能验证测试 (Performance Tests)
- **目标**: 验证性能指标和框架开销
- **框架**: 自定义 + JFR
- **覆盖范围**: 框架开销、资源使用
- **执行时机**: 每周

### 2.3 测试优先级

| 优先级 | 测试内容 | 必须通过 | 执行频率 |
|:------:|---------|:-------:|:-------:|
| **P0** | 核心接口测试 | ✅ | 每次提交 |
| **P0** | 场景执行测试 | ✅ | 每次提交 |
| **P0** | 策略执行测试 | ✅ | 每次提交 |
| **P1** | 运行器集成测试 | ✅ | 每次构建 |
| **P1** | 指标计算测试 | ✅ | 每次提交 |
| **P2** | 报告生成测试 | - | 每次合并 |
| **P2** | 性能验证测试 | - | 每周 |

---

## 3. 详细测试用例设计

### 3.1 核心接口测试

#### Scenario 接口测试

| 测试 ID | 测试名称 | 测试类型 | 优先级 | 描述 |
|--------|---------|---------|:------:|------|
| UT-SCN-001 | testGetName | 单元测试 | P0 | 验证场景名称返回正确 |
| UT-SCN-002 | testGetDescription | 单元测试 | P0 | 验证场景描述返回正确 |
| UT-SCN-003 | testCreateTasks | 单元测试 | P0 | 验证任务列表创建正确 |
| UT-SCN-004 | testCreateTasks_ZeroCount | 单元测试 | P0 | 验证任务数为 0 时的行为 |
| UT-SCN-005 | testCreateTasks_NegativeCount | 单元测试 | P0 | 验证负数任务数抛出异常 |
| UT-SCN-006 | testExecute | 集成测试 | P0 | 验证场景执行返回结果 |
| UT-SCN-007 | testVerify | 单元测试 | P0 | 验证结果正确性检查 |

#### ThreadStrategy 接口测试

| 测试 ID | 测试名称 | 测试类型 | 优先级 | 描述 |
|--------|---------|---------|:------:|------|
| UT-STR-001 | testGetName | 单元测试 | P0 | 验证策略名称返回正确 |
| UT-STR-002 | testGetDescription | 单元测试 | P0 | 验证策略描述返回正确 |
| UT-STR-003 | testCreateExecutor | 单元测试 | P0 | 验证执行器创建成功 |
| UT-STR-004 | testShutdown | 单元测试 | P0 | 验证执行器正确关闭 |
| UT-STR-005 | testShutdown_Twice | 单元测试 | P1 | 验证重复关闭不抛异常 |
| UT-STR-006 | testGetDefaultThreadCount | 单元测试 | P1 | 验证默认线程数返回正确 |

### 3.2 场景实现测试

#### CpuBoundScenario 测试

| 测试 ID | 测试名称 | 测试类型 | 优先级 | 描述 |
|--------|---------|---------|:------:|------|
| UT-CPU-001 | testMatrixMultiplication | 单元测试 | P0 | 验证矩阵乘法计算正确 |
| UT-CPU-002 | testMatrixResultVerification | 单元测试 | P0 | 验证结果验证逻辑 |
| UT-CPU-003 | testExecute_SingleTask | 集成测试 | P0 | 验证单任务执行 |
| UT-CPU-004 | testExecute_MultipleTasks | 集成测试 | P0 | 验证多任务并发执行 |
| UT-CPU-005 | testExecute_ResultAccuracy | 集成测试 | P1 | 验证执行结果准确性 |
| IT-CPU-001 | testWithPlatformThreads | 集成测试 | P0 | 验证与平台线程配合 |
| IT-CPU-002 | testWithVirtualThreads | 集成测试 | P0 | 验证与虚拟线程配合 |
| PT-CPU-001 | testPerformanceBaseline | 性能测试 | P2 | 建立性能基线 |

#### IoBoundScenario 测试

| 测试 ID | 测试名称 | 测试类型 | 优先级 | 描述 |
|--------|---------|---------|:------:|------|
| UT-IO-001 | testMemoryIoWrite | 单元测试 | P0 | 验证内存写入操作 |
| UT-IO-002 | testMemoryIoRead | 单元测试 | P0 | 验证内存读取操作 |
| UT-IO-003 | testIoTaskExecution | 单元测试 | P0 | 验证 I/O 任务执行 |
| UT-IO-004 | testExecute_SingleTask | 集成测试 | P0 | 验证单任务执行 |
| UT-IO-005 | testExecute_MultipleTasks | 集成测试 | P0 | 验证多任务并发执行 |
| IT-IO-001 | testWithPlatformThreads | 集成测试 | P0 | 验证与平台线程配合 |
| IT-IO-002 | testWithVirtualThreads | 集成测试 | P0 | 验证与虚拟线程配合 |
| PT-IO-001 | testPerformanceBaseline | 性能测试 | P2 | 建立性能基线 |
| PT-IO-002 | testVirtualThreadAdvantage | 性能测试 | P2 | 验证虚拟线程优势（≥20% 提升） |

### 3.3 策略实现测试

#### PlatformThreadStrategy 测试

| 测试 ID | 测试名称 | 测试类型 | 优先级 | 描述 |
|--------|---------|---------|:------:|------|
| UT-PLAT-001 | testExecutorCreation | 单元测试 | P0 | 验证线程池创建 |
| UT-PLAT-002 | testThreadCount | 单元测试 | P0 | 验证线程数配置 |
| UT-PLAT-003 | testShutdown | 单元测试 | P0 | 验证关闭后无法提交任务 |
| UT-PLAT-004 | testResourceCleanup | 单元测试 | P1 | 验证资源正确释放 |
| IT-PLAT-001 | testTaskExecution | 集成测试 | P0 | 验证任务可执行 |
| IT-PLAT-002 | testConcurrentExecution | 集成测试 | P0 | 验证并发执行能力 |

#### VirtualThreadStrategy 测试

| 测试 ID | 测试名称 | 测试类型 | 优先级 | 描述 |
|--------|---------|---------|:------:|------|
| UT-VIRT-001 | testExecutorCreation | 单元测试 | P0 | 验证虚拟线程执行器创建 |
| UT-VIRT-002 | testJavaVersionCheck | 单元测试 | P0 | 验证 Java 21+ 检查 |
| UT-VIRT-003 | testShutdown | 单元测试 | P0 | 验证关闭行为 |
| UT-VIRT-004 | testDefaultThreadCount | 单元测试 | P1 | 验证默认线程数（MAX_VALUE） |
| IT-VIRT-001 | testTaskExecution | 集成测试 | P0 | 验证任务可执行 |
| IT-VIRT-002 | testHighConcurrency | 集成测试 | P1 | 验证高并发能力 |
| IT-VIRT-003 | testThreadIdentity | 集成测试 | P1 | 验证线程为虚拟线程 |

### 3.4 运行器测试

#### BenchmarkRunner 测试

| 测试 ID | 测试名称 | 测试类型 | 优先级 | 描述 |
|--------|---------|---------|:------:|------|
| UT-RUN-001 | testRun_BasicExecution | 单元测试 | P0 | 验证基本执行流程 |
| UT-RUN-002 | testRun_WarmupPhase | 单元测试 | P0 | 验证预热阶段执行 |
| UT-RUN-003 | testRun_BenchmarkPhase | 单元测试 | P0 | 验证基准测试阶段 |
| UT-RUN-004 | testRun_MultipleIterations | 单元测试 | P0 | 验证多次迭代执行 |
| UT-RUN-005 | testRun_ResultAggregation | 单元测试 | P1 | 验证结果聚合正确 |
| UT-RUN-006 | testRun_ResourceCleanup | 单元测试 | P1 | 验证资源清理 |
| IT-RUN-001 | testRunWithCpuScenario | 集成测试 | P0 | 验证与 CPU 场景配合 |
| IT-RUN-002 | testRunWithIoScenario | 集成测试 | P0 | 验证与 I/O 场景配合 |
| IT-RUN-003 | testRunWithAllStrategies | 集成测试 | P0 | 验证与所有策略配合 |
| E2E-RUN-001 | testFullBenchmarkRun | E2E 测试 | P1 | 验证完整基准测试流程 |

### 3.5 指标收集测试

#### MetricCollector 测试

| 测试 ID | 测试名称 | 测试类型 | 优先级 | 描述 |
|--------|---------|---------|:------:|------|
| UT-MET-001 | testThroughputCalculation | 单元测试 | P0 | 验证吞吐量计算正确 |
| UT-MET-002 | testAverageLatencyCalculation | 单元测试 | P0 | 验证平均延迟计算 |
| UT-MET-003 | testPercentileCalculation | 单元测试 | P0 | 验证百分位计算 |
| UT-MET-004 | testP50Calculation | 单元测试 | P0 | 验证 P50 计算准确 |
| UT-MET-005 | testP95Calculation | 单元测试 | P0 | 验证 P95 计算准确 |
| UT-MET-006 | testP99Calculation | 单元测试 | P0 | 验证 P99 计算准确 |
| UT-MET-007 | testAggregation | 单元测试 | P1 | 验证多轮结果聚合 |
| UT-MET-008 | testEmptyResultHandling | 单元测试 | P1 | 验证空结果处理 |

### 3.6 配置管理测试

#### BenchmarkConfig 测试

| 测试 ID | 测试名称 | 测试类型 | 优先级 | 描述 |
|--------|---------|---------|:------:|------|
| UT-CFG-001 | testDefaultConfig | 单元测试 | P0 | 验证默认配置值 |
| UT-CFG-002 | testBuilderPattern | 单元测试 | P0 | 验证 Builder 模式 |
| UT-CFG-003 | testCustomConfig | 单元测试 | P1 | 验证自定义配置 |
| UT-CFG-004 | testValidation | 单元测试 | P1 | 验证配置校验 |
| UT-CFG-005 | testImmutable | 单元测试 | P1 | 验证配置不可变 |

### 3.7 报告生成测试

#### ConsoleReporter 测试

| 测试 ID | 测试名称 | 测试类型 | 优先级 | 描述 |
|--------|---------|---------|:------:|------|
| UT-CONS-001 | testReportOutput | 单元测试 | P1 | 验证控制台输出格式 |
| UT-CONS-002 | testReportContent | 单元测试 | P1 | 验证报告内容完整 |
| IT-CONS-001 | testWithRealResult | 集成测试 | P1 | 验证与真实结果配合 |

#### MarkdownReporter 测试

| 测试 ID | 测试名称 | 测试类型 | 优先级 | 描述 |
|--------|---------|---------|:------:|------|
| UT-MD-001 | testFileGeneration | 单元测试 | P2 | 验证 Markdown 文件生成 |
| UT-MD-002 | testFileContent | 单元测试 | P2 | 验证文件内容格式 |
| UT-MD-003 | testTableFormat | 单元测试 | P2 | 验证表格格式正确 |
| IT-MD-001 | testWithMultipleResults | 集成测试 | P2 | 验证多结果报告 |
| E2E-MD-001 | testFullReportGeneration | E2E 测试 | P2 | 验证完整报告生成流程 |

### 3.8 端到端测试

| 测试 ID | 测试名称 | 测试类型 | 优先级 | 描述 |
|--------|---------|---------|:------:|------|
| E2E-001 | testFullCpuBenchmark | E2E 测试 | P0 | 完整 CPU 场景基准测试 |
| E2E-002 | testFullIoBenchmark | E2E 测试 | P0 | 完整 I/O 场景基准测试 |
| E2E-003 | testAllCombinations | E2E 测试 | P1 | 所有场景×策略组合 |
| E2E-004 | testBenchmarkApp | E2E 测试 | P1 | 主程序入口测试 |
| E2E-005 | testReportGeneration | E2E 测试 | P2 | 完整报告生成流程 |

### 3.9 性能验证测试

| 测试 ID | 测试名称 | 测试类型 | 优先级 | 描述 |
|--------|---------|---------|:------:|------|
| PT-001 | testFrameworkOverhead | 性能测试 | P2 | 验证框架开销 ≤ 5% |
| PT-002 | testSingleTestDuration | 性能测试 | P2 | 验证单次测试 ≤ 60 秒 |
| PT-003 | testMemoryUsage | 性能测试 | P2 | 验证内存占用 ≤ 256MB |
| PT-004 | testVirtualThreadImprovement | 性能测试 | P2 | 验证虚拟线程提升 ≥ 20% (I/O 场景) |
| PT-005 | testResultStability | 性能测试 | P2 | 验证结果稳定性（方差 < 10%） |

### 3.10 边界情况测试

| 测试 ID | 测试名称 | 测试类型 | 优先级 | 描述 |
|--------|---------|---------|:------:|------|
| BC-001 | testZeroTaskCount | 边界测试 | P1 | 验证 0 任务数处理 |
| BC-002 | testNegativeTaskCount | 边界测试 | P1 | 验证负数任务数抛出异常 |
| BC-003 | testLargeTaskCount | 边界测试 | P1 | 验证大任务数处理 |
| BC-004 | testTimeout | 边界测试 | P1 | 验证超时处理 |
| BC-005 | testConcurrentModification | 边界测试 | P1 | 验证并发修改安全 |
| BC-006 | testResourceExhaustion | 边界测试 | P2 | 验证资源耗尽处理 |
| BC-007 | testJavaVersionBelow21 | 边界测试 | P1 | 验证 Java 版本检查 |
| BC-008 | testInterruptedExecution | 边界测试 | P1 | 验证中断处理 |

---

## 4. 测试任务分解

### 4.1 任务概览

| 波次 | 任务数 | 复杂度分布 | 说明 |
|------|:------:|:----------:|------|
| Wave 1 | 5 | 3S, 2M | 核心接口和配置 |
| Wave 2 | 5 | 2S, 3M | 场景和策略实现测试 |
| Wave 3 | 4 | 1S, 3M | 运行器和指标测试 |
| Wave 4 | 3 | 1S, 2M | 报告生成测试 |
| Wave 5 | 3 | 3M | 端到端和性能测试 |

### 4.2 详细任务列表

---

## TASK-001: 核心接口测试实现

**复杂度**: M  
**前置依赖**: 无  
**执行波次**: 1  

### 描述
实现 Scenario 和 ThreadStrategy 接口的单元测试，包括所有基本方法的测试和边界条件测试。

### 涉及文件
- [NEW] src/test/java/com/threadmg/benchmark/core/ScenarioTest.java
- [NEW] src/test/java/com/threadmg/benchmark/core/ThreadStrategyTest.java
- [NEW] src/test/java/com/threadmg/benchmark/core/MockScenario.java
- [NEW] src/test/java/com/threadmg/benchmark/core/MockThreadStrategy.java

### 验收标准
- [ ] Scenario 接口测试覆盖率 ≥ 85%
- [ ] ThreadStrategy 接口测试覆盖率 ≥ 85%
- [ ] 所有测试用例通过
- [ ] 包含边界条件测试（0 任务数、负数等）

### 验证命令
```bash
mvn test -Dtest=ScenarioTest,ThreadStrategyTest
```

---

## TASK-002: 数据模型测试实现

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 1  

### 描述
实现 BenchmarkResult、ScenarioResult 数据模型的测试，验证数据存取和计算方法正确性。

### 涉及文件
- [NEW] src/test/java/com/threadmg/benchmark/core/BenchmarkResultTest.java
- [NEW] src/test/java/com/threadmg/benchmark/core/ScenarioResultTest.java

### 验收标准
- [ ] 所有 getter 方法测试通过
- [ ] 计算方法（throughput, latency）测试通过
- [ ] 百分位计算测试通过
- [ ] 测试覆盖率 ≥ 90%

### 验证命令
```bash
mvn test -Dtest=BenchmarkResultTest,ScenarioResultTest
```

---

## TASK-003: 配置管理测试实现

**复杂度**: S  
**前置依赖**: 无  
**执行波次**: 1  

### 描述
实现 BenchmarkConfig 的测试，验证 Builder 模式、默认值和配置校验逻辑。

### 涉及文件
- [NEW] src/test/java/com/threadmg/benchmark/config/BenchmarkConfigTest.java

### 验收标准
- [ ] Builder 模式测试通过
- [ ] 默认配置值验证通过
- [ ] 配置校验逻辑测试通过
- [ ] 不可变性测试通过

### 验证命令
```bash
mvn test -Dtest=BenchmarkConfigTest
```

---

## TASK-004: CPU 场景测试实现

**复杂度**: M  
**前置依赖**: TASK-001, TASK-002  
**执行波次**: 2  

### 描述
实现 CpuBoundScenario 的单元测试和集成测试，验证矩阵乘法计算正确性和并发执行能力。

### 涉及文件
- [NEW] src/test/java/com/threadmg/scenarios/cpu/CpuBoundScenarioTest.java
- [NEW] src/test/java/com/threadmg/scenarios/cpu/MatrixMultiplicationTaskTest.java

### 验收标准
- [ ] 矩阵乘法计算结果正确性验证
- [ ] 单任务执行测试通过
- [ ] 多任务并发执行测试通过
- [ ] 与平台线程配合测试通过
- [ ] 与虚拟线程配合测试通过

### 验证命令
```bash
mvn test -Dtest=CpuBoundScenarioTest
```

---

## TASK-005: I/O 场景测试实现

**复杂度**: M  
**前置依赖**: TASK-001, TASK-002  
**执行波次**: 2  

### 描述
实现 IoBoundScenario 的单元测试和集成测试，验证内存 I/O 模拟的正确性和并发执行能力。

### 涉及文件
- [NEW] src/test/java/com/threadmg/scenarios/io/IoBoundScenarioTest.java
- [NEW] src/test/java/com/threadmg/scenarios/io/SimulatedIoTaskTest.java

### 验收标准
- [ ] 内存 I/O 操作测试通过
- [ ] 单任务执行测试通过
- [ ] 多任务并发执行测试通过
- [ ] 与平台线程配合测试通过
- [ ] 与虚拟线程配合测试通过

### 验证命令
```bash
mvn test -Dtest=IoBoundScenarioTest
```

---

## TASK-006: 平台线程策略测试实现

**复杂度**: M  
**前置依赖**: TASK-001  
**执行波次**: 2  

### 描述
实现 PlatformThreadStrategy 的测试，验证线程池创建、任务执行和资源清理。

### 涉及文件
- [NEW] src/test/java/com/threadmg/threads/platform/PlatformThreadStrategyTest.java

### 验收标准
- [ ] 执行器创建测试通过
- [ ] 线程数配置验证通过
- [ ] 关闭行为测试通过
- [ ] 资源清理验证通过
- [ ] 并发执行能力测试通过

### 验证命令
```bash
mvn test -Dtest=PlatformThreadStrategyTest
```

---

## TASK-007: 虚拟线程策略测试实现

**复杂度**: M  
**前置依赖**: TASK-001  
**执行波次**: 2  

### 描述
实现 VirtualThreadStrategy 的测试，验证虚拟线程执行器创建、高并发能力和资源清理。

### 涉及文件
- [NEW] src/test/java/com/threadmg/threads/virtual/VirtualThreadStrategyTest.java

### 验收标准
- [ ] 虚拟线程执行器创建测试通过
- [ ] Java 21+ 版本检查测试通过
- [ ] 高并发能力测试通过
- [ ] 线程身份验证（确认是虚拟线程）
- [ ] 资源清理验证通过

### 验证命令
```bash
mvn test -Dtest=VirtualThreadStrategyTest
```

---

## TASK-008: 运行器测试实现

**复杂度**: M  
**前置依赖**: TASK-004, TASK-005, TASK-006, TASK-007  
**执行波次**: 3  

### 描述
实现 BenchmarkRunner 的测试，验证预热、迭代执行、结果聚合和资源清理逻辑。

### 涉及文件
- [NEW] src/test/java/com/threadmg/benchmark/core/BenchmarkRunnerTest.java

### 验收标准
- [ ] 基本执行流程测试通过
- [ ] 预热阶段测试通过
- [ ] 多次迭代执行测试通过
- [ ] 结果聚合正确性验证
- [ ] 资源清理验证通过
- [ ] 与所有场景和策略组合测试通过

### 验证命令
```bash
mvn test -Dtest=BenchmarkRunnerTest
```

---

## TASK-009: 指标收集器测试实现

**复杂度**: M  
**前置依赖**: TASK-002  
**执行波次**: 3  

### 描述
实现 MetricCollector 的测试，验证吞吐量、延迟、百分位计算的准确性。

### 涉及文件
- [NEW] src/test/java/com/threadmg/benchmark/metrics/MetricCollectorTest.java

### 验收标准
- [ ] 吞吐量计算与手工计算对比误差 < 1%
- [ ] 平均延迟计算测试通过
- [ ] P50/P95/P99 百分位计算测试通过
- [ ] 多轮结果聚合测试通过
- [ ] 空结果处理测试通过

### 验证命令
```bash
mvn test -Dtest=MetricCollectorTest
```

---

## TASK-010: 控制台报告测试实现

**复杂度**: S  
**前置依赖**: TASK-008, TASK-009  
**执行波次**: 4  

### 描述
实现 ConsoleReporter 的测试，验证控制台输出格式和内容完整性。

### 涉及文件
- [NEW] src/test/java/com/threadmg/reporters/ConsoleReporterTest.java

### 验收标准
- [ ] 控制台输出格式正确
- [ ] 所有关键指标都输出
- [ ] 与真实结果配合测试通过

### 验证命令
```bash
mvn test -Dtest=ConsoleReporterTest
```

---

## TASK-011: Markdown 报告测试实现

**复杂度**: M  
**前置依赖**: TASK-008, TASK-009  
**执行波次**: 4  

### 描述
实现 MarkdownReporter 的测试，验证 Markdown 文件生成、格式和内容。

### 涉及文件
- [NEW] src/test/java/com/threadmg/reporters/MarkdownReporterTest.java

### 验收标准
- [ ] Markdown 文件生成成功
- [ ] 表格格式正确
- [ ] 所有指标包含在报告中
- [ ] 多结果报告测试通过
- [ ] 文件保存到正确目录

### 验证命令
```bash
mvn test -Dtest=MarkdownReporterTest
```

---

## TASK-012: 端到端测试实现

**复杂度**: M  
**前置依赖**: TASK-008, TASK-010, TASK-011  
**执行波次**: 5  

### 描述
实现完整的端到端测试，验证从启动到报告生成的完整流程。

### 涉及文件
- [NEW] src/test/java/com/threadmg/e2e/BenchmarkE2ETest.java
- [NEW] src/test/java/com/threadmg/e2e/ReportGenerationE2ETest.java

### 验收标准
- [ ] 完整 CPU 场景基准测试流程通过
- [ ] 完整 I/O 场景基准测试流程通过
- [ ] 所有场景×策略组合测试通过
- [ ] 报告生成流程测试通过
- [ ] BenchmarkApp 入口测试通过

### 验证命令
```bash
mvn test -Dtest=*E2ETest
```

---

## TASK-013: 性能验证测试实现

**复杂度**: M  
**前置依赖**: TASK-012  
**执行波次**: 5  

### 描述
实现性能验证测试，验证框架开销、资源使用和虚拟线程性能优势。

### 涉及文件
- [NEW] src/test/java/com/threadmg/performance/FrameworkOverheadTest.java
- [NEW] src/test/java/com/threadmg/performance/ResourceUsageTest.java
- [NEW] src/test/java/com/threadmg/performance/VirtualThreadAdvantageTest.java

### 验收标准
- [ ] 框架开销测试通过（≤ 5%）
- [ ] 单次测试时长测试通过（≤ 60 秒）
- [ ] 内存占用测试通过（≤ 256MB）
- [ ] 虚拟线程优势测试通过（I/O 场景 ≥ 20% 提升）
- [ ] 结果稳定性测试通过（方差 < 10%）

### 验证命令
```bash
mvn test -Dtest=*PerformanceTest
```

---

## TASK-014: 边界情况测试实现

**复杂度**: S  
**前置依赖**: TASK-001  
**执行波次**: 3  

### 描述
实现边界情况和异常处理测试，确保系统在各种极端情况下的健壮性。

### 涉及文件
- [NEW] src/test/java/com/threadmg/benchmark/core/BoundaryConditionsTest.java

### 验收标准
- [ ] 0 任务数处理测试通过
- [ ] 负数任务数异常抛出测试通过
- [ ] 大任务数处理测试通过
- [ ] 超时处理测试通过
- [ ] 并发修改安全测试通过
- [ ] Java 版本检查测试通过
- [ ] 中断处理测试通过

### 验证命令
```bash
mvn test -Dtest=BoundaryConditionsTest
```

---

## TASK-015: 测试基础设施配置

**复杂度**: M  
**前置依赖**: 无  
**执行波次**: 1  

### 描述
配置测试基础设施，包括 pom.xml 测试依赖、Maven Surefire 配置、JaCoCo 覆盖率配置。

### 涉及文件
- [NEW] pom.xml
- [MODIFY] .gitignore (添加测试相关忽略)

### 验收标准
- [ ] JUnit 5 依赖配置正确
- [ ] AssertJ 依赖配置正确
- [ ] JaCoCo 插件配置正确（覆盖率 ≥ 80%）
- [ ] Maven Surefire 配置正确
- [ ] 测试目录结构正确

### 验证命令
```bash
mvn test-compile
mvn jacoco:check
```

---

## 5. 测试执行计划

### 5.1 执行顺序

```
Wave 1 (Day 1-2)          Wave 2 (Day 3-4)          Wave 3 (Day 5-6)
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│ TASK-001        │      │ TASK-004        │      │ TASK-008        │
│ TASK-002        │      │ TASK-005        │      │ TASK-009        │
│ TASK-003        │      │ TASK-006        │      │ TASK-014        │
│ TASK-015        │      │ TASK-007        │      └─────────────────┘
└────────┬────────┘      └────────┬────────┘
         │                        │
         ▼                        ▼
Wave 4 (Day 7-8)          Wave 5 (Day 9-10)
┌─────────────────┐      ┌─────────────────┐
│ TASK-010        │      │ TASK-012        │
│ TASK-011        │      │ TASK-013        │
└─────────────────┘      └─────────────────┘
```

### 5.2 每日执行清单

| 日期 | 任务 | 预期输出 |
|------|------|----------|
| Day 1 | TASK-001, TASK-015 | 核心接口测试、项目配置 |
| Day 2 | TASK-002, TASK-003 | 数据模型和配置测试 |
| Day 3 | TASK-004, TASK-006 | CPU 场景和平台线程测试 |
| Day 4 | TASK-005, TASK-007 | I/O 场景和虚拟线程测试 |
| Day 5 | TASK-008, TASK-014 | 运行器和边界测试 |
| Day 6 | TASK-009 | 指标收集器测试 |
| Day 7 | TASK-010 | 控制台报告测试 |
| Day 8 | TASK-011 | Markdown 报告测试 |
| Day 9 | TASK-012 | 端到端测试 |
| Day 10 | TASK-013 | 性能验证测试 |

---

## 6. 测试工具与框架

### 6.1 推荐工具栈

| 类别 | 工具 | 版本 | 用途 |
|------|------|------|------|
| **测试框架** | JUnit 5 | 5.10+ | 单元测试、集成测试 |
| **断言库** | AssertJ | 3.24+ | 流式断言 |
| **Mock 框架** | Mockito | 5.7+ | 模拟对象（可选） |
| **覆盖率** | JaCoCo | 0.8.11+ | 代码覆盖率 |
| **构建工具** | Maven | 3.9+ | 项目构建 |
| **性能监控** | JFR | Built-in | 性能分析 |
| **CI/CD** | GitHub Actions | - | 持续集成 |

### 6.2 Maven 依赖配置

```xml
<dependencies>
    <!-- 测试依赖 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>3.24.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.7.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <!-- JaCoCo 覆盖率插件 -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.11</version>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>INSTRUCTION</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 6.3 测试命令速查

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=ClassName

# 运行特定包下的测试
mvn test -Dtest="com.threadmg.benchmark.**"

# 运行并生成覆盖率报告
mvn clean test jacoco:report

# 运行并检查覆盖率
mvn clean test jacoco:check

# 运行单个测试方法
mvn test -Dtest=ClassName#methodName

# 跳过测试
mvn package -DskipTests

# 运行性能测试
mvn test -Dtest=*PerformanceTest
```

---

## 7. 测试覆盖率目标

### 7.1 覆盖率要求

| 模块 | 行覆盖率 | 分支覆盖率 | 类覆盖率 |
|------|:-------:|:---------:|:-------:|
| **核心框架** | ≥ 85% | ≥ 80% | 100% |
| **场景实现** | ≥ 85% | ≥ 80% | 100% |
| **策略实现** | ≥ 85% | ≥ 80% | 100% |
| **指标收集** | ≥ 90% | ≥ 85% | 100% |
| **报告生成** | ≥ 80% | ≥ 75% | 100% |
| **配置管理** | ≥ 85% | ≥ 80% | 100% |
| **整体项目** | ≥ 80% | ≥ 75% | 100% |

### 7.2 覆盖率检查配置

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>INSTRUCTION</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                            <limit>
                                <counter>BRANCH</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.75</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---

## 8. 持续集成配置

### 8.1 GitHub Actions 工作流

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        java-version: [21, 22]
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK ${{ matrix.java-version }}
      uses: actions/setup-java@v4
      with:
        java-version: ${{ matrix.java-version }}
        distribution: 'temurin'
        cache: maven
    
    - name: Run tests
      run: mvn clean test
    
    - name: Check coverage
      run: mvn jacoco:check
    
    - name: Upload coverage report
      uses: codecov/codecov-action@v3
      with:
        files: target/site/jacoco/jacoco.xml

  benchmark:
    needs: test
    runs-on: ubuntu-latest
    if: github.event_name == 'push' && github.ref == 'refs/heads/main'
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: 21
        distribution: 'temurin'
    
    - name: Run benchmarks
      run: mvn test -Dtest=*E2ETest
    
    - name: Upload results
      uses: actions/upload-artifact@v3
      with:
        name: benchmark-results
        path: results/reports/
```

---

## 9. 测试报告模板

### 9.1 测试执行报告

```markdown
# 测试执行报告

## 基本信息
- 报告 ID: TEST-20260327-001
- 执行时间：2026-03-27 14:00:00
- 执行环境：Ubuntu 22.04, JDK 21
- 代码版本：abc123def

## 执行概览
| 类别 | 总数 | 通过 | 失败 | 跳过 | 通过率 |
|------|------|------|------|------|:------:|
| 单元测试 | XX | XX | 0 | 0 | 100% |
| 集成测试 | XX | XX | 0 | 0 | 100% |
| E2E 测试 | XX | XX | 0 | 0 | 100% |
| 性能测试 | XX | XX | 0 | 0 | 100% |
| **总计** | **XX** | **XX** | **0** | **0** | **100%** |

## 覆盖率统计
| 指标 | 目标 | 实际 | 状态 |
|------|------|------|------|
| 行覆盖率 | 80% | XX% | ✅/❌ |
| 分支覆盖率 | 75% | XX% | ✅/❌ |
| 类覆盖率 | 100% | XX% | ✅/❌ |

## 失败测试详情
（无）

## 结论
✅ 所有测试通过，可以合并
```

---

## 10. 风险与缓解

### 10.1 技术风险

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|:------:|:----:|----------|
| **虚拟线程测试不稳定** | 中 | 中 | 增加重试机制，多次运行取平均 |
| **性能测试环境差异** | 中 | 中 | 固定测试环境，使用容器化 |
| **测试执行时间过长** | 低 | 低 | 并行执行独立测试，优化测试数据 |
| **Mock 过度使用** | 低 | 低 | 优先使用真实对象，Mock 仅用于外部依赖 |

### 10.2 进度风险

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|:------:|:----:|----------|
| **测试任务延期** | 中 | 中 | 每日检查进度，及时调整 |
| **测试覆盖率不达标** | 低 | 中 | 早期集成覆盖率检查 |
| **集成测试发现问题多** | 中 | 高 | 提前进行组件联调 |

---

## 11. 总结

### 11.1 测试任务汇总

| 类别 | 任务数 | 测试用例数 | 预计工时 |
|------|:------:|:---------:|:-------:|
| 单元测试 | 8 | ~80 | 4 天 |
| 集成测试 | 4 | ~30 | 2 天 |
| E2E 测试 | 1 | ~10 | 2 天 |
| 性能测试 | 1 | ~10 | 2 天 |
| 基础设施 | 1 | - | 0.5 天 |
| **总计** | **15** | **~130** | **10.5 天** |

### 11.2 关键成功因素

1. **早期测试**: 与开发同步编写测试，确保测试覆盖率
2. **自动化**: 所有测试可自动化执行，集成到 CI/CD
3. **可维护性**: 测试代码与生产代码同等质量
4. **快速反馈**: 测试执行时间控制在合理范围内

### 11.3 下一步行动

1. 创建测试基础设施（TASK-015）
2. 按波次顺序执行测试任务
3. 每日检查测试覆盖率和通过率
4. 完成所有测试后生成测试报告

---

## ✅ 测试计划完成

**Feature**: MVP 基准测试框架  
**状态**: test-plan-ready  
**文件**: `.specs/mvp-test/test-plan.md`

### 测试任务统计

| 指标 | 数量 |
|------|------|
| 总任务数 | 15 个 |
| 单元测试任务 | 8 个 |
| 集成测试任务 | 4 个 |
| E2E 测试任务 | 1 个 |
| 性能测试任务 | 1 个 |
| 基础设施任务 | 1 个 |
| 预计测试用例 | ~130 个 |
| 预计工时 | 10.5 天 |

### 下一步

👉 开始执行测试任务，从 **TASK-015**（测试基础设施配置）开始

### 建议命令

```bash
# 1. 首先创建 pom.xml 和测试配置
# 2. 然后按波次执行测试任务

# 运行所有测试
mvn clean test

# 生成覆盖率报告
mvn jacoco:report

# 检查覆盖率是否达标
mvn jacoco:check
```
