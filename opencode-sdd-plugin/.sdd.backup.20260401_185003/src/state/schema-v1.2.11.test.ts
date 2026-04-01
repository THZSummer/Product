// .sdd/src/state/schema-v1.2.11.test.ts

import { validateState, FeatureState, FeatureStatus } from './schema-v1.2.11';

describe('State Schema v1.2.11 Validator', () => {
  test('should validate a valid state object', () => {
    const validState: FeatureState = {
      feature: 'test-feature',
      status: 'specified',
      name: 'Test Feature',
      version: '1.2.11',
      phase: 1,
      files: {
        spec: 'spec.md',
        plan: 'plan.md',
      },
      dependencies: {
        on: ['other-feature'],
        blocking: ['blocked-feature']
      },
      assignee: 'developer',
      createdAt: '2026-03-31T09:00:00Z',
      updatedAt: '2026-03-31T10:00:00Z'
    };

    const result = validateState(validState);
    expect(result.valid).toBe(true);
    expect(result.errors).toBeUndefined();
  });

  test('should fail validation when missing required fields', () => {
    const invalidState = {
      name: 'Test Feature Without Feature ID'
    };

    const result = validateState(invalidState);
    expect(result.valid).toBe(false);
    expect(result.errors).toBeDefined();
    expect(result.errors!.length).toBeGreaterThan(0);
    expect(result.errors![0].message).toContain('Missing required field: feature');
  });

  test('should fail validation when missing status field', () => {
    const invalidState = {
      feature: 'test-feature'
    };

    const result = validateState(invalidState);
    expect(result.valid).toBe(false);
    expect(result.errors).toBeDefined();
    expect(result.errors![0].message).toContain('Missing required field: status');
  });

  test('should fail validation when status is invalid', () => {
    const invalidState = {
      feature: 'test-feature',
      status: 'invalid-status' as FeatureStatus
    };

    const result = validateState(invalidState);
    expect(result.valid).toBe(false);
    expect(result.errors).toBeDefined();
    expect(result.errors![0].message).toContain('Invalid status');
  });

  test('should fail validation when phase is out of range', () => {
    const invalidState = {
      feature: 'test-feature',
      status: 'specified',
      phase: 7  // Out of range
    };

    const result = validateState(invalidState);
    expect(result.valid).toBe(false);
    expect(result.errors).toBeDefined();
    expect(result.errors![0].message).toContain('Invalid phase');
  });

  test('should fail validation with invalid dependencies structure', () => {
    const invalidState = {
      feature: 'test-feature',
      status: 'specified',
      dependencies: {
        on: 'not-an-array'  // Should be an array
      }
    };

    const result = validateState(invalidState);
    expect(result.valid).toBe(false);
    expect(result.errors).toBeDefined();
    expect(result.errors![0].message).toContain('dependencies.on must be an array');
  });

  test('should accept valid status values', () => {
    const validStatuses: FeatureStatus[] = [
      'initiated', 'specified', 'planned', 'tasked', 'implementing', 
      'reviewing', 'validated', 'completed', 'archived'
    ];

    for (const status of validStatuses) {
      const state: FeatureState = {
        feature: 'test-feature',
        status: status
      };

      const result = validateState(state);
      expect(result.valid).toBe(true);
    }
  });

  test('should validate file configuration correctly', () => {
    const invalidState = {
      feature: 'test-feature',
      status: 'specified',
      files: {
        spec: 123  // Should be string
      }
    };

    const result = validateState(invalidState);
    expect(result.valid).toBe(false);
    expect(result.errors).toBeDefined();
    expect(result.errors![0].message).toContain('files.spec must be a string');
  });

  test('should validate date formats', () => {
    const invalidState = {
      feature: 'test-feature',
      status: 'specified',
      createdAt: 'not-a-date'
    };

    const result = validateState(invalidState);
    expect(result.valid).toBe(false);
    expect(result.errors).toBeDefined();
    expect(result.errors![0].message).toContain('must be a valid ISO date string');
  });

  test('should handle minimal valid state with only required fields', () => {
    const minimalState = {
      feature: 'minimal-feature',
      status: 'specified'
    };

    const result = validateState(minimalState);
    expect(result.valid).toBe(true);
  });
});