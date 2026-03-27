# 技术规划：补全 README 与实现差异

**Feature ID**: readme-implementation-gap  
**关联规范**: [.specs/readme-implementation-gap/spec.md](spec.md)  
**创建日期**: 2026-03-27  

---

## 🏗️ 架构设计

### 整体架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                        用户入口层                                │
│                    scripts/run-benchmark.sh                     │
│              (参数解析 → 执行 Maven → 生成报告)                   │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                        配置管理层                                │
│              config/benchmark/benchmark.yaml                    │
│                         ▼                                        │
│         BenchmarkConfig.fromYaml() 加载解析                       │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                        基准测试核心层                             │
│              BenchmarkRunner 执行测试场景                         │
│              Scenario + ThreadStrategy 组合                       │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                        报告生成层                                │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌───────────┐ │
│  │ConsoleReporter│ │MarkdownReporter│ │HtmlReporter │ │JsonReporter│ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └───────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📦 模块设计

### 1. 脚本模块 (`scripts/run-benchmark.sh`)

#### 功能设计
```bash
#!/bin/bash
# 参数解析
# - 使用 getopt 解析长参数
# - 提供默认值
# - 参数验证

# 目录准备
# - 创建 results/raw 目录
# - 创建 results/reports 目录

# 配置加载
# - 检查 config/benchmark/benchmark.yaml 是否存在
# - 支持 --config 覆盖

# Maven 执行
# - 运行 mvn test -Dtest=BenchmarkE2ETest
# - 传递配置参数

# 报告生成
# - 调用相应的 Reporter 类
# - 输出到 results/reports/

# 错误处理
# - 捕获 Maven 执行结果
# - 适当的退出码
```

#### 参数定义
| 参数 | 短选项 | 默认值 | 说明 |
|-----|-------|-------|------|
| `--scenario` | `-s` | 全部 | 测试场景名称 |
| `--strategy` | `-t` | 全部 | 线程策略名称 |
| `--config` | `-c` | config/benchmark/benchmark.yaml | 配置文件路径 |
| `--output` | `-o` | console,markdown | 输出格式 (逗号分隔) |
| `--help` | `-h` | - | 显示帮助 |

### 2. 配置文件模块 (`config/benchmark/benchmark.yaml`)

#### YAML 结构设计
```yaml
# 基准测试核心配置
benchmark:
  warmupIterations: 3        # 预热轮次
  benchmarkIterations: 5     # 正式测试轮次
  taskCount: 1000            # 每轮任务数
  timeoutMillis: 60000       # 超时时间 (毫秒)
  verbose: true              # 详细输出

# 启用的测试场景
scenarios:
  - cpu-bound                # CPU 密集型
  - io-bound                 # I/O 密集型

# 启用的线程策略
strategies:
  - platform                 # 平台线程
  - virtual                  # 虚拟线程

# 启用的报告器
reporters:
  - console                  # 控制台输出
  - markdown                 # Markdown 报告
  - html                     # HTML 报告
  - json                     # JSON 报告

# 输出配置
output:
  directory: results         # 输出根目录
  raw: raw                   # 原始数据子目录
  reports: reports           # 报告子目录
```

### 3. BenchmarkConfig 增强设计

#### 新增方法签名
```java
public class BenchmarkConfig {
    // 现有方法保持不变...
    
    /**
     * 从 YAML 文件加载配置
     * @param path YAML 文件路径
     * @return 配置对象
     * @throws IOException 文件读取失败
     * @throws IllegalArgumentException YAML 格式错误
     */
    public static BenchmarkConfig fromYaml(Path path) throws IOException;
    
    /**
     * 从 YAML 字符串加载配置
     * @param yamlContent YAML 内容
     * @return 配置对象
     * @throws IllegalArgumentException YAML 格式错误
     */
    public static BenchmarkConfig fromYaml(String yamlContent);
    
    /**
     * 从 classpath 资源加载配置
     * @param resourcePath 资源路径 (如 "/config/benchmark.yaml")
     * @return 配置对象
     * @throws IOException 资源读取失败
     */
    public static BenchmarkConfig fromResource(String resourcePath) throws IOException;
}
```

#### YAML 解析实现策略
```java
// 使用 snakeyaml 库
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public static BenchmarkConfig fromYaml(String yamlContent) {
    Yaml yaml = new Yaml();
    Map<String, Object> configMap = yaml.load(yamlContent);
    
    Map<String, Object> benchmark = 
        (Map<String, Object>) configMap.get("benchmark");
    
    return new Builder()
        .warmupIterations(getInt(benchmark, "warmupIterations", 3))
        .benchmarkIterations(getInt(benchmark, "benchmarkIterations", 5))
        .taskCount(getInt(benchmark, "taskCount", 1000))
        .timeoutMillis(getLong(benchmark, "timeoutMillis", 60000))
        .verbose(getBoolean(benchmark, "verbose", true))
        .build();
}
```

### 4. HtmlReporter 设计

#### HTML 模板结构
```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>基准测试报告</title>
    <style>
        /* 内联 CSS 样式 */
        body { font-family: Arial, sans-serif; margin: 40px; }
        table { border-collapse: collapse; width: 100%; margin: 20px 0; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #4CAF50; color: white; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); 
                  color: white; padding: 30px; border-radius: 10px; }
        .metric-good { color: #28a745; }
        .metric-bad { color: #dc3545; }
    </style>
</head>
<body>
    <div class="header">
        <h1>📊 基准测试报告</h1>
        <p>生成时间：2026-03-27 12:00:00</p>
    </div>
    
    <h2>执行环境</h2>
    <table>...</table>
    
    <h2>性能对比</h2>
    <table>...</table>
    
    <h2>结论</h2>
    <p>...</p>
</body>
</html>
```

#### 类设计
```java
public class HtmlReporter {
    private final String outputDirectory;
    
    public HtmlReporter() {
        this("results/reports");
    }
    
    public HtmlReporter(String outputDirectory) {
        this.outputDirectory = outputDirectory;
        ensureDirectoryExists();
    }
    
    public void report(List<BenchmarkResult> results) {
        String html = buildHtml(results);
        writeToFile(html);
    }
    
    private String buildHtml(List<BenchmarkResult> results) {
        // 构建 HTML 内容
    }
    
    private void writeToFile(String html) {
        // 写入文件
    }
}
```

### 5. JsonReporter 设计

#### JSON 结构设计
```java
public class JsonReporter {
    // 使用 Java 21 的 JSON 处理
    // 或使用 Jackson ObjectMapper
    
    public void report(List<BenchmarkResult> results) {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("timestamp", Instant.now().toString());
        report.put("environment", buildEnvironmentInfo());
        report.put("results", buildResultsArray(results));
        
        String json = toJson(report);
        writeToFile(json);
    }
}
```

---

## 🔧 依赖管理

### Maven 依赖 (pom.xml)
```xml
<dependencies>
    <!-- 现有依赖... -->
    
    <!-- YAML 解析 -->
    <dependency>
        <groupId>org.yaml</groupId>
        <artifactId>snakeyaml</artifactId>
        <version>2.2</version>
    </dependency>
    
    <!-- JSON 处理 (如需要更强大的功能) -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.16.1</version>
    </dependency>
</dependencies>
```

---

## 📝 实现策略

### 优先级顺序
```
迭代 1 (P0):
  1. 创建 config/benchmark/benchmark.yaml
  2. 创建 scripts/run-benchmark.sh (基础版本)
  
迭代 2 (P1):
  3. 修改 BenchmarkConfig 添加 YAML 加载
  4. 更新脚本使用新配置加载
  
迭代 3 (P2):
  5. 创建 HtmlReporter
  6. 创建 JsonReporter
  7. 集成到脚本中
```

### 测试策略
```
单元测试:
  - BenchmarkConfigTest: YAML 加载测试
  - HtmlReporterTest: HTML 生成测试
  - JsonReporterTest: JSON 生成测试

集成测试:
  - BenchmarkE2ETest: 完整流程测试

手动验证:
  - 脚本执行测试
  - 报告格式验证
```

---

## ⚠️ 风险评估

| 风险 | 概率 | 影响 | 缓解措施 |
|-----|------|------|---------|
| snakeyaml 版本兼容 | 低 | 中 | 使用最新稳定版 2.2 |
| HTML 样式兼容 | 低 | 低 | 使用内联 CSS，避免外部依赖 |
| 脚本跨平台 | 中 | 中 | 仅支持 Unix/Linux，Windows 用 WSL |
| JSON 库选择 | 低 | 低 | 优先使用 Java 标准库 |

---

## 📋 验收清单

- [ ] `scripts/run-benchmark.sh` 可执行，`--help` 正常
- [ ] `config/benchmark/benchmark.yaml` 格式正确
- [ ] `BenchmarkConfig.fromYaml()` 方法工作正常
- [ ] `HtmlReporter` 生成有效 HTML
- [ ] `JsonReporter` 生成有效 JSON
- [ ] 所有单元测试通过
- [ ] 集成测试通过
- [ ] 手动验证通过

---

*规划版本：1.0*  
*最后更新：2026-03-27*
