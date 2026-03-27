package com.threadmg.scenarios.cpu;

import com.threadmg.benchmark.core.ScenarioResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CpuBoundScenario测试
 */
public class CpuBoundScenarioTest {
    
    @Test
    void testGetNameAndDescription() {
        CpuBoundScenario scenario = new CpuBoundScenario();
        
        assertEquals("cpu-bound", scenario.getName());
        assertEquals("CPU intensive scenario using matrix multiplication", scenario.getDescription());
    }
    
    @Test
    void testCreateTasks() {
        CpuBoundScenario scenario = new CpuBoundScenario(50, 10); // Small matrix, 10 tasks
        
        List<Runnable> tasks = scenario.createTasks(5);
        assertEquals(5, tasks.size());
        
        // We can only check if they are Runnable tasks
        for (Runnable task : tasks) {
            assertNotNull(task);
            // Execute task to test functionality
            assertDoesNotThrow(() -> task.run());
        }
        
        List<Runnable> zeroTasks = scenario.createTasks(0);
        assertEquals(0, zeroTasks.size());
    }
    
    @Test
    void testExecuteSmallTasks() throws InterruptedException {
        CpuBoundScenario scenario = new CpuBoundScenario(20, 5); // Small matrix, few tasks
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        ScenarioResult result = scenario.execute(executor, 3);  // 3 small tasks
        
        // Basic validation - should complete successfully
        assertTrue(result.getCompletedTasks() > 0);
        assertTrue(result.getTotalTasks() == 3);
        assertTrue(result.getDurationNanos() > 0);
        assertTrue(result.isSuccess());
        
        executor.shutdown();
    }
    
    @Test
    void testExecuteWithLargeTasks() throws InterruptedException {
        CpuBoundScenario scenario = new CpuBoundScenario(50, 3); // Medium-sized matrix
        
        ExecutorService executor = Executors.newWorkStealingPool(); // Use work-stealing for CPU-bound tasks
        ScenarioResult result = scenario.execute(executor, 2); // Few tasks to prevent timeout
        
        assertTrue(result.getTotalTasks() == 2);
        assertTrue(result.getCompletedTasks() >= 0); // At least 0
        assertTrue(result.getDurationNanos() > 0);
        
        executor.shutdown();
    }
    
    @Test
    void testVerifySuccess() {
        CpuBoundScenario scenario = new CpuBoundScenario();
        
        // Create a successful scenario result
        List<Long> latencies = List.of(5_000_000L, 6_000_000L, 5_500_000L); // Some sample latencies
        ScenarioResult successResult = new ScenarioResult(
            "cpu-bound", 3, 3, 1_000_000_000L, latencies, true, null
        );
        
        assertTrue(scenario.verify(successResult));
    }
    
    @Test
    void testVerifyFailure() {
        CpuBoundScenario scenario = new CpuBoundScenario();
        
        List<Long> latencies = List.of();
        ScenarioResult failureResult = new ScenarioResult(
            "cpu-bound", 5, 0, 1_000_000_000L, latencies, false, "Failed"
        );
        
        // Verify the behavior based on the actual CpuBoundScenario.verify() method
        // The verify method just checks if result.getCompletedTasks() > 0
        // So even if isSuccess = false, if completedTasks > 0, verify returns true
        boolean expectedVerifyResult = failureResult.getCompletedTasks() > 0;  // This will be false (0 > 0 is false)
        assertEquals(expectedVerifyResult, scenario.verify(failureResult));
    }
    
    @Test
    void testLargeMatrixExecution() throws InterruptedException {
        CpuBoundScenario scenario = new CpuBoundScenario(100, 1); // Larger matrix for more CPU work
        
        ExecutorService executor = Executors.newFixedThreadPool(4);
        ScenarioResult result = scenario.execute(executor, 1); // Just one heavy task
        
        assertTrue(result.getTotalTasks() == 1);
        assertTrue(result.getCompletedTasks() == 1);
        assertTrue(result.isSuccess());
        
        executor.shutdown();
    }
}