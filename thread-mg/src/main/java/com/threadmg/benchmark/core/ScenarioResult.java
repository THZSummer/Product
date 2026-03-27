package com.threadmg.benchmark.core;

import java.time.Instant;
import java.util.List;

/**
 * 场景执行结果
 */
public class ScenarioResult {
    private final String scenarioName;
    private final int totalTasks;
    private final int completedTasks;
    private final long durationNanos;
    private final List<Long> taskLatenciesNanos; // 每个任务的延迟
    private final boolean success;
    private final String errorMessage;
    private final Instant timestamp;

    public ScenarioResult(String scenarioName, int totalTasks, int completedTasks, 
                         long durationNanos, List<Long> taskLatenciesNanos, 
                         boolean success, String errorMessage) {
        this.scenarioName = scenarioName;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.durationNanos = durationNanos;
        this.taskLatenciesNanos = taskLatenciesNanos;
        this.success = success;
        this.errorMessage = errorMessage;
        this.timestamp = Instant.now();
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public long getDurationNanos() {
        return durationNanos;
    }

    public List<Long> getTaskLatenciesNanos() {
        return taskLatenciesNanos;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public double getThroughput() {
        if (durationNanos <= 0) {
            return 0.0;
        }
        return (double) completedTasks / (durationNanos / 1_000_000_000.0);
    }

    public double getAverageLatencyMs() {
        if (taskLatenciesNanos == null || taskLatenciesNanos.isEmpty()) {
            return 0.0;
        }
        return taskLatenciesNanos.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0) / 1_000_000.0; // Convert nanoseconds to milliseconds
    }

    public double getPercentile(double percentile) {
        if (taskLatenciesNanos == null || taskLatenciesNanos.isEmpty()) {
            return 0.0;
        }

        List<Long> sorted = taskLatenciesNanos.stream()
            .sorted()
            .toList();

        int index = (int) Math.ceil(percentile / 100.0 * sorted.size()) - 1;
        index = Math.max(0, Math.min(index, sorted.size() - 1));

        return sorted.get(index) / 1_000_000.0; // Convert nanoseconds to milliseconds
    }
}