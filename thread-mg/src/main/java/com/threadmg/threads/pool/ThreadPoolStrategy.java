package com.threadmg.threads.pool;

import com.threadmg.benchmark.core.ThreadStrategy;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池策略
 * <p>
 * 使用 ExecutorService 和 ThreadPoolExecutor 实现可配置的线程池策略。
 * 支持自定义线程池大小、队列容量和拒绝策略，适用于通用场景。
 * </p>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 使用默认配置
 * ThreadPoolStrategy strategy = new ThreadPoolStrategy();
 *
 * // 使用自定义配置
 * ThreadPoolStrategy strategy = new ThreadPoolStrategy(
 *     8,   // corePoolSize
 *     16,  // maxPoolSize
 *     60,  // keepAliveTime
 *     1000 // queueCapacity
 * );
 *
 * // 使用 Builder 模式
 * ThreadPoolStrategy strategy = ThreadPoolStrategy.builder()
 *     .corePoolSize(8)
 *     .maxPoolSize(16)
 *     .queueCapacity(1000)
 *     .rejectedPolicy(ThreadPoolStrategy.RejectPolicy.CALLER_RUNS)
 *     .build();
 * }</pre>
 *
 * <h2>配置说明</h2>
 * <ul>
 *     <li>corePoolSize: 核心线程数，默认 CPU 核心数</li>
 *     <li>maxPoolSize: 最大线程数，默认 CPU 核心数 × 2</li>
 *     <li>keepAliveTime: 空闲线程存活时间 (秒)，默认 60 秒</li>
 *     <li>queueCapacity: 任务队列容量，默认 1000</li>
 *     <li>rejectPolicy: 拒绝策略，默认 CALLER_RUNS</li>
 * </ul>
 *
 * @author thread-mg
 * @version 1.0
 * @since 2026-03-27
 * @see ThreadStrategy
 * @see ThreadPoolExecutor
 */
public class ThreadPoolStrategy implements ThreadStrategy {

    /**
     * 默认核心线程数（CPU 核心数）
     */
    private static final int DEFAULT_CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * 默认最大线程数（CPU 核心数 × 2）
     */
    private static final int DEFAULT_MAX_POOL_SIZE = DEFAULT_CORE_POOL_SIZE * 2;

    /**
     * 默认空闲线程存活时间（秒）
     */
    private static final long DEFAULT_KEEP_ALIVE_TIME = 60L;

    /**
     * 默认任务队列容量
     */
    private static final int DEFAULT_QUEUE_CAPACITY = 1000;

    /**
     * 默认拒绝策略
     */
    private static final RejectPolicy DEFAULT_REJECT_POLICY = RejectPolicy.CALLER_RUNS;

    /**
     * 核心线程数
     */
    private final int corePoolSize;

    /**
     * 最大线程数
     */
    private final int maxPoolSize;

    /**
     * 空闲线程存活时间（秒）
     */
    private final long keepAliveTime;

    /**
     * 任务队列容量
     */
    private final int queueCapacity;

    /**
     * 拒绝策略
     */
    private final RejectPolicy rejectPolicy;

    /**
     * 线程工厂
     */
    private final ThreadFactory threadFactory;

    /**
     * 执行器服务
     */
    private ExecutorService executor;

    /**
     * 是否已关闭
     */
    private volatile boolean shutdown = false;

    /**
     * 拒绝策略枚举
     */
    public enum RejectPolicy {
        /**
         * 抛出 RejectedExecutionException 异常
         */
        ABORT,
        /**
         * 由调用线程执行被拒绝的任务
         */
        CALLER_RUNS,
        /**
         * 直接丢弃被拒绝的任务
         */
        DISCARD,
        /**
         * 丢弃队列中最老的任务，然后重试添加
         */
        DISCARD_OLDEST
    }

    /**
     * 使用默认配置创建线程池策略
     */
    public ThreadPoolStrategy() {
        this(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME, DEFAULT_QUEUE_CAPACITY);
    }

    /**
     * 使用自定义配置创建线程池策略
     *
     * @param corePoolSize 核心线程数（至少为 1）
     * @param maxPoolSize  最大线程数（至少等于 corePoolSize）
     * @param keepAliveTime 空闲线程存活时间（秒）
     * @param queueCapacity 任务队列容量（至少为 1）
     * @throws IllegalArgumentException 如果参数无效
     */
    public ThreadPoolStrategy(int corePoolSize, int maxPoolSize, long keepAliveTime, int queueCapacity) {
        this(corePoolSize, maxPoolSize, keepAliveTime, queueCapacity, DEFAULT_REJECT_POLICY);
    }

    /**
     * 使用完整配置创建线程池策略
     *
     * @param corePoolSize  核心线程数（至少为 1）
     * @param maxPoolSize   最大线程数（至少等于 corePoolSize）
     * @param keepAliveTime 空闲线程存活时间（秒）
     * @param queueCapacity 任务队列容量（至少为 1）
     * @param rejectPolicy  拒绝策略（不可为 null）
     * @throws IllegalArgumentException 如果参数无效
     */
    public ThreadPoolStrategy(int corePoolSize, int maxPoolSize, long keepAliveTime,
                              int queueCapacity, RejectPolicy rejectPolicy) {
        this(corePoolSize, maxPoolSize, keepAliveTime, queueCapacity, rejectPolicy,
             new ThreadFactoryBuilder().build());
    }

    /**
     * 使用完整配置创建线程池策略（含线程工厂）
     *
     * @param corePoolSize  核心线程数（至少为 1）
     * @param maxPoolSize   最大线程数（至少等于 corePoolSize）
     * @param keepAliveTime 空闲线程存活时间（秒）
     * @param queueCapacity 任务队列容量（至少为 1）
     * @param rejectPolicy  拒绝策略（不可为 null）
     * @param threadFactory 线程工厂（不可为 null）
     * @throws IllegalArgumentException 如果参数无效
     */
    public ThreadPoolStrategy(int corePoolSize, int maxPoolSize, long keepAliveTime,
                              int queueCapacity, RejectPolicy rejectPolicy, ThreadFactory threadFactory) {
        // 参数验证
        if (corePoolSize < 1) {
            throw new IllegalArgumentException("corePoolSize must be >= 1");
        }
        if (maxPoolSize < corePoolSize) {
            throw new IllegalArgumentException("maxPoolSize must be >= corePoolSize");
        }
        if (keepAliveTime <= 0) {
            throw new IllegalArgumentException("keepAliveTime must be > 0");
        }
        if (queueCapacity < 1) {
            throw new IllegalArgumentException("queueCapacity must be >= 1");
        }
        if (rejectPolicy == null) {
            throw new IllegalArgumentException("rejectPolicy must not be null");
        }
        if (threadFactory == null) {
            throw new IllegalArgumentException("threadFactory must not be null");
        }

        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.queueCapacity = queueCapacity;
        this.rejectPolicy = rejectPolicy;
        this.threadFactory = threadFactory;
    }

    /**
     * 创建线程池策略的 Builder
     *
     * @return Builder 实例
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getName() {
        return "pool";
    }

    @Override
    public String getDescription() {
        return String.format("Configurable thread pool strategy (core=%d, max=%d, queue=%d)",
                corePoolSize, maxPoolSize, queueCapacity);
    }

    @Override
    public ExecutorService createExecutor() {
        if (shutdown) {
            throw new IllegalStateException("ThreadPoolStrategy has been shutdown");
        }

        // 创建任务队列
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(queueCapacity);

        // 创建拒绝策略处理器
        RejectedExecutionHandler rejectedHandler = createRejectHandler(rejectPolicy);

        // 创建线程池执行器
        executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                workQueue,
                threadFactory,
                rejectedHandler
        );

        // 允许核心线程超时
        ((ThreadPoolExecutor) executor).allowCoreThreadTimeOut(true);

        return executor;
    }

    /**
     * 根据拒绝策略枚举创建对应的拒绝处理器
     *
     * @param policy 拒绝策略枚举
     * @return 对应的 RejectedExecutionHandler
     */
    private RejectedExecutionHandler createRejectHandler(RejectPolicy policy) {
        return switch (policy) {
            case ABORT -> new ThreadPoolExecutor.AbortPolicy();
            case CALLER_RUNS -> new ThreadPoolExecutor.CallerRunsPolicy();
            case DISCARD -> new ThreadPoolExecutor.DiscardPolicy();
            case DISCARD_OLDEST -> new ThreadPoolExecutor.DiscardOldestPolicy();
        };
    }

    @Override
    public void shutdown() {
        if (shutdown) {
            return;
        }

        shutdown = true;

        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        System.err.println("ThreadPoolExecutor did not terminate gracefully");
                    }
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public int getDefaultThreadCount() {
        return corePoolSize;
    }

    /**
     * 获取核心线程数
     *
     * @return 核心线程数
     */
    public int getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * 获取最大线程数
     *
     * @return 最大线程数
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * 获取空闲线程存活时间
     *
     * @return 存活时间（秒）
     */
    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    /**
     * 获取任务队列容量
     *
     * @return 队列容量
     */
    public int getQueueCapacity() {
        return queueCapacity;
    }

    /**
     * 获取拒绝策略
     *
     * @return 拒绝策略
     */
    public RejectPolicy getRejectPolicy() {
        return rejectPolicy;
    }

    /**
     * 检查是否已关闭
     *
     * @return true 如果已关闭
     */
    public boolean isShutdown() {
        return shutdown;
    }

    /**
     * 内部线程工厂构建器
     */
    private static class ThreadFactoryBuilder {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final ThreadGroup group;

        ThreadFactoryBuilder() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        }

        ThreadFactory build() {
            return r -> {
                Thread t = new Thread(group, r,
                        "thread-pool-" + threadNumber.getAndIncrement(),
                        0);
                if (t.isDaemon()) {
                    t.setDaemon(false);
                }
                if (t.getPriority() != Thread.NORM_PRIORITY) {
                    t.setPriority(Thread.NORM_PRIORITY);
                }
                return t;
            };
        }
    }

    /**
     * Builder 模式构建器
     */
    public static class Builder {
        private int corePoolSize = DEFAULT_CORE_POOL_SIZE;
        private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
        private long keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;
        private int queueCapacity = DEFAULT_QUEUE_CAPACITY;
        private RejectPolicy rejectPolicy = DEFAULT_REJECT_POLICY;
        private ThreadFactory threadFactory = new ThreadFactoryBuilder().build();

        /**
         * 设置核心线程数
         *
         * @param size 核心线程数
         * @return this
         */
        public Builder corePoolSize(int size) {
            if (size < 1) {
                throw new IllegalArgumentException("corePoolSize must be >= 1");
            }
            this.corePoolSize = size;
            return this;
        }

        /**
         * 设置最大线程数
         *
         * @param size 最大线程数
         * @return this
         */
        public Builder maxPoolSize(int size) {
            if (size < corePoolSize) {
                throw new IllegalArgumentException("maxPoolSize must be >= corePoolSize");
            }
            this.maxPoolSize = size;
            return this;
        }

        /**
         * 设置空闲线程存活时间
         *
         * @param time 时间（秒）
         * @return this
         */
        public Builder keepAliveTime(long time) {
            if (time <= 0) {
                throw new IllegalArgumentException("keepAliveTime must be > 0");
            }
            this.keepAliveTime = time;
            return this;
        }

        /**
         * 设置任务队列容量
         *
         * @param capacity 队列容量
         * @return this
         */
        public Builder queueCapacity(int capacity) {
            if (capacity < 1) {
                throw new IllegalArgumentException("queueCapacity must be >= 1");
            }
            this.queueCapacity = capacity;
            return this;
        }

        /**
         * 设置拒绝策略
         *
         * @param policy 拒绝策略
         * @return this
         */
        public Builder rejectedPolicy(RejectPolicy policy) {
            if (policy == null) {
                throw new IllegalArgumentException("rejectPolicy must not be null");
            }
            this.rejectPolicy = policy;
            return this;
        }

        /**
         * 设置线程工厂
         *
         * @param factory 线程工厂
         * @return this
         */
        public Builder threadFactory(ThreadFactory factory) {
            if (factory == null) {
                throw new IllegalArgumentException("threadFactory must not be null");
            }
            this.threadFactory = factory;
            return this;
        }

        /**
         * 构建 ThreadPoolStrategy 实例
         *
         * @return ThreadPoolStrategy 实例
         */
        public ThreadPoolStrategy build() {
            return new ThreadPoolStrategy(
                    corePoolSize,
                    maxPoolSize,
                    keepAliveTime,
                    queueCapacity,
                    rejectPolicy,
                    threadFactory
            );
        }
    }
}
