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
      if (input.filePath.includes("specs-tree-root/")) {
        await client.app.log({
          body: {
            service: "sdd-plugin",
            level: "debug",
            message: "Spec file edited",
            extra: { file: input.filePath }
          }
        });
      }
    }
  };
};

export default SDDPlugin;
