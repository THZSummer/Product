// 状态机实现
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

export class StateMachine {
  private states: Map<string, FeatureState> = new Map();
  private stateFilePath: string;

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
}
