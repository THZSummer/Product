package com.threadmg.benchmark.core;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 测试场景接口
 * 定义基准测试的执行场景
 */
public interface Scenario {
    
    /**
     * 获取场景名称
     * @return 场景标识符（如 "cpu-bound", "io-bound"）
     */
    String getName();
    
    /**
     * 获取场景描述
     * @return 人类可读的场景描述
     */
    String getDescription();
    
    /**
     * 创建测试任务列表
     * @param count 任务数量
     * @return 可执行的任务列表
     */
    List<Runnable> createTasks(int count);
    
    /**
     * 执行场景测试
     * @param executor 执行器
     * @param taskCount 任务数量
     * @return 场景执行结果
     */
    ScenarioResult execute(ExecutorService executor, int taskCount);
    
    /**
     * 验证执行结果正确性
     * @param result 执行结果
     * @return 是否通过验证
     */
    boolean verify(ScenarioResult result);
}