package com.threadmg.scenarios.mixed;

import com.threadmg.benchmark.core.Scenario;
import com.threadmg.benchmark.core.ScenarioResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 混合负载场景
 * <p>
 * 模拟同时包含 CPU 计算和 I/O 等待的混合场景，最接近真实业务环境。
 * 典型用例包括数据处理（读取→计算→写入）、ETL 流程、图像处理等。
 * </p>
 *
 * <h2>场景特点</h2>
 * <ul>
 *     <li>CPU 计算和 I/O 操作混合</li>
 *     <li>可配置混合比例</li>
 *     <li>模拟真实业务场景</li>
 *     <li>测试线程策略的适应性</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 使用默认配置 (70% CPU + 30% I/O)
 * Scenario scenario = new MixedLoadScenario();
 *
 * // 使用自定义配置
 * Scenario scenario = new MixedLoadScenario(
 *     0.5,    // cpuRatio (50% CPU)
 *     0.5,    // ioRatio (50% I/O)
 *     1000,   // totalTasks
 *     50,     // matrixSize (CPU task)
 *     5       // ioDelayMs (I/O task)
 * );
 *
 * // 使用 Builder 模式
 * Scenario scenario = MixedLoadScenario.builder()
 *     .cpuRatio(0.7)
 *     .ioRatio(0.3)
 *     .totalTasks(1000)
 *     .build();
 * }</pre>
 *
 * <h2>配置参数</h2>
 * <ul>
 *     <li>cpuRatio: CPU 任务比例 (0.0-1.0)，默认 0.7</li>
 *     <li>ioRatio: I/O 任务比例 (0.0-1.0)，默认 0.3</li>
 *     <li>totalTasks: 任务总数，默认 1000</li>
 *     <li>matrixSize: 矩阵大小 (CPU 任务用)，默认 50</li>
 *     <li>ioDelayMs: I/O 延迟 (毫秒)，默认 5</li>
 * </ul>
 *
 * @author thread-mg
 * @version 1.0
 * @since 2026-03-27
 * @see Scenario
 */
public class MixedLoadScenario implements Scenario {

    /**
     * 默认 CPU 任务比例
     */
    private static final double DEFAULT_CPU_RATIO = 0.7;

    /**
     * 默认 I/O 任务比例
     */
    private static final double DEFAULT_IO_RATIO = 0.3;

    /**
     * 默认任务总数
     */
    private static final int DEFAULT_TOTAL_TASKS = 1000;

    /**
     * 默认矩阵大小
     */
    private static final int DEFAULT_MATRIX_SIZE = 50;

    /**
     * 默认 I/O 延迟 (毫秒)
     */
    private static final int DEFAULT_IO_DELAY_MS = 5;

    /**
     * CPU 任务比例 (0.0 - 1.0)
     */
    private final double cpuRatio;

    /**
     * I/O 任务比例 (0.0 - 1.0)
     */
    private final double ioRatio;

    /**
     * 任务总数
     */
    private final int totalTasks;

    /**
     * 矩阵大小（用于 CPU 密集型任务）
     */
    private final int matrixSize;

    /**
     * I/O 延迟 (毫秒)
     */
    private final int ioDelayMs;

    /**
     * 随机数生成器
     */
    private final Random random = new Random();

    /**
     * 使用默认配置创建混合负载场景
     */
    public MixedLoadScenario() {
        this(DEFAULT_CPU_RATIO, DEFAULT_IO_RATIO, DEFAULT_TOTAL_TASKS,
             DEFAULT_MATRIX_SIZE, DEFAULT_IO_DELAY_MS);
    }

    /**
     * 使用自定义配置创建混合负载场景
     *
     * @param cpuRatio    CPU 任务比例 (0.0-1.0)
     * @param ioRatio     I/O 任务比例 (0.0-1.0)
     * @param totalTasks  任务总数
     * @param matrixSize  矩阵大小
     * @param ioDelayMs   I/O 延迟 (毫秒)
     */
    public MixedLoadScenario(double cpuRatio, double ioRatio, int totalTasks,
                             int matrixSize, int ioDelayMs) {
        if (cpuRatio < 0.0 || cpuRatio > 1.0) {
            throw new IllegalArgumentException("cpuRatio must be between 0.0 and 1.0");
        }
        if (ioRatio < 0.0 || ioRatio > 1.0) {
            throw new IllegalArgumentException("ioRatio must be between 0.0 and 1.0");
        }
        if (cpuRatio + ioRatio > 1.0) {
            throw new IllegalArgumentException("cpuRatio + ioRatio must be <= 1.0");
        }
        if (totalTasks < 1) {
            throw new IllegalArgumentException("totalTasks must be >= 1");
        }
        if (matrixSize < 1) {
            throw new IllegalArgumentException("matrixSize must be >= 1");
        }
        if (ioDelayMs < 0) {
            throw new IllegalArgumentException("ioDelayMs must be >= 0");
        }

        this.cpuRatio = cpuRatio;
        this.ioRatio = ioRatio;
        this.totalTasks = totalTasks;
        this.matrixSize = matrixSize;
        this.ioDelayMs = ioDelayMs;
    }

    /**
     * 创建混合负载场景的 Builder
     *
     * @return Builder 实例
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getName() {
        return "mixed-load";
    }

    @Override
    public String getDescription() {
        return String.format("Mixed load scenario (CPU=%.0f%%, I/O=%.0f%%, tasks=%d)",
                cpuRatio * 100, ioRatio * 100, totalTasks);
    }

    @Override
    public List<Runnable> createTasks(int count) {
        List<Runnable> tasks = new ArrayList<>(count);
        int cpuTaskCount = (int) (count * cpuRatio);
        int ioTaskCount = (int) (count * ioRatio);
        int otherTaskCount = count - cpuTaskCount - ioTaskCount;

        // 创建 CPU 密集型任务
        for (int i = 0; i < cpuTaskCount; i++) {
            tasks.add(new CpuIntensiveTask());
        }

        // 创建 I/O 密集型任务
        for (int i = 0; i < ioTaskCount; i++) {
            tasks.add(new IoIntensiveTask());
        }

        // 创建其他任务（轻量级）
        for (int i = 0; i < otherTaskCount; i++) {
            tasks.add(new LightTask());
        }

        // 打乱任务顺序以模拟真实场景
        shuffleTasks(tasks);

        return tasks;
    }

    /**
     * 随机打乱任务列表
     *
     * @param tasks 任务列表
     */
    private void shuffleTasks(List<Runnable> tasks) {
        for (int i = tasks.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Runnable temp = tasks.get(i);
            tasks.set(i, tasks.get(j));
            tasks.set(j, temp);
        }
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
                future.get(60, TimeUnit.SECONDS); // 60 秒超时
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

        return true;
    }

    /**
     * 获取 CPU 任务比例
     *
     * @return CPU 任务比例
     */
    public double getCpuRatio() {
        return cpuRatio;
    }

    /**
     * 获取 I/O 任务比例
     *
     * @return I/O 任务比例
     */
    public double getIoRatio() {
        return ioRatio;
    }

    /**
     * 获取任务总数
     *
     * @return 任务总数
     */
    public int getTotalTasks() {
        return totalTasks;
    }

    /**
     * 获取矩阵大小
     *
     * @return 矩阵大小
     */
    public int getMatrixSize() {
        return matrixSize;
    }

    /**
     * 获取 I/O 延迟
     *
     * @return I/O 延迟 (毫秒)
     */
    public int getIoDelayMs() {
        return ioDelayMs;
    }

    /**
     * CPU 密集型任务实现
     */
    private class CpuIntensiveTask implements Runnable {
        @Override
        public void run() {
            // 执行矩阵乘法计算
            double[][] matrixA = createRandomMatrix(matrixSize);
            double[][] matrixB = createRandomMatrix(matrixSize);
            double[][] result = multiplyMatrices(matrixA, matrixB);

            // 简单验证
            if (result == null || result.length != matrixSize) {
                throw new RuntimeException("Matrix multiplication failed");
            }
        }

        private double[][] createRandomMatrix(int size) {
            double[][] matrix = new double[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = random.nextDouble();
                }
            }
            return matrix;
        }

        private double[][] multiplyMatrices(double[][] a, double[][] b) {
            int n = a.length;
            double[][] result = new double[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < n; k++) {
                        result[i][j] += a[i][k] * b[k][j];
                    }
                }
            }
            return result;
        }
    }

    /**
     * I/O 密集型任务实现
     */
    private class IoIntensiveTask implements Runnable {
        @Override
        public void run() {
            try {
                // 模拟 I/O 等待
                Thread.sleep(ioDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 轻量级任务实现
     */
    private class LightTask implements Runnable {
        @Override
        public void run() {
            // 简单的计算任务
            double sum = 0;
            for (int i = 0; i < 100; i++) {
                sum += Math.random();
            }
        }
    }

    /**
     * Builder 模式构建器
     */
    public static class Builder {
        private double cpuRatio = DEFAULT_CPU_RATIO;
        private double ioRatio = DEFAULT_IO_RATIO;
        private int totalTasks = DEFAULT_TOTAL_TASKS;
        private int matrixSize = DEFAULT_MATRIX_SIZE;
        private int ioDelayMs = DEFAULT_IO_DELAY_MS;

        /**
         * 设置 CPU 任务比例
         *
         * @param ratio CPU 任务比例 (0.0-1.0)
         * @return this
         */
        public Builder cpuRatio(double ratio) {
            if (ratio < 0.0 || ratio > 1.0) {
                throw new IllegalArgumentException("cpuRatio must be between 0.0 and 1.0");
            }
            this.cpuRatio = ratio;
            return this;
        }

        /**
         * 设置 I/O 任务比例
         *
         * @param ratio I/O 任务比例 (0.0-1.0)
         * @return this
         */
        public Builder ioRatio(double ratio) {
            if (ratio < 0.0 || ratio > 1.0) {
                throw new IllegalArgumentException("ioRatio must be between 0.0 and 1.0");
            }
            this.ioRatio = ratio;
            return this;
        }

        /**
         * 设置任务总数
         *
         * @param count 任务总数
         * @return this
         */
        public Builder totalTasks(int count) {
            if (count < 1) {
                throw new IllegalArgumentException("totalTasks must be >= 1");
            }
            this.totalTasks = count;
            return this;
        }

        /**
         * 设置矩阵大小
         *
         * @param size 矩阵大小
         * @return this
         */
        public Builder matrixSize(int size) {
            if (size < 1) {
                throw new IllegalArgumentException("matrixSize must be >= 1");
            }
            this.matrixSize = size;
            return this;
        }

        /**
         * 设置 I/O 延迟
         *
         * @param delayMs I/O 延迟 (毫秒)
         * @return this
         */
        public Builder ioDelayMs(int delayMs) {
            if (delayMs < 0) {
                throw new IllegalArgumentException("ioDelayMs must be >= 0");
            }
            this.ioDelayMs = delayMs;
            return this;
        }

        /**
         * 构建 MixedLoadScenario 实例
         *
         * @return MixedLoadScenario 实例
         */
        public MixedLoadScenario build() {
            return new MixedLoadScenario(
                    cpuRatio,
                    ioRatio,
                    totalTasks,
                    matrixSize,
                    ioDelayMs
            );
        }
    }
}
