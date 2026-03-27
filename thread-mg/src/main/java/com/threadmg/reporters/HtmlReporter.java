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
 * HTML 报告器
 * 生成带有样式的 HTML 格式性能对比报告
 */
public class HtmlReporter {
    private final String outputDirectory;
    
    public HtmlReporter() {
        this("results/reports");
    }
    
    public HtmlReporter(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        ensureDirectoryExists();
    }
    
    /**
     * 生成 HTML 报告
     * @param results 基准测试结果列表
     */
    public void report(List<BenchmarkResult> results) {
        String html = buildHtml(results);
        writeToFile(html);
    }
    
    /**
     * 确保输出目录存在
     */
    private void ensureDirectoryExists() {
        Path outputPath = Paths.get(outputDirectory);
        if (!Files.exists(outputPath)) {
            try {
                Files.createDirectories(outputPath);
            } catch (IOException e) {
                System.err.println("Failed to create output directory: " + e.getMessage());
            }
        }
    }
    
    /**
     * 构建 HTML 内容
     */
    private String buildHtml(List<BenchmarkResult> results) {
        StringBuilder html = new StringBuilder();
        
        // HTML 头部
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"zh-CN\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>基准测试报告</title>\n");
        html.append("    <style>\n");
        html.append(CSS_STYLES);
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        
        // 报告头部
        html.append("    <div class=\"header\">\n");
        html.append("        <h1>📊 基准测试报告</h1>\n");
        html.append("        <p>生成时间：").append(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ).append("</p>\n");
        html.append("    </div>\n\n");
        
        // 执行环境信息
        html.append("    <div class=\"section\">\n");
        html.append("        <h2>🖥️ 执行环境</h2>\n");
        html.append("        <table>\n");
        html.append("            <tr><th>属性</th><th>值</th></tr>\n");
        html.append("            <tr><td>Java 版本</td><td>").append(System.getProperty("java.version")).append("</td></tr>\n");
        html.append("            <tr><td>操作系统</td><td>").append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.version")).append("</td></tr>\n");
        html.append("            <tr><td>CPU 核数</td><td>").append(Runtime.getRuntime().availableProcessors()).append("</td></tr>\n");
        html.append("            <tr><td>最大堆内存</td><td>").append(Runtime.getRuntime().maxMemory() / (1024 * 1024)).append(" MB</td></tr>\n");
        html.append("        </table>\n");
        html.append("    </div>\n\n");
        
        // 性能对比表格
        html.append("    <div class=\"section\">\n");
        html.append("        <h2>📈 性能对比</h2>\n");
        html.append("        <table>\n");
        html.append("            <thead>\n");
        html.append("                <tr>\n");
        html.append("                    <th>场景</th>\n");
        html.append("                    <th>策略</th>\n");
        html.append("                    <th>吞吐量 (tasks/s)</th>\n");
        html.append("                    <th>P50 (ms)</th>\n");
        html.append("                    <th>P95 (ms)</th>\n");
        html.append("                    <th>P99 (ms)</th>\n");
        html.append("                    <th>标准差</th>\n");
        html.append("                </tr>\n");
        html.append("            </thead>\n");
        html.append("            <tbody>\n");
        
        for (BenchmarkResult r : results) {
            html.append("                <tr>\n");
            html.append("                    <td>").append(r.getScenarioName()).append("</td>\n");
            html.append("                    <td>").append(r.getStrategyName()).append("</td>\n");
            html.append(String.format("                    <td class=\"metric\">%.2f</td>\n", r.getAverageThroughput()));
            html.append(String.format("                    <td class=\"metric\">%.2f</td>\n", r.getP50Latency()));
            html.append(String.format("                    <td class=\"metric\">%.2f</td>\n", r.getP95Latency()));
            html.append(String.format("                    <td class=\"metric\">%.2f</td>\n", r.getP99Latency()));
            html.append(String.format("                    <td class=\"metric\">%.2f</td>\n", r.getStandardDeviation()));
            html.append("                </tr>\n");
        }
        
        html.append("            </tbody>\n");
        html.append("        </table>\n");
        html.append("    </div>\n\n");
        
        // 结论部分
        html.append("    <div class=\"section\">\n");
        html.append("        <h2>💡 结论</h2>\n");
        html.append("        <ul>\n");
        html.append("            <li>性能优势明显的列为优</li>\n");
        html.append("            <li>吞吐量越高越好</li>\n");
        html.append("            <li>延迟越低越好</li>\n");
        html.append("        </ul>\n");
        html.append("    </div>\n\n");
        
        // 页脚
        html.append("    <div class=\"footer\">\n");
        html.append("        <p>Thread Management Benchmark | Generated by HtmlReporter</p>\n");
        html.append("    </div>\n");
        
        html.append("</body>\n");
        html.append("</html>\n");
        
        return html.toString();
    }
    
    /**
     * 写入 HTML 文件
     */
    private void writeToFile(String html) {
        try {
            String fileName = "benchmark-report-" + 
                             LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + 
                             ".html";
            Path outputPath = Paths.get(outputDirectory, fileName);
            Files.writeString(outputPath, html);
            
            System.out.println("HTML report generated: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to generate HTML report: " + e.getMessage());
        }
    }
    
    /**
     * CSS 样式
     */
    private static final String CSS_STYLES = """
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                margin: 40px;
                background-color: #f5f5f5;
                color: #333;
            }
            .header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 30px;
                border-radius: 10px;
                margin-bottom: 30px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            }
            .header h1 {
                margin: 0 0 10px 0;
            }
            .header p {
                margin: 0;
                opacity: 0.9;
            }
            .section {
                background: white;
                padding: 25px;
                border-radius: 10px;
                margin-bottom: 25px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }
            .section h2 {
                margin-top: 0;
                color: #667eea;
                border-bottom: 2px solid #667eea;
                padding-bottom: 10px;
            }
            table {
                border-collapse: collapse;
                width: 100%;
                margin: 20px 0;
            }
            th, td {
                border: 1px solid #ddd;
                padding: 12px;
                text-align: left;
            }
            th {
                background-color: #667eea;
                color: white;
                font-weight: 600;
            }
            tr:nth-child(even) {
                background-color: #f8f9fa;
            }
            tr:hover {
                background-color: #e9ecef;
            }
            .metric {
                font-family: 'Courier New', monospace;
                text-align: right;
            }
            .footer {
                text-align: center;
                padding: 20px;
                color: #666;
                font-size: 0.9em;
            }
            ul {
                line-height: 1.8;
            }
            """;
}
