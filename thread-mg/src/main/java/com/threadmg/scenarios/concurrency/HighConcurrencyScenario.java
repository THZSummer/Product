package com.threadmg.scenarios.concurrency;

import com.threadmg.benchmark.core.Scenario;
import com.threadmg.benchmark.core.ScenarioResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 高并发短任务场景
 * <p>
 * 模拟大量短生命周期的任务，适用于测试高并发场景下不同线程策略的性能表现。
 * 典型用例包括 HTTP 请求处理、数据库查询、消息队列消费等。
 * </p>
 *
 * <h2>场景特点</h2>
 * <ul>
 *     <li>任务执行时间短（通常 &lt; 10ms）</li>
 *     <li>并发量高（默认 10000 个任务）</li>
 *     <li>任务无状态，可并行执行</li>
 *     <li>模拟 I/O 等待时间</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 使用默认配置
 * Scenario scenario = new HighConcurrencyScenario();
 *
 * // 使用自定义配置
 * Scenario scenario = new HighConcurrencyScenario(
 *     5000,   // taskCount
 *     1,      // minExecutionTimeMs
 *     5,      // maxExecutionTimeMs
 *     true    // simulateIoWait
 * );
 *
 * // 使用 Builder 模式
 * Scenario scenario = HighConcurrencyScenario.builder()
 *     .taskCount(10000)
 *     .executionTimeRange(1, 10)
 *     .simulateIoWait(true)
 *     .build();
 * }</pre>
 *
 * <h2>配置参数</h2>
 * <ul>
 *     <li>taskCount: 任务总数，默认 10000</li>
 *     <li>minExecutionTimeMs: 最小执行时间 (ms)，默认 1</li>
 *     <li>maxExecutionTimeMs: 最大执行时间 (ms)，默认 10</li>
 *     <li>simulateIoWait: 是否模拟 I/O 等待，默认 true</li>
 * </ul>
 *
 * @author thread-mg
 * @version 1.0
 * @since 2026-03-27
 * @see Scenario
 */
public class HighConcurrencyScenario implements Scenario {

    /**
     * 默认任务数量
     */
    private static final int DEFAULT_TASK_COUNT = 10000;

    /**
     * 默认最小执行时间 (毫秒)
     */
    private static final int DEFAULT_MIN_EXECUTION_TIME_MS = 1;

    /**
     * 默认最大执行时间 (毫秒)
     */
    private static final int DEFAULT_MAX_EXECUTION_TIME_MS = 10;

    /**
     * 默认是否模拟 I/O 等待
     */
    private static final boolean DEFAULT_SIMULATE_IO_WAIT = true;

    /**
     * 任务总数
     */
    private final int taskCount;

    /**
     * 最小执行时间 (毫秒)
     */
    private final int minExecutionTimeMs;

    /**
     * 最大执行时间 (毫秒)
     */
    private final int maxExecutionTimeMs;

    /**
     * 是否模拟 I/O 等待
     */
    private final boolean simulateIoWait;

    /**
     * 随机数生成器
     */
    private final Random random = new Random();

    /**
     * 使用默认配置创建高并发场景
     */
    public HighConcurrencyScenario() {
        this(DEFAULT_TASK_COUNT, DEFAULT_MIN_EXECUTION_TIME_MS,
             DEFAULT_MAX_EXECUTION_TIME_MS, DEFAULT_SIMULATE_IO_WAIT);
    }

    /**
     * 使用自定义配置创建高并发场景
     *
     * @param taskCount          任务总数
     * @param minExecutionTimeMs 最小执行时间 (毫秒)
     * @param maxExecutionTimeMs 最大执行时间 (毫秒)
     * @param simulateIoWait     是否模拟 I/O 等待
     */
    public HighConcurrencyScenario(int taskCount, int minExecutionTimeMs,
                                   int maxExecutionTimeMs, boolean simulateIoWait) {
        if (taskCount < 1) {
            throw new IllegalArgumentException("taskCount must be >= 1");
        }
        if (minExecutionTimeMs < 0) {
            throw new IllegalArgumentException("minExecutionTimeMs must be >= 0");
        }
        if (maxExecutionTimeMs < minExecutionTimeMs) {
            throw new IllegalArgumentException("maxExecutionTimeMs must be >= minExecutionTimeMs");
        }

        this.taskCount = taskCount;
        this.minExecutionTimeMs = minExecutionTimeMs;
        this.maxExecutionTimeMs = maxExecutionTimeMs;
        this.simulateIoWait = simulateIoWait;
    }

    /**
     * 创建高并发场景的 Builder
     *
     * @return Builder 实例
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getName() {
        return "high-concurrency";
    }

    @Override
    public String getDescription() {
        return String.format("High concurrency short task scenario (%d tasks, %d-%dms each)",
                taskCount, minExecutionTimeMs, maxExecutionTimeMs);
    }

    @Override
    public List<Runnable> createTasks(int count) {
        List<Runnable> tasks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            tasks.add(new ShortLivedTask());
        }
        return tasks;
    }

    @Override
    public ScenarioResult execute(ExecutorService executor, int taskCount) {
        List<Runnable> tasks = createTasks(taskCount);
        List<Future<?>> futures = new ArrayList<>(taskCount);
        List<Long> latencies = new ArrayList<>(taskCount);

        long startTime = System.nanoTime();

        // 提交所有任务
        for (Runnable task : tasks) {
            long taskStart = System.nanoTime();
            Future<?> future = executor.submit(task);
            futures.add(future);

            // 记录任务提交延迟
            long latency = System.nanoTime() - taskStart;
            latencies.add(latency);
        }

        // 等待所有任务完成
        int completedTasks = 0;
        for (Future<?> future : futures) {
            try {
                future.get(30, TimeUnit.SECONDS); // 30 秒超时
                completedTasks++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (ExecutionException | TimeoutException e) {
                // 记录错误但继续等待其他任务
            }
        }

        long endTime = System.nanoTime();
        long totalDuration = endTime - startTime;

        boolean success = (completedTasks == taskCount);
        String errorMessage = success ? null :
                "Some tasks failed to complete: expected=" + taskCount + ", actual=" + completedTasks;

        return new ScenarioResult(
                getName(),
                taskCount,
                completedTasks,
                totalDuration,
                latencies,
                success,
                errorMessage
        );
    }

    @Override
    public boolean verify(ScenarioResult result) {
        // 验证所有任务是否完成
        if (!result.isSuccess()) {
            return false;
        }

        // 验证吞吐量在合理范围内
        double throughput = result.getThroughput();
        if (throughput <= 0) {
            return false;
        }

        // 验证延迟在合理范围内
        double avgLatency = result.getAverageLatencyMs();
        if (avgLatency < 0) {
            return false;
        }

        return true;
    }

    /**
     * 获取任务数量
     *
     * @return 任务数量
     */
    public int getTaskCount() {
        return taskCount;
    }

    /**
     * 获取最小执行时间
     *
     * @return 最小执行时间 (毫秒)
     */
    public int getMinExecutionTimeMs() {
        return minExecutionTimeMs;
    }

    /**
     * 获取最大执行时间
     *
     * @return 最大执行时间 (毫秒)
     */
    public int getMaxExecutionTimeMs() {
        return maxExecutionTimeMs;
    }

    /**
     * 检查是否模拟 I/O 等待
     *
     * @return true 如果模拟 I/O 等待
     */
    public boolean isSimulateIoWait() {
        return simulateIoWait;
    }

    /**
     * 短生命周期任务实现
     */
    private class ShortLivedTask implements Runnable {
        @Override
        public void run() {
            try {
                // 模拟任务执行时间（1-10ms）
                int executionTime = random.nextInt(
                        maxExecutionTimeMs - minExecutionTimeMs + 1) + minExecutionTimeMs;

                if (simulateIoWait) {
                    // 模拟 I/O 等待
                    Thread.sleep(executionTime);
                } else {
                    // 纯 CPU 计算模拟
                    simulateCpuWork(executionTime);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        /**
         * 模拟 CPU 计算工作
         *
         * @param durationMs 持续时间 (毫秒)
         */
        private void simulateCpuWork(int durationMs) {
            long endTime = System.currentTimeMillis() + durationMs;
            long sum = 0;
            while (System.currentTimeMillis() < endTime) {
                sum += Math.random() * Math.random();
            }
            // 确保计算不被优化掉
            if (sum == Double.NaN) {
                throw new RuntimeException("Unexpected NaN result");
            }
        }
    }

    /**
     * Builder 模式构建器
     */
    public static class Builder {
        private int taskCount = DEFAULT_TASK_COUNT;
        private int minExecutionTimeMs = DEFAULT_MIN_EXECUTION_TIME_MS;
        private int maxExecutionTimeMs = DEFAULT_MAX_EXECUTION_TIME_MS;
        private boolean simulateIoWait = DEFAULT_SIMULATE_IO_WAIT;

        /**
         * 设置任务数量
         *
         * @param count 任务数量
         * @return this
         */
        public Builder taskCount(int count) {
            if (count < 1) {
                throw new IllegalArgumentException("taskCount must be >= 1");
            }
            this.taskCount = count;
            return this;
        }

        /**
         * 设置执行时间范围
         *
         * @param minMs 最小执行时间 (毫秒)
         * @param maxMs 最大执行时间 (毫秒)
         * @return this
         */
        public Builder executionTimeRange(int minMs, int maxMs) {
            if (minMs < 0) {
                throw new IllegalArgumentException("minExecutionTimeMs must be >= 0");
            }
            if (maxMs < minMs) {
                throw new IllegalArgumentException("maxExecutionTimeMs must be >= minExecutionTimeMs");
            }
            this.minExecutionTimeMs = minMs;
            this.maxExecutionTimeMs = maxMs;
            return this;
        }

        /**
         * 设置是否模拟 I/O 等待
         *
         * @param simulate true 模拟 I/O 等待
         * @return this
         */
        public Builder simulateIoWait(boolean simulate) {
            this.simulateIoWait = simulate;
            return this;
        }

        /**
         * 构建 HighConcurrencyScenario 实例
         *
         * @return HighConcurrencyScenario 实例
         */
        public HighConcurrencyScenario build() {
            return new HighConcurrencyScenario(
                    taskCount,
                    minExecutionTimeMs,
                    maxExecutionTimeMs,
                    simulateIoWait
            );
        }
    }
}
