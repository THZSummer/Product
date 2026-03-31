// .sdd/src/utils/compatibility.test.ts
import { isLegacyState, hasLegacySpecsStructure, getMigrationSuggestion, migrateStateFromLegacy } from './compatibility';

// Mock fs module as needed
jest.mock('fs', () => ({
  accessSync: jest.fn(),
  constants: { F_OK: 0 },
}));

describe('Compatibility utils', () => {
  test('should detect legacy state with version < 1.2.11', () => {
    const legacyState = {
      feature: 'test-feature',
      status: 'planned',
      version: '1.1.1',
    };

    const result = isLegacyState(legacyState);
    expect(result).toBe(true);
  });

  test('should detect legacy state with old fields', () => {
    const legacyState = {
      feature: 'test-feature',
      status: 'planned',
      mode: 'single', // This is an old field
    };

    const result = isLegacyState(legacyState);
    expect(result).toBe(true);
  });

  test('should detect new state format correctly', () => {
    const newState = {
      feature: 'test-feature',
      status: 'planned',
      version: '1.2.11',
    };

    const result = isLegacyState(newState);
    expect(result).toBe(false);
  });

  test('should migrate legacy state properly', () => {
    const legacyState = {
      feature: 'test-feature',
      status: 'planned',
      mode: 'single',
      subFeatures: ['sub1', 'sub2'],
      createdAt: '2026-03-31T00:00:00Z'
    };

    const migrated = migrateStateFromLegacy(legacyState);

    expect(migrated.version).toBe('1.2.11');
    expect(migrated.mode).toBeUndefined();
    expect(migrated.subFeatures).toBeUndefined();
    expect(migrated.feature).toBe('test-feature');
    expect(migrated.status).toBe('planned');
  });

  test('should detect non-legacy state when no old fields exist', () => {
    const modernState = {
      feature: 'test-feature',
      status: 'planned',
      version: '1.2.11',
    };

    const result = isLegacyState(modernState);
    expect(result).toBe(false);
  });
});