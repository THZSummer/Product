// SDD 命令定义 - 带流程防跳过验证
import { StateMachine } from '../state/machine.js';

export const sddCommand = {
  name: 'sdd',
  description: '📋 Specification-Driven Development 工作流（带流程守护）',
  category: 'Development',
  
  usage: `
/sdd init                    初始化 SDD 工作流
/sdd specify <feature>       创建 Feature Specification
/sdd clarify <feature>       澄清规范中的模糊点
/sdd plan <feature>          生成技术计划（需 spec.md）
/sdd tasks <feature>         分解任务（需 plan.md）
/sdd implement <task>        实现任务（需 tasks.md）
/sdd validate <feature>      验证实现（需 review 通过）
/sdd status [feature]        查看状态
/sdd retro <feature>         复盘
  `,

  examples: [
    '/sdd init',
    '/sdd specify 用户认证系统',
    '/sdd plan 用户认证系统',
    '/sdd tasks 用户认证系统',
    '/sdd implement TASK-001',
    '/sdd validate 用户认证系统',
    '/sdd status'
  ],

  async handler(ctx: any, args: any) {
    const [subcommand, ...rest] = args._ || [];
    const stateMachine = new StateMachine();

    if (!subcommand) {
      return showHelp();
    }

    switch (subcommand) {
      case 'init':
        return await handleInit(ctx);
      case 'specify':
        return await handleSpecify(ctx, rest.join(' '));
      case 'clarify':
        return await handleClarify(ctx, rest.join(' '));
      case 'plan':
        return await handlePlan(ctx, rest.join(' '), stateMachine);
      case 'tasks':
        return await handleTasks(ctx, rest.join(' '), stateMachine);
      case 'implement':
        return await handleImplement(ctx, rest.join(' '), stateMachine);
      case 'validate':
        return await handleValidate(ctx, rest.join(' '), stateMachine);
      case 'status':
        return await handleStatus(ctx, rest.join(' '));
      case 'retro':
        return await handleRetro(ctx, rest.join(' '));
      default:
        return `❌ 未知命令：${subcommand}\n\n使用 /sdd --help 查看可用命令`;
    }
  }
};

// 命令处理器
async function handleInit(ctx: any) {
  const dirs = [
    '.specs',
    '.specs/examples',
    '.specs/architecture/adr',
    '.specs/development',
    '.specs/planning',
    '.specs/project',
    '.specs/quality',
    '.opencode/agents',
    '.opencode/sdd/api-docs'
  ];

  for (const dir of dirs) {
    await fs.mkdir(dir, { recursive: true });
  }

  return `🚀 **初始化 SDD 工作流完成**

**已创建目录:**
${dirs.map(d => `- ${d}`).join('\n')}

**下一步:**
\`/sdd specify [feature 名称]\`
`;
}

async function handleSpecify(ctx: any, feature: string) {
  if (!feature) {
    return '❌ 请提供 Feature 名称\n\n用法：/sdd specify <feature 名称>';
  }

  return `📝 **创建规范：${feature}**

正在调用 @sdd-spec agent...

**规范结构:**
1. 元数据
2. 上下文
3. Goals & Non-Goals
4. 用户故事
5. 功能需求 (FR-XXX)
6. 非功能需求 (NFR-XXX)
7. 技术设计
8. 边界情况 (EC-XXX)
9. 开放问题

请稍候...
`;
}

async function handleClarify(ctx: any, feature: string) {
  if (!feature) return '❌ 请提供 Feature 名称';
  return `🔍 **澄清规范：${feature}**\n\n正在分析规范中的模糊点...`;
}

async function handlePlan(ctx: any, feature: string, stateMachine: StateMachine) {
  if (!feature) return '❌ 请提供 Feature 名称\n\n用法：/sdd specify <feature 名称>';
  
  // 前置验证：检查 spec.md 是否存在
  const featureId = feature.toLowerCase().replace(/\s+/g, '-').slice(0, 50);
  await stateMachine.load();
  const validation = await stateMachine.validateStageTransition(featureId, 'planned');
  
  if (!validation.allowed) {
    return `❌ **无法开始技术规划**\n\n` +
      `**原因**: ${validation.reason}\n\n` +
      `**当前状态**: ${validation.current || '未知'}\n\n` +
      `**缺失的前置阶段**:\n` +
      (validation.missingStages || []).map(s => `  - ${s.name}`).join('\n') + '\n\n' +
      `👉 请先完成前置阶段后再运行此命令`;
  }
  
  return `📐 **生成技术计划：${feature}**\n\n` +
    `✅ 前置验证通过：spec.md 已存在\n\n` +
    `1. 检查外部 API 文档缓存\n` +
    `2. 调用 @sdd-plan agent\n\n` +
    `请稍候...`;
}

async function handleTasks(ctx: any, feature: string, stateMachine: StateMachine) {
  if (!feature) return '❌ 请提供 Feature 名称';
  
  // 前置验证：检查 plan.md 是否存在
  const featureId = feature.toLowerCase().replace(/\s+/g, '-').slice(0, 50);
  await stateMachine.load();
  const validation = await stateMachine.validateStageTransition(featureId, 'tasked');
  
  if (!validation.allowed) {
    return `❌ **无法开始任务分解**\n\n` +
      `**原因**: ${validation.reason}\n\n` +
      `**当前状态**: ${validation.current || '未知'}\n\n` +
      `**缺失的前置阶段**:\n` +
      (validation.missingStages || []).map(s => `  - ${s.name}`).join('\n') + '\n\n' +
      `👉 请先完成前置阶段后再运行此命令`;
  }
  
  return `📋 **分解任务：${feature}**\n\n` +
    `✅ 前置验证通过：plan.md 已存在\n\n` +
    `1. 调用 @sdd-tasks agent\n\n` +
    `请稍候...`;
}

async function handleImplement(ctx: any, task: string, stateMachine: StateMachine) {
  if (!task) return '❌ 请提供任务 ID\n\n用法：/sdd implement <TASK-XXX>';
  
  // 前置验证：检查 tasks.md 是否存在
  const featureId = task.toLowerCase().replace(/task-\d+/i, '').replace(/-/g, '-').slice(0, 50);
  await stateMachine.load();
  const validation = await stateMachine.validateStageTransition(featureId, 'implementing');
  
  if (!validation.allowed) {
    return `❌ **无法开始任务实现**\n\n` +
      `**原因**: ${validation.reason}\n\n` +
      `**当前状态**: ${validation.current || '未知'}\n\n` +
      `**缺失的前置阶段**:\n` +
      (validation.missingStages || []).map(s => `  - ${s.name}`).join('\n') + '\n\n' +
      `👉 请先完成前置阶段后再运行此命令`;
  }
  
  return `🔨 **实现任务：${task}**\n\n` +
    `✅ 前置验证通过：tasks.md 已存在\n\n` +
    `1. 检查任务依赖\n` +
    `2. 调用 @sdd-build agent\n\n` +
    `请稍候...`;
}

async function handleValidate(ctx: any, feature: string, stateMachine: StateMachine) {
  if (!feature) return '❌ 请提供 Feature 名称';
  
  // 前置验证：检查 review 是否完成
  const featureId = feature.toLowerCase().replace(/\s+/g, '-').slice(0, 50);
  await stateMachine.load();
  const validation = await stateMachine.validateStageTransition(featureId, 'validated');
  
  if (!validation.allowed) {
    return `❌ **无法开始最终验证**\n\n` +
      `**原因**: ${validation.reason}\n\n` +
      `**当前状态**: ${validation.current || '未知'}\n\n` +
      `**缺失的前置阶段**:\n` +
      (validation.missingStages || []).map(s => `  - ${s.name}`).join('\n') + '\n\n' +
      `👉 请先完成前置阶段后再运行此命令`;
  }
  
  return `✅ **验证实现：${feature}**\n\n` +
    `✅ 前置验证通过：review 已完成\n\n` +
    `1. 调用 @sdd-validate agent\n\n` +
    `请稍候...`;
}

async function handleStatus(ctx: any, feature: string | undefined) {
  return `📊 **SDD 状态**

${feature ? `Feature: ${feature}` : '所有 Features'}

| Feature | 状态 | 任务进度 | 最后更新 |
|---------|------|----------|----------|
| - | - | - | - |

**状态流转:**
\`drafting → specified → clarified → planned → tasked → implementing → validating → completed\`
`;
}

async function handleRetro(ctx: any, feature: string) {
  if (!feature) return '❌ 请提供 Feature 名称';
  return `🔄 **复盘：${feature}**

1. 这次规范编写中，哪些地方做得好？
2. 遇到了什么意外或挑战？
3. 下次可以改进什么？
4. 学到了什么新东西？
`;
}

function showHelp() {
  return `📖 **SDD 工作流帮助**

**用法:** \`/sdd <command> [arguments]\`

**命令:**
| 命令 | 说明 |
|------|------|
| init | 初始化 SDD 工作流 |
| specify | 创建 Feature Specification |
| clarify | 澄清规范模糊点 |
| plan | 生成技术计划 |
| tasks | 分解任务 |
| implement | 实现任务 |
| validate | 验证实现 |
| status | 查看状态 |
| retro | 复盘 |

**示例:**
\`\`\`
/sdd init
/sdd specify 用户认证系统
/sdd plan 用户认证系统
/sdd tasks 用户认证系统
/sdd implement TASK-001
/sdd validate 用户认证系统
\`\`\`
`;
}

// 需要 fs 模块
import * as fs from 'fs/promises';
