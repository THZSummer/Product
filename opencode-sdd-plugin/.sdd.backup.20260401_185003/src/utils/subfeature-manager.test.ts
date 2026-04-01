// .sdd/src/utils/subfeature-manager.test.ts
import { detectFeatureMode, initSubFeature, generateSubFeatureIndex, validateSubFeatureDocuments, createMultiFeatureStructure, getAllSubFeatureStates } from './subfeature-manager';
import { FeatureState } from '../state/schema-v1.2.11';
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

describe('SubFeature Manager', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('should detect single mode when only one feature directory exists', async () => {
    // Mock to simulate that workspace can find specs-tree-root directory
    mockedExistsSync.mockImplementation((inputPath: any) => {
      const pathStr = typeof inputPath === 'string' ? inputPath : inputPath.toString();
      if (pathStr === 'specs-tree-root') {
        return true; // specs directory exists
      }
      // Simulate only one feature directory in specs-tree-root
      if (pathStr === 'specs-tree-root/test-feature') {
        return true;
      }
      // Mock subdir listing returns a single feature
      if (pathStr.includes('specs-tree-root') && pathStr !== 'specs-tree-root') {
        return false;
      }
      return false;
    });

    const result = await detectFeatureMode('test-feature');
    expect(result).toBe('single');
  });

  test('should initialize sub feature with proper structure', async () => {
    // Mock existsSync to make getSpecsDir() work appropriately with new path
    mockedExistsSync.mockImplementation((inputPath: any) => {
      const pathStr = typeof inputPath === 'string' ? inputPath : inputPath.toString();
      // Enable specs-tree-root directory
      return pathStr === 'specs-tree-root';
    });
    
    await initSubFeature('new-sub-feature');
    
    // Since initSubFeature calls mkdirSync for the new feature directory
    expect(mockedMkdirSync).toHaveBeenCalledWith(expect.any(String), { recursive: true });
    expect(mockedWriteFileSync).toHaveBeenCalled();
  });

  test('should validate sub feature documents', async () => {
    // Mock existsSync to make getFeatureDir() return correct path and make files exist
    mockedExistsSync.mockImplementation((inputPath: any) => {
      const pathStr = typeof inputPath === 'string' ? inputPath : inputPath.toString();
      // For validating sub feature files
      if (pathStr.includes('new-sub-feature') && (pathStr.endsWith('state.json') || 
          pathStr.endsWith('spec.md') || pathStr.endsWith('plan.md') || pathStr.endsWith('tasks.md'))) {
        return true;
      }
      // For workspace detection
      if (pathStr === 'specs-tree-root') {
        return true;
      }
      return false;
    });

    const result = await validateSubFeatureDocuments('new-sub-feature');
    
    expect(result.valid).toBe(true);
  });

  test('should create multi feature structure correctly', async () => {
    // Set up mocks for proper directory creation simulation
    mockedExistsSync.mockImplementation((inputPath: any) => {
      const pathStr = typeof inputPath === 'string' ? inputPath : inputPath.toString();
      // Allow workspace check to pass
      return pathStr === 'specs-tree-root';
    });
    
    await createMultiFeatureStructure('main-feature', ['sub1', 'sub2']);
    
    // Should call mkdirSync 6 times: each initSubFeature creates a directory and saves state, 
    // which also creates directory for saving state files - 3 features * 2 mkdir calls each = 6
    expect(mockedMkdirSync).toHaveBeenCalledTimes(6);
  });
});