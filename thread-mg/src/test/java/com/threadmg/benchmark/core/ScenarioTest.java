package com.threadmg.benchmark.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Scenario接口的测试
 */
public class ScenarioTest {
    
    private MockScenario mockScenario;
    
    @BeforeEach
    void setUp() {
        mockScenario = new MockScenario();
    }
    
    @Test
    void testNameAndDescription() {
        assertEquals("mock-scenario", mockScenario.getName());
        assertEquals("A mock scenario for testing", mockScenario.getDescription());
    }
    
    @Test
    void testCreateTasks() {
        List<Runnable> tasks = mockScenario.createTasks(5);
        assertEquals(5, tasks.size());
        
        List<Runnable> zeroTasks = mockScenario.createTasks(0);
        assertEquals(0, zeroTasks.size());
    }
    
    @Test
    void testExecute() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        ScenarioResult result = mockScenario.execute(executor, 3);
        
        assertTrue(result.isSuccess());
        assertEquals(3, result.getTotalTasks());
        assertEquals(3, result.getCompletedTasks());
        assertNotNull(result.getTaskLatenciesNanos());
        
        executor.shutdown();
    }
    
    @Test
    void testVerify() {
        // Create a proper ScenarioResult for successful operation
        List<Long> latencies = List.of(1000000L, 1500000L, 1200000L); // 1ms, 1.5ms, 1.2ms
        ScenarioResult successResult = new ScenarioResult(
            "test", 3, 3, 1000000000L, latencies, true, null
        );
        assertTrue(mockScenario.verify(successResult));
        
        ScenarioResult failureResult = new ScenarioResult(
            "test", 3, 2, 1000000000L, latencies, false, "some error"
        );
        assertFalse(mockScenario.verify(failureResult));
    }
    
    /**
     * Mock implementation of Scenario for testing purposes
     */
    private static class MockScenario implements Scenario {
        private int executionCount = 0;
        
        @Override
        public String getName() {
            return "mock-scenario";
        }
        
        @Override
        public String getDescription() {
            return "A mock scenario for testing";
        }
        
        @Override
        public List<Runnable> createTasks(int count) {
            return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> (Runnable) () -> {
                    executionCount++;
                    try {
                        Thread.sleep(10); // Small delay to simulate work
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                })
                .toList();
        }
        
        @Override
        public ScenarioResult execute(ExecutorService executor, int taskCount) {
            long startTime = System.nanoTime();
            List<Runnable> tasks = createTasks(taskCount);
            
            List<java.util.concurrent.Future<?>> futures = new java.util.ArrayList<>();
            List<Long> latencies = new java.util.ArrayList<>();
            
            for (Runnable task : tasks) {
                long taskStart = System.nanoTime();
                futures.add(executor.submit(task));
                long latency = System.nanoTime() - taskStart;
                latencies.add(latency);
            }
            
            // Wait for all tasks to complete
            futures.forEach(f -> {
                try {
                    f.get();
                } catch (Exception e) {
                    // Handle exception gracefully
                }
            });
            
            long totalTime = System.nanoTime() - startTime;
            return new ScenarioResult(
                getName(), 
                taskCount, 
                taskCount, 
                totalTime, 
                latencies, 
                true, 
                null
            );
        }
        
        @Override
        public boolean verify(ScenarioResult result) {
            return result.isSuccess() && result.getCompletedTasks() > 0;
        }
    }
}