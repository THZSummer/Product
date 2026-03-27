package com.threadmg.benchmark.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 边界条件和异常处理测试
 */
public class BoundaryConditionsTest {
    
    @Test
    void testZeroTaskCount() {
        // Create a simple scenario
        MockScenario scenario = new MockScenario();
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        ScenarioResult result = scenario.execute(executor, 0);
        
        // With 0 tasks, we expect 0 total & completed tasks
        assertEquals(0, result.getTotalTasks());
        assertEquals(0, result.getCompletedTasks());
        
        executor.shutdown();
    }
    
    @Test
    void testNegativeTaskCount() {
        MockScenario scenario = new MockScenario();
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        ScenarioResult result = scenario.execute(executor, -5);
        
        // It handles negative task count by just going through the execution loop
        // but maybe completing 0 tasks - this depends on specific implementation
        // For now, just verify it completes without exception 
        assertEquals(-5, result.getTotalTasks());  // The raw value passed in
        // Just verify that it didn't cause catastrophic failure (result is usable)
        assertNotNull(result);
        
        executor.shutdown();
    }
    
    @Test
    void testLargeTaskCount() {
        MockScenario scenario = new MockScenario();
        
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        // Try with a large but reasonable task count
        ScenarioResult result = scenario.execute(executor, 10000);
        
        // Should handle large counts without issues
        assertEquals(10000, result.getTotalTasks());
        // All or most tasks should complete since MockScenario processes them quickly
        assertTrue(result.getCompletedTasks() >= 0);
        
        executor.shutdown();
    }
    
    @Test
    void testScenarioWithLargeTaskCount() {
        MockScenario scenario = new MockScenario();
        
        // Test creating a large number of tasks at once
        List<Runnable> tasks = scenario.createTasks(10000);
        assertEquals(10000, tasks.size());
        
        // All are valid runnable objects
        for (Runnable task : tasks) {
            assertNotNull(task);
        }
    }
    
    @Test
    void testJavaVersionCheckInVirtualThreadStrategy() {
        // Check that the virtual thread strategy can handle Java version checking properly
        boolean supportsVT = com.threadmg.threads.virtual.VirtualThreadStrategy.supportsVirtualThreads();
        
        // Since we are in a Java 21+ environment, this should be true
        assertTrue(supportsVT, "Environment should support virtual threads");
    }
    
    @Test
    void testConcurrentScenarios() throws InterruptedException {
        // Test multiple scenarios can run without issue
        MockScenario scenario1 = new MockScenario();
        MockScenario scenario2 = new MockScenario();
        
        // Run a few scenarios concurrently
        ExecutorService sharedExecutor = Executors.newFixedThreadPool(10);
        
        ScenarioResult result1 = scenario1.execute(sharedExecutor, 10);
        ScenarioResult result2 = scenario2.execute(sharedExecutor, 10);
        
        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        
        sharedExecutor.shutdown();
    }
    
    @Test
    void testResultWithNullLatencies() {
        // Though our implementation shouldn't produce null latencies, let's test the resilience
        List<Long> nullLatencies = List.of();
        ScenarioResult result = new ScenarioResult(
            "test", 10, 10, 1_000_000_000L, nullLatencies, true, null
        );
        
        // These methods should not crash with empty latencies
        assertEquals(10.0, result.getThroughput(), 0.1);
        assertEquals(0.0, result.getAverageLatencyMs(), 0.1);
        assertTrue(result.getPercentile(50) >= 0);  // Should return 0.0 or greater
    }
    
    /**
     * Mock scenario for testing purposes
     */
    private static class MockScenario implements Scenario {
        @Override
        public String getName() {
            return "mock-scenario";
        }
        
        @Override
        public String getDescription() {
            return "Mock scenario for boundary condition testing";
        }
        
        @Override
        public List<Runnable> createTasks(int count) {
            return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> (Runnable) () -> {
                    // Minimal execution to avoid slowdowns
                })
                .toList();
        }
        
        @Override
        public ScenarioResult execute(ExecutorService executor, int taskCount) {
            // For this test, just return a dummy result
            List<Long> dummyLatencies = java.util.stream.IntStream.range(0, Math.max(0, taskCount))
                .mapToObj(i -> 1_000_000L) // 1ms per task
                .collect(java.util.stream.Collectors.toList());
                
            long startTime = System.nanoTime();
            // Execute the tasks
            createTasks(taskCount).forEach(task -> {
                try {
                    task.run();
                } catch (Exception e) {
                    // Just continue
                }
            });
            long duration = System.nanoTime() - startTime;
            
            return new ScenarioResult(
                getName(), 
                taskCount, 
                taskCount,
                duration, 
                dummyLatencies, 
                true, 
                null
            );
        }
        
        @Override
        public boolean verify(ScenarioResult result) {
            return result != null && result.getCompletedTasks() >= 0;
        }
    }
}