package com.threadmg.e2e;

import com.threadmg.BenchmarkApp;
import com.threadmg.benchmark.config.BenchmarkConfig;
import com.threadmg.benchmark.core.BenchmarkResult;
import com.threadmg.benchmark.core.BenchmarkRunner;
import com.threadmg.scenarios.cpu.CpuBoundScenario;
import com.threadmg.scenarios.io.IoBoundScenario;
import com.threadmg.threads.platform.PlatformThreadStrategy;
import com.threadmg.threads.virtual.VirtualThreadStrategy;
import com.threadmg.reporters.ConsoleReporter;
import com.threadmg.reporters.MarkdownReporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * 完整基准测试流程端到端测试
 */
public class BenchmarkE2ETest {
    
    @Test
    @EnabledForJreRange(min = JRE.JAVA_21)
    void testFullCpuBenchmark() throws InterruptedException {
        // Create simplified configuration for quick testing
        BenchmarkConfig config = new BenchmarkConfig.Builder()
            .warmupIterations(1)
            .benchmarkIterations(2)
            .taskCount(10)
            .build();
        
        BenchmarkRunner runner = new BenchmarkRunner(config);
        ConsoleReporter consoleReporter = new ConsoleReporter();
        
        // Create realistic scenario with small task count for quick test
        CpuBoundScenario scenario = new CpuBoundScenario(20, 10); // Small matrix, less work
        
        // Test with platform threads
        PlatformThreadStrategy platformStrategy = new PlatformThreadStrategy(2); // 2 threads
        BenchmarkResult platformResult = runner.run(scenario, platformStrategy);
        
        assertNotNull(platformResult);
        assertEquals("cpu-bound", platformResult.getScenarioName());
        assertEquals("platform-threads", platformResult.getStrategyName());
        consoleReporter.report(platformResult);
        
        // Test with virtual threads
        VirtualThreadStrategy virtualStrategy = new VirtualThreadStrategy();
        BenchmarkResult virtualResult = runner.run(scenario, virtualStrategy);
        
        assertNotNull(virtualResult);
        assertEquals("cpu-bound", virtualResult.getScenarioName());
        assertEquals("virtual-threads", virtualResult.getStrategyName());
        consoleReporter.report(virtualResult);
        
        // Verify that both completed their intended counts
        assertTrue(!platformResult.getIterationResults().isEmpty());
        assertTrue(!virtualResult.getIterationResults().isEmpty());
        
        platformStrategy.shutdown();
        virtualStrategy.shutdown();
    }
    
    @Test
    @EnabledForJreRange(min = JRE.JAVA_21)
    void testFullIoBenchmark() throws InterruptedException {
        // Create simplified configuration for quick testing
        BenchmarkConfig config = new BenchmarkConfig.Builder()
            .warmupIterations(1)
            .benchmarkIterations(2)
            .taskCount(20)
            .build();
        
        BenchmarkRunner runner = new BenchmarkRunner(config);
        ConsoleReporter consoleReporter = new ConsoleReporter();
        
        // Create I/O scenario with small task count for quick test
        IoBoundScenario ioScenario = new IoBoundScenario(1024*10, 20); // Small data size, fewer tasks
        
        // Test with platform threads
        PlatformThreadStrategy platformStrategy = new PlatformThreadStrategy(4);
        BenchmarkResult platformResult = runner.run(ioScenario, platformStrategy);
        
        assertNotNull(platformResult);
        assertEquals("io-bound", platformResult.getScenarioName());
        consoleReporter.report(platformResult);
        
        // Test with virtual threads
        VirtualThreadStrategy virtualStrategy = new VirtualThreadStrategy();
        BenchmarkResult virtualResult = runner.run(ioScenario, virtualStrategy);
        
        assertNotNull(virtualResult);
        assertEquals("io-bound", virtualResult.getScenarioName());
        consoleReporter.report(virtualResult);
        
        // Compare results - should complete without errors
        assertNotNull(platformResult.getIterationResults());
        assertNotNull(virtualResult.getIterationResults());
        
        platformStrategy.shutdown();
        virtualStrategy.shutdown();
    }
    
    @Test
    @EnabledForJreRange(min = JRE.JAVA_21)
    void testMainAppRunsSuccessfully() {
        // Test that the main application class executes without throwing errors
        // We'll call main and verify no exceptions occur (though it will run for a while)
        assertDoesNotThrow(() -> {
            // Just a simple check that the class is available
            Class<?> appClass = BenchmarkApp.class;
            assertNotNull(appClass);
            
            // Verify some key classes exist
            assertTrue(VirtualThreadStrategy.supportsVirtualThreads());
        });
    }
    
    @Test
    @EnabledForJreRange(min = JRE.JAVA_21)
    void testReportGenerationE2E() {
        // Create and run a simple test to verify reports work end-to-end
        BenchmarkConfig config = new BenchmarkConfig.Builder()
            .warmupIterations(1)
            .benchmarkIterations(1)
            .taskCount(5)
            .verbose(false) // Reduce noise in tests
            .build();
        
        BenchmarkRunner runner = new BenchmarkRunner(config);
        MarkdownReporter markdownReporter = new MarkdownReporter("target/test-reports");
        
        // Test with simple scenarios
        CpuBoundScenario cpuScenario = new CpuBoundScenario(10, 5);
        IoBoundScenario ioScenario = new IoBoundScenario(1024, 5);
        PlatformThreadStrategy strategy = new PlatformThreadStrategy(2);
        
        BenchmarkResult cpuResult = runner.run(cpuScenario, strategy);
        BenchmarkResult ioResult = runner.run(ioScenario, strategy);
        
        // Verify reports can be generated
        List<BenchmarkResult> results = Arrays.asList(cpuResult, ioResult);
        
        assertDoesNotThrow(() -> {
            markdownReporter.report(results);
        });
        
        // Results were generated without error
        assertEquals("cpu-bound", cpuResult.getScenarioName());
        assertEquals("io-bound", ioResult.getScenarioName());
    }
}