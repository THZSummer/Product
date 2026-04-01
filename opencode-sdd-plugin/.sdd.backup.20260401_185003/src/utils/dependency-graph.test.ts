// .sdd/src/utils/dependency-graph.test.ts
import { 
  detectCircularDependency, 
  validateDependencyGraph, 
  buildDependencyGraph 
} from './dependency-graph';

describe('Dependency Graph', () => {
  test('检测简单循环 A→B→A', () => {
    const deps = {
      'A': ['B'],
      'B': ['A']
    };
    const result = validateDependencyGraph(deps);
    expect(result.valid).toBe(false);
    expect(result.cycle).toBeDefined();
    expect(result.message).toContain('A → B → A');
  });
  
  test('检测复杂循环 A→B→C→A', () => {
    const deps = {
      'A': ['B'],
      'B': ['C'],
      'C': ['A']
    };
    const result = validateDependencyGraph(deps);
    expect(result.valid).toBe(false);
    expect(result.cycle).toEqual(['A', 'B', 'C', 'A']);
  });
  
  test('检测复杂循环 A→B→C→D→B', () => {
    const deps = {
      'A': ['B'],
      'B': ['C'],
      'C': ['D'],
      'D': ['B'] // D -> B -> C -> D -> B creates a shorter loop B -> C -> D -> B
    };
    const result = validateDependencyGraph(deps);
    expect(result.valid).toBe(false);
    expect(result.cycle).toContain('B'); 
    expect(result.cycle).toContain('C'); 
    expect(result.cycle).toContain('D'); 
  });
  
  test('无循环依赖', () => {
    const deps = {
      'A': [],
      'B': ['A'],
      'C': ['A', 'B']
    };
    const result = validateDependencyGraph(deps);
    expect(result.valid).toBe(true);
    expect(result.cycle).toBeUndefined();
  });
  
  test('复杂无循环场景', () => {
    const deps = {
      'user-auth': ['user-model'],
      'order-service': ['user-auth', 'inventory'],
      'payment': ['order-service'],
      'notification': ['order-service'],
      'user-model': []
    };
    const result = validateDependencyGraph(deps);
    expect(result.valid).toBe(true);
  });
  
  test('单节点无自依赖', () => {
    const deps = {
      'single-task': []
    };
    const result = validateDependencyGraph(deps);
    expect(result.valid).toBe(true);
  });
  
  test('detectCircularDependency with single node', () => {
    const deps = {
      'A': ['A'] // Self-loop
    };
    const result = detectCircularDependency('A', deps);
    expect(result).toEqual(['A', 'A']);
  });
  
  test('buildDependencyGraph works correctly', () => {
    const subFeatures = [
      { id: 'auth', dependencies: ['model'] },
      { id: 'model', dependencies: [] },
      { id: 'api', dependencies: ['auth'] }
    ];
    
    const expected = {
      'auth': ['model'],
      'model': [],
      'api': ['auth']
    };
    
    const result = buildDependencyGraph(subFeatures);
    expect(result).toEqual(expected);
  });
  
  test('validateDependencyGraph with disconnected nodes', () => {
    const deps = {
      'A': ['B'],
      'B': [],
      'C': ['D'], // Independent graph
      'D': []
    };
    const result = validateDependencyGraph(deps); 
    expect(result.valid).toBe(true);
  });
});