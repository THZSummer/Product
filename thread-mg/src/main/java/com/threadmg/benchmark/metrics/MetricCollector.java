package com.threadmg.benchmark.metrics;

import com.threadmg.benchmark.core.BenchmarkResult;
import com.threadmg.benchmark.core.ScenarioResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 性能指标收集器
 */
public class MetricCollector {
    
    public MetricCollector() {}
    
    public BenchmarkResult aggregate(
            String scenarioName,
            String strategyName,
            List<ScenarioResult> iterations) {
        
        // Calculate average throughput
        double avgThroughput = iterations.stream()
            .mapToDouble(ScenarioResult::getThroughput)
            .average()
            .orElse(0.0);
        
        // Collect all latencies from all iterations (for percentile calculations)
        List<Double> allLatenciesMs = new ArrayList<>();
        for (ScenarioResult result : iterations) {
            if (result.getTaskLatenciesNanos() != null) {
                for (Long latencyNanos : result.getTaskLatenciesNanos()) {
                    allLatenciesMs.add(latencyNanos / 1_000_000.0); // Convert to ms
                }
            } else {
                // If individual latencies are not available, use average for each iteration
                double avgFromThisIteration = result.getAverageLatencyMs();
                if (avgFromThisIteration > 0) {
                    // This is less granular but still captures some information
                    for (int i = 0; i < 5; i++) { // Adding it 5 times as approximation
                        allLatenciesMs.add(avgFromThisIteration);
                    }
                }
            }
        }
        
        // Sort for percentile calculations
        Collections.sort(allLatenciesMs);
        
        double p50 = calculatePercentile(allLatenciesMs, 50);
        double p95 = calculatePercentile(allLatenciesMs, 95);
        double p99 = calculatePercentile(allLatenciesMs, 99);
        
        // Return a new BenchmarkResult with the computed metrics
        // We will just pass through the original ScenarioResults
        return new BenchmarkResult(scenarioName, strategyName, 0, iterations.size(), iterations);
    }
    
    private double calculatePercentile(List<Double> sortedData, double percentile) {
        if (sortedData.isEmpty()) {
            return 0.0;
        }
        
        int index = (int) Math.ceil(percentile / 100.0 * sortedData.size()) - 1;
        index = Math.max(0, Math.min(index, sortedData.size() - 1));
        
        return sortedData.get(index);
    }
    
    // Additional utility methods for calculating various metrics
    
    public double calculateAverage(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    
    public double calculateStandardDeviation(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        
        double mean = calculateAverage(values);
        double sumSquaredDiff = values.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .sum();
            
        return Math.sqrt(sumSquaredDiff / values.size());
    }
}