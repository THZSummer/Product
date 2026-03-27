package com.threadmg.reporters;

import com.threadmg.benchmark.core.BenchmarkResult;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 控制台报告器
 * 将测试结果输出到控制台
 */
public class ConsoleReporter {
    
    public void report(BenchmarkResult result) {
        System.out.println("========================================");
        System.out.println("Benchmark Result: " + result.getScenarioName());
        System.out.println("Strategy: " + result.getStrategyName());
        System.out.println("----------------------------------------");
        System.out.printf("Throughput: %.2f tasks/sec%n", result.getAverageThroughput());
        System.out.printf("P50 Latency: %.2f ms%n", result.getP50Latency());
        System.out.printf("P95 Latency: %.2f ms%n", result.getP95Latency());
        System.out.printf("P99 Latency: %.2f ms%n", result.getP99Latency());
        System.out.printf("Std Dev Throughput: %.2f%n", result.getStandardDeviation());
        System.out.println("========================================");
    }
}