/**
 * Discovery工作流引擎
 * 实现7步需求挖掘流程
 */
import { DiscoveryStep, DiscoveryContext, StepExecutionResult, CoachingConfig } from './types';
import { CoachingModeEngine, CoachingLevel } from './coaching-mode';
import { StateMachine } from '../state/machine';
import { DiscoveryStateValidator } from './state-validator';
export type DiscoveryWorkflowStatus = 'pending' | 'running' | 'completed' | 'failed';
export type StatusChangeCallback = (featureId: string, status: DiscoveryWorkflowStatus, data?: any) => void | Promise<void>;
export interface DiscoveryConfig {
    autoUpdateState?: boolean;
    onStatusChange?: StatusChangeCallback[];
    logLevel?: 'debug' | 'info' | 'warn' | 'error';
}
/**
 * 7 步发现工作流定义
 */
export declare const DISCOVERY_WORKFLOW: DiscoveryStep[];
/**
 * Discovery 工作流引擎
 */
export declare class DiscoveryWorkflowEngine {
    private coachingModeEngine;
    private config;
    private stateMachine?;
    constructor(config?: DiscoveryConfig, stateMachine?: StateMachine);
    /**
     * 设置状态机用于状态联动
     */
    setStateMachine(stateMachine: StateMachine): void;
    /**
     * 注册状态变化回调
     */
    onStatusChange(callback: StatusChangeCallback): void;
    /**
     * 通知状态变化
     */
    private notifyStatusChange;
    /**
     * 执行整个工作流
     */
    execute(context: DiscoveryContext): Promise<DiscoveryContext>;
    /**
     * 对单个步骤应用辅导级别调整
     */
    private adjustStepByCoachingLevel;
    /**
     * 执行单步逻辑
     */
    executeStep(step: DiscoveryStep, context: DiscoveryContext): Promise<StepExecutionResult>;
    /**
     * 调用单个步骤的Agent
     * 这里是模拟实现，在实际中应该调用OpenCode Agent系统
     */
    private callStepAgent;
    /**
     * 从指定步骤恢复执行（支持中断续执行）
     */
    resumeFromStep(startStepIndex: number, context: DiscoveryContext): Promise<DiscoveryContext>;
    /**
     * 获取步骤总数
     */
    getTotalSteps(): number;
    /**
     * 获取当前进度
     */
    getCurrentProgress(context: DiscoveryContext): number;
}
export { CoachingLevel, CoachingConfig, DiscoveryStateValidator };
export type { DiscoveryStep, DiscoveryContext, StepExecutionResult, CoachingModeEngine };
