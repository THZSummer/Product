# 验证方案 (Validation Plan)

> 本文档定义了 thread-mg 项目的完整验证方案，确保所有基准测试的准确性、可靠性和可复现性。

---

## 📋 目录

1. [验证目标](#-验证目标)
2. [验证维度](#-验证维度)
3. [验证流程](#-验证流程)
4. [验证规则](#-验证规则)
5. [通过标准](#-通过标准)
6. [验证配置](#-验证配置)
7. [验证报告](#-验证报告)
8. [故障排查](#-故障排查)

---

## 🎯 验证目标

### 核心目标

| 目标 | 说明 | 成功标准 |
|------|------|----------|
| **正确性** | 确保测试结果准确反映代码行为 | 测试结果可复现 |
| **一致性** | 确保不同环境下的结果一致 | 跨环境差异 ≤ 10% |
| **可靠性** | 确保测试流程稳定可靠 | 错误率 ≤ 0.1% |
| **可追溯** | 确保结果可追溯到具体代码 | 每次测试有唯一 ID |

### 验证范围

```
┌─────────────────────────────────────────────────────────┐
│                    验证范围                              │
├─────────────────────────────────────────────────────────┤
│  ✓ 单元测试验证     - 功能正确性                        │
│  ✓ 基准测试验证     - 性能指标准确性                    │
│  ✓ 集成测试验证     - 组件协作正确性                    │
│  ✓ 回归测试验证     - 性能变化检测                      │
│  ✓ 稳定性测试验证   - 长时间运行稳定性                  │
│  ✓ 资源测试验证     - 资源使用合理性                    │
└─────────────────────────────────────────────────────────┘
```

---

## 📐 验证维度

### 维度总览

| 维度 | 验证内容 | 工具 | 频率 | 负责人 |
|------|---------|------|------|--------|
| **正确性** | 结果准确、逻辑正确 | JUnit 5 | 每次提交 | 开发者 |
| **性能** | 吞吐量、延迟达标 | JMH | 每次构建 | CI |
| **回归** | 无性能退化 | 历史对比 | 每次合并 | CI |
| **覆盖** | 测试覆盖充分 | JaCoCo | 每次提交 | CI |
| **资源** | CPU/内存合理 | JFR | 每周 | 性能团队 |
| **稳定** | 长时间运行稳定 | 自定义 | 持续 | CI |

### 详细指标

#### 1. 正确性指标

| 指标 | 计算方法 | 目标值 |
|------|---------|--------|
| 测试通过率 | 通过数/总数 × 100% | 100% |
| 断言覆盖率 | 有断言的测试/总测试 × 100% | ≥ 95% |
| 边界覆盖 | 边界用例数/总用例数 × 100% | ≥ 80% |

#### 2. 性能指标

| 指标 | 说明 | 基准值 | 告警阈值 |
|------|------|--------|----------|
| 吞吐量 | ops/sec | 场景定义 | -10% |
| P50 延迟 | 中位延迟 | 场景定义 | +20% |
| P99 延迟 | 99 百分位延迟 | 场景定义 | +50% |
| CPU 使用率 | 测试期间平均 | ≤ 80% | 90% |
| 内存占用 | 堆内存峰值 | ≤ 512MB | 1GB |

#### 3. 回归指标

| 指标 | 计算方法 | 阈值 | 处理 |
|------|---------|------|------|
| 性能变化率 | (新 - 旧)/旧 × 100% | ±5% | 超阈值需说明 |
| 稳定性变化 | 标准差变化率 | ±10% | 超阈值需优化 |
| 覆盖率变化 | 新覆盖 - 旧覆盖 | ≥ 0% | 下降需补充 |

---

## 🔄 验证流程

### 流程总览

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           验证流程                                      │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  代码提交   │ →  │  静态检查   │ →  │  单元测试   │ →  │  基准测试   │
│   Commit    │    │   Lint      │    │    Unit     │    │  Benchmark  │
└─────────────┘    └─────────────┘    └──────┬──────┘    └──────┬──────┘
                                             │                  │
                                             ↓                  ↓
                                       ┌─────────────┐   ┌─────────────┐
                                       │  覆盖检查   │   │  回归检测   │
                                       │  Coverage   │   │ Regression  │
                                       └──────┬──────┘   └──────┬──────┘
                                              │                  │
                                              └────────┬─────────┘
                                                       │
                                                       ↓
                                               ┌─────────────┐
                                               │  资源验证   │
                                               │  Resource   │
                                               └──────┬──────┘
                                                      │
                                                      ↓
                                               ┌─────────────┐
                                               │  稳定性验证 │
                                               │  Stability  │
                                               └──────┬──────┘
                                                      │
                                                      ↓
                                               ┌─────────────┐
                                               │  生成报告   │
                                               │   Report    │
                                               └─────────────┘
```

### 详细步骤

#### 步骤 1: 代码提交验证

```yaml
触发条件：git push
检查项:
  - 代码格式 (CheckStyle)
  - 编译通过 (Maven build)
  - 基础单元测试
  - 覆盖率检查
通过标准:
  - 无 CheckStyle 错误
  - 编译成功
  - 单元测试 100% 通过
  - 覆盖率 ≥ 80%
```

#### 步骤 2: 基准测试验证

```yaml
触发条件：代码合并到 main
检查项:
  - 预热轮次：5 次
  - 基准轮次：10 次
  - 线程数：按场景配置
  - 持续时间：每次迭代 1 秒
通过标准:
  - 结果方差 < 10%
  - 无异常抛出
  - 资源使用在限制内
```

#### 步骤 3: 回归检测

```yaml
触发条件：基准测试完成
检查项:
  - 对比历史基准数据
  - 计算性能变化率
  - 检测异常波动
通过标准:
  - 性能下降 ≤ 5%
  - 延迟增加 ≤ 10%
  - 或提供合理解释
```

#### 步骤 4: 稳定性验证

```yaml
触发条件：每周执行
检查项:
  - 长时间运行测试 (1 小时)
  - 内存泄漏检测
  - 线程泄漏检测
  - 错误率统计
通过标准:
  - 无内存泄漏
  - 无线程泄漏
  - 错误率 ≤ 0.1%
```

---

## 📏 验证规则

### 规则分类

#### 1. 正确性规则 (Correctness Rules)

| 规则 ID | 规则名称 | 描述 | 检查方法 |
|--------|---------|------|---------|
| CR-001 | 结果一致性 | 相同输入产生相同输出 | 确定性测试 |
| CR-002 | 边界处理 | 正确处理边界条件 | 边界测试 |
| CR-003 | 异常处理 | 异常情况下行为正确 | 异常注入 |
| CR-004 | 并发安全 | 多线程环境下数据一致 | 并发测试 |

```java
// 示例：正确性验证规则
public class CorrectnessRule implements ValidationRule {
    @Override
    public ValidationResult validate(BenchmarkResult result) {
        // 1. 检查结果非空
        if (result == null) {
            return ValidationResult.fail("结果为空");
        }
        
        // 2. 检查结果一致性
        if (!checkConsistency(result)) {
            return ValidationResult.fail("结果不一致");
        }
        
        // 3. 检查边界条件
        if (!checkBoundaries(result)) {
            return ValidationResult.fail("边界条件异常");
        }
        
        return ValidationResult.pass();
    }
}
```

#### 2. 性能规则 (Performance Rules)

| 规则 ID | 规则名称 | 描述 | 阈值 |
|--------|---------|------|------|
| PR-001 | 吞吐量下限 | 吞吐量不低于基准 | -10% |
| PR-002 | 延迟上限 | 延迟不高于基准 | +20% |
| PR-003 | 回归检测 | 无显著性能退化 | -5% |
| PR-004 | 方差控制 | 结果波动在可控范围 | < 10% |

```java
// 示例：性能验证规则
public class PerformanceRule implements ValidationRule {
    private static final double REGRESSION_THRESHOLD = 0.05;
    
    @Override
    public ValidationResult validate(BenchmarkResult result) {
        // 获取历史基准
        BenchmarkResult baseline = getBaseline(result.getScenario());
        
        // 计算变化率
        double changeRate = calculateChangeRate(result, baseline);
        
        // 检查回归
        if (changeRate < -REGRESSION_THRESHOLD) {
            return ValidationResult.fail(
                String.format("性能回归 %.2f%%", changeRate * 100)
            );
        }
        
        return ValidationResult.pass();
    }
}
```

#### 3. 资源规则 (Resource Rules)

| 规则 ID | 规则名称 | 描述 | 限制 |
|--------|---------|------|------|
| RR-001 | CPU 使用率 | CPU 使用不超过限制 | 90% |
| RR-002 | 内存使用 | 堆内存不超过限制 | 1GB |
| RR-003 | 线程数 | 线程数不超过限制 | 场景配置 |
| RR-004 | GC 频率 | GC 频率不超过限制 | 10 次/分钟 |

```java
// 示例：资源验证规则
public class ResourceRule implements ValidationRule {
    private static final double MAX_CPU_USAGE = 0.90;
    private static final long MAX_MEMORY = 1073741824; // 1GB
    
    @Override
    public ValidationResult validate(BenchmarkResult result) {
        ResourceMetrics resources = result.getResourceMetrics();
        
        // 检查 CPU
        if (resources.getCpuUsage() > MAX_CPU_USAGE) {
            return ValidationResult.fail(
                String.format("CPU 使用率过高：%.2f%%", resources.getCpuUsage() * 100)
            );
        }
        
        // 检查内存
        if (resources.getMemoryUsage() > MAX_MEMORY) {
            return ValidationResult.fail(
                String.format("内存使用过高：%d MB", resources.getMemoryUsage() / 1048576)
            );
        }
        
        return ValidationResult.pass();
    }
}
```

#### 4. 稳定性规则 (Stability Rules)

| 规则 ID | 规则名称 | 描述 | 限制 |
|--------|---------|------|------|
| SR-001 | 错误率 | 错误率不超过限制 | 0.1% |
| SR-002 | 崩溃率 | 崩溃率不超过限制 | 0% |
| SR-003 | 内存泄漏 | 无内存泄漏 | 0 字节/小时 |
| SR-004 | 线程泄漏 | 无线程泄漏 | 0 个/小时 |

---

## ✅ 通过标准

### 分级标准

| 级别 | 标准 | 处理 |
|------|------|------|
| **优秀** | 所有规则通过，性能提升 | 自动合并 |
| **良好** | 所有规则通过，性能持平 | 自动合并 |
| **合格** | 所有规则通过，性能下降≤5% | 自动合并 |
| **需审查** | 性能下降>5%，有合理解释 | 人工审查 |
| **不合格** | 任何规则失败 | 拒绝合并 |

### 检查清单

#### 提交前检查

- [ ] 代码通过编译
- [ ] 单元测试 100% 通过
- [ ] 代码覆盖率 ≥ 80%
- [ ] 无 CheckStyle 警告
- [ ] README 已更新（如需要）

#### 合并前检查

- [ ] 基准测试完成
- [ ] 性能回归 ≤ 5%
- [ ] 资源使用在限制内
- [ ] 验证报告生成
- [ ] 所有规则通过

#### 发布前检查

- [ ] 稳定性测试通过
- [ ] 文档完整
- [ ] 示例代码可运行
- [ ] 性能报告存档
- [ ] 变更日志更新

---

## ⚙️ 验证配置

### 配置文件

```yaml
# config/validation/validation.yaml
validation:
  # 性能验证配置
  performance:
    regressionThreshold: 0.05      # 5% 性能回归阈值
    warmupIterations: 5            # 预热轮次
    benchmarkIterations: 10        # 基准轮次
    iterationTimeSeconds: 1        # 每次迭代时间
    forkCount: 3                   # 运行次数
    
  # 覆盖率配置
  coverage:
    minimum: 0.80                  # 最低覆盖率
    checkBranches: true            # 检查分支覆盖
    excludePatterns:               # 排除模式
      - "**/test/**"
      - "**/generated/**"
      
  # 稳定性配置
  stability:
    testRuns: 3                    # 稳定性测试次数
    testDurationMinutes: 60        # 测试持续时间
    maxErrorRate: 0.001            # 最大错误率
    
  # 资源配置
  resources:
    maxCpuUsage: 0.90              # 最大 CPU 使用率
    maxMemoryUsage: 1073741824     # 最大内存 (1GB)
    maxThreadCount: 1000           # 最大线程数
    maxGCFrequency: 10             # 最大 GC 频率 (次/分钟)
    
  # 报告配置
  report:
    format: markdown               # 报告格式
    outputDir: results/reports     # 输出目录
    includeCharts: true            # 包含图表
    archiveResults: true           # 存档结果
```

### 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `VALIDATION_MODE` | 验证模式 (strict/normal/relaxed) | normal |
| `SKIP_PERF_CHECK` | 跳过性能检查 | false |
| `COVERAGE_THRESHOLD` | 覆盖率阈值 | 0.80 |
| `REPORT_FORMAT` | 报告格式 | markdown |

---

## 📊 验证报告

### 报告结构

```markdown
# 验证报告

## 基本信息
- 验证 ID: VAL-20260327-001
- 验证时间：2026-03-27 10:00:00
- 验证人：CI System
- 代码版本：abc123

## 验证概览
| 维度 | 状态 | 得分 |
|------|------|------|
| 正确性 | ✅ 通过 | 100% |
| 性能 | ✅ 通过 | 98% |
| 资源 | ✅ 通过 | 95% |
| 稳定性 | ✅ 通过 | 100% |

## 详细结果

### 正确性验证
- 测试用例：150
- 通过：150
- 失败：0

### 性能验证
- 场景数：6
- 吞吐量：平均 +2%
- 延迟：平均 -1%

### 回归检测
- 基准对比：通过
- 最大回归：-3.2% (IoBound-Virtual)

## 问题列表
无

## 结论
✅ 所有验证通过，可以合并
```

### 报告生成

```bash
# 生成验证报告
./scripts/validate.sh --report

# 查看历史报告
ls results/reports/

# 对比报告
./scripts/compare-reports.sh report1.md report2.md
```

---

## 🔧 故障排查

### 常见问题

#### 问题 1: 测试结果波动大

**症状**: 多次运行结果差异 > 20%

**可能原因**:
1. 系统负载不稳定
2. GC 影响
3. 预热不足

**解决方案**:
```bash
# 增加预热轮次
mvn test -Dwarmup=10

# 固定 CPU 频率
sudo cpufreq-set -g performance

# 增加运行次数
mvn test -DforkCount=5
```

#### 问题 2: 内存超限

**症状**: 测试失败，提示 OutOfMemoryError

**可能原因**:
1. 内存泄漏
2. 数据量过大
3. 堆配置过小

**解决方案**:
```bash
# 增加堆大小
mvn test -Xmx2g

# 分析内存使用
jmap -histo:live <pid>

# 检查泄漏
jcmd <pid> GC.class_histogram
```

#### 问题 3: 性能回归

**症状**: 性能下降超过阈值

**可能原因**:
1. 代码变更引入低效
2. 数据结构变更
3. 算法复杂度增加

**解决方案**:
1. 使用 Git 二分法定位问题提交
2. 分析 JFR 记录
3. 优化热点代码

### 诊断工具

| 工具 | 用途 | 命令 |
|------|------|------|
| JFR | 性能分析 | `jcmd <pid> JFR.start` |
| JMAP | 内存分析 | `jmap -heap <pid>` |
| JSTACK | 线程分析 | `jstack <pid>` |
| Async Profiler | 火焰图 | `./profiler.sh <pid>` |

---

## 📚 附录

### A. 验证命令速查

```bash
# 运行所有验证
./scripts/validate.sh

# 运行特定验证
./scripts/validate.sh --correctness
./scripts/validate.sh --performance
./scripts/validate.sh --stability

# 生成报告
./scripts/validate.sh --report

# 对比历史
./scripts/validate.sh --compare HEAD~1
```

### B. 验证状态码

| 状态码 | 含义 |
|--------|------|
| 0 | 所有验证通过 |
| 1 | 正确性验证失败 |
| 2 | 性能验证失败 |
| 3 | 资源验证失败 |
| 4 | 稳定性验证失败 |
| 100+ | 其他错误 |

### C. 相关文档

- [基准测试框架](../implementation/README.md)
- [场景说明](../scenarios/)
- [线程技术说明](../threads/)
- [SDD 工作流](../../README.md#-开发工作流)

---

*文档版本：1.0 | 最后更新：2026-03-27*
