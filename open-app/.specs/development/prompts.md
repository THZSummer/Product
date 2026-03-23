# SDD 提示词库

用于与 AI Agent 交互的标准提示词模板。

---

## /sdd:specify - 规范编写

```
你是 SDD 规范编写专家。请帮我为 [FEATURE_NAME] 创建完整的 Feature Specification。

项目背景：权限管理系统
Feature 概述：[简述要做什么]

请按照以下结构引导我完成规范编写：
1. 元数据（名称、标识符、优先级）
2. 上下文和目标用户
3. Goals 和 Non-Goals
4. 用户故事
5. 功能需求 (FR-XXX)
6. 非功能需求 (NFR-XXX)
7. 技术设计概要
8. 边界情况 (EC-XXX)
9. 开放问题

我的规范编写水平：[beginner/intermediate/advanced]
请根据我的水平调整辅导强度。
```

---

## /sdd:clarify - 规范澄清

```
请分析以下规范，找出：
1. 模糊的术语或描述
2. 未验证的假设
3. 遗漏的边界情况
4. 不可测试的需求

规范位置：.specs/[FEATURE_NAME]/spec.md

请逐个提出问题，不要一次性列出所有问题。
```

---

## /sdd:plan - 技术规划

```
你是 SDD 技术规划专家。请为以下规范创建技术计划：

规范位置：.specs/[FEATURE_NAME]/spec.md

请提供：
1. 架构影响分析
2. 2-3 个技术方案对比（优缺点、风险、工作量）
3. 推荐方案及理由
4. 文件影响分析（创建/修改/删除的文件列表）
5. 风险评估和缓解措施
6. 如需架构决策，生成 ADR

注意：如果规范涉及外部 API，请先检查 .opencode/sdd/api-docs/ 是否有缓存。
```

---

## /sdd:tasks - 任务分解

```
你是 SDD 任务分解专家。请将以下技术计划分解为原子任务：

计划位置：.specs/[FEATURE_NAME]/plan.md

要求：
1. 每个任务有唯一 ID (TASK-XXX)
2. 标注复杂度 (S/M/L)
3. 明确前置依赖
4. 分配执行波次
5. 定义可验证的验收标准
6. 提供验证命令

输出位置：.specs/[FEATURE_NAME]/tasks.md
```

---

## /sdd:implement - 实现任务

```
请实现以下任务：

任务位置：.specs/[FEATURE_NAME]/tasks.md
任务 ID: TASK-XXX

实现要求：
1. 只读取当前任务相关的文件
2. 遵循 .opencode/constitution.md 的规则
3. 完成后运行验收标准中的验证命令
4. 如遇阻塞，报告而非绕过
```

---

## /sdd:validate - 验证实现

```
你是 SDD 验证专家。请验证以下 Feature 的实现：

规范位置：.specs/[FEATURE_NAME]/spec.md
代码位置：[相关代码目录]

请检查：
1. 需求覆盖度（FR/NFR/EC）
2. 规范一致性
3. 宪法合规性（特别是权限安全）
4. 孤立代码检测

输出验证报告到：.specs/[FEATURE_NAME]/validation.md
```

---

## /sdd:api-docs - API 文档缓存

```
请获取并缓存以下外部服务的 API 文档：

服务名称：[SERVICE_NAME]
文档 URL: [API_DOCS_URL]

缓存位置：.opencode/sdd/api-docs/[service].json

需要缓存的信息：
1. 认证方式
2. 相关端点详情
3. 请求/响应格式
4. 速率限制
5. 错误码
```

---

## /sdd:retro - 复盘

```
Feature [FEATURE_NAME] 已完成。请引导我进行复盘：

1. 这次规范编写中，哪些地方做得好？
2. 遇到了什么意外或挑战？
3. 下次可以改进什么？
4. 学到了什么新东西？

将复盘记录保存到：.specs/[FEATURE_NAME]/retro.md
```

---

**版本**: 1.0.0  
**最后更新**: 2026-03-20  
**项目**: open-app
