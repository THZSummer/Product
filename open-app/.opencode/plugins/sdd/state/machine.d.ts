export interface FeatureState {
    id: string;
    name: string;
    state: string;
    createdAt: string;
    updatedAt: string;
    tasks?: any[];
}
export declare class StateMachine {
    private specsDir;
    private states;
    private stateFilePath;
    constructor(specsDir?: string);
    load(): Promise<void>;
    save(): Promise<void>;
    createFeature(name: string): Promise<FeatureState>;
    getState(featureId: string): FeatureState | undefined;
    getAllFeatures(): FeatureState[];
}
