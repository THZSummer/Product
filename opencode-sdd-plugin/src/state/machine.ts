// 状态机实现 - 带流程防跳过验证
import * as fs from 'fs/promises';
import * as path from 'path';

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
  missingStages?: { state: string; name: string }[];
  missingFiles?: string[];
  presentFiles?: string[];
}

export class StateMachine {
  private states: Map<string, FeatureState> = new Map();
  private stateFilePath: string;
  
  // 状态流转规则：key=当前状态，value=允许的下一个状态
  private validTransitions: Record<string, string[]> = {
    'drafting': ['specified'],
    'specified': ['planned'],
    'planned': ['tasked'],
    'tasked': ['implementing'],
    'implementing': ['reviewed'],
    'reviewed': ['validated'],
    'validated': ['completed'],
    'completed': [] // 终态，不可再流转
  };
  
  // 每个状态对应的必需文件
  private requiredFiles: Record<string, string[]> = {
    'specified': ['spec.md'],
    'planned': ['spec.md', 'plan.md'],
    'tasked': ['spec.md', 'plan.md', 'tasks.md'],
    'implementing': ['spec.md', 'plan.md', 'tasks.md'],
    'reviewed': ['spec.md', 'plan.md', 'tasks.md', 'review.md'],
    'validated': ['spec.md', 'plan.md', 'tasks.md', 'review.md', 'validation.md'],
    'completed': ['spec.md', 'plan.md', 'tasks.md', 'review.md', 'validation.md']
  };

  constructor(private specsDir: string = '.specs') {
    this.stateFilePath = path.join(specsDir, '.sdd', 'state.json');
  }

  async load() {
    try {
      const data = await fs.readFile(this.stateFilePath, 'utf-8');
      const parsed = JSON.parse(data);
      if (parsed.features) {
        Object.entries(parsed.features).forEach(([key, value]: [string, any]) => {
          this.states.set(key, value);
        });
      }
    } catch {
      // 文件不存在
    }
  }

  async save() {
    const dir = path.dirname(this.stateFilePath);
    await fs.mkdir(dir, { recursive: true });
    
    const data = {
      version: '1.0.0',
      updatedAt: new Date().toISOString(),
      features: Object.fromEntries(this.states)
    };
    
    await fs.writeFile(this.stateFilePath, JSON.stringify(data, null, 2));
  }

  async createFeature(name: string): Promise<FeatureState> {
    const id = name.toLowerCase().replace(/\s+/g, '-').slice(0, 50);
    const now = new Date().toISOString();
    
    const state: FeatureState = {
      id,
      name,
      state: 'drafting',
      createdAt: now,
      updatedAt: now,
      tasks: []
    };
    
    this.states.set(id, state);
    await this.save();
    return state;
  }

  getState(featureId: string): FeatureState | undefined {
    return this.states.get(featureId);
  }

  getAllFeatures(): FeatureState[] {
    return Array.from(this.states.values());
  }
  
  /**
   * 验证状态流转是否合法
   */
  canTransition(featureId: string, targetState: string): { valid: boolean; reason?: string; current?: string; target?: string; allowed?: string[] } {
    const current = this.states.get(featureId);
    if (!current) {
      return { valid: false, reason: 'Feature 不存在', current: undefined, target: targetState };
    }
    
    const currentState = current.state;
    const allowedTargets = this.validTransitions[currentState] || [];
    
    if (!allowedTargets.includes(targetState)) {
      return {
        valid: false,
        reason: `不允许从 ${currentState} 跳转到 ${targetState}`,
        current: currentState,
        target: targetState,
        allowed: allowedTargets
      };
    }
    
    return { valid: true, current: currentState, target: targetState };
  }
  
  /**
   * 获取缺失的前置阶段
   */
  getMissingStages(featureId: string, targetState: string): { state: string; name: string }[] {
    const current = this.states.get(featureId);
    if (!current) return [];
    
    const currentState = current.state;
    const allStates = ['drafting', 'specified', 'planned', 'tasked', 'implementing', 'reviewed', 'validated', 'completed'];
    const currentIndex = allStates.indexOf(currentState);
    const targetIndex = allStates.indexOf(targetState);
    
    if (currentIndex === -1 || targetIndex === -1) return [];
    if (targetIndex <= currentIndex) return []; // 逆向或同阶段
    
    const stageNames: Record<string, string> = {
      'drafting': '规范编写 (spec)',
      'specified': '技术规划 (plan)',
      'planned': '任务分解 (tasks)',
      'tasked': '任务实现 (build)',
      'implementing': '代码审查 (review)',
      'reviewed': '最终验证 (validate)',
      'validated': '完成'
    };
    
    const missing: { state: string; name: string }[] = [];
    for (let i = currentIndex + 1; i < targetIndex; i++) {
      missing.push({ state: allStates[i], name: stageNames[allStates[i]] });
    }
    
    return missing;
  }
  
  /**
   * 检查必需文件是否存在
   */
  async checkRequiredFiles(featureId: string, targetState: string): Promise<{ valid: boolean; missing: string[]; present?: string[]; reason?: string }> {
    const required = this.requiredFiles[targetState];
    if (!required) return { valid: true, missing: [] };
    
    const feature = this.states.get(featureId);
    if (!feature) return { valid: false, missing: required, reason: 'Feature 不存在' };
    
    const featureDir = path.join(this.specsDir, feature.id);
    const missing: string[] = [];
    
    for (const file of required) {
      const filePath = path.join(featureDir, file);
      try {
        await fs.access(filePath);
      } catch {
        missing.push(file);
      }
    }
    
    return {
      valid: missing.length === 0,
      missing,
      present: required.filter(f => !missing.includes(f))
    };
  }
  
  /**
   * 完整的阶段跳转验证（核心方法 - 防跳过关键）
   */
  async validateStageTransition(featureId: string, targetState: string): Promise<TransitionResult> {
    // 1. 加载最新状态
    await this.load();
    
    // 2. 验证状态流转合法性
    const transitionCheck = this.canTransition(featureId, targetState);
    if (!transitionCheck.valid) {
      return {
        allowed: false,
        reason: transitionCheck.reason,
        current: transitionCheck.current,
        target: targetState,
        allowedTargets: transitionCheck.allowed,
        missingStages: this.getMissingStages(featureId, targetState)
      };
    }
    
    // 3. 检查必需文件
    const fileCheck = await this.checkRequiredFiles(featureId, targetState);
    if (!fileCheck.valid) {
      return {
        allowed: false,
        reason: '缺失必需文件',
        current: transitionCheck.current,
        target: targetState,
        missingFiles: fileCheck.missing,
        presentFiles: fileCheck.present
      };
    }
    
    // 4. 验证通过
    return {
      allowed: true,
      current: transitionCheck.current,
      target: targetState
    };
  }
  
  /**
   * 更新状态（带验证）
   */
  async updateState(featureId: string, newState: string, data: any = {}): Promise<FeatureState> {
    await this.load();
    
    const feature = this.states.get(featureId);
    if (!feature) {
      throw new Error(`Feature 不存在：${featureId}`);
    }
    
    // 验证流转
    const validation = await this.validateStageTransition(featureId, newState);
    if (!validation.allowed) {
      throw new Error(`状态流转失败：${validation.reason}`);
    }
    
    feature.state = newState;
    feature.updatedAt = new Date().toISOString();
    Object.assign(feature, data);
    
    this.states.set(featureId, feature);
    await this.save();
    
    return feature;
  }
  
  /**
   * 获取下一步建议
   */
  getNextStep(featureId: string): { state: string; action: string } | null {
    const feature = this.states.get(featureId);
    if (!feature) return null;
    
    const allowed = this.validTransitions[feature.state] || [];
    if (allowed.length === 0) {
      return { state: 'completed', action: '已完成，无需操作' };
    }
    
    const nextState = allowed[0];
    const actionMap: Record<string, string> = {
      'specified': '@sdd spec [feature]',
      'planned': '@sdd plan [feature]',
      'tasked': '@sdd tasks [feature]',
      'implementing': '@sdd build [TASK-XXX]',
      'reviewed': '@sdd review [feature]',
      'validated': '@sdd validate [feature]',
      'completed': '完成'
    };
    
    return { state: nextState, action: actionMap[nextState] || '未知' };
  }
}
