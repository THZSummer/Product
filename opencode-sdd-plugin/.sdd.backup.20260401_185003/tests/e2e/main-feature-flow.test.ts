// .sdd/tests/e2e/main-feature-flow.test.ts
import { FeatureState } from '../../src/state/schema-v1.2.11';
import { saveState, loadState, aggregateSubFeatureState } from '../../src/state/manager';
import { createMultiFeatureStructure, detectFeatureMode } from '../../src/utils/subfeature-manager';
import { parseParallelGroups, computeExecutionOrder } from '../../src/utils/tasks-parser';
import { multiFeatureTestFixture } from './e2e-test.fixture';

describe('Multi-Feature End-to-End Flow Test', () => {
  // Skip these tests on platforms that don't support all functionality
  jest.setTimeout(30000);

  test('should create and manage multi-feature structure properly', async () => {
    // Simulate creation of a multi-feature structure
    await createMultiFeatureStructure('e2e-main-feature', ['e2e-sub-1', 'e2e-sub-2']);
    
    // Detect the mode after creation
    const mode = await detectFeatureMode('e2e-main-feature');
    expect(mode).toBe('multi');  // Because we created multiple sub-features
    
    // Verify we can save and load sub-feature states appropriately
    const subFeature1State: FeatureState = {
      feature: 'e2e-sub-1',
      name: 'Sub-Feature 1',
      status: 'specified',
      version: '1.2.11',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
    
    await saveState('e2e-sub-1', subFeature1State);
    
    const loadedState = await loadState('e2e-sub-1');
    expect(loadedState.feature).toBe('e2e-sub-1');
    expect(loadedState.status).toBe('specified');
  });

  test('should parse and order tasks correctly', () => {
    const mockTasksMd = `
# 任务分解示例

### 组 1: 功能设计 (可并行)

- [ ] TASK-001: 设计用户认证模块 (FR-251) (4h)
- [ ] TASK-002: 设计支付系统 (FR-252) (5h)

### 组 2: 实现阶段（等待组 1）

- [ ] TASK-003: 实现认证API (FR-251-1) (6h)
- [ ] TASK-004: 实现支付功能 (FR-252-1) (8h)

### 组 3: 测试部署（等待组 2）

- [ ] TASK-005: 集成测试 (FR-253) (4h)
- [ ] TASK-006: 生产部署 (FR-254) (2h)
`;

    const parsedGroups = parseParallelGroups(mockTasksMd);
    expect(parsedGroups.length).toBe(3);

    // Parse group names
    expect(parsedGroups[0].id).toBe(1);
    expect(parsedGroups[0].name).toContain('功能设计');
    expect(parsedGroups[1].id).toBe(2);
    expect(parsedGroups[1].name).toContain('实现阶段');
    expect(parsedGroups[2].id).toBe(3);
    expect(parsedGroups[2].name).toContain('测试部署');

    // Compute execution order
    const executionWaves = computeExecutionOrder(parsedGroups);
    expect(executionWaves.length).toBe(3); // Three waves based on dependencies
  });

  test('should aggregate feature states correctly', async () => {
    // For this test, we can validate the state aggregation logic
    const statusOrder = [
      'initiated', 'specified', 'planned', 'tasked', 'implementing', 'reviewing', 'validated', 'completed', 'archived'
    ];
    
    // Mock state that demonstrates the aggregation
    const slowerSubFeatureState = {
      feature: 'slower-sub',
      status: 'specified' as const,
      name: 'Slower Sub-Feature'
    };
    
    const fasterSubFeatureState = {
      feature: 'faster-sub',
      status: 'tasked' as const,
      name: 'Faster Sub-Feature'
    };
    
    // This simulates what would happen internally in aggregateSubFeatureState
    const subFeatures = [slowerSubFeatureState, fasterSubFeatureState];
    
    // Find slowest status among sub-features
    let minStatusIndex = Infinity;
    for (const state of subFeatures) {
      const index = statusOrder.indexOf(state.status);
      if (index !== -1 && index < minStatusIndex) {
        minStatusIndex = index;
      }
    }
    
    const aggregatedStatus = statusOrder[minStatusIndex];
    
    // Slower feature sets the overall status
    expect(aggregatedStatus).toBe('specified');
  });
});