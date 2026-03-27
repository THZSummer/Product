package com.threadmg.reporters;

import com.threadmg.benchmark.core.BenchmarkResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON 报告器
 * 生成机器可读的 JSON 格式性能报告
 */
public class JsonReporter {
    private final String outputDirectory;
    
    public JsonReporter() {
        this("results/reports");
    }
    
    public JsonReporter(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        ensureDirectoryExists();
    }
    
    /**
     * 生成 JSON 报告
     * @param results 基准测试结果列表
     */
    public void report(List<BenchmarkResult> results) {
        String json = buildJson(results);
        writeToFile(json);
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
     * 构建 JSON 内容
     */
    private String buildJson(List<BenchmarkResult> results) {
        Map<String, Object> report = new LinkedHashMap<>();
        
        // 时间戳
        report.put("timestamp", Instant.now().toString());
        
        // 执行环境信息
        report.put("environment", buildEnvironmentInfo());
        
        // 测试结果
        report.put("results", buildResultsArray(results));
        
        // 使用简单的 JSON 序列化（不依赖外部库）
        return toJson(report);
    }
    
    /**
     * 构建环境信息对象
     */
    private Map<String, Object> buildEnvironmentInfo() {
        Map<String, Object> env = new LinkedHashMap<>();
        env.put("javaVersion", System.getProperty("java.version"));
        env.put("osName", System.getProperty("os.name"));
        env.put("osVersion", System.getProperty("os.version"));
        env.put("cpuCores", Runtime.getRuntime().availableProcessors());
        env.put("maxHeapMemory", Runtime.getRuntime().maxMemory() / (1024 * 1024));
        return env;
    }
    
    /**
     * 构建结果数组
     */
    private List<Map<String, Object>> buildResultsArray(List<BenchmarkResult> results) {
        List<Map<String, Object>> resultsArray = new ArrayList<>();
        
        for (BenchmarkResult r : results) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("scenarioName", r.getScenarioName());
            result.put("strategyName", r.getStrategyName());
            result.put("warmupIterations", r.getWarmupIterations());
            result.put("benchmarkIterations", r.getBenchmarkIterations());
            result.put("averageThroughput", r.getAverageThroughput());
            result.put("p50Latency", r.getP50Latency());
            result.put("p95Latency", r.getP95Latency());
            result.put("p99Latency", r.getP99Latency());
            result.put("standardDeviation", r.getStandardDeviation());
            result.put("timestamp", r.getTimestamp().toString());
            resultsArray.add(result);
        }
        
        return resultsArray;
    }
    
    /**
     * 简单的 JSON 序列化（不使用外部库）
     */
    private String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        if (obj instanceof String) {
            return "\"" + escapeJson((String) obj) + "\"";
        }
        
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        
        if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) {
                    sb.append(",");
                }
                sb.append("\"").append(escapeJson(entry.getKey().toString())).append("\":");
                sb.append(toJson(entry.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
        
        if (obj instanceof List) {
            @SuppressWarnings("unchecked")
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : list) {
                if (!first) {
                    sb.append(",");
                }
                sb.append(toJson(item));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }
        
        return obj.toString();
    }
    
    /**
     * 转义 JSON 字符串中的特殊字符
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
    
    /**
     * 写入 JSON 文件
     */
    private void writeToFile(String json) {
        try {
            String fileName = "benchmark-report-" + 
                             LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + 
                             ".json";
            Path outputPath = Paths.get(outputDirectory, fileName);
            
            // 格式化的 JSON 输出（带缩进）
            String formattedJson = formatJson(json);
            Files.writeString(outputPath, formattedJson);
            
            System.out.println("JSON report generated: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to generate JSON report: " + e.getMessage());
        }
    }
    
    /**
     * 简单格式化 JSON（添加缩进和换行）
     */
    private String formatJson(String json) {
        // 简单的格式化：在 { [ 后添加换行和缩进，在 } ] 前添加换行
        StringBuilder formatted = new StringBuilder();
        int indent = 0;
        boolean inString = false;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            
            if (!inString) {
                if (c == '{' || c == '[') {
                    formatted.append(c).append('\n');
                    indent++;
                    for (int j = 0; j < indent; j++) {
                        formatted.append("  ");
                    }
                } else if (c == '}' || c == ']') {
                    formatted.append('\n');
                    indent--;
                    for (int j = 0; j < indent; j++) {
                        formatted.append("  ");
                    }
                    formatted.append(c);
                } else if (c == ',') {
                    formatted.append(c).append('\n');
                    for (int j = 0; j < indent; j++) {
                        formatted.append("  ");
                    }
                } else if (c == ':') {
                    formatted.append(c).append(' ');
                } else {
                    formatted.append(c);
                }
            } else {
                formatted.append(c);
            }
        }
        
        return formatted.toString();
    }
}
