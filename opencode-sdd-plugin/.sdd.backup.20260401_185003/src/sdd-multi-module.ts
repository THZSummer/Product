// .sdd/src/sdd-multi-module.ts
// 主入口模块用于 Multi-Feature SDD 实现

import { FeatureState, validateState, StateSchemaV1211 } from './state/schema-v1.2.11';
import { saveState, loadState, scanSubFeatures, detectFeatureMode, aggregateSubFeatureState } from './state/manager';
import { 
  detectFeatureMode as detectMode,
  initSubFeature,
  generateSubFeatureIndex,
  validateSubFeatureDocuments,
  createMultiFeatureStructure,
  getAllSubFeatureStates
} from './utils/subfeature-manager';
import { parseParallelGroups, computeExecutionOrder, ParallelGroup, ParsedTask } from './utils/tasks-parser';
import { notifyDependentFeatures } from './utils/dependency-notifier';
import { migrateState, backupState } from './state/migrator';
import { isLegacyState, migrateStateFromLegacy } from './utils/compatibility';
import { getSDDWorkspace, getSpecsDir, getFeatureDir, getStateFilePath } from './utils/workspace';

/**
 * Multi-Module SDD Plugin
 * Implements the SDD features for multi sub-feature projects
 */

export class MultiSDDManager {
  /**
   * Initialize a new multi-feature project structure
   */
  static async initializeMultiFeature(featureId: string, subFeatures: string[]): Promise<boolean> {
    try {
      // Create the multi-feature structure
      await createMultiFeatureStructure(featureId, subFeatures);
      
      console.log(`Successfully created multi-feature structure for ${featureId} with sub-features: ${subFeatures.join(', ')}`);
      
      return true;
    } catch (error) {
      console.error(`Failed to initialize multi-feature:`, error);
      return false;
    }
  }

  /**
   * Determine if a project uses multi-feature or single-feature mode
   */
  static async determineProjectMode(featureId: string): Promise<'single' | 'multi'> {
    return await detectFeatureMode(featureId);
  }

  /**
   * Execute a multi-step workflow: save state → aggregate → check dependencies
   */
  static async executeFeatureUpdate(featureId: string, newState: FeatureState): Promise<boolean> {
    try {
      // 1. Save the new state
      await saveState(featureId, newState);
      
      // 2. Aggregate related feature states
      const aggregatedState = await aggregateSubFeatureState(featureId);
      
      // 3. Notify any dependent features
      await notifyDependentFeatures(featureId, '');
      
      console.log(`Feature update completed for ${featureId}`);
      return true;
    } catch (error) {
      console.error(`Failed to execute feature update:`, error);
      return false;
    }
  }

  /**
   * Validate that all sub-features have required components
   */
  static async validateFeatureCompleteness(featureId: string): Promise<Record<string, { valid: boolean; issues: string[] }>> {
    try {
      const allStates = await getAllSubFeatureStates(featureId);
      const results: Record<string, { valid: boolean; issues: string[] }> = {};

      for (const [subFeatureId, _] of Object.entries(allStates)) {
        results[subFeatureId] = await validateSubFeatureDocuments(subFeatureId);
      }

      return results;
    } catch (error) {
      console.error(`Failed to validate feature completeness:`, error);
      return {};
    }
  }

  /**
   * Generate a sub-feature index for documentation purposes
   */
  static async generateSubFeatureIndex(featureId: string): Promise<string> {
    return await generateSubFeatureIndex(featureId);
  }

  /**
   * Parse tasks defined in tasks.md following the parallel group structure
   */
  static parseTasks(tasksMarkdown: string): ReturnType<typeof parseParallelGroups> {
    return parseParallelGroups(tasksMarkdown);
  }

  /**
   * Calculate the execution order of feature tasks considering dependencies
   */
  static calculateExecutionOrder(parallelGroups: ReturnType<typeof parseParallelGroups>): ReturnType<typeof computeExecutionOrder> {
    return computeExecutionOrder(parallelGroups);
  }

  /**
   * Load state and apply migrations if needed
   */
  static async loadAndMigrateState(featureId: string): Promise<FeatureState> {
    // Note: In a real implementation we'd need to read the raw state first to determine if migration is needed
    // This is a simplified version
    const state = await loadState(featureId);
    
    if (isLegacyState(state)) {
      console.log(`Migrating legacy state for feature ${featureId}`);
      return migrateStateFromLegacy(state);
    }
    
    return state;
  }
}

// Export all individual functions and types for custom usage
export {
  // State Schema & Validation
  FeatureState,
  validateState,
  StateSchemaV1211,
  
  // State Management
  saveState,
  loadState,
  scanSubFeatures,
  detectFeatureMode as detectStateFeatureMode,
  aggregateSubFeatureState,
  
  // Workspace utilities
  getSDDWorkspace,
  getSpecsDir,
  getFeatureDir,
  getStateFilePath,
  
  // Sub-feature management
  detectMode,
  initSubFeature,
  generateSubFeatureIndex,
  validateSubFeatureDocuments,
  createMultiFeatureStructure,
  getAllSubFeatureStates,
  
  // Task Parsing
  parseParallelGroups,
  computeExecutionOrder,
  ParallelGroup,
  ParsedTask,
  
  // Dependency management
  notifyDependentFeatures,
  
  // Migration tools
  migrateState,
  backupState,
  isLegacyState,
  migrateStateFromLegacy,
  
  // Additional types
  ParallelGroup as ParallelGroupType,
  ParsedTask as ParsedTaskType
};

export * from './state/schema-v1.2.11';
export * from './utils/tasks-parser';