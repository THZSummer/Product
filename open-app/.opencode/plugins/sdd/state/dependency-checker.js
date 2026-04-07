// 依赖状态检查器
// 基于 MultiFeatureManager 的依赖图实现状态级别的依赖检查
import * as fs from 'fs/promises';
import * as path from 'path';
/**
 * 依赖状态检查器
 *
 * 检查规则:
 * - 状态前进时：检查所有依赖 Feature 的状态 ≥ 当前状态
 * - 状态回退时：警告检查被依赖 Feature 的状态
 */
export class DependencyChecker {
    stateMachine;
    specsDir;
    // 缓存机制
    cache = new Map();
    cacheExpiry = 5000; // 5 秒缓存过期
    lastCacheUpdate = 0;
    constructor(stateMachine, specsDir = 'specs-tree-root') {
        this.stateMachine = stateMachine;
        this.specsDir = specsDir;
    }
    /**
     * 清除缓存
     */
    clearCache() {
        this.cache.clear();
        this.lastCacheUpdate = 0;
    }
    /**
     * 扫描所有 Feature 状态并构建依赖图
     */
    async scanAllFeatures() {
        const now = Date.now();
        // 检查缓存是否有效
        if (this.cache.size > 0 && now - this.lastCacheUpdate < this.cacheExpiry) {
            return this.cache;
        }
        const features = new Map();
        try {
            // 读取 specs 目录
            const specsDirPath = path.join(this.specsDir);
            const items = await fs.readdir(specsDirPath, { withFileTypes: true });
            for (const item of items) {
                if (!item.isDirectory())
                    continue;
                const featureId = item.name;
                const stateFile = path.join(specsDirPath, featureId, 'state.json');
                try {
                    const stateContent = await fs.readFile(stateFile, 'utf-8');
                    const state = JSON.parse(stateContent);
                    features.set(featureId, {
                        featureId,
                        featureName: state.name || featureId,
                        state: state.status || 'specified',
                        dependencies: state.dependencies?.on || []
                    });
                }
                catch (error) {
                    // state.json 不存在或格式错误，跳过
                    console.warn(`无法读取 Feature ${featureId} 的状态文件`);
                }
            }
            // 更新缓存
            this.cache = features;
            this.lastCacheUpdate = now;
        }
        catch (error) {
            console.error('扫描 Features 失败:', error);
        }
        return features;
    }
    /**
     * 检查 Feature 状态前进的依赖
     *
     * 规则: 所有依赖 Feature 的状态必须 ≥ 当前状态
     */
    async checkDependenciesForStateChange(featureId, targetState) {
        const features = await this.scanAllFeatures();
        const feature = features.get(featureId);
        if (!feature) {
            return {
                allowed: true,
                reason: 'Feature 不存在于状态文件中，跳过依赖检查'
            };
        }
        const blockingFeatures = [];
        const warnings = [];
        // 检查所有依赖 Feature
        for (const depId of feature.dependencies) {
            const depFeature = features.get(depId);
            if (!depFeature) {
                warnings.push(`依赖的 Feature ${depId} 不存在`);
                continue;
            }
            // 检查依赖状态是否满足要求
            if (!this.isStateReady(depFeature.state, targetState)) {
                blockingFeatures.push({
                    featureId: depId,
                    featureName: depFeature.featureName,
                    currentState: depFeature.state,
                    requiredState: targetState
                });
            }
        }
        // 如果有阻塞的依赖，不允许状态变更
        if (blockingFeatures.length > 0) {
            return {
                allowed: false,
                reason: `存在 ${blockingFeatures.length} 个未就绪的依赖 Feature`,
                blockingFeatures,
                warnings: warnings.length > 0 ? warnings : undefined
            };
        }
        // 检查通过
        return {
            allowed: true,
            warnings: warnings.length > 0 ? warnings : undefined
        };
    }
    /**
     * 检查状态回退的警告
     *
     * 规则: 如果有其他 Feature 依赖当前 Feature，状态回退可能影响它们
     */
    async checkStateRollbackWarnings(featureId, fromState, toState) {
        const features = await this.scanAllFeatures();
        const warnings = [];
        // 查找所有依赖当前 Feature 的 Feature
        for (const [otherId, otherFeature] of features) {
            if (otherId === featureId)
                continue;
            if (otherFeature.dependencies.includes(featureId)) {
                // 检查依赖者的状态是否高于目标状态
                if (this.isStateHigher(otherFeature.state, toState)) {
                    warnings.push(`Feature ${otherId} (${otherFeature.featureName}) 依赖此 Feature，` +
                        `且当前状态为 ${otherFeature.state}，回退可能导致依赖问题`);
                }
            }
        }
        return warnings;
    }
    /**
     * 检测循环依赖
     */
    async detectCircularDependencies() {
        const features = await this.scanAllFeatures();
        const cycles = [];
        const visited = new Set();
        const recursionStack = new Set();
        const dfs = (featureId, path) => {
            if (recursionStack.has(featureId)) {
                // 发现循环
                const cycleStart = path.indexOf(featureId);
                const cycle = path.slice(cycleStart);
                cycle.push(featureId);
                cycles.push(cycle);
                return;
            }
            if (visited.has(featureId))
                return;
            visited.add(featureId);
            recursionStack.add(featureId);
            path.push(featureId);
            const feature = features.get(featureId);
            if (feature) {
                for (const depId of feature.dependencies) {
                    dfs(depId, [...path]);
                }
            }
            recursionStack.delete(featureId);
        };
        for (const featureId of features.keys()) {
            dfs(featureId, []);
        }
        return cycles;
    }
    /**
     * 获取阻塞当前 Feature 的列表
     */
    async getBlockingFeatures(featureId) {
        const features = await this.scanAllFeatures();
        const feature = features.get(featureId);
        const blocking = [];
        if (!feature)
            return blocking;
        for (const depId of feature.dependencies) {
            const depFeature = features.get(depId);
            if (depFeature) {
                blocking.push({
                    featureId: depId,
                    featureName: depFeature.featureName,
                    state: depFeature.state
                });
            }
        }
        return blocking;
    }
    /**
     * 获取被当前 Feature 阻塞的列表
     */
    async getBlockedByFeatures(featureId) {
        const features = await this.scanAllFeatures();
        const blocked = [];
        for (const [otherId, otherFeature] of features) {
            if (otherId === featureId)
                continue;
            if (otherFeature.dependencies.includes(featureId)) {
                blocked.push({
                    featureId: otherId,
                    featureName: otherFeature.featureName,
                    state: otherFeature.state
                });
            }
        }
        return blocked;
    }
    /**
     * 辅助函数：检查状态是否就绪（≥ 目标状态）
     */
    isStateReady(currentState, targetState) {
        const stateOrder = {
            'drafting': 0,
            'discovered': 1,
            'specified': 2,
            'planned': 3,
            'tasked': 4,
            'implementing': 5,
            'reviewed': 6,
            'validated': 7,
            'completed': 8
        };
        const currentOrder = stateOrder[currentState] ?? 0;
        const targetOrder = stateOrder[targetState] ?? 0;
        return currentOrder >= targetOrder;
    }
    /**
     * 辅助函数：检查状态是否更高
     */
    isStateHigher(state1, state2) {
        const stateOrder = {
            'drafting': 0,
            'discovered': 1,
            'specified': 2,
            'planned': 3,
            'tasked': 4,
            'implementing': 5,
            'reviewed': 6,
            'validated': 7,
            'completed': 8
        };
        const order1 = stateOrder[state1] ?? 0;
        const order2 = stateOrder[state2] ?? 0;
        return order1 > order2;
    }
    /**
     * 获取依赖关系可视化数据
     */
    async getDependencyVisualization() {
        const features = await this.scanAllFeatures();
        const nodes = [];
        const edges = [];
        for (const [featureId, feature] of features) {
            nodes.push({
                id: featureId,
                label: feature.featureName,
                state: feature.state
            });
            for (const depId of feature.dependencies) {
                edges.push({
                    from: featureId,
                    to: depId
                });
            }
        }
        return { nodes, edges };
    }
}
