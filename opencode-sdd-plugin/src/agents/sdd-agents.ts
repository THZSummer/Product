// SDD Agents 注册
export async function registerAgents(context: any) {
  const agents = [
    {
      name: 'sdd',
      description: 'SDD 工作流智能入口 - 自动路由到正确阶段',
      mode: 'subagent',
      promptFile: '.opencode/agents/sdd.md'
    },
    {
      name: 'sdd-help',
      description: 'SDD 工作流帮助 - 查看完整命令参考',
      mode: 'subagent',
      promptFile: '.opencode/agents/sdd-help.md'
    },
    {
      name: 'sdd-spec',
      description: 'SDD 规范编写专家 (阶段 1/6)',
      mode: 'subagent',
      promptFile: '.opencode/agents/sdd-spec.md'
    },
    {
      name: 'sdd-plan',
      description: 'SDD 技术规划专家 (阶段 2/6)',
      mode: 'subagent',
      promptFile: '.opencode/agents/sdd-plan.md'
    },
    {
      name: 'sdd-tasks',
      description: 'SDD 任务分解专家 (阶段 3/6)',
      mode: 'subagent',
      promptFile: '.opencode/agents/sdd-tasks.md'
    },
    {
      name: 'sdd-build',
      description: 'SDD 任务实现专家 (阶段 4/6)',
      mode: 'subagent',
      promptFile: '.opencode/agents/sdd-build.md'
    },
    {
      name: 'sdd-review',
      description: 'SDD 代码审查专家 (阶段 5/6)',
      mode: 'subagent',
      promptFile: '.opencode/agents/sdd-review.md'
    },
    {
      name: 'sdd-validate',
      description: 'SDD 验证专家 (阶段 6/6)',
      mode: 'subagent',
      promptFile: '.opencode/agents/sdd-validate.md'
    }
  ];

  for (const agent of agents) {
    try {
      const fs = await import('fs/promises');
      await fs.access(agent.promptFile);
      console.log(`✅ Agent 可用：${agent.name}`);
    } catch {
      console.warn(`⚠️ Agent prompt 文件不存在：${agent.promptFile}`);
    }
  }
}
