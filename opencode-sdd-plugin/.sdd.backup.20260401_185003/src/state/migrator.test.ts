// .sdd/src/state/migrator.test.ts
import { migrateState, backupState, migrateEntireFeature } from './migrator';
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

describe('State Migrator', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('should migrate from v1.1.1 format correctly', async () => {
    const legacyState = {
      feature: 'legacy-feature',
      status: 'planned',
      mode: 'single',
      subFeatures: ['sub1', 'sub2'],
      createdAt: '2026-03-01T00:00:00Z',
      updatedAt: '2026-03-01T00:00:00Z'
    };

    const migrated = await migrateState(legacyState);

    expect(migrated.version).toBe('1.2.11');
    expect(migrated.feature).toBe('legacy-feature');
    expect(migrated.status).toBe('planned');
    // Old fields should be removed
    expect((migrated as any).mode).toBeUndefined();
    expect((migrated as any).subFeatures).toBeUndefined();
  });

  test('should backup state correctly', async () => {
    const originalState = {
      feature: 'test-backup',
      status: 'specified'
    };

    mockedExistsSync.mockReturnValue(false); // Backup dir doesn't exist initially
    
    const backupPath = await backupState(originalState, 'test-backup');

    expect(mockedMkdirSync).toHaveBeenCalled();
    expect(mockedWriteFileSync).toHaveBeenCalledWith(
      expect.stringContaining('state-test-backup-'),
      expect.any(String)
    );
    expect(backupPath).toContain('test-backup');
  });

  test('should keep current version if already 1.2.11', async () => {
    const currentState = {
      feature: 'current-feature',
      status: 'planned',
      version: '1.2.11',
      createdAt: '2026-03-01T00:00:00Z',
      updatedAt: '2026-03-01T00:00:00Z'
    };

    const result = await migrateState(currentState);

    expect(result.version).toBe('1.2.11');
    // The entire state should remain unchanged
    expect(result).toEqual(currentState);
  });

  test('should handle migration without version correctly', async () => {
    const oldStateWithoutVersion = {
      feature: 'old-feature',
      status: 'initiated',
      name: 'Old Feature',
      mode: 'single'
    };

    const result = await migrateState(oldStateWithoutVersion);

    expect(result.version).toBe('1.2.11');
    expect(result.feature).toBe('old-feature');
    expect(result.status).toBe('initiated');
    expect((result as any).mode).toBeUndefined();
  });
});