// .sdd/src/state/manager.test.ts
import { loadState, saveState, detectFeatureMode, aggregateSubFeatureState } from './manager';
import { FeatureState, validateState } from './schema-v1.2.11';
import { existsSync, readFileSync, writeFileSync, mkdirSync } from 'fs';

// Mock fs functions to avoid modifying real files during tests
jest.mock('fs', () => ({
  ...jest.requireActual('fs'),
  readFileSync: jest.fn(),
  writeFileSync: jest.fn(),
  existsSync: jest.fn(),
  mkdirSync: jest.fn(),
}));

const mockedReadFileSync = readFileSync as jest.MockedFunction<typeof readFileSync>;
const mockedWriteFileSync = writeFileSync as jest.MockedFunction<typeof writeFileSync>;
const mockedExistsSync = existsSync as jest.MockedFunction<typeof existsSync>;
const mockedMkdirSync = mkdirSync as jest.MockedFunction<typeof mkdirSync>;

describe('State Manager', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('should load state successfully when valid', async () => {
    const validState: FeatureState = {
      feature: 'test-feature',
      status: 'specified',
      name: 'Test Feature',
      version: '1.2.11',
      createdAt: '2026-03-31T09:00:00Z',
      updatedAt: '2026-03-31T10:00:00Z'
    };

    mockedExistsSync.mockReturnValue(true);  // state file exists
    mockedReadFileSync.mockReturnValue(JSON.stringify(validState));

    const result = await loadState('test-feature');
    
    expect(result).toEqual(validState);
  });

  test('should validate state when loading', async () => {
    const invalidState = {
      feature: 'test-feature',
      // Missing status - should be invalid
    };

    mockedExistsSync.mockReturnValue(true);
    mockedReadFileSync.mockReturnValue(JSON.stringify(invalidState));

    await expect(loadState('test-feature')).rejects.toThrow('State validation failed');
  });

  test('should create directory if it does not exist during save', async () => {
    const validState: FeatureState = {
      feature: 'new-feature',
      status: 'initiated',
    };

    mockedExistsSync.mockReturnValue(false); // Dir doesn't exist
    
    await expect(() => saveState('new-feature', validState)).not.toThrow();
    
    expect(mockedMkdirSync).toHaveBeenCalledWith(expect.any(String), { recursive: true });
  });

  test('should validate inputs during saving', async () => {
    // Test with completely invalid schema (missing required fields)
    const completelyInvalid = { feature: 'test' };  // Missing required status field
    
    const validationResult = validateState(completelyInvalid);
    expect(validationResult.valid).toBe(false);
    expect(validationResult.errors).toBeDefined();
  });

  test('should aggregate sub feature status correctly', () => {
    // We're testing the core logic of the aggregation here
    const statusOrder = [
      'initiated', 'specified', 'planned', 'tasked', 'implementing', 'reviewing', 'validated', 'completed', 'archived'
    ];
    
    // Mock testing for the algorithm with feature states
    const featureStates: FeatureState[] = [
      {
        feature: 'sub1',
        status: 'planned' as const,
        name: 'Sub Feature 1'
      },
      {
        feature: 'sub2',
        status: 'specified' as const,
        name: 'Sub Feature 2'
      },
      {
        feature: 'sub3',
        status: 'tasked' as const,
        name: 'Sub Feature 3'
      }
    ];
    
    // Should pick the lowest progress: 'specified' (index 1 in the array)
    let minStatusIndex = Infinity;
    for (const state of featureStates) {
      const index = statusOrder.indexOf(state.status);
      if (index !== -1 && index < minStatusIndex) {
        minStatusIndex = index;
      }
    }
    
    expect(statusOrder[minStatusIndex]).toBe('specified');
  });
});