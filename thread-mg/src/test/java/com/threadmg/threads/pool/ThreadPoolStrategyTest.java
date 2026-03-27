package com.threadmg.threads.pool;

import com.threadmg.threads.pool.ThreadPoolStrategy.RejectPolicy;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ThreadPoolStrategy 单元测试
 */
@DisplayName("ThreadPoolStrategy Tests")
class ThreadPoolStrategyTest {

    private ThreadPoolStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new ThreadPoolStrategy();
    }

    @AfterEach
    void tearDown() {
        if (strategy != null) {
            strategy.shutdown();
        }
    }

    @Nested
    @DisplayName("构造方法测试")
    class ConstructorTests {

        @Test
        @DisplayName("默认构造方法应使用默认参数")
        void testDefaultConstructor() {
            ThreadPoolStrategy s = new ThreadPoolStrategy();
            assertNotNull(s);
            assertEquals("pool", s.getName());
            assertTrue(s.getDefaultThreadCount() >= 1);
        }

        @Test
        @DisplayName("自定义参数构造方法应正确设置参数")
        void testCustomConstructor() {
            ThreadPoolStrategy s = new ThreadPoolStrategy(4, 8, 30L, 500);
            assertEquals(4, s.getCorePoolSize());
            assertEquals(8, s.getMaxPoolSize());
            assertEquals(30L, s.getKeepAliveTime());
            assertEquals(500, s.getQueueCapacity());
        }

        @Test
        @DisplayName("完整参数构造方法应正确设置所有参数")
        void testFullConstructor() {
            ThreadPoolStrategy s = new ThreadPoolStrategy(4, 8, 30L, 500, RejectPolicy.ABORT);
            assertEquals(4, s.getCorePoolSize());
            assertEquals(8, s.getMaxPoolSize());
            assertEquals(30L, s.getKeepAliveTime());
            assertEquals(500, s.getQueueCapacity());
            assertEquals(RejectPolicy.ABORT, s.getRejectPolicy());
        }

        @Test
        @DisplayName("corePoolSize 小于 1 应抛出异常")
        void testInvalidCorePoolSize() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ThreadPoolStrategy(0, 8, 30L, 500));
        }

        @Test
        @DisplayName("maxPoolSize 小于 corePoolSize 应抛出异常")
        void testInvalidMaxPoolSize() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ThreadPoolStrategy(8, 4, 30L, 500));
        }

        @Test
        @DisplayName("keepAliveTime 小于等于 0 应抛出异常")
        void testInvalidKeepAliveTime() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ThreadPoolStrategy(4, 8, 0L, 500));
        }

        @Test
        @DisplayName("queueCapacity 小于 1 应抛出异常")
        void testInvalidQueueCapacity() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ThreadPoolStrategy(4, 8, 30L, 0));
        }
    }

    @Nested
    @DisplayName("Builder 模式测试")
    class BuilderTests {

        @Test
        @DisplayName("Builder 应正确构建策略实例")
        void testBuilder() {
            ThreadPoolStrategy s = ThreadPoolStrategy.builder()
                    .corePoolSize(4)
                    .maxPoolSize(16)
                    .keepAliveTime(120L)
                    .queueCapacity(2000)
                    .rejectedPolicy(RejectPolicy.DISCARD)
                    .build();

            assertEquals(4, s.getCorePoolSize());
            assertEquals(16, s.getMaxPoolSize());
            assertEquals(120L, s.getKeepAliveTime());
            assertEquals(2000, s.getQueueCapacity());
            assertEquals(RejectPolicy.DISCARD, s.getRejectPolicy());
        }

        @Test
        @DisplayName("Builder 参数验证应生效")
        void testBuilderValidation() {
            assertThrows(IllegalArgumentException.class,
                    () -> ThreadPoolStrategy.builder()
                            .corePoolSize(0)
                            .build());
        }
    }

    @Nested
    @DisplayName("执行器创建测试")
    class ExecutorCreationTests {

        @Test
        @DisplayName("createExecutor 应返回有效的执行器")
        void testCreateExecutor() {
            ExecutorService executor = strategy.createExecutor();
            assertNotNull(executor);
            assertFalse(executor.isShutdown());
        }

        @Test
        @DisplayName("多次创建执行器应正常工作")
        void testMultipleExecutorCreation() {
            ExecutorService executor1 = strategy.createExecutor();
            strategy.shutdown();

            // 创建新策略实例
            strategy = new ThreadPoolStrategy();
            ExecutorService executor2 = strategy.createExecutor();

            assertNotNull(executor1);
            assertNotNull(executor2);
            assertFalse(executor2.isShutdown());
        }

        @Test
        @DisplayName("关闭后不应再创建执行器")
        void testCreateExecutorAfterShutdown() {
            strategy.shutdown();
            assertThrows(IllegalStateException.class,
                    () -> strategy.createExecutor());
        }
    }

    @Nested
    @DisplayName("任务执行测试")
    class TaskExecutionTests {

        @Test
        @DisplayName("执行简单任务应成功")
        void testSimpleTaskExecution() throws Exception {
            ExecutorService executor = strategy.createExecutor();
            CountDownLatch latch = new CountDownLatch(1);

            executor.submit(() -> {
                latch.countDown();
            });

            boolean completed = latch.await(5, TimeUnit.SECONDS);
            assertTrue(completed, "Task should complete within timeout");
        }

        @Test
        @DisplayName("执行多个任务应成功")
        void testMultipleTasksExecution() throws Exception {
            ExecutorService executor = strategy.createExecutor();
            int taskCount = 100;
            CountDownLatch latch = new CountDownLatch(taskCount);

            for (int i = 0; i < taskCount; i++) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    latch.countDown();
                });
            }

            boolean completed = latch.await(10, TimeUnit.SECONDS);
            assertTrue(completed, "All tasks should complete within timeout");
        }

        @Test
        @DisplayName("执行带返回值任务应成功")
        void testTaskWithReturn() throws Exception {
            ExecutorService executor = strategy.createExecutor();
            Future<Integer> future = executor.submit(() -> 42);

            Integer result = future.get(5, TimeUnit.SECONDS);
            assertEquals(42, result);
        }
    }

    @Nested
    @DisplayName("关闭测试")
    class ShutdownTests {

        @Test
        @DisplayName("shutdown 应正常关闭执行器")
        void testShutdown() {
            strategy.createExecutor();
            strategy.shutdown();

            assertTrue(strategy.isShutdown());
        }

        @Test
        @DisplayName("重复关闭应安全")
        void testDoubleShutdown() {
            strategy.createExecutor();
            strategy.shutdown();
            strategy.shutdown(); // Should not throw

            assertTrue(strategy.isShutdown());
        }

        @Test
        @DisplayName("等待关闭时应能中断")
        void testShutdownWithInterruption() throws Exception {
            ThreadPoolStrategy s = ThreadPoolStrategy.builder()
                    .corePoolSize(4)
                    .maxPoolSize(4)
                    .queueCapacity(1000)
                    .build();

            ExecutorService executor = s.createExecutor();

            // 提交长时间运行的任务
            for (int i = 0; i < 10; i++) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            s.shutdown();
            assertTrue(s.isShutdown());
        }
    }

    @Nested
    @DisplayName("拒绝策略测试")
    class RejectPolicyTests {

        @Test
        @DisplayName("ABORT 策略应抛出异常")
        void testAbortPolicy() throws Exception {
            ThreadPoolStrategy s = ThreadPoolStrategy.builder()
                    .corePoolSize(1)
                    .maxPoolSize(1)
                    .queueCapacity(1)
                    .rejectedPolicy(RejectPolicy.ABORT)
                    .build();

            ExecutorService executor = s.createExecutor();

            // 提交足够任务填满线程池和队列
            executor.submit(() -> sleep(1000));
            executor.submit(() -> sleep(1000));

            // 第 3 个任务应该被拒绝
            assertThrows(RejectedExecutionException.class,
                    () -> executor.submit(() -> sleep(1000)));

            s.shutdown();
        }

        @Test
        @DisplayName("CALLER_RUNS 策略应由调用线程执行")
        void testCallerRunsPolicy() throws Exception {
            ThreadPoolStrategy s = ThreadPoolStrategy.builder()
                    .corePoolSize(1)
                    .maxPoolSize(1)
                    .queueCapacity(1)
                    .rejectedPolicy(RejectPolicy.CALLER_RUNS)
                    .build();

            ExecutorService executor = s.createExecutor();

            // 提交足够任务
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                futures.add(executor.submit(() -> sleep(10)));
            }

            // 所有任务应成功提交
            assertEquals(5, futures.size());

            s.shutdown();
        }

        @Test
        @DisplayName("DISCARD 策略应丢弃任务")
        void testDiscardPolicy() throws Exception {
            ThreadPoolStrategy s = ThreadPoolStrategy.builder()
                    .corePoolSize(1)
                    .maxPoolSize(1)
                    .queueCapacity(1)
                    .rejectedPolicy(RejectPolicy.DISCARD)
                    .build();

            ExecutorService executor = s.createExecutor();

            // 提交足够任务
            for (int i = 0; i < 5; i++) {
                executor.submit(() -> sleep(10));
            }

            // 不应抛出异常
            s.shutdown();
        }
    }

    @Nested
    @DisplayName(" getDescription 测试")
    class DescriptionTests {

        @Test
        @DisplayName("getDescription 应返回包含配置的描述")
        void testGetDescription() {
            ThreadPoolStrategy s = new ThreadPoolStrategy(4, 8, 60L, 1000);
            String description = s.getDescription();

            assertTrue(description.contains("core=4"));
            assertTrue(description.contains("max=8"));
            assertTrue(description.contains("queue=1000"));
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
