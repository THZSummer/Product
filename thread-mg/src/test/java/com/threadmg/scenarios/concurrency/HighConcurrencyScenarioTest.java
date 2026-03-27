package com.threadmg.scenarios.concurrency;

import com.threadmg.benchmark.core.Scenario;
import com.threadmg.benchmark.core.ScenarioResult;
import com.threadmg.threads.platform.PlatformThreadStrategy;
import com.threadmg.threads.virtual.VirtualThreadStrategy;
import org.junit.jupiter.api.*;

import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HighConcurrencyScenario 单元测试
 */
@DisplayName("HighConcurrencyScenario Tests")
class HighConcurrencyScenarioTest {

    private HighConcurrencyScenario scenario;
    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        scenario = new HighConcurrencyScenario();
    }

    @AfterEach
    void tearDown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        if (scenario != null) {
            // Scenario doesn't have its own resources to cleanup
        }
    }

    @Nested
    @DisplayName("构造方法测试")
    class ConstructorTests {

        @Test
        @DisplayName("默认构造方法应使用默认参数")
        void testDefaultConstructor() {
            HighConcurrencyScenario s = new HighConcurrencyScenario();
            assertNotNull(s);
            assertEquals("high-concurrency", s.getName());
            assertEquals(10000, s.getTaskCount());
        }

        @Test
        @DisplayName("自定义参数构造方法应正确设置参数")
        void testCustomConstructor() {
            HighConcurrencyScenario s = new HighConcurrencyScenario(
                    5000, 2, 8, false);
            assertEquals(5000, s.getTaskCount());
            assertEquals(2, s.getMinExecutionTimeMs());
            assertEquals(8, s.getMaxExecutionTimeMs());
            assertFalse(s.isSimulateIoWait());
        }

        @Test
        @DisplayName("taskCount 小于 1 应抛出异常")
        void testInvalidTaskCount() {
            assertThrows(IllegalArgumentException.class,
                    () -> new HighConcurrencyScenario(0, 1, 10, true));
        }

        @Test
        @DisplayName("minExecutionTimeMs 小于 0 应抛出异常")
        void testInvalidMinExecutionTime() {
            assertThrows(IllegalArgumentException.class,
                    () -> new HighConcurrencyScenario(100, -1, 10, true));
        }

        @Test
        @DisplayName("maxExecutionTimeMs 小于 minExecutionTimeMs 应抛出异常")
        void testInvalidMaxExecutionTime() {
            assertThrows(IllegalArgumentException.class,
                    () -> new HighConcurrencyScenario(100, 10, 5, true));
        }
    }

    @Nested
    @DisplayName("Builder 模式测试")
    class BuilderTests {

        @Test
        @DisplayName("Builder 应正确构建场景实例")
        void testBuilder() {
            Scenario s = HighConcurrencyScenario.builder()
                    .taskCount(5000)
                    .executionTimeRange(2, 8)
                    .simulateIoWait(false)
                    .build();

            assertNotNull(s);
            assertEquals("high-concurrency", s.getName());
        }

        @Test
        @DisplayName("Builder 参数验证应生效")
        void testBuilderValidation() {
            assertThrows(IllegalArgumentException.class,
                    () -> HighConcurrencyScenario.builder()
                            .taskCount(0)
                            .build());
        }
    }

    @Nested
    @DisplayName("场景属性测试")
    class PropertyTests {

        @Test
        @DisplayName("getName 应返回正确名称")
        void testGetName() {
            assertEquals("high-concurrency", scenario.getName());
        }

        @Test
        @DisplayName("getDescription 应返回包含配置的描述")
        void testGetDescription() {
            String description = scenario.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("10000"));
            assertTrue(description.contains("tasks"));
        }
    }

    @Nested
    @DisplayName("任务创建测试")
    class TaskCreationTests {

        @Test
        @DisplayName("createTasks 应创建正确数量的任务")
        void testCreateTasks() {
            int count = 100;
            var tasks = scenario.createTasks(count);
            assertEquals(count, tasks.size());
        }

        @Test
        @DisplayName("createTasks 创建的任务应可执行")
        void testCreateTasksExecutable() {
            var tasks = scenario.createTasks(10);
            assertNotNull(tasks);
            assertFalse(tasks.isEmpty());
        }
    }

    @Nested
    @DisplayName("执行测试")
    class ExecutionTests {

        @Test
        @DisplayName("使用平台线程执行应成功")
        void testExecuteWithPlatformThreads() {
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(8);
            executor = strategy.createExecutor();

            ScenarioResult result = scenario.execute(executor, 100);

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals(100, result.getCompletedTasks());
            assertTrue(result.getDurationNanos() > 0);

            strategy.shutdown();
        }

        @Test
        @DisplayName("使用虚拟线程执行应成功")
        void testExecuteWithVirtualThreads() {
            if (!VirtualThreadStrategy.supportsVirtualThreads()) {
                return; // Skip if virtual threads not supported
            }

            VirtualThreadStrategy strategy = new VirtualThreadStrategy();
            executor = strategy.createExecutor();

            ScenarioResult result = scenario.execute(executor, 100);

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals(100, result.getCompletedTasks());

            strategy.shutdown();
        }

        @Test
        @DisplayName("执行结果应通过验证")
        void testVerifyResult() {
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(8);
            executor = strategy.createExecutor();

            ScenarioResult result = scenario.execute(executor, 100);

            assertTrue(scenario.verify(result));

            strategy.shutdown();
        }

        @Test
        @DisplayName("不同任务数量应正常执行")
        void testExecuteWithDifferentTaskCounts() {
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(4);
            executor = strategy.createExecutor();

            int[] taskCounts = {10, 50, 100};

            for (int count : taskCounts) {
                ScenarioResult result = scenario.execute(executor, count);
                assertNotNull(result);
                assertTrue(result.isSuccess(), "Failed for task count: " + count);
                assertEquals(count, result.getCompletedTasks());
            }

            strategy.shutdown();
        }

        @Test
        @DisplayName("高并发任务应在合理时间内完成")
        void testExecutionTime() {
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(16);
            executor = strategy.createExecutor();

            long startTime = System.currentTimeMillis();
            ScenarioResult result = scenario.execute(executor, 1000);
            long duration = System.currentTimeMillis() - startTime;

            assertTrue(result.isSuccess());
            // 1000 个任务，每个 1-10ms，16 线程并行，应该在 2 秒内完成
            assertTrue(duration < 2000, "Execution took too long: " + duration + "ms");

            strategy.shutdown();
        }
    }

    @Nested
    @DisplayName("验证测试")
    class VerificationTests {

        @Test
        @DisplayName("成功的结果应通过验证")
        void testVerifySuccess() {
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(8);
            executor = strategy.createExecutor();

            ScenarioResult result = scenario.execute(executor, 100);

            assertTrue(scenario.verify(result));

            strategy.shutdown();
        }

        @Test
        @DisplayName("失败的结果应不通过验证")
        void testVerifyFailure() {
            // Create a mock failed result
            ScenarioResult failedResult = new ScenarioResult(
                    "high-concurrency",
                    100,
                    50, // Only 50 completed
                    1000000000L,
                    java.util.List.of(),
                    false,
                    "Some tasks failed"
            );

            assertFalse(scenario.verify(failedResult));
        }
    }
}
