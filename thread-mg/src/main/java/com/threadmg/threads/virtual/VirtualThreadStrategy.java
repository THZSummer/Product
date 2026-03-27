package com.threadmg.threads.virtual;

import com.threadmg.benchmark.core.ThreadStrategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 虚拟线程策略
 * 使用Java 21+提供的虚拟线程
 */
public class VirtualThreadStrategy implements ThreadStrategy {
    private ExecutorService executor;
    
    public VirtualThreadStrategy() {
        // Verify Java version at runtime
        String version = System.getProperty("java.version");
        String[] parts = version.split("\\.");
        int majorVersion = Integer.parseInt(parts[0]);
        if (majorVersion < 21) {
            System.err.println("Warning: Virtual threads are available from Java 21+. Current version: " + version);
        }
    }
    
    @Override
    public String getName() {
        return "virtual-threads";
    }
    
    @Override
    public String getDescription() {
        return "Virtual thread strategy using project Loom implementation";
    }
    
    @Override
    public ExecutorService createExecutor() {
        executor = Executors.newVirtualThreadPerTaskExecutor();
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
                        System.err.println("Virtual thread pool did not terminate");
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
        // Virtual threads are unlimited in theory (limited by virtual memory),
        // so return MAX_VALUE to indicate no practical limit
        return Integer.MAX_VALUE;
    }
    
    /**
     * Check if the current Java version supports virtual threads
     */
    public static boolean supportsVirtualThreads() {
        try {
            String version = System.getProperty("java.version");
            String[] parts = version.split("\\.");
            int majorVersion = Integer.parseInt(parts[0]);
            
            // Check for versions like "21" or "21.0.1"
            if (majorVersion >= 21) {
                return true;
            }
            
            // Alternative check: try to create a virtual thread
            Class.forName("java.lang.VirtualThread");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}