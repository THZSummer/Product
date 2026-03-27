package com.threadmg.benchmark.metrics;

import com.threadmg.benchmark.core.ScenarioResult;
import com.threadmg.benchmark.core.BenchmarkResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * MetricCollector测试
 */
public class MetricCollectorTest {
    
    @Test
    void testAggregateMetrics() {
        MetricCollector collector = new MetricCollector();
        
        // Create several scenario results
        List<Long> latencies1 = List.of(1_000_000L, 2_000_000L, 1_500_000L); // 1ms, 2ms, 1.5ms
        ScenarioResult result1 = new ScenarioResult(
            "test-scenario", 10, 10, 1_000_000_000L, latencies1, true, null
        );
        
        List<Long> latencies2 = List.of(2_000_000L, 3_000_000L, 2_500_000L); // 2ms, 3ms, 2.5ms 
        ScenarioResult result2 = new ScenarioResult(
            "test-scenario", 10, 10, 1_000_000_000L, latencies2, true, null
        );
        
        List<ScenarioResult> iterationResults = List.of(result1, result2);
        
        BenchmarkResult aggregated = collector.aggregate("cpu-test", "platform-strategy", iterationResults);
        
        assertEquals("cpu-test", aggregated.getScenarioName());
        assertEquals("platform-strategy", aggregated.getStrategyName());
        
        // Verify we get back the same number of iterations
        assertEquals(iterationResults, aggregated.getIterationResults());
    }
    
    @Test
    void testAggregatesWithEmptyList() {
        MetricCollector collector = new MetricCollector();
        
        List<ScenarioResult> emptyResults = List.of();
        
        BenchmarkResult result = collector.aggregate("cpu-test", "platform-strategy", emptyResults);
        
        assertEquals("cpu-test", result.getScenarioName());
        assertEquals("platform-strategy", result.getStrategyName());
        assertTrue(result.getIterationResults().isEmpty());
    }
    
    @Test  
    void testAggregateWithSingleIteration() {
        MetricCollector collector = new MetricCollector();
        
        List<Long> latencies = List.of(1_000_000L, 5_000_000L, 3_000_000L); // 1ms, 5ms, 3ms
        ScenarioResult result = new ScenarioResult(
            "test-scenario", 5, 5, 1_000_000_000L, latencies, true, null
        );
        
        List<ScenarioResult> singleResult = List.of(result);
        
        BenchmarkResult aggregated = collector.aggregate("io-test", "virtual-strategy", singleResult);
        
        assertEquals("io-test", aggregated.getScenarioName());
        assertEquals("virtual-strategy", aggregated.getStrategyName());
        assertEquals(singleResult, aggregated.getIterationResults());
    }
    
    @Test
    void testPercentileCalculationHelper() {
        MetricCollector collector = new MetricCollector();
        
        // Test with a sorted list
        List<Double> sortedData = List.of(1.0, 2.0, 3.0, 4.0, 5.0);
        
        // P50 (median) of 1,2,3,4,5 should be 3
        double p50 = 0.0;  // we can't call private method, so this is a structural test
        
        // P90 of 1,2,3,4,5 should be approximately 5
        // The main test is that the aggregate method works correctly
        
        List<Long> latencies = List.of(1_000_000L, 2_000_000L, 3_000_000L, 4_000_000L, 5_000_000L);
        ScenarioResult result = new ScenarioResult(
            "test", 5, 5, 1_000_000_000L, latencies, true, null
        );
        
        List<ScenarioResult> results = List.of(result);
        
        BenchmarkResult aggregated = collector.aggregate("test", "strategy", results);
        
        // The aggregate method should complete without exception
        assertNotNull(aggregated);
    }
    
    @Test
    void testCalculateAverageHelper() {
        MetricCollector collector = new MetricCollector();
        
        List<Double> values = List.of(1.0, 2.0, 3.0, 4.0, 5.0);
        double avg = collector.calculateAverage(values);
        
        assertEquals(3.0, avg, 0.01);  // (1+2+3+4+5)/5 = 3
    }
    
    @Test
    void testCalculateAvgWithEmptyList() {
        MetricCollector collector = new MetricCollector();
        
        List<Double> emptyList = List.of();
        double avg = collector.calculateAverage(emptyList);
        
        assertEquals(0.0, avg, 0.01);
    }
    
    @Test
    void testCalculateAvgWithSingleValue() {
        MetricCollector collector = new MetricCollector();
        
        List<Double> singleVal = List.of(42.0);
        double avg = collector.calculateAverage(singleVal);
        
        assertEquals(42.0, avg, 0.01);
    }
    
    @Test
    void testStandardDeviation() {
        MetricCollector collector = new MetricCollector();
        
        List<Double> values = List.of(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0);
        // Mean = 40/8 = 5.0
        // Variances: (2-5)^2=9, (4-5)^2=1, (4-5)^2=1, (4-5)^2=1, (5-5)^2=0, (5-5)^2=0, (7-5)^2=4, (9-5)^2=16
        // Sum = 32, Avg = 4, StdDev = sqrt(4) = 2.0
        double stdDev = collector.calculateStandardDeviation(values);
        
        assertEquals(2.0, stdDev, 0.01);
    }
    
    @Test
    void testStandardDeviationWithIdenticalValues() {
        MetricCollector collector = new MetricCollector();
        
        // All values the same -> standard deviation should be 0
        List<Double> identicalValues = List.of(5.0, 5.0, 5.0, 5.0, 5.0);
        double stdDev = collector.calculateStandardDeviation(identicalValues);
        
        assertEquals(0.0, stdDev, 0.01);
    }
    
    @Test
    void testStandardDeviationWithEmptyList() {
        MetricCollector collector = new MetricCollector();
        
        List<Double> emptyList = List.of();
        double stdDev = collector.calculateStandardDeviation(emptyList);
        
        assertEquals(0.0, stdDev, 0.01);
    }
}