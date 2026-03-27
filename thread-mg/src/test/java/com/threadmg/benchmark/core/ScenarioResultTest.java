package com.threadmg.benchmark.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * ScenarioResult测试
 */
public class ScenarioResultTest {
    
    @Test
    void testBasicConstruction() {
        List<Long> latencies = List.of(1_000_000L, 2_000_000L, 3_000_000L); // 1ms, 2ms, 3ms in nanos
        ScenarioResult result = new ScenarioResult(
            "test-scenario", 10, 10, 1_000_000_000L, latencies, true, null
        );
        
        assertEquals("test-scenario", result.getScenarioName());
        assertEquals(10, result.getTotalTasks());
        assertEquals(10, result.getCompletedTasks());
        assertEquals(1_000_000_000L, result.getDurationNanos());
        assertTrue(result.isSuccess());
        assertNull(result.getErrorMessage());
        assertNotNull(result.getTimestamp());
        
        // Check calculations
        assertEquals(10.0, result.getThroughput(), 0.1); // 10 tasks in 1 sec
        assertEquals(2.0, result.getAverageLatencyMs(), 0.1); // (1+2+3)/3 = 2ms avg
    }
    
    @Test
    void testCalculationsForZeroValues() {
        List<Long> emptyLatencies = List.of();
        ScenarioResult result = new ScenarioResult(
            "test-scenario", 0, 0, 0, emptyLatencies, true, null
        );
        
        assertEquals(0.0, result.getThroughput(), 0.1);
        assertEquals(0.0, result.getAverageLatencyMs(), 0.1);
    }
    
    @Test
    void testPercentileCalculation() {
        // Create ordered latencies: 10ms, 20ms, 30ms, 40ms, 50ms
        List<Long> latencies = List.of(10_000_000L, 20_000_000L, 30_000_000L, 40_000_000L, 50_000_000L);
        ScenarioResult result = new ScenarioResult(
            "test-scenario", 5, 5, 1_000_000_000L, latencies, true, null
        );
        
        assertEquals(10.0, result.getPercentile(10), 0.1);  // Closest to first
        assertEquals(20.0, result.getPercentile(25), 0.1);  // Approx 25th
        assertEquals(30.0, result.getPercentile(50), 0.1);  // Median
        assertEquals(50.0, result.getPercentile(90), 0.1);  // Closest to last
    }
    
    @Test
    void testFailureCase() {
        List<Long> latencies = List.of();
        ScenarioResult result = new ScenarioResult(
            "test-scenario", 10, 5, 1_000_000_000L, latencies, false, "Some error occurred"
        );
        
        assertFalse(result.isSuccess());
        assertEquals("Some error occurred", result.getErrorMessage());
        assertEquals(5, result.getCompletedTasks());
    }
    
    @Test
    void testThroughputCalculation() {
        List<Long> latencies = List.of();
        ScenarioResult result = new ScenarioResult(
            "test-scenario", 20, 20, 2_000_000_000L, latencies, true, null  // 20 tasks in 2 seconds
        );
        
        assertEquals(10.0, result.getThroughput(), 0.1);  // 20/2 = 10 tasks/sec
    }
}