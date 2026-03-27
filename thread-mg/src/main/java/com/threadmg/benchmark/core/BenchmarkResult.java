package com.threadmg.benchmark.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 基准测试完整结果
 * 包含多轮迭代的聚合统计
 */
public class BenchmarkResult {
    private final String scenarioName;
    private final String strategyName;
    private final int warmupIterations;
    private final int benchmarkIterations;
    private final List<ScenarioResult> iterationResults;
    private final Instant timestamp;
    
    public BenchmarkResult(String scenarioName, String strategyName, 
                          int warmupIterations, int benchmarkIterations, 
                          List<ScenarioResult> iterationResults) {
        this.scenarioName = scenarioName;
        this.strategyName = strategyName;
        this.warmupIterations = warmupIterations;
        this.benchmarkIterations = benchmarkIterations;
        this.iterationResults = new ArrayList<>(iterationResults);
        this.timestamp = Instant.now();
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public int getWarmupIterations() {
        return warmupIterations;
    }

    public int getBenchmarkIterations() {
        return benchmarkIterations;
    }

    public List<ScenarioResult> getIterationResults() {
        return new ArrayList<>(iterationResults);
    }

    public Instant getTimestamp() {
        return timestamp;
    }
    
    public double getAverageThroughput() {
        if (iterationResults.isEmpty()) {
            return 0.0;
        }
        return iterationResults.stream()
            .mapToDouble(ScenarioResult::getThroughput)
            .average()
            .orElse(0.0);
    }
    
    public double getP50Latency() {
        return getPercentileAcrossIterations(50.0);
    }
    
    public double getP95Latency() {
        return getPercentileAcrossIterations(95.0);
    }
    
    public double getP99Latency() {
        return getPercentileAcrossIterations(99.0);
    }
    
    public double getStandardDeviation() {
        if (iterationResults.isEmpty()) {
            return 0.0;
        }
        
        double mean = getAverageThroughput();
        double sumSquaredDiff = iterationResults.stream()
            .mapToDouble(r -> Math.pow(r.getThroughput() - mean, 2))
            .sum();
            
        return Math.sqrt(sumSquaredDiff / iterationResults.size());
    }
    
    private double getPercentileAcrossIterations(double percentile) {
        // Collect all latencies from all iterations to calculate overall percentile
        List<Double> allLatencies = new ArrayList<>();
        for (ScenarioResult result : iterationResults) {
            // For simplicity here we use average latency of each iteration
            // In practice, we'd want all individual task latencies
            allLatencies.add(result.getAverageLatencyMs());
        }
        
        if (allLatencies.isEmpty()) {
            return 0.0;
        }
        
        allLatencies.sort(Double::compareTo);
        int index = (int) Math.ceil(percentile / 100.0 * allLatencies.size()) - 1;
        index = Math.max(0, Math.min(index, allLatencies.size() - 1));
        
        return allLatencies.get(index);
    }
}