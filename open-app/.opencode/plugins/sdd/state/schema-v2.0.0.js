// State Schema v2.0.0
// Defines the structure for distributed workflow state management
// Validation function
export function validateState(state) {
    // Required fields validation
    if (!state || typeof state !== 'object') {
        console.error('State must be an object');
        return false;
    }
    // Validate feature field
    if (!state.feature || typeof state.feature !== 'string') {
        console.error('feature must be a non-empty string');
        return false;
    }
    // Validate version field
    if (!state.version || typeof state.version !== 'string') {
        console.error('version must be a non-empty string');
        return false;
    }
    // Validate status field
    const validStatuses = ['specified', 'planned', 'tasked', 'building', 'reviewed', 'validated'];
    if (!validStatuses.includes(state.status)) {
        console.error('status must be one of the valid workflow statuses');
        return false;
    }
    // Validate phase field
    if (typeof state.phase !== 'number' || state.phase < 1 || state.phase > 6) {
        console.error('phase must be a number between 1 and 6');
        return false;
    }
    // Validate phaseHistory field
    if (!Array.isArray(state.phaseHistory)) {
        console.error('phaseHistory must be an array');
        return false;
    }
    for (const historyItem of state.phaseHistory) {
        if (!historyItem ||
            typeof historyItem !== 'object' ||
            typeof historyItem.phase !== 'number' ||
            typeof historyItem.status !== 'string' ||
            typeof historyItem.timestamp !== 'string' ||
            typeof historyItem.triggeredBy !== 'string') {
            console.error('Each phaseHistory item must have phase(number), status(string), timestamp(string), and triggeredBy(string)');
            return false;
        }
        if (!validStatuses.includes(historyItem.status)) {
            console.error('Phase history status must be one of the valid workflow statuses');
            return false;
        }
        if (historyItem.phase < 1 || historyItem.phase > 6) {
            console.error('Phase history phase must be between 1 and 6');
            return false;
        }
    }
    // Validate files field
    if (!state.files || typeof state.files !== 'object') {
        console.error('files must be an object');
        return false;
    }
    if (typeof state.files.spec !== 'string') {
        console.error('files.spec must be a string');
        return false;
    }
    // Validate dependencies field
    if (!state.dependencies || typeof state.dependencies !== 'object') {
        console.error('dependencies must be an object');
        return false;
    }
    if (!Array.isArray(state.dependencies.on) || !Array.isArray(state.dependencies.blocking)) {
        console.error('dependencies.on and dependencies.blocking must be arrays');
        return false;
    }
    // Additional checks based on status and phase relationship
    if (state.phase === 1 && state.status !== 'specified') {
        console.warn('Phase 1 should typically have "specified" status');
    }
    else if (state.phase === 2 && state.status !== 'planned') {
        console.warn('Phase 2 should typically have "planned" status');
    }
    else if (state.phase === 3 && state.status !== 'tasked') {
        console.warn('Phase 3 should typically have "tasked" status');
    }
    else if (state.phase === 4 && state.status !== 'building') {
        console.warn('Phase 4 should typically have "building" status');
    }
    else if (state.phase === 5 && state.status !== 'reviewed') {
        console.warn('Phase 5 should typically have "reviewed" status');
    }
    else if (state.phase === 6 && state.status !== 'validated') {
        console.warn('Phase 6 should typically have "validated" status');
    }
    return true;
}
