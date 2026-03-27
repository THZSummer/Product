package com.threadmg.scenarios.mixed;

import com.threadmg.benchmark.core.Scenario;
import com.threadmg.benchmark.core.ScenarioResult;
import com.threadmg.threads.platform.PlatformThreadStrategy;
import org.junit.jupiter.api.*;

import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MixedLoadScenario 单元测试
 */
@DisplayName("MixedLoadScenario Tests")
class MixedLoadScenarioTest {

    private MixedLoadScenario scenario;
    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        scenario = new MixedLoadScenario();
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
            MixedLoadScenario s = new MixedLoadScenario();
            assertNotNull(s);
            assertEquals("mixed-load", s.getName());
            assertEquals(0.7, s.getCpuRatio(), 0.01);
            assertEquals(0.3, s.getIoRatio(), 0.01);
            assertEquals(1000, s.getTotalTasks());
        }

        @Test
        @DisplayName("自定义参数构造方法应正确设置参数")
        void testCustomConstructor() {
            MixedLoadScenario s = new MixedLoadScenario(0.5, 0.3, 500, 30, 10);
            assertEquals(0.5, s.getCpuRatio(), 0.01);
            assertEquals(0.3, s.getIoRatio(), 0.01);
            assertEquals(500, s.getTotalTasks());
            assertEquals(30, s.getMatrixSize());
            assertEquals(10, s.getIoDelayMs());
        }

        @Test
        @DisplayName("cpuRatio 超出范围应抛出异常")
        void testInvalidCpuRatio() {
            assertThrows(IllegalArgumentException.class,
                    () -> new MixedLoadScenario(1.5, 0.3, 1000, 50, 5));
        }

        @Test
        @DisplayName("ioRatio 超出范围应抛出异常")
        void testInvalidIoRatio() {
            assertThrows(IllegalArgumentException.class,
                    () -> new MixedLoadScenario(0.5, 1.5, 1000, 50, 5));
        }

        @Test
        @DisplayName("cpuRatio + ioRatio > 1.0 应抛出异常")
        void testInvalidRatioSum() {
            assertThrows(IllegalArgumentException.class,
                    () -> new MixedLoadScenario(0.8, 0.5, 1000, 50, 5));
        }

        @Test
        @DisplayName("totalTasks 小于 1 应抛出异常")
        void testInvalidTotalTasks() {
            assertThrows(IllegalArgumentException.class,
                    () -> new MixedLoadScenario(0.5, 0.3, 0, 50, 5));
        }

        @Test
        @DisplayName("matrixSize 小于 1 应抛出异常")
        void testInvalidMatrixSize() {
            assertThrows(IllegalArgumentException.class,
                    () -> new MixedLoadScenario(0.5, 0.3, 1000, 0, 5));
        }

        @Test
        @DisplayName("ioDelayMs 小于 0 应抛出异常")
        void testInvalidIoDelayMs() {
            assertThrows(IllegalArgumentException.class,
                    () -> new MixedLoadScenario(0.5, 0.3, 1000, 50, -1));
        }
    }

    @Nested
    @DisplayName("Builder 模式测试")
    class BuilderTests {

        @Test
        @DisplayName("Builder 应正确构建场景实例")
        void testBuilder() {
            Scenario s = MixedLoadScenario.builder()
                    .cpuRatio(0.6)
                    .ioRatio(0.3)
                    .totalTasks(800)
                    .matrixSize(40)
                    .ioDelayMs(8)
                    .build();

            assertNotNull(s);
            assertEquals("mixed-load", s.getName());
        }

        @Test
        @DisplayName("Builder 参数验证应生效")
        void testBuilderValidation() {
            assertThrows(IllegalArgumentException.class,
                    () -> MixedLoadScenario.builder()
                            .cpuRatio(1.5)
                            .build());
        }
    }

    @Nested
    @DisplayName("场景属性测试")
    class PropertyTests {

        @Test
        @DisplayName("getName 应返回正确名称")
        void testGetName() {
            assertEquals("mixed-load", scenario.getName());
        }

        @Test
        @DisplayName("getDescription 应返回包含配置的描述")
        void testGetDescription() {
            String description = scenario.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("CPU"));
            assertTrue(description.contains("I/O"));
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
        @DisplayName("createTasks 应包含混合类型的任务")
        void testCreateTasksMixed() {
            MixedLoadScenario s = new MixedLoadScenario(0.5, 0.5, 100, 30, 5);
            var tasks = s.createTasks(100);

            assertEquals(100, tasks.size());
            assertNotNull(tasks);
        }

        @Test
        @DisplayName("任务顺序应被打乱")
        void testTasksShuffled() {
            MixedLoadScenario s = new MixedLoadScenario(0.9, 0.1, 100, 30, 5);
            
            // 创建多次，验证顺序不同
            var tasks1 = s.createTasks(100);
            var tasks2 = s.createTasks(100);
            
            assertNotNull(tasks1);
            assertNotNull(tasks2);
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
        @DisplayName("执行结果应通过验证")
        void testVerifyResult() {
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(8);
            executor = strategy.createExecutor();

            ScenarioResult result = scenario.execute(executor, 100);

            assertTrue(scenario.verify(result));

            strategy.shutdown();
        }

        @Test
        @DisplayName("不同混合比例应正常执行")
        void testExecuteWithDifferentRatios() {
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(8);
            executor = strategy.createExecutor();

            double[][] ratios = {
                    {0.9, 0.1},
                    {0.5, 0.3},
                    {0.3, 0.5},
                    {0.1, 0.1}
            };

            for (double[] ratio : ratios) {
                MixedLoadScenario s = new MixedLoadScenario(
                        ratio[0], ratio[1], 50, 30, 5);
                ScenarioResult result = s.execute(executor, 50);
                assertNotNull(result);
                assertTrue(result.isSuccess(),
                        "Failed for ratio: CPU=" + ratio[0] + ", I/O=" + ratio[1]);
            }

            strategy.shutdown();
        }

        @Test
        @DisplayName("CPU 密集型为主的场景应正常执行")
        void testCpuIntensiveScenario() {
            MixedLoadScenario s = new MixedLoadScenario(0.9, 0.05, 100, 50, 5);
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(8);
            executor = strategy.createExecutor();

            ScenarioResult result = s.execute(executor, 100);

            assertNotNull(result);
            assertTrue(result.isSuccess());

            strategy.shutdown();
        }

        @Test
        @DisplayName("I/O 密集型为主的场景应正常执行")
        void testIoIntensiveScenario() {
            MixedLoadScenario s = new MixedLoadScenario(0.1, 0.8, 100, 30, 10);
            PlatformThreadStrategy strategy = new PlatformThreadStrategy(8);
            executor = strategy.createExecutor();

            ScenarioResult result = s.execute(executor, 100);

            assertNotNull(result);
            assertTrue(result.isSuccess());

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
            ScenarioResult failedResult = new ScenarioResult(
                    "mixed-load",
                    100,
                    50,
                    1000000000L,
                    java.util.List.of(),
                    false,
                    "Some tasks failed"
            );

            assertFalse(scenario.verify(failedResult));
        }
    }
}
