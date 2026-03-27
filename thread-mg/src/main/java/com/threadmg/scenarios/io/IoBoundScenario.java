package com.threadmg.scenarios.io;

import com.threadmg.benchmark.core.Scenario;
import com.threadmg.benchmark.core.ScenarioResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * I/O密集型场景
 * 通过内存中的读写操作模拟I/O密集场景（避免磁盘I/O的不确定性）
 */
public class IoBoundScenario implements Scenario {
    private final int dataSize;
    private final int taskCount;

    public IoBoundScenario() {
        this(1024 * 10, 2000); // Changed: smaller data (10KB), more tasks (2000) for better I/O concurrency
    }
    
    public IoBoundScenario(int dataSize, int taskCount) {
        this.dataSize = dataSize;
        this.taskCount = taskCount;
    }

    @Override
    public String getName() {
        return "io-bound";
    }

    @Override
    public String getDescription() {
        return "I/O intensive scenario simulating read/write operations in memory";
    }

    @Override
    public List<Runnable> createTasks(int count) {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tasks.add(new SimulatedIoTask(dataSize));
        }
        return tasks;
    }

    @Override
    public ScenarioResult execute(ExecutorService executor, int taskCount) {
        List<Runnable> tasks = createTasks(taskCount);
        List<Future<?>> futures = new ArrayList<>();
        List<Long> latencies = new ArrayList<>(); // Track task latencies
        
        long startTime = System.nanoTime();
        
        // Submit all tasks
        for (Runnable task : tasks) {
            long taskStart = System.nanoTime();
            Future<?> future = executor.submit(() -> {
                task.run();
            });
            futures.add(future);
            
            // Record task creation latency
            long latency = System.nanoTime() - taskStart;
            latencies.add(latency);
        }
        
        // Wait for all tasks to complete
        List<Long> taskExecutionTime = new ArrayList<>();
        for (Future<?> future : futures) {
            try {
                long waitStart = System.nanoTime();
                future.get(60, TimeUnit.SECONDS); // 60-second timeout for I/O tasks
                taskExecutionTime.add(System.nanoTime() - waitStart);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                System.err.println("I/O task failed: " + e.getMessage());
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
        String errorMessage = success ? null : "Some I/O tasks failed to complete";
        
        return new ScenarioResult(
            getName(),
            taskCount,
            (int) completedTasks,
            totalDuration,
            latencies,  // Tracking latencies from submission to completion
            success,
            errorMessage
        );
    }

    @Override
    public boolean verify(ScenarioResult result) {
        // For I/O bound scenario, verification could ensure reasonable completion rates
        return result.isSuccess() && result.getCompletedTasks() > 0;
    }
    
    /**
     * 内部类：执行内存模拟I/O操作的任务
     */
    private static class SimulatedIoTask implements Runnable {
        private final int size;
        
        public SimulatedIoTask(int size) {
            this.size = size;
        }
        
        @Override
        public void run() {
            // Generate random data to simulate I/O work
            byte[] data = new byte[size];
            new Random().nextBytes(data);
            
            try {
                // Simulate brief computation to mimic processing overhead
                computeCheckSum(data);
                
                // Simulate write operation with simulated I/O delay
                // We use VirtualThread blocking simulation more effectively
                simulateIoDelay(2); // Simulate 2ms I/O delay for writing
                
                // Simulate network-style backpressure with slight delay
                simulateIoDelay(1); // Extra 1ms delay to simulate processing
                
                // Simulate the time to set up a read operation context
                if (size > 512) {  // Only add significant delays for larger I/O operations
                    simulateIoDelay(1);
                }
                
                // Small amount of actual computation to avoid optimizing away the loop
                processBuffers(data);
            } catch (Exception e) {
                throw new RuntimeException("Simulated I/O operation failed", e);
            }
        }
        
        // Use Thread.sleep or TimeUnit for blocking I/O simulation
        // In virtual threads this will suspend the thread but not the carrier OS thread
        private void simulateIoDelay(int ms) throws InterruptedException {
            Thread.sleep(ms);
        }
        
        // Helper method to process buffers with simulated realistic computation
        private void processBuffers(byte[] data) throws InterruptedException {
            // Simulate chunk-by-chunk I/O processing for larger items
            if (data.length > 2048) {
                for (int i = 0; i < data.length; i += 1024) {
                    // Process in blocks
                    int endIndex = Math.min(i + 1024, data.length);
                    
                    // Introduce I/O-like delays periodically
                    if (i % 4096 == 0) {  // Every 4KB
                        Thread.sleep(1);  // Realistic I/O pause
                    }
                }
            }
        }
        
        // Helper method to add some computational overhead to simulate real processing
        private long computeCheckSum(byte[] data) {
            long sum = 0;
            for (byte b : data) {
                sum += (b & 0xFF);
            }
            return sum;
        }
    }
}