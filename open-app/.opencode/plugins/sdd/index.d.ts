import { DiscoveryWorkflowEngine, CoachingLevel, CoachingConfig, CoachingModeEngine, DiscoveryStateValidator, DISCOVERY_WORKFLOW, DiscoveryConfig } from './discovery/workflow-engine';
import { StateMachine, DependencyChecker, FeatureStateEnum, FeatureState, TransitionResult, HistoryEntry, FeatureWithFullHistory, WorkflowStatus, PhaseHistory, validateState } from './state/machine';
import { AutoUpdater } from './state/auto-updater';
import { migrateState, MigrationResult } from './state/migrator';
import { SddMigrateSchemaCommand } from './commands/sdd-migrate-schema';
import { StateV2_0_0 } from './state/schema-v2.0.0';
export declare const SDDPlugin: ({ project, client, $, directory, worktree }: {
    project: any;
    client: any;
    $: any;
    directory: any;
    worktree: any;
}) => Promise<{
    "session.created": (input: any) => Promise<void>;
    "file.edited": (input: any) => Promise<void>;
    "session.idle": (input: any) => Promise<void>;
    "session.end": (input: any) => Promise<void>;
}>;
export { DISCOVERY_WORKFLOW, DiscoveryWorkflowEngine, CoachingLevel, CoachingConfig, CoachingModeEngine, DiscoveryStateValidator, DiscoveryConfig, AutoUpdater, StateMachine, DependencyChecker, StateV2_0_0, WorkflowStatus, PhaseHistory, validateState, FeatureStateEnum, FeatureState, TransitionResult, HistoryEntry, FeatureWithFullHistory, SddMigrateSchemaCommand, migrateState, MigrationResult, };
export default SDDPlugin;
