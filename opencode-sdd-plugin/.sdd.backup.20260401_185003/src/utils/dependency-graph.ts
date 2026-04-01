// .sdd/src/utils/dependency-graph.ts

/**
 * 依赖图工具 - 用于检测循环依赖
 */

export interface DependencyValidationResult {
  valid: boolean;
  cycle?: string[];
  message?: string;
}

/**
 * 循环依赖检测 (DFS)
 */
export function detectCircularDependency(
  subFeatureId: string,
  dependencies: Record<string, string[]>,
  visited: Set<string> = new Set(),
  path: string[] = []
): string[] | null {
  // 如果当前节点已在路径中，说明检测到循环
  if (path.includes(subFeatureId)) {
    const cycleStart = path.indexOf(subFeatureId);
    return [...path.slice(cycleStart), subFeatureId];
  }
  
  // 如果已访问过，跳过
  if (visited.has(subFeatureId)) {
    return null;
  }
  
  // 标记为已访问并加入路径
  visited.add(subFeatureId);
  path.push(subFeatureId);
  
  // 递归检查所有依赖
  const deps = dependencies[subFeatureId] || [];
  for (const dep of deps) {
    const cycle = detectCircularDependency(dep, dependencies, visited, [...path]);
    if (cycle) {
      return cycle;
    }
  }
  
  return null;
}

/**
 * 验证依赖图是否有循环
 */
export function validateDependencyGraph(
  dependencies: Record<string, string[]>
): DependencyValidationResult {
  const visited = new Set<string>();
  
  // 对所有节点进行循环检测
  for (const node of Object.keys(dependencies)) {
    if (!visited.has(node)) {
      const cycle = detectCircularDependency(node, dependencies, visited, []);
      if (cycle) {
        return {
          valid: false,
          cycle,
          message: `检测到循环依赖：${cycle.join(' → ')}`
        };
      }
    }
  }
  
  return { valid: true };
}

/**
 * 构建依赖关系图
 */
export function buildDependencyGraph(
  subFeatures: Array<{ id: string; dependencies?: string[] }>
): Record<string, string[]> {
  const graph: Record<string, string[]> = {};
  
  for (const sf of subFeatures) {
    graph[sf.id] = sf.dependencies || [];
  }
  
  return graph;
}