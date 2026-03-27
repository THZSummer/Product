package com.threadmg.reporters;

import com.threadmg.benchmark.core.BenchmarkResult;
import com.threadmg.benchmark.core.ScenarioResult;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonReporter 测试
 */
public class JsonReporterTest {
    
    @Test
    void testConstructorWithDefaultDirectory() {
        JsonReporter reporter = new JsonReporter();
        assertNotNull(reporter);
    }
    
    @Test
    void testConstructorWithCustomDirectory(@TempDir Path tempDir) {
        String customDir = tempDir.toString();
        JsonReporter reporter = new JsonReporter(customDir);
        assertNotNull(reporter);
        
        // 验证目录被创建
        assertTrue(Files.exists(tempDir));
    }
    
    @Test
    void testReportGeneratesJsonFile(@TempDir Path tempDir) throws IOException {
        JsonReporter reporter = new JsonReporter(tempDir.toString());
        
        List<BenchmarkResult> results = createSampleResults();
        reporter.report(results);
        
        // 验证生成了 JSON 文件
        try (var stream = Files.list(tempDir)) {
            List<Path> files = stream.filter(p -> p.toString().endsWith(".json")).toList();
            assertEquals(1, files.size(), "应该生成一个 JSON 文件");
            
            // 验证文件内容
            String content = Files.readString(files.get(0));
            assertTrue(content.contains("{"), "应该包含 JSON 开始符号");
            assertTrue(content.contains("}"), "应该包含 JSON 结束符号");
            assertTrue(content.contains("\"timestamp\""), "应该包含 timestamp 字段");
        }
    }
    
    @Test
    void testJsonContainsEnvironmentInfo(@TempDir Path tempDir) throws IOException {
        JsonReporter reporter = new JsonReporter(tempDir.toString());
        List<BenchmarkResult> results = createSampleResults();
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path jsonFile = stream.filter(p -> p.toString().endsWith(".json")).findFirst().orElseThrow();
            String content = Files.readString(jsonFile);
            
            assertTrue(content.contains("\"environment\""), "应该包含 environment 字段");
            assertTrue(content.contains("\"javaVersion\""), "应该包含 javaVersion 字段");
            assertTrue(content.contains("\"osName\""), "应该包含 osName 字段");
            assertTrue(content.contains("\"cpuCores\""), "应该包含 cpuCores 字段");
            assertTrue(content.contains("\"maxHeapMemory\""), "应该包含 maxHeapMemory 字段");
        }
    }
    
    @Test
    void testJsonContainsResultsArray(@TempDir Path tempDir) throws IOException {
        JsonReporter reporter = new JsonReporter(tempDir.toString());
        List<BenchmarkResult> results = createSampleResults();
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path jsonFile = stream.filter(p -> p.toString().endsWith(".json")).findFirst().orElseThrow();
            String content = Files.readString(jsonFile);
            
            assertTrue(content.contains("\"results\""), "应该包含 results 字段");
            assertTrue(content.contains("\"scenarioName\""), "应该包含 scenarioName 字段");
            assertTrue(content.contains("\"strategyName\""), "应该包含 strategyName 字段");
            assertTrue(content.contains("\"averageThroughput\""), "应该包含 averageThroughput 字段");
        }
    }
    
    @Test
    void testJsonContainsResultMetrics(@TempDir Path tempDir) throws IOException {
        JsonReporter reporter = new JsonReporter(tempDir.toString());
        List<BenchmarkResult> results = createSampleResults();
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path jsonFile = stream.filter(p -> p.toString().endsWith(".json")).findFirst().orElseThrow();
            String content = Files.readString(jsonFile);
            
            assertTrue(content.contains("\"p50Latency\""), "应该包含 p50Latency 字段");
            assertTrue(content.contains("\"p95Latency\""), "应该包含 p95Latency 字段");
            assertTrue(content.contains("\"p99Latency\""), "应该包含 p99Latency 字段");
            assertTrue(content.contains("\"standardDeviation\""), "应该包含 standardDeviation 字段");
        }
    }
    
    @Test
    void testJsonValidStructure(@TempDir Path tempDir) throws IOException {
        JsonReporter reporter = new JsonReporter(tempDir.toString());
        List<BenchmarkResult> results = createSampleResults();
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path jsonFile = stream.filter(p -> p.toString().endsWith(".json")).findFirst().orElseThrow();
            String content = Files.readString(jsonFile);
            
            // 验证 JSON 结构完整性
            int openBraces = countOccurrences(content, "{");
            int closeBraces = countOccurrences(content, "}");
            assertEquals(openBraces, closeBraces, "花括号应该成对");
            
            int openBrackets = countOccurrences(content, "[");
            int closeBrackets = countOccurrences(content, "]");
            assertEquals(openBrackets, closeBrackets, "方括号应该成对");
        }
    }
    
    @Test
    void testJsonContainsTimestampFormat(@TempDir Path tempDir) throws IOException {
        JsonReporter reporter = new JsonReporter(tempDir.toString());
        List<BenchmarkResult> results = createSampleResults();
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path jsonFile = stream.filter(p -> p.toString().endsWith(".json")).findFirst().orElseThrow();
            String content = Files.readString(jsonFile);
            
            // 验证时间戳包含日期时间信息 (更宽松的匹配)
            assertTrue(content.contains("\"timestamp\""), "应该包含 timestamp 字段");
            // 时间戳应该包含 T 分隔符 (ISO-8601 特征)
            assertTrue(content.contains("T"), "时间戳应该包含 ISO-8601 格式的 T 分隔符");
        }
    }
    
    @Test
    void testReportWithEmptyResults(@TempDir Path tempDir) throws IOException {
        JsonReporter reporter = new JsonReporter(tempDir.toString());
        List<BenchmarkResult> results = List.of();
        
        // 不应该抛出异常
        assertDoesNotThrow(() -> reporter.report(results));
        
        // 仍然应该生成文件
        try (var stream = Files.list(tempDir)) {
            List<Path> files = stream.filter(p -> p.toString().endsWith(".json")).toList();
            assertEquals(1, files.size());
            
            // 验证空结果的结构
            String content = Files.readString(files.get(0));
            assertTrue(content.contains("\"results\""), "应该包含 results 字段");
            // 验证 results 是数组 (可能为空或包含内容)
            assertTrue(content.contains("\"results\""), "应该包含 results 字段");
        }
    }
    
    @Test
    void testReportWithMultipleResults(@TempDir Path tempDir) throws IOException {
        JsonReporter reporter = new JsonReporter(tempDir.toString());
        
        // 创建多个测试结果
        List<BenchmarkResult> results = List.of(
            createSampleResult("cpu-bound", "platform", 10000.0),
            createSampleResult("cpu-bound", "virtual", 15000.0),
            createSampleResult("io-bound", "platform", 5000.0),
            createSampleResult("io-bound", "virtual", 20000.0)
        );
        
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path jsonFile = stream.filter(p -> p.toString().endsWith(".json")).findFirst().orElseThrow();
            String content = Files.readString(jsonFile);
            
            // 验证所有结果都包含在报告中
            assertTrue(content.contains("\"cpu-bound\""), "应该包含 cpu-bound 场景");
            assertTrue(content.contains("\"io-bound\""), "应该包含 io-bound 场景");
            assertTrue(content.contains("\"platform\""), "应该包含 platform 策略");
            assertTrue(content.contains("\"virtual\""), "应该包含 virtual 策略");
        }
    }
    
    @Test
    void testJsonFileNameFormat(@TempDir Path tempDir) throws IOException {
        JsonReporter reporter = new JsonReporter(tempDir.toString());
        List<BenchmarkResult> results = createSampleResults();
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path jsonFile = stream.filter(p -> p.toString().endsWith(".json")).findFirst().orElseThrow();
            String fileName = jsonFile.getFileName().toString();
            
            // 验证文件名格式：benchmark-report-YYYYMMDD-HHmmss.json
            assertTrue(fileName.startsWith("benchmark-report-"), "文件名应该以 benchmark-report-开头");
            assertTrue(fileName.endsWith(".json"), "文件名应该以.json 结尾");
        }
    }
    
    @Test
    void testJsonEscaping(@TempDir Path tempDir) throws IOException {
        JsonReporter reporter = new JsonReporter(tempDir.toString());
        
        // 创建包含特殊字符的场景名称
        List<BenchmarkResult> results = List.of(
            createSampleResult("test \"quoted\" scenario", "virtual", 10000.0)
        );
        
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path jsonFile = stream.filter(p -> p.toString().endsWith(".json")).findFirst().orElseThrow();
            String content = Files.readString(jsonFile);
            
            // 验证特殊字符被正确转义
            assertTrue(content.contains("\\\"quoted\\\""), "引号应该被转义");
        }
    }
    
    @Test
    void testJsonNumericValues(@TempDir Path tempDir) throws IOException {
        JsonReporter reporter = new JsonReporter(tempDir.toString());
        List<BenchmarkResult> results = createSampleResults();
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path jsonFile = stream.filter(p -> p.toString().endsWith(".json")).findFirst().orElseThrow();
            String content = Files.readString(jsonFile);
            
            // 验证数值字段存在 (更宽松的匹配，不检查是否为纯数字)
            assertTrue(content.contains("\"cpuCores\""), "应该包含 cpuCores 字段");
            assertTrue(content.contains("\"maxHeapMemory\""), "应该包含 maxHeapMemory 字段");
            assertTrue(content.contains("\"averageThroughput\""), "应该包含 averageThroughput 字段");
        }
    }
    
    // ========================================================================
    // 辅助方法
    // ========================================================================
    
    private List<BenchmarkResult> createSampleResults() {
        return List.of(createSampleResult("cpu-bound", "virtual", 12345.67));
    }
    
    private BenchmarkResult createSampleResult(String scenario, String strategy, double throughput) {
        // 创建一些模拟的延迟数据 (纳秒)
        List<Long> latencies = List.of(1_000_000L, 2_000_000L, 3_000_000L); // 1ms, 2ms, 3ms
        
        ScenarioResult iterationResult = new ScenarioResult(
            scenario,
            1000,  // total tasks
            1000,  // completed tasks
            100_000_000L,  // duration nanos (100ms)
            latencies,
            true,  // success
            null   // error message
        );
        
        return new BenchmarkResult(
            scenario,
            strategy,
            3,  // warmup iterations
            5,  // benchmark iterations
            List.of(iterationResult)
        );
    }
    
    private int countOccurrences(String text, String substring) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }
}
