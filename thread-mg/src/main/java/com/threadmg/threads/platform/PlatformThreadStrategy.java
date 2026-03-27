package com.threadmg.threads.platform;

import com.threadmg.benchmark.core.ThreadStrategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 平台线程策略
 * 使用传统的固定大小线程池
 */
public class PlatformThreadStrategy implements ThreadStrategy {
    private final int threadCount;
    private ExecutorService executor;
    
    public PlatformThreadStrategy() {
        // For I/O-bound scenarios, the traditional rule of thumb suggests 
        // processor count + some additional for I/O waiting
        this(Math.max(Runtime.getRuntime().availableProcessors() * 2, 16)); // Increased default for I/O scenarios
    }
    
    public PlatformThreadStrategy(int threadCount) {
        this.threadCount = Math.max(1, threadCount); // Ensure at least 1 thread
    }
    
    @Override
    public String getName() {
        return "platform-threads";
    }
    
    @Override
    public String getDescription() {
        return "Traditional platform thread strategy using fixed-size thread pool";
    }
    
    @Override
    public ExecutorService createExecutor() {
        executor = Executors.newFixedThreadPool(threadCount);
        return executor;
    }
    
    @Override
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    if (!executor.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                        System.err.println("Pool did not terminate");
                    }
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Override
    public int getDefaultThreadCount() {
        return threadCount;
    }
    
    public int getThreadCount() {
        return threadCount;
    }
}