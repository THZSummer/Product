package com.threadmg.benchmark.core;

import com.threadmg.benchmark.config.BenchmarkConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 基准测试运行器
 * 协调场景和策略的执行
 */
public class BenchmarkRunner {
    private final BenchmarkConfig config;
    
    public BenchmarkRunner(BenchmarkConfig config) {
        this.config = config;
    }

    public BenchmarkResult run(Scenario scenario, ThreadStrategy strategy) {
        // Run warmup iterations
        if (config.isVerbose()) {
            System.out.println("Starting warmup for " + scenario.getName() + 
                             " with " + strategy.getName() + "...");
        }
        List<ScenarioResult> warmupResults = runIterations(
            scenario, strategy, config.getWarmupIterations(), true);

        // Run benchmark iterations
        if (config.isVerbose()) {
            System.out.println("Starting benchmark for " + scenario.getName() + 
                             " with " + strategy.getName() + "...");
        }
        List<ScenarioResult> benchmarkResults = runIterations(
            scenario, strategy, config.getBenchmarkIterations(), false);
        
        if (config.isVerbose()) {
            System.out.println("Benchmark completed.");
        }
        
        // Return aggregated results
        return new BenchmarkResult(
            scenario.getName(),
            strategy.getName(),
            config.getWarmupIterations(),
            config.getBenchmarkIterations(),
            benchmarkResults
        );
    }
    
    private List<ScenarioResult> runIterations(
            Scenario scenario, 
            ThreadStrategy strategy, 
            int iterations,
            boolean isWarmup) {
        List<ScenarioResult> results = new ArrayList<>();
        
        for (int i = 0; i < iterations; i++) {
            if (config.isVerbose() && !isWarmup) {
                System.out.printf("Running iteration %d/%d%n", i + 1, iterations);
            }
            
            ExecutorService executor = strategy.createExecutor();
            
            try {
                ScenarioResult result = scenario.execute(executor, config.getTaskCount());
                results.add(result);
                
                // Shutdown the executor after each iteration to get clean state
                strategy.shutdown();
                
            } catch (Exception e) {
                System.err.println("Error in iteration " + (i + 1) + ": " + e.getMessage());
                strategy.shutdown();
                
                // Add error result
                List<Long> emptyLatencies = new ArrayList<>();
                ScenarioResult errorResult = new ScenarioResult(
                    scenario.getName(),
                    config.getTaskCount(),
                    0,
                    0,
                    emptyLatencies,
                    false,
                    e.getMessage()
                );
                results.add(errorResult);
            }
        }
        
        return results;
    }
}