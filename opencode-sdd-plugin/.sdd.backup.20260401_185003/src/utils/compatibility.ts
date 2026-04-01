// .sdd/src/utils/compatibility.ts

/**
 * 检测是否为旧格式 state.json（v1.1.1 或更早）
 */
export function isLegacyState(state: any): boolean {
  // 检查是否有版本字段并且版本小于1.2.11
  if (state.version && state.version !== '1.2.11') {
    return true;
  }
  // 检测是否有旧字段（如 mode、subFeatures）而没有新字段
  if (state.mode !== undefined || state.subFeatures !== undefined) {
    return true;
  }
  // 没有版本的情况下默认为旧格式
  return !state.version;
}

/**
 * 检测是否有旧的 .specs/ 结构（而非 .sdd/.specs/)
 */
export function hasLegacySpecsStructure(): boolean {
  const { accessSync, constants } = require('fs');
  const path = require('path');
  
  // 检查当前目录下是否存在 .specs/ 但没有 .sdd/ 目录
  try {
    accessSync('.specs', constants.F_OK);
    try {
      accessSync('.sdd', constants.F_OK);
      // Both exist, might be in transition phase
      return false;
    } catch {
      // Only .specs exists
      return true;
    }
  } catch {
    // No .specs directory
    return false;
  }
}

/**
 * 获取迁移建议消息
 */
export function getMigrationSuggestion(): string {
  if (hasLegacySpecsStructure()) {
    return '检测到旧版 .specs/ 结构，建议迁移到 .sdd/.specs/ 容器化结构';
  }
  return '';
}

/**
 * 将旧格式状态迁移为新格式
 */
export function migrateStateFromLegacy(oldState: any): any {
  // 从旧状态复制所有字段，但排除旧字段并添加新版本
  const { mode, subFeatures, ...newState } = oldState;
  
  // 确保设置新的版本号
  return {
    ...newState,
    version: '1.2.11',
    updatedAt: newState.updatedAt || new Date().toISOString()
  };
}