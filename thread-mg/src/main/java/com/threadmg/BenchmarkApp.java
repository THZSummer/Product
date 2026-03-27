package com.threadmg;

import com.threadmg.benchmark.config.BenchmarkConfig;
import com.threadmg.benchmark.core.BenchmarkResult;
import com.threadmg.benchmark.core.BenchmarkRunner;
import com.threadmg.reporters.ConsoleReporter;
import com.threadmg.reporters.HtmlReporter;
import com.threadmg.reporters.JsonReporter;
import com.threadmg.reporters.MarkdownReporter;
import com.threadmg.scenarios.cpu.CpuBoundScenario;
import com.threadmg.scenarios.io.IoBoundScenario;
import com.threadmg.threads.platform.PlatformThreadStrategy;
import com.threadmg.threads.virtual.VirtualThreadStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * 基准测试应用主入口
 * 执行完整的基准测试流程
 */
public class BenchmarkApp {
    
    public static void main(String[] args) {
        System.out.println("Starting Thread Benchmark Framework...");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Available Processors: " + Runtime.getRuntime().availableProcessors());
        
        // Check virtual thread support
        if (!VirtualThreadStrategy.supportsVirtualThreads()) {
            System.out.println("Warning: Virtual threads not supported in current Java version");
        }
        
        // Create configuration
        BenchmarkConfig config = BenchmarkConfig.defaultConfig();
        
        // Initialize components
        BenchmarkRunner runner = new BenchmarkRunner(config);
        ConsoleReporter consoleReporter = new ConsoleReporter();
        MarkdownReporter markdownReporter = new MarkdownReporter();
        HtmlReporter htmlReporter = new HtmlReporter();
        JsonReporter jsonReporter = new JsonReporter();
        
        // Define scenarios and strategies to test
        List<Object[]> testCases = new ArrayList<>();
        
        // Test case format: [scenario name, scenario instance]
        testCases.add(new Object[]{"CPU-Bound Scenario", new CpuBoundScenario()});
        testCases.add(new Object[]{"I/O-Bound Scenario", new IoBoundScenario()});
        
        // Run all combinations
        List<BenchmarkResult> allResults = new ArrayList<>();
        
        for (Object[] testCase : testCases) {
            String scenarioName = (String) testCase[0];
            var scenario = testCase[1];
            
            System.out.println("\n--- Testing " + scenarioName + " ---");
            
            // Platform threads
            System.out.println("Testing with Platform Threads...");
            var platformStrategy = new PlatformThreadStrategy();
            var platformResult = runner.run((com.threadmg.benchmark.core.Scenario) scenario, platformStrategy);
            allResults.add(platformResult);
            consoleReporter.report(platformResult);
            
            // Virtual threads
            System.out.println("Testing with Virtual Threads...");
            var virtualStrategy = new VirtualThreadStrategy();
            var virtualResult = runner.run((com.threadmg.benchmark.core.Scenario) scenario, virtualStrategy);
            allResults.add(virtualResult);
            consoleReporter.report(virtualResult);
        }
        
        // Generate comprehensive report
        System.out.println("\nGenerating comprehensive report...");
        markdownReporter.report(allResults);
        htmlReporter.report(allResults);
        jsonReporter.report(allResults);
        
        System.out.println("\nBenchmark completed successfully!");
    }
}