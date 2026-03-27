package com.threadmg.threads.platform;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;

/**
 * PlatformThreadStrategy测试
 */
public class PlatformThreadStrategyTest {
    
    @Test
    void testGetNameAndDescription() {
        PlatformThreadStrategy strategy = new PlatformThreadStrategy();
        
        assertEquals("platform-threads", strategy.getName());
        assertEquals("Traditional platform thread strategy using fixed-size thread pool", strategy.getDescription());
    }
    
    @Test
    void testDefaultConstructor() {
        PlatformThreadStrategy strategy = new PlatformThreadStrategy();
        
        // Should use max(availableProcessors() * 2, 16) as default for I/O-bound scenarios
        int expectedThreads = Math.max(Runtime.getRuntime().availableProcessors() * 2, 16);
        assertEquals(expectedThreads, strategy.getDefaultThreadCount());
    }
    
    @Test
    void testParameterizedConstructor() {
        PlatformThreadStrategy strategy = new PlatformThreadStrategy(8);
        
        assertEquals(8, strategy.getDefaultThreadCount());
    }
    
    @Test
    void testParameterizedConstructorMinValue() {
        // Even if we pass 0 or negative, it should default to 1
        PlatformThreadStrategy strategy = new PlatformThreadStrategy(0);
        assertEquals(1, strategy.getDefaultThreadCount());
        
        PlatformThreadStrategy negativeStrategy = new PlatformThreadStrategy(-5);
        assertEquals(1, negativeStrategy.getDefaultThreadCount());
    }
    
    @Test
    void testCreateAndShutdownExecutor() {
        PlatformThreadStrategy strategy = new PlatformThreadStrategy(2);
        
        ExecutorService executor = strategy.createExecutor();
        assertNotNull(executor);
        
        // Verify that we can call shutdown without exceptions
        assertDoesNotThrow(() -> {
            strategy.shutdown();
        });
    }
    
    @Test
    void testCreateExecutorMultipleTimes() {
        PlatformThreadStrategy strategy = new PlatformThreadStrategy(2);
        
        // Create executor
        ExecutorService firstExecutor = strategy.createExecutor();
        assertNotNull(firstExecutor);
        
        // Create another executor
        ExecutorService secondExecutor = strategy.createExecutor();
        assertNotNull(secondExecutor);
        
        // Both should be valid
        assertDoesNotThrow(() -> strategy.shutdown());
    }
    
    @Test
    void testShutdownWithoutCreatingExecutor() {
        PlatformThreadStrategy strategy = new PlatformThreadStrategy(2);
        
        // Shutdown should be safe even if no executor was created
        assertDoesNotThrow(() -> {
            strategy.shutdown();
        });
    }
    
    @Test
    void testReentrantShutdown() {
        PlatformThreadStrategy strategy = new PlatformThreadStrategy(2);
        
        // Create executor and shutdown
        ExecutorService executor = strategy.createExecutor();
        assertNotNull(executor);
        
        // First shutdown should be safe
        assertDoesNotThrow(() -> {
            strategy.shutdown();
        });
        
        // Second shutdown should also be safe
        assertDoesNotThrow(() -> {
            strategy.shutdown();
        });
    }
}