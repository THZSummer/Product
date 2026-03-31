// .sdd/src/utils/workspace.ts
import { existsSync } from 'fs';
import { join } from 'path';

/**
 * 获取 SDD 工作空间根目录
 * 优先级：环境变量 > .sdd/ 目录 > .specs/ 目录
 */
export function getSDDWorkspace(): string {
  // 1. 检查环境变量
  if (process.env.SDD_WORKSPACE) {
    return process.env.SDD_WORKSPACE;
  }
  
  // 2. 优先查找 .sdd/ 目录（容器模式）
  if (existsSync('.sdd')) {
    return '.sdd';
  }
  
  // 3. 回退到 .specs/ 目录（兼容模式）
  if (existsSync('.specs')) {
    return '.';
  }
  
  throw new Error('未找到 SDD 工作空间：请确保存在 .sdd/ 或 .specs/ 目录');
}

/**
 * 获取 specs 目录路径
 */
export function getSpecsDir(): string {
  const workspace = getSDDWorkspace();
  return workspace === '.sdd' ? '.sdd/.specs' : '.specs';
}

/**
 * 获取给定 feature 的目录路径
 */
export function getFeatureDir(featureId: string): string {
  const specsDir = getSpecsDir();
  return join(specsDir, featureId);
}

/**
 * 获取给定 feature 的状态文件路径
 */
export function getStateFilePath(featureId: string): string {
  return join(getFeatureDir(featureId), 'state.json');
}