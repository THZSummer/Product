package com.threadmg.scenarios.cpu;

import com.threadmg.benchmark.core.Scenario;
import com.threadmg.benchmark.core.ScenarioResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * CPU密集型场景
 * 通过矩阵乘法任务模拟CPU密集型计算
 */
public class CpuBoundScenario implements Scenario {
    private final int matrixSize;
    private final int taskCount;

    public CpuBoundScenario() {
        this(100, 1000); // Default matrix size and task count
    }
    
    public CpuBoundScenario(int matrixSize, int taskCount) {
        this.matrixSize = matrixSize;
        this.taskCount = taskCount;
    }

    @Override
    public String getName() {
        return "cpu-bound";
    }

    @Override
    public String getDescription() {
        return "CPU intensive scenario using matrix multiplication";
    }

    @Override
    public List<Runnable> createTasks(int count) {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tasks.add(new MatrixMultiplicationTask(matrixSize));
        }
        return tasks;
    }

    @Override
    public ScenarioResult execute(ExecutorService executor, int taskCount) {
        List<Runnable> tasks = createTasks(taskCount);
        List<Future<?>> futures = new ArrayList<>();
        List<Long> latencies = new ArrayList<>(); // Track task completion times
        
        long startTime = System.nanoTime();
        
        // Submit all tasks
        for (Runnable task : tasks) {
            long taskStart = System.nanoTime();
            Future<?> future = executor.submit(() -> {
                task.run();
            });
            futures.add(future);
            
            // Record start-to-finish latency for each task submission
            long latency = System.nanoTime() - taskStart;
            latencies.add(latency);
        }
        
        // Wait for all tasks to complete
        List<Long> taskExecutionTime = new ArrayList<>();
        for (Future<?> future : futures) {
            try {
                long waitStart = System.nanoTime();
                future.get(30, TimeUnit.SECONDS); // 30-second timeout for safety
                taskExecutionTime.add(System.nanoTime() - waitStart);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                System.err.println("Task failed: " + e.getMessage());
                Thread.currentThread().interrupt(); // Preserve interrupt status
                break;
            }
        }
        
        long endTime = System.nanoTime();
        long totalDuration = endTime - startTime;
        
        // Determine success based on successful task completions
        long completedTasks = futures.stream()
            .filter(f -> {
                try {
                    return f.isDone();
                } catch (Exception e) {
                    return false;
                }
            })
            .count();
        
        boolean success = completedTasks == taskCount;
        String errorMessage = success ? null : "Some tasks failed to complete";
        
        return new ScenarioResult(
            getName(),
            taskCount,
            (int) completedTasks,
            totalDuration,
            latencies,  // Using latencies tracking above
            success,
            errorMessage
        );
    }

    @Override
    public boolean verify(ScenarioResult result) {
        // For CPU bound scenario, verification could involve checking if results are reasonable
        // e.g., throughput should be within expected bounds
        return result.isSuccess() && result.getCompletedTasks() > 0;
    }
    
    /**
     * 内部类：执行矩阵乘法的计算任务
     */
    private static class MatrixMultiplicationTask implements Runnable {
        private final int size;
        
        public MatrixMultiplicationTask(int size) {
            this.size = size;
        }
        
        @Override
        public void run() {
            // Create random matrices
            double[][] a = createRandomMatrix(size);
            double[][] b = createRandomMatrix(size);
            
            // Perform matrix multiplication
            double[][] result = multiplyMatrices(a, b);
            
            // Simple verification - check that we got a non-null result
            if (result == null) {
                throw new RuntimeException("Matrix multiplication returned null");
            }
        }
        
        private double[][] createRandomMatrix(int n) {
            Random random = new Random();
            double[][] matrix = new double[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
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
}