// .sdd/src/utils/dependency-notifier.ts
import { FeatureState, FeatureStatus } from '../state/schema-v1.2.11';
import { getAllSubFeatureStates } from './subfeature-manager';
import { scanSubFeatures } from '../state/manager';

/**
 * 检查子 Feature 是否所有依赖已完成
 */
export function isDependencyReady(
  targetSubFeatureId: string,
  allSubFeatureStates: Record<string, FeatureState>,
  subFeatureDependencies: Record<string, string[]>
): boolean {
  // 获取当前子 Feature 的依赖
  const dependencies = subFeatureDependencies[targetSubFeatureId] || [];
  
  if (dependencies.length === 0) return true;
  
  // 检查所有依赖子 Feature 是否状态足够高级(至少完成specified阶段)
  return dependencies.every(depId => {
    const depState = allSubFeatureStates[depId];
    return depState && isSufficientlyAdvanced(depState.status);
  });
}

/**
 * 判断状态是否已达足够的进展水平
 * 在 SDD 工作流中，一般当一个任务达到 'planned' 状态时，就可以认为它对依赖项的阻塞已解除
 */
function isSufficientlyAdvanced(status: FeatureStatus): boolean {
  const advancedStages: FeatureStatus[] = [
    'planned', 'tasked', 'implementing', 'reviewing', 'validated', 'completed', 'archived'
  ];
  return advancedStages.includes(status);
}

/**
 * 检测指定子 Feature 的依赖变更
 * 当 subFeatureId 的状态更新时，检查哪些其他子 Feature 可能因此变为就绪状态
 */
export async function notifyDependentFeatures(
  completedSubFeatureId: string,
  currentFeaturePath: string
): Promise<void> {
  // 获取所有相关子 Feature 的状态
  const allSubFeatureStates = await getAllSubFeatureStates(currentFeaturePath);
  
  // 构建反向依赖映射：childFeature -> [parents that depend on child]
  const dependentSubFeaturesMap: Record<string, string[]> = {};
  
  // 遍历所有子 Feature 的依赖关系
  for (const [subFeatureId, state] of Object.entries(allSubFeatureStates)) {
    const dependencies = state.dependencies?.on || [];
    for (const dependency of dependencies) {
      if (!dependentSubFeaturesMap[dependency]) {
        dependentSubFeaturesMap[dependency] = [];
      }
      dependentSubFeaturesMap[dependency].push(subFeatureId);
    }
  }
  
  // 看看是否有依赖于已完成子 Feature 的其他 Feature
  const directlyDependentFeatures = dependentSubFeaturesMap[completedSubFeatureId] || [];
  
  // 检查每个依赖于 completedSubFeatureId 的子 Feature 是否现在就绪
  for (const dependentFeatureId of directlyDependentFeatures) {
    if (isDependencyReady(dependentFeatureId, allSubFeatureStates, extractAllDependencies(allSubFeatureStates))) {
      // 只在状态还不是 advanced 时才通知，避免重复通知
      if (!isSufficientlyAdvanced(allSubFeatureStates[dependentFeatureId].status)) {
        console.log(`📢 子 Feature "${dependentFeatureId}" 的依赖已就绪 (${completedSubFeatureId} 已完成)，可以开始开发`);
        
        // 调用回调或进一步操作（在这里可以扩展功能）
        triggerFeatureReadyNotification(dependentFeatureId);
      }
    }
  }
}

/**
 * 提取所有子 Feature 的依赖关系映射
 */
function extractAllDependencies(allSubFeatureStates: Record<string, FeatureState>): Record<string, string[]> {
  const deps: Record<string, string[]> = {};
  
  for (const [subFeatureId, state] of Object.entries(allSubFeatureStates)) {
    deps[subFeatureId] = state.dependencies?.on || [];
  }
  
  return deps;
}

/**
 * 触发对指定已就绪子 Feature 的通知
 */
function triggerFeatureReadyNotification(featureId: string): void {
  // 在这里可以添加额外的通知逻辑：
  // - 发送通知消息
  // - 更新 UI
  // - 触发自动化脚本
  // - 等等
  console.log(`✅ 通知：${featureId} 可以继续开发了`);
}

/**
 * 检查整个子 Feature 组的状态是否满足某些约束
 */
export function checkOverallProgress(subFeatureStates: FeatureState[]): boolean {
  // 检查是否有严重的阻塞情况
  const blockedCount = subFeatureStates.filter(
    sf => sf.dependencies?.blocking?.length && sf.status === 'planned'
  ).length;
  
  // 如果超过一半的子 Feature 都被阻塞，则发出警告
  if (blockedCount > subFeatureStates.length / 2) {
    console.warn(`⚠️  警告：${blockedCount}/${subFeatureStates.length} 个子 Feature 被阻塞，可能存在规划问题`);
    return false;
  }
  
  return true;
}