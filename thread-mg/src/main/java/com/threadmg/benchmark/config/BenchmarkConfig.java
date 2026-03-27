package com.threadmg.benchmark.config;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * 基准测试配置
 * 使用 Builder 模式构建，支持从 YAML 文件加载配置
 */
public class BenchmarkConfig {
    private final int warmupIterations;
    private final int benchmarkIterations;
    private final int taskCount;
    private final long timeoutMillis;
    private final boolean verbose;
    
    private BenchmarkConfig(Builder builder) {
        this.warmupIterations = builder.warmupIterations;
        this.benchmarkIterations = builder.benchmarkIterations;
        this.taskCount = builder.taskCount;
        this.timeoutMillis = builder.timeoutMillis;
        this.verbose = builder.verbose;
    }
    
    // Default configuration
    public static BenchmarkConfig defaultConfig() {
        return new Builder()
            .warmupIterations(3)
            .benchmarkIterations(5)
            .taskCount(1000)
            .timeoutMillis(60000)
            .verbose(true)
            .build();
    }
    
    /**
     * 从 YAML 文件路径加载配置
     * @param path YAML 文件路径
     * @return 配置对象
     * @throws IOException 文件读取失败
     * @throws IllegalArgumentException YAML 格式错误或配置项缺失
     */
    public static BenchmarkConfig fromYaml(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("配置文件不存在：" + path.toAbsolutePath());
        }
        String yamlContent = Files.readString(path);
        return fromYaml(yamlContent);
    }
    
    /**
     * 从 YAML 字符串加载配置
     * @param yamlContent YAML 内容
     * @return 配置对象
     * @throws IllegalArgumentException YAML 格式错误或配置项缺失
     */
    public static BenchmarkConfig fromYaml(String yamlContent) {
        try {
            Yaml yaml = new Yaml();
            @SuppressWarnings("unchecked")
            Map<String, Object> configMap = yaml.load(yamlContent);
            
            if (configMap == null) {
                throw new IllegalArgumentException("YAML 内容为空或格式错误");
            }
            
            // 获取 benchmark 配置段
            @SuppressWarnings("unchecked")
            Map<String, Object> benchmark = 
                (Map<String, Object>) configMap.get("benchmark");
            
            if (benchmark == null) {
                // 如果没有 benchmark 段，尝试直接解析顶层配置
                benchmark = configMap;
            }
            
            Builder builder = new Builder();
            
            // 解析配置项，使用默认值如果未指定
            if (benchmark.containsKey("warmupIterations")) {
                builder.warmupIterations(getInteger(benchmark.get("warmupIterations")));
            }
            if (benchmark.containsKey("benchmarkIterations")) {
                builder.benchmarkIterations(getInteger(benchmark.get("benchmarkIterations")));
            }
            if (benchmark.containsKey("taskCount")) {
                builder.taskCount(getInteger(benchmark.get("taskCount")));
            }
            if (benchmark.containsKey("timeoutMillis")) {
                builder.timeoutMillis(getLong(benchmark.get("timeoutMillis")));
            }
            if (benchmark.containsKey("verbose")) {
                builder.verbose(getBoolean(benchmark.get("verbose")));
            }
            
            return builder.build();
            
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) e;
            }
            throw new IllegalArgumentException("解析 YAML 配置失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 从 classpath 资源加载配置
     * @param resourcePath 资源路径 (如 "/config/benchmark.yaml")
     * @return 配置对象
     * @throws IOException 资源读取失败
     * @throws IllegalArgumentException YAML 格式错误
     */
    public static BenchmarkConfig fromResource(String resourcePath) throws IOException {
        try (InputStream inputStream = BenchmarkConfig.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("资源不存在：" + resourcePath);
            }
            
            // 读取 InputStream 内容为字符串
            String yamlContent = new String(inputStream.readAllBytes());
            return fromYaml(yamlContent);
        }
    }
    
    /**
     * 安全地将 Object 转换为 Integer
     */
    private static int getInteger(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        throw new IllegalArgumentException("配置项必须是数字类型：" + value);
    }
    
    /**
     * 安全地将 Object 转换为 Long
     */
    private static long getLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            return Long.parseLong((String) value);
        }
        throw new IllegalArgumentException("配置项必须是数字类型：" + value);
    }
    
    /**
     * 安全地将 Object 转换为 Boolean
     */
    private static boolean getBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        throw new IllegalArgumentException("配置项必须是布尔类型：" + value);
    }
    
    public int getWarmupIterations() {
        return warmupIterations;
    }
    
    public int getBenchmarkIterations() {
        return benchmarkIterations;
    }
    
    public int getTaskCount() {
        return taskCount;
    }
    
    public long getTimeoutMillis() {
        return timeoutMillis;
    }
    
    public boolean isVerbose() {
        return verbose;
    }
    
    public static class Builder {
        private int warmupIterations = 3;
        private int benchmarkIterations = 5;
        private int taskCount = 1000;
        private long timeoutMillis = 60000;
        private boolean verbose = true;
        
        public Builder warmupIterations(int warmupIterations) {
            this.warmupIterations = warmupIterations;
            return this;
        }
        
        public Builder benchmarkIterations(int benchmarkIterations) {
            this.benchmarkIterations = benchmarkIterations;
            return this;
        }
        
        public Builder taskCount(int taskCount) {
            this.taskCount = taskCount;
            return this;
        }
        
        public Builder timeoutMillis(long timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
            return this;
        }
        
        public Builder verbose(boolean verbose) {
            this.verbose = verbose;
            return this;
        }
        
        public BenchmarkConfig build() {
            return new BenchmarkConfig(this);
        }
    }
}