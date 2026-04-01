// .sdd/tests/e2e/e2e-test.fixture.ts
import { FeatureState } from '../../src/state/schema-v1.2.11';

// Example feature states used in end-to-end tests
export const multiFeatureTestFixture = {
  'main-feature': {
    feature: 'main-feature',
    name: 'Main Feature',
    status: 'initiated',
    phase: 1,
    version: '1.2.11',
    files: {
      spec: 'spec.md',
      plan: 'plan.md',
      tasks: 'tasks.md'
    },
    dependencies: {
      on: [],
      blocking: []
    },
    assignee: 'Project Lead',
    createdAt: '2026-03-31T09:00:00Z',
    updatedAt: '2026-03-31T09:00:00Z'
  } as FeatureState,
  
  'sub-feature-user': {
    feature: 'user-management',
    name: 'User Management',
    status: 'specified',
    version: '1.2.11',
    dependencies: {
      on: [],
      blocking: []
    },
    assignee: 'Alice',
    createdAt: '2026-03-31T09:15:00Z',
    updatedAt: '2026-03-31T09:15:00Z'
  } as FeatureState,
  
  'sub-feature-order': {
    feature: 'order-processing',
    name: 'Order Processing',
    status: 'planned',
    dependencies: {
      on: ['user-management'],  // depends on user management
      blocking: []
    },
    phase: 2,
    version: '1.2.11',
    assignee: 'Bob',
    createdAt: '2026-03-31T09:20:00Z',
    updatedAt: '2026-03-31T09:20:00Z'
  } as FeatureState
};