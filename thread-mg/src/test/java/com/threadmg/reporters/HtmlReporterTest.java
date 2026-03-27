package com.threadmg.reporters;

import com.threadmg.benchmark.core.BenchmarkResult;
import com.threadmg.benchmark.core.ScenarioResult;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HtmlReporter 测试
 */
public class HtmlReporterTest {
    
    @Test
    void testConstructorWithDefaultDirectory() {
        HtmlReporter reporter = new HtmlReporter();
        assertNotNull(reporter);
    }
    
    @Test
    void testConstructorWithCustomDirectory(@TempDir Path tempDir) {
        String customDir = tempDir.toString();
        HtmlReporter reporter = new HtmlReporter(customDir);
        assertNotNull(reporter);
        
        // 验证目录被创建
        assertTrue(Files.exists(tempDir));
    }
    
    @Test
    void testReportGeneratesHtmlFile(@TempDir Path tempDir) throws IOException {
        HtmlReporter reporter = new HtmlReporter(tempDir.toString());
        
        List<BenchmarkResult> results = createSampleResults();
        reporter.report(results);
        
        // 验证生成了 HTML 文件
        try (var stream = Files.list(tempDir)) {
            List<Path> files = stream.filter(p -> p.toString().endsWith(".html")).toList();
            assertEquals(1, files.size(), "应该生成一个 HTML 文件");
            
            // 验证文件内容
            String content = Files.readString(files.get(0));
            assertTrue(content.contains("<!DOCTYPE html>"), "应该包含 HTML 声明");
            assertTrue(content.contains("<html"), "应该包含 html 标签");
            assertTrue(content.contains("</html>"), "应该包含结束 html 标签");
            assertTrue(content.contains("基准测试报告"), "应该包含报告标题");
        }
    }
    
    @Test
    void testHtmlContainsEnvironmentInfo(@TempDir Path tempDir) throws IOException {
        HtmlReporter reporter = new HtmlReporter(tempDir.toString());
        List<BenchmarkResult> results = createSampleResults();
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path htmlFile = stream.filter(p -> p.toString().endsWith(".html")).findFirst().orElseThrow();
            String content = Files.readString(htmlFile);
            
            assertTrue(content.contains("执行环境"), "应该包含执行环境部分");
            assertTrue(content.contains("Java 版本"), "应该包含 Java 版本信息");
            assertTrue(content.contains("CPU 核数"), "应该包含 CPU 核数信息");
        }
    }
    
    @Test
    void testHtmlContainsPerformanceTable(@TempDir Path tempDir) throws IOException {
        HtmlReporter reporter = new HtmlReporter(tempDir.toString());
        List<BenchmarkResult> results = createSampleResults();
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path htmlFile = stream.filter(p -> p.toString().endsWith(".html")).findFirst().orElseThrow();
            String content = Files.readString(htmlFile);
            
            assertTrue(content.contains("性能对比"), "应该包含性能对比部分");
            assertTrue(content.contains("<table>"), "应该包含表格");
            assertTrue(content.contains("吞吐量"), "应该包含吞吐量列");
            assertTrue(content.contains("P50"), "应该包含 P50 延迟列");
            assertTrue(content.contains("P95"), "应该包含 P95 延迟列");
            assertTrue(content.contains("P99"), "应该包含 P99 延迟列");
        }
    }
    
    @Test
    void testHtmlContainsCssStyles(@TempDir Path tempDir) throws IOException {
        HtmlReporter reporter = new HtmlReporter(tempDir.toString());
        List<BenchmarkResult> results = createSampleResults();
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path htmlFile = stream.filter(p -> p.toString().endsWith(".html")).findFirst().orElseThrow();
            String content = Files.readString(htmlFile);
            
            assertTrue(content.contains("<style>"), "应该包含 style 标签");
            assertTrue(content.contains("body"), "应该包含 body 样式");
            assertTrue(content.contains("table"), "应该包含 table 样式");
            assertTrue(content.contains(".header"), "应该包含 header 样式");
        }
    }
    
    @Test
    void testHtmlContainsResultData(@TempDir Path tempDir) throws IOException {
        HtmlReporter reporter = new HtmlReporter(tempDir.toString());
        List<BenchmarkResult> results = createSampleResults();
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path htmlFile = stream.filter(p -> p.toString().endsWith(".html")).findFirst().orElseThrow();
            String content = Files.readString(htmlFile);
            
            // 验证包含测试结果数据
            assertTrue(content.contains("cpu-bound"), "应该包含场景名称");
            assertTrue(content.contains("virtual"), "应该包含策略名称");
        }
    }
    
    @Test
    void testHtmlValidStructure(@TempDir Path tempDir) throws IOException {
        HtmlReporter reporter = new HtmlReporter(tempDir.toString());
        List<BenchmarkResult> results = createSampleResults();
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path htmlFile = stream.filter(p -> p.toString().endsWith(".html")).findFirst().orElseThrow();
            String content = Files.readString(htmlFile);
            
            // 验证 HTML 结构完整性
            int openHtml = countOccurrences(content, "<html");
            int closeHtml = countOccurrences(content, "</html>");
            assertEquals(openHtml, closeHtml, "html 标签应该成对");
            
            int openHead = countOccurrences(content, "<head>");
            int closeHead = countOccurrences(content, "</head>");
            assertEquals(openHead, closeHead, "head 标签应该成对");
            
            int openBody = countOccurrences(content, "<body>");
            int closeBody = countOccurrences(content, "</body>");
            assertEquals(openBody, closeBody, "body 标签应该成对");
            
            int openTable = countOccurrences(content, "<table>");
            int closeTable = countOccurrences(content, "</table>");
            assertEquals(openTable, closeTable, "table 标签应该成对");
        }
    }
    
    @Test
    void testReportWithEmptyResults(@TempDir Path tempDir) throws IOException {
        HtmlReporter reporter = new HtmlReporter(tempDir.toString());
        List<BenchmarkResult> results = List.of();
        
        // 不应该抛出异常
        assertDoesNotThrow(() -> reporter.report(results));
        
        // 仍然应该生成文件
        try (var stream = Files.list(tempDir)) {
            List<Path> files = stream.filter(p -> p.toString().endsWith(".html")).toList();
            assertEquals(1, files.size());
        }
    }
    
    @Test
    void testReportWithMultipleResults(@TempDir Path tempDir) throws IOException {
        HtmlReporter reporter = new HtmlReporter(tempDir.toString());
        
        // 创建多个测试结果
        List<BenchmarkResult> results = List.of(
            createSampleResult("cpu-bound", "platform", 10000.0),
            createSampleResult("cpu-bound", "virtual", 15000.0),
            createSampleResult("io-bound", "platform", 5000.0),
            createSampleResult("io-bound", "virtual", 20000.0)
        );
        
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path htmlFile = stream.filter(p -> p.toString().endsWith(".html")).findFirst().orElseThrow();
            String content = Files.readString(htmlFile);
            
            // 验证所有结果都包含在报告中
            assertTrue(content.contains("platform"), "应该包含 platform 策略");
            assertTrue(content.contains("virtual"), "应该包含 virtual 策略");
            assertTrue(content.contains("cpu-bound"), "应该包含 cpu-bound 场景");
            assertTrue(content.contains("io-bound"), "应该包含 io-bound 场景");
        }
    }
    
    @Test
    void testHtmlFileNameFormat(@TempDir Path tempDir) throws IOException {
        HtmlReporter reporter = new HtmlReporter(tempDir.toString());
        List<BenchmarkResult> results = createSampleResults();
        reporter.report(results);
        
        try (var stream = Files.list(tempDir)) {
            Path htmlFile = stream.filter(p -> p.toString().endsWith(".html")).findFirst().orElseThrow();
            String fileName = htmlFile.getFileName().toString();
            
            // 验证文件名格式：benchmark-report-YYYYMMDD-HHmmss.html
            assertTrue(fileName.startsWith("benchmark-report-"), "文件名应该以 benchmark-report-开头");
            assertTrue(fileName.endsWith(".html"), "文件名应该以.html 结尾");
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
