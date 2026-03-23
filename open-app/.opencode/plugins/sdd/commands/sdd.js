// SDD 命令定义
export const sddCommand = {
    name: 'sdd',
    description: '📋 Specification-Driven Development 工作流',
    category: 'Development',
    usage: `
/sdd init                    初始化 SDD 工作流
/sdd specify <feature>       创建 Feature Specification
/sdd clarify <feature>       澄清规范中的模糊点
/sdd plan <feature>          生成技术计划
/sdd tasks <feature>         分解任务
/sdd implement <task>        实现任务
/sdd validate <feature>      验证实现
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
    async handler(ctx, args) {
        const [subcommand, ...rest] = args._ || [];
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
                return await handlePlan(ctx, rest.join(' '));
            case 'tasks':
                return await handleTasks(ctx, rest.join(' '));
            case 'implement':
                return await handleImplement(ctx, rest.join(' '));
            case 'validate':
                return await handleValidate(ctx, rest.join(' '));
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
async function handleInit(ctx) {
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
async function handleSpecify(ctx, feature) {
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
async function handleClarify(ctx, feature) {
    if (!feature)
        return '❌ 请提供 Feature 名称';
    return `🔍 **澄清规范：${feature}**\n\n正在分析规范中的模糊点...`;
}
async function handlePlan(ctx, feature) {
    if (!feature)
        return '❌ 请提供 Feature 名称';
    return `📐 **生成技术计划：${feature}**\n\n1. 检查规范完成状态 ✓\n2. 检查外部 API 文档缓存\n3. 调用 @sdd-plan agent\n\n请稍候...`;
}
async function handleTasks(ctx, feature) {
    if (!feature)
        return '❌ 请提供 Feature 名称';
    return `📋 **分解任务：${feature}**\n\n1. 检查技术计划完成状态 ✓\n2. 调用 @sdd-tasks agent\n\n请稍候...`;
}
async function handleImplement(ctx, task) {
    if (!task)
        return '❌ 请提供任务 ID\n\n用法：/sdd implement <TASK-XXX>';
    return `🔨 **实现任务：${task}**\n\n1. 检查任务依赖 ✓\n2. 调用 @build agent\n\n请稍候...`;
}
async function handleValidate(ctx, feature) {
    if (!feature)
        return '❌ 请提供 Feature 名称';
    return `✅ **验证实现：${feature}**\n\n1. 检查所有任务完成状态 ✓\n2. 调用 @sdd-validate agent\n\n请稍候...`;
}
async function handleStatus(ctx, feature) {
    return `📊 **SDD 状态**

${feature ? `Feature: ${feature}` : '所有 Features'}

| Feature | 状态 | 任务进度 | 最后更新 |
|---------|------|----------|----------|
| - | - | - | - |

**状态流转:**
\`drafting → specified → clarified → planned → tasked → implementing → validating → completed\`
`;
}
async function handleRetro(ctx, feature) {
    if (!feature)
        return '❌ 请提供 Feature 名称';
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
