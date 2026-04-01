// .sdd/src/state/manager.ts
import { FeatureState, validateState, FeatureStatus } from './schema-v1.2.11';
import { getSpecsDir, getFeatureDir, getStateFilePath } from '../utils/workspace';
import { readdirSync, readFileSync, writeFileSync, existsSync, mkdirSync, statSync } from 'fs';
import { join } from 'path';
import { validateDependencyGraph } from '../utils/dependency-graph';

/**
 * 扫描子 Feature 目录（同级目录结构）
 * 子 Feature 位于 .sdd/.specs/[sub-feature-id]/ 目录下
 */
export function scanSubFeatures(currentFeaturePath: string): FeatureState[] {
  const specsDir = getSpecsDir();
  const subFeatureStates: FeatureState[] = [];
  
  // 获取父目录路径，以避免扫描包含当前Feature的目录内容
  const currentDir = join(specsDir, currentFeaturePath.split('/')[1]); // Extract feature name
  const parentDir = currentDir.substring(0, currentDir.lastIndexOf('/')) || specsDir;

  // Ensure specsDir exists before reading
  if (!existsSync(parentDir)) {
    console.warn(`Specs directory does not exist: ${parentDir}`);
    return [];
  }

  const entries = readdirSync(parentDir, { withFileTypes: true });
  for (const entry of entries) {
    if (entry.isDirectory() && !entry.name.startsWith('.')) {
      const stateFile = join(parentDir, entry.name, 'state.json');
      if (existsSync(stateFile)) {
        try {
          const content = readFileSync(stateFile, 'utf-8');
          const parsedState = JSON.parse(content);
          subFeatureStates.push(parsedState);
        } catch (error) {
          console.warn(`Failed to read or parse state file: ${stateFile}`, error);
        }
      }
    }
  }
  
  return subFeatureStates;
}

/**
 * 扫描所有同级 feature 目录中的子 Feature
 */
export function scanAllSubFeaturesForDirectory(featureBaseDir: string): FeatureState[] {
  const specsDir = getSpecsDir();
  const subFeatureStates: FeatureState[] = [];
  
  if (!existsSync(specsDir)) {
    console.warn(`Specs directory does not exist: ${specsDir}`);
    return [];
  }

  const entries = readdirSync(specsDir, { withFileTypes: true });
  for (const entry of entries) {
    if (entry.isDirectory() && !entry.name.startsWith('.') && entry.name !== featureBaseDir) {
      const stateFile = join(specsDir, entry.name, 'state.json');
      if (existsSync(stateFile)) {
        try {
          const content = readFileSync(stateFile, 'utf-8');
          const parsedState = JSON.parse(content);
          subFeatureStates.push(parsedState);
        } catch (error) {
          console.warn(`Failed to read or parse state file: ${stateFile}`, error);
        }
      }
    }
  }
  
  return subFeatureStates;
}

/**
 * 加载特定 feature 的状态
 */
export async function loadState(featureId: string): Promise<FeatureState> {
  try {
    const statePath = getStateFilePath(featureId);
    const content = readFileSync(statePath, 'utf-8');
    const state = JSON.parse(content as string);
    
    const validation = validateState(state);
    if (!validation.valid) {
      throw new Error(`State validation failed: ${validation.errors?.map(e => e.message).join(', ')}`);
    }
    
    return state;
  } catch (error) {
    if (error instanceof SyntaxError) {
      throw new Error(`Failed to parse state file for feature ${featureId}: ${error.message}`);
    }
    throw error;
  }
}

/**
 * 保存状态
 */
export async function saveState(featureId: string, state: FeatureState): Promise<void> {
  // 验证状态
  const validation = validateState(state);
  if (!validation.valid) {
    throw new Error(`State validation failed: ${validation.errors?.map(e => e.message).join(', ')}`);
  }
  
  // 创建目录如果不存在
  const featureDir = getFeatureDir(featureId);
  if (!existsSync(featureDir)) {
    mkdirSync(featureDir, { recursive: true });
  }
  
  const statePath = getStateFilePath(featureId);
  const now = new Date().toISOString();
  
  // 添加时间戳
  const updatedState = {
    ...state,
    updatedAt: now,
    createdAt: state.createdAt || now
  };
  
  writeFileSync(statePath, JSON.stringify(updatedState, null, 2));
}

/**
 * 保存子 Feature 状态并检测依赖循环
 */
export async function saveSubFeatureState(featureId: string, subFeatureId: string, state: FeatureState): Promise<void> {
  // 验证状态
  const validation = validateState(state);
  if (!validation.valid) {
    throw new Error(`State validation failed: ${validation.errors?.map(e => e.message).join(', ')}`);
  }
  
  // 检测依赖图循环
  const allSubFeatures = scanAllSubFeaturesForDirectory(featureId);
  
  // 构建依赖图
  const dependencyGraph: Record<string, string[]> = {};
  for (const subFeature of allSubFeatures) {
    if (subFeature.dependencies?.on) {
      dependencyGraph[subFeature.feature] = subFeature.dependencies.on;
    } else {
      dependencyGraph[subFeature.feature] = [];
    }
  }
  
  // 检查新状态中的依赖
  if (state.dependencies?.on) {
    dependencyGraph[subFeatureId] = state.dependencies.on;
  } else if (!(subFeatureId in dependencyGraph)) {
    dependencyGraph[subFeatureId] = [];
  }
  
  // 验证依赖图是否存在循环
  const depValidation = validateDependencyGraph(dependencyGraph);
  if (!depValidation.valid) {
    throw new Error(`循环依赖检测失败：${depValidation.message}`);
  }
  
  // 创建子 Feature 目录如果不存在
  const featureDir = getFeatureDir(join(featureId, subFeatureId)); // Include subFeatureId in path
  if (!existsSync(featureDir)) {
    mkdirSync(featureDir, { recursive: true });
  }
  
  const statePath = getStateFilePath(`${featureId}/${subFeatureId}`); // Include subFeature in path
  const now = new Date().toISOString();
  
  // 添加时间戳
  const updatedState = {
    ...state,
    updatedAt: now,
    createdAt: state.createdAt || now
  };
  
  writeFileSync(statePath, JSON.stringify(updatedState, null, 2));
}

/**
 * 通过目录结构识别 Feature 模式
 */
export async function detectFeatureMode(featurePath: string): Promise<'single' | 'multi'> {
  const specsDir = getSpecsDir();
  if (!existsSync(specsDir)) {
    return 'single'; // 默认模式
  }
  
  const entries = readdirSync(specsDir, { withFileTypes: true });
  const featureDirs = entries
    .filter(entry => entry.isDirectory() && !entry.name.startsWith('.'))
    .map(entry => entry.name);
  
  // 有两个或更多 Feature 目录意味着可能是 multi 模式
  return featureDirs.length > 1 ? 'multi' : 'single';
}

/**
 * 聚合子 Feature 状态计算整体状态
 * 规则：Feature 状态等于所有子 Feature 中最慢的状态
 */
export async function aggregateSubFeatureState(featureBaseName: string): Promise<FeatureState> {
  const subFeatures = scanAllSubFeaturesForDirectory(featureBaseName);
  
  if (subFeatures.length === 0) {
    // 如果没有子 Feature，则假设处于 initial 状态
    const result: FeatureState = {
      feature: featureBaseName,
      status: 'specified',
      createdAt: new Date().toISOString()
    };
    return result;
  }
  
  const statusPriority: FeatureStatus[] = [
    'initiated', 'specified', 'planned', 'tasked', 'implementing', 'reviewing', 'validated', 'completed', 'archived'
  ];
  
  // 找到最低的状态级别
  let minStatusIndex = Infinity;
  
  for (const subFeature of subFeatures) {
    if (subFeature.status) {
      const index = statusPriority.indexOf(subFeature.status);
      if (index !== -1 && index < minStatusIndex) {
        minStatusIndex = index;
      }
    }
  }
  
  const aggregatedStatus: FeatureStatus = statusPriority[minStatusIndex] || 'specified';
  
  // 返回聚合后的状态
  return {
    feature: featureBaseName,
    status: aggregatedStatus,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  };
}