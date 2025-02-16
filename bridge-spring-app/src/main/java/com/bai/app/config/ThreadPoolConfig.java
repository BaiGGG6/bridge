package com.bai.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolConfig {

    private static Integer coreSize = Runtime.getRuntime().availableProcessors();

    public static Executor BridgeThreadPool;

    @Bean(name = "BridgeThreadPool")
    public Executor bridgeThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数，线程池创建时初始化的线程数
        executor.setCorePoolSize(5);
        // IO密集最大线程数，线程池允许创建的最大线程数
        executor.setMaxPoolSize(2 * coreSize + 1);
        // 队列容量，当核心线程都在工作时，新任务会被放入队列等待执行
        executor.setQueueCapacity(20);
        // 线程空闲时间，当线程空闲时间达到该值时，会被销毁
        executor.setKeepAliveSeconds(60);
        // 线程名称前缀，方便在日志中区分不同线程池的线程
        executor.setThreadNamePrefix("bridge-plugin-thread-");
        // 线程池关闭时等待所有任务完成
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 初始化线程池
        executor.initialize();
        // 赋值给静态变量
        BridgeThreadPool = executor;
        return executor;
    }

}