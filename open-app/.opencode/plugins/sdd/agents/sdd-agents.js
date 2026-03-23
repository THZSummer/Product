// SDD Agents 注册
export async function registerAgents(context) {
    const agents = [
        {
            name: 'sdd-spec',
            description: 'SDD 规范编写专家',
            mode: 'subagent',
            promptFile: '.opencode/agents/sdd-spec.md'
        },
        {
            name: 'sdd-plan',
            description: 'SDD 技术规划专家',
            mode: 'subagent',
            promptFile: '.opencode/agents/sdd-plan.md'
        },
        {
            name: 'sdd-tasks',
            description: 'SDD 任务分解专家',
            mode: 'subagent',
            promptFile: '.opencode/agents/sdd-tasks.md'
        },
        {
            name: 'sdd-validate',
            description: 'SDD 验证专家',
            mode: 'subagent',
            promptFile: '.opencode/agents/sdd-validate.md'
        }
    ];
    for (const agent of agents) {
        try {
            const fs = await import('fs/promises');
            await fs.access(agent.promptFile);
            console.log(`✅ Agent 可用：${agent.name}`);
        }
        catch {
            console.warn(`⚠️ Agent prompt 文件不存在：${agent.promptFile}`);
        }
    }
}
