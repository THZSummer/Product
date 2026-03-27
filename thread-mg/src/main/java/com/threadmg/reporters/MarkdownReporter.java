package com.threadmg.reporters;

import com.threadmg.benchmark.core.BenchmarkResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Markdown报告器
 * 生成Markdown格式的性能对比报告
 */
public class MarkdownReporter {
    private final String outputDirectory;
    
    public MarkdownReporter() {
        this("results/reports");
    }
    
    public MarkdownReporter(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        // Create output directory if it doesn't exist
        Path outputPath = Paths.get(outputDirectory);
        if (!Files.exists(outputPath)) {
            try {
                Files.createDirectories(outputPath);
            } catch (IOException e) {
                System.err.println("Failed to create output directory: " + e.getMessage());
            }
        }
    }
    
    public void report(List<BenchmarkResult> results) {
        StringBuilder md = new StringBuilder();
        md.append("# 基准测试报告\n\n");
        md.append("生成时间：").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
        
        // 执行环境信息
        md.append("## 执行环境\n\n");
        md.append("| 属性 | 值 |\n");
        md.append("|------|-----|\n");
        md.append("| Java版本 | ").append(System.getProperty("java.version")).append(" |\n");
        md.append("| 操作系统 | ").append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.version")).append(" |\n");
        md.append("| CPU核数 | ").append(Runtime.getRuntime().availableProcessors()).append(" |\n");
        md.append("| 最大堆内存 | ").append(Runtime.getRuntime().maxMemory() / (1024 * 1024)).append(" MB |\n\n");
        
        // 性能对比表格
        md.append("## 性能对比\n\n");
        md.append("| 场景 | 策略 | 吞吐量 (tasks/s) | P50 (ms) | P95 (ms) | P99 (ms) | 标准差 |\n");
        md.append("|------|------|-----------------|----------|----------|----------|--------|\n");
        
        for (BenchmarkResult r : results) {
            md.append(String.format("| %s | %s | %.2f | %.2f | %.2f | %.2f | %.2f |\n",
                r.getScenarioName(), r.getStrategyName(),
                r.getAverageThroughput(), r.getP50Latency(),
                r.getP95Latency(), r.getP99Latency(), r.getStandardDeviation()));
        }
        
        md.append("\n## 结论\n\n");
        md.append("- 性能优势明显的列为优\n");
        md.append("- 吞吐量越高越好\n");
        md.append("- 延迟越低越好\n\n");
        
        // 写入文件
        try {
            String fileName = "benchmark-report-" + 
                             LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + 
                             ".md";
            Path outputPath = Paths.get(outputDirectory, fileName);
            Files.writeString(outputPath, md.toString());
            
            System.out.println("Markdown report generated: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to generate markdown report: " + e.getMessage());
        }
    }
}