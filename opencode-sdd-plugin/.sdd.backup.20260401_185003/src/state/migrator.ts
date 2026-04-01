// .sdd/src/state/migrator.ts
import { FeatureState } from './schema-v1.2.11';
import { existsSync, readFileSync, writeFileSync, mkdirSync } from 'fs';
import { join, dirname } from 'path';

/**
 * 迁移状态 - 将旧版本状态迁移到 v1.2.11
 * @param oldState - 旧版本状态对象
 * @returns 迁移到 v1.2.11 的状态对象
 */
export async function migrateState(oldState: any): Promise<FeatureState> {
  // 1. 检测版本并执行相应的迁移
  if (!oldState.version || oldState.version === '1.1.1' || oldState.version < '1.2.11') {
    return migrateFromPre1211(oldState);
  }

  // 如果已经是最新版本，直接验证状态并返回
  return oldState as FeatureState;
}

/**
 * 迁移从 v1.1.1 或更早版本 (移除 old fields, 更新为新 schema)
 * @param oldState 
 * @returns 迁移到 v1.2.11 的状态对象
 */
function migrateFromPre1211(oldState: any): FeatureState {
  const { mode, subFeatures, ...rest } = oldState;
  
  // 新的状态对象，符合 v1.2.11 的 schema
  const newState: FeatureState = {
    ...rest,
    version: '1.2.11',  // 设置为新版本
    updatedAt: new Date().toISOString()  // 更新时间戳
  };

  // 如果没有创建时间，也设置一个
  if (!newState.createdAt) {
    newState.createdAt = new Date().toISOString();
  }
  
  return newState;
}

/**
 * 保存原始状态作为备份
 * 在 .sdd/.backups/ 目录下创建带有时间戳的备份
 */
export async function backupState(originalState: any, featureId: string): Promise<string> {
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-').replace(/T/, '_').split('.')[0];
  const backupFileName = `state-${featureId}-${timestamp}.json`;
  const backupDir = '.sdd/.backups';
  const backupPath = join(backupDir, backupFileName);

  // 确保备份目录存在
  if (!existsSync(backupDir)) {
    mkdirSync(backupDir, { recursive: true });
  }

  // 写入备份文件
  writeFileSync(backupPath, JSON.stringify(originalState, null, 2));
  
  return backupPath;
}

/**
 * 将旧的 .specs/ 目录下的状态迁移至 .sdd/.specs/
 * 这是一个高级迁移函数，用于将整个项目从旧模式迁移到新模式
 */
export async function migrateEntireFeature(featurePath: string): Promise<boolean> {
  try {
    const oldPath = join('.specs', featurePath, 'state.json');
    const newPath = join('.sdd', '.specs', featurePath, 'state.json');

    // 检查旧的 state.json 是否存在
    if (!existsSync(oldPath)) {
      console.log(`旧的状态文件不存在: ${oldPath}`);
      return false;
    }

    // 确保新目录结构存在
    const newDir = dirname(newPath);
    if (!existsSync(newDir)) {
      mkdirSync(newDir, { recursive: true });
    }

    // 读取旧状态
    const oldStateContent = readFileSync(oldPath, 'utf-8');
    const oldState = JSON.parse(oldStateContent);

    // 进行备份
    await backupState(oldState, featurePath);

    // 迁移状态
    const migratedState = await migrateState(oldState);

    // 保存到新的位置
    writeFileSync(newPath, JSON.stringify(migratedState, null, 2));

    console.log(`成功迁移 ${featurePath} 状态文件到新结构`);
    return true;
  } catch (error) {
    console.error(`迁移功能 ${featurePath} 时发生错误:`, error);
    return false;
  }
}