package com.threadmg.benchmark.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;

/**
 * ThreadStrategy接口的测试
 */
public class ThreadStrategyTest {
    
    @Test
    void testPlatformThreadStrategy() {
        ThreadStrategy platformStrategy = new MockPlatformThreadStrategy();
        
        assertEquals("platform-threads", platformStrategy.getName());
        assertEquals("A mock platform thread strategy for testing", platformStrategy.getDescription());
        assertEquals(4, platformStrategy.getDefaultThreadCount());
        
        ExecutorService executor = platformStrategy.createExecutor();
        assertNotNull(executor);
        
        platformStrategy.shutdown();
    }
    
    @Test
    void testVirtualThreadStrategy() {
        ThreadStrategy virtualStrategy = new MockVirtualThreadStrategy();
        
        assertEquals("virtual-threads", virtualStrategy.getName());
        assertEquals("A mock virtual thread strategy for testing", virtualStrategy.getDescription());
        assertEquals(Integer.MAX_VALUE, virtualStrategy.getDefaultThreadCount());
        
        ExecutorService executor = virtualStrategy.createExecutor();
        assertNotNull(executor);
        
        virtualStrategy.shutdown();
    }
    
    /**
     * Mock implementation of ThreadStrategy for platform threads
     */
    private static class MockPlatformThreadStrategy implements ThreadStrategy {
        private ExecutorService executor;
        
        @Override
        public String getName() {
            return "platform-threads";
        }
        
        @Override
        public String getDescription() {
            return "A mock platform thread strategy for testing";
        }
        
        @Override
        public ExecutorService createExecutor() {
            executor = java.util.concurrent.Executors.newFixedThreadPool(4);
            return executor;
        }
        
        @Override
        public void shutdown() {
            if (executor != null) {
                executor.shutdown();
            }
        }
        
        @Override
        public int getDefaultThreadCount() {
            return 4;
        }
    }
    
    /**
     * Mock implementation of ThreadStrategy for virtual threads
     */
    private static class MockVirtualThreadStrategy implements ThreadStrategy {
        private ExecutorService executor;
        
        @Override
        public String getName() {
            return "virtual-threads";
        }
        
        @Override
        public String getDescription() {
            return "A mock virtual thread strategy for testing";
        }
        
        @Override
        public ExecutorService createExecutor() {
            executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor();
            return executor;
        }
        
        @Override
        public void shutdown() {
            if (executor != null) {
                executor.shutdown();
            }
        }
        
        @Override
        public int getDefaultThreadCount() {
            return Integer.MAX_VALUE;
        }
    }
}