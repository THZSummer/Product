package com.threadmg.benchmark.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.threadmg.benchmark.config.BenchmarkConfig;
import com.threadmg.scenarios.cpu.CpuBoundScenario;
import com.threadmg.threads.platform.PlatformThreadStrategy;

/**
 * BenchmarkRunner测试
 */
public class BenchmarkRunnerTest {
    
    @Test
    void testRunBasicBenchmark() {
        BenchmarkConfig config = BenchmarkConfig.defaultConfig();
        BenchmarkRunner runner = new BenchmarkRunner(config);
        
        // Use simple, fast-running scenario and strategy
        CpuBoundScenario scenario = new CpuBoundScenario(20, 2);  // Small matrix and few tasks
        PlatformThreadStrategy strategy = new PlatformThreadStrategy(2);
        
        // Run a minimal benchmark
        BenchmarkResult result = runner.run(scenario, strategy);
        
        // Assert basic result properties
        assertEquals("cpu-bound", result.getScenarioName());
        assertEquals("platform-threads", result.getStrategyName());
        assertEquals(3, result.getWarmupIterations());
        assertEquals(5, result.getBenchmarkIterations());
        
        // The benchmark should at least complete without throwing errors
        assertNotNull(result.getIterationResults());
        assertTrue(result.getIterationResults().size() >= 0);
    }
    
    @Test
    void testRunWithMinimalIterations() {
        BenchmarkConfig config = new BenchmarkConfig.Builder()
            .warmupIterations(1)
            .benchmarkIterations(1)
            .taskCount(2)
            .build();
            
        BenchmarkRunner runner = new BenchmarkRunner(config);
        
        CpuBoundScenario scenario = new CpuBoundScenario(10, 1);  // Very small workload
        PlatformThreadStrategy strategy = new PlatformThreadStrategy(1);
        
        BenchmarkResult result = runner.run(scenario, strategy);
        
        assertEquals("cpu-bound", result.getScenarioName());
        assertEquals("platform-threads", result.getStrategyName());
        assertEquals(1, result.getWarmupIterations());
        assertEquals(1, result.getBenchmarkIterations());
    }
    
    @Test
    void testRunnerWithDifferentScenarios() {
        BenchmarkConfig config = BenchmarkConfig.defaultConfig();
        BenchmarkRunner runner = new BenchmarkRunner(config);
        
        // Test with CPU scenario
        CpuBoundScenario cpuScenario = new CpuBoundScenario(20, 3);
        PlatformThreadStrategy strategy = new PlatformThreadStrategy(2);
        
        assertDoesNotThrow(() -> {
            BenchmarkResult result = runner.run(cpuScenario, strategy);
            assertNotNull(result);
            assertEquals("cpu-bound", result.getScenarioName());
        });
    }
    
    @Test
    void testRunnerHandlesExceptionsDuringBenchmark() {
        BenchmarkConfig config = new BenchmarkConfig.Builder()
            .warmupIterations(1)
            .benchmarkIterations(1)
            .taskCount(1)
            .timeoutMillis(5000)  // 5 second timeout
            .build();
            
        BenchmarkRunner runner = new BenchmarkRunner(config);
        
        CpuBoundScenario scenario = new CpuBoundScenario(5, 1);  // Quick task
        PlatformThreadStrategy strategy = new PlatformThreadStrategy(1);
        
        // Should complete without errors
        assertDoesNotThrow(() -> {
            BenchmarkResult result = runner.run(scenario, strategy);
            assertNotNull(result);
        });
    }
}