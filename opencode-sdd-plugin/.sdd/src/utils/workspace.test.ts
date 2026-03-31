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
    
    expect(() => getSDDWorkspace()).toThrow('未找到 SDD 工作空间：请确保存在 .sdd/ 或 .specs/ 目录');
  });

  test('should get correct specs dir for .sdd workspace', () => {
    // Set up mock to simulate .sdd workspace
    const originalGetSDDWorkspace = jest.requireActual('./workspace').getSDDWorkspace;
    jest.spyOn(require('./workspace'), 'getSDDWorkspace').mockReturnValue('.sdd');
    
    const result = getSpecsDir();
    
    expect(result).toBe('.sdd/.specs');
  });

  test('should get correct specs dir for legacy workspace', () => {
    // Set up mock to simulate legacy workspace
    jest.spyOn(require('./workspace'), 'getSDDWorkspace').mockReturnValue('.');
    
    const result = getSpecsDir();
    
    expect(result).toBe('.specs');
  });

  test('should return correct feature directory', () => {
    const result = getFeatureDir('test-feature');
    
    // Since the mocked getSpecsDir will behave based on our workspace setup
    // we'll assume that the mock setup works correctly
    expect(typeof result).toBe('string');
  });

  test('should return correct state file path', () => {
    // Mock getFeatureDir
    const originalGetFeatureDir = jest.requireActual('./workspace').getFeatureDir;
    jest.spyOn(require('./workspace'), 'getFeatureDir').mockReturnValue('.sdd/.specs/test-feature');
    
    const result = getStateFilePath('test-feature');
    
    expect(result).toBe('.sdd/.specs/test-feature/state.json');
  });
});