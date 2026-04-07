/**
 * SDD 统一错误处理系统
 * 实现 FR-016~019: 统一错误处理体系，解决 T-005 错误处理不统一问题
 */
export declare enum ErrorCode {
    STATE_INVALID_TRANSITION = "STATE_INVALID_TRANSITION",// 无效的状态迁移
    STATE_VALIDATION_FAILED = "STATE_VALIDATION_FAILED",// 状态验证失败
    STATE_FILE_NOT_FOUND = "STATE_FILE_NOT_FOUND",// 状态文件未找到
    STATE_LOAD_ERROR = "STATE_LOAD_ERROR",// 状态加载错误
    STATE_SAVE_ERROR = "STATE_SAVE_ERROR",// 状态保存错误
    STATE_MISSING_DEPENDENCY = "STATE_MISSING_DEPENDENCY",// 状态依赖缺失
    DISCOVERY_STEP_EXECUTION_FAILED = "DISCOVERY_STEP_EXECUTION_FAILED",// 发现步骤执行失败
    DISCOVERY_CONTEXT_INVALID = "DISCOVERY_CONTEXT_INVALID",// 发现上下文无效
    DISCOVERY_CONFIG_MISSING = "DISCOVERY_CONFIG_MISSING",// 发现配置缺失
    DISCOVERY_RESULT_INVALID = "DISCOVERY_RESULT_INVALID",// 发现结果无效
    DISCOVERY_STEP_NOT_FOUND = "DISCOVERY_STEP_NOT_FOUND",// 发现步骤未找到
    TOOL_ARGUMENT_INVALID = "TOOL_ARGUMENT_INVALID",// 工具参数无效
    TOOL_FILE_OPERATION_FAILED = "TOOL_FILE_OPERATION_FAILED",// 文件操作失败
    TOOL_PARSE_ERROR = "TOOL_PARSE_ERROR",// 解析错误
    TOOL_EXECUTE_ERROR = "TOOL_EXECUTE_ERROR",// 执行错误
    TOOL_TIMEOUT_ERROR = "TOOL_TIMEOUT_ERROR",// 工具超时错误
    AGENT_NOT_FOUND = "AGENT_NOT_FOUND",// Agent 未找到
    AGENT_ALREADY_REGISTERED = "AGENT_ALREADY_REGISTERED",// Agent 已注册
    AGENT_REGISTRATION_FAILED = "AGENT_REGISTRATION_FAILED",// Agent 注册失败
    AGENT_EXECUTION_ERROR = "AGENT_EXECUTION_ERROR",// Agent 执行错误
    AGENT_CONFIGURATION_ERROR = "AGENT_CONFIGURATION_ERROR",// Agent 配置错误
    FILE_READ_ERROR = "FILE_READ_ERROR",// 文件读取错误
    FILE_WRITE_ERROR = "FILE_WRITE_ERROR",// 文件写入错误
    FILE_ACCESS_DENIED = "FILE_ACCESS_DENIED",// 文件访问被拒
    FILE_NOT_EXIST = "FILE_NOT_EXIST",// 文件不存在
    FILE_FORMAT_INVALID = "FILE_FORMAT_INVALID"
}
export interface ErrorContext {
    code: ErrorCode;
    details?: Record<string, any>;
    stack?: string;
    timestamp: string;
    component?: string;
    userId?: string;
    sessionId?: string;
}
/**
 * SDD 基础错误类
 * 所有 SDD 错误类的基类
 */
export declare class SddError extends Error {
    readonly code: ErrorCode;
    readonly context: ErrorContext;
    readonly isSddError = true;
    constructor(message: string, context: ErrorContext);
    /**
     * 获取错误的详细信息
     */
    toDetailedString(): string;
}
/**
 * 状态管理错误
 */
export declare class StateError extends SddError {
    constructor(code: Exclude<ErrorCode, ErrorCode.STATE_INVALID_TRANSITION>, message: string, details?: Record<string, any>);
}
/**
 * 发现阶段错误
 */
export declare class DiscoveryError extends SddError {
    constructor(code: ErrorCode, message: string, details?: Record<string, any>);
}
/**
 * 工具函数错误
 */
export declare class ToolError extends SddError {
    constructor(code: ErrorCode, message: string, details?: Record<string, any>);
}
/**
 * Agent 错误
 */
export declare class AgentError extends SddError {
    constructor(code: ErrorCode, message: string, details?: Record<string, any>);
}
/**
 * 配置相关错误
 */
export declare class ConfigError extends SddError {
    constructor(code: ErrorCode, message: string, details?: Record<string, any>);
}
/**
 * 错误处理器
 * 提供标准化的错误处理方式
 */
export declare class ErrorHandler {
    /**
     * 根据错误类型执行不同的处理策略
     */
    static handle(error: unknown, defaultSeverity?: 'warn' | 'error'): SddError | Error;
    /**
     * 记录错误日志
     */
    private static logError;
}
/**
 * 格式化错误信息
 */
export declare function formatErrorMessage(error: SddError): string;
export default ErrorHandler;
