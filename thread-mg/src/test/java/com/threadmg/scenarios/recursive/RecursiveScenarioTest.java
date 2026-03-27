package com.threadmg.scenarios.recursive;

import com.threadmg.benchmark.core.Scenario;
import com.threadmg.benchmark.core.ScenarioResult;
import com.threadmg.benchmark.core.ThreadStrategy;
import com.threadmg.threads.platform.PlatformThreadStrategy;
import org.junit.jupiter.api.*;

import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RecursiveScenario 单元测试
 */
@DisplayName("RecursiveScenario Tests")
class RecursiveScenarioTest {

    private RecursiveScenario scenario;
    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        scenario = new RecursiveScenario();
    }

    @AfterEach
    void tearDown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    @Nested
    @DisplayName("构造方法测试")
    class ConstructorTests {

        @Test
        @DisplayName("默认构造方法应使用默认参数")
        void testDefaultConstructor() {
            RecursiveScenario s = new RecursiveScenario();
            assertNotNull(s);
            assertEquals("recursive", s.getName());
            assertEquals(RecursiveScenario.AlgorithmType.QUICK_SORT, s.getAlgorithmType());
            assertEquals(10000, s.getDataSize());
            assertEquals(1000, s.getThreshold());
        }

        @Test
        @DisplayName("自定义参数构造方法应正确设置参数")
        void testCustomConstructor() {
            RecursiveScenario s = new RecursiveScenario(
                    RecursiveScenario.AlgorithmType.MERGE_SORT, 5000, 500);
            assertEquals(RecursiveScenario.AlgorithmType.MERGE_SORT, s.getAlgorithmType());
            assertEquals(5000, s.getDataSize());
            assertEquals(500, s.getThreshold());
        }

        @Test
        @DisplayName("algorithmType 为 null 应抛出异常")
        void testNullAlgorithmType() {
            assertThrows(IllegalArgumentException.class,
                    () -> new RecursiveScenario(null, 10000, 1000));
        }

        @Test
        @DisplayName("dataSize 小于 1 应抛出异常")
        void testInvalidDataSize() {
            assertThrows(IllegalArgumentException.class,
                    () -> new RecursiveScenario(
                            RecursiveScenario.AlgorithmType.QUICK_SORT, 0, 1000));
        }

        @Test
        @DisplayName("threshold 小于 1 应抛出异常")
        void testInvalidThreshold() {
            assertThrows(IllegalArgumentException.class,
                    () -> new RecursiveScenario(
                            RecursiveScenario.AlgorithmType.QUICK_SORT, 10000, 0));
        }
    }

    @Nested
    @DisplayName("Builder 模式测试")
    class BuilderTests {

        @Test
        @DisplayName("Builder 应正确构建场景实例")
        void testBuilder() {
            Scenario s = RecursiveScenario.builder()
                    .algorithmType(RecursiveScenario.AlgorithmType.FIBONACCI)
                    .dataSize(3000)
                    .threshold(500)
                    .build();

            assertNotNull(s);
            assertEquals("recursive", s.getName());
        }

        @Test
        @DisplayName("Builder 参数验证应生效")
        void testBuilderValidation() {
            assertThrows(IllegalArgumentException.class,
                    () -> RecursiveScenario.builder()
                            .dataSize(0)
                            .build());
        }
    }

    @Nested
    @DisplayName("场景属性测试")
    class PropertyTests {

        @Test
        @DisplayName("getName 应返回正确名称")
        void testGetName() {
            assertEquals("recursive", scenario.getName());
        }

        @Test
        @DisplayName("getDescription 应返回包含配置的描述")
        void testGetDescription() {
            String description = scenario.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("QUICK_SORT"));
            assertTrue(description.contains("size="));
            assertTrue(description.contains("threshold="));
        }

        @Test
        @DisplayName("不同算法的 getDescription")
        void testGetDescriptionDifferentAlgorithms() {
            RecursiveScenario mergeScenario = new RecursiveScenario(
                    RecursiveScenario.AlgorithmType.MERGE_SORT, 5000, 500);
            String mergeDesc = mergeScenario.getDescription();
            assertTrue(mergeDesc.contains("MERGE_SORT"));

            RecursiveScenario fibScenario = new RecursiveScenario(
                    RecursiveScenario.AlgorithmType.FIBONACCI, 1000, 100);
            String fibDesc = fibScenario.getDescription();
            assertTrue(fibDesc.contains("FIBONACCI"));
        }
    }

    @Nested
    @DisplayName("任务创建测试")
    class TaskCreationTests {

        @Test
        @DisplayName("createTasks 应创建正确数量的任务")
        void testCreateTasks() {
            int count = 10;
            var tasks = scenario.createTasks(count);
            assertEquals(count, tasks.size());
        }

        @Test
        @DisplayName("createTasks 创建的任务应可执行")
        void testCreateTasksExecutable() {
            var tasks = scenario.createTasks(5);
            assertNotNull(tasks);
            assertFalse(tasks.isEmpty());
        }
    }

    @Nested
    @DisplayName("快速排序测试")
    class QuickSortTests {

        @Test
        @DisplayName("快速排序应正确排序")
        void testQuickSort() {
            RecursiveScenario s = new RecursiveScenario(
                    RecursiveScenario.AlgorithmType.QUICK_SORT, 1000, 100);
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(4);
            executor = strategy.createExecutor();

            ScenarioResult result = s.execute(executor, 5);

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals(5, result.getCompletedTasks());

            strategy.shutdown();
        }

        @Test
        @DisplayName("快速排序应验证结果正确性")
        void testQuickSortVerification() {
            RecursiveScenario s = new RecursiveScenario(
                    RecursiveScenario.AlgorithmType.QUICK_SORT, 5000, 500);
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(4);
            executor = strategy.createExecutor();

            ScenarioResult result = s.execute(executor, 3);

            assertTrue(s.verify(result));

            strategy.shutdown();
        }
    }

    @Nested
    @DisplayName("归并排序测试")
    class MergeSortTests {

        @Test
        @DisplayName("归并排序应正确排序")
        void testMergeSort() {
            RecursiveScenario s = new RecursiveScenario(
                    RecursiveScenario.AlgorithmType.MERGE_SORT, 1000, 100);
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(4);
            executor = strategy.createExecutor();

            ScenarioResult result = s.execute(executor, 5);

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals(5, result.getCompletedTasks());

            strategy.shutdown();
        }

        @Test
        @DisplayName("归并排序应验证结果正确性")
        void testMergeSortVerification() {
            RecursiveScenario s = new RecursiveScenario(
                    RecursiveScenario.AlgorithmType.MERGE_SORT, 5000, 500);
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(4);
            executor = strategy.createExecutor();

            ScenarioResult result = s.execute(executor, 3);

            assertTrue(s.verify(result));

            strategy.shutdown();
        }
    }

    @Nested
    @DisplayName("斐波那契测试")
    class FibonacciTests {

        @Test
        @DisplayName("斐波那契应正确计算")
        void testFibonacci() {
            RecursiveScenario s = new RecursiveScenario(
                    RecursiveScenario.AlgorithmType.FIBONACCI, 3000, 20);
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(4);
            executor = strategy.createExecutor();

            ScenarioResult result = s.execute(executor, 3);

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals(3, result.getCompletedTasks());

            strategy.shutdown();
        }

        @Test
        @DisplayName("斐波那契应验证结果正确性")
        void testFibonacciVerification() {
            RecursiveScenario s = new RecursiveScenario(
                    RecursiveScenario.AlgorithmType.FIBONACCI, 2000, 15);
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(4);
            executor = strategy.createExecutor();

            ScenarioResult result = s.execute(executor, 3);

            assertTrue(s.verify(result));

            strategy.shutdown();
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

            ScenarioResult result = scenario.execute(executor, 5);

            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals(5, result.getCompletedTasks());
            assertTrue(result.getDurationNanos() > 0);

            strategy.shutdown();
        }

        @Test
        @DisplayName("执行结果应通过验证")
        void testVerifyResult() {
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(8);
            executor = strategy.createExecutor();

            ScenarioResult result = scenario.execute(executor, 5);

            assertTrue(scenario.verify(result));

            strategy.shutdown();
        }

        @Test
        @DisplayName("不同算法类型应正常执行")
        void testExecuteWithDifferentAlgorithms() {
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(4);
            executor = strategy.createExecutor();

            RecursiveScenario.AlgorithmType[] algorithms = {
                    RecursiveScenario.AlgorithmType.QUICK_SORT,
                    RecursiveScenario.AlgorithmType.MERGE_SORT,
                    RecursiveScenario.AlgorithmType.FIBONACCI
            };

            for (RecursiveScenario.AlgorithmType algo : algorithms) {
                RecursiveScenario s = new RecursiveScenario(algo, 1000, 100);
                ScenarioResult result = s.execute(executor, 3);
                assertNotNull(result);
                assertTrue(result.isSuccess(), "Failed for algorithm: " + algo);
                assertEquals(3, result.getCompletedTasks());
            }

            strategy.shutdown();
        }

        @Test
        @DisplayName("递归任务应在合理时间内完成")
        void testExecutionTime() {
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(8);
            executor = strategy.createExecutor();

            long startTime = System.currentTimeMillis();
            ScenarioResult result = scenario.execute(executor, 10);
            long duration = System.currentTimeMillis() - startTime;

            assertTrue(result.isSuccess());
            // 10 个递归任务应在 30 秒内完成
            assertTrue(duration < 30000, "Execution took too long: " + duration + "ms");

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

            ScenarioResult result = scenario.execute(executor, 5);

            assertTrue(scenario.verify(result));

            strategy.shutdown();
        }

        @Test
        @DisplayName("失败的结果应不通过验证")
        void testVerifyFailure() {
            ScenarioResult failedResult = new ScenarioResult(
                    "recursive",
                    10,
                    5,
                    1000000000L,
                    java.util.List.of(),
                    false,
                    "Some tasks failed"
            );

            assertFalse(scenario.verify(failedResult));
        }
    }
}
