package com.threadmg.reporters;

import com.threadmg.benchmark.core.BenchmarkResult;
import com.threadmg.benchmark.core.ScenarioResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConsoleReporter测试
 */
public class ConsoleReporterTest {
    
    @Test
    void testReportOutput() {
        // Capture system out to verify output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));
        
        try {
            // Create a sample benchmark result
            List<Long> latencies = List.of(1_000_000L, 2_000_000L, 3_000_000L); // 1, 2, 3ms
            ScenarioResult scenarioResult = new ScenarioResult(
                "cpu-test", 5, 5, 1_000_000_000L, latencies, true, null
            );
            
            List<ScenarioResult> iterationResults = List.of(scenarioResult);
            BenchmarkResult result = new BenchmarkResult(
                "cpu-test", "platform-threads", 2, 3, iterationResults
            );
            
            // Report the result
            ConsoleReporter reporter = new ConsoleReporter();
            reporter.report(result);
            
            // Verify the output contains expected elements
            String outputStr = output.toString();
            assertTrue(outputStr.contains("Benchmark Result: cpu-test"));
            assertTrue(outputStr.contains("Strategy: platform-threads"));
            assertTrue(outputStr.contains("Throughput:"));
            assertTrue(outputStr.contains("P50 Latency:"));
            assertTrue(outputStr.contains("========================================"));
            
        } finally {
            // Restore original system out
            System.setOut(originalOut);
        }
    }
    
    @Test
    void testReportWithZeroValues() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));
        
        try {
            List<Long> emptyLatencies = List.of();
            ScenarioResult scenarioResult = new ScenarioResult(
                "io-test", 0, 0, 0, emptyLatencies, true, null
            );
            
            List<ScenarioResult> iterationResults = List.of(scenarioResult);
            BenchmarkResult result = new BenchmarkResult(
                "io-test", "virtual-threads", 1, 1, iterationResults
            );
            
            ConsoleReporter reporter = new ConsoleReporter();
            reporter.report(result);
            
            String outputStr = output.toString();
            assertTrue(outputStr.contains("Benchmark Result: io-test"));
            assertTrue(outputStr.contains("Strategy: virtual-threads"));
            
        } finally {
            System.setOut(originalOut);
        }
    }
    
    @Test
    void testReportDoesNotThrow() {
        // Create a result and ensure reporting doesn't throw exceptions
        List<Long> latencies = List.of(1_000_000L, 2_000_000L);
        ScenarioResult scenarioResult = new ScenarioResult(
            "cpu-test", 3, 3, 1_000_000_000L, latencies, true, null
        );
        
        List<ScenarioResult> iterationResults = List.of(scenarioResult);
        BenchmarkResult result = new BenchmarkResult(
            "cpu-test", "platform-threads", 1, 2, iterationResults
        );
        
        ConsoleReporter reporter = new ConsoleReporter();
        
        // This should not throw any exceptions
        assertDoesNotThrow(() -> reporter.report(result));
    }
}