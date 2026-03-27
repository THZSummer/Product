# 测试场景说明

本文档详细描述 thread-mg 项目支持的所有测试场景。

---

## 📊 场景总览

| 场景 | 描述 | 典型用例 | 推荐技术 |
|------|------|----------|----------|
| **CPU 密集型** | 大量计算任务 | 科学计算、图像处理 | Fork/Join、线程池 |
| **I/O 密集型** | 网络/磁盘 I/O 等待 | 文件处理、API 调用 | 虚拟线程、CompletableFuture |
| **高并发短任务** | 大量短时任务并发 | Web 请求、消息处理 | 虚拟线程、线程池 |
| **混合负载** | CPU+I/O 混合场景 | 数据库查询 + 计算 | 虚拟线程、Fork/Join |
| **递归分治** | 递归算法、大数据处理 | 排序、搜索 | Fork/Join |
| **异步流水线** | 多阶段异步处理 | 数据处理管道 | CompletableFuture、Reactive |

---

## 🔥 CPU 密集型 (CPU-Bound)

### 场景描述
以计算为主要工作负载，CPU 利用率高，线程大部分时间在执行计算而非等待。

### 特征
- ✅ CPU 使用率接近 100%
- ✅ 无 I/O 等待
- ✅ 计算密集
- ✅ 适合并行化

### 典型任务
1. **矩阵运算**: 矩阵乘法、矩阵分解
2. **数值计算**: 素数计算、蒙特卡洛模拟
3. **图像处理**: 滤镜应用、图像变换
4. **加密解密**: 哈希计算、加解密

### 推荐技术

| 技术 | 推荐度 | 理由 |
|------|--------|------|
| Fork/Join | ⭐⭐⭐⭐⭐ | 工作窃取，负载均衡 |
| 线程池 | ⭐⭐⭐⭐ | 控制线程数，避免过载 |
| 平台线程 | ⭐⭐⭐ | 适合 CPU 密集，但开销大 |
| 虚拟线程 | ⭐⭐ | 优势不明显 |

### 实现示例
```java
public class CpuBoundScenario implements Scenario {
    // 矩阵乘法任务
    Task<double[][]> matrixTask = new MatrixMultiplicationTask(1000);
    
    // 素数计算任务
    Task<List<Integer>> primeTask = new PrimeNumberTask(1000000);
}
```

---

## 💾 I/O 密集型 (IO-Bound)

### 场景描述
以 I/O 操作为主要工作负载，线程大部分时间在等待 I/O 完成。

### 特征
- ✅ CPU 使用率低
- ✅ 大量 I/O 等待
- ✅ 适合异步处理
- ✅ 高并发优势明显

### 典型任务
1. **文件操作**: 文件读写、目录遍历
2. **网络请求**: HTTP 调用、Socket 通信
3. **数据库操作**: 查询、更新
4. **消息队列**: 消息收发

### 推荐技术

| 技术 | 推荐度 | 理由 |
|------|--------|------|
| 虚拟线程 | ⭐⭐⭐⭐⭐ | 轻量级，高并发优势 |
| CompletableFuture | ⭐⭐⭐⭐ | 异步非阻塞 |
| Reactive | ⭐⭐⭐⭐ | 响应式，背压支持 |
| 线程池 | ⭐⭐⭐ | 资源可控 |

### 实现示例
```java
public class IoBoundScenario implements Scenario {
    // HTTP 请求任务
    Task<String> httpTask = new HttpRequestTask("https://api.example.com");
    
    // 文件读取任务
    Task<byte[]> fileTask = new FileReadTask("/path/to/file");
}
```

---

## ⚡ 高并发短任务 (High-Concurrency)

### 场景描述
大量短生命周期任务同时执行，强调高并发处理能力。

### 特征
- ✅ 任务执行时间短
- ✅ 并发度高
- ✅ 线程切换频繁
- ✅ 调度开销显著

### 典型任务
1. **Web 请求**: HTTP 请求处理
2. **消息处理**: 消息队列消费
3. **事件处理**: 事件驱动任务
4. **RPC 调用**: 远程过程调用

### 推荐技术

| 技术 | 推荐度 | 理由 |
|------|--------|------|
| 虚拟线程 | ⭐⭐⭐⭐⭐ | 轻量级，调度高效 |
| 线程池 | ⭐⭐⭐⭐ | 复用线程，减少开销 |
| CompletableFuture | ⭐⭐⭐ | 异步组合 |
| Reactive | ⭐⭐⭐ | 流式处理 |

### 实现示例
```java
public class HighConcurrencyScenario implements Scenario {
    // 1000 个短任务
    List<Task<?>> tasks = IntStream.range(0, 1000)
        .mapToObj(i -> new ShortTask())
        .collect(Collectors.toList());
}
```

---

## 🔀 混合负载 (Mixed-Load)

### 场景描述
同时包含 CPU 计算和 I/O 等待的混合场景。

### 特征
- ✅ 计算和 I/O 交替
- ✅ 资源利用复杂
- ✅ 需要平衡策略
- ✅ 最接近真实业务

### 典型任务
1. **数据处理**: 读取→计算→写入
2. **图像处理**: 加载→处理→保存
3. **ETL 流程**: 提取→转换→加载
4. **ML 推理**: 数据准备→推理→结果输出

### 推荐技术

| 技术 | 推荐度 | 理由 |
|------|--------|------|
| 虚拟线程 | ⭐⭐⭐⭐⭐ | 灵活适应混合负载 |
| Fork/Join | ⭐⭐⭐⭐ | 计算部分高效 |
| 线程池 | ⭐⭐⭐ | 资源可控 |
| CompletableFuture | ⭐⭐⭐ | 异步编排 |

### 实现示例
```java
public class MixedLoadScenario implements Scenario {
    // ETL 任务：读取→转换→写入
    Task<ETLResult> etlTask = new ETLTask(
        new ReadStep(),
        new TransformStep(),
        new WriteStep()
    );
}
```

---

## 🔁 递归分治 (Recursive)

### 场景描述
使用分治策略，将大问题递归分解为小问题。

### 特征
- ✅ 递归结构
- ✅ 任务依赖
- ✅ 动态负载均衡需求
- ✅ 适合工作窃取

### 典型任务
1. **排序**: 快速排序、归并排序
2. **搜索**: 二分搜索、树遍历
3. **数值计算**: 斐波那契、阶乘
4. **图算法**: BFS、DFS

### 推荐技术

| 技术 | 推荐度 | 理由 |
|------|--------|------|
| Fork/Join | ⭐⭐⭐⭐⭐ | 专为递归设计 |
| 平台线程 | ⭐ | 不适用 |
| 虚拟线程 | ⭐ | 不适用 |

### 实现示例
```java
public class RecursiveScenario implements Scenario {
    // 快速排序任务
    Task<int[]> sortTask = new QuickSortTask(data, 0, data.length - 1);
    
    // 斐波那契任务
    Task<Long> fibTask = new FibonacciTask(40);
}
```

---

## 🔗 异步流水线 (Async-Pipeline)

### 场景描述
多阶段异步处理，数据在阶段间流动。

### 特征
- ✅ 阶段化
- ✅ 数据流
- ✅ 异步编排
- ✅ 背压需求

### 典型任务
1. **数据处理管道**: 过滤→转换→聚合
2. **流式处理**: 读取→解析→处理→输出
3. **工作流**: 步骤 1→步骤 2→步骤 3
4. **反应式系统**: 事件→处理→响应

### 推荐技术

| 技术 | 推荐度 | 理由 |
|------|--------|------|
| CompletableFuture | ⭐⭐⭐⭐⭐ | 链式编排 |
| Reactive | ⭐⭐⭐⭐⭐ | 响应式流 |
| 虚拟线程 | ⭐⭐ | 可用于阶段内 |
| 线程池 | ⭐⭐ | 资源控制 |

### 实现示例
```java
public class AsyncPipelineScenario implements Scenario {
    // 数据处理管道
    Task<Result> pipelineTask = CompletableFuture
        .supplyAsync(this::read)
        .thenApply(this::parse)
        .thenApply(this::transform)
        .thenApply(this::aggregate);
}
```

---

## 📐 场景选择指南

### 决策树

```
你的任务主要做什么？
│
├─ 大量计算？
│  └─ 是 → CPU 密集型
│      └─ 有递归结构？
│          ├─ 是 → 递归分治
│          └─ 否 → CPU 密集型
│
├─ 大量 I/O 等待？
│  └─ 是 → I/O 密集型
│
├─ 大量短任务？
│  └─ 是 → 高并发短任务
│
├─ 多阶段处理？
│  └─ 是 → 异步流水线
│
└─ 混合类型？
   └─ 是 → 混合负载
```

### 场景对比表

| 维度 | CPU 密集 | I/O 密集 | 高并发 | 混合 | 递归 | 流水线 |
|------|---------|---------|--------|------|------|--------|
| CPU 使用 | 高 | 低 | 中 | 中 | 高 | 中 |
| I/O 使用 | 低 | 高 | 中 | 中 | 低 | 中 |
| 内存使用 | 中 | 低 | 高 | 中 | 中 | 中 |
| 线程数 | 少 | 多 | 很多 | 中 | 中 | 中 |
| 任务时长 | 长 | 长 | 短 | 长 | 长 | 中 |

---

## 🔗 相关资源

- [线程技术说明](../threads/)
- [工程矩阵](../../README.md#-工程结构矩阵)
- [实现指南](../implementation/README.md)

---

*文档版本：1.0 | 最后更新：2026-03-27*
