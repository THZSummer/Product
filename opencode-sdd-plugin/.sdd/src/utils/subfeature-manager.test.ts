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
    // Mock specs directory exists and has only one feature
    mockedExistsSync.mockImplementation((inputPath: any) => {
      const pathStr = typeof inputPath === 'string' ? inputPath : inputPath.toString();
      return pathStr.includes('.specs'); // specs directory exists
    });

    const result = await detectFeatureMode('test-feature');
    expect(result).toBe('single');
  });

  test('should initialize sub feature with proper structure', async () => {
    mockedExistsSync.mockReturnValueOnce(false); // Dir does not exist initially
    
    await initSubFeature('new-sub-feature');
    
    expect(mockedMkdirSync).toHaveBeenCalledWith(expect.any(String), { recursive: true });
    expect(mockedWriteFileSync).toHaveBeenCalled();
  });

  test('should validate sub feature documents', async () => {
    mockedExistsSync.mockReturnValue(true); // All required files exist

    const result = await validateSubFeatureDocuments('test-feature');
    
    expect(result.valid).toBe(true);
  });

  test('should create multi feature structure correctly', async () => {
    mockedExistsSync.mockReturnValue(false); // Directories don't exist
    
    await createMultiFeatureStructure('main-feature', ['sub1', 'sub2']);
    
    expect(mockedMkdirSync).toHaveBeenCalledTimes(3); // For main + 2 sub features
  });
});