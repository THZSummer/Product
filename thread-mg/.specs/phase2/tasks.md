# 任务分解：Phase 2 扩展阶段

**Feature ID**: phase2-extension  
**关联文档**: [spec.md](spec.md) | [plan.md](plan.md)  
**创建日期**: 2026-03-27  
**状态**: completed（补档）

---

## 📋 任务列表

### P0 高优先级任务

#### TASK-001: 实现 ThreadPoolStrategy 核心类
- **描述**: 实现线程池策略核心类，支持可配置的线程池参数
- **复杂度**: M
- **前置依赖**: 无
- **执行波次**: 1
- **预计工作量**: 2 小时

##### 涉及文件
- [NEW] `src/main/java/com/threadmg/threads/pool/ThreadPoolStrategy.java`
- [NEW] `src/test/java/com/threadmg/threads/pool/ThreadPoolStrategyTest.java`

##### 验收标准
- [ ] 实现 ThreadStrategy 接口
- [ ] 支持核心线程数、最大线程数、队列容量配置
- [ ] 支持 4 种拒绝策略（Abort、CallerRuns、Discard、DiscardOldest）
- [ ] 实现 createExecutor() 方法返回 ExecutorService
- [ ] 实现 shutdown() 方法正确释放资源
- [ ] 实现 builder 模式支持链式配置
- [ ] 完整的 JavaDoc 注释
- [ ] 单元测试覆盖率 > 90%

##### 验证命令
```bash
mvn test -Dtest=ThreadPoolStrategyTest
```

---

#### TASK-002: 实现 HighConcurrencyScenario 场景类
- **描述**: 实现高并发短任务场景，模拟大量短生命周期任务
- **复杂度**: M
- **前置依赖**: 无
- **执行波次**: 1
- **预计工作量**: 1.5 小时

##### 涉及文件
- [NEW] `src/main/java/com/threadmg/scenarios/concurrency/HighConcurrencyScenario.java`
- [NEW] `src/test/java/com/threadmg/scenarios/concurrency/HighConcurrencyScenarioTest.java`

##### 验收标准
- [ ] 实现 Scenario 接口
- [ ] 支持配置任务数量（默认 10000）
- [ ] 支持配置执行时间范围（1-10ms）
- [ ] 支持模拟 I/O 等待
- [ ] 使用随机执行时间模拟真实场景
- [ ] 记录每个任务的延迟
- [ ] 单元测试覆盖率 > 85%

##### 验证命令
```bash
mvn test -Dtest=HighConcurrencyScenarioTest
```

---

#### TASK-003: 实现 MixedLoadScenario 场景类
- **描述**: 实现混合负载场景，混合 CPU 密集型和 I/O 密集型任务
- **复杂度**: M
- **前置依赖**: 无
- **执行波次**: 1
- **预计工作量**: 1.5 小时

##### 涉及文件
- [NEW] `src/main/java/com/threadmg/scenarios/mixed/MixedLoadScenario.java`
- [NEW] `src/test/java/com/threadmg/scenarios/mixed/MixedLoadScenarioTest.java`

##### 验收标准
- [ ] 实现 Scenario 接口
- [ ] 支持配置 CPU/I/O 任务比例
- [ ] CPU 任务使用矩阵计算
- [ ] I/O 任务使用 Thread.sleep 模拟
- [ ] 任务随机分配
- [ ] 单元测试覆盖率 > 85%

##### 验证命令
```bash
mvn test -Dtest=MixedLoadScenarioTest
```

---

#### TASK-004: 实现 RecursiveScenario 场景类
- **描述**: 实现递归分治场景，支持多种递归算法
- **复杂度**: L
- **前置依赖**: 无
- **执行波次**: 1
- **预计工作量**: 2 小时

##### 涉及文件
- [NEW] `src/main/java/com/threadmg/scenarios/recursive/RecursiveScenario.java`
- [NEW] `src/test/java/com/threadmg/scenarios/recursive/RecursiveScenarioTest.java`

##### 验收标准
- [ ] 实现 Scenario 接口
- [ ] 支持 4 种算法类型（快速排序、归并排序、斐波那契、矩阵乘法）
- [ ] 使用 Fork/Join 框架实现并行
- [ ] 支持配置并行阈值
- [ ] 验证结果正确性
- [ ] 单元测试覆盖率 > 85%

##### 验证命令
```bash
mvn test -Dtest=RecursiveScenarioTest
```

---

### P1 中优先级任务

#### TASK-005: 更新 benchmark.yaml 配置
- **描述**: 添加新场景和策略到基准测试配置
- **复杂度**: S
- **前置依赖**: TASK-001, TASK-002, TASK-003, TASK-004
- **执行波次**: 2
- **预计工作量**: 0.5 小时

##### 涉及文件
- [MODIFY] `config/benchmark/benchmark.yaml`

##### 验收标准
- [ ] 添加 `pool` 策略到 strategies 列表
- [ ] 添加 `high-concurrency` 场景到 scenarios 列表
- [ ] 添加 `mixed-load` 场景到 scenarios 列表
- [ ] 添加 `recursive` 场景到 scenarios 列表
- [ ] YAML 语法正确
- [ ] 配置项有中文注释

##### 验证命令
```bash
# 验证 YAML 语法
python3 -c "import yaml; yaml.safe_load(open('config/benchmark/benchmark.yaml'))"
```

---

#### TASK-006: 更新 README.md 功能清单
- **描述**: 更新项目 README，添加 Phase 2 新增功能
- **复杂度**: S
- **前置依赖**: TASK-001, TASK-002, TASK-003, TASK-004
- **执行波次**: 2
- **预计工作量**: 0.5 小时

##### 涉及文件
- [MODIFY] `README.md`

##### 验收标准
- [ ] 在功能清单中添加 ThreadPoolStrategy
- [ ] 在功能清单中添加 HighConcurrencyScenario
- [ ] 在功能清单中添加 MixedLoadScenario
- [ ] 在功能清单中添加 RecursiveScenario
- [ ] 更新完成度追踪（40% → 100%）

---

#### TASK-007: 创建 Phase 2 验证报告
- **描述**: 创建 Phase 2 完成验证报告
- **复杂度**: S
- **前置依赖**: TASK-001, TASK-002, TASK-003, TASK-004, TASK-005, TASK-006
- **执行波次**: 3
- **预计工作量**: 1 小时

##### 涉及文件
- [NEW] `docs/validation/PHASE2_REPORT.md`

##### 验收标准
- [ ] 包含 Phase 2 目标回顾
- [ ] 包含所有实现类的测试覆盖率报告
- [ ] 包含基准测试结果
- [ ] 包含与新场景的性能对比
- [ ] 包含问题和改进建议

---

## 📊 任务汇总

### 复杂度分布
| 复杂度 | 数量 | 占比 |
|--------|------|------|
| S (小) | 3 | 43% |
| M (中) | 3 | 43% |
| L (大) | 1 | 14% |

### 执行波次
| 波次 | 任务数 | 任务 ID |
|------|--------|---------|
| Wave 1 | 4 | TASK-001 ~ TASK-004 |
| Wave 2 | 2 | TASK-005 ~ TASK-006 |
| Wave 3 | 1 | TASK-007 |

### 工作量估算
| 优先级 | 任务数 | 预计工时 |
|--------|--------|----------|
| P0 | 4 | 7 小时 |
| P1 | 3 | 2 小时 |
| **总计** | **7** | **9 小时** |

---

## 🔗 任务依赖图

```
Wave 1 (并行执行):
┌───────────┐  ┌───────────┐  ┌───────────┐  ┌───────────┐
│ TASK-001  │  │ TASK-002  │  │ TASK-003  │  │ TASK-004  │
│ 线程池策略 │  │ 高并发场景 │  │ 混合负载  │  │ 递归分治  │
└─────┬─────┘  └─────┬─────┘  └─────┬─────┘  └─────┬─────┘
      │              │              │              │
      └──────────────┴──────────────┴──────────────┘
                                    │
                              Wave 2 (并行执行):
                        ┌───────────┴───────────┐
                        ▼                       ▼
                  ┌───────────┐           ┌───────────┐
                  │ TASK-005  │           │ TASK-006  │
                  │ 配置更新  │           │ README 更新│
                  └─────┬─────┘           └─────┬─────┘
                        │                       │
                        └───────────┬───────────┘
                                    │
                              Wave 3:
                                    ▼
                              ┌───────────┐
                              │ TASK-007  │
                              │ 验证报告  │
                              └───────────┘
```

---

*文档版本：1.0（补档） | 创建日期：2026-03-27*
