// 状态机实现
import * as fs from 'fs/promises';
import * as path from 'path';
export class StateMachine {
    specsDir;
    states = new Map();
    stateFilePath;
    constructor(specsDir = '.specs') {
        this.specsDir = specsDir;
        this.stateFilePath = path.join(specsDir, '.sdd', 'state.json');
    }
    async load() {
        try {
            const data = await fs.readFile(this.stateFilePath, 'utf-8');
            const parsed = JSON.parse(data);
            if (parsed.features) {
                Object.entries(parsed.features).forEach(([key, value]) => {
                    this.states.set(key, value);
                });
            }
        }
        catch {
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
    async createFeature(name) {
        const id = name.toLowerCase().replace(/\s+/g, '-').slice(0, 50);
        const now = new Date().toISOString();
        const state = {
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
    getState(featureId) {
        return this.states.get(featureId);
    }
    getAllFeatures() {
        return Array.from(this.states.values());
    }
}
