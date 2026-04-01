// .sdd/src/utils/subfeature-manager.ts
import { FeatureState } from '../state/schema-v1.2.11';
import { getSpecsDir, getFeatureDir } from './workspace';
import { readdirSync, readFileSync, writeFileSync, existsSync, mkdirSync, statSync } from 'fs';
import { join } from 'path';
import { scanSubFeatures, loadState, saveState } from '../state/manager';

/**
 * 检测 Feature 是否为多子 Feature 模式
 * 通过扫描同级目录判断是否存在多个子 Feature 目录
 */
export async function detectFeatureMode(featureId: string): Promise<'single' | 'multi'> {
  const specsDir = getSpecsDir();
  if (!existsSync(specsDir)) {
    return 'single'; // 如果目录不存在，默认认为是单模块
  }

  const entries = readdirSync(specsDir, { withFileTypes: true });
  const featureIds = entries
    .filter(entry => entry.isDirectory() && !entry.name.startsWith('.'))
    .map(entry => entry.name);

  // 检查是否只有一个 Feature 目录（可能是当前的 feature 或示例）
  // 如果有 2 个或更多的 Feature 目录，则为 multi 模式
  return featureIds.length > 1 ? 'multi' : 'single';
}

/**
 * 初始化子 Feature 目录结构
 * 在 .sdd/.specs/ 目录下创建子 Feature 文件夹
 */
export async function initSubFeature(featureId: string, baseDir?: string): Promise<void> {
  const specsDir = getSpecsDir();
  const subFeatureDir = join(specsDir, featureId);
  
  if (!existsSync(subFeatureDir)) {
    mkdirSync(subFeatureDir, { recursive: true });
  }
  
  // 创建基本的 state.json 文件
  const initialState: FeatureState = {
    feature: featureId,
    name: `Sub Feature ${featureId}`,
    version: '1.2.11',
    status: 'initiated',
    assignee: '',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  };
  
  await saveState(featureId, initialState);
}

/**
 * 生成子 Feature 索引表
 * 扫描同级目录并生成 Markdown 表格
 */
export async function generateSubFeatureIndex(baseFeatureId: string): Promise<string> {
  const specsDir = getSpecsDir();
  if (!existsSync(specsDir)) {
    return '';
  }

  const entries = readdirSync(specsDir, { withFileTypes: true });
  const subFeatureEntries = entries
    .filter(entry => entry.isDirectory() && !entry.name.startsWith('.'))
    .map(entry => entry.name);

  if (subFeatureEntries.length <= 1) {
    // 如果没有子 Feature 或只有自己，则返回空表
    return '';
  }

  let table = '\n| 子 Feature ID | 子 Feature 名称 | 目录路径 | 状态 | 负责人 | 阻塞依赖 |\n';
  table += '|---------------|-----------------|----------|------|--------|----------|\n';

  for (const subFeatureId of subFeatureEntries) {
    // 尝试从 state.json 获取信息
    try {
      const state = await loadState(subFeatureId);
      
      // 跳过主 Feature（即本身）
      if (state.feature === baseFeatureId) {
        continue;
      }
      
      const blocking = state.dependencies?.blocking?.join(', ') || '-';
      table += `| ${state.feature} | ${state.name || '-'} | ${subFeatureId} | ${state.status} | ${state.assignee || '-'} | ${blocking} |\n`;
    } catch (error) {
      // 如果无法加载，使用默认值
      table += `| ${subFeatureId} | - | ${subFeatureId} | - | - | - |\n`;
    }
  }

  return table;
}

/**
 * 验证子 Feature 文档完整性
 */
export async function validateSubFeatureDocuments(featureId: string): Promise<{ valid: boolean; issues: string[] }> {
  const featureDir = getFeatureDir(featureId);
  const issues: string[] = [];

  // 检查必需文件是否存在
  const requiredFiles = ['state.json', 'spec.md', 'plan.md', 'tasks.md'];
  
  for (const file of requiredFiles) {
    const filePath = join(featureDir, file);
    if (!existsSync(filePath)) {
      issues.push(`Missing required file: ${file}`);
    }
  }

  // 如果存在问题，则无效
  return { valid: issues.length === 0, issues };
}

/**
 * 创建完整的多子 Feature 结构
 */
export async function createMultiFeatureStructure(mainFeatureId: string, subFeatureIds: string[]): Promise<void> {
  // 初始化主 Feature 结构
  await initSubFeature(mainFeatureId);
  
  // 初始化所有子 Feature
  for (const subFeatureId of subFeatureIds) {
    await initSubFeature(subFeatureId);
  }
}

/**
 * 从同级目录获取所有子 Feature 状态
 */
export async function getAllSubFeatureStates(baseFeatureId: string): Promise<Record<string, FeatureState>> {
  const specsDir = getSpecsDir();
  const states: Record<string, FeatureState> = {};

  if (!existsSync(specsDir)) {
    return {};
  }

  const entries = readdirSync(specsDir, { withFileTypes: true });
  
  for (const entry of entries) {
    if (entry.isDirectory() && !entry.name.startsWith('.') && entry.name !== baseFeatureId) {
      try {
        const statePath = join(specsDir, entry.name, 'state.json');
        if (existsSync(statePath)) {
          const stateContent = readFileSync(statePath, 'utf-8');
          const state: FeatureState = JSON.parse(stateContent);
          states[entry.name] = state;
        }
      } catch (error) {
        console.warn(`Failed to load state for ${entry.name}:`, error);
      }
    }
  }

  return states;
}