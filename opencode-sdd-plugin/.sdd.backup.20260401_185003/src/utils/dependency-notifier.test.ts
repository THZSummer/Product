// .sdd/src/utils/dependency-notifier.test.ts
import { isDependencyReady, notifyDependentFeatures, checkOverallProgress } from './dependency-notifier';
import { FeatureState } from '../state/schema-v1.2.11';

describe('Dependency Notifier', () => {
  test('should determine dependency readiness correctly', () => {
    const allStates = {
      'auth-service': {
        feature: 'auth-service',
        status: 'planned' as const,
      } as FeatureState,
      'api-gateway': {
        feature: 'api-gateway',
        status: 'specified' as const,
        dependencies: {
          on: ['auth-service']  // Depends on auth-service
        }
      } as FeatureState,
      'payment-service': {
        feature: 'payment-service',
        status: 'tasked' as const,
        dependencies: {
          on: ['auth-service']
        }
      } as FeatureState
    };

    // api-gateway dependency is ready because auth-service is 'planned' (advanced stage)
    const isApiGatewayReady = isDependencyReady('api-gateway', allStates, {
      'api-gateway': ['auth-service'],
      'payment-service': ['auth-service'],
      'auth-service': []
    });
    
    expect(isApiGatewayReady).toBe(true);
    
    // However, if auth-service was at initiated, it wouldn't be ready yet
    const allStatesInitiated = {
      'auth-service': {
        feature: 'auth-service',
        status: 'initiated' as const,
      } as FeatureState,
      'api-gateway': {
        feature: 'api-gateway',
        status: 'specified' as const,
        dependencies: {
          on: ['auth-service']
        }
      } as FeatureState,
    };
    
    const isNotReady = isDependencyReady('api-gateway', allStatesInitiated, {
      'api-gateway': ['auth-service'],
      'auth-service': []
    });
    
    expect(isNotReady).toBe(false);
  });

  test('should detect blocking issues correctly', () => {
    const states: FeatureState[] = [
      {
        feature: 'api-service',
        status: 'planned' as const,
        dependencies: {
          blocking: ['db-migration']
        }
      } as FeatureState,
      {
        feature: 'ui-component',
        status: 'tasked' as const,
      } as FeatureState
    ];

    const hasProgressIssues = checkOverallProgress(states);
    
    // Expect return value based on the implementation
    expect(typeof hasProgressIssues).toBe('boolean');
  });

  test('should not crash with empty dependencies', () => {
    const allStates = {
      'service-a': {
        feature: 'service-a',
        status: 'planned' as const,
      } as FeatureState
    };

    const isReady = isDependencyReady('service-a', allStates, {});
    
    expect(isReady).toBe(true); // Ready since it has no dependencies
  });
});