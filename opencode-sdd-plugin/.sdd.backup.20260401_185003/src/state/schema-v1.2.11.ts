// .sdd/src/state/schema-v1.2.11.ts

/**
 * 统一 State Schema v1.2.11
 * 用于 SDD 多子 Feature 化并行开发
 * 移除了 mode 和 subFeatures 字段，采用目录结构自动识别模式
 */
export interface FeatureState {
  feature: string;           // Feature ID
  name?: string;             // 人类可读名称
  version?: string;          // State schema 版本，默认 "1.2.11"
  status: FeatureStatus;     // 当前状态
  phase?: number;            // SDD 阶段 1-6
  files?: {                  // 文档路径配置
    spec?: string;
    plan?: string;
    tasks?: string;
    readme?: string;
  };
  dependencies?: {           // 依赖关系
    on?: string[];
    blocking?: string[];
  };
  assignee?: string;         // 负责人
  createdAt?: string;
  updatedAt?: string;
}

/**
 * FeatureStatus 枚举定义
 */
export type FeatureStatus = 
  'initiated' | 
  'specified' | 
  'planned' | 
  'tasked' | 
  'implementing' | 
  'reviewing' | 
  'validated' | 
  'completed' | 
  'archived';

/**
 * State Schema v1.2.11 JSON Schema 定义（用于参考和文档）
 */
export const StateSchemaV1211 = {
  $schema: 'http://json-schema.org/draft-07/schema#',
  type: 'object',
  properties: {
    feature: {
      type: 'string',
      description: 'Feature ID'
    },
    name: {
      type: 'string',
      description: '人类可读的 Feature 名称'
    },
    version: {
      type: 'string',
      description: 'State schema 版本，默认 "1.2.11"',
      default: '1.2.11'
    },
    status: {
      type: 'string',
      enum: ['initiated', 'specified', 'planned', 'tasked', 'implementing', 'reviewing', 'validated', 'completed', 'archived'],
      description: '当前状态'
    },
    phase: {
      type: 'number',
      minimum: 1,
      maximum: 6,
      description: 'SDD 阶段 1-6'
    },
    files: {
      type: 'object',
      properties: {
        spec: { type: 'string', default: 'spec.md' },
        plan: { type: 'string', default: 'plan.md' },
        tasks: { type: 'string', default: 'tasks.md' },
        readme: { type: 'string', default: 'README.md' }
      },
      description: '文档路径配置'
    },
    dependencies: {
      type: 'object',
      properties: {
        on: { 
          type: 'array',
          items: { type: 'string' },
          description: 'Feature 依赖项'
        },
        blocking: {
          type: 'array',
          items: { type: 'string' },
          description: '被阻塞的 Feature 列表'
        }
      },
      description: '依赖关系'
    },
    assignee: {
      type: 'string',
      description: '负责人'
    },
    createdAt: {
      type: 'string',
      format: 'date-time',
      description: '创建时间'
    },
    updatedAt: {
      type: 'string',
      format: 'date-time',
      description: '更新时间'
    }
  },
  required: ['feature', 'status'],
  additionalProperties: true
};

/**
 * 手动验证状态对象是否符合 v1.2.11 Schema
 * @param state 待验证的状态对象
 * @returns 验证结果和错误信息
 */
export function validateState(state: any): { valid: boolean; errors?: any[] } {
  const errors: string[] = [];

  // 验证必要字段
  if (typeof state.feature !== 'string' || !state.feature) {
    errors.push('Missing required field: feature (must be a non-empty string)');
  }

  if (typeof state.status !== 'string') {
    errors.push('Missing required field: status (must be a string)');
  } else {
    const validStatuses: FeatureStatus[] = ['initiated', 'specified', 'planned', 'tasked', 'implementing', 'reviewing', 'validated', 'completed', 'archived'];
    if (!validStatuses.includes(state.status as FeatureStatus)) {
      errors.push(`Invalid status: ${state.status}. Valid values are: ${validStatuses.join(', ')}`);
    }
  }

  // 验证 phase 如果存在
  if (state.phase !== undefined && (typeof state.phase !== 'number' || state.phase < 1 || state.phase > 6)) {
    errors.push('Invalid phase: must be a number between 1 and 6');
  }

  // 验证 dependencies 如果存在
  if (state.dependencies) {
    if (typeof state.dependencies !== 'object') {
      errors.push('dependencies must be an object');
    } else {
      if (state.dependencies.on && !Array.isArray(state.dependencies.on)) {
        errors.push('dependencies.on must be an array');
      }
      if (state.dependencies.blocking && !Array.isArray(state.dependencies.blocking)) {
        errors.push('dependencies.blocking must be an array');
      }
    }
  }

  // 验证 files 如果存在
  if (state.files) {
    if (typeof state.files !== 'object') {
      errors.push('files must be an object');
    } else {
      if (state.files.spec && typeof state.files.spec !== 'string') {
        errors.push('files.spec must be a string');
      }
      if (state.files.plan && typeof state.files.plan !== 'string') {
        errors.push('files.plan must be a string');
      }
      if (state.files.tasks && typeof state.files.tasks !== 'string') {
        errors.push('files.tasks must be a string');
      }
      if (state.files.readme && typeof state.files.readme !== 'string') {
        errors.push('files.readme must be a string');
      }
    }
  }

  // 验证日期格式
  if (state.createdAt && typeof state.createdAt !== 'string') {
    errors.push('createdAt must be a string in ISO date format');
  }
  if (state.updatedAt && typeof state.updatedAt !== 'string') {
    errors.push('updatedAt must be a string in ISO date format');
  }
  if (state.createdAt && isNaN(Date.parse(state.createdAt))) {
    errors.push('createdAt must be a valid ISO date string');
  }
  if (state.updatedAt && isNaN(Date.parse(state.updatedAt))) {
    errors.push('updatedAt must be a valid ISO date string');
  }

  if (errors.length > 0) {
    return { valid: false, errors: errors.map(error => ({ message: error })) };
  }

  return { valid: true };
}