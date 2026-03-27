package com.threadmg.threads.virtual;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;

/**
 * VirtualThreadStrategy测试
 */
public class VirtualThreadStrategyTest {
    
    @Test
    void testGetNameAndDescription() {
        VirtualThreadStrategy strategy = new VirtualThreadStrategy();
        
        assertEquals("virtual-threads", strategy.getName());
        assertEquals("Virtual thread strategy using project Loom implementation", strategy.getDescription());
    }
    
    @Test
    void testDefaultConstructor() {
        VirtualThreadStrategy strategy = new VirtualThreadStrategy();
        
        // Virtual threads are theoretically unlimited
        assertEquals(Integer.MAX_VALUE, strategy.getDefaultThreadCount());
    }
    
    @Test
    void testCreateAndShutdownExecutor() {
        VirtualThreadStrategy strategy = new VirtualThreadStrategy();
        
        ExecutorService executor = strategy.createExecutor();
        assertNotNull(executor);
        
        // Verify that we can call shutdown without exceptions
        assertDoesNotThrow(() -> {
            strategy.shutdown();
        });
    }
    
    @Test
    void testCreateExecutorMultipleTimes() {
        VirtualThreadStrategy strategy = new VirtualThreadStrategy();
        
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
        VirtualThreadStrategy strategy = new VirtualThreadStrategy();
        
        // Shutdown should be safe even if no executor was created
        assertDoesNotThrow(() -> {
            strategy.shutdown();
        });
    }
    
    @Test
    void testReentrantShutdown() {
        VirtualThreadStrategy strategy = new VirtualThreadStrategy();
        
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
    
    @Test
    void testSupportsVirtualThreads() {
        boolean supports = VirtualThreadStrategy.supportsVirtualThreads();
        assertTrue(supports, "Current JVM should support virtual threads");
    }
    
    @Test
    void testJavaVersionCheck() {
        // Test that the static method can detect if virtual threads are supported
        boolean supports = VirtualThreadStrategy.supportsVirtualThreads();
        
        // On Java 21, this should be true
        // Output Java version for debugging
        System.out.println("Java version: " + System.getProperty("java.version"));
        
        // Since we're running on the given environment, assume Java 21+ is available
        assertTrue(supports, "Java version should support virtual threads");
    }
}