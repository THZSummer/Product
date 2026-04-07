import { StateMachine, FeatureStateEnum } from './machine';
/**
 * 依赖检查结果
 */
export interface DependencyCheckResult {
    allowed: boolean;
    reason?: string;
    blockingFeatures?: Array<{
        featureId: string;
        featureName: string;
        currentState: FeatureStateEnum;
        requiredState: FeatureStateEnum;
    }>;
    warnings?: string[];
}
/**
 * Feature 状态信息
 */
export interface FeatureStateInfo {
    featureId: string;
    featureName: string;
    state: FeatureStateEnum;
    dependencies: string[];
}
/**
 * 依赖状态检查器
 *
 * 检查规则:
 * - 状态前进时：检查所有依赖 Feature 的状态 ≥ 当前状态
 * - 状态回退时：警告检查被依赖 Feature 的状态
 */
export declare class DependencyChecker {
    private stateMachine;
    private specsDir;
    private cache;
    private cacheExpiry;
    private lastCacheUpdate;
    constructor(stateMachine: StateMachine, specsDir?: string);
    /**
     * 清除缓存
     */
    clearCache(): void;
    /**
     * 扫描所有 Feature 状态并构建依赖图
     */
    scanAllFeatures(): Promise<Map<string, FeatureStateInfo>>;
    /**
     * 检查 Feature 状态前进的依赖
     *
     * 规则: 所有依赖 Feature 的状态必须 ≥ 当前状态
     */
    checkDependenciesForStateChange(featureId: string, targetState: FeatureStateEnum): Promise<DependencyCheckResult>;
    /**
     * 检查状态回退的警告
     *
     * 规则: 如果有其他 Feature 依赖当前 Feature，状态回退可能影响它们
     */
    checkStateRollbackWarnings(featureId: string, fromState: FeatureStateEnum, toState: FeatureStateEnum): Promise<string[]>;
    /**
     * 检测循环依赖
     */
    detectCircularDependencies(): Promise<Array<string[]>>;
    /**
     * 获取阻塞当前 Feature 的列表
     */
    getBlockingFeatures(featureId: string): Promise<Array<{
        featureId: string;
        featureName: string;
        state: FeatureStateEnum;
    }>>;
    /**
     * 获取被当前 Feature 阻塞的列表
     */
    getBlockedByFeatures(featureId: string): Promise<Array<{
        featureId: string;
        featureName: string;
        state: FeatureStateEnum;
    }>>;
    /**
     * 辅助函数：检查状态是否就绪（≥ 目标状态）
     */
    private isStateReady;
    /**
     * 辅助函数：检查状态是否更高
     */
    private isStateHigher;
    /**
     * 获取依赖关系可视化数据
     */
    getDependencyVisualization(): Promise<{
        nodes: Array<{
            id: string;
            label: string;
            state: FeatureStateEnum;
        }>;
        edges: Array<{
            from: string;
            to: string;
        }>;
    }>;
}
