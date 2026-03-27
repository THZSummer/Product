export interface FeatureState {
    id: string;
    name: string;
    state: string;
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
export declare class StateMachine {
    private specsDir;
    private states;
    private stateFilePath;
    private validTransitions;
    private requiredFiles;
    constructor(specsDir?: string);
    load(): Promise<void>;
    save(): Promise<void>;
    createFeature(name: string): Promise<FeatureState>;
    getState(featureId: string): FeatureState | undefined;
    getAllFeatures(): FeatureState[];
    /**
     * 验证状态流转是否合法
     */
    canTransition(featureId: string, targetState: string): {
        valid: boolean;
        reason?: string;
        current?: string;
        target?: string;
        allowed?: string[];
    };
    /**
     * 获取缺失的前置阶段
     */
    getMissingStages(featureId: string, targetState: string): {
        state: string;
        name: string;
    }[];
    /**
     * 检查必需文件是否存在
     */
    checkRequiredFiles(featureId: string, targetState: string): Promise<{
        valid: boolean;
        missing: string[];
        present?: string[];
        reason?: string;
    }>;
    /**
     * 完整的阶段跳转验证（核心方法 - 防跳过关键）
     */
    validateStageTransition(featureId: string, targetState: string): Promise<TransitionResult>;
    /**
     * 更新状态（带验证）
     */
    updateState(featureId: string, newState: string, data?: any): Promise<FeatureState>;
    /**
     * 获取下一步建议
     */
    getNextStep(featureId: string): {
        state: string;
        action: string;
    } | null;
}
