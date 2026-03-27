# 任务分解：补全 README 与实现差异

**Feature ID**: readme-implementation-gap  
**关联文档**: [spec.md](spec.md) | [plan.md](plan.md)  
**创建日期**: 2026-03-27  

---

## 📋 任务列表

### P0 高优先级任务

#### Task 1.1: 创建目录结构
- **描述**: 创建必要的目录结构
- **工作量**: 5 分钟
- **依赖**: 无
- **验收标准**:
  - `scripts/` 目录存在
  - `config/benchmark/` 目录存在
  - `results/raw/` 目录存在
  - `results/reports/` 目录存在

#### Task 1.2: 创建 benchmark.yaml 配置文件
- **描述**: 创建 YAML 配置文件，包含所有必要配置项
- **工作量**: 15 分钟
- **依赖**: Task 1.1
- **验收标准**:
  - 文件位于 `config/benchmark/benchmark.yaml`
  - 包含 benchmark、scenarios、strategies、reporters、output 配置段
  - 所有配置项有中文注释
  - YAML 语法正确

#### Task 1.3: 创建 run-benchmark.sh 脚本框架
- **描述**: 创建脚本基础框架，包含参数解析和帮助信息
- **工作量**: 30 分钟
- **依赖**: Task 1.1
- **验收标准**:
  - 脚本可执行 (`chmod +x`)
  - `--help` 显示完整帮助信息
  - 参数解析正确
  - 有基本的错误处理

#### Task 1.4: 完善 run-benchmark.sh 功能
- **描述**: 添加 Maven 执行、报告生成等完整功能
- **工作量**: 45 分钟
- **依赖**: Task 1.3
- **验收标准**:
  - 能调用 Maven 执行测试
  - 能创建输出目录
  - 正确的退出码
  - 错误信息清晰

---

### P1 中优先级任务

#### Task 2.1: 添加 snakeyaml 依赖到 pom.xml
- **描述**: 在 Maven 配置中添加 YAML 解析库
- **工作量**: 5 分钟
- **依赖**: 无
- **验收标准**:
  - pom.xml 包含 snakeyaml 2.2 依赖
  - `mvn dependency:resolve` 成功

#### Task 2.2: 实现 BenchmarkConfig.fromYaml(String) 方法
- **描述**: 实现从 YAML 字符串加载配置的核心方法
- **工作量**: 30 分钟
- **依赖**: Task 2.1
- **验收标准**:
  - 方法签名正确
  - 能正确解析 YAML 字符串
  - 配置项映射正确
  - 异常处理完善

#### Task 2.3: 实现 BenchmarkConfig.fromYaml(Path) 方法
- **描述**: 实现从文件路径加载配置
- **工作量**: 15 分钟
- **依赖**: Task 2.2
- **验收标准**:
  - 方法签名正确
  - 能读取文件内容并调用 fromYaml(String)
  - 文件不存在时抛出清晰的异常

#### Task 2.4: 实现 BenchmarkConfig.fromResource(String) 方法
- **描述**: 实现从 classpath 资源加载配置
- **工作量**: 15 分钟
- **依赖**: Task 2.2
- **验收标准**:
  - 方法签名正确
  - 能从 classpath 读取资源
  - 资源不存在时抛出清晰的异常

#### Task 2.5: 编写 BenchmarkConfig YAML 加载单元测试
- **描述**: 为 YAML 加载功能编写完整的单元测试
- **工作量**: 45 分钟
- **依赖**: Task 2.2, 2.3, 2.4
- **验收标准**:
  - 测试覆盖所有 fromYaml 方法
  - 测试正常加载场景
  - 测试异常情况 (无效 YAML、文件不存在等)
  - 测试覆盖率 ≥ 80%

#### Task 2.6: 更新 run-benchmark.sh 使用 YAML 配置
- **描述**: 修改脚本使用新的 YAML 配置加载功能
- **工作量**: 20 分钟
- **依赖**: Task 1.4, Task 2.5
- **验收标准**:
  - 脚本能读取 benchmark.yaml
  - 配置参数传递到 Maven
  - --config 参数能覆盖默认配置

---

### P2 低优先级任务

#### Task 3.1: 创建 HtmlReporter 基础类
- **描述**: 创建 HTML 报告器的基础结构和 HTML 模板
- **工作量**: 30 分钟
- **依赖**: 无
- **验收标准**:
  - 类位于 `src/main/java/com/threadmg/reporters/HtmlReporter.java`
  - 有构造函数接受 outputDirectory 参数
  - 有 report(List<BenchmarkResult>) 方法
  - HTML 模板结构完整

#### Task 3.2: 实现 HtmlReporter HTML 生成逻辑
- **描述**: 实现完整的 HTML 内容生成
- **工作量**: 45 分钟
- **依赖**: Task 3.1
- **验收标准**:
  - 生成有效的 HTML5 文档
  - 包含执行环境信息表格
  - 包含性能对比表格
  - CSS 样式美观
  - 文件正确写入

#### Task 3.3: 编写 HtmlReporter 单元测试
- **描述**: 为 HTML 报告器编写单元测试
- **工作量**: 30 分钟
- **依赖**: Task 3.2
- **验收标准**:
  - 测试报告生成方法
  - 验证 HTML 结构
  - 验证文件写入
  - 测试覆盖率 ≥ 80%

#### Task 3.4: 创建 JsonReporter 基础类
- **描述**: 创建 JSON 报告器的基础结构
- **工作量**: 30 分钟
- **依赖**: 无
- **验收标准**:
  - 类位于 `src/main/java/com/threadmg/reporters/JsonReporter.java`
  - 有构造函数接受 outputDirectory 参数
  - 有 report(List<BenchmarkResult>) 方法

#### Task 3.5: 实现 JsonReporter JSON 生成逻辑
- **描述**: 实现完整的 JSON 内容生成
- **工作量**: 30 分钟
- **依赖**: Task 3.4
- **验收标准**:
  - 生成有效的 JSON
  - 包含 timestamp、environment、results 字段
  - JSON 结构符合规范
  - 文件正确写入

#### Task 3.6: 编写 JsonReporter 单元测试
- **描述**: 为 JSON 报告器编写单元测试
- **工作量**: 30 分钟
- **依赖**: Task 3.5
- **验收标准**:
  - 测试报告生成方法
  - 验证 JSON 结构
  - 验证文件写入
  - 测试覆盖率 ≥ 80%

#### Task 3.7: 集成报告器到 run-benchmark.sh
- **描述**: 在脚本中集成所有报告器
- **工作量**: 20 分钟
- **依赖**: Task 2.6, Task 3.3, Task 3.6
- **验收标准**:
  - --output 参数支持多种格式
  - 所有报告器都能正确调用
  - 报告生成在正确目录

---

### 收尾任务

#### Task 4.1: 代码审查
- **描述**: 审查所有新增代码
- **工作量**: 30 分钟
- **依赖**: 所有实现任务完成
- **验收标准**:
  - 代码符合规范
  - 无 CheckStyle 警告
  - JavaDoc 完整

#### Task 4.2: 更新 README 使用示例
- **描述**: 更新 README 中的脚本使用示例
- **工作量**: 15 分钟
- **依赖**: Task 1.4
- **验收标准**:
  - 脚本使用示例准确
  - 配置示例准确
  - 报告示例准确

#### Task 4.3: 运行完整验证
- **描述**: 运行所有测试和手动验证
- **工作量**: 30 分钟
- **依赖**: 所有任务完成
- **验收标准**:
  - 所有单元测试通过
  - 集成测试通过
  - 手动验证通过
  - 生成的报告格式正确

---

## 📊 任务依赖图

```
P0 任务流:
┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐
│ Task 1.1│ →  │ Task 1.2│    │ Task 1.1│ →  │ Task 1.3│ →  Task 1.4│
│ 目录结构 │    │ 配置文件 │    │ 目录结构 │    │ 脚本框架 │    │ 完整功能 │
└─────────┘    └─────────┘    └─────────┘    └─────────┘    └─────────┘

P1 任务流:
┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐
│ Task 2.1│ →  │ Task 2.2│ →  │ Task 2.3│    │ Task 2.4│
│ 添加依赖 │    │ fromYaml│    │ fromYaml│    │ fromYaml│
│         │    │(String) │    │ (Path)  │    │(Resource)│
└─────────┘    └─────────┘    └─────────┘    └─────────┘
                      │
                      ▼
               ┌─────────┐    ┌─────────┐    ┌─────────┐
               │ Task 2.5│ →  │ Task 2.6│ ←  │ Task 1.4│
               │ 单元测试 │    │ 更新脚本 │    │         │
               └─────────┘    └─────────┘    └─────────┘

P2 任务流:
┌─────────┐    ┌─────────┐    ┌─────────┐
│ Task 3.1│ →  │ Task 3.2│ →  │ Task 3.3│
│Html基础 │    │Html生成 │    │Html测试 │
└─────────┘    └─────────┘    └─────────┘

┌─────────┐    ┌─────────┐    ┌─────────┐
│ Task 3.4│ →  │ Task 3.5│ →  │ Task 3.6│
│Json基础 │    │Json生成 │    │Json测试 │
└─────────┘    └─────────┘    └─────────┘
                      │              │
                      └──────┬───────┘
                             ▼
                      ┌─────────┐
                      │ Task 3.7│
                      │ 集成报告│
                      └─────────┘

收尾任务:
所有实现任务 → Task 4.1 → Task 4.2 → Task 4.3 → ✅ 完成
```

---

## 🕐 时间估算

| 优先级 | 任务数 | 总工作量 |
|-------|-------|---------|
| P0 | 4 | 1.5 小时 |
| P1 | 6 | 2.5 小时 |
| P2 | 7 | 3.5 小时 |
| 收尾 | 3 | 1.25 小时 |
| **总计** | **20** | **约 8.75 小时** |

---

## ✅ 完成检查清单

### P0 检查项
- [ ] Task 1.1: 目录结构创建完成
- [ ] Task 1.2: benchmark.yaml 创建完成
- [ ] Task 1.3: 脚本框架创建完成
- [ ] Task 1.4: 脚本完整功能实现

### P1 检查项
- [ ] Task 2.1: snakeyaml 依赖添加
- [ ] Task 2.2: fromYaml(String) 实现
- [ ] Task 2.3: fromYaml(Path) 实现
- [ ] Task 2.4: fromResource(String) 实现
- [ ] Task 2.5: 单元测试完成
- [ ] Task 2.6: 脚本更新完成

### P2 检查项
- [ ] Task 3.1: HtmlReporter 基础类
- [ ] Task 3.2: HtmlReporter 生成逻辑
- [ ] Task 3.3: HtmlReporter 测试
- [ ] Task 3.4: JsonReporter 基础类
- [ ] Task 3.5: JsonReporter 生成逻辑
- [ ] Task 3.6: JsonReporter 测试
- [ ] Task 3.7: 报告器集成

### 收尾检查项
- [ ] Task 4.1: 代码审查通过
- [ ] Task 4.2: README 更新完成
- [ ] Task 4.3: 完整验证通过

---

*任务版本：1.0*  
*最后更新：2026-03-27*
