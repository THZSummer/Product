package com.threadmg.benchmark.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BenchmarkConfig 测试
 */
public class BenchmarkConfigTest {
    
    @Test
    void testDefaultConfig() {
        BenchmarkConfig config = BenchmarkConfig.defaultConfig();
        
        // Check default values according to spec.md/plan.md
        assertEquals(3, config.getWarmupIterations());
        assertEquals(5, config.getBenchmarkIterations());
        assertEquals(1000, config.getTaskCount());
        assertEquals(60000, config.getTimeoutMillis());
        assertTrue(config.isVerbose());
    }
    
    @Test
    void testBuilderPattern() {
        BenchmarkConfig config = new BenchmarkConfig.Builder()
            .warmupIterations(5)
            .benchmarkIterations(10)
            .taskCount(2000)
            .timeoutMillis(120000)
            .verbose(false)
            .build();
        
        assertEquals(5, config.getWarmupIterations());
        assertEquals(10, config.getBenchmarkIterations());
        assertEquals(2000, config.getTaskCount());
        assertEquals(120000, config.getTimeoutMillis());
        assertFalse(config.isVerbose());
    }
    
    @Test
    void testCustomConfigWithBuilder() {
        BenchmarkConfig customConfig = new BenchmarkConfig.Builder()
            .warmupIterations(1)
            .benchmarkIterations(1)
            .taskCount(100)
            .build();  // Not specifying verbose should use default (true)
        
        assertEquals(1, customConfig.getWarmupIterations());
        assertEquals(1, customConfig.getBenchmarkIterations());
        assertEquals(100, customConfig.getTaskCount());
        // Assuming defaults for unspecified fields
        assertEquals(60000, customConfig.getTimeoutMillis()); // Default
        assertTrue(customConfig.isVerbose());  // Default
    }
    
    @Test
    void testBuilderMethodReturnsBuilder() {
        BenchmarkConfig.Builder builder = new BenchmarkConfig.Builder();
        
        assertSame(builder, builder.warmupIterations(1));
        assertSame(builder, builder.benchmarkIterations(1));
        assertSame(builder, builder.taskCount(100));
        assertSame(builder, builder.timeoutMillis(30000));
        assertSame(builder, builder.verbose(false));
    }
    
    @Test
    void testConfigurationImmutability() {
        BenchmarkConfig config = new BenchmarkConfig.Builder()
            .warmupIterations(2)
            .benchmarkIterations(3)
            .build();
        
        // Values should remain unchanged after construction
        assertEquals(2, config.getWarmupIterations());
        assertEquals(3, config.getBenchmarkIterations());
        
        // Any change would require creating new object with builder
        BenchmarkConfig modifiedConfig = new BenchmarkConfig.Builder()
            .warmupIterations(5)
            .benchmarkIterations(7)
            .taskCount(config.getTaskCount()) // Reuse original value
            .timeoutMillis(config.getTimeoutMillis())
            .verbose(config.isVerbose())
            .build();
        
        assertNotEquals(config.getWarmupIterations(), modifiedConfig.getWarmupIterations());
        assertNotEquals(config.getBenchmarkIterations(), modifiedConfig.getBenchmarkIterations());
    }
    
    // ========================================================================
    // YAML 加载测试
    // ========================================================================
    
    @Test
    void testFromYamlString() {
        String yamlContent = """
            benchmark:
              warmupIterations: 5
              benchmarkIterations: 10
              taskCount: 2000
              timeoutMillis: 120000
              verbose: false
            """;
        
        BenchmarkConfig config = BenchmarkConfig.fromYaml(yamlContent);
        
        assertEquals(5, config.getWarmupIterations());
        assertEquals(10, config.getBenchmarkIterations());
        assertEquals(2000, config.getTaskCount());
        assertEquals(120000, config.getTimeoutMillis());
        assertFalse(config.isVerbose());
    }
    
    @Test
    void testFromYamlStringWithPartialConfig() {
        // 只指定部分配置，其他使用默认值
        String yamlContent = """
            benchmark:
              warmupIterations: 7
              taskCount: 500
            """;
        
        BenchmarkConfig config = BenchmarkConfig.fromYaml(yamlContent);
        
        assertEquals(7, config.getWarmupIterations());
        assertEquals(5, config.getBenchmarkIterations()); // 默认值
        assertEquals(500, config.getTaskCount());
        assertEquals(60000, config.getTimeoutMillis()); // 默认值
        assertTrue(config.isVerbose()); // 默认值
    }
    
    @Test
    void testFromYamlStringWithMinimalConfig() {
        // 空配置，全部使用默认值
        String yamlContent = """
            benchmark:
            """;
        
        BenchmarkConfig config = BenchmarkConfig.fromYaml(yamlContent);
        
        assertEquals(3, config.getWarmupIterations());
        assertEquals(5, config.getBenchmarkIterations());
        assertEquals(1000, config.getTaskCount());
        assertEquals(60000, config.getTimeoutMillis());
        assertTrue(config.isVerbose());
    }
    
    @Test
    void testFromYamlStringWithFullFileFormat() {
        // 完整的配置文件格式
        String yamlContent = """
            benchmark:
              warmupIterations: 3
              benchmarkIterations: 5
              taskCount: 1000
              timeoutMillis: 60000
              verbose: true
            
            scenarios:
              - cpu-bound
              - io-bound
            
            strategies:
              - platform
              - virtual
            
            reporters:
              - console
              - markdown
            """;
        
        BenchmarkConfig config = BenchmarkConfig.fromYaml(yamlContent);
        
        assertEquals(3, config.getWarmupIterations());
        assertEquals(5, config.getBenchmarkIterations());
        assertEquals(1000, config.getTaskCount());
        assertEquals(60000, config.getTimeoutMillis());
        assertTrue(config.isVerbose());
    }
    
    @Test
    void testFromYamlFile(@TempDir Path tempDir) throws IOException {
        String yamlContent = """
            benchmark:
              warmupIterations: 4
              benchmarkIterations: 8
              taskCount: 1500
              timeoutMillis: 90000
              verbose: true
            """;
        
        Path configFile = tempDir.resolve("test-config.yaml");
        Files.writeString(configFile, yamlContent);
        
        BenchmarkConfig config = BenchmarkConfig.fromYaml(configFile);
        
        assertEquals(4, config.getWarmupIterations());
        assertEquals(8, config.getBenchmarkIterations());
        assertEquals(1500, config.getTaskCount());
        assertEquals(90000, config.getTimeoutMillis());
        assertTrue(config.isVerbose());
    }
    
    @Test
    void testFromYamlFileNotFound() {
        Path nonExistentFile = Path.of("/non/existent/path/config.yaml");
        
        assertThrows(IOException.class, () -> {
            BenchmarkConfig.fromYaml(nonExistentFile);
        });
    }
    
    @Test
    void testFromYamlInvalidFormat() {
        String invalidYaml = """
            benchmark:
              warmupIterations: [invalid
              broken yaml
            """;
        
        assertThrows(IllegalArgumentException.class, () -> {
            BenchmarkConfig.fromYaml(invalidYaml);
        });
    }
    
    @Test
    void testFromYamlEmptyString() {
        String emptyYaml = "";
        
        // 空字符串应该抛出异常或使用默认值
        assertThrows(IllegalArgumentException.class, () -> {
            BenchmarkConfig.fromYaml(emptyYaml);
        });
    }
    
    @Test
    void testFromYamlWithDirectBenchmarkSection() {
        // 测试没有顶层 benchmark 段的情况
        String yamlContent = """
            warmupIterations: 6
            benchmarkIterations: 12
            taskCount: 3000
            """;
        
        BenchmarkConfig config = BenchmarkConfig.fromYaml(yamlContent);
        
        assertEquals(6, config.getWarmupIterations());
        assertEquals(12, config.getBenchmarkIterations());
        assertEquals(3000, config.getTaskCount());
    }
    
    @Test
    void testFromResource() throws IOException {
        // 测试从 classpath 资源加载
        // 注意：这需要资源文件存在于 test resources 中
        // 这里测试资源不存在的情况
        assertThrows(IOException.class, () -> {
            BenchmarkConfig.fromResource("/non-existent-config.yaml");
        });
    }
    
    @Test
    void testFromYamlWithStringValues() {
        // 测试配置值为字符串类型的情况
        String yamlContent = """
            benchmark:
              warmupIterations: "5"
              benchmarkIterations: "10"
              taskCount: "2000"
              timeoutMillis: "120000"
              verbose: "true"
            """;
        
        BenchmarkConfig config = BenchmarkConfig.fromYaml(yamlContent);
        
        assertEquals(5, config.getWarmupIterations());
        assertEquals(10, config.getBenchmarkIterations());
        assertEquals(2000, config.getTaskCount());
        assertEquals(120000, config.getTimeoutMillis());
        assertTrue(config.isVerbose());
    }
    
    @Test
    void testBuilderCompatibility() {
        // 确保 Builder 模式仍然正常工作
        BenchmarkConfig builderConfig = new BenchmarkConfig.Builder()
            .warmupIterations(5)
            .benchmarkIterations(10)
            .build();
        
        String yamlContent = """
            benchmark:
              warmupIterations: 5
              benchmarkIterations: 10
            """;
        BenchmarkConfig yamlConfig = BenchmarkConfig.fromYaml(yamlContent);
        
        // 两种方式应该产生相同的结果
        assertEquals(builderConfig.getWarmupIterations(), yamlConfig.getWarmupIterations());
        assertEquals(builderConfig.getBenchmarkIterations(), yamlConfig.getBenchmarkIterations());
        assertEquals(builderConfig.getTaskCount(), yamlConfig.getTaskCount());
        assertEquals(builderConfig.getTimeoutMillis(), yamlConfig.getTimeoutMillis());
        assertEquals(builderConfig.isVerbose(), yamlConfig.isVerbose());
    }
}