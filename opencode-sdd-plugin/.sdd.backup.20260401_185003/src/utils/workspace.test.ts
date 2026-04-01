// .sdd/src/utils/workspace.test.ts
import { getSDDWorkspace, getSpecsDir, getFeatureDir, getStateFilePath } from './workspace';
import { existsSync } from 'fs';

// Mock fs.existsSync to control our tests
jest.mock('fs', () => ({
  existsSync: jest.fn(),
  readFileSync: jest.fn(),
}));

const mockedExistsSync = existsSync as jest.MockedFunction<typeof existsSync>;

describe('Workspace Utils', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // 清除可能存在的环境变量影响
    delete process.env.SDD_WORKSPACE;
  });

  afterEach(() => {
    delete process.env.SDD_WORKSPACE;
  });

  test('should return env var when SDD_WORKSPACE is set', () => {
    process.env.SDD_WORKSPACE = '/custom/path';
    
    const result = getSDDWorkspace();
    
    expect(result).toBe('/custom/path');
    // Should not call existsSync when env var is set
    expect(existsSync).not.toHaveBeenCalled();
  });

  test('should return .sdd when .sdd directory exists', () => {
    mockedExistsSync.mockReturnValueOnce(true).mockReturnValueOnce(false);
    
    const result = getSDDWorkspace();
    
    expect(result).toBe('.sdd');
  });

  test('should return . when only .specs directory exists', () => {
    mockedExistsSync.mockReturnValueOnce(false).mockReturnValueOnce(true);
    
    const result = getSDDWorkspace();
    
    expect(result).toBe('.');
  });

  test('should throw error when no workspace is found', () => {
    mockedExistsSync.mockReturnValue(false);
    
    expect(() => getSDDWorkspace()).toThrow('未找到 SDD 工作空间：请确保存在 .sdd/ 或 specs-tree-root/ 或 .specs/ 目录');
  });

  test('should handle .sdd workspace correctly in getSpecsDir', () => {
    // Mock existsSync to make it think .sdd exists but not the others for getSDDWorkspace
    mockedExistsSync.mockImplementation((inputPath: any) => {
      const pathStr = typeof inputPath === 'string' ? inputPath : inputPath.toString();
      return pathStr === '.sdd'; // Only .sdd exists to trigger the first condition
    });
    
    // Directly call our functions
    expect(getSDDWorkspace()).toBe('.sdd');
    // Now the getSpecsDir should return .sdd/.specs when workspace is .sdd
    expect(getSpecsDir()).toBe('.sdd/.specs');
  });

  test('should return specs-tree-root when in legacy mode and it exists', () => {
    mockedExistsSync.mockImplementation((inputPath: any) => {
      const pathStr = typeof inputPath === 'string' ? inputPath : inputPath.toString();
      return pathStr === 'specs-tree-root'; // Only specs-tree-root exists in this case
    });
    
    expect(getSDDWorkspace()).toBe('.');
    expect(getSpecsDir()).toBe('specs-tree-root');
  });

  test('should create correct feature directory path', () => {
    // Test the combination: assume SDD_WORKSPACE and specs dir structure exist
    mockedExistsSync.mockImplementation((inputPath: any) => {
      const pathStr = typeof inputPath === 'string' ? inputPath : inputPath.toString();
      return pathStr.includes('specs-tree-root'); // Make specs-tree-root exist as the primary
    });
    
    // If specs-tree-root exists, getSpecsDir should return 'specs-tree-root'
    expect(getSpecsDir()).toBe('specs-tree-root'); // Assuming getSDDWorkspace returns '.'
    // And then getFeatureDir should combine them correctly  
    expect(getFeatureDir('test-feature')).toBe('specs-tree-root/test-feature');
  });

  test('should create correct state file path', () => {
    mockedExistsSync.mockImplementation((inputPath: any) => {
      const pathStr = typeof inputPath === 'string' ? inputPath : inputPath.toString();
      return pathStr.includes('specs-tree-root');
    });
    
    // If workspace returns '.' and specs-tree-root exists
    const result = getStateFilePath('test-feature');
    expect(result).toBe('specs-tree-root/test-feature/state.json');
  });
});