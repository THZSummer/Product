package com.threadmg.scenarios.recursive;

import com.threadmg.benchmark.core.Scenario;
import com.threadmg.benchmark.core.ScenarioResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 * 递归分治场景
 * <p>
 * 使用 Fork/Join 框架实现递归分治算法测试，适用于测试线程策略对递归任务的支持。
 * 典型用例包括排序算法、数值计算、图算法等。
 * </p>
 *
 * <h2>场景特点</h2>
 * <ul>
 *     <li>使用 Fork/Join 框架</li>
 *     <li>支持多种递归算法</li>
 *     <li>验证结果正确性</li>
 *     <li>测试工作窃取效率</li>
 * </ul>
 *
 * <h2>支持算法</h2>
 * <ul>
 *     <li>QUICK_SORT: 快速排序</li>
 *     <li>MERGE_SORT: 归并排序</li>
 *     <li>FIBONACCI: 斐波那契数列</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 使用默认配置 (快速排序)
 * Scenario scenario = new RecursiveScenario();
 *
 * // 使用自定义配置
 * Scenario scenario = new RecursiveScenario(
 *     AlgorithmType.MERGE_SORT,  // 算法类型
 *     100000,                     // 数据规模
 *     1000                        // 并行阈值
 * );
 *
 * // 使用 Builder 模式
 * Scenario scenario = RecursiveScenario.builder()
 *     .algorithmType(AlgorithmType.FIBONACCI)
 *     .dataSize(40)
 *     .threshold(10)
 *     .build();
 * }</pre>
 *
 * <h2>配置参数</h2>
 * <ul>
 *     <li>algorithmType: 算法类型，默认 QUICK_SORT</li>
 *     <li>dataSize: 数据规模，默认 10000</li>
 *     <li>threshold: 并行阈值，默认 1000</li>
 * </ul>
 *
 * @author thread-mg
 * @version 1.0
 * @since 2026-03-27
 * @see Scenario
 * @see ForkJoinPool
 */
public class RecursiveScenario implements Scenario {

    /**
     * 算法类型枚举
     */
    public enum AlgorithmType {
        /**
         * 快速排序
         */
        QUICK_SORT,
        /**
         * 归并排序
         */
        MERGE_SORT,
        /**
         * 斐波那契数列
         */
        FIBONACCI
    }

    /**
     * 默认算法类型
     */
    private static final AlgorithmType DEFAULT_ALGORITHM = AlgorithmType.QUICK_SORT;

    /**
     * 默认数据规模
     */
    private static final int DEFAULT_DATA_SIZE = 10000;

    /**
     * 默认并行阈值
     */
    private static final int DEFAULT_THRESHOLD = 1000;

    /**
     * 算法类型
     */
    private final AlgorithmType algorithmType;

    /**
     * 数据规模
     */
    private final int dataSize;

    /**
     * 并行阈值
     */
    private final int threshold;

    /**
     * 随机数生成器
     */
    private final Random random = new Random();

    /**
     * 使用默认配置创建递归场景
     */
    public RecursiveScenario() {
        this(DEFAULT_ALGORITHM, DEFAULT_DATA_SIZE, DEFAULT_THRESHOLD);
    }

    /**
     * 使用自定义配置创建递归场景
     *
     * @param algorithmType 算法类型
     * @param dataSize      数据规模
     * @param threshold     并行阈值
     */
    public RecursiveScenario(AlgorithmType algorithmType, int dataSize, int threshold) {
        if (algorithmType == null) {
            throw new IllegalArgumentException("algorithmType must not be null");
        }
        if (dataSize < 1) {
            throw new IllegalArgumentException("dataSize must be >= 1");
        }
        if (threshold < 1) {
            throw new IllegalArgumentException("threshold must be >= 1");
        }

        this.algorithmType = algorithmType;
        this.dataSize = dataSize;
        this.threshold = threshold;
    }

    /**
     * 创建递归场景的 Builder
     *
     * @return Builder 实例
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getName() {
        return "recursive";
    }

    @Override
    public String getDescription() {
        return String.format("Recursive scenario (%s, size=%d, threshold=%d)",
                algorithmType, dataSize, threshold);
    }

    @Override
    public List<Runnable> createTasks(int count) {
        List<Runnable> tasks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            tasks.add(new RecursiveAlgorithmTask());
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
                future.get(120, TimeUnit.SECONDS); // 120 秒超时
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
     * 获取算法类型
     *
     * @return 算法类型
     */
    public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    /**
     * 获取数据规模
     *
     * @return 数据规模
     */
    public int getDataSize() {
        return dataSize;
    }

    /**
     * 获取并行阈值
     *
     * @return 并行阈值
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * 递归任务实现（使用 Fork/Join）
     */
    private class RecursiveAlgorithmTask implements Runnable {
        @Override
        public void run() {
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            try {
                switch (algorithmType) {
                    case QUICK_SORT -> {
                        int[] data = generateRandomArray(dataSize);
                        int[] copy = data.clone();
                        forkJoinPool.invoke(new QuickSortTask(copy, 0, copy.length - 1));
                        // 验证排序结果
                        if (!isSorted(copy)) {
                            throw new RuntimeException("Quick sort verification failed");
                        }
                    }
                    case MERGE_SORT -> {
                        int[] data = generateRandomArray(dataSize);
                        int[] copy = data.clone();
                        forkJoinPool.invoke(new MergeSortTask(copy, 0, copy.length - 1));
                        // 验证排序结果
                        if (!isSorted(copy)) {
                            throw new RuntimeException("Merge sort verification failed");
                        }
                    }
                    case FIBONACCI -> {
                        // Fibonacci 计算使用较小的数据规模
                        int n = Math.min(dataSize / 100, 40); // 限制 n <= 40
                        long result = forkJoinPool.invoke(new FibonacciTask(n));
                        // 简单验证
                        if (result < 0) {
                            throw new RuntimeException("Fibonacci calculation failed");
                        }
                    }
                    default -> throw new IllegalArgumentException("Unknown algorithm: " + algorithmType);
                }
            } finally {
                forkJoinPool.shutdown();
            }
        }

        private int[] generateRandomArray(int size) {
            int[] array = new int[size];
            for (int i = 0; i < size; i++) {
                array[i] = random.nextInt(10000);
            }
            return array;
        }

        private boolean isSorted(int[] array) {
            for (int i = 1; i < array.length; i++) {
                if (array[i - 1] > array[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 快速排序任务
     */
    private class QuickSortTask extends RecursiveAction {
        private final int[] array;
        private final int low;
        private final int high;

        QuickSortTask(int[] array, int low, int high) {
            this.array = array;
            this.low = low;
            this.high = high;
        }

        @Override
        protected void compute() {
            if (high - low <= threshold) {
                // 使用单线程排序
                Arrays.sort(array, low, high + 1);
            } else {
                // 分区
                int pi = partition(array, low, high);

                // Fork 左右子任务
                QuickSortTask leftTask = new QuickSortTask(array, low, pi - 1);
                QuickSortTask rightTask = new QuickSortTask(array, pi + 1, high);

                leftTask.fork();
                rightTask.compute();
                leftTask.join();
            }
        }

        private int partition(int[] array, int low, int high) {
            int pivot = array[high];
            int i = low - 1;

            for (int j = low; j < high; j++) {
                if (array[j] <= pivot) {
                    i++;
                    // 交换
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }

            // 交换 pivot
            int temp = array[i + 1];
            array[i + 1] = array[high];
            array[high] = temp;

            return i + 1;
        }
    }

    /**
     * 归并排序任务
     */
    private class MergeSortTask extends RecursiveAction {
        private final int[] array;
        private final int low;
        private final int high;

        MergeSortTask(int[] array, int low, int high) {
            this.array = array;
            this.low = low;
            this.high = high;
        }

        @Override
        protected void compute() {
            if (high - low <= threshold) {
                // 使用单线程排序
                Arrays.sort(array, low, high + 1);
            } else {
                int mid = (low + high) / 2;

                // Fork 左右子任务
                MergeSortTask leftTask = new MergeSortTask(array, low, mid);
                MergeSortTask rightTask = new MergeSortTask(array, mid + 1, high);

                leftTask.fork();
                rightTask.compute();
                leftTask.join();

                // 合并
                merge(array, low, mid, high);
            }
        }

        private void merge(int[] array, int low, int mid, int high) {
            int[] temp = new int[high - low + 1];
            int i = low, j = mid + 1, k = 0;

            while (i <= mid && j <= high) {
                if (array[i] <= array[j]) {
                    temp[k++] = array[i++];
                } else {
                    temp[k++] = array[j++];
                }
            }

            while (i <= mid) {
                temp[k++] = array[i++];
            }
            while (j <= high) {
                temp[k++] = array[j++];
            }

            // 复制回原数组
            System.arraycopy(temp, 0, array, low, temp.length);
        }
    }

    /**
     * 斐波那契任务
     */
    private class FibonacciTask extends RecursiveTask<Long> {
        private final int n;

        FibonacciTask(int n) {
            this.n = n;
        }

        @Override
        protected Long compute() {
            if (n <= threshold) {
                // 使用迭代计算
                return computeIterative(n);
            } else {
                // Fork 两个子任务
                FibonacciTask f1 = new FibonacciTask(n - 1);
                FibonacciTask f2 = new FibonacciTask(n - 2);

                f1.fork();
                long f2Result = f2.compute();
                long f1Result = f1.join();

                return f1Result + f2Result;
            }
        }

        private long computeIterative(int n) {
            if (n <= 1) return n;
            long a = 0, b = 1;
            for (int i = 2; i <= n; i++) {
                long temp = a + b;
                a = b;
                b = temp;
            }
            return b;
        }
    }

    /**
     * Builder 模式构建器
     */
    public static class Builder {
        private AlgorithmType algorithmType = DEFAULT_ALGORITHM;
        private int dataSize = DEFAULT_DATA_SIZE;
        private int threshold = DEFAULT_THRESHOLD;

        /**
         * 设置算法类型
         *
         * @param type 算法类型
         * @return this
         */
        public Builder algorithmType(AlgorithmType type) {
            if (type == null) {
                throw new IllegalArgumentException("algorithmType must not be null");
            }
            this.algorithmType = type;
            return this;
        }

        /**
         * 设置数据规模
         *
         * @param size 数据规模
         * @return this
         */
        public Builder dataSize(int size) {
            if (size < 1) {
                throw new IllegalArgumentException("dataSize must be >= 1");
            }
            this.dataSize = size;
            return this;
        }

        /**
         * 设置并行阈值
         *
         * @param size 并行阈值
         * @return this
         */
        public Builder threshold(int size) {
            if (size < 1) {
                throw new IllegalArgumentException("threshold must be >= 1");
            }
            this.threshold = size;
            return this;
        }

        /**
         * 构建 RecursiveScenario 实例
         *
         * @return RecursiveScenario 实例
         */
        public RecursiveScenario build() {
            return new RecursiveScenario(
                    algorithmType,
                    dataSize,
                    threshold
            );
        }
    }
}
