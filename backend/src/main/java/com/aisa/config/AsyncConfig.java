//$Id$
package com.aisa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "pdfTaskExecutor")
    public Executor pdfTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);  // Threads to keep alive
        executor.setMaxPoolSize(50);   // Max threads on burst
        executor.setQueueCapacity(100); // Max queue size before rejecting
        executor.setThreadNamePrefix("PDF-Async-");
        executor.initialize();
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return pdfTaskExecutor();
    }
}
