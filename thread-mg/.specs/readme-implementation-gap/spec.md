# 功能规范：补全 README 与实现差异

**Feature ID**: readme-implementation-gap  
**创建日期**: 2026-03-27  
**优先级**: P0/P1/P2  
**状态**: 待开发  

---

## 📋 背景

README.md 文档中描述了完整的项目结构和功能，但实际代码实现存在以下差距：

1. **缺失脚本文件** - README 中描述的 `scripts/run-benchmark.sh` 等脚本不存在
2. **缺失配置文件** - README 中描述的 `config/benchmark/benchmark.yaml` 不存在
3. **配置加载功能缺失** - `BenchmarkConfig` 不支持从 YAML 文件加载配置
4. **报告器不完整** - 缺少 `HtmlReporter.java` 和 `JsonReporter.java`

本功能旨在系统性补全这些差距，确保文档与实现一致。

---

## 🎯 目标

### 主要目标
1. 创建完整的脚本工具集，支持一键运行基准测试
2. 创建 YAML 配置文件，支持灵活的测试参数配置
3. 增强 `BenchmarkConfig` 支持从 YAML 加载配置
4. 完成报告器模块，支持多种输出格式

### 验收标准

#### P0 高优先级

**任务 1: 创建 `scripts/run-benchmark.sh` 脚本**
- [ ] 脚本可执行 (`chmod +x`)
- [ ] 支持命令行参数：
  - `--scenario <name>` - 指定测试场景
  - `--strategy <name>` - 指定线程策略
  - `--config <path>` - 指定配置文件
  - `--output <format>` - 输出格式 (console/markdown/html/json)
  - `--help` - 显示帮助信息
- [ ] 默认使用 `config/benchmark/benchmark.yaml` 配置
- [ ] 自动创建必要的输出目录 (`results/raw`, `results/reports`)
- [ ] 运行 Maven 测试并捕获结果
- [ ] 错误处理和退出码规范 (0=成功，1=失败)

**任务 2: 创建 `config/benchmark/benchmark.yaml` 配置文件**
- [ ] 包含所有必要的配置项：
  ```yaml
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
    - html
    - json
  
  output:
    directory: results
    raw: raw
    reports: reports
  ```
- [ ] 配置项与 `BenchmarkConfig` 字段对应
- [ ] 包含配置说明注释

#### P1 中优先级

**任务 3: 修改 `BenchmarkConfig` 支持从 YAML 加载配置**
- [ ] 添加静态方法 `fromYaml(Path path)` 从文件加载
- [ ] 添加静态方法 `fromYaml(String yamlContent)` 从字符串加载
- [ ] 添加静态方法 `fromResource(String resourcePath)` 从 classpath 加载
- [ ] 配置项映射：
  - `benchmark.warmupIterations` → `warmupIterations`
  - `benchmark.benchmarkIterations` → `benchmarkIterations`
  - `benchmark.taskCount` → `taskCount`
  - `benchmark.timeoutMillis` → `timeoutMillis`
  - `benchmark.verbose` → `verbose`
- [ ] 依赖管理：添加 snakeyaml 或 equivalent YAML 解析库
- [ ] 异常处理：配置文件不存在/格式错误时抛出清晰的异常
- [ ] 向后兼容：保留现有的 Builder 模式
- [ ] 单元测试覆盖所有加载方法

#### P2 低优先级

**任务 4: 创建 `HtmlReporter.java`**
- [ ] 实现报告器接口 (与 `MarkdownReporter` 一致的 API)
- [ ] 生成完整的 HTML 报告，包含：
  - 报告标题和生成时间
  - 执行环境信息表格
  - 性能对比表格 (带样式)
  - 简单的 CSS 样式 (内联或外部)
  - 可选：简单的图表展示 (使用 SVG 或 Canvas)
- [ ] 输出目录：`results/reports/`
- [ ] 文件名格式：`benchmark-report-YYYYMMDD-HHmmss.html`
- [ ] 单元测试验证 HTML 结构

**任务 5: 创建 `JsonReporter.java`**
- [ ] 实现报告器接口 (与 `MarkdownReporter` 一致的 API)
- [ ] 生成 JSON 格式报告，结构：
  ```json
  {
    "timestamp": "2026-03-27T12:00:00Z",
    "environment": {
      "javaVersion": "21.0.1",
      "osName": "Linux",
      "osVersion": "5.15.0",
      "cpuCores": 8,
      "maxHeapMemory": 4096
    },
    "results": [
      {
        "scenarioName": "cpu-bound",
        "strategyName": "virtual",
        "warmupIterations": 3,
        "benchmarkIterations": 5,
        "averageThroughput": 12345.67,
        "p50Latency": 1.23,
        "p95Latency": 2.34,
        "p99Latency": 3.45,
        "standardDeviation": 123.45
      }
    ]
  }
  ```
- [ ] 输出目录：`results/reports/`
- [ ] 文件名格式：`benchmark-report-YYYYMMDD-HHmmss.json`
- [ ] 使用标准 JSON 库 (java.json 或 Jackson)
- [ ] 单元测试验证 JSON 结构

---

## 📊 约束条件

### 技术约束
- Java 版本：21+
- 构建工具：Maven 3.9+
- YAML 解析：snakeyaml 2.0+ (需添加到 pom.xml)
- JSON 处理：优先使用 Java 标准库，必要时使用 Jackson

### 代码规范
- 遵循现有代码风格
- 所有公共方法必须有 JavaDoc
- 单元测试覆盖率 ≥ 80%
- 通过 CheckStyle 检查

### 文档要求
- 更新 README 中的使用示例
- 配置文件的每个字段必须有注释说明
- 脚本必须有 `--help` 输出

---

## 🔗 依赖关系

```
任务依赖图:

┌─────────────────┐     ┌─────────────────┐
│  任务 1: 脚本   │     │  任务 2: 配置   │
│  run-benchmark  │     │  benchmark.yaml │
└────────┬────────┘     └────────┬────────┘
         │                       │
         │    ┌──────────────────┘
         │    │
         ▼    ▼
┌─────────────────────────┐
│   任务 3: BenchmarkConfig│
│   YAML 加载支持          │
└─────────────────────────┘
         │
         │    ┌─────────────────┐
         │    │  任务 4: HTML   │
         │    │  HtmlReporter   │
         ▼    ▼
┌─────────────────────────┐
│   任务 5: JSON Reporter │
│   JsonReporter          │
└─────────────────────────┘

注：任务 1 和 2 可以并行开发
    任务 3 依赖任务 2 的配置结构
    任务 4 和 5 可以并行开发
```

---

## 🧪 测试计划

### 单元测试
| 类 | 测试方法 | 覆盖场景 |
|---|---------|---------|
| BenchmarkConfig | testFromYamlFile | 从文件加载配置 |
| BenchmarkConfig | testFromYamlString | 从字符串加载配置 |
| BenchmarkConfig | testFromResource | 从 classpath 加载配置 |
| BenchmarkConfig | testInvalidYaml | 无效 YAML 处理 |
| BenchmarkConfig | testMissingFile | 文件不存在处理 |
| HtmlReporter | testGenerateReport | HTML 结构验证 |
| JsonReporter | testGenerateReport | JSON 结构验证 |

### 集成测试
| 测试 | 描述 |
|-----|------|
| end-to-end-benchmark | 完整流程测试：配置→运行→报告 |
| script-execution | 脚本执行和参数解析 |

### 手动验证
- [ ] 运行 `./scripts/run-benchmark.sh --help` 查看帮助
- [ ] 运行 `./scripts/run-benchmark.sh` 使用默认配置
- [ ] 验证生成的 HTML 报告可在浏览器打开
- [ ] 验证生成的 JSON 可被标准 JSON 工具解析

---

## 📁 文件变更清单

### 新增文件
```
scripts/
  └── run-benchmark.sh

config/
  └── benchmark/
      └── benchmark.yaml

src/main/java/com/threadmg/reporters/
  ├── HtmlReporter.java
  └── JsonReporter.java
```

### 修改文件
```
src/main/java/com/threadmg/benchmark/config/
  └── BenchmarkConfig.java  (添加 YAML 加载方法)

pom.xml  (添加 snakeyaml 依赖)
```

---

## ✅ 完成定义 (DoD)

- [ ] 所有 P0 任务完成并通过测试
- [ ] 所有 P1 任务完成并通过测试
- [ ] 所有 P2 任务完成并通过测试
- [ ] 单元测试覆盖率 ≥ 80%
- [ ] 代码通过 CheckStyle 检查
- [ ] README 使用示例已更新
- [ ] 所有脚本可执行并经过手动验证
- [ ] 生成的报告格式正确

---

*规范版本：1.0*  
*最后更新：2026-03-27*
