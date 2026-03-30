export declare const SDDPlugin: ({ project, client, $, directory, worktree }: {
    project: any;
    client: any;
    $: any;
    directory: any;
    worktree: any;
}) => Promise<{
    "session.created": (input: any) => Promise<void>;
    "file.edited": (input: any) => Promise<void>;
    tool: {
        sdd_init: {
            description: string;
            args: {};
            execute(args: any, context: any): Promise<string>;
        };
        sdd_specify: {
            description: string;
            args: {
                feature: {
                    type: string;
                    description: string;
                };
            };
            execute(args: any, context: any): Promise<string>;
        };
        sdd_status: {
            description: string;
            args: {};
            execute(args: any, context: any): Promise<string>;
        };
        sdd_roadmap: {
            description: string;
            args: {
                scope: {
                    type: string;
                    description: string;
                };
            };
            execute(args: any, context: any): Promise<string>;
        };
    };
}>;
export default SDDPlugin;
