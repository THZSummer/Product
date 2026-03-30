// SDD Plugin for OpenCode
// Specification: https://opencode.ai/docs/plugins/

export const SDDPlugin = async ({ project, client, $, directory, worktree }) => {
  // 使用官方日志 API
  await client.app.log({
    body: {
      service: "sdd-plugin",
      level: "info",
      message: "SDD Plugin loaded",
      extra: {
        directory: directory,
        project: project?.name
      }
    }
  });

  return {
    // 监听会话创建
    "session.created": async (input) => {
      // 可以在这里初始化 SDD 状态
    },

    // 监听文件编辑
    "file.edited": async (input) => {
      // 追踪规范文件变更
      if (input.filePath.includes(".specs/")) {
        await client.app.log({
          body: {
            service: "sdd-plugin",
            level: "debug",
            message: "Spec file edited",
            extra: { file: input.filePath }
          }
        });
      }
    },

    // 自定义工具
    tool: {
      // SDD 初始化
      sdd_init: {
        description: "Initialize SDD workflow for the project",
        args: {},
        async execute(args, context) {
          const dirs = [
            ".specs",
            ".specs/examples",
            ".specs/architecture/adr",
            ".specs/development",
            ".specs/planning",
            ".specs/project",
            ".specs/quality",
            ".opencode/sdd/api-docs"
          ];

          for (const dir of dirs) {
            await context.$`mkdir -p ${dir}`;
          }

          return "✅ SDD workflow initialized!";
        }
      },

      // 创建规范
      sdd_specify: {
        description: "Create a Feature Specification",
        args: {
          feature: {
            type: "string",
            description: "Feature name"
          }
        },
        async execute(args, context) {
          const feature = args.feature;
          
          // 创建规范目录
          await context.$`mkdir -p .specs/${feature.replace(/\s+/g, "-").toLowerCase()}`;
          
          return `📝 Creating specification for: ${feature}`;
        }
      },

      // 查看状态
      sdd_status: {
        description: "Show SDD workflow status",
        args: {},
        async execute(args, context) {
          const stateFile = ".specs/.sdd/state.json";
          try {
            const content = await context.$`cat ${stateFile}`.text();
            return `📊 SDD Status:\n${content}`;
          } catch {
            return "No SDD state found. Run sdd_init first.";
          }
        }
      },

      // 创建 Roadmap 规划
      sdd_roadmap: {
        description: "Create a multi-version Roadmap plan",
        args: {
          scope: {
            type: "string",
            description: "Roadmap scope (e.g., '2026 Q2', 'Phase 3', 'v1.3.0')"
          }
        },
        async execute(args, context) {
          const scope = args.scope;
          
          // 创建 roadmap 目录
          await context.$`mkdir -p .specs/roadmap`;
          
          return `🗓️ Creating roadmap for: ${scope}`;
        }
      }
    }
  };
};

export default SDDPlugin;
