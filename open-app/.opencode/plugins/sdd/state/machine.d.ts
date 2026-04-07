import { PhaseHistory, WorkflowStatus, StateV2_0_0, validateState } from './schema-v2.0.0';
import { DependencyChecker } from './dependency-checker';
export { DependencyChecker };
export type OldFeatureStateEnum = 'drafting' | 'discovered' | 'specified' | 'planned' | 'tasked' | 'implementing' | 'reviewed' | 'validated' | 'completed';
export type AgentWorkflowStateEnum = 'drafting' | 'discovered' | 'specified' | 'planned' | 'tasked' | 'implementing' | 'reviewed' | 'validated' | 'completed';
export type SddPhase = 1 | 2 | 3 | 4 | 5 | 6;
export type FeatureStateEnum = 'drafting' | 'discovered' | 'specified' | 'planned' | 'tasked' | 'implementing' | 'reviewed' | 'validated' | 'completed';
export interface FeatureState {
    id: string;
    name: string;
    state: FeatureStateEnum;
    createdAt: string;
    updatedAt: string;
    tasks?: any[];
}
export interface TransitionResult {
    allowed: boolean;
    current?: string;
    target?: string;
    reason?: string;
    allowedTargets?: string[];
    missingStages?: {
        state: string;
        name: string;
    }[];
    missingFiles?: string[];
    presentFiles?: string[];
}
export { PhaseHistory, WorkflowStatus, StateV2_0_0, validateState };
export interface AgentTransitionHook {
    onTransitionStart?(featureId: string, targetState: FeatureStateEnum): void;
    onTransitionComplete?(featureId: string, previousState: FeatureStateEnum, newState: FeatureStateEnum, triggeredBy?: string, comment?: string): void;
    onError?(error: any, featureId?: string, targetState?: string): void;
}
export interface AutoUpdaterIntegration {
    onFileChange?(filePath: string): void;
    onSessionIdle?(): void;
}
export interface HistoryEntry {
    timestamp: string;
    from: FeatureStateEnum;
    to: FeatureStateEnum;
    triggeredBy: string;
    actor?: string;
    comment?: string;
}
export interface FeatureWithFullHistory extends FeatureState {
    phaseHistory: PhaseHistory[];
    history: HistoryEntry[];
}
export declare class StateMachine {
    private specsDir;
    private states;
    private stateFilePath;
    private dependencyChecker?;
    private agentHook?;
    private validTransitions;
    private requiredFiles;
    constructor(specsDir?: string);
    setAgentHook(hook: AgentTransitionHook): void;
    setDependencyChecker(checker: DependencyChecker): void;
    load(): Promise<void>;
    save(): Promise<void>;
    createFeature(name: string): Promise<FeatureWithFullHistory>;
    getState(featureId: string): FeatureWithFullHistory | undefined;
    getAllFeatures(): FeatureWithFullHistory[];
    /**
     * 获取特定 feature 当前的相位 (SDD Phase: 1-6)
     */
    getCurrentPhase(featureId: string): number;
    /**
     * 验证状态流转是否合法
     */
    canTransition(featureId: string, targetState: FeatureStateEnum): {
        valid: boolean;
        reason?: string;
        current?: FeatureStateEnum;
        target?: FeatureStateEnum;
        allowed?: FeatureStateEnum[];
    };
    /**
     * 获取缺失的前置阶段（用于显示跳过阶段的警告）
     */
    getMissingStages(featureId: string, targetState: FeatureStateEnum): {
        state: string;
        name: string;
    }[];
    /**
     * 检查必需文件是否存在
     */
    checkRequiredFiles(featureId: string, targetState: FeatureStateEnum): Promise<{
        valid: boolean;
        missing: string[];
        present?: string[];
        reason?: string;
    }>;
    /**
     * 完整的阶段跳转验证（核心方法 - 防跳过提醒关键）
     */
    validateStageTransition(featureId: string, targetState: FeatureStateEnum): Promise<TransitionResult>;
    /**
     * 更新状态（带验证），支持钩子和历史追踪
     */
    updateState(featureId: string, newState: FeatureStateEnum, data?: any, triggeredBy?: string, comment?: string, skipValidation?: boolean): Promise<FeatureWithFullHistory>;
    private mapWorkflowStatus;
    /**
     * 获取下一步建议
     */
    getNextStep(featureId: string): {
        state: string;
        action: string;
    } | null;
}
