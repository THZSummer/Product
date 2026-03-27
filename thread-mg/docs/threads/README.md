# 线程技术说明

本文档详细描述 thread-mg 项目支持的所有线程技术。

---

## 📊 技术总览

| 技术 | Java 版本 | 核心特点 | 适用场景 |
|------|----------|---------|----------|
| **平台线程** | 8+ | OS 线程，1:1 映射 | CPU 密集型 |
| **虚拟线程** | 21+ | 轻量级，M:N 映射 | I/O 密集型 |
| **线程池** | 5+ | 资源复用 | 通用 |
| **Fork/Join** | 7+ | 工作窃取 | 递归分治 |
| **CompletableFuture** | 8+ | 异步编排 | 异步流水线 |
| **Reactive Streams** | 9+ | 响应式流 | 流式处理 |

---

## 🧵 平台线程 (Platform Threads)

### 概述
平台线程是传统的 Java 线程，与操作系统线程 1:1 映射。

### 特点
- ✅ **真实 OS 线程**: 直接映射到操作系统线程
- ✅ **抢占式调度**: 由操作系统调度
- ✅ **重量级**: 创建和切换开销大
- ✅ **栈内存大**: 默认 1MB 栈空间

### 优势
1. 成熟稳定，广泛使用
2. 适合 CPU 密集型任务
3. 调试工具完善
4. 行为可预测

### 劣势
1. 创建开销大
2. 内存占用高
3. 并发数受限
4. 上下文切换成本高

### 适用场景
- CPU 密集型计算
- 线程数可控的场景
- 需要精细控制的场景

### 使用示例
```java
// 创建平台线程
Thread thread = new Thread(() -> {
    // 任务逻辑
    System.out.println("Running in: " + Thread.currentThread());
});
thread.start();
thread.join();

// 使用线程池
ExecutorService executor = Executors.newFixedThreadPool(4);
executor.submit(() -> {
    // 任务逻辑
});
executor.shutdown();
```

### 性能特征
| 指标 | 值 |
|------|-----|
| 创建时间 | ~1ms |
| 栈内存 | 1MB (默认) |
| 切换开销 | 高 |
| 最大数量 | 数千 |

---

## 🪶 虚拟线程 (Virtual Threads)

### 概述
虚拟线程是 Java 21 引入的轻量级线程，由 JVM 管理，M:N 映射到平台线程。

### 特点
- ✅ **轻量级**: 创建开销极小
- ✅ **用户态调度**: 由 JVM 调度
- ✅ **栈内存小**: 初始仅几百字节
- ✅ **高并发**: 可创建百万级

### 优势
1. 极高的并发能力
2. 编程模型简单（同步代码）
3. I/O 密集型场景性能优异
4. 与现有 API 兼容

### 劣势
1. 需要 Java 21+
2. CPU 密集型场景优势不明显
3. 某些原生库可能不兼容
4. 调试工具待完善

### 适用场景
- I/O 密集型任务
- 高并发短任务
- 需要简单同步代码的场景

### 使用示例
```java
// Java 21+ 虚拟线程
// 方式 1: 使用 Thread.ofVirtual()
Thread virtualThread = Thread.ofVirtual().start(() -> {
    System.out.println("Running in: " + Thread.currentThread());
});
virtualThread.join();

// 方式 2: 使用 Executors 工厂方法
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> {
        // 任务逻辑
    });
}

// 方式 3: 使用 Thread.startVirtualThread()
Thread.startVirtualThread(() -> {
    // 任务逻辑
});
```

### 性能特征
| 指标 | 值 |
|------|-----|
| 创建时间 | ~1μs |
| 栈内存 | ~1KB (初始) |
| 切换开销 | 低 |
| 最大数量 | 百万级 |

---

## 🏊 线程池 (Thread Pool)

### 概述
线程池通过复用线程来减少创建开销，控制并发线程数量。

### 特点
- ✅ **线程复用**: 减少创建销毁开销
- ✅ **资源控制**: 限制最大并发数
- ✅ **任务队列**: 缓冲任务
- ✅ **灵活配置**: 可配置核心/最大线程数

### 优势
1. 资源可控
2. 性能稳定
3. 避免线程过多导致的问题
4. 配置灵活

### 劣势
1. 需要合理配置参数
2. 队列可能成为瓶颈
3. 线程泄漏风险
4. 配置不当可能死锁

### 适用场景
- 通用场景
- 需要控制资源的场景
- 任务数量波动大的场景

### 使用示例
```java
// 固定大小线程池
ExecutorService fixedPool = Executors.newFixedThreadPool(10);

// 可缓存线程池
ExecutorService cachedPool = Executors.newCachedThreadPool();

// 自定义线程池
ThreadPoolExecutor customPool = new ThreadPoolExecutor(
    4,                      // 核心线程数
    16,                     // 最大线程数
    60L, TimeUnit.SECONDS,  // 空闲超时
    new LinkedBlockingQueue<>(100),  // 任务队列
    new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略
);

// 提交任务
Future<?> future = customPool.submit(() -> {
    // 任务逻辑
});

// 关闭
customPool.shutdown();
```

### 性能特征
| 指标 | 值 |
|------|-----|
| 创建时间 | 一次性 |
| 栈内存 | N × 1MB |
| 切换开销 | 中 |
| 最大数量 | 可配置 |

---

## 🍴 Fork/Join 框架

### 概述
Fork/Join 框架使用工作窃取算法，适合递归分治任务。

### 特点
- ✅ **工作窃取**: 空闲线程窃取其他线程任务
- ✅ **分治策略**: 大任务分解为小任务
- ✅ **递归结构**: 适合递归算法
- ✅ **负载均衡**: 自动平衡负载

### 优势
1. 递归任务性能优异
2. 自动负载均衡
3. 减少线程竞争
4. 适合大数据处理

### 劣势
1. 仅适合特定场景
2. 实现复杂度较高
3. 调试困难
4. 不适合 I/O 密集型

### 适用场景
- 递归算法
- 可分治的大问题
- CPU 密集型计算

### 使用示例
```java
// 继承 RecursiveTask
class FibonacciTask extends RecursiveTask<Long> {
    private final int n;
    
    FibonacciTask(int n) { this.n = n; }
    
    @Override
    protected Long compute() {
        if (n <= 1) return (long) n;
        
        // Fork 两个子任务
        FibonacciTask f1 = new FibonacciTask(n - 1);
        f1.fork();
        
        FibonacciTask f2 = new FibonacciTask(n - 2);
        
        // Join 结果
        return f2.compute() + f1.join();
    }
}

// 使用 ForkJoinPool
ForkJoinPool pool = new ForkJoinPool();
Long result = pool.invoke(new FibonacciTask(40));
```

### 性能特征
| 指标 | 值 |
|------|-----|
| 创建时间 | 低 |
| 栈内存 | 中 |
| 切换开销 | 低 |
| 负载均衡 | 自动 |

---

## 🔮 CompletableFuture

### 概述
CompletableFuture 提供异步编程和结果组合的能力。

### 特点
- ✅ **异步非阻塞**: 不阻塞调用线程
- ✅ **链式调用**: 支持流式编排
- ✅ **结果组合**: 可组合多个异步结果
- ✅ **异常处理**: 完善的异常处理

### 优势
1. 异步编程简洁
2. 支持复杂的依赖关系
3. 非阻塞 I/O 友好
4. 与 Stream API 类似

### 劣势
1. 调试困难
2. 异常处理复杂
3. 可能产生回调地狱
4. 学习曲线陡峭

### 适用场景
- 异步流水线
- 多任务依赖编排
- 非阻塞 I/O

### 使用示例
```java
// 异步执行
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> fetchData());

// 链式处理
CompletableFuture<Result> result = future
    .thenApply(data -> parse(data))
    .thenApply(parsed -> transform(parsed))
    .thenAccept(r -> System.out.println(r));

// 组合多个 Future
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(this::task1);
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(this::task2);

// 全部完成
CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2);

// 任意一个完成
CompletableFuture<Object> any = CompletableFuture.anyOf(f1, f2);

// 组合结果
CompletableFuture<String> combined = f1.thenCombine(f2, (r1, r2) -> r1 + r2);
```

### 性能特征
| 指标 | 值 |
|------|-----|
| 创建时间 | 低 |
| 栈内存 | 低 |
| 切换开销 | 低 |
| 编排能力 | 强 |

---

## 🌊 Reactive Streams

### 概述
Reactive Streams 提供异步流式处理和背压支持。

### 特点
- ✅ **响应式**: 事件驱动
- ✅ **背压**: 消费者控制流量
- ✅ **流式处理**: 数据流管道
- ✅ **非阻塞**: 完全异步

### 优势
1. 背压保护
2. 流式处理高效
3. 组合能力强
4. 适合实时系统

### 劣势
1. 学习曲线陡峭
2. 调试困难
3. 实现复杂
4. 生态待完善

### 适用场景
- 流式数据处理
- 实时系统
- 需要背压的场景

### 使用示例
```java
// 使用 Flow API (Java 9+)
Publisher<Integer> publisher = new Publisher<>() {
    @Override
    public void subscribe(Subscriber<? super Integer> subscriber) {
        subscriber.onSubscribe(new Subscription() {
            private long requested;
            
            @Override
            public void request(long n) {
                requested += n;
                while (requested > 0) {
                    subscriber.onNext(1);
                    requested--;
                }
                subscriber.onComplete();
            }
            
            @Override
            public void cancel() {}
        });
    }
};

// 订阅
publisher.subscribe(new Subscriber<>() {
    @Override
    public void onSubscribe(Subscription s) {
        s.request(10);
    }
    
    @Override
    public void onNext(Integer item) {
        System.out.println(item);
    }
    
    @Override
    public void onError(Throwable t) {}
    
    @Override
    public void onComplete() {}
});
```

### 性能特征
| 指标 | 值 |
|------|-----|
| 创建时间 | 中 |
| 栈内存 | 低 |
| 切换开销 | 低 |
| 背压支持 | 有 |

---

## 📐 技术选择指南

### 决策树

```
你的任务是什么类型？
│
├─ CPU 密集型？
│  └─ 是 → 有递归结构？
│      ├─ 是 → Fork/Join
│      └─ 否 → 线程池/平台线程
│
├─ I/O 密集型？
│  └─ 是 → Java 21+？
│      ├─ 是 → 虚拟线程
│      └─ 否 → CompletableFuture/Reactive
│
├─ 高并发短任务？
│  └─ 是 → 虚拟线程/线程池
│
├─ 异步流水线？
│  └─ 是 → CompletableFuture/Reactive
│
└─ 流式处理？
   └─ 是 → Reactive Streams
```

### 技术对比表

| 维度 | 平台 | 虚拟 | 池 | F/J | CF | React |
|------|------|------|----|-----|----|-------|
| 创建开销 | 高 | 低 | 低 | 中 | 低 | 中 |
| 内存占用 | 高 | 低 | 中 | 中 | 低 | 低 |
| CPU 密集 | ⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐ |
| I/O 密集 | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| 易用性 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ |

---

## 🔗 相关资源

- [场景说明](../scenarios/)
- [工程矩阵](../../README.md#-工程结构矩阵)
- [实现指南](../implementation/README.md)
- [Java Virtual Threads (JEP 444)](https://openjdk.org/jeps/444)
- [Java Concurrency in Practice](https://jcip.net/)

---

*文档版本：1.0 | 最后更新：2026-03-27*
