package com.threadmg.benchmark.core;

import java.util.concurrent.ExecutorService;

/**
 * 线程策略接口
 * 定义不同线程技术的执行策略
 */
public interface ThreadStrategy {
    
    /**
     * 获取策略名称
     * @return 策略标识符（如 "platform", "virtual"）
     */
    String getName();
    
    /**
     * 获取策略描述
     * @return 人类可读的策略描述
     */
    String getDescription();
    
    /**
     * 创建执行器
     * @return 配置好的执行器服务
     */
    ExecutorService createExecutor();
    
    /**
     * 关闭执行器，释放资源
     */
    void shutdown();
    
    /**
     * 获取默认线程数/并发度
     * @return 默认并发配置
     */
    int getDefaultThreadCount();
}