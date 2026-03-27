# 规范文档：Phase 2 测试脚本

**Feature ID**: test-script  
**创建日期**: 2026-03-27  
**状态**: in-progress  

---

## 🎯 目标

创建一套便捷的测试脚本，支持 Phase 2 代码的增量测试和完整测试，提供快速反馈和覆盖率报告。

---

## 📋 需求说明

### 功能需求

#### FR-001: 增量测试
- 检测 Git 变更文件
- 只运行与变更相关的测试类
- 支持 Phase 2 新增的 4 个核心类的增量测试：
  - `ThreadPoolStrategy`
  - `HighConcurrencyScenario`
  - `MixedLoadScenario`
  - `RecursiveScenario`

#### FR-002: 完整测试
- 运行项目所有测试
- 生成完整测试报告
- 支持全量覆盖率分析

#### FR-003: 测试报告
- 彩色输出测试结果
- 显示测试进度
- 统计通过率、失败数、跳过数
- 显示测试耗时

#### FR-004: 覆盖率报告
- 集成 JaCoCo 覆盖率工具
- 生成 HTML 覆盖率报告
- 显示覆盖率百分比

#### FR-005: 监听模式
- 监听文件变更
- 自动触发测试
- 支持热重载测试

### 非功能需求

#### NFR-001: 性能
- 增量测试启动时间 < 5 秒
- 完整测试 < 60 秒（取决于硬件）
- 监听模式响应时间 < 2 秒

#### NFR-002: 可用性
- 跨平台兼容（Linux/macOS）
- 清晰的帮助文档
- 友好的错误提示

#### NFR-003: 可维护性
- 脚本结构清晰
- 完整的注释
- 易于扩展新测试场景

---

## 📐 设计规范

### 脚本结构

```
scripts/
├── test-phase2.sh      # 主测试脚本
├── test-full.sh        # 完整测试脚本（快捷方式）
└── test-watch.sh       # 监听模式脚本（快捷方式）
```

### 命令行接口

```bash
# 主脚本用法
./scripts/test-phase2.sh [选项]

# 支持的选项
-full          # 运行完整测试套件
-incremental   # 只测试 Phase 2 新增/修改的类 (默认)
-coverage      # 生成覆盖率报告
-report        # 生成详细测试报告
-watch         # 监听模式，文件变更自动测试
-help          # 显示帮助信息

# 组合使用
./scripts/test-phase2.sh -incremental -coverage
./scripts/test-phase2.sh -full -report
```

### 输出格式

#### 正常测试输出
```
========================================
 Phase 2 增量测试
========================================

📝 检测到变更文件:
  - src/main/java/.../ThreadPoolStrategy.java
  - src/test/java/.../ThreadPoolStrategyTest.java

🧪 运行测试: ThreadPoolStrategyTest
  ✅ testConstructor (0.12s)
  ✅ testExecute (0.45s)

========================================
 测试结果：86/86 通过 (100%)
 覆盖率：92%
 耗时：12.3s
========================================
```

#### 错误输出
```
========================================
 ❌ 测试失败
========================================

📊 失败统计：
  - 失败：2
  - 跳过：0
  - 总计：86

📋 失败详情:
  1. ThreadPoolStrategyTest.testRejectPolicy
     Expected: true but was: false
     
  2. HighConcurrencyScenarioTest.testTaskExecution
     Timeout after 30s

========================================
```

### Phase 2 测试类映射

| 主类 | 测试类 | Maven 参数 |
|------|--------|-----------|
| ThreadPoolStrategy | ThreadPoolStrategyTest | -Dtest=ThreadPoolStrategyTest |
| HighConcurrencyScenario | HighConcurrencyScenarioTest | -Dtest=HighConcurrencyScenarioTest |
| MixedLoadScenario | MixedLoadScenarioTest | -Dtest=MixedLoadScenarioTest |
| RecursiveScenario | RecursiveScenarioTest | -Dtest=RecursiveScenarioTest |

---

## ✅ 验收标准

### AC-001: 增量测试
- [ ] 脚本能正确检测 Git 变更
- [ ] 只运行与变更相关的测试类
- [ ] 如果没有变更，提示用户并退出

### AC-002: 完整测试
- [ ] 运行所有测试类
- [ ] 生成正确的测试报告
- [ ] 返回正确的退出码（成功=0，失败≠0）

### AC-003: 覆盖率报告
- [ ] 生成 JaCoCo HTML 报告
- [ ] 报告路径正确：`target/site/jacoco/`
- [ ] 显示覆盖率百分比

### AC-004: 监听模式
- [ ] 监听文件变更
- [ ] 自动触发测试
- [ ] 支持 Ctrl+C 退出

### AC-005: 帮助文档
- [ ] `-help` 显示完整帮助信息
- [ ] 包含使用示例
- [ ] 包含所有选项说明

---

## 🔗 相关文件

- `.specs/phase2/tasks.md` - Phase 2 任务定义
- `pom.xml` - Maven 配置（包含 JaCoCo 插件）
- `scripts/run-benchmark.sh` - 现有基准测试脚本

---

*文档版本：1.0 | 创建日期：2026-03-27*
