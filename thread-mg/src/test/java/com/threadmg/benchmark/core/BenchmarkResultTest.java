package com.threadmg.benchmark.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * BenchmarkResult测试
 */
public class BenchmarkResultTest {
    
    @Test
    void testBasicConstruction() {
        List<ScenarioResult> iterationResults = List.of(
            createSampleScenarioResult(10.0, 5.0),
            createSampleScenarioResult(15.0, 4.0),
            createSampleScenarioResult(20.0, 6.0)
        );
        
        BenchmarkResult result = new BenchmarkResult(
            "cpu-test",
            "platform-strategy",
            3,
            5,
            iterationResults
        );
        
        assertEquals("cpu-test", result.getScenarioName());
        assertEquals("platform-strategy", result.getStrategyName());
        assertEquals(3, result.getWarmupIterations());
        assertEquals(5, result.getBenchmarkIterations());
        assertEquals(3, result.getIterationResults().size());
        assertNotNull(result.getTimestamp());
    }
    
    @Test
    void testAverageThroughputCalculation() {
        List<ScenarioResult> iterationResults = List.of(
            createSampleScenarioResult(10.0, 5.0),   // throughput = 10.0
            createSampleScenarioResult(15.0, 4.0),   // throughput = 15.0
            createSampleScenarioResult(20.0, 6.0)    // throughput = 20.0
        );
        
        BenchmarkResult result = new BenchmarkResult(
            "test", "strategy", 1, 3, iterationResults
        );
        
        // Average of 10.0, 15.0, 20.0 = 15.0  
        // But since BenchmarkResult uses result.getAverageThroughput() which delegates
        // to MetricCollector, we might get a different calculation result based on 
        // how the individual throughput values are aggregated.
        // Let's calculate what the expected value is based on the actual implementation
        double expectedThroughput = (10.0 + 15.0 + 20.0) / 3.0;
        assertEquals(expectedThroughput, result.getAverageThroughput(), 0.01);
    }
    
    @Test
    void testStandardDeviation() {
        List<ScenarioResult> iterationResults = List.of(
            createSampleScenarioResult(10.0, 5.0),
            createSampleScenarioResult(10.0, 5.0),
            createSampleScenarioResult(10.0, 5.0)
        );
        
        BenchmarkResult result = new BenchmarkResult(
            "test", "strategy", 1, 3, iterationResults
        );
        
        // Standard deviation of identical values should be 0
        assertEquals(0.0, result.getStandardDeviation(), 0.01);
    }
    
    @Test
    void testEmptyResults() {
        List<ScenarioResult> emptyResults = List.of();
        
        BenchmarkResult result = new BenchmarkResult(
            "test", "strategy", 1, 1, emptyResults
        );
        
        assertEquals(0.0, result.getAverageThroughput(), 0.01);
        assertEquals(0.0, result.getStandardDeviation(), 0.01);
        assertEquals(0.0, result.getP50Latency(), 0.01);
        assertEquals(0.0, result.getP95Latency(), 0.01);
        assertEquals(0.0, result.getP99Latency(), 0.01);
    }
    
    /**
     * Helper method to create sample ScenarioResult instances for testing
     * @param throughput The throughput that the resulting ScenarioResult.getThroughput() should return
     * @param avgLatencyMs The average latency in milliseconds
     */
    private ScenarioResult createSampleScenarioResult(double throughput, double avgLatencyMs) {
        // To achieve the specified throughput, adjust the total task count and/or duration accordingly
        // Throughput = completedTasks / (durationInSeconds)
        // If we keep completedTasks=10, then durationNs = (completedTasks / throughput) * 1_000_000_000
        int completedTasks = 10;  // Fixed task count to simplify calculation
        long durationNs = (long) ((completedTasks / throughput) * 1_000_000_000L);

        // Create appropriate latencies to achieve our desired avgLatencyMs
        List<Long> latencies = List.of(
            (long)(avgLatencyMs * 1_000_000),  // Convert to nanos
            (long)(avgLatencyMs * 1_000_000),  // Make them all the same for predictable average
            (long)(avgLatencyMs * 1_000_000)
        );

        return new ScenarioResult(
            "test-scenario", 
            completedTasks,           // Assuming totalTasks same as completed for testing
            completedTasks, 
            durationNs, 
            latencies, 
            true, 
            null
        );
    }
}